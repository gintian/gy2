package com.hjsj.hrms.transaction.performance.nworkplan;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;

/**
 * SearchAppiaryLeaderNameTrans.java
 * Description: 找出报批领导姓名
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 6, 2013 11:44:43 AM Jianghe created
 */
public class SearchAppiaryLeaderNameTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		String curr_user = (String)this.getFormHM().get("curr_user");
		String state = (String)this.getFormHM().get("state");
		String personPage = (String)this.getFormHM().get("personPage");
		String p0100 = (String)this.getFormHM().get("p0100");
		String person = (String)this.getFormHM().get("person");
		curr_user=SafeCode.decode(curr_user);
		person=SafeCode.decode(person);
		String a0101 = "";
		if(curr_user!=null && !"".equals(curr_user)){
			a0101 = getName(curr_user);
		}
		if(person!=null && !"".equals(person)){
			String[] strarray = person.split(",");
			for (int i = 0; i < strarray.length; i++) {
				a0101 +=getName(strarray[i])+",";
			}
			if(a0101.length()>0)
				a0101 = a0101.substring(0,a0101.length()-1);
		}
		this.getFormHM().put("a0101", a0101);
		this.getFormHM().put("curr_user", SafeCode.encode(curr_user));
		this.getFormHM().put("state", state);
		this.getFormHM().put("personPage", personPage);
		this.getFormHM().put("p0100", p0100);
	}

	/**
	 * 得到直接上级姓名
	 * @param a0100
	 * @param dao
	 * @return
	 */
	public String getName(String curr_user)
	{
		String sql="";
		RowSet rs = null;
		String nbase = curr_user.substring(0,3);
		String a0100 = curr_user.substring(3);
		String a0101 = "";
		sql = "select a0101 from "+nbase+"a01 where a0100 = '"+a0100+"'";
		
		try 
		{
		    ContentDAO dao = new ContentDAO(this.getFrameconn());	
			rs = dao.search(sql);
			if(rs.next())
			{
				
				a0101 = (String)rs.getString("a0101");
				
			}
			
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return a0101;
	}	
}
