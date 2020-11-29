package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

/******
 * 招聘职位选择批次时动态填充部分信息
 * <p>Title: SearchBatchInfoTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-11-5 下午07:17:07</p>
 * @author xiexd
 * @version 1.0
 */
public class SearchBatchInfoTrans extends IBusiness {

	
	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//批次编号
		String batchId = (String)this.getFormHM().get("batchId");
		PositionBo bo = new PositionBo(this.frameconn, new ContentDAO(this.getFrameconn()), this.userView);
		LazyDynaBean bean = bo.getBatchInfo(batchId);
		String z0151 = (String)bean.get("z0151");
		String z0153 = (String)bean.get("z0153");
		String z0107 = (String)bean.get("z0107");
		String z0109 = (String)bean.get("z0109");
		CodeItem code = AdminCode.getCode("35", z0151);
		String codeZ0151 = "";
		if(code != null)
		    codeZ0151 = code.getCodename();
		
		this.getFormHM().put("codeZ0151", codeZ0151);
		this.getFormHM().put("z0151", z0151);
		this.getFormHM().put("z0153", z0153);
		this.getFormHM().put("z0107", z0107);
		this.getFormHM().put("z0109", z0109);
	}

}
