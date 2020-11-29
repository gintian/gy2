package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceRateFormulaBo;
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

public class CountFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		String formulaid = "";
		String itemid = "";
		ArrayList itemlist = new ArrayList();
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String busiId = (String) reqhm.get("busiid");
		String curFormulaid = (String)reqhm.get("formulaid");
		curFormulaid=curFormulaid!=null&&curFormulaid.length()>0?curFormulaid:"";
		reqhm.remove("formulaid");
		reqhm.remove("busiid");

		hm.put("sql", "select formulaid,moduleCode,busiId,itemid,useflag,itemname,runflag");
		hm.put("where"," from hr_formula where modulecode='SAL_JJ' and  busiId='"+busiId+"'");
		hm.put("column","formulaid,itemid,useflag,itemname,runflag");
		hm.put("orderby"," order by sortid,itemid desc");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try 
		 {			
			PieceRateFormulaBo formulaBo=new PieceRateFormulaBo(this.getFrameconn(),curFormulaid,this.userView);
			formulaid = curFormulaid;
			itemid = formulaBo.getItemId();
			if ("".equals(itemid))
			{
				formulaid=formulaBo.GetFirstFormula(dao, busiId);				
				itemid = formulaBo.getItemId();
			}
			CommonData datavo = new CommonData("","");
			itemlist.add(datavo);
			ArrayList list=DataDictionary.getFieldList("s05",Constant.USED_FIELD_SET);
			String excludeStr=",Nbase,A0100,I9999,".toUpperCase();
			for (int i=0;i<list.size();i++) {
				FieldItem fielditem = (FieldItem) list.get(i);
				if ("0".equals(fielditem.getState())) continue;
				if (excludeStr.indexOf(","+fielditem.getItemid().toUpperCase()+",")>-1) {continue;}
			    datavo = new CommonData(fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc(),fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc());
				itemlist.add(datavo);
			}
			hm.put("formulaid",formulaid);
			hm.put("itemid",itemid);
			hm.put("busiid",busiId);
			hm.put("formula","");	
			hm.put("itemlist",itemlist);	
			
		 }catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		 }

	}
}
