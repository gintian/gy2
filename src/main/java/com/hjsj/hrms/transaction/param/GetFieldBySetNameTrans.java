package com.hjsj.hrms.transaction.param;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
/**
 * 
 *<p>Title:GetFieldBySetNameTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 15, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class GetFieldBySetNameTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String setname=(String)this.getFormHM().get("tablename");
			String id=(String)this.getFormHM().get("idv");			
			ArrayList codesetlist=this.getFieldBySetNameTrans(setname,this.userView);	
			this.getFormHM().put("codesetlist",codesetlist);
			this.getFormHM().put("idv",id);
			this.getFormHM().put("tablename",setname);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
	}
	public ArrayList getFieldBySetNameTrans(String tablename,UserView userView)
    {
    	ArrayList list=new ArrayList();
    	CommonData dataobj = new CommonData();
		dataobj.setDataName("请选择");
		dataobj.setDataValue("");		
		list.add(dataobj);
		/*dataobj = new CommonData();
		dataobj.setDataName("A0100:人员编号");
		dataobj.setDataValue("A0100");
		list.add(dataobj);*/
		String setname=tablename;		
		if(setname==null||setname.length()<=0)
           return list;
		ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.ALL_FIELD_SET);
		
		if(fielditemlist!=null)
		{
			for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("M".equals(fielditem.getItemtype()))
		    	  continue;
		      if("0".equals(userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;
		      if("A".equals(fielditem.getItemtype())&&!"0".equalsIgnoreCase(fielditem.getCodesetid()))
		    	  continue;
		      if("D".equals(fielditem.getItemtype()))
		    	  continue;
		      /*if(fielditem.getItemlength()<10)
			        continue;*/
		      if(fielditem.getCodesetid()!=null&&!"UM".equals(fielditem.getCodesetid())&&!"UN".equals(fielditem.getCodesetid())&&!"@K".equals(fielditem.getCodesetid()))
		      {
		    	  dataobj = new CommonData();
			      dataobj = new CommonData(fielditem.getItemid(),   fielditem.getItemid().toUpperCase()+ ":"+ fielditem.getItemdesc());
			      list.add(dataobj);		     
			    }
		      }
		     
		}
	    return list;
    }

	public ArrayList getUsedFieldBySetNameTrans(String tablename,UserView userView)
    {
    	ArrayList list=new ArrayList();
    	CommonData dataobj = new CommonData();
		dataobj.setDataName("请选择");
		dataobj.setDataValue("");		
		list.add(dataobj);
		/*dataobj = new CommonData();
		dataobj.setDataName("A0100:人员编号");
		dataobj.setDataValue("A0100");
		list.add(dataobj);*/
		String setname=tablename;		
		if(setname==null||setname.length()<=0)
           return list;
		ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		
		if(fielditemlist!=null)
		{
			for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("M".equals(fielditem.getItemtype()))
		    	  continue;
		      if("0".equals(userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;
		      if("A".equals(fielditem.getItemtype())&&!"0".equalsIgnoreCase(fielditem.getCodesetid()))
		    	  continue;
		      if("D".equals(fielditem.getItemtype()))
		    	  continue;
		      /*if(fielditem.getItemlength()<10)
			        continue;*/
		      if(fielditem.getCodesetid()!=null&&!"UM".equals(fielditem.getCodesetid())&&!"UN".equals(fielditem.getCodesetid())&&!"@K".equals(fielditem.getCodesetid()))
		      {
		    	  dataobj = new CommonData();
			      dataobj = new CommonData(fielditem.getItemid(),   fielditem.getItemid().toUpperCase()+ ":"+ fielditem.getItemdesc());
			      list.add(dataobj);		     
			    }
		      }
		     
		}
	    return list;
    }
	
	public ArrayList getUsedFieldBySetNameTransOutNum(String tablename,UserView userView)//zhaogd 2013-11-30 且不支持数值型
    {
    	ArrayList list=new ArrayList();
    	CommonData dataobj = new CommonData();
		dataobj.setDataName("请选择");
		dataobj.setDataValue("");		
		list.add(dataobj);
		/*dataobj = new CommonData();
		dataobj.setDataName("A0100:人员编号");
		dataobj.setDataValue("A0100");
		list.add(dataobj);*/
		String setname=tablename;		
		if(setname==null||setname.length()<=0)
           return list;
		ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		
		if(fielditemlist!=null)
		{
			for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("M".equals(fielditem.getItemtype()))
		    	  continue;
		      if("0".equals(userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;
		      if("1".equals(userView.analyseFieldPriv(fielditem.getItemid())))//zhaogd 2014-2-26 在Excel导入中唯一性指标去除读权限指标
		    	  continue;
		      if("A".equals(fielditem.getItemtype())&&!"0".equalsIgnoreCase(fielditem.getCodesetid()))
		    	  continue;
		      if("D".equals(fielditem.getItemtype()))
		    	  continue;
		      if("N".equals(fielditem.getItemtype()))
		    	  continue;
		      /*if(fielditem.getItemlength()<10)
			        continue;*/
		      if(fielditem.getCodesetid()!=null&&!"UM".equals(fielditem.getCodesetid())&&!"UN".equals(fielditem.getCodesetid())&&!"@K".equals(fielditem.getCodesetid()))
		      {
		    	  dataobj = new CommonData();
			      dataobj = new CommonData(fielditem.getItemid(),   fielditem.getItemid().toUpperCase()+ ":"+ fielditem.getItemdesc());
			      list.add(dataobj);		     
			    }
		      }
		     
		}
	    return list;
    }

}
