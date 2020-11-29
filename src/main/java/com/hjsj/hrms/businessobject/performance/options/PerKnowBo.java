 package com.hjsj.hrms.businessobject.performance.options;

 import com.hrms.frame.dao.ContentDAO;
 import com.hrms.struts.exception.GeneralException;
 import com.hrms.struts.exception.GeneralExceptionHandler;
 import com.hrms.struts.taglib.CommonData;
 import org.apache.commons.beanutils.DynaBean;
 import org.apache.commons.beanutils.LazyDynaBean;

 import javax.sql.RowSet;
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.Iterator;


public class PerKnowBo {

	private String knowId;
	private String name;
	private String status;
	private String Seq;
	
	private Connection conn=null;
	
	public PerKnowBo(Connection conn) {
		this.conn=conn;
	}

	



	public String getKnowId() {
		return knowId;
	}





	public void setKnowId(String knowId) {
		this.knowId = knowId;
	}





	public String getName() {
		return name;
	}





	public void setName(String name) {
		this.name = name;
	}





	public String getStatus() {
		return status;
	}





	public void setStatus(String status) {
		this.status = status;
	}





	public String getSeq() {
		return Seq;
	}





	public void setSeq(String seq) {
		Seq = seq;
	}





	public ArrayList searchCheckBodyObjectList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		try
		{
			
			buf.append(" select know_id,name,status,seq from per_know ");
			buf.append(" order by  seq ");
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				LazyDynaBean lazyvo=new LazyDynaBean();
				lazyvo.set("knowId", rset.getString("know_id"));
				lazyvo.set("name", rset.getString("name"));
				lazyvo.set("status", rset.getString("status"));
				lazyvo.set("seq", rset.getString("seq"));
				list.add(lazyvo);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
	public ArrayList sortList(Connection conn){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "";
		sqlstr = "select know_id,name from per_know  order by seq ";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData dataobj = new CommonData(dynabean.get("know_id").toString(),
						dynabean.get("name").toString());
				list.add(dataobj);
			}
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	
}
