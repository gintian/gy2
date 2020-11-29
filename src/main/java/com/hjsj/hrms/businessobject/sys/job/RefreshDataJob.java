package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.utils.SyncSystemUtilBo;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshDataJob implements Job {

    private Logger log = LoggerFactory.getLogger(RefreshDataJob.class);

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            log.info("[刷新数据字典后台作业]任务开始");
            long start = System.currentTimeMillis();
            HttpClient httpClient = new HttpClient();
            String hrp_url = SystemConfig.getPropertyValue("hrp_logon_url");
            if (!"".equals(hrp_url)) {
                HttpMethod method = new GetMethod(hrp_url + "/servlet/sys/RefreshDataServlet?");
                httpClient.executeMethod(method);
                method.releaseConnection();
            }
            //发送集群操作处理刷新数据字典 wangb 20170626
            SyncSystemUtilBo.sendSyncCmd(SyncSystemUtilBo.SYNC_TYPE_RELOAD_DATADICTIONARY);
            log.info("[刷新数据字典后台作业]任务结束===[consume time is {} ms]===", (System.currentTimeMillis() - start));
        } catch (Exception e) {
            log.error("刷新数据字典后台作业发生异常,desc:{}", e);
            e.printStackTrace();
        }

    }

}
