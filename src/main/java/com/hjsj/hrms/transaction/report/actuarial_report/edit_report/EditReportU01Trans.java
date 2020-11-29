package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class EditReportU01Trans extends IBusiness {


	public void execute() throws GeneralException {
		String unitcode=(String)this.getFormHM().get("unitcode");
		String id=(String)this.getFormHM().get("id");
		String flag=(String)this.getFormHM().get("flag");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String opt=(String)hm.get("opt");	
		String from_model=(String)hm.get("from_model");
		String sql="select * from U01 where unitcode='"+unitcode+"' and id='"+id+"'";
		ArrayList fieldlist = DataDictionary.getFieldList("U01",Constant.USED_FIELD_SET);
		ArrayList list=new ArrayList();		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				  for(int i=0;i<fieldlist.size();i++) 
				  {
				 		FieldItem field=(FieldItem)fieldlist.get(i); 				 		
				 		FieldItem field_1=(FieldItem)field.cloneItem();
				 		field_1.setValue(this.frowset.getString(field.getItemid()));
				 		if("1".equals(field_1.getState()))
						{
									
				 			field_1.setVisible(true);
						}else
						{
							field_1.setVisible(false);
						}
				 		if(field_1.getExplain().trim().length()>0)
				 			field_1.setItemdesc(field_1.getExplain());
				 		if("U0101".equalsIgnoreCase(field.getItemid())|| "U0103".equalsIgnoreCase(field.getItemid()))
				 		list.add(field_1);
				  }
			}else
			{
				 for(int i=0;i<fieldlist.size();i++) 
				  {
				 		FieldItem field=(FieldItem)fieldlist.get(i); 				 		
				 		FieldItem field_1=(FieldItem)field.cloneItem();
				 		if("1".equals(field_1.getState()))
						{
									
				 			field_1.setVisible(true);
						}else
						{
							field_1.setVisible(false);
						}
				 		if(field_1.getExplain().trim().length()>0)
				 			field_1.setItemdesc(field_1.getExplain());
				 		if("U0101".equalsIgnoreCase(field.getItemid())|| "U0103".equalsIgnoreCase(field.getItemid()))
					    list.add(field_1);
				  }
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String reportStatus="";
	    try {
			this.frowset=dao.search("select flag from tt_calculation_ctrl  where unitcode='"+unitcode+"' and id="+id+" and report_id='U01'");
			if(this.frowset.next())
				reportStatus=this.frowset.getString("flag");
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	    this.getFormHM().put("reportStatus",reportStatus);
		ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
		 this.getFormHM().put("flagSub",ab.isSub("U01", ab.getSelfUnitCode(), id, "1"));
		  if("1".equals(ab.isRootUnit(this.getUserView().getUserName()))){
				this.getFormHM().put("rootUnit", "1");
			}else{
				this.getFormHM().put("rootUnit", "0");
			}
		this.getFormHM().put("selfUnitcode", ab.getSelfUnitCode());
		this.getFormHM().put("cycleStatus", ab.getCycleStatus(id));
		this.getFormHM().put("unitcode",unitcode);
		this.getFormHM().put("from_model",from_model);
		this.getFormHM().put("opt", opt);
    	this.getFormHM().put("fieldlsitU01", list);
    	this.getFormHM().put("isCollectUnit",ab.isCollectUnit(unitcode));
    	this.getFormHM().put("report_id","U01");
	}

}
