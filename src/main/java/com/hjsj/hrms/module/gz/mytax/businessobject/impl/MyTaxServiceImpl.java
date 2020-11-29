package com.hjsj.hrms.module.gz.mytax.businessobject.impl;

import com.hjsj.hrms.module.gz.mytax.businessobject.MyTaxService;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


public class MyTaxServiceImpl implements MyTaxService {
    private Connection conn;
    /**
     * 系统内置指标列表
     **/
    private static List<String> systemItemList = new ArrayList<String>();
    /**
     * 使用max进行取值的字段
     **/
    private static List<String> useMaxColumn = new ArrayList<String>();

    static {
        /**初始化系统内置列表指标开始**/
        systemItemList.add("A00Z2");
        systemItemList.add("ynssde");
        systemItemList.add("ljsde");
        systemItemList.add("ljse");
        systemItemList.add("lj_basedata");
        systemItemList.add("Tax_date");
        systemItemList.add("Sskcs");
        systemItemList.add("Basedata");
        systemItemList.add("Sl");
        systemItemList.add("Sds");
        /**初始化系统内置列表指标结束**/
        useMaxColumn.add("ljsde");//累计所得额
        useMaxColumn.add("ljse");//累计预扣税额
        useMaxColumn.add("lj_basedata");//累计基本减除费用
        useMaxColumn.add("Sskcs");//速算扣除数
        useMaxColumn.add("Basedata");//基数
        useMaxColumn.add("Sl");//税率
    }

    @Override
    public Map getMyTaxItemList(UserView userView) throws GeneralException {
        Map resultMap = new HashMap();
        List itemList = new ArrayList();//配置的指标列表
        List removeItemList = new ArrayList();//被系统移除的指标列表
        ContentDAO dao = new ContentDAO(this.conn);
        RecordVo recordVo = ConstantParamter.getRealConstantVo("GZ_MYTAX_PARAM");
        //if (recordVo == null) {//如果没有GZ_MYTAX_PARAM属性则新建
        //    recordVo = new RecordVo("CONSTANT");
        //    recordVo.setString("constant", "GZ_MYTAX_PARAM");
        //    recordVo.setString("str_value", "[]");
        //    dao.addValueObject(recordVo);
        //    resultMap.put("itemList", itemList);
        //    resultMap.put("removeItemList", removeItemList);
        //    return resultMap;
        //}
        String schemeItem = recordVo.getString("str_value");
        if (StringUtils.isNotEmpty(schemeItem)) {
            itemList = JSONArray.fromObject(schemeItem);
        }

        List gzItemList = new ArrayList();
        this.getGzTaxMxItemList(gzItemList);
        List gzItemIdList = this.getGzTaxMxItemIdList(gzItemList);
        Iterator iterator = itemList.iterator();
        while (iterator.hasNext()) {
            JSONObject json = (JSONObject) iterator.next();
            String itemid = json.getString("itemid");
            //
            if (!systemItemList.contains(itemid) && !gzItemIdList.contains(itemid)) {
                FieldItem field = DataDictionary.getFieldItem(itemid);
                if (field != null) {
                    String fieldName = field.getItemdesc();
                    removeItemList.add(fieldName);
                }
                iterator.remove();
            }
        }
        resultMap.put("itemList", itemList);
        resultMap.put("removeItemList", removeItemList);
        return resultMap;
    }

