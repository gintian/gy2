package com.hjsj.hrms.businessobject.kq.options.formula;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class KqFormulaBo {

	private Connection conn=null;
	public KqFormulaBo(Connection conn) {
		super();
		this.conn=conn;
	}
	
	public ArrayList getKqFormulaList()
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select chkid,name,validflag from hrpchkformula where flag=5 order by chkid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("chkid",rs.getString("chkid"));
				bean.set("name",rs.getString("name"));
				bean.set("validflag", rs.getString("validflag"));
				//bean.set("formula", )
				list.add(bean);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public LazyDynaBean getFormulaInfo(String chkid)
	{
		LazyDynaBean bean = new LazyDynaBean();
		try
		{
			String sql = "select name,information from hrpchkformula where chkid="+chkid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				bean.set("name", rs.getString("name"));
				bean.set("information",Sql_switcher.readMemo(rs,"information"));
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return bean;
	}
	
	public String getKqFormula(String chkid)
	{
		String formula="";
		try
		{
			String sql = "select formula from hrpchkformula where chkid="+chkid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				formula=Sql_switcher.readMemo(rs, "formula");
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return formula;
	}
}
