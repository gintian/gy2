package com.hjsj.hrms.transaction.report.report_isApprove;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

public class Report_content extends IBusiness {

	public void execute() throws GeneralException {
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String flag = (String) hm.get("flag");
			String unitcode = (String) hm.get("unitcode1");
			String tabid=(String)hm.get("tabid");	
			String description = "";
			String content1 = "";
			String[] content = null;
			String status = "";
			String  sql = "select description,status from treport_ctrl where unitcode = '"+unitcode+"' and tabid = "+tabid+"";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				description = rs.getString("description");
				description = PubFunc.keyWord_reback(description); //add by wangchaoqun on 2014-9-23
				status = rs.getString("status");
			}
			if(description!=null){
				content = description.split(";");
			
			if("1".equals(flag)){
				if("4".equals(status)|| "1".equals(status)|| "3".equals(status)|| "0".equals(status)){
					for(int i=0;i<content.length;i++){
						content1 = content1 + content[i]+"\n";
					}
				}else if("2".equals(status)|| "0".equals(status)){
					for(int i=0;i<content.length;i++){
						content1 = content1 + content[i]+"\n";
					}
				}

			}
			if("2".equals(flag)){
				if("2".equals(status)|| "0".equals(status)){
					content1 = content1 + content[content.length-1]+"\n";
				}				
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
