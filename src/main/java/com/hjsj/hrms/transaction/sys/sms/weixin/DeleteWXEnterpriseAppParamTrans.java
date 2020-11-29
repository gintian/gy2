package com.hjsj.hrms.transaction.sys.sms.weixin;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 *
 * @Titile: DeleteWXEnterpriseAppParamTrans
 * @Description:删除企业号应用
 * @Company:hjsj
 * @Create time: 2018年6月27日下午2:12:00
 * @author: wangbs
 * @version 1.0
 *
 */
public class DeleteWXEnterpriseAppParamTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			String itemId = (String) this.formHM.get("itemid");//应用编号
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sql = new StringBuffer();
			List list = new ArrayList();
			sql.append("delete from t_sys_weixin_param where wxitemid=?");
			list.add(itemId);
			
			int result = 0;
			result = dao.delete(sql.toString(),list);
			if(result==1){
				this.formHM.put("result", true);
			}else {
				this.formHM.put("result", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
