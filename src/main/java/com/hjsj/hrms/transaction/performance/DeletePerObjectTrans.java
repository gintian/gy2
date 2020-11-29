package com.hjsj.hrms.transaction.performance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class DeletePerObjectTrans extends IBusiness {

	

	public void execute() throws GeneralException {
		
		ArrayList perObjectlist=(ArrayList)this.getFormHM().get("selectedList");
		String plan_id=(String)this.getFormHM().get("dbpre");
		
        if(perObjectlist==null||perObjectlist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList  list=new ArrayList();  
        
        StringBuffer sql_str=new StringBuffer("select object_id  from per_object where ");
        StringBuffer whl_str=new StringBuffer("");
        try
        {
        	for(Iterator t=perObjectlist.iterator();t.hasNext();)
        	{
        		LazyDynaBean a=(LazyDynaBean)t.next();
        		ArrayList temList=new ArrayList();
        		temList.add(a.get("id"));
        		
        		whl_str.append(" or id="+a.get("id").toString());
        		list.add(temList);
        		
          }
        	
        	sql_str.append(whl_str.substring(4));
        	this.frowset=dao.search(sql_str.toString());
        	ArrayList mainBodyList=new ArrayList();
        	ArrayList perPointList=new ArrayList();
        	
        	while(this.frowset.next())
        	{
        		
        		ArrayList temp=new ArrayList();
        		temp.add(this.frowset.getString("object_id"));
        		temp.add(plan_id);
        		
        		ArrayList temp2=new ArrayList();
        		temp2.add(this.frowset.getString("object_id"));
        		
        		mainBodyList.add(temp);
        		perPointList.add(temp2);
        		
        	}
        	
        	dao.batchUpdate("delete from per_object where id=?",list) ;  
        	/** 删除考核对象所对应的主体 */
        	dao.batchUpdate("delete from per_mainbody where  object_id=? and plan_id=?",mainBodyList);
    		/** 删除考核主体要素权限 */
        	dao.batchUpdate("delete from per_pointpriv_"+plan_id+" where object_id=?",perPointList);
        	
			DbWizard dbWizard=new DbWizard(this.getFrameconn());
			if(dbWizard.isExistTable("per_table_"+plan_id,false))
				dao.batchUpdate("delete from per_table_"+plan_id+" where object_id=?",perPointList);
			if(dbWizard.isExistTable("per_result_"+plan_id,false))
				dao.batchUpdate("delete from per_result_"+plan_id+" where object_id=?",perPointList);
			if(dbWizard.isExistTable("per_gather_score_"+plan_id,false))//业绩数据录入里用到的表
				dao.batchUpdate("delete from per_gather_score_"+plan_id+" where gather_id in (select gather_id  from per_gather_"+plan_id+" where object_id=?) ",perPointList);
			if(dbWizard.isExistTable("per_gather_"+plan_id,false))
				dao.batchUpdate("delete from per_gather_"+plan_id+" where object_id=?",perPointList);
			
			
			
			 dao.delete("DELETE FROM per_interview WHERE plan_id = "+plan_id+" AND NOT (object_id IN (SELECT object_id FROM per_object WHERE plan_id = "+plan_id+"))" ,new ArrayList());
			
			/**fzg add*/
			dao.delete("DELETE FROM per_target_evaluation WHERE plan_id = "+plan_id+" AND NOT (object_id IN (SELECT object_id FROM per_object WHERE plan_id = "+plan_id+"))" ,new ArrayList());
			dao.delete("DELETE FROM per_article WHERE plan_id = "+plan_id+" AND NOT (A0100  IN (SELECT object_id FROM per_object WHERE plan_id = "+plan_id+"))" ,new ArrayList());
			if("2".equals(this.getPlanMethod(plan_id)))//目标管理计划
			{
			    dao.delete("DELETE FROM P04 WHERE plan_id = "+plan_id+" AND NOT (A0100 IN (SELECT object_id FROM per_object WHERE plan_id = "+plan_id+"))" ,new ArrayList());
			   
			    dao.delete("DELETE FROM PER_ITEMPRIV_"+plan_id+" where object_id not in (SELECT object_id FROM per_object WHERE plan_id = "+plan_id+")", new ArrayList());
			}	
			//删除动态项目权重表的数据
			String sql = "DELETE FROM  per_dyna_item where plan_id="+plan_id+" and body_id not in (select body_id from per_object where plan_id = "+plan_id+")";
			dao.delete(sql, new ArrayList());
        	
        	
        	
        }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	
	}

	 public String getPlanMethod(String plan_id)
	    {

		String method = "1";
		ContentDAO dao = new ContentDAO(this.frameconn);
		RecordVo vo = new RecordVo("per_plan");
		vo.setString("plan_id", plan_id);
		try
		{
		    vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		if(vo.getString("method")!=null)
		    method = vo.getString("method");
		return method;
	    }
	
}
