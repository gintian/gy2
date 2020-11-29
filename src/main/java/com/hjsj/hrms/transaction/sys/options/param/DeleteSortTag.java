package com.hjsj.hrms.transaction.sys.options.param;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 *<p>Title:DeleteSortTag.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class DeleteSortTag extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList list = (ArrayList) this.getFormHM().get("selectedlist");
		SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());
		String tag = (String)this.getFormHM().get("tag");
		for(int i=0;i<list.size();i++){
			LazyDynaBean bean = (LazyDynaBean)list.get(i);
			String tagname = (String)bean.get("tagname");
			infoxml.deleteTag(tag,tagname);
		}
		infoxml.reOrederSet();
		this.getFormHM().put("errmes","");
	}

}
