package com.hjsj.hrms.transaction.propose;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * @author chenmengqing
 */
public class SaveProposeTrans extends IBusiness {

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        RecordVo vo=(RecordVo)this.getFormHM().get("proposevo");
        if(vo==null)
            return;
        String flag=(String)this.getFormHM().get("flag");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag))
        {
            /**
             * 新加建议，进行保存处理
             */
        	getSaveRecordVo(vo);     
        	try
			{
        		dao.addValueObject(vo);
			}
        	catch(Exception ex)
			{
        		ex.printStackTrace();
	    	    throw GeneralExceptionHandler.Handle(ex);     
        	}
        }
        else if("0".equals(flag))
        {
	        /**
	         * 点编辑链接后，进行保存处理
	         */
            cat.debug("update_proposevo="+vo.toString());
	        try
	        {
	         	String check="";
	        	if("".equals(this.getFormHM().get("check").toString()))
	        	{
	        		vo.setString("annymous","0");
	        	}
	        	else
	        	{
	        	    check=this.getFormHM().get("check").toString();
	        	  	 if("on".equals(check))
	        	 	{        	
	        			vo.setString("annymous","1");
	        	 	}
	        	 	else
	        	 	{
	        	 		vo.setString("annymous","0");
	        	 	}
	        	}
	        	dao.updateValueObject(vo);
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }
        else
        {
	        /**
	         * 点答复链接后，进行保存处理
	         */
        	String replayCheck=this.getFormHM().get("replayCheck").toString();
        	if("on".equals(replayCheck))
        	{
        		vo.setString("flag","1");
        	}
        	else
        	{
        		vo.setString("flag","0");
        	}
            vo.setString("replyuser",this.userView.getUserFullName());
            vo.setDate("replytime",DateStyle.getSystemTime());            
            cat.debug("reply_update_proposevo="+vo.toString());
	        try
	        {
	            dao.updateValueObject(vo);
	        }
	        catch(SQLException sqle)
	        {
	    	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }            
        }
    }

	/**
	 * @param vo
	 * @throws GeneralException
	 */
	private void getSaveRecordVo(RecordVo vo) throws GeneralException {
		String check="";
		if(this.getFormHM().get("check")==null || "".equals(this.getFormHM().get("check").toString()))
		{
			vo.setString("annymous","0");
		}
		else
		{
		    check=this.getFormHM().get("check").toString();
		  	if("off".equals(check))
		 	{        		
				vo.setString("annymous","0");
		 	}
		 	else if("on".equals(check))
		 	{        	
				vo.setString("annymous","1");
		 	}
		 	else
		 	{
		 		vo.setString("annymous","0");
		 	}
		}
		IDGenerator idg=new IDGenerator(2,this.getFrameconn());
		String id=idg.getId("SUGGEST.ID");
		vo.setString("id",id);
		vo.setString("createuser",this.userView.getUserFullName());
		vo.setDate("createtime",DateStyle.getSystemTime());
		vo.setString("b0110",this.userView.getUserOrgId());
		vo.setString("e0122",this.userView.getUserDeptId());
		vo.setString("e01a1",this.userView.getUserPosId());
		if(vo.hasAttribute("replytime"))
			vo.removeValue("replytime");
		  
		/**不公开意见*/
		vo.setString("flag","0");
		cat.debug("add_proposevo="+vo.toString());
	}

}
