/*
 * Created on 2005-8-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_plan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title:InsertZppostionTrans</p>
 * <p>Description:发布招聘发布岗位,zp_position</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class InsertZppostionTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList del_list = (ArrayList)this.getFormHM().get("zpplanDetailslist");
		String plan_id_value = (String)this.getFormHM().get("plan_id_value");
		String flag_release = (String)this.getFormHM().get("flag_release");
		ArrayList list = new ArrayList();
        try{
		    for(int i = 0;i < del_list.size();i++){
			   RecordVo rv=(RecordVo)del_list.get(i);
			   try{
			   	  String strsql = "select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where pos_id = '"+rv.getString("pos_id")+"'";
			   	  this.frowset = dao.search(strsql);
			   	  if(!this.frowset.next()){
			   	     PreparedStatement pstmt=null;
			   	     try{
			            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
                        String zp_pos_id=idg.getId("zp_position.zp_pos_id");
			            String pos_sql = "insert into zp_position (zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status) values (?,?,?,?,?,?,?,?)";
			            ArrayList values = new ArrayList();
			            values.add(zp_pos_id);
			            values.add(rv.getString("amount"));
			            values.add(rv.getString("plan_id"));
			            values.add(rv.getString("dept_id"));
			            values.add(rv.getString("pos_id"));
			            values.add(DateUtils.getSqlDate(new Date()));
			            values.add(rv.getString("domain"));
			            values.add(rv.getString("status"));
			            dao.insert(pos_sql, values);
			            /*pstmt=this.getFrameconn().prepareStatement(pos_sql);
		  			    pstmt.setString(1,zp_pos_id);
		  			    pstmt.setString(2,rv.getString("amount"));
		  			    pstmt.setString(3,rv.getString("plan_id"));
		  			    pstmt.setString(4,rv.getString("dept_id"));
		  			    pstmt.setString(5,rv.getString("pos_id"));
		  			    pstmt.setDate(6,DateUtils.getSqlDate(new Date()));
		  			    pstmt.setString(7,rv.getString("domain"));
					    pstmt.setString(8,rv.getString("status"));
		  			    pstmt.executeUpdate();*/
		  			    String gather_sql = "update zp_gather set usedflag = 2 where gather_id = '"+rv.getString("gather_id")+"'";
		  			    dao.update(gather_sql);
			   	     }catch(Exception e){
			   	        e.printStackTrace();
		            	throw GeneralExceptionHandler.Handle(e);
			   	     }finally{
			   	        try
		    			{
		    				if(pstmt!=null)
		    					pstmt.close();
		    			}
		    			catch(SQLException ee)
		    			{
		    				ee.printStackTrace();
		    			}
			   	     }
			   	  }else{
			   	  	continue;
			   	  }
               }catch(Exception e){
    	           e.printStackTrace();
    	           throw GeneralExceptionHandler.Handle(e);
               } 
		      }
		    String query_sql = "";
		    if("UN".equals(this.userView.getManagePrivCode())){
		    	query_sql = "select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where dept_id like '"+this.userView.getManagePrivCodeValue()+"%'";
		    }else if("UM".equals(this.userView.getManagePrivCode())){
		    	query_sql = "select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where dept_id  like '"+this.userView.getManagePrivCodeValue()+"%'";
		    }else if(!"@K".equals(this.userView.getManagePrivCode())){
		    	query_sql = "select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position";
		    }
            this.frowset = dao.search(query_sql);
            while(this.frowset.next()){
            	RecordVo vo_pos = new RecordVo("zp_position",1);
            	vo_pos.setString("zp_pos_id",this.frowset.getString("zp_pos_id"));
            	vo_pos.setString("amount",this.frowset.getString("amount"));
            	vo_pos.setString("plan_id",this.frowset.getString("plan_id"));
            	vo_pos.setString("dept_id",this.frowset.getString("dept_id"));
            	vo_pos.setString("pos_id",this.frowset.getString("pos_id"));
            	vo_pos.setString("valid_date",PubFunc.DoFormatDate(PubFunc.FormatDate(this.frowset.getDate("valid_date"))));
            	vo_pos.setString("domain",this.frowset.getString("domain"));
            	vo_pos.setString("status",this.frowset.getString("status"));
            	list.add(vo_pos);
            }
            String update_sql = "update zp_plan set status = '05' where plan_id = '"+plan_id_value+"'";
            ArrayList planlist = new ArrayList();
            dao.update(update_sql,planlist);
	} catch (Exception e) {
		e.printStackTrace();
		throw GeneralExceptionHandler.Handle(e);
	}finally {
			this.getFormHM().put("zppositionlist", list);
			this.getFormHM().put("plan_id_value", plan_id_value);
			
		}

	}

}
