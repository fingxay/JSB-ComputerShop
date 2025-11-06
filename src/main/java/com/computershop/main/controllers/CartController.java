package com.computershop.main.controllers;

import com.computershop.main.entities.Cart;
import com.computershop.main.entities.CartItem;
import com.computershop.main.entities.User;
import com.computershop.main.services.CartService;
import com.computershop.main.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService;
    
    private Integer getUserIdFromSession(HttpSession session) {
        return (Integer) session.getAttribute("userId");
    }
    
    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        Integer userId = getUserIdFromSession(session);
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            List<CartItem> cartItems = cartService.getCartItems(userId);
            BigDecimal total = cartService.getCartTotal(userId);
            int itemCount = cartService.getCartItemCount(userId);
            
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartTotal", total);
            model.addAttribute("cartItemCount", itemCount);
            
            return "cart/view";
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            model.addAttribute("cartItems", List.of());
            model.addAttribute("cartTotal", BigDecimal.ZERO);
            model.addAttribute("cartItemCount", 0);
            return "cart/view";
        }
    }
    
    @PostMapping("/add")
    @ResponseBody
    public String addToCart(@RequestParam("productId") Integer productId,
                          @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                          HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        
        if (userId == null) {
            return "{\"success\": false, \"message\": \"Vui lòng đăng nhập\"}";
        }
        
        try {
            Optional<Cart> cartOpt = cartService.getCartByUserId(userId);
            Cart cart;
            
            if (cartOpt.isEmpty()) {
                Optional<User> userOpt = userService.getUserById(userId);
                if (userOpt.isEmpty()) {
                    return "{\"success\": false, \"message\": \"User không tồn tại\"}";
                }
                cart = cartService.getOrCreateCart(userOpt.get());
            }
            
            cartService.addToCart(userId, productId, quantity);
            int itemCount = cartService.getCartItemCount(userId);
            
            return "{\"success\": true, \"message\": \"Đã thêm vào giỏ hàng\", \"itemCount\": " + itemCount + "}";
            
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
    
    @PostMapping("/update")
    public String updateCartItem(@RequestParam("cartItemId") Integer cartItemId,
                                @RequestParam("quantity") Integer quantity,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Integer userId = getUserIdFromSession(session);
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            cartService.updateCartItemQuantity(cartItemId, quantity);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật số lượng");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/cart/view";
    }
    
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("cartItemId") Integer cartItemId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Integer userId = getUserIdFromSession(session);
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            cartService.removeFromCart(cartItemId);
            redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/cart/view";
    }
    
    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = getUserIdFromSession(session);
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        try {
            cartService.clearCart(userId);
            redirectAttributes.addFlashAttribute("success", "Đã xóa toàn bộ giỏ hàng");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/cart/view";
    }
    
    @GetMapping("/count")
    @ResponseBody
    public String getCartCount(HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        
        if (userId == null) {
            return "{\"count\": 0}";
        }
        
        try {
            int count = cartService.getCartItemCount(userId);
            return "{\"count\": " + count + "}";
        } catch (Exception e) {
            return "{\"count\": 0}";
        }
    }
}
