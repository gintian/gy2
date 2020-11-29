package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.general.impev.DateUtil;
import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.RowSet;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
/**
 * <p>
 * Title:KqSyncFromWeChatJob
 * </p>
 * <p>
 * Description:微信签到数据同步,后台作业中执行的类，将企业微信中的考勤签到打卡数据复制到人力资源系统中
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2017-07-24
 * </p>
 * 
 * @author duxl
 * @version 1.0
 * 
 */

public class KqSyncFromWeChatJob implements Job {
    
    private Category cat = null;
    //打卡应用access_token
    private String access_token = "";
    //审批应用AccessToken
    private String AccessToken = "";
    //获取微信企业的userid列表
	ArrayList useridList = new ArrayList();
	//获取的企业微信数据集合
	private ArrayList weChatTimelist = new ArrayList();
	//获取的微信数据userid集合
	private ArrayList userIdlist = new ArrayList();

	//企业ID
	private String corpid = "";
	//打卡应用的凭证密钥
	private String corpsecret = "";
	//审批应用的凭证密钥
	private String approvalsecret = "";
	//开始时间
	private String start_date = "";
	//结束时间
	private String end_date = "";
	//hr人员与微信userid对应的指标
	private String userIdindex = "";
	
	// 需要获取的打卡数据类型   1：上下班打卡；2：外出打卡；3：全部打卡（默认）
	private String checkinType = "3";
	
