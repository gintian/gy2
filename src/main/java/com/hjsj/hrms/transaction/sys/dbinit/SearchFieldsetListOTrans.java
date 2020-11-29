package com.hjsj.hrms.transaction.sys.dbinit;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:子集交易</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 2, 2008:4:51:44 PM</p>
 * @author wangyao
 * @version 1.0
 * 
 */
public class SearchFieldsetListOTrans extends IBusiness{
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer buf=new StringBuffer();
		/**信息群前缀*/
		String infor =(String)this.getFormHM().get("infor");
		try
		{
			buf.append("select fieldsetid,fieldsetdesc,useflag,changeflag,customdesc,multimedia_file_flag from fieldset where fieldsetid like '"+infor+"%' order by displayorder");
			RowSet rset=dao.search(buf.toString());
			ArrayList list=new ArrayList();
			while(rset.next())
			{
				RecordVo vo=new RecordVo("fieldset");
				vo.setString("fieldsetid", rset.getString("fieldsetid"));
				vo.setString("fieldsetdesc", rset.getString("fieldsetdesc"));
				vo.setString("customdesc", rset.getString("customdesc"));
				vo.setString("useflag", rset.getString("useflag"));
				vo.setString("changeflag", rset.getString("changeflag"));
				vo.setString("multimedia_file_flag", rset.getString("multimedia_file_flag"));
				list.add(vo);
			}
			this.getFormHM().put("list", list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
