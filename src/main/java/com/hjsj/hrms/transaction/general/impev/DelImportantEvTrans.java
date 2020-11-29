/**
 * 
 */
package com.hjsj.hrms.transaction.general.impev;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:DelImportantEvTrans
 * </p>
 * <p>
 * Description:删除重要信息报告
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class DelImportantEvTrans extends IBusiness {

	/**
	 * 
	 */
	public DelImportantEvTrans() {
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {

		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String selecteds = (String)hm.get("selecteds");
		selecteds = selecteds!=null&&selecteds.trim().length()>0?selecteds:"";
		hm.remove("selecteds");
		String[] p0600s = selecteds.split(",");
		StringBuffer sql = new StringBuffer();
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			sql.append("delete from p06 where p0600 in(");
			for(int i=0;i<p0600s.length;i++){
				sql.append(p0600s[i]+",");
			}
			sql.append("0)");
			dao.delete(sql.toString(), new ArrayList());
		}catch(Exception e){
			
		}finally{
			
		}
	}

}
