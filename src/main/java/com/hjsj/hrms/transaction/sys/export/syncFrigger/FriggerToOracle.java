package com.hjsj.hrms.transaction.sys.export.syncFrigger;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
/**
 * 
 * <p>Title:FriggerToOracle.java</p>
 * <p>Description>:FriggerToOracle.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 14, 2011 9:56:30 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: 郑文龙
 */
public class FriggerToOracle {
	/**
	 * 针对A01表 创建的触发器
	 * 
	 * @param dbname
	 */

	private Connection conn = null;
	
	private String photo = "";
	
	public FriggerToOracle(Connection conn) {
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
		// 同步照片
		photo = hsb.getAttributeValue(HrSyncBo.photo);;
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
					frigger = toUserA01(dbnames[i].toUpperCase(), (List) fields
							.get(table), (List) tranfields.get(table));
				} else if("1".equals(isJz) && table.equalsIgnoreCase(setid)){
					frigger = toUserJz(dbnames[i],table,(List) fields.get(table),
							(List) tranfields.get(table));
				} else {
					frigger = toUserSub(dbnames[i].toUpperCase(), table,
							(List) fields.get(table), (List) tranfields
									.get(table));
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
		if (fields != null && !fields.isEmpty()) {
			Set tables = fields.keySet();
			Iterator it = tables.iterator();
			while (it.hasNext()) {
				String table = (String) it.next();
				if (table.trim().length() < 1)
					continue;
				if ("B01".equalsIgnoreCase(table)) {
					frigger = toOrgB01((List) fields.get("B01"),
							(List) tranfields.get("B01"));
				} else {
					frigger = toOrgSub(table, (List) fields.get(table),
							(List) tranfields.get(table));
				}
				Statement st = null;
				try {
					st = conn.createStatement();
					if (frigger != null && frigger.length() > 0)
						st.execute(frigger);
				} catch (SQLException e) {
					e.printStackTrace();
					throw new GeneralException("TR_ORG_CHANGE_" + table
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
		createOrganization();
	}

	public void toPost(Map fields, Map tranfields) throws GeneralException {
		
		String frigger = "";
		if (fields != null && !fields.isEmpty()) {
			Set tables = fields.keySet();
			Iterator it = tables.iterator();
			while (it.hasNext()) {
				String table = (String) it.next();
				if (table.trim().length() < 1)
					continue;
				if ("K01".equalsIgnoreCase(table)) {
					frigger = toPostK01((List) fields.get("K01"),
							(List) tranfields.get("K01"));
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
		createOrganization();
	}
	
	public void toPhoto(String []dbNames) throws GeneralException{

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
	
	public void toFieldChange(String type, List fieldList, List sysIdList) throws GeneralException{

		Statement st = null;
		try {
			st = conn.createStatement();
			String trigger = createFieldChangeInsertFunc();
			if (trigger != null && trigger.length() > 0) {
				st.execute(trigger);
			}
			
			trigger = createFieldChangeUpdateFunc();
			if (trigger != null && trigger.length() > 0) {
				st.execute(trigger);
			}
			
			trigger = createFieldChangeTrigger(type, fieldList, sysIdList);
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

	private String createFieldChangeInsertFunc() {
		StringBuffer trigger = new StringBuffer();
		trigger.append("CREATE OR REPLACE FUNCTION FIELDCHANGE_INSERT_OPT(new_FieldValue varchar2, tableName varchar2, UNIQUE_IDValue varchar2, FieldName varchar2,SysId varchar2) RETURN VARCHAR2 AS\r\n");
		trigger.append("isExists Number;\r\n");
		trigger.append("dySQL varchar2(500);\r\n");
		trigger.append("BEGIN\r\n");
		trigger.append("   IF new_FieldValue is not null  THEN--字段值发生变化\r\n");
		trigger.append("        --判断记录是否存在 \r\n");
		trigger.append("      dySQL := 'select count(*) from ' || tableName || ' where UNIQUE_ID=''' || UNIQUE_IDValue ||''' and upper(fielditemid)='''|| FieldName ||''' and upper(sysid)='''|| SysId ||'''';\r\n");
		trigger.append("      execute immediate dySQL into isExists; \r\n");
		trigger.append("       --记录不存在需要新增 \r\n");
		trigger.append("        IF isExists = 0 THEN \r\n");
		trigger.append("          --更新变化后的值 \r\n");
		trigger.append("         dySQL := 'insert into ' ||  tableName ||'(UNIQUE_ID,fielditemid,oldValue,newValue,Sysid,flag) values(''' || UNIQUE_IDValue||''',upper('''|| FieldName ||'''),'''',''' || new_FieldValue || ''',upper('''||SysId || '''),1)';\r\n"); 
		trigger.append("          execute immediate dySQL;\r\n");
		trigger.append("        ELSE--记录存在需要更新\r\n");
		trigger.append("          --更新变化后的值\r\n");
		trigger.append("          dySQL := 'update '|| tableName || ' set newValue='''|| new_FieldValue ||''' where UNIQUE_ID='''|| UNIQUE_IDValue || ''' and upper(fielditemid)='''|| FieldName ||''' and upper(sysid)='''|| SysId ||'''';\r\n");
		trigger.append("          execute immediate dySQL;\r\n");
		trigger.append("          --更新同步状态\r\n");
		trigger.append("          dySQL := 'update '|| tableName ||' set flag = 1 where UNIQUE_ID='''|| UNIQUE_IDValue ||''' and upper(fielditemid)='''|| FieldName ||''' and upper(sysid)='''|| SysId ||''' and flag=0';\r\n");
		trigger.append("          execute immediate dySQL;\r\n");
		trigger.append("        END IF;\r\n");

		trigger.append("     END IF;\r\n");
		trigger.append("  RETURN NULL;\r\n");
		trigger.append("END FIELDCHANGE_INSERT_OPT;\r\n");
		
		return trigger.toString();
	}
	
	private String createFieldChangeUpdateFunc() {
		StringBuffer trigger = new StringBuffer();
		trigger.append("CREATE OR REPLACE FUNCTION FIELDCHANGE_UPDATE_OPT(new_FieldValue varchar2,old_fieldValue varchar2, tableName varchar2, UNIQUE_IDValue varchar2, FieldName varchar2,SysId varchar2) RETURN VARCHAR2 AS\r\n");
		trigger.append("isExists Number;\r\n");
		trigger.append("dySQL varchar2(500);\r\n");
		trigger.append("BEGIN\r\n");


		trigger.append("   IF (new_FieldValue is null and old_fieldValue is not null) or new_FieldValue<>old_fieldValue or (new_FieldValue is not null and old_fieldValue is null) THEN--字段值发生变化\r\n");
		trigger.append("        --判断记录是否存在 \r\n");
		trigger.append("       dySQL := 'select count(*) from ' || tableName || ' where UNIQUE_ID=''' || UNIQUE_IDValue ||''' and upper(fielditemid)='''|| FieldName ||''' and upper(sysid)='''|| SysId ||'''';\r\n");
		trigger.append("      execute immediate dySQL into isExists;\r\n");

		trigger.append("       --记录不存在需要新增 \r\n");
		trigger.append("        IF isExists = 0 THEN \r\n");
		trigger.append("          --更新变化后的值           \r\n");
		trigger.append("          dySQL := 'insert into ' ||  tableName ||'(UNIQUE_ID,fielditemid,oldValue,newValue,Sysid,flag) values(''' || UNIQUE_IDValue||''',upper('''|| FieldName ||'''),'''|| old_fieldValue ||''',''' || new_FieldValue || ''',upper('''||SysId || '''),1)';\r\n"); 
		trigger.append("          execute immediate dySQL;\r\n");
		          
		trigger.append("        ELSE--记录存在需要更新\r\n");
		                
		trigger.append("          --更新变化后的值\r\n");
		trigger.append("          dySQL := 'update '|| tableName || ' set newValue='''|| new_FieldValue ||''' where UNIQUE_ID='''|| UNIQUE_IDValue || ''' and upper(fielditemid)='''|| FieldName ||''' and upper(sysid)='''|| SysId ||'''';\r\n");
		trigger.append("          execute immediate dySQL;\r\n");
		trigger.append("          --更新同步状态\r\n");
		trigger.append("          dySQL := 'update '|| tableName ||' set flag = 1 where UNIQUE_ID='''|| UNIQUE_IDValue ||''' and upper(fielditemid)='''|| FieldName ||''' and upper(sysid)='''|| SysId ||''' and flag=0';\r\n");
		trigger.append("          execute immediate dySQL;\r\n");
		trigger.append("        END IF;\r\n");

		trigger.append("     END IF;\r\n");
		trigger.append("  RETURN NULL;\r\n");
		trigger.append("END FIELDCHANGE_UPDATE_OPT;\r\n");
		
		return trigger.toString();
	}
	
	private String createPhotoTrigger (String dbName) {
		StringBuffer trigger = new StringBuffer();
		trigger.append("create or replace trigger TR_PHOTO_CHANGE_" + dbName+ "A00\r\n");
		trigger.append("after insert or update or delete of ext on "+dbName+"a00 for each row \r\n");
		trigger.append("declare\r\n");
		trigger.append("  sflag integer; \r\n");
		trigger.append("  userid varchar2(100); \r\n");
		trigger.append("  syscode varchar2(20); \r\n");
		trigger.append("  strSQL varchar2(300); \r\n");
		trigger.append("  cout integer; \r\n");
		trigger.append("  type outappcursor is ref cursor; \r\n");
		trigger.append("  c_usra00 outappcursor; \r\n");
		trigger.append("begin \r\n");
		trigger.append("  if (:new.flag='P') then  \r\n");
		trigger.append("    if inserting then \r\n");
		trigger.append("      sflag:=2; \r\n");
		trigger.append("    end if; \r\n");

		trigger.append("    if updating then \r\n");
		trigger.append("      sflag:=2; \r\n");
		trigger.append("    end if; \r\n");

		trigger.append("    if deleting then \r\n");
		trigger.append("      sflag:=2; \r\n");
		trigger.append("    end if; \r\n");
       
		trigger.append("    select count(guidkey) into cout from " + dbName + "a01 where a0100=:new.a0100; \r\n");
		
		trigger.append("    if cout>0 then \r\n");
		trigger.append("      select guidkey into userid from " + dbName + "a01 where a0100=:new.a0100; \r\n");

		trigger.append("      open c_usra00 for  SELECT sys_id FROM t_sys_outsync WHERE state=1; \r\n");

		trigger.append("      fetch  c_usra00 into syscode; \r\n");
		trigger.append("      while c_usra00%found loop \r\n");
		trigger.append("      begin \r\n");
		trigger.append("        strSQL := 'update t_hr_view  set '||syscode||'p='||sflag||'  where unique_id='''||userid||''''; \r\n");
		trigger.append("        execute immediate strSQL; \r\n");
                  
		trigger.append("        fetch  c_usra00 into syscode; \r\n");
		trigger.append("      end; \r\n"); 
		trigger.append("      end loop; \r\n");
		trigger.append("      close c_usra00; \r\n");
		trigger.append("    end if; \r\n");
		trigger.append("  end if; \r\n");
       
		trigger.append("end; \r\n");
		
		
		return trigger.toString();
	}
	
	private String createFieldChangeTrigger (String type, List fieldList, List sysIdList) {
		StringBuffer trigger = new StringBuffer();
		
		String tab = "";
		if ("A".equalsIgnoreCase(type)) {
			tab = "HR";
		} else if ("B".equalsIgnoreCase(type)) {
			tab = "ORG";
		} else if ("K".equalsIgnoreCase(type)) {
			tab = "POST";
		}
		
		trigger.append("CREATE OR REPLACE TRIGGER TR_FIELD_CHANGE_T_");
		trigger.append(tab);
		trigger.append("_VIEW \r\n"); 
//		trigger.append("AFTER INSERT OR UPDATE OF ");
		trigger.append("AFTER INSERT OR UPDATE ");
		
//		for (int i = 0; i < fieldList.size(); i++) {
//			trigger.append(fieldList.get(i));
//			
//			if (i != fieldList.size() - 1) {
//				trigger.append(",");
//			}
//		}
		
		trigger.append(" ON T_");
		trigger.append(tab);
		trigger.append("_VIEW\r\n"); 
		trigger.append("FOR EACH ROW \r\n");
		trigger.append("BEGIN \r\n");
		trigger.append(" DECLARE  \r\n");  
		trigger.append("    --是否存在\r\n");
		trigger.append("    aa varchar2(20);\r\n \r\n");
		    
		trigger.append("  BEGIN \r\n \r\n");
		    
		trigger.append("    --新增 \r\n");
		trigger.append("    IF INSERTING THEN \r\n");
		
		for (int i = 0; i < fieldList.size(); i++) {
			String field = (String) fieldList.get(i);
//			trigger.append("      IF :new.");
//			trigger.append(field);
//			trigger.append(" is not null and :new.");
//			trigger.append(field);
//			trigger.append("<>'' THEN--字段值发生变化\r\n");
			for (int j = 0; j < sysIdList.size(); j++) {
				String sysId = (String) sysIdList.get(j);
				
				
				
				trigger.append(" aa := FIELDCHANGE_INSERT_OPT(:new."+ field+",'T_" + tab+ "_VIEW_LOG',:new.UNIQUE_ID,'"+field.toUpperCase()+"','"+sysId.toUpperCase()+"');\r\n");
				
//				trigger.append("        --判断记录是否存在 \r\n");
//				trigger.append("       select count(*) into isExists from t_");
//				trigger.append(tab);
//				trigger.append("_view_log where UNIQUE_ID=:new.UNIQUE_ID and upper(fielditemid)='");
//				trigger.append(field.toUpperCase());
//				trigger.append("' and upper(sysid)='");
//				trigger.append(sysId.toUpperCase());
//				trigger.append("';\r\n\r\n");
//				        
//				trigger.append("       --记录不存在需要新增 \r\n");
//				trigger.append("        IF isExists = 0 THEN \r\n");
//				trigger.append("          --更新变化后的值 \r\n");
//				trigger.append("          insert into t_");
//				trigger.append(tab);
//				trigger.append("_view_log(UNIQUE_ID,fielditemid,oldValue,newValue,Sysid,flag) values(:new.UNIQUE_ID,upper('");
//				trigger.append(field.toUpperCase());
//				trigger.append("'),''");
//
//				trigger.append(",:new.");
//				trigger.append(field.toUpperCase());
//				trigger.append(",upper('");
//				trigger.append(sysId.toUpperCase());
//				trigger.append("'),1); \r\n\r\n");
//				  
//				trigger.append("        ELSE--记录存在需要更新\r\n");
//				trigger.append("          --更新变化后的值\r\n");
//				trigger.append("          update t_");
//				trigger.append(tab);
//				trigger.append("_view_log set newValue=:new.");
//				trigger.append(field.toUpperCase());
//				trigger.append(" where UNIQUE_ID=:new.UNIQUE_ID and upper(fielditemid)='");
//				trigger.append(field.toUpperCase());
//				trigger.append("' and upper(sysid)='");
//				trigger.append(sysId.toUpperCase());
//				trigger.append("';\r\n\r\n");
//				          
//				trigger.append("          --更新同步状态\r\n");
//				trigger.append("          update t_");
//				trigger.append(tab);
//				trigger.append("_view_log set flag = 1 where UNIQUE_ID=:new.UNIQUE_ID and upper(fielditemid)='");
//				trigger.append(field.toUpperCase());
//				trigger.append("' and upper(sysid)='");
//				trigger.append(sysId.toUpperCase());
//				trigger.append("' and flag=0;\r\n");
//				trigger.append("        END IF;\r\n\r\n");
			}
//			trigger.append("     END IF;\r\n");
		
		}
		
		trigger.append("    ELSIF UPDATING THEN\r\n\r\n");
		      
		for (int i = 0; i < fieldList.size(); i++) {
			String field = (String) fieldList.get(i);
//			trigger.append("      IF :new.");
//			trigger.append(field);
//			trigger.append("<>:old.");
//			trigger.append(field);
//			trigger.append(" THEN--字段值发生变化\r\n");
			for (int j = 0; j < sysIdList.size(); j++) {
				String sysId = (String) sysIdList.get(j);
				
				trigger.append(" aa := FIELDCHANGE_UPDATE_OPT(:new."+ field+",:old."+field+",'T_" + tab+ "_VIEW_LOG',:new.UNIQUE_ID,'"+field.toUpperCase()+"','"+sysId.toUpperCase()+"');\r\n");
//				trigger.append("        --判断记录是否存在 \r\n");
//				trigger.append("       select count(*) into isExists from t_");
//				trigger.append(tab);
//				trigger.append("_view_log where UNIQUE_ID=:new.UNIQUE_ID and upper(fielditemid)='");
//				trigger.append(field.toUpperCase());
//				trigger.append("' and upper(sysid)='");
//				trigger.append(sysId.toUpperCase());
//				trigger.append("';\r\n\r\n");
//				        
//				trigger.append("       --记录不存在需要新增 \r\n");
//				trigger.append("        IF isExists = 0 THEN \r\n");
//				trigger.append("          --更新变化后的值 \r\n");
//				trigger.append("          insert into t_");
//				trigger.append(tab);
//				trigger.append("_view_log(UNIQUE_ID,fielditemid,oldValue,newValue,Sysid,flag) values(:new.UNIQUE_ID,upper('");
//				trigger.append(field.toUpperCase());
//				trigger.append("'),:old.");
//				trigger.append(field.toUpperCase());
//				trigger.append(",:new.");
//				trigger.append(field.toUpperCase());
//				trigger.append(",upper('");
//				trigger.append(sysId.toUpperCase());
//				trigger.append("'),1); \r\n\r\n");
//				  
//				trigger.append("        ELSE--记录存在需要更新\r\n");
//				trigger.append("          --更新变化后的值\r\n");
//				trigger.append("          update t_");
//				trigger.append(tab);
//				trigger.append("_view_log set newValue=:new.");
//				trigger.append(field.toUpperCase());
//				trigger.append(" where UNIQUE_ID=:new.UNIQUE_ID and upper(fielditemid)='");
//				trigger.append(field.toUpperCase());
//				trigger.append("' and upper(sysid)='");
//				trigger.append(sysId.toUpperCase());
//				trigger.append("';\r\n\r\n");
//				          
//				trigger.append("          --更新同步状态\r\n");
//				trigger.append("          update t_");
//				trigger.append(tab);
//				trigger.append("_view_log set flag = 1 where UNIQUE_ID=:new.UNIQUE_ID and upper(fielditemid)='");
//				trigger.append(field.toUpperCase());
//				trigger.append("' and upper(sysid)='");
//				trigger.append(sysId.toUpperCase());
//				trigger.append("' and flag=0;\r\n");
//				trigger.append("        END IF;\r\n\r\n");
			}
//			trigger.append("     END IF;\r\n");
		
		}
		      
		trigger.append("    END IF;\r\n\r\n");
		  
		  
		trigger.append("   -- update  t_");
		trigger.append(tab);
		trigger.append("_view_log set flag=0 where oldvalue=newvalue and flag =1;\r\n");
		trigger.append("  END;\r\n");
		trigger.append("END;\r\n");
		
		
		return trigger.toString();
	}
	
	private void createOrganization() throws GeneralException {
		String frigger = toOrganization();
		Statement st = null;
		try {
			st = conn.createStatement();
			if (frigger != null && frigger.length() > 0)
				st.execute(frigger);
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

	private String toUserA01(String dbName, List columns, List tranfields) {
		HrSyncBo hsb = new HrSyncBo(this.conn);
		String onlyfield = hsb.getAttributeValue(HrSyncBo.HR_ONLY_FIELD);// 唯一性指标
		if(onlyfield == null || onlyfield.length() < 1){
			onlyfield = "A0100";
		}
		String whereSql = " WHERE t_hr_view.UNIQUE_ID = syncKey";
		String table =  dbName.toUpperCase() + "A01";
		DbNameBo dbbo = new DbNameBo(conn);
		String username = dbbo.getLogonUserNameField();
		String password = dbbo.getLogonPassWordField();
		Set set = new HashSet();
		List list = new ArrayList();
		if (columns != null) {
			set.addAll(columns);
		}
		set.add(username);
		set.add(password);
		set.add("A0100");
		set.add("B0110");
		set.add("E0122");
		set.add("E01A1");
		set.add("A0101");
//		set.add("A0000");
		list.addAll(set);

		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE OR REPLACE TRIGGER ");
		frigger.append("TR_EMP_CHANGE_" + table + "\n");// ------触发器名称
		frigger.append("BEFORE INSERT OR UPDATE OR DELETE OF "); // --触发器触发类型
		/* ********************* 对于A01表的字段 *************************** */
		frigger.append(SyncFriggerTools.toFieldFrigger(list,
				SyncFriggerTools.O_MARK));// -----针对的字段
		frigger.append("\n");
		/* ********************* 对于A01表的字段 *************************** */
		frigger.append("ON " + table + " FOR EACH ROW\n");// ------针对的表
		/* ********************** 在触发器中声明变量 *********************** */
		frigger.append("DECLARE\n");
		frigger.append("  PRAGMA AUTONOMOUS_TRANSACTION;\n");
		frigger.append("  recExists integer;\n");
		frigger.append("  nbaseName varchar(100);\n");
		frigger.append("  syncKey varchar2(100);\n");
		frigger.append("  syncFlag char(1);\n");
		
		
		frigger.append("  type outappcursor is ref cursor;\n");
		frigger.append("  c_usra00 outappcursor;\n");
		frigger.append("  strSQL varchar2(500);\n");
		frigger.append("  syscode varchar2(20);\n");
		frigger.append("  code varchar2(50);\n");
		
		// 为所以字段定义值存储变量
		frigger.append("  empRow " + table + "%ROWTYPE;\n");
		// frigger.append(SyncFriggerTools.defFieldVar(list));
		// 为解释型字段定义存储变量
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,
				SyncFriggerTools.O_MARK));
		/* ********************** 在触发器中声明变量 *********************** */

		/* ********************** 在触发器中获得DBname *********************** */
		frigger.append("BEGIN\n");
		
		frigger.append("  IF UPDATING THEN\n");
		frigger.append("		IF ");
		
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			buffer.append(" and ((:new.");
			buffer.append(list.get(i));
			buffer.append(" IS NULL and :old.");
			buffer.append(list.get(i));
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append(list.get(i));
			buffer.append(" = :old.");
			buffer.append(list.get(i));
			buffer.append(")) ");
		}
		frigger.append(buffer.substring(4));
		buffer.delete(0, buffer.length());
		frigger.append("  THEN\n");
		frigger.append("  		return ;\n");
		frigger.append("  	END IF;\n");
		frigger.append("  END IF;\n");
		
		frigger.append("  IF DELETING THEN\n");
		frigger.append("    syncKey := :old.GUIDKEY;\n");
		if (list != null && !list.isEmpty()) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					frigger.append("    empRow." + column + " := :old."
							+ column + ";\n");
				}
			}
		}
		frigger.append("  ELSE\n");
		frigger.append("    IF :new.GUIDKEY IS NULL THEN\n");
		if(Sql_switcher.searchDbServerFlag() == Constant.DAMENG) {
			frigger.append("      :new.GUIDKEY := guid();\n");
		}else {
			frigger.append("      :new.GUIDKEY := sys_guid();\n");
		}
		frigger.append("    END IF;\n");
		
		/* start 处理人事异动 人员移库操作，数据视图出现一条空白记录，原因是移库前后人员guidkey不一致导致   wangb 2019-12-05*/
		frigger.append("    IF :new.GUIDKEY IS NOT NULL AND :old.GUIDKEY IS NOT NULL AND NVL(:new.GUIDKEY,'')<> NVL(:old.GUIDKEY,'') THEN\n");
		frigger.append("      UPDATE t_hr_view SET UNIQUE_ID=:new.GUIDKEY WHERE UNIQUE_ID=:old.GUIDKEY;\n");
		frigger.append("      COMMIT;\n");
		frigger.append("    END IF;\n");
		/* end 处理人事异动 人员移库操作，数据视图出现一条空白记录，原因是移库前后人员guidkey不一致导致   wangb 2019-12-05*/
		
		frigger.append("    syncKey := :new.GUIDKEY;\n");
		if (list != null && !list.isEmpty()) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					frigger.append("    empRow." + column + " := :new."
							+ column + ";\n");
				}
			}
		}
		frigger.append("  END IF;\n");

		frigger.append("  IF syncKey IS NOT NULL THEN\n");
		frigger.append("  BEGIN\n");
		frigger.append("    syncFlag := 0;--add\n");
		frigger.append("    BEGIN\n");
		/* 判断同步数据库是否存在 */
		frigger.append("      " + SyncFriggerTools.isExistsToRecord("recExists", "t_hr_view", whereSql, SyncFriggerTools.O_MARK) + ";\n");
		/* 判断同步数据库是否存在 */
		/* ************************** 查询人员库名称 *************************** */
		frigger.append("      " + SyncFriggerTools.getDbName("nbaseName", dbName, SyncFriggerTools.O_MARK) + ";\n");// 得到人员库 的描述
		/* ************************** 查询人员库名称 *************************** */
		frigger.append("    EXCEPTION\n");
		frigger.append("      WHEN NO_DATA_FOUND THEN\n");
		frigger.append("      RETURN;\n");
		frigger.append("    END;\n");
		
		/* 获得翻译性指标代码描述 */
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String tranColumn = (String) it.next();
				if (tranColumn != null) {
					frigger.append("    IF empRow." + tranColumn + " IS NOT NULL THEN\n");
					frigger.append("      " + tranColumn + "Desc := FUN_GET_CODEDESC('A01','" + tranColumn + "',empRow." + tranColumn + ",NULL,NULL);\n");
					frigger.append("    END IF;\n");
				}
			}
		}
		/* 获得翻译性指标代码描述 */
		
		frigger.append("    IF NOT DELETING AND :NEW." + onlyfield + " IS NOT NULL THEN\n");
		frigger.append("      IF NVL(recExists,0) = 0 THEN\n");
		frigger.append("        "
				+ SyncFriggerTools.insertA01(dbName, columns, tranfields,
						this.conn));
		frigger.append(";\n");
		frigger.append("        syncFlag := 1;--add\n");
		
		// 添加对照片标志的处理，初始化时为0
		if ("1".equals(photo)) {
			frigger.append("        open c_usra00 for  SELECT sys_id FROM t_sys_outsync WHERE state=1;\n");

			frigger.append("        fetch  c_usra00 into syscode;\n");
			frigger.append("        while c_usra00%found loop\n");
			frigger.append("        begin\n");
			frigger.append("			strSQL := 'update t_hr_view  set '||syscode||'p=0  where unique_id='''||syncKey||'''';\n");
			frigger.append("			execute immediate strSQL;\n");
	                  
			frigger.append("			fetch  c_usra00 into syscode;\n");
			frigger.append("        end;\n"); 
			frigger.append("        end loop;\n");
			frigger.append("        close c_usra00;\n");
			
		}
		
		frigger.append("      ELSE\n");
		frigger.append("        "
				+ SyncFriggerTools.updateA01(dbName, columns, tranfields,
						whereSql, this.conn));
		frigger.append(";\n");
		frigger.append("        syncFlag := 2;--mod\n");
		frigger.append("      END IF;\n");

		frigger.append("    ELSIF DELETING THEN\n");
		
		/*******************
		 * 暂时删除此处，不知当时为什么要把单位部门岗位清空，甚至a0100也清空
		 * wangzhongjun 
		 * 2012-07-14
		 * 
		 */			
//		frigger.append("      " + SyncFriggerTools.emptyAllRecord("WHERE UNIQUE_ID=syncKey AND UPPER(nbase_0)='" + dbName.toUpperCase() + "'", this.conn, HrSyncBo.A) + ";\n");
		
		frigger.append("      syncFlag := 3;--del\n");
		frigger.append("    END IF;\n");
		
		//--------------------------------------------------外部同步字段 重复 问题
		if(onlyfield!= null && onlyfield.length() > 0){
			String CusColumn = null;
			if("A0100".equalsIgnoreCase(onlyfield)){
				CusColumn = "A0100";
			}else{
				CusColumn = hsb.getAppAttributeValue(HrSyncBo.A, onlyfield);
			}
			
			// 去掉清空a0100
//			frigger.append("    IF empRow." + onlyfield + " IS NOT NULL THEN\n");
//			frigger.append("      UPDATE t_hr_view SET " + CusColumn + "=NULL WHERE " + CusColumn + "=empRow." + onlyfield + " and upper(nbase_0)<>'"+dbName.toUpperCase()+"' AND UNIQUE_ID <> syncKey;\n");
//			frigger.append("    END IF;\n");
		}
		//--------------------------------------------------外部同步字段 重复 问题
		frigger.append("    IF syncFlag <> 0 THEN\n");
		frigger.append("      PR_UP_SYNC_FLAG(syncFlag,NULL,syncKey,'" + dbName.toUpperCase() + "','A');\n");
		frigger.append("    END IF;\n");
		frigger.append("  END;\n");
		frigger.append("  END IF;\n");
		frigger.append("  COMMIT;\n");
		/* 新增B0110_code、E0122_code、E01A1_code 三个字段 */
		frigger.append("    SELECT B0110_0 into code FROM t_hr_view WHERE unique_id=syncKey;\n");
		frigger.append("    IF nvl(code,'') = '' then \n");
		frigger.append("	  update t_hr_view set B0110_code = NULL WHERE unique_id=syncKey;\n");
		frigger.append("    ELSE\n");
		frigger.append("      update t_hr_view set B0110_code = (SELECT corCode FROM organization where codeitemid=code) WHERE unique_id=syncKey;\n");
		frigger.append("      SELECT E0122_0 into code FROM t_hr_view WHERE unique_id=syncKey;\n");
		frigger.append("      IF nvl(code,'') = '' then \n");
		frigger.append("		update t_hr_view set E0122_code = NULL WHERE unique_id=syncKey;\n");
		frigger.append("      ELSE\n");
		frigger.append("        update t_hr_view set E0122_code = (SELECT corCode FROM organization where codeitemid=code) WHERE unique_id=syncKey;\n");
		frigger.append("        SELECT E01A1_0 into code FROM t_hr_view WHERE unique_id=syncKey;\n");
		frigger.append("        IF nvl(code,'') = '' then\n");
		frigger.append("		  update t_hr_view set E01A1_code = NULL WHERE unique_id=syncKey;\n");
		frigger.append("        ELSE\n");
		frigger.append("          update t_hr_view set E01A1_code = (SELECT corCode FROM organization where codeitemid=code) WHERE unique_id=syncKey;\n");
		frigger.append("        END IF;\n");
		frigger.append("      END IF;\n");
		frigger.append("    END IF;\n");
		frigger.append("  COMMIT;\n");
		/* 新增B0110_code、E0122_code、E01A1_code 三个字段 */
		frigger.append("END TR_EMP_CHANGE_" + table + ";");
		return frigger.toString();
	}

	private String toUserSub(String dbName, String table, List columns,
			List tranfields) {
		if (columns == null || columns.isEmpty()) {
			return "";
		}
		String whereSql = " WHERE t_hr_view.UNIQUE_ID = syncKey";
		String tableV = dbName.toUpperCase() + table;
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE OR REPLACE TRIGGER ");
		frigger.append("TR_EMP_CHANGE_" + tableV + "\n");// ------触发器名称
		frigger.append("AFTER INSERT OR UPDATE OR DELETE OF I9999,");
		/* ********************* 对于表的字段 *************************** */
		frigger.append(SyncFriggerTools.toFieldFrigger(columns,
				SyncFriggerTools.O_MARK));
		frigger.append("\n");
		/* ********************* 对于表的字段 *************************** */
		frigger.append("ON " + tableV + "\n");
		frigger.append("FOR EACH ROW\n");
		frigger.append("DECLARE\n");
		/* ***********************在数据库中定义变量************************** */
		frigger.append("  PRAGMA AUTONOMOUS_TRANSACTION;\n");
		frigger.append("  isMaxI9999 integer;\n");
		frigger.append("  syncKey varchar2(100);\n");
		// 为所以字段定义值存储变量
		frigger.append("  empRow " + tableV + "%ROWTYPE;\n");
		// frigger.append(SyncFriggerTools.defFieldVar(columns));
		// 为解释型字段定义存储变量
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,
				SyncFriggerTools.O_MARK));
		/* ***********************在数据库中定义变量************************** */
		frigger.append("BEGIN\n");
		
		
		
		
		/**当数据未发生变化时，将不执行触发器的其他代码*/
		frigger.append("  IF UPDATING THEN\n");
		frigger.append("		IF ");
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(" ((:new.");
		buffer.append("I9999");
		buffer.append(" IS NULL and :old.");
		buffer.append("I9999");
		buffer.append(" IS NULL) or ");
		
