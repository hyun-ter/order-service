package com.example.orderservice.inventory.domain.model;

import com.example.orderservice.product.domain.model.ProductId;

/**
 * 재고 Aggregate Root.
 *
 * <p>상품 1개당 재고 1개가 대응된다 (productId 기준).
 * 동시 요청에 의한 재고 차감 경쟁 조건은 Infrastructure 레이어의
 * {@code @Version} 낙관적 락으로 방어한다.
 *
 * <p>재고 차감은 {@code InventoryEventHandler}가 {@code OrderCreatedEvent} 수신 시
 * 트랜잭션 BEFORE_COMMIT 단계에서 호출한다.
 */
public class Inventory {

    private InventoryId inventoryId;
    private final ProductId productId;
    private StockQuantity quantity;
    private Long version; // 낙관적 락용 버전 (JPA @Version에 매핑됨)

    private Inventory(InventoryId inventoryId, ProductId productId, StockQuantity quantity, Long version) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.quantity = quantity;
        this.version = version;
    }

    /**
     * 새 재고를 등록할 때 사용하는 팩토리 메서드.
     * inventoryId, version은 null로 시작하며 JPA 저장 후 값이 부여된다.
     */
    public static Inventory create(ProductId productId, StockQuantity quantity) {
        return new Inventory(null, productId, quantity, null);
    }

    /**
     * DB에서 조회한 데이터로 재고 객체를 재구성하는 팩토리 메서드.
     * Infrastructure 레이어(InventoryRepositoryImpl)에서만 호출한다.
     */
    public static Inventory restore(InventoryId inventoryId, ProductId productId, StockQuantity quantity, Long version) {
        return new Inventory(inventoryId, productId, quantity, version);
    }

    /**
     * 재고를 차감한다.
     * 차감 후 수량이 0 미만이 되면 {@code OutOfStockException}이 발생한다.
     */
    public void decrease(int amount) {
        this.quantity = this.quantity.decrease(amount);
    }

    /** 재고를 증가시킨다 (입고 처리). */
    public void increase(int amount) {
        this.quantity = this.quantity.increase(amount);
    }

    public InventoryId getInventoryId() {
        return inventoryId;
    }

    public ProductId getProductId() {
        return productId;
    }

    public StockQuantity getQuantity() {
        return quantity;
    }

    public Long getVersion() {
        return version;
    }
}
