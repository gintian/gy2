package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.businessobject.general.info.BatchHandBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GetReferenceTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
//		 TODO Auto-generated method stub
		HashMap hm=this.getFormHM();

		String itemid = (String)hm.get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
		FieldItem fielditem = DataDictionary.getFieldItem(itemid);
		String codesetid ="";
		String itemtype = "A";
		int decimalwidth=10;
		if(fielditem!=null){
			if(fielditem.isCode()){
				codesetid = fielditem.getCodesetid();
			}
			itemtype=fielditem.getItemtype();
			decimalwidth = fielditem.getDecimalwidth();
		}
		
		BatchHandBo batch = new BatchHandBo(this.userView);

		ArrayList list = batch.refList(itemid);
		hm.put("refvaluelist",list);
		hm.put("codesetid",codesetid);
		hm.put("itemtype",itemtype);
		hm.put("decimalwidth",String.valueOf(decimalwidth));
	}

}
