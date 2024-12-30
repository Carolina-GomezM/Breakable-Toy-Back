package breakable.toy.breakable_toy;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Product {
    private int id;
    private String name;
    private String category;
    private int stock;
    private float price;
    private LocalDate expDate;
    private LocalDate creationDate;
    private LocalDate updDate;
/* 
    public Product(int id, String name, String category, int stock, float price, LocalDate expDate,
            LocalDate creationDate, LocalDate upddDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.stock = stock;
/*         this.price = price;
        this.expDate = expDate;
        this.creationDate = creationDate;
        this.updDate = upddDate;
    } */

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public LocalDate getExpDate() {
        return expDate;
    }

    public void setExpDate(LocalDate expDate) {
        this.expDate = expDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getupdDate() {
        return updDate;
    }

    public void setupdDate(LocalDate updDate) {
        this.updDate = updDate;
    } 
} 
