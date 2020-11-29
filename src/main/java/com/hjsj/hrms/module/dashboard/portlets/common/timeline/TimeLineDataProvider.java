package com.hjsj.hrms.module.dashboard.portlets.common.timeline;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeLineDataProvider extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        // 获取组件相关参数
        JSONObject portletConfig = (JSONObject)this.getFormHM().get("portletConfig");
        String welcome = getWelcome(portletConfig);
        JSONArray postData = getPostData(portletConfig);
        this.getFormHM().put("name",this.userView.getUserFullName());
        this.getFormHM().put("welcome",welcome);
        // 任职经历数据
        this.getFormHM().put("postData",postData);
    }

    /**
     * 获取欢迎信息
     * @param portletConfig
     * @return
     */
    private String getWelcome(JSONObject portletConfig) {
        String nbase = this.userView.getDbname();
        String A0100 = this.userView.getA0100();
        String infoTemp = portletConfig.getJSONObject("params").getString("infoTemp");
        List<String> itemList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        // 匹配以${开头，以}结尾，并且中间没有重复的${}的字符串
        String reg = "\\$\\{[^\\$^\\{^\\}]*\\}";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(infoTemp);
        while (matcher.find()) {
            itemList.add(matcher.group());
            map.put(matcher.group(),matcher.group().split(":")[1].replace("}","").replace(".","_"));
        }
        String item = "";
        String fieldSet = "";
        String fieldItem = "";
        Map<String,String> itemMap = new HashMap<>();
        String itemStr = "";
        for (String key:map.keySet()) {
            item = map.get(key);
            fieldSet =  item.substring(0,item.lastIndexOf("_"));
            fieldItem = item.substring(item.lastIndexOf("_") + 1);
            itemStr = itemMap.get(fieldSet);
            if(itemStr == null){
                itemStr = "";
            }
            itemStr = "".equalsIgnoreCase(itemStr)?fieldItem:itemStr + "," + fieldItem;
            itemMap.put(fieldSet,itemStr);
        }
        String sql = "";
        String joinSql = "";
        String codesetid = null;
        String tableName = "";
        String value = "";
        Map<String,String> valueMap = new HashMap<>();
        ContentDAO dao = new ContentDAO(this.frameconn);
        try{
            for (String key: itemMap.keySet()){
                tableName = nbase + key;
                if(!key.startsWith("A")){
                    tableName = key ;
                }
                itemStr = itemMap.get(key);
                String[] arr = itemStr.split(",");
                sql = "select ";
                joinSql = "";
                for(int x=0;x<arr.length;x++){
                    fieldItem = arr[x];
                    codesetid = DataDictionary.getFieldItem(fieldItem).getCodesetid();
                    if("0".equalsIgnoreCase(codesetid)){
                        sql += "data." + fieldItem + " as " + key + "_" + fieldItem + ",";
                    }else if("UN".equalsIgnoreCase(codesetid)||"UM".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid)){
                        sql += " org" + x + ".codeitemdesc as " + key + "_" + fieldItem + ",";
                        joinSql += " left join organization org"+x+" on org"+x+".codeitemid =data." + fieldItem + "\n";
                    }else {
                        sql += " item" + x + ".codeitemdesc as " + key + "_" + fieldItem + ",";
                        joinSql += " left join (select codeitemid,codeitemdesc from codeitem where codesetid='"+codesetid+"')item"+x+" on item"+x+".codeitemid = data." + fieldItem + "\n";
                    }
                }
                sql = sql.substring(0,sql.length()-1);
                sql += " from " + tableName + " data \n";
                sql += joinSql;
                sql += " where data.A0100='"+A0100+"'";
                if(!"A01".equalsIgnoreCase(key)){
                    sql += " and I9999=(select MAX(I9999) from " + tableName + " where A0100='" + A0100 + "')";
                }
                List<LazyDynaBean> list =  ExecuteSQL.executeMyQuery(sql,this.frameconn);
                for(LazyDynaBean bean : list){
                    for(int x=0;x<arr.length;x++){
                        fieldItem = arr[x];
                        value = (String)bean.get((key + "_" + fieldItem).toLowerCase());
                        if(value.contains("00:00:00")){
                            value = value.replace("00:00:00","").trim();
                        }
                        valueMap.put(key + "_" + fieldItem, value);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        for (String key:map.keySet()) {
            infoTemp = infoTemp.replace(key,valueMap.get(map.get(key)));
        }
        if(infoTemp.startsWith(this.userView.getUserFullName())){
            infoTemp = infoTemp.replaceFirst(this.userView.getUserFullName(),"");
        }
        if(!infoTemp.startsWith("，")){
            infoTemp = "，" + infoTemp;
        }
        return infoTemp;
    }

    /**
     * 获取任职信息
     * @param portletConfig
     * @return
     */
    private JSONArray getPostData(JSONObject portletConfig){
        JSONObject params = portletConfig.getJSONObject("params");
        String timeDataSet = (String)params.getJSONArray("timeDataSet").get(0);
        String pointNameField = (String)params.getJSONArray("pointNameField").get(0);
        String pointStartDateField = (String)params.getJSONArray("pointStartDateField").get(0);
        String pointEndDateField = (String)params.getJSONArray("pointEndDateField").get(0);
        String pointDescField = (String)params.getJSONArray("pointDescField").get(0);
        JSONArray postData = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //保留一位小数，并且四舍五入
        DecimalFormat df = new DecimalFormat("######0.0");
        DecimalFormat df1 = new DecimalFormat("0.00%");
        // 第几年
        double endYear = 0.0;
        List<Map<String,String>> dataList = new ArrayList<>();
        Date pointStartDate = null;
        Date pointEndDate = null;
        ContentDAO dao = new ContentDAO(this.frameconn);
        String sql = getPostSql(timeDataSet,pointNameField,pointStartDateField,pointEndDateField,pointDescField);
        try {
            this.frowset = dao.search(sql);
            while (this.frowset.next()){
                Map<String,String> map = new HashMap<>();
                // 岗位名称
                map.put("post",this.frowset.getString("pointName"));
                // 任职经历描述
                map.put("desc",this.frowset.getString("pointDesc"));
                pointStartDate = this.frowset.getDate("pointStartDate");
                pointEndDate = this.frowset.getDate("pointEndDate");
                // 任职时间范围
                map.put("date",sdf.format(pointStartDate) + "~" + sdf.format(pointEndDate));
                // 计算任职开始日期与结束日期的时间间隔
                Calendar start = Calendar.getInstance();
                start.setTime(pointStartDate);
                Calendar end = Calendar.getInstance();
                end.setTime(pointEndDate);
                double year = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
                double month = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                endYear += Double.valueOf(df.format(year + month/12));
                map.put("year",endYear + "年");
                // 任职时间间隔
                map.put("period",df.format(year + month/12));
                dataList.add(map);
            }
            for(Map map : dataList){
                JSONObject obj = new JSONObject();
                obj.put("post",map.get("post"));
                obj.put("desc",map.get("desc"));
                obj.put("date",map.get("date"));
                obj.put("year",map.get("year"));
                double period = Double.valueOf((String) map.get("period"));
                obj.put("percent",df1.format(endYear > 0 ? period/endYear : 0));
                postData.add(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postData;
    }

    /**
     * 获取任职经历数据sql
     * @param timeDataSet
     * @param pointNameField
     * @param pointStartDateField
     * @param pointEndDateField
     * @param pointDescField
     * @return
     */
    private String getPostSql(String timeDataSet,String pointNameField,String pointStartDateField,String pointEndDateField,String pointDescField){
        String sql = "";
        String nbase = this.userView.getDbname().toUpperCase();
        String A0100 = this.userView.getA0100();
        FieldItem item = DataDictionary.getFieldItem(pointNameField);
        String codesetid = "";
        if(item != null){
            codesetid = item.getCodesetid();
        }
        if("0".equalsIgnoreCase(codesetid)){
            sql += "select data."+pointNameField+" pointName,data."+pointStartDateField+" pointStartDate,data."+pointEndDateField+" pointEndDate,data."+pointDescField+" pointDesc ";
            sql += "\n";
        }else{
            sql += "select data."+pointStartDateField+" pointStartDate,data."+pointEndDateField+" pointEndDate,data."+pointDescField+" pointDesc ";
            sql += ",item.codeitemdesc pointName\n";
        }
        // 判断是视图还是子集
        if(new DbWizard(this.frameconn).isExistTable(timeDataSet)){
            if("0".equalsIgnoreCase(codesetid)){
                sql += "from " + timeDataSet + " data \n";
                sql += "where data.A0100='" + A0100 + "' and upper(nbase)='" + nbase + "'\n";
                sql += "order by data.I9999";
            }else if("@K".equalsIgnoreCase(codesetid)){
                sql += "from " + timeDataSet + " data \n";
                sql += "left join organization item on item.codeitemid = data." + pointNameField + "\n";
                sql += "where data.A0100='" + A0100 + "' and upper(nbase)='" + nbase + "'\n";
                sql += "order by data.I9999";
            }else {
                sql += "from " + timeDataSet + " data \n";
                sql += "left join codeitem item on item.codeitemid = data." + pointNameField + "\n";
                sql += "where data.A0100='" + A0100 + "' and upper(nbase)='" + nbase + "' and item.codesetid='" + codesetid+ "'\n";
                sql += "order by data.I9999";
            }
        }else {
            if("0".equalsIgnoreCase(codesetid)){
                sql += "from " + nbase + timeDataSet + " data \n";
                sql += "where data.A0100='" + A0100 + "' \n";
                sql += "order by data.I9999";
            }else if("@K".equalsIgnoreCase(codesetid)){
                sql += "from " + nbase + timeDataSet + " data \n";
                sql += "left join organization item on item.codeitemid = data." + pointNameField + "\n";
                sql += "where data.A0100='" + A0100 + "' \n";
                sql += "order by data.I9999";
            }else {
                sql += "from " + nbase + timeDataSet + " data \n";
                sql += "left join codeitem item on item.codeitemid = data." + pointNameField + "\n";
                sql += "where data.A0100='" + A0100 + "' and item.codesetid='" + codesetid+ "'\n";
                sql += "order by data.I9999";
            }
        }
        return sql;
    }
}
