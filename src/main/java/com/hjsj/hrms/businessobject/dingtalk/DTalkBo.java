package com.hjsj.hrms.businessobject.dingtalk;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiMessageCorpconversationGetsendresultRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiMessageCorpconversationGetsendresultResponse;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hjsj.weixin.message.resp.Article;
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
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 钉钉发消息接口
 * @author xus
 *17/5/3
 */
public class DTalkBo {
	private static Logger log =  Logger.getLogger(DTalkBo.class.getName());

	// 凭证获取（GET）
	private final static String token_url = "https://oapi.dingtalk.com/gettoken?corpid=appid&corpsecret=appsecret";
	//订阅号、服务号全局token 不会失效，token失效后则重新获取   xus 2017/3/29
	private static Token _Token;
	private static HashMap<String,Token> TokenMap = null;
	//系统中对应的人员id字段
	private static String cropid = "";
	private static String cropSecret = "";
	public static String userId = "";
	private static String agentId = "";
	private static boolean isAllparam = false;//所有参数已经配置
	private static HashMap<String,HashMap<String,String>> functionMap = null;//菜单map
	private static String msg_AppKey = "";
	private static String msg_AppSecret = "";
	
	/**
	 * 初始化，获取第三方接口参数配置
	 */
	static{
		try{
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
						cropid = child.getAttributeValue("value");
					}else if ("corpsecret".equals(key)){
						cropSecret = child.getAttributeValue("value");
					}
					else if ("agentid".equals(key)){
						agentId = child.getAttributeValue("value");
					}else if ("userid".equals(key)){
						userId = child.getAttributeValue("value");
					}else if ("msg_AppKey".equals(key)){
						msg_AppKey = child.getAttributeValue("value");
					}else if ("msg_AppSecret".equals(key)){
						msg_AppSecret = child.getAttributeValue("value");
					}else if("funcsecret".equals(key)){
						List funcList = child.getChildren();
						functionMap = new HashMap<String, HashMap<String,String>>();
						for (int j = 0; j < funcList.size(); ++j)
						{
							Element func = (Element) funcList.get(j);
							String menuid = func.getAttributeValue("menuid");
							//存储的menuid属性前边有“dd_”标识，此处去掉标识
							menuid = menuid.substring(3);
							if(functionMap.containsKey(menuid)){
								continue ; 
							}
							String appKey = func.getAttributeValue("appKey");
							String appSecret = func.getAttributeValue("appSecret");
							HashMap infoMap = new HashMap<String,String>();
							infoMap.put("appKey", appKey);
							infoMap.put("appSecret", appSecret);
							functionMap.put(menuid, infoMap);
						}
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		//新版钉钉去掉cropSecret参数  wangbo 20191106
		if(!cropid.isEmpty()/**&&!cropSecret.isEmpty()*/&&!agentId.isEmpty()&&!userId.isEmpty()) {
            isAllparam= true;
        }
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
	 * 发送https请求
	 * @param requestUrl 请求地址
	 * @param requestMethod 请求方式（GET、POST）
	 * @param outputStr 提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	private static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
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
	 * 微信服务号 获取接口访问凭证
	 * @param appid 凭证
	 * @param appsecret 密钥
	 * @return
	 */
	private static Token getToken() {
		
	Token token = null;
	try {
		if(_Token!=null&&!_Token.isExpired()) {
            return _Token;
        }
		
		String requestUrl = token_url.replace("appid", cropid).replace("appsecret", cropSecret);
		// 发起GET请求获取凭证
		JSONObject jsonObject =httpsRequest(requestUrl, "GET", null);

		if (null != jsonObject)
		{
			token = new Token();
			token.setAccessToken(jsonObject.getString("access_token"));
			_Token = token;
		}
		log.error("获取钉钉接口参数成功,CORP_ID:"+cropid+",CORP_SECRET:"+cropSecret);
	} catch (Exception e) {
		token = null;
		log.error("获取钉钉接口参数异常,"+e.getMessage());
		e.printStackTrace();
	}
	
		
		return token;
	}
	
	/**
	 * 
	 * 发送钉钉图文消息
	 * @param Token
	 * @param userid
	 * @param agentid
	 * @param title
	 * @param msg_content 消息正文
	 * @return
	 */
	private static boolean sendPicMessageToPerson(String Token,String userid,String title,String msg_content,String picUrl, String url ) {
		HashMap map = new HashMap();
		map.put("touser", userid);
		map.put("agentid", agentId);
		map.put("msgtype", "link");
				HashMap body=new HashMap();
				body.put("title", title);
				body.put("text", CommonUtil.delHTMLTag(msg_content));
				body.put("picUrl", picUrl);
				body.put("messageUrl", url);
		map.put("link", body);
		String ss = JSONObject.fromObject(map).toString();
		log.error(ss);
		String sendUrl="https://oapi.dingtalk.com/message/send?access_token="+Token;
		JSONObject json=httpsRequest(sendUrl,"POST",ss);
		log.error(json);
		return true;
	}
	
	/**
	 * 发送纯文字消息
	 * @param Token
	 * @param userid
	 * @param agentid
	 * @param title
	 * @param msg_content 消息正文
	 * @return
	 */
	private static boolean sendMessageToPerson(String Token,String userid,String title,String msg_content) {
		HashMap map = new HashMap();
		map.put("touser", userid);
		map.put("agentid", agentId);
		map.put("msgtype", "text");
			HashMap msg = new HashMap();
			msg.put("content",title+"\n  "+msg_content);
		map.put("text", msg);
		String ss = JSONObject.fromObject(map).toString();
		log.error(ss.substring(ss.indexOf('{')+1, ss.lastIndexOf('}')));
		String url="https://oapi.dingtalk.com/message/send?access_token="+Token;
		JSONObject json=httpsRequest(url, "POST", ss);
		log.error(json);
		return true;
	}
	
	/**
	 * 通过a0100  获取钉钉中人员id
	 * @param a0100
	 * @param Nbase
	 * @return
	 */
	private static String getUserId(String A0100,String Nbase){
		//如果与系统同步字段为“A0100”，直接返回A0100
		if("A0100".equals(userId)) {
            return A0100;
        }
		//如果与系统同步字段不为“A0100”，通过A0100查询到自定义同步字段
		//获取认证用户名对应字段
		Connection ehrConn = null;
		ResultSet res=null;
		String sql="";
		String userid="";
		try{
			// 获得ehr数据库连接
			ehrConn = AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(ehrConn);
			DbNameBo dbbo = new DbNameBo(ehrConn);
			String loginField = dbbo.getLogonUserNameField();
			if("username".equals(userId)) {
                sql="select "+loginField+" userid from "+Nbase+"A01 where A0100 = '"+A0100+"'";
            } else {
                sql="select "+userId+" userid from "+Nbase+"A01 where A0100 = '"+A0100+"'";
            }
			res=dao.search(sql);
			while(res.next()){//判断是否还有下一行  
				userid=res.getString("userid");
	        }  
		}catch(Exception e){
			
		}finally{
			PubFunc.closeDbObj(res);;
			PubFunc.closeDbObj(ehrConn);
		}
		return userid;
	}
	
	/**
	 * 通过username 获取钉钉中人员id
	 * @param username
	 * @return
	 */
	private static String getUserId(String username){
		//如果与系统同步字段为“username”，直接返回username
		if("username".equals(userId)) {
            return username;
        }
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
			//获取所有的库前缀
			List Nbase =DataDictionary.getDbpreList();
			String loginField = dbbo.getLogonUserNameField();
			String strsql="";
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<Nbase.size();i++){
				Object obj=Nbase.get(i);
				String pre=obj.toString();
				sb.append("select ").append(userId).append(" userid from ").append(pre).append("A01 where ").append(loginField).append(" = '").append(username).append("' ");
				strsql += "select "+userId+" userid from "+pre+"A01 where "+loginField+" = '"+username+"' ";
				if(i<Nbase.size()-1){
					sb.append(" union ");
					strsql += " union ";
				}
			}
//			strsql=strsql.substring(0,strsql.length()-6 );
			log.error(strsql);
			log.error("sb sql-------------->"+sb.toString());
//			res=dao.search(strsql);
			res=dao.search(sb.toString());
			while(res.next()){//判断是否还有下一行  
				userid=res.getString("userid");
	        }  
//			for(Object obj:Nbase){
//				String pre=obj.toString();
//				String sql="select "+userId+" from "+pre+"A01 where ";
//				sql+= loginField+ " = "+username;
//				res=dao.search(sql);
//				while(res.next()){//判断是否还有下一行  
//					userid=res.getString(userId);
//		        }  
//			}
		}catch(Exception e){
			
		}finally{
			PubFunc.closeDbObj(res);
			PubFunc.closeDbObj(ehrConn);
		}
		return userid;
	}
	
