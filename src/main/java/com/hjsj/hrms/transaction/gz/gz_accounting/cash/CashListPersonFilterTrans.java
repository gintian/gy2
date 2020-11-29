package com.hjsj.hrms.transaction.gz.gz_accounting.cash;

import com.hjsj.hrms.businessobject.gz.CashListBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class CashListPersonFilterTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String condid=(String)map.get("cond_id");
			String sql =(String)this.getFormHM().get("filterSql");
			String itemid=(String)this.getFormHM().get("itemid");
			//String tableName=(String)this.getFormHM().get("tableName");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String nmoneyid=(String)this.getFormHM().get("nmoneyid");
			String code =(String)this.getFormHM().get("code");
			String before = (String)this.getFormHM().get("beforeSql");
			String _before=PubFunc.decrypt(SafeCode.decode(before));
			String _sql=PubFunc.decrypt(SafeCode.decode(sql));
			CashListBo bo = new CashListBo(this.getFrameconn(),this.getUserView());   //xieguiquan add this.getUserView() 20100828
			bo.setMode("0");
			bo.setSalaryid(salaryid);
			ArrayList itemList = bo.getGzProjectList(salaryid);
		    ArrayList moneyItemList=bo.getSelectedMoneyItemList(nmoneyid);
		    String tableName=this.userView.getUserName()+"_"+"salary"+"_"+salaryid;
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			String order_by = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER, this.userView);
			bo.setOrder_by(order_by);
			if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
				tableName=this.userView.getUserName()+"_salary_"+salaryid;
			else
				tableName=manager+"_salary_"+salaryid;
		   if(manager.trim().length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName()))
				 priv_mode="1";
			String privSql=bo.getPrivSql(this.userView, gzbo);
		    String codeSql = bo.getCodeSql(tableName, code);
			ArrayList cashList =bo.getPersonListBySql(_sql,tableName,itemid,moneyItemList,codeSql,_before,priv_mode,privSql);
			ArrayList filterCondList = bo.getFilterCondList(salaryid);
			this.getFormHM().put("filterList", filterCondList);
			this.getFormHM().put("condid",condid);
			this.getFormHM().put("cashList",cashList);
			this.getFormHM().put("itemList",itemList);
			this.getFormHM().put("itemid",itemid);
			this.getFormHM().put("columnslist",moneyItemList);
			this.getFormHM().put("size",String.valueOf(cashList.size()));
			this.getFormHM().put("nmoneyid",nmoneyid);
			this.getFormHM().put("code",code);
			this.getFormHM().put("tableName",tableName);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("filterSql",sql);
			this.getFormHM().put("beforeSql",before);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
