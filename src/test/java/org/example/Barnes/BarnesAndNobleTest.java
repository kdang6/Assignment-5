//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.example.Barnes;

import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BarnesAndNobleTest {
    private BookDatabase mockBookDatabase;
    private BuyBookProcess mockBuyBookProcess;
    private BarnesAndNoble barnesAndNoble;

    @BeforeEach
    void setUp() {
        this.mockBookDatabase = (BookDatabase)Mockito.mock(BookDatabase.class);
        this.mockBuyBookProcess = (BuyBookProcess)Mockito.mock(BuyBookProcess.class);
        this.barnesAndNoble = new BarnesAndNoble(this.mockBookDatabase, this.mockBuyBookProcess);
    }

    @Test
    @DisplayName("specification-based")
    void testNullOrder() {
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart((Map)null);
        Assertions.assertThat(result).isNull();
    }

    @Test
    @DisplayName("specification-based")
    void testEmptyOrder() {
        Map<String, Integer> emptyOrder = new HashMap();
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(emptyOrder);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(0);
        Assertions.assertThat(result.getUnavailable()).isEmpty();
    }

    @Test
    @DisplayName("specification-based")
    void testSingleBook() {
        Book book = new Book("123-456", 25, 10);
        Mockito.when(this.mockBookDatabase.findByISBN("123-456")).thenReturn(book);
        Map<String, Integer> order = new HashMap();
        order.put("123-456", 3);
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(75);
        Assertions.assertThat(result.getUnavailable()).isEmpty();
        ((BuyBookProcess)Mockito.verify(this.mockBuyBookProcess)).buyBook(book, 3);
    }

    @Test
    @DisplayName("specification-based")
    void testSingleBookQuantity() {
        Book book = new Book("123-456", 30, 5);
        Mockito.when(this.mockBookDatabase.findByISBN("123-456")).thenReturn(book);
        Map<String, Integer> order = new HashMap();
        order.put("123-456", 8);
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(150);
        Assertions.assertThat(result.getUnavailable()).hasSize(1);
        Assertions.assertThat((Integer)result.getUnavailable().get(book)).isEqualTo(3);
        ((BuyBookProcess)Mockito.verify(this.mockBuyBookProcess)).buyBook(book, 5);
    }

    @Test
    @DisplayName("specification-based")
    void testMultipleBooks() {
        Book book1 = new Book("111-111", 20, 10);
        Book book2 = new Book("222-222", 15, 8);
        Book book3 = new Book("333-333", 40, 5);
        Mockito.when(this.mockBookDatabase.findByISBN("111-111")).thenReturn(book1);
        Mockito.when(this.mockBookDatabase.findByISBN("222-222")).thenReturn(book2);
        Mockito.when(this.mockBookDatabase.findByISBN("333-333")).thenReturn(book3);
        Map<String, Integer> order = new HashMap();
        order.put("111-111", 2);
        order.put("222-222", 3);
        order.put("333-333", 1);
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(125);
        Assertions.assertThat(result.getUnavailable()).isEmpty();
        ((BuyBookProcess)Mockito.verify(this.mockBuyBookProcess)).buyBook(book1, 2);
        ((BuyBookProcess)Mockito.verify(this.mockBuyBookProcess)).buyBook(book2, 3);
        ((BuyBookProcess)Mockito.verify(this.mockBuyBookProcess)).buyBook(book3, 1);
    }

    @Test
    @DisplayName("specification-based")
    void testMultipleBooksSomeUnavailable() {
        Book book1 = new Book("111-111", 20, 10);
        Book book2 = new Book("222-222", 15, 2);
        Book book3 = new Book("333-333", 40, 5);
        Mockito.when(this.mockBookDatabase.findByISBN("111-111")).thenReturn(book1);
        Mockito.when(this.mockBookDatabase.findByISBN("222-222")).thenReturn(book2);
        Mockito.when(this.mockBookDatabase.findByISBN("333-333")).thenReturn(book3);
        Map<String, Integer> order = new HashMap();
        order.put("111-111", 2);
        order.put("222-222", 5);
        order.put("333-333", 7);
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(270);
        Assertions.assertThat(result.getUnavailable()).hasSize(2);
        Assertions.assertThat((Integer)result.getUnavailable().get(book2)).isEqualTo(3);
        Assertions.assertThat((Integer)result.getUnavailable().get(book3)).isEqualTo(2);
    }

    @Test
    @DisplayName("structural-based")
    void testNullCheck() {
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart((Map)null);
        Assertions.assertThat(result).isNull();
        Mockito.verifyNoInteractions(new Object[]{this.mockBookDatabase, this.mockBuyBookProcess});
    }

    @Test
    @DisplayName("structural-based")
    void testEnoughQuantity() {
        Book book = new Book("999-999", 50, 10);
        Mockito.when(this.mockBookDatabase.findByISBN("999-999")).thenReturn(book);
        Map<String, Integer> order = new HashMap();
        order.put("999-999", 10);
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(500);
        Assertions.assertThat(result.getUnavailable()).isEmpty();
        ((BuyBookProcess)Mockito.verify(this.mockBuyBookProcess)).buyBook(book, 10);
    }

    @Test
    @DisplayName("structural-based")
    void testNotEnoughQuantity() {
        Book book = new Book("888-888", 25, 3);
        Mockito.when(this.mockBookDatabase.findByISBN("888-888")).thenReturn(book);
        Map<String, Integer> order = new HashMap();
        order.put("888-888", 10);
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(75);
        Assertions.assertThat(result.getUnavailable()).containsKey(book);
        ((BuyBookProcess)Mockito.verify(this.mockBuyBookProcess)).buyBook(book, 3);
    }

    @Test
    @DisplayName("structural-based")
    void testSingleIteration() {
        Book book = new Book("777-777", 100, 5);
        Mockito.when(this.mockBookDatabase.findByISBN("777-777")).thenReturn(book);
        Map<String, Integer> order = new HashMap();
        order.put("777-777", 1);
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(100);
        ((BookDatabase)Mockito.verify(this.mockBookDatabase, Mockito.times(1))).findByISBN(Mockito.anyString());
        ((BuyBookProcess)Mockito.verify(this.mockBuyBookProcess, Mockito.times(1))).buyBook((Book)Mockito.any(), Mockito.anyInt());
    }

    @Test
    @DisplayName("structural-based")
    void testMultipleIterations() {
        Book book1 = new Book("111-111", 10, 10);
        Book book2 = new Book("222-222", 20, 10);
        Book book3 = new Book("333-333", 30, 10);
        Book book4 = new Book("444-444", 40, 10);
        Mockito.when(this.mockBookDatabase.findByISBN("111-111")).thenReturn(book1);
        Mockito.when(this.mockBookDatabase.findByISBN("222-222")).thenReturn(book2);
        Mockito.when(this.mockBookDatabase.findByISBN("333-333")).thenReturn(book3);
        Mockito.when(this.mockBookDatabase.findByISBN("444-444")).thenReturn(book4);
        Map<String, Integer> order = new HashMap();
        order.put("111-111", 1);
        order.put("222-222", 1);
        order.put("333-333", 1);
        order.put("444-444", 1);
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(100);
        ((BookDatabase)Mockito.verify(this.mockBookDatabase, Mockito.times(4))).findByISBN(Mockito.anyString());
        ((BuyBookProcess)Mockito.verify(this.mockBuyBookProcess, Mockito.times(4))).buyBook((Book)Mockito.any(), Mockito.anyInt());
    }

    @Test
    @DisplayName("structural-based")
    void testZeroQuantity() {
        Book book = new Book("000-000", 50, 0);
        Mockito.when(this.mockBookDatabase.findByISBN("000-000")).thenReturn(book);
        Map<String, Integer> order = new HashMap();
        order.put("000-000", 5);
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(0);
        Assertions.assertThat((Integer)result.getUnavailable().get(book)).isEqualTo(5);
        ((BuyBookProcess)Mockito.verify(this.mockBuyBookProcess)).buyBook(book, 0);
    }

    @Test
    @DisplayName("structural-based")
    void testExactQuantityMatch() {
        Book book = new Book("555-555", 45, 7);
        Mockito.when(this.mockBookDatabase.findByISBN("555-555")).thenReturn(book);
        Map<String, Integer> order = new HashMap();
        order.put("555-555", 7);
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(order);
        Assertions.assertThat(result.getTotalPrice()).isEqualTo(315);
        Assertions.assertThat(result.getUnavailable()).isEmpty();
        ((BuyBookProcess)Mockito.verify(this.mockBuyBookProcess)).buyBook(book, 7);
    }

    @Test
    @DisplayName("structural-based")
    void testBookEquals() {
        Book book = new Book("123-456", 25, 5);
        Mockito.when(this.mockBookDatabase.findByISBN("123-456")).thenReturn(book);
        Map<String, Integer> order = new HashMap();
        order.put("123-456", 10);
        PurchaseSummary result = this.barnesAndNoble.getPriceForCart(order);
        Book resultBook = (Book)result.getUnavailable().keySet().iterator().next();
        boolean sameRef = resultBook.equals(resultBook);
        Assertions.assertThat(sameRef).isTrue();
        boolean nullCheck = resultBook.equals((Object)null);
        Assertions.assertThat(nullCheck).isFalse();
        boolean diffClass = resultBook.equals("NotABook");
        Assertions.assertThat(diffClass).isFalse();
        Book sameISBN = new Book("123-456", 99, 99);
        boolean sameIsbn = resultBook.equals(sameISBN);
        Assertions.assertThat(sameIsbn).isTrue();
        Book diffISBN = new Book("999-999", 25, 5);
        boolean differentIsbn = resultBook.equals(diffISBN);
        Assertions.assertThat(differentIsbn).isFalse();
    }
}
