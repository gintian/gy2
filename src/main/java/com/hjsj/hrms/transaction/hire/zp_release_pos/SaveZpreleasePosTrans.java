/*
 * Created on 2005-8-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_release_pos;

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
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zpreleasePosvo");
        if(vo==null)
            return;
        ArrayList list = new ArrayList();
        String flag=(String)this.getFormHM().get("flag");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag))
        {
            /**
             * 新增发布招聘岗位，进行保存处理
             */
        	try{
        	   String strsql = "select zp_pos_id from zp_position where pos_id = '"+vo.getString("pos_id")+"'";
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
                  String sql = "insert into zp_position (zp_pos_id,amount,plan_id,dept_id,pos_id,valid_date,domain,status) values (?,?,'0',?,?,?,?,?)";
                  list.clear();
                  list.add(zp_pos_id);
                  list.add(vo.getString("amount"));
                  list.add(vo.getString("dept_id"));
                  list.add(vo.getString("pos_id"));
                  list.add(DateUtils.getSqlDate(new Date()));
                  list.add(vo.getString("domain"));
                  list.add(vo.getString("status"));
                  dao.insert(sql, list);
                  list.clear();
                  /*pstmt=this.getFrameconn().prepareStatement(sql);
	  			  pstmt.setString(1,zp_pos_id);
	  			  pstmt.setString(2,vo.getString("amount"));
	  			  pstmt.setString(3,vo.getString("dept_id"));
	  			  pstmt.setString(4,vo.getString("pos_id"));
	  			  pstmt.setDate(5,DateUtils.getSqlDate(new Date()));
	  			  pstmt.setString(6,vo.getString("domain"));
				  pstmt.setString(7,vo.getString("status"));
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
        else if("0".equals(flag))
        {
	        /**
	         * 点编辑链接后，进行保存处理
	         */
	        try
	        {
	         		
	        	String sql="update zp_position set amount ='"+vo.getString("amount")+"',dept_id='"+vo.getString("dept_id")+"',pos_id ='"+vo.getString("pos_id")+"',domain='"+vo.getString("domain")+"',status = '"+vo.getString("status")+"' where zp_pos_id ='"+vo.getString("zp_pos_id")+"'";
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
