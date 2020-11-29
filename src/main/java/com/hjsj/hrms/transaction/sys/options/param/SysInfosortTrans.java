package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SysInfosortTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SysInfosortTrans extends IBusiness {

	public void execute() throws GeneralException {
	try
	{
		// TODO Auto-generated method stub
		SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());
		ArrayList rolelist = new ArrayList();
		String tag = (String)this.getFormHM().get("tag");
		if(tag==null|| "".equalsIgnoreCase(tag))
			tag = "set_a";
		ArrayList taglist = infoxml.getView_tag(tag);
		ArrayList list = new ArrayList();
		ArrayList fielditemlist = new ArrayList();
		for(int i=0;i<taglist.size();i++){
			LazyDynaBean bean = new LazyDynaBean();
			String tagname = (String)taglist.get(i);
			String viewvalue = infoxml.getView_value(tag,tagname);
			String recvalue = "";
			if("set_a".equalsIgnoreCase(tag)){
				if(viewvalue.length()>0){
					String[] viewvalues = viewvalue.split(",");
					for(int j=1;j<viewvalues.length+1;j++){
						FieldSet fieldset =DataDictionary.getFieldSetVo(viewvalues[j-1].toUpperCase());
						if(fieldset!=null)
							recvalue += fieldset.getCustomdesc()+",";
						if(j%5==0)
							recvalue += "<br>";
					}
					if(recvalue.length()>0)					
						recvalue = recvalue.substring(0,recvalue.length()-1);
				}
				bean.set("tagname",tagname);
				bean.set("viewvalue",recvalue);
				rolelist.add(bean);
			}else {
				if(viewvalue.length()>0){
					String[] viewvalues = viewvalue.split(",");
					for(int j=1;j<viewvalues.length+1;j++){
						if("b0110".equalsIgnoreCase(viewvalues[j-1])){
							recvalue += "单位名称,";
						}else if("e01a1".equalsIgnoreCase(viewvalues[j-1])){
							recvalue += "职位名称,";
						}else if("e0122".equalsIgnoreCase(viewvalues[j-1])){
							recvalue += "部门,";
						}else{
							FieldItem fielditem =DataDictionary.getFieldItem(viewvalues[j-1].toUpperCase());
							if(fielditem!=null)
								recvalue += fielditem.getItemdesc()+",";
						}
						if(j%5==0)
							recvalue += "<br>";
					}
					if(recvalue.length()>0)
						recvalue = recvalue.substring(0,recvalue.length()-1);
				}
				bean.set("tagname",tagname);
				bean.set("viewvalue",recvalue);
				rolelist.add(bean);
			}
		}
		if("set_b".equalsIgnoreCase(tag)) {
			fielditemlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.USED_FIELD_SET);
		    if(fielditemlist!=null){
		    	for(int j=0;j<fielditemlist.size();j++)
		    	{
		    		FieldSet fieldset=(FieldSet)fielditemlist.get(j);
			  	    if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
			  	    	continue;
			  	    if("B01".equals(fieldset.getFieldsetid())|| "B00".equals(fieldset.getFieldsetid()))
			  	    	continue;
			  	    //CommonData dataobj = new CommonData(fieldset.getFieldsetid(), /*"(" + fieldset.getFieldsetid() + ")"+*/ fieldset.getCustomdesc()/*getFieldsetdesc()*/);
			  	    LazyDynaBean filebean = new LazyDynaBean();
			  	    String settag  = fieldset.getFieldsetid();
			  	    filebean.set("setid",settag);
			  	    filebean.set("setname",fieldset.getCustomdesc());
			  	    
			  	    ArrayList taglists = infoxml.getView_tag(settag);
			  	    String recvalue = "";
				  	  for(int i=0;i<taglists.size();i++){
							String tagname = (String)taglists.get(i);
							String viewvalue = infoxml.getView_value(settag,tagname);
							 recvalue += tagname+" : " ;
							if(viewvalue.length()>0){
								String[] viewvalues = viewvalue.split(",");
								for(int x=1;x<viewvalues.length+1;x++){
									if("b0110".equalsIgnoreCase(viewvalues[x-1])){
										recvalue += "单位名称,";
									}else if("e01a1".equalsIgnoreCase(viewvalues[x-1])){
										recvalue += "职位名称,";
									}else if("e0122".equalsIgnoreCase(viewvalues[x-1])){
										recvalue += "部门,";
									}else{
										FieldItem fielditem =DataDictionary.getFieldItem(viewvalues[x-1].toUpperCase());
										if(fielditem!=null)
											recvalue += fielditem.getItemdesc()+",";
									}
									if(x%6==0)
										recvalue += "<br> ";
								}
								recvalue = recvalue.substring(0,recvalue.length()-1);
							}
							if(j<fielditemlist.size()-1)
						  		  recvalue += "<br>";
				  	  }
				  	  filebean.set("allsetvalue",recvalue);
			        list.add(filebean);
			       }
		    }
		    this.getFormHM().put("errmes","");
		} 
		if("set_a".equalsIgnoreCase(tag)){
			//子集分类按照排序显示  wangb 2019-07-17 bug 50347
			String tagorder =infoxml.getInfo_param("order");
			String[] tagorders = tagorder.split(",");
			ArrayList orderRoleList = new ArrayList();
			for(int i = 0 ; i < tagorders.length; i++){
				String order = tagorders[i];
				for(int j = 0 ; j < rolelist.size(); j++){
					LazyDynaBean lazyDynaBean = (LazyDynaBean) rolelist.get(j);
					if(order.equalsIgnoreCase((String)lazyDynaBean.get("tagname"))){
						orderRoleList.add(lazyDynaBean);
						break;
					}
				}
			}
			this.getFormHM().put("filesetlists",list);
			this.getFormHM().put("rolelist",orderRoleList);
		}else{
			this.getFormHM().put("filesetlists",list);
			this.getFormHM().put("rolelist",rolelist);
		} 
		if("set_a".equalsIgnoreCase(tag)){
			//子集分类按照排序显示  wangb 2019-07-17 bug 50347
			String tagorder =infoxml.getInfo_param("order");
			String[] tagorders = tagorder.split(",");
			ArrayList orderRoleList = new ArrayList();
			for(int i = 0 ; i < tagorders.length; i++){
				String order = tagorders[i];
				for(int j = 0 ; j < rolelist.size(); j++){
					LazyDynaBean lazyDynaBean = (LazyDynaBean) rolelist.get(j);
					if(order.equalsIgnoreCase((String)lazyDynaBean.get("tagname"))){
						orderRoleList.add(lazyDynaBean);
						break;
					}
				}
			}
			this.getFormHM().put("filesetlists",list);
			this.getFormHM().put("rolelist",orderRoleList);
		}else{
			this.getFormHM().put("filesetlists",list);
			this.getFormHM().put("rolelist",rolelist);
		} 
	}
	catch(Exception ex)
	{
		throw GeneralExceptionHandler.Handle(ex);
	}
 }

}
