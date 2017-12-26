package demo.v1;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import demo.inventory.Inventory;
import demo.inventory.InventoryRepository;
import demo.product.Product;
import demo.product.ProductRepository;
import demo.stock.Stock;
import demo.stock.StockRepository;

import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InventoryServiceV1 {
    private InventoryRepository inventoryRepository;
    private ProductRepository productRepository;
    private StockRepository stockRepository;
    private Session neo4jTemplate;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public InventoryServiceV1(InventoryRepository inventoryRepository,
            ProductRepository productRepository, Session neo4jTemplate,StockRepository stockRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.neo4jTemplate = neo4jTemplate;
        this.stockRepository = stockRepository;
    }

    @HystrixCommand
    public Product getProductByProductId(String productId) {
        Product product;

        product = productRepository.getProductByProductId(productId);

        if (product != null) {
            Stream<Inventory> availableInventory = inventoryRepository.getAvailableInventoryForProduct(productId)
                    .stream();
            product.setInStock(availableInventory.findAny().isPresent());
        }

        return product;
    }

    @HystrixCommand
    public Product getProduct(String productId) {
        Product product;

        product = productRepository.getProductByProductId(productId);

        if (product != null) {
            Stream<Inventory> availableInventory = inventoryRepository.getAvailableInventoryForProduct(productId)
                    .stream();
            product.setInStock(availableInventory.findAny().isPresent());
        }

        return product;
    }

    public List<Inventory> getAvailableInventoryForProductIds(String productIds) {
        List<Inventory> inventoryList;

        inventoryList = inventoryRepository.getAvailableInventoryForProductList(productIds.split(","));

        return neo4jTemplate.loadAll(inventoryList, 1)
                .stream().collect(Collectors.toList());
    }

    public Inventory modifyProductNum(String productId, Long productNum) {
        Long nowInventoryNum = Long.valueOf(getInventoryNumByPid(productId));
        nowInventoryNum = nowInventoryNum > 0 ? nowInventoryNum : 0;
        String modifyInventoryNum = String.valueOf(productNum + nowInventoryNum); //当前库存量加上原有库存量
        Inventory inventory = null;
        try {
            //修改同步库存数量
            inventory = inventoryRepository.modifyProductNum(productId, modifyInventoryNum);
            Long nowStockNum = stockRepository.getStockByProductId(productId).getNumber();
            log.info("修改库存数量成功，商品编号为：" + productId + " 数量修改为：" + modifyInventoryNum);
            //修改库存数量
            if (nowStockNum > 0) {
                Stock stock = stockRepository.syncStockNumByProductId(productId, (int) (nowStockNum - productNum));
                log.info("修改后当前未同步数量为：" + stock
                        .getNumber());
            }
        } catch (Exception e) {
            log.error("修改库存数量出错！产品编号：" + productId);
        }
        return inventory;
    }

    public String getInventoryNumByPid(String productId) {
        return inventoryRepository.getInventoryNumByPid(productId);
    }

    private Product getProductFallback(String productId) {
        return null;
    }

}
