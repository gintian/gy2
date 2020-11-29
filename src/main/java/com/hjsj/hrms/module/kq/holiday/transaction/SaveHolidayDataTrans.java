package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 保存假期管理数据
 * @Title:        SaveHolidayDataTrans.java
 * @Description:  保存修改后的假期管理数据的交易类
 * @Company:      hjsj     
 * @Create time:  2017年11月15日 上午10:11:12
 * @author        chenxg
 * @version       1.0
 */
public class SaveHolidayDataTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
    	
        try {
            ArrayList dataList=(ArrayList) this.getFormHM().get("savedata");
//            String tablekey = (String) this.getFormHM().get("tablekey");
            int decimal = 0;
            String item_value = "";
            ContentDAO dao = new ContentDAO(this.frameconn);
            StringBuffer updateSql = new StringBuffer("update q17 set ");
            for(int i = 0; i < dataList.size(); i++){
                DynaBean valueBean = (DynaBean) dataList.get(i);
                HashMap<String, Object> ValueMap = PubFunc.DynaBean2Map(valueBean);
                Iterator iter = ValueMap.entrySet().iterator();
                ArrayList<Object> valueList = new ArrayList<Object>();
                ArrayList<String> whereVlaue = new ArrayList<String>();
                ArrayList lists = new ArrayList();
                StringBuffer set_sql = new StringBuffer("");
                StringBuffer whereSql = new StringBuffer(" where ");
                while(iter.hasNext()){
                    Map.Entry<String, Object> entry = (Entry<String, Object>) iter.next();
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    if("a0100_e".equalsIgnoreCase(key))
                        key = "a0100";
                    
                    FieldItem fi = DataDictionary.getFieldItem(key, "q17");
                    if(fi == null)
                        continue;
                    
                    if("a0100".equalsIgnoreCase(key))
                        value = PubFunc.decrypt(value.toString());
                    else if("nbase".equalsIgnoreCase(key) || !"0".equalsIgnoreCase(fi.getCodesetid()))
                        value = value.toString().split("`")[0];
                    
                    if("a0100".equalsIgnoreCase(key) || "nbase".equalsIgnoreCase(key) 
                            || "Q1701".equalsIgnoreCase(key) || "Q1709".equalsIgnoreCase(key)) {
                        
                    	whereSql.append(key + "=? and ");
                        whereVlaue.add(value.toString());
                    } else {
                        
                        String type = fi.getItemtype();
                        if("N".equalsIgnoreCase(type)) {
                        	// 35778 校验数值型为空对象时，默认处理为0
                        	value = null==value ? "0" :value;
                        	value = StringUtils.isEmpty(value.toString()) ? "0" :value;
                            float item_value_f = 0;
                            decimal = fi.getDecimalwidth();
                            item_value = value.toString();
                            item_value = (item_value == null || ".".equals(item_value.trim()) || item_value.length() <= 0) ? "0" : item_value;
                            item_value_f = KQRestOper.round(item_value, decimal);
                            lists.add(key + "=" + item_value_f);
        				}else if("D".equalsIgnoreCase(type)){
        					
        					item_value = value.toString();
                            String item_value_d = "";
                            if (item_value == null || item_value.length() < 8) {
                                item_value_d = "";
                            } else {
                                item_value_d = Sql_switcher.dateValue(item_value);
                            }
                            if (StringUtils.isNotEmpty(item_value_d))
                                lists.add(key + "=" + item_value_d);
        				}
                        // 其他类型暂不做修改
//        				else {
//        					sql.append("'").append(value).append("',");
//        					lists.add(key + "=" + value.toString());
//        					valueList.add(value);
//        				}
                    }
                }
                
                if(whereSql.length() <= 7)
                    continue;
                else if(whereSql.toString().endsWith("and "))
                    whereSql.setLength(whereSql.length() - 4);
                    
                for (int r = 0; r < lists.size(); r++) {
                    set_sql.append(lists.get(r).toString() + ",");
                }
                set_sql.setLength(set_sql.length() - 1);
                
                String sqlall = updateSql.toString() + set_sql.toString() + whereSql.toString();
                dao.update(sqlall, whereVlaue);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }

}
