package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.weixin.utils.CommonUtil;
import com.hjsj.weixin.utils.Token;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @Titile: WxServiceMessage
 * @Description: 微信服务号发送模板消息功能类
 * @Company: sdhjsj
 * @Create time: 2018年11月30日上午11:59:22
 * @author: liujx
 * @version 1.0
 *
 */
public class WxServiceMessage {

	/** 服务号Id **/
	private String serviceId;
	/** 消息模板Id **/
	private String msgTmpId;
	/** 消息模板内容 **/
	private String msgContent;
	/** 消息模板接口 **/
	private static String requestUrl;
	/** 接口调用凭证 **/
	private static String accessToken;
	/** 系统能否推送消息 **/
	private boolean canSysSendMessage;
	/** 模板参数对应 **/
	private HashMap<String, String> templateMap;
	
	private Connection conn = null;
	
	public String getMsgTmpId() {
		return msgTmpId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	} 
	public void setMsgTmpId(String msgTmpId) {
		this.msgTmpId = msgTmpId;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	public WxServiceMessage(Connection conn) {
		this.conn = conn;
		getMsgParam(conn);
	}
	private void getMsgParam(Connection conn) {
		ContentDAO dao = new ContentDAO(conn);
		RowSet rowset = null;
		try {
			String menuxml = null;
			String wxitemid = null;
			List<String> paramList = new ArrayList<String>();
			paramList.add("service");
			rowset = dao.search("select wxitemid,str_value from t_sys_weixin_param where wxsetid=? and wxitemid=1", paramList);
			if(rowset.next()) {
				wxitemid = rowset.getString("wxitemid");
				menuxml = rowset.getString("str_value");
			}
			if(wxitemid == null || menuxml == null) {
                return;
            }
			Document doc = null;
			doc = PubFunc.generateDom(menuxml);
			XPath xpath = XPath.newInstance("/param/noticeTemplate");
			List<Element> paramslist = xpath.selectNodes(doc);
			if(paramslist == null || paramslist.size() < 1) {
                return;
            }
			
			Element element = (Element) paramslist.get(0);
			this.serviceId = wxitemid;
			this.msgTmpId = element.getAttributeValue("templateId");
			this.msgContent = element.getAttributeValue("templateContent");
			
			HashMap<String, String> templateMap = new HashMap<String, String>();
			List<Attribute> attributes = element.getAttributes();
			//根据节点属性拼装模板对应关系
			for(int i = 0; i < attributes.size(); i++) {
				Attribute attr = (Attribute) attributes.get(i);
				String attrName = attr.getName();
				if("templateId".equals(attrName) || "templateContent".equals(attrName)) {
                    continue;
                }
				String attrValue = attr.getValue();
				templateMap.put(attrName, attrValue);
			}
			this.templateMap = templateMap;
			
			this.canSysSendMessage = canSysSendMessage();
			WxServiceMessage.accessToken = this.getWXToken(conn);
			WxServiceMessage.requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + WxServiceMessage.accessToken;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowset);
		}
	}
	/**
	 * 系统是否能够发送消息
	 * @return
	 */
	public boolean canSysSendMessage() {
		boolean sendflag = false;
		if(StringUtils.isNotBlank(this.serviceId) && StringUtils.isNotBlank(this.msgTmpId) 
				&& StringUtils.isNotBlank(this.msgContent) && this.templateMap != null) {
            sendflag = true;
        }
		return sendflag;
	}
	/**
	 * 拼装要发送信息的集合
	 * @param list
	 * @return 拼接好的数据集合，包含openid
	 */
	private List<HashMap<String, String>> packageSendList(List<HashMap<String, String>> list) {
		
		List<String> guidkeyList = new ArrayList<String>();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowset = null;
		for(HashMap<String, String> infoMap : list) {
			guidkeyList.add(infoMap.get("guidkey"));
		}
		try {
			StringBuffer sqlStr = new StringBuffer();
			sqlStr.append("select guidkey,openid from zp_wx_related_person where guidkey in(");
			List<String> dataList = new ArrayList<String>();
			for(int i = 0; i < guidkeyList.size(); i++) {
				sqlStr.append("?,");
				dataList.add(guidkeyList.get(i));
			}
			
			sqlStr.setLength(sqlStr.length()-1);
			
			sqlStr.append(")");
			rowset = dao.search(sqlStr.toString(),dataList);
			
			HashMap<String, String> openInfoMap = new HashMap<String, String>();
			while(rowset.next()) {
				String openId = rowset.getString("openid");
				String guidkey = rowset.getString("guidkey");
				openInfoMap.put(guidkey, openId);
			}
			
			for(HashMap<String, String> infoMap : list) {
				Iterator<Entry<String, String>> dataIte = infoMap.entrySet().iterator();
				String guidKey = "";
				while(dataIte.hasNext()) {
					Entry<String, String> dataEntry = dataIte.next();
					String key = dataEntry.getKey();
					if("guidkey".equals(key)) {
						guidKey = dataEntry.getValue();
						break;
					}
				}
				String openId = openInfoMap.get(guidKey);
				if(StringUtils.isNotBlank(openId)) {
					infoMap.put("openId", openId);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowset);
		}
		
		return list;
	}
	/**
	 * 循环推送模板消息
	 * @param list 多个人员的数据集合
	 * @return
	 * @throws GeneralException
	 */
	public boolean send(List<HashMap<String, String>> list) {
		boolean sendFlag = true;
		List<HashMap<String, String>> sendList = packageSendList(list);
		try {
			boolean canSysSend = canSysSendMessage();
			if(!canSysSend) {
				throw GeneralExceptionHandler.Handle(new Exception("系统缺少必要条件，无法推送消息！"));
			}
			for(int i = 0; i < sendList.size(); i++) {
				HashMap<String, String> tempMap = sendList.get(i);
				
				sendFlag = send(tempMap);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		return sendFlag;
	}
	/**
	 * 推送单条模板消息
	 * @param tempMap
	 * @return
	 */
	public boolean send(HashMap<String, String> tempMap) {
		boolean sendFlag = true;
		if(!canSysSendMessage) {
			return false;
		}
		if(!tempMap.containsKey("openid")) {
			List<HashMap<String, String>> tempList = new ArrayList<HashMap<String, String>>();
			tempList.add(tempMap);
			List<HashMap<String, String>> sendList = packageSendList(tempList);
			tempMap = sendList.get(0);
		}
		Map<String, Object> infoMap = new HashMap<String, Object>();
		String openId = tempMap.get("openId");
		infoMap.put("touser", openId);
		infoMap.put("template_id", this.msgTmpId);
		infoMap.put("data", getSendData(tempMap));
		String sendData = JSONObject.fromObject(infoMap).toString();
		JSONObject jsonObj = null;
		
		if(StringUtils.isNotBlank(openId)) {
			jsonObj = CommonUtil.httpsRequest(requestUrl, "POST", sendData);
		}
		/** 
		 * {"errcode":0, "errmsg":"ok", "msgid":200228332} // 成功
		 */
		if(jsonObj == null || (Integer) jsonObj.get("errcode") != 0 || !"ok".equals(jsonObj.get("errmsg"))) {
			sendFlag = false;
		}
		
		return sendFlag;
	}
	/**
	 * 拼接data数据格式,色值固定
	 */
	private Map<String, Map<String, String>> getSendData(Map<String, String> infoMap) {
		Map<String, Map<String, String>> sendDataMap = new HashMap<String, Map<String, String>>();
		Map<String, String> tempMap = this.templateMap;
	 
		Iterator<Entry<String,String>> templaIte = tempMap.entrySet().iterator();
		Map<String, String> dataMap = null;
		while(templaIte.hasNext()) {
			dataMap = new HashMap<String, String>();
			Entry<String,String> entry = templaIte.next();
			String tempKey = entry.getKey();
			String tempValue = entry.getValue();
			
			String dataValue = infoMap.get(tempValue);
			dataMap.put("value", dataValue);
			dataMap.put("color", "#173177");
			sendDataMap.put(tempKey, dataMap);
		}
		
		return sendDataMap;
	}
	/**
	 * 获取access_token接口凭证
	 */
	private String getWXToken(Connection conn) {
		String access_token = null;
		ContentDAO dao = new ContentDAO(conn);
		RowSet rowset = null;
		BufferedReader reader = null;
		try {
			List<String> paramList = new ArrayList<String>();
			paramList.add("service");
			String appID = "";
			String appSecret = "";
			rowset = dao.search("select wxitemid,appid,app_secret from t_sys_weixin_param where wxsetid=? and wxitemid=1", paramList);
			if(rowset.next()) {
				appID = rowset.getString("appid");
				appSecret = rowset.getString("app_secret");
			}
			
			if(StringUtils.isBlank(appID) || StringUtils.isBlank(appSecret)) {
				throw GeneralExceptionHandler.Handle(new Exception("用户唯一凭证或凭证密钥缺失！"));
			}
			Token token = CommonUtil.getToken(appID, appSecret); //获取Token信息
			if(token == null) {
				throw GeneralExceptionHandler.Handle(new Exception("Token信息获取失败！"));
			}
			access_token = token.getAccessToken();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(reader);
			PubFunc.closeDbObj(rowset);
		}
		return access_token;
	}
}
