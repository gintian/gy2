package com.hjsj.hrms.transaction.pos.posroleinfo;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:SearchDeptExplain.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 6, 2009:2:39:29 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SearchDeptExplain extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//String unit_id = this.userView.getUnit_id();
		//String[] unitids = unit_id.split("`");
		String manamgePrivCode = this.userView.getManagePrivCodeValue();
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String uid = (String)hm.get("a_code");
		/**哪个模块，L为廉政风险防范模块，Z为全员职位说明书*/
		String modular = (String) this.getFormHM().get("modular");
		//String codesetid = uid.substring(0,2);
		String id = "";
		if(uid.length()>2)
			id = uid.substring(2);
		ArrayList list = new ArrayList();
		String sql = "";
		//if(id==null||id.equals("0")||id.equals("")){
			sql = "select codesetid,codeitemid,parentid,b.i9999,b.title,b.createtime,b.ole,b.ext from organization left join ";
			if (modular != null && "L".equals(modular)) {
				sql += "(select * from (select * from k00 where flag = 'l' ) a  where a.i9999=(select max(b.i9999) from (select * from k00 ) b where a.e01a1=b.e01a1 and b.i9999='9998'  )) b ";
			} else {
				sql += "(select * from (select * from k00 where UPPER(flag) = 'K' ) a  where a.i9999=(select max(b.i9999) from (select * from k00 ) b where a.e01a1=b.e01a1  )) b ";
			}
			 
			sql += "on codeitemid = b.e01a1 " +
			"where codesetid='@K' and parentid like '"+id+"%' ";
			/*if(unit_id.length()>0)//原来是按操作单位的
				sql += " and parentid in (";
				for(int i=0;i<unitids.length;i++){
					String unitid = unitids[i];
					String ids = unitid.substring(2);
					sql += "'"+ids+"',";
				}
				sql = sql.substring(0,sql.length()-1);
				sql += ") ";*/
			if(manamgePrivCode.length()>0){
				/*if(manaagePriv.equalsIgnoreCase("@K"))
					sql += " and e01a1 like '"+manamgePrivCode+"%' ";
				if(manaagePriv.equalsIgnoreCase("UN"))
					sql += " and b0110 like '"+manamgePrivCode+"%' ";
				if(manaagePriv.equalsIgnoreCase("UM"))*/
					sql += " and parentid like '"+manamgePrivCode+"%' ";
			}
			sql += "order by codesetid,codeitemid";
		
			/*if(codesetid.equalsIgnoreCase("UN"))
				sql = "select codesetid,codeitemid,b.i9999,b.title,b.createtime,b.ole,b.ext from organization left join " +
				"(select * from (select * from k00 where flag = 'k' ) a " +
				"where a.i9999=(select max(b.i9999) from (select * from k00 ) b where a.e01a1=b.e01a1  )) b " +
				"on codeitemid = b.e01a1 " +
				"where codeitemid<>parentid  and parentid='"+id+"' " +
				"order by codesetid,codeitemid";
			if(codesetid.equalsIgnoreCase("UM"))
				sql = "select codesetid,codeitemid,b.i9999,b.title,b.createtime,b.ole,b.ext from organization left join " +
				"(select * from (select * from k00 where flag = 'k' ) a " +
				"where a.i9999=(select max(b.i9999) from (select * from k00 ) b where a.e01a1=b.e01a1  )) b " +
				"on codeitemid = b.e01a1 " +
				"where codeitemid<>parentid  and parentid='"+id+"' " +
				"order by codesetid,codeitemid";
			if(codesetid.equalsIgnoreCase("@K"))
				sql = "select codesetid,codeitemid,b.i9999,b.title,b.createtime,b.ole,b.ext from organization left join " +
					"(select * from (select * from k00 where flag = 'k' ) a " +
					"where a.i9999=(select max(b.i9999) from (select * from k00 ) b where a.e01a1=b.e01a1  )) b " +
					"on codeitemid = b.e01a1 " +
					"where codeitemid<>parentid  and parentid='"+id+"' " +
					"order by codesetid,codeitemid";
		}*/
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("codesetid",this.frowset.getString("codesetid"));
				bean.set("codeitemid",this.frowset.getString("codeitemid"));
				bean.set("parentid",this.frowset.getString("parentid"));
				bean.set("i9999",PubFunc.nullToStr(this.frowset.getString("i9999")));
				bean.set("title",PubFunc.nullToStr(this.frowset.getString("title")));
				bean.set("createtime",PubFunc.FormatDate(this.frowset.getDate("createtime"),"yyyy-MM-dd"));
				if(this.frowset.getString("ext")==null)
					bean.set("ext","0");
				else
					bean.set("ext",this.frowset.getString("ext"));
				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel",uplevel);
		this.getFormHM().put("rolelist",list);
		this.getFormHM().put("a_code",uid);
	}

}
