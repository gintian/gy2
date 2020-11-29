package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:EditTemplateItemTrans.java</p>
 * <p>Description>:EditTemplateItemTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-10-27 下午02:14:54</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class EditTemplateItemTrans extends IBusiness{

	
	public void execute() throws GeneralException {
		try
		{
			String itemid=(String)this.getFormHM().get("itemid");
			String itemdesc=SafeCode.decode((String)this.getFormHM().get("itemdesc"));
			RecordVo vo = new RecordVo("per_template_item");
			vo.setString("item_id",itemid);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			vo=dao.findByPrimaryKey(vo);
			vo.setString("itemdesc", itemdesc);
			dao.updateValueObject(vo);
			itemdesc=SafeCode.encode(PubFunc.toHtml(itemdesc));
			this.getFormHM().put("itemdesc", itemdesc);
			this.getFormHM().put("itemid", itemid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
