/*
 * Created on 2005-8-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.staffreq;

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
 * <p>Title:SaveStaffReqTrans</p>
 * <p>Description:保存临时用工申请，zp_gather</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveStaffReqTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo vo=(RecordVo)this.getFormHM().get("zpgathervo");
		String gather_id_value = (String)this.getFormHM().get("gather_id_value");
		String gather_type = (String)this.getFormHM().get("gather_type");

        if(vo==null)
            return;
        String flag=(String)this.getFormHM().get("flag");
        String flag_mid = (String)this.getFormHM().get("flag_mid");
        String org_id_value =(String)this.getFormHM().get("org_id");
        String dept_id_value =(String)this.getFormHM().get("dept_id");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
       
        ArrayList list = new ArrayList();
        if("1".equals(flag))
        {
            /**
             * 新增临时用工申请
             */
        	PreparedStatement pstmt=null;
        	try{	   
               IDGenerator idg=new IDGenerator(2,this.getFrameconn());
               String gather_id=idg.getId("zp_gather.gather_id");
               vo.setString("gather_id",gather_id); 
               vo.setString("staff_id",this.userView.getUserName());
               String sql = "insert into zp_gather (gather_id,org_id,dept_id,valid_date,gather_type,create_date,staff_id,usedflag,status) values (?,?,?,"+PubFunc.DateStringChange(vo.getString("valid_date"))+",?,?,?,0,'01')";    
               list.add(gather_id);
               list.add(vo.getString("org_id"));
               list.add(vo.getString("dept_id"));
               list.add(Integer.parseInt(gather_type));
               list.add(DateUtils.getSqlDate(new Date()));
               list.add(vo.getString("staff_id"));
               dao.insert(sql, list);
               list.clear();
               /* pstmt=this.getFrameconn().prepareStatement(sql);
  			   pstmt.setString(1,gather_id);
  			   pstmt.setString(2,vo.getString("org_id"));
  			   pstmt.setString(3,vo.getString("dept_id"));*/
  			   //pstmt.setInt(4,Integer.parseInt(gather_type/*(vo.getString("gather_type")*/));	
  			   /*pstmt.setDate(5,DateUtils.getSqlDate(new Date()));
  			   pstmt.setString(6,vo.getString("staff_id"));	
  			   pstmt.executeUpdate();*/
               this.getFormHM().put("gather_id_value", gather_id);
               this.getFormHM().put("flag","0");
            }catch(Exception e){
            	e.printStackTrace();
            	throw GeneralExceptionHandler.Handle(e);
            }finally
			{
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
        }
        else if("0".equals(flag))
        {
	        /**
	         * 点修改链接后，进行保存处理
	         */
	        try
	        {
	        	if(vo.getString("org_id") != null && !"".equals(vo.getString("org_id"))){
	        		org_id_value = vo.getString("org_id");
	        	}
	        	if(vo.getString("dept_id") != null && !"".equals(vo.getString("dept_id"))){
	        		dept_id_value = vo.getString("dept_id");
	        	}
	        	/*看是否后org_id变化否变化则处理子集的所有信息*/
	        	this.frowset=dao.search("select org_id,dept_id from zp_gather  where gather_id='"+vo.getString("gather_id")+"'");
	        	if(this.frowset.next())
	        	{
	        		String orgid=this.frowset.getString("org_id");
	        		String deptid=this.frowset.getString("dept_id");
	        		if(!orgid.equalsIgnoreCase(org_id_value) || !deptid.equalsIgnoreCase(dept_id_value))
	        		{
	        			String sql="delete from zp_gather_pos where gather_id='" +vo.getString("gather_id")+"'";
	    	        	dao.delete(sql,list);
	        		}
	        	}	   
	        	String sql="update zp_gather set org_id='"+org_id_value+"',dept_id='"+dept_id_value+"',valid_date="+PubFunc.DateStringChange(vo.getString("valid_date"))+",gather_type="+Integer.parseInt(vo.getString("gather_type"))+""+" where gather_id='"+vo.getString("gather_id")+"'";
	        	dao.update(sql,list);
	        	     	
	        	this.getFormHM().put("gather_id_value", vo.getString("gather_id"));
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }

	}

}
