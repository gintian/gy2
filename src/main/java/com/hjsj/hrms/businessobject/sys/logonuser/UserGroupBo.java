/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.logonuser;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:</p>
 * <p>Description:用户组业务对象</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-7:15:03:39</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class UserGroupBo {
	
	private Connection conn;
	/**数据库操作对象类*/
	private ContentDAO dao;
	/**
	 * @param conn
	 * @param vo
	 */
	public UserGroupBo(Connection conn) {
		this.conn=conn;
		dao=new ContentDAO(this.conn);
	}
	
	private synchronized int getMaxId(){
		int nmax=1;
		String strsql="select max(groupid) as nmax from usergroup";
		//RowSet rset=null;
		ResultSet rset=null;
		try
		{
			rset=dao.search(strsql);
			if(rset.next()) {
                nmax=rset.getInt(nmax)+1;
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		/*
		finally
		{
        	try
        	{
        		if(rset!=null)
        			rset.close();
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}			
		}
		*/
		return nmax;
	}
	
	/**
	 * 更新用户名称描述
	 * @param vo
	 * @throws GeneralException
	 */
	public void updateUserGroupName(RecordVo vo)throws GeneralException
	{
		if(!"usergroup".equalsIgnoreCase(vo.getModelName())) {
            throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.parameter.type")));
        }
		try
		{
			dao.updateValueObject(vo);
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 根据组取得对应用用户表中的用户
	 * @param vo
	 * @return
	 */
	private List getUserListByGroupId(int groupid)
	{
		ArrayList list=new ArrayList();
		String strsql="select username from operuser where roleid=0 and groupid="+groupid;
		RowSet rset=null;
		try
		{
			rset=dao.search(strsql);
			while(rset.next()) {
                list.add(rset.getString("username"));
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		/*
		finally
		{
        	try
        	{
        		if(rset!=null)
        			rset.close();
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}   			
		}
		*/
		return list;
	}
	/**
	 * 取得对应用户组对象列表
	 * @param groupid
	 * @return
	 */
	private List getGroupsList(int groupid)
	{
		ArrayList list=new ArrayList();
		String strsql="select groupid,groupname from usergroup where groupname in (select username from operuser where roleid=1 and groupid="+groupid+")";
		RowSet rset=null;
		try
		{
			rset=dao.search(strsql);
			while(rset.next())
			{
				RecordVo vo=new RecordVo("usergroup");
				vo.setInt("groupid",rset.getInt("groupid"));
				vo.setString("groupname",rset.getString("groupname"));
				list.add(vo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		/*
		finally
		{
        	try
        	{
        		if(rset!=null)
        			rset.close();
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}   			
		}
		*/
		return list;	
	}
	/**
	 * 删除用户组
	 * @param vo
	 * @throws GeneralException
	 */
	public void removeUserGroup(RecordVo vo)throws GeneralException
	{
		if(!"usergroup".equalsIgnoreCase(vo.getModelName())) {
            throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.parameter.type")));
        }
		try
		{
			dao.deleteValueObject(vo);
			/**删除operuser对应的记录*/
			RecordVo user_vo=new RecordVo("operuser");
			user_vo.setString("username",vo.getString("groupname"));
			/**删除授权记录*/
			RecordVo priv_vo=new RecordVo("t_sys_function_priv");
			priv_vo.setString("id",vo.getString("groupname"));
			priv_vo.setInt("status",0);
			dao.deleteValueObject(priv_vo);
			/**删除组下用户*/
			UserObjectBo userobj=new UserObjectBo(this.conn);
			userobj.remove_User(user_vo,false);
			int groupid=vo.getInt("groupid");			
			List list=getUserListByGroupId(groupid);
			for(int i=0;i<list.size();i++)
			{
				RecordVo temp_vo=new RecordVo("operuser");
				temp_vo.setString("username",(String)list.get(i));
				userobj.remove_User(temp_vo,true);
			}
			/**删除组下的组*/

			list=getGroupsList(groupid);
			for(int i=0;i<list.size();i++)
			{
				RecordVo temp=(RecordVo)list.get(i);
				removeUserGroup(temp);				
			}
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}	
	/**
	 * 保存用户组对象 vo.setStrig("groupname","xxxx");
	 * @param vo
	 * @param parent 
	 * @throws GeneralException
	 */
	public void add_UserGroup(RecordVo vo,int parent_id) throws GeneralException
	{
		if(vo==null) {
            throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.object.null")));
        }
		if(!"usergroup".equalsIgnoreCase(vo.getModelName())) {
            throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.parameter.type")));
        }
		if(isExist(vo.getString("groupname"))) {
            throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.usergroup.exist")));
        }
		try
		{
			/**save group */
			vo.setInt("groupid",getMaxId());
			dao.addValueObject(vo);
			/**save operuser*/
			RecordVo uservo=new RecordVo("operuser");
			uservo.setString("username",vo.getString("groupname"));
			uservo.setInt("roleid",1);
			uservo.setInt("groupid",parent_id);
			uservo.setInt("photoid",23);
			UserObjectBo userobj=new UserObjectBo(this.conn);
			userobj.add_User(uservo,false);
			
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 分析用户是否存在,和登记用户库中指定的
	 * 登记账号和operuser中已存的用户进行对比对析?自助平台的用户和业务平台的用户
	 * 是否合起来考滤？
	 * @param user_value
	 * @return
	 */
	private boolean isExist(String user_value)
	{
      StringBuffer strsql=new StringBuffer();
      boolean bflag=false;             
        strsql.append(" select groupid from usergroup where groupname='");
        strsql.append(user_value);
        strsql.append("'");
        ContentDAO dao=new ContentDAO(conn);
        RowSet rset=null;
        try
        {
        	rset=dao.search(strsql.toString());
             if(rset.next()){
                 bflag=true;
             }
        }
        catch(Exception sqle)
        {
            sqle.printStackTrace();
        }
        finally
        {
        	try
        	{
        		if(rset!=null) {
                    rset.close();
                }
        	}
        	catch(Exception exx)
        	{
        		exx.printStackTrace();
        	}
        }
        return bflag;

	}
	/**
	 * 
	 * @Title: upd_group   
	 * @Description: 修改用户组方法   
	 * @param @param newname
	 * @param @param oldname
	 * @param @throws GeneralException 
	 * @return void    
	 * @throws
	 */
	public void upd_group(String newname,String oldname) throws GeneralException
	{
		if(isExist("newname")){
			throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.usergroup.exist")));
		} else {
			try
			{
				String sql = "";
				sql = "update usergroup set groupname = '"+newname+"' where groupname = '"+oldname+"'";
				String sql2 = "update operuser set username = '"+newname+"' where username = '"+oldname+"'";
				dao.update(sql2);
				dao.update(sql);
			}
			catch(Exception ex)
			{
				throw GeneralExceptionHandler.Handle(ex);
			}
		}
	}
}
