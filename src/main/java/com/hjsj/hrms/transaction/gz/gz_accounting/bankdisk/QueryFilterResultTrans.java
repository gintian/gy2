package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryFilterResultTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			  String a_code = (String)this.getFormHM().get("code");
			  String salaryid=(String)this.getFormHM().get("salaryid");
			  String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			  String opt=(String)this.getFormHM().get("opt");
			  String a0100s=(String)this.getFormHM().get("rightFields");
			  BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			  /**代发银行列表*/
			  ArrayList bankList = bo.getBankTemplatesList();
			  String bank_id="";
		      bank_id=(String)this.getFormHM().get("bank_id");
			  /**要显示的列名*/
		      HashMap salarySetMap=bo.getSalarySetFields(salaryid);
			  ArrayList columnsList = bo.getTemplateColumns(bank_id,salarySetMap);
			  /**列名的field_name*/
			 
			  ArrayList column=bo.getColumns(bank_id,salarySetMap);
			 
			 // ArrayList columnsInfo = bo.getFieldInfoFromSalarySet(column,salaryid,2,salarySetMap);
			  //ArrayList bankItemList=bo.getBankItemInfo(bank_id)
			  /**数据列表*/
			  HashMap hm = bo.getFormatMap(column,bank_id);
			  //ArrayList dataList = bo.getFilterResult(tableName,a_code,column,columnsInfo,hm,a0100s);
	          this.getFormHM().put("bank_id",bank_id);
	          this.getFormHM().put("columnsList",columnsList);
			//  this.getFormHM().put("dataList",dataList);
			  this.getFormHM().put("column",column);
			  this.getFormHM().put("bankList",bankList);
			  this.getFormHM().put("bankListSize",String.valueOf(bankList.size()));
			  this.getFormHM().put("columnListSize",String.valueOf(column.size()));
			  this.getFormHM().put("salaryid",salaryid);
			  this.getFormHM().put("code",a_code);
			  this.getFormHM().put("tableName",tableName);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
