package com.hjsj.hrms.module.kq.kqdata.businessobject.util;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.kq.config.item.businessobject.KqItemService;
import com.hjsj.hrms.module.kq.config.item.businessobject.impl.KqItemServiceImpl;
import com.hjsj.hrms.module.kq.config.period.businessobject.PeriodService;
import com.hjsj.hrms.module.kq.config.period.businessobject.impl.PeriodServiceImpl;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.SchemeMainService;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.impl.SchemeMainServiceImpl;
import com.hjsj.hrms.module.kq.config.shifts.businessobject.ShiftsService;
import com.hjsj.hrms.module.kq.config.shifts.businessobject.impl.ShiftsServiceImpl;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.sendmessage.email.SendEmailUtil;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.StringReader;
import java.sql.Connection;
import java.util.*;

public class KqDataUtil {
    /**
     * 人事处考勤员
     */
    public static final int role_Clerk = 1;
    /**
     * 人事处审核人
     */
    public static final int role_Reviewer = 2;
    /**
     * 下级机构考勤员
     */
    public static final int role_Agency_Clerk = 3;
    /**
     * 下级机构审核人
     */
    public static final int role_Agency_Reviewer = 4;


    /**
     * 考勤填报待办
     */
    public static final int TASKTYPE_FILL = 1;
    /**
     * 考勤审批待办
     */
    public static final int TASKTYPE_SP = 2;
    /**
     * 考勤确认待办
     */
    public static final int TASKTYPE_CONFIRM = 3;

    /**
     * 考勤退回待办
     */
    public static final int TASKTYPE_SP_BACK = 4;
    
    /**
     * 考勤待办类型标识
     */
    private final String KQ_TASK_TYPE = "K";


    private UserView userView;
    private Connection conn;


    public KqDataUtil(UserView userView) {
        this.setUserView(userView);
    }
    
    public KqDataUtil(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
    }


