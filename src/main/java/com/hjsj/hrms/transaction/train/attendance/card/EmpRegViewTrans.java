package com.hjsj.hrms.transaction.train.attendance.card;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.HashMap;
/**
 * 
 * <p>Title:EmpRegViewTrans.java</p>
 * <p>Description>:EmpRegViewTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 14, 2011 3:06:07 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: 郑文龙
 */
public class EmpRegViewTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String courseplan = (String) hm.get("courseplan");// 培训课程
		String classplan = (String) hm.get("classplan");// 培训班
		String regFlag = (String) hm.get("flag"); // 签到签退标记
		hm.remove("courseplan");
		hm.remove("classplan");
		hm.remove("flag");
//		Date date = new Date();
//		String nowDate = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
//		String nowTime = DateUtils.format(date, "mm:ss");
//		TrainAtteBo bo = new TrainAtteBo();
//		RecordVo vo = new RecordVo("tr_cardtime");
//		RecordVo vo = bo.getRegStateVo(regFlag, date, courseplan,
//				this.frameconn);
//		if(vo == null){
//			if("1".equals(regFlag)){
//				throw new GeneralException("今天的没有培训课或培训课已经结束！");
//			}else if("2".equals(regFlag)){
//				throw new GeneralException("今天的没有培训课或培训课还没开始！");
//			}
//		}
//		String reg_state = bo.getMsgBy(vo.getInt("late_for"), vo
//				.getInt("leave_early"));
		this.getFormHM().put("courseplan", courseplan);
		this.getFormHM().put("courseplanName", getCourName(courseplan));
		this.getFormHM().put("classplan", classplan);
//		this.getFormHM().put("nowDate", nowDate);
//		this.getFormHM().put("nowTime", nowTime);
		this.getFormHM().put("regFlag", regFlag);
//		this.getFormHM().put("reg_state", reg_state);

	}

	private String getCourName(String courCode) {
	    if(courCode != null && courCode.length() > 0)
	        courCode = PubFunc.decrypt(SafeCode.decode(courCode));
		String sql = "SELECT R1302 FROM R13 WHERE EXISTS (SELECT 1 FROM R41 WHERE  R4105=R1301 AND R4101='"
				+ courCode + "')";
		ContentDAO dao = new ContentDAO(this.frameconn);
		String R1302 = "";
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				R1302 = rs.getString("R1302");
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
		return R1302;
	}

}
