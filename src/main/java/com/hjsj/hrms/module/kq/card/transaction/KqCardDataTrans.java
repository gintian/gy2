package com.hjsj.hrms.module.kq.card.transaction;

import com.hjsj.hrms.module.kq.card.businessobject.KqCardDataService;
import com.hjsj.hrms.module.kq.card.businessobject.impl.KqCardDataServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
/**
 * 打卡数据
 * 
 * @Title:        KqCardDataTrans.java
 * @Description:  用于实现打卡数据页面操作的交易类
 * @Company:      hjsj     
 * @Create time:  2019年8月20日 下午4:42:59
 * @author        chenxg
 * @version       7.5
 */
public class KqCardDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		
		try {
			KqCardDataService kqCard = new KqCardDataServiceImpl(this.userView, this.frameconn);
			String param = (String) this.getFormHM().get("param");
			String type = (String) this.getFormHM().get("type");
			if("searchAction".equalsIgnoreCase(type)) {
				JSONObject paramJson = JSONObject.fromObject(param);
				kqCard.searchCardData(paramJson);
			} else if("deleteAction".equalsIgnoreCase(type)) {
				JSONArray paramJson = JSONArray.fromObject(param);
				kqCard.deleteCardData(paramJson);
			} else if("exportAction".equalsIgnoreCase(type)) {
				JSONArray paramJson = JSONArray.fromObject(param);
				String fileName = kqCard.exportCardData(paramJson);
				this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
			} else if("exportTemplateAction".equalsIgnoreCase(type)) {
				String importType = (String) this.getFormHM().get("importType");
				String fileName = kqCard.exportCardTemplate(importType);
				this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
			} else if("importTemplateAction".equalsIgnoreCase(type)) {
				String fileid = (String) this.getFormHM().get("fileid");
				String params = "{fileid:'" + fileid + "'}";
				kqCard.importCardTemplate(JSONObject.fromObject(params));
				String errorMsg = kqCard.getErrorMsg();
				this.getFormHM().put("msgJson", errorMsg);
			} else if("saveCardDataAction".equalsIgnoreCase(type)) {
				int count = kqCard.saveCardData();
				this.getFormHM().put("count", count);
			} else {
				String config = kqCard.getTableConfig();
				this.getFormHM().put("tableConfig", config);
				this.getFormHM().put("fieldArray", kqCard.getFieldsArray());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			String errorMsg = (String) this.userView.getHm().get("errorMsg");
			if(StringUtils.isEmpty(errorMsg))
				errorMsg = "true";
			if(errorMsg.indexOf("Error description:") > -1)
				errorMsg = errorMsg.substring(errorMsg.indexOf("Error description:") + 18);
				
			this.getFormHM().put("errorMessage", errorMsg);
			this.userView.getHm().remove("errorMsg");
		}
	}
}
