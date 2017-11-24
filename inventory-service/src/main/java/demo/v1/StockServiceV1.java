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
    public StockServiceV1(StockRepository stockRepository){
        this.stockRepository = stockRepository;
    }

    @HystrixCommand
    public List<Stock> getStockNoSync(){
        return stockRepository.getStockNoSync();
    }

    @HystrixCommand
    public Stock modifyProductState(String productId){
        return stockRepository.modifyProductState(productId);
    }

    @HystrixCommand(fallbackMethod = "getStockFeedBack")
    public Stock getStockByProductId(String productId){
        Stock stock;
        stock = stockRepository.getStockByProductId(productId);
        return stock;
    }

    @HystrixCommand(fallbackMethod = "getStockFeedBack")
    public Stock addStock(Stock stock,Long number){
        stock.setNumber(stock.getNumber()+number);
        return this.stockRepository.save(stock);
    }

    @HystrixCommand(fallbackMethod = "getStockFeedBack")
    public Iterable<Stock> getStockALL(){
        return this.stockRepository.findAll();
    }

    private Stock getStockFeedBack(String productId){return null;}

}
