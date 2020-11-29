/**
 * 
 */
package com.hjsj.hrms.transaction.sys.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:InitQueryTemplateTrans</p>
 * <p>Description:初始化查询模板值</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-3-1:9:31:18</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class InitQueryTemplateTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		  StringBuffer sql=new StringBuffer();
		  ContentDAO dao=null;
		  ArrayList inforlist=new ArrayList();
		  try
		  {
			/**信息分类列表*/
			sql.append("select classname,classpre from informationclass");
			dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			VersionControl ver_ctrl=new VersionControl();
			EncryptLockClient lockclient = (EncryptLockClient)this.getFormHM().get("lock");
			while(this.frowset.next())
			{
				String classpre = this.frowset.getString("classpre");
				if("Y".equalsIgnoreCase(classpre)||"V".equalsIgnoreCase(classpre)||"W".equalsIgnoreCase(classpre)){
					if(!lockclient.isHaveBM(31))
						continue;
					if(!ver_ctrl.searchFunctionId("350", userView.hasTheFunction("350")))
						continue;
				}
				if("H".equalsIgnoreCase(classpre)&&!ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012")))
					continue;
				CommonData vo=new CommonData(classpre,this.frowset.getString("classname"));
				inforlist.add(vo);
			}
			this.getFormHM().put("inforlist",inforlist);
			
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);
		  }
	}

}
