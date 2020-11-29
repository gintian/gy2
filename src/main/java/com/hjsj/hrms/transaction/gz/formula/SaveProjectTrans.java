package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.FormulaBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class SaveProjectTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		
		String base = "no";
		
		String formulaitemid= (String)hm.get("formulaitemid");
		formulaitemid=formulaitemid!=null&&formulaitemid.trim().length()>0?formulaitemid:"";
		String[] arr = formulaitemid.split(":");
		
		String salaryid= (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		String fieldsetid = (String) hm.get("fieldsetid");
		ContentDAO dao = new ContentDAO(this.frameconn);
		FormulaBo formulabo = new FormulaBo();
		String[] itemsort = formulabo.itemSortid(dao,salaryid);
		FieldItem fielditem = null;
		
		StringBuffer strsql = new StringBuffer();
		if("-2".equals(salaryid)){
			strsql.append("insert into  salaryformula(salaryid,itemid,sortid,");
			strsql.append("hzname,itemname,itemtype,runflag,useflag,cstate) values(");
			strsql.append(salaryid+",");
		}else{
			strsql.append("insert into  salaryformula(salaryid,itemid,sortid,");
			strsql.append("hzname,itemname,itemtype,runflag,useflag) values(");
			strsql.append(salaryid+",");
		}

		if(itemsort.length==2){
			strsql.append(itemsort[0]+","+itemsort[1]+",");
		}else{
			strsql.append("0,0,");
		}
		if(arr.length==2){
			strsql.append("'"+arr[1]+"','"+arr[0]+"',");
			fielditem = DataDictionary.getFieldItem(arr[0]);
		}else{
			strsql.append("0,0,");
		}
		if(fielditem!=null){
			strsql.append("'"+fielditem.getItemtype()+"',");
		}else{
			strsql.append("'N',");
		}
		
		if("-2".equals(salaryid)){
			strsql.append("0,1,'"+fieldsetid+"')");
		}else{
			strsql.append("0,1)");
		}
		try {
			if(!"-2".equals(salaryid)){
				StringBuffer context = new StringBuffer();
				SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
				String name = bo.getSalaryName(salaryid);
				context.append("新增："+name+"（"+salaryid+"）新增计算公式（"+arr[1]+"）<br>");

				this.getFormHM().put("@eventlog", context.toString());
			}

			dao.update(strsql.toString());
			base = itemsort[0];
		} catch(SQLException e) {
			// TODO Auto-generated catch block
			base = "no";
			e.printStackTrace();
		}
		hm.put("base",base);
	}

}
