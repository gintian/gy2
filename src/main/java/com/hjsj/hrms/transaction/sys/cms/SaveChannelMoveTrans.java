package com.hjsj.hrms.transaction.sys.cms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

public class SaveChannelMoveTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
		   DynaBean vo = (DynaBean)this.getFormHM().get("channel_vo");
		   String move_list =(String)vo.get("move_list");
		   String[] move_channel = move_list.split("/");
		   String parent_id = (String)vo.get("parent_id");
		   String isTop = (String)vo.get("isTop");
		   if("yes".equals(isTop)&& move_list.trim().length()>0)
			   newSort(move_channel);
		   else if("no".equals(isTop)&& move_list.trim().length()>0)
		       newSort(move_channel,parent_id);
		  }catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/**
	 * 将chl_sort重新赋值，从0开始,不是顶级节点
	 * @param move_list
	 * @param parent_id
	 */
	private void newSort(String [] move_list,String parent_id){
		String sql="";
		try{
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    for(int i= 0 ;i<move_list.length;i++){
			     sql = "update t_cms_channel set chl_sort = "+i+" where channel_id = "+move_list[i]+" and parent_id = "+parent_id;
			     dao.update(sql);
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 将chl_sort重新赋值，从0开始,是顶级节点
	 * @param move_list
	 */
	private void newSort(String[] move_list){
		String sql = "";
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			for(int i=0;i<move_list.length;i++){
				sql = "update t_cms_channel set chl_sort = "+i+" where channel_id = "+move_list[i]+" and channel_id = parent_id";
				dao.update(sql);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
