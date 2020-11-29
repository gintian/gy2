package com.hjsj.hrms.transaction.gz.templateset.moneystyle;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveMoneyStyleDetailTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String cname=(String)this.getFormHM().get("cname");
			String nitemid=(String)this.getFormHM().get("nitemid");
			String nstyleid=(String)this.getFormHM().get("nstyleid");
			String beforenitemid=(String)this.getFormHM().get("beforenitemid");
			StringBuffer sql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(beforenitemid==null|| "".equals(beforenitemid))
			{
				//新增
				sql.append("insert into moneyitem (nstyleid,nitemid,cname,nflag) values (");
				sql.append("'"+nstyleid+"','");
				sql.append(nitemid+"','");
				sql.append(cname+"','1')");
				dao.insert(sql.toString(),new ArrayList());
			}
			else
			{
				//修改
				sql.append("update moneyitem set nitemid='");
				sql.append(nitemid+"',");
				sql.append("cname='");
				sql.append(cname+"'");
				sql.append(" where nstyleid='");
				sql.append(nstyleid+"' and nitemid='");
				sql.append(beforenitemid+"'");
				dao.update(sql.toString());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