	/**
	 * 通过username发送单条消息方法
	 * @param username 
	 * @param title
	 * @param msg_content 消息内容
	 * @param Nbase 
	 * @param flag 发送文字消息为"text",其他为图文消息
	 * @return
	 */
	public static boolean sendMessage(String username,String title,String msg_content,String picUrl, String url){
		//xus 19/7/12  标题去空格
		title = title.trim();
		String userid = getUserId(username);
		HashMap msgParams = new HashMap();
		/**String title = (String) msgParams.get("title");
			String text = (String) msgParams.get("text");
			String messageUrl = (String) msgParams.get("msgurl");
			String url = (String) msgParams.get("url");**/
		if(url.length()==0||url==null){
			msgParams.put("type", "text");
			msgParams.put("text", title+"\n  "+msg_content);
		}else{
			msgParams.put("type", "link");
			msgParams.put("title", title);
			msgParams.put("text", msg_content);
			msgParams.put("messageUrl", "");
//			msgParams.put("messageUrl", picUrl);
			msgParams.put("url", url);
		}
			
		if("发送成功！".equals(sendMessageNew(userid, msgParams))) {
            return true;
        } else {
            return false;
        }
	}
//	public static boolean sendMessage(String username,String title,String msg_content,String picUrl, String url){
//		if(username==null||username.length()<=0){
//			log.info("username is not exist!");
//			return false;
//		}
//		//1、判断第三方接口参数是否配置了所有的参数
//		if(!isAllparam)
//			return false;
//		
//		//2、获取钉钉中token
//		Token t=new Token();
//		t=DTalkBo.getToken();
//		
//		//3、获取人员 通过username获取钉钉中的人员id  userid
//		String userid=getUserId(username);
//		
//		//4、发消息
//		if(url.length()==0||url==null)
//			sendMessageToPerson(t.getAccessToken(),userid,title,msg_content);
//		else
//			sendPicMessageToPerson(t.getAccessToken(),userid,title,msg_content,picUrl,  url);
//		
//		//看微信为什么要传bollean
//		return true;
//	}
	
	
	
