package com.hjsj.hrms.businessobject.workplan.summary;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

/**
 * 工作总结业务对象类
 * <p>
 * Title: WorkPlanSummaryBo
 * </p>
 * <p>
 * Description: 工作总结业务对象类
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2014-6-9 上午11:51:38
 * </p>
 * 
 * @author zhaoxj
 * @version 1.0
 */
public class WorkPlanSummaryBo {

	private Connection conn;
	private UserView userView;

	public WorkPlanSummaryBo() {
	}

	public WorkPlanSummaryBo(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}

	/**
	 * 传数字返回对应汉字 某年某月第一周
	 * 
	 * @param summaryCycle
	 * @param summaryYear
	 * @param summaryMonth
	 * @param summaryWeek
	 * @return
	 * @author szk 2014-9-15下午04:35:40
	 */
	public String getSummaryCycleDesc(String summaryCycle, String summaryYear,
			String summaryMonth, String summaryWeek) {
		String desc = summaryYear + "年";
		if (WorkPlanConstant.SummaryCycle.YEAR.equals(summaryYear)) {
            return desc;
        }

		if (WorkPlanConstant.SummaryCycle.HALFYEAR.equals(summaryCycle)) {
			if ("1".equals(summaryWeek)) {
                desc = desc + "上";
            } else {
                desc = desc + "下";
            }
			desc = desc + "半年";
		} else if (WorkPlanConstant.SummaryCycle.QUARTER.equals(summaryCycle)) {
			desc = desc
					+ "第"
					+ WorkPlanConstant.NUM_DESC[Integer.parseInt(summaryWeek) - 1]
					+ "季度";
		} else if (WorkPlanConstant.SummaryCycle.MONTH.equals(summaryCycle)) {
			desc = desc + summaryMonth + "月";
		} else if (WorkPlanConstant.SummaryCycle.WEEK.equals(summaryCycle)) {
			desc = desc
					+ summaryMonth
					+ "月"
					+ "第"
					+ WorkPlanConstant.NUM_DESC[Integer.parseInt(summaryWeek) - 1]
					+ "周";
		}

		return desc;
	}

	/**
	 * 传数字返回对应汉字 第一周 无年月
	 * 
	 * @param summaryCycle
	 * @param summaryYear
	 * @param summaryMonth
	 * @param summaryWeek
	 * @return
	 * @author szk 2014-9-15下午04:35:40
	 */
	public String getSummaryCycleDescone(String summaryCycle,
			String summaryYear, String summaryMonth, String summaryWeek) {
		String desc = "";
		if (WorkPlanConstant.SummaryCycle.YEAR.equals(summaryYear)) {
            return summaryYear + "年";
        }

		if (WorkPlanConstant.SummaryCycle.HALFYEAR.equals(summaryCycle)) {
			if ("1".equals(summaryWeek)) {
                desc = desc + "上";
            } else {
                desc = desc + "下";
            }
			desc = desc + "半年";
		} else if (WorkPlanConstant.SummaryCycle.QUARTER.equals(summaryCycle)) {
			desc = desc
					+ "第"
					+ WorkPlanConstant.NUM_DESC[Integer.parseInt(summaryWeek) - 1]
					+ "季度";
		} else if (WorkPlanConstant.SummaryCycle.MONTH.equals(summaryCycle)) {
			desc = desc + summaryWeek + "月";
		} else if (WorkPlanConstant.SummaryCycle.WEEK.equals(summaryCycle)) {
			desc = "第"
					+ WorkPlanConstant.NUM_DESC[Integer.parseInt(summaryWeek) - 1]
					+ "周";
		}
		return desc;
	}

	public String getCurCycleIndex(String summaryCycle, String summaryYear,
			String summaryMonth, String defaultWeek) {
		String curCycleIndex = "1";

		Date now = new Date();
		int curYear = DateUtils.getYear(now);
		int curMonth = DateUtils.getMonth(now);

		// 如果所选年度是当年，那么试着定位当前日期所在周期
		if (curYear != Integer.parseInt(summaryYear)) {
            return curCycleIndex;
        }

		if (WorkPlanConstant.SummaryCycle.WEEK.equals(summaryCycle)) {
		    //计算当天所属的那一周是属于哪个月的（主要是月初与月末） chenxg 2017-02-24
		    if (this.getTrueDate(curYear, curMonth, -1)) {
		        curYear = DateUtils.getYear(DateUtils.addMonths(now, -1));
		        curMonth = DateUtils.getMonth(DateUtils.addMonths(now, -1));
            } else if (this.getTrueDate(DateUtils.getYear(DateUtils.addMonths(now, 1)), DateUtils.getMonth(DateUtils.addMonths(now, -1)), 1)) {
                curYear = DateUtils.getYear(DateUtils.addMonths(now, 1));
                curMonth = DateUtils.getMonth(DateUtils.addMonths(now, 1));
            }
		    
			// 默认显示当前周
			if (!(curMonth < 10 ? curYear + "-0" + curMonth : curYear + "-" + curMonth).equals(
					Integer.parseInt(summaryMonth) < 10 ? summaryYear + "-0" + summaryMonth : summaryYear + "-" + summaryMonth)) {
				curCycleIndex = defaultWeek;
			}else {
				WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
				curCycleIndex = wpUtil.getWhichWeekInMonth(new Date())[2]+"";
			}
		} else if (WorkPlanConstant.SummaryCycle.MONTH.equals(summaryCycle)) {
			curCycleIndex = String.valueOf(curMonth);
		} else if (WorkPlanConstant.SummaryCycle.QUARTER.equals(summaryCycle)) {
			curCycleIndex = String.valueOf(DateUtils.getQuarter(now));
		} else if (WorkPlanConstant.SummaryCycle.HALFYEAR.equals(summaryCycle)) {
			// 当前月位于下半年
			if (curMonth > 6) {
                curCycleIndex = "2";
            }
		}

		return curCycleIndex;
	}

