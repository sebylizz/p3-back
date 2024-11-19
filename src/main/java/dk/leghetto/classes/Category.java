package dk.leghetto.classes;

import java.util.List;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "category", schema = "dev")
public class Category extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getParentCategory() { return parentCategory; }
    public void setParentCategory(Category parentCategory) { this.parentCategory = parentCategory; }

    
}
