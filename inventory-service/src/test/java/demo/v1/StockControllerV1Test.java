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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import demo.InventoryApplication;
import demo.ReadyData;
import demo.StockApplicationTest;
import demo.inventory.Inventory;
import demo.inventory.InventoryRepository;
import demo.stock.Stock;
import demo.stock.StockRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {InventoryApplication.class})
@ActiveProfiles(profiles = "test")
public class StockControllerV1Test {

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
     * 测试未同步商品
     */
    @Test
    public void getStockNoSyncTest() {
        List<Stock> stocks = (List<Stock>) restTemplate
                .getForObject(String.format("http://inventory-service/v1/stock/getStockNoSync"), Stock
                        .class);
        Assert.assertEquals("测试数据Stock不能为空！", stocks);
        List<Map<String, Long>> productIds = new ArrayList<>(stocks.size() + 1);
        for (Stock s : stocks) {
            System.out.print("产品编号：" + s.getProduct().getProductId() + "产品名称：" + s.getProduct().getName() + "\n");
        }
    }

    /**
     * 修改同步商品
     */
    @Test
    public void modifyProductState() {
        //添加测试数据
        try {
            List<Stock> stocks = readyStockData();
            for (Stock stock : stocks) {
                Stock s = stockRepository.save(stock);
                recodeMsg.put(s.getId(), s.getProduct().getProductId());
            }

            //同步状态
            for (Map.Entry entry : recodeMsg.entrySet()) {
                try {
                    Stock stock = restTemplate
                            .getForObject(
                                    String.format("http://inventory-service/v1/stock/modifyProductState?productId=%s",
                                            entry.getKey()), Stock.class);
                    Assert.assertTrue("同步数据：" + stock.getId() + "成功！", true);
                    System.out.print("同步数据：" + stock.getId() + "成功！\n");
                } catch (Exception e) {
                    System.out.print("同步数据：" + entry.getKey() + "失败！\n");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clearData(stockNode);
        }
    }

    public static List<Stock> readyStockData() {
        return ReadyData.readyStockData();
    }

    public static List<Inventory> readyInventoryData() {
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
