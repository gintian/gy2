package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExportDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String sql =PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("sql"))); 
			String salaryid=(String)this.getFormHM().get("salaryid");
			 
			
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			String sp=(String)this.getFormHM().get("sp");
			String flag=(String)this.getFormHM().get("flag");   //1: excel  2.text
			String a_code=(String)this.getFormHM().get("a_code");
			String itemids=((String)this.getFormHM().get("itemids")).toUpperCase()+"/";
			String condid=(String)this.getFormHM().get("condid");
			String filterWhl=SafeCode.decode((String)this.getFormHM().get("filterWhl"));
			filterWhl =PubFunc.decrypt(filterWhl); 
			String order_by=SafeCode.decode((String)this.getFormHM().get("order_by"));
			order_by =PubFunc.decrypt(order_by); 
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
//			String fileName=gzbo.exportFile(salaryid,flag,a_code,itemids,condid); 
			String fileName="";
			if("sp".equals(sp)){
				fileName=gzbo.exportFile3(salaryid,flag,a_code,itemids,condid,filterWhl,order_by,sql); 
			}else{
				fileName=gzbo.exportFile2(salaryid,flag,a_code,itemids,condid,filterWhl,order_by); 
			}
//			if(flag.equals("1"))
//				fileName=fileName.replaceAll(".xls","#");;
			this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(fileName)));
			this.getFormHM().put("flag",flag);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
