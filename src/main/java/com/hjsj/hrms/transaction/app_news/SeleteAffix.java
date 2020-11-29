package com.hjsj.hrms.transaction.app_news;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

public class SeleteAffix extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String news_id = (String)this.getFormHM().get("news_id");
		String sql = "select ext_file_id, Name,ext,News_id,createtime from appoint_news_ext_file where news_id = '"+news_id+"'";
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("ext_file_id",this.frowset.getString("ext_file_id"));
				bean.set("name",this.frowset.getString("Name"));
				bean.set("ext",this.frowset.getString("ext"));
				bean.set("news_id",this.frowset.getString("News_id"));
				bean.set("createtime",this.frowset.getDate("createtime"));
				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("affixlist",list);
	}

}