	/******
	 * 按时间区间汇总
	 * 
	 * @param cyclenow
	 * @param summaryCycle
	 * @param summaryYear
	 * @param summaryMonth
	 * @param select
	 * @param belong_type
	 * @param type
	 * @param e0122
	 * @param nbase
	 * @param a0100
	 * @return
	 * @author szk 2014-9-17下午02:33:30
	 */
	public String CollectByTime(String cyclenow, String summaryCycle,
			String summaryYear, String summaryMonth, String select,
			String belong_type, String type, String e0122, String nbase,
			String a0100) {
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer thisPlanTaskList = new StringBuffer(); // 本周计划
		try {
			// 本月有多少周
			int weeknum = getCollectSummaryNum(cyclenow, summaryCycle,
					summaryYear, summaryMonth);
			// 循环提取总结
			int start = 1;
			if (!WorkPlanConstant.SummaryCycle.WEEK.equals(summaryCycle)) {
				start = weeknum * (Integer.parseInt(select) - 1) + 1;
			}
			// for (int i = start; i <= weeknum; i++)
			while (weeknum > 0) {
				// 月报时参数位置不同
				String[] summaryDates = WorkPlanConstant.SummaryCycle.MONTH
						.equals(summaryCycle) ? getSummaryDates(summaryCycle,
						summaryYear, start + "", 0) : getSummaryDates(
						summaryCycle, summaryYear, summaryMonth, start);
				String desc = getSummaryCycleDescone(summaryCycle, summaryYear,
						summaryMonth, "" + start);
				thisPlanTaskList.append("\n" + desc + "工作总结 ");
				if (WorkPlanConstant.SummaryCycle.WEEK.equals(summaryCycle)) {
					thisPlanTaskList.append("("
							+ summaryDates[0].substring(5).replace('-', '月')
							+ "日~"
							+ summaryDates[1].substring(5).replace('-', '月')
							+ "日)");
				}
				// thisPlanTaskList.append("\n\n"+desc+"总结内容\n");
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT p0109,p0120");
				sql.append(" FROM P01 ");
				sql.append(" WHERE state=" + summaryCycle);
				sql.append(" AND P0104="
						+ Sql_switcher.dateValue(summaryDates[0]));
				sql.append(" AND P0106="
						+ Sql_switcher.dateValue(summaryDates[1]));

				// 我的工作总结
				if ("org".equals(type) || "2".equals(belong_type)) { // 部门
					sql.append(" AND e0122='" + e0122 + "'");
					sql.append(" AND belong_type=2");
				} else if ("person".equals(type) || belong_type == null
						|| !"2".equals(belong_type)) {
					sql.append(" AND nbase='" + nbase + "'");
					sql.append(" AND a0100='" + a0100 + "'");
					sql.append(" AND (belong_type is null or belong_type=0)");
				}

				RowSet frowset;

				frowset = dao.search(sql.toString());

				if (frowset.next()) {
					if (frowset.getString("p0109") != null
							&& frowset.getString("p0109").trim().length() > 0) {
						thisPlanTaskList.append("\n"
								+ frowset.getString("p0109") + "\n");
					} else {
                        thisPlanTaskList.append("\n未填写！\n");
                    }
					// p0120 = frowset.getString("p0120");
				} else {
					thisPlanTaskList.append("\n未填写！\n");
				}

				weeknum--;
				start++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return thisPlanTaskList.toString();
	}

	/**
	 * 得到工作总结的起止日期
	 * 
	 * @Title: getSummaryStartDate
	 * @Description: 得到工作总结的起止日期
	 * @param summaryCycle
	 *            工作总结周期（年、半年、季度、月、周, 具体值参考WorkPlanConstant.SummaryCycle）
	 * @param summaryYear
	 *            工作总结所在年份
	 * @param summaryMonth
	 *            周总结所在月份，其它类型不使用该值
	 * @param cycleIndex
	 *            工作总结所在周期的索引（第几周、第几月...)
	 * @return YYYY-MM-DD
	 */
	public String[] getSummaryDates(String summaryCycle, String summaryYear,
			String summaryMonth, int cycleIndex) {
		String startDate = "";
		String endDate = "";

		if (WorkPlanConstant.SummaryCycle.YEAR.equals(summaryCycle)) {
			startDate = summaryYear + "-01-01";
			endDate = summaryYear + "-12-31";
		} else if (WorkPlanConstant.SummaryCycle.HALFYEAR.equals(summaryCycle)) {
			if (1 == cycleIndex) {
				startDate = summaryYear + "-01-01";
				endDate = summaryYear + "-06-30";
			} else {
				startDate = summaryYear + "-07-01";
				endDate = summaryYear + "-12-31";
			}
		} else if (WorkPlanConstant.SummaryCycle.QUARTER.equals(summaryCycle)) {
			switch (cycleIndex) {
			case 1:
				startDate = summaryYear + "-01-01";
				endDate = summaryYear + "-03-31";
				break;
			case 2:
				startDate = summaryYear + "-04-01";
				endDate = summaryYear + "-06-30";
				break;
			case 3:
				startDate = summaryYear + "-07-01";
				endDate = summaryYear + "-09-30";
				break;
			case 4:
				startDate = summaryYear + "-10-01";
				endDate = summaryYear + "-12-31";
				break;
			}
		} else if (WorkPlanConstant.SummaryCycle.MONTH.equals(summaryCycle)) {
			startDate = summaryYear + "-";
			if (Integer.parseInt(summaryMonth) < 10) {
                startDate = startDate + "0";
            }
			startDate = startDate + summaryMonth + "-";
			endDate = startDate;

			startDate = startDate + "01";

			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(summaryYear), Integer
					.parseInt(summaryMonth) - 1, 1);
			int lastDayInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			endDate = endDate + String.valueOf(lastDayInMonth);
		} else if (WorkPlanConstant.SummaryCycle.WEEK.equals(summaryCycle)) {
			startDate = getMondayOfDate(Integer.parseInt(summaryYear), Integer
					.parseInt(summaryMonth), cycleIndex);
			endDate = getSunDayOfDate(Integer.parseInt(summaryYear), Integer
					.parseInt(summaryMonth), cycleIndex);
		}

		String[] summaryDates = { startDate, endDate };
		return summaryDates;
	}

	public int getSummaryNum(String cycle, String year, String month) {
		int num = 0;

		if (WorkPlanConstant.SummaryCycle.WEEK.equals(cycle)) {
            num = getWeekNum(Integer.parseInt(year), Integer.parseInt(month));
        } else if (WorkPlanConstant.SummaryCycle.MONTH.equals(cycle)) {
            num = 12;
        } else if (WorkPlanConstant.SummaryCycle.QUARTER.equals(cycle)) {
            num = 4;
        } else if (WorkPlanConstant.SummaryCycle.HALFYEAR.equals(cycle)) {
            num = 2;
        } else if (WorkPlanConstant.SummaryCycle.YEAR.equals(cycle)) {
            num = 1;
        }

		return num;
	}

	/**
	 * 汇总是计算月数
	 * 
	 * @param cyclenow
	 * @param summaryCycle
	 * @param summaryYear
	 * @param summaryMonth
	 * @return
	 * @author szk 2014-9-16上午10:44:00
	 */
	public int getCollectSummaryNum(String cyclenow, String cycle, String year,
			String month) {
		int num = 0;

		if (WorkPlanConstant.SummaryCycle.WEEK.equals(cycle)) {
            num = getWeekNum(Integer.parseInt(year), Integer.parseInt(month));
        } else if (WorkPlanConstant.SummaryCycle.QUARTER.equals(cyclenow)) {
            num = 3;
        } else if (WorkPlanConstant.SummaryCycle.HALFYEAR.equals(cyclenow)) {
			if (WorkPlanConstant.SummaryCycle.MONTH.equals(cycle)) {
                num = 6;
            } else if (WorkPlanConstant.SummaryCycle.QUARTER.equals(cycle)) {
                num = 2;
            }
		} else if (WorkPlanConstant.SummaryCycle.YEAR.equals(cyclenow)) {
            num = 4;
        }

		return num;
	}

	/**
	 * 获取某月有几周
	 * 
	 * @return
	 * @author szk 2014-7-3下午02:56:20
	 * @throws ParseException
	 */
	public int getWeekNum(int summaryYear, int summaryMonth) {
		int weeknum = 0;
		int weekInMonth = getWeekInMonth();

		Calendar c = Calendar.getInstance();
		c.set(summaryYear, summaryMonth - 1, 1);
		/**
		 * System.out.println("------------" + c.get(Calendar.YEAR) + "年" +
		 * (c.get(Calendar.MONTH)+1) + "月的天数和周数-------------");
		 * System.out.println("天数：" +
		 * c.getActualMaximum(Calendar.DAY_OF_MONTH)); System.out.println("周数："
		 * + c.getActualMaximum(Calendar.WEEK_OF_MONTH));
		 ***/

		Calendar weekCal = Calendar.getInstance();
		for (int i = 1; i <= c.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
			weekCal.set(summaryYear, summaryMonth - 1, i);
			int weekIndex = weekCal.get(Calendar.DAY_OF_WEEK);

			if (weekIndex == 1) {
				weekIndex = 7;
			} else {
				weekIndex = weekIndex - 1;
			}

			if (weekInMonth == weekIndex) {
				weeknum++;
			}
		}

		return weeknum;
	}

	/*
	 * 传入 第一周 返回 1
	 */
	public int getWeekId(String selectweek) {
		String[] weeks = { "第一周", "第二周", "第三周", "第四周", "第五周", };
		ArrayList weeklist = new ArrayList(Arrays.asList(weeks));
		return weeklist.indexOf(selectweek) + 1;
	}

	/**
	 * 得到工作总结起止时间sql条件
	 * 
	 * @Title: getSummaryDatesWhr
	 * @Description:
	 * @param year
	 *            总结所在年度
	 * @param month
	 *            总结所在月度
	 * @param cycleIndex
	 *            总结序号（第几周、第几季度......）
	 * @return
	 */
	public String getSummaryDatesWhr(String cycle, String year, String month,
			int cycleIndex) {

		String[] summaryDates = getSummaryDates(cycle, year, month, cycleIndex);

		StringBuilder sql = new StringBuilder();
		sql.append(" state=" + cycle);
		sql.append(" AND P0104=" + Sql_switcher.dateValue(summaryDates[0]));
		sql.append(" AND P0106=" + Sql_switcher.dateValue(summaryDates[1]));

		return sql.toString();
	}

	/**
	 * 得到某日子是星期几（1~7：一到日）
	 * 
	 * @Title: getWeek
	 * @Description:
	 * @param year
	 * @param month
	 * @param date
	 * @return 1~7
	 */
	private int getWeek(int year, int month, int date) {
		Calendar c = Calendar.getInstance();

		c.set(year, month - 1, date);
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}

		return dayForWeek;
	}

