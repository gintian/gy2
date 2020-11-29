package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.businessobject.info.OrgInfoUtils;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.valueobject.common.OrganizationView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class DelDutyInfoTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String codeitemid = (String) this.getFormHM().get("codeitemid");
		if (codeitemid == null || codeitemid.length() <= 0)
			throw GeneralExceptionHandler.Handle(new GeneralException(
					"得到组织单元编号为空，错误！"));
		else{
			//检查 是否越权访问 gdd 14-09-24 
			String privcode = new CheckPrivSafeBo(frameconn, userView).checkOrg(codeitemid, "4");
			if(!privcode.equals(codeitemid))
				throw GeneralExceptionHandler.Handle(new Exception("您没有访问权限！"));
		}
		String rownum = (String) this.getFormHM().get("rownum");
		String delpersonorg = (String) this.getFormHM().get("delpersonorg");
		String orgid = (String) this.getFormHM().get("orgid");
		this.getFormHM().put("rownum", rownum);
		String flag = "";
		OrgInfoUtils orgInfoUtils = new OrgInfoUtils(this.getFrameconn());
		ArrayList peopleOrgList = new ArrayList();// 人员变动前的机构 xuj 2010-4-28
		String codesetid = "@K";
		OrganizationView orgview = new OrganizationView();
		orgview.setCodesetid(codesetid);
		orgview.setCodeitemid(codeitemid);
		peopleOrgList.add(orgview);
		this.getFormHM().put("peopleOrg", "delete");
		this.getFormHM().put("peopleOrgList", peopleOrgList);
		this.peopleOrgChange();
		boolean isCorrect = orgInfoUtils.delOrgTrans(codeitemid, orgid,
				delpersonorg);
		if (isCorrect){
			//更新兼职状态，如果启用兼职并且有人员兼职此机构，则将任免标识改为 不在任  14-11-25 guodd
  		   String sql = new DbNameBo(frameconn).getUpdateJZSql(codeitemid);
            if(sql.length()>3 && sql != null){
         	   ContentDAO dao = new ContentDAO(frameconn);
         	   try {
					dao.update(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
            }
			
			flag = "ok";
		}
		this.getFormHM().put("flag", flag);
	}
	 /**
	 * 人员变动前的机构记录到选择的模板
	 * @throws GeneralException
	 */
	private void peopleOrgChange() throws GeneralException{
		try{
			String peopleOrg = (String) this.getFormHM().get("peopleOrg");
			ArrayList peopleOrgList = (ArrayList) this.getFormHM().get(
					"peopleOrgList");
			if (peopleOrg == null || "".equals(peopleOrg)
					|| peopleOrgList == null || peopleOrgList.size() == 0) {
				return;
			}
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
			String tempid = "";
				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						peopleOrg);
				if (tempid == null || "".equals(tempid))
					return;
			StringBuffer sql = new StringBuffer();
			ArrayList dblist = DataDictionary.getDbpreList();
			ContentDAO dao = new ContentDAO(this.frameconn);
			int nyear = 0;
			int nmonth = 0;
			nyear = DateUtils.getYear(new Date());
			nmonth = DateUtils.getMonth(new Date());
			RecordVo vo = new RecordVo("tmessage");
			vo.setString("username", "");
			vo.setInt("state", 0);
			vo.setInt("nyear", nyear);
			vo.setInt("nmonth", nmonth);
			vo.setInt("type", 0);
			vo.setInt("flag", 0);
			vo.setInt("sourcetempid", 0);
			vo.setInt("noticetempid", Integer.parseInt(tempid));
			StringBuffer changepre = new StringBuffer();
			StringBuffer change = new StringBuffer();
			for (int i = 0; i < peopleOrgList.size(); i++) {
				OrganizationView orgview = (OrganizationView) peopleOrgList
						.get(i);
				String codesetid = orgview.getCodesetid();
				String codeitemid = orgview.getCodeitemid();
				for (int n = 0; n < dblist.size(); n++) {
					String pre = (String) dblist.get(n);
					sql.setLength(0);
					sql.append("select a0100,a0101,b0110,e0122,e01a1 from "
							+ pre + "A01 where ");
					if ("UN".equalsIgnoreCase(codesetid)) {
						sql.append("b0110 like '" + codeitemid + "%'");
					} else if ("UM".equalsIgnoreCase(codesetid)) {
						sql.append("e0122 like '" + codeitemid + "%'");
					} else if ("@K".equalsIgnoreCase(codesetid)) {
						sql.append("e01a1 ='" + codeitemid + "'");
					}
					this.frowset = dao.search(sql.toString());
					vo.setString("db_type", pre);
					while (this.frowset.next()) {
						String a0100 = this.frowset.getString("a0100");
						String a0101 = this.frowset.getString("a0101");
						a0101 = a0101 != null ? a0101 : "";
						String b0110 = this.frowset.getString("b0110");
						String e0122 = this.frowset.getString("e0122");
						String e01a1 = this.frowset.getString("e01a1");
						vo.setString("a0100", a0100);
						vo.setString("a0101", a0101);
						changepre.setLength(0);
						change.setLength(0);
						if (b0110 != null && !"".equals(b0110)) {
							changepre.append("B0110=" + b0110 + ",");
							change.append("B0110,");
						}
						if (e0122 != null && !"".equals(e0122)) {
							changepre.append("E0122=" + e0122 + ",");
							change.append("E0122,");
						}
						if (e01a1 != null && !"".equals(e01a1)) {
							changepre.append("E01A1=" + e01a1 + ",");
							change.append("E01A1,");
						}
						if (a0101 != null && !"".equals(a0101)) {
							changepre.append("A0101=" + a0101 + ",");
							change.append("A0101,");
						}
						vo.setString("changepre", changepre.toString());
						vo.setString("change", change.toString());
						/** max id access mssql此字段是自增长类型 */
						if (Sql_switcher.searchDbServer() != Constant.MSSQL) {
							int nid = DbNameBo.getPrimaryKey("tmessage", "id",
									this.frameconn);
							vo.setInt("id", nid);
						}
						dao.addValueObject(vo);
					}
				}
			}
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
