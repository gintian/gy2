/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:执行人事变动或薪资变动计算</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-8:上午10:33:21</p> 
 *@author cmq
 *@version 4.0
 */
public class BatchComputeBzTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("tabid");
			/**如果ins_id=0,表示对与用户有关的临时表进行计算，否则对*/
			String ins_id=(String)this.getFormHM().get("ins_id");
			String ins_ids=(String)this.getFormHM().get("ins_ids");
			String selfapply=(String)this.getFormHM().get("selfapply");
			
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			if(selfapply!=null&& "1".equals(selfapply))
				tablebo.setBEmploy(true);
			if(ins_ids!=null&&ins_ids.indexOf(",")!=-1)
			{
				String[] insids=ins_ids.split(",");
				for(int i=0;i<insids.length;i++)
				{
					if(insids[i].trim().length()>0)
					{
						tablebo.batchCompute(insids[i].trim());
					}
				}
			}
			else
				tablebo.batchCompute(ins_id);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
