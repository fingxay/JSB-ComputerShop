package com.computershop.main.controllers;

import com.computershop.main.entities.Product;
import com.computershop.main.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        try {
            // Get featured products (limit to 6 for homepage)
            List<Product> featuredProducts = productService.getFeaturedProducts(6);
            model.addAttribute("products", featuredProducts);
            
            // Add other homepage data
            model.addAttribute("totalProducts", productService.getTotalProducts());
            model.addAttribute("categories", productService.getAllCategoryNames());
            
        } catch (Exception e) {
            // Fallback to empty list if service fails
            model.addAttribute("products", List.of());
            model.addAttribute("totalProducts", 0L);
            model.addAttribute("categories", List.of());
        }
        
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
