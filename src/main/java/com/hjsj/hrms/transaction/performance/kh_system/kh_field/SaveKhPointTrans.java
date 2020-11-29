package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Hashtable;

public class SaveKhPointTrans extends IBusiness{
	public void execute()throws GeneralException{
		String orgpoint=(String)this.getFormHM().get("orgpoint");
		String khpid=(String)this.getFormHM().get("khpid");
		String khpname=(String)this.getFormHM().get("khpname");
		AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.getFrameconn());
		if("-1".equals(orgpoint)&&"-1".equals(khpid)&&"-1".equals(khpname))
			appb.restore();
		else {
			String showmenus=(String)this.getFormHM().get("showmenus");
			if(showmenus==null||showmenus.length()==0){
				
			}else{
				showmenus=showmenus.substring(0,showmenus.length()-1);
			}
			appb.init();
			appb.setReturnHt(null);
			Hashtable ht=appb.analyseParameterXml();
			String pointset_menu=(String)ht.get("pointset_menu");
			DbWizard dbwizard=new DbWizard(this.getFrameconn());
			if(dbwizard.isExistTable("orgpointtable",false)){
				if(!pointset_menu.equalsIgnoreCase(orgpoint)){
					String sql="drop table orgpointtable";
					dbwizard.execute(sql);
				}else {
					DBMetaModel var2 = new DBMetaModel();
					var2.setConn(this.frameconn);
					var2.reloadTableModel("orgpointtable");
				}
			}
			HashMap hm=new HashMap();
			hm.put("showmenus", showmenus);
			hm.put("pointcode_menu", khpid);
			hm.put("pointname_menu", khpname);
			String nodename="ORG_POINT";
			appb.init();
			appb.setParam(nodename, orgpoint, hm);
		}
		appb.saveParam();
	}
}
