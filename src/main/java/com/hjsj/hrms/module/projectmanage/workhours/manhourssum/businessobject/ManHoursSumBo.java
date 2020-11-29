package com.hjsj.hrms.module.projectmanage.workhours.manhourssum.businessobject;

import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.module.projectmanage.project.businessobject.ManProjectHoursBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * <p>
 * Title: ManHoursSumBo
 * </p>
 * <p>
 * Description: 上传信息统计审批BO
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2015-12-28 下午1:17:20
 * </p>
 * 
 * @author liuyang
 * @version 1.0
 */
public class ManHoursSumBo {
    Connection conn;
    UserView userview;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ManHoursSumBo(Connection frameconn, UserView userView) {
        this.conn = frameconn;
        this.userview = userView;
    }

    /**
     * 
     * @Title:getColumnList
     * @Description：获取表头数据
     * @author liuyang
     * @param exceptFields
     * @param EditFields
     * @param isAddWidth
     * @param islock
     * @return
     */
    public ArrayList getColumnList(String exceptFields, String EditFields, String isAddWidth,
            String islock) {

        ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();

        ArrayList fieldList = DataDictionary.getFieldList("P15", 1);

        ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo = getColumnsInfo("P1303", "单位名称", 100, "0", "A", 30, 0);
        columnsInfo.setFieldsetid("P13");
        columnsInfo.setLocked(true);
        columnsList.add(columnsInfo);
        columnsInfo = new ColumnsInfo();
        columnsInfo = getColumnsInfo("P1305", "部门", 100, "0", "A", 30, 0);
        columnsInfo.setLocked(true);
        columnsList.add(columnsInfo);
        columnsInfo = new ColumnsInfo();
        columnsInfo = getColumnsInfo("A0101", "姓名", 100, "0", "A", 30, 0);
        columnsInfo.setLocked(true);
        columnsList.add(columnsInfo);
        columnsInfo = new ColumnsInfo();
        columnsInfo = getColumnsInfo("P1311", "担任角色", 100, "0", "A", 30, 0);
        columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
        columnsList.add(columnsInfo);
        for (int i = 0; i < fieldList.size(); i++) {
            FieldItem fi = (FieldItem) fieldList.get(i);
            columnsInfo = new ColumnsInfo();
            // 去除不需要的指标
            if (exceptFields.indexOf("," + fi.getItemid().toLowerCase() + ",") != -1)
                continue;
            
            // 去除未构库的指标
            if (!"1".equals(fi.getUseflag()))
                continue;
            
            // 去除隐藏的指标
            if (!"1".equals(fi.getState()))
                columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            
            String itemid = fi.getItemid();
            String itemdesc = fi.getItemdesc();
            String codesetId = fi.getCodesetid();
            String columnType = fi.getItemtype();
            // 显示长度
            int columnLength = fi.getItemlength();
            // 小数位
            int decimalWidth = fi.getDecimalwidth();
            columnsInfo = getColumnsInfo(itemid, itemdesc, 100, codesetId, columnType,
                    columnLength, decimalWidth);
            // 主键ID
            if ("P1501".equalsIgnoreCase(fi.getItemid()))
                columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
             // 内部工时确认标识ID
            else if ("P1519".equalsIgnoreCase(fi.getItemid()))
                columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            // 内部工时确认标识ID
            else if ("P1301".equalsIgnoreCase(fi.getItemid()))
                columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            // 修改p1201 type
            else if ("P1201".equalsIgnoreCase(fi.getItemid()))
                columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            // 需要增加列宽的列
            if (!StringUtils.isEmpty(isAddWidth)) {
                if (isAddWidth.indexOf("," + fi.getItemid() + ",") != -1)
                    columnsInfo.setColumnWidth(145);// 显示列宽
                
            }
            // 需要锁列
            if (!StringUtils.isEmpty(islock)) {
                if (islock.indexOf("," + fi.getItemid() + ",") != -1)
                    columnsInfo.setLocked(true);
                
            }

            if ("P1511，P1513,P1515".contains(fi.getItemid().toUpperCase())) {// 实际工时、标准工时、超额工时
                columnsInfo.setRendererFunc("ManHoursSum_me.infactTime");
                columnsInfo.setSummaryRendererFunc("ManHoursSum_me.infactTime");
                columnsInfo.setTextAlign("right");
            }

            columnsList.add(columnsInfo);
        }

        columnsInfo = new ColumnsInfo();
        columnsInfo = getColumnsInfo("P1203", "里程碑名称", 100, "0", "A", 30, 0);
        columnsInfo.setFieldsetid("P12");
        columnsList.add(columnsInfo);

        columnsInfo = new ColumnsInfo();
        columnsInfo = getColumnsInfo("workHours", "工时确认", 100, "0", "A", 30, 0);
        columnsList.add(columnsInfo);

        return columnsList;

    }

