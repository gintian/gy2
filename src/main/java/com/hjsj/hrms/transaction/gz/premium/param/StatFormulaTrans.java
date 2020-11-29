package com.hjsj.hrms.transaction.gz.premium.param;

import com.hjsj.hrms.businessobject.gz.premium.FormulaPremiumBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
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
public class StatFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String fmode  = (String)reqhm.get("fmode");
		this.getFormHM().put("fmode", fmode);
		String temp  =(String) reqhm.get("b_stat");
		reqhm.remove("b_stat");
		if(temp!=null&& "link".equals(temp))
		this.getFormHM().put("tag", "0");
		else if (temp!=null&& "add".equals(temp))
			this.getFormHM().put("tag", "1");
		else
			this.getFormHM().put("tag", "0");
		ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
		 String setid = xml.getNodeAttributeValue("/Params/BONUS_SET","setid");
	 	   // this.getFormHM().put("dist_field", setid);
		//String setid = (String)reqhm.get("setid");
		 setid=setid!=null&&setid.length()>0?setid:"";
		 if("".equals(setid)){
				throw new GeneralException("请设置奖金分配子集并保存设置的参数!");	
		 }
		//reqhm.remove("setid");
		
		String itemid = (String)reqhm.get("itemid");
		itemid=itemid!=null&&itemid.length()>0?itemid:"";
		reqhm.remove("itemid");
		
		hm.put("sql", "select itemid,useflag,hzname,itemname,smode");
		hm.put("where"," from bonusformula where setid='"+setid+"' and fmode="+fmode+" and itemtype!='A'");
		hm.put("column","itemid,useflag,hzname,itemname,smode");
		hm.put("orderby"," order by sortid,itemid desc");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String item = "";
		String itemname = "";
		String smode = "";
		String hzname ="";
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select itemid,itemname,smode,hzname from bonusformula where setid=");
			buf.append("'"+setid+"'");
			buf.append(" and fmode="+fmode);
			buf.append(" and itemtype!='A'");
			if(itemid.trim().length()>0){
				buf.append(" and itemid=");
				buf.append(itemid);
			}
			buf.append(" order by sortid");
			RowSet rs = dao.search(buf.toString());
			if(rs.next()){
				item = rs.getInt("itemid")+"";
				itemname = rs.getString("itemname");
				smode =rs.getInt("smode")+"";
				hzname = rs.getString("hzname");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hm.put("item",item);
		hm.put("itemname",itemname);
		hm.put("setid",setid);
		hm.put("formula","");
		hm.put("smode", smode);
		hm.put("hzname", hzname);
		FormulaPremiumBo formulaPremiumBo = new FormulaPremiumBo();

		hm.put("itemid",itemid);
		hm.put("itemlist",formulaPremiumBo.subStandardList(this.frameconn,setid));
		hm.put("stat_methods","0");
		hm.put("statlist",formulaPremiumBo.statList());
	}
}
