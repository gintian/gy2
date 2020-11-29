/*
 * Created on 2005-8-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.resource_plan;

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
 * <p>Title:SaveResourcePlanDetailsTrans</p>
 * <p>Description:保存人力规划明细，zp_hr_plan_details</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveResourcePlanDetailsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zpplanDetailsvo");
		String plan_id = vo.getString("plan_id");
		String plan_id_value = (String)this.getFormHM().get("plan_id_value");
        if(vo==null)
            return;
        String flag_detail=(String)this.getFormHM().get("flag_detail");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag_detail))
        {
            /**
             * 新增人力规划明细
             */
        	PreparedStatement pstmt=null;
        	try{
        	   IDGenerator idg=new IDGenerator(2,this.getFrameconn());
               String key_id = idg.getId("zp_hr_plan_details.key_id");
               String sql = "insert into zp_hr_plan_details (key_id,plan_id,dept_id,pos_id,amount,valid_date,type,reason) values (?,?,?,?,?,?,?,?)";
               ArrayList list = new ArrayList();
               list.clear();
               list.add(key_id);
               list.add(plan_id_value);
               list.add(vo.getString("dept_id"));
               list.add(vo.getString("pos_id"));
               list.add(Integer.parseInt(vo.getString("amount")));
               list.add(DateUtils.getSqlDate(new Date()));
               list.add(vo.getString("type"));
               if(vo.getString("reason").length()>250)
    			     list.add(vo.getString("reason").substring(0,250));
    		   else
    			   list.add(vo.getString("reason"));
               list.add(this.getFrowset().getString("gather_id"));
               dao.insert(sql, list);
              /* pstmt=this.getFrameconn().prepareStatement(sql);
  			   pstmt.setString(1,key_id);
  			   pstmt.setString(2,plan_id_value);
  			   pstmt.setString(3,vo.getString("dept_id"));
  			   pstmt.setString(4,vo.getString("pos_id"));	
  			   pstmt.setInt(5,Integer.parseInt(vo.getString("amount")));	
  			   pstmt.setDate(6,DateUtils.getSqlDate(new Date()));
  			   pstmt.setString(7,vo.getString("type"));	
  			   if(vo.getString("reason").length()>250)
  			     pstmt.setString(8,vo.getString("reason").substring(0,250));
  			   else
  			     pstmt.setString(8,vo.getString("reason"));
  			   pstmt.executeUpdate();*/
               
               this.getFormHM().put("plan_id_value",plan_id_value);
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
        else if("0".equals(flag_detail))
        {
	        /**
	         * 点修改链接后，进行保存处理
	         */
	        try
	        {
	        	ArrayList list = new ArrayList();
	        	String sql="update zp_hr_plan_details set dept_id = '"+vo.getString("dept_id")+"',pos_id='"+vo.getString("pos_id")+"',amount="+Integer.parseInt(vo.getString("amount"))+",type='"+vo.getString("type")+"',reason = '"+vo.getString("reason")+"' where plan_id='"+vo.getString("plan_id")+"' and key_id = "+vo.getString("key_id");
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
