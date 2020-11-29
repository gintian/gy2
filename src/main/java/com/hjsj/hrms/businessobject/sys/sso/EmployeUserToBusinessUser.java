/**
 * File：EmployeUserToBusinessUser.java
 * Author:wangzhongjun
 * Create time：2004-02-02
 * 维护人员：
 * 维护日期：
 * 维护原因：
 * 
 */

package com.hjsj.hrms.businessobject.sys.sso;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.admin.VerifyUser;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title:EmployeUserToBusinessUser
 * </p>
 * <p>
 * Description:单点登录，由第三方软件验证用户有效性。
 * 当用户为自助用户时，如果有业务用户与该用户关联时，则使用业务用户登录；
 * 没有业务用户与之关联，则使用原用户名登录。
 * 当用户为业务用户时，使用原用户登录。
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-10-18
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class EmployeUserToBusinessUser implements VerifyUser{

	/**登录用户名*/
	private String userId;
	
	/**是否需要调试*/
	private boolean debug = false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		EmployeUserToBusinessUser u = new EmployeUserToBusinessUser();
		u.debug = true;
		u.isExist("su", "");
		
	}

	/**
	 * 供外部类获得用户名
	 */
	@Override
    public String getUserId() {
		return userId;
	}

	/**
	 * 实现接口方法，
	 * 如果自助用户与业务用户关联，则将自主用户名转为业务用户名进行登录
	 */
	@Override
    public boolean isExist(String userName, String passWord) {
		if (this.debug) {
			System.out.println("开始执行isExist方法,用户名："+userName + "，密码："+ passWord);
		}
		
		// 用户是否存在
		boolean flag = false;
		
		Connection conn = null;
		
		try {
			conn = AdminDb.getConnection();
			
			if (this.debug) {
				System.out.println("获得数据库连接：" + conn.toString());
			}
			
			if (isExistBusinessUser(userName, passWord, conn)) {// 业务用户
				this.userId = userName;
				UserView userView=new UserView(this.userId, conn);
				
				if(userView.canLogin()){
					flag = true;
					if (this.debug) {
						System.out.println(this.userId + "用户登录成功");
					}
					
				} else {
					flag = false;
					if (this.debug) {
						System.out.println(this.userId + "用户登录失败");
					}
				}
			} else {//自助用户
			
				// 获得认证应用库
				List list = this.getCertificationDbList();
				
				if (this.debug) {
					System.out.println("获得认证应用库个数：" + list.size());
				}
				
				if (list.size() == 0) {
					flag  = false;
					if (this.debug) {
						System.out.println("获得认证应用库个数为0，登录失败");
					}	
				}
				
				// 查询自助用户是否存在
				List userList = isExistEmployeUser(userName, passWord, list, conn);
				if (userList.size() == 2) {
					flag = true;
					this.userId = userName;
					
					List businessUser = getBusinessUser(userName, userList, conn);
					if (businessUser.size() == 2) {
						this.userId = businessUser.get(0).toString();
						passWord = businessUser.get(1).toString();
					}
					
					UserView userView=new UserView(this.userId, passWord, conn);
					userView.setBcheckpwd(true);
					
					
					if(userView.canLogin()){
						if (this.debug) {
							System.out.println(this.userId + "用户登录成功");
						}
						flag = true;
					} else {
						flag = false;
						if (this.debug) {
							System.out.println(this.userId + "用户登录失败");
						}
					}
				} else {// 此用户为非法用户			
					flag = false;
					this.userId = userName;
					
					if (this.debug) {
						System.out.println(this.userId + "用户不存在");
					}
	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (this.debug) {
			System.out.println("isExist方法执行完毕，最后返回结果" + flag);
		}
		
		return flag;
	}
	
	/**
	 * 获得关联的业务用户名
	 * @param userName
	 * @param userList
	 * @return
	 */
	private List getBusinessUser(String userName, List userList, Connection conn) {
		List list = new ArrayList();
		String businessUser = "";
		StringBuffer buff = new StringBuffer();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			buff.append("select UserName,password from OperUser where upper(Nbase)=? and A0100=?");
			
			if (this.debug) {
    			System.out.println("查询关联用户的sql：" + buff.toString());
    		}
	    	
	    	rs = dao.search(buff.toString(),userList);
	    	if (rs.next()) {
	    		list.add(rs.getString("username"));
	    		String password = rs.getString("password");
	    		// 加密的密码需要脱密
	    		if (ConstantParamter.isEncPwd()) {
	    			if (this.debug) {
		    			System.out.println(rs.getString("username") + "用户密码为密文：" + password);
		    		}
	    			Des des = new Des();
	    			password = des.DecryPwdStr(password);
	    			
	    			if (this.debug) {
		    			System.out.println(rs.getString("username") + "用户密码密文解密后的明文：" + password);
		    		}
				 }
	    		
	    		list.add(password);
	    		
	    		if (this.debug) {
	    			System.out.println("与"+userName+"用户关联的业务用户为" + list.get(0).toString() + ",密码为：" + password);
	    		}
	    	} else {
	    		if (this.debug) {
	    			System.out.println("没有与"+userName+"用户关联的业务用户");
	    		}
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				
			} catch (Exception e) {
				
			}
		}
		
		return list;
	}
	
	/**
	 * 是否存在此自助用户，如果存在，则返回他们的应用库和人员标号
	 * @param userName
	 * @return
	 */
	private List isExistEmployeUser(String userName, String passWord, List dbList, Connection conn) {
		
		if (this.debug) {
			System.out.println("开始执行isExistEmployeUser方法，查询是否存在此自助用户");
		}
		
		List list = new ArrayList();
		StringBuffer buff = new StringBuffer();
		
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			buff.append("select nbase,a0100 ");
			buff.append(" from (");
			List userList = new ArrayList();
			
	    	for (int i = 0; i < dbList.size(); i++) {
	    		buff.append("select '");
	    		buff.append(dbList.get(i).toString());
	    		buff.append("' nbase,a0100 ");
	    		buff.append(" from ");
	    		buff.append(dbList.get(i).toString());
	    		buff.append("A01 where upper(");
	    		buff.append(getUserField());
	    		buff.append(")=? and upper(");
	    		buff.append(this.getPassWordField());
	    		buff.append(")=? ");
	    		userList.add(userName.toUpperCase());
	    		if (ConstantParamter.isEncPwd()) {
	    			Des des = new Des();
	    			userList.add(des.EncryPwdStr(passWord).toUpperCase());
	    		} else {
	    			userList.add(passWord.toUpperCase());
	    		}
	    		 
	    		 if (i != dbList.size() - 1) {
	    			 buff.append(" union all ");
	    		 }
	    	 }
		 
	    	buff.append(") bb ");
	    	
	    	if (this.debug) {
				System.out.println("查询是否存在自助用户的sql语句：" + buff.toString());
			}
	    	
	    	rs = dao.search(buff.toString(),userList);
	    	if (rs.next()) {
	    		list.add(rs.getString("nbase").toUpperCase());
	    		list.add(rs.getString("a0100"));
	    		
	    		if (this.debug) {
	    			System.out.println("存在"+userName+"自助用户:a0100=" + rs.getString("a0100") + ",nbase=" + rs.getString("nbase"));
	    		}
	    	} else {
	    		if (this.debug) {
	    			System.out.println("不存在"+userName+"自助用户");
	    		}
	    	}
		} catch (Exception e) {
			e.printStackTrace();
			if (this.debug) {
    			System.out.println("查询自助用户"+userName+"出现异常");
    		}
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				
			}
		}
		
		if (this.debug) {
			System.out.println("isExistEmployeUser方法执行完毕");
		}
		
		return list;
	}
	
	/**
	 * 是否存在此自助用户，如果存在，则返回他们的应用库和人员标号
	 * @param userName
	 * @return
	 */
	private boolean isExistBusinessUser(String userName, String passWord, Connection conn) {
		
		if (this.debug) {
			System.out.println("开始执行isExistBusinessUser方法，查询是否为业务用户");
		}
		boolean flag = false;
		List list = new ArrayList();
		StringBuffer buff = new StringBuffer();
		
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			buff.append("select * ");
			buff.append(" from  operuser where upper(username)=? and upper(password)=?");
			
			List userList = new ArrayList();
	    	userList.add(userName.toUpperCase());
    		if (ConstantParamter.isEncPwd()) {
    			Des des = new Des();
    			userList.add(des.EncryPwdStr(passWord).toUpperCase());
    		} else {
    			userList.add(passWord.toUpperCase());
    		}
	    	
	    	if (this.debug) {
				System.out.println("查询是否存在业务用户的sql语句：" + buff.toString());
			}
	    	
	    	rs = dao.search(buff.toString(),userList);
	    	if (rs.next()) {
	    		flag = true;
	    		
	    		if (this.debug) {
	    			System.out.println("存在"+userName+"业务用户");
	    		}
	    	} else {
	    		if (this.debug) {
	    			System.out.println("不存在"+userName+"业务用户");
	    		}
	    	}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
			if (this.debug) {
    			System.out.println("查询业务用户"+userName+"出现异常");
    		}
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				
			}
		}
		
		if (this.debug) {
			System.out.println("isExistBusinessUser方法执行完毕,返回结构" + flag);
		}
		
		return flag;
	}
	
	/**
	 * 获得登录用户名字段
	 * @return
	 */
	private String getUserField() {
		RecordVo user_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
	    String fielduser=user_vo.getString("str_value");
	    
	    if (this.debug) {
			System.out.println("常量表中设置登录用户名密码字段为：" + fielduser);
		}
	    
	    //验证用户名和密码字段
	    String Userfield="username";
		if(fielduser != null && fielduser.indexOf(",")>0 && fielduser.indexOf("#") == -1) {
			 Userfield=fielduser.substring(0,fielduser.indexOf(","));
		}
		
		if (this.debug) {
			System.out.println("登录用户名字段为：" + Userfield);
		}
		
		return Userfield;
	}
	
	/**
	 * 获得登录用户名字段
	 * @return
	 */
	private String getPassWordField() {
		RecordVo user_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		String fielduser=user_vo.getString("str_value");
	    
	    if (this.debug) {
			System.out.println("常量表中设置登录用户名密码字段为：" + fielduser);
		}
	    
	    //验证用户名和密码字段
	     String PWfield="UserPassword";
		 if(fielduser !=null && fielduser.indexOf(",")>0  && fielduser.indexOf("#")==-1)
		 {
			 PWfield=fielduser.substring(fielduser.indexOf(",")+1);
		 }
		
		if (this.debug) {
			System.out.println("登录密码字段为：" + PWfield);
		}
		
		return PWfield;
	}
	
	/**
	 * 获得认证应用库
	 * @return List<String>
	 */
	private List getCertificationDbList() {
		List list = new ArrayList();
		
		// 从常量表中获得设置的认证应用库
		RecordVo user_vo=ConstantParamter.getConstantVo("SS_LOGIN");
	    String dbStr = user_vo.getString("str_value");
	    
	    if (this.debug) {
			System.out.println("常量表中设置的认证应用库为：" + dbStr);
		}
	    
	    if (dbStr != null && dbStr.length() > 0) {
	    	String[] str = dbStr.split(",");
	    	for (int i = 0; i < str.length; i++) {
	    		if (str[i] != null && str[i].length() > 0) {
	    			list.add(str[i]);
	    		}
	    	}
	    } 
	    
	    if (this.debug) {
			System.out.println("常量表中设置的认证应用库共：" + list.size() + "个");
		}
	    
		return list;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
