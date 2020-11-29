package com.hjsj.hrms.module.kq.util;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/**
 * 考勤权限类
 * @Title:        KqPrivBo.java
 * @Description:  处理考勤规则类相关业务
 * @Company:      hjsj     
 * @Create time:  2017-11-1 上午10:34:23
 * @author        chenxg
 * @version       1.0
 */
public class KqPrivBo {
    private UserView userView;
    private Connection conn;

    public KqPrivBo(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
    }

    /**
     * 加UN的单位编号
     * 
     * @return
     * @throws GeneralException
     */
    public String getUNB0110() {
        String b0110 = "";
        b0110 = "UN";
        try {
            if (userView.isSuper_admin())
                return b0110;

            b0110 = b0110 + getPrivCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b0110;
    }

    /**
     * 单位编号
     * 
     * @return
     * @throws GeneralException
     */
    public String getPrivCode() throws GeneralException {
        String code = "";
        HashMap<String, String> codeMap = getKqPrivCode(this.userView);
        if (!codeMap.isEmpty() && "UN".equalsIgnoreCase(codeMap.get("privCodesetId")))
            code = codeMap.get("privItemId");
        else
            code = getDbB0100();

        return code;
    }

    /**
     * 得到单位
     * 
     * @return
     * @throws GeneralException
     */
    private String getDbB0100() throws GeneralException {
        HashMap<String, String> codeMap = getKqPrivCode(this.userView);
        String code = codeMap.get("privItemId");
        String b0110 = code;
        String codesetid = "";
        String kind = codeMap.get("privCodesetId");
        if ("UM".equalsIgnoreCase(kind) || "@K".equalsIgnoreCase(kind)) {
            codesetid = code;
            do {
                String[] codeset = getB0100(b0110);
                if (codeset != null && codeset.length >= 0) {
                    codesetid = codeset[0];
                    b0110 = codeset[1];
                }
            } while (!"UN".equals(codesetid));

        }
        
        if(StringUtils.isEmpty(b0110))
            b0110 = "";
        
        return b0110;
    }

    private String[] getB0100(String codeitemid) throws GeneralException {
        String[] codeset = new String[2];
        String parentid = "";
        String codesetid = "";
        RowSet rs = null;
        try {
            String orgSql = "SELECT parentid,codeitemid,codesetid from organization where codeitemid='"
                    + codeitemid + "'";
            ContentDAO dao = new ContentDAO(this.conn);

            rs = dao.search(orgSql);
            if (rs.next()) {
                codesetid = rs.getString("codesetid");
                parentid = rs.getString("parentid");
                if (codesetid != null && "UN".equalsIgnoreCase(codesetid)) {
                    codeset[0] = "UN";
                    codeset[1] = parentid;
                } else {
                    orgSql = "SELECT parentid,codesetid from organization where codeitemid='"
                            + parentid + "'";
                    rs = dao.search(orgSql);
                    if (rs.next()) {
                        codeset[0] = rs.getString("codesetid");
                        codeset[1] = parentid;
                    }
                }
            } else {
                codeset[0] = "UN";
                codeset[1] = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return codeset;
    }
    /**
     * 依据人员编号获取单位编码
     * @param a0100 人员编号
     * @param nbase 人员库
     * @return
     */
    public String getB0110FromA0100(String a0100, String nbase) {
        if (a0100 == null || a0100.length() <= 0)
            return "";

        if (nbase == null || nbase.length() <= 0)
            return "";

        String b0110 = "";
        StringBuffer sql = new StringBuffer();
        sql.append("select B0110 from " + nbase + "A01");
        sql.append(" where A0100='" + a0100 + "'");

        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next())
                b0110 = rs.getString("B0110");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return b0110;
    }

    /**
     * 取考勤管理范围sql条件 形如： ( nbase IN ('Usr','Ret') AND ((nbase='Usr' AND
     * EXISTS(SELECT 1 FROM UsrA01 Where e0122='010102' AND
     * destTab.a0100=UsrA01.a0100)) OR (nbase='Ret' AND EXISTS(SELECT 1 FROM
     * RetA01 Where e0122='010102' AND destTab.a0100=RetA01.a0100))) )
     * 
     * @Title: getKqEmpPrivWhr
     * @Description:
     * @param conn
     *            数据库连接
     * @param userView
     *            用户
     * @param destTab
     *            应用该条件的目标表（该表必须有nbase,a0100列）
     * @return
     */
    public static String getKqEmpPrivWhr(Connection conn, UserView userView, String destTab) {
        String privSQL = "";
        String empPrivWhr = "(";
        String nbaseWhr = "nbase IN (";

        KqUtilsClass kqUtils = new KqUtilsClass(conn, userView);
        try {
            ArrayList privDBList = getB0110Dase(userView, conn);
            if (0 >= privDBList.size())
                return privSQL;

            String nbaseTemp = "nbase='#NBASE#'";
            String empTemp = "";
            empTemp = getWhereINSql(userView, "#NBASE#");
            for (int i = 0; i < privDBList.size(); i++) {
                String nbase = (String) privDBList.get(i);

                if (i > 0) {
                    nbaseWhr = nbaseWhr + ",";
                    empPrivWhr = empPrivWhr + " OR ";
                }

                nbaseWhr = nbaseWhr + "'" + nbase + "'";

                String nbasePriv = nbaseTemp.replace("#NBASE#", nbase);
                String empPriv = empTemp.replace("#NBASE#", nbase);
                empPrivWhr = empPrivWhr + "(" + nbasePriv + " AND EXISTS(SELECT 1 " + empPriv;
                if (!empPriv.toLowerCase().contains(" where "))
                    empPrivWhr = empPrivWhr + " WHERE ";
                else
                    empPrivWhr = empPrivWhr + " AND ";

                empPrivWhr = empPrivWhr + destTab + ".a0100=" + nbase + "a01.a0100))";
            }

            empPrivWhr = empPrivWhr + ")";
            nbaseWhr = nbaseWhr + ")";

            privSQL = "(" + nbaseWhr + " AND " + empPrivWhr + ")";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return privSQL;
    }

    /**
     * 判断是否是考勤员角色
     * 
     * @param nbase
     * @param a0100
     * @param conn
     * @return
     */
    public static String ischecker(String nbase, String a0100, Connection conn) {
        String ischecker = "0";

        String table = "t_sys_staff_in_role A left join t_sys_role B  on A.role_id=B.role_id";
        String whr = " A.staff_id = '" + nbase + a0100 + "' and B.role_property=3";
        KqDBHelper dbHelper = new KqDBHelper(conn);
        if (dbHelper.isRecordExist(table, whr))
            ischecker = "1";

        return ischecker;
    }
    /**
     * 获取考勤范围编码及类型
     * @param userView
     * @return
     */
    public static LazyDynaBean getKqPrivCodeAndKind(UserView userView) {
        String code = "";
        String kind = "";
        LazyDynaBean bean = new LazyDynaBean();
        HashMap<String, String> codeMap = getKqPrivCode(userView);
        code = null==codeMap.get("privItemId") ? "" : codeMap.get("privItemId");
        if ("UN".equals(codeMap.get("privCodesetId")))
            kind = "2";
        else if ("UM".equals(codeMap.get("privCodesetId")))
            kind = "1";
        else if ("@K".equals(codeMap.get("privCodesetId")))
            kind = "0";
        else
            kind = "2";

        bean.set("code", code);
        bean.set("kind", kind);
        return bean;
    }

    /**
     * 得到考勤管理范围编码
     * 
     * @param userView
     * @return
     */
    public static HashMap<String, String> getKqPrivCode(UserView userView) {
        HashMap<String, String> kqPrivCodeMap = new HashMap<String, String>(); 
        if(userView.isSuper_admin())
            return kqPrivCodeMap;
        
        String privCodesetId = "";
        String privItemId = "";
        String privCode = userView.getKqManageValue();
        if (privCode != null && privCode.length() > 0) {
            privCodesetId = privCode.substring(0, 2);
            privItemId = privCode.substring(2);
        } else {
            privCodesetId = userView.getManagePrivCode();
            privItemId = userView.getManagePrivCodeValue();
        }
        
        kqPrivCodeMap.put("privCodesetId", privCodesetId);
        kqPrivCodeMap.put("privItemId", privItemId);

        return kqPrivCodeMap;
    }
    /**
     * 按人员库生成考勤权限的sql
     * @param kq_dbase_list 人员库List
     * @param userView 登录用户
     * @return
     */
    public static String getPrvListWhere(ArrayList kq_dbase_list, UserView userView) {
        StringBuffer condition = new StringBuffer();
        for (int i = 0; i < kq_dbase_list.size(); i++) {
            if (i > 0)
                condition.append(" or ");
            else
                condition.append(" and ( ");

            condition.append(" UPPER(nbase)='" + kq_dbase_list.get(i).toString().toUpperCase()
                    + "'");
            if (i == kq_dbase_list.size() - 1)
                condition.append(")");
        }

        for (int i = 0; i < kq_dbase_list.size(); i++) {
            String dbase = kq_dbase_list.get(i).toString();
            String whereIN = getWhereINSql(userView, dbase);
            if (i > 0)
                condition.append(" or ");
            else
                condition.append(" and ( ");

            condition.append(" a0100 in(select " + kq_dbase_list.get(i).toString() + "A01.a0100 "
                    + whereIN + ") ");
            if (i == kq_dbase_list.size() - 1)
                condition.append(")");
        }
        return condition.toString();
    }
    /**
     * 获取考勤范围内的顶级机构编码和机构类型
     * @param codeitemid 机构编码
     * @param conn 数据库链接
     * @return
     * @throws GeneralException
     */
    public static String[] getB0100(String codeitemid, Connection conn) throws GeneralException {
        String[] codeset = new String[2];
        String parentid = "";
        String codesetid = "";
        RowSet rs = null;
        try {
            String orgSql = "SELECT parentid,codeitemid,codesetid  from organization where codeitemid='"
                    + codeitemid + "'";
            ContentDAO dao = new ContentDAO(conn);

            rs = dao.search(orgSql);
            if (rs.next()) {
                parentid = rs.getString("parentid");
                codesetid = rs.getString("codesetid");
                if (codesetid != null && "UN".equalsIgnoreCase(codesetid)) {
                    codeset[0] = "UN";
                    codeset[1] = parentid;
                } else {
                    orgSql = "SELECT parentid,codesetid from organization where codeitemid='"
                            + parentid + "'";
                    rs = dao.search(orgSql);
                    if (rs.next()) {
                        codeset[0] = rs.getString("codesetid");
                        codeset[1] = parentid;
                    }
                }
            } else {
                codeset[0] = "UN";
                codeset[1] = parentid;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return codeset;
    }

    /**
     * 得到单位
     * 
     * @param code
     * @param kind
     * @param conn
     * @return
     * @throws GeneralException
     */
    public static String getDbB0100(String code, String kind, Connection conn)
            throws GeneralException {
        String b0110 = code;
        String codesetid = "";
        if ("1".equals(kind) || "0".equals(kind)) {
            codesetid = code;
            do {
                String[] codeset = getB0100(b0110, conn);
                if (codeset != null && codeset.length >= 0) {
                    codesetid = codeset[0];
                    b0110 = codeset[1];
                }
                
            } while (!"UN".equals(codesetid));

        }
        return b0110;
    }

    /**
     * 和whereIN一块使用的sql
     * 
     * @param sqlstr
     * @param wherestr
     * @param code
     * @param userView
     * @param userbase
     * @return String 例如select a0100 from XXXa01 where xxxx lik '123%'
     */
    public static String getUnionKqDepartSql(UserView userView, String nbase) {
        StringBuffer buff = new StringBuffer();
        if (!userView.isSuper_admin()) {
            buff.append(" union select a0100 from ");
            buff.append(nbase);
            buff.append("a01 where ");
            String field = KqParam.getInstance().getKqDepartment();
            /**
             * 如果设置了考勤部门，既根据管理范围权限控制， 同时也需要兼顾考勤归属部门。 变动前后部门的考勤员都可以看到异动人员的全月数据。
             */

            if (!"".equalsIgnoreCase(field)) {
                HashMap<String, String> codeMap = getKqPrivCode(userView);
                String code = codeMap.get("privItemId");
                if (code == null || code.length() <= 0) {// 走管理范围
                    code = userView.getManagePrivCodeValue();
                    buff.append(field + " like '" + code + "%'");
                } else
                    // 考勤范围
                    buff.append(field + " like '" + code + "%'");

            } else
                return " ";

        }

        return buff.toString();
    }

    /**
     * 根据权限,生成select.IN中的查询串
     * 
     * @param code
     *            链接级别
     * @param userbase
     *            库前缀
     * @param cur_date
     *            考勤日期
     * @return 返回查询SQL串
     * */
    public static String getWhereINSql(UserView userView, String userbase) {
        String strwhere = "";

        if (!userView.isSuper_admin()) {
            ArrayList fieldlist = new ArrayList();
            try {
                String kqManageValue = userView.getKqManageValue();
                if (null == kqManageValue)
                    kqManageValue = "";

                // 是考勤员角色用户，走考勤员角色权限
                if (!"".equals(kqManageValue))
                    strwhere = userView.getKqPrivSQLExpression("", userbase, fieldlist);
                else { // 不是考勤员角色，仍走用户的管理范围权限
                    String privExpression = getPrivExpression(userView);
                    strwhere = userView.getPrivSQLExpression(privExpression, userbase, false,
                            fieldlist);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            StringBuffer wheresql = new StringBuffer();
            wheresql.append(" from ");
            wheresql.append(userbase);
            wheresql.append("A01 ");

            strwhere = wheresql.toString();
        }

        return strwhere;
    }

    private static String getPrivExpression(UserView userView) {

        String expr = "1";
        String factor = "";
        String orgField = "";

        if ("UN".equals(userView.getManagePrivCode()))
            orgField = "B0110";
        else if ("UM".equals(userView.getManagePrivCode()))
            orgField = "E0122";
        else if ("@K".equals(userView.getManagePrivCode()))
            orgField = "E01A1";

        if (!"".equals(orgField)) {
            factor = orgField + "=";
            if (userView.getManagePrivCodeValue() != null
                    && userView.getManagePrivCodeValue().length() > 0) {
                factor += userView.getManagePrivCodeValue();
                factor += "%`";
            } else {
                factor += "%`" + orgField + "=`";
                expr = "1+2";
            }
        } else {
            factor = "B0110=";
            if (userView.getManagePrivCodeValue() != null
                    && userView.getManagePrivCodeValue().length() > 0)
                factor += userView.getManagePrivCodeValue();

            factor += "%`B0110=`";
            expr = "1+2";
        }

        return expr + "|" + factor;
    }

    /**
     * 一个单位的人员库权限
     * 
     * @param formHM
     * @param userView
     * @param conn
     * @return
     */
    public static ArrayList<String> getB0110Dase(UserView userView, Connection conn) {
        ArrayList<String> kq_dbase_list = new ArrayList<String>();
        try {
            KqVer kqVer = new KqVer();
            int ver = kqVer.getVersion();
            
            // 标准版考勤
            if (ver == KqConstant.Version.STANDARD) {
                String nbases = getKqParameter(conn).get("nbase");
                
                ArrayList dbaselist = userView.getPrivDbList(); // 求应用库前缀权限列表
                for (int i = 0; i < dbaselist.size(); i++) {
                    String userbase = dbaselist.get(i).toString();
                    if (nbases.indexOf(userbase) != -1) {
                        kq_dbase_list.add(userbase);
                    }
                }                
            } else {
                // 医院高校版考勤
                kq_dbase_list = KqPrivForHospitalUtil.getB0110Dase(userView, conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kq_dbase_list;
    }

    /**
     * 检索常量表中纪录为KQ_PARAMETER的Constant
     * **/
    public static String search_KQ_PARAMETER(Connection conn) {
        StringBuffer sb = new StringBuffer();
        String constant = "constant";
        if (Sql_switcher.searchDbServer() == Constant.KUNLUN)
            constant = "\"constant\"";
        sb.append("select Str_Value from " + constant + " where UPPER(" + constant
                + ")='KQ_PARAMETER'");
        ContentDAO dao = new ContentDAO(conn);
        RowSet rowSet = null;
        String xmlConstant = "";
        try {
            rowSet = dao.search(sb.toString());
            if (rowSet.next()) 
                xmlConstant = Sql_switcher.readMemo(rowSet, "Str_Value");

            // 防止strValue为空的情况
            if (xmlConstant == null || "".equals(xmlConstant.trim()))
                xmlConstant = init_XMLData(conn);
            

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return xmlConstant;
    }

    /**
     * 当常量表中没有KQ_PARAMETER记录，或KQ_PARAMETER中Constant为空的时候处理
     * */
    public static String init_XMLData(Connection conn) {
        StringBuffer xmlstr = new StringBuffer();
        xmlstr.append("<?xml version='1.0' encoding='GBK'?>");
        xmlstr.append("<kq cardno='' g_no='' kq_type=''>");
        xmlstr.append("<parameter B0110='UN'>");
        xmlstr.append("<nbase value=''/>");
        xmlstr.append("</parameter></kq>");
        ArrayList<String> deletelist = new ArrayList<String>();
        deletelist.add("KQ_PARAMETER");
        String deleteSQL = "delete from constant where Constant=?";
        ContentDAO dao = new ContentDAO(conn);
        try {
            dao.delete(deleteSQL, deletelist);
            StringBuffer insertSQL = new StringBuffer();
            insertSQL.append("insert into constant (Constant,Type,Describe,Str_Value)");
            insertSQL.append(" values (?,?,?,?)");
            ArrayList<String> insertlist = new ArrayList<String>();
            insertlist.add("KQ_PARAMETER");
            insertlist.add("A");
            insertlist.add("考勤参数");
            insertlist.add(xmlstr.toString());
            dao.insert(insertSQL.toString(), insertlist);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return xmlstr.toString();
    }
    /**
     * 获取考勤参数
     * @return
     */
    public static HashMap<String, String> getKqParameter(Connection conn) {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        try {
            // zxj changed 2014.02.03 人员库参数不再分单位，直接取“UN”
            String xmlContent = search_KQ_PARAMETER(conn);
            
            if (xmlContent != null && xmlContent.length() > 0) {
                Document doc = PubFunc.generateDom(xmlContent);// 读入xml
                String xpath = "/kq";
                XPath reportPath = XPath.newInstance(xpath);// 取得符合条件的节点
                
                List childlist = reportPath.selectNodes(doc);
                Iterator i = childlist.iterator();
                if (i.hasNext()) {
                    /** 报表基本参数 **/
                    Element childR = (Element) i.next();
                    String cardno = childR.getAttributeValue("cardno");
                    cardno = StringUtils.isEmpty(cardno) ? "" : cardno;
                    hashmap.put("cardno", cardno);
                    
                    String gNo = childR.getAttributeValue("g_no");
                    gNo = StringUtils.isEmpty(gNo) ? "" : gNo;
                    hashmap.put("g_no", gNo);
                    
                    String kqType = childR.getAttributeValue("kq_type");
                    kqType = StringUtils.isEmpty(kqType) ? "" : kqType;
                    hashmap.put("kq_type", kqType);
                }
                
                xpath = "/kq/parameter[@B0110='UN']";
                reportPath = XPath.newInstance(xpath);// 取得符合条件的节点
                childlist = reportPath.selectNodes(doc);
                i = childlist.iterator();
                if (i.hasNext()) {
                    /** 报表基本参数 **/
                    Element childR = (Element) i.next();
                    hashmap.put("b0110", childR.getAttributeValue("B0110"));
                    // 考勤人员库
                    Element nbase = childR.getChild("nbase");
                    String dd = nbase.getAttributeValue("value");
                    hashmap.put("nbase", dd);
                }
            }
            
            if (hashmap.isEmpty()) {
                hashmap.put("b0110", "UN");
                hashmap.put("nbase", "");// 考勤人员库
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return hashmap;
    }

    /**
     * 得到当前考勤员可操作的部门
     * 
     * @param nbase
     *            人员库
     * @param b0110
     *            机构编码
     * @param org_id
     *            机构指标
     * @param whereIN
     *            关于权限的sql
     * @return
     */
    public static String selcetB0100OrgId(String nbase, String b0110, String org_id, String whereIN) {
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select " + org_id + " from " + nbase + "A01 A ");
        sqlstr.append(" where");
        if (StringUtils.isNotEmpty(b0110))
            sqlstr.append(" b0110='" + b0110 + "' and ");

        if (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)
            sqlstr.append(" EXISTS(select 1 " + whereIN + " and " + nbase + "A01." + org_id
                    + "=A." + org_id + ")");
        else
            sqlstr.append(" EXISTS(select 1 " + whereIN + " where " + nbase + "A01." + org_id
                    + "=A." + org_id + ")");
        
        sqlstr.append(" and EXISTS(select 1 from organization where A." + org_id
                + "=organization.codeitemid)");
        sqlstr.append(" group by " + org_id);
        return sqlstr.toString();
    }

    /**
     * 得到在考勤范围内的部门员工编号，并添加到list中
     * 
     * @param conn
     *            数据库链接
     * @param strsql
     *            sql语句
     * @param org_id
     *            员工编号指标
     * @return
     * @throws GeneralException
     */
    public static ArrayList getQrgE0122List(Connection conn, String strsql, String org_id)
            throws GeneralException {

        ContentDAO dao = new ContentDAO(conn);
        ArrayList<String> orglist = new ArrayList<String>();
        RowSet rowSet = null;
        try {
            rowSet = dao.search(strsql.toString());
            while (rowSet.next()) {
                String orgvalue = rowSet.getString(org_id);
                if (orgvalue != null && orgvalue.length() > 0)
                    orglist.add(orgvalue);

            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return orglist;
    }
    
    /**
     * 添加考勤模块权限控制下的组织机构、人员库代码生成类（表格控件不支持考勤人员库、考勤角色等特殊处理）
     * @param fieldItem 指标对象
     * @param columnsInfo 表格组件列对象
     */
    public static void setKqPrivCodeSource(FieldItem fieldItem, ColumnsInfo columnsInfo) {
        if (fieldItem == null || columnsInfo == null) {
            return;
        }
        
        if("UN".equalsIgnoreCase(fieldItem.getCodesetid())
           || "UM".equalsIgnoreCase(fieldItem.getCodesetid())
           || "@K".equalsIgnoreCase(fieldItem.getCodesetid()))
            columnsInfo.setCodesource("GetKqOrgTree");
        else if("@@".equalsIgnoreCase(fieldItem.getCodesetid()))
            columnsInfo.setCodesource("GetKqNbaseTree");
    }
    
}
