package com.hjsj.hrms.transaction.performance.achivement.standarditem;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class AddStandardItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			//opt=0增加同级=1增加下级=2插入=3编辑
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)map.get("opt");
			String itemid=(String)map.get("itemid");
			String ruletype=(String)map.get("ruletype");
			if("3".equals(opt))
			{
				this.getFormHM().put("type","1");
				RecordVo vo = new RecordVo("per_standard_item");
				vo.setInt("item_id",new Integer(itemid).intValue());
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				vo=dao.findByPrimaryKey(vo);
				this.getFormHM().put("itemdesc",vo.getString("itemdesc"));
				this.getFormHM().put("score",vo.getString("score")==null?"":PubFunc.round(vo.getString("score"),2));
				this.getFormHM().put("top_value",vo.getString("top_value")==null?"":PubFunc.round(vo.getString("top_value"),2));
				this.getFormHM().put("bottom_value", vo.getString("bottom_value")==null?"":PubFunc.round(vo.getString("bottom_value"),2));
			}
			else{
		
				this.getFormHM().put("itemdesc","");
				this.getFormHM().put("score","");
				this.getFormHM().put("top_value","");
				this.getFormHM().put("bottom_value","");
				this.getFormHM().put("type","0");
			}
			this.getFormHM().put("ruletype",ruletype);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
