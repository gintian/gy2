package com.hjsj.hrms.transaction.mobileapp.rongcloud;

import com.hjsj.hrms.transaction.mobileapp.rongcloud.io.rong.ApiHttpClient;
import com.hjsj.hrms.transaction.mobileapp.rongcloud.io.rong.models.FormatType;
import com.hjsj.hrms.transaction.mobileapp.rongcloud.io.rong.models.SdkHttpResult;
import com.hjsj.hrms.transaction.mobileapp.rongcloud.io.rong.models.TxtMessage;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * 调用融云API向app移动服务推送消息
 * @author imac
 *
 */
public class RCApiClient {

	private static Logger log =  Logger.getLogger(RCApiClient.class);
	/**
	 * 发送单条图文消息给具体人员 调用频率：每秒钟限 100 次
	 * 
	 * @param usernames
	 *            eHR系统人员登录账号 即userView.getUserName()  每次最多支持发送1000人
	 * @param title
	 *            图文消息标题
	 * @param description
	 *            图文消息描述
	 * @param picUrl
	 *            图文消息图片url地址
	 *            如：http://www.hjsoft.com.cn/upload/121227/1212271440003680.gif
	 * @param url
	 *            点击图文消息进入页面地址
	 * @return 发送是否成功 true|false
	 */
	public static boolean sendMsgToPerson(List usernames, String title,
			String description, String picUrl, String url) {
		boolean flag = false;
		try {
			if(usernames==null||usernames.size()==0){
				log.error("usernames is null!");
				return false;
			}
			List toIds = usernameToRCtoken(usernames);
			sendMsgToPersonByToken(toIds,title,description,picUrl,url);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {

		}
		return flag;
	}
	
	private static boolean sendMsgToPersonByToken(List toIds, String title,
			String description, String picUrl, String url) {
		
		boolean flag = false;
		try {
			String key = "lmxuhwagx89rd";
			String secret = "j2YXpdluyTLUZ";
			key = RCTokenConstant.appkey;
			secret = RCTokenConstant.appsecret;
			
			/*toIds = new ArrayList<String>();
			toIds.add("YXo474hVm7IsIyU7GU5Wwg==");
			toIds.add("1YVU5uC7eRbkKcgepgH+jA==");*/
			String fromUserId = RCTokenConstant.getFromUserId();//"U58jQAaMAQGKo25aoK5Xug==";
			
			title = PubFunc.hireKeyWord_filter(title);
			description = PubFunc.hireKeyWord_filter(description);
			
			//url = "http://www.baidu.com";
			String jsonMsg = "{\"title\":\"%s\",\"description\":\"%s\",\"picurl\":\"%s\",\"url\":\"%s\"}";
			jsonMsg = String.format(jsonMsg, title,description,picUrl,url);
			//System.out.println(jsonMsg);
			log.debug(jsonMsg);
			SdkHttpResult result = ApiHttpClient.publishSystemMessage(key, secret, fromUserId,
					toIds, new TxtMessage(title,jsonMsg), "",
					"", FormatType.json);
			if(200==result.getHttpCode())
				flag=true;
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {

		}
		return flag;
	}
	
	private static List usernameToRCtoken(List usernames){
		List toIds = new ArrayList();
		Connection conn = null;
		RowSet rs = null;
		try{
			conn = AdminDb.getConnection();
			 /**获取认证人员库*/
	        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
	        if(login_vo!=null) {
	        	String str_value =  login_vo.getString("str_value");
	        	if(str_value!=null){
	        		ContentDAO dao = new ContentDAO(conn);
	        		String [] dbpres =str_value.split(",");
	        		String  usernamecolum = getUserName();
	        		String phonecolum = getMobilePhoneItem();
	        		for(int i=0;i<dbpres.length;i++){
	        			String dbpre = dbpres[i];
	        			if(dbpre.length()==3){
	        				int size = usernames.size();
	        				if(size<500){
	        					String sql = "select "+phonecolum +" from "+dbpre+"A01 where "+usernamecolum+" in ('"+(usernames.toString().substring(1, usernames.toString().length()-1).replaceAll(", ", "','"))+"')";
	        					rs = dao.search(sql);
	        					while (rs.next()){
	        						String phone = rs.getString(phonecolum);
	        						//非空判断，避免空指针错误 -haosl 2019-5-20
	        						if(StringUtils.isNotBlank(phone)){
	        							toIds.add(SafeCode.encrypt(phone));
	        						}
	        					}
	        				}else{
	        					int t=0;
	        					int n=0;
	        					while(true){
	        						t++;
	        						List tmps = usernames.subList(n, 499);
	        						n=n+499;
	        						if(n>size-1){
	        							tmps = usernames.subList(n, size-1);
	        							String sql = "select "+phonecolum +" from "+dbpre+"A01 where "+usernamecolum+" in ('"+(tmps.toString().substring(1, tmps.toString().length()-1).replaceAll(", ", "','"))+"')";
	        							rs = dao.search(sql);
	    	        					while (rs.next()){
	    	        						String phone = rs.getString(phonecolum);
                                            //非空判断，避免空指针错误 -haosl 2019-5-20
	    	        						if(StringUtils.isNotBlank(phone)){
	    	        							toIds.add(SafeCode.encrypt(phone));
	    	        						}
	    	        					}
	        							break;
	        						}
	        						if(t>50)//防止死循环保险锁
	        							break;
	        					}
	        				}
	        			}
	        		}
	        	}
	        	
	        }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
        return toIds;
	}
	
	/**
	 * 获取用户登陆指标
	 * @return
	 */
	private static String getUserName(){
		String username = "";
		RecordVo login_vo = ConstantParamter
				.getConstantVo("SS_LOGIN_USER_PWD");
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
	
	
	private static String getMobilePhoneItem(){
		RecordVo vo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE");
		String phone = "";
		 if(vo!=null){
			 phone=vo.getString("str_value");
			 phone = phone!=null?phone:"";
		 }
		 return phone;
	}
}
