package com.hjsj.hrms.module.kq.interfaces;

import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.kq.util.KqVer;
import com.hjsj.hrms.module.template.utils.BusinessService;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
  * 新考勤申请模板集成类
 * @author zhaoxj
 * @since 2019-09-16
 *
 */
public class KqAppInterface implements BusinessService {
    private final static String KQ_ITEM_CODESET = "27";
    
    // 考勤申请类型
    private int appType = -1;
    private HashMap<String, String> fieldMap = new HashMap<String, String>();
    
    private String leaveAppSubSet = "";
    private String leaveAppStartItem = "";
    private String leaveAppEndItem = "";
    
    private String overtimeAppSubSet = "";
    private String overtimeAppStartItem = "";
    private String overtimeAppEndItem = "";
    
    private String officeLeaveAppSubSet = "";
    private String officeLeaveAppStartItem = "";
    private String officeLeaveAppEndItem = "";
    
    private String appIdItem = "";
    private String appTypeItem = "";
    private String appStartItem = "";
    private String appEndItem = "";
    
    //记录冲突的申请业务类型
    private int repeatAppType = 0;

    @Override
    public void execution(ArrayList recordVoList, int tabid, String opt, UserView userview) throws GeneralException {
        
    }

    @Override
    public void execution(ArrayList recordVoList, int tabid, String opt, UserView userview, String busiTab,
            String mappingStr) throws GeneralException {
        // 驳回操作无需处理
        if (OPT_REJECT.equalsIgnoreCase(opt)) {
            return;
        }
        
        if ("q15".equalsIgnoreCase(busiTab)) {
            this.appType = KqConstant.AppType.LEAVE;
        } else if ("q13".equalsIgnoreCase(busiTab)) {
            this.appType = KqConstant.AppType.OFFICE_LEAVE;
        } else if ("q11".equalsIgnoreCase(busiTab)) {
            this.appType = KqConstant.AppType.OVERTIME;
        } else {
            // 不是考勤申请
            return;
        }
        
        KqVer kqVer = new KqVer();
        int ver = kqVer.getVersion();
        // 非高校医院班考勤不走此方法
        if (ver != KqConstant.Version.UNIVERSITY_HOSPITAL) {
            return;
        }
        
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            
            getFieldMap(mappingStr.toLowerCase());
            
            this.appIdItem = fieldMap.get(busiTab + "01").toString();
            this.appTypeItem = fieldMap.get(busiTab + "03").toString();
            this.appStartItem = fieldMap.get(busiTab + "z1").toString();
            this.appEndItem = fieldMap.get(busiTab + "z3").toString();
            
            // 校验申请是否重复
            checkRepeat(userview, conn ,recordVoList);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(conn);
        }
    }
    
    /**
          * 将模板与考勤业务表对应关系字符串解析为map形式
     * @param mapStr
     */
    private void getFieldMap(String mapStr) {
        if (StringUtils.isBlank(mapStr)) {
            return;
        }
        
        this.fieldMap.clear();
        
        String[] itemMap = mapStr.split(",");
        for (int i = 0; i < itemMap.length; i++) {
            String[] items = ((String)itemMap[i]).split(":");
            if (items.length != 2) {
                continue;
            }
            
            this.fieldMap.put(items[0], items[1]);
        }
    }
    
    private void checkRepeat(UserView userView, Connection conn, ArrayList recordVoList) throws GeneralException {
        this.repeatAppType = 0;
        
        // 取考勤参数中
        KqPrivForHospitalUtil kquUtil = new KqPrivForHospitalUtil(userView, conn);
        
        // 申请对应的子集 
        this.leaveAppSubSet = kquUtil.getLeave_setid();
        this.leaveAppStartItem = kquUtil.getLeave_start();
        this.leaveAppEndItem = kquUtil.getLeave_end();
        
        this.officeLeaveAppSubSet = kquUtil.getOfficeleave_setid();
        this.officeLeaveAppStartItem = kquUtil.getOfficeleave_start();
        this.officeLeaveAppEndItem = kquUtil.getOfficeleave_end();
        
        this.overtimeAppSubSet = kquUtil.getOvertime_setid();
        this.overtimeAppStartItem = kquUtil.getOvertime_start();
        this.overtimeAppEndItem = kquUtil.getOvertime_end();
        
        StringBuffer msg = new StringBuffer();
        
        // 当前申请数据是否与子集中已有记录重复
        for (int i = 0; i < recordVoList.size(); i++) {
            DynaBean app = (DynaBean)recordVoList.get(i);
            
            // 判断本次提交数据中有无申请冲突 
            String errorMsg = checkInList(recordVoList, app, i);
            if (StringUtils.isNotBlank(errorMsg)) {
                throw new GeneralException(errorMsg);
            }
            
            // 判断子集中是否有申请冲突
            if (checkRepeatInDB(conn, app)) {
                String repeatBusiTab = getBusiTabName(this.repeatAppType);
                
                msg.append("本次申请与").append(repeatBusiTab).append("子集中已有申请<span style='color: red'><strong>时间冲突或重复</strong></span>！<br>");
                msg.append("本次申请信息：<br>");
                msg.append(app.get("a0101_1").toString()).append(" ");
                msg.append(AdminCode.getCodeName(KQ_ITEM_CODESET, app.get(this.appTypeItem).toString())).append(" ");
                msg.append((String)app.get(appStartItem)).append("~").append((String)app.get(appEndItem));
                
                throw new GeneralException(msg.toString());
            }
        }
    }
    
    private String getBusiTabName(int appType) {
        String tabName = "";
        switch(appType) {
        case KqConstant.AppType.LEAVE:
            tabName = "请假";
            break;
        case KqConstant.AppType.OFFICE_LEAVE:
            tabName = "公出";
            break;
        case KqConstant.AppType.OVERTIME:
            tabName = "加班";
            break;
        }
        
        return tabName;
    }
    
    private String checkInList(ArrayList recordVoList, DynaBean app, int curIndex) {
        StringBuffer msg = new StringBuffer();
        
        String nbase = (String)app.get("basepre");
        String a0100 = (String)app.get("a0100");
        String a0101 = (String)app.get("a0101_1");
        String appIdValue = (String)app.get(appIdItem);
        String appTypeValue = (String)app.get(appTypeItem);
        String appStartValue = (String)app.get(appStartItem);
        String appEndValue = (String)app.get(appEndItem);
        
        if (StringUtils.isBlank(appStartValue) || StringUtils.isBlank(appEndValue)) {
            msg.append("申请开始和结束时间都不允许为空！<br>");
            msg.append(" 申请人员：").append(a0101);
            return msg.toString();
        }
        
        if (appStartValue.compareTo(appEndValue) >= 0) {
            msg.append("申请结束时间").append(appEndValue).append("不能小于等于开始时间").append(appStartValue).append("！<br>");
            msg.append(" 申请人员：").append(a0101);
            return msg.toString();
        }
        
        for (int i = 0; i < recordVoList.size(); i++) {
            // 同一记录
            if (i == curIndex) {
                continue;
            }
            DynaBean appInList = (DynaBean)recordVoList.get(i);
            String nbaseInList = (String)appInList.get("basepre");
            String a0100InList = (String)appInList.get("a0100");
            String a0101InList = (String)appInList.get("a0101_1");
            // 不是同一个人
            if (!nbase.equalsIgnoreCase(nbaseInList) || !a0100InList.equalsIgnoreCase(a0100)) {
                continue;
            }
            
            String appIdValueInList = (String)appInList.get(appIdItem);
            // 不同申请单号重复
            if (appIdValue.equalsIgnoreCase(appIdValueInList)) {
                msg.append("本次提交中有多个申请<strong>单号重复<strong>！<br>");
                msg.append("重复单号：").append(appIdValue);
                msg.append(" 申请人员：").append(a0101).append("、").append(a0101InList).append("<br>");
                continue;
            }
            
            String appStartValueInList = (String)appInList.get(appStartItem);
            String appEndValueInList = (String)appInList.get(appEndItem);
            // 同一人多个申请单时间冲突
            if (appStartValue.compareTo(appEndValueInList) < 0 && appEndValue.compareTo(appStartValueInList) > 0) {
                msg.append("本次提交中有同一人的多个申请<strong>时间冲突<strong>！<br>");
                msg.append(" 申请人员：").append(a0101).append("<br>");
                continue;
            }
        }
        
        return msg.toString();
    }
    
    /**
             * 检查申请与库中已有数据是否交叉
     * @param conn 数据库连接
     * @param app 申请信息
     * @return 存在交叉：true, 否则：false
     */
    private boolean checkRepeatInDB(Connection conn, DynaBean app) {
        boolean repeated = false;
        
        String nbase = (String)app.get("basepre");
        String a0100 = (String)app.get("a0100");
        String appIdValue = (String)app.get(appIdItem);
        String appStartValue = (String)app.get(appStartItem);
        String appEndValue = (String)app.get(appEndItem);
        
        // 通过模板申请的没批准前是不会进入子集的，所以暂时不需要校验申请单号
        String destAppIdValue = "";
        // 检查当前申请对应子集中是否有时段交叉
        // 其中，公出期间允许请假加班，所以只与公出申请比较即可，而请假和加班之间不能同时存在
        if (this.appType == KqConstant.AppType.OFFICE_LEAVE) {
            repeated = existInDB(conn, this.officeLeaveAppSubSet, nbase, a0100, destAppIdValue, appStartValue, appEndValue);
            if (repeated) {
                this.setRepeatAppType(KqConstant.AppType.OFFICE_LEAVE);
            }
        } else {
            repeated = existInDB(conn, this.leaveAppSubSet, nbase, a0100, destAppIdValue, appStartValue, appEndValue);
            if (repeated) {
                this.setRepeatAppType(KqConstant.AppType.LEAVE);
            }
            if (!repeated) {
                repeated = existInDB(conn, this.overtimeAppSubSet, nbase, a0100, destAppIdValue, appStartValue, appEndValue);
                if (repeated) {
                    this.setRepeatAppType(KqConstant.AppType.OVERTIME);
                }
            }
        }
        
        return repeated;
    }

    /**
              * 申请时段在库中是否已存在
     * @param conn
     * @param subset 检查的子集
     * @param nbase  人员库前缀
     * @param a0100  人员编号
     * @param appIdValue 申请编号
     * @param appStartValue 申请开启时间
     * @param appEndValue  申请结束时间
     * @return
     */
    private boolean existInDB(Connection conn, String subset, String nbase, String a0100, String appIdValue,
            String appStartValue, String appEndValue) {
        boolean exist = false;
        if (StringUtils.isBlank(subset)) {
            return exist;
        }
        
        String appStartItem = "";
        String appEndItem = "";
        if(subset.equalsIgnoreCase(this.leaveAppSubSet)) {
            appStartItem = this.leaveAppStartItem;
            appEndItem = this.leaveAppEndItem;
        } else if (subset.equalsIgnoreCase(this.officeLeaveAppSubSet)) {
            appStartItem = this.officeLeaveAppStartItem;
            appEndItem = this.officeLeaveAppEndItem;
        } else {
            appStartItem = this.overtimeAppStartItem;
            appEndItem = this.overtimeAppEndItem;
        }        
        
        ArrayList<String> params = new ArrayList<String>();
        
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT 1 FROM ").append(nbase).append(subset);
        sql.append(" WHERE a0100=?");
        params.add(a0100);
        sql.append(" and ").append(appStartItem).append("<").append(Sql_switcher.dateValue(appEndValue));
        sql.append(" and ").append(appEndItem).append(">").append(Sql_switcher.dateValue(appStartValue));
        if (StringUtils.isNotBlank(appIdValue)) {
            sql.append(" and ").append(handleItem(this.appIdItem)).append("<>?");
            params.add(appIdValue);
        }
        
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(conn);
        try {
            rs = dao.search(sql.toString(), params);
            exist = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exist;
    }
    
    
    /**
              * 将模板指标变化前变化后后缀符去掉
     * @param templateItem
     * @return
     */
    private String handleItem(String templateItem) {
        String item = templateItem;
        
        if (templateItem.contains("_2")) {
            item = templateItem.replace("_2", "");
        }
        
        if (templateItem.contains("_1")) {
            item = templateItem.replace("_1", "");
        }
        return item;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public int getRepeatAppType() {
        return repeatAppType;
    }

    public void setRepeatAppType(int repeatAppType) {
        this.repeatAppType = repeatAppType;
    }

}
