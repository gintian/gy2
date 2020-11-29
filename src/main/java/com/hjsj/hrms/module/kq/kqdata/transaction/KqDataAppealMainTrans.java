package com.hjsj.hrms.module.kq.kqdata.transaction;

import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataAppealMainService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqDataAppealMainServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

import java.util.HashMap;
/**
 * 数据上报 交易类
 * create time 2018/11/15
 * @author wangbo
 *
 */
public class KqDataAppealMainTrans extends IBusiness{

	private enum TransType{
		/**数据上报首页*/
		main,
		/**考勤数据上报发布功能*/
		release
//		,
//		/**考勤确认函*/
//		kqconfirmletter
	}
	
	@Override
	public void execute() throws GeneralException {
		
		try {
			HashMap returnStr = new HashMap();
			HashMap returnData = new HashMap();
			returnStr.put("return_code", "success");
			
			String jsonStr = (String)this.formHM.get("jsonStr");
			JSONObject json = JSONObject.fromObject(jsonStr);
			String type = json.getString("type");
			
			String optRole = "";
			if(this.userView.getStatus() == 0)
				optRole = "3";
			else if(this.userView.getStatus() ==4)
				optRole = "4";
			
			if(type.equalsIgnoreCase(TransType.main.toString())){
				String scheme_id = json.getString("scheme_id");
				scheme_id = scheme_id==null||scheme_id.trim().length()==0? "":scheme_id;
				scheme_id = PubFunc.decrypt(scheme_id);
				String kq_year = json.getString("kq_year");
				kq_year = kq_year==null||kq_year.trim().length()==0? "":kq_year;
				String kq_duration = json.getString("kq_duration");
				kq_duration = kq_duration==null||kq_duration.trim().length()==0? "":kq_duration;
				String org_id = json.getString("org_id");
				org_id = org_id==null||org_id.trim().length()==0? "":org_id;
				org_id = PubFunc.decrypt(org_id);
				
				String status = json.getString("status");
				int pageSize = json.getInt("pageSize");
				int currentPage = json.getInt("currentPage");
				KqDataAppealMainService kqDataAppealMainService = new KqDataAppealMainServiceImpl(this.userView, this.frameconn);
				returnData= kqDataAppealMainService.listKqDataAppeal(status,scheme_id, kq_year,kq_duration,org_id, currentPage, pageSize);
				returnData.put("optRole",optRole);
				returnStr.put("return_data", returnData);
			}else if(type.equalsIgnoreCase(TransType.release.toString())){
				String kq_year = json.getString("kq_year");
				String kq_duration = json.getString("kq_duration");
				String scheme_id = json.getString("scheme_id");
				scheme_id = PubFunc.decrypt(scheme_id);
				String org_id = json.getString("org_id");
				org_id = PubFunc.decrypt(org_id);
				KqDataAppealMainService kqDataAppealMainService = new KqDataAppealMainServiceImpl(this.userView, this.frameconn);
	//			kqDataAppealMainService.sendKqConfirmPendingTask(Integer.parseInt(scheme_id), kq_year, kq_duration, org_id);
	//			kqDataAppealMainService.sendKqConfirmLetter(Integer.parseInt(scheme_id), kq_year, kq_duration, org_id);
				kqDataAppealMainService.releaseKqData(Integer.parseInt(scheme_id), kq_year, kq_duration, org_id);
			}
	
	
	//		else if(type.equalsIgnoreCase(TransType.kqconfirmletter.toString())){
	//			String kq_year = json.getString("kq_year");
	//			String kq_duration = json.getString("kq_duration");
	//			String scheme_id = json.getString("scheme_id");
	//			scheme_id = PubFunc.decrypt(scheme_id);
	//			String org_id = json.getString("org_id");
	//			org_id = PubFunc.decrypt(org_id);
	//			KqDataAppealMainService kqDataAppealMainService = new KqDataAppealMainServiceImpl(this.userView, this.frameconn);
	//			kqDataAppealMainService.getKqConfirmLetter(Integer.parseInt(scheme_id), kq_year, kq_duration, org_id);
	//		}
			
			this.formHM.put("returnStr",returnStr);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
