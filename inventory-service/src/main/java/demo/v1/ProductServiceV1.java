package demo.v1;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import demo.inventory.Inventory;
import demo.product.Product;
import demo.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Thinkpad on 2017/11/24 0024.
 */
@Service
public class ProductServiceV1 {

    private ProductRepository productRepository;

    @Autowired
    public ProductServiceV1(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @HystrixCommand
    public Iterable<Product> getProductAll() {


        Iterable<Product> productList = productRepository.findAll();


        return productList;
    }



    @HystrixCommand
    public Product updateProductByProductId(Product product){

         productRepository.updateProductByProductId(product.getProductId(),product.getName(),
                 product.getUnitPrice(),product.getDescription());

        return null;
    }

    private Product getProductFallback(String productId) {
        return null;
    }

}
