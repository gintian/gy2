package com.hjsj.hrms.businessobject.info;

import com.hrms.frame.dao.ContentDAO;

import javax.sql.RowSet;
import java.sql.SQLException;

public class AgentinfoUtils {

	/**
	 * 得到代理人名称
	 * @param agent_id
	 * @param status
	 * @return
	 */
    public String getAgent_fullname(String agent_id,int status,ContentDAO dao)
    {
    	String agent_fullname="";
    	if(status==4)
    	{
    		String nbase=agent_id.substring(0,3);
    		String a0100=agent_id.substring(3);
    		String sql="select a0101 from "+nbase+"A01 where a0100='"+a0100+"'";
    		RowSet rs=null;
    		try {
				rs=dao.search(sql);
				if(rs.next()) {
                    agent_fullname=rs.getString("a0101");
                }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}   		
    	}
    	return agent_fullname;
    }
}
