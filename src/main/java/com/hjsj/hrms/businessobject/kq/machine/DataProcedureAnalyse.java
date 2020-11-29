package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.ValidateAppOper;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.options.KqItem;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassObject;
import com.hjsj.hrms.businessobject.kq.register.*;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class DataProcedureAnalyse implements Runnable {
    // 数据存储过程版本
    private final static String KQ_PROCEDURE_VER = "20140322";

    private Connection conn;
    private UserView userView;
    private String analyseType; // 0: 非机考人员 1：机考人员 100: 全部
    private String kq_type;
    private String kq_card;
    private String kq_Gno;
    private String fAnalyseTempTab; // 数据处理表
    private String fExceptCardTab; // 临时异常表的名称
    private String fTranOverTimeTab; // 临时延时加班表
    private String cardToOverTime; // 休息日刷卡转加班
    private String fBusiCompareTab; // //申请比对表
    private String fTmpBusiDataTab; // 申请记录临时表
    private String dataUpdateType = ""; // 更新数据类型0:全部1：只更新业务数据
    private ArrayList db_list = new ArrayList();
    private ContentDAO dao = null;
    private KqParam kqParam = null;
    private HashMap kqItem_hash = new HashMap();
    private DataAnalyseUtils dataAnalyseUtils;
    private String card_no_temp_field = "card_no";
    private String g_no_temp_field = "g_no";
    private String kq_sDate = "sDate";
    private String kq_dkind = "dkind";
    private String initflag; // 是否是初始化日明细
    private String pick_flag = "0";
    private RowSet fBusiData = null;
    private RowSet fLeaveBackData = null; // 销假业务数据集
    private HashMap overApplys = new HashMap();
    private RowSet rs_FEmpDatas = null;
    private String no_tranData = "";
    private String creat_pick = "1"; // 将申请单明细数据统计到考勤日明细
    private String creat_register = "0";
    private String mainsql = "";
    private ArrayList whereCode_List = new ArrayList();
    private HashMap whereInMap_forNbase = new HashMap();
    private String kqEmpWhrTmp = "";
    private ArrayList nbase_list = new ArrayList();
    private ArrayList b0110_list = new ArrayList();
    private String pub_desT_where = "";
    private int status = 1;

    // 优化速度用到的变量
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    private StringBuffer strWhrBuffer = new StringBuffer();
    private StringBuffer strSQLBuffer = new StringBuffer();
    private StringBuffer cardTimesBuffer = new StringBuffer();
    
    //异步方式时传入的数据处理参数
    private HashMap dataAnayseParams;

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInitflag() {
        return initflag;
    }

    public void setInitflag(String initflag) {
        this.initflag = initflag;
    }

    public void setPick_flag(String pick_flag) {
        this.pick_flag = pick_flag;
    }

    public DataProcedureAnalyse(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        this.kqEmpWhrTmp = RegisterInitInfoData.getKqEmpPrivWhr(conn, userView, "{TAB}");
    }

    /**
     * 数据处理所需临时表。（表名与CS保持一致）
     */
    private void init() {
        if (this.analyseType != null && "1".equals(this.analyseType)) {
            this.fAnalyseTempTab = "kt_" + this.userView.getUserName() + "_dd"; // changed
            // at
            // 20091203
            // kqtmp_xxx_daydata
            this.fExceptCardTab = "kt_" + this.userView.getUserName() + "_ed"; // changed
            // at
            // 20091203
            // kqtmp_xxxx_exceptcard
            this.fTranOverTimeTab = "kt_" + this.userView.getUserName() + "_tt";// changed
            // at
            // 20091203
            // kqtmp_xxxx_tranovertime
            this.fBusiCompareTab = "kt_" + this.userView.getUserName() + "_bc";// changed
            // at
            // 20091203
            // kqtmp_xxxx_busicompare
            this.cardToOverTime = "kt_" + this.userView.getUserName() + "_co";// 休息日刷卡转加班

        } else if (this.analyseType != null && "101".equals(this.analyseType)) {
            // this.analyseType=101 数据处理集中处理
            this.fAnalyseTempTab = "kq_analyse_result";
            this.fExceptCardTab = "kq_analyse_exceptcard";
            this.fTranOverTimeTab = "kq_analyse_tranovertime";
            this.fBusiCompareTab = "kq_analyse_busicompare";
            this.cardToOverTime = "kq_analyse_cardtoovertime";// 休息日刷卡转加班
            // this.fExceptCardTab ="kt_" +this.userView.getUserName() + "_ed";
            // //changed at 20091203 kqtmp_xxxx_exceptcard
            // this.fTranOverTimeTab= "kt_" +this.userView.getUserName() +
            // "_tt";//changed at 20091203 kqtmp_xxxx_tranovertime
            // this.fBusiCompareTab= "kt_" +this.userView.getUserName() +
            // "_bc";//changed at 20091203 kqtmp_xxxx_busicompare

        } else {
            this.fAnalyseTempTab = "kt_" + this.userView.getUserName() + "_dd2";// changed
            // at
            // 20091203
            // kqtmp_xxx_daydata2
            this.fExceptCardTab = "kt_" + this.userView.getUserName() + "_ed"; // changed
            // at
            // 20091203
            // kqtmp_xxxx_exceptcard
            this.fTranOverTimeTab = "kt_" + this.userView.getUserName() + "_tt";// changed
            // at
            // 20091203
            // kqtmp_xxxx_tranovertime
            this.fBusiCompareTab = "kt_" + this.userView.getUserName() + "_bc";// changed
            // at
            // 20091203
            // kqtmp_xxxx_busicompare
            this.cardToOverTime = "kt_" + this.userView.getUserName() + "_co";// 休息日刷卡转加班
                                                                              // 2012/5/8
                                                                              // add
        }

        this.fTmpBusiDataTab = "kt_" + this.userView.getUserName() + "_bd";

        /** 数据处理分用户处理 以前的 wangy* */
        // this.fExceptCardTab ="kt_" +this.userView.getUserName() + "_ed";
        // //changed at 20091203 kqtmp_xxxx_exceptcard
        // this.fTranOverTimeTab= "kt_" +this.userView.getUserName() +
        // "_tt";//changed at 20091203 kqtmp_xxxx_tranovertime
        // this.fBusiCompareTab= "kt_" +this.userView.getUserName() +
        // "_bc";//changed at 20091203 kqtmp_xxxx_busicompare
        /** 结束* */
        /*
         * if(this.analyseType!=null&&this.analyseType.equals("1")) {
         * this.fAnalyseTempTab="kqtmp_"+this.userView.getUserName()+"_daydata";
         * }else {
         * this.fAnalyseTempTab="kqtmp_"+this.userView.getUserName()+"_daydata2"
         * ; } this.fExceptCardTab ="kqtmp_" +this.userView.getUserName() +
         * "_exceptcard"; this.fTranOverTimeTab= "kqtmp_"
         * +this.userView.getUserName() + "_tranovertime"; this.fBusiCompareTab=
         * "kqtmp_" +this.userView.getUserName() + "_busicompare";
         */
    }

    public DataProcedureAnalyse(Connection conn, UserView userView, String analyseType, String kq_type, String kq_card,
            String kq_Gno, String dataUpdateType, ArrayList db_list) {

        this.conn = conn;
        this.userView = userView;
        this.analyseType = analyseType == null || analyseType.length() <= 0 ? "0" : analyseType;// 0:
                                                                                                // 非机考人员
                                                                                                // 1：机考人员
                                                                                                // 100:
                                                                                                // 全部,,101:
                                                                                                // 集中处理
        init();
        this.kq_type = kq_type;// 考勤方式字段
        this.kq_card = kq_card;// 考勤卡号字段
        this.kq_Gno = kq_Gno;
        this.dataUpdateType = dataUpdateType;// 更新数据类型0:全部1：只更新业务数据
        this.db_list = db_list;
        this.dao = new ContentDAO(this.conn);
        kqParam = KqParam.getInstance();
        this.dataAnalyseUtils = new DataAnalyseUtils(this.conn, this.userView);
        this.dataAnalyseUtils.setAnalyseType(this.analyseType);
        if ("101".equals(this.analyseType)) {
            this.pub_desT_where = " " + Sql_switcher.isnull("cur_user", "'" + this.userView.getUserName() + "'") + "='"
                    + this.userView.getUserName() + "' ";
        }
        this.mainsql = getmainsql();
        try {
            KqClassObject kqClassObject = new KqClassObject(this.conn);
            kqClassObject.checkKqClassTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        this.kqEmpWhrTmp = RegisterInitInfoData.getKqEmpPrivWhr(conn, userView, "{TAB}");
    }

    public void initTempTable() throws GeneralException {
        DbWizard dbWizard = new DbWizard(this.conn);
        // 数据处理表
        if (!dbWizard.isExistTable(this.fAnalyseTempTab, false)) {
            this.dataAnalyseUtils.createDataAnalyseTmp(this.fAnalyseTempTab);
        }

        if (!dbWizard.isExistTable(this.fExceptCardTab, false))// 临时异常表的名称
        {
            this.dataAnalyseUtils.ceartFExceptCardTab(this.fExceptCardTab);// 异常数据表
        }

        if (!dbWizard.isExistTable(this.fTranOverTimeTab, false))// 临时延时加班表
        {
            this.dataAnalyseUtils.createTranOverTimeTab(this.fTranOverTimeTab);// 延时加班表
        }

        if (!dbWizard.isExistTable(this.cardToOverTime, false)) {
            this.dataAnalyseUtils.createCardToOverTimeTab(this.cardToOverTime);// 休息日刷卡转加班
        }

        if (!dbWizard.isExistTable(this.fBusiCompareTab, false))// //申请比对表
        {
            this.dataAnalyseUtils.createCompareBusiWithFactTab(this.fBusiCompareTab);// 业务申请与实际刷卡情况表
        }
    }

    /**
     * 判断是否存在某个存储过程
     * 
     * @return
     */
    private boolean isExistPro(String proName) {
        boolean isExists = false;

        StringBuffer sql = new StringBuffer();
        if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
            sql.append("select * from user_objects where object_name = '" + proName.toUpperCase() + "'");
        } else if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
            sql.append("select * from dbo.sysobjects where id = object_id(N'[dbo].[" + proName + "]')");
        }

        ContentDAO dao = new ContentDAO(conn);
        ResultSet rs = null;
        try {
            rs = dao.search(sql.toString());
            isExists = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return isExists;
    }

    /**
     * 开始分析（数据处理入口）
     * 
     * @param code
     * @param kind
     * @param start_date
     * @param end_date
     * @param analysBase
     * @return
     * @throws GeneralException
     */
    public boolean dataAnalys(String code, String kind, String start_date, String end_date, String analysBase)
            throws GeneralException {

        return dataAnalys(code, kind, start_date, end_date, analysBase, true);
    }

    /**
     * 开始分析（后台作业数据处理入口）
     * 
     * @param code
     * @param kind
     * @param start_date
     * @param end_date
     * @param analysBase
     * @return
     * @throws GeneralException
     */
    public boolean dataAnalys(String code, String kind, String start_date, String end_date, String analysBase,
            boolean updateCurUser) throws GeneralException {
        this.userView.getHm().put("analyse_result", "begin");
        this.userView.getHm().put("error_info", "");
        
        /**
         * this.analyseType=101 集中处理
         */
        boolean needCloseConn = false;
        if (this.getDataAnayseParams() != null) {
            this.conn = (Connection) AdminDb.getConnection();
            needCloseConn = true;
            
            this.dao = new ContentDAO(this.conn);
            this.dataAnalyseUtils = new DataAnalyseUtils(this.conn, this.userView);
        }
        
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
        boolean isCorrect = true;

        try {
            initBaseWhere(code, kind);// 组织条件，人员权限条件，人员库条件
            String updateSQL = "";

            // 更新日期类型
            String field = this.getDateType();
            if (field.length() > 0) {
                updateSQL = "update " + this.fAnalyseTempTab + " set " + field + "=case when "
                        + Sql_switcher.isnull("class_id", "0") + " >0 and dkind<>3  then '工作日' when "
                        + Sql_switcher.isnull("class_id", "0")
                        + "=0 and dkind<>3 then '公休日'  when dkind=3 then '节假日'  end where cur_user='"
                        + this.userView.getUserName() + "'";
            }

            // 集中处理
            if ("101".equals(this.analyseType)) {
                DbWizard dbWizard = new DbWizard(this.conn);

                String kq_analyse_emp = "kq_analyse_emp"; // 集中处理人员表

                this.pub_desT_where = "cur_user='" + this.userView.getUserName() + "' ";
                String kq_analyse_date = "kq_analyse_date"; // 集中处理时间

                // 判断数据处理集中表是否存在
                if (!dbWizard.isExistTable(this.fAnalyseTempTab)) {
                    this.dataAnalyseUtils.createDataAnalyseTmp(this.fAnalyseTempTab); // 表不存在就建立表
                } else {
                    this.dataAnalyseUtils.checkAnalyseTempTab(this.fAnalyseTempTab);
                }

                this.dataAnalyseUtils.dropAnalyseTempTabCloumn(this.fAnalyseTempTab);// 删除多余字段
                boolean g_no = getG_no(this.fAnalyseTempTab, code, kind);
                if (!g_no) {
                    this.dataAnalyseUtils.createDataAnalyseTmp(this.fAnalyseTempTab); // 表不存在就建立表
                }

                if (!dbWizard.isExistTable(kq_analyse_emp, false)) {
                    this.dataAnalyseUtils.analyseTableTmp(kq_analyse_emp);
                }

                if (!dbWizard.isExistTable(kq_analyse_date, false)) {
                    this.dataAnalyseUtils.analysedateTableTmp(kq_analyse_date);
                }

                String date_Table = this.dataAnalyseUtils.createTimeTemp();// 建立时间临时表
                // 删除不是考勤期间的数据,kq_analyse_result只保存当前期间的数据
                deletedata();
                if ("all".equalsIgnoreCase(analysBase)) {
                    /** 集中处理时间表,* */
                    analyseDate(start_date, end_date, kq_analyse_date);
                    /** 结束* */
                    /** 集中处理 需再次处理人员表 * */
                    analyseEmp(kq_analyse_emp, code, kind);
                    /** 结束* */
                    // 先删除集中表中符合的数据，在写入
                    dateleAndInsertAnalyeEmps(this.fAnalyseTempTab, date_Table, code, kind, start_date, end_date);// 删除并写入数据
                    /*
                     * deleteAnalyeEmps(this.fAnalyseTempTab,date_Table,code,kind
                     * ,start_date,end_date);//先删除存在的数据； //从人员表插入（UsrA01...）
                     * insertAnalyeEmps
                     * (this.fAnalyseTempTab,date_Table,code,kind
                     * ,start_date,end_date);//给临时表插入数据
                     */
                } else if ("change".equalsIgnoreCase(analysBase)) {
                    // 从指定表插入（如：kq_emloy_change等保存有人员基本信息的表）
                    insertEmpIntoEmp(this.fAnalyseTempTab, "kq_employ_change", date_Table, start_date, end_date);
                } else {
                    return false;
                }

                String codewhere = "";
                String codeString = "";
                if ("1".equals(kind)) {
                    codewhere = " e0122 like '" + code + "%'";
                    codeString = " " + fAnalyseTempTab + ".e0122 like '" + code + "%'";
                } else if ("0".equals(kind)) {
                    codewhere = " e01a1 like '" + code + "%'";
                    codeString = " " + fAnalyseTempTab + ".e01a1 like '" + code + "%'";
                } else if ("2".equals(kind)) {
                    codewhere = " b0110 like '" + code + "%'";
                    codeString = " " + fAnalyseTempTab + ".b0110 like '" + code + "%'";
                } else if ("-1".equals(kind)) {
                    String t = code.substring(3, code.length());
                    String t1 = code.substring(0, 3);
                    codewhere = " a0100='" + t + "' and nbase='" + t1 + "'";
                    codeString = " " + fAnalyseTempTab + ".a0100='" + t + "' and " + fAnalyseTempTab + ".nbase='" + t1
                            + "'";
                }
                this.kqItem_hash = this.dataAnalyseUtils.count_Leave();
                delDifferKqType(this.fAnalyseTempTab, start_date, end_date, code, kind);// 删掉不需要的
                insertEmployeeShiftToTmp(this.fAnalyseTempTab, code, kind, start_date, end_date);// 添加对应的考勤班次
                insertClassInfoToTmp(this.fAnalyseTempTab, code, kind, start_date, end_date);// 添加考勤班次的基本班次信息
                
                // 导入考勤规则指标
                kqUtilsClass.leadingInItemToQ03(nbase_list, start_date, end_date, fAnalyseTempTab, codeString);
                
                // 更新日期类型指标
                if (field.length() > 0) {
                    dao.update(updateSQL);
                }
                
                //数据初始化完成，开始正式进行分析处理
                this.userView.getHm().put("analyse_result", "init_finished");
                
                if (!(this.creat_pick != null && "0".equals(this.creat_pick))) {
                    // 清空现有异常数据
                    this.dataAnalyseUtils.ceartFExceptCardTab(this.fExceptCardTab, this.analyseType);// 异常数据表
                    otherTempTabEmps("1", code, kind, start_date, end_date);
                    this.dataAnalyseUtils.createTranOverTimeTab(this.fTranOverTimeTab, this.analyseType);// 延时加班表
                    otherTempTabEmps("2", code, kind, start_date, end_date);
                    this.dataAnalyseUtils.createCompareBusiWithFactTab(this.fBusiCompareTab, this.analyseType);// 业务申请与实际刷卡情况表
                    this.dataAnalyseUtils.createCardToOverTimeTab(this.fBusiCompareTab, this.analyseType);
                    otherTempTabEmps("3", code, kind, start_date, end_date);
                    this.dataAnalyseUtils.createCardToOverTimeTab(this.cardToOverTime, this.analyseType);
                    otherTempTabEmps("4", code, kind, start_date, end_date);
                    // //过滤正常班次
                    checknormal(code, kind, start_date, end_date);// 过滤正常班次

                    // boolean isCorrect=true;
                    if (("1".equals(this.analyseType) || "101".equals(this.analyseType))
                            && this.kqParam.getNeed_busicompare() != null
                            && "1".equals(this.kqParam.getNeed_busicompare())) {
                        // 得到业务申请数据集
                        queryBusiData(this.fAnalyseTempTab, start_date, end_date, codewhere);
                    }

                    // 执行数据处理存储过程
                    execDataProcedureInDB();

                    if ("101".equals(this.analyseType) && this.kqParam.getNeed_busicompare() != null
                            && "1".equals(this.kqParam.getNeed_busicompare()) && !isExistPro("KqCompareBusiData")) {
                        // 对比分析业务申请与实际刷卡情况
                        compareBusiWithFactCards(this.fAnalyseTempTab, start_date, end_date, codewhere);
                    }
                    specialDisposal(this.fAnalyseTempTab, codewhere, start_date, end_date);// 按参数设置的“工作时间”来计算请假等业务
                    calcFactAbsent(this.fAnalyseTempTab, codewhere, start_date, end_date);// 处理旷工和夜班
                    updateRepairCardTimes(codewhere, start_date, end_date);// 处理补签到计数

                    // 更正休息日转加班的加班类型,节假日的加班置为节假日加班
                    updateCardToOvertimeAppType(codewhere, start_date, end_date);

                    // 根据公式,计算考勤数据
                    CountInfo countInfo = new CountInfo(userView, conn);
                    countInfo.countKQInfo(start_date, end_date, fAnalyseTempTab, codeString);
                    
                    // 最后判断 如果为休息班次并且所有的值为null；isok置为休息
                    setRest(this.fAnalyseTempTab, codewhere, start_date, end_date, -1); 
                }

                if ("0".equalsIgnoreCase(this.dataUpdateType)) {
                    updateDataToQ03(this.fAnalyseTempTab, this.kqItem_hash, start_date, end_date, code, kind);// 全部
                } else if ("1".equalsIgnoreCase(this.dataUpdateType)) {
                    updateBusiDataToQ03(this.fAnalyseTempTab, start_date, end_date, code, kind);// 只更新业务数据
                }
                /** ***********删除日期表************ */

                kqUtilsClass.dropTable(date_Table);
                // 添加排序字段dbid，a0000，并将值填到q03表中，此处将添加所有，不按人员权限走

            } else {
                // this.analyseType!=101 生成日明细，统计，分用户处理等走以前的分表处理方式 wangy
                // 建立临时表
                this.dataAnalyseUtils.createDataAnalyseTmp(this.fAnalyseTempTab);
                this.dataAnalyseUtils.dropAnalyseTempTabCloumn(this.fAnalyseTempTab);
                String date_Table = this.dataAnalyseUtils.createTimeTemp();// 建立时间临时表
                // initializtion_date_Table(code,date_Table,start_date,end_date);//给临时时间表插入数据

                if ("all".equalsIgnoreCase(analysBase)) {
                    // 从人员表插入（UsrA01...）
                    insertAnalyeEmps(this.fAnalyseTempTab, date_Table, code, kind, start_date, end_date);// 给临时表插入数据
                } else if ("change".equalsIgnoreCase(analysBase)) {
                    // 从指定表插入（如：kq_emloy_change等保存有人员基本信息的表）
                    insertEmpIntoEmp(this.fAnalyseTempTab, "kq_employ_change", date_Table, start_date, end_date);
                } else {
                    return false;
                }
                // 不处理，只给日明细添加人员信息
                this.kqItem_hash = this.dataAnalyseUtils.count_Leave();
                delDifferKqType(this.fAnalyseTempTab, start_date, end_date, code, kind);// 删掉不需要的
                insertEmployeeShiftToTmp(this.fAnalyseTempTab, code, kind, start_date, end_date);// 添加对应的考勤班次
                insertClassInfoToTmp(this.fAnalyseTempTab, code, kind, start_date, end_date);// 添加考勤班次的基本班次信息
                
                // 导入考勤规则指标
                kqUtilsClass.leadingInItemToQ03(nbase_list, start_date, end_date, fAnalyseTempTab, "");
                
                // 更新日期类型指标
                if (field.length() > 0) {
                    dao.update(updateSQL);
                }
                
                //数据初始化完成，开始正式进行分析处理
                this.userView.getHm().put("analyse_result", "init_finished");
                
                if (!(this.creat_pick != null && "0".equals(this.creat_pick))) {

                    this.dataAnalyseUtils.ceartFExceptCardTab(this.fExceptCardTab, this.analyseType);// 异常数据表
                    this.dataAnalyseUtils.createTranOverTimeTab(this.fTranOverTimeTab, this.analyseType);// 延时加班表
                    this.dataAnalyseUtils.createCompareBusiWithFactTab(this.fBusiCompareTab, this.analyseType);// 业务申请与实际刷卡情况表
                    this.dataAnalyseUtils.createCardToOverTimeTab(this.cardToOverTime, this.analyseType);// 休息日转加班

                    if (this.analyseType.equals(KqConstant.AnalyseType.MACHINE)
                            || this.analyseType.equals(KqConstant.AnalyseType.MACHINE_CENTRAL)) {
                        checknormal(code, kind, start_date, end_date);// 过滤正常班次
                    }

                    String codewhere = "";
                    if ("1".equals(kind)) {
                        codewhere = " e0122 like '" + code + "%'";
                    } else if ("0".equals(kind)) {
                        codewhere = " e01a1 like '" + code + "%'";
                    } else if ("2".equals(kind)) {
                        codewhere = " b0110 like '" + code + "%'";
                    } else if ("-1".equals(kind)) {
                        String t = code.substring(3, code.length());
                        String t1 = code.substring(0, 3);
                        codewhere = " a0100='" + t + "' and nbase='" + t1 + "'";
                    }
                    if ((this.analyseType.equals(KqConstant.AnalyseType.MACHINE) || this.analyseType
                            .equals(KqConstant.AnalyseType.MACHINE_CENTRAL))
                            && this.kqParam.getNeed_busicompare() != null
                            && "1".equals(this.kqParam.getNeed_busicompare())) {
                        // 得到业务申请数据集
                        queryBusiData(this.fAnalyseTempTab, start_date, end_date, codewhere);
                    }

                    // 执行数据处理存储过程
                    execDataProcedureInDB();

                    if ("1".equals(this.analyseType) && this.kqParam.getNeed_busicompare() != null
                            && "1".equals(this.kqParam.getNeed_busicompare()) && !isExistPro("KqCompareBusiData")) {

                        // 对比分析业务申请与实际刷卡情况
                        compareBusiWithFactCards(this.fAnalyseTempTab, start_date, end_date, codewhere);
                    }
                    specialDisposal(this.fAnalyseTempTab, codewhere, start_date, end_date);

                    // 按参数设置的“工作时间”来计算请假等业务
                    calcFactAbsent(this.fAnalyseTempTab, codewhere, start_date, end_date);
                    // 补签到计数
                    updateRepairCardTimes(codewhere, start_date, end_date);

                    // 根据公式,计算考勤数据
                    CountInfo countInfo = new CountInfo(userView, conn);
                    countInfo.countKQInfo(start_date, end_date, fAnalyseTempTab, "");
                    
                    // 最后判断 如果为休息班次并且所有的值为null；isok置为休息
                    setRest(this.fAnalyseTempTab, codewhere, start_date, end_date, -1);

                    // 更正休息日转加班的加班类型,节假日的加班置为节假日加班
                    updateCardToOvertimeAppType("", "", "");
                }
                
                if (!"1".equals(this.analyseType))// 如果
                {
                    if (this.dataUpdateType == null || "".equals(this.dataUpdateType)) {
                        this.dataUpdateType = "1";
                    }
                    
                    if ("0".equals(this.dataUpdateType)) {
                        updateDataToQ03(this.fAnalyseTempTab, this.kqItem_hash, start_date, end_date, "", "");// 全部
                    } else {
                        updateBusiDataToQ03(this.fAnalyseTempTab, start_date, end_date, "", "");// 只更新业务数据
                    }
                }
                /** ***********删除日期表************ */
                kqUtilsClass.dropTable(date_Table);
            }
            // 添加排序字段dbid，a0000，并将值填到q03表中，此处将添加所有，不按人员权限走
            if (!"1".equals(this.analyseType) && ("0".equals(this.dataUpdateType) || "1".equals(this.dataUpdateType))) {
                KqUtilsClass utils = new KqUtilsClass(this.conn);
                if (utils.addColumnToKq("q03")) {
                    DataAnalyseSync.updateOrder("q03", start_date, end_date, this.fAnalyseTempTab, this.conn);
                }
            }
            this.userView.getHm().put("analyse_result", "finished");
        } catch (GeneralException e) {
            this.userView.getHm().put("analyse_result", "error");
            String errorInfo = (e.getErrorCode() + e.getErrorDescription()).replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", "");
            errorInfo = errorInfo.replace("\"", " ").replace("'", " ");
            this.userView.getHm().put("error_info", errorInfo);
            throw GeneralExceptionHandler.Handle(e);
        } catch (Exception e) {
            this.userView.getHm().put("analyse_result", "error");
            this.userView.getHm().put("error_info", e.getMessage().replaceAll("\r\n", "").replaceAll("\n", "").replaceAll("\r", ""));
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            if (updateCurUser) // 更新当前操作人
            {
                updateCurUser();
            }
            
            if (needCloseConn) {
                PubFunc.closeDbObj(this.conn);
                this.conn = null;
            }         
        }
        return isCorrect;
    }

    /**
     * 由于数据处理默认是公休日加班,更正休息日转加班的加班类型,节假日为节假日加班
     * 
     * @param table
     *            休息日转加班表名
     */
    private void updateCardToOvertimeAppType(String codeWhere, String from, String to) {
        from = from + " 00:00";
        to = to + " 23:59";
        StringBuffer sql = new StringBuffer();
        sql.append("select nbase, a0100, begin_date, end_date from " + this.cardToOverTime);
        if ("101".equals(this.analyseType)) {
            sql.append(" where 1 = 1");
            if (codeWhere != null && codeWhere.length() > 0) {
                sql.append(" and " + codeWhere);
            }
            sql.append(" and begin_date >= " + Sql_switcher.dateValue(from));
            sql.append(" and end_date <= " + Sql_switcher.dateValue(to));
        }
        ContentDAO dao = new ContentDAO(conn);
        ResultSet rs = null;
        ArrayList list = new ArrayList();
        ArrayList oneList;
        ValidateAppOper validateAppOper = new ValidateAppOper(userView, conn);
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                oneList = new ArrayList();
                Timestamp start = rs.getTimestamp(3);
                boolean isFeast = validateAppOper.is_Feast(start);
                if (isFeast) {
                    Timestamp end = rs.getTimestamp(4);
                    String nbase = rs.getString(1);
                    String a0100 = rs.getString(2);
                    oneList.add(nbase);
                    oneList.add(a0100);
                    oneList.add(start);
                    oneList.add(end);
                    list.add(oneList);
                }
            }

            sql.setLength(0);
            sql.append("update " + this.cardToOverTime
                    + " set overtime_type = 11 where nbase = ? and a0100 = ? and begin_date = ? and end_date = ?");
            dao.batchUpdate(sql.toString(), list);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (GeneralException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新数据处理中间表 当前操作人 cur_user
     */
    public void updateCurUser() {

        String update_curUser = "update " + this.fAnalyseTempTab + " set cur_user = ? where cur_user=?";
        ArrayList uplist = new ArrayList();
        uplist.add(null);
        uplist.add(this.userView.getUserName());
        try {
            dao.update(update_curUser, uplist);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将日期类型更新到日明细表中,根据临时表中的dkind字段更新日明细q03表中的字段 dkind 为1，q03中的日期类型字段改为"工作日",
     * dkind 为2，q03中的日期类型字段改为"公休日"， dkind 为3，q03中的日期类型字段改为"节假日"，
     */
    public void updateDateType(ArrayList kq_dbase_list, String start_date, String end_date) {
        
        String dateType = this.getDateType();
        if (dateType.length() > 0) {
            //for (int i = 0; i < kq_dbase_list.size(); i++) {
                //String userbase = kq_dbase_list.get(i).toString();
               // String whereIN = RegisterInitInfoData.getWhereINSql(this.userView, userbase);
                String whereIn = RegisterInitInfoData.getKqEmpPrivWhr(conn, userView, "{TAB}");
                StringBuffer sql = new StringBuffer();
                sql.append("update q03 set ");
                sql.append(dateType);
                sql.append(" = (select case ");
                sql.append(" when dkind = '3'then '节假日' ");
                sql.append(" when class_id is null or class_id='0' then '公休日' ");
                sql.append(" else '工作日' end q03z8 ");
                sql.append(" from ");
                sql.append(this.fAnalyseTempTab);
                sql.append(" a ");
                sql.append(" where q03.q03z0 = a.q03z0 ");
                sql.append(" and q03.nbase=a.nbase and q03.a0100=a.a0100 ");
                sql.append(" and a.q03z0>='" + start_date + "' and a.q03z0<='" + end_date + "'");
                //sql.append(" and a.nbase='" + userbase + "'");
                if (!this.fAnalyseTempTab.toUpperCase().startsWith("KT_")) {
                    sql.append(" and ").append(whereIn.replace("{TAB}", "a"));
                }
//                if (whereIN != null && whereIN.length() > 0) {
//                    if (!this.userView.isSuper_admin()) {
//                        if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)
//                            sql.append(" and  EXISTS(select a0100 " + whereIN + " and " + userbase
//                                    + "A01.a0100=a.a0100)");
//                        else
//                            sql.append(" and  EXISTS(select a0100 " + whereIN + " where " + userbase
//                                    + "A01.a0100=a.a0100)");
//                    }
//                }
                sql.append(")");
                sql.append(" where exists ( ");
                sql.append(" select 1 from ");
                sql.append(this.fAnalyseTempTab);
                sql.append(" b ");
                sql.append(" where q03.q03z0 = b.q03z0 and ");
                sql.append(" q03.nbase=b.nbase and q03.a0100=b.a0100 ");
                sql.append(" and b.q03z0>='" + start_date + "' and b.q03z0<='" + end_date + "'");
                if (!this.fAnalyseTempTab.toUpperCase().startsWith("KT_")) {
                    sql.append(" and ").append(whereIn.replace("{TAB}", "b"));
                }
                //sql.append(\" and b.nbase='\" + userbase + \"'\");
//                if (whereIN != null && whereIN.length() > 0) {
//                    if (!this.userView.isSuper_admin()) {
//                        if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)
//                            sql.append(" and  EXISTS(select a0100 " + whereIN + " and " + userbase
//                                    + "A01.a0100=b.a0100)");
//                        else
//                            sql.append(" and  EXISTS(select a0100 " + whereIN + " where " + userbase
//                                    + "A01.a0100=b.a0100)");
//                    }
//                }
                sql.append(")");
                sql.append(" and q03.q03z0>='" + start_date + "' and q03.q03z0<='" + end_date + "'");
                //sql.append(" and q03.nbase='" + userbase + "'");
                sql.append(" and ").append(whereIn.replace("{TAB}", "Q03"));
//                if (whereIN != null && whereIN.length() > 0) {
//                    if (!this.userView.isSuper_admin()) {
//                        if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)
//                            sql.append(" and  EXISTS(select a0100 " + whereIN + " and " + userbase
//                                    + "A01.a0100=q03.a0100)");
//                        else
//                            sql.append(" and  EXISTS(select a0100 " + whereIN + " where " + userbase
//                                    + "A01.a0100=q03.a0100)");
//                    }
//                }
                try {
                    // System.out.println(sql);
                    dao.update(sql.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            //}

        }
    }

    /**
     * 获得日期类型字段的代码值 查找q03表中的"日期类型"字段（用户自定义添加的字段）
     * 
     * @return
     */
    private String getDateType() {
        String dateType = "";
        ArrayList list = DataDictionary.getFieldList("q03", Constant.USED_FIELD_SET);
        for (int i = 0; i < list.size(); i++) {
            FieldItem item = (FieldItem) list.get(i);
            if ("A".equalsIgnoreCase(item.getItemtype()) && "日期类型".equalsIgnoreCase(item.getItemdesc())) {
                dateType = item.getItemid();
            }
        }
        return dateType;
    }

    // 过滤班次正常的 wangyao
    private void checknormal(String code, String kind, String start_date, String end_date, String nbase, String whereIN)
            throws GeneralException {
        RowSet rowSet = null;
        try {
            KqItem kqItem = new KqItem(this.conn);
            String noOndutyCard1 = kqItem.getFieldIdByKqItemDesc("上班缺刷卡1");
            String noOffdutyCard1 = kqItem.getFieldIdByKqItemDesc("下班缺刷卡1");
            String noOndutyCard2 = kqItem.getFieldIdByKqItemDesc("上班缺刷卡2");
            String noOffdutyCard2 = kqItem.getFieldIdByKqItemDesc("下班缺刷卡2");
            String noOndutyCard3 = kqItem.getFieldIdByKqItemDesc("上班缺刷卡3");
            String noOffdutyCard3 = kqItem.getFieldIdByKqItemDesc("下班缺刷卡3");

            // EmpNetSignin empNetSignin=new
            // EmpNetSignin(this.userView,this.conn);
            // 先都置为 0 不正常
            String codewhere = "";
            String existswhere = "";
            String strDWhere = "";
            if (this.analyseType.equals(KqConstant.AnalyseType.MACHINE_CENTRAL)) {
                if ("-1".equals(kind) && code != null && code.length() >= 0) {
                    String t = code.substring(3, code.length());
                    String t1 = code.substring(0, 3);
                    codewhere = " a0100='" + t + "' and nbase='" + t1 + "'";
                } else if ("spec".equals(kind)) {
                    codewhere = this.whereCode_List.get(0).toString();// "a0100 in "
                                                                      // + code
                                                                      // +
                                                                      // " and nbase='"
                                                                      // + nbase
                                                                      // + "'";
                } else {
                    String orgField = "";
                    if ("1".equals(kind) && code != null && code.length() >= 0) {
                        orgField = "e0122";
                    } else if ("0".equals(kind) && code != null && code.length() >= 0) {
                        orgField = "e01a1";
                    } else if ("2".equals(kind) && code != null && code.length() >= 0) {
                        orgField = "b0110";
                    }

                    // codewhere="e0122 like '"+code+"%'";
                    if (!this.userView.isSuper_admin() && whereIN != null && whereIN.length() > 0) {
                        existswhere = "EXISTS(select 1 " + whereIN;
                        if (whereIN.toUpperCase().contains("WHERE")) {
                            existswhere = existswhere + " and ";
                        } else {
                            existswhere = existswhere + " where ";
                        }

                        existswhere = existswhere + nbase + "A01.a0100=K.a0100";

                    } else {
                        existswhere = "EXISTS(select 1 from " + nbase + "A01 where " + nbase + "A01.a0100=K.a0100";
                    }

                    if (!"".equals(orgField)) {
                        strDWhere = orgField + " like '" + code + "%'";
                        existswhere = existswhere + " and " + nbase + "A01." + orgField + " like '" + code + "%'";
                    }

                    existswhere = existswhere + ")";
                }
            }

            String Q15Existssql = " and q15.nbase='" + nbase + "' and K.nbase='" + nbase + "'";
            String Q11Existssql = " and q11.nbase='" + nbase + "' and K.nbase='" + nbase + "'";
            String Q13Existssql = " and q13.nbase='" + nbase + "' and K.nbase='" + nbase + "'";
            String temp_where = " and K.q03z0>='" + start_date + "' and K.q03z0<='" + end_date + "' ";

            if (codewhere != null && codewhere.length() > 0) {
                if ("-1".equals(kind)) {
                    Q11Existssql = Q11Existssql + " and " + codewhere;
                    Q13Existssql = Q13Existssql + " and " + codewhere;
                    Q15Existssql = Q15Existssql + " and " + codewhere;
                } else if (!"spec".equals(kind)) {
                    Q11Existssql = Q11Existssql + " and K." + codewhere + " and q11." + codewhere;
                    Q13Existssql = Q13Existssql + " and K." + codewhere + " and q13." + codewhere;
                    Q15Existssql = Q15Existssql + " and K." + codewhere + " and q15." + codewhere;
                } else {
                    String specCodeWhrK = codewhere.replaceAll("nbase", "k.nbase").replaceAll("a0100", "k.a0100");
                    String specCodeWhrQ = codewhere.replaceAll("nbase", "q11.nbase").replaceAll("a0100", "q11.a0100");
                    Q11Existssql = Q11Existssql + " and " + specCodeWhrK + " and " + specCodeWhrQ;

                    specCodeWhrQ = codewhere.replaceAll("nbase", "q13.nbase").replaceAll("a0100", "q13.a0100");
                    Q13Existssql = Q13Existssql + " and " + specCodeWhrK + " and " + specCodeWhrQ;

                    specCodeWhrQ = codewhere.replaceAll("nbase", "q15.nbase").replaceAll("a0100", "q15.a0100");
                    Q15Existssql = Q15Existssql + " and " + specCodeWhrK + " and " + specCodeWhrQ;
                }
            }

            if (existswhere != null && existswhere.length() > 0) {
                Q11Existssql = Q11Existssql + " and " + existswhere.replaceAll("K.a0100", "Q11.a0100");
                Q13Existssql = Q13Existssql + " and " + existswhere.replaceAll("K.a0100", "Q13.a0100");
                Q15Existssql = Q15Existssql + " and " + existswhere.replaceAll("K.a0100", "Q15.a0100");
            }

            StringBuffer ExecMySql = new StringBuffer();
            if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                ExecMySql.append("UPDATE  K set ISNormal=0 from " + this.fAnalyseTempTab + " K where q03z0>='"
                        + start_date + "' and q03z0<='" + end_date + "'");
            } else {
                ExecMySql.append("UPDATE  " + this.fAnalyseTempTab + " K set ISNormal=0 where q03z0>='" + start_date
                        + "' and q03z0<='" + end_date + "'");
            }

            ExecMySql.append(" and nbase ='" + nbase + "'");

            addWhrForAnalyseCenter(ExecMySql, nbase, codewhere, strDWhere, existswhere, whereIN);

            //this.dao.update(ExecMySql.toString());


                // 第一种情况 无排班 并且没有申请 ISNormal=1
                String strFromDt = "CAST(K.Q03Z0+' '+'00:00:01' AS DATETIME)";
                String strToDt = "CAST(K.Q03Z0+' '+'23:59:59' AS DATETIME)";
 
                if (Constant.MSSQL != Sql_switcher.searchDbServer()) {
                    strFromDt = "TO_DATE(K.Q03Z0||' 00:00:01', 'YYYY.MM.DD hh24:mi:ss')";
                    strToDt = "TO_DATE(K.Q03Z0||' 23:59:59', 'YYYY.MM.DD hh24:mi:ss')";
                }

                StringBuffer notClassSQL = new StringBuffer();
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    notClassSQL.append("UPDATE K SET ISNormal=1");
                    notClassSQL.append(" from " + this.fAnalyseTempTab + " K ");
                } else {
                    notClassSQL.append("UPDATE " + this.fAnalyseTempTab + " K SET ISNormal=1");                
                }

                notClassSQL.append(" WHERE NOT EXISTS(SELECT 1 FROM kq_class");
                notClassSQL.append(" WHERE kq_class.class_id=K.class_id " + temp_where + ")");
                notClassSQL.append(" and q03z0>='" + start_date);
                notClassSQL.append("' and q03z0<='" + end_date + "'");
                notClassSQL.append(" and nbase='" + nbase + "'");

                addWhrForAnalyseCenter(notClassSQL, nbase, codewhere, strDWhere, existswhere, whereIN);

                this.dao.update(notClassSQL.toString());

                notClassSQL.setLength(0);
                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                    notClassSQL.append("UPDATE K SET ISNormal=1 from " + this.fAnalyseTempTab + " K");
                } else {
                    notClassSQL.append("UPDATE " + this.fAnalyseTempTab + " K SET ISNormal=1");
                }
                notClassSQL.append(" WHERE " + Sql_switcher.isnull("class_id", "-1") + "=-1");
                notClassSQL.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
                notClassSQL.append(" and nbase='" + nbase + "'");

                addWhrForAnalyseCenter(notClassSQL, nbase, codewhere, strDWhere, existswhere, whereIN);

                this.dao.update(notClassSQL.toString());

                notClassSQL.setLength(0);
                // 第二种 有班次 并且没有申请
                StringBuffer class_sql = new StringBuffer();
                class_sql.append("select class_id,name from kq_class");
                // 需要检测延时加班的班次无法批量处理
                class_sql.append(" where " + Sql_switcher.isnull("check_tran_overtime", "'0'") + "<>'1'");
                
                // TODO oracle弹性班暂不处理
                if (Constant.ORACEL == Sql_switcher.searchDbServer()) {
                    class_sql.append(" AND " + Sql_switcher.isnull("onduty_flextime_1", "'aa'") + "='aa'");
                }

                // 三个上班时段的暂不处理
                class_sql.append(" AND (onduty_3 is null OR onduty_3='') ");
                
                // 有加班时段的班次暂不处理
                class_sql.append(" AND (onduty_4 is null OR onduty_4='')");

                //跨天班暂不批量处理
                //class_sql.append(" AND (class_id=0 or (offduty_2 is not null and offduty_2<>''  and onduty_1<offduty_2)");
               // class_sql.append("or ((offduty_2 is null or offduty_2='') and offduty_1 is not null and onduty_1<offduty_1))");
                
                //两个时段中间休息也要刷两次卡的暂不处理
                class_sql.append(" AND (onduty_2 IS NULL ");
                class_sql.append(" OR (onduty_2 IS NOT NULL");
                class_sql.append(" AND (").append(Sql_switcher.isnull("offduty_card_1", "0")).append("=0");
                class_sql.append(" OR ").append(Sql_switcher.isnull("onduty_card_2", "0")).append("=0)");
                class_sql.append("))");
                
                // 只取当前需处理数据中已排的班次
                class_sql.append(" AND class_id in (");
                class_sql.append("select class_id from " + this.fAnalyseTempTab + " K");
                class_sql.append(" where  q03z0>='" + start_date);
                class_sql.append("' and q03z0<='" + end_date + "'");

                if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                    class_sql.append(" and " + codewhere);
                }

                class_sql.append(" and nbase='" + nbase + "'");

                if (this.analyseType.equals(KqConstant.AnalyseType.MACHINE_CENTRAL) && existswhere != null
                        && existswhere.length() > 0) {
                    class_sql.append(" and " + existswhere);
                } else if (whereIN != null && whereIN.length() > 0) {
                    if (!this.userView.isSuper_admin()) {
                        class_sql.append(" and  EXISTS(select a0100 " + whereIN);
                        if (whereIN.toUpperCase().contains("WHERE")) {
                            class_sql.append(" and ");
                        } else {
                            class_sql.append(" where ");
                        }
                        class_sql.append(nbase + "A01.a0100=K.a0100)");
                    }
                }
                class_sql.append(")");
                rowSet = this.dao.search(class_sql.toString());

                //有与刷卡有前后跨天的情况，所以刷卡数据取数范围相应前后扩大一天
                String cardFromDate = DateUtils.FormatDate(DateUtils.addDays(DateUtils.getDate(start_date, "yyyy.MM.dd"), -1),"yyyy.MM.dd");
                String cardToDate = DateUtils.FormatDate(DateUtils.addDays(DateUtils.getDate(end_date, "yyyy.MM.dd"), 1),"yyyy.MM.dd");
                
                while (rowSet.next()) {
                    // 第二种 (1)classid=0 休息没有申请 ISNormal=1
                    String classid = rowSet.getString("class_id");
                    // 第二种 (2)有排班并且不为休息
                    HashMap map = getOnOffTime(classid);
                    // 第二种 (2)有排班并且不为休息 都有ISNormal=1
                    String onduty_card_1 = "";
                    String offduty_card_1 = "";
                    String onduty_card_2 = "";
                    String offduty_card_2 = "";
                    String offduty_card_3 = "";
                    String onduty_flextime_1 = "";
                    String offduty_flextime_1 = "";
                    String offduty_flextime_2 = "";
                    String offduty_flextime_3 = "";
                    String onduty_1 = "";

                    if (!"0".equalsIgnoreCase(classid)) {
                        onduty_card_1 = (String) map.get("onduty_card_1");
                        offduty_card_1 = (String) map.get("offduty_card_1");
                        onduty_card_2 = (String) map.get("onduty_card_2");
                        offduty_card_2 = (String) map.get("offduty_card_2");
                        offduty_card_3 = (String) map.get("offduty_card_3");
                        onduty_flextime_1 = (String) map.get("onduty_flextime_1");
                        offduty_flextime_1 = (String) map.get("offduty_flextime_1");
                        offduty_flextime_2 = (String) map.get("offduty_flextime_2");
                        offduty_flextime_3 = (String) map.get("offduty_flextime_3");
                        onduty_1 = (String) map.get("onduty_1");
                    }

                    StringBuffer updateSQL = new StringBuffer();
                    if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                        updateSQL.append("UPDATE  K SET ISNormal=1");
                    } else {
                        updateSQL.append("UPDATE " + this.fAnalyseTempTab + " K SET ISNormal=1");
                    }

                    String name = rowSet.getString("name");
                    // 判断是否需要统计班次
                    if (!"0".equals(classid)) {
                        String className = getClassName(name);
                        if (!"".equals(className) && className.length() > 0) {
                            updateSQL.append("," + className + "=1");
                        }
                    }

                    if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                        updateSQL.append(" from " + this.fAnalyseTempTab + " K  ");
                    } 
                    updateSQL.append(" WHERE class_id=" + classid);

                    String on_start_time_1 = ""; // 1上班
                    String on_end_time_1 = ""; // 1迟到
                    String offduty_1_1 = ""; // 第一时段下班
                    String offduty_2_2 = ""; // 第二时段下班
                    String offduty_3_3 = ""; // 第三时段下班
                    String xiaban = "";
                    if (!"0".equalsIgnoreCase(classid)) {
                        on_start_time_1 = (String) map.get("on_start_time_1"); // 1上班
                        on_end_time_1 = (String) map.get("on_end_time_1"); // 1迟到
                        offduty_1_1 = (String) map.get("offduty_1_1");
                        offduty_2_2 = (String) map.get("offduty_2_2");
                        offduty_3_3 = (String) map.get("offduty_3_3");

                        xiaban = "";
                        if ("1".equals(offduty_card_3)) {
                            xiaban = offduty_3_3;
                        } else if ("1".equals(offduty_card_2)) {
                            xiaban = offduty_2_2;
                        } else if ("1".equals(offduty_card_1)) {
                            xiaban = offduty_1_1;
                        } else {
                            if (!"".equals(offduty_2_2) && offduty_2_2.length() > 0) {
                                xiaban = offduty_2_2;
                            } else {
                                xiaban = offduty_1_1;
                            }
                        }
                        // 若迟到大于上班并且上班大于下班+1天
                        if (on_start_time_1.compareTo(xiaban) > 0) {
                            updateSQL.append(" AND NOT EXISTS(SELECT 1 FROM Q11");
                            updateSQL.append(" WHERE Q11Z5='03'  " + temp_where + "");
                            updateSQL.append(Q11Existssql);
                            updateSQL.append(" AND Q11.NBASE=K.NBASE");
                            updateSQL.append(" AND Q11.A0100=K.A0100");
                            updateSQL.append(" AND ((Q11Z1>=" + strFromDt + " AND Q11Z1<" + strToDt + "+1)");
                            updateSQL.append(" OR (Q11Z3>" + strFromDt + " AND Q11Z3<=" + strToDt + "+1)");
                            updateSQL.append(" OR (Q11Z1<" + strFromDt + " AND Q11Z3>" + strToDt + "+1)))");
                            updateSQL.append(" AND NOT EXISTS(SELECT 1 FROM Q13");
                            updateSQL.append(" WHERE Q13Z5='03' " + temp_where + "");
                            updateSQL.append(Q13Existssql);
                            updateSQL.append(" AND Q13.NBASE=K.NBASE");
                            updateSQL.append(" AND Q13.A0100=K.A0100");
                            updateSQL.append(" AND ((Q13Z1>=" + strFromDt + " AND Q13Z1<" + strToDt + "+1)");
                            updateSQL.append(" OR (Q13Z3>" + strFromDt + " AND Q13Z3<=" + strToDt + "+1)");
                            updateSQL.append(" OR (Q13Z1<" + strFromDt + " AND Q13Z3>" + strToDt + "+1)))");

                            updateSQL.append(" AND NOT EXISTS(SELECT 1 FROM Q15");
                            updateSQL.append(" WHERE Q15Z5='03'  " + temp_where + "");
                            updateSQL.append(Q15Existssql);
                            updateSQL.append(" AND Q15.NBASE=K.NBASE");
                            updateSQL.append(" AND Q15.A0100=K.A0100");
                            updateSQL.append(" AND ((Q15Z1>=" + strFromDt + " AND Q15Z1<" + strToDt + "+1)");
                            updateSQL.append(" OR (Q15Z3>" + strFromDt + " AND Q15Z3<=" + strToDt + "+1)");
                            updateSQL.append(" OR (Q15Z1<" + strFromDt + " AND Q15Z3>" + strToDt + "+1)))");
                        } else {
                            updateSQL.append(" AND NOT EXISTS(SELECT 1 FROM Q11");
                            updateSQL.append(" WHERE Q11Z5='03'  " + temp_where + "");
                            updateSQL.append(Q11Existssql);
                            updateSQL.append(" AND Q11.NBASE=K.NBASE");
                            updateSQL.append(" AND Q11.A0100=K.A0100");
                            updateSQL.append(" AND ((Q11Z1>=" + strFromDt + " AND Q11Z1<" + strToDt + ")");
                            updateSQL.append(" OR (Q11Z3>" + strFromDt + " AND Q11Z3<=" + strToDt + ")");
                            updateSQL.append(" OR (Q11Z1<" + strFromDt + " AND Q11Z3>" + strToDt + ")))");
                            updateSQL.append(" AND NOT EXISTS(SELECT 1 FROM Q13");
                            updateSQL.append(" WHERE Q13Z5='03'  " + temp_where + "");
                            updateSQL.append(Q13Existssql);
                            updateSQL.append(" AND Q13.NBASE=K.NBASE");
                            updateSQL.append(" AND Q13.A0100=K.A0100");
                            updateSQL.append(" AND ((Q13Z1>=" + strFromDt + " AND Q13Z1<" + strToDt + ")");
                            updateSQL.append(" OR (Q13Z3>" + strFromDt + " AND Q13Z3<=" + strToDt + ")");
                            updateSQL.append(" OR (Q13Z1<" + strFromDt + " AND Q13Z3>" + strToDt + ")))");

                            updateSQL.append(" AND NOT EXISTS(SELECT 1 FROM Q15");
                            updateSQL.append(" WHERE Q15Z5='03' " + temp_where + "");
                            updateSQL.append(Q15Existssql);
                            updateSQL.append(" AND Q15.NBASE=K.NBASE");
                            updateSQL.append(" AND Q15.A0100=K.A0100");
                            updateSQL.append(" AND ((Q15Z1>=" + strFromDt + " AND Q15Z1<" + strToDt + ")");
                            updateSQL.append(" OR (Q15Z3>" + strFromDt + " AND Q15Z3<=" + strToDt + ")");
                            updateSQL.append(" OR (Q15Z1<" + strFromDt + " AND Q15Z3>" + strToDt + ")))");
                        }
                    } else {
                        updateSQL.append(" AND NOT EXISTS(SELECT 1 FROM Q11");
                        updateSQL.append(" WHERE Q11Z5='03' " + temp_where + "");
                        updateSQL.append(Q11Existssql);
                        updateSQL.append(" AND Q11.NBASE=K.NBASE");
                        updateSQL.append(" AND Q11.A0100=K.A0100");
                        updateSQL.append(" AND ((Q11Z1>=" + strFromDt + " AND Q11Z1<" + strToDt + ")");
                        updateSQL.append(" OR (Q11Z3>" + strFromDt + " AND Q11Z3<=" + strToDt + ")");
                        updateSQL.append(" OR (Q11Z1<" + strFromDt + " AND Q11Z3>" + strToDt + ")))");
                        updateSQL.append(" AND NOT EXISTS(SELECT 1 FROM Q13");
                        updateSQL.append(" WHERE Q13Z5='03'  " + temp_where + "");
                        updateSQL.append(Q13Existssql);
                        updateSQL.append(" AND Q13.NBASE=K.NBASE");
                        updateSQL.append(" AND Q13.A0100=K.A0100");
                        updateSQL.append(" AND ((Q13Z1>=" + strFromDt + " AND Q13Z1<" + strToDt + ")");
                        updateSQL.append(" OR (Q13Z3>" + strFromDt + " AND Q13Z3<=" + strToDt + ")");
                        updateSQL.append(" OR (Q13Z1<" + strFromDt + " AND Q13Z3>" + strToDt + ")))");

                        updateSQL.append(" AND NOT EXISTS(SELECT 1 FROM Q15");
                        updateSQL.append(" WHERE Q15Z5='03'  " + temp_where + "");
                        updateSQL.append(Q15Existssql);
                        updateSQL.append(" AND Q15.NBASE=K.NBASE");
                        updateSQL.append(" AND Q15.A0100=K.A0100");
                        updateSQL.append(" AND ((Q15Z1>=" + strFromDt + " AND Q15Z1<" + strToDt + ")");
                        updateSQL.append(" OR (Q15Z3>" + strFromDt + " AND Q15Z3<=" + strToDt + ")");
                        updateSQL.append(" OR (Q15Z1<" + strFromDt + " AND Q15Z3>" + strToDt + ")))");
                    }
                    
                    if (!"0".equalsIgnoreCase(classid) && map != null) {
                            if ((onduty_card_1 != null && !"0".equals(onduty_card_1) && !"2".equals(onduty_card_1))
                                    || (offduty_card_1 != null && !"0".equals(offduty_card_1) && !"2".equals(offduty_card_1))
                                    || (onduty_card_2 != null && !"0".equals(onduty_card_2) && !"2".equals(onduty_card_2))
                                    || (offduty_card_2 != null && !"0".equals(offduty_card_2) && !"2".equals(offduty_card_2))) {
                                String aStr = "CAST(A.work_date+' '+A.work_time+':00' AS DATETIME)";
                                if (Constant.ORACEL == Sql_switcher.searchDbServer()) {
                                    aStr = "TO_DATE(A.work_date||' '||A.work_time||':00','YYYY.MM.DD hh24:mi:ss')";    
                                }

                                // 时段1 进1
                                if ("1".equals(onduty_card_1)) {
                                    on_start_time_1 = (String) map.get("on_start_time_1");
                                    if ("".equals(onduty_flextime_1)) {
                                        on_end_time_1 = (String) map.get("on_end_time_1");
                                    } else {
                                        on_end_time_1 = onduty_flextime_1;
                                    }
                                    
                                    updateSQL.append(" AND EXISTS(SELECT 1 FROM kq_originality_data A");
                                    updateSQL.append(" WHERE  A.SP_FLAG='03'");
                                    updateSQL.append(" AND " + Sql_switcher.isnull("A.iscommon", "'1'") + "='1'");
                                    updateSQL.append(" AND A.nbase='" + nbase + "'");
                                    updateSQL.append(" AND " + Sql_switcher.sqlNull("A.INOUT_FLAG","0") + "<>-1");
                                    updateSQL.append(" AND A.NBASE=K.NBASE");
                                    updateSQL.append(" AND A.A0100=K.A0100");
                                    
                                    String strShiftFrom = "CAST(K.Q03Z0+' '+'" + on_start_time_1 + ":00' AS DATETIME)";
                                    String strShiftTo = "CAST(K.Q03Z0+' '+'" + on_end_time_1 + ":59' AS DATETIME)";
                                    if (Constant.ORACEL == Sql_switcher.searchDbServer()) {
                                        strShiftFrom = "TO_DATE(K.Q03Z0||' '||'" + on_start_time_1 + ":00', 'YYYY.MM.DD hh24:mi:ss')";
                                        strShiftTo = "TO_DATE(K.Q03Z0||' '||'" + on_end_time_1 + ":59', 'YYYY.MM.DD hh24:mi:ss')";
                                    }

                                    //上班刷卡与上班点之间跨天
                                    if (on_start_time_1.compareTo(onduty_1) > 0) {
                                        strShiftFrom = strShiftFrom + "-1";
                                    }
                                    
                                    //上班迟到或弹性班时点与上班点之间跨天
                                    if (onduty_1.compareTo(on_end_time_1)>0) {
                                        strShiftTo = strShiftTo + "+1";
                                    }
                                    
                                    updateSQL.append(" " + temp_where);
                                    updateSQL.append(" AND " + aStr + ">=" + strShiftFrom);
                                    updateSQL.append(" AND " + aStr + "<=" + strShiftTo);
                                   
                                    updateSQL.append(" and A.work_date>='" + cardFromDate + "' and A.work_date<='" + cardToDate
                                            + "' ");
                                    if (existswhere != null && existswhere.length() > 0) {
                                        updateSQL.append(" and " + existswhere.replaceAll("K.a0100", "A.a0100"));
                                    } else if (whereIN != null && whereIN.length() > 0) {
                                        if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                                            updateSQL.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase
                                                    + "A01.a0100=A.a0100)");
                                            
                                        } else {
                                            updateSQL.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase
                                                    + "A01.a0100=A.a0100)");
                                        }
                                    }
                                    if (codewhere != null && codewhere.length() > 0) {
                                        updateSQL.append(" and " + codewhere);
                                    }
                                    updateSQL.append(")");
                                }
                                
                                if ("1".equals(offduty_card_1)) {
                                    String leave_early_1 = "";
                                    if ("".equals(offduty_flextime_1)) {
                                        leave_early_1 = (String) map.get("leave_early_1"); // 早退
                                    } else {
                                        //弹性班直接从弹性下班算起
                                        leave_early_1 = offduty_flextime_1;
                                    }
                                    
                                    String offduty_1 = (String)map.get("offduty_1");
                                    
                                    String strShiftFrom = "CAST(K.Q03Z0+' '+'" + leave_early_1 + ":00' AS DATETIME)";
                                    String strShiftTo = "CAST(K.Q03Z0+' '+'" + offduty_1 + ":59' AS DATETIME)";
                                    if (Constant.ORACEL == Sql_switcher.searchDbServer()) {
                                        strShiftFrom = "TO_DATE(K.Q03Z0||' '||'" + leave_early_1 + ":00', 'YYYY.MM.DD hh24:mi:ss')";
                                        strShiftTo = "TO_DATE(K.Q03Z0||' '||'" + offduty_1 + ":59', 'YYYY.MM.DD hh24:mi:ss')";
                                    }

                                    //迟到点与上班点之间跨天
                                    if(leave_early_1.compareTo(onduty_1)<0) {
                                        strShiftFrom = strShiftFrom + "+1";
                                    }
                                    
                                    if(offduty_1.compareTo(onduty_1)<0) {
                                        strShiftTo = strShiftTo + "+1";
                                    }
                                    
                                    updateSQL.append(" AND EXISTS(SELECT 1 FROM kq_originality_data A");
                                    updateSQL.append(" WHERE  A.SP_FLAG='03'");
                                    updateSQL.append(" AND " + Sql_switcher.isnull("A.iscommon", "'1'") + "='1'");
                                    updateSQL.append(" AND A.nbase='" + nbase + "'");
                                    updateSQL.append(" AND " + Sql_switcher.sqlNull("A.INOUT_FLAG","0") + "<>1");
                                    updateSQL.append(" AND A.NBASE=K.NBASE");
                                    updateSQL.append(" AND A.A0100=K.A0100");
                                    updateSQL.append(" " + temp_where);
                                    updateSQL.append(" AND " + aStr + ">=" + strShiftFrom);
                                    updateSQL.append(" AND " + aStr + "<=" + strShiftTo);
                                    
                                    updateSQL.append(" and A.work_date>='" + cardFromDate + "' and A.work_date<='" + cardToDate
                                            + "' ");
                                    if (existswhere != null && existswhere.length() > 0) {
                                        updateSQL.append(" and " + existswhere.replaceAll("K.a0100", "A.a0100"));
                                    } else if (whereIN != null && whereIN.length() > 0) {
                                        if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                                            updateSQL.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase
                                                    + "A01.a0100=A.a0100)");
                                            
                                        } else {
                                            updateSQL.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase
                                                    + "A01.a0100=A.a0100)");
                                        }
                                    }
                                    if (codewhere != null && codewhere.length() > 0) {
                                        updateSQL.append(" and " + codewhere);
                                    }
                                    updateSQL.append(")");
                                }
                                
                                if ("1".equals(onduty_card_1) && "1".equals(offduty_card_1)) {
                                    
                                }
                                
                                // 第二时段
                                if ("1".equals(onduty_card_2)) {
                                    String onduty_start_2 = (String) map.get("onduty_start_2"); // 时段二上班起刷卡
                                    String be_late_work_2 = (String) map.get("be_late_work_2"); // 迟到
                                    
                                    String strShiftFrom = "CAST(K.Q03Z0+' '+'" + onduty_start_2 + ":00' AS DATETIME)";
                                    String strShiftTo = "CAST(K.Q03Z0+' '+'" + be_late_work_2 + ":59' AS DATETIME)";
                                    if (Constant.ORACEL == Sql_switcher.searchDbServer()) {
                                        strShiftFrom = "TO_DATE(K.Q03Z0||' '||'" + onduty_start_2 + ":00', 'YYYY.MM.DD hh24:mi:ss')";
                                        strShiftTo = "TO_DATE(K.Q03Z0||' '||'" + be_late_work_2 + ":59', 'YYYY.MM.DD hh24:mi:ss')";
                                    }

                                    // 上班刷卡起前跨天
                                    if (onduty_1.compareTo(onduty_start_2)>=0) {
                                        strShiftFrom = strShiftFrom + "+1";
                                    }
                                    // 迟到与上班点跨天
                                    if (onduty_1.compareTo(be_late_work_2)>0) {
                                        strShiftTo = strShiftTo + "+1";
                                    } 
                                    
                                    updateSQL.append(" AND EXISTS(SELECT 1 FROM kq_originality_data A");
                                    updateSQL.append(" WHERE  A.SP_FLAG='03'");
                                    updateSQL.append(" AND " + Sql_switcher.isnull("A.iscommon", "'1'") + "='1'");
                                    updateSQL.append(" AND A.nbase='" + nbase + "'");
                                    updateSQL.append(" AND " + Sql_switcher.sqlNull("A.INOUT_FLAG","0") + "<>-1");
                                    updateSQL.append(" AND A.NBASE=K.NBASE");
                                    updateSQL.append(" AND A.A0100=K.A0100");
                                    updateSQL.append(" AND " + aStr + ">=" + strShiftFrom);
                                    updateSQL.append(" AND " + aStr + "<=" + strShiftTo);
                                   
                                    updateSQL.append(" and A.work_date>='" + cardFromDate + "' and A.work_date<='" + cardToDate
                                            + "' ");
                                    if (existswhere != null && existswhere.length() > 0) {
                                        updateSQL.append(" and " + existswhere.replaceAll("K.a0100", "A.a0100"));
                                    } else if (whereIN != null && whereIN.length() > 0) {
                                        if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                                            updateSQL.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase
                                                    + "A01.a0100=A.a0100)");
                                            
                                        } else {
                                            updateSQL.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase
                                                    + "A01.a0100=A.a0100)");
                                        }
                                    }
                                    
                                    if (codewhere != null && codewhere.length() > 0) {
                                        updateSQL.append(" and " + codewhere);
                                    }
                                    updateSQL.append(")");
                                }
                                
                                if ("1".equals(offduty_card_2)) {
                                    String leave_early_2 = "";
                                    if ("".equals(offduty_flextime_2)) {
                                        leave_early_2 = (String) map.get("leave_early_2"); // 早退
                                    } else {
                                        leave_early_2 = offduty_flextime_2;
                                    }
                                    
                                    String offduty_2 = (String)map.get("offduty_2");
                                    
                                    String strShiftFrom = "CAST(K.Q03Z0+' '+'" + leave_early_2 + ":00' AS DATETIME)";
                                    String strShiftTo = "CAST(K.Q03Z0+' '+'" + offduty_2 + ":59' AS DATETIME)";
                                    if (Constant.ORACEL == Sql_switcher.searchDbServer()) {
                                        strShiftFrom = "TO_DATE(K.Q03Z0||' '||'" + leave_early_2 + ":00', 'YYYY.MM.DD hh24:mi:ss')";
                                        strShiftTo = "TO_DATE(K.Q03Z0||' '||'" + offduty_2 + ":59', 'YYYY.MM.DD hh24:mi:ss')";
                                    }

                                    if (leave_early_2.compareTo(onduty_1)<=0) {
                                        strShiftFrom = strShiftFrom + "+1";
                                    }
                                    
                                    //TODO 是否需要考虑下班点已经跨天，但刷卡止再次跨天的情况？
                                    if (offduty_2.compareTo(onduty_1)<=0) {
                                        strShiftTo = strShiftTo + "+1";
                                    }
                                    
                                    updateSQL.append(" AND EXISTS(SELECT 1 FROM kq_originality_data A");
                                    updateSQL.append(" WHERE  A.SP_FLAG='03'");
                                    updateSQL.append(" AND " + Sql_switcher.isnull("A.iscommon", "'1'") + "='1'");
                                    updateSQL.append(" AND A.nbase='" + nbase + "'");
                                    updateSQL.append(" AND " + Sql_switcher.sqlNull("A.INOUT_FLAG","0") + "<>1");
                                    updateSQL.append(" AND A.NBASE=K.NBASE");
                                    updateSQL.append(" AND A.A0100=K.A0100");
                                    updateSQL.append(" AND " + aStr + ">=" + strShiftFrom);
                                    updateSQL.append(" AND " + aStr + "<=" + strShiftTo);
                                    
                                    updateSQL.append(" and A.work_date>='" + cardFromDate + "' and A.work_date<='" + cardToDate
                                            + "' ");
                                    if (existswhere != null && existswhere.length() > 0) {
                                        updateSQL.append(" and " + existswhere.replaceAll("K.a0100", "A.a0100"));
                                    } else if (whereIN != null && whereIN.length() > 0) {
                                        if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                                            updateSQL.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase
                                                    + "A01.a0100=A.a0100)");
                                            
                                        } else {
                                            updateSQL.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase
                                                    + "A01.a0100=A.a0100)");
                                        }
                                    }
                                    
                                    if (codewhere != null && codewhere.length() > 0) {
                                        updateSQL.append(" and " + codewhere);
                                    }
                                    updateSQL.append(")");
                                }
                            }
                    }
                    updateSQL.append(" and q03z0>='" + start_date + "'");
                    updateSQL.append(" and q03z0<='" + end_date + "'");
                    updateSQL.append(" and nbase='" + nbase + "'");
                    addWhrForAnalyseCenter(updateSQL, nbase, codewhere, strDWhere, existswhere, whereIN);
                    this.dao.update(updateSQL.toString());

                    // 要求刷卡 没有申请 也没有刷卡记录 ISNormal=1 isOK='旷工' 旷工标记符记录
                    // 缺刷卡次数依次置为1
                    // 旷工不需要考虑弹性情况
                    if (!"0".equalsIgnoreCase(classid)) {
                        // 得到旷工时常
                        String absentee = getAbsenteeHours(classid);
                        // HashMap map=getOnOffTime(classid);
                        if (map != null) {
                            // 第二种 (2)有排班并且不为休息 都有ISNormal=1
                            onduty_card_1 = (String) map.get("onduty_card_1");
                            offduty_card_1 = (String) map.get("offduty_card_1");
                            onduty_card_2 = (String) map.get("onduty_card_2");
                            offduty_card_2 = (String) map.get("offduty_card_2");
                            if ((onduty_card_1 != null && !"0".equals(onduty_card_1) && !"2".equals(onduty_card_1))
                                    || (offduty_card_1 != null && !"0".equals(offduty_card_1) && !"2"
                                            .equals(offduty_card_1))
                                    || (offduty_card_2 != null && !"0".equals(offduty_card_2) && !"2"
                                            .equals(offduty_card_2))) {
                                // 有排班 无申请 无刷卡 计为旷工
                                StringBuffer updateSQLNULL = new StringBuffer();
                                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                                    updateSQLNULL.append("UPDATE K SET ISNormal=1,isOK='旷工'");
                                } else {                                    
                                    updateSQLNULL.append("UPDATE " + this.fAnalyseTempTab + " K SET ISNormal=1,isOK='旷工'");
                                }

                                if (!"".equals(absentee) && absentee.length() > 0) {
                                    updateSQLNULL.append("," + absentee);
                                }

                                // 记录缺刷卡次数
                                if (!"".equals(noOndutyCard1) && onduty_card_1 != null && !"0".equals(onduty_card_1)) {
                                    updateSQLNULL.append(",").append(noOndutyCard1).append("=1");
                                }

                                if (!"".equals(noOffdutyCard1) && offduty_card_1 != null && !"0".equals(offduty_card_1)) {
                                    updateSQLNULL.append(",").append(noOffdutyCard1).append("=1");
                                }

                                if (!"".equals(noOndutyCard2) && onduty_card_2 != null && !"0".equals(onduty_card_2)) {
                                    updateSQLNULL.append(",").append(noOndutyCard2).append("=1");
                                }

                                if (!"".equals(noOffdutyCard2) && offduty_card_2 != null && !"0".equals(offduty_card_2)) {
                                    updateSQLNULL.append(",").append(noOffdutyCard2).append("=1");
                                }

                                // TODO 没考虑三时段班次
                                // if (!"".equals(noOndutyCard3) &&
                                // onduty_card_3 != null &&
                                // !onduty_card_3.equals("0"))
                                // updateSQLNULL.append(",").append(noOndutyCard3).append("=1");

                                // if (!"".equals(noOffdutyCard3) &&
                                // offduty_card_3 != null &&
                                // !offduty_card_3.equals("0"))
                                // updateSQLNULL.append(",").append(noOffdutyCard3).append("=1");
                                if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                                    updateSQLNULL.append(" from " + this.fAnalyseTempTab + " K");
                                }
                                updateSQLNULL.append(" WHERE class_id=" + classid);

                                // 若迟到大于上班并且上班大于下班+1天
                                String aStr = "";
                                if (on_start_time_1.compareTo(xiaban) > 0) {
                                    updateSQLNULL.append(" AND NOT EXISTS(SELECT 1 FROM Q11");
                                    updateSQLNULL.append(" WHERE Q11Z5='03' " + temp_where + "");
                                    updateSQLNULL.append(Q11Existssql);
                                    updateSQLNULL.append(" AND Q11.NBASE=K.NBASE");
                                    updateSQLNULL.append(" AND Q11.A0100=K.A0100");
                                    updateSQLNULL
                                            .append(" AND ((Q11Z1>=" + strFromDt + " AND Q11Z1<" + strToDt + "+1)");
                                    updateSQLNULL.append(" OR (Q11Z3>" + strFromDt + " AND Q11Z3<=" + strToDt + "+1)");
                                    updateSQLNULL.append(" OR (Q11Z1<" + strFromDt + " AND Q11Z3>" + strToDt + "+1)))");

                                    updateSQLNULL.append(" AND NOT EXISTS(SELECT 1 FROM Q13");
                                    updateSQLNULL.append(" WHERE Q13Z5='03' " + temp_where + "");
                                    updateSQLNULL.append(Q13Existssql);
                                    updateSQLNULL.append(" AND Q13.NBASE=K.NBASE");
                                    updateSQLNULL.append(" AND Q13.A0100=K.A0100");
                                    updateSQLNULL
                                            .append(" AND ((Q13Z1>=" + strFromDt + " AND Q13Z1<" + strToDt + "+1)");
                                    updateSQLNULL.append(" OR (Q13Z3>" + strFromDt + " AND Q13Z3<=" + strToDt + "+1)");
                                    updateSQLNULL.append(" OR (Q13Z1<" + strFromDt + " AND Q13Z3>" + strToDt + "+1)))");

                                    updateSQLNULL.append(" AND NOT EXISTS(SELECT 1 FROM Q15");
                                    updateSQLNULL.append(" WHERE Q15Z5='03'  " + temp_where + "");
                                    updateSQLNULL.append(Q15Existssql);
                                    updateSQLNULL.append(" AND Q15.NBASE=K.NBASE");
                                    updateSQLNULL.append(" AND Q15.A0100=K.A0100");
                                    updateSQLNULL
                                            .append(" AND ((Q15Z1>=" + strFromDt + " AND Q15Z1<" + strToDt + "+1)");
                                    updateSQLNULL.append(" OR (Q15Z3>" + strFromDt + " AND Q15Z3<=" + strToDt + "+1)");
                                    updateSQLNULL.append(" OR (Q15Z1<" + strFromDt + " AND Q15Z3>" + strToDt + "+1)))");

                                    String start_date1 = DateUtils.format(
                                            DateUtils.addDays(DateUtils.getDate(start_date, "yyyy.MM.dd"), -1),
                                            "yyyy.MM.dd");
                                    
                                    aStr = combineDateTimeSQL("A.work_date", "A.work_time");

                                    updateSQLNULL.append(" AND NOT EXISTS (SELECT 1 FROM kq_originality_data A");
                                    updateSQLNULL.append(" WHERE A.SP_FLAG='03'");
                                    updateSQLNULL.append(" AND " + Sql_switcher.isnull("A.iscommon", "'1'") + "='1'");
                                    updateSQLNULL.append(" AND A.nbase='" + nbase + "'");
                                    updateSQLNULL.append(" AND A.NBASE=K.NBASE");
                                    updateSQLNULL.append(" AND A.A0100=K.A0100");

                                    //linbz 32193 跨天班次处理刷卡点时间范围错误
                                    updateSQLNULL.append(" AND A.work_date>='" + cardFromDate + "'");
                                    updateSQLNULL.append(" AND A.work_date<='" + cardToDate + "' ");
                                    
                                    if (existswhere != null && existswhere.length() > 0) {
                                        updateSQLNULL.append(" AND " + existswhere);
                                    } else if (whereIN != null && whereIN.length() > 0) {
                                        if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                                            updateSQLNULL.append(" AND  EXISTS(select a0100 " + whereIN + " and "
                                                    + nbase + "A01.a0100=A.a0100)");

                                        } else {
                                            updateSQLNULL.append(" AND  EXISTS(select a0100 " + whereIN + " and "
                                                    + nbase + "A01.a0100=A.a0100)");
                                        }
                                    }
                                    if (codewhere != null && codewhere.length() > 0) {
                                        updateSQLNULL.append(" AND " + codewhere);
                                    }
                                } else {
                                    updateSQLNULL.append(" AND NOT EXISTS(SELECT 1 FROM Q11");
                                    updateSQLNULL.append(" WHERE Q11Z5='03' " + temp_where + "");
                                    updateSQLNULL.append(Q11Existssql);
                                    updateSQLNULL.append(" AND Q11.NBASE=K.NBASE");
                                    updateSQLNULL.append(" AND Q11.A0100=K.A0100");
                                    updateSQLNULL.append(" AND ((Q11Z1>=" + strFromDt + " AND Q11Z1<" + strToDt + ")");
                                    updateSQLNULL.append(" OR (Q11Z3>" + strFromDt + " AND Q11Z3<=" + strToDt + ")");
                                    updateSQLNULL.append(" OR (Q11Z1<" + strFromDt + " AND Q11Z3>" + strToDt + ")))");

                                    updateSQLNULL.append(" AND NOT EXISTS(SELECT 1 FROM Q13");
                                    updateSQLNULL.append(" WHERE Q13Z5='03' " + temp_where + "");
                                    updateSQLNULL.append(Q13Existssql);
                                    updateSQLNULL.append(" AND Q13.NBASE=K.NBASE");
                                    updateSQLNULL.append(" AND Q13.A0100=K.A0100");
                                    updateSQLNULL.append(" AND ((Q13Z1>=" + strFromDt + " AND Q13Z1<" + strToDt + ")");
                                    updateSQLNULL.append(" OR (Q13Z3>" + strFromDt + " AND Q13Z3<=" + strToDt + ")");
                                    updateSQLNULL.append(" OR (Q13Z1<" + strFromDt + " AND Q13Z3>" + strToDt + ")))");

                                    updateSQLNULL.append(" AND NOT EXISTS(SELECT 1 FROM Q15");
                                    updateSQLNULL.append(" WHERE Q15Z5='03' " + temp_where + "");
                                    updateSQLNULL.append(Q15Existssql);
                                    updateSQLNULL.append(" AND Q15.NBASE=K.NBASE");
                                    updateSQLNULL.append(" AND Q15.A0100=K.A0100");
                                    updateSQLNULL.append(" AND ((Q15Z1>=" + strFromDt + " AND Q15Z1<" + strToDt + ")");
                                    updateSQLNULL.append(" OR (Q15Z3>" + strFromDt + " AND Q15Z3<=" + strToDt + ")");
                                    updateSQLNULL.append(" OR (Q15Z1<" + strFromDt + " AND Q15Z3>" + strToDt + ")))");

                                    aStr = combineDateTimeSQL("A.work_date", "A.work_time");
                                    
                                    updateSQLNULL.append(" AND NOT EXISTS (SELECT 1 FROM kq_originality_data A");
                                    updateSQLNULL.append(" WHERE A.SP_FLAG='03'");
                                    updateSQLNULL.append(" AND A.nbase='" + nbase + "'");
                                    updateSQLNULL.append(" AND " + Sql_switcher.isnull("A.iscommon", "'1'") + "='1'");
                                    updateSQLNULL.append(" AND A.NBASE=K.NBASE");
                                    updateSQLNULL.append(" AND A.A0100=K.A0100");
                                    //linbz 32193 跨天班次处理刷卡点时间范围错误
                                    updateSQLNULL.append(" and A.work_date>='" + cardFromDate + "'");
                                    updateSQLNULL.append(" and A.work_date<='" + cardToDate + "' ");
                                    
                                    if (existswhere != null && existswhere.length() > 0) {
                                        updateSQLNULL.append(" and " + existswhere);
                                    } else if (whereIN != null && whereIN.length() > 0) {
                                        if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                                            updateSQLNULL.append(" and  EXISTS(select a0100 " + whereIN + " and "
                                                    + nbase + "A01.a0100=A.a0100)");

                                        } else {
                                            updateSQLNULL.append(" and  EXISTS(select a0100 " + whereIN + " and "
                                                    + nbase + "A01.a0100=A.a0100)");
                                        }
                                    }

                                    if (codewhere != null && codewhere.length() > 0) {
                                        updateSQLNULL.append(" and " + codewhere);
                                    }
                                }
                                String oneSQL = "";
                                String twoSql = "";
                                if ("1".equals(onduty_card_1)) {
                                    on_start_time_1 = (String) map.get("on_start_time_1"); // 上班刷卡起
                                    on_end_time_1 = (String) map.get("on_end_time_1");
                                    // 签到跨夜，对比上班刷卡起是否大于迟到 如果大于就-1天
                                    if (on_start_time_1.compareTo(on_end_time_1) > 0) {
                                        oneSQL = combineDateTimeSQL("K.Q03Z0", on_start_time_1) + "-1";
                                        if (!"1".equals(offduty_card_1) && !"1".equals(offduty_card_2)) {
                                            String offduty_1 = (String) map.get("offduty_1"); // 下班刷卡止
                                            String offduty_2 = (String) map.get("offduty_2"); // 下班刷卡止
                                            // 47820 优先校验第二段下班时间，防止班次出现第二段上班时间需要刷卡的情况
                                            if (offduty_2 != null && offduty_2.length() > 0) {
                                                twoSql = combineDateTimeSQL("K.Q03Z0", (String) map.get("offduty_2"));
                                            } else if (offduty_1 != null && offduty_1.length() > 0) {
                                                twoSql = combineDateTimeSQL("K.Q03Z0", (String) map.get("offduty_1"));
                                            }
                                        } else {
                                            if ("1".equals(offduty_card_2)) {
                                                twoSql = combineDateTimeSQL("K.Q03Z0", (String) map.get("offduty_2"));
                                            } else if ("1".equals(offduty_card_1)) {
                                                twoSql = combineDateTimeSQL("K.Q03Z0", (String) map.get("offduty_1"));
                                            }
                                        }
                                    } else // +1
                                    {
                                        oneSQL = combineDateTimeSQL("K.Q03Z0", (String) map.get("on_start_time_1"));
                                        if (!"1".equals(offduty_card_1) && !"1".equals(offduty_card_2)) {
                                            String offduty_1 = (String) map.get("offduty_1"); // 下班刷卡止
                                            String offduty_2 = (String) map.get("offduty_2"); // 下班刷卡止
                                            // 47820 优先校验第二段下班时间，防止班次出现第二段上班时间需要刷卡的情况
                                            if ((offduty_2 != null && offduty_2.length() > 0)
                                                    || (offduty_2_2 != null && offduty_2_2.length() > 0)) {
                                                if (offduty_2 != null && offduty_2.length() > 0) {
                                                    String on = (String) map.get("on_start_time_1"); // 上班
                                                    if (on.compareTo(offduty_2) > 0) {
                                                        twoSql = combineDateTimeSQL("K.Q03Z0", offduty_2) + "+1";
                                                    } else {
                                                        twoSql = combineDateTimeSQL("K.Q03Z0", offduty_2);
                                                    }
                                                } else {
                                                    String on = (String) map.get("on_start_time_1"); // 上班
                                                    if (on.compareTo(offduty_2_2) > 0) {
                                                        twoSql = combineDateTimeSQL("K.Q03Z0", (String) map.get("offduty_2_2")) + "+1";
                                                    } else {
                                                        twoSql = combineDateTimeSQL("K.Q03Z0", (String) map.get("offduty_2_2"));
                                                    }
                                                }
                                            } else if ((offduty_1 != null && offduty_1.length() > 0)
                                                    || (offduty_1_1 != null && offduty_1_1.length() > 0)) {
                                                if (offduty_1 != null && offduty_1.length() > 0) {
                                                    String on = (String) map.get("on_start_time_1"); // 上班
                                                    if (on.compareTo(offduty_1) > 0) {
                                                        twoSql = combineDateTimeSQL("K.Q03Z0", offduty_1) + "+1";
                                                    } else {
                                                        twoSql = combineDateTimeSQL("K.Q03Z0", offduty_1);
                                                    }
                                                } else {
                                                    String on = (String) map.get("on_start_time_1"); // 上班
                                                    if (on.compareTo(offduty_1_1) > 0) {
                                                        twoSql = combineDateTimeSQL("K.Q03Z0", (String) map.get("offduty_1_1")) + "+1";
                                                    } else {
                                                        twoSql = combineDateTimeSQL("K.Q03Z0", (String) map.get("offduty_1_1"));
                                                    }
                                                }
                                            } else {
                                                twoSql = combineDateTimeSQL("K.Q03Z0", (String) map.get("offduty_1_1"));
                                            }
                                        } else {
                                            if ("1".equals(offduty_card_2)) {
                                                String offduty_2 = (String) map.get("offduty_2"); // 下班刷卡止
                                                String leave_early_2 = (String) map.get("leave_early_2"); // 早退

                                                // zxj 20191218 jazz56503 条件2判断跨天改为与上班时间点比较
                                                if (leave_early_2.compareTo(offduty_2) > 0
                                                        || onduty_1.compareTo(offduty_2) > 0) {
                                                    twoSql = combineDateTimeSQL("K.Q03Z0", offduty_2) + "+1";
                                                } else {
                                                    twoSql = combineDateTimeSQL("K.Q03Z0", offduty_2);
                                                }
                                            } else if ("1".equals(offduty_card_1)) {
                                                String offduty_1 = (String) map.get("offduty_1"); // 下班刷卡止
                                                String leave_early_1 = (String) map.get("leave_early_1"); // 早退
                                                if (leave_early_1.compareTo(offduty_1) > 0
                                                        || onduty_1.compareTo(offduty_1) > 0) {
                                                    twoSql = combineDateTimeSQL("K.Q03Z0", offduty_1) + "+1";
                                                } else {
                                                    twoSql = combineDateTimeSQL("K.Q03Z0", offduty_1);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (oneSQL != null && oneSQL.length() > 0) {
                                    updateSQLNULL.append(" AND " + aStr + ">=" + oneSQL + " ");
                                }
                                
                                if (twoSql != null && twoSql.length() > 0) {
                                    updateSQLNULL.append(" AND " + aStr + "<=" + twoSql + "");
                                }
                                
                                updateSQLNULL.append(")");
                                updateSQLNULL.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
                                updateSQLNULL.append(" and nbase='" + nbase + "'");
                                updateSQLNULL.append(" and isnormal<>1");
                                addWhrForAnalyseCenter(updateSQLNULL, nbase, codewhere, strDWhere, existswhere, whereIN);
                                this.dao.update(updateSQLNULL.toString());
                            }
                        }
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("考勤处理表过滤正常班次数据出错！");
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
    }
    
    private String combineDateTimeSQL(String date, String time) {
        String timeStr = time;
        if (timeStr.contains(":")) {
            timeStr = "'" + timeStr + ":00'";
        } else if (timeStr.toUpperCase().contains("WORK_TIME")) {
            timeStr = timeStr + Sql_switcher.concat() + "':00'";
        }
        
        if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
            return "CAST(" + date + "+' '+" + timeStr + " AS DATETIME)";
        } else {
            return "TO_DATE(" + date + "||' '||" + timeStr + ", 'YYYY.MM.DD hh24:mi:ss')";
        }
    }
    
    private String[] getOverlapTime(String beginTime1, String endTime1, String beginTime2, String endTime2) {
        String[] overlapTime = {"", ""};
        
        Date dBeginTime1 = DateUtils.getDate("2016.01.01 " + beginTime1, "yyyy.MM.dd HH:mm");
        Date dEndTime1 = DateUtils.getDate("2016.01.01 " + endTime1, "yyyy.MM.dd HH:mm");
        if (dBeginTime1.compareTo(dEndTime1)<0) {
            dEndTime1 = DateUtils.addDays(dEndTime1, 1);
        }
        
        Date dBeginTime2 = DateUtils.getDate("2016.01.01 " + beginTime2, "yyyy.MM.dd HH:mm");
        Date dEndTime2 = DateUtils.getDate("2016.01.01 " + endTime2, "yyyy.MM.dd HH:mm");
        if (dBeginTime2.compareTo(dEndTime2)<0) {
            dEndTime2 = DateUtils.addDays(dEndTime2, 1);
        }
        
        Date overlapBeginTime = null;
        Date overlapEndTime = null;
        if (dBeginTime1.compareTo(dBeginTime2)<=0 && dEndTime1.compareTo(dBeginTime2)>0) {
            overlapBeginTime = dBeginTime2;
            if (dBeginTime1.compareTo(dEndTime2)<=0 && dEndTime1.compareTo(dEndTime2)>0) {
                overlapEndTime = dEndTime2;
            } else {
                overlapEndTime = dEndTime1;
            }
        } else if (dBeginTime2.compareTo(dBeginTime2)<=0 && dEndTime2.compareTo(dBeginTime1)>0) {
            overlapBeginTime = dBeginTime1;
            if (dBeginTime2.compareTo(dEndTime1)<=0 && dEndTime2.compareTo(dEndTime1)>0) {
                overlapEndTime = dEndTime1;
            } else {
                overlapEndTime = dEndTime2;
            }
        } else if (dBeginTime1.equals(dBeginTime2) && dEndTime1.equals(dEndTime2)) {
            overlapBeginTime = dBeginTime1;
            overlapEndTime = dEndTime1;
        }
            
        if (overlapBeginTime != null && overlapEndTime != null) {
            overlapTime[0] = timeFormatter.format(overlapBeginTime);
            overlapTime[1] = timeFormatter.format(overlapEndTime);
        }
            
        return overlapTime;
    }
    

    /**
     * 为预先检查出来的处理结果添加刷卡数据
     * 
     * @Title: updateCardTimeForNormalData
     * @Description:
     */
    private void updateCardTimeForNormalData(String uptWhr) {

        ContentDAO dao = new ContentDAO(this.conn);
        StringBuilder sql = new StringBuilder();

        String tempTab = "t#kq_" + this.userView.getUserName();
        try {
            KqUtilsClass.dropTable(this.conn, tempTab);

            // 1、将每个人的刷卡数据按work_date分组放置到临时表中
            if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                sql.append("SET ARITHABORT ON; SELECT nbase,a0100,work_date,");
                sql.append("[work_time]=stuff(b.[work_time].value('/R[1]', 'nvarchar(max)'),1,1,'')");
                sql.append(" INTO ").append(tempTab);
                sql.append(" FROM (SELECT  nbase,a0100,work_date ");
                sql.append("       FROM kq_originality_data k");
                sql.append("       WHERE EXISTS(SELECT 1 FROM ").append(this.fAnalyseTempTab).append(" t");
                sql.append("                    WHERE t.a0100=k.a0100 and t.nbase=k.nbase and k.work_date=t.Q03Z0");
                sql.append("                    AND ").append(uptWhr);
                sql.append("                    AND t.ISNORMAL='1')");
                sql.append("             and " + Sql_switcher.isnull("K.sp_flag", "'03'") + "='03'");
                sql.append("             and " + Sql_switcher.isnull("K.iscommon", "'1'") + "='1'");
                sql.append("       GROUP BY nbase,a0100,work_date) a");
                sql.append(" CROSS apply (");
                sql.append("       SELECT [work_time] =(");
                sql.append("       SELECT N',' + [work_time] FROM kq_originality_data k");
                sql.append("       WHERE k.nbase = a.nbase and k.A0100=a.A0100 and k.work_date=a.work_date");
                sql.append("       AND EXISTS(SELECT 1 FROM ").append(this.fAnalyseTempTab).append(" t");
                sql.append("                    WHERE t.a0100=k.a0100 and t.nbase=k.nbase and k.work_date=t.Q03Z0");
                sql.append("                    AND ").append(uptWhr);
                sql.append("                    AND t.ISNORMAL='1')");
                sql.append("       and " + Sql_switcher.isnull("K.sp_flag", "'03'") + "='03'");
                sql.append("       and " + Sql_switcher.isnull("K.iscommon", "'1'") + "='1'");
                sql.append("       FOR XML PATH(''), ROOT('R'), TYPE");
                sql.append("      )");
                sql.append(" ) b;");
            } else {
                sql.append("create table ").append(tempTab).append(" as");
                sql.append(" SELECT t1.A0100,t1.nbase,t1.work_date,substr(MAX(sys_connect_by_path(t1.work_time,',')),2) as cardtimes");
                sql.append(" FROM");
                sql.append(" (SELECT a.A0100, a.nbase,a.work_date,a.work_time,");
                sql.append("  row_number() over(PARTITION BY a.A0100,a.nbase,a.work_date ORDER BY a.work_time) rn");
                sql.append(" FROM kq_originality_data a");
                sql.append(" WHERE EXISTS(SELECT 1 FROM ").append(this.fAnalyseTempTab).append(" b");
                sql.append(" WHERE b.a0100=a.a0100 and b.nbase=a.nbase and a.work_date=b.Q03Z0");
                sql.append(" AND ").append(uptWhr);
                sql.append(" AND B.ISNORMAL='1')");
                sql.append(" and " + Sql_switcher.isnull("a.sp_flag", "'03'") + "='03'");
                sql.append(" and " + Sql_switcher.isnull("a.iscommon", "'1'") + "='1'");
                sql.append(" ) t1");
                sql.append(" START WITH t1.rn=1");
                sql.append(" CONNECT BY t1.A0100=PRIOR t1.A0100 AND t1.nbase=PRIOR t1.nbase");
                sql.append(" AND t1.work_date=PRIOR t1.work_date AND t1.rn-1=PRIOR t1.rn");
                sql.append("  GROUP BY t1.A0100,t1.nbase,t1.work_date");
            }
            dao.update(sql.toString());

            // 给临时表加主键
            dao.update("alter table " + tempTab + " add constraint PK_" + tempTab
                    + " primary key(nbase,a0100,work_date)");

            // 2、从临时表更新刷卡数据到数据处理结果表
            sql.setLength(0);
            if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                sql.append("UPDATE ").append(this.fAnalyseTempTab);
                sql.append(" SET ").append(this.fAnalyseTempTab).append(".card_time=").append(tempTab)
                        .append(".work_time");
                sql.append(" FROM ").append(tempTab);
                sql.append(" WHERE ").append(this.fAnalyseTempTab).append(".nbase=").append(tempTab).append(".nbase");
                sql.append(" AND ").append(this.fAnalyseTempTab).append(".a0100=").append(tempTab).append(".a0100");
                sql.append(" AND ").append(this.fAnalyseTempTab).append(".q03z0=").append(tempTab).append(".work_date");
            } else {
                sql.append("UPDATE ").append(this.fAnalyseTempTab);
                sql.append(" SET(CARD_TIME)");
                sql.append("=( SELECT substr(cardtimes,0,500) FROM ").append(tempTab).append(" cd");
                sql.append(" WHERE cd.A0100=").append(this.fAnalyseTempTab).append(".A0100");
                sql.append(" AND cd.nbase=").append(this.fAnalyseTempTab).append(".nbase");
                sql.append(" AND cd.work_date=").append(this.fAnalyseTempTab).append(".Q03Z0");
                sql.append(" AND cd.cardtimes IS NOT NULL)");
                sql.append(" WHERE ISNORMAL='1'");
                sql.append(" AND ").append(uptWhr);
                sql.append(" AND EXISTS ( SELECT 1 FROM ").append(tempTab);
                sql.append("  WHERE cardtimes IS NOT NULL");
                sql.append(" AND ").append(tempTab).append(".A0100=").append(this.fAnalyseTempTab).append(".A0100");
                sql.append(" AND ").append(tempTab).append(".nbase=").append(this.fAnalyseTempTab).append(".nbase");
                sql.append(" AND ").append(tempTab).append(".work_date=").append(this.fAnalyseTempTab).append(".Q03Z0");
                sql.append(")");
            }
            dao.update(sql.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.dropTable(this.conn, tempTab);
        }
    }

    private void addWhrForAnalyseCenter(StringBuffer sql, String nbase, String codewhere, String strDWhere,
            String existswhere, String whereIN) {
        if (this.analyseType.equals(KqConstant.AnalyseType.MACHINE_CENTRAL) && existswhere != null
                && existswhere.length() > 0) {
            sql.append(" and " + existswhere);
        } else if (whereIN != null && whereIN.length() > 0) {
            if (!this.userView.isSuper_admin()) {
                sql.append(" and  EXISTS(select a0100 " + whereIN);
                if (whereIN.toUpperCase().contains("WHERE")) {
                    sql.append(" and ");
                } else {
                    sql.append(" where ");
                }
                sql.append(nbase + "A01.a0100=K.a0100)");
            }
        }

        if (this.analyseType.equals(KqConstant.AnalyseType.MACHINE_CENTRAL)) {
            if (codewhere != null && codewhere.length() > 0) {
                sql.append(" and " + codewhere);
            }

            if (strDWhere != null && strDWhere.length() > 0) {
                sql.append(" and " + strDWhere);
            }

            sql.append(" and " + this.pub_desT_where);
        }
    }

    // 得到基本班次中是否需要刷卡与刷卡时间 wangyao
    private HashMap getOnOffTime(String classid) {
        HashMap map = new HashMap();
        RowSet rowSet = null;
        try {
            NetSignIn netSignIn = new NetSignIn();
            StringBuffer sql = new StringBuffer();
            String columns = netSignIn.kqClassShiftColumns();
            sql.delete(0, sql.length());
            sql.append("select " + columns + " from kq_class where class_id=" + classid + "");
            StringBuffer buf = new StringBuffer();
            buf.append("");
            rowSet = this.dao.search(sql.toString());
            String on_start_time_1 = ""; // 1上班刷卡起
            String onduty_1 = ""; //
            String on_end_time_1 = ""; // 1迟到

            String leave_early_1 = ""; // 1早退
            String offduty_1 = ""; // 1下班
            String onduty_start_2 = ""; // 2上班
            String be_late_work_2 = ""; // 2迟到
            String leave_early_2 = ""; // 2早退
            String offduty_2 = ""; // 2下班
            String on_start_time_3 = ""; // 3上班
            String on_end_time_3 = ""; // 3迟到
            String leave_early_3 = ""; // 3早退
            String offduty_3 = ""; // 3下班
            String onduty_card_1 = ""; // 上班需刷卡1
            String offduty_card_1 = ""; // 下班需刷卡1
            String onduty_card_2 = ""; // 上班需刷卡2
            String offduty_card_2 = ""; // 下班需刷卡2
            String onduty_card_3 = ""; // 上班需刷卡3
            String offduty_card_3 = ""; // 下班需刷卡3
            String offduty_1_1 = ""; // 第一时段下班
            String offduty_2_2 = ""; // 第二时段下班
            String offduty_3_3 = ""; // 第二时段下班

            String onduty_flextime_1 = ""; // 第一时段上班弹性时点
            String offduty_flextime_1 = ""; // 第一时段下班弹性时点
            String offduty_flextime_2 = ""; // 第二时段下班弹性时点
            String offduty_flextime_3 = ""; // 第三时段下班弹性时点

            if (rowSet.next()) {
                onduty_card_1 = rowSet.getString("onduty_card_1");
                offduty_card_1 = rowSet.getString("offduty_card_1");
                onduty_card_2 = rowSet.getString("onduty_card_2");
                offduty_card_2 = rowSet.getString("offduty_card_2");
                onduty_card_3 = rowSet.getString("onduty_card_3");
                offduty_card_3 = rowSet.getString("offduty_card_3");

                if (rowSet.getString("onduty_start_1") != null && rowSet.getString("onduty_start_1").length() > 0) {
                    on_start_time_1 = rowSet.getString("onduty_start_1");
                }

                onduty_1 = rowSet.getString("onduty_1");

                if (rowSet.getString("be_late_for_1") != null && rowSet.getString("be_late_for_1").length() > 0) {
                    on_end_time_1 = rowSet.getString("be_late_for_1");
                }

                if (rowSet.getString("leave_early_1") != null && rowSet.getString("leave_early_1").length() > 0) {
                    leave_early_1 = rowSet.getString("leave_early_1");
                }

                if (rowSet.getString("offduty_end_1") != null && rowSet.getString("offduty_end_1").length() > 0) {
                    offduty_1 = rowSet.getString("offduty_end_1");
                }

                if (rowSet.getString("offduty_1") != null && rowSet.getString("offduty_1").length() > 0) {
                    offduty_1_1 = rowSet.getString("offduty_1");
                }

                if (rowSet.getString("onduty_start_2") != null && rowSet.getString("onduty_start_2").length() > 0) {
                    onduty_start_2 = rowSet.getString("onduty_start_2");
                }

                if (rowSet.getString("be_late_for_2") != null && rowSet.getString("be_late_for_2").length() > 0) {
                    be_late_work_2 = rowSet.getString("be_late_for_2");
                }

                if (rowSet.getString("leave_early_2") != null && rowSet.getString("leave_early_2").length() > 0) {
                    leave_early_2 = rowSet.getString("leave_early_2");
                }

                if (rowSet.getString("offduty_end_2") != null && rowSet.getString("offduty_end_2").length() > 0) {
                    offduty_2 = rowSet.getString("offduty_end_2");
                }

                if (rowSet.getString("offduty_2") != null && rowSet.getString("offduty_2").length() > 0) {
                    offduty_2_2 = rowSet.getString("offduty_2");
                }

                if (rowSet.getString("onduty_start_3") != null && rowSet.getString("onduty_start_3").length() > 0) {
                    on_start_time_3 = rowSet.getString("onduty_start_3");
                }

                if (rowSet.getString("be_late_for_3") != null && rowSet.getString("be_late_for_3").length() > 0) {
                    on_end_time_3 = rowSet.getString("be_late_for_3");
                }

                if (rowSet.getString("leave_early_3") != null && rowSet.getString("leave_early_3").length() > 0) {
                    leave_early_3 = rowSet.getString("leave_early_3");
                }

                if (rowSet.getString("offduty_end_3") != null && rowSet.getString("offduty_end_3").length() > 0) {
                    offduty_3 = rowSet.getString("offduty_end_3");
                }

                if (rowSet.getString("offduty_3") != null && rowSet.getString("offduty_3").length() > 0) {
                    offduty_3_3 = rowSet.getString("offduty_3");
                }

                if (rowSet.getString("onduty_flextime_1") != null && rowSet.getString("onduty_flextime_1").length() > 0) {
                    onduty_flextime_1 = rowSet.getString("onduty_flextime_1");
                }
                // 36795 处理第一段弹性下班时间赋值错误
                if (rowSet.getString("offduty_flextime_1") != null
                        && rowSet.getString("offduty_flextime_1").length() > 0) {
                    offduty_flextime_1 = rowSet.getString("offduty_flextime_1");
                }

                if (rowSet.getString("offduty_flextime_2") != null
                        && rowSet.getString("offduty_flextime_2").length() > 0) {
                    offduty_flextime_2 = rowSet.getString("offduty_flextime_2");
                }

                if (rowSet.getString("offduty_flextime_3") != null
                        && rowSet.getString("offduty_flextime_3").length() > 0) {
                    offduty_flextime_3 = rowSet.getString("offduty_flextime_3");
                }

                map.put("onduty_card_1", onduty_card_1);
                map.put("offduty_card_1", offduty_card_1);
                map.put("onduty_card_2", onduty_card_2);
                map.put("offduty_card_2", offduty_card_2);
                map.put("onduty_card_3", onduty_card_3);
                map.put("offduty_card_3", offduty_card_3);

                map.put("on_start_time_1", on_start_time_1);
                map.put("onduty_1", onduty_1);
                map.put("on_end_time_1", on_end_time_1);
                map.put("leave_early_1", leave_early_1);
                map.put("offduty_1", offduty_1);
                map.put("offduty_1_1", offduty_1_1);

                map.put("onduty_start_2", onduty_start_2);
                map.put("be_late_work_2", be_late_work_2);
                map.put("leave_early_2", leave_early_2);
                map.put("offduty_2", offduty_2);
                map.put("offduty_2_2", offduty_2_2);

                map.put("on_start_time_3", on_start_time_3);
                map.put("on_end_time_3", on_end_time_3);
                map.put("leave_early_3", leave_early_3);
                map.put("offduty_3", offduty_3);
                map.put("offduty_3_3", offduty_3_3);

                map.put("onduty_flextime_1", onduty_flextime_1);
                map.put("offduty_flextime_1", offduty_flextime_1);
                map.put("offduty_flextime_2", offduty_flextime_2);
                map.put("offduty_flextime_3", offduty_flextime_3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return map;
    }

    // 判断是否需要统计班次
    public String getClassName(String classname) throws GeneralException {
        String name = "";
        String fiel = "";
        RowSet rowSet = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select fielditemid from kq_item where item_name='" + classname + "'");
        try {
            rowSet = this.dao.search(sql.toString());
            if (rowSet.next()) {
                fiel = rowSet.getString("fielditemid");
            }
            if (fiel != null) {
                name = fiel;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return name;
    }

    /*
     * 旷工的时候同时更新 kq_item表中的 item_id=25 字段根据 Unit_DAY = '02'; // 天 Unit_HOUR =
     * '01'; // 小时 Unit_MINUTE = '03'; // 分钟 Unit_Time = '04'; // 次
     */
    private String getAbsenteeHours(String classid) throws GeneralException {
        RowSet rowSet = null;
        String hoursSql = "";
        StringBuffer sql = new StringBuffer();
        StringBuffer sql1 = new StringBuffer();
        String item_unit = ""; // 单位 按小时还是分钟
        String fielditemid = "";
        sql.append("select item_unit,fielditemid from kq_item where item_id='" + 25 + "'");

        try {
            rowSet = this.dao.search(sql.toString());
            if (rowSet.next()) {
                item_unit = rowSet.getString("item_unit");
                fielditemid = rowSet.getString("fielditemid");
            }
            KqUtilsClass.closeDBResource(rowSet);

            if (fielditemid == null || "".equals(fielditemid)) {
                return "";
            }

            sql.setLength(0);
            sql.append("SELECT work_hours FROM kq_class WHERE class_id=").append(classid);
            rowSet = this.dao.search(sql.toString());
            if (!rowSet.next()) {
                return "";
            }

            String work_hours = rowSet.getString("work_hours");

            float hours = Float.parseFloat(work_hours);
            if ("01".equals(item_unit)) // 小时
            {
                hoursSql = fielditemid + "=" + hours;
            } else if ("03".equals(item_unit)) // 分钟
            {
                hours = (int) (hours * 60.0);
                hoursSql = fielditemid + "=" + hours;
            } else if ("02".equals(item_unit) || "04".equals(item_unit)) {
                hoursSql = fielditemid + "=1";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return hoursSql;
    }

    // 最后判断 如果为休息班次并且所有的值为null；isok置为休息 wangyao
    public void setRest(String Tab, String codewhere, String start_date, String end_date, int i)
            throws GeneralException {

        if (i >= 0) {
            this.nbase_list = new ArrayList();
            this.nbase_list.add(this.db_list.get(i));
        }
        RowSet rowSet = null;
        StringBuffer sql = new StringBuffer();
        StringBuffer fiesql = new StringBuffer(); 
        StringBuffer upsql = new StringBuffer();
        // 只取考勤规则中对应的Q03中已构库的数值型指标kq 
        sql.append("select fielditemid from kq_item");
        sql.append(" where " + Sql_switcher.isnull("fielditemid", "'0'") + "<>'0'");
        sql.append(" and upper(fielditemid) in (select upper(itemid) from t_hr_busifield");
        sql.append(" where fieldsetid='Q03'");
        sql.append(" and useflag='1' and itemtype='N') ");
        try {
            rowSet = this.dao.search(sql.toString());
            while (rowSet.next()) {
                fiesql.append("AND " + Sql_switcher.sqlNull(rowSet.getString("fielditemid"), 0) + "=0 ");
            }
            
            upsql.append("UPDATE " + Tab);
            upsql.append(" SET isOK='休息'");
            upsql.append(" WHERE ISOK <> '休息' ");
            upsql.append(" AND " + Sql_switcher.sqlNull("class_id", 0) + "=0 ");
            upsql.append(fiesql);

            if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                upsql.append(" and " + codewhere);
            }

            upsql.append(" and q03z0>='" + start_date);
            upsql.append("' and q03z0<='" + end_date + "'");

            if ("101".equals(this.analyseType)) {
                upsql.append(" and " + this.pub_desT_where);
            }

            andFilterTermUpdate(upsql.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("考勤处理休息班次对应处理出错！"));
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
    }

    /**
     * 插入人员信息
     * 
     * @param date_table
     */
    private void dateleAndInsertAnalyeEmps(String analyse_Tmp, String date_Table, String code, String kind,
            String start_date, String end_date) throws GeneralException {
        if (null == this.nbase_list || this.nbase_list.size() <= 0) {
            return;
        }

        String codewhere = "";
        String nbase = "";
        String whereIN = "";
        String b0110 = "";

        // 删除集中表中本次需要处理的数据
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM kq_analyse_result");
        sql.append(" where q03z0>='").append(start_date).append("'");
        sql.append(" and q03z0<='").append(end_date).append("'");
        sql.append(" and exists(SELECT 1 FROM kq_analyse_emp A");
        sql.append(" where A.user_name='").append(this.userView.getUserName()).append("'");
        sql.append(" and A.nbase=kq_analyse_result.nbase and A.a0100=kq_analyse_result.a0100)");
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            dao.update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 向集中表插入本次需要处理的数据
        for (int i = 0; i < this.nbase_list.size(); i++) {
            nbase = (String) this.nbase_list.get(i);
            whereIN = (String) this.whereInMap_forNbase.get(nbase);
            // synchronizationInit101(analyse_Tmp,nbase,whereIN,start_date,end_date);
            if (this.whereCode_List != null && this.whereCode_List.size() > 0) {
                for (int r = 0; r < this.whereCode_List.size(); r++) {
                    codewhere = (String) this.whereCode_List.get(r);
                    b0110 = (String) this.b0110_list.get(r);
                    // 考虑到单位或部门下面可能有人员（注意不是岗位下面），不能使用like
                    if (codewhere.indexOf("b0110") != -1)// ||
                                                         // codewhere.indexOf("e0122")
                                                         // != -1)
                    {
                        codewhere = codewhere.replace("like", "=");
                        codewhere = codewhere.replace("%", "");
                    }
                    initializtion_date_Table(b0110, date_Table, start_date, end_date);
                    
                    DataAnalyseSync.insertEmpIntoTmp(b0110, analyse_Tmp, date_Table, nbase, codewhere, whereIN,
                            start_date, end_date, this.card_no_temp_field, this.g_no_temp_field, this.kq_dkind,
                            this.kq_sDate, this.kq_Gno, this.kq_card, this.kq_type, this.analyseType, dao,
                            this.userView, this.mainsql, this.conn);
                    if (this.initflag == null || !"1".equals(this.initflag)) {
                        this.dataAnalyseUtils.synchronizationInitTemp_Table(nbase, analyse_Tmp, codewhere, start_date,
                                end_date);
                    }
                }
            } else {

                initializtion_date_Table(b0110, date_Table, start_date, end_date);
                
                DataAnalyseSync.insertEmpIntoTmp(b0110, analyse_Tmp, date_Table, nbase, codewhere, whereIN, start_date,
                        end_date, this.card_no_temp_field, this.g_no_temp_field, this.kq_dkind, this.kq_sDate,
                        this.kq_Gno, this.kq_card, this.kq_type, this.analyseType, dao, this.userView, this.mainsql,
                        this.conn);
                if (this.initflag == null || !"1".equals(this.initflag)) {
                    this.dataAnalyseUtils.synchronizationInitTemp_Table(nbase, analyse_Tmp, codewhere, start_date,
                            end_date);
                }
            }
        }
    }

    /**
     * 插入人员信息
     * 
     * @param date_table
     */
    private void insertAnalyeEmps(String analyse_Tmp, String date_Table, String code, String kind, String start_date,
            String end_date) throws GeneralException {
        String codewhere = "";
        String nbase = "";
        String whereIN = "";
        String b0110 = "";
        if (this.nbase_list != null && this.nbase_list.size() > 0) {
            for (int i = 0; i < this.nbase_list.size(); i++) {
                nbase = (String) this.nbase_list.get(i);
                whereIN = (String) this.whereInMap_forNbase.get(nbase);
                if (this.whereCode_List != null && this.whereCode_List.size() > 0) {
                    for (int r = 0; r < this.whereCode_List.size(); r++) {
                        codewhere = (String) this.whereCode_List.get(r);
                        b0110 = (String) this.b0110_list.get(r);
                        initializtion_date_Table(b0110, date_Table, start_date, end_date);
                        insertEmpIntoTmp(b0110, analyse_Tmp, date_Table, nbase, codewhere, whereIN, start_date,
                                end_date);
                        if (this.initflag == null || !"1".equals(this.initflag)) {
                            this.dataAnalyseUtils.synchronizationInitTemp_Table(nbase, analyse_Tmp, codewhere,
                                    start_date, end_date);
                        }
                    }
                } else {
                    initializtion_date_Table(b0110, date_Table, start_date, end_date);
                    insertEmpIntoTmp(b0110, analyse_Tmp, date_Table, nbase, codewhere, whereIN, start_date, end_date);
                    if (this.initflag == null || !"1".equals(this.initflag)) {
                        this.dataAnalyseUtils.synchronizationInitTemp_Table(nbase, analyse_Tmp, codewhere, start_date,
                                end_date);
                    }
                }
            }
        }
    }

    /**
     * 初始化日期临时表数据表
     * 
     * @param b0110
     * @param date_Table
     * @param start_date
     * @param end_date
     * @throws GeneralException
     */
    private void initializtion_date_Table(String b0110, String date_Table, String start_date, String end_date)
            throws GeneralException {
        BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.conn);
        ArrayList date_list = baseClassShift.getDatelist(start_date, end_date);
        ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, this.conn);
        String rest_date = restList.get(0).toString();
        String rest_b0110 = restList.get(1).toString();
        baseClassShift.initializtion_date_Table(date_list, rest_date, date_Table, rest_b0110, b0110);
    }

    /**
     * 初始化人员临时表把人员库的信息更新到里面
     * 
     * @param analyse_Tmp
     * @param date_table
     * @param nbase
     * @param codewhere
     * @param whereIN
     *            插入主集中的信息，我在这插入调试
     */
    private void insertEmpIntoTmp(String b0110, String analyse_Tmp, String date_table, String nbase, String codewhere,
            String whereIN, String start_date, String end_date) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into " + analyse_Tmp + "(q03z0,nbase,a0100,b0110,e0122,e01a1,a0101,");
        if (!"".equals(this.mainsql) || this.mainsql.length() > 0) {
            sql.append("" + this.card_no_temp_field + "," + this.g_no_temp_field + "," + this.kq_dkind
                    + ",q03z3,flag,cur_user," + mainsql + ")");
            sql.append(" select " + date_table + "." + this.kq_sDate + " as q03z0,");
            sql.append("'" + nbase + "' as nbase,");
            sql.append("a0100,b0110,e0122,e01a1,a0101," + this.kq_card + " as " + this.card_no_temp_field + ",");
            sql.append(this.kq_Gno + " as " + this.g_no_temp_field + ",");
            sql.append(this.kq_dkind + "," + this.kq_type + ",'1','" + this.userView.getUserName() + "'," + mainsql);
        } else {
            sql.append("" + this.card_no_temp_field + "," + this.g_no_temp_field + "," + this.kq_dkind
                    + ",q03z3,flag,cur_user)");
            sql.append(" select " + date_table + "." + this.kq_sDate + " as q03z0,");
            sql.append("'" + nbase + "' as nbase,");
            sql.append("a0100,b0110,e0122,e01a1,a0101," + this.kq_card + " as " + this.card_no_temp_field + ",");
            sql.append(this.kq_Gno + " as " + this.g_no_temp_field + ",");
            sql.append(this.kq_dkind + "," + this.kq_type + ",'1','" + this.userView.getUserName() + "'");
        }

        sql.append(" from " + nbase + "A01 A, (select orgid,sdate,dkind from " + date_table);
        if (b0110 != null && !"".equals(b0110)) {
            sql.append(" where orgid='" + b0110 + "'");
        } else {
            sql.append(" WHERE orgid is null or orgid=''");
        }
        sql.append(" group by orgid,sdate,dkind) " + date_table);

        sql.append(" where " + this.kq_type + " is not null");
        if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
            sql.append(" and " + this.kq_type + "<>''");
        }

        sql.append(" and NOT EXISTS(SELECT 1 FROM " + analyse_Tmp + " t1");
        sql.append(" where " + date_table + "." + this.kq_sDate + "=t1.q03z0 and t1.nbase='" + nbase
                + "' and A.a0100=t1.a0100");
        sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");

        if ("101".equals(this.analyseType)) {
            sql.append(" and " + Sql_switcher.isnull("cur_user", "'##'") + "<>'##'");
        }
        sql.append(")");

        // 日明细统计调用时，只处理日明细中考勤方式为手工考勤的记录
        if ("1".equals(this.pick_flag)) {
            sql.append(" and EXISTS(SELECT 1 FROM Q03 Q WHERE Q.A0100=A.A0100 AND Q.nbase='" + nbase + "'");
            sql.append(" and Q.q03z0>='" + start_date + "' and Q.q03z0<='" + end_date + "'");
            sql.append(" and Q.q03z3='" + KqConstant.KqType.MANUAL + "'");
            sql.append(")");
        }

        if (codewhere != null && codewhere.length() > 0) {
            // zxj 20150416 个别处理 这里是从A01里取人员数据，不需要nbase条件
            if (codewhere.contains("a0100='") && codewhere.contains("nbase='")) {
                sql.append(" and " + codewhere.replaceAll("nbase='", "'" + nbase + "'='"));
            } else {
                sql.append(" and " + codewhere);
            }
        }

        if (this.analyseType != null && ("1".equals(this.analyseType) || "101".equals(this.analyseType))) {
            // sql.append(" and ("+this.kq_card+" is not null or
            // "+this.kq_card+"<>'') ");//and "+this.kq_type+"='02'
            sql.append(" and (" + Sql_switcher.isnull(this.kq_card, "'##'") + "<>'##')");
        }

        if (whereIN != null && whereIN.length() > 0) {
            if (!this.userView.isSuper_admin()) {
                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                    sql.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase + "A01.a0100=A.a0100)");
                } else {
                    sql.append(" and  EXISTS(select a0100 " + whereIN + " where " + nbase + "A01.a0100=A.a0100)");
                }
            }
        }

        // zxj 20150416 个别处理无法固定orgid，除非一个一个人处理
        if (this.b0110_list.size() > 0 && !"".equals(this.b0110_list.get(0))) {
            sql.append(" and " + date_table + ".orgid=A.b0110");
        }

        sql.append(" and " + date_table + ".sDate>='" + start_date + "' and " + date_table + ".sDate<='" + end_date
                + "'");
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            ArrayList list = new ArrayList();
            // System.out.println("初始化数据---"+this.userView.getUserName()+"---〉"+sql.toString());
            dao.insert(sql.toString(), list);
        } catch (Exception e) {
            System.out.println("初始化考勤处理增加人员信息数据出错！---" + this.userView.getUserName() + "---〉" + sql.toString());
            e.printStackTrace();
            // throw GeneralExceptionHandler.Handle(new
            // GeneralException("初始化考勤处理增加人员信息数据出错！"));
        }
    }

    /**
     * 判断Q03是否从主集中导入指标
     * 
     * @return
     */
    public String getmainsql() {
        String selectSQL = "";
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        sql.append("select itemid,itemtype,itemdesc from fielditem where fieldsetid='A01' and useflag='1'");
        RowSet rowSet = null;
        try {
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                ArrayList noblist = new ArrayList();
                String itemid = rowSet.getString("itemid");
                String itemtype = rowSet.getString("itemtype");
                String itemdesc = rowSet.getString("itemdesc");
                noblist.add(itemid);
                noblist.add(itemtype);
                noblist.add(itemdesc);
                list.add(noblist);
            }
            for (int i = 0; i < list.size(); i++) {
                ArrayList lists = (ArrayList) list.get(i);
                String nobitemid = (String) lists.get(0);
                String nobitemtype = (String) lists.get(1);
                String nobitemdesc = (String) lists.get(2);
                sql.setLength(0);
                sql.append("select itemid from t_hr_busifield where fieldsetid='Q03' and useflag='1' ");
                sql.append("and itemid='" + nobitemid + "' and itemtype='" + nobitemtype + "' and itemdesc='"
                        + nobitemdesc + "'");
                rowSet = dao.search(sql.toString());
                while (rowSet.next()) {
                    String itemi = rowSet.getString("itemid");
                    if (!"A0101".equals(itemi) && !"A0100".equals(itemi) && !"B0110".equals(itemi)
                            && !"E0122".equals(itemi) && !"E01A1".equals(itemi)) {
                        selectSQL += itemi + ",";
                    }
                }
            }
            if (selectSQL != null && selectSQL.length() > 0) {
                selectSQL = selectSQL.substring(0, selectSQL.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return selectSQL;
    }

    private String getB0110ForA0100(String nbase, String a0100) {
        String b0110 = "";
        RowSet rs = null;
        try {
            String sql = "select b0110 from " + nbase + "A01 where a0100='" + a0100 + "'";
            rs = this.dao.search(sql);
            if (rs.next()) {
                b0110 = rs.getString("b0110");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return b0110;
    }

    /**
     * 返回考勤方式where语句
     * 
     * @return
     */
    private String getKqTypeWhr(String kqType) {
        String where = "";
        switch (Sql_switcher.searchDbServer()) {
        case Constant.MSSQL: {
            if (analyseType != null && ("1".equals(analyseType) || "101".equals(analyseType))) {
                if ("1".equals(this.creat_register)) {
                    where = Sql_switcher.isnull(kqType, "'kq'") + "='" + DateAnalyseImp.KqType_Machine + "' or "
                            + Sql_switcher.isnull(kqType, "'kq'") + "='" + DateAnalyseImp.kqType_hand + "' or "
                            + Sql_switcher.isnull(kqType, "'kq'") + "='" + DateAnalyseImp.kqType_Nokq + "'";
                } else {
                    where = Sql_switcher.isnull(kqType, "'kq'") + "='" + DateAnalyseImp.KqType_Machine + "'";
                }
            } else if (analyseType != null && "0".equals(analyseType)) {
                where = Sql_switcher.isnull(kqType, "'" + DateAnalyseImp.KqType_Machine + "'") + "<>'"
                        + DateAnalyseImp.KqType_Machine + "' and "
                        + Sql_switcher.isnull(kqType, "'" + DateAnalyseImp.kqType_Leavekq + "'") + "<>'"
                        + DateAnalyseImp.kqType_Leavekq + "'";
            } else {
                // where=Sql_switcher.isnull(kqType,"'kq'")+"<>'kq' and
                // "+Sql_switcher.isnull(kqType,"'kq'")+"<>'"+kqType_Leavekq+"'";
                // where=kqType+" is not null and "+kqType+"<>'' and
                // "+kqType+"<>'"+kqType_Leavekq+"'";
                where = Sql_switcher.isnull(kqType, "'kq'") + "<>'kq' and " + kqType + "<>'"
                        + DateAnalyseImp.kqType_Leavekq + "'";
            }
            break;
        }
        case Constant.ORACEL: {
            if (analyseType != null && ("1".equals(analyseType) || "101".equals(analyseType))) {
                where = kqType + "='" + DateAnalyseImp.KqType_Machine + "' and " + kqType + "<>'"
                        + DateAnalyseImp.kqType_Leavekq + "'";
            } else if (analyseType != null && "0".equals(analyseType)) {
                where = Sql_switcher.isnull(kqType, "'" + DateAnalyseImp.KqType_Machine + "'") + "<>'"
                        + DateAnalyseImp.KqType_Machine + "' and "
                        + Sql_switcher.isnull(kqType, "'" + DateAnalyseImp.kqType_Leavekq + "'") + "<>'"
                        + DateAnalyseImp.kqType_Leavekq + "'";
            } else {
                // where=kqType+" is not null and
                // "+kqType+"<>'"+kqType_Leavekq+"'";
                where = Sql_switcher.isnull(kqType, "'kq'") + "<>'kq' and " + kqType + "<>'"
                        + DateAnalyseImp.kqType_Leavekq + "'";
            }
            break;
        }
        case Constant.DB2: {
            if (analyseType != null && ("1".equals(analyseType) || "101".equals(analyseType))) {
                where = kqType + "='" + DateAnalyseImp.KqType_Machine + "' and " + kqType + "<>'"
                        + DateAnalyseImp.kqType_Leavekq + "'";
            } else if (analyseType != null && "0".equals(analyseType)) {
                where = kqType + "<>'" + DateAnalyseImp.KqType_Machine + "' and " + kqType + "<>'"
                        + DateAnalyseImp.kqType_Leavekq + "'";
            } else {
                // where=kqType+" is null or "+kqType+"<>'"+kqType_Leavekq+"'";
                where = Sql_switcher.isnull(kqType, "'kq'") + "<>'kq' and " + kqType + "<>'"
                        + DateAnalyseImp.kqType_Leavekq + "'";
            }
            break;
        }
        }
        return "(" + where + ")";
    }

    /**
     * 从指定表插入（如：kq_emloy_change等保存有人员基本信息的表）
     * 
     * @param analyse_Tmp
     * @param change_Temp
     * @param date_Table
     * @param start_date
     * @param end_date
     * @throws GeneralException
     */
    private void insertEmpIntoEmp(String analyse_Tmp, String change_Temp, String date_Table, String start_date,
            String end_date) throws GeneralException {
        StringBuffer changeSQL = new StringBuffer();
        StringBuffer where =  new StringBuffer();
        where.append("flag=4 and status in (1,3,4)");
        where.append(" and ").append(RegisterInitInfoData.getKqEmpPrivWhr(this.conn, this.userView, change_Temp));
        
        changeSQL.append("select DISTINCT b0110 from " + change_Temp);
        changeSQL.append(" where " + where.toString());
        String b0110 = "";
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sql = null;
        ArrayList list = new ArrayList();
        RowSet rs = null;
        try {
            rs = dao.search(changeSQL.toString());
            while (rs.next()) {
                sql = new StringBuffer();
                b0110 = rs.getString("b0110");
                initializtion_date_Table(b0110, date_Table, start_date, end_date);
                sql.append("insert into " + analyse_Tmp + "(q03z0,nbase,a0100,b0110,e0122,e01a1,a0101," + this.kq_dkind
                        + ",flag,cur_user)");
                sql.append(" select " + date_Table + "." + this.kq_sDate + " as q03z0,");
                sql.append(" nbase,a0100,b0110,e0122,e01a1,a0101," + this.kq_dkind + ",'1','"
                        + this.userView.getUserName() + "'");
                sql.append(" from " + change_Temp + "," + date_Table);
                sql.append(" where ").append(where.toString());
                sql.append(" and " + date_Table + ".orgid = '" + b0110 + "'");
                sql.append(" and " + change_Temp + ".b0110 = '" + b0110 + "'");
                dao.insert(sql.toString(), list);
            }
            PubFunc.closeDbObj(rs);
            
            changeSQL = new StringBuffer();
            changeSQL.append("select DISTINCT nbase from " + change_Temp);
            changeSQL.append(" where " + where.toString());
            rs = dao.search(changeSQL.toString());
            while (rs.next()) {
                String destTab = analyse_Tmp;
                String srcTab = rs.getString("nbase") + "A01";// 源表
                String nbase = rs.getString("nbase");
                if (srcTab == null || srcTab.length() < 0) {
                    continue;
                }
                String strJoin = destTab + ".A0100=" + srcTab + ".A0100";// 关联串
                // xxx.field_name=yyyy.field_namex,....
                String strSet = destTab + ".q03z3=" + srcTab + "." + this.kq_type + "`" + destTab + "."
                        + this.card_no_temp_field + "=" + srcTab + "." + this.kq_card + "`" + destTab + "."
                        + this.g_no_temp_field + "=" + srcTab + "." + this.kq_Gno;// 更新串
                // xxx.field_name=yyyy.field_namex,....
                String strDWhere = " " + destTab + ".nbase='" + nbase + "'";// 更新目标的表过滤条件
                String strSWhere = "";// 源表的过滤条件
                String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,
                        strSWhere);
                // System.out.println("更新人员的考勤方式--->"+update);
                update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
                dao.update(update);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("初始化考勤处理表人员数据出错！"));
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
    }

    /**
     * 删除临时表中不要求处理的考勤类型
     * 
     * @param temp_table
     */
    private void delDifferKqType(String temp_table, String start_date, String end_date, String code, String kind)
            throws GeneralException {
        if (this.analyseType == null || analyseType.length() <= 0) {
            this.analyseType = "100";
        }
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        String codewhere = "";
        if ("1".equals(kind)) {
            codewhere = "e0122 like '" + code + "%'";
        } else if ("0".equals(kind)) {
            codewhere = "e01a1 like '" + code + "%'";
        } else if ("2".equals(kind)) {
            codewhere = "b0110 like '" + code + "%'";
        } else if ("-1".equals(kind)) {
            String t = code.substring(3, code.length());
            String t1 = code.substring(0, 3);
            codewhere = " a0100='" + t + "' and nbase='" + t1 + "'";
        } else if ("spec".equals(kind)) {
            codewhere = this.whereCode_List.get(0).toString();// "a0100 in " +
                                                              // code +
                                                              // " and nbase='"
                                                              // + t + "'";
        }
        String nbase = "";
        String whereIN = "";
        ContentDAO dao = new ContentDAO(this.conn);
        String temp_where = " and " + this.fAnalyseTempTab + ".q03z0>='" + start_date + "' and " + this.fAnalyseTempTab
                + ".q03z0<='" + end_date + "' ";
        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
            if ("-1".equals(kind)) {
                temp_where = temp_where + " and " + codewhere;
            } else if (!"spec".equals(kind)) {
                temp_where = temp_where + " and " + this.fAnalyseTempTab + "." + codewhere;
            } else {
                temp_where = temp_where
                        + " and "
                        + codewhere.replaceAll("nbase", this.fAnalyseTempTab + ".nbase").replaceAll("a0100",
                                this.fAnalyseTempTab + ".a0100");
            }

        }
        try {
            KqDBHelper kqDB = new KqDBHelper(this.conn);
            
            // 集中处理需要清掉数据处理表中对应日明细中已经是非机器考勤的数据,或者日明中没有的数据
            if ((this.analyseType.equals(KqConstant.AnalyseType.MACHINE_CENTRAL))) {
                sql.setLength(0);
                sql.append("DELETE FROM ").append(temp_table);
                sql.append(" WHERE ").append(RegisterInitInfoData.getKqEmpPrivWhr(this.conn, userView, temp_table));
                sql.append(" AND EXISTS(SELECT 1 FROM Q03 WHERE Q03.nbase=").append(temp_table).append(".nbase");
                sql.append(" AND Q03.a0100=").append(temp_table).append(".a0100");
                sql.append(" AND Q03.q03z0=").append(temp_table).append(".q03z0");
                sql.append(" AND NOT (" + getKqTypeWhr("Q03.q03z3") + ")");
                sql.append(")");
                dao.delete(sql.toString(), list);
                // 61706 删除日明细中没有的数据，避免数据处理表中垃圾数据无法清除
                sql.setLength(0);
                sql.append("DELETE FROM ").append(temp_table);
                sql.append(" WHERE ").append(RegisterInitInfoData.getKqEmpPrivWhr(this.conn, userView, temp_table));
                sql.append(" AND (cur_user='").append(userView.getUserName()).append("'");
                sql.append(" OR cur_user is null OR cur_user='')");
                sql.append(" AND NOT EXISTS(SELECT 1 FROM Q03 WHERE Q03.nbase=").append(temp_table).append(".nbase");
                sql.append(" AND Q03.a0100=").append(temp_table).append(".a0100");
                sql.append(" AND Q03.q03z0=").append(temp_table).append(".q03z0");
                sql.append(")");
                dao.delete(sql.toString(), list);
            }

            if (this.nbase_list != null && this.nbase_list.size() > 0) {
                whereIN = this.kqEmpWhrTmp.replace("{TAB}", temp_table);//(String) this.whereInMap_forNbase.get(nbase);
                // 删除考勤方式不对应的记录
                sql.setLength(0);
                sql.append("delete from " + temp_table);
                sql.append(" where NOT (" + getKqTypeWhr("q03z3") + ")");
                sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
                if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                    sql.append(" and " + codewhere);
                }
                sql.append(" and ").append(whereIN);
                dao.delete(sql.toString(), list);
                
                if ("100".equals(this.analyseType)) {
                    updateHandCard(temp_table, start_date, end_date, nbase, whereIN);
                }
                sql = new StringBuffer();
                sql.append("delete from " + temp_table + " WHERE  EXISTS (");
                sql.append("select 1 from q03 where ");
                sql.append("q03.q03z0>='" + start_date + "'");
                sql.append(" and q03.q03z0<='" + end_date + "'");
                // 数据处理的时候，不处理日明细中报批，报审，已批，发布的人的数据
                sql.append(" and q03.q03z5 in ('02','03','04','08')");
                sql.append(" and " + temp_table + ".a0100=q03.a0100");
                sql.append(" and " + temp_table + ".nbase=q03.nbase");
                sql.append(")");
                sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
                sql.append(" and ").append(whereIN);
                
                if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                    sql.append(" and " + codewhere);
                }
                
                if ("101".equals(this.analyseType)) {
                    sql.append(" and " + this.pub_desT_where);
                }
                
                dao.delete(sql.toString(), list);
                
                // 开始考勤日期
                String startField = KqParam.getInstance().getKqStartDateField();
                String startSet = kqDB.getTableNameByFieldName(startField);
                // 结束考勤日期
                String endField = KqParam.getInstance().getKqEndDateField();
                String endSet = kqDB.getTableNameByFieldName(endField);
                
                for(int i = 0; i< this.nbase_list.size(); i++) {
                    nbase = (String)this.nbase_list.get(i);
                    //删除考勤开始日期前（入职日期）数据
                    if(StringUtils.isNotBlank(startField) && StringUtils.isNotBlank(startSet)) {
                        String startTableName = nbase + startSet;
                        
                        sql.setLength(0);
                        sql.append("delete from ").append(temp_table);
                        sql.append(" where nbase='").append(nbase).append("'");
                        sql.append(" and  EXISTS (");
                        sql.append(" select 1 from ").append(startTableName).append(" A");
                        sql.append(" where A.a0100=").append(temp_table).append(".a0100");
                        sql.append(" and A." + startField + " is not null");
                        sql.append(" and ").append(Sql_switcher.dateToChar("A." + startField, "yyyy-mm-dd"));
                        sql.append(">replace(").append(temp_table+ ".Q03z0, '.', '-')");
                        if (!"a01".equals(startSet.toLowerCase())) {
                            sql.append(" and A.I9999=(select max(i9999) from " + startTableName + " C WHERE C.A0100=A.A0100)");
                        }
                        sql.append(")");
                        sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
                        sql.append(" and ").append(whereIN);
                        
                        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                            sql.append(" and " + codewhere);
                        }
                        
                        if ("101".equals(this.analyseType)) {
                            sql.append(" and " + this.pub_desT_where);
                        }
                        
                        dao.delete(sql.toString(), list);
                    }
                    
                    //删除考勤结束日期后（入职日期）数据
                    if(StringUtils.isNotBlank(endField) && StringUtils.isNotBlank(endSet)) {
                        String endTableName = nbase + endSet;
                        
                        sql.setLength(0);
                        sql.append("delete from ").append(temp_table);
                        sql.append(" where nbase='").append(nbase).append("'");
                        sql.append(" and  EXISTS (");
                        sql.append(" select 1 from ").append(endTableName).append(" A");
                        sql.append(" where A.a0100=").append(temp_table).append(".a0100");
                        sql.append(" and A." + endField + " is not null");
                        sql.append(" and ").append(Sql_switcher.dateToChar("A." + endField, "yyyy-mm-dd"));
                        sql.append("<=replace(").append(temp_table+ ".Q03z0, '.', '-')"); 
                        if (!"a01".equals(endSet.toLowerCase())) {
                            sql.append(" and A.I9999=(select max(i9999) from " + endTableName + " C WHERE C.A0100=A.A0100)");
                        }
                        sql.append(")");
                        sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
                        sql.append(" and ").append(whereIN);
                        
                        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                            sql.append(" and " + codewhere);
                        }
                        
                        if ("101".equals(this.analyseType)) {
                            sql.append(" and " + this.pub_desT_where);
                        }
                        
                        dao.delete(sql.toString(), list);
                    }
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("清除考勤处理表非处理数据出错！"));
        }

    }

    /**
     * 修改临时表手工考勤人员都是部要求刷卡
     * 
     * @param table_temp
     */
    private void updateHandCard(String table_temp, String start_date, String end_date, String nbase, String whereIN) {
        StringBuffer sql = new StringBuffer();
        sql.append("update " + table_temp + " set ");
        sql.append("onduty_card_1=?,offduty_card_1=?,onduty_card_2=?,offduty_card_2=?,");
        sql.append("onduty_card_3=?,offduty_card_3=?,onduty_card_4=?,offduty_card_4=? ");
        sql.append(" where " + getKqTypeWhr("q03z3"));
        sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
        sql.append(" and ").append(whereIN);

        ArrayList list = new ArrayList();
        list.add("0");
        list.add("0");
        list.add("0");
        list.add("0");
        list.add("0");
        list.add("0");
        list.add("0");
        list.add("0");
        try {
            this.dao.update(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从人员排班表中得到日班次
     * 
     * @param temp_table
     * @throws GeneralException
     */
    private void insertEmployeeShiftToTmp(String temp_table, String code, String kind, String start_date,
            String end_date) throws GeneralException {
        String codewhere = "";
        if ("1".equals(kind)) {
            codewhere = "e0122 like '" + code + "%'";
        } else if ("0".equals(kind)) {
            codewhere = "e01a1 like '" + code + "%'";
        } else if ("2".equals(kind)) {
            codewhere = "b0110 like '" + code + "%'";
        } else if ("-1".equals(kind)) {
            String t = code.substring(3, code.length());
            String t1 = code.substring(0, 3);
            codewhere = "a0100='" + t + "' and nbase='" + t1 + "'";
        } else if ("spec".equals(kind)) {
            codewhere = this.whereCode_List.get(0).toString();// "a0100 in " +
                                                              // code +
                                                              // "  and  nbase='"
                                                              // + t + "'";
        }
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            String nbase = "";
            String whereIN = "";
            if (this.nbase_list != null && this.nbase_list.size() > 0) {
                //for (int i = 0; i < this.nbase_list.size(); i++) {
                    //nbase = (String) this.nbase_list.get(i);
                    whereIN = this.kqEmpWhrTmp; //(String) this.whereInMap_forNbase.get(nbase);
                    String destTab = temp_table;// 目标表
                    String srcTab = "kq_employ_shift";// 源表
                    String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and  " + destTab + ".nbase=" + srcTab
                            + ".nbase and " + destTab + ".q03z0=" + srcTab + ".q03z0";// 关联串
                    // xxx.field_name=yyyy.field_namex,....
                    String strSet = destTab + ".class_id=" + srcTab + ".class_id";// 更新串
                    // xxx.field_name=yyyy.field_namex,....
                    String strDWhere = destTab + ".q03z0>='" + start_date + "' and " + destTab + ".q03z0<='" + end_date
                            + "'";// 更新目标的表过滤条件

                    String strSWhere = srcTab + ".q03z0>='" + start_date + "' and " + srcTab + ".q03z0<='" + end_date
                            + "'";// 源表的过滤条件
                    //strDWhere = strDWhere + " and " + destTab + ".nbase='" + nbase + "'";
                    //strSWhere = strDWhere + " and " + srcTab + ".nbase='" + nbase + "'";
                    strDWhere = strDWhere + " and " + whereIN.replace("{TAB}", destTab);
                    strSWhere = strSWhere + " and " + whereIN.replace("{TAB}", srcTab);
//                    if (whereIN != null && whereIN.length() > 0) {
//                        if (!userView.isSuper_admin()) {
//                            strDWhere = strDWhere + "  and exists (select a0100 " + whereIN + " and " + destTab
//                                    + ".a0100=" + nbase + "a01.a0100)";
//                            strSWhere = strSWhere + "  and exists (select a0100 " + whereIN + " and " + srcTab
//                                    + ".a0100=" + nbase + "a01.a0100)";// 源表的过滤条件
//                        } else {
//                            strDWhere = strDWhere + "  and exists (select a0100 " + whereIN + " where " + destTab
//                                    + ".a0100=" + nbase + "a01.a0100)";
//                            strSWhere = strSWhere + "  and exists (select a0100 " + whereIN + " where " + srcTab
//                                    + ".a0100=" + nbase + "a01.a0100)";// 源表的过滤条件
//                        }
//                    }

                    String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,
                            strSWhere);
                    update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");

                    if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                        if (!"spec".equalsIgnoreCase(kind)) {
                            update += " and " + codewhereAddTableSrc(destTab, codewhere);
                        } else {
                            update += " and "
                                    + codewhere.replaceAll("nbase", destTab + ".nbase").replaceAll("a0100",
                                            destTab + ".a0100");
                        }
                    }
                    update += " and " + destTab + ".q03z0>='" + start_date + "' and " + destTab + ".q03z0<='"
                            + end_date + "'";
                    // System.out.println("从人员排班表中得到日班次--->"+update);
                    if ("101".equals(this.analyseType)) {
                        update += " and " + this.pub_desT_where;
                    }
                    dao.update(update);
                //}
            } else {
                String destTab = temp_table;// 目标表
                String srcTab = "kq_employ_shift";// 源表
                String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and  " + destTab + ".nbase=" + srcTab
                        + ".nbase and " + destTab + ".q03z0=" + srcTab + ".q03z0";// 关联串
                // xxx.field_name=yyyy.field_namex,....
                String strSet = destTab + ".class_id=" + srcTab + ".class_id";// 更新串
                // xxx.field_name=yyyy.field_namex,....
                String strDWhere = "" + destTab + ".q03z0>='" + start_date + "' and " + destTab + ".q03z0<='"
                        + end_date + "'";// 更新目标的表过滤条件
                String strSWhere = "" + srcTab + ".q03z0>='" + start_date + "' and " + srcTab + ".q03z0<='" + end_date
                        + "'";// 源表的过滤条件
                String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,
                        strSWhere);
                update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
                // System.out.println("从人员排班表中得到日班次--->"+update);
                if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                    update += " and " + codewhereAddTableSrc(destTab, codewhere);
                }
                update += " and " + destTab + ".q03z0>='" + start_date + "' and " + destTab + ".q03z0<='" + end_date
                        + "'";
                if ("101".equals(this.analyseType)) {
                    update += " and " + this.pub_desT_where;
                }
                dao.update(update);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("添加考勤处理表班次基本数据出错！"));
        }

    }

    /**
     * 把班次信息更新到临时表里面
     * 
     * @param temp_table
     * @throws GeneralException
     */
    private void insertClassInfoToTmp(String temp_table, String code, String kind, String start_date, String end_date)
            throws GeneralException {
        String destTab = temp_table;// 目标表
        String srcTab = "kq_class";// 源表
        String strJoin = destTab + ".class_id=" + srcTab + ".class_id";
        StringBuffer strSet = new StringBuffer();
        strSet.append(destTab + ".onduty_card_1=" + srcTab + ".onduty_card_1`");
        strSet.append(destTab + ".offduty_card_1=" + srcTab + ".offduty_card_1`");
        strSet.append(destTab + ".onduty_start_1=" + srcTab + ".onduty_start_1`");
        strSet.append(destTab + ".onduty_1=" + srcTab + ".onduty_1`");

        strSet.append(destTab + ".onduty_flextime_1=" + srcTab + ".onduty_flextime_1`");// 弹性班
        strSet.append(destTab + ".be_late_for_1=" + srcTab + ".be_late_for_1`");
        strSet.append(destTab + ".absent_work_1=" + srcTab + ".absent_work_1`");
        strSet.append(destTab + ".onduty_end_1=" + srcTab + ".onduty_end_1`");
        strSet.append(destTab + ".offduty_start_1=" + srcTab + ".offduty_start_1`");
        strSet.append(destTab + ".leave_early_absent_1=" + srcTab + ".leave_early_absent_1`");
        strSet.append(destTab + ".leave_early_1=" + srcTab + ".leave_early_1`");
        strSet.append(destTab + ".offduty_1=" + srcTab + ".offduty_1`");
        strSet.append(destTab + ".offduty_flextime_1=" + srcTab + ".offduty_flextime_1`");// 弹性班
        strSet.append(destTab + ".offduty_end_1=" + srcTab + ".offduty_end_1`");

        strSet.append(destTab + ".onduty_card_2=" + srcTab + ".onduty_card_2`");
        strSet.append(destTab + ".offduty_card_2=" + srcTab + ".offduty_card_2`");
        strSet.append(destTab + ".onduty_start_2=" + srcTab + ".onduty_start_2`");
        strSet.append(destTab + ".onduty_2=" + srcTab + ".onduty_2`");
        strSet.append(destTab + ".onduty_flextime_2=" + srcTab + ".onduty_flextime_2`");// 弹性班
        strSet.append(destTab + ".be_late_for_2=" + srcTab + ".be_late_for_2`");
        strSet.append(destTab + ".absent_work_2=" + srcTab + ".absent_work_2`");
        strSet.append(destTab + ".onduty_end_2=" + srcTab + ".onduty_end_2`");
        strSet.append(destTab + ".offduty_start_2=" + srcTab + ".offduty_start_2`");
        strSet.append(destTab + ".leave_early_absent_2=" + srcTab + ".leave_early_absent_2`");
        strSet.append(destTab + ".leave_early_2=" + srcTab + ".leave_early_2`");
        strSet.append(destTab + ".offduty_2=" + srcTab + ".offduty_2`");
        strSet.append(destTab + ".offduty_flextime_2=" + srcTab + ".offduty_flextime_2`");// 弹性班
        strSet.append(destTab + ".offduty_end_2=" + srcTab + ".offduty_end_2`");

        strSet.append(destTab + ".onduty_card_3=" + srcTab + ".onduty_card_3`");
        strSet.append(destTab + ".offduty_card_3=" + srcTab + ".offduty_card_3`");
        strSet.append(destTab + ".onduty_start_3=" + srcTab + ".onduty_start_3`");
        strSet.append(destTab + ".onduty_3=" + srcTab + ".onduty_3`");
        strSet.append(destTab + ".onduty_flextime_3=" + srcTab + ".onduty_flextime_3`");// 弹性班
        strSet.append(destTab + ".be_late_for_3=" + srcTab + ".be_late_for_3`");
        strSet.append(destTab + ".absent_work_3=" + srcTab + ".absent_work_3`");
        strSet.append(destTab + ".onduty_end_3=" + srcTab + ".onduty_end_3`");
        strSet.append(destTab + ".offduty_start_3=" + srcTab + ".offduty_start_3`");
        strSet.append(destTab + ".leave_early_absent_3=" + srcTab + ".leave_early_absent_3`");
        strSet.append(destTab + ".leave_early_3=" + srcTab + ".leave_early_3`");
        strSet.append(destTab + ".offduty_3=" + srcTab + ".offduty_3`");
        strSet.append(destTab + ".offduty_flextime_3=" + srcTab + ".offduty_flextime_3`");// 弹性班
        strSet.append(destTab + ".offduty_end_3=" + srcTab + ".offduty_end_3`");

        strSet.append(destTab + ".onduty_card_4=" + srcTab + ".onduty_card_4`");
        strSet.append(destTab + ".offduty_card_4=" + srcTab + ".offduty_card_4`");
        strSet.append(destTab + ".onduty_start_4=" + srcTab + ".onduty_start_4`");
        strSet.append(destTab + ".onduty_4=" + srcTab + ".onduty_4`");
        strSet.append(destTab + ".onduty_flextime_4=" + srcTab + ".onduty_flextime_4`");// 弹性班
        strSet.append(destTab + ".be_late_for_4=" + srcTab + ".be_late_for_4`");
        strSet.append(destTab + ".absent_work_4=" + srcTab + ".absent_work_4`");
        strSet.append(destTab + ".onduty_end_4=" + srcTab + ".onduty_end_4`");
        strSet.append(destTab + ".offduty_start_4=" + srcTab + ".offduty_start_4`");
        strSet.append(destTab + ".leave_early_absent_4=" + srcTab + ".leave_early_absent_4`");
        strSet.append(destTab + ".leave_early_4=" + srcTab + ".leave_early_4`");
        strSet.append(destTab + ".offduty_4=" + srcTab + ".offduty_4`");
        strSet.append(destTab + ".offduty_flextime_4=" + srcTab + ".offduty_flextime_4`");// 弹性班
        strSet.append(destTab + ".offduty_end_4=" + srcTab + ".offduty_end_4`");
        // other
        strSet.append(destTab + ".night_shift_start=" + srcTab + ".night_shift_start`");
        strSet.append(destTab + ".night_shift_end=" + srcTab + ".night_shift_end`");

        strSet.append(destTab + ".work_hours=" + srcTab + ".work_hours/60.0`");
        strSet.append(destTab + ".zero_absent=" + srcTab + ".zero_absent`");
        // strSet.append(destTab+".zeroflag="+srcTab+".zeroflag`");
        // strSet.append(destTab+".domain_count="+srcTab+".domain_count`");
        // strSet.append(destTab+".one_absent="+srcTab+".one_absent`");
        strSet.append(destTab + ".overtime_from=" + srcTab + ".overtime_from`");
        strSet.append(destTab + ".overtime_type=" + srcTab + ".overtime_type`");
        strSet.append(destTab + ".check_tran_overtime=" + srcTab + ".check_tran_overtime`");
        // strSet.append(destTab+".name="+destTab+".name"); //之前的
        strSet.append(destTab + ".name=" + srcTab + ".name"); // wangyao
        String strDWhere = "q03z0>='" + start_date + "' and q03z0<='" + end_date + "'";// 更新目标的表过滤条件
        String strSWhere = "";// 源表的过滤条件

        /*
         * String
         * update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin
         * ,strSet.toString(),strDWhere,strSWhere);
         * update=KqUtilsClass.repairSqlTwoTable
         * (srcTab,strJoin,update,strDWhere,"");
         */
        // System.out.println("从人员排班表中得到日班次--->"+update);
        ContentDAO dao = new ContentDAO(this.conn);
        String codewhere = "";
        if ("1".equals(kind)) {
            codewhere = "e0122 like '" + code + "%'";
        } else if ("0".equals(kind)) {
            codewhere = "e01a1 like '" + code + "%'";
        } else if ("2".equals(kind)) {
            codewhere = "b0110 like '" + code + "%'";
        } else if ("-1".equals(kind)) {
            String t = code.substring(3, code.length());
            String t1 = code.substring(0, 3);
            codewhere = " a0100='" + t + "' and nbase='" + t1 + "'";
        } else if ("spec".equals(kind)) {
            codewhere = this.whereCode_List.get(0).toString();// " a0100 in " +
                                                              // code +
                                                              // " and nbase='"
                                                              // + t + "'";
        }
        try {

            String nbase = "";
            String whereIN = "";
            if (this.nbase_list != null && this.nbase_list.size() > 0) {
                //for (int i = 0; i < this.nbase_list.size(); i++) {
                    //nbase = (String) this.nbase_list.get(i);
                    whereIN = this.kqEmpWhrTmp; //(String) this.whereInMap_forNbase.get(nbase);
                    String strDwhere1 = strDWhere + " and " + this.kqEmpWhrTmp.replace("{TAB}", destTab);
                    //String strDwhere1 = strDWhere + " and nbase='" + nbase + "'";
//                    if (whereIN != null && whereIN.length() > 0) {
//                        if (!userView.isSuper_admin()) {
//                            strDwhere1 = strDwhere1 + "  and exists (select a0100 " + whereIN + " and " + destTab
//                                    + ".a0100=" + nbase + "a01.a0100)";
//
//                        } else {
//                            strDwhere1 = strDwhere1 + "  and exists (select a0100 " + whereIN + " where " + destTab
//                                    + ".a0100=" + nbase + "a01.a0100)";
//                        }
//                    }

                    String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet.toString(),
                            strDwhere1, strSWhere);
                    update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDwhere1, "");
                    // System.out.println("从人员排班表中得到日班次--->"+update);
                    if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                        update += " and " + codewhere;
                    }
                    update += " and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'";
                    if ("101".equals(this.analyseType)) {
                        update += " and " + this.pub_desT_where;
                    }
                    dao.update(update);
                    dao.update(setOnDuty(temp_table, codewhere, start_date, end_date, nbase, whereIN));
                    if (!"101".equals(this.analyseType) && !"1".equals(this.analyseType))// 这个是初始化的时候不分析刷卡纪录
                    {
                        dao.update(upMustCard(temp_table, codewhere, start_date, end_date, nbase, whereIN));
                    }
                //}
            } else {

                String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet.toString(),
                        strDWhere, strSWhere);
                update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
                // System.out.println("从人员排班表中得到日班次--->"+update);
                if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                    update += " and " + codewhere;
                }
                update += " and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'";
                if ("101".equals(this.analyseType)) {
                    update += " and " + this.pub_desT_where;
                }
                dao.update(update);
                dao.update(setOnDuty(temp_table, codewhere, start_date, end_date, "", ""));
                if (!"101".equals(this.analyseType) && !"1".equals(this.analyseType))// 这个是初始化的时候不分析刷卡纪录
                {
                    dao.update(upMustCard(temp_table, codewhere, start_date, end_date, "", ""));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("添加考勤处理表班次基本数据出错！"));
        }
        /*
         * try { if(this.analyseType.equals("101")) update+=" and "+codewhere;
         * dao.update(update);
         * dao.update(setOnDuty(temp_table,codewhere,start_date,end_date));
         * if(!this.analyseType.equals("101")&&!this.analyseType.equals("1"))//
         * 这个是初始化的时候不分析刷卡纪录 {
         * dao.update(upMustCard(temp_table,codewhere,start_date,end_date)); } }
         * catch (Exception e) { e.printStackTrace(); throw
         * GeneralExceptionHandler.Handle(e); }
         */
    }

    /**
     * 修改应出勤
     * 
     * @param temp_table
     * @return
     */
    private String setOnDuty(String temp_table, String codewhere, String start_date, String end_date, String nbase,
            String whereIN) {
        HashMap item_Map = (HashMap) this.kqItem_hash.get(DateAnalyseImp.kqItem_ONDUTY);
        String itemUnit = "";
        if (item_Map != null) {
            itemUnit = (String) item_Map.get("item_unit");
        }
        String ondutyTime = "work_hours";
        if (itemUnit == null || itemUnit.length() <= 0) {
            itemUnit = DateAnalyseImp.unit_HOUR;
        }
        if (itemUnit.equals(DateAnalyseImp.unit_HOUR)) {
            ondutyTime = ondutyTime + "";
        } else if (itemUnit.equals(DateAnalyseImp.unit_MINUTE)) {
            ondutyTime = ondutyTime + "*60";
        } else {
            ondutyTime = "1";
        }
        StringBuffer sql = new StringBuffer();
        sql.append("update " + temp_table + " set");
        sql.append(" q03z1 =" + ondutyTime + " where work_hours>0");
        sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");// 更新目标的表过滤条件")
        
        if (nbase != null && nbase.length() > 0) {
            sql.append(" and nbase='" + nbase + "'");
        }
        
        if (whereIN != null && whereIN.length() > 0) {
            if (!this.userView.isSuper_admin()) {
                sql.append(" and ").append(whereIN.replace("{TAB}", temp_table));
//                sql.append(" and exists (select a0100 " + whereIN + " and " + temp_table + ".a0100=" + nbase
//                        + "a01.a0100)");
            }
        }
        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
            sql.append(" and " + codewhere);
        }
        if ("101".equals(this.analyseType)) {
            sql.append(" and " + this.pub_desT_where);
        }
        // System.out.println(sql.toString());
        return sql.toString();
    }

    /**
     * 如果分析的不完全是机器考勤人员的数据，则把所有班次置为无需刷卡状态
     * 
     * @return
     */
    private String upMustCard(String temp_table, String codewhere, String start_date, String end_date, String nbase,
            String whereIN) {
        StringBuffer sql = new StringBuffer();
        sql.append("update " + temp_table + " set");
        sql.append(" onduty_card_1='0',offduty_card_1='0',onduty_card_2 ='0',offduty_card_2='0',");
        sql.append(" onduty_card_3='0',offduty_card_3='0',onduty_card_4 ='0',offduty_card_4='0'");
        sql.append(" where q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");// 更新目标的表过滤条件")
        if (nbase != null && nbase.length() > 0) {
            sql.append(" and nbase='" + nbase + "'");
        }
        if (whereIN != null && whereIN.length() > 0) {
            if (!this.userView.isSuper_admin()) {
                sql.append(" and ").append(whereIN.replace("{TAB}", temp_table));
//                sql.append(" and exists (select a0100 " + whereIN + " and " + temp_table + ".a0100=" + nbase
//                        + "a01.a0100)");
            }
        }
        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
            sql.append(" and " + codewhere);
        }
        if ("101".equals(this.analyseType)) {
            sql.append(" and " + this.pub_desT_where);
        }
        return sql.toString();
    }

    /**
     * 调整公休节假日考勤项目
     * 
     * @param analyse_Tmp
     */
    private void specialDisposal(String analyse_Tmp, String codewhere, String start_date, String end_date)
            throws GeneralException {
        if (codewhere != null && codewhere.length() > 0) {
            codewhere = "(" + codewhere + ")";
        }

        StringBuffer upSQL = null;

        // 处理没有可参考的以天记的项目数据
        StringBuffer strDayUnitDataSQL = new StringBuffer();
        strDayUnitDataSQL.append("UPDATE " + analyse_Tmp);
        strDayUnitDataSQL.append(" SET @@ = 1");
        strDayUnitDataSQL.append(" WHERE @@>=0.01");
        strDayUnitDataSQL.append(" AND " + Sql_switcher.isnull("work_hours", "0") + "<=0.1");
        strDayUnitDataSQL.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
        if ("101".equals(this.analyseType)) {
            strDayUnitDataSQL.append(" and " + this.pub_desT_where);
        }
        try {
            // 清除公休日考勤项目数据SQL模板
            StringBuffer sqlRest = null;

            // 考勤项目对公休日和节假日数据的要求
            String has_rest = "";
            String has_feast = "";
            String fielditemid = "";
            String getItemCategory = "";
            String itemUnit = "";
            for (Iterator it = this.kqItem_hash.entrySet().iterator(); it.hasNext();) {
                Map.Entry e = (Map.Entry) it.next();
                HashMap item_hs = (HashMap) e.getValue();
                has_rest = (String) item_hs.get("has_rest");
                has_feast = (String) item_hs.get("has_feast");
                getItemCategory = e.getKey().toString().substring(0, 1);
                itemUnit = (String) item_hs.get("item_unit");
                fielditemid = (String) item_hs.get("fielditemid");
                if (fielditemid == null || fielditemid.length() <= 0) {
                    continue;
                }

                // zxj changed 20140317 检查是否是数值型指标，非数值型指标不用校正
                FieldItem fieldItem = DataDictionary.getFieldItem(fielditemid, "Q03");
                if (null == fieldItem) {
                    continue;
                }

                if (!"N".equals(fieldItem.getItemtype())) {
                    continue;
                }

                if (DateAnalyseImp.unit_DAY.equals(itemUnit)) {
                    // 处理没有可参考的以天记的项目数据
                    if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                        strDayUnitDataSQL.append(" and " + codewhere);
                    }
                    String sql = strDayUnitDataSQL.toString().replaceAll("@@", fielditemid);
                    andFilterTermUpdate(sql.toString());
                    // dao.update(sql);
                }

                if ("1".equals(e.getKey().toString().substring(0, 1)))// 加班不适合该约束
                {
                    continue;
                }

                // 首钢修改，并且不等于公休日；
                // 清除公休日考勤项目数据SQL模板
                if (has_rest != null && "0".equals(has_rest))// 不包含公休日,
                {
                    fielditemid = (String) item_hs.get("fielditemid");
                    sqlRest = new StringBuffer();
                    if (fielditemid != null && fielditemid.length() > 0) {
                        sqlRest.append("update " + analyse_Tmp + " set");
                        sqlRest.append(" " + fielditemid + "=NULL ");
                        /** 增加开始* */
                        sqlRest.append(" where q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
                        sqlRest.append(" and " + this.kq_dkind + "!='" + DateAnalyseImp.dkHoliday + "'");
                        sqlRest.append(" and (class_id  IS  NULL or class_id=0)");
                        sqlRest.append(" and ").append(fielditemid).append(" is not null");
                        /** 结束* */
                        // sqlRest.append(" where (class_id IS NULL or
                        // class_id=0)");
                        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                            sqlRest.append(" and " + codewhere);
                        }
                        if ("101".equals(this.analyseType)) {
                            sqlRest.append(" and " + this.pub_desT_where);
                        }
                        // System.out.println(sqlRest.toString());
                        andFilterTermUpdate(sqlRest.toString());
                        // dao.update(sqlRest.toString());
                    }
                }
                // 首钢修改：节假日有排班，但是为旷工 把旷工数据统计出来； wangy
                // 不包含节假日
                // 清除节假日考勤项目数据SQL模板
                if (has_feast != null && "0".equals(has_feast)) {
                    fielditemid = (String) item_hs.get("fielditemid");
                    sqlRest = new StringBuffer();
                    if (fielditemid != null && fielditemid.length() > 0) {
                        sqlRest.append("update " + analyse_Tmp + " set");
                        sqlRest.append(" " + fielditemid + "=NULL ");
                        sqlRest.append(" where q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
                        sqlRest.append(" and " + this.kq_dkind + "='" + DateAnalyseImp.dkHoliday + "'");
                        /** 增加开始* */
                        sqlRest.append(" and (class_id  IS  NULL or class_id=0)");
                        sqlRest.append(" and ").append(fielditemid).append(" is not null");
                        /** 结束* */
                        // System.out.println(sqlRest.toString());
                        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                            sqlRest.append(" and " + codewhere);
                        }
                        if ("101".equals(this.analyseType)) {
                            sqlRest.append(" and " + this.pub_desT_where);
                        }
                        andFilterTermUpdate(sqlRest.toString());
                        // dao.update(sqlRest.toString());
                    }
                }
                // 请假、公出时长大于应出勤，则计为应出勤时长
                // 有排班时，请假、公出时长大于应出勤，则计为应出勤时长
                if ("0".equals(getItemCategory) || "3".equals(getItemCategory)) {
                    String strTranFormula = "";
                    if (DateAnalyseImp.unit_DAY.equals(itemUnit)) {
                        strTranFormula = "1";
                    } else if (DateAnalyseImp.unit_HOUR.equals(itemUnit)) {
                        strTranFormula = "work_hours";
                    } else if (DateAnalyseImp.unit_MINUTE.equals(itemUnit)) {
                        strTranFormula = "work_hours*60.0";
                    }
                    if (strTranFormula.length() > 0) {
                        upSQL = new StringBuffer();
                        upSQL.append("UPDATE " + analyse_Tmp);
                        upSQL.append(" set " + fielditemid + "=" + strTranFormula);
                        upSQL.append(" where q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
                        upSQL.append(" and " + fielditemid + ">" + strTranFormula);
                        // upSQL.append(" and class_id is not null and
                        // class_id>0 ");
                        upSQL.append(" and class_id>0 ");
                        // System.out.println(upSQL);
                        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                            upSQL.append(" and " + codewhere);
                        }
                        if ("101".equals(this.analyseType)) {
                            upSQL.append(" and " + this.pub_desT_where);
                        }
                        andFilterTermUpdate(upSQL.toString());
                        // dao.update(upSQL.toString());
                    }
                }

            }
            // 补充节假日加班数据
            supplementHolidayShiftOvertime(analyse_Tmp, codewhere, start_date, end_date);
            // 清除班次内实际工时小于《1小时的班次次数
            clearShiftTimesOfDutyZero(analyse_Tmp, start_date, end_date, codewhere);
            // 清除未排班日中的刷卡数据
            upSQL = new StringBuffer();
            upSQL.append("UPDATE " + analyse_Tmp + " SET card_time=NULL");
            upSQL.append(" where q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
            // upSQL.append(" and class_id IS NULL ");
            upSQL.append(" and " + Sql_switcher.isnull("class_id", "-1") + "=-1");
            upSQL.append(" and " + Sql_switcher.isnull("card_time", "'##'") + "<>'##'");// AND
                                                                                        // card_time
                                                                                        // IS
                                                                                        // NOT
                                                                                        // NULL
            if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                upSQL.append(" and " + codewhere);
            }
            if ("101".equals(this.analyseType)) {
                upSQL.append(" and " + this.pub_desT_where);
            }
            andFilterTermUpdate(upSQL.toString());
            // dao.update(upSQL.toString());
            // 将标记为正常的休息日，改为“休息”
            upSQL = new StringBuffer();
            upSQL.append("UPDATE " + analyse_Tmp + " SET isOK='休息'");
            upSQL.append(" where q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
            upSQL.append(" and class_id=0 AND isOK='正常'");

            if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                upSQL.append(" and " + codewhere);
            }

            if ("101".equals(this.analyseType)) {
                upSQL.append(" and " + this.pub_desT_where);
            }
            andFilterTermUpdate(upSQL.toString());
            // dao.update(upSQL.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("", "分析业务数据统计错误，请查看考勤项目中，统计指标是否对应！", "", ""));
        }
    }

    /**
     * 为排在节假日的班次人员，补充节假日加班数据 总节假日加班=已有节假日加班+（应出勤-请假-迟到-旷工
     */

    private boolean supplementHolidayShiftOvertime(String analyse_Tmp, String codewhere, String start_date,
            String end_date) throws GeneralException {
        boolean isCorrect = true;

        // 节假日没有排班算加班
        if (!(kqParam.getHolidayShiftIsOvertime() != null && "1".equals(kqParam.getHolidayShiftIsOvertime()))) {
            return isCorrect;
        }

        // 默认的节假日加班没有对应指标
        HashMap item_OFeast = (HashMap) this.kqItem_hash.get(DateAnalyseImp.kqItem_OFeast);
        if (item_OFeast == null) {
            return isCorrect;
        }

        String analyseType_1 = this.analyseType;
        if (this.creat_register != null && "1".equals(this.creat_register))// 生成日明细只处理手工人员
        {
            this.analyseType = "0";
        }
        String kqtypeWhr = getKqTypeWhr("q03z3");

        // 节假日加班指标
        String fieldOFeast = (String) item_OFeast.get("fielditemid");
        String formula = "";
        String itemUnit = "";

        StringBuffer strSQL = new StringBuffer();
        strSQL.append("UPDATE " + analyse_Tmp + " SET " + fieldOFeast + " = ");
        strSQL.append(Sql_switcher.isnull(fieldOFeast, "0") + "+");
        formula = getFactWorkHoursSQL();
        itemUnit = (String) item_OFeast.get("item_unit");
        if (DateAnalyseImp.unit_DAY.equals(itemUnit)) {
            strSQL.append(formula + "/work_hours");
        } else if (DateAnalyseImp.unit_HOUR.equals(itemUnit)) {
            strSQL.append(formula);
        } else if (DateAnalyseImp.unit_MINUTE.equals(itemUnit)) {
            strSQL.append(formula + "*60");
        } else {
            strSQL.append("1");
        }
        strSQL.append(" where q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
        strSQL.append(" and " + this.kq_dkind + "='" + DateAnalyseImp.dkHoliday + "'");
        strSQL.append(" and work_hours>0.01");
        strSQL.append(" and " + formula + ">0.01");
        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
            strSQL.append(" and " + codewhere);
        }
        if ("101".equals(this.analyseType)) {
            strSQL.append(" and " + this.pub_desT_where);
        }
        if (kqtypeWhr != null && kqtypeWhr.length() > 0) {
            strSQL.append(" and " + kqtypeWhr);// 生成日明细只处理手工人员
        }
        try {
            // System.out.println(strSQL.toString());
            andFilterTermUpdate(strSQL.toString());

            strSQL.setLength(0);
            strSQL.append("UPDATE " + analyse_Tmp + " SET ISOK='加班'");
            strSQL.append(" WHERE ISOK='正常'");
            strSQL.append(" AND q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
            strSQL.append(" and " + this.kq_dkind + "='" + DateAnalyseImp.dkHoliday + "'");
            strSQL.append(" AND work_hours>0.01 ");
            strSQL.append(" AND " + formula + ">0.0001");
            if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                strSQL.append(" and " + codewhere);
            }
            if ("101".equals(this.analyseType)) {
                strSQL.append(" and " + this.pub_desT_where);
            }
            if (kqtypeWhr != null && kqtypeWhr.length() > 0) {
                strSQL.append(" and " + kqtypeWhr);// 生成日明细只处理手工人员
            }
            andFilterTermUpdate(strSQL.toString());

            strSQL.setLength(0);
            strSQL.append("UPDATE " + analyse_Tmp + " SET ISOK=ISOK " + Sql_switcher.concat() + "'+加班'");
            strSQL.append(" WHERE ISOK<>'正常' AND (NOT ISOK LIKE '%加班%')");
            strSQL.append(" and " + this.kq_dkind + "='" + DateAnalyseImp.dkHoliday + "'");
            strSQL.append(" AND work_hours>0.01");
            strSQL.append(" AND " + formula + ">0.0001");
            strSQL.append(" AND q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
            if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                strSQL.append(" and " + codewhere);
            }
            if ("101".equals(this.analyseType)) {
                strSQL.append(" and " + this.pub_desT_where);
            }
            if (kqtypeWhr != null && kqtypeWhr.length() > 0) {
                strSQL.append(" and " + kqtypeWhr);// 生成日明细只处理手工人员
            }
            andFilterTermUpdate(strSQL.toString());

        } catch (GeneralException e) {

            isCorrect = false;
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } catch (SQLException e) {
            isCorrect = false;
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        this.analyseType = analyseType_1;
        return isCorrect;
    }

    /**
     * 分析表中涉及到的 并且 需要统计班次次数的 班次
     * 
     * @param analyse_Tmp
     * @param start_date
     * @param end_date
     * @param codewhere
     * @return
     * @throws GeneralException
     */
    private boolean clearShiftTimesOfDutyZero(String analyse_Tmp, String start_date, String end_date, String codewhere)
            throws GeneralException {
        boolean isCorrect = true;
        String formula = getFactWorkHoursSQL();
        if (formula.length() <= 0) {
            return true;
        }
        // StringBuffer strWhr=new StringBuffer();
        // strWhr.append("EXISTS(SELECT 1 FROM " + analyse_Tmp + " A WHERE
        // A.CLASS_ID=KQ_CLASS.CLASS_ID)");
        // strWhr.append(" AND NAME IN (SELECT ITEM_NAME FROM kq_item WHERE
        // "+Sql_switcher.isnull("fielditemid", "'##'")+"<>'##' ");
        // strWhr.append(" AND fielditemid IN (SELECT ITEMID FROM T_HR_BUSIFIELD
        // WHERE FIELDSETID='Q03' AND USEFLAG='1'))");
        // strWhr.append(" AND EXISTS (SELECT ITEMID FROM T_HR_BUSIFIELD WHERE
        // FIELDSETID='Q03' AND USEFLAG='1' and kq_item.fielditemid and
        // T_HR_BUSIFIELD.ITEMID))");
        StringBuffer sql = new StringBuffer();
        ArrayList shiftlist = getBaseShiftList(start_date, end_date, codewhere);
        for (int i = 0; i < shiftlist.size(); i++) {
            LazyDynaBean bean = (LazyDynaBean) shiftlist.get(i);
            String name = (String) bean.get("name");
            String id = (String) bean.get("class_id");
            HashMap item_map = this.dataAnalyseUtils.getKqItemByNameFromDB(name);
            String itemid = (String) item_map.get("fielditemid");
            if (item_map == null || itemid == null || itemid.length() <= 0) {
                continue;
            }
            sql.setLength(0);
            sql.append("UPDATE " + analyse_Tmp + " SET ");
            sql.append(" " + itemid + "=null where " + formula + "<1");
            sql.append(" and " + Sql_switcher.isnull(itemid, "'-1'") + "<>'-1'");
            sql.append(" and class_id='" + id + "'");
            sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
            if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                sql.append(" and " + codewhere);
            }
            // System.out.println(sql.toString());
            try {
                andFilterTermUpdate(sql.toString());
            } catch (SQLException e) {

                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(e);
            }
        }
        return isCorrect;
    }

    /**
     * 累计各请假和迟到、早退、旷工
     * 
     * @return
     */
    private String getFactWorkHoursSQL() {
        String formula = "";
        for (Iterator it = this.kqItem_hash.entrySet().iterator(); it.hasNext();) {
            Map.Entry e = (Map.Entry) it.next();
            HashMap item_hs = (HashMap) e.getValue();
            String getItemCategory = e.getKey().toString().substring(0, 1);
            if ((getItemCategory != null && getItemCategory.equals(DateAnalyseImp.kqItem_Leave))
                    || e.getKey().toString().equals(DateAnalyseImp.kqItem_WLate)
                    || e.getKey().toString().equals(DateAnalyseImp.kqItem_WEarly)
                    || e.getKey().toString().equals(DateAnalyseImp.kqItem_WAbsent)) {
                String itemValue = this.dataAnalyseUtils.tranUnitToHours(item_hs);
                if (itemValue != null && itemValue.length() > 0) {
                    formula = formula + "+" + itemValue;
                }
            }

        }
        if (formula.length() <= 0) {
            return "";
        }
        formula = formula.substring(1);
        // 应出勤-（请假+迟到+早退+旷工）
        formula = "(work_hours-(" + formula + "))";
        return formula;
    }

    /**
     * 处理旷工和夜班 1.先将各考勤项目的值统一转换成小时数,然后整体转换成旷工的单位 2.旷工 = 应出勤 - 请假s - 公出s
     * 3.计算条件：旷工=应出勤 4.只有在全天旷工情况下可用此算法，其它情况无法判断旷工的具体数值
     * 
     * @param analyse_Tmp
     */
    private void calcFactAbsent(String analyse_Tmp, String codewhere, String start_date, String end_date)
            throws GeneralException {
        HashMap item_Onduty = (HashMap) this.kqItem_hash.get(DateAnalyseImp.kqItem_ONDUTY);

        if (item_Onduty == null) {
            return;
        }

        String ondutyFld = (String) item_Onduty.get("fielditemid");
        HashMap item_WAbsent = (HashMap) this.kqItem_hash.get(DateAnalyseImp.kqItem_WAbsent);

        if (item_WAbsent == null) {
            return;
        }

        String absentFld = (String) item_WAbsent.get("fielditemid");
        HashMap item_hs = null;
        String itemUnit = "";
        String fielditemid = "";
        String formula = "";
        String lateAndEarlyFormula = "";
        String itemValue = "";
        StringBuffer sql = null;

        if (absentFld == null || absentFld.length() <= 0) {
            return;
        }

        for (Iterator it = this.kqItem_hash.entrySet().iterator(); it.hasNext();) {
            Map.Entry e = (Map.Entry) it.next();
            if (!("0".equals(e.getKey().toString().substring(0, 1)) || "3"
                    .equals(e.getKey().toString().substring(0, 1))))// 加班不适合该约束
            {
                if (e.getKey().toString().equals(DateAnalyseImp.kqItem_WLate)
                        || e.getKey().toString().equals(DateAnalyseImp.kqItem_WEarly)) {
                    // 迟到+早退
                    item_hs = (HashMap) e.getValue();
                    fielditemid = (String) item_hs.get("fielditemid");
                    if (fielditemid != null && fielditemid.length() > 0) {
                        itemValue = this.dataAnalyseUtils.tranUnitToHours(item_hs);
                        if (itemValue != null && itemValue.length() > 0) {
                            lateAndEarlyFormula = lateAndEarlyFormula + "-" + itemValue;
                        }
                    }
                }
            } else {
                item_hs = (HashMap) e.getValue();
                fielditemid = (String) item_hs.get("fielditemid");
                if (fielditemid != null && fielditemid.length() > 0) {
                    itemValue = this.dataAnalyseUtils.tranUnitToHours(item_hs);
                    if (itemValue != null && itemValue.length() > 0) {
                        formula = formula + "-" + itemValue;
                    }
                }
            }

        }

        RowSet rowSet = null;
        try {
            String tranFormula = "";
            String timesWhr = "";
            String allDayLeaveWhr = "";

            if (formula != null && formula.length() > 0) {
                String formulaWhr = formula.replaceAll("-", "+");
                formulaWhr = "(" + formulaWhr.substring(1) + ")";
                allDayLeaveWhr = "(" + this.dataAnalyseUtils.tranUnitToHours(item_Onduty) + "<=" + formulaWhr + ")";

                // 全天请假、公出，那么旷工为空
                sql = new StringBuffer();
                sql.append("update " + analyse_Tmp);
                sql.append(" set " + absentFld + "=null");
                sql.append(" where " + allDayLeaveWhr);
                sql.append(" and q03z0>='" + start_date);
                sql.append("' and q03z0<='" + end_date + "'");
                sql.append(" and ").append(absentFld).append(" is not null");
                if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                    sql.append(" and " + codewhere);
                }
                if ("101".equals(this.analyseType)) {
                    sql.append(" and " + this.pub_desT_where);
                }
                andFilterTermUpdate(sql.toString());
                // this.dao.update(sql.toString());
                /*
                 * if(uu==0) return;
                 */
                // 按进出匹配计算的离岗时长中应除去请假、公出
                HashMap item_Leavetime = (HashMap) this.kqItem_hash.get(DateAnalyseImp.KqItem_LEAVETIME);
                if (item_Leavetime != null) {
                    // 离岗时长中还应除去迟到、早退
                    formulaWhr = (formula + lateAndEarlyFormula).replaceAll("-", "+");
                    formulaWhr = "(" + formulaWhr.substring(1) + ")";

                    String leaveTimeFld = (String) item_Leavetime.get("fielditemid");

                    itemUnit = (String) item_Leavetime.get("item_unit");

                    if (itemUnit == null || itemUnit.length() <= 0) {
                        itemUnit = "";
                    }

                    if (itemUnit.equals(DateAnalyseImp.unit_MINUTE)) {
                        tranFormula = "(" + formulaWhr + "*60)";
                    } else if (itemUnit.equals(DateAnalyseImp.unit_DAY)) {
                        tranFormula = "(" + formulaWhr + "/work_hours)";
                    } else if (itemUnit.equals(DateAnalyseImp.unit_ONCE)) {
                        timesWhr = "(" + formulaWhr + ">0.001)";
                    } else {
                        tranFormula = formulaWhr;
                    }

                    if (this.kqParam.getCheck_inout_match() != null && "1".equals(this.kqParam.getCheck_inout_match())) {

                        sql = new StringBuffer();
                        sql.append("UPDATE " + analyse_Tmp);
                        sql.append(" SET " + leaveTimeFld + "=" + leaveTimeFld + "-" + tranFormula);
                        sql.append(" WHERE " + Sql_switcher.isnull(leaveTimeFld, "0") + ">0");
                        sql.append(" and q03z0>='" + start_date);
                        sql.append("' and q03z0<='" + end_date + "'");
                        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                            sql.append(" and " + codewhere);
                        }
                        if ("101".equals(this.analyseType)) {
                            sql.append(" and " + this.pub_desT_where);
                        }
                        andFilterTermUpdate(sql.toString());

                        // 从离岗中减去旷工时长
                        String adsernt_formula = this.dataAnalyseUtils.tranUnitToHours(item_WAbsent);

                        itemUnit = (String) item_Leavetime.get("item_unit");

                        if (itemUnit == null || itemUnit.length() <= 0) {
                            itemUnit = "";
                        }

                        if (itemUnit.equals(DateAnalyseImp.unit_MINUTE)) {
                            tranFormula = "(" + adsernt_formula + "*60)";
                        } else if (itemUnit.equals(DateAnalyseImp.unit_DAY)) {
                            tranFormula = "(" + adsernt_formula + "/work_hours)";
                        } else if (itemUnit.equals(DateAnalyseImp.unit_ONCE)) {
                            timesWhr = "(" + adsernt_formula + ">0.001)";
                        } else {
                            tranFormula = adsernt_formula;
                        }

                        sql.delete(0, sql.length());
                        sql.append("UPDATE " + analyse_Tmp);
                        sql.append(" SET " + leaveTimeFld + "=" + leaveTimeFld + "-" + tranFormula);
                        sql.append(" WHERE " + Sql_switcher.isnull(leaveTimeFld, "0") + ">0");
                        sql.append(" AND " + Sql_switcher.isnull(absentFld, "0") + ">0");
                        sql.append(" and q03z0>='" + start_date);
                        sql.append("' and q03z0<='" + end_date + "'");
                        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                            sql.append(" and " + codewhere);
                        }
                        if ("101".equals(this.analyseType)) {
                            sql.append(" and " + this.pub_desT_where);
                        }
                        andFilterTermUpdate(sql.toString());

                        // 将小于零的离岗置为NULL
                        sql.delete(0, sql.length());
                        sql.append("UPDATE " + analyse_Tmp);
                        sql.append(" SET " + leaveTimeFld + "=NULL");
                        sql.append("  WHERE " + Sql_switcher.isnull(leaveTimeFld, "0") + "<=0");
                        sql.append(" and ").append(leaveTimeFld).append(" is not null");
                        sql.append(" and q03z0>='" + start_date);
                        sql.append("' and q03z0<='" + end_date + "'");
                        if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                            sql.append(" and " + codewhere);
                        }
                        if ("101".equals(this.analyseType)) {
                            sql.append(" and " + this.pub_desT_where);
                        }
                        andFilterTermUpdate(sql.toString());
                    }

                    sql = new StringBuffer();
                    sql.append("UPDATE " + analyse_Tmp);
                    sql.append(" SET " + leaveTimeFld + "=NULL");
                    sql.append(" WHERE " + leaveTimeFld + "<=0");
                    sql.append(" and ").append(leaveTimeFld).append(" is not null");
                    sql.append(" and q03z0>='" + start_date);
                    sql.append("' and q03z0<='" + end_date + "'");
                    if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                        sql.append(" and " + codewhere);
                    }
                    if ("101".equals(this.analyseType)) {
                        sql.append(" and " + this.pub_desT_where);
                    }
                    andFilterTermUpdate(sql.toString());

                    sql = new StringBuffer();
                    sql.append("UPDATE " + analyse_Tmp);
                    sql.append(" SET ISOK=ISOK" + Sql_switcher.concat() + "'+离岗'");
                    sql.append(" WHERE " + Sql_switcher.isnull(leaveTimeFld, "0") + ">0");
                    sql.append(" and ISOK<>'正常'");
                    sql.append(" and q03z0>='" + start_date);
                    sql.append("' and q03z0<='" + end_date + "'");
                    if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                        sql.append(" and " + codewhere);
                    }
                    if ("101".equals(this.analyseType)) {
                        sql.append(" and " + this.pub_desT_where);
                    }
                    andFilterTermUpdate(sql.toString());

                    sql = new StringBuffer();
                    sql.append("UPDATE " + analyse_Tmp + " SET ISOK='离岗'  ");
                    sql.append(" WHERE " + Sql_switcher.isnull(leaveTimeFld, "0") + ">0");
                    sql.append(" and ISOK<>'离岗'");
                    sql.append(" and q03z0>='" + start_date);
                    sql.append("' and q03z0<='" + end_date + "'");
                    sql.append(" and isok='正常' ");
                    if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                        sql.append(" and " + codewhere);
                    }
                    if ("101".equals(this.analyseType)) {
                        sql.append(" and " + this.pub_desT_where);
                    }
                    andFilterTermUpdate(sql.toString());
                }
            }
            // 组成计算旷工的公式
            if ((ondutyFld != null && ondutyFld.length() > 0) && (formula != null && formula.length() > 0)) {
                if (this.kqParam.getCheck_inout_match() != null && "1".equals(this.kqParam.getCheck_inout_match())) {
                    formula = "(" + this.dataAnalyseUtils.tranUnitToHours(item_WAbsent) + formula + ")";
                } else {
                    formula = "(" + this.dataAnalyseUtils.tranUnitToHours(item_Onduty) + formula + ")";
                }
            } else {
                return;
            }

            itemUnit = (String) item_WAbsent.get("item_unit");
            if (itemUnit == null || itemUnit.length() <= 0) {
                itemUnit = "";
            }

            if (itemUnit.equals(DateAnalyseImp.unit_MINUTE)) {
                tranFormula = "(" + formula + "*60)";
            } else if (itemUnit.equals(DateAnalyseImp.unit_DAY)) {
                tranFormula = "(" + formula + "/work_hours)";
            } else if (itemUnit.equals(DateAnalyseImp.unit_ONCE)) {
                timesWhr = "(" + formula + ">0.001)";
            } else {
                tranFormula = formula;
            }

            // 计算旷工
            sql = new StringBuffer();
            sql.append("update " + analyse_Tmp);
            sql.append(" set " + absentFld + "=");
            if (timesWhr == null || timesWhr.length() <= 0) {
                sql.append(Sql_switcher.isnull(tranFormula, "0"));
            } else {
                sql.append("1");
            }
            sql.append(" where " + Sql_switcher.isnull("class_id", "0") + "<>0");
            sql.append(" and " + Sql_switcher.isnull(absentFld, "0") + ">0");
            // 不是按匹配进出刷卡的，只有在全天旷工情况下
            // if(this.kqParam.getCheck_inout_match()==null||this.kqParam.getCheck_inout_match().length()<=0||this.kqParam.getCheck_inout_match().equals("0"))
            sql.append(" and " + this.dataAnalyseUtils.tranUnitToHours(item_WAbsent) + "="
                    + this.dataAnalyseUtils.tranUnitToHours(item_Onduty));
            if (timesWhr != null && timesWhr.length() > 0) {
                sql.append(" and " + timesWhr);
            }

            if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                sql.append(" and " + codewhere);
            }

            sql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");
            if ("101".equals(this.analyseType)) {
                sql.append(" and " + this.pub_desT_where);
            }
            andFilterTermUpdate(sql.toString());

            // 将值小于零的旷工置为NULL
            sql = new StringBuffer();
            sql.append("UPDATE " + analyse_Tmp);
            sql.append(" SET " + absentFld + "=NULL");
            sql.append(" WHERE " + Sql_switcher.isnull(absentFld, "0") + "<=0");
            sql.append(" and ").append(absentFld).append(" is not null");
            sql.append(" and q03z0>='" + start_date);
            sql.append("' and q03z0<='" + end_date + "'");

            if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                sql.append(" and " + codewhere);
            }

            if ("101".equals(this.analyseType)) {
                sql.append(" and " + this.pub_desT_where);
            }

            andFilterTermUpdate(sql.toString());
            // this.dao.update(sql.toString());
            // 首钢更改，统计出来为旷工不在设置为正常 wangy
            // sql=new StringBuffer();
            // 将状态为旷工但旷工数值为0的状态重置
            // sql.append("UPDATE " + analyse_Tmp + " SET isok='正常' WHERE " +
            // Sql_switcher.isnull(absentFld,"0") + "<=0 AND isok='旷工'");
            // this.dao.update(sql.toString());
            sql = new StringBuffer();
            itemUnit = (String) item_WAbsent.get("item_unit");
            if (itemUnit == null || itemUnit.length() <= 0) {
                itemUnit = "";
            }
            // 将旷工大于应出勤的部分截去
            if (itemUnit.equals(DateAnalyseImp.unit_MINUTE) || itemUnit.equals(DateAnalyseImp.unit_HOUR)) {
                if (itemUnit.equals(DateAnalyseImp.unit_MINUTE)) {
                    tranFormula = "work_hours*60.0";
                } else if (itemUnit.equals(DateAnalyseImp.unit_HOUR)) {
                    tranFormula = "work_hours";
                }

                sql.append("UPDATE " + analyse_Tmp);
                sql.append(" SET " + absentFld + "=" + tranFormula);
                sql.append(" WHERE " + Sql_switcher.isnull(absentFld, "0") + ">" + tranFormula);
                sql.append(" and q03z0>='" + start_date);
                sql.append("' and q03z0<='" + end_date + "'");

                if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                    sql.append(" and " + codewhere);
                }

                if ("101".equals(this.analyseType)) {
                    sql.append(" and " + this.pub_desT_where);
                }

                andFilterTermUpdate(sql.toString());
            }
            // 2009.9.16 应天津代理要求，离岗时长已不再与旷工等价
            // 刷卡匹配
            if (this.kqParam.getCheck_inout_match() != null && "1".equals(this.kqParam.getCheck_inout_match())) {
                /*
                 * HashMap
                 * item_Leavetime=(HashMap)this.kqItem_hash.get(KqItem_LEAVETIME
                 * ); if(item_Leavetime!=null) { String
                 * leaveTimeFld=(String)item_Leavetime.get("fielditemid");
                 * formula =this.dataAnalyseUtils.tranUnitToHours(item_WAbsent);
                 * itemUnit=(String)item_Leavetime.get("item_unit");
                 * if(itemUnit.equals(unit_MINUTE)) {
                 * tranFormula="("+formula+"*60)"; }else
                 * if(itemUnit.equals(unit_DAY)) {
                 * tranFormula="("+formula+"/work_hours)"; }else
                 * if(itemUnit.equals(unit_ONCE)) {
                 * timesWhr="("+formula+">0.001)"; }else { tranFormula=formula;
                 * } sql=new StringBuffer(); sql.append("UPDATE " + analyse_Tmp+
                 * " SET " + leaveTimeFld + "=" + tranFormula);
                 * this.dao.update(sql.toString()); }
                 */
            }
            // 夜班
            HashMap nightHash = (HashMap) this.kqItem_hash.get(DateAnalyseImp.kqItem_Night);
            if (nightHash == null) {
                return;
            }

            String nightFld = (String) nightHash.get("fielditemid");

            sql = new StringBuffer();
            sql.append("update " + analyse_Tmp);
            sql.append(" set " + nightFld + "=null");
            sql.append(" where " + nightFld + ">0");
            sql.append(" and (" + Sql_switcher.isnull(absentFld, "0") + ">0");
            sql.append(" and (");
            // 全天旷工不计夜班
            sql.append(this.dataAnalyseUtils.tranUnitToHours(item_WAbsent));
            sql.append("=");
            sql.append(this.dataAnalyseUtils.tranUnitToHours(item_Onduty) + ")");
            // 全体请假或公出不计夜班
            if (!"".equals(allDayLeaveWhr)) {
                sql.append(" or (" + allDayLeaveWhr + ")");
            }
            sql.append(") and q03z0>='" + start_date);
            sql.append("' and q03z0<='" + end_date + "'");

            if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                sql.append(" and " + codewhere);
            }

            if ("101".equals(this.analyseType)) {
                sql.append(" and " + this.pub_desT_where);
            }

            andFilterTermUpdate(sql.toString());

            /** 开始：计算班次次数;工时-请假-迟到-早退-旷工<=0 班次次数=0* */
            HashMap item_classOnce = null;
            String fielditemidOnce = "";
            String itemValueOnce = "";
            String classOnce = Sql_switcher.isnull("work_hours", "0"); // 组的SQL
            for (Iterator it = this.kqItem_hash.entrySet().iterator(); it.hasNext();) {
                Map.Entry e = (Map.Entry) it.next();
                /** 得到请假、迟到、早退、旷工* */
                if (("0".equals(e.getKey().toString().substring(0, 1)))
                        || e.getKey().toString().equals(DateAnalyseImp.kqItem_WLate)
                        || e.getKey().toString().equals(DateAnalyseImp.kqItem_WEarly)
                        || e.getKey().toString().equals(DateAnalyseImp.kqItem_WAbsent)) {
                    item_classOnce = (HashMap) e.getValue();
                    fielditemidOnce = (String) item_classOnce.get("fielditemid");
                    if (fielditemidOnce != null && fielditemidOnce.length() > 0) {
                        itemValueOnce = this.dataAnalyseUtils.tranUnitToHours(item_classOnce);
                        if (itemValueOnce != null && itemValueOnce.length() > 0) {
                            classOnce = classOnce + "-" + itemValueOnce;
                        }
                    }
                }
            }
            // 分析表中涉及到的 并且 需要统计班次次数的 班次
            StringBuffer strWhr = new StringBuffer();
            strWhr.append("select kq_class.class_id as class_id,kq_item.fielditemid as fielditemid");
            strWhr.append(" from kq_class,kq_item");
            strWhr.append(" where EXISTS(SELECT 1 FROM " + analyse_Tmp);
            strWhr.append(" A where A.Class_Id=kq_class.class_id");
            strWhr.append(" and q03z0>='" + start_date);
            strWhr.append("' and q03z0<='" + end_date + "'");
            if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                strWhr.append(" and " + codewhere);
            }
            strWhr.append(") and NAME IN (SELECT ITEM_NAME FROM kq_item");
            strWhr.append(" WHERE " + Sql_switcher.isnull("fielditemid", "'##'") + "<>'##'");
            strWhr.append(" AND EXISTS");
            strWhr.append(" (SELECT ITEMID FROM T_HR_BUSIFIELD");
            strWhr.append(" WHERE FIELDSETID='Q03'");
            strWhr.append(" AND USEFLAG='1'");
            strWhr.append(" and kq_item.fielditemid=T_HR_BUSIFIELD.ITEMID))");
            strWhr.append(" and kq_class.name=kq_item.item_name");

            rowSet = this.dao.search(strWhr.toString());
            while (rowSet.next()) {
                StringBuffer strSQL = new StringBuffer();
                strSQL.append("UPDATE " + analyse_Tmp);
                strSQL.append(" SET " + rowSet.getString("fielditemid") + "=NULL");
                strSQL.append(" WHERE " + classOnce + "<=0");
                strSQL.append(" and ").append(rowSet.getString("fielditemid")).append(" is not null");
                strSQL.append(" AND " + Sql_switcher.isnull(rowSet.getString("fielditemid"), "'-1'") + "<>'-1'");
                strSQL.append(" AND class_id=" + rowSet.getString("class_id") + "");
                strSQL.append(" AND q03z0>='" + start_date);
                strSQL.append("' and q03z0<='" + end_date + "'");
                if ("101".equals(this.analyseType) && codewhere != null && codewhere.length() > 0) {
                    strSQL.append(" AND " + codewhere);
                }
                if ("101".equals(this.analyseType)) {
                    sql.append(" and " + this.pub_desT_where);
                }
                andFilterTermUpdate(strSQL.toString());
            }
            /** 结束* */
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("统计处理旷工、夜班结果出现错误，请查看考勤项目中，统计指标是否对应以及应出勤、旷工（天）等指标计量单位是否正确！");
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }

    }

    /**
     * 更新全部数据
     * 
     * @param table_temp
     */
    public void updateDataToQ03(String table_temp, HashMap kqItem_Map, String start_date, String end_date, String code,
            String kind) throws GeneralException {

        String nbase = "";
        String whereIN = "";
        if (this.nbase_list != null && this.nbase_list.size() > 0) {
            //for (int i = 0; i < this.nbase_list.size(); i++) {
            //    nbase = (String) this.nbase_list.get(i);
            //    whereIN = this.//(String) this.whereInMap_forNbase.get(nbase);
                updateDataToQ03(table_temp, kqItem_Map, start_date, end_date, nbase, whereIN, code, kind);
            //}
        }
    }

    /**
     * 更新全部数据
     * 
     * @param table_temp
     */
    public void updateDataToQ03(String table_temp, HashMap kqItem_Map, String start_date, String end_date,
            String nbase, String whereIN, String code, String kind) throws GeneralException {

        DbWizard db = new DbWizard(this.conn);

        HashMap item_hs = null;
        start_date = start_date.replaceAll("-", "\\.");
        end_date = end_date.replaceAll("-", "\\.");
        String destTab = "q03";// 目标表
        String srcTab = table_temp;// 源表
        String destCodewhere = "";
        String srcCodewhere = "";
        if ("1".equals(kind)) {
            srcCodewhere = table_temp + ".e0122 like '" + code + "%'";
            destCodewhere = destTab + ".e0122 like '" + code + "%'";
        } else if ("0".equals(kind)) {
            srcCodewhere = table_temp + ". e01a1 like '" + code + "%'";
            destCodewhere = destTab + ".e01a1 like '" + code + "%'";
        } else if ("2".equals(kind)) {
            srcCodewhere = table_temp + ".b0110 like '" + code + "%'";
            destCodewhere = destTab + ".b0110 like '" + code + "%'";
        } else if ("-1".equals(kind)) {
            String t = code.substring(3, code.length());
            String t1 = code.substring(0, 3);
            srcCodewhere = table_temp + ".a0100='" + t + "' and " + table_temp + ".nbase='" + t1 + "'";
            destCodewhere = destTab + ".a0100='" + t + "' and " + destTab + ".nbase='" + t1 + "'";
        } else if ("spec".equals(kind)) {
            String t = this.nbase_list.get(0).toString();
            srcCodewhere = table_temp + ".a0100 in " + code + " and " + table_temp + ".nbase='" + t + "'";
            destCodewhere = destTab + ".a0100 in " + code + " and " + destTab + ".nbase='" + t + "'";
        }
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase= " + srcTab + ".nbase and "
                + destTab + ".q03z0=" + srcTab + ".q03z0";// 关联串
                                                          // xxx.field_name=yyyy.field_namex,....
        StringBuffer strSet = new StringBuffer();
        // strSet.append(destTab+".q03z3="+srcTab+".q03z3");//更新串
        // xxx.field_name=yyyy.field_namex,....
        StringBuffer srcFlds = new StringBuffer();
        String srcFld = "";
        for (Iterator it = kqItem_Map.entrySet().iterator(); it.hasNext();) {
            Map.Entry e = (Map.Entry) it.next();
            item_hs = (HashMap) e.getValue();
            srcFld = (String) item_hs.get("fielditemid");
            FieldItem fielditem = DataDictionary.getFieldItem(srcFld, "Q03");

            if (fielditem == null || "0".equals(fielditem.getUseflag())
                    || !db.isExistField(destTab, fielditem.getItemid(), false)
                    || !db.isExistField(srcTab, fielditem.getItemid(), false)) {
                continue;
            }
//zxj 注释原因 不应控制类型，因为有的项目是从子集导入的，什么类型的都有
//            if (fielditem != null && !fielditem.getCodesetid().equals("0")
//                    && !fielditem.getItemtype().equalsIgnoreCase("N"))
//                continue;

            if (srcFld != null && srcFld.length() > 0) {
                //刷卡时间、处理结果、班次名称后续特殊判断，这里跳过，避免考勤规则中定义了这三项导致sql拼接重复
                if ("ctime".equalsIgnoreCase(srcFld) || "isok".equalsIgnoreCase(srcFld) || "cname".equalsIgnoreCase(srcCodewhere)) {
                    continue;
                }
                
                if (!strSet.toString().contains(destTab + "." + srcFld + "=")) {
                    strSet.append("" + destTab + "." + srcFld + "=" + srcTab + "." + srcFld + "`");
                }

                if (!this.mainsql.toLowerCase().contains(srcFld) && !srcFlds.toString().toUpperCase().contains("," + srcFld)) {
                    srcFlds.append("," + srcFld);
                }
            }
        }

        if (db.isExistField(destTab, "ctime", false) && "Q03".equalsIgnoreCase(destTab)) {// 刷卡时间
            strSet.append("" + destTab + ".ctime=" + srcTab + ".card_time`");
            srcFlds.append(",ctime");
        }
        if (db.isExistField(destTab, "isOk", false) && "Q03".equalsIgnoreCase(destTab)) {// 处理结果
            strSet.append("" + destTab + ".isOk=" + srcTab + ".isOk`");
            srcFlds.append(",isOk");
        }
        if (db.isExistField(destTab, "cname", false) && "Q03".equalsIgnoreCase(destTab)) {// 班次名称
            strSet.append("" + destTab + ".cname=" + srcTab + ".name`");
            srcFlds.append(",cname");
        }
        
        String exceptItems = ",A0101,A0100,NBASE,Q03Z0,B0110,E0122,E01A1,I9999,MODTIME,MODUSERNAME,Q03Z3,Q03Z5,STATE,";
        ArrayList fieldItems = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        for (int i=0; i<fieldItems.size(); i++) {
            String item = ((FieldItem)fieldItems.get(i)).getItemid();
            
            if (exceptItems.contains("," + item.toUpperCase() + ",")) {
                continue;
            }
            
            if (srcFlds.toString().toUpperCase().contains("," + item.toUpperCase())) {
                continue;
            }
            
            strSet.append("" + destTab + "." + item + "=" + srcTab + "." + item + "`");
            
            if (!("," + this.mainsql.toLowerCase() + ",").contains("," + item + ",")) {
                srcFlds.append("," + item);
            }
        }

        if (db.isExistField(destTab, "modtime", false)// 广东中烟 清空操作时间和操作用户
                && "Q03".equalsIgnoreCase(destTab)) {
            strSet.append("" + destTab + ".modtime=null`");
        }
        if (db.isExistField(destTab, "modusername", false) && "Q03".equalsIgnoreCase(destTab)) {
            strSet.append("" + destTab + ".modusername=null`");
        }
        if (strSet.length() > 0) {
            strSet.setLength(strSet.length() - 1);
        }

        String strSWhere = table_temp + ".flag='1'";// 源表的过滤条件
        strSWhere = strSWhere + " and " + srcTab + ".q03z0>='" + start_date + "' and " + srcTab + ".q03z0<='"
                + end_date + "'";
        
        strSWhere = strSWhere + " and " + this.kqEmpWhrTmp.replace("{TAB}", table_temp);

        if ("101".equals(this.analyseType) && srcCodewhere != null && srcCodewhere.length() > 0) {
            strSWhere = strSWhere + " and " + srcCodewhere;
        }
        
        if ("101".equals(this.analyseType)) {
            strSWhere = strSWhere + " and " + this.pub_desT_where;
        }
        
        String onStr = destTab + ".A0100=" + srcTab + ".A0100 and  " + destTab + ".nbase= " + srcTab + ".nbase and "
                + destTab + ".q03z0=" + srcTab + ".q03z0";
        
        String strDWhere = "EXISTS(SELECT 1 FROM " + srcTab + " WHERE " + onStr + " and " + strSWhere + ") and "
                + destTab + ".Q03Z5 in ('01','07')";// 更新目标的表过滤条件
        
        strDWhere = strDWhere + " and " + destTab + ".q03z0>='" + start_date + "' and " + destTab + ".q03z0<='"
                + end_date + "'";// and " + destTab + ".nbase='" + nbase + "'";

        if ("101".equals(this.analyseType) && destCodewhere != null && destCodewhere.length() > 0) {
            strDWhere = strDWhere + " and " + destCodewhere;
        }
        
        strDWhere = strDWhere + " and " + this.kqEmpWhrTmp.replace("{TAB}", destTab);
//        if (whereIN != null && whereIN.length() > 0) {
//            if (!this.userView.isSuper_admin()) {
//                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)
//                    strDWhere = strDWhere
//                            + (" and  EXISTS(select a0100 " + whereIN + " and " + nbase + "A01.a0100=" + destTab + ".a0100)");
//                else
//                    strDWhere = strDWhere
//                            + (" and  EXISTS(select a0100 " + whereIN + " where " + nbase + "A01.a0100=" + destTab + ".a0100)");
//            }
//        }

        if (getKqTypeWhr("q03.q03z3") != null && getKqTypeWhr(this.kq_type).length() > 0) {
            // strDWhere=strDWhere+" and "+getKqTypeWhr("q03.q03z3");
        }
        // 修改已有的
        // System.out.println(strSWhere);
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet.toString(), strDWhere,
                strSWhere);
        // System.out.println(update);
        //String othWhereSql = destTab + ".a0100 in(select a0100 " + whereIN + ")";
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
        if ("101".equals(this.analyseType) && destCodewhere != null && destCodewhere.length() > 0) {
            update = update + " and " + destCodewhere;
        }

        // System.out.println("更新人员的考勤方式--->"+update);
        // String strSet_2=strSet.toString().replace('`',',');
        // 添加没有的
        try {
            // System.out.println(update);
            this.dao.update(update);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("更新全部数据到日明细出错！"));
        }

        if (this.pick_flag == null || !"1".equals(this.pick_flag)) {
            StringBuffer insertSql = new StringBuffer();
            // 往Q03表中写入A01主集中的信息
            if (!"".equals(this.mainsql) || this.mainsql.length() > 0) {
                insertSql.append("INSERT INTO Q03(Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1" + srcFlds.toString()
                        + ",q03z3,q03z5," + this.mainsql + ")");
                insertSql.append(" select Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1" + srcFlds.toString()
                        + ",Q03Z3,'01' as q03z5," + this.mainsql);
            } else {
                insertSql.append("INSERT INTO Q03(Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1" + srcFlds.toString()
                        + ",q03z3,q03z5)");
                insertSql.append(" select Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1" + srcFlds.toString()
                        + ",Q03Z3,'01' as q03z5");
            }
            // insertSql.append("INSERT INTO
            // Q03(Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1"+srcFlds.toString()+",q03z3,q03z5)");
            // insertSql.append(" select
            // Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1"+srcFlds.toString()+",Q03Z3,'01'
            // as q03z5");
            insertSql.append(" from " + srcTab);
            insertSql.append(" where   " + strSWhere + " and NOT EXISTS(SELECT 1 FROM " + destTab + " WHERE " + onStr
                    + ")");
            insertSql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");// and nbase='" + nbase + "'");
            insertSql.append(" and ").append(this.kqEmpWhrTmp.replace("{TAB}", srcTab));
            
