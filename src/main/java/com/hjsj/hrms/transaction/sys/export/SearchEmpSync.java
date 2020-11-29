package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p> Title:SearchEmpSync.java </p>
 * <p> Description: </p>
 * <p> Company:HJHJ </p>
 * <p> Create time:Mar 24, 2008 </p>
 * 
 * @author FengXiBin
 * @version 4.0
 */
public class SearchEmpSync extends IBusiness {

	public void execute() throws GeneralException {
		try {
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String emporg = (String) this.getFormHM().get("emporg");
			String tacitly = (String) this.getFormHM().get("tacitly");
			String state = (String) this.getFormHM().get("state");
			if (emporg == null)
				emporg = (String) hm.get("emporg");
			hm.remove("emporg");
			if(tacitly==null)
				tacitly = "全部";
			if(state == null)
				state = "all";
			ArrayList statelist = createState();
			this.getFormHM().put("statelist", statelist);

			//xus 18/3/15
			String url=selectUrl(tacitly);
			this.getFormHM().put("url", url);
			// 姓名
//			String select_name = (String) this.getFormHM().get("select_name");
			/*
			 * 区分 查询按钮和机构查询
			 * 机构查询 查询框内容清空
			 * 查询框查询 select_name 才有值 且 清除查询拼接参数   wangb 20170830 31073
			 */
			String select_name="";
			String type=(String)hm.get("select");
			if(type !=null && "1".equalsIgnoreCase(type)){
				// 姓名
				select_name = (String) this.getFormHM().get("select_name");
				hm.remove("select");
			}else{
				this.getFormHM().put("select_name", "");
			}
			ArrayList sysid = selectSysid();
			this.getFormHM().put("sysid", sysid);
			String a_code = (String) hm.get("a_code");
			HrSyncBo hsb = new HrSyncBo(this.frameconn);
			ArrayList typelist = new ArrayList();
			String sync_mode = hsb.getAttributeValue(HrSyncBo.SYNC_MODE);

			if (hsb.isSync_a01()) {
				CommonData da = new CommonData();
				da.setDataName("人员视图");
				da.setDataValue("1");
				typelist.add(da);
				hsb.creatHrTable("t_hr_view");
				/*人员视图表不存在创建并初始化,缺少外部系统字段,添加对应外部系统字段     wangb 20180321 bug 35643*/
				hsb.addSysOutsyncFlag("t_hr_view");
			}
			if (hsb.isSync_b01()) {
				CommonData da = new CommonData();
				da.setDataName("单位视图");
				da.setDataValue("2");
				typelist.add(da);
				hsb.creatOrgTable("t_org_view");
				/*单位视图表不存在创建并初始化,缺少外部系统字段,添加对应外部系统字段      wangb 20180321 bug 35643*/
				hsb.addSysOutsyncFlag("t_org_view");
			}
			if (hsb.isSync_k01()) {
				CommonData da = new CommonData();
				da.setDataName("岗位视图");
				da.setDataValue("3");
				typelist.add(da);
				hsb.creatPostTable("t_post_view");
				/*岗位视图表不存在创建并初始化,缺少外部系统字段,添加对应外部系统字段      wangb 20180321 bug 35643*/
				hsb.addSysOutsyncFlag("t_post_view");
			}

			this.getFormHM().put("sync_typelst", typelist);
			if (emporg == null) {

				if (hsb.isSync_a01())
					emporg = "1";
				else if (hsb.isSync_b01())
					emporg = "2";
				else if (hsb.isSync_k01())
					emporg = "3";
				else
					emporg = "1";

			} else {
				if ("1".equals(emporg))
					if (!hsb.isSync_a01())
						emporg = "2";
				if ("2".equals(emporg))
					if (!hsb.isSync_b01())
						emporg = "3";
				if ("3".equals(emporg))
					if (!hsb.isSync_k01())
						emporg = "1";

				/*
				 * if(emporg.equals("1")) if(!hsb.isSync_a01()) throw new
				 * GeneralException("您还未设置同步信息，请先设置同步信息!");
				 * if(emporg.equals("2")) if(!hsb.isSync_b01()) throw new
				 * GeneralException("您还未设置同步信息，请先设置同步信息!");
				 * if(emporg.equals("3")) if(!hsb.isSync_k01()) throw new
				 * GeneralException("您还未设置同步信息，请先设置同步信息!");
				 */
			}
			this.getFormHM().put("sync_mode", sync_mode);
			this.getFormHM().put("emporg", emporg);
			String sysidcolumn = "";
			if("全部".equalsIgnoreCase(tacitly)){
				for (int i = 0; i < sysid.size(); i++) {
					CommonData cd = (CommonData)sysid.get(i);
					if("全部".equals(cd.getDataName()))
						continue;
						sysidcolumn += "," + cd.getDataName();
				}
			}else{
				sysidcolumn += "," + tacitly;
			}
			String where = "";
			if(!"all".equalsIgnoreCase(state)){
				String[] strcolumn = sysidcolumn.split(",");
				for (int i = 0; i < strcolumn.length; i++) {
					if(i==0)continue;
					if(strcolumn.length<3){
						if("0".equals(state)) {
							where += " and (" + strcolumn[i] + "= '" + state + "' and sys_flag<>3)";
						}else if("-1".equals(state)) {
							where += " and (" + strcolumn[i] + "= '0' and sys_flag =3)";
						}else {
							where += " and " + strcolumn[i] + "= '" + state + "'";
						}
					}else{
						if(i==1) {
							if("0".equals(state)) {
								where += " and ((" + strcolumn[i] + "= '" + state + "' and sys_flag<>3)";
							}else if("-1".equals(state)) {
								where += " and ((" + strcolumn[i] + "= '0' and sys_flag =3)";
							}else {
								where += " and (" + strcolumn[i] + "= '" + state + "'";
							}
						}else if(i==strcolumn.length-1) {
							if("0".equals(state)) {
								where += " or (" + strcolumn[i] + "= '" + state + "' and sys_flag<>3))";
							}else if("-1".equals(state)) {
								where += " or (" + strcolumn[i] + "= '0' and sys_flag =3))";
							}else {
								where += " or " + strcolumn[i] + "= '" + state + "')";
							}
						}else {
							if("0".equals(state)) {
								where += " or (" + strcolumn[i] + "= '" + state + "' and sys_flag<>3)";
							}else if("-1".equals(state)) {
								where += " or (" + strcolumn[i] + "= '0' and sys_flag =3)";
							}else {
								where += " or " + strcolumn[i] + "= '" + state + "'";
							}
						}
					}
				}
			}
			if ("1".equalsIgnoreCase(emporg)) {
				// hsb.creatHrTable();
				// 将人员数据导入新创建的表
				String dbtemp = hsb.getTextValue(HrSyncBo.BASE);
				ArrayList dbnamelist = this.getDBName(dbtemp);
				this.getFormHM().put("dbnamelist", dbnamelist);
				StringBuffer sql = new StringBuffer();
				// String columns =
				// hsb.getColumn().replaceAll("b0110","B0110_0").replaceAll("e01a1","E01a1_0").replaceAll("e0122","E0122_0");
				String columns = hsb.getColumn();
				String column = "";
				if (sync_mode != null && "trigger".equalsIgnoreCase(sync_mode))// 增加一个系统项sys_flag
				{
					sql.append("select a0000,username,unique_id,nbase,a0101,B0110_0,E01a1_0,E0122_0,sys_flag" + columns + sysidcolumn);//添加本系统状态字段 sys_flag 20171117 wangb
					column = "username,nbase,unique_id,a0101,sys_flag" + columns + sysidcolumn;//添加本系统状态字段 sys_flag 20171117 wangb
				} else {				
					sql.append("select a0000,username,unique_id,nbase,a0101,B0110_0,E01a1_0,E0122_0" + columns + ",sys_flag,flag" + sysidcolumn);//添加本系统状态字段 sys_flag 20171117 wangb
					column = "username,nbase,unique_id,a0101" + columns + ",sys_flag,flag" + sysidcolumn;//添加本系统状态字段 sys_flag 20171117 wangb
				}
				String selectsql = sql.toString();
				StringBuffer wheresql = new StringBuffer();
				wheresql.append(" from t_hr_view where 1 = 1");
				wheresql.append(where);
				// 过滤机构范围
				if (!(a_code == null || "".equals(a_code))) {
					String codesetid = a_code.substring(0, 2);
					String value = a_code.substring(2);
					if (!(value == null || "".equals(value.trim()))) {
						if ("UN".equalsIgnoreCase(codesetid)) {
							wheresql.append(" and B0110_0 like '");
							wheresql.append(value);
							wheresql.append("%'");
						}
						if ("UM".equalsIgnoreCase(codesetid)) {
							wheresql.append(" and E0122_0 like '");
							wheresql.append(value);
							wheresql.append("%'");
						}
					}
				}
				// 过滤人员库
				String dbname = (String) this.getFormHM().get("dbname");
				boolean b = false;
				for (int i = 0; i < dbnamelist.size(); i++) {
					CommonData cd = (CommonData) dbnamelist.get(i);
					if (cd.getDataValue().equalsIgnoreCase(dbname))
						b = true;
				}
				if (!b)
					dbname = "Usr";
				if (!(dbname == null || "".equals(dbname))) {
					wheresql.append(" and Upper(nbase_0) = '"
							+ dbname.toUpperCase() + "'");
				} else {
					wheresql.append(" and Upper(nbase_0) = 'USR'");
				}

				// 过滤姓名
				if (select_name != null && select_name.trim().length() > 0) {

					select_name = PubFunc.getStr(select_name);
					// String whereA0101 = whereA0101NoPriv(this.userView, this
					// .getFrameconn(), dbname, select_name, "0");
					String whereA0101 = "A0101 LIKE '%" + select_name + "%'";
					if (whereA0101 != null && whereA0101.length() > 0)
						wheresql.append(" AND " + whereA0101);
				}

				/** 视图定义指标 */
				ArrayList fieldslist = new ArrayList();

				/** 固定指标,人员类别及用户名 */
				FieldItem field = null;
				if("全部".equalsIgnoreCase(tacitly)){
					
//					if (sync_mode != null && sync_mode.equalsIgnoreCase("trigger"))// 增加一个系统项sys_flag
//					{
						for (int i=0;i<sysid.size();i++) {
							CommonData cd = (CommonData) sysid.get(i);
							if("全部".equals(cd.getDataName()))
								continue;
							field = new FieldItem(cd.getDataName(), cd.getDataName());
							field.setItemtype("A");
							field.setItemdesc(cd.getDataName());
							field.setItemlength(1);
							field.setCodesetid("0");
							fieldslist.add(field);
						}
//					} else {
//						field = new FieldItem("flag", "flag");
//						field.setItemtype("A");
//						field.setItemdesc("标志");
//						field.setItemlength(1);
//						field.setCodesetid("0");
//						fieldslist.add(field);
//					}
				}else{
					field = new FieldItem(tacitly, tacitly);
					field.setItemtype("A");
					field.setItemdesc(tacitly);
					field.setItemlength(1);
					field.setCodesetid("0");
					fieldslist.add(field);
				}
				/** 人员视图 页面 添加唯一标识列 wangb 20170707 */
//				field = new FieldItem("unique_id", "unique_id");
//				field.setItemtype("A");
//				field.setItemdesc("唯一标识");
//				field.setItemlength(50);
//				field.setCodesetid("0");
//				fieldslist.add(field);
				
				field = new FieldItem("nbase", "nbase");
				field.setItemtype("A");
				field.setItemdesc("人员类别");
				field.setItemlength(50);
				field.setCodesetid("0");
				fieldslist.add(field);

				field = new FieldItem("a0101", "a0101");
				field.setItemtype("A");
				field.setItemdesc("姓名");
				field.setItemlength(40);
				field.setCodesetid("0");
				fieldslist.add(field);

				field = new FieldItem("username", "username");
				field.setItemtype("A");
				field.setItemdesc("用户名");
				field.setItemlength(50);
				field.setCodesetid("0");
				fieldslist.add(field);
				
				ArrayList fieldslists = hsb.getCustomFieldsList();
				fieldslist.addAll(fieldslists);
				
				

				this.getFormHM().put("tacitly", tacitly);
				this.getFormHM().put("dbname", dbname);
				this.getFormHM().put("selectsql", selectsql);
				this.getFormHM().put("wheresql", wheresql.toString());
				this.getFormHM().put("order", "order by a0000");
				this.getFormHM().put("column", column);
				this.getFormHM().put("fieldslist", fieldslist);
				this.getFormHM().put("sync_mode", sync_mode);
			} else if ("2".equalsIgnoreCase(emporg)) {
				StringBuffer sql = new StringBuffer();
				String columns = hsb.getOrgColumn();
				String column = "";
				if (sync_mode != null && "trigger".equalsIgnoreCase(sync_mode))// 增加一个系统项sys_flag
				{
				
				sql.append("select b0110_0,unique_id,codesetid,parentid,parentdesc,grade,corcode,sys_flag"//添加本系统状态字段 sys_flag 20171117 wangb
						+ columns + sysidcolumn);
				column = "b0110_0,unique_id,codesetid,corcode,sys_flag"//添加本系统状态字段 sys_flag 20171117 wangb
						+ columns + sysidcolumn;
				} else {
					sql.append("select b0110_0,unique_id,codesetid,parentid,parentdesc,grade,corcode,sys_flag"//添加本系统状态字段 sys_flag 20171117 wangb
							+ columns + ",flag"+ sysidcolumn);
					column = "b0110_0,unique_id,codesetid,corcode,sys_flag"//添加本系统状态字段 sys_flag 20171117 wangb
							+ columns +",flag"+ sysidcolumn;
				}
				String selectsql = sql.toString();
				StringBuffer wheresql = new StringBuffer();
				wheresql.append(" from t_org_view where 1 = 1");
				wheresql.append(where);
				// 过滤机构范围
				if (!(a_code == null || "".equals(a_code))) {
					String codesetid = a_code.substring(0, 2);
					String value = a_code.substring(2);
					wheresql.append(" and B0110_0 like '");
					wheresql.append(value);
					wheresql.append("%'");
					/*
					 * if(!(value==null || value.trim().equals(""))) {
					 * if(codesetid.equalsIgnoreCase("UN")) { wheresql.append("
					 * and B0110_0 like '"); wheresql.append(value);
					 * wheresql.append("%'"); }
					 * if(codesetid.equalsIgnoreCase("UM")) { wheresql.append("
					 * and E0122_0 like '"); wheresql.append(value);
					 * wheresql.append("%'"); } }
					 */
				}
				ArrayList fieldslist = new ArrayList();
				FieldItem field = new FieldItem();
				if("全部".equalsIgnoreCase(tacitly)){
//					if (sync_mode != null && sync_mode.equalsIgnoreCase("trigger"))// 增加一个系统项sys_flag
//					{
					for (int i=0;i<sysid.size();i++) {
						CommonData cd = (CommonData) sysid.get(i);
						if("全部".equals(cd.getDataName()))
							continue;
							field = new FieldItem(cd.getDataName(), cd.getDataName());
							field.setItemtype("A");
							field.setItemdesc(cd.getDataName());
							field.setItemlength(1);
							field.setCodesetid("0");
							fieldslist.add(field);
					}
//					} else {
//						field = new FieldItem("flag", "flag");
//						field.setItemtype("A");
//						field.setItemdesc("标志");
//						field.setItemlength(1);
//						field.setCodesetid("0");
//						fieldslist.add(field);
//					}
				}else{
					field = new FieldItem(tacitly,tacitly);
					field.setItemtype("A");
					field.setItemdesc(tacitly);
					field.setItemlength(1);
					field.setCodesetid("0");
					fieldslist.add(field);
				}
				/** 单位视图 页面 添加唯一标识列 wangb 20170707 */
//				field = new FieldItem("unique_id", "unique_id");
//				field.setItemtype("A");
//				field.setItemdesc("唯一标识");
//				field.setItemlength(50);
//				field.setCodesetid("0");
//				fieldslist.add(field);
				
				field = new FieldItem("b0110_0", "b0110_0");
				field.setItemtype("A");
				field.setItemdesc("单位编码");
				field.setItemlength(50);
				field.setCodesetid("UN");
				fieldslist.add(field);
				field = new FieldItem("corcode", "corcode");
				field.setItemtype("A");
				field.setItemdesc("转换代码");
				field.setItemlength(50);
				field.setCodesetid("");
				fieldslist.add(field);
				ArrayList fieldslists = hsb.getOrgCustomFieldsList();
				fieldslist.addAll(fieldslists);

				

				this.getFormHM().put("selectsql", selectsql);
				this.getFormHM().put("wheresql", wheresql.toString());
				this.getFormHM().put("column", column);
				this.getFormHM().put("order", "order by b0110_0");
				this.getFormHM().put("fieldslist", fieldslist);
				this.getFormHM().put("sync_mode", sync_mode);
			} else if ("3".equalsIgnoreCase(emporg)) {
				StringBuffer sql = new StringBuffer();
				String columns = hsb.getPostColumn();
				String column = "";
				if (sync_mode != null && "trigger".equalsIgnoreCase(sync_mode))// 增加一个系统项sys_flag
				{
					sql.append("select e0122_0,unique_id,e01a1_0,codesetid,parentid,parentdesc,grade,corcode,sys_flag"//添加本系统状态字段 sys_flag 20171117 wangb
							+ columns + sysidcolumn);
					column = "e0122_0,unique_id,e01a1_0,corcode,codesetid,sys_flag"//添加本系统状态字段 sys_flag 20171117 wangb
							+ columns + sysidcolumn;
				} else {
					sql.append("select e0122_0,unique_id,e01a1_0,codesetid,parentid,parentdesc,grade,corcode,sys_flag"//添加本系统状态字段 sys_flag 20171117 wangb
							+ columns + ",flag" + sysidcolumn);
					column = "e0122_0,unique_id,e01a1_0,corcode,codesetid,sys_flag"//添加本系统状态字段 sys_flag 20171117 wangb
							+ columns +",flag" + sysidcolumn;
				}
				String selectsql = sql.toString();
				StringBuffer wheresql = new StringBuffer();
				wheresql.append(" from t_post_view where 1 = 1");
				wheresql.append(where);
				// 过滤机构范围
				if (!(a_code == null || "".equals(a_code))) {
					String codesetid = a_code.substring(0, 2);
					String value = a_code.substring(2);
					wheresql.append(" and e01a1_0 like '");
					wheresql.append(value);
					wheresql.append("%'");
					/*
					 * if(!(value==null || value.trim().equals(""))) {
					 * if(codesetid.equalsIgnoreCase("UN")) { wheresql.append("
					 * and B0110_0 like '"); wheresql.append(value);
					 * wheresql.append("%'"); }
					 * if(codesetid.equalsIgnoreCase("UM")) { wheresql.append("
					 * and E0122_0 like '"); wheresql.append(value);
					 * wheresql.append("%'"); } }
					 */
				}

				ArrayList fieldslist = new ArrayList();
				FieldItem field = null;
				if("全部".equalsIgnoreCase(tacitly)){
//					if (sync_mode != null && sync_mode.equalsIgnoreCase("trigger")){// 增加一个系统项sys_flag
//					for (int i=0;i<sysid.size();i++) {
//						CommonData cd = (CommonData) sysid.get(i);
//						if(cd.getDataName().equals("全部"))
//							continue;
//						if (sync_mode != null && !sync_mode.equals("trigger")) {
//							String webserver = SystemConfig.getPropertyValue("webserver");
//							if (webserver != null && webserver.equalsIgnoreCase("old")) {
//								field = new FieldItem(cd.getDataName(), cd.getDataName());
//								field.setItemtype("A");
//								field.setItemdesc(cd.getDataName());
//								field.setItemlength(1);
//								field.setCodesetid("0");
//								fieldslist.add(field);
//							}
//						} else {
//							field = new FieldItem(cd.getDataName(), cd.getDataName());
//							field.setItemtype("A");
//							field.setItemdesc(cd.getDataName());
//							field.setItemlength(1);
//							field.setCodesetid("0");
//							fieldslist.add(field);
//						}
//					}
					
					
					for (int i=0;i<sysid.size();i++) {
						CommonData cd = (CommonData) sysid.get(i);
						if("全部".equals(cd.getDataName()))
							continue;
							field = new FieldItem(cd.getDataName(), cd.getDataName());
							field.setItemtype("A");
							field.setItemdesc(cd.getDataName());
							field.setItemlength(1);
							field.setCodesetid("0");
							fieldslist.add(field);
					}
					
					
					
//					} else {
//						field = new FieldItem("flag", "flag");
//						field.setItemtype("A");
//						field.setItemdesc("标志");
//						field.setItemlength(1);
//						field.setCodesetid("0");
//						fieldslist.add(field);
//					}
				}else{
					field = new FieldItem(tacitly,tacitly);
					field.setItemtype("A");
					field.setItemdesc(tacitly);
					field.setItemlength(1);
					field.setCodesetid("0");
					fieldslist.add(field);
				}
				/** 岗位视图 页面 添加唯一标识列 wangb 20170707 */
//				field = new FieldItem("unique_id", "unique_id");
//				field.setItemtype("A");
//				field.setItemdesc("唯一标识");
//				field.setItemlength(50);
//				field.setCodesetid("0");
//				fieldslist.add(field);
				
				field = new FieldItem("e0122_0", "e0122_0");
				field.setItemtype("A");
				field.setItemdesc("部门编码");
				field.setItemlength(50);
				field.setCodesetid("UM");
				fieldslist.add(field);

				field = new FieldItem("e01a1_0", "e01a1_0");
				field.setItemtype("A");
				field.setItemdesc("岗位编码");
				field.setItemlength(50);
				field.setCodesetid("@K");
				fieldslist.add(field);
				field = new FieldItem("corcode", "corcode");
				field.setItemtype("A");
				field.setItemdesc("转换代码");
				field.setItemlength(50);
				field.setCodesetid("");
				fieldslist.add(field);
				ArrayList fieldslists = hsb.getPostCustomFieldsList();

				fieldslist.addAll(fieldslists);
				
				this.getFormHM().put("selectsql", selectsql);
				this.getFormHM().put("wheresql", wheresql.toString());
				this.getFormHM().put("column", column);
				this.getFormHM().put("order", "order by b0110_0");
				this.getFormHM().put("fieldslist", fieldslist);
				this.getFormHM().put("sync_mode", sync_mode);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
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

	public ArrayList getDBName(String dbstr) {
		ArrayList retlist = new ArrayList();
		if (dbstr == null || "".equals(dbstr)) {
			// dbstr = "usr";
			// String datavalue = AdminCode.getCodeName("@@",dbstr);
			// CommonData cd = new CommonData(dbstr,datavalue);
			CommonData cd = new CommonData("", "");
			retlist.add(cd);
		} else {
			String[] db = dbstr.split(",");
			for (int i = 0; i < db.length; i++) {
				if (!(db[i] == null || "".equals(db[i]))) {
					String datavalue = AdminCode.getCodeName("@@", db[i]);
					CommonData cd = new CommonData(db[i], datavalue);
					retlist.add(cd);
				}
			}
		}
		return retlist;
	}

	public boolean checkRecord(ContentDAO dao) {
		boolean ret = false;
		String sql = "select * from t_hr_view";
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				ret = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public boolean insertRecord(ContentDAO dao, ArrayList dblist) {
		boolean ret = false;
		StringBuffer sql = new StringBuffer();
		String dbname = "";
		try {
			for (int i = 0; i < dblist.size(); i++) {
				dbname = (String) dblist.get(i);
				String hz_dbname = AdminCode.getCodeName("@@", dbname);
				sql.append("insert into t_hr_view");
				sql.append("(nbase,nbase_0,A0100,");
				sql.append("B0110_0,E0122_0,A0101,E01A1_0,");
				sql.append("username,userpassword,flag)");
				sql.append("( select '" + hz_dbname + "','" + dbname + "',");
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

	/**
	 * 
	 * @param userView
	 * @param conn
	 * @param userbase
	 * @param a0101value
	 * @param querylike
	 * @return
	 * @throws GeneralException
	 */
	public String whereA0101NoPriv(UserView userView, Connection conn,
			String userbase, String a0101value, String querylike)
			throws GeneralException {
		if (a0101value == null || a0101value.length() <= 0)
			return "";
		String select_name = PubFunc.getStr(a0101value);
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
		String pinyin_field = sysbo
				.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		String strwhere = "";
		boolean bresult = true;
		boolean blike = false;
		StringBuffer strfactor = new StringBuffer();
		if (a0101value.indexOf("*") != -1) {
			strfactor.append("A0101=" + a0101value + "`");
			querylike = "0";
		} else {
			strfactor.append("A0101=*" + a0101value + "*`");
		}

		if (!(pinyin_field == null || "".equals(pinyin_field) || "#"
				.equals(pinyin_field))) {
			String pinyinstr = PubFunc.getPinyinCode(select_name.trim());
			if (querylike != null && "1".equals(querylike)) {
				strwhere = " (" + userbase + "A01.a0101 like '%"
						+ select_name.trim() + "%' or " + userbase + "A01."
						+ pinyin_field + " like '" + pinyinstr + "%' ) ";
			} else {
				strfactor.append(pinyin_field + "=" + pinyinstr + "*`");
				FactorList factorlist = new FactorList("1+2",
						strfactor.toString(), userbase, false, blike, bresult,
						1, userView.getUserId());
				strwhere = factorlist.getSqlExpression();

			}

		} else {
			if (querylike != null && "1".equals(querylike)) {
				strwhere = " " + userbase + "A01.a0101 like '%"
						+ select_name.trim() + "%'";
			} else {
				FactorList factorlist = new FactorList("1",
						strfactor.toString(), userbase, false, blike, bresult,
						1, userView.getUserId());
				strwhere = factorlist.getSqlExpression();
			}

		}
		if (strwhere != null && strwhere.indexOf("WHERE") != -1) {
			// strwhere = strwhere.substring(strwhere.indexOf("WHERE") + 5);
			strwhere = "t_hr_view.A0101 IN (SELECT A0101" + strwhere + ")";
		}
		return strwhere;
	}

	/**
	 * 查询已启用的系统
	 * <p>Create Time:2012-11-10 下午1:29:18</p>
	 * <p>@author:jianc</p>
	 */
	private ArrayList selectSysid() {
		String sql = "select sys_id from t_sys_outsync where state != 0";
		ArrayList sysid = new ArrayList();
		CommonData cda = new CommonData();
		cda.setDataName("全部");
		cda.setDataValue("全部");
		sysid.add(cda);
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				String str = this.frowset.getString("sys_id");
				CommonData cd = new CommonData();
				cd.setDataName(str);
				cd.setDataValue(str);
				sysid.add(cd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sysid;
	}
	/**
	 * 创建可选状态
	 * <p>Create Time:2012-11-10 下午1:31:11</p>
	 * <p>@author:jianc</p>
	 */
	private ArrayList createState(){
		ArrayList statelist = new ArrayList();
		CommonData cd1 = new CommonData("all","全部");
		CommonData cd2 = new CommonData("1","新增");
		CommonData cd3 = new CommonData("2","修改");
		CommonData cd4 = new CommonData("3","删除");
		CommonData cd5 = new CommonData("0","已同步");
		CommonData cd6 = new CommonData("-1","删除已同步");
		statelist.add(cd1);
		statelist.add(cd2);
		statelist.add(cd3);
		statelist.add(cd4);
		statelist.add(cd5);
		statelist.add(cd6);
		
		return statelist;
	}
	/**
	 * xus 18/3/15
	 * @param sysid
	 * @return
	 */
	private String selectUrl(String sysid){
		String url="";
		String sql = "select url from t_sys_outsync where sys_id = ? ";
		ArrayList values=new ArrayList();
		values.add(sysid);
		ContentDAO dao=new ContentDAO (this.getFrameconn());
		try {
			this.frowset=dao.search(sql,values);
			
			if (this.frowset.next()) {
				url = this.frowset.getString("url");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return url;
	}
}
