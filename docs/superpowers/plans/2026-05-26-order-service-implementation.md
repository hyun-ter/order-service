# Order Service 구현 계획

**날짜:** 2026-05-26  
**브랜치:** `feat/implement-order-service`  
**상태:** 완료 ✅

---

## 구현 개요

포트폴리오용 쿠팡 주문 서비스 벤치마킹.  
Spring Boot 4.0 + JPA + DDD 기반 모놀리식 단일 앱.

---

## Task 목록

### Task 1: 인프라 설정 ✅
**커밋:** `1c1f551`, `6bff7b0`

- `build.gradle`에 JPA, Validation, MySQL, Testcontainers 의존성 추가
- `docker-compose.yml` 생성 (MySQL 8.0)
- `application.properties` MySQL 연결 설정
- Hibernate 6.x 호환성 수정 (dialect 제거, 로깅 경로 변경)

### Task 2: Member 도메인 ✅
**커밋:** `5ab69f6`, `6ff94f9`

**도메인 모델 (순수 Java)**
- `MemberId` — Long id, equals/hashCode
- `Email` — 정규식 이메일 형식 검증
- `Address` — street/detail/zipCode, null/blank 검증
- `Member` — Aggregate Root, `register()` / `restore()` 팩토리

**레이어 구성**
- `MemberRepository` 인터페이스 (domain)
- `MemberApplicationService` — 이메일 중복 검증 후 저장
- `MemberJpaEntity` / `MemberRepositoryImpl` (infrastructure)
- `MemberController` — `POST /api/members`, `GET /api/members/{id}`

**예외:** `MemberNotFoundException`, `DuplicateEmailException`

### Task 3: Seller 도메인 ✅
**커밋:** `4de8cf6`

**도메인 모델**
- `SellerId`, `BusinessName`, `Contact` VO
- `SellerStatus` enum — ACTIVE / SUSPENDED
- `Seller` — Aggregate Root

**레이어 구성**
- `SellerApplicationService`, `SellerController`
- `POST /api/sellers`, `GET /api/sellers/{id}`

**예외:** `SellerNotFoundException`

### Task 4: Product 도메인 ✅
**커밋:** `9c12f03`, `b4f5447`

**공유 Value Object**
- `Money` (`common/vo`) — BigDecimal 기반, scale-independent equals, add/multiply

**도메인 모델**
- `ProductId`, `Category` VO
- `ProductStatus` enum — ON_SALE / SOLD_OUT / DISCONTINUED
- `Product` — Aggregate Root, SellerId ID 참조

**레이어 구성**
- `ProductApplicationService`, `ProductController`
- `POST /api/products`, `GET /api/products/{id}`
- `GET /api/products?category=`, `GET /api/products?sellerId=`

**코드 품질 개선:**
- `ProductJpaEntity.status` — String → `@Enumerated(EnumType.STRING)`
- `Money.equals()` — `compareTo` 기반으로 변경

**예외:** `ProductNotFoundException`

### Task 5: Inventory 도메인 ✅
**커밋:** `5c0d624`

**도메인 모델**
- `InventoryId`, `StockQuantity` VO — decrease 시 `OutOfStockException`
- `Inventory` — Aggregate Root, `@Version` 낙관적 락
- `OutOfStockEvent` (정의)

**이벤트 핸들러**
- `InventoryEventHandler` — `@TransactionalEventListener(BEFORE_COMMIT)` + `@Transactional(MANDATORY)`
- `OrderCreatedEvent` 수신 → 재고 차감

**레이어 구성**
- `InventoryApplicationService`, `InventoryController`
- `POST /api/inventories`, `GET /api/inventories/{productId}`
- `PATCH /api/inventories/{productId}/increase`

**예외:** `OutOfStockException`, `InventoryNotFoundException`

### Task 6: Order 도메인 ✅
**커밋:** `c8bf0b2`

**도메인 모델**
- `OrderId` VO
- `OrderStatus` enum — PENDING / PAID / SHIPPING / DELIVERED / CANCELLED
- `OrderItem` — Order Aggregate 내부 엔티티, 주문 시점 상품 스냅샷
- `Order` — Aggregate Root, `create()` / `cancel()` / `restore()`
- `OrderCreatedEvent` — 주문 생성 후 Application Service에서 발행
- `OrderDomainService` — 여러 Product의 ON_SALE 상태 검증

**도메인 이벤트 플로우**
```
POST /api/orders
  → OrderApplicationService
    → 회원 존재 확인
    → 상품 조회 + ON_SALE 검증 (OrderDomainService)
    → OrderItem 생성 (상품명·단가 스냅샷)
    → Order.create() → 저장
    → ApplicationEventPublisher.publishEvent(OrderCreatedEvent)
  → InventoryEventHandler (BEFORE_COMMIT, MANDATORY)
    → Inventory.decrease() per item
    → 재고 부족 시 OutOfStockException → 트랜잭션 전체 롤백
```

**레이어 구성**
- `OrderApplicationService`, `OrderController`
- `POST /api/orders` (201), `GET /api/orders/{id}` (200)
- `GET /api/orders?memberId=` (200), `PATCH /api/orders/{id}/cancel` (200)

**예외:** `OrderNotFoundException`, `InvalidOrderStatusException`

### Task 7: 전역 예외 처리 ✅
**커밋:** `5dd399a`

`GlobalExceptionHandler` (`@RestControllerAdvice`):

| 예외 | HTTP 상태 |
|------|-----------|
| `*NotFoundException` (5종) | 404 NOT_FOUND |
| `DuplicateEmailException`, `OutOfStockException` | 409 CONFLICT |
| `InvalidOrderStatusException` | 400 BAD_REQUEST |
| `MethodArgumentNotValidException` | 400 VALIDATION_ERROR |
| `IllegalArgumentException` | 400 INVALID_INPUT |
| `Exception` (그 외) | 500 INTERNAL_ERROR |

### Task 8: 테스트 ✅
**커밋:** `76fe658`

| 유형 | 파일 | 개수 |
|------|------|------|
| 도메인 단위 | `MoneyTest`, `MemberTest`, `OrderTest`, `InventoryTest` | 15개 |
| Controller | `MemberControllerTest` (@WebMvcTest) | 4개 |
| Repository | `MemberJpaRepositoryTest` (@DataJpaTest + H2) | 2개 |
| 기타 | `OrderServiceApplicationTests` | 1개 |
| **합계** | | **22개** |

---

## 아키텍처 원칙

1. **도메인 레이어 순수성** — Spring/JPA 어노테이션 없는 순수 Java
2. **의존성 역전** — 도메인이 `Repository` 인터페이스 선언, Infrastructure가 구현
3. **도메인 간 경계** — 직접 객체 참조 금지, ID 참조 또는 이벤트만 허용
4. **이벤트 기반 재고 차감** — `BEFORE_COMMIT` + `MANDATORY`로 원자성 보장

---

## 기술 스택

| 항목 | 선택 |
|------|------|
| Framework | Spring Boot 4.0 |
| Language | Java 17 |
| ORM | Spring Data JPA (Hibernate 6.x) |
| DB | MySQL 8.0 (Docker) |
| Test DB | H2 (인메모리) |
| 빌드 | Gradle |

---

## 실행 방법

```bash
# MySQL 실행
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun

# 테스트 실행
./gradlew test
```

---

## 잠재적 개선점

- `InventoryEventHandler` 통합 테스트 추가 (재고 부족 시 주문 롤백 시나리오)
- `Order.updateStatus()` 상태 전이 규칙 강화
- 도메인 이벤트를 Aggregate Root에서 직접 누적 (`AbstractAggregateRoot` 패턴)
