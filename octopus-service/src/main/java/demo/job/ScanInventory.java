package demo.job;

import com.alibaba.fastjson.JSON;
import com.vip.saturn.job.AbstractSaturnJavaJob;
import com.vip.saturn.job.SaturnJobExecutionContext;
import com.vip.saturn.job.SaturnJobReturn;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

import demo.service.StockServiceV1;
import demo.util.JSONSerializer;
import demo.v1.StockControllerV1;

/**
 * 定时扫描仓库 java job
 * TODO 测试，不加@RestController注解貌似不行
 */
public class ScanInventory extends AbstractSaturnJavaJob{

    private Logger log = LoggerFactory.getLogger(ScanInventory.class);

    @Autowired
    private StockServiceV1 stockServiceV1;

    /**
     * Java定时作业的具体执行任务内容
     *
     * @param  jobName 作业名称
     * @param  shardItem 分片id
     * @param  shardParam 该分片的参数
     * @param  context 作业运行的上下文
     * @return 根据作业的处理结果, 返回相应的信息
     */
    @Override
    public SaturnJobReturn handleJavaJob(final String jobName, final Integer shardItem, final String shardParam,
            final SaturnJobExecutionContext context) {
        //查询未同步数据
        List<Map<String,Long>> stocks = stockServiceV1.getStockNoSync();
        String stocksStr = JSONSerializer.serialize(stocks);
        System.out.print(String.format("扫描到进货商品：[%s] 时间：" + DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"),
                stocksStr));
        log.info(String.format("扫描到进货商品：[%s] 时间：" + DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"),
                stocksStr));
        return new SaturnJobReturn(stocksStr);
    }

}
