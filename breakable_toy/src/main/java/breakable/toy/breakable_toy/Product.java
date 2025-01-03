package breakable.toy.breakable_toy;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Product {
    private int ID;
    private String name;
    private String category;
    private int stock;
    private float price;
    private LocalDate expDate;
    private LocalDate creationDate;
    private LocalDate updDate;

} 
