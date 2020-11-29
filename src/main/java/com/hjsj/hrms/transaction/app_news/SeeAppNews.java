package com.hjsj.hrms.transaction.app_news;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
/**
 * 
 *<p>Title:SeeAppNews.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SeeAppNews extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//System.out.println("111");
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String newsid = (String)hm.get("news_id");
		String username = (String)hm.get("username");
		if(newsid==null)
			newsid="";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
/*			this.getFormHM().put("title","");
			this.getFormHM().put("inceptname","");
			this.getFormHM().put("disposals","");
			this.getFormHM().put("constant","");
			this.getFormHM().put("days","");*/
			Date date = new Date();
			if(!"".equals(newsid)){
				String selsql = "select * from appoint_news where news_id='"+newsid+"'";
				this.frowset = dao.search(selsql);
				int days= 0;
				if(this.frowset.next()){
					this.getFormHM().put("title",this.frowset.getString("title"));
					this.getFormHM().put("inceptname",this.frowset.getString("inceptuser"));
					this.getFormHM().put("disposals",this.frowset.getString("dis_flag"));
					this.getFormHM().put("constant",this.frowset.getString("content"));
					days = this.frowset.getInt("days");
					date = this.frowset.getDate("sendtime");
					this.getFormHM().put("days",String.valueOf(days));
				}
				RecordVo vo = new RecordVo("appoint_news");
				vo.setString("news_id",newsid);
				vo = dao.findByPrimaryKey(vo);
				Calendar cd = Calendar.getInstance();
				cd.add(Calendar.DAY_OF_MONTH,-days);
				Date d = cd.getTime();
				int result = d.compareTo(date);
				if(result>0&& "1".equals(vo.getString("state"))&&vo.getString("inceptuser").equalsIgnoreCase(userView.getUserName())){
					vo.setInt("state",5);
					dao.updateValueObject(vo);
				}
				else if(result<=0&& "1".equals(vo.getString("state"))&&vo.getString("inceptuser").equalsIgnoreCase(userView.getUserName())){
					vo.setInt("state",2);
					dao.updateValueObject(vo);
				}
				String sql = "select ext_file_id, Name,ext,News_id,createtime from appoint_news_ext_file where news_id = '"+newsid+"'";
				this.frowset = dao.search(sql);
				StringBuffer affixstr = new StringBuffer();
				
				
				int index = 1;
				while(this.frowset.next()){
					if(index==1){
						affixstr.append("<tr class=\"list3\">"+
								"<TD align=\"right\"  >"+
									"附件&nbsp;:&nbsp;&nbsp;"+
								"</TD>");
					}
					else{
						affixstr.append("<tr class=\"list3\">"+
								"<TD align=\"left\"  >"+
									"&nbsp;&nbsp;&nbsp;"+
								"</TD>");
					}
					affixstr.append("<TD> ");
					affixstr.append(index+". "+"<a href=\"/sys/downloadall?id="+this.frowset.getString("ext_file_id")+"&fileid=ext_file_id&tablename=appoint_news_ext_file&filenamecolumn=name&ext=ext&content=content\">" +
							this.frowset.getString("Name")+"</a>");
					affixstr.append("</TD><tr>"+
						"<td height=\"5\"></td>"+
					"</tr>");
					index++;
				}
				affixstr.append("</tr>");
				this.getFormHM().put("affixstr",affixstr.toString());
			}
			
		}catch (SQLException e) {e.printStackTrace();}
	}
}

