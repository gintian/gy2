package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 数据视图设置职位
 * <p>Title:SearchHrSyncPostFiled.java</p>
 * <p>Description>:SearchHrSyncPostFiled.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jun 21, 2010 10:03:19 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SearchHrSyncPostFiled extends IBusiness {

	public void execute() throws GeneralException {
		HrSyncBo hsb = new HrSyncBo(this.frameconn);	
		ArrayList fieldsetlist = new ArrayList();
		fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
		ArrayList setlist = new ArrayList();
		for(int i=0;i<fieldsetlist.size();i++)
	    {
	      FieldSet fieldset=(FieldSet)fieldsetlist.get(i);
	      if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
	        continue;
	      if("A00".equals(fieldset.getFieldsetid())|| "B00".equals(fieldset.getFieldsetid())|| "K00".equals(fieldset.getFieldsetid()))
	    	  continue;
	      CommonData dataobj = new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
	      setlist.add(dataobj);
	    }
		this.getFormHM().put("setlist",setlist);
		String fields = hsb.getTextValue(hsb.POST_FIELDS);
		ArrayList itemlist=hsb.getSimpleFields(fields);
		this.getFormHM().put("itemlist",itemlist);
	}

}
