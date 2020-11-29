package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
/**
 * 上会材料--同步投票结果（票数）
 * @author haosl
 *
 * 2017-07-18
 */
public class SyncVoteStatus extends IBusiness {
	private static final long serialVersionUID = 1L;

	@Override
	public void execute() throws GeneralException {
		try {
			String w0301 = this.getFormHM().get("w0301")==null?"":(String)this.getFormHM().get("w0301");
			if(StringUtils.isNotBlank(w0301))
				w0301 = PubFunc.decrypt(w0301);
			ReviewFileBo bo = new ReviewFileBo(this.getFrameconn());
			// 同步鉴定专家人数
			bo.asyncPersonNum(w0301);
			//同步投票 状态
			bo.asyncStatus(w0301);
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