	/**
	 * 发送单条文本消息给多人（username）
	 * @param username
	 * @param title
	 * @param msg_content
	 */
	public static boolean sendMessage(List usernames,String title,String msg_content,String picUrl, String url){
		if(usernames.size()<=0){
			log.info("username is null!");
			return false;
		}
		if(usernames.size()>1000){
			log.info("usernames length more than 1000!");
			return false;
		}
		for(Object obj:usernames){
			String username=obj.toString();
			sendMessage( username, title, msg_content, picUrl,  url);
		}
		return true;
	}
	
	/**
	 * 通过A0100发送单条消息方法
	 * @param A0100
	 * @param Nbase
	 * @param title
	 * @param msg_content 消息内容
	 * @return
	 */
	public static boolean sendMessage(String A0100,String Nbase,String title,String msg_content, String picUrl, String url){
		//xus 19/7/12  标题去空格
		title = title.trim();
		if(Nbase==null||Nbase.length()!=3){
			log.error("nbase is error!");
			return false;
		}
		if(A0100==null||A0100.length()<=0||Nbase.length()==0){
			log.info("A0100 is not exist!");
			return false;
		}
		//判断第三方接口参数是否配置了所有的参数
		if(!isAllparam) {
            return false;
        }
		//获取人员 通过A0100，Nbase获取钉钉中的人员id  userid
		String userid=getUserId(A0100,Nbase);
		if(userid==null||"".equals(userid)) {
            return false;
        }
		HashMap msgParams = new HashMap();
		/**String title = (String) msgParams.get("title");
			String text = (String) msgParams.get("text");
			String messageUrl = (String) msgParams.get("msgurl");
			String url = (String) msgParams.get("url");**/
		if(url.length()==0||url==null){
			msgParams.put("type", "text");
			msgParams.put("text", title+"\n  "+msg_content);
		}else{
			msgParams.put("type", "link");
			msgParams.put("title", title);
			msgParams.put("text", msg_content);
			msgParams.put("messageUrl", picUrl);
			msgParams.put("url", url);
		}
			
		if("发送成功！".equals(sendMessageNew(userid, msgParams))) {
            return true;
        } else {
            return false;
        }
	}
//	public static boolean sendMessage(String A0100,String Nbase,String title,String msg_content, String picUrl, String url){
//		if(Nbase==null||Nbase.length()!=3){
//			log.error("nbase is error!");
//			return false;
//		}
//		if(A0100==null||A0100.length()<=0||Nbase.length()==0){
//			log.info("A0100 is not exist!");
//			return false;
//		}
//		
//		//1、判断第三方接口参数是否配置了所有的参数
//		if(!isAllparam)
//			return false;
//		
//		//2、获取钉钉中token
//		Token t=new Token();
//		t=DTalkBo.getToken();
//		
//		//3、获取人员 通过A0100，Nbase获取钉钉中的人员id  userid
//		String userid=getUserId(A0100,Nbase);
//		if(userid==null||"".equals(userid))
//			return false;
//		//4、发消息
//		if(url.length()==0||url==null)
//			sendMessageToPerson(t.getAccessToken(),userid,title,msg_content);
//		else
//			sendPicMessageToPerson(t.getAccessToken(),userid,title,msg_content,picUrl,  url);
//		
//		return true;
//	}
	
