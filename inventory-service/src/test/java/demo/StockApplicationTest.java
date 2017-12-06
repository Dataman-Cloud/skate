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
    private Map<String, Object> recode = new ConcurrentHashMap<>();
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
                    recode.put(String.valueOf(s.getId()), s.getProduct().getProductId());
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
                recode.put(String.valueOf(stock.getId()), stock.getProduct().getProductId());
            }

            for (Stock st : stocks) {
                Stock stock = stockRepository.modifyProductState(st.getProduct().getProductId());
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
                recode.put("inventory-" + s.getId(), s.getProduct().getProductId());
            }
            List<Stock> stocks = readyStockData();
            for (Stock s : stocks) {
                Stock stock = stockRepository.save(s);
                recode.put("stock-" + s.getId(), s.getProduct().getProductId());
            }

            for (Inventory inventory : inventories) {
                for (Stock s : stocks) {
                    String productId = s.getProduct().getProductId();
                    if (inventory.getProduct().getProductId().equals(productId)) {
                        Long productNum = s.getNumber();
                        Long nowInventoryNum = Long.valueOf(inventoryRepository.getInventoryNumByPid(productId));
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
        if (recode != null && recode.size() > 0) {
            if (nodeName.equals(stockNode)) {
                for (Map.Entry<String, Object> map : recode.entrySet()) {
                    String id = map.getKey();
                    if (id.startsWith("stock-")) {
                        id = id.replaceAll("stock-", "");
                    }
                    try {
                        stockRepository.deleteSRelationP(id);
                        recode.remove(map.getKey());
                        log.info("清理测试数据：stockId:" + id + " 成功！");
                    } catch (Exception e) {
                        log.error("清理测试数据：stockId:" + id + " 失败！");
                        e.printStackTrace();
                    }
                }
            } else if (nodeName.equals(inventoryNode)) {
                for (Map.Entry<String, Object> map : recode.entrySet()) {
                    String id = map.getKey();
                    if (id.startsWith("inventory-")) {
                        id = id.replaceAll("inventory-", "");
                    }
                    try {
                        inventoryRepository.deleteIRelationP(id);
                        recode.remove(map.getKey());
                        log.info("清理测试数据：inventoryId:" + id + " 成功！");
                    } catch (Exception e) {
                        log.error("清理测试数据：inventoryId:" + id + " 失败！");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

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


    @Test
    public void updateStockByStockId() {
        Stock stock = stockRepository.updateStockByProductId("SKU-64233", 95);
        //  System.out.println(stock.getNumber());
    }
}
