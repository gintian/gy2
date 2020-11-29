package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
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
public class FormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		ArrayList itemList = new ArrayList();
		
		String fielditemid = (String)reqhm.get("itemid");
		
		String flag = (String)reqhm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"0";
		reqhm.remove("flag");

	
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		GrossManagBo gmb = new GrossManagBo(this.getFrameconn());
		ArrayList salarysetlist = gmb.getSalarySetList("0", this.userView);
		String salaryid="-1";
		if(salarysetlist!=null&&salarysetlist.size()>0)
			salaryid=((CommonData)salarysetlist.get(0)).getDataValue();
		String formular = "";
		if("1".equals(flag)){
			FieldItem fielditem = DataDictionary.getFieldItem(fielditemid);
			CommonData dataobj =  new CommonData("","");
			itemList.add(dataobj);
			ArrayList fieldlist = this.userView.getPrivFieldList(fielditem.getFieldsetid(), Constant.UNIT_FIELD_SET);
			for(int i=0;i<fieldlist.size();i++){
				FieldItem field = (FieldItem)fieldlist.get(i);
				if(field!=null){
					dataobj =  new CommonData(field.getItemdesc(),field.getItemdesc());
					itemList.add(dataobj);
				}
			}
			formular = (String)reqhm.get("formula");
			formular=formular!=null&&formular.trim().length()>0?formular:"";
			reqhm.remove("formula");
			formular=SafeCode.decode(formular);
			this.getFormHM().put("type", "0");
		}else{
			String itemsql = "select distinct itemid,itemdesc,sortid from salaryset where salaryid="+salaryid+" order by sortid";
			CommonData top = new CommonData();
			top = new CommonData("","");
			itemList.add(top);
			try {
				this.frowset = dao.search(itemsql);
				while(this.frowset.next()){
					//String itemid = this.frowset.getString("itemid");
					String itemdesc = this.frowset.getString("itemdesc");
					CommonData dataobj = new CommonData();
					//dataobj = new CommonData(itemid,itemdesc);
					dataobj = new CommonData(itemdesc,itemdesc);
					itemList.add(dataobj);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			/*GrossPayManagement gross = new GrossPayManagement(this.frameconn,"GZ_PARAM");
			String formulaaa = gross.getFormula(fielditemid);*/
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),2);
			formular = bo.getFormula(fielditemid);
			this.getFormHM().put("type", "1");
		}
		
		this.getFormHM().put("checkflag", flag);
		
		this.getFormHM().put("salaryid",salaryid);
		this.getFormHM().put("salarysetlist", salarysetlist);
		this.getFormHM().put("fieldItems","");
		this.getFormHM().put("itemlist",itemList);
		this.getFormHM().put("formula",formular);
	}
}
