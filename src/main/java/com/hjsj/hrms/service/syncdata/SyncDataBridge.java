package com.hjsj.hrms.service.syncdata;


import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SyncDataBridge {
    private String xmlMessage;
    private Logger log = LoggerFactory.getLogger(SyncDataThread.class);

    public SyncDataBridge(String xmlMessage) {
        this.xmlMessage = xmlMessage;
    }

    public SyncDataBridge() {

    }

    /**
     * ehr发送的xmlmessage
     * <p>
     * <?xml version="1.0" encoding="UTF-8"?><root>
     * <recs>
     * <rec>emp</rec>
     *
     * </recs>
     * <jdbc>
     * <sysid>ESB</sysid>
     * <ip_addr>127.0.0.1</ip_addr>
     * <port>1433</port>
     * <username>yksoft</username>
     * <pass>yksoft1919</pass>
     * <database>gqys</database>
     * <datatype>mssql</datatype>
     * <emp_table>t_hr_view</emp_table>
     * <org_table>t_org_view</org_table>
     * <post_table>t_post_view</post_table>
     * <emp_where>unique_id in ('0F8287F9-C6B9-4A20-90E7-47F0B6261CB4','DF07FAAC-66C0-49FA-BCEE-B78D4E45CEBF')</emp_where>
     * <org_where>1=2</org_where>
     * <post_where>1=2</post_where>
     * </jdbc>
     * </root>
     */

    public String run() {
        /** 找到当前同步系统对应的配置文件并解析
         （1）解析xmlmessage，通过节点<sysid>可知道当前需要同步的系统代号，如代号为ESB，则找相应的配置文件ESB.xml。
         （2）获取指标对应关系及主键等信息
         （3）获取当前配置发送类
         */
        String errorMsg = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("本次数据同步开始,开始时间:{}", sdf.format(new Date()));
        Connection conn = null;
        log.debug("EHR发送给接口服务的xml格式：");
        log.debug(xmlMessage);
        try {
            conn = AdminDb.getConnection();
            SyncDataParam syncDataParam = new SyncDataParam(xmlMessage);
            SyncDataUtil syncDataUtil = new SyncDataUtil(conn, syncDataParam);
            String sendclass = syncDataParam.getSendClassName();
            boolean bOrg = syncDataParam.isSyncOrg();//是否同步机构
            boolean bPost = syncDataParam.isSyncPost();//是否同步岗位
            boolean bEmp = syncDataParam.isSyncEmp();//是否同步人员
            Boolean isComplete = syncDataParam.getIsComplete();
            /** 获取需要同步的数据
             （1）从视图表中获取所有需要同步的数据（新增、更新、删除），以list方式组装lazybean的方式。
             （2）需考虑机构及人员使用的主键等信息
             */
            ArrayList<LazyDynaBean> orgDataList = null;
            ArrayList<LazyDynaBean> postDataList = null;
            ArrayList<LazyDynaBean> empDataList = null;
            if (bOrg) {
                orgDataList = syncDataUtil.getOrgSyncData();
            }
            if (bPost) {
                postDataList = syncDataUtil.getPostSyncData();
            }

            if (bEmp) {
                empDataList = syncDataUtil.getEmpSyncData();
            }
            /** 调用发送类，由具体的发送类完成发送工作
             （1）调用发送类，将需要同步的数据datalist作为参数传送给发送类。
             （2）发送类与目标系统进行交互，组装报文------->发送报文------>接收处理是否成功报文。
             （3）调用公用方法，更新发送是否成功状态。
             */

            SyncDataInter implClass = null;
            if (sendclass != null && sendclass.length() > 0) {
                try {
                    implClass = (SyncDataInter) Class.forName(sendclass).newInstance();
                    implClass.setConnection(conn);
                    implClass.setSyncDataParam(syncDataParam);
                    implClass.init();
                    //顺序进行数据同步(1：组织机构新增、更新 2：岗位新增、更新 3：人员新增、更新 4：人员删除 5：岗位删除 6：组织机构删除)
                    //同步机构
                    if (bOrg) {
                        log.info("同步机构数据新增更新开始,开始时间:" + sdf.format(new Date()));
                        errorMsg = implClass.syncOrgDataAddOrUpdate(orgDataList);
                        log.info("同步机构数据新增更新结束,结束时间:" + sdf.format(new Date()));
                    }
                    //同步岗位
                    if (bPost) {
                        log.info("同步岗位数据新增更新开始,开始时间:" + sdf.format(new Date()));
                        errorMsg = implClass.syncPostDataAddOrUpdate(postDataList);
                        log.info("同步岗位数据新增更新结束,结束时间:" + sdf.format(new Date()));
                    }
                    if (bEmp) {
                        log.info("同步人员数据新增更新开始,开始时间:" + sdf.format(new Date()));
                        errorMsg = implClass.syncEmpDataAddOrUpdate(empDataList);
                        log.info("同步人员数据新增更新结束,结束时间:" + sdf.format(new Date()));
                        if (!isComplete) {
                            log.info("同步人员数据删除开始,开始时间:" + sdf.format(new Date()));
                            errorMsg = implClass.syncEmpDataDelete(empDataList);
                            log.info("同步人员数据删除结束,结束时间:" + sdf.format(new Date()));
                        }
                    }
                    if (!isComplete) {
                        if (bPost) {
                            log.info("同步岗位数据删除开始,开始时间:" + sdf.format(new Date()));
                            errorMsg = implClass.syncPostDataDelete(postDataList);
                            log.info("同步岗位数据删除结束,结束时间:" + sdf.format(new Date()));
                        }
                        if (bOrg) {
                            log.info("同步机构数据删除开始,开始时间:" + sdf.format(new Date()));
                            errorMsg = implClass.syncOrgDataDelete(orgDataList);
                            log.info("同步机构数据删除结束,结束时间:" + sdf.format(new Date()));
                        }
                    }
                    log.info("本次数据同步结束,结束时间:{}", sdf.format(new Date()));
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                // 关闭数据库连接
                PubFunc.closeDbObj(conn);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return errorMsg;
    }
}