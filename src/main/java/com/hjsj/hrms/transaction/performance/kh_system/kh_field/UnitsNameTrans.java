package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class UnitsNameTrans extends IBusiness{
	public void execute()throws GeneralException{
		
		String flag=(String)this.getFormHM().get("plag");
		String unitocode=(String)this.getFormHM().get("unticode");
		if("1".equals(flag)){
			String copypoints=(String)this.getFormHM().get("copypoints");
			String unitname=this.rtname(unitocode);
			this.getFormHM().put("unitname", unitname);
			this.getFormHM().put("copypoints", copypoints);
		}else{
			String copyorg=(String)this.getFormHM().get("copyorg");
			String copyname=this.rtname(copyorg);
			String unitname=this.rtname(unitocode);
			this.getFormHM().put("unitname", unitname);
			this.getFormHM().put("copyname", copyname);
			this.getFormHM().put("copyorg", copyorg);
		}
		this.getFormHM().put("plag", flag);
	}
	public String rtname(String unitcode){
		String unitname="";
		if(unitcode.indexOf("UN")!=-1||unitcode.indexOf("UM")!=-1){
			unitcode=unitcode.substring(2);
		}
		String sql="select * from organization where codeitemid='"+unitcode+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				unitname=this.frowset.getString("codeitemdesc");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return unitname;
	}
}
