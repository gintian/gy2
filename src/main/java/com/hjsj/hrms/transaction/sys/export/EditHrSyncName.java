package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 
 * <p>
 * Title:EditHrSyncName.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * Create time:May 4, 2009:11:44:44 AM
 * </p>
 * 
 * @author huaitao
 * @version 1.0
 */
public class EditHrSyncName extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hmap = (HashMap) this.getFormHM().get("requestPamaHM");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String type = (String) this.getFormHM().get("type");
		String field = (String) hmap.get("field");
		String editname = SafeCode.decode((String) hmap.get("editname"));
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		String oldname = "";

		if ("a".equalsIgnoreCase(type))
			oldname = hsb.getAppAttributeValue(hsb.A, field);
		else if ("b".equalsIgnoreCase(type))
			oldname = hsb.getAppAttributeValue(hsb.B, field);
		else if ("k".equalsIgnoreCase(type))
			oldname = hsb.getAppAttributeValue(hsb.K, field);
		checkFile(oldname, editname, type);
		saveFields(type, field, editname, dao);
		if ("a".equalsIgnoreCase(type)) {
			if ("a0101".equalsIgnoreCase(field))// 如果给姓名列重命名就新增一列
			{
				DbWizard dbw = new DbWizard(this.frameconn);
				Table table = new Table("t_hr_view");
				if (field.equalsIgnoreCase(oldname))// 新增一列
				{
					Field field1 = new Field(editname, editname);
					field1.setDatatype(DataType.STRING);
					field1.setLength(30);
					table.addField(field1);
					dbw.addColumns(table);
				} else {
					if ("a0101".equalsIgnoreCase(editname))// 改回原来的列名
					// 删掉原来新增的列
					{
						if (dbw.isExistField("t_hr_view", oldname)) {
							Field field1 = new Field(oldname, oldname);
							field1.setDatatype(DataType.STRING);
							field1.setLength(30);
							table.addField(field1);
							dbw.dropColumns(table);
						}
					} else {
						if (dbw.isExistField("t_hr_view", oldname))
							hsb.updateColumn("t_hr_view", oldname, editname);
					}
				}
			} else
				hsb.updateColumn("t_hr_view", oldname, editname);
		} else if ("b".equalsIgnoreCase(type))
			hsb.updateColumn("t_org_view", oldname, editname);
		else if ("k".equalsIgnoreCase(type))
			hsb.updateColumn("t_post_view", oldname, editname);
	}

	private boolean saveFields(String type, String field, String appfield,
			ContentDAO dao) throws GeneralException {
		boolean isCorrect = false;
		if (field == null)
			field = "";
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		if ("A".equalsIgnoreCase(type)) {
			String fields = hsb.getTextValue(hsb.FIELDS);
			if (fields.indexOf(field) == -1) {
				fields += "," + field;
				hsb.setTextValue(hsb.FIELDS, fields);
			}
			hsb.setAppAttributeValue(hsb.A, field, appfield);
			hsb.saveParameter(dao);
		} else if ("B".equalsIgnoreCase(type)) {
			String fields = hsb.getTextValue(hsb.ORG_FIELDS);
			if (fields.indexOf(field) == -1) {
				fields += "," + field;
				hsb.setTextValue(hsb.ORG_FIELDS, fields);
			}
			hsb.setAppAttributeValue(hsb.B, field, appfield);
			hsb.saveParameter(dao);
		} else if ("K".equalsIgnoreCase(type)) {
			String fields = hsb.getTextValue(hsb.POST_FIELDS);
			if (fields.indexOf(field) == -1) {
				fields += "," + field;
				hsb.setTextValue(hsb.POST_FIELDS, fields);
			}
			hsb.setAppAttributeValue(hsb.K, field, appfield);
			hsb.saveParameter(dao);
		}

		return isCorrect;
	}

	private void checkFile(String oldName, String eideName, String type)
			throws GeneralException {
		if (oldName.equalsIgnoreCase(eideName)) {
			return;
		}
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		String fields = "";
		if ("a".equalsIgnoreCase(type)) {
			fields = hsb.getColumn();
			fields += ",nbase,nbase_0,unique_id,a0100,b0110_0,e0122_0,a0101,e01a1_0,username,userpassword,sdate,flag,sys_flag,";
		} else if ("b".equalsIgnoreCase(type)) {
			fields = hsb.getOrgColumn();
			fields += "b0110_0,unique_id,codesetid,codeitemdesc,parentid,parentdesc,grade,sdate,flag,sys_flag,a0000,corcode,";
		} else if ("k".equalsIgnoreCase(type)) {
			fields = hsb.getPostColumn();
			fields += "e01a1_0,e0122_0,unique_id,codesetid,codeitemdesc,parentid,parentdesc,grade,sdate,flag,sys_flag,a0000,corcode,";
		}
		if ((fields.toUpperCase()).indexOf("," + eideName.toUpperCase() + ",") != -1) {
			throw new GeneralException("自定义字段'" + eideName + "'与其它字段名称相同，无法修改！");
		}
	}
}
