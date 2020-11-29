package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveBusi_org_deptAjaxTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String username = (String)this.getFormHM().get("username");
		String busi_org_dept = (String)this.getFormHM().get("return_v");
			if(username!=null&&username.length()>0){
				String sql = "update operuser set busi_org_dept=? where username='"+username+"'";
		
				try{
					ContentDAO dao = new ContentDAO(this.frameconn);
					ArrayList list = new ArrayList();
					list.add(busi_org_dept);	
					dao.update(sql,list);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					
				}
			}

	}

}
