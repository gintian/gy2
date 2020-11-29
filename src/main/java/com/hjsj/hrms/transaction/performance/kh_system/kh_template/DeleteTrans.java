package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:DeleteTrans.java</p>
 * <p>Description>:删除考核模板项目和指标交易</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-7-2 上午10:12:27</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class DeleteTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			/**=1是删除项目，=2是删除指标*/
			String type=(String)this.getFormHM().get("type");
			String itemid=(String)this.getFormHM().get("itemid");
			String pointid=(String)this.getFormHM().get("pointid");
			String templateid=(String)this.getFormHM().get("id");
			String subsys_id=(String)this.getFormHM().get("subsys_id");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			if("1".equals(type))
			{
				bo.deleteItem(itemid, templateid);
			}
			else
			{
				String item_id = bo.getItemidByPointid(pointid, templateid);
				bo.deletePoint(pointid, templateid);
				boolean flag = bo.isHavePoint(item_id);
				if(!flag)
				{
					bo.setKind(item_id, "2");
				}
			}
			this.getFormHM().put("subsys_id", subsys_id);
			this.getFormHM().put("templateid",templateid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
