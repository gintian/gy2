/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.nodedefine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * @author Owner
 *
 */
public class SearchTranNodeTrans extends IBusiness{

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tabid=(String)this.getFormHM().get("tabid");
		StringBuffer sql=new StringBuffer();
		sql.append("select n.node_id,n.nodename from t_wf_node n,t_wf_transition o where n.tabid=");
		sql.append(tabid);
		sql.append(" and n.node_id=o.next_nodeid and n.nodetype='2'  order by o.tran_id");
		//System.out.println(sql);
		 try
		  {
			ArrayList inforlist=new ArrayList();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				CommonData vo=new CommonData(this.frowset.getString("node_id"),this.frowset.getString("nodename"));
				inforlist.add(vo);
			}
			this.getFormHM().put("tranlist",inforlist);			
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);
		  }
	}

}
