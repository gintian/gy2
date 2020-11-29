package com.hjsj.hrms.transaction.app_news;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:DeleteAppNews.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class DeleteAppNews extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList list = (ArrayList)this.getFormHM().get("selectedlist");
		RecordVo vo = new RecordVo("appoint_news");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			for(int i=0;i<list.size();i++){
				LazyDynaBean  bean = (LazyDynaBean)list.get(i);
				String news_id = (String)bean.get("news_id");
				vo.setString("news_id",news_id);
				dao.deleteValueObject(vo);
				String filecontent = (String)bean.get("filecontent");
				if("1".equalsIgnoreCase(filecontent)){
					String sql = "select ext_file_id from appoint_news_ext_file where News_id = '"+news_id+"'";
					this.frowset = dao.search(sql);
					if(this.frowset.next()){
						RecordVo extvo = new RecordVo("appoint_news_ext_file");
						String ext_id = this.frowset.getString("ext_file_id");
						extvo.setString("ext_file_id",ext_id);
						dao.deleteValueObject(extvo);
					}
				}
					
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
