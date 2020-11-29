package com.hjsj.hrms.transaction.sys.export.syncFrigger;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * 
 * <p>
 * Title:FriggerMSSQL.java
 * </p>
 * <p>
 * Description>:FriggerMSSQL.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Jan 14, 2011 9:55:53 AM
 * </p>
 * <p>
 * 
 * @version: 5.0
 *           </p>
 *           <p>
 * @author: 郑文龙
 *          </p>
 */
public class FriggerMSSQL {

	private Connection conn = null;

//	private static final String codeTable = "SELECT CODEITEMDESC,CODESETID,CODEITEMID FROM CODEITEM UNION SELECT CODEITEMDESC,CODESETID,CODEITEMID FROM organization";

	public FriggerMSSQL(Connection conn) {
		this.conn = conn;
	}

	public void toUser(String[] dbnames, Map fields, Map tranfields)
			throws GeneralException {
		Set tables = new HashSet();
		tables.add("A01");
		if (fields != null && !fields.isEmpty()) {
			tables.addAll(fields.keySet()) ;
		}
		HrSyncBo hsb = new HrSyncBo(this.conn);
		String isJz = hsb.getAttributeValue(HrSyncBo.JZ_FIELD);
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		 /**兼职参数*/
		String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid").toUpperCase();
		 /** 兼职单位 */
		String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
		 /** 兼职部门 */
		String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
		/**兼职岗位*/
		String post_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
		/**任免标志*/
		String appoint_filed = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
		/**排序*/
		String order_filed = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"order");
		
