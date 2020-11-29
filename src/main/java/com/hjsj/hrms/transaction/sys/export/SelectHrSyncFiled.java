package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
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
 *<p>Title:SelectHrSyncFiled.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 3, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SelectHrSyncFiled extends IBusiness {

	public void execute() throws GeneralException {
		
		HrSyncBo hsb = new HrSyncBo(this.frameconn);	
		String type = (String)this.getFormHM().get("type");
		if("init".equalsIgnoreCase(type))
		{
			ArrayList fieldsetlist = new ArrayList();
			fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.USED_FIELD_SET);
		      /*ArrayList  fieldunitlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
		      ArrayList  fieldposlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);	
		      for(int i=0;i<fieldunitlist.size();i++)
		      {
		    	  fieldsetlist.add(fieldunitlist.get(i));
		      }
		      for(int i=0;i<fieldposlist.size();i++)
		      {
		    	  fieldsetlist.add(fieldposlist.get(i));
		      }*/
			ArrayList setlist = new ArrayList();
			for(int i=0;i<fieldsetlist.size();i++)
		    {
		      FieldSet fieldset=(FieldSet)fieldsetlist.get(i);
		      if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
		        continue;
		      if("A00".equals(fieldset.getFieldsetid())|| "B01".equals(fieldset.getFieldsetid())|| "B00".equals(fieldset.getFieldsetid()))
		    	  continue;		      
		      CommonData dataobj = new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
		      setlist.add(dataobj);
		    }
			this.getFormHM().put("setlist",setlist);
			String fields = hsb.getTextValue(hsb.FIELDS);
			/*if(fields.indexOf("a0101")==-1)
			    fields+=",a0101";*/
			ArrayList itemlist=hsb.getSimpleFields(fields);
			this.getFormHM().put("itemlist",itemlist);
		}
		else if("change".equalsIgnoreCase(type))
		{
			ArrayList list=new ArrayList();
			String setname=(String)this.getFormHM().get("tablename");
			ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		    if(fielditemlist!=null)
		    {
				for(int i=0;i<fielditemlist.size();i++)
			    {
			      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			      if("M".equals(fielditem.getItemtype()))
			    	continue;
			      if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
			        continue;
			      
			      
			      CommonData dataobj = new CommonData();
			      dataobj = new CommonData(fielditem.getItemid(), /*"(" + fielditem.getItemid()+ ")"+*/ fielditem.getItemdesc());
			      
			      list.add(dataobj);
			    }
		    }
		    this.getFormHM().clear();
		    this.getFormHM().put("fieldlist",list);
		}
		
	}

}
