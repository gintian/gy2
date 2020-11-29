/**
 * 
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 */
public class BackDateTrans extends IBusiness {

	/**
	 * 
	 */
	public BackDateTrans() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ArrayList archivedatelist = new ArrayList();
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = "select archive_date from hr_org_catalog";
			this.frecset = dao.search(sql);
			while(this.frecset.next()){
				CommonData dataobj = new CommonData(sdf.format(this.frecset.getTimestamp("archive_date")),sdf.format(this.frecset.getTimestamp("archive_date")));
				archivedatelist.add(dataobj);
			}
		}catch(Exception e){
			
		}finally{
			this.getFormHM().put("archivedatelist", archivedatelist);
		}
	}

}
