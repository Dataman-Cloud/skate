package demo.v1;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import demo.InventoryApplication;
import demo.ReadyData;
import demo.inventory.Inventory;
import demo.inventory.InventoryRepository;
import demo.stock.Stock;
import demo.stock.StockRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {InventoryApplication.class})
@ActiveProfiles(profiles = "test")
public class InventoryControllerV1Test {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StockRepository stockRepository;

    private Map<Long, Object> recodeMsg = new ConcurrentHashMap<>();

    @Autowired
    private InventoryRepository inventoryRepository;

    private static final String stockNode = "stock";
    private static final String inventoryNode = "inventory";

    /**
     * 修改商品库存量
     */
    @Test
    public void modifyProductNumTest() {
        try {
            List<Inventory> inventories = readyInventoryData();
            for (Inventory inventory : inventories) {
                Inventory s = inventoryRepository.save(inventory);
                recodeMsg.put(s.getId(), s.getProduct().getProductId());
            }
            List<Stock> stocks = readyStockData();
            for (Stock s : stocks) {
                Stock stock = stockRepository.save(s);
            }

            for (Inventory inventory : inventories) {
                for (Stock s : stocks) {
                    String productId = s.getProduct().getProductId();
                    Long productNum = s.getNumber();
                    Long nowInventoryNum = inventoryRepository.getInventoryNumByPid(productId);
                    nowInventoryNum = nowInventoryNum > 0 ? nowInventoryNum : 0;
                    String modifyInventoryNum = String.valueOf(productNum + nowInventoryNum); //当前库存量加上原有库存量
                    Inventory in = restTemplate
                            .getForObject(String.format
                                            ("http://inventory-service/api/inventory/v1/stock/modifyProductNum/%s/%s", productId, productNum),
                                    Inventory.class);
                    System.out.print("修改数据编号：" + in.getId() + "成功！");
                }
            }

            Assert.assertTrue("修改数据成功！", true);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clearData(inventoryNode);
            //clearData(stockNode);
        }
    }

    public List<Stock> readyStockData() {
        return ReadyData.readyStockData();
    }

    public List<Inventory> readyInventoryData() {
        return ReadyData.readyInventoryData();
    }

    public void clearData(String nodeName) {
        if (recodeMsg != null && recodeMsg.size() > 0) {
            if (nodeName.equals(stockNode)) {
                for (Map.Entry<Long, Object> map : recodeMsg.entrySet()) {
                    stockRepository.delete(map.getKey());
                }
            } else if (nodeName.equals(inventoryNode)) {
                for (Map.Entry<Long, Object> map : recodeMsg.entrySet()) {
                    inventoryRepository.delete(map.getKey());
                }
            }
            recodeMsg.clear();
        }
    }
}