		buffer.append(" (:new.");
		buffer.append("I9999");
		buffer.append(" = :old.");
		buffer.append("I9999");
		buffer.append(")) ");
		
		for (int i = 0; i < columns.size(); i++) {
			buffer.append(" and ((:new.");
			buffer.append(columns.get(i));
			buffer.append(" IS NULL and :old.");
			buffer.append(columns.get(i));
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append(columns.get(i));
			buffer.append(" = :old.");
			buffer.append(columns.get(i));
			buffer.append(")) ");
		}
		frigger.append(buffer.toString());
//		buffer.delete(0, buffer.length());
		frigger.append("  THEN\n");
		frigger.append("  		return ;\n");
		frigger.append("  	END IF;\n");
		frigger.append("  END IF;\n");
		
		
		
		
		
		frigger.append("  IF DELETING THEN\n");
		frigger.append("    empRow.A0100 := :old.A0100;\n");
		frigger.append("    empRow.i9999 := :old.i9999;\n");
		frigger.append("  ELSE\n");
		frigger.append("    empRow.A0100 := :new.A0100;\n");
		frigger.append("    empRow.i9999 := :new.i9999;\n");
		for (int i = 0; i < columns.size(); i++) {
			frigger.append("    empRow." + columns.get(i) + " := :new."
					+ columns.get(i) + ";\n");
		}
		frigger.append("  END IF;\n");
		frigger.append("  BEGIN\n");
		frigger.append("    SELECT GUIDKEY INTO syncKey FROM " + dbName + "A01 WHERE  A0100 = empRow.A0100;\n");
		frigger.append("  EXCEPTION\n");
		frigger.append("    WHEN NO_DATA_FOUND THEN\n");
		frigger.append("      RETURN;\n");
		frigger.append("  END;\n");
		
