package com.hjsj.hrms.service.core;

import com.hjsj.hrms.businessobject.sys.options.interfaces.ChangeInfoInterfaces;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * <p>Title:HrChangeInfoService.java</p>
 * <p>Description>:HrChangeInfoService.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 5, 2010 9:25:13 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class HrChangeInfoService implements HrChangeInfoServiceIntf{
    /**
     * 返回所有人员变动信息数据
     * @param changeFlag 更新标识0：新增；1：修改；2：删除
     * @return
     */
	public String getChangeUsers (String changeFlag,String username,String password)
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
   		   xml=infoInter.getChangeUsersXML(conn,changeFlag);
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
	public String getWhereChangeUsers (String whereStr,String username,String password)
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
	/**
	 * 校验operuser中的用户名密码
	 * @param hjusername
	 * @param password
	 * @return
	 */
	private boolean cheakCode(String hjusername,String password)
	{
		boolean isCorrect=false;
		Connection conn = null;	
		RowSet rs=null;
		try
   	    {
   		   conn=AdminDb.getConnection();   		   
   		   String sql="select 1 from operuser where username='"+hjusername+"' and password='"+password+"'";
   		   ContentDAO dao =new ContentDAO(conn);
   		   rs=dao.search(sql);
   		   if(rs.next())
   			  isCorrect=true;
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
}
