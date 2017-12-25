package demo.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import demo.service.StockService;

/**
 * 缓存对象
 */
public class LocalCache {

    private static final String stockServiceKey = "stockService";

    private static final String tokenKey = "token";

    private static Map<String, StockService> stockServiceBean = new ConcurrentHashMap<String, StockService>();

    private static Map<String, String> tokenCache = new ConcurrentHashMap<String, String>();

    public static StockService getStockServiceBean() {
        return stockServiceBean.get(stockServiceKey);
    }

    public static void setStockServiceBean(StockService stockService) {
        stockServiceBean.put(stockServiceKey, stockService);
    }

    public static String getToken() {
        return tokenCache.get(tokenKey);
    }

    public static void setToken(String token) {
        tokenCache.put(tokenKey, token);
    }
}
