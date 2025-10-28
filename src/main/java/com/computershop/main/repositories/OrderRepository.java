package com.computershop.main.repositories;

import com.computershop.main.entities.Order;
import com.computershop.main.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    /**
     * Find orders by user
     */
    List<Order> findByUser(User user);
    
    /**
     * Find orders by user ID
     */
    List<Order> findByUserUserId(Integer userId);
    
    /**
     * Find orders by date range
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find orders by user ordered by date descending (most recent first)
     */
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    /**
     * Find orders by user ID ordered by date descending
     */
    List<Order> findByUserUserIdOrderByOrderDateDesc(Integer userId);
    
    /**
     * Find orders from today
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :startOfDay AND o.orderDate < :endOfDay")
    List<Order> findTodayOrders(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    /**
     * Find orders from last N days
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :startDate")
    List<Order> findOrdersFromLastDays(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Count orders by user
     */
    long countByUser(User user);
    
    /**
     * Count orders by user ID
     */
    long countByUserUserId(Integer userId);
    
    /**
     * Find orders with total amount calculation
     */
    @Query("SELECT o, SUM(od.price * od.quantity) as totalAmount FROM Order o " +
           "JOIN o.orderDetails od GROUP BY o.orderId")
    List<Object[]> findOrdersWithTotalAmount();
    
    /**
     * Find recent orders (last 30 days)
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :thirtyDaysAgo ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
    
    /**
     * Find recent orders by user ID with limit
     */
    @Query(value = "SELECT TOP :limit * FROM orders WHERE user_id = :userId ORDER BY order_date DESC", nativeQuery = true)
    List<Order> findRecentOrdersByUserId(@Param("userId") Integer userId, @Param("limit") int limit);
    
    /**
     * Get total spent by user ID
     */
    @Query("SELECT COALESCE(SUM(od.price * od.quantity), 0) FROM Order o " +
           "JOIN o.orderDetails od WHERE o.user.userId = :userId")
    double getTotalSpentByUserId(@Param("userId") Integer userId);
    
    /**
     * Find recent orders for admin dashboard
     */
    @Query(value = "SELECT TOP :limit * FROM orders ORDER BY order_date DESC", nativeQuery = true)
    List<Order> findRecentOrdersForAdmin(@Param("limit") int limit);
}