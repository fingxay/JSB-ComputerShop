package com.computershop.main.repositories;

import com.computershop.main.entities.OrderDetail;
import com.computershop.main.entities.Order;
import com.computershop.main.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    
    /**
     * Find order details by order
     */
    List<OrderDetail> findByOrder(Order order);
    
    /**
     * Find order details by order ID
     */
    List<OrderDetail> findByOrderOrderId(Integer orderId);
    
    /**
     * Find order details by product
     */
    List<OrderDetail> findByProduct(Product product);
    
    /**
     * Find order details by product ID
     */
    List<OrderDetail> findByProductProductId(Integer productId);
    
    /**
     * Find order details by quantity greater than specified amount
     */
    List<OrderDetail> findByQuantityGreaterThan(Integer minQuantity);
    
    /**
     * Find order details by price range
     */
    List<OrderDetail> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Calculate total revenue for a product
     */
    @Query("SELECT SUM(od.price * od.quantity) FROM OrderDetail od WHERE od.product.productId = :productId")
    BigDecimal calculateTotalRevenueByProduct(@Param("productId") Integer productId);
    
    /**
     * Calculate total quantity sold for a product
     */
    @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.product.productId = :productId")
    Long calculateTotalQuantitySoldByProduct(@Param("productId") Integer productId);
    
    /**
     * Find best selling products by quantity
     */
    @Query("SELECT od.product, SUM(od.quantity) as totalSold FROM OrderDetail od " +
           "GROUP BY od.product ORDER BY totalSold DESC")
    List<Object[]> findBestSellingProductsByQuantity();
    
    /**
     * Find best selling products by revenue
     */
    @Query("SELECT od.product, SUM(od.price * od.quantity) as totalRevenue FROM OrderDetail od " +
           "GROUP BY od.product ORDER BY totalRevenue DESC")
    List<Object[]> findBestSellingProductsByRevenue();
    
    /**
     * Calculate total order amount
     */
    @Query("SELECT SUM(od.price * od.quantity) FROM OrderDetail od WHERE od.order.orderId = :orderId")
    BigDecimal calculateOrderTotal(@Param("orderId") Integer orderId);
    
    /**
     * Count items in order
     */
    @Query("SELECT COUNT(od) FROM OrderDetail od WHERE od.order.orderId = :orderId")
    long countItemsInOrder(@Param("orderId") Integer orderId);
    
    /**
     * Find order details with product info for reporting
     */
    @Query("SELECT od, od.product.productName, od.product.price FROM OrderDetail od WHERE od.order.orderId = :orderId")
    List<Object[]> findOrderDetailsWithProductInfo(@Param("orderId") Integer orderId);
}