    /**
     * 
     * @Title:getButtonList
     * @Description：按钮数据
     * @author liuyang
     * @param type
     * @return
     */
    public ArrayList getButtonList(String type) {
        ArrayList buttonList = new ArrayList();

        if (userview.hasTheFunction("3900301"))
            buttonList.add("-");
        if (userview.hasTheFunction("3900302") ) {
            buttonList.add(newButton("同意", null, "ManHoursSum_me.accede", null, "true"));
            buttonList.add("-");
        }
        if (userview.hasTheFunction("3900303") ) {
            buttonList.add(newButton("退回", null, "ManHoursSum_me.refuse", null, "true"));
            buttonList.add("-");
        }
        if (userview.hasTheFunction("3900304")) {
            buttonList.add(newButton("删除", null, "ManHoursSum_me.dele", null, "true"));
            buttonList.add("-");
        }
        
        if ("1".equals(type))
            buttonList.add(newButton("返回", null, "ManHoursSum_me.returnToMainPage", null, "true"));
        else
            buttonList.add(newButton("返回", null, "ManHoursSum_me.returnToDetail", null, "true"));
        
        ButtonInfo querybox = new ButtonInfo();
        querybox.setFunctionId("PM00000204");
        querybox.setType(ButtonInfo.TYPE_QUERYBOX);
        querybox.setText("请输入姓名，任务描述...");
        buttonList.add(querybox);
        return buttonList;
    }

