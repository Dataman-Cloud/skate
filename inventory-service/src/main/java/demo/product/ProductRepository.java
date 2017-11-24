package demo.product;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends GraphRepository<Product> {
    //Product getProductByProductId(@Param("productId") String productId);
    @Query("MATCH (product:Product) \n" +
            "\t WHERE product.id = {productId} \n" +
            "\t RETURN product")
    Product getProductByProductId(@Param("productId") String productId);
}
