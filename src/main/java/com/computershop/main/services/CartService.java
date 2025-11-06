package com.computershop.main.services;

import com.computershop.main.entities.Cart;
import com.computershop.main.entities.CartItem;
import com.computershop.main.entities.Product;
import com.computershop.main.entities.User;
import com.computershop.main.repositories.CartRepository;
import com.computershop.main.repositories.CartItemRepository;
import com.computershop.main.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public Optional<Cart> getCartByUserId(Integer userId) {
        return cartRepository.findByUserIdWithItems(userId);
    }
    
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }
    
    @Transactional
    public CartItem addToCart(Integer userId, Integer productId, Integer quantity) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock. Available: " + product.getStockQuantity());
        }
        
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            
            if (product.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Not enough stock. Available: " + product.getStockQuantity());
            }
            
            item.setQuantity(newQuantity);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
            return cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            cart.addCartItem(newItem);
            cartRepository.save(cart);
            return newItem;
        }
    }
    
    @Transactional
    public void updateCartItemQuantity(Integer cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));
        
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
        
        if (item.getProduct().getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock. Available: " + item.getProduct().getStockQuantity());
        }
        
        item.setQuantity(quantity);
        item.getCart().setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(item);
    }
    
    @Transactional
    public void removeFromCart(Integer cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));
        
        Cart cart = item.getCart();
        cart.removeCartItem(item);
        cartItemRepository.delete(item);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
    
    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
        
        cart.getCartItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
    
    public BigDecimal getCartTotal(Integer userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);
        
        if (cartOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return cartOpt.get().getCartItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public int getCartItemCount(Integer userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);
        
        if (cartOpt.isEmpty()) {
            return 0;
        }
        
        return cartOpt.get().getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    public List<CartItem> getCartItems(Integer userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);
        return cartOpt.map(Cart::getCartItems).orElse(List.of());
    }
}
