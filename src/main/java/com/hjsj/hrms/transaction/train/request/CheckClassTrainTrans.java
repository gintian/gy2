package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class CheckClassTrainTrans extends IBusiness {

	public void execute() throws GeneralException {

		String code = "";

		// 管理范围
		TrainCourseBo tbo = new TrainCourseBo(this.userView);
		code = tbo.getUnitIdByBusi();

		TrainClassBo bo = new TrainClassBo(this.getFrameconn());

		ArrayList formulaList = bo.getPxTrainFormulaList();

		ArrayList list = new ArrayList();
		TransDataBo tdb = new TransDataBo(this.frameconn);
		ArrayList fieldlist = tdb.filedItemList();
		StringBuffer sql = new StringBuffer();
		String wherestr = bo.getWherestr(formulaList, userView);// 公式的结果
		String sqlwhere = "";
		if (code != null && code.length() > 0 && code.indexOf("UN`") == -1) {
			String[] b0110 = code.split("`");
			for (int j = 0; j < b0110.length; j++) {
				sqlwhere = "b0110 like '" + b0110[j] + "%' or";
			}
			sqlwhere = sqlwhere.substring(0, sqlwhere.length() - 3);
		} else if (code == null || code.length() < 1 || code.indexOf("UN`") != -1) {
			sqlwhere = "1=1";
		}
		sql.append("select r3130");
		sql.append(" from r31");
		sql.append(" where " + wherestr);
		sql.append(" and (" + sqlwhere + ") and r3127<>'06'");

		for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			if("r3130".equalsIgnoreCase(fielditem.getItemid()))
				list.add(fielditem);
		}
		
		
		this.getFormHM().put("sql",sql.toString());
		this.getFormHM().put("columns", "r3130");
		this.getFormHM().put("wherestr", "");
		this.getFormHM().put("itemlist", list);
	}
}