		frigger.append("  IF INSERTING THEN\n");
		frigger.append("    SELECT NVL(MAX(I9999), 0) INTO isMaxI9999 FROM " + tableV + " WHERE A0100 = empRow.A0100;\n");
		frigger.append("  ELSE\n");
		frigger.append("    SELECT NVL(MAX(I9999), 0) INTO isMaxI9999 FROM " + tableV + " WHERE A0100 = empRow.A0100 AND i9999 <> :OLD.i9999;\n");
		frigger.append("  END IF;\n");
		frigger.append("  IF DELETING AND isMaxI9999=0 THEN\n");
		frigger.append("    " + SyncFriggerTools.emptySubRecord("t_hr_view", columns,whereSql, this.conn, HrSyncBo.A) + ";\n");
		frigger.append("  ELSIF (UPDATING AND :new.i9999 < :old.i9999 AND :old.i9999 > isMaxI9999) OR isMaxI9999 < empRow.i9999 THEN\n");
		frigger.append("    IF DELETING OR isMaxI9999 > empRow.i9999 THEN\n");
		frigger.append("      " + SyncFriggerTools.getMaxSubRecord(tableV, columns,"empRow", "WHERE I9999=isMaxI9999 AND A0100=empRow.A0100",SyncFriggerTools.O_MARK)+ ";\n");
		frigger.append("    END IF;\n");
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				frigger.append("    IF empRow." + column
						+ " IS NOT NULL THEN\n");
				if (column != null) {
					frigger.append("      " + column + "Desc := FUN_GET_CODEDESC('" + table + "','" + column + "',empRow." + column + ",NULL,NULL);\n");
				}
				frigger.append("    END IF;\n");
			}
		}
		frigger.append("    " + SyncFriggerTools.UpData(HrSyncBo.A, columns,tranfields, "empRow", whereSql,SyncFriggerTools.O_MARK, this.conn)+";\n");
		frigger.append("  ELSE\n");
		frigger.append("    syncKey := NULL;\n");
		frigger.append("  END IF;\n");
		frigger.append("  PR_UP_SYNC_FLAG('2',NULL,syncKey,'" + dbName.toUpperCase() + "','A_');\n");
		frigger.append("  COMMIT;\n");
		frigger.append("END TR_EMP_CHANGE_" + tableV + ";\n");
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
		List list  = new ArrayList();
		list.addAll(fieldSet);
		String whereSql = " WHERE t_hr_view.UNIQUE_ID = syncKey";
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE OR REPLACE TRIGGER ");
		frigger.append("TR_EMP_CHANGE_" + table + "\n");// ------触发器名称
		frigger.append("AFTER INSERT OR UPDATE OR DELETE OF I9999,");
		/* ********************* 对于表的字段 *************************** */
		frigger.append(SyncFriggerTools.toFieldFrigger(list,
				SyncFriggerTools.O_MARK));
		frigger.append("\n");
		/* ********************* 对于表的字段 *************************** */
		frigger.append("ON " + table + "\n");
		frigger.append("FOR EACH ROW\n");
		frigger.append("DECLARE\n");
		/* ***********************在数据库中定义变量************************** */
		frigger.append("  PRAGMA AUTONOMOUS_TRANSACTION;\n");
		frigger.append("  isMaxI9999 integer;\n");
		frigger.append("  syncKey varchar2(100);\n");
		if("1".equals(isJz)){
			frigger.append("  unit     varchar2(30);\n");
			frigger.append("  dept     varchar2(30);\n");
			if(!"".equals(post_field)){
				frigger.append("  post     varchar2(30);\n");
			}
			frigger.append("  jz_str   varchar2(300);\n");
			frigger.append("  TYPE cur_type IS REF CURSOR;\n");
			frigger.append("  c_jz cur_type;\n");
		}
		// 为所以字段定义值存储变量
		frigger.append("  empRow " + table + "%ROWTYPE;\n");
		// frigger.append(SyncFriggerTools.defFieldVar(columns));
		// 为解释型字段定义存储变量
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,
				SyncFriggerTools.O_MARK));
		/* ***********************在数据库中定义变量************************** */
		frigger.append("BEGIN\n");
		
		
		
		
		/**当数据未发生变化时，将不执行触发器的其他代码*/
		frigger.append("  IF UPDATING THEN\n");
		frigger.append("		IF ");
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(" ((:new.");
		buffer.append("I9999");
		buffer.append(" IS NULL and :old.");
		buffer.append("I9999");
		buffer.append(" IS NULL) or ");
		
		buffer.append(" (:new.");
		buffer.append("I9999");
		buffer.append(" = :old.");
		buffer.append("I9999");
		buffer.append(")) ");
		
		for (int i = 0; i < list.size(); i++) {
			buffer.append(" and ((:new.");
			buffer.append(list.get(i));
			buffer.append(" IS NULL and :old.");
			buffer.append(list.get(i));
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append(list.get(i));
			buffer.append(" = :old.");
			buffer.append(list.get(i));
			buffer.append(")) ");
		}
		frigger.append(buffer.toString());
