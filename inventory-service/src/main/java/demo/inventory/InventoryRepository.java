package demo.inventory;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryRepository extends GraphRepository<Inventory> {
    @Query("MATCH (product:Product),\n" +
            "\t(product)<-[:PRODUCT_TYPE]-(inventory:Inventory)\n" +
            "WHERE product.productId = {productId} AND NOT (inventory)<-[:CONTAINS_PRODUCT]-()\n" +
            "RETURN inventory")
    List<Inventory> getAvailableInventoryForProduct(@Param("productId") String productId);

    @Query("MATCH (product:Product),\n" +
            "\t(product)<-[:PRODUCT_TYPE]-(inventory:Inventory),\n" +
            "    (inventory)-[:STOCKED_IN]->(:Warehouse { name: \"{warehouseName}\" })\n" +
            "WHERE product.productId = {productId} AND NOT (inventory)<-[:CONTAINS_PRODUCT]-()\n" +
            "RETURN inventory")
    List<Inventory> getAvailableInventoryForProductAndWarehouse(@Param("productId") String productId,
                                                                @Param("warehouseName") String warehouseName);

    @Query("MATCH (product:Product),\n" +
            "\t(product)<-[:PRODUCT_TYPE]-(inventory:Inventory)\n" +
            "WHERE product.productId in {productIds} AND NOT (inventory)<-[:CONTAINS_PRODUCT]-()\n" +
            "RETURN inventory")
    List<Inventory> getAvailableInventoryForProductList(@Param("productIds") String[] productIds);

    @Query("MATCH (inventory:Inventory),(product:Product) \n" +
            "\t product.id={productId} \n" +
            "\t set inventory.inventoryNumber={productNum} \n" +
            "\t RETURN inventory")
    Inventory modifyProductNum(@Param("productId") String productId,@Param("productNum") long productNum);

    @Query("MATCH (inventory:Inventory),(product:Product) \n" +
            "\t WHERE product.id={productId} \n" +
            "\t RETURN COUNT(inventory)")
    Long getInventoryNumByPid(@Param("productId") String productId);
}
