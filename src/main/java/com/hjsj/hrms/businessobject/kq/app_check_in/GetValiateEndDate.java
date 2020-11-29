package com.hjsj.hrms.businessobject.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.module.kq.application.KqOverTimeForLeaveBo;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class GetValiateEndDate
{

    private Connection conn;
    private UserView userView;

    public GetValiateEndDate(UserView userView, Connection conn)
    {
        this.userView = userView;
        this.conn = conn;
    }

    /**
     * 计算结束时间
     * 
     * @param startDate
     *            开始日期
     * @param d_count
     *            天数
     * @param timeSet
     *            班次的开始时间和结束时间
     * @return Date 返回结束日期时间
     */
    public Date getEndTimetoQ11(Date startDate, Date endDate, String count, boolean colDay_flag, String dayOrHour_flag, String class_id)
    {
        if ("0".equals(dayOrHour_flag))
        {
            int d_count = Integer.parseInt(count);
            // 天数处理
            if (d_count >= 1)
            {
                d_count--;
            } else if (d_count < 1)
            {
                d_count = 0;
            }
            /**
             * 判断结束时间和开始时间的大小 开始时间大 日期 加 1天
             */
            if (colDay_flag)
            {
                endDate = OperateDate.addDay(endDate, d_count + 1);
            } else
            {
                endDate = OperateDate.addDay(endDate, d_count);
            }
        } else if ("1".equals(dayOrHour_flag))
        {
            float h_count = Float.parseFloat(count);//小时数
            if (startDate.after(endDate) || "#".equals(class_id))
            {
            	Calendar c = Calendar.getInstance();
            	c.setTime(startDate);
            	c.add(Calendar.SECOND, (int) (h_count*60*60));
            	endDate = c.getTime();
            } else
            {
                endDate = startDayWorkTime(startDate, (int)h_count, class_id);
            }
        }
        return endDate;
    }

    public Date startDayWorkTime(Date date, int count, String class_id)
    {

        StringBuffer sql = new StringBuffer();
        sql.append("select * from kq_class where class_id='" + class_id + "'");
        ContentDAO dao = new ContentDAO(this.conn);
        Date nowDate = OperateDate.getDateByFormat(date, "HH:mm");
        Date endDate = OperateDate.getDateByFormat(date, "yyyy-MM-dd");
        ArrayList onD = new ArrayList();
        ArrayList offD = new ArrayList();
        long workTime = 0;
        long workLong = count * 60 * 60 * 1000;
        long leavingsTime = 0;
        int day_count = 0;
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            if (re.next())
            {
                // 赋值
                for (int i = 1; i <= 3; i++)
                {
                    if (re.getString("offduty_" + i).length() > 1 && re.getString("onduty_" + i).length() > 1)
                    {
                        onD.add(OperateDate.strToDate(re.getString("onduty_" + i), "HH:mm"));
                        offD.add(OperateDate.strToDate(re.getString("offduty_" + i), "HH:mm"));
                    } else
                    {
                        break;
                    }
                }

            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        // 判断是否跨天 跨天加1

        for (int i = 0; i < onD.size(); i++)
        {
            if (((Date) onD.get(i)).after((Date) offD.get(i)))
            {
                offD.set(i, OperateDate.addDay((Date) offD.get(i), 1));
                for (int j = i + 1; j < onD.size(); j++)
                {
                    offD.set(j, OperateDate.addDay((Date) offD.get(j), 1));
                    onD.set(j, OperateDate.addDay((Date) onD.get(j), 1));
                }
            }
            
            if (i != 0 && ((Date) offD.get(i - 1)).after((Date) onD.get(i)))
            {
                onD.set(i, OperateDate.addDay((Date) onD.get(i), 1));
                offD.set(i, OperateDate.addDay((Date) offD.get(i), 1));
                for (int j = i + 1; j < onD.size(); j++)
                {
                    offD.set(j, OperateDate.addDay((Date) offD.get(j), 1));
                    onD.set(j, OperateDate.addDay((Date) onD.get(j), 1));
                }

            }
        }
        // 申请加班第一天
        for (int i = 0; i < onD.size(); i++)
        {
            if (((Date) offD.get(i)).after(nowDate))
            {
                leavingsTime = workLong - ((Date) offD.get(i)).getTime() + nowDate.getTime();
                if (leavingsTime <= 0)
                {
                    return new Date(endDate.getTime() + workLong + nowDate.getTime() - OperateDate.strToDate("00:00", "HH:mm").getTime());
                } else
                {
                    workLong = leavingsTime;
                    if (i < onD.size() - 1)
                    {
                        nowDate = (Date) onD.get(i + 1);
                    }
                }
            }
        }
        // 计算所选班次的工时
        for (int i = 0; i < onD.size(); i++)
        {
            workTime = workTime + ((Date) onD.get(i)).getTime() - ((Date) offD.get(i)).getTime();
        }
        // 计算天数
        day_count = 0;
        if(workTime!=0){
            day_count = (int) (workLong / workTime);
        }
        workLong = 0;
        if(workTime!=0){
            workLong = workLong % workTime;
        }
        endDate = OperateDate.addDay(endDate, day_count + 1);
        nowDate = (Date) onD.get(0);
        // 申请工作的最后一天
        for (int i = 0; i < onD.size(); i++)
        {
            if (((Date) offD.get(i)).after(nowDate))
            {
                leavingsTime = workLong - ((Date) offD.get(i)).getTime() + nowDate.getTime();
                if (leavingsTime <= 0)
                {
                    return new Date(endDate.getTime() + workLong + nowDate.getTime() - OperateDate.strToDate("00:00", "HH:mm").getTime());
                } else
                {
                    workLong = leavingsTime;
                    if (i < onD.size() - 1)
                    {
                        nowDate = (Date) onD.get(i + 1);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获得公出日的最后日期
     * 
     * @param startDate
     *            开始时间
     * @param count
     *            小时或天数
     * @param dayOrHour_flag
     *            按照小时或天数的标记
   * @param class_id
     *            公出类型
     * @param nbase
     *            在库人员
     * @param A0100
     *            人员编号
     * @return
     * @throws GeneralException
     */
    public Date getEndTimeToQ13(Date startDate, String count, String dayOrHour_flag, String class_id, String nbase, String A0100) throws GeneralException
    {
        if (A0100 == null || A0100.length() < 1 || nbase == null || nbase.length() < 1) {
            return null;
        }
        
        Date endDate = null;
        AnnualApply ann = new AnnualApply(this.userView, this.conn);
        if ("0".equals(dayOrHour_flag))
        {
            int d_count = Integer.parseInt(count);
            // 天数处理
            if (d_count >= 1)
            {
                d_count--;
            } else if (d_count < 1)
            {
                d_count = 0;
            }
            endDate = OperateDate.addDay(startDate, d_count);
        } 
        else 
        {
            float h_count = Float.parseFloat(count);//小时数
        	Calendar c = Calendar.getInstance();
        	c.setTime(startDate);
        	c.add(Calendar.SECOND, (int) (h_count*60*60));
        	endDate = c.getTime();
        }
        
        HashMap kqItem_hash = ann.count_Leave(class_id); //判断该申请类型是否包好公休，节日
        ArrayList list = null;// 申请的日期
        ArrayList reDay = null;
        if ("1".equals(kqItem_hash.get("has_rest")) && "1".equals(kqItem_hash.get("has_feast")))
        {
        } 
        else if ("1".equals(kqItem_hash.get("has_rest")))
        {
            int num = 1;
            while (num > 0)
            {
                list = OperateDate.getDayByDate(startDate, endDate);
                reDay = getWeekDateArea(list, nbase, A0100);
                num = 0;
                for (int i = 0; i < reDay.size(); i++)
                {
                    if (this.isFeast((Date) reDay.get(i)))
                    {
                        num++;
                    }
                }
                startDate = OperateDate.addDay(endDate, 1);
                endDate = OperateDate.addDay(endDate, num);
            }
        } 
        else if ("1".equals(kqItem_hash.get("has_feast")))
        {
            int num = 1;
            while (num > 0)
            {
                list = OperateDate.getDayByDate(startDate, endDate);
                reDay = getWeekDateArea(list, nbase, A0100);
                num = 0;
                for (int i = 0; i < reDay.size(); i++)
                {
                    if (!this.isFeast((Date) reDay.get(i)))
                    {
                        num++;
                    }
                }
                startDate = OperateDate.addDay(endDate, 1);
                endDate = OperateDate.addDay(endDate, num);
            }
        } 
        else
        {
            int num = 1;
            while (num > 0)
            {
                list = OperateDate.getDayByDate(startDate, endDate);
                reDay = getWeekDateArea(list, nbase, A0100);
                num = reDay.size();
                startDate = OperateDate.addDay(endDate, 1);
                endDate = OperateDate.addDay(endDate, num);
            }
        }
        
        if ("0".equals(dayOrHour_flag))
        {
            Map timeMap = getTimeByDate(nbase, A0100, endDate);
            if (!(timeMap == null) && !"".equals(timeMap) && !timeMap.isEmpty())
            {
                if (((Boolean) timeMap.get("conday")).booleanValue())
                {
                    endDate = OperateDate.addDay(endDate, 1);
                }
                endDate = OperateDate.strToDate(OperateDate.dateToStr(endDate, "yyyy-MM-dd") + " " + (String) timeMap.get("endTime"), "yyyy-MM-dd HH:mm");
            } 
            else
            {
                endDate = OperateDate.strToDate(OperateDate.dateToStr(endDate, "yyyy-MM-dd") + " 23:59", "yyyy-MM-dd HH:mm");
            }
        }
        return endDate;
    }


    /**
     * 获得请假的最后日期
     * 
     * @param startDate
     *            开始时间
     * @param count
     *            小时或天数
     * @param dayOrHour_flag
     *            按照小时或天数的标记
     * @param class_id
     *            请假类型
     * @param nbase
     *            在库人员
     * @param A0100
     *            人员编号
     * @return
     * @throws GeneralException
     */
    public Date getEndTimeToQ15(Date startDate, String count, String dayOrHour_flag, String class_id, String nbase, String A0100) throws GeneralException
    {
        if (A0100 == null || A0100.length() < 1 || nbase == null || nbase.length() < 1) {
            return null;
        }
        
        Date endDate = null;
        AnnualApply ann = new AnnualApply(this.userView, this.conn);
        if ("0".equals(dayOrHour_flag))
        {
            int d_count = Integer.parseInt(count);
            // 天数处理
            if (d_count >= 1)
            {
                d_count--;
            } else if (d_count < 1)
            {
                d_count = 0;
            }
            endDate = OperateDate.addDay(startDate, d_count);
        } 
        else if ("1".equals(dayOrHour_flag))
        {
            float h_count = Float.parseFloat(count);//小时数
        	Calendar c = Calendar.getInstance();
        	c.setTime(startDate);
        	c.add(Calendar.SECOND, (int) (h_count*60*60));
        	endDate = c.getTime();
        }
        
        HashMap kqItem_hash = ann.count_Leave(class_id);
        ArrayList list = null;// 申请的日期
        ArrayList reDay = null;
        if ("1".equals(kqItem_hash.get("has_rest")) && "1".equals(kqItem_hash.get("has_feast")))
        {
        } 
        else if ("1".equals(kqItem_hash.get("has_rest")))
        {
            int num = 1;
            while (num > 0)
            {
                list = OperateDate.getDayByDate(startDate, endDate);
                reDay = getWeekDateArea(list, nbase, A0100);
                num = 0;
                for (int i = 0; i < reDay.size(); i++)
                {
                    if (this.isFeast((Date) reDay.get(i)))
                    {
                        num++;
                    }
                }
                startDate = OperateDate.addDay(endDate, 1);
                endDate = OperateDate.addDay(endDate, num);
            }
        } 
        else if ("1".equals(kqItem_hash.get("has_feast")))
        {
            int num = 1;
            while (num > 0)
            {
                list = OperateDate.getDayByDate(startDate, endDate);
                reDay = getWeekDateArea(list, nbase, A0100);
                num = 0;
                for (int i = 0; i < reDay.size(); i++)
                {
                    if (!this.isFeast((Date) reDay.get(i)))
                    {
                        num++;
                    }
                }
                startDate = OperateDate.addDay(endDate, 1);
                endDate = OperateDate.addDay(endDate, num);
            }
        } 
        else
        {
            int num = 1;
            while (num > 0)
            {
                list = OperateDate.getDayByDate(startDate, endDate);
                reDay = getWeekDateArea(list, nbase, A0100);
                num = reDay.size();
                startDate = OperateDate.addDay(endDate, 1);
                endDate = OperateDate.addDay(endDate, num);
            }
        }
        
        if ("0".equals(dayOrHour_flag))
        {
            Map timeMap = getTimeByDate(nbase, A0100, endDate);
            if (!(timeMap == null) && !"".equals(timeMap) && !timeMap.isEmpty())
            {
                if (((Boolean) timeMap.get("conday")).booleanValue())
                {
                    endDate = OperateDate.addDay(endDate, 1);
                }
                endDate = OperateDate.strToDate(OperateDate.dateToStr(endDate, "yyyy-MM-dd") + " " + (String) timeMap.get("endTime"), "yyyy-MM-dd HH:mm");
            } 
            else
            {
                endDate = OperateDate.strToDate(OperateDate.dateToStr(endDate, "yyyy-MM-dd") + " 23:59", "yyyy-MM-dd HH:mm");
            }
        }
        return endDate;
    }

    /**
     * 判断 这个时间是否在已经排班
     * 
     * @param date
     * @param nbase
     * @param A0100
     * @return
     */
    public boolean isArrangedWeek(Date date, String nbase, String A0100)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("select 1 from kq_employ_shift where exists(select * from kq_class where kq_class.class_id=kq_employ_shift.class_id) and q03z0 = '" + OperateDate.dateToStr(date, "yyyy.MM.dd") + "' and nbase='" + nbase + "' and a0100='" + A0100 + "'");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            if (!re.next()) {
                return false;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        } 
        finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        return true;
    }

    /**
     * 判断时间段是否在公休日内
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    public boolean isRestOfWeekDay(Date startDate, Date endDate, String nbase, String A0100)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("select 1 from kq_employ_shift");
        sql.append(" where class_id != '0' and Q03Z0 in (");
        if("00:00".equalsIgnoreCase(OperateDate.dateToStr(endDate, "HH:mm"))){
            endDate = OperateDate.addDay(endDate, -1);
        }
        ArrayList list = OperateDate.getDayByDate(startDate, endDate);
        for (int i = 0; i < list.size(); i++)
        {
            sql.append("'" + getStrD(list, i) + "',");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") and nbase='" + nbase + "'");
        sql.append(" and A0100='" + A0100 + "'");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            if (re.next())
            {
                return false;
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        } 
        finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        return true;
    }

    /**
     * 判断这个时间段是否是节假日
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    public boolean isFeastDay(Date startDate, Date endDate)
    {
    	if("00:00".equalsIgnoreCase(OperateDate.dateToStr(endDate, "HH:mm"))){
            endDate = OperateDate.addDay(endDate, -1);
        }
        ArrayList list = OperateDate.getDayByDate(startDate, endDate);
        for (int i = 0; i < list.size(); i++)
        {
            if (!isFeast((Date) list.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断这个日期是否是节假日
     * szk
     * @param date
     * @return
     */
    public boolean isFeast(Date date)
    {
         String longDate = OperateDate.dateToStr(date, "yyyy.MM.dd");
         return !"".equals(IfRestDate.if_Feast(longDate, this.conn));        
    }

    public boolean isFeastofWork(String table, String nbase, String A0100, Date startDate, Date endDate, String id)
    {
        Set setDate = getFeastDate(startDate);
        if(setDate == null || setDate.size() <= 0) {
            return false;
        }
        StringBuffer sql = new StringBuffer();
        Iterator it = setDate.iterator();
        sql.append("select * from " + table);
        sql.append(" where " + Sql_switcher.dateToChar(table + "Z1") + " in (");
        while (it.hasNext())
        {
            Date feastDate = (Date) it.next();
            sql.append("'" + OperateDate.dateToStr(feastDate, "yyyy-MM-dd") + "',");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") and nbase='" + nbase + "'");
        sql.append(" and A0100='" + A0100 + "'");
        sql.append(" and " + table + "03='" + id + "'");
        sql.append("and " + table + "z5!='10'");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            if (re.next())
            {
                return true;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        return false;
    }

    /**
     * 通过这个时间点 获得 在这个时间点上的所有节日日期
     * 
     * @param date
     * @return
     */
    public Set getFeastDate(Date date)
    {
        String shortDate = OperateDate.dateToStr(date, "MM.dd");
        String longDate = OperateDate.dateToStr(date, "yyyy.MM.dd");
        Set set = new HashSet();
        
        //zxj 20140916 原sql条件仍有遗漏的情况（末尾没有逗号的feastdates无法匹配），现已改进
        StringBuffer sql = new StringBuffer();      
        sql.append("SELECT feast_name,feast_dates from kq_feast");
        sql.append(" where ','" + Sql_switcher.concat() + "cast(feast_dates as varchar(1000))" + Sql_switcher.concat() + "','");
        sql.append(" like '%," + shortDate + ",%'");
        sql.append(" or ','" + Sql_switcher.concat() + "cast(feast_dates as varchar(1000))" + Sql_switcher.concat() + "','");
        sql.append(" like '%" + longDate + "%'");
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            if (re.next())
            {
                String tS[] = re.getString("feast_dates").split(",");
                for (int i = 0; i < tS.length; i++)
                {
	                if (tS[i].length() == "MM.dd".length())
	                {
	                    set.add(OperateDate.strToDate(OperateDate.dateToStr(date, "yyyy") + "." + tS[i], "yyyy.MM.dd"));
	                 
	                } else
	                {
	                    set.add(OperateDate.strToDate(tS[i], "yyyy.MM.dd"));
	                }
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        return set;
    }
    /**
     * 判断公休日是否有申请
     * @param table
     * @param nbase
     * @param A0100
     * @param startDate
     * @param endDate
     * @param id
     * @return
     */
    public boolean isRestofWork(String table, String nbase, String A0100, Date startDate, Date endDate, String id)
    {
        ArrayList setDate = OperateDate.getDayByDate(startDate, endDate);
        StringBuffer sql = new StringBuffer();
        Iterator it = setDate.iterator();
        sql.append("select * from " + table);
        sql.append(" where " + Sql_switcher.dateToChar(table + "Z1") + " in (");
        while (it.hasNext())
        {
            Date feastDate = (Date) it.next();
            sql.append("'" + OperateDate.dateToStr(feastDate, "yyyy-MM-dd") + "',");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") and nbase='" + nbase + "'");
        sql.append(" and A0100='" + A0100 + "'");
        sql.append(" and " + table + "03='" + id + "'");
        sql.append("and " + table + "z5!='10'");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            if (re.next())
            {
                return true;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        return false;
    }
    /**
     * 组装用户信息字符
     * 
     * @param infoStr
     * @return
     */
    public ArrayList userInfo(String infoStr)
    {
        ArrayList infoList = new ArrayList();
        String s1[] = infoStr.split("'");
        for (int i = 0; i < s1.length; i++)
        {
            String s2[] = s1[i].split(",");
            Map infoMap = new HashMap();
            for (int j = 0; j < s2.length; j++)
            {
                String s3[] = s2[j].split("=");
                if (s3.length == 1)
                {
                    infoMap.put(s3[0].trim(), "");
                } else
                {
                    infoMap.put(s3[0].trim(), s3[1].trim());
                }
            }
            infoList.add(infoMap);
        }
        return infoList;
    }

    /**
     * 判断申请的时间是否在已经安排的工作日之内
     * 
     * @param nbase
     *            所在库（在职库，等）
     * @param A0100
     *            人员id
     * @param startDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param class_id
     *            选择的班次id
     * @return 如果返回的是true 则 代表在工作日之内
     */
    public boolean isWeekDay(String nbase, String A0100, Date startDate, Date endDate, String class_id, boolean coDay_flag)
    {
        startDate = OperateDate.addDay(startDate, -1);
        KqUtilsClass kqUtils = new KqUtilsClass(this.conn);
        Map classMap = kqUtils.getClassTimeMap();
        String strXClassId = "";
        strXClassId = getXClassId(class_id);

        ArrayList list = OperateDate.getDayByDate(startDate, endDate);
        Map class1 = new HashMap();
        // -------------------------------组装sql语句开始------------------------------------
        StringBuffer sql = new StringBuffer();
        sql.append("select Q03Z0, class_id from kq_employ_shift ");
        sql.append("where nbase='" + nbase + "' ");
        sql.append("and A0100='" + A0100 + "' ");
        sql.append("and (Q03Z0='" + OperateDate.dateToStr((Date) list.get(0), "yyyy.MM.dd") + "' ");
        for (int i = 1; i < list.size(); i++)
        {
            String date = OperateDate.dateToStr((Date) list.get(i), "yyyy.MM.dd");
            sql.append("or Q03Z0='" + date + "' ");
        }
        sql.append(")");
        // -------------------------------组装sql语句结束-------------------------------------
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            while (re.next())
            {
                if (strXClassId.indexOf("(" + re.getString("class_id") + ")") != -1)
                {
                    class1.put(re.getString("Q03Z0"), re.getString("class_id"));
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        
        if (class1.isEmpty())
        {
            return false;
        }
        
        if (class1.get(getStrD(list, 0)) != null)
        {// 申请加班日期的前一天
            String tTime[] = ((String) classMap.get(class1.get(getStrD(list, 0)))).split("~");
            if (!(tTime[1].charAt(0) == '0'))
            {
                if (isBigToTime(tTime[1].substring(1), OperateDate.dateToStr(startDate, "HH:mm")))
                {
                    return true;
                }
            }
            class1.remove(getStrD(list, 0));
        }
        
        if (class1.get(getStrD(list, list.size() - 1)) != null)
        {// 申请加班的最后一天
            String tTime[] = ((String) classMap.get(class1.get(getStrD(list, list.size() - 1)))).split("~");
            // String uTime[] = null;
            // if (!class_id.equals("#")) {
            // uTime = ((String) classMap.get(class_id)).split("~");
            // }
            if (coDay_flag)
            {
                if (isBigToTime(OperateDate.dateToStr(endDate, "HH:mm"), tTime[0]))
                {
                    return true;
                }
            } else
            {
                if (!(tTime[1].charAt(0) == '0'))
                {
                    if (isBigToTime(OperateDate.dateToStr(endDate, "HH:mm"), tTime[0]))
                    {
                        return true;
                    }
                } else
                {
                    if (isBigToTime(tTime[1].substring(1), OperateDate.dateToStr(startDate, "HH:mm")) && isBigToTime(OperateDate.dateToStr(endDate, "HH:mm"), tTime[0]))
                    {
                        return true;
                    }
                }
            }
            class1.remove(getStrD(list, list.size() - 1));
        }
        
        if (class1.get(getStrD(list, 1)) != null)
        {// 申请加班日期第一天
            String tTime[] = ((String) classMap.get(class1.get(getStrD(list, 1)))).split("~");
            if (coDay_flag && isBigToTime(OperateDate.dateToStr(startDate, "HH:mm"), tTime[1].substring(1)))
            {
                class1.remove(getStrD(list, 1));
            }
        }
        
        if (!class1.isEmpty())
        {
            return true;
        }
        return false;
    }

    /**
     * 参数是两个时间型字符串
     * 
     * @param startTime
     *            样式为"HH:mm"
     * @param endTime
     *            样式为"HH:mm"
     * @return 当第一个参数大返回 true 反之返回 false
     */
    public static boolean isBigToTime(String startTime, String endTime)
    {
        Date startD = null;
        Date endD = null;
        startD = OperateDate.strToDate(startTime, "HH:mm");
        endD = OperateDate.strToDate(endTime, "HH:mm");
        if (startD.getTime() <= endD.getTime())
        {
            return false;
        }
        return true;
    }

    /**
     * 得到一个和你选择班次 有时间有交集的字符串 样式=(班次id1)(班次id2)
     * 
     * @param id
     *            Class_id 班次id
     * @return
     */
    public String getXClassId(String id)
    {
        // 实例化类KqUtilsClass
        KqUtilsClass kqUtils = new KqUtilsClass(this.conn);
        // 调用getClassTimeMap()方法得到Map（key=class_id,value=开始时间~结束时间）
        Map classMap = kqUtils.getClassTimeMap();
        String reStr = "";
        Date useStaD = null;
        Date useEndD = null;
        if (!"#".equals(id) && !"".equals(id) && !id.equals(null))
        {
            // 得到应用的班次时间段
            String userTime[] = ((String) classMap.get(id)).split("~");
            useStaD = OperateDate.strToDate(userTime[0], "HH:mm");
            useEndD = OperateDate.strToDate(userTime[1].substring(1), "HH:mm");
            if (!(userTime[1].charAt(0) == '0'))
            {
                OperateDate.addDay(useEndD, Integer.parseInt("" + userTime[1].charAt(0)));
            }
        }
        // if (isConDay_Work(userTime[0], userTime[1])) {
        // useEndD = OperateDate.addDay(useEndD, 1);
        // }
        Set idSet = classMap.keySet();
        Iterator it = idSet.iterator();
        if (!"#".equals(id) && !"".equals(id) && !id.equals(null))
        {
            while (it.hasNext())
            {
                String classId = (String) it.next();

                String classTime[] = ((String) classMap.get(classId)).split("~");
                if (classTime[0] == null && classTime[1] == null) {
                    continue;
                }
                Date startD = OperateDate.strToDate(classTime[0], "HH:mm");
                Date endD = OperateDate.strToDate(classTime[1].substring(1), "HH:mm");
                // isConDay_Work方法 第一个参数大 返回true
                if (!(classTime[1].charAt(0) == '0'))
                {
                    endD = OperateDate.addDay(endD, Integer.parseInt("" + classTime[1].charAt(0)));
                }
                if (useEndD.getTime() > startD.getTime() && endD.getTime() > useStaD.getTime())
                {
                    reStr += "(" + classId + ")";
                }

            }
        } else
        {
            while (it.hasNext())
            {
                String classId = (String) it.next();
                reStr += "(" + classId + ")";
            }
        }
        return reStr;
    }

    /**
     * 得到"yyyy.MM.dd"样式的日期字符串
     * 
     * @param list
     *            日期List
     * @param i
     * @return
     */
    public String getStrD(ArrayList list, int i)
    {
        return OperateDate.dateToStr((Date) list.get(i), "yyyy.MM.dd");
    }

    /**
     * 
     * @param list
     * @return
     */
    public ArrayList getWeekDateArea(ArrayList list, String nbase, String A0100)
    {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList wl = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("Select DISTINCT Q03Z0 from kq_employ_shift where nbase='" + nbase + "' and A0100='" + A0100 + "' and class_id='0' and Q03Z0 in(");
        for (int i = 0; i < list.size(); i++)
        {
            sql.append("'" + getStrD(list, i) + "',");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            while (re.next())
            {
                wl.add(OperateDate.strToDate(re.getString("Q03Z0"), "yyyy.MM.dd"));
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        return wl;
    }


    /**
     * 检查请假申请提前（延后）天数是否符合要求
     * @return <String> 不符合要求返回相应提示信息
     * @author zhaoxj 2012-7-13
     * 
     */
    public String leaveTimeApp(Date startDate, Date endDate, String nbase, String A0100, String b0110)
    {
        String retMsg = "";
        
        String leavetime_rule = KqParam.getInstance().getLeavetimeRule(this.conn, this.userView);
        Date app_time = new Date();
        ArrayList list = OperateDate.getDayByDate(startDate, app_time);
        ArrayList workList = getWeekDateArea(list, nbase, A0100);
        int redays = workList.size();
        int days = 0;
        if (leavetime_rule != null && leavetime_rule.length() > 0)
        {
            long num = Integer.parseInt(leavetime_rule);
            days = OperateDate.getDayCountByDate(startDate, app_time);
            
            if ((num + redays) > days)
            {
                retMsg = "无法完成申请！请假至少提前" + String.valueOf(num) + "个工作日。";
            }
        } else
        {
            String late_leavetime_rule = KqParam.getInstance().getLateLeavetimeRule(this.conn, this.userView);
            if (late_leavetime_rule != null && late_leavetime_rule.length() > 0)
            {
                long num = Integer.parseInt(late_leavetime_rule);
                days = OperateDate.getDayCountByDate(app_time, startDate);
                
                if ((num + redays) < days)
                {
                    retMsg = "无法完成申请！请假最迟只可延后" + String.valueOf(num) + "个工作日。";
                }
            }
        }

        return retMsg;
    }


    public boolean isEvenToClass(ArrayList infolist, String nbase, ArrayList dateList)
    {
        Iterator it = infolist.iterator();
        StringBuffer sql = new StringBuffer();
        String rqclass = "select DISTINCT Q03Z0,class_id from kq_employ_shift where";
        String jinrq = "select DISTINCT Q03Z0 from kq_employ_shift where";
        while (it.hasNext())
        {
            Map infoMap = (Map) it.next();
            sql.append(" (nbase='" + (String) infoMap.get("nbase") + "' and A0100='" + infoMap.get("a0100") + "'");
            sql.append(" and Q03Z0 in(");
            for (int i = 0; i < dateList.size(); i++)
            {
                sql.append("'" + getStrD(dateList, i) + "',");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(")) or");
        }
        sql.delete(sql.length() - 2, sql.length());
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        int rqcCount = 0;
        int jrqCount = 0;
        try
        {
            rqclass += sql.toString();
            jinrq += sql.toString();
            rs = dao.search(rqclass);
            rs.last();
            rqcCount = rs.getRow();
            rs = dao.search(jinrq);
            rs.last();
            jrqCount = rs.getRow();
            if (rqcCount == jrqCount)
            {
                return true;
            } else
            {
                return false;
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
        return true;
    }

    /**
     * 判断申请的时间和工作班次是否有交集
     * 
     * @param nbase
     * @param A0100
     * @param startDate
     * @param endDate
     * @return
     */
    public boolean isAppTimeXWorkclass(String nbase, String A0100, Date startDate, Date endDate)
    {
        String date = OperateDate.dateToStr(startDate, "yyyy.MM.dd");
        Date startTime = OperateDate.getDateByFormat(startDate, "HH:mm");
        Date endTime = OperateDate.getDateByFormat(endDate, "HH:mm");
        Date onduty = null;
        Date offduty = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select onduty_1,offduty_1,offduty_2,offduty_3");
        sql.append(" from kq_class where class_id=");
        sql.append("(select class_id from kq_employ_shift where nbase='");
        sql.append(nbase + "' and A0100='" + A0100 + "' and Q03Z0='");
        sql.append(date + "' and class_id!='0')");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            if (re.next())
            {

                onduty = OperateDate.strToDate(re.getString("onduty_1"), "HH:mm");
                for (int i = 3; i > 0; i--)
                {
                    if (re.getString("offduty_" + i) != null && re.getString("offduty_" + i).length() == 5)
                    {
                        offduty = OperateDate.strToDate(re.getString("offduty_" + i), "HH:mm");
                        break;
                    }
                }
            } else
            {
                return false;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        if (OperateDate.getDateByFormat(startDate, "yyyy-MM-dd").getTime() == OperateDate.getDateByFormat(endDate, "yyyy-MM-dd").getTime())
        {
            if (offduty.getTime() > startTime.getTime() && onduty.getTime() < endTime.getTime())
            {
                return true;
            }
        } else
        {
            if (offduty.getTime() > startTime.getTime())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得人员在date这日班次的开始和结束时间 以及 这个班次是否跨天
     * 
     * @param nbase
     * @param A0100
     * @param date
     * @return
     */
    public Map getTimeByDate(String nbase, String A0100, Date date)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("select onduty_1,onduty_2,onduty_3,offduty_1,offduty_2,offduty_3");
        sql.append(" from kq_class where class_id!='0' and class_id=");
        sql.append("(select class_id from kq_employ_shift where");
        sql.append(" nbase='" + nbase + "' and A0100='" + A0100 + "' and ");
        sql.append("Q03Z0='" + OperateDate.dateToStr(date, "yyyy.MM.dd") + "')");
        Map data = new HashMap();
        boolean conday = false;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            if (re.next())
            {
                String onduty = "";
                String offduty = "";
                
                //上班时间
                onduty = re.getString("onduty_1");
                
                //找下班时间
                for (int i = 3; i > 0; i--)
                {
                    offduty = re.getString("offduty_" + i);
                    if (offduty != null && offduty.length() == 5) {
                        break;
                    }
                }
                
                /*分析班次是否跨零点
                 * 新算法：支持跨24小时
                 */
                String onDutyTime = "";
                String offDutyTime = "";
                String preTime = "";
                for (int i=1; i<=3; i++) {
                    onDutyTime = re.getString("onduty_" + i);
                    onDutyTime = PubFunc.nullToStr(onDutyTime).trim();
                    
                    offDutyTime = re.getString("offduty_" + i);
                    offDutyTime = PubFunc.nullToStr(offDutyTime).trim();
                    
                    //上班点或下班点未定义，直接退出
                    if ("".equals(onDutyTime) || "".equals(offDutyTime)) {
                        break;
                    }
                    
                    //本段上班点与上段下班点间是否跨零点
                    if (i>1) {
                        conday = 0 < preTime.compareTo(onDutyTime);
                        if (conday) {
                            break;
                        }
                    }
                    
                    //本段上班点和下班点间是否跨零点
                    conday = 0 <= onDutyTime.compareTo(offDutyTime);
                    if (conday) {
                        break;
                    }
                    
                    preTime = offDutyTime;                    
                }
                
                /* 原算法：每个时段的下班点与第一上班点比较，出现下班点早于上班点的，即认为跨零点
                String offduty_time =  (null == re.getString("offduty_1") || "".equals(re.getString("offduty_1"))) ? 
                		"23:59" : re.getString("offduty_1");
                if (isBigToTime(onduty, offduty_time))
                {
                    conday = true;
                } else if ((re.getString("onduty_2") != null && re.getString("onduty_2").length() > 0) && (re.getString("offduty_2") != null && re.getString("offduty_2").length() > 1))
                {
                    if (isBigToTime(onduty, re.getString("onduty_2")) || isBigToTime(onduty, re.getString("offduty_2")))
                    {
                        conday = true;
                    } else if ((re.getString("onduty_3") != null && re.getString("onduty_3").length() > 1) && (re.getString("offduty_3") != null && re.getString("offduty_3").length() > 1))
                    {
                        if (isBigToTime(onduty, re.getString("onduty_3")) || isBigToTime(onduty, re.getString("offduty_3")))
                        {
                            conday = true;
                        }
                    }
                }
                */
                data.put("conday", new Boolean(conday));
                data.put("startTime", onduty);
                data.put("endTime", offduty);
                return data;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        return data;
    }

    /**
     * 判读加班，公出，请假申请的时间段是否冲突
     * 
     * @param table
     *            表名
     * @param nbase
     *            人员库
     * @param a0100
     *            人员ID
     * @param startDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @return
     * 
     */
    public boolean checkTimeX(String table, String nbase, String a0100, String startDate, String endDate)
    {
        StringBuffer sql = new StringBuffer();
        if ("q15".equalsIgnoreCase(table))
        {
            sql.append("SELECT q1501,q15z1,q15z3 FROM q15");
            sql.append(" WHERE " + Sql_switcher.isnull("q1517", "'0'") + "!='1' and q15z5!='10' and nbase='" + nbase);
            sql.append("' and a0100='" + a0100 + "' and q15z3>" + Sql_switcher.dateValue(startDate));
            sql.append(" and " + table + "z1<" + Sql_switcher.dateValue(endDate));
        } else
        {
            sql.append("SELECT 1 FROM " + table);
            sql.append(" WHERE " + table + "z5!='10' and nbase='" + nbase);
            sql.append("' and a0100='" + a0100 + "' and " + table + "z3>" + Sql_switcher.dateValue(startDate));
            sql.append(" and " + table + "z1<" + Sql_switcher.dateValue(endDate));
        }
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            while (re.next())
            {
                if ("q15".equalsIgnoreCase(table))
                {
                    String sql1 = "Select q15z1,q15z3 from q15 where q1519='" + re.getString("q1501") + "' and q15z5 = '03'";
                    RowSet re1 = dao.search(sql1);
                    if (re1.next())
                    {
                        Date q_q15z1 = OperateDate.getDateByFormat(re.getTimestamp("q15z1"), "yyyy-MM-dd HH:mm");
                        Date q_q15z5 = OperateDate.getDateByFormat(re.getTimestamp("q15z3"), "yyyy-MM-dd HH:mm");
                        Date t_q15z1 = OperateDate.getDateByFormat(re1.getTimestamp("q15z1"), "yyyy-MM-dd HH:mm");
                        Date t_q15z5 = OperateDate.getDateByFormat(re1.getTimestamp("q15z3"), "yyyy-MM-dd HH:mm");
                        if (!q_q15z1.equals(t_q15z1) && q_q15z1.before(OperateDate.strToDate(endDate, "yyyy-MM-dd HH:mm")) && t_q15z1.after(OperateDate.strToDate(startDate, "yyyy-MM-dd HH:mm")))
                        {
                            return true;
                        } else if (!t_q15z5.equals(q_q15z5) && t_q15z5.before(OperateDate.strToDate(endDate, "yyyy-MM-dd HH:mm")) && q_q15z5.after(OperateDate.strToDate(startDate, "yyyy-MM-dd HH:mm")))
                        {
                            return true;
                        }
                    } else
                    {
                        return true;
                    }
                } else
                {
                    return true;
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        return false;
    }

    /**
     * 根据a0100，nbase查询用户信息
     * 
     * @param nbase
     * @param a0100
     * @return 获得 Map(a0100,a0101,b0110,e0122,e01a1,nbase)
     */
    public Map getInfoMap(String nbase, String a0100)
    {
        Map infoMap = new HashMap();
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT a0100,a0101,b0110,e0122,e01a1 FROM " + nbase + "a01");
        sql.append(" where a0100 ='" + a0100 + "'");
        RowSet re = null;
        try
        {
            re = dao.search(sql.toString());
            if (re.next())
            {
                infoMap.put("a0100", re.getString("a0100"));// 人员编号
                infoMap.put("a0101", re.getString("a0101"));// 姓名
                infoMap.put("b0110", re.getString("b0110"));// 单位编码
                infoMap.put("e0122", re.getString("e0122"));// 部门编码
                infoMap.put("e01a1", re.getString("e01a1"));// 职务编码
                infoMap.put("nbase", nbase);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            KqUtilsClass.closeDBResource(re);
        }
        return infoMap;
    }
    
    /**
     * 检查申请的调休假在调休加班有效时间内可用时长是否够用
     * @param startDate
     * @param infoMap
     * @param app_type
     * @param nbase
     * @param date_count(单位分钟)
     * @param hr_count(单位分钟)
     * @return
     */
    public String checkUsableTime(Date startDate, Map infoMap, String app_type, String nbase, String date_count, String hr_count)
    {
        int timesCount =0;
        int timeCount = 0;
        if(date_count == null || date_count.length() == 0) {
            timeCount = (int)Double.parseDouble(hr_count);
        } else {
            timeCount = (int)Double.parseDouble(date_count);
        }
        GetValiateEndDate ve = new GetValiateEndDate(userView, conn);
        timesCount = ve.getTimesCount(startDate,nbase,(String)infoMap.get("a0100"),conn);
        String msg = "";
        // 36946 换算为小时数
        if(timesCount < timeCount) {
        	// 经讨论 关于调休时长之类提示信息  小数位长度统一 暂定2位 
        	msg = infoMap.get("a0101")+"当前调休时长不足！可用的调休时长仅剩余"+PubFunc.round(timesCount/60.0 + "", 2)+"小时。";
        }
        
        return msg;
    }
    
    /**
     * 获取有效期内调休假可用时长
     * @param startDate
     * @param nbase
     * @param a0100
     * @param conn
     * @return
     */
    public int getTimesCount(Date startDate,String nbase,String a0100,Connection conn){
        String usable = KqParam.getInstance().getOVERTIME_FOR_LEAVETIME_LIMIT();
        if("".equals(usable)) {
            return 0;
        }
        
//        startDate = new Date();//结束时间改为当前系统时间
//        int validityTime = Integer.parseInt(usable);
//        String start_d = OperateDate.dateToStr(OperateDate.addDay(startDate, 0-validityTime),"yyyy.MM.dd");
//        String end_d = OperateDate.dateToStr(startDate,"yyyy.MM.dd");
        
        KqOverTimeForLeaveBo kqOverTimeForLeave = new KqOverTimeForLeaveBo(this.conn, this.userView);
        HashMap period = kqOverTimeForLeave.getEffectivePeriod(startDate);
        String start_d = ((String)period.get("from")).replaceAll("-", ".");
        String end_d = ((String)period.get("to")).replaceAll("-", ".");
        
        StringBuffer sb = new StringBuffer();
        sb.append("select sum(Q3309) from Q33 where nbase ='"+nbase+"' and a0100 ='"+a0100+"'");
        sb.append(" and Q3303 >= '"+start_d+"' and Q3303 <= '"+end_d+"'");
        RowSet rs = null;
        int timesCount = 0;
        ContentDAO dao = new ContentDAO(conn);
        try
        {
            rs = dao.search(sb.toString());
            while(rs.next()){
                timesCount = timesCount + rs.getInt(1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }finally{
            KqUtilsClass.closeDBResource(rs);
        }
        return timesCount;
    }
    
    public int getTimesDetailsCount(Date startDate,String nbase,String a0100,Connection conn,String content){
        KqOverTimeForLeaveBo kqOverTimeForLeave = new KqOverTimeForLeaveBo(conn, this.userView);
        HashMap period = kqOverTimeForLeave.getEffectivePeriod(startDate);
        String start_d = ((String)period.get("from")).replaceAll("-", ".");
        String end_d = ((String)period.get("to")).replaceAll("-", ".");
        
        StringBuffer sb = new StringBuffer();
        sb.append("select "+content+" from Q33 where nbase ='"+nbase+"' and a0100 ='"+a0100+"'");
        sb.append(" and Q3303 >= '"+start_d+"' and Q3303 <= '"+end_d+"'");
        RowSet rs = null;
        int timesCount = 0;
        ContentDAO dao = new ContentDAO(conn);
        try
        {
            rs = dao.search(sb.toString());
            while(rs.next()){
                timesCount = timesCount + rs.getInt(1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }finally{
            KqUtilsClass.closeDBResource(rs);
        }
        return timesCount;
    }
    
    public String checkAppRepeat(String table, String nbase, Map empInfo, Date startDate, Date endDate, boolean isSelfApp) {
        String a0100 = (String)empInfo.get("a0100"); 
        String a0101 = "";
        if (!isSelfApp) {
            a0101 = (String)empInfo.get("a0101");
        }
        String strStartDate = OperateDate.dateToStr(startDate, "yyyy-MM-dd HH:mm");
        String strEndDate = OperateDate.dateToStr(endDate, "yyyy-MM-dd HH:mm");
        
        //zxj 20170913 验证申请冲突统一调用annualApply中方法
        AnnualApply annualApply = new AnnualApply(this.userView, this.conn);
        try {
            annualApply.isRepeatedAllAppType(table, nbase, a0100, a0101, strStartDate, strEndDate, conn, "", "");
        } catch(GeneralException e) {
            return e.getErrorDescription();
        } catch(Exception e) {
            return e.getMessage();
        }
        
        return "";
    }
}
