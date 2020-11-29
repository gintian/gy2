package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.kq.holiday.businessobject.HolidayBo;
import com.hjsj.hrms.module.kq.util.KqPrivBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 快速查询交易类
 * @Title:        SearchHolidayTrans.java
 * @Description:  假期管理快速查询调用的交易类
 * @Company:      hjsj     
 * @Create time:  2017年11月15日 上午10:12:58
 * @author        chenxg
 * @version       1.0
 */
public class SearchHolidayTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            ArrayList<String> valuesList = new ArrayList<String>();
            String subModuleId = (String) this.getFormHM().get("subModuleId");
            TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(subModuleId);
            if (catche != null) {
                // 拼接sql
                StringBuffer sql = new StringBuffer();
                // 查询类型，1为输入查询，2为方案查询
                String type = (String) this.getFormHM().get("type");
                if ("1".equals(type)) {
                    // 输入的内容
                    valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
                    
                    if (valuesList.size() > 0) {
                        String where = getWhereSql(valuesList);
                        if (!StringUtils.isEmpty(where))
                            sql.append(" and (" + where + ")");
                    }
                    
                } else {
                    sql.append(" and ");
                    String exp = SafeCode.decode(this.getFormHM().get("exp").toString());
                    exp = PubFunc.keyWord_reback(exp);
                    String cond = PubFunc.keyWord_reback(SafeCode.decode(this.getFormHM().get("cond").toString()));
                    if (cond.length() < 1 || exp.length() < 1) {
                        // 查询方案点击全部，刷新保存的快速查询sql
                        if (catche.getCustomParamHM() != null)
                            catche.getCustomParamHM().put("fastQuerySql", "");
                        // 刷新userView中的sql参数
                        catche.setQuerySql("");
                        this.userView.getHm().put(subModuleId, catche);
                        return;
                    }
                    
                    FactorList parser = new FactorList(exp, cond, userView.getUserName());
                    sql.append(parser.getSingleTableSqlExpression("myGridData"));
                }

                catche.setQuerySql(sql.toString());
                //保存快速查询条件备用
                 if(catche.getCustomParamHM()==null)
                     catche.setCustomParamHM(new HashMap<String, String>());
                 
                 catche.getCustomParamHM().put("fastQuerySql", sql.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 拼接 查询条件
     * 
     * @param valuelist
     *            输入框中的输入的值
     * @return
     */
    private String getWhereSql(ArrayList<String> valuelist) {
        if (valuelist == null || valuelist.size() < 1)
            return "";

        StringBuffer where = new StringBuffer();
        HolidayBo bo = new HolidayBo(this.frameconn, this.userView);
        ArrayList<String> dbNameList = KqPrivBo.getB0110Dase(this.userView, this.frameconn);
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
        String pinyin = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
        FieldItem item=DataDictionary.getFieldItem(pinyin.toLowerCase());
        for (int i = 0; i < valuelist.size(); i++) {
            String value = valuelist.get(i);
            if (StringUtils.isEmpty(value))
                continue;

            value = SafeCode.decode(value);
            if (StringUtils.isEmpty(value))
                continue;

            // 34148 快速查询框内多个条件拼接or时 需补充右括号
            if (where != null && where.length() > 1)
                where.append(" or");

            where.append(" A0101 like '%" + value + "%'");
            // 获取部门编号
            String orgids = bo.getOrgIds(value);
            where.append(" or E0122 in ('abc'" + orgids + ")");
            
            if(StringUtils.isNotEmpty(pinyin) && item != null && "1".equalsIgnoreCase(item.getUseflag())) {
                where.append(" or exists (select 1 from (");
                for(int a = 0; a < dbNameList.size(); a++){
                    String dbname = dbNameList.get(a);
                    if(a > 0)
                        where.append(" union ");
                    
                    where.append("select '" + dbname + "' nbase,a0100 from ");
                    where.append(dbname + "a01 where " + pinyin);
                    where.append(" like '%" + value + "%'");
                }
                
                where.append(") b");
                // 34367 查询框输入多个条件时，需把下面where语句分别放入各个条件中
                where.append(" where myGridData.nbase=b.nbase and myGridData.a0100=b.a0100)");
            }
        }


        if (where == null || where.length() < 1)
            where.append("");

        return where.toString();

    }

}
