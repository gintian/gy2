package com.hjsj.hrms.transaction.browse.history;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SelectSnapshotTrans.java</p>
 * <p>Description>:SelectSnapshotTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 19, 2010 5:19:29 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class SelectSnapshotTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList selectedlist=new ArrayList();
		String sql="select fieldSetId,fieldSetDesc from fieldSet where fieldsetid like 'A%'";
		try {
			RowSet rs = dao.search(sql);
			while(rs.next()){
				CommonData obj=new CommonData(rs.getString("fieldSetId"),rs.getString("fieldSetDesc"));
				selectedlist.add(obj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("selectedlist",selectedlist);
	}

}
