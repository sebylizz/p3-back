package dk.leghetto.classes;

import java.time.LocalDate;
import java.util.List;

public class ProductRequestDTO {

    private String name;
    private String description;
    private Boolean isActive;
    private Long categoryId;
    private Long collectionId;

    private List<ColorDTO> colors;
    private PriceDTO price;
    private List<VariantDTO> variants;

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

    public List<ColorDTO> getColors() {
        return colors;
    }

    public void setColors(List<ColorDTO> colors) {
        this.colors = colors;
    }

    public PriceDTO getPrice() {
        return price;
    }

    public void setPrice(PriceDTO price) {
        this.price = price;
    }

    public List<VariantDTO> getVariants() {
        return variants;
    }

    public void setVariants(List<VariantDTO> variants) {
        this.variants = variants;
    }



    public static class ColorDTO {
        private Long colorId;
        private String mainImage;
        private String images;

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
    }

    public static class PriceDTO {
        private Double price;
        private Boolean isDiscount;
        private LocalDate startDate;
        private LocalDate endDate;


        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
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

    public static class VariantDTO {
        private Long colorId;
        private Long sizeId;
        private Integer quantity;


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

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
