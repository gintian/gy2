package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;


public class InitItemRankTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		    HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		    String item_id=(String)map.get("itemid");
		    String temp_id=(String)map.get("temp_id");
		    KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
		    ArrayList itemList = bo.getItemListToConfigRank(item_id, temp_id);
		    RecordVo vo = new RecordVo("per_template");
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    vo.setString("template_id", temp_id);
		    vo=dao.findByPrimaryKey(vo);
		    StringBuffer infos=new StringBuffer("");
		    infos.append(vo.getString("name")+"("+ResourceFactory.getProperty("label.kh.template.total")+":"+PubFunc.round(vo.getString("topscore"),2)+")");
		    this.getFormHM().put("itemList", itemList);
		    this.getFormHM().put("infos", infos.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
