package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sys.DbEncrypUtil;
import com.hrms.frame.dao.tool.DbSecurity;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import org.apache.log4j.Category;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *  特别注意：此类中的conn.createStatement()不能使用ContenDAO来替换，因为此类被ContenDAO调用
 * 
 * @author wangzj
 *
 *
 */
public class DbSecurityImpl implements DbSecurity 
{
	//log日志
	private static Category log = Category.getInstance(DbSecurityImpl.class.getName());
	
	private boolean flag = false;
	
	@Override
    public void closeWallet(Connection conn) {
		try {
			DbEncrypUtil util = DbEncrypUtil.getInstance();
			util.closeWallet(conn);
			log.debug("关闭Wallet成功");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("关闭wallet失败，失败原因：" + e.getMessage());
		}

	}

	@Override
    public boolean openWallet(Connection conn, String sql) {
		DbEncrypUtil util = DbEncrypUtil.getInstance();
		
		//根据sql判断是否需要打开wallet
		boolean flag = util.needOpenWallet(conn,sql);
		
		try {
		
			if (flag) {
				util.openWallet(conn);
				log.debug("打开Wallet成功");
			}
		}catch (Exception e) {
			e.printStackTrace();
			flag = false;
			log.error("打开wallet失败，失败原因：" + e.getMessage());
		}
		
		
		return flag;
	}
	
	/**
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 */
	@Override
    public boolean openWallet(Connection conn, List sql) {
		if (sql == null || sql.size() == 0) {
			return false;
		}
		
		DbEncrypUtil util = DbEncrypUtil.getInstance();
		
		//根据sql判断是否需要打开wallet
		boolean flag = false;
		
		for (int i = 0; i < sql.size(); i++) {
			flag = util.needOpenWallet(conn,(String)sql.get(i));
			
			if(flag) {
				break;
			}
		}
		
		try {
		
			if (flag) {
				util.openWallet(conn);
				log.debug("打开Wallet成功");
			}
		}catch (Exception e) {
			e.printStackTrace();
			flag = false;
			log.error("打开wallet失败，失败原因：" + e.getMessage());
		}
		
		
		return flag;
	}
	
	/**
	 * 打开wallet
	 * @param conn 
	 * @param sql String 需要执行的sql语句
	 * @return 是否已打开Wallet
	 */
	public boolean open(Connection conn, String sql){

		 String bwallet = SystemConfig.getPropertyValue("wallet");
		 if ("true".equalsIgnoreCase(bwallet)) {
		      try {
		        this.flag = openWallet(conn, sql);
		      } catch (Exception ex){
		      }

		 }
		 
		 return this.flag;
	}
	
