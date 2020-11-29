package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.weixin.utils.CommonUtil;
import com.hjsj.weixin.utils.Token;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 *
 * @Titile: SettingWXEnterpriseParamTrans
 * @Description:发布企业微信号应用配置
 * @Company:hjsj
 * @Create time: 2018年6月27日下午3:15:01
 * @author: wangbs
 * @version 1.0
 *
 */
public class SettingWXEnterpriseParamTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		StringReader reader = null;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			String corpId = "";//企业号corpid
			String w_selfservice_address = "";//微信服务地址
			String corpsecret = "";//企业号 corpsecret
			RecordVo recordVo = ConstantParamter.getConstantVo("SS_QQWX");
			// 判断数据库中是否存在该字段
			if (recordVo != null) {
				// 读取xml转换为Document
				Document doc;
				doc = PubFunc.generateDom(recordVo.getString("str_value"));
				// 读取根节点
				Element root = doc.getRootElement();
				List list = root.getChildren();
				Element child;
				// 循环提取数据,变量重新赋值
				for (int i = 0; i < list.size(); i++) {
					String key = "";
					String value = "";
					String target = "";
					child = (Element) list.get(i);
					List AttributeList = child.getAttributes();
					for(int j=0;j<AttributeList.size()-1;j++) {
						String attribute = AttributeList.get(j).toString();
						int index = attribute.indexOf("=");
						int beginIndex = index+2;
						int endIndex = attribute.length()-2;
						target = attribute.substring(beginIndex,endIndex);
						if(j==0) {
							if("corpid".equalsIgnoreCase(target)||"w_selfservice_address".equalsIgnoreCase(target) || "corpsecret".equalsIgnoreCase(target)) {
								key = target;
							}else {
								break;
							}
						}else {
							value = target;
							if("corpid".equals(key)) {
								corpId = value;
							}else if("w_selfservice_address".equals(key)){
								w_selfservice_address = value;
							}else if("corpsecret".equals(key)){
								corpsecret = value;
							}
						}
					}
				}
			}

			RecordVo releaseVo = (RecordVo) this.formHM.get("releaseInfo");
			String wxitemid =releaseVo.getString("wxitemid");
			String wxname =releaseVo.getString("wxname");
			String appid =releaseVo.getString("appid");
			String appSecret =releaseVo.getString("app_secret");
			String url =releaseVo.getString("url");
			String app_type =releaseVo.getString("app_type");
			String description =releaseVo.getString("description");
			String str_value =releaseVo.getString("str_value");
			
			//获取ACCESS_TOKEN   corpId & secret 企业应用 secret参数
			CommonUtil._QyToken = null;
			Token token = CommonUtil.getQyToken(corpId,appSecret);

			//上传应用头像
			String photoUrl = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
			photoUrl = photoUrl.replace("ACCESS_TOKEN", token.getAccessToken());
			photoUrl = photoUrl.replace("TYPE", "image");

			Document doc = null;
			doc = PubFunc.generateDom(str_value);
			Element root = doc.getRootElement();
			//List elementList = root.getChildren();
			//Element logoParam = (Element) elementList.get(0);
			Element logoParam = root.getChild("param");
			logoParam.getAttributeValue("");
			String path = logoParam.getAttributeValue("path");
			path = PubFunc.decrypt(path);
			String filename = logoParam.getAttributeValue("filename");
			filename = PubFunc.decrypt(filename);
			String fileType = filename.split("\\.")[1];
			filename = filename.split("\\.")[0];

			String resp = HttpResponseParam(photoUrl, path, filename, fileType, "POST");
			JSONObject jsonObject = JSONObject.fromObject(resp);
			String media_id = jsonObject.getString("media_id");

			if(app_type == null ||"".equalsIgnoreCase(app_type)){//先更新应用信息，在发布自定义菜单
				relaseAppMain(url, app_type, corpId, appid, media_id, wxname, description, token);
				String result = (String) this.formHM.get("result");
				if(!"1".equalsIgnoreCase(result))
					return;
				relaseAppCustomMemu(url,corpId,appid,token,str_value);
			}else{
				relaseAppMain(url, app_type, corpId, appid, media_id, wxname, description, token);
			}

		} catch (Exception e) {
			e.printStackTrace();
			this.formHM.put("result","2");
		} finally {
			PubFunc.closeIoResource(reader);
		}
	}
	/**
	 * 发布企业号自定义菜单
	 * @param url 信任域名
	 * @param corpId 企业corpid
	 * @param agentId 应用id
	 * @param token 凭证
	 * @param str_value 自定义菜单数据
	 */
	private void relaseAppCustomMemu(String url,String corpId,String agentId,Token token,String str_value){
		StringBuffer str = new StringBuffer();
		HashMap settingBodyMap = new HashMap();
		List buttonList = new ArrayList();
		Document doc;
		try {
			doc = PubFunc.generateDom(str_value);

			Element rootEl = doc.getRootElement();
			List menusList = rootEl.getChildren("menu");
			for( int i = 0 ; i < menusList.size() ; i++){
				Map buttonMap = new HashMap();
				Element menuEl = (Element) menusList.get(i);
				String name = menuEl.getAttributeValue("menuname");
				String menuurl = menuEl.getAttributeValue("menuurl");
				buttonMap.put("name", name);
				List funcsList = menuEl.getChildren("function");
				if(funcsList == null || funcsList.size() == 0){
					if(menuurl.trim().length()==0)
						continue;
					buttonMap.put("key", "menu_"+(i+1));
					buttonMap.put("url", menuurl);
					buttonMap.put("type", "view");
					buttonList.add(buttonMap);
					continue;
				}
				List<Map<String,String>> sub_buttonList = new ArrayList<Map<String,String>>();
				for( int j = 0 ; j < funcsList.size() ; j++){
					Map<String,String> sub_buttonMap = new HashMap<String,String>();
					Element funcEl = (Element) funcsList.get(j);
					String functionname = funcEl.getAttributeValue("functionname");
					String functionurl = funcEl.getAttributeValue("functionurl");
					functionurl = functionurl.replace("${CORPID}",corpId);
					String key = "function_"+(i+1)+"_"+(j+1);
					sub_buttonMap.put("key", key);
					sub_buttonMap.put("name", functionname);
					sub_buttonMap.put("type", "view");
					sub_buttonMap.put("url", functionurl);
					sub_buttonList.add(sub_buttonMap);
				}
				buttonMap.put("sub_button", sub_buttonList);
				buttonList.add(buttonMap);
			}
			settingBodyMap.put("button", buttonList);
			JSONObject settingJson = JSONObject.fromObject(settingBodyMap);
			String settingJsonString = settingJson.toString();
			
			//发布企业号应用自定义菜单
			String wxAppCustomMenuUrl = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN&agentid=AGENTID";
			wxAppCustomMenuUrl = wxAppCustomMenuUrl.replace("ACCESS_TOKEN", token.getAccessToken());
			wxAppCustomMenuUrl = wxAppCustomMenuUrl.replace("AGENTID",agentId);
			JSONObject jsonObject = CommonUtil.httpsRequest(wxAppCustomMenuUrl, "POST", settingJsonString);
			if("ok".equalsIgnoreCase(jsonObject.getString("errmsg"))) {
				this.formHM.put("result", 1);
			}else{
				this.formHM.put("result", "3");
				this.formHM.put("error_msg", jsonObject.getString("errmsg"));
			}
				
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
            e.printStackTrace();
        }
	}

	/**
	 * 发布企业应用主页面功能
	 * @param w_selfservice_address  服务地址
	 * @param app_type 功能类型
	 * @param corpId  企业号 corpId
	 * @param wxitemid   应用agrentId
	 * @param media_id 微信上传logo号
	 * @param wxname  应用名称
	 * @param description 应用描述
	 * @param token 接口调用凭证
	 */
	private void relaseAppMain(String url,String app_type,String corpId,String agentId,String media_id,String wxname,String description,Token token){
		//应用地址home_url、state
		String state = "";
		String weixinInterfaceurl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
		StringBuffer REDIRECT_URI = new StringBuffer();
		REDIRECT_URI.append(url+"/w_selfservice/oauthservlet?dest="+url+"/w_selfservice/module/selfservice/");
		if("home".equals(app_type)) {
			state = "menuid=home&etoken=ETOKEN";
			REDIRECT_URI.append("home.jsp");
		}else if(app_type != null && app_type.trim().length() >0) {
			state = "menuid="+app_type+"&etoken=ETOKEN";
			REDIRECT_URI.append("index.jsp");
		}else{
			weixinInterfaceurl = "";
		}
		String redirect_uri = URLEncoder.encode(REDIRECT_URI.toString());
		weixinInterfaceurl = weixinInterfaceurl.replace("APPID", corpId);
		weixinInterfaceurl = weixinInterfaceurl.replace("REDIRECT_URI", redirect_uri);
		weixinInterfaceurl = weixinInterfaceurl.replace("STATE", SafeCode.encode(state));
		weixinInterfaceurl = weixinInterfaceurl.replace("SCOPE", "snsapi_base");

		StringBuffer str = new StringBuffer();
		str.append("{");
		str.append("\"agentid\":"+agentId+",");//应用agentId
		str.append("\"report_location_flag\":0,");//固定
		str.append("\"logo_mediaid\":\""+media_id+"\",");//logo图片
		str.append("\"name\":\""+wxname+"\",");//应用名称
		str.append("\"description\":\""+description+"\",");//应用简介
		str.append("\"redirect_domain\":\""+url+"\",");//信任域名
		str.append("\"isreportenter\":0,");//固定
		str.append("\"home_url\":\""+weixinInterfaceurl+"\"");//应用地址
		str.append("}");

		//创建企业微信应用
		String appUrl = "https://qyapi.weixin.qq.com/cgi-bin/agent/set?access_token=ACCESS_TOKEN";
		appUrl = appUrl.replace("ACCESS_TOKEN", token.getAccessToken());
		JSONObject appResp = CommonUtil.httpsRequest(appUrl,"POST",str.toString());

		if("ok".equals(appResp.getString("errmsg"))) {
			this.formHM.put("result", "1");
		}else {
			this.formHM.put("result", "3");
			this.formHM.put("error_msg", appResp.getString("errmsg"));
		}
	}


	/**
	 * 发送请求
	 * @param url   请求路径
	 * @param fileUrl 请求图片所在路径
	 * @param fileName 图片名称
	 * @param fileType 图片类型
	 * @param method 请求方式
	 * @return 请求返回参数
	 */
	public String HttpResponseParam(String url ,String fileUrl, String fileName,String fileType,String method){
		BufferedReader reader = null;
		OutputStream  out = null;
		String response = ""; //请求返回数据

		String boundary ="-------------------------acebdf13572468";
		String boundaryPrefix ="--";
		String newLine ="\r\n";

		try {
			URL httpUrl = new URL(url);//创建请求URL对象
			HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();//打开URL连接
			conn.setRequestMethod(method);//POST请求
			conn.setUseCaches(false); // 设置不用缓存
			//conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("connection", "keep-alive");//设置长连接
			conn.setUseCaches(false);//设置不要缓存
			conn.setDoOutput(true);//允许输出流
			conn.setDoInput(true);//允许输入流
			// 设置请求头参数  
			conn.setRequestProperty("charset", "UTF-8");//设置utf-8编码格式
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);  
			conn.setRequestProperty("Content-Length:", "220");
			conn.connect();
			if(fileUrl != null && fileUrl.trim().length() != 0){
				//				out = new OutputStreamWriter(conn.getOutputStream());
				out = new DataOutputStream(conn.getOutputStream());
				// 上传文件  
				File file = new File(fileUrl);  
				StringBuilder sb = new StringBuilder();  
				sb.append(boundaryPrefix + boundary);  
				sb.append(newLine);  
				// 文件参数,photo参数名可以随意修改  
				sb.append("Content-Disposition: form-data; name=\"media\";filename=\""+fileName+"."+fileType+"\"; filelength="+fileName.length()+"\r\n"); 
				sb.append("Content-Type:application/octet-stream");  
				// 参数头设置完以后需要两个换行，然后才是参数内容  
				sb.append(newLine+newLine);  
				// 将参数头的数据写入到输出流中  
				out.write(sb.toString().getBytes());  

				// 数据输入流,用于读取文件数据
				try(
					DataInputStream in = new DataInputStream(new FileInputStream(file));
				) {
					byte[] bufferOut = new byte[1024];
					int bytes = 0;
					// 每次读1KB数据,并且将文件数据写入到输出流中
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}

					// 最后添加换行
					out.write(newLine.getBytes());
				}

				// 定义最后数据分隔线，即--加上BOUNDARY再加上--。  
				byte[] end_data = (newLine + boundaryPrefix + boundary + boundaryPrefix + newLine).getBytes();  
				// 写上结尾标识  
				out.write(end_data);  
				out.flush();  
				out.close();
				out.flush();
			}
			// 请求返回的状态
			if (conn.getResponseCode() == 200) {
				//读取响应
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String lines ="";
				while ((lines = reader.readLine()) != null) {
					lines = new String(lines.getBytes(), "utf-8");
					response+=lines;
				}
			}else{
				//				log.info("微信支付下单接口参数请求失败！");
				System.out.println("微信支付下单接口参数请求失败！");
			}

			conn.disconnect();	// 断开连接
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			try {
				if(out!=null){
					out.close();
				}
				if(reader!=null){
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
