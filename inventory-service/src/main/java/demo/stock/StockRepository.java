package demo.stock;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockRepository extends GraphRepository<Stock> {

    @Query("MATCH (stock:Stock) \n" +
            "\t WHERE stock.sync=false \n" +
            "\t RETURN stock")
    List<Stock> getStockNoSync();

    @Query("MATCH (product:Product), \n" +
            "\t (product)<-[:PRODUCT_STOCK]-(stock:Stock) WHERE stock.sync=false AND product.productId = {productId} \n" +
            "\t set stock.sync=true \n" +
            "\t RETURN stock")
    Stock modifyProductState(@Param("productId") String productId);

    @Query("MATCH (stock:Stock),(product:Product) \n" +
            "\t (product)<-[:PRODUCT_STOCK]-(stock:Stock) WHERE stock.sync=false AND product.productId = {productId} \n" +
            "\t set stock.sync={state} \n" +
            "\t RETURN stock")
    Stock modifyProductState(@Param("productId") String productId, @Param("state") Boolean state);

}
