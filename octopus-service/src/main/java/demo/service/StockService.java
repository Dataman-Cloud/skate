package demo.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import demo.util.HttpUtil;
import demo.util.JSONSerializer;
import demo.util.TimeUtil;

@Service
public class StockService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private RestTemplate restTemplate;

    private Map<String, Object> tokenCache = new ConcurrentHashMap<>();

    @Autowired
    public StockService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${spring.application.remoteGetStockNoSyncUrl}")
    private String remoteGetStockNoSync;

    @Value("${spring.application.remoteModifyProductStateUrl}")
    private String remoteModifyProductStateUrl;

    @Value("${spring.application.remoteModifyInventoryNumUrl}")
    private String remoteModifyInventoryNumUrl;

    @Value("${spring.application.productIdKey}")
    private String productIdKey;

    @Value("${spring.application.getTokenUrl}")
    private String getTokenUrl;

    @Value("${spring.application.productNumKey}")
    private String productNumKey;

    @Value("${spring.application.oauthClientId}")
    private String oauthClientId;

    @Value("${spring.application.oauthSecret}")
    private static String oauthSecret;

    @Value("${spring.application.passwordGrantStr}")
    private String passwordGrantStr;

    @Value("${spring.application.accessTokenStr}")
    private String accessTokenStr;

    /**
     * 获取未同步的商品
     */
    public String getStockNoSync() {

        List<Map<String, Object>> stockList = new ArrayList<>();
        String stockStr = null;
        try {
            //1:获取token
            String token = HttpUtil.getToken(restTemplate, getTokenUrl + passwordGrantStr, oauthClientId, oauthSecret);
            log.info("token : " + token);
            HttpEntity<String> request = new HttpEntity<String>(HttpUtil.getHeaders());
            //2:调用接口获取数据
            stockStr = restTemplate.postForObject(remoteGetStockNoSync + accessTokenStr + token, request, String
                    .class);
            log.info("stockStr: " + stockStr);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stockStr;
    }

    /**
     * 同步库存 1：修改进货状态 2：修改库存数量
     */
    public String syncInventory(String kafkaMsg) {

        String result = null;
        String productId = null;
        StringBuilder record = new StringBuilder();
        Long productNum = 0L;
        try {
            //获取商品id,并修改进货表中数据状态
            List<Map<String, Object>> stocks = JSONSerializer.json2Map(kafkaMsg, Map[].class);
            for (Map<String, Object> stock : stocks) {
                productId = stock.get(productIdKey).toString();
                Double DProductNum = (Double) stock.get(productNumKey);
                double dProductNum = DProductNum.doubleValue();
                long lProductNum = (long) dProductNum;
                productNum = (Long) lProductNum;

                //1：修改进货状态
                this.modifyStockState(productId);

                //2：修改库存数量
                this.modifyProductNum(productId, productNum);

                log.info("同步商品编号为：" + productId + "数量为：" + productNum + "入库成功！" + DateFormatUtils.format(new
                                Date(),
                        "yyyy-MM-dd " +
                                "HH:mm:ss \n"), kafkaMsg);
                record.append("(编号为：" + productId + " 数量为：" + productNum + " )");
            }
            result = "同步商品：[" + record.toString() + "] 成功！" + TimeUtil.ymdHms2str();
        } catch (Exception e) {
            result = "同步商品编号为：" + productId + "数量为：" + productNum + "失败！" + TimeUtil.ymdHms2str();
        } finally {
            log.info(result);
        }
        return result;
    }

    /**
     * 修改进货商品状态
     */
    public void modifyStockState(String productId) {
        //1:获取token
        String token = HttpUtil.getToken(restTemplate, getTokenUrl + passwordGrantStr, oauthClientId, oauthSecret);
        log.info("token : " + token);
        HttpEntity<String> request = new HttpEntity<String>(HttpUtil.getHeaders());
        //2:调用接口获取数据
        String stock = restTemplate
                .postForObject(remoteModifyProductStateUrl + productId + accessTokenStr + token, request, String
                        .class);
        if (StringUtils.isNotBlank(token)) {
            tokenCache.put("token", token);
        }
        log.info(stock);
    }

    /**
     * 修改商品库存数量
     */
    public void modifyProductNum(String productId, Long productNum) {

        String token = null;
        if (tokenCache != null && tokenCache.size() > 0) {
            token = tokenCache.get("token").toString();
            if (StringUtils.isBlank(token)) {
                token = HttpUtil.getToken(restTemplate, getTokenUrl + passwordGrantStr, oauthClientId, oauthSecret);
                tokenCache.put("token", token);
            }
        } else {
            token = HttpUtil.getToken(restTemplate, getTokenUrl + passwordGrantStr, oauthClientId, oauthSecret);
            tokenCache.put("token", token);
        }

        HttpEntity<String> request = new HttpEntity<String>(HttpUtil.getHeaders());
        String inventory = restTemplate
                .postForObject(remoteModifyInventoryNumUrl + productId + "/" + productNum + accessTokenStr + token,
                        request,
                        String.class);
        log.info(inventory);
    }
}