	// 需求排除的异常打卡数据 异常类型，字符串，包括：时间异常，地点异常，未打卡，wifi异常，非常用设备。
	// 如果有多个异常，以分号间隔,或者“all”全部排除。注意：未打卡总是被排除指纹的，无论是否设置。
	private String exceptionTypes = "";
	// 如果exceptionTypes不等于all,那么将其分拆放到list中
	private ArrayList exceptionTypeList = new ArrayList();
	
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        // 作业类id
        String jobId = context.getJobDetail().getName();
        // 添加日志
        cat = Category.getInstance(KqSyncFromWeChatJob.class);
        Connection conn = null;
        try {
        	cat.info("开始同步企业微信打卡补卡数据...");
        	
        	// 人力资源系统的数据库连接
            conn = (Connection) AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            
            UserView userView = new UserView("su", conn);
            userView.canLogin(false);
            //错误信息
            String errorInfo = "";	
            KqUtilsClass kqUtilsClass = new KqUtilsClass(conn, userView);
            //库集合
            ArrayList preList = kqUtilsClass.getKqPreList();
            if(preList.size() <= 0){
            	cat.error("系统中未设置考勤人员库，默认同步在职人员库！");
            	preList.add("Usr");
            }
             
 			//获取考勤卡号指标方法：
 			KqParameter para = new KqParameter(userView, conn);
 			HashMap hashmap = para.getKqParamterMap();
 		    //卡号指标
 			String cardnoField = (String) hashmap.get("cardno");
 			if(StringUtils.isEmpty(cardnoField)){
 				errorInfo = "主集中未设置考勤卡号指标！";
            	cat.error(errorInfo);
            	throw new JobExecutionException(errorInfo);
            }

 			//取当前考勤期间
        	ArrayList durationlist = RegisterDate.getKqDayList(conn);
            if (durationlist == null || durationlist.size() <= 0) {
                 cat.info("没有定义考勤期间，取当前月份数据！");
                 String firstDay = DateUtil.getFirstDayOfMonth();
                 firstDay = firstDay.replaceAll("-", "\\.");
                 durationlist.add(firstDay);
            }
            //数据处理开始日期（默认为期间开始日期）当前考勤期间第一天
            start_date = (String) durationlist.get(0);
            Date s_date = DateUtils.getDate(start_date, "yyyy.MM.dd");
            //数据处理结束日期（默认为当天）
            end_date = PubFunc.getStringDate("yyyy.MM.dd");
            Date e_date = DateUtils.getDate(end_date, "yyyy.MM.dd");

            //没到期间开始日期
            if (e_date.before(s_date)) {
                 cat.info("没到期间开始日期，取当前月份数据！");
                 String firstDay = DateUtil.getFirstDayOfMonth();
                 start_date = firstDay.replaceAll("-", "\\.");
            }
            checkinType = getJobParaValue(dao, "checkin_type", jobId, "3");
            
            exceptionTypes = getJobParaValue(dao, "exception_type", jobId, "");
            createExceptionTypeList(exceptionTypes);
            
            /*通过第三方平台参数，获取企业ID*/
            getWeChat();
            
            //corpSecret必须使用打卡应用的Secret，所以必须从后台作业参数中获取
            corpsecret = getJobParaValue(dao, "corpsecret", jobId, "");
            //审批应用
            approvalsecret = getJobParaValue(dao, "approvalsecret", jobId, "");
            //hr人员与微信通讯录交集标识指标
            String userflag = getJobParaValue(dao, "userflag", jobId, "");
            //（微信同步入口不为hr系统时）
            userIdindex = getJobParaValue(dao, "wxqyid", jobId, "");
            
            if(StringUtils.isEmpty(userIdindex)){
            	userIdindex = getUserName();
            }
            
            if(StringUtils.isEmpty(corpid)){
            	corpid = getJobParaValue(dao, "corpid", jobId, "");
            }
            
            //未取到参数
            if(StringUtils.isNotEmpty(corpsecret)||StringUtils.isNotEmpty(approvalsecret)){
            	if(StringUtils.isEmpty(corpid)){
                	errorInfo = "未设置企业微信接口参数corpid！";
                	cat.error(errorInfo);
                	throw new JobExecutionException(errorInfo);
            	}
            }else if(StringUtils.isEmpty(corpsecret)&&StringUtils.isEmpty(approvalsecret)&&StringUtils.isEmpty(corpid)){
            	errorInfo = "未设置企业微信接口参数corpid、corpsecret和approvalsecret！";
            	cat.error(errorInfo);
            	throw new JobExecutionException(errorInfo);
            }else if(StringUtils.isEmpty(corpsecret)&&StringUtils.isEmpty(approvalsecret)&&StringUtils.isNotEmpty(corpid)){
            	errorInfo = "未设置企业微信接口参数corpsecret和approvalsecret！";
            	cat.error(errorInfo);
            	throw new JobExecutionException(errorInfo);
            }else{
            	cat.info("获取企业微信接口参数成功！");
            }
            //获取打卡应用accessToken
        	access_token = getAccessToken(corpid, corpsecret);
        	
        	//获取列表useridlist用户id
        	ArrayList useridList = getUseridList(dao,userflag,userIdindex,preList);

        	getUsridList(useridList);

        	//获取审批数据
        	if(StringUtils.isNotEmpty(approvalsecret)){
        		AccessToken = getAccessToken(corpid, approvalsecret);
        		getWeChatData(AccessToken , start_date, end_date, null, "2");
        	}
	        if(weChatTimelist.size() == 0){
	        	errorInfo = start_date+"-"+end_date+"未取到企业微信数据！";
	        	cat.error(errorInfo);
	        	throw new JobExecutionException(errorInfo);
	        }

	        cat.error("当前时间获取企业微信原始数据共"+weChatTimelist.size()+"条记录！");
	        //dingTakeTimelist 去重  同一分钟 的数据
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");  
	        for (int i=0 ;i<weChatTimelist.size()-1;i++){       
	            for (int j=weChatTimelist.size()-1;j>i;j--){  
	            	LazyDynaBean ldb1 = (LazyDynaBean) weChatTimelist.get(i);
	            	long workTime1 = (Long)ldb1.get("workTime")*1000;
	            	Date workDate1 = new Date(workTime1);
	            	String workTimeStr1 = sdf.format(workDate1);
	            	String userId1 = (String)ldb1.get("userId");
	            	
	            	LazyDynaBean ldb2 = (LazyDynaBean) weChatTimelist.get(j);
	            	long workTime2 = (Long)ldb2.get("workTime")*1000;
	            	Date workDate2 = new Date(workTime2);
	            	String workTimeStr2 = sdf.format(workDate2);
	            	String userId2 = (String)ldb2.get("userId");
	                if(workTimeStr2.equalsIgnoreCase(workTimeStr1) && userId2.equalsIgnoreCase(userId1)){       
	                	 weChatTimelist.remove(j); 
	                 }        
	              }        
	        }
	        int numDing = weChatTimelist.size();
	        cat.error("当前时间获取企业微信同步数据，按分钟去重后共"+weChatTimelist.size()+"条记录！");
        	
	        /*查询微信记录与原刷卡数据是否重复sql*/
	        StringBuffer kqsql = new StringBuffer();
	        kqsql.append(" select A0100 from kq_originality_data ");
	        kqsql.append(" where nbase=? ");
	        kqsql.append(" and A0100=? ");
	        kqsql.append(" and work_date=? ");
	        kqsql.append(" and work_time=? ");
	        
	        /*刷卡表添加记录sql*/
	        StringBuffer kqsqlInto = new StringBuffer();
	        kqsqlInto.append(" insert into kq_originality_data  ");
	        kqsqlInto.append(" (nbase,A0100,work_date,work_time,card_no,A0101,B0110,E0122,E01A1,location,sp_flag,inout_flag,datafrom,iscommon) ");
	        kqsqlInto.append(" values ");
	        kqsqlInto.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
	        
			//获取到微信userid集合
	        ArrayList userIdlistS = getWeChatUserIdList();
	        
	        /*循环userids集合并加入刷卡表*/
	        int num = 0;
	        int kqnum = 0;
	        for(int i=0;i<userIdlistS.size();i++){
	        	String userids = (String) userIdlistS.get(i);
	        	
	        	ArrayList kqInfolist = getKqInfolist(dao, preList, userids, cardnoField, userIdindex);       		
        		if(kqInfolist.size() == 0){
        			continue;
        		}
        		
        		num += kqInfolist.size();
        		kqnum += insertDataMain(dao, kqInfolist, kqsql.toString(), kqsqlInto.toString());
	        }
	        cat.info("获取微信有效打卡数据"+num+"条记录，未找到对应系统人员信息的刷卡数据有"+(numDing-num)+"条记录，系统已存在的刷卡数据有"+(num-kqnum)+"条记录。");
	        cat.info("成功同步微信有效打卡数据"+kqnum+"条记录。");
	        cat.info("完成微信签到打卡数据同步。");
        	
        } catch (JobExecutionException e) {
        	throw e;
        } catch (Exception e) {
        	e.printStackTrace();
        	cat.error("错误：{"+e.toString()+"}");
        } finally {
            PubFunc.closeDbObj(conn);
        }
    }
    
    private void createExceptionTypeList(String exceptions) {
        this.exceptionTypeList.clear();
        
        if (StringUtils.isEmpty(exceptions) || "all".equalsIgnoreCase(exceptions)) {
			return;
		}
        
        String[] arr = exceptions.split(";");
        for (int i=0; i<arr.length; i++) {
            this.exceptionTypeList.add(arr[i]);
        }
    }
    
    private boolean hasExceptionInList(String exceptions) {
        if (StringUtils.isEmpty(exceptions)) {
			return false;
		}
        
        if ("all".equalsIgnoreCase(this.exceptionTypes)) {
			return true;
		}
        
        String[] arr = exceptions.split(";");
        for (int i=0; i<arr.length; i++) {
            for (int j=0; j<this.exceptionTypeList.size(); j++) {
                if (arr[i].equalsIgnoreCase(this.exceptionTypeList.get(j).toString())) {
					return true;
				}
            }
        } 
        
        return false;
    }
    
    
    /**
     * 根据作业参数名取回参数值
     * @param conn
     * @param paramName 参数名称
     * @param jobId 作业类id
     * @return
     */
    private String getJobParaValue(ContentDAO dao, String paramName, String jobId, String defaultValue) {
        String returnValue = defaultValue;
        try {
            //从数据库读取
            RecordVo vo = new RecordVo("t_sys_jobs");
            vo.setInt("job_id", Integer.parseInt(jobId));
            vo = dao.findByPrimaryKey(vo);

            String jobParam = vo.getString("job_param");
            //没有设置参数
            if (null == jobParam) {
				return returnValue;
			}

            jobParam = jobParam.trim();
            //没有设置参数
            if ("".equals(jobParam)) {
				return returnValue;
			}

            //参数格式：param1=value1,param2=value2...
            //解析参数
            String[] params = jobParam.split(",");
            for (int i = 0; i < params.length; i++) {
                String[] param = params[i].split("=");
                if (2 != param.length) {
					continue;
				}

                if (!paramName.equalsIgnoreCase(param[0].trim())) {
					continue;
				}

                returnValue = param[1].trim();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            cat.error("错误：{"+e.toString()+"}");
        }

        return returnValue;
    }
    
    
    /**
     * 获取微信corpid 企业应用的凭证密钥
     * @return
     * @throws JobExecutionException 
     */
    private void getWeChat() throws JobExecutionException {
    	try {
			RecordVo recordVo = ConstantParamter.getConstantVo("SS_QQWX");
			if (recordVo != null) {
				Document doc = PubFunc.generateDom(recordVo.getString("str_value"));
				Element root = doc.getRootElement();
				List list = root.getChildren();
				for (int i = 0; i < list.size(); ++i) {
					Element child = (Element) list.get(i);
					String key = child.getAttributeValue("key");
					if("corpid".equals(key)){
						corpid = child.getAttributeValue("value");
					}
				}
				cat.info("从第三方平台参数获取企业微信接口参数,corpid:"+corpid);
			}else{
				throw new JobExecutionException("获取企业微信接口参数异常！");
			}
		} catch (JobExecutionException e) {
        	throw e;
        } catch (Exception e) {
			cat.error("获取企业微信接口参数异常,"+e.getMessage());
			e.printStackTrace();
		}
    }    
     
    /**
     * 求登录用户名字段
     * @return
     */
	private String getUserName() {
		String username = "";
		RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		if (login_vo == null) {
			username = "username";
		} else {
			String login_name = login_vo.getString("str_value").toLowerCase();
			int idx = login_name.indexOf(",");
			if (idx == -1) {
				username = "username";
			} else {
				username = login_name.substring(0, idx);
				if ("#".equals(username) || "".equals(username)) {
					username = "username";
				}
			}
		}
		return username;
	}
    
    /**
     * 获取企业微信access_token
     * @param corpid 企业Id
     * @param corpsecret 企业应用的凭证密钥
     * @return
     * @throws JobExecutionException 
     */
    private  String getAccessToken(String corpid, String corpsecret) throws JobExecutionException {
    	
    	String requestUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRECT";
    	requestUrl = requestUrl.replace("ID", corpid);
		requestUrl = requestUrl.replace("SECRECT", corpsecret);
    	JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
    	/**
    	 * 返回的jsonObject
    	 * 正确的："errcode": 0,"errmsg": "ok","access_token": "fw8ef8we8f76e6f7s8df8s","expires_in":7200
    	 * 错误的："errcode": 40091,"errmsg": "provider_secret is invalid"
    	 */
    	String accessToken = "";
    	//错误信息
        String errorInfo = "";
    	if (null != jsonObject)
		{
			try 
			{
				if(jsonObject.getInt("errcode") == 0){
					accessToken = jsonObject.getString("access_token");
				}else{
					errorInfo = "获取access_token失败 errcode:{"+ jsonObject.getInt("errcode")+"} errmsg:{"+ jsonObject.getString("errmsg")+"}";
					cat.error(errorInfo);
					throw new JobExecutionException(errorInfo);
				}
			}
			catch (JSONException e)
			{
				// 获取token失败
				errorInfo = "获取access_token失败 errcode:{"+ jsonObject.getInt("errcode")+"} errmsg:{"+ jsonObject.getString("errmsg")+"}";
				cat.error(errorInfo);
				throw new JobExecutionException(errorInfo);
			}
		}else{
			errorInfo = "获取access_token失败 ！";
			cat.error(errorInfo);
			throw new JobExecutionException(errorInfo);
		}
    	
		return accessToken;
    }
    
    
    /**
     * 获取企业微信useridlist
     * @return
     * @throws JobExecutionException 
     */    
    private ArrayList getUseridList(ContentDAO dao, String userflag,String wxqyid,ArrayList preList) throws JobExecutionException {
    	StringBuffer sql = new StringBuffer();
    	if(StringUtils.isNotEmpty(userflag)){
    	    for (int j=0; j<preList.size(); j++) {
    	        String pre = (String)preList.get(j);
    	        if(j > 0){
    	        	sql.append(" union all ");
    	        }    	       
    	        sql.append("select ").append(wxqyid).append(" as WXQY_id from ").append(pre).append("A01").append(" where ").append(userflag).append("  =  '1' ");
    	    }
    	    sql.append(" ORDER BY WXQY_id ASC");
    	}else{
        	sql.append("select WXQY_id from t_hr_view where WXQY = 0 ORDER BY A0100 ASC");
    	}
    	RowSet rs = null;
    	try{
    		 rs = dao.search(sql.toString()); 
    		 while (rs.next()) {
		        String userId = rs.getString("WXQY_id");
		        if(StringUtils.isNotBlank(userId)){
		        	useridList.add(userId);
		        }
    		 }
    	} catch (Exception e) {
            e.printStackTrace();
            cat.error("错误：{"+e.toString()+"}");
        } finally {
            PubFunc.closeDbObj(rs);
        }
    	return useridList;
    }	
    	
    
    /**
     * 每100个用户获取一次打卡数据
     * @param useridList 用户列表
     * @throws JobExecutionException
     */
     private  void getUsridList(ArrayList useridList) throws JobExecutionException {
    	try {
    		
    		int typeNum = 100; //每100个用户
	    	int usrNum = useridList.size();
	    	if(usrNum > typeNum){
	    		int num = usrNum/typeNum;
	    		int remainder = usrNum%typeNum;
	    		if(remainder > 0) {
					num = num + 1;
				}
	    		for(int i=0;i<num;i++){
	    			ArrayList usridList = new ArrayList();
	    			if(i==(num-1) && remainder > 0){
	    				for(int k=0;k<remainder;k++){
	    					String usrid = useridList.get(i*100+k).toString();
	    					usridList.add(usrid);
	    				}
	    				getWeChatData(access_token , start_date, end_date, usridList, "1");
	    			}else{
	    				for(int j=0;j<100;j++){
	    					String usrid = useridList.get(i*100+j).toString();
	    					usridList.add(usrid);
	    				}
	    				getWeChatData(access_token , start_date, end_date, usridList, "1");
	    			}
	    		
	    		}
	    	}else{
	        	//获取微信打卡数据
	        	getWeChatData(access_token , start_date, end_date, useridList, "1");
	    	}
    		
    	} catch (JobExecutionException e) {
        	throw e;
        }  catch (Exception e) {
			e.printStackTrace();
			cat.error("错误：{"+e.toString()+"}");
		}
     }
    
    /**
     * 获取企业微信打卡数据和审批补卡数据
     * @param access_token 
     * @param fromstr 开始日期 
     * @param tostr   结束日期 
     * @param useridList 用户列表
     * @throws JobExecutionException
     */
     private  void getWeChatData(String access_token, String fromstr, String tostr, ArrayList useridList, String type) throws JobExecutionException {
    	try {
    		
    		int typeNum = 30; //时间区间为一个月，所以每隔30天为一周期获取数据
	    	Date fromdt = DateUtils.getDate(fromstr, "yyyy.MM.dd");
	    	Date todt = DateUtils.getDate(tostr, "yyyy.MM.dd");
	    	int days = DateUtils.dayDiff(fromdt, todt);
	    	if(days > typeNum){
	    		int num = days/typeNum;
	    		int remainder = days%typeNum;
	    		if(remainder > 0) {
					num = num + 1;
				}
	    		String fromClockstr = fromstr;
	    		String toClockstr = "";
	    		for(int i=0;i<num;i++){
	    			if(i==(num-1) && remainder > 0){
	    				if("1".equals(type)){
	    					getClockdata(access_token, fromClockstr, tostr, useridList);
	    				}else if("2".equals(type)){
	    					getApprovaldata(access_token, fromClockstr, tostr, null);
	    				}
	    			}else{
	    				toClockstr = DateUtils.format(DateUtils.addDays(DateUtils.getDate(fromClockstr, "yyyy.MM.dd"), (typeNum-1)), "yyyy.MM.dd");
	    				if("1".equals(type)){
	    					getClockdata(access_token, fromClockstr, toClockstr, useridList);
	    				}else if("2".equals(type)){
	    					getApprovaldata(access_token, fromClockstr, toClockstr, null);
	    				}
	    				fromClockstr = DateUtils.format(DateUtils.addDays(DateUtils.getDate(toClockstr, "yyyy.MM.dd"), 1), "yyyy.MM.dd");
	    			}
	    		
	    		}
	    	}else{
	    		if("1".equals(type)){
	    			getClockdata(access_token, fromstr, tostr, useridList);
	    		}else if("2".equals(type)){
	    			getApprovaldata(access_token, fromstr, tostr, null);
	    		}	
	    	}
    		
    	} catch (JobExecutionException e) {
        	throw e;
        }  catch (Exception e) {
			e.printStackTrace();
			cat.error("错误：{"+e.toString()+"}");
		}
     }
    
       
     /**
      * 打卡数据
      * @param access_token 
      * @param fromstr 开始日期 
      * @param tostr   结束日期 
      * @param useridList 用户列表
      * @throws JobExecutionException
      */
     private  void getClockdata(String accessToken, String fromstr, String tostr, ArrayList useridList) throws JobExecutionException {
    	 try { 
	    	fromstr = fromstr+" 00:00";
	    	tostr = tostr+" 23:59";
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
	    	long froml = sdf.parse(fromstr).getTime()/1000;
	    	long tol = sdf.parse(tostr).getTime()/1000;
 			JSONObject Json = new JSONObject();
 			Json.put("opencheckindatatype", checkinType);
    	    Json.put("starttime", froml);
    	    Json.put("endtime", tol);
    	    Json.put("useridlist", useridList);
    	    String requestUrl = "https://qyapi.weixin.qq.com/cgi-bin/checkin/getcheckindata?access_token="+accessToken;
    	    JSONObject jsonObject = httpsRequest(requestUrl, "POST", Json.toString());
			if (null != jsonObject)
			{
				if(jsonObject.getInt("errcode") == 0){
					JSONArray signJsons = new JSONArray();
					signJsons = jsonObject.getJSONArray("checkindata");
					LazyDynaBean ldb = new LazyDynaBean();
					for(int i=0;i<signJsons.size();i++){
						ldb = new LazyDynaBean();
						JSONObject json = (JSONObject) signJsons.get(i);
						
						String exceptionTypeJson = json.getString("exception_type");
						if ("未打卡".equals(exceptionTypeJson)) {
							continue;
						}
						
						// 需要排除的其它异常
						if (this.hasExceptionInList(exceptionTypeJson)) {
							continue;
						}
						
						String location_address = json.getString("location_detail");
						if(StringUtils.isEmpty(location_address)){
							location_address = json.getString("location_title"); 
						}
						ldb.set("userId", json.getString("userid"));
						ldb.set("workTime", json.getLong("checkin_time"));
						ldb.set("address", location_address);
						ldb.set("exceptionType", exceptionTypeJson);
						userIdlist.add(json.getString("userid"));
						weChatTimelist.add(ldb);
					}
				}else{
					//错误信息
		            String errorInfo = "获取企业微信打卡数据失败errcode:{"+ jsonObject.getInt("errcode")+"} errmsg:{"+ jsonObject.getString("errmsg")+"}";
					cat.error(errorInfo);
					throw new JobExecutionException(errorInfo);
				}
			}
    	    
    	 } catch (JobExecutionException e) {
         	throw e;
    	    //json.toString();
         }  catch (Exception e) {
 			e.printStackTrace();
 			cat.error("错误：{"+e.toString()+"}");
 		}
    	 
     }
     
     /**
      * 审批补卡数据
      * @param access_token 
      * @param fromstr 开始日期 
      * @param tostr   结束日期 
      * @param next_spnum 拉取列表的最后一个审批单号
      * @throws JobExecutionException
      */
     private  void getApprovaldata(String accessToken, String fromstr, String tostr, Integer next_spnum) throws JobExecutionException {
    	 try { 
	    	fromstr = fromstr+" 00:00";
	    	tostr = tostr+" 23:59";
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
	    	long froml = sdf.parse(fromstr).getTime()/1000;
	    	long tol = sdf.parse(tostr).getTime()/1000;
 			JSONObject Json = new JSONObject();
    	    Json.put("starttime", froml);
    	    Json.put("endtime", tol);
    	    Json.put("next_spnum", next_spnum);
    	    String requestUrl = "https://qyapi.weixin.qq.com/cgi-bin/corp/getapprovaldata?access_token="+accessToken;
    	    JSONObject jsonObject = httpsRequest(requestUrl, "POST", Json.toString());
			if (null != jsonObject)
			{
				if(jsonObject.getInt("errcode") == 0){
					JSONArray signJsons = new JSONArray();
					signJsons = jsonObject.getJSONArray("data");
					LazyDynaBean ldb = new LazyDynaBean();
					for(int i=0;i<signJsons.size();i++){
						JSONObject json = (JSONObject) signJsons.get(i);
						if("打卡补卡".equals(json.getString("spname"))||"补卡".equals(json.getString("spname"))){
							if(json.getInt("sp_status")==2){
								JSONObject comm =json.getJSONObject("comm");
								String apply_data = comm.getString("apply_data");
								JSONObject applyData = JSONObject.fromObject(apply_data);
								JSONObject begin_time = applyData.getJSONObject("begin_time");
								JSONObject end_time = applyData.getJSONObject("end_time");
								if("开始时间".equals(begin_time.getString("title"))){
									ldb = new LazyDynaBean();
									long workBegin_time =Long.parseLong(begin_time.getString("value"))/1000;
									ldb.set("userId", json.getString("apply_user_id"));
									ldb.set("workTime", workBegin_time);
									ldb.set("address", "补卡数据");
									userIdlist.add(json.getString("apply_user_id"));
									weChatTimelist.add(ldb);
								}
								if("结束时间".equals(end_time.getString("title"))){
									ldb = new LazyDynaBean();
									long workEnd_time =Long.parseLong(end_time.getString("value"))/1000;
									ldb.set("userId", json.getString("apply_user_id"));
									ldb.set("workTime", workEnd_time);
									ldb.set("address", "补卡数据");
									userIdlist.add(json.getString("apply_user_id"));
									weChatTimelist.add(ldb);
								}
			
							}
						}
					}
	    			if(signJsons.size()==10000){
	    				next_spnum = jsonObject.getInt("next_spnum");
	    				getApprovaldata(accessToken, fromstr, tostr, next_spnum);
	    			}
				}else{
					//错误信息
		            String errorInfo = "获取企业微信补卡数据失败errcode:{"+ jsonObject.getInt("errcode")+"} errmsg:{"+ jsonObject.getString("errmsg")+"}";
					cat.error(errorInfo);
					throw new JobExecutionException(errorInfo);
				}
			}
    	    
    	 } catch (JobExecutionException e) {
         	throw e;
    	    //json.toString();
         }  catch (Exception e) {
 			e.printStackTrace();
 			cat.error("错误：{"+e.toString()+"}");
 		}
    	 
     }
     
     /**
      * 获取到微信userid集合，为查主集对应指标 去除重复userID 
      * 
      * @return userIdlist
      */
     private ArrayList getWeChatUserIdList(){
     	
     	if(userIdlist.size() == 0){
     		cat.error("未取到企业微信数据中userId！");
     		return new ArrayList();
     	}
     	//去除重复userId 
     	for(int i=0 ;i<userIdlist.size()-1;i++){       
             for(int j=userIdlist.size()-1;j>i;j--){  
             	String userId1 = (String) userIdlist.get(i);
             	String userId2 = (String) userIdlist.get(j);
                  if(userId2.equals(userId1)){       
                 	 userIdlist.remove(j);       
                   }        
               }        
         }
     	
     	//** 每次查询oracle不能超过1000条，拼接时，1000条为一个字符串
 		StringBuffer userIds = new StringBuffer();
 		int n = 1;
 		ArrayList userIdlistS = new ArrayList();
         for(int i=0;i<userIdlist.size();i++){
         	String userId = (String) userIdlist.get(i);
         	if(!StringUtils.isEmpty(userIds.toString())){
         		userIds.append(",");
         	}
         	userIds.append( "'").append(userId).append("'");
         	if(i == 999*n){
         		userIdlistS.add(userIds.toString());
         		n++;
         		userIds = new StringBuffer();
         	}
         }
         
         if(!StringUtils.isEmpty(userIds.toString())) {
			 userIdlistS.add(userIds.toString());
		 }
         
     	return userIdlistS;
     }
     
     /**
      * 获取人员基本信息 A0100,A0101,B0110,E0122,E01A1...
      * @param dao 
      * @param preList 考勤库集合
      * @param userIds	对应主集的userID数据  'userID','userID','userID','userID'...
      * @paramcardnoField 	考勤卡号对应指标
      * @param userIdindex	userID对应指标
      * @return
      */
     private ArrayList getKqInfolist(ContentDAO dao, ArrayList preList, String userIds, String cardnoField, String userIdindex) throws JobExecutionException {
     	StringBuffer sql = new StringBuffer();
     	ArrayList kqInfolist = new ArrayList();
	    for (int j=0; j<preList.size(); j++) {
	        String pre = (String)preList.get(j);
	        if(j > 0){
	        	sql.append(" union all ");
	        }
	        sql.append("SELECT A0100,A0101,B0110,E0122,E01A1,").append(cardnoField).append(" as cardno,").append(userIdindex).append(" as userId,'"+pre+"' as nbase");
	        sql.append(" FROM ").append(pre).append("A01");
	        sql.append(" where ").append(userIdindex).append(" in (").append(userIds).append(") ");
	       
	    }
 	    RowSet rs = null;
 	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");  
     	try{
 	    	rs = dao.search(sql.toString());
 		    ArrayList weChatTimelistC = weChatTimelist;
 			while(rs.next()){
 				
 				String A0100 = rs.getString("A0100");
 				String nbase = rs.getString("nbase");
 				String userId = rs.getString("userId");
 				String A0101 = rs.getString("A0101");
 				A0101 = StringUtils.isEmpty(A0101) ? "null" : A0101;
 				String B0110 = rs.getString("B0110");
 				B0110 = StringUtils.isEmpty(B0110) ? "null" : B0110;
 				String E0122 = rs.getString("E0122");
 				E0122 = StringUtils.isEmpty(E0122) ? "null" : E0122;
 				String E01A1 = rs.getString("E01A1");
 				E01A1 = StringUtils.isEmpty(E01A1) ? "null" : E01A1;
 				String cardno = rs.getString("cardno");
 				cardno = StringUtils.isEmpty(cardno) ? " " : cardno;
 				
 				for(int i=weChatTimelistC.size()-1;i>=0;i--){
 					LazyDynaBean ldbC = (LazyDynaBean) weChatTimelistC.get(i);
 		        	String userIdc = (String) ldbC.get("userId");
 		        	if(!userId.equalsIgnoreCase(userIdc)) {
						continue;
					}
 		        	
 		        	String exceptionType =  (String) ldbC.get("exceptionType");
 		        	String address = (String) ldbC.get("address");
 		        	address = PubFunc.splitString(address, 200);
 		        	long timeLong = (Long) ldbC.get("workTime")*1000;
 		        	Date workDate = new Date(timeLong);
 		        	String workTimeStr = sdf.format(workDate);
 		        	String date = "";
 		    		String time = "";
 		        	if((workTimeStr.trim()).indexOf(" ") > -1){
 		        		date = workTimeStr.split(" ")[0];
 		        		time = workTimeStr.split(" ")[1];
 		        	}else{
 		        		cat.error("userId为："+userId+"，签到时间转换错误！获取的时间为"+timeLong+"。");
 		        		continue;
 		        	}
 		        	
 		        	LazyDynaBean ldb = new LazyDynaBean();
 					ldb.set("A0100", A0100);
 					ldb.set("A0101", A0101);
 					ldb.set("B0110", B0110);
 					ldb.set("E0122", E0122);
 					ldb.set("E01A1", E01A1);
 					ldb.set("userId", userId);
 					ldb.set("nbase", nbase);
 					ldb.set("cardno", cardno);
 		        	ldb.set("work_date", date);
 		        	ldb.set("work_time", time);
 		        	ldb.set("location", "企业微信："+address);
 		        	ldb.set("exceptionType", exceptionType);
 		        	kqInfolist.add(ldb);
 		        	
 		        	weChatTimelistC.remove(i);
 				}
 			}
         } catch (Exception e) {
             e.printStackTrace();
             cat.error("错误：{"+e.toString()+"}");
         } finally {
             PubFunc.closeDbObj(rs);
         }
 		return kqInfolist;
     }
     
     /**
      * 拼装考勤数据，并往考勤表增加记录
      * @param dao
      * @param kqInfolist
      * @param kqsql
      * @param kqsqlInto
      */
     private int insertDataMain(ContentDAO dao, ArrayList kqInfolist, String kqsql, String kqsqlInto) throws JobExecutionException {
     	
     	RowSet kqrs = null;
     	int num = 0;
     	try{
     		for(int i=kqInfolist.size()-1;i>=0;i--){    		
 				LazyDynaBean ldb = (LazyDynaBean) kqInfolist.get(i);
 				String nbase = (String) ldb.get("nbase");
 				String A0100 = (String) ldb.get("A0100");
 				String work_date = (String) ldb.get("work_date");
 				String work_time = (String) ldb.get("work_time");
 				String exceptionType = (String) ldb.get("exceptionType");
 				if(StringUtils.isEmpty(nbase) || StringUtils.isEmpty(A0100)){
 					kqInfolist.remove(i);
 					continue;
 				}
 				ArrayList kqWherelist = new ArrayList();
 				kqWherelist.add(nbase);
 				kqWherelist.add(A0100);
 				kqWherelist.add(work_date);
 				kqWherelist.add(work_time);
 				
 				kqrs = dao.search(kqsql.toString(), kqWherelist);
 				if(kqrs.next()){
 					continue;
 				}
 				
 				String A0101 = (String) ldb.get("A0101");
 				A0101 = "null".equalsIgnoreCase(A0101) ? null : A0101;
 				String B0110 = (String) ldb.get("B0110");
 				B0110 = "null".equalsIgnoreCase(B0110) ? null : B0110;
 				String E0122 = (String) ldb.get("E0122");
 				E0122 = "null".equalsIgnoreCase(E0122) ? null : E0122;
 				String E01A1 = (String) ldb.get("E01A1");
 				E01A1 = "null".equalsIgnoreCase(E01A1) ? null : E01A1;
 				//正常出勤点签到
 				String iscommon = getIscommon(exceptionType);
 				
 				ArrayList kqIntolist = new ArrayList();
 				kqIntolist.add(nbase);
 				kqIntolist.add(A0100);
 				kqIntolist.add(work_date);
 				kqIntolist.add(work_time);
 				kqIntolist.add((String) ldb.get("cardno"));
 				kqIntolist.add(A0101);
 				kqIntolist.add(B0110);
 				kqIntolist.add(E0122);
 				kqIntolist.add(E01A1);
 				kqIntolist.add((String) ldb.get("location"));
 				kqIntolist.add("03");
 				kqIntolist.add(0);
 				kqIntolist.add(0);
 				kqIntolist.add(iscommon);
 				num += dao.insert(kqsqlInto.toString(), kqIntolist);
 			}
     	} catch (Exception e) {
             e.printStackTrace();
             cat.error("错误：{"+e.toString()+"}");
         } finally {
             PubFunc.closeDbObj(kqrs);
         }
         return num;
     }
     
    /**
     * 异常打卡判断iscommon字段为否 
     * @param exceptionType
     * @return
     */
    private String getIscommon(String exceptionType) {
        if (StringUtils.isEmpty(exceptionType)){
        	 return "1";
        }
    	String exceptions = "时间异常;地点异常;未打卡;wifi异常;非常用设备";
    	String[] exceptionsArr = exceptions.split(";");
        String[] exceptionTypeArr = exceptionType.split(";");
        for (int i=0; i<exceptionTypeArr.length; i++) {
            for (int j=0; j<exceptionsArr.length; j++) {
                if (exceptionTypeArr[i].equalsIgnoreCase(exceptionsArr[j])) {
					return "0";
				}
            }
        } 
		return "1";
	}

	/**
     * 发送请求  获取数据
     * @param requestUrl 路径
     * @param requestMethod 传送方式get | post 
     * @param outputStr 传送数据
     * @return jsonObject
     */
	private   JSONObject httpsRequest(String requestUrl,String requestMethod, String outputStr) throws JobExecutionException{
		JSONObject jsonObject = null;
		try {
			TrustManager tm[] = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new SecureRandom());
			javax.net.ssl.SSLSocketFactory ssf = sslContext.getSocketFactory();
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(requestMethod);
			if("POST".equalsIgnoreCase(requestMethod)){
				conn.setRequestProperty("Content-Type", "application/json");
			}
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (ConnectException ce) {
			cat.error("连接超时：{}", ce);
		} catch (Exception e) {
			cat.error("https请求异常：{}", e);
		}
		return jsonObject;
	}
	
	public class MyX509TrustManager implements X509TrustManager {

		public MyX509TrustManager() {
		}

		@Override
        public void checkClientTrusted(X509Certificate ax509certificate[], String s)
				throws CertificateException {
		}

		@Override
        public void checkServerTrusted(X509Certificate ax509certificate[], String s)
				throws CertificateException {
		}

		@Override
        public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

}
