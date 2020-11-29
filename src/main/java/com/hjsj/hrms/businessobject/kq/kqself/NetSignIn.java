package com.hjsj.hrms.businessobject.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 网上签到
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Aug 1, 2007:10:37:47 AM
 * </p>
 * 
 * @author dengcan
 * @version 4.0
 */
public class NetSignIn {

	private Connection conn;

	private UserView userView;
	private String table_name = "kq_originality_data";
	private String class_id;
	private boolean is_sign = true;
	private String remess = "";

	public boolean isIs_sign() {
		return is_sign;
	}

	public void setIs_sign(boolean is_sign) {
		this.is_sign = is_sign;
	}

	public String getClass_id() {
		return class_id;
	}

	public void setClass_id(String class_id) {
		this.class_id = class_id;
	}

	public NetSignIn() {
	}

	public NetSignIn(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}

	/**
	 * 验证ip
	 * 
	 * @return
	 */
	public boolean validateIP(String nbase, String a0100, String netSignCheckIP,String signFlag)
			throws GeneralException {
		String errString = "";
		if ("1".equals(signFlag))
		{
			errString = ResourceFactory.getProperty("kq.netsign.error.ipnotequal.out");
		}else 
		{
			errString = ResourceFactory.getProperty("kq.netsign.error.ipnotequal.in");
		}
		boolean isCorrect = true;
		// 签到是否限制IP 0：不绑定 1：绑定（默认）2：有IP绑定，无IP不绑定 wangyao
		netSignCheckIP = netSignCheckIP != null && netSignCheckIP.length() > 0 ? netSignCheckIP : "1";
		
		//绑定IP
		if ("1".equals(netSignCheckIP)) {
			RecordVo ip_vo = ConstantParamter.getConstantVo("SS_BIND_IPADDR");
			if (ip_vo == null) {
				isCorrect = false;
				throw GeneralExceptionHandler.Handle(new GeneralException("", 
						ResourceFactory.getProperty("kq.netsign.error.notsetipfield"), "", ""));
			}
			
			String ip_addr_column = ip_vo.getString("str_value");
			if (ip_addr_column == null || ip_addr_column.length() <= 0 || "#".equals(ip_addr_column)) {
				isCorrect = false;
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						ResourceFactory.getProperty("kq.netsign.error.notsetipfield"), "", ""));
			}
			
			String loact_ip = this.userView.getRemote_ip();
			if (loact_ip == null || loact_ip.length() <= 0) {
				isCorrect = false;
				throw GeneralExceptionHandler.Handle(new GeneralException("", 
						ResourceFactory.getProperty("kq.netsign.error.notfoundip"), "", ""));
			}
			
