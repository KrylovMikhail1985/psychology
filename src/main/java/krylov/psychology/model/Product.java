package krylov.psychology.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "product_name")
    @NotBlank
    private String productName;

    @Column(name = "cost")
    @Min(500)
    @Max(9999)
    private int cost;

    @Column(name = "duration")
    private String duration;
    @Column(name = "description")
    @NotBlank
    private String description;
    @Column(name = "actual")
    private boolean actual;
    @Column(name = "priority")
    @Min(1)
    @Max(999)
    private int priority;

    @OneToMany(mappedBy = "product")
    private List<Therapy> therapies;

    public Product() {
    }

    public Product(String productName, int cost, String duration, String description, boolean actual, int priority) {
        this.productName = productName;
        this.cost = cost;
        this.duration = duration;
        this.description = description;
        this.actual = actual;
        this.priority = priority;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public List<Therapy> getTherapies() {
        return therapies;
    }

    public void setTherapies(List<Therapy> therapies) {
        this.therapies = therapies;
    }

    public boolean isActual() {
        return actual;
    }

    public void setActual(boolean actual) {
        this.actual = actual;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