    @Override
    public List listGzTaxMxField(UserView userView) throws GeneralException {
        List gzItems = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        //gzItems.add(JSONObject.fromObject("{itemid:\"Nbase\",itemType:\"A\",itemName:\"" + ResourceFactory.getProperty("gz_new.gz_nbase") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"A0100\",itemType:\"A\",itemName:\"" + ResourceFactory.getProperty("a0100.label") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"A00Z0\",itemType:\"D\",itemName:\"" + ResourceFactory.getProperty("gz.columns.a00z0") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"A00Z1\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("gz.columns.a00z1") + "\"}"));
        gzItems.add(JSONObject.fromObject("{itemid:\"A00Z2\",itemType:\"D\",itemName:\"" + ResourceFactory.getProperty("gz_new.gz_accounting.send_time") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"A00Z3\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("label.gz.count") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"A0000\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("a0000.label") + "\"}"));
        gzItems.add(JSONObject.fromObject("{itemid:\"ynssde\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("mytax.field.ynssde") + "\"}"));
        gzItems.add(JSONObject.fromObject("{itemid:\"ljsde\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("mytax.field.ljsde") + "\"}"));
        gzItems.add(JSONObject.fromObject("{itemid:\"ljse\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("mytax.field.ljse") + "\"}"));
        gzItems.add(JSONObject.fromObject("{itemid:\"lj_basedata\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("mytax.field.lj_basedata") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"znjy\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("mytax.field.znjy") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"sylr\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("mytax.field.sylr") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"zfdklx\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("mytax.field.zfdklx") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"zfzj\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("mytax.field.zfzj") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"jxjy\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("mytax.field.jxjy") + "\"}"));
        gzItems.add(JSONObject.fromObject("{itemid:\"Tax_date\",itemType:\"D\",itemName:\"" + ResourceFactory.getProperty("gz.self.tax.taxdate") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"TaxMode\",itemName:\"" + ResourceFactory.getProperty("gz.columns.taxmode") + "\"}"));
        gzItems.add(JSONObject.fromObject("{itemid:\"Sskcs\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("gz.columns.sskcs") + "\"}"));
        gzItems.add(JSONObject.fromObject("{itemid:\"Basedata\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("gz.columns.basedata") + "\"}"));
        gzItems.add(JSONObject.fromObject("{itemid:\"Sl\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("gz.columns.sl") + "\"}"));
        gzItems.add(JSONObject.fromObject("{itemid:\"Sds\",itemType:\"N\",itemName:\"" + ResourceFactory.getProperty("gz.self.tax.sds") + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"B0110\",itemType:\"A\",itemName:\"" + DataDictionary.getFieldItem("B0110").getItemdesc() + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"E0122\",itemType:\"A\",itemName:\"" + DataDictionary.getFieldItem("E0122").getItemdesc() + "\"}"));
        //gzItems.add(JSONObject.fromObject("{itemid:\"A0101\",itemType:\"A\",itemName:\"" + DataDictionary.getFieldItem("A0101").getItemdesc() + "\"}"));
        this.getGzTaxMxItemList(gzItems);//增加系统中配置的指标
        return gzItems;
    }

