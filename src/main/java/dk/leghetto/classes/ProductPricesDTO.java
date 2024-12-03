package dk.leghetto.classes;

import java.time.LocalDate;
import java.util.List;

public class ProductPricesDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private Long categoryId;
    private Long parentCategoryId;
    private Long collectionId;
    private List<ColorDTO> colors;
    private List<PriceDTO> prices; 

    public ProductPricesDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
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

    public List<PriceDTO> getPrices() {
        return prices;
    }

    public void setPrices(List<PriceDTO> prices) {
        this.prices = prices;
    }


    public static class ColorDTO {
        private Long id;
        private Long productId;
        private Long colorId;
        private String mainImage;
        private String images;
        private Integer totalSales;
        private List<VariantDTO> variants;

        public ColorDTO() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getProduct() {
            return productId;
        }

        public void setProduct(Long productId) {
            this.productId = productId;
        }

        public Long getColor() {
            return colorId;
        }

        public void setColor(Long colorId) {
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

        public List<VariantDTO> getVariants() {
            return variants;
        }

        public void setVariants(List<VariantDTO> variants) {
            this.variants = variants;
        }

        public static class VariantDTO {
            private Long id;
            private Colors colorId;
            private Long sizeId;
            private Long quantity;

            public VariantDTO() {
            }

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public Colors getColorId() {
                return colorId;
            }

            public void setColorId(Colors colorId) {
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

    public static class PriceDTO {
        private Long id;
        private Long price;
        private boolean isDiscount;
        private LocalDate startDate;
        private LocalDate endDate;

        public PriceDTO() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }

        public boolean isDiscount() {
            return isDiscount;
        }

        public void setDiscount(boolean discount) {
            isDiscount = discount;
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
}
