/*
 * Created on 2005-8-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_plan;

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
 * <p>Title:SaveZpreleasePosTrans</p>
 * <p>Description:保存招聘发布岗位,zp_position</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveZpreleasePosTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zppositionvo");
        if(vo==null)
            return;
        String flag_release =(String)this.getFormHM().get("flag_release");
        String plan_id_value = (String)this.getFormHM().get("plan_id_value");
        String dept_id_1 =(String)this.getFormHM().get("dept_id_1");
        String pos_id_1 =(String)this.getFormHM().get("pos_id_1");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        ArrayList list = new ArrayList();
        if("1".equals(flag_release))
        {
            /**
             * 新增发布招聘岗位，进行保存处理
             */
        	try{
        		String strsql = "select zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status from zp_position where pos_id = '"+vo.getString("pos_id")+"'";
        		this.frowset = dao.search(strsql);
         	    while(this.frowset.next()){
         	   	   String sql="update zp_position set amount ='"+vo.getString("amount")+"',domain='"+vo.getString("domain")+"',status = '"+vo.getString("status")+"' where pos_id ='"+vo.getString("pos_id")+"'";
 	               dao.update(sql,list);
 	               return;
         	    }
         	    PreparedStatement pstmt=null;
         	    try{
        	       IDGenerator idg=new IDGenerator(2,this.getFrameconn());
                   String zp_pos_id=idg.getId("zp_position.zp_pos_id");
                   vo.setString("zp_pos_id",zp_pos_id);    
                   String sql = "insert into zp_position (zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status) values(?,?,?,?,?,?,?,?)";
                   ArrayList values = new ArrayList();
                   values.add(zp_pos_id);
                   values.add(vo.getString("amount"));
                   values.add(plan_id_value);
                   values.add(vo.getString("dept_id"));
                   values.add(vo.getString("pos_id"));
                   values.add(DateUtils.getSqlDate(new Date()));
                   values.add(vo.getString("domain"));
                   values.add(vo.getString("status"));
                   dao.insert(sql, values);
                   /* pstmt=this.getFrameconn().prepareStatement(sql);
	  			   pstmt.setString(1,zp_pos_id);
	  			   pstmt.setString(2,vo.getString("amount"));
	  			   pstmt.setString(3,plan_id_value);
	  			   pstmt.setString(4,vo.getString("dept_id"));
	  			   pstmt.setString(5,vo.getString("pos_id"));
	  			   pstmt.setDate(6,DateUtils.getSqlDate(new Date()));
	  			   pstmt.setString(7,vo.getString("domain"));
				   pstmt.setString(8,vo.getString("status"));
	  			   pstmt.executeUpdate();*/
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
            }catch(Exception e){
    	        e.printStackTrace();
    	        throw GeneralExceptionHandler.Handle(e);
            }             
        }
        else if("0".equals(flag_release))
        {
	        /**
	         * 点编辑链接后，进行保存处理
	         */
	        try
	        {
	        	if(vo.getString("dept_id") != null && !"".equals(vo.getString("dept_id"))){
	        		dept_id_1 = vo.getString("dept_id");
	        	}
	        	if(vo.getString("pos_id") != null && !"".equals(vo.getString("pos_id"))){
	        		pos_id_1 = vo.getString("pos_id");
	        	}
	        	String sql="update zp_position set amount ='"+vo.getString("amount")+"',dept_id='"+dept_id_1+"',pos_id ='"+pos_id_1+"',domain='"+vo.getString("domain")+"',status = '"+vo.getString("status")+"' where zp_pos_id ='"+vo.getString("zp_pos_id")+"'";
	        	dao.update(sql,list);
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }

	}

}
