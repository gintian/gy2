package com.hjsj.hrms.utils.sso;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.admin.OnlineUserView;
import org.apache.commons.collections.FastHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单点登陆工具
 * @author guodd
 * Date: 2017-3-27
 */
public class SsoTool {

	HttpServletRequest request; 
	HttpServletResponse response;
	HttpSession session;
	
	public SsoTool(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
		this.session = request.getSession();
	}
	
	/**
	 * 一般情况下使用此类用于单点，需要传request和response。
	 * 有时仅需要使用内部方法，request和response不是必须的，此时创建对象
	 * 通过newInstance方法获取，防止使用new的方式创建类时多个构造函数造成开发人员混淆
	 */
	private SsoTool(){
	}
	
	/**
	 * 获取SsoTool对象，仅用于工具类。
	 * 此方法获取的SsoTool只能使用
	 * getEmployeeLogonInfo、getBusiAccountLogonInfo、getUserInfo、getBusiUserInfo
	 * 四个方法，因为其他方法依赖request和response
	 * @return
	 */
	public static SsoTool newInstance(){
		return new SsoTool();
	}
	
	
	/**
	 * 获取自助用户登陆账号密码
	 * @param value  查询值
	 * @param field  查询值对应A01字段，默认为认证用户名里设置的字段
	 * @param connectBusiUser 是否关联查询业务用户
	 * @return HashMap{username:"su",password:"123"}
	 */
	public HashMap getEmployeeLogonInfo(String value,String field,boolean connectBusiUser){
		return getEmployeeLogonInfo(value,field,connectBusiUser,null);
	}
	
	/**
	 * 获取自助用户登陆账号密码
	 * @param value  查询值
	 * @param field  查询值对应A01字段，默认为认证用户名里设置的字段
	 * @param connectBusiUser 是否关联查询业务用户
	 * @param nbase 指定人员库
	 * @return HashMap{username:"su",password:"123"}
	 */
	public HashMap getEmployeeLogonInfo(String value,String field,boolean connectBusiUser,String nbase){
		HashMap empInfo = null;
		Connection connection = null;
		RowSet rs = null;
		try{
			connection = AdminDb.getConnection();
			DbNameBo dbbo = new DbNameBo(connection);
			//获取用户名字段名
			String loginField = dbbo.getLogonUserNameField();
			//如果没有设置，默认登陆指标
			field = field==null||field.length()<1?loginField:field;
			//获取密码字段名
	   	    String pwdField = dbbo.getLogonPassWordField();
	   	    
	   	    List nbaselist = null;
	   	    if(nbase!=null && nbase.length()>0){
	   	    		nbaselist = new ArrayList();
	   	    		RecordVo vo = new RecordVo("dbname");
	   	    		vo.setString("pre", nbase);
	   	    		nbaselist.add(vo);
	   	    }else{
	   	    		//登陆认证人员库
	   	    		nbaselist = dbbo.getAllLoginDbNameList();
	   	    }
	   	    	
	   		List userList = new ArrayList();
			
			ContentDAO dao =new ContentDAO(connection);
			 //查询人员用户名和密码
			StringBuffer sqls = new StringBuffer();
			for (int i = 0; i < nbaselist.size(); i++) {
				RecordVo dbvo =  (RecordVo)nbaselist.get(i);
				sqls.append("select a0100,\'");
				sqls.append(dbvo.getString("pre"));
				sqls.append("\' nbase,");
				sqls.append(loginField);
				sqls.append(" username, ");
				sqls.append(pwdField);
				sqls.append(" password ");
				sqls.append(" from ");
				sqls.append(dbvo.getString("pre"));
				sqls.append("a01 where ");
				sqls.append(field);
				sqls.append(" =? ");
				userList.add(value);
				if (i<nbaselist.size()-1){
					sqls.append(" union all ");
				}
			}
			rs = dao.search(sqls.toString(),userList);
			
			String username = "";
			String pwd = "";
			nbase = "";
			String a0100 = "";
			
			if(!rs.next())
				return null;
			
			username = rs.getString("username");
			pwd = rs.getString("password");
			a0100 = rs.getString("a0100");
			nbase = rs.getString("nbase");
			
			//如果关联业务用户，使用业务用户身份登陆，查找业务用户账号密码
			if(connectBusiUser){
				rs = dao.search("select username,password from operuser where a0100 = '"+a0100+"' and nbase = '"+nbase+"'");
				if(rs.next()){
					username = rs.getString("username"); 
					pwd = rs.getString("password");
				}
			}
			
			empInfo = new HashMap();
			empInfo.put("username", username);
			// 密码如果加密需要脱密
			if (ConstantParamter.isEncPwd()) {
				 Des des = new Des();
				 pwd = des.DecryPwdStr(pwd);
			}
			empInfo.put("password", pwd);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(connection);
		}
		
		return empInfo;
	}
	
	
	/**
	 * 获取业务用户登陆信息
	 * @param username 用户名
	 * @return HashMap{username:"su",password:"123"}
	 */
	public HashMap getBusiAccountLogonInfo(String username){
		HashMap empInfo = null;
		Connection connection = null;
		RowSet rs = null;
		try{
			connection = AdminDb.getConnection();
			ContentDAO dao =new ContentDAO(connection);
			List userList = new ArrayList();
			
			String sql = "select username,password from operuser where username=?";
			userList.add(username);
			rs = dao.search(sql, userList);
			if(!rs.next()){
				return null;
			}
			
			empInfo = new HashMap();
			empInfo.put("username", rs.getString("username"));
			String pwd = rs.getString("password");
			// 密码如果加密需要脱密
			if (ConstantParamter.isEncPwd()) {
				Des des = new Des();
				pwd = des.DecryPwdStr(pwd);
			}
			empInfo.put("password", pwd);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(connection);
		}
		
		return empInfo;
	}
	
