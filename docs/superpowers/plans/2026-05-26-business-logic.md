# Order Service 비즈니스 로직 정리

**날짜:** 2026-05-26  
**브랜치:** `feat/implement-order-service`

---

## 1. Member (회원)

### 1.1 회원 가입 (`POST /api/members`)

**흐름:**
1. 요청의 `email`로 `Email` VO 생성 → 형식 검증 (정규식: `^[^@\s]+@[^@\s]+\.[^@\s]+$`)
2. 이미 동일한 이메일이 존재하면 `DuplicateEmailException` (409 Conflict)
3. `street`, `detail`, `zipCode`로 `Address` VO 생성 → street/zipCode null·blank 검증
4. `Member.register(email, name, address)` 팩토리로 도메인 객체 생성 (memberId=null)
5. 저장 후 응답 반환

**도메인 규칙:**
- 이메일은 시스템 전체에서 고유해야 한다
- 이메일 형식이 유효하지 않으면 VO 생성 단계에서 즉시 실패한다
- `street`(도로명)와 `zipCode`(우편번호)는 필수이며, `detail`(상세주소)은 선택이다

**발생 가능 예외:**

| 예외 | 조건 | HTTP |
|------|------|------|
| `DuplicateEmailException` | 동일 이메일 이미 존재 | 409 |
| `IllegalArgumentException` | 이메일 형식 오류, street/zipCode blank | 400 |
| `MethodArgumentNotValidException` | @Valid 검증 실패 | 400 |

---

### 1.2 회원 단건 조회 (`GET /api/members/{memberId}`)

**흐름:**
1. `memberId`로 `MemberId` VO 생성
2. `MemberRepository.findById()` 조회
3. 없으면 `MemberNotFoundException` (404)

---

## 2. Seller (판매자)

### 2.1 판매자 등록 (`POST /api/sellers`)

**흐름:**
1. `businessName`으로 `BusinessName` VO 생성 → null·blank 검증
2. `phone`, `email`로 `Contact` VO 생성 → 둘 다 필수
3. `Seller.register(businessName, contact)` 팩토리로 생성 (status=ACTIVE, sellerId=null)
4. 저장 후 응답 반환

**도메인 규칙:**
- 신규 판매자는 항상 `ACTIVE` 상태로 등록된다
- 상호명과 연락처는 필수다

---

### 2.2 판매자 단건 조회 (`GET /api/sellers/{sellerId}`)

**흐름:**
1. `SellerId` VO 생성
2. `SellerRepository.findById()` 조회
3. 없으면 `SellerNotFoundException` (404)

---

## 3. Product (상품)

### 3.1 상품 등록 (`POST /api/products`)

**흐름:**
1. `sellerId`로 `SellerId` VO 생성
2. `price`로 `Money` VO 생성 → null·음수 검증 (BigDecimal 기반)
3. `category`로 `Category` VO 생성 → null·blank 검증
4. `Product.register(sellerId, name, price, category)` 팩토리 생성 (status=ON_SALE, productId=null)
5. 저장 후 응답 반환

**도메인 규칙:**
- 신규 상품은 항상 `ON_SALE` 상태로 등록된다
- 가격은 0 이상이어야 한다 (0원 상품 허용)
- 판매자 존재 여부는 이 시점에 검증하지 않는다 (ID 참조 방식)

---

### 3.2 상품 단건 조회 (`GET /api/products/{productId}`)

**흐름:**
1. `ProductId` VO 생성
2. `ProductRepository.findById()` 조회
3. 없으면 `ProductNotFoundException` (404)

---

### 3.3 판매자별 상품 목록 조회 (`GET /api/products?sellerId={id}`)

**흐름:**
1. `SellerId` VO 생성
2. `ProductRepository.findBySellerId()` 조회
3. 결과가 없어도 빈 목록 반환 (예외 없음)

---

### 3.4 카테고리별 상품 목록 조회 (`GET /api/products?category={cat}`)

**흐름:**
1. `Category` VO 생성
2. `ProductRepository.findByCategory()` 조회
3. 결과가 없어도 빈 목록 반환 (예외 없음)

**참고:** `sellerId`와 `category` 둘 다 없으면 400 Bad Request 반환.  
둘 다 있으면 `category`가 우선 처리된다.

---

## 4. Inventory (재고)

### 4.1 재고 등록 (`POST /api/inventories`)

**흐름:**
1. `productId`로 `ProductId` VO 생성
2. `quantity`로 `StockQuantity` VO 생성 → 음수 불가 (0 허용)
3. `Inventory.create(productId, quantity)` 팩토리 생성 (inventoryId=null, version=null)
4. 저장 후 응답 반환

**도메인 규칙:**
- 재고 수량은 0 이상이어야 한다
- 상품 1개당 재고 1개 원칙 (productId 기준)
- `version` 필드가 JPA `@Version`에 매핑되어 동시 수정 시 낙관적 락이 적용된다

---

### 4.2 상품별 재고 조회 (`GET /api/inventories/{productId}`)

**흐름:**
1. `ProductId` VO 생성
2. `InventoryRepository.findByProductId()` 조회
3. 없으면 `InventoryNotFoundException` (404)

---

### 4.3 재고 증가 — 입고 (`PATCH /api/inventories/{productId}/increase`)

**흐름:**
1. `ProductId` VO 생성
2. `InventoryRepository.findByProductId()` 조회 → 없으면 `InventoryNotFoundException` (404)
3. `Inventory.increase(amount)` 호출 → `StockQuantity.increase()` → 새 수량 반환
4. 저장 후 응답 반환

---

### 4.4 재고 차감 — 이벤트 기반 (`InventoryEventHandler`)

