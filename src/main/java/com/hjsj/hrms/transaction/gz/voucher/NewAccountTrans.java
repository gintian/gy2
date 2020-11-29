package com.hjsj.hrms.transaction.gz.voucher;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;
/**
 * 
* 
* 类名称：NewAccountTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 16, 2013 5:44:46 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 16, 2013 5:44:46 PM   
* 修改备注：   修改科目
* @version    
*
 */
public class NewAccountTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		ContentDAO dao = new ContentDAO(this.frameconn);
		try{
			String i_id = (String) map.get("i_id");
			String sql = "select * from GZ_code where i_id='"+i_id+"'";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				this.getFormHM().put("accid", rs.getString("ccode"));
				this.getFormHM().put("accname", rs.getString("ccode_name"));
				this.getFormHM().put("accgrade", rs.getString("igrade"));
				this.getFormHM().put("flag", "2");
				this.getFormHM().put("i_id", i_id);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
