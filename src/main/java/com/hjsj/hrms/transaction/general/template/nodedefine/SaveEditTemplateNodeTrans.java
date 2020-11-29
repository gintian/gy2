/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.nodedefine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Owner
 *
 */
public class SaveEditTemplateNodeTrans extends IBusiness{

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		LazyDynaBean actor=(LazyDynaBean)hm.get("actor");
		try{
			if(actor!=null)
			{
				String tabid=(String)actor.get("tabid");
				String node_id=(String)actor.get("node_id");
				String nodename=(String)actor.get("nodename");
				String actorid=(String)actor.get("acotrid");
				String actorname=(String)actor.get("actorname");
				String actor_type=(String)actor.get("objecttype");
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				StringBuffer updatestr=new StringBuffer();
				updatestr.append("update t_wf_node set nodename='");
				updatestr.append(nodename);
				updatestr.append("' where node_id=");
				updatestr.append(node_id);
				updatestr.append(" and tabid=");
				updatestr.append(tabid);
				dao.update(updatestr.toString());
				StringBuffer deletestr=new StringBuffer();
				deletestr.append("delete from t_wf_actor where node_id=");
				deletestr.append(node_id);
				/* 当前节点仅能定义一种类型的审批对象,chenmengqing added at 20080325
    			deletestr.append(" and actor_type='");
				deletestr.append(actor_type);
				deletestr.append("'");
				*/
				dao.delete(deletestr.toString(),new ArrayList());
				StringBuffer addstr=new StringBuffer();
				addstr.append("insert into t_wf_actor(node_id,actorid,actor_type,actorname)values(");
				addstr.append(node_id);
				addstr.append(",'");
				addstr.append(actorid);
				addstr.append("','");
				addstr.append(actor_type);
				addstr.append("','");
				addstr.append(actorname);
				addstr.append("')");
				dao.insert(addstr.toString(),new ArrayList());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
