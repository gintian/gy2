package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class EditReportU02ListTrans  extends IBusiness {


	public void execute() throws GeneralException {
		String unitcode=(String)this.getFormHM().get("unitcode");
		String id=(String)this.getFormHM().get("id");		
		String Report_id=(String)this.getFormHM().get("report_id");	
		String kmethod =(String)this.getFormHM().get("kmethod");
		EditReport editReport=new EditReport();
    	ArrayList fieldlist=editReport.getU02FieldList(this.getFrameconn(),Report_id,true);
		ArrayList list=new ArrayList();
		String olditemdesc="";
//		if(SystemConfig.getPropertyValue("oldiditem")!=null&&SystemConfig.getPropertyValue("oldiditem").startsWith("U02")){
//			olditemdesc = SystemConfig.getPropertyValue("oldiditem").trim();
//    	}
		for(int i=0;i<fieldlist.size();i++) 
		  {
		 		FieldItem field=(FieldItem)fieldlist.get(i); 
		 		if("1".equals(field.getState()))
				{
							
		 			field.setVisible(true);
				}else
				{
					field.setVisible(false);
				}
		 		if("63".equals(field.getCodesetid()))
		 		{
		 			String codename=AdminCode.getCodeName("63", "1");		 			
		 			field.setValue("1");
		 			field.setViewvalue(codename);
		 			
		 		}else if("62".equals(field.getCodesetid()))
		 		{
		 			if(kmethod!=null&& "0".equals(kmethod))
		 			{
		 				if(editReport.isBeforeCycle(this.getFrameconn(),id))
		 				{
		 					String codename=AdminCode.getCodeName("62", "2");		 			
				 			field.setValue("2");
				 			field.setViewvalue(codename);
		 				}else
		 				{
		 					String codename=AdminCode.getCodeName("62", "1");		 			
				 			field.setValue("1");
				 			field.setViewvalue(codename);
		 				}
		 			}else
		 			{
		 				String codename=AdminCode.getCodeName("62", "3");		 			
			 			field.setValue("3");
			 			field.setViewvalue(codename);
		 			}
		 			//field.setVisible(false);
		 		}
		 		if("U0200".equalsIgnoreCase(field.getItemid()))
		 			continue;
		 		if("U0243".equalsIgnoreCase(field.getItemid())){
		 			continue;
		 		}
//		 		if(field.getItemid().equalsIgnoreCase("U0207")){
//		 			field.setReadonly(true);
//		 		}
//		 		if(field.getItemid().equalsIgnoreCase("U0209")){
//		 			field.setReadonly(true);
//		 		}
		 		list.add(field.clone());
		  }
		this.getFormHM().put("editlistU02", list);
		this.getFormHM().put("report_id", Report_id);
		this.getFormHM().put("unitcode", unitcode);
		this.getFormHM().put("id", id);
//		this.getFormHM().put("olditemdesc", olditemdesc);
	}

}
