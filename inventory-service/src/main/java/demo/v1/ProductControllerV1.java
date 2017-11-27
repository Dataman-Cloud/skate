package demo.v1;

import demo.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by Thinkpad on 2017/11/24 0024.
 */
@RestController
@RequestMapping("/v1")
public class ProductControllerV1 {

    private ProductServiceV1 productServiceV1;

    @Autowired
    private ProductControllerV1(ProductServiceV1 productServiceV1){
        this.productServiceV1 = productServiceV1;
    }

    @RequestMapping(path = "/products", method = RequestMethod.GET, name = "getProductAll")
    public ResponseEntity getProductAll() {
        return Optional.ofNullable(productServiceV1.getProductAll())
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/product")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product){
        return Optional.ofNullable(productServiceV1.updateProductByProductId(product))
                .map(result ->new ResponseEntity<>(result,HttpStatus.OK)).orElse(new ResponseEntity<Product>(HttpStatus.OK));

    }
}
