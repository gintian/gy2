package com.hjsj.hrms.transaction.general.inform.emp.output;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class DeleteRosterTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tabid=(String)this.getFormHM().get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		String infor_kind=(String)this.getFormHM().get("infor_kind");//=1 r,2 un
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList paralist=new ArrayList();
		ArrayList list=new ArrayList();		
		list.add(tabid);
		paralist.add(list);
		
		StringBuffer strsql=new StringBuffer();
		strsql.append("delete from lname where tabid=?");
		StringBuffer sql=new StringBuffer();
		sql.append("delete from lbase where tabid=?");
		try {
			 dao.batchUpdate(strsql.toString(),paralist);
			 dao.batchUpdate(sql.toString(),paralist);
			 /**删除所有基于这个花名册建立的临时表*/
			 String prefix="m"+tabid;
			 String nameColumn="name";
			 if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				 nameColumn="table_name";
			 DbWizard dbWizard = new DbWizard(this.getFrameconn());
			 if("1".equals(infor_kind))
			 {
				 this.frowset=dao.search("select pre from dbname ");
				 while(this.frowset.next())
				 {
					 String pre=this.frowset.getString("pre");
					 String cond="and lower("+nameColumn+") like  'm"+tabid+"_%_"+pre.toLowerCase()+"'";
					 this.dropTableByCondition(dao, dbWizard, cond,prefix);
				 }
			 }
			 else if("2".equals(infor_kind))
			 {
				 String cond="and lower("+nameColumn+") like  'm"+tabid+"_%_b' ";
				 this.dropTableByCondition(dao, dbWizard, cond,prefix);
			 }else if("3".equals(infor_kind))
			 {
				 String cond="and lower("+nameColumn+") like  'm"+tabid+"_%_k' ";
				 this.dropTableByCondition(dao, dbWizard, cond,prefix);
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	public void dropTableByCondition(ContentDAO dao,DbWizard dbw,String where,String prefix)
 	{
 		try
 		{
 			int dbflag=Sql_switcher.searchDbServer();
 			String sql="";
			switch(dbflag)
			{
				case Constant.MSSQL:
					sql="select name from sysobjects where type='u' "+where;
					break;
				case Constant.DB2:
					sql="SELECT NAME FROM SYSIBM.SYSTABLES WHERE TYPE = 'T' AND CREATOR != 'SYSIBM' "+where;
					break;
				case Constant.ORACEL:
					sql="SELECT TABLE_NAME FROM USER_TABLES WHERE 1=1 "+where;
					break;
			}
			if(sql.length()>0)
			{
				RowSet rowSet=dao.search(sql);
				while(rowSet.next())
				{
					String tableName=rowSet.getString(1);
					String fix = tableName.substring(0,tableName.indexOf("_"));
					if(fix.equalsIgnoreCase(prefix))
					{
				    	Table table=new Table(tableName);
				    	if(dbw.isExistTable(table))
				        	dbw.dropTable(table);
					}
				}
			}
 		}
 		catch(Exception e)
 		{
 			e.printStackTrace();
 		}
 	}

}
