package com.hjsj.hrms.transaction.general.inform.search;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			ArrayList list=new ArrayList();
			String setname=(String)this.getFormHM().get("tablename");
			ArrayList fielditemlist=this.getUserView().getPrivFieldList(setname);
			if(fielditemlist==null)
				fielditemlist = new ArrayList();
			
			String oper = (String)this.getFormHM().get("oper");
			if(oper!=null && "bonusinfo".equalsIgnoreCase(oper))//奖金信息管理调用单表查询
			{
			    ConstantXml xml = new ConstantXml(this.frameconn, "GZ_PARAM", "Params");			  
			    String jobnumFld = xml.getTextValue("/Params/Bonus/num");  // 工号字段
			    CommonData dataobj = new CommonData();
			    dataobj = new CommonData("dbase:A:@@:A01","人员库");			      
			    list.add(dataobj);
			    
			    FieldItem fielditem = DataDictionary.getFieldItem("b0110");
			    dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemtype()
			    		  +":"+fielditem.getCodesetid()+":"+fielditem.getFieldsetid(),
			    		  	fielditem.getItemdesc());			      
			    list.add(dataobj);
			    
			    fielditem = DataDictionary.getFieldItem("e0122");
			    dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemtype()
			    		  +":"+fielditem.getCodesetid()+":"+fielditem.getFieldsetid(),
			    		  	fielditem.getItemdesc());			      
			    list.add(dataobj);
			    
			    fielditem = DataDictionary.getFieldItem("a0101");	
			    dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemtype()
			    		  +":"+fielditem.getCodesetid()+":"+fielditem.getFieldsetid(),
			    		  	fielditem.getItemdesc());			      
			    list.add(dataobj);
			    if(jobnumFld!=null && jobnumFld.length()>0)
			    {
				 fielditem = DataDictionary.getFieldItem(jobnumFld);		
				    dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemtype()
				    		  +":"+fielditem.getCodesetid()+":"+fielditem.getFieldsetid(),
				    		  	"工号");			      
				    list.add(dataobj);
			    }			   
			}
		    for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("M".equals(fielditem.getItemtype()))
		    	continue;
		      if(this.userView.analyseFieldPriv(fielditem.getItemid())==null)
			        continue;
		      if(this.userView.analyseFieldPriv(fielditem.getItemid()).length()<1)
			        continue;
		      if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;
		      CommonData dataobj = new CommonData();
		      dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemtype()
		    		  +":"+fielditem.getCodesetid()+":"+fielditem.getFieldsetid(),
		    		  	fielditem.getItemdesc());
		      
		      list.add(dataobj);
		    }
		    if(oper!=null && "bonusinfo".equalsIgnoreCase(oper))//奖金信息管理调用单表查询
		    {
			 CommonData dataobj = new CommonData();
			 dataobj = new CommonData("CreateUserName:A:0:"+setname,"录入员");			      
			 list.add(dataobj);
		    }
		    this.getFormHM().clear();
		    this.getFormHM().put("fieldlist",list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
	}

}
