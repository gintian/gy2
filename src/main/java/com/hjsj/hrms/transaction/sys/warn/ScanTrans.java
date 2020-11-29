package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.attestation.zjz.SendEmailFormOA;
import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import com.hjsj.hrms.businessobject.sys.warn.ContextTools;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hjsj.weixin.utils.CommonUtil;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 检索工具，共两个个地方可能使用： 1、定时任务 2、手工确定检索 定时任务已经完成并启用 手工确定检索待完成
 * <p>
 * 业务流程： 1、取得所有预警设置 2、根据预警设置填充预警结果 3、缓存预警结果统计数字
 *
 * @author zhm
 */
public class ScanTrans implements IConstant {

    private static ScanTrans instance = null;
    private static String birthday_wid = "";
    /**
     * 扫锚时间间隔
     */
    private static float frequency = 10;
    private static boolean isSave = false;
    private static boolean isFrequency = false;

    public boolean isFrequency() {
        return isFrequency;
    }

    public static void setFrequency(boolean isFrequ) {
        isFrequency = isFrequ;
    }

    private static Logger log = LoggerFactory.getLogger(ScanTrans.class);

    //singleton creator
    private ScanTrans() {
    }

    public static ScanTrans getInstance() {
        if (instance == null) {
            instance = new ScanTrans();
        }
        return instance;
    }

    /*
     * 根据预警设置条件扫描预警结果 结果供预警标签显示
     */
    synchronized static public void doScan(int frequencytt) {
        frequency = frequencytt;
        try {   /*// 检索预警设置,统一放到warnScanPlugin处，同时控制后台作业集群冲突问题 xuj2013-7-5 update
			try
			{
				//控制预警#集群环境预警提示冲突，只能保证一台机器做预警,false|true,如果未定义或为true，则预警
				//warn_scan=true
				String warn_scan=SystemConfig.getProperty("warn_scan");
				if(warn_scan!=null&&warn_scan.equalsIgnoreCase("false"))
					return;

			}catch(Exception e)
			{}*/
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.debug("进入doScan方法。。。。。。" + sdf.format(d));
            ArrayList alConfig = queryAllConfig();
            d = new Date();
            log.debug("执行完queryAllConfig()方法。。。。。。" + sdf.format(d));
            // 生成检索预警结果
            fillWarnResult(alConfig);
        } catch (Exception e) {
            log.error("doScan:预警后台作业出错!,desc:{}", e);
            e.printStackTrace();
        }
    }

    /*
     * 检索所有的config，即所有hrpwarn表格中的记录 不包含“禁用”状态的记录
     */
    private static ArrayList queryAllConfig() {
        ArrayList arrayList = new ArrayList();
        StringBuffer sbSql = new StringBuffer();
        RecordVo vo = new RecordVo("hrpwarn");
        sbSql.append("select ");
        sbSql.append(Key_HrpWarn_Fields[0]);
        for (int i = 1; i < Key_HrpWarn_Fields.length; i++) {
            if ("norder".equalsIgnoreCase(Key_HrpWarn_Fields[i]) && (!vo.hasAttribute("norder"))) {
                continue;
            }
            sbSql.append(",");
            sbSql.append(Key_HrpWarn_Fields[i]);
        }
        sbSql.append(" from ");

        if (vo.hasAttribute("norder"))
            sbSql.append(Key_HrpWarn_Table + " where valid<>0 order by norder");
        else
            sbSql.append(Key_HrpWarn_Table + " where valid<>0 ");
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("执行查询预警sql语句:{}", sbSql.toString());
        arrayList = TransTool.executeQuerySql(sbSql.toString());
        return arrayList;
    }


    /**
     * 预警设置保存后自动检索一次
     *
     * @param dbean
     * @throws Exception synchronized
     */
    public static void warnResult(DynaBean dbean) throws Exception {
        //System.out.println("进入保存符合预警条件记录。。。。。。。");
        isSave = true;
        getBirthdayWid();
        ArrayList alBasePre = DataDictionary.getDbpreList();//取得"库前缀"
        String strTime = ContextTools.getMilliSecond().substring(11, 16);
        //判断是否需要扫描填充预警结果（hrpwarn_result）数据
        //保存和新增跳过warn_scan参数验证 guodd 2018-06-15
        if (isNeedScan(dbean, strTime)) {
            runWarn(dbean, alBasePre);
        }
    }

    /**
     * 后台线程自动运行
     * 扫描填充预警结果 清空并插入hrpwarn_result表格中
     */
    private static void fillWarnResult(ArrayList alConfig) throws Exception {
        //System.out.println("后台预警线程扫描.......................");
        //cat.debug("后台预警线程扫描.......................");
        getBirthdayWid();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.debug("执行完getBirthdayWid()方法。。。。。。" + sdf.format(d));
        ArrayList alBasePre = DataDictionary.getDbpreList();// 取得"库前缀"
        boolean isCorrect = true;
        String strTime = ContextTools.getMilliSecond().substring(11, 16);
        d = new Date();
        log.debug("当前时间" + sdf.format(d) + " VS strTime:" + strTime);
        for (int i = 0; i < alConfig.size() && alConfig != null; i++) {//遍历hrpwarn表
            if (isCorrect) {
                DynaBean dbean = (DynaBean) alConfig.get(i);//获得一个预警记录
                //isCorrect=false;
                // 1、如果是第一次启动服务，缓存为空时，存储该预警设置
                String strWid = (String) dbean.get(Key_HrpWarn_FieldName_ID);//wid字段
                if (ContextTools.getWarnConfigCache().get(strWid) == null) {
                    //当前预警记录加入缓存
                    ContextTools.getWarnConfigCache().put(strWid, dbean);
                }
                d = new Date();
                log.debug((String) dbean.get(Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]进入getWarnResult（)方法：" + sdf.format(d));
                getWarnResult(dbean, alBasePre, strTime);
            }

        }
    }

