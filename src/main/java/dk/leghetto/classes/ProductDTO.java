package dk.leghetto.classes;

import java.util.List;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private Long collectionId;
    private Long price;
    private String mainImage;
    private List<ProductColor> colors;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String name, String description, Long categoryId, Long collectionId, Long price, String mainImage, List<ProductColor> colors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.collectionId = collectionId;
        this.price = price;
        this.mainImage = mainImage;
        this.colors = colors;
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
    public String getMainImage() {
        return mainImage;
    }
    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }
    public List<ProductColor> getColors() {
        return colors;
    }
    public void setColors(List<ProductColor> colors) {
        this.colors = colors;
    }
}
