package com.computershop.main.controllers;

import com.computershop.main.entities.User;
import com.computershop.main.entities.Role;
import com.computershop.main.services.UserService;
import com.computershop.main.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
        }
        if (logout != null) {
            model.addAttribute("message", "Bạn đã đăng xuất thành công.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                              @RequestParam("confirmPassword") String confirmPassword,
                              @RequestParam(value = "agree", required = false) String agree,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        // Validation
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            model.addAttribute("error", "Tên đăng nhập không được để trống.");
            return "register";
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "Email không được để trống.");
            return "register";
        }
        
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
            return "register";
        }
        
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "register";
        }
        
        if (agree == null) {
            model.addAttribute("error", "Bạn phải đồng ý với điều khoản sử dụng.");
            return "register";
        }

        try {
            // Check if username already exists
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                model.addAttribute("error", "Tên đăng nhập đã tồn tại.");
                return "register";
            }
            
            // Check if email already exists
            if (userService.findByEmail(user.getEmail()).isPresent()) {
                model.addAttribute("error", "Email đã được sử dụng.");
                return "register";
            }
            
            // Set default role (USER)
            Optional<Role> userRole = roleService.findByRoleName("USER");
            if (userRole.isPresent()) {
                user.setRole(userRole.get());
            } else {
                // Create USER role if not exists
                Role newUserRole = new Role();
                newUserRole.setRoleId(2); // Assuming 1=ADMIN, 2=USER
                newUserRole.setRoleName("USER");
                roleService.createRole(newUserRole);
                user.setRole(newUserRole);
            }
            
            // Register user
            userService.registerUser(user);
            
            redirectAttributes.addFlashAttribute("success", 
                "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "register";
        }
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "remember-me", required = false) String rememberMe,
                           HttpServletRequest request,
                           Model model) {
        
        try {
            // Validate credentials
            Optional<User> userOpt = userService.validateLogin(username, password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole().getRoleName());
                
                // Redirect based on role
                String roleName = user.getRole().getRoleName();
                if ("admin".equals(roleName)) {
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/";
                }
            } else {
                model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
                return "login";
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi đăng nhập: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("message", "Bạn đã đăng xuất thành công.");
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password"; // Will need to create this template
    }
}