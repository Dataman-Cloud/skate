package demo.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import demo.stock.Stock;

/**
 * 进货控制器
 */
@RestController
@RequestMapping("/v1/stock")
public class StockControllerV1 {

    private Logger log = LoggerFactory.getLogger(StockControllerV1.class);

    @Autowired
    private StockServiceV1 stockService;

    /**
     * 获取未同步的货品
     */
    @RequestMapping("/getStockNoSync")
    public ResponseEntity getStockNoSync() {
        return Optional.ofNullable(stockService.getStockNoSync())
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 同步产品状态
     */
    @RequestMapping("/modifyProductState/{productId}")
    public ResponseEntity modifyProductState(@PathVariable("productId") String productId) {
        return Optional.ofNullable(stockService.modifyProductState(productId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 根据产品Id获取产品库存
     * @return
     */
    @RequestMapping(path = "/{productId}", method = RequestMethod.GET, name = "getStockByProductId")
    public ResponseEntity<Stock> getStockByProductId(@PathVariable("productId") String productId) {
        return Optional.ofNullable(stockService.getStockByProductId(productId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 获取产品库存列表
     * @return
     */
    @RequestMapping(path = "/stock", method = RequestMethod.GET, name = "getStockAll")
    public ResponseEntity getStockAll(){
        return Optional.ofNullable(stockService.getStockALL())
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 添加到库存
     * @return
     */
    @RequestMapping(path = "/updateStock", method = RequestMethod.GET, name = "updateStock")
    public ResponseEntity updateStock(@RequestBody Stock stock){
        return Optional.ofNullable(stockService.updateStock(stock))
                .map(result -> new ResponseEntity<>(result,HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
