package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SetSubclass.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SetSubclass extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tag = (String)this.getFormHM().get("tag");
		String tagname = (String)this.getFormHM().get("tagname");
		if(tag==null|| "".equalsIgnoreCase(tag))
			tag = "set_a";
		SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());
		String viewname = infoxml.getView_value(tag,tagname);
		ArrayList list = new ArrayList();
		ArrayList fielditemlist = new ArrayList();
		if("set_a".equalsIgnoreCase(tag)){
			fielditemlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.USED_FIELD_SET);
		    if(fielditemlist!=null){
		    	for(int i=0;i<fielditemlist.size();i++)
		    	{
		    		FieldSet fieldset=(FieldSet)fielditemlist.get(i);
			  	    if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
			  	    	continue;
			  	    if("A00".equals(fieldset.getFieldsetid())|| "B01".equals(fieldset.getFieldsetid())|| "B00".equals(fieldset.getFieldsetid())|| "A01".equals(fieldset.getFieldsetid()))
			  	    	continue;
			  	    CommonData dataobj=null;
			  	    dataobj = new CommonData(fieldset.getFieldsetid(), /*"(" + fieldset.getFieldsetid() + ")"+*/ fieldset.getCustomdesc()/*getFieldsetdesc()*/);
			  	    list.add(dataobj);
			    }
		    }
		} else {
			fielditemlist=DataDictionary.getFieldList(tag.toUpperCase(),Constant.USED_FIELD_SET);
			if(fielditemlist!=null)
		    {
				/*获取所有分类中的指标*/
				String allField = infoxml.getAllSetfield(tag);
				allField = (","+allField+",").toLowerCase();
				/*当前分类的指标*/
				String currentField = (","+viewname+",").toLowerCase();
				for(int i=0;i<fielditemlist.size();i++)
			    {
			      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			      if("M".equals(fielditem.getItemtype()))
			    	continue;
			      if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
			        continue;
				  /*【61428】过滤掉其他分类已经设置了的指标，防止重复显示 guodd*/
				  if(allField.indexOf(","+fielditem.getItemid().toLowerCase()+",")!=-1 &&
					 currentField.indexOf(","+fielditem.getItemid().toLowerCase()+",")==-1){
					continue;
				  }
			      CommonData dataobj = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
			      list.add(dataobj);
			    }
		    }
		}
		this.getFormHM().put("subclasslist",list);
		ArrayList selectlist = new ArrayList();
		
		if("set_a".equalsIgnoreCase(tag)){
			if(viewname.length()>0){
				String[] viewnames = viewname.split(",");
				for(int i=0;i<viewnames.length;i++){
					CommonData dataobj;
					FieldSet fieldset =DataDictionary.getFieldSetVo(viewnames[i].toUpperCase());
					if(fieldset!=null){
						dataobj = new CommonData(viewnames[i],fieldset.getCustomdesc());
						selectlist.add(dataobj);
					}
				}
			}
		}else {
			if(viewname.length()>0){
				String[] viewnames = viewname.split(",");
				for(int i=0;i<viewnames.length;i++){
					CommonData dataobj = null;
					if("b0110".equalsIgnoreCase(viewnames[i])){
						dataobj = new CommonData(viewnames[i],"单位名称");
					}else if("e01a1".equalsIgnoreCase(viewnames[i])){
						dataobj = new CommonData(viewnames[i],"职位名称");
					}else if("e0122".equalsIgnoreCase(viewnames[i])){
						dataobj = new CommonData(viewnames[i],"部门");
					}else{
						FieldItem fielditem =DataDictionary.getFieldItem(viewnames[i].toUpperCase());
						if(fielditem!=null){
							dataobj = new CommonData(viewnames[i],fielditem.getItemdesc());
							selectlist.add(dataobj);
							continue;
						}
					}
					if(dataobj!=null)
					selectlist.add(dataobj);
				}
			}
		}
		
		this.getFormHM().put("selectsubclass",selectlist);
		 
	}
}
