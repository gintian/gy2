package com.hjsj.hrms.transaction.general.query.general;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * <p>Title:InitGeneraQueryTrans.java</p>
 * <p>Description:初始化通用查询接口</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 14, 2006 2:09:04 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class InitGeneraQueryTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList fieldList=(ArrayList)this.getFormHM().get("fieldList");
		

	}

}
