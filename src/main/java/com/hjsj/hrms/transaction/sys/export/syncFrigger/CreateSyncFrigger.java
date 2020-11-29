package com.hjsj.hrms.transaction.sys.export.syncFrigger;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
/**
 * 
 * <p>Title:CreateSyncFrigger.java</p>
 * <p>Description>:CreateSyncFrigger.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 14, 2011 9:56:06 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: 郑文龙
 */
public class CreateSyncFrigger {

	private Connection conn = null;
	private UserView userView = null;
	
	public static int HR_FLAG = 1;
	public static int ORG_FLAG = 2;
	public static int POST_FLAG = 3;
	public static int PHOTO_FLAG = 4;// 照片
	public static int FIELD_FLAG = 5;// 跟踪指标变化前后信息
	public static int ONLY_ORG_FLAG = 0;
	public boolean fieldAndCode = false;
	public String fieldAndCodeSeq = ":";

	public boolean getFieldAndCode() {
		return fieldAndCode;
	}

	public void setFieldAndCode(boolean fieldAndCode) {
		this.fieldAndCode = fieldAndCode;
	}

	public String getFieldAndCodeSeq() {
		return fieldAndCodeSeq;
	}

	public void setFieldAndCodeSeq(String fieldAndCodeSeq) {
		this.fieldAndCodeSeq = fieldAndCodeSeq;
	}

	private static int DBMark = Sql_switcher.searchDbServer();
	
	public CreateSyncFrigger(Connection conn) {
		this.conn = conn;
	}

	public CreateSyncFrigger(Connection conn,UserView userView, boolean fieldAndCode, String fieldAndCodeSeq) {
		this.conn = conn;
		this.userView = userView;
		this.setFieldAndCode(fieldAndCode);
		this.setFieldAndCodeSeq(fieldAndCodeSeq);
		createFun();
	}

