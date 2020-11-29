package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:SaveGradeScopeTrans.java</p>
 * <p>Description:保存设置的主体权重数据</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 15, 2011:11:53:46 AM</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SaveGradeScopeTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		try
		{
			String planid=(String)this.getFormHM().get("planid");			
			ArrayList gradeScopeList=(ArrayList)this.getFormHM().get("gradeScopeList");						
									
			String nodename="ScoreScopes";
			String childname="ScoreScope";
			ArrayList nodeAttribute=new ArrayList();
			String node_str="PerPlan_Parameter/ScoreScopes";
			ArrayList childattribute=new ArrayList();
			childattribute.add("BodyId");			
			childattribute.add("DownScope");
			childattribute.add("UpScope");
			
			LoadXml loadxml = new LoadXml(this.frameconn, planid);
			loadxml.saveHasMoreChildsNode(nodename, childname, nodeAttribute, gradeScopeList, node_str, childattribute);			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		
	}

}