package com.hjsj.hrms.transaction.stat.history;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * 常用统计保存
 * @author Administrator
 *
 */
public class SetStaticDataDateTrans  extends IBusiness {
	public void execute() throws GeneralException {
		String statid=(String)this.getFormHM().get("statid");
        String sql="select * from SName where id="+statid;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String archive_type="";
        try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				archive_type=this.frowset.getString("archive_type");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.getFormHM().put("archive_type", archive_type);
	}

}