//            if (whereIN != null && whereIN.length() > 0) {
//                if (!this.userView.isSuper_admin()) {
//                    if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)
//                        insertSql.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase + "A01.a0100="
//                                + srcTab + ".a0100)");
//                    else
//                        insertSql.append(" and  EXISTS(select a0100 " + whereIN + " where " + nbase + "A01.a0100="
//                                + srcTab + ".a0100)");
//                }
//            }
            if ("101".equals(this.analyseType) && srcCodewhere != null && srcCodewhere.length() > 0) {
                insertSql.append(" and " + srcCodewhere);
            }
            /*
             * if(this.analyseType.equals("101")) insertSql.append(" and
             * "+this.pub_desT_where);
             */
            try {
                ArrayList list = new ArrayList();
                this.dao.insert(insertSql.toString(), list);
            } catch (Exception e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(new GeneralException("更新全部数据到日明细出错！"));
            }
        }

        KqUtilsClass utils = new KqUtilsClass(this.conn);
        if (utils.addColumnToKq("q03")) {
//            StringBuffer where = new StringBuffer();
//            where.append(" where q03z0 between '");
//            where.append(start_date);
//            where.append("' and '");
//            where.append(end_date);
//            where.append("'");
//            where.append(" AND " + strDWhere);
//            utils.updateQ03(nbase, where.toString());
        }

    }

    private void updateBusiDataToQ03(String table_temp, String start_date, String end_date, String code, String kind)
            throws GeneralException {
        String nbase = "";
        String whereIN = "";
        if (this.nbase_list != null && this.nbase_list.size() > 0) {
            //for (int i = 0; i < this.nbase_list.size(); i++) {
            //    nbase = (String) this.nbase_list.get(i);
            //    whereIN = (String) this.whereInMap_forNbase.get(nbase);
                updateBusiDataToQ03(table_temp, start_date, end_date, nbase, whereIN, code, kind);
            //}
        }
    }

    /**
     * 只更新业务数据、比如：请假、加班、公出、班次、刷卡、班次次数统计等
     */
    private void updateBusiDataToQ03(String table_temp, String start_date, String end_date, String nbase,
            String whereIN, String code, String kind) throws GeneralException {
        HashMap item_hs = null;
        String destTab = "q03";// 目标表
        String srcTab = table_temp;// 源表
        String destCodewhere = "";
        String srcCodewhere = "";
        if ("1".equals(kind)) {
            srcCodewhere = table_temp + ".e0122 like '" + code + "%'";
            destCodewhere = destTab + ".e0122 like '" + code + "%'";
        } else if ("0".equals(kind)) {
            srcCodewhere = table_temp + ". e01a1 like '" + code + "%'";
            destCodewhere = destTab + ".e01a1 like '" + code + "%'";
        } else if ("2".equals(kind)) {
            srcCodewhere = table_temp + ".b0110 like '" + code + "%'";
            destCodewhere = destTab + ".b0110 like '" + code + "%'";
        } else if ("-1".equals(kind)) {
            String t = code.substring(3, code.length());
            String t1 = code.substring(0, 3);
            srcCodewhere = table_temp + ".a0100='" + t + "' and " + table_temp + ".nbase='" + t1 + "'";
            destCodewhere = destTab + ".a0100='" + t + "' and " + destTab + ".nbase='" + t1 + "'";
        } else if ("spec".equals(kind)) {
            String t = this.nbase_list.get(0).toString();
            srcCodewhere = table_temp + ".a0100 in " + code + " and " + table_temp + ".nbase='" + t + "'";
            destCodewhere = destTab + ".a0100 in " + code + " and " + destTab + ".nbase='" + t + "'";
        }
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase= " + srcTab + ".nbase and "
                + destTab + ".q03z0=" + srcTab + ".q03z0";// 关联串
                                                          // xxx.field_name=yyyy.field_namex,....
        // strSet.append(destTab+".q03z3="+srcTab+".q03z3");//更新串
        // xxx.field_name=yyyy.field_namex,....
        StringBuffer srcFlds = new StringBuffer();
        String srcFld = "";
        String strSWhere = "";// 源表的过滤条件
        StringBuffer strSWhere1 = new StringBuffer();
        strSWhere1.append(" " + srcTab + ".q03z0>='" + start_date + "' and " + srcTab + ".q03z0<='" + end_date
                + "'");// and " + srcTab + ".nbase='" + nbase + "'");
        
        if ("101".equals(this.analyseType) && srcCodewhere != null && srcCodewhere.length() > 0) {
            strSWhere1.append(" and " + srcCodewhere);
        }
        
        if ("101".equals(this.analyseType)) {
            strSWhere1.append(" and " + this.pub_desT_where);
        }
        
        strSWhere1.append(" and ").append(this.kqEmpWhrTmp.replace("{TAB}", srcTab));
//        if (whereIN != null && whereIN.length() > 0) {
//            if (!this.userView.isSuper_admin()) {
//                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)
//                    strSWhere1.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase + "A01.a0100=" + srcTab
//                            + ".a0100)");
//                else
//                    strSWhere1.append(" and  EXISTS(select a0100 " + whereIN + " where " + nbase + "A01.a0100="
//                            + srcTab + ".a0100)");
//            }
//        }

        String onStr = destTab + ".A0100=" + srcTab + ".A0100 and  " + destTab + ".nbase=" + srcTab + ".nbase and "
                + destTab + ".q03z0=" + srcTab + ".q03z0";
        String strDWhere = "EXISTS(SELECT 1 FROM " + srcTab + " WHERE " + onStr + ") and " + destTab
                + ".Q03Z5 in ('01','07')";// 更新目标的表过滤条件
        strDWhere = strDWhere + " and " + destTab + ".q03z0>='" + start_date + "' and " + destTab + ".q03z0<='"
                + end_date + "'";// and " + destTab + ".nbase='" + nbase + "'";

        if ("101".equals(this.analyseType) && destCodewhere != null && destCodewhere.length() > 0) {
            strDWhere = strDWhere + " and " + destCodewhere;
        }
        
        strDWhere = strDWhere + " and " + this.kqEmpWhrTmp.replace("{TAB}", destTab);
//        if (whereIN != null && whereIN.length() > 0) {
//            if (!this.userView.isSuper_admin()) {
//                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)
//                    strDWhere = strDWhere
//                            + (" and  EXISTS(select a0100 " + whereIN + " and " + nbase + "A01.a0100=" + destTab + ".a0100)");
//                else
//                    strDWhere = strDWhere
//                            + (" and  EXISTS(select a0100 " + whereIN + " where " + nbase + "A01.a0100=" + destTab + ".a0100)");
//            }
//        }
        
        StringBuffer strSet = new StringBuffer();
        String update = "";
        try {
            if (this.no_tranData != null && "1".equals(no_tranData)) {
                srcFld = "q03z1";
                srcFlds.append("," + srcFld);
                // 修改已有的数据来源
                strSet.append(destTab + "." + srcFld + "=" + srcTab + "." + srcFld);
                strSWhere = Sql_switcher.sqlNull(srcTab + "." + srcFld, "0") + ">0.01" + " and "
                        + strSWhere1.toString();
                update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet.toString(), strDWhere, strSWhere);
//                update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
                // System.out.println(update);
                this.dao.update(update);

            } else {
                for (Iterator it = this.kqItem_hash.entrySet().iterator(); it.hasNext();) {
                    Map.Entry e = (Map.Entry) it.next();
                    item_hs = (HashMap) e.getValue();
                    String sdata_src = (String) item_hs.get("sdata_src");
                    String kqItemName = (String) item_hs.get("item_name");
                    // 非从申请单统计指标

                    srcFld = (String) item_hs.get("fielditemid");
                    
                    if ("".equals(sdata_src) && !"Q03Z1".equalsIgnoreCase(srcFld) 
                            && !"调休加班".equals(kqItemName) && !"日期类型".equals(kqItemName)
                            && !this.isClassItem((String)e.getKey())) {
                        continue;
                    }
                    
                    FieldItem fielditem = DataDictionary.getFieldItem(srcFld);
                    // 指标不存在
                    if (fielditem == null) {
                        continue;
                    }

                    // 指标未构库
                    if ("0".equals(fielditem.getUseflag())) {
                        continue;
                    }

                    if (srcFld != null && srcFld.length() > 0) {
                        if (!this.mainsql.toLowerCase().contains(srcFld)) {
                            srcFlds.append("," + srcFld);
                        }
                    }
                    // 修改已有的数据来源
                    strSet.append('`');
                    strSet.append(destTab + "." + srcFld + "=" + srcTab + "." + srcFld);
                    
                }
                
                update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet.toString().substring(1), strDWhere, strSWhere);
                //update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
                // System.out.println(update);
                this.dao.update(update);
            }
