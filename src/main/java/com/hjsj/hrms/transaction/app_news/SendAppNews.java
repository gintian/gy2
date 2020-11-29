package com.hjsj.hrms.transaction.app_news;


import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Date;
/**
 * 
 *<p>Title:SendAppNews.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SendAppNews extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//System.out.println("111");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		//String inceptname = (String)this.getFormHM().get("inceptname");
		String inceptnameid = (String)this.getFormHM().get("inceptnameid");
		String dbname = "",nameid = "";
		if(inceptnameid!=null&&!"".equals(inceptnameid)&&inceptnameid.length()==11){
			dbname = inceptnameid.substring(0,3);
			nameid = inceptnameid.substring(3);
		}
		String sql = "",A0101 = "";
		if(!"".equals(nameid))
			sql = "select UserName from "+dbname+"A01 where A0100 = '"+nameid+"'";
		if(!"".equals(sql)){
			try {
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					A0101 = this.frowset.getString("UserName");
					if(A0101==null)
						throw new GeneralException("", "此用户未设定登录帐号，请选择其他用户，会给其设定登录帐号","", "");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
			
		String title = (String)this.getFormHM().get("title");
		String constant = (String)this.getFormHM().get("constant");
		/*fckeditor 提交内容过滤注入js代码  guodd 2019-05-06 */
		constant = PubFunc.stripScriptXss(constant);
		String senduser = userView.getUserName();
		String days = (String)this.getFormHM().get("days");
		String disposals = (String)this.getFormHM().get("disposals");
		//FormFile file = (FormFile)this.getFormHM().get("newsfile");
		String states = (String)this.getFormHM().get("state");
		String news_id = (String)this.getFormHM().get("news_id");
		int state = 1;
		if(!(states==null|| "".equalsIgnoreCase(states))){
			state = Integer.parseInt(states);
		}
		//String fname = "", ext = "";
		
		RecordVo vo = new RecordVo("appoint_news");
		String id ="";
		if("".equalsIgnoreCase(news_id)||news_id==null){
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			id = idg.getId("appoint_news.id");
			vo.setString("news_id",id);
			this.getFormHM().put("news_id",id);
		}
		else
			vo.setString("news_id",news_id);
		vo.setString("senduser",senduser);
		vo.setDate("sendtime",new Date());
		if(!"".equals(A0101))
			vo.setString("inceptuser",A0101);
		else
			vo.setString("inceptuser",inceptnameid);
		vo.setString("title",title);
		vo.setString("content",constant);
		if(!"".equalsIgnoreCase(days))
			vo.setInt("days",Integer.parseInt(days));
		if(disposals!=null&&!"".equalsIgnoreCase(disposals))
			vo.setInt("dis_flag",Integer.parseInt(disposals));
		vo.setInt("state",state);
		
		if("".equalsIgnoreCase(news_id)||news_id==null)
			dao.addValueObject(vo);
		else
			try {
				dao.updateValueObject(vo);
			} catch (GeneralException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		this.getFormHM().put("state","4");
		this.getFormHM().put("isdraft","0");
		this.getFormHM().put("inceptnameid","");
			
	}

}
