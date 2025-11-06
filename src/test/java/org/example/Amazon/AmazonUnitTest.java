package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AmazonUnitTest {

    private ShoppingCart mockCart;
    private List<PriceRule> rules;

    @BeforeEach
    void setUp() {
        mockCart = Mockito.mock(ShoppingCart.class);
        rules = new ArrayList<>();
    }


    @Test
    @DisplayName("specification-based")
    void testCalculate_withNoRules_returnsZero() {
        // Test: empty rules list
        when(mockCart.getItems()).thenReturn(new ArrayList<>());

        Amazon amazon = new Amazon(mockCart, rules);
        double result = amazon.calculate();

        assertThat(result).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based")
    void testCalculate_withSingleRule_returnsCorrectPrice() {
        // Test: single pricing rule
        Item item1 = new Item(ItemType.OTHER, "Book", 2, 15.0);
        Item item2 = new Item(ItemType.OTHER, "Pen", 5, 2.0);
        List<Item> items = Arrays.asList(item1, item2);

        when(mockCart.getItems()).thenReturn(items);
        rules.add(new RegularCost());

        Amazon amazon = new Amazon(mockCart, rules);
        double result = amazon.calculate();

        // (2 * 15.0) + (5 * 2.0) = 40.0
        assertThat(result).isEqualTo(40.0);
    }

    @Test
    @DisplayName("specification-based")
    void testCalculate_withMultipleRules_aggregatesAllPrices() {
        // Test normal case: multiple pricing rules combined
        Item item = new Item(ItemType.ELECTRONIC, "Laptop", 1, 1000.0);
        List<Item> items = List.of(item);

        when(mockCart.getItems()).thenReturn(items);
        rules.add(new RegularCost());
        rules.add(new DeliveryPrice());
        rules.add(new ExtraCostForElectronics());

        Amazon amazon = new Amazon(mockCart, rules);
        double result = amazon.calculate();

        // 1000.0 (regular) + 5.0 (delivery for 1 item) + 7.5 (electronics) = 1012.5
        assertThat(result).isEqualTo(1012.5);
    }

    @Test
    @DisplayName("specification-based")
    void testCalculate_withEmptyCart_returnsOnlyFixedCosts() {
        // Test boundary: empty cart
        when(mockCart.getItems()).thenReturn(new ArrayList<>());
        rules.add(new RegularCost());
        rules.add(new DeliveryPrice());

        Amazon amazon = new Amazon(mockCart, rules);
        double result = amazon.calculate();

        // 0.0 (regular) + 0.0 (delivery for 0 items) = 0.0
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    @DisplayName("specification-based")
    void testAddToCart_addsItemSuccessfully() {
        // Test normal operation of addToCart
        Item item = new Item(ItemType.OTHER, "Mouse", 1, 25.0);

        Amazon amazon = new Amazon(mockCart, rules);
        amazon.addToCart(item);

        verify(mockCart, times(1)).add(item);
    }

    @Test
    @DisplayName("specification-based")
    void testAddToCart_multipleItems_addsAllItems() {
        // Test adding multiple items
        Item item1 = new Item(ItemType.OTHER, "Book", 1, 20.0);
        Item item2 = new Item(ItemType.ELECTRONIC, "Phone", 1, 500.0);

        Amazon amazon = new Amazon(mockCart, rules);
        amazon.addToCart(item1);
        amazon.addToCart(item2);

        verify(mockCart, times(1)).add(item1);
        verify(mockCart, times(1)).add(item2);
        verify(mockCart, times(2)).add(any(Item.class));
    }

    // ========== STRUCTURAL-BASED TESTS ==========

    @Test
    @DisplayName("structural-based")
    void testCalculate_loopCoverage_zeroIterations() {
        // Cover loop with zero iterations (empty rules)
        when(mockCart.getItems()).thenReturn(new ArrayList<>());

        Amazon amazon = new Amazon(mockCart, new ArrayList<>());
        double result = amazon.calculate();

        assertThat(result).isEqualTo(0.0);
        verify(mockCart, never()).getItems();
    }

    @Test
    @DisplayName("structural-based")
    void testCalculate_loopCoverage_singleIteration() {
        // Cover loop with exactly one iteration
        Item item = new Item(ItemType.OTHER, "Notebook", 3, 5.0);
        when(mockCart.getItems()).thenReturn(List.of(item));

        rules.add(new RegularCost());
        Amazon amazon = new Amazon(mockCart, rules);
        double result = amazon.calculate();

        assertThat(result).isEqualTo(15.0);
        verify(mockCart, times(1)).getItems();
    }

    @Test
    @DisplayName("structural-based")
    void testCalculate_loopCoverage_multipleIterations() {
        // Cover loop with multiple iterations
        Item item1 = new Item(ItemType.OTHER, "Item1", 1, 10.0);
        Item item2 = new Item(ItemType.OTHER, "Item2", 1, 20.0);
        when(mockCart.getItems()).thenReturn(Arrays.asList(item1, item2));

        rules.add(new RegularCost());
        rules.add(new DeliveryPrice());
        rules.add(new ExtraCostForElectronics());

        Amazon amazon = new Amazon(mockCart, rules);
        double result = amazon.calculate();

        // 30.0 (regular) + 5.0 (delivery) + 0.0 (no electronics) = 35.0
        assertThat(result).isEqualTo(35.0);
        verify(mockCart, times(3)).getItems();
    }

    @Test
    @DisplayName("structural-based")
    void testCalculate_pathCoverage_regularCostOnly() {
        // Test path with only RegularCost rule
        Item item = new Item(ItemType.OTHER, "Widget", 4, 12.5);
        when(mockCart.getItems()).thenReturn(List.of(item));

        rules.add(new RegularCost());
        Amazon amazon = new Amazon(mockCart, rules);
        double result = amazon.calculate();

        assertThat(result).isEqualTo(50.0);
    }

    @Test
    @DisplayName("structural-based")
    void testCalculate_pathCoverage_deliveryPriceOnly() {
        // Test path with only DeliveryPrice rule
        Item item1 = new Item(ItemType.OTHER, "Item1", 1, 10.0);
        Item item2 = new Item(ItemType.OTHER, "Item2", 1, 10.0);
        Item item3 = new Item(ItemType.OTHER, "Item3", 1, 10.0);
        when(mockCart.getItems()).thenReturn(Arrays.asList(item1, item2, item3));

        rules.add(new DeliveryPrice());
        Amazon amazon = new Amazon(mockCart, rules);
        double result = amazon.calculate();

        // 3 items = $5 delivery
        assertThat(result).isEqualTo(5.0);
    }

    @Test
    @DisplayName("structural-based")
    void testCalculate_pathCoverage_electronicsCostOnly() {
        // Test path with only ExtraCostForElectronics rule
        Item electronic = new Item(ItemType.ELECTRONIC, "Tablet", 1, 300.0);
        when(mockCart.getItems()).thenReturn(List.of(electronic));

        rules.add(new ExtraCostForElectronics());
        Amazon amazon = new Amazon(mockCart, rules);
        double result = amazon.calculate();

        assertThat(result).isEqualTo(7.5);
    }

    @Test
    @DisplayName("structural-based")
    void testAddToCart_statementCoverage() {
        // Ensure all statements in addToCart are covered
        Item item = new Item(ItemType.OTHER, "Test", 1, 1.0);

        Amazon amazon = new Amazon(mockCart, rules);
        amazon.addToCart(item);

        verify(mockCart).add(item);
        verifyNoMoreInteractions(mockCart);
    }

    @Test
    @DisplayName("structural-based")
    void testCalculate_boundaryValue_largeNumberOfItems() {
        // Test with many items to cover different delivery price tiers
        List<Item> manyItems = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            manyItems.add(new Item(ItemType.OTHER, "Item" + i, 1, 1.0));
        }
        when(mockCart.getItems()).thenReturn(manyItems);

        rules.add(new RegularCost());
        rules.add(new DeliveryPrice());

        Amazon amazon = new Amazon(mockCart, rules);
        double result = amazon.calculate();

        // 15 items * $1.0 = $15.0 + $20.0 delivery (>10 items) = $35.0
        assertThat(result).isEqualTo(35.0);
    }

    @Test
    @DisplayName("structural-based")
    void testCalculate_dataFlow_finalPriceAccumulation() {
        // Test that finalPrice accumulates correctly across multiple rules
        Item item = new Item(ItemType.ELECTRONIC, "Camera", 2, 200.0);
        when(mockCart.getItems()).thenReturn(List.of(item));

        rules.add(new RegularCost());
        rules.add(new DeliveryPrice());
        rules.add(new ExtraCostForElectronics());

        Amazon amazon = new Amazon(mockCart, rules);
        double result = amazon.calculate();

        assertThat(result).isEqualTo(412.5);
    }
}
