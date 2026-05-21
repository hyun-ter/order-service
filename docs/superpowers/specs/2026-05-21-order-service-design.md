# Order Service Design Spec

**Date:** 2026-05-21 (Updated: 2026-05-22)
**Purpose:** 포트폴리오 용도 — 쿠팡 주문 서비스 벤치마킹
**Stack:** Spring Boot 4.0, JPA, DDD, MySQL (Docker)

---

## 1. 범위

- 회원 가입/조회
- 판매자 등록/조회, 판매자가 상품 등록
- 주문 생성 → 결제 → 배송 상태 관리 (회원이 주문 생성)
- 상품 조회
- 재고 연동 (주문 생성 시 재고 차감)

---

## 2. 아키텍처

**Layered Architecture + DDD** (모놀리식 단일 앱)

도메인(`member`, `seller`, `order`, `product`, `inventory`)별로 패키지를 나누고, 각 도메인 내부에서 레이어를 분리한다.

**핵심 규칙:**
- 도메인 레이어는 Spring/JPA 등 외부 의존성 없음 (순수 Java)
- 도메인 간 직접 객체 참조 금지 — 이벤트 또는 ID 참조만 허용
- `infrastructure` 레이어가 `domain`의 Repository 인터페이스를 구현

---

## 3. 패키지 구조

```
com.example.orderservice
├── member/
│   ├── presentation/        # REST Controller, Request/Response DTO
│   ├── application/         # MemberApplicationService
│   ├── domain/
│   │   ├── model/           # Member, MemberId, Email, Address
│   │   └── repository/      # MemberRepository (interface)
│   └── infrastructure/
│       └── persistence/     # MemberJpaRepository, MemberJpaEntity
│
├── seller/
│   ├── presentation/        # REST Controller, Request/Response DTO
│   ├── application/         # SellerApplicationService
│   ├── domain/
│   │   ├── model/           # Seller, SellerId, BusinessName, Contact
│   │   └── repository/      # SellerRepository (interface)
│   └── infrastructure/
│       └── persistence/     # SellerJpaRepository, SellerJpaEntity
│
├── order/
│   ├── presentation/        # REST Controller, Request/Response DTO
│   ├── application/         # OrderApplicationService, OrderEventHandler
│   ├── domain/
│   │   ├── model/           # Order, OrderItem, OrderId, OrderStatus, Money
│   │   ├── event/           # OrderCreatedEvent
│   │   ├── repository/      # OrderRepository (interface)
│   │   └── service/         # OrderDomainService
│   └── infrastructure/
│       └── persistence/     # OrderJpaRepository, OrderJpaEntity
│
├── product/
│   ├── presentation/        # REST Controller, Request/Response DTO
│   ├── application/         # ProductApplicationService
│   ├── domain/
│   │   ├── model/           # Product, ProductId, Money, Category, ProductStatus
│   │   └── repository/      # ProductRepository (interface)
│   └── infrastructure/
│       └── persistence/     # ProductJpaRepository, ProductJpaEntity
│
└── inventory/
    ├── application/         # InventoryApplicationService, InventoryEventHandler
    ├── domain/
    │   ├── model/           # Inventory, InventoryId, StockQuantity
    │   ├── event/           # OutOfStockEvent
    │   └── repository/      # InventoryRepository (interface)
    └── infrastructure/
        └── persistence/     # InventoryJpaRepository, InventoryJpaEntity
```

---

## 4. 도메인 모델

### Member (Aggregate Root)

| 필드 | 타입 | 설명 |
|---|---|---|
| memberId | MemberId (VO) | 회원 식별자 |
| email | Email (VO) | 이메일 (고유, 형식 검증) |
| name | String | 이름 |
| address | Address (VO) | 기본 배송지 (도로명, 상세주소, 우편번호) |
| createdAt | LocalDateTime | 가입 시각 |

- `Member.register()` 팩토리 메서드로 생성
- 이메일 중복 검증은 `MemberApplicationService`에서 처리

### Seller (Aggregate Root)

| 필드 | 타입 | 설명 |
|---|---|---|
| sellerId | SellerId (VO) | 판매자 식별자 |
| businessName | BusinessName (VO) | 상호명 |
| contact | Contact (VO) | 연락처 (전화번호, 이메일) |
| status | SellerStatus | ACTIVE / SUSPENDED |
| createdAt | LocalDateTime | 등록 시각 |

- 상품 등록 시 `sellerId`를 Product에 ID 참조로 저장

### Order (Aggregate Root)

| 필드 | 타입 | 설명 |
|---|---|---|
| orderId | OrderId (VO) | 주문 식별자 |
| memberId | MemberId (VO) | 회원 ID 참조 |
| orderItems | List\<OrderItem\> | 주문 항목 |
| totalAmount | Money (VO) | 총 금액 |
| status | OrderStatus | PENDING → PAID → SHIPPING → DELIVERED / CANCELLED |
| createdAt | LocalDateTime | 생성 시각 |
| updatedAt | LocalDateTime | 수정 시각 |

