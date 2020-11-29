package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.general.impev.DateUtil;
import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
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
 * Title:KqSyncFromDingTalkJob
 * </p>
 * <p>
 * Description:钉钉签到数据同步,后台作业中执行的类，将钉钉中的考勤签到打卡数据复制到人力资源系统中
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2017-03-23
 * </p>
 * 
 * @author linbz
 * @version 1.0
 * 
 */
public class KqSyncFromDingTalkJob implements Job {
    
    private Category cat = null;
    
    private String accessToken = "";
	//获取的钉钉数据集合
	private ArrayList dingTakeTimelist = new ArrayList();
	//获取的钉钉数据userid集合
	private ArrayList userIdlist = new ArrayList();
	//企业ID
	private String corpid = "";
	//企业应用的凭证密钥
	private String corpSecret = "";
	//人员ID
	private String userID = "";
	//钉钉发送消息&同步配置&考勤 参数
	private String msg_AppKey = "";
	//钉钉发送消息&同步配置&考勤 参数
	private String msg_AppSecret = "";
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        // 作业类id
        String jobId = context.getJobDetail().getName();
        // 添加日志
        cat = Category.getInstance(KqSyncFromDingTalkJob.class);
        Connection conn = null;
        try {
        	cat.info("开始同步钉钉签到打卡数据...");
        	
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
            	cat.info("系统中未设置考勤人员库，默认同步在职人员库！");
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
            String start_date = (String) durationlist.get(0);
            Date s_date = DateUtils.getDate(start_date, "yyyy.MM.dd");
            //数据处理结束日期（默认为当天）
            String end_date = PubFunc.getStringDate("yyyy.MM.dd");
            Date e_date = DateUtils.getDate(end_date, "yyyy.MM.dd");

            //没到期间开始日期
            if (e_date.before(s_date)) {
                 cat.info("没到期间开始日期，取当前月份数据！");
                 String firstDay = DateUtil.getFirstDayOfMonth();
                 start_date = firstDay.replaceAll("-", "\\.");
            }
            /**
             * 设置的取当天的前几天考勤数据 
             * 如设置则按前3天获取
             */
            String days = getJobParaValue(dao, "days", jobId, "3");
            cat.info("钉钉接口后台作业参数,days:"+days+",获取当前时间前"+days+"天考勤打卡签到数据！");
            
            /**
             * flag
	             * =0旧版本获取标识corpid/corpSecret
	             * =1通过msg_AppKey/msg_AppSecret获取
             * paramFrom
	             * =0 后台作业参数
	             * =1 第三方参数
             * 获取accessToken规则：
             * <一>、优先从后台作业参数获取
             * 1、取参数corpid/corpSecret
             * 2、取参数msg_AppKey/msg_AppSecret
             * 3、若通过corpid/corpSecret获取到的accessToken为空，则再次通过msg_AppKey/msg_AppSecret获取 
             * 4、若两种方式都获取的accessToken都为空，并且这四个参数全部为空则从第三方接口取，
             * 只要有一个不为空，则以后台作业为准，结果认为获取accessToken失败，不再考虑其他配置
             * <二>、其次从第三方接口参数获取
             * 	同<一>（1、2、3、4）按照上述规则从第三方接口重新取
             */
            String paramFrom = "0";
            /*取userId start*/
            DbNameBo dbbo = new DbNameBo(conn);
            // 优先解析作业参数
            String userIdindex = getJobParaValue(dao, "userId", jobId, "");
            if(StringUtils.isEmpty(userIdindex)){
            	cat.info("后台作业参数中未设置钉钉userId对应指标！");
            	paramFrom = "1";
            }else{
            	/*校验userID等于username时取认证用户名对应字段*/
                if("username".equals(userIdindex)) {
					userIdindex = dbbo.getLogonUserNameField();
				}
            	errorInfo = checkUserId(conn, preList.get(0).toString(), userIdindex);
            	if(StringUtils.isEmpty(errorInfo)) {
            		userID = userIdindex;
            		cat.info("后台作业参数中设置的userId对应指标是"+userID);
            	}else {
            		cat.info("后台作业参数中"+errorInfo);
            		paramFrom = "1";
            	}
            }
            
            if("1".equals(paramFrom)) {
            	/*通过第三方平台参数，获取企业ID，凭证密匙,人员ID*/
            	getDingTalk();
            	/*校验userID等于username时取认证用户名对应字段*/
                if("username".equals(userID)) {
					userID = dbbo.getLogonUserNameField();
				}
            	errorInfo = checkUserId(conn, preList.get(0).toString(), userID);
            	if(StringUtils.isEmpty(errorInfo)) {
					cat.info("第三方参数中设置的userId对应指标是"+userID);
				} else {
            		cat.error("第三方参数中"+errorInfo);
            		throw new JobExecutionException(errorInfo);
            	}
            }
            /*取userId end*/
            
        	// 从后台作业参数中获取
            corpid = getJobParaValue(dao, "corpid", jobId, "");
            corpSecret = getJobParaValue(dao, "corpSecret", jobId, "");
    		
    		accessToken = getAccessToken("0", "0");
    		if(StringUtils.isEmpty(accessToken) 
    				&& (StringUtils.isEmpty(corpid) && StringUtils.isEmpty(corpSecret))) {
    			msg_AppKey = getJobParaValue(dao, "msg_AppKey", jobId, "");
        		msg_AppSecret = getJobParaValue(dao, "msg_AppSecret", jobId, "");
        		accessToken = getAccessToken("0", "1");
    		}
    		/**
    		 * 1、获取的accessToken为空
    		 * 2、corpid与corpSecret都为空
    		 * 3、msg_AppKey与msg_AppSecret都为空
    		 * 满足这三个条件再从第三方取参,否则以后台作业参数为准
    		 */
            if(StringUtils.isEmpty(accessToken)
            		&& (StringUtils.isEmpty(corpid) && StringUtils.isEmpty(corpSecret))
            				&& (StringUtils.isEmpty(msg_AppKey) && StringUtils.isEmpty(msg_AppSecret))) {
        		/*通过第三方平台参数，获取企业ID，凭证密匙,人员ID*/
        		getDingTalk();
            	accessToken = getAccessToken("1", "0");
        		if(StringUtils.isEmpty(accessToken) 
        				&& (StringUtils.isEmpty(corpid) && StringUtils.isEmpty(corpSecret))) {
					accessToken = getAccessToken("1", "1");
				}
            }
        	/**
        	 * 通过两种方式都获取不到accessToken则获取失败
        	 */
        	if(StringUtils.isEmpty(accessToken)) {
        		errorInfo = "获取access_token失败 ！";
    			cat.error(errorInfo);
    			throw new JobExecutionException(errorInfo);
        	}else {
				cat.info("获取到的access_token："+accessToken);
			}
        	// 获取ehr系统中所有userid集合
        	ArrayList userIdAlllist = getUserIdlist(dao, preList, userID);
        	/**
        	 * 根据设置的后台作业参数days获取相应的时间范围
        	 */
        	end_date = DateUtils.format(new Date(), "yyyy.MM.dd");
        	start_date = DateUtils.format(DateUtils.addDays(new Date(), (-Integer.parseInt(days)+1)), "yyyy.MM.dd");
        	// 获取打卡数据，每50个用户一组
        	ArrayList userIdClocklist = getUserIdlistGroupData(userIdAlllist, 50);
        	for(int i=0 ;i<userIdClocklist.size();i++) {
        		
        		ArrayList clocklist = (ArrayList)userIdClocklist.get(i);
        		getDingTalkData(accessToken, clocklist, start_date, end_date, "1");
        	}
        	ArrayList userIdSignlist = getUserIdlistGroupData(userIdAlllist, 10);
        	// 获取签到数据，每10个用户一组
        	for(int i=0 ;i<userIdSignlist.size();i++) {
        		ArrayList signlist = (ArrayList)userIdSignlist.get(i);
        		getDingTalkDataByUserid(accessToken, signlist, start_date, end_date, "2");
        	}
	        
	        if(dingTakeTimelist.size() == 0){
	        	errorInfo = start_date+"-"+end_date+"未取到钉钉数据！";
	        	cat.error(errorInfo);
	        	throw new JobExecutionException(errorInfo);
	        }
	        
	        cat.info("当前时间获取钉钉原始数据共"+dingTakeTimelist.size()+"条记录！");
	        //dingTakeTimelist 去重  同一分钟 的数据
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");  
	        for (int i=0 ;i<dingTakeTimelist.size()-1;i++){       
	            for (int j=dingTakeTimelist.size()-1;j>i;j--){  
	            	LazyDynaBean ldb1 = (LazyDynaBean) dingTakeTimelist.get(i);
	            	long workTime1 = (Long)ldb1.get("workTime");
	            	Date workDate1 = new Date(workTime1);
	            	String workTimeStr1 = sdf.format(workDate1);
	            	String userId1 = (String)ldb1.get("userId");
	            	
	            	LazyDynaBean ldb2 = (LazyDynaBean) dingTakeTimelist.get(j);
	            	long workTime2 = (Long)ldb2.get("workTime");
	            	Date workDate2 = new Date(workTime2);
	            	String workTimeStr2 = sdf.format(workDate2);
	            	String userId2 = (String)ldb2.get("userId");
	                if(workTimeStr2.equalsIgnoreCase(workTimeStr1) && userId2.equalsIgnoreCase(userId1)){       
	                	 dingTakeTimelist.remove(j); 
	                 }        
	              }        
	        }
	        int numDing = dingTakeTimelist.size();
	        cat.info("当前时间获取钉钉同步数据，按分钟去重后共"+dingTakeTimelist.size()+"条记录！");
	        
	        /*查询钉钉记录与原刷卡数据是否重复sql*/
	        StringBuffer kqsql = new StringBuffer();
	        kqsql.append(" select A0100 from kq_originality_data ");
	        kqsql.append(" where nbase=? ");
	        kqsql.append(" and A0100=? ");
	        kqsql.append(" and work_date=? ");
	        kqsql.append(" and work_time=? ");
	        
	        /*刷卡表添加记录sql*/
	        StringBuffer kqsqlInto = new StringBuffer();
	        kqsqlInto.append(" insert into kq_originality_data  ");
	        kqsqlInto.append(" (nbase,A0100,work_date,work_time,card_no,A0101,B0110,E0122,E01A1,location,sp_flag,inout_flag,datafrom) ");
	        kqsqlInto.append(" values ");
	        kqsqlInto.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?) ");
	        
