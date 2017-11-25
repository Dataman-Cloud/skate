package demo.v1;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import demo.inventory.Inventory;
import demo.product.Product;
import demo.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Thinkpad on 2017/11/24 0024.
 */
@Service
public class ProductServiceV1 {
    private ProductRepository productRepository;

    private ProductServiceV1(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @HystrixCommand(fallbackMethod = "getProductFallback")
    public Iterable<Product> getProductAll() {


        Iterable<Product> productList = productRepository.findAll();
/*
        if (product != null) {
            Stream<Inventory> availableInventory = inventoryRepository.getAvailableInventoryForProduct(productId).stream();
            product.setInStock(availableInventory.findAny().isPresent());
        }*/

        return productList;
    }

    private Product getProductFallback(String productId) {
        return null;
    }

}
