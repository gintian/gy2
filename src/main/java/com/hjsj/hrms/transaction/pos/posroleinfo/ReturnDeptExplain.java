package com.hjsj.hrms.transaction.pos.posroleinfo;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:ReturnDeptExplain.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 6, 2009:2:38:39 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class ReturnDeptExplain extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String manamgePrivCode = this.userView.getManagePrivCodeValue();
		/**哪个模块，L为廉政风险防范模块，Z为全员职位说明书*/
		String modular = (String) this.getFormHM().get("modular");
		String uid = (String)this.getFormHM().get("a_code");
		String id = "";
		if(uid.length()>2)
			id = uid.substring(2);
		ArrayList list = new ArrayList();
		/*String sql = "select codesetid,codeitemid,b.title,b.createtime,b.ole,b.ext from organization left join " +
				"(select * from (select * from k00 where flag = 'k' ) a " +
				"where a.i9999=(select max(b.i9999) from (select * from k00 ) b where a.e01a1=b.e01a1  )) b " +
				"on codeitemid = b.e01a1 " +
				"where codeitemid like '"+id+"%' " +
				"order by codesetid,codeitemid";*/
		String sql = "";
		sql = "select codesetid,codeitemid,parentid,b.i9999,b.title,b.createtime,b.ole,b.ext from organization left join ";
		if (modular != null && "L".equals(modular)) {
			sql += "(select * from (select * from k00 where flag = 'l' ) a  where a.i9999=(select max(b.i9999) from (select * from k00 ) b where a.e01a1=b.e01a1 and b.i9999='9998'  )) b ";
		} else {
			sql += "(select * from (select * from k00 where UPPER(flag) = 'K' ) a  where a.i9999=(select max(b.i9999) from (select * from k00 ) b where a.e01a1=b.e01a1  )) b ";
		}
		sql += "on codeitemid = b.e01a1 " +
		"where codesetid='@K' and parentid like '"+id+"%' " ;
		if(manamgePrivCode.length()>0)
			sql += " and parentid like '"+manamgePrivCode+"%' ";
		sql += " order by codesetid,codeitemid";
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
		this.getFormHM().put("rolelist",list);
	}

}
