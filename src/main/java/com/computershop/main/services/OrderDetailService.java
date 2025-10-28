package com.computershop.main.services;

import com.computershop.main.entities.OrderDetail;
import com.computershop.main.entities.Order;
import com.computershop.main.entities.Product;
import com.computershop.main.repositories.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailService {
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Autowired
    private ProductService productService;
    
    /**
     * Get all order details
     */
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }
    
    /**
     * Get order detail by ID
     */
    public Optional<OrderDetail> getOrderDetailById(Integer orderDetailId) {
        return orderDetailRepository.findById(orderDetailId);
    }
    
    /**
     * Get order details by order
     */
    public List<OrderDetail> getOrderDetailsByOrder(Order order) {
        return orderDetailRepository.findByOrder(order);
    }
    
    /**
     * Get order details by order ID
     */
    public List<OrderDetail> getOrderDetailsByOrderId(Integer orderId) {
        return orderDetailRepository.findByOrderOrderId(orderId);
    }
    
    /**
     * Get order details by product
     */
    public List<OrderDetail> getOrderDetailsByProduct(Product product) {
        return orderDetailRepository.findByProduct(product);
    }
    
    /**
     * Get order details by product ID
     */
    public List<OrderDetail> getOrderDetailsByProductId(Integer productId) {
        return orderDetailRepository.findByProductProductId(productId);
    }
    
    /**
     * Get order details by quantity greater than
     */
    public List<OrderDetail> getOrderDetailsByQuantityGreaterThan(Integer minQuantity) {
        return orderDetailRepository.findByQuantityGreaterThan(minQuantity);
    }
    
    /**
     * Get order details by price range
     */
    public List<OrderDetail> getOrderDetailsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return orderDetailRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    /**
     * Create new order detail
     */
    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        // Validate stock availability
        Product product = orderDetail.getProduct();
        if (!productService.hasSufficientStock(product.getProductId(), orderDetail.getQuantity())) {
            throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
        }
        
        // Decrease product stock
        productService.decreaseStock(product.getProductId(), orderDetail.getQuantity());
        
        return orderDetailRepository.save(orderDetail);
    }
    
    /**
     * Create order detail with parameters
     */
    public OrderDetail createOrderDetail(Order order, Product product, Integer quantity, BigDecimal price) {
        // Validate stock availability
        if (!productService.hasSufficientStock(product.getProductId(), quantity)) {
            throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
        }
        
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setQuantity(quantity);
        orderDetail.setPrice(price);
        
        // Decrease product stock
        productService.decreaseStock(product.getProductId(), quantity);
        
        return orderDetailRepository.save(orderDetail);
    }
    
    /**
     * Update order detail
     */
    public OrderDetail updateOrderDetail(Integer orderDetailId, OrderDetail orderDetailDetails) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("Order detail not found with id: " + orderDetailId));
        
        // If quantity is changing, adjust stock
        Integer oldQuantity = orderDetail.getQuantity();
        Integer newQuantity = orderDetailDetails.getQuantity();
        
        if (!oldQuantity.equals(newQuantity)) {
            Product product = orderDetail.getProduct();
            Integer quantityDifference = newQuantity - oldQuantity;
            
            if (quantityDifference > 0) {
                // Increasing quantity - check stock availability
                if (!productService.hasSufficientStock(product.getProductId(), quantityDifference)) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
                }
                productService.decreaseStock(product.getProductId(), quantityDifference);
            } else {
                // Decreasing quantity - return stock
                productService.increaseStock(product.getProductId(), Math.abs(quantityDifference));
            }
        }
        
        orderDetail.setQuantity(orderDetailDetails.getQuantity());
        orderDetail.setPrice(orderDetailDetails.getPrice());
        
        return orderDetailRepository.save(orderDetail);
    }
    
    /**
     * Delete order detail
     */
    public void deleteOrderDetail(Integer orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("Order detail not found with id: " + orderDetailId));
        
        // Return stock to product
        productService.increaseStock(orderDetail.getProduct().getProductId(), orderDetail.getQuantity());
        
        orderDetailRepository.deleteById(orderDetailId);
    }
    
    /**
     * Calculate total revenue by product
     */
    public BigDecimal calculateTotalRevenueByProduct(Integer productId) {
        BigDecimal revenue = orderDetailRepository.calculateTotalRevenueByProduct(productId);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    
    /**
     * Calculate total quantity sold by product
     */
    public Long calculateTotalQuantitySoldByProduct(Integer productId) {
        Long quantity = orderDetailRepository.calculateTotalQuantitySoldByProduct(productId);
        return quantity != null ? quantity : 0L;
    }
    
    /**
     * Get best selling products by quantity
     */
    public List<Object[]> getBestSellingProductsByQuantity() {
        return orderDetailRepository.findBestSellingProductsByQuantity();
    }
    
    /**
     * Get best selling products by revenue
     */
    public List<Object[]> getBestSellingProductsByRevenue() {
        return orderDetailRepository.findBestSellingProductsByRevenue();
    }
    
    /**
     * Calculate order total
     */
    public BigDecimal calculateOrderTotal(Integer orderId) {
        BigDecimal total = orderDetailRepository.calculateOrderTotal(orderId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Count items in order
     */
    public long countItemsInOrder(Integer orderId) {
        return orderDetailRepository.countItemsInOrder(orderId);
    }
    
    /**
     * Get order details with product info
     */
    public List<Object[]> getOrderDetailsWithProductInfo(Integer orderId) {
        return orderDetailRepository.findOrderDetailsWithProductInfo(orderId);
    }
    
    /**
     * Process order items (create multiple order details for an order)
     */
    public List<OrderDetail> processOrderItems(Order order, List<OrderItemRequest> orderItems) {
        return orderItems.stream().map(item -> {
            Product product = productService.getProductById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + item.getProductId()));
            
            return createOrderDetail(order, product, item.getQuantity(), item.getPrice());
        }).toList();
    }
    
    /**
     * Inner class for order item requests
     */
    public static class OrderItemRequest {
        private Integer productId;
        private Integer quantity;
        private BigDecimal price;
        
        public OrderItemRequest() {}
        
        public OrderItemRequest(Integer productId, Integer quantity, BigDecimal price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }
        
        // Getters and Setters
        public Integer getProductId() { return productId; }
        public void setProductId(Integer productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }
}