package com.hjsj.hrms.service.core;

import com.hjsj.hrms.businessobject.sys.options.interfaces.ChangeInfoInterfaces;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HrInfoservice {
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
    public String getAllUser(String username,String password)
    {
    	if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		String xml="";
        Connection conn = null;	
        //String whereStr=" nbase_0='LLZ' and e0122_0 like '100003%'";
        String whereStr="";
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
   		   xml=infoInter.getWhereChangeUsers(conn,whereStr);
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("获取人员信息错误",1,"");
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }   	   
		return xml;
    }
    /**
     * 时间需要带时分秒 yyyy-MM-dd HH:mm:ss
     * @param username
     * @param password
     * @param lastTime
     * @return
     */
    public String getChangeUsers(String username,String password,String lastTime)
    {
    	if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		if(lastTime==null||lastTime.length()<=0)
			return returnMessLog("传入的时间不能为空",1,"");
		if(!checkDateFormat(lastTime))
			return returnMessLog("传入的时间格式不正确,正确格式为yyyy-MM-dd HH:mm:ss",1,"");
		String xml="";
        Connection conn = null;				
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
   		   StringBuffer where=new StringBuffer();
   		   where.append(Sql_switcher.dateValue(lastTime)+"<=sDate");
   		   xml=infoInter.getWhereChangeUsers(conn, where.toString());
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("获取人员信息错误",1,"");
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
		return xml;
    }      
    /**
	 * 返回符合所带查询条件的人员变动信息数据
	 * @param whereStr 自定义SQL查询条件	
	 * @return
	 */
	public String getWhereChangeUsers(String whereStr,String username,String password)
	{
		if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		if(whereStr==null||whereStr.length()<=0)
			whereStr="";
		if(!checkWhere(whereStr))
		{
			return returnMessLog("传入的条件中有SQL注入危险语句，系统不能通过，请修改",1,"");
		}
		String xml="";
        Connection conn = null;				
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
   		   xml=infoInter.getWhereChangeUsers(conn, whereStr);
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("获取人员信息错误",1,"");
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
		return xml;
	}
	/**
	 * 获得HR业务系统所有的组织机构列
	 * @param username
	 * @param password
	 * @return
	 */
	public String getAllOrganizations(String username,String password)
	{
		if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		String xml="";
        Connection conn = null;				
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
   		   xml=infoInter.getWhereChangeOrganizations(conn, "");
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("获取机构信息错误",1,"");
   	    }finally
  	    {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
   	    return xml;
	}
/**
 * 获得HR业务系统所有指定时间以后的组织机构列
 * @param username
 * @param password
 * @param lastTime
 * @return
 */
	public String getChangeOrganizations(String username,String password,String lastTime)
	{
		if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		if(lastTime==null||lastTime.length()<=0)
			return returnMessLog("传入的时间不能为空",1,"");
		if(!checkDateFormat(lastTime))
			return returnMessLog("传入的时间格式不正确,正确格式为yyyy-MM-dd HH:mm:ss",1,"");
		String xml="";
        Connection conn = null;				
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
   		   StringBuffer where=new StringBuffer();
		   where.append(Sql_switcher.dateValue(lastTime)+"<=sDate");
   		   xml=infoInter.getWhereChangeOrganizations(conn, where.toString());
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("获取机构信息错误",1,"");
   	    }finally
  	    {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
   	    return xml;
	}
	/**
	 * 获得HR业务系统所有指定条件的组织机构列
	 * @param username
	 * @param password
	 * @param lastTime
	 * @return
	 */
	public String getWhereOrganizations(String whereStr,String username,String password)
	{
		if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		if(whereStr==null||whereStr.length()<=0)
			whereStr="";
		if(!checkWhere(whereStr))
		{
			return returnMessLog("传入的条件中有SQL注入危险语句，系统不能通过，请修改",1,"");
		}
		String xml="";
        Connection conn = null;				
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
   		   xml=infoInter.getWhereChangeOrganizations(conn, whereStr);
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("获取机构信息错误",1,"");
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
		return xml;
	}
	
	/**
	 * 获得HR业务系统所有的组织机构列
	 * @param username
	 * @param password
	 * @return
	 */
	public String getAllPost(String username,String password)
	{
		if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		String xml="";
        Connection conn = null;				
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
   		   xml=infoInter.getWhereChangePost(conn, "");
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("获取机构信息错误",1,"");
   	    }finally
  	    {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
   	    return xml;
	}
