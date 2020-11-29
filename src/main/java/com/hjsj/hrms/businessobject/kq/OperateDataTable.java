package com.hjsj.hrms.businessobject.kq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;

import java.sql.Connection;
import java.util.ArrayList;

public class OperateDataTable {

	private Connection conn;
	public OperateDataTable(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 删除临时数据表
	 * @param tablename
	 */
	public void dropTable(String tablename)
	{
		String deleteSQL="delete from "+tablename+"";		
		ArrayList deletelist= new ArrayList();
		
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			dao.delete(deleteSQL,deletelist);
			DbWizard dbWizard =new DbWizard(this.conn);
			Table table=new Table(tablename);
			dbWizard.dropTable(table);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
