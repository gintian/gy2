package com.hjsj.hrms.transaction.sys.cms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;


public class SaveChannelDetailMoveTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			String[] move_content = (String[])this.getFormHM().get("right_fields");
			String channel_id = (String)this.getFormHM().get("channel_id");
			if(move_content != null&&channel_id != null)
			  newSort(move_content,channel_id);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 将content_sort重新赋值，从0开始
	 * @param move_list
	 * @param channel_id
	 */
	private void newSort(String [] move_list,String channel_id){
		String sql="";
		try{
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    for(int i= 0 ;i<move_list.length;i++){
			     sql = "update t_cms_content set content_sort = "+i+" where content_id = "+move_list[i]+" and channel_id = "+channel_id;
			     dao.update(sql);
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
