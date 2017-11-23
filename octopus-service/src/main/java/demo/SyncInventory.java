package demo;

import com.dataman.octopus.job.OctopusJavaMsgJob;
import com.vip.saturn.job.SaturnJobExecutionContext;
import com.vip.saturn.job.SaturnJobReturn;
import com.vip.saturn.job.msg.MsgHolder;

/**
 * 同步仓库数据 java msg job
 */
public class SyncInventory extends OctopusJavaMsgJob {

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
        return jobReturn;
    }
}