			StringBuffer sql = new StringBuffer();
			sql.append("select 1 from " + nbase + "a01 ");
			sql.append(" where a0100='" + a0100 + "'");
			sql.append(" and " + ip_addr_column + "='" + loact_ip + "'");
			
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			try {
				rs = dao.search(sql.toString());
				if (!rs.next()) {
					isCorrect = false;
					throw GeneralExceptionHandler.Handle(new GeneralException("",errString, "", ""));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
			    KqUtilsClass.closeDBResource(rs);
			}
		} else if ("0".equals(netSignCheckIP)) {
			String loact_ip = this.userView.getRemote_ip();
			if (loact_ip == null || loact_ip.length() <= 0) {
				isCorrect = false;
				throw GeneralExceptionHandler.Handle(new GeneralException("", 
						ResourceFactory.getProperty("kq.netsign.error.notfoundip"), "", ""));
			}
		} else if ("2".equals(netSignCheckIP)) {
			RecordVo ip_vo = ConstantParamter.getConstantVo("SS_BIND_IPADDR");
			if (ip_vo == null) {
                return isCorrect;
            }
			
			String ip_addr_column = ip_vo.getString("str_value");
			if (ip_addr_column == null | ip_addr_column.length() <= 0 || "#".equals(ip_addr_column)) {
                return isCorrect;
            }
			
			String loact_ip = this.userView.getRemote_ip();
			if (loact_ip == null || loact_ip.length() <= 0) {
				isCorrect = false;
				throw GeneralExceptionHandler.Handle(new GeneralException("", 
						ResourceFactory.getProperty("kq.netsign.error.notfoundip"), "", ""));
			}
			
			StringBuffer sql = new StringBuffer();
			sql.append("select " + ip_addr_column + " as er from " + nbase + "a01 ");
			sql.append(" where a0100='" + a0100 + "'");
			ContentDAO dao = new ContentDAO(this.conn);
			String field = "";
			RowSet rs = null;
			try {
				rs = dao.search(sql.toString());
				if (rs.next()) {
					field = rs.getString("er");
				
					if (field != null && !"".equals(field) && !field.equalsIgnoreCase(loact_ip)) {
						isCorrect = false;
						throw GeneralExceptionHandler.Handle(new GeneralException("",errString, "", ""));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
			    KqUtilsClass.closeDBResource(rs);
			}
		}
		return isCorrect;
	}

	/**
	 * 得到卡号
	 * 
	 * @param nbase
	 * @param a0100
	 * @return
	 * @throws GeneralException
	 */
	public String getKqCard(String nbase, String a0100) throws GeneralException {
		HashMap map = new HashMap();
		KqParameter kq_paramter = new KqParameter(map, this.userView, "", this.conn);
		String kq_cardno = kq_paramter.getCardno();
		if (kq_cardno == null || kq_cardno.length() <= 0) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("kq.netsign.error.notsetcardfield"), "", ""));
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select " + kq_cardno + " from " + nbase + "a01 ");
		sql.append(" where a0100='" + a0100 + "'");
		ContentDAO dao = new ContentDAO(this.conn);
		String cardno = "";
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				cardno = rs.getString(kq_cardno);
			}
			
			if (cardno == null || cardno.length() <= 0) {
				throw new GeneralException("", ResourceFactory.getProperty("kq.netsign.error.notsetcard"), "", "");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return cardno;
	}

	/**
	 * 签到/签退
	 * 
	 * @param nbase
	 * @param a0100
	 * @param cardno
	 * @param work_date
	 * @param work_tiem
	 * @param location签到/签退
	 * @return
	 * @throws GeneralException
	 */
	public boolean onNetSign(String nbase, String a0100, String cardno,
			String oper_cause, Date oper_time, String work_date,
			String work_time, String location, String sp_flag, String ip_addr)
			throws GeneralException {
		boolean isCorrect = true;
		/*
		 * if(!ifNetSign(nbase,a0100,work_date,work_tiem)) {
		 * this.setRemess("不可以在请假时间范围内，签到签退！"); isCorrect=false; return
		 * isCorrect; }
		 */
		
		//如果记录已经存在就不用重复插入了
		KqDBHelper kqDB = new KqDBHelper(this.conn);
		String whr = "a0100='" + a0100 
		           + "' AND nbase='" + nbase 
		           + "' AND work_date='" + work_date 
		           + "' AND work_time='" + work_time + "'";
		if (kqDB.isRecordExist(this.table_name, whr)) {
            return isCorrect;
        }
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ");
		sql.append(this.table_name);
		sql.append("(a0100,nbase,card_no,work_date,work_time,a0101,b0110,e0122,e01a1,location");
		sql.append(",inout_flag,oper_cause,oper_user,oper_time,oper_mach,sp_flag,datafrom)");
		sql.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		ArrayList list = new ArrayList();
		list.add(a0100);
		list.add(nbase);
		list.add(cardno);
		list.add(work_date);
		list.add(work_time);
		list.add(userView.getUserFullName());
		list.add(userView.getUserOrgId());
		list.add(userView.getUserDeptId());
		list.add(userView.getUserPosId());
		list.add(location);
		list.add("0");
		list.add(oper_cause);
		list.add(this.userView.getUserFullName());
		if (oper_time == null) {
            list.add(null);
        } else {
            list.add(DateUtils.getTimestamp(DateUtils.format(oper_time,	"yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
        }
		list.add(ip_addr);
		list.add(sp_flag);
		list.add("1");
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			dao.insert(sql.toString(), list);
		} catch (Exception e) {
			isCorrect = false;
			e.printStackTrace();
		}
		return isCorrect;
	}

	/**
	 * 得到日期yyyy.MM.dd
	 * 
	 * @return
	 */
	public String getWork_date() {
		return PubFunc.getStringDate("yyyy.MM.dd");
	}

	/**
	 * 得到时间
	 * 
	 * @return HH:mm
	 */
	public String getWork_time() {
		return PubFunc.getStringDate("HH:mm");
	}

	/** ************************判断签到,签退范围*********************************** */
	/**
	 * 签到范围
	 * 
	 * @param nbase
	 * @param a0100
	 * @param work_date
	 * @param work_tiem
	 */
	public boolean signInScope(String nbase, String a0100, String b0110,
			String e0122, String e01a1, String work_date, String work_tiem,
			String singin_flag) throws GeneralException {
		boolean isCorrect = false;
		ArrayList classid_list = getClassID(nbase, a0100, b0110, e0122, e01a1,
				work_date);
		if (classid_list == null || classid_list.size() <= 0) {
			return isCorrect;
			// throw GeneralExceptionHandler.Handle(new
			// GeneralException("","没有匹配的班次,网签无效!","",""));
		}
		if (classid_list != null && classid_list.size() == 1) {
			if (classid_list.get(0) != null
					&& "0".equals(classid_list.get(0).toString())) {
                classid_list = restSigIn(work_date, work_tiem, nbase, a0100,
                        singin_flag);
            }
			/*
			 * if(classid_list==null||classid_list.size()<=0) { throw
			 * GeneralExceptionHandler.Handle(new
			 * GeneralException("","没有匹配的班次,网签无效!","","")); }
			 */
		}
		String class_id = "";
		if (classid_list == null || classid_list.size() <= 0) {
            return true;
        }
		for (int i = 0; i < classid_list.size(); i++) {
			class_id = classid_list.get(i).toString();
			if (class_id == null || class_id.length() <= 0
					|| "0".equals(class_id)) {
                continue;
            }
			if ("0".equals(singin_flag))// 签到
			{
				isCorrect = analyseOnDuty(class_id, work_date, work_tiem);
			} else if ("1".equals(singin_flag))// 签退
			{
				isCorrect = analyseOffDuty(class_id, work_date, work_tiem);
			}
			if (isCorrect) {
				this.setClass_id(class_id);
				break;
			}

		}
		return isCorrect;
	}

	/**
	 * 得到适合的班次
	 * 
	 * @param nbase
	 * @param a0100
	 * @param b0110
	 * @param e0122
	 * @param e01a1
	 * @param work_date
	 * @return
	 */
	public ArrayList getClassID(String nbase, String a0100, String b0110,
			String e0122, String e01a1, String work_date) {
		StringBuffer sql = new StringBuffer();
		sql.append("select class_id from kq_employ_shift ");
		sql.append("where nbase='" + nbase + "'");
		sql.append(" and a0100='" + a0100 + "'");
		sql.append(" and q03z0='" + work_date + "'");
		ContentDAO dao = new ContentDAO(this.conn);
		String class_id = "";
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				class_id = rs.getString("class_id");
				if (class_id != null && class_id.length() > 0) {
                    list.add(class_id);
                }
			} else {
				sql = new StringBuffer(); // 不定排班中的职位
				sql.append("select class_id from kq_org_dept_able_shift");
				sql.append(" where org_dept_id='" + e01a1 + "'");
				rs = dao.search(sql.toString());
				while (rs.next()) {
					class_id = rs.getString("class_id");
					if (class_id != null && class_id.length() > 0) {
                        list.add(class_id);
                    }
				}
				if (list == null || list.size() <= 0)// 不定排班中的部门
				{
					sql = new StringBuffer();
					sql.append("select class_id from kq_org_dept_able_shift");
					sql.append(" where org_dept_id='" + e0122 + "'");
					rs = dao.search(sql.toString());
					while (rs.next()) {
						class_id = rs.getString("class_id");
						if (class_id != null && class_id.length() > 0) {
                            list.add(class_id);
                        }
					}
				}
				if (list == null || list.size() <= 0)// 不定排班中的单位
				{
					sql = new StringBuffer();
					sql.append("select class_id from kq_org_dept_able_shift");
					sql.append(" where org_dept_id='" + b0110 + "'");
					rs = dao.search(sql.toString());
					while (rs.next()) {
						class_id = rs.getString("class_id");
						if (class_id != null && class_id.length() > 0) {
                            list.add(class_id);
                        }
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return list;
	}

	/**
	 * 分析签到
	 * 
	 * @param class_id
	 * @param work_date
	 * @param work_tiem
	 * @return
	 */
	private boolean analyseOnDuty(String class_id, String work_date,
			String work_tiem) {
		boolean isCorrect = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select " + kqClassShiftColumns());
		sql.append(" from kq_class where class_id='" + class_id + "'");
		ContentDAO dao = new ContentDAO(this.conn);
		Date dutyTiem = DateUtils.getDate(work_date + " " + work_tiem,
				"yyyy.MM.dd HH:mm");
		Date o_Tiem = DateUtils.getDate(work_tiem, "HH:mm");
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				KqUtilsClass kqUtilsClass = new KqUtilsClass();
				Date FTime;
				Date TTime;
				String strFTime = "";
				String strTTime = ""; // 迟到
				for (int i = 0; i < 4; i++) {
					String onduty_start = rs.getString("onduty_start_"
							+ (i + 1));// 上班刷卡时可
					String onduty = rs.getString("onduty_" + (i + 1));// 上班时止
					if ((onduty_start != null && onduty_start.length() > 0)) {
                        strFTime = onduty_start;
                    } else if (onduty != null && onduty.length() > 0) {
                        strFTime = onduty;
                    }
					if (strFTime == null || strFTime.length() <= 0) {
                        continue;
                    }
					String onduty_end = rs.getString("onduty_end_" + (i + 1));// 上班结束时刻
					String absent_work = rs.getString("absent_work_" + (i + 1));
					String be_late_for = rs.getString("be_late_for_" + (i + 1));// 迟到
					if (onduty_end != null && onduty_end.length() > 0) {
                        strTTime = onduty_end;
                    } else if (absent_work != null && absent_work.length() > 0) {
                        strTTime = absent_work;
                    } else if (be_late_for != null && be_late_for.length() > 0) {
                        strTTime = be_late_for;
                    } else if (onduty != null && onduty.length() > 0) {
                        strTTime = onduty;
                    }
					if (strTTime == null || strTTime.length() <= 0) {
                        continue;
                    }
					FTime = DateUtils.getDate(work_date + " " + strFTime,
							"yyyy.MM.dd HH:mm");
					TTime = DateUtils.getDate(work_date + " " + strTTime,
							"yyyy.MM.dd HH:mm");
					Date f_Time = DateUtils.getDate(strFTime, "HH:mm");
					Date t_Time = DateUtils.getDate(strTTime, "HH:mm");
					float time_l = kqUtilsClass.getPartMinute(FTime, TTime); // 大于0是当天，小于0是跨天
					if (time_l < 0) {
						Date zone_T0 = DateUtils.getDate("00:00", "HH:mm");
						Date zone_T2 = DateUtils.getDate("24:00", "HH:mm");
						float time_z24_1 = kqUtilsClass.getPartMinute(f_Time,
								o_Tiem);
						float time_z24_2 = kqUtilsClass.getPartMinute(o_Tiem,
								zone_T2);
						float time_z00_1 = kqUtilsClass.getPartMinute(zone_T0,
								o_Tiem);
						float time_z00_2 = kqUtilsClass.getPartMinute(o_Tiem,
								t_Time);
						if (time_z24_1 >= 0 && time_z24_2 > 0) {
							TTime = DateUtils.addDays(TTime, 1); // 得出第二天的日期
						} else if (time_z00_1 >= 0 && time_z00_2 >= 0) {
							FTime = DateUtils.addDays(FTime, -1);
						}
					}
					// 本月的第一天
					Calendar calendar1 = new GregorianCalendar();
					calendar1.set(Calendar.DATE, 1);
					SimpleDateFormat simpleFormate1 = new SimpleDateFormat(
							"yyyy.MM.dd");
					String ksr = simpleFormate1.format(calendar1.getTime());
					// ksr.trim();
					// 本月的最后一天
					Calendar calendar = new GregorianCalendar();
					calendar.set(Calendar.DATE, 1);
					calendar.roll(Calendar.DATE, -1);
					SimpleDateFormat simpleFormate = new SimpleDateFormat(
							"yyyy.MM.dd");
					String jsr = simpleFormate.format(calendar.getTime());
					// 如果跨天并且为第一天或者最后一天,应且签到是在00点左右的时候特出处理
					if (time_l < 0	&& (work_date.equals(ksr) || work_date.equals(jsr))) {
						isCorrect = true;
						break;
					} else {
						float f_time = kqUtilsClass.getPartMinute(FTime,
								dutyTiem);
						float t_time = kqUtilsClass.getPartMinute(dutyTiem,
								TTime);
						if (f_time >= 0 && t_time >= 0) {
							isCorrect = true;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return isCorrect;
	}

	/**
	 * 分析签退
	 * 
	 * @param class_id
	 * @param work_date
	 * @param work_tiem
	 * @return
	 */
	private boolean analyseOffDuty(String class_id, String work_date,
			String work_tiem) {
		boolean isCorrect = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select " + kqClassShiftColumns());
		sql.append(" from kq_class where class_id='" + class_id + "'");
		ContentDAO dao = new ContentDAO(this.conn);
		Date dutyTiem = DateUtils.getDate(work_date + " " + work_tiem,
				"yyyy.MM.dd HH:mm");
		Date o_Tiem = DateUtils.getDate(work_tiem, "HH:mm");
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				KqUtilsClass kqUtilsClass = new KqUtilsClass();
				Date FTime;
				Date TTime;
				String strFTime = "";
				String strTTime = "";
				for (int i = 0; i < 4; i++) {
					String offduty_start = rs.getString("offduty_start_"
							+ (i + 1));// 下班刷卡时可
					String leave_early_absent = rs
							.getString("leave_early_absent_" + (i + 1));// 下班矿工时可
					String leave_early = rs.getString("leave_early_" + (i + 1));// 下班早退
					String offduty = rs.getString("offduty_" + (i + 1));// 下班时刻
					if ((offduty_start != null && offduty_start.length() > 0)) {
                        strFTime = offduty_start;
                    } else if (leave_early_absent != null
							&& leave_early_absent.length() > 0) {
                        strFTime = leave_early_absent;
                    } else if (leave_early != null && leave_early.length() > 0) {
                        strFTime = leave_early;
                    } else if (offduty != null && offduty.length() > 0) {
                        strFTime = offduty;
                    }
					if (strFTime == null || strFTime.length() <= 0) {
                        continue;
                    }
					String offduty_end = rs.getString("offduty_end_" + (i + 1));// 下班结束时可
					if (offduty_end != null && offduty_end.length() > 0) {
                        strTTime = offduty_end;
                    } else if (offduty != null && offduty.length() > 0) {
                        strTTime = offduty;
                    }
					if (strTTime == null || strTTime.length() <= 0) {
                        continue;
                    }
					FTime = DateUtils.getDate(work_date + " " + strFTime,
							"yyyy.MM.dd HH:mm");
					TTime = DateUtils.getDate(work_date + " " + strTTime,
							"yyyy.MM.dd HH:mm");
					float time_l = kqUtilsClass.getPartMinute(FTime, TTime);
					if (time_l < 0) {
						Date f_Time = DateUtils.getDate(strFTime, "HH:mm");
						Date t_Time = DateUtils.getDate(strTTime, "HH:mm");
						Date zone_T0 = DateUtils.getDate("00:00", "HH:mm");
						Date zone_T2 = DateUtils.getDate("24:00", "HH:mm");
						float time_z24_1 = kqUtilsClass.getPartMinute(f_Time,
								o_Tiem);
						float time_z24_2 = kqUtilsClass.getPartMinute(o_Tiem,
								zone_T2);
						float time_z00_1 = kqUtilsClass.getPartMinute(zone_T0,
								o_Tiem);
						float time_z00_2 = kqUtilsClass.getPartMinute(o_Tiem,
								t_Time);
						if (time_z24_1 >= 0 && time_z24_2 > 0) {
							TTime = DateUtils.addDays(TTime, 1);
						} else if (time_z00_1 >= 0 && time_z00_2 >= 0) {
							FTime = DateUtils.addDays(FTime, -1);
						}
					}
					float f_time = kqUtilsClass.getPartMinute(FTime, dutyTiem);
					float t_time = kqUtilsClass.getPartMinute(dutyTiem, TTime);
					if (f_time >= 0 && t_time >= 0) {
						isCorrect = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			KqUtilsClass.closeDBResource(rs);
		}
		return isCorrect;
	}

	/** **********************计算迟到早退时间******************************* */
	public String signInCount(String class_id, String work_date,
			String work_tiem, String singin_flag) throws GeneralException {
		String mess = "";
		if (class_id == null || class_id.length() <= 0) {
            return "";
        }
		if ("0".equals(singin_flag))// 签到
		{
			mess = countOnDuty(class_id, work_date, work_tiem);
		} else if ("1".equals(singin_flag))// 签退
		{
			mess = countOffDuty(class_id, work_date, work_tiem);
		}
		return mess;
	}

	/**
	 * 签到分析迟到旷工分钟
	 * 
	 * @param class_id
	 * @param work_date
	 * @param work_tiem
	 * @return
	 */
	private String countOnDuty(String class_id, String work_date,
			String work_tiem) {
		StringBuffer sql = new StringBuffer();
		sql.append("select " + kqClassShiftColumns());
		sql.append(" from kq_class where class_id='" + class_id + "'");
		ContentDAO dao = new ContentDAO(this.conn);
		Date dutyTiem = DateUtils.getDate(work_date + " " + work_tiem, "yyyy.MM.dd HH:mm");
		Date o_Tiem = DateUtils.getDate(work_tiem, "HH:mm");
		StringBuffer mees = new StringBuffer();
		int time_i = 0;
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				KqUtilsClass kqUtilsClass = new KqUtilsClass();
				Date FTime;
				Date TTime;
				Date onTime;
				Date on_Flextime;
				String strFTime = "";
				String strTTime = "";
				for (int i = 0; i < 4; i++) {

					String onduty_end = rs.getString("onduty_end_" + (i + 1));// 上班结束时刻
					String onduty_flextime = rs.getString("onduty_flextime_"
							+ (i + 1));// 上班弹性时间
					String absent_work = rs.getString("absent_work_" + (i + 1));
					String be_late_for = rs.getString("be_late_for_" + (i + 1));// 迟到
					String onduty = rs.getString("onduty_" + (i + 1));// 上班时可
					on_Flextime = null;
					if (absent_work != null && absent_work.length() > 0
							&& onduty_end != null && onduty_end.length() > 0
							&& onduty != null && onduty.length() > 0) {
						strFTime = absent_work;
						strTTime = onduty_end;
						FTime = DateUtils.getDate(work_date + " " + strFTime,
								"yyyy.MM.dd HH:mm");
						TTime = DateUtils.getDate(work_date + " " + strTTime,
								"yyyy.MM.dd HH:mm");
						onTime = DateUtils.getDate(work_date + " " + onduty,
								"yyyy.MM.dd HH:mm");
						if (onduty_flextime != null
								&& onduty_flextime.length() == 5) {
							on_Flextime = DateUtils.getDate(work_date + " "
									+ onduty_flextime, "yyyy.MM.dd HH:mm");
						}
						float time_l = kqUtilsClass.getPartMinute(FTime, TTime);
						if (time_l < 0) {
							Date f_Time = DateUtils.getDate(strFTime, "HH:mm");
							Date t_Time = DateUtils.getDate(strTTime, "HH:mm");
							Date zone_T0 = DateUtils.getDate("00:00", "HH:mm");
							Date zone_T2 = DateUtils.getDate("24:00", "HH:mm");
							float time_z24_1 = kqUtilsClass.getPartMinute(
									f_Time, o_Tiem);
							float time_z24_2 = kqUtilsClass.getPartMinute(
									o_Tiem, zone_T2);
							float time_z00_1 = kqUtilsClass.getPartMinute(
									zone_T0, o_Tiem);
							float time_z00_2 = kqUtilsClass.getPartMinute(
									o_Tiem, t_Time);
							if (time_z24_1 >= 0 && time_z24_2 > 0) {
								TTime = DateUtils.addDays(TTime, 1);
							} else if (time_z00_1 >= 0 && time_z00_2 >= 0) {
								FTime = DateUtils.addDays(FTime, -1);
							}
						}
						float f_time = kqUtilsClass.getPartMinute(FTime,
								dutyTiem);
						float t_time = kqUtilsClass.getPartMinute(dutyTiem,
								TTime);
						if (f_time > 0 && t_time >= 0) {
							float time_f = 0;
							if (on_Flextime != null)// 有弹性班次的
							{
								time_f = kqUtilsClass.getPartMinute(
										on_Flextime, dutyTiem);
								if (time_f < 0) {
									Date zone_T = DateUtils.getDate(work_date
											+ " 24:00", "yyyy.MM.dd HH:mm");
									time_f = kqUtilsClass.getPartMinute(
											on_Flextime, zone_T);
									zone_T = DateUtils.getDate(work_date
											+ " 00:00", "yyyy.MM.dd HH:mm");
									time_f = kqUtilsClass.getPartMinute(zone_T,
											dutyTiem)
											+ time_f;
								}
							} else {
								time_f = kqUtilsClass.getPartMinute(onTime,
										dutyTiem);
								if (time_f < 0) {
									Date zone_T = DateUtils.getDate(work_date
											+ " 24:00", "yyyy.MM.dd HH:mm");
									time_f = kqUtilsClass.getPartMinute(onTime,
											zone_T);
									zone_T = DateUtils.getDate(work_date
											+ " 00:00", "yyyy.MM.dd HH:mm");
									time_f = kqUtilsClass.getPartMinute(zone_T,
											dutyTiem)
											+ time_f;
								}
							}
							time_i = Math.round(time_f);
							time_i = Math.abs(time_i);
							mees.append("旷工" + time_i + "分钟");
							this.is_sign = false;
							break;
						}
					}
					if (absent_work != null && absent_work.length() > 0
							&& be_late_for != null && be_late_for.length() > 0
							&& onduty != null && onduty.length() > 0) {
						strFTime = be_late_for;
						strTTime = absent_work;
						FTime = DateUtils.getDate(work_date + " " + strFTime,
								"yyyy.MM.dd HH:mm");
						TTime = DateUtils.getDate(work_date + " " + strTTime,
								"yyyy.MM.dd HH:mm");
						onTime = DateUtils.getDate(work_date + " " + onduty,
								"yyyy.MM.dd HH:mm");
						if (onduty_flextime != null
								&& onduty_flextime.length() == 5) {
							on_Flextime = DateUtils.getDate(work_date + " "
									+ onduty_flextime, "yyyy.MM.dd HH:mm");
						}
						float time_l = kqUtilsClass.getPartMinute(FTime, TTime);
						if (time_l < 0) {
							Date f_Time = DateUtils.getDate(strFTime, "HH:mm");
							Date t_Time = DateUtils.getDate(strTTime, "HH:mm");
							Date zone_T0 = DateUtils.getDate("00:00", "HH:mm");
							Date zone_T2 = DateUtils.getDate("24:00", "HH:mm");
							float time_z24_1 = kqUtilsClass.getPartMinute(
									f_Time, o_Tiem);
							float time_z24_2 = kqUtilsClass.getPartMinute(
									o_Tiem, zone_T2);
							float time_z00_1 = kqUtilsClass.getPartMinute(
									zone_T0, o_Tiem);
							float time_z00_2 = kqUtilsClass.getPartMinute(
									o_Tiem, t_Time);
							if (time_z24_1 >= 0 && time_z24_2 > 0) {
								TTime = DateUtils.addDays(TTime, 1);
							} else if (time_z00_1 >= 0 && time_z00_2 >= 0) {
								FTime = DateUtils.addDays(FTime, -1);
							}
						}
						float f_time = kqUtilsClass.getPartMinute(FTime,
								dutyTiem);
						float t_time = kqUtilsClass.getPartMinute(dutyTiem,
								TTime);
						if (f_time > 0 && t_time >= 0) {
							float time_f = 0;
							if (on_Flextime != null)// 有弹性班次的
							{
								time_f = kqUtilsClass.getPartMinute(
										on_Flextime, dutyTiem);
								if (time_f < 0) {
									Date zone_T = DateUtils.getDate(work_date
											+ " 24:00", "yyyy.MM.dd HH:mm");
									time_f = kqUtilsClass.getPartMinute(
											on_Flextime, zone_T);
									zone_T = DateUtils.getDate(work_date
											+ " 00:00", "yyyy.MM.dd HH:mm");
									time_f = kqUtilsClass.getPartMinute(zone_T,
											dutyTiem)
											+ time_f;
								}
							} else {
								time_f = kqUtilsClass.getPartMinute(onTime,
										dutyTiem);
								if (time_f < 0) {
									Date zone_T = DateUtils.getDate(work_date
											+ " 24:00", "yyyy.MM.dd HH:mm");
									time_f = kqUtilsClass.getPartMinute(onTime,
											zone_T);
									zone_T = DateUtils.getDate(work_date
											+ " 00:00", "yyyy.MM.dd HH:mm");
									time_f = kqUtilsClass.getPartMinute(zone_T,
											dutyTiem)
											+ time_f;
								}
							}
							time_i = Math.round(time_f);
							time_i = Math.abs(time_i);
							this.is_sign = false;
							mees.append("迟到" + time_i + "分钟");
							break;
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return mees.toString();
	}

	/**
	 * 分析签退旷工早退
	 * 
	 * @param class_id
	 * @param work_date
	 * @param work_tiem
	 * @return
	 */
	private String countOffDuty(String class_id, String work_date,
			String work_tiem) {

		StringBuffer sql = new StringBuffer();
		sql.append("select " + kqClassShiftColumns());
		sql.append(" from kq_class where class_id='" + class_id + "'");
		ContentDAO dao = new ContentDAO(this.conn);
		Date dutyTiem = DateUtils.getDate(work_date + " " + work_tiem,
				"yyyy.MM.dd HH:mm");
		Date o_Tiem = DateUtils.getDate(work_tiem, "HH:mm");
		StringBuffer mees = new StringBuffer();
		int time_i = 0;
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				KqUtilsClass kqUtilsClass = new KqUtilsClass();
				Date FTime;
				Date TTime;
				String strFTime = "";
				String strTTime = "";
				Date off_Time;
				for (int i = 4; i > 1; i--) {
					String offduty_start = rs.getString("offduty_start_"
							+ (i - 1));// 下班刷卡时可
					String leave_early_absent = rs
							.getString("leave_early_absent_" + (i - 1));// 下班矿工时可
					String leave_early = rs.getString("leave_early_" + (i - 1));// 下班早退
					String offduty = rs.getString("offduty_" + (i - 1));// 下班时刻
					if (offduty_start != null && offduty_start.length() > 0
							&& leave_early_absent != null
							&& leave_early_absent.length() > 0
							&& offduty != null && offduty.length() > 0) {
						strFTime = offduty_start;
						strTTime = leave_early_absent;
						FTime = DateUtils.getDate(work_date + " " + strFTime,
								"yyyy.MM.dd HH:mm");
						TTime = DateUtils.getDate(work_date + " " + strTTime,
								"yyyy.MM.dd HH:mm");
						off_Time = DateUtils.getDate(work_date + " " + offduty,
								"yyyy.MM.dd HH:mm");
						float time_l = kqUtilsClass.getPartMinute(FTime, TTime);
						if (time_l < 0) {
							Date f_Time = DateUtils.getDate(strFTime, "HH:mm");
							Date t_Time = DateUtils.getDate(strTTime, "HH:mm");
							Date zone_T0 = DateUtils.getDate("00:00", "HH:mm");
							Date zone_T2 = DateUtils.getDate("24:00", "HH:mm");
							float time_z24_1 = kqUtilsClass.getPartMinute(
									f_Time, o_Tiem);
							float time_z24_2 = kqUtilsClass.getPartMinute(
									o_Tiem, zone_T2);
							float time_z00_1 = kqUtilsClass.getPartMinute(
									zone_T0, o_Tiem);
							float time_z00_2 = kqUtilsClass.getPartMinute(
									o_Tiem, t_Time);
							if (time_z24_1 >= 0 && time_z24_2 > 0) {
								TTime = DateUtils.addDays(TTime, 1);
							} else if (time_z00_1 >= 0 && time_z00_2 >= 0) {
								FTime = DateUtils.addDays(FTime, -1);
							}
						}
						float f_time = kqUtilsClass.getPartMinute(FTime,
								dutyTiem);
						float t_time = kqUtilsClass.getPartMinute(dutyTiem,
								TTime);
						if (f_time >= 0 && t_time > 0) {
							float time_f = kqUtilsClass.getPartMinute(dutyTiem,
									off_Time);
							if (time_f < 0) {
								Date zone_T = DateUtils.getDate(work_date
										+ " 24:00", "yyyy.MM.dd HH:mm");
								time_f = kqUtilsClass.getPartMinute(off_Time,
										zone_T);
								zone_T = DateUtils.getDate(
										work_date + " 00:00",
										"yyyy.MM.dd HH:mm");
								time_f = kqUtilsClass.getPartMinute(zone_T,
										dutyTiem)
										+ time_f;
							}
							time_i = Math.round(time_f);
							time_i = Math.abs(time_i);
							mees.append("旷工" + time_i + "分钟");
							this.is_sign = false;
							break;
						}
					}
					if (leave_early != null && leave_early.length() > 0
							&& leave_early_absent != null
							&& leave_early_absent.length() > 0
							&& offduty != null && offduty.length() > 0) {
						strFTime = leave_early_absent;
						strTTime = leave_early;
						FTime = DateUtils.getDate(work_date + " " + strFTime,
								"yyyy.MM.dd HH:mm");
						TTime = DateUtils.getDate(work_date + " " + strTTime,
								"yyyy.MM.dd HH:mm");
						off_Time = DateUtils.getDate(work_date + " " + offduty,
								"yyyy.MM.dd HH:mm");
						float time_l = kqUtilsClass.getPartMinute(FTime, TTime);
						if (time_l < 0) {
							Date f_Time = DateUtils.getDate(strFTime, "HH:mm");
							Date t_Time = DateUtils.getDate(strTTime, "HH:mm");
							Date zone_T0 = DateUtils.getDate("00:00", "HH:mm");
							Date zone_T2 = DateUtils.getDate("24:00", "HH:mm");
							float time_z24_1 = kqUtilsClass.getPartMinute(
									f_Time, o_Tiem);
							float time_z24_2 = kqUtilsClass.getPartMinute(
									o_Tiem, zone_T2);
							float time_z00_1 = kqUtilsClass.getPartMinute(
									zone_T0, o_Tiem);
							float time_z00_2 = kqUtilsClass.getPartMinute(
									o_Tiem, t_Time);
							if (time_z24_1 >= 0 && time_z24_2 > 0) {
								TTime = DateUtils.addDays(TTime, 1);
							} else if (time_z00_1 >= 0 && time_z00_2 >= 0) {
								FTime = DateUtils.addDays(FTime, -1);
							}
						}
						float f_time = kqUtilsClass.getPartMinute(FTime,
								dutyTiem);
						float t_time = kqUtilsClass.getPartMinute(dutyTiem,
								TTime);
						if (f_time >= 0 && t_time > 0) {
							float time_f = kqUtilsClass.getPartMinute(dutyTiem,
									off_Time);
							if (time_f < 0) {
								Date zone_T = DateUtils.getDate(work_date
										+ " 24:00", "yyyy.MM.dd HH:mm");
								time_f = kqUtilsClass.getPartMinute(off_Time,
										zone_T);
								zone_T = DateUtils.getDate(
										work_date + " 00:00",
										"yyyy.MM.dd HH:mm");
								time_f = kqUtilsClass.getPartMinute(zone_T,
										dutyTiem)
										+ time_f;
							}
							time_i = Math.round(time_f);
							time_i = Math.abs(time_i);
							mees.append("早退" + time_i + "分钟");
							this.is_sign = false;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return mees.toString();
	}

	/** ***************规定时间间隔内不能签多次*************** */
	/**
	 * 规定时间间隔内不能签多次
	 */
	public boolean IsExists(String nbase, String a0100, String work_date,
			String work_tiem) {
		boolean isCorrect = true;
		
		String[] ds = work_date.split("\\.");
		int cy = 0, cm = 0, cd = 0;
		if (ds.length == 3) {
			cy = Integer.parseInt(ds[0]);
			cm = Integer.parseInt(ds[1]) - 1;
			cd = Integer.parseInt(ds[2]);
		} else {
			return isCorrect;
		}
		
		String[] ts = work_tiem.split(":");
		int ch = 0, cmi = 0;
		if (ts.length == 2) {
			ch = Integer.parseInt(ts[0]);
			cmi = Integer.parseInt(ts[1]);
		} else {
			return isCorrect;
		}
		
		Calendar c = Calendar.getInstance();
		c.set(cy, cm, cd, ch, cmi);
		Date d2 = c.getTime();// 欲刷卡时间
		
		KqParam kqParam = KqParam.getInstance();		
		int amount = kqParam.getCard_interval();// 重复刷卡间隔X分钟
		
		StringBuffer sql = new StringBuffer();
		sql.append("select work_time from kq_originality_data where work_date='");
		sql.append(work_date);
		sql.append("' and nbase='");
		sql.append(nbase);
		sql.append("' and a0100='");
		sql.append(a0100 + "'");

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			
			while (rs.next()) {
				String time = rs.getString("work_time");
				ts = time.split(":");
				int h = 0, mi = 0;
				if (ts.length == 2) {
					h = Integer.parseInt(ts[0]);
					mi = Integer.parseInt(ts[1]);
				} else {
					return isCorrect;
				}				
				
				c.set(cy, cm, cd, h, mi);
				
				c.add(Calendar.MINUTE, amount);
				Date d1 = c.getTime();// 时间跨度止
				c.add(Calendar.MINUTE, -amount*2);
				Date d3 = c.getTime();// 时间跨度起				
				
				if (d2.compareTo(d1) <= 0 && d2.compareTo(d3) >= 0) {
					isCorrect = false;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			KqUtilsClass.closeDBResource(rs);
		}
		return isCorrect;
	}

	public String kqClassShiftColumns() {
		StringBuffer columns = new StringBuffer();
		columns.append("class_id,name,onduty_card_1,offduty_card_1,onduty_card_2,offduty_card_2,");
		columns.append("onduty_card_3,offduty_card_3,onduty_card_4,offduty_card_4,");
		columns.append("onduty_start_1,onduty_1,onduty_flextime_1,be_late_for_1,absent_work_1,onduty_end_1,");
		columns.append("rest_start_1,rest_end_1,offduty_start_1,leave_early_absent_1,leave_early_1,");
		columns.append("offduty_1,offduty_flextime_1,offduty_end_1,");
		// 2
		columns.append("onduty_start_2,onduty_2,onduty_flextime_2,be_late_for_2,absent_work_2,onduty_end_2,");
		columns.append("rest_start_2,rest_end_2,offduty_start_2,leave_early_absent_2,leave_early_2,");
		columns.append("offduty_2,offduty_flextime_2,offduty_end_2,");
		// 3
		columns.append("onduty_start_3,onduty_3,onduty_flextime_3,be_late_for_3,absent_work_3,onduty_end_3,");
		columns.append("rest_start_3,rest_end_3,offduty_start_3,leave_early_absent_3,leave_early_3,");
		columns.append("offduty_3,offduty_flextime_3,offduty_end_3,");
		// 4
		columns.append("onduty_start_4,onduty_4,onduty_flextime_4,be_late_for_4,absent_work_4,onduty_end_4,");
		columns.append("rest_start_4,rest_end_4,offduty_start_4,leave_early_absent_4,leave_early_4,");
		columns.append("offduty_4,offduty_flextime_4,offduty_end_4,");
		// other
		columns.append("night_shift_start,night_shift_end,zeroflag,domain_count,work_hours,zero_absent,one_absent");
		return columns.toString();
	}

	/**
	 * 
	 * @param work_date
	 * @param work_tiem
	 * @param singin_flag
	 * @return
	 */
	private ArrayList restSigIn(String work_date, String work_tiem,
			String nbase, String a0100, String singin_flag)
			throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select q1104 from q11 where a0100='" + a0100
				+ "' and nbase='" + nbase + "' and "
				+ Sql_switcher.isnull("q1104", "''") + "<>''");
		if ("0".equals(singin_flag)) {
			// sql.append(" and "+Sql_switcher.dateToChar("q11z1",
			// "YYYY-MM-DD")+"="+Sql_switcher.dateValue(work_date));
			sql.append(" and " + Sql_switcher.dateToChar("q11z1", "YYYY.MM.DD")
					+ "='" + work_date + "'");
		} else if ("1".equals(singin_flag)) {
			// sql.append(" and "+Sql_switcher.dateToChar("q11z3",
			// "YYYY-MM-DD")+"="+Sql_switcher.dateValue(work_date));
			sql.append(" and " + Sql_switcher.dateToChar("q11z3", "YYYY.MM.DD")
					+ "='" + work_date + "'");
		}
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			while (rs.next()) {
				list.add(rs.getString("q1104"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return list;
	}	

	public String getOffduty(RowSet rs) throws Exception {
		if (rs.getString("offduty_3") != null && rs.getString("offduty_3").length() > 0) {
			return rs.getString("offduty_3");
		} else if (rs.getString("offduty_2") != null && rs.getString("offduty_2").length() > 0) {
			return rs.getString("offduty_2");
		} else if (rs.getString("offduty_1") != null && rs.getString("offduty_1").length() > 0) {
			return rs.getString("offduty_1");
		} else {
			return "";
		}
	}

	/**
	 * 是否刷卡
	 * 
	 * @param nbase
	 * @param a0100
	 * @param work_date
	 * @param work_time
	 * @return
	 */
	public boolean ifNetSign(String nbase, String a0100, String work_date,
			String work_time) {
		boolean isCorrect = canNetSign(nbase, a0100, work_date, work_time, "q15");
		if (!isCorrect) {
            this.setRemess("不可以在请假时间范围内，签到签退！");
        }
		
		return isCorrect;
	}

	/**
	 * 登陆系统是否刷卡
	 * 
	 * @param nbase
	 * @param a0100
	 * @param work_date
	 * @param work_time
	 * @param table
	 * @return
	 */
	public boolean ifNetSign_logon(String nbase, String a0100, String work_date,
			String work_time,String table) {
		return canNetSign(nbase, a0100, work_date, work_time, table);
	}
	
	private boolean canNetSign(String nbase, String a0100, String work_date,
            String work_time,String table) {
	    boolean isCorrect = true;
	   
        StringBuffer sql = new StringBuffer();
        table = table.toLowerCase();
        sql.append("select * from " + table);
        sql.append(" where nbase='" + nbase + "' and a0100='" + a0100 + "'");
        if("q15".equals(table)) {
            sql.append(" and q1517<>1 ");
        }
        sql.append(" and " + table + "z5='03' and " + table + "z0='01' ");
        sql.append(" and ");
        sql.append(Sql_switcher.dateValue(work_date + " " + work_time));
        sql.append(" between " + table + "z1 and " + table + "z3");
        
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sql.toString());
            String q1519 = "";
            if (rs.next()) {
                isCorrect = false;
                
                // 请假需判断休假情况 ，在休假内允许签到、签退、补刷
                if ("q15".equalsIgnoreCase(table)) {
                    q1519 = rs.getString( table + "01");
                    if (checkXiaojia(q1519, nbase, a0100, work_date, work_time)) {
                        isCorrect = true;
                        return isCorrect;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect;
	}

	/**
	 * 
	 * @param q1519
	 * @return
	 */
	private boolean checkXiaojia(String q1519, String nbase, String a0100,
			String work_date, String work_tiem) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from q15 where q1519='" + q1519
				+ "' and q1517='1'");
		sql.append(" and nbase='" + nbase + "' and a0100='" + a0100
				+ "' and q15z5='03' and q15z0='01' ");
		sql.append(" and "
				+ Sql_switcher.dateValue(work_date + " " + work_tiem)
				+ " between q15z1 and q15z3");
		ContentDAO dao = new ContentDAO(this.conn);
		boolean isCorrect = false;
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				isCorrect = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return isCorrect;
	}

	public String getRemess() {
		return remess;
	}

	public void setRemess(String remess) {
		this.remess = remess;
	}

}
