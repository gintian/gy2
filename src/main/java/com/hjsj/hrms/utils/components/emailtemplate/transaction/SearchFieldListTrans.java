package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;



/**
 * <p>Title:SearchFieldListTrans</p>
 * <p>Description:插入指标后，根据itemid查找fielditem中的一些字段，以便插入到email_field中</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 3, 2015 2:06:15 PM</p>
 * @author sunming
 * @version 1.0
 */
public class SearchFieldListTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
	try {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String itemid = (String)this.getFormHM().get("fieldcontent");
		String itemdesc = (String) this.getFormHM().get("fieldtitle");
		String fieldsetid = (String) this.getFormHM().get("fieldsetid");
		String itemtype="A";
		int itemlength = 0;
		int decimalwidth = 0;
		String codesetid = "0";
		if(!"undefined".equals(fieldsetid)){
			FieldItem fieldItem = DataDictionary.getFieldItem(itemid, fieldsetid);
			itemtype = fieldItem.getItemtype();
			itemlength = fieldItem.getItemlength();
			decimalwidth = fieldItem.getDecimalwidth();
			codesetid = fieldItem.getCodesetid();
		}else{
			itemlength = 200;
			fieldsetid = "sys";
		}
		
//		ContentDAO dao = new ContentDAO(this.getFrameconn());
//		String sql="";
//		if(fieldsetid.substring(0,1).equals("Z")){
//			sql = "select itemtype,itemlength,decimalwidth,codesetid,fieldsetid from t_hr_busifield where itemid='"+itemid+"' and fieldsetid='"+fieldsetid+"'";
//		}else{
//			sql = "select itemtype,itemlength,decimalwidth,codesetid,fieldsetid from fielditem where itemid='"+itemid+"' and fieldsetid='"+fieldsetid+"'";
//		}
//			this.frowset = dao.search(sql);
//			while(this.frowset.next()){
//				itemtype = this.frowset.getString("itemtype");
//				itemlength = this.frowset.getString("itemlength");
//				decimalwidth = this.frowset.getString("decimalwidth");
//				codesetid = this.frowset.getString("codesetid");
//				fieldsetid = this.frowset.getString("fieldsetid");
//			}
			this.getFormHM().put("fieldtype",itemtype);
			this.getFormHM().put("fieldlen",itemlength+"");
			this.getFormHM().put("ndec",decimalwidth+"");
			this.getFormHM().put("codeset",codesetid);
			this.getFormHM().put("fieldset",fieldsetid);
			this.getFormHM().put("fieldcontent",itemid);
			this.getFormHM().put("fieldtitle",itemdesc);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