	/**
	 * 获取某月的第一周的星期一 日期
	 * 
	 * @param datetime
	 * @return
	 */
	private Date getFirstMonday(int year, int month) {

		Date monday = null;
		// 获取，1号是星期几
		int dayForWeek = this.getWeek(year, month, 1);
		int weekInMonth = this.getWeekInMonth();

		Date firstDayInMonth = DateUtils.getDate(year, month, 1);
		if (dayForWeek <= weekInMonth) {// 本月
			monday = DateUtils.addDays(firstDayInMonth, -(dayForWeek - 1));
		} else if (dayForWeek > weekInMonth) {// 上月
			monday = DateUtils.addDays(firstDayInMonth, (7 - dayForWeek + 1));
		}

		return monday;
	}

	private int getWeekInMonth() {
		String weekInMonth = SystemConfig.getPropertyValue("week_in_month");// 系统中设置周几为一周的标识
		if (null == weekInMonth) {
            weekInMonth = "3";
        } else {
            weekInMonth = weekInMonth.trim();
        }

		if ("0".compareTo(weekInMonth) < 0 || "8".compareTo(weekInMonth) > 0) {
			weekInMonth = "3";
		}

		return Integer.parseInt(weekInMonth);
	}

	/**
	 * 根据某年某月中的第几周，获取到，该周的周一的日期
	 * 
	 * @param datetime
	 * @param whichWeek
	 * @return
	 */
	public String getMondayOfDate(int year, int month, int whichWeek) {
		Date monday = this.getFirstMonday(year, month);
		return DateUtils.FormatDate(DateUtils.addDays(monday,
				(whichWeek - 1) * 7), "yyyy-MM-dd");
	}

	/**
	 * 根据某年某月中的第几周，获取到，该周的周七的日期
	 * 
	 * @param datetime
	 * @param whichWeek
	 * @return
	 */
	public String getSunDayOfDate(int year, int month, int whichWeek) {
		Date sunday = this.getFirstMonday(year, month);
		return DateUtils.FormatDate(DateUtils.addDays(sunday,
				6 + (whichWeek - 1) * 7), "yyyy-MM-dd");
	}

	/**
	 * 当前期间和已批时间
	 * 
	 * @param dao
	 * @param datetime
	 * @param nbase
	 * @param a0100
	 * @return 当前期间 ，已批
	 * @author szk 2014-7-8下午04:51:10
	 * @throws ParseException
	 */
	public ArrayList getP011503Num(ContentDAO dao, int cycle, int year,
			int month, String nbase, String a0100, String e0122,
			String belong_type) throws ParseException {
	    return getP011503Num(dao, cycle, year, month, nbase, a0100, e0122, belong_type, new Date());
	}

