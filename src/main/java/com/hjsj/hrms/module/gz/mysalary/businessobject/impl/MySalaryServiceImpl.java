package com.hjsj.hrms.module.gz.mysalary.businessobject.impl;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.gz.mysalary.businessobject.MySalaryService;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.sql.RowSet;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MySalaryServiceImpl implements MySalaryService {

    private static Category log = Category.getInstance(MySalaryServiceImpl.class.getName());

    Connection conn;

    public MySalaryServiceImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List listMySalaryScheme(UserView userView) throws GeneralException {
        ArrayList schemeList = new ArrayList();
        String unitPriv = userView.getUnitIdByBusi("1");//获取权限
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        sql.append("select id,name,salary_table,B0110,role_id ");
        sql.append("from gz_table_scheme ");
        sql.append("where 1=1 ");
        if (unitPriv.indexOf("UN`") == -1) {
        	unitPriv = unitPriv.replaceAll("`", ",");//每种权限分割符可能会不一样
        	String[] unitPrivs = unitPriv.split(",");
            sql.append("and ");
            sql.append("( ");
            for (int i = 0; i < unitPrivs.length; i++) {
                sql.append("B0110 like ? or ");
                list.add(unitPrivs[i] + "%");
            }
            if (unitPrivs.length > 0)
                sql.setLength(sql.length() - 3);
            sql.append(") ");
        }
        sql.append(" order by norder");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = PubFunc.hireKeyWord_filter(rs.getString("name"));
                String salary_table = rs.getString("salary_table");
                String B0110 = rs.getString("B0110");
                String role_id = rs.getString("role_id");
                HashMap map = new HashMap();
                map.put("id", PubFunc.encrypt(String.valueOf(id)));
                map.put("name", name);
                map.put("salary_table", salary_table);
                FieldSet fieldset = DataDictionary.getFieldSetVo(salary_table);
                String salary_table_name = "";
                if (fieldset != null) {//避免有脏数据 salary_table为空导致前台页面出不来的问题
                    salary_table_name = fieldset.getFieldsetdesc();
                }
                map.put("salary_table_name", salary_table_name);
                String b0100_name = null;
                if (StringUtils.isNotEmpty(B0110) && AdminCode.getCode(B0110.substring(0, 2), B0110.substring(2)) != null) {//避免中途添加或删除组织机构造成空指针异常
                    b0100_name = AdminCode.getCode(B0110.substring(0, 2), B0110.substring(2)).getCodename();
                    map.put("B0110", StringUtils.isNotEmpty(b0100_name) ? B0110.substring(0, 2) + b0100_name : "");
                    map.put("B0110_name", b0100_name);
                } else {
                    map.put("B0110", "");
                    map.put("B0110_name", "");
                }
                //map.put("B0110_name", codeItem.getCodename());
                //CodeItem codeItem = AdminCode.getCode(B0110.substring(0, 2),B0110.substring(2));
                if (role_id == null || role_id.length() == 0 || "all".equalsIgnoreCase(role_id)) {
                    map.put("role_id", "");
                    map.put("role_name", "");
                } else {
                    map.put("role_id", role_id);
                    map.put("role_name", this.getRoleName(role_id));
                }
                schemeList.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取权限范围内薪酬方案出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getprivscheme"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getprivscheme"));
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return schemeList;
    }


    @Override
    public HashMap getMySalaryScheme(String id, UserView userView) throws GeneralException {
        HashMap map = new HashMap();
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        sql.append("select id,name,salary_table,salary_date,payable,taxable,incometax,realpay,B0110,role_id,zeroItemCtrl,year,items,norder from gz_table_scheme where 1=1 and id=?");
        if (StringUtils.isNotBlank(id)) {
            list.add(Integer.parseInt(PubFunc.decrypt(id)));
        } else {
            list.add("");
        }
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
                //rs.getInt("id");
                String name = PubFunc.hireKeyWord_filter(rs.getString("name"));
                String salary_table = rs.getString("salary_table");
                String salary_date = rs.getString("salary_date");
                String payable = rs.getString("payable");
                String taxable = rs.getString("taxable");
                String incometax = rs.getString("incometax");
                String realpay = rs.getString("realpay");
                String B0110 = rs.getString("B0110");
                String role_id = rs.getString("role_id");
                String zeroItemCtrl = rs.getString("zeroItemCtrl");
                String items = rs.getString("items");
                int year = rs.getInt("year");
                int norder = rs.getInt("norder");
                HashMap salary_fields = new HashMap();
                salary_fields.put("id", id);
                salary_fields.put("name", name);
                salary_fields.put("salary_table", salary_table);
                FieldSet fieldset = DataDictionary.getFieldSetVo(salary_table);
                map.put("salary_table_name", fieldset == null ? "" : fieldset.getFieldsetdesc());
                salary_fields.put("salary_date", salary_date);
                salary_fields.put("payable", payable);
                salary_fields.put("taxable", taxable);
                salary_fields.put("incometax", incometax);
                salary_fields.put("realpay", realpay);
                salary_fields.put("year", year);
                salary_fields.put("norder", norder);
                String b0100_name = "";
                if (StringUtils.isNotEmpty(B0110)) {
                    b0100_name = AdminCode.getCode(B0110.substring(0, 2), B0110.substring(2)).getCodename();
                }
                salary_fields.put("B0110", StringUtils.isNotEmpty(b0100_name) && StringUtils.isNotEmpty(B0110) ? B0110.substring(2) + "`" + b0100_name : "");
                map.put("B0110_name", b0100_name);
                if (role_id == null || role_id.length() == 0 || "all".equalsIgnoreCase(role_id)) {
                    map.put("role_id", "");
                    map.put("role_name", "");
                } else {
                    String[] roleid_array = role_id.split(",");
                    String roleid = "";
                    for (int i = 0; i < roleid_array.length; i++) {
                        if (StringUtils.isEmpty(roleid_array[i])) {
                            continue;
                        }
                        roleid += PubFunc.encrypt(roleid_array[i]);
                        if (i < roleid_array.length - 1) {
                            roleid += ",";
                        }
                    }
                    map.put("role_id", roleid);
                    map.put("role_name", this.getRoleName(role_id));
                }
                salary_fields.put("zeroItemCtrl", zeroItemCtrl);
                map.put("salary_fields", salary_fields);


                //获取自定义薪资结构
                if (StringUtils.isEmpty(items)) {
                    items = "[]";
                }
                ArrayList itemList = new ArrayList();
                JSONArray jsonArray = JSONArray.fromObject(items);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject itemMap = (JSONObject) jsonArray.get(i);
                    String item = (String) itemMap.get("item");
                    String[] fielditems = null;
                    if (StringUtils.isNotEmpty(item)) {
                        fielditems = item.split(",");
                        StringBuffer descStr = new StringBuffer();
                        for (int j = 0; j < fielditems.length; j++) {
                            FieldItem fieldItem = DataDictionary.getFieldItem(fielditems[j]);
                            String itemdesc = fieldItem == null ? "" :fieldItem.getItemdesc();
                            descStr.append(itemdesc + ",");
                        }
                        if (descStr.length() > 0)
                            descStr.setLength(descStr.length() - 1);
                        itemMap.put("itemname", descStr.toString());
                    }
                    itemList.add(itemMap);
                }
                map.put("salary_items", itemList);

                //获取配置视图表的指标
                ArrayList fieldList = DataDictionary.getFieldList(salary_table, Constant.EMPLOY_FIELD_SET);
                ArrayList table_items = new ArrayList();
                if (fieldList != null) {
                    for (int i = 0; i < fieldList.size(); i++) {
                        FieldItem fieldItem = (FieldItem) fieldList.get(i);
                        HashMap itemHM = new HashMap();
                        itemHM.put("fielditem", fieldItem.getItemid());
                        itemHM.put("fieldname", fieldItem.getItemdesc());
                        table_items.add(itemHM);
                    }
                }
                map.put("table_items", table_items);
            }
            //获取角色列表
            ArrayList roleList = userView.getRolelist();
            map.put("role_items", this.listRole(roleList));

            //获取薪酬视图表
            sql.setLength(0);
            list.clear();
            ArrayList tableList = new ArrayList();
            sql.append("select fieldsetid,customdesc from t_hr_busitable where id=50 and fieldsetid like 'V_MY_GZ_%'");
            rs = dao.search(sql.toString());
            while (rs.next()) {
                HashMap tableHM = new HashMap();
                tableHM.put("salary_table", rs.getString("fieldsetid"));
                tableHM.put("salary_table_name", rs.getString("customdesc"));
                tableList.add(tableHM);
            }
            map.put("salary_table", tableList);
        } catch (SQLException e) {
            e.printStackTrace();
            //获取我的薪酬方案出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmysalaryscheme"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmysalaryscheme"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    @Override
    public HashMap saveMySalaryScheme(Map data, UserView userView) throws GeneralException {
        HashMap map = new HashMap();
        RecordVo vo = new RecordVo("gz_table_scheme");
        vo.setString("name", PubFunc.hireKeyWord_filter((String) data.get("name")));
        vo.setString("salary_table", (String) data.get("salary_table"));
        vo.setString("salary_date", (String) data.get("salary_date"));
        vo.setString("payable", (String) data.get("payable"));
        vo.setString("taxable", (String) data.get("taxable"));
        vo.setString("incometax", (String) data.get("incometax"));
        vo.setString("realpay", (String) data.get("realpay"));
        vo.setString("b0110", (String) data.get("B0110"));
        vo.setInt("year",(Integer)data.get("year"));
        String role_id = (String) data.get("role_id");
        role_id = role_id == null || role_id.trim().length() == 0 ? null : role_id;
        String realRoleId = "";
        if (StringUtils.isNotEmpty(role_id)) {
            String[] roleIdArray = role_id.split(",");
            if (roleIdArray.length > 0) {
                realRoleId += ",";
            }
            for (int i = 0; i < roleIdArray.length; i++) {
                if (StringUtils.isNotEmpty(PubFunc.decrypt(roleIdArray[i]))) {
                    realRoleId += PubFunc.decrypt(roleIdArray[i]);
                    realRoleId += ",";
                }
            }
        }
        vo.setString("role_id", realRoleId);
        vo.setString("zeroitemctrl", (String) data.get("zeroItemCtrl"));
        vo.setString("items", ((JSONArray) data.get("items")).toString());

        ContentDAO dao = new ContentDAO(this.conn);
        int i = 0;
        try {
            if (data.containsKey("id")) {
                vo.setInt("id", Integer.parseInt(PubFunc.decrypt((String) data.get("id"))));
                i = dao.updateValueObject(vo);
            } else {
                vo.setString("create_user", userView.getUserName());
                Date nowDate = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String create_date = simpleDateFormat.format(nowDate);
                vo.setDate("create_time", create_date);
                vo.setInt("id", this.getMaxSchemeId());
                i = dao.addValueObject(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //保存我的薪资方案出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.savemysalaryscheme"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.savemysalaryscheme"));
        }
        if (i <= 0) {
            //未保存成功我的薪酬方案
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.notsavemysalaryscheme"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.notsavemysalaryscheme"));
        }
        map.put("id", PubFunc.encrypt(String.valueOf(vo.getObject("id"))));
        map.put("salary_table", vo.getString("salary_table"));
        map.put("name", vo.getString("name"));
        FieldSet fieldset = DataDictionary.getFieldSetVo(vo.getString("salary_table"));
        map.put("salary_table_name", fieldset.getFieldsetdesc());
        String b0110 = (String) data.get("B0110");
        String b0100_name = "";
        if (StringUtils.isNotEmpty(b0110)) {
            b0100_name = AdminCode.getCode(b0110.substring(0, 2), b0110.substring(2)).getCodename();
        }
        map.put("B0110", b0110);
        map.put("B0110_name", b0100_name);
        map.put("role_id", vo.getString("role_id"));
        map.put("role_name", this.getRoleName(vo.getString("role_id")));
        return map;
    }

    @Override
    public String deleteMySalaryScheme(String id) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        try {
            sql.append("delete gz_table_scheme where 1=1 and ");
            String[] ids = id.split(",");
            sql.append("id in (");
            for (int i = 0; i < ids.length; i++) {
                sql.append("?,");
                list.add(Integer.parseInt(PubFunc.decrypt(ids[i])));
            }
            if (ids.length > 0)
                sql.setLength(sql.length() - 1);
            sql.append(")");
            ContentDAO dao = new ContentDAO(this.conn);
            dao.delete(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
            //删除我的薪酬方案出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.deletemysalaryscheme"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.deletemysalaryscheme"));
        }
        return "success";
    }

    @Override
    public HashMap getMySalaryViewField(String salary_table) throws GeneralException {
        HashMap map = new HashMap();
        // 将字符型指标放开
        String sql = "select itemid,itemdesc,itemtype from t_hr_busifield where 1=1 and FieldSetId=? and useflag=1 and itemtype in ('A','N','D') and itemid not like '___Z1' and itemid !='I9999' order by displayid";
        ArrayList list = new ArrayList();
        list.add(salary_table);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        ArrayList dateList = new ArrayList();
        ArrayList numberList = new ArrayList();
        // 需要替换日期型名称的指标项（如果有修改后的替换）
        ArrayList replaceDateFieldItem = new ArrayList();
        // 包含数值型和字符型指标的数组
        ArrayList characterAndNumberList = new ArrayList();
        HashMap defaultOption = new HashMap();
        defaultOption.put("itemid","none");
        defaultOption.put("itemdesc","");// 修改为“”
        numberList.add(defaultOption);
        // 获取视图表的主表
        String fieldSetId = "";
        StringBuffer searchSalaryTableMain = new StringBuffer();
        RowSet searchSalaryTableMainSet = null;
        searchSalaryTableMain.append("select classpre from t_hr_busitable where FieldSetId = ? ");
        try {
            searchSalaryTableMainSet = dao.search(searchSalaryTableMain.toString(),Arrays.asList(salary_table));
            while (searchSalaryTableMainSet.next()){
                String classpre = searchSalaryTableMainSet.getString("classpre");
                StringReader s = new StringReader(classpre);
                Document classpreDoc = (new SAXBuilder()).build(s);
                Element classpreEle = classpreDoc.getRootElement();
                Element fieldsetidElement = classpreEle.getChild("fieldsetid");
                if (fieldsetidElement!=null){
                    fieldSetId = fieldsetidElement.getTextTrim();
                }
            }

            rs = dao.search(sql, list);
            while (rs.next()) {
                HashMap itemHM = new HashMap();
                itemHM.put("itemid", rs.getString("itemid"));
                itemHM.put("itemdesc", rs.getString("itemdesc"));
                if ("N".equalsIgnoreCase(rs.getString("itemtype"))){
                    numberList.add(itemHM);
                    characterAndNumberList.add(itemHM);
                }else if ("A".equalsIgnoreCase(rs.getString("itemtype"))){
                    characterAndNumberList.add(itemHM);
                }else if ("D".equalsIgnoreCase(rs.getString("itemtype"))&&rs.getString("itemid").substring(0,3).equalsIgnoreCase(fieldSetId)){
                    dateList.add(itemHM);
                }
            }
            map.put("N", numberList);
            map.put("D", dateList);
            map.put("A", characterAndNumberList);
        } catch (SQLException e) {
            e.printStackTrace();
            //获取薪酬视图指标出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getSalaryViewField"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getSalaryViewField"));
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 北辰2开个性化接口
     */
    @Override
    public HashMap getMySalaryInfo(UserView userView, String id, String startDate, String endDate) throws GeneralException {
        HashMap dataHM = new HashMap();
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        HashMap map = null;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        RowSet rows = null;
        HashMap viewFieldHM = null;
        if (id == null || id.trim().length() == 0) {
            map = this.getMySalaryScheme(userView);
        } else {
            sql.append("select id,name,salary_table,salary_date,payable,taxable,incometax,realpay,zeroItemCtrl,items from gz_table_scheme where 1=1 and id=?");
            list.add(Integer.parseInt(PubFunc.decrypt(id)));
            try {
                rs = dao.search(sql.toString(), list);
                if (rs.next()) {
                    map = new HashMap();
                    String name = PubFunc.hireKeyWord_filter(rs.getString("name"));
                    String salary_table = rs.getString("salary_table");
                    viewFieldHM = this.getViewField(userView,salary_table,"self",false);
                    String salary_date = rs.getString("salary_date");
                    String payable = rs.getString("payable");
                    String taxable = rs.getString("taxable");
                    String incometax = rs.getString("incometax");
                    String realpay = rs.getString("realpay");
                    String zeroItemCtrl = rs.getString("zeroItemCtrl");
                    String items = rs.getString("items");
                    out:if(!viewFieldHM.containsKey(salary_date) || !viewFieldHM.containsKey(payable) || !viewFieldHM.containsKey(taxable)|| !viewFieldHM.containsKey(incometax)|| !viewFieldHM.containsKey(realpay)){
                        if("none".equalsIgnoreCase(taxable)||"none".equalsIgnoreCase(incometax)||"none".equalsIgnoreCase(payable)||"none".equalsIgnoreCase(realpay)){
                            break out;
                        }
                    	log.error("GZSchemeFieldConfigError");
                    	throw new GeneralException("GZSchemeFieldConfigError");
                    }
                    map.put("id", PubFunc.encrypt(String.valueOf(rs.getInt("id"))));
                    map.put("name", name);
                    map.put("salary_table", salary_table);
                    map.put("salary_date", salary_date);
                    map.put("payable", payable);
                    map.put("taxable", taxable);
                    map.put("incometax", incometax);
                    map.put("realpay", realpay);
                    map.put("zeroItemCtrl", zeroItemCtrl);
                    map.put("items", items);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                //获取我的薪酬方案出错
                log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmysalaryscheme"));
                throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmysalaryscheme"));
            }
        }
        viewFieldHM = this.getViewField(userView,(String)map.get("salary_table"),"self",false);
        sql.setLength(0);
        list.clear();
        StringBuffer sql1 = new StringBuffer();//初始化进入时，没有起始时间和结束时间
        sql1.append("select " + map.get("salary_date") + ",");
        if(!"none".equalsIgnoreCase(map.get("payable").toString())){
            sql1.append(""+ map.get("payable") + " as payable,");
        }
        if(!"none".equalsIgnoreCase(map.get("taxable").toString())){
            sql1.append(""+ map.get("taxable") + " as taxable,");
        }
        if(!"none".equalsIgnoreCase(map.get("incometax").toString())){
            sql1.append("" + map.get("incometax") + " as incometax,");
        }
        if(!"none".equalsIgnoreCase(map.get("realpay").toString())){
            sql1.append(map.get("realpay") + " as realpay,");
        }
        sql.append("select ");
        if(!"none".equalsIgnoreCase(map.get("payable").toString())){
            sql.append("sum(" + map.get("payable") + ") as payable,");
        }
        if(!"none".equalsIgnoreCase(map.get("taxable").toString())){
            sql.append("sum(" + map.get("taxable") + ") as taxable,");
        }
        if(!"none".equalsIgnoreCase(map.get("incometax").toString())){
            sql.append("sum(" + map.get("incometax") + ") as incometax,");
        }
        if(!"none".equalsIgnoreCase(map.get("realpay").toString())){
            sql.append("sum(" + map.get("realpay") + ") as realpay,");
        }
        JSONArray jsonArray = JSONArray.fromObject(map.get("items"));
        for (int i = 0; i < jsonArray.size(); i++) {
            Map itemHM = JSONObject.fromObject(jsonArray.get(i));
            String field = (String) itemHM.get("item");
            if (StringUtils.isNotBlank(field)) {
                String[] fields = field.split(",");
                for (int j = 0; j < fields.length; j++) {
                	if(!viewFieldHM.containsKey(fields[j])){
                		continue;
                	}
                	// 判断指标是否是字符型的数据
                    FieldItem fieldItem = DataDictionary.getFieldItem(fields[j]);
                	if ("N".equals(fieldItem.getItemtype())){
                        sql.append("sum(" + fields[j] + ") as " + fields[j] + ",");
                    }else {
                        sql.append("Max(" + fields[j] + ") as " + fields[j] + ",");
                    }
                    sql1.append(fields[j] + ",");
                }
//				if(fields.length > 0 && i == jsonArray.size()-1) {
//					sql.setLength(sql.length()-1);
//					sql1.setLength(sql1.length()-1);
//				}
            }
        }
        sql1.setLength(sql1.length() - 1);
        sql.setLength(sql.length() - 1);
        sql1.append(" from " + map.get("salary_table") + " where 1=1 and nbase=? and A0100=? ");
        sql.append(" from " + map.get("salary_table") + " ");
        sql.append(" where 1=1 and ");
        sql.append("nbase = ? and A0100 = ? and ");
        list.add(userView.getDbname());
        list.add(userView.getA0100());
        try {
            if ((startDate != null && startDate.trim().length() > 0) && (endDate != null && endDate.trim().length() > 0)) {
                if (Sql_switcher.dbflag == 1)
                    sql.append("CONVERT(varchar(7)," + map.get("salary_date") + ",23)>=? and CONVERT(varchar(7)," + map.get("salary_date") + ",23)<=? ");
                else if (Sql_switcher.dbflag == 2)
                    sql.append("to_char(" + map.get("salary_date") + ",'yyyy-mm')>=? and to_char(" + map.get("salary_date") + ",'yyyy-mm')<=? ");
                sql.append("group by A0100");
                list.add(startDate);
                list.add(endDate);
                rs = dao.search(sql.toString(), list);
                if (rs.next()) {
                    //获取固定薪资指标
                    if(!"none".equalsIgnoreCase(map.get("payable").toString())){
                        dataHM.put("payable", rs.getDouble("payable"));
                    }
                    if(!"none".equalsIgnoreCase(map.get("taxable").toString())){
                        dataHM.put("taxable", rs.getDouble("taxable"));
                    }
                    if(!"none".equalsIgnoreCase(map.get("incometax").toString())){
                        dataHM.put("incometax", rs.getDouble("incometax"));
                    }
                    if(!"none".equalsIgnoreCase(map.get("realpay").toString())){
                        dataHM.put("realpay", rs.getDouble("realpay"));
                    }
                    dataHM.put("zeroItemCtrl", map.get("zeroItemCtrl"));

                    //获取自定义工资结构
                    ArrayList itemList = new ArrayList();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Map itemHM = JSONObject.fromObject(jsonArray.get(i));
                        String field = (String) itemHM.get("item");
                        ArrayList itemDataList = new ArrayList();
                        double values = 0;
                        if (StringUtils.isNotBlank(field)) {
                            String[] fields = field.split(",");
                            for (int j = 0; j < fields.length; j++) {
                            	if(!viewFieldHM.containsKey(fields[j])){
                            		continue;
                            	}
                                rows = null;
                                HashMap fieldHM = new HashMap();
                                // double value = rs.getDouble(fields[j]);
                                // 定义指标类型
                                String itemType = "";
                                list.clear();
                                list.add((String)map.get("salary_table"));
                                list.add(fields[j]);
                                rows = dao.search("select itemdesc,itemtype from t_hr_busifield where FieldSetId = ? and ItemId = ?",list);
                                if(rows.next()){
                                    fieldHM.put("name", rows.getString("itemdesc"));
                                    // 获取到指标类型
                                    itemType =  rows.getString("itemtype");
                                }
                                double value = 0;
                                // 判断指标类型是否是数值型
                                if ("N".equals(itemType)){
                                    value = rs.getDouble(fields[j]);
                                    fieldHM.put("value", value);
                                }else{
                                    String codesetid = DataDictionary.getFieldItem(fields[j]).getCodesetid();
                                    if ("0".equals(codesetid)){
                                        fieldHM.put("value", "0".equals(rs.getString(fields[j]))?"":rs.getString(fields[j]));
                                    }else{
                                        String codeItemDesc = AdminCode.getCodeName(codesetid,rs.getString(fields[j]));
                                        fieldHM.put("value", codeItemDesc);
                                    }
                                }
                                fieldHM.put("itemtype", itemType);
                                values += value;
                                itemDataList.add(fieldHM);
                            }
                        }
                        HashMap itemDataHM = new HashMap();
                        itemDataHM.put("name", itemHM.get("name"));
                        itemDataHM.put("chart", itemHM.get("chart"));
                        itemDataHM.put("total", itemHM.get("total"));
                        itemDataHM.put("value", values);
                        itemDataHM.put("fieldList", itemDataList);
                        itemList.add(itemDataHM);
                    }
                    dataHM.put("items", itemList);
                }
            } else {
                sql1.append("order by " + map.get("salary_date") + " desc ");
                rs = dao.search(sql1.toString(), list);
                if (rs.next()) {
                    //获取固定薪资指标
                    if(!"none".equalsIgnoreCase(map.get("payable").toString())){
                        dataHM.put("payable", rs.getDouble("payable"));
                    }
                    if(!"none".equalsIgnoreCase(map.get("taxable").toString())){
                        dataHM.put("taxable", rs.getDouble("taxable"));
                    }
                    if(!"none".equalsIgnoreCase(map.get("incometax").toString())){
                        dataHM.put("incometax", rs.getDouble("incometax"));
                    }
                    if(!"none".equalsIgnoreCase(map.get("realpay").toString())){
                        dataHM.put("realpay", rs.getDouble("realpay"));
                    }
                    dataHM.put("zeroItemCtrl", map.get("zeroItemCtrl"));
                    Date date = rs.getDate((String) map.get("salary_date"));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                    String[] time = sdf.format(date).split("-");
                    dataHM.put("year", time[0]);
                    dataHM.put("month", time[1]);
                    //获取自定义工资结构
                    ArrayList itemList = new ArrayList();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Map itemHM = JSONObject.fromObject(jsonArray.get(i));
                        String field = (String) itemHM.get("item");
                        ArrayList itemDataList = new ArrayList();
                        double values = 0;
                        if (StringUtils.isNotBlank(field)) {
                            String[] fields = field.split(",");
                            for (int j = 0; j < fields.length; j++) {
                                HashMap fieldHM = new HashMap();
                                // 定义指标类型
                                String itemType = "";
                                rows = null;
                                list.clear();
                                list.add((String)map.get("salary_table"));
                                list.add(fields[j]);
                                rows = dao.search("select itemdesc,itemtype from t_hr_busifield where FieldSetId = ? and ItemId = ?",list);
                                if(rows.next()){
                                    fieldHM.put("name", rows.getString("itemdesc"));
                                    // 获取到指标类型
                                    itemType =  rows.getString("itemtype");
                                }
                                double value = 0;
                                // 判断指标类型是否是数值型
                                if ("N".equals(itemType)){
                                    value = rs.getDouble(fields[j]);
                                    fieldHM.put("value", value);
                                }else{
                                    String codesetid = DataDictionary.getFieldItem(fields[j]).getCodesetid();
                                    if ("0".equals(codesetid)){
                                        fieldHM.put("value", "0".equals(rs.getString(fields[j]))?"":rs.getString(fields[j]));
                                    }else{
                                        String codeItemDesc = AdminCode.getCodeName(codesetid,rs.getString(fields[j]));
                                        fieldHM.put("value", codeItemDesc);
                                    }
                                }
                                values += value;
                                itemDataList.add(fieldHM);
                            }
                        }
                        HashMap itemDataHM = new HashMap();
                        itemDataHM.put("name", itemHM.get("name"));
                        itemDataHM.put("chart", itemHM.get("chart"));
                        itemDataHM.put("total", itemHM.get("total"));
                        itemDataHM.put("value", values);
                        itemDataHM.put("fieldList", itemDataList);
                        itemList.add(itemDataHM);
                    }
                    dataHM.put("items", itemList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取人员机构出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getPersonUint"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getPersonUint"));
        } finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(rows);
        }
        dataHM.put("id", map.get("id"));
        dataHM.put("schemes", map.get("schemes"));
        return dataHM;
    }

    @Override
    public Map getMySalaryData(UserView userView, String id) throws GeneralException {
        Map dataHM = new HashMap();
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        Map map = null;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        if (id == null || id.trim().length() == 0) {
            map = this.getMySalaryScheme(userView);
        } else {
            sql.append("select id,name,salary_table,salary_date,payable,taxable,incometax,realpay,zeroItemCtrl,year,items from gz_table_scheme where 1=1 and id=?");
            list.add(Integer.parseInt(PubFunc.decrypt(id)));
            try {
                rs = dao.search(sql.toString(), list);
                if (rs.next()) {
                    map = new HashMap();
                    String name = rs.getString("name");
                    String salary_table = rs.getString("salary_table");
                    String salary_date = rs.getString("salary_date");
                    String payable = rs.getString("payable");
                    String taxable = rs.getString("taxable");
                    String incometax = rs.getString("incometax");
                    String realpay = rs.getString("realpay");
                    String zeroItemCtrl = rs.getString("zeroItemCtrl");
                    int year = rs.getInt("year");
                    String items = rs.getString("items");
                    map.put("id", PubFunc.encrypt(String.valueOf(rs.getInt("id"))));
                    map.put("name", name);
                    map.put("salary_table", salary_table);
                    map.put("salary_date", salary_date);
                    map.put("payable", payable);
                    map.put("taxable", taxable);
                    map.put("incometax", incometax);
                    map.put("realpay", realpay);
                    map.put("zeroItemCtrl", zeroItemCtrl);
                    map.put("year", year);
                    map.put("items", items);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                //获取我的薪酬方案出错
                log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmysalaryscheme"));
                throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmysalaryscheme"));
            }
        }
        HashMap viewFieldHM = this.getViewField(userView,(String)map.get("salary_table"),"self",false);
        out:if(!viewFieldHM.containsKey(map.get("salary_date"))|| !viewFieldHM.containsKey(map.get("payable"))||!viewFieldHM.containsKey(map.get("taxable")) ||
                !viewFieldHM.containsKey(map.get("incometax")) ||!viewFieldHM.containsKey(map.get("realpay"))){
            if("none".equalsIgnoreCase(map.get("taxable").toString())||"none".equalsIgnoreCase(map.get("incometax").toString())||"none".equalsIgnoreCase(map.get("payable").toString())||"none".equalsIgnoreCase(map.get("realpay").toString())){
                break out;
            }
        	log.error("GZSchemeFieldConfigError");
        	throw new GeneralException("GZSchemeFiledConfigError");
        }
        dataHM.put("id", map.get("id"));
        list.clear();
        sql.setLength(0);
        this.isViewExist((String)map.get("salary_table"));
        //每年应发、实发金额
        sql.append("select * from (");
        if (Sql_switcher.dbflag == 1) {
            sql.append("select year(" + map.get("salary_date") + ") year");
            if(!"none".equalsIgnoreCase(map.get("payable").toString())){
                sql.append(",sum(" + map.get("payable") + ") payable");
            }
            if(!"none".equalsIgnoreCase(map.get("realpay").toString())){
                sql.append(",sum(" + map.get("realpay") + ") realpay");
            }
            sql.append(" from " + map.get("salary_table") + " ");
            sql.append("where nbase = ? and A0100 = ? and " + map.get("salary_date") + " is not null  group by year(" + map.get("salary_date") + ")");
        } else if (Sql_switcher.dbflag == 2) {
            sql.append("select to_char(" + map.get("salary_date") + ",'yyyy') year");
            if(!"none".equalsIgnoreCase(map.get("payable").toString())){
                sql.append(",sum(" + map.get("payable") + ") payable");
            }
            if(!"none".equalsIgnoreCase(map.get("realpay").toString())){
                sql.append(",sum(" + map.get("realpay") + ") realpay");
            }
            sql.append(" from " + map.get("salary_table") + " ");
            sql.append("where nbase = ? and A0100 = ? and " + map.get("salary_date") + " is not null  group by to_char(" + map.get("salary_date") + ",'yyyy')");
        }
        sql.append(") v order by year desc");
        list.add(userView.getDbname());
        list.add(userView.getA0100());
        ArrayList yearList = new ArrayList();
        int startYear = (Integer)map.get("year");
        int maxYear = 0;
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                HashMap yearHM = new HashMap();
                yearHM.put("year", rs.getString("year"));
                int year = Integer.parseInt(rs.getString("year"));
                if (startYear > 0 && year <startYear )
                	continue;

                if (maxYear < year)
                    maxYear = year;
                if(!"none".equalsIgnoreCase(map.get("payable").toString())){
                    yearHM.put("payableYear", rs.getDouble("payable"));
                }
                if(!"none".equalsIgnoreCase(map.get("realpay").toString())){
                    yearHM.put("realpayYear", rs.getDouble("realpay"));
                }
                yearList.add(yearHM);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取年工资出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.queryYear"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.queryYear"));
        }
        sql.setLength(0);
        list.clear();
        int index = 0;
        for (int i = 0; i < yearList.size(); i++) {
            String year = (String) ((HashMap) yearList.get(i)).get("year");
            if (maxYear == Integer.parseInt(year)) {
                index = i;
                break;
            }
        }
        //获取每年每月应发、实发工资
        ArrayList monthList = new ArrayList();
        if (Sql_switcher.dbflag == 1) {
            sql.append("select month(" + map.get("salary_date") + ") month");
            if (!"none".equalsIgnoreCase(map.get("payable").toString())) {
                sql.append("," + map.get("payable") + " payable");
            }
            if (!"none".equalsIgnoreCase(map.get("realpay").toString())) {
                sql.append("," + map.get("realpay") + " realpay");
            }
            sql.append(" from " + map.get("salary_table") + " where year(" + map.get("salary_date") + ")=? ");
        }
        else if (Sql_switcher.dbflag == 2) {
            sql.append("select to_char(" + map.get("salary_date") + ",'fmmm') month");
            if(!"none".equalsIgnoreCase(map.get("payable").toString())){
                sql.append("," + map.get("payable") + " payable");
            }
            if(!"none".equalsIgnoreCase(map.get("realpay").toString())){
                sql.append("," + map.get("realpay") + " realpay ");
            }
            sql.append(" from " + map.get("salary_table") + " where to_char(" + map.get("salary_date") + ",'yyyy')=? ");
        }
        sql.append(" and nbase = ? and A0100 = ? order by " + map.get("salary_date") + " asc");

        list.add(String.valueOf(maxYear));
        list.add(userView.getDbname());
        list.add(userView.getA0100());
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                HashMap monthHM = new HashMap();
                monthHM.put("month", rs.getString("month"));
                if(!"none".equalsIgnoreCase(map.get("payable").toString())){
                    monthHM.put("payable", rs.getDouble("payable"));
                }
                if(!"none".equalsIgnoreCase(map.get("realpay").toString())){
                    monthHM.put("realpay", rs.getDouble("realpay"));
                }
                monthList.add(monthHM);
            }
            if (yearList.size() > 0)
                ((HashMap) yearList.get(index)).put("monthList", monthList);
        } catch (SQLException e) {
            e.printStackTrace();
            //获取月工资出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmonthpay"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmonthpay"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        dataHM.put("yearList", yearList);
        dataHM.put("schemes", map.get("schemes"));
        return dataHM;
    }

    @Override
    public Map getMySalaryMonthData(UserView userView, String id, String year) throws GeneralException {
        Map dataHM = new HashMap();
        StringBuffer sql = new StringBuffer();
        ArrayList monthList = new ArrayList();
        HashMap viewHM = this.getMySalaryScheme(id, userView);
        HashMap salaryFieldHM = (HashMap) viewHM.get("salary_fields");
        ArrayList list = new ArrayList();
        if (Sql_switcher.dbflag == 1) {
            sql.append("select month(" + salaryFieldHM.get("salary_date") + ") month");
            if(!"none".equalsIgnoreCase(salaryFieldHM.get("payable").toString())){
                sql.append("," + salaryFieldHM.get("payable") + " payable");
            }
            if(!"none".equalsIgnoreCase(salaryFieldHM.get("realpay").toString())){
                sql.append("," + salaryFieldHM.get("realpay") + " realpay ");
            }
            sql.append(" from " + salaryFieldHM.get("salary_table") + " where nbase = ? and A0100 = ? and year(" + salaryFieldHM.get("salary_date") + ")=? ");
        }
        else if (Sql_switcher.dbflag == 2) {
            sql.append("select to_char(" + salaryFieldHM.get("salary_date") + ",'fmmm') month");
            if(!"none".equalsIgnoreCase(salaryFieldHM.get("payable").toString())){
                sql.append("," + salaryFieldHM.get("payable") + " payable");
            }
            if(!"none".equalsIgnoreCase(salaryFieldHM.get("realpay").toString())){
                sql.append("," + salaryFieldHM.get("realpay") + " realpay ");
            }
            sql.append(" from " + salaryFieldHM.get("salary_table") + " where nbase = ? and A0100 = ? and to_char(" + salaryFieldHM.get("salary_date") + ",'yyyy')=? ");
        }
        sql.append(" order by " + salaryFieldHM.get("salary_date"));
        list.add(userView.getDbname());
        list.add(userView.getA0100());
        list.add(year);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                HashMap monthHM = new HashMap();
                monthHM.put("month", rs.getString("month"));
                if(!"none".equalsIgnoreCase(salaryFieldHM.get("payable").toString())){
                    monthHM.put("payable", rs.getDouble("payable"));
                }
                if(!"none".equalsIgnoreCase(salaryFieldHM.get("realpay").toString())){
                    monthHM.put("realpay", rs.getDouble("realpay"));
                }
                monthList.add(monthHM);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取月工资出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmonthpay"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmonthpay"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        dataHM.put("monthList", monthList);
        return dataHM;
    }

    @Override
    public Map getMySalaryMonthInfo(UserView userView, String id, String year, String month) throws GeneralException {
        HashMap dataHM = new HashMap();
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();

        Map map = this.getMySalaryField(id);
        sql.append("select " + map.get("payable") + " as payable," + map.get("taxable") + "as taxable,");
        sql.append(map.get("incometax") + " as incometax," + map.get("realpay") + "as realpay,");
        JSONArray jsonArray = JSONArray.fromObject(map.get("items"));
        for (int i = 0; i < jsonArray.size(); i++) {
            Map itemHM = JSONObject.fromObject(jsonArray.get(i));
            String field = (String) itemHM.get("item");
            String[] fields = field.split(",");
            for (int j = 0; j < fields.length; j++) {
                sql.append(fields[j] + ",");
            }
            if (fields.length > 0 && i == jsonArray.size() - 1) {
                sql.setLength(sql.length() - 1);
            }
        }
        sql.append(" from " + map.get("salary_table") + " ");
        sql.append(" where 1=1 and ");
        sql.append("nbase = ? and A0100 = ? and ");
        if (Sql_switcher.dbflag == 1)
            sql.append("year(" + map.get("salary_date") + ")=? and month(" + map.get("salary_date") + ")=? ");
        else if (Sql_switcher.dbflag == 2)
            sql.append("to_char(" + map.get("salary_date") + ",'yyyy')=? and month(" + map.get("salary_date") + ",'yyyy')=? ");
        list.add(userView.getDbname());
        list.add(userView.getA0100());
        list.add(year);
        list.add(month);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
                //获取固定薪资指标
                dataHM.put("payable", rs.getDouble("payable"));
                dataHM.put("taxable", rs.getDouble("taxable"));
                dataHM.put("incometax", rs.getDouble("incometax"));
                dataHM.put("realpay", rs.getDouble("realpay"));
                dataHM.put("zeroItemCtrl", map.get("zeroItemCtrl"));

                //获取自定义工资结构
                ArrayList itemList = new ArrayList();
                for (int i = 0; i < jsonArray.size(); i++) {
                    Map itemHM = JSONObject.fromObject(jsonArray.get(i));
                    String field = (String) itemHM.get("item");
                    String[] fields = field.split(",");
                    ArrayList itemDataList = new ArrayList();
                    double values = 0;
                    for (int j = 0; j < fields.length; j++) {
                        HashMap fieldHM = new HashMap();
                        double value = rs.getDouble(fields[j]);
                        FieldItem fieldItem = DataDictionary.getFieldItem(fields[j]);
                        String fieldname = fieldItem.getItemdesc();
                        fieldHM.put("name", fieldname);
                        fieldHM.put("value", value);
                        values += value;
                        itemDataList.add(fieldHM);
                    }
                    HashMap itemDataHM = new HashMap();
                    itemDataHM.put("name", itemHM.get("name"));
                    itemDataHM.put("chart", itemHM.get("chart"));
                    itemDataHM.put("value", values);
                    itemDataHM.put("fieldList", itemDataList);
                    itemList.add(itemDataHM);
                }
                dataHM.put("items", itemList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取人员机构出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getPersonUint"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getPersonUint"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataHM;
    }

    @Override
    public Map getMySalaryYearInfo(UserView userView, String id, String year) throws GeneralException {
        Map dataHM = new HashMap();
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        Map salaryHM = this.getMySalaryField(id);
        if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString()) || !"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
            //获取年应发、 实发工资
            sql.append("select ");
            if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString())){
                sql.append("sum(" + salaryHM.get("payable") + ") payable,");
            }
            if(!"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
                sql.append("sum(" + salaryHM.get("realpay") + ") realpay,");
            }
            sql.deleteCharAt(sql.length()-1);
            sql.append(" from " + salaryHM.get("salary_table") + " where ");
            sql.append("nbase = ? and A0100 = ? and");
            if (Sql_switcher.dbflag == 1) {
                sql.append(" year(" + salaryHM.get("salary_date") + ")=?");
            }
            else if (Sql_switcher.dbflag == 2) {
                sql.append(" to_char(" + salaryHM.get("salary_date") + ",'yyyy')=? ");
            }
            list.add(userView.getDbname());
            list.add(userView.getA0100());
            list.add(year);
            try {
                rs = dao.search(sql.toString(), list);
                if (rs.next()) {
                    if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString())){
                        dataHM.put("payableYear", rs.getDouble("payable"));
                    }
                    if(!"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
                        dataHM.put("realpayYear", rs.getDouble("realpay"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                //年工资出错
                log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.queryYear"));
                throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.queryYear"));
            }
        }

        sql.setLength(0);
        list.clear();
        //获取年份区间
        if (Sql_switcher.dbflag == 1)
            sql.append("select * from (select year(" + salaryHM.get("salary_date") + ") year from " + salaryHM.get("salary_table") + " where nbase = ? and A0100 = ? and " + salaryHM.get("salary_date") + " is not null  group by year(" + salaryHM.get("salary_date") + ")) v order by year desc ");
        else if (Sql_switcher.dbflag == 2)
            sql.append("select * from (select to_char(" + salaryHM.get("salary_date") + ",'yyyy') year from " + salaryHM.get("salary_table") + " where nbase = ? and A0100 = ? and " + salaryHM.get("salary_date") + " is not null  group by to_char(" + salaryHM.get("salary_date") + ",'yyyy')) v order by year desc ");
        ArrayList yearList = new ArrayList();
        int startYear = (Integer)salaryHM.get("year");
        try {
            list.add(userView.getDbname());
            list.add(userView.getA0100());
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                HashMap yearHM = new HashMap();
                if(startYear > 0 &&  startYear > Integer.parseInt(rs.getString("year"))){
                	continue;
                }
                yearHM.put("year",rs.getString("year"));
                yearList.add(yearHM);
            }
            dataHM.put("yearList", yearList);
        } catch (SQLException e) {
            e.printStackTrace();
            //获取年份区间出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getyearinterval"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getyearinterval"));
        }

        sql.setLength(0);
        list.clear();
        //获取某年各月应发实发工资
        if (Sql_switcher.dbflag == 1) {
            sql.append("select month(" + salaryHM.get("salary_date") + ") month");
            if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString())){
                sql.append("," + salaryHM.get("payable") + " payable");
            }
            if(!"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
                sql.append("," + salaryHM.get("realpay") + " realpay ");
            }
            sql.append(" from " + salaryHM.get("salary_table") + " where nbase = ? and A0100 = ? and year(" + salaryHM.get("salary_date") + ")=? order by " + salaryHM.get("salary_date"));
        }
        else if (Sql_switcher.dbflag == 2) {
            sql.append("select to_char(" + salaryHM.get("salary_date") + ",'fmmm') month");
            if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString())){
                sql.append("," + salaryHM.get("payable") + " payable");
            }
            if(!"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
                sql.append("," + salaryHM.get("realpay") + " realpay ");
            }
            sql.append(" from " + salaryHM.get("salary_table") + " where nbase = ? and A0100 = ? and to_char(" + salaryHM.get("salary_date") + ",'yyyy')=? order by " + salaryHM.get("salary_date"));
        }
        list.add(userView.getDbname());
        list.add(userView.getA0100());
        list.add(year);
        ArrayList monthList = new ArrayList();
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                HashMap monthHM = new HashMap();
                monthHM.put("month", rs.getString("month"));
                if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString())){
                    monthHM.put("payable", rs.getDouble("payable"));
                }
                if(!"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
                    monthHM.put("realpay", rs.getDouble("realpay"));
                }
                monthList.add(monthHM);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取月工资出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmonthpay"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmonthpay"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        dataHM.put("monthList", monthList);
        return dataHM;
    }


    @Override
    public Map getMySalaryHistoryInfo(UserView userView, String id) throws GeneralException {
        Map dataHM = new HashMap();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        Map salaryHM = this.getMySalaryField(id);
        ArrayList list = new ArrayList();
        //应发、实发总金额
        if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString()) || !"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
            sql.append("select ");
            if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString())){
                sql.append("sum(" + salaryHM.get("payable") + ") payableSum,");
            }
            if(!"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
                sql.append("sum(" + salaryHM.get("realpay") + ") realpaySum,");
            }
            sql.deleteCharAt(sql.length()-1);
            sql.append(" from " + salaryHM.get("salary_table") + " where nbase = ? and A0100 = ?");
            list.add(userView.getDbname());
            list.add(userView.getA0100());
            try {
                rs = dao.search(sql.toString(), list);
                if (rs.next()) {
                    if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString())){
                        dataHM.put("payableSum", rs.getDouble("payableSum"));
                    }
                    if(!"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
                        dataHM.put("realpaySum", rs.getDouble("realpaySum"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                //获取应发、实发总工资出错
                log.error("");
                throw new GeneralException("");
            }
        }
        sql.setLength(0);
        list.clear();
        //每年应发、实发金额
        sql.append("select * from (");
        if (Sql_switcher.dbflag == 1) {
            sql.append("select year(" + salaryHM.get("salary_date") + ") year");
            if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString())){
                sql.append(",sum(" + salaryHM.get("payable") + ") payable");
            }
            if(!"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
                sql.append(",sum(" + salaryHM.get("realpay") + ") realpay ");
            }
            sql.append(" from " + salaryHM.get("salary_table") + " ");
            sql.append("where nbase = ? and A0100 = ? and " + salaryHM.get("salary_date") + " is not null  group by year(" + salaryHM.get("salary_date") + ")");
        } else if (Sql_switcher.dbflag == 2) {
            sql.append("select to_char(" + salaryHM.get("salary_date") + ",'yyyy') year");
            if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString())){
                sql.append(",sum(" + salaryHM.get("payable") + ") payable");
            }
            if(!"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
                sql.append(",sum(" + salaryHM.get("realpay") + ") realpay");
            }
            sql.append(" from " + salaryHM.get("salary_table") + " ");
            sql.append("where nbase = ? and A0100 = ? and " + salaryHM.get("salary_date") + " is not null  group by to_char(" + salaryHM.get("salary_date") + ",'yyyy')");
        }
        sql.append(") v order by year");
        list.add(userView.getDbname());
        list.add(userView.getA0100());
        ArrayList yearList = new ArrayList();
        int startYear = (Integer)salaryHM.get("year");
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                HashMap yearHM = new HashMap();
                int year = Integer.parseInt(rs.getString("year"));
                if(startYear > 0 &&  startYear > year){
                	continue;
                }
                yearHM.put("year", year);
                if(!"none".equalsIgnoreCase(salaryHM.get("payable").toString())){
                    yearHM.put("payableYear", rs.getDouble("payable"));
                }
                if(!"none".equalsIgnoreCase(salaryHM.get("realpay").toString())){
                    yearHM.put("realpayYear", rs.getDouble("realpay"));
                }
                yearList.add(yearHM);
            }
            dataHM.put("yearList", yearList);
        } catch (SQLException e) {
            e.printStackTrace();
            //获取年工资出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.queryYear"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.queryYear"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return dataHM;
    }

    /**
     * 获取我的薪资方案
     * @param userView
     * @return
     * @throws GeneralException
     */
    public HashMap getMySalaryScheme(UserView userView) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ArrayList roleList = userView.getRolelist();
    	if(userView.isSuper_admin()&&StringUtils.isNotEmpty(userView.getA0100())){
    		roleList = this.getSupAdminUserRolelist(userView);//如果是su并且关联自助用户  获取自助用户的角色信息
    	}
    	ArrayList list = new ArrayList();
    	String B0110 = "";
    	if(StringUtils.isNotEmpty(userView.getA0100())){
    		B0110 = userView.getUserOrgId();
    	}else{
    		B0110 = userView.getUnit_id();
    	}
    	B0110 = B0110 == null?  "":B0110;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        sql.append("select id,name,salary_table,salary_date,payable,taxable,incometax,realpay,B0110,zeroItemCtrl,year,items from gz_table_scheme ");
        sql.append("where ");
        if((userView.isSuper_admin() || StringUtils.isNotEmpty(userView.getA0100())) || "UN`".equalsIgnoreCase(B0110)){
        	sql.append(" 1 = 1 and ");
        }
        if(!B0110.toUpperCase().startsWith("UN")) {
        	sql.append("B0110 in (");
        	ArrayList orgList = new ArrayList();
        	getOrglist(orgList,B0110);
        	for (int i = 0; i <= orgList.size()-1; i++) {
        		sql.append("?,");
        		list.add("UN" + orgList.get(i));
        	}
        	if(orgList.size() > 0) {
        		sql.setLength(sql.length() - 1);
        	}else {
        		sql.append("''");
        	}
        	sql.append(") and ");
        }
        if (roleList.size() > 0) {//用户角色
            sql.append(" ( role_id is null or role_id='' or (");
            for (int i = 0; i < roleList.size(); i++) {
                sql.append("role_id like ? or ");
                list.add("%," + roleList.get(i) + ",%");
            }
            sql.setLength(sql.length() - 3);
            sql.append(") )");
        } else {
            if(userView.isSuper_admin()){//超级用户没有角色
                sql.append(" (1=1)");
            }else{
                sql.append(" (role_id is null or role_id='')");
            }

        }
        sql.append(" order by norder");
        HashMap map = new HashMap();
        HashMap orgHM = new HashMap();
        String orgId = null;
        try {
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
                String name = PubFunc.hireKeyWord_filter(rs.getString("name"));
                String salary_table = rs.getString("salary_table");
                String salary_date = rs.getString("salary_date");
                String payable = rs.getString("payable");
                String taxable = rs.getString("taxable");
                String incometax = rs.getString("incometax");
                String realpay = rs.getString("realpay");
                String zeroItemCtrl = rs.getString("zeroItemCtrl");
                String items = rs.getString("items");
                int year = rs.getInt("year");
                map.put("id", PubFunc.encrypt(String.valueOf(rs.getInt("id"))));
                map.put("name", name);
                map.put("salary_table", salary_table);
                map.put("salary_date", salary_date);
                map.put("payable", payable);
                map.put("taxable", taxable);
                map.put("incometax", incometax);
                map.put("realpay", realpay);
                map.put("zeroItemCtrl", zeroItemCtrl);
                map.put("items", items);
                map.put("year", year);
                ArrayList schemeList = new ArrayList();
                HashMap schemeHM = new HashMap();
                schemeHM.put("id", PubFunc.encrypt(String.valueOf(rs.getInt("id"))));
                schemeHM.put("name", name);
                schemeList.add(schemeHM);
                orgId = rs.getString("B0110");
                orgHM.put(rs.getString("B0110"), schemeList);
            } else {
                //该员工未配置薪酬方案
                log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.personnotconfigsalaryscheme"));
                throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.personnotconfigsalaryscheme"));
            }
