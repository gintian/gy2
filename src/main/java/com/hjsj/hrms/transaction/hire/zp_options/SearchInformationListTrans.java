/*
 * Created on 2005-9-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchInformationListTrans</p>
 * <p>Description:查询面试资料列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchInformationListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn()); 
	    HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String pos_id=(String)hm.get("a_posid");
        String pos_id_value = (String)this.getFormHM().get("pos_id");
		StringBuffer strsql=new StringBuffer();
		if(pos_id != null && !"".equals(pos_id)){
			strsql.append("select test_id,pos_id,name,description,status from zp_pos_test where pos_id = '"+pos_id+"'");
		}else{
			strsql.append("select test_id,pos_id,name,description,status from zp_pos_test where pos_id = '"+pos_id_value+"'");
		}
	    ArrayList list=new ArrayList();
	    ArrayList nameList = new ArrayList();
	    try
	    {
	      ResultSet rs = dao.search(strsql.toString(),nameList);
	      while(rs.next())
	      {
	          RecordVo vo=new RecordVo("zp_pos_test");
	          vo.setString("pos_id",rs.getString("pos_id"));
	          String name=PubFunc.nullToStr(rs.getString("name"));
	          vo.setString("name",name.substring(0,name.length()>20?20:name.length()));
	          vo.setString("description",PubFunc.nullToStr(Sql_switcher.readMemo(rs,"description")));
	          vo.setString("status",rs.getString("status"));
	          vo.setString("test_id",rs.getString("test_id"));
	          list.add(vo);
	      }
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("testQuestionlist",list);
	        this.getFormHM().put("pos_id",pos_id);
	    }

	}

}
