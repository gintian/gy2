package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPieceRateFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String busiid = (String)reqhm.get("busiid");
		busiid=busiid!=null&&busiid.trim().length()>0?busiid:"";	
		reqhm.remove("busiid");
		if ("".equals(busiid)) return;
		ContentDAO dao = new ContentDAO(this.frameconn);		
		ArrayList itemlist = new ArrayList();	
		try
		{	
			String excludeStr="";
			ArrayList dylist = null;
/*			String sqlstr="select itemid from hr_formula where modulecode ='SAL_JJ' and  busiid ='"+busiid+"'"+" order by sortid,itemid desc";
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				if(dynabean.get("itemid")!=null&&dynabean.get("itemid").toString().trim().length()>0){
					excludeStr =excludeStr+","+dynabean.get("itemid").toString().toUpperCase()+"";
				}
			}*/
			excludeStr=excludeStr+",Nbase,A0100,I9999,S0100,B0110,E0122,E01A1,A0101,".toUpperCase();
			
			ArrayList list=DataDictionary.getFieldList("s05",Constant.USED_FIELD_SET);			
			for (int i=0;i<list.size();i++) {
				FieldItem fielditem = (FieldItem) list.get(i);
				if ("0".equals(fielditem.getState())) continue;
				if (excludeStr.indexOf(","+fielditem.getItemid().toUpperCase()+",")>-1) {continue;}
				CommonData datavo = new CommonData(fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc(),
						                        fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc());
				itemlist.add(datavo);

		      } 
		}	
		catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}	
	
		hm.put("busiid",busiid);
		hm.put("formulaitemid","");
		hm.put("formulaitemlist",itemlist);
	}


}
