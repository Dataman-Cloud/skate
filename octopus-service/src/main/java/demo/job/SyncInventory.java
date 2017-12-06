package demo.job;

import com.dataman.octopus.job.OctopusJavaMsgJob;
import com.vip.saturn.job.SaturnJobExecutionContext;
import com.vip.saturn.job.SaturnJobReturn;
import com.vip.saturn.job.msg.MsgHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import demo.OctopusApplication;
import demo.service.StockService;
import demo.util.TimeUtil;

/**
 * 同步仓库数据 java msg job
 */
public class SyncInventory extends OctopusJavaMsgJob {

    private Logger log = LoggerFactory.getLogger(ScanInventory.class);

    //测试时开启，打包时注释掉，springBoot只支持一个启动main实例
    /*public static void main(String[] args) {
        SyncInventory syncInventory = new SyncInventory();

        String value = "[{\"productId\":\"SKU-24642\",\"productNum\":100},{\"productId\":\"SKU-34563\",\"productNum\":100},{\"productId\":\"SKU-12464\",\"productNum\":100},{\"productId\":\"SKU-64233\",\"productNum\":100}]";
        syncInventory.handleMsgJob("aa", 1, value, new MsgHolder(new byte[1024], null, "test"), new
                SaturnJobExecutionContext());
    }*/

    /**
     * Java消息作业的具体执行任务内容
     *
     * @param jobName   作业名称
     * @param key       消息内容的key
     * @param value     消息内容的value
     * @param msgHolder 消息内容的payload
     * @param context   作业运行的上下文
     * @return 根据作业的处理结果, 返回相应的信息
     */
    public SaturnJobReturn handleMsgJob(String jobName, Integer key, String value, MsgHolder msgHolder,
            SaturnJobExecutionContext context) {
        log.info("msg job 同步库存信息开始！");
        log.info("1:准备初始化环境...");
        ConfigurableApplicationContext evn = initEnv(new String[0]);
        log.info("2:获取相关类bean对象...");
        StockService stockService = evn.getBean(StockService.class);

        String body = String.format("[Octopus]: Job %s-%s-%s payload=%s, headers=%s ",
                jobName, key, value, new String(msgHolder.getPayloadBytes()), msgHolder.getProp());
        log.info(body);

        log.info(String.format("kafka接受到进货商品信息：[%s] 时间：" + TimeUtil.ymdHms2str(),
                value));

        log.info("3:开始执行具体任务...");
        String result = stockService.syncInventory(value);
        log.info("4:已经执行完成，开始返回执行结果...");
        return new SaturnJobReturn("demo1: " + "headers=" + msgHolder.getProp()
                + ", payload=" + new String(msgHolder.getPayloadBytes())
                + ", offset=" + msgHolder.getMessageId() + ", time=" + System
                .currentTimeMillis() + ", info=" + result);
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
