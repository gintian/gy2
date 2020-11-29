package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.businessobject.sys.warn.ContextTools;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 刷新预警结果
 * <p>Title:RefreshWarnResultDetailTrans.java</p>
 * <p>Description>:RefreshWarnResultDetailTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 18, 2010 11:41:49 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class RefreshWarnResultDetailTrans extends IBusiness implements IConstant {



	public void execute() throws GeneralException {
		String strWid = (String) ((HashMap) this.getFormHM().get(Key_Request_Param_HashMap)).get("warn_wid");
		//获取封装当前预警条件的动态BEAN
		DynaBean dbean = (DynaBean) ContextTools.getWarnConfigCache().get(
				strWid);
		ArrayList alBasePre = DataDictionary.getDbpreList();	
		try {
			ScanTrans.runWarn(dbean,alBasePre);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
