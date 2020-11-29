package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ViewOneEmpPlanTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String apply_id =(String)hm.get("apply_id");       
        ArrayList fieldlist = DataDictionary.getFieldList("q31",Constant.USED_FIELD_SET);
        StringBuffer sql_str = new StringBuffer();		
		String columns = "";
		
		sql_str.append("select ");
		for (int i = 0; i < fieldlist.size(); i++)
		{
			  FieldItem field = (FieldItem) fieldlist.get(i);
			  columns=columns+field.getItemid().toString()+",";
			  sql_str.append(field.getItemid());
			  if (i != fieldlist.size()- 1)
			     sql_str.append(",");
		}
		sql_str.append(" from ");
		sql_str.append("q31");			
		sql_str.append(" where q3101='"+apply_id+"'");	
		ArrayList list=new ArrayList();
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
		 {
			 this.frowset=dao.search(sql_str.toString());			 
			 if(this.frowset.next())
			 {
				 for(int i=0;i< fieldlist.size();i++) 
					{
					  FieldItem field=(FieldItem)fieldlist.get(i); 
					  if("M".equals(field.getItemtype()))
					  {
						  field.setValue(Sql_switcher.readMemo(this.frowset,field.getItemid()));
					  }else if("D".equals(field.getItemtype()))
  					  {
  						  Date date=this.frowset.getDate(field.getItemid());
  						  if(date!=null)
  						  {
  							field.setValue(DateUtils.format(date,"yyyy-MM-dd"));
  						  }	  						
  					  }else
					  {
						  field.setValue(this.frowset.getString(field.getItemid()));
					  }
				      				      	
				    	if("q2901".equals(field.getItemid())|| "q3101".equals(field.getItemid())|| "nbase".equals(field.getItemid())|| "a0100".equals(field.getItemid())|| "b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "e01a1".equals(field.getItemid())|| "q31z5".equals(field.getItemid()))
				  		    field.setVisible(false);
				  		else
				  		    field.setVisible(true);
				    	list.add(field.clone());	
				   	} 
			 }
			 
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }       
       this.getFormHM().put("onelist",list);
        
	}


}
