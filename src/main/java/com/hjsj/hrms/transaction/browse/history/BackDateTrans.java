package com.hjsj.hrms.transaction.browse.history;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:BackDateTrans.java</p>
 * <p>Description>:BackDateTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 23, 2010 12:01:20 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class BackDateTrans extends IBusiness{

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		List list1=new ArrayList();
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			RowSet rs=dao.search("select id,create_date,description from hr_hisdata_list order by id desc");
			while(rs.next()){
				java.sql.Date date=rs.getDate("create_date");
				//CommonData obj=new CommonData(sdf.format(date),rs.getString("description"));
				List list=new ArrayList();
				list.add(rs.getString("id"));
				list.add(sdf.format(date));
				list.add(rs.getString("description"));
				list1.add(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("list1", list1);
	}
}
