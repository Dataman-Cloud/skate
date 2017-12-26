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


    @Query("MATCH (stock:Stock),(product:Product), \n" +
            "\t (product)<-[:PRODUCT_STOCK]-(stock:Stock) WHERE product.productId = {productId} \n" +
            "\t RETURN stock")
    Stock getStockByProductId(@Param("productId") String productId);


    @Query("MATCH (product:Product), \n" +
            "\t (product)<-[:PRODUCT_STOCK]-(stock:Stock)\n" +
            "\t RETURN stock")
    Iterable<Stock> getProductRelateStock();

    @Query("MATCH (stock:Stock),(product:Product), \n" +
            "\t (product)<-[:PRODUCT_STOCK]-(stock:Stock)\n" +
            "\t where product.productId = {productId} set stock.number = {number} ,stock.sync= false  RETURN stock")
    Stock updateStockByProductId(@Param("productId")String productId,
                              @Param("number")Integer number);

    @Query("MATCH (product:Product), \n" +
            "\t (product)<-[r:PRODUCT_STOCK]-(stock:Stock)\n" +
            "\t where ID(stock) = {stockId} delete product,r,stock")
    void deleteSRelationP(@Param("stockId")String stockId);

}
