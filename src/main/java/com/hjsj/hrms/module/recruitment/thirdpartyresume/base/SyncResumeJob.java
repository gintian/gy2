package com.hjsj.hrms.module.recruitment.thirdpartyresume.base;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
/**
 * 第三方简历导入后台作业类
 * @Title:        SyncResumeJob.java
 * @Description:  用于导入第三方简历，同步应聘人员简历信息
 * @Company:      hjsj     
 * @Create time:  2017-7-1 下午03:31:54
 * @author        chenxg
 * @version       1.0
 */
public class SyncResumeJob implements Job{

    private int days = 1;
    private String thirdPartyName = "all";
    private Category cat = null;
    private Connection conn = null;
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try{
            // 添加日志
            cat = Category.getInstance(SyncResumeJob.class);
            // 作业类id
            String jobId = arg0.getJobDetail().getName();
            this.conn = AdminDb.getConnection();
            //获取作业参数
            parseJobParam(conn, jobId);
            
            ThirdPartyResumeSourceFactory ThirdPartyResumeInfo = new ThirdPartyResumeSourceFactory();
            ArrayList<HashMap<String, String>> ThirdPartyInfo = ThirdPartyResumeInfo.getThirdPartyResumeSources();
            //当配置的第三方为all时，导入全部有效的第三方简历
            if("all".equalsIgnoreCase(this.thirdPartyName)) {
                this.thirdPartyName = "";
                for(int i = 0; i < ThirdPartyInfo.size(); i ++){
                    HashMap<String, String> infoMap = ThirdPartyInfo.get(i);
                    String valid = infoMap.get("valid");
                    if("0".equalsIgnoreCase(valid))
                        continue;
                    
                    this.thirdPartyName += infoMap.get("name") + ",";
                }
                
            }
            
            UserView userView = new UserView("su", this.conn);
            userView.canLogin(false);
            StringBuffer showInfor = new StringBuffer();
            String[] thirdPartyNames = this.thirdPartyName.split(",");
            for(int i = 0; i < thirdPartyNames.length; i++){
                if(StringUtils.isEmpty(thirdPartyNames[i]))
                    continue;
                
                HashMap<String, String> map = new HashMap<String, String>();
                if("BeiSen".equalsIgnoreCase(thirdPartyNames[i])){
                    Date endDate = new Date();
                    //计算开始时间
                    Calendar calendar = Calendar.getInstance();   
                    calendar.setTime(endDate); 
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-days);  
                    Date startDate = calendar.getTime();
                    String start = DateUtils.format(startDate, "yyyy-MM-dd hh:mm:ss");
                    String end = DateUtils.format(endDate, "yyyy-MM-dd hh:mm:ss");
                    
                    map.put("startDate", start);
                    map.put("endDate",  end);
                }
                
                Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
                String blacklist_field = sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST, "field");
                map.put("blacklist_field", blacklist_field);
                ThirdPartyResumeBase base = ThirdPartyResumeSourceFactory.getThirdPartyResumeBo(this.conn, thirdPartyNames[i], userView);
                base.getResumeFromThirdParty(map);
                // 导入简历提示信息
                String showInforLog = base.getShowInforLog();
                //解析不正确的文件
                String FlistLog = base.getFlistLog();
                //以下记录人员库中已存在
                String PlistLog = base.getPlistLog();
                //以下记录在黑名单库中存在
                String blackLog = base.getBlacklistLog();
                
                int m = 0;
                showInfor.append(showInforLog + "<br>");
                
                if(StringUtils.isNotEmpty(blackLog)){
                    m = m + 1;
                    showInfor.append(m + "、以下记录在黑名单库中存在,不能导入:<br>");
                    showInfor.append(blackLog + "<br>");
                }
                
                if(StringUtils.isNotEmpty(PlistLog)){
                    m = m + 1;
                    showInfor.append(m + "、以下记录人员库中已存在,不能导入:<br>");
                    showInfor.append(PlistLog + "<br>");
                }
                
                if(StringUtils.isNotEmpty(FlistLog)){
                    m = m + 1;
                    showInfor.append(m + "、以下人员的简历解析不对或缺失关键信息,不能导入:<br>");
                    showInfor.append(FlistLog + "<br>");
                }
            }
            //将信息输出到日志中
            cat.error(showInfor.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(conn);
        }
    }
    /**
     * 获取作业参数
     * @param conn 数据库链接
     * @param jobId 作业类id
     */
    private void parseJobParam(Connection conn, String jobId) {
        String params = "";
        ResultSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search("select job_param from t_sys_jobs where job_id=" + jobId);
            if (rs.next())
                params = rs.getString("job_param");
        } catch (Exception e) {
            cat.error("job_param字段是t_sys_jobs表中新增字段，字段类型为text；如果没有设置此参数，可忽略此错误！");
        } finally {
            PubFunc.closeResource(rs);
        }
            
        if (null == params || "".equals(params))
            return;
            
        try {
            String[] paramArray = params.split(";");
            for (int i = 0; i < paramArray.length; i++) {
                String[] param = paramArray[i].split("=");
                if ("source".equalsIgnoreCase(param[0])) {
                    this.thirdPartyName = StringUtils.isEmpty(param[1].trim()) ? "all" : param[1].trim();
                } else if ("days".equalsIgnoreCase(param[0])) {
                    String day = StringUtils.isEmpty(param[1].trim()) ? "1" : param[1].trim();
                    if (day != null) {
                        try {
                            days = Integer.valueOf(day) ;
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
