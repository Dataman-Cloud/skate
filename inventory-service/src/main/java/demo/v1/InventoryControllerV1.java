package demo.v1;

import demo.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1")
public class InventoryControllerV1 {

    private InventoryServiceV1 inventoryService;

    @Autowired
    public InventoryControllerV1(InventoryServiceV1 inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RequestMapping(path = "/products/{productId}", method = RequestMethod.GET, name = "getProductByProductId")
    public ResponseEntity<Product> getProductByProductId(@PathVariable("productId") String productId) {
        return Optional.ofNullable(inventoryService.getProductByProductId(productId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/inventory", method = RequestMethod.GET, name = "getAvailableInventoryForProductIds")
    public ResponseEntity getAvailableInventoryForProductIds(@RequestParam("productIds") String productIds) {
        return Optional.ofNullable(inventoryService.getAvailableInventoryForProductIds(productIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 通过产品编号修改库存量
     */
    @RequestMapping("/modifyInventoryNum/{productId}/{productNum}")
    public ResponseEntity modifyInventoryNum(@PathVariable("productId") String productId, @PathVariable("productNum")
            Long productNum) {
        return Optional.ofNullable(inventoryService.modifyProductNum(productId, productNum))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 通过产品编号获取库存量
     */
    @RequestMapping(path = "/getInventoryNumByPid/{productId}", method = RequestMethod.GET, name = "getInventoryNumByPid")
    public ResponseEntity getInventoryNumByPid(@PathVariable("productId") String productId) {
        return Optional.ofNullable(inventoryService.getInventoryNumByPid(productId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}
