package demo.v1;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import demo.inventory.Inventory;
import demo.inventory.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import demo.stock.Stock;
import demo.stock.StockRepository;

@Service
public class StockServiceV1 {

    private StockRepository stockRepository;
    private InventoryRepository inventoryRepository;

    @Autowired
    public StockServiceV1(StockRepository stockRepository,InventoryRepository inventoryRepository) {
        this.stockRepository = stockRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @HystrixCommand(fallbackMethod = "getStockFeedBack")
    public List<Stock> getStockNoSync() {
        return stockRepository.getStockNoSync();
    }

    @HystrixCommand(fallbackMethod = "getStockFeedBack")
    public Stock modifyProductState(String productId) {
        return stockRepository.modifyProductState(productId);
    }

    @HystrixCommand(fallbackMethod = "getStockFeedBack")
    public Stock addStock(Stock stock, Long number) {
        stock.setNumber(stock.getNumber() + number);
        return this.stockRepository.save(stock);
    }

    @HystrixCommand(fallbackMethod = "getStockFeedBack")
    public Iterable<Stock> getStockALL() {
        return this.stockRepository.findAll();
    }

    @HystrixCommand
    public Stock getStockByProductId(String productId) {
        return this.stockRepository.getStockByProductId(productId);
    }

    private Stock getStockFeedBack(String productId) {
        return null;
    }

    @HystrixCommand
    public Stock updateStockByProductId(String productId,Integer number) {
         return    stockRepository.updateStockByProductId(productId,number);
    }

    @HystrixCommand
    public Iterable<Stock> getProductRelateStock() {
        Iterable<Stock> stockIterable = stockRepository.getProductRelateStock();
        stockIterable.forEach(new Consumer<Stock>() {
            @Override
            public void accept(Stock stock) {
                if (stock.getProduct() != null) {
                    Stream<Inventory> availableInventory = inventoryRepository.getAvailableInventoryForProduct(stock.getProduct().getProductId()).stream();
                    stock.getProduct().setInStock(availableInventory.findAny().isPresent());
                }
            }
        });

        return stockIterable;
    }



}
