package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.weixin.utils.CommonUtil;
import com.hjsj.weixin.utils.MyX509TrustManager;
import com.hjsj.weixin.utils.Token;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * 钉钉发送预警消息工具类
 * xus 17/04/19
 */
public class DDWarnUtil {
	private static Logger log =  Logger.getLogger(DDWarnUtil.class.getName());

	// 凭证获取（GET）
	public final static String token_url = "https://oapi.dingtalk.com/gettoken?corpid=appid&corpsecret=appsecret";
	//订阅号、服务号全局token 不会失效，token失效后则重新获取   xus 2017/3/29
	public static Token _Token;
	//系统中对应的人员id字段
	public static String userId = null;
	public static String agentId = null;
	
	/**
	 * 发送https请求
	 * @param requestUrl 请求地址
	 * @param requestMethod 请求方式（GET、POST）
	 * @param outputStr 提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			URL url = new URL(null,requestUrl,new sun.net.www.protocol.https.Handler());
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			conn.addRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(requestMethod);

			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				// 注意编码格式
				outputStream.write(outputStr.getBytes("UTF-8"));//
				outputStream.close();
			}

			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}

			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (ConnectException ce) {
			ce.printStackTrace();
			log.error("连接超时：{}", ce);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("https请求异常：{}", e);
		}
		return jsonObject;
	}
	
	/**
	 * 创建钉钉发消息表 
	 * xus 17/04/20
	 * @param TableName
	 * @param conn
	 * @return
	 */
	public static boolean createDDTableInfo(String TableName,Connection conn){
		Table table = new Table(TableName);
		DbWizard dbw=new DbWizard(conn);
		Field dingtalk_msg_id=new Field("dingtalk_msg_id",1);
		dingtalk_msg_id.setLength(50);
		table.addField(dingtalk_msg_id);
		Field sender=new Field("sender",1);
		sender.setLength(50);
		table.addField(sender);
		Field receiver=new Field("receiver",1);
		receiver.setLength(50);
		table.addField(receiver);
		Field msg_content=new Field("msg_content",13);
		table.addField(msg_content);
		Field send_time=new Field("send_time",12);
		table.addField(send_time);
		Field username=new Field("username",1);
		username.setLength(50);
		table.addField(username);
		Field wid=new Field("wid",4);
		table.addField(wid);
		try {
			dbw.createTable(table);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return true;
	}

	/**
	 * 微信服务号 获取接口访问凭证
	 * @param appid 凭证
	 * @param appsecret 密钥
	 * @return
	 */
	public static Token getToken() {
		
	String CORP_ID = "dingfd789edfa0f5ccb735c2f4657eb6378f";
	String CORP_SECRET = "BHyF2CO705EZUmt5PBzqJLkBZQwZWYdUaiqYnzcjVkOV2tmXzPkOziJOcC7AE5VY";
	String userid = null;
	Token token = null;
	Connection conn=null;
	StringReader reader = null;
	try {
		RecordVo recordVo = ConstantParamter.getConstantVo("DINGTALK");
		if (recordVo != null)
		{
			Document doc = PubFunc.generateDom(recordVo.getString("str_value"));
			Element root = doc.getRootElement();
			List list = root.getChildren();
			for (int i = 0; i < list.size(); ++i)
			{
				Element child = (Element) list.get(i);
				String key = child.getAttributeValue("key");
				if("corpid".equals(key)){
					CORP_ID = child.getAttributeValue("value");
				}else if ("corpsecret".equals(key)){
					CORP_SECRET = child.getAttributeValue("value");
				}
				/**
				 * 获取页面中配置的agentid、userid（新增一个属性 必填）
				 * 如果小助手中agentid都是相同的，给默认值。如果不相同，则必须配
				 */
				else if ("agentid".equals(key)){
					agentId = child.getAttributeValue("value");
				}else if ("userid".equals(key)){
					userId = child.getAttributeValue("value");
				}
			}
		}
		if(_Token!=null&&!_Token.isExpired())
			return _Token;
		
		String requestUrl = token_url.replace("appid", CORP_ID).replace("appsecret", CORP_SECRET);
		// 发起GET请求获取凭证
		JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);

		if (null != jsonObject)
		{
			token = new Token();
			token.setAccessToken(jsonObject.getString("access_token"));
			_Token = token;
		}
		log.error("获取钉钉接口参数成功,CORP_ID:"+CORP_ID+",CORP_SECRET:"+CORP_SECRET);
	} catch (Exception e) {
		token = null;
		log.error("获取钉钉接口参数异常,"+e.getMessage());
		e.printStackTrace();
	}finally {
		if (reader != null) 
			reader.close();
	}
	
		
		return token;
	}
	
