package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.fill_cycle.ReportCycleBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SubmitTemplateU02Trans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList list_insert=(ArrayList)this.getFormHM().get("import_insertList");
		ArrayList list_update=(ArrayList)this.getFormHM().get("import_updateList");
		String insert_sql=(String)this.getFormHM().get("import_insertSql");
	    String update_sql=(String)this.getFormHM().get("import_updateSql");
	    String unitcode=(String)this.getFormHM().get("unitcode");
    	String id=(String)this.getFormHM().get("id");
    	String report_id=(String)this.getFormHM().get("report_id");
    	String kmethod=(String)this.getFormHM().get("kmethod");//kmethod
    	String addother =(String)this.getFormHM().get("addother");
    	String flag = (String)this.getFormHM().get("flag");
    	String escope="";	
		escope=report_id.split("_")[1];
		String updatehistory = (String)this.getFormHM().get("updatehistory");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try {	
	    if(this.getFormHM().get("cycleparm")!=null&&!"".equals(this.getFormHM().get("cycleparm"))){
	    	//String deleteinfo = " delete from U02 where escope='"+escope+"' and id="+new Integer(id)+"";
	    	String unitcodes=(String)this.getFormHM().get("unitcodes");
	    	String unitcodesarr[]=unitcodes.split(",");
	    	if("1".equals(addother)){
	    		dao.batchUpdate(update_sql.toString(), list_update);
	    	}else{
	    	    String	delete_sql="delete from U02 where  escope=? and id=?  ";
				ArrayList dellist = new ArrayList();
//				for(int i=0;i<unitcodesarr.length;i++){
//		    		ArrayList list = new ArrayList();
//		    		list.add(escope);
//		    		list.add(id);
//		    		//list.add(unitcodesarr[i]);
//		    		
//		    		dellist.add(list);
//		    	}
				dao.batchUpdate(delete_sql, dellist);	
	    	}
//			if(kmethod.equals("0")){
//				for(int i=0;i<unitcodesarr.length;i++){
//					unitcode = unitcodesarr[i];
//			EditReport.introduceData(unitcode,id,report_id,this.getFrameconn(),this.userView);
//				}
//			}
	 			dao.batchInsert(insert_sql.toString(), list_insert);
	 		//	dao.batchUpdate(update_sql.toString(), list_update);
	 			String sql=""; 
	 			sql="update u02 set u0209='1' where u0209 is null and id='"+id+"' and escope='"+escope+"'";			
	 			dao.update(sql);
	 			for(int i=0;i<unitcodesarr.length;i++){
	 				sql="delete from tt_calculation_ctrl where unitcode='"+unitcodesarr[i]+"' and id='"+id+"' and report_id='"+report_id+"'";
		 			dao.delete(sql, new ArrayList());
		 			sql="insert into tt_calculation_ctrl(unitcode,id,report_id,flag)values('"+unitcodesarr[i]+"','"+id+"','"+report_id+"','1')";
		 	        dao.insert(sql, new ArrayList());
					
		    	}
	 			//符合条件自动更新U05表中的内容
	 			ReportCycleBo reportCycleBo=new ReportCycleBo();
	 			reportCycleBo.isupdateU05(id,kmethod,this.getFrameconn(),this.getUserView()) ;
	 			
	    }else{
	   	    if(updatehistory!=null&& "updatehistory".equals(updatehistory)){
	   	    	dao.batchUpdate(update_sql.toString(), list_update);	
	   	    }else{
			dao.batchUpdate(update_sql.toString(), list_update);
			dao.batchInsert(insert_sql.toString(), list_insert);
			String sql="update u02 set u0209='1' where u0209 is null and id='"+id+"' and escope='"+escope+"'";			
			dao.update(sql);
			
			if(!"2".equals(flag)){
				 sql="delete from tt_calculation_ctrl where unitcode='"+unitcode+"' and id='"+id+"' and report_id='"+report_id+"'";
				dao.delete(sql, new ArrayList());
				sql="insert into tt_calculation_ctrl(unitcode,id,report_id,flag)values('"+unitcode+"','"+id+"','"+report_id+"','0')";	
				 dao.insert(sql, new ArrayList());		
			}
	   	    }
	    }
	    }catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}

