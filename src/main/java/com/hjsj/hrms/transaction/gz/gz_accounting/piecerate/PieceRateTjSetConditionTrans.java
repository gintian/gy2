package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceReportDefBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class PieceRateTjSetConditionTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();	
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String tjWhere= (String)reqhm.get("tjWhere");
		/* 安全问题 sql-in-url 计件薪资-报表 xiaoyun 2014-9-18 start */
		tjWhere = PubFunc.decrypt(SafeCode.decode(tjWhere));
		//tjWhere=SafeCode.decode(tjWhere);
		//tjWhere=PubFunc.keyWord_reback(tjWhere);
		/* 安全问题 sql-in-url 计件薪资-报表 xiaoyun 2014-9-18 end */
		tjWhere=tjWhere!=null&&tjWhere.length()>0?tjWhere:"";
		
		PieceReportDefBo defBo =new PieceReportDefBo(this.frameconn);
		hm.put("tjWhere",tjWhere);
		hm.put("cond_setId","");
		hm.put("cond_setList",defBo.getCondSetList(true));			
	}

}
