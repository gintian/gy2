package com.hjsj.hrms.transaction.browse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;

public class RejectInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList approveInfoList=(ArrayList)this.getFormHM().get("selectedlist");
		String a0100=(String)this.getFormHM().get("a0100");
		String userbase=(String)this.getFormHM().get("userbase");
		String setname=(String)this.getFormHM().get("setname");
		String i9999=(String)this.getFormHM().get("i9999");
		StringBuffer approvesql=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
			if("A01".equalsIgnoreCase(setname))
			{
				approvesql.append("update ");
				approvesql.append(userbase);
				approvesql.append("A01 set state='2' where a0100='");
				approvesql.append(a0100);
				approvesql.append("' and (state='1' or state='3')");
			}else if("A00".equalsIgnoreCase(setname))
			{
				if(approveInfoList==null || approveInfoList.size()==0)
					return;
				approvesql.append("update ");
				approvesql.append(userbase);
				approvesql.append(setname);
				approvesql.append(" set state='2' where (state='1' or state='3') and a0100='");
				approvesql.append(a0100);
				approvesql.append("' and (i9999=-1 ");
				for(int i=0;i<approveInfoList.size();i++)
				{
					approvesql.append(" or i9999=");
					DynaBean vo=(DynaBean)approveInfoList.get(i);
					approvesql.append(vo.get("i9999"));
				}
				approvesql.append(")");
			
			}else
			{
				if(approveInfoList==null || approveInfoList.size()==0)
					return;
				approvesql.append("update ");
				approvesql.append(userbase);
				approvesql.append(setname);
				approvesql.append(" set state='2' where (state='1' or state='3') and a0100='");
				approvesql.append(a0100);
				approvesql.append("' and (i9999=-1 ");
				for(int i=0;i<approveInfoList.size();i++)
				{
					approvesql.append(" or i9999=");
					RecordVo vo=(RecordVo)approveInfoList.get(i);
					approvesql.append(vo.getInt("i9999"));
				}
				approvesql.append(")");
			}
			//System.out.println(approvesql.toString());
			dao.update(approvesql.toString());
	    }
	    catch(Exception sqle)
	    {
	       sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	}

}
