package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *
 * @Titile: SaveWXEnterpriseParamTrans
 * @Description:保存企业号参数配置
 * @Company:hjsj
 * @Create time: 2018年6月27日下午3:14:45
 * @author: wangbs
 * @version 1.0
 *
 */
public class SaveWXEnterpriseParamTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		boolean flag = true;
		String message = "";
		try {
			MorphDynaBean paramInfoBean = (MorphDynaBean) this.formHM.get("paramInfo");
			HashMap paramInfo = PubFunc.DynaBean2Map(paramInfoBean);
			String corpid = (String) paramInfo.get("corpid");//企业号corpid
			corpid = StringUtils.trimToEmpty(corpid);
			String corpsecret = (String) paramInfo.get("corpsecret");//通讯录Secret
			corpsecret = StringUtils.trimToEmpty(corpsecret);
			String agentid = (String) paramInfo.get("agentid");//企业小助手AgentID
			agentid = StringUtils.trimToEmpty(agentid);//默认显示为空 
			String msgsecret = (String) paramInfo.get("msgsecret");//企业小助手Secret
			msgsecret = StringUtils.trimToEmpty(msgsecret);
			String token = (String) paramInfo.get("token");//用于生成签名
			token = StringUtils.trimToEmpty(token);
			String encodingaeskey = (String) paramInfo.get("encodingaeskey");//消息加密
			encodingaeskey = StringUtils.trimToEmpty(encodingaeskey);
			String w_selfservice_address = (String) paramInfo.get("w_selfservice_address");//微信服务地址
			w_selfservice_address = StringUtils.trimToEmpty(w_selfservice_address);//默认显示为空
			ArrayList func_secret = (ArrayList) paramInfo.get("func_secret");
			
			//后台保存微信支付需要配置的 微信商户号 和 证书key
			String mchid = (String) paramInfo.get("mchid");//微信商户号
			mchid = StringUtils.trimToEmpty(mchid);
			String mchkey = (String) paramInfo.get("mchkey");//证书key
			mchkey = StringUtils.trimToEmpty(mchkey);
			String url = (String) paramInfo.get("url");
			url = StringUtils.trimToEmpty(url);
			
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
			//用于微信支付配置的微信商户号,没有用到微信支付不需要填参数
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
			if (ConstantParamter.getConstantVo("SS_QQWX") == null) {
				dao.addValueObject(para_vo);
			}else {
				dao.updateValueObject(para_vo);
			}
			
			// 放入ConstantParamter字典中
			ConstantParamter.putConstantVo(para_vo, "SS_QQWX");
			ConstantParamter.setAttribute("wx_corpid", corpid);
			ConstantParamter.setAttribute("wx_corpsecret", corpsecret);
			ConstantParamter.setAttribute("wx_url", url);
			ConstantParamter.setAttribute("wx_token", token);
			ConstantParamter.setAttribute("wx_encodingaeskey", encodingaeskey);
			ConstantParamter.setAttribute("wx_agentid", agentid);
		} catch (Exception e) {
			flag = false;
			message = e.getMessage();
			this.formHM.put("message", message);
			e.printStackTrace();
		} finally {
			this.formHM.put("result", flag);
		}
	}
}
