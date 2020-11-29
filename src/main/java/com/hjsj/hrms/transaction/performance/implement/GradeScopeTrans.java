package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:GradeScopeTrans.java</p>
 * <p>Description:设置主体评分范围</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 15, 2011:9:05:04 AM</p>
 * @author JinChunhai
 * @version 5.0
 */

public class GradeScopeTrans extends IBusiness 
{
	
	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		try
		{
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String planid=(String)hm.get("planid");			
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
	        if(!_bo.isHavePriv(this.userView, planid)){	
	        	return;
	        } 
			ArrayList gradeScopeList = getGradeScopeList(planid);
			this.getFormHM().put("gradeScopeList",gradeScopeList);	
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	/**
	 * 查询主体评分范围
	 * @param planid
	 * @return
	 */
	public ArrayList getGradeScopeList(String planid)
	{
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{			
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			LoadXml loadxml = new LoadXml(this.frameconn, planid);						
			ArrayList gradeList = loadxml.getPerGradeScopeList("ScoreScope");
									
			PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());			
			StringBuffer sql = new StringBuffer("");
		    sql.append("select DISTINCT po.body_id,pmb.name ");
		    sql.append(" from per_object po left join per_mainbodyset pmb on po.body_id=pmb.body_id ");		    	   
		    sql.append(" where po.plan_id = " + planid);
		    sql.append(pb.getPrivWhere(this.userView));	
		    sql.append(" and po.body_id is not null ");
		    
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{
				String body_id = (String)rowSet.getString("body_id");				
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("BodyId", body_id);
				abean.set("name",rowSet.getString("name"));		
					
				if(body_id!=null && body_id.trim().length()>0)
				{	
					if(gradeList!=null && gradeList.size()>0)
					{
						for (int i = 0; i < gradeList.size(); i++)
						{
						    LazyDynaBean bean = (LazyDynaBean) gradeList.get(i);
						    String bodyId = (String) bean.get("BodyId");
						    String upScope = (String) bean.get("UpScope");
						    String downScope = (String) bean.get("DownScope");
						    
						    if(body_id.equalsIgnoreCase(bodyId))
						    {
						    	abean.set("DownScope",downScope);
						    	abean.set("UpScope",upScope);								
								break;
								
						    }else if(i==(gradeList.size()-1))
						    {
						    	abean.set("DownScope","");
						    	abean.set("UpScope","");
						    }
						}
					}else
					{
						abean.set("DownScope","");
						abean.set("UpScope","");						
					}
				}else
				{
					abean.set("DownScope","");
					abean.set("UpScope","");
				}
				
				list.add(abean);
			}
			
			if(rowSet!=null)
				rowSet.close();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

}
