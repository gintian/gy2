package com.hjsj.hrms.transaction.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class IsVorganizationTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList orgcodeitemid=(ArrayList)this.getFormHM().get("orgcodeitemid");
		if(orgcodeitemid==null){
			return;
		}
		for(int i=0;i<orgcodeitemid.size();i++){
			String code = (String)orgcodeitemid.get(i);
			if(code!=null&&code.length()>0){
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				try {
					this.frecset = dao.search("select * from organization where codeitemid='"+code+"'");
					if(!this.frecset.next()){
						throw GeneralExceptionHandler.Handle(new GeneralException("","虚拟机构不许复制，操作失败！","",""));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
