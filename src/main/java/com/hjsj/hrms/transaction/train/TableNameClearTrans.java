/*
 * 创建日期 2005-8-23
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.train;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author luangaojiong
 *
 * 清除操作
 */
public class TableNameClearTrans extends IBusiness {

	/* （非 Javadoc）
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		this.getFormHM().put("pickTableName","");
		this.getFormHM().put("factNum","");
		 DoCodeBean addlist=new DoCodeBean();
		 this.getFormHM().put("infoAddList",addlist.getDynamicList(this.getFrameconn()));
		 this.getFormHM().put("infoDetailAddList",addlist.getDynamicList(this.getFrameconn(),1));
	}

}