	/**
	 * 发送单条消息给多人（A0100s）
	 * @param username
	 * @param title
	 * @param msg_content
	 */
	public static boolean sendMessage(List A0100s,String Nbase,String title,String msg_content, String picUrl, String url){
		if(Nbase==null||Nbase.length()!=3){
			log.error("nbase is error!");
			return false;
		}
		if(A0100s==null||A0100s.size()<=0){
			log.info("A0100s is null!");
			return false;
		}
		if(A0100s.size()>1000){
			log.info("A0100s more than 1000!");
			return false;
		}
		for(Object obj:A0100s){
			String A0100=obj.toString();
			sendMessage( A0100,Nbase, title, msg_content,  picUrl,  url);
		}
		return true;
	}
	/**
	 * 发送多条消息到多人
	 * @param usernames
	 * @param articles
	 * @return
	 */
	public static boolean sendMessage(List usernames, List articles) {
		boolean flag = false;
		try {
			if(usernames==null){
				log.error("usernames is null!");
				return false;
			}
			if(articles==null){
				log.error("articles is null!");
				return false;
			}
			if(usernames.size()>1000){
				log.error("userid more than 1000!");
				return false;
			}
			for(int i=0;i<articles.size();i++){
				Article article = (Article)articles.get(i);
				String description = article.getDescription();
				description = CommonUtil.delHTMLTag(description);
				article.setDescription(description);
				String picUrl=article.getPicUrl()==null?"":article.getPicUrl();
				String url=article.getUrl()==null?"":article.getUrl();
				sendMessage(usernames, article.getTitle(), description,picUrl , url);
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} 
		return flag;
	}
	/**
	 * 发送多条消息到组织机构下的人员
	 * @param orgids
	 * @param articles
	 * @return
	 */
	public static boolean sendMsgToDept(List orgids, List articles){
		boolean flag=false;
		if(orgids==null){
			log.error("orgids is null!");
			return false;
		}
		if(articles==null){
			log.error("articles is null!");
			return false;
		}
		if(orgids.size()>100){
			log.error("deptid more than 100!");
			return false;
		}
		
		Token t = DTalkBo.getToken();
		String url="";
		String picUrl="";
		List DDids= getDDidByOrg(orgids);
		if(DDids==null||DDids.size()==0){
			log.info("DDids is null");
			return false;
		}
		for(int i=0;i<articles.size();i++){
			Article article = (Article)articles.get(i);
			String title=article.getTitle();
			String description = article.getDescription();
			description = CommonUtil.delHTMLTag(description);
			article.setDescription(description);
			url=article.getUrl()==null?"":article.getUrl();
			picUrl=article.getPicUrl()==null?"":article.getPicUrl();
			for(Object obj:DDids){
				String userid=obj.toString();
				if(picUrl.length()==0&&url.length()==0) {
                    if(sendMessageToPerson(t.getAccessToken(),userid,title,description)) {
                        flag = true;
                    } else
                    if(sendPicMessageToPerson(t.getAccessToken(),userid,title,description,picUrl,  url)) {
                        flag = true;
                    }
                }
			}
			
		}
		return flag;
	}
	/**
	 * 发送单条消息到组织机构下的人员
	 * @param orgids
	 * @param title
	 * @param description
	 * @param picUrl
	 * @param url
	 * @return
	 */
	public static boolean sendMsgToDept(List orgids, String title,String description,String picUrl,String url){
		boolean flag = false;
		if(orgids==null){
			log.error("orgids is null!");
			return false;
		}
		if(orgids.size()>100){
			log.error("deptid more than 100!");
			return false;
		}
		List DDids= getDDidByOrg(orgids);
		if(DDids==null||DDids.size()==0){
			log.info("DDids is null");
			return false;
		}
		Token t = DTalkBo.getToken();
		for(Object obj:DDids){
			String userid=obj.toString();
			if(url.length()==0||url==null) {
                if(sendMessageToPerson(t.getAccessToken(),userid,title,description)) {
                    flag = true;
                } else
                if(sendPicMessageToPerson(t.getAccessToken(),userid,title,description,picUrl,  url)) {
                    flag = true;
                }
            }
		}
		return flag;
	}
	
	/**
	 * 	通过 userid 发送消息
	 * @param userid
	 * @param title
	 * @param msg_content
	 * @param picUrl
	 * @param url
	 * @return
	 */
	public static boolean sendMessageByUserId(String userid,String title,String msg_content,String picUrl, String url){
		Token t=new Token();
		t=DTalkBo.getToken();
		if(url.length()==0||url==null) {
            sendMessageToPerson(t.getAccessToken(),userid,title,msg_content);
        } else {
            sendPicMessageToPerson(t.getAccessToken(),userid,title,msg_content,picUrl,  url);
        }
		return true;
	}
	/**
	 * 通过组织机构ids获取钉钉ids
	 * @param orgids
	 * @return
	 */
	private static List getDDidByOrg(List orgids) {
		Connection ehrConn=null;
		ResultSet res=null;
		ContentDAO dao=null;
		List DDids=new ArrayList();
		String insql="";
		for(int i=0;i<orgids.size();i++){
			Object obj=orgids.get(i);
			String orgid=obj.toString();
			insql+="'"+orgid+"'";
			if(i!=orgids.size()-1) {
                insql+=",";
            }
		}
		String strsql="";
		StringBuffer sb=new StringBuffer();
		List Nbase =DataDictionary.getDbpreList();
		try{
			ehrConn = AdminDb.getConnection();
			dao=new ContentDAO(ehrConn);
			DbNameBo dbbo = new DbNameBo(ehrConn);
			String loginField = dbbo.getLogonUserNameField();
			for(int i=0;i<Nbase.size();i++){
				Object obj=Nbase.get(i);
				String nbase=obj.toString();
				if("username".equals(userId)){
					if(loginField==null) {
                        return DDids;
                    }
					sb.append("select ").append(loginField).append(" userid from ").append(nbase).append("A01 where E0122 in (").append(insql).append(") ");
					strsql+="select "+loginField+" userid from "+nbase+"A01 where E0122 in ("+insql+") ";
				}else{
					if(userId==null) {
                        return DDids;
                    }
					sb.append("select ").append(userId).append(" userid from ").append(nbase).append("A01 where E0122 in (").append(insql).append(") ");
					strsql+="select "+userId+" userid from "+nbase+"A01 where E0122 in ("+insql+") ";
				}
				if(i!=Nbase.size()-1){
					sb.append("union ");
					strsql+="union ";
				}
			}
			log.error("sql------->"+sb.toString());
			res=dao.search(sb.toString());
//			res=dao.search(strsql);
			while(res.next()){//判断是否还有下一行  
				DDids.add(res.getString("userid"));
	        }  
		}catch(Exception e){
			
		}finally{
			PubFunc.closeDbObj(res);
			PubFunc.closeDbObj(ehrConn);
		}
		return DDids;		
	}
	
	/**
	 * 钉钉新版发送消息接口
	 * @param usernames：支持多个username发送消息以“，”分割 例：usernames="zhangjun,lisi";
	 * @param msgParams
	 * 			消息参数的Map。{
	 * 							type:"text/link/image/file/markdown/oa",  (消息类型，必填。text和link最常用分别为纯文本消息和图文消息)
	 * 							text:"消息内容",  							  (消息内容，必填，text和link消息内容都为此参数)
	 * 							agentid:"894374",						  (发送消息的功能id，参照钉钉后台，选填)
	 * 							msgmenu:"10",							  (获取钉钉消息accesstoken的功能菜单，选填，home:移动门户；1：我的薪酬；10：业务办理；11：项目工时；13：移动考勤；14：员工档案；......)
	 * 							title:"标题",								  (标题，link)
	 * 							msgurl:"图片路径",							  (图片路径，link)
	 * 							url:"跳转路径"								  (跳转路径，link)
	 * 						}
	 * @return
	 * @throws ApiException
	 */
	public static String sendMessageNew(String usernames,HashMap msgParams) {
		
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");

		OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
		//若msgParams参数中没有agentid，msgmenu参数，则取系统中默认的参数
		String agent = (String) msgParams.get("agentid")==null?agentId:(String) msgParams.get("agentid");
		if(agent == null ||"".equals(agent)) {
            return "agentid不能为空！";
        }
		String reg = "^[0-9]+(.[0-9]+)?$";
	    if(!agent.matches(reg)){
	        return "agentid不能为字符类型！";
	    }
		String msgAppKey = (String) msgParams.get("appKey")==null?msg_AppKey:(String) msgParams.get("appKey");
		String msgAppSecret = (String) msgParams.get("appSecret")==null?msg_AppSecret:(String) msgParams.get("appSecret");
		
		request.setUseridList(usernames);
		request.setAgentId(Long.parseLong(agent));
		request.setToAllUser(false);

		//根据不同的消息类型发送消息
		String type = (String) msgParams.get("type");
		OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
		if("text".equals(type)){
			String text = (String) msgParams.get("text");
			msg.setMsgtype("text");
			msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
			msg.getText().setContent(text);
		}
		/*else if("image".equals(type)){
			String media = (String) msgParams.get("media");
			msg.setMsgtype("image");
			msg.setImage(new OapiMessageCorpconversationAsyncsendV2Request.Image());
			msg.getImage().setMediaId(media);
		}else if("file".equals(type)){
			String media = (String) msgParams.get("media");
			msg.setMsgtype("file");
			msg.setFile(new OapiMessageCorpconversationAsyncsendV2Request.File());
			msg.getFile().setMediaId(media);
		}*/
		else if("link".equals(type)){
			String title = (String) msgParams.get("title");
			String text = (String) msgParams.get("text");
			//将html代码还原 haosl bug 58082    20200217
			text = PubFunc.reverseHtml(text).replaceAll("<br />", "\n");
			String picurl = (String) msgParams.get("picurl");
			String url = (String) msgParams.get("url");
//			picurl="http://www.hjsoft.com.cn:8089/"+WeiXinBo.MESSAGE_PICTURE_NOTICE;
			if(picurl == null || "null".equals(picurl) || "".equals(picurl)) {
                picurl = SystemConfig.getPropertyValue("w_selfservice_url")+"/"+WeiXinBo.MESSAGE_PICTURE_NOTICE;
            }
			msg.setMsgtype("link");
			msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
			msg.getLink().setTitle(title);
			msg.getLink().setText(text);
			msg.getLink().setMessageUrl(url);
			msg.getLink().setPicUrl(picurl);
		}
		/*else if("markdown".equals(type)){
			String text = (String) msgParams.get("text");
			String title = (String) msgParams.get("title");
			msg.setMsgtype("markdown");
			msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
			msg.getMarkdown().setText(text);
			msg.getMarkdown().setTitle(title);
		}else if("oa".equals(type)){
			String text = (String) msgParams.get("text");
			String title = (String) msgParams.get("title");
			msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
			msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
			msg.getOa().getHead().setText(title);
			msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
			msg.getOa().getBody().setContent(text);
			msg.setMsgtype("oa");
		}*/
		
		request.setMsg(msg);
		
		try {
			String accessToken = getNewAccessToken(msgAppKey,msgAppSecret);
			OapiMessageCorpconversationAsyncsendV2Response response;
			response = client.execute(request,accessToken);
		
			OapiMessageCorpconversationGetsendresultResponse resultresponse = getNewSendMsgResult(Long.parseLong(agent), response.getTaskId(),accessToken);
			if(resultresponse.getErrcode()!=0){
				String returnMsg = resultresponse.getErrmsg();
				//19/4/1 xus 钉钉发送消息 返回报错信息处理
				if(returnMsg.indexOf("ding talk error[")>-1){
					returnMsg = returnMsg.substring(16, returnMsg.length()-1);
					if(returnMsg==null||returnMsg.length()==0) {
                        return "发送失败！";
                    }
					String splStrs[] = returnMsg.split(",");
					for(int i = 0;i<splStrs.length;i++){
						//19/4/1 xus 钉钉发送消息 返回报错信息处理:截取明确的失败信息。
						if(splStrs[i].indexOf("submsg=")>-1&&splStrs[i].length()>7){
							returnMsg = splStrs[i].substring(7);
							break;
						}
					}
				}
				return returnMsg;
			}
			List<String> failedList = resultresponse.getSendResult().getFailedUserIdList();
			List<String> invalidList = resultresponse.getSendResult().getInvalidUserIdList();
			List<String> forbiddenList = resultresponse.getSendResult().getForbiddenUserIdList();
			if((failedList!=null&&failedList.contains(usernames))||(invalidList!=null&&invalidList.contains(usernames))||(forbiddenList != null&&forbiddenList.contains(usernames))){
				return "发送失败！";
			}else{
				return "发送成功！";
			}
		} catch (ApiException e) {
			e.printStackTrace();
			return "发送失败！";
		}
	}
	
	/**
	 * 获取发送结果信息
	 * @param agentId
	 * @param taskId
	 * @param msgmenu 
	 * @return
	 * @throws ApiException
	 */
	public static OapiMessageCorpconversationGetsendresultResponse getNewSendMsgResult(Long agentId,Long taskId, String accessToken) throws ApiException{
		HashMap resultMap = new HashMap();
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/getsendresult");
		OapiMessageCorpconversationGetsendresultRequest request  = new OapiMessageCorpconversationGetsendresultRequest();
		request.setAgentId(agentId);
		request.setTaskId(taskId);
		OapiMessageCorpconversationGetsendresultResponse response = client.execute(request, accessToken);
		return response;
	}
	/**
	 * 新版钉钉获取AccessToken
	 * @param menuid
	 * @return
	 */
	public static String getNewAccessToken(String appKey,String appSecret) {
		String accToken = "";
		//功能菜单的map不为空 并 菜单id不为空 并 菜单id不为"main"
		if(appKey!=null&&appSecret!=null&&!"null".equals(appKey)&&!"null".equals(appSecret)&&!"".equals(appKey)&&!"".equals(appSecret)){
			if(TokenMap !=null && TokenMap.containsKey(appKey) && !TokenMap.get(appKey).isExpired()){
				log.info("钉钉获取本地未失效func_accesstoken成功!");
				return TokenMap.get(appKey).getAccessToken();
			}
			try {
				DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
				OapiGettokenRequest request = new OapiGettokenRequest();
				request.setAppkey(appKey);
				request.setAppsecret(appSecret);
				request.setHttpMethod("GET");
				OapiGettokenResponse response = client.execute(request);
				if(response.getErrcode() == 0){
					accToken = response.getAccessToken();
					Token token= new Token();
					token.setAccessToken(accToken);
					log.info("钉钉获取新func_accesstoken成功!");
					if(TokenMap==null) {
                        TokenMap = new HashMap<String, Token>();
                    }
					TokenMap.put(appKey, token);
				}else{
					log.error("钉钉获取func_accesstoken失败!"+response.getErrmsg());
				}
			} catch (ApiException e) {
				log.error("钉钉获取func_accesstoken失败!"+e.getMessage());
				return accToken;
			}
			return accToken;
		}
		
		//旧版 通过CORP_ID,CORP_SECRET获取accessToken
		getToken();
		//19/4/1 xus _Token为空时 返回""
		if(_Token == null || "".equals(_Token)){
			return "";
		}
		accToken = _Token.getAccessToken();

		return accToken;
	}
}
