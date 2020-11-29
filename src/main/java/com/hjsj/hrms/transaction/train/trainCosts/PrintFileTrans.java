package com.hjsj.hrms.transaction.train.trainCosts;

import com.hjsj.hrms.businessobject.hire.ExecuteExcel;
import com.hjsj.hrms.businessobject.hire.ExecutePdf;
import com.hjsj.hrms.businessobject.train.b_plan.PlanTransBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.ArrayList;

public class PrintFileTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String fieldWidths = (String) this.getFormHM().get("fieldWidths");
		String tablename = (String) this.getFormHM().get("tablename");
		String whl_sql = (String) this.userView.getHm().get("train_sql");
		whl_sql=PubFunc.keyWord_reback(whl_sql);
		String flag = (String) this.getFormHM().get("flag");
		String names = this.userView.getUserName();
		this.getFormHM().put("names", names);
		
		String outName = "";
		if ("r25".equalsIgnoreCase(tablename)) { // 培训计划
			outName = getTrainPlan(flag,tablename,fieldWidths,whl_sql);
		}else if("r01,r10,r04,r07,r11,r13".indexOf(tablename)!=-1)
		{
		    ArrayList list=DataDictionary.getFieldList(tablename,Constant.USED_FIELD_SET);
		    if("r13".equalsIgnoreCase(tablename)){
			    for (int i = 0; i < list.size(); i++) {
			    	FieldItem item = (FieldItem) list.get(i);
			    	if("0".equals(item.getState()))
			    		list.remove(i--);
				}
		    }
		    ExecuteExcel executeExcel = new ExecuteExcel(this.getFrameconn(), this.getUserView(), tablename);
		    executeExcel.setParamtervo(null);
		    outName = executeExcel.createTabExcel(list, whl_sql, "2");
		}
		outName = PubFunc.encrypt(outName);
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("outName",outName);
	}

	private String getTrainPlan(String flag, String tablename,
			String fieldWidths, String whl_sql) throws GeneralException {
		String outName = "";
		try {
			String model = (String) this.getFormHM().get("model");
			model = model != null && model.length() > 0 ? model : "0";
			PlanTransBo planbo = new PlanTransBo(this.getFrameconn(), model);
			ArrayList fieldList = planbo.itemPDFList();
			Connection con = this.getFrameconn();
			if ("1".equals(flag)){ // PDF
				ExecutePdf executePdf = new ExecutePdf(con, tablename, this.getUserView());
				outName = executePdf.createTabPdf(this.getUserView().getUserName(), fieldWidths,
						tablename, fieldList, whl_sql, "2");
			}else{ // EXCEL
				ExecuteExcel executeExcel = new ExecuteExcel(con, this.getUserView(), tablename);
				outName = executeExcel.createTabExcel(fieldList, whl_sql, "2");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outName;
	}

}
