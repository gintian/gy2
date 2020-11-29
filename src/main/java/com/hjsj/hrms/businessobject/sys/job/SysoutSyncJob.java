package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class SysoutSyncJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        // 作业类id
        String jobId = context.getJobDetail().getName();

        Connection conn = null;
        RowSet rs = null;
        try {
            conn = (Connection) AdminDb.getConnection();
            HrSyncBo hsb = new HrSyncBo(conn);
            String sync_mode = hsb.getAttributeValue(HrSyncBo.SYNC_MODE);
            if (sync_mode == null || !"trigger".equalsIgnoreCase(sync_mode)) {
                return;
            }
            String hr_only_field = hsb.getAttributeValue(HrSyncBo.HR_ONLY_FIELD);//唯一标示
//			if(hr_only_field==null||hr_only_field.length()<=0)
//			{
//				Category.getInstance("com.hjsj.hrms.businessobject.sys.job.SysoutSyncJob").error("没有设置人员唯一指标");
//				return;
//			}
            /** 判断人员同步 */
            String sync_A01 = hsb.getAttributeValue(HrSyncBo.SYNC_A01);
            sync_A01 = sync_A01 != null && sync_A01.trim().length() > 0 ? sync_A01 : "0";
            /** 判断单位同步 */
            String sync_B01 = hsb.getAttributeValue(HrSyncBo.SYNC_B01);
            sync_B01 = sync_B01 != null && sync_B01.trim().length() > 0 ? sync_B01 : "0";
            /**newAdd 判断岗位同步 */
            String sync_K01 = hsb.getAttributeValue(HrSyncBo.SYNC_K01);
            sync_K01 = sync_K01 != null && sync_K01.trim().length() > 0 ? sync_K01 : "0";

            String fail_time = hsb.getAttributeValue(HrSyncBo.FAIL_LIMIT);
            StringBuffer sql = new StringBuffer();
            sql.append("select * from t_sys_outsync where state=1");
//			if(fail_time!=null&&fail_time.trim().length()>0){
//				sql.append(" and fail_time<"+fail_time);
            String sync_data_addr = SystemConfig.getPropertyValue("hr_data_addr");
            String sync_data_post = SystemConfig.getPropertyValue("hr_data_post");
            String sync_user = SystemConfig.getPropertyValue("hr_sync_user");
            String sync_pass = SystemConfig.getPropertyValue("hr_sync_pass");
            String sync_base = SystemConfig.getPropertyValue("hr_data_base");
            String sync_baseType = SystemConfig.getPropertyValue("dbserver");
            String sync_emp_table = SystemConfig.getPropertyValue("sync_emp_table");
            String sync_org_table = SystemConfig.getPropertyValue("sync_org_table");
            String sync_post_table = SystemConfig.getPropertyValue("sync_post_table");//newAdd

            DbWizard dbwd = new DbWizard(conn);
            boolean isSend = false;
            if (dbwd.isExistField("t_sys_outsync", "send", false)) {
                isSend = true;
            }
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString());
            ArrayList list = new ArrayList();
            while (rs.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("sys_id", rs.getString("sys_id"));
                bean.set("url", clearUrl_WSDL(rs.getString("url")));
                bean.set("sys_name", rs.getString("sys_name"));
                bean.set("sync_method", rs.getString("sync_method"));
                String tar = rs.getString("targetNamespace");
                if (tar == null) {
                    tar = "";
                }

                try {
                    // 错误次数
                    String erroTimes = rs.getString("fail_time");
                    if (fail_time != null && fail_time.trim().length() > 0) {
//						sql.append(" and fail_time<"+fail_time);
                        if (Integer.parseInt(erroTimes) > Integer.parseInt(fail_time)) {
                            System.out.println("系统代号为" + rs.getString("sys_id") + "的系统：" + rs.getString("sys_name") + ",同步错误次数已达到允许范围最大值，同步已停止，请检查数据！");
                            continue;
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                bean.set("targetNamespace", tar);
                if (isSend) {

                    String other_param = rs.getString("other_param");
                    if (other_param == null) {
                        other_param = "";
                    }

                    String control = rs.getString("control");
                    control = control == null ? "" : control;

                    String send = rs.getString("send");
                    send = send == null ? "" : send;

                    bean.set("send", send);//new 是否发送
                    bean.set("control", control);//new 那些类型需要发送
                    bean.set("other_param", other_param);//new 发送的过滤条件
                }
                list.add(bean);
            }
            for (int i = 0; i < list.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) list.get(i);
                bean.set("sync_data_addr", sync_data_addr);
                bean.set("sync_data_post", sync_data_post);
                bean.set("sync_user", sync_user);
                bean.set("sync_pass", sync_pass);
                bean.set("sync_base", sync_base);
                bean.set("sync_baseType", sync_baseType);
                bean.set("org_table", sync_org_table);
                bean.set("emp_table", sync_emp_table);
                bean.set("post_table", sync_post_table);
                bean.set("sync_B01", sync_B01);
                bean.set("sync_A01", sync_A01);
                bean.set("sync_K01", sync_K01);
                if (isSend && "0".equals(bean.get("send")))//new 是否发送0=不发送
                {
                    continue;
                }

                String other_param = bean.get("other_param").toString();
                PareXmlUtils utils = new PareXmlUtils(other_param);
                String jobIdSet = utils.getTextValue("/params/jobId");

                if (jobIdSet != null && jobIdSet.trim().length() > 0 && (!jobId.equalsIgnoreCase(jobIdSet))) {
                    continue;
                }
				
				
				/*SysoutSyncThread syncthread=new SysoutSyncThread(bean,hr_only_field);//通过线程发送消息
				Thread t = new Thread(syncthread);
				t.start();*/
                SysoutSyncBridge sysoutsyncbridge = new SysoutSyncBridge(bean, hr_only_field);
                String errorMsg = sysoutsyncbridge.run();
                if (StringUtils.isNotEmpty(errorMsg)) {
                    throw new JobExecutionException("同步增量数据至kafka出错!" + errorMsg);
                }
            }

        } catch (JobExecutionException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
			throw new JobExecutionException("同步增量数据至kafka出错!" + e.getMessage());
		} finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private String clearUrl_WSDL(String url) {
        String trim_url = url.trim();
        if (trim_url != null && trim_url.indexOf("?wsdl") != -1) {
            url = trim_url.substring(0, trim_url.indexOf("?wsdl"));
        }
        return url;
    }

}
