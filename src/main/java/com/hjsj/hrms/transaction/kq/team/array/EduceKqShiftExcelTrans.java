package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.EduceKqShiftExcel;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.ibm.icu.text.SimpleDateFormat;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 打印考勤排班
 * <p>Title:EduceKqShiftExcelTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 9, 2007 11:13:30 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class EduceKqShiftExcelTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            // 选择日期（筛选日期当天符合条件的数据并显示前后一周内所有数据）
            String dateBegin = (String) this.getFormHM().get("dateBegin");
            if (StringUtils.isNotEmpty(dateBegin))
                dateBegin = dateBegin.replace(".", "-");
            // 筛选条件是时间时的起始时间
            String startTimes = (String) this.getFormHM().get("startTimes");
            // 筛选条件是时间时的结束时间
            String endTimes = (String) this.getFormHM().get("endTimes");
            // 筛选条件时班次筛选的班次ID
            String name = (String) this.getFormHM().get("select_kqlist");
            String checkType = (String) this.getFormHM().get("checkType");
            // 是否点击显示二级 筛选工具栏 -1-是点击
            String selectShowBar = (String) this.getFormHM().get("selectShowBar");
            String clicked = (String) this.getFormHM().get("clicked");
            String identity = (String)this.getFormHM().get("identity");
            
            String a_code = (String) this.getFormHM().get("a_code");
            String decrypt_flag = (String)this.getFormHM().get("decrypt_flag");//获取是否解密标志
            String nbase = (String) this.getFormHM().get("nbase");
            if(!"0".equals(decrypt_flag)){
            	a_code = PubFunc.decrypt(a_code);            	
            	nbase = PubFunc.decrypt(nbase);
            }
            String session_date = (String) this.getFormHM().get("session_data");
            String finsh = (String) this.getFormHM().get("finsh");
            String his = (String) this.getFormHM().get("his");//归档 1 封存 0
            String select_name = (String) this.getFormHM().get("select_name");
            String select_pre = (String) this.getFormHM().get("select_pre");
            String select_flag = (String) this.getFormHM().get("select_flag");
            if (select_flag != null && "1".equals(select_flag)) {
                if (select_pre != null && select_pre.length() > 0 && !"0".equals(select_pre) && !"all".equals(select_pre)) {
                    nbase = select_pre;
                }
            }

            if (finsh == null || finsh.length() <= 0)
                finsh = "0";

            String cur_date = "";
            if (session_date != null && session_date.length() > 0) {
                cur_date = session_date;
            } else {
                ArrayList sessionlist = RegisterDate.sessionDate(this.getFrameconn(), finsh);
                CommonData vo = (CommonData) sessionlist.get(0);
                cur_date = vo.getDataValue();
            }

            ArrayList datelist = RegisterDate.getOneDurationDateList(this.getFrameconn(), cur_date, "-1", "");
            
            // 0为表格方式，1为记录方式
            String state = (String) this.getFormHM().get("state");
            if (state == null || state.length() <= 0)
                state = "0";
            
            String week_data = (String) this.getFormHM().get("week_data");
            
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            //获取考勤期间的每周的起止日期
            HashMap weekStartEnd = kqUtilsClass.getStartAndEndDay(cur_date);
            if(StringUtils.isNotEmpty(week_data) && !"全月".equals(week_data) && "1".equalsIgnoreCase(state)){
            	//获取指定考勤周的起止日期
            	String currentStartEnd = (String) weekStartEnd.get(week_data);
            	String currentStart = currentStartEnd.split("至")[0];
            	String currentEnd = currentStartEnd.split("至")[1];
            	datelist = RegisterDate.getOneDurationDateList(this.getFrameconn(), cur_date,currentStart,currentEnd);
            }
            if (a_code == null || a_code.length() <= 0) {
                a_code = "UN";
            }

            String where_c = kqUtilsClass.getWhere_C("1", "a0101", select_name);
            EduceKqShiftExcel educeKqShiftExcel = new EduceKqShiftExcel(this.getFrameconn(), this.userView);
            educeKqShiftExcel.setInwhere(where_c);
            String excel_filename = "";
            ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
            String b0110 = managePrivCode.getPrivOrgId();

            if ("0".equals(state)) {
                if (nbase == null || nbase.length() <= 0) {
                    ArrayList kq_base_list = RegisterInitInfoData.getB0110Dase(this.formHM, this.userView, this.getFrameconn(),
                            b0110);

                    if (kq_base_list == null || kq_base_list.size() == 0)
                        throw new GeneralException(ResourceFactory.getProperty("kq.register.dbase.nosave"));

                    nbase = kq_base_list.get(0).toString();
                }
                //表格
                excel_filename = educeKqShiftExcel.createTableExcel(datelist, a_code, nbase, his);
            } else {
                ArrayList db_list = new ArrayList();
                if (a_code.indexOf("EP") != -1) {
                    if (nbase == null || nbase.length() <= 0) {
                        db_list = RegisterInitInfoData.getB0110Dase(this.formHM, this.userView, this.getFrameconn(), b0110);
                    } else {
                        db_list.add(nbase);
                    }
                } else {
                    if (select_pre == null || select_pre.length() <= 0 || "all".equals(select_pre)) {
                        db_list = RegisterInitInfoData.getB0110Dase(this.formHM, this.userView, this.getFrameconn(), b0110);
                    } else {
                        db_list.add(select_pre);
                    }
                }
                
                // 获得筛选条件是时间筛选时将获得的时间与所有班次的时间相比较，与班次区间有交叉时保存，用于条件查询
                if ("clicked".equalsIgnoreCase(clicked)) {
                    // zxj 20190828 改正取周序号逻辑，考勤期间与自然月不一定是对应的，需要从考勤期间取周次
                    String res = "";
                    String duration = RegisterDate.getDurationFromDate(dateBegin, this.getFrameconn());
                    // 57820 当班日期 没有设置考勤期间的直接抛出
                    if(StringUtils.isBlank(duration)) {
                        throw new GeneralException(ResourceFactory.getProperty("kq.register.nonce.nosession"));
                    }
                    weekStartEnd = kqUtilsClass.getStartAndEndDay(duration);
                    Iterator iter = weekStartEnd.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String key = (String)entry.getKey();
                        String[] dates  = ((String)entry.getValue()).split("至");
                        if(dates.length != 2)
                            continue;
                        
                        if (dates[0].compareTo(dateBegin)<=0 && dateBegin.compareTo(dates[1])<=0) {
                            res = key;
                        }
                    }
                    
                    // 获取指定考勤周的起止日期
                    String currentStartEnd = (String) weekStartEnd.get(res);
                    String currentStart = currentStartEnd.split("至")[0];
                    currentStart = chengeFullTime(currentStart);
                    String currentEnd = currentStartEnd.split("至")[1];
                    currentEnd = chengeFullTime(currentEnd);
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    int betwen = (int) ((sdf.parse(currentEnd).getTime() - sdf.parse(currentStart).getTime()) / 86400000 + 1);
                    if (betwen < 7) {
                        if (kqUtilsClass.isFirstOrLastDay(currentStart, "first", "yyyy-MM-dd"))
                            currentStart = kqUtilsClass.getRestDate(currentStart, betwen);
                        else if (kqUtilsClass.isFirstOrLastDay(currentEnd, "last", "yyyy-MM-dd"))
                            currentEnd = kqUtilsClass.getRestDate(currentEnd, betwen);
                    }
                    datelist = this.getSatisfyDataList(kqUtilsClass, res, dateBegin, weekStartEnd);
                    if (datelist.size() != 7) {
                        datelist.clear();
                        sdf = new SimpleDateFormat("yyyy.MM.dd");
                        currentStart = currentStart.replace("-", ".");
                        currentEnd = currentEnd.replace("-", ".");
                        Date d1 = sdf.parse(currentStart);
                        datelist.add(currentStart);
                        for (int i = 1; i < 6; i++) {
                            datelist.add(sdf.format(DateUtils.addDays(d1, i)));
                        }
                        datelist.add(currentEnd);
                    }
                    
                    ArrayList kqcllist = new ArrayList();
                    KqUtilsClass kqcl = new KqUtilsClass(this.frameconn, this.userView);
                    kqcllist = kqcl.getKqClassListInPriv();
                    
                    // 当获取到筛选条件 为班次时
                    if (StringUtils.isNotEmpty(name) && "2".equals(identity)) {
                        // 获取班次数据
                        LazyDynaBean ldb = new LazyDynaBean();
                        for (int i = 0; i < kqcllist.size(); i++) {
                            ldb = (LazyDynaBean) kqcllist.get(i);
                            if (((String) ldb.get("classId")).equals(name)) {
                                // 保存下拉回显数据
                                this.getFormHM().put("nameId", name);
                                // 保存查询班次名称
                                name = "'" + (String) ldb.get("name") + "'";
                            }
                        }
                    } else {
                        LazyDynaBean ldb = new LazyDynaBean();
                        CommonData da = new CommonData();
                        StringBuffer strBuff = new StringBuffer("");
                        for (int i = 0; i < kqcllist.size(); i++) {
                            ldb = (LazyDynaBean) kqcllist.get(i);
                            for (int j = 1; j <= 4; j++) {
                                sdf = new SimpleDateFormat("HH:mm");
                                Date onduty = null;
                                Date offduty = null;
                                if (StringUtils.isNotEmpty(((String) ldb.get("onduty_" + j)).trim())
                                        && StringUtils.isNotEmpty(((String) ldb.get("offduty_" + j)).trim())) {
                                    onduty = sdf.parse((String) ldb.get("onduty_" + j));
                                    offduty = sdf.parse((String) ldb.get("offduty_" + j));
                                    Date startTime = sdf.parse(startTimes);
                                    Date endTime = sdf.parse(endTimes);
                                    // 当班次时间为跨条情况下将时间分区间考虑
                                    if (onduty.after(offduty)) {
                                        if (classesSpan(onduty, sdf.parse("23:59"), startTime, endTime)
                                                || classesSpan(sdf.parse("00:00"), offduty, startTime, endTime)) {
                                            strBuff.append(" '" + (String) ldb.get("name") + "',");
                                            break;
                                        }
                                    } else if (classesSpan(onduty, offduty, startTime, endTime)) {
                                        strBuff.append(" '" + (String) ldb.get("name") + "',");
                                        break;
                                    }
                                }
                            }
                        }
                        if (strBuff.length() > 0)
                            name = strBuff.toString().substring(0, strBuff.toString().lastIndexOf(","));
                        else {
                            name = "'XYZ'";
                        }                        
                    }                 
                }
                
                if ("1".equals(his)) {
                    excel_filename = educeKqShiftExcel.returnRecordExcelHis(datelist, a_code, db_list);
                } else {
                    excel_filename = educeKqShiftExcel.returnRecordExcel(datelist, a_code, db_list, dateBegin, name, clicked);
                }
            }
            //xiexd 2014.09.12 加密文件名
            excel_filename = PubFunc.encrypt(excel_filename);
            this.formHM.put("excelfile", excel_filename);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    /**
     * 
     * @Title:classesSpan
     * @Description：班次与筛选时间进行匹配
     * @author liuyang
     * @param onduty
     *            班次起始时间
     * @param offduty
     *            班次结束时间
     * @param startTime
     *            筛选条件起始时间
     * @param endTime
     *            筛选条件结束时间
     * @return 是否存在交集
     */
    private boolean classesSpan(Date onduty, Date offduty, Date startTime, Date endTime) {
        boolean bl = false;
        // 起止时间在班次时间内或起始/结束时间相同
        if (((onduty.before(startTime) || offduty.equals(endTime)) && 
            (offduty.after(endTime) || offduty.equals(endTime))) ||
            // 起始时间早于班次起始时间 && 结束时间晚于班次起始时间
            (onduty.after(startTime) && onduty.before(endTime)) ||
            // 结束时间晚于班次结束时间 && 起始时间早于班次起始时间
            (offduty.before(endTime) && offduty.after(startTime)) ||
            // 班次时间在起始时间内
            (onduty.after(startTime) && offduty.before(endTime)))
            bl = true;
        return bl;
    }
    
    /**
     * 
     * @Title:chengeFullTime
     * @Description：判断时间字符串中是否确实"0"的情况
     * @author liuyang
     * @param time 需要进行判断的时间
     * @return 返回正确的时间字符串
     */
    private String chengeFullTime(String time) {
        String chengedTime = time;
        String[] datePart = chengedTime.split("-");
        int monthPart = Integer.parseInt(datePart[1]);
        int timePart = Integer.parseInt(datePart[2]);
        if (monthPart < 10 && datePart[1].length() == 1)
            chengedTime = datePart[0] + "-0" + datePart[1];
        else
            chengedTime = datePart[0] + "-" + datePart[1];

        if (timePart < 10 && datePart[2].length() == 1)
            chengedTime = chengedTime + "-0" + datePart[2];
        else
            chengedTime = chengedTime + "-" + datePart[2];
        return chengedTime;

    }
    
    /**
     * 
     * @param kqUtilsClass
     * @param week_date   cur_date处于指定月的第几周
     * @param cur_date    传递的日期参数；格式yyyy-MM-dd 或yyyy-MM 或 yyyy-MM-dd hh:mm:ss
     * @param weekStartEnd  cur_date所在月每周的 起止日期
     * @return
     * @throws GeneralException
     */
    private ArrayList getSatisfyDataList(KqUtilsClass kqUtilsClass,
            String week_date, String cur_date, HashMap weekStartEnd)
            throws GeneralException {
        ArrayList datelist;
        //获取指定考勤周的起止日期
        String currentStartEnd = (String) weekStartEnd.get(week_date);
        String currentStart = currentStartEnd.split("至")[0];
        String currentEnd = currentStartEnd.split("至")[1];
        //currentEnd和currentStart差值
        int diff = kqUtilsClass.getTimeDifDay(currentStart, currentEnd, "yyyy-MM-dd");
        //第一周或最后一周不是整周
        if(diff<7){
            if(kqUtilsClass.isFirstOrLastDay(currentStart, "first", "yyyy-MM-dd"))
                currentStart = kqUtilsClass.getRestDate(currentStart, diff);
            else if(kqUtilsClass.isFirstOrLastDay(currentEnd, "last", "yyyy-MM-dd"))
                currentEnd = kqUtilsClass.getRestDate(currentEnd, diff);
        }
        datelist = RegisterDate.getOneDurationDateList(this.getFrameconn(), cur_date,currentStart,currentEnd);
        return datelist;
    }
}
