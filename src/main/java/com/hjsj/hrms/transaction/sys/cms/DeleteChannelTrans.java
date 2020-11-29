package com.hjsj.hrms.transaction.sys.cms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;


public class DeleteChannelTrans extends IBusiness{
	
	/**
	 * 删除频道下的内容
	 * @param channel_id
	 */
	private void deleteContent(String channel_id)throws GeneralException
	{
		try
		{
			  StringBuffer buf=new StringBuffer();
			  ContentDAO dao=new ContentDAO(this.getFrameconn());
			  buf.append("delete from t_cms_content where channel_id=");
			  buf.append(channel_id);
			  dao.update(buf.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}
	/**
	 * 删除子节点
	 * @param parent_id
	 */
	private void deleteChild(String channel_id)throws GeneralException
	{
	  try
	  {
		  StringBuffer buf=new StringBuffer();
		  ContentDAO dao=new ContentDAO(this.getFrameconn());
		  /**查找子节点*/
		  buf.append("select channel_id from t_cms_channel where channel_id<>parent_id and parent_id=");
		  buf.append(channel_id);
		  RowSet rset=dao.search(buf.toString());
		  while(rset.next())
		  {
			  String temp=rset.getString("channel_id");
			  deleteContent(temp);
			  /**删除子节点，递归删除*/
			  deleteChild(temp);
		  }//while loop end.
		  /**删除此节点*/
		  buf.setLength(0);
		  buf.append("delete from t_cms_channel where channel_id=");
		  buf.append(channel_id);
		  dao.update(buf.toString());
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  throw GeneralExceptionHandler.Handle(ex);		  
	  }
	}
	
	public void execute() throws GeneralException{
		try
		{
			int channel_id = Integer.parseInt((String)this.getFormHM().get("channel_id"));
			deleteChild(String.valueOf(channel_id));
			this.getFormHM().clear();
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
