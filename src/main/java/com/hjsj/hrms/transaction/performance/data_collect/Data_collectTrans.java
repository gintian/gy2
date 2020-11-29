package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.performance.data_collect.Data_collectBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class Data_collectTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm =(HashMap) this.getFormHM().get("requestPamaHM");
		String fieldsetid =(String) hm.get("fieldsetid");
		String[] dbid =(String[]) this.getFormHM().get("dbid");
		String cexpr="";
		String personScope="-1";
		Data_collectBo dbo=new Data_collectBo();
		Connection conn = this.getFrameconn();
		ArrayList ValueList = dbo.getXmlValue(conn);
		HashMap VMap = new HashMap();
		for(int i=0;i<ValueList.size();i++){
			HashMap temMap = (HashMap) ValueList.get(i);
			if(fieldsetid.equals((String)temMap.get("set_id"))){
				VMap=temMap;
				break;
			}
		}
		if(!(VMap.get("dbid")==null||"".equals(VMap.get("dbid")))){
			dbid= ((String)VMap.get("dbid")).split(",");
		}else{
			dbid=new String[0];
		}
		cexpr=(String) VMap.get("cexpr");
		personScope=(String)VMap.get("flag");
		ArrayList auditList =dbo.getAudit(fieldsetid, conn);
		ArrayList dbList = dbo.getDbList(dbid, conn);
		this.getFormHM().put("cexpr",cexpr);
		this.getFormHM().put("auditList", auditList);
		this.getFormHM().put("dbList", dbList);
		this.getFormHM().put("dbid", dbid);
		this.getFormHM().put("personScope", personScope);
		this.getFormHM().put("fieldsetid", fieldsetid);
	}

}
