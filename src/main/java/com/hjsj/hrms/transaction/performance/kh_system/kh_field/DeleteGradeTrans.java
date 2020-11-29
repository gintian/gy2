package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:DeleteGradeTrans.java</p>
 * <p>Description:删除标准标度</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2011-08-06</p>
 * @author JinChunhai
 * @version 5.0
 */

public class DeleteGradeTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(subsys_id!=null && subsys_id.trim().length()>0 && "35".equalsIgnoreCase(subsys_id))
				per_comTable = "per_grade_competence"; // 能力素质标准标度
			String ids = (String)this.getFormHM().get("ids");
			StringBuffer buf = new StringBuffer();
			if(ids.indexOf("`")!=-1)
			{
				String temp[] = ids.split("`");
				for(int i=0;i<temp.length;i++)
				{
					buf.append(",'");
					buf.append(temp[i]);
					buf.append("'");
				}
			}else
			{
				buf.append(",'");
				buf.append(ids);
				buf.append("'");
			}
			String sql = "delete from "+per_comTable+" where grade_template_id in("+buf.toString().substring(1)+")";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.delete(sql,new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
