package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：PerTemplateStatusTrans 
 * 类描述： 返回模板的权重分值标识
 * 创建人：zhaoxg
 * 创建时间：Nov 19, 2015 4:39:52 PM
 * 修改人：zhaoxg
 * 修改时间：Nov 19, 2015 4:39:52 PM
 * 修改备注： 
 * @version
 */
public class PerTemplateStatusTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try{
			String template_id = (String) this.getFormHM().get("template_id");
			String status =  "0";
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select status from per_template   where template_id='"+template_id+"'";
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				status = this.frowset.getString("status");
			}
			this.getFormHM().put("status", status);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
