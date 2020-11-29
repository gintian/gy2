package com.hjsj.hrms.transaction.kq.machine.select;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class InitSelectFieldsTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList fieldlist = new ArrayList();
		fieldlist = this.newFieldList();
		this.getFormHM().put("fieldlist", fieldlist);
		this.getFormHM().put("selectedlist", new ArrayList());
	}

	/**
	 * 返回显示的所有选择字段
	 */
	private ArrayList newFieldList() {
		ArrayList fieldlist = new ArrayList();
		//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
		HashMap hashmap = para.getKqParamterMap();
		String g_no = (String) hashmap.get("g_no");
		String cardno = (String) hashmap.get("cardno");
		//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		CommonData cd = new CommonData("B0110", "单位");
		fieldlist.add(cd);
		cd = new CommonData("E0122", "部门");
		fieldlist.add(cd);
		cd = new CommonData("A0101", "姓名");
		fieldlist.add(cd);
		cd = new CommonData(g_no, "工号");
		fieldlist.add(cd);
		cd = new CommonData(cardno, "考勤卡号");
		fieldlist.add(cd);
		return fieldlist;
	}

}
