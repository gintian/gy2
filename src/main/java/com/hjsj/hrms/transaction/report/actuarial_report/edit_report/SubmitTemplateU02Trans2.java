package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SubmitTemplateU02Trans2 extends IBusiness {

		public void execute() throws GeneralException {
			//ArrayList list_insert=(ArrayList)this.getFormHM().get("import_insertList");
			ArrayList list_update=(ArrayList)this.getFormHM().get("import_updateList");
			//String insert_sql=(String)this.getFormHM().get("import_insertSql");
		    String update_sql=(String)this.getFormHM().get("import_updateSql");
		    String unitcode=(String)this.getFormHM().get("unitcode");
	    	String id=(String)this.getFormHM().get("id");
	    	String report_id=(String)this.getFormHM().get("report_id");
	    	//System.out.println("report_id:"+report_id+"report_id");
	    	String kmethod=(String)this.getFormHM().get("kmethod");//kmethod
	    	String unitcodes=(String)this.getFormHM().get("unitcodes");
	    	String description=(String)this.getFormHM().get("description");
	    	this.getFormHM().remove("description");
	    	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
	    	String parm = (String)hm.get("b_importsubmit2");
	    	String escope="";	
			escope=report_id.split("_")[1];
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
		    try {	
		    	EditReport editReport = new EditReport();
		    	if(parm!=null&& "reject".equals(parm)){//直接驳回
		    		String updatesql = "update u02 set editflag=2 where id="+id+" and unitcode='"+unitcode+"' and escope='"+escope+"'";
		    		dao.update(updatesql);
		    		String delete_sql="delete from tt_calculation_ctrl where  id="+id+" and unitcode='"+unitcode+"' and report_id='"+report_id+"' ";
		    		dao.update(delete_sql);
		    		String sql="insert into tt_calculation_ctrl(unitcode,id,report_id,flag,description)values('"+unitcode+"',"+id+",'"+report_id+"',2,'"+description+"')";
		    		dao.insert(sql, new ArrayList());	
		    		
					String	liststr =editReport.getUnitCodeParentid( "",this.getFrameconn(),unitcode);
					
					if(liststr.indexOf(",")!=-1)
						liststr= liststr.substring(0,liststr.length()-1);
					//System.out.println("liststr:"+liststr);
					 sql="update tt_calculation_ctrl set flag=? where id=? and unitcode=? and report_id= ? ";
					String liststrs[] = liststr.split(",");
					    ArrayList update = new ArrayList();
				    	for(int i=0;i<liststrs.length;i++){
				    		ArrayList list = new ArrayList();
				    		list.add("-1");
				    		list.add(id);
				    		list.add(liststrs[i]);
				    		list.add(report_id.trim());
				    		update.add(list);
				    	}
						dao.batchUpdate(sql, update);
					
		    	}else{
		    	String unitcodesarr[]=unitcodes.split(",");
		    	//System.out.println(update_sql.toString());
				dao.batchUpdate(update_sql.toString(), list_update);
				//dao.batchInsert(insert_sql.toString(), list_insert);
				String sql=""; 
	 			sql="update u02 set editflag=1 where  id='"+id+"' and escope='"+escope+"'and editflag=3 and unitcode in("+unitcodes+")";			
	 			dao.update(sql);
				//update状态表本部门是2,父亲以上部门为-1
				String delete_sql="delete from tt_calculation_ctrl where  id=? and unitcode=? and report_id=? ";
				ArrayList dellist = new ArrayList();
				for(int i=0;i<unitcodesarr.length;i++){
		    		ArrayList list = new ArrayList();
		    		list.add(id);
		    		list.add(unitcodesarr[i]);
		    		list.add(report_id.trim());
		    		dellist.add(list);
		    	}
				dao.batchUpdate(delete_sql, dellist);
				 sql="insert into tt_calculation_ctrl(unitcode,id,report_id,flag,description)values(?,?,?,?,?)";
				
			    ArrayList insert = new ArrayList();
		    	for(int i=0;i<unitcodesarr.length;i++){
		    		ArrayList list = new ArrayList();
		    		list.add(unitcodesarr[i].trim());
		    		list.add(id);
		    		list.add(report_id.trim());
		    		list.add("2");
		    		list.add(description);
		    		insert.add(list);
		    	}
				dao.batchInsert(sql, insert);
				//递归组合sql
				
				String liststr ="";
				for(int i=0;i<unitcodesarr.length;i++){
					liststr =editReport.getUnitCodeParentid( liststr,this.getFrameconn(),unitcodesarr[i]);
				}
				if(liststr.indexOf(",")!=-1)
					liststr= liststr.substring(0,liststr.length()-1);
				//System.out.println("liststr:"+liststr);
				 sql="update tt_calculation_ctrl set flag=? where id=? and unitcode=? and report_id= ? ";
				String liststrs[] = liststr.split(",");
				    ArrayList update = new ArrayList();
			    	for(int i=0;i<liststrs.length;i++){
			    		ArrayList list = new ArrayList();
			    		list.add("-1");
			    		list.add(id);
			    		list.add(liststrs[i]);
			    		list.add(report_id.trim());
			    		update.add(list);
			    	}
					dao.batchUpdate(sql, update);
				
		    	}
		    }catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    this.getFormHM().put("flag","2");
		}
		}


