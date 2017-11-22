package demo;

import com.vip.saturn.job.AbstractSaturnJavaJob;
import com.vip.saturn.job.SaturnJobExecutionContext;
import com.vip.saturn.job.SaturnJobReturn;

/**
 * 定时扫描仓库 java job
 */
public class ScanInventory extends AbstractSaturnJavaJob{

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
        
        return new SaturnJobReturn("我是分片" + shardItem + "的处理结果");
    }
}
