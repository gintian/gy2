package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:DelMainBodyTypeTrans.java</p>
 * <p>Description:删除考核主体类别相关</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-03-23 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class DelMainBodyTypeTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		String plan_id = (String)this.getFormHM().get("plan_id");
		String body_id = (String)this.getFormHM().get("body_id");
	
		ArrayList list1 = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select * from per_mainbody ");
		buf.append("WHERE plan_id=");
		buf.append(plan_id);
		buf.append("  AND body_id=");
		buf.append(body_id);
	
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
		    RowSet rs = dao.search(buf.toString());
		    while (rs.next())
		    {
		    	ArrayList list = new ArrayList();
		        String object_id = rs.getString("object_id");
		        String mainbody_id = rs.getString("mainbody_id");
		        list.add(object_id);
		        list.add(mainbody_id);
		        list1.add(list);
		    }
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		//删除 PER_POINTPRIV_xxx表的记录
		buf = new StringBuffer();
		buf.append("delete from PER_POINTPRIV_");
		buf.append(plan_id);
		buf.append(" where object_id=? and mainbody_id=?");
		if(list1.size()>0)
		{
		    try
		    {
		    	dao.batchUpdate(buf.toString(), list1);
		    } catch (Exception e)
		    {
		    	e.printStackTrace();
		    }
		}
		
		//删除待办记录 chent 20160220 start
		String sql = "select mainbody_id From per_mainbody where plan_id="+plan_id+" and body_id="+body_id+"";
		RowSet rs = null;
		try
	    {
			rs = dao.search(sql);//查看该主体类别下的考核主体
			while(rs.next()){
				String mainBody = rs.getString("mainbody_id");
				String delSql = "delete from t_hr_pendingtask where pending_type='33' and receiver='Usr"+mainBody+"' and ext_flag like 'PERPF_"+plan_id+"%'";
				dao.update(delSql);//删除待办
			}
	    } catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	    //删除待办记录 chent 20160220 end
		
		//删除per_mainbody表的记录
		buf = new StringBuffer();
		buf.append("delete from per_mainbody ");
		buf.append("WHERE plan_id=");
		buf.append(plan_id);
		buf.append("  AND body_id=");
		buf.append(body_id);
		
		try
	    {
			dao.delete(buf.toString(), new ArrayList());
	    } catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
		
    }

}
