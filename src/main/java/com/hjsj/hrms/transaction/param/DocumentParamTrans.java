package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.param.DocumentParamXML;
import com.hjsj.hrms.businessobject.sys.Configuration;
import com.hjsj.hrms.businessobject.sys.options.JWhichUtil;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
/**
 * 
 *<p>Title:DocumentParamTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 9, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class DocumentParamTrans  extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList field_list=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		ArrayList user_field_list=new ArrayList();
		CommonData dataobj=new CommonData();
		dataobj.setDataName("请选择");
		dataobj.setDataValue("");
		user_field_list.add(dataobj);
		if(field_list!=null){
			for(int i=0;i<field_list.size();i++)
			{
				FieldSet fielditem=(FieldSet)field_list.get(i);
				if("A01".equalsIgnoreCase(fielditem.getFieldsetid())|| "A00".equalsIgnoreCase(fielditem.getFieldsetid()))
					continue;
				dataobj = new CommonData(fielditem.getFieldsetid(), fielditem.getFieldsetdesc());
				user_field_list.add(dataobj);
			}
		}
		DocumentParamXML documentparamXML=new DocumentParamXML(this.getFrameconn());
		this.getFormHM().put("user_field_list",user_field_list);
		bzParamSet(documentparamXML,user_field_list);//班子
		
	}
	/**
	 * 班子
	 * @param user_field_list
	 */
	private void bzParamSet(DocumentParamXML documentparamXML,ArrayList user_field_list)
	{
		 
		String bz_fieldsetid=documentparamXML.getValue(DocumentParamXML.FILESET,"setid");
		if(bz_fieldsetid==null||bz_fieldsetid.length()<=0)
		{
			CommonData fielditem=(CommonData)user_field_list.get(0);
			bz_fieldsetid=fielditem.getDataValue();
		}
		this.getFormHM().put("bz_fieldsetid",bz_fieldsetid);//人员集指标
		//LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);		
		ArrayList bz_codesetlist=getFieldBySetNameTrans(bz_fieldsetid,this.userView);	    
 	    String bz_codesetid=documentparamXML.getValue(DocumentParamXML.FILESET,"fielditem");
 	    if(bz_codesetid==null||bz_codesetid.length()<=0)
 	    {
 	    	if(bz_codesetlist!=null&&bz_codesetlist.size()>0)
 	    	{
 	    		CommonData dataobj =(CommonData)bz_codesetlist.get(0);
 	 	    	bz_codesetid=dataobj.getDataValue();
 	    	}
 	    	
 	    }
 	    //ArrayList bz_privlist = this.getBZ_privlist();
 	    //String law_file_priv = this.getLaw_file_priv();//分类授权
 	    
 	  
 	    //ArrayList bz_codeitemlist=leaderParam.codeItemList(bz_fieldsetid,bz_codesetid);
 	    //String bz_codeitemid=documentparamXML.getValue(DocumentParamXML.FILESET,"value");;
 	    this.getFormHM().put("bz_codesetid",bz_codesetid);//代码指标
 	    this.getFormHM().put("bz_codesetlist",bz_codesetlist);	//代码指标
 	    //this.getFormHM().put("bz_privlist", bz_privlist);
 	    //this.getFormHM().put("law_file_priv", law_file_priv);
 	    //this.getFormHM().put("bz_codeitemlist",bz_codeitemlist);//代码值
 	    //this.getFormHM().put("bz_codeitemid",bz_codeitemid);//代码值
	}
	
	public ArrayList getFieldBySetNameTrans(String tablename,UserView userView)
    {
    	ArrayList list=new ArrayList();
    	CommonData dataobj = new CommonData();
		dataobj.setDataName("请选择");
		dataobj.setDataValue("");
		list.add(dataobj);
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
		      if(fielditem.getItemlength()<10)
			        continue;
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
	public ArrayList getBZ_privlist(){
		ArrayList list = new ArrayList();
		list.add(new CommonData("true","文件授权"));
		list.add(new CommonData("false","分类授权"));
		return list;
	}
	public String getLaw_file_priv(){
		String str = "";
		try{
			String rootPath=JWhichUtil.getResourceFilePath("system.properties");	    	
			if(rootPath==null||rootPath.length()<=0)
			    throw GeneralExceptionHandler.Handle(new GeneralException("找不到system.properties资源文件！"));
			Configuration configuration=new Configuration(rootPath);				
			String law_file_priv=configuration.getValue("law_file_priv");
			law_file_priv=law_file_priv!=null&&law_file_priv.length()>0?law_file_priv:"";
			str = law_file_priv;
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
}
