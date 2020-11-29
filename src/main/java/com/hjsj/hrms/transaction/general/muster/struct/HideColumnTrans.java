/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster.struct;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-21:10:48:22</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class HideColumnTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String field_name=(String)hm.get("field_name");
		field_name=field_name!=null?field_name:"";
		
		String coumsize=(String)hm.get("coumsize");
		coumsize=coumsize!=null?coumsize:"";
		
		int m=Integer.parseInt(coumsize)-1;
		
		ArrayList list=(ArrayList)this.getFormHM().get("fieldlist");
		try
		{
			FieldItem fielditem = DataDictionary.getFieldItem(field_name);
			if(fielditem!=null){
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String fieldvalue = fielditem.getFieldsetid()+"."+fielditem.getItemid();
				dao.update("update lbase set Width=0 where Field_name='"+fieldvalue.toUpperCase()+"'");
				for(int i=0;i<list.size();i++){
					Field item=(Field)list.get(i);
					if(item.getName().equals(field_name)){
						item.setVisible(false);
						this.getFormHM().put("coumsize",m+"");
						break;
					}
					
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		this.getFormHM().put("fieldlist",list);
	}

}
