package com.hjsj.hrms.transaction.selfinfo.agent;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 删除代办
 * <p>Title:DelAgentTrans.java</p>
 * <p>Description>:DelAgentTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Aug 21, 2010 11:08:06 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class DelAgentTrans extends IBusiness {

	public void execute() throws GeneralException {
		 ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
		 if(selectedinfolist==null||selectedinfolist.size()<=0)
			 return;
		 String sql="delete from agent_set where id=?";

		 ArrayList list=new ArrayList();
		 String Agentnameid = "";
		 for(int i=0;i<selectedinfolist.size();i++)
		 {
			LazyDynaBean dean= (LazyDynaBean)selectedinfolist.get(i);
			ArrayList li=new ArrayList();
			li.add(dean.get("id"));
			list.add(li);
			Agentnameid+=dean.get("id");
			Agentnameid+=",";
		 }
		 Agentnameid=Agentnameid.substring(0,Agentnameid.length()-1);
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 String str = "";
		 String agenta0100 = "";	//代理人的a0100
		 String agentnbase= "";		//代理人的所在库名
		 //删除代理人 查询被删除的代理人的姓名，显示在日志上
		 try {
			 String sqla0100="select * from agent_set where id in ("+Agentnameid+")";
	
			 this.frowset=dao.search(sqla0100);
				while(this.frowset.next())
		  		{
					agenta0100 = this.frowset.getString("a0100");
					agentnbase = this.frowset.getString("nbase");
					String sqlname1="select A0101 ,* from "+agentnbase+"A01 where A0100 = '"+agenta0100+"'";
					RowSet a = this.frowset;
					a=dao.search(sqlname1);
					while(a.next())
				  	{
						str+= a.getString("a0101");
						str+= ",";
				  	}	
					
		  		}

				str=str.substring(0,str.length()-1);//去掉最后一个逗号
			dao.batchUpdate(sql, list);
			//删除代理人日志 author:zangxj  day:2014-06-07
			this.getFormHM().put("@eventlog",ResourceFactory.getProperty("agent.proxy.del")+"("+str+")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    

}
