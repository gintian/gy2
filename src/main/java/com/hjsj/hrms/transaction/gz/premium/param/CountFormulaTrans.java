package com.hjsj.hrms.transaction.gz.premium.param;

import com.hjsj.hrms.businessobject.gz.FormulaBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
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
public class CountFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");

		String setid = (String)reqhm.get("setid");
		setid=setid!=null&&setid.length()>0?setid:"";
		reqhm.remove("setid");
		
		String itemid = (String)reqhm.get("itemid");
		itemid=itemid!=null&&itemid.length()>0?itemid:"";
		reqhm.remove("itemid");
		
		hm.put("sql", "select itemid,useflag,hzname,itemname,runflag");
		hm.put("where"," from bonusformula where setid="+setid);
		hm.put("column","itemid,useflag,hzname,itemname,runflag");
		hm.put("orderby"," order by sortid,itemid desc");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String item = "";
		String itemname = "";
		
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select itemid,itemname from bonusformula where setid=");
			buf.append("'"+setid+"'");
			if(itemid.trim().length()>0){
				buf.append(" and itemid=");
				buf.append(itemid);
			}
			buf.append(" order by sortid");
			RowSet rs = dao.search(buf.toString());
			if(rs.next()){
				item = rs.getInt("itemid")+"";
				itemname = rs.getString("itemname");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hm.put("item",item);
		hm.put("itemname",itemname);
		hm.put("setid",setid);
		hm.put("formula","");
		
		FormulaBo formulsbo = new FormulaBo();
		
		hm.put("itemid",itemid);
		hm.put("itemlist",formulsbo.subStandardList(this.frameconn,setid));
	}
}
