package com.hjsj.hrms.transaction.gz.formula;

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
public class ViewFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");

		String salaryid = (String)reqhm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.length()>0?salaryid:"";
		reqhm.remove("salaryid");
		
		String itemid = (String)reqhm.get("itemid");
		itemid=itemid!=null&&itemid.length()>0?itemid:"";
		reqhm.remove("itemid");
		String fieldsetid = (String)reqhm.get("state");
		fieldsetid=fieldsetid!=null&&fieldsetid.length()>0?fieldsetid:"";
		
		hm.put("sql", "select s.itemid,s.useflag,s.hzname,s.itemname,s.runflag,s.itemtype,f.codesetid");
		if("-2".equals(salaryid)){
			hm.put("where"," from salaryformula s,fielditem f where upper(s.itemname) = upper(f.itemid) and s.salaryid = "+salaryid+"  and (s.cstate='"+fieldsetid+"' )");
		}else{
			hm.put("where"," from salaryformula s,fielditem f where upper(s.itemname) = upper(f.itemid) and s.salaryid = "+salaryid);
		}
		
		hm.put("column","itemid,useflag,hzname,itemname,runflag,itemtype,codesetid");
		hm.put("orderby"," order by s.sortid,s.itemid desc");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String item = "";
		String itemname = "";
		String itemtype = "";
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select itemid,itemname,itemtype from salaryformula where salaryid=");
			buf.append(salaryid);
			if(itemid.trim().length()>0){
				buf.append(" and upper(itemid)=");
				buf.append(itemid.toUpperCase());
			}
			if("-2".equals(salaryid)){
				buf.append(" and cstate='"+fieldsetid+"' ");
			}
			buf.append(" order by sortid");
			RowSet rs = dao.search(buf.toString());
			if(rs.next()){
				item = rs.getInt("itemid")+"";
				itemname = rs.getString("itemname");
				itemtype = rs.getString("itemtype");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hm.put("item",item);
		hm.put("itemname",itemname);
		hm.put("itemtype",itemtype);
		hm.put("salaryid",salaryid);
		hm.put("formula","");
		this.getFormHM().put("fieldsetid", fieldsetid);
		FormulaBo formulsbo = new FormulaBo();
		formulsbo.setUserview(this.userView);
		hm.put("itemid",itemid);
		if("-2".equals(salaryid)){
			hm.put("itemlist",formulsbo.subStandardList(this.frameconn,salaryid,fieldsetid));
		}
		else{
			hm.put("itemlist",formulsbo.subStandardList(this.frameconn,salaryid));
		}
		
	}
}
