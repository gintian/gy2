package com.hjsj.hrms.module.kq.card.transaction;

import com.hjsj.hrms.module.kq.card.businessobject.impl.KqCardDataAnalysisServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
/**
 * 打卡数据数据分析
 * @Title:        KqCardDataAnalysisStatisticsTrans.java
 * @Description:  用于分析打卡数据时调用存储过程的交易类
 * @Company:      hjsj     
 * @Create time:  2019年11月6日 上午11:55:35
 * @author        chenxg
 * @version       7.5
 */
public class KqCardDataAnalysisStatisticsTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
		    KqCardDataAnalysisServiceImpl dataAnalysis = new KqCardDataAnalysisServiceImpl(this.userView, this.frameconn);
			String param = (String) this.getFormHM().get("param");
			JSONObject paramJson = JSONObject.fromObject(param);
            dataAnalysis.setDateParam(paramJson);
            new Thread(dataAnalysis).start();
			
			
//			dataAnalysis.dataAnalys(paramJson);
		} catch (Exception e) {
			e.printStackTrace();
			this.userView.getHm().put("errorMsg", e.toString());
		}
	}
}
