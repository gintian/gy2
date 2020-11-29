package com.hjsj.hrms.module.kq.kqself.kqempcal.businessobject;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.options.KqItem;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.team.KqClassArray;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 员工考勤日历接口类
 * <p>
 * Title: KqEmployeeCalendar
 * </p>
 * <p>
 * Description: 提供员工各种考勤数据信息
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2016-9-28 下午02:21:54
 * </p>
 * 
 * @author zhaoxj
 * @version 1.0
 */
public class KqEmployeeCalendar {
    private String nbase;
    private String A0100;
    private Connection conn;
    private UserView userView;
    private ContentDAO dao;

    public KqEmployeeCalendar(Connection conn, UserView userView, String A0100, String nbase) {
        this.conn = conn;
        this.nbase = nbase;
        this.A0100 = A0100;
        this.userView = userView;
        this.dao = new ContentDAO(conn);
    }

    // 取考勤期间列表
    public ArrayList getKqDurations(Connection conn) throws GeneralException {
        ArrayList sessionlist = null;
        try {
            sessionlist = RegisterDate.sessionDate(conn, "-1");
        } catch (GeneralException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return sessionlist;
    }

    // 取某日期段内每日考勤概况
    /**
     * 取得日考勤状态信息（每日考勤是否正常，申请单是否审批，首末刷卡）
     * 
     * @Title: getKqDailyInfo
     * @Description: 取得日考勤状态信息（每日考勤是否正常，申请单是否审批，首末刷卡）
     * @param requestInfo
     *            其中包含：nbase,a0100,fromdate,todate
     * @return 每个bean中，包含date,state,cards
     * @throws GeneralException
     */
    public ArrayList getDailyInfo(String fromDate, String toDate) throws GeneralException {

        ArrayList<LazyDynaBean> dailyInfo = getInitDailyInfo(fromDate, toDate);
        // 取考勤出勤状态
        LoadDailyException(dailyInfo, fromDate, toDate);

        // 取申请审批情况
        LoadDailyAppState(dailyInfo, fromDate, toDate);

        // 取每日首末刷卡
        // LoadDailyCards(dailyInfo, nbase, A0100, fromDate, toDate);

        return dailyInfo;
    }

    // 得到某段日期内考勤汇总情况
    public HashMap<String, Float> getSumInfo(String fromDate, String toDate) throws GeneralException {
        HashMap<String, Float> hm = new HashMap<String, Float>();
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);

        String sumField = "";
        // 需要汇总的指标
        StringBuffer sumSQL = new StringBuffer();
        sumSQL.append("SELECT 0");
        // 实出勤
        sumField = this.getKqItemSumSQL("28", "normal");
        if (!"".equals(sumField)) {
            sumSQL.append(",");
            sumSQL.append(sumField);
        }

        // 异常(迟到、早退、旷工)
        sumField = this.getKqItemSumSQL("21", "be_late");
        if (!"".equals(sumField)) {
            sumSQL.append(",");
            sumSQL.append(sumField);
        }

        sumField = this.getKqItemSumSQL("23", "leave_early");
        if (!"".equals(sumField)) {
            sumSQL.append(",");
            sumSQL.append(sumField);
        }

        sumField = this.getKqItemSumSQL("25", "absent");
        if (!"".equals(sumField)) {
            sumSQL.append(",");
            sumSQL.append(sumField);
        }

        // 请假公出
        sumField = this.getKqItemSumSQL("0%", "leave");
        if (!"".equals(sumField)) {
            sumSQL.append(",");
            sumSQL.append(sumField);
        }

        // 加班
        sumField = this.getKqItemSumSQL("1%", "overtime");
        if (!"".equals(sumField)) {
            sumSQL.append(",");
            sumSQL.append(sumField);
        }

        // 公出
        sumField = this.getKqItemSumSQL("3%", "office_leave");
        if (!"".equals(sumField)) {
            sumSQL.append(",");
            sumSQL.append(sumField);
        }

        sumSQL.append(" FROM Q03");
        sumSQL.append(" WHERE nbase=?");
        sumSQL.append(" and a0100=?");
        sumSQL.append(" and Q03Z0>=?");
        sumSQL.append(" and Q03Z0<=?");

        ArrayList params = new ArrayList();
        params.add(nbase);
        params.add(A0100);
        params.add(fromDate.replaceAll("-", "."));
        params.add(toDate.replaceAll("-", "."));

        try {
            rs = dao.search(sumSQL.toString(), params);
            if (rs.next()) {
                hm.put("normal", (Float) rs.getFloat("normal") < 0 ? 0 : (Float) rs.getFloat("normal"));
                hm.put("be_late", (Float) rs.getFloat("be_late") < 0 ? 0 : (Float) rs.getFloat("be_late"));
                hm.put("absent", (Float) rs.getFloat("absent") < 0 ? 0 : (Float) rs.getFloat("absent"));
                hm.put("leave_early", (Float) rs.getFloat("leave_early") < 0 ? 0 : (Float) rs.getFloat("leave_early"));
                hm.put("leave", (Float) rs.getFloat("leave") < 0 ? 0 : (Float) rs.getFloat("leave"));
                hm.put("overtime", (Float) rs.getFloat("overtime") < 0 ? 0 : (Float) rs.getFloat("overtime"));
                hm.put("office_leave",
                        (Float) rs.getFloat("office_leave") < 0 ? 0 : (Float) rs.getFloat("office_leave"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return hm;
    }

    // 得到某日考勤详细情况，包括当天班次信息、刷卡数据时间等
    public HashMap getDailyDetail(String date) throws GeneralException {
        /*
         * //当天班次信息 // RecordVo classVo = getClassVo(date); ArrayList classlist =
         * getClasstimes(date); //当天刷卡时间 // ArrayList cardlist = getCardData(date);
         * //刷卡时间信息 ArrayList cardinfolist = getCardIndex(date);
         * 
         * // hm.put("classinfo", classVo); hm.put("classname", classname);
         * hm.put("classtime", classlist); // hm.put("cardtime", cardlist); //
         * hm.put("attendance", attendance); hm.put("cardinfo", cardinfolist);
         */
        HashMap hm = getCardClassInfo(date);
        try {
            String classid = (String) hm.get("classid");
            // 考勤规则
            HashMap map = getKqItems();
            ArrayList q11items = (ArrayList) map.get("q11items");
            // 校验加班模板--是否是公休日 节假日
            String restFlag = IfRestDate.if_Feast(date, this.conn);
            for (int i = 0; i < q11items.size(); i++) {
                HashMap oneMap = (HashMap) q11items.get(i);
                String itemdesc = (String) oneMap.get("itemdesc");
                // 为空则代表是节假日
                if (!"".equals(restFlag)) {
                    if (itemdesc.contains("节假")) {
                        q11items.remove(i);
                        q11items.add(0, oneMap);
                        break;
                    }
                }
                // 排班为0 则代表是休息，即：公休日
                else if ("0".equals(classid)) {
                    if (itemdesc.contains("公休")) {
                        q11items.remove(i);
                        q11items.add(0, oneMap);
                        break;
                    }
                }
                // 其他则为平时加班
                else {
                    if (itemdesc.contains("平时")) {
                        q11items.remove(i);
                        q11items.add(0, oneMap);
                        break;
                    }
                }
            }
            ArrayList classlist = getClassList();
            // 增加班次开始结束时间
            String onduty = "";
            String offduty = "";
            CommonData da = null;
            if (StringUtils.isNotEmpty(classid) && !"0".equals(classid)) {
                for (int i = 0; i < classlist.size(); i++) {
                    da = (CommonData) classlist.get(i);
                    if (da.getDataValue().equals(classid)) {
                        String str = da.getDataName();
                        String classtime = str.split("\\(")[1].replaceAll("\\)", "");
                        onduty = classtime.split("~")[0];
                        offduty = classtime.split("~")[1];
                        break;
                    }
                }
            }
            map.put("onduty", onduty);
            map.put("offduty", offduty);
            map.put("classList", classlist);
            // 获取加班是否调休=0没有调休字段
            map.put("isExistIftoRest", "0");
            String isExistIftoRest = KqUtilsClass.getFieldByDesc("q11",
                    ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
            // 不为空就是有调休字段
            if (StringUtils.isNotEmpty(isExistIftoRest))
                map.put("isExistIftoRest", "1");
            hm.put("allItems", map);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return hm;
    }

    /**
     * 取考勤规则对应的日明细指标（SQL格式）
     * 
     * @param itemId
     *            考勤规则id或一类id（如 ‘01%’）
     * @param alia
     *            指标别名
     * @return
     */
    public String getKqItemSumSQL(String itemId, String alia) {
        String sumField = "0";
        String fieldItemId = "";

        if (!itemId.endsWith("%")) {
            AnnualApply annualApply = new AnnualApply(null, this.conn);
            try {
                HashMap kqItem = annualApply.count_Leave(itemId);
                fieldItemId = ((String) kqItem.get("fielditemid")).trim();
                if (!"".equals(fieldItemId)) {
                    FieldItem item = DataDictionary.getFieldItem(fieldItemId, "Q03");
                    if (item == null || !"1".equals(item.getUseflag()))
                        fieldItemId = "";
                    else
                        fieldItemId = tranUnitToDay(fieldItemId, (String) kqItem.get("item_unit"));
                }
            } catch (Exception e) {

            }
        } else {
            RowSet rs = null;
            StringBuffer leaveSumSQL = new StringBuffer();
            leaveSumSQL.append("(0");
            StringBuffer leaveKqItem = new StringBuffer();
            leaveKqItem.append("select item_id,has_rest,has_feast,item_unit,fielditemid,sdata_src");
            leaveKqItem.append(" from kq_item");
            leaveKqItem.append(" WHERE fielditemid IS NOT NULL");
            if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                leaveKqItem.append(" and fielditemid <> ''");
            }
            leaveKqItem.append(" and item_id like '").append(itemId).append("'");
            try {
                rs = dao.search(leaveKqItem.toString());
                while (rs.next()) {
                    fieldItemId = rs.getString("fielditemid");
                    //60453 避免一些特殊情况下，查出来的数据中仍有异常数据
                    if (StringUtils.isBlank(fieldItemId))
                        continue;

                    FieldItem item = DataDictionary.getFieldItem(fieldItemId, "Q03");
                    if (item == null || !"1".equals(item.getUseflag()))
                        continue;

                    fieldItemId = Sql_switcher.isnull(fieldItemId, "0");
                    fieldItemId = tranUnitToDay(fieldItemId, rs.getString("item_unit"));
                    // 如果考勤项目设置异常则不需拼如SQL公式
                    if (StringUtils.isBlank(fieldItemId))
                        continue;
                    leaveSumSQL.append("+").append(fieldItemId);
                }

                // 加班需考虑调休加班
                if ("1%".equalsIgnoreCase(itemId)) {
                    HashMap hm = getOverTimeForLeaveKqItem();
                    if (null != hm && (String) hm.get("fielditemid") != null) {
                        fieldItemId = Sql_switcher.isnull((String) hm.get("fielditemid"), "0");
                        fieldItemId = tranUnitToDay(fieldItemId, (String) hm.get("item_unit"));
                        leaveSumSQL.append("+").append(fieldItemId);
                    }
                }
            } catch (Exception e) {

            } finally {
                PubFunc.closeDbObj(rs);
            }
            leaveSumSQL.append(")");
            fieldItemId = leaveSumSQL.toString();
        }

        if (!"".equals(fieldItemId)) {
            sumField = "sum(" + fieldItemId + ")";
        }

        if (alia != null && !"".equals(alia))
            sumField = sumField + " as " + alia;

        return sumField;
    }

    /**
     * 把指标转换为以天为单位的格式
     * 
     * @param fieldItemId
     *            指标
     * @param oldUnit
     *            原单位
     * @return
     */
    private String tranUnitToDay(String fieldItemId, String oldUnit) {
        String dayFieldItemId = "";
        fieldItemId = Sql_switcher.isnull(fieldItemId, "0");
        if (fieldItemId != null) {
            if (KqConstant.Unit.MINUTE.equals(oldUnit))
                dayFieldItemId = fieldItemId + "/480.0";
            else if (KqConstant.Unit.HOUR.equals(oldUnit))
                dayFieldItemId = fieldItemId + "/8.0";
            else if (KqConstant.Unit.DAY.equals(oldUnit))
                dayFieldItemId = fieldItemId;
        }
        // dayFieldItemId = Sql_switcher.round(dayFieldItemId, 1);
        return dayFieldItemId;
    }

    // 得到考勤业务相关模板 "leave" : ["1:请假模板1","2:请假模板2"]...
    public HashMap<String, ArrayList<String>> getKqTemplates() throws GeneralException {
        HashMap<String, ArrayList<String>> kqTemplates = new HashMap<String, ArrayList<String>>();
        ArrayList<String> leaveTemplates = new ArrayList<String>();
        ArrayList<String> officeleaveTemplates = new ArrayList<String>();
        ArrayList<String> overtimeTemplates = new ArrayList<String>();
        ArrayList<String> cardTemplates = new ArrayList<String>();
        ArrayList<String> qxjTemplates = new ArrayList<String>();
        ArrayList<String> qxjQ11Templates = new ArrayList<String>();
        ArrayList<String> qxjQ13Templates = new ArrayList<String>();
        ArrayList<String> qxjQ15Templates = new ArrayList<String>();

        String value = "";
        try {
            RecordVo option_vo = ConstantParamter.getRealConstantVo("RSYW_PARAM");
            if (option_vo != null && option_vo.getString("str_value").toLowerCase() != null
                    && option_vo.getString("str_value").toLowerCase().trim().length() > 0
                    && option_vo.getString("str_value").toLowerCase().indexOf("xml") != -1) {
                Document doc = PubFunc.generateDom(option_vo.getString("str_value").toLowerCase()); // 读入xml
                Element root = doc.getRootElement(); // 取得根节点
                List modulelist = root.getChildren("module");
                if (modulelist == null || modulelist.size() <= 0)
                    return kqTemplates;

                TemplateTableParamBo templateTableParamBo = new TemplateTableParamBo(this.conn);

                // 39356 业务用户关联自助用户 按自助用户走
                UserView userViewTemplate = this.getUserView();
                if (userViewTemplate.getS_userName() != null && userViewTemplate.getS_userName().length() > 0
                        && userViewTemplate.getStatus() == 0 && userViewTemplate.getBosflag() != null) {

                    userViewTemplate = new UserView(userViewTemplate.getS_userName(), userViewTemplate.getS_pwd(),
                            this.conn);
                    try {
                        userViewTemplate.canLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (int j = 0; j < modulelist.size(); j++) {
                    Element module = (Element) modulelist.get(j);
                    if (!"30".equalsIgnoreCase(module.getAttributeValue("id")))
                        continue;

                    List reclist = module.getChildren("rec");
                    Element rec = null;
                    for (int i = 0; i < reclist.size(); i++) {
                        rec = (Element) reclist.get(i);
                        value = rec.getText();
                        if (value == null || value.trim().length() == 0) {
                            continue;
                        }

                        String[] select_ids = value.split(",");
                        if (select_ids == null || select_ids.length == 0)
                            continue;

                        String recName = rec.getAttributeValue("name");

                        StringBuffer sql = new StringBuffer();
                        sql.append("select tabId,name from template_table  ");
                        sql.append(" where TabId in (");
                        for (int k = 0; k < select_ids.length; k++) {
                            sql.append("'" + select_ids[k] + "',");
                        }
                        sql.setLength(sql.length() - 1);
                        sql.append(")");
                        sql.append(" order by tabid");

                        RowSet rs = dao.search(sql.toString());
                        while (rs.next()) {
                            String tabid = rs.getString("tabId");

                            // 是否有模板权限
                            if (!userViewTemplate.isHaveResource(IResourceConstant.RSBD, tabid))
                                continue;

                            // 如果是请假、加班、公出模板，需检查是否定义了与考勤模块的对应关系
                            if (recName.startsWith("请假") || recName.startsWith("加班") || recName.startsWith("公出")
                                    || recName.startsWith("销假")) {
                                if (!templateTableParamBo.isKqTempalte(Integer.parseInt(tabid)))
                                    continue;
                            }
                            WF_Instance ins = new WF_Instance(Integer.parseInt(tabid), conn, userView);
                            String info = ins.getKqMappingInfo(tabid);

                            String infodata = "";
                            if (info.indexOf("~") != -1 && !"~".equals(info)) {
                                infodata = info.split("~")[1];
                            }
                            infodata = infodata.replace(":", "`");
                            boolean qxj = false;
                            if (infodata.indexOf("QXJ") != -1)
                                qxj = true;
                            // QXJ01`A0C02_2,QXJ05`A0C03_2,QXJZ1`A0C04_2,QXJZ3`A0C05_2,QXJ07`A0C06_2,
                            // QXJ01_O`A0C09_2,QXJ03_O`A0C10_2,QXJZ1_O`A0C07_2,QXJZ3_O`A0C08_2
                            // q15~
                            // Q1501:AB115_2,
                            // Q1503:AB101_2,
                            // Q1505:AB102_2,
                            // Q15Z1:AB109_2,
                            // Q15Z3:AB110_2

                            String tabname = rs.getString("name");
                            // userView.analyseTablePriv(tabid);
                            if (recName.startsWith("请假")) {
                                if (qxj)
                                    qxjQ15Templates.add(tabid + ":" + tabname + ":" + infodata);
                                else
                                    leaveTemplates.add(tabid + ":" + tabname + ":" + infodata);
                            } else if (recName.startsWith("加班")) {
                                if (qxj)
                                    qxjQ11Templates.add(tabid + ":" + tabname + ":" + infodata);
                                else
                                    overtimeTemplates.add(tabid + ":" + tabname + ":" + infodata);
                            } else if (recName.startsWith("公出")) {
                                if (qxj)
                                    qxjQ13Templates.add(tabid + ":" + tabname + ":" + infodata);
                                else
                                    officeleaveTemplates.add(tabid + ":" + tabname + ":" + infodata);
                            } else if (recName.contains("补刷") || recName.contains("刷卡") || recName.contains("补签")
                                    || recName.contains("打卡")) {
                                cardTemplates.add(tabid + ":" + tabname + ":" + infodata);
                            } else if (recName.startsWith("销假") && qxj) {
                                qxjQ15Templates.add(tabid + ":" + tabname + ":" + infodata);
                                qxjQ11Templates.add(tabid + ":" + tabname + ":" + infodata);
                                qxjQ13Templates.add(tabid + ":" + tabname + ":" + infodata);
                            }

                        }

                    }
                }
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            throw GeneralExceptionHandler.Handle(ee);
        }

        kqTemplates.put("leave", leaveTemplates);
        kqTemplates.put("officeleave", officeleaveTemplates);
        kqTemplates.put("overtime", overtimeTemplates);
        kqTemplates.put("card", cardTemplates);
        // 销假模板
        kqTemplates.put("qxjq15", qxjQ15Templates);
        // [47:销假测试:
        // QXJ01`A0C02_2,QXJ05`A0C03_2,QXJZ1`A0C04_2,QXJZ3`A0C05_2,QXJ07`A0C06_2,
        // QXJ01_O`A0C09_2,QXJ03_O`A0C10_2,QXJZ1_O`A0C07_2,QXJZ3_O`A0C08_2]
        kqTemplates.put("qxjq11", qxjQ11Templates);
        kqTemplates.put("qxjq13", qxjQ13Templates);
        return kqTemplates;
    }

    // 获取某天刷卡数据
    private ArrayList getCardData(ArrayList classDateList, String date) throws GeneralException {

        ArrayList cardlist = new ArrayList();
        String onduty_start = "";// 上班刷卡时间起
        String offduty_end = "";// 下班刷卡时间止
        if (classDateList.size() > 0) {
            LazyDynaBean classldb = new LazyDynaBean();
            classldb = (LazyDynaBean) classDateList.get(0);
            onduty_start = (String) classldb.get("onduty_start");
            classldb = (LazyDynaBean) classDateList.get(classDateList.size() - 1);
            offduty_end = (String) classldb.get("offduty_end");
        } else if ((StringUtils.isEmpty(onduty_start) || StringUtils.isEmpty(offduty_end))
                && !StringUtils.isEmpty(date)) {
            onduty_start = date + " 00:00";
            offduty_end = date + " 23:59";
        } else {
            return cardlist;
        }
        // 29841 完善校验，当上下班不需刷卡时直接返回
        if (StringUtils.isEmpty(onduty_start) || StringUtils.isEmpty(offduty_end)) {
            return cardlist;
        }
        RowSet rs = null;
        LazyDynaBean ldb = new LazyDynaBean();

        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT work_date,work_time,inout_flag,sp_flag");
        sql.append(" FROM kq_originality_data ");
        sql.append(" WHERE nbase=?");
        sql.append(" and a0100=?");
        sql.append(" and ((work_date=? and work_time>=?) or work_date>?)");
        sql.append(" and ((work_date=? and work_time<=?) or work_date<?)");

        ArrayList params = new ArrayList();
        params.add(nbase);
        params.add(A0100);
        params.add(onduty_start.split(" ")[0].toString());
        params.add(onduty_start.split(" ")[1].toString());
        params.add(onduty_start.split(" ")[0].toString());
        params.add(offduty_end.split(" ")[0].toString());
        params.add(offduty_end.split(" ")[1].toString());
        params.add(offduty_end.split(" ")[0].toString());

        try {
            rs = dao.search(sql.toString(), params);
            while (rs.next()) {
                ldb = new LazyDynaBean();
                String worktime = (String) rs.getString("work_time");
                String workdate = (String) rs.getString("work_date");
                String inoutflag = (String) rs.getString("inout_flag");
                String spflag = (String) rs.getString("sp_flag");
                ldb.set("worktime", worktime);
                ldb.set("inoutflag", inoutflag);
                ldb.set("workdate", workdate);
                ldb.set("spflag", spflag);
                ldb.set("spflaginfo", AdminCode.getCodeName("23", spflag));
                cardlist.add(ldb);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return cardlist;
    }

    // 获取某天班次信息
    private RecordVo getClassVo(String date) {
        KqClassArray kqClassArray = new KqClassArray(this.conn);

        String classId = kqClassArray.getClassId(date, A0100, nbase);
        RecordVo vo = kqClassArray.getClassMessage(classId);

        return vo;
    }

    private ArrayList getClasstimes(String date) {
        RecordVo classVo = getClassVo(date);
        String onduty_start = "";// 上班刷卡时间起
        String onduty = "";// 上班
        String offduty = "";// 下班
        String offduty_end = "";// 下班刷卡时间止
        ArrayList classlist = new ArrayList();
        LazyDynaBean ldb = new LazyDynaBean();

        for (int i = 0; i < 4; i++) {
            ldb = new LazyDynaBean();
            onduty_start = classVo.getString("onduty_start_" + (i + 1));
            onduty = classVo.getString("onduty_" + (i + 1));
            offduty = classVo.getString("offduty_" + (i + 1));
            offduty_end = classVo.getString("offduty_end_" + (i + 1));

            if ((onduty == null || onduty.length() <= 0) || (offduty == null || offduty.length() <= 0))
                continue;

            ldb.set("onduty_start", onduty_start);
            ldb.set("onduty", onduty);
            ldb.set("offduty", offduty);
            ldb.set("offduty_end", offduty_end);

            classlist.add(ldb);

        }
        return classlist;
    }

    /**
     * 跨天班次时间点则加一天
     * 
     * @param time1
     * @param time2
     * @return
     */
    private String getcClassDateTime(String time1, String time2) {

        Date beforeTime = DateUtils.getDate(time1.substring(11), "HH:mm");
        Date nowTime = DateUtils.getDate(time2, "HH:mm");

        if (!nowTime.before(beforeTime)) {
            time2 = time1.substring(0, 11) + time2;
        } else {
            Date time1Date = DateUtils.getDate(time1.substring(0, 10), "yyyy.MM.dd");
            String datevalue = DateUtils.format(DateUtils.addDays(time1Date, 1), "yyyy.MM.dd");
            time2 = datevalue + " " + time2;
        }
        return time2;
    }

    /**
     * 刷卡开始起的时间点在前一天则减一天
     * 
     * @param time1
     * @param time2
     * @return
     */
    private String getcClassDateTime1(String time1, String time2) {

        Date beforeTime = DateUtils.getDate(time1.substring(11), "HH:mm");
        Date nowTime = DateUtils.getDate(time2, "HH:mm");

        if (!nowTime.before(beforeTime)) {
            Date time1Date = DateUtils.getDate(time1.substring(0, 10), "yyyy.MM.dd");
            String datevalue = DateUtils.format(DateUtils.addDays(time1Date, -1), "yyyy.MM.dd");
            time2 = datevalue + " " + time2;
        } else {
            time2 = time1.substring(0, 11) + time2;
        }
        return time2;
    }

    /**
     * 获取当天的班次信息，刷卡信息，班次名称
     * 
     * @param date
     *            日期2016.01.16
     * @return
     */
    public HashMap getCardClassInfo(String date) throws GeneralException {
        HashMap hm = new HashMap();
        // 当天刷卡时间
        try {

            ArrayList classDateList = new ArrayList();
            // 刷卡时间提示信息标示，到前台获取对应数据
            String normal = "normal";
            String late = "late";
            String absent = "absent";
            String early = "early";
            String leave_late = "leave_late";
            KqParam kqParam = KqParam.getInstance();
            String flex = kqParam.getFlextimeRuler();
            // 班次信息
            RecordVo vo = getClassVo(date);
            String classId = vo.getString("class_id");// 班次编号
            hm.put("classid", classId);
            String className = vo.getString("name");// 班次名称
            LazyDynaBean classldb = new LazyDynaBean();

            String dateflag = "";
            String onduty_card_1 = vo.getString("onduty_card_1");// 上班是否刷卡
            String offduty_card_1 = vo.getString("offduty_card_1");// 下班是否刷卡
            String onduty_start_1 = vo.getString("onduty_start_1");// 上班刷卡起1
            String onduty_1 = vo.getString("onduty_1");// 上班1
            if (!StringUtils.isEmpty(onduty_1)) {
                onduty_1 = date + " " + onduty_1;
                dateflag = onduty_1;
            }
            if (!StringUtils.isEmpty(onduty_start_1)) {
                onduty_start_1 = getcClassDateTime1(dateflag, onduty_start_1);
                classldb.set("onduty_start", onduty_start_1);
            }
            if (!StringUtils.isEmpty(onduty_1)) {
                classldb.set("onduty", onduty_1);
            }
            String onduty_flextime_1 = vo.getString("onduty_flextime_1");// 弹性上班时间
            if (!StringUtils.isEmpty(onduty_flextime_1))
                onduty_flextime_1 = getcClassDateTime(dateflag, onduty_flextime_1);
            else if (!StringUtils.isEmpty(onduty_1))
                dateflag = onduty_1;
            String be_late_for_1 = vo.getString("be_late_for_1");// 迟到1
            if (!StringUtils.isEmpty(be_late_for_1))
                be_late_for_1 = getcClassDateTime(dateflag, be_late_for_1);// date+" "+be_late_for_1;
            else if (!StringUtils.isEmpty(onduty_flextime_1))
                dateflag = onduty_flextime_1;
            String absent_work_1 = vo.getString("absent_work_1");// 迟到旷工1
            if (!StringUtils.isEmpty(absent_work_1))
                absent_work_1 = getcClassDateTime(dateflag, absent_work_1);
            else if (!StringUtils.isEmpty(be_late_for_1))
                dateflag = be_late_for_1;
            String onduty_end_1 = vo.getString("onduty_end_1");// 上班刷卡止1
            if (!StringUtils.isEmpty(onduty_end_1))
                onduty_end_1 = getcClassDateTime(dateflag, onduty_end_1);
            else if (!StringUtils.isEmpty(absent_work_1))
                dateflag = absent_work_1;
            String offduty_start_1 = vo.getString("offduty_start_1");// 下班刷卡起1
            if (!StringUtils.isEmpty(offduty_start_1))
                offduty_start_1 = getcClassDateTime(dateflag, offduty_start_1);
            else if (!StringUtils.isEmpty(onduty_end_1))
                dateflag = onduty_end_1;
            String leave_early_absent_1 = vo.getString("leave_early_absent_1");// 早退旷工1
            if (!StringUtils.isEmpty(leave_early_absent_1))
                leave_early_absent_1 = getcClassDateTime(dateflag, leave_early_absent_1);
            else if (!StringUtils.isEmpty(offduty_start_1))
                dateflag = offduty_start_1;
            String leave_early_1 = vo.getString("leave_early_1");// 早退1
            if (!StringUtils.isEmpty(leave_early_1))
                leave_early_1 = getcClassDateTime(dateflag, leave_early_1);
            else if (!StringUtils.isEmpty(leave_early_absent_1))
                dateflag = leave_early_absent_1;
            String offduty_1 = vo.getString("offduty_1");// 下班1
            if (!StringUtils.isEmpty(offduty_1)) {
                offduty_1 = getcClassDateTime(dateflag, offduty_1);
                classldb.set("offduty", offduty_1);
            } else if (!StringUtils.isEmpty(leave_early_1))
                dateflag = leave_early_1;
            String offduty_flextime_1 = vo.getString("offduty_flextime_1");// 弹性下班时间
            if (!StringUtils.isEmpty(offduty_flextime_1)) {
                offduty_flextime_1 = getcClassDateTime(dateflag, offduty_flextime_1);
            } else if (!StringUtils.isEmpty(offduty_1))
                dateflag = offduty_1;
            String offduty_end_1 = vo.getString("offduty_end_1");// 下班刷卡止1
            if (!StringUtils.isEmpty(offduty_end_1)) {
                offduty_end_1 = getcClassDateTime(dateflag, offduty_end_1);
                classldb.set("offduty_end", offduty_end_1);
            } else if (!StringUtils.isEmpty(offduty_flextime_1))
                dateflag = offduty_flextime_1;
            if (!StringUtils.isEmpty(onduty_1) && !StringUtils.isEmpty(offduty_1))
                classDateList.add(classldb);
            classldb = new LazyDynaBean();

            String onduty_card_2 = vo.getString("onduty_card_2");
            String offduty_card_2 = vo.getString("offduty_card_2");
            String onduty_start_2 = vo.getString("onduty_start_2");
            if (!StringUtils.isEmpty(onduty_start_2)) {
                onduty_start_2 = getcClassDateTime(dateflag, onduty_start_2);
                classldb.set("onduty_start", onduty_start_2);
            } else if (!StringUtils.isEmpty(offduty_end_1))
                dateflag = offduty_end_1;
            String onduty_2 = vo.getString("onduty_2");
            if (!StringUtils.isEmpty(onduty_2)) {
                onduty_2 = getcClassDateTime(dateflag, onduty_2);
                classldb.set("onduty", onduty_2);
            } else if (!StringUtils.isEmpty(onduty_start_2))
                dateflag = onduty_start_2;
            String onduty_flextime_2 = vo.getString("onduty_flextime_2");// 弹性上班时间
            if (!StringUtils.isEmpty(onduty_flextime_2))
                onduty_flextime_2 = getcClassDateTime(dateflag, onduty_flextime_2);
            else if (!StringUtils.isEmpty(onduty_2))
                dateflag = onduty_2;
            String be_late_for_2 = vo.getString("be_late_for_2");
            if (!StringUtils.isEmpty(be_late_for_2))
                be_late_for_2 = getcClassDateTime(dateflag, be_late_for_2);
            else if (!StringUtils.isEmpty(onduty_flextime_2))
                dateflag = onduty_flextime_2;
            String absent_work_2 = vo.getString("absent_work_2");
            if (!StringUtils.isEmpty(absent_work_2))
                absent_work_2 = getcClassDateTime(dateflag, absent_work_2);
            else if (!StringUtils.isEmpty(be_late_for_2))
                dateflag = be_late_for_2;
            String onduty_end_2 = vo.getString("onduty_end_2");
            if (!StringUtils.isEmpty(onduty_end_2))
                onduty_end_2 = getcClassDateTime(dateflag, onduty_end_2);
            else if (!StringUtils.isEmpty(absent_work_2))
                dateflag = absent_work_2;
            String offduty_start_2 = vo.getString("offduty_start_2");
            if (!StringUtils.isEmpty(offduty_start_2))
                offduty_start_2 = getcClassDateTime(dateflag, offduty_start_2);
            else if (!StringUtils.isEmpty(onduty_end_2))
                dateflag = onduty_end_2;
            String leave_early_2 = vo.getString("leave_early_2");
            if (!StringUtils.isEmpty(leave_early_2))
                leave_early_2 = getcClassDateTime(dateflag, leave_early_2);
            else if (!StringUtils.isEmpty(offduty_start_2))
                dateflag = offduty_start_2;
            String leave_early_absent_2 = vo.getString("leave_early_absent_2");
            if (!StringUtils.isEmpty(leave_early_absent_2))
                leave_early_absent_2 = getcClassDateTime(dateflag, leave_early_absent_2);
            else if (!StringUtils.isEmpty(leave_early_2))
                dateflag = leave_early_2;
            String offduty_2 = vo.getString("offduty_2");
            if (!StringUtils.isEmpty(offduty_2)) {
                offduty_2 = getcClassDateTime(dateflag, offduty_2);
                classldb.set("offduty", offduty_2);
            } else if (!StringUtils.isEmpty(leave_early_absent_2))
                dateflag = leave_early_absent_2;
            String offduty_flextime_2 = vo.getString("offduty_flextime_2");// 弹性下班时间
            if (!StringUtils.isEmpty(offduty_flextime_2)) {
                offduty_flextime_2 = getcClassDateTime(dateflag, offduty_flextime_2);
            } else if (!StringUtils.isEmpty(offduty_2))
                dateflag = offduty_2;
            String offduty_end_2 = vo.getString("offduty_end_2");
            if (!StringUtils.isEmpty(offduty_end_2)) {
                offduty_end_2 = getcClassDateTime(dateflag, offduty_end_2);
                classldb.set("offduty_end", offduty_end_2);
            } else if (!StringUtils.isEmpty(offduty_flextime_2))
                dateflag = offduty_flextime_2;
            if (!StringUtils.isEmpty(onduty_2) && !StringUtils.isEmpty(offduty_2))
                classDateList.add(classldb);
            classldb = new LazyDynaBean();

            String onduty_card_3 = vo.getString("onduty_card_3");
            String offduty_card_3 = vo.getString("offduty_card_3");

            String onduty_start_3 = vo.getString("onduty_start_3");
            if (!StringUtils.isEmpty(onduty_start_3)) {
                onduty_start_3 = getcClassDateTime(dateflag, onduty_start_3);
                classldb.set("onduty_start", onduty_start_3);
            } else if (!StringUtils.isEmpty(offduty_end_2))
                dateflag = offduty_end_2;
            String onduty_3 = vo.getString("onduty_3");
            if (!StringUtils.isEmpty(onduty_3)) {
                onduty_3 = getcClassDateTime(dateflag, onduty_3);
                classldb.set("onduty", onduty_3);
            } else if (!StringUtils.isEmpty(onduty_start_3))
                dateflag = onduty_start_3;
            String onduty_flextime_3 = vo.getString("onduty_flextime_3");// 弹性上班时间3
            if (!StringUtils.isEmpty(onduty_flextime_3))
                onduty_flextime_3 = getcClassDateTime(dateflag, onduty_flextime_3);
            else if (!StringUtils.isEmpty(onduty_3))
                dateflag = onduty_3;
            String be_late_for_3 = vo.getString("be_late_for_3");
            if (!StringUtils.isEmpty(be_late_for_3))
                be_late_for_3 = getcClassDateTime(dateflag, be_late_for_3);
            else if (!StringUtils.isEmpty(onduty_flextime_3))
                dateflag = onduty_flextime_3;
            String absent_work_3 = vo.getString("absent_work_3");
            if (!StringUtils.isEmpty(absent_work_3))
                absent_work_3 = getcClassDateTime(dateflag, absent_work_3);
            else if (!StringUtils.isEmpty(be_late_for_3))
                dateflag = be_late_for_3;
            String onduty_end_3 = vo.getString("onduty_end_3");
            if (!StringUtils.isEmpty(onduty_end_3))
                onduty_end_3 = getcClassDateTime(dateflag, onduty_end_3);
            else if (!StringUtils.isEmpty(absent_work_3))
                dateflag = absent_work_3;
            String offduty_start_3 = vo.getString("offduty_start_3");
            if (!StringUtils.isEmpty(offduty_start_3))
                offduty_start_3 = getcClassDateTime(dateflag, offduty_start_3);
            else if (!StringUtils.isEmpty(onduty_end_3))
                dateflag = onduty_end_3;
            String leave_early_3 = vo.getString("leave_early_3");
            if (!StringUtils.isEmpty(leave_early_3))
                leave_early_3 = getcClassDateTime(dateflag, leave_early_3);
            else if (!StringUtils.isEmpty(offduty_start_3))
                dateflag = offduty_start_3;
            String leave_early_absent_3 = vo.getString("leave_early_absent_3");
            if (!StringUtils.isEmpty(leave_early_absent_3))
                leave_early_absent_3 = getcClassDateTime(dateflag, leave_early_absent_3);
            else if (!StringUtils.isEmpty(leave_early_3))
                dateflag = leave_early_3;
            String offduty_3 = vo.getString("offduty_3");
            if (!StringUtils.isEmpty(offduty_3)) {
                offduty_3 = getcClassDateTime(dateflag, offduty_3);
                classldb.set("offduty", offduty_3);
            } else if (!StringUtils.isEmpty(leave_early_absent_3))
                dateflag = leave_early_absent_3;
            String offduty_flextime_3 = vo.getString("offduty_flextime_3");// 弹性下班时间
            if (!StringUtils.isEmpty(offduty_flextime_3)) {
                offduty_flextime_3 = getcClassDateTime(dateflag, offduty_flextime_3);
            } else if (!StringUtils.isEmpty(offduty_3))
                dateflag = offduty_3;
            String offduty_end_3 = vo.getString("offduty_end_3");
            if (!StringUtils.isEmpty(offduty_end_3)) {
                offduty_end_3 = getcClassDateTime(dateflag, offduty_end_3);
                classldb.set("offduty", offduty_end_3);
            } else if (!StringUtils.isEmpty(offduty_flextime_3))
                dateflag = offduty_flextime_3;
            if (!StringUtils.isEmpty(onduty_3) && !StringUtils.isEmpty(offduty_3))
                classDateList.add(classldb);
            classldb = new LazyDynaBean();

            String onduty_card_4 = vo.getString("onduty_card_4");
            String offduty_card_4 = vo.getString("offduty_card_4");

            String onduty_start_4 = vo.getString("onduty_start_4");
            if (!StringUtils.isEmpty(onduty_start_4)) {
                onduty_start_4 = getcClassDateTime(dateflag, onduty_start_4);
                classldb.set("onduty_start", onduty_start_4);
            } else if (!StringUtils.isEmpty(offduty_end_3))
                dateflag = offduty_end_3;
            String onduty_4 = vo.getString("onduty_4");
            if (!StringUtils.isEmpty(onduty_4)) {
                onduty_4 = getcClassDateTime(dateflag, onduty_4);
                classldb.set("onduty", onduty_4);
            } else if (!StringUtils.isEmpty(onduty_start_4))
                dateflag = onduty_start_4;
            String onduty_flextime_4 = vo.getString("onduty_flextime_4");// 弹性上班时间4
            if (!StringUtils.isEmpty(onduty_flextime_4))
                onduty_flextime_4 = getcClassDateTime(dateflag, onduty_flextime_4);
            else if (!StringUtils.isEmpty(onduty_4))
                dateflag = onduty_4;
            String be_late_for_4 = vo.getString("be_late_for_4");
            if (!StringUtils.isEmpty(be_late_for_4))
                be_late_for_4 = getcClassDateTime(dateflag, be_late_for_4);
            else if (!StringUtils.isEmpty(onduty_flextime_4))
                dateflag = onduty_flextime_4;
            String absent_work_4 = vo.getString("absent_work_4");
            if (!StringUtils.isEmpty(absent_work_4))
                absent_work_4 = getcClassDateTime(dateflag, absent_work_4);
            else if (!StringUtils.isEmpty(be_late_for_4))
                dateflag = be_late_for_4;
            String onduty_end_4 = vo.getString("onduty_end_4");
            if (!StringUtils.isEmpty(onduty_end_4))
                onduty_end_4 = getcClassDateTime(dateflag, onduty_end_4);
            else if (!StringUtils.isEmpty(absent_work_4))
                dateflag = absent_work_4;
            String offduty_start_4 = vo.getString("offduty_start_4");
            if (!StringUtils.isEmpty(offduty_start_4))
                offduty_start_4 = getcClassDateTime(dateflag, offduty_start_4);
            else if (!StringUtils.isEmpty(onduty_end_4))
                dateflag = onduty_end_4;
            String leave_early_4 = vo.getString("leave_early_4");
            if (!StringUtils.isEmpty(leave_early_4))
                leave_early_4 = getcClassDateTime(dateflag, leave_early_4);
            else if (!StringUtils.isEmpty(offduty_start_4))
                dateflag = offduty_start_4;
            String leave_early_absent_4 = vo.getString("leave_early_absent_4");
            if (!StringUtils.isEmpty(leave_early_absent_4))
                leave_early_absent_4 = getcClassDateTime(dateflag, leave_early_absent_4);
            else if (!StringUtils.isEmpty(leave_early_4))
                dateflag = leave_early_4;
            String offduty_4 = vo.getString("offduty_4");
            if (!StringUtils.isEmpty(offduty_4)) {
                offduty_4 = getcClassDateTime(dateflag, offduty_4);
                classldb.set("offduty", offduty_4);
            } else if (!StringUtils.isEmpty(leave_early_absent_4))
                dateflag = leave_early_absent_4;
            String offduty_flextime_4 = vo.getString("offduty_flextime_4");// 弹性下班时间4
            if (!StringUtils.isEmpty(offduty_flextime_4)) {
                offduty_flextime_4 = getcClassDateTime(dateflag, offduty_flextime_4);
            } else if (!StringUtils.isEmpty(offduty_4))
                dateflag = offduty_4;
            String offduty_end_4 = vo.getString("offduty_end_4");
            if (!StringUtils.isEmpty(offduty_end_4)) {
                offduty_end_4 = getcClassDateTime(dateflag, offduty_end_4);
                classldb.set("offduty", offduty_end_4);
            } else if (!StringUtils.isEmpty(offduty_flextime_4))
                dateflag = offduty_flextime_4;
            if (!StringUtils.isEmpty(onduty_4) && !StringUtils.isEmpty(offduty_4))
                classDateList.add(classldb);

            // 40008 取消bug29841的校验 只要有打卡就全部显示
            ArrayList cardlist = this.getCardData(classDateList, date);

            int classLen = classDateList.size();
            // 每个刷卡时间段是否确定刷卡时间点
            boolean onduty_yes_1 = false;
            boolean offduty_yes_1 = false;
            boolean onduty_yes_2 = false;
            boolean offduty_yes_2 = false;
            boolean onduty_yes_3 = false;
            boolean offduty_yes_3 = false;
            boolean onduty_yes_4 = false;
            boolean offduty_yes_4 = false;

            int offduty1 = -1;
            int leave_early1 = -1;
            int leave_early_absent1 = -1;
            int offduty_start1 = -1;
            int offduty2 = -1;
            int leave_early2 = -1;
            int leave_early_absent2 = -1;
            int offduty_start2 = -1;
            int offduty3 = -1;
            int leave_early3 = -1;
            int leave_early_absent3 = -1;
            int offduty_start3 = -1;
            int offduty4 = -1;
            int leave_early4 = -1;
            int leave_early_absent4 = -1;
            int offduty_start4 = -1;
            // 弹性时长
            long flextimeLen = 0;

            for (int i = 0; i < cardlist.size(); i++) {
                LazyDynaBean ldb = new LazyDynaBean();
                ldb = (LazyDynaBean) cardlist.get(i);
                String cardtime = (String) ldb.get("worktime");
                String cardWorkDate = (String) ldb.get("workdate");
                String inoutflag = (String) ldb.get("inoutflag");
                String spflag = (String) ldb.get("spflag");
                String spflaginfo = (String) ldb.get("spflaginfo");
                ldb.set("infomsg", "");// 刷卡点信息标示
                ldb.set("spinfo", "");// 刷卡点审批状态
                // 当刷卡数据不是已批状态 || 未排班 时给出审批状态
                if (!"03".equals(spflag) || "0".equals(classId)) {
                    ldb.set("spinfo", spflaginfo);
                    continue;
                }

                if (!(cardtime.length() > 0))
                    continue;
                Date carddate = DateUtils.getDate(cardWorkDate + " " + cardtime, "yyyy.MM.dd HH:mm");
                // 第一时间段上班刷卡
                if (!onduty_yes_1 && "1".equals(onduty_card_1) && !"-1".equals(inoutflag)) {
                    if (!StringUtils.isEmpty(onduty_start_1) && !StringUtils.isEmpty(onduty_end_1)) {
                        Date onduty_start_date1 = DateUtils.getDate(onduty_start_1, "yyyy.MM.dd HH:mm");
                        Date onduty_end_date1 = DateUtils.getDate(onduty_end_1, "yyyy.MM.dd HH:mm");
                        // 刷卡点 必须在刷卡起 至 刷卡止 之间的时间段，否则进行下个时间段的校验
                        if (!carddate.after(onduty_end_date1) && !carddate.before(onduty_start_date1)) {
                            if (onduty_1.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_1, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", normal);
                                    onduty_yes_1 = true;
                                    continue;
                                }
                            }
                            if (onduty_flextime_1.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_flextime_1, "yyyy.MM.dd HH:mm");

                                if (!carddate.after(classdate)) {
                                    ldb.set("infomsg", normal);
                                    if ("0".equals(flex)) {// 完全弹性
                                        flextimeLen = carddate.getTime()
                                                - DateUtils.getDate(onduty_1, "yyyy.MM.dd HH:mm").getTime();
                                    } else if ("1".equals(flex)) {// 不完全弹性
                                        flextimeLen = DateUtils.getDate(onduty_flextime_1, "yyyy.MM.dd HH:mm").getTime()
                                                - DateUtils.getDate(onduty_1, "yyyy.MM.dd HH:mm").getTime();
                                    }
                                    onduty_yes_1 = true;
                                    continue;
                                } else {
                                    if (be_late_for_1.length() > 0) {
                                        Date lateClassDate = DateUtils.getDate(be_late_for_1, "yyyy.MM.dd HH:mm");
                                        if (carddate.before(lateClassDate)) {
                                            ldb.set("infomsg", late);
                                            flextimeLen = DateUtils.getDate(onduty_flextime_1, "yyyy.MM.dd HH:mm")
                                                    .getTime()
                                                    - DateUtils.getDate(onduty_1, "yyyy.MM.dd HH:mm").getTime();
                                            onduty_yes_1 = true;
                                            continue;
                                        }
                                    }
                                }

                            } else if (be_late_for_1.length() > 0) {
                                Date classdate = DateUtils.getDate(be_late_for_1, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", normal);
                                    onduty_yes_1 = true;
                                    continue;
                                }
                            }
                            if (absent_work_1.length() > 0) {
                                Date classdate = DateUtils.getDate(absent_work_1, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", late);
                                    onduty_yes_1 = true;
                                    continue;
                                }
                            }
                            if (onduty_end_1.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_end_1, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", absent);
                                    onduty_yes_1 = true;
                                    continue;
                                }
                            }
                        } else if (carddate.before(onduty_start_date1)) {
                            // ldb.set("infomsg", normal);
                            // onduty_yes_1 = true;
                            ldb.set("worktime", "");
                            continue;
                        }
                    }

                }
                // 第一时间段下班刷卡
                if (!offduty_yes_1 && "1".equals(offduty_card_1) && !"1".equals(inoutflag)) {
                    // 下班刷卡点反过来，从刷卡时间止开始比较
                    if (!StringUtils.isEmpty(offduty_start_1) && !StringUtils.isEmpty(offduty_end_1)) {
                        Date offduty_start_date1 = DateUtils.getDate(offduty_start_1, "yyyy.MM.dd HH:mm");
                        Date offduty_end_date1 = DateUtils.getDate(offduty_end_1, "yyyy.MM.dd HH:mm");
                        // 下班刷卡点 必须在下班刷卡起 至 下班刷卡止 之间的时间段，否则进行下个时间段的校验
                        if (!carddate.after(offduty_end_date1) && !carddate.before(offduty_start_date1)) {

                            if (offduty_1.length() > 0) {
                                Date classdate = DateUtils.getDate(offduty_1, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_1.length() > 0 && flextimeLen > 0 && classLen == 1) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate)) {
                                    long timedelta = carddate.getTime() - classdate.getTime();
                                    if (timedelta != 0 && timedelta / 60 > 30) {
                                        ldb.set("infomsg", leave_late);
                                    } else {
                                        ldb.set("infomsg", normal);
                                    }
                                    if (offduty1 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty1);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early1 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early1);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early_absent1 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent1);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start1 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start1);
                                        ldb1.set("infomsg", "");
                                    }
                                    offduty1 = i;
                                    // offduty_yes_1 = true;
                                    continue;
                                }
                            }
                            if (leave_early_1.length() > 0) {
                                Date classdate = DateUtils.getDate(leave_early_1, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_1.length() > 0 && flextimeLen > 0 && classLen == 1) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate) && !classdate.equals(carddate)) {
                                    ldb.set("infomsg", normal);
                                    if (leave_early1 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early1);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early_absent1 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent1);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start1 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start1);
                                        ldb1.set("infomsg", "");
                                    }
                                    leave_early1 = i;
                                    // offduty_yes_1 = true;
                                    continue;
                                }
                            }
                            if (leave_early_absent_1.length() > 0) {
                                Date classdate = DateUtils.getDate(leave_early_absent_1, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_1.length() > 0 && flextimeLen > 0 && classLen == 1) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate) && !classdate.equals(carddate)) {
                                    ldb.set("infomsg", early);
                                    if (leave_early_absent1 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent1);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start1 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start1);
                                        ldb1.set("infomsg", "");
                                    }
                                    leave_early_absent1 = i;
                                    // offduty_yes_1 = true;
                                    continue;
                                }
                            }
                            if (offduty_start_1.length() > 0) {
                                Date classdate = DateUtils.getDate(offduty_start_1, "yyyy.MM.dd HH:mm");
                                if (!carddate.before(classdate)) {
                                    ldb.set("infomsg", absent);
                                    if (offduty_start1 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start1);
                                        ldb1.set("infomsg", "");
                                    }
                                    offduty_start1 = i;
                                    // offduty_yes_1 = true;
                                    continue;
                                }
                            }
                        }
                    }

                }
                // 第二时间段上班刷卡
                if (!onduty_yes_2 && "1".equals(onduty_card_2) && !"-1".equals(inoutflag)) {
                    if (!StringUtils.isEmpty(onduty_start_2) && !StringUtils.isEmpty(onduty_end_2)) {
                        Date onduty_start_date2 = DateUtils.getDate(onduty_start_2, "yyyy.MM.dd HH:mm");
                        Date onduty_end_date2 = DateUtils.getDate(onduty_end_2, "yyyy.MM.dd HH:mm");
                        // 刷卡点 必须在刷卡起 至 刷卡止 之间的时间段，否则进行下个时间段的校验
                        if (!carddate.after(onduty_end_date2) && !carddate.before(onduty_start_date2)) {
                            if (onduty_start_2.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_start_2, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", normal);
                                    onduty_yes_2 = true;
                                    continue;
                                }
                            }
                            if (onduty_2.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_2, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", normal);
                                    onduty_yes_2 = true;
                                    continue;
                                }
                            }
                            if (be_late_for_2.length() > 0) {
                                Date classdate = DateUtils.getDate(be_late_for_2, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", normal);
                                    onduty_yes_2 = true;
                                    continue;
                                }
                            }
                            if (absent_work_2.length() > 0) {
                                Date classdate = DateUtils.getDate(absent_work_2, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", late);
                                    onduty_yes_2 = true;
                                    continue;
                                }
                            }
                            if (onduty_end_2.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_end_2, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", absent);
                                    onduty_yes_2 = true;
                                    continue;
                                }
                            }
                        }
                    }

                }

                // 第二时间段下班刷卡
                if (!offduty_yes_2 && "1".equals(offduty_card_2) && !"1".equals(inoutflag)) {

                    if (!StringUtils.isEmpty(offduty_start_2) && !StringUtils.isEmpty(offduty_end_2)) {
                        Date offduty_start_date2 = DateUtils.getDate(offduty_start_2, "yyyy.MM.dd HH:mm");
                        Date offduty_end_date2 = DateUtils.getDate(offduty_end_2, "yyyy.MM.dd HH:mm");
                        // 下班刷卡点 必须在下班刷卡起 至 下班刷卡止 之间的时间段，否则进行下个时间段的校验
                        if (!carddate.after(offduty_end_date2) && !carddate.before(offduty_start_date2)) {
                            if (offduty_2.length() > 0) {
                                Date classdate = DateUtils.getDate(offduty_2, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_2.length() > 0 && flextimeLen > 0 && classLen == 2) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate)) {
                                    long timedelta = carddate.getTime() - classdate.getTime();
                                    if (timedelta != 0 && timedelta / 60 > 30) {
                                        ldb.set("infomsg", leave_late);
                                    } else {
                                        ldb.set("infomsg", normal);
                                    }
                                    // ldb.set("off", 3);
                                    // ldb.set("onoff", "off2");
                                    if (offduty2 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty2);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early2 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early2);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early_absent2 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent2);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start2 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start2);
                                        ldb1.set("infomsg", "");
                                    }
                                    offduty2 = i;
                                    // offduty_yes_2 = true;
                                    continue;
                                }
                            }
                            if (leave_early_2.length() > 0) {
                                Date classdate = DateUtils.getDate(leave_early_2, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_2.length() > 0 && flextimeLen > 0 && classLen == 2) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate) && !classdate.equals(carddate)) {
                                    ldb.set("infomsg", normal);
                                    if (leave_early2 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early2);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early_absent2 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent2);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start2 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start2);
                                        ldb1.set("infomsg", "");
                                    }
                                    leave_early2 = i;
                                    // offduty_yes_2 = true;
                                    continue;
                                }
                            }
                            if (leave_early_absent_2.length() > 0) {
                                Date classdate = DateUtils.getDate(leave_early_absent_2, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_2.length() > 0 && flextimeLen > 0 && classLen == 2) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate) && !classdate.equals(carddate)) {
                                    ldb.set("infomsg", early);
                                    if (leave_early_absent2 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent2);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start2 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start2);
                                        ldb1.set("infomsg", "");
                                    }
                                    leave_early_absent2 = i;
                                    // offduty_yes_2 = true;
                                    continue;
                                }
                            }
                            if (offduty_start_2.length() > 0) {
                                Date classdate = DateUtils.getDate(offduty_start_2, "yyyy.MM.dd HH:mm");
                                if (!carddate.before(classdate)) {
                                    ldb.set("infomsg", absent);
                                    if (offduty_start2 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start2);
                                        ldb1.set("infomsg", "");
                                    }
                                    offduty_start2 = i;
                                    // offduty_yes_2 = true;
                                    continue;
                                }
                            }
                        }
                    }

                }
                // 第三时间段上班刷卡
                if (!onduty_yes_3 && "1".equals(onduty_card_3) && !"-1".equals(inoutflag)) {
                    if (!StringUtils.isEmpty(onduty_start_3) && !StringUtils.isEmpty(onduty_end_3)) {
                        Date onduty_start_date3 = DateUtils.getDate(onduty_start_3, "yyyy.MM.dd HH:mm");
                        Date onduty_end_date3 = DateUtils.getDate(onduty_end_3, "yyyy.MM.dd HH:mm");
                        // 刷卡点 必须在刷卡起 至 刷卡止 之间的时间段，否则进行下个时间段的校验
                        if (!carddate.after(onduty_end_date3) && !carddate.before(onduty_start_date3)) {
                            if (onduty_start_3.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_start_3, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", normal);
                                    onduty_yes_3 = true;
                                    continue;
                                }
                            }
                            if (onduty_3.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_3, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", normal);
                                    onduty_yes_3 = true;
                                    continue;
                                }
                            }
                            if (be_late_for_3.length() > 0) {
                                Date classdate = DateUtils.getDate(be_late_for_3, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", normal);
                                    onduty_yes_3 = true;
                                    continue;
                                }
                            }
                            if (absent_work_3.length() > 0) {
                                Date classdate = DateUtils.getDate(absent_work_3, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", late);
                                    onduty_yes_3 = true;
                                    continue;
                                }
                            }
                            if (onduty_end_3.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_end_3, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", absent);
                                    onduty_yes_3 = true;
                                    continue;
                                }
                            }
                        }
                    }

                }
                // 第三时间段下班刷卡
                if (!offduty_yes_3 && "1".equals(offduty_card_3) && !"1".equals(inoutflag)) {
                    if (!StringUtils.isEmpty(offduty_start_3) && !StringUtils.isEmpty(offduty_end_3)) {
                        Date offduty_start_date3 = DateUtils.getDate(offduty_start_3, "yyyy.MM.dd HH:mm");
                        Date offduty_end_date3 = DateUtils.getDate(offduty_end_3, "yyyy.MM.dd HH:mm");
                        // 下班刷卡点 必须在下班刷卡起 至 下班刷卡止 之间的时间段，否则进行下个时间段的校验
                        if (!carddate.after(offduty_end_date3) && !carddate.before(offduty_start_date3)) {
                            if (offduty_3.length() > 0) {
                                Date classdate = DateUtils.getDate(offduty_3, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_3.length() > 0 && flextimeLen > 0 && classLen == 3) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate)) {
                                    long timedelta = carddate.getTime() - classdate.getTime();
                                    if (timedelta != 0 && timedelta / 60 > 30) {
                                        ldb.set("infomsg", leave_late);
                                    } else {
                                        ldb.set("infomsg", normal);
                                    }
                                    if (offduty3 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty3);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early3 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early3);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early_absent3 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent3);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start3 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start3);
                                        ldb1.set("infomsg", "");
                                    }
                                    offduty3 = i;
                                    // offduty_yes_3 = true;
                                    continue;
                                }
                            }
                            if (leave_early_3.length() > 0) {
                                Date classdate = DateUtils.getDate(leave_early_3, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_3.length() > 0 && flextimeLen > 0 && classLen == 3) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate) && !classdate.equals(carddate)) {
                                    ldb.set("infomsg", normal);
                                    if (leave_early3 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early3);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early_absent3 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent3);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start3 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start3);
                                        ldb1.set("infomsg", "");
                                    }
                                    leave_early3 = i;
                                    // offduty_yes_3 = true;
                                    continue;
                                }
                            }
                            if (leave_early_absent_3.length() > 0) {
                                Date classdate = DateUtils.getDate(leave_early_absent_3, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_3.length() > 0 && flextimeLen > 0 && classLen == 3) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate) && !classdate.equals(carddate)) {
                                    ldb.set("infomsg", early);
                                    if (leave_early_absent3 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent3);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start3 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start3);
                                        ldb1.set("infomsg", "");
                                    }
                                    leave_early_absent3 = i;
                                    // offduty_yes_3 = true;
                                    continue;
                                }
                            }
                            if (offduty_start_3.length() > 0) {
                                Date classdate = DateUtils.getDate(offduty_start_3, "yyyy.MM.dd HH:mm");
                                if (!carddate.before(classdate)) {
                                    ldb.set("infomsg", absent);
                                    if (offduty_start3 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start3);
                                        ldb1.set("infomsg", "");
                                    }
                                    offduty_start3 = i;
                                    // offduty_yes_3 = true;
                                    continue;
                                }
                            }
                        }
                    }

                }
                // 第四时间段上班刷卡
                if (!onduty_yes_4 && "1".equals(onduty_card_4) && !"-1".equals(inoutflag)) {
                    if (!StringUtils.isEmpty(onduty_start_4) && !StringUtils.isEmpty(onduty_end_4)) {
                        Date onduty_start_date4 = DateUtils.getDate(onduty_start_4, "yyyy.MM.dd HH:mm");
                        Date onduty_end_date4 = DateUtils.getDate(onduty_end_4, "yyyy.MM.dd HH:mm");
                        // 刷卡点 必须在刷卡起 至 刷卡止 之间的时间段，否则进行下个时间段的校验
                        if (!carddate.after(onduty_end_date4) && !carddate.before(onduty_start_date4)) {
                            if (onduty_start_4.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_start_4, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", normal);
                                    onduty_yes_4 = true;
                                    continue;
                                }
                            }
                            if (onduty_4.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_4, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", normal);
                                    onduty_yes_4 = true;
                                    continue;
                                }
                            }
                            if (be_late_for_4.length() > 0) {
                                Date classdate = DateUtils.getDate(be_late_for_4, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", normal);
                                    onduty_yes_4 = true;
                                    continue;
                                }
                            }
                            if (absent_work_4.length() > 0) {
                                Date classdate = DateUtils.getDate(absent_work_4, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", late);
                                    onduty_yes_4 = true;
                                    continue;
                                }
                            }
                            if (onduty_end_4.length() > 0) {
                                Date classdate = DateUtils.getDate(onduty_end_4, "yyyy.MM.dd HH:mm");
                                if (carddate.before(classdate)) {
                                    ldb.set("infomsg", absent);
                                    onduty_yes_4 = true;
                                    continue;
                                }
                            }

                        }
                    }

                }
                // 第四时间段下班刷卡
                if (!offduty_yes_4 && "1".equals(offduty_card_4) && !"1".equals(inoutflag)) {
                    if (!StringUtils.isEmpty(offduty_start_4) && !StringUtils.isEmpty(offduty_end_4)) {
                        Date offduty_start_date4 = DateUtils.getDate(offduty_start_4, "yyyy.MM.dd HH:mm");
                        Date offduty_end_date4 = DateUtils.getDate(offduty_end_4, "yyyy.MM.dd HH:mm");
                        // 下班刷卡点 必须在下班刷卡起 至 下班刷卡止 之间的时间段，否则进行下个时间段的校验
                        if (!carddate.after(offduty_end_date4) && !carddate.before(offduty_start_date4)) {
                            if (offduty_4.length() > 0) {
                                Date classdate = DateUtils.getDate(offduty_4, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_4.length() > 0 && flextimeLen > 0 && classLen == 4) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate)) {
                                    long timedelta = carddate.getTime() - classdate.getTime();
                                    if (timedelta != 0 && timedelta / 60 > 30) {
                                        ldb.set("infomsg", leave_late);
                                    } else {
                                        ldb.set("infomsg", normal);
                                    }
                                    if (offduty4 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty4);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early4 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early4);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early_absent4 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent4);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start4 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start4);
                                        ldb1.set("infomsg", "");
                                    }
                                    offduty4 = i;
                                    // offduty_yes_4 = true;
                                    continue;
                                }
                            }
                            if (leave_early_4.length() > 0) {
                                Date classdate = DateUtils.getDate(leave_early_4, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_4.length() > 0 && flextimeLen > 0 && classLen == 4) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate) && !classdate.equals(carddate)) {
                                    ldb.set("infomsg", normal);
                                    if (leave_early4 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early4);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (leave_early_absent4 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent4);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start4 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start4);
                                        ldb1.set("infomsg", "");
                                    }
                                    leave_early4 = i;
                                    // offduty_yes_4 = true;
                                    continue;
                                }
                            }
                            if (leave_early_absent_4.length() > 0) {
                                Date classdate = DateUtils.getDate(leave_early_absent_4, "yyyy.MM.dd HH:mm");
                                if (offduty_flextime_4.length() > 0 && flextimeLen > 0 && classLen == 4) {
                                    long timeLen = classdate.getTime() + flextimeLen;
                                    classdate.setTime(timeLen);
                                }
                                if (!carddate.before(classdate) && !classdate.equals(carddate)) {
                                    ldb.set("infomsg", early);
                                    if (leave_early_absent4 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(leave_early_absent4);
                                        ldb1.set("infomsg", "");
                                    }
                                    if (offduty_start4 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start4);
                                        ldb1.set("infomsg", "");
                                    }
                                    leave_early_absent4 = i;
                                    // offduty_yes_4 = true;
                                    continue;
                                }
                            }
                            if (offduty_start_4.length() > 0) {
                                Date classdate = DateUtils.getDate(offduty_start_4, "yyyy.MM.dd HH:mm");
                                if (!carddate.before(classdate)) {
                                    ldb.set("infomsg", absent);
                                    if (offduty_start4 != -1) {
                                        LazyDynaBean ldb1 = (LazyDynaBean) cardlist.get(offduty_start4);
                                        ldb1.set("infomsg", "");
                                    }
                                    offduty_start4 = i;
                                    // offduty_yes_4 = true;
                                    continue;
                                }
                            }
                        }
                    }

                }

            }
            hm.put("classname", className);
            hm.put("classtime", classDateList);
            hm.put("cardinfo", cardlist);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return hm;
    }

    private ArrayList<LazyDynaBean> getInitDailyInfo(String fromDate, String toDate) {
        ArrayList<LazyDynaBean> dailyInfo = new ArrayList<LazyDynaBean>();
        String pattern = "yyyy.MM.dd";
        Date dFromDate = DateUtils.getDate(fromDate, pattern);
        Date dToDate = DateUtils.getDate(toDate, pattern);
        int days = DateUtils.dayDiff(dFromDate, dToDate);
        for (int i = 0; i < days + 1; i++) {
            String aDate = DateUtils.format(DateUtils.addDays(dFromDate, i), pattern);
            LazyDynaBean infoBean = new LazyDynaBean();
            infoBean.set("date", aDate);
            infoBean.set("state", "0");// =0 正常；=1 异常(迟到、早退、旷工)
            infoBean.set("leave", "0");// =0 正常；=1请假
            infoBean.set("overtime", "0");// =0 正常；=1加班
            infoBean.set("outwork", "0");// =0 正常；=1公出
            infoBean.set("rest", "0");// =0 正常；=1休息(应出勤为0)
            infoBean.set("normal", "0");// =0 ；=1(实出勤>=应出勤)
            infoBean.set("spflag", "0");// =0 ；=1已报批
            dailyInfo.add(infoBean);
        }

        return dailyInfo;
    }

    private String getOverTimeForLeaveFld() {
        String fld = "";

        KqItem kqItem = new KqItem(this.conn);
        fld = kqItem.getFieldIdByKqItemDesc("调休加班");
        if (!"".equalsIgnoreCase(fld)) {
            FieldItem item = DataDictionary.getFieldItem(fld, "Q03");
            // 未构库
            if (null == item || "0".equalsIgnoreCase(item.getUseflag()))
                fld = "";
        }
        return fld;
    }

    private HashMap getOverTimeForLeaveKqItem() throws GeneralException {
        HashMap hm = null;

        String overtimeForLeave = getOverTimeForLeaveFld();
        if ("".equals(overtimeForLeave))
            return hm;

        RowSet rSet = null;
        try {
            String itemid = "";
            rSet = dao.search(
                    "SELECT item_id FROM kq_item WHERE upper(fielditemid)='" + overtimeForLeave.toUpperCase() + "'");
            if (rSet.next()) {
                itemid = rSet.getString("item_id");
                if (null == itemid || "".equals(itemid))
                    return hm;
            }

            AnnualApply annualApply = new AnnualApply(this.userView, this.conn);
            hm = annualApply.count_Leave(itemid);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rSet);
        }

        return hm;
    }

    private void LoadDailyException(ArrayList<LazyDynaBean> dailyInfo, String fromDate, String toDate)
            throws GeneralException {

        HashMap map = getExceptionIndex();
        String absenteeism = (String) map.get("25");
        String late = (String) map.get("21");
        String early = (String) map.get("23");
        StringBuffer excepsql = new StringBuffer();
        // 59342 完善校验
        boolean bool = false;
        if (StringUtils.isNotEmpty(absenteeism)) {
        	excepsql.append(absenteeism).append(">0 ");
        	bool = true;
        }
        
        if (StringUtils.isNotEmpty(late)) {
        	if(bool)
        		excepsql.append(" or ");
        	excepsql.append(late).append(">0 ");
        	bool = true;
        }
        
        if (StringUtils.isNotEmpty(early)) {
        	if(bool)
        		excepsql.append(" or ");
        	excepsql.append(early).append(" >0 ");
        }

        String rest = (String) map.get("rest");
        StringBuffer restsql = new StringBuffer();
        StringBuffer normalsql = new StringBuffer();
        if (StringUtils.isEmpty(rest)) {

        } else {
            if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
            	// 60356 兼容休息班次 =0 
                restsql.append("("+rest+ " is null or " +rest+"=0)");
            } else {
                restsql.append(Sql_switcher.isnull(rest, "0") + "=0 ");
            }
            
            String normal = Sql_switcher.isnull((String) map.get("normal"), "0");
            String normalUnit = (String)map.get("normalunit");
            
            rest = Sql_switcher.isnull(rest, "0");
            String restUnit = (String)map.get("restunit");
            
            //应出勤、实出勤单位一致，直接比较
            if (normalUnit.equals(restUnit)) {
                normalsql.append(normal).append(">=").append(rest);
            } else {
                String standardWorkHour = KqParam.getInstance().getSTANDARD_HOURS();
                
                //应出勤、实出勤单位不一致，转换后比较
 
                //实出勤单位为是天
                if (KqConstant.Unit.DAY.equals(normalUnit) || KqConstant.Unit.TIMES.equals(normalUnit)) {
                    normalsql.append(normal).append(">=1.0");
                } else if (KqConstant.Unit.HOUR.equals(normalUnit)) {
                    if (KqConstant.Unit.DAY.equals(restUnit) || KqConstant.Unit.TIMES.equals(restUnit)) {
                        //转化成小时比较
                        normalsql.append(normal).append(">=").append(rest).append("*").append(standardWorkHour);
                    } else {
                        normalsql.append(normal).append(">=").append(rest).append("/").append(standardWorkHour); 
                    }
                } else if (KqConstant.Unit.MINUTE.equals(normalUnit)) {
                    if (KqConstant.Unit.DAY.equals(restUnit) || KqConstant.Unit.TIMES.equals(restUnit)) {
                        //转化成分钟比较
                        normalsql.append(normal).append(">=").append(rest).append("*60.0*").append(standardWorkHour);
                    } else {
                        normalsql.append(normal).append(">=").append(rest).append("*60.0"); 
                    }
                }
            }
        }

        ArrayList leavelist = (ArrayList) map.get("leave");
        StringBuffer leavesql = new StringBuffer();
        String leave = "";
        for (int i = 0; i < leavelist.size(); i++) {
            leave = (String) leavelist.get(i);
            if (StringUtils.isEmpty(leave))
                continue;

            if (i == 0)
                leavesql.append(" " + leave + ">0 ");
            else
                leavesql.append(" or " + leave + ">0 ");

        }

        ArrayList overtimelist = (ArrayList) map.get("overtime");
        StringBuffer overtimesql = new StringBuffer();
        String overtime = "";
        for (int i = 0; i < overtimelist.size(); i++) {
            overtime = (String) overtimelist.get(i);
            if (StringUtils.isEmpty(overtime))
                continue;

            if (i == 0)
                overtimesql.append(" " + overtime + ">0 ");
            else
                overtimesql.append(" or " + overtime + ">0 ");

        }
        // zxj 增加判断调休加班
        String overtimeForLeave = getOverTimeForLeaveFld();
        if (!"".equalsIgnoreCase(overtimeForLeave)) {
            if ("".equalsIgnoreCase(overtimesql.toString()))
                overtimesql.append(" " + overtimeForLeave + ">0 ");
            else
                overtimesql.append(" or " + overtimeForLeave + ">0 ");
        }

        ArrayList outworklist = (ArrayList) map.get("outwork");
        StringBuffer outworksql = new StringBuffer();
        String outwork = "";
        for (int i = 0; i < outworklist.size(); i++) {
            outwork = (String) outworklist.get(i);
            if (StringUtils.isEmpty(outwork))
                continue;

            if (i == 0)
                outworksql.append(" " + outwork + ">0 ");
            else
                outworksql.append(" or " + outwork + ">0 ");
        }

        StringBuffer sql1 = new StringBuffer();
        StringBuffer sql2 = new StringBuffer();
        sql1.append("SELECT Q03Z0");
        sql1.append(" FROM Q03");
        sql1.append(" WHERE nbase=?");
        sql1.append(" AND a0100=?");
        sql1.append(" AND Q03Z0>=?");
        sql1.append(" AND Q03Z0<=?");
        sql1.append(" AND ( ");

        sql2.append(" )");
        sql2.append(" ORDER BY Q03Z0");

        ArrayList params = new ArrayList();
        params.add(nbase);
        params.add(A0100);
        params.add(fromDate);
        params.add(toDate);

        RowSet rs = null;
        try {
            Date dFromDate = DateUtils.getDate(fromDate, "yyyy.MM.dd");
            String q03z0 = "";
            if (StringUtils.isNotEmpty(excepsql.toString())) {
                rs = dao.search(sql1.toString() + excepsql.toString() + sql2.toString(), params);
                while (rs.next()) {
                    q03z0 = rs.getString("Q03Z0");
                    updateInfoToList(dailyInfo, dFromDate, q03z0, "state", "1");// 异常否
                }
            }
            if (StringUtils.isNotEmpty(leavesql.toString())) {
                rs = dao.search(sql1.toString() + leavesql.toString() + sql2.toString(), params);
                while (rs.next()) {
                    q03z0 = rs.getString("Q03Z0");
                    updateInfoToList(dailyInfo, dFromDate, q03z0, "leave", "1");// 请假
                }
            }
            if (StringUtils.isNotEmpty(overtimesql.toString())) {
                rs = dao.search(sql1.toString() + overtimesql.toString() + sql2.toString(), params);
                while (rs.next()) {
                    q03z0 = rs.getString("Q03Z0");
                    updateInfoToList(dailyInfo, dFromDate, q03z0, "overtime", "1");// 加班
                }
            }
            if (StringUtils.isNotEmpty(outworksql.toString())) {
                // 38841 之前公出期间有请假加班不显示公出状态先注释，现在全部都显示应有的出勤状态
                // // 获取公出时应筛选出期间的请假或加班
                // StringBuffer outworSql = new
                // StringBuffer(sql1.toString()+outworksql.toString());
                // // 增加去除请假和加班的条件
                // outworSql.append(") and Q03Z0 not in (").append(sql1.toString());
                // outworSql.append(leavesql.toString()).append(" or
                // ").append(overtimesql.toString()).append(")");
                // outworSql.append(sql2.toString());
                // ArrayList outworkParams = new ArrayList();
                // outworkParams.add(nbase);
                // outworkParams.add(A0100);
                // outworkParams.add(fromDate);
                // outworkParams.add(toDate);
                // outworkParams.add(nbase);
                // outworkParams.add(A0100);
                // outworkParams.add(fromDate);
                // outworkParams.add(toDate);
                // rs = dao.search(outworSql.toString(), outworkParams);

                rs = dao.search(sql1.toString() + outworksql.toString() + sql2.toString(), params);
                while (rs.next()) {
                    q03z0 = rs.getString("Q03Z0");
                    updateInfoToList(dailyInfo, dFromDate, q03z0, "outwork", "1");// 公出
                }
            }
            if (StringUtils.isNotEmpty(restsql.toString())) {
                rs = dao.search(sql1.toString() + restsql.toString() + sql2.toString(), params);
                while (rs.next()) {
                    q03z0 = rs.getString("Q03Z0");
                    updateInfoToList(dailyInfo, dFromDate, q03z0, "rest", "1");// 休息（应出勤为空）
                }
            }
            if (StringUtils.isNotEmpty(normalsql.toString())) {
                rs = dao.search(sql1.toString() + normalsql.toString() + sql2.toString(), params);
                while (rs.next()) {
                    q03z0 = rs.getString("Q03Z0");
                    updateInfoToList(dailyInfo, dFromDate, q03z0, "normal", "1");// 实出勤>=应出勤
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 给 已报批的申请单据 的日期增加标识 spflag=‘1’
     * 
     * @param dailyInfo
     * @param fromDate
     * @param toDate
     */
    private void LoadDailyAppState(ArrayList<LazyDynaBean> dailyInfo, String fromDate, String toDate)
            throws GeneralException {

        String fromDateSql = Sql_switcher.dateValue(fromDate + " 00:00:00");
        String toDateSql = Sql_switcher.dateValue(toDate + " 23:59:59");

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT Z1,Z3");// ,Z5
        sql.append(" FROM (");
        sql.append("SELECT Q15Z1 as Z1,Q15Z3 as Z3");// ,Q15Z5 as Z5
        sql.append(" FROM Q15");
        sql.append(" WHERE nbase=?");
        sql.append(" AND a0100=?");
        sql.append(" AND (");
        sql.append(" (Q15Z1").append(">=").append(fromDateSql);
        sql.append(" AND Q15Z1").append("<=").append(toDateSql).append(")");
        sql.append(" OR (Q15Z3").append(">=").append(fromDateSql);
        sql.append(" AND Q15Z3").append("<=").append(toDateSql).append(")");
        sql.append(" OR (Q15Z1").append("<").append(fromDateSql);
        sql.append(" AND Q15Z3").append(">").append(toDateSql).append(")");
        sql.append(")");
        // sql.append(" AND Q15Z5 IN ('02','03')");
        sql.append(" AND Q15Z5 ='02' ");
        sql.append(" UNION ALL ");
        sql.append("SELECT Q13Z1 as Z1,Q13Z3 as Z3");// ,Q13Z5 as Z5
        sql.append(" FROM Q13");
        sql.append(" WHERE nbase=?");
        sql.append(" AND a0100=?");
        sql.append(" AND (");
        sql.append(" (Q13Z1").append(">=").append(fromDateSql);
        sql.append(" AND Q13Z1").append("<=").append(toDateSql).append(")");
        sql.append(" OR (Q13Z3").append(">=").append(fromDateSql);
        sql.append(" AND Q13Z3").append("<=").append(toDateSql).append(")");
        sql.append(" OR (Q13Z1").append("<").append(fromDateSql);
        sql.append(" AND Q13Z3").append(">").append(toDateSql).append(")");
        sql.append(")");
        // sql.append(" AND Q13Z5 IN ('02','03')");
        sql.append(" AND Q13Z5 ='02' ");
        sql.append(" UNION ALL ");
        sql.append("SELECT Q11Z1 as Z1,Q11Z3 as Z3");// ,Q11Z5 as Z5
        sql.append(" FROM Q11");
        sql.append(" WHERE nbase=?");
        sql.append(" AND a0100=?");
        sql.append(" AND (");
        sql.append(" (Q11Z1").append(">=").append(fromDateSql);
        sql.append(" AND Q11Z1").append("<=").append(toDateSql).append(")");
        sql.append(" OR (Q11Z3").append(">=").append(fromDateSql);
        sql.append(" AND Q11Z3").append("<=").append(toDateSql).append(")");
        sql.append(" OR (Q11Z1").append("<").append(fromDateSql);
        sql.append(" AND Q11Z3").append(">").append(toDateSql).append(")");
        sql.append(")");
        // sql.append(" AND Q11Z5 IN ('02','03')");
        sql.append(" AND Q11Z5 ='02' ");
        sql.append(") A");
        sql.append(" ORDER BY Z1");

        ArrayList params = new ArrayList();
        params.add(nbase);
        params.add(A0100);
        params.add(nbase);
        params.add(A0100);
        params.add(nbase);
        params.add(A0100);

        RowSet rs = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
            Date dFromDate = DateUtils.getDate(fromDate, "yyyy.MM.dd");
            Date appFromDate = null;
            Date appToDate = null;
            Date startDate = null;
            String curDate = "";
            String state = "0"; // =1已报批
            rs = dao.search(sql.toString(), params);
            while (rs.next()) {
                appFromDate = rs.getTimestamp("Z1");
                appToDate = rs.getTimestamp("Z3");
                startDate = df.parse(df.format(appFromDate));
                int days = DateUtils.dayDiff(appFromDate, appToDate);
                for (int i = 0; i <= days; i++) {
                    curDate = DateUtils.FormatDate(DateUtils.addDays(startDate, i), "yyyy.MM.dd");
                    state = "1";

                    updateInfoToList(dailyInfo, dFromDate, curDate, "spflag", state);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }

    private void LoadDailyCards(ArrayList<LazyDynaBean> dailyInfo, String nbase, String a0100, String fromDate,
            String toDate) throws GeneralException {
        String pattern = "yyyy.MM.dd";
        Date dFromDate = DateUtils.getDate(fromDate, pattern);

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT work_date, work_time");
        sql.append(" FROM kq_originality_data");
        sql.append(" WHERE nbase=?");
        sql.append(" AND a0100=?");
        sql.append(" AND work_date>=?");
        sql.append(" AND work_date<=?");
        sql.append(" ORDER BY work_date, work_time");

        ArrayList params = new ArrayList();
        params.add(nbase);
        params.add(a0100);
        params.add(fromDate);
        params.add(toDate);

        RowSet rs = null;
        try {
            String preWorkDate = "";
            String preWorkTime = "";
            String dayCards = "";
            rs = dao.search(sql.toString(), params);
            while (rs.next()) {
                String workDate = rs.getString("work_date").replaceAll(".", "-");
                if (!"".equals(preWorkDate) && !workDate.equals(preWorkDate)) {
                    if (!"".equals(dayCards))
                        dayCards = dayCards + "," + preWorkTime;
                    else
                        dayCards = preWorkTime;

                    updateInfoToList(dailyInfo, dFromDate, workDate, "cards", dayCards);
                }

                String workTime = rs.getString("work_time");
                if ("".equals(dayCards))
                    dayCards = workTime;

                preWorkDate = workDate;
                preWorkTime = workTime;

                if (rs.isLast()) {
                    if (!"".equals(dayCards))
                        dayCards = dayCards + "," + preWorkTime;
                    else
                        dayCards = preWorkTime;

                    updateInfoToList(dailyInfo, dFromDate, workDate, "cards", dayCards);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }

    private void updateInfoToList(ArrayList<LazyDynaBean> dailyInfo, Date dFromDate, String workDate, String infokey,
            String infoValue) {
        Date curDate = DateUtils.getDate(workDate, "yyyy.MM.dd");
        int dayIndex = DateUtils.dayDiff(dFromDate, curDate);
        // 防止超出该考勤期间的出勤情况
        if (dayIndex >= 0 && dayIndex < dailyInfo.size()) {
            LazyDynaBean infoBean = (LazyDynaBean) dailyInfo.get(dayIndex);
            infoBean.set(infokey, infoValue);
        }
    }

    // 获取异常情况 的指标名
    private HashMap getExceptionIndex() throws GeneralException {
        HashMap map = new HashMap();

        StringBuffer fieldValidWhr = new StringBuffer(" AND fielditemid is not null");
        if (Sql_switcher.searchDbServer() == Constant.MSSQL)
            fieldValidWhr.append(" AND fielditemid<>'' ");
        // 增加指标校验为q03表并且构库
        // 33056 查询考勤规则对应指标时大小写不一致问题，引起日历显示的状态颜色错误
        fieldValidWhr.append(
                " AND upper(fielditemid) in (select upper(itemid) from t_hr_busifield where fieldSetid='Q03' and useflag='1')");

        String absenteeism = "";// 旷工
        String absenteeismSql = "SELECT item_unit, fielditemid FROM kq_item WHERE item_id='25'" + fieldValidWhr;

        String late = "";// 迟到
        String lateSql = "SELECT item_unit, fielditemid FROM kq_item WHERE item_id='21'" + fieldValidWhr;

        String early = "";// 早退
        String earlySql = "SELECT item_unit, fielditemid FROM kq_item WHERE item_id='23'" + fieldValidWhr;
        // 请假
        ArrayList leavelist = new ArrayList();
        String leave = "SELECT item_unit, fielditemid FROM kq_item WHERE item_id like '0%'" + fieldValidWhr;
        // 加班
        ArrayList overtimelist = new ArrayList();
        String overtime = "SELECT item_unit, fielditemid FROM kq_item WHERE item_id like '1%'" + fieldValidWhr;
        // 公出
        ArrayList outworklist = new ArrayList();
        String outwork = "SELECT item_unit, fielditemid FROM kq_item WHERE item_id like '3%'" + fieldValidWhr;

        String rest = "";// 休息(应出勤指标为空是休息)
        String restUnit = "";
        String restsql = "SELECT item_unit, fielditemid FROM kq_item WHERE item_id='27'" + fieldValidWhr;

        String normal = "";// 实出勤指标
        String normalUnit = "";
        String normalsql = "SELECT item_unit, fielditemid FROM kq_item WHERE item_id='28'" + fieldValidWhr;

        RowSet rs = null;
        try {
            rs = dao.search(absenteeismSql);
            if (rs.next()) {
                absenteeism = rs.getString("fielditemid");
            }

            rs = dao.search(lateSql);
            if (rs.next()) {
                late = rs.getString("fielditemid");
            }

            rs = dao.search(earlySql);
            if (rs.next()) {
                early = rs.getString("fielditemid");
            }

            rs = dao.search(leave);
            while (rs.next()) {
                String leaveindex = rs.getString("fielditemid");
                leavelist.add(leaveindex);
            }

            rs = dao.search(overtime);
            while (rs.next()) {
                String overindex = rs.getString("fielditemid");
                overtimelist.add(overindex);
            }

            rs = dao.search(outwork);
            while (rs.next()) {
                String outindex = rs.getString("fielditemid");
                outworklist.add(outindex);
            }

            rs = dao.search(restsql);
            if (rs.next()) {
                rest = rs.getString("fielditemid");
                restUnit = rs.getString("item_unit");
            }

            rs = dao.search(normalsql);
            if (rs.next()) {
                normal = rs.getString("fielditemid");
                normalUnit = rs.getString("item_unit");
            }

            map.put("25", absenteeism);
            map.put("21", late);
            map.put("23", early);
            map.put("leave", leavelist);
            map.put("overtime", overtimelist);
            map.put("outwork", outworklist);
            map.put("rest", rest);
            map.put("restunit", restUnit);
            map.put("normal", normal);
            map.put("normalunit", normalUnit);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    public ArrayList<HashMap<String, String>> getPersonHoliday(String kqYear) throws GeneralException {
        ArrayList<HashMap<String, String>> holidays = new ArrayList<HashMap<String, String>>();

        KqAppInterface kqAppInterface = new KqAppInterface(conn, userView);
        String b0110 = kqAppInterface.getB0110ByEmpInfo(nbase, A0100);
        // 从Q17中取当前人员的所有假类的天数（已休、可休）
        String types = KqParam.getInstance().getHolidayTypes(conn, b0110);
        String[] typesArr = types.split(",");
        StringBuffer quotedTypes = new StringBuffer();
        for (int i = 0; i < typesArr.length; i++) {
            String type = typesArr[i].trim();
            if ("".equals(type))
                continue;

            quotedTypes.append("'").append(type).append("'");
            if (i < (typesArr.length - 1))
                quotedTypes.append(",");
        }

        String last_balance = KqUtilsClass.getFieldByDesc("q17", "上年结余");
        String last_balance_Time = KqUtilsClass.getFieldByDesc("q17", "结余截止日期");
        String last_spare = KqUtilsClass.getFieldByDesc("q17", "结余剩余");

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM Q17");
        sql.append(" WHERE q1701=?");
        sql.append(" AND nbase=?");
        sql.append(" AND a0100=?");
        sql.append(" AND q1709 IN (");
        sql.append(quotedTypes.toString());
        sql.append(")");

        ArrayList<String> sqlParam = new ArrayList<String>();
        sqlParam.add(kqYear);
        sqlParam.add(nbase);
        sqlParam.add(A0100);

        String curDate = DateUtils.format(new Date(), "yyyy-MM-dd");

        AnnualApply annualApply = new AnnualApply(this.userView, this.conn);
        RowSet rs = null;
        try {
            rs = this.dao.search(sql.toString(), sqlParam);
            while (rs.next()) {
                HashMap kqItem_hash = annualApply.count_Leave(rs.getString("q1709"));
                kqItem_hash.put("item_unit", KqConstant.Unit.DAY);
                String appDesc = (String) kqItem_hash.get("item_name");
                String unit = (String) kqItem_hash.get("item_unit");
                String unitDesc = "";
                if (KqConstant.Unit.DAY.equals(unit))
                    unitDesc = KqConstant.Unit.DAY_DESC;
                else if (KqConstant.Unit.HOUR.equals(unit))
                    unitDesc = KqConstant.Unit.HOUR_DESC;
                else if (KqConstant.Unit.MINUTE.equals(unit))
                    unitDesc = KqConstant.Unit.MINUTE_DESC;
                else
                    unitDesc = KqConstant.Unit.TIMES_DESC;

                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("type", rs.getString("q1709"));
                hm.put("name", appDesc);
                hm.put("unit", unitDesc);

                float used = rs.getFloat("q1705");
                float remain = rs.getFloat("q1707");
                float lastYesrUsed = 0;
                // 处理结余问题
                if (!"".equals(last_balance) && !"".equals(last_spare) && !"".equals(last_balance_Time)) {
                    String lastBalanceDate = "";
                    if (rs.getDate(last_balance_Time) != null) {
                        lastBalanceDate = DateUtils.FormatDate(rs.getDate(last_balance_Time));
                        // 结余截止日期大于等于当天时，天数中需包含结余
                        if (lastBalanceDate.compareTo(curDate) >= 0) {
                            remain = remain + rs.getFloat(last_spare);
                            used = used + rs.getFloat(last_balance) - rs.getFloat(last_spare);
                        }
                        lastYesrUsed = rs.getFloat(last_balance) - rs.getFloat(last_spare);
                    }
                }
                hm.put("used", Float.toString(used));
                hm.put("remain", Float.toString(remain));
                hm.put("flag", "1");
                hm.put("lastYesrUsed", Float.toString(lastYesrUsed));

                holidays.add(hm);
            }
            // 2 get other leave info
            ArrayList<Date> yearScope = RegisterDate.getKqYearScope(this.conn, kqYear);
            Date yearStart = null;
            Date yearEnd = null;
            if (yearScope != null && yearScope.size() == 2) {
                yearStart = yearScope.get(0);
                yearEnd = yearScope.get(1);
            }

            if (yearStart == null || yearEnd == null)
                return holidays;

            String strFrom = DateUtils.format(yearStart, "yyyy-MM-dd") + " 00:00:00";
            String strTo = DateUtils.format(yearEnd, "yyyy-MM-dd") + " 23:59:59";
            String fromDateSql = Sql_switcher.dateValue(strFrom);
            String toDateSql = Sql_switcher.dateValue(strTo);
            ArrayList list = new ArrayList();
            list.add(nbase);
            list.add(A0100);
            list.add(nbase);
            list.add(A0100);
            // 54524 过滤年假等 并增加排序
            String whereSql = " and Q15Z5='03'  and Q1503 not in(" + quotedTypes.toString() + ")";
            // 增加销假计算
            String q15sql = getAppDataSql("Q15", fromDateSql, toDateSql, whereSql);
            q15sql += " ORDER BY qstate, qZ1";

            String curYear = DateUtils.format(new Date(), "yyyy");
            String leaveType = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();

            HashMap<String, String> leaveForOverTimeHM = null;
            // 当前年假时，取调休假（加班）信息，并放入假期列表
            if (kqYear.equals(curYear)) {
                leaveForOverTimeHM = this.getLeaveForOvertimHm(nbase, A0100, b0110, kqAppInterface, annualApply);
                if (leaveForOverTimeHM != null)
                    holidays.add(leaveForOverTimeHM);
            }

            String preType = "";
            String curType = "";
            double timeLen = 0;
            String timeLenItem = this.getCustomTimeLen("Q15");

            HashMap<String, String> hm = null;
            HashMap kqItem_hash = null;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            rs = this.dao.search(q15sql, list);
            while (rs.next()) {
                curType = rs.getString("qstate");
                if (!curType.equals(preType)) {
                    if (!"".equals(preType)) {
                        hm.put("used", Double.toString(timeLen));
                        holidays.add(hm);
                        timeLen = 0;
                    }

                    kqItem_hash = annualApply.count_Leave(curType);
                    String appDesc = (String) kqItem_hash.get("item_name");
                    String unit = (String) kqItem_hash.get("item_unit");
                    String unitDesc = "";
                    if (KqConstant.Unit.DAY.equals(unit))
                        unitDesc = KqConstant.Unit.DAY_DESC;
                    else if (KqConstant.Unit.HOUR.equals(unit))
                        unitDesc = KqConstant.Unit.HOUR_DESC;
                    else if (KqConstant.Unit.MINUTE.equals(unit))
                        unitDesc = KqConstant.Unit.MINUTE_DESC;
                    else
                        unitDesc = KqConstant.Unit.TIMES_DESC;

                    // 当年调休假
                    if (leaveType.equals(curType) && kqYear.equals(curYear)) {
                        hm = leaveForOverTimeHM;
                    }

                    if (hm == null) {
                        hm = new HashMap<String, String>();
                        hm.put("type", curType);
                        hm.put("name", appDesc);
                        hm.put("flag", "0");
                        hm.put("remain", "");
                        hm.put("unit", unitDesc);
                    }
                }
                // 申请单据
                Date qFromDate = rs.getTimestamp("qZ1");
                Date qToDate = rs.getTimestamp("qZ3");
                if (StringUtils.isBlank(timeLenItem)) {
                    timeLen = timeLen + annualApply.calcLeaveAppTimeLen(this.nbase, this.A0100, rs.getString("b0110"),
                            qFromDate, qToDate, kqItem_hash, null, Integer.MAX_VALUE);
                } else {
                    timeLen = timeLen + rs.getFloat("customtimelen");
                }
                // 销假单据
                Date xFromDate = rs.getTimestamp("xZ1");
                Date xToDate = rs.getTimestamp("xZ3");
                if (null != xFromDate && null != xToDate) {
                    if (StringUtils.isBlank(timeLenItem)) {
                        timeLen = timeLen - annualApply.calcLeaveAppTimeLen(this.nbase, this.A0100,
                                rs.getString("b0110"), xFromDate, xToDate, kqItem_hash, null, Integer.MAX_VALUE);
                    } else {
                        timeLen = timeLen - rs.getFloat("xcustomtimelen");
                    }
                }
                preType = curType;
            }

            if (hm != null) {
                hm.put("used", Double.toString(timeLen));
                // 不是当年调休假的，需要放入holidays；当年的调休假已提前放入，避免没请假不去调休加班时长的问题
                if (!leaveType.equals(curType) || !kqYear.equals(curYear))
                    holidays.add(hm);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return holidays;
    }

    private HashMap<String, String> getLeaveForOvertimHm(String nbase, String a0100, String b0110,
            KqAppInterface kqAppInterface, AnnualApply annualApply) {
        String curYear = DateUtils.format(new Date(), "yyyy");
        String leaveType = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
        if (StringUtils.isBlank(leaveType))
            return null;

        HashMap kqItem_hash;
        try {
            kqItem_hash = annualApply.count_Leave(leaveType);
        } catch (GeneralException e1) {
            e1.printStackTrace();
            return null;
        }
        String appDesc = (String) kqItem_hash.get("item_name");
        String unit = (String) kqItem_hash.get("item_unit");
        String unitDesc = "";
        if (KqConstant.Unit.DAY.equals(unit))
            unitDesc = KqConstant.Unit.DAY_DESC;
        else if (KqConstant.Unit.HOUR.equals(unit))
            unitDesc = KqConstant.Unit.HOUR_DESC;
        else if (KqConstant.Unit.MINUTE.equals(unit))
            unitDesc = KqConstant.Unit.MINUTE_DESC;
        else
            unitDesc = KqConstant.Unit.TIMES_DESC;

        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put("type", leaveType);
        hm.put("name", appDesc);
        hm.put("flag", "0");
        hm.put("remain", "0");
        hm.put("unit", unitDesc);
        hm.put("used", "0");

        LazyDynaBean appInfo = new LazyDynaBean();
        appInfo.set("type", leaveType);
        appInfo.set("nbase", nbase);
        appInfo.set("a0100", a0100);
        appInfo.set("b0110", b0110);


        // 调休假和年度本身没有关系，当年取可休天数即可
        try {
            hm.put("remain", Double.toString(kqAppInterface.getAppCanUseDays(appInfo)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hm;
    }

    /**
     * 获取人员在某日期范围内某项申请的明细
     * 
     * @param appType
     * @param fromDate
     * @param toDate
     */
    public ArrayList<HashMap<String, String>> getPersonAppDetail(String appType, Date fromDate, Date toDate)
            throws GeneralException {
        ArrayList<HashMap<String, String>> appDetail = new ArrayList<HashMap<String, String>>();

        if (null == appDetail || "".equals(appType.trim()))
            return appDetail;

        appType = appType.trim();

        String tab = "";
        if (appType.startsWith("0"))
            tab = "Q15";
        else if (appType.startsWith("1"))
            tab = "Q11";
        else if (appType.startsWith("3"))
            tab = "Q13";
        else
            return appDetail;

        String strFrom = DateUtils.format(fromDate, "yyyy-MM-dd") + " 00:00:00";
        String strTo = DateUtils.format(toDate, "yyyy-MM-dd") + " 23:59:59";

        RowSet rs = null;

        StringBuffer sql = new StringBuffer("SELECT * FROM (");
        // 请假单
        sql.append(" SELECT * FROM ").append(tab);
        sql.append(" WHERE ").append(tab).append("03").append("=?");
        sql.append(" AND nbase=?");
        sql.append(" AND a0100=?");
        sql.append(" AND ").append(tab).append("Z5='03'");
        if ("Q15".equalsIgnoreCase(tab))
            sql.append(" AND ").append(tab).append("19 IS NULL");
        sql.append(" AND ((").append(tab).append("Z1").append(">=").append(Sql_switcher.dateValue(strFrom));
        sql.append(" AND ").append(tab).append("Z1").append("<=").append(Sql_switcher.dateValue(strTo)).append(")");
        sql.append(" OR (").append(tab).append("Z3").append(">=").append(Sql_switcher.dateValue(strFrom));
        sql.append(" AND ").append(tab).append("Z3").append("<=").append(Sql_switcher.dateValue(strTo)).append(")");
        sql.append(" OR (").append(tab).append("Z1").append("<").append(Sql_switcher.dateValue(strFrom));
        sql.append(" AND ").append(tab).append("Z3").append(">").append(Sql_switcher.dateValue(strTo)).append(")");
        sql.append(")");
        if ("Q15".equalsIgnoreCase(tab)) {
            // 销假单
            sql.append(" UNION ALL");
            sql.append(" SELECT * FROM ").append(tab);
            sql.append(" WHERE ").append(tab).append("03").append("=?");
            sql.append(" AND nbase=?");
            sql.append(" AND a0100=?");
            sql.append(" AND ").append(tab).append("Z5='03'");
            sql.append(" AND ").append(tab).append("19 IS NOT NULL");
            sql.append(" AND Q1519 IN (SELECT Q1501 FROM Q15");
            sql.append(" WHERE ").append(tab).append("03").append("=?");
            sql.append(" AND nbase=?");
            sql.append(" AND a0100=?");
            sql.append(" AND ").append(tab).append("Z5='03'");
            sql.append(" AND ").append(tab).append("19 IS NULL");
            sql.append(" AND ((").append(tab).append("Z1").append(">=").append(Sql_switcher.dateValue(strFrom));
            sql.append(" AND ").append(tab).append("Z1").append("<=").append(Sql_switcher.dateValue(strTo)).append(")");
            sql.append(" OR (").append(tab).append("Z3").append(">=").append(Sql_switcher.dateValue(strFrom));
            sql.append(" AND ").append(tab).append("Z3").append("<=").append(Sql_switcher.dateValue(strTo)).append(")");
            sql.append(")");
            sql.append(")");
        }
        sql.append(") A");
        sql.append(" ORDER BY ").append(tab).append("Z1");

        ArrayList<String> sqlParam = new ArrayList<String>();
        sqlParam.add(appType);
        sqlParam.add(nbase);
        sqlParam.add(A0100);
        sqlParam.add(appType);
        sqlParam.add(nbase);
        sqlParam.add(A0100);
        sqlParam.add(appType);
        sqlParam.add(nbase);
        sqlParam.add(A0100);

        try {
            AnnualApply annualApply = new AnnualApply(this.userView, this.conn);
            float[] holidayRule = annualApply.getHoliday_minus_rule();

            HashMap kqItem_hash = annualApply.count_Leave(appType);
            String appDesc = (String) kqItem_hash.get("item_name");
            String unit = (String) kqItem_hash.get("item_unit");
            String unitDesc = "";
            if (KqConstant.Unit.DAY.equals(unit))
                unitDesc = KqConstant.Unit.DAY_DESC;
            else if (KqConstant.Unit.HOUR.equals(unit))
                unitDesc = KqConstant.Unit.HOUR_DESC;
            else if (KqConstant.Unit.MINUTE.equals(unit))
                unitDesc = KqConstant.Unit.MINUTE_DESC;
            else
                unitDesc = KqConstant.Unit.TIMES_DESC;

            rs = this.dao.search(sql.toString(), sqlParam);
            while (rs.next()) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("id", rs.getString(tab + "01"));
                if ("q15".equalsIgnoreCase(tab))
                    hm.put("oldId", rs.getString(tab + "19") == null ? "" : rs.getString(tab + "19"));
                else
                    hm.put("oldId", "");
                hm.put("type", appType);
                hm.put("typeName", appDesc);

                Date applyTime = rs.getTimestamp(tab + "05");
                Date beginTime = rs.getTimestamp(tab + "z1");
                Date endTime = rs.getTimestamp(tab + "z3");
                // 34953 防止数据错误报错，若日期为null则显示空字符串即可
                hm.put("applyTime", null != applyTime ? DateUtils.format(applyTime, "yyyy-MM-dd HH:mm") : "");
                hm.put("beginTime", null != beginTime ? DateUtils.format(beginTime, "yyyy-MM-dd HH:mm") : "");
                hm.put("endTime", null != endTime ? DateUtils.format(endTime, "yyyy-MM-dd HH:mm") : "");
                hm.put("reason", rs.getString(tab + "07"));
                hm.put("unit", unitDesc);

                float timeCount = 0;
                // 20191120 没有自定义时长指标的按申请单进行计算，否则，取自定义时长指标值
                String customTimeLenItem = getCustomTimeLen(tab);
                if (StringUtils.isBlank(customTimeLenItem)) {
                    // 年假需要按规则计算申请假期时长
                    float[] factHolidayRule = null;
                    if (KqParam.getInstance().isHoliday(this.conn, rs.getString("b0110"), appType))
                        factHolidayRule = holidayRule;

                    timeCount = annualApply.calcLeaveAppTimeLen(this.nbase, this.A0100, rs.getString("b0110"),
                            beginTime, endTime, kqItem_hash, factHolidayRule, Integer.MAX_VALUE);
                } else {
                    timeCount = rs.getFloat(customTimeLenItem);
                }
                hm.put("timeLen", Float.toString(timeCount));

                appDetail.add(hm);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return appDetail;
    }

    /**
     * 获取年度的开始结束日期（假期取定义的有效时间范围，其它的取考勤年度的开始、结束日期）
     * 
     * @param kqYear
     * @param appType
     * @return
     */
    public ArrayList<Date> getKqDateScope(String kqYear, String appType) throws GeneralException {

        ArrayList list = new ArrayList();
        RowSet rs = null;
        try {

            KqAppInterface kqAppInterface = new KqAppInterface(conn, userView);
            String b0110 = kqAppInterface.getB0110ByEmpInfo(nbase, A0100);
            // 判断是否是假期管理中的假类
            boolean isYearHoliday = KqParam.getInstance().isHoliday(this.conn, b0110, appType);
            if (isYearHoliday) {
                // 将传入的请假类型转换为system参数中的假期映射关系中的目标假期类型，因有的假是消耗同一个假期天数的
                String leaveId = KqAppInterface.switchTypeIdFromHolidayMap(appType);
                // 假期
                StringBuffer strsql = new StringBuffer();
                strsql.append("SELECT q17z1,q17z3");
                strsql.append(" FROM q17");
                strsql.append(" where q1701=?");
                strsql.append(" AND nbase=?");
                strsql.append(" AND a0100=?");
                strsql.append(" AND q1709=?");

                ArrayList sqlParam = new ArrayList();
                sqlParam.add(kqYear);
                sqlParam.add(nbase);
                sqlParam.add(A0100);
                sqlParam.add(leaveId);

                rs = dao.search(strsql.toString(), sqlParam);
                if (rs.next()) {
                    Date startDate = rs.getDate("q17z1");
                    Date endDate = rs.getDate("q17z3");
                    list.add(startDate);
                    list.add(endDate);
                }
            } else {
                list = RegisterDate.getKqYearScope(conn, kqYear);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return list;
    }

    public void setNbase(String nbase) {
        this.nbase = nbase;
    }

    public String getNbase() {
        return nbase;
    }

    public void setA0100(String a0100) {
        A0100 = a0100;
    }

    public String getA0100() {
        return A0100;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    public UserView getUserView() {
        return userView;
    }

    /**
     * 获取整个期间内的申请单据
     * 
     * @param fromDate
     * @param toDate
     * @return
     */
    public HashMap getDailyApplyList(String fromDate, String toDate) throws GeneralException {

        HashMap<String, ArrayList<String>> applyData = new HashMap<String, ArrayList<String>>();
        RowSet rs = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            LazyDynaBean ldb = new LazyDynaBean();
            String fromDateSql = Sql_switcher.dateValue(fromDate + " 00:00:00");
            String toDateSql = Sql_switcher.dateValue(toDate + " 23:59:59");
            ArrayList list = new ArrayList();
            list.add(nbase);
            list.add(A0100);
            list.add(nbase);
            list.add(A0100);
            // 请假单据
            String q15sql = getAppDataSql("Q15", fromDateSql, toDateSql, "");
            rs = dao.search(q15sql, list);
            ArrayList q15list = getAppDataList(rs);
            applyData.put("leaveApps", q15list);

            // 公出单据
            String q13sql = getAppDataSql("Q13", fromDateSql, toDateSql, "");
            rs = dao.search(q13sql, list);
            ArrayList q13list = getAppDataList(rs);
            applyData.put("officeleaveApps", q13list);

            // 加班单据
            String q11sql = getAppDataSql("Q11", fromDateSql, toDateSql, "");
            rs = dao.search(q11sql, list);
            ArrayList q11list = getAppDataList(rs);
            applyData.put("overApps", q11list);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return applyData;
    }

    public ArrayList getAppDataList(RowSet rs) throws GeneralException {
        ArrayList list = new ArrayList();
        try {
            LazyDynaBean ldb = new LazyDynaBean();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            while (rs.next()) {
                // 申请单据
                Date qFromDate = rs.getTimestamp("qZ1");
                Date qToDate = rs.getTimestamp("qZ3");
                String qz1 = df.format(qFromDate);
                String qz3 = df.format(qToDate);
                String qz5 = rs.getString("qZ5");
                String qstate = rs.getString("qstate");
                String qreason = rs.getString("qreason");
                String qapprover = rs.getString("qapprover");
                String qnum = rs.getString("qnum");// 单据号
                String b0110 = rs.getString("b0110");
                String a0100 = rs.getString("a0100");
                String nbase = rs.getString("nbase");
                // 参考班次
                String classid = rs.getString("classid");
                String tableflag = rs.getString("tableflag");
                // 获取申请时长
                ArrayList listUnit = getTimelenUnit(tableflag, qstate, qFromDate, qToDate, b0110, nbase, a0100,
                        classid);
                String qtimelen = (String) listUnit.get(0) + (String) listUnit.get(1);
                try {
                    if (null != rs.getString("customtimelen")) {
                        qtimelen = rs.getString("customtimelen") + (String) listUnit.get(1);
                    }
                } catch (Exception e) {

                }
                // 销假单据
                Date xFromDate = rs.getTimestamp("xZ1");
                Date xToDate = rs.getTimestamp("xZ3");
                String xz1 = xFromDate != null ? df.format(xFromDate) : "";
                String xz3 = xToDate != null ? df.format(xToDate) : "";
                String xz5 = rs.getString("xZ5");
                String xstate = rs.getString("xstate");
                String xreason = rs.getString("xreason");
                String xapprover = rs.getString("xapprover");
                String xnum = rs.getString("xnum");// 单据号
                Date xdate = rs.getTimestamp("xdate");
                String xdatestr = xdate != null ? df.format(xdate) : "";

                ldb = new LazyDynaBean();
                // 申请
                // 开始时间
                ldb.set("qz1", qz1);
                // 结束时间
                ldb.set("qz3", qz3);
                ldb.set("qspz5", qz5);
                ldb.set("qz5", AdminCode.getCodeName("23", qz5));
                ldb.set("qstate03", qstate);
                // 申请类型
                ldb.set("qstate", AdminCode.getCodeName("27", qstate));
                // 申请事由
                ldb.set("qreason", StringUtils.isEmpty(qreason) ? "" : qreason);
                ldb.set("qapprover", StringUtils.isEmpty(qapprover) ? "" : qapprover);
                ldb.set("qnum", qnum);
                // 时长
                ldb.set("qtimelen", qtimelen);
                ldb.set("tableflag", tableflag);
                // 销假
                // 开始时间
                ldb.set("xz1", xz1);
                // 结束时间
                ldb.set("xz3", xz3);
                ldb.set("xspz5", StringUtils.isEmpty(xz5) ? "" : xz5);
                ldb.set("xz5", StringUtils.isNotEmpty(xz5) ? AdminCode.getCodeName("23", xz5) : "");
                ldb.set("xstate", StringUtils.isNotEmpty(xstate) ? AdminCode.getCodeName("27", xstate) : "");
                ldb.set("xreason", StringUtils.isEmpty(xreason) ? "" : xreason);
                ldb.set("xapprover", StringUtils.isEmpty(xapprover) ? "" : xapprover);
                ldb.set("xnum", StringUtils.isEmpty(xnum) ? "" : xnum);
                // 销假申请时间
                ldb.set("xdate", xdatestr);

                list.add(ldb);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }

    /**
     * 获取每个表单的SQL
     * 
     * @param table
     * @param fromDateSql
     * @param toDateSql
     * @return
     */
    public String getAppDataSql(String table, String fromDateSql, String toDateSql, String whereSql) {

        String customTimeLenItem = getCustomTimeLen(table);

        // 校验销假单号指标是否存在
        boolean table19 = true;
        FieldItem item = DataDictionary.getFieldItem(table + "19");
        if (null == item || !"1".equals(item.getUseflag()))
            table19 = false;

        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT * FROM ");
        sql.append(" (");
        sql.append("SELECT '").append(table.toLowerCase()).append("' as tableflag,").append(table)
                .append("01 as qnum,");
        sql.append(table).append("Z1 as qZ1,").append(table).append("Z3 as qZ3,");
        sql.append(table).append("Z5 as qZ5,").append(table).append("03 as qstate,");
        sql.append(table).append("04 as classid,");
        sql.append("b0110,a0100,nbase,");
        sql.append(table).append("07 as qreason,").append(table).append("13 as qapprover ");
        if (StringUtils.isNotBlank(customTimeLenItem))
            sql.append(",").append(customTimeLenItem).append(" as customtimelen");
        sql.append(" FROM ").append(table);
        sql.append(" WHERE nbase=? AND a0100=? ");
        sql.append(whereSql);
        if (table19)
            sql.append(" AND (").append(table).append("19 is null or ").append(table).append("19='')");

        sql.append(" AND (");
        sql.append(" (").append(table).append("Z1").append(">=").append(fromDateSql);
        sql.append(" AND ").append(table).append("Z1").append("<=").append(toDateSql).append(")");
        sql.append(" OR (").append(table).append("Z3").append(">=").append(fromDateSql);
        sql.append(" AND ").append(table).append("Z3").append("<=").append(toDateSql).append(")");
        sql.append(" OR (").append(table).append("Z1").append("<").append(fromDateSql);
        sql.append(" AND ").append(table).append("Z3").append(">").append(toDateSql).append(")");
        sql.append(")");
        sql.append(") a ");

        sql.append(" left join ");
        // 左连接对应的销假申请单
        sql.append(" (");
        sql.append("SELECT ").append(table).append("01 as xnum,");
        sql.append(table).append("Z1 as xZ1,").append(table).append("Z3 as xZ3,").append(table).append("05 as xdate,");
        sql.append(table).append("Z5 as xZ5,").append(table).append("03 as xstate,");
        if (table19)
            sql.append(table).append("19 as xjnum,");
        else
            sql.append("'' as xjnum,");
        sql.append(table).append("07 as xreason,").append(table).append("13 as xapprover ");
        if (StringUtils.isNotBlank(customTimeLenItem))
            sql.append(",").append(customTimeLenItem).append(" as xcustomtimelen");
        sql.append(" FROM ").append(table);
        sql.append(" WHERE nbase=? AND a0100=?");
        sql.append(whereSql);
        sql.append(" AND (");
        sql.append(" (").append(table).append("Z1").append(">=").append(fromDateSql);
        sql.append(" AND ").append(table).append("Z1").append("<=").append(toDateSql).append(")");
        sql.append(" OR (").append(table).append("Z3").append(">=").append(fromDateSql);
        sql.append(" AND ").append(table).append("Z3").append("<=").append(toDateSql).append(")");
        sql.append(" OR (").append(table).append("Z1").append("<").append(fromDateSql);
        sql.append(" AND ").append(table).append("Z3").append(">").append(toDateSql).append(")");
        sql.append(")");
        sql.append(") b ");

        sql.append(" on b.xjnum = a.qnum ");
        return sql.toString();
    }

    public HashMap getKqItems() throws GeneralException {

        HashMap map = new HashMap();
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer("");
            sql.append("SELECT codeitemid, codeitemdesc,parentid  FROM codeitem  where codesetid ='27'");
            sql.append(" and codeitemid<>parentid ");
            sql.append(" order by a0000");

            ArrayList q15items = new ArrayList();
            ArrayList q11items = new ArrayList();
            ArrayList q13items = new ArrayList();
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String parentid = rs.getString("parentid");
                HashMap oneMap = new HashMap();
                oneMap.put("itemid", rs.getString("codeitemid"));
                oneMap.put("itemdesc", rs.getString("codeitemdesc"));
                oneMap.put("parentid", parentid);

                if ("0".equals(parentid)) {
                    q15items.add(oneMap);
                } else if ("1".equals(parentid)) {
                    q11items.add(oneMap);
                } else if ("3".equals(parentid)) {
                    q13items.add(oneMap);
                }
            }
            map.put("q11items", q11items);
            map.put("q13items", q13items);
            map.put("q15items", q15items);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    public ArrayList getTimelenUnit(String tableflag, String appType, Date beginTime, Date endTime, String b0110,
            String nbase, String A0100, String classid) throws GeneralException {

        ArrayList listUnit = new ArrayList();
        try {

            AnnualApply annualApply = new AnnualApply(userView, conn);
            float[] holidayRule = annualApply.getHoliday_minus_rule();
            HashMap kqItem_hash = annualApply.count_Leave(appType);
            String appDesc = (String) kqItem_hash.get("item_name");
            // 年假需要按规则计算申请假期时长
            float[] factHolidayRule = null;
            if (KqParam.getInstance().isHoliday(conn, b0110, appType)) {
                factHolidayRule = holidayRule;
            }
            float timeCount = 0;
            String unit = (String) kqItem_hash.get("item_unit");

            String timeLenItem = getCustomTimeLen(tableflag);
            if (StringUtils.isBlank(timeLenItem)) {
                // 加班时长计算
                if ("q11".equalsIgnoreCase(tableflag)) {
                    // 如果有参考班次 按班次时长计算
                    if (StringUtils.isBlank(classid) || "0".equals(classid))
                        timeCount = annualApply.calcOverAppTimeLen(nbase, A0100, beginTime, endTime, kqItem_hash,
                                Integer.MAX_VALUE);
                    else {
                        timeCount = annualApply.getOvertimeLen(appType, classid, nbase, A0100, beginTime, endTime);
                        unit = KqConstant.Unit.HOUR;
                    }
                } else {
                    // 请假 公出时长计算
                    timeCount = annualApply.calcLeaveAppTimeLen(nbase, A0100, b0110, beginTime, endTime, kqItem_hash,
                            factHolidayRule, Integer.MAX_VALUE);
                }
            } else {

            }
            String timeLen = Float.toString(timeCount);
            listUnit.add(timeLen);
            // 单位描述
            String unitDesc = "";
            if (KqConstant.Unit.DAY.equals(unit))
                unitDesc = KqConstant.Unit.DAY_DESC;
            else if (KqConstant.Unit.HOUR.equals(unit))
                unitDesc = KqConstant.Unit.HOUR_DESC;
            else if (KqConstant.Unit.MINUTE.equals(unit))
                unitDesc = KqConstant.Unit.MINUTE_DESC;
            else
                unitDesc = KqConstant.Unit.TIMES_DESC;
            listUnit.add(unitDesc);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return listUnit;
    }

    /**
     * 客户在system中个性化定义的申请时长指标
     * 
     * @param appTab
     *            申请表名
     * @return 时长指标id
     */
    private String getCustomTimeLen(String appTab) {
        if (StringUtils.isBlank(appTab))
            return "";

        String customItem = SystemConfig.getPropertyValue("kq_timelen_" + appTab.toLowerCase());
        if (StringUtils.isBlank(customItem))
            return "";

        FieldItem item = DataDictionary.getFieldItem(customItem, appTab);
        if (item == null)
            return "";

        if (!"1".equalsIgnoreCase(item.getUseflag()))
            return "";

        return customItem;
    }

    /**
     * 获取参考班次下拉列表数据
     * 
     * @return
     * @throws GeneralException
     */
    public ArrayList getClassList() throws GeneralException {

        ArrayList classlist = new ArrayList();
        try {
            KqUtilsClass kqcl = new KqUtilsClass(this.conn, this.userView);
            ArrayList kqcllist = new ArrayList();
            kqcllist = kqcl.getKqClassListInPriv();
            LazyDynaBean ldb = new LazyDynaBean();
            CommonData da = new CommonData();
            // ArrayList class_list = new ArrayList();
            da.setDataName("<无>");
            da.setDataValue("#");
            classlist.add(da);
            for (int i = 0; i < kqcllist.size(); i++) {
                String onduty = "";
                String offduty = "";
                ldb = (LazyDynaBean) kqcllist.get(i);
                if ("0".equals((String) ldb.get("classId"))) {
                    continue;
                }
                da = new CommonData();
                onduty = (String) ldb.get("onduty_1");
                for (int j = 3; j > 0; j--) {
                    offduty = (String) ldb.get("offduty_" + j);
                    if (offduty != null && offduty.length() == 5)
                        break;
                }
                if (onduty != null && onduty.trim().length() > 0 && offduty != null && offduty.trim().length() > 0) {
                    da.setDataName((String) ldb.get("name") + "(" + onduty + "~" + offduty + ")");
                    da.setDataValue((String) ldb.get("classId"));
                    classlist.add(da);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return classlist;
    }

}
