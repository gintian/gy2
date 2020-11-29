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
/**
 * <p>Title:InitCashListTrans.java</p>
 * <p>Description:薪资发放/分钱清单初始化方法</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007.09.04 9:32:00 am</p>
 * @author lizhenwei
 * @version 4.0
 *
 */

public class InitCashListTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
		try
		{
			
			String before = (String)map.get("before");
			String code =(String)map.get("a_code");
			String salaryid=(String)map.get("salaryid");
			String opt = (String)map.get("opt");
			String filterSql="";
			String condid=(String)map.get("condid");
			if(opt==null)
			{
				filterSql=(String)this.getFormHM().get("filterSql");
				condid=(String)this.getFormHM().get("condid");
			}
			before=PubFunc.decrypt(SafeCode.decode(before));
			filterSql=PubFunc.decrypt(SafeCode.decode(filterSql));
			CashListBo bo = new CashListBo(this.getFrameconn(),this.getUserView()); //xieguiquan add this.getUserView() 20100828
			bo.setMode("0");
			bo.setSalaryid(salaryid);
			/**当前薪资类别中数值型指标列表*/
			ArrayList itemList = bo.getGzProjectList(salaryid);
		    String itemid=(String)this.getFormHM().get("itemid");
		    ArrayList filterCondList = bo.getFilterCondList(salaryid);
		    if(opt!=null||itemid==null)
		    	itemid=bo.getMaxItemid(salaryid);
		    String nmoneyid=String.valueOf(bo.getNstyleidBySalaryid(salaryid));
		   ArrayList moneyItemList=bo.getSelectedMoneyItemList(nmoneyid);
		   String tableName=this.userView.getUserName()+"_"+"salary"+"_"+salaryid;
		   SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		   String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		   String order_by=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView);
		   bo.setOrder_by(order_by);
		   String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
		   if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
			   tableName=this.userView.getUserName()+"_salary_"+salaryid;
			else
				tableName=manager+"_salary_"+salaryid;
		   if(manager.trim().length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName()))
			   priv_mode="1";
		    String privSql=bo.getPrivSql(this.userView, gzbo);
		    bo.setUserview(this.userView);
			ArrayList cashList =bo.getCashList(code,tableName,itemid,moneyItemList,before,filterSql,privSql,priv_mode);
		//	ArrayList list = bo.getPersonListBySql(filterSql, tableName, itemid, moneyItemList, codeSql, before);
			this.getFormHM().put("cashList",cashList);
			this.getFormHM().put("itemList",itemList);
			this.getFormHM().put("itemid",itemid);
			this.getFormHM().put("columnslist",moneyItemList);
			this.getFormHM().put("size",String.valueOf(cashList.size()));
			this.getFormHM().put("nmoneyid",nmoneyid);
			this.getFormHM().put("code",code);
			this.getFormHM().put("tableName",tableName);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("filterSql",SafeCode.encode(PubFunc.encrypt(filterSql)));
			this.getFormHM().put("beforeSql",SafeCode.encode(PubFunc.encrypt(before)));
			this.getFormHM().put("filterList", filterCondList);
			this.getFormHM().put("condid",condid);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			map.remove("opt");
		}
		
	}
}
