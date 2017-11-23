package demo.job;

import com.dataman.octopus.job.OctopusJavaMsgJob;
import com.vip.saturn.job.SaturnJobExecutionContext;
import com.vip.saturn.job.SaturnJobReturn;
import com.vip.saturn.job.msg.MsgHolder;

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import ch.qos.logback.core.net.SyslogOutputStream;

/**
 * 同步仓库数据 java msg job
 */
public class SyncInventory extends OctopusJavaMsgJob {

    private Logger log = LoggerFactory.getLogger(ScanInventory.class);

    /**
     * Java消息作业的具体执行任务内容
     *
     * @param  jobName 作业名称
     * @param  key 消息内容的key
     * @param  value 消息内容的value
     * @param  msgHolder 消息内容的payload
     * @param  context 作业运行的上下文
     * @return 根据作业的处理结果, 返回相应的信息
     */
    public SaturnJobReturn handleMsgJob(String jobName, Integer key, String value, MsgHolder msgHolder,
            SaturnJobExecutionContext context) {
        String body = String.format("[Octopus]: Job %s-%s-%s payload=%s, headers=%s ",
                jobName, key, value, new String(msgHolder.getPayloadBytes()), msgHolder.getProp());
        System.out.println(body);

        SaturnJobReturn jobReturn = new SaturnJobReturn("demo1: " + "headers=" + msgHolder.getProp()
                + ", payload=" + new String(msgHolder.getPayloadBytes())
                + ", offset=" + msgHolder.getMessageId() + ", time=" + System.currentTimeMillis());

        //获取商品id,并修改进货表中数据状态
        System.out.print(String.format("kafka接受到进货商品信息：[%s] 时间：" + DateFormatUtils.format(new Date(),"yyyy-MM-dd " +
                        "HH:mm:ss"), value));
        log.info(String.format("kafka接受到进货商品信息：[%s] 时间：" + DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"),
                value));
        //在商品数量信息同步修改到库存表中

        return jobReturn;
    }
}
