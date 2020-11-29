package com.hjsj.hrms.transaction.train.attendance.card;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.attendance.TrainAtteBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title:EmpRegListTrans.java</p>
 * <p>Description>:EmpRegListTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 14, 2011 3:06:03 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: 郑文龙
 */
public class EmpRegListTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String courseplan = (String) this.getFormHM().get("courseplan") != null ? (String) this.getFormHM().get("courseplan") : "";
		if(courseplan != null && courseplan.length() > 0)
		    courseplan = PubFunc.decrypt(SafeCode.decode(courseplan));
		String a_code = (String) this.getFormHM().get("a_code");
		String emp_name = (String) this.getFormHM().get("emp_name");
		String regType = (String) this.getFormHM().get("regType");
		if (emp_name == null) {
			emp_name = "";
		}
		if(regType == null){
			regType = "0";
		}
		a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
		hm.remove("a_code");
		hm.remove("courseplan");
		Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
		if (uplevel == null || uplevel.length() == 0)
			uplevel = "0";
		this.getFormHM().put("uplevel", uplevel);
//		if ("".equals(a_code)
//				&& (userView.getStatus() == 4 || userView.isSuper_admin())) {
//			a_code = this.getUserView().getManagePrivCode()
//					+ this.getUserView().getManagePrivCodeValue();
//		}
//		/** 业务用户走操作单位，没有操作单位时走管理范围 */
//		else if ("".equals(a_code)
//				&& (userView.getStatus() == 0 && !userView.isSuper_admin())) {
//			String codeall = userView.getUnit_id();
//			if (codeall != null && codeall.length() > 2)
//				a_code = codeall.split("`")[0];
//			else if ("".equals(a_code))
//				a_code = this.getUserView().getManagePrivCode()
//						+ this.getUserView().getManagePrivCodeValue();
//		}
		if("".equals(a_code)&&!userView.isSuper_admin()){
			TrainCourseBo bo = new TrainCourseBo(this.userView);
			a_code = bo.getUnitIdByBusi();
			if(a_code.length()<3)
				throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
		}

		DbWizard dbw = new DbWizard(this.getFrameconn());
		if(!dbw.isExistTable("tr_cardtime", false))
			throw new GeneralException(ResourceFactory.getProperty("培训签到表不存在!"));
			
		ConstantXml constantbo = new ConstantXml(this.getFrameconn(),
				"TR_PARAM");
		String card_no = getCardNo(constantbo);
		ArrayList nbases = DataDictionary.getDbpreList();
		// ArrayList nbases = this.userView.getPrivDbList();
		StringBuffer innTable = new StringBuffer();
		for (int i = 0; i < nbases.size(); i++) {
			String nbase = (String) nbases.get(i);
			innTable.append("SELECT  " + nbase + "A01." + card_no + ",R.R4001,R.nbase FROM "
					+ nbase + "A01 INNER JOIN R40 R ON R.R4001=" + nbase
					+ "A01.A0100 AND R.nbase='" + nbase + "' UNION ");
		}
		innTable.delete(innTable.length() - 6, innTable.length());
		String columns = "";
		String sql_str = "";

			columns = "a0100,nbase,r4101,b0110,e0122,a0101," + card_no
			+ ",card_time,card_type,leave_early,late_for";
			sql_str = "SELECT a0100,TR.nbase,r4101,b0110,e0122,a0101,"+ card_no + ",";
			if(Sql_switcher.searchDbServer()==2)
				sql_str += Sql_switcher.dateToChar("card_time", "YYYY-MM-DD HH24:MI:SS");
			sql_str += " card_time,card_type,leave_early,late_for,oper_time";
		String cond_str = " FROM tr_cardtime TR LEFT JOIN (" + innTable
				+ ") A ON TR.A0100=A.R4001 AND TR.nbase=A.nbase";
		
		TrainAtteBo bo = new TrainAtteBo();
		String search=(String)this.getFormHM().get("search");
		String searchstr=  bo.getSearchWhere(search);//条件查询sql
		this.getFormHM().put("search", "");
		
		ArrayList classplanlist = bo.getTrainClass(this.getFrameconn(), a_code);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
//		String classplan=(String)this.getFormHM().get("classplan");
//		try {
//			if (courseplan != null && !"".equals(courseplan))
//				cond_str += " where r4101='" + courseplan
//						+ "' AND a0101 Like '" + emp_name + "%'";
//			else if (classplanlist.size() > 0) {
//				CommonData cd = (CommonData) classplanlist.get(0);
//				String sql = "select r4101 from r41,r13 where r1301=r4105 and r4103='"
//						+ cd.getDataValue() + "'";
//				this.frecset = dao.search(sql);
//				if (this.frecset.next()) {
//					courseplan = this.frecset.getString("r4101");
//					// this.getFormHM().put("courseplan", courseplan);
//					// this.getFormHM().put("classplan", cd.getDataValue());
//					cond_str += " where r4101='" + courseplan + "'";
//				} else
//					cond_str += " where 1=2";
//			} else
//				cond_str += " where 1=2";
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		cond_str += " where r4101='" + courseplan + "' AND TR.a0101 Like '" + emp_name + "%'";
		if("1".equals(regType)){
			cond_str += " AND (card_type = '1' or card_type = '3')";
		} else if("2".equals(regType)){
			cond_str += " AND (card_type = '2' or card_type = '4')";
		}
		String order_str = "ORDER BY oper_time DESC";
        this.userView.getHm().put("train_sql", sql_str + cond_str + searchstr + order_str);
        this.userView.getHm().put("train_columns", columns);
		this.getFormHM().put("order_str", order_str);
		this.getFormHM().put("classplanlist", classplanlist);
		this.getFormHM().put("columns", columns);
		this.getFormHM().put("sql_str", sql_str);
		this.getFormHM().put("cond_str", cond_str + searchstr);
		this.getFormHM().put("card_no", card_no.toLowerCase());
		this.getFormHM().put("isOk", "0");
		this.getFormHM().put("regType", regType);
	}

	/**
	 * 查询考勤卡号是否有效
	 * @param constantbo
	 * @return
	 * @throws GeneralException
	 */
	private String getCardNo(ConstantXml constantbo) throws GeneralException {
		String card_no = constantbo.getTextValue("/param/attendance/card_no");
		if (card_no == null || card_no.length() < 1) {
			throw new GeneralException(ResourceFactory
					.getProperty("train.attendance.set.kqcard")
					+ "!");
		} else {
			FieldItem fieldItem = DataDictionary.getFieldItem(card_no, "A01");
			if (fieldItem == null || !"1".equals(fieldItem.getUseflag())) {
				throw new GeneralException(ResourceFactory
						.getProperty("train.attendance.set.kqcard")
						+ "!");
			}
		}
		return card_no;
	}
}
