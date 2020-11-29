/*
 * Created on 2005-8-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.resource_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Title:SubmitResourcePlanTrans</p>
 * <p>Description:提交人力规划，zp_hr_plan</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin,update wlh
 * @version 1.0
 * 
 */

public class SubmitResourcePlanTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zpplanvo");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try{
    	String sql="update zp_hr_plan set status = '06' where plan_id='"+vo.getString("plan_id")+"'";
    	dao.update(sql);
    	this.getFormHM().put("plan_id_value",vo.getString("plan_id"));
    	/*更改人员编制数*/
    	RecordVo rv= ConstantParamter.getRealConstantVo("PS_WORKOUT");  
		if(rv == null){
			return;
		}else
		{
			StringBuffer sqlstr=new StringBuffer();
			String posWork = rv.getString("str_value");
			int strIndex = posWork.indexOf("|");
			if(strIndex != -1){
				 String setstr = posWork.substring(0,strIndex);
				 int fieldIndex = posWork.indexOf(",");
				 if(fieldIndex != -1){
				     String firstfieldstr = posWork.substring(strIndex+1,fieldIndex);
			   	     String lastfieldstr = posWork.substring(fieldIndex+1,posWork.length());
			   	    if("UM".equals(this.userView.getManagePrivCode())){
				        sql="select pos_id,amount,gather_id,type from zp_hr_plan_details where plan_id = '"+ vo.getString("plan_id") + "' and dept_id = '"+this.userView.getManagePrivCodeValue()+"'";
				    }else{
				        sql="select pos_id,amount,gather_id,type from zp_hr_plan_details where plan_id = '"+ vo.getString("plan_id") + "'";
				    }
			   	   this.frowset = dao.search(sql);
			   	   ResultSet rs=null;
			   	   while(this.frowset.next())
			   	   {
			   	   	  rs=dao.search("select " + firstfieldstr + " from " + setstr + " where e01a1='" + this.frowset.getString("pos_id") + "'");
			   	   	  sqlstr.delete(0,sqlstr.length());
			   	      sqlstr.append("update ");
			   	      sqlstr.append(setstr);
			   	      sqlstr.append(" set ");
			   	      sqlstr.append(firstfieldstr);
			   	      sqlstr.append("=");
			   	      if(rs.next()) 
			   	      	if(rs.getString(firstfieldstr)!=null)
			   	      	{
			   	          sqlstr.append(firstfieldstr);
			   	          if("01".equalsIgnoreCase(this.frowset.getString("type")))			   	      	
			   	             sqlstr.append(" + ");
			   	          else
			   	             sqlstr.append(" - ");
			   	         }
			   	      sqlstr.append(this.frowset.getInt("amount"));
			   	      sqlstr.append(" where e01a1='");
			   	      sqlstr.append(this.frowset.getString("pos_id"));
			   	      sqlstr.append("'");
			   	      if(setstr.indexOf("01") == -1)
			   	      {
			   	        sqlstr.append(" and i9999=(select max(i9999) as i9999 from ");
			   	        sqlstr.append(setstr);
			   	        sqlstr.append(" where e01a1='");
			   	        sqlstr.append(this.frowset.getString("pos_id"));
			   	        sqlstr.append("')");
			   	      }
			   	      dao.update(sqlstr.toString());
			   	      sqlstr.delete(0,sqlstr.length());
			   	      sqlstr.append("update zp_gather set usedflag=2 where gather_id='");
			   	      sqlstr.append(this.frowset.getString("gather_id"));
			   	      sqlstr.append("'");
			   	      dao.update(sqlstr.toString());
			   	   }
			   	   //if(rs!=null)
			   	   	// rs.close();
				 }		   	  
			}		   	 
		  }
       }
       catch(SQLException sqle)
       {
   	     sqle.printStackTrace();
	     throw GeneralExceptionHandler.Handle(sqle);            
       }
	}
}
