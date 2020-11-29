package com.hjsj.hrms.businessobject.train.attendance;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>
 * Title:TrainAddBo.java
 * </p>
 * <p>
 * Description:培训考勤
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-03-03 14:40:00
 * </p>
 * 
 * @author LiWeichao
 * @version 5.0
 */
public class TrainAtteBo {

	private boolean into = false;
	private UserView userView;
	public TrainAtteBo() {

	}

	/**
	 * 根据管理范围获取培训班
	 * 
	 * @param conn
	 * @param a_code
	 * @return
	 */
	public ArrayList getTrainClass(Connection conn, String a_code) {
		StringBuffer sqlwhere = new StringBuffer();
		ArrayList classplanlist = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		ResultSet rs = null;
		try {
			sqlwhere.append("select r3101,r3130 from r31 where r3127='04' AND r3130 IS NOT NULL ");
			if(Sql_switcher.searchDbServer() == Constant.MSSQL) {
                sqlwhere.append(" AND r3130 <> ''");
            }
			
			if (a_code != null && a_code.trim().length() > 2) {
				String tmp[] = a_code.split("`");
				sqlwhere.append(" and (");
				for (int i = 0; i < tmp.length; i++) {
					String t = tmp[i];
					if ("UN".equalsIgnoreCase(t.substring(0, 2))) {
                        sqlwhere.append("B0110 like '" + t.substring(2, t.length()) + "%' or ");
                    }
					if ("UM".equalsIgnoreCase(t.substring(0, 2))) {
                        sqlwhere.append("E0122 like '" + t.substring(2, t.length()) + "%' or ");
                    }
				}
				sqlwhere.setLength(sqlwhere.length()-4);
				sqlwhere.append(")");
			}
			sqlwhere.append(" and (select count(1) from r41 where r4103=r3101)>0");//培训课程为空的不显示
			sqlwhere.append(" order by r3101 desc");
			rs = dao.search(sqlwhere.toString());
			while (rs.next()) {
				CommonData cd = new CommonData();
				cd.setDataName(rs.getString("r3130").replaceAll("%26lt;","<").replaceAll("%26gt;",">").replaceAll("%26quot;", "\"").replaceAll("%26amp;", "&"));
				cd.setDataValue(SafeCode.encode(PubFunc.encrypt(rs.getString("r3101"))));
				classplanlist.add(cd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
                    rs.close();
                }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return classplanlist;
	}

	/**
	 * 求某时间段中的时间集合
	 * 
	 * @param dateFirst
	 *            开始时间
	 * @param dateSecond
	 *            结束时间
	 * @return 该时间段中的每一天list
	 */
	public ArrayList displayEveryDate(String dateFirst, String dateSecond) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		ArrayList cal_date = new ArrayList();
		try {
			Date dateOne = dateFormat.parse(dateFirst);
			Date dateTwo = dateFormat.parse(dateSecond);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateOne);
			while (calendar.getTime().before(dateTwo)) {
				cal_date.add(dateFormat.format(calendar.getTime()));
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			cal_date.add(dateSecond);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cal_date;
	}

	/**
	 * 比较两个时间的大小
	 * 
	 * @param dateFirst
	 *            开始时间
	 * @param dateSecond
	 *            结束时间
	 * @return boolean dateSecond>=dateFirst =true:false
	 */
	public boolean verdictDate(String dateFirst, String dateSecond) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		boolean flag = true;
		try {
			Date date1 = (Date) format.parseObject(dateFirst);
			Date date2 = (Date) format.parseObject(dateSecond);
			flag = date2.after(date1);
			if (dateFirst.equals(dateSecond)) {
                flag = true;
            }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 根据r4101获取 课程开始时间、课程结束时间、课时
	 * 
	 * @param conn
	 * @param r4101
	 *            培训课程ID
	 * @return String[] 0=课程开始时间、1=课程结束时间、2=课时
	 */
	public String[] getR41Info(Connection conn, String r4101) {
		String[] str = new String[3];
		ResultSet rs = null;
		try {
			String sql = "select r4108,r4110,r4112 from r41 where r4101='"
					+ r4101 + "'";
			ContentDAO dao = new ContentDAO(conn);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			rs = dao.search(sql);
			if (rs.next()) {
				Date date = rs.getDate("r4108");
				if(date!=null) {
                    str[0] = sdf.format(date);
                }
				date = rs.getDate("r4110");
				if(date!=null) {
                    str[1] = sdf.format(date);
                }
				str[2] = String.valueOf(rs.getDouble("r4112"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
                    rs.close();
                }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	/**
	 * 根据迟到分钟和早退分钟得到状态信息
	 * 
	 * @param late
	 * @param leave
	 * @return
	 */
	public String getMsgBy(int late, int leave) {
		String state = "";
		if (leave > 0) {
			state = "2";
		} else if (late > 0) {
			state = "1";
		} else {
			state = "0";
		}
		return state;
	}

	public boolean isNumeric(String pramt) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(pramt).matches();
	}

	/**
	 * 
	 * @param regFlag
	 *            签到类别（1：签到/2：签退/3：补签到/4：补签退）
	 * @param card_num
	 *            考勤卡号
	 * @param nowDate
	 *            签到时间
	 * @param courseplan
	 *            签到课程
	 * @param conn
	 *            数据连接
	 * @return RecordVo(tr_cardtime)
	 * @throws GeneralException
	 */
	public RecordVo getRegStateVo(String regFlag, String card_num,
			Date nowDate, String courseplan, Connection conn,String classplan)
			throws GeneralException {
		String nowYear = OperateDate.dateToStr(nowDate, "yyyy-MM-dd");// 签到 年
		String nowTime = OperateDate.dateToStr(nowDate, "HH:mm");// 签到 时间
		ConstantXml constantbo = new ConstantXml(conn, "TR_PARAM");
		StringBuffer class_sql = new StringBuffer();
		RecordVo vo = new RecordVo("tr_cardtime");
		vo.setDate("card_time", OperateDate.getDateByFormat(nowDate,
				"yyyy-MM-dd HH:mm:ss"));
		vo.setDate("oper_time", OperateDate.getDateByFormat(nowDate,
				"yyyy-MM-dd HH:mm:ss"));
		vo.setInt("card_type", Integer.parseInt(regFlag));// --------类别（1：签到/2：签退）
		vo.setString("r4101", courseplan);
		if ("1".equals(regFlag) || "3".equals(regFlag)) {// 签到
			class_sql.append("SELECT train_date,begin_time,end_time FROM tr_classplan WHERE (");
			class_sql.append(Sql_switcher.dateToChar("train_date", "yyyy-MM-dd") + " ");
			class_sql.append(Sql_switcher.concat());
			class_sql.append(" ' ' ");
			class_sql.append(Sql_switcher.concat());
			class_sql.append(" end_time) BETWEEN '" + nowYear + " " + nowTime
					+ "' AND '"+nowYear+" 23:59:59' AND ");
			class_sql.append(Sql_switcher.isnull("begin_card", "0") + "=1");
			class_sql.append(" AND R4101='" + courseplan + "'");
			class_sql.append(" ORDER BY train_date,begin_time");
		} else if ("2".equals(regFlag) || "4".equals(regFlag)) {// 签退
			class_sql
					.append("SELECT train_date,begin_time,end_time FROM tr_classplan WHERE (");
			class_sql.append(Sql_switcher
					.dateToChar("train_date", "yyyy-MM-dd")
					+ " ");
			class_sql.append(Sql_switcher.concat());
			class_sql.append(" ' ' ");
			class_sql.append(Sql_switcher.concat());
			class_sql.append(" begin_time) BETWEEN '"+nowYear+" 00:00' AND '" + nowYear + " " + nowTime
					+ "' AND ");
			class_sql.append(Sql_switcher.isnull("end_card", "0") + "=1");
			class_sql.append(" AND R4101='" + courseplan + "'");
			class_sql.append(" ORDER BY train_date desc,begin_time desc");
		}

		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		RowSet usRow = null;
		try {
			rs = dao.search(class_sql.toString());
			if (rs.next()) {
				Date train_date = rs.getDate("train_date");
				String card_date = OperateDate.dateToStr(train_date, "yyyy-MM-dd");
				String begin_time = rs.getString("begin_time");
				String end_time = rs.getString("end_time");
				Date begin_date = OperateDate.strToDate(card_date + " "
						+ begin_time, "yyyy-MM-dd HH:mm");
				Date end_date = OperateDate.strToDate(card_date + " " + end_time,
						"yyyy-MM-dd HH:mm");
				Date now_date = OperateDate.strToDate(nowYear + " " + nowTime,
						"yyyy-MM-dd HH:mm");
				if ("1".equals(regFlag) || "3".equals(regFlag)) {// 签到
					if (begin_date.before(now_date)) {
						long l_late = now_date.getTime() - begin_date.getTime();
						int late_minutes = (int) (l_late / (1000 * 60));
						String leave_early = constantbo
								.getTextValue("/param/attendance/leave_early");
						if (leave_early == null || leave_early.length() < 1) {
							leave_early = "0";
						}
						if (isNumeric(leave_early)) {
							int iLeave_early = Integer.parseInt(leave_early);
							if (iLeave_early < late_minutes) {
								vo.setInt("late_for", late_minutes);
							}
						}
					}
				} else if ("2".equals(regFlag) || "4".equals(regFlag)) {// 签退
					if (end_date.after(now_date)) {
						String late_for = constantbo
								.getTextValue("/param/attendance/late_for");
						long l_late = end_date.getTime() - now_date.getTime();
						int early_minutes = (int) (l_late / (1000 * 60));
						if (late_for == null || late_for.length() < 1) {
							late_for = "0";
						}
						if (isNumeric(late_for)) {
							int iLate_for = Integer.parseInt(late_for);
							if (iLate_for < early_minutes) {
								vo.setInt("leave_early", early_minutes);
							}
						}
					}
				}
				ArrayList list = getUserIDByCardNum(card_num, conn, classplan);
				if (list.isEmpty()) {
					list = getUserIDByCardNum2(card_num, conn);
					if (list.isEmpty()) {
						throw new GeneralException(ResourceFactory
								.getProperty("train.b_plan.reg.errmsg1"));
					} else  if (list.size() > 1) {
						throw new GeneralException(ResourceFactory
								.getProperty("train.b_plan.reg.errmsg2"));
					} else  if (list.size() == 1) {
						HashMap userMap = (HashMap) list.get(0);
						String a0100 = (String) userMap.get("a0100");
						String nbase = (String) userMap.get("nbase");
						vo = getVo(a0100, nbase,conn,vo);
						this.into = true;
					} else {
						vo = null;
					}
				} else if (list.size() > 1) {
					throw new GeneralException(ResourceFactory
							.getProperty("train.b_plan.reg.errmsg2"));
				} else if (list.size() == 1) {
					HashMap userMap = (HashMap) list.get(0);
					String a0100 = (String) userMap.get("a0100");
					String nbase = (String) userMap.get("nbase");
					vo = getVo(a0100, nbase,conn,vo);
				}
			} else {
				vo = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (usRow != null) {
				try {
					usRow.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return vo;
	}
	
	/**
	 * 根据培训班id获得培训班名称
	 * @param id
	 * @param conn
	 * @return
	 */
	public String getClassName (String id, Connection conn) {
		String className = "";
		String sql = "select R3130 from r31 where r3101='"+id+"'";
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if (rs.next()) {
				className = rs.getString("R3130");
				className = className == null ? "" : className;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return className;
	}
	public RecordVo getVo (String a0100, String nbase, Connection conn,RecordVo vo) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a0100, '");
		sql.append(nbase);
		sql.append("' nbase,a0101,B0110,E0122,E01A1 FROM " + nbase);
		sql.append("A01 WHERE a0100='" + a0100 + "'");
		ContentDAO dao = new ContentDAO(conn);
		RowSet usRow = null;
		try {
			usRow = dao.search(sql.toString());
			if (usRow.next()) {
				String b0110 = usRow.getString("b0110");
				String e0122 = usRow.getString("e0122");
				String e01a1 = usRow.getString("e01a1");
				String a0101 = usRow.getString("a0101");
				vo.setString("a0100", a0100);
				vo.setString("nbase", nbase);
				vo.setString("a0101", a0101);
				vo.setString("b0110", b0110);
				vo.setString("e0122", e0122);
				vo.setString("e01a1", e01a1);
			} else {
				vo = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (usRow != null) {
					usRow.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return vo;
	}

	/**
	 * 
	 * @param regFlag
	 *            签到类别（1：签到/2：签退/3：补签到/4：补签退）
	 * @param nowDate
	 *            签到时间
	 * @param courseplan
	 *            签到课程
	 * @param conn
	 *            数据连接
	 * @return RecordVo(tr_cardtime)
	 * @throws GeneralException
	 */
    public RecordVo getRegStateVo(String regFlag, Date nowDate, String courseplan, Connection conn) throws GeneralException {
        String nowYear = OperateDate.dateToStr(nowDate, "yyyy-MM-dd");// 签到 年
        String nowTime = OperateDate.dateToStr(nowDate, "HH:mm");// 签到 时间
        ConstantXml constantbo = new ConstantXml(conn, "TR_PARAM");
        StringBuffer class_sql = new StringBuffer();
        RecordVo vo = new RecordVo("tr_cardtime");
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            
            vo.setDate("card_time", OperateDate.getDateByFormat(nowDate, "yyyy-MM-dd HH:mm:ss"));
            vo.setDate("oper_time", OperateDate.getDateByFormat(nowDate, "yyyy-MM-dd HH:mm"));
            vo.setInt("card_type", Integer.parseInt(regFlag));// --------类别（1：签到/2：签退）
            vo.setString("r4101", courseplan);
            if ("1".equals(regFlag) || "3".equals(regFlag)) {// 签到
                rs = dao.search("select begin_card from tr_classplan where R4101='" + courseplan + "' and begin_card=1");
                if(!rs.next()){
                    throw new GeneralException("",ResourceFactory.getProperty("train.b_plan.reg.errmsg8"),"","");
                }
                
                class_sql.append("SELECT begin_card,train_date,begin_time,end_time FROM tr_classplan WHERE (");
                class_sql.append(Sql_switcher.dateToChar("train_date", "yyyy-MM-dd") + " ");
                class_sql.append(Sql_switcher.concat());
                class_sql.append(" ' ' ");
                class_sql.append(Sql_switcher.concat());
                class_sql.append(" end_time) BETWEEN '" + nowYear + " " + nowTime + "' AND '" + nowYear + " 23:59:59' AND ");
                class_sql.append(Sql_switcher.isnull("begin_card", "0") + "=1");
                class_sql.append(" AND R4101='" + courseplan + "'");
                class_sql.append(" ORDER BY train_date,begin_time");
            } else if ("2".equals(regFlag) || "4".equals(regFlag)) {// 签退
                rs = dao.search("select 1 from tr_classplan where R4101='" + courseplan + "' and end_card=1");
                if(!rs.next()){
                    throw new GeneralException("",ResourceFactory.getProperty("train.b_plan.reg.errmsg9"),"","");
                }
                
                class_sql.append("SELECT end_card,train_date,begin_time,end_time FROM tr_classplan WHERE (");
                class_sql.append(Sql_switcher.dateToChar("train_date", "yyyy-MM-dd") + " ");
                class_sql.append(Sql_switcher.concat());
                class_sql.append(" ' ' ");
                class_sql.append(Sql_switcher.concat());
                class_sql.append(" begin_time) BETWEEN '" + nowYear + " 00:00' AND '" + nowYear + " " + nowTime + "' AND ");
                class_sql.append(Sql_switcher.isnull("end_card", "0") + "=1");
                class_sql.append(" AND R4101='" + courseplan + "'");
                class_sql.append(" ORDER BY train_date desc,begin_time desc");
            }

            rs = dao.search(class_sql.toString());
            if (rs.next()) {
                Date train_date = rs.getDate("train_date");
                String card_date = OperateDate.dateToStr(train_date, "yyyy-MM-dd");
                String begin_time = rs.getString("begin_time");
                String end_time = rs.getString("end_time");
                Date begin_date = OperateDate.strToDate(card_date + " " + begin_time, "yyyy-MM-dd HH:mm");
                Date end_date = OperateDate.strToDate(card_date + " " + end_time, "yyyy-MM-dd HH:mm");
                Date now_date = OperateDate.strToDate(nowYear + " " + nowTime, "yyyy-MM-dd HH:mm");
                if ("1".equals(regFlag) || "3".equals(regFlag)) {// 签到
                    if (begin_date.before(now_date)) {
                        long l_late = now_date.getTime() - begin_date.getTime();
                        int late_minutes = (int) (l_late / (1000 * 60));
                        String leave_early = constantbo.getTextValue("/param/attendance/leave_early");
                        if (leave_early == null || leave_early.length() < 1) {
                            leave_early = "0";
                        }
                        if (isNumeric(leave_early)) {
                            int iLeave_early = Integer.parseInt(leave_early);
                            if (iLeave_early < late_minutes) {
                                vo.setInt("late_for", late_minutes);
                            }
                        }
                    }
                } else if ("2".equals(regFlag) || "4".equals(regFlag)) {// 签退
                    if (end_date.after(now_date)) {
                        String late_for = constantbo.getTextValue("/param/attendance/late_for");
                        long l_late = end_date.getTime() - now_date.getTime();
                        int early_minutes = (int) (l_late / (1000 * 60));
                        if (late_for == null || late_for.length() < 1) {
                            late_for = "0";
                        }
                        if (isNumeric(late_for)) {
                            int iLate_for = Integer.parseInt(late_for);
                            if (iLate_for < early_minutes) {
                                vo.setInt("leave_early", early_minutes);
                            }
                        }
                    }
                }
            } else {
                vo = null;
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
        return vo;
    }

	/**
	 * 根据考勤卡号查找人员编号和人员库(已批)
	 * 
	 * @param card_num
	 *            考勤卡号
	 * @param conn
	 *            数据连接
	 * @return ArrayList(Map(a0100,nbase))
	 * @throws GeneralException
	 */
	public ArrayList getUserIDByCardNum(String card_num, Connection conn, String r3101)
			throws GeneralException {
		ArrayList nbases = DataDictionary.getDbpreList();
		ConstantXml constantbo = new ConstantXml(conn, "TR_PARAM");
		String card_no = getCardno(constantbo);
		StringBuffer innTable = new StringBuffer();
		for (int i = 0; i < nbases.size(); i++) {
			String nbase = (String) nbases.get(i);
			innTable.append("SELECT R4001,nbase FROM ");
			innTable.append(nbase);
			innTable.append("A01 INNER JOIN R40 R ON R.R4001=");
			innTable.append(nbase);
			innTable.append("A01.A0100 AND R.nbase='");
			innTable.append(nbase);
			innTable.append("' WHERE ");
			innTable.append(card_no);
			innTable.append("='");
			innTable.append(card_num);
			innTable.append("' and R4005='");
			innTable.append(r3101);
			innTable.append("' AND r4013='03' UNION ");
		}
		innTable.delete(innTable.length() - 6, innTable.length());
		String sql = innTable.toString();
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try {
			rs = dao.search(sql);
			while (rs.next()) {
//				int row = rs.getRow();
//				if (row == 1) {
				HashMap map = new HashMap();
				String a0100 = rs.getString("R4001");
				String nbase = rs.getString("nbase");
				map.put("a0100", a0100);
				map.put("nbase", nbase);
				list.add(map);
//				}
			}
			rs.getRow();
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
	 * 查询考勤卡号是否有效
	 * @param constantbo
	 * @return
	 * @throws GeneralException
	 */
	private String getCardno(ConstantXml constantbo) throws GeneralException {
		String card_no = constantbo.getTextValue("/param/attendance/card_no");// 获得设置的考号字段名称
		if (card_no == null || card_no.length() < 1) {
			throw new GeneralException(ResourceFactory
					.getProperty("train.attendance.set.kqcard")
					+ "!");
		} else {
			FieldItem fieldItem = DataDictionary.getFieldItem(card_no, "A01");
			if(fieldItem==null||!"1".equals(fieldItem.getUseflag())){
				throw new GeneralException(ResourceFactory
						.getProperty("train.attendance.set.kqcard")
						+ "!");
			}
		}
		return card_no;
	}

	/**
	 * 根据考勤卡号查找人员编号和人员库(已批)
	 * 
	 * @param card_num
	 *            考勤卡号
	 * @param conn
	 *            数据连接
	 * @return ArrayList(Map(a0100,nbase))
	 * @throws GeneralException
	 */
	public ArrayList getUserIDByCardNum2(String card_num, Connection conn)
			throws GeneralException {
		ArrayList nbases = this.userView.getPrivDbList();
		ConstantXml constantbo = new ConstantXml(conn, "TR_PARAM");
		String card_no = getCardno(constantbo);
		StringBuffer innTable = new StringBuffer();
		for (int i = 0; i < nbases.size(); i++) {
			String nbase = (String) nbases.get(i);
			String where = InfoUtils.getWhereINSql(this.userView, nbase);
			innTable.append("SELECT '");
			innTable.append(nbase);
			innTable.append("' nbase,a0100 ");
			innTable.append(where);
			if (where.toLowerCase().contains("where")) {
				innTable.append(" and ");
			} else {
				innTable.append(" WHERE ");
			}
			innTable.append(card_no);
			innTable.append("='");
			innTable.append(card_num);
			innTable.append("' UNION ");
		}
		innTable.delete(innTable.length() - 6, innTable.length());
		String sql = innTable.toString();
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try {
			rs = dao.search(sql);
			while (rs.next()) {
//				int row = rs.getRow();
//				if (row == 1) {
				HashMap map = new HashMap();
				String a0100 = rs.getString("a0100");
				String nbase = rs.getString("nbase");
				map.put("a0100", a0100);
				map.put("nbase", nbase);
				list.add(map);
//				}
			}
//			rs.getRow();
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
	 * 得到培训班的list
	 * 
	 * @param conn
	 * @param a_code
	 * @param spflag
	 *            审批标志，如果有多个，用逗号隔开
	 * @return
	 */
	public ArrayList getTrainClassForSpflag(Connection conn, String a_code,
			String spflag, String time) {
		StringBuffer sqlwhere = new StringBuffer();
		ArrayList classplanlist = new ArrayList();
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			sqlwhere.append("select r3101,r3130 from r31 where 1=1");
			if (spflag != null && spflag.length() > 0) {
				sqlwhere.append(" and r3127 in (");
				String flags[] = spflag.split(",");
				for (int i = 0; i < flags.length; i++) {
					sqlwhere.append("'" + flags[i] + "',");
				}
				sqlwhere.setLength(sqlwhere.length() - 1);
				sqlwhere.append(")");
			}

			if (a_code != null && a_code.trim().length() > 2) {
				String tmp[] = a_code.split("`");
				sqlwhere.append(" and (");
				for (int i = 0; i < tmp.length; i++) {
					String t = tmp[i];
					if ("UN".equalsIgnoreCase(t.substring(0, 2))) {
                        sqlwhere.append("B0110 like '" + t.substring(2, t.length()) + "%' or ");
                    }
					if ("UM".equalsIgnoreCase(t.substring(0, 2))) {
                        sqlwhere.append("E0122 like '" + t.substring(2, t.length()) + "%' or ");
                    }
				}
				sqlwhere.setLength(sqlwhere.length()-4);
				sqlwhere.append(")");
			}
			if (time != null && time.trim().length() > 2) {
				sqlwhere.append(" and " + time);
			}
			sqlwhere.append(" and (select count(1) from r41 where r4103=r3101)>0");//培训课程为空的不显示
			sqlwhere.append(" order by r3101 desc");
			//sqlwhere.append(" order by I9999");
			rs = dao.search(sqlwhere.toString());
			while (rs.next()) {
				CommonData cd = new CommonData();
				cd.setDataName(rs.getString("r3130"));
				cd.setDataValue(SafeCode.encode(PubFunc.encrypt(rs.getString("r3101"))));
				classplanlist.add(cd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
                    rs.close();
                }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return classplanlist;
	}

	/**
	 * 条件查询解析
	 * 
	 * @param search
	 * @return
	 */
	public String getSearchWhere(String search) {
		StringBuffer wherestr = new StringBuffer();
		if (search != null && search.trim().length() > 0) {
			search = SafeCode.decode(search);
			String searcharr[] = search.split("::");
			if (searcharr.length == 3) {
				wherestr.append(" and (");
				String sexpr = searcharr[0];
				String sfactor = searcharr[1];
				sexpr = PubFunc.keyWord_reback(sexpr);
				sfactor = PubFunc.keyWord_reback(sfactor);
				boolean blike = false;
				blike = searcharr[2] != null && "1".equals(searcharr[2]) ? true
						: false;
				String strSFACTOR = sfactor;
				sfactor = "";
				String strItem[] = strSFACTOR.split("`");
				String xpr = "";
				for (int i = 0; i < strItem.length; i++) {
					String item = strItem[i] + " ";
					String code = "";
					if (item.indexOf("<>") != -1) {
                        code = "<>";
                    } else if (item.indexOf(">=") != -1) {
                        code = ">=";
                    } else if (item.indexOf("<=") != -1) {
                        code = "<=";
                    } else if (item.indexOf(">") != -1) {
                        code = ">";
                    } else if (item.indexOf("<") != -1) {
                        code = "<";
                    } else {
                        code = "=";
                    }

					String emp[] = item.split(code);
					if (blike&&emp[1]!=null&&emp[1].trim().length()>0) {
						if ("b0110".equalsIgnoreCase(emp[0])
								|| "e0122".equalsIgnoreCase(emp[0])
								|| "e01a1".equalsIgnoreCase(emp[0])) {
							if("<>".equalsIgnoreCase(code)) {
                                wherestr.append(xpr + emp[0] + " not like '"
                                        + emp[1].trim() + "%'");
                            } else {
                                wherestr.append(xpr + emp[0] + " like '"
                                        + emp[1].trim() + "%'");
                            }
						} else if ("a0101".equalsIgnoreCase(emp[0])) {
							if("<>".equalsIgnoreCase(code) && !"".equals(emp[1].trim()))//zwl 查询 不等于空时候
                            {
                                wherestr.append(xpr + emp[0] + " not like '%"
                                        + emp[1].trim() + "%'");
                            } else {
                                wherestr.append(xpr + emp[0] + " like '%"
                                        + emp[1].trim() + "%'");
                            }
						} else if ("card_time".equalsIgnoreCase(emp[0])) {
							wherestr.append(xpr + emp[0] + code
									+ Sql_switcher.dateValue(emp[1].trim()));
						} else {
							emp[1] = emp[1].trim() == null
									|| emp[1].trim().length() < 1 ? "0"
									: emp[1].trim();
							wherestr.append(xpr + emp[0] + code + emp[1]);
						}
					} else {
						if ("b0110".equalsIgnoreCase(emp[0])
								|| "e0122".equalsIgnoreCase(emp[0])
								|| "e01a1".equalsIgnoreCase(emp[0])
								|| "a0101".equalsIgnoreCase(emp[0])) {
							wherestr.append(xpr + emp[0] + code +"'" + emp[1].trim()
									+ "'");
						} else if ("card_time".equalsIgnoreCase(emp[0])) {
							//wherestr.append(xpr + emp[0] + code + Sql_switcher.dateValue(emp[1].trim()));
							wherestr.append(xpr+this.getDataValue(emp[0], code, emp[1].trim()));
						} else {
							emp[1] = emp[1].trim() == null
									|| emp[1].trim().length() < 1 ? "0"
									: emp[1].trim();
							wherestr.append(xpr + Sql_switcher.isnull(emp[0], "0") + code + emp[1]);
							//System.out.println(Sql_switcher.isnull(emp[0], "0")+code+emp[1]);
						}
					}
					int temp=sexpr.indexOf((i + 1) + "") + String.valueOf(i+1).length();//下一个的位数
					if (sexpr.substring(sexpr.indexOf((i + 1) + "")) != null
							&& sexpr.substring(temp).length() > 0) {
						xpr = sexpr.substring(temp, temp+1);
						if ("+".equals(xpr)) {
                            xpr = " OR ";
                        } else if ("*".equals(xpr)) {
                            xpr = " AND ";
                        }
					} else {
                        xpr = "";
                    }
				}
				wherestr.append(")");
			}
		}
		//System.out.println(wherestr.toString());
		return wherestr.toString();
	}

	public ArrayList getClassList(String courseplan, Connection conn,
			String regFlag) {
		String sql= "";
		if("1".equals(regFlag) || "3".equals(regFlag)){
			sql = "SELECT * FROM tr_classplan WHERE r4101='" + courseplan
				+ "' ORDER BY train_date,begin_time";
		}else if("2".equals(regFlag) || "4".equals(regFlag)){
			sql = "SELECT * FROM tr_classplan WHERE r4101='" + courseplan
			+ "' ORDER BY train_date desc,begin_time desc";
		}
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				RecordVo vo = new RecordVo("tr_classplan");
				// String r4101 = rs.getString("r4101");
				// Date train_date = rs.getDate("train_date");
				// String begin_time = rs.getString("begin_time");
				// String end_time = rs.getString("end_time");
				// double class_len = rs.getDouble("class_len");
				// int begin_card = rs.getInt("begin_card");
				// int end_card = rs.getInt("end_card");
				vo.setString("r4101", rs.getString("r4101"));
				vo.setDate("train_date", rs.getDate("train_date"));
				vo.setString("begin_time", rs.getString("begin_time"));
				vo.setString("end_time", rs.getString("end_time"));
				vo.setDouble("class_len", rs.getDouble("class_len"));
				vo.setInt("begin_card", rs.getInt("begin_card"));
				vo.setInt("end_card", rs.getInt("end_card"));
				list.add(vo);
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
	 * 是否存在相同的签到记录（只能有一个签到记录）
	 * 
	 * @param vo(tr_cardtime表)
	 * @return 存在 true 不存在 false
	 */

	public boolean isExistsRecord(RecordVo vo, Connection conn) {
		TrainAtteBo bo = new TrainAtteBo();
		String nbase = vo.getString("nbase");
		String a0100 = vo.getString("a0100");
		String r4101 = vo.getString("r4101");
		Date card_time = vo.getDate("card_time");
		String card_type = vo.getString("card_type");
		Date begin_time_range = null;
		Date end_time_range = null;
		ArrayList list = bo.getClassList(r4101, conn, card_type);
		Iterator it = list.iterator();
		if ("1".equals(card_type) || "3".equals(card_type)) {
			while (it.hasNext()) {
				RecordVo classVo = (RecordVo) it.next();
				Date train_date = classVo.getDate("train_date");
				String end_time = classVo.getString("end_time");
				end_time_range = OperateDate.strToDate(OperateDate.dateToStr(
						train_date, "yyyy-MM-dd")
						+ " " + end_time, "yyyy-MM-dd HH:mm");
				if (end_time_range.after(card_time)) {
					break;
				}
				begin_time_range = end_time_range;
			}
		} else {
			while (it.hasNext()) {
				RecordVo classVo = (RecordVo) it.next();
				Date train_date = classVo.getDate("train_date");
				String begin_time = classVo.getString("begin_time");
				begin_time_range = OperateDate.strToDate(OperateDate.dateToStr(
						train_date, "yyyy-MM-dd")
						+ " " + begin_time, "yyyy-MM-dd HH:mm");
				if (begin_time_range.before(card_time)) {
					break;
				}
				end_time_range = begin_time_range;
			}
		}

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT 1 FROM tr_cardtime WHERE nbase='");
		sql.append(nbase);
		sql.append("' AND a0100='" + a0100 + "' AND r4101='" + r4101
						+ "' AND ");
		
		String formatTime="yyyy-MM-dd HH:mm";
		
		if(Sql_switcher.searchDbServer() == Constant.ORACEL){
			formatTime="yyyy-MM-dd HH24:mi";
		}
		
		
		if (begin_time_range == null) {
			sql.append(Sql_switcher.dateToChar("card_time",
							formatTime));
			sql.append("<'"
					+ OperateDate.dateToStr(end_time_range, "yyyy-MM-dd HH:mm")
					+ "'");
		} else if (end_time_range == null) {
			sql.append(Sql_switcher.dateToChar("card_time",
					formatTime));
			sql.append(">'"
					+ OperateDate.dateToStr(begin_time_range,
							"yyyy-MM-dd HH:mm") + "'");
		} else {
			sql.append(Sql_switcher.dateToChar("card_time",
					formatTime));
			sql.append(" BETWEEN '"
					+ OperateDate.dateToStr(begin_time_range,
							"yyyy-MM-dd HH:mm") + "' AND '");
			sql.append(OperateDate
					.dateToStr(end_time_range, "yyyy-MM-dd HH:mm")
					+ "'");
		}
		if ("1".equals(card_type) || "3".equals(card_type)) {
			sql.append(" AND (card_type=1 or card_type=3)");
		} else {
			sql.append(" AND (card_type=2 or card_type=4)");
		}
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			if (rs.next()) {
				return true;
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
		return false;
	}

	/**
	 * 得到统计培训按课程汇总的sql参数
	 * 
	 * @param classplan
	 * @param fielditemlist
	 * @return
	 */
	public LazyDynaBean getCourseSignCollectSQLParam(String classplan,
			ArrayList fielditemlist, String search) {
		LazyDynaBean bean = new LazyDynaBean();
		String columns = "r4101,r4701,r4703,r4705,r4707,r4709,r4711,r4713,r4715,r4717,r4719";
		ArrayList list = new ArrayList();
		for (int i = 0; i < fielditemlist.size(); i++) {
			FieldItem fielditem = (FieldItem) fielditemlist.get(i);
			if (("," + columns + ",").indexOf(fielditem.getItemid()
					.toLowerCase()) == -1) {
                fielditem.setVisible(false);
            }
			if("r4101".equalsIgnoreCase(fielditem.getItemid())) {
                fielditem.setVisible(true);
            }
			list.add(fielditem.clone());
		}
		StringBuffer sql = new StringBuffer();
		sql
				.append("select r4101,r4701,r4703,r4705,r4707,r4709,r4711,r4713,r4715,r4717,r4719 from (");
		sql
				.append("select r4101,sum(r4701) r4701,sum(r4703) r4703,sum(r4705) r4705,sum(r4707) r4707,");
		sql
				.append("sum(r4709) r4709,sum(r4711) r4711,sum(r4713) r4713,sum(r4715) r4715,sum(r4717) r4717,sum(r4719) r4719 ");
		sql.append(" from R47 WHERE Exists(");
		sql.append("select r41.r4101 from r41 where r4103='" + classplan + "'");
		sql.append(" and r41.r4101=R47.r4101)");
		sql.append(" group by r4101");
		sql.append(" ) ss");
		if (search != null && search.length() > 0) {
			sql.append(" where 1=1 " + search);
		}
		bean.set("sql_str", sql.toString());
		bean.set("where_str", "");
		bean.set("order_str", "order by r4101");
		bean.set("columns", columns);
		bean.set("fielditemlist", list);
		return bean;
	}

	/**
	 * 得到统计培训按课程汇总的sql参数
	 * 
	 * @param classplan
	 * @param fielditemlist
	 * @return
	 */
	public LazyDynaBean getClassSignCollectSQLParam(String a_code, String time,
			String search, String spflag, ArrayList fielditemlist) {
		LazyDynaBean bean = new LazyDynaBean();
		String columns = "r4103,r4701,r4703,r4705,r4707,r4709,r4711,r4713,r4715,r4717,r4719";
		ArrayList list = new ArrayList();
		FieldItem fielditem = new FieldItem();
		fielditem.setItemdesc("培训班名称");
		fielditem.setItemid("r4103");
		fielditem.setItemtype("A");
		fielditem.setCodesetid("0");
		fielditem.setItemlength(50);
		fielditem.setVisible(true);
		list.add(fielditem);
		
	    String allColumns = "," + columns + ",";
		for (int i = 0; i < fielditemlist.size(); i++) {
			fielditem = (FieldItem) fielditemlist.get(i);
			String itemid = fielditem.getItemid().toLowerCase();
			
			if("r4103".equals(itemid)) {
                continue;
            }
			
			if (allColumns.indexOf(itemid) == -1) {
                fielditem.setVisible(false);
            }
			
			list.add(fielditem.clone());
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select r4103,r4701,r4703,r4705,r4707,r4709,r4711,r4713,r4715,r4717,r4719 from (");
		sql.append("select r4103,sum(r4701) r4701,sum(r4703) r4703,sum(r4705) r4705,sum(r4707) r4707,");
		sql.append("sum(r4709) r4709,sum(r4711) r4711,sum(r4713) r4713,sum(r4715) r4715,sum(r4717) r4717,sum(r4719) r4719 ");
		sql.append(" from R47 left join r41 on r41.r4101=R47.r4101 ");
		sql.append("where exists");
		// 培训班
		sql.append("(select r3101 from r31 where 1=1");
		if (spflag != null && spflag.length() > 0) {
			sql.append(" and r3127 in (");
			String flags[] = spflag.split(",");
			for (int i = 0; i < flags.length; i++) {
				sql.append("'" + flags[i] + "',");
			}
			sql.setLength(sql.length() - 1);
			sql.append(")");
		}

		if (a_code != null && a_code.trim().length() > 2) {
			String tmp[] = a_code.split("`");
			sql.append(" and (");
			for (int i = 0; i < tmp.length; i++) {
				String t = tmp[i];
				if ("UN".equalsIgnoreCase(t.substring(0, 2))) {
                    sql.append("B0110 like '" + t.substring(2, t.length()) + "%' or ");
                }
				if ("UM".equalsIgnoreCase(t.substring(0, 2))) {
                    sql.append("E0122 like '" + t.substring(2, t.length()) + "%' or ");
                }
			}
			sql.setLength(sql.length()-4);
			sql.append(")");
		}
		if (time != null && time.trim().length() > 2) {
			sql.append(" and " + time);
		}
		sql.append(" and r41.r4103=R31.r3101)");
		sql.append(" group by r4103");
		sql.append(")ss");
		if (search != null && search.length() > 0) {
			sql.append(" where 1=1 " + search);
		}
		// System.out.println(sql.toString());

		bean.set("sql_str", sql.toString());
		bean.set("where_str", "");
		bean.set("order_str", "order by r4103");
		bean.set("columns", columns);
		bean.set("fielditemlist", list);
		return bean;
	}

	/**
	 * 得到培训班状态
	 * 
	 * @param conn
	 * @param r3101
	 * @return
	 */
	public LazyDynaBean getTrainClassSpflag(Connection conn, String r3101) {
		StringBuffer sqlwhere = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		LazyDynaBean bean = new LazyDynaBean();
		RowSet rs = null;
		try {
			sqlwhere.append("select r3101,r3130,r3127 from r31 where r3101='"
					+ r3101 + "'");
			rs = dao.search(sqlwhere.toString());
			while (rs.next()) {

				bean.set("r3101", rs.getString("r3130"));
				bean.set("r3130", rs.getString("r3101"));
				bean.set("r3127", rs.getString("r3127"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
                    rs.close();
                }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return bean;
	}

	public boolean getInto() {
		return into;
	}
	
	/**
	 * 未排进培训班的人员刷卡时直接进库
	 * @param r3101 培训班ID
	 * @param r4101 培训课程ID
	 * @param card_num 卡号
	 * @throws GeneralException 
	 */
	public void addTrainStudent(UserView userview,Connection conn,String r3101,String r4101,String card_num) throws GeneralException{
		ContentDAO dao = new ContentDAO(conn);
		String r40Info[]=getR41Info(conn,r4101);
		RowSet rs = null;
		ArrayList nbases = userview.getPrivDbList();
		ConstantXml constantbo = new ConstantXml(conn, "TR_PARAM");
		String card_no = getCardno(constantbo);

		ConstantXml cx = new ConstantXml(conn);
		String ZP_DBNAME=cx.getConstantValue("ZP_DBNAME");
		try{
			for (int i = 0; i < nbases.size(); i++) {
				StringBuffer innTable = new StringBuffer();
				String nbase = (String) nbases.get(i);
				if(nbase!=null&&nbase.length()>0){
					if(nbase.equalsIgnoreCase(ZP_DBNAME)) {
                        continue;
                    }
					innTable.append("SELECT a0100,a0101,b0110,e0122");
					innTable.append(InfoUtils.getWhereINSql(userview, nbase));
					if(userview.isSuper_admin()) {
                        innTable.append("WHERE ");
                    } else {
                        innTable.append("AND ");
                    }
					innTable.append(card_no);
					innTable.append("='");
					innTable.append(card_num+"'");
					
					rs = dao.search(innTable.toString());
					while (rs.next()) {
						int row = rs.getRow();
						if (row == 1) {
							RecordVo vo = new RecordVo("r40");
							vo.setString("r4001",rs.getString("a0100"));
							vo.setString("r4005",r3101);
							vo.setString("r4002",rs.getString("a0101"));
							vo.setString("b0110",rs.getString("b0110"));
							vo.setString("e0122",rs.getString("e0122"));
							vo.setDate("r4006",r40Info[0]);
							vo.setDate("r4007",r40Info[1]);
							String str = r40Info[2];
							if (str == null || str.length() < 1) {
								str = "0";
							}
							vo.setDouble("r4008", Double.parseDouble(str));
//							vo.setInt("r4008",Integer.parseInt(r40Info[2])Float.p);
							vo.setString("nbase",nbase);
							vo.setString("r4013","03");
							dao.addValueObject(vo);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getDataValue(String fielditemid,String operate,String value)
	{

		StringBuffer a_value=new StringBuffer("");	
		if(value!=null&&value.length()>0){
			String[] tempvalue=value.split("-");
			if(tempvalue.length==1){
				value=value+"-01-01";
			}
			if(tempvalue.length==2){
				if(tempvalue[1].length()==1){
					value=tempvalue[0]+"-0"+tempvalue[1]+"-01";
				}else{
					value=value+"-01";
				}
			}
			if(tempvalue.length==3){
				if(tempvalue[1].length()==1){
					tempvalue[1]="0"+tempvalue[1];
				}
				if(tempvalue[2].length()==1){
					tempvalue[2]="0"+tempvalue[2];
				}
				value=tempvalue[0]+"-"+tempvalue[1]+"-"+tempvalue[2];
			}
			try
			{

				if("=".equals(operate))
				{
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" and ");
					a_value.append(Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" and ");
					a_value.append(Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(" ) ");
				}
				else 
				{	if(">=".equals(operate)){
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid)+">"+value.substring(0,4)+" or ( ");
					a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+">"+value.substring(5,7)+" ) or ( ");
					a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+">="+value.substring(8));
					a_value.append(") ) ");
				}
				else if("<=".equals(operate)){
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid)+"<"+value.substring(0,4)+" or ( ");
					a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"<"+value.substring(5,7)+" ) or ( ");
					a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+"<="+value.substring(8));
					a_value.append(") ) ");
				}else
				{
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" or ( ");
					a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" ) or ( ");
					a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(") ) ");

				}


				}

				/*	a_value.append(fielditemid);
				a_value.append(operate);
				a_value.append(Sql_switcher.dateValue(value));
				 */
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}else{
			a_value.append(fielditemid+operate+"null");
			a_value.append(" or "+fielditemid+operate+"''");
		}
		return a_value.toString();
	}
	
	public void addClassPlanColumn(Connection conn){
		ContentDAO dao=new ContentDAO(conn);
		DbWizard db=new DbWizard(conn);
		if(!db.isExistField("tr_classplan", "minute", false)){
			try {
				dao.update("ALTER TABLE tr_classplan ADD Minute numeric(8,2)");
				DBMetaModel dbmodel=new DBMetaModel(conn);
				dbmodel.reloadTableModel("tr_classplan");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}
}
