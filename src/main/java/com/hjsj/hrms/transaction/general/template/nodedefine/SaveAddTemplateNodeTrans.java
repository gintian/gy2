/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.nodedefine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
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
public class SaveAddTemplateNodeTrans extends IBusiness{

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
				IDGenerator idg=new IDGenerator(2,this.getFrameconn());
			
	            String node_id=idg.getId("wf_node.node_id");
				String nodename=(String)actor.get("nodename");
				String actorid=(String)actor.get("acotrid");
				String actorname=(String)actor.get("actorname");
				String actor_type=(String)actor.get("objecttype");
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				StringBuffer addstr=new StringBuffer();
				
			
				addstr.append("insert into t_wf_node(node_id,nodename,nodetype,tabid)values(");
				addstr.append(node_id);
				addstr.append(",'");
				addstr.append(nodename);
				addstr.append("','2',");
				addstr.append(tabid);
				addstr.append(")");
				//添加节点
				dao.insert(addstr.toString(),new ArrayList());
				addstr.delete(0,addstr.length());
				addstr.append("insert into t_wf_actor(node_id,actorid,actor_type,actorname)values(");
				addstr.append(node_id);
				addstr.append(",'");
				addstr.append(actorid);
				addstr.append("','");
				addstr.append(actor_type);
				addstr.append("','");
				addstr.append(actorname);
				addstr.append("')");
				//添加节点审批人
	
				dao.insert(addstr.toString(),new ArrayList());
				
				
				
				getPreNode_id(tabid,dao);
				if(this.getPrenode()!=null && this.getEndnode()!=null)
				{
					//System.out.println("preid" + this.getPrenode());
					addstr.delete(0,addstr.length());
					addstr.append("update t_wf_transition set next_nodeid=");
					addstr.append(node_id);
					addstr.append(" where tabid=");
					addstr.append(tabid);
					addstr.append(" and next_nodeid=");
					addstr.append(this.getEndnode());
					dao.update(addstr.toString());
					addstr.delete(0,addstr.length());
					String tran_id=idg.getId("wf_trans.tran_id");
					addstr.append("insert into t_wf_transition(tran_id,tran_name,pre_nodeid,next_nodeid,tabid)values(");
					addstr.append(tran_id);
					addstr.append(",'");
					addstr.append(tran_id);
					addstr.append("',");
					addstr.append(node_id);
					addstr.append(",");
					addstr.append(this.getEndnode());
					addstr.append(",");
					addstr.append(tabid);
					addstr.append(")");
					//添加变迁节点流程
					dao.insert(addstr.toString(),new ArrayList());
				}
				else
				{
					getBeginAndEndNodeid(tabid,dao);
					addstr.delete(0,addstr.length());
					String tran_id=idg.getId("wf_trans.tran_id");
					addstr.append("insert into t_wf_transition(tran_id,tran_name,pre_nodeid,next_nodeid,tabid)values(");
					addstr.append(tran_id);
					addstr.append(",'");
					addstr.append(tran_id);
					addstr.append("',");
					addstr.append(this.getPrenode());
					addstr.append(",");
					addstr.append(node_id);
					addstr.append(",");
					addstr.append(tabid);
					addstr.append(")");
					//添加变迁节点流程
					dao.insert(addstr.toString(),new ArrayList());
					addstr.delete(0,addstr.length());
					String tran_id1=idg.getId("wf_trans.tran_id");
					addstr.append("insert into t_wf_transition(tran_id,tran_name,pre_nodeid,next_nodeid,tabid)values(");
					addstr.append(tran_id1);
					addstr.append(",'");
					addstr.append(tran_id1);
					addstr.append("',");
					addstr.append(node_id);
					addstr.append(",");
					addstr.append(this.getEndnode());
					addstr.append(",");
					addstr.append(tabid);
					addstr.append(")");
					//添加变迁节点流程
					dao.insert(addstr.toString(),new ArrayList());
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	private String prenode;
	private String endnode;
	private void getBeginAndEndNodeid(String tabid,ContentDAO dao) throws GeneralException
	{

		//String id=null;
		try
		{
			/*SELECT t_wf_transition.*
			FROM t_wf_transition INNER JOIN
			      t_wf_node ON t_wf_transition.tabid = t_wf_node.tabid AND 
			      t_wf_transition.next_nodeid = t_wf_node.node_id
			WHERE (t_wf_transition.tabid = 26) AND (t_wf_node.nodetype = 9)*/
			StringBuffer idsql=new StringBuffer();
			idsql.append("select * from t_wf_node o where o.tabid=");
			idsql.append(tabid);
			idsql.append(" and (nodetype='1' or nodetype='9')");
		//	System.out.println(idsql.toString());
			this.frowset=dao.search(idsql.toString());
			while(this.frowset.next())
			{
				if("1".equals(this.getFrowset().getString("nodetype")))
				  prenode=String.valueOf(this.getFrowset().getInt("node_id"));
			    if("9".equals(this.getFrowset().getString("nodetype")))
				  endnode=String.valueOf(this.getFrowset().getInt("node_id"));
			}			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}	
	}
	private void getPreNode_id(String tabid,ContentDAO dao) throws GeneralException
	{
		//String id=null;
		try
		{
			/*SELECT t_wf_transition.*
			FROM t_wf_transition INNER JOIN
			      t_wf_node ON t_wf_transition.tabid = t_wf_node.tabid AND 
			      t_wf_transition.next_nodeid = t_wf_node.node_id
			WHERE (t_wf_transition.tabid = 26) AND (t_wf_node.nodetype = 9)*/
			StringBuffer idsql=new StringBuffer();
			idsql.append("select t.next_nodeid,t.pre_nodeid  from t_wf_transition t,t_wf_node o where o.tabid=");
			idsql.append(tabid);
			idsql.append(" and t.tabid=o.tabid and t.next_nodeid=o.node_id and o.nodetype='9'");
			//System.out.println(idsql.toString());
			this.frowset=dao.search(idsql.toString());
			if(this.frowset.next())
			{
				prenode=String.valueOf(this.getFrowset().getInt("pre_nodeid"));
				endnode=String.valueOf(this.getFrowset().getInt("next_nodeid"));
			}			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public String getEndnode() {
		return endnode;
	}
	public void setEndnode(String endnode) {
		this.endnode = endnode;
	}
	public String getPrenode() {
		return prenode;
	}
	public void setPrenode(String prenode) {
		this.prenode = prenode;
	}

}
