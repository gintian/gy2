package com.hjsj.hrms.transaction.gz.voucher;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
* 
* 类名称：DeleteAccountTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 16, 2013 5:39:56 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 16, 2013 5:39:56 PM   
* 修改备注：   删除科目
* @version    
*
 */
public class DeleteAccountTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String ids=(String)this.getFormHM().get("deleteIds");
			String[] idArr= ids.split(",");
			StringBuffer sb= new StringBuffer();
			for(int i=0;i<idArr.length;i++){
				sb.append(",'");
				sb.append(idArr[i]);
				sb.append("'");
			}
				String sql = " delete from GZ_code where i_id in("+sb.toString().substring(1)+")";
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				dao.delete(sql,new ArrayList());
			
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