//		buffer.delete(0, buffer.length());
		frigger.append("  THEN\n");
		frigger.append("  		return ;\n");
		frigger.append("  	END IF;\n");
		frigger.append("  END IF;\n");
		
		
		
		
		
		frigger.append("  IF DELETING THEN\n");
		frigger.append("    empRow.A0100 := :old.A0100;\n");
		frigger.append("    empRow.i9999 := :old.i9999;\n");
		frigger.append("  ELSE\n");
		frigger.append("    empRow.A0100 := :new.A0100;\n");
		frigger.append("    empRow.i9999 := :new.i9999;\n");
		if(columns != null && !columns.isEmpty()){
			for (int i = 0; i < columns.size(); i++) {
				frigger.append("    empRow." + columns.get(i) + " := :new."
						+ columns.get(i) + ";\n");
			}
		}
		frigger.append("  END IF;\n");
		frigger.append("  BEGIN\n");
		frigger.append("    SELECT GUIDKEY INTO syncKey FROM " + dbName + "A01 WHERE  A0100 = empRow.A0100;\n");
		frigger.append("  EXCEPTION\n");
		frigger.append("    WHEN NO_DATA_FOUND THEN\n");
		frigger.append("      RETURN;\n");
		frigger.append("  END;\n");
		if(columns != null && !columns.isEmpty()){
			frigger.append("  IF INSERTING THEN\n");
			frigger.append("    SELECT NVL(MAX(I9999), 0) INTO isMaxI9999 FROM " + table + " WHERE A0100 = empRow.A0100;\n");
			frigger.append("  ELSE\n");
			frigger.append("    SELECT NVL(MAX(I9999), 0) INTO isMaxI9999 FROM " + table + " WHERE A0100 = empRow.A0100 AND i9999 <> :OLD.i9999;\n");
			frigger.append("  END IF;\n");
			frigger.append("  IF DELETING AND isMaxI9999=0 THEN\n");
			frigger.append("    " + SyncFriggerTools.emptySubRecord("t_hr_view", columns,whereSql, this.conn, HrSyncBo.A) + ";\n");
			frigger.append("  ELSIF (UPDATING AND :new.i9999 < :old.i9999 AND :old.i9999 > isMaxI9999) OR isMaxI9999 < empRow.i9999 THEN\n");
			frigger.append("    IF DELETING OR isMaxI9999 > empRow.i9999 THEN\n");
			frigger.append("      " + SyncFriggerTools.getMaxSubRecord(table, columns,"empRow", "WHERE I9999=isMaxI9999 AND A0100=empRow.A0100",SyncFriggerTools.O_MARK)+ ";\n");
			frigger.append("    END IF;\n");
			if (tranfields != null && !tranfields.isEmpty()) {
				Iterator it = tranfields.iterator();
				while (it.hasNext()) {
					String column = (String) it.next();
					frigger.append("    IF empRow." + column
							+ " IS NOT NULL THEN\n");
					if (column != null) {
						frigger.append("      " + column + "Desc := FUN_GET_CODEDESC('" + setid.toUpperCase() + "','" + column + "',empRow." + column + ",NULL,NULL);\n");
					}
					frigger.append("    END IF;\n");
				}
			}
			frigger.append("    " + SyncFriggerTools.UpData(HrSyncBo.A, columns,tranfields, "empRow", whereSql,SyncFriggerTools.O_MARK, this.conn)+";\n");
			frigger.append("  ELSE\n");
			frigger.append("    syncKey := NULL;\n");
			frigger.append("  END IF;\n");
		}
		if("1".equals(isJz) && "".equals(post_field)){
			if (order_filed != null && order_filed.length() > 0) {
				frigger.append("  OPEN c_jz FOR SELECT " + unit_field + "," + dept_field + " FROM " + table + " WHERE A0100=empRow.A0100 AND i9999<>empRow.i9999  and "+ appoint_filed +"='0' order by " + order_filed + ";\n");
			} else {
				frigger.append("  OPEN c_jz FOR SELECT " + unit_field + "," + dept_field + " FROM " + table + " WHERE A0100=empRow.A0100 AND i9999<>empRow.i9999  and "+ appoint_filed +"='0' order by i9999;\n");
			}
			frigger.append("  FETCH c_jz INTO unit,dept;\n");
			frigger.append("  WHILE c_jz%FOUND LOOP\n");
			/* /兼职单位/兼职部门/兼职岗位/ */
			frigger.append("    IF LENGTH(jz_str) >0 AND unit IS NOT NULL AND dept IS NOT NULL THEN\n");
			frigger.append("      jz_str := jz_str || ',/' || unit || '/' || dept || '/';\n");
			frigger.append("    ELSIF unit IS NOT NULL AND dept IS NOT NULL THEN\n");
			frigger.append("      jz_str := '/' || unit || '/' || dept || '/';\n");
			frigger.append("    END IF;\n");
			frigger.append("    FETCH c_jz INTO unit,dept;\n");
			frigger.append("  END LOOP;\n");
			frigger.append("  CLOSE c_jz;\n");
			frigger.append("  IF NOT DELETING AND :NEW." + unit_field + " IS NOT NULL AND :NEW." + dept_field + " IS NOT NULL and :NEW." + appoint_filed + "='0' THEN\n");
			frigger.append("    IF LENGTH(jz_str) >0 THEN\n");
			frigger.append("      jz_str := jz_str || ',/' || :NEW." + unit_field + " || '/' || :NEW." + dept_field + " || '/';\n");
			frigger.append("    ELSIF :NEW." + unit_field + " IS NOT NULL AND :NEW." + dept_field + " IS NOT NULL THEN\n");
			frigger.append("      jz_str := '/' || :NEW." + unit_field + " || '/' || :NEW." + dept_field + " || '/';\n");
			frigger.append("    END IF;\n");
			frigger.append("  END IF;\n");
			//frigger.append("  IF LENGTH(jz_str) >0 THEN\n");
			frigger.append("    UPDATE t_hr_view SET jz_field = jz_str,sdate=sysdate WHERE t_hr_view.UNIQUE_ID=syncKey;\n");
			//frigger.append("  END IF;\n");
		}else if("1".equals(isJz) && !"".equals(post_field)){
			if (order_filed != null && order_filed.length() > 0) {
				frigger.append("  OPEN c_jz FOR SELECT " + unit_field + "," + dept_field + "," + post_field + " FROM " + table + " WHERE A0100=empRow.A0100 AND i9999<>empRow.i9999 and "+ appoint_filed +"='0' order by " + order_filed + ";\n");
			} else {
				frigger.append("  OPEN c_jz FOR SELECT " + unit_field + "," + dept_field + "," + post_field + " FROM " + table + " WHERE A0100=empRow.A0100 AND i9999<>empRow.i9999 and "+ appoint_filed +"='0' order by i9999;\n");
			}
			frigger.append("  FETCH c_jz INTO unit,dept,post;\n");
			frigger.append("  WHILE c_jz%FOUND LOOP\n");
			/* /兼职单位/兼职部门/兼职岗位/ */
			frigger.append("    IF LENGTH(jz_str) >0 AND unit IS NOT NULL AND dept IS NOT NULL AND post IS NOT NULL THEN\n");
			frigger.append("      jz_str := jz_str || ',/' || unit || '/' || dept || '/' || post || '/';\n");
			frigger.append("    ELSIF unit IS NOT NULL AND dept IS NOT NULL AND post IS NOT NULL THEN\n");
			frigger.append("      jz_str := '/' || unit || '/' || dept || '/' || post || '/';\n");
			frigger.append("    END IF;\n");
			frigger.append("    FETCH c_jz INTO unit,dept,post;\n");
			frigger.append("  END LOOP;\n");
			frigger.append("  CLOSE c_jz;\n");
			frigger.append("  IF NOT DELETING AND :NEW." + unit_field + " IS NOT NULL AND :NEW." + dept_field + " IS NOT NULL and :NEW." + post_field + " IS NOT NULL and :NEW." + appoint_filed + "='0' THEN\n");
			frigger.append("    IF LENGTH(jz_str) >0 THEN\n");
			frigger.append("      jz_str := jz_str || ',/' || :NEW." + unit_field + " || '/' || :NEW." + dept_field + " || '/' || :NEW." + post_field + " || '/'  ;\n");
			frigger.append("    ELSIF :NEW." + unit_field + " IS NOT NULL AND :NEW." + dept_field + " IS NOT NULL AND :NEW." + post_field + " IS NOT NULL THEN\n");
			frigger.append("      jz_str := '/' || :NEW." + unit_field + " || '/' || :NEW." + dept_field + " || '/' || :NEW." + post_field + " || '/';\n");
			frigger.append("    END IF;\n");
			frigger.append("  END IF;\n");
			//frigger.append("  IF LENGTH(jz_str) >0 THEN\n");
			frigger.append("    UPDATE t_hr_view SET jz_field = jz_str,sdate=sysdate WHERE t_hr_view.UNIQUE_ID=syncKey;\n");
			//frigger.append("  END IF;\n");
		}
		frigger.append("  PR_UP_SYNC_FLAG('2',NULL,syncKey,'" + dbName.toUpperCase() + "','A_');\n");
		frigger.append("  COMMIT;\n");
		frigger.append("END TR_EMP_CHANGE_" + table + ";\n");
		return frigger.toString();
	}
	
	private String generationOrganizationFrigger(String orgCode,
			String postCode, String hrCode) {
		DbWizard dbw = new DbWizard(this.conn);
		if (!dbw.isExistTable("t_hr_view",false) && !dbw.isExistTable("t_org_view",false)
				&& !dbw.isExistTable("t_post_view",false))
			return "";
		HrSyncBo hsb = new HrSyncBo(this.conn);

		/* 转大写 */
		orgCode = orgCode.toUpperCase();
		postCode = postCode.toUpperCase();
		hrCode = hrCode.toUpperCase();
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE OR REPLACE TRIGGER TR_ORG_CHANGE_ORGANIZATION\n");
		frigger.append("BEFORE INSERT OR UPDATE OR DELETE ");
//		frigger.append("OF CODEITEMID,CORCODE,CODESETID,CODEITEMDESC,PARENTID,GRADE,END_DATE,A0000\n");
		frigger.append("OF CODEITEMID,CORCODE,CODESETID,CODEITEMDESC,PARENTID,GRADE,END_DATE,LEVELA0000\n");//监听 levelA0000  wangb 20170807
		frigger.append("ON ORGANIZATION FOR EACH ROW\n");
		frigger.append("DECLARE\n");
		frigger.append("  PRAGMA AUTONOMOUS_TRANSACTION;\n");
		frigger.append("  syncKey VARCHAR2(100);\n");
		frigger.append("  TYPE outappcursor IS REF CURSOR;\n");
		frigger.append("  orgRow ORGANIZATION%ROWTYPE;\n");
		if ((hsb.isSync_b01() && dbw.isExistTable("t_org_view",false))
				|| (hsb.isSync_k01() && dbw.isExistTable("t_post_view",false))) {
			frigger.append("  syncFlag VARCHAR2(1);\n");// 部门同步标志
			frigger.append("  aParentDesc ORGANIZATION.CODEITEMDESC%TYPE ;\n");
			frigger.append("  recExists integer;\n");
			
		}
		if ((orgCode.indexOf("B0110") != -1 && hsb.isSync_b01() && dbw
				.isExistTable("t_org_view",false))
				|| (postCode.indexOf("E0122") != -1 && hsb.isSync_k01() && dbw
						.isExistTable("t_post_view",false))
				|| (hrCode.indexOf("E0122") != -1 && hsb.isSync_a01() && dbw
						.isExistTable("t_hr_view",false))) {
			frigger.append("  desc_cursor outappcursor;\n");
			frigger.append("  ORGID   VARCHAR2(100);\n");
			frigger.append("  ORGDESC VARCHAR2(1000);\n");
		}

		frigger.append("  aparentGUIDKEY ORGANIZATION.GUIDKEY%TYPE;\n");//记录上级机构唯一标识 变量  wangb 20170901 30941
		frigger.append("  num integer;\n");//上级机构记录是否存在 wangb 20170901 30941
		frigger.append("BEGIN\n");
		
		
		/**当数据未发生变化时，将不执行触发器的其他代码*/
		frigger.append("  IF UPDATING THEN \n");
		frigger.append("		IF ");
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(" ((:new.");
		buffer.append("CODEITEMID");
		buffer.append(" IS NULL and :old.");
		buffer.append("CODEITEMID");
		buffer.append(" IS NULL) or ");
		
		buffer.append(" (:new.");
		buffer.append("CODEITEMID");
		buffer.append(" = :old.");
		buffer.append("CODEITEMID");
		buffer.append(")) ");
//		CODEITEMID,CORCODE,CODESETID,CODEITEMDESC,PARENTID,GRADE,END_DATE,A0000
			buffer.append(" and ((:new.");
			buffer.append("CORCODE");
			buffer.append(" IS NULL and :old.");
			buffer.append("CORCODE");
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append("CORCODE");
			buffer.append(" = :old.");
			buffer.append("CORCODE");
			buffer.append(")) ");
			
			buffer.append(" and ((:new.");
			buffer.append("CODESETID");
			buffer.append(" IS NULL and :old.");
			buffer.append("CODESETID");
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append("CODESETID");
			buffer.append(" = :old.");
			buffer.append("CODESETID");
			buffer.append(")) ");
			
			
			buffer.append(" and ((:new.");
			buffer.append("CODEITEMDESC");
			buffer.append(" IS NULL and :old.");
			buffer.append("CODEITEMDESC");
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append("CODEITEMDESC");
			buffer.append(" = :old.");
			buffer.append("CODEITEMDESC");
			buffer.append(")) ");
			
			
			
			buffer.append(" and ((:new.");
			buffer.append("PARENTID");
			buffer.append(" IS NULL and :old.");
			buffer.append("PARENTID");
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append("PARENTID");
			buffer.append(" = :old.");
			buffer.append("PARENTID");
			buffer.append(")) ");
			
			
			
			
			buffer.append(" and ((:new.");
			buffer.append("GRADE");
			buffer.append(" IS NULL and :old.");
			buffer.append("GRADE");
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append("GRADE");
			buffer.append(" = :old.");
			buffer.append("GRADE");
			buffer.append(")) ");
			
			
			
			buffer.append(" and ((:new.");
			buffer.append("END_DATE");
			buffer.append(" IS NULL and :old.");
			buffer.append("END_DATE");
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append("END_DATE");
			buffer.append(" = :old.");
			buffer.append("END_DATE");
			buffer.append(")) ");
			
			/*修改A0000不调用触发器   wangb  20170804*/
//			buffer.append(" and ((:new.");
//			buffer.append("A0000");
//			buffer.append(" IS NULL and :old.");
//			buffer.append("A0000");
//			buffer.append(" IS NULL) or ");
//			
//			buffer.append(" (:new.");
//			buffer.append("A0000");
//			buffer.append(" = :old.");
//			buffer.append("A0000");
//			buffer.append(")) ");
			
			/*触发器监听更新levelA0000   wangb  20170807*/
			buffer.append(" and ((:new.");
			buffer.append("LEVELA0000");
			buffer.append(" IS NULL and :old.");
			buffer.append("LEVELA0000");
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append("LEVELA0000");
			buffer.append(" = :old.");
			buffer.append("LEVELA0000");
			buffer.append(")) ");
		
		frigger.append(buffer.toString());
//		buffer.delete(0, buffer.length());
		frigger.append("  THEN \n");
		frigger.append("  		return ;\n");
		frigger.append("  	END IF;\n");
		frigger.append("  END IF;\n");
		
		
		
		if((hsb.isSync_k01() && dbw.isExistTable("t_post_view",false)) ||(hsb.isSync_b01() && dbw.isExistTable("t_org_view",false))){
			String isNull = (String)SystemConfig.getPropertyValue("sync_org_top_isNull"); 
			frigger.append("  IF DELETING THEN\n");
			frigger.append("    syncKey := :old.GUIDKEY;\n");
			frigger.append("    orgRow.CODEITEMID := :old.CODEITEMID;\n");
			frigger.append("    orgRow.CODESETID := :old.CODESETID;\n");
			frigger.append("  ELSE\n");
			frigger.append("    IF :new.GUIDKEY IS NULL THEN\n");
			if(Sql_switcher.searchDbServerFlag() == Constant.DAMENG) {
				frigger.append("      :new.GUIDKEY := guid();\n");
			}else {
				frigger.append("      :new.GUIDKEY := sys_guid();\n");
			}
			frigger.append("    END IF;\n");
			frigger.append("    syncKey := :new.GUIDKEY;\n");
			frigger.append("    orgRow.CODEITEMID :=:new.CODEITEMID;\n");
			frigger.append("    orgRow.CORCODE:=:new.CORCODE;\n");
			frigger.append("    orgRow.CODESETID:=:new.CODESETID;\n");
			frigger.append("    orgRow.CODEITEMDESC:=:new.CODEITEMDESC;\n");
			frigger.append("    orgRow.PARENTID:=:new.PARENTID;\n");
			frigger.append("    orgRow.GRADE:=:new.GRADE;\n");
			frigger.append("    orgRow.END_DATE:=:new.END_DATE;\n");
//			frigger.append("    orgRow.A0000:=:new.A0000;\n");
			frigger.append("    orgRow.levelA0000:=:new.LEVELA0000;\n");//a0000值替换为 levelA0000 wangb 20170807
			frigger.append("    BEGIN\n");
			frigger.append("      SELECT CODEITEMDESC INTO aParentDesc FROM ORGANIZATION WHERE CODEITEMID = :new.PARENTID;\n");
			frigger.append("    EXCEPTION\n");
			frigger.append("      WHEN NO_DATA_FOUND THEN\n");
		    frigger.append("        aParentDesc := NULL;\n");
			frigger.append("    END;\n");
			frigger.append("    IF (:new.CODEITEMDESC IS NOT NULL) THEN\n");
			frigger.append("    BEGIN\n");
			frigger.append("      UPDATE t_org_view SET parentdesc=:new.CODEITEMDESC WHERE parentid=:new.CODEITEMID;\n");
			if(hsb.isSync_k01())
			    frigger.append("      UPDATE t_post_view SET parentdesc=:new.CODEITEMDESC WHERE parentid=:new.CODEITEMID;\n");
			frigger.append("    END;\n");
			frigger.append("    END IF;\n");
			//判断顶级机构的父级 是否 置空
			if("true".equalsIgnoreCase(isNull)){
				frigger.append("    IF :new.PARENTID=:new.CODEITEMID THEN\n");
				frigger.append("      orgRow.PARENTID:=NULL;\n");
				frigger.append("      aParentDesc := NULL;\n");
				frigger.append("    ELSE\n");
			}
			frigger.append("  END IF;\n");
			frigger.append("  IF NOT DELETING THEN\n");
			/*获取上级机构唯一标识 wangb 20170901 30941  31247 31250*/
			frigger.append("    IF orgRow.END_DATE > sysdate THEN\n");
			/*新增顶级机构时，查询不到记录 报错 改为  新增的机构编码和父级机构编码相同 为顶级机构  wangb 32378 */
			frigger.append("      IF :new.PARENTID = :new.CODEITEMID THEN\n");
			frigger.append("        aparentGUIDKEY := :new.GUIDKEY;\n");
			frigger.append("      ELSE\n");
			frigger.append("        begin\n");
			frigger.append("          select count(*) INTO num FROM ORGANIZATION WHERE CODEITEMID = :new.PARENTID;\n");//判断上级机构是否存在wangb 20170901 30941
			frigger.append("          EXCEPTION\n");
			frigger.append("          WHEN NO_DATA_FOUND THEN\n");
			frigger.append("            num := 0;\n");
			frigger.append("        end;\n");
			frigger.append("        begin\n");
			frigger.append("          IF NVL(num,0)=0 THEN\n");
			frigger.append("            select GUIDKEY INTO aparentGUIDKEY FROM ORGANIZATION WHERE CODEITEMID = :old.PARENTID;\n");
			frigger.append("          ELSE\n");
			frigger.append("            select GUIDKEY INTO aparentGUIDKEY FROM ORGANIZATION WHERE CODEITEMID = :new.PARENTID;\n");
			frigger.append("          END IF;\n");
			frigger.append("          EXCEPTION\n");
			frigger.append("          WHEN NO_DATA_FOUND THEN\n");
			frigger.append("            aparentGUIDKEY := null;\n");
			frigger.append("        end;\n");
			frigger.append("      END IF;\n");
			frigger.append("    END IF;\n");
			if(hsb.isSync_b01()){
				frigger.append("    IF NVL(orgRow.CODESETID,'@K')<>'@K' THEN\n");
				frigger.append("      SELECT COUNT(1) INTO recExists FROM t_org_view WHERE UNIQUE_ID=syncKey;\n");
				frigger.append("      IF NVL(recExists,0)=0 AND orgRow.END_DATE > sysdate THEN\n");
				frigger.append("      BEGIN\n");
				frigger.append("        IF (aParentDesc IS NOT NULL) THEN\n");
//				frigger.append("          insert into t_org_view(b0110_0,unique_id,codesetid,codeitemdesc,parentid,parentdesc,grade,a0000,corcode)\n");
				frigger.append("          insert into t_org_view(b0110_0,unique_id,codesetid,codeitemdesc,parentid,parentdesc,parentGUIDKEY,grade,levelA0000,corcode)\n");// a0000 替换为 levelA0000 wangb 20170811 和 添加同步上级机构唯一标识字段 wangb 20170901 30941
				frigger.append("          values(orgRow.codeitemid,syncKey,orgRow.codesetid,orgRow.codeitemdesc,orgRow.parentid,aParentDesc,aparentGUIDKEY,orgRow.grade,orgRow.levelA0000,orgRow.corcode);\n");//添加parentGUIDKEY 上级机构唯一标识 a0000 替换为 levelA0000 wangb 20170811 和 添加同步上级机构唯一标识字段 wangb 20170901 30941
//				frigger.append("          values(orgRow.codeitemid,syncKey,orgRow.codesetid,orgRow.codeitemdesc,orgRow.parentid,aParentDesc,orgRow.grade,orgRow.a0000,orgRow.corcode);\n");
				frigger.append("        ELSE\n");
//                frigger.append("          insert into t_org_view(b0110_0,unique_id,codesetid,codeitemdesc,parentid,grade,a0000,corcode)\n");
                frigger.append("          insert into t_org_view(b0110_0,unique_id,codesetid,codeitemdesc,parentid,parentGUIDKEY,grade,levelA0000,corcode)\n");// a0000 替换为 levelA0000 wangb 20170811 和 添加同步上级机构唯一标识字段 wangb 20170901 30941
                frigger.append("          values(orgRow.codeitemid,syncKey,orgRow.codesetid,orgRow.codeitemdesc,orgRow.parentid,aparentGUIDKEY,orgRow.grade,orgRow.levelA0000,orgRow.corcode);\n");// a0000 替换为 levelA0000 wangb 20170811 和 添加同步上级机构唯一标识字段 wangb 20170901 30941
//                frigger.append("          values(orgRow.codeitemid,syncKey,orgRow.codesetid,orgRow.codeitemdesc,orgRow.parentid,orgRow.grade,orgRow.a0000,orgRow.corcode);\n");
                frigger.append("        END IF;\n");                
				frigger.append("        syncFlag := 1;\n");
				frigger.append("      END;\n");
				frigger.append("      ELSE\n");
				frigger.append("        IF (aParentDesc IS NOT NULL) THEN\n");
				frigger.append("          UPDATE t_org_view\n");
				frigger.append("          SET b0110_0=orgRow.CODEITEMID,codesetid=orgRow.codesetid,codeitemdesc=orgRow.codeitemdesc,\n");
				frigger.append("              parentid=orgRow.parentid,parentdesc=aParentDesc,parentGUIDKEY=aparentGUIDKEY,grade=orgRow.grade,levelA0000=orgRow.levelA0000,corcode=orgRow.corcode\n");// a0000 替换为 levelA0000 wangb 20170811 和 添加同步上级机构唯一标识字段 wangb 20170901 30941
//				frigger.append("              parentid=orgRow.parentid,parentdesc=aParentDesc,grade=orgRow.grade,a0000=orgRow.a0000,corcode=orgRow.corcode\n");
				frigger.append("          WHERE UNIQUE_ID = syncKey;\n");
				frigger.append("        ELSE\n");
                frigger.append("          UPDATE t_org_view\n");
                frigger.append("          SET b0110_0=orgRow.CODEITEMID,codesetid=orgRow.codesetid,codeitemdesc=orgRow.codeitemdesc,\n");
                frigger.append("              parentid=orgRow.parentid,parentGUIDKEY=aparentGUIDKEY,grade=orgRow.grade,levelA0000=orgRow.levelA0000,corcode=orgRow.corcode\n");// a0000 替换为 levelA0000 wangb 20170811 和 添加同步上级机构唯一标识字段 wangb 20170901 30941
//                frigger.append("              parentid=orgRow.parentid,grade=orgRow.grade,a0000=orgRow.a0000,corcode=orgRow.corcode\n");
                frigger.append("          WHERE UNIQUE_ID = syncKey;\n");
                frigger.append("        END IF;\n");
				frigger.append("        syncFlag := 2;\n");
				frigger.append("      END IF;\n");
				frigger.append("      UPDATE t_org_view SET corcode=NULL WHERE corcode=orgRow.CORCODE AND UNIQUE_ID<>syncKey;\n");
				frigger.append("    END IF;\n");
			}
			if(hsb.isSync_k01()){
				frigger.append("    IF NVL(orgRow.CODESETID,'##')='@K' THEN\n");
				frigger.append("      SELECT COUNT(1) INTO recExists FROM t_post_view WHERE UNIQUE_ID=syncKey;\n");
				frigger.append("      IF NVL(recExists,0)=0 AND orgRow.END_DATE > sysdate THEN\n");
				frigger.append("        IF (aParentDesc IS NOT NULL) THEN\n");
				frigger.append("          insert into t_post_view(e01a1_0,unique_id,codesetid,codeitemdesc,parentid,parentdesc,parentGUIDKEY,grade,levelA0000,corcode) values(orgRow.codeitemid,syncKey,orgRow.codesetid,orgRow.codeitemdesc,orgRow.parentid,aParentDesc,aparentGUIDKEY,orgRow.grade,orgRow.levelA0000,orgRow.corcode);\n");//a0000 替换为 levelA0000 wangb 20170811 和 添加同步上级机构唯一标识字段 wangb 20170901 30941
//				frigger.append("          insert into t_post_view(e01a1_0,unique_id,codesetid,codeitemdesc,parentid,parentdesc,grade,a0000,corcode) values(orgRow.codeitemid,syncKey,orgRow.codesetid,orgRow.codeitemdesc,orgRow.parentid,aParentDesc,orgRow.grade,orgRow.a0000,orgRow.corcode);\n");
				frigger.append("        ELSE\n");
				frigger.append("          insert into t_post_view(e01a1_0,unique_id,codesetid,codeitemdesc,parentid,parentGUIDKEY,grade,levelA0000,corcode) values(orgRow.codeitemid,syncKey,orgRow.codesetid,orgRow.codeitemdesc,orgRow.parentid,aparentGUIDKEY,orgRow.grade,orgRow.levelA0000,orgRow.corcode);\n");//a0000 替换为 levelA0000 wangb 20170811 和 添加同步上级机构唯一标识字段 wangb 20170901 30941
//				frigger.append("          insert into t_post_view(e01a1_0,unique_id,codesetid,codeitemdesc,parentid,grade,a0000,corcode) values(orgRow.codeitemid,syncKey,orgRow.codesetid,orgRow.codeitemdesc,orgRow.parentid,orgRow.grade,orgRow.a0000,orgRow.corcode);\n");
				frigger.append("        END IF;\n");
				frigger.append("        syncFlag := 1;\n");
				frigger.append("      ELSE\n");
				frigger.append("        IF (aParentDesc IS NOT NULL) THEN\n");
				frigger.append("          update t_post_view set e01a1_0=orgRow.CODEITEMID,codesetid=orgRow.codesetid,codeitemdesc=orgRow.codeitemdesc,parentid=orgRow.parentid,parentdesc=aParentDesc,parentGUIDKEY=aparentGUIDKEY,grade=orgRow.grade,levelA0000=orgRow.levelA0000,corcode=orgRow.corcode WHERE UNIQUE_ID = syncKey;\n");//a0000 替换为 levelA0000 wangb 20170811 和 添加同步上级机构唯一标识字段 wangb 20170901 30941
//				frigger.append("          update t_post_view set e01a1_0=orgRow.CODEITEMID,codesetid=orgRow.codesetid,codeitemdesc=orgRow.codeitemdesc,parentid=orgRow.parentid,parentdesc=aParentDesc,grade=orgRow.grade,a0000=orgRow.a0000,corcode=orgRow.corcode WHERE UNIQUE_ID = syncKey;\n");
				frigger.append("        ELSE\n");
				frigger.append("          update t_post_view set e01a1_0=orgRow.CODEITEMID,codesetid=orgRow.codesetid,codeitemdesc=orgRow.codeitemdesc,parentid=orgRow.parentid,parentGUIDKEY=aparentGUIDKEY,grade=orgRow.grade,levelA0000=orgRow.levelA0000,corcode=orgRow.corcode WHERE UNIQUE_ID = syncKey;\n");//a0000 替换为 levelA0000 wangb 20170811 和 添加同步上级机构唯一标识字段 wangb 20170901 30941
//				frigger.append("          update t_post_view set e01a1_0=orgRow.CODEITEMID,codesetid=orgRow.codesetid,codeitemdesc=orgRow.codeitemdesc,parentid=orgRow.parentid,grade=orgRow.grade,a0000=orgRow.a0000,corcode=orgRow.corcode WHERE UNIQUE_ID = syncKey;\n");
				frigger.append("        END IF;\n");
				frigger.append("        syncFlag := 2;\n");
				frigger.append("      END IF;\n");
				frigger.append("      UPDATE t_post_view SET corcode=NULL WHERE corcode=orgRow.CORCODE AND UNIQUE_ID<>syncKey;\n");
				frigger.append("    END IF;\n");
			}
			frigger.append("    IF orgRow.END_DATE < sysdate THEN\n");
			frigger.append("      syncFlag := 3;\n");
			frigger.append("    END IF;\n");
			frigger.append("  ELSE\n");
			
			/*******************
			 * 暂时删除此处，不知当时为什么要把单位部门岗位清空，甚至a0100也清空
			 * wangzhongjun 
			 * 2012-07-14
			 * 
			 */	
			
//			if(hsb.isSync_b01()){
//				frigger.append("    IF NVL(orgRow.CODESETID,'@K')<>'@K' THEN\n");
//				frigger.append("      " + SyncFriggerTools.emptyAllRecord("WHERE UNIQUE_ID=syncKey", this.conn, HrSyncBo.B) + ";\n");
//				frigger.append("    END IF;\n");
//			}
//			if(hsb.isSync_k01()){
//				frigger.append("    IF NVL(orgRow.CODESETID,'##')='@K' THEN\n");
//				frigger.append("      " + SyncFriggerTools.emptyAllRecord("WHERE UNIQUE_ID=syncKey", this.conn, HrSyncBo.K) + ";\n");
//				frigger.append("    END IF;\n");
//			}
			
			
			frigger.append("    syncFlag := 3;\n");
			frigger.append("  END IF;\n");
		}
		
		frigger.append("  IF UPDATING AND orgRow.CODEITEMID IS NOT NULL THEN\n");// 修改
		/*记录原机构编码 wangb 20170811*/
		frigger.append("    IF :new.codeitemid <> :old.codeitemid THEN\n");
		frigger.append("      IF NVL(orgRow.codesetid,'##') <> '@K' THEN\n");
		if(hsb.isSync_b01())
			frigger.append("        UPDATE t_org_view SET origincodeitemid=:old.codeitemid WHERE unique_id = syncKey;\n");
		else
			frigger.append("        syncKey :=syncKey;--单位视图不能存在时,添加一行语句防止报错\n");
		frigger.append("      ELSE\n");
		if(hsb.isSync_k01())
			frigger.append("        UPDATE t_post_view SET origincodeitemid=:old.codeitemid WHERE unique_id = syncKey;\n");
		else
			frigger.append("        syncKey :=syncKey;--岗位视图不能存在时,添加一行语句防止报错\n");
		frigger.append("      END IF;\n");
		frigger.append("    END IF;\n");
		if ((hsb.isSync_b01() && dbw.isExistTable("t_org_view",false)) || (hsb.isSync_k01() && dbw.isExistTable("t_post_view",false))){
			frigger.append("    IF :new.codeitemdesc <> :old.codeitemdesc THEN\n");
			if (hsb.isSync_b01() && dbw.isExistTable("t_org_view",false)){
				frigger.append("      UPDATE t_org_view SET PARENTDESC=:new.codeitemdesc WHERE PARENTID = :new.CODEITEMID;\n");
				frigger.append("      PR_UP_SYNC_FLAG('2','PARENTID',:new.CODEITEMID,'','B_0');\n");
			}
			if (hsb.isSync_k01() && dbw.isExistTable("t_post_view",false)){
				frigger.append("      UPDATE t_post_view SET PARENTDESC=:new.codeitemdesc WHERE PARENTID = :new.CODEITEMID;\n");
				frigger.append("      PR_UP_SYNC_FLAG('2','PARENTID',:new.CODEITEMID,'','K_0');\n");
			}
			frigger.append("    END IF;\n");
		}
		
		/* ********************** 判断职位说明是否被修改开始 ******************* */
		frigger.append("    IF NVL(:new.CODESETID,'XX') = '@K' AND :new.codeitemdesc <> :old.codeitemdesc THEN\n");
		frigger.append("      syncKey :=syncKey;--人员视图或岗位视图不能存在时，防止报错\n");
		if (hsb.isSync_a01() && dbw.isExistTable("t_hr_view",false) && hrCode.indexOf("E01A1") != -1) {
			frigger.append("-- 判断职位说明是否被修改 \n");
			String curE01A1 = hsb.getAppAttributeValue(HrSyncBo.A,"E01A1");
			frigger.append("      UPDATE t_hr_view SET " + curE01A1 + "=:new.codeitemdesc WHERE E01A1_0 = :new.CODEITEMID;\n");
			frigger.append("      PR_UP_SYNC_FLAG('2','E01A1_0',:new.CODEITEMID,'','A_0');\n");
			
		}
		if(hsb.isSync_k01()	&& dbw.isExistTable("t_post_view", false) && postCode.indexOf("E01A1") != -1){
			//修改岗位名称时，触发器没有同步到数据视图  bug 37608  wangb 20180517
			String curE01A1 = hsb.getAppAttributeValue(HrSyncBo.K,"E01A1");
			frigger.append("      UPDATE t_post_view SET " + curE01A1 + "=:new.codeitemdesc WHERE E01A1_0 = :new.CODEITEMID;\n");
			frigger.append("      PR_UP_SYNC_FLAG('2','E01A1_0',:new.CODEITEMID,'','K_0');\n");
		}
		frigger.append("    END IF;\n");
		/* ********************** 判断职位说明是否被修改结束 ******************* */
		if (hsb.isSync_a01() && dbw.isExistTable("t_hr_view",false) && hrCode
				.indexOf("B0110") != -1){
			frigger.append("    IF NVL(:new.CODESETID,'XX') = 'UN' AND :new.codeitemdesc <> :old.codeitemdesc THEN\n");
			String curE01A1 = hsb.getAppAttributeValue(HrSyncBo.A,"B0110");
			frigger.append("      UPDATE t_hr_view SET " + curE01A1 + "=:new.codeitemdesc WHERE B0110_0 = :new.CODEITEMID;\n");
			frigger.append("      PR_UP_SYNC_FLAG('2','B0110_0',:new.CODEITEMID,'','A_0');\n");
			frigger.append("    END IF;\n");
		}
		if ((hsb.isSync_b01() && dbw.isExistTable("t_org_view",false) && orgCode.indexOf("B0110") != -1)
				|| (hsb.isSync_k01() && dbw.isExistTable("t_post_view",false) && postCode
						.indexOf("E0122") != -1)
				|| (hsb.isSync_a01() && dbw.isExistTable("t_hr_view",false) && hrCode
						.indexOf("E0122") != -1)) {

			frigger.append("    IF NVL(:new.CODESETID,'@K') <> '@K' AND :new.codeitemdesc <> :old.codeitemdesc THEN\n");
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.conn);
		    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		    if(uplevel==null||uplevel.length()==0)
		    	uplevel="0";
			frigger.append("    OPEN desc_cursor FOR SELECT CODEITEMID FROM ORGANIZATION WHERE CODEITEMID LIKE (:new.CODEITEMID || '%') AND CODESETID=:new.CODESETID AND GRADE <= (:new.GRADE + " + uplevel + ");\n");
			frigger.append("    FETCH desc_cursor INTO ORGID;\n");
			frigger.append("    WHILE desc_cursor%FOUND LOOP\n");
			frigger.append("      IF ORGID IS NOT NULL THEN\n");
			frigger.append("        ORGDESC := FUN_GET_CODEDESC('B01','B0110',ORGID,:new.CODEITEMID,:new.codeitemdesc);\n");
			
			if(hsb.isSync_b01() && dbw.isExistTable("t_org_view",false)
					&& orgCode.indexOf("B0110") != -1){
				String curB0110 = hsb.getAppAttributeValue(HrSyncBo.B, "B0110");
				frigger.append("        UPDATE t_org_view SET " + curB0110 + " = ORGDESC WHERE B0110_0 = ORGID;\n");
				frigger.append("        PR_UP_SYNC_FLAG('2','B0110_0',ORGID,'','B_0');\n");
			}
			if (hsb.isSync_k01() && dbw.isExistTable("t_post_view",false)
					&& postCode.indexOf("E0122") != -1) {
				String curE0122 = hsb.getAppAttributeValue(HrSyncBo.K,"E0122");
				frigger.append("        IF :new.CODESETID = 'UM' THEN\n");
				frigger.append("          UPDATE t_post_view SET " + curE0122 + " = ORGDESC WHERE E0122_0 = ORGID;\n");
				frigger.append("          PR_UP_SYNC_FLAG('2','E0122_0',ORGID,'','K_0');\n");
				frigger.append("        END IF;\n");
			}
			if (hsb.isSync_a01() && dbw.isExistTable("t_hr_view",false)
					&& hrCode.indexOf("E0122") != -1) {
				String curE0122 = hsb.getAppAttributeValue(HrSyncBo.A,
						"E0122");
				frigger.append("        IF :new.CODESETID = 'UM' THEN\n");
				frigger.append("          UPDATE t_hr_view SET " + curE0122 + " = ORGDESC WHERE E0122_0 = ORGID;\n");
				frigger.append("          PR_UP_SYNC_FLAG('2','E0122_0',ORGID,'','A_0');\n");
				frigger.append("        END IF;\n");
			}
			frigger.append("      END IF;\n");
			frigger.append("      FETCH desc_cursor INTO ORGID;\n");
			frigger.append("    END LOOP;\n");
			frigger.append("    CLOSE desc_cursor;\n");
			/* ******************** * 判断机构说明是否被修改 ******************* */
			frigger.append("    END IF;\n");
		}
		frigger.append("  END IF;\n");
		
		if (hsb.isSync_k01() && dbw.isExistTable("t_post_view",false)) {
			frigger.append("  IF syncKey IS NOT NULL AND NVL(orgRow.CODESETID,'XX') = '@K' THEN\n");// 删除
			frigger.append("    PR_UP_SYNC_FLAG(syncFlag,'',syncKey,'','K');\n");
			frigger.append("  END IF;\n");
		}
		if (hsb.isSync_b01() && dbw.isExistTable("t_org_view",false)) {
			frigger.append("  IF syncKey IS NOT NULL AND NVL(orgRow.CODESETID,'@K') <> '@K' THEN\n");// 删除
			frigger.append("    PR_UP_SYNC_FLAG(syncFlag,'',syncKey,'','B');\n");
			frigger.append("  END IF;\n");
		}
		frigger.append("  COMMIT;\n");
		frigger.append("END TR_ORG_CHANGE_ORGANIZATION;");
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

	private String toOrgB01(List columns, List tranfields) {
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE OR REPLACE TRIGGER TR_ORG_CHANGE_B01\n");
		frigger.append("AFTER INSERT OR UPDATE OR DELETE OF \n");
		if (columns == null)
			return "";
		frigger.append(SyncFriggerTools.toFieldFrigger(columns,
				SyncFriggerTools.O_MARK));
		frigger.append("\n");
		frigger.append("ON B01\n");
		frigger.append("  FOR EACH ROW\n");
		frigger.append("DECLARE\n");
		frigger.append("  PRAGMA AUTONOMOUS_TRANSACTION;\n");
		frigger.append("  recExists INTEGER;\n");
		frigger.append("  syncKey VARCHAR2(100);\n");
		frigger.append("  B01Row B01%ROWTYPE;\n");
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,
				SyncFriggerTools.O_MARK));
		frigger.append("BEGIN\n");
		
		/**当数据未发生变化时，将不执行触发器的其他代码*/
		frigger.append("  IF UPDATING THEN\n");
		frigger.append("		IF ");
		
		StringBuffer buffer = new StringBuffer();
		
		
		for (int i = 0; i < columns.size(); i++) {
			buffer.append(" and ((:new.");
			buffer.append(columns.get(i));
			buffer.append(" IS NULL and :old.");
			buffer.append(columns.get(i));
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append(columns.get(i));
			buffer.append(" = :old.");
			buffer.append(columns.get(i));
			buffer.append(")) ");
		}
		frigger.append(buffer.substring(4));
