package com.hjsj.hrms.transaction.gz.premium.param;

import com.hjsj.hrms.businessobject.gz.premium.FormulaPremiumBo;
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
		
		String setid= (String)hm.get("setid");
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		FormulaPremiumBo formulaPremiumBo = new FormulaPremiumBo();
		String[] itemsort = formulaPremiumBo.itemSortid(dao,setid);
		FieldItem fielditem = null;
		
		StringBuffer strsql = new StringBuffer();
		//IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	//	int  itemid=Integer.parseInt(idg.getId(("bonusformula.itemid").toUpperCase()));
		strsql.append("insert into  bonusformula(setid,itemid,sortid,");
		strsql.append("hzname,itemname,itemtype,useflag,fmode) values(");
		strsql.append("'"+setid+"',");
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
		strsql.append("1,");
		strsql.append(this.getFormHM().get("fmode")+")");
	//	System.out.println(strsql.toString());
		try {
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
