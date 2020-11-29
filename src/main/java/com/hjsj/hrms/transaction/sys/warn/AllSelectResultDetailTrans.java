package com.hjsj.hrms.transaction.sys.warn;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 预警人员结果全选
 * @author Owner
 *
 */
public class AllSelectResultDetailTrans  extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String sql=(String)this.getFormHM().get("sql");
		sql=SafeCode.decode(sql);
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);			
			while(this.frowset.next())
			{
				list.add(this.frowset.getString("nbase")+this.frowset.getString("a0100"));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("list", list);
	}

}
