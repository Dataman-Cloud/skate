package demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import demo.inventory.Inventory;
import demo.inventory.InventoryRepository;
import demo.stock.Stock;
import demo.stock.StockRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {InventoryApplication.class})
@ActiveProfiles(profiles = "test")
public class StockApplicationTest {

    private Logger log = LoggerFactory.getLogger(InventoryApplicationTests.class);
    private Boolean neo4jConnection = true;
    private Map<Long, Object> recode = new ConcurrentHashMap<>();
    private static final String stockNode = "stock";
    private static final String inventoryNode = "inventory";

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private Neo4jConfiguration neo4jConfiguration;

    @Test
    public void addStockTest() {
        if (neo4jConnection) {

            List<Stock> stocks = readyStockData();
            try {
                for (Stock stock : stocks) {
                    Stock s = stockRepository.save(stock);
                    recode.put(s.getId(), s.getProduct().getProductId());
                }

                Assert.assertTrue("新增数据成功！", true);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //删除测试数据
                clearData(stockNode);
            }
        }
    }

    @Test
    public void modifyStateTest() {
        List<Stock> stocks = readyStockData();
        try {

            for (Stock stock : stocks) {
                Stock s = stockRepository.save(stock);
            }

            for (Stock st : stocks) {
                Stock stock = stockRepository.modifyProductState(st.getProduct().getProductId());
                recode.put(stock.getId(), stock.getProduct().getProductId());
            }
            Assert.assertTrue("修改数据成功！", true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clearData(stockNode);
        }
    }

    @Test
    public void modifyInventoryNumTest() {
        try {
            List<Inventory> inventories = readyInventoryData();
            for (Inventory inventory : inventories) {
                Inventory s = inventoryRepository.save(inventory);
                recode.put(s.getId(), s.getProduct().getProductId());
            }
            List<Stock> stocks = readyStockData();
            for (Stock s : stocks) {
                Stock stock = stockRepository.save(s);
            }

            for (Inventory inventory : inventories) {
                for (Stock s : stocks) {
                    String productId = s.getProduct().getProductId();
                    if (inventory.getProduct().getProductId().equals(productId)) {
                        Long productNum = s.getNumber();
                        Long nowInventoryNum = inventoryRepository.getInventoryNumByPid(productId);
                        nowInventoryNum = nowInventoryNum > 0 ? nowInventoryNum : 0;
                        String modifyInventoryNum = String.valueOf(productNum + nowInventoryNum); //当前库存量加上原有库存量
                        Inventory i = inventoryRepository.modifyProductNum(productId, modifyInventoryNum);
                        System.out.print("商品编号：" + i.getProduct().getProductId() + "修改成功\n");
                    }
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

    public static List<Stock> readyStockData() {
        return ReadyData.readyStockData();
    }

    public static List<Inventory> readyInventoryData() {
        return ReadyData.readyInventoryData();
    }

    public void clearData(String nodeName) {
        if (recode != null && recode.size() > 0) {
            if (nodeName.equals(stockNode)) {
                for (Map.Entry<Long, Object> map : recode.entrySet()) {
                    stockRepository.delete(map.getKey());
                }
            } else if (nodeName.equals(inventoryNode)) {
                for (Map.Entry<Long, Object> map : recode.entrySet()) {
                    inventoryRepository.delete(map.getKey());
                }
            }
            recode.clear();
        }
    }

    @Test
    public void deleteNode() {
        String nodeName = "Stock";
        try {
            neo4jConfiguration.getSession().query(
                    "MATCH (n:" + nodeName + ") OPTIONAL MATCH (n)-[r]-() DELETE n, r;", new HashMap<>())
                    .queryResults();
        } catch (Exception e) {
            neo4jConnection = false;
        }
    }
}
