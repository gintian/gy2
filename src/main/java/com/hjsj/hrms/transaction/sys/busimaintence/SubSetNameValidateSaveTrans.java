package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * <p>Title:构库子集名称验证保存</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 27, 2009:10:32:13 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SubSetNameValidateSaveTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tableid = (String)this.getFormHM().get("tableid");
		String tablename = (String)this.getFormHM().get("tablename");
		tablename = com.hrms.frame.codec.SafeCode.decode(tablename);
		String mainid = (String)this.getFormHM().get("mainid");
		this.savetable(tableid, tablename,mainid);
	}
	public void savetable(String id , String name, String mainid)
	{
		try{
			ContentDAO dao= new ContentDAO(this.getFrameconn());
			String sql = "Update t_hr_busitable set customdesc ='"+name+"' where fieldsetid ='"+id+"' and id ='"+mainid+"'";
			dao.update(sql, new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
