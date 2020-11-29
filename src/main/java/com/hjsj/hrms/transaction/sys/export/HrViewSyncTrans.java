package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.sys.export.syncFrigger.CreateSyncFrigger;
import com.hjsj.hrms.utils.SqlDifference;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class HrViewSyncTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		try {
			HrSyncBo hsb = new HrSyncBo(this.frameconn);
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String emporg = (String) this.getFormHM().get("emporg");
			String up = (String) hm.get("up");
			String codefields = hsb.getTextValue(HrSyncBo.CODE_FIELDS);
			if (codefields == null)
				codefields = "";
			hsb.setCodefields(codefields);
			// String orgcodefields = hsb.getTextValue(hsb.ORG_FIELDS);
			// 原来传的是机构指标，应该是翻译指标才对；
			String orgcodefields = hsb.getTextValue(HrSyncBo.ORG_CODE_FIELDS);
			if (orgcodefields == null)
				orgcodefields = "";
			hsb.setOrgcodefields(orgcodefields);
			String postcodefields = hsb.getTextValue(HrSyncBo.POST_CODE_FIELDS);
			if (postcodefields == null)
				postcodefields = "";
			hsb.setPostcodefields(postcodefields);
			String sync_mode = hsb.getAttributeValue(HrSyncBo.SYNC_MODE);
			// if (up == null)
			// up = "yes";
			// 触发器任务
			hsb.createGuidkey();
			if (sync_mode != null && "trigger".equalsIgnoreCase(sync_mode)) {
				hm.remove("up");
				Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.frameconn);
				String uplevel = sysoth
						.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
				if (uplevel == null || uplevel.length() == 0)
					uplevel = "0";
				int nlevel = Integer.parseInt(uplevel);
				if (nlevel > 0) {
					hsb.dropTable("dept_table");
					hsb.setNlevel(nlevel);
					hsb.uplevelDeptTable("dept_table");
					hsb.uplevelDeptTableTriggerMode("dept_table", "UM");
				}
				
				boolean fieldAndCode = false;
				String fieldAndCodeSeq = ":";
				if ("1".equals(hsb.getAttributeValue(HrSyncBo.FIELDANDCODE))){
					fieldAndCode = true;
					fieldAndCodeSeq = hsb.getAttributeValue(HrSyncBo.FIELDANDCODESEQ);
				}
				
				CreateSyncFrigger csf = new CreateSyncFrigger(this.frameconn,
						this.userView, fieldAndCode, fieldAndCodeSeq);
				if ("yes".equalsIgnoreCase(up)) {	
					if (hsb.isSync_a01()) {
						csf.delFrigger(CreateSyncFrigger.HR_FLAG);
						hsb.dropTable("t_hr_view");
						String onlyfield = hsb
								.getAttributeValue(HrSyncBo.HR_ONLY_FIELD);
						isOnlyToField(onlyfield);
						hsb.importHrDataTriggerMode();
						csf.createFrigger(CreateSyncFrigger.HR_FLAG);
					}
					if (hsb.isSync_b01()) {
						csf.delFrigger(CreateSyncFrigger.ORG_FLAG);
						hsb.dropTable("t_org_view");
						hsb.importOrgDataTriggerMode();
						csf.createFrigger(CreateSyncFrigger.ORG_FLAG);
					}
					if (hsb.isSync_k01()) {
						csf.delFrigger(CreateSyncFrigger.POST_FLAG);
						hsb.dropTable("t_post_view");
						hsb.importPostDataTriggerMode();// 机构
						csf.createFrigger(CreateSyncFrigger.POST_FLAG);
					}
					
					if (hsb.isSync_fieldchange()) {
						csf.delFrigger(CreateSyncFrigger.FIELD_FLAG);
						if (hsb.isSync_a01()) {
							
							hsb.dropTable("t_hr_view_log");
							hsb.creatFieldChangeTable("t_hr_view_log");
//							hsb.creatFieldChangeTable();
						}
						
						if (hsb.isSync_b01()) {
							hsb.dropTable("t_org_view_log");
							hsb.creatFieldChangeTable("t_org_view_log");
//							hsb.creatFieldChangeTable("TR_FIELD_CHANGE_T_ORG_VIEW");
						}
						
						if (hsb.isSync_k01()) {
							hsb.dropTable("t_post_view_log");
							hsb.creatFieldChangeTable("t_post_view_log");
//							hsb.creatFieldChangeTable("TR_FIELD_CHANGE_T_POST_VIEW");
						}
						csf.createFrigger(CreateSyncFrigger.FIELD_FLAG);
					}
					
					if (hsb.isSync_photo()) {
						hsb.dropView("t_photo_view");
						
						//获得已选人员库
						String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
						if(dbnamestr==null || dbnamestr.length()<1){
							return;
						}
						String[] dbNames = dbnamestr.split(",");
						hsb.createPhotoView("t_photo_view", dbNames, hsb
								.getAttributeValue(HrSyncBo.HR_ONLY_FIELD));
						csf.createFrigger(CreateSyncFrigger.PHOTO_FLAG);
					}
				} else {
					if ("1".equals(emporg)) {
						csf.delFrigger(CreateSyncFrigger.HR_FLAG);
						hsb.dropTable("t_hr_view");
						String onlyfield = hsb
								.getAttributeValue(HrSyncBo.HR_ONLY_FIELD);
						if (hsb.isSync_a01()) {
							isOnlyToField(onlyfield);
							hsb.importHrDataTriggerMode();
							csf.createFrigger(CreateSyncFrigger.HR_FLAG);
						}
						
						if (hsb.isSync_photo()) {
							hsb.dropView("t_photo_view");
							
							//获得已选人员库
							String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
							if(dbnamestr==null || dbnamestr.length()<1){
								return;
							}
							String[] dbNames = dbnamestr.split(",");
							hsb.createPhotoView("t_photo_view", dbNames, hsb
									.getAttributeValue(HrSyncBo.HR_ONLY_FIELD));
							csf.createFrigger(CreateSyncFrigger.PHOTO_FLAG);
						}
					} else if ("2".equals(emporg)) {
						csf.delFrigger(CreateSyncFrigger.ORG_FLAG);
						hsb.dropTable("t_org_view");
						if (hsb.isSync_b01()) {
							hsb.importOrgDataTriggerMode();
							csf.createFrigger(CreateSyncFrigger.ORG_FLAG);
						}
					} else if ("3".equals(emporg)) {
						csf.delFrigger(CreateSyncFrigger.POST_FLAG);
						hsb.dropTable("t_post_view");
						if (hsb.isSync_k01()) {
							hsb.importPostDataTriggerMode();// 机构
							csf.createFrigger(CreateSyncFrigger.POST_FLAG);
						}
					}
					
					if (hsb.isSync_fieldchange()) {
						if (hsb.isSync_a01()) {
							hsb.creatFieldChangeTable("t_hr_view_log");
//							hsb.creatFieldChangeTable();
						}
						
						if (hsb.isSync_b01()) {
							hsb.creatFieldChangeTable("t_org_view_log");
//							hsb.creatFieldChangeTable("TR_FIELD_CHANGE_T_ORG_VIEW");
						}
						
						if (hsb.isSync_k01()) {
							hsb.creatFieldChangeTable("t_post_view_log");
//							hsb.creatFieldChangeTable("TR_FIELD_CHANGE_T_POST_VIEW");
						}
						csf.createFrigger(CreateSyncFrigger.FIELD_FLAG);
					}
				}
			} else {
				if (up == null)
					up = "yes";
				if ("yes".equalsIgnoreCase(up)) {
					Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(
							this.frameconn);
					String uplevel = sysoth
							.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
					if (uplevel == null || uplevel.length() == 0)
						uplevel = "0";
					int nlevel = Integer.parseInt(uplevel);
					if (nlevel > 0) {
						hsb.dropTable("dept_table");
						hsb.setNlevel(nlevel);
						hsb.uplevelDeptTable("dept_table");
					}
					// 定时任务
					if (emporg == null) {
						// 删除初始表
						hsb.dropTable("t_org_view");
						hsb.dropTable("t_org_view_t");
						hsb.dropTable("t_hr_view");
						hsb.dropTable("t_hr_view_t");
						hsb.dropTable("t_user_view");
						hsb.dropView("t_photo_view");
						hsb.dropTable("t_organization_view");
						hsb.operUserSynchronization();// 业务人员
						hsb.organizationSynchronization();// 组织机构视图
						hsb.importOrgData("t_org_view");// 机构
						hsb.importData("t_hr_view");// 人员
						hsb.dropTable("t_post_view");
						hsb.dropTable("t_post_view_t");
						hsb.importPostData("t_post_view");// 机构
					} else if ("1".equals(emporg)) {
						hsb.dropTable("t_hr_view");
						hsb.dropTable("t_hr_view_t");
						hsb.dropTable("t_user_view");
						hsb.dropView("t_photo_view");
						hsb.operUserSynchronization();// 业务人员
						hsb.importData("t_hr_view");// 人员

					} else if ("2".equals(emporg)) {
						hsb.dropTable("t_org_view");
						hsb.dropTable("t_org_view_t");
						hsb.dropTable("t_organization_view");
						hsb.organizationSynchronization();// 组织机构视图
						hsb.importOrgData("t_org_view");// 机构
					} else if ("3".equals(emporg)) {
						hsb.dropTable("t_post_view");
						hsb.dropTable("t_post_view_t");
						hsb.importPostData("t_post_view");// 机构
					}
					
					boolean fieldAndCode = false;
					String fieldAndCodeSeq = ":";
					if ("1".equals(hsb.getAttributeValue(HrSyncBo.FIELDANDCODE))){
						fieldAndCode = true;
						fieldAndCodeSeq = hsb.getAttributeValue(HrSyncBo.FIELDANDCODESEQ);
					}
					CreateSyncFrigger csf = new CreateSyncFrigger(this.frameconn,
							this.userView, fieldAndCode, fieldAndCodeSeq);
					
					
					if (hsb.isSync_a01()) {
						hsb.importHrDataTriggerMode();
					}
					if (hsb.isSync_b01()) {
						hsb.importOrgDataTriggerMode();
					}
					if (hsb.isSync_k01()) {
						hsb.importPostDataTriggerMode();// 机构
					}
					
					if (hsb.isSync_fieldchange()) {
						csf.delFrigger(CreateSyncFrigger.FIELD_FLAG);
						if (hsb.isSync_a01()) {
							
							hsb.dropTable("t_hr_view_log");
							hsb.creatFieldChangeTable("t_hr_view_log");
						}
						
						if (hsb.isSync_b01()) {
							hsb.dropTable("t_org_view_log");
							hsb.creatFieldChangeTable("t_org_view_log");
						}
						
						if (hsb.isSync_k01()) {
							hsb.dropTable("t_post_view_log");
							hsb.creatFieldChangeTable("t_post_view_log");
						}
						csf.createFrigger(CreateSyncFrigger.FIELD_FLAG);
					}
					
					if (hsb.isSync_photo()) {
						hsb.dropView("t_photo_view");
						
						//获得已选人员库
						String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
						if(dbnamestr==null || dbnamestr.length()<1){
							return;
						}
						String[] dbNames = dbnamestr.split(",");
						hsb.createPhotoView("t_photo_view", dbNames, hsb
								.getAttributeValue(HrSyncBo.HR_ONLY_FIELD));
					}
										
				}
			}

		} catch (GeneralException e) {
			throw e;
		} catch (Exception ex) {
			ex.printStackTrace();

			String error = ex.toString();
			if (error != null && error.indexOf("无法将 NULL 插入") != -1) {
				throw new GeneralException("您设置唯一性指标，个别信息为空，请确认修改后同步!");
			} else
				throw new GeneralException("您还未设置同步信息，请先设置同步信息!");

		}

		/*
		 * String dbnamestr = hsb.getTextValue(hsb.BASE);
		 * 
		 * String fieldstr=hsb.getTextValue(hsb.FIELDS);
		 * hsb.HrSync(dbnamestr,fieldstr);
		 */
	}

	public void delete(ContentDAO dao) {

		StringBuffer sb = new StringBuffer();
		sb.append(" delete t_hr_view ");
		// System.out.println(sb.toString());
		try {
			dao.update(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void HrSync(String dbname, String fieldstr) {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		this.delete(dao);
		ArrayList dblist = new ArrayList();
		if (!(dbname == null || "".equals(dbname))) {
			dblist = this.getHrDB(dbname);
		} else {
			dblist.add("usr");
		}
		this.insertRecord(dao, dblist);
		this.getDbnames(dbname, fieldstr, dao);
	}

	public void updateHr(String dbname, String fieldstr, ContentDAO dao) {

		StringBuffer sb = new StringBuffer();
		FieldItem fi = DataDictionary.getFieldItem(fieldstr);
		if (!(fi == null || "a01".equalsIgnoreCase(fi.getFieldsetid())))// 不是主集指标
		{
			String table = fi.getFieldsetid();
			sb.append(" update t_hr_view ");
			sb.append(" set " + fieldstr + "= ");
			sb.append(" (select " + fieldstr + " ");
			sb.append(" from " + dbname + table + " ");
			sb.append(" where a0100=t_hr_view.a0100");
			sb.append(" and i9999=");
			sb.append(" (select max(i9999) ");
			sb.append(" from " + dbname + table + " ");
			sb.append(" where a0100=t_hr_view.a0100");
			sb.append(" ))");
			sb.append(" ");
			sb.append(" where nbase_0 like '" + dbname + "'");
		} else {
			sb.append(" update t_hr_view ");
			sb.append(" set " + fieldstr + "= ");
			sb.append(" (select " + fieldstr + " ");
			sb.append(" from " + dbname + "a01 ");
			sb.append(" where a0100=t_hr_view.a0100)");
			sb.append(" where nbase_0 like '" + dbname + "'");
		}
		// System.out.println(sb.toString());
		try {
			dao.update(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void getDbnames(String dbnamestr, String fieldstr, ContentDAO dao) {
		if (!(dbnamestr == null || "".equals(dbnamestr))) {
			String[] dbname = dbnamestr.split(",");
			for (int i = 0; i < dbname.length; i++) {
				this.getFields(dbname[i], fieldstr, dao);
			}
		} else {
			this.getFields("usr", fieldstr, dao);
		}

	}

	public void getFields(String dbname, String fieldstr, ContentDAO dao) {
		if (!(fieldstr == null || "".equals(fieldstr))) {
			String[] fields = fieldstr.split(",");
			for (int i = 0; i < fields.length; i++) {
				this.updateHr(dbname, fields[i], dao);
			}
		}
	}

	public ArrayList getHrDB(String str) {
		ArrayList list = new ArrayList();
		String[] db = str.split(",");
		for (int i = 0; i < db.length; i++) {
			if (!(db[i] == null || "".equals(db[i]))) {
				list.add(db[i]);
			}
		}
		return list;
	}

	public boolean insertRecord(ContentDAO dao, ArrayList dblist) {
		boolean ret = false;
		StringBuffer sql = new StringBuffer();
		String dbname = "";
		try {
			for (int i = 0; i < dblist.size(); i++) {
				dbname = (String) dblist.get(i);
				sql.append("insert into t_hr_view");
				sql.append("(nbase,nbase_0,A0100,");
				sql.append("B0110_0,E0122_0,A0101,E01A1_0,");
				sql.append("username,userpassword,flag)");
				sql.append("( select '" + dbname + "','" + dbname + "',");
				sql.append(" A0100,B0110,E0122,A0101,E01A1,");
				sql.append("username,userpassword,0 from " + dbname + "a01)");
				sql.append(" ");
				// System.out.println(sql.toString());
				dao.update(sql.toString());
				sql.delete(0, sql.length());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private void isOnlyToField(String onlyfield) throws GeneralException {
		if (onlyfield == null || onlyfield.length() < 1) {
			return;
		}
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
		if (dbnamestr == null || dbnamestr.length() < 1) {
			return;
		}
		String[] dbnames = dbnamestr.split(",");
		StringBuffer sqlinner = new StringBuffer();
		StringBuffer table = new StringBuffer();
		for (int i = 0; i < dbnames.length; i++) {
			table.append(dbnames[i] + "A01,");
			sqlinner.append("SELECT " + onlyfield + " FROM " + dbnames[i]
					+ "A01 WHERE ");
			sqlinner.append(SqlDifference.isNotNull(onlyfield));
			sqlinner.append(" UNION ALL ");
		}
		table.deleteCharAt(table.length() - 1);
		sqlinner.delete(sqlinner.length() - 10, sqlinner.length() - 1);
		String sql = "SELECT " + onlyfield + " FROM (" + sqlinner.toString()
				+ ") A GROUP BY " + onlyfield + " HAVING COUNT(" + onlyfield
				+ ") > 1";

		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				// throw new GeneralException("设置的唯一字段在表" + table.toString()
				// + "中有的值不唯一。");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}
}
