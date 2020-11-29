package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * <p>Title: WeiXinTrans </p>
 * <p>Description: 微信参数设置</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2016-1-28 下午1:15:19</p>
 * @author jingq
 * @version 1.0
 */
public class WeiXinTrans extends IBusiness {

	private static final long serialVersionUID = 1L;
	
	private enum TransType{
		/**查询微信参数**/
		searchWeixin,
		/**保存微信参数**/
		saveWeixin,
		/**查询钉钉参数**/
		searchDdtalk,
		/**保存钉钉参数**/
		saveDdtalk,
		/**查询企业邮箱参数**/
		searchMail,
		/**保存企业邮箱参数**/
		saveMail,
		/**测试发送钉钉消息**/
		TestSendDdtalkMsg,
		/**测试发送微信消息**/
		TestSendWeixinMsg,
	}

	@SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		StringReader reader = null;
		boolean flag = true;
		String message = "";
		HashMap<Object, Object> hm = this.getFormHM();
		try {
			String transType = (String) hm.get("transType");
			if(transType!=null){
				if(transType.equals(TransType.searchWeixin.toString())){
					HashMap<String, String> map = new HashMap<String, String>();
					RecordVo recordVo = ConstantParamter.getConstantVo("SS_QQWX");
					// 判断数据库中是否存在
					if (recordVo != null) {
						// 读取xml转换为Document
						Document doc = PubFunc.generateDom(recordVo.getString("str_value"));

						// 读取根节点
						Element root = doc.getRootElement();
						List<?> list = root.getChildren();
						Element child;
						// 循环提取数据输出到前台
						for (int i = 0; i < list.size(); i++) {
							child = (Element) list.get(i);
							map.put(child.getAttributeValue("key"), child.getAttributeValue("value"));
							if("funcsecret".equals(child.getAttributeValue("key"))){
								List<?> childlist = child.getChildren();
								Element menu;
								for (int j = 0; j < childlist.size(); j++) {
									menu = (Element) childlist.get(j);
									map.put("menu_"+menu.getAttributeValue("menuid"), menu.getAttributeValue("secret"));
									map.put(menu.getAttributeValue("menuid"), menu.getAttributeValue("desc"));
								}
							}
						}
					}
					hm.put("param", map);
				} else if(transType.equals(TransType.saveWeixin.toString())){
					MorphDynaBean obj = (MorphDynaBean) hm.get("param");
					String corpid = (String) obj.get("corpid");
					corpid = corpid == null ? "" : corpid.trim();
					String corpsecret = (String) obj.get("corpsecret");
					corpsecret = corpsecret == null ? "" : corpsecret.trim();
					String msgsecret = (String) obj.get("msgsecret");
					msgsecret = msgsecret == null ? "" : msgsecret.trim();
					String url = (String) obj.get("url");
					url = url == null ? "" : url.trim();
					String token = (String) obj.get("token");
					token = token == null ? "" : token.trim();
					String encodingaeskey = (String) obj.get("encodingaeskey");
					encodingaeskey = encodingaeskey == null ? "" : encodingaeskey.trim();
					//后台保存微信支付需要配置的 微信商户号 和 证书key  wangb 20180412
					String mchid = (String) obj.get("mchid");
					mchid = mchid == null ? "" : mchid.trim();
					String mchkey = (String) obj.get("mchkey");
					mchkey = mchkey == null ? "" : mchkey.trim();
					String agentid = (String) obj.get("agentid");
//					agentid = (agentid == null||"".equals(agentid)) ? "0" : agentid.trim();
					agentid = (agentid == null||"".equals(agentid)) ? "" : agentid.trim();//默认显示为空  wangb 20170916  31639
					String w_selfservice_address = (String) obj.get("w_selfservice_address");
//					w_selfservice_address = (w_selfservice_address == null||"".equals(w_selfservice_address)) ? "0" : w_selfservice_address.trim();
					w_selfservice_address = (w_selfservice_address == null||"".equals(w_selfservice_address)) ? "" : w_selfservice_address.trim();//默认显示为空  wangb 20170916  31639
					ArrayList func_secret = (ArrayList) obj.get("func_secret");
					
					// 组装xml
					Element root = new Element("params");
					// 企业号拥有一个唯一的CorpID,企业使用主调模式获取时用来获取获取AccessToken
					Element child = new Element("param");
					child.setAttribute("key", "corpid");
					child.setAttribute("value", corpid);
					child.setAttribute("desc", ResourceFactory.getProperty("system.sms.weixin.corpid.desc"));
					root.addContent(child);
					// 管理组凭证密钥,企业使用主调模式获取时用来获取获取AccessToken
					child = new Element("param");
					child.setAttribute("key", "corpsecret");
					child.setAttribute("value", corpsecret);
					child.setAttribute("desc", ResourceFactory.getProperty("system.sms.weixin.corpsecret.desc"));
					root.addContent(child);
					// 企业应用接收企业号推送请求的访问协议和地址，支持http或https协议,可填写ip地址，其中端口只能为80或443
					child = new Element("param");
					child.setAttribute("key", "url");
					child.setAttribute("value", url);
					child.setAttribute("desc", ResourceFactory.getProperty("system.sms.weixin.url.desc"));
					root.addContent(child);
					// 企业任意填写，用于生成签名，因文或数字，长度为3-32字符
					child = new Element("param");
					child.setAttribute("key", "token");
					child.setAttribute("value", token);
					child.setAttribute("desc", ResourceFactory.getProperty("system.sms.weixin.token.desc"));
					root.addContent(child);
					// 用于消息体的加密，是AES密钥的Base64编码，因文或数字，长度为43字符
					child = new Element("param");
					child.setAttribute("key", "encodingaeskey");
					child.setAttribute("value", encodingaeskey);
					child.setAttribute("desc", ResourceFactory.getProperty("system.sms.weixin.encodingaeskey.desc"));
					root.addContent(child);
					//用于微信支付配置的微信商户号,没有用到微信支付不需要填参数 wangb 20180412
					child = new Element("param");
					child.setAttribute("key","mchid");
					child.setAttribute("value",mchid);
					child.setAttribute("desc",ResourceFactory.getProperty("system.sms.weixin.mchid.desc"));
					root.addContent(child);
					//用于微信支付配置的证书key,没有用到微信支付不需要填参数 wangb 20180412
					child = new Element("param");
					child.setAttribute("key","mchkey");
					child.setAttribute("value",mchkey);
					child.setAttribute("desc",ResourceFactory.getProperty("system.sms.weixin.mchkey.desc"));
					root.addContent(child);
					
					// 企业应用与后台哪些功能对应，用于多后台服务部署或后台不同功能对应企业不同应用
					child = new Element("param");
					child.setAttribute("key", "agentid");
					child.setAttribute("value", agentid);
					child.setAttribute("desc", ResourceFactory.getProperty("system.sms.weixin.agentid.desc"));
					root.addContent(child);
					//发消息应用的secret码
					child = new Element("param");
					child.setAttribute("key", "msgsecret");
					child.setAttribute("value", msgsecret);
					child.setAttribute("desc", ResourceFactory.getProperty("企业小助手secret码，用于发消息使用"));
					root.addContent(child);
					// 微信服务地址
					child = new Element("param");
					child.setAttribute("key", "w_selfservice_address");
					child.setAttribute("value", w_selfservice_address);
					child.setAttribute("desc", ResourceFactory.getProperty("http://ip:端口 desc=微信服务的地址"));
					root.addContent(child);
					//具体功能配置secret码
					if(func_secret.size()>0){
						child = new Element("param");
						child.setAttribute("key", "funcsecret");
						child.setAttribute("desc", ResourceFactory.getProperty("微信功能secretid"));
						Element func=new Element("func");
						for(Object funcEle:func_secret){
							MorphDynaBean mdb=(MorphDynaBean)funcEle;
							HashMap hash=PubFunc.DynaBean2Map(mdb);
							func=new Element("func");
							func.setAttribute("menuid",hash.get("menuid").toString());
							func.setAttribute("secret",hash.get("secret").toString());
							func.setAttribute("desc",hash.get("desc").toString());
							child.addContent(func);
						}
						root.addContent(child);
					}
					// 生成XMLOutputter
					Document myDocument = new Document(root);
					XMLOutputter outputter = new XMLOutputter();
					Format format = Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					
					// 组装RecordVo
					RecordVo para_vo = new RecordVo("constant");
					para_vo.setString("constant", "SS_QQWX");
					para_vo.setString("describe", "微信接口参数");
					para_vo.setString("str_value", outputter.outputString(myDocument));
					
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					// 不存在则增加，存在则更新
					if (ConstantParamter.getConstantVo("SS_QQWX") == null)
						dao.addValueObject(para_vo);
					else
						dao.updateValueObject(para_vo);
					
					// 放入ConstantParamter字典中
					ConstantParamter.putConstantVo(para_vo, "SS_QQWX");
					ConstantParamter.setAttribute("wx_corpid", corpid);
					ConstantParamter.setAttribute("wx_corpsecret", corpsecret);
					ConstantParamter.setAttribute("wx_url", url);
					ConstantParamter.setAttribute("wx_token", token);
					ConstantParamter.setAttribute("wx_encodingaeskey", encodingaeskey);
					ConstantParamter.setAttribute("wx_agentid", agentid);
				} else if(transType.equals(TransType.searchDdtalk.toString())){
					HashMap<String, String> map = new HashMap<String, String>();
					RecordVo recordVo = ConstantParamter.getConstantVo("DINGTALK");
					// 判断数据库中是否存在
					if (recordVo != null) {
						// 读取xml转换为Document
						Document doc = PubFunc.generateDom(recordVo.getString("str_value"));

						// 读取根节点
						Element root = doc.getRootElement();
						List<?> list = root.getChildren();
						Element child;
						// 循环提取数据输出到前台
						for (int i = 0; i < list.size(); i++) {
							child = (Element) list.get(i);
							map.put("ddtalk_"+child.getAttributeValue("key"), child.getAttributeValue("value"));
							//钉钉功能菜单
							if("funcsecret".equals(child.getAttributeValue("key"))){
								List<?> childlist = child.getChildren();
								Element menu;
								for (int j = 0; j < childlist.size(); j++) {
									menu = (Element) childlist.get(j);
									map.put("appKey_"+menu.getAttributeValue("menuid"), menu.getAttributeValue("appKey"));
									map.put("appSecret_"+menu.getAttributeValue("menuid"), menu.getAttributeValue("appSecret"));
									map.put(menu.getAttributeValue("menuid"), menu.getAttributeValue("desc"));
								}
							}
						}
					}
					hm.put("param", map);
				} else if(transType.equals(TransType.saveDdtalk.toString())){
					MorphDynaBean obj = (MorphDynaBean) hm.get("param");
					String corpid = (String) obj.get("ddtalk_corpid");
					corpid = corpid == null ? "" : corpid.trim();
					String corpsecret = (String) obj.get("ddtalk_corpsecret");
					corpsecret = corpsecret == null ? "" : corpsecret.trim();
					String agentid = (String) obj.get("ddtalk_agentid");
					/**
					 * 第三方接口参数配置新增一个userid字段
					 * xus 17/4/19
					 */
					String userid = (String) obj.get("ddtalk_userid");
					userid = userid == null ? "" : userid.trim();
					
					String msg_AppKey = (String) obj.get("msg_AppKey");
					msg_AppKey = msg_AppKey == null ? "" : msg_AppKey.trim();
					String msg_AppSecret = (String) obj.get("msg_AppSecret");
					msg_AppSecret = msg_AppSecret == null ? "" : msg_AppSecret.trim();
					
//					String msgstate = (String) obj.get("ddtalk_msgstate");
//					msgstate = msgstate == null ? "" : msgstate.trim();
					agentid = agentid == null ? "" : agentid.trim();
					ArrayList func_secret = (ArrayList) obj.get("func_secret");
					// 组装xml
					Element root = new Element("params");
					// 钉钉后台管理组拥有一个唯一的CorpID,企业使用主调模式获取时用来获取AccessToken
					Element child = new Element("param");
					child.setAttribute("key", "corpid");
					child.setAttribute("value", corpid);
					root.addContent(child);
					// 管理组凭证密钥,企业使用主调模式获取时用来获取获取AccessToken
					child = new Element("param");
					child.setAttribute("key", "corpsecret");
					child.setAttribute("value", corpsecret);
					root.addContent(child);
					
					// 企业应用与后台哪些功能对应，用于多后台服务部署或后台不同功能对应企业不同应用，例如后台服务以哪个微应用发消息
					child = new Element("param");
					child.setAttribute("key", "agentid");
					child.setAttribute("value", agentid);
					root.addContent(child);
					
					// xus 17/04/14 ddid在系统中对应的主集id字段
					child = new Element("param");
					child.setAttribute("key", "userid");
					child.setAttribute("value", userid);
					root.addContent(child);
					
					// xus 19/01/10  发送消息的功能菜单
					child = new Element("param");
					child.setAttribute("key", "msg_AppKey");
					child.setAttribute("value", msg_AppKey);
					root.addContent(child);
					
					// xus 19/01/10  发送消息的功能菜单
					child = new Element("param");
					child.setAttribute("key", "msg_AppSecret");
					child.setAttribute("value", msg_AppSecret);
					root.addContent(child);
					
//					// xus 19/01/10  发送消息的功能菜单
//					child = new Element("param");
//					child.setAttribute("key", "msgstate");
//					child.setAttribute("value", msgstate);
//					root.addContent(child);
					
					//具体功能配置secret码
					if(func_secret.size()>0){
						child = new Element("param");
						child.setAttribute("key", "funcsecret");
//						child.setAttribute("desc", ResourceFactory.getProperty("钉钉功能secretid"));
						child.setAttribute("desc", "钉钉功能secretid");
						Element func=new Element("func");
						for(Object funcEle:func_secret){
							MorphDynaBean mdb=(MorphDynaBean)funcEle;
							HashMap hash=PubFunc.DynaBean2Map(mdb);
							func=new Element("func");
							func.setAttribute("menuid",hash.get("menuid").toString());
							func.setAttribute("appKey",hash.get("appKey").toString());
							func.setAttribute("appSecret",hash.get("appSecret").toString());
							func.setAttribute("desc",hash.get("desc").toString());
							child.addContent(func);
						}
						root.addContent(child);
					}
					
					// 生成XMLOutputter
					Document myDocument = new Document(root);
					XMLOutputter outputter = new XMLOutputter();
					Format format = Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					
					// 组装RecordVo
					RecordVo para_vo = new RecordVo("constant");
					para_vo.setString("constant", "DINGTALK");
					para_vo.setString("describe", "钉钉接口参数");
					para_vo.setString("str_value", outputter.outputString(myDocument));
					
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					// 不存在则增加，存在则更新
					if (ConstantParamter.getConstantVo("DINGTALK") == null)
						dao.addValueObject(para_vo);
					else
						dao.updateValueObject(para_vo);
					
					// 放入ConstantParamter字典中
					ConstantParamter.putConstantVo(para_vo, "DINGTALK");
					ConstantParamter.setAttribute("DINGTALK_corpid", corpid);
					ConstantParamter.setAttribute("DINGTALK_corpsecret", corpsecret);
					ConstantParamter.setAttribute("DINGTALK_agentid", agentid);
					ConstantParamter.setAttribute("DINGTALK_userid", userid);
					ConstantParamter.setAttribute("DINGTALK_msg_AppKey", msg_AppKey);
					ConstantParamter.setAttribute("DINGTALK_msg_AppSecret", msg_AppSecret);
//					ConstantParamter.setAttribute("DINGTALK_msgstate", msgstate);
				}else if(transType.equals(TransType.searchMail.toString())){
					HashMap<String, String> map = new HashMap<String, String>();
					RecordVo vo = ConstantParamter.getConstantVo("SS_ENTERPRISEMAIL");
					if(vo!=null){
						Document doc = PubFunc.generateDom(vo.getString("str_value"));
						Element root = doc.getRootElement();
						List<?> list = root.getChildren();
						Element child;
						for (int i = 0; i < list.size(); i++) {
							child = (Element) list.get(i);
							map.put(child.getAttributeValue("key"), child.getAttributeValue("value"));
						}
					}
					hm.put("param", map);
				} else if(transType.equals(TransType.saveMail.toString())){
					MorphDynaBean obj = (MorphDynaBean) hm.get("param");
					String clientid = (String) obj.get("clientid");
					String clientsecret = (String) obj.get("clientsecret");
					clientid = clientid == null ? "" : clientid.trim();
					clientsecret = clientsecret == null ? "" : clientsecret.trim();
					Element root = new Element("params");
					//clientid
					Element child = new Element("param");
					child.setAttribute("key", "clientid");
					child.setAttribute("value", clientid);
					child.setAttribute("desc", ResourceFactory.getProperty("system.sms.enterprisemail.clientid.desc"));
					root.addContent(child);
					//clientsecret
					child = new Element("param");
					child.setAttribute("key", "clientsecret");
					child.setAttribute("value", clientsecret);
					child.setAttribute("desc", ResourceFactory.getProperty("system.sms.enterprisemail.clientsecret.desc"));
					root.addContent(child);
					
					Document doc = new Document(root);
					XMLOutputter output = new XMLOutputter();
					Format format = Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					output.setFormat(format);
					
					RecordVo vo = new RecordVo("constant");
					vo.setString("constant", "SS_ENTERPRISEMAIL");
					vo.setString("describe", ResourceFactory.getProperty("system.sms.enterprisemail.options"));
					vo.setString("str_value", output.outputString(doc));
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					if(ConstantParamter.getConstantVo("SS_ENTERPRISEMAIL")==null){
						dao.addValueObject(vo);
					} else {
						dao.updateValueObject(vo);
					}
					ConstantParamter.putConstantVo(vo, "SS_ENTERPRISEMAIL");
				} else if("useridlist".equals(transType)){
					/**
					 * xus 17/04/14
					 * 钉钉参数userid下拉选项查询
					 */
					String sql = " select itemid itemid,itemdesc itemdesc from fielditem where fieldsetid = 'A01' and itemtype = 'A' and useflag = '1' and codesetid = '0'";
					JSONArray useridList = new JSONArray();
					JSONObject useridListObj = null;
					
					useridListObj = new JSONObject();
					useridListObj.put("itemid","username");
					useridListObj.put("itemdesc","username(默认)");
					useridList.add(useridListObj);
					
					ContentDAO  dao = new ContentDAO(this.frameconn);
					this.frowset = dao.search(sql);
					while(this.frowset.next()){//判断是否还有下一行  
						useridListObj=new JSONObject();
						useridListObj.put("itemid", this.frowset.getString("itemid"));
						useridListObj.put("itemdesc", this.frowset.getString("itemdesc"));
						useridList.add(useridListObj);
			        }   
					
					hm.put("useridList", useridList);
				}else if(transType.equals(TransType.TestSendDdtalkMsg.toString())){
					String userid = (String) hm.get("userid");
					String agentid = (String) hm.get("agentid");
					String appKey = (String) hm.get("appKey");
					String appSecret = (String) hm.get("appSecret");
					String text = "测试发送消息：\n 消息内容：测试"+Math.random()*10000;
					//xus 测试发送钉钉消息
					HashMap paramMap = new HashMap();
					paramMap.put("type", "text");
					paramMap.put("text", text);
					paramMap.put("agentid", agentid);
					paramMap.put("appKey", appKey);
					paramMap.put("appSecret", appSecret);
					String errorMsg = DTalkBo.sendMessageNew(userid,paramMap);
//					String errorMsg = DTalkBo.sendMessageNew("manager6436",paramMap);
					hm.put("errorMsg", errorMsg);
				}else if(transType.equals(TransType.TestSendWeixinMsg.toString())){
					String userid = (String) hm.get("userid");
					String text = "测试发送消息：\n 消息内容：测试"+Math.random()*10000;
					boolean sendFlag = WeiXinBo.sendMsgToPerson(userid, "测试发送消息", text, "", "");
					hm.put("sendFlag", sendFlag);
				}
			}
		} catch (Exception e) {
			flag = false;
			message = e.getMessage();
			hm.put("message", message);
			e.printStackTrace();
		} finally {
			hm.put("flag", flag);
			PubFunc.closeIoResource(reader);
		}
	}

}
