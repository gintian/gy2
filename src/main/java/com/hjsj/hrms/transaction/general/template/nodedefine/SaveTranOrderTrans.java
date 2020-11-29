/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.nodedefine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Owner
 *
 */
public class SaveTranOrderTrans extends IBusiness{

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList tranlist=new ArrayList();
		String[] nodeList=(String[])this.getFormHM().get("right_fields");
		String tabid=(String)this.getFormHM().get("tabid");
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer searchnodes=new StringBuffer();
			searchnodes.append("select tran_id from t_wf_transition where tabid=");
			searchnodes.append(tabid);
			searchnodes.append(" order by tran_id");
			this.frowset=dao.search(searchnodes.toString());
			while(this.frowset.next())
			{
				tranlist.add(String.valueOf(this.getFrowset().getInt("tran_id")));
			}
			UpdateTran(nodeList,tranlist,tabid,dao);
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
			//System.out.println(idsql.toString());
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
	private void UpdateTran(String[] nodelist,ArrayList tranlist,String tabid,ContentDAO dao) throws GeneralException
	{
		try{
			getBeginAndEndNodeid(tabid,dao);
			StringBuffer updatesql=new StringBuffer();
			for(int i=0;i<tranlist.size();i++)
			{
				updatesql.delete(0,updatesql.length());
				if(i==0)
				{
					updatesql.append("update t_wf_transition set pre_nodeid=");
					updatesql.append(this.getPrenode());
			    	updatesql.append(",next_nodeid=");
					updatesql.append(nodelist[i]);
					updatesql.append(" where tran_id=");
					updatesql.append(tranlist.get(i));
					updatesql.append(" and tabid=");
					updatesql.append(tabid);
					dao.update(updatesql.toString());
				}else if(i==tranlist.size()-1)
				{
					updatesql.append("update t_wf_transition set pre_nodeid=");
					updatesql.append(nodelist[i-1]);
					updatesql.append(",next_nodeid=");
					updatesql.append(this.getEndnode());
					updatesql.append(" where tran_id=");
					updatesql.append(tranlist.get(i));
					updatesql.append(" and tabid=");
					updatesql.append(tabid);
					dao.update(updatesql.toString());
				}else
				{
					updatesql.append("update t_wf_transition set pre_nodeid=");
					updatesql.append(nodelist[i-1]);
					updatesql.append(",next_nodeid=");
					updatesql.append(nodelist[i]);
					updatesql.append(" where tran_id=");
					updatesql.append(tranlist.get(i));
					updatesql.append(" and tabid=");
					updatesql.append(tabid);
					dao.update(updatesql.toString());
				}
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
