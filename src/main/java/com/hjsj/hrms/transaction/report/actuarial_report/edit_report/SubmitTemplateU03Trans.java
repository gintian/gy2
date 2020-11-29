package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SubmitTemplateU03Trans extends IBusiness {

	public void execute() throws GeneralException {
	//	ArrayList list_insert=(ArrayList)this.getFormHM().get("import_insertList");
		ArrayList list_update=(ArrayList)this.getFormHM().get("import_updateList");
		ArrayList list_deleteinfo=(ArrayList)this.getFormHM().get("import_deleteinfo");
	//	String insert_sql=(String)this.getFormHM().get("import_insertSql");
	    String update_sql=(String)this.getFormHM().get("import_updateSql");
	   // String unitcode=(String)this.getFormHM().get("unitcode");
		String unitcodes=(String)this.getFormHM().get("unitcodes");
    	String id=(String)this.getFormHM().get("id");
    	String report_id=(String)this.getFormHM().get("report_id");
//    	String escope="";	
//		escope=report_id.split("_")[1];
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try {	
	    	//System.out.println("unitcodes:"+unitcodes);
	    	String unitcodesarr[]=unitcodes.split(",");
	    	String delete_sql = " delete  from u03 where id="+id+" and unitcode=? ";
	    	dao.batchUpdate(delete_sql, list_deleteinfo);
	    	//更新以下部门
			String update_sql1 = " insert into  u03(u0301,id,unitcode,editflag) values('1',"+id+",?,'1') ";
			String update_sql2 = " insert into  u03(u0301,id,unitcode,editflag) values('2',"+id+",?,'1') ";
			ArrayList updateu03 = new ArrayList();
			for(int i=0;i<unitcodesarr.length;i++){
	    		ArrayList list = new ArrayList();
	    		list.add(unitcodesarr[i]);
	    		updateu03.add(list);
	    	}
			dao.batchUpdate(update_sql1, updateu03);
			dao.batchUpdate(update_sql2, updateu03);
			//String update_sql ="";
			dao.batchUpdate(update_sql.toString(), list_update);
			
			delete_sql="delete from tt_calculation_ctrl where  id=? and unitcode=? and report_id=? ";
			ArrayList dellist = new ArrayList();
			for(int i=0;i<unitcodesarr.length;i++){
	    		ArrayList list = new ArrayList();
	    		list.add(id);
	    		list.add(unitcodesarr[i]);
	    		list.add(report_id.trim());
	    		dellist.add(list);
	    	}
			dao.batchUpdate(delete_sql, dellist);
			String sql="insert into tt_calculation_ctrl(unitcode,id,report_id,flag)values(?,?,?,?)";
			
		    ArrayList insert = new ArrayList();
	    	for(int i=0;i<unitcodesarr.length;i++){
	    		ArrayList list = new ArrayList();
	    		list.add(unitcodesarr[i]);
	    		list.add(id);
	    		list.add(report_id.trim());
	    		list.add("1");
	    		insert.add(list);
	    	}
			dao.batchInsert(sql, insert);
	    
	    }catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}

