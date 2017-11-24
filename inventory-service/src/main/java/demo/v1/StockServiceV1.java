package demo.v1;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import demo.stock.Stock;
import demo.stock.StockRepository;

@Service
public class StockServiceV1 {

    private StockRepository stockRepository;

    @Autowired
    public StockServiceV1(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @HystrixCommand
    public List<Stock> getStockNoSync() {
        return stockRepository.getStockNoSync();
    }

    @HystrixCommand
    public Stock modifyProductState(String productId) {
        return stockRepository.modifyProductState(productId);
    }

}
