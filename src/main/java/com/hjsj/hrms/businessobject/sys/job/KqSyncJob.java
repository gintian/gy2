package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.param.DocumentSyncBo;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import org.apache.log4j.Category;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title:KqSyncJob
 * </p>
 * <p>
 * Description:后台作业中执行的类，将其他系统数据库的信息复制到人力资源系统中
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-12-16
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class KqSyncJob implements Job {
    
    private String dateScope = "";
    //zxj 20150929 后台作业参数“同步天数”，意义与数据处理后台作业一致，表示往前N天
    private int days = 0;
    private String mailto = "";
    private Category cat = null;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        // 作业类id
        String jobId = context.getJobDetail().getName();
        // 添加日志
        cat = Category.getInstance(KqSyncJob.class);
        Connection conn = null;
        try {
            // 人力资源系统的数据库连接
            conn = (Connection) AdminDb.getConnection();
            
            //解析作业参数
            parseJobParam(conn, jobId);
            
            Date c_date = new Date();
            String curr = DateUtils.format(c_date, "yyyy.MM.dd");
            String start = curr;
            String end = curr;

            // 同步时间范围控制 目前仅支持参数all，表示从考勤期间开始日期到当天
            if ("all".equalsIgnoreCase(dateScope) || -1 >= days) {
                List list = RegisterDate.getKqDayList(conn);
                if (list.size() == 0 || list == null) {
                    cat.error(ResourceFactory.getProperty("kq.register.session.nosave"));
                } else {
                    start = (String) list.get(0);
                }
            } else if (days > 0) {
                Date date = OperateDate.addDay(c_date, 0 - days);
                start = DateUtils.format(date, "yyyy.MM.dd");
            }

            DocumentSyncBo bo = new DocumentSyncBo(conn);
            bo.setMailto(mailto);
            bo.sync(start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭数据库连接
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void parseJobParam(Connection conn, String jobId) {
        String params = "";
        
        ResultSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search("select job_param from t_sys_jobs where job_id=" + jobId);
            if (rs.next()) {
                params = rs.getString("job_param");
            }
        } catch (Exception e) {
            cat.error("job_param字段是t_sys_jobs表中新增字段，字段类型为text；如果没有设置此参数，可忽略此错误！");
        } finally {
            try {
                if (null != rs) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            
        if (null == params || "".equals(params)) {
            return;
        }
            
        try {
            String[] paramArray = params.split(";");
            for (int i = 0; i < paramArray.length; i++) {
                String param = paramArray[i];
                if ("all".equalsIgnoreCase(param)) {
                    dateScope = "all";
                } else if (param.toLowerCase().startsWith("mailto")) {
                    mailto = param.substring(7);
                } else if (param.toLowerCase().startsWith("days")) {
                    String day = param.substring(5);
                    if (day != null) {
                        try {
                            days = Integer.valueOf(day);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
