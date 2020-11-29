package com.hjsj.hrms.utils.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.constant.SystemConfig;
import org.apache.log4j.Category;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

/************************
 *  
 *  特别注意：此类中的conn.createStatement()不能使用ContenDAO来替换，因为此类被ContenDAO调用
 * 
 * 
 * @author wangzj
 *
 */
public class DbEncrypUtil {

	private volatile static DbEncrypUtil uniqueInstance;
	
	//log日志
	private static Category log = Category.getInstance(DbEncrypUtil.class.getName());
	private static ArrayList list = new ArrayList();
	private static ArrayList viewList = new ArrayList();

	private DbEncrypUtil() {
		init();
	}
	
	private void init() {
		Connection conn = null;
		ResultSet rs = null;
		Statement st = null;
		try {
			conn = AdminDb.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("select tablename from t_sys_encrypt");
			while (rs.next()) {
				String tableName = rs.getString("tablename");
				list.add(tableName.toLowerCase());
			}
			PubFunc.closeDbObj(rs);
			//获取所有视图表view
			rs = st.executeQuery("select TABLE_NAME from user_tab_comments where table_type='VIEW'");
			while(rs.next()) {
				String viewName = rs.getString("TABLE_NAME").toLowerCase();
				viewList.add(viewName);
			}
			log.debug("数据库透明加密初始化成功，需要打开wallet才能查询的表包括：" + Arrays.toString(list.toArray()));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("数据库透明加密失败，失败原因:" + e.getMessage());
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(st);
			PubFunc.closeDbObj(conn);
		}
	}

	/**
	 * 获得线程安全的DbEncryp实例
	 * @return
	 */
	public static DbEncrypUtil getInstance() { 
		
		if(uniqueInstance == null) { //(1)
			createDbEncryp();
		}
		
        return uniqueInstance;
    }
	
	private static synchronized void createDbEncryp() {
		if(uniqueInstance == null) {
			uniqueInstance = new DbEncrypUtil();
			
			log.debug("创建线程安全DbEncrypUtil实例成功");
		}
		
	}
	
	/**
	 * 是否需要打开Wallet
	 * @param sql String 需要执行的sql语句
	 * @return boolean true为需要打开，false为不需要打开
	 */
	public boolean needOpenWallet(Connection conn,String sql) {
		
		if (sql == null || sql.length() <= 0) {
			return false;
		}
		
		boolean flag = false;
		for (int i = 0; i < list.size(); i++) {
			String tableName = (String) list.get(i);
			if (sql.toLowerCase().contains(tableName)) {
				flag = true;
				log.debug("sql语句【" + sql + "】中包含表【" + tableName + "】,需要打开wallet" );
				break;
			}
		}
		if(!flag) {
			for(int i = 0 ; i < viewList.size(); i++) {
				String viewName = (String) viewList.get(i);
				if(sql.toLowerCase().contains(viewName)) {
					String tableNames = getViewToTableName(conn,viewName);
					for (int j = 0; j < list.size(); j++) {
						String tableName = (String) list.get(j);
						if (tableNames.toLowerCase().contains(tableName)) {
							flag = true;
							log.debug("sql语句【" + sql + "】中包含表【" + tableName + "】,需要打开wallet" );
							break;
						}
					}
					break;
				}
			}
		}
		
		return flag;
	}
	
