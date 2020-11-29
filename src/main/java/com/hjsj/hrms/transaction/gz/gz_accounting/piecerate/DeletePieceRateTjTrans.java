package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeletePieceRateTjTrans extends IBusiness{
/**
 * 删除计件统计信息报表
 * 田野
 * 2013-04-03
 */
	public void execute() throws GeneralException {
		StringBuffer info = new StringBuffer();
		try{
			String defId=(String)this.getFormHM().get("defId")==null?"":(String)this.getFormHM().get("defId");
			if(!"".equals(defId)){
				StringBuffer sql = new StringBuffer();
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				sql.append("delete from hr_summarydef where defId in ("+defId+")");
				int  result = dao.update(sql.toString());
				if(result>=1){
					info.append("Success");
				}else{
					info.append("Fail");
				}
			}else{
				info.append("请选择报表！");
			}
			this.getFormHM().put("info",SafeCode.encode(info.toString()));
		}catch (Exception sqle){
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}
}
