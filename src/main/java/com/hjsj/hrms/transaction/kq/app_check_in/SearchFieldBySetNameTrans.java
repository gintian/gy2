/*
 * Created on 2006-2-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * @author wxh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchFieldBySetNameTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try
		{
			ArrayList list=new ArrayList();
			String setname=(String)this.getFormHM().get("setname");
			ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
			KqParameter para = new KqParameter();
			boolean isPost=true;
	    	if ("1".equalsIgnoreCase(para.getKq_orgView_post())) {
	    		isPost = false;
	    	} else {
	    		isPost = true;
	    	}
		    for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;
		      if("e01a1".equalsIgnoreCase(fielditem.getItemid())&&!isPost)
		    	  continue;
		      if("M".equalsIgnoreCase(fielditem.getItemtype()))
		    	  continue;
		      CommonData dataobj = new CommonData();
		      dataobj = new CommonData(fielditem.getItemid(), /*"(" + fielditem.getItemid()+ ")"+*/ fielditem.getItemdesc());
		      
		      list.add(dataobj);
		    }
		    this.getFormHM().clear();
		    this.getFormHM().put("queryfieldlist",list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
	}

}
