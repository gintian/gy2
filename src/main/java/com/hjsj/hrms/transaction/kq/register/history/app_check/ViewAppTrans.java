/*
 * Created on 2006-2-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.register.history.app_check;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author liwc
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewAppTrans extends IBusiness {
	
	
	
	public void execute() throws GeneralException {
		try
		{
			//String flag=(String)this.getFormHM().get("flag");
			   String table=(String)this.getFormHM().get("table");
			   
			   String ta=table.toLowerCase().substring(0,3);
			   ArrayList fieldList = DataDictionary.getFieldList(ta,Constant.USED_FIELD_SET);// 字段名
			   ArrayList viewlist=new ArrayList();
				for(int i=0;i<fieldList.size();i++)
				{
					FieldItem field_new=new FieldItem();				
					FieldItem field=(FieldItem)fieldList.get(i);				
					field.setValue("");
					field.setViewvalue("");
					if("1".equals(field.getState()))
						field.setVisible(true);
					else
						field.setVisible(false);
					if(field.getItemid().equals(ta+"07"))
						this.getFormHM().put("visi", ta+"07");
					//部门领导与部门领导意见 如果state=1显示否则不现实
					if(field.getItemid().equals(ta+"09")||field.getItemid().equals(ta+"11")||field.getItemid().equals(ta+"13")||field.getItemid().equals(ta+"15"))
					{
						if(!field.isVisible())
						{
							//continue;
						}
					}
					field_new=(FieldItem)field.cloneItem();
					viewlist.add(field_new);
				}
			   /*新增*/
				String tcodeid="";
			      ArrayList infolist=(ArrayList)this.getFormHM().get("selectedinfolist");
			      this.getFormHM().put("infolist",infolist);
			      SearchAllApp searchAllApp=new SearchAllApp();
			      this.getFormHM().put("salist",searchAllApp.getTableList(table, this.frameconn));
			    	  for(int i=0;i< viewlist.size();i++) 
				     {
			         	FieldItem field=(FieldItem)viewlist.get(i);
			    	   	field.setValue("");
			    		field.setViewvalue("");
			    		if(field.getItemid().equals(ta+"05"))
				      	{
				      		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				      		String strDate = sdf.format(new java.util.Date());
				      		field.setItemlength(20);
				      		field.setValue(strDate);
				      	}
			    		if(field.getItemid().equals(ta+"11")||field.getItemid().equals(ta+"15"))
			    		{
			    			tcodeid=codesetidQ(ta,field.getItemid());
			    		}
//			    		if(field.getItemid().equals(ta+"07")||field.getItemid().equals(ta+"11")||field.getItemid().equals(ta+"15"))
			    		if(field.getItemid().equals(ta+"07"))
			    		{
			    			field.setItemtype("M");
			    		}
			    		if(field.getItemid().equals(ta+"11")||field.getItemid().equals(ta+"15"))
			    		{
			    			if("M".equals(tcodeid))
			    			{
			    				field.setItemtype("M");
			    			}else if("A".equals(tcodeid))
			    			{
			    				if("q1515".equals(field.getItemid())|| "q1115".equals(field.getItemid())|| "q1315".equals(field.getItemid()))
						      	{
						      		this.getFormHM().put("salistko",searchAllApp.getOneList15(field.getItemid(),this.getFrameconn()));
						      	}
						      	if("q1511".equals(field.getItemid())|| "q1111".equals(field.getItemid())|| "q1311".equals(field.getItemid()))
						      	{
						      		this.getFormHM().put("salist11",searchAllApp.getOneList11(field.getItemid(),this.getFrameconn()));
						      	}
			    			}
			    		}
//				      	if(field.getItemid().equals("q1503"))
//				      	{
//				    		this.getFormHM().put("salist",searchAllApp.getOneList("0",this.getFrameconn()));		    		
//				      	}
//				      	if(field.getItemid().equals("q1103"))
//				      	{
//				    		this.getFormHM().put("salist",searchAllApp.getOneList("1",this.getFrameconn()));	
//				      	}
//				      	if(field.getItemid().equals("q1303"))
//				      	{
//				    		this.getFormHM().put("salist",searchAllApp.getOneList("3",this.getFrameconn()));	
//				      	}
				      	if(field.getItemid().equalsIgnoreCase(ta+"09"))
				      	{
				      		field.setReadonly(true);
				      	}
				      	if(field.getItemid().equalsIgnoreCase(ta+"13"))
				      	{
				      		field.setReadonly(true);
				      	}
			    		if(field.getItemid().equals(ta+"01")|| "nbase".equals(field.getItemid())|| "a0100".equals(field.getItemid())|| "b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "a0101".equals(field.getItemid())||field.getItemid().equals(ta+"z5")||field.getItemid().equals(ta+"z0")|| "e01a1".equals(field.getItemid()))
			  		       field.setVisible(false);
			    		else if("q1517".equals(field.getItemid())|| "q1519".equals(field.getItemid()))
							field.setVisible(false);
			    	 }
			     this.getFormHM().put("viewlist",viewlist);	
			     //if(flag.equals("0"))
			     //{
			        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
		            String bill_id=(String)hm.get("bill_id");
			         /*查阅单个申请记录*/
			        view(ta, viewlist, bill_id);
			     //}
			     KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
			     ArrayList class_list=kqUtilsClass.getKqClassList();
			     this.getFormHM().put("class_list",class_list);
			     this.getFormHM().put("table",table);
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
		   
	}

	/*查阅单个申请记录*/
	 private void view(String table, ArrayList fieldlist,String bill_id) throws GeneralException {
		 
		String temp;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String insertname="";
		String ta=table.toLowerCase();
		String app_type="";
		String z5="";
		try{
			String sql="select * from "+ta+"_arc where "+ta+"01='"+bill_id+"'";
			this.frecset=dao.search(sql);
			if(this.frecset.next()){
			  z5=this.frecset.getString(ta+"z5");
			  if(z5==null||z5.length()<=0)
				  z5="";
			  for(int i=0;i<fieldlist.size();i++) 
			  {
			 		FieldItem field=(FieldItem)fieldlist.get(i); 
			 		if(field.getItemid().equals(insertname))
			 			field.setValue(bill_id);
			 		else{
			 		if("D".equals(field.getItemtype()))
			 		{
			 			if(field.getItemid().equals(ta+"z7")&&this.frecset.getDate(field.getItemid())==null)
			 			{
			 				continue;
			 			}else if(this.frecset.getDate(field.getItemid())!=null)
			 			{
			 				field.setValue(DateUtils.format(this.frecset.getDate(field.getItemid()),"yyyy-MM-dd HH:mm"));//.replace('-','.' )
			 			}
			 		}
				    else if("A".equals(field.getItemtype()))
			        {
				 	   field.setValue(this.frecset.getString(field.getItemid().toLowerCase()));
				 	   temp=this.frecset.getString(field.getItemid().toLowerCase());
				 	   if(field.getItemid().equalsIgnoreCase(table+"11")||field.getItemid().equalsIgnoreCase(table+"15"))
				 	   {
				 		  temp=AdminCode.getCode(field.getCodesetid(),temp)!=null?AdminCode.getCode(field.getCodesetid( ),temp).getCodename():"";
				 		 if(field.getItemid().equalsIgnoreCase(table+"11")&&!"0".equals(field.getCodesetid()))
				      	 {
				      			if(temp!=null&&temp.length()>0)
				      				this.getFormHM().put("mess2", field.getValue());
				      			else
				      				this.getFormHM().put("mess2", "");
				      	 }else if(field.getItemid().equalsIgnoreCase(table+"15")&&!"0".equals(field.getCodesetid()))
				      	 {
				      		if(temp!=null&&temp.length()>0)
			      				this.getFormHM().put("mess1", field.getValue());
			      			else
			      				this.getFormHM().put("mess1", "");
				      	 }
				 	  }else if(temp!=null && temp.trim().length()>0&&!"0".equals(field.getCodesetid()))
				 	  {
					 		  temp=AdminCode.getCode(field.getCodesetid(),temp)!=null?AdminCode.getCode(field.getCodesetid( ),temp).getCodename():"";
					  }
			  		    
			  		    field.setViewvalue(temp);
			  		    if("q1103".equals(field.getItemid())|| "q1303".equals(field.getItemid())|| "q1503".equals(field.getItemid()))
			  		    {
			  		    	app_type=this.frecset.getString(field.getItemid().toLowerCase());
			  		    }
			  		    if(field.getItemid().equalsIgnoreCase(ta+"09"))
				      	{
			  		    	field.setReadonly(true);
				      	}
				      	if(field.getItemid().equalsIgnoreCase(ta+"13"))
				      	{
				      		field.setReadonly(true);
				      	}
			  		 }
			        else 	  
			 		 	field.setValue(this.frecset.getString(field.getItemid().toLowerCase()));
				   }	
			 	}
			    String isAllow="true";
			    if(this.frecset.getString(ta+"z5")!=null&& "03".equals(this.frecset.getString(ta+"z5")))
			    	isAllow="false";
		        this.getFormHM().put("isAllow", isAllow);
			}
		}catch(Exception e){
		     e.printStackTrace();
		     throw GeneralExceptionHandler.Handle(e); 
		}finally
		{
		       for(int i=0;i< fieldlist.size();i++) 
		       {
	         	FieldItem field=(FieldItem)fieldlist.get(i);
	    		if(field.getItemid().equals(ta+"01")|| "nbase".equals(field.getItemid())|| "a0100".equals(field.getItemid())||field.getItemid().equals(ta+"z5")||field.getItemid().equals(ta+"z0")|| "e01a1".equals(field.getItemid()))
	  		      field.setVisible(false);
	    		else if("q1517".equals(field.getItemid())|| "q1519".equals(field.getItemid()))
					field.setVisible(false);
	    		if("b0110".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "a0101".equals(field.getItemid()))
	    		  field.setReadonly(true);
    		   }	
		       
		       this.getFormHM().put("fieldlist",fieldlist);
		       this.getFormHM().put("viewlist",fieldlist);		       
		       this.getFormHM().put("mess",app_type);
		       this.getFormHM().put("z5",z5);
		}
		  
    }
	  
	  public String codesetidQ(String teble,String itemid)
	  {
		  String codesetid="";
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  StringBuffer sql = new StringBuffer();
		  RowSet rowSet=null;
		  String itemtype="";
		  String codeid="";
		  try{
			  sql.append("select itemtype,codesetid from t_hr_busifield  where fieldsetid='"+teble+"' and itemid='"+itemid+"'");
			  rowSet=dao.search(sql.toString());
			  while(rowSet.next())
				{
				    itemtype = rowSet.getString("itemtype");
					codeid=rowSet.getString("codesetid");
				}
			  if("A".equals(itemtype)&& "0".equals(codeid))
			  {
				  codesetid="M";
			  }else
			  {
				  codesetid="A";
			  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }finally{
	        	if(rowSet!=null)
					try {
						rowSet.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        }
		  return codesetid;
	  }
}
