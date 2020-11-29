package com.hjsj.hrms.transaction.kq.kqself.apply;

import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchAnnualApplyTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	 	String table = (String) hm.get("table");
	 	String plan_id=(String)hm.get("plan_id");
	 	String plan_name = "";
	  	ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);
		this.getFormHM().put("table", table);
		ArrayList list=new ArrayList();
		for(int i=0;i< fieldlist.size();i++) 
		{
	      	FieldItem field=(FieldItem)fieldlist.get(i);
	      	field.setValue("");
	      	field.setViewvalue("");
	      	if("q2903".equals(field.getItemid()))
	      	{
	      		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	      		String strDate = sdf.format(new java.util.Date());*/
	      		field.setValue(getYears(plan_id));
	      		
	      	}
	      	if("q3105".equals(field.getItemid()))
	      	{
	      		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	      		String strDate = sdf.format(new java.util.Date());
	      		field.setValue(strDate);
	      	}
	    	if("q31z0".equals(field.getItemid())|| "q2901".equals(field.getItemid())|| "q3101".equals(field.getItemid())|| "nbase".equals(field.getItemid())|| "a0100".equals(field.getItemid())|| "b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "a0101".equals(field.getItemid())|| "q31z5".equals(field.getItemid())|| "q31z7".equals(field.getItemid())|| "e01a1".equals(field.getItemid()))
	  		    field.setVisible(false);
	  		else
	  		    field.setVisible(true);
	    	FieldItem field_n=(FieldItem)field.cloneItem();
			list.add(field_n); 
			
	   	}	
		AnnualApply annualApply= new AnnualApply(this.userView,this.getFrameconn());
		ArrayList planList = new ArrayList();
		planList = annualApply.getPlanList(this.userView,this.getFrameconn());
		this.getFormHM().put("flist",list);	
		this.getFormHM().put("plist",planList);
		this.getFormHM().put("plan_id",plan_id);
		for (int i = 0; i < planList.size(); i++) 
		{
			CommonData vo = new CommonData();
			vo = (CommonData) planList.get(i);
			if (vo.getDataValue().equals(plan_id))
			{
				plan_name = vo.getDataName();
				
			}
		}
		this.getFormHM().put("plan_name", plan_name);
	}   
	private String getYears(String plan_id)
	{
		String sql="select q2903 from q29 where q2901='"+plan_id+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
  		String strDate = sdf.format(new java.util.Date());  		
		try
		{
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				strDate=this.frowset.getString("q2903");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return strDate;
	}
}