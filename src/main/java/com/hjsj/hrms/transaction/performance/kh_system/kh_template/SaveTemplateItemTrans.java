package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SaveTemplateItemTrans.java</p>
 * <p>Description>:SaveTemplateItemTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-5-16 上午08:51:55</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SaveTemplateItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			  String template_id = (String)this.getFormHM().get("template_id");
			  String itemdesc = (String)this.getFormHM().get("itemdesc");
			  String parentid = (String)this.getFormHM().get("parentid");
			  /**考虑？？？*/
			  String opt=(String)this.getFormHM().get("opt");//=0是顶级节点，其他不是顶级节点
			  IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			  int id = new Integer(idg.getId("per_template_item.item_Id")).intValue();
			  KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			  int seq =bo.getMaxId("per_template_item","seq");
			  RecordVo vo = new RecordVo("per_template_item");
			  vo.setInt("item_id",id);
			  vo.setInt("seq",seq);
			  vo.setString("template_id",template_id);
			  vo.setString("itemdesc", itemdesc);
			  if(!"0".equals(opt))
				  	 vo.setString("parent_id",parentid);
	    		/**新建的项目。默认为个性项目*/
	    	  vo.setInt("kind",2);
	    	  ContentDAO dao= new ContentDAO(this.getFrameconn());
			  dao.addValueObject(vo);
			  bo.setTemplateItemChild(parentid, template_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
