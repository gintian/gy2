package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceReportDefBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class PieceRateTjSetGroupTrans extends IBusiness {

	public void execute() throws GeneralException {
		String selectFields=(String)this.getFormHM().get("rightFields");
		if (selectFields==null) selectFields="";
		//处理重复的字段
		String[] arrFlds= selectFields.split(",");
		String strFlds="";
		for (int i=0;i<arrFlds.length;i++){
			if ((strFlds+",").indexOf(arrFlds[i])<0){				
				strFlds=strFlds+","+arrFlds[i];			
			}
		}
		if (strFlds.length()>0)
			strFlds= strFlds.substring(1);
		this.getFormHM().put("rightFields",strFlds);

		PieceReportDefBo defBo =new PieceReportDefBo(this.frameconn);		
		this.getFormHM().put("selectedFieldList",defBo.getSelectedFiledList(strFlds));
	}


}
