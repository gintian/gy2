package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.sys.export.syncFrigger.CreateSyncFrigger;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 保存数据视图配置
 * <p>
 * Title:SaveHrSyncTrans.java
 * </p>
 * <p>
 * Description>:SaveHrSyncTrans.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Oct 9, 2010 9:47:34 AM
 * </p>
 * <p>
 * 
 * @version: 5.0
 *           </p>
 *           <p>
 * @author: s.xin
 */
public class SaveHrSyncTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String isSync = (String) hm.get("isSync");
		String sync_field = (String) this.getFormHM().get("sync_field");
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		hsb.setAttributeValue(HrSyncBo.KEY_FIELd, sync_field);
		// String code_value = (String)this.getFormHM().get("code_value");
		// hsb.setAttributeValue(HrSyncBo.CODE,code_value);
		String code_value = (String) this.getFormHM().get("code_value");
		String sync_A01 = (String) this.getFormHM().get("sync_A01");
		String sync_B01 = (String) this.getFormHM().get("sync_B01");
		String sync_K01 = (String) this.getFormHM().get("sync_K01");
		String sync_mode = (String) this.getFormHM().get("sync_mode");
		String fail_limit = (String) this.getFormHM().get("fail_limit");
		
		String jz_field = (String) this.getFormHM().get("jz_field");//同步兼职
		String photo = (String) this.getFormHM().get("photo");// 同步照片
		photo = photo == null ||photo.length() < 1 ? "0" : photo;
		
		// 跟踪指标前后变化信息
		String fieldChange = (String) this.getFormHM().get("fieldChange");
		fieldChange = fieldChange == null || fieldChange.length() < 1 ? "0" : fieldChange;
		
		// 翻译指标包含代码
		String fieldAndCode = (String) this.getFormHM().get("fieldAndCode");
		fieldAndCode = fieldAndCode == null || fieldAndCode.length() < 1 ? "0" : fieldAndCode;
		
		// 分隔符
		String fieldAndCodeSeq = (String) this.getFormHM().get("fieldAndCodeSeq");
		//数据视图，同步参数设置，分隔符还原  jingq add 2014.09.22
		fieldAndCodeSeq = PubFunc.keyWord_reback(fieldAndCodeSeq);
		fieldAndCodeSeq = fieldAndCodeSeq == null || fieldAndCodeSeq.length() < 1 ? ":" : fieldAndCodeSeq;
		
		jz_field = jz_field == null || jz_field.length()<1 ? "0" : jz_field;
		if("1".equals(jz_field)){
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			/**兼职参数*/
			String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
			if(!"true".equalsIgnoreCase(flag)){
				throw new GeneralException("设置兼职前，请先在参数设置中启用参数设置！");
			}
			String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
			if(setid == null || setid.length() < 1){
				throw new GeneralException("设置兼职前，请先在参数设置中设置兼职子集表！");
			}
			/**兼职单位字段*/
			String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
			if(unit_field == null || unit_field.length() < 1){
				throw new GeneralException("设置兼职前，请先在参数设置中设置兼职单位！");
			}
			String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");	
			if(dept_field == null || dept_field.length() < 1){
				throw new GeneralException("设置兼职前，请先在参数设置中设置兼职部门！");
			}
		}
		hsb.setAttributeValue(HrSyncBo.JZ_FIELD, jz_field);
		
		if (!(code_value == null || "".equals(code_value))) {
			if ("on".equalsIgnoreCase(code_value)
					|| "1".equalsIgnoreCase(code_value))
				hsb.setAttributeValue(HrSyncBo.CODE, "1");
			else
				hsb.setAttributeValue(HrSyncBo.CODE, "0");
		}
		if (!(sync_A01 == null || "".equals(sync_A01))) {
			if ("1".equalsIgnoreCase(sync_A01))
				hsb.setAttributeValue(HrSyncBo.SYNC_A01, "1");
			else
				hsb.setAttributeValue(HrSyncBo.SYNC_A01, "0");
		}
		if (!(sync_B01 == null || "".equals(sync_B01))) {
			if ("1".equalsIgnoreCase(sync_B01))
				hsb.setAttributeValue(HrSyncBo.SYNC_B01, "1");
			else
				hsb.setAttributeValue(HrSyncBo.SYNC_B01, "0");
		}
		if (!(sync_K01 == null || "".equals(sync_K01))) {
			if ("1".equalsIgnoreCase(sync_K01))
				hsb.setAttributeValue(HrSyncBo.SYNC_K01, "1");
			else
				hsb.setAttributeValue(HrSyncBo.SYNC_K01, "0");
		}
			if ("1".equalsIgnoreCase(photo))
				hsb.setAttributeValue(HrSyncBo.photo, "1");
			else
				hsb.setAttributeValue(HrSyncBo.photo, "0");
			
		// 跟踪指标变化前后信息	
		if ("1".equals(fieldChange)) {
			hsb.setAttributeValue(HrSyncBo.FIELDCHANGE, "1");
		} else {
			hsb.setAttributeValue(HrSyncBo.FIELDCHANGE, "0");
		}
		
		// 翻译指标含代码
		if ("1".equals(fieldAndCode)) {
			hsb.setAttributeValue(HrSyncBo.FIELDANDCODE, "1");
			hsb.setAttributeValue(HrSyncBo.FIELDANDCODESEQ, fieldAndCodeSeq);
			
		} else {
			hsb.setAttributeValue(HrSyncBo.FIELDANDCODE, "0");
			hsb.setAttributeValue(HrSyncBo.FIELDANDCODESEQ, fieldAndCodeSeq);
		}
		
		hsb.setFieldAndCode(fieldAndCode);
		hsb.setFieldAndCodeSeq(fieldAndCodeSeq);
		
		sync_mode = sync_mode != null && sync_mode.trim().length() > 0 ? sync_mode
				: "time_job";// 定时任务：time_job；触发器：trigger
		hsb.setAttributeValue(HrSyncBo.SYNC_MODE, sync_mode);
		fail_limit = fail_limit != null && fail_limit.trim().length() > 0 ? fail_limit
				: "";// 失败次数上限
		hsb.setAttributeValue(HrSyncBo.FAIL_LIMIT, fail_limit);
		hsb.saveParameter(dao);
		this.getFormHM().put("code_value", code_value);
		hsb = new HrSyncBo(this.frameconn);
		String dbtemp = hsb.getTextValue(HrSyncBo.BASE);
		String dbname = "";
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
		// 设置照片指标
		hsb.setPhotoFields("ext");
		
		// 翻译指标含代码
		boolean booleaFieldAndCode = false;
		if ("1".equals(fieldAndCode)) {
			booleaFieldAndCode = true;
		} 
		/*保存数据视图参数时，更新视图指标，移除被删除的指标   wangb  20171211 33246*/
		hsb.updateFieldAndCodeField(dao,HrSyncBo.FIELDS,HrSyncBo.CODE_FIELDS);
		hsb.updateFieldAndCodeField(dao,HrSyncBo.ORG_FIELDS,HrSyncBo.ORG_CODE_FIELDS);
		hsb.updateFieldAndCodeField(dao,HrSyncBo.POST_FIELDS,HrSyncBo.POST_CODE_FIELDS);
		
		CreateSyncFrigger csf = new CreateSyncFrigger(this.frameconn,
				this.userView, booleaFieldAndCode, fieldAndCodeSeq);
		csf.delFrigger(CreateSyncFrigger.HR_FLAG);
		csf.delFrigger(CreateSyncFrigger.ORG_FLAG);
		csf.delFrigger(CreateSyncFrigger.POST_FLAG);
		csf.delFrigger(CreateSyncFrigger.PHOTO_FLAG);
		// 删除指标变化前后信息的触发器
		csf.delFrigger(CreateSyncFrigger.FIELD_FLAG);
		hsb.createGuidkey();
		
		csf.createFun();
		
		
		
		
		if ("1".equals(isSync)) {
			if (sync_mode != null && !"trigger".equalsIgnoreCase(sync_mode)) {
				hsb.dropTable("t_hr_view");
				hsb.dropTable("t_org_view");
				hsb.dropTable("t_post_view");
				hsb.dropView("t_photo_view");
				
				if ("1".equals(sync_A01)) {
					hsb.dropTable("t_hr_view_t");
					hsb.dropTable("t_user_view");
					hsb.operUserSynchronization();// 业务人员
					hsb.importData("t_hr_view");// 人员
				}
				if ("1".equals(sync_B01)) {
					hsb.dropTable("t_org_view_t");
					hsb.dropTable("t_organization_view");
					hsb.organizationSynchronization();// 组织机构视图
					hsb.importOrgData("t_org_view");// 机构
				}
				if ("1".equals(sync_K01)) {
					hsb.dropTable("t_post_view_t");
					hsb.importPostData("t_post_view");// 机构
				}
				if ("1".equals(photo)) {
					hsb.dropView("t_photo_view");
					
					//获得已选人员库
					String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
					if(dbnamestr==null || dbnamestr.length()<1){
						return;
					}
					String[] dbNames = dbnamestr.split(",");
					hsb.createPhotoView("t_photo_view", dbNames, sync_field);
					
				}
				
			} else {
				
			}
		} else {
			if (sync_mode != null && "trigger".equalsIgnoreCase(sync_mode)) {
				if ("1".equals(sync_A01) ) {
					hsb.creatHrTable("t_hr_view");
					csf.createFrigger(CreateSyncFrigger.HR_FLAG);
				}
				if ("1".equals(sync_B01)) {
					hsb.creatOrgTable("t_org_view");
					csf.createFrigger(CreateSyncFrigger.ORG_FLAG);
				}
				if ("1".equals(sync_K01)) {
					hsb.creatPostTable("t_post_view");
					csf.createFrigger(CreateSyncFrigger.POST_FLAG);
				}
				if ("1".equals(fieldChange)) {
					if ("1".equals(sync_A01) ) {
						hsb.creatFieldChangeTable("t_hr_view_log");
//						hsb.creatFieldChangeTable();
					}
					
					if ("1".equals(sync_B01)) {
						hsb.creatFieldChangeTable("t_org_view_log");
//						hsb.creatFieldChangeTable("TR_FIELD_CHANGE_T_ORG_VIEW");
					}
					
					if ("1".equals(sync_K01)) {
						hsb.creatFieldChangeTable("t_post_view_log");
//						hsb.creatFieldChangeTable("TR_FIELD_CHANGE_T_POST_VIEW");
					}
					csf.createFrigger(CreateSyncFrigger.FIELD_FLAG);
				} else {
					
				}
				if ("1".equals(photo)) {
					hsb.dropView("t_photo_view");
					//获得已选人员库
					String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
					if(dbnamestr==null || dbnamestr.length()<1){
						return;
					}
					String[] dbNames = dbnamestr.split(",");
					hsb.createPhotoView("t_photo_view", dbNames, sync_field);
					
					// 添加系统代号+P字段
					ArrayList sysout_list = hsb.getSysOutSyncFlag();
					StringBuffer sql = new StringBuffer();
					sql.append("update t_hr_view set ");
					DbWizard dbw = new DbWizard(this.frameconn);
					DBMetaModel dbmodel = new DBMetaModel(this.frameconn);
					if (dbw.isExistTable("t_hr_view", false)) {
						Table table = new Table("t_hr_view");
						boolean flag = false;
						for (int i = 0; i < sysout_list.size(); i++) {
							String code = sysout_list.get(i).toString();
							
							if(!dbw.isExistField("t_hr_view", code.toLowerCase() + "p",false)) {
								Field item = new Field(code.toLowerCase() + "p", code.toLowerCase() + "p");
								item.setDatatype(DataType.INT);
								item.setLength(2);
								table.addField(item);
								sql.append(code + "p=1,");
								flag = true;
							}
							
						}
						
						
						if (flag) {
							try {
								dbw.addColumns(table);
								dbmodel.reloadTableModel("t_hr_view");
								dao.update(sql.substring(0,sql.length() - 1));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					csf.createFrigger(CreateSyncFrigger.PHOTO_FLAG);
				} else {
					hsb.dropView("t_photo_view");
					// 删除系统代号+P字段
					ArrayList sysout_list = hsb.getSysOutSyncFlag();
					DbWizard dbw = new DbWizard(this.frameconn);
					DBMetaModel dbmodel = new DBMetaModel(this.frameconn);
					if (dbw.isExistTable("t_hr_view", false)) {
						for (int i = 0; i < sysout_list.size(); i++) {
							String code = sysout_list.get(i).toString();
							Table table = new Table("t_hr_view");
							if(dbw.isExistField("t_hr_view", code.toLowerCase() + "p",false)) {
								Field item = new Field(code.toLowerCase() + "p", code.toLowerCase() + "p");
								table.addField(item);
								dbw.dropColumns(table);
							}
							
						}
						dbmodel.reloadTableModel("t_hr_view");
					}
					
				}
			} else if (sync_mode != null && "time_job".equalsIgnoreCase(sync_mode)) {
				if ("1".equals(sync_A01) ) {
					hsb.creatHrTable("t_hr_view");
				}
				if ("1".equals(sync_B01)) {
					hsb.creatOrgTable("t_org_view");
				}
				if ("1".equals(sync_K01)) {
					hsb.creatPostTable("t_post_view");
				}
				if ("1".equals(fieldChange)) {
					if ("1".equals(sync_A01) ) {
						hsb.creatFieldChangeTable("t_hr_view_log");
					}
					
					if ("1".equals(sync_B01)) {
						hsb.creatFieldChangeTable("t_org_view_log");
					}
					
					if ("1".equals(sync_K01)) {
						hsb.creatFieldChangeTable("t_post_view_log");
					}
				} 
				
				if ("1".equals(photo)) {
					hsb.dropView("t_photo_view");
					//获得已选人员库
					String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
					if(dbnamestr==null || dbnamestr.length()<1){
						return;
					}
					String[] dbNames = dbnamestr.split(",");
					hsb.createPhotoView("t_photo_view", dbNames, sync_field);
					
					// 添加系统代号+P字段
					ArrayList sysout_list = hsb.getSysOutSyncFlag();
					StringBuffer sql = new StringBuffer();
					sql.append("update t_hr_view set ");
					DbWizard dbw = new DbWizard(this.frameconn);
					DBMetaModel dbmodel = new DBMetaModel(this.frameconn);
					if (dbw.isExistTable("t_hr_view", false)) {
						Table table = new Table("t_hr_view");
						boolean flag = false;
						for (int i = 0; i < sysout_list.size(); i++) {
							String code = sysout_list.get(i).toString();
							
							if(!dbw.isExistField("t_hr_view", code.toLowerCase() + "p",false)) {
								Field item = new Field(code.toLowerCase() + "p", code.toLowerCase() + "p");
								item.setDatatype(DataType.INT);
								item.setLength(2);
								table.addField(item);
								sql.append(code + "p=1,");
								flag = true;
							}
							
						}
						
						
						if (flag) {
							try {
								dbw.addColumns(table);
								dbmodel.reloadTableModel("t_hr_view");
								dao.update(sql.substring(0,sql.length() - 1));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					csf.createFrigger(CreateSyncFrigger.PHOTO_FLAG);
				} else {
					hsb.dropView("t_photo_view");
					// 删除系统代号+P字段
					ArrayList sysout_list = hsb.getSysOutSyncFlag();
					DbWizard dbw = new DbWizard(this.frameconn);
					DBMetaModel dbmodel = new DBMetaModel(this.frameconn);
					if (dbw.isExistTable("t_hr_view", false)) {
						for (int i = 0; i < sysout_list.size(); i++) {
							String code = sysout_list.get(i).toString();
							Table table = new Table("t_hr_view");
							if(dbw.isExistField("t_hr_view", code.toLowerCase() + "p",false)) {
								Field item = new Field(code.toLowerCase() + "p", code.toLowerCase() + "p");
								table.addField(item);
								dbw.dropColumns(table);
							}
							
						}
						dbmodel.reloadTableModel("t_hr_view");
					}
					
				}
				
				
			}
		}
		if (dbtemp.indexOf(",") != -1)
			dbname = dbtemp.substring(0, dbtemp.indexOf(","));
		else
			dbname = dbtemp;
		this.getFormHM().put("dbname", dbname);
	}

}
