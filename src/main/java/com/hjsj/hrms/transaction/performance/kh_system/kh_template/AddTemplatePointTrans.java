package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:AddTemplatePointTrans.java</p>
 * <p>Description>:AddTemplatePointTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-5-16 下午01:08:48</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class AddTemplatePointTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String itemid = (String)this.getFormHM().get("itemid");
			String pointid = (String)this.getFormHM().get("pointid");
			/**模板的权重标识*/
			String status =(String)this.getFormHM().get("status");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			String msg = "0";
			boolean flag = bo.isHavePoint(itemid, pointid);
			if(flag)// 已经存在了
			{
				msg="1";
			}
			else
			{
	    		ContentDAO dao = new ContentDAO(this.getFrameconn());
	    		int seq = bo.getMaxId("per_template_point","seq");
	    		RecordVo vo = new RecordVo("per_template_point");
	    		vo.setString("item_id",itemid);
	    		vo.setString("point_id",pointid);
	    		vo.setInt("seq",seq);
	    		if("0".equals(status))
	    		{
	    			vo.setInt("rank", 1);
	    		}
    			dao.addValueObject(vo);
    		}
			this.getFormHM().put("msg",msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
