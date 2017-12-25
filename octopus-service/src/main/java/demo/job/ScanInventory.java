package demo.job;

import com.vip.saturn.job.AbstractSaturnJavaJob;
import com.vip.saturn.job.SaturnJobExecutionContext;
import com.vip.saturn.job.SaturnJobReturn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import demo.OctopusApplication;
import demo.service.StockService;
import demo.util.LocalCache;
import demo.util.TimeUtil;

/**
 * 定时扫描仓库 java job
 */
public class ScanInventory extends AbstractSaturnJavaJob {

    private Logger log = LoggerFactory.getLogger(ScanInventory.class);

    //测试时开启，打包时注释掉，springBoot只支持一个启动main实例
    /*public static void main(String[] args) {
        ScanInventory scanInventory = new ScanInventory();
        scanInventory.handleJavaJob("aa", 1, "a", new SaturnJobExecutionContext());
    }*/

    /**
     * Java定时作业的具体执行任务内容
     *
     * @param jobName    作业名称
     * @param shardItem  分片id
     * @param shardParam 该分片的参数
     * @param context    作业运行的上下文
     * @return 根据作业的处理结果, 返回相应的信息
     */
    @Override
    public SaturnJobReturn handleJavaJob(final String jobName, final Integer shardItem, final String shardParam,
            final SaturnJobExecutionContext context) {
        log.info("java job 定时扫描进货表开始！");

        log.info("1:准备初始化环境...");
        //通过判断缓存中是否存在bean对象来选择是否重启应用
        StockService stockService = LocalCache.getStockServiceBean();

        if (stockService == null) {
            ConfigurableApplicationContext evn = initEnv(new String[0]);
            log.info("2:获取相关类bean对象...");
            stockService = evn.getBean(StockService.class);
            LocalCache.setStockServiceBean(stockService);
        }

        log.info("3:开始执行具体任务...");

        String stocksStr = stockService.getStockNoSync();

        log.info(String.format("扫描到进货商品：[%s] 时间：" + TimeUtil.ymdHms2str(),
                stocksStr));
        log.info("4:已经执行完成，开始返回执行结果 " + stocksStr + " ，并放入topic中...");
        return new SaturnJobReturn(stocksStr);
    }

    /**
     * 环境初始化
     */
    private ConfigurableApplicationContext initEnv(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(OctopusApplication.class, args);
        log.info("初始化环境完成！");
        return context;
    }
}
