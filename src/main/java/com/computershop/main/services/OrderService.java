package com.computershop.main.services;

import com.computershop.main.entities.Order;
import com.computershop.main.entities.User;
import com.computershop.main.entities.OrderDetail;
import com.computershop.main.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderDetailService orderDetailService;
    
    /**
     * Get all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    /**
     * Get order by ID
     */
    public Optional<Order> getOrderById(Integer orderId) {
        return orderRepository.findById(orderId);
    }
    
    /**
     * Get orders by user
     */
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
    
    /**
     * Get orders by user ID
     */
    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserUserId(userId);
    }
    
    /**
     * Get user's orders (most recent first)
     */
    public List<Order> getUserOrdersRecent(Integer userId) {
        return orderRepository.findByUserUserIdOrderByOrderDateDesc(userId);
    }
    
    /**
     * Get orders by date range
     */
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }
    
    /**
     * Get today's orders
     */
    public List<Order> getTodayOrders() {
        return orderRepository.findTodayOrders();
    }
    
    /**
     * Get orders from last N days
     */
    public List<Order> getOrdersFromLastDays(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return orderRepository.findOrdersFromLastDays(startDate);
    }
    
    /**
     * Get recent orders (last 30 days)
     */
    public List<Order> getRecentOrders() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return orderRepository.findRecentOrders(thirtyDaysAgo);
    }
    
    /**
     * Create new order
     */
    public Order createOrder(Order order) {
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        return orderRepository.save(order);
    }
    
    /**
     * Create order for user
     */
    public Order createOrderForUser(Integer userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    /**
     * Update order
     */
    public Order updateOrder(Integer orderId, Order orderDetails) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        if (orderDetails.getUser() != null) {
            order.setUser(orderDetails.getUser());
        }
        if (orderDetails.getOrderDate() != null) {
            order.setOrderDate(orderDetails.getOrderDate());
        }
        
        return orderRepository.save(order);
    }
    
    /**
     * Delete order
     */
    public void deleteOrder(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Order not found with id: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }
    
    /**
     * Count orders by user
     */
    public long countOrdersByUser(User user) {
        return orderRepository.countByUser(user);
    }
    
    /**
     * Count orders by user ID
     */
    public long countOrdersByUserId(Integer userId) {
        return orderRepository.countByUserUserId(userId);
    }
    
    /**
     * Get orders with total amounts
     */
    public List<Object[]> getOrdersWithTotalAmount() {
        return orderRepository.findOrdersWithTotalAmount();
    }
    
    /**
     * Calculate order total using OrderDetailService
     */
    public Double calculateOrderTotal(Integer orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        return orderDetails.stream()
                .mapToDouble(od -> od.getPrice().doubleValue() * od.getQuantity())
                .sum();
    }
    
    /**
     * Get order statistics for user
     */
    public OrderStatistics getOrderStatisticsForUser(Integer userId) {
        long totalOrders = countOrdersByUserId(userId);
        List<Order> recentOrders = getUserOrdersRecent(userId);
        
        // Calculate total spent (simplified - in real app, calculate from order details)
        double totalSpent = 0.0;
        for (Order order : recentOrders) {
            totalSpent += calculateOrderTotal(order.getOrderId());
        }
        
        return new OrderStatistics(totalOrders, totalSpent, recentOrders.size());
    }
    
    /**
     * Inner class for order statistics
     */
    public static class OrderStatistics {
        private long totalOrders;
        private double totalSpent;
        private int recentOrdersCount;
        
        public OrderStatistics(long totalOrders, double totalSpent, int recentOrdersCount) {
            this.totalOrders = totalOrders;
            this.totalSpent = totalSpent;
            this.recentOrdersCount = recentOrdersCount;
        }
        
        // Getters
        public long getTotalOrders() { return totalOrders; }
        public double getTotalSpent() { return totalSpent; }
        public int getRecentOrdersCount() { return recentOrdersCount; }
    }
}