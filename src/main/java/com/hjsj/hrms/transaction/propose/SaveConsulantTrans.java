/*
 * Created on 2005-5-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.propose;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
/**
 * @author Administrator
 *
 */
public class SaveConsulantTrans extends IBusiness {

	 public void execute() throws GeneralException {
        RecordVo vo=(RecordVo)this.getFormHM().get("cousulantov");
        if(vo==null)
            return;
        String flag=(String)this.getFormHM().get("flag");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        ArrayList paralist=new ArrayList();         
        if("1".equals(flag))
        {
            /**新加建议，进行保存处理*/
            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
            String id=PubFunc.NullToZero(idg.getId("consultation.id"));
	        try
	        {        	

	            String sql="insert into consultation (id,createuser,createtime,ccontent,B0110,E0122,E01A1) values (?,?,?,?,?,?,?)";
	            paralist.add(id);
	            paralist.add(this.userView.getUserFullName());
	            paralist.add(DateUtils.getTimestamp(new Date())/*DateStyle.getSystemTime()*/);
	            paralist.add(vo.getString("ccontent"));
	            paralist.add(this.userView.getUserOrgId());
	            paralist.add(this.userView.getUserDeptId());
	            paralist.add(this.userView.getUserPosId());
	         	dao.update(sql,paralist);
	        }
	        catch(Exception sqle)
	        {
	    	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }
        else if("0".equals(flag))
        {
	        /**点编辑链接后，进行保存处理*/
            cat.debug("update_consulantvo="+vo.toString());
	        try
	        {
	        	 String id=vo.getString("id");
	        	 String sql="update  consultation set ccontent=?,B0110=?,E0122=?,E01A1=? where id=?";
	        	 paralist.add(vo.getString("ccontent"));
	        	 paralist.add(this.userView.getUserOrgId());
	        	 paralist.add(this.userView.getUserDeptId());
	        	 paralist.add(this.userView.getUserPosId());
	        	 paralist.add(id);	        	 
	        	 dao.update(sql,paralist);
	        }
	        catch(Exception sqle)
	        {
	    	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }
        else
        {
	        /**点答复链接后，进行保存处理*/
            cat.debug("reply_update_consulantvo="+vo.toString());
	        try
	        {
	        	 String sql="update consultation set replyuser=?,replytime=?,rcontent=? where id=?";
	        	 paralist.add(this.userView.getUserFullName());
	        	 paralist.add(DateUtils.getTimestamp(new Date())/*DateStyle.getSystemTime()*/);
	        	 paralist.add(vo.getString("rcontent"));
	        	 paralist.add(vo.getString("id"));
	        	 dao.update(sql,paralist);
	        }
	        catch(Exception sqle)
	        {
	    	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }            
        }
    }
	
}