			//获取到钉钉userid集合
	        ArrayList userIdlistS = getDingUserIdList();
	        
	        /*循环userids集合并加入刷卡表*/
	        int num = 0;
	        int kqnum = 0;
	        for(int i=0;i<userIdlistS.size();i++){
	        	String userids = (String) userIdlistS.get(i);
	        	
	        	ArrayList kqInfolist = getKqInfolist(dao, preList, userids, cardnoField, userID);
//        		
        		if(kqInfolist.size() == 0){
        			continue;
        		}
        		
        		num += kqInfolist.size();
        		kqnum += insertDataMain(dao, kqInfolist, kqsql.toString(), kqsqlInto.toString());
	        }
	        cat.info("获取钉钉有效打卡数据"+num+"条记录，未找到对应系统人员信息的刷卡数据有"+(numDing-num)+"条记录，系统已存在的刷卡数据有"+(num-kqnum)+"条记录。");
	        cat.info("成功同步钉钉有效打卡数据"+kqnum+"条记录。");
	        cat.info("完成钉钉签到打卡数据同步。");
        } catch (JobExecutionException e) {
        	throw e;
        } catch (Exception e) {
        	e.printStackTrace();
        	cat.error("错误：{"+e.toString()+"}");
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
    /**
     * 获取ehr系统所有有效的用户id
     * @param dao
     * @param preList		库集合
     * @param userIdindex	参数userid指标名称
     * @return
     */
    private ArrayList getUserIdlist(ContentDAO dao, ArrayList preList, String userIdindex) {
    	
    	StringBuffer sql = new StringBuffer();
    	ArrayList list = new ArrayList();
	    for (int j=0; j<preList.size(); j++) {
	        String pre = (String)preList.get(j);
	        if(j > 0){
	        	sql.append(" union all ");
	        }
	        sql.append("SELECT DISTINCT ").append(userIdindex).append(" as userId");
	        sql.append(" FROM ").append(pre).append("A01 ");
	        sql.append(" WHERE ").append(userIdindex).append(" IS NOT NULL ");
	        if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                sql.append(" AND ").append(userIdindex).append("<>''");
            }
	    }
	    RowSet rs = null;
    	try{
	    	rs = dao.search(sql.toString());
			while(rs.next()){
				list.add(rs.getString("userId"));
			}
        } catch (Exception e) {
            e.printStackTrace();
            cat.error("错误：{"+e.toString()+"}");
        } finally {
            PubFunc.closeDbObj(rs);
        }
    	cat.info("获取系统全部userId");
    	cat.info(list.toString());
    	return list;
    }
    /**
     * 拼装多少条一组数据
     * @param list			数据集合
     * @param indexNum		多少条一组 
     * @return
     */
    private ArrayList getUserIdlistGroupData(ArrayList list, int indexNum) {
    	
    	ArrayList userIdAlllist = new ArrayList();
    	ArrayList userIdlist = new ArrayList();
    	try{
			
			if(list.size() < indexNum) {
				
				userIdAlllist.add(list);
			}else {
	    		
				for(int i=0;i<list.size();i++) {
					
					userIdlist.add((String)list.get(i));
					
					if(userIdlist.size()%indexNum == 0) {
						
						userIdAlllist.add(userIdlist);
						userIdlist = new ArrayList();
					}else if(i == list.size()-1 && list.size()%indexNum > 0) {
						userIdAlllist.add(userIdlist);
					}
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
            cat.error("错误：{"+e.toString()+"}");
        }
    	return userIdAlllist;
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
     * 获取人员基本信息 A0100,A0101,B0110,E0122,E01A1...
     * @param dao 
     * @param preList 考勤库集合
     * @param userIds	对应主集的userID数据  'userID','userID','userID','userID'...
     * @param cardnoField 	考勤卡号对应指标
     * @param userIdindex	userID对应指标
     * @return
     */
    private ArrayList getKqInfolist(ContentDAO dao, ArrayList preList, String userIds, String cardnoField, String userIdindex)
    		throws JobExecutionException {
    	StringBuffer sql = new StringBuffer();
    	ArrayList kqInfolist = new ArrayList();
	    for (int j=0; j<preList.size(); j++) {
	        String pre = (String)preList.get(j);
	        if(j > 0){
	        	sql.append(" union all ");
	        }
	        sql.append("SELECT A0100,A0101,B0110,E0122,E01A1,").append(cardnoField).append(" as cardno,")
	        	.append(userIdindex).append(" as userId,'"+pre+"' as nbase");
	        sql.append(" FROM ").append(pre).append("A01");
	        sql.append(" where ").append(userIdindex).append(" in (").append(userIds).append(") ");
	       
	    }
	    RowSet rs = null;
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");  
    	try{
	    	rs = dao.search(sql.toString());
		    ArrayList dingTakeTimelistC = dingTakeTimelist;
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
				
				for(int i=dingTakeTimelistC.size()-1;i>=0;i--){
					LazyDynaBean ldbC = (LazyDynaBean) dingTakeTimelistC.get(i);
		        	String userIdc = (String) ldbC.get("userId");
		        	if(!userId.equalsIgnoreCase(userIdc)) {
						continue;
					}
		        	
		        	String address = (String) ldbC.get("address");
		        	address = PubFunc.splitString(address, 200);
		        	long timeLong = (Long) ldbC.get("workTime");
		        	Date workDate = new Date(timeLong);
		        	String workTimeStr = sdf.format(workDate);
		        	String date = "";
		    		String time = "";
		        	if((workTimeStr.trim()).indexOf(" ") > -1){
		        		date = workTimeStr.split(" ")[0];
		        		time = workTimeStr.split(" ")[1];
		        	}else{
		        		cat.info("userId为："+userId+"，签到时间转换错误！获取的时间为"+timeLong+"。");
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
		        	ldb.set("location", "钉钉："+address);
		        	kqInfolist.add(ldb);
		        	
		        	dingTakeTimelistC.remove(i);
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
     * 获取到钉钉userid集合，为查主集对应指标 去除重复userID 
     * 
     * @return userIdlist
     */
    private ArrayList getDingUserIdList(){
    	
    	if(userIdlist.size() == 0){
    		cat.info("未取到钉钉数据中userId！");
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
     * 从第三方接口
     * 获取钉钉corpid 企业Id, corpsecret 企业应用的凭证密钥
     * msg_AppKey、msg_AppSecret
     * 注意 userID 如果从后台作业获取到userID 则不会重新获取
     * @return
     * @throws JobExecutionException 
     */
    private void getDingTalk() throws JobExecutionException {
    	try {
			RecordVo recordVo = ConstantParamter.getConstantVo("DINGTALK");
			if (recordVo != null) {
				Document doc = PubFunc.generateDom(recordVo.getString("str_value"));
				Element root = doc.getRootElement();
				List list = root.getChildren();
				for (int i = 0; i < list.size(); ++i) {
					Element child = (Element) list.get(i);
					String key = child.getAttributeValue("key");
					if("corpid".equals(key)){
						corpid = child.getAttributeValue("value");
					}else if ("corpsecret".equals(key)){
						corpSecret = child.getAttributeValue("value");
					}else if("userid".equals(key) && StringUtils.isEmpty(userID)){
						userID = child.getAttributeValue("value");
					}else if ("msg_AppKey".equals(key)){
						msg_AppKey = child.getAttributeValue("value");
					}else if ("msg_AppSecret".equals(key)){
						msg_AppSecret = child.getAttributeValue("value");
					}
				}
				cat.info("从第三方平台参数获取钉钉接口参数,CORP_ID:"+corpid+",CORP_SECRET:"+corpSecret+",USER_ID:"+userID
						+",msg_AppKey:"+msg_AppKey+",msg_AppSecret:"+msg_AppSecret);
			}else{
				cat.info("从第三方平台没有获取到钉钉接口参数！");
			}
		} catch (Exception e) {
			cat.error("从第三方平台获取钉钉接口参数异常,"+e.getMessage());
			e.printStackTrace();
		}
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
     * 获取钉钉打卡或签到(按部门获取)数据
     * @param accessToken 
     * @param fromstr 开始日期 2017.01.01
     * @param tostr   结束日期 2017.01.31
     * @param type =1 打卡数据；=2 签到(按部门获取)数据
     * @throws JobExecutionException
     */
    private  void getDingTalkData(String accessToken, ArrayList userIdGrouplist, String fromstr, String tostr, String type) 
    		throws JobExecutionException {
    	try {
	    	
	    	int typeNum = 7;
	    	if("1".equals(type)){//打卡数据
	    		typeNum = 7;
	    	}else if("2".equals(type)){//签到数据
	    		typeNum = 45;
	    	}else{
	    		return;
	    	}
	    	
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
	    					getClockdata(accessToken, userIdGrouplist, fromClockstr, tostr, 0);
	    				}else if("2".equals(type)){
	    					getSigndata(accessToken , fromClockstr, tostr, 0);
	    		    	}
	    			}else{
		    			toClockstr = DateUtils.format(DateUtils.addDays(DateUtils.getDate(fromClockstr, "yyyy.MM.dd"), (typeNum-1)), "yyyy.MM.dd");
		    			if("1".equals(type)){
	    					getClockdata(accessToken, userIdGrouplist, fromClockstr, toClockstr, 0);
	    				}else if("2".equals(type)){
	    					getSigndata(accessToken , fromClockstr, toClockstr, 0);
	    		    	}
		    			fromClockstr = DateUtils.format(DateUtils.addDays(DateUtils.getDate(toClockstr, "yyyy.MM.dd"), 1), "yyyy.MM.dd");
	    			}
	    		}
	    	}else{
	    		if("1".equals(type)){
					getClockdata(accessToken, userIdGrouplist, fromstr, tostr, 0);
				}else if("2".equals(type)){
					getSigndata(accessToken , fromstr, tostr, 0);
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
     * 获取钉钉打卡或签到(按用户获取)数据
     * @param accessToken 
     * @param fromstr 开始日期 2017.01.01
     * @param tostr   结束日期 2017.01.31
     * @param type =1 打卡数据；=2 签到(按用户获取)数据
     * @throws JobExecutionException
     */
    private void getDingTalkDataByUserid(String accessToken, ArrayList userIdGrouplist, String fromstr, String tostr, String type) 
    		throws JobExecutionException {
    	try {
	    	String stareDate = fromstr;
	    	Date fromdt = DateUtils.getDate(fromstr, "yyyy.MM.dd");
	    	Date todt = DateUtils.getDate(tostr, "yyyy.MM.dd");
	    	int days = DateUtils.dayDiff(fromdt, todt);
	    	/**
	    	 * 如果是取1个人的数据，时间范围最大到10天，
	    	 * 如果是取多个人的数据，时间范围最大1天。
	    	 */
	    	if("2".equals(type)) {
	    		String signData = "";
	    		for(int i=0;i<userIdGrouplist.size();i++){
	    			signData += (String)userIdGrouplist.get(i);
	    			if(i < userIdGrouplist.size()-1) {
						signData += ",";
					}
	    		}
	    		/**
	    		 * 在循环中获取
	    		 */
	    		for(int i=0;i<days;i++){
	    			
	    			fromstr = DateUtils.format(DateUtils.addDays(DateUtils.getDate(stareDate, "yyyy.MM.dd"), i), "yyyy.MM.dd");
	    			getUserSigndata(accessToken, signData, fromstr, fromstr, 0);
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
     * 获取打卡数据
     * accessToken
     * 
     */
    private  void getClockdata(String accessToken, ArrayList userIdClocklist, String fromstr, String tostr, int offset) throws JobExecutionException {
    	/**
    	 * access_token   必填         调用接口凭证
    	 * userId           
    	 * workDateFrom	     必填	   开始时间
    	 * workDateTo            结束时间（默认为当前时间）
    	 * 
    	 * 注意   **开始时间和结束时间的间隔不能大于7 天
    	 */
    	try {
	    	fromstr = fromstr+" 00:00:00";
	    	fromstr = fromstr.replaceAll("\\.", "-");
			tostr = tostr+" 23:59:59";
			tostr = tostr.replaceAll("\\.", "-");
			
			String requestUrl = "https://oapi.dingtalk.com/attendance/list?access_token="+accessToken;
			
//			String jsonMsg="{\"workDateFrom\": \""+fromstr+"\",\"workDateTo\": \""+tostr+"\",\"userIdList\": [],\"offset\": "+offset+",\"limit\": 48}";
			JSONObject jsonMsg = new JSONObject();
			jsonMsg.put("workDateFrom", fromstr);
			jsonMsg.put("workDateTo", tostr);
			jsonMsg.put("userIdList", userIdClocklist);
			jsonMsg.put("offset", (long)offset);
			jsonMsg.put("limit", 50L);
	        
			JSONObject jsonObject = httpsRequest(requestUrl, "POST", jsonMsg.toString());
//			cat.info(jsonObject.toString());
			/**
			 * 返回的jsonObject
			 * "errcode": 0,"errmsg": "ok",
			 * recordresult：
				 * "id": 唯一标示ID
				 * "groupId": 考勤组ID
				 * "planId": 排班ID
				 * "recordId": 打卡记录ID
				 * "workDate": 工作日
				 * "userId": 用户ID
				 * "checkType": 考勤类型（OnDuty：上班，OffDuty：下班）
				 * "timeResult": 时间结果（Normal:正常;Early:早退; Late:迟到;SeriousLate:严重迟到；NotSigned:未打卡）
				 * locationResult  位置结果（Normal:范围内；Outside:范围外）
				 * approveId    关联的审批id
				 * baseCheckTime   计算迟到和早退，基准时间
				 * userCheckTime    实际打卡时间
			 * 
			 */
			
			if (null != jsonObject)
			{
				if(jsonObject.getInt("errcode") == 0){
					JSONArray signJsons = new JSONArray();
					signJsons = jsonObject.getJSONArray("recordresult");
					LazyDynaBean ldb = new LazyDynaBean();
					for(int i=0;i<signJsons.size();i++){
						ldb = new LazyDynaBean();
						JSONObject json = (JSONObject) signJsons.get(i);
						//timeResult时间结果（Normal:正常;Early:早退; Late:迟到;SeriousLate:严重迟到；NotSigned:未打卡）
						//未打卡直接跳出
						if("NotSigned".equalsIgnoreCase(json.getString("timeResult"))) {
							continue;
						}
						
						ldb.set("userId", json.getString("userId"));
						ldb.set("workTime", json.getLong("userCheckTime"));
						ldb.set("address", "钉钉打卡数据");
						userIdlist.add(json.getString("userId"));
						dingTakeTimelist.add(ldb);
					}
					// 钉钉API获取打卡记录接口更新 分页获取
					if(true == jsonObject.getBoolean("hasMore")) {
						offset = offset + 50;
						getClockdata(accessToken, userIdClocklist, fromstr, tostr, offset);
					}
				}else{
					/**
					 * 54584
					 * 根据钉钉接口主动调用的频率限制问题  
					 * 遇到此错误码，需要你在服务端代码里等待1秒钟再继续运行。 
					 */
					if(jsonObject.getInt("errcode") == 7) {
	        			Thread.currentThread().sleep(2000);
	        			getClockdata(accessToken, userIdClocklist, fromstr, tostr, offset);
					}else {
						// 错误信息
						String errorInfo = "获取钉钉打卡数据失败errcode:{"+ jsonObject.getInt("errcode")+"} errmsg:{"+ jsonObject.getString("errmsg")+"}";
						cat.error(errorInfo);
						throw new JobExecutionException(errorInfo);
					}
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
     * 根据 用户 获取签到数据
     * @param accessToken 调用接口凭证
     * @param fromstr  开始时间
     * @param tostr    结束时间
     * @param offset   页数从0 开始
     * @throws JobExecutionException
     */
    private  void getUserSigndata(String accessToken, String userIdSignlist, String fromstr, String tostr, int offset) throws JobExecutionException {
    	/**
    	 * access_token   必填         	调用接口凭证
    	 * userid_list    必填         	需要查询的用户列表  String []  最大列表长度：10
    	 * start_time	      必填	 起始时间,单位毫秒
    	 * end_time       必填           截止时间，单位毫秒。如果是取1个人的数据，时间范围最大到10天，如果是取多个人的数据，时间范围最大1天。
    	 * cursor         必填	 分页查询的游标，最开始可以传0
    	 * size           必填      	 分页查询的每页大小，最大100
    	 * 
    	 * 注意   **通过定义时间参数 来确定获取几天的签到数据 
    	 */
    	try{
	    	fromstr = fromstr+" 00:00";
	    	tostr = tostr+" 23:59";
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
	    	long froml = sdf.parse(fromstr).getTime();
	    	long tol = sdf.parse(tostr).getTime();
	    	String requestUrl = "https://oapi.dingtalk.com/topapi/checkin/record/get?access_token="+accessToken;
	    	
	    	JSONObject jsonMsg = new JSONObject();
			jsonMsg.put("start_time", froml);
			jsonMsg.put("end_time", tol);
			jsonMsg.put("userid_list", userIdSignlist);
			jsonMsg.put("cursor", offset);
			jsonMsg.put("size", 100);
			
	    	JSONObject jsonObject = httpsRequest(requestUrl, "POST", jsonMsg.toString());
	    	/**
	    	 * 返回的jsonObject
	    	 *  errcode	返回码
				errmsg	对返回码的文本描述内容
				result	
					└ next_cursor	表示下次查询的游标，为null代表没有更多的数据
					└ page_list	分页列表
						└ checkin_time	签到时间，单位毫秒
						└ image_list	签到照片url列表
						└ detail_place	签到详细地址
						└ remark	签到备注
						└ userid	员工唯一标识
						└ place	签到地址
	    	 */
	    	if (null != jsonObject)
			{
	    		if(jsonObject.getInt("errcode") == 0){
	    			JSONObject resultObj = jsonObject.getJSONObject("result");
	    			JSONArray signJsons = resultObj.getJSONArray("page_list");
	    			LazyDynaBean ldb = new LazyDynaBean();
	    			for(int i=0;i<signJsons.size();i++){
	    				JSONObject json = (JSONObject) signJsons.get(i);
	    				ldb = new LazyDynaBean();
	    				ldb.set("userId", json.getString("userid"));
	        			ldb.set("workTime", json.getLong("checkin_time"));
	        			ldb.set("address", json.getString("place"));
	        			userIdlist.add(json.getString("userid"));
	        			dingTakeTimelist.add(ldb);
	    			}
	    			if(signJsons.size()==100){
	    				offset++;
	    				getUserSigndata(accessToken, userIdSignlist, fromstr, tostr, offset);
	    			}
				}else{
					/**
					 * 54584
					 * 根据钉钉接口主动调用的频率限制问题  
					 * 遇到此错误码，需要你在服务端代码里等待1秒钟再继续运行。 
					 */
					if(jsonObject.getInt("errcode") == 7) {
	        			Thread.currentThread().sleep(2000);
	        			getUserSigndata(accessToken, userIdSignlist, fromstr, tostr, offset);
					}else {
						//错误信息
						String errorInfo = "获取钉钉签到数据失败 ，errcode:{"+ jsonObject.getInt("errcode")+"} errmsg:{"+ jsonObject.getString("errmsg")+"}";
						cat.error(errorInfo);
						throw new JobExecutionException(errorInfo);
					}
				}
			}
	    } catch (JobExecutionException e) {
        	throw e;
        } catch (Exception e) {
			e.printStackTrace();
			cat.error("错误：{"+e.toString()+"}");
		}
    }
    /**
     * 根据部门获取签到数据
     * @param accessToken 调用接口凭证
     * @param fromstr  开始时间
     * @param tostr    结束时间
     * @param offset   页数从0 开始
     * @throws JobExecutionException
     */
    private  void getSigndata(String accessToken, String fromstr, String tostr, int offset) throws JobExecutionException {
    	/**
    	 * access_token   必填         调用接口凭证
    	 * department_id  必填         部门id（1 表示根部门）
    	 * start_time	     必填	   开始时间
    	 * end_time              结束时间（默认为当前时间）
    	 * offset                支持分页查询，与size 参数同时设置时才生效，此参数代表偏移量，从0 开始
    	 * size                  支持分页查询，与offset 参数同时设置时才生效，此参数代表分页大小，最大100
    	 * order                 排序，asc 为正序，desc 为倒序
    	 * 注意   **开始时间和结束时间的间隔不能大于45 天
    	 */
    	try{
	    	fromstr = fromstr+" 00:00";
	    	tostr = tostr+" 23:59";
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
	    	long froml = sdf.parse(fromstr).getTime();
	    	long tol = sdf.parse(tostr).getTime();
	    	
	    	String requestUrl = "https://oapi.dingtalk.com/checkin/record?access_token="+accessToken+"&department_id=1&start_time="+froml+"&end_time="+tol+"&offset="+offset+"&size=100&order=asc";
	//    	requestUrl = requestUrl.replace("ID", corpid);
			
	    	JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
	    	/**
	    	 * 返回的jsonObject
	    	 * "errcode": 0,"errmsg": "ok",
	    	 * data :
		    	 * "name": 成员名称,
		    	 * "userId": 员工唯一标识ID（不可修改）,
		    	 * "avatar": 头像url,
		    	 * "timestamp": 签到时间, 
		    	 * "place": 签到地址, 
		    	 * "detailPlace": 签到详细地址,
		    	 * "remark": 签到备注,
		    	 * "imageList": 签到照片url列表,
	    	 */
	    	if (null != jsonObject)
			{
	    		if(jsonObject.getInt("errcode") == 0){
	    			JSONArray signJsons = new JSONArray();
	    			signJsons = jsonObject.getJSONArray("data");
	    			LazyDynaBean ldb = new LazyDynaBean();
	    			for(int i=0;i<signJsons.size();i++){
	    				JSONObject json = (JSONObject) signJsons.get(i);
	    				ldb = new LazyDynaBean();
	    				ldb.set("userId", json.getString("userId"));
	        			ldb.set("workTime", json.getLong("timestamp"));
	        			ldb.set("address", json.getString("place"));
	        			userIdlist.add(json.getString("userId"));
	        			dingTakeTimelist.add(ldb);
	    			}
	    			if(signJsons.size()==100){
	    				offset++;
	    				getSigndata(accessToken, fromstr, tostr, offset);
	    			}
				}else{
					//错误信息
		            String errorInfo = "获取钉钉签到数据失败 ，errcode:{"+ jsonObject.getInt("errcode")+"} errmsg:{"+ jsonObject.getString("errmsg")+"}";
					cat.error(errorInfo);
					throw new JobExecutionException(errorInfo);
				}
			}
	    } catch (JobExecutionException e) {
        	throw e;
        } catch (Exception e) {
			e.printStackTrace();
			cat.error("错误：{"+e.toString()+"}");
		}
    }
    
    /**
     * 获取钉钉access_token
     * @param paramFrom =0通过后台作业获取的参数；=1通过第三方接口获取的参数
     * @param flag =0通过corpid/corpSecret获取； =1通过msg_AppKey/msg_AppSecret获取
     * @return
     * @throws JobExecutionException 
     */
    private String getAccessToken(String paramFrom, String flag) throws JobExecutionException {
    	String flagInfo = "";
    	String requestUrl = "https://oapi.dingtalk.com/gettoken?";
    	String fromStr = "0".equals(paramFrom) ? "后台作业" : "第三方接口";
    	if("0".equals(flag)) {
    		if(StringUtils.isEmpty(corpid) || StringUtils.isEmpty(corpSecret)) {
    			cat.info(fromStr+"参数中获取钉钉接口参数无效,corpid:"+corpid+",corpSecret:"+corpSecret);
    			return "";
    		}else {
				cat.info(fromStr+"参数中获取钉钉接口参数成功,corpid:"+corpid+",corpSecret:"+corpSecret);
			}
    		requestUrl += "corpid=ID&corpsecret=SECRECT";
    		requestUrl = requestUrl.replace("ID", corpid);
    		requestUrl = requestUrl.replace("SECRECT", corpSecret);
    		flagInfo = "通过corpid/corpSecret";
    	}else if("1".equals(flag)) {
    		if(StringUtils.isEmpty(msg_AppKey) || StringUtils.isEmpty(msg_AppSecret)) {
    			cat.info(fromStr+"参数中获取钉钉接口参数无效,msg_AppKey:"+msg_AppKey+",msg_AppSecret:"+msg_AppSecret);
    			return "";
    		}else {
				cat.info(fromStr+"参数中获取钉钉接口参数成功,msg_AppKey:"+msg_AppKey+",msg_AppSecret:"+msg_AppSecret);
			}
    		requestUrl += "appkey=APPKEY&appsecret=APPSECRET";
    		requestUrl = requestUrl.replace("APPKEY", msg_AppKey);
    		requestUrl = requestUrl.replace("APPSECRET", msg_AppSecret);
    		flagInfo = "通过msg_AppKey/msg_AppSecret";
    	}else {
    		return "";
    	}
		
    	JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
    	/**
    	 * 返回的jsonObject
    	 * 正确的："errcode": 0,"errmsg": "ok","access_token": "fw8ef8we8f76e6f7s8df8s"
    	 * 错误的："errcode": 43003,"errmsg": "require https"
    	 */
    	String accessToken = "";
    	//错误信息
        String errorInfo = "";
    	if (null != jsonObject){
			try{
				if(jsonObject.getInt("errcode") == 0){
					accessToken = jsonObject.getString("access_token");
				}else{
					errorInfo = flagInfo+"获取access_token失败 errcode:{"+ jsonObject.getInt("errcode")+"} errmsg:{"+ jsonObject.getString("errmsg")+"}";
					cat.error(errorInfo);
//					throw new JobExecutionException(errorInfo);
				}
			}catch (JSONException e){
				// 获取token失败
				errorInfo = flagInfo+"获取access_token失败 errcode:{"+ jsonObject.getInt("errcode")+"} errmsg:{"+ jsonObject.getString("errmsg")+"}";
				cat.error(errorInfo);
//				throw new JobExecutionException(errorInfo);
			}
		}else{
			errorInfo = flagInfo+"获取access_token失败 ！";
			cat.error(errorInfo);
//			throw new JobExecutionException(errorInfo);
		}
		return accessToken;
    }
    /**
     * 校验userid是否合法
     * @param conn
     * @param nbase
     * @param userIdindex
     * @return
     * @throws JobExecutionException
     */
    private String checkUserId(Connection conn, String nbase, String userIdindex) throws JobExecutionException {
    	String errorInfo = "";
    	//校验该指标是否构库
    	FieldItem item = DataDictionary.getFieldItem(userIdindex, "A01");
    	if(null == item){
    		DbWizard dbWizard = new DbWizard(conn);
    		//如果字段没在数据字典，则校验主集里是否存在
    		if(!dbWizard.isExistField(nbase+"A01", userIdindex)){
    			errorInfo = "设置的钉钉userId对应指标"+userIdindex+"不存在！";
    		}
    	}else{
    		String useflag = item.getUseflag();//1 已够库，0  未构库
    		if("0".equals(useflag)){
    			errorInfo = "设置的钉钉userId对应指标"+userIdindex+"未构库！";
    		}
    	}
    	return errorInfo;
    }
    /**
     * 发送请求  获取数据
     * @param requestUrl 路径
     * @param requestMethod 传送方式get | post 
     * @param outputStr 传送数据
     * @return jsonObject
     */
	private JSONObject httpsRequest(String requestUrl,
			String requestMethod, String outputStr) throws JobExecutionException{
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
