package com.hjsj.hrms.transaction.kq.options.sign_point.person;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:InitCommonQueryTrans</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-28:13:39:51</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class InitCommonQueryTrans extends IBusiness {

	public void execute() throws GeneralException {
		  try
		  {
			  HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String priv=(String)hm.get("priv");
			priv=priv!=null?priv:"1";
			hm.remove("priv");
			/**未定义信息类别,默认为人员信息*/
			String infor_kind=(String)this.getFormHM().get("type");
			if(infor_kind==null|| "".equals(infor_kind))
				infor_kind="1";
			this.getFormHM().put("type",infor_kind);
			/**
			 * 权限范围内的人员库列表
			 */
			
			if(hm.get("isGetSql")!=null)
			{
				this.getFormHM().put("isGetSql","1");
				hm.remove("isGetSql");
			}
			else
				this.getFormHM().put("isGetSql","0");
			
			this.getFormHM().put("chpriv",priv);
			this.getFormHM().put("selectedlist",new ArrayList());
			this.getFormHM().put("expression","");
			this.getFormHM().put("factorlist",  new ArrayList());
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);
		  }

	}

}
