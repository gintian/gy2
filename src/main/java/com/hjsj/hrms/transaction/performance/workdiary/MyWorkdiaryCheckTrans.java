package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * <p>Title:MyWorkdiaryCheckTrans.java</p>
 * <p>Description>:MyWorkdiaryCheckTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Sep 10, 2010 12:52:06 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao
 */
public class MyWorkdiaryCheckTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList list=new ArrayList();
		String a0100=this.getUserView().getA0100();
		WorkdiarySelStr selStr=new WorkdiarySelStr();
		////日志报批只支持直管领导，不支持像“公司领导”、“同级”之类的考核关系。如果一个对象有多个直管领导，那么报批的时候会弹出一个框，让你选择到底要报给谁   郭峰注释
		ArrayList listldb = selStr.getSuperiorUser(a0100, new ContentDAO(this.getFrameconn()));
		for (int i = 0; i < listldb.size(); i++) {
			LazyDynaBean ldb=(LazyDynaBean) listldb.get(i);
			list.add(ldb.get("username")+":"+ldb.get("a0101"));
		}
		this.getFormHM().put("outname", list);
	}

}
