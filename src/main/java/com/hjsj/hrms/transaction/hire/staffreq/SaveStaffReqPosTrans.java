/**
 * Created on 2005-8-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.staffreq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveStaffReqPosTrans</p>
 * <p>Description:保存临时用工申请岗位，zp_gather_pos</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SaveStaffReqPosTrans extends IBusiness {

   public void execute() throws GeneralException {
	   RecordVo vo=(RecordVo)this.getFormHM().get("gatherPosvo");
	   String gather_id_value = (String)this.getFormHM().get("gather_id_value");
        if(vo==null)
            return;
        String flag_pos=(String)this.getFormHM().get("flag_pos");
        ContentDAO dao=new ContentDAO(this.getFrameconn());         
        if("1".equals(flag_pos))
        {
          String sqle = "select * from zp_gather_pos where gather_id = '"+gather_id_value+"' and pos_id = '"+vo.getString("pos_id")+"'";	
          try{
          	  ArrayList list = new ArrayList();
     	      this.frowset = dao.search(sqle);
   	          String strsql="";
     	      while(this.frowset.next()){
     	      	if(vo.getString("reason").length()>250)
     	      	    strsql = "update zp_gather_pos set amount = "+Integer.parseInt(vo.getString("amount"))+",type = '"+vo.getString("type")+"',reason = '"+vo.getString("reason").substring(0,250)+"' where gather_id = '"+gather_id_value+"' and pos_id = '"+vo.getString("pos_id")+"'";
                else
                	strsql = "update zp_gather_pos set amount = "+Integer.parseInt(vo.getString("amount"))+",type = '"+vo.getString("type")+"',reason = '"+vo.getString("reason")+"' where gather_id = '"+gather_id_value+"' and pos_id = '"+vo.getString("pos_id")+"'";
              	dao.update(strsql,list); 
     	      	return;
     	      }
     	     String sql="";
     	     if(vo.getString("reason").length()>250)
     	        sql = "insert into zp_gather_pos (gather_id,pos_id,amount,type,reason) values('"+gather_id_value+"','"+vo.getString("pos_id")+"',"+vo.getString("amount")+",'"+vo.getString("type")+"','"+vo.getString("reason").substring(0,250)+"')";
     	      else
     	     	sql = "insert into zp_gather_pos (gather_id,pos_id,amount,type,reason) values('"+gather_id_value+"','"+vo.getString("pos_id")+"',"+vo.getString("amount")+",'"+vo.getString("type")+"','"+vo.getString("reason")+"')";
      	      dao.update(sql,list); 
              this.getFormHM().put("gatherPoslist", list);
              this.getFormHM().put("gather_id_value", gather_id_value);
     	  }catch(Exception e){
     	   	  e.printStackTrace();
     	   }
        }
        else if("0".equals(flag_pos))
        {
	        /**
	         * 点修改链接后，进行保存处理
	         */
	        try
	        {
	        	ArrayList list = new ArrayList();
	        	String sql="update zp_gather_pos set amount="+Integer.parseInt(vo.getString("amount"))+",type='"+vo.getString("type")+"',reason = '"+vo.getString("reason")+"' where gather_id='"+vo.getString("gather_id")+"' and pos_id = '"+vo.getString("pos_id")+"'";
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
