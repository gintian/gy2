package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:GetPersonByCondTrans.java</p>
 * <p>Description:业绩任务书分配任务高级</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2009-07-21</p>
 * @author JinChunhai
 * @version 4.2
 */

public class GetPersonByCondTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			String sql_str=(String)this.getFormHM().get("sql_str");
			sql_str = PubFunc.keyWord_reback(sql_str);
			sql_str = PubFunc.decrypt(SafeCode.decode(sql_str));
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from  UsrA01 where a0100 in ( "+sql_str+" ) ");
			ArrayList fieldlist=new ArrayList();
			while(this.frowset.next())
			{
				CommonData data=new CommonData("Usr"+this.frowset.getString("a0100"),this.frowset.getString("a0101"));
				fieldlist.add(data);
			}
			this.getFormHM().put("fieldlist", fieldlist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
