package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.team.KqShiftClass;
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

public class SearchKqShiftTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String a_code = (String) this.getFormHM().get("a_code");
            // xiexd 2014.09.19排班处理
            if(a_code!=null && !"".equals(a_code) && !"null".equalsIgnoreCase(a_code))
            {
                String a_str = a_code.substring(0, 2);
                if(!"UN".equals(a_str)&&!"@K".equals(a_str)&&!"UM".equals(a_str)&&!"EP".equals(a_str)&&!"GP".equals(a_str))
                {               
                    a_code = PubFunc.decrypt(a_code);
                }
            }
            if (a_code.startsWith("UN") || a_code.startsWith("@K") || a_code.startsWith("UM"))
                this.getFormHM().put("codeid", a_code);
            this.getFormHM().put("unCodeitemid", this.getFormHM().get("codeid"));

            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String nbase = (String) hm.get("nbase");
            if(nbase != null && !"".equals(nbase) && nbase.length()>3)
                nbase = PubFunc.decrypt(nbase);//解密
            String grnbase = nbase;
            // 回显数据时 单选按钮
            String identity;
            String select_flag = (String) this.getFormHM().get("select_flag");
            String select_name = (String) this.getFormHM().get("select_name");
            if("请输入姓名、工号或考勤卡号".equalsIgnoreCase(select_name))
            	select_name="";
            String select_pre = (String) this.getFormHM().get("select_pre");
            String session_date = (String) this.getFormHM().get("session_data");
            ArrayList sessionlist = (ArrayList) this.getFormHM().get("sessionlist");
            // 选择根据时间或者班次进行条件查询-1-时间条件查询-2-班次条件查询
            String checkType = (String) this.getFormHM().get("checkType");
            // 选择日期（筛选日期当天符合条件的数据并显示前后一周内所有数据）
            String dateBegin = (String) this.getFormHM().get("dateBegin");
            // 筛选条件是时间时的起始时间
            String startTimes = (String) this.getFormHM().get("startTimes");
            // 筛选条件是时间时的结束时间
            String endTimes = (String) this.getFormHM().get("endTimes");
            // 筛选条件时班次筛选的班次ID
            String name = (String) this.getFormHM().get("select_kqlist");
            // 是否点击显示二级 筛选工具栏 -1-是点击
            String selectShowBar = (String) this.getFormHM().get("selectShowBar");
            
            KqUtilsClass kqcl = new KqUtilsClass(this.frameconn, this.userView);
            ArrayList nameList = kqcl.getKqClassListInPriv();
            // 当筛选条件是 时间或者 未指定筛选条件
            if ("1".equals(checkType) || "3".equals(checkType) || StringUtils.isEmpty(checkType)) {
                identity = "1";
                if (StringUtils.isNotEmpty(startTimes) && StringUtils.isNotEmpty(endTimes)) {
                    name = "";
                    this.getFormHM().remove("nameId");
                    this.getFormHM().put("endTimesH", endTimes.substring(0, endTimes.indexOf(":")));
                    this.getFormHM().put("endTimesM", endTimes.substring(endTimes.indexOf(":") + 1));
                    this.getFormHM().put("startTimesH", startTimes.substring(0, startTimes.indexOf(":")));
                    this.getFormHM().put("startTimesM", startTimes.substring(startTimes.indexOf(":") + 1));
                }
            } else {
                identity = "2";
                this.getFormHM().put("startTimesH", "00");
                this.getFormHM().put("startTimesM", "00");
                this.getFormHM().put("endTimesH", "23");
                this.getFormHM().put("endTimesM", "59");
            }
            // 当获取到筛选条件 为班次时
            if (StringUtils.isNotEmpty(name)&& identity == "2") {
                // 获取班次数据
                LazyDynaBean ldb = new LazyDynaBean();
                for (int i = 0; i < nameList.size(); i++) {
                    ldb = (LazyDynaBean) nameList.get(i);
                    if (((String) ldb.get("classId")).equals(name)) {
                        // 保存下拉回显数据
                        this.getFormHM().put("nameId", name);
                        // 保存查询班次名称
                        name = "'" + (String) ldb.get("name") + "'";
                    }
                }
            }

            if (StringUtils.isNotEmpty(dateBegin))
                dateBegin = dateBegin.replace(".", "-");

            if (sessionlist == null || sessionlist.size() <= 0) {
                sessionlist = RegisterDate.sessionDate(this.getFrameconn());
            }

            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            ArrayList weeklist = new ArrayList();
            String week_date = (String) this.getFormHM().get("week_data");
            String cur_date = "";

            if (session_date != null && session_date.length() > 0) {
                cur_date = session_date;
            } else {
                if (sessionlist == null || sessionlist.size() <= 0)
                    throw new GeneralException(ResourceFactory.getProperty("kq.register.session.nosave"));

                CommonData vo = (CommonData) sessionlist.get(0);
                cur_date = vo.getDataValue();
            }

            weeklist = kqUtilsClass.getWeekOrder(cur_date);
            ArrayList datelist = new ArrayList();
            // 获取考勤期间的每周的起止日期
            HashMap weekStartEnd = kqUtilsClass.getStartAndEndDay(cur_date);
            datelist = RegisterDate.getOneDurationDateList(this.getFrameconn(), cur_date, "", "");
            // 0为表格方式，1为记录方式
            String state = (String) this.getFormHM().get("state");
            // 默认如果有本周就显示本周记录，否则显示第一周
            if ("1".equals(state) && StringUtils.isEmpty(week_date)) {
                if (weekStartEnd.containsKey("本周"))
                    week_date = "本周";
                else
                    week_date = "第一周";
            }
            if (state == null || state.length() <= 0)
                state = "0";

            if (StringUtils.isNotEmpty(week_date) && !"全月".equals(week_date) && "1".equalsIgnoreCase(state)) {
                datelist = this.getSatisfyDataList(kqUtilsClass, week_date, cur_date, weekStartEnd);
            }
            
            
            // 在进行 二级 筛选时
            if (("1".equals(checkType) || "2".equals(checkType)) && "1".equalsIgnoreCase(state)) {
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
                // 获得筛选条件是时间筛选时将获得的时间与所有班次的时间相比较，与班次区间有交叉时保存，用于条件查询
                if ("1".equals(checkType) & StringUtils.isNotEmpty(startTimes)) {
                    ArrayList kqcllist = new ArrayList();
                    kqcllist = kqcl.getKqClassListInPriv();
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
                        name = "''";
                    }
                }
            }

            if (a_code == null || a_code.length() <= 0 || "null".equals(a_code)) {
                a_code = this.userView.getKqManageValue();
                if (a_code == null || a_code.length() <= 0) {
                    if (userView.getManagePrivCode() != null && userView.getManagePrivCodeValue() != null) {
                        a_code = userView.getManagePrivCode() + userView.getManagePrivCodeValue();
                    }
                }
            } else if (a_code.indexOf("EP") != -1) {
                select_name = "";
                select_pre = "";
            }

            String code = getCodeFormA_code(a_code);
            String kind = getKindFormA_code(a_code);

            ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
            if ((select_pre == null || select_pre.length() <= 0) && kq_dbase_list.size()>1)
                select_pre = "all";
            
            String table_html = "";

            String kqTypeWhr = kqUtilsClass.getKqTypeWhere(KqConstant.KqType.STOP, true);
            
            KqShiftClass kqShiftClass = new KqShiftClass(this.getFrameconn(), this.userView);
            kqShiftClass.setDb_list(kq_dbase_list);
            
            //取输入查询条件 29537 数据库参数应为select_pre
            String where_c = kqShiftClass.getSelWhere(select_flag, select_name, select_pre);       
            kqShiftClass.setWhere_c(where_c);
            String select_a0100 = "";
            String select_nbase = "";
            if ("0".equals(state)) {//表格
                if (nbase == null || nbase.length() <= 0) {
                    if (select_flag != null && "1".equals(select_flag)) {
                        nbase = select_pre;
                    } else {
                        ArrayList kq_base_list = kq_dbase_list;

                        if (kq_base_list == null || kq_base_list.size() == 0)
                            throw new GeneralException(ResourceFactory.getProperty("kq.register.dbase.nosave"));

                        nbase = kq_base_list.get(0).toString();
                    }
                } else {
                    if (a_code == null || a_code.indexOf("EP") == -1) {
                        if (select_flag != null && "1".equals(select_flag)) {
                            if (select_pre != null && select_pre.length() > 0 && !"0".equals(select_pre) && !"all".equals(select_pre) && !"".equals(select_pre)) {
                                nbase = select_pre;
                            }
                        }
                    }
                }

                //zxj jazz 38138 页面上搜索框走这里
                if (!"".equals(select_name) && select_flag != null && "1".equals(select_flag)) {
                    ArrayList nbaseList = new ArrayList();
                    if ("all".equalsIgnoreCase(select_pre)) {
                        nbaseList.addAll(kq_dbase_list);
                    } else {
                        nbaseList.add(select_pre);
                    }

                    ArrayList list = kqShiftClass.getUserRecord(select_name, nbaseList, code, kind, kqTypeWhr);
                    if (list.size() >= 1) {
                        String str = (String) list.get(0);
                        select_a0100 = str.split("'")[0];
                        select_nbase = str.split("'")[1];
                    }

                    if (select_a0100 != null && select_a0100.length() > 0 && select_nbase != null && select_nbase.length() > 0) {
                        table_html = kqShiftClass.returnShiftHtml(datelist, "EP" + select_a0100, select_nbase);
                    } else {
                        table_html = kqShiftClass.returnShiftHtml(datelist, a_code, nbase);
                    }
                } else {
                    //点选组织机构、班组、人员时
                    table_html = kqShiftClass.returnShiftHtml(datelist, a_code, nbase);
                }
              //清除列表时的回显筛选条件 时间条件的缓存数据
              this.getFormHM().remove("startTimesH");
              this.getFormHM().remove("startTimesM");
              this.getFormHM().remove("endTimesH");
              this.getFormHM().remove("endTimesM");
              this.getFormHM().remove("nameId");
              this.getFormHM().remove("dateBegin");
              identity = "1";
              this.getFormHM().put("checkType", "3");
            } else {//记录
                String curpage = (String) this.getFormHM().get("curpage");
                if (curpage == null || curpage.length() <= 0)
                    curpage = "1";

                int cp = Integer.parseInt(curpage);
                ArrayList db_list = new ArrayList();
                if (select_flag != null && "1".equals(select_flag) && !"all".equals(select_pre) && !"0".equals(select_pre) && !"".equals(select_pre)) {
                    db_list.add(select_pre);
                } else {
                    if (a_code.indexOf("EP") != -1) {
                        if (nbase == null || nbase.length() <= 0)
                            db_list = kq_dbase_list;
                        else
                            db_list.add(nbase);
                    } else {
                        if (select_pre == null || select_pre.length() <= 0 || "all".equals(select_pre)) {
                            db_list = kq_dbase_list;
                        } else {
                            db_list.add(select_pre);
                        }
                    }
                }

                ArrayList list = kqShiftClass.getUserRecord(select_name, db_list, code, kind, kqTypeWhr);
                if (list.size() >= 1) {
                    String str = (String) list.get(0);
                    select_a0100 = str.split("'")[0];
                    select_nbase = str.split("'")[1];
                }
                //29614 linbz 记录模式下where_c是传的需要查询的值，然后单独拼接sql
                kqShiftClass.setWhere_c(select_name);
                table_html = kqShiftClass.returnRecordHtml(datelist, a_code, db_list, cp, 10, kqTypeWhr, dateBegin,
                        name, selectShowBar);
            }

            this.getFormHM().put("kq_list", kqUtilsClass.getKqNbaseList(kq_dbase_list));

            ArrayList list = new ArrayList();
            ArrayList kqlist = new ArrayList();
            CommonData vo = null;
            kqlist = kqcl.getKqClassListInPriv();
            LazyDynaBean ldb = new LazyDynaBean();
            for (int i = 0; i < kqlist.size(); i++) {
                ldb = (LazyDynaBean) kqlist.get(i);
                String classId = (String) ldb.get("classId");
                String className = (String) ldb.get("name");
                vo = new CommonData();
                vo.setDataName(className);
                vo.setDataValue(classId);
                list.add(vo);
            }
            this.getFormHM().put("bc_list", list);
            this.getFormHM().put("state", state);
            this.getFormHM().put("session_data", cur_date);
            this.getFormHM().put("table_html", table_html);
            this.getFormHM().put("sessionlist", sessionlist);
            this.getFormHM().put("weeklist", weeklist);
            String a_code1 = PubFunc.encrypt(a_code);
            String nbase1 = PubFunc.encrypt(nbase);
            this.getFormHM().put("a_code", a_code1);
            this.getFormHM().put("nbase", nbase1);
            this.getFormHM().put("datelist", datelist);
            this.getFormHM().put("hidden_name", select_name);
            if (select_name != null && select_name.length() > 0) {
                if ("0".equals(state))
                    this.getFormHM().put("code_mess", kqUtilsClass.getACodeDesc("EP" + select_a0100, select_nbase));
                else
                    this.getFormHM().put("code_mess", kqUtilsClass.getACodeDesc("EP" + select_a0100, select_nbase));
            } else {
                this.getFormHM().put("code_mess", kqUtilsClass.getACodeDesc(a_code, nbase));
            }

            this.getFormHM().put("grnbase", grnbase);
            this.getFormHM().put("select_name", select_name);
            this.getFormHM().put("select_flag", "");
            // 将checkType值进行调整，防止二级查询后，再进行以及查询进入到二级查询条件中
            if (StringUtils.isNotEmpty(checkType)) {
                if ("1".equals(checkType))
                    this.getFormHM().put("checkType", "3");
                if ("2".equals(checkType))
                    this.getFormHM().put("checkType", "4");
            }
            this.getFormHM().put("identity", identity);
            // 判断当前是否还是存在下拉情况
            String clicked = StringUtils.isNotEmpty(checkType) && "1".equals(selectShowBar) ? "clicked" : "";
            this.getFormHM().put("clicked", clicked);
            if (StringUtils.isNotEmpty(checkType)&& "1".equals(state)) {
            	// 57820 当班日期 如果不存在 默认所选期间第一天
            	if(StringUtils.isBlank(dateBegin)) {
            		dateBegin = (String)datelist.get(0);
            	}
            	this.getFormHM().put("dateBegin", dateBegin.replace("-", "."));
            }
            // 清除无用缓存，不能清除，会导致前端无法确定谁当班查询状态，输出排班表不正确
//            this.getFormHM().remove("startTimes");
//            this.getFormHM().remove("endTimes");
//            this.getFormHM().remove("selectShowBar");
//            this.getFormHM().remove("select_kqlist");

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
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


    private String getCodeFormA_code(String a_code) {
        if (a_code == null || a_code.length() <= 0)
            a_code = "UN";
        String code = "";
        if (a_code.indexOf("UN") != -1 || a_code.indexOf("UM") != -1 || a_code.indexOf("@K") != -1)
            if (a_code.length() > 2) {
                code = a_code.substring(2);
            }
        return code;
    }

    private String getKindFormA_code(String a_code) {
        if (a_code == null || a_code.length() <= 0)
            a_code = "UN";
        String kind = "";
        if (a_code.indexOf("UN") != -1)
            kind = "2";
        else if (a_code.indexOf("UM") != -1) {
            kind = "1";
        } else if (a_code.indexOf("@K") != -1) {
            kind = "0";
        }
        return kind;
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
}
