package dk.leghetto.classes;

import java.time.LocalDate;
import java.util.List;

public class ProductRequestDTO {
    private String name;
    private String description;
    private Boolean isActive;
    private Long categoryId; // Foreign key to categories
    private Long collectionId; // Foreign key to collections

    private PriceDTO price; // Price details
    private List<ColorDTO> colors; // Associated colors
    private List<VariantDTO> variants; // Associated variants

    // Getters and setters

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public Long getCollectionId() {
        return collectionId;
    }
    
    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }
    
    public PriceDTO getPrice() {
        return price;
    }
    
    public void setPrice(PriceDTO price) {
        this.price = price;
    }
    
    public List<ColorDTO> getColors() {
        return colors;
    }
    
    public void setColors(List<ColorDTO> colors) {
        this.colors = colors;
    }
    
    public List<VariantDTO> getVariants() {
        return variants;
    }
    
    public void setVariants(List<VariantDTO> variants) {
        this.variants = variants;
    }
    

    public static class PriceDTO {
        private Long price;
        private Boolean isDiscount;
        private LocalDate startDate; 
        private LocalDate endDate;

        // Getters and setters
        public Long getPrice() {
            return price;
        }
        
        public void setPrice(Long price) {
            this.price = price;
        }
        
        public Boolean getIsDiscount() {
            return isDiscount;
        }
        
        public void setIsDiscount(Boolean isDiscount) {
            this.isDiscount = isDiscount;
        }
        
        public LocalDate getStartDate() {
            return startDate;
        }
        
        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }
        
        public LocalDate getEndDate() {
            return endDate;
        }
        
        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }
        
    }

    public static class ColorDTO {
        private Long colorId; // Foreign key to colors table
        private String mainImage; // Main image URL
        private String images; // Additional image URLs
        private Integer totalSales; // Total sales for this color

        // Getters and setters
        public Long getColorId() {
            return colorId;
        }
        
        public void setColorId(Long colorId) {
            this.colorId = colorId;
        }
        
        public String getMainImage() {
            return mainImage;
        }
        
        public void setMainImage(String mainImage) {
            this.mainImage = mainImage;
        }
        
        public String getImages() {
            return images;
        }
        
        public void setImages(String images) {
            this.images = images;
        }
        
        public Integer getTotalSales() {
            return totalSales;
        }
        
        public void setTotalSales(Integer totalSales) {
            this.totalSales = totalSales;
        }
        
    }

    public static class VariantDTO {
        private Long colorId; // Links to product_colors
        private Long sizeId; // Foreign key to sizes table
        private Long quantity; // Stock quantity
        // Getters and setters

        public Long getColorId() {
            return colorId;
        }
        
        public void setColorId(Long colorId) {
            this.colorId = colorId;
        }
        
        public Long getSizeId() {
            return sizeId;
        }
        
        public void setSizeId(Long sizeId) {
            this.sizeId = sizeId;
        }
        
        public Long getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Long quantity) {
            this.quantity = quantity;
        }
        
    }
}
