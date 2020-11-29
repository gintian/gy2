package com.hjsj.hrms.transaction.propose;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
/**
 * @author chenmengqing
 */
public class SearchConsulantTrans extends IBusiness {

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String id = PubFunc.decrypt((String)hm.get("a_id"));
        String flag=(String)this.getFormHM().get("flag");
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
        if("1".equals(flag))
            return;
        cat.debug("------>suggest_id====="+id);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("consultation");
        try
        {
        	  StringBuffer strsql=new StringBuffer();
        	  strsql.append("select id,createuser,createtime,ccontent,replyuser,replytime,rcontent,B0110,E0122,E01A1 from consultation where id=");
        	  strsql.append(id);
              vo.setString("id",id);
		      this.frowset=dao.search(strsql.toString());
		      if(this.frowset.next())
		      {
		    	//bug:35277 已答复问题不允许再次修改
		    	if(StringUtils.isNotEmpty(frowset.getString("replyuser"))
		    			||StringUtils.isNotEmpty(PubFunc.FormatDate(frowset.getDate("replytime")))
		    			||StringUtils.isNotEmpty(Sql_switcher.readMemo(frowset,"rcontent")))
		    		throw GeneralExceptionHandler.Handle(new Exception("该问题已答复，不允许修改!"));
		        vo.setString("id",frowset.getString("id"));
		        vo.setString("createuser",frowset.getString("createuser"));
		        vo.setDate("createtime",PubFunc.FormatDate(frowset.getDate("createtime")));
		        vo.setString("ccontent",Sql_switcher.readMemo(frowset,"ccontent"));
		        vo.setString("replyuser",frowset.getString("replyuser"));
		        vo.setDate("replytime",PubFunc.FormatDate(frowset.getDate("replytime")));
			    vo.setString("rcontent",Sql_switcher.readMemo(frowset,"rcontent"));
		      }
        }
        catch(Exception sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("cousulantTb",vo);
        }
    }

}
