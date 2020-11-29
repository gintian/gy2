/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:查询功能列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 29, 2008:3:15:01 PM</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class EditStaticStatementTrans extends IBusiness {
    /**
	 */
	
	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
	String scopeid = (String)hm.get("scopeid");
	hm.remove("scopeid");
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	RecordVo vo = new RecordVo("tscope");
	vo.setInt("scopeid", Integer.parseInt(scopeid));
	try {
	vo =dao.findByPrimaryKey(vo);
	this.getFormHM().put("scopeid", scopeid);
	this.getFormHM().put("scopename", vo.getString("name"));
	String scopeownerunitid = vo.getString("owner_unit");
	this.getFormHM().put("scopeownerunitid", scopeownerunitid);
	if(scopeownerunitid.indexOf("UN")!=-1||scopeownerunitid.indexOf("UM")!=-1){
	this.getFormHM().put("scopeownerunit", AdminCode.getCodeName(scopeownerunitid.substring(0,2),scopeownerunitid.substring(2,scopeownerunitid.length())));
	}
	this.getFormHM().put("scopeunitsids", vo.getString("units"));
	String scopeunits = vo.getString("units");
	String scopeunits2[] = scopeunits.split("`");
	String scopeunitsname = "";
	for(int i=0;i<scopeunits2.length;i++){
		if(scopeunits2[i].indexOf("UN")!=-1||scopeunits2[i].indexOf("UM")!=-1){
			scopeunitsname+=AdminCode.getCodeName(scopeunits2[i].substring(0,2),scopeunits2[i].substring(2,scopeunits2[i].length()))+",";
		}
	}
	this.getFormHM().put("scopeunits", scopeunitsname);
		
	} catch (SQLException e) {
		e.printStackTrace();
	}

	}
	
}
