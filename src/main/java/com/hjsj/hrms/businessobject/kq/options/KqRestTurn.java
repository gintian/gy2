package com.hjsj.hrms.businessobject.kq.options;

import com.hrms.frame.dao.ContentDAO;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class KqRestTurn {

	
	 /**
     * 得到上级部门的id
     * @param codeitemid
     * @return
     */
    public ArrayList getUpDeptId(String codeitemid,String codesetid,Connection conn,String code)
    {
    	String orgSql="SELECT parentid,codeitemid from organization where codeitemid='"+ codeitemid +"' and codesetid='"+codesetid+"'";
    	ContentDAO dao=new ContentDAO(conn);
    	RowSet rowSet=null;
    	String parentid="";    	
    	try
    	{
    		rowSet=dao.search(orgSql);
        	if(rowSet.next())
        	{
        		parentid=rowSet.getString("parentid");        	
        		
        	}
        
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
		{
			if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
    	ArrayList list=new ArrayList();
    	list.add(parentid);
    	if(parentid.equals(codeitemid))
    	{
    		list.add(code);
    	}else
    	{
    		list.add("UN"+parentid);
    	}
    	
    	
    	
    	return list;
    }
    public String getb0110s(String codeitemid,String parentid,String codesetid,Connection conn)
    {
    	ArrayList list=new ArrayList(); 
    	if(codeitemid.indexOf("UN")!=-1)
    	{
    		int i=codeitemid.indexOf("UN");
    		codeitemid=codeitemid.substring(i+2);
    	} 
    	if(codeitemid==null)
    	{
    		return "";
    	}else if(codeitemid.length()<=0)
    	{
    		return "''";
    	}    	
    	String code="";
    	do
    	{
    		list.add(codeitemid);
    		ArrayList code_list=getUpDeptId(codeitemid,codesetid,conn,parentid);
    		codeitemid=code_list.get(0).toString();
    		code=code_list.get(1).toString();
    	}while(!parentid.equals(code));
    	StringBuffer Str=new StringBuffer();
    	for(int i=0;i<list.size();i++)
    	{
    		Str.append("'UN"+list.get(i).toString()+"',");
    	}
    	String b0110s=Str.toString().substring(0,Str.length()-1);
    	return b0110s;
    }
}