	/**
	 * 发送钉钉图文消息
	 * @param Token
	 * @param userid
	 * @param agentid
	 * @param title
	 * @param msg_content 消息正文
	 * @return
	 */
	public static boolean sendDDMessage(String Token,String userid,String agentid,String title,String msg_content) {
		HashMap map = new HashMap();
		map.put("touser", userid);
		map.put("agentid", agentid);
		map.put("msgtype", "oa");
			HashMap msg = new HashMap();
				HashMap body=new HashMap();
				body.put("title", title);
				body.put("content", CommonUtil.delHTMLTag(msg_content));
				body.put("image", "http://www.hjsoft.com.cn:8089/UserFiles/Image/warn.png");
			msg.put("body",body);
		map.put("oa", msg);
		String ss = JSONObject.fromObject(map).toString();
		String url="https://oapi.dingtalk.com/message/send?access_token="+Token;
		JSONObject json=httpsRequest(url,"POST",ss);
		return true;
	}
	
	/**
	 * 发送纯文字钉钉消息
	 * @param Token
	 * @param userid
	 * @param agentid
	 * @param title
	 * @param msg_content 消息正文
	 * @return
	 */
	public static boolean sendTextDDMessage(String Token,String userid,String agentid,String title,String msg_content) {
		HashMap map = new HashMap();
		map.put("touser", userid);
		map.put("agentid", agentid);
		map.put("msgtype", "text");
			HashMap msg = new HashMap();
			msg.put("content",title+"\n  "+ CommonUtil.delHTMLTag(msg_content));
		map.put("text", msg);
		String ss = JSONObject.fromObject(map).toString();
		System.out.println(ss);
		String url="https://oapi.dingtalk.com/message/send?access_token="+Token;
		JSONObject json=httpsRequest(url,"POST",ss);
		System.out.println(json);
		return true;
	}
	
	/**
	 * 通过钉钉接口参数配置的userid 获取钉钉中人员id
	 * @param username
	 * @param Nbase
	 * @return
	 */
	public static String getUserId(String username,List Nbase){
		//如果与系统同步字段为“username”，直接返回username
		if("username".equals(userId))
			return username;
		//如果与系统同步字段不为“username”，通过username（认证用户名）查询到自定义同步字段
		//获取认证用户名对应字段
		Connection ehrConn = null;
		ResultSet res=null;
		
		String userid="";
		try{
			// 获得ehr数据库连接
			ehrConn = AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(ehrConn);
			DbNameBo dbbo = new DbNameBo(ehrConn);
			String loginField = dbbo.getLogonUserNameField();
			for(Object obj:Nbase){
				String pre=obj.toString();
				String sql="select "+userId+" from "+pre+"A01 where ";
				sql+= loginField+ " = "+username;
				res=dao.search(sql);
				while(res.next()){//判断是否还有下一行  
					userid=res.getString(userId);
		        }  
			}
		}catch(Exception e){
			
		}finally{
			try{
				res.close();
				ehrConn.close();
			}catch(SQLException se){
				System.out.println(se);
			}
			
		}
		return userid;
	}
	
	/**
	 * 钉钉发送消息方法
	 * @param username 
	 * @param title
	 * @param msg_content 消息内容
	 * @param Nbase 
	 * @param flag 发送文字消息为"text",其他为图文消息
	 * @return
	 */
	public static boolean sendDDmessageTotal(String username,String title,String msg_content,List Nbase,String flag){
		//1、获取钉钉中token
		Token t=new Token();
		t=DDWarnUtil.getToken();
		
		//2、获取人员 通过userid获取钉钉中的人员明细 messageUrl picUrl title text
		String userid=getUserId(username,Nbase);
		
		//3、发消息
		//判断为图文消息还是文本消息
		if("text".equals(flag))
			sendTextDDMessage(t.getAccessToken(),userid,agentId,title,msg_content);
		else
			sendDDMessage(t.getAccessToken(),userid,agentId,title,msg_content);
		
		//看微信为什么要传bollean
		return true;
	}
	
}
