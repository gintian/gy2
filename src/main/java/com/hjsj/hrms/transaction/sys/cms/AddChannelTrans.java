package com.hjsj.hrms.transaction.sys.cms;


import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;

public class AddChannelTrans extends IBusiness {
	
	/**
	 * 求当前频道下的内容最大顺序号
	 * @param channel_id
	 * @return
	 */
	private int getMaxSortNo(String parent_id)
	{
		int maxsort=1;
		StringBuffer buf=new StringBuffer();
		buf.append("select max(chl_sort) as nmax from  t_cms_channel where parent_id=");
		buf.append(parent_id);
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(buf.toString());
			if(rset.next())
				maxsort=rset.getInt("nmax")+1;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return maxsort;
	}	
	
	public void execute () throws GeneralException{
		try{
		    DynaBean channel_vo = (LazyDynaBean)this.getFormHM().get("channel_vo");
			String name=(String)channel_vo.get("name");
			String function_id =(String)channel_vo.get("function_id");
			String temp_visible=(String)channel_vo.get("visible");
			String temp_visible_type =(String)channel_vo.get("visible_type");
			String parent_id =(String)this.getFormHM().get("parent_id");
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
			int visible = 0;
			int visible_type = 0;
			int id=0;
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			id=  Integer.parseInt(idg.getId("t_cms_channel.channel_id"));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("t_cms_channel");
			vo.setInt("channel_id",id);
			vo.setInt("menu_width", Integer.parseInt(menu_width));
			vo.setInt("icon_width",Integer.parseInt(icon_width));
			vo.setInt("icon_height",Integer.parseInt(icon_height));
			vo.setString("name",name);
			vo.setString("function_id",function_id);
			vo.setInt("chl_sort", getMaxSortNo(parent_id));
			if(temp_visible == null && temp_visible.trim().length() ==0){
				visible = 0;
			}else if("1".equals(temp_visible)){
				visible = 1;
			}
			vo.setInt("visible",visible);
			if("0".equals(temp_visible_type)){
				visible_type = 0;
			}else if("1".equals(temp_visible_type)){
				visible_type = 1;
			}
			vo.setInt("visible_type",visible_type);
			if("-1".equals(parent_id))
				vo.setInt("parent_id",id);	
			else
				vo.setInt("parent_id",Integer.parseInt(parent_id));
			if(icon_url != null)
				vo.setString("icon_url",icon_url);
			vo.setInt("state", 0);
			dao.addValueObject(vo);
			this.getFormHM().put("channel_id", String.valueOf(id));
			this.getFormHM().put("parent_id",parent_id);
			this.getFormHM().put("name",name);
			
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