**트리거:** `OrderCreatedEvent` 발행 시 자동 실행

**흐름:**
1. 주문 항목마다 순서대로 처리:
   - `productId`로 재고 조회 → 없으면 `InventoryNotFoundException`
   - `Inventory.decrease(quantity)` 호출 → `StockQuantity.decrease()` → 재고 부족이면 `OutOfStockException`
   - 변경된 재고 저장
2. 예외 발생 시 주문 생성 트랜잭션 전체 롤백

**트랜잭션 설계:**
```
주문 생성 트랜잭션 시작
  → Order 저장
  → ApplicationEventPublisher.publishEvent(OrderCreatedEvent)
     → [BEFORE_COMMIT] InventoryEventHandler.handleOrderCreated()
        → Inventory.decrease() (같은 트랜잭션에 MANDATORY 참여)
        → 재고 부족 → OutOfStockException
  → 예외 → 트랜잭션 전체 롤백 (Order 저장 취소)
```

**도메인 규칙:**
- 주문 생성과 재고 차감은 원자적으로 처리된다
- 멀티 아이템 주문 시 하나라도 재고 부족이면 전체 주문이 취소된다
- `@Version` 낙관적 락으로 동시 주문에 의한 재고 초과 차감을 방지한다

---

## 5. Order (주문)

### 5.1 주문 생성 (`POST /api/orders`)

**흐름:**
1. `memberId`로 회원 존재 확인 → 없으면 `MemberNotFoundException` (404)
2. 각 주문 항목의 `productId`로 상품 조회 → 없으면 `ProductNotFoundException` (404)
3. `OrderDomainService.validateOrderable()` — 모든 상품이 `ON_SALE`인지 검증
   - `SOLD_OUT` 또는 `DISCONTINUED` 상품이 있으면 `IllegalStateException` (500)
4. `OrderItem.of(productId, productName, price, quantity)` 생성 — **현재 시점 상품 정보 스냅샷**
5. `Order.create(memberId, orderItems)` — totalAmount 자동 계산 (각 항목 소계 합산), status=PENDING
6. `OrderRepository.save()` 저장
7. `ApplicationEventPublisher.publishEvent(OrderCreatedEvent)` 발행
   - `InventoryEventHandler`가 수신 → 재고 차감 (재고 부족 시 롤백)
8. 응답 반환

**도메인 규칙:**
- 주문 시점의 상품명과 단가를 `OrderItem`에 스냅샷으로 저장한다  
  (이후 상품 정보가 변경되어도 주문 내역은 원래 값을 유지)
- 총 금액은 `OrderItem`들의 소계 합산으로 자동 계산된다  
  (`unitPrice × quantity`의 합)
- 신규 주문은 항상 `PENDING` 상태로 시작한다
- 주문 생성과 재고 차감은 하나의 트랜잭션으로 처리된다

**발생 가능 예외:**

| 예외 | 조건 | HTTP |
|------|------|------|
| `MemberNotFoundException` | 회원 미존재 | 404 |
| `ProductNotFoundException` | 상품 미존재 | 404 |
| `IllegalStateException` | ON_SALE이 아닌 상품 포함 | 500 |
| `InventoryNotFoundException` | 재고 정보 미존재 | 404 |
| `OutOfStockException` | 재고 부족 | 409 |

---

### 5.2 주문 단건 조회 (`GET /api/orders/{orderId}`)

**흐름:**
1. `OrderId` VO 생성
2. `OrderRepository.findById()` 조회
3. 없으면 `OrderNotFoundException` (404)

---

### 5.3 회원별 주문 목록 조회 (`GET /api/orders?memberId={id}`)

**흐름:**
1. `MemberId` VO 생성
2. `OrderRepository.findByMemberId()` 조회
3. 결과가 없어도 빈 목록 반환 (예외 없음)

---

### 5.4 주문 취소 (`PATCH /api/orders/{orderId}/cancel`)

**흐름:**
1. `OrderId` VO 생성
2. `OrderRepository.findById()` 조회 → 없으면 `OrderNotFoundException` (404)
3. `Order.cancel()` 호출:
   - `status == PENDING`이면 → `CANCELLED`로 변경, `updatedAt` 갱신
   - `status != PENDING`이면 → `InvalidOrderStatusException` (400)
4. 저장 후 응답 반환

**도메인 규칙:**
- `PENDING` 상태에서만 취소 가능하다
- `PAID` 이후(결제 완료, 배송 중, 배송 완료) 상태에서는 취소 불가
- 취소 시 재고 복구는 현재 구현되지 않는다 (향후 개선 대상)

**주문 상태 전이:**
```
PENDING → PAID → SHIPPING → DELIVERED
   ↓
CANCELLED (PENDING 상태에서만 가능)
```

---

## 6. 공통 Value Object 도메인 규칙

### Money
- `BigDecimal` 기반 (float/double 금융 정밀도 오류 방지)
- 0 이상의 값만 허용 (음수 불가)
- scale에 관계없이 동등 비교 (1.0 == 1.00)
- `add()`, `multiply()` 연산은 항상 새 인스턴스 반환 (불변)

### Email
- 정규식 검증: `^[^@\s]+@[^@\s]+\.[^@\s]+$`
- 생성 시점에 검증하므로 Email 인스턴스는 항상 유효한 이메일

### Address
- `street`(도로명), `zipCode`(우편번호)는 필수
- `detail`(상세주소)은 선택 (null 허용)

### StockQuantity
- 0 이상의 값만 허용
- `decrease()` 후 음수가 되면 즉시 `OutOfStockException`
- 불변 — `decrease()` / `increase()`는 새 인스턴스 반환
