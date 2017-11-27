package demo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import demo.inventory.Inventory;
import demo.inventory.InventoryRepository;
import demo.product.Product;
import demo.stock.Stock;
import demo.stock.StockRepository;
import demo.v1.InventoryServiceV1;

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

    @Before
    public void setup() {
        try {
            /*neo4jConfiguration.getSession().query(
                    "MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n, r;", new HashMap<>())
                    .queryResults();*/
        } catch (Exception e) {
            neo4jConnection = false;
        }
    }

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
            for(Stock s : stocks){
                Stock stock = stockRepository.save(s);
            }

            for(Inventory inventory : inventories){
                for (Stock s : stocks) {
                    String productId = s.getProduct().getProductId();
                    if(inventory.getProduct().getProductId().equals(s.getProduct().getProductId())){
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
        Product product1 = new Product("衣服-TEST-12464", "TEST-12464", "测试新增的数据", 15.55);
        Stock stock1 = new Stock(product1, 100L, "admin", new Date(), null, false);

        Product product2 = new Product("衣服-TEST-34563", "TEST-34563", "测试新增的数据", 18.99);
        Stock stock2 = new Stock(product2, 100L, "admin", new Date(), null, false);

        Product product3 = new Product("衣服-TEST-64233", "TEST-64233", "测试新增的数据", 188.99);
        Stock stock3 = new Stock(product3, 100L, "admin", new Date(), null, false);

        List<Stock> stocks = Arrays.asList(stock1, stock2, stock3).stream().collect(Collectors.toList());
        return stocks;
    }

    public static List<Inventory> readyInventoryData() {
        Product product1 = new Product("衣服-TEST-12464", "TEST-12464", "测试新增的数据", 15.55);
        Inventory inventory1 = new Inventory("150", product1);

        Product product2 = new Product("衣服-TEST-34563", "TEST-34563", "测试新增的数据", 18.99);
        Inventory inventory2 = new Inventory("180", product2);

        Product product3 = new Product("衣服-TEST-64233", "TEST-64233", "测试新增的数据", 188.99);
        Inventory inventory3 = new Inventory("230", product3);

        List<Inventory> inventory = Arrays.asList(inventory1, inventory2, inventory3).stream().collect(Collectors
                .toList());
        return inventory;
    }

    public void clearData(String nodeName) {
        if(recode!=null && recode.size()>0) {
            if (nodeName.equals("stock")) {
                for (Map.Entry<Long, Object> map : recode.entrySet()) {
                    stockRepository.delete(map.getKey());
                }

            } else if (nodeName.equals("inventory")) {
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
