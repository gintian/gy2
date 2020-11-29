/*
 * Created on 2005-8-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_resource;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:SearchZpresourceSetTrans</p>
 * <p>Description:查询招聘资源分类,zp_resource_set</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class SearchZpresourceSetTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String type_id=(String)hm.get("a_id");
        String flag_set=(String)this.getFormHM().get("flag_set");
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
        if("1".equals(flag_set))
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("zp_resource_set");
        try
        {
            vo.setString("type_id",type_id);
            String sql = "select * from zp_resource_set where type_id = '"+type_id+"'";
            this.frowset = dao.search(sql);
            while(this.frowset.next()){
            	vo.setString("name",this.getFrowset().getString("name"));
            	vo.setString("status",this.getFrowset().getString("status"));
            }
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("zpresourceSetvo",vo);
        }

	}

}
