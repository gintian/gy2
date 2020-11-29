package com.hjsj.hrms.transaction.train.resource.trainroom;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TrainRoomAddTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		if (hm != null) {
			String fieldName = (String) hm.get("fieldName");
			hm.remove("fieldName");
			String day = (String) hm.get("day");
			day = day.length() == 1 ? "0" + day : day;
			hm.remove("day");

			Date _t1 = new Date();
			Date _t2 = DateUtils.getDate(DateUtils.FormatDate(new Date(),"yyyy-MM-dd")+ " 18:00", "yyyy-MM-dd HH:mm");
			if (_t1.getTime() > _t2.getTime()) {
				Date tmp = _t1;
				_t1 = _t2;
				_t2 = tmp;
			}
			this.getFormHM().put("startdate",DateUtils.FormatDate(_t1, "HH:mm"));
			this.getFormHM().put("enddate", DateUtils.FormatDate(_t2, "HH:mm"));
			this.getFormHM().put("strdate",getFormHM().get("year") + "-" + getFormHM().get("month")+ "-" + day);
			this.getFormHM().put("fieldName", SafeCode.decode(fieldName));
		} else {// save or del
			String state = (String) this.getFormHM().get("state");
			if ("save".equals(state))
				save();
			if ("del".equals(state))
				del();
		}
	}

	private void del() {
		String flag = "ok";
		String r1001 = (String) this.getFormHM().get("r1001");
		r1001 = PubFunc.decrypt(SafeCode.decode(r1001));
		String r6101 = (String) this.getFormHM().get("r6101");
		String r6103 = (String) this.getFormHM().get("r6103");
		String sql = "delete from r61 where r1001='" + r1001 + "' and nbase='"
				+ userView.getDbname() + "' and a0100='" + userView.getA0100()
				+ "' and r6101=" + Sql_switcher.dateValue(r6101)
				+ " and r6103=" + Sql_switcher.dateValue(r6103);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.delete(sql, new ArrayList());
		} catch (SQLException e) {
			flag = "error";
			this.getFormHM().put("flag", "error");
			e.printStackTrace();
		}
		this.getFormHM().put("flag", flag);
	}

	private void save() throws GeneralException {
		String flag = "ok";
		String fieldId = (String) this.getFormHM().get("fieldId");
		fieldId = PubFunc.decrypt(SafeCode.decode(fieldId));
		String strdate = (String) this.getFormHM().get("strdate");
		String begin_time = (String) this.getFormHM().get("begin_time");
		String end_time = (String) this.getFormHM().get("end_time");
		String declare = SafeCode.decode((String) this.getFormHM().get("declare"));
		
		if(begin_time.length()<5 || end_time.length()<5){
			this.getFormHM().put("flag", "输入时间格式错误！正确格式为：HH:MM");
			return;
		}
		//对开始时间截取、判断
		String h = begin_time.substring(0, 2);
		String m = begin_time.substring(3, 5);
		for(int i = 0;i<h.length();i++){
			if(!Character.isDigit(h.charAt(i))){
				this.getFormHM().put("flag", "开始时间输入有误，请重新输入。");
				return;
			}
		}
		for(int i = 0;i<m.length();i++){
			if(!Character.isDigit(m.charAt(i))){
				this.getFormHM().put("flag", "开始时间输入有误，请重新输入。");
				return;
			}
		}
		int h1 = Integer.parseInt(h);
		int m1 = Integer.parseInt(m);
		if (begin_time == null || begin_time.length() != 5 || h1 >= 24 || m1 >= 60) {
			this.getFormHM().put("flag", "开始时间输入有误，请重新输入。");
			return;
		}
		//对结束时间截取、判断
		String hh = end_time.substring(0, 2);
		String mm = end_time.substring(3, 5);
		for(int i = 0;i<hh.length();i++){
			if(!Character.isDigit(hh.charAt(i))){
				this.getFormHM().put("flag", "结束时间输入有误，请重新输入。");
				return;
			}
		}
		for(int i = 0;i<mm.length();i++){
			if(!Character.isDigit(mm.charAt(i))){
				this.getFormHM().put("flag", "结束时间输入有误，请重新输入。");
				return;
			}
		}
		int hh1 = Integer.parseInt(hh);
		int mm1 = Integer.parseInt(mm);
		if (end_time == null || end_time.length() != 5 || hh1 >= 24	|| mm1 >= 60) {
			this.getFormHM().put("flag", "结束时间输入有误，请重新输入。");
			return;
		}

		Date _t1 = DateUtils.getDate(strdate + " " + begin_time,
				"yyyy-MM-dd HH:mm");
		Date _t2 = DateUtils.getDate(strdate + " " + end_time,
				"yyyy-MM-dd HH:mm");
		if (_t1.getTime() >= _t2.getTime()) {
			this.getFormHM().put("flag", "开始时间不能大于结束时间。");
			return;
		}

		if (!checkTrainRoomInfo(fieldId, _t1, _t2)) {
			this.getFormHM().put("flag", "您申请的时间段已被占用，请重新选择时间段。");
			return;
		}

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("r61");
		vo.setString("r1001", fieldId);
		vo.setString("nbase", userView.getDbname());
		vo.setString("a0100", userView.getA0100());
		vo.setString("a0101", userView.getUserFullName());
		vo.setString("b0110", userView.getUserOrgId());
		vo.setString("e0122", userView.getUserDeptId());
		vo.setString("e01a1", userView.getUserPosId());
		vo.setDate("r6101", _t1);
		vo.setDate("r6103", _t2);
		vo.setString("r6105", declare);
		vo.setString("r6111", "02");
		dao.addValueObject(vo);

		this.getFormHM().put("flag", flag);
		StringBuffer value = new StringBuffer("&nbsp;<font color='blue'>");
		value.append(begin_time + "~" + end_time);
		value.append("</font>&nbsp;");
		value.append("(<a href='###' onclick=\"del('" + SafeCode.encode(PubFunc.encrypt(fieldId)) + "','"
				+ DateUtils.FormatDate(_t1, "yyyy-MM-dd HH:mm:ss") + "','"
				+ DateUtils.FormatDate(_t2, "yyyy-MM-dd HH:mm:ss")
				+ "');\" title='删除'>X</a>)&nbsp;<br/>");
		this.getFormHM().put("value", SafeCode.encode(value.toString()));
	}

	private boolean checkTrainRoomInfo(String fieldId, Date _t1, Date _t2) {
		boolean b = true;
		StringBuffer sql = new StringBuffer();
		sql.append("select r6101,r6103 from r61");
		sql.append(" where r1001='" + fieldId + "'");
		sql.append(" and " + Sql_switcher.year("r6103") + "='"
				+ DateUtils.getYear(_t2) + "'");
		sql.append(" and " + Sql_switcher.month("r6103") + "='"
				+ DateUtils.getMonth(_t2) + "'");
		sql.append(" and " + Sql_switcher.day("r6103") + "='"
				+ DateUtils.getDay(_t2) + "'");
		sql.append(" and ((nbase='" + userView.getDbname() + "'");
		sql.append(" and a0100='" + userView.getA0100() + "')");
		sql.append(" or r6111='03')");

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next()) {
				Date r6101 = this.frowset.getTimestamp("r6101");
				Date r6103 = this.frowset.getTimestamp("r6103");
				if ((_t1.getTime() >= r6101.getTime() && _t1.getTime() <= r6103
						.getTime())
						|| (_t2.getTime() >= r6101.getTime() && _t2.getTime() <= r6103
								.getTime())
						|| (_t1.getTime() < r6101.getTime() && _t2.getTime() > r6103
								.getTime())) {
					b = false;
				}
			}
		} catch (SQLException e) {
			return false;
		}

		return b;
	}
}