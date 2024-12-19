package breakable.toy.breakable_toy;

import java.time.LocalDate;

public class Product {
    private int id;
    private String name;
    private String category;
    private int stock;
    private float price;
    private LocalDate exp_date;
    private LocalDate creationDate;
    private LocalDate updDate;

    public Product(int id, String name, String category, int stock, float price, LocalDate exp_date,
            LocalDate creationDate, LocalDate upddDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.price = price;
        this.exp_date = exp_date;
        this.creationDate = creationDate;
        this.updDate = upddDate;
    }

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
        return exp_date;
    }

    public void setExpDate(LocalDate exp_date) {
        this.exp_date = exp_date;
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
