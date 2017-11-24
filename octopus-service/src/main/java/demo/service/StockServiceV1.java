package demo.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import demo.inventory.Inventory;
import demo.stock.Stock;

@Service
public class StockServiceV1 {

    private RestTemplate restTemplate;

    @Autowired
    public StockServiceV1(@LoadBalanced RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 获取未同步的商品
     */
    @HystrixCommand
    public List<Map<String, Long>> getStockNoSync() {

        List<Stock> stocks = (List<Stock>) restTemplate
                .getForObject(String.format("http://inventory-service/v1/stock/getStockNoSync"), Stock
                        .class);
        List<Map<String, Long>> productIds = new ArrayList<>(stocks.size() + 1);
        Map<String, Long> stockMap = null;
        for (Stock s : stocks) {
            stockMap = new HashMap<>();
            stockMap.put(s.getProduct().getProductId(), s.getNumber());
            productIds.add(stockMap);
        }
        return productIds;
    }

    /**
     * 修改进货商品状态
     */
    @HystrixCommand
    public Stock modifyStockState(String productId) {
        Stock stock = restTemplate
                .getForObject(String.format("http://inventory-service/v1/stock/modifyProductState?productId=%s",
                        productId), Stock
                        .class);
        return stock;
    }

    /**
     * 修改商品库存数量
     */
    @HystrixCommand
    public Inventory modifyProductNum(String productId, Long productNum) {
        Inventory inventory = restTemplate
                .getForObject(String.format("http://inventory-service/v1/stock/modifyProductNum?productId=%s" +
                        "&productNum=%s", productId, productNum), Inventory.class);
        return inventory;
    }
}
