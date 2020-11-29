package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;

/**
 * SearchItemdescTrans.java
 * Description: 查找指标分类名称
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Oct 24, 2012 5:10:53 PM Jianghe created
 */
public class SearchItemdescTrans extends IBusiness{
	public void execute() throws GeneralException 
	{
		String codesetid = (String)this.getFormHM().get("codesetid");
		String itemdesc = getItemDesc(codesetid);
		this.getFormHM().put("itemdesc", itemdesc);
	}
	public String getItemDesc(String codesetid)
	{
		String codesetidL = "";
		String flag = "";
		if (codesetid.indexOf("_") != -1) {
			codesetidL = codesetid;
			String[] codesetidLs=codesetidL.split("_");
			if("55".equals(codesetidLs[0])&&codesetidLs.length==2){
				codesetid=codesetidLs[0];
				flag=codesetidLs[1];
			}
		}
			
		String sql="";
		RowSet rs = null;
		String itemdesc = null;
		sql = "select codesetdesc from codeset where 1=1 "; 
		if(codesetid!=null && !"".equals(codesetid))
		    sql+="and codesetid='"+codesetid+"'";
		try 
		{
		    ContentDAO dao = new ContentDAO(this.getFrameconn());	
			rs = dao.search(sql);
			if(rs.next())
			{
				
				itemdesc = (String)rs.getString("codesetdesc");
				if("UN".equalsIgnoreCase(codesetid)){
					itemdesc="单位名称";
				}else if("UM".equalsIgnoreCase(codesetid)){
					itemdesc="部门名称";
				}
				if ("@K".equals(codesetid)) 
				{
					itemdesc = ResourceFactory.getProperty("tree.kkroot.gwdesc");
				}
				if("2".equals(flag)&&"55".equals(codesetid))
					itemdesc = ResourceFactory.getProperty("tree.train.lessonname");
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return itemdesc;
	}	
}
