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

/**
 * @author Owner
 *
 */
public class DeleteTemplateNodeTrans extends IBusiness{

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList deletelist=(ArrayList)this.getFormHM().get("selectedlist");		//System.out.println("fadsfdasf" + deletelist.size());
		
		if(deletelist==null || deletelist.size()==0)
			return;
		StringBuffer deletesql=new StringBuffer();
		StringBuffer deleteactorsql=new StringBuffer();
		deletesql.append("delete from t_wf_node where ");
		deleteactorsql.append("delete from t_wf_actor where");
		StringBuffer updatesql=new StringBuffer();
	    StringBuffer checksql=new StringBuffer();
		try
		{
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
			for(int i=0;i<deletelist.size();i++)
			{
				LazyDynaBean rec=(LazyDynaBean)deletelist.get(i);
				if(i==0)
				{
					deletesql.append(" node_id=");
					deletesql.append(rec.get("node_id"));
					//deleteactorsql.append(" node_id")
					deleteactorsql.append(" node_id=");
					deleteactorsql.append(rec.get("node_id"));
				}
				else
				{
					deletesql.append(" or node_id=");
					deletesql.append(rec.get("node_id"));
					deleteactorsql.append(" or node_id=");
					deleteactorsql.append(rec.get("node_id"));
				}
				/*this.frowset=dao.search("select next_nodeid from t_wf_transition where pre_nodeid=" + rec.get("node_id"));
				if(this.frowset.next())
				{*/
				    updatesql.delete(0,updatesql.length());
					updatesql.append("update t_wf_transition set next_nodeid=(select next_nodeid from t_wf_transition where pre_nodeid=");
					updatesql.append(rec.get("node_id"));
					updatesql.append(" and next_nodeid is not null) where next_nodeid=");
					updatesql.append(rec.get("node_id"));
					//System.out.println(updatesql.toString());
					dao.update(updatesql.toString());
					updatesql.delete(0,updatesql.length());
					updatesql.append("delete from t_wf_transition where pre_nodeid=");
					updatesql.append(rec.get("node_id"));
					dao.delete(updatesql.toString(),new ArrayList());
				/*}else
				{
					updatesql.delete(0,updatesql.length());
					updatesql.append("delete from t_wf_transition where next_nodeid=");
					updatesql.append(rec.get("node_id"));
					dao.delete(updatesql.toString(),new ArrayList());
				}*/
				
			}

		    dao.delete(deletesql.toString(),new ArrayList());
		    dao.delete(deleteactorsql.toString(),new ArrayList());
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
