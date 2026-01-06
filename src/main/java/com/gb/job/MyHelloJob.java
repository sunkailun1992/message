package com.gb.job;

import com.alibaba.schedulerx.worker.domain.JobContext;
import com.alibaba.schedulerx.worker.processor.JavaProcessor;
import com.alibaba.schedulerx.worker.processor.ProcessResult;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author sunkailun
 * @DateTime 2021/1/4  下午2:09
 * @email 376253703@qq.com
 * @phone 13777579028
 * @explain
 */
@Component
public class MyHelloJob extends JavaProcessor {

    @Override
    public ProcessResult process(JobContext context) throws Exception {
        System.out.println("hello schedulerx2.0");
        return new ProcessResult(true);
    }
}
