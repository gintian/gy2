/**
 * <p>Title:咨询查询</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-1:19:14:20</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
package com.hjsj.hrms.transaction.propose;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewConsulantTrans extends IBusiness 
{
	 public void execute() throws GeneralException 
	 {
        HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");        
        String id = PubFunc.decrypt((String)hm.get("a_id"));
        String flag=(String)this.getFormHM().get("flag");
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
        if(!"2".equals(flag))
            return;
        cat.debug("------>consultation_id====="+id);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("consultation");
        try
        {
              vo.setString("id",id);
              StringBuffer strsql=new StringBuffer();
        	  strsql.append("select id,createuser,createtime,ccontent,replyuser,replytime,rcontent,B0110,E0122,E01A1 from consultation where id=");
        	  strsql.append(id);
              vo.setString("id",id);
		      this.frowset=dao.search(strsql.toString());
		      if(this.frowset.next())
		      {
		          vo.setString("id",frowset.getString("id"));
		          String temp="";
		          vo.setString("createuser",frowset.getString("createuser"));
		          vo.setDate("createtime",PubFunc.FormatDate(frowset.getDate("createtime")));
		          temp=Sql_switcher.readMemo(frowset,"ccontent");
		          vo.setString("ccontent",temp);
		          vo.setString("replyuser",frowset.getString("replyuser"));
		          vo.setDate("replytime",PubFunc.FormatDate(frowset.getDate("replytime")));
		          temp=Sql_switcher.readMemo(frowset,"rcontent");
			      vo.setString("rcontent",temp);
		      }
		    //vo=dao.findByPrimaryKey(vo);
            /**进行回车换行*/
            String content=vo.getString("ccontent");
            content=PubFunc.toHtml(content);
            vo.setString("ccontent",content);
            String rcontent=vo.getString("rcontent");
            rcontent=PubFunc.toHtml(rcontent);
            vo.setString("rcontent",rcontent);
            this.getFormHM().put("cousulantTb",vo);
            cat.debug("------->Vo="+vo.getString("ccontent"));
        }
        catch(Exception sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
    }
	
 }



