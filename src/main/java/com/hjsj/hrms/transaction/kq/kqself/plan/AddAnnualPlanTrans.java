package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
/**
 * 
 * <p>Title:</p>
 * <p>Description:添加部门计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-21:10:36:02</p>
 * @author kf-1
 * @version 1.0
 *
 */
public class AddAnnualPlanTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
            String table="q29";
		 	String year="";
		  	ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);// 字段名
			this.getFormHM().put("table", table);
			ArrayList list=new ArrayList();
			for(int i=0;i< fieldlist.size();i++) 
			{
		      	FieldItem field=(FieldItem)fieldlist.get(i);
		      	field.setValue("");
		      	field.setViewvalue("");
		      	if("q2903".equals(field.getItemid()))
		      	{
		      		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		      		String strDate = sdf.format(new java.util.Date());
		      		year=strDate;
		      		field.setValue(strDate);
		      	}
		      	if("q2909".equals(field.getItemid()))
		      	{
		      		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		      		String strDate = sdf.format(new java.util.Date());
		      		field.setValue(strDate);
		      	}
		      	if("q2911".equals(field.getItemid()))
		      	{
		      		field.setValue(this.userView.getUserFullName());
		      	}
		    	if("q2901".equals(field.getItemid())|| "b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "q29z5".equals(field.getItemid())|| "q2913".equals(field.getItemid())|| "q29z0".equals(field.getItemid())|| "q29z7".equals(field.getItemid()))
		  		    field.setVisible(false);
		  		else
		  		   field.setVisible(true);
		    	FieldItem field_n=(FieldItem)field.cloneItem();
				 list.add(field_n);
		   	}	
			this.getFormHM().put("flist",list);			
			this.getFormHM().put("year",year);
			

	}

}
