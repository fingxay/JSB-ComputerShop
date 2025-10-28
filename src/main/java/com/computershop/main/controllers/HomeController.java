package com.computershop.main.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model){
        List<ProductDto> products = new ArrayList<>();
    products.add(new ProductDto(1L, "Mechanical Keyboard - Red Switch", "KeyMaster", 1200000.0, "/Images/keyboard.svg"));
    products.add(new ProductDto(2L, "Wireless Gaming Mouse", "ProMouse", 450000.0, "/Images/mouse.svg"));
    products.add(new ProductDto(3L, "Gaming Headset", "SoundCore", 650000.0, "/Images/headset.svg"));

        model.addAttribute("products", products);
        return "home"; // resolves to src/main/resources/templates/home.html
    }

    // Simple DTO inside controller file to avoid touching entities for demo purpose
    public static class ProductDto{
        private Long id;
        private String name;
        private String brand;
        private Double price;
        private String imageUrl;

        public ProductDto(Long id, String name, String brand, Double price, String imageUrl) {
            this.id = id;
            this.name = name;
            this.brand = brand;
            this.price = price;
            this.imageUrl = imageUrl;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getBrand() { return brand; }
        public Double getPrice() { return price; }
        public String getImageUrl() { return imageUrl; }

        public void setId(Long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setBrand(String brand) { this.brand = brand; }
        public void setPrice(Double price) { this.price = price; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