//		buffer.delete(0, buffer.length());
		frigger.append("  THEN\n");
		frigger.append("  		return ;\n");
		frigger.append("  	END IF;\n");
		frigger.append("  END IF;\n");
		
		
		frigger.append("  IF DELETING THEN\n");
		frigger.append("    B01Row.B0110 :=:OLD.B0110;\n");
		frigger.append("  ELSE\n");
		frigger.append("    B01Row.B0110 :=:NEW.B0110;\n");
		if (columns != null && !columns.isEmpty()) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					frigger.append("    B01Row." + column + ":= :new." + column + ";\n");
				}
			}
		}
		frigger.append("  END IF;\n");
		frigger.append("  BEGIN\n");
		frigger.append("    SELECT GUIDKEY INTO syncKey FROM ORGANIZATION WHERE CODEITEMID=:NEW.B0110 AND SYSDATE BETWEEN START_DATE AND END_DATE;\n");
		/* ********************** 判断是否存在这个记录 ********************** */
		frigger.append("    " + SyncFriggerTools.isExistsToRecord("recExists", "t_org_view","WHERE UNIQUE_ID = syncKey", SyncFriggerTools.O_MARK) + ";\n");
		/* ********************** 判断是否存在这个记录 ********************** */
		frigger.append("  EXCEPTION\n");
		frigger.append("    WHEN NO_DATA_FOUND THEN\n");
		frigger.append("    RETURN;\n");
		frigger.append("  END;\n");
		
		/* ********************** 获得B0110翻译指标代码 ********************** */
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null) {
					frigger.append("    IF B01Row." + column + " IS NOT NULL THEN\n");
					frigger.append("      " + column + "Desc := FUN_GET_CODEDESC('B01','" + column + "',B01Row." + column + ",NULL,NULL);\n");
					frigger.append("    END IF;\n");
				}
			}
		}
		/* ********************** 获得B0110翻译指标代码 ********************** */

		frigger.append("  IF NVL(recExists,0) <> 0 THEN\n");
		frigger.append("    " + SyncFriggerTools.updateB01(columns, tranfields,"WHERE UNIQUE_ID = syncKey", conn) + ";\n");// 更新t_org_view表数据
		frigger.append("    PR_UP_SYNC_FLAG('2',NULL,syncKey,NULL,'B_');\n");
		frigger.append("  END IF;\n");
		frigger.append("  COMMIT;\n");
		frigger.append("END TR_ORG_CHANGE_B01;\n");
		return frigger.toString();
	}

	private String toOrgSub(String table, List columns, List tranfields) {
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE OR REPLACE TRIGGER TR_ORG_CHANGE_" + table
				+ "\n");
		frigger.append("AFTER INSERT OR UPDATE OR DELETE OF I9999,");
		if (columns == null || columns.isEmpty())
			return "";
		frigger.append(SyncFriggerTools.toFieldFrigger(columns,
				SyncFriggerTools.O_MARK));
		frigger.append("\n");
		frigger.append("ON " + table + " FOR EACH ROW\n");
		frigger.append("DECLARE\n");
		frigger.append("  PRAGMA AUTONOMOUS_TRANSACTION;\n");

		frigger.append("  isMaxI9999 integer;\n");
		frigger.append("  syncKey varchar2(100);\n");
		frigger.append("  orgRow " + table + "%ROWTYPE;\n");
		// frigger.append(SyncFriggerTools.defFieldVar(columns));
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,
				SyncFriggerTools.O_MARK));

		frigger.append("BEGIN\n");
		
		
		/**当数据未发生变化时，将不执行触发器的其他代码*/
		frigger.append("  IF UPDATING THEN\n");
		frigger.append("		IF ");
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(" ((:new.");
		buffer.append("I9999");
		buffer.append(" IS NULL and :old.");
		buffer.append("I9999");
		buffer.append(" IS NULL) or ");
		
		buffer.append(" (:new.");
		buffer.append("I9999");
		buffer.append(" = :old.");
		buffer.append("I9999");
		buffer.append(")) ");
		
		for (int i = 0; i < columns.size(); i++) {
			buffer.append(" and ((:new.");
			buffer.append(columns.get(i));
			buffer.append(" IS NULL and :old.");
			buffer.append(columns.get(i));
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append(columns.get(i));
			buffer.append(" = :old.");
			buffer.append(columns.get(i));
			buffer.append(")) ");
		}
		frigger.append(buffer.toString());