//			String orgId = null;
            while (rs.next()) {
//                if (!orgHM.containsKey(rs.getString("B0110")))
//                    break;
//				orgId = rs.getString("B0110");
                HashMap schemeHM = new HashMap();
                schemeHM.put("id", PubFunc.encrypt(String.valueOf(rs.getInt("id"))));
                schemeHM.put("name", rs.getString("name"));
                ((ArrayList) orgHM.get(orgId)).add(schemeHM);
            }
            map.put("schemes", orgHM.get(orgId));
        } catch (SQLException e) {
            e.printStackTrace();
            //获取我的薪酬方案出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmysalaryscheme"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmysalaryscheme"));
        }
        return map;
    }

    /**
     *  获取当前机构和上级一直到顶级机构的集合
     * @param b0110
     * @return
     */
    private void getOrglist(ArrayList orgList,String b0110){
    	CodeItem codeItem = AdminCode.getCode("UN", b0110);
    	if(codeItem != null) {
    		orgList.add(b0110);
    		if(StringUtils.equalsIgnoreCase(codeItem.getCodeitem(), codeItem.getPcodeitem())) {
    			return;
    		}
    		getOrglist(orgList,codeItem.getPcodeitem());
    	}else {
    		codeItem = AdminCode.getCode("UM", b0110);
    		if(codeItem != null) {
    			getOrglist(orgList,codeItem.getPcodeitem());
    		}
    	}
    }


    /**
     * 获取角色名称
     *
     * @param role_id 角色id
     * @return 角色名称
     * @throws GeneralException
     */
    private String getRoleName(String role_id) throws GeneralException {
        StringBuffer roleName = new StringBuffer();
        Map<String, String> roleData = new HashMap<String, String>();
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        String[] role_ids = role_id.split(",");
        sql.append("select role_id,role_name from t_sys_role where valid=1 and ");
        sql.append("role_id in (");
        for (int i = 0; i < role_ids.length; i++) {
            sql.append("?");
            if (i < role_ids.length - 1) {
                sql.append(",");
            }
            list.add(role_ids[i]);
        }
        //if(role_id.length() > 0){
        //	sql.setLength(sql.length()-1);
        //}
        sql.append(")");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                //roleName.append(rs.getString("role_name")+",");
                String roleId = rs.getString("role_id");
                String rolename = rs.getString("role_name");
                roleData.put(roleId, rolename);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取角色名称出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getrolename"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getrolename"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        for (int i = 0; i < role_ids.length; i++) {
            if (StringUtils.isEmpty(role_ids[i])) {
                continue;
            }
            roleName.append(roleData.get(role_ids[i]) + ",");
        }
        if (roleName.length() > 0)
            roleName.setLength(roleName.length() - 1);
        return roleName.toString();
    }


    private ArrayList listRole(ArrayList roleidList) throws GeneralException {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        ArrayList idList = new ArrayList();
        sql.append("select role_id,role_name from t_sys_role where valid=1 ");
        sql.append("and role_id in (");
        for (int i = 0; i < roleidList.size(); i++) {
            sql.append("?,");
            idList.add(roleidList.get(i));
        }
        if (roleidList.size() < 1) {
            sql.append("?");
            idList.add("");
        }
        if (roleidList.size() > 0)
            sql.setLength(sql.length() - 1);
        sql.append(")");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), idList);
            while (rs.next()) {
                HashMap map = new HashMap();
                map.put("role_id", rs.getString("role_id"));
                map.put("role_name", rs.getString("role_name"));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取角色信息出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getroleinfo"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getroleinfo"));
        }
        return list;
    }

    private int getMaxSchemeId() throws GeneralException {
        String sql = "select MAX(id) max from gz_table_scheme";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                Object max = rs.getObject("max");
                if (max == null || "null".equals(max))
                    return 1;
                if (Sql_switcher.dbflag == 1)
                    return (Integer) max + 1;
                else if (Sql_switcher.dbflag == 2)
                    return ((BigDecimal) max).intValue() + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取最大薪酬方案编号出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmaxsalaryschemeid"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmaxsalaryschemeid"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return 1;
    }

    /**
     * 获取业务字典表displayorder排序字段最大值
     *
     * @return
     * @throws GeneralException
     */
    private int getSalaryViewDispalyOrder() throws GeneralException {
        String sql = "select Max(displayorder) as max from t_hr_busitable";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                Object max = rs.getObject("max");
                if (max == null || "null".equals(max))
                    return 1;
                if(max instanceof BigDecimal) {
                	 return ((BigDecimal) max).intValue();
                }else {
                	 return (Integer) max;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取最大视图排序出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getMaxVieworder"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getMaxVieworder"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return 1;
    }

    /**
     * 生成视图对应的sql语句
     *
     * @param nbases     人员库
     * @param fieldsetid 主数据来源子集编号
     * @param itemHM     子集和指标集合
     * @param userView
     * @return
     */
    private String getSalaryViewSQL(String nbases, String fieldsetid, HashMap itemHM, UserView userView) throws GeneralException, SQLException {
        StringBuffer sql = new StringBuffer();
        ArrayList fieldsetList = (ArrayList) itemHM.get("fieldsets");
        ArrayList fielditemList = (ArrayList) itemHM.get("fielditems");
        String[] nbase = nbases.split(",");
        for (int i = 0; i < nbase.length; i++) {
            // 先拼上视图所需要的列
            sql.append("select ");
            //sql server 库
            if (Sql_switcher.searchDbServer() == 1) {
                sql.append(" '" + nbase[i] + "' nbase,cast(month(" + nbase[i] + fieldsetid + "." + fieldsetid + "z0) AS varchar(2)) + '月' '月份',");
            } else if (Sql_switcher.searchDbServer() == 2) {
                // oracle 库
                sql.append(" '" + nbase[i] + "' nbase,to_char(" + nbase[i] + fieldsetid + "." + fieldsetid + "z0,'fmmm') || '月' 月份,");
            }
            sql.append(nbase[i] + fieldsetid + "." + fieldsetid + "z0,");
            sql.append(nbase[i] + fieldsetid + ".A0100,");
            sql.append(nbase[i] + fieldsetid + ".i9999 i9999,");
            // 遍历视图所需要的字段
            for (int j = 0; j < fielditemList.size(); j++) {
                HashMap fielditem = (HashMap) fielditemList.get(j);
                // 如果选择了年月标识指标则不拼因为上面已经拼了年月标识指标
                if (((String)fielditem.get("itemid")).toUpperCase().equals(fieldsetid+"Z0")){
                    continue;
                }
                sql.append(nbase[i] + fielditem.get("fieldsetid") + "." + ((String) fielditem.get("itemid")).toUpperCase() +" "+ ((String) fielditem.get("itemid")).toUpperCase() + ",");
            }
            sql.setLength(sql.length()-1);
            sql.append(" from");
            // 再去拼多表连接后显示的字段
            //sql server 库
            if (Sql_switcher.searchDbServer() == 1) {
                sql.append("(select '" + nbase[i] + "' nbase,cast(month(" + nbase[i] + fieldsetid + "." + fieldsetid + "z0) AS varchar(2)) + '月' '月份',");
            } else if (Sql_switcher.searchDbServer() == 2) {
                // oracle 库
                sql.append("(select '" + nbase[i] + "' nbase,to_char(" + nbase[i] + fieldsetid + "." + fieldsetid + "z0,'fmmm') || '月' 月份,");
            }
            sql.append(nbase[i] + fieldsetid + "." + fieldsetid + "z0,");
            sql.append(nbase[i] + fieldsetid + ".A0100,");
            sql.append("min(" + nbase[i] + fieldsetid + ".i9999) i9999,");

            // 通过标志变量（子集拼接完事之后将子集名称赋值给标志位），判断当前遍历到哪个子集
            String flag = fieldsetid;
            //判断子集是否是主表，先拼主表
            for (int j = 0; j < fieldsetList.size(); j++) {
                if (fieldsetList.get(j).equals(flag)) {
                    // 遍历选中的指标项将主表的指标拼到sql中
                    for (int k = 0; k < fielditemList.size(); k++) {
                        HashMap fielditem = (HashMap) fielditemList.get(k);
                        String itemtype = (String) fielditem.get("itemtype");
                        String resultSql = "";
                        if ((fielditem.get("fieldsetid")).equals(flag)) {
                            // 如果选择了年月标识指标则不拼因为上面已经拼了年月标识指标
                            if (((String)fielditem.get("itemid")).toUpperCase().equals(fieldsetid+"Z0")){
                                continue;
                            }
                            // 判断当前主集指标是否是数值型，如果是则进行计算公式转化
                            if ("N".equalsIgnoreCase(itemtype)){
                                ArrayList fieldList = (ArrayList) this.searchNumberFieldItem(((String) fielditem.get("itemid")).substring(0, 3), "check");
                                String calcformat = (String) fielditem.get("calcformat");
                                String calcformatSql = this.formulaReplaceSql(userView, fieldList, itemtype, calcformat);
                                // 格式化为视图所需要的格式
                                resultSql = this.formatFormulaSql(nbase[i],((String) fielditem.get("itemid")).substring(0,3),calcformatSql);
                            }
                            //sql server 库
                            if (Sql_switcher.searchDbServer() == 1) {
                                if (StringUtils.contains((String) fielditem.get("itemid"), "z1") || !"N".equals(itemtype.toUpperCase())) {
                                    // 只对a58z0的日期型指标，进行isnull
                                    if ("D".equals(itemtype.toUpperCase())&&!StringUtils.contains((String) fielditem.get("itemid"), "z0")){
                                        sql.append("max(" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ") " + fielditem.get("itemid") + ",");
                                    }else {
                                        sql.append("max(isnull(" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ",0)) " + fielditem.get("itemid") + ",");
                                    }
                                } else {
                                    if ("0".equals(resultSql.trim())){
                                        sql.append("sum(isnull(" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ",0)) " + fielditem.get("itemid") + ",");
                                    }else{
                                        sql.append("sum(" + resultSql + ") " + fielditem.get("itemid") + ",");
                                    }
                                }
                            } else if (Sql_switcher.searchDbServer() == 2) {
                                // oracle 库
                                if (StringUtils.contains((String) fielditem.get("itemid"), "z1") || !"N".equals(itemtype.toUpperCase())) {
                                    // 只对a58z0的日期型指标，进行isnull
                                    if ("D".equals(itemtype.toUpperCase())&&!StringUtils.contains((String) fielditem.get("itemid"), "z0")){
                                        sql.append("max(" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ") " + fielditem.get("itemid") + ",");
                                    }else {
                                        sql.append("max(nvl(" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ",0)) " + fielditem.get("itemid") + ",");
                                    }
                                } else {
                                    if ("0".equals(resultSql.trim())){
                                        sql.append("sum(nvl(" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ",0)) " + fielditem.get("itemid") + ",");
                                    }else{
                                        sql.append("sum(" + resultSql + ") " + fielditem.get("itemid") + ",");
                                    }

                                }
                            }
                        }
                    }
                    // 判断是否选择了指标如果选择了，则将最后一个逗号减掉
                    if (fielditemList.size() > 0) {
                        sql.setLength(sql.length() - 1);
                    }
                    sql.append(" from " + nbase[i] + fieldsetid + " group by " + fieldsetid + "z0, A0100) "+ nbase[i] + fieldsetid );
                }
            }
            //判断是否选中了多个子集
            if (fieldsetList.size()>1){
                // 重新遍历fieldsetList拼子集
                for (int j = 0; j < fieldsetList.size(); j++) {
                    //因为上面已经拼完主表所以跳过主表直接拼其他表
                    if (fieldsetList.get(j).equals(flag)) {
                        continue;
                    }
                    sql.append(" left join ( select ");
                    sql.append(nbase[i] + fieldsetList.get(j) + "." + fieldsetList.get(j) + "z0,");
                    sql.append(nbase[i] + fieldsetList.get(j) + ".A0100 a0100,");
                    // 遍历指标
                    for (int k = 0; k < fielditemList.size(); k++) {
                        //过滤掉主表中的指标不拼因为上面已经拼完了
                        // String fielditem = (String) fielditemList.get(k);
                        HashMap fielditem = (HashMap) fielditemList.get(k);
                        String itemtype = (String) fielditem.get("itemtype");
                        String resultSql = "";
                        // 过去掉主表和不是当前表的指标
                        if ((fielditem.get("fieldsetid")).equals(flag)||!(fielditem.get("fieldsetid")).equals(fieldsetList.get(j))) {
                            continue;
                        }
                        // 如果选择了年月标识指标则不拼因为上面已经拼了年月标识指标
                        if (((String)fielditem.get("itemid")).toUpperCase().equals(fieldsetList.get(j)+"Z0")){
                            continue;
                        }
                        // 判断当前主集指标是否是数值型，如果是则进行计算公式转化
                        if ("N".equalsIgnoreCase(itemtype)){
                            ArrayList fieldList = (ArrayList) this.searchNumberFieldItem(((String) fielditem.get("itemid")).substring(0, 3), "check");
                            String calcformat = (String) fielditem.get("calcformat");
                            String calcformatSql = this.formulaReplaceSql(userView, fieldList, itemtype, calcformat);
                            // 格式化为视图所需要的格式
                            resultSql = this.formatFormulaSql(nbase[i],((String) fielditem.get("itemid")).substring(0,3),calcformatSql);
                        }
                        if (Sql_switcher.searchDbServer() == 1) {//sql server 库
                            if (StringUtils.contains((String) fielditem.get("itemid"), "z1") || !"N".equals(itemtype.toUpperCase())) {
                                // 只对a58z0的日期型指标，进行isnull
                                if ("D".equals(itemtype.toUpperCase())&&!StringUtils.contains((String) fielditem.get("itemid"), "z0")){
                                    sql.append("max(" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ") " + fielditem.get("itemid") + ",");
                                }else {
                                    sql.append("max(isnull(" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ",0)) " + fielditem.get("itemid") + ",");
                                }
                            } else {
                                if ("0".equals(resultSql.trim())){
                                    sql.append("sum(isnull(" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ",0)) " + fielditem.get("itemid") + ",");
                                }else{
                                    sql.append("sum(" + resultSql + ") " + fielditem.get("itemid") + ",");
                                }
                            }
                        } else if (Sql_switcher.searchDbServer() == 2) {// oracle 库
                            if (StringUtils.contains((String) fielditem.get("itemid"), "z1") || !"N".equals(itemtype.toUpperCase())) {
                                // 只对a58z0的日期型指标，进行isnull
                                if ("D".equals(itemtype.toUpperCase())&&!StringUtils.contains((String) fielditem.get("itemid"), "z0")){
                                    sql.append("max(" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ") " + fielditem.get("itemid") + ",");
                                }else {
                                    sql.append("max(nvl (" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ",0)) " + fielditem.get("itemid") + ",");
                                }
                            } else {
                                if ("0".equals(resultSql.trim())){
                                    sql.append("sum(nvl(" + nbase[i] + fielditem.get("fieldsetid") + "." + fielditem.get("itemid") + ",0)) " + fielditem.get("itemid") + ",");
                                }else{
                                    sql.append("sum(" + resultSql + ") " + fielditem.get("itemid") + ",");
                                }

                            }
                        }

                    }
                    // 去掉多余分号
                    sql.setLength(sql.length() - 1);
                    sql.append(" from " + nbase[i] + fieldsetList.get(j) + " group by " + fieldsetList.get(j) + "z0, A0100)"+ nbase[i] + fieldsetList.get(j) );
                    sql.append(" on " + nbase[i] + fieldsetid + "." + fieldsetid + "z0=" + nbase[i] + fieldsetList.get(j) + "." + fieldsetList.get(j) + "z0 and " + nbase[i] + fieldsetid + ".A0100=" + nbase[i] + fieldsetList.get(j) + ".a0100 ");

                }
            }
            sql.append(" union all ");
        }
        sql.setLength(sql.length() - 10);
        return sql.toString();
    }

    /**
     * 创建视图失败，清空视图信息
     *
     * @param fieldsetid
     * @throws GeneralException
     */
    private void deleteSalaryViewInfo(String fieldsetid) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        sql.append("delete t_hr_busitable where fieldsetid=?");
        list.add(fieldsetid);
        try {
            dao.delete(sql.toString(), list);
        } catch (SQLException e) {
            e.printStackTrace();
            //删除创建失败的视图表出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.deletefailview"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.deletefailview"));
        }
        sql.setLength(0);

        sql.append("delete t_hr_busifield where fieldsetid=?");
        try {
            dao.delete(sql.toString(), list);
        } catch (SQLException e) {
            e.printStackTrace();
            //删除创建失败的视图指标出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.deletefailviewfiled"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.deletefailviewfiled"));
        }
    }

    @Override
    public String checkSalaryViewTable(String salary_table_name, String salary_table) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("select 1 from t_hr_busitable where id=50 ");
        if(StringUtils.isNotBlank(salary_table_name)) {
        	sql.append(" and customdesc=?");
        	list.add(salary_table_name);
        }
        if(StringUtils.isNotBlank(salary_table)) {
        	sql.append(" and fieldsetid=?");
        	list.add(salary_table);
        }
        RowSet rs = null;
        try {
			rs = dao.search(sql.toString(),list);
			if(rs.next()) {
				if(StringUtils.isNotBlank(salary_table_name)) {
		        	String msg = ResourceFactory.getProperty("gz.mysalary.scheme.error.viewNamepresence");
		        	msg = msg.replace("{1}", salary_table_name);
		        	//视图名称已存在，请重新输入视图表名
		        	log.error(msg);
		        	throw new GeneralException(msg);
		        }
		        if(StringUtils.isNotBlank(salary_table)) {
		        	String msg = ResourceFactory.getProperty("gz.mysalary.scheme.error.viewTablepresence");
		        	msg = msg.replace("{1}", salary_table);
		        	//视图表名已存在，请重新输入视图表名
		        	log.error(msg);
		        	throw new GeneralException(msg);
		        }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
        return "success";
    }

    @Override
    public HashMap getSalaryViewParam(UserView userView) throws GeneralException {
        StringBuffer dbpriv = userView.getDbpriv();
        String[] nbases = null;
        if (!userView.isSuper_admin())
            nbases = dbpriv.toString().split(",");
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        ArrayList dbList = new ArrayList();
        sql.append("select dbname,pre from dbname where 1=1 ");
        if(!userView.isSuper_admin() && (nbases == null || nbases.length == 0)) {
        	sql.append("and 1 = 2  ");
        }
        if (nbases != null && nbases.length > 0) {
            sql.append("and pre in (");
            for (int i = 0; i < nbases.length; i++) {
                sql.append("?,");
                list.add(nbases[i]);
            }
            sql.setLength(sql.length() - 1);
            sql.append(")");
        }
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
                HashMap map = new HashMap();
                map.put("dbname", rs.getString("dbname"));
                map.put("nbase", rs.getString("pre"));
                dbList.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取人员库信息出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getdbnameinfo"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getdbnameinfo"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        ArrayList fieldsetlist = userView.getPrivFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);//获取权限子集
        ArrayList fieldSetDataList = new ArrayList();
        for (int i = 0; i < fieldsetlist.size(); i++) {
            HashMap fieldsetMap = new HashMap();
            FieldSet fieldset = (FieldSet) fieldsetlist.get(i);
            if (!"1".equalsIgnoreCase(fieldset.getChangeflag()))
                continue;
            if ("0".equalsIgnoreCase(fieldset.getUseflag()))
                continue;
            fieldsetMap.put("fieldsetid", fieldset.getFieldsetid());
            fieldsetMap.put("fieldsetdesc", fieldset.getCustomdesc());
            fieldSetDataList.add(fieldsetMap);
        }
        HashMap dataHM = new HashMap();
        dataHM.put("nbases", dbList);
        dataHM.put("fieldsets", fieldSetDataList);
        return dataHM;
    }

    @Override
    public HashMap saveSalaryView(HashMap data, String type, UserView userView) throws GeneralException, SQLException {
    	ContentDAO dao = new ContentDAO(this.conn);
        HashMap map = new HashMap();
        HashMap itemMap = new HashMap();
        ArrayList dateItemList = new ArrayList();
        ArrayList numItemList = new ArrayList();
        ArrayList characterAndNumItemList = new ArrayList();
        // 添加空选项
        HashMap emptyFieldHM = new HashMap();
        emptyFieldHM.put("itemid","none");
        emptyFieldHM.put("itemdesc","");
        numItemList.add(emptyFieldHM);
        RecordVo tableVo = new RecordVo("t_hr_busitable");
        tableVo.setString("fieldsetid", (String) data.get("salary_table"));
        tableVo.setString("id", "50");
        if("update".equalsIgnoreCase(type)){//修改视图   删除视图同时删除配置视图信息和视图指标
        	try {
        		dao.delete("delete t_hr_busitable where fieldsetid=? and id=50 ", Arrays.asList((String) data.get("salary_table")));
        		dao.delete("delete t_hr_busifield where fieldsetid=?", Arrays.asList((String) data.get("salary_table")));
        		dao.update("drop view "+(String) data.get("salary_table"));
        	} catch (SQLException e1) {
        		//修改视图，需要先删，在建
        	}
        }

        tableVo.setString("fieldsetdesc", (String) data.get("salary_table_name"));
        tableVo.setString("customdesc", (String) data.get("salary_table_name"));
        tableVo.setString("useflag", "1");
        tableVo.setInt("displayorder", this.getSalaryViewDispalyOrder());

        StringBuffer classpre = new StringBuffer();
        classpre.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>  <view><sql>");
        Object items = data.get("items");
        HashMap itemHM = null;
        HashMap fielditemsHM = null;
        if (!(items instanceof HashMap)){
            itemHM = PubFunc.DynaBean2Map((DynaBean) items);
        }
        // 转换类型,前台传的是map
        ArrayList fielditemsList = (ArrayList) itemHM.get("fielditems");
        for(int i = 0; i< fielditemsList.size();i++){
            if (!(fielditemsList.get(i) instanceof HashMap)){
                fielditemsHM = PubFunc.DynaBean2Map((DynaBean) fielditemsList.get(i));

                fielditemsList.set(i,fielditemsHM);
            }
        }

        String viewSql = this.getSalaryViewSQL((String) data.get("nbase"), (String) data.get("fieldsetid"), itemHM, userView);
        classpre.append(viewSql);
        classpre.append("</sql>");
        classpre.append("<nbases>");
        classpre.append((String) data.get("nbase"));
        classpre.append("</nbases>");
        // 将视图主表存放在xml中，便于编辑视图时回显视图主表
        classpre.append("<fieldsetid>");
        classpre.append((String) data.get("fieldsetid"));
        classpre.append("</fieldsetid></view>");
        tableVo.setString("classpre", classpre.toString());//拼视图sql语句
        tableVo.setString("ownflag", "0");
        FieldSet fieldset = new FieldSet((String) ((String) data.get("salary_table")).toUpperCase());
        //fieldset.setFieldsetid(cDX.toUpperCase());
        fieldset.setFieldsetdesc((String) data.get("salary_table_name"));
        fieldset.setCustomdesc((String) data.get("salary_table_name"));
        fieldset.setUseflag("1");
        DataDictionary.addFieldSet(((String) data.get("salary_table")).toUpperCase(), fieldset);
        int count = dao.addValueObject(tableVo);
        if (count < 0) {
            //新增视图信息出错
            this.deleteSalaryViewInfo((String) data.get("salary_table"));
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.addviewinfo"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.addviewinfo"));
        }
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        RowSet rs = null;
        sql.append("select itemid,useflag,itemtype,itemdesc,itemlength,decimalwidth,codesetid,displaywidth,state,displayid from fielditem where 1=1 ");
        ArrayList fieldList = (ArrayList) itemHM.get("fielditems");

        sql.append("and itemid in(");

        if (fieldList.size() > 0) {
            for (int i = 0; i < fieldList.size(); i++) {
                // String field = (String) fieldList.get(i);
                HashMap field = (HashMap) fieldList.get(i);
                sql.append("?,");
                String itemid = (String) field.get("itemid");
                list.add(itemid.toUpperCase());
            }
            sql.setLength(sql.length() - 1);
            sql.append(")");
            try {
                rs = dao.search(sql.toString(), list);
                while (rs.next()) {
                    FieldItem fieldItem = new FieldItem((String) data.get("salary_table"), rs.getString("itemid"));
                    fieldItem.setItemtype(rs.getString("itemtype"));
                    // 修改原来的指标名称获取的逻辑，因为视图表的指标可编辑，这里将指标名称换为修改后的
                    for (int i = 0; i < fielditemsList.size(); i++) {
                        String itemid = (String) ((HashMap) fielditemsList.get(i)).get("itemid");
                        if (rs.getString("itemid").equalsIgnoreCase(itemid)){
                            String itemdesc = (String) ((HashMap) fielditemsList.get(i)).get("itemdesc");
                            fieldItem.setItemdesc(itemdesc);
                            break;
                        }
                        else{
                            fieldItem.setItemdesc(rs.getString("itemdesc"));
                        }
                    }

                    // fieldItem.setItemdesc(rs.getString("itemdesc"));
                    DataDictionary.addFieldItem((String) ((String) data.get("salary_table")).toUpperCase(), fieldItem, 0);
                    RecordVo fieldVo = new RecordVo("t_hr_busifield");
                    fieldVo.setString("fieldsetid", (String) data.get("salary_table"));
                    fieldVo.setString("itemid", rs.getString("itemid"));
                    fieldVo.setString("displayid", rs.getString("displayid"));
                    fieldVo.setString("itemtype", rs.getString("itemtype"));
                    // 这里修改向业务指标信息表(t_hr_busifield)中添加的指标名称为编辑后的
                    for (int i = 0; i < fielditemsList.size(); i++) {
                        String itemid = (String) ((HashMap) fielditemsList.get(i)).get("itemid");
                        if (rs.getString("itemid").equalsIgnoreCase(itemid)){
                            String itemdesc = (String) ((HashMap) fielditemsList.get(i)).get("itemdesc");
                            fieldVo.setString("itemdesc", itemdesc);
                            break;
                        }
                        else{
                            fieldVo.setString("itemdesc", rs.getString("itemdesc"));
                        }
                    }
                    // fieldVo.setString("itemdesc", rs.getString("itemdesc"));
                    fieldVo.setString("itemlength", rs.getString("itemlength"));
                    fieldVo.setString("decimalwidth", rs.getString("decimalwidth"));
                    fieldVo.setString("codesetid", rs.getString("codesetid"));
                    fieldVo.setString("displaywidth", rs.getString("displaywidth"));
                    fieldVo.setString("state", rs.getString("state"));
                    fieldVo.setString("useflag", rs.getString("useflag"));
                    fieldVo.setString("keyflag", "0");
                    fieldVo.setString("codeflag", "0");
                    fieldVo.setString("ownflag", "0");
                    // 将计算公式保存到expression字段
                    for (int i = 0; i < fielditemsList.size(); i++) {
                        String itemid = (String) ((HashMap) fielditemsList.get(i)).get("itemid");
                        if (rs.getString("itemid").equalsIgnoreCase(itemid)&& "N".equals(rs.getString("itemtype"))){
                            String calcformat = (String) ((HashMap) fielditemsList.get(i)).get("calcformat");
                            StringBuffer calcformatBuffer = new StringBuffer();
                            calcformatBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\n" +
                                    "<params>\n<calcformula>\n");
                            calcformatBuffer.append(calcformat);
                            calcformatBuffer.append("</calcformula>\n</params>\n");
                            fieldVo.setString("expression", calcformatBuffer.toString());
                            break;
                        }
                    }
                    count = dao.addValueObject(fieldVo);
                    if (count <= 0) {
                        //保存视图指标出错
                        log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.saveviewfield"));
                        throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.saveviewfield"));
                    }
                    HashMap fieldHM = new HashMap();
                    fieldHM.put("itemid", rs.getString("itemid"));
                    //fieldHM.put("itemtype", rs.getString("itemtype"));
                    // 这里修改向前台传输的指标名称为编辑后的
                    for (int i = 0; i < fielditemsList.size(); i++) {
                        String itemid = (String) ((HashMap) fielditemsList.get(i)).get("itemid");
                        if (rs.getString("itemid").equalsIgnoreCase(itemid)){
                            String itemdesc = (String) ((HashMap) fielditemsList.get(i)).get("itemdesc");
                            fieldHM.put("itemdesc", itemdesc);
                            break;
                        }
                        else{
                            fieldHM.put("itemdesc", rs.getString("itemdesc"));
                        }
                    }
                    //排除次数和i9999指标
                    if ("D".equalsIgnoreCase(rs.getString("itemtype"))&&((String) data.get("fieldsetid")).equalsIgnoreCase(rs.getString("itemid").substring(0,3))) {
                        dateItemList.add(fieldHM);
                    }
                    else if ("N".equalsIgnoreCase(rs.getString("itemtype")) && !StringUtils.contains(rs.getString("itemid").toUpperCase(), "Z1") && !StringUtils.equalsIgnoreCase(rs.getString("itemid").toUpperCase(), "I9999")){
                        numItemList.add(fieldHM);
                        characterAndNumItemList.add(fieldHM);
                    }else if ("A".equalsIgnoreCase(rs.getString("itemtype"))){
                        characterAndNumItemList.add(fieldHM);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                this.deleteSalaryViewInfo((String) data.get("salary_table"));
                //获取子集指标出错
                log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getfield"));
                throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getfield"));
            }
        }

        try {
            dao.update("create view " + (String) data.get("salary_table") + " as " + viewSql);
        } catch (SQLException e) {
            e.printStackTrace();
            //创建视图出错
            this.deleteSalaryViewInfo((String) data.get("salary_table"));
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.createview"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.createview"));
        }
        map.put("salary_table", (String) data.get("salary_table"));
        map.put("salary_table_name", (String) data.get("salary_table_name"));
        itemMap.put("D", dateItemList);
        itemMap.put("N", numItemList);
        itemMap.put("A", characterAndNumItemList);
        map.put("items", itemMap);
        return map;
    }

    private Map getMySalaryField(String id) throws GeneralException {
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        Map map = null;
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        sql.append("select id,name,salary_table,salary_date,payable,taxable,incometax,realpay,zeroItemCtrl,year,items from gz_table_scheme where 1=1 and id=?");
        list.add(Integer.parseInt(PubFunc.decrypt(id)));
        try {
            rs = dao.search(sql.toString(), list);
            if (rs.next()) {
                map = new HashMap();
                String name = rs.getString("name");
                String salary_table = rs.getString("salary_table");
                String salary_date = rs.getString("salary_date");
                String payable = rs.getString("payable");
                String taxable = rs.getString("taxable");
                String incometax = rs.getString("incometax");
                String realpay = rs.getString("realpay");
                String zeroItemCtrl = rs.getString("zeroItemCtrl");
                int year = rs.getInt("year");
                String items = rs.getString("items");
                map.put("id", PubFunc.encrypt(String.valueOf(rs.getInt("id"))));
                map.put("name", name);
                map.put("salary_table", salary_table);
                map.put("salary_date", salary_date);
                map.put("payable", payable);
                map.put("taxable", taxable);
                map.put("incometax", incometax);
                map.put("realpay", realpay);
                map.put("zeroItemCtrl", zeroItemCtrl);
                map.put("year", year);
                map.put("items", items);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取我的薪酬方案出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmysalaryscheme"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.getmysalaryscheme"));
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }

    public HashMap saveMySalaryScheme(Map data) throws GeneralException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HashMap getMySalaryInfo(UserView userView,String nbase,String a0100,String year, String schemeId,String state) throws GeneralException {
        HashMap returnData = new HashMap();
        Map fieldsMap = new HashMap();
        Map salaryScheme = null;
        String items = "";
        //获取人员所在机构对应的薪酬方案
        if (StringUtils.isEmpty(schemeId)) {//说明是初始化
            salaryScheme = this.getMySalaryScheme(userView);
            schemeId = PubFunc.decrypt((String) salaryScheme.get("id"));
            items = (String) salaryScheme.get("items");//薪资表自定义结构
        } else {
            Map schemeData = this.getMySalaryScheme(schemeId, userView);
            salaryScheme = (Map) schemeData.get("salary_fields");
            items = (String) schemeData.get("salary_items").toString();
            schemeId = PubFunc.decrypt(schemeId);
        }
        String schemeName = (String) salaryScheme.get("name");
        String salaryTable = (String) salaryScheme.get("salary_table");
        String dateField = (String) salaryScheme.get("salary_date");
        String zeroItemCtrl = (String)salaryScheme.get("zeroItemCtrl");
        HashMap viewFieldHM = this.getViewField(userView,salaryTable,state,false);
        this.isViewExist(salaryTable);
        if(!viewFieldHM.containsKey(salaryScheme.get("salary_date")) /*||!viewFieldHM.containsKey(salaryScheme.get("payable"))||!viewFieldHM.containsKey(salaryScheme.get("taxable")) ||
        		!viewFieldHM.containsKey(salaryScheme.get("incometax")) ||!viewFieldHM.containsKey(salaryScheme.get("realpay"))*/){
        	log.error("GZSchemeFieldConfigError");
        	throw new GeneralException("GZSchemeFiledConfigError");
        }
        //薪酬表年份集合
        List<String> yearList = this.getYearData(schemeId,salaryTable, dateField,nbase,a0100);
        if (yearList.size() == 0) {
            returnData.put("noData", true);
            returnData.put("schemes", salaryScheme == null ? "[]" : salaryScheme.get("schemes"));
            return returnData;
        }
        if (StringUtils.isEmpty(year)) {//说明是首次进入没选择年份 默认查询最高年份
            year = yearList.get(0);
        }
        returnData.put("yearList", yearList);
        returnData.put("year", year);
        String payable = (String) salaryScheme.get("payable");
        String taxable = (String) salaryScheme.get("taxable");
        String incometax = (String) salaryScheme.get("incometax");
        String realpay = (String) salaryScheme.get("realpay");
        String salary_date = (String) salaryScheme.get("salary_date");
        fieldsMap.put(payable, "payable");//应发工资
        fieldsMap.put(taxable, "taxable");//应纳税金额
        fieldsMap.put(incometax, "incometax");//个人所得税
        fieldsMap.put(realpay, "realpay");//实发工资
        // fieldsMap.put("payable",payable);
        // fieldsMap.put("taxable",taxable);
        // fieldsMap.put("incometax",incometax);
        // fieldsMap.put("realpay",realpay);

        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select ").append(ResourceFactory.getProperty("gz.acount.month")+",").append(salary_date).append(" as salary_date");
        if (!"none".equals(payable)||"".equals(payable)){
            sqlBuffer.append("," + payable).append(" as payable");
        }
        if (!"none".equals(taxable)||"".equals(taxable)){
            sqlBuffer.append("," + taxable).append(" as taxable");
        }
        if (!"none".equals(incometax)||"".equals(incometax)){
            sqlBuffer.append(",").append(incometax).append(" as incometax");
        }
        if (!"none".equals(realpay)||"".equals(realpay)){
            sqlBuffer.append(",").append(realpay).append(" as realpay");
        }
        //StringBuffer sumSql = new StringBuffer();
        //sumSql.append("select sum(").append(payable).append(")").append(" as payableSum").append(",").append("sum(").append(taxable).append(")");
        //sumSql.append(" as taxableSum").append(",").append("sum(").append(incometax).append(")").append(" as incometaxSum").append(",").append("sum(");
        //sumSql.append(realpay).append(")").append(" as realpaySum");
        JSONArray jsonArray = JSONArray.fromObject(items);
        List itemDataList = new ArrayList();
        viewFieldHM = this.getViewField(userView,(String)salaryScheme.get("salary_table"),state,false);
        for (int i = 0; i < jsonArray.size(); i++) {
            Map itemMap = new HashMap();
            Map itemHM = JSONObject.fromObject(jsonArray.get(i));
            itemMap.put("name", (String) itemHM.get("name"));
            String field = (String) itemHM.get("item");
            String[] fields = field.split(",");
            HashMap fieldHM = new HashMap();
            List fieldList = new ArrayList();
            for (int j = 0; j < fields.length; j++) {
            	if(!viewFieldHM.containsKey(fields[j])){
            		continue;
            	}
            	// 优化前是去fieldItem里面拿指标名称，优化后去t_hr_busifield表里查询修改后的指标名称
                ContentDAO dao = new ContentDAO(this.conn);
                RowSet rowSet = null;
                ArrayList list = new ArrayList();
                list.add((String)salaryScheme.get("salary_table"));
                list.add(fields[j]);
                String fieldname = "";
                String itemType = "";
                try {
                    rowSet = dao.search("select * from t_hr_busifield where FieldSetId = ? and ItemId = ?",list);
                    while (rowSet.next()){
                        fieldname = rowSet.getString("itemdesc");
                        itemType = rowSet.getString("itemtype");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }finally {
                    PubFunc.closeResource(rowSet);
                }

                // FieldItem fieldItem = DataDictionary.getFieldItem(fields[j]);
                sqlBuffer.append(",").append(fields[j]);
                //sumSql.append(",").append("sum(").append(fields[j]).append(")").append(" as ").append(fields[j]).append("Sum");
                // String fieldname = fieldItem.getItemdesc();
                fieldHM.put(fields[j], fieldname);
                // 由于字符型指标和数值型指标的前台显示格式不同，所以需要将itemType传输过去
                HashMap itemTypeAndItemId = new HashMap();
                itemTypeAndItemId.put(fields[j],itemType);
                fieldList.add(itemTypeAndItemId);
            }
            itemMap.put("fields", fieldHM);
            itemMap.put("fieldList", fieldList);//由于对象属性无序，所以新增此数组用于顺序显示指标
            itemDataList.add(itemMap);

        }
        //sumSql.append(" from ").append(salaryTable).append(" group by nbase,a0100,").append(Sql_switcher.year(salary_date));
        //sumSql.append(" having a0100 =? and nbase = ? and ").append(Sql_switcher.year(salary_date)).append(" = ?");
        fieldsMap.put("items", itemDataList);
        returnData.put("fields", fieldsMap);
        returnData.put("schemes", salaryScheme == null ? new ArrayList() : salaryScheme.get("schemes"));
        sqlBuffer.append(" from ").append(salaryTable).append(" where nbase =? and a0100 = ?").append(" and ").append(Sql_switcher.year(salary_date)).append("=?");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        String month = "";
        //ResultSetMetaData resultSetMetaData = null;
        List monthList = new ArrayList();
        try {
            rowSet = dao.search(sqlBuffer.toString(), Arrays.asList(nbase,a0100, year));
            //resultSetMetaData = rowSet.getMetaData();
            while (rowSet.next()) {
                Map monthData = new HashMap();
                if (StringUtils.contains(salary_date.toLowerCase(), "z0")){
                    month = rowSet.getString(ResourceFactory.getProperty("gz.acount.month"));
                }
                else{
                    month = String.valueOf(rowSet.getDate("salary_date").getMonth()+1);
                }
                if (!"none".equals(payable)){
                    String payableValue = rowSet.getString("payable");
                    monthData.put(payable, payableValue);
                }
                if (!"none".equals(taxable)){
                    String taxableValue = rowSet.getString("taxable");
                    monthData.put(taxable, taxableValue);
                }
                if (!"none".equals(incometax)){
                    String incometaxValue = rowSet.getString("incometax");
                    monthData.put(incometax, incometaxValue);
                }
                if (!"none".equals(realpay)){
                    String realpayValue = rowSet.getString("realpay");
                    monthData.put(realpay, realpayValue);
                }

                monthData.put("month", month);
                for (int i = 0; i < jsonArray.size(); i++) {
                    Map itemHM = JSONObject.fromObject(jsonArray.get(i));
                    String field = (String) itemHM.get("item");
                    String[] fields = field.split(",");
                    for (int j = 0; j < fields.length; j++) {
                    	if(!viewFieldHM.containsKey(fields[j])){
                    		continue;
                    	}
                        RowSet rowSet1 = null;
                    	String itemType = "";
                    	rowSet1 = dao.search("select itemtype from fielditem where itemid = ?",Arrays.asList(fields[j]));
                    	while (rowSet1.next()){
                            itemType = rowSet1.getString("itemtype");
                        }
                    	if ("A".equals(itemType)){
                            String codesetid = DataDictionary.getFieldItem(fields[j]).getCodesetid();
                            if ("0".equals(codesetid)){
                                monthData.put(fields[j], "0".equals(rowSet.getString(fields[j]))?"":rowSet.getString(fields[j]));
                            }else {
                                String codeItemDesc = AdminCode.getCodeName(codesetid,rowSet.getString(fields[j]));
                                monthData.put(fields[j], codeItemDesc);
                            }
                        }else {
                            monthData.put(fields[j], StringUtils.isEmpty(rowSet.getString(fields[j]))?0:rowSet.getString(fields[j]));
                        }
                    }

                }
                monthList.add(monthData);
            }
            Collections.sort(monthList, new Comparator<Map<String, String>>() {
                @Override
                public int compare(Map<String, String> o1, Map<String, String> o2) {
                    return Integer.valueOf(o1.get("month").replaceAll("[^0-9]", "")) - Integer.valueOf(o2.get("month").replaceAll("[^0-9]", ""));//根据month月份数字进行排序
                }
            });
            //rowSet = dao.search(sumSql.toString(), Arrays.asList(userView.getA0100(), userView.getDbname(), year));
            //if (rowSet.next()) {
            //    Map sumValueMap = new HashMap();
            //    String payableSumValue = rowSet.getString("payableSum");
            //    String taxableSumValue = rowSet.getString("taxableSum");
            //    String incometaxSumValue = rowSet.getString("incometaxSum");
            //    String realpaySumValue = rowSet.getString("realpaySum");
            //    sumValueMap.put("month", "合计");
            //    sumValueMap.put(payable, payableSumValue);
            //    sumValueMap.put(taxable, taxableSumValue);
            //    sumValueMap.put(incometax, incometaxSumValue);
            //    sumValueMap.put(realpay, realpaySumValue);
            //    for (int i = 0; i < jsonArray.size(); i++) {
            //        Map itemHM = JSONObject.fromObject(jsonArray.get(i));
            //        String field = (String) itemHM.get("item");
            //        String[] fields = field.split(",");
            //        for (int j = 0; j < fields.length; j++) {
            //            sumValueMap.put(fields[j], rowSet.getString(fields[j] + "Sum"));
            //		}
            //
            //	}
            //    monthList.add(sumValueMap);
            //}
        } catch (SQLException e) {
            e.printStackTrace();
            //returnData.put("return_code","fail");//为了前台页面不至于空白，这里不抛出异常
            //returnData.put("return_msg",ResourceFactory.getProperty("mysalary.error.queryMySalaryData").replace("{schemeName}",schemeName));
            throw new GeneralException(ResourceFactory.getProperty("mysalary.error.queryMySalaryData").replace("{schemeName}", schemeName));
        }

        returnData.put("values", monthList);
        returnData.put("zeroItemCtrl",zeroItemCtrl);
        return returnData;
    }

    /**
     * 获取当前薪酬表的年份集合
     *
     * @param salaryTable 视图表名
     * @param dateField   时间维度指标
     * @return
     */
    private List<String> getYearData(String schemeId,String salaryTable, String dateField,String nbase,String a0100) throws GeneralException {
       // String b0110 = userView.getUserOrgId();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        List<String> yearList = new ArrayList();
        try {
	        RecordVo vo = new RecordVo("gz_table_scheme");
	        vo.setInt("id", Integer.parseInt(schemeId));
	        vo = dao.findByPrimaryKey(vo);
	        int startYear = vo.getInt("year");
	        StringBuffer sqlBuffer = new StringBuffer();
	        ArrayList list = new ArrayList();
	        sqlBuffer.append("select distinct ");
	        sqlBuffer.append(Sql_switcher.year(dateField));
	        sqlBuffer.append(" salaryYear from ");
	        sqlBuffer.append(salaryTable).append(" where ")
	                .append(" nbase=? and A0100=? and ")
	                .append(dateField)
	                .append(" is not null order by salaryYear desc");
	        list.add(nbase);
	        list.add(a0100);
            rowSet = dao.search(sqlBuffer.toString(), list);
            while (rowSet.next()) {
                String year = rowSet.getString("salaryYear");
                if (startYear > 0 ) {
                    if (Integer.valueOf(year) >= startYear) {
                        yearList.add(year);
                    }
                } else {
                    yearList.add(year);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //获取薪酬表年份出错
            log.error(ResourceFactory.getProperty("gz.mysalary.scheme.error.queryYear"));
            throw new GeneralException(ResourceFactory.getProperty("gz.mysalary.scheme.error.queryYear"));
        } finally {
            PubFunc.closeResource(rowSet);
        }
        return yearList;
    }

    /**
     * 获取su用户关联的自助用户的授权角色
     * @return
     */
    private ArrayList getSupAdminUserRolelist(UserView userView){
        ArrayList roleList = new ArrayList();
        DbNameBo dbNameBo = new DbNameBo(this.conn);
        String usernameField = dbNameBo.getLogonUserNameField();
        ContentDAO dao = new ContentDAO(this.conn);
        StringBuffer sql = new StringBuffer();
        sql.append("select ").append(usernameField).append(" from ").append(userView.getDbname()+"A01");
        sql.append(" where a0100 = ?");
        RowSet rowSet = null;
        try{
            rowSet = dao.search(sql.toString(),Arrays.asList(userView.getA0100()));
            if(rowSet.next()){
                String username = rowSet.getString(usernameField);
                UserView relationUserView = new UserView(username,this.conn);
                relationUserView.canLogin();
                roleList = relationUserView.getRolelist();
            }

        }catch (Exception e){

        }finally {
            PubFunc.closeResource(rowSet);
        }
        return roleList;
    }

	@Override
	public UserView getEmployeeSalaryInfo(String nbase, String a0100) throws GeneralException {
		UserView userView = null;
		String userfield = ConstantParamter.getLoginUserNameField();
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "select "+ userfield +" from "+ nbase + "A01 where a0100=?";
		RowSet rs = null;
		String username = "";
		try {
			rs = dao.search(sql, Arrays.asList(a0100));
			if(rs.next()){
				username = rs.getString(userfield);
			}
			userView = new UserView(username,this.conn);
			userView.isLockedLogin();
			userView.canLogin(false);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return userView;
	}

    /**
     * @param sortItem
     * @Description: 薪酬方案排序
     * @Param: [sortItem]
     * @return: void
     * @Author: Liuyd
     * @Date: 2020/7/16
     */
    @Override
    public void saveNorder(String sortItem) throws GeneralException {
        ArrayList updateParamList = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        String[] sortItemList = sortItem.split("`");
        String norder = sortItemList[0];
        String id = PubFunc.decrypt(sortItemList[1]);
        updateParamList.add(norder);
        updateParamList.add(Integer.valueOf(id));
        StringBuffer updateNorderSql = new StringBuffer();
        updateNorderSql.append("update gz_table_scheme set norder = ? where id = ?");
        try {
            int i = dao.update(updateNorderSql.toString(),updateParamList);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GeneralException("排序失败！");
        }

    }

    /**
     * @param fieldSetId
     * @Description: 根据子集id获取该子集下的数值型指标
     * @Param: [fieldSetId]
     * @return: java.util.List
     * @Author: Liuyd
     * @Date: 2020/7/17
     */
    @Override
    public List searchNumberFieldItem(String fieldSetId,String flag) throws GeneralException {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("select * from fielditem ");
        sql.append("where itemtype = 'N' and fieldsetid = ?");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), Arrays.asList(fieldSetId.toUpperCase()));
            while (rs.next()){
                String itemid = rs.getString("itemid");
                String itemdesc = rs.getString("itemdesc");
                // 计算公式项目数据所走的逻辑
                if ("search".equalsIgnoreCase(flag)){
                    HashMap itemIdAndItemDescMap = new HashMap();
                    itemIdAndItemDescMap.put("id",itemid+":"+itemdesc);
                    itemIdAndItemDescMap.put("name",itemid+":"+itemdesc);
                    list.add(itemIdAndItemDescMap);
                }else if ("check".equalsIgnoreCase(flag)){
                    // 校验计算公式时走的逻辑
                    FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
                    list.add(fieldItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param userView
     * @param c_expr
     * @param itemType
     * @param fieldSetId
     * @Description: 校验计算公式内容
     * @Param: [userView, c_expr, itemType, fieldSetId]
     * @return: java.lang.String
     * @Author: Liuyd
     * @Date: 2020/7/20
     */
    @Override
    public String checkFormula(UserView userView, String c_expr, String itemType, String fieldSetId) throws GeneralException {
        ArrayList fieldlist = (ArrayList) this.searchNumberFieldItem(fieldSetId,"check");
        c_expr = SafeCode.decode(c_expr);
        c_expr = PubFunc.keyWord_reback(c_expr);
        String flag = "";
        try {
            if (c_expr != null && c_expr.length() > 0) {
                YksjParser yp = new YksjParser(userView, fieldlist, YksjParser.forNormal, getVarType(itemType)
                        , YksjParser.forPerson, "Ht", "");
                yp.setVarList(fieldlist);//使用“执行标准”函数时，临时变量需要用到单独传入的fielditem数据集 zhanghua 20170516
                yp.setCon(this.conn);
                boolean b = false;
                try {
                    b = yp.Verify_where(c_expr.trim());
                } catch (Exception e) {
                    e.printStackTrace();

                    b = false;
                }
                if (b) {// 校验通过
                    flag = "ok";
                } else {
                    flag = yp.getStrError();
                }
            } else {
                flag = "ok";
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return flag;
    }

    /**
	 * 获取当前用户的角色集合
	 * @param nbase
	 * @param a0100
	 * @return
	 */
	private ArrayList getRoleList(String nbase,String a0100){
		ArrayList list = new ArrayList();
		String sql = "select role_id from t_sys_staff_in_role where upper(staff_id)=?";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql, Arrays.asList(nbase.toUpperCase()+a0100));
			while(rs.next()){
				list.add(rs.getString("role_id"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;
	}

	/**
	 * 数据库迁移，导致视图表不存在，先检验视图表是否存在
	 * @param view
	 * @throws GeneralException
	 */
	private void isViewExist(String view) throws GeneralException{
		DbWizard dbw = new DbWizard(this.conn);
		if(!dbw.isExistTable(view)){
			//视图不存在
			log.error("--->getViewError");
			throw new GeneralException("getViewError");
		}
	}

	@Override
	public HashMap getViewData(String view) throws GeneralException {
		HashMap dataHM = new HashMap();
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		sql.append("select itemid,itemdesc,itemtype,expression from t_hr_busifield where FieldSetId=? and itemtype in('A','N','D') and useflag=1 order by displayid");
		RowSet rs = null;
        RowSet viewSqlRs = null;
		ArrayList numFieldList = new ArrayList();
		ArrayList mainsourceList = new ArrayList();
		HashMap mainsourceKeyHM= new HashMap();
		String createViewSql = ""; // 视图创建sql
        String searchViewSql = "select * from t_hr_busitable where FieldSetId = ?";
		String mainsource = "";
        String [] nbasesList = null;
		try {
			rs = dao.search(sql.toString(), Arrays.asList(view));
			while(rs.next()){
				HashMap map = new HashMap();
				String itemid = rs.getString("itemid");
				FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
				String fieldSetId = fieldItem.getFieldsetid();
				if(StringUtils.equalsIgnoreCase(fieldSetId, view)) {//子集和视图表名一致跳过，指标体系删除了指标
					continue;
				}
				FieldSet fieldSet = DataDictionary.getFieldSetVo(fieldSetId);
				itemid = itemid.toLowerCase();
				fieldSetId = fieldSetId.toUpperCase();
				String itemType = rs.getString("itemtype");
				String itemname = rs.getString("itemdesc");

                // 放开时间维度指标的控制
				// if("D".equalsIgnoreCase(itemType) && itemid.toUpperCase().endsWith("Z0")){
				// 	mainsource = fieldSetId;
				// }
				if(!mainsourceKeyHM.containsKey(fieldSetId)){
					mainsourceKeyHM.put(fieldSetId, fieldSetId);
					HashMap mainsourceHM = new HashMap();
					mainsourceHM.put("fieldsetid", fieldSetId);
					mainsourceHM.put("fieldsetdesc", fieldSet.getFieldsetdesc());
					mainsourceList.add(mainsourceHM);
				}

                // 解析计算公式
                String calcformat = "";
                String expression = rs.getString("expression");
                if (expression!=null){
                    StringReader s = new StringReader(expression);
                    Document classpreDoc = (new SAXBuilder()).build(s);
                    Element classpreEle = classpreDoc.getRootElement();
                    Element calcformatElement = classpreEle.getChild("calcformula");
                    if (calcformatElement!=null){
                        // 如果指标没有编辑过计算公式，则默认显示指标编辑前的名字
                        if ("null".equals(calcformatElement.getTextTrim())||calcformatElement.getTextTrim().isEmpty()){
                            calcformat = fieldItem.getItemdesc();
                        }else{
                            calcformat = calcformatElement.getTextTrim();
                        }
                    }
                }
                map.put("id", itemid);
				map.put("fieldsetid", fieldSetId);
				map.put("fieldsetdesc", fieldSet.getFieldsetdesc());
				map.put("itemid", itemid);
				map.put("itemdesc", itemname);
				map.put("itemtype", itemType);
				map.put("calcformat", calcformat);
				numFieldList.add(map);
			}
			// 查询出视图创建的sql获取出nbases节点传给前台，解决人员库不回显的问题
            viewSqlRs = dao.search(searchViewSql,Arrays.asList(view));
			while (viewSqlRs.next()){
                createViewSql = viewSqlRs.getString("classpre");
                StringReader s = new StringReader(createViewSql);
                Document classpreDoc = (new SAXBuilder()).build(s);
                Element classpreEle = classpreDoc.getRootElement();
                Element nbases = classpreEle.getChild("nbases");
                Element fieldsetid = classpreEle.getChild("fieldsetid");
                if (nbases!=null){
                    String textTrim = nbases.getTextTrim();
                    nbasesList = textTrim.split(",");
                }
                // 视图主表从classpre中取
                if (fieldsetid!=null){
                    String textTrim = fieldsetid.getTextTrim();
                    mainsource = textTrim;
                }
            }
		} catch (SQLException e) {
			e.printStackTrace();
			//获取薪资表指标出错
			log.error("gz.mysalary.scheme.error.getViewField");
            throw new GeneralException("gz.mysalary.scheme.error.getViewField");
		} catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(viewSqlRs);
		}
		dataHM.put("mainsource", mainsource );
		dataHM.put("mainsourceList", mainsourceList);
		dataHM.put("fieldList", numFieldList);
		dataHM.put("nbasesChecked", nbasesList);
		return dataHM;
	}

	/**
	 * 获取用户权限范围内指标
	 * @param userView
	 * @param view 视图
	 * @param state 用户标识
	 * @param privState  true 走权限 false 不走权限
	 * @return
	 */
	private HashMap getViewField(UserView userView,String view,String state,boolean privState){
		HashMap map = new HashMap();
		String sql = "select itemid from t_hr_busifield where fieldsetid = ? ";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		StringBuffer fieldPrivStr = new StringBuffer();
		if (StringUtils.equalsIgnoreCase("emply", state)) { //查看员工薪酬获取指标权限
			fieldPrivStr = userView.getFieldpriv();
		} else { //查看本人薪酬获取员工角色特征指标权限
			fieldPrivStr = userView.getEmp_fieldpriv();
		}
		try {
			rs = dao.search(sql,Arrays.asList(view));
			while(rs.next()){
				if(!privState) {
					map.put(rs.getString("itemid"), rs.getString("itemid"));
					continue;
				}
				if(userView.isSuper_admin()) {
					map.put(rs.getString("itemid"), rs.getString("itemid"));
					continue;
				}
				if(fieldPrivStr.indexOf(","+rs.getString("itemid")+"1,") == -1 && fieldPrivStr.indexOf(","+rs.getString("itemid")+"2,") == -1) {
					continue;
				}
				map.put(rs.getString("itemid"), rs.getString("itemid"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
    /**
    * @Description: 获取计算公式对应指标类型
    * @Param: [itemType]
    * @return: int
    * @Author: Liuyd
    * @Date: 2020/7/20
    */
    private int getVarType(String itemType) {
        int varType = YksjParser.FLOAT; // float
        if ("D".equals(itemType)){
            varType = YksjParser.DATEVALUE;
        }
        else if ("A".equals(itemType) || "M".equals(itemType)){
            varType = YksjParser.STRVALUE;
        }
        return varType;
    }
    /**
    * @Description: 根据计算公式返回拼接的sql
    * @Param: [userView, fieldList, itemtype, calcformat]
    * @return: java.lang.String
    * @Author: Liuyd
    * @Date: 2020/7/21
    */
    private String formulaReplaceSql(UserView userView, ArrayList fieldList,String itemtype,String calcformat) throws SQLException, GeneralException {
        // YksjParser yp = new YksjParser(userView, (ArrayList) this.searchNumberFieldItem(((String) fielditem.get("itemid")).substring(0,3),"check"), YksjParser.forNormal, 6, YksjParser.forPerson, "Ht", userView.getDbname());
        YksjParser yp = new YksjParser(userView, fieldList, YksjParser.forNormal, 6, YksjParser.forPerson, "Ht", userView.getDbname());
        yp.setCon(conn);
        int varType = getVarType(itemtype);
        yp.setVarType(varType);
        yp.run(calcformat);
        String calcFormatSql = yp.getSQL();
        return calcFormatSql;
    }
    /**
    * @Description: 将计算公式转化为能拼到视图sql的格式(为字段前面加上表名)
    * @Param: [nabse, fieldSetId, calcformatSql]
    * @return: java.lang.String
    * @Author: Liuyd
    * @Date: 2020/7/22
    */
    private String formatFormulaSql (String nabse,String fieldSetId,String calcformatSql){
        /**
         *  替换变量
         *  替换格式例如：
         *  a5810->UsrA58.a5810
         */
        StringBuffer replacement = new StringBuffer();
        replacement.append(nabse);
        replacement.append(fieldSetId.toUpperCase());
        replacement.append('.');
        replacement.append(fieldSetId);
        String replace = calcformatSql.replace(fieldSetId, replacement.toString());
        return replace;
    }

    @Override
    public HashMap getMySalarySchemeMaxAndMinDate(String salary_table, String salary_date, String nbase, String a0100) throws GeneralException{
    	HashMap dateHM = new HashMap();
    	StringBuffer sql = new StringBuffer();
    	sql.append("select max("+salary_date+") maxDate,min("+salary_date+") minDate from "+ salary_table);
    	sql.append(" where nbase=? and a0100=? ");
    	ContentDAO dao = new ContentDAO(conn);
    	RowSet rs =null; 
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    	try {
			rs = dao.search(sql.toString(), Arrays.asList(nbase,a0100));
			if(rs.next()) {
				if(StringUtils.isNotBlank(rs.getString("maxDate"))) {
					dateHM.put("maxDate",sdf.format(rs.getDate("maxDate")));
					dateHM.put("minDate",sdf.format(rs.getDate("minDate")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
    	return dateHM;
    }

}
