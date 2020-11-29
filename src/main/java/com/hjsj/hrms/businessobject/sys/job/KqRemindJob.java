package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.param.DocumentSyncBo;
import com.hjsj.hrms.businessobject.param.DocumentSyncXML;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title:KqRemindJob
 * </p>
 * <p>
 * Description:将人力系统中的加班、公出、请假已报批的数据放到核二三oa数据库中
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-04-20
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class KqRemindJob implements Job {

	@Override
    public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Connection conn  = null;
		Connection oaConn = null;
		try {
			conn = AdminDb.getConnection();
			
			// 创建临时表
			this.createTempTable(conn);
			this.createTempResult(conn);
			
			// 插入数据到临时表
			insertIntoTemp(conn, "q11");
			insertIntoTemp(conn, "q13");
			insertIntoTemp(conn, "q15");
			// 更新发送人的员工号
			updateTemp(conn);
			// 更新上级职位
			updateTempParent(conn);
			// 更新接收人的信息及内容
			updateTempRe(conn);
			// 获得设置的oa数据库连接
			DocumentSyncBo bo = new DocumentSyncBo(conn);
			String xml = bo.getConnXML();
			// 解析xml
			DocumentSyncXML docuXml = new DocumentSyncXML(conn, xml);
			List list = docuXml.getBeanList("/datasources/datasource");
			if (list != null && list.size() > 0) {
				LazyDynaBean bean = (LazyDynaBean) list.get(0);
				oaConn = bo.getConn(bean);
				// 同步数据
				syncData(oaConn, conn);
			} else {
				Category.getInstance("com.hrms.frame.dao.ContentDAO").error("未设置oa数据库连接或未获得！！");
			}
			
			
			// 删除临时表
//			this.dropTempTable("tmp_he23_kq_to_oa_data", conn);
			this.dropTempTable("t#_kq_he23_to_oa_data", conn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (oaConn != null) {
					oaConn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param oaConn
	 * @param conn
	 */
	private void syncData(Connection oaConn, Connection conn) {
		// 临时表
		String temp = "t#_kq_he23_to_oa_data";
		// 结果表
		String result = "tmp_he23_kq_to_oa_result";
		// 目标表
		String target = "ssInsinf";
		
		ContentDAO dao = new ContentDAO(conn);
		ContentDAO oaDao = new ContentDAO(oaConn);
		// 查询临时表数据
		StringBuffer sql = new StringBuffer();
		sql.append("select sendempid,a0101,receiveEmpId,");
		sql.append("content,readed,sdate,type,q_id,sdate from ");
		sql.append(temp);
		
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			while (rs.next()) {
				sql.delete(0, sql.length());
				sql.append("insert into ");
				sql.append(target);
				sql.append("(sendempid,sendname,receiveEmpId,");
				sql.append("content,readed,sdate) values(?,?,?,?,?,?)");
				
				String sendempid = rs.getString("sendempid");
				sendempid = sendempid == null ? "" : sendempid;
				String a0101 = rs.getString("a0101");
				a0101 = a0101 == null ? "" : a0101;
				String receiveEmpId = rs.getString("receiveEmpId");
				receiveEmpId = receiveEmpId == null ? "" : receiveEmpId;
				String content = rs.getString("content");
				content = content == null ? "" : content;
				String readed = rs.getString("readed");
				readed = readed == null ? "0" : readed;
				String sdate = rs.getString("sdate");
				sdate = sdate == null ? DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") : sdate;
				String type = rs.getString("type");
				String q_id = rs.getString("q_id");
				String sendTime = rs.getString("sdate");
			
				ArrayList list = new ArrayList();
				list.add(sendempid);
				list.add(a0101);
				list.add(receiveEmpId);
				list.add(content);
				list.add(readed);
				list.add(sdate);
				
				RecordVo vo  = new RecordVo(result);
				vo.setString("q_type", type);
				vo.setString("q_id", q_id);
				vo.setDate("sendtime", sendTime);
				try {
					// 将数据保存到目标表 中
					oaDao.insert(sql.toString(), list);
					vo.setString("q_flag", "1");
				} catch (Exception e) {
					e.printStackTrace();
					vo.setString("q_flag", "-1");
				}
				// 保存到结果表中
				dao.addValueObject(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error("同步失败！！");
		} finally {
			// 关闭结果集
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
		
	}
	/**
	 * 将需要的数据保存到临时表中
	 * @param conn Connection 数据库连接
	 * @param tableName String 表名，q11, q13,q15
	 */
	private void insertIntoTemp(Connection conn, String tableName) {
		String temp = "t#_kq_he23_to_oa_data";
		// 结果表
		String result = "tmp_he23_kq_to_oa_result";
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ");
		sql.append(temp);
		sql.append("(type,q_id,nbase,a0100,a0101,e01a1,");
		sql.append("Apptime,sdate,readed) select ");
		sql.append(Sql_switcher.substr("'"+tableName+"'", "2", "2"));
		sql.append(" type,");
		sql.append(tableName);
		sql.append("01 q_id,nbase,a0100,a0101,e01a1,");
		sql.append(tableName);		
		sql.append("05 Apptime,");
		sql.append(Sql_switcher.sqlNow());
		sql.append(" sdate,0 readed from ");
		sql.append(tableName);
		sql.append(" q where (");
		sql.append(tableName);
		sql.append("z5='02' or ");
		sql.append(tableName);
		sql.append("z5='08') and not exists(select 1 from ");
		sql.append(result);
		sql.append(" r where q.");		
		sql.append(tableName);
		sql.append("01=r.q_id");
		sql.append(" and r.q_type =");
		sql.append(Sql_switcher.substr("'"+tableName+"'", "2", "2"));
		sql.append(")");
		
		try {
			ContentDAO dao = new ContentDAO(conn);
			
			dao.insert(sql.toString(), new ArrayList());
		} catch (Exception e) {
			e.printStackTrace();
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error("向t#_kq_he23_to_oa_data表插入申请数据时错误！");
		}
		
	}
	
	/**
	 * 更新发送人的id
	 * @param conn
	 */
	private void updateTemp(Connection conn) {
		String temp = "t#_kq_he23_to_oa_data";
		
		ContentDAO dao = new ContentDAO(conn);
		List list = getNbaseList(conn);
		// oa员工号字段
		String field = this.getField(conn);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				try {
					String nbase = (String) list.get(i);
					String destTab = temp;//目标表
					String srcTab = nbase + "A01";//源表
					String strJoin = destTab+".a0100="+srcTab+".a0100 and upper("+destTab + ".nbase)='"+nbase.toUpperCase()+"'";//关联串  xxx.field_name=yyyy.field_namex,....
					//更新串  xxx.field_name=yyyy.field_namex,....
					StringBuffer strSet = new StringBuffer();
					strSet.append(destTab+".sendempid="+srcTab+"."+field);
					String strDWhere="";//更新目标的表过滤条件
					String strSWhere= Sql_switcher.isnull(srcTab+"."+field, "'0'") + "<> '0'";//源表的过滤条件  
					String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet.toString(),strDWhere,strSWhere);
					update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
					dao.update(update);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 更新上级职位
	 * @param conn
	 */
	private void updateTempParent(Connection conn) {
		String temp = "t#_kq_he23_to_oa_data";
		String kField = getKField(conn);
		ContentDAO dao = new ContentDAO(conn);
		try {
			String destTab = temp;//目标表
			String srcTab = "K01";//源表
			String strJoin = destTab+".e01a1="+srcTab+".e01a1";//关联串  xxx.field_name=yyyy.field_namex,....
			//更新串  xxx.field_name=yyyy.field_namex,....
			StringBuffer strSet = new StringBuffer();
			strSet.append(destTab+".parent="+srcTab+"."+kField);
			String strDWhere="";//更新目标的表过滤条件
			String strSWhere= Sql_switcher.isnull(srcTab+"."+kField, "'0'") + "<> '0'";//源表的过滤条件  
			String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet.toString(),strDWhere,strSWhere);
			update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
			dao.update(update);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新接收人的信息及提示内容
	 * @param conn
	 */
	private void updateTempRe(Connection conn) {
		String temp = "t#_kq_he23_to_oa_data";
		String field = getField(conn);
		DbNameBo dbbo = new DbNameBo(conn);
		String username = dbbo.getLogonUserNameField();
		String password = dbbo.getLogonPassWordField();
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select a0101,type,q_id,parent from ");
			sql.append(temp);
			
			rs = dao.search(sql.toString());
			while (rs.next()) {
				RowSet rs2 = null;
				String parent =  rs.getString("parent");
				String type = rs.getString("type");
				String id = rs.getString("q_id");
				String a0101 = rs.getString("a0101");
				if (parent != null && parent.length() > 0) {
					StringBuffer str = new StringBuffer();
					str.append("select ");
					str.append(username);
					str.append(",");
					str.append(password);
					str.append(",");
					str.append(field);
					str.append(" from usra01 where e01a1 = '");
					str.append(parent);
					str.append("'");
					
					rs2 = dao.search(str.toString());
					if (rs2.next()) {
						String reId = rs2.getString(field);
						if (reId == null) {
							continue;
						}
						String user = rs2.getString(username);
						String pwd = rs2.getString(password);
						
						// 如果密码加密，需要将密码转为明文
						if(ConstantParamter.isEncPwd(conn)) {
							Des des = new Des();
							pwd = des.DecryPwdStr(pwd);
						}
						RecordVo vo  =  new RecordVo(temp);
						vo.setString("type", type);
						vo.setString("q_id", id);
						vo = dao.findByPrimaryKey(vo);
						// 设置接收人id
						vo.setInt("receiveempid", Integer.parseInt(reId));
						// 设置内容
						StringBuffer constent = new StringBuffer();
						constent.append("<a href=\"");
						String serverUrl = SystemConfig.getPropertyValue("hrp_logon_url");
						if (serverUrl == null || serverUrl.length() <= 0) {
							Category.getInstance("com.hrms.frame.dao.ContentDAO").error("system.properties文件中未设置hrp_logon_url！！");
						}
						constent.append(serverUrl);
						
						if ("11".equalsIgnoreCase(type)) {
							constent.append("/kq/app_check_in/all_app.do?");
							constent.append("b_query=link&action=all_app_data.do");
							constent.append("&target=mil_body&table=Q11");
							constent.append("&returnvalue=&appfwd=1&etoken=");
						} else if ("13".equalsIgnoreCase(type)) {
							constent.append("/kq/app_check_in/all_app.do?");
							constent.append("b_query=link&action=all_app_data.do");
							constent.append("&target=mil_body&table=Q13");
							constent.append("&returnvalue=&appfwd=1&etoken=");
						} else if ("15".equalsIgnoreCase(type)) {
							constent.append("/kq/app_check_in/all_app.do?");
							constent.append("b_query=link&action=all_app_data.do");
							constent.append("&target=mil_body&table=Q15");
							constent.append("&returnvalue=&appfwd=1&etoken=");
						}
						// 用户名密码64编码 
						String base = PubFunc.convertTo64Base(user+","+pwd);
						constent.append(PubFunc.convertUrlSpecialCharacter(base));
						constent.append("\">");
						constent.append(a0101);
						constent.append("提交了");
						if ("11".equals(type)) {
							constent.append("加班");
						} else if ("13".equals(type)) {
							constent.append("公出");
						} else if ("15".equals(type)) {
							constent.append("请假");
						}
						constent.append("申请");
						constent.append("</a>");
						vo.setString("content", constent.toString());
						dao.updateValueObject(vo);
					}
				}
				if (rs2 != null) {
					rs2.close();
				}
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			Category.getInstance("com.hrms.frame.dao.ContentDAO").error("更新表t#_kq_he23_to_oa_data的content、receiveEmpId字段时出错！！");
		} finally {
			try {
			if (rs != null) {
				rs.close();
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获得oa员工号字段
	 * @param conn
	 * @return
	 */
	private String getField(Connection conn) {
		String field = "";
		StringBuffer sql = new StringBuffer();
		sql.append("select itemid from fielditem ");
		sql.append("where fieldsetid='A01' and upper(itemdesc)='OA员工号'");
		
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());
			if (rs.next()) {
				field = rs.getString("itemid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return field;
	}
	/**
	 * 获得人员库列表
	 * @return List<String> 人员库列表
	 */
	private ArrayList getNbaseList(Connection conn) {
		ArrayList list = new ArrayList();
		String sql = "select * from dbname";
		ResultSet rs = null;
		ContentDAO dao = new ContentDAO(conn);
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				list.add(rs.getString("pre"));
			}
		} catch (Exception e) {
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
		
		return list;
	}
	
	/**
	 * 创建结果表，将发送过的数据保存到该表中
	 * @param conn Connection 数据库连接
	 */
	private void createTempResult(Connection conn) {
		String tableName = "tmp_he23_kq_to_oa_result";
		DbWizard zard = new DbWizard(conn);
		if (!zard.isExistTable(tableName, false)) {
			try {
				Table table = new Table(tableName);
				// 申请类型
				Field temp = this.createField("Q_type", "申请类型", false, true, DataType.STRING, 3);
				table.addField(temp);
						
				//Id
				temp  = this.createField("Q_id", "id", false, true, DataType.STRING, 20);
				table.addField(temp);
				
				// 发送时间
				temp = this.createField("sendtime", "发送时间", true, false, DataType.DATETIME, 0);
				table.addField(temp);
				
				//发送状态
				temp = this.createField("Q_flag", "发送状态", true, false, DataType.STRING, 20);
				table.addField(temp);
				
				zard.createTable(table);
			} catch (Exception e) {
				e.printStackTrace();
				Category.getInstance("com.hrms.frame.dao.ContentDAO").error("创建结果表tmp_he23_kq_to_oa_result时失败！！");
			}
			
		}
		
		// 重新加载t#_kq_he23_to_oa_data表，以便使用RecordVo
		DBMetaModel model = new DBMetaModel(conn);
		model.reloadTableModel(tableName);

	}
	
	/**
	 * 数据库连接
	 * @param conn
	 */
	private void createTempTable(Connection conn) {
		String tableName = "t#_kq_he23_to_oa_data";

		DbWizard zard = new DbWizard(conn);

		// 创建表
		Table table = new Table(tableName);
		if (zard.isExistTable(tableName, false)) {
			// 清空表
			clearTemp(tableName, conn);
		} else {
			try {
				// 申请类型
				Field temp = this.createField("Type", "申请类型", false, true,
						DataType.STRING, 255);
				table.addField(temp);
				
				// 申请id
				temp = this.createField("Q_Id", "申请id", false, true,
						DataType.STRING, 20);
				table.addField(temp);
				
				// 人员库
				temp = this.createField("nbase", "人员库", true, false,
						DataType.STRING, 3);
				table.addField(temp);
				
				// 发送人姓名
				temp = this.createField("A0101", "发送人姓名", true, false,
						DataType.STRING, 20);
				table.addField(temp);
				
				// 人员编号
				temp = this.createField("A0100", "人员编号", true, false,
						DataType.STRING, 8);
				table.addField(temp);
				
				// 发送人员工号
				temp = this.createField("sendempid", "发送人员工号", true, false,
						DataType.INT, 0);
				table.addField(temp);
				
				// 接收人oa系统员工号
				temp = this.createField("receiveEmpId", "接收人oa系统员工号", true, false,
						DataType.INT, 3);
				table.addField(temp);
				// 提示内容
				temp = this.createField("content", "提示内容", true, false,
						DataType.CLOB, 3);
				table.addField(temp);
				// 是否已读
				temp = this.createField("readed", "是否已读", true, false,
						DataType.BOOLEAN, 3);
				table.addField(temp);
	
				// 发送日期	Datetime
				temp = this.createField("sdate", "发送日期", true, false,
						DataType.DATETIME, 3);
				table.addField(temp);
				
				//申请日期	Datetime
				temp = this.createField("Apptime", "申请日期", true, false,
						DataType.DATETIME, 3);
				table.addField(temp);
				
				//职位
				temp = this.createField("e01a1", "职位", true, false,
						DataType.STRING, 30);
				table.addField(temp);
				
				//上级职位
				temp = this.createField("parent", "上级职位", true, false,
						DataType.STRING, 30);
				table.addField(temp);
				
				
				zard.createTable(table);
			} catch (Exception e) {
				e.printStackTrace();
				Category.getInstance("com.hrms.frame.dao.ContentDAO")
				.error("创建临时表t#_kq_he23_to_oa_data时出错");
			} 

		}

		// 重新加载t#_kq_he23_to_oa_data表，以便使用RecordVo
		DBMetaModel model = new DBMetaModel(conn);
		model.reloadTableModel(tableName);
	}

	/**
	 * 创建一个新字段
	 * 
	 * @param fieldName
	 *            String 字段名称
	 * @param desc
	 *            String 字段描述
	 * @param nullAble
	 *            boolean 是否可以为空
	 * @param keyAble
	 *            boolean 是否为主键
	 * @param dataType
	 *            boolean 数据类型，例如DataType.STRING
	 * @param length
	 *            int 长度
	 * @return Field
	 */
	private Field createField(String fieldName, String desc, boolean nullAble,
			boolean keyAble, int dataType, int length) {
		// 创建字段
		Field temp = new Field(fieldName, desc);
		// 是否可以为空
		temp.setNullable(nullAble);
		// 是否是主键
		temp.setKeyable(keyAble);
		// 数据类型
		temp.setDatatype(dataType);
		
		// 长度
		if (dataType == DataType.STRING) {
			temp.setLength(length);
		}

		return temp;
	}

	/**
	 * 清空临时表
	 * 
	 * @param opTable
	 *            String 表名
	 */
	private void clearTemp(String tableName, Connection conn) {
		StringBuffer buff = new StringBuffer();
		buff.append("TRUNCATE table ");
		buff.append(tableName);
		try {
			ContentDAO dao = new ContentDAO(conn);
			dao.update(buff.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除临时表
	 * 
	 * @param opTable
	 *            String 表名
	 */
	private void dropTempTable(String tableName, Connection conn) {
		StringBuffer buff = new StringBuffer();
		buff.append("drop table ");
		buff.append(tableName);
		try {
			ContentDAO dao = new ContentDAO(conn);
			dao.update(buff.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getKField(Connection conn) {
		String field = "";
		String sql = "select str_value from constant where constant='PS_SUPERIOR'";
		ResultSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if (rs.next()) {
				field = rs.getString("str_value");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return field;
	}

}