    /**
     * 预警数据保存(将预警记录符合条件的记录填充到预警结果（hrpwarn_result）表中)
     *
     * @param dbean     预警对象
     * @param alBasePre 人员库集合
     * @throws Exception
     */
    public static synchronized boolean getWarnResult(DynaBean dbean, ArrayList alBasePre, String strTime) throws Exception {

        if (isNeedScan(dbean, strTime)) {//判断是否需要扫描填充预警结果（hrpwarn_result）数据
            //System.out.println("进入预警###########");
            try {
                //控制预警#集群环境预警提示冲突，只能保证一台机器做预警,false|true,如果未定义或为true，则预警
                //warn_scan=true
                String warn_scan = SystemConfig.getPropertyValue("warn_scan");
                if (warn_scan != null && "false".equalsIgnoreCase(warn_scan))
                    return false;
                else
                    runWarn(dbean, alBasePre);

            } catch (Exception e) {
                log.error("getWarnResult:预警数据保存出错!,desc:{}", e);
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean runWarn(DynaBean dbean, ArrayList alBasePre) throws Exception {
        StringBuffer sbInsertSQL = new StringBuffer("insert into hrpwarn_result ");
        String strWid = (String) dbean.get(Key_HrpWarn_FieldName_ID);
        deleteOldWarnResult(strWid);//删除原有数据
        String valid = (String) dbean.get(Key_HrpWarn_FieldName_Valid);
        if (valid == null || valid.length() <= 0 || "0".equals(valid))//禁用是不处理
            return true;
        ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO) dbean.get(Key_HrpWarn_Ctrl_VO);// 组装xml解析器
        if (ctrlVo == null) {
            // 解析XML结果存入recordVo的虚拟字段中
            ctrlVo = new ConfigCtrlInfoVO(dbean.get(Key_HrpWarn_FieldName_CtrlInf).toString());
            dbean.set(Key_HrpWarn_Ctrl_VO, ctrlVo);
        }
        UserView uv = null;
        String warntyp = (String) dbean.get(Key_HrpWarn_FieldName_Warntyp);
        if (warntyp == null || warntyp.length() <= 0)
            warntyp = "0";//预警类型 0：人员;1：单位;2:职位；3：业务
        //System.out.println(ResourceFactory.getProperty("label.sys.warn.lastsimple")+":"+((String) dbean.get(Key_HrpWarn_FieldName_Name)+"["+(String) dbean.get(Key_HrpWarn_FieldName_ID)+"]")+" execute starting.....");
        log.debug(ResourceFactory.getProperty("label.sys.warn.lastsimple") + ":" + ((String) dbean.get(Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]") + " execute starting.....");
        if ("0".equals(warntyp)) {
            if ("1".equals(ctrlVo.getIsComplex())) { // 复杂查询

                //复杂预警规则
                String strExpress = (String) dbean.get("csource");
                //strExpress = strExpress.replaceAll("'","\"");
                if (strExpress == null || strExpress.trim().length() < 1) {
                    ;
                } else {
                    Connection conn1 = null;
                    try {
                        conn1 = AdminDb.getConnection();
                        ContentDAO dao = new ContentDAO(conn1);
                        uv = new UserView("HrpWarn", conn1);
                        uv.setUserFullName("HrpWarn");
                        uv.setUserName("HrpWarn");
                        uv.canLogin(false);
                        ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
                        /******新加人员库******/
                        DomainTool tool = new DomainTool();
                        ArrayList dblist = tool.getNbaseList(ctrlVo.getStrNbase(), dao);
                        if (dblist != null && dblist.size() > 0)
                            alBasePre = dblist;
                        for (int j = 0; j < alBasePre.size(); j++) {
                            StringBuffer insertSQL = new StringBuffer("insert into hrpwarn_result ");
                            int infoGroup = 0; // forPerson 人员
                            int varType = 8; // logic

                            YksjParser yp = new YksjParser(uv, alUsedFields,
                                    YksjParser.forSearch, varType, infoGroup, "Ht", alBasePre.get(j).toString());
                            YearMonthCount ymc = null;
                            yp.run_Where(strExpress, ymc, "", "hrpwarn_result", dao, "", conn1, "A", null);
                            String tempTableName = yp.getTempTableName();

                            ArrayList fieldsetList = yp.getUsedSets();
                            StringBuffer whereSql = new StringBuffer();
                            if (fieldsetList.size() > 0) {
                                whereSql.append("(");
                                for (int i = 0; i < fieldsetList.size(); i++) {
                                    String fieldset = (String) fieldsetList.get(i);
                                    whereSql.append("( a0100 in (select DISTINCT a0100 from ").append(alBasePre.get(j).toString()).append(fieldset).append(") ) or ");
                                }
                                whereSql.setLength(whereSql.length() - 3);
                                whereSql.append(")");
                            }

                            //System.out.println("tempTableName=" + tempTableName);

                            insertSQL.append(" (wid , nbase , a0100 ) select '");
                            insertSQL.append(strWid);
                            insertSQL.append("' , '");
                            insertSQL.append(alBasePre.get(j).toString());
                            insertSQL.append("' , a0100 from ");
                            insertSQL.append(tempTableName);

                            String w = yp.getSQL();
                            //System.out.println("w=" + w);
                            if (w == null || "".equals(w)) {
                                insertSQL.append(" where ");
                                insertSQL.append(whereSql);
                            } else {
                                insertSQL.append(" where ");
                                insertSQL.append(w);
                                insertSQL.append(" and ");
                                insertSQL.append(whereSql);
                            }
                            //System.out.println(insertSQL.toString());
                            dao.update(insertSQL.toString());
                        }

                    } catch (Exception ex) {
                        ;
                    } finally {
                        try {
                            if (conn1 != null)
                                conn1.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            } else {// 简单查询
                String strExpress = ctrlVo.getStrSimpleExpress();//简单查询公式
                if (strExpress == null || strExpress.trim().length() < 1) {
                } else {
                    Connection conn1 = null;
                    try {
                        /******新加人员库******/
                        conn1 = AdminDb.getConnection();
                        uv = new UserView("su", conn1);
                        uv.canLogin(false);
                        ContentDAO dao = new ContentDAO(conn1);
                        DomainTool tool = new DomainTool();
                        ArrayList dblist = tool.getNbaseList(ctrlVo.getStrNbase(), dao);
                        if (dblist != null && dblist.size() > 0)
                            alBasePre = dblist;
                        String strInsert = getSimpleInsertSQL(strExpress, alBasePre, strWid, ctrlVo.getStrDays(), dbean);
                        if (strInsert != null && strInsert.length() > 0) {
                            sbInsertSQL.append(strInsert);
                            TransTool.executeSql(sbInsertSQL.toString());
                        }

                        //System.out.println(sbInsertSQL.toString());

                    } catch (Exception ex) {
                        ;
                    } finally {
                        try {
                            if (conn1 != null)
                                conn1.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

            }
            /***********住总，预警发送给OA通知****************/
			/*Connection conn1=null;
			try
			{
				String oauth_warn=SystemConfig.getProperty("oauth_warn");
				String hrp_logon_url=SystemConfig.getProperty("hrp_logon_url");
				if(oauth_warn!=null&&oauth_warn.equals("true"))
				{
					conn1=AdminDb.getConnection();
					WarnSerachDomain warnSerachDomain=new WarnSerachDomain();
					ArrayList list=warnSerachDomain.getUserNames(conn1,(String)dbean.get("Obj"),ctrlVo.getStrNbase());
					Simpleclient simpleclient=new Simpleclient();
					if(list!=null&&list.size()>0)
					   simpleclient.seadWarnMsgToOAuth(conn1,list,strWid,(String)dbean.get(Key_HrpWarn_FieldName_Msg),hrp_logon_url);
					//dbean.get(Key_HrpWarn_FieldName_Msg)
				}
			}catch(Exception e)
			{}finally{
				try{
					if(conn1!=null)
						conn1.close();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}*/
            /***********住总，预警发送给OA通知,结束****************/
            Connection conn1 = null;
            try {
                /******新加人员库******/
                conn1 = AdminDb.getConnection();
                uv = new UserView("su", conn1);
                uv.canLogin(false);
                /**发送邮件*/
                if ("true".equalsIgnoreCase(ctrlVo.getIsEmail())) {
                    /** 中建接口 */
                    boolean flag = false;
                    if (SystemConfig.getPropertyValue("sso_zjz_oa_sendmail") != null && "true".equalsIgnoreCase(SystemConfig.getPropertyValue("sso_zjz_oa_sendmail")))
                        flag = true;
                    if (flag) {
                        //sendMailToZJZOA(dbean,uv);//2010.12.29,测试时给所有预警对象“总部”全都发送了，进沟通先不要这个，只要发送到每个人的
                    } else
                        SendMail(dbean);
                    //SendMail(dbean);

                    //邮件发送给个人
                    sendEveryOneEmail(dbean, alBasePre, uv);
                }
                /**发短信*/
                if ("true".equalsIgnoreCase(ctrlVo.getIsMobile())) {
                    sendEverySms(dbean, alBasePre, uv);
                    SendSms(dbean);
                }
                if (birthday_wid != null && birthday_wid.equalsIgnoreCase(strWid)) {
                    sendEveryOneBD(dbean, alBasePre, uv);
                }

                //微信通知
                if ("true".equalsIgnoreCase(ctrlVo.getIsWeixin())) {
                    //通知到预警对象
                    SendWeixin(dbean);
                    //通知到本人
                    sendEveryWeixinMsg(dbean, alBasePre, uv, birthday_wid);

                }
                //钉钉通知
                if ("true".equalsIgnoreCase(ctrlVo.getIsDingtalk())) {
                    //通知到预警对象
                    SendDingDIng(dbean, alBasePre);
                    //通知到本人
                    sendEveryDDMsg(dbean, alBasePre, uv, birthday_wid);

                }
                //System.out.println(sbInsertSQL.toString());

            } catch (Exception ex) {
                ;
            } finally {
                try {
                    if (conn1 != null)
                        conn1.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } else if ("1".equals(warntyp)) {
            if ("1".equals(ctrlVo.getIsComplex())) { // 复杂查询

                //复杂预警规则
                String strExpress = (String) dbean.get("csource");
                //strExpress = strExpress.replaceAll("'","\"");
                if (strExpress == null || strExpress.trim().length() < 1) {
                    ;
                } else {
                    Connection conn1 = null;
                    try {
                        conn1 = AdminDb.getConnection();
                        ContentDAO dao = new ContentDAO(conn1);
                        uv = new UserView("HrpWarn", conn1);
                        uv.setUserFullName("HrpWarn");
                        uv.setUserName("HrpWarn");
                        uv.canLogin(false);
                        ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
                        /******新加人员库******/
                        DomainTool tool = new DomainTool();

                        StringBuffer insertSQL = new StringBuffer("insert into hrpwarn_result ");
                        int varType = 8; // logic
                        YksjParser yp = new YksjParser(uv, alUsedFields,
                                YksjParser.forSearch, varType, YksjParser.forUnit, "Ht", "");
                        YearMonthCount ymc = null;
                        yp.run_Where(strExpress, ymc, "", "hrpwarn_result", dao, "", conn1, "A", null);
                        String tempTableName = yp.getTempTableName();
                        insertSQL.append(" (wid , nbase , a0100 ) select '");
                        insertSQL.append(strWid);
                        insertSQL.append("' , 'UN'");
                        insertSQL.append(" , b0110 from ");
                        insertSQL.append(tempTableName);
                        String w = yp.getSQL();
                        if (w == null || "".equals(w)) {
                        } else {
                            insertSQL.append(" where ");
                            insertSQL.append(w);
                        }
                        //System.out.println(insertSQL.toString());
                        dao.update(insertSQL.toString());


                    } catch (Exception ex) {
                        ;
                    } finally {
                        try {
                            if (conn1 != null)
                                conn1.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            }
        } else if ("2".equals(warntyp)) {
            if ("1".equals(ctrlVo.getIsComplex())) { // 复杂查询

                //复杂预警规则
                String strExpress = (String) dbean.get("csource");
                //strExpress = strExpress.replaceAll("'","\"");
                if (strExpress == null || strExpress.trim().length() < 1) {
                    ;
                } else {
                    Connection conn1 = null;
                    try {
                        conn1 = AdminDb.getConnection();
                        ContentDAO dao = new ContentDAO(conn1);
                        uv = new UserView("HrpWarn", conn1);
                        uv.setUserFullName("HrpWarn");
                        uv.setUserName("HrpWarn");
                        uv.canLogin(false);
                        ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
                        /******新加人员库******/
                        DomainTool tool = new DomainTool();

                        StringBuffer insertSQL = new StringBuffer("insert into hrpwarn_result ");
                        int varType = 8; // logic
                        YksjParser yp = new YksjParser(uv, alUsedFields,
                                YksjParser.forSearch, varType, YksjParser.forPosition, "Ht", "");
                        YearMonthCount ymc = null;
                        yp.run_Where(strExpress, ymc, "", "hrpwarn_result", dao, "", conn1, "A", null);
                        String tempTableName = yp.getTempTableName();
                        insertSQL.append(" (wid , nbase , a0100 ) select '");
                        insertSQL.append(strWid);
                        insertSQL.append("' , '#@K'");
                        insertSQL.append(" , e01a1 from ");
                        insertSQL.append(tempTableName);
                        String w = yp.getSQL();
                        if (w == null || "".equals(w)) {
                        } else {
                            insertSQL.append(" where ");
                            insertSQL.append(w);
                        }
                        //System.out.println(insertSQL.toString());
                        dao.update(insertSQL.toString());


                    } catch (Exception ex) {
                        ;
                    } finally {
                        try {
                            if (conn1 != null)
                                conn1.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            }
        } else if ("3".equals(warntyp))//业务预警
        {
            if ("1".equals(ctrlVo.getIsComplex())) { // 复杂查询

                //复杂预警规则
                String strExpress = (String) dbean.get("csource");
                if (strExpress == null || strExpress.trim().length() < 1) {
                    ;
                } else {
                    Connection conn1 = null;
                    try {
                        conn1 = AdminDb.getConnection();
                        ContentDAO dao = new ContentDAO(conn1);

                        uv = new UserView("HrpWarn", conn1);
                        uv.setUserFullName("HrpWarn");
                        uv.setUserName("HrpWarn");
                        uv.canLogin(false);

                        String setid = (String) dbean.get("setid");
                        String discSetId = setid;

                        //zhaoxj 员工月汇总与员工日明细用的同一套业务字典
                        if ("Q05".equalsIgnoreCase(setid))
                            discSetId = "Q03";

                        ArrayList alUsedFields = DataDictionary.getFieldList(discSetId, Constant.USED_FIELD_SET);

                        int varType = 8; // logic
                        YksjParser yp = new YksjParser(uv, alUsedFields,
                                YksjParser.forNormal, varType, YksjParser.forPerson, "Ht", "");
                        //yp.setStdTmpTable(setid);
                        yp.setCon(conn1);
                        yp.run(strExpress);
                        String FSQL = yp.getSQL();

                        if (!"q03".equalsIgnoreCase(setid) && !"q05".equalsIgnoreCase(setid))
                            sendTranWarnEmail(FSQL, setid, dbean, dao);
                        else
                            sendKqQ03Q05WarnEmail(FSQL, setid, dbean, dao, uv, conn1, yp.getMapUsedFieldItems());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            if (conn1 != null)
                                conn1.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            }
        }
        log.debug(ResourceFactory.getProperty("label.sys.warn.lastsimple") + ":" + ((String) dbean.get(Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]") + " execute end.....");
        //System.out.println(ResourceFactory.getProperty("label.sys.warn.lastsimple")+":"+((String) dbean.get(Key_HrpWarn_FieldName_Name)+"["+(String) dbean.get(Key_HrpWarn_FieldName_ID)+"]")+" execute end.....");
        return true;
    }

    /**
     * 删除原有的预警数据
     *
     * @param wid
     */
    public static void deleteOldWarnResult(String wid) {
        if (wid == null || "".equals(wid)) {
        } else {
            String sql = "delete from hrpwarn_result where wid=" + wid;

            //System.out.println(sql);

            Connection conn3 = null;
            try {
                conn3 = AdminDb.getConnection();
                ContentDAO dao = new ContentDAO(conn3);
                dao.delete(sql, new ArrayList());
            } catch (Exception ex) {
                ;
            } finally {
                try {
                    if (conn3 != null)
                        conn3.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * @param strXmlExpress 简单公式
     * @param alBasePre     库前缀列表
     * @param strWid        预警序号
     * @param strDays       提前多少天告警,对简单的预警起作用
     * @param dbean         当前预警设置对象DyanBean
     * @return
     */
    private static String getSimpleInsertSQL(String strXmlExpress,
                                             ArrayList alBasePre, String strWid, String strDays, DynaBean dbean) {
        if (strXmlExpress == null || strXmlExpress.trim().length() < 1)
            return null;
        StringBuffer sbTemp = new StringBuffer();
        // 使用数据库结果，生成的SQL原形如下所示：
        // insert hrpwarn_result select '1' as 'wid', 'Usr' as nbase, a0100 from
        // UsrA01 union select '1' as 'wid', 'Ret' as nbase, a0100 from RetA01

        //公式表达式
        String strExpression = strXmlExpress.substring(0, strXmlExpress.indexOf("|"));
        //公式指标信息
        String strFactor = strXmlExpress.substring(strXmlExpress.indexOf("|") + 1);

        /**取得以前设置的截止日期*/
        String preDate = ConstantParamter.getAppdate("su");

        try {
            // 如果需要扫描，说明当前日期就是提前了xx天的日期，所以实际要比较的数据库中天数是xx天后
            long lMillis = System.currentTimeMillis();
            lMillis += 1000 * 60 * 60 * 24 * Integer.parseInt((strDays == null || strDays.length() == 0 ? "0" : strDays));// 提前xx天
            Date date = new Date(lMillis);
            SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd");
            String strDayBefore = SDF.format(date).toString();

            /**设置截止时间*/
            ConstantParamter.putAppdate("su", strDayBefore);

            for (int j = 0; j < alBasePre.size(); j++) {
                // String strPre = (String)((DynaBean)alBasePre.get(j)).get("pre");
                String strPre = (String) alBasePre.get(j);
                sbTemp.append("select '" + strWid + "' as wid," + "'" + strPre + "' as nbase, ");
                sbTemp.append(strPre);
                sbTemp.append("a01.a0100 ");
                try {
                    // 保存Fieldlist给WarnResultTag中的
                    // userview.getPrivSQLExpression参数使用
                    FactorList factor = new FactorList(strExpression, strFactor,
                            strPre, false, false, true, 1, "su");
                    ArrayList fieldlist = factor.getFieldList();
                    dbean.set(Key_HrpWarn_Condition_FieldList, fieldlist);

                    String strSql = factor.getSqlExpression();
                    sbTemp.append(strSql + " UNION ");

                    // ContextTools.log(strSql);
                } catch (GeneralException e) {
                    e.printStackTrace();
                }
            }
            sbTemp.setLength(sbTemp.length() - 7); // 删除最后的" UNION "
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ConstantParamter.putAppdate("su", preDate);
        }
        return sbTemp.toString();
    }

    /*
     * 判断两个预警设置对象内容是否相同
     */
    private static boolean isEqualWarnDynaBean(Object src, Object dest) {
        boolean bReturn = true;
        if (src instanceof DynaBean && dest instanceof DynaBean) {
            DynaBean dbeanSrc = (DynaBean) src;
            DynaBean dbeanDest = (DynaBean) dest;
            RecordVo vo = new RecordVo("hrpwarn");
            for (int i = 1; i < Key_HrpWarn_Fields.length; i++) {
                /**hrpwarn数据库表升级norder,解决第一次启动时报错,chenmengqing changed at 20111203*/
                if ("norder".equalsIgnoreCase(Key_HrpWarn_Fields[i]) && (!vo.hasAttribute("norder")))
                    continue;
                String srcstr = (String) dbeanSrc.get(Key_HrpWarn_Fields[i]);
                String deststr = (String) dbeanDest.get(Key_HrpWarn_Fields[i]);
                srcstr = PubFunc.keyWord_reback(srcstr.replaceAll("\n", "").replaceAll("\r", ""));
                deststr = PubFunc.keyWord_reback(deststr.replaceAll("\n", "").replaceAll("\r", ""));
                if (srcstr.equals(deststr)) {
                    ;
                } else {
                    //System.out.println(Key_HrpWarn_Fields[i]+" "+srcstr);
                    //System.out.println(Key_HrpWarn_Fields[i]+" "+deststr);
                    bReturn = false;
                    break;
                }
            }
        } else {
            bReturn = false;
        }
        return bReturn;
    }

    /*
     * 判断是否需要扫描填充预警结果（hrpwarn_result）数据
     * 1、是否刚启动服务 ?true:false
     * 2、是否预警内容发生变动?true:false
     * 3、是否提前xx天的凌晨00:00:00 - 00:10:00之间 ?true:false 其他返回false
     */
    private static boolean isNeedScan(DynaBean dbean, String strTime) {

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.debug((String) dbean.get(Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]进入getWarnResult（)方法：" + sdf.format(d));

        // 1、新增预警情况
        String strWid = (String) dbean.get(Key_HrpWarn_FieldName_ID);//wid字段
        if (ContextTools.getWarnConfigCache().get(strWid) == null) {
            //当前预警记录加入缓存
            ContextTools.getWarnConfigCache().put(strWid, dbean);
            d = new Date();
            log.debug((String) dbean.get("新增加预警" + Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]进入getWarnResult（)方法：" + sdf.format(d));
            return true;
        }

        // 2、如果发生变更，启动扫描条件为真,条件未变，但内容发生了变生（比如预警设置发生改变时）
        if (!isEqualWarnDynaBean(ContextTools.getWarnConfigCache().get(strWid), dbean)) {
            //更新缓存，下一次定时任务比较使用之
            ContextTools.getWarnConfigCache().put(strWid, dbean);
            //System.out.println("equal=true");
            d = new Date();
            log.debug((String) dbean.get("修改预警" + Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]进入getWarnResult（)方法：" + sdf.format(d));
            return true;
        }

        // 是否提前xx天的凌晨？
        boolean bReturn = false;

        //获取动态Bean中存放的预警控制对象（封装了hrpwarn表中的warn_ctrl字段XML信息）
        ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO) dbean.get(Key_HrpWarn_Ctrl_VO);
        if (ctrlVo == null) {
            //将当前动态Bean中的warn_ctrl字段XML信息封装为预警控制对象
            ctrlVo = new ConfigCtrlInfoVO((String) dbean.get(Key_HrpWarn_FieldName_CtrlInf));
            //将预警对象放入当前动态Bean中
            dbean.set(Key_HrpWarn_Ctrl_VO, ctrlVo);
        }

        String strFreqType = ctrlVo.getStrFreqType();//预警类型（月/周/天）
        String strFreqValue = ctrlVo.getStrFreqValue();//值（1-31 1-7 1-24）

        if (strFreqValue == null || strFreqValue.trim().length() < 1) {
            strFreqValue = "1";
        }
		/*if(isFrequency){
			return true;
		}*/
        /**定义在早晨七点三十分钟执行,对每月或每周生效*/
        float f_s = Float.parseFloat("7.3");//定义为7点30分
        float f_e = 0;//系统当前时间
        float diff = 0;
        float tmp = frequency / 100;

        //获得系统当前时间的小时与分钟
        //String strTime = ContextTools.getMilliSecond().substring(11, 16);
        f_e = Float.parseFloat(strTime.replaceAll(":", ".")/*replace(":",".")*/);
        d = new Date();
        log.debug(((String) dbean.get(Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]->") + strFreqType + "-->" + sdf.format(d));
        if ("0".equals(strFreqType)) { // 每月xx日
            Calendar cal = Calendar.getInstance();
            int iLastDayofMonth = cal.get(Calendar.DAY_OF_MONTH);// 获得当前月当前日是几号
            int iFreqValue = Integer.parseInt(strFreqValue);//预警控制中设置的日期
            if (iLastDayofMonth == iFreqValue) {//当前与设置的相同
				/*diff=Math.abs(f_e-f_s); //（当前设置为7:20--7:40范围）
				if(diff<=tmp){
					bReturn=true;
					//System.out.println("每月time=true");
				}*/
                diff = f_e - f_s;
                if (diff > ((tmp / 2) * -1) && diff <= (tmp / 2)) {
                    bReturn = true;
                    //System.out.println("每天time=true");
                    log.debug(ResourceFactory.getProperty("label.sys.warn.lastsimple") + ":" + ((String) dbean.get(Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]->每月") + iLastDayofMonth + "，当前时间(对比7：30)：" + strTime + ",时间差" + diff + ",周期" + tmp + "(" + frequency + ")" + " execute will start");
                }
            }
        } else if ("1".equals(strFreqType)) { // 每周星期x
            if (strFreqValue.trim().length() < 1)
                strFreqValue = "1";
            GregorianCalendar c = new GregorianCalendar();
            int iDayInWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
            if (Integer.parseInt(strFreqValue) == iDayInWeek) {
				/*diff=Math.abs(f_e-f_s);//（当前设置为7:20--7:40范围）
				if(diff<=tmp){
					bReturn=true;
					//System.out.println("每周time=true");
				}*/
                diff = f_e - f_s;
                if (diff > ((tmp / 2) * -1) && diff <= (tmp / 2)) {
                    bReturn = true;
                    //System.out.println("每天time=true");
                    log.debug(ResourceFactory.getProperty("label.sys.warn.lastsimple") + ":" + ((String) dbean.get(Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]->每周") + iDayInWeek + "，当前时间(对比7：30)：" + strTime + ",时间差" + diff + ",周期" + tmp + "(" + frequency + ")" + " execute will start");
                }
            }
        } else if ("2".equals(strFreqType)) { // 每天 xx:xx
            Date work_time = DateUtils.getDate(strFreqValue, "HH:mm");
            Date now_time = DateUtils.getDate(strTime, "HH:mm");
            d = new Date();
            log.debug((String) dbean.get(Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]进入getPartMinute（)方法：" + sdf.format(d));
            diff = getPartMinute(work_time, now_time);
            d = new Date();
            log.debug((String) dbean.get(Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]完成getPartMinute（)方法：" + sdf.format(d));

            //System.out.println(strFreqValue+"---"+strTime+"---"+diff+"---"+frequency);
            if (diff > ((frequency / 2) * -1) && diff <= (frequency / 2)) {
                bReturn = true;
                //System.out.println("每天time=true");
                log.debug(ResourceFactory.getProperty("label.sys.warn.lastsimple") + ":" + ((String) dbean.get(Key_HrpWarn_FieldName_Name) + "[" + (String) dbean.get(Key_HrpWarn_FieldName_ID) + "]->每天") + strFreqValue + "，当前时间：" + strTime + ",时间差" + diff + ",周期" + frequency + " execute will start");
            }
			/*f_s=Float.parseFloat(strFreqValue.replaceAll(":","."));//设置时间
			diff=Math.abs(f_e-f_s);//当前时间的前后10分钟
			//float tmp=(this.frequency/100);
			if(diff<=tmp)
				bReturn=true;*/
        }
        return bReturn;
    }

    private static void SendSms(DynaBean db) {
        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) db.get(Key_HrpWarn_Ctrl_VO);
        ArrayList alUserIDList = getUserIDList(vo.getStrDomain());
        String nameInfo = "";
        if (alUserIDList != null && alUserIDList.size() > 0) {
            Connection conn = null;
            String sms_content_T = "t_sys_smsbox";
            String hrpwarn_T = "hrpwarn_result";
            String cur_time = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
            String sendspace = "1";
            RowSet rs = null;
            try {
                String strWid = (String) db.get(Key_HrpWarn_FieldName_ID);// 获取预警对象用户ID
                String strFreqType = vo.getStrFreqType();//预警类型（月/周/天）
                String time_where = "";
                if ("0".equals(strFreqType)) { // 每月xx日
                    sendspace = "30";
                    if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                        time_where = " and  " + Sql_switcher.diffDays(Sql_switcher.dateValue(cur_time), "send_time") + "<" + (Integer.parseInt(sendspace)) + "";
                    else
                        time_where = " and  " + Sql_switcher.diffDays("'" + cur_time + "'", "send_time") + "<" + (Integer.parseInt(sendspace)) + "";
                } else if ("1".equals(strFreqType)) { // 每周星期x
                    sendspace = "7";
                    if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                        time_where = " and  " + Sql_switcher.diffDays(Sql_switcher.dateValue(cur_time), "send_time") + "<" + (Integer.parseInt(sendspace)) + "";
                    else
                        time_where = " and  " + Sql_switcher.diffDays("'" + cur_time + "'", "send_time") + "<" + (Integer.parseInt(sendspace)) + "";
                } else if ("2".equals(strFreqType)) { // 每天 xx:xx
                    sendspace = "1";
                    if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                        time_where = " and  " + Sql_switcher.diffDays(Sql_switcher.dateValue(cur_time), "send_time") + "<" + (Integer.parseInt(sendspace)) + "";
                    else
                        time_where = " and  " + Sql_switcher.diffDays("'" + cur_time + "'", "send_time") + "<" + (Integer.parseInt(sendspace)) + "";
                }

                conn = AdminDb.getConnection();
                ContentDAO dao = new ContentDAO(conn);
                ArrayList msglist = new ArrayList();
                StringBuffer sql = new StringBuffer();
                for (int i = 0; i < alUserIDList.size(); i++) {
                    DynaBean dBean = (LazyDynaBean) alUserIDList.get(i);
                    String receiver = (String) (dBean.get("userfullname") == null ? "" : dBean.get("userfullname"));
                    String username = (String) (dBean.get("username") == null ? "" : dBean.get("username"));
                    String phone = (String) (dBean.get("phone") == null ? "" : dBean.get("phone"));
                    if ("".equals(phone.trim()))
                        continue;

                    sql.setLength(0);
                    sql.append(" select 1");
                    sql.append(" from " + sms_content_T + " h where username='" + username + "' and wid='" + strWid + "' and sender='su'");//sender='su'解决接收人即是预警条件又是预警对象时只接收到了作为预警条件的短信
                    rs = dao.search(sql.toString());
                    if (rs.next()) {
                        sql.append(time_where);
                        rs.close();
                        rs = dao.search(sql.toString());
                        if (rs.next()) {
                            continue;
                        }
                    }

                    UserView userView = new UserView(username, conn);
                    /** 不检查口令，仅检查用户名 */
                    if (userView.canLogin(false)) {
                        ScanTotal st = new ScanTotal(userView);
                        int iTotal = st.getCount(db);
                        if (iTotal > 0) {// 有实际人数的时候才发送消息？
                            //if(nameInfo==null||nameInfo.length()<=0){
                            //nameInfo= getHrpwarn_resultUsernanme(strWid,dao);//21646 取消设置nameInfo是否为空判断
                            nameInfo = st.getHrpwarn_resultUsernanme(db, dao);
                            //}
                            String msg = ResourceFactory.getProperty("label.sys.warn.smsclew") + ": " + db.get(Key_HrpWarn_FieldName_Msg) + " " + nameInfo + " (计" + iTotal + "人)";
                            LazyDynaBean mBean = new LazyDynaBean();
                            mBean.set("sender", "su");
                            mBean.set("receiver", receiver);
                            mBean.set("phone_num", phone);
                            mBean.set("msg", msg);
                            mBean.set("wid", strWid);//预警编号
                            mBean.set("username", username);//登陆名称
                            mBean.set("templateId", vo.getStrNote());
                            //System.out.println(sql.toString());
                            //System.out.println(receiver);
                            msglist.add(mBean);
                        }
                    }
                }
                SmsBo smsbo = new SmsBo(conn);
                smsbo.batchSendMessage(msglist);

            } catch (Exception e) {
                e.printStackTrace();
                //throw GeneralExceptionHandler.Handle(e);
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                    if (conn != null)
                        conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void SendMail(DynaBean db) {

        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) db.get(Key_HrpWarn_Ctrl_VO);
        // 获取预警对象用户ID
        ArrayList alUserIDList = getUserIDList(vo.getStrDomain());
        //用于存储已发送用户的dbpre+a0100，屏蔽同一人拥有多个预警对象角色导致重复接收邮件问题 2014-02-14
        HashMap sendedPerson = new HashMap();
        if (alUserIDList != null && alUserIDList.size() > 0) {
            Connection conn = null;
            try {
                conn = AdminDb.getConnection();
                UserObjectBo user_bo = new UserObjectBo(conn);
                EMailBo mailbo = new EMailBo(conn, true, "");
                /**取系统管理员的电子信箱*/
                String src_addr = mailbo.getSAddr();//user_bo.getEmailAddress("su","");
                if (src_addr == null || "".equals(src_addr.trim()))
                    return;
                for (int i = 0; i < alUserIDList.size(); i++) {
                    DynaBean dBean = (LazyDynaBean) alUserIDList.get(i);
                    String a0100 = ((String) dBean.get("a0100")).toUpperCase();
                    if (sendedPerson.containsKey(a0100)) {
                        continue;
                    } else {
                        sendedPerson.put(a0100, null);
                    }
                    String username = (String) (dBean.get("username") == null ? "" : dBean.get("username"));
                    //String a0100=(String)(dBean.get("a0100")==null?"":dBean.get("a0100"));
                    //a0100=a0100.substring(3);
                    String email_address = (String) (dBean.get("email") == null ? "" : dBean.get("email"));
                    if ("".equals(email_address.trim()))
                        continue;
                    UserView userView = new UserView(username, conn);
                    /** 不检查口令，仅检查用户名 */
                    if (userView.canLogin(false)) {
                        ScanTotal st = new ScanTotal(userView);
                        StringBuffer names = new StringBuffer();
                        //int iTotal = st.getCount(db);
                        int iTotal = st.getCount(db, names, true);
                        if (iTotal > 0) {// 有实际人数的时候才发送消息？
                            //String strEmail = ResourceFactory.getProperty("warn.email.msg").replace("{0}", ((String)dBean.get("userfullname"))+((String)dBean.get("a0107"))).replace("{1}", db.get(Key_HrpWarn_FieldName_Msg) + "(共计"+ iTotal + "人)").replace("{2}", names.toString());
                            String strEmail = ResourceFactory.getProperty("warn.email.msg.html").replace("{0}", ((String) db.get(Key_HrpWarn_FieldName_Name))).replace("{1}", ((String) dBean.get("userfullname")) + ((String) dBean.get("a0107"))).replace("{2}", db.get(Key_HrpWarn_FieldName_Msg) + "(共计" + iTotal + "人)").replace("{3}", names.toString()).replace("{4}", PubFunc.FormatDate(new Date(), "yyyy-MM-dd"));
                            //zxj 预警有自己的格式模板，不适用邮件发送类提供的固定模板
                            mailbo.setUseTemplate(false);
                            mailbo.sendEmail((String) db.get(Key_HrpWarn_FieldName_Name), strEmail, "", src_addr, email_address);
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                //throw GeneralExceptionHandler.Handle(e);
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 取得预警对象的人员ID列表 预警对象为部门时，默认包含下级部门 所以检索部门中的人员时使用
     * like 查询（下级部门id包含上级id，并扩展两位）
     *
     * @param strDomain
     * @return
     */
    private static ArrayList getUserIDList(String strDomain) {
        Connection conn = null;
        ArrayList userlist = new ArrayList();
        try {
            conn = AdminDb.getConnection();
            UserObjectBo user_bo = new UserObjectBo(conn);
            if (strDomain == null || strDomain.trim().length() < 3) {// length<3?因为“RL”“UN”等情况存在

            } else if (strDomain.startsWith("RL") || strDomain.contains(",RL")) {//zxj 20141031 有逗号开头的情况
                strDomain = strDomain.replaceAll("RL", "");
                String[] roles = strDomain.split(",");

                for (int i = 0; i < roles.length; i++) {
                    if ("".equals(roles[i]))
                        continue;

                    userlist.addAll(user_bo.findUserListByRoleId(roles[i]));
                }
            } else {
                //strDomain = strDomain.replaceAll("UN", "");
                //strDomain = strDomain.replaceAll("UM", "");
                //strDomain = strDomain.replaceAll("@K", "");
                String[] strOrgs = strDomain.split(",");
                for (int i = 0; i < strOrgs.length; i++) {
                    if ("".equals(strOrgs[i]))
                        continue;

                    String codesetid = strOrgs[i].substring(0, 2);
                    String codeitemid = strOrgs[i].substring(2);
                    userlist.addAll(user_bo.findUserListByOrgId(codeitemid, codesetid));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception ee) {

            }
        }
        return userlist;
    }


    public void countExc_p(Connection conn, UserView userView, ArrayList alUsedFields, String nbase, String exc_p, String whl) {
        if (exc_p == null || exc_p.length() <= 0)
            return;
        int infoGroup = 0; // forPerson 人员
        int varType = 6; // float
        YearMonthCount ycm = null;
        ContentDAO dao = new ContentDAO(conn);
        YksjParser yp = new YksjParser(userView, alUsedFields, YksjParser.forSearch, varType, infoGroup, "Ht", nbase);
        yp.run(exc_p, ycm, "a0100", "hrpwarn_result", dao,
                whl, conn, "A", 50, 2, null);
    }

    /**
     * 发送邮件
     *
     * @param dbean
     * @param alBasePre
     * @param userView
     */
    public static void sendEveryOneEmail(DynaBean dbean, ArrayList alBasePre, UserView userView) {
        String strWid = (String) dbean.get(Key_HrpWarn_FieldName_ID);
        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) dbean.get(Key_HrpWarn_Ctrl_VO);
        if (vo.getStrEveryone() == null || !"true".equalsIgnoreCase(vo.getStrEveryone()))
            return;
        String templateId = vo.getStrNote();
        if (templateId == null || templateId.length() <= 0)
            return;
    	/*if(1==1)
    		return;*/
        String sendspace = vo.getStrSendspace();
        if (sendspace == null || sendspace.length() <= 0)
            sendspace = "7";
        if (Integer.parseInt(sendspace) < 1)
            sendspace = "1";
        String email_content_T = "email_content";
        String hrpwarn_T = "hrpwarn_result";
        StringBuffer sql = new StringBuffer();
        String cur_time = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
        Connection conn = null;
        StringBuffer buf = new StringBuffer();
        String z1 = cur_time.substring(0, 10) + " 00:00:00";
        String z3 = cur_time.substring(0, 10) + " 23:59:59";
        buf.append(" and (send_time>=" + Sql_switcher.dateValue(z1));
        buf.append(" and send_time<=" + Sql_switcher.dateValue(z3) + ")");

        RowSet rs = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            EmailTemplateBo bo = new EmailTemplateBo(conn);
            ArrayList personList = new ArrayList();
            switch (Sql_switcher.searchDbServer()) {
                case Constant.ORACEL: {
                    cur_time = "to_date('" + cur_time + "','yyyy-mm-dd  hh24:mi:ss')";
                    break;
                }
            }
            for (int i = 0; i < alBasePre.size(); i++) {
                String strPre = (String) alBasePre.get(i);
                sql = new StringBuffer();
        		/*sql.append(" select '"+strPre+"',a0100");
        		sql.append(" from "+hrpwarn_T+" h where h.nbase='"+strPre+"' and h.wid="+strWid+"");
        		sql.append(" and a0100 in(select a0100 from "+email_content_T+" E where pre='"+strPre+"' and E.wid='"+strWid+"'");
        		sql.append(" and "+Sql_switcher.diffDays("'"+cur_time+"'","send_time")+">="+(Integer.parseInt(sendspace))+"");//sendspace==0的话就无限发送了
        		sql.append(")"); */
                sql.append(" select '" + strPre + "',a0100");
                sql.append(" from " + hrpwarn_T + " h where h.nbase='" + strPre + "' and h.wid=" + strWid + "");
                sql.append(" and a0100 in(select a0100 from (");
                sql.append(" select a0100 ,max(send_time) as s_time from email_content E");
                sql.append(" where pre='" + strPre + "' and E.wid='" + strWid + "' group by a0100) aa");
                if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                    sql.append(" where  " + Sql_switcher.diffDays("" + cur_time + "", "s_time") + ">" + (Integer.parseInt(sendspace)) + "");
                else
                    sql.append(" where  " + Sql_switcher.diffDays("'" + cur_time + "'", "s_time") + ">" + (Integer.parseInt(sendspace)) + "");

                sql.append(")");
                //System.out.println("-------"+sql.toString());
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    personList.add(strPre + rs.getString("a0100"));
                }
                sql.delete(0, sql.length());
                sql.append(" select '" + strPre + "',a0100");
                sql.append(" from " + hrpwarn_T + " h where h.nbase='" + strPre + "' and h.wid=" + strWid + "");
                sql.append(" and a0100 not in(select a0100 from " + email_content_T + " E where pre='" + strPre + "' and E.wid='" + strWid + "'");
                sql.append(" " + buf.toString());
                sql.append(")");
                //System.out.println("-------"+sql.toString());
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    personList.add(strPre + rs.getString("a0100"));
                }
            }
            boolean flag = false;
            if (SystemConfig.getPropertyValue("sso_zjz_oa_sendmail") != null && "true".equalsIgnoreCase(SystemConfig.getPropertyValue("sso_zjz_oa_sendmail")))
                flag = true;
            if (flag) {
                warnZJZOAInfomation(strWid, personList, templateId, userView, true);
            } else
                bo.warnExportInfomation(strWid, personList, templateId, userView, true);
    		/*if(birthday_wid!=null&&birthday_wid.equalsIgnoreCase(strWid))
    		{
    			sendMessToAppoint_news(strWid,personList,userView,sendspace);//生日提醒
    		}*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ee) {

            }
        }

    }

    /**
     * 对每个预警结果人发送短信
     *
     * @param dbean
     * @param alBasePre
     * @param userView
     * @throws Exception
     */
    public static void sendEverySms(DynaBean dbean, ArrayList alBasePre, UserView userView) throws Exception {
        String strWid = (String) dbean.get(Key_HrpWarn_FieldName_ID);
        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) dbean.get(Key_HrpWarn_Ctrl_VO);
        if (vo.getStrEveryone() == null || !"true".equalsIgnoreCase(vo.getStrEveryone()))
            return;
        String templateId = vo.getStrNote();
        if (templateId == null || templateId.length() <= 0)
            return;
        String sendspace = vo.getStrSendspace();
        if (sendspace == null || sendspace.length() <= 0)
            sendspace = "7";
        String sms_content_T = "t_sys_smsbox";
        String hrpwarn_T = "hrpwarn_result";
        StringBuffer sql = new StringBuffer();
        String cur_time = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
        Connection conn = null;
        RowSet rs = null;
        //RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        String loadname = "username";
//    	if(login_vo!=null)
//    	{
//    		String login_name = login_vo.getString("str_value");
//    		if(login_name!=null&&login_name.length()>0)
//    		{
//    			int idx=login_name.indexOf(",");
//                if(idx!=-1)
//                {
//                	loadname=login_name.substring(0,idx);
//                }
//    		}
//    		if(loadname==null||loadname.length()<=0)
//    			loadname="username";
//    	}
        try {
            conn = AdminDb.getConnection();
            AutoSendEMailBo phoneBO = new AutoSendEMailBo(conn);
            String phoneFiled = phoneBO.getMobileField();
            if (phoneFiled == null || phoneFiled.length() <= 0)
                return;
            DbNameBo dbbo = new DbNameBo(conn);
            loadname = dbbo.getLogonUserNameField();
            EmailTemplateBo bo = new EmailTemplateBo(conn);
            ContentDAO dao = new ContentDAO(conn);
            ArrayList personList = new ArrayList();
            ArrayList list = bo.getTemplateFieldInfo(Integer.parseInt(templateId), 2);
            String msg = bo.getEmailContent(Integer.parseInt(templateId));
            //String msg = ""+ dbean.get(Key_HrpWarn_FieldName_Msg) ;
            StringBuffer buf = new StringBuffer();
            String z1 = cur_time.substring(0, 10) + " 00:00:00";
            String z3 = cur_time.substring(0, 10) + " 23:59:59";
            buf.append(" and (send_time>=" + Sql_switcher.dateValue(z1));
            buf.append(" and send_time<=" + Sql_switcher.dateValue(z3) + ")");
            switch (Sql_switcher.searchDbServer()) {
                case Constant.ORACEL: {
                    cur_time = "to_date('" + cur_time + "','yyyy-mm-dd  hh24:mi:ss')";
                    break;
                }
            }
            for (int i = 0; i < alBasePre.size(); i++) {
                HashMap tempmap = new HashMap();
                String strPre = (String) alBasePre.get(i);
                sql = new StringBuffer();
                sql.append(" select '" + strPre + "',h.a0100,a.a0101 as a0101,a." + phoneFiled + " as phone_num,a." + loadname + " as loadname");
                sql.append(" from " + hrpwarn_T + " h," + strPre + "A01 a where h.nbase='" + strPre + "' and a.a0100=h.a0100 and h.wid=" + strWid + "");
                sql.append(" and a." + loadname + " in(select username from " + sms_content_T + " E where  E.wid='" + strWid + "'");
                if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                    sql.append(" and " + Sql_switcher.diffDays("" + cur_time + "", "(select max(send_time) from " + sms_content_T + " F where  F.wid='" + strWid + "')") + ">" + (Integer.parseInt(sendspace)) + ")");
                else
                    sql.append(" and " + Sql_switcher.diffDays("'" + cur_time + "'", "(select max(send_time) from " + sms_content_T + " F where  F.wid='" + strWid + "')") + ">" + (Integer.parseInt(sendspace)) + ")");

                sql.append(" and a." + loadname + " is not null ");
                //System.out.println(sql.toString());
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    LazyDynaBean mBean = new LazyDynaBean();
                    mBean.set("sender", userView.getUserFullName());//发送者姓名
                    mBean.set("receiver", rs.getString("a0101"));//接收人姓名
                    mBean.set("pera0100", strPre + rs.getString("a0100"));
                    if (rs.getString("phone_num") == null || rs.getString("phone_num").length() <= 0)
                        continue;
                    mBean.set("phone_num", rs.getString("phone_num"));//电话号码
                    String cont = bo.getFactContent(msg, (String) mBean.get("pera0100"), list, userView);
                    mBean.set("msg", cont);//信息
                    mBean.set("wid", strWid);//预警编号
                    mBean.set("username", rs.getString("loadname"));//登陆名称
//					mBean.set("templateId", templateId);
                    tempmap.put(rs.getString("a0100") + rs.getString("loadname"), "");
                    personList.add(mBean);
                }
                sql.delete(0, sql.length());
                sql.append(" select '" + strPre + "',h.a0100,a.a0101 as a0101,a." + phoneFiled + " as phone_num,a." + loadname + " as loadname");
                sql.append(" from " + hrpwarn_T + " h," + strPre + "A01 a where h.nbase='" + strPre + "' and a.a0100=h.a0100 and h.wid=" + strWid + "");
                sql.append(" and a." + loadname + " not in(select username from " + sms_content_T + " E where  E.wid='" + strWid + "'");
                sql.append(" " + buf.toString());
                sql.append(")");
                //System.out.println(sql.toString());
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    if (tempmap.containsKey(rs.getString("a0100") + rs.getString("loadname"))) {
                        continue;
                    }
                    LazyDynaBean mBean = new LazyDynaBean();
                    mBean.set("sender", userView.getUserFullName());//发送者姓名
                    mBean.set("receiver", rs.getString("a0101"));//接收人姓名
                    mBean.set("pera0100", strPre + rs.getString("a0100"));
                    if (rs.getString("phone_num") == null || rs.getString("phone_num").length() <= 0)
                        continue;
                    mBean.set("phone_num", rs.getString("phone_num"));//电话号码
                    String cont = bo.getFactContent(msg, (String) mBean.get("pera0100"), list, userView);
                    mBean.set("msg", cont);//信息
                    mBean.set("wid", strWid);//预警编号
                    mBean.set("username", rs.getString("loadname"));//登陆名称
//					mBean.set("templateId", templateId);
                    personList.add(mBean);
                }
            }
            SmsBo smsbo = new SmsBo(conn, userView);
            smsbo.batchSendMessage(personList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ee) {

            }
        }
    }

    /**
     *
     */
    private static void getBirthdayWid() {
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
            birthday_wid = sysbo.getValue(Sys_Oth_Parameter.BIRTHDAY_WID);
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 发送内部消息
     *
     * @param wid
     * @param personList
     * @param userView
     */
    private static void sendMessToAppoint_news(String wid, ArrayList personList, UserView userView, String days, String templateId) {
        String appoint_news_Table = "appoint_news";
        String tempTable = "email_content";
        if (personList == null || personList.size() == 0)
            return;
        RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        String loadname = "username";
        if (login_vo != null) {
            String login_name = login_vo.getString("str_value");
            if (login_name != null && login_name.length() > 0) {
                int idx = login_name.indexOf(",");
                if (idx != -1) {
                    loadname = login_name.substring(0, idx);
                }
            }
            if (loadname == null || loadname.length() <= 0)
                loadname = "username";
            if ("#".equals(loadname))
                loadname = "username";
        }
        StringBuffer sql = new StringBuffer();
        Connection conn = null;
        RowSet rs = null;
        try {
            conn = AdminDb.getConnection();
            DbWizard dbWizard = new DbWizard(conn);
            if (!dbWizard.isExistTable(appoint_news_Table, false)) {
                if (conn != null)
                    conn.close();
                return;
            }
            ContentDAO dao = new ContentDAO(conn);
            EmailTemplateBo bo = new EmailTemplateBo(conn);
            ArrayList list = bo.getTemplateFieldInfo(Integer.parseInt(templateId), 2);
            String msg = bo.getEmailContent(Integer.parseInt(templateId));
            for (int i = 0; i < personList.size(); i++) {
                String str = (String) personList.get(i);
                RecordVo vo = new RecordVo("appoint_news");
                sql.delete(0, sql.length());
                sql.append("select " + loadname + " as username from " + str.substring(0, 3) + "A01 A where a0100='" + str.substring(3) + "'");
                sql.append(" and A." + loadname + " is not null ");
                //System.out.println(sql.toString());
                rs = dao.search(sql.toString());
                if (rs.next()) {
                    String cont = bo.getFactContent(msg, str, list, userView);
                    String subject = bo.getEmailTemplateSubject(templateId);

                    vo.setString("title", subject);//模版名称
                    vo.setString("content", cont);//发送模版
                    vo.setString("inceptuser", rs.getString("username"));//接受人员
                    IDGenerator idg = new IDGenerator(2, conn);
                    String insertid = idg.getId(("appoint_news.id"));
                    vo.setString("news_id", insertid);
                    vo.setString("senduser", userView.getUserName());//发送人员
                    vo.setDate("sendtime", new Date());//发送时间
                    vo.setString("days", days);//有效天数
                    vo.setString("wid", wid);//预警编号
                    vo.setInt("state", 0);//状态
                    vo.setInt("dis_flag", 0);//处理方式
                    String del = "delete from appoint_news where inceptuser='" + rs.getString("username") + "' and wid='" + wid + "'";
                    dao.delete(del, new ArrayList());
                    dao.addValueObject(vo);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 发送内部消息(以前方式需要发邮件的前提下才能通知到本人)
     *
     * @param wid
     * @param personList
     * @param userView
     */
    private static void sendMessToAppoint_news(String wid, ArrayList personList, UserView userView, String days) {
        String appoint_news_Table = "appoint_news";
        String tempTable = "email_content";
        if (personList == null || personList.size() == 0)
            return;
        RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        String loadname = "username";
        if (login_vo != null) {
            String login_name = login_vo.getString("str_value");
            if (login_name != null && login_name.length() > 0) {
                int idx = login_name.indexOf(",");
                if (idx != -1) {
                    loadname = login_name.substring(0, idx);
                }
            }
            if (loadname == null || loadname.length() <= 0)
                loadname = "username";
            if ("#".equals(loadname))
                loadname = "username";
        }
        StringBuffer sql = new StringBuffer();
        Connection conn = null;
        RowSet rs = null;
        try {
            conn = AdminDb.getConnection();
            DbWizard dbWizard = new DbWizard(conn);
            if (!dbWizard.isExistTable(appoint_news_Table, false)) {
                if (conn != null)
                    conn.close();
                return;
            }
            ContentDAO dao = new ContentDAO(conn);

            for (int i = 0; i < personList.size(); i++) {
                String str = (String) personList.get(i);
                RecordVo vo = new RecordVo("appoint_news");
                sql.delete(0, sql.length());
                sql.append("select T.subject,T.content ,A." + loadname + " as username from " + tempTable + " T," + str.substring(0, 3) + "A01 A where wid='" + wid + "'");
                sql.append(" and T.pre='" + str.substring(0, 3) + "' and T.a0100='" + str.substring(3) + "' and A.a0100=T.a0100");
                sql.append(" and A." + loadname + " is not null ");
                sql.append(" and T.i9999=(select max(i9999) from email_content where wid='" + wid + "' and pre='" + str.substring(0, 3) + "'  and a0100='" + str.substring(3) + "')");
                //System.out.println(sql.toString());
                rs = dao.search(sql.toString());
                if (rs.next()) {

                    vo.setString("title", rs.getString("subject"));//模版名称
                    vo.setString("content", rs.getString("content"));//发送模版
                    vo.setString("inceptuser", rs.getString("username"));//接受人员
                    IDGenerator idg = new IDGenerator(2, conn);
                    String insertid = idg.getId(("appoint_news.id"));//.toUpperCase()
                    vo.setString("news_id", insertid);
                    vo.setString("senduser", userView.getUserName());//发送人员
                    vo.setDate("sendtime", new Date());//发送时间
                    vo.setString("days", days);//有效天数
                    vo.setString("wid", wid);//预警编号
                    vo.setInt("state", 0);//状态
                    vo.setInt("dis_flag", 0);//处理方式
                    String del = "delete from appoint_news where inceptuser='" + rs.getString("username") + "' and wid='" + wid + "'";
                    dao.delete(del, new ArrayList());
                    dao.addValueObject(vo);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (Exception e) {

            }
        }
    }


    /**
     * 发送生日
     *
     * @param dbean
     * @param alBasePre
     * @param userView
     */
    public static void sendEveryOneBD(DynaBean dbean, ArrayList alBasePre, UserView userView) {
        String strWid = (String) dbean.get(Key_HrpWarn_FieldName_ID);
        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) dbean.get(Key_HrpWarn_Ctrl_VO);
        if (vo.getStrEveryone() == null || !"true".equalsIgnoreCase(vo.getStrEveryone()))
            return;
        String templateId = vo.getStrNote();
        if (templateId == null || templateId.length() <= 0)
            return;
    	/*if(1==1)
    		return;*/
        String sendspace = vo.getStrSendspace();
        if (sendspace == null || sendspace.length() <= 0)
            sendspace = "7";
        RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        String loadname = "username";
        if (login_vo != null) {
            String login_name = login_vo.getString("str_value");
            if (login_name != null && login_name.length() > 0) {
                int idx = login_name.indexOf(",");
                if (idx != -1) {
                    loadname = login_name.substring(0, idx);
                }
            }
            if (loadname == null || loadname.length() <= 0)
                loadname = "username";
            if ("#".equals(loadname))
                loadname = "username";
        }
        //String email_content_T="email_content";
        String hrpwarn_T = "hrpwarn_result";
        String appoint_news_Table = "appoint_news";
        StringBuffer sql = new StringBuffer();
        String cur_time = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
        Connection conn = null;
        StringBuffer buf = new StringBuffer();
        String z1 = cur_time.substring(0, 10) + " 00:00:00";
        String z3 = cur_time.substring(0, 10) + " 23:59:59";
        buf.append(" and (sendtime>=" + Sql_switcher.dateValue(z1));
        buf.append(" and sendtime<=" + Sql_switcher.dateValue(z3) + ")");
        RowSet rs = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            ArrayList personList = new ArrayList();
            switch (Sql_switcher.searchDbServer()) {
                case Constant.ORACEL: {
                    cur_time = "to_date('" + cur_time + "','yyyy-mm-dd  hh24:mi:ss')";
                    break;
                }
            }
            for (int i = 0; i < alBasePre.size(); i++) {
                String strPre = (String) alBasePre.get(i);
                sql = new StringBuffer();
                sql.append(" select '" + strPre + "',a0100");
                sql.append(" from " + hrpwarn_T + " h where h.nbase='" + strPre + "' and h.wid=" + strWid + "");
                sql.append(" and a0100 in(select a0100 from " + strPre + "A01 A, " + appoint_news_Table + " E where A." + loadname + "=E.inceptuser and E.wid='" + strWid + "'");
                if (!isSave) {//保存时不执行跨天校验//changxy 20170107
                    if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                        sql.append(" and " + Sql_switcher.diffDays("" + cur_time + "", "sendtime") + ">" + (Integer.parseInt(sendspace)) + "");
                    else
                        sql.append(" and " + Sql_switcher.diffDays("'" + cur_time + "'", "sendtime") + ">" + (Integer.parseInt(sendspace)) + "");
                }

                sql.append(")");
                //System.out.println(sql.toString());
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    personList.add(strPre + rs.getString("a0100"));
                }
                sql.delete(0, sql.length());
                sql.append(" select '" + strPre + "',a0100");
                sql.append(" from " + hrpwarn_T + " h where h.nbase='" + strPre + "' and h.wid=" + strWid + "");
                sql.append(" and a0100 not in(select a0100 from (select a0100 from " + strPre + "A01 A, " + appoint_news_Table + " E where A." + loadname + "=E.inceptuser and E.wid='" + strWid + "'");
                sql.append(" " + buf.toString());
                sql.append(")aa)");
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    personList.add(strPre + rs.getString("a0100"));
                }
            }
            if ("true".equalsIgnoreCase(vo.getIsEmail())) {
                sendMessToAppoint_news(strWid, personList, userView, sendspace);//生日提醒,要勾选邮件提醒才邮件通知本人和系统消息框提醒
            } else {
                sendMessToAppoint_news(strWid, personList, userView, sendspace, templateId);//不用邮件通知，只在系统填出消息框中提醒
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ee) {

            }
        }

    }

    /**
     * 发送生日
     *
     * @param dbean
     * @param alBasePre
     * @param userView
     */
    public static void sendEveryOneBDOWN(DynaBean dbean, ArrayList alBasePre, UserView userView) {
        String strWid = (String) dbean.get(Key_HrpWarn_FieldName_ID);
        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) dbean.get(Key_HrpWarn_Ctrl_VO);
        if (vo.getStrEveryone() == null || !"true".equalsIgnoreCase(vo.getStrEveryone()))
            return;
        String templateId = vo.getStrNote();
        if (templateId == null || templateId.length() <= 0)
            return;
    	/*if(1==1)
    		return;*/
        String sendspace = vo.getStrSendspace();
        if (sendspace == null || sendspace.length() <= 0)
            sendspace = "7";
        RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        String loadname = "username";
        if (login_vo != null) {
            String login_name = login_vo.getString("str_value");
            if (login_name != null && login_name.length() > 0) {
                int idx = login_name.indexOf(",");
                if (idx != -1) {
                    loadname = login_name.substring(0, idx);
                }
            }
            if (loadname == null || loadname.length() <= 0)
                loadname = "username";
            if ("#".equals(loadname))
                loadname = "username";
        }
        //String email_content_T="email_content";
        String hrpwarn_T = "hrpwarn_result";
        String appoint_news_Table = "appoint_news";
        StringBuffer sql = new StringBuffer();
        String cur_time = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
        Connection conn = null;
        StringBuffer buf = new StringBuffer();
        String z1 = cur_time.substring(0, 10) + " 00:00:00";
        String z3 = cur_time.substring(0, 10) + " 23:59:59";
        buf.append(" and (sendtime>=" + Sql_switcher.dateValue(z1));
        buf.append(" and sendtime<=" + Sql_switcher.dateValue(z3) + ")");
        RowSet rs = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            ArrayList personList = new ArrayList();
            switch (Sql_switcher.searchDbServer()) {
                case Constant.ORACEL: {
                    cur_time = "to_date('" + cur_time + "','yyyy-mm-dd  hh24:mi:ss')";
                    break;
                }
            }
            for (int i = 0; i < alBasePre.size(); i++) {
                String strPre = (String) alBasePre.get(i);
                sql = new StringBuffer();
                sql.append(" select '" + strPre + "',a0100");
                sql.append(" from " + hrpwarn_T + " h where h.nbase='" + strPre + "' and h.wid=" + strWid + "");
                sql.append(" and a0100 in(select a0100 from " + strPre + "A01 A, " + appoint_news_Table + " E where A." + loadname + "=E.inceptuser and E.wid='" + strWid + "'");
                if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                    sql.append(" and " + Sql_switcher.diffDays("" + cur_time + "", "sendtime") + ">" + (Integer.parseInt(sendspace)) + "");
                else
                    sql.append(" and " + Sql_switcher.diffDays("'" + cur_time + "'", "sendtime") + ">" + (Integer.parseInt(sendspace)) + "");

                sql.append(")");
                //System.out.println(sql.toString());
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    personList.add(strPre + rs.getString("a0100"));
                }
                sql.delete(0, sql.length());
                sql.append(" select '" + strPre + "',a0100");
                sql.append(" from " + hrpwarn_T + " h where h.nbase='" + strPre + "' and h.wid=" + strWid + "");
                sql.append(" and a0100 not in(select a0100 from (select a0100 from " + strPre + "A01 A, " + appoint_news_Table + " E where A." + loadname + "=E.inceptuser and E.wid='" + strWid + "'");
                sql.append(" " + buf.toString());
                sql.append(")aa)");
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    personList.add(strPre + rs.getString("a0100"));
                }
            }
            sendMessToAppoint_news(strWid, personList, userView, sendspace);//生日提醒
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ee) {

            }
        }

    }
    /******************************/
    /**
     * 业务预警发送邮件
     *
     * @param FSQL
     * @param setid
     * @param dao
     */
    private static void sendTranWarnEmail(String FSQL, String setid, DynaBean db, ContentDAO dao) {
        String sql = "select itemid from t_sys_warntype where upper(setid)='" + setid.toUpperCase() + "' ";
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            String itemid = "";
            if (rs.next())
                itemid = rs.getString("itemid");

            if (itemid == null || itemid.length() <= 0)
                return;

            sql = "select " + itemid + " itemid from " + setid + " where " + FSQL;
            rs = dao.search(sql);
            ArrayList list = new ArrayList();
            while (rs.next()) {
                if (rs.getString("itemid") != null && rs.getString("itemid").length() > 0)
                    list.add(rs.getString("itemid"));
            }

            ArrayList elist = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                String email = getEmail((String) list.get(i), dao);
                if (email != null && email.length() > 0) {
                    LazyDynaBean bean = new LazyDynaBean();
                    bean.set("email", email);
                    bean.set("username", (String) list.get(i));
                    elist.add(bean);
                }
            }
            //System.out.println(elist);
            SendTranWarnMail(elist, db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
        }
    }

    /*
     * 发送考勤员工日明细或月汇总预警提示
     * @author zhaoxj
     * @date 2013-07-05
     */
    private static void sendKqQ03Q05WarnEmail(String FSQL, String setid, DynaBean db, ContentDAO dao,
                                              UserView uv, Connection conn, HashMap usedItems) {
        if (null == FSQL || "".equals(FSQL))
            return;

        RowSet rs = null;
        String emailFld = "";
        String mobileFld = "";

        try {
            ConfigCtrlInfoVO ctrlInfoVO = (ConfigCtrlInfoVO) db.get(Key_HrpWarn_Ctrl_VO);
            String strDomain = ctrlInfoVO.getStrDomain();

            if (strDomain == null || strDomain.trim().length() < 3)
                strDomain = "";

            boolean needSendEmail = "true".equalsIgnoreCase(ctrlInfoVO.getIsEmail());
            boolean needSendSms = "true".equalsIgnoreCase(ctrlInfoVO.getIsMobile());
            boolean needWeixin = "true".equalsIgnoreCase(ctrlInfoVO.getIsWeixin());

            if (needSendEmail) {
                emailFld = getEmailFld();
            }

            if (needSendSms) {
                mobileFld = getMobileFld();
            }

            if ("".equals(emailFld) && "".equals(mobileFld) && !needWeixin)
                return;

            ArrayList mailItems = getKqQ03Q05ItemsForMail(usedItems);

            //发通知给员工
            sendMsgToEmployee(FSQL, db, dao, uv, conn, emailFld, mobileFld,
                    needSendEmail, needSendSms, mailItems, needWeixin);

            //发通知给角色
            sendMsgToRole(FSQL, setid, db, dao, uv, conn, emailFld, mobileFld, strDomain,
                    needSendEmail, needSendSms, mailItems, needWeixin);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    private static ArrayList getInitSumData(ArrayList items) {
        ArrayList sumData = new ArrayList();
        for (int i = 0; i < items.size(); i++) {
            FieldItem item = (FieldItem) items.get(i);

            if ("N".equalsIgnoreCase(item.getItemtype()))
                sumData.add("0");
            else
                sumData.add("");
        }

        return sumData;
    }

    private static String getTabSumRow(ArrayList sumData, ArrayList items) {
        StringBuffer sumRowHtml = new StringBuffer("<tr style='background-color:#FFF8D2'>");

        for (int i = 0; i < items.size(); i++) {
            FieldItem item = (FieldItem) items.get(i);

            sumRowHtml.append("<td style=\"border:1px solid #C4D8EE; border-top:0px;");
            if (i > 0)
                sumRowHtml.append("border-left:0px;");

            sumRowHtml.append("\"");

            if ("N".equalsIgnoreCase(item.getItemtype()))
                sumRowHtml.append(" align=\"right\"");

            if (0 == i)
                sumRowHtml.append(" align=\"center\"");

            sumRowHtml.append(">&nbsp;");

            if (0 == i)
                sumRowHtml.append("合计");
            else {
                if (!"0".equals(sumData.get(i).toString()))
                    sumRowHtml.append(sumData.get(i).toString());
            }

            sumRowHtml.append("&nbsp;</td>");
        }

        return sumRowHtml.append("</tr>").toString();

    }

    private static void sendMsgToEmployee(String sql, DynaBean db, ContentDAO dao, UserView uv, Connection conn,
                                          String emailFld, String mobileFld, boolean needSendEmail, boolean needSendSms, ArrayList items, boolean needWeixin)
            throws SQLException, GeneralException {

        // linbz  20160504  给员工本人发考勤异常提醒，不用显示单位B0110，部门E0122，岗位E01A1，姓名A0101
        ArrayList needItems = new ArrayList();
        for (int i = 0; i < items.size(); i++) {
            FieldItem item = (FieldItem) items.get(i);
            String itemId = item.getItemid();

            if (",A0101,B0110,E0122,E01A1,".contains("," + itemId.toUpperCase() + ","))
                continue;

            needItems.add(item);
        }

        ArrayList nbases = DataDictionary.getDbpreList();
        StringBuilder strSQL = new StringBuilder();
        strSQL.append("select * from (");
        for (int i = 0; i < nbases.size(); i++) {
            String nbase = (String) nbases.get(i);

            if (i > 0) {
                strSQL.append(" union all ");
            }
            strSQL.append(" select a.*,b.a0000 as a01a0000 from (");
            strSQL.append(" SELECT * FROM Q03");
            strSQL.append(" WHERE ").append("Q03.nbase='").append(nbase).append("'");
            strSQL.append(" and (").append(sql).append(")");
            strSQL.append(") a");
            strSQL.append(" left join ").append(nbase).append("A01 b");
            strSQL.append(" on a.a0100=b.a0100");
        }

        strSQL.append(") c");
        strSQL.append(" ORDER BY nbase,a01a0000,a0100,Q03Z0");

        RowSet rs = null;
        try {
            rs = dao.search(strSQL.toString());

            String preNbase = "";
            String preA0100 = "";

            String topic = (String) db.get(Key_HrpWarn_FieldName_Name);
            String emailAdress = "";
            String strEmail = "";
            String strEmailHead = "" + db.get(Key_HrpWarn_FieldName_Msg);
            String phone = "";
            ArrayList personList = new ArrayList();

            ArrayList sumData = getInitSumData(needItems);

            String tabHeader = getKqQ03Q05MailTabHeader(needItems);
            String loginfield = "username";
            if (needWeixin) {
                loginfield = getSelfLoginUserNameFld();
            }
            ArrayList emplist = new ArrayList();
            while (rs.next()) {
                String nbase = rs.getString("nbase");
                String a0100 = rs.getString("a0100");
                String a0101 = rs.getString("a0101");

                //邮件提醒
                if (needSendEmail) {
                    //循环到下一个人
                    if (!nbase.equals(preNbase) || !a0100.equals(preA0100)) {
                        //如果不是第一个人，发送邮件给前一人
                        if ((!"".equals(preNbase) && !"".equals(preA0100))) {
                            String sumDataHtml = getTabSumRow(sumData, needItems);
                            String mailContent = "<html><body>"
                                    + strEmailHead
                                    + "<br><br>您的考勤数据<br>"
                                    + "<table cellspacing='0' border='0px'>"
                                    + tabHeader
                                    + strEmail
                                    + sumDataHtml
                                    + "</table><br>"
                                    + "</body></html>";
                            sendEmail(conn, preNbase, preA0100, topic, emailAdress, mailContent);
                            strEmail = "";
                            sumData = getInitSumData(needItems);
                        }

                        //取当前人员的邮件地址
                        emailAdress = getEmailAdress(dao, nbase, a0100, emailFld);
                        if (!PubFunc.isEmailAddress(emailAdress)) {
                            emailAdress = "";
                        }
                    }

                    //如果邮件地址有效，组合邮件内容
                    if (!"".equals(emailAdress)) {
                        strEmail += getKqQ03Q05DataByEmp(rs, needItems, sumData);

                        //如果是最后一条记录，直接发送邮件
                        if (rs.isLast()) {
                            String sumDataHtml = getTabSumRow(sumData, needItems);
                            String mailContent = "<html><body>"
                                    + strEmailHead
                                    + "<br><br>您的考勤数据<br>"
                                    + "<table cellspacing='0' border='0px'>"
                                    + tabHeader
                                    + strEmail
                                    + sumDataHtml
                                    + "</table><br>"
                                    + "</body></html>";
                            sendEmail(conn, nbase, a0100, topic, emailAdress, mailContent);
                            strEmail = "";
                            sumData = getInitSumData(needItems);
                        }
                    }
                }

                //短信提醒
                if (needSendSms) {
                    //取当前人员的手机号码
                    phone = getEmailAdress(dao, nbase, a0100, mobileFld);

                    //循环到下一个人(包括第一个人)
                    if (!nbase.equals(preNbase) || !a0100.equals(preA0100)) {
                        addMsgToMsgList(personList, uv.getUserFullName(), a0101, phone, (String) db.get(Key_HrpWarn_FieldName_Msg));
                    }
                }

                if (needWeixin) {
                    if (!nbase.equals(preNbase) || !a0100.equals(preA0100)) {
                        emplist.add(nbase + a0100);
                    }
                }
                preNbase = nbase;
                preA0100 = a0100;

            }

            if (needSendSms && personList.size() > 0) {
                SmsBo smsbo = new SmsBo(conn, uv);
                smsbo.batchSendMessage(personList);
            }
            if (needWeixin && emplist.size() > 0) {
                for (int i = 0; i < emplist.size(); i++) {
                    String dba0100 = (String) emplist.get(i);
                    rs = dao.search("select " + loginfield + " from " + dba0100.substring(0, 3) + "a01 where a0100='" + dba0100.substring(3) + "'");
                    if (rs.next()) {
                        String usename = rs.getString(loginfield);
                        if (usename != null && usename.length() > 0) {
                            WeiXinBo.sendMsgToPerson(usename, topic, strEmailHead, "http://www.hjsoft.com.cn:8089/UserFiles/Image/warn.png", "");
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void sendMsgToRole(String sql, String setId, DynaBean db, ContentDAO dao, UserView uv, Connection conn,
                                      String emailFld, String mobileFld, String strDomain, boolean needSendEmail, boolean needSendSms, ArrayList items, boolean needWeixin)
            throws SQLException, GeneralException {

        if ("".equals(strDomain))
            return;

        // 获取预警对象用户ID
        ArrayList alUserIDList = getUserIDList(strDomain);
        if (alUserIDList == null || alUserIDList.size() == 0)
            return;

        //对每个用户发通知（前提：用户管理范围内人员有符合预警条件的数据）
        for (int i = 0; i < alUserIDList.size(); i++) {
            DynaBean dBean = (LazyDynaBean) alUserIDList.get(i);
            String username = (String) (dBean.get("username") == null ? "" : dBean.get("username"));
            String objPhone = (String) (dBean.get("phone") == null ? "" : dBean.get("phone"));

            String objEmail = (String) (dBean.get("email") == null ? "" : dBean.get("email"));
            //校验邮件地址合法性
            if (!PubFunc.isEmailAddress(objEmail)) {
                objEmail = "";
            }

            String objA0100 = (String) (dBean.get("a0100") == null ? "" : dBean.get("a0100"));
            String objNbase = objA0100.substring(0, 3);
            String objA0101 = (String) (dBean.get("userfullname") == null ? "" : dBean.get("userfullname"));
            if ("".equals(objA0101))
                objA0101 = username;

            RowSet rs = null;

            UserView userView = new UserView(username, conn);
            try {
                /** 不检查口令，仅检查用户名 */
                if (!userView.canLogin(false))
                    continue;

                //组sql语句，查询要发预警提示的人员
                ArrayList nbases = DataDictionary.getDbpreList();
                StringBuilder strSQL = new StringBuilder();
                strSQL.append("select * from (");
                for (int j = 0; j < nbases.size(); j++) {
                    String nbase = (String) nbases.get(j);

                    String kqWhr = RegisterInitInfoData.getWhereINSql(userView, nbase);
                    if (null == kqWhr || "".equals(kqWhr))
                        continue;

                    if (j > 0) {
                        strSQL.append(" union all ");
                    }
                    strSQL.append(" select a.*,b.a0000 as a01a0000 from (");
                    strSQL.append(" SELECT * FROM Q03");
                    strSQL.append(" WHERE ").append("Q03.nbase='").append(nbase).append("'");
                    strSQL.append(" and (").append(sql).append(")");
                    strSQL.append(" and a0100 in (select a0100 ").append(kqWhr).append(")");
                    strSQL.append(") a");
                    strSQL.append(" left join ").append(nbase).append("A01 b");
                    strSQL.append(" on a.a0100=b.a0100");
                }

                strSQL.append(") A");
                strSQL.append(" ORDER BY nbase,a01a0000,a0100,Q03Z0");

                rs = dao.search(strSQL.toString());

                //异常数据人数
                int empCount = 0;
                String preNbase = "";
                String preA0100 = "";

                String topic = (String) db.get(Key_HrpWarn_FieldName_Name);
                String strEmail = "";
                String strEmailHead = "" + db.get(Key_HrpWarn_FieldName_Msg);

                ArrayList sumData = getInitSumData(items);
                String tabHeader = getKqQ03Q05MailTabHeader(items);

                while (rs.next()) {
                    if (0 == empCount)
                        empCount++;

                    String nbase = rs.getString("nbase");
                    String a0100 = rs.getString("a0100");

                    //循环到下一个人
                    if (!nbase.equals(preNbase) || !a0100.equals(preA0100)) {
                        //如果不是第一个人，人数加1
                        if ((!"".equals(preNbase) && !"".equals(preA0100))) {
                            if (needSendEmail && !"".equals(objEmail)) {
                                strEmail += getTabSumRow(sumData, items);
                                sumData = getInitSumData(items);
                            }
                            empCount++;
                        }
                    }

                    //如果需要发邮件
                    if (needSendEmail && !"".equals(objEmail)) {
                        strEmail += getKqQ03Q05DataByEmp(rs, items, sumData);
                    }

                    preNbase = nbase;
                    preA0100 = a0100;
                }

                if (needSendEmail && !"".equals(objEmail) && !"".equals(strEmail)) {
                    strEmail += getTabSumRow(sumData, items);
                    String mailContent = "<html><body>"
                            + strEmailHead
                            + "<br><br>考勤数据<br>"
                            + "<table cellspacing='0' border='0px'>"
                            + tabHeader
                            + strEmail
                            + "</table><br>"
                            + "</body></html>";
                    sendEmail(conn, objNbase, objA0100, topic, objEmail, mailContent);
                }

                if (needSendSms && !"".equals(objPhone) && empCount > 0) {
                    SmsBo smsbo = new SmsBo(conn, uv);
                    smsbo.sendMessage(uv.getUserFullName(), objA0101, objPhone, (String) db.get(Key_HrpWarn_FieldName_Msg));
                }
                if (needWeixin && empCount > 0) {
                    WeiXinBo.sendMsgToPerson(username, topic, strEmailHead, "http://www.hjsoft.com.cn:8089/UserFiles/Image/warn.png", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != rs) {
                    try {
                        rs.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void sendEmail(Connection conn, String nbase, String a0100, String topic, String address, String content) {
        EMailBo mailbo = null;
        try {
            //地址为空，发不了
            if ("".equals(PubFunc.nullToStr(address).trim()))
                return;

            //内容为空，不发
            if ("".equals(PubFunc.nullToStr(content).trim()))
                return;

            mailbo = new EMailBo(conn, true, nbase);
            mailbo.sendEmail(topic, content, "", address);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mailbo)
                mailbo.close();
        }
    }

    private static void addMsgToMsgList(ArrayList MsgList, String sender, String receiver, String phone, String msg) {
        //如果手机号码为空，发不了
        if ("".equals(PubFunc.nullToStr(phone)))
            return;

        //如果发送信息为空，不发
        if ("".equals(PubFunc.nullToStr(msg.trim())))
            return;

        LazyDynaBean mBean = new LazyDynaBean();
        mBean.set("sender", sender);//发送者姓名
        mBean.set("receiver", receiver);//接收人姓名
        mBean.set("phone_num", phone);//电话号码
        mBean.set("msg", msg.trim());//信息
        MsgList.add(mBean);
    }

    /**
     * 取得考勤预警邮件内容表格中需要的指标项列表
     *
     * @param usedFieldsByExpress 公式中用到的指标项
     * @return
     * @Title: getKqQ03Q05ItemsForMail
     * @Description:
     */
    private static ArrayList getKqQ03Q05ItemsForMail(HashMap usedFieldsByExpress) {
        ArrayList items = new ArrayList();

        ArrayList q03Items = DataDictionary.getFieldList("q03", Constant.USED_FIELD_SET);

        //第一步 把几个固定的指标拿出来放到最前边
        for (int i = 0; i < q03Items.size(); i++) {
            FieldItem anItem = (FieldItem) q03Items.get(i);

            String itemId = anItem.getItemid().toUpperCase();
            if ("A0101,B0110,E0122,E01A1,Q03Z0".contains(itemId))
                items.add(anItem);
        }

        //第二步 将公式中用到的非上一步中的固定指标按业务字典的顺序拿出来
        for (int i = 0; i < q03Items.size(); i++) {
            FieldItem anItem = (FieldItem) q03Items.get(i);
            String itemId = anItem.getItemid().toUpperCase();
            if ("A0101,B0110,E0122,E01A1,Q03Z0".contains(itemId))
                continue;

            Iterator it = usedFieldsByExpress.values().iterator();
            while (it.hasNext()) {
                FieldItem usedItem = (FieldItem) it.next();
                if (itemId.equalsIgnoreCase(usedItem.getItemid())) {
                    items.add(usedItem);
                    continue;
                }
            }
        }

        return items;
    }

    /**
     * 得到考勤预警邮件数据表头
     *
     * @param items 表头需要的列
     * @return
     * @Title: getKqQ03Q05MailTabHeader
     * @Description:
     */
    private static String getKqQ03Q05MailTabHeader(ArrayList items) {
        StringBuilder titles = new StringBuilder();
        titles.append("<tr>");

        for (int i = 0; i < items.size(); i++) {
            FieldItem item = (FieldItem) items.get(i);
            titles.append("<th style=\"background-color:#f4f7f7;border:1px solid #C4D8EE;");
            if (i > 0)
                titles.append(" border-left:0px");
            titles.append("\">&nbsp;");
            titles.append(item.getItemdesc());
            titles.append("&nbsp;</th>");
        }

        return titles.append("</tr>").toString();
    }

    private static String getKqQ03Q05DataByEmp(RowSet rs, ArrayList items, ArrayList sumData) {

        StringBuilder kqData = new StringBuilder();
        StringBuilder data = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            FieldItem item = (FieldItem) items.get(i);
            String itemId = item.getItemid();

            try {
                String strValue = "";
                String itemType = item.getItemtype();
                if ("A".equalsIgnoreCase(itemType) || "M".equalsIgnoreCase(itemId)) {
                    strValue = rs.getString(itemId);
                    if (strValue == null)
                        strValue = "";

                    //如果是代码型，需转换成代码名称
                    String codeSetId = item.getCodesetid();
                    if (!"".equalsIgnoreCase(codeSetId) && !"0".equalsIgnoreCase(codeSetId))
                        strValue = AdminCode.getCodeName(codeSetId, strValue);
                } else if ("N".equalsIgnoreCase(itemType)) {
                    BigDecimal numValue = rs.getBigDecimal(itemId);
                    if (numValue != null && 0 != numValue.compareTo(BigDecimal.valueOf(0))) {
                        strValue = numValue.setScale(item.getDecimalwidth(), BigDecimal.ROUND_HALF_UP).toString();

                        BigDecimal sumValue = BigDecimal.valueOf(Double.parseDouble(((String) sumData.get(i))));
                        sumData.set(i, (sumValue.add(numValue)).setScale(item.getDecimalwidth(), BigDecimal.ROUND_HALF_UP).toString());
                    }
                } else if ("D".equalsIgnoreCase(itemId)) {
                    java.sql.Date dateValue = rs.getDate(itemId);
                    if (dateValue != null)
                        strValue = DateUtils.FormatDate(dateValue);
                }

                data.append("<td style=\"border:1px solid #C4D8EE; border-top:0px;");
                if (i > 0)
                    data.append("border-left:0px;");
                data.append("\"");
                if ("N".equalsIgnoreCase(itemType))
                    data.append(" align=\"right\"");
                data.append(">&nbsp;");
                data.append(strValue);
                data.append("&nbsp;</td>");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        kqData.append("<tr>").append(data).append("</tr>");

        return kqData.toString();
    }

    /*
     * 得到电子邮箱指标
     */
    private static String getEmailFld() {
        String emailFld = "";

        AutoSendEMailBo bo = new AutoSendEMailBo(null);
        emailFld = bo.getEmailField();
        if (emailFld == null || emailFld.length() <= 0)
            emailFld = "";

        return emailFld;
    }

    /*
     * 得到移动电话号码指标
     */
    private static String getMobileFld() {
        String mobileFld = "";

        AutoSendEMailBo bo = new AutoSendEMailBo(null);
        mobileFld = bo.getMobileField();
        if (mobileFld == null || mobileFld.length() <= 0)
            mobileFld = "";

        return mobileFld;
    }

    /*
     * 得到自助用户登录名指标
     */
    private static String getSelfLoginUserNameFld() {
        String login_username = "username";

        RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        if (login_vo != null) {
            String login_name = login_vo.getString("str_value");
            int idx = login_name.indexOf(",");
            if (idx != -1) {
                login_username = login_name.substring(0, idx);
                if (login_username == null || login_username.length() <= 0 || "#".equals(login_username))
                    login_username = "username";
            }
        }

        return login_username;
    }

    private static String getEmail(String username, ContentDAO dao) {
        String email = "";
        String email_name = getEmailFld();
        if ("".equals(email_name))
            return email;

        String sql = "select a0100,nbase,email from operuser where username='" + username + "'";

        RowSet rs = null;
        try {
            rs = dao.search(sql);
            String a0100 = "";
            String nbase = "";
            if (rs.next()) {
                a0100 = rs.getString("a0100");
                nbase = rs.getString("nbase");
                email = rs.getString("email");
                if (email != null && email.length() > 0)
                    return email;

                if (a0100 == null || a0100.length() <= 0 || nbase == null || nbase.length() <= 0)
                    return "";

                sql = "select " + email_name + " email from " + nbase + "A01 where a0100='" + a0100 + "'";
                rs = dao.search(sql);
                if (rs.next()) {
                    email = rs.getString("email");
                }
            } else {
                String login_username = getSelfLoginUserNameFld();
                ArrayList dblist = DataDictionary.getDbpreList();
                for (int i = 0; i < dblist.size(); i++) {
                    nbase = (String) dblist.get(i);
                    sql = "select " + email_name + " email from " + nbase + "A01 where " + login_username + "='" + username + "'";
                    rs = dao.search(sql);
                    if (rs.next()) {
                        email = rs.getString("email");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            e.printStackTrace();
        }
        return email;
    }

    private static String getEmailAdress(ContentDAO dao, String nbase, String a0100, String emailFld) {
        String email = "";

        if ("".equals(emailFld))
            return email;

        RowSet rs = null;
        try {
            String sql = "select " + emailFld + " email from " + nbase + "A01 where a0100='" + a0100 + "'";
            rs = dao.search(sql);
            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return email;
    }

    private static void SendTranWarnMail(ArrayList list, DynaBean db) {

        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) db.get(Key_HrpWarn_Ctrl_VO);
        // 获取预警对象用户ID

        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            UserObjectBo user_bo = new UserObjectBo(conn);
            //**取系统管理员的电子信箱*//
            String src_addr = user_bo.getEmailAddress("su", "");
            if (src_addr == null || "".equals(src_addr.trim())) {
                EmailTemplateBo bo = new EmailTemplateBo(conn);
                src_addr = bo.getFromAddr();
            }
            if (src_addr == null || "".equals(src_addr.trim()))
                return;
            for (int r = 0; r < list.size(); r++) {
                LazyDynaBean bean = (LazyDynaBean) list.get(r);
                String username = (String) bean.get("username");
                String email_address = (String) bean.get("email");
                UserView userView = new UserView(username, conn);
                /** 不检查口令，仅检查用户名 */
                if (userView.canLogin(false)) {
                    EMailBo mailbo = new EMailBo(conn, true, userView.getDbname());
                    String strEmail = "" + db.get(Key_HrpWarn_FieldName_Msg);
                    mailbo.sendEmail((String) db.get(Key_HrpWarn_FieldName_Name), strEmail, "", src_addr, email_address);

                    WeiXinBo.sendMsgToPerson(username, (String) db.get(Key_HrpWarn_FieldName_Name), strEmail, "http://www.hjsoft.com.cn:8089/UserFiles/Image/warn.png", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw GeneralExceptionHandler.Handle(e);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static long getPartMinute(Date start_date, Date end_date) {
        int sY = DateUtils.getYear(start_date);
        int sM = DateUtils.getMonth(start_date);
        int sD = DateUtils.getDay(start_date);
        int sH = DateUtils.getHour(start_date);
        int smm = DateUtils.getMinute(start_date);

        int eY = DateUtils.getYear(end_date);
        int eM = DateUtils.getMonth(end_date);
        int eD = DateUtils.getDay(end_date);
        int eH = DateUtils.getHour(end_date);
        int emm = DateUtils.getMinute(end_date);
        GregorianCalendar d1 = new GregorianCalendar(sY, sM, sD, sH, smm, 00);
        GregorianCalendar d2 = new GregorianCalendar(eY, eM, eD, eH, emm, 00);
        Date date1 = d1.getTime();
        Date date2 = d2.getTime();
        long l1 = date1.getTime();
        long l2 = date2.getTime();
        long part = (l2 - l1) / (60 * 1000L);
        return part;
    }

    /**
     * 中建总发送邮件
     */
    private static void sendMailToZJZOA(DynaBean db, UserView userView) {
        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) db.get(Key_HrpWarn_Ctrl_VO);
        // 获取预警对象用户ID
        ArrayList alUserIDList = getUserIDList(vo.getStrDomain());
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            SendEmailFormOA oa = new SendEmailFormOA(conn);
            if (alUserIDList != null && alUserIDList.size() > 0) {
                for (int i = 0; i < alUserIDList.size(); i++) {
                    DynaBean dBean = (LazyDynaBean) alUserIDList.get(i);
                    String username = (String) (dBean.get("username") == null ? "" : dBean.get("username"));
                    String a0100 = (String) (dBean.get("a0100") == null ? "" : dBean.get("a0100"));
                    String nbase = a0100.substring(0, 3);
                    a0100 = a0100.substring(3);
                    ScanTotal st = new ScanTotal(userView);
                    int iTotal = st.getCount(db);
                    if (iTotal > 0) {// 有实际人数的时候才发送消息？

                        LazyDynaBean sendBean = null;
                        if (userView.getA0100() != null && userView.getA0100().length() > 0) {
                            sendBean = new LazyDynaBean();
                            sendBean.set("nbase", userView.getDbname());
                            sendBean.set("a0100", userView.getA0100());
                        }
                        ArrayList manList = new ArrayList();
                        LazyDynaBean a_bean = new LazyDynaBean();
                        a_bean.set("nbase", nbase);
                        a_bean.set("a0100", a0100);
                        a_bean.set("url", "");
                        manList.add(a_bean);
                        String strEmail = "" + db.get(Key_HrpWarn_FieldName_Msg) + "(计" + iTotal + "人)";
                        String title = (String) db.get(Key_HrpWarn_FieldName_Name);
                        //oa.sendEmail(manList,sendBean,title,strEmail);
                        oa.sendEmail(manList, "HR系统", title, strEmail);

                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw GeneralExceptionHandler.Handle(e);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 中建oa
     *
     * @param wid
     * @param personList
     * @param templateid
     * @param userView
     * @param issend
     */
    private static void warnZJZOAInfomation(String wid, ArrayList personList, String templateid, UserView userView, boolean issend) {
        Connection conn = null;
        RowSet rs = null;
        try {
            if (personList == null || personList.size() == 0)
                return;
            conn = AdminDb.getConnection();
            EmailTemplateBo bo = new EmailTemplateBo(conn);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());
            StringBuffer buf = new StringBuffer();
            ContentDAO dao = new ContentDAO(conn);
            String emailfield = bo.getEmailField(templateid);
            String emailfieldset = bo.getEmailFieldSetId(emailfield);
            /**得模板标题*/
            String subject = bo.getEmailTemplateSubject(templateid);
            StringBuffer sql = new StringBuffer();

            SendEmailFormOA oa = new SendEmailFormOA(conn);
            ArrayList fieldList = bo.getTemplateFieldInfo(Integer.parseInt(templateid), 2);
            String contentSrc = bo.getEmailContent(Integer.parseInt(templateid));
            for (int i = 0; i < personList.size(); i++) {
                String pre = (String) personList.get(i);
                String nbase = pre.substring(0, 3);
                String a0100 = pre.substring(3);
                ArrayList manList = new ArrayList();
                LazyDynaBean a_bean = new LazyDynaBean();
                String prea0100 = nbase + a0100;
                a_bean.set("nbase", nbase);
                a_bean.set("a0100", a0100);
                a_bean.set("url", "");
                manList.add(a_bean);
                String cont = bo.getFactContent(contentSrc, pre, fieldList, userView);
                if (cont != null) {
                    cont = cont.replaceAll("\r\n", "<br>");
                    cont = cont.replace("\n", "<br>");
                    cont = cont.replace("\r", "<br>");
                }
                sql = new StringBuffer();
                sql.append("select subject from email_name e where");
                sql.append(" e.id=" + templateid);
                rs = dao.search(sql.toString());
                ArrayList list = new ArrayList();
                subject = "";
                if (rs.next()) {
                    subject = rs.getString("subject");
                }
                if (cont != null && cont.indexOf("<<自动登陆_") != -1) {
                    String temp = cont.substring(cont.indexOf("<<自动登陆_") + 7);
                    if (temp.indexOf(">>") != -1) {
                        temp = temp.substring(0, temp.indexOf(">>"));
                        StringBuffer url = new StringBuffer();
                        url.append(SystemConfig.getProperty("sso_logon_url"));
                        url.append("/general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=6&tabid=" + temp);
                        String etoken = getetoken(nbase, a0100, conn);
                        url.append("&appfwd=1&etoken=" + etoken + "&validatepwd=false");
                        cont = cont.replaceAll("<<自动登陆_" + temp + ">>", url.toString());
                    }
                }
                oa.sendEmail(manList, "HR系统", subject, cont);
            }
            for (int i = 0; i < personList.size(); i++) {
                String str = (String) personList.get(i);
                buf.append("insert into email_content(");
                buf.append("wid,id,username,subject,send_ok,pre,a0000,a0100,b0110,e01a1,send_time,I9999)");
                buf.append(" select " + wid + " as wid," + templateid + " as id,'");
                buf.append(userView.getUserName());
                buf.append("' as username,'" + subject + "' as subject,0 as send_ok,temp.*,");
                if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                    buf.append("to_date('" + time + "','yyyy-mm-dd HH:mi:ss') ");
                else
                    buf.append("'" + time + "' ");
                buf.append("as send_time,0 as I9999 from (");
                buf.append(" select '");
                buf.append(str.substring(0, 3) + "' as pre,a0000,a0100,b0110,e01a1 from ");
                buf.append(str.substring(0, 3) + "a01 where a0100 ='" + str.substring(3) + "'");
                buf.append(") temp");
                dao.update(buf.toString());
                buf.setLength(0);
            }
            bo.configI9999(wid, templateid, time, dao);
            for (int i = 0; i < personList.size(); i++) {
                sql.setLength(0);
                sql.append(" update email_content set address=");
                sql.append("(select ");
                sql.append(emailfield);
                sql.append(" as address from (");
                String pre = (String) personList.get(i);
                sql.append(" select u.a0100,");
                sql.append("u." + emailfield);
                sql.append(" from ");
                sql.append(pre.substring(0, 3) + emailfieldset);
                sql.append(" u,email_content e where e.a0100=u.a0100 and e.a0100='" + pre.substring(3) + "' and e.pre='" + pre.substring(0, 3) + "' and e.id=" + templateid + " and e.wid='" + wid + "'");
                sql.append(" and e.I9999=(select I9999 from(select max(i9999) as I9999 from");
                sql.append(" email_content where email_content.a0100='" + pre.substring(3) + "' and ");
                sql.append("email_content.pre='" + pre.substring(0, 3) + "' and email_content.wid='" + wid + "' and ");
                sql.append("email_content.id=" + templateid + " and ");
                if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                    sql.append("to_char(email_content.send_time,'yyyy-mm-dd HH:mi:ss')");
                else
                    sql.append(" email_content.send_time");
                sql.append("='" + time + "') temp )) t ) where ");

                if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                    sql.append("to_char(email_content.send_time,'yyyy-mm-dd HH:mi:ss')");
                else
                    sql.append(" email_content.send_time");
                sql.append("='" + time + "' and email_content.id=" + templateid + " and email_content.a0100='" + pre.substring(3) + "' and email_content.pre = '" + pre.substring(0, 3) + "'");
                //System.out.println(sql.toString());
                dao.update(sql.toString());
                sql.setLength(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw GeneralExceptionHandler.Handle(e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getetoken(String nbase, String a0100, Connection conn) {
        AttestationUtils utils = new AttestationUtils();
        LazyDynaBean fieldbean = utils.getUserNamePassField();
        String username_field = (String) fieldbean.get("name");
        String password_field = (String) fieldbean.get("pass");
        StringBuffer sql = new StringBuffer("");
        sql.append("select a0101," + username_field + " username," + password_field + " password,a0101 from " + nbase + "A01");
        sql.append(" where a0100='" + a0100 + "'");
        ContentDAO dao = new ContentDAO(conn);
        String etoken = "";
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            String username = "";
            String password = "";
            if (rs.next()) {
                username = rs.getString("username");
                password = rs.getString("password");
            }
            etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username + "," + password));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return etoken;
    }

    /**
     * 得到预警结果人员
     *
     * @param wid
     * @param dao
     * @return
     */
    private static String getHrpwarn_resultUsernanme(String wid, ContentDAO dao) {
        String sql = "select nbase from Hrpwarn_result where wid='" + wid + "' group by nbase";
        RowSet rs = null;
        int count = 10;
        StringBuffer empStr = new StringBuffer();
        try {
            rs = dao.search(sql);
            ArrayList list = new ArrayList();
            while (rs.next()) {
                list.add(rs.getString("nbase"));
            }
            int r = 0;
            boolean isCorrect = true;
            for (int i = 0; i < list.size(); i++) {
                String nbase = (String) list.get(i);
                sql = "select a0101 from " + nbase + "A01 A where exists(select 1 from  Hrpwarn_result H where H.a0100=A.a0100 and H.nbase='" + nbase + "' and wid='" + wid + "')";
                rs = dao.search(sql);
                while (rs.next()) {
                    empStr.append(rs.getString("a0101"));
                    r++;
                    if (r > count) {
                        isCorrect = false;
                        break;
                    }
                    empStr.append(",");
                }
                if (!isCorrect)
                    break;
            }
            if (empStr.length() > 0)
                empStr.setLength(empStr.lastIndexOf(","));
            if (!isCorrect) {

                empStr.append("...");
            }
        } catch (Exception e) {
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

        return empStr.toString();
    }

    private static void SendWeixin(DynaBean db) {
        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) db.get(Key_HrpWarn_Ctrl_VO);
        // 获取预警对象用户ID
        ArrayList alUserIDList = getUserIDOrDeptIDList(vo.getStrDomain());
        //用于存储已发送用户的dbpre+a0100，屏蔽同一人拥有多个预警对象角色导致重复接收邮件问题 2014-02-14
        HashMap sendedPerson = new HashMap();
        if (alUserIDList != null && alUserIDList.size() > 0) {
            Connection conn = null;
            try {
                conn = AdminDb.getConnection();
                for (int i = 0; i < alUserIDList.size(); i++) {
                    DynaBean dBean = (LazyDynaBean) alUserIDList.get(i);
                    String a0100 = ((String) dBean.get("a0100")).toUpperCase();
                    if (sendedPerson.containsKey(a0100)) {
                        continue;
                    } else {
                        sendedPerson.put(a0100, null);
                    }
                    String username = (String) (dBean.get("username") == null ? "" : dBean.get("username"));

                    UserView userView = new UserView(username, conn);
                    /** 不检查口令，仅检查用户名 */
                    if (userView.canLogin(false)) {
                        ScanTotal st = new ScanTotal(userView);
                        StringBuffer names = new StringBuffer();
                        //int iTotal = st.getCount(db);
                        int iTotal = st.getCount(db, names, false);
                        if (iTotal > 0) {// 有实际人数的时候才发送消息？
                            String strEmail = ResourceFactory.getProperty("warn.weixin.msg").replace("{0}", ((String) dBean.get("userfullname")) + ((String) dBean.get("a0107"))).replace("{1}", db.get(Key_HrpWarn_FieldName_Msg) + "(共计" + iTotal + "人)").replace("{2}", names.toString());
                            //String strEmail = ResourceFactory.getProperty("warn.email.msg.html").replace("{0}", ((String) db.get(Key_HrpWarn_FieldName_Name))).replace("{1}", ((String)dBean.get("userfullname"))+((String)dBean.get("a0107"))).replace("{2}", db.get(Key_HrpWarn_FieldName_Msg) + "(共计"+ iTotal + "人)").replace("{3}", names.toString()).replace("{4}", PubFunc.FormatDate(new Date(),"yyyy-MM-dd"));
                            WeiXinBo.sendMsgToPerson(username, (String) db.get(Key_HrpWarn_FieldName_Name), strEmail, "http://www.hjsoft.com.cn:8089/UserFiles/Image/warn.png", "");
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                //throw GeneralExceptionHandler.Handle(e);
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * xus 17/4/19
     * 发送钉钉消息
     */
    private static void SendDingDIng(DynaBean db, List alBasePre) {
        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) db.get(Key_HrpWarn_Ctrl_VO);
        // 获取预警对象用户ID
        ArrayList alUserIDList = getUserIDOrDeptIDList(vo.getStrDomain());
        //用于存储已发送用户的dbpre+a0100，屏蔽同一人拥有多个预警对象角色导致重复接收邮件问题 2014-02-14
        HashMap sendedPerson = new HashMap();
        if (alUserIDList != null && alUserIDList.size() > 0) {
            Connection conn = null;
            try {
                conn = AdminDb.getConnection();
                for (int i = 0; i < alUserIDList.size(); i++) {
                    DynaBean dBean = (LazyDynaBean) alUserIDList.get(i);
                    String a0100 = ((String) dBean.get("a0100")).toUpperCase();
                    if (sendedPerson.containsKey(a0100)) {
                        continue;
                    } else {
                        sendedPerson.put(a0100, null);
                    }
                    String username = (String) (dBean.get("username") == null ? "" : dBean.get("username"));

                    UserView userView = new UserView(username, conn);
                    /** 不检查口令，仅检查用户名 */
                    if (userView.canLogin(false)) {
                        ScanTotal st = new ScanTotal(userView);
                        StringBuffer names = new StringBuffer();
                        //int iTotal = st.getCount(db);
                        int iTotal = st.getCount(db, names, false);
                        if (iTotal > 0) {// 有实际人数的时候才发送消息？
                            String strEmail = ResourceFactory.getProperty("warn.weixin.msg").replace("{0}", ((String) dBean.get("userfullname")) + ((String) dBean.get("a0107"))).replace("{1}", db.get(Key_HrpWarn_FieldName_Msg) + "(共计" + iTotal + "人)").replace("{2}", names.toString());
                            //String strEmail = ResourceFactory.getProperty("warn.email.msg.html").replace("{0}", ((String) db.get(Key_HrpWarn_FieldName_Name))).replace("{1}", ((String)dBean.get("userfullname"))+((String)dBean.get("a0107"))).replace("{2}", db.get(Key_HrpWarn_FieldName_Msg) + "(共计"+ iTotal + "人)").replace("{3}", names.toString()).replace("{4}", PubFunc.FormatDate(new Date(),"yyyy-MM-dd"));
                            DTalkBo.sendMessage(username, (String) db.get(Key_HrpWarn_FieldName_Name), strEmail, "", "");

                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                //throw GeneralExceptionHandler.Handle(e);
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static ArrayList getUserIDOrDeptIDList(String strDomain) {
        Connection conn = null;
        ArrayList userlist = new ArrayList();
        try {
            conn = AdminDb.getConnection();
            UserObjectBo user_bo = new UserObjectBo(conn);
            if (strDomain == null || strDomain.trim().length() < 3) {// length<3?因为“RL”“UN”等情况存在

            } else if (strDomain.startsWith("RL") || strDomain.contains(",RL")) {//zxj 20141031 有逗号开头的情况
                strDomain = strDomain.replaceAll("RL", "");
                String[] roles = strDomain.split(",");

                for (int i = 0; i < roles.length; i++) {
                    if ("".equals(roles[i]))
                        continue;

                    userlist.addAll(user_bo.findUserIdListByRoleId(roles[i]));
                }
            } else {
                //strDomain = strDomain.replaceAll("UN", "");
                //strDomain = strDomain.replaceAll("UM", "");
                //strDomain = strDomain.replaceAll("@K", "");
                String[] strOrgs = strDomain.split(",");
                for (int i = 0; i < strOrgs.length; i++) {
                    if ("".equals(strOrgs[i]))
                        continue;

                    String codesetid = strOrgs[i].substring(0, 2);
                    String codeitemid = strOrgs[i].substring(2);
                    userlist.addAll(user_bo.findUserListByOrgId(codeitemid, codesetid));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception ee) {

            }
        }
        return userlist;
    }

    /**
     * 对每个预警结果人发送微信
     *
     * @param dbean
     * @param alBasePre
     * @param userView
     * @throws Exception
     */
    public static void sendEveryWeixinMsg(DynaBean dbean, ArrayList alBasePre, UserView userView, String birthday_wid) throws Exception {
        String strWid = (String) dbean.get(Key_HrpWarn_FieldName_ID);
        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) dbean.get(Key_HrpWarn_Ctrl_VO);
        if (vo.getStrEveryone() == null || !"true".equalsIgnoreCase(vo.getStrEveryone()))
            return;
        String templateId = vo.getStrNote();
        if (templateId == null || templateId.length() <= 0)
            return;
        String sendspace = vo.getStrSendspace();
        if (sendspace == null || sendspace.length() <= 0)
            sendspace = "7";
        String sms_content_T = "t_sys_weixin_msg";
        String hrpwarn_T = "hrpwarn_result";
        StringBuffer sql = new StringBuffer();
        String cur_time = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
        Connection conn = null;
        RowSet rs = null;
        String loadname = "username";

        try {
            conn = AdminDb.getConnection();

            DbNameBo dbbo = new DbNameBo(conn);
            loadname = dbbo.getLogonUserNameField();
            EmailTemplateBo bo = new EmailTemplateBo(conn);
            ContentDAO dao = new ContentDAO(conn);
            ArrayList personList = new ArrayList();
            ArrayList list = bo.getTemplateFieldInfo(Integer.parseInt(templateId), 2);
            String msg = bo.getEmailContent(Integer.parseInt(templateId));
            String title = bo.getEmailTemplateSubject(templateId);
            //String msg = ""+ dbean.get(Key_HrpWarn_FieldName_Msg) ;
            StringBuffer buf = new StringBuffer();
            String z1 = cur_time.substring(0, 10) + " 00:00:00";
            String z3 = cur_time.substring(0, 10) + " 23:59:59";
            buf.append(" and (send_time>=" + Sql_switcher.dateValue(z1));
            buf.append(" and send_time<=" + Sql_switcher.dateValue(z3) + ")");
            switch (Sql_switcher.searchDbServer()) {
                case Constant.ORACEL: {
                    cur_time = "to_date('" + cur_time + "','yyyy-mm-dd  hh24:mi:ss')";
                    break;
                }
            }
            for (int i = 0; i < alBasePre.size(); i++) {
                HashMap tempmap = new HashMap();
                String strPre = (String) alBasePre.get(i);
                sql = new StringBuffer();
                sql.append(" select '" + strPre + "',h.a0100,a.a0101 as a0101,a." + loadname + " as loadname");
                sql.append(" from " + hrpwarn_T + " h," + strPre + "A01 a where h.nbase='" + strPre + "' and a.a0100=h.a0100 and h.wid=" + strWid + "");
                sql.append(" and a." + loadname + " in(select username from " + sms_content_T + " E where  E.wid='" + strWid + "'");
                if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                    sql.append(" and " + Sql_switcher.diffDays("" + cur_time + "", "(select max(send_time) from " + sms_content_T + " F where  F.wid='" + strWid + "')") + ">" + (Integer.parseInt(sendspace)) + ")");
                else
                    sql.append(" and " + Sql_switcher.diffDays("'" + cur_time + "'", "(select max(send_time) from " + sms_content_T + " F where  F.wid='" + strWid + "')") + ">" + (Integer.parseInt(sendspace)) + ")");

                sql.append(" and a." + loadname + " is not null ");
                //System.out.println(sql.toString());
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    LazyDynaBean mBean = new LazyDynaBean();
                    mBean.set("sender", userView.getUserFullName());//发送者姓名
                    mBean.set("receiver", rs.getString("a0101"));//接收人姓名
                    mBean.set("pera0100", strPre + rs.getString("a0100"));
                    String cont = bo.getFactContent(msg, (String) mBean.get("pera0100"), list, userView);
                    mBean.set("msg", cont);//信息
                    mBean.set("wid", strWid);//预警编号
                    mBean.set("username", rs.getString("loadname"));//登陆名称
                    tempmap.put(rs.getString("a0100") + rs.getString("loadname"), "");
                    personList.add(mBean);
                }
                sql.delete(0, sql.length());
                sql.append(" select '" + strPre + "',h.a0100,a.a0101 as a0101,a." + loadname + " as loadname");
                sql.append(" from " + hrpwarn_T + " h," + strPre + "A01 a where h.nbase='" + strPre + "' and a.a0100=h.a0100 and h.wid=" + strWid + "");
                sql.append(" and a." + loadname + " not in(select username from " + sms_content_T + " E where  E.wid='" + strWid + "'");
                sql.append(" " + buf.toString());
                sql.append(")");
                sql.append(" and a." + loadname + " is not null ");
                //System.out.println(sql.toString());
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    if (tempmap.containsKey(rs.getString("a0100") + rs.getString("loadname"))) {
                        continue;
                    }
                    LazyDynaBean mBean = new LazyDynaBean();
                    mBean.set("sender", userView.getUserFullName());//发送者姓名
                    mBean.set("receiver", rs.getString("a0101"));//接收人姓名
                    mBean.set("pera0100", strPre + rs.getString("a0100"));
                    String cont = bo.getFactContent(msg, (String) mBean.get("pera0100"), list, userView);
                    mBean.set("msg", cont);//信息
                    mBean.set("wid", strWid);//预警编号
                    mBean.set("username", rs.getString("loadname"));//登陆名称
                    personList.add(mBean);
                }
            }
            if (birthday_wid != null && birthday_wid.equals(strWid)) {
                sendBirthMsg(title, personList);
            } else {
                sendWeixinMsg(dao, title, personList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (Exception ee) {

            }
        }
    }

    /**
     * 对每个预警结果人发送钉钉
     *
     * @param dbean
     * @param alBasePre
     * @param userView
     * @throws Exception
     */
    public static void sendEveryDDMsg(DynaBean dbean, ArrayList alBasePre, UserView userView, String birthday_wid) throws Exception {
        String strWid = (String) dbean.get(Key_HrpWarn_FieldName_ID);
        ConfigCtrlInfoVO vo = (ConfigCtrlInfoVO) dbean.get(Key_HrpWarn_Ctrl_VO);
        if (vo.getStrEveryone() == null || !"true".equalsIgnoreCase(vo.getStrEveryone()))
            return;
        String templateId = vo.getStrNote();
        if (templateId == null || templateId.length() <= 0)
            return;
        String sendspace = vo.getStrSendspace();
        if (sendspace == null || sendspace.length() <= 0)
            sendspace = "7";
        String sms_content_T = "t_sys_dingtalk_msg";
        String hrpwarn_T = "hrpwarn_result";
        StringBuffer sql = new StringBuffer();
        String cur_time = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss");
        Connection conn = null;
        RowSet rs = null;
        String loadname = "username";
        try {
            conn = AdminDb.getConnection();
            //xus 17/4/20 判断如果不存在t_sys_dingtalk_msg表 则创建此表
            DbWizard dbw = new DbWizard(conn);
            if (!dbw.isExistTable("t_sys_dingtalk_msg", false)) {
                DTalkBo.createDDTableInfo("t_sys_dingtalk_msg", conn);
            }

            DbNameBo dbbo = new DbNameBo(conn);
            loadname = dbbo.getLogonUserNameField();
            EmailTemplateBo bo = new EmailTemplateBo(conn);
            ContentDAO dao = new ContentDAO(conn);
            ArrayList personList = new ArrayList();
            ArrayList list = bo.getTemplateFieldInfo(Integer.parseInt(templateId), 2);
            String msg = bo.getEmailContent(Integer.parseInt(templateId));
            String title = bo.getEmailTemplateSubject(templateId);
            //String msg = ""+ dbean.get(Key_HrpWarn_FieldName_Msg) ;
            StringBuffer buf = new StringBuffer();
            String z1 = cur_time.substring(0, 10) + " 00:00:00";
            String z3 = cur_time.substring(0, 10) + " 23:59:59";
            buf.append(" and (send_time>=" + Sql_switcher.dateValue(z1));
            buf.append(" and send_time<=" + Sql_switcher.dateValue(z3) + ")");
            switch (Sql_switcher.searchDbServer()) {
                case Constant.ORACEL: {
                    cur_time = "to_date('" + cur_time + "','yyyy-mm-dd  hh24:mi:ss')";
                    break;
                }
            }
            for (int i = 0; i < alBasePre.size(); i++) {
                HashMap tempmap = new HashMap();
                String strPre = (String) alBasePre.get(i);
                sql = new StringBuffer();
                sql.append(" select '" + strPre + "',h.a0100,a.a0101 as a0101,a." + loadname + " as loadname");
                sql.append(" from " + hrpwarn_T + " h," + strPre + "A01 a where h.nbase='" + strPre + "' and a.a0100=h.a0100 and h.wid=" + strWid + "");
                sql.append(" and a." + loadname + " in(select username from " + sms_content_T + " E where  E.wid='" + strWid + "'");
                if (Sql_switcher.searchDbServer() == Constant.ORACEL)
                    sql.append(" and " + Sql_switcher.diffDays("" + cur_time + "", "(select max(send_time) from " + sms_content_T + " F where  F.wid='" + strWid + "')") + ">" + (Integer.parseInt(sendspace)) + ")");
                else
                    sql.append(" and " + Sql_switcher.diffDays("'" + cur_time + "'", "(select max(send_time) from " + sms_content_T + " F where  F.wid='" + strWid + "')") + ">" + (Integer.parseInt(sendspace)) + ")");

                sql.append(" and a." + loadname + " is not null ");
                //System.out.println(sql.toString());
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    LazyDynaBean mBean = new LazyDynaBean();
                    mBean.set("sender", userView.getUserFullName());//发送者姓名
                    mBean.set("receiver", rs.getString("a0101"));//接收人姓名
                    mBean.set("pera0100", strPre + rs.getString("a0100"));
                    String cont = bo.getFactContent(msg, (String) mBean.get("pera0100"), list, userView);
                    mBean.set("msg", cont);//信息
                    mBean.set("wid", strWid);//预警编号
                    mBean.set("username", rs.getString("loadname"));//登陆名称
                    tempmap.put(rs.getString("a0100") + rs.getString("loadname"), "");
                    personList.add(mBean);
                }
                sql.delete(0, sql.length());
                sql.append(" select '" + strPre + "',h.a0100,a.a0101 as a0101,a." + loadname + " as loadname");
                sql.append(" from " + hrpwarn_T + " h," + strPre + "A01 a where h.nbase='" + strPre + "' and a.a0100=h.a0100 and h.wid=" + strWid + "");
                sql.append(" and a." + loadname + " not in(select username from " + sms_content_T + " E where  E.wid='" + strWid + "'");
                sql.append(" " + buf.toString());
                sql.append(")");
                sql.append(" and a." + loadname + " is not null ");
                //System.out.println(sql.toString());
                rs = dao.search(sql.toString());
                while (rs.next()) {
                    if (tempmap.containsKey(rs.getString("a0100") + rs.getString("loadname"))) {
                        continue;
                    }
                    LazyDynaBean mBean = new LazyDynaBean();
                    mBean.set("sender", userView.getUserFullName());//发送者姓名
                    mBean.set("receiver", rs.getString("a0101"));//接收人姓名
                    mBean.set("pera0100", strPre + rs.getString("a0100"));
                    String cont = bo.getFactContent(msg, (String) mBean.get("pera0100"), list, userView);
                    mBean.set("msg", cont);//信息
                    mBean.set("wid", strWid);//预警编号
                    mBean.set("username", rs.getString("loadname"));//登陆名称
                    personList.add(mBean);
                }
            }
            if (birthday_wid != null && birthday_wid.equals(strWid)) {
                sendDDBirthMsg(title, personList, alBasePre);
            } else {
                sendDingDingMsg(dao, title, personList, alBasePre);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(conn);
        }
    }

    private static void sendBirthMsg(String title, ArrayList personList) {

        int lssize = personList.size();
        for (int i = 0; i < lssize; i++) {
            LazyDynaBean mBean = (LazyDynaBean) personList.get(i);
            String username = (String) mBean.get("username");
            String msg_content = (String) mBean.get("msg");
            String txtcontent = msg_content.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
            //  txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符
            boolean flag = WeiXinBo.sendMsgToPerson(username, title, txtcontent, "http://www.hjsoft.com.cn:8089/UserFiles/Image/birth_cookie.jpg", "");
        }
    }

    //钉钉发送生日消息
    private static void sendDDBirthMsg(String title, ArrayList personList, List alBasePre) {

        int lssize = personList.size();
        for (int i = 0; i < lssize; i++) {
            LazyDynaBean mBean = (LazyDynaBean) personList.get(i);
            String username = (String) mBean.get("username");
            String msg_content = (String) mBean.get("msg");
            String txtcontent = msg_content.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
            //  txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符
//				boolean flag = WeiXinBo.sendMsgToPerson(username, title, txtcontent, "http://www.hjsoft.com.cn:8089/UserFiles/Image/birth_cookie.jpg", "");
            //人员库设置为空（默认Usr）
            DTalkBo.sendMessage(username, title, CommonUtil.delHTMLTag(msg_content), "", "");
        }
    }

    private static void sendWeixinMsg(ContentDAO dao, String title, ArrayList personList) {
        try {
            int lssize = personList.size();
            java.sql.Date start_date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start_date1 = sdf.format(new java.util.Date());
            try {
                start_date = new java.sql.Date(sdf.parse(start_date1).getTime());
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            for (int i = 0; i < lssize; i++) {
                LazyDynaBean mBean = (LazyDynaBean) personList.get(i);
                String username = (String) mBean.get("username");
                String msg_content = (String) mBean.get("msg");
                boolean flag = WeiXinBo.sendMsgToPerson(username, title, msg_content, "http://www.hjsoft.com.cn:8089/UserFiles/Image/warn.png", "");
                if (flag) {
                    String zj = CreateSequence.getUUID();
                    RecordVo vo = new RecordVo("t_sys_weixin_msg");
                    vo.setString("weixin_msg_id", CreateSequence.getUUID());
                    vo.setString("sender", (String) mBean.get("sender"));
                    vo.setString("receiver", (String) mBean.get("receiver"));
                    vo.setString("msg_content", (String) mBean.get("msg"));
                    vo.setString("username", username);
                    vo.setString("wid", (String) mBean.get("wid"));
                    vo.setDate("send_time", new Date());
                    dao.addValueObject(vo);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * xus 17/4/19
     * 发送丁丁消息
     */
    private static void sendDingDingMsg(ContentDAO dao, String title, ArrayList personList, List alBasePre) {
        try {
            int lssize = personList.size();
            java.sql.Date start_date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start_date1 = sdf.format(new java.util.Date());
            try {
                start_date = new java.sql.Date(sdf.parse(start_date1).getTime());
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            for (int i = 0; i < lssize; i++) {
                LazyDynaBean mBean = (LazyDynaBean) personList.get(i);
                String username = (String) mBean.get("username");
                String msg_content = (String) mBean.get("msg");
                String preA0100 = (String) mBean.get("pera0100");
                String A0100 = preA0100.substring(3);
                String Nbase = preA0100.substring(0, 3);
//				System.out.println(A0100+Nbase);
//				return;
                boolean flag = DTalkBo.sendMessage(A0100, Nbase, title, CommonUtil.delHTMLTag(msg_content), "", "");
                if (flag) {
                    String zj = CreateSequence.getUUID();
                    RecordVo vo = new RecordVo("t_sys_dingtalk_msg");
                    vo.setString("dingtalk_msg_id", CreateSequence.getUUID());
                    vo.setString("sender", (String) mBean.get("sender"));
                    vo.setString("receiver", (String) mBean.get("receiver"));
                    vo.setString("msg_content", (String) mBean.get("msg"));
                    vo.setString("username", username);
                    vo.setString("wid", (String) mBean.get("wid"));
                    vo.setDate("send_time", new Date());
                    dao.addValueObject(vo);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
