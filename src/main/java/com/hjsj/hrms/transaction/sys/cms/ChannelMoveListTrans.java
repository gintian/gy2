package com.hjsj.hrms.transaction.sys.cms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;


public class ChannelMoveListTrans extends IBusiness{
	public void execute() throws GeneralException{
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String channel_id = (String)hm.get("channel_id");
		int parent_id = 0;
		String isTop = "";
		String str = "select parent_id from t_cms_channel where channel_id = "+channel_id;
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(str);
			while(this.frowset.next()){
				parent_id = this.frowset.getInt("parent_id");
			}
			StringBuffer sql = new StringBuffer();
			if(parent_id == Integer.parseInt(channel_id)){//查的是顶级节点
				sql.append("select name,channel_id,chl_sort from t_cms_channel ");
				sql.append("where channel_id = parent_id ");
				sql.append("order by chl_sort");
				isTop = "yes";
			}else{//不是顶级节点
		       sql.append("select name,channel_id,chl_sort from t_cms_channel where parent_id = ");
		       sql.append(parent_id);
		       sql.append(" and channel_id <>");
		       sql.append(parent_id);
		       sql.append(" order by chl_sort");
		       isTop = "no";
			}
			ArrayList list = new ArrayList();
		    this.frowset = dao.search(sql.toString());
		    while(this.frowset.next()){
		    	CommonData obj = new CommonData(String.valueOf(this.frowset.getInt("channel_id")),this.frowset.getString("name"));
		    	list.add(obj);
		    }
		    this.getFormHM().put("list",list);
		    this.getFormHM().put("parent_id",String.valueOf(parent_id));
		    this.getFormHM().put("isTop",isTop);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
