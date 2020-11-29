package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.service.syncdata.SyncDataService;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.sql.Connection;

public class SyncNoticeToDataInterfeceJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Connection connHr = null;
        try {
            connHr = AdminDb.getConnection();
            ContentDAO daoHr = new ContentDAO(connHr);
            /*获取后台作业配置的参数*/
            RecordVo vo = new RecordVo("t_sys_jobs");
            String jobId = context.getJobDetail().getName();
            vo.setInt("job_id", Integer.parseInt(jobId));
            vo = daoHr.findByPrimaryKey(vo);
            String jobParam = vo.getString("job_param");/*作业参数*/
            String[] recs = jobParam.split(",");
            String emp = recs[0].substring(recs[0].lastIndexOf("=") + 1);
            String org = recs[1].substring(recs[1].lastIndexOf("=") + 1);
            String post = recs[2].substring(recs[2].lastIndexOf("=") + 1);
            String targetNamespace = "http://WebXml.com.cn/";
            String methodName = "sendSyncJobMsg";
            String paramName = "xmlMessage";

            RecordVo outSync = new RecordVo("t_sys_outsync");
            outSync.setString("sys_id", "KAFKA");
            outSync = vo = daoHr.findByPrimaryKey(outSync);
            String[] split = outSync.getString("url").split("\\?");//?在正则表达示中有相应的不同意义，所以在使用时要进行转义处理。
            String url = "";
            if (split.length == 2) {
                url = split[0];
            } else {
                url = "http://172.31.61.18:8081/data_interface/services/SyncData";
            }
            StringBuffer xml = new StringBuffer();
            xml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?><hr><recs>");
            //根据作业参数判断是否全量同步人员、机构和岗位
            if ("true".equalsIgnoreCase(emp)) {
                xml.append("<rec>emp</rec>");
            }
            if ("true".equalsIgnoreCase(org)) {
                xml.append("<rec>org</rec>");
            }
            if ("true".equalsIgnoreCase(post)) {
                xml.append("<rec>post</rec>");
            }

            xml.append("</recs><jdbc><sysid>KAFKA</sysid><ip_addr>192.192.102.250</ip_addr><port>1433</port><username>sa</username><pass>yksoft1919</pass><database>gqys</database><datatype>mssql</datatype><emp_table>t_hr_view</emp_table><org_table>t_org_view</org_table><post_table>t_post_view</post_table><emp_where><![CDATA[]]></emp_where><org_where><![CDATA[]]></org_where><post_where><![CDATA[]]></post_where>");

            xml.append("<kafka>complete</kafka>");
            xml.append("</jdbc></hr>");

            String errorMsg = getMessage(xml.toString());
            if (StringUtils.isNotEmpty(errorMsg)) {
                throw new JobExecutionException("同步全量数据至kafka出错!" + errorMsg);
            }
        } catch (JobExecutionException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new JobExecutionException("同步全量数据至kafka出错!");
        } finally {
            PubFunc.closeDbObj(connHr);
        }

    }

    /**
     * 根据空间名和参数名获得webservice接口返回的数据（解决调用.net的webservice时出错的问题）
     *
     * @return 调用webservice返回的结果
     */
    private String getMessage(String paramValue) {
        String mess = null;

        try {
            SyncDataService SyncDataService = new SyncDataService();
            mess = SyncDataService.sendSyncJobMsg(paramValue);

        } catch (Exception e) {
            mess = e.getMessage();
            e.printStackTrace();
        }
        return mess;

    }

}
