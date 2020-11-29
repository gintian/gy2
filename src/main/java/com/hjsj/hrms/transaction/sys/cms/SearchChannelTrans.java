package com.hjsj.hrms.transaction.sys.cms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchChannelTrans extends IBusiness{
	public void execute() throws GeneralException{
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		try{
			if("link".equals((String)hm.get("b_query")))
			{
		    	this.getFormHM().put("name","");
		    	this.getFormHM().put("function_id","");
		    	this.getFormHM().put("visible","1");
		    	this.getFormHM().put("visible_type","0");
		    	this.getFormHM().put("icon_url","");
				return;
			}
			else if("query".equals((String)hm.get("b_query")))
			{
				int id = Integer.parseInt((String)hm.get("channel_id"));
				String sql ="select * from t_cms_channel where channel_id = "+id;
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				this.frowset = dao.search(sql);
				while(this.frowset.next()){
					this.getFormHM().put("channel_id",this.frowset.getString("channel_id"));
					this.getFormHM().put("name", this.frowset.getString("name")==null?"":this.frowset.getString("name"));
					this.getFormHM().put("function_id",this.frowset.getString("function_id")==null?"":this.frowset.getString("function_id"));
					this.getFormHM().put("visible",this.frowset.getInt("visible")+"");
					this.getFormHM().put("visible_type",this.frowset.getString("visible_type"));
					this.getFormHM().put("icon_url",this.frowset.getString("icon_url")==null?"":this.frowset.getString("icon_url"));
					this.getFormHM().put("icon_width",this.frowset.getString("icon_width")==null?"0":this.frowset.getString("icon_width"));
					this.getFormHM().put("icon_height",this.frowset.getString("icon_height")==null?"0":this.frowset.getString("icon_height"));
					this.getFormHM().put("menu_width",this.frowset.getString("menu_width")==null?"0":this.frowset.getString("menu_width"));

					this.getFormHM().put("parent_id",this.frowset.getString("parent_id"));
				}
		  }
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