	/**
	 * 根据表名加密表
	 * @param conn
	 * @param tableName
	 * @return boolean
	 */
	public  boolean encryptTableName(Connection conn, String tableName){
		
		if (conn == null || tableName == null) {
			log.debug("传递的参数conn 或 tableName为空，加密失败......" );
			return false;
		}
		boolean state = false;
		String bwallet = SystemConfig.getPropertyValue("wallet");
		if ("true".equalsIgnoreCase(bwallet)&&Sql_switcher.searchDbServer()==Constant.ORACEL) {//如果是orcale库并且设置了wallet=true才走进去  zhaoxg add 2015-12-15
			boolean flag = true;
			DbEncrypUtil util = DbEncrypUtil.getInstance();
			try {				
				if (flag) {
					util.openWallet(conn);
					log.debug("打开Wallet成功");
				}
			}catch (Exception e) {
				e.printStackTrace();
				flag = false;
				log.error("打开wallet失败，失败原因：" + e.getMessage());
			}
			ResultSet rs = null;
			Statement st = null;
			String sql = "";
		    sql = "select * from user_encrypted_columns";
		    try {
		    	st = conn.createStatement();
				rs=st.executeQuery(sql);
				String sectable ="";
				String seccol = "";
				List sectlisttab = new ArrayList();
				while(rs.next()){
					sectable = rs.getString("TABLE_NAME").toLowerCase();
					seccol = rs.getString("COLUMN_NAME");
					sectlisttab.add(sectable);
					}
//				if(sectlisttab.contains(tableName.toLowerCase())){//加密过还需验真字段是否全加密
//					state = util.encryptTable(conn, tableName);
//				}else{//没有加密过
					ConstantXml constantXml = new ConstantXml(conn,"DB_SECURITY","root");
					String rsydid = constantXml.getTextValue("/root/rsydid");//人事异动
					String gjhmcid = constantXml.getTextValue("/root/gjhmcid");//高级花名册id
				    String salaryid = constantXml.getTextValue("/root/salaryid"); //薪资id
				    String cyhmcid = constantXml.getTextValue("/root/cyhmcid");//常用花名册id
				    String reportid = constantXml.getTextValue("/root/reportid");//报表id
				    String tablename = constantXml.getTextValue("/root/tablename");//表名
				    if(rsydid!=null && !"".equals(rsydid)){//人事异动加密
						//按规则生成的人事异动的表  用户名templet_xxxx 、templet_xxxx、g_templet_xxxx
						if(rsydid.contains(",")){
							String[] rsydarray=rsydid.split(",");
							if(rsydarray.length>0){
								for(int i=0;i<rsydarray.length;i++){
									String rsydids = rsydarray[i];
									if(tableName.toLowerCase().startsWith("g_templet_")){
										int lastnum = tableName.lastIndexOf("_");
										String id = tableName.substring(lastnum,tableName.length());
										if(rsydids.equals(id)){
											state = util.encryptTable(conn, tableName);
											break;
										}
									}
									if(tableName.toLowerCase().lastIndexOf("_")==tableName.toLowerCase().indexOf("_")){
										int lastnum = tableName.lastIndexOf("_");
										String id = tableName.substring(lastnum+1,tableName.length());
										if(rsydids.equals(id)){
											state = util.encryptTable(conn, tableName);
											break;
										}
									}
								}
							}
						}
				    } if(gjhmcid!=null && !"".equals(gjhmcid)){
				    	if(gjhmcid.contains(",")){
							String [] gjhmcarr =  gjhmcid.split(",");
							if(gjhmcarr.length>0){
								for(int i=0;i<gjhmcarr.length;i++){
									String gjhmcids = gjhmcarr[i];
									if(tableName.toLowerCase().lastIndexOf("_")==tableName.toLowerCase().indexOf("_")+7){
										int lastnum = tableName.lastIndexOf("_");
										String id = tableName.substring(lastnum+1,tableName.length());
										if(gjhmcids.equals(id)){
											state = util.encryptTable(conn, tableName);
										}
									}	
								}	
							}
				        }
				    } if(salaryid!=null &&!"".equals(salaryid)){
						//用户名_salary_salaryid 、t#用户名_gz_Ins、t#用户名_gz_Dec、t#用户名_gz_Bd、t#用户名_gz
						if(salaryid.contains(",")){
							String[] salaryarr = salaryid.split(",");
							if(salaryarr.length>0){
								for(int i=0;i<salaryarr.length;i++){
									String salaryids = salaryarr[i];
									if(tableName.toLowerCase().lastIndexOf("_")==tableName.toLowerCase().indexOf("_")+7){
										int lastnum = tableName.lastIndexOf("_");
										String id = tableName.substring(lastnum+1,tableName.length());
										if(salaryids.equals(id)){
											state = util.encryptTable(conn, tableName);
										}
									}
									}
								}
							}
						if(tableName.toLowerCase().startsWith("t#") && tableName.toLowerCase().endsWith("_gz_ins")){
							state = util.encryptTable(conn, tableName);
						}
						if(tableName.toLowerCase().startsWith("t#") && tableName.toLowerCase().endsWith("_gz_dec")){
							state = util.encryptTable(conn, tableName);
						}
						if(tableName.toLowerCase().startsWith("t#") && tableName.toLowerCase().endsWith("_gz_bd")){
							state = util.encryptTable(conn, tableName);
						}
						if(tableName.toLowerCase().startsWith("t#") && tableName.toLowerCase().endsWith("_gz")){
							state = util.encryptTable(conn, tableName);
						}
					}
				     if(cyhmcid!=null &&!"".equals(cyhmcid)){//常用花名册
						//人员花名册:m花名册编号_用户名_人员库前缀(Usr,Oth…),单位花名册:m花名册编号_B,职位花名册:m花名册编号_K
				    	 List prelist = new ArrayList();
						 sql = "select Pre from dbName";
						 PubFunc.closeDbObj(rs);
						 rs=st.executeQuery(sql);
						 while(rs.next()){
								String pre = rs.getString("Pre");
								prelist.add(pre);
							}
						if(cyhmcid.contains(",")){
							String [] cyhmcarr = cyhmcid.split(",");
							if(cyhmcarr.length>0){
								for(int i=0;i<cyhmcarr.length;i++){
									String cyhmcids = cyhmcarr[i];
								    for(int k=0;k<prelist.size();k++){
								    	if(tableName.toLowerCase().endsWith(prelist.get(k).toString().toLowerCase())&&tableName.toLowerCase().startsWith("m")){
											String [] arr = tableName.substring(1,tableName.length()).split("_");
											if(arr[0].equals(cyhmcids)){
												state = util.encryptTable(conn, tableName);
											}
										}
								    }
									if(tableName.toLowerCase().endsWith("_b")&&tableName.toLowerCase().startsWith("m")){
										String [] arr = tableName.substring(1,tableName.length()).split("_");
										if(arr[0].equals(cyhmcids)){
											state = util.encryptTable(conn, tableName);
										}
									}
									if(tableName.toLowerCase().endsWith("_k")&&tableName.toLowerCase().startsWith("m")){
										String [] arr = tableName.substring(1,tableName.length()).split("_");
										if(arr[0].equals(cyhmcids)){
											state = util.encryptTable(conn, tableName);
										}
									}
									}
								}
							}
						} if(reportid!=null &&!"".equals(reportid)){//报表
							//tb表号  编辑报表统计结果表 tt_表号  BS报表汇总统计结果表 ta_表号  BS报表归档表
							if(reportid.contains(",")){
								String[] reportarr = reportid.split(",");
								List reportlist = new ArrayList();
								if(reportarr.length>0){
									for(int i=0;i<reportarr.length;i++){
										String reportids = reportarr[i];
										String tbid = "tb"+reportids;
										String tt_id = "tt_"+reportids;
										String ta_id = "ta_"+reportids;
										reportlist.add(tbid);
										reportlist.add(tt_id);
										reportlist.add(ta_id);
										}
									if(reportlist.contains(tableName.toLowerCase())){
										state = util.encryptTable(conn, tableName);
									}
								}
							}
						}
//				}
				}catch (Exception e) {
					e.printStackTrace();
				}finally {
					PubFunc.closeDbObj(rs);
					PubFunc.closeDbObj(st);
				}
			if (flag) {
				try {
					util.closeWallet(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return  state;
		} else {
			return true;
		}
	}
	/**
	 * 界面用户输入后执行加密表
	 * @param conn
	 * @param tableName
	 * @return
	 */
	public  boolean encryptTableName2(Connection conn, String tableName){
		if (conn == null || tableName == null) {
			log.debug("传递的参数conn 或 tableName为空，加密失败......" );
			return false;
		}
		boolean state = false;
		String bwallet = SystemConfig.getPropertyValue("wallet");
		if ("true".equalsIgnoreCase(bwallet)) {
			boolean flag = true;
			DbEncrypUtil util = DbEncrypUtil.getInstance();
			try {				
				if (flag) {
					util.openWallet(conn);
					log.debug("打开Wallet成功");
				}
			}catch (Exception e) {
				e.printStackTrace();
				flag = false;
				log.error("打开wallet失败，失败原因：" + e.getMessage());
			}
			try{
				state = util.encryptTable(conn, tableName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (flag) {
				try {
					util.closeWallet(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return  state;
		} else {
			return true;
		}
	}
	
	/**
	 * 关闭wallet
	 * @param conn
	 * @param flag 是否已经打开Wallet，也就是open函数的返回值
	 */
	public void close(Connection conn){
		 if (this.flag && conn != null) {
			 closeWallet(conn);
		 }
	}
	
	
	public static void main(String[] str) {
		
		DbSecurityImpl dbS = new DbSecurityImpl();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = AdminDb.getConnection();
			String sql = "select * from usra01";
			st = conn.createStatement();
			
			dbS.open(conn, sql);
			
			rs =st.executeQuery(sql);
			
			if (rs.next()) {
				System.out.println(rs.getString("a0101"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	

}
