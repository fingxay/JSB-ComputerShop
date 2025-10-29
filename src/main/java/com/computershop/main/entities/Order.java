package com.computershop.main.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;
    
    @Column(name = "status")
    private String status = "pending"; // Default status
    
    // Default constructor
    public Order() {}
    
    // Constructor with parameters
    public Order(User user, LocalDateTime orderDate) {
        this.user = user;
        this.orderDate = orderDate;
    }
    
    // Getters and Setters
    public Integer getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }
    
    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Helper method to calculate total amount
    public double getTotalAmount() {
        if (orderDetails == null || orderDetails.isEmpty()) {
            return 0.0;
        }
        return orderDetails.stream()
                .mapToDouble(detail -> detail.getPrice().doubleValue() * detail.getQuantity())
                .sum();
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", orderDate=" + orderDate +
                '}';
    }
}