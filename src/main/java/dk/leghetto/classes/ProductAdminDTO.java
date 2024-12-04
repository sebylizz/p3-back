package dk.leghetto.classes;

import java.time.LocalDate;
import java.util.List;

public class ProductAdminDTO {
    private Long id;
    private String name;
    private Long price;
    private List<ColorDTO> colors;

    public ProductAdminDTO() {
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

    public static class ColorDTO {
        private Long id;
        private Colors colorId;
        private String mainImage;

        public ColorDTO() {
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

        public String getMainImage() {
            return mainImage;
        }

        public void setMainImage(String mainImage) {
            this.mainImage = mainImage;
        }
    }

}