    @Override
    public String saveMyTaxItem(List list, UserView userView) throws GeneralException {
        JSONArray tempList = new JSONArray();
        for (Object bean : list) {
            Map map;
            if (!(bean instanceof Map)) {//不是Map的话是从其他地方保存过来的
                MorphDynaBean bean1 = (MorphDynaBean) bean;
                map = PubFunc.DynaBean2Map(bean1);
            } else {
                map = (Map) bean;
            }
            tempList.add(JSONObject.fromObject(map));
        }
        ContentDAO dao = new ContentDAO(this.conn);
        RecordVo recordVo = ConstantParamter.getRealConstantVo("GZ_MYTAX_PARAM");
        recordVo.setString("str_value", tempList.toString());
        try {
            dao.updateValueObject(recordVo);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String deleteMyTaxItem(String ids, UserView userView) throws GeneralException {
        return null;
    }

    @Override
    public Map initMyTaxData(UserView userView) throws GeneralException {
        Map returnData = new HashMap();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        //获取个税缴纳年份集合
        List yearList = new ArrayList();
        StringBuffer queryYearSql = new StringBuffer();
        queryYearSql.append("select Max(");
        queryYearSql.append(Sql_switcher.year("A00Z0")).append(") maxYear,");
        queryYearSql.append("Min(").append(Sql_switcher.year("A00Z0")).append(") minYear from");
        queryYearSql.append("(select A00Z0 from gz_tax_mx where NBASE=?").append(" and A0100 = ?");
        queryYearSql.append(" union all select A00Z0 from taxarchive where NBASE=?");
        queryYearSql.append(" and A0100=?) g");
        List param = new ArrayList();
        param.add(userView.getDbname());
        param.add(userView.getA0100());
        param.add(userView.getDbname());
        param.add(userView.getA0100());
        String maxYear = "";
        try {
            rs = dao.search(queryYearSql.toString(), param);
            if (rs.next()) {
                maxYear = rs.getString("maxYear");
                String minYear = rs.getString("minYear");
                if (StringUtils.isNotEmpty(maxYear) && StringUtils.isNotEmpty(minYear)) {
                    for (int i = Integer.valueOf(maxYear); i >= Integer.valueOf(minYear); i--) {
                        yearList.add(String.valueOf(i));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GeneralException(ResourceFactory.getProperty("mytax.error.queryYear"));
        }
        if (yearList.size() == 0) {
            returnData.put("noData", true);
            return returnData;
        }
        returnData.put("yearList", yearList);
        returnData.put("year", maxYear);//初始化时默认查询最高年份数据
        //获取个税缴纳年份结束
        //获取我的个税项目
        List<JSONObject> items = new ArrayList();
        JSONObject fields = new JSONObject();
        Map itemsMap = this.getMyTaxItemList(userView);
        JSONArray schemeItemArray = (JSONArray) itemsMap.get("itemList");
        this.getTaxParamColumn(userView, schemeItemArray, true);//移除计算公式出错的项目
        for (int i = 0; i < schemeItemArray.size(); i++) {
            JSONObject itemObject = schemeItemArray.getJSONObject(i);
            JSONObject item = new JSONObject();
            String itemid = itemObject.getString("itemid");
            String itemName = itemObject.getString("name");
            String itemType = itemObject.getString("itemType");
            item.put("itemid", itemid);
            item.put("itemType", itemType);
            if (StringUtils.equalsIgnoreCase("N", itemType)) {
                String format = "0,000.00";
                FieldItem tempItem = DataDictionary.getFieldItem(itemid);
                if (tempItem != null) {//数据字典中整形的不用小数格式化,非整数的现只显示两位
                    if (tempItem.getDecimalwidth() == 0) {
                        format = "0,000";
                    }
                }
                item.put("format", format);
            }
            fields.put(itemid, itemName);
            items.add(item);
        }
        returnData.put("items", items);
        returnData.put("fields", fields);
        return returnData;
    }

    @Override
    public List getMyTaxData(String year, UserView userView) throws GeneralException {
        StringBuffer collectSql = new StringBuffer();
        StringBuffer notCollectSql = new StringBuffer();
        StringBuffer sumSql = new StringBuffer();
        Map itemsMap = this.getMyTaxItemList(userView);
        JSONArray schemeItemArray = (JSONArray) itemsMap.get("itemList");
        this.getTaxParamColumn(userView, schemeItemArray, true);
        YksjParser yp = new YksjParser(userView, getCheckFieldList(userView), YksjParser.forNormal, 6, YksjParser.forPerson, "Ht", userView.getDbname());
        yp.setCon(conn);
        Map<String, String> collectMap = new HashMap();//按年汇总的指标集合
        Map<String, String> notCollectMap = new HashMap();//非按年汇总的指标集合
        Map<String, String> notCollectTypeMap = new HashMap();//非按年汇总的指标类型集合
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        List paramList = new ArrayList();
        paramList.add(userView.getDbname());
        paramList.add(userView.getA0100());
        paramList.add(year);
        paramList.add(userView.getDbname());
        paramList.add(userView.getA0100());
        paramList.add(year);
        List valueList = new ArrayList();
        Map<String, Map> monthData = new HashMap();
        try {
            for (int i = 0; i < schemeItemArray.size(); i++) {
                String calcFormatSql = "";//计算规则所转换的sql语句
                JSONObject itemObject = schemeItemArray.getJSONObject(i);
                String calcFormat = itemObject.getString("calcFormat");//计算规则
                String itemid = itemObject.getString("itemid");//项目Id
                String yearCollect = itemObject.getString("yearCollect");//是否按年汇总
                String itemType = itemObject.getString("itemType");//指标类型
                yp.setVarType(getVarType(itemType));
                yp.run(calcFormat);
                calcFormatSql = yp.getSQL();
                if (StringUtils.equalsIgnoreCase(yearCollect, "0")) {//不按年汇总
                    notCollectMap.put(itemid, calcFormatSql);
                    notCollectTypeMap.put(itemid, itemType);
                } else if (StringUtils.equalsIgnoreCase(yearCollect, "1")) {//按年汇总,能按年汇总的都是数值型指标
                    collectMap.put(itemid, calcFormatSql);
                }
            }
            for (String key : notCollectMap.keySet()) {
                String itemType = notCollectTypeMap.get(key);//根据类型决定是使用sum还是max
                boolean isUseSum = StringUtils.equals("N", itemType) && !useMaxColumn.contains(key);
                if (isUseSum) {//数字型中非累计指标使用sum
                    sumSql.append("sum(").append(notCollectMap.get(key)).append(") ").append(key);
                    sumSql.append(",");
                } else {//其余的全部使用MAX
                    sumSql.append("max(").append(notCollectMap.get(key)).append(") ").append(key);
                    sumSql.append(",");
                }
            }
            if (sumSql.length() > 0)
                sumSql.setLength(sumSql.length() - 1);//去掉最后多余的逗号
            if (notCollectMap.size() > 0) {
                notCollectSql.append("select * from (");
                notCollectSql.append("select ");
                notCollectSql.append(Sql_switcher.month("A00Z0")).append(" month,");
                notCollectSql.append(sumSql);
                notCollectSql.append(" from gz_tax_mx where NBASE=? and A0100 =? and flag =1 and ").append(Sql_switcher.year("A00Z0")).append("=?");
                notCollectSql.append(" group by ").append(Sql_switcher.month("A00Z0")+",TAXMODE").append(" union all ");
                notCollectSql.append(" select ");
                notCollectSql.append(Sql_switcher.month("A00Z0")).append(" month,");
                notCollectSql.append(sumSql);
                notCollectSql.append(" from taxarchive  where NBASE=? and A0100 =? and flag =1 and ").append(Sql_switcher.year("A00Z0")).append("=?");
                notCollectSql.append(" group by ").append(Sql_switcher.month("A00Z0")+",TAXMODE").append(")temp order by month desc");
                rs = dao.search(notCollectSql.toString(), paramList);
                int index = 0;
                while (rs.next()) {
                    String month = rs.getString("month");
                    if (!monthData.containsKey(month)) {
                        Map temp = new HashMap();
                        monthData.put(month, temp);
                    }else {//区分不同计税方式，月份数据不能全部合并  例如2月有3条 2条综合所得 1条年终奖  这样2月份会有2条数据  wangb 2020-02-28
                    	while(true) {
                    		if(!monthData.containsKey(month+"-"+index)) {
                    			Map temp = new HashMap();
                    			month = month+"-"+index;
                                monthData.put(month, temp);
                    			break;
                    		}
                    		index++;
                    	}
                    }
                    for (String itemid : notCollectMap.keySet()) {

                        String itemType = notCollectTypeMap.get(itemid);//获取数据类型
                        if (StringUtils.equals(itemType, "A")) {
                            String value = rs.getString(itemid);
                            String codesetid = "0";//默认使用0,即不进行翻译
                            if (StringUtils.equalsIgnoreCase(itemid, "NBASE")) {
                                codesetid = "@@";
                            } else if (StringUtils.equalsIgnoreCase(itemid, "B0110")) {
                                codesetid = "UN";
                            } else if (StringUtils.equalsIgnoreCase(itemid, "E0122")) {
                                codesetid = "UM";
                            } else {
                                FieldItem item = DataDictionary.getFieldItem(itemid);
                                codesetid = item.getCodesetid();
                            }
                            if (!StringUtils.equals("0", codesetid)) {//代码型指标进行翻译
                                value = AdminCode.getCodeName(codesetid, value);
                            }
                            monthData.get(month).put(itemid, value);
                        } else if (StringUtils.equals(itemType, "D")) {//日期型进行格式化一下
                            Date dateValue = rs.getDate(itemid);
                            String fromat = "yyyy-MM-dd";//默认以年月日展现
                            String value = StringUtils.EMPTY;
                            FieldItem item = DataDictionary.getFieldItem(itemid);
                            if (item != null) {
                                int itemlength = item.getItemlength();
                                if (itemlength == 4) {//4位
                                    fromat = "yyyy";
                                } else if (itemlength == 7) {
                                    fromat = "yyyy-MM";
                                } else if (itemlength == 16) {
                                    fromat = "yyyy-MM-dd HH:mm";
                                } else if (itemlength > 16) {
                                    fromat = "yyyy-MM-dd HH:mm:ss";
                                }
                            }
                            if (dateValue != null) {
                                SimpleDateFormat format = new SimpleDateFormat(fromat);
                                value = format.format(dateValue);
                            }
                            monthData.get(month).put(itemid, value);
                        } else {
                            String value = rs.getString(itemid);
                            monthData.get(month).put(itemid, value);
                        }
                    }
                    if(month.indexOf("-") != -1) {//实际存放还是真实的月份
                    	monthData.get(month).put("month", month.split("-")[0]);
                    }else {
                    	monthData.get(month).put("month", month);
                    }
                }
            }

            if (collectMap.size() > 0) {
                //查询两张表中都是我这个人的个税数据
                StringBuffer allSqlBuffer = new StringBuffer();//最大的sql
                allSqlBuffer.append("select month,");
                for (String key : collectMap.keySet()) {
                    allSqlBuffer.append(" sum(").append(key).append(") as ").append(key).append(",");
                }
                allSqlBuffer.setLength(allSqlBuffer.length() - 1);
                allSqlBuffer.append(" from (");

                StringBuffer secondBuffer = new StringBuffer();//进行各月累积使用的sql
                secondBuffer.append("select A.month,");
                for (String key : collectMap.keySet()) {
                    secondBuffer.append(" (case when A.month>=B.month then B.").append(key).append(" else 0 end) ").append(key).append(",");
                }
                secondBuffer.setLength(secondBuffer.length() - 1);
                secondBuffer.append(" from (");

                StringBuffer combineBuffer = new StringBuffer();//进行数据合并使用的sql
                //对从两个表中查询出来的数据进行一次合并否则归档中和未归档中同时存在同一个月的时候会出现问题
                combineBuffer.append("select month,");
                for (String key : collectMap.keySet()) {
                    combineBuffer.append(" sum(").append(key).append(") as ").append(key).append(",");
                }
                combineBuffer.setLength(combineBuffer.length() - 1);
                combineBuffer.append(" from (");

                StringBuffer selectTempBuffer = new StringBuffer();//最内层查询最原始分组的数据
                selectTempBuffer.append("select ").append(Sql_switcher.month("A00Z0")).append(" month,");
                for (String key : collectMap.keySet()) {
                    selectTempBuffer.append(" sum(").append(collectMap.get(key)).append(") ").append(key).append(",");
                }
                selectTempBuffer.setLength(selectTempBuffer.length() - 1);
                selectTempBuffer.append(" from ").append("gz_tax_mx");
                selectTempBuffer.append(this.getTempSql(userView, year, "gz_tax_mx"));
                selectTempBuffer.append(" group by A00Z0");
                String oneSelectSql = selectTempBuffer.toString();//先生成一个gz_tax_mx
                selectTempBuffer.setLength(0);//清空
                selectTempBuffer.append(oneSelectSql);
                selectTempBuffer.append(" union all ");
                selectTempBuffer.append(oneSelectSql.replaceAll("gz_tax_mx", "taxarchive "));

                combineBuffer.append(selectTempBuffer).append(") combine group by month ");

                secondBuffer.append(combineBuffer).append(") A,");
                secondBuffer.append("(").append(combineBuffer).append(")B");
                allSqlBuffer.append(secondBuffer).append(")temp group by month");
                //System.out.println(allSqlBuffer.toString());可输出语句查看然后调整
                rs = dao.search(allSqlBuffer.toString());
                while (rs.next()) {
                    String month = rs.getString("month");
                    if (!monthData.containsKey(month)) {
                        Map temp = new HashMap();
                        monthData.put(month, temp);
                    }
                    for (String itemid : collectMap.keySet()) {
                        String value = rs.getString(itemid);
                        //这里是按月份整体累计了，所有当某个月数据有多条时，同时给一样的值  wangb 2020-02-28
                        Set keySet = monthData.keySet();
                        Iterator iterator = keySet.iterator();
                        while(iterator.hasNext()) {
                        	String key = (String) iterator.next();
                        	if(month.contentEquals(key.split("-")[0])) {
                        		((Map) monthData.get(key)).put(itemid, value);
                        	}
                        }
                    }
                    ((Map) monthData.get(month)).put("month", month);
                }
            }

            for (Object itemid : monthData.keySet()) {
                valueList.add(monthData.get(itemid));
            }
            Collections.sort(valueList, new Comparator<Map>() {
                @Override
                public int compare(Map o1, Map o2) {
                    return Integer.valueOf((String) o1.get("month")) - Integer.valueOf((String) o2.get("month"));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return valueList;
    }

    @Override
    public String checkFormula(UserView userView, String c_expr, String itemType) throws GeneralException {
        ArrayList fieldlist = this.getCheckFieldList(userView);
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
     * 获取一年中所有月的所得税
     *
     * @param userView 当前登录用户
     * @param year     索要查询的年
     * @return 一年所有月的所得税
     */
    @Override
    public Map<String, String> getMonthSdsOfYear(UserView userView, String year) throws GeneralException {
        Map<String, String> monthSdsMap = new HashMap<String, String>();
        List<String> paramList = new ArrayList<String>();
        RowSet rs = null;
        double ljse = 0.00;
        try {
            StringBuffer selectSqlBuffer = new StringBuffer();
            selectSqlBuffer.append("select sum(sds) as sds,");
            selectSqlBuffer.append(Sql_switcher.month("A00Z0")).append(" as month");
            selectSqlBuffer.append(" from (");
            selectSqlBuffer.append("select ").append(Sql_switcher.isnull("sum(sds)", "0.00"));
            selectSqlBuffer.append(" as sds,A00Z0 from gz_tax_mx where A0100=? and nbase=? and ");
            //selectSqlBuffer.append("select sum(sds) as sds,A00Z0 from gz_tax_mx where A0100=? and nbase=? and ");
            selectSqlBuffer.append(Sql_switcher.year("A00Z0")).append("=?");
            selectSqlBuffer.append(" group by A00Z0");
            selectSqlBuffer.append(" union all ");
            selectSqlBuffer.append("select ").append(Sql_switcher.isnull("sum(sds)", "0.00"));
            selectSqlBuffer.append(" as sds,A00Z0 from taxarchive where A0100=? and nbase=? and ");
            selectSqlBuffer.append(Sql_switcher.year("A00Z0")).append("=?");
            selectSqlBuffer.append(" group by A00Z0)temp group by ");
            selectSqlBuffer.append(Sql_switcher.month("A00Z0"));
            /**设置参数列表**/
            paramList.add(userView.getA0100());
            paramList.add(userView.getDbname());
            paramList.add(year);
            paramList.add(userView.getA0100());
            paramList.add(userView.getDbname());
            paramList.add(year);
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(selectSqlBuffer.toString(), paramList);
            while (rs.next()) {
                String month = rs.getString("month");
                String sds = rs.getString("sds");
                ljse = ljse + Double.parseDouble(sds);
                monthSdsMap.put(month, sds);
            }
            monthSdsMap.put("ljse", String.valueOf(ljse));
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException(ResourceFactory.getProperty("mytax.error.queryYearLjse"));
        }
        return monthSdsMap;
    }

    /**
     * 获取计算公式代码型指标所拥有的指标项数据
     *
     * @return
     */
    public List getCodeItems(String _itemid) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            _itemid = _itemid.split(":")[0];
            if (_itemid == null || _itemid.length() < 1) {
                map = new HashMap<String, String>();
                map.put("id", "");
                map.put("name", "");
                list.add(map);
                return list;
            }
            ArrayList dylist = null;
            FieldItem fielditem = (FieldItem) DataDictionary.getFieldItem(_itemid);
            String codesetid = "";
            if (fielditem != null) {
                codesetid = fielditem.getCodesetid();
                if (fielditem.isCode() || codesetid.trim().length() > 0) {
                    if (codesetid != null || codesetid.trim().length() > 0) {
                        StringBuffer _sqlstr = new StringBuffer();
                        if ("@K".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "UN".equalsIgnoreCase(codesetid)) {
                            _sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='");
                            _sqlstr.append(codesetid);
                            _sqlstr.append("' order by a0000");
                        } else if ("@@".equalsIgnoreCase(codesetid)) {
                            _sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
                        } else {
                            _sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='");
                            _sqlstr.append(codesetid);
                            _sqlstr.append("' and invalid=1");
                            if (AdminCode.isRecHistoryCode(codesetid)) {//按照是否有效和有效时间来卡住  zhaoxg add 2014-8-14
                                String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
                                _sqlstr.append(" and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ");
                            }
                            _sqlstr.append(" order by a0000");
                        }
                        dylist = dao.searchDynaList(_sqlstr.toString());

                        for (Iterator it = dylist.iterator(); it.hasNext(); ) {
                            DynaBean dynabean = (DynaBean) it.next();
                            String codeitemid = dynabean.get("codeitemid").toString();
                            String codeitemdesc = dynabean.get("codeitemdesc").toString();
                            map = new HashMap<String, String>();
                            map.put("id", codeitemid);
                            map.put("name", codeitemid + ":" + codeitemdesc);
                            list.add(map);
                        }
                        map = new HashMap<String, String>();
                        map.put("id", "");
                        map.put("name", "");
                        list.add(map);
                    } else {
                        map = new HashMap<String, String>();
                        map.put("id", "");
                        map.put("name", "");
                        list.add(map);
                    }
                } else {
                    map = new HashMap<String, String>();
                    map.put("id", "");
                    map.put("name", "");
                    list.add(map);
                }
            } else if (StringUtils.equalsIgnoreCase(_itemid, "")) {

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return list;
    }

    /**
     * 获取计算公式校验所需的fieldlist
     *
     * @return
     */
    private ArrayList getCheckFieldList(UserView userView) throws GeneralException {
        List<Map> fieldList = this.listGzTaxMxField(userView);
        ArrayList<FieldItem> checkFieldList = new ArrayList<FieldItem>();
        FieldItem fieldItem = null;
        for (Map map : fieldList) {
            String itemid = (String) map.get("itemid");
            if (StringUtils.equalsIgnoreCase(itemid, "Nbase")) {
                fieldItem = this.getFieldItem("", "NBASE", ResourceFactory.getProperty("gz_new.gz_nbase"), "A", 3, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "A0100")) {
                fieldItem = this.getFieldItem("", "A0100", ResourceFactory.getProperty("a0100.label"), "A", 8, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "A00Z0")) {
                fieldItem = this.getFieldItem("", "A00Z0", ResourceFactory.getProperty("gz.columns.a00z0"), "D", 20, 4, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "A00Z1")) {
                fieldItem = this.getFieldItem("", "A00Z1", ResourceFactory.getProperty("gz.columns.a00z1"), "N", 15, 0, 5, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "A00Z2")) {
                fieldItem = this.getFieldItem("", "A00Z2", ResourceFactory.getProperty("gz_new.gz_accounting.send_time"), "D", 20, 10, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "A00Z3")) {
                fieldItem = this.getFieldItem("", "A00Z3", ResourceFactory.getProperty("label.gz.count"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "A0000")) {
                fieldItem = this.getFieldItem("", "A0000", ResourceFactory.getProperty("a0000.label"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "YNSSDE")) {
                fieldItem = this.getFieldItem("", "YNSSDE", ResourceFactory.getProperty("mytax.field.ynssde"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "LJSDE")) {
                fieldItem = this.getFieldItem("", "LJSDE", ResourceFactory.getProperty("mytax.field.ljsde"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "LJSE")) {
                fieldItem = this.getFieldItem("", "LJSE", ResourceFactory.getProperty("mytax.field.ljse"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "LJ_BASEDATA")) {
                fieldItem = this.getFieldItem("", "LJ_BASEDATA", ResourceFactory.getProperty("mytax.field.lj_basedata"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "ZNJY")) {
                fieldItem = this.getFieldItem("", "ZNJY", ResourceFactory.getProperty("mytax.field.znjy"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "SYLR")) {
                fieldItem = this.getFieldItem("", "SYLR", ResourceFactory.getProperty("mytax.field.sylr"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "ZFDKLX")) {
                fieldItem = this.getFieldItem("", "ZFDKLX", ResourceFactory.getProperty("mytax.field.zfdklx"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "ZFZJ")) {
                fieldItem = this.getFieldItem("", "ZFZJ", ResourceFactory.getProperty("mytax.field.zfzj"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "JXJY")) {
                fieldItem = this.getFieldItem("", "JXJY", ResourceFactory.getProperty("mytax.field.jxjy"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "TAX_DATE")) {
                fieldItem = this.getFieldItem("", "TAX_DATE", ResourceFactory.getProperty("gz.self.tax.taxdate"), "D", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "TAXMODE")) {
                fieldItem = this.getFieldItem("", "TAXMODE", ResourceFactory.getProperty("gz.columns.taxmode"), "A", 15, 0, 0, "46");
            } else if (StringUtils.equalsIgnoreCase(itemid, "SSKCS")) {
                fieldItem = this.getFieldItem("", "SSKCS", ResourceFactory.getProperty("gz.columns.sskcs"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "BASEDATA")) {
                fieldItem = this.getFieldItem("", "BASEDATA", ResourceFactory.getProperty("gz.columns.basedata"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "SL")) {
                fieldItem = this.getFieldItem("", "SL", ResourceFactory.getProperty("gz.columns.sl"), "N", 15, 0, 0, "0");
            } else if (StringUtils.equalsIgnoreCase(itemid, "SDS")) {
                fieldItem = this.getFieldItem("", "SDS", ResourceFactory.getProperty("gz.self.tax.sds"), "N", 15, 0, 0, "0");
            } else {
                fieldItem = DataDictionary.getFieldItem(itemid);
            }
            if (fieldItem != null) {
                checkFieldList.add(fieldItem);
            }
        }
        return checkFieldList;
    }

    private FieldItem getFieldItem(String fieldsetid, String itemid, String itemdesc, String itemtype, int itemlength, int displaywidth,
                                   int decimalwidth, String codesetid) {
        FieldItem item = new FieldItem();
        item.setFieldsetid(fieldsetid);
        item.setItemid(itemid);
        item.setItemdesc(itemdesc);
        item.setItemtype(itemtype);
        item.setItemlength(itemlength);
        item.setDisplaywidth(displaywidth);
        item.setDecimalwidth(decimalwidth);
        item.setCodesetid(codesetid);
        item.setVarible(0);
        return item;
    }

    /**
     * 获取中间sql语句
     *
     * @param userView  登录用户对象
     * @param year      年份
     * @param tableName 所要使用的表名
     * @return 产生的中间语句
     */
    private String getTempSql(UserView userView, String year, String tableName) {
        StringBuffer sql = new StringBuffer();
        sql.append(" where NBASE=").append("'").append(userView.getDbname()).append("'");
        sql.append(" and A0100=").append("'").append(userView.getA0100()).append("'");
        sql.append(" and flag=1 ");//增加个税表中生效标识
        sql.append(" and ").append(Sql_switcher.year("A00Z0")).append("=").append("'").append(year).append("'");
        return sql.toString();
    }

    public MyTaxServiceImpl(Connection conn) {
        this.conn = conn;
        //类创建时  检查GZ_MYTAX_PARAM字段是否存在 不存在则构建
        try {
            ContentDAO dao = new ContentDAO(conn);
            RecordVo recordVo = ConstantParamter.getRealConstantVo("GZ_MYTAX_PARAM");
            if (recordVo == null) {//如果没有GZ_MYTAX_PARAM属性则新建
                recordVo = new RecordVo("CONSTANT");
                recordVo.setString("constant", "GZ_MYTAX_PARAM");
                recordVo.setString("str_value", "[]");
                dao.addValueObject(recordVo);

            }
            if (StringUtils.isEmpty(recordVo.getString("str_value"))) {// 弥补第一次str_value 赋值成null 导致类型转换异常的问题
                recordVo.setString("str_value", "[]");
                dao.updateValueObject(recordVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取计算公式对应指标类型
     *
     * @param itemType 指标类型
     * @return 对应计算公式中的类型
     */
    private int getVarType(String itemType) {
        int varType = YksjParser.FLOAT; // float
        if ("D".equals(itemType))
            varType = YksjParser.DATEVALUE;
        else if ("A".equals(itemType) || "M".equals(itemType))
            varType = YksjParser.STRVALUE;
        return varType;
    }

    /**
     * 获得个税明细表结构中设置的指标
     *
     * @param gzItems 传递过来的列表 此方法向其追加数据
     */
    private void getGzTaxMxItemList(List gzItems) {
        Document doc;
        try {
            //ConstantXml constantXml = new ConstantXml(this.conn, );不再从缓存中取了因为发现设置之后不刷新缓存
            RecordVo vo = ConstantParamter.getRealConstantVo("GZ_TAX_MX", this.conn);
            StringBuilder temp_xml = new StringBuilder();
            temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
            temp_xml.append("<param>");
            temp_xml.append("</param>");
            String xml = StringUtils.EMPTY;
            if (vo != null)
                xml = vo.getString("str_value");
            if (xml == null || "".equals(xml)) {
                xml = temp_xml.toString();
            }
            doc = PubFunc.generateDom(xml.toString());
            XPath xpath = XPath.newInstance("/param/items");
            Element element = (Element) xpath.selectSingleNode(doc);
            String fieldItem = element == null ? "" : element.getText();
            if (StringUtils.isNotEmpty(fieldItem)) {
                String[] fieldItemArray = fieldItem.split(",");
                for (String tempField : fieldItemArray) {
                    FieldItem field = DataDictionary.getFieldItem(tempField);
                    if (field != null) {
                        String fieldName = field.getItemdesc();
                        Map tempMap = new HashMap();
                        tempMap.put("itemid", tempField);
                        tempMap.put("itemType", field.getItemtype());
                        tempMap.put("itemName", fieldName);
                        gzItems.add(tempMap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据获取的个税明细指标列表获取对应的idlist
     * 方便数据的判定
     *
     * @param gzTaxMxItemList 个税明细指标列表
     * @return 个税itemid列表
     */
    private List getGzTaxMxItemIdList(List gzTaxMxItemList) {
        List<String> gzTaxMxItemIdList = new ArrayList<String>();//itemid列表
        for (Object item : gzTaxMxItemList) {
            Map itemMap = (Map) item;
            String itemId = (String) itemMap.get("itemid");
            gzTaxMxItemIdList.add(itemId);
        }
        return gzTaxMxItemIdList;
    }

    /**
     * @param userView
     * @param list       个税项目集合
     * @param removeFlag 是否从个税项目集合中移除
     * @return 计算公式出错的项目名称
     */
    public String getTaxParamColumn(UserView userView, List list, boolean removeFlag) {
        Iterator item = list.iterator();
        StringBuffer tips = new StringBuffer();
        while (item.hasNext()) {
            JSONObject json = (JSONObject) item.next();
            String itemid = json.getString("itemid");
            String itemType = json.getString("itemType");
            String c_expr = json.getString("calcFormat");
            String name = json.getString("name");
            try {
                String checkFlag = this.checkFormula(userView, c_expr, itemType);
                if (!StringUtils.equals(checkFlag, "ok")) {
                    if (removeFlag) {
                        item.remove();
                    } else {
                        tips.append(name).append(",");
                    }
                }
            } catch (GeneralException e) {
                e.printStackTrace();
            }
        }
        if (tips.length() > 0) {
            tips.setLength(tips.length() - 1);
        }
        return tips.toString();
    }
}