		if("1".equals(isJz)){
			if(setid != null && setid.length() > 0 && unit_field != null && unit_field.length() > 0 && dept_field != null && dept_field.length() > 0 && appoint_filed != null && appoint_filed.length() > 0 && post_field != null && post_field.length() > 0){
				tables.add(setid);
			} else {
				throw new GeneralException("请把人员兼职设置完整后在操作！");
			}
		}
		for (int i = 0; i < dbnames.length; i++) {
			Iterator it = tables.iterator();
			while (it.hasNext()) {
				String frigger = null;
				String table = (String) it.next();
				if ("A01".equalsIgnoreCase(table)) {
					frigger = toUserA01(dbnames[i], (List) fields.get(table),
							(List) tranfields.get(table));
				} else if("1".equals(isJz) && table.equalsIgnoreCase(setid)){
					frigger = toUserJz(dbnames[i],table,(List) fields.get(table),
							(List) tranfields.get(table));
				} else {
					frigger = toUserSub(dbnames[i], table, (List) fields
							.get(table), (List) tranfields.get(table));
				}
				Statement st = null;
				try {
					st = conn.createStatement();
					if (frigger != null && frigger.length() > 0)
						st.execute(frigger);
					
				} catch (SQLException e) {
					e.printStackTrace();
					throw new GeneralException("TR_EMP_CHANGE_" + dbnames[i]
							+ table + ":触发器创建失败！\n" + e.getMessage());
				} finally {
					try {
						if (st != null) {
							st.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		createOrganization();
	}

	public void toOrg(Map fields, Map tranfields) throws GeneralException {
		String frigger = "";
		createOrganization();
		if (fields == null || fields.isEmpty())
			return;
		
		Set tables = fields.keySet();
		Iterator it = tables.iterator();
		while (it.hasNext()) {
			String table = (String) it.next();
			if (table.trim().length() < 1)
				continue;
			if ("B01".equalsIgnoreCase(table)) {
				frigger = toOrgB01((List)fields.get("B01"), (List)tranfields.get("B01"));
			} 
			else {
				frigger = toOrgSub(table, (List) fields.get(table),	(List) tranfields.get(table));
			}
			
			Statement st = null;
			try {
				st = conn.createStatement();
				if (frigger != null && frigger.length() > 0)
					st.execute(frigger);
			} 
			catch (SQLException e) {
				e.printStackTrace();
				throw new GeneralException("TR_ORG_CHANGE_" + table
						+ ":触发器创建失败！\n" + e.getMessage());
			} 
			finally {
				try {
					if (st != null) {
						st.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void toPost(Map fields, Map tranfields) throws GeneralException {

		String frigger = "";
		createOrganization();
		if (fields == null || fields.isEmpty())
			return;
		Set tables = fields.keySet();
		Iterator it = tables.iterator();
		while (it.hasNext()) {
			String table = (String) it.next();
			if (table.trim().length() < 1)
				continue;
			if ("K01".equalsIgnoreCase(table)) {
				frigger = toPostK01((List) fields.get("K01"), (List) tranfields
						.get("K01"));
			} else {
				frigger = toPostSub(table, (List) fields.get(table),
						(List) tranfields.get(table));
			}
			Statement st = null;
			try {
				st = conn.createStatement();
				if (frigger != null && frigger.length() > 0)
					st.execute(frigger);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new GeneralException("TR_POST_CHANGE_" + table
						+ ":触发器创建失败！\n" + e.getMessage());
			} finally {
				try {
					if (st != null) {
						st.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void toPhoto(String[] dbNames) {
		
		Statement st = null;
		try {
			st = conn.createStatement();
			for (int i = 0; i < dbNames.length; i++) {
				String trigger = createPhotoTrigger(dbNames[i].toUpperCase());
				if (trigger != null && trigger.length() > 0) {
					st.execute(trigger);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void toFieldChange(String type, List fieldList, List sysIdList) {
	
		Statement st = null;
		try {
			st = conn.createStatement();
			String trigger = createFieldChangeTrigger(type, fieldList, sysIdList);
			if (trigger != null && trigger.length() > 0) {
				st.execute(trigger);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private void createOrganization() throws GeneralException {

		String frigger = toOrganization();
		Statement st = null;
		try {
			st = conn.createStatement();
			if (frigger != null && frigger.length() > 0) {
				new CreateSyncFrigger(this.conn).delFrigger(CreateSyncFrigger.ONLY_ORG_FLAG);
				st.execute(frigger);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneralException("TR_ORG_CHANGE_ORGANIZATION:触发器创建失败！\n" + e.getMessage());
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 针对A01表 创建的触发器
	 * 
	 * @param dbname
	 */
	private String toUserA01(String dbName, List columns, List tranfields) {
		return toUserA01(dbName, columns, tranfields, false, ":");
	}

	/**
	 * 针对A01表 创建的触发器
	 * 
	 * @param dbname
	 */
	private String toUserA01(String dbName, List columns, List tranfields, boolean fieldAndCode, String fieldAndCodeSeq) {
		HrSyncBo hsb = new HrSyncBo(this.conn);
		String table = dbName.toUpperCase() + "A01";
		String whereSql = " WHERE t_hr_view.UNIQUE_ID = @syncKey";
		List list = new ArrayList();
		String columsStr = "";
		if (columns != null) {
			list.addAll(columns);
			columsStr = columns.toString().toUpperCase();
		}
		DbNameBo dbbo = new DbNameBo(conn);
		String onlyfield = hsb.getAttributeValue(HrSyncBo.HR_ONLY_FIELD);
		if(onlyfield == null || onlyfield.length() < 1){
			onlyfield = "A0100";
		}
		String username = dbbo.getLogonUserNameField().toUpperCase();
		String password = dbbo.getLogonPassWordField().toUpperCase();
		if (list.indexOf(username) == -1) {
			list.add(username);
		}
		if (list.indexOf(password) == -1) {
			list.add(password);
		}
		list.add("A0100");
//		list.add("A0000");
		if (columsStr.indexOf("B0110") == -1) {
			list.add("B0110");
		}
		if (columsStr.indexOf("E0122") == -1) {
			list.add("E0122");
		}
		if (columsStr.indexOf("E01A1") == -1) {
			list.add("E01A1");
		}
		if (columsStr.indexOf("A0101") == -1) {
			list.add("A0101");
		}
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE TRIGGER TR_EMP_CHANGE_" + table + " ON [dbo].["
				+ table + "]\n");
		frigger.append("AFTER DELETE,INSERT,UPDATE\n");
		frigger.append("AS\n");
		frigger.append("DECLARE\n");
		frigger.append("  @nbaseName varchar(100),\n");
		frigger.append("  @strSQL    varchar(1000),\n");
		frigger.append("  @outappId  varchar(100),\n");
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,SyncFriggerTools.M_MARK));
		frigger.append("  @onlyfield varchar(100),\n");
		frigger.append("  @A0100     varchar(30),\n");
		frigger.append("  @syncKey   varchar(100),\n");
		frigger.append("  @nbaseID   varchar(30),\n");
		frigger.append("  @syncFlag  char(1),\n");
		frigger.append("  @code   varchar(50),");
		frigger.append("  @OldSyncKey   varchar(100),");
		frigger.append("  @recExists integer\n");
		frigger.append("\n");
		 
		frigger.append("IF NOT EXISTS(SELECT 1 FROM DELETED) OR NOT EXISTS(SELECT 1 FROM INSERTED) OR");
		for (int i = 0; i < list.size(); i++) {
			frigger.append(" UPDATE(");
			frigger.append(list.get(i));
			frigger.append(") OR");
		}
		frigger.delete(frigger.length() - 3, frigger.length());
		frigger.append("\n");
		frigger.append("BEGIN\n");
		frigger.append("  SELECT @nbaseName=DBNAME FROM DBNAME WHERE UPPER(PRE)='"
						+ dbName.toUpperCase() + "';\n");
		frigger.append("  IF EXISTS(SELECT 1 FROM INSERTED)\n");
		frigger.append("    DECLARE C_" + table + " CURSOR FOR SELECT GUIDKEY,A0100 FROM INSERTED;\n");
		frigger.append("  ELSE\n");
		frigger.append("    DECLARE C_" + table + " CURSOR FOR SELECT GUIDKEY,A0100 FROM DELETED;\n");

		frigger.append("  OPEN C_" + table + ";\n");
		frigger.append("  FETCH NEXT FROM C_" + table + " INTO @syncKey,@A0100;\n");
		frigger.append("  WHILE (@@FETCH_STATUS = 0)\n");
		frigger.append("  BEGIN\n");
		
		frigger.append("  IF(ISNULL(@syncKey,'')='' or (select count(*) from inserted  a inner join  deleted b on a.GUIDKEY = b.GUIDKEY  "); 
		for (int i = 0; i < list.size(); i++) {
			frigger.append(" and ((a.");
			frigger.append(list.get(i));
			frigger.append(" is null and b.");
			frigger.append(list.get(i));
			frigger.append(" is null) or (a.");
			frigger.append(list.get(i));
			frigger.append(" =b.");
			frigger.append(list.get(i));
			frigger.append("))");
		}
		
		frigger.append(" where a.GUIDKEY=@syncKey)=0)\n");
		frigger.append("BEGIN\n");
		
		// ------------------ 在新增人员时候判断同步标识是否为空
		frigger.append("    IF ISNULL(@syncKey,'')=''\n");
		frigger.append("    BEGIN\n");
		frigger.append("      SET @syncKey =  newid();\n");
		frigger.append("      UPDATE " + table + " SET GUIDKEY = @syncKey WHERE A0100=@A0100;\n");
		frigger.append("    END;\n");
		
		frigger.append("    SET @syncFlag=0;\n");
		
		frigger.append("    IF EXISTS(SELECT 1 FROM INSERTED)\n");
		frigger.append("    BEGIN\n");
		frigger.append("      SELECT @onlyfield=" + onlyfield + " FROM " + table + "  WHERE GUIDKEY = @syncKey;\n");
		/* 获得翻译性指标代码描述 */
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					frigger.append("      SELECT @" + column + "Desc =" + column + "  FROM " + table + " WHERE GUIDKEY = @syncKey;\n");
					frigger.append("      SELECT @" + column + "Desc = dbo.FUN_GET_CODEDESC(getdate(),'A01','" + column + "',@" + column + "Desc);\n");
				}
			}
			frigger.append("\n");
		}
		/* 获得翻译性指标代码描述 */
		
		/* start 处理人事异动 人员移库操作，数据视图出现一条空白记录，原因是移库前后人员guidkey不一致导致   wangb 2019-12-05*/
		frigger.append("      SELECT @OldSyncKey = GUIDKEY FROM DELETED;\n");
		frigger.append("      IF EXISTS(SELECT 1 FROM INSERTED) and EXISTS(SELECT 1 FROM DELETED) and ISNULL(@OldSyncKey,'')<> ISNULL(@syncKey,'')\n");
		frigger.append("      BEGIN\n");
		frigger.append("        UPDATE t_hr_view SET unique_id=@syncKey WHERE unique_id=@OldSyncKey;\n");
		frigger.append("      END;\n");
		/* end 处理人事异动 人员移库操作，数据视图出现一条空白记录，原因是移库前后人员guidkey不一致导致   wangb 2019-12-05*/
		
		
		/* 插入或更新同步表 标志改成 1（新增）或2（更新） */
		frigger.append("      IF NOT EXISTS(SELECT 1 FROM t_hr_view " + whereSql + ") AND ISNULL(@onlyfield,'')<>''\n");
		frigger.append("      BEGIN\n");
		frigger.append("        "
				+ SyncFriggerTools.insertA01toM(dbName, columns, tranfields,
						this.conn) + "\n");
		frigger.append("        SET @syncFlag=1\n");
		frigger.append("      END\n");
		frigger.append("      ELSE IF ISNULL(@onlyfield,'')<>''\n");
		frigger.append("      BEGIN\n");
		frigger.append("        "
							+ SyncFriggerTools
									.updateA01toM(
											dbName,
											columns,
											tranfields,
											"FROM t_hr_view INNER JOIN "
													+ table
													+ " T ON t_hr_view.UNIQUE_ID = T.GUIDKEY " + whereSql, this.conn)
							+ "\n");
	
		frigger.append("        SET @syncFlag=2;\n");
		frigger.append("      END\n");

		/* 新增B0110_code、E0122_code、E01A1_code 三个字段 */
		frigger.append("      SELECT @code = B0110_0 FROM t_hr_view WHERE unique_id=@syncKey;\n");
		frigger.append("      IF ISNULL(@code,'') = ''\n");
		frigger.append("		update t_hr_view set B0110_code = NULL WHERE unique_id=@syncKey;\n");
		frigger.append("      ELSE\n");
		frigger.append("        update t_hr_view set B0110_code = (SELECT corCode FROM organization where codeitemid=@code) WHERE unique_id=@syncKey;\n");

		frigger.append("      SELECT @code = E0122_0 FROM t_hr_view WHERE unique_id=@syncKey;\n");
		frigger.append("      IF ISNULL(@code,'') = ''\n");
		frigger.append("		update t_hr_view set E0122_code = NULL WHERE unique_id=@syncKey;\n");
		frigger.append("      ELSE\n");
		frigger.append("        update t_hr_view set E0122_code = (SELECT corCode FROM organization where codeitemid=@code) WHERE unique_id=@syncKey;\n");
		
		frigger.append("      SELECT @code = E01A1_0 FROM t_hr_view WHERE unique_id=@syncKey;\n");
		frigger.append("      IF ISNULL(@code,'') = ''\n");
		frigger.append("		update t_hr_view set E01A1_code = NULL WHERE unique_id=@syncKey;\n");
		frigger.append("      ELSE\n");
		frigger.append("        update t_hr_view set E01A1_code = (SELECT corCode FROM organization where codeitemid=@code) WHERE unique_id=@syncKey;\n");
		/* 新增B0110_code、E0122_code、E01A1_code 三个字段 */
		/* 插入或更新同步表 标志改成 1（新增）或2（更新） */
		//--------------------------------------------------外部同步字段 重复 问题
		if(onlyfield!= null && onlyfield.length() > 0){
			String CusColumn = null;
			if("A0100".equalsIgnoreCase(onlyfield)){
				CusColumn = "A0100";
			}else{
				CusColumn = hsb.getAppAttributeValue(HrSyncBo.A, onlyfield);
			}
			// 去掉清空a0100
//			frigger.append("      IF ISNULL(@onlyfield,'')<>'' AND EXISTS(SELECT 1 FROM t_hr_view WHERE " + CusColumn + "=@onlyfield and upper(nbase_0)=upper(@nbaseName)  AND UNIQUE_ID <> @syncKey)\n");
//			frigger.append("        UPDATE t_hr_view SET " + CusColumn + "=NULL WHERE " + CusColumn + "=@onlyfield AND UNIQUE_ID <> @syncKey;\n");
		}
		//--------------------------------------------------外部同步字段 重复 问题
		frigger.append("    END\n");
		/* 人员删除 同步标志的值 设为3 */
		frigger.append("    ELSE IF ISNULL(@syncKey,'')<>''\n");
		frigger.append("    BEGIN\n");
		
		/*******************
		 * 暂时删除此处，不知当时为什么要把单位部门岗位清空，甚至a0100也清空
		 * wangzhongjun 
		 * 2012-07-14
		 * 
		 */	
//		frigger.append("      IF EXISTS(SELECT 1 FROM t_hr_view WHERE UNIQUE_ID=@syncKey AND UPPER(nbase_0)='" + dbName.toUpperCase() + "')\n");	
//		frigger.append("        " + SyncFriggerTools.emptyAllRecord("WHERE UNIQUE_ID=@syncKey AND UPPER(nbase_0)='" + dbName.toUpperCase() + "'", this.conn, HrSyncBo.A) + "\n");
		
		
		frigger.append("      SET @syncFlag=3;\n");
		frigger.append("    END\n");
		/* 人员删除 同步标志的值 设为3 */

		/* 修改同步标志 */
		frigger.append("    IF @syncFlag <> 0");
		frigger.append("      EXEC PR_UP_SYNC_FLAG @syncFlag,'',@syncKey,'"+ dbName.toUpperCase() +"','A'\n");
		frigger.append(" END\n");
		frigger.append("    FETCH NEXT FROM C_" + table + " INTO @syncKey,@A0100;\n");
		
		frigger.append("  END\n");
		/* 修改同步标志 */
		frigger.append("  CLOSE C_" + table + "\n");
		frigger.append("  DEALLOCATE C_" + table + "\n");
		frigger.append("END\n");
		
		return frigger.toString();
	}

	/**
	 * 对人员子集创建触发器
	 * 
	 * @param dbName
	 *            人员库
	 * @param table
	 *            表名
	 * @param columns
	 *            字段名
	 * @param tranfields
	 *            翻译字段名
	 * @return
	 */
	private String toUserSub(String dbName, String table, List columns,
			List tranfields) {

		if (columns == null || columns.isEmpty())
			return "";
		String atable = table;
		table = dbName.toUpperCase() + table;

		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE TRIGGER TR_EMP_CHANGE_" + table + " ON [dbo].["
				+ table + "]\n");
		frigger.append("AFTER DELETE,INSERT,UPDATE\n");
		frigger.append("AS\n");
		frigger.append("DECLARE\n");
		frigger.append("  @strSQL   varchar(1000),\n");
		frigger.append("  @outappId varchar(100),\n");
		frigger.append("  @A0100    varchar(30),\n");
		frigger.append("  @syncFlag char(1),\n");
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,SyncFriggerTools.M_MARK));
		frigger.append("  @syncKey  varchar(100),\n");
		frigger.append("  @MaxI9999 integer,\n");
		frigger.append("  @I9999    integer\n");

		frigger.append("IF NOT EXISTS(SELECT 1 FROM DELETED) OR NOT EXISTS(SELECT 1 FROM INSERTED) OR");
		for (int i = 0; i < columns.size(); i++) {
			frigger.append(" UPDATE(");
			frigger.append(columns.get(i));
			frigger.append(") OR");
		}
		frigger.append(" UPDATE(I9999)");
		frigger.append("\n");

		frigger.append("BEGIN\n");
		frigger.append("  IF EXISTS(SELECT 1 FROM INSERTED)\n");
		frigger.append("    DECLARE C_" + table
				+ " CURSOR FOR SELECT A0100,I9999 FROM INSERTED\n");
		frigger.append("  ELSE\n");
		frigger.append("    DECLARE C_" + table
				+ " CURSOR FOR SELECT A0100,I9999 FROM DELETED\n");

		frigger.append("  OPEN C_" + table + "\n");
		frigger.append("  FETCH NEXT FROM C_" + table
						+ " INTO @A0100,@I9999\n");
		frigger.append("  WHILE (@@FETCH_STATUS = 0)\n");
		frigger.append("  BEGIN\n");
		frigger.append("    SET @syncFlag=0\n");
		frigger.append("    SELECT @syncKey = GUIDKEY FROM " + dbName
				+ "A01 WHERE A0100=@A0100\n");
		
		frigger.append("  IF((select count(*) from inserted a inner join  deleted b on a.A0100 = b.A0100 and a.I9999 = b.I9999  "); 
		
		for (int i = 0; i < columns.size(); i++) {
			frigger.append(" and ((a.");
			frigger.append(columns.get(i));
			frigger.append(" is null and b.");
			frigger.append(columns.get(i));
			frigger.append(" is null) or (a.");
			frigger.append(columns.get(i));
			frigger.append(" =b.");
			frigger.append(columns.get(i));
			frigger.append("))");
		}
		
		frigger.append(" where a.A0100=@A0100)=0)\n");
		frigger.append("BEGIN\n");
		
		frigger.append("    IF NOT EXISTS(SELECT 1 FROM " + table
				+ " WHERE A0100=@A0100)\n");
		frigger.append("    BEGIN\n");
		frigger.append("      "
					+ SyncFriggerTools.emptySubRecord("t_hr_view", columns,
							"WHERE UNIQUE_ID=@syncKey ", this.conn,
							HrSyncBo.A) + "\n");

		frigger.append("      SET @syncFlag=2\n");
		frigger.append("    END\n");
		frigger.append("    ELSE\n");
		frigger.append("    BEGIN\n");
		frigger.append("      SELECT @MaxI9999 = Max(I9999) FROM " + table
				+ " WHERE A0100=@A0100\n");
		frigger.append("      IF ISNULL(@MaxI9999,0)<>0 AND (@I9999>=@MaxI9999 OR EXISTS(SELECT 1 FROM DELETED WHERE A0100=@A0100 AND I9999>=@MaxI9999))\n");
		frigger.append("      BEGIN\n");
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					frigger.append("      SELECT @" + column + "Desc =" + column + "  FROM " + table + " WHERE I9999=@MaxI9999 AND A0100=@A0100;\n");
					frigger.append("      SELECT @" + column + "Desc = dbo.FUN_GET_CODEDESC(getdate(),'"+ atable +"','" + column + "',@" + column + "Desc);\n");
				}

			}
			frigger.append("\n");
		}
		
		StringBuffer strBuff = new StringBuffer();
		for (int i = 0; i < columns.size(); i++) {
			strBuff.append(" T.");
			strBuff.append(columns.get(i));
			if(i != columns.size() - 1) {
				strBuff.append(",");
			}
		}
		
		frigger.append("        " + SyncFriggerTools
									.UpData(
											HrSyncBo.A,
											columns,
											tranfields,
											"A",
											"FROM t_hr_view INNER JOIN (SELECT " + strBuff.toString()
//													+ ",U.GUIDKEY FROM "
													+ ",U.GUIDKEY UGID FROM "// 2表 都有GUIDKEY字段，没有明确指明属于哪张表  wangb 20170912
													+ table
													+ " T LEFT JOIN "
													+ dbName
//													+ "A01 U ON T.A0100=U.A0100 WHERE T.A0100=@A0100 AND T.I9999=@MaxI9999) A ON t_hr_view.UNIQUE_ID=A.GUIDKEY",
													+ "A01 U ON T.A0100=U.A0100 WHERE T.A0100=@A0100 AND T.I9999=@MaxI9999) A ON t_hr_view.UNIQUE_ID=A.UGID",// 2表 都有GUIDKEY字段，没有明确指明属于哪张表  wangb 20170912
											SyncFriggerTools.M_MARK, this.conn)
							+ "\n");
		frigger.append("        SET @syncFlag=2\n");
		frigger.append("      END\n");
		frigger.append("    END\n");
		frigger.append("    IF @syncFlag=2\n");
		frigger.append("      EXEC PR_UP_SYNC_FLAG @syncFlag,'',@syncKey,'"+ dbName.toUpperCase() +"','A_'\n");
		
		frigger.append("    END\n");
		
		frigger.append("    FETCH NEXT FROM C_" + table
				+ " INTO @A0100,@I9999\n");
		frigger.append("  END\n");
		frigger.append("  CLOSE C_" + table + "\n");
		frigger.append("  DEALLOCATE C_" + table + "\n");
		frigger.append("END\n");
		return frigger.toString();
	}

	private String toUserJz(String dbName,String setid, List columns,
			List tranfields) {
		HrSyncBo hsb = new HrSyncBo(this.conn);
		String isJz = hsb.getAttributeValue(HrSyncBo.JZ_FIELD);
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		 /**兼职参数*/
		String table = dbName.toUpperCase() + setid;
		Set fieldSet = new HashSet();
		if(columns != null && !columns.isEmpty()){
			fieldSet.addAll(columns);
		}
		 /**兼职单位字段*/
		String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
		fieldSet.add(unit_field);
		 /** 兼职部门 */
		String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
		fieldSet.add(dept_field);
		 /** 兼职岗位 */
		String post_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
		fieldSet.add(post_field);
		/**任免标志*/
		String appoint_filed = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
		fieldSet.add(appoint_filed);
		/**排序*/
		String order_filed = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"order");
		if (order_filed != null && order_filed.trim().length() > 0) {
			fieldSet.add(order_filed);
		}
		
		
		// 现在的程序兼职指标不一定为@K,
//		if(post_field!=null&&post_field.length()>0)
//		{
//			FieldItem fielitem=DataDictionary.getFieldItem(post_field);
//			if(fielitem==null||!fielitem.getCodesetid().equals("@K"))
//			{
//				post_field="";
//			}else{
//				fieldSet.add(post_field);
//			}
//		}		
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE TRIGGER TR_EMP_CHANGE_" + table + " ON [dbo].["
				+ table + "]\n");
		frigger.append("AFTER DELETE,INSERT,UPDATE\n");
		frigger.append("AS\n");
		frigger.append("DECLARE\n");
		frigger.append("  @strSQL   varchar(1000),\n");
		frigger.append("  @outappId varchar(100),\n");
		frigger.append("  @A0100    varchar(30),\n");
		frigger.append("  @syncFlag char(1),\n");
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,SyncFriggerTools.M_MARK));
		if("1".equals(isJz)){
			frigger.append("  @unit     varchar(30),\n");
			frigger.append("  @dept     varchar(30),\n");
			if(!"".equals(post_field)){
				frigger.append("  @post     varchar(30),\n");
			}
			frigger.append("  @jz_str   varchar(300),\n");
		}
		frigger.append("  @syncKey  varchar(100),\n");
		frigger.append("  @MaxI9999 integer,\n");
		frigger.append("  @I9999    integer\n");

		frigger.append("IF NOT EXISTS(SELECT 1 FROM DELETED) OR NOT EXISTS(SELECT 1 FROM INSERTED) OR");
		for (Iterator it = fieldSet.iterator(); it.hasNext();) {
			frigger.append(" UPDATE(");
			frigger.append((String)it.next());
			frigger.append(") OR");
		}
		frigger.append(" UPDATE(I9999)");
		frigger.append("\n");

		frigger.append("BEGIN\n");
		
		frigger.append("  IF EXISTS(SELECT 1 FROM INSERTED)\n");
		frigger.append("    DECLARE C_" + table
				+ " CURSOR FOR SELECT A0100,I9999 FROM INSERTED\n");
		frigger.append("  ELSE\n");
		frigger.append("    DECLARE C_" + table
				+ " CURSOR FOR SELECT A0100,I9999 FROM DELETED\n");

		frigger.append("  OPEN C_" + table + "\n");
		frigger.append("  FETCH NEXT FROM C_" + table
						+ " INTO @A0100,@I9999\n");
		frigger.append("  WHILE (@@FETCH_STATUS = 0)\n");
		frigger.append("  BEGIN\n");
		
		frigger.append("    SET @jz_str=''\n");
		frigger.append("    SET @syncFlag=0\n");
		frigger.append("    SELECT @syncKey = GUIDKEY FROM " + dbName
				+ "A01 WHERE A0100=@A0100\n");
		
		frigger.append("  IF((select count(*) from inserted a inner join deleted b on a.A0100 = b.A0100 and  a.I9999 = b.I9999 "); 
		
		for (Iterator it = fieldSet.iterator(); it.hasNext();) {
			String field = (String) it.next();
			frigger.append(" and ((a.");
			frigger.append(field);
			frigger.append(" is null and b.");
			frigger.append(field);
			frigger.append(" is null) or (a.");
			frigger.append(field);
			frigger.append(" =b.");
			frigger.append(field);
			frigger.append("))");
		}
		
		frigger.append(" where a.A0100=@A0100)=0)\n");
		frigger.append("    BEGIN\n");
		
		if(columns != null && !columns.isEmpty()){
			frigger.append("    IF NOT EXISTS(SELECT 1 FROM " + table
					+ " WHERE A0100=@A0100)\n");
			frigger.append("    BEGIN\n");
			frigger.append("      "
						+ SyncFriggerTools.emptySubRecord("t_hr_view", columns,
								"WHERE UNIQUE_ID=@syncKey ", this.conn,
								HrSyncBo.A) + "\n");
	
			frigger.append("      SET @syncFlag=2\n");
			frigger.append("    END\n");
			frigger.append("    ELSE\n");
			frigger.append("    BEGIN\n");
			frigger.append("      SELECT @MaxI9999 = Max(I9999) FROM " + table
					+ " WHERE A0100=@A0100\n");
			frigger.append("      IF ISNULL(@MaxI9999,0)<>0 AND (@I9999>=@MaxI9999 OR EXISTS(SELECT 1 FROM DELETED WHERE A0100=@A0100 AND I9999>=@MaxI9999))\n");
			frigger.append("      BEGIN\n");
			if (tranfields != null && !tranfields.isEmpty()) {
				Iterator it = tranfields.iterator();
				while (it.hasNext()) {
					String column = (String) it.next();
					if (column != null && column.length() > 0) {
						frigger.append("      SELECT @" + column + "Desc =" + column + "  FROM " + table + " WHERE I9999=@MaxI9999 AND A0100=@A0100;\n");
						frigger.append("      SELECT @" + column + "Desc = dbo.FUN_GET_CODEDESC(getdate(),'"+ setid +"','" + column + "',@" + column + "Desc);\n");
					}
	
				}
				frigger.append("\n");
			}
			frigger.append("        " + SyncFriggerTools
										.UpData(
												HrSyncBo.A,
												columns,
												tranfields,
												"A",
//												"FROM t_hr_view INNER JOIN (SELECT T.*,GUIDKEY FROM "
												"FROM t_hr_view INNER JOIN (SELECT T.*,U.GUIDKEY UGID FROM "// 2表 都有GUIDKEY字段，没有明确指明属于哪张表  wangb 20170912
														+ table
														+ " T LEFT JOIN "
														+ dbName
//														+ "A01 U ON T.A0100=U.A0100 WHERE T.A0100=@A0100 AND T.I9999=@MaxI9999) A ON t_hr_view.UNIQUE_ID=A.GUIDKEY",
														+ "A01 U ON T.A0100=U.A0100 WHERE T.A0100=@A0100 AND T.I9999=@MaxI9999) A ON t_hr_view.UNIQUE_ID=A.UGID",// 2表 都有GUIDKEY字段，没有明确指明属于哪张表  wangb 20170912
												SyncFriggerTools.M_MARK, this.conn)
								+ "\n");
			frigger.append("        SET @syncFlag=2\n");
			frigger.append("      END\n");
			frigger.append("    END\n");
		}
		if("1".equals(isJz) && "".equals(post_field)){
			if (order_filed != null && order_filed.length() > 0) {
				frigger.append("    DECLARE c_jz CURSOR FOR SELECT " + unit_field + "," + dept_field + " FROM " + table + " WHERE A0100=@A0100 and "+ appoint_filed +"='0' order by " + order_filed + ";\n");
			} else {
				frigger.append("    DECLARE c_jz CURSOR FOR SELECT " + unit_field + "," + dept_field + " FROM " + table + " WHERE A0100=@A0100 and "+ appoint_filed +"='0' order by i9999;\n");
			}
			frigger.append("    OPEN c_jz;\n");
			frigger.append("    FETCH NEXT FROM c_jz INTO @unit,@dept\n");
			frigger.append("    WHILE (@@FETCH_STATUS = 0)\n");
			frigger.append("    BEGIN\n");
			/* /兼职单位/兼职部门/兼职岗位/ */
			frigger.append("      IF LEN(@jz_str) >0 AND ISNULL(@unit,'') <>'' AND ISNULL(@dept,'')<>''\n");
			frigger.append("        SET @jz_str = @jz_str + ',/' + @unit + '/' + @dept + '/'\n");
			frigger.append("      ELSE IF ISNULL(@unit,'') <>'' AND ISNULL(@dept,'')<>''\n");
			frigger.append("        SET @jz_str = '/' + @unit + '/' + @dept + '/'\n");
			frigger.append("      FETCH NEXT FROM c_jz INTO @unit,@dept\n");
			frigger.append("    END;\n");
			frigger.append("    CLOSE c_jz;\n");
			frigger.append("    DEALLOCATE c_jz;\n");
//			frigger.append("    IF LEN(@jz_str) >0 AND EXISTS(SELECT 1 FROM t_hr_view WHERE t_hr_view.UNIQUE_ID=@syncKey)\n");
			frigger.append("    IF EXISTS(SELECT 1 FROM t_hr_view WHERE t_hr_view.UNIQUE_ID=@syncKey)\n");
			frigger.append("    BEGIN\n");
			frigger.append("      UPDATE t_hr_view SET jz_field = @jz_str,sdate = GETDATE() WHERE t_hr_view.UNIQUE_ID=@syncKey;\n");
			frigger.append("      SET @syncFlag=2;\n");
			frigger.append("    END;\n");
		}else if("1".equals(isJz) && !"".equals(post_field)){
			if (order_filed != null && order_filed.length() > 0) {
				frigger.append("    DECLARE c_jz CURSOR FOR SELECT " + unit_field + "," + dept_field + "," + post_field + " FROM " + table + " WHERE A0100=@A0100 and "+ appoint_filed +"='0' order by " + order_filed + ";\n");
			} else {
				frigger.append("    DECLARE c_jz CURSOR FOR SELECT " + unit_field + "," + dept_field + "," + post_field + " FROM " + table + " WHERE A0100=@A0100 and "+ appoint_filed +"='0' order by i9999;\n");
			}
			frigger.append("    OPEN c_jz;\n");
			frigger.append("    FETCH NEXT FROM c_jz INTO @unit,@dept,@post\n");
			frigger.append("    WHILE (@@FETCH_STATUS = 0)\n");
			frigger.append("    BEGIN\n");
			/* /兼职单位/兼职部门/兼职岗位/ */
			frigger.append("      IF LEN(@jz_str) >0 AND ISNULL(@unit,'') <>'' AND ISNULL(@dept,'')<>'' AND ISNULL(@post,'')<>''\n");
			frigger.append("        SET @jz_str = @jz_str + ',/' + @unit + '/' + @dept + '/' + @post +'/'\n");
			frigger.append("      ELSE IF ISNULL(@unit,'') <>'' AND ISNULL(@dept,'')<>'' AND ISNULL(@post,'')<>''\n");
			frigger.append("        SET @jz_str = '/' + @unit + '/' + @dept + '/' + @post + '/'\n");
			frigger.append("      FETCH NEXT FROM c_jz INTO @unit,@dept,@post\n");
			frigger.append("    END;\n");
			frigger.append("    CLOSE c_jz;\n");
			frigger.append("    DEALLOCATE c_jz;\n");
//			frigger.append("    IF LEN(@jz_str) >0 AND EXISTS(SELECT 1 FROM t_hr_view WHERE t_hr_view.UNIQUE_ID=@syncKey)\n");
			frigger.append("    IF EXISTS(SELECT 1 FROM t_hr_view WHERE t_hr_view.UNIQUE_ID=@syncKey)\n");
			frigger.append("    BEGIN\n");
			frigger.append("      UPDATE t_hr_view SET jz_field = @jz_str,sdate = GETDATE() WHERE t_hr_view.UNIQUE_ID=@syncKey;\n");
			frigger.append("      SET @syncFlag=2;\n");
			frigger.append("    END;\n");
		}
		frigger.append("    IF @syncFlag=2\n");
		frigger.append("      EXEC PR_UP_SYNC_FLAG @syncFlag,'',@syncKey,'"+ dbName.toUpperCase() +"','A_'\n");
		
		frigger.append("END\n");
		
		frigger.append("    FETCH NEXT FROM C_" + table
				+ " INTO @A0100,@I9999\n");
		frigger.append("  END\n");
		frigger.append("  CLOSE C_" + table + "\n");
		frigger.append("  DEALLOCATE C_" + table + "\n");
		frigger.append("END\n");
		return frigger.toString();
	}
	
	private String toOrganization() {
		HrSyncBo hsb = new HrSyncBo(this.conn);
		/* *********************** 部门字段 ********************** */
		String columnsToOrg = hsb.getTextValue(HrSyncBo.ORG_FIELDS);
		String orgcode = "";
		if (!hsb.isBcode()) {
			orgcode = hsb.filtration(columnsToOrg);
		} else {
			orgcode = hsb.getTextValue(HrSyncBo.ORG_CODE_FIELDS);
		}
		/* *********************** 部门字段 ********************** */
		/* *********************** 职位字段 ********************** */
		String columnsToPost = hsb.getTextValue(HrSyncBo.POST_FIELDS);
		String postcode = "";
		if (!hsb.isBcode()) {
			postcode = hsb.filtration(columnsToPost);
		} else {
			postcode = hsb.getTextValue(HrSyncBo.POST_CODE_FIELDS);
		}
		/* *********************** 职位字段 ********************** */
		/* *********************** 人员字段 ********************** */
		String columnsToHr = hsb.getTextValue(HrSyncBo.FIELDS);
		String hrcode = "";
		if (!hsb.isBcode()) {
			hrcode = hsb.filtration(columnsToHr);
		} else {
			hrcode = hsb.getTextValue(HrSyncBo.CODE_FIELDS);
		}
		/* *********************** 人员字段 ********************** */
		if (hsb.isSync_a01() || hsb.isSync_b01() || hsb.isSync_k01()) {
			return generationOrganizationFrigger(orgcode, postcode, hrcode);
		}
		return "";
	}

	/**
	 * 对机构表Organization创建触发器
	 * 
	 * @param orgCode
	 *            单位翻译型代码
	 * @param postCode
	 *            职位翻译型代码
	 * @param hrCode
	 *            人员翻译型代码
	 * @return
	 */
	private String generationOrganizationFrigger(String orgCode,
			String postCode, String hrCode) {
		DbWizard dbw = new DbWizard(this.conn);
		if (!dbw.isExistTable("t_hr_view", false)
				&& !dbw.isExistTable("t_org_view", false)
				&& !dbw.isExistTable("t_post_view", false))
			return "";
		
		HrSyncBo hsb = new HrSyncBo(this.conn);
		
		orgCode = orgCode.toUpperCase();
		postCode = postCode.toUpperCase();
		hrCode = hrCode.toUpperCase();
		
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE TRIGGER TR_ORG_CHANGE_ORGANIZATION ON [dbo].[organization]\n");
		frigger.append("AFTER UPDATE,INSERT,DELETE\n");
		frigger.append("AS\n");
		frigger.append("DECLARE\n");
		frigger.append("  @strSQL VARCHAR(1000),\n");
		frigger.append("  @syncFlag INT,\n");
		frigger.append("  @outappId VARCHAR(100),\n");
		frigger.append("  @syncKey  varchar(100),\n");
		if ((hsb.isSync_k01() && postCode.indexOf("E0122") != -1)
				|| (hsb.isSync_a01() && hrCode.indexOf("E0122") != -1)
				|| (hsb.isSync_b01() && orgCode.indexOf("B0110") != -1)) {
			frigger.append("  @ORGID    varchar(50),\n");
			frigger.append("  @ORGDesc  varchar(1000),\n");
		}
		frigger.append("  @CODEITEMID VARCHAR(100),\n");
		frigger.append("  @CODESETID VARCHAR(100),\n");
		frigger.append("  @CODEITEMDESC VARCHAR(100),\n");
		frigger.append("  @END_DATE DATETIME,\n");
		frigger.append("  @CORCODE VARCHAR(100),\n");
		frigger.append("  @PARENTID VARCHAR(100),\n");
		frigger.append("  @GRADE  VARCHAR(100),\n");
		frigger.append("  @PARENTDESE varchar(100),\n");
		frigger.append("  @PARENTGUIDKEY varchar(50),\n");//记录上级机构唯一标识  wangb 20170811
		frigger.append("  @ORIGINCODEITEMID varchar(100),\n");//记录原机构编码  wangb 20170811
//		frigger.append("  @A0000 int\n");
		frigger.append("  @levelA0000 int\n");//与同步字段同名 wangb 20170811

//		frigger.append("IF NOT EXISTS(SELECT 1 FROM DELETED) OR NOT EXISTS(SELECT 1 FROM INSERTED) OR UPDATE(CODEITEMID) OR UPDATE(CORCODE) OR UPDATE(CODESETID) OR UPDATE(CODEITEMDESC) OR UPDATE(PARENTID) OR UPDATE(GRADE) OR UPDATE(END_DATE) \n");/*OR UPDATE(A0000)*/
		frigger.append("IF NOT EXISTS(SELECT 1 FROM DELETED) OR NOT EXISTS(SELECT 1 FROM INSERTED) OR UPDATE(CODEITEMID) OR UPDATE(CORCODE) OR UPDATE(CODESETID) OR UPDATE(CODEITEMDESC) OR UPDATE(PARENTID) OR UPDATE(GRADE) OR UPDATE(END_DATE) OR UPDATE(LEVELA0000)\n");/*监听组织机构表 levelA0000 字段  wangb 20170807 */

		frigger.append("BEGIN\n");
		frigger.append("  IF EXISTS(SELECT 1 FROM INSERTED)\n");
//		frigger.append("    DECLARE CUR_ORG CURSOR FOR SELECT GUIDKEY,CODEITEMID,CORCODE,CODESETID,CODEITEMDESC,PARENTID,GRADE,END_DATE,a0000 FROM INSERTED\n");
		frigger.append("    DECLARE CUR_ORG CURSOR FOR SELECT GUIDKEY,CODEITEMID,CORCODE,CODESETID,CODEITEMDESC,PARENTID,GRADE,END_DATE,levelA0000 FROM INSERTED\n");//a0000 替换为levelA0000 wangb 20170807
		frigger.append("  ELSE\n");
//		frigger.append("    DECLARE CUR_ORG CURSOR FOR SELECT GUIDKEY,CODEITEMID,CORCODE,CODESETID,CODEITEMDESC,PARENTID,GRADE,END_DATE,a0000 FROM DELETED\n");
		frigger.append("    DECLARE CUR_ORG CURSOR FOR SELECT GUIDKEY,CODEITEMID,CORCODE,CODESETID,CODEITEMDESC,PARENTID,GRADE,END_DATE,levelA0000 FROM DELETED\n");//a0000 替换为levelA0000 wangb 20170807
		frigger.append("  OPEN CUR_ORG\n");
		// --------------------游标遍历-------------------------
//		frigger.append("  FETCH NEXT FROM CUR_ORG INTO @syncKey,@CODEITEMID,@CORCODE,@CODESETID,@CODEITEMDESC,@PARENTID,@GRADE,@END_DATE,@A0000\n");
		frigger.append("  FETCH NEXT FROM CUR_ORG INTO @syncKey,@CODEITEMID,@CORCODE,@CODESETID,@CODEITEMDESC,@PARENTID,@GRADE,@END_DATE,@levelA0000\n");//与同步字段同名 wangb 20170811
		frigger.append("  WHILE (@@FETCH_STATUS = 0)\n");
		frigger.append("  BEGIN\n");
		frigger.append("    IF EXISTS(SELECT 1 FROM INSERTED) AND ISNULL(@syncKey,'')=''\n");
		frigger.append("    BEGIN\n");
		frigger.append("      SET @syncKey = newid();\n");
		frigger.append("      UPDATE ORGANIZATION SET GUIDKEY = @syncKey WHERE CODEITEMID = @CODEITEMID\n");
		frigger.append("    END;\n");
		String isNull = (String) SystemConfig.getPropertyValue("sync_org_top_isNull");
		
		
		frigger.append("  IF((select count(*) from inserted a inner join deleted b on a.GUIDKEY = b.GUIDKEY  "); 
		
//		frigger.append(" and ISNULL(a.CODEITEMID,'')=ISNULL(b.CODEITEMID,'') and ISNULL(a.CORCODE,'')=ISNULL(b.CORCODE,'') and ISNULL(a.CODESETID,'')=ISNULL(b.CODESETID,'') and ISNULL(a.CODEITEMDESC,'')=ISNULL(b.CODEITEMDESC,'') and ISNULL(a.PARENTID,'')=ISNULL(b.PARENTID,'') and ISNULL(a.GRADE,'')=ISNULL(b.GRADE,'') and ISNULL(a.END_DATE,'')=ISNULL(b.END_DATE,'')");
		
		frigger.append(" and ((a.CODEITEMID is null and b.CODEITEMID is null) or a.CODEITEMID=b.CODEITEMID) ");
		frigger.append(" and ((a.CORCODE is null and b.CORCODE is null) or a.CORCODE=b.CORCODE) ");
		frigger.append(" and ((a.CODESETID is null and b.CODESETID is null) or a.CODESETID=b.CODESETID) ");
		frigger.append(" and ((a.CODEITEMDESC is null and b.CODEITEMDESC is null) or a.CODEITEMDESC=b.CODEITEMDESC) ");
		frigger.append(" and ((a.PARENTID is null and b.PARENTID is null) or a.PARENTID=b.PARENTID) ");
		frigger.append(" and ((a.GRADE is null and b.GRADE is null) or a.GRADE=b.GRADE) ");
		frigger.append(" and ((a.END_DATE is null and b.END_DATE is null) or a.END_DATE=b.END_DATE) ");
		//frigger.append(" and ((a.A0000 is null and b.A0000 is null) or a.A0000=b.A0000) ");
		frigger.append(" and ((a.levelA0000 is null and b.levelA0000 is null) or a.levelA0000=b.levelA0000) ");//添加对levelA0000 判断 wangb 20170807
		
		
		frigger.append(" where a.GUIDKEY = @syncKey)=0)\n");
		frigger.append("BEGIN\n");
		
//		frigger.append("    SELECT @PARENTDESE=CODEITEMDESC FROM organization WHERE CODEITEMID=@PARENTID\n");
		frigger.append("    IF @END_DATE > getdate() BEGIN\n");//有效的机构才能获取上级机构为一维编码 wangb 30785 20170818
		frigger.append("        SELECT @PARENTDESE=CODEITEMDESC,@PARENTGUIDKEY=GUIDKEY FROM organization WHERE CODEITEMID=@PARENTID\n");//获取上级机构唯一标识 wangb 20170811
		frigger.append("    END\n");
		frigger.append("    ELSE BEGIN\n");//失效的还是走默认的  wangb 30785 20170818
		frigger.append("        SELECT @PARENTDESE=CODEITEMDESC FROM organization WHERE CODEITEMID=@PARENTID\n");
		frigger.append("    END\n");
		if ("true".equalsIgnoreCase(isNull)) {
			frigger.append("    IF @CODEITEMID=@PARENTID\n");
			frigger.append("    BEGIN \n");
			frigger.append("      SET @PARENTID = NULL\n");
			frigger.append("      SET @PARENTDESE = NULL\n");
			frigger.append("    END\n");
		}
		if ((hsb.isSync_b01() && dbw.isExistTable("t_org_view", false))
				|| (hsb.isSync_k01() && dbw.isExistTable("t_post_view", false))) {
			// ---------新增修改
			frigger.append("    IF EXISTS(SELECT 1 FROM INSERTED)\n");
			frigger.append("    BEGIN \n");
			if (hsb.isSync_b01() && dbw.isExistTable("t_org_view", false)) {
				frigger.append("      IF ISNULL(@CODESETID,'@K') <> '@K'\n");
				frigger.append("      BEGIN\n");
				frigger.append("        IF NOT EXISTS(SELECT 1 FROM t_org_view WHERE UNIQUE_ID = @syncKey) AND @END_DATE > getdate()\n");
				frigger.append("        BEGIN\n");
//				frigger.append("          INSERT INTO t_org_view(UNIQUE_ID,b0110_0,corcode,codesetid,codeitemdesc,parentid,parentdesc,grade,a0000) VALUES(@syncKey,@CODEITEMID,@CORCODE,@CODESETID,@CODEITEMDESC,@PARENTID,@PARENTDESE,@GRADE,@A0000)\n");
				frigger.append("          INSERT INTO t_org_view(UNIQUE_ID,b0110_0,corcode,codesetid,codeitemdesc,parentid,parentdesc,parentGUIDKEY,grade,levelA0000) VALUES(@syncKey,@CODEITEMID,@CORCODE,@CODESETID,@CODEITEMDESC,@PARENTID,@PARENTDESE,@PARENTGUIDKEY,@GRADE,@levelA0000)\n");//新增时 上级机构唯一标识同步到单位视图表里和levelA0000 同步 wangb 20170811
				frigger.append("          SET @syncFlag = 1\n");
				frigger.append("        END\n");
				frigger.append("        ELSE IF @END_DATE > getdate()\n");
				frigger.append("        BEGIN\n");
//				frigger.append("          UPDATE t_org_view SET t_org_view.b0110_0=@CODEITEMID,t_org_view.corcode=@CORCODE,t_org_view.codesetid=@CODESETID,t_org_view.codeitemdesc=@CODEITEMDESC,t_org_view.parentid=@PARENTID,t_org_view.parentdesc=@PARENTDESE,t_org_view.grade=@GRADE,t_org_view.a0000=@A0000 WHERE t_org_view.UNIQUE_ID=@syncKey\n");
				frigger.append("          UPDATE t_org_view SET t_org_view.b0110_0=@CODEITEMID,t_org_view.corcode=@CORCODE,t_org_view.codesetid=@CODESETID,t_org_view.codeitemdesc=@CODEITEMDESC,t_org_view.parentid=@PARENTID,t_org_view.parentdesc=@PARENTDESE,t_org_view.parentGUIDKEY=@PARENTGUIDKEY,t_org_view.grade=@GRADE,t_org_view.levelA0000=@levelA0000 WHERE t_org_view.UNIQUE_ID=@syncKey\n");//更新时 上级机构唯一标识同步到单位视图表里和levelA0000 同步  wangb 20170811
				frigger.append("          SET @syncFlag = 2\n");
				frigger.append("        END\n");
				frigger.append("        ELSE\n");
				frigger.append("          SET @syncFlag = 3\n");
				// ------------------------------------外部同步字段 重复
				frigger.append("        IF EXISTS(SELECT 1 FROM t_org_view WHERE corcode=@CORCODE AND t_org_view.UNIQUE_ID<>@syncKey)\n");
				frigger.append("          UPDATE t_org_view SET corcode=NULL WHERE corcode=@CORCODE AND t_org_view.UNIQUE_ID<>@syncKey\n");
				// ------------------------------------外部同步字段 重复
				frigger.append("      END\n");
			}
			if (hsb.isSync_k01() && dbw.isExistTable("t_post_view", false)) {
				frigger.append("      IF ISNULL(@CODESETID,'##') = '@K'\n");
				frigger.append("      BEGIN\n");
				frigger.append("        IF NOT EXISTS(SELECT 1 FROM t_post_view WHERE UNIQUE_ID = @syncKey) AND @END_DATE > getdate()\n");
				frigger.append("        BEGIN\n");
//				frigger.append("          INSERT INTO t_post_view(UNIQUE_ID,e01a1_0,corcode,codesetid,codeitemdesc,parentid,parentdesc,grade,a0000) VALUES(@syncKey,@CODEITEMID,@CORCODE,@CODESETID,@CODEITEMDESC,@PARENTID,@PARENTDESE,@GRADE,@A0000)\n");
				frigger.append("          INSERT INTO t_post_view(UNIQUE_ID,e01a1_0,corcode,codesetid,codeitemdesc,parentid,parentdesc,parentGUIDKEY,grade,levelA0000) VALUES(@syncKey,@CODEITEMID,@CORCODE,@CODESETID,@CODEITEMDESC,@PARENTID,@PARENTDESE,@PARENTGUIDKEY,@GRADE,@levelA0000)\n");//新增时 上级机构唯一标识同步到岗位视图表里和levelA0000同步  wangb 20170811
				frigger.append("          SET @syncFlag = 1\n");
				frigger.append("        END\n");
				frigger.append("        ELSE IF @END_DATE > getdate()\n");
				frigger.append("        BEGIN\n");
//				frigger.append("          UPDATE t_post_view SET t_post_view.e01a1_0=@CODEITEMID,t_post_view.corcode=@CORCODE,t_post_view.codesetid=@CODESETID,t_post_view.codeitemdesc=@CODEITEMDESC,t_post_view.parentid=@PARENTID,t_post_view.parentdesc=@PARENTDESE,t_post_view.grade=@GRADE,t_post_view.a0000=@A0000 WHERE t_post_view.UNIQUE_ID=@syncKey\n");
				frigger.append("          UPDATE t_post_view SET t_post_view.e01a1_0=@CODEITEMID,t_post_view.corcode=@CORCODE,t_post_view.codesetid=@CODESETID,t_post_view.codeitemdesc=@CODEITEMDESC,t_post_view.parentid=@PARENTID,t_post_view.parentdesc=@PARENTDESE,t_post_view.parentGUIDKEY=@PARENTGUIDKEY,t_post_view.grade=@GRADE,t_post_view.levelA0000=@levelA0000 WHERE t_post_view.UNIQUE_ID=@syncKey\n");//更新时 上级机构唯一标识同步到岗位视图表里和levelA0000同步  wangb 20170811
				frigger.append("          SET @syncFlag = 2\n");
				frigger.append("        END\n");
				frigger.append("        ELSE\n");
				frigger.append("          SET @syncFlag = 3\n");
				// ------------------------------------外部同步字段 重复
				frigger.append("        IF EXISTS(SELECT 1 FROM t_post_view WHERE corcode=@CORCODE AND t_post_view.UNIQUE_ID<>@syncKey)\n");
				frigger.append("          UPDATE t_post_view SET corcode=NULL WHERE corcode=@CORCODE AND t_post_view.UNIQUE_ID<>@syncKey\n");
				// ------------------------------------外部同步字段 重复
				frigger.append("      END\n");
			}
			frigger.append("    END\n");
			// ---------新增修改
			// ---------删除操作
			frigger.append("    IF NOT EXISTS(SELECT 1 FROM INSERTED)\n");
			frigger.append("    BEGIN\n");
			if (hsb.isSync_b01() && dbw.isExistTable("t_org_view", false)) {
				/******
				 * 不知道为什么会清空，暂时删除
				 * wangzhongjun
				 * 2012-07-14
				 */
//				frigger.append("      IF ISNULL(@CODESETID,'@K') <> '@K'\n");

//				frigger.append("        " + SyncFriggerTools.emptyAllRecord("WHERE UNIQUE_ID=@syncKey", this.conn, HrSyncBo.B) + "\n");
			}
			if (hsb.isSync_k01() && dbw.isExistTable("t_post_view", false)) {
				/******
				 * 不知道为什么会清空，暂时删除
				 * wangzhongjun
				 * 2012-07-14
				 */
//				frigger.append("      IF ISNULL(@CODESETID,'##') = '@K'\n");
//				frigger.append("        " + SyncFriggerTools.emptyAllRecord("WHERE UNIQUE_ID=@syncKey", this.conn, HrSyncBo.K) + "\n");
			}
			frigger.append("      SET @syncFlag = 3\n");
			frigger.append("    END\n");
			// ---------删除操作
		}
		/*当更新 codeitemid时 记录原机构编码同步到数据视图表里  wangb 20170811*/
		frigger.append("    IF UPDATE(CODEITEMID)\n");
		frigger.append("    BEGIN\n");
		frigger.append("      IF ISNULL(@CODESETID,'##') <> '@K'\n");
		frigger.append("      BEGIN\n");
		frigger.append("        select @ORIGINCODEITEMID=codeitemid from deleted where GUIDKEY=@syncKey\n");
		frigger.append("        UPDATE t_org_view set origincodeitemid=@ORIGINCODEITEMID where unique_id=@syncKey\n");
		frigger.append("      END\n");
		frigger.append("      ELSE\n");
		frigger.append("      BEGIN\n");
		frigger.append("        select @ORIGINCODEITEMID=codeitemid from deleted where GUIDKEY=@syncKey\n");
		frigger.append("        UPDATE t_post_view set origincodeitemid=@ORIGINCODEITEMID where unique_id=@syncKey\n");
		frigger.append("      END\n");
		frigger.append("    END\n");
		
		/* 新增B0110_code、E0122_code、E01A1_code 三个字段 */
		if(dbw.isExistTable("t_hr_view", false) && hsb.isSync_a01()){
			frigger.append("    IF UPDATE(corCode)\n");
			frigger.append("    BEGIN\n");
			frigger.append("      IF ISNULL(@CODESETID,'##') = 'UN'\n");
			frigger.append("      BEGIN\n");
			frigger.append("        UPDATE t_hr_view SET B0110_code = @CORCODE WHERE B0110_0=@CODESETID\n");
			frigger.append("        EXEC PR_UP_SYNC_FLAG '2','B0110_0',@CODESETID,'','A_0'\n");
			frigger.append("      END\n");
			frigger.append("      ELSE IF ISNULL(@CODESETID,'##') = 'UM'\n");
			frigger.append("      BEGIN\n");
			frigger.append("        UPDATE t_hr_view SET E0122_code = @CORCODE WHERE E0122_0=@CODESETID\n");
			frigger.append("        EXEC PR_UP_SYNC_FLAG '2','E0122_0',@CODESETID,'','A_0'\n");
			frigger.append("      END\n");
			frigger.append("      ELSE IF ISNULL(@CODESETID,'##') = '@K'\n");
			frigger.append("      BEGIN\n");
			frigger.append("        UPDATE t_hr_view SET E01A1_code = @CORCODE WHERE E01A1_0=@CODESETID\n");
			frigger.append("        EXEC PR_UP_SYNC_FLAG '2','E01A1_0',@CODESETID,'','A_0'\n");
			frigger.append("      END\n");
			frigger.append("    END\n");
		}
		/* 新增B0110_code、E0122_code、E01A1_code 三个字段 */
		
		// 如果单位，部门是翻译型代码
		if ((hsb.isSync_b01() && dbw.isExistTable("t_org_view", false))
				|| (hsb.isSync_k01() && dbw.isExistTable("t_post_view", false))
				|| (hsb.isSync_a01() && dbw.isExistTable("t_hr_view", false) && (hrCode
						.indexOf("E01A1") != -1
						|| hrCode.indexOf("B0110") != -1 || hrCode
						.indexOf("E0122") != -1))) {
			frigger.append("    IF UPDATE(CODEITEMDESC)\n");
			frigger.append("    BEGIN\n");
			if ((hsb.isSync_a01() && dbw.isExistTable("t_hr_view", false) && hrCode
					.indexOf("E01A1") != -1)
					|| (hsb.isSync_k01()
							&& dbw.isExistTable("t_post_view", false) && postCode
							.indexOf("E01A1") != -1)) {
				frigger.append("      IF @CODESETID='@K'\n");
				frigger.append("      BEGIN\n");
				if (hsb.isSync_k01() && dbw.isExistTable("t_post_view", false)
						&& postCode.indexOf("E01A1") != -1) {
					String cusE01A1 = hsb.getAppAttributeValue(HrSyncBo.K,
							"E01A1");
					frigger.append("        UPDATE t_post_view SET "
							+ cusE01A1
							+ " = @CODEITEMDESC WHERE E01A1_0 = @CODEITEMID\n");
				}
				if (hsb.isSync_a01() && dbw.isExistTable("t_hr_view", false)
						&& hrCode.indexOf("E01A1") != -1) {
					String cusE01A1 = hsb.getAppAttributeValue(HrSyncBo.A,
							"E01A1");
					frigger.append("        UPDATE t_hr_view SET " + cusE01A1
							+ " = @CODEITEMDESC WHERE E01A1_0 = @CODEITEMID\n");
				}
				frigger.append("      END\n");
			}
			if ((hsb.isSync_a01() && dbw.isExistTable("t_hr_view", false) && (hrCode
					.indexOf("E0122") != -1 || hrCode.indexOf("B0110") != -1))
					|| (hsb.isSync_k01() && dbw.isExistTable("t_post_view",
							false))
					|| (hsb.isSync_b01() && dbw.isExistTable("t_org_view",
							false))) {
				frigger.append("      IF @CODESETID<>'@K'\n");
				frigger.append("      BEGIN\n");
				if (hsb.isSync_a01() && dbw.isExistTable("t_hr_view", false)
						&& hrCode.indexOf("B0110") != -1) {
					String cusB0110 = hsb.getAppAttributeValue(HrSyncBo.A,
							"B0110");
					frigger.append("        UPDATE t_hr_view SET " + cusB0110
							+ " = @CODEITEMDESC WHERE B0110_0 = @CODEITEMID\n");
				}
				if (hsb.isSync_k01() && dbw.isExistTable("t_post_view", false)) {
					frigger.append("        UPDATE t_post_view SET PARENTDESC = @CODEITEMDESC WHERE PARENTID = @CODEITEMID\n");
				}
				if (hsb.isSync_b01() && dbw.isExistTable("t_org_view", false)) {
					frigger.append("        UPDATE t_org_view SET PARENTDESC = @CODEITEMDESC WHERE PARENTID = @CODEITEMID\n");
				}
				if ((hsb.isSync_b01() && dbw.isExistTable("t_org_view", false)
							&& orgCode.indexOf("B0110") != -1) || 
						(hsb.isSync_k01() && dbw.isExistTable("t_post_view", false)
								&& postCode.indexOf("E0122") != -1) || 
						(hsb.isSync_a01() && dbw.isExistTable("t_hr_view", false)
								&& hrCode.indexOf("E0122") != -1)) {
					Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.conn);
				    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
				    if(uplevel==null||uplevel.length()==0)
				    	uplevel="0";
					frigger.append("        DECLARE CUR_ORGDesc CURSOR FOR SELECT CODEITEMID FROM ORGANIZATION WHERE CODEITEMID LIKE @CODEITEMID + '%' AND CODESETID = @CODESETID AND GRADE <= (@GRADE + " + uplevel + ");\n");
					frigger.append("        OPEN CUR_ORGDesc\n");
					frigger.append("        FETCH NEXT from CUR_ORGDesc into @ORGID\n");
					frigger.append("        WHILE (@@FETCH_STATUS = 0)\n");
					frigger.append("        BEGIN\n");
					frigger.append("          SELECT @ORGDesc = dbo.FUN_GET_CODEDESC(getdate(),'B01','B0110',@ORGID);\n");
					
					if (hsb.isSync_b01() && dbw.isExistTable("t_org_view", false)
							&& orgCode.indexOf("B0110") != -1){
						String cusB0110 = hsb.getAppAttributeValue(HrSyncBo.B,"B0110");
						frigger.append("          UPDATE t_org_view SET " + cusB0110 + " = @ORGDesc WHERE B0110_0=@ORGID\n");
						frigger.append("          EXEC PR_UP_SYNC_FLAG '2','B0110_0',@ORGID,'','B_0'\n");
					}
					if (hsb.isSync_k01()
							&& dbw.isExistTable("t_post_view", false)
							&& postCode.indexOf("E0122") != -1) {
						String cusE0122 = hsb.getAppAttributeValue(HrSyncBo.K, "E0122");
						frigger.append("          IF @CODESETID = 'UM'\n");
						frigger.append("          BEGIN\n");
						frigger.append("            UPDATE t_post_view SET "+ cusE0122+ " = @ORGDesc WHERE E0122_0=@ORGID\n");
						frigger.append("            EXEC PR_UP_SYNC_FLAG '2','E0122_0',@ORGID,'','K_0'\n");
						frigger.append("          END;\n");
					}
					if (hsb.isSync_a01()
							&& dbw.isExistTable("t_hr_view", false)
							&& hrCode.indexOf("E0122") != -1) {
						String cusE0122 = hsb.getAppAttributeValue(HrSyncBo.A,"E0122");
						frigger.append("          IF @CODESETID = 'UM'\n");
						frigger.append("          BEGIN\n");
						frigger.append("            UPDATE t_hr_view SET " + cusE0122 + " = @ORGDesc WHERE E0122_0=@ORGID\n");
						frigger.append("            EXEC PR_UP_SYNC_FLAG '2','E0122_0',@ORGID,'','A_0'\n");
						frigger.append("          END;\n");
					}
					
					
					
					frigger.append("          FETCH NEXT FROM CUR_ORGDesc INTO @ORGID\n");
					frigger.append("        END\n");
					frigger.append("        CLOSE CUR_ORGDesc\n");
					frigger.append("        DEALLOCATE CUR_ORGDesc\n");
				}
				frigger.append("      END\n");
			}
			frigger.append("    END\n");
		}
		// 如果单位，部门是翻译型代码

		if (hsb.isSync_b01() && dbw.isExistTable("t_org_view", false)) {
			frigger.append("    IF ISNULL(@CODESETID,'@K')<>'@K'\n");
			frigger.append("      EXEC PR_UP_SYNC_FLAG @syncFlag,'',@syncKey,'','B'\n");
		}
		if (hsb.isSync_k01() && dbw.isExistTable("t_post_view", false)) {
			frigger.append("    IF  ISNULL(@CODESETID,'##')='@K'\n");
			frigger.append("      EXEC PR_UP_SYNC_FLAG @syncFlag,'',@syncKey,'','K'\n");
		}
		
		frigger.append(" END;\n");
		
//		frigger.append("  FETCH NEXT FROM CUR_ORG INTO @syncKey,@CODEITEMID,@CORCODE,@CODESETID,@CODEITEMDESC,@PARENTID,@GRADE,@END_DATE,@A0000\n");
		frigger.append("  FETCH NEXT FROM CUR_ORG INTO @syncKey,@CODEITEMID,@CORCODE,@CODESETID,@CODEITEMDESC,@PARENTID,@GRADE,@END_DATE,@levelA0000\n");//同步levelA0000  wangb 20170811
		frigger.append("  END\n");
		// --------------------游标遍历-------------------------
		frigger.append("  CLOSE CUR_ORG;\n");
		frigger.append("  DEALLOCATE CUR_ORG;\n");
		frigger.append("END\n");
		return frigger.toString();
	}

	/**
	 * 对机构子集表创建触发器
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            字段名
	 * @param tranfields
	 *            翻译型字段名
	 * @return
	 */
	private String toOrgSub(String table, List columns, List tranfields) {
		if (columns == null || columns.isEmpty())
			return "";
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE TRIGGER TR_ORG_CHANGE_" + table + " ON [dbo].["
				+ table + "]\n");
		frigger.append("AFTER DELETE,INSERT,UPDATE\n");
		frigger.append("AS\n");
		frigger.append("DECLARE\n");
		frigger.append("  @strSQL    varchar(1000),\n");
		frigger.append("  @outappId  varchar(100),\n");
		frigger.append("  @B0110     varchar(30),\n");
		frigger.append("  @syncKey  varchar(100),\n");
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,
				SyncFriggerTools.M_MARK));
		frigger.append("  @I9999     integer,\n");
		frigger.append("  @MaxI9999  integer\n");
		frigger.append("\n");

		frigger
				.append("IF NOT EXISTS(SELECT 1 FROM DELETED) OR NOT EXISTS(SELECT 1 FROM INSERTED) OR");
		for (int i = 0; i < columns.size(); i++) {
			frigger.append(" UPDATE(");
			frigger.append(columns.get(i));
			frigger.append(") OR");
		}
		frigger.append(" UPDATE(I9999)");
		frigger.append("\n");

		frigger.append("BEGIN\n");
		frigger.append("  IF EXISTS(SELECT 1 FROM INSERTED)\n");
		frigger.append("    DECLARE C_" + table
				+ " CURSOR FOR SELECT B0110,I9999 FROM INSERTED\n");
		frigger.append("  ELSE\n");
		frigger.append("    DECLARE C_" + table
				+ " CURSOR FOR SELECT B0110,I9999 FROM DELETED\n");

		frigger.append("  OPEN C_" + table + "\n");
		frigger.append("  FETCH NEXT FROM C_" + table
						+ " INTO @B0110,@I9999\n");
		frigger.append("  WHILE (@@FETCH_STATUS = 0)\n");
		frigger.append("  BEGIN\n");
		frigger.append("    SELECT @syncKey = GUIDKEY FROM ORGANIZATION WHERE CODEITEMID=@B0110;\n");
		
		
		frigger.append("  IF((select count(*) from inserted  a inner join deleted  b on a.b0110 = b.b0110 and a.I9999 = b.I9999  "); 
		for (int i = 0; i < columns.size(); i++) {
			frigger.append(" and ((a.");
			frigger.append(columns.get(i));
			frigger.append(" is null and b.");
			frigger.append(columns.get(i));
			frigger.append(" is null) or a.");
			frigger.append(columns.get(i));
			frigger.append("=b.");
			frigger.append(columns.get(i));
			frigger.append(") ");
		}
		
		frigger.append("where a.b0110=@B0110)=0)\n");
		frigger.append("BEGIN\n");
		
		frigger.append("    IF ISNULL(@syncKey,'')<>''\n");
		frigger.append("    BEGIN\n");
		frigger.append("      IF NOT EXISTS(SELECT 1 FROM " + table
				+ " WHERE B0110=@B0110)\n");
		frigger.append("      BEGIN\n");
		frigger.append("        "
				+ SyncFriggerTools.emptySubRecord("t_org_view", columns,
						"WHERE UNIQUE_ID=@syncKey", conn, HrSyncBo.B) + "\n");
		frigger.append("      END\n");
		frigger.append("      ELSE\n");
		frigger.append("      BEGIN\n");
		frigger.append("        SET @MaxI9999=0\n");
		frigger.append("        SELECT @MaxI9999 = Max(I9999) FROM " + table + " WHERE B0110=@B0110\n");
		frigger
				.append("        IF ISNULL(@MaxI9999,0)<>0 AND (@I9999>=@MaxI9999 OR EXISTS(SELECT 1 FROM DELETED WHERE B0110=@B0110 AND I9999>=@MaxI9999))\n");
		frigger.append("        BEGIN\n");
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					frigger.append("      SELECT @" + column + "Desc =" + column + "  FROM " + table + " WHERE I9999=@MaxI9999 AND B0110 = @B0110;\n");
					frigger.append("      SELECT @" + column + "Desc = dbo.FUN_GET_CODEDESC(getdate(),'" + table + "','" + column + "',@" + column + "Desc);\n");
				}
			}
			frigger.append("\n");
		}
		frigger
				.append("          "
						+ SyncFriggerTools
								.UpData(
										HrSyncBo.B,
										columns,
										tranfields,
										"B",
										"FROM t_org_view t INNER JOIN (SELECT * FROM "
												+ table
												+ " WHERE I9999=@MaxI9999 AND B0110=@B0110) B ON B0110_0=B.B0110 WHERE UNIQUE_ID=@syncKey",
										SyncFriggerTools.M_MARK, this.conn)
						+ "\n");
		frigger.append("        END\n");
		frigger.append("      END\n");
		frigger.append("      IF EXISTS(SELECT 1 FROM ORGANIZATION WHERE GUIDKEY=@syncKey AND GETDATE() BETWEEN START_DATE AND END_DATE)\n");
		frigger.append("      BEGIN\n");
		frigger.append("        EXEC PR_UP_SYNC_FLAG '2','',@syncKey,'','B_'\n");
		frigger.append("      END\n");
		frigger.append("    END\n");
		frigger.append("    END\n");
		frigger.append("    FETCH NEXT FROM C_" + table + " INTO @B0110,@I9999\n");
		frigger.append("  END\n");
		frigger.append("  CLOSE C_" + table + "\n");
		frigger.append("  DEALLOCATE C_" + table + "\n");
		frigger.append("END\n");
		return frigger.toString();
	}

	/**
	 * 对B01表创建触发器
	 * 
	 * @param columns
	 *            字段名
	 * @param tranfields
	 *            翻译型字段名
	 * @return
	 */
	private String toOrgB01(List columns, List tranfields) {
		if (columns == null)
			return "";
		List list = new ArrayList();
		list.addAll(columns);
		if (list.indexOf("B0110") == -1) {
			list.add("B0110");
		}
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE TRIGGER TR_ORG_CHANGE_B01 ON [dbo].[B01]\n");
		frigger.append("AFTER INSERT,UPDATE,DELETE\n");
		frigger.append("AS\n");
		frigger.append("DECLARE\n");
		frigger.append("  @strSQL    varchar(1000),\n");
		frigger.append("  @outappId  varchar(100),\n");
		frigger.append("  @aParentDesc varchar(100),\n");
		frigger.append("  @syncKey  varchar(100),\n");
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String codeVar = (String) it.next();
				if (codeVar != null && codeVar.length() > 0) {
					frigger.append("  @" + codeVar + "Desc varchar(100),\n");
				}
			}
		}
		frigger.append("  @B0110     varchar(30)\n");
		frigger.append("\n");

		frigger.append("IF NOT EXISTS(SELECT 1 FROM DELETED) OR NOT EXISTS(SELECT 1 FROM INSERTED) OR");
		for (int i = 0; i < list.size(); i++) {
			frigger.append(" UPDATE(");
			frigger.append(list.get(i));
			frigger.append(") OR");
		}
		frigger.delete(frigger.length() - 3, frigger.length());
		frigger.append("\n");
		frigger.append("BEGIN\n");
		frigger.append("  IF EXISTS(SELECT 1 FROM INSERTED)\n");
		frigger.append("    DECLARE C_B01 CURSOR FOR SELECT B0110 FROM INSERTED\n");
		frigger.append("  ELSE\n");
		frigger.append("    DECLARE C_B01 CURSOR FOR SELECT B0110 FROM DELETED\n");
		frigger.append("  OPEN C_B01");
		frigger.append("  FETCH NEXT FROM C_B01 INTO @B0110\n");
		frigger.append("  WHILE (@@FETCH_STATUS = 0)\n");
		frigger.append("  BEGIN\n");
		frigger.append("    SELECT @syncKey = GUIDKEY FROM ORGANIZATION WHERE CODEITEMID=@B0110 and END_DATE>GETDATE();\n");
		
		
		frigger.append("  IF((select count(*) from inserted a inner join  deleted  b on a.B0110 = b.B0110  "); 
		
		for (int i = 0; i < list.size(); i++) {
			frigger.append(" and ((a.");
			frigger.append(list.get(i));
			frigger.append(" is null and b.");
			frigger.append(list.get(i));
			frigger.append(" is null) or a.");
			frigger.append(list.get(i));
			frigger.append("=b.");
			frigger.append(list.get(i));
			frigger.append(") ");
		}
		
		frigger.append(" where a.b0110=@B0110)=0)\n");
		frigger.append("BEGIN\n");
		
		frigger.append("    IF ISNULL(@syncKey,'')<>''\n");
		frigger.append("    BEGIN\n");
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					frigger.append("      SELECT @" + column + "Desc =" + column + "  FROM B01 WHERE B0110 = @B0110;\n");
					frigger.append("      SELECT @" + column + "Desc = dbo.FUN_GET_CODEDESC(getdate(),'B01','" + column + "',@" + column + "Desc);\n");
				}
			}
			frigger.append("\n");
		}

		frigger.append("      IF EXISTS(SELECT 1 FROM B01 WHERE B0110 = @B0110)\n");
		frigger.append("      BEGIN\n");
		frigger.append("          "
				+ SyncFriggerTools.updateB01toM(columns, tranfields, this.conn)
				+ "\n");
		frigger.append("      END\n");
		frigger.append("      ELSE\n");
		frigger.append("      BEGIN\n");
		frigger.append("        "
				+ SyncFriggerTools.emptySubRecord("t_org_view", columns,
						"WHERE UNIQUE_ID=@syncKey", conn, HrSyncBo.B) + "\n");
		frigger.append("      END\n");
		frigger.append("      IF EXISTS(SELECT 1 FROM ORGANIZATION WHERE GUIDKEY=@syncKey AND GETDATE() BETWEEN START_DATE AND END_DATE)\n");
		frigger.append("        EXEC PR_UP_SYNC_FLAG '2','',@syncKey,'','B_'\n");
		frigger.append("    END\n");
		
		frigger.append("  END\n");
		frigger.append("    FETCH NEXT FROM C_B01 INTO @B0110\n");
		frigger.append("  END\n");
		frigger.append("  CLOSE C_B01\n");
		frigger.append("  DEALLOCATE C_B01\n");
		frigger.append("END\n");
		return frigger.toString();
	}

