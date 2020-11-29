/*
 * Created on 2005-8-31
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveTestProcessTrans</p>
 * <p>Description:保存面试环节</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SaveTestProcessTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("testProcessvo");
        if(vo==null)
            return;
        String flag=(String)this.getFormHM().get("flag");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag))
        {
            /**
             * 新增面试环节，进行保存处理
             */
        	try{
               IDGenerator idg=new IDGenerator(2,this.getFrameconn());
               String tache_id=idg.getId("zp_tache.tache_id");
               vo.setString("tache_id",tache_id);    
               String sql = "insert into zp_tache (tache_id,name) values("+tache_id+",'"+vo.getString("name")+"')";
               ArrayList list = new ArrayList();
               dao.update(sql,list);
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
	         	
	        	ArrayList list = new ArrayList();
	        	String sql="update zp_tache set name ='"+vo.getString("name")+"' where tache_id ="+vo.getString("tache_id");
	        	dao.update(sql,list);
	            
	        	
	            //dao.updateValueObject(vo);
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }

	}

}
