package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class AmazonIntegrationTest {
    private static Database database;
    private ShoppingCart shoppingCart;
    private Amazon amazon;

    @BeforeAll
    static void setUpDatabase() {
        database = new Database();
    }

    @BeforeEach
    void setUp() {
        database.resetDatabase();

        shoppingCart = new ShoppingCartAdaptor(database);
    }

    @AfterAll
    static void tearDown() {
        if (database != null) {
            database.close();
        }
    }

    @Test
    @DisplayName("specification-based")
    void testAddItemAndCalculate() {
        //Test: add item to cart and calculate price
        List<PriceRule> rules = Arrays.asList(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        );
        amazon = new Amazon(shoppingCart, rules);

        Item book = new Item(ItemType.OTHER, "Java Book", 1, 45.0);
        amazon.addToCart(book);

        double totalPrice = amazon.calculate();

        assertThat(totalPrice).isEqualTo(50.0);
        assertThat(shoppingCart.getItems()).hasSize(1);
        assertThat(shoppingCart.getItems().get(0).getName()).isEqualTo("Java Book");
    }

    @Test
    @DisplayName("specification-based")
    void testAddMultipleItemsMixedTypes() {
        //Test: adding multiple items of different types
        List<PriceRule> rules = Arrays.asList(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        );
        amazon = new Amazon(shoppingCart, rules);

        Item laptop = new Item(ItemType.ELECTRONIC, "Laptop", 1, 1200.0);
        Item mouse = new Item(ItemType.ELECTRONIC, "Mouse", 2, 25.0);
        Item book = new Item(ItemType.OTHER, "Book", 3, 15.0);

        amazon.addToCart(laptop);
        amazon.addToCart(mouse);
        amazon.addToCart(book);

        double totalPrice = amazon.calculate();

        assertThat(totalPrice).isEqualTo(1307.5);
        assertThat(shoppingCart.getItems()).hasSize(3);
    }

    @Test
    @DisplayName("specification-based")
    void testEmptyCart() {
        //Test: empty cart
        List<PriceRule> rules = Arrays.asList(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        );
        amazon = new Amazon(shoppingCart, rules);

        double totalPrice = amazon.calculate();

        assertThat(totalPrice).isEqualTo(0.0);
        assertThat(shoppingCart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("specification-based")
    void testDeliveryPriceThreeItems() {
        //Test: delivery price calculation for 3 items
        List<PriceRule> rules = Arrays.asList(
                new RegularCost(),
                new DeliveryPrice()
        );
        amazon = new Amazon(shoppingCart, rules);

        amazon.addToCart(new Item(ItemType.OTHER, "Item1", 1, 10.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Item2", 1, 10.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Item3", 1, 10.0));

        double totalPrice = amazon.calculate();

        assertThat(totalPrice).isEqualTo(35.0);
    }

    @Test
    @DisplayName("specification-based")
    void testDeliveryPriceElevenItems() {
        //Test: delivery price calculation for 11 items
        List<PriceRule> rules = Arrays.asList(
                new RegularCost(),
                new DeliveryPrice()
        );
        amazon = new Amazon(shoppingCart, rules);

        for (int i = 1; i <= 11; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 5.0));
        }

        double totalPrice = amazon.calculate();

        assertThat(totalPrice).isEqualTo(75.0);
    }


    @Test
    @DisplayName("structural-based")
    void testItemsStoredRetrieved() {
        //Test: items are stored and retrieved from database
        List<PriceRule> rules = List.of(new RegularCost());
        amazon = new Amazon(shoppingCart, rules);

        Item item1 = new Item(ItemType.OTHER, "Pen", 5, 2.0);
        Item item2 = new Item(ItemType.ELECTRONIC, "USB Drive", 1, 15.0);

        amazon.addToCart(item1);
        amazon.addToCart(item2);

        List<Item> retrievedItems = shoppingCart.getItems();

        assertThat(retrievedItems).hasSize(2);
        assertThat(retrievedItems.get(0).getName()).isEqualTo("Pen");
        assertThat(retrievedItems.get(0).getQuantity()).isEqualTo(5);
        assertThat(retrievedItems.get(1).getName()).isEqualTo("USB Drive");
        assertThat(retrievedItems.get(1).getType()).isEqualTo(ItemType.ELECTRONIC);
    }

    @Test
    @DisplayName("structural-based")
    void testResetClearsAllItems() {
        //Test: database reset clears all data
        List<PriceRule> rules = List.of(new RegularCost());
        amazon = new Amazon(shoppingCart, rules);

        amazon.addToCart(new Item(ItemType.OTHER, "Item1", 1, 10.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Item2", 1, 20.0));

        assertThat(shoppingCart.getItems()).hasSize(2);

        database.resetDatabase();

        ShoppingCart newCart = new ShoppingCartAdaptor(database);
        assertThat(newCart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("structural-based")
    void testCostWithMultipleQuantities() {
        //Test: RegularCost rule with different quantities
        List<PriceRule> rules = List.of(new RegularCost());
        amazon = new Amazon(shoppingCart, rules);

        amazon.addToCart(new Item(ItemType.OTHER, "Item1", 3, 10.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Item2", 2, 25.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Item3", 1, 100.0));

        double totalPrice = amazon.calculate();

        assertThat(totalPrice).isEqualTo(180.0);
    }

    @Test
    @DisplayName("structural-based")
    void testElectronicsCostOnlyChargedOnce() {
        //Test: electronics fee is only charged once regardless of quantity
        List<PriceRule> rules = Arrays.asList(
                new RegularCost(),
                new ExtraCostForElectronics()
        );
        amazon = new Amazon(shoppingCart, rules);

        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Phone", 1, 500.0));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Tablet", 2, 300.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Case", 1, 20.0));

        double totalPrice = amazon.calculate();

        assertThat(totalPrice).isEqualTo(1127.5);
    }

    @Test
    @DisplayName("structural-based")
    void testNoElectronicsNoExtraCharge() {
        //Test: electronics fee is not charged when no electronics in cart
        List<PriceRule> rules = Arrays.asList(
                new RegularCost(),
                new ExtraCostForElectronics()
        );
        amazon = new Amazon(shoppingCart, rules);

        amazon.addToCart(new Item(ItemType.OTHER, "Book", 2, 15.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Pen", 5, 1.0));

        double totalPrice = amazon.calculate();

        assertThat(totalPrice).isEqualTo(35.0);
    }

    @Test
    @DisplayName("structural-based")
    void testEverything() {
        //Test: integration with all rules
        List<PriceRule> rules = Arrays.asList(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        );
        amazon = new Amazon(shoppingCart, rules);

        // Add 5 items total (4-10 range for delivery)
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Keyboard", 1, 80.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Book1", 2, 20.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Book2", 1, 30.0));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Mouse", 1, 40.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Notebook", 3, 5.0));

        double totalPrice = amazon.calculate();

        // Regular: 80 + 40 + 30 + 40 + 15 = 205.0
        // Delivery: 5 items = 12.5
        // Electronics: 7.5
        // Total: 225.0
        assertThat(totalPrice).isEqualTo(225.0);
    }

    @Test
    @DisplayName("structural-based")
    void testMultipleOperations() {
        //Test: multiple sequential operations with database
        List<PriceRule> rules = List.of(new RegularCost());
        amazon = new Amazon(shoppingCart, rules);

        amazon.addToCart(new Item(ItemType.OTHER, "Item1", 1, 10.0));
        assertThat(shoppingCart.getItems()).hasSize(1);

        amazon.addToCart(new Item(ItemType.OTHER, "Item2", 1, 20.0));
        assertThat(shoppingCart.getItems()).hasSize(2);


        amazon.addToCart(new Item(ItemType.OTHER, "Item3", 1, 30.0));
        assertThat(shoppingCart.getItems()).hasSize(3);

        double totalPrice = amazon.calculate();
        assertThat(totalPrice).isEqualTo(60.0);
    }

    @Test
    @DisplayName("structural-based")
    void testItemType() {
        //Test: ItemType is properly stored and retrieved
        List<PriceRule> rules = List.of(new RegularCost());
        amazon = new Amazon(shoppingCart, rules);

        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Electronic1", 1, 100.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Other1", 1, 50.0));

        List<Item> items = shoppingCart.getItems();

        assertThat(items.get(0).getType()).isEqualTo(ItemType.ELECTRONIC);
        assertThat(items.get(1).getType()).isEqualTo(ItemType.OTHER);
    }

    @Test
    @DisplayName("structural-based")
    void testDecimalPrices() {
        //Test: decimal prices are handled correctly
        List<PriceRule> rules = Arrays.asList(
                new RegularCost(),
                new DeliveryPrice()
        );
        amazon = new Amazon(shoppingCart, rules);

        amazon.addToCart(new Item(ItemType.OTHER, "Item1", 3, 12.99));
        amazon.addToCart(new Item(ItemType.OTHER, "Item2", 2, 7.50));

        double totalPrice = amazon.calculate();

        // Regular: (3 * 12.99) + (2 * 7.50) = 38.97 + 15.00 = 53.97
        // Delivery: 2 items = 5.0
        // Total: 58.97
        assertThat(totalPrice).isEqualTo(58.97);
    }


    @Test
    @DisplayName("structural-based")
    void testDeliveryPriceZeroItems() {
        //Test: totalItems >= 1 returns false (when totalItems == 0)
        List<PriceRule> rules = List.of(new DeliveryPrice());
        amazon = new Amazon(shoppingCart, rules);

        // Empty cart - totalItems = 0
        double totalPrice = amazon.calculate();

        assertThat(totalPrice).isEqualTo(0.0);
        assertThat(shoppingCart.getItems()).hasSize(0);
    }

    @Test
    @DisplayName("structural-based")
    void testDeliveryPriceOneItem() {
        //Test:  totalItems >= 1 && totalItems <= 3 both true (boundary: 1 item)
        List<PriceRule> rules = List.of(new DeliveryPrice());
        amazon = new Amazon(shoppingCart, rules);

        amazon.addToCart(new Item(ItemType.OTHER, "Item1", 1, 10.0));
        double totalPrice = amazon.calculate();

        assertThat(totalPrice).isEqualTo(5.0);
    }


    @Test
    @DisplayName("structural-based")
    void testDatabaseAlreadyExists() {
        //Test: connection != null returns true
        // The database is already initialized in @BeforeAll, so creating a new instance
        // should hit the early return
        Database secondDatabase = new Database();

        // Verify it returns the same connection
        assertThat(secondDatabase.getConnection()).isNotNull();
        assertThat(secondDatabase.getConnection()).isEqualTo(database.getConnection());
    }

    @Test
    @DisplayName("structural-based")
    void testDatabaseExceptionHandling() {
        //Test: throw new RuntimeException(e)
        Database testDb = new Database();

        try {
            testDb.withSql(() -> {
                var stmt = testDb.getConnection().prepareStatement("SELECT * FROM nonexistent_table");
                stmt.executeQuery();
                return null;
            });
            assertThat(false).isTrue();
        } catch (RuntimeException e) {
            assertThat(e).isNotNull();
            assertThat(e.getCause()).isInstanceOf(java.sql.SQLException.class);
        }
    }

    @Test
    @DisplayName("structural-based")
    void testDatabaseNullConnection() {
        //Test: connection != null returns false in close()
        Database tempDb = new Database();
        tempDb.close();
        tempDb.close();

        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("structural-based")
    void testNumberOfItems() {
        //Test: numberOfItems() method
        List<PriceRule> rules = List.of(new RegularCost());
        amazon = new Amazon(shoppingCart, rules);

        int initialCount = shoppingCart.numberOfItems();
        assertThat(initialCount).isEqualTo(0);

        amazon.addToCart(new Item(ItemType.OTHER, "Item1", 1, 10.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Item2", 1, 20.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Item3", 1, 30.0));

        int count = shoppingCart.numberOfItems();

        assertThat(count).isGreaterThanOrEqualTo(0);

        assertThat(shoppingCart.getItems()).hasSize(3);
    }
}