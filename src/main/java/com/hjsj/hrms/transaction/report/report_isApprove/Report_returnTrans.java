package com.hjsj.hrms.transaction.report.report_isApprove;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

public class Report_returnTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String flag = (String) hm.get("flag");
			String unitcode = (String) hm.get("unitcode1");
			String tabid=(String)hm.get("tabid");	
			String description = "";
			String content1 = "";
			String[] content = null;
			String  sql = "select appuser,username,description from treport_ctrl where unitcode = '"+unitcode+"' and tabid = "+tabid+"";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				description = rs.getString("description");
			}

			description = PubFunc.keyWord_reback(description);
			if(description!=null&&!"".equals(description)){
				content = description.split(";");
			}
			if(content!=null){
				for(int i=0;i<content.length;i++){
					content1 = content1 + content[i]+"\n";
				}
			}

			this.getFormHM().put("content", content1);
			this.getFormHM().put("flag", flag);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
