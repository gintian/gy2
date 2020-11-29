package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchTrainAddTreeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try {
			//培训班/培训资源评估：新增---资源名称
			String classid=(String)this.getFormHM().get("classid");
			classid = classid==null?"":classid;
			String r3702=(String)this.getFormHM().get("r3702");
			r3702 = r3702==null?"":r3702;
			String name = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("name")));
			name = name==null?"":name;
			String xmlsql = getSqlByClassId(classid,r3702);
			String itemdesc = "";
			String itemid = "";
			ArrayList desclist = new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(!"".equals(xmlsql)){
				this.frowset = dao.search(xmlsql);
				while(this.frowset.next()){
					CommonData objvo = new CommonData();
					itemdesc = (String)this.frowset.getString("codeitemdesc");
					itemid = (String)this.frowset.getString("codeitemid");
					objvo.setDataName(itemdesc);
					objvo.setDataValue(itemid);
					desclist.add(objvo);
				}
			}
			this.getFormHM().put("namelist", desclist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getSqlByClassId(String classid, String r3702) {
		String strSql = "";
		if (r3702 != null && !"".equals(r3702)){
			if ("01".equals(r3702))// 教师
				strSql = "Select a.R0401 as codeitemid,a.R0402 as codeitemdesc from R04 a,R41 b Where a.R0401=b.R4106 and b.R4103='" + classid + "'";
			else if ("02".equals(r3702))// 机构
				strSql = "Select a.R0101 as codeitemid,a.R0102 as codeitemdesc from R01 a,R31 b Where a.R0101=b.R3128 and b.R3101='" + classid + "'";
			else if ("03".equals(r3702))// 资料
				strSql = "Select a.R0701 as codeitemid,a.R0702 as codeitemdesc from R07 a,R41 b Where a.R0701=b.R4114 and b.R4103='" + classid + "'";
			else if ("04".equals(r3702))// 场所
				strSql = "Select a.R1001 as codeitemid,a.R1011 as codeitemdesc from R10 a,R31 b Where a.R1001=b.R3126 and b.R3101='" + classid + "'";
			else if ("05".equals(r3702))// 项目
				strSql = "Select a.R1301 as codeitemid,a.R1302 as codeitemdesc from R13 a,R41 b Where a.R1301=b.R4105 and b.R4103='" + classid + "'";
		}
        return strSql; 
	}
}
