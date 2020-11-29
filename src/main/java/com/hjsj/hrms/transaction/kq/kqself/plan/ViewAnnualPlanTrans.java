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
/**
 * 
 * <p>Title:</p>
 * <p>Description:查看一个部门计划的详细信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-22:11:05:38</p>
 * @author kf-1
 * @version 1.0
 *
 */
public class ViewAnnualPlanTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
        String table="q29";
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String plan_id =(String)hm.get("plan_id");  
        String param =(String)hm.get("param");  
        ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);// 字段名
        StringBuffer sql_str = new StringBuffer();		
		String columns = "";
		ArrayList list=new ArrayList();
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
		sql_str.append(table);			
		sql_str.append(" where q2901='"+plan_id+"'");	
		if("view".equals(param))
		{    			
			list=getViewfieldlist(sql_str.toString(),fieldlist);
		}else
		{
			list=getupdatefieldlist(sql_str.toString(),fieldlist);
		}
    		
   		this.getFormHM().put("onelist",list);   
   		
	} 
	
    private ArrayList getupdatefieldlist(String sql_str,ArrayList fieldlist)
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());    		
  		 try
  		 {
  			 this.frowset=dao.search(sql_str);
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
  				      				      	
  					if("q2901".equals(field.getItemid())|| "b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "q29z5".equals(field.getItemid())|| "q2913".equals(field.getItemid())|| "q29z0".equals(field.getItemid())|| "q29z7".equals(field.getItemid()))
  				  		    field.setVisible(false);
  				  		else
  				  		    field.setVisible(true);
  						
  				   	} 
  			 }
  			 
  		 }catch(Exception e)
  		 {
  			 e.printStackTrace();
  		 } 
  		 return fieldlist;
    }
    
    private ArrayList getViewfieldlist(String sql_str,ArrayList fieldlist)
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn()); 
    	AnnualApply annualApply= new AnnualApply();
    	ArrayList list=new ArrayList();
  		 try
  		 {
  			 this.frowset=dao.search(sql_str);
  			 if(this.frowset.next())
  			 {
  				 for(int i=0;i< fieldlist.size();i++) 
  					{
  					  FieldItem field=(FieldItem)fieldlist.get(i); 
  					  if("M".equals(field.getItemtype()))
  					  {
  						  //System.out.println(field.getItemid());
  						  //System.out.println(Sql_switcher.readMemo(this.frowset,field.getItemid()));
  						  field.setValue(Sql_switcher.readMemo(this.frowset,field.getItemid()));
  					  }else
  					  {
  						  if("q29z0".equals(field.getItemid().trim()))
						  {
  							String codeitemid=this.frowset.getString(field.getItemid());
  							String codeitemdesc = AdminCode.getCodeName("30",codeitemid);
  							field.setViewvalue(codeitemdesc);
  							 field.setValue(this.frowset.getString(field.getItemid()));
						  }else if("q29z5".equals(field.getItemid().trim()))
						  {
							  String codeitemid=this.frowset.getString(field.getItemid());
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
  				      				      	
  					if("q2901".equals(field.getItemid())|| "b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid()))
  				  		    field.setVisible(false);
  				  		else
  				  		    field.setVisible(true);
  					FieldItem field_n=(FieldItem)field.cloneItem();
  	  				list.add(field_n);
  				  }   				 
  			 }
  			 
  		 }catch(Exception e)
  		 {
  			 e.printStackTrace();
  		 } 
  		 return list;
    }
}