//		buffer.delete(0, buffer.length());
		frigger.append("  THEN\n");
		frigger.append("  		return ;\n");
		frigger.append("  	END IF;\n");
		frigger.append("  END IF;\n");
		
		
		frigger.append("  IF DELETING THEN\n");
		frigger.append("    orgRow.B0110 := :old.B0110;\n");
		frigger.append("    orgRow.i9999 := :old.i9999;\n");
		frigger.append("  ELSE\n");
		frigger.append("    orgRow.B0110 := :new.B0110;\n");
		frigger.append("    orgRow.i9999 := :new.i9999;\n");
		for (int i = 0; i < columns.size(); i++) {
			frigger.append("    orgRow." + columns.get(i) + " := :new."
					+ columns.get(i) + ";\n");
		}
		frigger.append("  END IF;\n");

		frigger.append("  BEGIN\n");
		frigger.append("    SELECT GUIDKEY INTO syncKey FROM ORGANIZATION WHERE CODEITEMID=orgRow.B0110 AND SYSDATE BETWEEN START_DATE AND END_DATE;\n");
		frigger.append("  EXCEPTION\n");
		frigger.append("    WHEN NO_DATA_FOUND THEN\n");
		frigger.append("    RETURN;\n");
		frigger.append("  END;\n");	
		
		frigger.append("  IF INSERTING THEN\n");
		frigger.append("    SELECT NVL(MAX(I9999), 0) INTO isMaxI9999 FROM " + table + " WHERE B0110 = orgRow.B0110;\n");
		frigger.append("  ELSE\n");
		frigger.append("    SELECT NVL(MAX(I9999), 0) INTO isMaxI9999 FROM " + table + " WHERE B0110 = orgRow.B0110 AND i9999 <> :OLD.i9999;\n");
		frigger.append("  END IF;\n");
		frigger.append("  IF DELETING AND isMaxI9999=0 THEN\n");
		frigger.append("    " + SyncFriggerTools.emptySubRecord("t_org_view", columns,"WHERE UNIQUE_ID=syncKey", this.conn, HrSyncBo.B)+";\n");
		frigger.append("  ELSIF (UPDATING AND :new.i9999 < :old.i9999 AND :old.i9999 > isMaxI9999) OR isMaxI9999 < orgRow.i9999 THEN\n");
		frigger.append("    IF DELETING OR isMaxI9999 > orgRow.i9999 THEN\n");
		frigger.append("      " + SyncFriggerTools.getMaxSubRecord(table, columns, "orgRow", "WHERE I9999=isMaxI9999 AND B0110 = orgRow.B0110",SyncFriggerTools.O_MARK) + ";\n");
		frigger.append("    END IF;\n");
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null) {
					frigger.append("    IF orgRow." + column + " IS NOT NULL THEN\n");
					frigger.append("      " + column + "Desc := FUN_GET_CODEDESC('" + table + "','" + column + "',orgRow." + column + ",NULL,NULL);\n");
					frigger.append("    END IF;\n");
				}
			}
		}
		frigger.append("    " + SyncFriggerTools.UpData(HrSyncBo.B, columns, tranfields, "orgRow", "WHERE UNIQUE_ID = syncKey",SyncFriggerTools.O_MARK, this.conn) + ";\n");
		frigger.append("  ELSE\n");
		frigger.append("    syncKey := NULL;\n");
		frigger.append("  END IF;\n");
		
		frigger.append("  PR_UP_SYNC_FLAG('2','',syncKey,'','B_');\n");
		frigger.append("  COMMIT;\n");
		frigger.append("END TR_ORG_CHANGE_" + table + ";\n");
		return frigger.toString();
	}

	private String toPostK01(List columns, List tranfields) {
		StringBuffer frigger = new StringBuffer();
		frigger.append("CREATE OR REPLACE TRIGGER TR_POST_CHANGE_K01\n");
		frigger.append("AFTER INSERT OR UPDATE OR DELETE OF ");
		if (columns == null)
			return "";
		frigger.append(SyncFriggerTools.toFieldFrigger(columns,
				SyncFriggerTools.O_MARK));
		frigger.append("\n");
		frigger.append("ON K01 FOR EACH ROW\n");
		frigger.append("DECLARE\n");
		frigger.append("  PRAGMA AUTONOMOUS_TRANSACTION;\n");
		frigger.append("  recExists integer;\n");
		frigger.append("  syncKey varchar2(100);\n");
		frigger.append("  K01Row K01%ROWTYPE;\n");
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,
				SyncFriggerTools.O_MARK));
		frigger.append("BEGIN\n");
		
		
		/**当数据未发生变化时，将不执行触发器的其他代码*/
		frigger.append("  IF UPDATING THEN\n");
		frigger.append("		IF ");
		
		StringBuffer buffer = new StringBuffer();
		
		
		for (int i = 0; i < columns.size(); i++) {
			buffer.append(" and ((:new.");
			buffer.append(columns.get(i));
			buffer.append(" IS NULL and :old.");
			buffer.append(columns.get(i));
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append(columns.get(i));
			buffer.append(" = :old.");
			buffer.append(columns.get(i));
			buffer.append(")) ");
		}
		frigger.append(buffer.substring(4));
