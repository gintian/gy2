package com.hjsj.hrms.transaction.app_news;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
/**
 * 
 *<p>Title:TimingSearch.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class TimingSearch implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		Connection conn=null;
		String selsql = "select News_id,sendtime,Days,Dis_flag from appoint_news ";
		ArrayList idlist = new ArrayList();
		ArrayList sendtimelist = new ArrayList();
		ArrayList Dayslist = new ArrayList();
		ArrayList flaglist = new ArrayList();
		RecordVo vo = new RecordVo("appoint_news");
		RowSet rset=null;
		try {
			conn = (Connection) AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rset = dao.search(selsql);
			while(rset.next()){
				idlist.add(rset.getString("News_id"));
				sendtimelist.add(rset.getDate("sendtime"));
				Dayslist.add(rset.getInt("Days")+"");
				flaglist.add(rset.getInt("Dis_flag")+"");
			}
		
			if(idlist.size()>0){
				for(int i=0;i<idlist.size();i++){
					Calendar cd = Calendar.getInstance();
					cd.add(Calendar.DAY_OF_MONTH,-Integer.parseInt((String)Dayslist.get(i)));
					Date d = cd.getTime();
					int result = d.compareTo((Date)sendtimelist.get(i));
					if(result>0){
						if("0".equalsIgnoreCase(flaglist.get(i).toString())){
							vo.setString("news_id",idlist.get(i).toString());
							vo = dao.findByPrimaryKey(vo);
							dao.deleteValueObject(vo);
							String sql = "select ext_file_id from appoint_news_ext_file where News_id = '"+idlist.get(i)+"'";
							RowSet filers = dao.search(sql);
							if(filers.next()){
								RecordVo extvo = new RecordVo("appoint_news_ext_file");
								String ext_id = filers.getString("ext_file_id");
								extvo.setString("ext_file_id",ext_id);
								dao.deleteValueObject(extvo);
							}
						}
						else{
							vo.setString("news_id",idlist.get(i).toString());
							vo = dao.findByPrimaryKey(vo);
							vo.setInt("state",0);
							dao.updateValueObject(vo);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally{
			try {
				if(rset!=null)
					rset.close();
				if(conn!=null)
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
