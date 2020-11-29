package com.hjsj.hrms.transaction.performance.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:InitAddPerParamTrans.java</p>
 * <p>Description>:新增或编辑评语模板</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 17, 2010 12:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class InitAddPerParamTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("per_param");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
	    String id=(String)hm.get("id");
		
		String info=(String)this.getFormHM().get("info");

		try
		{
			if("edit".equals(info))
			{
				StringBuffer strsql = new StringBuffer();
				strsql.append("select id,kind,content,username,param_name from per_param  where id=");
				strsql.append(id);
				this.frowset = dao.search(strsql.toString());
				if (this.frowset.next()) 
				{
					vo.setString("id", frowset.getString("id"));
					vo.setString("kind", frowset.getString("kind"));
					vo.setString("content", frowset.getString("content"));
					vo.setString("username", frowset.getString("username"));
					vo.setString("param_name", frowset.getString("param_name"));				
				}
				
//				PerParamBo bo=new PerParamBo(this.getFrameconn());
//				RecordVo recordVo=bo.getPlanVo(id);
//				String kind=(String)recordVo.getString("kind");
//				String content=(String)recordVo.getString("content");
//				String username=(String)recordVo.getString("username");
//				String paramName=(String)recordVo.getString("param_name");
//				
//				this.getFormHM().put("id",id);
//				this.getFormHM().put("kind",kind);
//				this.getFormHM().put("content",content);
//				this.getFormHM().put("username",username);
//				this.getFormHM().put("paramName",paramName);
				this.getFormHM().put("info","editend");
				
			} else 
			{						
				vo.setString("id","");
				vo.setString("kind","");
				vo.setString("content","");
				vo.setString("username","");
				vo.setString("param_name","");
			}
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally
        {   
//		System.out.println("---------------");
            this.getFormHM().put("perparamvo",vo);
//         System.out.println("++++++++++++++++");
        }
		
	}
	
}
