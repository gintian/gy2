package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Hashtable;

public class IsHaveUnitsTrans extends IBusiness{
	public void execute()throws GeneralException{
		String unitcode=(String)this.getFormHM().get("unitcode");
		if(unitcode.indexOf("UN")!=-1||unitcode.indexOf("UM")!=-1){
			unitcode=unitcode.substring(2);
		}else{
			
		}
		try {
			AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
			appb.init();
			appb.setReturnHt(null);
			Hashtable ht=appb.analyseParameterXml();
			String pointset_menu=(String)ht.get("pointset_menu");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql="select codeitemid ,codeitemdesc from  organization where parentid like'"+unitcode+"%' and codesetid<>'@K' and codeitemid not in (select distinct(b0110) from "+pointset_menu+") order by codeitemid";
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				this.getFormHM().put("parameters", "ok");
			}else{
				this.getFormHM().put("parameters", "no");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
