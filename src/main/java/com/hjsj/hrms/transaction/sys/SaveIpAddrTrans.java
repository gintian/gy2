package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 * <p>Title:SaveIpAddrTrans</p>
 * <p>Description:保存</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 7, 2005:10:22:36 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveIpAddrTrans extends IBusiness {

    /**
     * 
     */
    public SaveIpAddrTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        RecordVo vo=(RecordVo)this.getFormHM().get("ip_vo");
        if(vo==null)
            return;
        //系统管理IP地址新增修改，jingq add 2014.09.21
        vo.setString("description", PubFunc.keyWord_reback(vo.getString("description")));
        String flag=(String)this.getFormHM().get("flag");
        ContentDAO dao=new ContentDAO(this.getFrameconn());        
        if("1".equals(flag))
        {
            /**
             * 新建，进行保存处理
             */
            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
            String id=idg.getId("ip_address.id");
            vo.setString("id",id);
            if(vo.getString("valid")==null|| "".equals(vo.getString("valid")))
                vo.setString("valid","0");
            cat.debug("ip_vo="+vo.toString());           
            dao.addValueObject(vo);            
        }
        else 
        {
	        /**
	         * 点编辑链接后，进行保存处理
	         */
            cat.debug("ip_vo="+vo.toString());
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

}
