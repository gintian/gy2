package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ListPerKnowTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String returnflag=(String)hm.get("returnflag");
		this.getFormHM().put("returnflag",returnflag);
		try
		{
	
			ArrayList setlist=this.searchCheckBodyObjectList();
			this.getFormHM().put("setlist", setlist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	public ArrayList searchCheckBodyObjectList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		try
		{
			
			buf.append(" select know_id,name,status,seq from per_know ");
			buf.append(" order by  seq ");
			ContentDAO dao=new ContentDAO(this.frameconn);
//			RowSet rset=dao.search(buf.toString());
//			while(rset.next())
//			{
//				LazyDynaBean lazyvo=new LazyDynaBean();
//				lazyvo.set("knowId", rset.getString("know_id"));
//				lazyvo.set("name", rset.getString("name"));
//				lazyvo.set("status", rset.getString("status"));
//				lazyvo.set("seq", rset.getString("seq"));
//				list.add(lazyvo);
//			}
			this.frowset = dao.search(buf.toString());
			while (this.frowset.next()) {
				RecordVo vo = new RecordVo("per_know");
				vo.setString("know_id", this.frowset.getString("know_id"));
				vo.setString("name", this.frowset.getString("name"));
				vo.setString("seq", this.frowset.getString("seq"));
				vo.setString("status", this.frowset.getString("status"));			
				list.add(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	
}
