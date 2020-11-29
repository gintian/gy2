package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 * <p>Title:SearchIpAddrTrans</p>
 * <p>Description:根据传过来的a_ip_id查询对应的对象</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 7, 2005:10:22:17 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchIpAddrTrans extends IBusiness {

    /**
     * 
     */
    public SearchIpAddrTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String ip_id=(String)hm.get("a_ip_id");
        String flag=(String)this.getFormHM().get("flag");
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
        if("1".equals(flag))
            return;
        cat.debug("------>a_ip_id====="+ip_id);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("ip_address");
        try
        {
            vo.setString("id",ip_id);
            vo=dao.findByPrimaryKey(vo);
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("ip_vo",vo);
        }

    }

}
