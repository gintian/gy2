/*
 * Created on 2005-5-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.propose;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteHtmlFileTrans extends IBusiness {

	 /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        ArrayList proposelist=(ArrayList)this.getFormHM().get("selectedlist");
        if(proposelist==null||proposelist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
        	deleteFile(proposelist,dao);
            dao.deleteValueObject(proposelist);
        }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

    }

    /**
     * xus 20/5/15 删除vfs中的文件
     * @param proposelist
     * @param dao
     */
	private void deleteFile(ArrayList proposelist, ContentDAO dao) {
		for(Object o : proposelist) {
			RecordVo vo = (RecordVo)o;
			String contentid = (String)vo.getString("contentid");
			String sql = "select fileid from resource_list where contentid = ? ";
			ArrayList values = new ArrayList();
			values.add(contentid);
			try {
				this.frowset = dao.search(sql, values);
				if(this.frowset.next()) {
					if(StringUtils.isNotBlank(this.frowset.getString("fileid"))) {
						String fileid = this.frowset.getString("fileid");
						VfsService.deleteFile(this.getUserView().getUserName(), fileid);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


}