/**
 * 获得HR业务系统所有指定时间以后的组织机构列
 * @param username
 * @param password
 * @param lastTime
 * @return
 */
	public String getChangePost(String username,String password,String lastTime)
	{
		if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		if(lastTime==null||lastTime.length()<=0)
			return returnMessLog("传入的时间不能为空",1,"");
		if(!checkDateFormat(lastTime))
			return returnMessLog("传入的时间格式不正确,正确格式为yyyy-MM-dd HH:mm:ss",1,"");
		String xml="";
        Connection conn = null;				
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
   		   StringBuffer where=new StringBuffer();
		   where.append(Sql_switcher.dateValue(lastTime)+"<=sDate");
   		   xml=infoInter.getWhereChangePost(conn, where.toString());
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("获取机构信息错误",1,"");
   	    }finally
  	    {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
   	    return xml;
	}
	/**
	 * 获得HR业务系统所有指定条件的岗位机构列
	 * @param username
	 * @param password
	 * @return
	 */
	public String getWherePost(String whereStr,String username,String password)
	{
		if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		if(whereStr==null||whereStr.length()<=0)
			whereStr="";
		if(!checkWhere(whereStr))
		{
			return returnMessLog("传入的条件中有SQL注入危险语句，系统不能通过，请修改",1,"");
		}
		String xml="";
        Connection conn = null;				
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
   		   xml=infoInter.getWhereChangePost(conn, whereStr);
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("获取机构信息错误",1,"");
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
		return xml;
	}
    /**
	 * 校验operuser中的用户名密码
	 * @param hjusername
	 * @param password
	 * @return
	 */
	private boolean cheakCode(String hjusername,String password)
	{
		boolean isCorrect = false;
		Connection conn = null;	
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try
   	    {
   		   conn = AdminDb.getConnection();   		   
   		   String sql = "select 1 from operuser where upper(username)=? and password=? ";
   		   list.add(hjusername.toUpperCase());
   		   list.add(password);
   		   ContentDAO dao =new ContentDAO(conn);
   		   rs = dao.search(sql, list);
   		   if(rs.next())
   		   {
   			  isCorrect = true;
   		   }
   		   
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		  
   	    }finally
  	     {
  		    try{
  		    	if(rs!=null)
  		    		rs.close();
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
   	    return isCorrect;		
	}
	
	/**
	 * 返回已同步信息数据,更新同步中间表
	 * @param username 用户名
	 * @param password 密码
	 * @param xml 其他系统更新成功的记录
	 * @param onlyFiled 唯一指标名称，默认为unique_id
	 * @param sysFlag 系统标识，由eHR系统提供 OA、AD
	 * @param type "ORG" 代表机构，"HR"代表人员，"POST"代表岗位
	 * @return
	 */
	public String returnSynchroXml(String username,String password,
			String xml, String onlyFiled,String sysFlag, String type) 
	{
		String strInfo = "";
		if(username==null||username.trim().length()<=0||password==null||password.trim().length()<=0)
		{
			strInfo = "传入的校验用户名密码不能为空！";
			return strInfo;
		}
		boolean isCheck = cheakCode(username,password);
		if(!isCheck)
		{
			strInfo = "传入的用户名密码校验错误！";
			return strInfo;
		}
		boolean isCorrect = false;
        Connection conn = null;	
        ChangeInfoInterfaces infoInter = new ChangeInfoInterfaces();
		try
   	    {
   		   conn = AdminDb.getConnection();   		   
   		   isCorrect = infoInter.returnSynchroXml(conn,xml,onlyFiled,sysFlag,type);
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   strInfo = "同步信息时发生错误！";
  		   return strInfo;
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
   	    if(isCorrect)
   	    {
   	    	strInfo = "0";
			return strInfo;
   	    //	return returnMessLog("接收已同步信息数据更新成功",0,"");
   	    }  	    	
   	    else
   	    {
   	    	strInfo = "1";
			return strInfo;
   	    //	return returnMessLog("接收已同步信息数据更新失败",1,infoInter.getErrorMess());	
   	    }
	}
	/**
	 * 返回已同步信息数据（以方便，hr系统将已同步数据，同步标识更改为已同步，这样，一边下次检索时，过滤信息）
	 * @param xml
	 * <?xml version="1.0" encoding="GB2312"?>
     * <hr version="5.0">
     *  <element> 
     *   <nbase>Usr</ nbase >  <!—对应用户同步接收信息里的nbase人员库-->
     *   <a0100>00000001</ a0100><!—对应用户同步接收信息里的a0100,hr人员id-->
     *  </element > 
     * </hr>
	 * @return
	 */
	public String returnSynchroXml (String xml,String username,String password)
	{
		if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		boolean isCorrect=false;
        Connection conn = null;	
        ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
		try
   	    {
   		   conn=AdminDb.getConnection();   		   
   		   isCorrect= infoInter.returnSynchroUserXml(conn, xml);
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("同步人员信息时发生错误",1,"");
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
   	    if(isCorrect)
   	    	return returnMessLog("接收已同步信息数据更新成功",0,"");
   	    else
   	    	return returnMessLog("接收已同步信息数据更新失败",1,infoInter.getErrorMess());
		//return isCorrect;
	}
	/**
	 * 数组有hr系统人员库和hr系统id组成的一个一维数组[usr0000001，usr0000002，usr0000003，。。。。]
	 * @param arrayString 包含的属性为：nbase+a0100
	 * @return
	 */
	public String returnSynchroArray (String[] arrayString,String username,String password)
	{
		if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		boolean isCorrect=false;
        Connection conn = null;	
        ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   
   		   isCorrect=infoInter.returnSynchroArray(conn, arrayString);
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("同步人员信息时发生错误",1,"");
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
   	    if(isCorrect)
	    	return returnMessLog("接收已同步信息数据更新成功",0,"");
	    else
	    	return returnMessLog("接收已同步信息数据更新失败",1,infoInter.getErrorMess());		
	}
	/**
	 * 
	 * @param strString 数组有hr系统人员库和hr系统id组成的String字符串由逗号隔开usr0000001，usr0000002，usr0000003，。。。。
	 * @param username
	 * @param password
	 * @return
	 */
	public String returnSynchroString (String strString,String username,String password)
	{
		if(username==null||username.length()<=0||password==null||password.length()<=0)
		{
			return returnMessLog("传入的校验用户名密码不能为空！",1,"");
		}
		boolean isCheck=cheakCode(username,password);
		if(!isCheck)
		{
			return returnMessLog("传入的校验用户名密码错误",1,"");
		}
		boolean isCorrect=false;
        Connection conn = null;	
        ChangeInfoInterfaces infoInter=new ChangeInfoInterfaces();
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   if(strString!=null&&strString.length()>0)
   		   {
   			   String []arrayString=strString.split(",");
    		   isCorrect=infoInter.returnSynchroArray(conn, arrayString);
   		   }else
   		   {
   			  return returnMessLog("同步人员信息时发生错误,同步信息不能为空！",1,"");
   		   }
   		   
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("同步人员信息时发生错误",1,"");
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
   	    if(isCorrect)
	    	return returnMessLog("接收已同步信息数据更新成功",0,"");
	    else
	    	return returnMessLog("接收已同步信息数据更新失败",1,infoInter.getErrorMess());		
	}
	private String returnMessLog(String mess,int flag,String errorElementStr)
	{
		StringBuffer strxml=new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312' ?>");
		strxml.append("<hr version=\"5.0\">");
		strxml.append("<title>人力资源系统</title>");
		strxml.append("<language>zh-cn</language>");
		if(flag==0)
		   strxml.append("<mess>"+mess+"</mess>");
		else if(flag==1)
		   strxml.append("<error>"+mess+"</error>");	
		if(errorElementStr!=null&&errorElementStr.length()>0)
			strxml.append(errorElementStr);
		strxml.append("</hr>");		
		return strxml.toString();
	}
	/**
	 * 校验时间格式
	 * @param checkValue
	 * @return
	 */
	private boolean checkDateFormat(String checkValue)
	{
		 if(checkValue==null||checkValue.length()<=0)
			 return false;
	        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");   
	        Date d = null;   
	        if(checkValue != null && !"".equals(checkValue))
	        {   
	            if(checkValue.split("/").length > 1)   
	            {   
	                dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");   
	            }   
	            if (checkValue.split("-").length > 1)   
	            {   
	                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
	            } 
	            if (checkValue.split(".").length > 1)   
	            {   
	                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");   
	            } 
	        }else  
	        {   
	            return false;   
	        }   
	        try  
	        {   
	            d = dateFormat.parse(checkValue);   
	            //System.out.println(d);   
	        }   
	        catch(Exception e)   
	        {   
	            //System.out.println("格式错误");   
	            return false;   
	        }   
	        String eL= "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-9]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";   
	        Pattern p = Pattern.compile(eL);    
	        Matcher m = p.matcher(checkValue);    
	        boolean b = m.matches();   
	        if(b)   
	        {   
	            //System.out.println("格式正确"); 
	        	return true;  
	        }   
	        else  
	        {   
	            //System.out.println("格式错误"); 
	        	return false;  
	        }  
	}
	private boolean checkWhere(String where)
	{
		String str = where.toLowerCase();
	    if (str.indexOf("select") > -1 && str.indexOf("from") > -1) return false;   
	    if (str.indexOf("where") > -1 && str.indexOf("=") > -1) return false;   
	    if (str.indexOf("update") > -1 && str.indexOf("set") > -1) return false;   
	    if (str.indexOf("delete") > -1 && str.indexOf("from") > -1) return false;   
	    if (str.indexOf("insert") > -1 && str.indexOf("into") > -1) return false;   
	    if (str.indexOf("create") > -1 && str.indexOf("table") > -1) return false;   
	    if (str.indexOf("create") > -1 && str.indexOf("view") > -1) return false;   
	    if (str.indexOf("create") > -1 && str.indexOf("proc") > -1) return false;   
	    if (str.indexOf("drop") > -1 && str.indexOf("table") > -1) return false;   
	    if (str.indexOf("drop") > -1 && str.indexOf("view") > -1) return false;   
	    if (str.indexOf("drop") > -1 && str.indexOf("proc") > -1) return false;   
	    if (str.indexOf("alter") > -1 && str.indexOf("table") > -1) return false;   
	    if (str.indexOf("alter") > -1 && str.indexOf("view") > -1) return false;   
	    if (str.indexOf("alter") > -1 && str.indexOf("proc") > -1) return false;   
	    if (str.indexOf("exec") > -1 && str.indexOf("master") > -1) return false;   
	    if (str.indexOf("exec") > -1 && str.indexOf("xp_cmdshell") > -1) return false;   
	  
         return true;
	}
	public String checkLogonUser(String username,String password,String validatepwd)
	{
		Connection conn = null;	
        boolean istrue=true;
		try
   	    {
   		   conn=AdminDb.getConnection();
   		   UserView userView=null;
   		   if(validatepwd!=null&& "false".equals(validatepwd))
 		       userView=new UserView(username,conn);
 	       else
 	           userView=new UserView(username,password,conn);	
   		  if(!userView.canLogin())
   			istrue=false;
   		  if(istrue)
   		  {
   			return "<return><code>1</code><message>验证成功</message></return>";
   		  }
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		   return returnMessLog("获取人员信息错误",1,"");
   	    }finally
  	     {
  		    try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }   	   
		return "<return><code>0</code><message>验证失败</message></return>";
	}
}
