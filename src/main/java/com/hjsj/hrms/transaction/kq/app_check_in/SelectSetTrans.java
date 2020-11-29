/*
 * Created on 2006-2-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * @author wxh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SelectSetTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String infor="1";
		ArrayList list=new ArrayList();
		
		ArrayList fieldsetlist=null;
	    if("1".equals(infor))
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.ALL_FIELD_SET);
	    else if(("2".equals(infor)))
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
	    else
	      fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);	    
	    for(int i=0;i<fieldsetlist.size();i++)
	    {
	      FieldSet fieldset=(FieldSet)fieldsetlist.get(i);	     
	      if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
	        continue;
	      //多媒体子集不显示 wangy
	      if(!"A00".equalsIgnoreCase(fieldset.getFieldsetid())&&!"B00".equalsIgnoreCase(fieldset.getFieldsetid())&&!"K00".equalsIgnoreCase(fieldset.getFieldsetid()))
	      {
	    	  CommonData dataobj = new CommonData(fieldset.getFieldsetid(), /*"(" + fieldset.getFieldsetid() + ")"+*/ fieldset.getCustomdesc());
	    	  list.add(dataobj);
	      }
	    }
	    this.getFormHM().clear();
	    /**为了不让前台弹出提示框*/
	    this.getFormHM().put("message","");
	    this.getFormHM().put("setlist",list);
	}

}