	/**
	 * 当前期间和已批时间
	 * @param dao
	 * @param cycle
	 * @param year
	 * @param month
	 * @param nbase
	 * @param a0100
	 * @param e0122
	 * @param belong_type
	 * @param date
	 * @return 当前期间 ，已批
	 * @throws ParseException
	 */
	public ArrayList getP011503Num(ContentDAO dao, int cycle, int year,
            int month, String nbase, String a0100, String e0122,
            String belong_type, Date date) throws ParseException {
        String num = "";
        RowSet rs = null;

        int weeknum = 1;
        String strCycle = String.valueOf(cycle);
        if (WorkPlanConstant.SummaryCycle.WEEK.equals(strCycle)) {
            weeknum = getWeekNum(year, month);
        } else if (WorkPlanConstant.SummaryCycle.MONTH.equals(strCycle)) {
            weeknum = 12;
        } else if (WorkPlanConstant.SummaryCycle.QUARTER.equals(strCycle)) {
            weeknum = 4;
        } else if (WorkPlanConstant.SummaryCycle.HALFYEAR.equals(strCycle)) {
            weeknum = 2;
        }

        int have = 1; // 应填
        int flag = 0;
        try {
            for (int i = 1; i <= weeknum; i++) {
                // 取报告期的开始日期，以判断是否应交
                String[] summaryDates = WorkPlanConstant.SummaryCycle.MONTH
                        .equals(strCycle) ? getSummaryDates(strCycle, String
                        .valueOf(year), i + "", i) : getSummaryDates(strCycle,
                        String.valueOf(year), String.valueOf(month), i);
                String firstday = summaryDates[0];

                if (DateUtils.getDate(firstday, "yyyy-MM-dd").after(date)
                        && flag == 0) {
                    have = i - 1;
                    flag = 1;
                } else if (i == weeknum && flag == 0) {
                    have = weeknum;
                }

                String summaryDatesWhr = getSummaryDatesWhr(strCycle, String
                        .valueOf(year), String.valueOf(month), i);

                StringBuffer strSql = new StringBuffer(
                        "select p0115,time from P01 ");
                strSql.append(" WHERE " + summaryDatesWhr);
                if ("2".equals(belong_type)) {
                    strSql.append(" and e0122 ='" + e0122
                            + "' and belong_type = 2");// ,p0120
                } else {
                    strSql.append(" and nbase  = '" + nbase + "' and a0100 ='"
                            + a0100);
                    strSql
                            .append("' and (belong_type is null or belong_type =0)");// ,p0120
                }

                rs = dao.search(strSql.toString());
                if (rs.next()) {
                    if ("02".equals(rs.getString("p0115"))) {
                        num += rs.getString("time") + "_";
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            WorkPlanUtil.closeDBResource(rs);
        }
        ArrayList re = new ArrayList();
        re.add("" + have);
        re.add(num);
        return re;
    }

	/**
	 * 查询关注人
	 * 
	 * @param para
	 * @return
	 * @author szk 2014-7-21下午04:30:07
	 * @param p0100
	 * @param num
	 *            第几页
	 * @param rownum
	 *            几条
	 * @param a0100
	 * @param nbase
	 * @param querytype
	 * @throws GeneralException
	 */
	public ArrayList searchPersonList(String para, String p0100, int rownum,
			int num, String nbase, String a0100, String e01a1, String querytype)
			throws GeneralException {
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			// 得到认证人员库列表
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
			String strpres = "";
			if (login_vo != null) {
                strpres = login_vo.getString("str_value");
            }

			String[] arrpre = strpres.split(",");
			if (arrpre.length <= 0) {
				throw new GeneralException("未设置认证人员库！");
			}

			WorkPlanUtil wpu = new WorkPlanUtil(this.conn, this.userView);
			// 获取拼音简码的字段
			String pinyinFld = wpu.getPinYinFld();

			// 获取唯一性指标的字段
			String uniqueFld = wpu.getUniquenessFld();

			// 得到电子邮箱指标
			String emailFld = wpu.getEmailFld();
			// 上级Usr000009,Usr0000049格式
			String excludeIds = "";
			if ("person".equals(querytype)) {
				String objectid = nbase + a0100;
				excludeIds = excludeIds + ","
						+ wpu.getMyAllSuperPerson(objectid, "1");
				// 如果是上级指定关注人
				excludeIds = excludeIds + "," + objectid;
			} else if ("org".equals(querytype)) {
				String objectid = e01a1;
				excludeIds = excludeIds + ","
						+ wpu.getMyAllSuperPerson(objectid, "2");
				// 如果是上级指定关注人
				excludeIds = excludeIds + ","
						+ wpu.getFirstDeptLeaders(objectid);
			}
			StringBuffer sql = new StringBuffer();

			sql.append("SELECT");
			if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
				sql.append(" TOP " + rownum + " ");
			}

			sql.append("nbase,a0100,a0101,b0110,E0122,E01A1," + emailFld
					+ ",org,pos FROM (");
			for (int i = 0; i < arrpre.length; i++) {
				String pre = arrpre[i];
				sql.append("SELECT  '" + pre + "' nbase,");

				if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
					sql
							.append("ROW_NUMBER() OVER (ORDER BY a.a0000) AS RowNo,");
				} else {
					sql.append("rownum as rowno,");
				}

				sql.append("a.a0100,a0101,b0110,E0122,E01A1," + emailFld
						+ ",o2.codeitemdesc org,o3.codeitemdesc pos");
				sql.append(" FROM " + pre + "A01 a left join organization o2");
				sql
						.append(" on a.E0122 = o2.codeitemid left join organization o3");
				sql.append(" on a.E01A1 = o3.codeitemid ");
				// szk用联表方式替代not in，排除已选的人
				sql.append("left join (select * from P09 where P0903 = '"
						+ p0100 + "' and p0901 =3) p");
				sql.append(" on a.A0100 = p.A0100");
				sql.append(" where p.a0100 is null ");
				sql.append(" and (a.a0101 LIKE '%" + para + "%'");

				if (!"".equals(pinyinFld)) {
                    sql.append(" OR a." + pinyinFld + " like '" + para + "%'");
                }

				if (!"".equals(emailFld)) {
                    sql.append(" OR a." + emailFld + " like '" + para + "%'");
                }

				if (!"".equals(uniqueFld)) {
                    sql.append(" OR a." + uniqueFld + " LIKE '" + para + "%'");
                }

				sql.append(") union ");
			}

			if (sql.length() > 0) {
                sql.setLength(sql.length() - 7);
            }

			if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
				sql.append(") AS A WHERE RowNo > " + rownum + "*(" + num
						+ "-1)");
			} else {
				sql.append(") a where rowno between "
						+ (1 + rownum * (num - 1)) + " and " + (rownum * num));
			}

			WorkPlanBo pb = new WorkPlanBo(conn, userView);
			rs = dao.search(sql.toString());
			while (rs.next()) {
				// 排除自己 和上级
				if (!(rs.getString("a0100").equals(a0100) && rs.getString(
						"nbase").equals(nbase))
						&& excludeIds.indexOf(rs.getString("nbase")
								+ rs.getString("a0100")) == -1) {
					ArrayList personInfo = new ArrayList();
					// 要存的数据
					personInfo.add(rs.getString("nbase") + "^"
							+ rs.getString("a0100") + "^"
							+ rs.getString("a0101") + "^"
							+ rs.getString("b0110") + "^"
							+ rs.getString("E0122") + "^"
							+ rs.getString("E01A1"));
					personInfo.add(rs.getString("a0101") + " "); // 暂时不显示岗位 +
																	// rs.getString("pos")
																	// + " "
					personInfo.add(rs.getString(emailFld) == null ? "" : rs
							.getString(emailFld)); // 邮箱
					String photoUrl = pb.getPhotoPath(rs.getString("nbase"), rs
							.getString("a0100"));
					personInfo.add(photoUrl);
					personInfo.add(rs.getString("org") == null ? "" : rs
							.getString("org"));
					list.add(personInfo);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneralException("查询人员信息失败！");
		} finally {
			WorkPlanUtil.closeDBResource(rs);
		}

		return list;
	}

	/**
	 * @param para
	 *            :nbase^a0100^a0101^b0110^e0122^e01a1
	 * @author szk 2014-7-24上午11:04:18
	 * @param p0100
	 */
	public HashMap addperson(String p0100, String para) {
		// String[] paras = para.split("\\^");
		String nbase = para.substring(0, 3);
		String a0100 = para.substring(3, para.length());
		ContentDAO dao = new ContentDAO(this.conn);
		HashMap mp = new HashMap();
		try {
			RecordVo p09Vo = new RecordVo("p09");
			IDGenerator idg = new IDGenerator(2, this.conn);
			String pid = idg.getId("P09.P0900");
			p09Vo.setString("p0900", pid);
			p09Vo.setInt("p0901", 3); // 3、工作总结
			p09Vo.setInt("p0903", Integer.parseInt(p0100));
			p09Vo.setString("nbase", nbase);
			p09Vo.setString("a0100", a0100);
			p09Vo.setInt("p0905", 3); // 关注人

			String strsql = "select * from " + nbase + "A01 where a0100='"
					+ a0100 + "'";
			RowSet rset = dao.search(strsql);
			if (rset.next()) {
				p09Vo.setString("p0907", rset.getString("b0110"));
				p09Vo.setString("p0909", rset.getString("e0122"));
				p09Vo.setString("p0911", rset.getString("e01a1"));
				p09Vo.setString("p0913", rset.getString("a0101"));
			}

			dao.addValueObject(p09Vo);
			WorkPlanBo pb = new WorkPlanBo(conn, userView);
			String url = pb.getPhotoPath(nbase, a0100);
			String a0101 = rset.getString("a0101");
			mp.put("url", url);
			mp.put("a0101", a0101);
			mp.put("p0900", WorkPlanUtil.encryption(pid));
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mp;
	}

	/**
	 * @title 关注人list
	 * @param p0100
	 * @return
	 * @author szk 2014-7-25下午02:57:25
	 * @throws GeneralException
	 */
	public ArrayList searchPhotoList(String p0100) throws GeneralException {
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao
					.search("select p0900,p0913,nbase,a0100 from p09 where p0901=3 and p0905 = 3 and p0903='"
							+ p0100 + "'");
			WorkPlanBo pb = new WorkPlanBo(conn, userView);
			while (rs.next()) {
				HashMap mp = new HashMap();
				String url = pb.getPhotoPath(rs.getString("nbase"), rs
						.getString("a0100"));
				String a0101 = rs.getString("p0913");
				mp.put("url", url);
				mp.put("a0101", a0101);
				mp.put("p0900", WorkPlanUtil.encryption(rs.getString("p0900")));
				list.add(mp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneralException("数据查询出错！");
		} finally {
			WorkPlanUtil.closeDBResource(rs);
		}
		return list;
	}

	/**
	 * 删除一个关注人
	 * 
	 * @param p0900
	 * @author szk 2014-7-26下午04:22:46
	 * @throws GeneralException
	 */
	public void deletePhoto(String p0900) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.update("delete from p09 where p0900 = " + p0900);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneralException("关注人删除出错！");
		}
	}

	/**
	 * 查询右边成员列表
	 * 
	 * @param a0100
	 * @param month
	 * @param week
	 * @return
	 * @author szk 2014-7-30下午03:33:42
	 * @param num
	 * @param rownum
	 * @param querytype
	 *            当前是那个页面person个人 team团队 org部门 sub_org
	 * @throws GeneralException
	 */
	public ArrayList searchPersonMap(String a0100, int cycle, int year,
			int month, String week, int rownum, int num)
			throws GeneralException {
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		RowSet rset = null;

		String b0110 = this.userView.getUserOrgId();
		String e0122 = this.userView.getUserDeptId();// 当前人所在的部门
		WorkPlanUtil workPlanUtil = new WorkPlanUtil(conn, userView);

		// 所有的上级部门 P0707 in ('研发二部','产品研发部'，'研发中心');
		String deptId = disposeSqlStr(workPlanUtil.getParentDeptId(e0122));

		// 所有的上级单位
		String unitId = disposeSqlStr(workPlanUtil.getParentUnitId(b0110));

		try {
			StringBuffer str = new StringBuffer();
			StringBuffer strsql = new StringBuffer(); // 查询语句
			// StringBuffer strcount = new StringBuffer();//计数语句
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
			String strpres = "";
			if (login_vo != null) {
                strpres = login_vo.getString("str_value");
            }
			String[] arrpre = strpres.split(",");
			String belong_type = ""; // 总结类型 个人，部门
			String summaryDatesWhr = "";
			/****
			 * 先不区分个人 部门 if("org".equalsIgnoreCase(querytype) ||
			 * "sub_org".equalsIgnoreCase(querytype)) { belong_type
			 * =" belong_type = '2' "; }else if
			 * ("person".equalsIgnoreCase(querytype) ||
			 * "team".equalsIgnoreCase(querytype)) { belong_type
			 * =" belong_type != '2' "; }
			 *************/
			int toIndex = 0;
			summaryDatesWhr = getSummaryDatesWhr(String.valueOf(cycle), String
					.valueOf(year), String.valueOf(month), Integer
					.parseInt(week));

			strsql
					.append("select p0100,NBASE,p0900,E01A1,E0122,A0100,A0101,codeitemdesc,belong_type from");
			str
					.append(" (select p0100,NBASE,p0900,E01A1,E0122,A0100,A0101,codeitemdesc,belong_type from (");

			str
					.append("select b.p0100,b.NBASE,a.p0900,b.A0100,b.A0101,b.E01A1,b.E0122,b.belong_type");
			str.append(" from p09 a left join P01 b");
			str.append(" on a.P0903=b.P0100 ");
			str
					.append(" WHERE P0901=3 and P0905=3 and P0115 != '01' and a.A0100='"
							+ a0100 + "' ");
			str.append(" and " + summaryDatesWhr);
			str.append(" union");
			str
					.append(" select p0100,NBASE,0 as p0900,A0100,a0101,E01A1,E0122,belong_type");
			str.append(" from P01");
			str.append(" WHERE a0100!='" + a0100);
			str.append("' and (scope=3");
			str.append(" or (scope=1 and B0110 in (" + unitId + "))");
			str.append(" or (scope=2 and E0122 in (" + deptId + ")))");
			str.append(belong_type + " AND " + summaryDatesWhr);
			str.append(" union ");

			if (str.length() > 0) {
                str.setLength(str.length() - 7);
            }

			str
					.append(")t left join organization o2 on t.E0122 = o2.codeitemid) d");

			strsql.append(str);
			// strcount.append("select count(*) cnt from " + str);
			// int pagecount = 0;
			// rset = dao.search(strcount.toString());
			// if (rset.next()) {
			// pagecount = rset.getInt(1); //总条数
			// }

			// strsql.append(" where RowNo > " + rownum + "*(" + num + "-1)");

			rs = dao.search(strsql.toString());
			while (rs.next()) {
				// 要存的数据
				HashMap mp = new HashMap();
				WorkPlanBo pb = new WorkPlanBo(conn, userView);
				String p0900 = (String) rs.getString("p0900");
				String url = pb.getPhotoPath(rs.getString("nbase"), rs
						.getString("a0100"));
				String a0101 = rs.getString("a0101");
				String pos = rs.getString("codeitemdesc");
				String E0122 = rs.getString("E0122");
				/** 判断添加的关注人是否和范围有交集，重复的只显示范围的关注人 ，即不可删除的关注人 **/
				if (p0900 != null
						&& !"0".equals(p0900)
						&& IsInList(a0100, arrpre, summaryDatesWhr, rs
								.getString("a0100"), rs.getString("p0100"))) {
					continue;
				}
				mp.put("url", url);
				mp.put("a0101", a0101);
				mp.put("pos", pos);
				mp.put("belong_type", rs.getString("belong_type"));
				mp.put("E0122", WorkPlanUtil.encryption(E0122));
				mp.put("a0100", WorkPlanUtil.encryption(rs.getString("a0100")));
				mp.put("nbase", WorkPlanUtil.encryption(rs.getString("nbase")));
				mp.put("p0900", "0".equals((String) rs.getString("p0900")) ? rs
						.getString("p0900") : WorkPlanUtil.encryption(rs
						.getString("p0900")));
				list.add(mp);
			}
			int pagecount = list.size();
			if ((num - 1) * rownum - pagecount == 0
					|| (num - 1) * rownum > pagecount) {
				if (num > 1) {
                    num = num - 1;
                }
			}
			toIndex = num * rownum;
			if (toIndex > pagecount) {
                toIndex = pagecount;
            }
			HashMap mp1 = new HashMap();
			mp1.put("num", num + "");
			mp1.put("sql", str.toString());
			mp1.put("toIndex", toIndex + "");
			list.add(mp1);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneralException("数据查询出错或未设置认证人员库！");
		} finally {
			WorkPlanUtil.closeDBResource(rs);
			WorkPlanUtil.closeDBResource(rset);
		}

		return list;
	}

	/**
	 * 将以逗号拼接的id字符串处理成能够让sql语句中in识别的字符串
	 * 
	 * @param oldId
	 * @return
	 */
	private String disposeSqlStr(String oldId) {
		String[] ids = oldId.split(",");
		String newId = "";
		for (int i = 0; i < ids.length; i++) {
			newId = "'" + ids[i] + "'," + newId;
		}
		newId = newId.substring(0, newId.length() - 1);
		return newId;
	}

	/**
	 * 判断关注人是否已经包含在可见范围 szk
	 * 
	 * @param a0100
	 * @param nbase
	 * @param summaryDatesWhr
	 * @throws GeneralException
	 */
	private boolean IsInList(String a0100, String[] arrpre,
			String summaryDatesWhr, String nowa0100, String nowp0100)
			throws GeneralException {
		StringBuffer str = new StringBuffer();
		RowSet reRowSet = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			for (int i = 0; i < arrpre.length; i++) {
				String pre = arrpre[i];
				str.append(" select p0100,A0100,a0101,E01A1,E0122,belong_type");
				str.append(" from P01");
				str.append(" WHERE a0100!='" + a0100);
				str.append("' and (scope=3");
				str.append(" or (scope=1 and B0110=(select b0110 from " + pre
						+ "A01 where A0100='" + a0100 + "'))");
				str.append(" or (scope=2 and E0122=(select E0122 from " + pre
						+ "A01 where A0100='" + a0100 + "')) )");
				str.append(" AND " + summaryDatesWhr);
				str.append(" union ");
			}

			if (str.length() > 0) {
                str.setLength(str.length() - 7);
            }

			reRowSet = dao.search(str.toString());
			while (reRowSet.next()) {
				String ra0100 = (String) reRowSet.getString("a0100");
				String rp0100 = (String) reRowSet.getString("p0100");
				if (nowa0100.equals(ra0100) && nowp0100.equals(rp0100)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException("数据查询出错！");
		} finally {
			WorkPlanUtil.closeDBResource(reRowSet);
		}

		return false;

	}

	/**
	 * 返回正确的月，2014.8.1应为7月，6.30为7月
	 * 
	 * @param datetime
	 * @return isnow
	 * @author szk 2014-6-30上午11:04:06
	 * @param i
	 */
	public boolean getTrueDate(int year, int month, int i) {
		return getTrueDate(year, month, i, new Date());
	}
	/**
	 * 返回正确的月，2014.8.1应为7月，6.30为7月
	 * @param year
	 * @param month
	 * @param i
	 * @param date
	 * @return isnow
	 */
	public boolean getTrueDate(int year, int month, int i, Date date) {
        Date nowmonth_mon = this.getFirstMonday(year, month);
        Date befor_mon = DateUtils.addDays(nowmonth_mon, -7);
        return date.after(befor_mon)
                && (date.before(nowmonth_mon) || (DateUtils.FormatDate(date,
                        "yyyy-MM-dd").equals(
                        DateUtils.FormatDate(nowmonth_mon, "yyyy-MM-dd")) && i == 1));

    }

	// 获取年份
	public ArrayList getMinYear(String nbase, String a0100) {

		ArrayList yearList = new ArrayList();

		// 当前时间年份
		int nowYear = DateUtils.getYear(new Date());
		// 默认显示当前
		int year = nowYear;

		StringBuffer sql = new StringBuffer();
		sql.append("select MIN( " + Sql_switcher.year("p0114")
				+ ") as year from P01");
		sql
				.append(" where NBASE = '" + nbase + "' and A0100 = '" + a0100
						+ "'");

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());

			if (rs.next()) {
				String queryYear = rs.getString("year");
				if (!("".equals(queryYear) || queryYear == null)) {
					year = Integer.parseInt(queryYear) > (nowYear - 10) ? Integer
							.parseInt(queryYear)
							: (nowYear - 9);
				}
			}
			// 获取当前年份,如果最小年份和当前年份相同怎显示时往前推一年
			if (nowYear == year) {
				year = year - 1;
			}
			for (int i = year; i <= nowYear + 1; i++) {
				yearList.add(i + "");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			WorkPlanUtil.closeDBResource(rs);
		}

		return yearList;
	}

	/**
	 * 是否有团队成员
	 * 
	 * @return
	 * @author szk 2014-8-19上午11:45:11
	 */
	public String isHaveTeam() {
		WorkSummaryMethodBo methodBo = new WorkSummaryMethodBo(this.userView,
				this.conn);
		ContentDAO dao = new ContentDAO(this.conn);
		int count = 0;
		String teamsql = methodBo.getTeamPeopleSql(userView.getDbname(),
				userView.getA0100(), true);
		StringBuffer strsql = new StringBuffer();
		strsql.append("select count(*) cno from (");
		strsql.append(teamsql);
		strsql.append(") b");

		RowSet rset;
		try {
			rset = dao.search(strsql.toString());
			if (rset.next()) {
				count = rset.getInt(1); // 总条数
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * 是否有关注人
	 * 
	 * @return
	 * @author szk 2014-8-19下午01:25:42
	 * @param week
	 * @param month
	 * @param summaryYear
	 * @param summaryCycle
	 */
	public String isHavePerson(int summaryCycle, int summaryYear, int month,
			String week) {
		try {
			ArrayList list = searchPersonMap(this.userView.getA0100(),
					summaryCycle, summaryYear, month, week, 2, 1);
			if (list.size() - 1 == 0) {
				return "false";
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return "true";
	}

	/**
	 * 是否有下属部门
	 * 
	 * @author szk 2014-8-19下午01:31:59
	 */
	public String isHaveSubOrg() {
		WorkPlanBo pb = new WorkPlanBo(this.conn, this.userView);
		WorkPlanUtil wputil = new WorkPlanUtil(this.conn, this.userView);
		ArrayList e01a1list = new ArrayList();
		e01a1list = wputil.getMyE01a1List(userView.getDbname(), userView
				.getA0100());
		String info = pb.getMySubDeptList(e01a1list, 1);
		if ("".equals(info)) {
			return "false";
		}
		return "true";
	}

	/**
	 * 是否是部门负责人
	 * 
	 * @return
	 * @author szk 2014-8-19下午01:25:42
	 */
	public String isHaveOrg() {
		WorkPlanUtil wputil = new WorkPlanUtil(this.conn, this.userView);
		ArrayList e0122list = wputil.getDeptList(userView.getDbname(), userView
				.getA0100());
		if (e0122list.size() == 0) {
			return "false";
		}
		return "true";
	}

	/**
	 * @param nbase
	 * @param a0100
	 * @return
	 * @author szk 2014-9-3下午01:47:56
	 */
	public String getUserA0101(String nbase, String a0100) {
		StringBuffer strsql = new StringBuffer();
		strsql.append("select a0101 from " + nbase + "A01 where a0100 = '"
				+ a0100 + "'");

		RowSet rset;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rset = dao.search(strsql.toString());
			if (rset.next()) {
				return rset.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @Title: checkIsCanReadSummary
	 * @Description: 登陆人是否能查看此人的总结 如果有总结id即p0700大于0 其他参数可以不传
	 * @param @param belong_type 个人总结：1 部门总结：2
	 * @param @param objectid 人员编号
	 * @param @param p0700 总结id
	 * @param @return
	 * @return boolean
	 * @author:wangrd
	 * @throws
	 */
	public boolean checkIsCanReadSummary(int belong_type, String objectid,
			int p0700) {
		boolean b = false;
		try {

			if (p0700 > 0) {
				b = checkIsCanReadSummary(p0700);
			} else {
				if ("".equals(this.userView.getA0100())) {
                    return true;
                }
				// 是否是个人总结
				if (belong_type == 2) {
					b = (new WorkPlanUtil(this.conn, userView)
							.isMyDept(objectid) || new WorkPlanUtil(this.conn,
							userView).isMyTeamDept(objectid));
				} else {
					b = (objectid.equalsIgnoreCase(userView.getDbname()
							+ userView.getA0100()) || new WorkPlanUtil(
							this.conn, userView).isMyTeamPeople(objectid
							.substring(0, 3), objectid.substring(3)));
				}
				/*
				 * //是否我的团队 if (belong_type == 1) { // 个人 return new
				 * WorkPlanUtil(this.conn,
				 * userView).isMyTeamPeople(objectid.substring(0, 3),
				 * objectid.substring(3)); // 是不是我下属负责的计划 } else if (belong_type
				 * == 2) { // 团队 return new WorkPlanUtil(this.conn,
				 * userView).isMyTeamDept(objectid); // 是不是我下属负责的部门 }
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * @Title: checkIsCanReadSummary
	 * @Description: 登陆人是否能查看此总结
	 * @param @param p0100 总结id
	 * @param @return
	 * @return boolean
	 * @author:wangrd
	 * @throws
	 */
	public boolean checkIsCanReadSummary(int p0100) {
		boolean b = false;
		try {
			if (p0100 > 0) {
				String nbase = "";
				String a0100 = "";
				String objectid = "";
				ContentDAO dao = new ContentDAO(this.conn);
				WorkPlanUtil planUtil = new WorkPlanUtil(this.conn,
						this.userView);
				RecordVo p01vo = new RecordVo("p01");
				try {
					p01vo.setInt("p0100", p0100);
					p01vo = dao.findByPrimaryKey(p01vo);

				} catch (Exception e) {
					e.printStackTrace();
				}
				int belong_type = p01vo.getInt("belong_type");

				// 我自己的总结
				if (belong_type == 2) {
					objectid = p01vo.getString("e0122");
					b = new WorkPlanUtil(this.conn, userView)
							.isMyDept(objectid);
				} else {
					nbase = p01vo.getString("nbase");
					a0100 = p01vo.getString("a0100");
					objectid = nbase + a0100;
					b = (nbase + a0100).equalsIgnoreCase(userView.getDbname()
							+ userView.getA0100());
				}
				if (b) {
					return b;
				}
				if ("".equals(this.userView.getA0100())) {// 业务用户
					String e01a1 = "";
					if (belong_type == 2) {
						e01a1 = new WorkPlanUtil(this.conn, userView)
								.getDeptLeaderE01a1(objectid);
					} else {
						e01a1 = p01vo.getString("e01a1");
					}
					return new WorkPlanUtil(this.conn, userView)
							.isMyManageE01a1(e01a1);
				}

				int rowCount = 0;
				String strsql = "";
				String b0110 = this.userView.getUserOrgId();
				String e0122 = this.userView.getUserDeptId();
				if (belong_type == 2) {// 部门
					// 是否是我的部门
					if (planUtil.isMyTeamDept(objectid)) {
						return true;
					}
				} else {
					// 是否是我的下级
					String e01a1 = p01vo.getString("e01a1");
					String myE01a1s = planUtil.getMyE01a1s(this.userView
							.getDbname(), this.userView.getA0100());
					if (planUtil.isMySubE01a1(myE01a1s, e01a1)) {
						return true;
					}
				}

				// 我关注的
				strsql = "select count(*) cnt from p09"
						+ " where P0901=3 and P0905=3 " + " and p0903=? "
						+ " and Nbase='" + this.userView.getDbname() + "'"
						+ " and a0100='" + this.userView.getA0100() + "'";

				ArrayList paramList = new ArrayList();
				paramList.add(Integer.valueOf(p0100));
				try {
					RowSet rset = dao.search(strsql, paramList);
					if (rset.next()) {
						rowCount = rset.getInt(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (rowCount > 0) {
					return true;
				}

				// 可见范围内的
				strsql = "select count(*) cnt from  p01 " + " where  (scope=3 "
						+ " or (scope=1 and  b0110 like '" + b0110 + "%') "
						+ " or (scope=2 and  e0122 ='" + e0122 + "')" + ")";

				if (belong_type == 2) {// 部门
					strsql = strsql + " and e0122='" + objectid + "'"
							+ " and belong_type=2";
				} else {
					strsql = strsql + " and Nbase='" + nbase + "'"
							+ " and a0100='" + a0100 + "'"
							+ " and (belong_type is null or belong_type=0)";
				}
				strsql = strsql + " and p0100=?";
				paramList.clear();
				paramList.add(Integer.valueOf(p0100));
				try {
					RowSet rset = dao.search(strsql, paramList);
					if (rset.next()) {
						rowCount = rset.getInt(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (rowCount > 0) {
					return true;
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * 通过下属取上属
	 * 
	 * @param nbase
	 * @param a0100
	 * @return
	 * @author szk 2014-10-14下午04:27:32
	 */
	public String getSuper(String nbase, String a0100) {
		String usrA0100 = "";
		String ps_superior = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RecordVo ps_superior_vo = ConstantParamter.getRealConstantVo(
					"PS_SUPERIOR", this.conn);
			if (ps_superior_vo != null) {
				ps_superior = ps_superior_vo.getString("str_value");
			}
			String strsql = "select A0100 from "
					+ nbase
					+ "A01 where E01A1 =(select k."
					+ ps_superior
					+ " from "
					+ nbase
					+ "A01 u right join K01 k on u.E01A1 = k.E01A1 where A0100='"
					+ a0100 + "')";
			RowSet rset = dao.search(strsql);
			while (rset.next()) {
				usrA0100 = nbase + rset.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usrA0100;
	}

	public boolean isMyDirectSubTeamPeople(String nbase, String a0100,
			String belong_type, String p0100) {
		boolean b = false;
		WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.conn, this.userView);
		String myE01a1s = workPlanUtil.getMyE01a1s(this.userView.getDbname(),
				this.userView.getA0100());
		String e01a1 = "";
		if ("0".equals(belong_type) || "1".equals(belong_type)) {// 个人计划
			RowSet rset = null;
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select * from p01 where p0100='" + p0100 + "'";
			try {
				rset = dao.search(sql);
				if (rset.next()) {
					e01a1 = rset.getString("e01a1");
					if (e01a1 == null || "".equals(e01a1)) {
						e01a1 = workPlanUtil.getPersonVo(nbase, a0100)
								.getString("e01a1");
					}
				} else {
					RecordVo a01vo = workPlanUtil.getPersonVo(nbase, a0100);
					e01a1 = a01vo.getString("e01a1");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			// e01a1 = workPlanUtil.getDeptLeaderE01a1(nbase+a0100);
			RecordVo a01vo = workPlanUtil.getPersonVo(nbase, a0100);
			e01a1 = a01vo.getString("e01a1");
		}
		String superE01a1 = workPlanUtil.getApprovedSuperE01a1(e01a1);
		if ("".equals(superE01a1)) {
			return false;
		}
		if (("," + myE01a1s + ",").indexOf("," + superE01a1 + ",") > -1) {
			b = true;
		}

		return b;
	}

	public String getSummaryMsgEmail_BodyText(String targetA0101,
			String content, String summary_title) {
		String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
		StringBuffer bodytext = new StringBuffer();
		bodytext.setLength(0);
		bodytext.append(targetA0101).append(", 您好！").append("<br />");
		bodytext.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		bodytext.append("    ");
		bodytext.append(this.userView.getUserFullName());
		bodytext.append("在您的");

		bodytext.append(summary_title);
		bodytext.append("下发布了消息:");
		bodytext.append("<br />");
		bodytext.append("<br />");
		bodytext.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		bodytext.append("    ");

		if(StringUtils.isNotBlank(content)){
			content = content.replaceAll("\r\n|\n", "<br/>&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		bodytext.append(content);

		bodytext.append("<br />");
		bodytext.append("<br />");
		bodytext.append("<br />");
		bodytext.append(cur_date);
		return bodytext.toString();
	}

	public String getSummaryMsg1Email_BodyText(String targetA0101,
			String summaryOwnerName, String content, String summary_title) {
		String cur_date = PubFunc.getStringDate("yyyy年MM月dd日");
		StringBuffer bodytext = new StringBuffer();
		bodytext.setLength(0);
		bodytext.append(targetA0101).append(", 您好！").append("<br />");
		bodytext.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		bodytext.append("    ");
		bodytext.append(this.userView.getUserFullName());
		bodytext.append("在");
		bodytext.append(summaryOwnerName);
		bodytext.append("的");

		bodytext.append(summary_title);
		bodytext.append("下发布了消息:");
		bodytext.append("<br />");
		bodytext.append("<br />");
		bodytext.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		bodytext.append("    ");
		if(StringUtils.isNotBlank(content)){
			content = content.replaceAll("\r\n|\n", "<br/>&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		bodytext.append(content);

		bodytext.append("<br />");
		bodytext.append("<br />");
		bodytext.append("<br />");
		bodytext.append(cur_date);
		return bodytext.toString();
	}

	public String getSummaryHref(String nbase, String a0100, String type,
			String olda0100, String e0122s, String summaryCycle,
			String summaryYear, String summaryMonth, String summaryWeek) {
		// 获取etoken的值
		AttestationUtils attestUtils = new AttestationUtils();
		String etoken = attestUtils.getetoken(nbase, a0100, this.conn);
		String[] aMan = splitNbaseA0100(olda0100);
		StringBuffer url = new StringBuffer();
		url.append(this.userView.getServerurl());
		url.append("/workplan/work_summary.do?b_query=link&type=" + type);
		String belong_type = "0";
		if ("org".equals(type)) {
			belong_type = "2";
		}
		url.append("&belong_type=" + belong_type);
		url.append("&cycle=" + summaryCycle);
		url.append("&year=" + summaryYear);
		url.append("&month=" + summaryMonth);
		url.append("&week=" + summaryWeek);
		url.append("&e0122=" + e0122s);
		url.append("&isemail=true");
		url.append("&nbase=" + WorkPlanUtil.encryption(aMan[0]));
		url.append("&a0100=" + WorkPlanUtil.encryption(aMan[1]));
		url.append("&appfwd=1&etoken=" + etoken);
		return url.toString();
	}

	private String[] splitNbaseA0100(String nbaseA0100) {
		String nbase = nbaseA0100.substring(0, 3);
		String a0100 = nbaseA0100.substring(3);
		String[] aMan = { nbase, a0100 };
		return aMan;
	}
	public static void main(String[] args) {
        
    }
}
