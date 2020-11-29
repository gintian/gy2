/*
 * 创建日期 2005-8-25
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.train;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * @author luangaojiong
 *
 * TODO 要更改此生成的类型注释的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
public class AddFirstClearInfoPickTrans extends IBusiness {

	/* （非 Javadoc）
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO 自动生成方法存根
		this.getFormHM().put("r19id","0");
		this.getFormHM().put("newr19id","0");
		this.getFormHM().put("pickTableName","");
		this.getFormHM().put("factNum","");
		this.getFormHM().put("pickInfolst", new ArrayList());
		 DoCodeBean addlist=new DoCodeBean();
		this.getFormHM().put("infoAddList",addlist.getDynamicList(this.getFrameconn()));
		this.getFormHM().put("infoDetailAddList",addlist.getDynamicList(this.getFrameconn(),1));
	}

}
