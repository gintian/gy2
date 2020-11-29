package com.hjsj.hrms.transaction.app_news;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:SearchAppNews.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SearchAppNews extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//System.out.println("111");
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String type = (String)hm.get("type");
		String newsid = (String)hm.get("news_id");
		if(newsid==null|| "".equals(newsid))
			newsid="";
		this.getFormHM().put("type",type);
		String sendtime = (String)this.getFormHM().get("sendtime");
		String sendtimeto = (String)this.getFormHM().get("sendtimeto");
		String state = (String)this.getFormHM().get("state");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if("select".equalsIgnoreCase(type)){
			
			if(sendtime==null|| "".equals(sendtime))
				sendtime = "";
			if(sendtimeto==null|| "".equals(sendtimeto))
				sendtimeto = "";
			if(state==null|| "4".equals(state))
				state = "";
			StringBuffer sql = new StringBuffer();
			//sql.append("select appoint_news.news_id news_id,senduser,sendtime,inceptuser,title,appoint_news.content newscontent,days,dis_flag,state,appoint_news_ext_file.content filecontent from appoint_news left join appoint_news_ext_file on appoint_news.news_id = appoint_news_ext_file.news_id where (senduser = '"+userView.getUserName()+"' or (inceptuser = '"+userView.getUserName()+"' and state <> 0))");
			sql.append("select appoint_news.news_id news_id,senduser,sendtime,inceptuser,title,days,dis_flag,state,appoint_news_ext_file.news_id ext_id from appoint_news left join appoint_news_ext_file on appoint_news.news_id = appoint_news_ext_file.news_id where (inceptuser = '"+userView.getUserName()+"' and state <> 0)");
			if(!"".equals(state)){
				//if(state.equalsIgnoreCase("2")){
					sql.append(" and (state = "+Integer.parseInt(state)+" and inceptuser ='"+ userView.getUserName()+"') ");
				/*}else{
					sql.append(" and (state = "+Integer.parseInt(state)+" and senduser ='"+ userView.getUserName()+"') ");
				}*/
			}
			if(!"".equals(sendtime)){
				sql.append(" and sendtime >="+Sql_switcher.dateValue(sendtime+" 00:00:00"));
			}
			if(!"".equals(sendtimeto)){
				sql.append(" and sendtime <= "+Sql_switcher.dateValue(sendtimeto+" 23:59:59"));
			}
			ArrayList rolelist = new ArrayList();
			sql.append(" order by sendtime");
			try {
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next()){
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("news_id",this.frowset.getString("news_id"));
					bean.set("senduser",this.frowset.getString("senduser"));
					bean.set("sendtime",this.frowset.getString("sendtime"));
					bean.set("inceptuser",this.frowset.getString("inceptuser"));
					//bean.set("content",this.frowset.getString("newscontent"));
					int days = this.frowset.getInt("days");
					bean.set("days",String.valueOf(days));
					int states = this.frowset.getInt("state");
					String statesvalue= "";
					if(states==0)
						statesvalue="起草";
					else if(states==1)
						statesvalue="未读";
					else if(states==2)
						statesvalue="已读";
					else if(states==3)
						statesvalue="超期";
					else if(states==5)
						statesvalue="超期已读";
					bean.set("statesvalue",statesvalue);
					bean.set("states",String.valueOf(states));
					String in = this.frowset.getString("ext_id");
					if(states==1&&this.frowset.getString("inceptuser").equalsIgnoreCase(userView.getUserName()))
						bean.set("title",this.frowset.getString("title")+"<font color='red'> 新</font>");
					else
						bean.set("title",this.frowset.getString("title"));
					if(in==null)
						bean.set("filecontent","0");
					else
						bean.set("filecontent","1");
					if(this.frowset.getString("senduser").equalsIgnoreCase(userView.getUserName()))
						bean.set("username","1");
					else
						bean.set("username","0");
					if(this.frowset.getString("inceptuser").equalsIgnoreCase(userView.getUserName()))
						bean.set("del","1");
					else
						bean.set("del","0");
					rolelist.add(bean);
				}
			} catch (SQLException e) {e.printStackTrace();}
				this.getFormHM().put("rolelist",rolelist);
				this.getFormHM().put("rolelist2",rolelist);
				//this.getFormHM().put("news_id","");
		}
		else if("receive".equalsIgnoreCase(type)){
			
			if(sendtime==null|| "".equals(sendtime))
				sendtime = "";
			if(sendtimeto==null|| "".equals(sendtimeto))
				sendtimeto = "";
			if(state==null|| "4".equals(state))
				state = "";
			StringBuffer sql = new StringBuffer();
			//sql.append("select appoint_news.news_id news_id,senduser,sendtime,inceptuser,title,appoint_news.content newscontent,days,dis_flag,state,appoint_news_ext_file.content filecontent from appoint_news left join appoint_news_ext_file on appoint_news.news_id = appoint_news_ext_file.news_id where (senduser = '"+userView.getUserName()+"' or (inceptuser = '"+userView.getUserName()+"' and state <> 0))");
			sql.append("select distinct appoint_news.news_id news_id,senduser,sendtime,inceptuser,title,days,dis_flag,state,appoint_news_ext_file.news_id ext_file_id from appoint_news left join appoint_news_ext_file on appoint_news.news_id = appoint_news_ext_file.news_id where senduser = '"+userView.getUserName()+"'");
			if(!"".equals(state)){
				/*if(state.equalsIgnoreCase("2")){
					sql.append(" and (state = "+Integer.parseInt(state)+" and inceptuser ='"+ userView.getUserName()+"') ");
				}else{*/
					sql.append(" and (state = "+Integer.parseInt(state)+" and senduser ='"+ userView.getUserName()+"') ");
				//}
			}
			if(!"".equals(sendtime)){
				sql.append(" and sendtime >="+Sql_switcher.dateValue(sendtime+" 00:00:00"));
			}
			if(!"".equals(sendtimeto)){
				sql.append(" and sendtime <= "+Sql_switcher.dateValue(sendtimeto+" 23:59:59"));
			}
			ArrayList rolelist = new ArrayList();
			sql.append(" order by sendtime");
			try {
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next()){
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("news_id",this.frowset.getString("news_id"));
					bean.set("senduser",this.frowset.getString("senduser"));
					bean.set("sendtime",this.frowset.getString("sendtime").substring(0,19));
					bean.set("inceptuser",this.frowset.getString("inceptuser"));
					//bean.set("content",this.frowset.getString("newscontent"));
					int days = this.frowset.getInt("days");
					bean.set("days",String.valueOf(days));
					int states = this.frowset.getInt("state");
					String statesvalue= "";
					if(states==0)
						statesvalue="起草";
					else if(states==1)
						statesvalue="未读";
					else if(states==2)
						statesvalue="已读";
					else if(states==3)
						statesvalue="超期";
					else if(states==5)
						statesvalue="超期已读";
					bean.set("statesvalue",statesvalue);
					bean.set("states",String.valueOf(states));
					String ext_file_id = this.frowset.getString("ext_file_id");
					if(states==1&&this.frowset.getString("inceptuser").equalsIgnoreCase(userView.getUserName()))
						bean.set("title",this.frowset.getString("title")+"<font color='red'> 新</font>");
					else
						bean.set("title",this.frowset.getString("title"));
					if(ext_file_id==null)
						bean.set("filecontent","0");
					else
						bean.set("filecontent","1");
					if(this.frowset.getString("senduser").equalsIgnoreCase(userView.getUserName()))
						bean.set("username","1");
					else
						bean.set("username","0");
					if(this.frowset.getString("inceptuser").equalsIgnoreCase(userView.getUserName()))
						bean.set("del","1");
					else
						bean.set("del","0");
					rolelist.add(bean);
				}
			} catch (SQLException e) {e.printStackTrace();}
				this.getFormHM().put("rolelist",rolelist);
				this.getFormHM().put("rolelist2",rolelist);
				//this.getFormHM().put("news_id","");
		}
		/*else if(type.equalsIgnoreCase("send")){
			try {
				this.getFormHM().put("title","");
				this.getFormHM().put("inceptname","");
				this.getFormHM().put("disposals","");
				this.getFormHM().put("constant","");
				this.getFormHM().put("days","");
				if(!newsid.equals("")){
					String selsql = "select * from appoint_news where news_id='"+newsid+"'";
					this.frowset = dao.search(selsql);
					while(this.frowset.next()){
						this.getFormHM().put("title",this.frowset.getString("title"));
						this.getFormHM().put("inceptname",this.frowset.getString("inceptuser"));
						this.getFormHM().put("disposals",this.frowset.getString("dis_flag"));
						this.getFormHM().put("constant",this.frowset.getString("content"));
						int days = this.frowset.getInt("days");
						this.getFormHM().put("days",String.valueOf(days));
					}
				}
				
			}catch (SQLException e) {e.printStackTrace();}
		}*/
	}

}
