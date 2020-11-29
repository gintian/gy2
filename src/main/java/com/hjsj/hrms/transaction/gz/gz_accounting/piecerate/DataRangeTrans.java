package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class DataRangeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList setlist = new ArrayList();
		try{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String expression_str = PubFunc.keyWord_reback(SafeCode.decode((String)hm.get("strExpression")));
			ArrayList list=DataDictionary.getFieldList("s05",Constant.USED_FIELD_SET);
			CommonData vo1 = new CommonData("","");
			setlist.add(vo1);
			CommonData vo = new CommonData("S0102","S0102:计件作业类别");
			setlist.add(vo);
			String excludeStr=",Nbase,A0100,I9999,S0100,".toUpperCase();
			for (int i=0;i<list.size();i++) {
				FieldItem fielditem = (FieldItem) list.get(i);
				if ("0".equals(fielditem.getState())) continue;
				if (excludeStr.indexOf(","+fielditem.getItemid().toUpperCase()+",")>-1) {continue;}
				CommonData datavo = new CommonData(fielditem.getItemid().toUpperCase(),fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc());
				setlist.add(datavo);
			}
			this.getFormHM().put("setlist", setlist);
			this.getFormHM().put("formula", expression_str);
		}catch(Exception e)
		{
			e.printStackTrace();
		} 
	}

}
