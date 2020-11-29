package com.hjsj.hrms.service;

import com.hjsj.hrms.businessobject.gz.GzServiceBo;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.pigeonhole.UpdateQ33;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.interfaces.webservice.HrIssuanceInterf;
import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HrpIssuanceService {
    /**
     * Auto generated method signature 常用统计
     *
     * @param hrStaticsXml
     */

    public String HrStaticsXml(String username, String password,
                               String checkoutpwd, String key, String flag) {

        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        Connection conn = null;
        String xml = "";
        if (!hrIssuanceInterf.checkKey(key, flag)) {
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！");
        }
        boolean isCorrect = false;
        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                UserView userView = hrIssuanceInterf.getSetView(username,
                        password, checkoutpwd, conn);
                if (userView != null) {
                    xml = hrIssuanceInterf.getHrStatics(conn);
                    isCorrect = true;
                } else {
                    xml = hrIssuanceInterf.getErrorMessage("没有该用户");
                }
            } else
                // System.out.println("数据池连接没有成功");
                xml = hrIssuanceInterf.getErrorMessage("数据池连接没有成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                // sql.printStackTrace();
            }
        }
        return xml;
    }

    /**
     * Auto generated method signature 公告
     *
     * @param hrBoardXml
     */

    public String HrBoardXml(String username, String password,
                             String checkoutpwd, String key, String flag) {
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        ;
        Connection conn = null;
        String xml = "";
        if (!hrIssuanceInterf.checkKey(key, flag)) {
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！");
        }
        boolean isCorrect = false;
        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                UserView userView = hrIssuanceInterf.getSetView(username,
                        password, checkoutpwd, conn);
                if (userView != null) {
                    xml = hrIssuanceInterf.getHrBoardContent(conn);
                    isCorrect = true;
                } else {
                    xml = hrIssuanceInterf.getErrorMessage("没有该用户");
                }
            } else
                xml = hrIssuanceInterf.getErrorMessage("数据池连接没有成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                // sql.printStackTrace();
            }
        }
        return xml;
    }

    /**
     * Auto generated method signature 预警
     *
     * @param hrWarnXmlRequest
     */
    public String HrWarnXml(String username, String password,
                            String checkoutpwd, String key, String flag) {
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        ;
        Connection conn = null;
        String xml = "";
        if (!hrIssuanceInterf.checkKey(key, flag)) {
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！");
        }
        boolean isCorrect = false;
        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                UserView userView = hrIssuanceInterf.getSetView(username,
                        password, checkoutpwd, conn);
                if (userView != null) {
                    xml = hrIssuanceInterf.getHrSysWarn(conn);
                    isCorrect = true;
                } else {
                    xml = hrIssuanceInterf.getErrorMessage("没有该用户");
                }
            } else
                xml = hrIssuanceInterf.getErrorMessage("数据池连接没有成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                // sql.printStackTrace();
            }
        }
        return xml;
    }

    /**
     * Auto generated method signature 报表
     *
     * @param hrBoardXml
     */

    public String HrReportXml(String username, String password,
                              String checkoutpwd, String key, String flag) {
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        ;
        Connection conn = null;
        String xml = "";
        if (!hrIssuanceInterf.checkKey(key, flag)) {
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！");
        }
        boolean isCorrect = false;
        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                UserView userView = hrIssuanceInterf.getSetView(username,
                        password, checkoutpwd.toLowerCase(), conn);
                if (userView != null) {
                    xml = hrIssuanceInterf.getHrReportContent(conn);
                    isCorrect = true;
                } else {
                    xml = hrIssuanceInterf.getErrorMessage("没有该用户");
                }
            } else
                xml = hrIssuanceInterf.getErrorMessage("数据池连接没有成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                // sql.printStackTrace();
            }
        }
        return xml;
    }

    /**
     * Auto generated method signature 请假、公出、请假申请
     *
     * @param hrBoardXml
     */

    public String HrKQInfXml(String username, String password,
                             String checkoutpwd, String key, String flag) {
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        ;
        Connection conn = null;
        String xml = "";
        if (!hrIssuanceInterf.checkKey(key, flag)) {
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！");
        }
        boolean isCorrect = false;
        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                UserView userView = hrIssuanceInterf.getSetView(username,
                        password, checkoutpwd.toLowerCase(), conn);
                if (userView != null) {
                    xml = hrIssuanceInterf.getHrkqContent(conn);
                    isCorrect = true;
                } else {
                    xml = hrIssuanceInterf.getErrorMessage("没有该用户");
                }
            } else
                xml = hrIssuanceInterf.getErrorMessage("数据池连接没有成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                // sql.printStackTrace();
            }
        }
        return xml;
    }

    /**
     * Auto generated method signature 请假、公出、请假申请
     *
     * @param hrBoardXml
     */

    public String HrKQInfXml(String username, String password,
                             String checkoutpwd, String key, String flag, String type) {
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        ;
        Connection conn = null;
        String xml = "";
        if (!hrIssuanceInterf.checkKey(key, flag)) {
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！");
        }
        boolean isCorrect = false;
        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                UserView userView = hrIssuanceInterf.getSetView(username,
                        password, checkoutpwd.toLowerCase(), conn);
                if (userView != null) {
                    xml = hrIssuanceInterf.getHrkqContent(conn, type);
                    isCorrect = true;
                } else {
                    xml = hrIssuanceInterf.getErrorMessage("没有该用户");
                }
            } else
                xml = hrIssuanceInterf.getErrorMessage("数据池连接没有成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                // sql.printStackTrace();
            }
        }
        return xml;
    }

    /**
     * 通过关联指标得到eHR系统用户名获取待办消息
     */
    public String getMessageByRelevance(String username) {
        String returnValue = "";
        String userN = "";
        List dbList = new ArrayList();
        String Userfield = "username";
        List lists = new ArrayList();
        List businessUser = new ArrayList();
        String column = SystemConfig.getPropertyValue("columnName");


        //System.out.print("column:"+username);
        Connection conn = null;
        RowSet rs = null;
        try {
            conn = AdminDb.getConnection();
            RecordVo user_vo = ConstantParamter.getConstantVo("SS_LOGIN");
            String dbStr = user_vo.getString("str_value");

            RecordVo user_vo1 = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
            String fielduser = user_vo1.getString("str_value");
            //验证用户名和密码字段
            if (fielduser != null && fielduser.indexOf(",") > 0) {
                Userfield = fielduser.substring(0, fielduser.indexOf(","));
                if ("".equals(Userfield) || "#".equals(Userfield))
                    Userfield = "username";
            }

            if (dbStr != null && dbStr.length() > 0) {
                String[] str = dbStr.split(",");
                for (int i = 0; i < str.length; i++) {
                    if (str[i] != null && str[i].length() > 0) {
                        dbList.add(str[i]);
                    }
                }
            }

            if (column == null || column.length() <= 0) {
                column = Userfield;
            }

            StringBuffer buff = new StringBuffer();
            ContentDAO dao = new ContentDAO(conn);
            buff.append("select nbase,a0100," + Userfield);
            buff.append(" from (");
            List userList = new ArrayList();

            for (int i = 0; i < dbList.size(); i++) {
                buff.append("select '");
                buff.append(dbList.get(i).toString());
                buff.append("' nbase,a0100," + Userfield);
                buff.append(" from ");
                buff.append(dbList.get(i).toString());
                buff.append("A01 where upper(");
                buff.append(Userfield);
                buff.append(")= ( ");
                buff.append("select upper(" + Userfield);
                buff.append(") from " + dbList.get(i).toString());
                buff.append("A01 where upper(" + column);
                buff.append(") = ?)");
                userList.add(username.toUpperCase());
                if (i != dbList.size() - 1) {
                    buff.append(" union all ");
                }
            }
            buff.append(") bb ");
            rs = dao.search(buff.toString(), userList);
            if (rs.next()) {
                lists.add(rs.getString("nbase").toUpperCase());
                lists.add(rs.getString("a0100"));
                userN = rs.getString(Userfield);
            }

            if (lists.size() == 2) {
                StringBuffer sbuff = new StringBuffer();
                sbuff.append("select UserName,password from OperUser where upper(Nbase)=? and A0100=?");
                rs = dao.search(sbuff.toString(), lists);
                if (rs.next()) {
                    if (rs.getString("username") != null) {
                        username = rs.getString("username");
                    }
                } else {    //如果没有关联业务用户 则取自助用户名登录
                    username = userN;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        returnValue = HrMatterXml(username, "", "false", "ZkMhYprHJ9ZvBMCpzsT3+1YfsDXkcy6R7iEniGcg8Ug8fMlpbBDWHw==", "cncec");
        return returnValue;
    }


    /**
     * 为市政集团单独开发的webservice（与美络公司oa系统开发的）
     *
     * @param username String y用户名
     * @param key
     * @param flag
     * @return
     */
    public String getHrMatterXml(String username, String key, String flag) {
        StringBuffer buff = new StringBuffer();
        try {
            String xml = HrMatterXml(username, "", "false", key, flag);
            String hrp_logon_url = SystemConfig.getProperty("hrp_logon_url");
            List list = new ArrayList();
            if (xml != null && xml.length() > 0) {
                PareXmlUtils utils = new PareXmlUtils(xml);
                list = utils.getNodes("/rss/channel/item");
            }

            buff.append("<?xml version='1.0' encoding='GB2312'?>");
            buff.append("<IMServicesResponse>");
            buff.append("<SystemMessages to='" + username + "' hint='hint' IsClear='0' color='#ffffff' group='人事系统' count='" + list.size() + "'>");
            if (list.size() > 0) {
                buff.append("<SystemMessage type='w'><id>001</id><desc>待办(" + list.size() + ")</desc><url>" + hrp_logon_url + "/templates/attestation/sz/bzpslogon.jsp?user_ID={username}&skipurl=/general/template/matterList.do?b_query=link`ver=5</url></SystemMessage>");
            }
            buff.append("<SystemMessage type='t'><id>001</id><desc>人事待办(" + list.size() + ")</desc><url>" + hrp_logon_url + "/templates/attestation/sz/bzpslogon.jsp?user_ID={username}&skipurl=/general/template/matterList.do?b_query=link`ver=5</url></SystemMessage>");
            buff.append("</SystemMessages>");
            buff.append("</IMServicesResponse>");


        } catch (Exception e) {
            e.printStackTrace();
        }

        return buff.toString();

    }

    /**
     * 待办信息
     *
     * @param username    用户名
     * @param password    密码
     * @param checkoutpwd 是否检测密码
     * @param key
     * @param flag
     * @return
     */
    public String HrMatterXml(String username, String password,
                              String checkoutpwd, String key, String flag) {
        String xml = "";

        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();

        if (!hrIssuanceInterf.checkKey(key, flag)) {
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！");
        }
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                UserView userView = hrIssuanceInterf.getSetView(username,
                        password, checkoutpwd.toLowerCase(), conn);
                if (userView != null) {
                    xml = hrIssuanceInterf.getHrMatterContent(conn);
                } else {
                    xml = hrIssuanceInterf.getErrorMessage("没有该用户");
                }
            } else
                xml = hrIssuanceInterf.getErrorMessage("数据池连接没有成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                // sql.printStackTrace();
            }
        }
        return xml;
    }

    /**
     * 待办信息
     *
     * @param username 用户名
     * @return
     */
    public String pendingWork(String username) {
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        ;
        Connection conn = null;
        String xml = "";
        String erroStr = "<?xml version='1.0' encoding='UTF-8'?><pendingWorks><totalnumber><![CDATA[0]]></totalnumber></pendingWorks>";
        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                UserView userView = hrIssuanceInterf.getSetView(username, "",
                        "false", conn);
                if (userView != null) {
                    xml = hrIssuanceInterf.getHrMatterContent2(conn);

                } else {
                    xml = erroStr;
                }
            } else
                xml = erroStr;
        } catch (Exception e) {
            e.printStackTrace();
            return erroStr;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                // sql.printStackTrace();
            }
        }
        return xml;
    }

    /**
     * 获得待办列表（中建）
     *
     * @param username
     * @param password
     * @param checkoutpwd
     * @param key
     * @param flag
     * @return
     */
    public String getHrMatterXml(String usercode, String token) {
        String xml = "";
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        if (hrIssuanceInterf.checkZJValide(token)) {
            Connection conn = null;
            try {
                conn = AdminDb.getConnection();
                if (conn != null) {
                    UserView userView = hrIssuanceInterf.getSetView(usercode,
                            "", "false".toLowerCase(), conn);
                    if (userView != null) {
                        xml = hrIssuanceInterf.getZJHrMatterContent(conn);
                    } else {
                        xml = hrIssuanceInterf.getErrorMessage("没有该用户");
                    }
                } else
                    xml = hrIssuanceInterf.getErrorMessage("数据池连接没有成功");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            xml = hrIssuanceInterf.getErrorMessage("中建待办访问验证失败！");
        }

        return xml;
    }

    /**
     * Auto generated method signature
     *
     * @param isUserName
     */
    public boolean isUserName(String username, String password,
                              String validatepwd) {

        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        ;
        Connection conn = null;
        boolean isCorrect = false;
        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                UserView userView = hrIssuanceInterf.getSetView(username,
                        password, validatepwd, conn);
                if (userView != null) {
                    isCorrect = true;
                }
            } else
                System.out.println("数据池连接没有成功");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                // sql.printStackTrace();
            }
        }
        return isCorrect;
    }

    /**
     * 根据获得etoken
     *
     * @param username 用户名
     * @param key
     * @param flag
     * @return
     */
    public String getEToken(String username, String key, String flag) {

        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        // 验证错误
        if (!hrIssuanceInterf.checkKey(key, flag)) {
            return "";
        }

        String etoken = "";
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                UserView userView = hrIssuanceInterf.getSetView(username, "",
                        "false", conn);
                if (userView != null) {
                    etoken = PubFunc.convertUrlSpecialCharacter(PubFunc
                            .convertTo64Base(username + ","
                                    + userView.getPassWord()));
                }
            } else {
                etoken = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                // sql.printStackTrace();
            }
        }

        return etoken;
    }

    /**
     * 返回人事异动审批结果（HR系统提供，OA系统调用）
     *
     * @param yddid  task_id
     * @param result 1：成功；0：失败
     * @return 1：成功；0：失败；
     */
    public String processResult(String yddid, String result) {
        String info = "1";
        SynOaService sos = new SynOaService();
        info = sos.processResult(yddid, result);
        return info;
    }

    /**
     * 外部系统调用此方法，通知 e-HR系统执行数据导入功能
     *
     * @param xml_param <?xml version="1.0" encoding="GB2312"?>
     *                  <hr>
     *                  <params> <!--数据导入/映射关系的作业类标识，程序需按哪几个映射关系执行导入操作-->
     *                  <taskinfo>xxxx</taskinfo> <!--外部数据过滤条件,SQL片段-->
     *                  <filter_str><![CDATA[ b0125 is not null]]> </filter_str>
     *                  <!--eHR数据保护条件,SQL片段,如果无保护条件，此节点不存在--> <protect_str><![CDATA[
     *                  b0125 is not null]]></ protect _str>
     *                  <!--目标库信息，需执行移库操作移库操作默认将原库信息删掉,如果不执行移库操作，此节点不存在-->
     *                  <to_nbase>Oth</to_nbase> </params>
     *                  </hr>
     * @return 1:成功 2：失败
     * @author dengc
     */
    public String impInfoByNotice(String xml_param) {
        String flag = "2";
        Connection conn = null;
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                flag = hrIssuanceInterf.impInfoByMidtable(xml_param, conn);
            } else
                flag = "2";
        } catch (Exception e) {
            e.printStackTrace();
            flag = "2";
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                // sql.printStackTrace();
            }
        }

        return flag;
    }

    /**
     * 获得HR信息
     *
     * @param username    String 用户名
     * @param password    String 密码
     * @param checkoutpwd String 是否验证密码，true为验证密码，false为不验证密码
     * @param key         String hr系统分配的固定值
     * @param flag        String hr系统分配的固定值
     * @param kind        String 种类
     * @param type        String 类型
     * @return
     */
    public String getHrInfo(String username, String password,
                            String checkoutpwd, String key, String flag, String kind,
                            String type) {
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        ;
        Connection conn = null;
        String xml = "";
        if (!hrIssuanceInterf.checkKey(key, flag)) {
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！");
        }

        try {
            conn = AdminDb.getConnection();
            if (conn != null) {
                UserView userView = hrIssuanceInterf.getSetView(username,
                        password, checkoutpwd.toLowerCase(), conn);
                if (userView != null) {
                    xml = hrIssuanceInterf.getInfo(conn, kind, type);

                } else {
                    xml = hrIssuanceInterf.getErrorMessage("没有该用户");

                    // 如果是待办个数，没有这个用户时返回0（华宇工程提出的要求，wangzhongjun改）
                    if ("N".equalsIgnoreCase(kind)) {
                        xml = "0";
                    }
                }
            } else
                xml = hrIssuanceInterf.getErrorMessage("数据池连接没有成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sql) {
                sql.printStackTrace();
            }
        }
        return xml;
    }

    public String getHrInfoByID(String idname, String id, String checkoutpwd,
                                String key, String flag, String kind, String type) {
        String userName = getUserNameByID(idname, id);
        return getHrInfo(userName, "", "false", key, flag, kind, type);
    }

    private String getUserNameByID(String idname, String id) {
        String userName = "";
        String nbase = "";
        String A0100 = "";
        RowSet rs = null;
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            if (null != conn) {
                ContentDAO dao = new ContentDAO(conn);
                StringBuffer sql = new StringBuffer();
                DbNameBo dbNameBo = new DbNameBo(conn);

                ArrayList dbNames = dbNameBo.getAllDbNameVoList();

                // 根据id指标值查找人员
                if (null != dbNames && !dbNames.isEmpty()) {
                    for (int i = 0; i < dbNames.size(); i++) {
                        RecordVo recordVo = (RecordVo) dbNames.get(i);
                        nbase = recordVo.getString("pre");

                        sql.append("SELECT a0100 FROM ");
                        sql.append(nbase + "A01");
                        sql.append(" WHERE UPPER(");
                        sql.append((idname));
                        sql.append(")='");
                        sql.append(id.toUpperCase());
                        sql.append("'");

                        rs = dao.search(sql.toString());
                        if (rs.next()) {
                            A0100 = rs.getString("a0100");
                            break;
                        }
                    }
                }

                // 人员存在
                if (!"".equals(A0100)) {
                    // 检查人员与操作用户是否关联
                    userName = getOperUserName(dao, nbase, A0100);

                    // 没有关联操作用户，继续取自助用户账号
                    if ("".equals(userName)) {
                        userName = getSelfUserName(dao, nbase, A0100);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != rs)
                    rs.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userName;
    }

    private String getOperUserName(ContentDAO dao, String nbase, String A0100) {
        String userName = "";
        RowSet rs = null;

        StringBuffer sql = new StringBuffer();

        sql.append("SELECT username FROM operuser");
        sql.append(" WHERE nbase='");
        sql.append(nbase);
        sql.append("' AND A0100='");
        sql.append(A0100);
        sql.append("'");

        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                userName = rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return userName;

    }

    private String getSelfUserName(ContentDAO dao, String nbase, String A0100) {
        String userName = "";
        RowSet rs = null;

        try {
            RecordVo loginVo = ConstantParamter
                    .getConstantVo("SS_LOGIN_USER_PWD");
            String strValue = loginVo.getString("str_value");
            String[] logonInfo = null;
            if (null != strValue && !"".equals(strValue)) {
                logonInfo = strValue.split(",");
            }

            if (!"".equals(logonInfo[0]) && logonInfo[0] != null) {
                StringBuffer sql = new StringBuffer();

                sql.append("SELECT ");
                sql.append(logonInfo[0]);
                sql.append(" FROM ");
                sql.append(nbase);
                sql.append("A01");
                sql.append(" WHERE A0100='");
                sql.append(A0100);
                sql.append("'");

                rs = dao.search(sql.toString());
                if (rs.next()) {
                    userName = rs.getString(logonInfo[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return userName;

    }

    public String Huayu_peWageRecv(String info) {
        String flag = "true";
        Connection conn = null;
        try {
            Document doc = PubFunc.generateDom(info);
            XPath xpath = XPath.newInstance("/msg/gzperiod");
            Element element = (Element) xpath.selectSingleNode(doc);
            String gzperiod = "";
            String unitcode = "";
            String deptcode = "";
            if (element != null)
                gzperiod = element.getText();
            xpath = XPath.newInstance("/msg/unitcode");
            element = (Element) xpath.selectSingleNode(doc);
            if (element != null)
                unitcode = element.getText();
            xpath = XPath.newInstance("/msg/deptcode");
            element = (Element) xpath.selectSingleNode(doc);
            if (element != null)
                deptcode = element.getText();
            conn = AdminDb.getConnection();
            GzServiceBo bo = new GzServiceBo(conn);
            flag = bo.impGzData(gzperiod, unitcode, deptcode);
        } catch (Exception e) {
            flag = "false";
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 获取eHR系统假期天数（可休、已休天数）
     *
     * @param key
     * @param flag
     * @param xmlMessage
     * @return
     * @throws Exception
     */
    public String getHolidayMsg(String key, String flag, String xmlMessage) {
        Connection conn = null;
        String info = "ok"; // 要返回的异常信息
        double useddays = 0.0; // 已休天数
        double remaindays = 0.0; // 可休天数
        String nbase = null;
        String A0100 = null; // 人员编号
        String userid = null;
        String htype = null;
        String StrHdate = null;
        Date hdate = null;
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        if (!hrIssuanceInterf.checkKey(key, flag))
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！");

        try {
            Document doc = PubFunc.generateDom(xmlMessage);
            Element root = doc.getRootElement();
            userid = ((Element) root.getChildren("userid").get(0)).getText();
            htype = ((Element) root.getChildren("htype").get(0)).getText();
            StrHdate = ((Element) root.getChildren("hdate").get(0)).getText()
                    .replace('.', '-');

            if (userid == null || userid == "" || htype == null || htype == ""
                    || StrHdate == null || StrHdate == "")
                return strToXml("传入的值格式不正确");
        } catch (Exception e) {
            // e.printStackTrace();
            return strToXml("传入的xml格式错误!");
        }

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            hdate = new Date(df.parse(StrHdate).getTime());
            if (!StrHdate.equals(df.format(df.parse(StrHdate))))// 抛异常就不是正确格式
                return strToXml("传入的请假日期格式不正确");
        } catch (Exception e2) {
            e2.printStackTrace();
            return strToXml("传入的请假日期格式不正确");
        }

        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);

            ConstantXml csXML = new ConstantXml(conn, "SYS_OTH_PARAM");
            org.jdom.Element e = csXML
                    .getElement("/param/chk_uniqueness/field[@type='0']");
            String valid = e.getAttributeValue("valid");
            String name = e.getAttributeValue("name");

            if (!"1".equals(valid))
                return strToXml("无法查找对应人员！人力资源系统未设置人员唯一性指标。");

            HashMap hm = selectHtype(conn, dao, htype);
            String codeItemId = (String) hm.get("codeitemid");
            if (codeItemId == null)
                return strToXml("没有该请假类型!");

            String[] dbName = getKqDbName(conn);
            if (dbName.length == 0)
                return strToXml("当前人力资源系统未设置考勤人员库！无法查询。");

            ArrayList emplist = null;
            emplist = getA0100ByUseridAndDbName(dao, name, userid, dbName);

            if (0 == emplist.size())
                return strToXml("没有该人员信息");

            nbase = (String) emplist.get(0);
            A0100 = (String) emplist.get(1);

            hm = getQueryDaysInfo(conn, (String) hm.get("querytype"), nbase,
                    A0100, codeItemId, hdate);
            if (hm != null && !hm.isEmpty()) {
                useddays = ((Double) hm.get("useddays")).doubleValue();
                remaindays = ((Double) hm.get("remaindays")).doubleValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return strToXml(info, useddays, remaindays);
    }

    /**
     * 获取eHR系统假期天数（可休、已休天数）
     *
     * @param key
     * @param flag
     * @param xmlMessage
     * @return
     * @throws Exception
     */
    public String getRemainHolidays(String key, String flag, String xmlMessage) {
        Connection conn = null;
        String info = "ok"; // 要返回的异常信息
        double useddays = 0.0; // 已休天数
        double remaindays = 0.0; // 可休天数
        String nbase = null;
        String A0100 = null; // 人员编号
        String userid = null;
        String htype = null;
        String StrHdate = null;
        String startDate = "";
        String endDate = "";
        Date hdate = null;
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        if (!hrIssuanceInterf.checkKey(key, flag))
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！");

        try {
            Document doc = PubFunc.generateDom(xmlMessage);
            Element root = doc.getRootElement();
            userid = ((Element) root.getChildren("userid").get(0)).getText();
            htype = ((Element) root.getChildren("htype").get(0)).getText();
            StrHdate = ((Element) root.getChildren("hdate").get(0)).getText()
                    .replace('.', '-');
            startDate = ((Element) root.getChildren("sdate").get(0)).getText();
            endDate = ((Element) root.getChildren("edate").get(0)).getText();

            if (userid == null || userid == "" || htype == null || htype == ""
                    || StrHdate == null || StrHdate == "")
                return strToXml("传入的值格式不正确");
        } catch (Exception e) {
            // e.printStackTrace();
            return strToXml("传入的xml格式错误!");
        }

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            hdate = new Date(df.parse(StrHdate).getTime());

            DateUtils.getDate(startDate, "yyyy-MM-dd HH:mm:ss");
            DateUtils.getDate(endDate, "yyyy-MM-dd HH:mm:ss");
            if (!StrHdate.equals(df.format(df.parse(StrHdate))))// 抛异常就不是正确格式
                return strToXml("传入的请假日期格式不正确");
        } catch (Exception e2) {
            e2.printStackTrace();
            return strToXml("传入的请假日期格式不正确");
        }

        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);

            String name = "";
            try {
                name = SystemConfig
                        .getPropertyValue("interface_holiday_uniqfile");

                if (name == null || name.length() <= 0) {
                    return strToXml("在system.properties文件中未设置interface_holiday_uniqfile参数，请设置：interface_holiday_uniqfile=人员唯一对应指标");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return strToXml("在system.properties文件中未设置interface_holiday_uniqfile参数，请设置：interface_holiday_uniqfile=人员唯一对应指标");
            }

            String[] dbName = getKqDbName(conn);
            if (dbName.length == 0)
                return strToXml("当前人力资源系统未设置考勤人员库！无法查询。");

            ArrayList emplist = null;
            emplist = getA0100ByUseridAndDbName(dao, name, userid, dbName);

            if (0 == emplist.size())
                return strToXml("没有该人员信息");

            A0100 = (String) emplist.get(1);
            nbase = (String) emplist.get(0);
            String b0110 = (String) emplist.get(2);

            AnnualApply annual = new AnnualApply(new UserView("su", conn), conn);

            String sels = getKqItem(htype, conn);

            if (sels == null || sels.length() <= 0)
                return strToXml("没有该请假类型!");

            if (KqParam.getInstance().isHoliday(conn, b0110, sels)) {
                HashMap kqItem_hash = annual.count_Leave(sels);
                float myTime = annual.getMy_Time(sels, A0100, nbase, OperateDate.dateToStr(DateUtils.getDate(startDate, "yyyy-MM-dd HH:mm:ss"), "yyyy.MM.dd HH:mm"), OperateDate.dateToStr(DateUtils.getDate(endDate, "yyyy-MM-dd HH:mm:ss"), "yyyy.MM.dd HH:mm"), b0110, kqItem_hash);
//                float other_time = annual.othenSealTime(sels, startDate, endDate, A0100, nbase, b0110, "", kqItem_hash, "add", "");

                remaindays = myTime;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return strToXml2(info, 0, remaindays);
    }


    /*
     * 得到查询的天数信息（某个年假、调休假等的已休和可休天数）
     */
    private HashMap getQueryDaysInfo(Connection conn, String queryType,
                                     String nbase, String A0100, String codeItemId, Date hdate) {
        HashMap hm = null;
        // 组装请求数据
        HashMap queryInfo = new HashMap();
        queryInfo.put("nbase", nbase);
        queryInfo.put("a0100", A0100);
        queryInfo.put("codeitemid", codeItemId);
        queryInfo.put("hdate", hdate);

        if ("0".equals(queryType)) {
            ContentDAO dao = new ContentDAO(conn);
            hm = getHolidayDays(dao, queryInfo);
        } else if ("1".equals(queryType))
            hm = getOverTimeToRestDays(conn, queryInfo);

        return hm;
    }

    /*
     * 获取某人某假期天数信息
     */
    private HashMap getHolidayDays(ContentDAO dao, HashMap queryInfo) {
        HashMap hm = new HashMap();
        RowSet rs = null;
        try {
            String sql = "select Q1705,Q1707 from Q17 where nbase=? and A0100=? and Q1709=? and ? between Q17z1 and Q17z3";
            ArrayList list = new ArrayList();
            list.add((String) queryInfo.get("nbase"));
            list.add((String) queryInfo.get("a0100"));
            list.add((String) queryInfo.get("codeitemid"));
            list.add((Date) queryInfo.get("hdate"));

            rs = dao.search(sql, list);
            if (rs.next()) {
                double useddays = rs.getDouble(1) >= 0 ? rs.getDouble(1) : 0.0;
                double remaindays = rs.getDouble(2) >= 0 ? rs.getDouble(2)
                        : 0.0;

                hm.put("useddays", Double.valueOf(useddays));
                hm.put("remaindays", Double.valueOf(remaindays));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            close(null, rs);
        }

        return hm;
    }

    /*
     * 获取某人调休天数信息
     */
    private HashMap getOverTimeToRestDays(Connection conn, HashMap queryInfo) {
        HashMap hm = new HashMap();

        GetValiateEndDate gve = new GetValiateEndDate(null, conn);
        int usableTime = gve.getTimesCount(new java.util.Date(),
                (String) queryInfo.get("nbase"),
                (String) queryInfo.get("a0100"), conn);
        // 将分钟数据转换成天（1天=8小时）
        double usableDays = usableTime / 480.0;

        hm.put("useddays", Double.valueOf(usableDays));
        hm.put("remaindays", Double.valueOf(usableDays));

        return hm;
    }

    /**
     * 更新eHR系统假期表
     *
     * @param key
     * @param flag
     * @param xmlMessage
     * @return
     * @throws GeneralException
     * @throws IOException
     */
    public String syncHolidayMsg(String key, String flag, String xmlMessage) {
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        if (!hrIssuanceInterf.checkKey(key, flag))
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！"); // 这个已经是 XML
        // 了

        String info = "ok";
        Connection conn = null;
        RowSet rs = null;
        double day = 0.0;// 可休天数
        String userid = null, htype = null, hdays = null, strHdate = null;
        String nbase = null, A0100 = null;
        Date hdate = null;

        try {
            Document doc = PubFunc.generateDom(xmlMessage);
            Element root = doc.getRootElement();
            userid = ((Element) root.getChildren("userid").get(0)).getText();
            htype = ((Element) root.getChildren("htype").get(0)).getText();
            hdays = ((Element) root.getChildren("hdays").get(0)).getText();
            strHdate = ((Element) root.getChildren("hdate").get(0)).getText().replace('.', '-');

            if (userid == null || userid == "" || htype == null || htype == ""
                    || hdays == null || hdays == "")
                return strToXml("传入的值格式不正确");
        } catch (Exception e2) {
            e2.printStackTrace();
            return strToXml("传入的xml格式有问题!");
        }

        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            hdate = new Date(df.parse(strHdate).getTime());
            if (!strHdate.equals(df.format(df.parse(strHdate))))// 抛异常就不是正确格式
                return strToXml("传入的请假日期格式不正确");
        } catch (Exception e2) {
            e2.printStackTrace();
            return strToXml("传入的请假日期格式不正确");
        }

        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            ConstantXml csXML = new ConstantXml(conn, "SYS_OTH_PARAM");
            org.jdom.Element e = csXML
                    .getElement("/param/chk_uniqueness/field[@type='0']");
            String valid = e.getAttributeValue("valid");
            String name = e.getAttributeValue("name");

            if (!"1".equals(valid))
                return strToXml("无法查找对应人员！人力资源系统未设置人员唯一性指标。");

            HashMap hm = selectHtype(conn, dao, htype);
            String codeItemId = (String) hm.get("codeitemid");
            if (codeItemId == null)
                return strToXml("没有该请假类型!");

            String[] dbName = getKqDbName(conn);
            if (dbName.length == 0)
                return strToXml("当前人力资源系统未设置考勤人员库！无法查询。");

            ArrayList emplist = null;
            emplist = getA0100ByUseridAndDbName(dao, name, userid, dbName);

            if (0 == emplist.size())
                return strToXml("没有该人员信息");

            nbase = (String) emplist.get(0);
            A0100 = (String) emplist.get(1);

            String queryType = (String) hm.get("querytype");
            try {
                if (Double.parseDouble(hdays) <= 0)
                    return strToXml("请假天数必须大于0!");

                // 得到当前剩余天数，检查天数是否够用
                HashMap hmDays = getQueryDaysInfo(conn, queryType, nbase,
                        A0100, codeItemId, hdate);
                day = ((Double) hmDays.get("remaindays")).doubleValue();

                if (Double.parseDouble(hdays) > day)
                    return strToXml("请假天数超过可休天数!");
            } catch (Exception d) {
                return strToXml("传入的请假天数格式不正确!");
            }

            nbase = (String) emplist.get(0);
            A0100 = (String) emplist.get(1);

            if (!updatedays(conn, queryType, nbase, A0100, hdate, hdays,
                    codeItemId))
                return strToXml("扣减假期天数失败!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, rs);

        }
        return strToXml(info);
    }

    public String updateHolidays(String key, String flag, String xmlMessage) {
        HrIssuanceInterf hrIssuanceInterf = new HrIssuanceInterf();
        if (!hrIssuanceInterf.checkKey(key, flag))
            return hrIssuanceInterf.getErrorMessage("传入的KEY值有错误！"); // 这个已经是 XML
        // 了

        String info = "ok";
        Connection conn = null;
        RowSet rs = null;
        double day = 0.0;// 可休天数
        String userid = null, htype = null, hdays = null, strHdate = null;
        String nbase = null, A0100 = null, start = "", end = "";
        java.util.Date hdate = null;
        String userField = "";

        try {
            Document doc = PubFunc.generateDom(xmlMessage);
            Element root = doc.getRootElement();
            userid = ((Element) root.getChildren("userid").get(0)).getText();
            htype = ((Element) root.getChildren("htype").get(0)).getText();
            hdays = ((Element) root.getChildren("hdays").get(0)).getText();
            strHdate = ((Element) root.getChildren("hdate").get(0)).getText();
            start = ((Element) root.getChildren("sdate").get(0)).getText();
            end = ((Element) root.getChildren("edate").get(0)).getText();

            if (userid == null || userid == "" || htype == null || htype == ""
                    || start == null || start.length() <= 0 || end == null
                    || end.length() <= 0)
                return strToXml("传入的值格式不正确");
        } catch (Exception e2) {
            e2.printStackTrace();
            return strToXml("传入的xml格式有问题!");
        }

        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            hdate = DateUtils.getDate(strHdate, "yyyy-MM-dd");

            DateUtils.getDate(start, "yyyy-MM-dd HH:mm:ss");

            DateUtils.getDate(end, "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e2) {
            e2.printStackTrace();
            return strToXml("传入的日期格式不正确");
        }

        try {
            userField = SystemConfig
                    .getPropertyValue("interface_holiday_uniqfile");

            if (userField == null || userField.length() <= 0) {
                return strToXml("在system.properties文件中未设置interface_holiday_uniqfile参数，请设置：interface_holiday_uniqfile=人员唯一对应指标");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return strToXml("在system.properties文件中未设置interface_holiday_uniqfile参数，请设置：interface_holiday_uniqfile=人员唯一对应指标");
        }

        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            String querytype = "0";
            HashMap hm = selectHtype(conn, dao, htype);

            querytype = (String) hm.get("querytype");

            if ("1".equals(querytype)) {
                return syncHolidayMsg(key, flag, xmlMessage);

            }


            String sels = getKqItem(htype, conn);

            if (sels == null || sels.length() <= 0) {

                return strToXml("没有该请假类型!");
            }

            String[] dbName = getKqDbName(conn);
            if (dbName.length == 0)
                return strToXml("当前人力资源系统未设置考勤人员库！无法查询。");

            ArrayList emplist = null;
            emplist = getA0100ByUseridAndDbName(dao, userField, userid, dbName);

            if (0 == emplist.size())
                return strToXml("没有该人员信息");

            nbase = (String) emplist.get(0);
            A0100 = (String) emplist.get(1);
            String b0110 = (String) emplist.get(2);

            try {
                if (Double.parseDouble(hdays) <= 0)
                    return strToXml("请假天数必须大于0!");
            } catch (Exception w) {
                w.printStackTrace();
            }

            // 处理假期管理里面的数据

            AnnualApply annualApply = new AnnualApply(null, conn);
            float[] holiday_rules = annualApply.getHoliday_minus_rule();// 年假假期规则

            HashMap holidayTypeMap = new HashMap();

            ArrayList updateSqlList = new ArrayList();

            if (isHoliday(conn, holidayTypeMap, b0110, sels)) {
                HashMap kqItem_hash = annualApply.count_Leave(sels);

                java.sql.Date kq_start = DateUtils.getSqlDate(start,
                        "yyyy-MM-dd HH:mm:ss");
                ;
                java.sql.Date kq_end = DateUtils.getSqlDate(end,
                        "yyyy-MM-dd HH:mm:ss");

                String starts = DateUtils.format(kq_start,
                        "yyyy.MM.dd HH:mm:ss");
                String ends = DateUtils.format(kq_end, "yyyy.MM.dd HH:mm:ss");
                float leave_tiem = annualApply
                        .getHistoryLeaveTime(kq_start, kq_end, A0100, nbase,
                                b0110, kqItem_hash, holiday_rules);
                String history = annualApply.upLeaveManage(A0100, nbase, sels,
                        starts, ends, leave_tiem, "1", b0110, kqItem_hash,
                        holiday_rules);

                String sqlStart = "";
                String sqlEnd = "";
                if (Sql_switcher.searchDbServer() == 1) {
                    sqlStart = "cast('" + start + "' as datetime)";
                    sqlEnd = "cast('" + end + "' as datetime)";
                } else {
                    sqlStart = "to_date('" + start + "','yyyy-MM-dd HH:mm:ss')";
                    sqlEnd = "to_date('" + end + "', 'yyyy-MM-dd HH:mm:ss')";
                }
                String updateSql = "update q15 set history='" + history
                        + "' where a0100='" + A0100 + "'and upper(nbase)='"
                        + nbase.toUpperCase() + "' and q1503='" + sels
                        + "' and q15z1=" + sqlStart + " and  q15z3=" + sqlEnd;

                // updateData(sql, new ArrayList(), conn);

                dao.update(updateSql);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return strToXml(info);
    }

    private boolean isHoliday(Connection conn, HashMap holidayTypeMap, String b0110, String leaveTypeId) {
        String holiday_type = "";
        if (holidayTypeMap.containsKey(b0110)) {
            holiday_type = (String) holidayTypeMap.get(b0110);
        } else {
            holiday_type = KqParam.getInstance().getHolidayTypes(conn, b0110);
            holidayTypeMap.put(b0110, holiday_type);
        }

        return ("," + holiday_type + ",").indexOf("," + leaveTypeId + ",") != -1;
    }

    /**
     * 修改请假天数
     *
     * @param hdate 请假时间
     * @param A0100 请假人编号
     * @param field 请假类型编号
     * @return 请假是否成功
     */
    private boolean updatedays(Connection conn, String queryType, String nbase,
                               String A0100, Date hdate, String hdays, String codeItemId) {
        boolean uptResult = false;

        HashMap updateInfo = new HashMap();
        updateInfo.put("nbase", nbase);
        updateInfo.put("a0100", A0100);
        updateInfo.put("codeitemid", codeItemId);
        updateInfo.put("hdays", hdays);
        updateInfo.put("hdate", hdate);

        if ("0".equals(queryType)) {
            ContentDAO dao = new ContentDAO(conn);
            uptResult = updateRestDays(dao, updateInfo);
        } else if ("1".equals(queryType))
            uptResult = updateOverTimeToRestDays(conn, updateInfo);

        return uptResult;
    }

    /*
     * 更新年假天数
     */
    private boolean updateRestDays(ContentDAO dao, HashMap updateInfo) {
        int uptCount = 0;
        try {
            String sql = "update Q17 set Q1707=Q1707-?,Q1705=Q1705+? where nbase=? and  A0100=? and Q1709=? and ? between q17z1 and q17z3";
            ArrayList list = new ArrayList();
            list.add((String) updateInfo.get("hdays"));
            list.add((String) updateInfo.get("hdays"));
            list.add((String) updateInfo.get("nbase"));
            list.add((String) updateInfo.get("a0100"));
            list.add((String) updateInfo.get("codeitemid"));
            list.add((Date) updateInfo.get("hdate"));
            uptCount = dao.update(sql, list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return uptCount > 0;
    }

    /*
     * 更新调休假天数
     */
    private boolean updateOverTimeToRestDays(Connection conn, HashMap updateInfo) {
        boolean uptResult = false;
        int appTime = (int) (Float.parseFloat((String) updateInfo.get("hdays")) * 480);
        UpdateQ33 uptQ33 = new UpdateQ33(null, conn);
        uptResult = uptQ33.upQ33((String) updateInfo.get("nbase"), (String) updateInfo.get("a0100"), appTime);
        return uptResult;
    }

    /**
     * 获得当前设置的考勤库
     *
     * @return 人员库
     * @throws GeneralException
     */
    private String[] getKqDbName(Connection conn) {
        // TODO 暂时取第一个考勤人员库参数设置,有多单位设置不同时,会有问题
        ConstantXml csXML = new ConstantXml(conn, "KQ_PARAMETER");
        org.jdom.Element e = csXML.getElement("/kq/parameter[1]/nbase");
        return e.getAttributeValue("value").split(",");
    }

    /**
     * 根据人员标示、标示值和人员库获得人员编号
     *
     * @param name   唯一标示
     * @param userid 标示值
     * @param dbName 人员库
     * @return 人员库，人员编号
     */
    private ArrayList getA0100ByUseridAndDbName(ContentDAO dao, String name,
                                                String userid, String[] dbName) {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        String A0100 = null;
        try {
            StringBuffer sql = new StringBuffer();
            for (int i = 0; i < dbName.length; i++) {
                sql.append("select A0100,'");
                sql.append(dbName[i]);
                sql.append("' as nbase,b0110 ");
                sql.append(" from ");
                sql.append(dbName[i]);
                sql.append("A01 where ");
                sql.append(name);
                sql.append("='");
                sql.append(userid);
                sql.append("'");
                if (dbName.length > 1 && i < dbName.length - 1)
                    sql.append(" union all ");
            }
            rs = dao.search(sql.toString());
            if (rs.next()) {
                A0100 = rs.getString(1);
                String b0110 = rs.getString("b0110");
                b0110 = b0110 == null ? "" : b0110;
                list.add(rs.getString(2));
                list.add(A0100);
                list.add(b0110);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 根据请假类型名称获得请假类型编码
     *
     * @param htype
     * @return
     */
    private HashMap selectHtype(Connection conn, ContentDAO dao, String htype) {
        HashMap hm = new HashMap();

        RowSet rs = null;
        try {
            ArrayList list = new ArrayList();
            list.add(htype);

            String sql = "select codeitemid from codeitem where codeitemdesc=?";
            rs = dao.search(sql, list);
            if (rs.next()) {
                String codeItemId = rs.getString(1);

                // 存在要求的请假类型，检查是否为假期管理假类，或调休假
                KqParam kqParam = KqParam.getInstance();

                String holidayTypes = "," + kqParam.getHolidayTypes(conn, new UserView("su", conn)) + ",";
                if (holidayTypes.contains("," + codeItemId + ","))
                    hm.put("querytype", "0");
                else {
                    String leaveTimeTypeUsedOverTime = kqParam
                            .getLeaveTimeTypeUsedOverTime();
                    if (leaveTimeTypeUsedOverTime.equalsIgnoreCase(codeItemId))
                        hm.put("querytype", "1");
                    else
                        hm.put("querytype", "");
                }

                hm.put("codeitemid", rs.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return hm;
    }

    /**
     * 修改为XML格式
     *
     * @param info
     * @param useddays
     * @param remaindays
     * @return
     * @throws IOException
     */
    private String strToXml(String info, double useddays, double remaindays) {
        Document doc = new Document();
        Element root = new Element("ehr");
        doc.setRootElement(root);

        if (info != null)
            root.addContent(new Element("info").addContent(info));

        if (useddays >= 0)
            root.addContent(new Element("useddays").addContent(Double
                    .toString(useddays)));

        if (remaindays >= 0)
            root.addContent(new Element("remaindays").addContent(Double
                    .toString(remaindays)));

        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        XMLOutputter xmlout = new XMLOutputter(format);

        return xmlout.outputString(doc);
    }

    private String strToXml2(String info, double useddays, double remaindays) {
        Document doc = new Document();
        Element root = new Element("ehr");
        doc.setRootElement(root);

        if (info != null)
            root.addContent(new Element("info").addContent(info));

//		if (useddays >= 0)
//			root.addContent(new Element("useddays").addContent(Double
//					.toString(useddays)));

        if (remaindays >= 0)
            root.addContent(new Element("remaindays").addContent(Double
                    .toString(remaindays)));

        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        XMLOutputter xmlout = new XMLOutputter(format);

        return xmlout.outputString(doc);
    }

    private String strToXml(String info) {
        return strToXml(info, -1, -1);
    }

    /**
     * 关闭连接
     *
     * @param conn
     * @param rs
     */
    private void close(Connection conn, RowSet rs) {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getKqItem(String item_name, Connection conn) {
        RowSet rs = null;
        String item = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search("select item_id from kq_item where item_name='"
                    + item_name + "'");
            if (rs.next()) {
                item = rs.getString("item_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return item;
    }


}