	/**
	 * 创建触发器
	 * @throws GeneralException 
	 */
	public void createFrigger(int sync_t_flag) throws GeneralException {
		HrSyncBo hsb = new HrSyncBo(this.conn);
		DbWizard dbw = new DbWizard(this.conn);
		// 判断同步的是人员、机构、还是职位。
		if (sync_t_flag == HR_FLAG && hsb.isSync_a01()) {//HR_FLAG 人员标志 isSync_a01() 这个方法是判断是否选择类人员同步
			/* ************************** 人员 ************************* */
			if (dbw.isExistTable("t_hr_view",false)) {
				String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);//获得已选人员库
				if(dbnamestr==null || dbnamestr.length()<1){
					return;
				}
				String columns = hsb.getTextValue(HrSyncBo.FIELDS);//获得已选的人员字段
				String[] dbnames = dbnamestr.split(",");
				// 翻译型代码
				String codefieldstr = "";
				if (!hsb.isBcode()) {//判断是否选择的全部翻译成代码描述
					codefieldstr = hsb.filtration(columns);//获得全部的翻译型代码
				} else {
					codefieldstr = hsb.getTextValue(HrSyncBo.CODE_FIELDS);//获得选择的翻译型代码
				}
				Map tranfields = getFields(codefieldstr, sync_t_flag);//整理字段按照表归类
				Map fields = getFields(columns, sync_t_flag);//整理字段按照表归类
				//判断是用的那种数据库
				if (DBMark == 1) {//SQL Server
					new FriggerMSSQL(this.conn).toUser(dbnames, fields,
							tranfields);
				} else if (DBMark == 2) {//Orcale
					new FriggerToOracle(this.conn).toUser(dbnames, fields,
							tranfields);
				}
			}
			/* ************************** 人员 ************************* */
		} else if (sync_t_flag == ORG_FLAG && hsb.isSync_b01()) {//ORG_FLAG 人员标志 isSync_b01() 这个方法是判断是否选择类机构同步
			/* ************************** 机构 ************************* */
			if (dbw.isExistTable("t_org_view",false)) {
				String orgfieldstr = hsb.getTextValue(HrSyncBo.ORG_FIELDS);//获得已选的机构字段

				Map fieldsOrg = getFields(orgfieldstr, sync_t_flag);//整理字段按照表归类
				String orgcodefieldstr = "";
				if (!hsb.isBcode()) {//判断是否选择的全部翻译成代码描述
					orgcodefieldstr = hsb.filtration(orgfieldstr);//获得全部的翻译型代码
				} else {
					orgcodefieldstr = hsb
							.getTextValue(HrSyncBo.ORG_CODE_FIELDS);
				}

				Map tranfieldsOrg = getFields(orgcodefieldstr, sync_t_flag);//整理字段按照表归类
				//判断是用的那种数据库
				if (DBMark == 1) {
					new FriggerMSSQL(this.conn).toOrg(fieldsOrg, tranfieldsOrg);//获得选择的翻译型代码
				} else if (DBMark == 2) {
					new FriggerToOracle(this.conn).toOrg(fieldsOrg,
							tranfieldsOrg);
				}
			}
			/* ************************** 机构 ************************* */
		} else if (sync_t_flag == POST_FLAG && hsb.isSync_k01()) {//POST_FLAG 人员标志 isSync_k01() 这个方法是判断是否选择类职位同步
			/* ************************** 职位 ************************* */
			if (dbw.isExistTable("t_post_view",false)) {
				String postfieldstr = hsb.getTextValue(HrSyncBo.POST_FIELDS);//获得已选的职位字段

				Map fieldsOrg = getFields(postfieldstr, sync_t_flag);//整理字段按照表归类
				String postcodefieldstr = "";
				if (!hsb.isBcode()) {//判断是否选择的全部翻译成代码描述
					postcodefieldstr = hsb.filtration(postfieldstr);//获得全部的翻译型代码
				} else {
					postcodefieldstr = hsb
							.getTextValue(HrSyncBo.POST_CODE_FIELDS);//获得选择的翻译型代码
				}

				Map tranfields = getFields(postcodefieldstr, sync_t_flag);//整理字段按照表归类
				//判断是用的那种数据库
				if (DBMark == 1) {
					new FriggerMSSQL(this.conn).toPost(fieldsOrg, tranfields);
				} else if (DBMark == 2) {
					new FriggerToOracle(this.conn)
							.toPost(fieldsOrg, tranfields);
				}
			}
			/* ************************** 职位 ************************* */
		} else if (sync_t_flag == PHOTO_FLAG && hsb.isSync_photo()) {
			if (dbw.isExistTable("t_hr_view",false)) {
				String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);//获得已选人员库
				if(dbnamestr==null || dbnamestr.length()<1){
					return;
				}
				String[] dbNames = dbnamestr.split(",");
				if (DBMark == 1) {
					new FriggerMSSQL(this.conn).toPhoto(dbNames);
				} else if (DBMark == 2) {
					new FriggerToOracle(this.conn).toPhoto(dbNames);
				}
			}
		} else if (sync_t_flag == FIELD_FLAG) {
			
			// 系统代号
			List sysIdList = getSysIdList("3");
			if (sysIdList.size() > 0) {
				
				
				if (dbw.isExistTable("t_hr_view",false) && hsb.isSync_a01()) {
					// 人员字段
					List hrFieldList = getFieldList("A", hsb);
					
					hrFieldList.add("nbase");
					hrFieldList.add("b0110_code");
					hrFieldList.add("e0122_code");
					hrFieldList.add("e01a1_code");
					hrFieldList.add("b0110_0");
					hrFieldList.add("e0122_0");
					hrFieldList.add("e01a1_0");
					hrFieldList.add("username");
					hrFieldList.add("userpassword");
					
					if (DBMark == 1) {
						new FriggerMSSQL(this.conn).toFieldChange("A", hrFieldList, sysIdList);
					} else if (DBMark == 2) {
						new FriggerToOracle(this.conn).toFieldChange("A", hrFieldList, sysIdList);
					}
				}
				
				if (dbw.isExistTable("t_org_view",false) && hsb.isSync_b01()) {
					// 机构字段
					List orgFieldList = getFieldList("B", hsb);
					
					orgFieldList.add("codeitemdesc");
					orgFieldList.add("parentdesc");
					orgFieldList.add("corcode");
					orgFieldList.add("parentid");
					orgFieldList.add("grade");
					
					if (DBMark == 1) {
						new FriggerMSSQL(this.conn).toFieldChange("B", orgFieldList, sysIdList);
					} else if (DBMark == 2) {
						new FriggerToOracle(this.conn).toFieldChange("B", orgFieldList, sysIdList);
					}
				}
				
				if (dbw.isExistTable("t_post_view",false) && hsb.isSync_k01()) {
					// 机构字段
					List postFieldList = getFieldList("K", hsb);
					
					postFieldList.add("codeitemdesc");
					postFieldList.add("parentdesc");
					postFieldList.add("corcode");
					postFieldList.add("parentid");
					postFieldList.add("grade");
					
					if (DBMark == 1) {
						new FriggerMSSQL(this.conn).toFieldChange("K", postFieldList, sysIdList);
					} else if (DBMark == 2) {
						new FriggerToOracle(this.conn).toFieldChange("K", postFieldList, sysIdList);
					}
				}
			}
		}
	}

	private List getSysIdList(String send) {
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select sys_id from t_sys_outsync where send='"+send+"' and state=1");
			while(rs.next())  {
				list.add(rs.getString("sys_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	private List getFieldList (String type, HrSyncBo bo) {
		//获得已选的人员字段
		ArrayList hrFieldList = new ArrayList();
		List elList = bo.getNodeList("/root/" + type.toLowerCase());
		for (int i = 0; i < elList.size(); i++) {
			Element el = (Element) elList.get(i);
			hrFieldList.add(el.getText());
		}
		
		return hrFieldList;
	}
	
	/**
	 * 按照表和字段的对应关系 整理字段
	 * @param columns
	 * @return
	 */
	private Map getFields(String columns, int uro) {
		String column[] = columns.split(",");
		StringBuffer s_columns = new StringBuffer();
		Map reMap = new HashMap();
		for (int i = 0; i < column.length; i++) {
			s_columns.append("'" + column[i].toUpperCase() + "',");
		}
		s_columns.deleteCharAt(s_columns.length() - 1);
		String sql = "SELECT FIELDSETID,ITEMID,ITEMDESC "
				+ "FROM fielditem WHERE UPPER(ITEMID) IN ("
				+ s_columns.toString() + ") AND USEFLAG = 1 "
				+ "ORDER BY FIELDSETID,ITEMID";
		ContentDAO dao = new ContentDAO(conn);
		columns = columns + ",";
		String upFIELDSETID = "";
		List list = new ArrayList();
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				String FIELDSETID = rs.getString("FIELDSETID");
				String ITEMID = rs.getString("ITEMID");
				if (FIELDSETID != null || FIELDSETID.length() > 0) {
					if ("".equals(upFIELDSETID)
							|| !upFIELDSETID.equals(FIELDSETID)) {
						if (!list.isEmpty())
							reMap.put(upFIELDSETID.toUpperCase(), list);

						list = new ArrayList();
						if (ITEMID != null || ITEMID.length() > 0)
							list.add(ITEMID.toUpperCase());
					} else if (upFIELDSETID.equals(FIELDSETID))
						if (ITEMID != null || ITEMID.length() > 0)
							list.add(ITEMID.toUpperCase());
				}
				upFIELDSETID = FIELDSETID;
			}
			if (upFIELDSETID != null && upFIELDSETID.trim().length() > 0
					&& reMap.get(upFIELDSETID) == null) {
				reMap.put(upFIELDSETID.toUpperCase(), list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		if (s_columns.indexOf("B0110") != -1) {
			if (uro == HR_FLAG) {
				List aList = (List) reMap.get("A01");
				if (aList == null) {
					list = new ArrayList();
					list.add("B0110");
					reMap.put("A01", list);
				} else {
					aList.add("B0110");
				}
			} else if (uro == ORG_FLAG) {
				List aList = (List) reMap.get("B01");
				if (aList == null) {
					list = new ArrayList();
					list.add("B0110");
					reMap.put("B01", list);
				} else {
					aList.add("B0110");
				}
			}
		}
		if (s_columns.indexOf("E01A1") != -1) {
			if (uro == HR_FLAG) {
				List aList = (List) reMap.get("A01");
				if (aList == null) {
					list = new ArrayList();
					list.add("E01A1");
					reMap.put("A01", list);
				} else {
					aList.add("E01A1");
				}
			}else if(uro == POST_FLAG){
				List aList = (List) reMap.get("K01");
				if (aList == null) {
					list = new ArrayList();
					list.add("E01A1");
					reMap.put("K01", list);
				} else {
					aList.add("E01A1");
				}
			}
		}
		if (s_columns.indexOf("E0122") != -1) {
			if (uro == ORG_FLAG) {
				reMap.remove("A01");
			} else if (uro == POST_FLAG) {
				reMap.remove("A01");
				List aList = (List) reMap.get("K01");
				if (aList == null) {
					list = new ArrayList();
					list.add("E0122");
					reMap.put("K01", list);
				} else {
					aList.add("E0122");
				}
			}
		}
		return reMap;
	}

	/**
	 * 删除触发器
	 * @param sync_t_flag
	 */
	public void delFrigger(int sync_t_flag) {
		List list = getFriggerName(sync_t_flag);
//		list.addAll(delFriggerToOrganization());
		exeDelFrigger(list);
	}

	/**
	 * 删除触发器
	 * @param FriggerNames
	 * @return
	 */
	private boolean exeDelFrigger(List FriggerNames) {
		Iterator it = FriggerNames.iterator();
		try {
			ContentDAO dao = new ContentDAO(conn);
			ArrayList sqlsList = new ArrayList();
			while (it.hasNext()) {
				String friggerName = (String) it.next();
				if (friggerName != null && isExitToFrigger(friggerName)) {
//					System.out.println("DELETE TRIGGER NAME: " + friggerName);
					String sql = "drop trigger " + friggerName;
					sqlsList.add(sql);
				}
			}
			dao.batchUpdate(sqlsList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
		}
		return true;
	}

	/**
	 * 判断这个触发器是否存在
	 * @param friggerName
	 * @return
	 */
	private boolean isExitToFrigger(String friggerName) {
		if (friggerName == null || friggerName.length() < 1) {
			return false;
		}
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "";
		if (DBMark == 1) {
			sql = "select name as 'trigger_name' from sysobjects "
					+ "where xtype='TR' and upper(name) = '"
					+ friggerName.toUpperCase() + "'";
		} else if (DBMark == 2) {
			sql = "select trigger_name from user_triggers "
					+ "where upper(trigger_name) = '"
					+ friggerName.toUpperCase() + "'";
		}
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 获得全部可能的触发器的名称
	 * wangb 20170925 31845 
	 * 原来：删除触发器时查询所有子集（不管子集下是否有触发器都删除）
	 * 现在：直接查询库中所有触发器（不走子集）
	 * 在区分 人员、单位、岗位和照片视图下所有触发器
	 * @param sync_t_flag
	 * @return
	 */
	private List getFriggerName(int sync_t_flag){
		List list = new ArrayList();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = AdminDb.getConnection();
			String sql = "";
			if(Sql_switcher.searchDbServer()== Constant.MSSQL)//查询所有触发器 oracle 和 sql server  sql语句不同
				sql = "select name from sysobjects where xtype='TR' and name like ?";
			else
				sql = "select DISTINCT name from all_source where type='TRIGGER' and name like ?";
			ps = conn.prepareStatement(sql);
			if (sync_t_flag == HR_FLAG) {
				List uList = this.userView.getPrivFieldSetList(1);//人员子集
				String param ="TR_EMP_CHANGE_%";
				ps.setString(1, param);
				rs = ps.executeQuery();
				while(rs.next()){
					list.add(rs.getString("name"));
				}
				list.add("TR_ORG_CHANGE_ORGANIZATION");
			}else if (sync_t_flag == PHOTO_FLAG) {
				String param ="TR_PHOTO_CHANGE_%";
				ps.setString(1, param);
				rs = ps.executeQuery();
				while(rs.next()){
					list.add(rs.getString("name"));
				}
			} else if (sync_t_flag == FIELD_FLAG) {
				list.add("TR_FIELD_CHANGE_T_HR_VIEW");
				list.add("TR_FIELD_CHANGE_T_ORG_VIEW");
				list.add("TR_FIELD_CHANGE_T_POST_VIEW");
			}else if (sync_t_flag == ORG_FLAG) {
				String param ="TR_ORG_CHANGE_%";
				ps.setString(1, param);
				rs = ps.executeQuery();
				while(rs.next()){
					list.add(rs.getString("name"));
				}
			} else if (sync_t_flag == POST_FLAG) {
				String param ="TR_POST_CHANGE__%";
				ps.setString(1, param);
				rs = ps.executeQuery();
				while(rs.next()){
					list.add(rs.getString("name"));
				}
				list.add("TR_ORG_CHANGE_ORGANIZATION");
			} else  if (sync_t_flag == ONLY_ORG_FLAG) {
				list.add("TR_ORG_CHANGE_ORGANIZATION");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(ps);
			PubFunc.closeResource(conn);
		}
		
		return list;
	}

	/**
	 * 在表ORGANIZATION中创建的触发器名称
	 * @return
	 */
	public List delFriggerToOrganization() {
		List list = new ArrayList();
		list.add("TR_ORG_CHANGE_ORGANIZATION");
		return list;
	}
	
	public void createFun(){
		HrSyncBo hsb = new HrSyncBo(this.conn);
		String sync_mode = hsb.getAttributeValue(HrSyncBo.SYNC_MODE);
		dorpDBTool();
//		if(sync_mode.equalsIgnoreCase("trigger")){
			if(DBMark == 1){
				FriggerMSSQL fm = new FriggerMSSQL(this.conn);
				fm.CreateFunTransCode(this.fieldAndCode, this.fieldAndCodeSeq);
			}else if(DBMark == 2){
				FriggerToOracle fo = new FriggerToOracle(this.conn);
				fo.CreateFunTransCode(this.fieldAndCode, this.fieldAndCodeSeq);
			}
//		}else{
//			dorpDBTool();
//		}
	}
	
	public void dorpDBTool(){
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = null;
		RowSet rs = null;
		try {
			if(DBMark == 1){
				sql = "SELECT 1 FROM DBO.SYSOBJECTS WHERE ID= OBJECT_ID('FUN_GET_CODEDESC') AND XTYPE = 'FN'";
				rs = dao.search(sql);
				if(rs.next()){
					sql = "DROP FUNCTION FUN_GET_CODEDESC";
					dao.update(sql);
				}
				sql = "SELECT 1 FROM DBO.SYSOBJECTS WHERE ID= OBJECT_ID('PR_UP_SYNC_FLAG') AND OBJECTPROPERTY(ID,'ISPROCEDURE')=1";
				rs = dao.search(sql);
				if(rs.next()){
					sql = "DROP PROCEDURE PR_UP_SYNC_FLAG";
					dao.update(sql);
				}
			}else{
				sql = "SELECT 1 FROM USER_OBJECTS WHERE UPPER(OBJECT_TYPE) = 'FUNCTION' AND OBJECT_NAME = 'FUN_GET_CODEDESC'";
				rs = dao.search(sql);
				if(rs.next()){
					sql = "DROP FUNCTION FUN_GET_CODEDESC";
					dao.update(sql.toString());
				}
				sql = "SELECT 1 FROM USER_OBJECTS WHERE UPPER(OBJECT_TYPE) = 'PROCEDURE' AND OBJECT_NAME = 'PR_UP_SYNC_FLAG'";
				rs = dao.search(sql);
				if(rs.next()){
					sql = "DROP PROCEDURE PR_UP_SYNC_FLAG";
					dao.update(sql.toString());
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
