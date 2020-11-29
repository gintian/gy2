package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:DeletePerDegreeTrans.java</p>
 * <p>Description:绩效评估/考核等级/删除考核等级</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-15 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class DeletePerDegreeTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	
		String typeidss = (String) this.getFormHM().get("deletestr");
		String plan_id = (String) this.getFormHM().get("plan_id");		
		
		String typeids = typeidss.substring(0, typeidss.length() - 1);
		String[] temp = typeids.replaceAll("／", "/").split("/");
		if (!"".equals(typeids))
		{
		    if(this.deleteFactor(temp,plan_id))
		    	this.getFormHM().put("delflag", "1");
		    else
		    	this.getFormHM().put("delflag", "0");	    
		}
    }

    public boolean deleteFactor(String[] temp , String plan_id)
    {
		boolean flag =false;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer ids = new StringBuffer();
		
		try
		{
		    for (int i = 0; i < temp.length; i++)
		    {
				ids.append("'");
				ids.append(temp[i]);
				ids.append("',");
		    }
		    ids.setLength(ids.length() - 1);
		    
		    StringBuffer selsectSql = new StringBuffer();
		    selsectSql.append("select * from per_degreedesc where degree_id in (");
		    selsectSql.append(ids.toString());
		    selsectSql.append(")");
		    RowSet rs = dao.search(selsectSql.toString());
		    if(rs.next())
		    	return flag;
		    
		    StringBuffer strsql = new StringBuffer();
		    strsql.append("delete from per_degree where degree_id in(");
		    strsql.append(ids.toString());
		    strsql.append(")");
		    dao.delete(strsql.toString(), new ArrayList());
		    
		    // 删除计划参数中等级的高级设置xml
		    if(plan_id!=null && plan_id.trim().length()>0)
			{
				LoadXml loadxml = new LoadXml(this.frameconn,plan_id);						
				loadxml.deleteGradeHighValue(temp);				
			}
		    
		    flag=true;
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		return flag;
    }

}