	/**
	 * 获取视图关联的表
	 * @return
	 */
	private String getViewToTableName(Connection conn,String viewName) {
		ResultSet rs = null;
		Statement st = null;
		StringBuffer tableName = new StringBuffer();
		try {
			st =conn.createStatement();
			String query = "select referenced_name from user_dependencies where name= '"+viewName.toUpperCase()+"' and type = 'VIEW' and  referenced_type = 'TABLE'";
			rs = st.executeQuery(query);
			while (rs.next()) {
				tableName.append(rs.getString("referenced_name").toLowerCase()+",");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(st);
		}
		return tableName.toString();
	}
	
	/**
	 * 打开Wallet
	 * @param conn
	 */
	public synchronized void  openWallet(Connection conn) throws Exception{
		ResultSet rs = null;
		Statement st = null;
		try {
			log.debug("正在打开wallet......." );
			conn.setAutoCommit(false);
			
			st = conn.createStatement();
			String query = "select num from t_sys_wallet for update";
			
			log.debug("查询表t_sys_wallet并为它加排他锁，需要执行sql【" + query + "】" );			
			rs = st.executeQuery(query);
			log.debug("执行sql成功，为t_sys_wallet表加上了排它锁");
			int num = 0;
			if (rs.next()) {
				num = rs.getInt("num");
			}
			
			log.debug("数据库中num值为" + num );
			
			// Wallet处于关闭状态，需要打开
			if (num == 0) {
				String openStr = "alter system set encryption wallet open identified by " + getWalletPassword();
				log.debug("num值为0，需要打开wallet，正在执行打开wallet sql【" + openStr +"】");
				st.execute(openStr);
				log.debug("执行打开Wallet sql成功");
				
				String state = "update t_sys_wallet set openflag=1";
				log.debug("更新openflag状态，正在执行打开sql【" + state +"】");
				st.execute(state);
				log.debug("执行更新openflag状态 sql成功");
				
			} 
			
			String numSql = "update t_sys_wallet set num=num+1";
			log.debug("需要将计数器加1，需要执行sql【" + numSql + "】");
			// 更新计数器num,同时更新openflag与CS保持一致
			st.execute(numSql);
			log.debug("更新计数器sql执行成功");
			
			conn.commit();
			conn.setAutoCommit(true);
			log.debug("表t_sys_wallet的排他锁释放");
			
			log.debug("提交事务，wallet已打开.......");
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			log.error("wallet打开失败，失败原因：" + e.getMessage());
			throw e;
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(st);
		}
	}
	
	/**
	 * 获取wallet的密码
	 * @return
	 */
	private String getWalletPassword() {
		String pwd = "";
		
		try {
			pwd = SystemConfig.getPropertyValue("wallet_key");
			log.debug("获取的wallet_key的值为" + pwd);
			
			if (pwd != null && pwd.startsWith("@")) {
				pwd = new Des().DecryPwdStr(pwd.substring(1));
				log.debug("wallet_key为密文，需要解密，解密后的密码为：" + pwd);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			log.error("获取wallet的密码失败，未能从system.properties中正确读取wallet_key的值");
		}
		return pwd;
	}
	
	/**
	 * 关闭Wallet
	 * @param conn
	 */
	public synchronized void  closeWallet(Connection conn) throws Exception{
		ResultSet rs = null;
		Statement st = null;
		try {
			log.debug("正在关闭wallet......." );
			conn.setAutoCommit(false);
			st = conn.createStatement();
			
			String query = "select num from t_sys_wallet for update";
			log.debug("查询表t_sys_wallet并为它加排他锁，需要执行sql【" + query + "】" );	
			rs = st.executeQuery(query);
			log.debug("执行sql成功，为t_sys_wallet表加上了排它锁");
			
			int num = 0;
			if (rs.next()) {
				num = rs.getInt("num");
			}
			log.debug("数据库中num值为" + num );
			
			// Wallet处于关闭状态，需要打开
			if (num == 1) {
				String closeStr = "alter system set encryption wallet close identified by " + getWalletPassword();
				log.debug("num值为1，需要关闭wallet，正在执行关闭wallet sql【" + closeStr +"】");
				st.execute(closeStr);
				log.debug("执行关闭Wallet sql成功");
				
				String state = "update t_sys_wallet set openflag=0";
				log.debug("更新openflag状态，正在执行关闭 sql【" + state +"】");
				st.execute(state);
				log.debug("执行更新openflag状态 sql成功");
			} 
			
			if (num ==0) {
				String closeStr = "alter system set encryption wallet close identified by " + getWalletPassword();
				log.debug("num值虽然为0，可能是计数器乱了或初始化时执行，需要关闭wallet，正在执行关闭wallet sql【" + closeStr +"】");
				try {
					st.execute(closeStr);
					log.debug("执行关闭Wallet sql成功");
				} catch (Exception es) {
					
				}
								
				String state = "update t_sys_wallet set openflag=0";
				log.debug("更新openflag状态，正在执行关闭 sql【" + state +"】");
				st.execute(state);
				log.debug("执行更新openflag状态 sql成功");
			}
			
			
			
			if (num >= 1) {
				String numSql = "update t_sys_wallet set num=num-1";
				log.debug("需要将计数器减1，需要执行sql【" + numSql + "】");
				// 更新计数器num,同时更新openflag与CS保持一致
				st.execute(numSql);
				log.debug("更新计数器sql执行成功");
			} else if (num < 0){
				String closeStr = "alter system set encryption wallet close identified by " + getWalletPassword();
				log.debug("num值虽然为0，可能是计数器乱了或初始化时执行，需要关闭wallet，正在执行关闭wallet sql【" + closeStr +"】");
				try {
					st.execute(closeStr);
					log.debug("执行关闭Wallet sql成功");
				} catch (Exception es) {
					
				}
								
				String state = "update t_sys_wallet set openflag=0,num=0";
				log.debug("更新openflag状态，正在执行关闭 sql【" + state +"】");
				st.execute(state);
				log.debug("执行更新openflag状态 sql成功");
			}
			conn.commit();
			conn.setAutoCommit(true);
			log.debug("表t_sys_wallet的排他锁释放");
			
			log.debug("提交事务，wallet已关闭.......");
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			log.error("关闭wallet失败，失败原因: " + e.getMessage());
			throw e;
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(st);
		}
	}
	
	/**
	 * 根据表明加密表
	 * @param tableName 表名
	 * @return boolean 
	 */
	public boolean encryptTable(Connection conn, String tableName){
		log.debug("调用encryptTable方法，根据表名加密所有列......" );
		
		boolean flag = false;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Statement st = null;
		Statement st2 = null;
		try {
			ArrayList<String> ConstraintColumns = getConstraintColumns(conn, tableName);

			st = conn.createStatement();
			st2 = conn.createStatement();
//			List rsydcolsrc = new ArrayList();
//			String sql = "select COLUMN_NAME from user_encrypted_columns where upper(TABLE_NAME)='"+tableName.toUpperCase()+"'";
//			rs=st.executeQuery(sql);
//			while(rs.next()){
//					rsydcolsrc.add(rs.getString("COLUMN_NAME"));//加密的字段
//				}				
			rs2 = st.executeQuery("select * from " + tableName + " where 1=2");
			int count = rs2.getMetaData().getColumnCount();
			
			for (int i = 1; i <= count; i++) {
				String columnName = rs2.getMetaData().getColumnName(i);
				if (ConstraintColumns.contains(columnName.toUpperCase())) {
					continue;
				}

				int type = rs2.getMetaData().getColumnType(i);
				
				try {
//					if(rsydcolsrc.size()>0){
//						if(!rsydcolsrc.contains(columnName)){
//							st2.execute("ALTER TABLE " + tableName + " MODIFY (" + columnName +" ENCRYPT no SALT)");
//						}
//					}else{
						st2.execute("ALTER TABLE " + tableName + " MODIFY (" + columnName +" ENCRYPT no SALT)");
					//}
					
				} catch (Exception e) {
					log.debug("加密表" + tableName + "中的" + columnName+ "时加密失败,字段类型：" + type+ "；失败原因：" + e.getMessage());
					//e.printStackTrace();
				}
				
			}
			rs = st.executeQuery("select 1 from t_sys_encrypt where upper(tableName)='" + tableName.toUpperCase() + "'");
			if (!rs.next()) {
				st.execute("insert into t_sys_encrypt(tablename) values('" + tableName + "')");
				this.list.add(tableName.toLowerCase());
			}

			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(rs2);
			PubFunc.closeDbObj(st);
			PubFunc.closeDbObj(st2);
		}
		
		return flag;
	}

	/**
	 * 获取表的约束（主键和索引）涉及的字段列表
	 * @param conn 数据库连接
	 * @param tableName 表名（大小写不限）
	 * @return 字段列表（其中可能有重复字段）
	 */
	private ArrayList<String> getConstraintColumns(Connection conn, String tableName) {
		ArrayList<String> columns = new ArrayList<String>();

		ResultSet rs = null;
		Statement st = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("select A.column_name");
			sql.append(" from dba_cons_columns A LEFT JOIN dba_constraints B");
			sql.append(" ON A.table_name=B.table_name");
			sql.append(" and A.owner=b.owner");
			sql.append(" and A.constraint_name=B.constraint_name");
			sql.append(" where A.table_name=upper('").append(tableName).append("')");
			sql.append(" and A.owner=upper('").append(conn.getMetaData().getUserName()).append("')");

			st = conn.createStatement();
			rs = st.executeQuery(sql.toString());
			while (rs.next()) {
				columns.add(rs.getString("column_name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(st);
		}
		return columns;
	}
}
