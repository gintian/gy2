package com.hjsj.hrms.transaction.kq.options.adjustcode;

import com.hjsj.hrms.businessobject.kq.set.AdjustCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Iterator;

public class OrderCodeTrans extends IBusiness{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void execute()throws GeneralException
    {
    	String table=(String)this.getFormHM().get("table");
    	String flag=(String)this.getFormHM().get("flag");
    	String isSave=(String)this.getFormHM().get("isSave");
    	if(flag==null||flag.length()<=0)
             flag="";
    	this.getFormHM().put("flag", flag);
    	this.getFormHM().put("isSave", isSave);
    	ArrayList field_list=new ArrayList(); 
    	ArrayList fielditemlist = new ArrayList();
    	fielditemlist = new AdjustCode().getFieldByOrder(table);
    	
    	for(Iterator it = fielditemlist.iterator();it.hasNext();){
    		FieldItem field = (FieldItem)it.next();
    		String desc= field.getItemdesc();
			String itemid= field.getItemid();
			String state= field.getState();
			//更改李群提出：调整指标顺序，去掉人员编号指标
			if(state!=null&& "1".equals(state))
				desc=desc+"(显示)";
			else
				desc=desc+"(隐藏)";
			CommonData dataobj = new CommonData();
			dataobj.setDataValue(itemid);
    		dataobj.setDataName(desc);
    		field_list.add(dataobj);
    	}
    	this.getFormHM().put("field_list",field_list);
    	this.getFormHM().put("tablemess",getTablemess(table));
    	this.getFormHM().put("table",table);
	}
    private String getTablemess(String table)
    {
    	String tablemess="";
    	if("q03".equalsIgnoreCase(table))
    		tablemess="考勤数据表";
    	else if("q11".equalsIgnoreCase(table))
    		tablemess="加班申请表";
    	else if("q13".equalsIgnoreCase(table))
    		tablemess="公出申请表";
    	else if("q15".equalsIgnoreCase(table))
    		tablemess="请假申请表";
    	return tablemess;
    }
}
