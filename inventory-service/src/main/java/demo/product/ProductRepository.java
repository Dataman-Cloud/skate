package demo.product;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends GraphRepository<Product> {
    @Query("MATCH (product:Product) \n" +
            "\t WHERE product.productId = {productId} \n" +
            "\t RETURN product")
    Product getProductByProductId(@Param("productId") String productId);

    @Query("MATCH (product:Product)\n"+
            "\t WHERE product.productId = {productId}\n"+
             "\t set product.name = {name} ,product.description = {description},product.unitPrice = {unitPrice}")
    Product updateProductByProductId(@Param("productId") String productId,@Param("name")String name,
                                     @Param("unitPrice") Double unitPrice,@Param("description")String description);
}
