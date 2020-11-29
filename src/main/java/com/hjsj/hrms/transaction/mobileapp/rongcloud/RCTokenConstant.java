package com.hjsj.hrms.transaction.mobileapp.rongcloud;

import com.hjsj.hrms.actionform.propose.RandomStrg;
import com.hjsj.hrms.transaction.mobileapp.rongcloud.io.rong.ApiHttpClient;
import com.hjsj.hrms.transaction.mobileapp.rongcloud.io.rong.models.FormatType;
import com.hjsj.hrms.transaction.mobileapp.rongcloud.io.rong.models.SdkHttpResult;
import com.hjsj.hrms.transaction.mobileapp.utils.PhotoImgBo;
import com.hjsj.hrms.transaction.mobileapp.utils.Tools;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.collections.FastHashMap;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * 存每个用链接融云的token缓存类
 * @author imac
 *
 */
public class RCTokenConstant {
	private static FastHashMap tokenMap;
	
	public final static String appkey = "z3v5yqkbvos80";
	public final static String appsecret = "QxuEf0RZwaE";
	
	private static String fromUserId;
	
	static {
		//初始化appkey和appsecret
		//appkey = SystemConfig.getPropertyValue("RCIM_appkey");
		//appsecret = SystemConfig.getPropertyValue("RCIM_appsecret");
	}
	
