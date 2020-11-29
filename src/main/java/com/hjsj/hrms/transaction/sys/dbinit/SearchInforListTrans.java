/**
 * 
 */
package com.hjsj.hrms.transaction.sys.dbinit;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:信息群列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 2, 2008:4:03:00 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchInforListTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer buf=new StringBuffer();
		try
		{
			EncryptLockClient lockclient = (EncryptLockClient)this.getFormHM().get("lock");
			VersionControl ver_ctrl=new VersionControl();	
			buf.append("select inforid,Classname,Classpre,Keyfield from informationclass order by inforid");
			RowSet rset=dao.search(buf.toString());
			ArrayList list=new ArrayList();
			while(rset.next())
			{
				String classpre = rset.getString("classpre");
				if("Y".equalsIgnoreCase(classpre)||"V".equalsIgnoreCase(classpre)||"W".equalsIgnoreCase(classpre)){
					if(!lockclient.isHaveBM(31))
						continue;
					if(!ver_ctrl.searchFunctionId("350", userView.hasTheFunction("350")))
						continue;
				}
				if("H".equalsIgnoreCase(classpre)&&!ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012")))
					continue;
				RecordVo vo=new RecordVo("informationclass");
				vo.setInt("inforid", rset.getInt("inforid"));
				vo.setString("classname", rset.getString("classname")+"指标集");
				vo.setString("classpre", rset.getString("classpre"));
				vo.setString("keyfield", rset.getString("keyfield"));
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
