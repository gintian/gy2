package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:批量修改</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 26, 2010 4:52:47 PM</p> 
 *@author dengc
 *@version 5.0
 */
public class ButchUpdateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String table_name=(String)this.getFormHM().get("table_name");
			String task_id=(String)this.getFormHM().get("task_id");
			String itemid=(String)this.getFormHM().get("itemid");//修改的是哪个指标  格式：B0110_2
			String formula=(String)this.getFormHM().get("formula");	//修改后的内容
			String cond=(String)this.getFormHM().get("cond");//条件
			String selchecked=(String)this.getFormHM().get("selchecked");//复选框选中
			String whl=PubFunc.keyWord_reback((String)this.getFormHM().get("whl"));//查询过滤条件串
			/**查询过滤条件是加密的,解密回来**/
			whl = PubFunc.decrypt(whl);
			String tabid=(String)this.getFormHM().get("tabid");
			String needcondition=(String) this.userView.getHm().get("template_sql_1");//PubFunc.keyWord_reback((String)this.getFormHM().get("needcondition"));//
			String sp_batch=(String)this.getFormHM().get("sp_batch");
			String batch_task=(String)this.getFormHM().get("batch_task");
			cond=SafeCode.decode(cond);
			formula=SafeCode.decode(formula);
			if("1".equals(sp_batch)&&batch_task!=null){//如果是批量方式
				batch_task =batch_task.replace(",,", ",");
				if(batch_task.startsWith(","))
					batch_task = batch_task.substring(1);
				if(batch_task.endsWith(","))
					batch_task = batch_task.substring(0,batch_task.length()-1);
				task_id =batch_task;
			}
			TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
			bo.batchUpdateItem(itemid,formula,cond,whl,table_name,task_id,selchecked,needcondition);
			
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
