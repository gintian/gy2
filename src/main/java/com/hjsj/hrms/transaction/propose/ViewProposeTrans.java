package com.hjsj.hrms.transaction.propose;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author chenmengqing
 */
public class ViewProposeTrans extends IBusiness {

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String id=(String)hm.get("a_id");
        String flag=(String)this.getFormHM().get("flag");
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
        if(!"2".equals(flag))
            return;
        cat.debug("------>suggest_id====="+id);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("suggest");
        try
        {
            vo.setString("id",id);
            vo=dao.findByPrimaryKey(vo);
            /**进行回车换行*/
            vo.setString("scontent",PubFunc.toHtml(vo.getString("scontent")));
            vo.setString("rcontent",PubFunc.toHtml(vo.getString("rcontent")));
            cat.debug("------->Vo="+vo.getString("scontent"));
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("proposevo",vo);
        }
    }

}
