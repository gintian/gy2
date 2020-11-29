package com.hjsj.hrms.transaction.app_news;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class WrtieMessage extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String newsid = (String)this.getFormHM().get("news_id");
		if(newsid==null|| "".equals(newsid))
			newsid="";
		ContentDAO dao = new ContentDAO(this.frameconn);
		String isdraft = (String)this.getFormHM().get("isdraft");
		if(!"0".equalsIgnoreCase(isdraft))
			this.getFormHM().put("news_id","");
		try {
			this.getFormHM().put("title","");
			this.getFormHM().put("inceptname","");
			this.getFormHM().put("disposals","0");
			this.getFormHM().put("constant","");
			this.getFormHM().put("days","");
			if(!"".equals(newsid)){
				String selsql = "select * from appoint_news where news_id='"+newsid+"'";
				this.frowset = dao.search(selsql);
				while(this.frowset.next()){
					this.getFormHM().put("title",this.frowset.getString("title"));
					this.getFormHM().put("inceptname",this.frowset.getString("inceptuser"));
					this.getFormHM().put("disposals",this.frowset.getString("dis_flag"));
					this.getFormHM().put("constant",this.frowset.getString("content"));
					int days = this.frowset.getInt("days");
					this.getFormHM().put("days",String.valueOf(days));
					this.getFormHM().put("inceptnameid",this.frowset.getString("inceptuser"));
				}
			}
		}catch (SQLException e) {e.printStackTrace();}
	}

}
