package com.hjsj.hrms.transaction.performance.totalrank;

import com.hjsj.hrms.businessobject.hire.ExecuteExcel;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:ExportFileTrans.java</p>
 * <p>Description:考核体系导出Excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-26 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */


public class ExportFileTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		String excelsql=(String)this.getFormHM().get("strsql");
		excelsql=excelsql!=null&&excelsql.trim().length()>0?excelsql:"";
		excelsql=SafeCode.decode(excelsql);

		String recTable = (String)this.getFormHM().get("fieldsetid");

		ArrayList fieldList = getFieldList(recTable);
		ExecuteExcel executeExcel = new ExecuteExcel(this.frameconn, this.getUserView(), recTable);
		String outName = executeExcel.createTabExcelHt(fieldList, excelsql, "3");
		outName = outName.replaceAll(".xls", "#");
		this.getFormHM().put("outName", PubFunc.encrypt(outName));
	}
	/**
	 * 求当前数据集的指标列表
	 * @param setname
	 * @return
	 */
	private ArrayList getFieldList(String setname){

		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn);
		ArrayList list = gzbo.fieldItemList(fieldset);
		ArrayList fieldlist=new ArrayList();
		int I9999 = 1;
		for(int i=0;i<list.size();i++){
			FieldItem field=(FieldItem)list.get(i);
			String itemid=field.getItemid();

			if("0".equals(this.userView.analyseFieldPriv(itemid)))
				continue;
			if("1".equals(this.userView.analyseFieldPriv(itemid)))
				field.setReadonly(true);
			if(!"2".equals(this.userView.analyseTablePriv(setname)))
				field.setReadonly(true);
			field.setSortable(true);
			fieldlist.add(field);
//			if(!fieldset.isMainset()){
//				if(itemid.equalsIgnoreCase("A0101")&&I9999>0){
//					FieldItem itemfield=DataDictionary.getFieldItem("i9999");
//					itemfield.setItemdesc(ResourceFactory.getProperty("recidx.label"));
//					itemfield.setItemtype("N");
//					itemfield.setDecimalwidth(0);
//					fieldlist.add(itemfield);
//				}
//			}
		}
		return fieldlist;
	}
}