- `Order.create()` 팩토리 메서드로 생성 → `OrderCreatedEvent` 발행
- `Order.cancel()` 호출 시 상태 검증 (PAID 이후 취소 불가)

### Product (Aggregate Root)

| 필드 | 타입 | 설명 |
|---|---|---|
| productId | ProductId (VO) | 상품 식별자 |
| sellerId | SellerId (VO) | 판매자 ID 참조 |
| name | String | 상품명 |
| price | Money (VO) | 가격 (음수 불가) |
| category | Category (VO) | 카테고리 |
| status | ProductStatus | ON_SALE / SOLD_OUT / DISCONTINUED |

### Inventory (Aggregate Root)

| 필드 | 타입 | 설명 |
|---|---|---|
| inventoryId | InventoryId (VO) | 재고 식별자 |
| productId | ProductId (VO) | 상품 ID 참조 |
| quantity | StockQuantity (VO) | 재고 수량 (음수 불가 검증) |
| version | Long | 낙관적 락 (`@Version`) |

- `Inventory.decrease(quantity)` → 재고 부족 시 `OutOfStockException`

---

## 5. 도메인 이벤트 플로우

```
주문 생성 요청 (POST /api/orders)
  → OrderApplicationService
    → MemberRepository로 회원 존재 확인
    → ProductRepository로 상품 존재 및 ON_SALE 상태 확인
    → Order.create() → OrderCreatedEvent 발행 (ApplicationEventPublisher)
  → InventoryEventHandler (OrderCreatedEvent 구독, @TransactionalEventListener)
    → Inventory.decrease() 호출
    → 재고 부족 시 OutOfStockException → 트랜잭션 롤백
```

이벤트는 Spring의 `ApplicationEventPublisher`로 동기 처리 (같은 트랜잭션 내).
`@TransactionalEventListener(phase = BEFORE_COMMIT)` 사용으로 롤백 보장.

---

## 6. API 설계

### Member API

| Method | Path | 설명 |
|---|---|---|
| POST | /api/members | 회원 가입 |
| GET | /api/members/{memberId} | 회원 단건 조회 |

### Seller API

| Method | Path | 설명 |
|---|---|---|
| POST | /api/sellers | 판매자 등록 |
| GET | /api/sellers/{sellerId} | 판매자 단건 조회 |

### Order API

| Method | Path | 설명 |
|---|---|---|
| POST | /api/orders | 주문 생성 (회원만 가능) |
| GET | /api/orders/{orderId} | 주문 단건 조회 |
| GET | /api/orders?memberId={id} | 회원별 주문 목록 |
| PATCH | /api/orders/{orderId}/cancel | 주문 취소 |

### Product API

| Method | Path | 설명 |
|---|---|---|
| POST | /api/products | 상품 등록 (판매자만 가능) |
| GET | /api/products/{productId} | 상품 단건 조회 |
| GET | /api/products?category={cat} | 카테고리별 상품 목록 |
| GET | /api/products?sellerId={id} | 판매자별 상품 목록 |

### Inventory API

| Method | Path | 설명 |
|---|---|---|
| POST | /api/inventories | 재고 등록 |
| GET | /api/inventories/{productId} | 상품별 재고 조회 |
| PATCH | /api/inventories/{productId}/increase | 재고 증가 (입고) |

---

## 7. 기술 스택

| 항목 | 선택 |
|---|---|
| Framework | Spring Boot 4.0 |
| Language | Java 17 |
| ORM | Spring Data JPA |
| DB | MySQL 8.0 (Docker) |
| Validation | spring-boot-starter-validation |
| Test | JUnit 5, MockMvc, @DataJpaTest, Testcontainers |

### 추가 의존성 (build.gradle)

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-validation'
runtimeOnly 'com.mysql:mysql-connector-j'
testImplementation 'org.testcontainers:mysql'
testImplementation 'org.testcontainers:junit-jupiter'
```

### docker-compose.yml

```yaml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: order_service
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
```

---

## 8. 테스트 전략

| 레이어 | 도구 | 설명 |
|---|---|---|
| Domain | JUnit 5 (순수 단위 테스트) | Aggregate, Value Object, Domain Service |
| API (Controller) | `@WebMvcTest` + MockMvc | 요청/응답 검증 |
| Repository | `@DataJpaTest` + H2 | 쿼리 및 매핑 검증 |

---

## 9. 에러 처리

- `GlobalExceptionHandler` (`@RestControllerAdvice`)로 일관된 에러 응답 포맷 제공
- 주요 예외: `MemberNotFoundException`, `SellerNotFoundException`, `OrderNotFoundException`, `ProductNotFoundException`, `OutOfStockException`, `InvalidOrderStatusException`, `DuplicateEmailException`
- HTTP 상태코드: 404 (Not Found), 400 (Bad Request), 409 (Conflict — 재고 부족 / 이메일 중복)
