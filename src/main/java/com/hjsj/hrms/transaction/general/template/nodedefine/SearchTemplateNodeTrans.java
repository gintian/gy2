package com.hjsj.hrms.transaction.general.template.nodedefine;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateUtilBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 */

/**
 * @author Owner
 *
 */
public class SearchTemplateNodeTrans extends IBusiness{

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	    String tabid=(String)this.getFormHM().get("tabid");
		StringBuffer nodesql=new StringBuffer();
		nodesql.append("select n.node_id,n.nodename,a.actorname,n.tabid from t_wf_node n");
		nodesql.append(Sql_switcher.left_join("t_wf_node n","t_wf_actor a","n.node_id","a.node_id"));
		nodesql.append(",t_wf_transition o where n.tabid=");
		nodesql.append(tabid);
		nodesql.append(" and nodetype='2' and o.next_nodeid=n.node_id order by o.tran_id");
		//System.out.println(nodesql.toString());
	    ArrayList nodelist=new ArrayList();
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
		    this.frowset=dao.search(nodesql.toString());
		    String node_id="";
		    String pre_id="";
		    String pre_name="";
		    String pre_actor="";
		    if(this.frowset.next())
		    {
		    	node_id=String.valueOf(this.getFrowset().getInt("node_id"));
		    	pre_id=String.valueOf(this.getFrowset().getInt("node_id"));
	    		pre_name=this.getFrowset().getString("nodename");			    	
	    		pre_actor+=this.getFrowset().getString("actorname")==null?"":this.getFrowset().getString("actorname");
	    		if(this.frowset.isLast())
	    		{
	    			LazyDynaBean rec=new LazyDynaBean();
	    			
	    			rec.set("node_id",String.valueOf(this.getFrowset().getInt("node_id")));
		    		rec.set("nodename",pre_name);
		    		rec.set("actorname",pre_actor);
	    			nodelist.add(rec);		
	    		}
	    		 while(this.frowset.next())
	 		    {
	 		    	node_id=String.valueOf(this.getFrowset().getInt("node_id"));
	 		    	if(!node_id.equalsIgnoreCase(pre_id))
	 		    	{
	 		    		LazyDynaBean rec=new LazyDynaBean();
	 		    		rec.set("node_id",pre_id);
	 		    		rec.set("nodename",pre_name);
	 		    		rec.set("actorname",pre_actor);
	 		    		pre_id=String.valueOf(this.getFrowset().getInt("node_id"));
	 		    		if(this.getFrowset().getString("nodename")!=null)
	 		    		    pre_name=this.getFrowset().getString("nodename");
	 		    		else
	 		    			pre_name="";
	 		    		if(this.getFrowset().getString("actorname")!=null)
	 		    		    pre_actor=this.getFrowset().getString("actorname");
	 		    		else
	 		    			pre_actor="";
	 		    		nodelist.add(rec);
	 		    		if(this.frowset.isLast())
	 		    		{
	 		    			LazyDynaBean rec1=new LazyDynaBean();
	 		    			rec1.set("node_id",pre_id);
	 		    			rec1.set("nodename",pre_name);
	 		    			rec1.set("actorname",pre_actor);
	 		    			nodelist.add(rec1);		
	 		    		}
	 		    	}else
	 		    	{
	 		    		if(this.getFrowset().getString("actorname")!=null)
	 		    	        pre_actor+="," + this.getFrowset().getString("actorname");
	 		    		if(this.frowset.isLast())
	 		    		{
	 		    			LazyDynaBean rec=new LazyDynaBean();
	 		    			
	 		    			rec.set("node_id",String.valueOf(this.getFrowset().getInt("node_id")));
	 			    		rec.set("nodename",pre_name);
	 			    		rec.set("actorname",pre_actor);
	 		    			nodelist.add(rec);		
	 		    		}
	 		    	}
	 		    }	    		
		    }
		    
		    String isOperate="1";
		    RecordVo template_tableVo =TemplateUtilBo.readTemplate(Integer.parseInt(tabid),this.getFrameconn());
			TemplateTableBo tt=new TemplateTableBo(this.getFrameconn(),template_tableVo,this.getUserView());
			if(tt.getSp_mode()==1)
				isOperate="0";
			this.getFormHM().put("isOperate",isOperate);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("nodelist",nodelist);
	}

}