    /**
     * 列头ColumnsInfo对象初始化
     * 
     * @param columnId
     *            id
     * @param columnDesc
     *            名称
     * @param columnDesc
     *            显示列宽
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth,
            String codesetId, String columnType, int columnLength, int decimalWidth) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        columnsInfo.setCodesetId(codesetId);// 指标集
        columnsInfo.setColumnType(columnType);// 类型N|M|A|D
        columnsInfo.setColumnLength(columnLength);// 显示长度
        columnsInfo.setDecimalWidth(decimalWidth);// 小数位
        columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
        columnsInfo.setReadOnly(false);// 是否只读
        if("workHours".equalsIgnoreCase(columnId)) {
        	columnsInfo.setFromDict(false);// 是否从数据字典里来
        } else {
        	columnsInfo.setFromDict(true);// 是否从数据字典里来
        }
        
        columnsInfo.setLocked(false);// 是否锁列
        columnsInfo.setEditableValidFunc("false");// 添加编辑器
        return columnsInfo;
    }

    private ButtonInfo newButton(String text, String id, String handler, String icon, String getdata) {
        ButtonInfo button = new ButtonInfo(text, handler);
        if (getdata != null)
            button.setGetData(Boolean.valueOf(getdata).booleanValue());
        if (icon != null)
            button.setIcon(icon);
        if (id != null)
            button.setId(id);
        return button;
    }

    /**
     * 
     * @Title:accedeManHoursSumApply
     * @Description：接受申请
     * @author liuyang
     * @param manSumIdStrs
     * @param manDetailStrs
     * @param projectId
     * @param text
     * @throws GeneralException
     */
    public void accedeManHoursSumApply(String manSumIdStrs, String manDetailStrs, String projectId,
            String landMarkStrs, String text) throws GeneralException {

        ContentDAO dao = new ContentDAO(conn);

        try {

            StringBuffer sumStateSqlStr = new StringBuffer();
            ArrayList values = new ArrayList();

            sumStateSqlStr.append("update  P15 ");
            sumStateSqlStr.append(" set ");
            sumStateSqlStr.append(" P1519='1',");
            sumStateSqlStr.append(" P1517=?");
            sumStateSqlStr.append(" where P1501 in ( "
                    + manSumIdStrs.substring(0, manSumIdStrs.lastIndexOf(",")) + " )");
            values.add(text);
            dao.update(sumStateSqlStr.toString(), values);

            ManProjectHoursBo bo = new ManProjectHoursBo(this.conn, this.userview);

            bo.sumData("p13", manDetailStrs.substring(0, manDetailStrs.lastIndexOf(",")));
            // 判断是否有里程碑
            if (landMarkStrs.length() > 0)
                bo.sumData("p12", landMarkStrs.substring(0, landMarkStrs.lastIndexOf(",")));
            bo.sumData("p11", projectId);
            saveWorkeSummary(manSumIdStrs);
        } catch (SQLException e) {

            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /**
     * 
     * @Title:refuseManHoursSumApply
     * @Description：拒绝申请
     * @author liuyang
     * @param manSumIdStrs
     * @param text
     * @throws GeneralException
     */
    public void refuseManHoursSumApply(String manSumIdStrs, String text) throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        try {
            for (int i = 0; i < manSumIdStrs.split(",").length; i++) {
                String manSumIdStr = manSumIdStrs.split(",")[i];
                StringBuffer sqlStr = new StringBuffer();

                StringBuffer sumStateSqlStr = new StringBuffer();
                ArrayList values = new ArrayList();
                sumStateSqlStr.append("update  P15 ");
                sumStateSqlStr.append(" set ");
                sumStateSqlStr.append(" P1519 = '2',");
                sumStateSqlStr.append("P1517 = ?");
                sumStateSqlStr.append(" where P1501 = ?");
                values.add(text);
                values.add(manSumIdStr);
                dao.update(sumStateSqlStr.toString(), values);
            }

        } catch (Exception e) {

            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /**
     * 
     * @Title:getDateRange
     * @Description：获取页面时间区间方法
     * @author liuyang
     * @param dateRange
     * @return
     */
    public String getDateRange(String dateRange) {
        Date date = new Date();

        StringBuffer sql = new StringBuffer(" and ");
        // 查询方案 00 查询全部 ，01本周，02本月，03本季，04本年
        if ("00".equals(dateRange)) {
            return "";
        }
        if ("01".equals(dateRange)) {
            sql.append("(");
            sql.append(" (P1507>=" + getFirstDayOfWeek(date));
            sql.append(" and P1507<=" + getLastDayOfWeek(date));
            sql.append(")");
            sql.append(" or (P1509>=" + getFirstDayOfWeek(date));
            sql.append(" and P1509<=" + getLastDayOfWeek(date));
            sql.append(")");
            sql.append(")");
        }
        if ("02".equals(dateRange)) {
            sql.append("(");
            sql.append(" (P1507>=" + getFirstDayOfMonth(date));
            sql.append(" and P1507<=" + getLastDayOfMonth(date));
            sql.append(")");
            sql.append(" or (P1509>=" + getFirstDayOfMonth(date));
            sql.append(" and P1509<=" + getLastDayOfMonth(date));
            sql.append(")");
            sql.append(")");
        }
        if ("03".equals(dateRange)) {
            sql.append("(");
            sql.append(" (P1507>=" + getFirstDayOfQuarter(date));
            sql.append(" and P1507<=" + getLastDayOfQuarter(date));
            sql.append(")");
            sql.append(" or (P1509>=" + getFirstDayOfQuarter(date));
            sql.append(" and P1509<=" + getLastDayOfQuarter(date));
            sql.append(")");
            sql.append(")");
        }
        if ("04".equals(dateRange)) {
            sql.append("(");
            sql.append(" (P1507>=" + getFirstdayOfYear(date));
            sql.append(" and P1507<=" + getLastdayOfYear(date));
            sql.append(")");
            sql.append(" or (P1509>=" + getFirstdayOfYear(date));
            sql.append(" and P1509<=" + getLastdayOfYear(date));
            sql.append(")");
            sql.append(")");
        }
        return sql.toString();
    }

    private String getFirstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Sunday
        String d = sdf.format(calendar.getTime());
        d = d + " 00:00:00";
        return Sql_switcher.dateValue(d);
    }

    private String getLastDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6); // Saturday
        String d = sdf.format(calendar.getTime());
        d = d + " 23:59:59";
        return Sql_switcher.dateValue(d);
    }

