package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:DeleteProAppraiseTrans.java</p>
 * <p>Description:删除描述性评议项</p>
 * <p>Company:hjsj</p>
 * <p>create time:2014-08-16 14:13:19</p>
 * @author JinChunhai
 * @version 7.0 
 */

public class DeleteProAppraiseTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    	String plan_id = (String) hm.get("plan_id");
    	String delStr = (String) hm.get("ids");
    	
		ExamPlanBo bo = new ExamPlanBo(plan_id,this.frameconn);
		String tempTable = "t#des_review";
    	try
	    {
    		ContentDAO dao = new ContentDAO(this.getFrameconn());			
			
    		String[] ids = delStr.split("@");
    		StringBuffer delSql = new StringBuffer();
    		for (int i = 0; i < ids.length; i++)
    		{
    		    if ("".equals(ids[i]))
    		    	continue;
    		    delSql.append(ids[i]);
    		    delSql.append(",");
    		}
    		delSql.setLength(delSql.length() - 1);
    	
    		StringBuffer strSql = new StringBuffer();
    		strSql.append("delete from ");
    		strSql.append(tempTable);
    		strSql.append(" where id in (");
    		strSql.append(delSql.toString());
    		strSql.append(")");    	
    		
    		dao.delete(strSql.toString(), new ArrayList());
		    
		    // 保存描述性评议项设置
		    bo.saveHighSet(tempTable);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}finally
		{		
			ArrayList list = bo.getTempData(tempTable);
		    this.getFormHM().put("extproList",list);	
		    this.getFormHM().put("addDescription","");
		}		

    }

}
