package com.hjsj.hrms.businessobject.param;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.machine.SyncCardData;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.*;
import java.util.*;

/**
 * <p>
 * Title:DocumentSyncBo
 * </p>
 * <p>
 * Description:添加考勤同步配制
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-12-22
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class DocumentSyncBo {
	private Category cat = null;
	
	// 数据库连接
	private Connection conn;
	// 表空间
	private String space;
	// 数据库类型
	private String baseType;
	//通知邮箱
	private String mailto;

	public DocumentSyncBo() {
		cat = Category.getInstance(DocumentSyncBo.class);
	}

	public DocumentSyncBo(Connection conn) {
		this.conn = conn;
		cat = Category.getInstance(DocumentSyncBo.class);
	}

	/**
	 * 同步数据
	 * @param start String 开始时间
	 * @param end String 结束时间
	 * @return boolean 是否成功
	 */
	public boolean sync(String start, String end) {
		System.out.println("开始同步...");
		boolean flag = true;
		DocumentSyncXML xml = new DocumentSyncXML(this.conn, getConnXML());
		// 获得数据库信息
		List list = xml.getBeanList("/datasources/datasource");
		// 遍历所有数据库信息，逐一同步
		for (int i = 0; i < list.size(); i++) {
			try {
				LazyDynaBean bean = (LazyDynaBean) list.get(i);
				// 创建临时表
				creatTemptable(this.conn);
				// 按照开始和结束时间同步数据
				syncData(bean,start,end);
				// 删除临时表
				deleteTemptable(this.conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return flag;
	}
	
	/**
	 * 创建临时表
	 * @param conn
	 */
	private void creatTemptable(Connection conn) {
		DbWizard dbWizard=new DbWizard(conn);
		String opts = "Q15,Q13,Q11";
		String[] op = opts.split(",");
		for (int i = 0; i < op.length; i++) {
			try {
			String name = op[i];
			if (dbWizard.isExistTable("t#_OA_TO_HRP_" + name,false)) {
				dbWizard.dropTable("t#_OA_TO_HRP_" + name);
			}
			// 重新创建
			Table table=new Table("t#_OA_TO_HRP_" + name);
			
			//  e0127	关联字段	Varchar(100)
			Field temp=new Field("E0127","关联字段");
			temp.setNullable(true);
			temp.setKeyable(false);
			temp.setDatatype(DataType.STRING);
		    temp.setLength(100);		
			table.addField(temp);
			
			//  Q1301	单号	Varchar(10)
			Field temp1=new Field(name + "01","单号");
			temp1.setNullable(true);
			temp1.setKeyable(false);
			temp1.setDatatype(DataType.STRING);
		    temp1.setLength(10);		
			table.addField(temp1);
		
//			Nbase	人员库	Varchar(3)
			Field temp2=new Field("Nbase","人员库");
			temp2.setNullable(true);
			temp2.setKeyable(false);
			temp2.setDatatype(DataType.STRING);
		    temp2.setLength(3);		
			table.addField(temp2);
			
//			B0110	单位	Varchar(30)
			Field temp3=new Field("B0110","单位");
			temp3.setNullable(true);
			temp3.setKeyable(false);
			temp3.setDatatype(DataType.STRING);
		    temp3.setLength(30);		
			table.addField(temp3);
			
//			E0122	部门	Varchar(30)
			Field temp4=new Field("E0122","部门");
			temp4.setNullable(true);
			temp4.setKeyable(false);
			temp4.setDatatype(DataType.STRING);
		    temp4.setLength(30);		
			table.addField(temp4);
			
//			E01A1	职位	Varchar(30)
			Field temp5=new Field("E01A1","职位");
			temp5.setNullable(true);
			temp5.setKeyable(false);
			temp5.setDatatype(DataType.STRING);
		    temp5.setLength(30);		
			table.addField(temp5);
			
//			A0101	姓名	Varchar(50)
			Field temp6=new Field("A0101","姓名");
			temp6.setNullable(true);
			temp6.setKeyable(false);
			temp6.setDatatype(DataType.STRING);
		    temp6.setLength(50);		
			table.addField(temp6);
			
//			A0100	人员编号	Varchar(8)
			Field temp7=new Field("A0100","人员编号");
			temp7.setNullable(true);
			temp7.setKeyable(false);
			temp7.setDatatype(DataType.STRING);
		    temp7.setLength(8);		
			table.addField(temp7);
			
//			I9999	顺序号	Int
//			Field temp8=new Field("I9999","顺序号");
//			temp8.setNullable(true);
//			temp8.setKeyable(false);
//			temp8.setDatatype(DataType.INT);	
//			table.addField(temp8);
			
//			Q1303	类型	Varchar(30)
			Field temp9=new Field(name + "03","类型");
			temp9.setNullable(true);
			temp9.setKeyable(false);
			temp9.setDatatype(DataType.STRING);
		    temp9.setLength(30);		
			table.addField(temp9);
			
//			Q1305	申请日期	Datetime
			Field temp10=new Field(name + "05","申请日期");
			temp10.setNullable(true);
			temp10.setKeyable(false);
			temp10.setDatatype(DataType.DATETIME);	
			table.addField(temp10);
			
//			Q13Z1	起始时间	Datetime
			Field temp11=new Field(name + "Z1","起始时间");
			temp11.setNullable(true);
			temp11.setKeyable(false);
			temp11.setDatatype(DataType.DATETIME);	
			table.addField(temp11);
			
//			Q13Z3	结束时间	Datetime
			Field temp12=new Field(name + "Z3","结束时间");
			temp12.setNullable(true);
			temp12.setKeyable(false);
			temp12.setDatatype(DataType.DATETIME);	
			table.addField(temp12);
			
//			Q1307	公出事由	Varchar(255)
			Field temp13=new Field(name + "07","事由");
			temp13.setNullable(true);
			temp13.setKeyable(false);
			temp13.setDatatype(DataType.STRING);
		    temp13.setLength(255);		
			table.addField(temp13);
			
//			Q1309	部门领导	Varchar(30)
			Field temp14=new Field(name + "09","部门领导");
			temp14.setNullable(true);
			temp14.setKeyable(false);
			temp14.setDatatype(DataType.STRING);
		    temp14.setLength(30);		
			table.addField(temp14);
			
//			Q1311	部门领导意见	Varchar(100)
			Field temp15=new Field(name + "11","部门领导意见");
			temp15.setNullable(true);
			temp15.setKeyable(false);
			temp15.setDatatype(DataType.STRING);
		    temp15.setLength(100);		
			table.addField(temp15);
			
//			Q1313	单位领导	Varchar(30)
			Field temp16=new Field(name + "13","单位领导");
			temp16.setNullable(true);
			temp16.setKeyable(false);
			temp16.setDatatype(DataType.STRING);
		    temp16.setLength(30);		
			table.addField(temp16);
			
//			Q1315	单位领导意见	Varchar(100)
			Field temp17=new Field(name + "15","单位领导意见");
			temp17.setNullable(true);
			temp17.setKeyable(false);
			temp17.setDatatype(DataType.STRING);
		    temp17.setLength(100);		
			table.addField(temp17);
			
//			Q13Z0	审批结果	Varchar(2)
			Field temp18=new Field(name + "Z0","审批结果");
			temp18.setNullable(true);
			temp18.setKeyable(false);
			temp18.setDatatype(DataType.STRING);
		    temp18.setLength(2);		
			table.addField(temp18);
			
//			Q13Z5	审批状态	Varhcar(2)
			Field temp19=new Field(name + "Z5","审批状态");
			temp19.setNullable(true);
			temp19.setKeyable(false);
			temp19.setDatatype(DataType.STRING);
		    temp19.setLength(2);		
			table.addField(temp19);
			
//			Q1304	参考班次	int
			Field temp20=new Field(name + "04","参考班次");
			temp20.setNullable(true);
			temp20.setKeyable(false);
			temp20.setDatatype(DataType.INT);	
			table.addField(temp20);
			
//			Q13Z7	审批时间	Datetime
			Field temp21=new Field(name + "Z7","审批时间");
			temp21.setNullable(true);
			temp21.setKeyable(false);
			temp21.setDatatype(DataType.DATETIME);	
			table.addField(temp21);
			
//			state	状态	Varchar(2)
			Field temp22=new Field("state","状态");
			temp22.setNullable(true);
			temp22.setKeyable(false);
			temp22.setDatatype(DataType.STRING);
		    temp22.setLength(2);		
			table.addField(temp22);


			dbWizard.createTable(table);
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除临时表
	 * @param conn
	 */
	private void deleteTemptable(Connection conn) {
		DbWizard dbWizard=new DbWizard(conn);
		String opts = "Q15,Q13,Q11";
		String[] op = opts.split(",");
		for (int i = 0; i < op.length; i++) {
			try {
				String name = op[i];
				if (dbWizard.isExistTable("t#_OA_TO_HRP_" + name, false)) {
					dbWizard.dropTable("t#_OA_TO_HRP_" + name);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 按照开始和结束时间同步一个数据库的数据
	 * @param bean LazyDynaBean 数据库信息
	 * @param start String 开始时间
	 * @param end String 结束时间
	 */
	private void syncData(LazyDynaBean bean, String start, String end) {
		Connection oaConn = null;
		String status = (String) bean.get("status");
		status = status == null ? "0" : status;
		try {
			if ("1".equalsIgnoreCase(status)) {
				oaConn = getConn(bean);
				
				if(oaConn == null) {
                    return;
                }
				
				// 操作表
				String opts = (String ) bean.get("options");
				String[] op = opts.split(",");
				// 表空间
				this.space = (String) bean.get("space");
				// 数据库类型
				this.baseType = (String) bean.get("dbtype");
				// 关联的指标
				String related = (String) bean.get("related");				
				for (int i = 0; i < op.length; i++) {
					if ("kq_originality_data".equalsIgnoreCase(op[i])) {
					    String start_date = start;
    					String start_hh = "00";
    					String start_mm = "00";
    					String end_date = end;
    					String end_hh = "23";
    					String end_mm = "59";
    					 
    					String start_time=start_hh+":"+start_mm;
    					String end_time=end_hh+":"+end_mm;
    					
    					SyncCardData syncCardData = new SyncCardData(this.conn);
						syncCardData.setMailTo(this.getMailto());
						
						if(syncCardData.isAllowSync(bean)) {
							try {
								syncCardData.sycnCardData(start_date,start_time,end_date,end_time);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						// 查询数据
						List list = selectOAData(op[i], oaConn, start, end);
						
						// 清空临时表
						clearTemp(op[i]);
						// 将数据保存到临时 表中
						saveToTemp(list, null, op[i]);
						// 更新临时表
						updateBaseTempTable(op[i],related);
						updateTypeTempTable (op[i]);
						// 将临时表中的数据保存到相应的表中
						saveToTable(op[i], list.size());
						// 清空临时表
						clearTemp(op[i]);
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (oaConn != null) {				
					oaConn.close();				
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 清空临时表
	 * @param opTable String 表名
	 */
	private void clearTemp(String opTable) {
		StringBuffer buff = new StringBuffer();
		buff.append("TRUNCATE table t#_OA_to_HRP_");
		buff.append(opTable);
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(buff.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将数据保存到表中
	 * @param opTable String 表名
	 */
	private void saveToTable(String opTable, int listSize) {
		ContentDAO dao = new ContentDAO(this.conn);
		// 更新应经同步的数据
		String destTab=opTable;//目标表
		String srcTab="t#_OA_to_HRP_" + opTable;//源表
		String strJoin=destTab+"."+opTable+"Z1="+srcTab+"." 
		+ opTable+"Z1 and " + destTab+"."+opTable+"Z3="
		+srcTab+"." + opTable+"Z3 and " + destTab+".a0100="
		+srcTab+".a0100 and "+destTab+".nbase="+srcTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
		
		String strDWhere="";//更新目标的表过滤条件
		
		StringBuffer sql = new StringBuffer();
		ArrayList valueList = new ArrayList();
		try {
			strJoin += " and " + srcTab + "." + opTable + "03=" + opTable + "." + opTable + "03";
			StringBuffer delSql = new StringBuffer();
			StringBuffer delclo = new StringBuffer();
			delSql.append("delete from ");//opTable
			delSql.append(srcTab);
			delSql.append(" where (");
			
			delclo.append(srcTab);
			delclo.append(".");
			delclo.append(opTable);
			delclo.append("z1,");
			
			delclo.append(srcTab);
			delclo.append(".");
			delclo.append(opTable);
			delclo.append("z3,");
			
			delclo.append(srcTab);
			delclo.append(".a0100,");
			
			delclo.append(srcTab);
			delclo.append(".nbase,");
			
			delclo.append(srcTab);
			delclo.append(".");
			delclo.append(opTable);
			delclo.append("03");
			
			delSql.append(delclo);
			
			delSql.append(") in (select ");
			delSql.append(delclo);
			delSql.append(" from ");
			delSql.append(srcTab);
			delSql.append(",");
			delSql.append(opTable);
			delSql.append(" where ");
			delSql.append(strJoin);
			delSql.append(")");
			// 删除
			//System.out.println("delete from " + srcTab + repairSqlTwoTable(srcTab,strJoin,strDWhere,"",destTab));
			int delCount = dao.update("delete from " + srcTab + repairSqlTwoTable(srcTab,strJoin,strDWhere,"",destTab));
			
			// 删除a0100 是空的数据
			if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
				delCount += dao.update("delete from " + srcTab + " where a0100 is null or a0100='' or " + opTable+ "03 is null or " + opTable + "03='' or " + opTable+ "z1 is null or " + opTable + "z1='' or " + opTable+ "z3 is null or " + opTable + "z3=''");
			} else {
				delCount += dao.update("delete from " + srcTab + " where a0100 is null or " + opTable+ "03 is null or " + opTable+ "z1 is null or " + opTable+ "z3 is null");
			}
			
			//删除申请类型在kq_item中不存在的数据
			delCount += dao.update("DELETE FROM " + srcTab + " WHERE NOT EXISTS(SELECT 1 FROM kq_item WHERE item_id=" + srcTab + "." + opTable + "03)");
            
//			System.out.println(delSql.toString());
			
//			int delCount = dao.update(delSql.toString());
//			System.out.println("删除语句1:  delete from " + srcTab + repairSqlTwoTable(srcTab,strJoin,strDWhere,"",destTab));
//			System.out.println("删除 " + delCount + " 条数据");
			
			// 查询主键
			IDFactoryBean idFactory = new IDFactoryBean();
			
			// 插入主键
//			String sql = "select * from t#_OA_to_HRP_" + opTable +" where " + opTable + "03 is not null and " + opTable + "z1 is not null and " + opTable + "z3 is not null and e0127 is not null";
			
			int dbname = Sql_switcher.searchDbServer();
			
			sql.append("select * from t#_OA_to_HRP_");
			sql.append(opTable);
			sql.append(" a where ");
			sql.append(" not exists(select 1 from  ");
			sql.append(opTable);
			sql.append(" b where b.a0100=a.a0100 and upper(b.nbase)=upper(a.nbase) and b." +opTable+ "03=a."+opTable+"03 and b."+opTable+"z1=a."+opTable+"z1 and b."+opTable+"z3=a."+opTable+"z3) and ");
			sql.append(opTable);
			sql.append("03 is not null ");
			
			if (dbname == 1) {
				sql.append(" and ");
				sql.append(opTable);
				sql.append("03<>'' ");
			}
			
			sql.append(" and ");
			sql.append(opTable);
			sql.append("z1 is not null ");
			
			sql.append(" and ");
			sql.append(opTable);
			sql.append("z3 is not null ");
			
			sql.append(" and ");
			sql.append("a0100 is not null ");
			
			if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
				sql.append(" and ");
				sql.append(opTable);
				sql.append("z1<>'' ");
				
				sql.append(" and ");
				sql.append(opTable);
				sql.append("z3<>'' ");
				
				sql.append(" and ");
				sql.append("a0100<>'' ");
			}
			
			RowSet rs = null;
			rs = dao.search(sql.toString());
			while (rs.next()) {
				ArrayList list = new ArrayList();
				String nbase = rs.getString("nbase");
				String b0110 = rs.getString("B0110");
				String e0122 = rs.getString("E0122");
				String e01A1 = rs.getString("E01A1");
				String a0101 = rs.getString("A0101");
				String a0100 = rs.getString("A0100");
				String q1503 = rs.getString(opTable + "03");
				Object q15Z1 = rs.getObject(opTable + "Z1");
				Object q15Z3 = rs.getObject(opTable + "Z3");
				Object q1505 = rs.getObject(opTable + "05");
				String q1507 = rs.getString(opTable + "07");
				String q1509 = rs.getString(opTable + "09");
				String q1511 = rs.getString(opTable + "11");
				String q1513 = rs.getString(opTable + "13");
				String q1515 = rs.getString(opTable + "15");
				String q15Z0 = rs.getString(opTable + "Z0");
				String q15Z5 = rs.getString(opTable + "Z5");
				int q1504 = rs.getInt(opTable + "04");
				Object q15Z7 = rs.getObject(opTable + "Z7");
				String state = rs.getString("STATE");
				String id = idFactory.getId(opTable.toUpperCase() + "."+opTable.toUpperCase()+"01", "", this.conn);
				
				list.add(id);
				list.add(nbase);
				list.add(b0110);
				list.add(e0122);
				list.add(e01A1);
				list.add(a0101);
				list.add(a0100);
				list.add(q1503);
				list.add(q1505);
				list.add(q15Z1);
				list.add(q15Z3);
				list.add(q1507);
				list.add(q1509);
				list.add(q1511);
				list.add(q1513);
				list.add(q1515);
				list.add(q15Z0);
				list.add(q15Z5);
				list.add(Integer.valueOf(q1504));
				list.add(q15Z7);
				list.add(state);
				
				valueList.add(list);
			}
			
			StringBuffer buf = new StringBuffer();
			buf.append("insert into " );
			buf.append(opTable );
			buf.append( "(");
			buf.append(opTable);
			buf.append("01,Nbase,B0110,E0122,E01A1,A0101,A0100,");
			buf.append(opTable);
			buf.append("03,");
			buf.append(opTable);
			buf.append("05,");
			buf.append(opTable);
			buf.append("Z1,");
			buf.append(opTable);
			buf.append("Z3,");
			buf.append(opTable);
			buf.append("07,");
			buf.append(opTable);
			buf.append("09,");
			buf.append(opTable);
			buf.append("11,");
			buf.append(opTable);
			buf.append("13,");
			buf.append(opTable);
			buf.append("15,");
			buf.append(opTable);
			buf.append("Z0,");
			buf.append(opTable);
			buf.append("Z5,");
			buf.append(opTable);
			buf.append("04,");
			buf.append(opTable);
			buf.append("Z7,state)  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			dao.batchUpdate(buf.toString(), valueList);
		} catch (Exception e) {
			e.printStackTrace();
		}

	    //处理年假扣减
		if ("q15".equalsIgnoreCase(opTable)) {
            handleAnnualHoliday(dao, valueList);
        }
	}

    private void handleAnnualHoliday(ContentDAO dao, ArrayList valueList) {
        if (null == valueList || 0 == valueList.size()) {
            return;
        }
            
        // 当后台设置importdata_deductholidays=true时，才扣减年假
        String deduct = SystemConfig.getPropertyValue("importdata_deductholidays");
        if (!"true".equalsIgnoreCase(deduct)) {
            return;
        }
            
        StringBuffer find = new StringBuffer();
		find.append("select q1501,Nbase,B0110,E0122,E01A1,A0101,A0100,");
		find.append("q1503,q1505,q15Z1,q15Z3,q1507,q1509,q1511,q1513,q1515,q15Z0,q15Z5,");
		find.append("q1504,q15Z7,state");
		find.append(" from q15");
		find.append(" where q1501 in (APPID)");
		
	    HashMap holidayTypeMap = new HashMap();
	    AnnualApply annualApply = new AnnualApply(null, conn);
	    float[] holiday_rules = annualApply.getHoliday_minus_rule();// 年假扣减规则
	    
	    StringBuffer buf = new StringBuffer();
	    for (int i = 0; i < valueList.size(); i++) {
	        ArrayList list = (ArrayList) valueList.get(i);
	        String id = (String) list.get(0);
	        buf.append(",");
	        buf.append("'");
	        buf.append(id);
	        buf.append("'");
	        
	        //原因1：oracle的in中item有数量限制（9i：256个；9i以上1000个）
	        //原因2：担心sql语句太长有其它问题
	        //策略：每一百个申请执行一次查询
	        
	        //如果够100个或到最后一个，那么执行假期扣减
	        if (0 == i % 100 || i == valueList.size()-1) {
	            String findSql = find.toString().replace("APPID", buf.substring(1));
	            buf.setLength(0);
	            try {
	                deductHoliday(dao, findSql, holidayTypeMap, annualApply, holiday_rules);
	            } catch (Exception e) {
	                
	            }
	        }
	    }
	}

    private void deductHoliday(ContentDAO dao, String findSql, HashMap holidayTypeMap, 
            AnnualApply annualApply, float[] holiday_rules) throws SQLException, GeneralException {
        RowSet rs = null;
        ArrayList updateSqlList = new ArrayList();
        try {
            rs = dao.search(findSql);
            while (rs.next()) {
            	// 单位代码
            	String b0110 = rs.getString("b0110");
            	String holiday_type = "";
            	if (holidayTypeMap.containsKey(b0110)) {
            		holiday_type = (String) holidayTypeMap.get(b0110);
            	} else {
            		holiday_type = KqParam.getInstance().getHolidayTypes(this.conn, b0110);
					holidayTypeMap.put(b0110, holiday_type);
            	}
    
            	// 请假类型
            	String sels = rs.getString("q1503");
            	if (("," + holiday_type.toUpperCase() + ",").indexOf("," + sels.toUpperCase() + ",") == -1) {
                    continue;
                }
            	
            		HashMap kqItem_hash = annualApply.count_Leave(sels);
    
            		java.sql.Timestamp kq_start = rs.getTimestamp("q15z1");
            		java.sql.Timestamp kq_end = rs.getTimestamp("q15z3");
            		String a0100 = rs.getString("a0100");
            		String nbase = rs.getString("nbase");
            		String id = rs.getString("q1501");
            		String start = DateUtils.format(kq_start, "yyyy.MM.dd HH:mm:ss");
            		String end = DateUtils.format(kq_end, "yyyy.MM.dd HH:mm:ss");
            		String errorInfo = "";
            		
            		//增加异常处理，防止单个人数据有问题时影响其他人员数据的扣减
            		try {
                		float leave_tiem = annualApply.getHistoryLeaveTime(
                				kq_start, kq_end, a0100, nbase, b0110, kqItem_hash,
                				holiday_rules);
                		String history = annualApply.upLeaveManage(a0100, nbase,
                				sels, start, end, leave_tiem, "1", b0110, kqItem_hash,
                				holiday_rules);
                		String updateSql = "update q15 set history='" + history
                				         + "' where q1501='" + id + "'";
                		
                		updateSqlList.add(updateSql);
                		
                		if("".equals(history) || "0.0,0.0;0.0,0.0".equals(history)) {
                			errorInfo = "考勤同步数据异常：申请假期时长为0 (" + rs.getString("a0101")
                			          + "  " + sels + AdminCode.getCodeName("27", sels)+ "  " + start + "~" + end + ")";
                			cat.error(errorInfo);
                			System.out.println(errorInfo);
                		}
            		} catch (Exception e) {
            		    e.printStackTrace();
            		}
            	}
            
            if (updateSqlList.size() > 0) {
            	dao.batchUpdate(updateSqlList);
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
    }
	
	/**
	 * 修改Sql_switcher.getUpdateSqlTwoTable后的不足
	 * 
	 * @param srcTab//原表
	 * @param strJoin//关联条件
	 * @param update//Sql_switcher.getUpdateSqlTwoTable得到的语句
	 * @return
	 */
	private static String repairSqlTwoTable(String srcTab, String strJoin, String strDWhere, String whereIN, String destTab) {
		String update = "";
		String falgS = "";
		if (strDWhere == null || strDWhere.length() <= 0) {
            falgS = "where";
        } else {
            falgS = "and";
        }
		String strSWhere = "";
		if (whereIN == null || whereIN.length() <= 0) {
            strSWhere = "";
        } else {
            strSWhere = " and " + whereIN;
        }
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {

		}
		case Constant.ORACEL: {
			String where2 = " " + falgS + "  EXISTS(SELECT 1 FROM " + destTab + "  WHERE " + strJoin + ")";
			where2 = where2 + strSWhere;
			update = update + where2;
			break;
		}
		case Constant.DB2: {
			String where2 = " " + falgS + " EXISTS(SELECT 1 FROM " + srcTab
					+ "," + destTab +  "  WHERE " + strJoin + ")";
			where2 = where2 + strSWhere;
			update = update + where2;
			break;
		}
		}
		return update;
	}
	
	/**
	 * 更新临时表
	 * @param opTable String 表名称
	 * @param related String 关联字段
	 */
	private void updateBaseTempTable (String opTable, String related) {
		List list = getNbaseList();
		ContentDAO dao = new ContentDAO(this.conn);
		for (int i = 0; i < list.size(); i++) {
			try {
				String nbase = (String) list.get(i);
				String destTab="t#_OA_to_HRP_" + opTable;//目标表
				String srcTab=nbase+"A01";//源表
				String strJoin=destTab+".e0127="+srcTab+"." + related;//关联串  xxx.field_name=yyyy.field_namex,....
				//更新串  xxx.field_name=yyyy.field_namex,....
				StringBuffer strSet=new StringBuffer();
				strSet.append(destTab+".nbase='"+nbase+"'`"+destTab+".a0100="+srcTab+".a0100`");
				strSet.append(destTab+".a0101="+srcTab+".a0101`"+destTab+".e01a1="+srcTab+".e01a1`");
				strSet.append(destTab+".B0110="+srcTab+".B0110`"+destTab+".E0122="+srcTab+".E0122`"+destTab+"."+opTable+"Z5='03'`");
				strSet.append(destTab+"."+opTable+"Z0='01'");
				String strDWhere="";//更新目标的表过滤条件
				String strSWhere="";//源表的过滤条件 
				switch(Sql_switcher.searchDbServer()) {
					case Constant.MSSQL: {
						strSWhere = "("+srcTab+"."+related+" is not null and "+srcTab+"."+related+"<>'')";
						break;
					}
				
					case Constant.ORACEL: {
						strSWhere = "("+srcTab+"."+related+" is not null)";	 
						break;
					}
				
					case Constant.DB2:{
						strSWhere = "("+srcTab+"."+related+" is not null)";
						break;
					}
				}
				
				String update=getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet.toString(),strDWhere,strSWhere);
				update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
				//System.out.println(update);
				dao.update(update);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getUpdateSqlTwoTable(String destTab, String srcTab, String strJoin, String strSet, String strDWhere, String strSWhere)
	/*      */   {
	/*  153 */     StringBuffer strSQL = new StringBuffer();
	/*  154 */     boolean bapp = false;
	/*  155 */     switch (Sql_switcher.searchDbServer())
	/*      */     {
	/*      */     case 2:
	/*      */     case 3:
	/*  159 */       StringBuffer strDFields = new StringBuffer();
	/*  160 */       StringBuffer strSFields = new StringBuffer();
	/*  161 */       getUpdateFields(strSet, strDFields, strSFields);
	/*      */ 
	/*  163 */       strSQL.append("update ");
	/*  164 */       strSQL.append(destTab);
	/*  165 */       strSQL.append(" set (");
	/*  166 */       strSQL.append(strDFields.toString());
	/*  167 */       strSQL.append(")=(select distinct ");
	/*  168 */       strSQL.append(strSFields.toString());
	/*  169 */       strSQL.append(" from ");
	/*  170 */       strSQL.append(srcTab);
	/*  171 */       strSQL.append(" where ");
	/*  172 */       strSQL.append(strJoin);
	/*  173 */       if ((strSWhere != null) && (!"".equals(strSWhere)))
	/*      */       {
	/*  175 */         strSQL.append(" and ");
	/*  176 */         strSQL.append(strSWhere);
	/*      */       }
	/*  178 */       strSQL.append(")");
	/*  179 */       if ((strDWhere == null) || ("".equals(strDWhere)))
	/*      */ {
                    break;
                }
	/*  181 */       strSQL.append(" where ");
	/*  182 */       strSQL.append(strDWhere); break;
	/*      */     default:
	/*  187 */       strSet = strSet.replace('`', ',');
	/*  188 */       strSQL.append("update ");
	/*  189 */       strSQL.append(destTab);
	/*  190 */       String strLeft = " left join " + srcTab + " on " + strJoin;
	/*  191 */       String strUpdate = " set " + strSet;
	/*  192 */       String strFrom = " from " + destTab;
	/*  193 */       strSQL.append(strUpdate);
	/*  194 */       strSQL.append(strFrom);
	/*  195 */       strSQL.append(strLeft);
	/*  196 */       if ((strSWhere != null) && (!"".equals(strSWhere)))
	/*      */       {
	/*  198 */         strSQL.append(" where ");
	/*  199 */         strSQL.append(strSWhere);
	/*  200 */         bapp = true;
	/*      */       }
	/*  202 */       if ((strDWhere == null) || ("".equals(strDWhere)))
	/*      */ {
                    break;
                }
	/*  204 */       if (bapp)
	/*  205 */ {
                    strSQL.append(" and ");
                }
	/*      */       else
	/*  207 */ {
                    strSQL.append(" where ");
                }
	/*  208 */       strSQL.append(strDWhere);
	/*      */     }
	/*      */ 
	/*  212 */     return strSQL.toString();
	/*      */   }
	
	
	 private static void getUpdateFields(String strSet, StringBuffer strDFields, StringBuffer strSFields)
	 /*      */   {
	 /*  116 */     String[] strArr = StringUtils.split(strSet, "`");
	 /*  117 */     for (int i = 0; i < strArr.length; i++)
	 /*      */     {
	 /*  119 */       String temp = strArr[i];
	 /*  120 */       String[] strtmp = StringUtils.split(temp, "=", 2);
	 /*  121 */       strDFields.append(strtmp[0]);
	 /*  122 */       strDFields.append(",");
	 /*  123 */       strSFields.append(strtmp[1]);
	 /*  124 */       strSFields.append(",");
	 /*      */     }
	 /*  126 */     if (strDFields.length() > 0)
	 /*  127 */ {
             strDFields.setLength(strDFields.length() - 1);
         }
	 /*  128 */     if (strSFields.length() > 0)
	 /*  129 */ {
             strSFields.setLength(strSFields.length() - 1);
         }
	 /*      */   }
	
	/**
	 * 更新临时表
	 * @param opTable String 表名称
	 * @param related String 关联字段
	 */
	private void updateTypeTempTable (String opTable) {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String destTab="t#_OA_to_HRP_" + opTable;//目标表
			String srcTab="kq_item";//源表
			String strJoin=destTab+"."+opTable+"03="+srcTab+"." + "item_name";//关联串  xxx.field_name=yyyy.field_namex,....
			//更新串  xxx.field_name=yyyy.field_namex,....
			StringBuffer strSet=new StringBuffer();
			strSet.append(destTab+"."+opTable+"03="+srcTab+".item_id");
			String strDWhere="";//更新目标的表过滤条件
//				String strSWhere="(item_name is not null and item_name<>'')";//源表的过滤条件  
			String strSWhere="item_name is not null";//源表的过滤条件 
			String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet.toString(),strDWhere,strSWhere);
			update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
			//System.out.println(update);
			dao.update(update);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得人员库列表
	 * @return List<String> 人员库列表
	 */
	private List getNbaseList() {
		List list = new ArrayList();
//		String sql = "select * from dbname";
//		ResultSet rs = null;
//		ContentDAO dao = new ContentDAO(this.conn);
		
		String nbases = "";
		try {
			nbases = SystemConfig.getPropertyValue("kq_app_import_nbase");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
		    // zxj add 2014.02.28 考勤人员库不再分单位设置，直接调用取人员库方法
		    ArrayList preList = RegisterInitInfoData.getUNDase(this.conn);
		    for (int i=0; i<preList.size(); i++) {
		        String pre = (String)preList.get(i);
		        
		        if (nbases != null && nbases.trim().length() > 0) {
                    if (nbases.toUpperCase().indexOf(pre.toUpperCase()) != -1) {
                        list.add(pre);
                    }
                } else {
                    list.add(pre);
                }
		    }
		    
		    /* zxj 2014.02.28 考勤人员库不再分单位设置，所以不用取全部人员库了
			rs = dao.search(sql);
						
			while (rs.next()) {
				if (nbases != null && nbases.trim().length() > 0) {
					String nbase = rs.getString("pre");
					if (nbases.toUpperCase().indexOf(nbase.toUpperCase()) != -1) {
						list.add(nbase);
					}
				} else {
					list.add(rs.getString("pre"));
				}
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			if (rs != null) {
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
		}
		
		return list;
	}
	/**
	 * 将数据保存到临时表中
	 * @param dataList
	 * @param idList
	 * @param opTable
	 */
	private void saveToTemp(List dataList, List idList, String opTable) {
		ContentDAO dao = new ContentDAO(this.conn);
			// 插入到临时表
			try {
				// 将id更新到数据中
				for (int i = 0; i < dataList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) dataList.get(i);
					Iterator it = bean.getMap().entrySet().iterator();
					String sql = "insert into t#_OA_to_HRP_" + opTable ;
					StringBuffer cols = new StringBuffer();
					StringBuffer values = new StringBuffer();
					List list = new ArrayList();
					while (it.hasNext()) {
						Map.Entry en =  (Map.Entry) it.next();
						cols.append(",");
						cols.append(en.getKey());
						values.append(",?");
						list.add(en.getValue());
					}
					
					// 添加q1501
//					cols.append(",");
//					cols.append(opTable);
//					cols.append("01");
//					values.append(",?");
//					String id = (String) idList.get(i);
//					list.add(id);

					dao.insert(sql + "("+cols.substring(1)+") values (" +values.substring(1) + ")", list);
				}
		
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	private String getOpDbdate(String fieldname, String format){
		StringBuffer strvalue = new StringBuffer();
		int len = format.length();
		if("oracle".equalsIgnoreCase(this.baseType)){
			strvalue.append("to_char(");
			strvalue.append(fieldname);
			strvalue.append(",'");
			strvalue.append(format);
			strvalue.append("')");
		} else if ("mysql".equalsIgnoreCase(this.baseType)) {
			strvalue.append("date_format(");
			strvalue.append(fieldname);
			strvalue.append(",'");
			strvalue.append(format);
			strvalue.append("')");
		}
		else{
			strvalue.append("convert(varchar(");
			strvalue.append(len);
			strvalue.append("),");
			strvalue.append(fieldname);
			strvalue.append(",20)");
		}
		
		return strvalue.toString();
		
	}
	
	/**
	 * 查询oa数据库中的数据
	 * @param optable String 查询的表
	 * @param conn Connection 数据库连接
	 * @param start  String 开始时间
	 * @param end String 结束时间
	 * @return
	 */
	private List selectOAData (String optable, Connection oaConn, String start, String end) {
		List list = new ArrayList();
		
		// 查询sql
		StringBuffer sql = new StringBuffer();
		// 如果是Oracle，添加表空间
		if ("oracle".equalsIgnoreCase(this.baseType)) {
			if (this.space != null && this.space.length() > 0) {
				sql.append("select distinct * from "+this.space+".HRP_");
			} else {
				sql.append("select distinct * from HRP_");
			}
		} else {
			sql.append("select distinct * from HRP_");
		}
		
		String format = "yyyy-MM-dd";
		if("mysql".equalsIgnoreCase(this.baseType)){
			format="%Y-%m-%d";
		}
		
		start = start.replaceAll("\\.", "\\-");
		end = end.replaceAll("\\.", "\\-");
		
		sql.append(optable);
		sql.append("_VIEW where ((");
		sql.append(getOpDbdate(optable + "Z7", format));
		sql.append(" >='"+start+"' and ");
		sql.append(getOpDbdate(optable + "Z7", format));
		sql.append("<='"+end+"') or (");
		
		sql.append(getOpDbdate(optable + "Z1", format));
		sql.append(" >='"+start+"' and ");
		sql.append(getOpDbdate(optable + "Z1", format));
		sql.append("<='"+end+"') or (");
		
		sql.append(getOpDbdate(optable + "Z3", format));
		sql.append(" >='"+start+"' and ");
		sql.append(getOpDbdate(optable + "Z3", format));
		sql.append("<='"+end+"')");
		
		sql.append(") AND " + optable + "03 IS NOT NULL");

		ResultSet rs = null;
		Statement stmt = null;
		try {
		    if (cat.isDebugEnabled()) {
                cat.debug("考勤申请同步取数：" + sql.toString());
            }
		      
            stmt = oaConn.createStatement();
            try {
                rs = stmt.executeQuery(sql.toString());
            } catch (Exception e) {
                e.printStackTrace();
                // zxj 20190429 有时误配表所属用户，这里不带用户重试一次
                if (StringUtils.isNotBlank(this.space)) {
                    rs = stmt.executeQuery(sql.toString().replace(this.space + ".HRP_", "HRP_"));
                } else {
                    throw e;
                }
            }

			ResultSetMetaData meta = rs.getMetaData();
			int count = meta.getColumnCount();
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				for (int i = 0; i < count; i++) {
					String colName = meta.getColumnName(i + 1);
					if("z1".equalsIgnoreCase(colName.substring(3)) || "z3".equalsIgnoreCase(colName.substring(3))
							|| "z7".equalsIgnoreCase(colName.substring(3))
							|| "05".equalsIgnoreCase(colName.substring(3))) {
                        bean.set(colName, rs.getTimestamp(colName));
                    } else {
                        bean.set(colName, rs.getObject(colName));
                    }
				}
				
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(stmt);
		}
		
		return list;
	}
	/**
	 * 获得oa数据库连接
	 * @param bean
	 * @return
	 */
	public Connection getConn (LazyDynaBean bean) {

		Connection oaConn = null;
		String type = (String) bean.get("dbtype");
		StringBuffer url = new StringBuffer();
		// 数据库用户名
		String username = (String) bean.get("user");
		// 数据库密码
		String userpwd = (String) bean.get("pwd");
		try {
			if ("oracle".equalsIgnoreCase(type)) {
				// url
				url.append("jdbc:oracle:thin:@");
				url.append(bean.get("ip"));
				url.append(":");
				url.append(bean.get("port"));
				url.append(":");
				url.append(bean.get("dbname"));
								
				Class.forName("oracle.jdbc.OracleDriver");
				oaConn = DriverManager.getConnection( url.toString(), username, userpwd);
			} else if ("mssql".equalsIgnoreCase(type)) {
				// url
				url.append("jdbc:sqlserver://");
				url.append(bean.get("ip"));
				url.append(":");
				url.append(bean.get("port"));
				url.append(";databaseName=");
				url.append(bean.get("dbname"));
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				oaConn = DriverManager.getConnection(url.toString(), username, userpwd); 
			} else if ("db2".equalsIgnoreCase(type)) {
				Class.forName("Com.ibm.db2.jdbc.net.DB2Driver"); 
				url.append("jdbc:db2://");
				url.append(bean.get("ip"));
				url.append(":");
				url.append(bean.get("port"));
				url.append("/");
				url.append(bean.get("dbname"));

				oaConn = DriverManager.getConnection(url.toString(), username, userpwd); 

			} else if ("mysql".equalsIgnoreCase(type)) {
				Class.forName("com.mysql.jdbc.Driver");
//				jdbc:mysql://127.0.0.1:3306/scutcs
				url.append("jdbc:mysql://");
				url.append(bean.get("ip"));
				url.append(":");
				url.append(bean.get("port"));
				url.append("/");
				url.append(bean.get("dbname"));
				// 解决别名无法取到的问题
				url.append("?useOldAliasMetadataBehavior=true");
				
				
				oaConn = DriverManager.getConnection(url.toString(), username, userpwd); 
			}
		} catch (Exception e) {
			//如果是mysql且连接失败,则尝试指定字符集UTF-8重新链接
			if("mysql".equalsIgnoreCase(type)){
				url.append("&useunicode=true&characterEncoding=UTF-8");
				try {
					oaConn = DriverManager.getConnection(url.toString(), username, userpwd);
				} catch (SQLException e1) {
					e1.printStackTrace();
					System.out.println("无法打开考勤数据同步目标数据库！");
				} 
			}else{
				e.printStackTrace(); 
				System.out.println("无法打开考勤数据同步目标数据库！");
			}
			
		}
		
		
		
		return oaConn;
	}
	/**
	 * 新增数据库信心的保存
	 * 
	 * @param bean
	 *            LazyDynaBean 封装了数据库信息
	 */
	public void save(LazyDynaBean bean) {
		DocumentSyncXML xml = new DocumentSyncXML(this.conn, getConnXML());
		try {
			xml.saveParameter(bean);
			String xmlStr = xml.getXML();
			RecordVo vo = new RecordVo("kq_parameter");
			vo.setString("status", "1");
			vo.setString("b0110", "UN");
			vo.setString("description", "考勤同步配制");
			vo.setString("name", "KQ_OA_DATA_TO_HRP");
			vo.setString("content", xmlStr);

			ContentDAO dao = new ContentDAO(conn);
			dao.updateValueObject(vo);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新同步配制信息
	 * 
	 * @param bean
	 *            LazyDynaBean 封装数据库连接信息集合
	 */
	public void update(LazyDynaBean bean) {
		ArrayList list = new ArrayList();
		list.add(bean);
		batchupdate(list);
	}

	/**
	 * 批量更新同步信息
	 * 
	 * @param list
	 *            List<LazyDynaBean>
	 */
	public void batchupdate(List list) {
		DocumentSyncXML xml = new DocumentSyncXML(this.conn, getConnXML());
		try {
			for (int i = 0; i < list.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) list.get(i);
				xml.updateParameter(bean);
			}
			String xmlStr = xml.getXML();
			RecordVo vo = this.getVo("KQ_OA_DATA_TO_HRP");
			vo.setString("content", xmlStr);

			ContentDAO dao = new ContentDAO(conn);
			dao.updateValueObject(vo);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除一条记录
	 * 
	 * @param id
	 *            String 记录的序号
	 */
	public void delete(String id) {
		DocumentSyncXML xml = new DocumentSyncXML(this.conn, getConnXML());
		try {
			String[] ids = id.split(",");
			for (int i = 0; i < ids.length; i++) {
				xml.deleteParameter(ids[i]);
			}
			String xmlStr = xml.getXML();
			RecordVo vo = new RecordVo("kq_parameter");
			vo.setString("status", "1");
			vo.setString("b0110", "UN");
			vo.setString("description", "考勤同步配制");
			vo.setString("name", "KQ_OA_DATA_TO_HRP");
			vo.setString("content", xmlStr);

			ContentDAO dao = new ContentDAO(conn);
			dao.updateValueObject(vo);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得一个新的id，在最后一个id加1
	 * 
	 * @return String 新的id
	 */
	public String getId() {
		List list = getConnStrList();
		String id = "1";
		if (list.size() > 0) {
			LazyDynaBean bean = (LazyDynaBean) list.get(list.size() - 1);
			String tem = (String) bean.get("id");
			int temp = Integer.parseInt(tem) + 1;
			id = String.valueOf(temp);
		}
		return id;
	}

	/**
	 * 获得除A0100、nbase之外的指标项
	 * 
	 * @return ArrayList<CommonData>
	 */
	public ArrayList getA01File() {
		ArrayList list = new ArrayList();
		List listAll = DataDictionary.getFieldList("A01",
				Constant.USED_FIELD_SET);
		if (listAll != null && listAll.size() > 0) {
			for (int i = 0; i < listAll.size(); i++) {
				FieldItem item = (FieldItem) listAll.get(i);
				String name = item.getItemid();
				String desc = item.getItemdesc();
				if (!("A0100".equalsIgnoreCase(name) || "nbase"
						.equalsIgnoreCase(name))) {
					if ("A".equalsIgnoreCase(item.getItemtype()) && "0".equalsIgnoreCase(item.getCodesetid())) {
						CommonData data = new CommonData();
						data.setDataName(desc);
						data.setDataValue(name);
						list.add(data);
					}
				}
			}
		}

		return list;
	}

	/**
	 * 获得数据库类型
	 * 
	 * @return ArrayList<CommonData>
	 */
	public ArrayList getDBType() {
		ArrayList list = new ArrayList();
		CommonData data = new CommonData();
		data.setDataName("MSSQL");
		data.setDataValue("mssql");
		CommonData data2 = new CommonData();
		data2.setDataName("ORACLE");
		data2.setDataValue("oracle");
		CommonData data3 = new CommonData();
		data3.setDataName("DB2");
		data3.setDataValue("db2");
		CommonData data4 = new CommonData();
		data4.setDataName("MySQL");
		data4.setDataValue("mysql");
		list.add(data);
		list.add(data2);
		list.add(data4);
		//list.add(data3);
		return list;
	}

	/**
	 * 获得所有数据库连接的信息
	 * 
	 * @return List<LazyDynaBean>
	 */
	public List getConnStrList() {
		List list = new ArrayList();
		DocumentSyncXML xml = new DocumentSyncXML(this.conn, getConnXML());
		List childlist = xml.getAllElmentList("/datasources/datasource");
		for (int i = 0; i < childlist.size(); i++) {
			LazyDynaBean bean = new LazyDynaBean();
			Element el = (Element) childlist.get(i);
			String id = el.getAttributeValue("id");
			int sId = Integer.parseInt(id);
			String desc = xml.getValue(sId, "desc");
			String type = xml.getValue(sId, "dbtype").toUpperCase();
			String ip = xml.getValue(sId, "ip");
			String status = xml.getValue(sId, "status");

			bean.set("id", id);
			bean.set("desc", desc);
			bean.set("type", type);
			bean.set("ip", ip);
			bean.set("status", status);
			list.add(bean);
		}

		return list;
	}

	/**
	 * 根据id获得所有数据库连接的信息
	 * 
	 * @return
	 */
	public LazyDynaBean getConnStrList(String id) {
		LazyDynaBean bean = new LazyDynaBean();
		DocumentSyncXML xml = new DocumentSyncXML(this.conn, getConnXML());
		Element el = xml.getElement(id);
		bean.set("syncxml_id", el.getAttributeValue("id"));
		List child = el.getChildren();
		for (int i = 0; i < child.size(); i++) {
			Element e = (Element) child.get(i);
			String name = e.getName();
			String value = e.getText();
			bean.set("syncxml_" + name, value);
		}
		return bean;
	}

	/**
	 * 获得同步配制的xml
	 * 
	 * @return String xml字符窜
	 */
	public String getConnXML() {
		StringBuffer buff = new StringBuffer();
		String xml = "";
		buff.append("select * from kq_parameter ");
		buff.append("where b0110='UN' and ");
		buff.append("name='KQ_OA_DATA_TO_HRP' and status='1'");
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs = null;
		try {
			rs = dao.search(buff.toString());
			if (rs.next()) {
				xml = rs.getString("content");
				if (xml == null || xml.length() == 0) {
					init();
					xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n<datasources>\r\n</datasources>\r\n";
				}
			} else {
				init();
				xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n<datasources>\r\n</datasources>\r\n";
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

		return xml;
	}

	/**
	 * 初始化参数
	 */
	private void init() {
		RecordVo vo = new RecordVo("kq_parameter");
		vo.setString("status", "1");
		vo.setString("b0110", "UN");
		vo.setString("description", "考勤同步配制");
		vo.setString("name", "KQ_OA_DATA_TO_HRP");
		StringBuffer strxml = new StringBuffer();
		strxml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n");
		strxml.append("<datasources>\r\n");
		strxml.append("</datasources>\r\n");
		vo.setString("content", strxml.toString());
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select * from kq_parameter where UPPER(name) = 'KQ_OA_DATA_TO_HRP' ";
			rs = dao.search(sql);
			if (rs.next()) {
				dao.updateValueObject(vo);
			} else {
				dao.addValueObject(vo);
			}
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception ex) {
			ex.printStackTrace();
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
	 * 根据主键获得vo
	 * 
	 * @param name
	 * @return
	 */
	private RecordVo getVo(String name) {
		RecordVo vo = new RecordVo("kq_parameter");
		vo.setString("name", name);
		vo.setString("b0110", "UN");
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vo;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

    public void setMailto(String mailto) {
        this.mailto = mailto;
    }

    public String getMailto() {
        return mailto;
    }
}
