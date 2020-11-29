package com.hjsj.hrms.transaction.general.template.goabroad.collect;

import com.hjsj.hrms.businessobject.general.template.collect.CollectStat;
import com.hjsj.hrms.businessobject.general.template.collect.EduceExcel;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Iterator;

public class EduceExcelTrans extends IBusiness {
   
    
	public void execute() throws GeneralException 
	{
	    //zxj 20161018  条件从userview中取，避免传到页面泄漏数据库结构
		String where = (String)this.userView.getHm().get("goboard_where"); 		
		String order = (String)this.userView.getHm().get("goboard_orderby");		
		order = order == null ? "" : order;
		String subset=(String)this.userView.getHm().get("goboard_subset");
		
		String nbase=(String)this.getFormHM().get("nbase");
		
		CollectStat collectStat=new CollectStat(this.getFrameconn());
		ArrayList columnlist= DataDictionary.getFieldList(subset,Constant.USED_FIELD_SET);
		String Privtable = this.userView.analyseTablePriv(subset);
		if(!"0".equals(Privtable)){
			for(Iterator it = columnlist.iterator();it.hasNext();){
				FieldItem fielditem = (FieldItem)it.next();
				String itemid = fielditem.getItemid();
				String privField = this.userView.analyseFieldPriv(itemid);
				if("0".equals(privField)){
					it.remove();
				}
			}
		}else{
			columnlist = new ArrayList();
		}
		columnlist=collectStat.getColumnlist(columnlist);
//		if(columns==null||columns.length()<=0)
		String columns = collectStat.getSQLFieldString(columnlist,nbase);
		
		if(where==null||where.length()<=0)
		{
			where=getWhereStr(subset);
		}
		EduceExcel educeExcel = new EduceExcel(this.getFrameconn());
		educeExcel.setUserView(userView);
		String excelfile = educeExcel.creatExcel(columns,where,nbase,subset,columnlist,order);
		excelfile = PubFunc.encryption(excelfile);
		this.getFormHM().put("excelfile",excelfile);
	}
    public String getWhereStr(String subset)
    {
    	String nbase=(String)this.getFormHM().get("nbase");
		String code = (String) this.getFormHM().get("code");
		String kind = (String) this.getFormHM().get("kind"); 
		String where="from "+nbase+subset+","+nbase+"A01";	
		CollectStat collectStat=new CollectStat(this.getFrameconn());
		where =where+collectStat.getWhere(code,kind,nbase,this.userView,subset);
		return where;
    }
}
