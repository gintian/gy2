package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:DeleteCheckBodyObjectTrans.java</p>
 * <p>Description:清除考核主体筛选条件</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-05-15 11:11:11</p>
 * @author JinChunhai
 * @version 6.0
 */

public class DeleteCheckBodyObjectTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{	
//		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
//		String bodyType =(String)hm.get("bodyType");
//		hm.remove("bodyType");
		
		String bodyType = (String)this.getFormHM().get("bodyType");
		String typeidss = (String)this.getFormHM().get("deletestr");
		String typeids = typeidss.substring(0,typeidss.length()-1);		
		String [] temp = typeids.replaceAll("／", "/").split("/");
		if(!"".equals(typeids))
		{
			this.deleteFactor(temp,bodyType);
			this.getFormHM().put("info","true");
		}
	}
	
	public void deleteFactor(String [] temp,String bodyType) throws GeneralException
	{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer body_id=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        try 
        {
        	for(int i=0;i<temp.length;i++)
			{
				 if(isExist(temp[i],bodyType))  
				 {					
					 throw new GeneralException("存在正在使用的记录，不能删除！");									 
				 }			 				 
				 body_id.append("'");
				 body_id.append(temp[i]);
				 body_id.append("',");
	        }    
		 	body_id.setLength(body_id.length()-1);
            strsql.append("delete from per_mainbodyset where body_id in(");
            strsql.append(body_id.toString());
            strsql.append(")");
			dao.delete(strsql.toString(),new ArrayList());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public boolean isExist(String bodyId,String bodyType) throws GeneralException
	{
		StringBuffer strsql = new StringBuffer();
	
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		try 
		{
			if("0".equals(bodyType))//主体考核类别
			{
				strsql.setLength(0);
				strsql.append("select body_id from per_mainbody where body_id=");
				strsql.append(bodyId);
				this.frowset = dao.search(strsql.toString());
				if (this.frowset.next()) 
					return true;
				
				strsql.setLength(0);
				strsql.append("select body_id from per_mainbody_std where body_id=");
				strsql.append(bodyId);
				this.frowset = dao.search(strsql.toString());
				if (this.frowset.next()) 
					return true;
			}else if("1".equals(bodyType))//对象考核类别
			{
				strsql.setLength(0);
				strsql.append("select body_id from per_object where body_id=");
				strsql.append(bodyId);
				this.frowset = dao.search(strsql.toString());
				if (this.frowset.next()) 
					return true;
				
				strsql.setLength(0);
				strsql.append("select * from per_object_std where obj_body_id=");
				strsql.append(bodyId);
				this.frowset = dao.search(strsql.toString());
				if (this.frowset.next()) 
					return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneralException("查询数据异常！");
		}
		return false;
	}	

}
