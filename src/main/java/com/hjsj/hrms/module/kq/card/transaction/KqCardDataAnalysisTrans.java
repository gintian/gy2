package com.hjsj.hrms.module.kq.card.transaction;

import com.hjsj.hrms.module.kq.card.businessobject.KqCardDataAnalysisService;
import com.hjsj.hrms.module.kq.card.businessobject.impl.KqCardDataAnalysisServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

public class KqCardDataAnalysisTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			KqCardDataAnalysisService dataAnalysis = new KqCardDataAnalysisServiceImpl(this.userView, this.frameconn);
			String param = (String) this.getFormHM().get("param");
			String type = (String) this.getFormHM().get("type");
			if("searchAction".equalsIgnoreCase(type)) {
				JSONObject paramJson = JSONObject.fromObject(param);
				dataAnalysis.searchCardData(paramJson);
			} else if("exportAction".equalsIgnoreCase(type)) {
				JSONArray paramJson = JSONArray.fromObject(param);
				String fileName = dataAnalysis.exportCardData(paramJson);
				this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
//			} else if("dataAnalysAction".equalsIgnoreCase(type)) {
//				JSONObject paramJson = JSONObject.fromObject(param);
//				dataAnalysis.dataAnalys(paramJson);
			} else {
				String config = dataAnalysis.getTableConfig();
				this.getFormHM().put("fieldArray", dataAnalysis.getFieldsArray());
				this.getFormHM().put("tableConfig", config);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			String errorMsg = (String) this.userView.getHm().get("errorMsg");
			if(StringUtils.isEmpty(errorMsg))
				errorMsg = "true";

			this.getFormHM().put("errorMsg", errorMsg);
			this.userView.getHm().remove("errorMsg");
		}
		
	}

}
