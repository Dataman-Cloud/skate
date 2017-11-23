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

    @RequestMapping(path = "/products/{productId}", method = RequestMethod.GET, name = "getProduct")
    public ResponseEntity<Product> getProduct(@PathVariable("productId") String productId) {
        return Optional.ofNullable(inventoryService.getProduct(productId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/inventory", method = RequestMethod.GET, name = "getAvailableInventoryForProductIds")
    public ResponseEntity getAvailableInventoryForProductIds(@RequestParam("productIds") String productIds) {
        return Optional.ofNullable(inventoryService.getAvailableInventoryForProductIds(productIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/modifyProductNum", method = RequestMethod.GET, name = "modifyProductNum")
    public ResponseEntity modifyProductNum(@RequestParam("productId") String productId,@RequestParam("productNum")
            long productNum){
        return Optional.ofNullable(inventoryService.modifyProductNum(productId,productNum))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 通过产品编号获取库存量
     * @param productId
     * @return
     */
    @RequestMapping(path = "/getInventoryNumByPid", method = RequestMethod.GET, name = "getInventoryNumByPid")
    public ResponseEntity getInventoryNumByPid(@RequestParam("productId") String productId){
        return Optional.ofNullable(inventoryService.getInventoryNumByPid(productId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
