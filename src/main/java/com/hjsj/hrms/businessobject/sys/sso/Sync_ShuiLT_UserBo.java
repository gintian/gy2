package com.hjsj.hrms.businessobject.sys.sso;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class Sync_ShuiLT_UserBo {
	private  Connection conn=null;
	private ContentDAO dao=null;
	private String groupid="1";	
	private HashMap groupMap=new HashMap();
	private ArrayList sync_insert_list=new ArrayList();
	private ArrayList sync_del_list=new ArrayList();
    public Sync_ShuiLT_UserBo(Connection conn)throws GeneralException
    {
    	this.conn=conn;
    	this.dao=new ContentDAO(this.conn);    	
    }   
    /**
     * 同步添加
     * @param  linkfield operuser中的字段，暂时UserName
     * @return 返回list集合里包含list中间存放的是登陆名和用户组id
     */ 
    public ArrayList syncInsert(Connection hr_conn)throws GeneralException
    {
    	getUserGroup(hr_conn);
    	StringBuffer sql=new StringBuffer();
    	sql.append("select EMPLOYEE_OPERATIONNAME,DEPARTMENT_SHORTDN from Office_EmpCompetence A");
    	sql.append(" where A.OPERATION_TYPE='00' and A.EMPLOYEE_ISSYNCHRON='0' ");
    	ArrayList list=new ArrayList();
    	try
    	{
    		RowSet rs=this.dao.search(sql.toString()); 
    		String groupName="";
    		String gid="";
    		ArrayList uplist=new ArrayList();
    		while(rs.next())
    		{
    			ArrayList olist=new ArrayList();  
    			ArrayList ouplist=new ArrayList();  
    			groupName=rs.getString("DEPARTMENT_SHORTDN")!=null&&rs.getString("DEPARTMENT_SHORTDN").length()>0?rs.getString("DEPARTMENT_SHORTDN"):"";
    		    if(groupName!=null&&groupName.length()>0)
    		    {
    		    	gid=(String)this.groupMap.get(groupName);
    		    	if(gid==null||gid.length()<=0)
    		    	{
    		    		gid=adddUserGroup(hr_conn,groupName);    		    		
    		    	}
    		    }else
    		    {
    		    	gid=this.groupid;
    		    }
    		    olist.add(rs.getString("EMPLOYEE_OPERATIONNAME"));
    		    olist.add(new Integer(gid));
    		    list.add(olist);
    		    ouplist.add(rs.getString("EMPLOYEE_OPERATIONNAME"));
    		    uplist.add(ouplist);
    		}   
    		this.sync_insert_list=uplist;
    		//signSync(uplist);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		//throw GeneralExceptionHandler.Handle(e);
    	}
    	return  list;
    }
    /**
     * 同步删除
     * @param linkfield operuser中的字段，暂时UserName
     * @return 返回list集合里包含list中间存放的是登陆名
     */   
    public ArrayList syncDelete()throws GeneralException
    {
    	StringBuffer sql=new StringBuffer();
    	sql.append("select EMPLOYEE_OPERATIONNAME,DEPARTMENT_NAME from Office_EmpCompetence A");
    	sql.append(" where A.OPERATION_TYPE='01' and A.EMPLOYEE_ISSYNCHRON='0' ");
    	//sql.append(" and not exists (select * from operuser B where A.EMPLOYEE_OPERATIONNAME=B.UserName )");
    	ArrayList list=new ArrayList();
    	try
    	{
    		RowSet rs=this.dao.search(sql.toString()); 
    		ArrayList uplist=new ArrayList();
    		while(rs.next())
    		{
    			ArrayList olist=new ArrayList();  
    			ArrayList ouplist=new ArrayList();  
    			olist.add(rs.getString("EMPLOYEE_OPERATIONNAME"));
    		    list.add(olist);
    		    ouplist.add(rs.getString("EMPLOYEE_OPERATIONNAME"));
    		    uplist.add(ouplist);
    		}   
    		//signSync(uplist);
    		this.sync_del_list=uplist;
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return  list;
    	
    }
    /**
     * 标记同步为1
     * @param list
     */
    public void signSync(ArrayList list)throws GeneralException
    {
    	String updateSQL="update Office_EmpCompetence set EMPLOYEE_ISSYNCHRON='1' where EMPLOYEE_OPERATIONNAME=?";
    	try
    	{
    		dao.batchUpdate(updateSQL,list);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
    /**
     * 取得用户组
     */
    private void getUserGroup(Connection hr_conn)throws GeneralException
    {
    	try
    	{
    		String sql="select  GroupId,GroupName from UserGroup";    	
    		ContentDAO dao=new ContentDAO(hr_conn);
    		RowSet rs=dao.search(sql);
    		int i=0;
    		while(rs.next())
    		{
    			groupMap.put(rs.getString("GroupName"),rs.getString("GroupId"));
    			if(i==0) {
                    groupid=rs.getString("GroupId");
                }
    			i++;
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    }
    /**
     * 增加用户组
     * @param GroupName
     */
    private String adddUserGroup(Connection hr_conn,String GroupName)throws GeneralException
    {
    	String sql="select max(GroupId) as GroupId from UserGroup";
    	int id=0;
    	ContentDAO dao=new ContentDAO(hr_conn);
    	try
    	{   RowSet rs=dao.search(sql);
    		if(rs.next())
    		{
    			id=rs.getInt("GroupId");
    		}
    		id=id+1;
    		sql="insert into UserGroup(GroupId,GroupName) values("+id+",'"+GroupName+"')";
    		dao.insert(sql, new ArrayList());
    		sql="insert into OperUser(UserName,photoid,GroupID,RoleID,state) values('"+GroupName+"',23,1,1,1)";
    		dao.insert(sql, new ArrayList());
    		groupMap.put(GroupName,id+"");
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	return id+"";
    }
	public ArrayList getSync_insert_list() {
		return sync_insert_list;
	}
	public void setSync_insert_list(ArrayList sync_insert_list) {
		this.sync_insert_list = sync_insert_list;
	}
	public ArrayList getSync_del_list() {
		return sync_del_list;
	}
	public void setSync_del_list(ArrayList sync_del_list) {
		this.sync_del_list = sync_del_list;
	}    
}
