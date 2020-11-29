package com.hjsj.hrms.transaction.sys.cms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;


public class EditChannelTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			DynaBean channel_vo = (LazyDynaBean)this.getFormHM().get("channel_vo");
			String name=(String)channel_vo.get("name");
			String function_id =(String)channel_vo.get("function_id");
			String temp_visible=(String)channel_vo.get("visible");
			String temp_visible_type =(String)channel_vo.get("visible_type");
			//String parent_id =(String)channel_vo.get("parent_id");
			String icon_url = (String)channel_vo.get("icon_url");
			String icon_width=(String)channel_vo.get("icon_width");
			String icon_height=(String)channel_vo.get("icon_height");
			String menu_width=(String)channel_vo.get("menu_width");
			if(menu_width==null|| "".equalsIgnoreCase(menu_width))
				menu_width="70";
			if(icon_width==null|| "".equalsIgnoreCase(icon_width))
				icon_width="60";
			if(icon_height==null|| "".equalsIgnoreCase(icon_height))
				icon_height="20";				
			
			String temp_channel_id = (String)this.getFormHM().get("channel_id");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("t_cms_channel");
			vo.setInt("menu_width", Integer.parseInt(menu_width));
			vo.setInt("icon_width",Integer.parseInt(icon_width));
			vo.setInt("icon_height",Integer.parseInt(icon_height));			
			vo.setString("name",name);
			vo.setString("function_id",function_id);
			vo.setInt("visible",Integer.parseInt(temp_visible));
			vo.setInt("visible_type",Integer.parseInt(temp_visible_type));
			//vo.setInt("parent_id",Integer.parseInt(parent_id));
			vo.setString("icon_url",icon_url);
			vo.setInt("channel_id",Integer.parseInt(temp_channel_id));
			dao.updateValueObject(vo);
			this.getFormHM().put("name",name);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
