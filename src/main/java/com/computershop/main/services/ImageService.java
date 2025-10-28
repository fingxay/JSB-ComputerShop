package com.computershop.main.services;

import com.computershop.main.entities.Image;
import com.computershop.main.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    
    @Autowired
    private ImageRepository imageRepository;
    
    /**
     * Get all images
     */
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }
    
    /**
     * Get image by ID
     */
    public Optional<Image> getImageById(Integer imageId) {
        return imageRepository.findById(imageId);
    }
    
    /**
     * Get image by URL
     */
    public Optional<Image> getImageByUrl(String imageUrl) {
        return imageRepository.findByImageUrl(imageUrl);
    }
    
    /**
     * Search images by URL containing keyword
     */
    public List<Image> searchImagesByUrl(String keyword) {
        return imageRepository.findByImageUrlContaining(keyword);
    }
    
    /**
     * Find images by URL pattern
     */
    public List<Image> findImagesByPattern(String pattern) {
        return imageRepository.findByImageUrlPattern(pattern);
    }
    
    /**
     * Create new image
     */
    public Image createImage(Image image) {
        if (imageRepository.existsByImageUrl(image.getImageUrl())) {
            throw new RuntimeException("Image with URL '" + image.getImageUrl() + "' already exists");
        }
        return imageRepository.save(image);
    }
    
    /**
     * Create image by URL
     */
    public Image createImageByUrl(String imageUrl) {
        if (imageRepository.existsByImageUrl(imageUrl)) {
            // Return existing image instead of throwing error
            return imageRepository.findByImageUrl(imageUrl).orElse(null);
        }
        
        Image image = new Image();
        image.setImageUrl(imageUrl);
        return imageRepository.save(image);
    }
    
    /**
     * Update image
     */
    public Image updateImage(Integer imageId, Image imageDetails) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
        
        // Check URL uniqueness (excluding current image)
        if (!image.getImageUrl().equals(imageDetails.getImageUrl()) && 
            imageRepository.existsByImageUrl(imageDetails.getImageUrl())) {
            throw new RuntimeException("Image with URL '" + imageDetails.getImageUrl() + "' already exists");
        }
        
        image.setImageUrl(imageDetails.getImageUrl());
        return imageRepository.save(image);
    }
    
    /**
     * Delete image
     */
    public void deleteImage(Integer imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new RuntimeException("Image not found with id: " + imageId);
        }
        imageRepository.deleteById(imageId);
    }
    
    /**
     * Check if image exists by URL
     */
    public boolean existsByImageUrl(String imageUrl) {
        return imageRepository.existsByImageUrl(imageUrl);
    }
    
    /**
     * Get or create image by URL
     */
    public Image getOrCreateImageByUrl(String imageUrl) {
        return imageRepository.findByImageUrl(imageUrl)
                .orElseGet(() -> createImageByUrl(imageUrl));
    }
    
    /**
     * Validate image URL format
     */
    public boolean isValidImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return false;
        }
        
        // Basic URL validation - can be enhanced
        return imageUrl.startsWith("http://") || 
               imageUrl.startsWith("https://") || 
               imageUrl.startsWith("/") || 
               imageUrl.startsWith("data:image/");
    }
    
    /**
     * Get default placeholder image
     */
    public Image getPlaceholderImage() {
        return getOrCreateImageByUrl("/Images/placeholder.svg");
    }
}