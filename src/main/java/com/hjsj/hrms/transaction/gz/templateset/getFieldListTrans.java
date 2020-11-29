package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryPropertyBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class getFieldListTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			 String hideFlag = "0";//是否隐藏“数据范围” 1:隐藏
			 String royalty_setid=(String)this.getFormHM().get("royalty_setid");
			 String salaryid=(String)this.getFormHM().get("salaryid");
			 SalaryPropertyBo bo=new SalaryPropertyBo(this.getFrameconn()); 
			 ArrayList fieldList=bo.getFieldList(2,royalty_setid,salaryid); 
			 if(fieldList==null || fieldList.size()==0){
				 hideFlag = "1";
			 }
			 this.getFormHM().put("fieldList",fieldList);
			
			 ArrayList dateList=bo.getFieldList(1,royalty_setid,salaryid); 
			 this.getFormHM().put("dateList",dateList);
			 this.getFormHM().put("hideFlag", hideFlag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
	}

}