	/**
	 * 初始化化su在融云token，使用su业务用户的邮箱，用于发送系统消息
	 */
	private static void initFromUserId(){
		Connection conn = null;
		RowSet rs = null;
		try{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			
			String serverid = Tools.getMACAddress();//获取租户id mac地址
			String sql = "select userid from t_sys_rcim where id='su' and serverid='"+serverid+"'";
			rs = dao.search(sql);
			if(rs.next()){
				fromUserId= rs.getString("userid");
			}else{
				sql = "select email from operuser where username='su'";
				rs = dao.search(sql);
				String email = "";
				if(rs.next()){
					email = rs.getString("email");
				}
				if(email!=null&&email.length()>0){
					if(email.length()>11){
						email= email.substring(0, 11);
					}
					fromUserId = SafeCode.encrypt(email);
				}else{
					RandomStrg RSTR = new RandomStrg();
					RSTR.setCharset("a-z");
					RSTR.setLength("11");
					RSTR.generateRandomObject();
					fromUserId=RSTR.getRandom();
				}
				SdkHttpResult result = ApiHttpClient.getToken(appkey, appsecret, fromUserId,ResourceFactory.getProperty("message.sys.send.mobile"),
						"http://www.hjsoft.com.cn:8089/UserFiles/Image/tixing.png", FormatType.json);
				JSONObject jsonObject = JSONObject.fromObject(result.toString());
				Map resultMap = (Map)jsonObject.get("result");
				String token =(String)resultMap.get("token");
				RecordVo vo = new RecordVo("t_sys_rcim");
				vo.setString("serverid", serverid);
				vo.setString("id", "su");
				vo.setString("userid", fromUserId);
				vo.setString("rctoken", token);
				dao.addValueObject(vo);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn !=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String getFromUserId(){
		if(fromUserId==null||fromUserId.length()==0){
			initFromUserId();
		}
		return fromUserId;
	}
	
	/**
	 * 
	 * @param userView 此版本只支持自主用户有消息功能IM，如果业务用户关联自助用户则使用关联自助用户的token
	 * @param url 人员头像 服务器地址 http://www.hjsoft.com.cn:8081
	 * @param isSys 后台系统用户发送系统消息注册的token
	 * @return
	 */
	public static String getRCToken(UserView userView,String url,Boolean isSys){
		Connection conn = null;
		RowSet rs = null;
		String token = "";
		String serverid="";
		String userid="";
		try{
			serverid = Tools.getMACAddress();//获取租户id mac地址
			String dbpre = userView.getDbname();
			String a0100 = userView.getA0100();//如果a0100
			String id = userView.getUserId();
			String mobile = userView.getUserTelephone();
			String email = userView.getUserEmail();
			int status = userView.getStatus();//自助用户为加密手机号（加密方式使用SafeCode.encrypt），业务用户为加密邮箱地址（由于融云对userid长度有限制，长度不能超过32位，故加密前邮箱地址如超过11位只取前11位）
			
			if(status ==4){//自助用户
				if(mobile==null||mobile.length()==0)
					return token;
			}else{//业务用户
				if(isSys){
					if(email==null||email.length()==0)
						return token;
					if(email.length()>11){
						email= email.substring(0, 11);
					}
				}else{
					if(a0100.length()>0){//关联了自助用户
						if(mobile==null||mobile.length()==0)
							return token;
					}else{
						return token;
					}
				}
			}
			userid = status==4?SafeCode.encrypt(mobile):(isSys?SafeCode.encrypt(email):SafeCode.encrypt(mobile));
			id=status==4?(dbpre+a0100):(isSys?id:(dbpre+a0100));
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			if(tokenMap==null){
				initToken(dao,rs);
				token = (String)tokenMap.get(serverid+id);
				if(token==null){
					String portraitUri = url +  PhotoImgBo.getPhotoPath(conn,userView.getDbname() ,userView.getA0100());
					SdkHttpResult result = null;
					result = ApiHttpClient.getToken(appkey, appsecret, userid, userView.getUserFullName(),
							portraitUri, FormatType.json);
					JSONObject jsonObject = JSONObject.fromObject(result.toString());
					Map resultMap = (Map)jsonObject.get("result");
					token =(String)resultMap.get("token");
					
					tokenMap.put(serverid+id,token);
					savetoken(dao,serverid,id,userid,token);
				}
			}else{
				token = (String)tokenMap.get(serverid+id);
				if(token==null){
					String portraitUri = url +  PhotoImgBo.getPhotoPath(conn,userView.getDbname() ,userView.getA0100());//getPhotoPath(conn,userView);
					SdkHttpResult result = null;
					result = ApiHttpClient.getToken(appkey, appsecret, userid, userView.getUserFullName(),
							portraitUri, FormatType.json);
					JSONObject jsonObject = JSONObject.fromObject(result.toString());
					Map resultMap = (Map)jsonObject.get("result");
					token =(String)resultMap.get("token");
					
					tokenMap.put(serverid+id,token);
					savetoken(dao,serverid,id,userid,token);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn !=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		userView.getHm().put("userid", userid);
		return token;
	}
	
	private static void initToken(ContentDAO dao,RowSet rs ) throws SQLException{
		String sql = "select * from t_sys_rcim";
		rs = dao.search(sql);
		tokenMap = new FastHashMap();
		while(rs.next()){
			String serverid = rs.getString("serverid");//租户id
			String id = rs.getString("id");//包含业务用户（即username）、自助用户（即a0100）
			String userid = rs.getString("userid");
			String rctoken = rs.getString("rctoken");
			tokenMap.put(serverid+id, rctoken);
		}
	}
	
	/**
	 * 持久化token
	 * @param dao
	 * @param userid
	 * @param email
	 * @param token
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private static void savetoken(ContentDAO dao,String serverid,String id,String userid,String token) throws GeneralException, SQLException{
		RecordVo vo = new RecordVo("t_sys_rcim");
		vo.setString("serverid", serverid);
		vo.setString("id", id);
		if(dao.isExistRecordVo(vo)){
			vo.setString("userid", userid);
			vo.setString("rctoken", token);
			dao.updateValueObject(vo);
		}else{
			vo.setString("userid", userid);
			vo.setString("rctoken", token);
			dao.addValueObject(vo);
		}
	}
	
	/**
	 * 判断被发起会话的用户是否已注册融云token
	 * @param dao
	 * @param serverid
	 * @param id
	 * @param userid
	 * @throws Exception
	 */
	public static void isSaveToken(ContentDAO dao,String serverid,String id,String userid) throws Exception{
		
		if(tokenMap.get(serverid+id)==null){
			RecordVo vo = new RecordVo("t_sys_rcim");
			vo.setString("serverid", serverid);
			vo.setString("id", id);
			vo.setString("userid", userid);
			String token = "";
			SdkHttpResult result = null;
			result = ApiHttpClient.getToken(appkey, appsecret, userid,"",
					"", FormatType.json);
			JSONObject jsonObject = JSONObject.fromObject(result.toString());
			Map resultMap = (Map)jsonObject.get("result");
			token =(String)resultMap.get("token");
			
			vo.setString("rctoken", token);
			tokenMap.put(serverid+id,token);
			dao.addValueObject(vo);
		}
	}
}