//		buffer.delete(0, buffer.length());
		frigger.append("  THEN\n");
		frigger.append("  		return ;\n");
		frigger.append("  	END IF;\n");
		frigger.append("  END IF;\n");
		
		
		
		
		//初始赋值
		frigger.append("  IF DELETING THEN --删除操作初始化\n");
		frigger.append("    K01Row.E01A1 :=:OLD.E01A1;\n");
		frigger.append("  ELSE --新增 或 修改操作初始化\n");
		frigger.append("    K01Row.E01A1 :=:NEW.E01A1;\n");
		frigger.append("    K01Row.E0122 :=:NEW.E0122;\n");
		if (columns != null && !columns.isEmpty()) {
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null && column.length() > 0) {
					frigger.append("      K01Row." + column + ":= :new."
							+ column + ";\n");
				}
			}
		}
		frigger.append("  END IF;\n");
		//初始赋值
		frigger.append("  BEGIN\n");
		frigger.append("    SELECT GUIDKEY INTO syncKey FROM ORGANIZATION WHERE CODEITEMID=K01Row.E01A1 AND SYSDATE BETWEEN START_DATE AND END_DATE;\n");
		/* ********************** 判断是否存在这个记录 ********************** */
		frigger.append("    " + SyncFriggerTools.isExistsToRecord("recExists", "t_post_view", "WHERE UNIQUE_ID = syncKey", SyncFriggerTools.O_MARK) + ";\n");
		/* ********************** 判断是否存在这个记录 ********************** */
		frigger.append("  EXCEPTION\n");
		frigger.append("    WHEN NO_DATA_FOUND THEN\n");
		frigger.append("    RETURN;\n");
		frigger.append("  END;\n");

		/* ********************** 获得翻译指标代码 ********************** */
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null) {
					frigger.append("    IF K01Row." + column + " IS NOT NULL THEN\n");
					frigger.append("      " + column + "Desc := FUN_GET_CODEDESC('K01','" + column + "',K01Row." + column + ",NULL,NULL);\n");
					frigger.append("    END IF;\n");
				}
			}
		}
		/* ********************** 获得翻译指标代码 ********************** */

		frigger.append("  IF NVL(recExists,0) <> 0 THEN\n");
		frigger.append("    " + SyncFriggerTools.updateK01(columns, tranfields, "WHERE UNIQUE_ID = syncKey", conn)+ ";\n");// 更新t_org_view表数据
		frigger.append("    PR_UP_SYNC_FLAG('2',NULL,syncKey,'','K_');\n");
		frigger.append("  END IF;\n");
		frigger.append("  COMMIT;\n");
		frigger.append("END TR_POST_CHANGE_K01;\n");
		return frigger.toString();
	}

	private String toPostSub(String table, List columns, List tranfields) {
		StringBuffer frigger = new StringBuffer();
		table = table.toUpperCase();
		frigger.append("CREATE OR REPLACE TRIGGER TR_POST_CHANGE_" + table
				+ "\n");
		frigger.append("AFTER INSERT OR UPDATE OR DELETE OF I9999,");
		if (columns == null || columns.isEmpty())
			return "";
		frigger.append(SyncFriggerTools.toFieldFrigger(columns,
				SyncFriggerTools.O_MARK));
		frigger.append("\n");
		frigger.append("ON " + table + " FOR EACH ROW\n");
		frigger.append("DECLARE\n");
		frigger.append("  PRAGMA AUTONOMOUS_TRANSACTION;\n");

		frigger.append("  isMaxI9999 integer;\n");
		frigger.append("  syncKey varchar2(100);\n");
		frigger.append("  postRow " + table + "%ROWTYPE;\n");
		frigger.append(SyncFriggerTools.defCodeVar(tranfields,
				SyncFriggerTools.O_MARK));
		frigger.append("BEGIN\n");
		
		
		/**当数据未发生变化时，将不执行触发器的其他代码*/
		frigger.append("  IF UPDATING THEN\n");
		frigger.append("		IF ");
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(" ((:new.");
		buffer.append("I9999");
		buffer.append(" IS NULL and :old.");
		buffer.append("I9999");
		buffer.append(" IS NULL) or ");
		
		buffer.append(" (:new.");
		buffer.append("I9999");
		buffer.append(" = :old.");
		buffer.append("I9999");
		buffer.append(")) ");
		
		for (int i = 0; i < columns.size(); i++) {
			buffer.append(" and ((:new.");
			buffer.append(columns.get(i));
			buffer.append(" IS NULL and :old.");
			buffer.append(columns.get(i));
			buffer.append(" IS NULL) or ");
			
			buffer.append(" (:new.");
			buffer.append(columns.get(i));
			buffer.append(" = :old.");
			buffer.append(columns.get(i));
			buffer.append(")) ");
		}
		frigger.append(buffer.toString());
//		buffer.delete(0, buffer.length());
		frigger.append("  THEN\n");
		frigger.append("  		return ;\n");
		frigger.append("  	END IF;\n");
		frigger.append("  END IF;\n");
		
		
		frigger.append("  IF DELETING THEN\n");
		frigger.append("    postRow.E01A1 := :old.E01A1;\n");
		frigger.append("    postRow.i9999 := :old.i9999;\n");
		frigger.append("  ELSE\n");
		frigger.append("    postRow.E01A1 := :new.E01A1;\n");
		frigger.append("    postRow.i9999 := :new.i9999;\n");
		for (int i = 0; i < columns.size(); i++) {
			frigger.append("    postRow." + columns.get(i) + " := :new."
					+ columns.get(i) + ";\n");
		}
		frigger.append("  END IF;\n");
		
		frigger.append("  BEGIN\n");
		frigger.append("    SELECT GUIDKEY INTO syncKey FROM ORGANIZATION WHERE CODEITEMID=postRow.E01A1 AND SYSDATE BETWEEN START_DATE AND END_DATE;\n");
		frigger.append("  EXCEPTION\n");
		frigger.append("    WHEN NO_DATA_FOUND THEN\n");
		frigger.append("      RETURN;\n");
		frigger.append("  END;\n");
		frigger.append("  IF INSERTING THEN\n");
		frigger.append("    SELECT NVL(MAX(I9999), 0) INTO isMaxI9999 FROM " + table + " WHERE E01A1 = postRow.E01A1;\n");
		frigger.append("  ELSE\n");
		frigger.append("    SELECT NVL(MAX(I9999), 0) INTO isMaxI9999 FROM " + table + " WHERE E01A1 = postRow.E01A1 AND i9999 <> :old.i9999;\n");
		frigger.append("  END IF;\n");
		frigger.append("  IF DELETING AND isMaxI9999=0 THEN\n");
		frigger.append("    " + SyncFriggerTools.emptySubRecord("t_post_view", columns,"WHERE UNIQUE_ID=syncKey", this.conn, HrSyncBo.K)+";\n");
		frigger.append("  ELSIF (UPDATING AND :new.i9999 < :old.i9999 AND :old.i9999 > isMaxI9999) OR isMaxI9999 < postRow.i9999 THEN\n");
		frigger.append("    IF DELETING OR isMaxI9999 > postRow.i9999 THEN\n");
		frigger.append("      " + SyncFriggerTools.getMaxSubRecord(table, columns, "postRow", "WHERE I9999=isMaxI9999 AND E01A1 = postRow.E01A1",SyncFriggerTools.O_MARK) + ";\n");
		frigger.append("    END IF;\n");
		if (tranfields != null && !tranfields.isEmpty()) {
			Iterator it = tranfields.iterator();
			while (it.hasNext()) {
				String column = (String) it.next();
				if (column != null) {
					frigger.append("    IF postRow." + column + " IS NOT NULL THEN\n");
					frigger.append("      " + column + "Desc := FUN_GET_CODEDESC('" + table + "','" + column + "',postRow." + column + ",NULL,NULL);\n");
					frigger.append("    END IF;\n");
				}
			}
		}
		frigger.append("    " + SyncFriggerTools.UpData(HrSyncBo.K, columns, tranfields, "postRow", "WHERE UNIQUE_ID = syncKey",SyncFriggerTools.O_MARK, this.conn) + ";\n");
		frigger.append("  ELSE\n");
		frigger.append("    syncKey := NULL;\n");
		frigger.append("  END IF;\n");
		frigger.append("  PR_UP_SYNC_FLAG('2',NULL,syncKey,NULL,'K_');\n");
		frigger.append("  COMMIT;\n");
		frigger.append("END TR_POST_CHANGE_" + table + ";\n");
		return frigger.toString();
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
			sql.append("/**\n");
			sql.append(" * 制作人：郑文龙\n");
			sql.append(" * 时间：  2011-08-03\n");
			sql.append(" * 作用：  翻译代码\n");
			sql.append(" * tableName   表名 对应FIELDITEM表中 FIELDSETID 字段 \n");
			sql.append(" * fieldName   字段名 对应FIELDITEM表中 ITEMID字段\n");
			sql.append(" * fieldValue  字段值 对应CODEITEM表中 CODEITEMID字段\n");
			//这两个字段是用于在修改 机构名称时候
			sql.append(" * cOrgID      字段值 对应CODEITEM表中 CODEITEMID字段\n");
			sql.append(" * cOrgDesc    字段值 对应CODEITEM表中 CODEITEMDESC字段\n");
			sql.append(" **/\n");
		    sql.append("CREATE OR REPLACE FUNCTION FUN_GET_CODEDESC(tableName IN VARCHAR2, fieldName IN VARCHAR2, fieldValue IN VARCHAR2,cOrgID IN VARCHAR2,cOrgDesc IN VARCHAR2) RETURN VARCHAR2 IS\n");
			sql.append("  Result     VARCHAR2(1000);\n");
			sql.append("  c_setid    VARCHAR2(10);\n");
			sql.append("  t_name     VARCHAR2(10);\n");
			sql.append("  f_name     VARCHAR2(10);\n");
			sql.append("  ORGID      VARCHAR2(100);\n");
			sql.append("  ORGCODE    VARCHAR2(100);\n");
			sql.append("  PARENTID   VARCHAR2(100);\n");
			sql.append("  ORGNAME    VARCHAR2(100);\n");
			sql.append("  i          INT;\n");
			
			sql.append("  cout       number;\n");
			
			sql.append("BEGIN\n");
			sql.append("  i := 0;\n");
			sql.append("  t_name := UPPER(tableName);\n");
			sql.append("  f_name := UPPER(fieldName);\n");
			sql.append("  IF (t_name = 'B01' AND f_name = 'B0110') OR f_name = 'E0122' THEN\n");
			sql.append("    ORGID := fieldValue;\n");
			sql.append("    WHILE i <= " + uplevel + " LOOP\n");
			sql.append("		select count(1) into cout from ORGANIZATION WHERE CODEITEMID = ORGID and sysdate between start_date and end_date;\n");
			sql.append("		if cout =1 then \n");
			sql.append("      BEGIN \n");
			sql.append("        SELECT CODEITEMDESC,PARENTID,CODESETID INTO ORGNAME,PARENTID,ORGCODE FROM ORGANIZATION WHERE CODEITEMID = ORGID and sysdate between start_date and end_date;\n");
			sql.append("      EXCEPTION\n");
            sql.append("        WHEN NO_DATA_FOUND THEN\n");
            sql.append("          PARENTID := NULL;\n");
            sql.append("      END;\n");
			sql.append("      IF PARENTID <> ORGID AND PARENTID IS NOT NULL AND (c_setid = ORGCODE OR c_setid IS NULL) THEN\n");
			sql.append("        IF cOrgID IS NOT NULL AND cOrgID = ORGID THEN\n");
			sql.append("        	IF i=0 THEN\n");
			sql.append("          		Result :=  cOrgDesc;\n");
			sql.append("        	ELSE\n");
			sql.append("          		Result :=  cOrgDesc || '" + sep + "' || Result;\n");
			sql.append("        	END IF;\n");
			
			sql.append("        ELSE\n");
			sql.append("        	IF i=0 THEN\n");
			sql.append("          		Result :=  ORGNAME;\n");
			sql.append("        	ELSE\n");
			sql.append("          		Result :=  ORGNAME || '" + sep + "' || Result;\n");
			sql.append("        	END IF;\n");
			
			sql.append("        END IF;\n");
			
			sql.append("        c_setid := ORGCODE;\n");
			sql.append("        ORGID := PARENTID;\n");
			sql.append("      ELSE\n");
			sql.append("		IF c_setid is null Then \n");
			sql.append("        	RETURN(cOrgDesc);\n");
			sql.append("        ELSIF ORGCODE<> c_setid	Then \n ");
			sql.append("        	RETURN(Result);\n");
			sql.append("        ELSIF ORGCODE= c_setid	Then \n ");
			sql.append("        	RETURN(cOrgDesc||'" + sep + "'||nvl(Result,''));\n");
			sql.append("        END IF;\n");
			sql.append("      END IF;\n");
			
			
			sql.append("		end if;");
			
			
			sql.append("      i := i + 1;\n");
			sql.append("    END LOOP;\n");
			sql.append("  ELSE\n");
			sql.append("    BEGIN\n");
			sql.append("      IF f_name = 'B0110' THEN\n");
			sql.append("        c_setid := 'UN';\n");
			sql.append("      ELSIF f_name = 'E01A1' THEN\n");
			sql.append("        c_setid := '@K';\n");
			sql.append("      ELSE\n");
			sql.append("        SELECT CODESETID INTO c_setid FROM FIELDITEM WHERE FIELDSETID = t_name AND ITEMID = f_name;\n");
			sql.append("      END IF;\n");
			sql.append("      IF c_setid = '@K' OR c_setid = 'UN' OR c_setid = 'UM' THEN\n");
			sql.append("		SELECT count(1) into cout FROM ORGANIZATION WHERE CODESETID = c_setid AND CODEITEMID = fieldValue and sysdate between start_date and end_date;\n");
			sql.append("		if cout = 1 then\n");
			sql.append("        	SELECT CODEITEMDESC INTO Result FROM ORGANIZATION WHERE CODESETID = c_setid AND CODEITEMID = fieldValue and sysdate between start_date and end_date;\n");
			sql.append("		end if;\n");
			sql.append("      ELSIF c_setid <> '0' THEN\n");
			sql.append(" 		SELECT count(1) into cout FROM CODEITEM WHERE CODESETID = c_setid AND CODEITEMID = fieldValue and sysdate between start_date and end_date;\n");
			sql.append("		if cout=1 then");
			if (fieldAndCode) {
				sql.append("        	SELECT CODEITEMID||'" + fieldAndCodeSeq + "'||CODEITEMDESC INTO Result FROM CODEITEM WHERE CODESETID = c_setid AND CODEITEMID = fieldValue and sysdate between start_date and end_date;\n");
			} else {
				sql.append("        	SELECT CODEITEMDESC INTO Result FROM CODEITEM WHERE CODESETID = c_setid AND CODEITEMID = fieldValue and sysdate between start_date and end_date;\n");
			}
			sql.append("		end if;\n");
			sql.append("      END IF;\n");
			sql.append("    EXCEPTION\n");
			sql.append("      WHEN NO_DATA_FOUND THEN\n");
			sql.append("        Result := NULL;\n");
			sql.append("    END;\n");
			sql.append("  END IF;\n");
			sql.append("  RETURN(Result);\n");
			sql.append("END FUN_GET_CODEDESC;\n");
			st.execute(sql.toString());

			sql.setLength(0);
			sql.append("/**\n");
			sql.append(" * 制作人：郑文龙\n");
			sql.append(" * 时间：  2011-08-03\n");
			sql.append(" * 作用：  修改人员（t_hr_view）、机构（t_org_view）和岗位（t_post_view）表同步标识\n");
			sql.append(" * syncFlag  修改的同步标识的值 1 新增 2 修改 3 删除 0 已同步\n");
			sql.append(" * keyName   主键名称 只有在 mark = A_0，B_0，K_0 时候起作用 否则 主键按照 unique_id 字段\n");
			sql.append(" * keyValue  主键值\n");
			sql.append(" * dbValue   人员库值 又有在同步人员时候有用  即 mark = A，A_，A_0\n");
			sql.append(" * mark      标识 = 人员（A 人员主集标识 A_ 人员子集标识 A_0 自定义）机构（B 人员主集标识 B_ 人员子集标识 B_0 自定义）\n");
			sql.append(" *                           岗位（K 人员主集标识 K_ 人员子集标识 K_0 自定义）\n");
			sql.append(" **/\n");
			sql.append("CREATE OR REPLACE PROCEDURE PR_UP_SYNC_FLAG\n");
			if(Sql_switcher.searchDbServerFlag() == Constant.DAMENG) {
				sql.append("(syncFlag NUMBER,keyName IN VARCHAR2, keyValue IN VARCHAR2,dbValue IN VARCHAR2, mark in CHAR(10))\n");
			}else {
				sql.append("(syncFlag NUMBER,keyName IN VARCHAR2, keyValue IN VARCHAR2,dbValue IN VARCHAR2, mark in CHAR)\n");
			}
			sql.append("IS\n");
			sql.append("  CURSOR outapp_cur IS SELECT sys_id FROM t_sys_outsync WHERE state=1;\n");
			sql.append("  strSQL VARCHAR2(1000);\n");
			sql.append("  outappid VARCHAR2(30);\n");
			sql.append("BEGIN\n");
			sql.append("  IF keyValue IS NOT NULL THEN\n");
			sql.append("    OPEN outapp_cur;\n");
			sql.append("    FETCH outapp_cur INTO outappid;\n");
			sql.append("	IF mark = 'A' THEN\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("      strSQL := 'UPDATE t_hr_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id =''' ||keyValue|| ''' AND UPPER(nbase_0) =''' ||dbValue||'''';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    ELSE \n");
//			sql.append("      strSQL := 'UPDATE t_hr_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ' WHERE unique_id =''' ||keyValue|| ''' AND UPPER(nbase_0) =''' ||dbValue||''' and nvl(flag,-1)<>1 and nvl(flag,-1)<>2';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("      strSQL := 'UPDATE t_hr_view SET sdate = sysdate WHERE unique_id =''' ||keyValue|| ''' AND UPPER(nbase_0) =''' ||dbValue||'''';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    END IF;\n");
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			
			sql.append("      WHILE outapp_cur%FOUND LOOP\n");
//			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||'''';\n");
//			sql.append("        EXECUTE IMMEDIATE strSQL;\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||'''';\n");
			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=CASE WHEN ' || outappid || '=1 AND 3=' || syncFlag || ' THEN 0 ELSE ' || syncFlag || ' END,sdate = sysdate WHERE UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114                        
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSE \n");
//			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=' || syncFlag || ' WHERE UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");
			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=CASE WHEN 3<>' || syncFlag || ' AND sys_flag=3 AND ' || outappid || '=0 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 AND ' || syncFlag || '<>3 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 THEN 0 ELSE ' || syncFlag || ' END WHERE UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("        strSQL := 'UPDATE t_hr_view SET sdate = sysdate WHERE UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    END IF;\n");
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO outappid;\n");
			sql.append("      END LOOP;\n");
			sql.append("      strSQL := 'UPDATE t_hr_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id =''' ||keyValue|| ''' AND UPPER(nbase_0) =''' ||dbValue||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSIF mark = 'A_' THEN\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("      strSQL := 'UPDATE t_hr_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id =''' ||keyValue|| ''' AND UPPER(nbase_0) =''' ||dbValue|| '''AND NVL(flag,0)=0' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    ELSE \n");
//			sql.append("      strSQL := 'UPDATE t_hr_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ' WHERE unique_id =''' ||keyValue|| ''' AND UPPER(nbase_0) =''' ||dbValue|| '''AND NVL(flag,0)=0 and nvl(flag,-1)<>1 and nvl(flag,-1)<>2' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("      strSQL := 'UPDATE t_hr_view SET sdate = sysdate WHERE unique_id =''' ||keyValue|| ''' AND UPPER(nbase_0) =''' ||dbValue|| '''AND NVL(flag,0)=0' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    END IF;\n");
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE outapp_cur%FOUND LOOP\n");
//			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||'''';\n");
//			sql.append("        EXECUTE IMMEDIATE strSQL;\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||'''';\n");
			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=CASE WHEN ' || outappid || '=1 AND 3=' || syncFlag || ' THEN 0 ELSE ' || syncFlag || ' END,sdate = sysdate WHERE NVL('||outappid||',0)=0 AND UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSE \n");
//			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");
			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=CASE WHEN 3<>' || syncFlag || ' AND sys_flag=3 AND ' || outappid || '=0 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 AND ' || syncFlag || '<>3 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 THEN 0 ELSE ' || syncFlag || ' END,sdate = sysdate WHERE NVL('||outappid||',0)=0 AND UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("        strSQL := 'UPDATE t_hr_view SET sdate = sysdate WHERE UPPER(nbase_0)= ''' || dbValue || ''' AND unique_id =''' || keyValue ||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    END IF;\n");
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO outappid;\n");
			sql.append("      END LOOP;\n");
			sql.append("      strSQL := 'UPDATE t_hr_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id =''' ||keyValue|| ''' AND UPPER(nbase_0) =''' ||dbValue|| '''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSIF mark = 'A_0' THEN\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("      strSQL := 'UPDATE t_hr_view SET flag = ' || syncFlag || ',sys_flag = ' || syncFlag || ',sdate = sysdate WHERE ' || keyName || '=''' || keyValue || ''' AND NVL(flag,0)=0' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    ELSE \n");
//			sql.append("      strSQL := 'UPDATE t_hr_view SET flag = ' || syncFlag || ',sys_flag = ' || syncFlag || ' WHERE ' || keyName || '=''' || keyValue || ''' AND NVL(flag,0)=0 and nvl(flag,-1)<>1 and nvl(flag,-1)<>2' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("      strSQL := 'UPDATE t_hr_view SET sdate = sysdate WHERE ' || keyName || '=''' || keyValue || ''' AND NVL(flag,0)=0' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    END IF;\n");
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE outapp_cur%FOUND LOOP\n");
//			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND ' || keyName || '=''' || keyValue ||'''';\n");
//			sql.append("        EXECUTE IMMEDIATE strSQL;\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND ' || keyName || '=''' || keyValue ||'''';\n");
			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=CASE WHEN ' || outappid || '=1 AND 3=' || syncFlag || ' THEN 0 ELSE ' || syncFlag || ' END,sdate = sysdate WHERE NVL('||outappid||',0)=0 AND ' || keyName || '=''' || keyValue ||'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSE \n");
//			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=' || syncFlag || ' WHERE NVL('||outappid||',0)=0 AND ' || keyName || '=''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");
			sql.append("        strSQL := 'UPDATE t_hr_view SET ' || outappid || '=CASE WHEN 3<>' || syncFlag || ' AND sys_flag=3 AND ' || outappid || '=0 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 AND ' || syncFlag || '<>3 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 THEN 0 ELSE ' || syncFlag || ' END WHERE NVL('||outappid||',0)=0 AND ' || keyName || '=''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("        strSQL := 'UPDATE t_hr_view SET sdate = sysdate WHERE ' || keyName || '=''' || keyValue ||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    END IF;\n");
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO outappid;\n");
			sql.append("      END LOOP;\n");
			sql.append("      strSQL := 'UPDATE t_hr_view SET flag = ' || syncFlag || ',sys_flag = ' || syncFlag || ',sdate = sysdate WHERE ' || keyName || '=''' || keyValue || '''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSIF mark = 'B' THEN\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("      strSQL := 'UPDATE t_org_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id =''' ||keyValue||'''';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    ELSE \n");
//			sql.append("      strSQL := 'UPDATE t_org_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ' WHERE unique_id =''' ||keyValue||''' and nvl(flag,-1)<>1 and nvl(flag,-1)<>2';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("      strSQL := 'UPDATE t_org_view SET sdate = sysdate WHERE unique_id =''' ||keyValue||'''';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    END IF;\n");
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE outapp_cur%FOUND LOOP\n");
//			sql.append("        strSQL := 'UPDATE t_org_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE unique_id =''' || keyValue ||'''';\n");
//			sql.append("        EXECUTE IMMEDIATE strSQL;\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("        strSQL := 'UPDATE t_org_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE unique_id =''' || keyValue ||'''';\n");
			sql.append("        strSQL := 'UPDATE t_org_view SET ' || outappid || '=CASE WHEN ' || outappid || '=1 AND 3=' || syncFlag || ' THEN 0 ELSE ' || syncFlag || ' END,sdate = sysdate WHERE unique_id =''' || keyValue ||'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSE \n");
//			sql.append("        strSQL := 'UPDATE t_org_view SET ' || outappid || '=' || syncFlag || ' WHERE unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");
			sql.append("        strSQL := 'UPDATE t_org_view SET ' || outappid || '=CASE WHEN 3<>' || syncFlag || ' AND sys_flag=3 AND ' || outappid || '=0 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 AND ' || syncFlag || '<>3 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 THEN 0 ELSE ' || syncFlag || ' END WHERE unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("        strSQL := 'UPDATE t_org_view SET sdate = sysdate WHERE unique_id =''' || keyValue ||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    END IF;\n");
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO outappid;\n");
			sql.append("      END LOOP;\n");
			sql.append("      strSQL := 'UPDATE t_org_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id =''' ||keyValue||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSIF mark = 'B_' THEN\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("      strSQL := 'UPDATE t_org_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id =''' ||keyValue|| ''' AND NVL(flag,0)=0';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    ELSE \n");
//			sql.append("      strSQL := 'UPDATE t_org_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ' WHERE unique_id =''' ||keyValue|| ''' AND NVL(flag,0)=0 and nvl(flag,-1)<>1 and nvl(flag,-1)<>2';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("      strSQL := 'UPDATE t_org_view SET sdate = sysdate WHERE unique_id =''' ||keyValue|| ''' AND NVL(flag,0)=0';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    END IF;\n");
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE outapp_cur%FOUND LOOP\n");
//			sql.append("         strSQL := 'UPDATE t_org_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND unique_id =''' || keyValue ||'''';\n");
//			sql.append("        EXECUTE IMMEDIATE strSQL;\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("         strSQL := 'UPDATE t_org_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND unique_id =''' || keyValue ||'''';\n");
			sql.append("         strSQL := 'UPDATE t_org_view SET ' || outappid || '=CASE WHEN ' || outappid || '=1 AND 3=' || syncFlag || ' THEN 0 ELSE ' || syncFlag || ' END,sdate = sysdate WHERE NVL('||outappid||',0)=0 AND unique_id =''' || keyValue ||'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSE \n");
//			sql.append("         strSQL := 'UPDATE t_org_view SET ' || outappid || '=' || syncFlag || ' WHERE NVL('||outappid||',0)=0 AND unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");
			sql.append("         strSQL := 'UPDATE t_org_view SET ' || outappid || '=CASE WHEN 3<>' || syncFlag || ' AND sys_flag=3 AND ' || outappid || '=0 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 AND ' || syncFlag || '<>3 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 THEN 0 ELSE ' || syncFlag || ' END WHERE NVL('||outappid||',0)=0 AND unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("         strSQL := 'UPDATE t_org_view SET sdate = sysdate WHERE unique_id =''' || keyValue ||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    END IF;\n");
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO outappid;\n");
			sql.append("      END LOOP;\n");
			sql.append("      strSQL := 'UPDATE t_org_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id =''' ||keyValue|| '''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSIF mark = 'B_0' THEN\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("      strSQL := 'UPDATE t_org_view SET flag = ' || syncFlag || ',sys_flag = ' || syncFlag || ',sdate = sysdate WHERE ' || keyName || '=''' || keyValue || ''' AND NVL(flag,0)=0' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    ELSE \n");
//			sql.append("      strSQL := 'UPDATE t_org_view SET flag = ' || syncFlag || ',sys_flag = ' || syncFlag || ' WHERE ' || keyName || '=''' || keyValue || ''' AND NVL(flag,0)=0 and nvl(flag,-1)<>1 and nvl(flag,-1)<>2' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("      strSQL := 'UPDATE t_org_view SET sdate = sysdate WHERE ' || keyName || '=''' || keyValue || ''' AND NVL(flag,0)=0' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    END IF;\n");
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE outapp_cur%FOUND LOOP\n");
//			sql.append("         strSQL := 'UPDATE t_org_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND ' || keyName || ' =''' || keyValue ||'''';\n");
//			sql.append("        EXECUTE IMMEDIATE strSQL;\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("         strSQL := 'UPDATE t_org_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND ' || keyName || ' =''' || keyValue ||'''';\n");
			sql.append("         strSQL := 'UPDATE t_org_view SET ' || outappid || '=CASE WHEN ' || outappid || '=1 AND 3=' || syncFlag || ' THEN 0 ELSE ' || syncFlag || ' END,sdate = sysdate WHERE NVL('||outappid||',0)=0 AND ' || keyName || ' =''' || keyValue ||'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSE \n");
//			sql.append("         strSQL := 'UPDATE t_org_view SET ' || outappid || '=' || syncFlag || ' WHERE NVL('||outappid||',0)=0 AND ' || keyName || ' =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");
			sql.append("         strSQL := 'UPDATE t_org_view SET ' || outappid || '=CASE WHEN 3<>' || syncFlag || ' AND sys_flag=3 AND ' || outappid || '=0 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 AND ' || syncFlag || '<>3 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 THEN 0 ELSE ' || syncFlag || ' END WHERE NVL('||outappid||',0)=0 AND ' || keyName || ' =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("         strSQL := 'UPDATE t_org_view SET sdate = sysdate WHERE ' || keyName || ' =''' || keyValue ||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    END IF;\n");
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO outappid;\n");
			sql.append("      END LOOP;\n");
			sql.append("      strSQL := 'UPDATE t_org_view SET flag = ' || syncFlag || ',sys_flag = ' || syncFlag || ',sdate = sysdate WHERE ' || keyName || '=''' || keyValue || '''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSIF mark = 'K' THEN\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("      strSQL := 'UPDATE t_post_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id ='''|| keyValue ||'''';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    ELSE \n");
//			sql.append("      strSQL := 'UPDATE t_post_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ' WHERE unique_id ='''|| keyValue ||''' and nvl(flag,-1)<>1 and nvl(flag,-1)<>2';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("      strSQL := 'UPDATE t_post_view SET sdate = sysdate WHERE unique_id ='''|| keyValue ||'''';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    END IF;\n");
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE outapp_cur%FOUND LOOP\n");
//			sql.append("        strSQL := 'UPDATE t_post_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE unique_id =''' || keyValue ||'''';\n");
//			sql.append("        EXECUTE IMMEDIATE strSQL;\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("        strSQL := 'UPDATE t_post_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE unique_id =''' || keyValue ||'''';\n");
			sql.append("        strSQL := 'UPDATE t_post_view SET ' || outappid || '=CASE WHEN ' || outappid || '=1 AND 3=' || syncFlag || ' THEN 0 ELSE ' || syncFlag || ' END,sdate = sysdate WHERE unique_id =''' || keyValue ||'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSE \n");
//			sql.append("        strSQL := 'UPDATE t_post_view SET ' || outappid || '=' || syncFlag || ' WHERE unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");
			sql.append("        strSQL := 'UPDATE t_post_view SET ' || outappid || '=CASE WHEN 3<>' || syncFlag || ' AND sys_flag=3 AND ' || outappid || '=0 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 AND ' || syncFlag || '<>3 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 THEN 0 ELSE ' || syncFlag || ' END WHERE unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("        strSQL := 'UPDATE t_post_view SET sdate = sysdate WHERE unique_id =''' || keyValue ||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    END IF;\n");
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO outappid;\n");
			sql.append("      END LOOP;\n");
			sql.append("      strSQL := 'UPDATE t_post_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id ='''|| keyValue ||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSIF mark = 'K_' THEN\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("      strSQL := 'UPDATE t_post_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id ='''|| keyValue ||''' AND NVL(flag,0)=0';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    ELSE \n");
//			sql.append("      strSQL := 'UPDATE t_post_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id ='''|| keyValue ||''' AND NVL(flag,0)=0 and nvl(flag,-1)<>1 and nvl(flag,-1)<>2';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("      strSQL := 'UPDATE t_post_view SET sdate = sysdate WHERE unique_id ='''|| keyValue ||''' AND NVL(flag,0)=0';\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    END IF;\n");
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE outapp_cur%FOUND LOOP\n");
//			sql.append("        strSQL := 'UPDATE t_post_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND unique_id =''' || keyValue ||'''';\n");
//			sql.append("        EXECUTE IMMEDIATE strSQL;\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("        strSQL := 'UPDATE t_post_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND unique_id =''' || keyValue ||'''';\n");
			sql.append("        strSQL := 'UPDATE t_post_view SET ' || outappid || '=CASE WHEN ' || outappid || '=1 AND 3=' || syncFlag || ' THEN 0 ELSE ' || syncFlag || ' END,sdate = sysdate WHERE NVL('||outappid||',0)=0 AND unique_id =''' || keyValue ||'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSE \n");
//			sql.append("        strSQL := 'UPDATE t_post_view SET ' || outappid || '=' || syncFlag || ' WHERE NVL('||outappid||',0)=0 AND unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");
			sql.append("        strSQL := 'UPDATE t_post_view SET ' || outappid || '=CASE WHEN 3<>' || syncFlag || ' AND sys_flag=3 AND ' || outappid || '=0 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 AND ' || syncFlag || '<>3 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 THEN 0 ELSE ' || syncFlag || ' END WHERE NVL('||outappid||',0)=0 AND unique_id =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("        strSQL := 'UPDATE t_post_view SET sdate = sysdate WHERE unique_id =''' || keyValue ||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    END IF;\n");
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO outappid;\n");
			sql.append("      END LOOP;\n");
			sql.append("      strSQL := 'UPDATE t_post_view SET flag =' ||syncFlag|| ',sys_flag =' ||syncFlag|| ',sdate = sysdate WHERE unique_id ='''|| keyValue ||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSIF mark = 'K_0' THEN\n");
			
//			/****2012-09-09 wangzhongjun 修改******/
//			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("      strSQL := 'UPDATE t_post_view SET flag = ' || syncFlag || ',sys_flag = ' || syncFlag || ',sdate = sysdate WHERE ' || keyName || '=''' || keyValue || ''' AND NVL(flag,0)=0' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    ELSE \n");
//			sql.append("      strSQL := 'UPDATE t_post_view SET flag = ' || syncFlag || ',sys_flag = ' || syncFlag || ',sdate = sysdate WHERE ' || keyName || '=''' || keyValue || ''' AND NVL(flag,0)=0 and nvl(flag,-1)<>1 and nvl(flag,-1)<>2' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("      strSQL := 'UPDATE t_post_view SET sdate = sysdate WHERE ' || keyName || '=''' || keyValue || ''' AND NVL(flag,0)=0' ;\n");
//			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
//			sql.append("    END IF;\n");
//			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("      WHILE outapp_cur%FOUND LOOP\n");
//			sql.append("         strSQL := 'UPDATE t_post_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND ' || keyName || ' =''' || keyValue ||'''';\n");
//			sql.append("        EXECUTE IMMEDIATE strSQL;\n");
			
			/****2012-09-09 wangzhongjun 修改******/
			sql.append("	IF (syncFlag<>1 and syncFlag<>2) THEN\n");
//			sql.append("         strSQL := 'UPDATE t_post_view SET ' || outappid || '=' || syncFlag || ',sdate = sysdate WHERE NVL('||outappid||',0)=0 AND ' || keyName || ' =''' || keyValue ||'''';\n");
			sql.append("         strSQL := 'UPDATE t_post_view SET ' || outappid || '=CASE WHEN ' || outappid || '=1 AND 3=' || syncFlag || ' THEN 0 ELSE ' || syncFlag || ' END,sdate = sysdate WHERE NVL('||outappid||',0)=0 AND ' || keyName || ' =''' || keyValue ||'''';\n");// 执行 同步 或 删除 操作， 当前视图状态为新增 且 执行删除时，结果为删除已同步  否则 结果为原来规则值          wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    ELSE \n");
//			sql.append("         strSQL := 'UPDATE t_post_view SET ' || outappid || '=' || syncFlag || ' WHERE NVL('||outappid||',0)=0 AND ' || keyName || ' =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");
			sql.append("         strSQL := 'UPDATE t_post_view SET ' || outappid || '=CASE WHEN 3<>' || syncFlag || ' AND sys_flag=3 AND ' || outappid || '=0 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 AND ' || syncFlag || '<>3 THEN 1 WHEN sys_flag=3 AND ' || outappid || '=0 THEN 0 ELSE ' || syncFlag || ' END WHERE NVL('||outappid||',0)=0 AND ' || keyName || ' =''' || keyValue ||''' and nvl(' || outappid || ',-1)<>1 and nvl(' || outappid || ',-1)<>2';\n");//执行 新增或修改 操作，当前视图状态为 删除已同步时，结果不变还是删除已同步状态 否则 结果为原来规则值   wangb 20171114
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("         strSQL := 'UPDATE t_post_view SET sdate = sysdate WHERE ' || keyName || ' =''' || keyValue ||'''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    END IF;\n");
			/****2012-09-09 wangzhongjun 修改*****end*/
			
			sql.append("        FETCH outapp_cur INTO outappid;\n");
			sql.append("      END LOOP;\n");
			sql.append("      strSQL := 'UPDATE t_post_view SET flag = ' || syncFlag || ',sys_flag = ' || syncFlag || ',sdate = sysdate WHERE ' || keyName || '=''' || keyValue || '''';\n");
			sql.append("      EXECUTE IMMEDIATE strSQL;\n");
			sql.append("    END IF;\n");
			sql.append("    CLOSE outapp_cur;\n");
			sql.append("  END IF;\n");
			sql.append("END PR_UP_SYNC_FLAG;\n");
			st.execute(sql.toString());
		} catch (SQLException e) {
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
	
	public static void main(String[] args) {
		FriggerToOracle fq = new FriggerToOracle(null);
		fq.CreateFunTransCode();
	}
}
