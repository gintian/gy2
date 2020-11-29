package com.hjsj.hrms.module.employeemanager.dao.impl;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.employeemanager.dao.EmployeemanagerDao;
import com.hjsj.hrms.transaction.mobileapp.utils.HTMLParamUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @Title 类名
 * @Description 类说明
 * @Company hjsj
 * @Author 编写人
 * @Date
 * @Version 1.0.0
 */

public class EmployeemanagerDaoImpl implements EmployeemanagerDao {
    private Connection conn;
    private UserView userView;

    public EmployeemanagerDaoImpl(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    /**
       * 统计我的档案下属成员总人数
       * @author houby
       * @param nbase 人员库
       * @param a0100 人员编号
       * @return int
       * @throws GeneralException
       */
    @Override
    public int getTotalCount(String nbase, String a0100) throws GeneralException {
        int totalCount=0;
        ContentDAO dao;
        RowSet rs = null;
        String objectId="";
        String nbaseCond="";
        String KCond="";
        String UMCond="";
        String UNCond="";
        StringBuffer sql = new StringBuffer();
        sql.append("select object_id from t_wf_mainbody  WHERE   relation_id =  (");
        sql.append("select relation_id from t_wf_relation where  default_line='1' and actor_type = '1') ");
        sql.append("and sp_grade='9' and mainbody_id='"+nbase+a0100+"'");
        try{
            dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            String whereSql = "where 1<>1 ";
            while(rs.next()){
                objectId=rs.getString("object_id");
                if("".equals(objectId)) {
                    continue;
                }
                if("@K".equalsIgnoreCase(objectId.substring(0, 2))){
                    KCond+="'"+objectId.substring(2)+"',";
                }else if("UM".equalsIgnoreCase(objectId.substring(0, 2))){
                    UMCond+="'"+objectId.substring(2)+"',";
                }else if("UN".equalsIgnoreCase(objectId.substring(0, 2))){
                    UNCond+="'"+objectId.substring(2)+"',";
                }else if(nbase.equalsIgnoreCase(objectId.substring(0, 3))){
                    nbaseCond+="'"+objectId.substring(3)+"',";
                }
            }
            //通过objectid生成岗位，部门，机构，人员的过滤范围
            if("".equals(KCond)&&"".equals(UMCond)&&"".equals(UNCond)&&"".equals(nbaseCond)) {
                return 0;
            }

            if(!"".equals(KCond)){
                whereSql+=" or E01A1 in ("+KCond.substring(0,KCond.length()-1)+") ";
            }
            if(!"".equals(UMCond)){
                whereSql+=" or E0122 in ("+KCond.substring(0,KCond.length()-1)+") ";
            }
            if(!"".equals(UNCond)){
                whereSql+=" or B0110 in ("+KCond.substring(0,KCond.length()-1)+") ";
            }
            if(!"".equals(nbaseCond)){
                whereSql+=" or A0100 in ("+nbaseCond.substring(0,nbaseCond.length()-1)+") ";
            }
            sql.setLength(0);
            sql.append("SELECT  count(1) count FROM "+nbase + "A01  " +whereSql);
            rs = dao.search(sql.toString());
            if(rs.next()){
                totalCount=rs.getInt("count");
            }
        }catch(Exception e ){
            e.printStackTrace();
        }finally {
            if(rs!=null){
                try {
                    rs.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        return totalCount;
    }

    /**
       * 获取员工信息列表
       * @author houby
       * @param loadType 加载类型
       * @param unitid 部门编号 UNxxx
       * @param cond 人员范围高级条件
       * @param page 页数
       * @param limit 每页几条
       * @param loadType 加载类型
       * @return Map
       * @throws GeneralException
       */
    @Override
    public Map getEmpMap(String loadType, String unitid, String cond, String page, String limit, String queryParams) throws GeneralException {
        List empList = new ArrayList();
        Map empMap = new HashMap();
        RowSet rs = null;
        String a0100 = "";
        String nbase = "";
        if(queryParams!=null && "2".equalsIgnoreCase(loadType)){
            HashMap queryMap=(HashMap) JSON.parse(queryParams);
            if(queryMap.size()>0){
                a0100 = (String) queryMap.get("a0100");
                nbase = (String) queryMap.get("nbase");
            }
        }
        String sql="";
        //网络地址
        String url="/w_selfservice";
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            //根据加载类型判断
            if("1".equals(loadType)){
                String orgType="UN";
                CodeItem orgItem = AdminCode.getCode("UN", unitid);
                if(orgItem==null){
                    orgItem= AdminCode.getCode("UM", unitid);
                    orgType="UM";
                }
                cond=cond==null?"":cond;
                String codeSet="";
                if(!"ALL".equals(unitid)){
                    codeSet=orgType+"`"+unitid;
                }
                String empSql = this.getSQL(codeSet, cond, String.valueOf(page), String.valueOf(limit),queryParams);
                List list=this.getPersonList(empSql, url);
                for(Object o:list){
                    HashMap map=(HashMap)o;
                    String org=map.get("org").toString();
                    String photo=map.get("photo").toString();
                    if("/w_selfservice/images/photo.jpg".equals(photo)){
                        map.remove("photo");
                        map.put("photo", "/w_selfservice/images/nophoto.png");
                    }
                    String[] array=org.split("/");
                    map.put("b0110_name", array[0]);
                    if(array.length>=2) {
                        map.put("e0122_name", array[1].trim());
                    } else {
                        map.put("e0122_name", "");
                    }
                    if(array.length>=3) {
                        map.put("e01a1_name", array[2].trim());
                    } else {
                        map.put("e01a1_name", "");
                    }
                    empList.add(map);
                }
                empMap.put("totalCount",getTotalCount(loadType,unitid,cond,queryParams));
            }else{
                a0100=a0100==""?this.userView.getA0100():a0100;
                nbase=nbase==null?this.userView.getDbname():nbase;
                String objectId="";
                String nbaseCond="";
                String KCond="";
                String UMCond="";
                String UNCond="";
                HashMap map=new HashMap();
                //获取主审批关系 当前审批人的所有object_id
                sql = "select object_id from t_wf_mainbody  WHERE   relation_id =  (select relation_id from t_wf_relation where  default_line='1' and actor_type = '1') and sp_grade='9' and mainbody_id='"+nbase+a0100+"'";
                rs = dao.search(sql.toString());
                String whereSql = "where 1<>1 ";
                while(rs.next()){
                    objectId=rs.getString("object_id");
                    if("".equals(objectId)) {
                        continue;
                    }
                    if("@K".equalsIgnoreCase(objectId.substring(0, 2))){
                        KCond+="'"+objectId.substring(2)+"',";
                    }else if("UM".equalsIgnoreCase(objectId.substring(0, 2))){
                        UMCond+="'"+objectId.substring(2)+"',";
                    }else if("UN".equalsIgnoreCase(objectId.substring(0, 2))){
                        UNCond+="'"+objectId.substring(2)+"',";
                    }else if(nbase.equalsIgnoreCase(objectId.substring(0, 3))){
                        nbaseCond+="'"+objectId.substring(3)+"',";
                    }
                }
                //通过objectid生成岗位，部门，机构，人员的过滤范围
                if("".equals(KCond)&&"".equals(UMCond)&&"".equals(UNCond)&&"".equals(nbaseCond)) {
                    return empMap;
                }

                if(!"".equals(KCond)){
                    whereSql+=" or E01A1 in ("+KCond.substring(0,KCond.length()-1)+") ";
                }
                if(!"".equals(UMCond)){
                    whereSql+=" or E0122 in ("+KCond.substring(0,KCond.length()-1)+") ";
                }
                if(!"".equals(UNCond)){
                    whereSql+=" or B0110 in ("+KCond.substring(0,KCond.length()-1)+") ";
                }
                if(!"".equals(nbaseCond)){
                    whereSql+=" or A0100 in ("+nbaseCond.substring(0,nbaseCond.length()-1)+") ";
                }

                sql="SELECT  A0100,'"+nbase+"' dbpre,A0101 ,B0110,E0122,E01A1,a0000  FROM "+nbase+"A01  "+whereSql;
                rs = dao.search(sql, Integer.parseInt(limit), Integer.parseInt(page));
                while(rs.next()){
                    map = new HashMap();
                    String a0100_new = rs.getString("a0100");
                    String dbpre = rs.getString("dbpre");
                    String b0110 = rs.getString("b0110");
                    String e0122 = rs.getString("e0122");
                    String e01a1 = rs.getString("e01a1");
                    Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
                    String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
                    display_e0122 = display_e0122 == null || display_e0122.length() == 0 ? "0" : display_e0122;
                    map.put("dbpre", dbpre);
                    map.put("a0100", a0100_new);
                    map.put("b0110", rs.getString("b0110"));
                    map.put("name", rs.getString("a0101"));
                    b0110 = AdminCode.getCodeName("UN", b0110);
                    b0110 = b0110 == null ? "" : b0110;
                    CodeItem itemid = AdminCode.getCode("UM", e0122, Integer.parseInt(display_e0122));
                    if (itemid != null) {
                        e0122 = itemid.getCodename();
                    }
                    e0122 = e0122 == null ? "" : e0122;
                    e01a1 = AdminCode.getCodeName("@K", e01a1);
                    e01a1 = e01a1 == null ? "" : e01a1;
                    map.put("b0110_name", b0110);
                    map.put("e0122_name", e0122);
                    map.put("e01a1_name", e01a1);
                    StringBuffer photourl = new StringBuffer();
                    PhotoImgBo pib = new PhotoImgBo(this.conn);
                    pib.setIdPhoto(true);
                    String filename = pib.getPhotoPath(dbpre,a0100_new);
                    if (!"".equals(filename)) {
                        photourl.append(url);
                        photourl.append(filename);
                    } else {
                        photourl.append(url);
                        photourl.append("/images/nophoto.png");
                    }
                    map.put("photo", photourl.toString());
                    empList.add(map);
                }
                sql="SELECT  count(1) count FROM "+nbase+"A01  "+whereSql;
                rs = dao.search(sql);
                if(rs.next()){
                    empMap.put("totalCount",rs.getInt("count"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try{
                if(rs!=null){
                    rs.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            empMap.put("empList",empList);
        }
        return empMap;
    }

    /**
       * 获取快速查询指标
       * @param
       * @return List
       * @throws Exception
       */
    @Override
    public List getfieldList() throws Exception {
        List fieldList = new ArrayList();
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try{
            String fieldStr = "";
            String sql = "select Str_Value from constant where Constant = 'SS_QUERYTEMPLATE'";
            rs = dao.search(sql);
            if(rs.next()){
                fieldStr = rs.getString("Str_Value");
            }
            if(fieldStr.trim().length()>0){
                String[] targetList = fieldStr.split(",");
                for(String field:targetList){
                    Map fieldMap = new HashMap();
                    if("B0110".equalsIgnoreCase(field) ||
                            "E01A1".equalsIgnoreCase(field) ||
                            "H0100".equalsIgnoreCase(field)){
                        FieldItem fieldItem = DataDictionary.getFieldItem(field);
                        fieldMap.put("itemid",field);
                        fieldMap.put("name",fieldItem.getItemdesc());
                        fieldMap.put("itemType",fieldItem.getItemtype());
                        fieldMap.put("itemLength",fieldItem.getItemlength());
                        fieldMap.put("codesetid",fieldItem.getCodesetid());
                        fieldMap.put("nodeLevel",false);

                    }else{
                        rs = null;
                        sql ="select itemid,itemdesc,itemtype,itemlength,field.codesetid,max(code.layer) layer from " +
                                "fielditem field left join codeitem code on field.codesetid = code.codesetid " +
                                "where field.itemid='"+field+"' group by " +
                                "itemid, itemdesc, itemtype, itemlength, field.codesetid;";
                        rs = dao.search(sql);
                        if(rs.next()){
                            fieldMap.put("itemid",rs.getString("itemid"));
                            fieldMap.put("name",rs.getString("itemdesc"));
                            String type = rs.getString("itemtype");
                            fieldMap.put("itemType",type);
                            fieldMap.put("itemLength",rs.getString("itemlength"));
                            String codesetid = rs.getString("codesetid");
                            fieldMap.put("codesetid",codesetid);
                            if("D".equalsIgnoreCase(type)){
                                fieldMap.put("format","Y.m.d");
                            }
                            //代码型指标 判断是否有多级节点
                            if(!"0".equals(codesetid)){
                                int layer = (Integer)rs.getInt("layer");
                                if(layer>1 || "UM".equalsIgnoreCase(codesetid) || "UN".equalsIgnoreCase(codesetid)){
                                    fieldMap.put("nodeLevel",false);
                                }else{
                                    fieldMap.put("nodeLevel",true);
                                }
                            }
                        }
                    }
                    fieldList.add(fieldMap);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(rs!=null){
                    rs.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return fieldList;
    }

    /**
     *
     * @Title: getSQL
     * @Description: 获取查询SQL语句
     * @param unitID    组织机构ID
     * @param keywords  便捷查询
     * @param pageIndex 第几页
     * @param pageSize  每页显示条数
     * @return String
     * @throws GeneralException
     */
    private String getSQL(String unitID, String keywords, String pageIndex,
                          String pageSize, String queryParams) throws GeneralException {
        int index = Integer.parseInt(pageIndex);
        int size = Integer.parseInt(pageSize);
        StringBuffer resultSql = new StringBuffer();
        try {
            List list;
            list = this.getNbaseList();
            if (list.size() == 0) {
                throw new GeneralException("没有人员库权限！");
            }
            String dbpre;
            StringBuffer unitIDSql = new StringBuffer();
            // 组织机构树判断
            String codeset = "";
            String codevalue = "";
            // 如果没有传入组织机构，则走管理范围
            if (unitID.length() > 0) {
                String[] temporary = unitID.split("`");
                codeset = temporary[0];
                codevalue = temporary[1];
            } else {
                codeset = userView.getManagePrivCode();
                codevalue = userView.getManagePrivCodeValue();
            }
            // UN单位名称b0110 UM部门e0122 @K岗位名称（职位）e01a1、
            if (codevalue.length() > 0) {
                if ("UN".equals(codeset) || "un".equals(codeset)) {
                    unitIDSql.append(" b0110 like '" + codevalue + "%'");
                } else if ("UM".equals(codeset) || "um".equals(codeset)) {
                    unitIDSql.append(" e0122 like '" + codevalue + "%'");
                }
            } else if (codeset.length() == 0) {
                // unitIDSql.append(" 1 = 2");
                throw new GeneralException("没有管理范围权限！");
            } else {
                unitIDSql.append(" 1 = 1");
            }

            //zxj 20160512  jazz18604 我的团队也要走高级授权
            String privWhr = userView.getPrivSQLExpression("###",false,true);

            // 快速查询判断
            StringBuffer keywordsSql = new StringBuffer();
            if(queryParams!=null){
                keywordsSql.append(this.getQueryWhereStr(queryParams));
            }else if (keywords.length() > 0 ) {
                keywordsSql.append(this.getKeywordsWhereStr(keywords));
            }
            // 循环组合SQL语句
            for (int i = 0, length = list.size(); i < length; i++) {
                dbpre = (String) list.get(i);
                resultSql.append(" union all ");
                resultSql.append("select distinct " + dbpre + "a01.a0100,'" + (i + 1) + "' ord,'" + dbpre + "' dbpre," + dbpre
                        + "a01.b0110," + dbpre + "a01.e01a1," + dbpre + "a01.e0122,a0101,a0000 ");
                //resultSql.append(" from " + dbpre + "A01");
                resultSql.append(privWhr.replace("###", dbpre));
                resultSql.append(" AND ");
                resultSql.append(unitIDSql);
                resultSql.append(keywordsSql);
            }
            dbpre = resultSql.toString().substring(11);
            resultSql.setLength(0);
            resultSql.append("select * from (select ROW_NUMBER() over(ORDER BY ord, A0000) numberCode, A.* from (");
            resultSql.append(dbpre + ") A");
            resultSql.append(") T where numberCode between " + ((index - 1) * size + 1) + " and " + (size * index));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return resultSql.toString();
    }

    /**
     *
     * @Title: getNbaseList
     * @Description:根据管理范围和系统设置的范围查询人员库，去交集
     * @return List
     * @throws GeneralException
     */
    private List getNbaseList() throws GeneralException {
        ArrayList dbpres;
        try {
//            // 系统设置的人员库
//            RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
//            String logindbpre = "";
//            if (login_vo != null)
//                logindbpre = login_vo.getString("str_value").toLowerCase();
            // 管理范围
            dbpres = userView.getPrivDbList();
            // 取交集
//            for (int i = 0; i < dbpres.size(); i++) {
//                String pre = (String) dbpres.get(i);
//                if (logindbpre.toUpperCase().indexOf(pre.toUpperCase()) == -1) {
//                    dbpres.remove(i);
//                    --i;
//                }
//            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
        return dbpres;
    }

    /**
     *
     * @Title: getQueryWhereStr
     * @Description: 获得基本信息（姓名，拼音简码，工号）条件语句
     * @param queryParams 快速查询项
     * @return String
     * @throws GeneralException
     */
    private String getQueryWhereStr(String queryParams) throws GeneralException {
        StringBuffer where = new StringBuffer();
        Map queryMap = new HashMap();
        ContentDAO dao = new ContentDAO(this.conn);
        String specialA01 = "B0110,E01A1,E0122";// A01表中有的指标不以A01开头的，进行特殊处理
        RowSet rs = null;
        Boolean isFuzzyQuery = false;
        where.append(" and (  ");
        if(queryParams!=null){
            queryMap=(HashMap) JSON.parse(queryParams);
        }
        //先获取是否勾选了“模糊查询”选项
        if((boolean)queryMap.get("isFuzzyQuery")){
            isFuzzyQuery= true;
        }
        queryMap.remove("isFuzzyQuery");
        if(queryMap.isEmpty()){
            return "";
        }
        try{
            // 循环Map 取出其key 和 value
            Iterator iter = queryMap.keySet().iterator();
            while (iter.hasNext()) {
                String fieldKey = (String) iter.next();//快速查询指标项
                String fieldValue = (String) queryMap.get(fieldKey);//快速查询指标对应的值
                // 用来获取是哪张表里的指标
                String virtualKey = fieldKey;
                String tableName = virtualKey.substring(0,3);
                if(!"A01".equalsIgnoreCase(tableName) && specialA01.indexOf(virtualKey)==-1){
                    StringBuffer sql =new StringBuffer();
                    String a0100Set = "";
                    List list;
                    list = this.getNbaseList();
                    // 循环组合SQL语句
                    for (int i = 0, length = list.size(); i < length; i++) {
                        String dbpre = (String) list.get(i);
                        sql.append(" union all ");
                        sql.append("select distinct a0100 from " + dbpre + tableName + " where "+fieldKey + "='"+fieldValue+"' ");
                    }
                    rs = dao.search(sql.toString().substring(11));
                    while(rs.next()){
                        String a0100 = rs.getString("a0100");
                        a0100Set = a0100Set + a0100+",";
                    }
                    a0100Set = a0100Set.substring(0,a0100Set.length()-1);
                    where.append(" a0100 in("+a0100Set);
                    where.append(" ) and ");
                    continue;
                }
                // 判断key中是否包含日期型的
                if(fieldKey.indexOf("from")!=-1 || fieldKey.indexOf("end")!=-1){
                    fieldKey = fieldKey.split("_")[0];
                    if(where.toString().indexOf(fieldKey)!=-1){
                        continue;
                    }
                    where.append(fieldKey + " between '" + queryMap.get(fieldKey + "_from") + "' and '" + queryMap.get(fieldKey + "_end") + "' and ");
                    continue;
                }
                //支持模糊查询
                if(isFuzzyQuery){
                    where.append(fieldKey + " like '%" + fieldValue + "%' and ");
                }else{
                    where.append(fieldKey + " = '" + fieldValue + "' and ");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        	PubFunc.closeDbObj(rs);
        }
        String whereSql = where.substring(0,where.length()-4);
        whereSql+=")";
        return whereSql;
    }

    /**
     *
     * @Title: getKeywordsWhereStr
     * @Description: 获得基本信息（姓名，拼音简码，工号）条件语句
     * @param keywords 便捷查询
     * @return String
     * @throws GeneralException
     */
    private String getKeywordsWhereStr(String keywords) throws GeneralException {
        StringBuffer where = new StringBuffer();
        try {
            String[] keyword = keywords.split("\n");
            where.append(" and (  ");// 姓名
            for (int i = 0; i < keyword.length; i++) {
                if ("".equals(keyword[i].trim())) {
                    continue;
                }
                if (i == 0) {
                    where.append(" a0101 like '%" + keyword[i] + "%' ");
                } else {
                    where.append(" or a0101 like '%" + keyword[i] + "%' ");
                }
            }
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
            String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
            FieldItem item = DataDictionary.getFieldItem(onlyname);
            if (item != null && !"a0101".equalsIgnoreCase(onlyname) && !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
                for (int i = 0; i < keyword.length; i++) {
                    if ("".equals(keyword[i].trim())) {
                        continue;
                    }
                    where.append(" or " + onlyname + " like '%" + keyword[i] + "%' ");
                }
            }
            String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
            item = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
            if (!(pinyin_field == null || "".equals(pinyin_field)
                    || "#".equals(pinyin_field) || item == null || "0".equals(item.getUseflag()))
                    && !"a0101".equalsIgnoreCase(pinyin_field)
                    && !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
                for (int i = 0; i < keyword.length; i++) {
                    if ("".equals(keyword[i].trim())) {
                        continue;
                    }
                    where.append(" or " + pinyin_field + " like '%" + keyword[i] + "%' ");
                }
            }
            where.append(")");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return where.toString();
    }

    /**
     *
     * @Title: getPersonList
     * @Description: 根据sql语句得到人员List
     * @param sql
     * @param url
     * @return List
     * @throws GeneralException
     */
    private List getPersonList(String sql, String url) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        RowSet rs = null;
        HashMap map = null;
        try {
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
            String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            display_e0122 = display_e0122 == null || display_e0122.length() == 0 ? "0" : display_e0122;
            String seprartor = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
            seprartor = seprartor != null && seprartor.length() > 0 ? seprartor : "/";
            rs = dao.search(sql.toString());
            while (rs.next()) {
                map = new HashMap();
                String a0100 = rs.getString("a0100");
                String dbpre = rs.getString("dbpre");
                map.put("dbpre", dbpre);
                map.put("a0100", a0100);
                map.put("b0110", rs.getString("b0110"));
                map.put("name", rs.getString("a0101"));
                String b0110 = rs.getString("b0110");
                String e0122 = rs.getString("e0122");
                String e01a1 = rs.getString("e01a1");
                b0110 = AdminCode.getCodeName("UN", b0110);
                b0110 = b0110 == null ? "" : b0110.trim();
                CodeItem itemid = AdminCode.getCode("UM", e0122, Integer.parseInt(display_e0122));
                if (itemid != null) {
                    e0122 = itemid.getCodename();
                }
                e0122 = e0122 == null ? "" : e0122.trim();
                e01a1 = AdminCode.getCodeName("@K", e01a1);
                e01a1 = e01a1 == null ? "" : e01a1.trim();
                String org = b0110 + (b0110.length() > 0 && e0122.length() > 0 ? seprartor : "")
                        + e0122 + (e01a1.length() > 0 && e0122.length() > 0 ? seprartor : "") + e01a1;
                map.put("b0110_name", b0110);
                map.put("e0122_name", e0122);
                map.put("e01a1_name", e01a1);
                map.put("org",org);
                // 获取设置的人员描述
                String info = this.getInfo(dbpre, a0100);
                info = info.replaceAll("\r", "").replaceAll("\n", "").replace("\r\n", "").trim();
                map.put("info", info);
                // 照片地址
                StringBuffer photourl = new StringBuffer();
                String filename = getPicUrl( dbpre, rs.getString("a0100"));
                if (!"".equals(filename)) {
                    photourl.append(url);
                    photourl.append(filename);
                } else {
                    photourl.append(url);
                    photourl.append("/images/photo.jpg");
                }
                map.put("photo", photourl.toString());
                list.add(map);
            }
        } catch (SQLException e) {
            throw GeneralExceptionHandler.Handle(e);
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        	PubFunc.closeDbObj(rs);
        }
        return list;
    }

    /**
     * 获取人员信息简介
     *
     * @param dbpre
     * @param a0100
     * @return
     * @throws GeneralException
     */
    private String getInfo(String dbpre, String a0100) throws GeneralException {
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            Map map = HTMLParamUtils.getBasicinfo_Map(conn);
            if (map == null) {
                return "";
            }
            String basicinfo_template = (String) map.get("basicinfo_template");
            Map mapsets = (Map) map.get("mapsets");
            Map mapsetstr = (Map) map.get("mapsetstr");
            for (Iterator i = mapsets.keySet().iterator(); i.hasNext();) {
                String setid = (String) i.next();
                List itemids = (List) mapsets.get(setid);
                String itemidstr = ((StringBuffer) mapsetstr.get(setid)).substring(1);
                StringBuffer sql = new StringBuffer();
                sql.append("select " + itemidstr + " from " + dbpre + setid + " where a0100='" + a0100 + "'");
                if (!"A01".equals(setid)) {
                    sql.append(" and i9999=(select max(i9999) from " + dbpre + setid + " where a0100='" + a0100 + "')");
                }
                rs = dao.search(sql.toString());
                if (rs.next()) {
                    for (int n = 0; n < itemids.size(); n++) {
                        String itemid = (String) itemids.get(n);
                        FieldItem fielditem = DataDictionary.getFieldItem(itemid);
                        String itemtype = fielditem.getItemtype();
                        String value = "";
                        if ("N".equals(itemtype)) {
                            if(fielditem.getDecimalwidth()>0) {
                                value = String.valueOf(rs.getObject(itemid));
                            } else {
                                value = String.valueOf(rs.getInt(itemid));
                            }
                        } else if ("D".equals(itemtype)) {
                            Object obj = rs.getDate(itemid);
                            value = String.valueOf(obj == null ? "" : obj);
                            value = value.replace('-', '.');
                        } else if ("A".equals(itemtype)) {
                            String codesetid = fielditem.getCodesetid();
                            value = rs.getString(itemid);
                            value = value == null ? "" : value;
                            if (!(codesetid.length() == 0 || "0".equals(codesetid))) {
                                value = AdminCode.getCodeName(codesetid, value);
                            }
                        }
                        basicinfo_template = basicinfo_template.replace("[" + itemid + "]", value);
                    }
                } else {
                    for (int n = 0; n < itemids.size(); n++) {
                        String itemid = (String) itemids.get(n);
                        basicinfo_template = basicinfo_template.replace("[" + itemid + "]", "");
                    }
                }
            }
            return basicinfo_template;
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        	PubFunc.closeDbObj(rs);
        }
    }

    /**
     * xus 判断内存中是否存在图片，如果存在在内存中获取 否则在库中获取
     * @param dbpre
     * @param A0100
     * @return
     */
    private String getPicUrl(String dbpre,String A0100){
        String url="";
        String filename="";
        StringBuffer photourl=new StringBuffer();
        PhotoImgBo pib = new PhotoImgBo(conn);
        pib.setIdPhoto(true);
        String absPath = "";
        boolean genPhotoSuccess=false;
        try{
            absPath = pib.getPhotoRootDir();
        }catch(Exception ex){
        }
        if(absPath != null && absPath.length() > 0){
            try {
                absPath += pib.getPhotoRelativeDir(dbpre, A0100);

                String guid = pib.getGuid();
                //获取 文件名为 “photo.xxx”的文件，格式未知
                String fileWName = pib.getPersonImageWholeName(absPath, "photo");

                // 如果不存在文件，创建文件
                if (fileWName.length() < 1) {
                    fileWName = pib.createPersonPhoto(absPath, conn, dbpre,
                            A0100, "photo");
                }

                //如果有图片或创建了图片，使用新图片
                if (fileWName.length() > 0) {
                    absPath += fileWName;

                    filename = pib.getPhotoPath(dbpre, A0100);
                    this.userView.getHm().put(guid, absPath);

                    // 只要能走到这里，表示照片成功产生了
                    genPhotoSuccess = true;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(!genPhotoSuccess){
            // 如果不存在文件，创建文件
            try {
                filename = pib.getPhotoPath(dbpre, A0100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filename;
    }

    /*
    * 获取人员范围员工总数
    * */
    private int getTotalCount(String loadType,String unitid,String cond, String queryParams){
        int count=0;
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            //根据加载类型判断
            String countSql = "SELECT  count(1) count  FROM UsrA01 WHERE '1'='1' ";
            String orgCond="";
            String hrCond="";
            if(!"".equals(unitid)&&!"ALL".equals(unitid)) {
                orgCond="And UsrA01.E0122 LIKE '"+unitid+"%' ";
            }
            if(!"".equals(cond)){
                hrCond=getCountsWhereStr(cond);
            }else if(queryParams!=null){
                hrCond = getQueryCountsWhereStr(queryParams);
            }
            countSql=countSql+orgCond+hrCond;
            rs = dao.search(countSql);
            if(rs.next()){
                count = rs.getInt("count");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(rs!=null){
                    rs.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return count;
    }

    /*
    * 获取员工总数控制语句
    * */
    private String getCountsWhereStr(String keywords) throws GeneralException {
        StringBuffer where = new StringBuffer();
        try {
            String[] keyword = keywords.split("\n");
            where.append(" and (  ");// 姓名
            for (int i = 0; i < keyword.length; i++) {
                if ("".equals(keyword[i].trim())) {
                    continue;
                }
                if (i == 0) {
                    where.append(" a0101 like '%" + keyword[i] + "%' ");
                } else {
                    where.append(" or a0101 like '%" + keyword[i] + "%' ");
                }
            }
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
            String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
            FieldItem item = DataDictionary.getFieldItem(onlyname);
            if (item != null && !"a0101".equalsIgnoreCase(onlyname) && !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
                for (int i = 0; i < keyword.length; i++) {
                    if ("".equals(keyword[i].trim())) {
                        continue;
                    }
                    where.append(" or " + onlyname + " like '%" + keyword[i] + "%' ");
                }
            }
            String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
            item = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
            if (!(pinyin_field == null || "".equals(pinyin_field)
                    || "#".equals(pinyin_field) || item == null || "0".equals(item.getUseflag()))
                    && !"a0101".equalsIgnoreCase(pinyin_field)
                    && !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
                for (int i = 0; i < keyword.length; i++) {
                    if ("".equals(keyword[i].trim())) {
                        continue;
                    }
                    where.append(" or " + pinyin_field + " like '%" + keyword[i] + "%' ");
                }
            }
            where.append(")");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return where.toString();
    }
    /*
    * 通过快速查询获取的人员数量
    * */
    private String getQueryCountsWhereStr(String queryParams){
        StringBuffer where = new StringBuffer();
        Map queryMap = new HashMap();
        ContentDAO dao = new ContentDAO(this.conn);
        String specialA01 = "B0110,E01A1,E0122";// A01表中有的指标不以A01开头的，进行特殊处理
        RowSet rs = null;
        Boolean isFuzzyQuery = false;
        where.append(" and (  ");
        if(queryParams!=null){
            queryMap=(HashMap) JSON.parse(queryParams);
        }
        //先获取是否勾选了“模糊查询”选项
        if((boolean)queryMap.get("isFuzzyQuery")){
            isFuzzyQuery= true;
        }
        queryMap.remove("isFuzzyQuery");
        if(queryMap.isEmpty()){
            return "";
        }
        try{
            // 循环Map 取出其key 和 value
            Iterator iter = queryMap.keySet().iterator();
            while (iter.hasNext()) {
                String fieldKey = (String) iter.next();//快速查询指标项
                String fieldValue = (String) queryMap.get(fieldKey);//快速查询指标对应的值
                // 用来获取是哪张表里的指标
                String virtualKey = fieldKey;
                String tableName = virtualKey.substring(0,3);
                if(!"A01".equalsIgnoreCase(tableName) && specialA01.indexOf(virtualKey)==-1){
                    StringBuffer sql =new StringBuffer();
                    String a0100Set = "";
                    List list;
                    list = this.getNbaseList();
                    // 循环组合SQL语句
                    for (int i = 0, length = list.size(); i < length; i++) {
                        String dbpre = (String) list.get(i);
                        sql.append(" union all ");
                        sql.append("select distinct a0100 from " + dbpre + tableName + " where "+fieldKey + "='"+fieldValue+"' ");
                    }
                    rs = dao.search(sql.toString().substring(11));
                    while(rs.next()){
                        String a0100 = rs.getString("a0100");
                        a0100Set = a0100Set + a0100+",";
                    }
                    a0100Set = a0100Set.substring(0,a0100Set.length()-1);
                    where.append(" a0100 in("+a0100Set);
                    where.append(" ) and ");
                    continue;
                }
                // 判断key中是否包含日期型的
                if(fieldKey.indexOf("from")!=-1 || fieldKey.indexOf("end")!=-1){
                    fieldKey = fieldKey.split("_")[0];
                    if(where.toString().indexOf(fieldKey)!=-1){
                        continue;
                    }
                    where.append(fieldKey + " between '" + queryMap.get(fieldKey + "_from") + "' and '" + queryMap.get(fieldKey + "_end") + "' and ");
                    continue;
                }
                //支持模糊查询
                if(isFuzzyQuery){
                    where.append(fieldKey + " like '%" + fieldValue + "%' and ");
                }else{
                    where.append(fieldKey + " = '" + fieldValue + "' and ");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        	PubFunc.closeDbObj(rs);
        }
        String whereSql = where.substring(0,where.length()-4);
        whereSql+=")";
        return whereSql;
    }
}
