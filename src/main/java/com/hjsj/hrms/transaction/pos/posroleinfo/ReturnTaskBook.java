package com.hjsj.hrms.transaction.pos.posroleinfo;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:ReturnTaskBook.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 7, 2009:3:47:42 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class ReturnTaskBook extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String manaagePriv = this.userView.getManagePrivCode();
		String manamgePrivCode = this.userView.getManagePrivCodeValue();
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String dbname = (String)this.getFormHM().get("dbname");
		if(dbname==null|| "".equalsIgnoreCase(dbname))
			dbname = "usr";
		String taskyear = (String)this.getFormHM().get("taskyear");
		if(taskyear==null|| "".equalsIgnoreCase(taskyear))
			taskyear = "";
		String username = (String)this.getFormHM().get("username");
		if(username==null|| "".equalsIgnoreCase(username))
			username = "";
		String uid = (String)this.getFormHM().get("a_code");
		String codesetid = uid.substring(0,2);
		String id = uid.substring(2);
		ArrayList list = new ArrayList();
		ArrayList dbnamelist = new ArrayList();
		ArrayList yearlist = new ArrayList();
		String sql = "";
		if(id==null|| "".equalsIgnoreCase(id)){
			sql = "select "+dbname+"a01.a0100,a0101,b0110,e01a1,e0122,b.i9999,b.title,b.createtime,b.ole,b.ext from "+dbname+"a01 left join " +
			"(select * from (select * from "+dbname+"a00 where flag = 't' ) a " +
			"where a.i9999=(select max(b.i9999) from (select * from "+dbname+"a00 ) b where a.a0100=b.a0100  )) b " +
			"on "+dbname+"a01.a0100 = b.a0100 " +
			" where 1=1 ";
		}else{
			sql = "select "+dbname+"a01.a0100,a0101,b0110,e01a1,e0122,b.i9999,b.title,b.createtime,b.ole,b.ext from "+dbname+"a01 left join " +
			"(select * from (select * from "+dbname+"a00 where flag = 't' ) a " +
			"where a.i9999=(select max(b.i9999) from (select * from "+dbname+"a00 ) b where a.a0100=b.a0100  )) b " +
			"on "+dbname+"a01.a0100 = b.a0100 ";
			if("@K".equalsIgnoreCase(codesetid))
				sql += "where e01a1 like '"+id+"%' ";
			if("UN".equalsIgnoreCase(codesetid))
				sql += "where b0110 like '"+id+"%' ";
			if("UM".equalsIgnoreCase(codesetid))
				sql += "where e0122 like '"+id+"%' ";
		}
		if("@K".equalsIgnoreCase(manaagePriv))
			sql += " and e01a1 like '"+manamgePrivCode+"%' ";
		if("UN".equalsIgnoreCase(manaagePriv))
			sql += " and b0110 like '"+manamgePrivCode+"%' ";
		if("UM".equalsIgnoreCase(manaagePriv))
			sql += " and e0122 like '"+manamgePrivCode+"%' ";
		
		if(!"".equalsIgnoreCase(username))
			sql += " and "+dbname+"a01.a0101 like '"+username+"%' ";
		if(!"".equalsIgnoreCase(taskyear))
			sql += " and b.createtime >= '"+taskyear+"' and b.createtime <='"+(Integer.parseInt(taskyear)+1)+"'";
		sql += " order by "+dbname+"a01.a0100,b.createtime";
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("a0100",this.frowset.getString("a0100"));
				bean.set("a0101",this.frowset.getString("a0101"));
				bean.set("b0110",this.frowset.getString("b0110"));
				bean.set("e01a1",this.frowset.getString("e01a1"));
				bean.set("e0122",this.frowset.getString("e0122"));
				bean.set("i9999",PubFunc.nullToStr(this.frowset.getString("i9999")));
				bean.set("title",PubFunc.nullToStr(this.frowset.getString("title")));
				bean.set("createtime",PubFunc.FormatDate(this.frowset.getDate("createtime"),"yy-MM-dd"));
				if(this.frowset.getString("ext")==null)
					bean.set("ext","0");
				else
					bean.set("ext",this.frowset.getString("ext"));
				list.add(bean);
			}
			String dbsql = "select * from dbname ";
			this.frowset = dao.search(dbsql);
			while(this.frowset.next()){
				CommonData data = new CommonData(this.frowset.getString("pre"),this.frowset.getString("dbname"));
				dbnamelist.add(data);
			}
			String yearsql = "select createtime from "+dbname+"a00 where flag = 't' ";
			this.frowset = dao.search(yearsql);
			ArrayList tlist = new ArrayList();
			yearlist.add(new CommonData("",""));
			while(this.frowset.next()){
				if(tlist.indexOf(PubFunc.FormatDate(this.frowset.getDate("createtime"),"yyyy"))!=-1)
					continue;
				tlist.add(PubFunc.FormatDate(this.frowset.getDate("createtime"),"yyyy"));
				CommonData data = new CommonData(PubFunc.FormatDate(this.frowset.getDate("createtime"),"yyyy"),PubFunc.FormatDate(this.frowset.getDate("createtime"),"yyyy")+"年");
				yearlist.add(data);
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
		this.getFormHM().put("dbname",dbname);
		this.getFormHM().put("dbnamelist",dbnamelist);
		this.getFormHM().put("yearlist",yearlist);
		this.getFormHM().put("taskyear","");
		this.getFormHM().put("username","");
	}

}
