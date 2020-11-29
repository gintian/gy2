package com.hjsj.hrms.transaction.train.attendance.card;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * <p>Title:GetEmpInfoByCardTrans.java</p>
 * <p>Description>:GetEmpInfoByCardTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 14, 2011 3:07:03 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: 郑文龙
 */

public class GetEmpInfoByCardTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
//		String courseplan = (String) this.getFormHM().get("courseplan");
		String classplan = (String) this.getFormHM().get("classplan");
		String card_num = (String) this.getFormHM().get("card_num");
		// 是否选中未排成班的人员刷卡时直接进库时，1为选中，0为未选中
		String into = (String) this.getFormHM().get("into");
		if(classplan == null || classplan.length() < 1 || card_num == null || card_num.length() < 1){
			return;
		} else {
		    classplan = PubFunc.decrypt(SafeCode.decode(classplan));
		}
//		String reg_date = (String) this.getFormHM().get("reg_date");
		ArrayList list = getUserInfo(classplan,card_num);
		
		// 选中未排成班的人员刷卡时直接进库时，
		//输入卡号查询人员范围按管理范围过滤，1为选中，0为未选中
		if ("1".equals(into)) {
			list = this.getUserInfoByPriv(card_num, classplan, list);
		}

		
		this.getFormHM().put("empList", list);
	}

	public ArrayList getUserInfo(String classplan, String card_num) {

		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		ConstantXml constantbo = new ConstantXml(this.frameconn, "TR_PARAM");
		String card_no = constantbo.getTextValue("/param/attendance/card_no");
//		ArrayList nbases = this.userView.getPrivDbList();
		ArrayList nbases = DataDictionary.getDbpreList();
		if (nbases.isEmpty()) {
			return null;
		}
		StringBuffer innTable = new StringBuffer();
		for (int i = 0; i < nbases.size(); i++) {
			String nbase = (String) nbases.get(i);
			innTable.append("SELECT A0100,'" + nbase + "' nbase," + nbase + "A01." + card_no + " FROM " + nbase
					+ "A01 WHERE " + card_no + " LIKE '%" + card_num
					+ "%' UNION ");
		}
		innTable.delete(innTable.length() - 6, innTable.length());
		String sql = "SELECT A0100,R40.nbase,B0110,E0122,R4002,U." + card_no + " FROM R40 INNER JOIN ("
				+ innTable
				+ ") U ON R4001=A0100 AND R40.nbase=U.nbase WHERE R40.R4013='03' AND R40.R4005='"
				+ classplan + "'";
		ArrayList list = new ArrayList();
		try {
			rs = dao.search(sql);
			while (rs.next()) {
//				String A0100 = rs.getString("A0100");
//				String nbase = rs.getString("nbase");
				String A0101 = rs.getString("R4002");
				card_num = rs.getString(card_no);
//				String B0110 = rs.getString("B0110");
//				String E0122 = rs.getString("E0122");
				CommonData cd = new CommonData();
				cd.setDataName(A0101 + "(" + card_num + ")");
				cd.setDataValue(card_num);
				list.add(cd);
//				bean.set("A0100", A0100);
//				bean.set("nbase", nbase);
//				bean.set("B0110", B0110);
//				bean.set("E0122", E0122);
//				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	/**
	 * 根据管理范围和卡号，模糊查询人员
	 * @param card_num String 卡号
	 * @return ArrayList<CommonData>
	 */
	public ArrayList getUserInfoByPriv(String card_num, String classplan, ArrayList list) {

		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		ConstantXml constantbo = new ConstantXml(this.frameconn, "TR_PARAM");
		
		// 考勤卡号指标代码
		String card_no = constantbo.getTextValue("/param/attendance/card_no");

		// 权限人员库
		ArrayList nbases = this.userView.getPrivDbList();
		if (nbases == null ||nbases.size() <= 0) {
			return new ArrayList();
		}
		
		StringBuffer sql = new StringBuffer();
		for (int i = 0; i < nbases.size(); i++) {
			String nbase = (String) nbases.get(i);
			/* ----------------ZWL排除招聘人员库---------------- */
			ConstantXml cx = new ConstantXml(this.frameconn);
			String ZP_DBNAME=cx.getConstantValue("ZP_DBNAME");
			if(ZP_DBNAME != null && ZP_DBNAME.equalsIgnoreCase(nbase)){
				continue;
			}
			/* ----------------ZWL排除招聘人员库---------------- */
			// 管理范围条件
			String where = InfoUtils.getWhereINSql(userView, nbase);
			sql.append("SELECT A0101,");
			sql.append(card_no);
			sql.append(" ");
			sql.append(where);
			if (where.toLowerCase().contains("where")) {
				sql.append(" and ");
				sql.append(card_no);
				sql.append(" like '%");
				sql.append(card_num);
				sql.append("%'");
			} else {
				sql.append(" where ");
				sql.append(card_no);
				sql.append(" like '%");
				sql.append(card_num);
				sql.append("%'");
			}
			
			// 不在排班表中
			sql.append(" and not exists(select 1 from R40 r where r.R4001=");
			sql.append(nbase);
			sql.append("a01.A0100 and upper(r.nbase)='");
			sql.append(nbase.toUpperCase());
			sql.append("' and r.R4013='03' AND r.R4005='");
			sql.append(classplan);
			sql.append("')");			
			sql.append(" UNION ");
		}
		sql.delete(sql.length() - 6, sql.length());
		if (list == null) {
			list = new ArrayList();
		}
		try {
			//如果是oracle库用rownum来查询前20条记录
			if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
				rs = dao.search("select A0101," + card_no + " from ("
						+ sql.toString() + ") where rownum<=20");
			}// 如果是sql库用top来查询前20条记录
			else {
				rs = dao.search("select top 20 A0101," + card_no + " from ("
						+ sql.toString() + ") a");
			}
			while (rs.next()) {
				String A0101 = rs.getString("a0101");
				card_num = rs.getString(card_no);
				CommonData cd = new CommonData();
				cd.setDataName(A0101 + "(" + card_num + ")");
				cd.setDataValue(card_num);
				list.add(cd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

}
