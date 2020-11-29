package com.hjsj.hrms.utils.components.defineformula.transaction.tax;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：DelTaxTableTrans 
 * 类描述： 删除税率表
 * 创建人：zhaoxg
 * 创建时间：Nov 27, 2015 5:28:35 PM
 * 修改人：zhaoxg
 * 修改时间：Nov 27, 2015 5:28:35 PM
 * 修改备注： 
 * @version
 */
public class DelTaxTableTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String taxid = (String) this.getFormHM().get("taxid");
		String taxitem = (String) this.getFormHM().get("taxitem");
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer str = new StringBuffer();
			str.append("delete gz_taxrate_item where taxid="+taxid);
			str.append(" and taxitem in ("+taxitem.substring(1)+")");
			dao.delete(str.toString(), new ArrayList());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