    private String getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        String d = sdf.format(calendar.getTime());
        d = d + " 00:00:00";
        return Sql_switcher.dateValue(d);
    }

    private String getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.roll(Calendar.DATE, -1);
        String d = sdf.format(calendar.getTime());
        d = d + " 23:59:59";
        return Sql_switcher.dateValue(d);
    }

    private String getFirstDayOfQuarter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String d = sdf.format(getFirstDayOfQuarter(calendar.get(Calendar.YEAR),
                getQuarterOfYear(date)));
        d = d + " 00:00:00";
        return Sql_switcher.dateValue(d);
    }

    private String getLastDayOfQuarter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String d = sdf.format(getLastDayOfQuarter(calendar.get(Calendar.YEAR),
                getQuarterOfYear(date)));
        d = d + " 23:59:59";
        return Sql_switcher.dateValue(d);
    }

    private Date getFirstDayOfQuarter(Integer year, Integer quarter) {
        Calendar calendar = Calendar.getInstance();
        Integer month = new Integer(0);
        if (quarter == 1) {
            month = 1 - 1;
        } else if (quarter == 2) {
            month = 4 - 1;
        } else if (quarter == 3) {
            month = 7 - 1;
        } else if (quarter == 4) {
            month = 10 - 1;
        } else {
            month = calendar.get(Calendar.MONTH);
        }
        return getFirstDayOfMonth(year, month);
    }

    public static Date getLastDayOfQuarter(Integer year, Integer quarter) {
        Calendar calendar = Calendar.getInstance();
        Integer month = new Integer(0);
        if (quarter == 1) {
            month = 3 - 1;
        } else if (quarter == 2) {
            month = 6 - 1;
        } else if (quarter == 3) {
            month = 9 - 1;
        } else if (quarter == 4) {
            month = 12 - 1;
        } else {
            month = calendar.get(Calendar.MONTH);
        }
        return getLastDayOfMonth(year, month);
    }

    private Date getFirstDayOfMonth(Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        if (year == null) {
            year = calendar.get(Calendar.YEAR);
        }
        if (month == null) {
            month = calendar.get(Calendar.MONTH);
        }
        calendar.set(year, month, 1);
        return calendar.getTime();
    }

    public static Date getLastDayOfMonth(Integer year, Integer month) {
        Calendar calendar = Calendar.getInstance();
        if (year == null) {
            year = calendar.get(Calendar.YEAR);
        }
        if (month == null) {
            month = calendar.get(Calendar.MONTH);
        }
        calendar.set(year, month, 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.getTime();
    }

    public int getQuarterOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) / 3 + 1;
    }

    public String getFirstdayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), 0, 1);
        String d = sdf.format(calendar.getTime());
        d = d + " 00:00:00";
        return Sql_switcher.dateValue(d);
    }

    public String getLastdayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), 11, 1);
        calendar.roll(Calendar.DATE, -1);
        String d = sdf.format(calendar.getTime());
        d = d + " 23:59:59";
        return Sql_switcher.dateValue(d);
    }

    /**
     * 返回p15中所有字段
     * 
     * @return
     */
    public String getP15FieldSql() {
        StringBuffer res = new StringBuffer("");
        ArrayList fieldList = DataDictionary.getFieldList("P15", 1);

        FieldItem fi = null;
        for (int i = 0; i < fieldList.size(); i++) {
            fi = (FieldItem) fieldList.get(i);
            res.append("p15." + fi.getItemid() + ",");
        }
        
        if (res.length() > 1)
            res.setLength(res.length() - 1);
        
        return res.toString();
    }

    public void sendMsgToAll(String manSumIdStrs, String content, String refuseOrAccede)
            throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        String[] applicationId = manSumIdStrs.split(",");
        String sql = "";
        String time = "";
        String projectName = "";
        String mName = "";
        String a0100 = "";
        String nbase = "";
        String title = "";
        String description = "";
        String format_str = "yyyy-MM-dd ";
        WeiXinBo wxb = new WeiXinBo();
        for (int i = 0; i < applicationId.length; i++) {
            sql = "select "
                    + Sql_switcher.dateToChar("p15.p1507", format_str)
                    + " p1507,P11.P1103,P13.a0100,P13.nbase,P13.A0101 from P15,P13,p11 where p15.P1501 = '"
                    + applicationId[i] + "' and p15.P1301 = p13.P1301 and p15.P1101 = p11.P1101";
            RowSet rs = null;
            try {
                rs = dao.search(sql);
                if (rs.next()) {
                    time = rs.getString("p1507");
                    projectName = rs.getString("p1103");
                    mName = rs.getString("a0101");
                    a0100 = rs.getString("a0100");
                    nbase = rs.getString("nbase");
                }
                String type = "edit";
                if ("refuse".equals(refuseOrAccede)) {
                    title = mName + "的" + projectName + time + "工时未被确认";
                    description = mName + "，你好！\r\n由于如下原因：\r\n" + (content == null ? "无" : content)
                            + "\r\n你提交的" + projectName + "工时未被认可，请核对！";
                } else if ("accede".equals(refuseOrAccede)) {
                    type = "view";
                    title = mName + "的" + projectName + time + "工时已认可";
                    description = mName + "，你好！\r\n你提交的" + projectName
                            + "工时已认可，请核对！\r\n下一步工作安排如下：\r\n" + (content == null ? "无" : content);
                }
                String usernameTo = getUserName(a0100, nbase);
                String passwordTo = getPassword(a0100, nbase);
                String serverURL = userview.getServerurl();
                String etoken = PubFunc.convertUrlSpecialCharacter(PubFunc
                        .convertTo64Base(usernameTo + "," + passwordTo));
                String url = serverURL + "/w_selfservice/module/selfservice/index.jsp?etoken="
                        + etoken + "&menuid=12&loadType=" + type + "&p1501="
                        + PubFunc.encrypt(applicationId[i]);
                String picUrl = "";
                if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid")))
                    WeiXinBo.sendMsgToPerson(usernameTo, title, description, picUrl, url);
                
                //推送至钉钉 chenxg 2017-06-01
                if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK","corpid")))
                    DTalkBo.sendMessage(usernameTo, title, description, picUrl, url);
                    
            } catch (SQLException e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            } finally {
                PubFunc.closeDbObj(rs);
            }
        }

    }

    private String getUserName(String a0100, String nbase) throws GeneralException {
        DbNameBo dbNameBo = new DbNameBo(conn);
        String userNameField = dbNameBo.getLogonUserNameField();
        if (userNameField == null || "".equals(userNameField))
            return "";

        String sql = "select " + userNameField + " username from " + nbase + "A01 where a0100 = "
                + a0100;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String username = "";
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                username = rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return username;
    }

    private String getPassword(String a0100, String nbase) throws GeneralException {
        DbNameBo dbNameBo = new DbNameBo(conn);
        String passwordField = dbNameBo.getLogonPassWordField();
        if (passwordField == null || "".equals(passwordField))
            return "";

        String sql = "select " + passwordField + " from " + nbase + "A01 where a0100 = " + a0100;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String password = "";
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                password = rs.getString(passwordField);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return password;
    }

    /**
     * 批准提交的项目工时时，将项目工时的描述同步到对应人员的周总结中
     * 
     * @param manSumIdStrs
     *            批准的项目工时的id
     */
    public void saveWorkeSummary(String manSumIdStrs) {
        RowSet rs = null;
        ArrayList<String> sqlList = new ArrayList<String>();
        try {
            if (manSumIdStrs.endsWith(","))
                manSumIdStrs = manSumIdStrs.substring(0, manSumIdStrs.lastIndexOf(","));

            PlanTaskBo pbo = new PlanTaskBo(this.conn, this.userview);
            WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.conn, this.userview);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT P15.P1101,"+Sql_switcher.dateToChar("P15.P1507","yyyy-MM-dd")+" P1507,P15.P1505,P15.P1507,P13.A0100,P13.NBASE,P13.A0101");
            sql.append(" FROM P15,P13 WHERE P15.P1301=P13.P1301 AND P1501 IN (");
            sql.append(manSumIdStrs + ")");
            WorkPlanSummaryBo bo = new WorkPlanSummaryBo();
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String p1101 = rs.getString("P1101");
                String p1505 = rs.getString("P1505");
                String p1507 = rs.getString("P1507");
                String a0100 = rs.getString("A0100");
                String nbase = rs.getString("NBASE");
                String a0101 = rs.getString("A0101");

                // 获取人员信息
                RecordVo a01Vo = pbo.getPersonByObjectId(nbase + a0100);
                String b0110 = a01Vo.getString("b0110");
                String e0122 = a01Vo.getString("e0122");
                String e0a01 = a01Vo.getString("e01a1");

                Date date = DateUtils.getDate(p1507, "yyyy-MM-dd");
                int year = DateUtils.getYear(date);
                int month = DateUtils.getMonth(date);

                // 判断一号是否是本月
                if (bo.getTrueDate(year, month, -1, date)) {
                    year = DateUtils.getYear(DateUtils.addMonths(date, -1));
                    month = DateUtils.getMonth(DateUtils.addMonths(date, -1));
                }

                // 判断月末是否为下个月
                if (bo.getTrueDate(DateUtils.getYear(DateUtils.addMonths(date, 1)),
                        DateUtils.getMonth(DateUtils.addMonths(date, -1)), 1, date)) {
                    year = DateUtils.getYear(DateUtils.addMonths(date, 1));
                    month = DateUtils.getMonth(DateUtils.addMonths(date, 1));
                }

                // 当月第几周
                ArrayList week = bo.getP011503Num(dao, 1, year, month, nbase, a0100, e0122, "0", date);
                // 所在周总结的开始时间和结束时间
                String[] weekDate = bo.getSummaryDates("1", year + "", month + "", Integer.valueOf(week.get(0).toString()));
                // 取开始时间是星期几
                String weekName = KqUtilsClass.getWeekName(date);
                // 安全过滤
                p1505 = PubFunc.hireKeyWord_filter(p1505);
                p1505 = p1505.replace("\n", "\n    ");
                
                String task = "\r\n" + weekName + "(" + DateUtils.format(date, "yyyy.MM.dd")
                        + "  " + getProjectName(p1101) + ")\r\n    " + p1505;
                if (checkWorkSummary(nbase, a0100, weekDate)) {
                    String summary = getWorkSummary(nbase, a0100, weekDate);
                    task = summary + task;
                    StringBuffer updateSql = new StringBuffer();
                    updateSql.append("UPDATE P01");
                    updateSql.append(" SET P0109=?");
                    updateSql.append(" WHERE NBASE=?");
                    updateSql.append(" AND A0100=?");
                    updateSql.append(" AND " + Sql_switcher.dateToChar("P0104", "yyyy-MM-dd")
                            + "=?");
                    updateSql.append(" AND " + Sql_switcher.dateToChar("P0106", "yyyy-MM-dd")
                            + "=?");
                    updateSql.append(" AND STATE=?");

                    ArrayList<String> valueList = new ArrayList<String>();
                    valueList.add(task);
                    valueList.add(nbase);
                    valueList.add(a0100);
                    valueList.add(weekDate[0]);
                    valueList.add(weekDate[1]);
                    valueList.add("1");
                    dao.update(updateSql.toString(), valueList);
                } else {
                    String supere01a1 = workPlanUtil.getSuperE01a1s(e0a01);
                    // 获取周总结的id
                    IDGenerator idg = new IDGenerator(2, this.conn);
                    String pid = idg.getId("P01.p0100");
                    //在oracle库下 保存日期型有问题 改用vo 新增数据  wangb 20190521 
                    RecordVo P01Vo = new RecordVo("P01");
                    P01Vo.setInt("p0100", Integer.parseInt(pid));
                    P01Vo.setString("a0100",a0100);
                    P01Vo.setString("nbase",nbase);
                    P01Vo.setString("b0110",b0110);
                    P01Vo.setString("e0122",e0122);
                    P01Vo.setString("a0101",a0101);
                    P01Vo.setString("e01a1",e0a01);
                    P01Vo.setString("supere01a1",supere01a1);
                    P01Vo.setString("p0115","01");
                    P01Vo.setInt("state",1);
                    P01Vo.setDate("p0104", weekDate[0]);
                    P01Vo.setDate("p0106", weekDate[1]);
                    P01Vo.setDate("p0114", PubFunc.FormatDate(new Date(),"yyyy-MM-dd"));
                    P01Vo.setString("time",week.get(0).toString());
                    P01Vo.setString("p0109",task);
                    P01Vo.setString("p0120","");
                    P01Vo.setString("score","-1");
                    P01Vo.setString("belong_type","0");
                    P01Vo.setString("scope","4");
                    dao.addValueObject(P01Vo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
    }

    /**
     * 获取项目名称
     * 
     * @param projectid
     *            项目id
     * @return
     */
    private String getProjectName(String projectid) {
        String projectName = "";
        RowSet rs = null;
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT P1103 FROM p11");
            sql.append(" WHERE P1101=");
            sql.append(projectid);
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if(rs.next())
                projectName = rs.getString("P1103");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return projectName;
    }
    /**
     * 检测周总结是否填写
     * 
     * @param nbase
     *            人员库
     * @param a0100
     *            人员编号
     * @param weekDate
     *            周总结的开始时间与结束时间
     * @return true： 存在 | false：不存在
     */
    private boolean checkWorkSummary(String nbase, String a0100, String[] weekDate) {
        boolean flag = false;
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT 1 FROM P01");
            sql.append(" WHERE NBASE='" + nbase + "'");
            sql.append(" AND A0100='" + a0100 + "'");
            sql.append(" AND " + Sql_switcher.dateToChar("P0104", "yyyy-MM-dd") + "='"
                    + weekDate[0] + "'");
            sql.append(" AND " + Sql_switcher.dateToChar("P0106", "yyyy-MM-dd") + "='"
                    + weekDate[1] + "'");
            sql.append(" AND STATE=1");

            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next())
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return flag;
    }

    /**
     * 获取已填写的周总的的本周总结的内容
     * 
     * @param nbase
     *            人员库
     * @param a0100
     *            人员编号
     * @param weekDate
     *            周总结的开始时间与结束时间
     * @return summary 总结内容
     */
    private String getWorkSummary(String nbase, String a0100, String[] weekDate) {
        String summary = "";
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT P0109 FROM P01");
            sql.append(" WHERE NBASE='" + nbase + "'");
            sql.append(" AND A0100='" + a0100 + "'");
            sql.append(" AND " + Sql_switcher.dateToChar("P0104", "yyyy-MM-dd") + "='"
                    + weekDate[0] + "'");
            sql.append(" AND " + Sql_switcher.dateToChar("P0106", "yyyy-MM-dd") + "='"
                    + weekDate[1] + "'");
            sql.append(" AND STATE=1");

            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next())
                summary = rs.getString("P0109");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return summary;
    }

    public void deleManHoursSumApply(String manSumIdStrs, String manDetailStrs, String projectId, String landMarkStrs) throws GeneralException {

        ContentDAO dao = new ContentDAO(conn);

        try {

            StringBuffer sumStateSqlStr = new StringBuffer();
            ArrayList values = new ArrayList();

            sumStateSqlStr.append("delete from  P15 ");
            sumStateSqlStr.append(" where P1501 in ( " + manSumIdStrs.substring(0, manSumIdStrs.lastIndexOf(","))
                    + " )");
            dao.update(sumStateSqlStr.toString(), values);
            
            ManProjectHoursBo bo = new ManProjectHoursBo(this.conn, this.userview);

            bo.sumData("p13", manDetailStrs.substring(0, manDetailStrs.lastIndexOf(",")));
            // 判断是否有里程碑
            if (landMarkStrs.length() > 0)
                bo.sumData("p12", landMarkStrs.substring(0, landMarkStrs.lastIndexOf(",")));
            bo.sumData("p11", projectId);

        } catch (Exception e) {

            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }
    
}
