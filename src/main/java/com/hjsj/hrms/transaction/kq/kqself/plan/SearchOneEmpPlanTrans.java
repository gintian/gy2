package com.hjsj.hrms.transaction.kq.kqself.plan;

import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SearchOneEmpPlanTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String apply_id =(String)hm.get("apply_id");   
        this.getFormHM().put("apply_id",apply_id);
        String param =(String)hm.get("param");  
        if(param==null||param.length()<=0)
        	param="";
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
		if("view".equals(param))
		{    			
			fieldlist=getViewfieldlist(sql_str.toString(),fieldlist);
		}else
		{
			fieldlist=getupdatefieldlist(sql_str.toString(),fieldlist);
		}		
       this.getFormHM().put("onelist",fieldlist);
        
	}
  public ArrayList getViewfieldlist(String sql_str,ArrayList fieldlist)
  {
	  ContentDAO dao=new ContentDAO(this.getFrameconn());
	  AnnualApply annualApply= new AnnualApply();
	  ArrayList list=new ArrayList();
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
					  }else
					  {
						  if("q31z0".equals(field.getItemid().trim()))
						  {
  							String codeitemid = this.frowset.getString(field.getItemid());
  							String codeitemdesc = AdminCode.getCodeName("30",codeitemid);
  							field.setViewvalue(codeitemdesc);
  							 field.setValue(this.frowset.getString(field.getItemid()));
						  }else if("q31z5".equals(field.getItemid().trim()))
						  {
							  String codeitemid = this.frowset.getString(field.getItemid());
							  String codeitemdesc = AdminCode.getCodeName("23",codeitemid);
							  field.setViewvalue(codeitemdesc);
	  						  field.setValue(this.frowset.getString(field.getItemid()));
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
					  }
				      				      	
				    	if("q2901".equals(field.getItemid())|| "q3101".equals(field.getItemid())|| "nbase".equals(field.getItemid())|| "a0100".equals(field.getItemid())|| "b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "a0101".equals(field.getItemid())|| "e01a1".equals(field.getItemid()))
				  		    field.setVisible(false);
				  		else
				  		    field.setVisible(true);
				    	list.add(field.cloneItem());
				   	} 
			 }
			 
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }       
		 return list;
  }
  public ArrayList getupdatefieldlist(String sql_str,ArrayList fieldlist)
  {
	  ContentDAO dao=new ContentDAO(this.getFrameconn());
	  ArrayList list=new ArrayList();
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
				      				      	
				    	if("q31z0".equals(field.getItemid())|| "q2901".equals(field.getItemid())|| "q3101".equals(field.getItemid())|| "nbase".equals(field.getItemid())|| "a0100".equals(field.getItemid())|| "b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "a0101".equals(field.getItemid())|| "q31z5".equals(field.getItemid())|| "e01a1".equals(field.getItemid())|| "q31z7".equals(field.getItemid()))
				  		    field.setVisible(false);
				  		else
				  		    field.setVisible(true);
				    	list.add(field.cloneItem());
				   	} 
			 }
			 
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }       
		 return list;
  }
}
