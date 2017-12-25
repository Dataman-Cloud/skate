package demo.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import demo.util.HttpUtil;
import demo.util.JSONSerializer;
import demo.util.LocalCache;
import demo.util.TimeUtil;

@Service
public class StockService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private RestTemplate restTemplate;

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
    private String oauthSecret;

    @Value("${spring.application.passwordGrantStr}")
    private String passwordGrantStr;

    @Value("${spring.application.accessTokenStr}")
    private String accessTokenStr;

    @Value("${spring.application.returnMsgKey}")
    private String returnMsgKey;

    @Value("${spring.application.resultKey}")
    private String resultKey;

    /**
     * 获取未同步的商品
     */
    public String getStockNoSync() {

        List<Map<String, Object>> stockList = new ArrayList<Map<String, Object>>();
        String stockStr = null;
        try {
            //1:获取token
            String token = LocalCache.getToken();
            if (StringUtils.isBlank(token)) {
                token = HttpUtil.getToken(restTemplate, getTokenUrl + passwordGrantStr, oauthClientId, oauthSecret);
                LocalCache.setToken(token);
            }
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
        String productNum = null;
        try {
            //获取商品id,并修改进货表中数据状态
            if (kafkaMsg.endsWith("}]\"}")) {
                kafkaMsg = kafkaMsg.replaceAll("\\\\","");
                kafkaMsg = "[" + kafkaMsg + "]";
                kafkaMsg = kafkaMsg.replace("\"returnMsg\":\"", "\"returnMsg\":");
                kafkaMsg = kafkaMsg.replace("}]\"}]", "}]}]");
                kafkaMsg = kafkaMsg.replaceAll(" ", "");

                List<Map<String, Object>> stockMaps = JSONSerializer.json2ListMap(kafkaMsg, Map[].class);
                Map<String, Object> stockMap = stockMaps.get(0);
                if (stockMap != null) {
                    if (stockMap.get(resultKey).equals(HttpUtil.OK)) {
                        List<Map<String, Object>> stocks = (List<Map<String, Object>>) stockMap.get(returnMsgKey);

                        for (Map<String, Object> stock : stocks) {
                            productId = stock.get(productIdKey).toString();
                            productNum = stock.get(productNumKey).toString();

                            //1：修改进货状态
                            this.modifyStockState(productId);

                            //2：修改库存数量
                            this.modifyProductNum(productId, productNum);

                            log.info("同步商品编号为：" + productId + "数量为：" + productNum + "入库成功！" + TimeUtil.ymdHms2str(),
                                    kafkaMsg);
                            record.append("(编号为：" + productId + " 数量为：" + productNum + " )");
                        }
                        result = "同步商品：[" + record.toString() + "] 成功！" + TimeUtil.ymdHms2str();
                    } else {
                        result = "Kafka msg states is: " + stockMap.get(resultKey);
                    }
                }
            } else {
                result = "暂时没有未同步的商品！";
            }
        } catch (Exception e) {
            result = "同步商品编号为：" + productId + "数量为：" + productNum + "失败！" + e.getMessage();
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
        String token = LocalCache.getToken();
        if (StringUtils.isBlank(token)) {
            token = HttpUtil.getToken(restTemplate, getTokenUrl + passwordGrantStr, oauthClientId, oauthSecret);
            LocalCache.setToken(token);
        }
        log.info("token : " + token);
        HttpEntity<String> request = new HttpEntity<String>(HttpUtil.getHeaders());
        //2:调用接口获取数据
        String stock = restTemplate
                .postForObject(remoteModifyProductStateUrl + productId + accessTokenStr + token, request, String
                        .class);
        log.info(stock);
    }

    /**
     * 修改商品库存数量
     */
    public void modifyProductNum(String productId, String productNum) {

        String token = LocalCache.getToken();
        if (StringUtils.isBlank(token)) {
            token = HttpUtil.getToken(restTemplate, getTokenUrl + passwordGrantStr, oauthClientId, oauthSecret);
            LocalCache.setToken(token);
        }
        log.info("token : " + token);

        HttpEntity<String> request = new HttpEntity<String>(HttpUtil.getHeaders());
        String inventory = restTemplate
                .postForObject(remoteModifyInventoryNumUrl + productId + "/" + productNum + accessTokenStr + token,
                        request,
                        String.class);
        log.info(inventory);
    }
}
