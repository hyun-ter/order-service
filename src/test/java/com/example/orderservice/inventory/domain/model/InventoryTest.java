package com.example.orderservice.inventory.domain.model;

import com.example.orderservice.common.exception.OutOfStockException;
import com.example.orderservice.product.domain.model.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryTest {

    @Test
    @DisplayName("Inventory.decrease(amount): 정상 차감")
    void decrease_정상차감() {
        Inventory inventory = Inventory.create(new ProductId(1L), new StockQuantity(10));

        inventory.decrease(3);

        assertThat(inventory.getQuantity().getValue()).isEqualTo(7);
    }

    @Test
    @DisplayName("Inventory.decrease(amount): 재고 부족 시 OutOfStockException")
    void decrease_재고부족시_예외() {
        Inventory inventory = Inventory.create(new ProductId(1L), new StockQuantity(5));

        assertThatThrownBy(() -> inventory.decrease(10))
                .isInstanceOf(OutOfStockException.class);
    }

    @Test
    @DisplayName("Inventory.increase(amount): 정상 증가")
    void increase_정상증가() {
        Inventory inventory = Inventory.create(new ProductId(1L), new StockQuantity(5));

        inventory.increase(3);

        assertThat(inventory.getQuantity().getValue()).isEqualTo(8);
    }
}
