package com.hjsj.hrms.transaction.kq.options.imports;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * <p>Title:清除考勤规则导入指标</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 29, 2010:5:47:13 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class DeleImportTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String akq_item1=(String)hm.get("akq_item1"); //id
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		sql.append("update kq_item set other_param='' where item_id='"+akq_item1+"'");
		try
		{
			dao.update(sql.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
