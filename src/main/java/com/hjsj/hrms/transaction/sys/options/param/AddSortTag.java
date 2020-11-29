package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:AddSortTag.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class AddSortTag extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tagname = (String)this.getFormHM().get("sortname");
		/*[58055] 有特殊符号时此处需要转回去，否则会显示不出。原因时保存分类顺序时，转回来了，保存没转，导致匹配不上不显示。此处统一处理一下 guodd 2020-02-17*/
		tagname = PubFunc.keyWord_reback(tagname);
		String tag = (String)this.getFormHM().get("tag");
		if(tag==null|| "".equalsIgnoreCase(tag))
			tag = "set_a";
		boolean flag = true;
		if("set_a".equals(tag)){
			ArrayList fielditemlist=DataDictionary.getFieldSetList(Constant.ALL_FIELD_SET,Constant.EMPLOY_FIELD_SET);
			for(int i=0;i<fielditemlist.size();i++){
				FieldSet fs = (FieldSet)fielditemlist.get(i);
				if(fs!=null&&tagname.equalsIgnoreCase(fs.getCustomdesc())){
					flag = false;
					break;
				}
			}
		}
		String errmes = null;
			if(flag){
				SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());
				errmes = infoxml.saveView_param(tag,tagname,this.getFrameconn());
				infoxml.reOrederSet();
			}else{
				errmes="11";
			}
		this.getFormHM().put("errmes",errmes);
		this.getFormHM().put("sortname","");
	}

}