	private String getValidateCode(String username){
		// 如果登陆需要验证码，生成验证码
				String needVCode=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.VALIDATECODE);
				String validateCode = "";
				if ("true".equalsIgnoreCase(needVCode)) {
					/*try {
						int codelen = 6;
						//自定义附加码长度
						String	validatecodelen=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.VALIDATECODELEN);
						//自定义附加码长度	
						String	validatecodeinfo=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.VALIDATECODEINFO);
						if(validatecodelen!=null&&validatecodelen.length()>0)
						{
							codelen=Integer.parseInt(validatecodelen);
						}
						StringBuffer strSrc=new StringBuffer();
						if(validatecodeinfo!=null&&validatecodeinfo.length()>0)
						{
							strSrc.append(validatecodeinfo);
						}else {
							strSrc.append("QAZWSXEDCRFVTGBYHNUJMIKLP123456789");
						}
				        ServletUtilities.createValidateCodeImage(codelen,strSrc,session);
				        validateCode = (String) session.getAttribute("validatecode");
				        
					} catch (Exception e) {
						e.printStackTrace();
					}
					*/
					/*单点登陆 随便给个验证码，两边一致能通过登陆验证即可*/
					validateCode = "123456";
					//图片验证
					session.setAttribute("validatecode", validateCode);
					//短信或微信验证
					session.setAttribute("smsvcode", validateCode);
					session.setAttribute("smsvuser", username);
					long start = System.currentTimeMillis()/1000;
					session.setAttribute("smsvcode_time", String.valueOf(start));
					
				}
				return validateCode;
		
	}
	
	/**
	 * 获取验证码
	 * @return String 验证码
	 */
	public String getValidateCode(){
		return getValidateCode(null);
	}
	
	/**
	 * 清空此用户的登陆记录
	 * @param username 用户名
	 */
	public void cleanLogonMark(String username){
		FastHashMap hm = (FastHashMap)session.getServletContext().getAttribute("userNames");
		if(hm!=null){
				Object[] onlineviewArray = hm.values().toArray();
				if(onlineviewArray!=null && onlineviewArray.length>0){
					for(int i=0;i<onlineviewArray.length;i++){
						if(onlineviewArray[i]!=null){
							OnlineUserView vo = (OnlineUserView)onlineviewArray[i];
							if (username.equalsIgnoreCase(vo.getUserId())) 
							{
								if (!vo.getSession().getId().equalsIgnoreCase(session.getId())) 
								{
									vo.getSession().invalidate();
								}
							}
						}
					}
					
				}
		}
	}
	
	/**
	 * 登陆跳转
	 * @param logonUrl 登陆url
	 * @param username 用户名
	 * @param password 密码
	 */
	public void doLogon(String logonUrl,String username,String password){
		/*不踢掉原来的用户*/
		//this.cleanLogonMark(username);
		String validateCode = this.getValidateCode(username);
		try {
		
			//当request走HireKeywordFilter时会变成ParameterRequestWrapper对象，连接传参会失败，需要已此方式设置参数
			/**
			 * 新版本 走HireKeywordFilter的时候，会将request转为 ParameterRequestWrapper对象(ParameterRequestWrapper继承HttpServletRequestWrapper)
			 * 旧版本 走HireKeywordFilter没有转，并且包里没有ParameterRequestWrapper.java 文件
			 * 当request为ParameterRequestWrapper对象时，地址传参不起作用，需要通过ParameterRequestWrapper.addParameter方法添加参数
			 * 为了兼容旧版本可能不存在ParameterRequestWrapper文件，此处使用反射机制调用方法添加参数
			 */
			if("com.hjsj.hrms.servlet.ParameterRequestWrapper".equals(request.getClass().getName())){
					Method addMethod = request.getClass().getMethod("addParameter", String.class,String.class);
				    addMethod.invoke(request, "logon.x", "link");
				    addMethod.invoke(request, "username", username);
				    addMethod.invoke(request, "password", password);
				    addMethod.invoke(request, "validatecode", validateCode);
			}else{
				password = URLEncoder.encode(password,"UTF-8");
				logonUrl+="?logon.x=link&username="+username+"&password="+password+"&validatecode="+validateCode;
			}
			//添加单点标识
			session.setAttribute("isSSO", "1");
			request.getRequestDispatcher(logonUrl).forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 通过用户名、密码获取用户信息
	 * xus 17/5/24
	 * @param username
	 * @param pwd
	 * @param oth  其他查询字段 ,如果不需要传null
	 * @return
	 */
	public Map getUserInfo(String username,String pwd,String[] oth){
		Map info=null;
		Connection connection = null;
		RowSet rs = null;
		try{
			connection = AdminDb.getConnection();
			DbNameBo dbbo = new DbNameBo(connection);
			ContentDAO dao=new ContentDAO(connection);
			
			//设置了密码加密，对密码进行加密
			if (ConstantParamter.isEncPwd()) {
				Des des = new Des();
				pwd = des.EncryPwdStr(pwd);
			}
			
			//登陆字段
			String loginField = dbbo.getLogonUserNameField();
			//获取密码字段名
	   	    String pwdField = dbbo.getLogonPassWordField();
			RecordVo vo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE", connection);
			String phone="";
			if(vo!=null){
				 phone = vo.getString("str_value");
				 phone = phone!=null?phone:"";
			}
			String email="";
			vo = ConstantParamter.getConstantVo("SS_EMAIL", connection);
			if(vo!=null){
				 email=vo.getString("str_value");
				 email = email!=null?email:"";
			}
			
			StringBuffer otherFields = new StringBuffer();
			if(oth!=null){
				for(int i=0;i<oth.length;i++){
					if("a0101".equalsIgnoreCase(oth[i]))
						continue;
					otherFields.append(",").append(oth[i]);
				}
				otherFields.append(", '1' tail ");
			}
			ArrayList values =  new ArrayList();
			List nbaselist = dbbo.getAllLoginDbNameList();
			StringBuffer sql=new StringBuffer();
			for(int i=0;i<nbaselist.size();i++){
				Object obj= ((RecordVo) nbaselist.get(i)).getString("pre");
				String pre=obj.toString();
				sql.append("select a0101 fullname,");
				sql.append(phone).append(" phone,");
				sql.append(email).append(" email,");
				sql.append(" a0100,");
				sql.append(" '").append(pre).append("' nbase ");
				sql.append(otherFields);
				sql.append(" from ");
				sql.append(pre);
				sql.append("A01 where ");
				sql.append(loginField);
				sql.append(" = ?");
				sql.append(" and ");
				sql.append(Sql_switcher.isnull(pwdField, "''"));
				sql.append(" = ? ");
				values.add(username);
				values.add(pwd);
				if(i<nbaselist.size()-1)
					sql.append(" union ");
			}
			rs=dao.search(sql.toString(),values);
			if(!rs.next()){
				return null;
			}
			
			info = new HashMap();
			ResultSetMetaData meta =  rs.getMetaData();
			for(int i=meta.getColumnCount();i>0;i--){
				String name = meta.getColumnName(i);
				info.put(name, rs.getObject(name));
			}
			
		}catch(Exception e){
			return null;
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(connection);
		}
		return info;
	}
	
	/**
	 * 
	 * @param username
	 * @param pwd
	 * @param oth 其他查询字段,如果不需要传null
	 * @return
	 */
	public HashMap getBusiUserInfo(String username,String pwd,String[] oth){
		HashMap info = null;
		Connection connection = null;
		RowSet rs = null;
		try{
			connection = AdminDb.getConnection();
			ContentDAO dao =new ContentDAO(connection);
			List userList = new ArrayList();
			
			//设置了密码加密，对密码进行加密
			if (ConstantParamter.isEncPwd()) {
				Des des = new Des();
				pwd = des.EncryPwdStr(pwd);
			}
			
			StringBuffer otherFields = new StringBuffer();
			if(oth!=null){
				for(int i=0;i<oth.length;i++){
					if("a0100".equalsIgnoreCase(oth[i]) || "nbase".equalsIgnoreCase(oth[i]) || "phone".equalsIgnoreCase(oth[i])
							|| "fullname".equalsIgnoreCase(oth[i]) || "email".equalsIgnoreCase(oth[i]))
						continue;
					otherFields.append(",").append(oth[i]);
				}
				otherFields.append(", '1' tail ");
			}
			
			String sql = "select fullname,phone,email,a0100,nbase "+otherFields.toString()+" from operuser where username=? and password=?";
			userList.add(username);
			userList.add(pwd);
			rs = dao.search(sql, userList);
			if(!rs.next()){
				return null;
			}
			
			info = new HashMap();
			ResultSetMetaData meta =  rs.getMetaData();
			for(int i=meta.getColumnCount();i>0;i--){
				String name = meta.getColumnName(i);
				info.put(name, rs.getObject(name));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(connection);
		}
		return info;
	}
	
}
