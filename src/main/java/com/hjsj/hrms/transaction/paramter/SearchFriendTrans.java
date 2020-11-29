package com.hjsj.hrms.transaction.paramter;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @author chenmengqing
 */
public class SearchFriendTrans extends IBusiness {

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String id=(String)hm.get("a_id");
        String flag=(String)this.getFormHM().get("flag");
        /**按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑 用户的使用习惯。*/
        if("1".equals(flag))
            return;
        cat.debug("------>suggest_id====="+id);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("hr_friend_website");
        try
        {
        	  StringBuffer strsql=new StringBuffer();
        	  strsql.append("select site_id,log_icon,url,name from hr_friend_website where  site_id='");
         	  strsql.append(id);
        	  strsql.append("'");         	  
              vo.setString("site_id",id);
		      this.frowset=dao.search(strsql.toString());
		      if(this.frowset.next())
		      {
		      	vo.setString("site_id",frowset.getString("site_id"));
		        vo.setString("url",frowset.getString("url"));
		        vo.setString("name",frowset.getString("name"));
		      }
        }
        catch(Exception sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
          this.getFormHM().put("friendTb",vo);
        }
    }

}