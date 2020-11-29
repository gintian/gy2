/*
 * Created on 2005-11-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_persondb;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchPersonentryTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String operate="";
		if(hm.get("b_update")!=null)
			operate=(String)hm.get("b_update");
		String username=userView.getUserName();
		if("add".equals(operate))
			username=(String)userView.getHm().get("add_username");
		
		
	    StringBuffer strsql=new StringBuffer();
		RecordVo constantuser_vo=ConstantParamter.getRealConstantVo("SS_LOGIN_USER_PWD");
		String usernamefield=constantuser_vo.getString("str_value");
		if(usernamefield!=null && usernamefield.length()>0)
		    usernamefield=usernamefield.substring(0,usernamefield.indexOf(","));
		if(usernamefield !=null && usernamefield.indexOf(",")>0)
			  usernamefield=usernamefield.substring(0,usernamefield.indexOf(","));
			else
			  usernamefield="username";
		RecordVo constandb_vo=(RecordVo)ConstantParamter.getRealConstantVo("ZP_DBNAME");
		String userbase=constandb_vo.getString("str_value");
		RecordVo constantset_vo=ConstantParamter.getRealConstantVo("ZP_SUBSET_LIST");
		ArrayList infoSetList=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		String setStr=constantset_vo.getString("str_value");
		cat.debug("---setStr---->" + setStr);
		ArrayList zpsetlist=new ArrayList();
		try{	    	
	    	if(!infoSetList.isEmpty())
	    	{
	    	   for(int i=0;i<infoSetList.size();i++)
	    	   {
	    	   	 FieldSet fieldset=(FieldSet)infoSetList.get(i);
	    	    if(setStr!=null && setStr.indexOf(fieldset.getFieldsetid())!=-1)
	    	    {
	    	    	zpsetlist.add(fieldset);
	    	    }
	    	   }
	    	}	    
	    }catch(Exception e){
	       e.printStackTrace();
	       throw GeneralExceptionHandler.Handle(e); 
	    }    
		String A0100=userView.getUserId();
		if("add".equals(operate))
			A0100=(String)userView.getHm().get("add_a0100");
		
	    ArrayList zpfieldlist=new ArrayList();
		RecordVo constantfield_vo=ConstantParamter.getRealConstantVo("ZP_FIELD_LIST");
		String fieldStr=constantfield_vo.getString("str_value");
        if(fieldStr!=null && fieldStr.length()>0)
        {
			String fieldsubStr=fieldStr.substring(fieldStr.indexOf("A01{"));
			cat.debug("select zp_field_list" + fieldsubStr);
			if(fieldsubStr!=null && fieldsubStr.length()>4)
				fieldStr=fieldsubStr.substring(4,fieldsubStr.indexOf("}"));
			cat.debug("select sub zp_field_list");
			ArrayList infofieldlist=DataDictionary.getFieldList("A01",Constant.EMPLOY_FIELD_SET);
			strsql.append("select * from ");
			strsql.append(userbase);
			strsql.append("A01 where ");
			strsql.append(usernamefield);
			strsql.append("='");
			strsql.append(username);
			strsql.append("'");
			cat.debug("-----userView.getUserId()----->" + A0100);
			List entrollpersonlist=ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());
			if(entrollpersonlist!=null && !entrollpersonlist.isEmpty())
			{
			   LazyDynaBean rec=(LazyDynaBean)entrollpersonlist.get(0);
			   A0100=rec.get("a0100")!=null?rec.get("a0100").toString():"";
			}
			for(int i=0;i<infofieldlist.size();i++)
		 	{
		 	    FieldItem fielditem=(FieldItem)infofieldlist.get(i);
		 	    if(fieldStr.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1)
		 	    {
		 	    	FieldItemView fieldItemView=new FieldItemView();
					fieldItemView.setAuditingFormula(fielditem.getAuditingFormula());
					fieldItemView.setAuditingInformation(fielditem.getAuditingInformation());
					fieldItemView.setCodesetid(fielditem.getCodesetid());
					fieldItemView.setDecimalwidth(fielditem.getDecimalwidth());
					fieldItemView.setDisplayid(fielditem.getDisplayid());
					fieldItemView.setDisplaywidth(fielditem.getDisplaywidth());
					fieldItemView.setExplain(fielditem.getExplain());
					fieldItemView.setFieldsetid(fielditem.getFieldsetid());
					fieldItemView.setItemdesc(fielditem.getItemdesc());
					fieldItemView.setItemid(fielditem.getItemid());
					fieldItemView.setItemlength(fielditem.getItemlength());
					fieldItemView.setItemtype(fielditem.getItemtype());
					fieldItemView.setModuleflag(fielditem.getModuleflag());
					fieldItemView.setState(fielditem.getState());
					fieldItemView.setUseflag(fielditem.getUseflag());
					fieldItemView.setPriv_status(fielditem.getPriv_status());
			           //在struts用来表示换行的变量
					fieldItemView.setRowflag(String.valueOf(infofieldlist.size()-1));
		 	 		//为了在选择代码时方便而压入权限码开始
				    if(!entrollpersonlist.isEmpty())
				    {
				  	   LazyDynaBean recdata=(LazyDynaBean)entrollpersonlist.get(0);
				       if("A".equals(fielditem.getItemtype()) || "M".equals(fielditem.getItemtype()))
					   {
					      if(!"0".equals(fielditem.getCodesetid()))
					      {
							 String codevalue=recdata.get(fielditem.getItemid())!=null?recdata.get(fielditem.getItemid()).toString():"";
							 //System.out.println("itemida" + fieldItem.getItemid() + "typea" + fieldItem.getItemtype());
							 if(codevalue !=null && codevalue.trim().length()>0 && fielditem.getCodesetid()!=null && fielditem.getCodesetid().trim().length()>0)
							   	fieldItemView.setViewvalue(AdminCode.getCode(fielditem.getCodesetid(),codevalue)!=null?AdminCode.getCode(fielditem.getCodesetid(),codevalue).getCodename():"");
							 else
								fieldItemView.setViewvalue("");
					       }
					       else
					       {
						      //System.out.println("itemid" + fieldItem.getItemid() + "type" + fieldItem.getItemtype());
						      fieldItemView.setViewvalue(recdata.get(fielditem.getItemid())!=null?recdata.get(fielditem.getItemid()).toString():"");
					       }
					       fieldItemView.setValue(recdata.get(fielditem.getItemid())!=null?recdata.get(fielditem.getItemid()).toString():"");						
					   }else if("D".equals(fielditem.getItemtype()))                 //日期型有待格式化处理
					   {
						  if(recdata.get(fielditem.getItemid())!=null && recdata.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==10)
					      {
							 fieldItemView.setViewvalue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,10)));
							 fieldItemView.setValue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,10)));
						  }else if(recdata.get(fielditem.getItemid())!=null && recdata.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==4)
						  {
							 fieldItemView.setViewvalue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,4)));
							 fieldItemView.setValue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,4)));
						  }else if(recdata.get(fielditem.getItemid())!=null && recdata.get(fielditem.getItemid()).toString().length()>=10 && fielditem.getItemlength()==7)
						  {
							 fieldItemView.setViewvalue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,7)));
							 fieldItemView.setValue(new FormatValue().format(fielditem,recdata.get(fielditem.getItemid().toLowerCase()).toString().substring(0,7)));
						  }
						  else
			              {
							 fieldItemView.setViewvalue("");
							 fieldItemView.setValue("");
			              }
					   }
					   else                                                          //数值类型的有待格式化处理
					   {
						  fieldItemView.setValue(PubFunc.DoFormatDecimal(recdata.get(fielditem.getItemid())!=null?recdata.get(fielditem.getItemid()).toString():"",fielditem.getDecimalwidth()));						
					   }
				     }		   
				   	 zpfieldlist.add(fieldItemView);
			       }
		       } 
		    }
		    cat.debug("----- A0100 ----->" + A0100);
		    if("add".equals(operate))
		    	this.getFormHM().put("isHandWork","1");
		    
			this.getFormHM().put("a0100",A0100);
			this.getFormHM().put("actiontype","update");
		    this.getFormHM().put("zpfieldlist",zpfieldlist);            //压回页面
		    this.getFormHM().put("zpsetlist",zpsetlist);
		    this.getFormHM().put("userbase",userbase);
		    this.getFormHM().put("useraccount",username);
		    this.getFormHM().put("existusermessage","");
		    this.getFormHM().put("setname","A01");		
	}

}
