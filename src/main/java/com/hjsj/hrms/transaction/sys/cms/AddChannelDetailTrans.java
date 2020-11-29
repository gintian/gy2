package com.hjsj.hrms.transaction.sys.cms;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.Date;


public class AddChannelDetailTrans extends IBusiness {
	/**
	 * 求当前频道下的内容最大顺序号
	 * @param channel_id
	 * @return
	 */
	private int getMaxSortNo(String channel_id)
	{
		int maxsort=1;
		StringBuffer buf=new StringBuffer();
		buf.append("select max(content_sort) as nmax from  t_cms_content where channel_id=");
		buf.append(channel_id);
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
	
	public void execute() throws GeneralException{
		boolean bInsert=false;
		try{
				RecordVo contentvo=(RecordVo)this.getFormHM().get("contentvo");
				
				//String content=contentvo.getString("content");
				//content=content.replaceAll(" ", "");
				//contentvo.setString("content", content);
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String content_id=contentvo.getString("content_id");
				String channel_id=(String)this.getFormHM().get("channel_id");
				if(channel_id==null|| "-1".equals(channel_id))
					return;
				String visible=contentvo.getString("visible");
				if(visible==null|| "".equalsIgnoreCase(visible))
					contentvo.setInt("visible", 0);
				/**根据内容序号是否为空
				 * 分析是新增记录，还是保存记录
				 */
				if(content_id==null|| "".equalsIgnoreCase(content_id))
				{
					bInsert=true;
				    IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			        content_id =idg.getId("t_cms_content.content_id");					
					contentvo.setInt("content_id", Integer.parseInt(content_id));
					contentvo.setInt("channel_id", Integer.parseInt(channel_id));
					int maxno=getMaxSortNo("channel_id");
					contentvo.setInt("content_sort", maxno);
				}
				contentvo.setInt("state", 0);
				contentvo.setDate("news_date", new Date());
				contentvo.setString("create_user", this.userView.getUserFullName());
				String content=contentvo.getString("content");
				content=PubFunc.hireKeyWord_filter_reback(content);
				content=content.replaceAll("&sup1;", "1");
				content=content.replaceAll("&sup2;", "2");
				content=content.replaceAll("&sup3;","3");
				content=content.replaceAll("&ordm;","o");
				content=content.replaceAll("&acirc;","a");
				content=content.replaceAll("&eth;","d");
				content=content.replaceAll("&yacute;","y");
				content=content.replaceAll("&thorn;","t");	
				content=content.replaceAll("&ETH;","D");
				content=content.replaceAll("&THORN;","T");
				content=content.replaceAll("&Yacute;","Y");
				/*fckeditor 提交内容过滤注入js代码  guodd 2019-05-06 */
				content=PubFunc.stripScriptXss(content);
				contentvo.setString("content",content);
				String out_url = contentvo.getString("out_url");
				out_url = PubFunc.keyWord_reback(out_url);
				contentvo.setString("out_url", out_url);
				//【6089】招聘：外网内容管理参数输入的英文，保存后成中文了，点击社会招聘报错  jingq add 2014.12.17
				String params = contentvo.getString("params");
				//params = PubFunc.keyWord_reback(params);
				params = params.replaceAll("？", "?");
				params = params.replaceAll("／", "/");
				params = params.replaceAll("＝", "=");
				params = params.replaceAll("＆", "&");
				contentvo.setString("params", params);
				if(!bInsert)
					dao.updateValueObject(contentvo);
				else
				    dao.addValueObject(contentvo);				
				/*
				String temp_channel_id =(String)this.getFormHM().get("channel_id");
				String title =(String)this.getFormHM().get("title");
				String temp_content_type = (String)this.getFormHM().get("content_type");
				String temp_visible=(String)this.getFormHM().get("visible");
				String out_url=(String)this.getFormHM().get("out_url");
				String params =(String)this.getFormHM().get("params");
				String create_user=this.userView.getUserName();
				String content=(String)this.getFormHM().get("content");
				Date news_date = new Date();
				int content_id = 0;
				int flag=0;
				String temp_content_id =(String)this.getFormHM().get("content_id");
				if(temp_content_id!= null && temp_content_id.trim().length()>0 && !temp_content_id .equals("0")){
					content_id = Integer.parseInt((String)this.getFormHM().get("content_id"));
					flag=1;
				}else{
				    IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			        content_id = Integer.parseInt(idg.getId("t_cms_content.content_id"));
				}
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				RecordVo vo = new RecordVo("t_cms_content");
				
				vo.setInt("channel_id",Integer.parseInt(temp_channel_id));
				vo.setString("title",title);
				vo.setInt("content_type",Integer.parseInt(temp_content_type));
				vo.setInt("content_id",content_id);
				vo.setInt("visible",Integer.parseInt(temp_visible));
				vo.setString("out_url",out_url);
				vo.setString("params",params);
				vo.setDate("news_date",news_date);
				vo.setString("create_user",create_user);
				vo.setString("content",content);
				*/
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
