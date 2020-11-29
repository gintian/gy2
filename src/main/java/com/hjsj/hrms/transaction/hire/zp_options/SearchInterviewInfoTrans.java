/*
 * Created on 2005-9-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:SearchInterviewInfoTrans</p>
 * <p>Description:新增面试资料</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchInterviewInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String test_id=(String)hm.get("a_testid");
        String pos_id = (String)this.getFormHM().get("pos_id");
        String flag = (String)this.getFormHM().get("flag");
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
        if("1".equals(flag))
            return;
        //ExecuteSQL executeSQL=new ExecuteSQL();
        RecordVo vo=new RecordVo("zp_pos_test");
        try
        {
            vo.setString("test_id",test_id);
            String sql = "select * from zp_pos_test where test_id = "+test_id;
            List rs = ExecuteSQL.executeMyQuery(sql,this.getFrameconn());
        	for(int i=0;rs!=null&&i<rs.size();i++){
	    		LazyDynaBean rec=(LazyDynaBean)rs.get(i);
            	vo.setString("pos_id",rec.get("pos_id").toString());
            	vo.setString("name",rec.get("name").toString());
            	vo.setString("description",PubFunc.nullToStr(rec.get("description").toString()));
            }
        }
        catch(Exception sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("testQuestionvo",vo);
            this.getFormHM().put("pos_id",pos_id);
        }

	}

}
