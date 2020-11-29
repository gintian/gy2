package com.hjsj.hrms.transaction.report.report_isApprove;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class Report_isApproveTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String relation_id = sysbo.getValue(Sys_Oth_Parameter.APPROVEID);
		String username = SafeCode.decode((String) hm.get("username"));
		hm.remove("username");
		String sql = "";
		if(username==null|| "".equals(username)){
			 sql = "select mainbody_id,a0101 from t_wf_mainbody where relation_id = '"+relation_id+"' and object_id = '"+this.getUserView().getUserName()+"' and sp_grade = '9'";
		}else{
			if(username.equals(this.getUserView().getUserName())){
				sql = "select mainbody_id,a0101 from t_wf_mainbody where relation_id = '"+relation_id+"' and object_id = '"+this.getUserView().getUserName()+"' and sp_grade = '9'";
			}else{
				sql = "select mainbody_id,a0101 from t_wf_mainbody where relation_id = '"+relation_id+"' and object_id = '"+username+"' and sp_grade = (select min(sp_grade) from t_wf_mainbody where sp_grade>(select sp_grade from t_wf_mainbody where relation_id = "+relation_id+" and object_id = '"+username+"' and mainbody_id = '"+this.getUserView().getUserName()+"'))";
			}		
		}	 
		RowSet rs = dao.search(sql);
		ArrayList list = new ArrayList();
		LazyDynaBean bean = null;
		while(rs.next()){
			bean = new LazyDynaBean();
			String mainbody_id = rs.getString("mainbody_id");
			String a0101 = rs.getString("a0101");
			if(a0101==null|| "".equals(a0101)){
				a0101 = mainbody_id;
			}
			
			bean.set("mainbody_id", mainbody_id);
			bean.set("a0101", a0101);
			list.add(bean);
		}
		this.getFormHM().put("list", list);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
