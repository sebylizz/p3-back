package dk.leghetto.classes;

import java.util.List;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private Long collectionId;
    private Long price;
    private List<ColorDTO> colors;
    private String mainImage;

    public ProductDTO() {
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
    public Long getPrice() {
        return price;
    }
    public void setPrice(Long price) {
        this.price = price;
    }
    public List<ColorDTO> getColors() {
        return colors;
    }
    public void setColors(List<ColorDTO> colors) {
        this.colors = colors;
    }
    public String getMainImage() {
        return mainImage;
    }
    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public static class ColorDTO {
        private Long id;
        private String name;
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
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
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

        public Integer getTotalSales(){
            return totalSales;
        }
        public void setTotalSales(Integer totalSales){
            this.totalSales=totalSales;
        }
        public List<VariantDTO> getVariants() {
            return variants;
        }
        public void setVariants(List<VariantDTO> variants) {
            this.variants = variants;
        }

        public static class VariantDTO {
            private Long id;
            private String size;
            private Long quantity;
            public VariantDTO() {
            }

            public Long getId() { return id; }
            public void setId(Long id) {
                this.id = id;
            }
            public String getSize() {
            return size;
            }
            public void setSize(String size) {
            this.size = size;
            }
            public Long getQuantity() {
            return quantity;
            }
            public void setQuantity(Long quantity) {
            this.quantity = quantity;
            }
        }
    }
}