	/**
	 * 对职位子集创建触发器
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            字段名
	 * @param tranfields
	 *            翻译型字段名
	 * @return
	 */
	private String toPostSub(String table, List columns, List tranfields) {
		if (columns == null || columns.isEmpty())
			return "";
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE TRIGGER TR_POST_CHANGE_" + table + " ON [dbo].["
				+ table + "]\n");
		frigger.append("AFTER DELETE,INSERT,UPDATE\n");
		frigger.append("AS\n");
		frigger.append("DECLARE\n");
		frigger.append("  @strSQL    varchar(1000),\n");
		frigger.append("  @outappId  varchar(100),\n");
//		frigger.append("  @syncFlag  varchar(1),\n");
		frigger.append("  @E01A1     varchar(30),\n");
		frigger.append("  @syncKey  varchar(100),\n");
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,
				SyncFriggerTools.M_MARK));
		frigger.append("  @I9999     integer,\n");
		frigger.append("  @MaxI9999  integer\n");
		frigger.append("\n");

		frigger.append("IF NOT EXISTS(SELECT 1 FROM DELETED) OR NOT EXISTS(SELECT 1 FROM INSERTED) OR");
		for (int i = 0; i < columns.size(); i++) {
			frigger.append(" UPDATE(");
			frigger.append(columns.get(i));
			frigger.append(") OR");
		}
		frigger.append(" UPDATE(I9999)");
		frigger.append("\n");

		frigger.append("BEGIN\n");
		frigger.append("  IF EXISTS(SELECT 1 FROM INSERTED)\n");
		frigger.append("    DECLARE C_" + table
				+ " CURSOR FOR SELECT E01A1,I9999 FROM INSERTED\n");
		frigger.append("  ELSE\n");
		frigger.append("    DECLARE C_" + table
				+ " CURSOR FOR SELECT E01A1,I9999 FROM DELETED\n");
		frigger.append("  OPEN C_" + table + "\n");
		frigger.append("  FETCH NEXT FROM C_" + table
						+ " INTO @E01A1,@I9999\n");
		frigger.append("  WHILE (@@FETCH_STATUS = 0)\n");
		frigger.append("  BEGIN\n");
		frigger.append("    SELECT @syncKey = GUIDKEY FROM ORGANIZATION WHERE CODEITEMID=@E01A1;\n");
		
		frigger.append("  IF((select count(*) from inserted a inner join deleted b on a.E01A1 = b.E01A1 and a.I9999 = b.I9999 "); 
		
		for (int i = 0; i < columns.size(); i++) {
			frigger.append(" and ((a.");
			frigger.append(columns.get(i));
			frigger.append(" is null and b.");
			frigger.append(columns.get(i));
			frigger.append(" is null) or a.");
			frigger.append(columns.get(i));
			frigger.append("=b.");
			frigger.append(columns.get(i));
			frigger.append(") ");
		}
		
		
		
		
		frigger.append(" where a.e01a1=@E01A1)=0)\n");
		frigger.append("BEGIN\n");
		
		frigger.append("    IF ISNULL(@syncKey,'')<>''\n");
		frigger.append("    BEGIN\n");
		frigger.append("      IF NOT EXISTS(SELECT 1 FROM " + table + " WHERE E01A1=@E01A1)\n");
		frigger.append("      BEGIN\n");
		frigger.append("        "
				+ SyncFriggerTools.emptySubRecord("t_post_view", columns,
						"WHERE UNIQUE_ID=@syncKey", conn, HrSyncBo.K) + "\n");
		frigger.append("      END\n");
		frigger.append("      ELSE\n");
		frigger.append("      BEGIN\n");
		frigger.append("        SELECT @MaxI9999 = Max(I9999) FROM " + table
				+ " WHERE E01A1=@E01A1\n");
		frigger
				.append("        IF ISNULL(@MaxI9999,0)<>0 AND (@I9999>=@MaxI9999 OR EXISTS(SELECT 1 FROM DELETED WHERE E01A1=@E01A1 AND I9999>=@MaxI9999))\n");
		frigger.append("        BEGIN\n");
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					frigger.append("      SELECT @" + column + "Desc =" + column + "  FROM " + table + " WHERE I9999=@MaxI9999 AND E01A1 = @E01A1;\n");
					frigger.append("      SELECT @" + column + "Desc = dbo.FUN_GET_CODEDESC(getdate(),'" + table + "','" + column + "',@" + column + "Desc);\n");
				}
			}
			frigger.append("\n");
		}

		frigger
				.append("          "
						+ SyncFriggerTools
								.UpData(
										HrSyncBo.K,
										columns,
										tranfields,
										"K",
										"FROM t_post_view t INNER JOIN (SELECT * FROM "
												+ table
												+ " WHERE E01A1=@E01A1 AND I9999=@MaxI9999) K ON t.E01A1_0 = K.E01A1 WHERE UNIQUE_ID=@syncKey",
										SyncFriggerTools.M_MARK, this.conn)
						+ "\n");
		frigger.append("        END\n");
		frigger.append("      END\n");
		frigger.append("      IF EXISTS(SELECT 1 FROM ORGANIZATION WHERE GUIDKEY=@syncKey AND GETDATE() BETWEEN START_DATE AND END_DATE)\n");
		frigger.append("        EXEC PR_UP_SYNC_FLAG '2','',@syncKey,'','K_'\n");
		frigger.append("    END\n");
		
		frigger.append("  END\n");
		
		frigger.append("    FETCH NEXT FROM C_" + table + " INTO @E01A1,@I9999\n");
		frigger.append("  END\n");
		frigger.append("  CLOSE C_" + table + "\n");
		frigger.append("  DEALLOCATE C_" + table + "\n");
		frigger.append("END\n");
		return frigger.toString();
	}

	/**
	 * 对K01表创建触发器
	 * 
	 * @param columns
	 *            字段名
	 * @param tranfields
	 *            翻译型字段名
	 * @return
	 */
	private String toPostK01(List columns, List tranfields) {
		if (columns == null)
			return "";
		List list = new ArrayList();
		list.addAll(columns);
		if (list.indexOf("E01A1") == -1) {
			list.add("E01A1");
		}
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE TRIGGER TR_POST_CHANGE_K01 ON [dbo].[K01]\n");
		frigger.append("AFTER INSERT,UPDATE,DELETE\n");
		frigger.append("AS\n");
		frigger.append("DECLARE\n");
		frigger.append("  @strSQL    varchar(1000),\n");
		frigger.append("  @outappId  varchar(100),\n");
		frigger.append("  @aParentDesc varchar(100),\n");
		frigger.append("  @syncKey  varchar(100),\n");
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,
				SyncFriggerTools.M_MARK));
		frigger.append("  @E01A1     varchar(30),\n");
		frigger.append("  @E0122     varchar(30),\n");
		frigger.append("  @recExists integer\n");
		frigger.append("\n");

		frigger
				.append("IF NOT EXISTS(SELECT 1 FROM DELETED) OR NOT EXISTS(SELECT 1 FROM INSERTED) OR");
		for (int i = 0; i < list.size(); i++) {
			frigger.append(" UPDATE(");
			frigger.append(list.get(i));
			frigger.append(") OR");
		}
		frigger.delete(frigger.length() - 3, frigger.length());
		frigger.append("\n");

		frigger.append("BEGIN\n");
		frigger.append("  IF EXISTS(SELECT 1 FROM INSERTED)\n");
		frigger.append("    DECLARE C_K01 CURSOR FOR SELECT E01A1,E0122 FROM INSERTED\n");
		frigger.append("  ELSE\n");
		frigger.append("    DECLARE C_K01 CURSOR FOR SELECT E01A1,E0122 FROM DELETED\n");
		frigger.append("  OPEN C_K01");
		frigger.append("  FETCH NEXT FROM C_K01 INTO @E01A1,@E0122\n");
		frigger.append("  WHILE (@@FETCH_STATUS = 0)\n");
		frigger.append("  BEGIN\n");
		frigger.append("    SELECT @syncKey = GUIDKEY FROM ORGANIZATION WHERE CODEITEMID=@E01A1;\n");
		
		frigger.append("  IF((select count(*) from inserted  a inner join deleted b on a.E01A1 = b.E01A1  "); 
		
		for (int i = 0; i < columns.size(); i++) {
			frigger.append(" and ((a.");
			frigger.append(columns.get(i));
			frigger.append(" is null and b.");
			frigger.append(columns.get(i));
			frigger.append(" is null) or a.");
			frigger.append(columns.get(i));
			frigger.append("=b.");
			frigger.append(columns.get(i));
			frigger.append(") ");
		}
		
		
		frigger.append(" where a.e01a1=@E01A1)=0)\n");
		frigger.append("BEGIN\n");
		
		frigger.append("    IF ISNULL(@syncKey,'')<>''\n");
		frigger.append("    BEGIN\n");
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					frigger.append("      SELECT @" + column + "Desc =" + column + "  FROM K01 WHERE E01A1 = @E01A1;\n");
					frigger.append("      SELECT @" + column + "Desc = dbo.FUN_GET_CODEDESC(getdate(),'K01','" + column + "',@" + column + "Desc);\n");
				}
			}
			frigger.append("\n");
		}

		frigger.append("      IF EXISTS(SELECT 1 FROM K01 WHERE E01A1=@E01A1)\n");
		frigger.append("      BEGIN\n");
		frigger.append("        "
				+ SyncFriggerTools.updateK01toM(columns, tranfields, this.conn)
				+ "\n");
		frigger.append("      END\n");
		frigger.append("      ELSE\n");
		frigger.append("      BEGIN\n");
		frigger.append("        "
				+ SyncFriggerTools.emptySubRecord("t_post_view", columns,
						"WHERE UNIQUE_ID=@syncKey", this.conn, HrSyncBo.K) + "\n");
		frigger.append("      END\n");
		frigger.append("      IF EXISTS(SELECT 1 FROM ORGANIZATION WHERE GUIDKEY=@syncKey AND GETDATE() BETWEEN START_DATE AND END_DATE)\n");
		frigger.append("        EXEC PR_UP_SYNC_FLAG '2','',@syncKey,'','K_'\n");
		frigger.append("    END\n");
		
		frigger.append("  END\n");
		frigger.append("    FETCH NEXT FROM C_K01 INTO @E01A1,@E0122\n");
		frigger.append("  END\n");
		frigger.append("  CLOSE C_K01\n");
		frigger.append("  DEALLOCATE C_K01\n");
		frigger.append("END\n");
		return frigger.toString();
	}
	
	private String createPhotoTrigger(String dbName) {
		StringBuffer trigger = new StringBuffer();
		trigger.append("CREATE TRIGGER TR_photo_CHANGE_");
		trigger.append(dbName);
		trigger.append("A00 ON " + dbName + "A00 \r\n");
		trigger.append(" AFTER DELETE,INSERT,UPDATE \r\n");
		trigger.append(" AS \r\n");
		trigger.append(" DECLARE \r\n");
		trigger.append(" @strSQL   varchar(1000), \r\n");
		trigger.append(" @outappid varchar(100), \r\n");
		trigger.append(" @A0100    varchar(30), \r\n");
		trigger.append(" @syncFlag char(1), \r\n");
		trigger.append(" @syncKey  varchar(100), \r\n");
		trigger.append(" @MaxI9999 integer, \r\n");
		trigger.append(" @I9999    integer, \r\n");
		trigger.append(" @sqls varchar(1000) \r\n");
		trigger.append(" IF EXISTS(SELECT 1 FROM DELETED where upper(flag)='P') OR EXISTS(SELECT 1 FROM INSERTED where upper(flag)='P') OR UPDATE(ext) \r\n");
		trigger.append(" BEGIN \r\n");

		
		trigger.append(" --创建游标，查询变动的a0100及i9999 \r\n");
		trigger.append(" IF EXISTS(SELECT 1 FROM INSERTED where upper(flag)='P') \r\n");
		trigger.append(" DECLARE C_USRA00 CURSOR FOR SELECT A0100,I9999 FROM INSERTED \r\n");
		trigger.append(" ELSE \r\n");
		trigger.append(" DECLARE C_USRA00 CURSOR FOR SELECT A0100,I9999 FROM DELETED \r\n");

		
		trigger.append(" --打开游标取数 \r\n");
		trigger.append(" OPEN C_USRA00  \r\n");
		trigger.append(" FETCH NEXT FROM C_USRA00 INTO @A0100,@I9999 \r\n");
		trigger.append(" WHILE (@@FETCH_STATUS = 0) \r\n");
		trigger.append(" BEGIN \r\n");
		trigger.append(" --查询该人员的唯一id \r\n");
		trigger.append(" SET @syncFlag=0 \r\n");
		trigger.append(" SELECT @syncKey = GUIDKEY FROM " + dbName + "A01 WHERE A0100=@A0100 \r\n");

      			

		trigger.append(" --查询所有已系统代号 \r\n");

		trigger.append(" DECLARE outapp_cur CURSOR FOR SELECT sys_id FROM t_sys_outsync WHERE state=1; \r\n");
		trigger.append(" OPEN outapp_cur; \r\n");
		trigger.append(" FETCH outapp_cur INTO @outappid; \r\n");
		trigger.append(" WHILE (@@FETCH_STATUS = 0) \r\n");
		trigger.append(" BEGIN \r\n");
		trigger.append(" SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + 'P=2 ,sdate = GETDATE() WHERE  unique_id =''' + @syncKey +''''; \r\n");
		trigger.append(" EXEC(@strSQL); \r\n");
		trigger.append(" FETCH outapp_cur INTO @outappid; \r\n");
		trigger.append(" END \r\n");

		trigger.append(" CLOSE outapp_cur; \r\n");
		trigger.append(" DEALLOCATE outapp_cur; \r\n");



		trigger.append(" FETCH NEXT FROM C_USRA00 INTO @A0100,@I9999 \r\n");
		trigger.append(" END \r\n");
		trigger.append(" CLOSE C_USRA00 \r\n");
		trigger.append(" DEALLOCATE C_USRA00 \r\n");
		trigger.append(" END");
		
		return trigger.toString();
	}
	
	private String createFieldChangeTrigger(String type, List fieldList, List sysIdList) {
		StringBuffer trigger = new StringBuffer();
		
		String tab = "";
		if ("A".equalsIgnoreCase(type)) {
			tab = "HR";
		} else if ("B".equalsIgnoreCase(type)) {
			tab = "ORG";
		} else if ("K".equalsIgnoreCase(type)) {
			tab = "POST";
		}
		
		trigger.append("create trigger TR_FIELD_CHANGE_T_");
		trigger.append(tab);
		trigger.append("_VIEW \r\n");
		trigger.append("	on t_");
		trigger.append(tab);
		trigger.append("_view after insert,update as\r\n");
		
//		if (update(unique_id)  or update(e0122)  or update(e01a1)  or update(a0101)  or update(a0177)  or update(a0107)  or update(c0183)  or update(c0104)  or update(c0102)  or update(e0127)  or update(c010t)  or update(c01ux)  or update(c0109)  or update(c01uy)  or update(c01um)  or update(a0111)  or update(c0101)  or update(a0121)  or update(c01uu)  or update(a0141)  or update(c01uj)  or update(c0105)  or update(c01sc)  or update(c01uz)  or update(a0114)  or update(a0127)  or update(e0104)  or update(c01uc)  or update(c01tc)  or update(c010e)  or update(c010d)  or update(c010u)  or update(c01un)  or update(h01sj)  or update(h01sk)  or update(b0110)  or update(c01uw)  or update(a0161)  or update(e0110)  or update(e0112)  or update(c01ui)  or update(h01sr)  or update(h01ss)  or update(h01st)  or update(c01vi)  or update(c010a)  or update(c01vq)  or update(c01vk)  or update(a0154)  or update(c010n)  or update(h01su)  or update(a0302)  or update(a0305)  or update(a0303)  or update(a0304)  or update(a0306)  or update(a0307)  or update(a0301)  or update(h01sl)  or update(aa305)  or update(c01vl)  or update(nbase)  or update(b0110_code)  or update(e0122_code)  or update(e01a1_code)  or update(b0110_0)  or update(e0122_0)  or update(e01a1_0)  or update(username)  or update(userpassword))
//			begin
		
		trigger.append(" if (update(unique_id) ");
		
		
		for (int i = 0; i < fieldList.size(); i++) {
			trigger.append(" or  update(");
			trigger.append(fieldList.get(i));
			trigger.append(")");
		}
		
		trigger.append(" )  \r\n");
		
		trigger.append(" begin  \r\n");
		
		
		
		trigger.append("		DECLARE @isExists int,@unique_id varchar(38),@oldvalue varchar(500),");
		
		for (int i = 0; i < fieldList.size(); i++) {
			trigger.append("@");
			trigger.append(fieldList.get(i));
			trigger.append(" varchar(500)");
			
			if (i != fieldList.size() - 1) {
				trigger.append(",");
			}
		}
		
		trigger.append(";\r\n");

		trigger.append("		--新增\r\n");
		trigger.append("		if exists(select 1 from inserted) and not exists(select 1 from deleted)\r\n");
		trigger.append(" 		begin\r\n");
		trigger.append("        	DECLARE C_USRA01545 CURSOR FOR SELECT unique_id,");
		
		for (int i = 0; i < fieldList.size(); i++) {
			
			
			String field = (String) fieldList.get(i);
			
			FieldItem item = DataDictionary.getFieldItem(field);
			if (item != null && item.isDate()) {
				trigger.append("CONVERT(varchar(100), " + field + ", 25) ");
				trigger.append(field);
			} else {
				trigger.append(fieldList.get(i));
			}
			
//			trigger.append(fieldList.get(i));
			
			if (i != fieldList.size() - 1) {
				trigger.append(",");
			}
		}
				
		trigger.append(" FROM INSERTED;\r\n");
		                       
		trigger.append("        	OPEN C_USRA01545;\r\n");
		trigger.append("            FETCH NEXT FROM C_USRA01545 INTO @unique_id,");
		
		for (int i = 0; i < fieldList.size(); i++) {
			trigger.append("@");
			trigger.append(fieldList.get(i));
			
			if (i != fieldList.size() - 1) {
				trigger.append(",");
			}
		}
		
		trigger.append(";\r\n");
		trigger.append("            WHILE (@@FETCH_STATUS = 0)\r\n");
		trigger.append("            BEGIN\r\n");
		                     	
		for (int i = 0; i < fieldList.size(); i++) {
			String field = (String) fieldList.get(i);
			trigger.append("            	if @" + field + " is not null and @" + field + "<>'' and update(" + field + ")\r\n");
			trigger.append("                begin\r\n");
			
			for (int j = 0; j < sysIdList.size(); j++) {
				String sysId = (String)sysIdList.get(j);
				trigger.append("                	--判断记录是否存在\r\n");
				trigger.append("                    select @isExists =count(*) from t_");
				trigger.append(tab);
				trigger.append("_view_log where UNIQUE_ID=@unique_id and fielditemid='" + field.toUpperCase() + "' and sysid='" + sysId.toUpperCase()+ "';\r\n");
				                     	        
				trigger.append("                    --记录不存在需要新增\r\n");
				trigger.append("                    IF @isExists = 0\r\n");
				trigger.append("                    --更新变化后的值\r\n");
				trigger.append("                    	insert into t_");
				trigger.append(tab);
				trigger.append("_view_log(UNIQUE_ID,fielditemid,oldValue,newValue,Sysid,flag) values(@unique_id,'" + field.toUpperCase() + "','',@" + field + ",'" + sysId.toUpperCase()+ "',1); \r\n");
				                     	  
				trigger.append("                    ELSE--记录存在需要更新\r\n");
				trigger.append("                    begin\r\n");
				trigger.append("                     	--更新变化后的值\r\n");
				trigger.append("                     	update t_");
				trigger.append(tab);
				trigger.append("_view_log set newValue=@" + field + ",flag=1 where UNIQUE_ID=@unique_id and fielditemid='" + field.toUpperCase() + "' and sysid='" + sysId.toUpperCase()+ "';\r\n");
				                     	          
//				trigger.append("                     	--更新同步状态\r\n");
//				trigger.append("                     	update t_");
//				trigger.append(tab);
//				trigger.append("_view_log set flag = 1 where UNIQUE_ID=@unique_id and fielditemid='" + field.toUpperCase() + "' and sysid='" + sysId.toUpperCase()+ "' and flag=0;\r\n");
				trigger.append("                    END;\r\n");
		
			}
			trigger.append("                end;\r\n");
		                             
		}                             
		                             
		                             
		trigger.append("				FETCH NEXT FROM C_USRA01545 INTO @unique_id,");
		
		for (int i = 0; i < fieldList.size(); i++) {
			trigger.append("@");
			trigger.append(fieldList.get(i));
			
			if (i != fieldList.size() - 1) {
				trigger.append(",");
			}
		}
		
		trigger.append(";\r\n");
		trigger.append(" 			END\r\n");
		trigger.append("            CLOSE C_USRA01545\r\n");
		trigger.append("            DEALLOCATE C_USRA01545\r\n");
		trigger.append("		end;\r\n");

		trigger.append("        --更新\r\n");
		trigger.append("        else \r\n");
		trigger.append("        begin\r\n");
		trigger.append("        	DECLARE C_USRA01545 CURSOR FOR SELECT unique_id,");
		
		for (int i = 0; i < fieldList.size(); i++) {
			
			String field = (String) fieldList.get(i);
			
			FieldItem item = DataDictionary.getFieldItem(field);
			if (item != null && item.isDate()) {
				trigger.append("CONVERT(varchar(100), " + field +", 25) ");
				trigger.append(field);
			} else {
				trigger.append(fieldList.get(i));
			}
						
			if (i != fieldList.size() - 1) {
				trigger.append(",");
			}
		}
				
		trigger.append(" FROM INSERTED;\r\n");
		                       
		trigger.append("            OPEN C_USRA01545;\r\n");
		trigger.append("			FETCH NEXT FROM C_USRA01545 INTO @unique_id,");
		
		for (int i = 0; i < fieldList.size(); i++) {
			trigger.append("@");
			trigger.append(fieldList.get(i));
			
			if (i != fieldList.size() - 1) {
				trigger.append(",");
			}
		}
				
		trigger.append(";\r\n");
		trigger.append("            WHILE (@@FETCH_STATUS = 0)\r\n");
		trigger.append("            BEGIN\r\n");
		for (int i = 0; i < fieldList.size(); i++) {
			
			
			String field = (String) fieldList.get(i);
			
			trigger.append("         if update(" + field + ")");
			trigger.append("         begin\r\n");
						
			FieldItem item = DataDictionary.getFieldItem(field);
			if (item != null && item.isDate()) {
			
				trigger.append("            	select @oldvalue =CONVERT(varchar(100), " + field +", 25)  from deleted where unique_id=@unique_id;\r\n");
			} else {
				trigger.append("            	select @oldvalue =" + field + "  from deleted where unique_id=@unique_id;\r\n");
			}
						                     			
			trigger.append("            	if @" + field + "<>@oldvalue \r\n");
			trigger.append("           		begin\r\n");
			
			
			for (int j = 0; j < sysIdList.size(); j++) {
				String sysId = (String) sysIdList.get(j);
			trigger.append("                	--判断记录是否存在\r\n");
			trigger.append("                    select @isExists =count(*) from t_");
			trigger.append(tab);
			trigger.append("_view_log where UNIQUE_ID=@unique_id and fielditemid='" + field.toUpperCase() + "' and sysid='" + sysId.toUpperCase() + "';\r\n");
			                     		       
			trigger.append("                    --记录不存在需要新增\r\n");
			trigger.append("                    IF @isExists = 0\r\n");
			trigger.append("                    	--更新变化后的值\r\n");
			trigger.append("                     	insert into t_");
			trigger.append(tab);
			trigger.append("_view_log(UNIQUE_ID,fielditemid,oldValue,newValue,Sysid,flag) values(@unique_id,'" + field.toUpperCase() + "',@oldvalue,@" + field + ",'" + sysId.toUpperCase() + "',1); \r\n");
			                     		  
			trigger.append("                    ELSE--记录存在需要更新\r\n");
			trigger.append("                    begin\r\n");
			trigger.append("                     	--更新变化后的值\r\n");
			trigger.append("                     	update t_");
			trigger.append(tab);
			trigger.append("_view_log set newValue=@" + field + ",flag=1 where UNIQUE_ID=@unique_id and fielditemid='" + field.toUpperCase() + "' and sysid='" + sysId.toUpperCase() + "';\r\n");
			                     		          
//			trigger.append("                     	--更新同步状态\r\n");
//			trigger.append("                     	update t_");
//			trigger.append(tab);
//			trigger.append("_view_log set flag = 1 where UNIQUE_ID=@unique_id and fielditemid='" + field.toUpperCase() + "' and sysid='" + sysId.toUpperCase() + "' and flag=0;\r\n");
			trigger.append("                    END;\r\n");
			
		}
			
			
			trigger.append(" 				end;     \r\n"); 
			
			trigger.append("         end;\r\n");
		}
		                            
		trigger.append("                FETCH NEXT FROM C_USRA01545 INTO @unique_id,");
		
		for (int i = 0; i < fieldList.size(); i++) {
			trigger.append("@");
			trigger.append(fieldList.get(i));
			
			if (i != fieldList.size() - 1) {
				trigger.append(",");
			}
		}
		
		trigger.append(";\r\n");
		trigger.append("			END\r\n");
		trigger.append("            CLOSE C_USRA01545\r\n");
		trigger.append("            DEALLOCATE C_USRA01545\r\n");

		trigger.append("	end;\r\n");
		
		trigger.append("	end;\r\n");
		
		return trigger.toString();
	}
	
	public void CreateFunTransCode(){
		CreateFunTransCode(false, ":");
	}
	
	public void CreateFunTransCode(boolean fieldAndCode, String fieldAndCodeSeq){
		StringBuffer sql = new StringBuffer();
		Statement st = null;
		try {
			st = conn.createStatement();
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.conn);
		    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		    String sep = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
		    if(uplevel==null||uplevel.length()==0)
		    	uplevel="0";
			sql.append("--================================================\n");
			sql.append("-- 制作人：郑文龙\n");
			sql.append("-- 时间：  2011-08-03\n");
			sql.append("-- 作用：  翻译代码\n");
			sql.append("-- tableName   表名 对应FIELDITEM表中 FIELDSETID 字段 \n");
			sql.append("-- fieldName   字段名 对应FIELDITEM表中 ITEMID字段\n");
			sql.append("-- fieldValue  字段值 对应CODEITEM表中 CODEITEMID字段\n");
			sql.append("--================================================\n");
		    sql.append("CREATE FUNCTION FUN_GET_CODEDESC\n");
			sql.append("(\n");
			sql.append("	@curtime datetime, @tableName VARCHAR(100),@fieldName VARCHAR(100),@fieldValue VARCHAR(100)\n");
			sql.append(")\n");
			sql.append("RETURNS VARCHAR(1000)\n");
			sql.append("AS\n");
			sql.append("BEGIN\n");
			sql.append("  -- 定义变量\n");
			sql.append("  DECLARE\n");
			sql.append("  @ResultVar  VARCHAR(1000),\n");
			sql.append("  @codeSetId  VARCHAR(10),\n");
			sql.append("  @FIELDSETID VARCHAR(10),\n");
			sql.append("  @ITEMID     VARCHAR(10),\n");
			sql.append("  @ORGID      VARCHAR(100),\n");
			sql.append("  @ORGCODE    VARCHAR(10),\n");
			sql.append("  @PARENTID   VARCHAR(100),\n");
			sql.append("  @ORGNAME    VARCHAR(100),\n");
			sql.append("  @i          INT,\n");
			sql.append("  @cout       INT\n");
			sql.append("  --正文\n");
			sql.append("  SET @FIELDSETID = UPPER(@tableName);\n");
			sql.append("  SET @ITEMID = UPPER(@fieldName);\n");
			sql.append("  SET @i = 0;\n");
			sql.append("  IF (@FIELDSETID='B01' AND @ITEMID='B0110') OR @ITEMID = 'E0122'\n");
			sql.append("  BEGIN\n");
			sql.append("    SET @ORGID = @fieldValue;\n");
			sql.append("    WHILE @i <= " + uplevel + "\n");
			sql.append("    BEGIN\n");
			
			sql.append("      SELECT @cout=count(1) FROM ORGANIZATION WHERE CODEITEMID = @ORGID and @curtime between start_date and end_date;\n");
			sql.append("	if @cout=1 \n");
			sql.append("    BEGIN\n");
			sql.append("      SELECT @ORGNAME=LTrim(RTrim(CODEITEMDESC)),@PARENTID=PARENTID,@ORGCODE=CODESETID FROM ORGANIZATION WHERE CODEITEMID = @ORGID and @curtime between start_date and end_date;\n");
			sql.append("      IF @PARENTID <> @ORGID AND ISNULL(@PARENTID,'')<>'' AND (@ORGCODE=@codeSetId OR ISNULL(@codeSetId,'')='')\n");
			sql.append("      BEGIN\n");
			sql.append("        SET @ORGID = @PARENTID;\n");
			sql.append("        SET @codeSetId = @ORGCODE;\n");
			sql.append(" 		IF(@i = 0 ) BEGIN \n");
			sql.append("        	SET @ResultVar = @ORGNAME + ISNULL(@ResultVar,'');\n");
			sql.append("      	END;\n");
			sql.append("      	ELSE BEGIN\n");
			sql.append("        	SET @ResultVar =  @ORGNAME + '" + sep + "' + ISNULL(@ResultVar,'');\n");
			sql.append("      	END;\n");
			sql.append("      END;\n");
			sql.append("      ELSE begin\n");
			sql.append("if ISNULL(@codeSetId,'')='' ");
//			sql.append("        RETURN @ResultVar\n");
			sql.append("        return @ORGNAME;");
			sql.append(" else if @ORGCODE<>@codeSetId RETURN @ResultVar ");
			sql.append(" else if @ORGCODE=@codeSetId RETURN @ORGNAME + '" + sep + "' + ISNULL(@ResultVar,'')\n");
			sql.append("    END;\n");
			sql.append("    END;\n");
			
			sql.append("      SET @i = @i + 1;\n");
			sql.append("    END;\n");
			sql.append("  END;\n");
			sql.append("  ELSE\n");
			sql.append("  BEGIN\n");
			sql.append("    IF @ITEMID = 'B0110'\n");
			sql.append("      SET @codeSetId = 'UN';\n");
			sql.append("    ELSE IF @ITEMID = 'E01A1'\n");
			sql.append("      SET @codeSetId = '@K';\n");
			sql.append("    ELSE\n");
			sql.append("      SELECT @codeSetId = CODESETID FROM FIELDITEM WHERE FIELDSETID = @FIELDSETID AND ITEMID = @ITEMID;\n");
			sql.append("    IF @CODESETID = '@K' OR @CODESETID = 'UN' OR @CODESETID = 'UM' begin\n");
			
			sql.append("		SELECT @cout=count(1) FROM ORGANIZATION WHERE CODESETID = @codeSetId AND CODEITEMID = @fieldValue and @curtime between start_date and end_date;\n");
			sql.append("		if @cout=1 begin \n");
			sql.append("      		SELECT @ResultVar = LTrim(RTrim(CODEITEMDESC)) FROM ORGANIZATION WHERE CODESETID = @codeSetId AND CODEITEMID = @fieldValue and @curtime between start_date and end_date;\n");
			sql.append("		end;\n");
			sql.append("	end;\n");
			sql.append("    ELSE IF @CODESETID <> '0' begin\n");
			
			sql.append("      SELECT @cout=count(1) FROM CODEITEM WHERE CODESETID = @codeSetId AND CODEITEMID = @fieldValue and @curtime between start_date and end_date;\n");
			
			sql.append("		if @cout=1 begin \n");
			if (fieldAndCode) {
				sql.append("      		SELECT @ResultVar = LTrim(RTrim(CODEITEMID)) + '" + fieldAndCodeSeq + "' + CODEITEMDESC  FROM CODEITEM WHERE CODESETID = @codeSetId AND CODEITEMID = @fieldValue and getdate() between start_date and end_date;\n");
			} else {
				sql.append("      		SELECT @ResultVar = CODEITEMDESC FROM CODEITEM WHERE CODESETID = @codeSetId AND CODEITEMID = @fieldValue and @curtime between start_date and end_date;\n");
			}
			sql.append("		end;\n");
			sql.append("	end;");
			sql.append("  END;\n");
			sql.append("  RETURN @ResultVar\n");
			sql.append("END");
			st.execute(sql.toString());
		
			sql.setLength(0);
			sql.append("--================================================\n");
			sql.append("-- 制作人：郑文龙\n");
			sql.append("-- 时间：  2011-08-03\n");
			sql.append("-- 作用：  修改人员（t_hr_view）、机构（t_org_view）和岗位（t_post_view）表同步标识\n");
			sql.append("-- syncFlag  修改的同步标识的值 1 新增 2 修改 3 删除 0 已同步\n");
			sql.append("-- keyName   主键名称 只有在 mark = A_0，B_0，K_0 时候起作用 否则 主键按照 unique_id 字段\n");
			sql.append("-- keyValue  主键值\n");
			sql.append("-- dbValue   人员库值 又有在同步人员时候有用  即 mark = A，A_，A_0\n");
			sql.append("-- mark      标识 = 人员（A 人员主集标识 A_ 人员子集标识 A_0 自定义）机构（B 人员主集标识 B_ 人员子集标识 B_0 自定义）\n");
			sql.append("--                           岗位（K 人员主集标识 K_ 人员子集标识 K_0 自定义）\n");
			sql.append("--================================================\n");
			sql.append("CREATE PROCEDURE PR_UP_SYNC_FLAG\n");
			sql.append("@syncFlag char(1),@keyName VARCHAR(100), @keyValue VARCHAR(100),@dbValue VARCHAR(100), @mark CHAR(10)\n");
			sql.append("AS\n");
			sql.append("BEGIN\n");
			sql.append("  DECLARE\n");
			sql.append("  @strSQL VARCHAR(1000),\n");
			sql.append("  @outappid VARCHAR(30)\n");
			sql.append("\n");
			sql.append("  IF @keyValue IS NOT NULL\n");
			sql.append("  BEGIN\n");
			sql.append("    DECLARE outapp_cur CURSOR FOR SELECT sys_id FROM t_sys_outsync WHERE state=1;\n");
			sql.append("    OPEN outapp_cur;\n");
			sql.append("    FETCH outapp_cur INTO @outappid;\n");
			sql.append("	IF @mark = 'A'\n");
			sql.append("    BEGIN\n");			
//			sql.append("      SET @strSQL = 'UPDATE t_hr_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND UPPER(nbase_0) =''' + @dbValue + '''';\n");
//			sql.append("      EXEC(@strSQL);\n");
			
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("         SET @strSQL = 'UPDATE t_hr_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND UPPER(nbase_0) =''' + @dbValue + '''';\n");
//			sql.append("         EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			sql.append("      ELSE \n");
//			sql.append("      BEGIN\n");
//			sql.append("         SET @strSQL = 'UPDATE t_hr_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ' WHERE unique_id =''' + @keyValue + ''' AND UPPER(nbase_0) =''' + @dbValue + ''' and flag<>1 and flag<>2';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("         SET @strSQL = 'UPDATE t_hr_view SET sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND UPPER(nbase_0) =''' + @dbValue + '''';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			sql.append("      WHILE (@@FETCH_STATUS = 0)\n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +'''';\n");
//			sql.append("        EXEC(@strSQL);\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +'''';\n");
			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=CASE WHEN ' + @outappid +'=1 AND 3=' + @syncFlag +' THEN 0 ELSE ' + @syncFlag + ' END,sdate = GETDATE() WHERE UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			sql.append("      ELSE \n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=' + @syncFlag + ' WHERE UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");
			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=CASE WHEN 3<>'+ @syncFlag +' AND sys_flag=3 AND '+ @outappid +'=0 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'<>3 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'=3 THEN 0 ELSE ' + @syncFlag + ' END WHERE UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET sdate = GETDATE() WHERE UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +'''';\n");
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			
			sql.append("        FETCH outapp_cur INTO @outappid;\n");
			sql.append("      END;\n");
			sql.append("    END;\n");
			sql.append("    ELSE IF @mark = 'A_'\n");
			sql.append("    BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_hr_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND UPPER(nbase_0) =''' + @dbValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("      EXEC(@strSQL);\n");
			
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_hr_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND UPPER(nbase_0) =''' + @dbValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("      EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			sql.append("      ELSE \n");
//			sql.append("      BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_hr_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ' WHERE unique_id =''' + @keyValue + ''' AND UPPER(nbase_0) =''' + @dbValue + ''' AND ISNULL(flag,0)=0 and flag<>1 and flag<>2';\n");
//			sql.append("      EXEC(@strSQL);\n");
//			sql.append("      SET @strSQL = 'UPDATE t_hr_view SET sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND UPPER(nbase_0) =''' + @dbValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("      EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			
			sql.append("      WHILE (@@FETCH_STATUS = 0)\n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +'''';\n");
//			sql.append("        EXEC(@strSQL);\n");
			
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +'''';\n");
			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=CASE WHEN ' + @outappid +'=1 AND 3=' + @syncFlag +' THEN 0 ELSE ' + @syncFlag + ' END,sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			sql.append("      ELSE \n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=' + @syncFlag + 'WHERE ISNULL('+@outappid+',0)=0 AND UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");
			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=CASE WHEN 3<>'+ @syncFlag +' AND sys_flag=3 AND '+ @outappid +'=0 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'<>3 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'=3 THEN 0 ELSE ' + @syncFlag + ' END WHERE ISNULL('+@outappid+',0)=0 AND UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET sdate = GETDATE() WHERE UPPER(nbase_0)= ''' + @dbValue + ''' AND unique_id =''' + @keyValue +'''';\n");
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			
			sql.append("        FETCH outapp_cur INTO @outappid;\n");
			sql.append("      END;\n");
			sql.append("    END;\n");
			sql.append("    ELSE IF @mark = 'A_0'\n");
			sql.append("    BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_hr_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ',sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0' ;\n");
//			sql.append("      EXEC(@strSQL);\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_hr_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ',sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0' ;\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			sql.append("      ELSE \n");
//			sql.append("      BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_hr_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ' WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0 and flag<>1 and flag<>2' ;\n");
//			sql.append("      EXEC(@strSQL);\n");
//			sql.append("      SET @strSQL = 'UPDATE t_hr_view SET sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0' ;\n");
//			sql.append("      EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			
			sql.append("      WHILE (@@FETCH_STATUS = 0)\n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + '=''' + @keyValue +'''';\n");
//			sql.append("        EXEC(@strSQL);\n");
			
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + '=''' + @keyValue +'''';\n");
			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=CASE WHEN ' + @outappid +'=1 AND 3=' + @syncFlag +' THEN 0 ELSE ' + @syncFlag + ' END,sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + '=''' + @keyValue +'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			sql.append("      ELSE \n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=' + @syncFlag + ' WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + '=''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");
			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET ' + @outappid + '=CASE WHEN 3<>'+ @syncFlag +' AND sys_flag=3 AND '+ @outappid +'=0 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'<>3 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'=3 THEN 0 ELSE ' + @syncFlag + ' END WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + '=''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("      EXEC(@strSQL);\n");
			sql.append("        SET @strSQL = 'UPDATE t_hr_view SET sdate = GETDATE() WHERE   ' + @keyName + '=''' + @keyValue +'''';\n");
			sql.append("      EXEC(@strSQL);\n");
			
			sql.append("      END;\n");
			
			
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			sql.append("        FETCH outapp_cur INTO @outappid;\n");
			sql.append("      END;\n");
			sql.append("    END;\n");
			sql.append("    ELSE IF @mark = 'B'\n");
			sql.append("    BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + '''';\n");
//			sql.append("      EXEC(@strSQL);\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + '''';\n");
//			sql.append("      EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			sql.append("      ELSE \n");
//			sql.append("      BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ' WHERE unique_id =''' + @keyValue + ''' and flag<>1 and flag<>2';\n");
//			sql.append("      EXEC(@strSQL);\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET sdate = GETDATE() WHERE unique_id =''' + @keyValue + '''';\n");
//			sql.append("      EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			sql.append("      WHILE (@@FETCH_STATUS = 0)\n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue +'''';\n");
//			sql.append("        EXEC(@strSQL);\n");
			
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue +'''';\n");
			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=CASE WHEN ' + @outappid +'=1 AND 3=' + @syncFlag +' THEN 0 ELSE ' + @syncFlag + ' END,sdate = GETDATE() WHERE unique_id =''' + @keyValue +'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			sql.append("      ELSE \n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=' + @syncFlag + ' WHERE unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");
			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=CASE WHEN 3<>'+ @syncFlag +' AND sys_flag=3 AND '+ @outappid +'=0 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'<>3 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'=3 THEN 0 ELSE ' + @syncFlag + ' END WHERE unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("      EXEC(@strSQL);\n");
			sql.append("      SET @strSQL = 'UPDATE t_org_view SET sdate = GETDATE() WHERE unique_id =''' + @keyValue +'''';\n");
			sql.append("      EXEC(@strSQL);\n");
			sql.append("      END;\n");
			
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			sql.append("        FETCH outapp_cur INTO @outappid;\n");
			sql.append("      END;\n");
			sql.append("    END;\n");
			sql.append("    ELSE IF @mark = 'B_'\n");
			sql.append("    BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("      EXEC(@strSQL);\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			sql.append("      ELSE \n");
//			sql.append("      BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ' WHERE unique_id =''' + @keyValue + ''' AND ISNULL(flag,0)=0 and flag<>1 and flag<>2';\n");
//			sql.append("      EXEC(@strSQL);\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("      EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE (@@FETCH_STATUS = 0)\n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND unique_id =''' + @keyValue +'''';\n");
//			sql.append("        EXEC(@strSQL);\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND unique_id =''' + @keyValue +'''';\n");
			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=CASE WHEN ' + @outappid +'=1 AND 3=' + @syncFlag +' THEN 0 ELSE ' + @syncFlag + ' END,sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND unique_id =''' + @keyValue +'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			sql.append("      ELSE \n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=' + @syncFlag + ' WHERE ISNULL('+@outappid+',0)=0 AND unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");
			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=CASE WHEN 3<>'+ @syncFlag +' AND sys_flag=3 AND '+ @outappid +'=0 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'<>3 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'=3 THEN 0 ELSE ' + @syncFlag + ' END WHERE ISNULL('+@outappid+',0)=0 AND unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("        SET @strSQL = 'UPDATE t_org_view SET sdate = GETDATE() WHERE unique_id =''' + @keyValue +'''';\n");
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			sql.append("        FETCH outapp_cur INTO @outappid;\n");
			sql.append("      END;\n");
			sql.append("    END;\n");
			sql.append("    ELSE IF @mark = 'B_0'\n");
			sql.append("    BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ',sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("      EXEC(@strSQL);\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ',sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			sql.append("      ELSE \n");
//			sql.append("      BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ' WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0 and flag<>1 and flag<>2';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      SET @strSQL = 'UPDATE t_org_view SET sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			sql.append("      WHILE (@@FETCH_STATUS = 0)\n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + ' =''' + @keyValue +'''';\n");
//			sql.append("        EXEC(@strSQL);\n");
			
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + ' =''' + @keyValue +'''';\n");
			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=CASE WHEN ' + @outappid +'=1 AND 3=' + @syncFlag +' THEN 0 ELSE ' + @syncFlag + ' END,sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + ' =''' + @keyValue +'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			
			
			sql.append("	  else if(@syncFlag=2) begin \n");
//			sql.append("	  	SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=' + @syncFlag + ' WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + ' =''' + @keyValue +''' and ' + @outappid + ' is not null and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");
			sql.append("	  	SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=CASE WHEN 3<>'+ @syncFlag +' AND sys_flag=3 AND '+ @outappid +'=0 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'<>3 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'=3 THEN 0 ELSE ' + @syncFlag + ' END WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + ' =''' + @keyValue +''' and ' + @outappid + ' is not null and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("	  	EXEC(@strSQL);\n");
			sql.append("	  	SET @strSQL = 'UPDATE t_org_view SET sdate = GETDATE() WHERE ' + @keyName + ' =''' + @keyValue +'''';\n");
			sql.append("	  	EXEC(@strSQL);\n");
			sql.append("	  end; \n");
			
			sql.append("      ELSE \n");
			
			sql.append("      BEGIN\n");
			sql.append("        SET @strSQL = 'UPDATE t_org_view SET ' + @outappid + '=' + @syncFlag + ' WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + ' =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");
			sql.append("        EXEC(@strSQL);\n");
			sql.append("        SET @strSQL = 'UPDATE t_org_view SET sdate = GETDATE() WHERE   ' + @keyName + ' =''' + @keyValue +'''';\n");
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			sql.append("        FETCH outapp_cur INTO @outappid;\n");
			sql.append("      END;\n");
			sql.append("    END;\n");
			sql.append("    ELSE IF @mark = 'K'\n");
			sql.append("    BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + '''';\n");
//			sql.append("      EXEC(@strSQL);\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + '''';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			sql.append("      ELSE \n");
//			sql.append("      BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ' WHERE unique_id =''' + @keyValue + ''' and flag<>1 and flag<>2';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET sdate = GETDATE() WHERE unique_id =''' + @keyValue + '''';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE (@@FETCH_STATUS = 0)\n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue +'''';\n");
//			sql.append("        EXEC(@strSQL);\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue +'''';\n");
			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=CASE WHEN ' + @outappid +'=1 AND 3=' + @syncFlag +' THEN 0 ELSE ' + @syncFlag + ' END,sdate = GETDATE() WHERE unique_id =''' + @keyValue +'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			sql.append("      ELSE \n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=' + @syncFlag + ' WHERE unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2 ';\n");
			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=CASE WHEN 3<>'+ @syncFlag +' AND sys_flag=3 AND '+ @outappid +'=0 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'<>3 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'=3 THEN 0 ELSE ' + @syncFlag + ' END WHERE unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2 ';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("        SET @strSQL = 'UPDATE t_post_view SET sdate = GETDATE() WHERE unique_id =''' + @keyValue +'''';\n");
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO @outappid;\n");
			sql.append("      END;\n");
			sql.append("    END;\n");
			sql.append("    ELSE IF @mark = 'K_'\n");
			sql.append("    BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("      EXEC(@strSQL);\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			sql.append("      ELSE \n");
//			sql.append("      BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ' WHERE unique_id =''' + @keyValue + ''' AND ISNULL(flag,0)=0 and flag<>1 and flag<>2';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE (@@FETCH_STATUS = 0)\n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND unique_id =''' + @keyValue +'''';\n");
//			sql.append("        EXEC(@strSQL);\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND unique_id =''' + @keyValue +'''';\n");
			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=CASE WHEN ' + @outappid +'=1 AND 3=' + @syncFlag +' THEN 0 ELSE ' + @syncFlag + ' END,sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND unique_id =''' + @keyValue +'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			sql.append("      ELSE \n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=' + @syncFlag + ' WHERE ISNULL('+@outappid+',0)=0 AND unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");
			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=CASE WHEN 3<>'+ @syncFlag +' AND sys_flag=3 AND '+ @outappid +'=0 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'<>3 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'=3 THEN 0 ELSE ' + @syncFlag + ' END WHERE ISNULL('+@outappid+',0)=0 AND unique_id =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("        SET @strSQL = 'UPDATE t_post_view SET sdate = GETDATE() WHERE   unique_id =''' + @keyValue +'''';\n");
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO @outappid;\n");
			sql.append("      END;\n");
			sql.append("    END;\n");
			sql.append("    ELSE IF @mark = 'K_0'\n");
			sql.append("    BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ',sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("      EXEC(@strSQL);\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ',sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			sql.append("      ELSE \n");
//			sql.append("      BEGIN\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ' WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0 and flag<>1 and flag<>2';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      SET @strSQL = 'UPDATE t_post_view SET sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
//			sql.append("        EXEC(@strSQL);\n");
//			sql.append("      END;\n");
//			
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE (@@FETCH_STATUS = 0)\n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + ' =''' + @keyValue +'''';\n");
//			sql.append("        EXEC(@strSQL);\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("      IF(@syncFlag<>1 and @syncFlag<>2) BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=' + @syncFlag + ',sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + ' =''' + @keyValue +'''';\n");
			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=CASE WHEN ' + @outappid +'=1 AND 3=' + @syncFlag +' THEN 0 ELSE ' + @syncFlag + ' END,sdate = GETDATE() WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + ' =''' + @keyValue +'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			sql.append("      ELSE \n");
			sql.append("      BEGIN\n");
//			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=' + @syncFlag + ' WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + ' =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");
			sql.append("        SET @strSQL = 'UPDATE t_post_view SET ' + @outappid + '=CASE WHEN 3<>'+ @syncFlag +' AND sys_flag=3 AND '+ @outappid +'=0 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'<>3 THEN 1 WHEN sys_flag=3 AND ' + @outappid + '=0 AND '+@syncFlag+'=3 THEN 0 ELSE ' + @syncFlag + ' END WHERE ISNULL('+@outappid+',0)=0 AND ' + @keyName + ' =''' + @keyValue +''' and isnull(' + @outappid + ',-1)<>1 and isnull(' + @outappid + ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("        EXEC(@strSQL);\n");
			sql.append("        SET @strSQL = 'UPDATE t_post_view SET sdate = GETDATE() WHERE ' + @keyName + ' =''' + @keyValue +'''';\n");
			sql.append("        EXEC(@strSQL);\n");
			sql.append("      END;\n");
			
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO @outappid;\n");
			sql.append("      END;\n");
			sql.append("    END;\n");
			sql.append("    CLOSE outapp_cur;\n");
			sql.append("    DEALLOCATE outapp_cur;\n");
			
			
			sql.append("    IF @mark = 'A'\n");
			sql.append("        BEGIN\n");
			sql.append("      		SET @strSQL = 'UPDATE t_hr_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND UPPER(nbase_0) =''' + @dbValue + '''';\n");
			sql.append("      		EXEC(@strSQL);\n");										  
			sql.append("        END;\n");
			sql.append("    ELSE IF @mark = 'A_'\n");
			sql.append("    	BEGIN\n");
			sql.append("      		SET @strSQL = 'UPDATE t_hr_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND UPPER(nbase_0) =''' + @dbValue + ''' AND ISNULL(flag,0)=0';\n");
			sql.append("      		EXEC(@strSQL);\n");     
							  
			sql.append("    	END;\n");
			sql.append("    ELSE IF @mark = 'A_0'\n");
			sql.append("        BEGIN\n");
			sql.append("      		SET @strSQL = 'UPDATE t_hr_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ',sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0' ;\n");
			sql.append("      		EXEC(@strSQL);\n");
			sql.append("        END;\n");
			sql.append("    ELSE IF @mark = 'B'\n");
			sql.append("        BEGIN\n");
			sql.append("      		SET @strSQL = 'UPDATE t_org_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + '''';\n");
			sql.append("      		EXEC(@strSQL);\n");
			sql.append("    	END;\n");
			sql.append("    ELSE IF @mark = 'B_'\n");
			sql.append("    	BEGIN\n");
			sql.append("      		SET @strSQL = 'UPDATE t_org_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
			sql.append("      		EXEC(@strSQL);\n");

			sql.append("    	END;\n");
			sql.append("    ELSE IF @mark = 'B_0'\n");
			sql.append("    	BEGIN\n");
			sql.append("      		SET @strSQL = 'UPDATE t_org_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ',sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
			sql.append("      		EXEC(@strSQL);\n");
			sql.append("    	END;\n");
			sql.append("    ELSE IF @mark = 'K'\n");
			sql.append("    	BEGIN\n");
			sql.append("      		SET @strSQL = 'UPDATE t_post_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + '''';\n");
			sql.append("      		EXEC(@strSQL);\n");
			sql.append("    	END;\n");
			sql.append("    ELSE IF @mark = 'K_'\n");
			sql.append("    	BEGIN\n");
			sql.append("      		SET @strSQL = 'UPDATE t_post_view SET flag =' + @syncFlag + ',sys_flag =' + @syncFlag + ',sdate = GETDATE() WHERE unique_id =''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
			sql.append("      		EXEC(@strSQL);\n");
	      
			sql.append("    	END;\n");
			sql.append("    ELSE IF @mark = 'K_0'\n");
			sql.append("    	BEGIN\n");
			sql.append("      		SET @strSQL = 'UPDATE t_post_view SET flag = ' + @syncFlag + ',sys_flag = ' + @syncFlag + ',sdate = GETDATE() WHERE ' + @keyName + '=''' + @keyValue + ''' AND ISNULL(flag,0)=0';\n");
			sql.append("      		EXEC(@strSQL);\n");
			sql.append("    	END;\n");
			
			
			
			
			sql.append("  END;\n");
			sql.append("END;\n");
			st.execute(sql.toString());
		
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(st);
		}
	}
}