//            strSet = destTab + ".q03z3=" + srcTab + ".q03z3";
//            update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere,
//                    strSWhere1.toString());
//            update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
            // this.dao.update(update);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("更新业务数据到日明细出错！"));
        }
        // 添加没有的 日明细统计、人员变动比对不需要添加
        if (this.pick_flag == null || !"1".equals(this.pick_flag)) {
            StringBuffer insertSql = new StringBuffer();
            // insertSql.append("INSERT INTO
            // Q03(Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1"+srcFlds.toString()+",q03z3,q03z5)");
            // 增加A01主集指标
            if (!"".equals(this.mainsql) || this.mainsql.length() > 0) {
                insertSql.append("INSERT INTO Q03(Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1" + srcFlds.toString()
                        + ",q03z3,q03z5," + this.mainsql + ")");
                insertSql.append(" select Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1" + srcFlds.toString()
                        + ",Q03Z3,'01' as q03z5," + this.mainsql);
            } else {
                insertSql.append("INSERT INTO Q03(Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1" + srcFlds.toString()
                        + ",q03z3,q03z5)");
                insertSql.append(" select Q03Z0,nbase,A0100,A0101,B0110,E0122,E01A1" + srcFlds.toString()
                        + ",Q03Z3,'01' as q03z5");
            }
            insertSql.append(" from " + srcTab);
            insertSql.append(" where  NOT EXISTS(SELECT 1 FROM " + destTab + " WHERE " + onStr + ")");
            insertSql.append(" and q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");// and nbase='" + nbase + "'");
            
            insertSql.append(" and ").append(this.kqEmpWhrTmp.replace("{TAB}", srcTab));
//            if (whereIN != null && whereIN.length() > 0) {
//                if (!this.userView.isSuper_admin()) {
//                    if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)
//                        insertSql.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase + "A01.a0100="
//                                + srcTab + ".a0100)");
//                    else
//                        insertSql.append(" and  EXISTS(select a0100 " + whereIN + " where " + nbase + "A01.a0100="
//                                + srcTab + ".a0100)");
//                }
//            }
            if ("101".equals(this.analyseType) && srcCodewhere != null && srcCodewhere.length() > 0) {
                insertSql.append(" and " + srcCodewhere);
            }
            /*
             * if(this.analyseType.equals("101")) insertSql.append(" and
             * "+this.pub_desT_where);
             */

            try {
                ArrayList list = new ArrayList();
                // System.out.println(insertSql.toString());
                dao.insert(insertSql.toString(), list);
            } catch (Exception e) {
                e.printStackTrace();
                throw GeneralExceptionHandler.Handle(new GeneralException("更新业务数据到日明细出错！"));
            }
        }

    }

    /**
     * 是否为班次次数规则（考勤规则名称与班次名称相同）
     * @param kqItemId
     * @return
     */
    private boolean isClassItem(String kqItemId) {
        boolean isClass = false;
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT 1 FROM kq_item");
            sql.append(" WHERE item_id=?");
            sql.append(" AND item_name in (SELECT name FROM kq_class)");
            
            ArrayList params = new ArrayList();
            params.add(kqItemId);
            
            rs = this.dao.search(sql.toString(), params);
            isClass = rs.next();
        } catch(Exception exception) {
            exception.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        
        return isClass;
    }
    
    /**
     * 对比分析业务申请与实际刷卡情况
     * 
     * @param fDayDataTmp
     * @param sTdate
     * @param eTdate
     * @param codewhere
     * @throws GeneralException
     */
    private void compareBusiWithFactCards(String fDayDataTmp, String sTdate, String eTdate, String codewhere)
            throws GeneralException {

        // 得到业务数据集
        queryBusiData(fDayDataTmp, sTdate, eTdate, codewhere);

        if (this.fBusiData == null) {
            return;
        }

        try {
            Date busiB;
            Date busiE;
            String nbase;
            String a0100;
            String appType;
            String applyID;
            String applyTable;
            String overApply;
            String next_nbase;
            String next_a0100;
            String next_appType;
            String supplementId;
            float busiTimeLen = 0;
            float factTimeLen = 0;
            Date cardB;
            Date cardE;
            String busiDate_str = "";
            Date[] cardDs = new Date[2];

            // 业务比对刷卡起始、结束允许的毫秒
            long busiCardBeginMS = this.kqParam.getBusi_cardbegin() * 60 * 1000L;
            long busiCardEndMS = this.kqParam.getBusi_cardend() * 60 * 1000L;

            while (this.fBusiData.next()) {
                supplementId = "";
                factTimeLen = 0;
                cardB = null;
                cardE = null;
                busiB = this.fBusiData.getTimestamp("FromTime");
                busiE = this.fBusiData.getTimestamp("ToTime");
                nbase = this.fBusiData.getString("nbase");
                a0100 = this.fBusiData.getString("a0100");
                appType = this.fBusiData.getString("applyType");
                applyID = this.fBusiData.getString("applyID");
                applyTable = this.fBusiData.getString("busiTab");
                // applyContent = this.fBusiData.getString("applyReason");

                // 如果是已确认的延时加班，则不用比对了。
                // if (this.dataAnalyseUtils.checkAppType(kqItem_Overtime,
                // appType))
                // {
                /*
                 * tranoverApp=(String)this.fTranOverTimeApps.get(applyID);
                 * if(tranoverApp!=null&&tranoverApp.length()>0) continue;
                 */
                // if (applyContent != null &&
                // applyContent.equalsIgnoreCase("延时加班")) {
                // continue;
                // }
                // }

                // 是补申请条已与其它申请合并分析了
                overApply = (String) this.overApplys.get(applyID);
                if (overApply != null && overApply.length() > 0) {
                    continue;
                }

                // 查看下一条是否为补条
                if (!this.fBusiData.isLast()) {
                    this.fBusiData.next();
                    next_nbase = this.fBusiData.getString("nbase");
                    next_a0100 = this.fBusiData.getString("a0100");
                    next_appType = this.fBusiData.getString("applyType");
                    if (nbase.equals(next_nbase) && a0100.equals(next_a0100) && appType.equals(next_appType)
                            && this.dataAnalyseUtils.getPartMinute(busiE, this.fBusiData.getTimestamp("FromTime")) < 3) {
                        busiE = this.fBusiData.getTimestamp("ToTime");
                        supplementId = this.fBusiData.getString("applyID");
                        this.overApplys.put(supplementId, supplementId);

                    } else {
                        // supplementId=this.fBusiData.getString("applyID")!=null&&this.fBusiData.getString("applyID").length()>0?this.fBusiData.getString("applyID"):"";
                        // 不是补条，则跳回当前正在处理的申请
                        this.fBusiData.previous();
                    }
                }

                // 判断销假
                /*
                 * if("q15".equalsIgnoreCase(applyTable)){
                 * this.fLeaveBackData.beforeFirst();//数据集 置首
                 * while(this.fLeaveBackData.next()){ String q1519 =
                 * this.fLeaveBackData.getString("q1519");//获取 销假id
                 * if(applyID.equals(q1519)){ Date xj_busiB =
                 * this.fLeaveBackData.getTimestamp("FromTime"); Date xj_busiE =
                 * this.fLeaveBackData.getTimestamp("ToTime");
                 * if(xj_busiB.equals(busiB) && xj_busiE.equals(busiE)){
                 * continue; }else if(xj_busiB.equals(busiB)){ busiB = xj_busiE;
                 * }else if(xj_busiE.equals(busiE)){ busiE = xj_busiB; } } } }
                 */

                busiTimeLen = this.dataAnalyseUtils.getHourSpan(busiB, busiE);

                // 长申请时间(非加班)
                if (busiTimeLen >= 8 && (!"1".equals(appType.substring(0, 1)))) {
                    // 取申请开始＋半小时～申请结束 之间的刷卡记录
                    Date FTD = DateUtils.getDate(DateUtils.format(busiB, "yyyy.MM.dd HH:mm:ss"), "yyyy.MM.dd HH:mm:ss");
                    FTD.setTime(FTD.getTime() + (30 * 60 * 1000L));
                    getEmpDatas(this.fAnalyseTempTab, nbase, a0100, "", FTD, busiE, false);
                    // 匹配刷卡记录
                    cardDs = this.dataAnalyseUtils.checkNoCardForBusi(busiB, busiE, this.rs_FEmpDatas, cardB, cardE);
                    cardB = cardDs[0];
                    cardE = cardDs[1];
                } else {
                    // 取 申请开始＋提前刷卡～申请结束＋延后刷卡 之间的刷卡记录
                    Date FTD = DateUtils.getDate(datetimeFormatter.format(busiB), "yyyy.MM.dd HH:mm:ss");
                    Date TTD = DateUtils.getDate(datetimeFormatter.format(busiE), "yyyy.MM.dd HH:mm:ss");
                    FTD.setTime(FTD.getTime() - busiCardBeginMS);
                    TTD.setTime(TTD.getTime() + busiCardEndMS);
                    getEmpDatas(this.fAnalyseTempTab, nbase, a0100, "", FTD, TTD, false);

                    cardDs = this.dataAnalyseUtils.checkCardDataForBusi(busiB, busiE, this.rs_FEmpDatas, "1".equals(appType
                            .substring(0, 1)), cardB, cardE, null);
                    cardB = cardDs[0];
                    cardE = cardDs[1];
                    // 无起始刷卡，有结束刷卡，看申请时间段是否包含上班点
                    if ((cardB == null || "".equals(cardB)) && (cardE != null)
                            && ("q15".equalsIgnoreCase(applyTable) || "q13".equalsIgnoreCase(applyTable))) {
                        KqEmpClassBean empBean = this.dataAnalyseUtils.getOneFEmpQry(fDayDataTmp, nbase, a0100,
                                dateFormatter.format(busiB));
                        if (empBean != null && "1".equals(empBean.getOnduty_card_1()) && empBean.getOnduty_1() != null) {
                            Date onduty = OperateDate.strToDate(dateFormatter.format(busiB).replace(".", "-") + " "
                                    + empBean.getOnduty_1(), "yyyy-MM-dd HH:mm");
                            if (busiB.getTime() <= onduty.getTime() && busiE.getTime() >= onduty.getTime()) {
                                cardB = onduty;
                            }
                        }

                    }
                    // 有起始刷卡，无结束刷卡，看申请时间段是否包含下班点
                    if ((cardE == null || "".equals(cardE)) && (cardB != null)
                            && ("q15".equalsIgnoreCase(applyTable) || "q13".equalsIgnoreCase(applyTable))) {
                        KqEmpClassBean empBean = this.dataAnalyseUtils.getOneFEmpQry(fDayDataTmp, nbase, a0100,
                                dateFormatter.format(busiB));
                        if (empBean != null && "1".equals(empBean.getOffduty_card_3())
                                && empBean.getOffduty_3() != null) {
                            Date offduty = OperateDate.strToDate(dateFormatter.format(busiE).replace(".", "-") + " "
                                    + empBean.getOffduty_3(), "yyyy-MM-dd HH:mm");
                            if (busiB.getTime() <= offduty.getTime() && busiE.getTime() >= offduty.getTime()) {
                                cardE = offduty;
                            }
                        } else if (empBean != null && "1".equals(empBean.getOffduty_card_2())
                                && empBean.getOffduty_2() != null) {
                            Date offduty = OperateDate.strToDate(dateFormatter.format(busiE).replace(".", "-") + " "
                                    + empBean.getOffduty_2(), "yyyy-MM-dd HH:mm");
                            if (busiB.getTime() <= offduty.getTime() && busiE.getTime() >= offduty.getTime()) {
                                cardE = offduty;
                            }
                        } else if (empBean != null && "1".equals(empBean.getOffduty_card_1())
                                && empBean.getOffduty_1() != null) {
                            Date offduty = OperateDate.strToDate(dateFormatter.format(busiE).replace(".", "-") + " "
                                    + empBean.getOffduty_1(), "yyyy-MM-dd HH:mm");
                            if (busiB.getTime() <= offduty.getTime() && busiE.getTime() >= offduty.getTime()) {
                                cardE = offduty;
                            }
                        }
                    }
                }

                // 如果是平时加班且申请起始刷卡未找到，那么检测是否申请的是延时加班，
                // 如是延时加班，则用下班时间作为起始刷卡
                if (this.dataAnalyseUtils.checkAppType(DateAnalyseImp.kqItem_Overtime, appType)
                        && (cardB == null || this.dataAnalyseUtils.getPartMinute(cardB, busiB) > 0
                                && (cardE != null && busiTimeLen < 8))) {
                    KqEmpClassBean empBean = this.dataAnalyseUtils.getOneFEmpQry(fDayDataTmp, nbase, a0100,
                            dateFormatter.format(busiB));
                    if (empBean != null && empBean.getClass_id() != null && empBean.getClass_id().length() > 0) {
                        busiDate_str = dateFormatter.format(busiB);
                        if (empBean.getClass_id() != null && empBean.getClass_id().length() > 0
                                && empBean.getOffduty() != null && empBean.getOffduty().length() > 0
                                && empBean.getOnduty() != null && empBean.getOnduty().length() > 0) {
                            // 非跨天班（跨天班暂时不考虑）
                            if (!"0".equals(empBean.getClass_id())
                                    && this.dataAnalyseUtils.getPartMinute(
                                            DateUtils.getDate(empBean.getOnduty(), "HH:mm"),
                                            DateUtils.getDate(empBean.getOffduty(), "HH:mm")) > 0) {
                                Date TTD = DateUtils.getDate(busiDate_str + " " + empBean.getOffduty(),
                                        "yyyy.MM.dd HH:mm");
                                if (cardB == null || this.dataAnalyseUtils.getPartMinute(cardB, TTD) > 0) {
                                    // 开始刷卡在下班之前，并且业务申请起始时间在下班前后1小时之间
                                    Date TTD1 = DateUtils.getDate(busiDate_str + " " + empBean.getOffduty(),
                                            "yyyy.MM.dd HH:mm");
                                    TTD1.setTime(TTD.getTime() - (30 * 60 * 1000L));
                                    Date TTD2 = DateUtils.getDate(busiDate_str + " " + empBean.getOffduty(),
                                            "yyyy.MM.dd HH:mm");
                                    TTD2.setTime(TTD.getTime() + (30 * 60 * 1000L));

                                    if (this.dataAnalyseUtils.getPartMinute(TTD1, busiB) >= 0
                                            && this.dataAnalyseUtils.getPartMinute(TTD2, busiB) <= 0) {
                                        cardB = TTD;
                                    }
                                }
                            }

                        }
                    }
                }
                // 按找到的刷卡时间来判断申请与实际是否相符
                if (cardB != null && cardE != null) {
                    factTimeLen = this.dataAnalyseUtils.getHourSpan(cardB, cardE);

                    if (((factTimeLen > busiTimeLen) && ((factTimeLen - busiTimeLen) * 60 > this.kqParam
                            .getBusifact_diff()))
                            || ((factTimeLen < busiTimeLen) && ((busiTimeLen - factTimeLen) * 60 > this.kqParam
                                    .getBusi_morethan_fact()))) {
                        // 刷卡时长远大于申请时长||申请时长远大于刷卡时长

                        combineAndExecuteSQL(this.fBusiData, busiTimeLen, busiB, busiE, cardB, cardE, factTimeLen,
                                supplementId);

                    }
                } else {
                    combineAndExecuteSQL(this.fBusiData, busiTimeLen, busiB, busiE, cardB, cardE, 0, supplementId);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("对比分析业务申请与实际刷卡情况数据出错！"));
        } finally {
            KqUtilsClass.closeDBResource(this.fLeaveBackData);
            KqUtilsClass.closeDBResource(this.fBusiData);
        }
    }

    /**
     * 查找某时间范围内的刷卡记录。 是否将找到的刷卡记录更新到临时表中（正常分析刷卡数据时用）。
     * 
     * @param table_temp
     * @param nbase
     * @param a0100
     * @param strDate
     * @param FTD
     * @param TTD
     * @param empClassBean
     * @param isInsertCardTime
     *            ,是否统计刷卡时间，放到临时表里面
     */
    private void getEmpDatas(String table_temp, String nbase, String a0100, String strDate, Date FTD, Date TTD,
            boolean isInsertCardTime) {
        String strFDate = dateFormatter.format(FTD);
        String strTDate = dateFormatter.format(TTD);
        String strFTime = timeFormatter.format(FTD);
        String strTTime = timeFormatter.format(TTD);

        strWhrBuffer.setLength(0);
        strWhrBuffer.append(" where nbase='" + nbase);
        strWhrBuffer.append("' and a0100='" + a0100);
        if (!strFDate.equals(strTDate)) {
            strWhrBuffer.append("' and ((work_date='" + strFDate);
            strWhrBuffer.append("' and work_time>='" + strFTime);
            strWhrBuffer.append("') or (work_date='" + strTDate);
            strWhrBuffer.append("' and work_time<='" + strTTime);
            strWhrBuffer.append("') or (work_date>'" + strFDate);
            strWhrBuffer.append("' and work_date<'" + strTDate);
            strWhrBuffer.append("'))");
        } else {
            strWhrBuffer.append("' and work_date='" + strFDate);
            strWhrBuffer.append("' and work_time>='" + strFTime);
            strWhrBuffer.append("' and work_time<='" + strTTime);
            strWhrBuffer.append("'");
        }

        /*
         * strWhe.append(" and "+Sql_switcher.isnull("location","'kq'")+"<>'补签到'"
         * );
         * strWhe.append(" and "+Sql_switcher.isnull("location","'kq'")+"<>'补签退'"
         * );
         */
        strWhrBuffer.append(" and " + Sql_switcher.isnull("sp_flag", "'03'") + "='03'");
        strWhrBuffer.append(" and " + Sql_switcher.isnull("iscommon", "'1'") + "='1'");
        
        strSQLBuffer.setLength(0);
        strSQLBuffer.append("select DISTINCT work_date,work_time from kq_originality_data");
        strSQLBuffer.append(strWhrBuffer.toString());
        strSQLBuffer.append(" ORDER BY work_date, work_time");

        // System.out.println(sql.toString());
        try {
            // 将查到的原始记录状态设成为处理
            String updateSQL = "update kq_originality_data set status=NULL " + strWhrBuffer.toString();
            dao.update(updateSQL);

            this.rs_FEmpDatas = dao.search(strSQLBuffer.toString());
            if (table_temp == null || "".equals(table_temp)) {
                return;
            }

            if (isInsertCardTime) {
                cardTimesBuffer.setLength(0);
                while (this.rs_FEmpDatas.next()) {
                    cardTimesBuffer.append(this.rs_FEmpDatas.getString("work_time") + ",");
                }
                if (cardTimesBuffer.toString() != null && cardTimesBuffer.toString().length() > 0) {
                    cardTimesBuffer.setLength(cardTimesBuffer.length() - 1);
                    updateDataToTmp(table_temp, a0100, nbase, strDate, "card_time", cardTimesBuffer.toString());
                }
                this.rs_FEmpDatas.beforeFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * //记录到分析表中
     * 
     * @param cardData
     * @param busiTimeLen
     * @param busiB
     * @param busiE
     */
    private void combineAndExecuteSQL(RowSet busiData, float busiTimeLen, Date busiB, Date busiE, Date cardB,
            Date cardE, float factTimeLen, String supplementId) throws GeneralException// 记录分析表中
    {
        if (supplementId == null || supplementId.length() <= 0) {
            supplementId = "";
        }

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO  " + this.fBusiCompareTab);
        sql.append("(ID,nbase,A0100,B0110,E0122,E01A1,");
        sql.append("A0101,Q03Z0,busi_begin,busi_end,busi_timelen,fact_begin,fact_end,");
        sql.append("fact_timelen,busi_type,status,flag,appid,supplement)");
        sql.append(" values ");
        sql.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        try {
            ArrayList list = new ArrayList();
            list.add(this.dataAnalyseUtils.getNewID(this.fBusiCompareTab));
            list.add(busiData.getString("nbase"));
            list.add(busiData.getString("a0100"));
            list.add(busiData.getString("B0110"));
            list.add(busiData.getString("E0122"));
            list.add(busiData.getString("E01A1"));
            list.add(busiData.getString("A0101"));
            list.add(DateUtils.format(busiB, "yyyy.MM.dd"));

            // list.add(DateUtils.getSqlDate(DateUtils.format(busiB,"yyyy.MM.dd
            // HH:MM"), "yyyy.MM.dd HH:MM"));
            // list.add(DateUtils.getSqlDate(DateUtils.format(busiE,"yyyy.MM.dd
            // HH:MM"), "yyyy.MM.dd HH:MM"));
            // list.add(DateUtils.getSqlDate(DateUtils.format(busiB, "yyyy.MM.dd
            // HH:MM:ss"), "yyyy.MM.dd HH:mm:ss"));
            // list.add(DateUtils.getSqlDate(DateUtils.format(busiE, "yyyy.MM.dd
            // HH:MM:ss"), "yyyy.MM.dd HH:mm:ss"));
            list.add(busiB);
            list.add(busiE);
            list.add(new Float(busiTimeLen));
            switch (Sql_switcher.searchDbServer()) {
            case Constant.MSSQL: {
                if (cardB != null) {
                    list.add(DateUtils.format(cardB, "yyyy.MM.dd HH:mm:ss"));
                } else {
                    list.add(null);
                }

                if (cardE != null) {
                    list.add(DateUtils.format(cardE, "yyyy.MM.dd HH:mm:ss"));
                } else {
                    list.add(null);
                }

                break;
            }
            case Constant.ORACEL: {
                if (cardB != null) {
                    list.add(new Timestamp(cardB.getTime()));
                } else {
                    list.add(null);
                }

                if (cardE != null) {
                    list.add(new Timestamp(cardE.getTime()));
                } else {
                    list.add(null);
                }

                break;
            }
            case Constant.DB2: {
                if (cardB != null) {
                    list.add(DateUtils.format(cardB, "yyyy.MM.dd HH:mm:ss"));
                } else {
                    list.add(null);
                }

                if (cardE != null) {
                    list.add(DateUtils.format(cardE, "yyyy.MM.dd HH:mm:ss"));
                } else {
                    list.add(null);
                }
                break;
            }
            }

            list.add(new Float(factTimeLen));
            list.add(busiData.getString("applytype"));
            list.add(new Integer(0));
            list.add(new Integer(0));
            list.add(busiData.getString("applyid"));
            list.add(supplementId);
            this.dao.insert(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("对比分析业务申请与实际刷卡情况数据出错！"));
        }

    }

    /**
     * 申请数据的数据集
     * 
     * @param fDayDataTmp
     * @param sTdate
     * @param eTdate
     * @param codewhere
     */
    private void queryBusiData(String fDayDataTmp, String fromDate, String toDate, String codewhere) {

        this.dataAnalyseUtils.createTmpBusiDataTab(this.fTmpBusiDataTab);

        StringBuffer strSQL = new StringBuffer();
        String uptSQL;

        String oToDate = toDate.substring(0, 10);
        Date aDate = DateUtils.addDays(DateUtils.getDate(oToDate, "yyyy.MM.dd"), 1);
        oToDate = DateUtils.FormatDate(aDate, "yyyy.MM.dd");

        // 将业务申请插入到临时表中
        strSQL.append("INSERT INTO ");
        strSQL.append(fTmpBusiDataTab);
        strSQL.append("(busiTab,applyType,fromTime,toTime,applyID,applyReason,");
        strSQL.append(" nbase,A0100,B0110,E0122,E01A1,A0101)");
        strSQL.append(" SELECT applyTable,applyType,fromTime,toTime,applyID,applyContent,");
        strSQL.append(" nbase,A0100,B0110,E0122,E01A1,A0101 FROM (");
        strSQL.append(this.dataAnalyseUtils.getBusiSQL("q11", fDayDataTmp, fromDate, oToDate, "", codewhere));
        strSQL.append(" UNION ALL ");
        strSQL.append(this.dataAnalyseUtils.getBusiSQL("q13", fDayDataTmp, fromDate, oToDate, "", codewhere));
        strSQL.append(" UNION ALL ");
        strSQL.append(this.dataAnalyseUtils.getBusiSQL("q15", fDayDataTmp, fromDate, oToDate, "", codewhere));
        strSQL.append(") AAA");
        strSQL.append(" ORDER BY nbase,A0100,FromTime");
        try {
            dao.update(strSQL.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        /**销假处理增加公出 加班**/
        // 处理请假销假情况
        handleQXJBusiData("Q15");
        // 处理加班销假情况
        handleQXJBusiData("Q11");        
        // 处理公出销假情况
        handleQXJBusiData("Q13");
        /****************************/
        uptSQL = "SELECT busiTab,applyType,fromTime,toTime,applyID,nbase,A0100,B0110,E0122,E01A1,A0101 FROM "
                + fTmpBusiDataTab + " ORDER BY nbase,A0100,fromtime";
        try {
            this.fBusiData = this.dao.search(uptSQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理销假情况
     * @param table Q15/Q13/Q11
     * 
     */
    private void handleQXJBusiData(String table) {

        // 校验销假单号指标是否存在
        FieldItem item = DataDictionary.getFieldItem(table+"19");
        // 若不存在或未构库直接返回
        if(null==item || !"1".equals(item.getUseflag())) {
            return;
        }
        // Q15.Q15
        String tablep = table+"."+table;
        
        StringBuffer strSQL = new StringBuffer();
        StringBuffer strOn = new StringBuffer();
        String strUpts;
        String strSrcWhr;
        String strDestWhr;
        String uptSQL;
        // 处理销假情况
        // 情况1、删除销假销掉全部的
        strSQL.setLength(0);
        strSQL.append("DELETE FROM ").append(fTmpBusiDataTab);
        strSQL.append(" WHERE busiTab='").append(table).append("' AND EXISTS(SELECT 1 FROM ").append(table);
        strSQL.append(" WHERE ").append(table).append("19=").append(fTmpBusiDataTab).append(".applyID");
        strSQL.append(" AND ").append(table).append("Z1=").append(fTmpBusiDataTab).append(".fromTime");
        strSQL.append(" AND ").append(table).append("Z3=").append(fTmpBusiDataTab).append(".toTime ");
        strSQL.append(" AND ").append(table).append("Z5='03')");
        try {
            dao.update(strSQL.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 情况2、销假销掉后一部分
        strOn.setLength(0);
        strOn.append(fTmpBusiDataTab ).append( ".applyID=").append(tablep).append("19 ");
        strOn.append(" AND " ).append(fTmpBusiDataTab).append( ".nbase=").append(table).append(".nbase");
        strOn.append(" AND " ).append(fTmpBusiDataTab).append( ".A0100=").append(table).append(".A0100");
        strOn.append(" AND " ).append(fTmpBusiDataTab).append( ".toTime=").append(tablep).append("Z3");
        strOn.append(" AND " ).append(fTmpBusiDataTab).append( ".fromTime<").append(tablep).append("Z1");

        strDestWhr = fTmpBusiDataTab + ".busiTab='"+table+"'";
        strSrcWhr = tablep+"Z5='03' AND "+tablep+"19 IS NOT NULL AND "+tablep+"19<>''";
        strUpts = fTmpBusiDataTab + ".toTime=";
        switch (Sql_switcher.searchDbServer()) {
        case Constant.MSSQL:
            strUpts = strUpts + "DATEADD(SECOND,-1,"+tablep+"Z1)";
            break;
        default:
            strUpts = strUpts + "("+tablep+"Z1-1/86400)";
            break;
        }

        uptSQL = Sql_switcher.getUpdateSqlTwoTable(fTmpBusiDataTab, table, strOn.toString(), strUpts, strDestWhr,
                strSrcWhr);
        try {
            dao.update(uptSQL);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 情况3、销假销掉前一部分
        strOn.setLength(0);
        strOn.append(fTmpBusiDataTab ).append( ".applyID=").append(tablep).append("19 ");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".nbase=").append(table).append(".nbase");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".A0100=").append(table).append(".A0100");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".fromTime=").append(tablep).append("Z1");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".toTime>").append(tablep).append("Z3");

        strDestWhr = fTmpBusiDataTab + ".busiTab='"+table+"'";
        strSrcWhr = tablep+"Z5='03' AND "+tablep+"19 IS NOT NULL AND "+tablep+"19<>''";
        strUpts = fTmpBusiDataTab + ".fromTime=";
        switch (Sql_switcher.searchDbServer()) {
        case Constant.MSSQL:
            strUpts = strUpts + "DATEADD(SECOND,1,"+tablep+"Z3)";
            break;
        default:
            strUpts = strUpts + "("+tablep+"Z3+1/86400)";
            break;
        }

        uptSQL = Sql_switcher.getUpdateSqlTwoTable(fTmpBusiDataTab, table, strOn.toString(), strUpts, strDestWhr,
                strSrcWhr);
        try {
            dao.update(uptSQL);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 情况4、销假销掉中间部分
        strSQL.setLength(0);
        strSQL.append("INSERT INTO " ).append( fTmpBusiDataTab ).append( "(busiTab,applyType,fromTime,toTime,applyID,applyReason,");
        strSQL.append("nbase,A0100,B0110,E0122,E01A1,A0101)");
        strSQL.append("SELECT busiTab,applyType" ).append( Sql_switcher.concat() ).append( "'_1',fromTime,toTime,applyID,applyReason,");
        strSQL.append("nbase,A0100,B0110,E0122,E01A1,A0101");
        strSQL.append(" FROM " ).append( fTmpBusiDataTab ).append( " A");
        strSQL.append(" WHERE EXISTS(SELECT 1 FROM ").append(table).append(" WHERE A.applyID=").append(tablep).append("19 AND ").append(table).append(".nbase=A.nbase AND ").append(table).append(".A0100=A.A0100");
        strSQL.append(" AND ").append(table).append("Z1>A.fromTime AND ").append(table).append("Z3<A.toTime)");
        try {
            dao.update(strSQL.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        strOn.setLength(0);
        strOn.append(fTmpBusiDataTab ).append( ".applyID=").append(tablep).append("19");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".nbase=").append(table).append(".nbase");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".A0100=").append(table).append(".A0100");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".fromTime<").append(tablep).append("Z1");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".toTime>").append(tablep).append("Z3");
        strDestWhr = fTmpBusiDataTab + ".busiTab='"+table+"'";
        strSrcWhr = tablep+"Z5='03' AND "+tablep+"19 IS NOT NULL AND "+tablep+"19<>''";

        strUpts = fTmpBusiDataTab + ".toTime=";
        switch (Sql_switcher.searchDbServer()) {
        case Constant.MSSQL:
            strUpts = strUpts + "DATEADD(SECOND,-1,"+tablep+"Z1)";
            break;
        default:
            strUpts = strUpts + "("+tablep+"Z1-1/86400)";
            break;
        }

        uptSQL = Sql_switcher.getUpdateSqlTwoTable(fTmpBusiDataTab, table, strOn.toString(), strUpts, strDestWhr,
                strSrcWhr);
        try {
            dao.update(uptSQL);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        strOn.setLength(0);
        strOn.append(fTmpBusiDataTab ).append( ".applyID=(").append(tablep).append("19" + Sql_switcher.concat() + "'_1')");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".nbase=").append(table).append(".nbase");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".A0100=").append(table).append(".A0100");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".fromTime<").append(tablep).append("Z1");
        strOn.append(" AND " ).append( fTmpBusiDataTab ).append( ".toTime>").append(tablep).append("Z3");

        strUpts = fTmpBusiDataTab + ".fromTime=";
        switch (Sql_switcher.searchDbServer()) {
        case Constant.MSSQL:
            strUpts = strUpts + "DATEADD(SECOND,1,"+tablep+"Z3)";
            break;
        default:
            strUpts = strUpts + "("+tablep+"Z3+1/86400)";
            break;
        }

        uptSQL = Sql_switcher.getUpdateSqlTwoTable(fTmpBusiDataTab, table, strOn.toString(), strUpts, strDestWhr,
                strSrcWhr);
        try {
            dao.update(uptSQL);
        } catch (Exception e) {
            e.printStackTrace();
            this.fBusiData = null;
            return;
        }
    }
    /**
     * //插入刷卡分析结果
     * 
     * @param table_temp
     * @param a0100
     * @param nbase
     * @param strDate
     * @param fieldItem
     * @param valueStr
     */
    private void updateDataToTmp(String table_temp, String a0100, String nbase, String strDate, String fieldItem,
            String valueStr) {
        if (fieldItem == null || fieldItem.length() <= 0) {
            return;
        }
        StringBuffer sql = new StringBuffer();
        sql.append("update " + table_temp + " set");
        sql.append(" " + fieldItem + "=? where a0100=? and nbase=? and q03z0=?");
        ArrayList list = new ArrayList();
        list.add(valueStr);
        list.add(a0100);
        list.add(nbase);
        list.add(strDate);
        try {
            this.dao.update(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 集中处理时：表中保存本考勤期间的数据，删除不是本考勤期间的数据 WANGY
     * 
     * @param table_name
     * @return
     */
    public void deletedata() throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            ArrayList list = RegisterDate.getKqDayList(this.conn);
            String ksdate = "";
            String jsend = "";
            if (list != null && list.size() > 0) {
                ksdate = list.get(0).toString(); // 考勤期间开始日期
                jsend = list.get(1).toString(); // 考勤期间结束日期
            }

            // zxj 20141107 原只删自己权限范围内的数据，这样容易留下垃圾数据，现改为删除全部非本期间数据
            StringBuffer sql = new StringBuffer();
            sql.append("delete from " + this.fAnalyseTempTab);
            sql.append(" where q03z0<'" + ksdate + "' OR q03z0>'" + jsend + "'");
            dao.update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("删除考勤处理表中非本期间数据出错！");
        }
    }

    /**
     * 数据处理集中处理方式 时间表 wangy
     * 
     * @param start_date
     *            开始时间
     * @param end_date
     *            结束时间
     */
    public void analyseDate(String start_date, String end_date, String tabledate) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        String usrName = "";
        sql.append("select user_name from " + tabledate + " where user_name='" + userView.getUserName() + "'");
        try {
            rowSet = dao.search(sql.toString());
            if (rowSet.next()) {
                usrName = rowSet.getString("user_name");
            }
            if (!"".equals(usrName) && usrName.length() > 0) {
                sql.setLength(0);
                sql.append("update " + tabledate + " set BEGIN_DATE='" + start_date + "',END_DATE='" + end_date + "'");
                sql.append(" where User_Name='" + userView.getUserName() + "'");
                dao.update(sql.toString());
            } else {
                sql.setLength(0);
                ArrayList listdate = new ArrayList();
                sql.append("INSERT INTO " + tabledate + "(User_Name,BEGIN_DATE,END_DATE) VALUES ");
                sql.append("('" + userView.getUserName() + "','" + start_date + "','" + end_date + "')");
                dao.insert(sql.toString(), listdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("初始化考勤处理时间表数据出错！"));
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
    }

    /**
     * 数据处理集中处理 人员表 wangy
     * 
     * @param emptable
     * @param code
     * @param kind
     */
    public void analyseEmp(String emptable, String code, String kind) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        sql.append("DELETE FROM " + emptable + " WHERE User_Name='" + userView.getUserName() + "'");
        try {
            ArrayList list = new ArrayList();
            dao.delete(sql.toString(), list);
            String codewhere = "";
            String nbase = "";
            String whereIN = "";
            if (this.nbase_list != null && this.nbase_list.size() > 0) {
                for (int i = 0; i < this.nbase_list.size(); i++) {
                    nbase = (String) this.nbase_list.get(i);
                    whereIN = (String) this.whereInMap_forNbase.get(nbase);
                    if (this.whereCode_List != null && this.whereCode_List.size() > 0) {
                        for (int r = 0; r < this.whereCode_List.size(); r++) {
                            codewhere = (String) this.whereCode_List.get(r);
                            insertAnalyseEmp(nbase, codewhere, whereIN, emptable);
                        }
                    } else {
                        insertAnalyseEmp(nbase, codewhere, whereIN, emptable);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("初始化考勤处理人员数据出错！"));
        }
    }

    /**
     * 数据处理集中处理 人员表 写入 wangy
     * 
     * @param nbase
     * @param codewhere
     * @param whereIN
     * @param emptable
     */
    public void insertAnalyseEmp(String nbase, String codewhere, String whereIN, String emptable)
            throws GeneralException {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into " + emptable + "(User_Name,NBase,A0100)");

        if (codewhere.contains("a0100") && codewhere.contains("nbase")) {
            sql.append(" select name,nbase,a0100 from (");
        }

        sql.append(" select '" + userView.getUserName() + "' as name,");
        sql.append("'" + nbase + "' as nbase,a0100 ");
        sql.append("from " + nbase + "A01");
        sql.append(" where NOT EXISTS(select 1 from ").append(emptable).append(" E");
        sql.append(" where E.User_Name='").append(userView.getUserName()).append("'");
        sql.append(" and E.nbase='").append(nbase).append("'");
        sql.append(" and E.a0100=").append(nbase).append("A01").append(".a0100)");
        String kqtypeWhr = getKqTypeWhr(this.kq_type);
        if (kqtypeWhr != null && kqtypeWhr.length() > 0) {
            sql.append(" and " + kqtypeWhr);
        }
        if (this.analyseType != null && ("1".equals(this.analyseType) || "101".equals(this.analyseType))) {
            // sql.append(" and ("+this.kq_card+" is not null or
            // "+this.kq_card+"<>'')");
            sql.append(" and " + Sql_switcher.isnull(this.kq_card, "'##'") + "<>'##'");
        }

        if (!this.userView.isSuper_admin() && whereIN != null && whereIN.length() > 0) {
            sql.append(" and a0100 in(select a0100 " + whereIN + ")");
        }

        if (codewhere.contains("a0100") && codewhere.contains("nbase")) {
            sql.append(") a");
        }

        if (codewhere != null && codewhere.length() > 0) {
            if (codewhere.contains("a0100") && codewhere.contains("nbase")) {
                sql.append(" where " + codewhere);
            } else {
                sql.append(" and " + codewhere);
            }
        }
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            ArrayList list = new ArrayList();
            dao.insert(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("初始化考勤处理人员数据出错！"));
        }
    }

    public boolean getG_no(String table, String code, String kind) {
        boolean flag = true;
        /*
         * StringBuffer sql = new StringBuffer(); ContentDAO dao=new
         * ContentDAO(this.conn); RowSet rowSet = null; String codewhere="";
         * if(kind.equals("1")) { codewhere="e0122 like '"+code+"%'"; }else
         * if(kind.equals("0")) { codewhere="e01a1 like '"+code+"%'"; }else
         * if(kind.equals("2")) { codewhere="b0110 like '"+code+"%'"; }else
         * if(kind.equals("-1")) { String t = code.substring(3,code.length());
         * String t1 = code.substring(0,3); codewhere=" a0100='"+t+"' and
         * nbase='"+t1+"'"; } sql.append("select g_no from kq_analyse_result");
         * sql.append(" where 1=2 ");
         * //if(this.analyseType.equals("101")&&codewhere
         * !=null&&codewhere.length()>0)
         * 
         * try { rowSet=dao.search(sql.toString());
         * 
         * }catch(Exception e) { flag=false; //e.printStackTrace(); }finally {
         * if(rowSet!=null) try { rowSet.close(); } catch (SQLException e) {
         * 
         * e.printStackTrace(); } }
         */
        ReconstructionKqField reconstructionKqField = new ReconstructionKqField(this.conn);
        if (!reconstructionKqField.checkFieldSave(table, "g_no")) {
            flag = false;
        }
        return flag;
    }

    public String getFBusiCompareTab() {
        return fBusiCompareTab;
    }

    public void setFBusiCompareTab(String busiCompareTab) {
        fBusiCompareTab = busiCompareTab;
    }

    public String getFTranOverTimeTab() {
        return fTranOverTimeTab;
    }

    public void setFTranOverTimeTab(String tranOverTimeTab) {
        fTranOverTimeTab = tranOverTimeTab;
    }

    public String getFExceptCardTab() {
        return fExceptCardTab;
    }

    public void setFExceptCardTab(String exceptCardTab) {
        fExceptCardTab = exceptCardTab;
    }

    public String getFAnalyseTempTab() {
        return fAnalyseTempTab;
    }

    public void setFAnalyseTempTab(String analyseTempTab) {
        fAnalyseTempTab = analyseTempTab;
    }

    public String getKq_Gno() {
        return kq_Gno;
    }

    public void setKq_Gno(String kq_Gno) {
        this.kq_Gno = kq_Gno;
    }

    public String getNo_tranData() {
        return no_tranData;
    }

    public void setNo_tranData(String no_tranData) {
        this.no_tranData = no_tranData;
    }

    public String getCreat_pick() {
        return creat_pick;
    }

    public void setCreat_pick(String creat_pick) {
        this.creat_pick = creat_pick;
    }

    /**
     * 删除异常数据表，延时加班表，业务申请与实际刷卡情况表表数据
     * 
     * @param tmpTable_flag
     *            1：异常数据表；2：延时加班表；3：业务申请与实际刷卡情况表表数据
     * @param code
     * @param kind
     * @param start_date
     * @param end_date
     * @throws GeneralException
     */
    private void otherTempTabEmps(String tmpTable_flag, String code, String kind, String start_date, String end_date)
            throws GeneralException {
        String codewhere = "";
        String nbase = "";
        String whereIN = "";
        ContentDAO dao = new ContentDAO(this.conn);
        if (this.nbase_list != null && this.nbase_list.size() > 0) {
            for (int i = 0; i < this.nbase_list.size(); i++) {
                nbase = (String) this.nbase_list.get(i);
                whereIN = (String) this.whereInMap_forNbase.get(nbase);
                if (this.whereCode_List != null && this.whereCode_List.size() > 0) {
                    for (int r = 0; r < this.whereCode_List.size(); r++) {
                        codewhere = (String) this.whereCode_List.get(r);
                        deleteTempTable(dao, tmpTable_flag, nbase, whereIN, codewhere, start_date, end_date);
                    }
                } else {
                    deleteTempTable(dao, tmpTable_flag, nbase, whereIN, codewhere, start_date, end_date);
                }
            }
        }
        // ContentDAO dao=new ContentDAO(this.conn);
        /*
         * if(code!=null&&code.length()>0&&!kind.equals("-1")) { hashMap=new
         * HashMap(); String b0110=this.dataAnalyseUtils.getB0110(code,kind);
         * ArrayList
         * dblist=RegisterInitInfoData.getB0110Dase(hashMap,userView,conn
         * ,b0110); if(dblist==null||dblist.size()<=0) dblist=this.db_list;
         * if(dblist==null||dblist.size()<=0) return; String nbase=""; String
         * whereIN="";
         * 
         * if(kind.equals("1")) { codewhere="e0122 like '"+code+"%'"; }else
         * if(kind.equals("0")) { codewhere="e01a1 like '"+code+"%'"; }else
         * if(kind.equals("2")) { codewhere="b0110 like '"+code+"%'"; }else
         * if(kind.equals("-1")) { String t = code.substring(3,code.length());
         * String t1 = code.substring(0,3); codewhere=" a0100='"+t+"' and
         * nbase='"+t1+"'"; } for(int i=0;i<dblist.size();i++) {
         * nbase=(String)dblist.get(i);
         * whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
         * deleteTempTable
         * (dao,tmpTable_flag,nbase,whereIN,codewhere,start_date,end_date); }
         * }else if(kind.equals("-1")) { String a0100=code.substring(3); String
         * nbase=code.substring(0,3); String
         * b0110=getB0110ForA0100(nbase,a0100); codewhere=" a0100='"+a0100+"'";
         * deleteTempTable
         * (dao,tmpTable_flag,nbase,"",codewhere,start_date,end_date);
         * 
         * }else { ArrayList dblist=new ArrayList();
         * if(this.db_list==null||this.db_list.size()<=0)
         * dblist=this.userView.getPrivDbList(); else dblist=this.db_list;
         * for(int r=0;r<dblist.size();r++) { String
         * userbase=dblist.get(r).toString();
         * 
         * String
         * whereIN=RegisterInitInfoData.getWhereINSql(this.userView,userbase);
         * 
         * //公休日 if(!userView.isSuper_admin()) { String
         * whereB0110=RegisterInitInfoData
         * .selcet_OrgId(userbase,"b0110",whereIN); ArrayList
         * orgidb0110List=OrgRegister
         * .getQrgE0122List(this.conn,whereB0110,"b0110"); for(int
         * t=0;t<orgidb0110List.size();t++) { hashMap=new HashMap(); String
         * b0110_one=orgidb0110List.get(t).toString(); String
         * nbase=RegisterInitInfoData
         * .getOneB0110Dase(hashMap,this.userView,userbase,b0110_one,this.conn);
         *//** ******按照该单位的人员库的操作******** */
        /*
         * if(nbase!=null&&nbase.length()>0) {
         * 
         * codewhere="b0110 like '"+b0110_one+"%'";
         * deleteTempTable(dao,tmpTable_flag
         * ,nbase,whereIN,codewhere,start_date,end_date); } } }else { ArrayList
         * b0100list
         * =RegisterInitInfoData.getAllBaseOrgid(userbase,"b0110",whereIN
         * ,this.conn); for(int n=0;n<b0100list.size();n++) { hashMap=new
         * HashMap(); String b0110_one=b0100list.get(n).toString(); String
         * nbase=
         * RegisterInitInfoData.getOneB0110Dase(hashMap,this.userView,userbase
         * ,b0110_one,this.conn);
         *//** ******按照该单位的人员库的操作******** */
        /*
         * if(nbase!=null&&nbase.length()>0) { codewhere="b0110 like
         * '"+b0110_one+"%'";
         * deleteTempTable(dao,tmpTable_flag,nbase,"",codewhere
         * ,start_date,end_date); } } } } }
         */
    }

    /**
     * 删除异常数据表，延时加班表，业务申请与实际刷卡情况表表数据
     * 
     * @param dao
     * @param tmpTable_flag
     *            1：异常数据表；2：延时加班表；3：业务申请与实际刷卡情况表表数据
     * @param nbase
     * @param whereIN
     * @param codewhere
     * @param start_date
     * @param end_date
     */
    public void deleteTempTable(ContentDAO dao, String tmpTable_flag, String nbase, String whereIN, String codewhere,
            String start_date, String end_date) {
        StringBuffer sql = new StringBuffer();
        String tableName = "";
        sql.append("delete from ");
        String where = "";
        if ("1".equals(tmpTable_flag)) {
            // this.fExceptCardTab异常数据表
            tableName = this.fExceptCardTab;
            where = " work_date>='" + start_date + "' and work_date<='" + end_date + "'";
        } else if ("2".equals(tmpTable_flag)) {
            // this.fTranOverTimeTab延时加班表
            tableName = this.fTranOverTimeTab;
            StringBuffer selectSQL = new StringBuffer();
            String column_z1 = "begin_date";
            String column_z3 = "end_date";
            selectSQL.append(" (" + column_z1 + "<=" + Sql_switcher.dateValue(end_date + " 23:59"));
            selectSQL.append(" AND " + column_z3 + ">=" + Sql_switcher.dateValue(start_date + " 00:00"));
            // selectSQL.append(" and " + column_z1 + "<"
            // + Sql_switcher.charToDate("'" + end_date + "'") + ")");
            // selectSQL.append(" or (" + column_z3 + ">"
            // + Sql_switcher.charToDate("'" + start_date + "'"));
            // selectSQL.append(" and " + column_z3 + "<"
            // + Sql_switcher.charToDate("'" + end_date + "'") + ")");
            // selectSQL.append(" or (" + column_z1 + "<="
            // + Sql_switcher.charToDate("'" + start_date + "'"));
            // selectSQL.append(" and " + column_z3 + ">="
            // + Sql_switcher.charToDate("'" + end_date + "'") + ")");
            selectSQL.append(")");
            where = selectSQL.toString();
        } else if ("3".equals(tmpTable_flag)) {
            // this.fBusiCompareTab业务申请与实际刷卡情况表
            tableName = this.fBusiCompareTab;
            // begin_date end_date
            where = " busi_end>=" + Sql_switcher.dateValue(start_date + " 00:00") + " and busi_begin<="
                    + Sql_switcher.dateValue(end_date + " 23:59") + "";
        } else if ("4".equals(tmpTable_flag)) {
            tableName = this.cardToOverTime;
            // begin_date end_date
            where = " begin_date>=" + Sql_switcher.dateValue(start_date + " 00:00") + " and end_date<="
                    + Sql_switcher.dateValue(end_date + " 23:59") + "";
        } else {
            return;
        }
        sql.append(tableName);
        sql.append(" where nbase='" + nbase + "'");
        if (codewhere != null && codewhere.length() > 0) {
            sql.append(" and " + codewhere);
        }
        if (where != null && where.length() > 0) {
            sql.append(" and " + where);
        }
        if (whereIN != null && whereIN.length() > 0) {
            if (!this.userView.isSuper_admin()) {
                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                    sql.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase + "A01.a0100=" + tableName
                            + ".a0100");
                } else {
                    sql.append(" and  EXISTS(select a0100 " + whereIN + " where " + nbase + "A01.a0100=" + tableName
                            + ".a0100");
                }
                if (codewhere != null && codewhere.length() > 0) {
                    sql.append(" and " + codewhere);
                }
                sql.append(")");
            }
        }
        try {
            // System.out.println(sql.toString());
            dao.delete(sql.toString(), new ArrayList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化公用条件
     * 
     * @param code
     * @param kind
     * @throws GeneralException
     *             组织条件，人员权限条件，人员库条件
     */
    private void initBaseWhere(String code, String kind) throws GeneralException {
        String codewhere = "";
        HashMap hashMap = new HashMap();
        HashMap b0110_hashMap = new HashMap();
        ArrayList dblist = new ArrayList();

        if (code != null && code.length() > 0 && "2".equals(kind)) {
            if (this.db_list == null || this.db_list.size() <= 0) {
                KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
                this.db_list = kqUtilsClass.getKqPreList();
                dblist = this.db_list;
            } else {
                dblist = this.db_list;
            }

            for (int r = 0; r < dblist.size(); r++) {
                String userbase = dblist.get(r).toString();
                String whereIN = RegisterInitInfoData.getWhereINSql(this.userView, userbase);
                this.whereInMap_forNbase.put(userbase, whereIN);
                ArrayList b0100list = RegisterInitInfoData.getAllBaseOrgid(userbase, "b0110", whereIN, conn, code);
                for (int n = 0; n < b0100list.size(); n++) {
                    hashMap = new HashMap();
                    String b0110_one = b0100list.get(n).toString();
                    String nbase = RegisterInitInfoData.getOneB0110Dase(hashMap, this.userView, userbase, b0110_one,
                            this.conn);
                    /** ******按照该单位的人员库的操作******** */
                    if (nbase != null && nbase.length() > 0) {
                        whereIN = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
                        codewhere = "b0110 like '" + b0110_one + "%'";
                        String cc = (String) b0110_hashMap.get(b0110_one);
                        if (cc == null || cc.length() <= 0) {
                            b0110_hashMap.put(b0110_one, b0110_one);
                            this.b0110_list.add(b0110_one);
                            this.whereCode_List.add(codewhere);
                        }
                    }
                }
            }
        } else if (code != null && code.length() > 0 && !"-1".equals(kind) && !"spec".equalsIgnoreCase(kind)) {
            hashMap = new HashMap();
            String b0110 = this.dataAnalyseUtils.getB0110(code, kind);
            dblist = RegisterInitInfoData.getB0110Dase(hashMap, userView, conn, b0110);
            if (dblist == null || dblist.size() <= 0) {
                dblist = this.db_list;
            }
            if (dblist == null || dblist.size() <= 0) {
                return;
            }
            String nbase = "";
            String whereIN = "";

            if ("1".equals(kind)) {
                codewhere = "e0122 like '" + code + "%'";
            } else if ("0".equals(kind)) {
                codewhere = "e01a1 like '" + code + "%'";
            } else if ("2".equals(kind)) {
                codewhere = "b0110 like '" + code + "%'";
            } else if ("-1".equals(kind)) {
                String t = code.substring(3, code.length());
                String t1 = code.substring(0, 3);
                codewhere = " a0100='" + t + "' and nbase='" + t1 + "'";
            }
            for (int i = 0; i < dblist.size(); i++) {
                nbase = (String) dblist.get(i);
                whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                this.whereInMap_forNbase.put(nbase, whereIN);
            }
            this.whereCode_List.add(codewhere);
            this.b0110_list.add(b0110);

        } else if ("-1".equals(kind)) {
            String a0100 = code.substring(3);
            String nbase = code.substring(0, 3);
            String b0110 = getB0110ForA0100(nbase, a0100);
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
            codewhere = " a0100='" + a0100 + "'";
            this.whereCode_List.add(codewhere);
            this.b0110_list.add(b0110);
            this.whereInMap_forNbase.put(nbase, whereIN);
            dblist.add(nbase);
        } else if ("spec".equalsIgnoreCase(kind)) {
            // String[] a0100s = code.split("`");
            StringBuffer wherestr = new StringBuffer();
            wherestr.append("(").append(code.toString()).append(")");
            /*
             * wherestr.append("a0100 in("); String a0100 = ""; for (int i = 0;
             * i < a0100s.length; i++) { if (i == 0) a0100 = a0100s[i];
             * wherestr.append("'" + a0100s[i] + "',"); }
             * wherestr.setLength(wherestr.length() - 1); wherestr.append(")");
             */
            // String b0110 = code.to
            // String b0110 =
            // getB0110ForA0100(this.nbase_list.get(0).toString(), a0100);
            this.whereCode_List.add(wherestr.toString());
            this.b0110_list.add("");
        } else {

            if (this.db_list == null || this.db_list.size() <= 0) {
                KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
                this.db_list = kqUtilsClass.getKqPreList();
                dblist = this.db_list;
            } else {
                dblist = this.db_list;
            }
            for (int r = 0; r < dblist.size(); r++) {
                String userbase = dblist.get(r).toString();
                String whereIN = RegisterInitInfoData.getWhereINSql(this.userView, userbase);
                this.whereInMap_forNbase.put(userbase, whereIN);
                if (!userView.isSuper_admin() && whereIN != null && whereIN.length() > 0) {
                    String whereB0110 = RegisterInitInfoData.selcet_OrgId(userbase, "b0110", whereIN);
                    ArrayList orgidb0110List = OrgRegister.getQrgE0122List(this.conn, whereB0110, "b0110");
                    for (int t = 0; t < orgidb0110List.size(); t++) {
                        hashMap = new HashMap();
                        String b0110_one = orgidb0110List.get(t).toString().trim();
                        String nbase = RegisterInitInfoData.getOneB0110Dase(hashMap, this.userView, userbase,
                                b0110_one, this.conn);
                        /** ******按照该单位的人员库的操作******** */
                        if (nbase != null && nbase.length() > 0) {
                            whereIN = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
                            codewhere = "b0110 like '" + b0110_one + "%'";
                            String cc = (String) b0110_hashMap.get(b0110_one);
                            if (cc == null || cc.length() <= 0) {
                                b0110_hashMap.put(b0110_one, b0110_one);
                                this.b0110_list.add(b0110_one);
                                this.whereCode_List.add(codewhere);
                            }
                        }
                    }
                } else {
                    ArrayList b0100list = RegisterInitInfoData.getAllBaseOrgid(userbase, "b0110", whereIN, this.conn);
                    for (int n = 0; n < b0100list.size(); n++) {
                        // hashMap = new HashMap();
                        String b0110_one = b0100list.get(n).toString();
                        String nbase = userbase;// RegisterInitInfoData.getOneB0110Dase(hashMap,
                                                // this.userView, userbase,
                                                // b0110_one, this.conn);
                        /** ******按照该单位的人员库的操作******** */
                        // if (nbase != null && nbase.length() > 0) {
                        whereIN = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
                        codewhere = "b0110 like '" + b0110_one + "%'";
                        String cc = (String) b0110_hashMap.get(b0110_one);
                        if (cc == null || cc.length() <= 0) {
                            b0110_hashMap.put(b0110_one, b0110_one);
                            this.b0110_list.add(b0110_one);
                            this.whereCode_List.add(codewhere);
                        }
                    }
                }

            }
        }
        this.nbase_list = dblist;
    }

    /**
     * 过滤班次正常的 wangyao
     * 
     * @param code
     * @param kind
     * @param start_date
     * @param end_date
     */
    private void checknormal(String code, String kind, String start_date, String end_date) throws GeneralException {
        String codewhere = "";
        if ("1".equals(kind)) {
            codewhere = " e0122 like '" + code + "%'";
        } else if ("0".equals(kind)) {
            codewhere = " e01a1 like '" + code + "%'";
        } else if ("2".equals(kind)) {
            codewhere = " b0110 like '" + code + "%'";
        } else if ("-1".equals(kind)) {
            String t = code.substring(3, code.length());
            String t1 = code.substring(0, 3);
            codewhere = " a0100='" + t + "' and nbase='" + t1 + "'";
        } else if ("spec".equals(kind)) {
            codewhere = this.whereCode_List.get(0).toString();// "a0100 in " +
                                                              // code +
                                                              // " and nbase='"
                                                              // + t + "'";
        }

        StringBuilder uptWhr = new StringBuilder();
        uptWhr.append("q03z0>='" + start_date + "' and q03z0<='" + end_date + "'");

        if (codewhere != null && codewhere.length() > 0) {
            uptWhr.append(" and (" + codewhere).append(")");
        }

        if ("101".equals(this.analyseType)) {
            uptWhr.append(" and (" + this.pub_desT_where).append(")");
        }

        String upsql = "update " + this.fAnalyseTempTab + " set  flag=1,IsOk='正常',LackCard='0',ISNormal=0 where "
                + uptWhr.toString();

        if (this.userView.isSuper_admin()) {
            try {
                this.dao.update(upsql);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new GeneralException("考勤处理表过滤正常班次数据出错！");
            }

            if (!"101".equals(this.analyseType) && !"1".equals(this.analyseType)) {
                return;
            }

            if (canUseQuickMode()) {
                String nbase = "";
                for (int i = 0; i < this.nbase_list.size(); i++) {
                    nbase = (String) this.nbase_list.get(i);
                    checknormal(code, kind, start_date, end_date, nbase, "");
                }
            }
        } else {
            try {
                String nbase = "";
                String whereIN = "";
                if (this.nbase_list != null && this.nbase_list.size() > 0) {
                    for (int i = 0; i < this.nbase_list.size(); i++) {
                        nbase = (String) this.nbase_list.get(i);
                        whereIN = (String) this.whereInMap_forNbase.get(nbase);
                        String sql = upsql + " and nbase ='" + nbase + "'";
                        if (whereIN != null && whereIN.length() > 0) {
                            if (!this.userView.isSuper_admin()) {
                                if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                                    sql += (" and  EXISTS(select a0100 " + whereIN + " and " + nbase + "A01.a0100="
                                            + this.fAnalyseTempTab + ".a0100)");
                                } else {
                                    sql += (" and  EXISTS(select a0100 " + whereIN + " where " + nbase + "A01.a0100="
                                            + this.fAnalyseTempTab + ".a0100)");
                                }
                            }
                        }
                        this.dao.update(sql);
                        if (canUseQuickMode()) {
                            if ("101".equals(this.analyseType) || "1".equals(this.analyseType)) {
                                checknormal(code, kind, start_date, end_date, nbase, whereIN);
                            }

                        }
                    }
                }
                /*
                 * this.dao.update(upsql);
                 * 
                 * 
                 * //检查正常的数据 首钢增加 if(quick_analyse_mode.equalsIgnoreCase("1")) {
                 * checknormal(code,kind); }
                 */
            } catch (SQLException e1) {
                e1.printStackTrace();
                throw new GeneralException("考勤处理表过滤正常班次数据出错！");
            }
        }

        if (canUseQuickMode() && ("101".equals(this.analyseType) || "1".equals(this.analyseType))) {
            updateCardTimeForNormalData(uptWhr.toString());
        }
    }

    private boolean canUseQuickMode() {
        // 数据处理精简方式 0：原处理方式 1：首钢精简处理方式
        String quick_analyse_mode = KqParam.getInstance().getQuickAnalyseMode();
        quick_analyse_mode = quick_analyse_mode != null && quick_analyse_mode.length() > 0 ? quick_analyse_mode : "0";

        int version = 8;
        try {
            DatabaseMetaData dbMeta = this.conn.getMetaData();
            version = dbMeta.getDatabaseMajorVersion(); // sql2000=8 sql2005=9
                                                        // sql2008=10 sql2012=11
        } catch (Exception e) {

        }

        return "1".equalsIgnoreCase(quick_analyse_mode) // 考勤参数中明确指定按精简模式处理
                || Constant.ORACEL == Sql_switcher.searchDbServer() // oracle库可以快速处理
                || (Constant.MSSQL == Sql_switcher.searchDbServer() && version >= 9); // sql2005及以上可以快速处理
    }

    /**
     * 加过滤条件修改表
     * 
     * @param update
     * @throws GeneralException
     * @throws SQLException
     */
    private void andFilterTermUpdate(String update) throws GeneralException, SQLException {
        String nbase = "";
        String whereIN = "";
        if (this.nbase_list != null && this.nbase_list.size() > 0) {
            String update2 = update;
            //if (!this.userView.isSuper_admin() && !this.fAnalyseTempTab.startsWith("KT_")) {
                whereIN = this.kqEmpWhrTmp.replace("{TAB}", this.fAnalyseTempTab); //(String) this.whereInMap_forNbase.get(nbase);
                update2 = update2 + " and " + whereIN;
            //}
            this.dao.update(update2);
        } else {
            this.dao.update(update);
        }
    }

    /**
     * 开始分析
     * 
     * @param code
     * @param kind
     * @param start_date
     * @param end_date
     * @param analysBase
     * @return
     * @throws GeneralException
     */
    public boolean specDataAnalys(ArrayList a0100list, ArrayList nbase, String start_date, String end_date,
            String analysBase, int nbaseIndex) throws GeneralException {

        boolean isCorrect = true;
        if (a0100list == null || a0100list.size() <= 0) {
            return false;
        }

        String updateSQL = "";
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
        // 更新日期类型
        String field = this.getDateType();
        if (field.length() > 0) {
            updateSQL = "update " + fAnalyseTempTab + " set " + field + "=case when "
                    + Sql_switcher.isnull("class_id", "0") + ">0 and dkind<>3  then '工作日' when "
                    + Sql_switcher.isnull("class_id", "0")
                    + "=0 and dkind<>3 then '公休日' when dkind=3 then '节假日' end  where cur_user='"
                    + this.userView.getUserName() + "'";
        }

        String kind = "spec";

        StringBuffer specWhr = new StringBuffer();
        specWhr.append("(");
        for (int i = 0; i < a0100list.size(); i++) {
            String a0100[] = ((String) a0100list.get(i)).split("`");
            specWhr.append(" (a0100='" + a0100[1] + "' and nbase='" + a0100[0] + "') or ");
        }

        specWhr.setLength(specWhr.length() - 4);
        specWhr.append(")");

        // this.nbase_list.addAll(nbase);
        initBaseWhere(specWhr.toString(), kind);// code1 组织条件，人员权限条件，人员库条件
        this.nbase_list.addAll(nbase);
        if ("101".equals(this.analyseType)) {
            String kq_analyse_emp = "kq_analyse_emp"; // 集中处理人员表
            String kq_analyse_date = "kq_analyse_date"; // 集中处理时间

            this.pub_desT_where = " " + Sql_switcher.isnull("cur_user", "'" + this.userView.getUserName() + "'") + "='"
                    + this.userView.getUserName() + "' ";

            DbWizard dbWizard = new DbWizard(this.conn);
            if (!dbWizard.isExistTable(kq_analyse_emp, false)) {
                this.dataAnalyseUtils.analyseTableTmp(kq_analyse_emp);
            }

            if (!dbWizard.isExistTable(kq_analyse_date, false)) {
                this.dataAnalyseUtils.analysedateTableTmp(kq_analyse_date);
            }

            String date_Table = this.dataAnalyseUtils.createTimeTemp();// 建立时间临时表

            // 删除不是考勤期间的数据,kq_analyse_result只保存当前期间的数据
            deletedata();

            if ("all".equalsIgnoreCase(analysBase)) {
                /** 集中处理时间表,* */
                analyseDate(start_date, end_date, kq_analyse_date);
                /** 结束* */
                /** 集中处理 需再次处理人员表 * */
                analyseEmp(kq_analyse_emp, "", "");
                /** 结束* */
                // 先删除集中表中符合的数据，在写入
                dateleAndInsertAnalyeEmps(this.fAnalyseTempTab, date_Table, "", kind, start_date, end_date);// 删除并写入数据
            } else if ("change".equalsIgnoreCase(analysBase)) {
                // 从指定表插入（如：kq_emloy_change等保存有人员基本信息的表）
                insertEmpIntoEmp(this.fAnalyseTempTab, "kq_employ_change", date_Table, start_date, end_date);
            } else {
                return false;
            }

            this.kqItem_hash = this.dataAnalyseUtils.count_Leave();
            delDifferKqType(this.fAnalyseTempTab, start_date, end_date, "", kind);// 删掉不需要的
            insertEmployeeShiftToTmp(this.fAnalyseTempTab, "", kind, start_date, end_date);// 添加对应的考勤班次
            insertClassInfoToTmp(this.fAnalyseTempTab, "", kind, start_date, end_date);// 添加考勤班次的基本班次信息
            
            try {
                if (field.length() > 0) {
                    dao.update(updateSQL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!(this.creat_pick != null && "0".equals(this.creat_pick))) {
                // 清空现有异常数据
                this.dataAnalyseUtils.ceartFExceptCardTab(this.fExceptCardTab, this.analyseType);// 异常数据表
                otherTempTabEmps("1", "", kind, start_date, end_date);
                this.dataAnalyseUtils.createTranOverTimeTab(this.fTranOverTimeTab, this.analyseType);// 延时加班表
                otherTempTabEmps("2", "", kind, start_date, end_date);
                this.dataAnalyseUtils.createCompareBusiWithFactTab(this.fBusiCompareTab, this.analyseType);// 业务申请与实际刷卡情况表
                otherTempTabEmps("3", "", kind, start_date, end_date);
                this.dataAnalyseUtils.createCompareBusiWithFactTab(this.cardToOverTime, this.analyseType);
                otherTempTabEmps("4", "", kind, start_date, end_date);
                // //过滤正常班次
                checknormal("", kind, start_date, end_date);// 过滤正常班次

                // boolean isCorrect=true;

                if (("1".equals(this.analyseType) || "101".equals(this.analyseType))
                        && this.kqParam.getNeed_busicompare() != null && "1".equals(this.kqParam.getNeed_busicompare())) {
                    // 得到业务申请数据集
                    queryBusiData(this.fAnalyseTempTab, start_date, end_date, specWhr.toString());
                }

                // 执行数据处理存储过程
                execDataProcedureInDB();

                if ("101".equals(this.analyseType) && this.kqParam.getNeed_busicompare() != null
                        && "1".equals(this.kqParam.getNeed_busicompare()) && !isExistPro("KqCompareBusiData")) {
                    // 对比分析业务申请与实际刷卡情况
                    compareBusiWithFactCards(this.fAnalyseTempTab, start_date, end_date, specWhr.toString());
                }
                specialDisposal(this.fAnalyseTempTab, specWhr.toString(), start_date, end_date);// 按参数设置的“工作时间”来计算请假等业务
                calcFactAbsent(this.fAnalyseTempTab, specWhr.toString(), start_date, end_date);// 处理旷工和夜班
                updateRepairCardTimes(specWhr.toString(), start_date, end_date);// 处理补签到计数
                // setRest(this.fAnalyseTempTab, codewhere, start_date,
                // end_date); // 最后判断
                // 如果为休息班次并且所有的值为null；isok置为休息
                // wangyao
                // 导入考勤规则指标
                kqUtilsClass.leadingInItemToQ03(nbase_list, start_date, end_date, fAnalyseTempTab, specWhr.toString()
                        .replaceAll("nbase", fAnalyseTempTab + ".nbase")
                        .replaceAll("a0100", fAnalyseTempTab + ".a0100"));
                // 根据公式,计算考勤数据
                CountInfo countInfo = new CountInfo(userView, conn);
                countInfo.countKQInfo(start_date, end_date, fAnalyseTempTab, specWhr.toString());
            }
            /** ***********删除日期表************ */
            kqUtilsClass.dropTable(date_Table);
            String update_curUser = "update " + this.fAnalyseTempTab + " set cur_user = null where cur_user='"
                    + this.userView.getUserName() + "'";
            try {
                dao.update(update_curUser);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // this.analyseType!=101 走以前的分表处理方式 wangy
            if (nbaseIndex == 0) {
                this.dataAnalyseUtils.createDataAnalyseTmp(this.fAnalyseTempTab);// 建立临时表
            }
            String date_Table = this.dataAnalyseUtils.createTimeTemp();// 建立时间临时表
            // initializtion_date_Table(code,date_Table,start_date,end_date);//给临时时间表插入数据

            if ("all".equalsIgnoreCase(analysBase)) {
                // 从人员表插入（UsrA01...）
                insertAnalyeEmps(this.fAnalyseTempTab, date_Table, "", kind, start_date, end_date);// 给临时表插入数据
            } else if ("change".equalsIgnoreCase(analysBase)) {
                // 从指定表插入（如：kq_emloy_change等保存有人员基本信息的表）
                insertEmpIntoEmp(this.fAnalyseTempTab, "kq_employ_change", date_Table, start_date, end_date);
            } else {
                return false;
            }
            // 不处理，只给日明细添加人员信息
            this.kqItem_hash = this.dataAnalyseUtils.count_Leave();
            delDifferKqType(this.fAnalyseTempTab, start_date, end_date, specWhr.toString(), kind);// 删掉不需要的
            insertEmployeeShiftToTmp(this.fAnalyseTempTab, specWhr.toString(), kind, start_date, end_date);// 添加对应的考勤班次
            insertClassInfoToTmp(this.fAnalyseTempTab, specWhr.toString(), kind, start_date, end_date);// 添加考勤班次的基本班次信息
            
            try {
                if (field.length() > 0) {
                    dao.update(updateSQL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (!(this.creat_pick != null && "0".equals(this.creat_pick))) {

                this.dataAnalyseUtils.ceartFExceptCardTab(this.fExceptCardTab, this.analyseType);// 异常数据表
                this.dataAnalyseUtils.createTranOverTimeTab(this.fTranOverTimeTab, this.analyseType);// 延时加班表
                this.dataAnalyseUtils.createCompareBusiWithFactTab(this.fBusiCompareTab, this.analyseType);// 业务申请与实际刷卡情况表
                this.dataAnalyseUtils.createCardToOverTimeTab(this.cardToOverTime, this.analyseType);
                checknormal("", kind, start_date, end_date);// 过滤正常班次

                // boolean isCorrect=true;

                if (("1".equals(this.analyseType) || "101".equals(this.analyseType))
                        && this.kqParam.getNeed_busicompare() != null && "1".equals(this.kqParam.getNeed_busicompare())) {
                    // 得到业务申请数据集
                    queryBusiData(this.fAnalyseTempTab, start_date, end_date, specWhr.toString());
                }

                // 执行数据处理存储过程
                execDataProcedureInDB();

                if ("1".equals(this.analyseType) && this.kqParam.getNeed_busicompare() != null
                        && "1".equals(this.kqParam.getNeed_busicompare()) && !isExistPro("KqCompareBusiData")) {
                    // 对比分析业务申请与实际刷卡情况
                    compareBusiWithFactCards(this.fAnalyseTempTab, start_date, end_date, specWhr.toString());
                }
                specialDisposal(this.fAnalyseTempTab, specWhr.toString(), start_date, end_date);// 按参数设置的“工作时间”来计算请假等业务
                calcFactAbsent(this.fAnalyseTempTab, specWhr.toString(), start_date, end_date);// 处理旷工和夜班
                updateRepairCardTimes(specWhr.toString(), start_date, end_date);// 处理补签到计数
                // setRest(this.fAnalyseTempTab, codewhere, start_date,
                // end_date); // 最后判断
                // 如果为休息班次并且所有的值为null；isok置为休息
                // wangyao
                // 导入考勤规则指标
                kqUtilsClass.leadingInItemToQ03(nbase_list, start_date, end_date, fAnalyseTempTab, "");
                // 根据公式,计算考勤数据
                CountInfo countInfo = new CountInfo(userView, conn);
                countInfo.countKQInfo(start_date, end_date, fAnalyseTempTab, "");
            }
            
            /** ***********删除日期表************ */
            kqUtilsClass.dropTable(date_Table);
        }
        return isCorrect;
    }

    /**
     * 检查数据库中数据处理存储过程版本与程序要求是否匹配
     * 
     * @Title: getProcedureVerFromDB
     * @Description: 没有版本返回空，如果有版本并且低于程序要求的存储过程版本，那么提示更新存储过程
     * @return
     */
    private boolean checkProcedureVerFromDB() {
        boolean isOK = true;

        if (!isExistPro("KqGetVersion")) {
            return isOK;
        }

        String sqlCall = "{call  KqGetVersion(?)}";
        CallableStatement cstmt = null; // 存储过程
        try {
            cstmt = this.conn.prepareCall(sqlCall);
            // 用户名
            cstmt.registerOutParameter(1, java.sql.Types.VARCHAR);
            cstmt.execute();
            String ver = cstmt.getString(1);
            ver = ver == null ? "" : ver;
            isOK = KQ_PROCEDURE_VER.compareToIgnoreCase(ver) <= 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(cstmt);
        }

        return isOK;
    }

    /**
     * 执行数据处理存储过程
     * 
     * @Title: execDataProcedureInDB
     * @Description:
     * @throws GeneralException
     */
    private void execDataProcedureInDB() throws GeneralException {
        if (!checkProcedureVerFromDB()) {
            throw new GeneralException("", "数据处理存储过程版本太低，请更新后重试！", "", "");
        }

        String sqlCall = "{call  KqAnalyseData(?,?,?,?,?)}";
        CallableStatement cstmt = null; // 存储过程
        int min_overtime = this.kqParam.getMin_overtime();
        int interval = this.kqParam.getCard_interval();
        int calctime_type = Integer.parseInt(this.kqParam.getRestleave_calctime_type());
        try {
            cstmt = this.conn.prepareCall(sqlCall);
            // 0: 非机考人员 1：机考人员 100: 全部 101:集中处理
            cstmt.setInt(1, Integer.parseInt(this.analyseType));
            // 用户名
            cstmt.setString(2, this.userView.getUserName());
            // 最小加班时长
            cstmt.setInt(3, min_overtime);
            // 休息时段离岗处理规则
            cstmt.setInt(4, calctime_type);
            // 刷卡间隔
            cstmt.setInt(5, interval);
            cstmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("", "调用存储过程出错！" + e.getMessage(), "", "");
        } finally {
            KqUtilsClass.closeDBResource(cstmt);
        }
    }

    private ArrayList getBaseShiftList(String start_date, String end_date, String codewhere) {
        StringBuffer class_sql = new StringBuffer();
        HashMap map = new HashMap();
        RowSet rs = null;
        ArrayList shiftlist = new ArrayList();
        if (this.nbase_list != null && this.nbase_list.size() > 0) {
            try {
                for (int i = 0; i < this.nbase_list.size(); i++) {
                    class_sql.setLength(0);
                    String nbase = (String) this.nbase_list.get(i);
                    String whereIN = (String) this.whereInMap_forNbase.get(nbase);
                    class_sql.append("select class_id,name from kq_class where class_id in (");
                    class_sql.append("select class_id from " + this.fAnalyseTempTab + " where  q03z0>='" + start_date
                            + "' and q03z0<='" + end_date + "'");
                    if (whereIN != null && whereIN.length() > 0) {
                        if (!this.userView.isSuper_admin()) {
                            if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                                class_sql.append(" and  EXISTS(select a0100 " + whereIN + " and " + nbase
                                        + "A01.a0100=" + this.fAnalyseTempTab + ".a0100)");
                            } else {
                                class_sql.append(" and  EXISTS(select a0100 " + whereIN + " where " + nbase
                                        + "A01.a0100=" + this.fAnalyseTempTab + ".a0100)");
                            }
                        }
                    }
                    class_sql.append(")");
                    // System.out.println(update);
                    rs = this.dao.search(class_sql.toString());
                    while (rs.next()) {
                        map.put(rs.getString("class_id"), rs.getString("name"));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                KqUtilsClass.closeDBResource(rs);
            }
            for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
                Map.Entry e = (Map.Entry) it.next();
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("class_id", e.getKey().toString());
                bean.set("name", e.getValue());
                shiftlist.add(bean);
            }
        }
        return shiftlist;

    }

    public HashMap getKqItem_hash() {
        return kqItem_hash;
    }

    public void setKqItem_hash(HashMap kqItem_hash) {
        this.kqItem_hash = kqItem_hash;
    }

    private String codewhereAddTableSrc(String tablesrc, String codewhere) {
        if (codewhere == null || codewhere.length() <= 0) {
            return "";
        }
        if (tablesrc == null || tablesrc.length() <= 0) {
            return codewhere;
        }
        StringBuffer buf = new StringBuffer();
        String[] ss = codewhere.split("and");
        for (int i = 0; i < ss.length; i++) {
            buf.append(" " + tablesrc + "." + ss[i] + " and ");
        }
        buf.setLength(buf.length() - 4);
        return buf.toString();
    }

    public String getCreat_register() {
        return creat_register;
    }

    public void setCreat_register(String creat_register) {
        this.creat_register = creat_register;
    }

    public String getCardToOverTime() {
        return cardToOverTime;
    }

    public void setCardToOverTime(String cardToOverTime) {
        this.cardToOverTime = cardToOverTime;
    }

    /**
     * 得到考勤规则中的考勤指标和公式
     * 
     * @return
     * @throws GeneralException
     */

    public ArrayList getTargetList() throws GeneralException {
        StringBuffer kq_Target = new StringBuffer();
        kq_Target.append("select item_name,fielditemid,c_expr from kq_item ");
        kq_Target.append(" where " + Sql_switcher.isnull("fielditemid", "'a'") + "<>'a' order by displayorder");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSetF = null;
        ArrayList targetlist = new ArrayList();
        try {
            rowSetF = dao.search(kq_Target.toString());
            while (rowSetF.next()) {
                HashMap map = new HashMap();
                String c_expr = Sql_switcher.readMemo(rowSetF, "c_expr");
                if (c_expr != null && c_expr.length() > 0) {
                    map.put("item_name", rowSetF.getString("item_name"));
                    map.put("fielditemid", rowSetF.getString("fielditemid"));
                    map.put("c_expr", Sql_switcher.readMemo(rowSetF, "c_expr"));
                    targetlist.add(map);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rowSetF);
        }
        return targetlist;
    }

    public int getLXtype(ArrayList columnlist, String fielditemid) {
        int lxtype = YksjParser.INT;
        for (int r = 0; r < columnlist.size(); r++) {
            FieldItem fielditem = (FieldItem) columnlist.get(r);
            if (fielditemid.equalsIgnoreCase(fielditem.getItemid())) {
                if ("D".equalsIgnoreCase(fielditem.getItemtype())) {
                    lxtype = YksjParser.DATEVALUE;
                    break;
                } else if ("N".equalsIgnoreCase(fielditem.getItemtype())) {
                    if (fielditem.getDecimalwidth() > 0) {
                        lxtype = YksjParser.FLOAT;
                        break;
                    } else {
                        lxtype = YksjParser.INT;
                        break;
                    }
                } else if ("A".equalsIgnoreCase(fielditem.getItemtype())) {
                    lxtype = YksjParser.STRVALUE;
                    break;
                }
            }
        }
        return lxtype;
    }

    public void collectRegisterDataByAnalyse(String nbase, String start_date, String end_date, String kq_type,
            String kq_duration, String kq_period, String mainindex, String mainindex1, ArrayList fielditemlist,
            String statcolumnstr, String insertcolumnstr, String existsWhr, String code, String kind) {

        try {

            CollectRegister collectRegister = new CollectRegister();
            collectRegister.setConn(this.conn);
            String tablename = this.fAnalyseTempTab;

            boolean if_delete = delRecord(nbase, code, kind, tablename, kq_duration, start_date, end_date, existsWhr);
            if (if_delete) {
                collectRegister.collectRecord2(dao, nbase, start_date, end_date, code, kind, tablename, fielditemlist,
                        existsWhr, kq_duration, kq_type, insertcolumnstr, statcolumnstr, kq_period, mainindex,
                        mainindex1);
            } else {
                throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory
                        .getProperty("kq.register.collect.lost")));
            }

        } catch (GeneralException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断Q03中那些指标是从A01主集中取得的
     * 
     * @param itemtype
     * @param itemid
     * @param itemdesc
     * @return
     */
    public boolean getindexA01(String itemtype, String itemid, String itemdesc) {
        boolean field = true;
        itemtype = itemtype.toUpperCase();
        itemid = itemid.toUpperCase();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String sql = "select itemid from fielditem where fieldsetid='A01' and itemid='" + itemid + "' and itemtype='"
                + itemtype + "' and itemdesc='" + itemdesc + "'";
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String itemi = rs.getString("itemid");
                if (!"A0101".equals(itemi) && !"E0122".equals(itemi)) {
                    if (itemi != null && itemi.length() > 0) {
                        field = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return field;
    }

    /**
     * 判断Q03是否从主集中导入指标 destTab=Q03 srcTab=Q05
     * 
     * @return
     */
    public String getmainsql2() {
        String selectSQL = "";
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        sql.append("select itemid,itemtype,itemdesc from fielditem where fieldsetid='A01' and useflag='1'");
        RowSet rowSet = null;
        try {
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                ArrayList noblist = new ArrayList();
                String itemid = rowSet.getString("itemid");
                String itemtype = rowSet.getString("itemtype");
                String itemdesc = rowSet.getString("itemdesc");
                noblist.add(itemid);
                noblist.add(itemtype);
                noblist.add(itemdesc);
                list.add(noblist);
            }
            for (int i = 0; i < list.size(); i++) {
                ArrayList lists = (ArrayList) list.get(i);
                String nobitemid = (String) lists.get(0);
                String nobitemtype = (String) lists.get(1);
                String nobitemdesc = (String) lists.get(2);
                sql.setLength(0);
                sql.append("select itemid from t_hr_busifield where fieldsetid='Q03' and useflag='1' ");
                sql.append("and itemid='" + nobitemid + "' and itemtype='" + nobitemtype + "' and itemdesc='"
                        + nobitemdesc + "'");
                rowSet = dao.search(sql.toString());
                while (rowSet.next()) {
                    String itemi = rowSet.getString("itemid");
                    if (!"A0101".equals(itemi) && !"A0100".equals(itemi) && !"B0110".equals(itemi)
                            && !"E0122".equals(itemi) && !"E01A1".equals(itemi)) {
                        selectSQL += "MAX(" + itemi + "),";
                    }
                }
            }
            if (selectSQL.length() > 0) {
                selectSQL = selectSQL.substring(0, selectSQL.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return selectSQL;
    }

    public boolean delRecord(String userbase, String code, String kind, String analyseTable, String kq_duration,
            String start_date, String end_date, String whereIN) {
        boolean iscorrect = false;
        String kindField = "";
        if ("1".equals(kind)) {
            kindField = "e0122";
        } else if ("0".equals(kind)) {
            kindField = "e01a1";
        } else if ("-1".equals(kind)) {
            kindField = "a0100";
        } else {
            kindField = "b0110";
        }

        try {
            ContentDAO dao = new ContentDAO(this.conn);
            // 判断是否已经汇总过
            StringBuffer delete_kq_Sum = new StringBuffer();
            delete_kq_Sum.append("delete from Q05 where");
            delete_kq_Sum.append(" nbase='" + userbase + "'");
            // delete_kq_Sum.append(" and b0110='"+code+"'");
            delete_kq_Sum.append(" and Q03Z0='" + kq_duration + "'");
            /*
             * zxj 20140904 删除月汇总数据为什么要看有没有日明细，不在日明细里更应该删掉才对
             * delete_kq_Sum.append(" and a0100 in(select a0100 from q03");
             * delete_kq_Sum.append(" where nbase='"+userbase+"'");
             * delete_kq_Sum.append(" and Q03Z0 >= '"+start_date+"'");
             * delete_kq_Sum.append(" and Q03Z0 <= '"+end_date+"'");
             */
            // 月汇总与数据处理确认后的月汇总
            if (analyseTable == null || "".equals(analyseTable)) {
                delete_kq_Sum.append(" and " + Sql_switcher.isnull(kindField, "'#'") + " like '" + code + "%'");
            } else {
                if (!"-1".equals(kind)) {
                    delete_kq_Sum.append(" and EXISTS(select distinct a0100 from " + analyseTable);
                    delete_kq_Sum.append(" where " + analyseTable + ".a0100 = q05.a0100 and " + analyseTable
                            + ".nbase = q05.nbase");
                    delete_kq_Sum.append(" and " + analyseTable + ".nbase = '" + userbase + "'");
                    delete_kq_Sum.append(" and " + Sql_switcher.isnull(analyseTable + "." + kindField, "'#'")
                            + " like '" + code + "%')");
                    delete_kq_Sum.append(" and " + Sql_switcher.isnull(kindField, "'#'") + " like '" + code + "%'");
                }
            }
            if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1) {
                delete_kq_Sum.append(" and  EXISTS(select a0100 " + whereIN + " and " + userbase
                        + "A01.a0100=q05.a0100)");
            } else {
                delete_kq_Sum.append(" and  EXISTS(select a0100 " + whereIN + " where " + userbase
                        + "A01.a0100=q05.a0100)");
            }
            delete_kq_Sum.append(" and ").append(Sql_switcher.isnull("Q03Z5", "'01'")).append(" in ('01','07')");
            // delete_kq_Sum.append(")");

            if (analyseTable != null && analyseTable.length() > 0 && "-1".equals(kind)) {
                delete_kq_Sum.append(" and " + Sql_switcher.isnull(kindField, "'#'") + " = '" + code.substring(3)
                        + "' and nbase = '" + code.substring(0, 3) + "'");
            }

            dao.delete(delete_kq_Sum.toString(), new ArrayList());
            iscorrect = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iscorrect;
    }

    /**
     * 
     * @Title:updateRepairCardTimes
     * @Description：统计补签次数
     * @author liuyang
     * @param codeWhere
     *            本次处理所选组织机构、个人或个别处理所选人员条件
     * @param startDate
     *            本次处理所选开始日期
     * @param endDate
     *            本次处理所选结束日期
     * @throws GeneralException
     */
    private void updateRepairCardTimes(String codeWhere, String startDate, String endDate) throws GeneralException {
        String repairCardTimesFld = "";

        KqItem kqItem = new KqItem(this.conn);
        repairCardTimesFld = kqItem.getFieldIdByKqItemDesc("补刷卡次数");
        if (StringUtils.isEmpty(repairCardTimesFld)) {
            repairCardTimesFld = kqItem.getFieldIdByKqItemDesc("补签次数");

            if (StringUtils.isEmpty(repairCardTimesFld)) {
                repairCardTimesFld = kqItem.getFieldIdByKqItemDesc("忘打卡次数");
            }
        }

        if (StringUtils.isEmpty(repairCardTimesFld)) {
            return;
        }

        HashMap reSign = new HashMap();
        String codeW = codeWhere;
        try {
            if (codeWhere.indexOf("a0100") > 0 && codeWhere.indexOf("nbase") > 0) {
                codeW = codeWhere.replace("a0100", this.fAnalyseTempTab + ".a0100");
                codeW = codeW.replace("nbase", this.fAnalyseTempTab + ".nbase");
            }

            // 目标表
            String destTab = this.fAnalyseTempTab;
            // 数据源表
            StringBuffer srcTab = new StringBuffer("");
            srcTab.append(" (select nbase,a0100,work_date,count(1) cardtimes");
            srcTab.append(" from kq_originality_data A");
            srcTab.append(" where A.datafrom=1");
            srcTab.append(" and A.sp_flag='03'");
            srcTab.append(" and " + Sql_switcher.isnull("A.iscommon", "'1'") + "='1'");
            srcTab.append(" and A.work_date>='" + startDate).append("'");
            srcTab.append(" and A.work_date<='" + endDate + "'");
            srcTab.append(" and EXISTS(SELECT 1 FROM ").append(this.fAnalyseTempTab);
            srcTab.append(" WHERE ").append(this.fAnalyseTempTab).append(".nbase=A.nbase");
            srcTab.append(" AND ").append(this.fAnalyseTempTab).append(".A0100=A.A0100");
            if (this.analyseType.equals(KqConstant.AnalyseType.MACHINE_CENTRAL)) {
                srcTab.append(" AND ").append(this.pub_desT_where);
            }
            srcTab.append(")");

            srcTab.append(" group by nbase,a0100,work_date ) B ");
            // 关联条件
            StringBuffer strJoin = new StringBuffer("");
            strJoin.append(this.fAnalyseTempTab + ".nbase=B.nbase ");
            strJoin.append(" and " + this.fAnalyseTempTab + ".a0100=B.a0100 ");
            strJoin.append(" and " + this.fAnalyseTempTab + ".q03z0=B.work_date ");
            // 转换值
            String strSet = repairCardTimesFld + "=B.cardtimes ";
            // 目标表条件
            StringBuffer strDWhere = new StringBuffer("");
            strDWhere.append(" q03z0>= '" + startDate + "'");
            strDWhere.append(" and q03z0<= '" + endDate + "'");
            if ("101".equals(this.analyseType)) {
                strDWhere.append(" and " + this.pub_desT_where);
            }

            if (StringUtils.isNotEmpty(codeW)) {
                strDWhere.append(" and (").append(codeW).append(")");
            }

            // 数据源表条件
            StringBuffer strSWhere = new StringBuffer("");
            strSWhere.append(" work_date>='" + startDate);
            strSWhere.append("' and work_date<='" + endDate + "'");
            if (StringUtils.isNotEmpty(codeW)) {
                strSWhere.append(" and " + codeW.replace(this.fAnalyseTempTab + ".", "B."));
            }

            String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab.toString(), strJoin.toString(), strSet,
                    strDWhere.toString(), strSWhere.toString());
            dao.update(update);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }   
    }

    @Override
    public void run() {
        String code = (String)this.dataAnayseParams.get("code");
        String kind = (String)this.dataAnayseParams.get("kind");
        String start_date = (String)this.dataAnayseParams.get("start_date");
        String end_date = (String)this.dataAnayseParams.get("end_date");
        String analysBase = (String)this.dataAnayseParams.get("analysBase");
        try {
            this.dataAnalys(code, kind, start_date, end_date, analysBase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDataAnayseParams(HashMap dataAnayseParams) {
        this.dataAnayseParams = dataAnayseParams;
    }

    public HashMap getDataAnayseParams() {
        return dataAnayseParams;
    }

}