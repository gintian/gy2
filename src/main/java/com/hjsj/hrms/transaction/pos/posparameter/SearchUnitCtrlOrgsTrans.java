package com.hjsj.hrms.transaction.pos.posparameter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;
 


public class SearchUnitCtrlOrgsTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		  Map hm = (Map)this.getFormHM().get("requestPamaHM");
		  String ctrlOrg = hm.get("ctrlorg").toString();
		  ctrlOrg = ctrlOrg.replaceAll("UN", "");
		  ctrlOrg = ctrlOrg.replaceAll("UM", "");
		  String orgdesc = getOrgDesc(ctrlOrg);
		  this.getFormHM().put("controlOrgDesc", orgdesc);
	}
	
	/**
	 * 查询机构名称
	 * @param orgids 机构id以逗号隔开,just like: 0101,0102,
	 * @return 机构名称, example:集团总部，直属单位，
	 */
	public String getOrgDesc(String orgids){
		String orgdesc="";
		StringBuffer sql= new StringBuffer("select codeitemdesc from organization where codeitemid in (");
		String[] codeid = orgids.split(",");
		for(int i=0;i<codeid.length;i++){
			sql.append("'"+codeid[i]+"',");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		ContentDAO dao = new ContentDAO(frameconn);
		try{
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				orgdesc+=this.frowset.getString("codeitemdesc")+",";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return orgdesc;
	}
}