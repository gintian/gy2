package com.hjsj.hrms.transaction.sys.cms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;


public class ChannelDetailMoveListTrans extends IBusiness {
	public void execute() throws GeneralException{
		try{
			String channel_id = (String)this.getFormHM().get("channel_id");
			String sql = "select title,content_sort,content_id from t_cms_content where channel_id = "+channel_id+" order by content_sort";
			ArrayList move_list = new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				CommonData obj = new CommonData(String.valueOf(this.frowset.getInt("content_id")),this.frowset.getString("title"));
				move_list.add(obj);
			}
			this.getFormHM().put("move_list",move_list);
			this.getFormHM().put("channel_id",channel_id);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
