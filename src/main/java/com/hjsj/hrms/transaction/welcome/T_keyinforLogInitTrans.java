package com.hjsj.hrms.transaction.welcome;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class T_keyinforLogInitTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
		String content_type=(String)hm.get("content_type");
		String id=(String)hm.get("id");
		if(content_type==null||content_type.length()<=0)
			content_type="0";
		String content="";
		try
		{
			if("0".equals(content_type))
			{
				String sql = "select topic from announce where id="+ id;
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset = dao.search(sql);
				if (this.frowset.next()) {
					content=this.frowset.getString("topic");
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		 ArrayList dblist=userView.getPrivDbList();
	        StringBuffer cond=new StringBuffer();
	        cond.append("select pre,dbname from dbname where pre in (");
	        for(int i=0;i<dblist.size();i++)
	        {
	            if(i!=0)
	                cond.append(",");
	            cond.append("'");
	            cond.append((String)dblist.get(i));
	            cond.append("'");
	        }
	        if(dblist.size()==0)
	            cond.append("''");
	        cond.append(")");
	        cond.append(" order by dbid");
	        /**应用库前缀过滤条件*/
	        this.getFormHM().put("dbcond",cond.toString());
		this.getFormHM().put("content_type", content_type);
		this.getFormHM().put("content", content);
	}

}
