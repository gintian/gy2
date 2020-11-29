package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.CycleKqClass;
import com.hjsj.hrms.businessobject.kq.team.KqShiftGroup;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 显示排班选择对象
 *<p>
 * Title:
 * </p>
 *<p>
 * Description:
 * </p>
 *<p>
 * Company:HJHJ
 * </p>
 *<p>
 * Create time:May 9, 2007:5:15:08 PM
 * </p>
 * 
 * @author sunxin
 *@version 4.0
 */
public class SearchShiftObjectTrans extends IBusiness {
    
    public void execute() throws GeneralException {
        try {
            String object_flag = (String) this.getFormHM().get("object_flag");
            String a_code = (String) this.getFormHM().get("a_code");
            String str = a_code.substring(0, 2);
            //2014.10.23 xxd截取字符串前两位，判断是否为单位、部门、岗位、班组、人员，都不满足则直接解密该字符串
            if(!"UN".equals(str) && !"UM".equals(str) && !"@K".equals(str) && !"GP".equals(str) && !"EP".equals(str)){
            	a_code = PubFunc.decrypt(a_code);
            }
            String nbase = (String) this.getFormHM().get("nbase");
            //判断，人员库前缀长度为3，大于3则进行解密。
            if(null != nbase && nbase.length()>3){
            	nbase = PubFunc.decrypt(nbase);
            }
            //nbase = PubFunc.decrypt(nbase);
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String exist_emp = (String) hm.get("exist_emp");
            hm.put("exist_emp", "");
            String select_pre = (String) this.getFormHM().get("select_pre");
            String fields[] = null;
            if (exist_emp != null && exist_emp.length() > 0) {
                fields = exist_emp.split("\\^");
            }
            
            if (object_flag == null || object_flag.length() <= 0)
                return;
            
            String sql_str = "";
            String column = "";
            String where_str = "";
            KqShiftGroup kqShiftGroup = new KqShiftGroup(this.getFrameconn(), this.userView);
            String org_id = kqShiftGroup.getOrgId(a_code, nbase, this.userView, this.getFormHM());
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());

            if ("0".equals(object_flag))// 人员
            {
                String code = "";
                if (a_code != null && a_code.indexOf("UN") == -1) {
                    if (a_code.length() > 2) {
                        code = a_code.substring(2);
                    }
                    
                    if (a_code.indexOf("GP") == -1) {
                        ArrayList kq_dbase_list = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(), a_code);
                        ArrayList sql_db_list = new ArrayList();
                        if (a_code.indexOf("EP") != -1 && nbase != null && nbase.length() > 0) {
                        	sql_db_list.add(nbase);
                        } else if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) 
						{
                        	sql_db_list.add(select_pre);
						}
                        else {
                            sql_db_list = kq_dbase_list;
                        }
                        
                        String kind = "";
                        if (a_code.indexOf("UN") != -1) {
                            kind = "2";
                        } else if (a_code.indexOf("UM") != -1) {
                            kind = "1";
                        } else if (a_code.indexOf("@K") != -1) {
                            kind = "0";
                        } else if (a_code.indexOf("EP") != -1) {
                            kind = "a01";
                            ArrayList list = new ArrayList();
                            list.add(nbase);
                            kq_dbase_list = list;
                        } else {
                            kind = "0";
                        }
                        
                        CycleKqClass cycleKqClass = new CycleKqClass(this.getFrameconn(), this.userView);
                        ArrayList emplist = cycleKqClass.getEmpList(fields);
                        sql_str = kqShiftGroup.getQueryString(sql_db_list, this.userView, code, kind, emplist);
                        this.getFormHM().put("kq_list", kqUtilsClass.getKqNbaseList(kq_dbase_list));
                    } else {
                        String groupWhr = getGroupWhr(kqShiftGroup, code, org_id);
                        
                        ArrayList dlist = getDbase(this.getFrameconn(), groupWhr);
                        if (dlist == null || dlist.size() == 0)
                            throw new GeneralException("选择的班组没有添加人员！");
                        
                        ArrayList sql_db_list = new ArrayList();
                        if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
                            CommonData da = new CommonData(select_pre, select_pre);
                            sql_db_list.add(da);
                        } else {
                            sql_db_list = dlist;
                        }
                        
                        StringBuffer strsql = new StringBuffer();
                        for (int i = 0; i < sql_db_list.size(); i++) {
                            CommonData vo = (CommonData) sql_db_list.get(i);
                            String dbper = vo.getDataValue();
                            String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbper);
                            
                            strsql.append("select nbase,b0110,e0122,e01a1,a0100,a0101 from kq_group_emp");
                            strsql.append(" where nbase='" + dbper + "'");
                            strsql.append(" AND " + groupWhr);
                            
                            if (!this.userView.isSuper_admin()) {
                                String privCode = RegisterInitInfoData.getKqPrivCode(userView);
                                String privCodeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
                                if (!"".equals(privCodeValue)) {
                                    if (privCode != null && "UN".equals(privCode))
                                        strsql.append(" and b0110 like '" + privCodeValue + "%'");
                                    else if (privCode != null && "UM".equals(privCode))
                                        strsql.append(" and e0122 like '" + privCodeValue + "%'");
                                    else if (privCode != null && "@K".equals(privCode))
                                        strsql.append(" and e01a1 like '" + privCodeValue + "%'");
                                }
                                strsql.append(" and   a0100 in(select distinct a0100 " + whereA0100In + ") ");
                            }
                            strsql.append(" UNION ");
                        }
                        strsql.setLength(strsql.length() - 7);
                        sql_str = strsql.toString();
                        this.getFormHM().put("kq_list", dlist);
                    }
                } else {
                    if (a_code.length() > 2) {
                        code = a_code.substring(2);
                    }
                    ArrayList kq_dbase_list = RegisterInitInfoData.getDase3(this.getFormHM(), this.userView, this.getFrameconn());
                    ArrayList sql_db_list = new ArrayList();
                    if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
                        sql_db_list.add(select_pre);
                    } else {
                        sql_db_list = kq_dbase_list;
                    }
                    sql_str = kqShiftGroup.getQueryString(sql_db_list, this.userView, code, "2", null);
                    this.getFormHM().put("kq_list", kqUtilsClass.getKqNbaseList(kq_dbase_list));
                }

                column = "nbase,b0110,e0122,e01a1,a0100,a0101";
                where_str = "";
            } else if ("1".equals(object_flag))// 班组
            {
                
                HashMap hash = kqShiftGroup.getKqGroupSQL("UN" + org_id);
                sql_str = hash.get("sqlstr").toString();
                column = hash.get("column").toString();
                where_str = hash.get("where").toString();
            }
            this.getFormHM().put("object_flag", object_flag);
            this.getFormHM().put("sql_str", sql_str);
            this.getFormHM().put("column", column);
            this.getFormHM().put("where_str", where_str);

            KqParameter kqpr = new KqParameter();
            this.getFormHM().put("isPost", kqpr.getKq_orgView_post());
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private ArrayList getDbase(Connection conn, String groupWhr) throws GeneralException {

        StringBuffer stb = new StringBuffer();
        ContentDAO dao = new ContentDAO(conn);
        HashMap hash = new HashMap();
        ArrayList dbaselist = RegisterInitInfoData.getDase3(hash, this.userView, conn);
        ArrayList slist = new ArrayList();
        // String[] base=dlist.split(",");
        RowSet rs = null;
        try {
            stb.append("select * from dbname");
            stb.append(" where pre in (select nbase from kq_group_emp");
            stb.append(" where ");
            stb.append(groupWhr);
            stb.append(")");
            rs = dao.search(stb.toString());
            while (rs.next()) {
                String dbpre = rs.getString("pre");
                for (int i = 0; i < dbaselist.size(); i++) {
                    String userbase = dbaselist.get(i).toString();
                    if (dbpre != null && dbpre.equalsIgnoreCase(userbase)) {
                        CommonData vo = new CommonData(rs.getString("pre"), rs.getString("dbname"));
                        slist.add(vo);
                    }
                }
            }
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return slist;

    }
    
    /**
     * @Title: getGroupWhr   
     * @Description: 取得班组条件，形如：group_id in (....)   
     * @param @param kqShiftGroup
     * @param @param groupId 班组编号（已选的班组编号或空）
     * @param @param orgId 当前定位的单位编号（没定位的传入的是管理范围单位）
     * @param @return 
     * @return String    
     * @throws
     */
    private String getGroupWhr(KqShiftGroup kqShiftGroup, String groupId, String orgId) {
        String whr = "";
        
        if (groupId != null && !"".equals(groupId)) {
            whr = "group_id='" + groupId + "'";
        } else {
            HashMap hash = kqShiftGroup.getKqGroupSQL("UN" + orgId);
            whr = hash.get("where").toString();
            whr = "group_id IN (SELECT group_id " + whr + ")";
        }
        
        return whr;
    }
}
