package demo.v1;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.product.Product;
import demo.product.ProductRepository;

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
    public Iterable<Product> updateProductByProductId(Product product){
        product.setName(product.getName()==null?"":product.getName());
        product.setDescription(product.getDescription()==null?"":product.getDescription());
        product.setUnitPrice(product.getUnitPrice()==null?0:product.getUnitPrice());

      return   productRepository.updateProductByProductId(product.getProductId(),product.getName(),
                 product.getUnitPrice(),product.getDescription());


    }

    private Product getProductFallback(String productId) {
        return null;
    }

}
