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

public class ChangeNumberFieldTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String itemid=(String)this.getFormHM().get("itemid");
			//String tableName=(String)this.getFormHM().get("tableName");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String nmoneyid=(String)this.getFormHM().get("nmoneyid");
			String code =(String)this.getFormHM().get("code");
			String before = (String)this.getFormHM().get("beforeSql");
			String filterSql = (String)this.getFormHM().get("filterSql");
			String _before=PubFunc.decrypt(SafeCode.decode(before));
			String _filterSql=PubFunc.decrypt(SafeCode.decode(filterSql));
			CashListBo bo = new CashListBo(this.getFrameconn(),"0",salaryid);
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
			bo.setUserview(this.userView);
			String privSql=bo.getPrivSql(this.userView, gzbo);
			ArrayList cashList =bo.getCashList(code,tableName,itemid,moneyItemList,_before,_filterSql,privSql,priv_mode);
			this.getFormHM().put("cashList",cashList);
			this.getFormHM().put("itemList",itemList);
			this.getFormHM().put("itemid",itemid);
			this.getFormHM().put("columnslist",moneyItemList);
			this.getFormHM().put("size",String.valueOf(cashList.size()));
			this.getFormHM().put("nmoneyid",nmoneyid);
			this.getFormHM().put("code",code);
			this.getFormHM().put("tableName",tableName);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("filterSql",filterSql);
			this.getFormHM().put("beforeSql",before);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
