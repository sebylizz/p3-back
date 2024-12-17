package dk.leghetto.classes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dk.leghetto.classes.ProductDTO.ColorDTO;
import dk.leghetto.classes.ProductDTO.ColorDTO.VariantDTO;
import dk.leghetto.classes.ProductGetPostDTO.PriceDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    @Inject
    EntityManager entityManager;

    public List<ProductDTO> getAllActiveProducts() {
        List<Product> products = find("isActive = true").list();
        return products.stream()
                .map(product -> {
                    ProductDTO productDTO = new ProductDTO();
                    productDTO.setId(product.getId());
                    productDTO.setName(product.getName());
                    productDTO.setDescription(product.getDescription());
                    productDTO.setCategoryId(product.getCategory().getId());
                    productDTO.setCollectionId(product.getCollection().getId());
                    productDTO.setPrice(product.getPrice().getPrice());
                    productDTO.setColors(product.getColors().stream()
                            .map(color -> {
                                ColorDTO colorDTO = new ColorDTO();
                                colorDTO.setId(color.getId());
                                colorDTO.setName(color.getColor().getName());
                                colorDTO.setMainImage(color.getMainImage());
                                colorDTO.setImages(color.getImages());
                                colorDTO.setTotalSales(color.getTotalSales());
                                colorDTO.setVariants(color.getVariants().stream()
                                        .map(colorVariant -> {
                                            ColorDTO.VariantDTO variantDTO = new ColorDTO.VariantDTO();
                                            variantDTO.setId(colorVariant.getId());
                                            variantDTO.setSize(colorVariant.getSize().getName());
                                            variantDTO.setQuantity(colorVariant.getQuantity());
                                            return variantDTO;
                                        }).collect(Collectors.toList()));

                                return colorDTO;
                            }).collect(Collectors.toList()));
                    productDTO.setMainImage(product.getVariants().get(0).getColor().getMainImage());
                    return productDTO;
                }).collect(Collectors.toList());
    }

    public List<ProductAdminDTO> getAllProducts() {
        List<Product> products = Product.listAll();
        return products.stream()
                .map(product -> {
                    ProductAdminDTO productAdminDTO = new ProductAdminDTO();
                    productAdminDTO.setId(product.getId());
                    productAdminDTO.setName(product.getName());

                    if (product.getPrice() != null) {
                        productAdminDTO.setPrice(product.getPrice().getPrice());
                    }

                    productAdminDTO.setColors(product.getColors().stream()
                            .map(color -> {
                                ProductAdminDTO.ColorDTO colorDTO = new ProductAdminDTO.ColorDTO();
                                colorDTO.setId(color.getId());
                                colorDTO.setColorId(color.getColor());
                                colorDTO.setMainImage(color.getMainImage());
                                return colorDTO;
                            })
                            .limit(1)
                            .collect(Collectors.toList()));

                    return productAdminDTO;
                })
                .collect(Collectors.toList());
    }

    

    public ProductGetPostDTO getProductWithPricesById(Long productId) {

        Product product = find("id", productId).firstResult();

        if (product == null) {
            throw new NotFoundException("Product with id " + productId + " not found");
        }

        ProductGetPostDTO productDTO = new ProductGetPostDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setIsActive(product.getIsActive());

        Category category = product.getCategory();
        if (category != null) {
            productDTO.setCategoryId(category.getId());
            productDTO.setParentCategoryId(
                    category.getParentCategory() != null ? category.getParentCategory().getId() : null);
        }

        if (product.getCollection() != null) {
            productDTO.setCollectionId(product.getCollection().getId());
        }

        productDTO.setColors(product.getColors().stream()
                .map(color -> {
                    ProductGetPostDTO.ColorDTO colorDTO = new ProductGetPostDTO.ColorDTO();
                    colorDTO.setId(color.getId());
                    colorDTO.setProduct(product.getId());
                    colorDTO.setColor(color.getColor().getId());
                    colorDTO.setMainImage(color.getMainImage());
                    colorDTO.setImages(color.getImages());
                    colorDTO.setTotalSales(color.getTotalSales());

                    colorDTO.setVariants(color.getVariants().stream()
                            .map(variant -> {
                                ProductGetPostDTO.ColorDTO.VariantDTO variantDTO = new ProductGetPostDTO.ColorDTO.VariantDTO();
                                variantDTO.setId(variant.getId());
                                variantDTO.setColorId(color.getColor());
                                variantDTO.setSizeId(variant.getSize() != null ? variant.getSize().getId() : null);
                                variantDTO.setQuantity(variant.getQuantity());
                                return variantDTO;
                            }).collect(Collectors.toList()));

                    return colorDTO;
                }).collect(Collectors.toList()));

        productDTO.setPrices(product.getPrices().stream()
                .map(price -> {
                    ProductGetPostDTO.PriceDTO priceDTO = new ProductGetPostDTO.PriceDTO();
                    priceDTO.setId(price.getId());
                    priceDTO.setPrice(price.getPrice());
                    priceDTO.setDiscount(price.getIsDiscount());
                    priceDTO.setStartDate(price.getStartDate());
                    priceDTO.setEndDate(price.getEndDate());
                    return priceDTO;
                }).collect(Collectors.toList()));

        return productDTO;
    }
    @Transactional
    public List<Map<String, Long>> addOrUpdateProduct(Long productId, ProductGetPostDTO request) throws Exception {
        Product product = (productId != null) ? Product.findById(productId) : new Product();

        if (productId != null && product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }

        Category category = Category.findById(request.getCategoryId());
        if (category == null) {
            throw new IllegalArgumentException("Invalid category ID: " + request.getCategoryId());
        }

        Collection collection = Collection.findById(request.getCollectionId());
        if (collection == null) {
            throw new IllegalArgumentException("Invalid collection ID: " + request.getCollectionId());
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setIsActive(request.getIsActive());
        product.setCategory(category);
        product.setCollection(collection);

        if (productId == null) {
            product.persist();
        }

        validateAndUpdatePrices(product, request.getPrices());
        List<Map<String, Long>> newColorMapping = updateColorsAndVariants(product, request.getColors());

        product.persist();

        return newColorMapping;
    }


    public void updateBasicDetails(Product product, ProductGetPostDTO productDTO) {
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setIsActive(productDTO.getIsActive());
        product.setCategory(Category.findById(productDTO.getCategoryId()));
        product.setCollection(Collection.findById(productDTO.getCollectionId()));
    }

    public void validateAndUpdatePrices(Product product, List<ProductGetPostDTO.PriceDTO> priceDTOs) {
        List<ProductPrice> existingPrices = product.getPrices();
        List<ProductPrice> pricesToDelete = new ArrayList<>();
        List<ProductGetPostDTO.PriceDTO> pricesToAdd = new ArrayList<>();

        Set<Long> dtoPriceIds = priceDTOs.stream()
                .map(ProductGetPostDTO.PriceDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (ProductPrice existingPrice : existingPrices) {
            if (!dtoPriceIds.contains(existingPrice.getId())) {
                if (!existingPrice.getIsDiscount()) {
                    boolean isCurrentNonDiscount = existingPrices.stream()
                            .filter(price -> !price.getIsDiscount())
                            .anyMatch(price -> price.getStartDate().isBefore(LocalDate.now().plusDays(1)) &&
                                    (price.getEndDate() == null || price.getEndDate().isAfter(LocalDate.now())) &&
                                    price.equals(existingPrice));

                    if (isCurrentNonDiscount) {
                        throw new IllegalArgumentException("Cannot delete the current active non-discount price.");
                    }
                }
                pricesToDelete.add(existingPrice);
            } else {
                ProductGetPostDTO.PriceDTO matchingDTO = priceDTOs.stream()
                        .filter(dto -> existingPrice.getId().equals(dto.getId()))
                        .findFirst()
                        .orElse(null);

                if (matchingDTO != null) {
                    validatePriceDates(matchingDTO.getStartDate(), matchingDTO.getEndDate());
                    existingPrice.setPrice(matchingDTO.getPrice());
                    existingPrice.setIsDiscount(matchingDTO.isDiscount());
                    existingPrice.setStartDate(matchingDTO.getStartDate());
                    existingPrice.setEndDate(matchingDTO.getEndDate());
                }
            }
        }

        for (ProductGetPostDTO.PriceDTO priceDTO : priceDTOs) {
            if (priceDTO.getId() == null) {
                pricesToAdd.add(priceDTO);
            }
        }

        for (ProductGetPostDTO.PriceDTO priceDTO : pricesToAdd) {
            validatePriceDates(priceDTO.getStartDate(), priceDTO.getEndDate());
            if (!priceDTO.isDiscount()) {
                handleNonDiscountPrice(existingPrices, priceDTO);
            }
            addNewPrice(product, priceDTO);
        }

        deletePrices(product, pricesToDelete);
    }

    private void validatePriceDates(LocalDate startDate, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date.");
        }
    }

    private void handleNonDiscountPrice(List<ProductPrice> existingPrices, ProductGetPostDTO.PriceDTO newPrice) {
        for (ProductPrice price : existingPrices) {
            if (!price.getIsDiscount() && price.getEndDate() == null) {
                price.setEndDate(newPrice.getStartDate().minusDays(1));
            }
        }
    }

    private void addNewPrice(Product product, ProductGetPostDTO.PriceDTO priceDTO) {
        ProductPrice newPrice = new ProductPrice();
        newPrice.setPrice(priceDTO.getPrice());
        newPrice.setIsDiscount(priceDTO.isDiscount());
        newPrice.setStartDate(priceDTO.getStartDate());
        newPrice.setEndDate(priceDTO.getEndDate());
        newPrice.setProduct(product);
        newPrice.persist();
        product.getPrices().add(newPrice);
    }

    private void deletePrices(Product product, List<ProductPrice> pricesToDelete) {
        for (ProductPrice price : pricesToDelete) {
            product.getPrices().remove(price);
            price.delete();
        }
    }

    public List<Map<String, Long>> updateColorsAndVariants(Product product,
            List<ProductGetPostDTO.ColorDTO> colorDTOs) {
        List<ProductColor> existingColors = product.getColors();
        List<ProductColor> colorsToDelete = new ArrayList<>();
        List<ProductColor> colorsToAdd = new ArrayList<>();
        List<Map<String, Long>> newColorMapping = new ArrayList<>();

        Set<Long> dtoColorIds = colorDTOs.stream()
                .map(ProductGetPostDTO.ColorDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (ProductColor existingColor : existingColors) {
            if (!dtoColorIds.contains(existingColor.getId())) {
                colorsToDelete.add(existingColor);
            } else {
                updateExistingColor(existingColor, colorDTOs);
            }
        }

        for (ProductGetPostDTO.ColorDTO colorDTO : colorDTOs) {
            if (colorDTO.getId() == null) {
                addNewColor(product, colorDTO, colorsToAdd);
            }
        }

        for (ProductColor colorToDelete : colorsToDelete) {
            colorToDelete.delete();
        }
        for (ProductColor colorToAdd : colorsToAdd) {
            colorToAdd.persist();
            Map<String, Long> colorMap = new HashMap<>();
            colorMap.put("colorId", colorToAdd.getColor().getId());
            colorMap.put("id", colorToAdd.getId());
            newColorMapping.add(colorMap);
        }
        return newColorMapping;
    }

    private void updateExistingColor(ProductColor color, List<ProductGetPostDTO.ColorDTO> colorDTOs) {
        ProductGetPostDTO.ColorDTO matchingDTO = colorDTOs.stream()
                .filter(dto -> dto.getId().equals(color.getId()))
                .findFirst()
                .orElse(null);

        if (matchingDTO != null) {
            color.setMainImage(matchingDTO.getMainImage());
            color.setImages(matchingDTO.getImages());
            color.setTotalSales(matchingDTO.getTotalSales());

            updateVariants(color, matchingDTO.getVariants());
        }
    }

    private void updateVariants(ProductColor color, List<ProductGetPostDTO.ColorDTO.VariantDTO> variantDTOs) {
        List<ProductVariant> variantsToDelete = new ArrayList<>();
        List<ProductVariant> variantsToAdd = new ArrayList<>();
        Set<Long> dtoVariantIds = variantDTOs.stream()
                .map(ProductGetPostDTO.ColorDTO.VariantDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (ProductVariant existingVariant : color.getVariants()) {
            if (!dtoVariantIds.contains(existingVariant.getId())) {
                variantsToDelete.add(existingVariant);
            } else {
                updateExistingVariant(existingVariant, variantDTOs);
            }
        }

        for (ProductGetPostDTO.ColorDTO.VariantDTO variantDTO : variantDTOs) {
            if (variantDTO.getId() == null) {
                addNewVariant(color, variantDTO, variantsToAdd);
            }
        }

        for (ProductVariant variantToDelete : variantsToDelete) {
            variantToDelete.delete();
        }
        for (ProductVariant variantToAdd : variantsToAdd) {
            variantToAdd.persist();
        }
    }

    private void updateExistingVariant(ProductVariant variant,
            List<ProductGetPostDTO.ColorDTO.VariantDTO> variantDTOs) {
        ProductGetPostDTO.ColorDTO.VariantDTO matchingDTO = variantDTOs.stream()
                .filter(dto -> dto.getId().equals(variant.getId()))
                .findFirst()
                .orElse(null);

        if (matchingDTO != null) {
            variant.setSize(ProductSize.findById(matchingDTO.getSizeId()));
            variant.setQuantity(matchingDTO.getQuantity());
        }
    }

    private void addNewColor(Product product, ProductGetPostDTO.ColorDTO colorDTO, List<ProductColor> colorsToAdd) {
        ProductColor newColor = new ProductColor();
        newColor.setMainImage(colorDTO.getMainImage());
        newColor.setImages(colorDTO.getImages());
        newColor.setTotalSales(colorDTO.getTotalSales());
        newColor.setProduct(product);

        Colors colorEntity = Colors.findById(colorDTO.getColor());
        if (colorEntity == null) {
            throw new IllegalArgumentException("Invalid color ID: " + colorDTO.getColor());
        }
        newColor.setColor(colorEntity);
        newColor.persist();

        if (colorDTO.getVariants() != null) {
            for (ProductGetPostDTO.ColorDTO.VariantDTO variantDTO : colorDTO.getVariants()) {
                ProductVariant newVariant = new ProductVariant();
                newVariant.setSize(ProductSize.findById(variantDTO.getSizeId()));
                newVariant.setQuantity(variantDTO.getQuantity());
                newVariant.setColor(newColor);
                newVariant.setProduct(product);
                newVariant.persist();
            }
        }

        colorsToAdd.add(newColor);
    }

    private void addNewVariant(ProductColor color, ProductGetPostDTO.ColorDTO.VariantDTO variantDTO,
            List<ProductVariant> variantsToAdd) {
        if (variantsToAdd == null) {
            variantsToAdd = new ArrayList<>();
        }

        ProductVariant newVariant = new ProductVariant();
        newVariant.setSize(ProductSize.findById(variantDTO.getSizeId()));
        newVariant.setQuantity(variantDTO.getQuantity());
        newVariant.setColor(color);
        newVariant.setProduct(color.getProduct());
        variantsToAdd.add(newVariant);
    }

}
