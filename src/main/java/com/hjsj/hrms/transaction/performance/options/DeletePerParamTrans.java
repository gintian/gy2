package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:DeletePerParamTrans.java</p>
 * <p>Description>:删除评语模板</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 17, 2010 12:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class DeletePerParamTrans extends IBusiness {

	public void execute() throws GeneralException 
	{	
		String typeidss = (String)this.getFormHM().get("deletestr");
		String typeids = typeidss.substring(0,typeidss.length()-1);		
		String [] temp = typeids.replaceAll("／", "/").split("/");
		if(!"".equals(typeids))
		{
			this.deleteFactor(temp);
			this.getFormHM().put("info","true");
		}
	}
	
	public void deleteFactor(String [] temp)
	{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer ids=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        try 
        {
			 for(int i=0;i<temp.length;i++)
			 {
				 ids.append("'");
				 ids.append(temp[i]);
				 ids.append("',");
	         }    
			 ids.setLength(ids.length()-1);
			 strsql.append("delete from per_param where id in(");
			 strsql.append(ids.toString());
			 strsql.append(")");
			 dao.delete(strsql.toString(),new ArrayList());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

}