    /**
     * 获取当前用户身份
     *
     * @param connection
     * @param scheme_id  考勤方案id
     * @param viewType   页面区分 0 考勤上报 1考勤审批
     * @return KqDataUtil.role_开头的静态变量
     * @throws GeneralException
     * @author ZhangHua
     * @date 21:07 2018/11/6
     */
    public int getKqRole(Connection connection, String scheme_id, String viewType) throws GeneralException {
        SchemeMainService schemeMainService = new SchemeMainServiceImpl(connection, this.getUserView());
        try {
            ArrayList parameterList = new ArrayList();
            parameterList.add(scheme_id);
            ArrayList<LazyDynaBean> schemeList = schemeMainService.listKq_scheme(" And scheme_id=? ", parameterList, "");
            return this.getKqRole(viewType, schemeList.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }


    /**
     * 获取当前用户身份
     *
     * @param viewType   页面区分 0 考勤上报 1考勤审批
     * @param schemeBean 考勤方案公共bean
     * @return KqDataUtil.role_开头的静态变量 0则没有身份
     * @throws GeneralException
     * @author ZhangHua
     * @date 21:07 2018/11/6
     */
    public int getKqRole(String viewType, LazyDynaBean schemeBean) throws GeneralException {

        try {
            String userName = "n";
            if (StringUtils.isNotBlank(this.getUserView().getDbname()) && StringUtils.isNotBlank(this.getUserView().getA0100())) {
                userName = this.getUserView().getDbname() + this.getUserView().getA0100();
            }

            HashMap schemeMap = (HashMap) schemeBean.getMap();
            String clerk = String.valueOf(schemeMap.get("clerk_username"));


            if ("1".equals(viewType)) {
                if (this.getUserView().getUserName().equalsIgnoreCase(clerk)) {
                    return KqDataUtil.role_Clerk;
                }

                if (schemeMap.containsKey("reviewer_id") && userName.equalsIgnoreCase(String.valueOf(schemeMap.get("reviewer_id")))) {
                    return KqDataUtil.role_Reviewer;
                }
            } else {
                ArrayList<HashMap> orgMap = (ArrayList<HashMap>) schemeMap.get("org_map");

                for (HashMap org : orgMap) {
                    if (org.containsKey("y_clerk_username") && this.getUserView().getUserName().equalsIgnoreCase(String.valueOf(org.get("y_clerk_username")))) {
                        return KqDataUtil.role_Agency_Clerk;
                    }
                    if (org.containsKey("reviewer_id") && userName.equalsIgnoreCase(String.valueOf(org.get("reviewer_id")))) {
                        return KqDataUtil.role_Agency_Reviewer;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return 0;
    }

    /**
     * 获取当前用户报批下一个节点的身份和用户id
     *
     * @param viewType   页面区分 0 考勤上报 1考勤审批
     * @param schemeBean 考勤方案公共bean
     * @param role       当前用户身份
     * @param org_id
     * @return LazyDynaBean  role:(int)身份 username:(String)用户名 若role为空 则没有下级审批人了
     * @throws GeneralException
     * @author ZhangHua
     * @date 21:10 2018/11/6
     */
    public LazyDynaBean getKqRoleNextLevel(String viewType, LazyDynaBean schemeBean, int role, String org_id) throws GeneralException {
        LazyDynaBean dataBean = new LazyDynaBean();


        try {
            String a0100=this.getUserView().getDbname()+this.getUserView().getA0100();
            String userName="";
            if(this.getUserView().getStatus()==0){
                userName=this.getUserView().getUserName();
            }else if(StringUtils.isNotBlank(this.getUserView().getS_userName())){
                userName=this.getUserView().getS_userName();
            }



            HashMap schemeMap = (HashMap) schemeBean.getMap();
            ArrayList<HashMap> orgMap = (ArrayList<HashMap>) schemeMap.get("org_map");
            String secondary_admin="1";
            if(schemeBean.getMap().containsKey("secondary_admin")&&"0".equals((String)schemeBean.get("secondary_admin"))){
                secondary_admin="0";
            }

            switch (role) {

                case role_Agency_Clerk: {
                    if(StringUtils.isNotBlank(userName)) {
                        for (HashMap org : orgMap) {
                            if (((String) org.get("org_id")).equalsIgnoreCase(org_id)) {
                                if (org.containsKey("y_clerk_username") && userName.equalsIgnoreCase(String.valueOf(org.get("y_clerk_username")))
                                        && org.containsKey("reviewer_id") && StringUtils.isNotBlank((String) org.get("reviewer_id"))) {
                                    if(StringUtils.isNotBlank(a0100)&&a0100.equalsIgnoreCase((String)org.get("reviewer_id"))){
                                        continue;
                                    }
                                    dataBean.set("role", String.valueOf(role_Agency_Reviewer));
                                    dataBean.set("username", org.get("reviewer_id"));
                                    return dataBean;
                                }
                            }
                        }
                    }
                }
                case role_Agency_Reviewer: {

                    if("0".equals(secondary_admin)){
                        dataBean.set("role", "");
                        dataBean.set("username", "");
                        return dataBean;
                    }


                    if (schemeMap.containsKey("clerk_username") && StringUtils.isNotBlank((String) schemeMap.get("clerk_username"))) {
                        dataBean.set("role", String.valueOf(role_Clerk));
                        dataBean.set("username", schemeMap.get("clerk_username"));
                        return dataBean;
                    }
                }
                case role_Clerk: {
                    if (schemeMap.containsKey("reviewer_id") && StringUtils.isNotBlank((String) schemeMap.get("reviewer_id"))) {

//                        if(StringUtils.isBlank(a0100)||!a0100.equalsIgnoreCase((String) schemeMap.get("reviewer_id"))) {
                            dataBean.set("role", String.valueOf(KqDataUtil.role_Reviewer));
                            dataBean.set("username", schemeMap.get("reviewer_id"));
                            return dataBean;
//                        }
                    }
                }
                default: {
                    dataBean.set("role", "");
                    dataBean.set("username", "");
                    return dataBean;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 获取当前用户的前一个节点的身份和用户id
     *
     * @param scheme_id  考勤方案id
     * @param viewType   页面区分 0 考勤上报 1考勤审批
     * @param schemeBean 考勤方案公共bean
     * @return LazyDynaBean  role:(int)身份 username:(String)用户名 若role为空 则没有下级审批人了
     * @throws GeneralException
     * @author Haosl
     * @date 21:10 2018/11/6
     */
    public LazyDynaBean getKqRolePreviousLevel(String scheme_id, String viewType, LazyDynaBean schemeBean, String orgId) throws GeneralException {
        LazyDynaBean dataBean = new LazyDynaBean();

        try {

            HashMap schemeMap = (HashMap) schemeBean.getMap();
            ArrayList<HashMap> orgMap = (ArrayList<HashMap>) schemeMap.get("org_map");
            int role = this.getKqRole(viewType, schemeBean);
            switch (role) {
                case role_Agency_Reviewer: {
                    for (HashMap org : orgMap) {
                        if (!orgId.equals(String.valueOf(org.get("org_id"))))
                            continue;
                        if (org.containsKey("y_clerk_username") && StringUtils.isNotBlank(String.valueOf(org.get("y_clerk_username")))) {
                            dataBean.set("role", role_Agency_Clerk);
                            dataBean.set("username", org.get("y_clerk_username"));
                            return dataBean;
                        }
                    }
                }
                case role_Clerk: {
                    for (HashMap org : orgMap) {
                        if (!orgId.equals(String.valueOf(org.get("org_id"))))
                            continue;
                        if (org.containsKey("reviewer_id") &&
                                StringUtils.isNotBlank(String.valueOf(org.get("reviewer_id")))) {
                            dataBean.set("role", role_Agency_Reviewer);
                            dataBean.set("username", org.get("reviewer_id"));
                            return dataBean;
                        } else if (org.containsKey("y_clerk_username") && StringUtils.isNotBlank(String.valueOf(org.get("y_clerk_username")))) {
                            dataBean.set("role", role_Agency_Clerk);
                            dataBean.set("username", String.valueOf(org.get("y_clerk_username")));
                            return dataBean;
                        }
                    }
                }
                case role_Reviewer: {
                    if (schemeMap.containsKey("clerk_username") && StringUtils.isNotBlank((String) schemeMap.get("clerk_username"))) {
                        dataBean.set("role", KqDataUtil.role_Clerk);
                        dataBean.set("username", String.valueOf(schemeMap.get("clerk_username")));
                        return dataBean;
                    }
                }
                default: {
                    dataBean.set("role", "");
                    dataBean.set("username", "");
                    return dataBean;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 生成功能导航菜单的json串
     *
     * @param name 菜单名
     * @param id   菜单id
     * @param list 菜单功能集合
     * @return
     */
    public static String getMenuStr(String name, String id, ArrayList list) {
        StringBuffer str = new StringBuffer();
        try {
            if (name.length() > 0) {
                str.append("<jsfn>{xtype:'button',text:'" + name + "'");
            }
            if (StringUtils.isNotBlank(id)) {
                str.append(",id:'");
                str.append(id);
                str.append("'");
            }
            str.append(",menu:{items:[");
            for (int i = 0; i < list.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) list.get(i);
                if (i != 0)
                    str.append(",");
                str.append("{");
                if (bean.get("xtype") != null && bean.get("xtype").toString().length() > 0)
                    str.append("xtype:'" + bean.get("xtype") + "'");
                if (bean.get("text") != null && bean.get("text").toString().length() > 0)
                    str.append("text:'" + bean.get("text") + "'");
                if (bean.get("handler") != null && bean.get("handler").toString().length() > 0) {
                    if (bean.get("xtype") != null && "datepicker".equalsIgnoreCase(bean.get("xtype").toString())) {//时间控件单独处理一下 方法GzGlobal.aaa(picker, date)这样写
                        str.append(",handler:function(picker, date){" + bean.get("handler") + ";}");
                    } else {
                        str.append(",handler:function(){" + bean.get("handler") + ";}");
                    }
                }
                String menuId = (String) bean.get("id");

                if (menuId != null && menuId.length() > 0)//人事异动-手工选择按钮需要id（gaohy）
                    str.append(",id:'" + menuId + "'");
                else
                    menuId = "";
                if (bean.get("icon") != null && bean.get("icon").toString().length() > 0)
                    str.append(",icon:'" + bean.get("icon") + "'");
                if (bean.get("value") != null && bean.get("value").toString().length() > 0)
                    str.append(",value:" + bean.get("value") + "");
                ArrayList menulist = (ArrayList) bean.get("menu");
                if (menulist != null && menulist.size() > 0) {
                    str.append(getMenuStr("", menuId, menulist));
                }
                str.append("}");
            }
            str.append("]}");
            if (name.length() > 0) {
                str.append("}</jsfn>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }


    /**
     * 考勤发送待办公共方法
     *
     * @param conn
     * @param title        待办标题
     * @param scheme_id    考勤方案id
     * @param kq_year      考勤年份
     * @param kq_duration  考勤期间
     * @param org_id       归属单位
     * @param taskType     发送方式 关联taskType开头常量
     * @param role         发送人身份 关联role开头常量
     * @param receiverList 接收人 自助用户人员库加a0100 如 usr0000001 业务用户为账号
     * @param schemeBean   审批代办时用到，别的代办可以不穿
     * @throws GeneralException
     * @author ZhangHua
     * @date 18:31 2018/11/17
     */
    public void kqSendPengdingTask(Connection conn, String title, int scheme_id, String kq_year, String kq_duration, String org_id, int taskType
    		, int role, ArrayList<String> receiverList, LazyDynaBean schemeBean) throws GeneralException {

        ContentDAO dao = new ContentDAO(conn);
        try {
            String pending_url = "/module/utils/jsp.do?br_query=link&param=";
            String kq_url = "";
            String ext_flag = "";
            if (taskType != KqDataUtil.TASKTYPE_CONFIRM && StringUtils.isEmpty(org_id))
                return;
            String org_id_e = PubFunc.encrypt(org_id);
            switch (taskType) {
                //填报待办
                case KqDataUtil.TASKTYPE_FILL: {
                    ext_flag = "KQ_FILL_" + kq_year + kq_duration + "_" + scheme_id + "_" + org_id_e;
                    kq_url = "/module/kq/kqdata/KqDataMxForPendingTask.html?schemeId=" + PubFunc.encrypt(String.valueOf(scheme_id)) + "&kqYear=" + kq_year + "&kqDuration=" + kq_duration;
                    kq_url += "&fromflag=pt&orgId=" + org_id_e + "&viewType=0&operation=1&optRole=" + KqDataUtil.role_Agency_Clerk;
                    kq_url += "&sp_flag=01";
                }
                break;
                //审批待办
                case KqDataUtil.TASKTYPE_SP: {
                	// linbz 优化流程  驳回特殊处理  接受 用户id`角色（1234）
                    String reviewers = receiverList.get(0);
                    String[] users = StringUtils.split(reviewers, "`");
                    String reviewerId = users[0];
                    int reviewerRole = Integer.parseInt(users[1]);
                    String viewType = "0";
                    // 人事处的就是1 就是审批
                    if(reviewerRole==KqDataUtil.role_Clerk || reviewerRole==KqDataUtil.role_Reviewer)
                    	viewType = "1";
                    // 重新拼list下面添加待办任务用
                    receiverList.clear();
                    receiverList.add(reviewerId);
                    // linbz 是否有下级审批人
                    boolean isNextLevel = isKqRoleNextLevel(viewType, schemeBean, reviewerRole, org_id);
                    String hasNextApprover = isNextLevel ? "1" : "0";//是否有下级审批人，用于判断显示报批 | 批准
                    ext_flag = "KQ_SP_" + kq_year + kq_duration + "_" + scheme_id + "_" + org_id_e;
                    kq_url = "/module/kq/kqdata/KqDataMxForPendingTask.html?schemeId=" + PubFunc.encrypt(String.valueOf(scheme_id)) + "&kqYear=" + kq_year + "&kqDuration=" + kq_duration;
                    kq_url += "&hasNextApprover=" + hasNextApprover + "&fromflag=pt&orgId=" + org_id_e + "&viewType=" + viewType + "&operation=1&optRole=" + reviewerRole;
                    kq_url += "&sp_flag=02";
                }
                break;
                //驳回待办
                case KqDataUtil.TASKTYPE_SP_BACK: {
                	// linbz 优化流程  驳回特殊处理  接受 用户id`角色（1234）
                    String reviewers = receiverList.get(0);
                    String[] users = StringUtils.split(reviewers, "`");
                    String reviewerId = users[0];
                    int reviewerRole = Integer.parseInt(users[1]);
                    String viewType = "0";
                    // 人事处的就是1 就是审批
                    if(reviewerRole==KqDataUtil.role_Clerk || reviewerRole==KqDataUtil.role_Reviewer)
                    	viewType = "1";
                    // 重新拼list下面添加待办任务用
                    receiverList.clear();
                    receiverList.add(reviewerId);
                    
                    ext_flag = reviewerRole == KqDataUtil.role_Agency_Clerk ? "KQ_FILL_" : "KQ_SP_";
                    ext_flag += kq_year + kq_duration + "_" + scheme_id + "_" + org_id_e;
                    kq_url = "/module/kq/kqdata/KqDataMxForPendingTask.html?schemeId=" + PubFunc.encrypt(String.valueOf(scheme_id)) + "&kqYear=" + kq_year + "&kqDuration=" + kq_duration;
                    kq_url += "&fromflag=pt&orgId=" + org_id_e + "&viewType=" + viewType + "&operation=1&optRole=" + reviewerRole;
                    kq_url += "&sp_flag=07";
                }
                break;
                //确认待办
                case KqDataUtil.TASKTYPE_CONFIRM: {
                	// 51293 下发本人确认待办  机构参数应该是orgId （org_id由于之前wb确认待办参数写错导致）
                    kq_url = "/kq/kqdata/kqdataconfirm?schemeId=" + PubFunc.encrypt(String.valueOf(scheme_id)) + "&kq_year=" + kq_year + "&kq_duration=" + kq_duration 
                    		+ "&orgId=" + PubFunc.encrypt(org_id) + "&label=show";
                    ext_flag = "KQ_CONFIRM_" + kq_year + kq_duration + "_" + scheme_id + "_" + org_id_e;
                }
                break;
                default:
            }

            String sender = "";
            if (role == KqDataUtil.role_Clerk || role == KqDataUtil.role_Agency_Clerk) {
                sender = this.getUserView().getUserName();
            } else {
                sender = this.getUserView().getDbname() + this.getUserView().getA0100();
            }

            HashSet a0100Set = this.listUnFinishPengdingTask(dao, ext_flag);

            pending_url = pending_url + SafeCode.encode(kq_url);
            ArrayList<ArrayList> dataList = new ArrayList<ArrayList>();
            StringBuffer strSql = new StringBuffer();
            strSql.append(" insert into t_hr_pendingtask (pending_id ,pending_title ,pending_url ,pending_status ,");
            strSql.append("pending_level ,pending_type ,receiver ,sender ,lasttime ,create_time  ,ext_flag ,");
            strSql.append("bread) ");
            strSql.append(" values (?,?,?,0,1,30,?,?,");
            strSql.append(Sql_switcher.sqlNow());
            strSql.append(",");
            strSql.append(Sql_switcher.sqlNow());
            strSql.append(",?,0");
            strSql.append(")");
            IDGenerator idGenerator = new IDGenerator(2, conn);
            PendingTask pe = new PendingTask();

            for (String receiver : receiverList) {
                if (a0100Set.contains(receiver.toLowerCase())) {
                    continue;
                }
                ArrayList list = new ArrayList();
                int pending_id = Integer.parseInt(idGenerator.getId("pengdingTask.pengding_id"));
                list.add(pending_id);
                list.add(title);
                list.add(pending_url);
                list.add(receiver);
                list.add(sender);
                list.add(ext_flag);
                dataList.add(list);
            }
            if (dataList.size() > 0) {
                dao.batchUpdate(strSql.toString(), dataList);

                for (ArrayList list : dataList) {
                    pe.insertPending(String.valueOf(list.get(0)), this.KQ_TASK_TYPE, title, sender, (String) list.get(3), pending_url, 0, 1, "30", this.getUserView());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 完成考勤待办公共方法
     * （认为 考勤员必须为业务用户，审批人必须为自助用户）
     *
     * @param conn
     * @param scheme_id    考勤方案id
     * @param kq_year      考勤年份
     * @param kq_duration  考勤期间
     * @param taskType     发送方式 关联taskType开头常量
     * @param role         发送人身份 关联role开头常量
     * @param receiverList 接收人 自助用户人员库加a0100 如 usr0000001 业务用户为账号
     *                     (如果完成填报和报批的待办，本方法都将默认在receiverList中加入当前用户以清除自己的待办)
     * @throws GeneralException
     * @author ZhangHua
     * @date 18:33 2018/11/17
     */
    public void kqFinishPengdingTask(Connection conn, int scheme_id, String kq_year, String kq_duration, int taskType, int role
    		, ArrayList<String> receiverList, String orgId) throws GeneralException {
        String ext_flag = "";
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            String orgId_e = PubFunc.encrypt(orgId);
            String myUserName = "";
            if (role == KqDataUtil.role_Clerk || role == KqDataUtil.role_Agency_Clerk) {
                myUserName = this.getUserView().getUserName();
            } else {
                myUserName = this.getUserView().getDbname() + this.getUserView().getA0100();
            }
            switch (taskType) {
                case KqDataUtil.TASKTYPE_FILL: {
                    ext_flag = "KQ_FILL_" + kq_year + kq_duration + "_" + scheme_id + "_" + orgId_e;
                    if (receiverList == null) {
                        receiverList = new ArrayList<String>();
                    }
                    receiverList.add(myUserName);
                }
                break;
                case KqDataUtil.TASKTYPE_SP: {
                    ext_flag = "KQ_SP_" + kq_year + kq_duration + "_" + scheme_id + "_" + orgId_e;
                    if (receiverList == null) {
                        receiverList = new ArrayList<String>();
                    }
                    receiverList.add(myUserName);
                }
                break;
                case KqDataUtil.TASKTYPE_CONFIRM: {
                    ext_flag = "KQ_CONFIRM_" + kq_year + kq_duration + "_" + scheme_id + "_" + orgId_e;
                }
                break;
                default:
            }

            if (receiverList.size() == 0)
                return;

            ArrayList<Integer> idList = new ArrayList<Integer>();
            StringBuffer strSql = new StringBuffer();
            strSql.append("select pending_id from t_hr_pendingtask");
    		strSql.append(" where ext_flag='").append(ext_flag).append("'");
    		strSql.append(" and receiver in ( ");
    		
            StringBuffer strWhere = new StringBuffer();
            ArrayList list = new ArrayList();
            
            for (int i = 0; i < receiverList.size(); i++) {
                strWhere.append("?,");
                list.add(receiverList.get(i));
                if (i != 0 && i % 300 == 0) {
                    if (strWhere.length() > 0) {
                        strWhere.deleteCharAt(strWhere.length() - 1);
                        strWhere.append(")");
                    }

                    rs = dao.search(strSql.toString() + strWhere.toString(), receiverList);
                    while (rs.next()) {
                        idList.add(rs.getInt("pending_id"));
                    }

                    strWhere.setLength(0);
                    list.clear();
                }
            }
            if (strWhere.length() > 0) {
                strWhere.deleteCharAt(strWhere.length() - 1);
                strWhere.append(")");
            }

            if (list.size() == 0)
                return;
            rs = dao.search(strSql.toString() + strWhere.toString(), list);
            while (rs.next()) {
                idList.add(rs.getInt("pending_id"));
            }
            PendingTask pe = new PendingTask();
            strSql.setLength(0);
            strSql.append(" update t_hr_pendingtask set pending_status=1 where pending_id in(");
            strWhere.setLength(0);
            ArrayList newIdList = new ArrayList();
            for (int i = 0; i < idList.size(); i++) {
                strWhere.append("?,");
                pe.updatePending(this.KQ_TASK_TYPE, String.valueOf(idList.get(i)), 1, "30", this.userView);
                newIdList.add(idList.get(i));
                if (i != 0 && i % 300 == 0) {
                    if (strWhere.length() > 0) {
                        strWhere.deleteCharAt(strWhere.length() - 1);
                        strWhere.append(")");
                    }
                    dao.update(strSql.toString() + strWhere.toString(), newIdList);
                    strWhere.setLength(0);
                    newIdList.clear();
                }
            }
            if (idList.size() % 300 != 0) {
                strWhere.deleteCharAt(strWhere.length() - 1);
                strWhere.append(")");
                dao.update(strSql.toString() + strWhere.toString(), newIdList);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 按考勤方案id清除所有考勤待办
     * @param conn
     * @param scheme_id
     * @throws GeneralException
     * @author ZhangHua
     * @date 17:45 2018/12/7
     */
    public void cleanKqPengdingTaskBySchemeId(Connection conn, String scheme_id, String kq_year, String kq_duration, String org_id) throws GeneralException {

        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            ArrayList<Integer> idList = new ArrayList<Integer>();
            String ext_flag="";
            // 删除这个方案中某个期间的
            if(StringUtils.isNotBlank(kq_year) && StringUtils.isNotBlank(kq_duration)){
                ext_flag="KQ%\\_"+kq_year + kq_duration+"\\_"+scheme_id+"\\_%";
            }// 删除这个方案中摸个机构的
            else if(StringUtils.isNotBlank(scheme_id) && StringUtils.isNotBlank(org_id)){
            	ext_flag="KQ%\\_"+scheme_id+"\\_"+PubFunc.encrypt(org_id)+"%";
            }// 删除整个方案的
            else if(StringUtils.isNotBlank(scheme_id)){
                ext_flag="KQ%\\_"+scheme_id+"\\_%";
            }

            StringBuffer strSql = new StringBuffer();
            strSql.append("select pending_id from t_hr_pendingtask where pending_type=30 and ext_flag like '"+ext_flag+"' escape '\\'");
            rs = dao.search(strSql.toString());
            while (rs.next()) {
                idList.add(rs.getInt("pending_id"));
            }
            PendingTask pe = new PendingTask();
            strSql.setLength(0);
            strSql.append(" delete from t_hr_pendingtask  where pending_id in(");
            StringBuffer strWhere = new StringBuffer();
            ArrayList newIdList = new ArrayList();
            for (int i = 0; i < idList.size(); i++) {
                strWhere.append("?,");
                pe.updatePending(this.KQ_TASK_TYPE, String.valueOf(idList.get(i)), 100, "30", this.userView);
                newIdList.add(idList.get(i));
                if (i != 0 && i % 300 == 0) {
                    if (strWhere.length() > 0) {
                        strWhere.deleteCharAt(strWhere.length() - 1);
                        strWhere.append(")");
                    }
                    dao.update(strSql.toString() + strWhere.toString(), newIdList);
                    strWhere.setLength(0);
                    newIdList.clear();
                }
            }
            if (idList.size() % 300 != 0) {
                strWhere.deleteCharAt(strWhere.length() - 1);
                strWhere.append(")");
                dao.update(strSql.toString() + strWhere.toString(), newIdList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }

    }

    /**
     * 清除考勤邮件发送记录表
     * @param conn
     * @param scheme_id
     * @param kq_year
     * @param kq_duration
     * @param org_id
     * @throws GeneralException
     * @author ZhangHua
     * @date 13:41 2018/12/8
     */
    public void cleanKqSysMessageBySchemeId(Connection conn, String scheme_id,String kq_year,String kq_duration,String org_id) throws GeneralException {
        try{
            SendEmailUtil sendEmailUtil=new SendEmailUtil(this.getUserView(),conn,"30","1");
            StringBuffer strWhere=new StringBuffer();
            String extra = scheme_id + "\\_%";
            if(StringUtils.isNotBlank(kq_year)&&StringUtils.isNotBlank(kq_duration)){
                extra+="\\_"+kq_year + kq_duration + "\\_%";
            }
            strWhere.append(" And extra like '").append(extra).append("' ");
            strWhere.append(" and (function_id=1 or function_id=2)");
            sendEmailUtil.cleanSysMessageBySql(strWhere.toString(),null);
        }catch (Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }


    /**
     * 获取没有完成的考勤待办
     *
     * @param dao
     * @param ext_flag
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 18:34 2018/11/17
     */
    public HashSet<String> listUnFinishPengdingTask(ContentDAO dao, String ext_flag) throws GeneralException {
        HashSet<String> a0100Set = new HashSet<String>();
        RowSet rs = null;
        try {
            StringBuffer strSql = new StringBuffer("select receiver from t_hr_pendingtask where ext_flag=? and pending_status=0 ");
            ArrayList list = new ArrayList();
            list.add(ext_flag);
            rs = dao.search(strSql.toString(), list);
            while (rs.next()) {
                a0100Set.add(rs.getString("receiver").toLowerCase());
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return a0100Set;

    }


    public UserView getUserView() {
        return userView;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    public static String nullif(String str) {
        if (str == null)
            return "";
        else
            return str;
    }

    /**
     * 获取考勤方案定义的人员范围sql
     *
     * @param cond
     * @param dbname
     * @return
     * @throws GeneralException
     * @author ZhangHua
     * @date 11:45 2018/11/5
     */
    public String getComplexCondSql(Connection connection, String cond, String dbname, String sqlWhere) throws GeneralException {
        String w = "";
        try {
            ContentDAO dao = new ContentDAO(connection);
            String tempTableName = "";
            int infoGroup = 0; // forPerson 人员
            int varType = 8; // logic
            
//            StringBuffer whereIN = new StringBuffer();
//            if (StringUtils.isNotBlank(sqlWhere)) {
//                whereIN.append("select ").append(dbname).append("A01.a0100 From ").append(dbname).append("A01");
//                whereIN.append(" where 1=1 ").append(sqlWhere);
//            }
            ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
            YksjParser yp = new YksjParser(this.getUserView(), alUsedFields,
                    YksjParser.forSearch, varType, infoGroup, "Ht", dbname);
            YearMonthCount ymc = null;
            yp.run_Where(cond, ymc, "", "hrpwarn_result", dao, sqlWhere, connection, "A", null);
            tempTableName = yp.getTempTableName();
            w = yp.getSQL();

            if (w.trim().length() < 3 || yp.isFError()) {
                //定义的人员范围有误！
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.data.sp.msg.ComplexCondSqlErroe")));
            }

            if (w != null && w.trim().length() > 0) {
                return " and exists (select null from " + tempTableName + " where " + tempTableName + ".a0100=" + dbname + "A01.a0100 and ( " + w + " ))";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return w;

    }
    /**
     * 获得唯一性指标描述
     * @param conn
     * @return
     */
    public String getOnlyFieldName(Connection conn) {
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
        String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
        String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值

        if(!"0".equals(uniquenessvalid)&&StringUtils.isNotBlank(onlyname)
                && !"a0101".equalsIgnoreCase(onlyname)
                && !"b0110".equalsIgnoreCase(onlyname)
                && !"e0122".equalsIgnoreCase(onlyname)
                && !"e01a1".equalsIgnoreCase(onlyname)){
            String filedName = fieldInA01(onlyname);
            if(StringUtils.isNotBlank(filedName))
                return filedName;
        }
        return "";
    }
    /** 是否是主集指标并已构库 */
    private String fieldInA01(String field) {
        if (StringUtils.isBlank(field)) {
            return "";
        }
        FieldItem fieldItem = DataDictionary.getFieldItem(field, "a01");
        if(null != fieldItem && "1".equals(fieldItem.getUseflag())) {
            return fieldItem.getItemdesc();
        }
        return "";
    }
    

    /**
     * 按考勤方案id清除所有考勤待办
     * @param conn
     * @param scheme_id
     * @throws GeneralException
     * @author ZhangHua
     * @date 17:45 2018/12/7
     */
    public void cleanKqPengdingTaskByUserName(String scheme_id, Connection conn, String username, String org_id) throws GeneralException {

        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            ArrayList<Integer> idList = new ArrayList<Integer>();
            
            StringBuffer strSql = new StringBuffer();
            strSql.append("select pending_id from t_hr_pendingtask where pending_type=30 and ext_flag like ? escape '\\' and (receiver = ? or sender = ?)");
            ArrayList list= new ArrayList();
            String ext_flag="";
            if (StringUtils.isNotBlank(org_id)) {
				ext_flag="KQ%\\_"+scheme_id+"\\_"+PubFunc.encrypt(org_id)+"%";
			}else {
				ext_flag="KQ%\\_"+scheme_id+"\\_"+"%";
			}
            list.add(ext_flag);
            list.add(username);
            list.add(username);
            rs = dao.search(strSql.toString(), list);
            while (rs.next()) {
                idList.add(rs.getInt("pending_id"));
            }
            PendingTask pe = new PendingTask();
            strSql.setLength(0);
            strSql.append(" delete from t_hr_pendingtask  where pending_id in(");
            StringBuffer strWhere = new StringBuffer();
            ArrayList newIdList = new ArrayList();
            for (int i = 0; i < idList.size(); i++) {
                strWhere.append("?,");
                pe.updatePending(this.KQ_TASK_TYPE, String.valueOf(idList.get(i)), 100, "30", this.userView);
                newIdList.add(idList.get(i));
                if (i != 0 && i % 300 == 0) {
                    if (strWhere.length() > 0) {
                        strWhere.deleteCharAt(strWhere.length() - 1);
                        strWhere.append(")");
                    }
                    dao.update(strSql.toString() + strWhere.toString(), newIdList);
                    strWhere.setLength(0);
                    newIdList.clear();
                }
            }
            if (idList.size() % 300 != 0) {
                strWhere.deleteCharAt(strWhere.length() - 1);
                strWhere.append(")");
                dao.update(strSql.toString() + strWhere.toString(), newIdList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }

    }
    
    /**
     * 考勤方案切换人员的时候修改其待办
     *
     * @param conn
     * @param title        待办标题
     * @param scheme_id    考勤方案id
     * @param kq_year      考勤年份
     * @param kq_duration  考勤期间
     * @param org_id       归属单位
     * @param taskType     发送方式 关联taskType开头常量
     * @param reviewer 接收人 自助用户人员库加a0100 如 usr0000001 业务用户为账号
     * @param schemeBean   审批代办时用到，别的代办可以不穿
     * @throws GeneralException
     * @author ZhangHua
     * @date 18:31 2018/11/17
     */
    public void kqSendPengdingTask_changeForScheme(Connection conn, String title, int scheme_id, String kq_year, String kq_duration, String org_id, int taskType, String sender, String reviewer, LazyDynaBean schemeBean) throws GeneralException {

        ContentDAO dao = new ContentDAO(conn);
        try {
            String pending_url = "/module/utils/jsp.do?br_query=link&param=";
            String kq_url = "";
            String ext_flag = "";
            if (taskType != KqDataUtil.TASKTYPE_CONFIRM && StringUtils.isEmpty(org_id))
                return;
            String org_id_e = PubFunc.encrypt(org_id);
            switch (taskType) {
                //填报待办
                case KqDataUtil.TASKTYPE_FILL: {
                    ext_flag = "KQ_FILL_" + kq_year + kq_duration + "_" + scheme_id + "_" + org_id_e;
                    kq_url = "/module/kq/kqdata/KqDataMxForPendingTask.html?schemeId=" + PubFunc.encrypt(String.valueOf(scheme_id)) + "&kqYear=" + kq_year + "&kqDuration=" + kq_duration;
                    kq_url += "&fromflag=pt&orgId=" + org_id_e + "&viewType=0&operation=1&optRole=" + KqDataUtil.role_Agency_Clerk;
                    kq_url += "&sp_flag=01";
                }
                break;
                //审批待办
                case KqDataUtil.TASKTYPE_SP: {
                    HashMap schemeMap = (HashMap) schemeBean.getMap();
                    String clerk = String.valueOf(schemeMap.get("clerk_username"));
                    int reviewerRole = -1;//接收人角色
                    // 56124 更换考勤员或审核人时 校验viewType问题
                    String viewType = "0";
                    if (reviewer.equals(clerk)) {
                        reviewerRole = KqDataUtil.role_Clerk;
                        viewType = "1";
                    } else {
                        ArrayList<HashMap> orgMap = (ArrayList<HashMap>) schemeMap.get("org_map");
                        for (HashMap org : orgMap) {
                            String id = (String) org.get("org_id");
                            if (!org_id.equals(id))
                                continue;
                            if (org.containsKey("reviewer_id") && reviewer.equals(String.valueOf(org.get("reviewer_id")))) {
                                reviewerRole = KqDataUtil.role_Agency_Reviewer;
                                break;
                            } else if (org.containsKey("y_clerk_username") && reviewer.equalsIgnoreCase(String.valueOf(org.get("y_clerk_username")))) {
                                reviewerRole = KqDataUtil.role_Agency_Clerk;
                                break;
                            }
                        }
                    }
                    
                    LazyDynaBean newxRole = getKqRoleNextLevel(String.valueOf(scheme_id), schemeBean, reviewerRole, org_id);
                    String hasNextApprover = "".equals(newxRole.get("role")) ? "0" : "1";//是否有下级审批人，用于判断显示报批 | 批准
                    ext_flag = "KQ_SP_" + kq_year + kq_duration + "_" + scheme_id + "_" + org_id_e;
                    kq_url = "/module/kq/kqdata/KqDataMxForPendingTask.html?schemeId=" + PubFunc.encrypt(String.valueOf(scheme_id)) + "&kqYear=" + kq_year + "&kqDuration=" + kq_duration;
                    kq_url += "&hasNextApprover=" + hasNextApprover + "&fromflag=pt&orgId=" + org_id_e + "&viewType=" + viewType + "&operation=1&optRole=" + reviewerRole;
                    kq_url += "&sp_flag=02";
                }
                break;
                //驳回待办
                case KqDataUtil.TASKTYPE_SP_BACK: {
                    HashMap schemeMap = (HashMap) schemeBean.getMap();
                    String clerk = String.valueOf(schemeMap.get("clerk_username"));
                    int reviewerRole = -1;//接收人角色
                    String viewType = "0";
                    if (reviewer.equals(clerk)) {
                        reviewerRole = KqDataUtil.role_Clerk;
                        viewType = "1";
                    } else {
                        ArrayList<HashMap> orgMap = (ArrayList<HashMap>) schemeMap.get("org_map");
                        for (HashMap org : orgMap) {
                            String id = (String) org.get("org_id");
                            if (!org_id.equals(id))
                                continue;
                            if (org.containsKey("reviewer_id") && reviewer.equals(String.valueOf(org.get("reviewer_id")))) {
                                reviewerRole = KqDataUtil.role_Agency_Reviewer;
                                break;
                            } else if (org.containsKey("y_clerk_username") && reviewer.equalsIgnoreCase(String.valueOf(org.get("y_clerk_username")))) {
                                reviewerRole = KqDataUtil.role_Agency_Clerk;
                                break;
                            }
                        }
                    }

                    ext_flag = reviewerRole == KqDataUtil.role_Agency_Clerk ? "KQ_FILL_" : "KQ_SP_";
                    ext_flag += kq_year + kq_duration + "_" + scheme_id + "_" + org_id_e;
                    kq_url = "/module/kq/kqdata/KqDataMxForPendingTask.html?schemeId=" + PubFunc.encrypt(String.valueOf(scheme_id)) + "&kqYear=" + kq_year + "&kqDuration=" + kq_duration;
                    kq_url += "&fromflag=pt&orgId=" + org_id_e + "&viewType=" + viewType + "&operation=1&optRole=" + reviewerRole;
                    kq_url += "&sp_flag=07";
                }
                break;
                //确认待办
                case KqDataUtil.TASKTYPE_CONFIRM: {
                	// 51293 下发本人确认待办  机构参数应该是orgId （org_id由于之前wb确认待办参数写错导致）
                    kq_url = "/kq/kqdata/kqdataconfirm?schemeId=" + PubFunc.encrypt(String.valueOf(scheme_id)) + "&kq_year=" + kq_year + "&kq_duration=" + kq_duration 
                    		+ "&orgId=" + PubFunc.encrypt(org_id) + "&label=show";
                    ext_flag = "KQ_CONFIRM_" + kq_year + kq_duration + "_" + scheme_id + "_" + org_id_e;
                }
                break;
                default:
            }
            HashSet a0100Set = this.listUnFinishPengdingTask(dao, ext_flag);

            pending_url = pending_url + SafeCode.encode(kq_url);
            ArrayList<ArrayList> dataList = new ArrayList<ArrayList>();
            StringBuffer strSql = new StringBuffer();
            strSql.append(" insert into t_hr_pendingtask (pending_id ,pending_title ,pending_url ,pending_status ,");
            strSql.append("pending_level ,pending_type ,receiver ,sender ,lasttime ,create_time  ,ext_flag ,");
            strSql.append("bread) ");
            strSql.append(" values (?,?,?,0,1,30,?,?,");
            strSql.append(Sql_switcher.sqlNow());
            strSql.append(",");
            strSql.append(Sql_switcher.sqlNow());
            strSql.append(",?,0");
            strSql.append(")");
            IDGenerator idGenerator = new IDGenerator(2, conn);
            PendingTask pe = new PendingTask();

            ArrayList list_ = new ArrayList();
            int pending_id = Integer.parseInt(idGenerator.getId("pengdingTask.pengding_id"));
            list_.add(pending_id);
            list_.add(title);
            list_.add(pending_url);
            list_.add(reviewer);
            list_.add(sender);
            list_.add(ext_flag);
            dataList.add(list_);
            if (dataList.size() > 0) {
                dao.batchUpdate(strSql.toString(), dataList);

                for (ArrayList list : dataList) {
                    pe.insertPending(String.valueOf(list.get(0)), this.KQ_TASK_TYPE, title, sender, (String) list.get(3), pending_url, 0, 1, "30", this.getUserView());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    /**
     * 修改考勤方案的时候修改对应的待办名称
     * @param scheme_id
     * @param conn
     * @throws GeneralException
     * @author sunjian
     */
    public int updateKqPengdingName(String scheme_id, Connection conn, String old_name, String new_name) throws GeneralException {
    	int count = 0;
        ContentDAO dao = new ContentDAO(conn);
        try {
        	count = dao.update("update t_hr_pendingtask set pending_title = replace(pending_title,?,?) where pending_type=30 and ext_flag like ? escape '\\' ", 
            		Arrays.asList(new String[] {old_name, new_name, "KQ%\\_"+scheme_id+"\\_%"}));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return count;
    }
    /**
     *  新建完数据后需要同步变动部门人员的数据
     * @param scheme_id
     * @param kq_year
     * @param kq_duration
     * @param guidkeys  <br/>&nbsp;&nbsp;&nbsp;&nbsp;guidkeys不为空的时候用于人员增减，为空则用于新建考勤的时候
     */
    public void syncchangeDeptKqData(Connection conn,String kq_year, String kq_duration, String scheme_id,List<String> guidkeys) throws GeneralException {
        Map<String,List<String>> map = new HashMap<String,List<String>>();
        SchemeMainService schemeMainService = new SchemeMainServiceImpl(conn, this.userView);
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            KqPrivForHospitalUtil kqPrivForHospitalUtil=new KqPrivForHospitalUtil(this.userView,conn);
            //轮岗子集
            String changSetId = kqPrivForHospitalUtil.getKqChangeSetid();
            //轮岗部门
            String changDept = kqPrivForHospitalUtil.getKqChangeDeptField();
            //轮岗开始时间
            String changeStartField = kqPrivForHospitalUtil.getKqChangeStartField();
            String changeEndField = kqPrivForHospitalUtil.getKqChangeEndField();
            //是否配置了变动子集的变动部门
            if(StringUtils.isBlank(changSetId) || StringUtils.isBlank(changDept)){
                return ;
            }
            ArrayList parameterList = new ArrayList();
            parameterList.add(scheme_id);
            ArrayList<LazyDynaBean> shemeBeanList = schemeMainService.listKq_scheme("And scheme_id=? ", parameterList, "",kq_year,kq_duration);
            LazyDynaBean shemeBean=shemeBeanList.get(0);
            String[] dbNameList = String.valueOf(shemeBean.get("cbase")).split(",");
            //1.查询变动岗位子集中对应记录
            StringBuffer qSql = new StringBuffer();
            qSql.append("select t1.*,t2."+changDept+",t2."+changeStartField+",t2."+changeEndField);
            qSql.append(" from (SELECT Q35.*,$dbname$A01.a0100 AS t_a0100 FROM Q35,");
            qSql.append("$dbname$A01 WHERE Q35.guidkey = $dbname$A01.GUIDKEY");
            //guidkeys不为空的时候用于人员增减，为空则用于新建考勤的时候
            List values = new ArrayList();
            if(guidkeys!=null && !guidkeys.isEmpty()){
                qSql.append(" and Q35.guidkey in (");
                for(String s : guidkeys){
                    qSql.append("?,");
                    values.add(s);
                }
                qSql.setLength(qSql.length()-1);
                qSql.append(")");
            }
            qSql.append(" AND kq_year =? AND kq_duration=? AND scheme_id=?");
            qSql.append(" AND $dbname$A01.a0100 in (SELECT a0100 FROM $dbname$"+changSetId+" where ");
            qSql.append(" ("+Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+"=?");
            qSql.append(" or "+Sql_switcher.dateToChar(changeEndField,"yyyy-MM")+"=?))");
            qSql.append(") t1 left join $dbname$"+changSetId+" t2  on t1.t_a0100=t2.a0100 ");
            // 45851 日期校验错误
            qSql.append(" and (("+Sql_switcher.dateToChar("t2."+changeStartField,"yyyy-MM")+"<=?");
            qSql.append(" and "+Sql_switcher.dateToChar("t2."+changeEndField,"yyyy-MM")+">=?)");
            //兼容结束时间是null 的情况
            qSql.append(" or ((t2."+changeEndField+" is null or t2."+changeEndField+"='') and "
            		+Sql_switcher.dateToChar("t2."+changeStartField,"yyyy-MM")+"<=?))");
            qSql.append(" and t2."+changDept+"=t1.org_id");
            qSql.append(" order by t1.t_a0100,t2."+changDept+" DESC");
            values.add(kq_year);
            values.add(kq_duration);
            values.add(scheme_id);
            values.add(kq_year+"-"+kq_duration);
            values.add(kq_year+"-"+kq_duration);
            values.add(kq_year+"-"+kq_duration);
            values.add(kq_year+"-"+kq_duration);
            values.add(kq_year+"-"+kq_duration);

            parameterList.clear();
            parameterList.add(kq_year);
            parameterList.add(kq_duration);
            PeriodService periodService = new PeriodServiceImpl(userView, conn);
            ArrayList<LazyDynaBean> periods = periodService.listKq_duration(" and kq_year=? and kq_duration=?", parameterList, null);
            LazyDynaBean periodBean = periods.size()>0?periods.get(0):null;
            //考勤期间开始时间
            Date kqStart = null;
            //考勤期间结束时间
            Date kqEnd = null;
            Calendar kqStartCal =Calendar.getInstance();
            Calendar kqEndCal =Calendar.getInstance();
            if(periodBean!=null) {
                kqStart = (Date) periodBean.get("kq_start");
                kqEnd = (Date) periodBean.get("kq_end");
                kqStartCal.setTime(kqStart);
                kqEndCal.setTime(kqEnd);
            }
            ArrayList<String> allDays = new ArrayList<String>();
            // 55744 应根据期间的开始结束时间获取全部天数
            int alldays = DateUtils.dayDiff(kqStart, kqEnd);
            for(int i=1; i<= alldays+1;i++){
                String temp = "q35"+(i<10?"0"+i:i+"");
                allDays.add(temp);
            }
            for (String dbName : dbNameList) {
                //1、先查出来在别的部门下有变动数据的人，然后更新到本部门
                rs = dao.search(qSql.toString().replaceAll("\\$dbname\\$", dbName), values);
                ArrayList<String> list = null;
                String mkey = "";//存储上次循环的guidkey
                //日期的容器,方便记录那些天已经更新过
                List<String> allDays_copy = null;
                while (rs.next()) {
                    String guidkey = rs.getString("guidkey");
                    String changOrgId = rs.getString(changDept);
                    //第一次进入
                    if(StringUtils.isEmpty(mkey)){
                        mkey = guidkey;
                    }
                    //过程中或者 最后一条都要执行这里
                    if (!mkey.equalsIgnoreCase(guidkey)) {
                        //重置日期的容器
                        allDays_copy = new ArrayList(allDays);
                    }
                    //第一次进来
                    if(allDays_copy == null){
                        //初始化日期容器
                        allDays_copy = new ArrayList(allDays);
                    }
                    //记录下处理过的人员
                    mkey = guidkey;
                    Date startD = rs.getTimestamp(changeStartField);
                    Date endD = rs.getTimestamp(changeEndField);
                    Calendar startCal = Calendar.getInstance();
                    Calendar endCal = Calendar.getInstance();
                    if(StringUtils.isEmpty(changOrgId)){
                        //非变动部门数据
                        for(String s : allDays_copy){
                            if (!map.containsKey(mkey)) {
                                list = new ArrayList();
                                String value = rs.getString(s);
                                if(value == null){
                                    continue;
                                }
                                list.add(s + "`" + value);
                                map.put(mkey, list);
                            } else {
                                list = (ArrayList<String>) map.get(mkey);
                                String value = rs.getString(s);
                                if(value == null){
                                    continue;
                                }
                                list.add(s + "`" +value);
                                map.put(mkey, list);
                            }
                        }
                    }else{
                        //变动部门数据
                        if (startD != null && kqStart != null && kqEnd != null) {
                            //判断变动岗位的起始时间是否在考勤起始时间范围内
                            if (startD.before(kqStart)) {
                                startD = kqStart;
                            }
                            if(endD == null || endD.after(kqEnd)){
                                endD = kqEnd;
                            }
                            if(startD.after(endD)){
                                continue;
                            }
                            startCal.setTime(startD);
                            endCal.setTime(endD);
                            //得到变动岗开始时间与考勤期间的开始时间相差的天数
                            int days = DateUtils.dayDiff(kqStart, startD);
                            for (int i = 1; !startCal.after(endCal); startCal.add(Calendar.DATE, 1)) {
                                String temp = (i + days) < 10 ? "0" + (i + days) : i + days + "";
                                temp = "q35" + temp;
                                i++;
                                String value = rs.getString(temp);
                                if(allDays_copy.contains(temp)){
                                    allDays_copy.remove(temp);
                                }
                                //只判断null 就行了，空串有特殊含义（比如维护过日明细单是有删掉了，此时是空串，为了方便计算这么做的）
                                if(value==null){
                                    continue;
                                }
                                if (!map.containsKey(mkey)) {
                                    list = new ArrayList();
                                    list.add(temp + "`" + value);
                                    map.put(mkey, list);
                                } else {
                                    list = (ArrayList<String>) map.get(mkey);
                                    list.add(temp + "`" + value);

                                    map.put(mkey, list);
                                }
                            }
                        }
                    }
                }
                //便利map 开始更新数据
                List upSqls = new ArrayList();
                List valueList = new ArrayList();
                for(Map.Entry<String,List<String>> entry : map.entrySet()){
                    String guidkey_ = entry.getKey();
                    List<String> upVals = entry.getValue();
                    StringBuffer sql = new StringBuffer();
                    List upvalues = new ArrayList();
                    upvalues.add(kq_year);
                    upvalues.add(kq_duration);
                    upvalues.add(scheme_id);
                    upvalues.add(guidkey_);
                    if(upVals.size()>0){
                        sql.append("update Q35 set ");
                        for(String upVal: upVals){
                            String[] arr = upVal.split("`");
                            if(arr.length==1){
                                sql.append(arr[0]+"='',");
                            }else{
                                sql.append(arr[0]+"='"+arr[1]+"',");
                            }

                        }
                        sql.setLength(sql.length()-1);
                        sql.append(" where kq_year=? and kq_duration=? and scheme_id=? and guidkey=?");
                        upSqls.add(sql.toString());
                        valueList.add(upvalues);
                    }

                }
                dao.batchUpdate(upSqls,valueList);
            }
        }catch(Exception e){
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }
    /**
     * 查询是否有下级审批人
     * isKqRoleNextLevel
     * @param viewType
     * @param schemeBean
     * @param role
     * @param org_id
     * @return
     * @throws GeneralException
     * @date 2019年3月18日 上午8:56:13
     * @author linbz
     */
    public boolean isKqRoleNextLevel(String viewType, LazyDynaBean schemeBean, int role, String org_id) throws GeneralException {
        
    	boolean isNextLevel = false;
        try {
            HashMap schemeMap = (HashMap) schemeBean.getMap();
            // 考勤员不可为空  审核员可以为空
            switch (role) {
                case role_Agency_Clerk: {
                	ArrayList<HashMap> orgMap = (ArrayList<HashMap>) schemeMap.get("org_map");
                    for (HashMap org : orgMap) {
                        if (((String) org.get("org_id")).equalsIgnoreCase(org_id)) {
                            if (org.containsKey("reviewer_id") && StringUtils.isNotBlank((String) org.get("reviewer_id"))) {
                            	return true;
                            }
                        }
                    }
                }
                case role_Agency_Reviewer: {
                	String secondary_admin="1";
                    if(schemeBean.getMap().containsKey("secondary_admin")&&"0".equals((String)schemeBean.get("secondary_admin"))){
                        secondary_admin="0";
                    }
                    if("0".equals(secondary_admin))
                        return isNextLevel;
                    
                    if (schemeMap.containsKey("clerk_username") && StringUtils.isNotBlank((String) schemeMap.get("clerk_username")))
                        return true;
                }
                case role_Clerk: {
                    if (schemeMap.containsKey("reviewer_id") && StringUtils.isNotBlank((String) schemeMap.get("reviewer_id"))) 
                    	return true;
                }
                default: 
                    return isNextLevel;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 根据考勤期间获取开始结束日期
     * getDatesByKqDuration
     * @param connection
     * @param kq_year
     * @param kq_duration
     * @return
     * @throws GeneralException
     * @date 2019年4月11日 下午9:31:01
     * @author linbz
     */
    public LazyDynaBean getDatesByKqDuration(Connection connection, String kq_year, String kq_duration) throws GeneralException {
        LazyDynaBean bean = new LazyDynaBean();
        RowSet rs = null;
        try {
        	StringBuffer strSql = new StringBuffer();
        	ContentDAO dao = new ContentDAO(connection);
        	strSql.append("SELECT kq_start,kq_end ");
            strSql.append(" FROM kq_duration where kq_year=? and kq_duration=? ");
            ArrayList pList = new ArrayList();
            pList.add(kq_year);
            pList.add(kq_duration);
            rs = dao.search(strSql.toString(), pList);
            while (rs.next()) {
                bean.set("kq_start", rs.getDate("kq_start"));
                bean.set("kq_end", rs.getDate("kq_end"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return bean;
    }
    /**
	 * 获取 方案中  考勤员与审核人的归档权限
	 * getApproveUser
	 * @param kqSchemeBean	方案信息
	 * @param orgMap		下级机构信息
	 * @param haveSecondary	是否需要上报
	 * @param viewType		=0上报页面；=1审批页面
	 * @return
	 * @date 2019年4月23日 下午2:59:22
	 * @author linbz
	 */
    public String getApproveUserFile(HashMap kqSchemeBean, HashMap orgMap, boolean haveSecondary, String viewType) {
		String approveUser = "";
		try {
			HashMap map = orgMap;
			String functionid = "272030105";
			if(haveSecondary) {
				map = kqSchemeBean;
				functionid = "272030202";
			}
			// 审批页面中的未上报方案进来需特殊处理
			boolean viewBool = "1".equals(viewType) && !haveSecondary;
			// 优先显示考勤员
			UserView clerkUser = new UserView((String) map.get(viewBool?"y_clerk_username":"clerk_username"), conn); 
			clerkUser.canLogin(false);
			boolean bool = clerkUser.hasTheFunction(functionid);
			if(!bool) {
				// 暂时不考虑审核人是否有归档权限
				approveUser = (String) map.get(viewBool?"reviewer":"reviewer_fullname");
			}else 
				approveUser = (String) map.get("clerk_fullname");
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return approveUser;
	}
    /**
	 * 获取 所有的班次与考勤项目集合
	 * getAllClassAndItems
	 * @return
	 * @date 2019年4月23日 下午2:59:22
	 * @author linbz
	 * @throws GeneralException
	 */
    public HashMap<String,LazyDynaBean> getAllClassAndItems() throws GeneralException {
		ShiftsService shiftsService = new ShiftsServiceImpl(userView, conn);
		KqItemService itemService = new KqItemServiceImpl(userView, conn);
		HashMap<String,LazyDynaBean> map = new HashMap<String,LazyDynaBean>();
		ArrayList<LazyDynaBean>	kqItems = itemService.listKqItem("", null, "");
		ArrayList<LazyDynaBean>	kqClazzs = shiftsService.listKq_class("", null, "seq asc");
		
		for(LazyDynaBean bean : kqClazzs) {
			map.put("C"+bean.get("class_id"), bean);
		}
		for(LazyDynaBean bean : kqItems) {
			//排除定义了计算公式的项目
			if(StringUtils.isNotEmpty(String.valueOf(bean.get("c_expr"))))
				continue;
			map.put("I"+bean.get("item_id"), bean);
		}
		
		return map;
	}
    
    /**
     * 获取默认考勤参数xml
     * getSignatureIDXmlStr
     * @return
     * @throws GeneralException
     * @date 2019年9月4日 下午2:09:58
     * @author linbz
     */
	public String getSignatureIDXmlStr(ArrayList<HashMap> infoList) throws GeneralException{
		String xmlStr = "";
		StringReader reader = null;
		try {
			 String xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?> <params></params>";
			 //构建Document对象
			 Document doc = PubFunc.generateDom(xml);
			 //获得root节点
			 Element root = doc.getRootElement();
			 
			 for(HashMap map : infoList) {
				 Element recordNode = new Element("record");
				 recordNode.setAttribute("kq_user_id", (String)map.get("kq_user_id"));
				 Element itemNode = new Element("item");
				 itemNode.setAttribute("UserName", (String)map.get("UserName"));
				 itemNode.setAttribute("SignatureID", (String)map.get("SignatureID"));
				 itemNode.setAttribute("MarkID", (String)map.get("MarkID"));
				 recordNode.addContent(itemNode);
				 root.addContent(recordNode);
			 }
			 
			 //设置xml字体编码，然后输出为字符串
			 Format format=Format.getRawFormat();
			 format.setEncoding("UTF-8");
			 XMLOutputter output=new XMLOutputter(format);
			 xmlStr = output.outputString(doc);
			 
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			reader.close();
		}
		 return xmlStr;
	 }
	/**
     * 获取签章照片信息
     * getSignaturePhoto
     * @param xml		参数
	 * @param kqUserId	考勤流程里4个角色的序号1\2\3\4
     * @return
     * @throws GeneralException
     * @date 2019年9月4日 下午2:09:58
     * @author linbz
     */
	public HashMap getSignaturePhoto(String xml, String kqUserId) throws GeneralException{
    	
    	HashMap map = new HashMap();
		StringReader reader = null;
		try{
			 // 构建Document对象
			 Document doc = PubFunc.generateDom(xml);
			 // 获得root节点
			 Element root = doc.getRootElement();
			 
			 List childlist = root.getChildren("record");
			 ArrayList singerList = new ArrayList();
			 if(childlist!=null&&childlist.size()>0){
				 for(int k=0;k<childlist.size();k++){
					 org.jdom.Element element1 = (org.jdom.Element)childlist.get(k);
					 String kq_user_id = element1.getAttributeValue("kq_user_id");
					 if(kq_user_id.equals(kqUserId)) {
						 Element itemElement = element1.getChild("item");
						 map.put("UserName", itemElement.getAttributeValue("UserName"));
						 map.put("SignatureID", itemElement.getAttributeValue("SignatureID"));
						 map.put("MarkID", itemElement.getAttributeValue("MarkID"));
					 }
				 }
			 }
			 
		 }catch (Exception e) {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }finally{
			 reader.close();
		 }
		return map;
	}
	/**
     * 获取该条记录的当前流程信息
     * getDatesByKqDuration
     * @param connection
     * @param kq_year
     * @param kq_duration
     * @param org_id
	 * @param scheme_id
     * @return
     * @throws GeneralException
     * @date 2019年9月16日 下午10:31:01
     * @author linbz
     */
    public HashMap getKq_extend_logInfo(Connection connection, String kq_year, String kq_duration, String org_id
    		, String scheme_id) throws GeneralException {
    	HashMap map = new HashMap();
        RowSet rs = null;
        try {
        	StringBuffer strSql = new StringBuffer();
        	ContentDAO dao = new ContentDAO(connection);
        	strSql.append("SELECT curr_user ");
            strSql.append(" FROM kq_extend_log");
            strSql.append(" WHERE scheme_id=? and kq_year=? and kq_duration=? and org_id=? ");
            ArrayList pList = new ArrayList();
            pList.add(scheme_id);
            pList.add(kq_year);
            pList.add(kq_duration);
            pList.add(org_id);
            rs = dao.search(strSql.toString(), pList);
            while (rs.next()) {
            	map.put("curr_user", rs.getString("curr_user"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }
    /**
	 * 通过guidkey获取NbaseA0100
	 * getNbaseA0100ByGuidkey
	 * @param guidkeys	'','','',''
	 * @return
	 * @date 2019年9月21日 下午2:13:26
	 * @author linbz
	 */
	public ArrayList<String> getNbaseA0100ByGuidkey(String guidkeys) {
		ArrayList<String> list = new ArrayList<String>();
		RowSet rs = null;
		try {
			ArrayList<String> kq_dbase_list = this.userView.getPrivDbList();
			StringBuffer sql = new StringBuffer("");
			for(int i=0;i<kq_dbase_list.size();i++) {
				String dbname = kq_dbase_list.get(i);
				if(StringUtils.isBlank(dbname))
					continue;
				sql.append("select '"+dbname+"' nbase,A0100 from ").append(dbname).append("A01 where GUIDKEY in ( ").append(guidkeys).append(")");
				if(i < kq_dbase_list.size()-1)
					sql.append(" UNION ALL ");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while(rs.next()) {
				list.add(rs.getString("nbase")+rs.getString("A0100"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	} 
	/**
	 * 通过kq_extend_log获取当前操作人角色
	 * @param scheme_id
	 * @param kq_year
	 * @param kq_duration
	 * @param org_id
	 * @return
	 */
	public int getKqRoleByLog(String scheme_id, String kq_year, String kq_duration, String org_id) {
		int role = 0;
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append("select curr_user from kq_extend_log");
			sql.append(" where scheme_id=? and kq_year=? and kq_duration=? and org_id=?");
			ArrayList valuelist = new ArrayList();
			valuelist.add(scheme_id);
			valuelist.add(kq_year);
			valuelist.add(kq_duration);
			valuelist.add(org_id);
			
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString(), valuelist);
			while(rs.next()) {
				String curr_user = rs.getString("curr_user");
				if(",3,4,1,2,".contains(","+curr_user+",")) {
					role = Integer.parseInt(curr_user);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return role;
	}
	/**
	 * 获取周几描述
	 * getWeekDesc
	 * @param week	
	 * @return
	 * @date 2020年1月7日 下午7:13:26
	 * @author linbz
	 */
	public String getWeekDesc(int week) {
		String weekStr = "";
		switch (week) {
            case 1:
                weekStr = ResourceFactory.getProperty("kq.date.column.zri");
                break;
            case 2:
                weekStr =ResourceFactory.getProperty("kq.date.column.zyi");
                break;
            case 3:
                weekStr = ResourceFactory.getProperty("kq.date.column.zer");
                break;
            case 4:
                weekStr = ResourceFactory.getProperty("kq.date.column.zsan");
                break;
            case 5:
                weekStr = ResourceFactory.getProperty("kq.date.column.zsi");
                break;
            case 6:
                weekStr = ResourceFactory.getProperty("kq.date.column.zwu");
                break;
            case 7:
                weekStr = ResourceFactory.getProperty("kq.date.column.zliu");
                break;
            default:
            	weekStr =ResourceFactory.getProperty("kq.date.column.zyi");
                break;
		}
		return weekStr;
	}
}
