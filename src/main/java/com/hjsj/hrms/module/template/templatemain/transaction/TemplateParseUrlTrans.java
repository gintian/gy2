package com.hjsj.hrms.module.template.templatemain.transaction;

import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>TemplateParseUrlTrans</p>
 * <p>Description>:解析加密的url</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-09-08 上午10:36:32</p>
 * <p>@version: 7.0</p>
 */
public class TemplateParseUrlTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			HashMap formMap= this.getFormHM();			
			String url = TemplateFuncBo.getValueFromMap(formMap,"url");//获取url参数
			String ismobilebrowser = TemplateFuncBo.getValueFromMap(formMap,"ismobilebrowser");//是否是手机端
			//如果有url，则从url中解析模板参数，覆盖默认参数。
			if (url!=null){
				HashMap map=null;
				int encryStart=url.indexOf("encryptParam");
				if (encryStart>-1){//从导航菜单过来的 参数都加密了
					map =TemplateFuncBo.getAllParamFromEncrytUrl(url);
				}
				else {//程序拼链接过来的，无加密参数。
					map =TemplateFuncBo.getAllParamFromUrl(url);
				}
				
				Iterator iter = map.entrySet().iterator();
				//20200426 wangrd  如果是手机端访问，则转换为移动端链接				
				if ("true".equals(ismobilebrowser)) {
					String w_selfservice_url=SystemConfig.getPropertyValue("w_selfservice_url");
					String weiXinUrl =w_selfservice_url;
					if (!"".equals(w_selfservice_url)) {
						weiXinUrl = weiXinUrl  + "/w_selfservice/module/selfservice/index.jsp?";
						Pattern pattern = Pattern.compile("[0-9]+"); 
						String tabId = "";
						String taskId = "";
						String insId = "";
						String etoken = "";
						String sp_flag = "";
						
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							String name = (String) entry.getKey();
							String value = (String) entry.getValue();
							if ("tab_id".equalsIgnoreCase(name)) {
								tabId = value;
							} else if ("ins_id".equalsIgnoreCase(name)) {
								insId = value;
							} else if ("task_id".equalsIgnoreCase(name)) {
								// 微信代办通知打不开。 原因：参数改变，taskid变成task_id,加密改成不加密了
								if (!pattern.matcher(value).matches())
									taskId = PubFunc.decryption(value);
								else
									taskId = value;
							} else if ("taskid".equalsIgnoreCase(name)) {
								if (!pattern.matcher(value).matches())
									taskId = PubFunc.decryption(value);
								else
									taskId = value;
							} else if ("etoken".equalsIgnoreCase(name)) {
								etoken = value;
							} else if ("sp_flag".equalsIgnoreCase(name)) {
								sp_flag = value;
							}
						}
						String idEdit = "0";
						if ("1".equals(sp_flag)) {
							idEdit = "1";
						}
						if ("".equals(etoken)) {
							String username = this.userView.getUserName();
							String password = this.userView.getPassWord();
							etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
						}
						weiXinUrl= weiXinUrl +"menuid=2&tabid=" + tabId + "&isedit=" + idEdit
								+ "&taskid=" + taskId 
								+ "&insid=" + insId
								+ "&etoken=" + etoken		
								+ "&frommessage=0&objectid=";
					}
					this.getFormHM().put("weiXinUrl", weiXinUrl);
				}else {
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = (String) entry.getKey();
						String val = (String) entry.getValue();
						this.getFormHM().put(key, val);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}


}
