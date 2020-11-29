package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.resource.ScormXMLBo;
import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 培训中用到的查询方法 权限 组装权限范围内sqlWhere
 * LiWeichao
 */
public class TrainCourseBo {

    private UserView user;
    private Connection conn;
    private String table;

    public TrainCourseBo() {
    }

    public TrainCourseBo(UserView user) {
        this.user = user;
    }

    public TrainCourseBo(Connection conn) {
        this.conn = conn;
    }

    public TrainCourseBo(String table) {
        this.table = table;
    }

    public TrainCourseBo(UserView user, Connection conn) {
        this.user = user;
        this.conn = conn;
    }

    /**
     * 培训中的设置参数
     * 
     * @param str_path
     * @param attributeName
     * @return
     */
    public String getTrparam(String str_path, String attributeName) {
        ConstantXml constantbo = new ConstantXml(conn, "TR_PARAM");
        String str = constantbo.getNodeAttributeValue(str_path, attributeName);
        str = str == null || str.length() < 1 || "#".equals(str) || "###".equals(str) ? "" : str;
        return str;
    }

    /**
     * 当前用户的单位编码(UN01`UN02)
     * 
     * @param user
     * @return
     */
    public String getUnitIdByBusi() throws GeneralException {
        String b0110 = "UN`";// user.getUnitIdByBusi("6");
        try{
            if (user != null && !user.isSuper_admin()) {
            	// 1、业务用户：先取业务操作单位->操作单位->管理范围
            	// 2、自助用户：先取关联的业务用户的（业务操作单位->操作单位）->自身的业务操作单位->管理范围->所属单位
            	b0110 = user.getUnitIdByBusi("6");
            	if (b0110 == null || "".equals(b0110) || "UN".equalsIgnoreCase(b0110)
            			|| "UM`".equalsIgnoreCase(b0110) || "@K`".equalsIgnoreCase(b0110)) {
                    throw new Exception("您没有培训模块的管理范围权限！请联系管理员。");
                }
            	
            	b0110 = PubFunc.getHighOrgDept(b0110.replaceAll("`", ","));
            	if (b0110.length() < 3 && user.getStatus() == 4) {
            		b0110 = "UN" + user.getUserOrgId();
            	}
//            	去掉培训获取考勤权限的代码，如有业务需求再做修改
//                String kqpriv = user.getKqManageValue();
//                if (!(kqpriv.length() > 0 && kqpriv != null)) {
//                } else {
//                    b0110 = kqpriv + ",";
//                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("", "您没有培训模块的管理范围权限！请联系管理员。", "", "");
        }
        return b0110.replaceAll(",", "`");
    }

    /**
     * 当前用户的单位编码(UN01`UN02)
     * 
     * @param user
     * @return
     */
    public static String getUnitIdByBusiWhere(UserView user) {
        TrainCourseBo bo = new TrainCourseBo(user);
        StringBuffer where = new StringBuffer();
        try {
            String b0110 = bo.getUnitIdByBusi();
            where.append(" where (");
            if (b0110 != null && b0110.length() > 2 && b0110.indexOf("UN`") == -1) {
                String[] t = b0110.split("`");
                for (int i = 0; i < t.length; i++) {
                    if (t != null && t[i].length() > 2) {
                        String tt = t[i].substring(2);
                        if (tt == null || tt.length() < 1) {
                            continue;
                        }
                        where.append(" b0110 like '" + tt + "%' or");
                    }
                }
                if (where.toString().endsWith("or")) {
                    where.setLength(where.length() - 3);
                }
                where.append(" or b0110='HJSJ')");
            } else if (b0110 == null || b0110.length() < 3) {
                where.append(" b0110='HJSJ')");
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return where.toString();
    }
    /**
     * 培训班操作权限校验
     * @param user
     * @return
     * @throws GeneralException
     */
    public static String getUnitIdByBusiStrWhere(UserView user) throws GeneralException {
        TrainCourseBo bo = new TrainCourseBo(user);
        String code = bo.getUnitIdByBusi();

        StringBuffer where = new StringBuffer();
        if (code != null && code.length() > 2 && code.indexOf("UN`") == -1) {
            String unitarr[] = code.split("`");
            String str = "";
            for (int i = 0; i < unitarr.length; i++) {
                if (unitarr[i] != null && unitarr[i].trim().length() > 2 && "UN".equalsIgnoreCase(unitarr[i].substring(0, 2))) {
                    str += "B0110 like '" + unitarr[i].substring(2) + "%' or ";
                } else {
                    if (unitarr[i].trim().length() > 2) {
                        str += "E0122 like '" + unitarr[i].substring(2) + "%' or ";
                    }
                }
            }
            if (str.length() > 0) {
                where.append(" and (" + str + " b0110='HJSJ')");
            } else {
                throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
            }
        } else if (code == null || code.length() < 3) {
            where.append(" and (b0110='HJSJ')");
        } else {
            return " and 1=1 ";
        }
        return where.toString();
    }

    /**
     * 判断该代码项是否为上级
     * 
     * @param id
     *            ,dao
     * @return
     */
    public boolean getCodeIsParent(String id) {
        boolean isParent = false;
        String sql = "select r5020 from r50 where r5000='" + id + "'";
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                String tmpb0110 = rs.getString("r5020");
                if ((tmpb0110 != null && !"".equals(tmpb0110)) && (isUserParent(tmpb0110) == 2 || isUserParent(tmpb0110) == -1)) {
                    isParent = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return isParent;
    }

    /**
     * 判断单位是否是当前用户的上级单位(非超级用户)
     * 
     * @param tmpb0110
     *            单位编码
     * @return -1:权限范围外 2：上级 3、1：权限范围内
     */
    public int isUserParent(String tmpb0110) {
        int isParent = -1;
        try {
            tmpb0110 = tmpb0110 == null || tmpb0110.length() < 1 ? "UN`" : tmpb0110;// 非超级用户查看
            // codeitem.b0110=""
            // 默认为
            // isParent
            // =
            // 2;
            if (tmpb0110.length() < 1) {
                return 0;
            }
            String b0110 = getUnitIdByBusi();
            if ("UN`".equals(tmpb0110)) {
                if ("UN`".equals(b0110)) {
                    isParent = 1;
                } else {
                    isParent = 2;
                }
            } else {
                tmpb0110 = PubFunc.getHighOrgDept(tmpb0110.replaceAll("`", ","));
                b0110 = PubFunc.getHighOrgDept(b0110.replaceAll("`", ","));
                String tmparr[] = tmpb0110.split(",");
                String arr[] = b0110.split(",");
                for (int i = 0; i < tmparr.length; i++) {
                    String tmp = tmparr[i];
                    if (tmp == null || tmp.length() < 1) {
                        continue;
                    }
                    tmp = tmp.replaceAll("UN", "").replaceAll("UM", "");
                    for (int j = 0; j < arr.length; j++) {
                        if (arr[j] == null || arr[j].length() < 1) {
                            continue;
                        }
                        arr[j] = arr[j].replaceAll("UN", "").replaceAll("UM", "");
                        if (arr[j].equalsIgnoreCase(tmp)) {
                            isParent = 1;
                            break;// 有一个应该就有权限吧
                        } else if (arr[j].startsWith(tmp)) {
                            isParent = 2;
                            break;
                        } else if (tmp.startsWith(arr[j])) {
                            isParent = 3;
                            break;// 有一个应该就有权限吧
                        }
                    }
                    if (isParent == 2) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isParent;
    }

    /**
     * 组成sql语句
     * 
     * @param sexpr
     *            因子表达式
     * @param sfactor
     *            条件
     * @param dbpre
     *            人员库
     * @param query_type
     *            区别查询类型,[1.简单查询 2.通用查询]
     * @param history
     *            是否为历史记录查询 [1.是 0.否]
     * @param like
     *            是否为模糊查询 [1.是 0.否]
     * @param result
     *            是否为二次结果查询 [1.是 0.否]
     * @param type
     *            区别查询类型 [1.人员查询 2.单位查询 3.职位查询]
     * @param unit
     *            查询范围 [0.查单位 1.查部门 2.全部]
     * @return strwhere
     * @throws GeneralException
     */
    public String strWhere(String sexpr, String sfactor, String dbpre, String query_type, String history, String like, String result, String type, String unite) throws GeneralException {
        StringBuffer strwhere = new StringBuffer();
        boolean blike = false;
        if ("1".equals(like)) {
            blike = true;
        }
        boolean bresult = true;
        if ("1".equals(result)) {
            bresult = false;
        }
        boolean bhis = false;
        if ("1".equals(history)) {
            bhis = true;
        }
        String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
        try {
            sfactor = sfactor.replaceAll("\\$THISMONTH\\[\\]", "当月");
            FactorList factorslist = new FactorList(sexpr, sfactor, dbpre, bhis, blike, bresult, Integer.parseInt(type), user.getUserId());
            strwhere.append(factorslist.getSqlExpression());

            if ("1".equals(type)) {
                strwhere.append(getPrivSqlWhere(dbpre));
            }

            /** 在K01中加入B0110指标，查询时，将sql替换 */
            if ("3".equals(type)) {
                String str = strwhere.toString().toUpperCase().replaceAll("LEFT JOIN A01 ON K01.E01A1=A01.A0100", " ").replaceAll("A01", "K01");
                strwhere.setLength(0);
                strwhere.append(str);
            }
            /** 对单位还得加上管理范围 */
            if ("2".equals(type) || "3".equals(type)) {
                String backdate = DateUtils.format(new Date(), "yyyy-MM-dd");
                String priv = getUnitIdByBusi();
                String filtercond = getPrivSql(dbpre, priv);
                if (filtercond.length() != 0) {
                    strwhere.append(" and (" + filtercond + ")");
                }
                if ("2".equals(type)) {
                    if ("0".equals(unite)) {

                        strwhere.append(" and B01.B0110 in(select codeitemid from organization where codesetid='UN' and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date)");

                    } else if ("1".equals(unite)) {

                        strwhere.append(" and B01.B0110 in(select codeitemid from organization where codesetid='UM'");
                        strwhere.append(" and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ");
                        strwhere.append(")");
                    } else if ("2".equals(unite)) {
                        strwhere.append(" and B01.B0110 in(select codeitemid from organization where ");
                        strwhere.append(Sql_switcher.dateValue(bosdate) + " between start_date and end_date ");
                        strwhere.append(")");

                    }

                } else if ("3".equals(type)) {
                    strwhere.append(" and K01.E01A1 in (select codeitemid from organization where codesetid='@K' ");
                    strwhere.append(" and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ");
                    strwhere.append(")");

                }
            }
        } catch (GeneralException e) {
            throw GeneralExceptionHandler.Handle(e);
        }

        return strwhere.toString();
    }

    /**
     * 条件查询解析
     * 
     * @param search
     * @return
     */
    public String getSearchWhere(String search) {
        StringBuffer wherestr = new StringBuffer();
        if (search != null && search.trim().length() > 0) {
            search = SafeCode.decode(search);
            search = PubFunc.keyWord_reback(search);
            search = PubFunc.reBackWord(search);
            String searcharr[] = search.split("::");

            if (searcharr.length == 3) {
                wherestr.append(" and (");
                String sexpr = searcharr[0];
                String sfactor = searcharr[1];
                boolean blike = false;
                blike = searcharr[2] != null && "1".equals(searcharr[2]) ? true : false;
                String strSFACTOR = sfactor;
                sfactor = "";
                String strItem[] = strSFACTOR.split("`");
                String xpr = "";
                for (int i = 0; i < strItem.length; i++) {
                    String item = strItem[i] + " ";
                    String code = "";
                    if (item.indexOf("<>") != -1) {
                        code = "<>";
                    } else if (item.indexOf(">=") != -1) {
                        code = ">=";
                    } else if (item.indexOf("<=") != -1) {
                        code = "<=";
                    } else if (item.indexOf(">") != -1) {
                        code = ">";
                    } else if (item.indexOf("<") != -1) {
                        code = "<";
                    } else {
                        code = "=";
                    }

                    String emp[] = item.split(code);
                    if (emp[1] == null || emp[1].trim().length() == 0) {
                        emp[1] = "";
                    }
                    if (blike) {
                        if ("b0110".equalsIgnoreCase(emp[0]) || "e0122".equalsIgnoreCase(emp[0]) || "e01a1".equalsIgnoreCase(emp[0])) {
                            if (emp[1].trim().length() < 1) {
                                emp[1] = "HJSJ";
                            }
                            if ("<>".equalsIgnoreCase(code)) {
                                wherestr.append(xpr + Sql_switcher.isnull(emp[0], "'HJSJ'") + " not like '" + emp[1].trim() + "%'");
                            } else {
                                wherestr.append(xpr + Sql_switcher.isnull(emp[0], "'HJSJ'") + " like '" + emp[1].trim() + "%'");
                            }
                        } else if (!isNumeric(emp[0], emp[1])) {
                            if ("<>".equalsIgnoreCase(code)) {
                                wherestr.append(xpr + emp[0] + " not like '%" + emp[1].trim() + "%'");
                            } else {
                                wherestr.append(xpr + emp[0] + " like '" + emp[1].trim() + "%'");
                            }
                        } else {
                            emp[1] = emp[1].trim() == null || emp[1].trim().length() < 1 ? "0" : emp[1].trim();
                            wherestr.append(xpr + Sql_switcher.isnull(emp[0], "0") + code + emp[1]);
                        }
                    } else {
                        if ("b0110".equalsIgnoreCase(emp[0]) || "e0122".equalsIgnoreCase(emp[0]) || "e01a1".equalsIgnoreCase(emp[0]) || !isNumeric(emp[0], emp[1])) {
                            if (emp[1].trim().length() < 1) {
                                emp[1] = "HJSJ";
                            }
                            wherestr.append(xpr + Sql_switcher.isnull(emp[0], "'HJSJ'") + code + "'" + emp[1].trim() + "'");
                        } else {
                            emp[1] = emp[1].trim() == null || emp[1].trim().length() < 1 ? "0" : emp[1].trim();
                            wherestr.append(xpr + Sql_switcher.isnull(emp[0], "0") + code + emp[1].trim());
                        }
                    }
                    int temp = sexpr.indexOf((i + 1) + "") + String.valueOf(i + 1).length();// 下一个的位数
                    if (sexpr.substring(sexpr.indexOf((i + 1) + "")) != null && sexpr.substring(temp).length() > 0) {
                        xpr = sexpr.substring(temp, temp + 1);
                        if ("+".equals(xpr)) {
                            xpr = " OR ";
                        } else if ("*".equals(xpr)) {
                            xpr = " AND ";
                        }
                    } else {
                        xpr = "";
                    }
                }
                wherestr.append(")");
            }
        }
        // System.out.println(wherestr.toString());
        return wherestr.toString();
    }

    /**
     * 判断是否是数字
     * 
     * @param str
     * @return
     */
    public boolean isNumeric(String field, String str) {

        FieldItem item = DataDictionary.getFieldItem(field);
        if (item != null && !"N".equalsIgnoreCase(item.getItemtype())) {
            return false;
        }
        if (item != null && "N".equalsIgnoreCase(item.getItemtype())) {
            return true;
        }

        int begin = 0;
        boolean once = true;
        if (str == null || "".equals(str.trim())) {
            return false;
        }
        str = str.trim();
        if (str.startsWith("+") || str.startsWith("-")) {
            if (str.length() == 1) {
                return false;
            }
            begin = 1;
        }
        for (int i = begin; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                if (str.charAt(i) == '.' && once) {
                    once = false;
                } else {
                    return false;
                }
            }
        }
        if (str.length() == (begin + 1) && !once) {
            return false;
        }
        return true;
    }

    /**
     *管理范围sql
     * 
     * @param dbpre
     *            人员库
     *@param priv
     *            管理范围
     * @return
     */
    public String getPrivSql(String dbpre, String priv) {
        if (dbpre == null || "".equals(dbpre)) {
            dbpre = "";
        } else {
            dbpre = dbpre + "A01.";
        }

        StringBuffer sqlWhere = new StringBuffer();
        if (user != null && !user.isSuper_admin()) {
            if (priv != null && priv.length() > 2 && priv.indexOf("UN`") == -1) {
                String tmparr[] = priv.split("`");
                for (int i = 0; i < tmparr.length; i++) {
                    String tmp = tmparr[i];
                    if (tmp != null && tmp.length() > 2) {
                        if (i > 0) {
                            sqlWhere.append(" or ");
                        }

                        if ("UN".equalsIgnoreCase(tmp.substring(0, 2))) {
                            sqlWhere.append(dbpre + "B0110 like '" + tmp.substring(2, tmp.length()) + "%'");
                        } else if ("UM".equalsIgnoreCase(tmp.substring(0, 2))) {
                            sqlWhere.append(dbpre + "E0122 like '" + tmp.substring(2, tmp.length()) + "%'");
                        } else if ("@K".equalsIgnoreCase(tmp.substring(0, 2))) {
                            sqlWhere.append(dbpre + "E01A1 like '" + tmp.substring(2, tmp.length()) + "%'");
                        } else if (tmp.length() > 3) {
                            sqlWhere.append("(nbase='" + tmp.substring(0, 3) + "' and a0100='" + tmp.substring(3) + "')");
                        }
                    }
                }
            }
        }
        return sqlWhere.toString();
    }

    /**
     *管理范围查询人员sql
     * 
     * @param nbase
     *            人员库
     * @return
     */
    public String getPrivSqlWhere(String nbase) {
        String where = "";
        try {
            if (user != null && !user.isSuper_admin()) {
                String priv = getUnitIdByBusi();
                where = getPrivSql(nbase, priv);
                if (where.length() > 0) {
                    where = " AND a0100 in(select a0100 from " + nbase + "A01 where " + where + ")";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return where;
    }

    /**
     * 返回上级
     * 
     * @param b0110
     * @param i
     * @return
     */
    public String getSupUnit(String b0110, int i) {
        RowSet rs = null;
        boolean flag = true;
        String supunit = b0110;
        StringBuffer sbf = new StringBuffer();
        do {
            flag = false;
            String sql = "select codeitemid from organization where codesetid='UN' and codeitemid=(select parentid from organization where codesetid='UN' and codeitemid<>parentid and codeitemid='"
                    + supunit + "')";
            ContentDAO dao = new ContentDAO(conn);
            try {
                rs = dao.search(sql);
                if (rs.next()) {
                    supunit = rs.getString("codeitemid");
                    if (supunit != null && supunit.length() > 0) {
                        flag = true;
                        if (i == 1) {
                            sbf.append(" or r5020='" + supunit + "'");
                        } else {
                            sbf.append("r5020='" + supunit + "' or ");
                        }
                    }
                }
            } catch (SQLException e) {
               PubFunc.closeResource(rs);
                e.printStackTrace();
            }
        } while (flag);

        PubFunc.closeResource(rs);

        return sbf.toString();
    }

    /**
     * 获取课程列表
     * 
     * @param flag
     *            需要课程的状态
     * @param isParent
     *            是否显示上级
     * @return
     */
    public ArrayList getCourseList(String flag, boolean isParent) {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select r5000,r5003 from r50");
            sql.append(" where r5022 in ('" + flag.replaceAll(",", "','") + "')");
            if (user != null && !user.isSuper_admin()) {
                String priv = getUnitIdByBusi();
                if (priv != null && priv.length() > 2 && priv.indexOf("UN`") == -1) {
                    String tmparr[] = priv.split("`");
                    sql.append(" and (");
                    for (int i = 0; i < tmparr.length; i++) {
                        String tmp = tmparr[i];
                        if (tmp != null && tmp.length() > 2) {
                            if ("UN".equalsIgnoreCase(tmp.substring(0, 2))) {
                                if (isParent) {
                                    sql.append("r5020=" + Sql_switcher.substr("'" + tmp.substring(2, tmp.length()) + "'", "1", Sql_switcher.length("r5020")) + " or ");
                                }

                                sql.append("r5020 like '" + tmp.substring(2, tmp.length()) + "%' or ");
                            }
                        }
                    }
                    // if(!sql.toString().endsWith(" and ("))
                    // sql.setLength(sql.length()-3);
                    sql.append(Sql_switcher.isnull("r5020", "'-1'") + "='-1'");
                    if (Sql_switcher.searchDbServer() == 1) {
                        sql.append(" or r5020=''");
                    }
                    sql.append(" or r5014=1)");
                }
            }

            CommonData data = new CommonData("#", "全部");
            list.add(data);
            rs = dao.search(sql.toString());
            while (rs.next()) {
                data = new CommonData();
                data.setDataName(rs.getString("r5003"));
                data.setDataValue(String.valueOf(rs.getInt("r5000")));
                list.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return list;
    }

    /**
     * 课程推送
     * 
     * @param r5000
     *            推送的课程
     * @param nbase
     *            推送人员库
     * @param a0100
     * @param b0110
     * @param e0122
     * @param e01a1
     * @param a0101
     * @param lesson_from
     *            课程来源(0=岗位课程，1=自选课程，2=推送课程，3=培训班，4=职务课程，5=培训课程-选人推送)
     * @param r3101
     * @throws Exception
     */
    public void pushCourse(String r5000, String nbase, String a0100, String b0110, String e0122, String e01a1, String a0101, String lesson_from, String r3101) throws Exception {
        ContentDAO dao = new ContentDAO(conn);
        RowSet frowset = null;
        ResultSet frecset = null;
        RecordVo vo = null;
        int id = 0;
        try {
            frowset = dao.search("select id from tr_selected_lesson where R5000=" + r5000 + " and nbase='" + nbase + "' and a0100='" + a0100 + "'");
            if (frowset.next()) {
                id = frowset.getInt("id");
                Date start_date = null;
                Date end_date = null;
                frowset = dao.search("select R5030,R5031 from R50 where R5000=" + r5000);
                if (frowset.next()) {
                    start_date = frowset.getTimestamp("r5030");
                    end_date = frowset.getTimestamp("r5031");
                }
                String sql = "update tr_selected_lesson set";
                if ((!"".equals(r3101)) || r3101 != null) {
                    sql += " r3101='" + r3101 + "',";
                }
                sql += " start_date=" + Sql_switcher.dateValue(DateUtils.FormatDate(start_date, "yyyy-MM-dd HH:mm:ss")) + ",end_date="
                        + Sql_switcher.dateValue(DateUtils.FormatDate(end_date, "yyyy-MM-dd HH:mm:ss")) + " where id=" + id;
                dao.update(sql);
            } else {
                vo = new RecordVo("tr_selected_lesson");
                IDGenerator idg = new IDGenerator(2, conn);
                id = Integer.parseInt(idg.getId("tr_selected_lesson.id"));
                vo.setInt("id", id);
                ArrayList keys = new ArrayList();
                keys.add("id");
                vo.setKeylist(keys);
                vo.setInt("r5000", Integer.parseInt(r5000));
                vo.setString("nbase", nbase);
                vo.setString("a0100", a0100);
                vo.setString("b0110", b0110);
                vo.setString("e0122", e0122);
                vo.setString("e01a1", e01a1);
                vo.setString("a0101", a0101);
                vo.setString("r3101", r3101);
                vo.setInt("lprogress", 0);
                vo.setInt("learnedhour", 0);
                vo.setInt("pass_state", 0);
                vo.setInt("exam_result", 0);
                vo.setInt("lesson_from", Integer.parseInt(lesson_from));
                vo.setInt("state", 0);
                frowset = dao.search("select R5030,R5031 from R50 where R5000=" + r5000);
                if (frowset.next()) {
                    vo.setDate("start_date", frowset.getDate("r5030"));
                    vo.setDate("end_date", frowset.getDate("r5031"));
                }
                
                dao.addValueObject(vo);
            }
            /** 保存相应课件信息 */
            frowset = dao.search("select * from r51 where r5000=" + r5000);
            while (frowset.next()) {
                int r5100 = frowset.getInt("r5100");
                String strsql = " where r5100=" + r5100 + " and nbase='" + nbase + "' and a0100='" + a0100 + "'";
                frecset = dao.search("select 1 from tr_selected_course" + strsql);
                if (frecset.next()) {
                    dao.update("update tr_selected_course set id=" + id + strsql);
                } else {
                    vo = new RecordVo("tr_selected_course");
                    vo.setInt("r5100", r5100);
                    vo.setString("nbase", nbase);
                    vo.setString("a0100", a0100);
                    vo.setInt("id", id);
                    vo.setInt("lprogress", 0);
                    vo.setInt("learnedhour", 0);
                    vo.setInt("state", 0);
                    dao.addValueObject(vo);
                }

                // 获得课件类型
                String type = frowset.getString("r5105");
                if ("4".equals(type)) {
                    // 解析xml
                    String xml = frowset.getString("xmlcontent");
                    if (xml == null || xml.length() <= 0) {
                        throw GeneralExceptionHandler.Handle(new GeneralException("scorm课件xml不存在"));
                    }
                    ScormXMLBo bo = new ScormXMLBo(xml, "mo");
                    List list = bo.getAllScoAndParent();
                    StringBuffer delBuff = new StringBuffer();
                    delBuff.append("delete from tr_selected_course_scorm where ");
                    delBuff.append("a0100='");
                    delBuff.append(a0100);
                    delBuff.append("' and nbase='");
                    delBuff.append(nbase);
                    delBuff.append("' and r5100=");
                    delBuff.append(r5100);
                    delBuff.append(" and scoid not in (");

                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            String[] str = list.get(i).toString().split(";&;");
                            RecordVo scormVo = new RecordVo("tr_selected_course_scorm");
                            scormVo.setInt("r5100", r5100);
                            scormVo.setString("nbase", nbase);
                            scormVo.setString("a0100", a0100);
                            scormVo.setString("scoid", str[0]);
                            scormVo.setString("parent", str[1]);
                            if (dao.isExistRecordVo(scormVo)) {
                                dao.updateValueObject(scormVo);
                            } else {
                                scormVo.setString("parent", str[1]);
                                scormVo.setInt("lesson_status", 6);
                                dao.addValueObject(scormVo);
                            }

                            delBuff.append("'");
                            delBuff.append(str[0]);
                            delBuff.append("'");
                            if (i != list.size() - 1) {
                                delBuff.append(",");
                            }
                        }

                        // 删除多余的课程信息
                        delBuff.append(")");
                        dao.delete(delBuff.toString(), new ArrayList());
                    }
                }
            }

            // 删除多余推送课件
            String sql = "delete tr_selected_course where nbase='" + nbase + "' and a0100='" + a0100 + "' and id=" + id + " and R5100 not in(select R5100 from R51 where R5000=" + r5000 + ")";
            dao.delete(sql, new ArrayList());

            StringBuffer buff = new StringBuffer();
            buff.delete(0, buff.length());
            buff.append("update tr_selected_lesson set Learnedhour=(select sum(Learnedhour) from tr_selected_course where r5100 in(select r5100 from r51 where r5000=");
            buff.append(r5000);
            buff.append(") and a0100='");
            buff.append(a0100);
            buff.append("' and nbase='");
            buff.append(nbase);
            buff.append("'),state=1 where r5000=");
            buff.append(r5000);
            buff.append(" and a0100='");
            buff.append(a0100);
            buff.append("' and nbase='");
            buff.append(nbase);
            buff.append("'");

            dao.update(buff.toString());

            buff.delete(0, buff.length());

            // 查询课件个数

            buff.append("select count(*) a from r51 where r5000=");
            buff.append(r5000);

            int count = 1;
            frowset = dao.search(buff.toString());
            if (frowset.next()) {
                count = frowset.getInt("a");
                count = count == 0 ? 1 : count;
            }

            buff.delete(0, buff.length());
            buff.append("update tr_selected_lesson set lprogress=(select sum(lprogress)/");
            buff.append(count);
            buff.append(" from tr_selected_course where r5100 in (select r5100 from r51 where r5000=");

            buff.append(r5000);
            buff.append(") and a0100='");
            buff.append(a0100);
            buff.append("' and nbase='");
            buff.append(nbase);
            buff.append("') where r5000=");
            buff.append(r5000);
            buff.append(" and a0100='");
            buff.append(a0100);
            buff.append("' and nbase='");
            buff.append(nbase);
            buff.append("'");
            dao.update(buff.toString());
        } catch (SQLException e) {
        } finally {
            PubFunc.closeResource(frecset);
            PubFunc.closeResource(frowset);
        }
    }

    /**
	 * 发送课程体系通知到微信
	 * @param r5000 培训课程id
	 * @param lesson_from 课程来源(0=岗位课程，4=职务课程，5=培训课程-选人推送)
	 * @param picUrl 图片url
	 * @param url 点击图文消息进入页面地址
	 * @param idlist 发送到人员
	 * @return
	 * @throws Exception
	 */
	public boolean sendCourseToWX(String r5000,String lesson_from,String picUrl,HashMap url,ArrayList idlist) throws Exception{
		boolean flag = false;
		String sql = "select R5003,r5012,imageurl from R50 where R5000 = '"+r5000+"'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String topic = "";
		ContentDAO dao=new ContentDAO(this.conn);
			
		try {
			rs =dao.search(sql);
			String content = "";
			String img = "";
			while(rs.next()){
				topic = rs.getString("R5003");
				content = rs.getString("r5012");
				content = StringUtils.isEmpty(content) ? topic : content;
				
				img = rs.getString("imageurl")==null?"":rs.getString("imageurl");
			}
			if(!"".equals(img)){//【11151】培训管理：培训视频推送之后，手机收到微信提示，不显示课程图片  jingq upd 2015.07.16
				String texturl = (String) url.get(idlist.get(0));
				texturl = texturl.replace("http://", "");
				picUrl = "http://"+texturl.substring(0, texturl.indexOf("/"))+"/servlet/vfsservlet?fileid=" + img;
			}
			
			for(int i=0;i<idlist.size();i++){
				String u =(String) idlist.get(i);
				String urlstr = (String)url.get(u);
				if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("wx", "corpid"))) {
                    flag = WeiXinBo.sendMsgToPerson(u, topic, content, picUrl, urlstr);
                }
				//推送至钉钉 chenxg 2017-06-01
//                if(StringUtils.isNotEmpty(ConstantParamter.getAttribute("DINGTALK","corpid")))
//                    DTalkBo.sendMessage(u, topic, content, "", "");
				
			}
			
		} catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
		    PubFunc.closeResource(rs);
		    PubFunc.closeResource(ps);
		}
		return flag;
	}
    
    public ArrayList getTrainNbases(String state) {
        ArrayList dbprivlist = new ArrayList();

        // 培训人员库
        ConstantXml constantbo = new ConstantXml(this.conn, "TR_PARAM");
        String nbaseParam = constantbo.getTextValue("/param/post_traincourse/nbase");
        nbaseParam = nbaseParam == null ? "" : nbaseParam;

        if (nbaseParam.length() == 0 && "1".equals(state)) {
            return dbprivlist;
        }

        // 当前用户人员库权限
        ArrayList dblist = this.user.getPrivDbList();
        if (dblist == null || dblist.size() == 0) {
            return dbprivlist;
        }

        // 取两者的交集
        for (int i = dblist.size() - 1; i >= 0; i--) {
            String dbpre = ((String) dblist.get(i));
            if ("2".equals(state) || nbaseParam.indexOf(dbpre) != -1) {
                dbprivlist.add(dbpre);
            }
        }

        return dbprivlist;
    }

    public String getNbasesFromList(ArrayList dbprivlist) {
        String nbases = "";
        for (int i = 0; i < dbprivlist.size(); i++) {
            nbases = nbases + dbprivlist.get(i);
            if (i < dbprivlist.size() - 1) {
                nbases = nbases + ",";
            }
        }
        return nbases;
    }

    public String getWhereStr(String search) throws GeneralException {
        StringBuffer wherestr = new StringBuffer();
        if (search != null && search.trim().length() > 0) {
            search = SafeCode.decode(search);
            search = PubFunc.keyWord_reback(search);
            search = PubFunc.reBackWord(search);
            String searcharr[] = search.split("::");
            if (searcharr.length == 3) {
                try {
                    FactorList factorslist = new FactorList(searcharr[0], searcharr[1], "", false, "1".equals(searcharr[2]), false, 0, "su");
                    wherestr.append(" and " + factorslist.getSingleTableSqlExpression(this.table));
                } catch (Exception e) {
                    throw GeneralExceptionHandler.Handle(e);
                }

            }
        }
        // System.out.println(wherestr.toString());
        return wherestr.toString();
    }

    public static RowSet getHotLesson(Connection conn) {
        RowSet rs = null;

        ConstantXml constantbo = new ConstantXml(conn, "TR_PARAM");
        String top = constantbo.getNodeAttributeValue("/param/hot_course", "top");
        if (top == null || "".equals(top) || !IsInteger(top) || top.trim().length() > 3) {
            top = "10";
        }
        // 陈旭光修改：添加查询条件 为自选 、公开、可以选修并且为发布状态的课程
        StringBuffer sql = new StringBuffer();
        switch (Sql_switcher.searchDbServer()) {
        case Constant.MSSQL:
            sql.append("select ");
            sql.append("top " + top.trim() + " count(*) as counts ,r5000");
            sql.append(" from tr_selected_lesson");
            sql.append(" where exists ( select r5000 from r50 where r50.r5000=tr_selected_lesson.r5000 and r5014 = '1' and r5016 = '1' and r5022 = '04') and lesson_from = 1");
            sql.append(" group by r5000 order by counts desc");
            break;
        case Constant.ORACEL:
            sql.append("select  * from (select  count(*),r5000 from tr_selected_lesson");
            sql.append(" where exists ( select r5000 from r50 where r50.r5000=tr_selected_lesson.r5000 and r5014 = '1' and r5016 = '1' and r5022 = '04') and lesson_from = 1 ");
            sql.append(" group by r5000 order by count(*) desc)  c where rownum<=" + top);
            break;
        }

        ContentDAO dao = new ContentDAO(conn);
        try {
            rs = dao.search(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 验证正整数
     * 
     * @param s
     * @return
     */
    public static boolean IsInteger(String s) {
        int num = 0;
        try {
            num = Integer.parseInt(s);
            if (num > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 将关联表类型的指标转换为可见的文字
     * 
     * @param codesetid
     * @param codeItemid
     * @return
     * @throws GeneralException
     */
    public String codeFlagToName(String codesetid, String codeItemid) throws GeneralException {
        String codetable = "";
        String codevalue = "";
        String codedesc = "";
        String codeitemDesc = "";
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(conn);
        String codeid = codesetid.replaceAll("1_", "");
        try {
            String sql = "select codetable,codevalue,codedesc from t_hr_relatingcode where codesetid='" + codeid + "'";
            if (!"r25".equalsIgnoreCase(codesetid)) {
                rs = dao.search(sql);
                if (rs.next()) {
                    codetable = rs.getString("codetable");
                    codevalue = rs.getString("codevalue");
                    codedesc = rs.getString("codedesc");
                }
            } else {
                codetable = "r25";
                codevalue = "r2501";
                codedesc = "r2502";
            }
            sql = "select " + codedesc + " from " + codetable + " where " + codevalue + "='" + codeItemid + "'";

            rs = dao.search(sql);
            if (rs.next()) {
                codeitemDesc = rs.getString(codedesc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return codeitemDesc;
    }

    /**
     * 培训课程范围控制
     * 
     * @param user
     * @return
     */
    public static String getLessonByBusiWhere(UserView user) {
        String sql = "";
        try {
            TrainCourseBo bo = new TrainCourseBo(user);
            String b0110 = bo.getUnitIdByBusi();
            if (b0110 != null && b0110.length() > 2 && b0110.indexOf("UN`") == -1) {
                sql = " and (";
                String[] units = b0110.split("`");
                if (units.length > 0 && b0110.length() > 0) {
                    for (int i = 0; i < units.length; i++) {
                        String b0110s = units[i].substring(2);
                        sql += "r5020=" + Sql_switcher.substr("'" + b0110s + "'", "1", Sql_switcher.length("r5020"));
                        sql += " or r5020 like '";
                        sql += b0110s;
                        sql += "%'";
                        sql += " or ";
                    }
                }
                sql += Sql_switcher.isnull("r5020", "'-1'");
                sql += "='-1'";
                if (Sql_switcher.searchDbServer() == 1) {
                    sql += " or r5020=''";
                }
                sql += " or r5014=1)";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }

    /**
     * 获取培训课程分类新的节点的当前级别的编号
     * 
     * @param codeitemid
     *            当前节点已存在的最大节点
     * @param pitemid
     *            父节点
     * @return 节点编号
     */
    public String getcodeitemid(String codeitemid, String pitemid) {
        String newid = "";
        boolean flag = false;
        int maxid = 0;
        String maxstring = "";
        int len = codeitemid.trim().length() - pitemid.trim().length();
        len = len == 0 ? 2 : len;

        String maxitemid = codeitemid.trim().substring(codeitemid.trim().length() - len, codeitemid.trim().length());
        // 获取当前级别最大的编号
        for (int i = 0; i < len; i++) {
            maxstring += "z";
        }
        // 将最大编号转换为10进制
        maxid = Integer.parseInt(maxstring, 36);
        // 当已存在的最大编号不是当前级别可达到的最大编号时，在已存在的最大编号基础上加1
        maxitemid = maxitemid.toLowerCase();
        if (!maxitemid.equalsIgnoreCase(maxstring)) {
            int id = Integer.parseInt(maxitemid, 36);
            newid = Integer.toString(id + 1, 36).toUpperCase();

            int m = len - newid.length();
            if (m > 0) {
                for (int n = 0; n < m; n++) {
                    newid = "0" + newid;
                }
            }
        } else {
            // 当已存在的最大编号是当前级别可达到的最大编号时，寻找可插入的编号插入
            for (int i = 1; i <= maxid; i++) {
                flag = false;
                // 将编号转换为36进制
                String itemid = Integer.toString(i, 36).toUpperCase();
                int m = len - itemid.length();
                if (m > 0) {
                    for (int n = 0; n < m; n++) {
                        itemid = "0" + itemid;
                    }
                }

                flag = checkIdExist(pitemid, itemid);

                if (!flag) {
                    newid = itemid;
                    break;
                }

            }
        }
        return newid;
    }

    /**
     * 检测节点编号是否已存在
     * 
     * @param pitemid
     *            当前级别父节点编号
     * @param itemid
     *            当前级别新节点编号
     * @return true 存在| false 不存在
     */
    private boolean checkIdExist(String pitemid, String itemid) {
        boolean flag = false;
        RowSet rs = null;
        try {
            StringBuffer sqlstr = new StringBuffer();
            sqlstr.append("select 1 from ");
            sqlstr.append(" codeitem where codesetid='55'");
            sqlstr.append(" and ");
            if (pitemid.trim().length() < 1) {
                sqlstr.append("parentid=codeitemid");
            } else {
                sqlstr.append("parentid='");
                sqlstr.append(pitemid + pitemid);
                sqlstr.append("' and parentid<>codeitemid");
            }
            sqlstr.append(" and codeitemid='" + itemid + "'");
            ContentDAO dao = new ContentDAO(this.conn);

            rs = dao.search(sqlstr.toString());
            if (rs.next()) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return flag;
    }
    /**
     * 获取完整用课程简介
     * @param r5000
     * @return
     */
    public static String getr5012(String r5000) {
        String value = "";
        RowSet rs = null;
        Connection con = null;
        try{
            r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
            con = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(con);
            rs = dao.search("select r5012 from r50 where r5000="+r5000);
            if(rs.next()) {
                value = rs.getString("r5012");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeResource(rs);
            PubFunc.closeResource(con);
        }
        return value;
    }
    /**
     * 职务/岗位推送课程发送邮件
     * @param lessonlist 课程编号的list
     * @param dbname 人员库
     * @param pushmap 每门课程及其要推送的人员的map
     * @param basePath 网址（默认包含服务器ip和端口）
     * @return personNames 邮件地址异常的人员姓名
     */
	public ArrayList pushLesson(ArrayList personlist, String dbname, HashMap pushmap, String basePath) {
		ArrayList emaillist = new ArrayList();
		try {
			for (int i = 0; i < personlist.size(); i++) {
				String person = (String) personlist.get(i);
				String r5000s = (String) pushmap.get(person);

				LazyDynaBean emailbean = sendEMail(r5000s, dbname, person, basePath);
				if (emailbean != null) {
                    emaillist.add(emailbean);
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return emaillist;
	}
    /**
     * 推送课程发送邮件
     * @param r5000s 课程id
     * @param dbname 人员库
     * @param a0100  人员编号
     * @param basePath 网址（默认包含服务器ip和端口）
     * @return 要发送的邮件生成的对象
     * @throws Exception
     */
    public LazyDynaBean sendEMail(String r5000s, String dbname, String a0100, String basePath) throws Exception {
        LazyDynaBean emailbean = new LazyDynaBean();
        ResultSet rs = null;
        try {
            if(basePath.endsWith("/")) {
                basePath = basePath.substring(0, basePath.length()-1);
            }
            String email = ConstantParamter.getEmailField().toLowerCase();
            if(StringUtils.isEmpty(email)) {
                return null;
            }
                    
            String loguser = ConstantParamter.getLoginUserNameField().toLowerCase();
            String logpassword = ConstantParamter.getLoginPasswordField();
            RecordVo vo = null;
            ContentDAO dao = new ContentDAO(conn);

            StringBuffer buf = new StringBuffer();// 邮件内容
            StringBuffer title = new StringBuffer();// 邮件标题

            EmailTemplateBo bo = new EmailTemplateBo(conn);

            vo = new RecordVo(dbname + "A01");
            vo.setString("a0100", a0100);
            if (dao.isExistRecordVo(vo)) {
                if (vo != null) {
                    buf.setLength(0);
                    title.setLength(0);
                    String email_address = vo.getString(email);
                    String a0101 = vo.getString("a0101");
                    String username = vo.getString(loguser);
                    String password = vo.getString(logpassword.toLowerCase());
                    String etoken = PubFunc.convertTo64Base(username + "," + password);

                    title.append("培训课程通知");
                    buf.append(a0101 + ":<br><br>&nbsp;&nbsp;&nbsp;&nbsp;您好，系统为您推送了几门在线课程。<br><br>");
                    buf.append("&nbsp;&nbsp;&nbsp;&nbsp;课程列表（点击课程名称可开始学习）：<br><br>");
                    ArrayList r5000list = TrainExamPlanBo.getList(r5000s);
                    for (int i = 0; i < r5000list.size(); i++) {
                        String lessonid = (String) r5000list.get(i);
                        String sql = "select r5000,r5003,r5004 from r50 where r5000 in (" + lessonid + ")";
                        rs = dao.search(sql);
                        while (rs.next()) {
                            String r5000 = rs.getString("r5000");
                            String r5003 = rs.getString("r5003");
                            String r5004 = rs.getString("r5004");
                            buf.append("<a href=\"" + basePath + "/train/resource/mylessons/learncoursebyextjs.jsp?opt=me&etoken=" + etoken + "&classes=" + SafeCode.encode(PubFunc.encrypt(r5004))
                                    + "&lesson=" + SafeCode.encode(PubFunc.encrypt(r5000)) + "&lessonState=ing\">《" + r5003 + "》</a>、");
                        }
                    }

                    String content = "";
                    if (buf.toString().endsWith("、")) {
                        content = (String) buf.subSequence(0, buf.length() - 1) + "。";
                    }

                    emailbean.set("toAddr", email_address);
                    emailbean.set("subject", title.toString());
                    emailbean.set("bodyText", content);
                    emailbean.set("href", "");
                    emailbean.set("hrefDesc", "");
                    
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        
        return emailbean;
    }
    
    /**
     * 获取培训课程的上传图片的保存路径
     * @return 返回系统路径
     * @throws GeneralException
     */
    public String getAttacmentRootDir() throws GeneralException {

        return getAttacmentRootDir("0");
    }
    /**
     * 获取系统参数中设置的多媒体路径
     * @param type =0：培训课程上传图片的路径；=1培训课件上传的路径
     * @return 系统参数中设置的多媒体路径
     * @throws GeneralException
     */
    public String getAttacmentRootDir(String type) throws GeneralException {

        ConstantXml constantXml = new ConstantXml(this.conn, "FILEPATH_PARAM");
        String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");

        if (rootDir == null || "".equals(rootDir)) {
            throw new GeneralException("没有配置多媒体存储路径！");
        }

        rootDir = rootDir.replace("\\", File.separator);
        if (!rootDir.endsWith(File.separator)) {
            rootDir = rootDir + File.separator;
        }

        if("0".equalsIgnoreCase(type)) {
            rootDir = rootDir + "doc" + File.separator;
        } else if("1".equalsIgnoreCase(type)) {
            rootDir = rootDir + "videostreams" + File.separator;
        }
        
        return rootDir;
    }
    /**
     * sql语句中将数值类型数据转为字符串 注：此方法主要用于试卷，转换分数类型故小数位数是固定的。
     * @param itemid 数值类型数据
     * @return
     */
    public String floatTochar(String itemid){
        StringBuffer strvalue = new StringBuffer();
        switch (Sql_switcher.searchDbServer())
        {
            case 1:
                strvalue.append("CAST(");
                strvalue.append(itemid);
                strvalue.append(" AS NUMERIC(8,1))");
                break;
            case 2:
                strvalue.append("TRIM(TO_CHAR(");
                strvalue.append(itemid);
                strvalue.append("))");
                break;
            case 3:
                strvalue.append("CHAR(INT(");
                strvalue.append(itemid);
                strvalue.append("))");
                break;
        }
        return strvalue.toString();
    }
    /**
     * 生成GUID
     * @return
     */
    public static String createGuid() {
        UUID uuid = UUID.randomUUID();
        String guid = uuid.toString().toUpperCase(); 
        return guid;
    }
}
