package com.computershop.main.controllers;

import com.computershop.main.entities.User;
import com.computershop.main.entities.Product;
import com.computershop.main.entities.Category;
import com.computershop.main.entities.Order;
import com.computershop.main.services.UserService;
import com.computershop.main.services.ProductService;
import com.computershop.main.services.CategoryService;
import com.computershop.main.services.OrderService;
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
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private OrderService orderService;

    /**
     * Check if user is admin - used by all admin methods
     */
    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "admin".equals(role);
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            // Dashboard statistics
            model.addAttribute("totalUsers", userService.getTotalUsers());
            model.addAttribute("totalProducts", productService.getTotalProducts());
            model.addAttribute("totalCategories", categoryService.getAllCategories().size());
            model.addAttribute("totalOrders", orderService.getTotalOrders());
            model.addAttribute("totalRevenue", orderService.getTotalRevenue());
            
            // Recent activities
            model.addAttribute("recentOrders", orderService.getRecentOrders(10));
            model.addAttribute("lowStockProducts", productService.getLowStockProducts(10));
            model.addAttribute("recentUsers", userService.getRecentUsers(5));
            
            return "admin/dashboard"; // Will need to create this template
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "admin/dashboard";
        }
    }

    // === USER MANAGEMENT ===
    
    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "admin/users"; // Will need to create this template
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            model.addAttribute("users", List.of());
            return "admin/users";
        }
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") Integer userId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            userService.toggleUserStatus(userId);
            redirectAttributes.addFlashAttribute("success", "Trạng thái người dùng đã được cập nhật");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    // === CATEGORY MANAGEMENT ===
    
    @GetMapping("/categories")
    public String manageCategories(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            List<Category> categories = categoryService.getAllCategoriesOrderedByName();
            model.addAttribute("categories", categories);
            model.addAttribute("newCategory", new Category());
            return "admin/categories"; // Will need to create this template
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            model.addAttribute("categories", List.of());
            return "admin/categories";
        }
    }

    @PostMapping("/categories/create")
    public String createCategory(@ModelAttribute("newCategory") Category category,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Danh mục đã được tạo thành công");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/update")
    public String updateCategory(@PathVariable("id") Integer categoryId,
                               @ModelAttribute("category") Category category,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            categoryService.updateCategory(categoryId, category);
            redirectAttributes.addFlashAttribute("success", "Danh mục đã được cập nhật thành công");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable("id") Integer categoryId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            categoryService.deleteCategory(categoryId);
            redirectAttributes.addFlashAttribute("success", "Danh mục đã được xóa thành công");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/categories";
    }

    // === PRODUCT MANAGEMENT ===
    
    @GetMapping("/products")
    public String manageProducts(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            List<Product> products = productService.getAllProducts();
            List<Category> categories = categoryService.getAllCategoriesOrderedByName();
            
            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
            model.addAttribute("newProduct", new Product());
            
            return "admin/products"; // Will need to create this template
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            model.addAttribute("products", List.of());
            model.addAttribute("categories", List.of());
            return "admin/products";
        }
    }

    @PostMapping("/products/create")
    public String createProduct(@ModelAttribute("newProduct") Product product,
                              @RequestParam("categoryId") Integer categoryId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            // Validate required fields
            if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tên sản phẩm không được để trống");
                return "redirect:/admin/products";
            }
            
            if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "Giá sản phẩm phải lớn hơn 0");
                return "redirect:/admin/products";
            }
            
            // Set category
            Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
            if (categoryOpt.isPresent()) {
                product.setCategory(categoryOpt.get());
            }
            
            productService.createProduct(product);
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được tạo thành công");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editProductPage(@PathVariable("id") Integer productId,
                                HttpSession session,
                                Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            Optional<Product> productOpt = productService.getProductById(productId);
            if (productOpt.isEmpty()) {
                return "redirect:/admin/products";
            }
            
            List<Category> categories = categoryService.getAllCategoriesOrderedByName();
            
            model.addAttribute("product", productOpt.get());
            model.addAttribute("categories", categories);
            
            return "admin/edit-product"; // Will need to create this template
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/products/{id}/update")
    public String updateProduct(@PathVariable("id") Integer productId,
                              @ModelAttribute("product") Product product,
                              @RequestParam("categoryId") Integer categoryId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            // Set category
            Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
            if (categoryOpt.isPresent()) {
                product.setCategory(categoryOpt.get());
            }
            
            productService.updateProduct(productId, product);
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được cập nhật thành công");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable("id") Integer productId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            productService.deleteProduct(productId);
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được xóa thành công");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }

    // === ORDER MANAGEMENT ===
    
    @GetMapping("/orders")
    public String manageOrders(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            List<Order> orders = orderService.getAllOrders();
            model.addAttribute("orders", orders);
            return "admin/orders"; // Will need to create this template
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            model.addAttribute("orders", List.of());
            return "admin/orders";
        }
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable("id") Integer orderId,
                            HttpSession session,
                            Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            Optional<Order> orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isPresent()) {
                model.addAttribute("order", orderOpt.get());
                return "admin/order-detail"; // Will need to create this template
            } else {
                return "redirect:/admin/orders";
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/admin/orders";
        }
    }
}