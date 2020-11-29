package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:ListPerParamTrans.java</p>
 * <p>Description>:评语模板列表</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 17, 2010 12:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class ListPerParamTrans extends IBusiness 
{
   
	public void execute() throws GeneralException 
	{
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String returnflag=(String)hm.get("returnflag");
		this.getFormHM().put("returnflag",returnflag);
		 
	    String from_eval =(String)hm.get("from_eval");	    
	    hm.remove("from_eval");
	    from_eval= from_eval==null?"0":from_eval;

		this.getFormHM().put("from_eval", from_eval);	   
	    
		try
		{
			ArrayList setlist=this.searchPerParamList();
			this.getFormHM().put("setlist", setlist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	public ArrayList searchPerParamList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		buf.append(" select id,kind,content,username,param_name from per_param order by id desc");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset = dao.search(buf.toString());
			while (this.frowset.next()) 
			{
				RecordVo vo = new RecordVo("per_param");
				vo.setString("id", this.frowset.getString("id"));
				vo.setString("kind", this.frowset.getString("kind"));
				vo.setString("content", PubFunc.toHtml(this.frowset.getString("content")));			
				vo.setString("username", this.frowset.getString("username"));
				vo.setString("param_name", this.frowset.getString("param_name"));
				list.add(vo);
			}
//			RowSet rset=dao.search(buf.toString());
//			while(rset.next())
//			{
//				LazyDynaBean lazyvo=new LazyDynaBean();
//				lazyvo.set("id", rset.getString("id"));
//				lazyvo.set("kind", rset.getString("kind"));
//				lazyvo.set("content", PubFunc.toHtml(rset.getString("content")));
//				lazyvo.set("username", rset.getString("username"));
//				lazyvo.set("paramName", rset.getString("param_name"));
//				list.add(lazyvo);
//			}
//			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
}
