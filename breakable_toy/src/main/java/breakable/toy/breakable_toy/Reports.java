package breakable.toy.breakable_toy;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Reports {
    private String category;
    private Long totalProductsInStock;
    private double totalValueInStock;
    private double averagePriceInStock;

}
