package com.hjsj.hrms.transaction.welcome;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 * 查看公告回复
 * <p>Title:ViewPronunciamentoRevertTrans.java</p>
 * <p>Description>:ViewPronunciamentoRevertTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 8, 2010 9:38:35 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class ViewPronunciamentoRevertTrans extends  IBusiness{
    public void execute()throws GeneralException{
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
   	    String logid=(String)hm.get("logid");
   	    String opinion=ResourceFactory.getProperty("lable.revert.nohave.content");
   	    if(logid!=null&&logid.length()>0)
   	    {
   	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
   	    	RecordVo vo=new RecordVo("t_keyinfor_log");			
			vo.setString("logid", logid);
			try {
				vo=dao.findByPrimaryKey(vo);
				if(vo!=null)
				{
					opinion=vo.getString("opinion");
					
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
   	    }
   	    this.getFormHM().put("opinion", opinion);
   	
    }

}
