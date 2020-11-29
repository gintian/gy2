package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.bankgz.utils.MacAddressUtil;
import com.hjsj.hrms.bankgz.utils.SendKafkaGlblSrvNo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * function：推送统一门户菜单权限
 * datetime：2020-5-28 18:58:45
 * author：wangcy
 */
public class GZYHSyncPrivJob implements Job {

    private Logger log = LoggerFactory.getLogger(GZYHSyncPrivJob.class);
    private String jobId = "";
    private String staffRoleId = "";//员工角色编号
    private String leaderRoleId = "";//领导角色编号
    private HashMap roleCorreMap = new HashMap();//角色基础信息对应

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("[统一门户菜单权限推送至全渠道]任务开始");
        long start = System.currentTimeMillis();
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            boolean initFlag = init(context, conn);

            if (initFlag) {
                ArrayList<LazyDynaBean> tSysMenuManagerList = getSysMenuManagerData(conn);//菜单层级表(初始化) todo 已测过
                ArrayList<LazyDynaBean> tSysRoleInMenuList = getSysRoleInMenuData(conn);//角色基础、角色菜单对应表 todo 已测过
                ArrayList<LazyDynaBean> tSysStaffInRoleList = getSysStaffInRoleData(conn);//用户角色关系表(触发器)todo 已测过

                synctSysMenuManager(tSysMenuManagerList, conn);

                synctSysRoleInMenu(tSysRoleInMenuList, conn);

                synctSysStaffInRole(tSysStaffInRoleList, conn);
            }
        } catch (JobExecutionException e) {
            throw e;
        } catch (Exception e) {
            log.error("统一门户菜单权限推送获取数据库连接出错!,ErrorMessage:{}", e);
        } finally {
            PubFunc.closeDbObj(conn);
        }
        log.info("[[统一门户菜单权限推送至全渠道]]任务结束===[consume time is {} ms]===", (System.currentTimeMillis() - start));

    }

    /**
     * 初始化参数配置 配置格式为:staffRoleId=xxx,leaderRoleId=xxx
     *
     * @param context
     * @param conn
     * @return
     */
    private boolean init(JobExecutionContext context, Connection conn) throws JobExecutionException {
        boolean flag = false;
        this.jobId = context.getJobDetail().getName();
        this.staffRoleId = getJobParaValue("staffRoleId", "", conn);
        this.leaderRoleId = getJobParaValue("leaderRoleId", "", conn);
        if (StringUtils.isNotBlank(this.staffRoleId) && StringUtils.isNotBlank(this.leaderRoleId)) {
            getSysRoleData(conn);//角色基础表信息(初始化)
            log.info("角色基础信息 roleCorreMap:{}", roleCorreMap);
            flag = true;
        } else {
            log.error("统一门户权限推送-->员工、领导 角色编号未配置，请在后台作业-作业参数上配置!");
            log.error("统一门户权限推送-->格式为:staffRoleId=xxx,leaderRoleId=xxx");
            throw new JobExecutionException("统一门户权限推送-->员工、领导 角色编号未配置，请在后台作业-作业参数上配置!,格式为:staffRoleId=xxx,leaderRoleId=xxx");
        }
        return flag;
    }

    /**
     * 获取菜单层级信息
     *
     * @param conn
     * @return
     */
    private ArrayList<LazyDynaBean> getSysMenuManagerData(Connection conn) {
        String sysIndex = "menu_id,menu_name,menu_order,display_flag,keep_flag,layer,parent_id,url,path,name,status,flag";
        ArrayList<LazyDynaBean> tSysMenuManagerList = new ArrayList();
        RowSet rs = null;
        String sql = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            sql = "select " + sysIndex + " from t_sys_menu_manager_view where flag in (1,2,3)";
            rs = dao.search(sql);
            while (rs.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("menu_id", rs.getString("menu_id"));
                bean.set("menu_name", rs.getString("menu_name"));
                bean.set("menu_order", rs.getString("menu_order"));
                bean.set("display_flag", rs.getString("display_flag"));
                bean.set("keep_flag", rs.getString("keep_flag"));
                bean.set("layer", rs.getString("layer"));
                bean.set("parent_id", rs.getString("parent_id"));
                bean.set("url", rs.getString("url"));
                bean.set("path", rs.getString("path"));
                bean.set("name", rs.getString("name"));
                bean.set("status", rs.getString("status"));
                bean.set("flag", rs.getString("flag"));
                tSysMenuManagerList.add(bean);
            }
        } catch (Exception e) {
            log.error("getSysMenuManagerData:查询菜单层级表出错!,ErrorMessage:{},sql:{}", e, sql);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return tSysMenuManagerList;
    }

    /**
     * 获取角色菜单对应表
     *
     * @param conn
     * @return
     */
    private ArrayList<LazyDynaBean> getSysRoleInMenuData(Connection conn) {
        HashMap<String, String> menuRoleMap = new HashMap();//存储菜单与权限号对应关系
        getRoleMenuMap(menuRoleMap, conn);
        //先判断员工、领导角色编号是否发生变化 如果变化需把之前角色编号置为删除状态
        updatePrivFlag(conn);
        RowSet rs = null;
        String sql = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            sql = "select id,functionpriv from t_sys_function_priv where STATUS ='1' and id in('" + staffRoleId + "','" + leaderRoleId + "')";
            rs = dao.search(sql);
            while (rs.next()) {
                String role_id = rs.getString("id");//角色id
                String functionpriv = rs.getString("functionpriv");//权限号
                List<String> privArry = treeClear(functionpriv.split(","));
                StringBuffer menuIdBuf = new StringBuffer();
                for (String function_id : privArry) {
                    if (StringUtils.isNotEmpty(function_id) && function_id.length() > 1) {
                        String menu_id = menuRoleMap.get(function_id);//菜单号
                        if (StringUtils.isBlank(menu_id)) {
                            continue;
                        }
                        menuIdBuf.append(menu_id + ",");
                    }
                }
                dealPrivData(role_id, menuIdBuf.toString(), conn);
            }

        } catch (Exception e) {
            log.error("getSysRoleInMenuData:获取角色菜单对应表出错!,ErrorMessage:{},sql:{}", e, sql);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return getSysRoleInMenuList(conn);
    }

    /**
     * 获取角色菜单对应表数据
     *
     * @param conn
     * @return
     */
    private ArrayList<LazyDynaBean> getSysRoleInMenuList(Connection conn) {
        ArrayList<LazyDynaBean> tSysRoleInMenuList = new ArrayList();
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        String sql = "";
        try {
            sql = "select role_id,menu_code,flag from t_sys_role_in_menu_view where flag in (1,2,3)";
            rs = dao.search(sql);
            while (rs.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("role_id", rs.getString("role_id"));
                bean.set("role_name", roleCorreMap.get(rs.getString("role_id")));
                bean.set("menu_code", rs.getString("menu_code"));
                bean.set("flag", rs.getString("flag"));
                tSysRoleInMenuList.add(bean);
            }
        } catch (Exception e) {
            log.error("getSysRoleInMenuList:获取角色菜单对应表数据出错!,ErrorMessage:{},sql:{}", e, sql);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return tSysRoleInMenuList;
    }

    /**
     * 获取用户角色关系表
     *
     * @param conn
     * @return
     */
    private ArrayList<LazyDynaBean> getSysStaffInRoleData(Connection conn) {
        ContentDAO dao = new ContentDAO(conn);
        //比对数据
        dealNewStaffToView(dao);
        ArrayList<LazyDynaBean> tSysStaffInRoleList = new ArrayList();
        RowSet rs = null;
        String sql = "";
        try {
            sql = "select staff_id,emp_number,role_id,flag from t_sys_staff_in_role_view where flag in (1,2,3)";
            rs = dao.search(sql);
            while (rs.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                String staff_id = rs.getString("staff_id");//Usr00000258
                String emp_number = rs.getString("emp_number");//员工编号
                if (emp_number != null && StringUtils.isNotEmpty(emp_number)) {
                    bean.set("staff_id", staff_id);
                    bean.set("counterEmployee", emp_number);
                    bean.set("role_id", rs.getString("role_id"));
                    bean.set("flag", rs.getString("flag"));
                    tSysStaffInRoleList.add(bean);
                }
            }
        } catch (Exception e) {
            log.error("getSysStaffInRoleData:获取用户角色关系表出错!,ErrorMessage:{},sql:{}", e, sql);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return tSysStaffInRoleList;
    }

    /**
     * usra01与t_sys_staff_in_role_view进行比对 usra01不存在的
     *
     * @param dao
     */
    private void dealNewStaffToView(ContentDAO dao) {
        RowSet rs = null;
        String sql = "";
        try {
            //先比对人员删除的
            sql = "select staff_id,emp_number from t_sys_staff_in_role_view temp where not exists(select A0144 from USRA01  where A0144 = temp.EMP_NUMBER and 'Usr'||A0100 = temp.STAFF_ID) and FLAG='0'";
            rs = dao.search(sql);
            while (rs.next()) {
                String staff_id = rs.getString("staff_id");
                String a0100 = staff_id.substring(3);
                String empNumber = rs.getString("emp_number");
                insertOrdelStaffView(a0100, empNumber, dao, "1");
            }
            //再比对人员新增的
            sql = "select a0100,a0144 from USRA01 A01 where not exists(select EMP_NUMBER from t_sys_staff_in_role_view  where EMP_NUMBER = A01.A0144 and STAFF_ID = 'Usr'||A01.A0100) and A0144 is not null";
            rs = dao.search(sql);
            while (rs.next()) {
                String a0100 = rs.getString("a0100");
                String empNumber = rs.getString("a0144");
                insertOrdelStaffView(a0100, empNumber, dao, "0");
            }
        } catch (SQLException e) {
            log.error("dealNewStaffToView:比对用户角色出错!,ErrorMessage:{},sql:{}", e, sql);
        } finally {
            PubFunc.closeResource(rs);
        }

    }

    private void insertOrdelStaffView(String staff_id, String empNumber, ContentDAO dao, String operateFlag) {
        try {
            RecordVo vo = new RecordVo("t_sys_staff_in_role_view");
            vo.setString("staff_id", "Usr" + staff_id);
            vo.setString("emp_number", empNumber);
            vo.setString("role_id", "00002074");
            if ("0".equals(operateFlag)) {
                vo.setString("flag", "1");
                dao.addValueObject(vo);
            } else {
                vo.setString("flag", "3");
                dao.updateValueObject(vo);
            }
        } catch (Exception e) {
            log.error("insertOrdelStaffView:比对用户角色出错!,ErrorMessage:{}", e);
        }

    }

    /**
     * 同步菜单层级表
     *
     * @param datalist
     */
    public void synctSysMenuManager(ArrayList<LazyDynaBean> datalist, Connection conn) throws JobExecutionException {
        String menulevelPushUrl = SystemConfig.getPropertyValue("menulevelPushUrl");
        for (LazyDynaBean bean : datalist) {
            String sNo = MacAddressUtil.getGlbSrvNo();
            SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "ACP2005007");//登记流水号
            String jsonObj = packSyncJson(bean, "ACP2005007", sNo);
            boolean callFlag = sendByPost(jsonObj, "ACP2005007", menulevelPushUrl, sNo);
            if (callFlag) {
                String menu_id = (String) bean.get("menu_id");
                sucessSysMenuManager(menu_id, conn);
            }
        }

    }

    /**
     * 同步角色菜单对应表
     *
     * @param datalist
     */
    public void synctSysRoleInMenu(ArrayList<LazyDynaBean> datalist, Connection conn) throws JobExecutionException {
        String roleMenuPushUrl = SystemConfig.getPropertyValue("roleMenuPushUrl");
        for (LazyDynaBean bean : datalist) {
            String sNo = MacAddressUtil.getGlbSrvNo();
            SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "ACP2004003");//登记流水号
            String jsonObj = packSyncJson(bean, "ACP2004003", sNo);
            boolean callFlag = sendByPost(jsonObj, "ACP2004003", roleMenuPushUrl, sNo);
            if (callFlag) {
                String role_id = (String) bean.get("role_id");
                sucessSysRoleInMenu(role_id, conn);
            }
        }

    }

    /**
     * 同步用户角色关系表
     *
     * @param datalist
     */
    public void synctSysStaffInRole(ArrayList<LazyDynaBean> datalist, Connection conn) throws JobExecutionException {
        String userRolePushUrl = SystemConfig.getPropertyValue("userRolePushUrl");
        for (LazyDynaBean bean : datalist) {
            String flag = (String) bean.get("flag");//1:新增 2: 修改 3: 删除
            boolean callFlag = false;//调用接口成功标识
            String sNo = MacAddressUtil.getGlbSrvNo();
//            SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "ACP2004009");//登记流水号
            //如果是删除直接删除
            if ("3".equals(flag)) {
                String jsonObj = packSyncDeleteJson(bean, "ACP2004009", sNo);
                callFlag = sendByPost(jsonObj, "ACP2004009", userRolePushUrl, sNo);
            } else if ("2".equals(flag)) {//如果是修改则先删除再新增
                String jsonObj = packSyncDeleteJson(bean, "ACP2004009", sNo);
                boolean deleteFlag = sendByPost(jsonObj, "ACP2004009", userRolePushUrl, sNo);
                if (deleteFlag) {
                    jsonObj = packSyncInsertJson(bean, "ACP2004009", sNo);
                    callFlag = sendByPost(jsonObj, "ACP2004009", userRolePushUrl, sNo);
                }
            } else {
                String jsonObj = packSyncInsertJson(bean, "ACP2004009", sNo);
                callFlag = sendByPost(jsonObj, "ACP2004009", userRolePushUrl, sNo);
            }
            if (callFlag) {
                String role_id = (String) bean.get("role_id");
                String staff_id = (String) bean.get("staff_id");
                sucessSysStaffInRole(role_id, staff_id, conn);
                if ("3".equals(flag)) {
                    sucessDeleteSysStaffInRole(role_id, staff_id, conn);
                }
            }
        }

    }

    /**
     * 组装接口报文
     *
     * @param bean
     * @param transFlag
     * @return
     */
    private String packSyncJson(LazyDynaBean bean, String transFlag, String sNo) {
        JSONObject escMessage = new JSONObject();
        JSONObject requestData = new JSONObject();

        JSONObject systemHeader = new JSONObject();//业务数据系统头
        systemHeader.put("sourceSystemCode", "HRS");//请求方系统码
        systemHeader.put("sinkSystemCode", "UAP");//目标方系统码
        systemHeader.put("actionId", transFlag);//服务码
        systemHeader.put("actionVersion", "v1");//交易码版本 默认v1
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        systemHeader.put("sourceJnlNo", sourceJnlNo);//请求方流水号 交易日期(8位：YYYYMMDD) + 请求方流水序号（12 位）
        systemHeader.put("glblSrvNo", sNo);//全局流水号 雪花算法
        systemHeader.put("timestamp", timestamp);//请求发送时间戳 yyyy-MM-dd HH:mm:ss.SSS
        systemHeader.put("ip", SystemConfig.getPropertyValue("hrpserver"));//发送请求机器ip
        JSONObject appHeader = new JSONObject();
        appHeader.put("BankNum", "001");
        appHeader.put("Brno", "00998");
        appHeader.put("Userno", "02928");//todo
        systemHeader.put("appHeader", appHeader);
        requestData.put("systemHeader", systemHeader);

        JSONObject body = new JSONObject();
        if ("ACP2005007".equals(transFlag)) {//菜单层级表
            String menu_id = (String) bean.get("menu_id");
            String menu_name = (String) bean.get("menu_name");
            String menu_order = (String) bean.get("menu_order");
            String display_flag = (String) bean.get("display_flag");
            String keep_flag = (String) bean.get("keep_flag");
            String layer = (String) bean.get("layer");
            String parent_id = (String) bean.get("parent_id");
            //String url = (String) bean.get("url");
            //String path = (String) bean.get("path");
            String name = (String) bean.get("name");
            String status = (String) bean.get("status");
            String flag = (String) bean.get("flag");

            body.put("MenuCod", menu_id);//菜单代码
            body.put("MenuNam", menu_name); //菜单名称
            body.put("MenuOrder", menu_order);//菜单排序
            body.put("DisplayFlag", display_flag);//展示标志
            body.put("KeepFlag", keep_flag);//数据保持标志
            body.put("MenuClassf", layer);//菜单层级
            body.put("SupeMenuCod", parent_id);//上级菜单代码
            body.put("NAME", name);//菜单名称
            body.put("Status", status);//状态
            body.put("OprFlag", flag);//操作标志

        } else if ("ACP2004003".equals(transFlag)) {//角色菜单信息
            String role_id = (String) bean.get("role_id");
            String role_name = (String) bean.get("role_name");
            String menu_code = (String) bean.get("menu_code");
            String flag = (String) bean.get("flag");

            body.put("OprFlag", flag);//操作标志
            body.put("DutyNo", role_id);//角色代码
            body.put("DutyNm", role_name);//角色名称
            body.put("DutyTyp", "2");//角色类型
            JSONArray LoopRecArry = new JSONArray();
            String[] menu_code_split = menu_code.split(",");
            for (int i = 0; i < menu_code_split.length; i++) {
                String menuCode = menu_code_split[i];
                JSONObject tempObj = new JSONObject();
                tempObj.put("AuthFlag", "");//授权标志
                tempObj.put("ExecFlag", "1");//执行标志
                tempObj.put("TranNo", menuCode);
                LoopRecArry.add(tempObj);
            }
            body.put("LoopRec", LoopRecArry);
        }
        requestData.put("body", body);
        escMessage.put("requestData", requestData);
        return escMessage.toString();
    }

    /**
     * 组装删除报文
     *
     * @param bean
     * @param transFlag
     * @return
     */
    private String packSyncDeleteJson(LazyDynaBean bean, String transFlag, String sNo) {
        JSONObject escMessage = new JSONObject();
        JSONObject requestData = new JSONObject();

        JSONObject systemHeader = new JSONObject();//业务数据系统头
        systemHeader.put("sourceSystemCode", "HRS");//请求方系统码
        systemHeader.put("sinkSystemCode", "UAP");//目标方系统码
        systemHeader.put("actionId", transFlag);//服务码
        systemHeader.put("actionVersion", "v1");//交易码版本 默认v1
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        systemHeader.put("sourceJnlNo", sourceJnlNo);//请求方流水号 交易日期(8位：YYYYMMDD) + 请求方流水序号（12 位）
        systemHeader.put("glblSrvNo", sNo);//全局流水号 雪花算法
        systemHeader.put("timestamp", timestamp);//请求发送时间戳 yyyy-MM-dd HH:mm:ss.SSS
        systemHeader.put("ip", SystemConfig.getPropertyValue("hrpserver"));//发送请求机器ip
        JSONObject appHeader = new JSONObject();
        appHeader.put("BankNum", "001");
        appHeader.put("Brno", "00998");
        String counterEmployee = (String) bean.get("counterEmployee");//柜员号
        appHeader.put("Userno", counterEmployee);
        systemHeader.put("appHeader", appHeader);
        requestData.put("systemHeader", systemHeader);

        JSONObject body = new JSONObject();
        body.put("TlrNo", counterEmployee);//柜员号
        body.put("OperFlag", "U"); //操作标志
        body.put("RolePostCode", "");//角色岗位编号

        JSONArray arry = new JSONArray();
        JSONObject tempObj = new JSONObject();
        tempObj.put("OperFlag", "U");
        tempObj.put("DutyTyp", "2");
        arry.add(tempObj);
        body.put("BussPostLoopRec", arry);//业务岗位列表

        requestData.put("body", body);
        escMessage.put("requestData", requestData);

        return escMessage.toString();
    }

    /**
     * 组装角色菜单新增报文
     *
     * @param bean
     * @param transFlag
     * @return
     */
    private String packSyncInsertJson(LazyDynaBean bean, String transFlag, String sNo) {
        JSONObject escMessage = new JSONObject();
        JSONObject requestData = new JSONObject();

        JSONObject systemHeader = new JSONObject();//业务数据系统头
        systemHeader.put("sourceSystemCode", "HRS");//请求方系统码
        systemHeader.put("sinkSystemCode", "UAP");//目标方系统码
        systemHeader.put("actionId", transFlag);//服务码
        systemHeader.put("actionVersion", "v1");//交易码版本 默认v1
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        systemHeader.put("sourceJnlNo", sourceJnlNo);//请求方流水号 交易日期(8位：YYYYMMDD) + 请求方流水序号（12 位）
        systemHeader.put("glblSrvNo", sNo);//全局流水号 雪花算法
        systemHeader.put("timestamp", timestamp);//请求发送时间戳 yyyy-MM-dd HH:mm:ss.SSS
        systemHeader.put("ip", SystemConfig.getPropertyValue("hrpserver"));//发送请求机器ip
        JSONObject appHeader = new JSONObject();
        appHeader.put("BankNum", "001");
        appHeader.put("Brno", "00998");
        String counterEmployee = (String) bean.get("counterEmployee");//柜员号
        appHeader.put("Userno", counterEmployee);
        systemHeader.put("appHeader", appHeader);
        requestData.put("systemHeader", systemHeader);

        JSONObject body = new JSONObject();
        String role_id = (String) bean.get("role_id");

        JSONArray arry = new JSONArray();
        body.put("TlrNo", counterEmployee);//柜员号
        body.put("OperFlag", "A"); //操作标志
        body.put("RolePostCode", "");//角色岗位编号

        JSONObject tempObj = new JSONObject();
        tempObj.put("OperFlag", "A");
        tempObj.put("DutyTyp", "2");
        tempObj.put("BussPostCode", role_id);
        arry.add(tempObj);
        body.put("BussPostLoopRec", arry);//业务岗位列表

        requestData.put("body", body);
        escMessage.put("requestData", requestData);
        return escMessage.toString();
    }


    /**
     * 调用全渠道平台接口
     *
     * @return
     */
    public boolean sendByPost(String param, String actionId, String menuPrivPushUrl, String sNo) throws JobExecutionException {
        boolean isSucceed = false;
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setContentCharset("GBK");
        PostMethod postMethod = new PostMethod(menuPrivPushUrl);
        String responseMsg = "";
        try {
            String macVal = MacAddressUtil.getMacAddress(param, "GZYH.ACPA_node.zak");
            postMethod.addRequestHeader("X-GZB-mac", macVal);
            postMethod.addRequestHeader("x-gzb-sourcesystemcode", "1000");
            postMethod.addRequestHeader("X-GZB-jnlNo", sourceJnlNo);//业务流水号
            postMethod.addRequestHeader("X-GZB-actionId", actionId);
            RequestEntity se = new StringRequestEntity(param, "application/json", "UTF-8");
            postMethod.setRequestEntity(se);
            int status = httpClient.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = postMethod.getResponseBodyAsStream();
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                responseMsg = out.toString("UTF-8");
                JSONObject obj = JSONObject.fromObject(responseMsg);
                String responseCode = (String) obj.get("responseCode");
                if ("ACPAAAAAAA".equals(responseCode)) {//调用成功
                    isSucceed = true;
                } else if ("ACPE204017".equals(responseCode)) {//对应已存在相同菜单数据
                    isSucceed = true;
                    log.info("权限推送-->已存在相同菜单数据或角色 responseMsg:{},请求报文param:{},请求地址:{}", responseMsg, param, menuPrivPushUrl);
                } else if ("ACPE204002".equals(responseCode)) {//对应当前员工已存在相同角色
                    isSucceed = true;
                    log.info("权限推送-->当前员工已赋予此角色 responseMsg:{},请求报文param:{},请求地址:{}", responseMsg, param, menuPrivPushUrl);
                } else {
                    log.error("权限推送-->调用全渠道接口{}失败，ErrorMessage返回结果为 responseMsg:{},请求报文param:{},请求地址:{}", actionId, responseMsg, param, menuPrivPushUrl);
                    SendKafkaGlblSrvNo.updateGlbSrlNo(sNo, "1", responseCode, obj.toString());
                }
            } else {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = postMethod.getResponseBodyAsStream();
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                responseMsg = out.toString("UTF-8");
                log.error("权限推送-->调用全渠道接口{}失败，ErrorMessage返回结果为 responseMsg:{},请求报文param:{},请求地址:{}", actionId, responseMsg, param, menuPrivPushUrl);
                throw new JobExecutionException("权限推送-->调用全渠道接口失败！" + responseMsg);
            }
        } catch (JobExecutionException e) {
            throw e;
        } catch (Exception e) {
            log.error("sendByPost:调用全渠道推送权限接口{}出错!,ErrorMessage:{},param:{},请求地址:{}", actionId, e, param, menuPrivPushUrl);
            throw new JobExecutionException("权限推送-->调用全渠道接口失败！" + e);
        } finally {
            postMethod.releaseConnection();
        }
        return isSucceed;
    }

    /**
     * 同步成功更新用户角色关系表
     */
    public void sucessSysStaffInRole(String role_id, String staff_id, Connection conn) {
        String sql = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            sql = "update t_sys_staff_in_role_view set flag = 0 where role_id='" + role_id + "' AND  staff_id='" + staff_id + "'";
            dao.update(sql);
        } catch (Exception e) {
            log.error("sucessSysStaffInRole:更新状态出错!,ErrorMessage:{},role_id:{},staff_id:{}", e, role_id, staff_id);
        }

    }

    /**
     * 同步成功 如果是删除 需删除这条记录 用户角色关系表
     */
    public void sucessDeleteSysStaffInRole(String role_id, String staff_id, Connection conn) {
        try {
            ContentDAO dao = new ContentDAO(conn);
            RecordVo vo = new RecordVo("t_sys_staff_in_role_view");
            vo.setString("role_id", role_id);
            vo.setString("staff_id", staff_id);
            dao.deleteValueObject(vo);
        } catch (Exception e) {
            log.error("sucessDeleteSysStaffInRole:删除用户角色表数据出错!,ErrorMessage:{},role_id:{},staff_id:{}", e, role_id, staff_id);
        }

    }

    /**
     * 同步成功更新菜单层级表信息
     */
    public void sucessSysMenuManager(String menu_id, Connection conn) {
        String sql = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            sql = "update t_sys_menu_manager_view set flag = 0 where menu_id='" + menu_id + "'";
            dao.update(sql);
        } catch (Exception e) {
            log.error("sucessSysMenuManager:同步成功更新菜单层级表信息出错!,ErrorMessage:{},menu_id:{}", e, menu_id);
        }

    }

    /**
     * 同步成功更新角色菜单对应表
     */
    public void sucessSysRoleInMenu(String role_id, Connection conn) {
        String sql = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            sql = "update t_sys_role_in_menu_view set flag = 0 where role_id='" + role_id + "'";
            dao.update(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取角色基础表信息
     *
     * @param conn
     * @return
     */
    private void getSysRoleData(Connection conn) {
        String sysIndex = "role_id,role_name";
        ArrayList<String> tSysRoleList = new ArrayList();
        RowSet rs = null;
        String sql = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            sql = "select " + sysIndex + " from t_sys_role where role_id in (?,?)";
            tSysRoleList.add(staffRoleId);
            tSysRoleList.add(leaderRoleId);
            rs = dao.search(sql, tSysRoleList);
            while (rs.next()) {
                String role_id = rs.getString("role_id");
                String role_name = rs.getString("role_name");
                roleCorreMap.put(role_id, role_name);
            }
        } catch (Exception e) {
            log.error("getSysRoleData:查询角色基础信息出错!,ErrorMessage:{}", e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }

    /**
     * 获取员工编号
     *
     * @param staff_id
     */
    private String getCounterEmployee(String staff_id, Connection conn) {
        String counterEmployee = "";
        String A0100 = staff_id.substring(3);
        String sql = "select a0144 from UsrA01 where a0100 = '" + A0100 + "'";
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                counterEmployee = rs.getString(1);
            }
        } catch (SQLException e) {
            log.error("getCounterEmployee:获取员工编号出错!,ErrorMessage:{},sql:{}", e, sql);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return counterEmployee;
    }


    /**
     * @param arr
     * @return
     */
    private List treeClear(Object[] arr) {
        List list = new ArrayList();
        for (int i = 0; i < arr.length; i++) {
            if (!list.contains(arr[i])) {
                list.add(arr[i]);
            }
        }
        return list;
    }

    /**
     * 获取菜单权限号对应关系
     *
     * @param menuRoleMap
     * @param conn
     * @return
     */
    private HashMap getRoleMenuMap(HashMap menuRoleMap, Connection conn) {
        String sql = "select menu_id,function_id from t_sys_menu_manager_view";
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql);
            while (rs.next()) {
                String function_id = rs.getString("function_id");
                String menu_id = rs.getString("menu_id");
                menuRoleMap.put(function_id, menu_id);
            }
        } catch (SQLException e) {
            log.error("getRoleMenuMap:获取菜单权限号对应关系出错!,ErrorMessage:{},sql:{}", e, sql);
        } finally {
            PubFunc.closeResource(rs);
        }
        return menuRoleMap;
    }


    /**
     * 判断角色菜单对应表是否已有相同数据(没有则进行新增)
     *
     * @param role_id
     * @param menuIdBuf
     * @param conn
     */
    private void dealPrivData(String role_id, String menuIdBuf, Connection conn) {
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        String existFlag = "";
        try {
            rs = dao.search("select menu_code from t_sys_role_in_menu_view where role_id = '" + role_id + "'");
            //如果已有角色id 再取角色对应的菜单id是否一样 如果一样不做处理(既角色菜单表没发生变化)，不一样 则更新flag为修改状态
            if (rs.next()) {
                String menu_code = rs.getString("menu_code");
                if (!menuIdBuf.equalsIgnoreCase(menu_code)) {
                    existFlag = "true";
                } else {
                    existFlag = "false";//不做处理
                }
            }
            RecordVo vo = new RecordVo("t_sys_role_in_menu_view");
            vo.setString("role_id", role_id);
            vo.setString("menu_code", menuIdBuf);
            if ("true".equalsIgnoreCase(existFlag)) {//表示当前配置的角色id在同步的角色菜单对应表中有对应记录，但对应的菜单编号发生变化，进行修改同步操作
                vo.setString("flag", "2");
                dao.updateValueObject(vo);
            } else if ("".equalsIgnoreCase(existFlag)) {//表示当前配置的角色id在同步的角色菜单对应表中没有，进行新增同步操作
                vo.setString("flag", "1");
                dao.addValueObject(vo);
            }
        } catch (Exception e) {
            log.error("dealPrivData:处理菜单权限对应关系出错!,ErrorMessage:{}", e);
        }

    }

    /**
     * 判断员工、领导角色编号是否发生变化 如果变化需把之前角色编号置为删除状态
     *
     * @param conn
     */
    private void updatePrivFlag(Connection conn) {
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        List roleidList = new ArrayList();
        roleidList.add(staffRoleId);
        roleidList.add(leaderRoleId);
        try {
            rs = dao.search("select role_id from t_sys_role_in_menu_view ");
            while (rs.next()) {
                String role_id = rs.getString("role_id");
                if (!roleidList.contains(role_id)) {
                    RecordVo vo = new RecordVo("t_sys_role_in_menu_view");
                    vo.setString("role_id", role_id);
                    vo.setString("flag", "3");
                    dao.updateValueObject(vo);
                }
            }

        } catch (Exception e) {
            log.error("updatePrivFlag:角色编号变法，修改之前角色编号状态出错!,ErrorMessage:{}", e);
        }
    }


    /**
     * 获取配置参数
     *
     * @param paramName
     * @param defaultValue
     * @param conn
     * @return
     */
    private String getJobParaValue(String paramName, String defaultValue, Connection conn) {
        String returnValue = defaultValue;
        try {
            ContentDAO dao = new ContentDAO(conn);
            RecordVo vo = new RecordVo("t_sys_jobs");
            vo.setInt("job_id", Integer.parseInt(this.jobId));
            vo = dao.findByPrimaryKey(vo);
            String jobParam = vo.getString("job_param");

            if (jobParam == null) {
                return returnValue;
            }
            jobParam = jobParam.trim();

            if ("".equals(jobParam)) {
                return returnValue;
            }

            String[] params = jobParam.split(",");
            for (int i = 0; i < params.length; i++) {
                String[] param = params[i].split("=");
                if (2 != param.length) {
                    continue;
                }
                if (!paramName.equalsIgnoreCase(param[0].trim())) {
                    continue;
                }
                returnValue = param[1].trim();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("错误：{" + e.toString() + "}");
        }
        return returnValue;
    }

}
