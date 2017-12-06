package demo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import demo.inventory.Inventory;
import demo.product.Product;
import demo.stock.Stock;

/**
 * 准备数据
 */
public class ReadyData {

    public static List<Stock> readyStockData() {
        Product product1 = new Product("衣服-TEST-124641", "TEST-124641", "测试新增的数据", 15.55);
        Stock stock1 = new Stock(product1, 100L, "admin", new Date(), null, false);

        Product product2 = new Product("衣服-TEST-345631", "TEST-345631", "测试新增的数据", 18.99);
        Stock stock2 = new Stock(product2, 100L, "admin", new Date(), null, false);

        Product product3 = new Product("衣服-TEST-642331", "TEST-642331", "测试新增的数据", 188.99);
        Stock stock3 = new Stock(product3, 100L, "admin", new Date(), null, false);

        List<Stock> stocks = Arrays.asList(stock1, stock2, stock3).stream().collect(Collectors.toList());
        return stocks;
    }

    public static List<Inventory> readyInventoryData() {
        Product product1 = new Product("衣服-TEST-124641", "TEST-124641", "测试新增的数据", 15.55);
        Inventory inventory1 = new Inventory("150", product1);

        Product product2 = new Product("衣服-TEST-345631", "TEST-345631", "测试新增的数据", 18.99);
        Inventory inventory2 = new Inventory("180", product2);

        Product product3 = new Product("衣服-TEST-642331", "TEST-642331", "测试新增的数据", 188.99);
        Inventory inventory3 = new Inventory("230", product3);

        List<Inventory> inventory = Arrays.asList(inventory1, inventory2, inventory3).stream().collect(Collectors
                .toList());
        return inventory;
    }
}
