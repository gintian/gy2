package com.hjsj.hrms.businessobject.stat.crosstab;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计维度(一维常用统计/代码指标)
 * <p>create time 2014.9.11</p>
 * @author genglz
 * @version 1.0
 */
public class StatItem {

    /** 常用统计 */
    private static final int ITEM_TYPE_STAT = 0;
    
    /** 代码指标 */
    private static final int ITEM_TYPE_MENU = 1;
    
    private int type = ITEM_TYPE_STAT;
    
    /** 编号 */
    private String id;
    
    private String name;
    
    /** 统计条件/关联代码 */
    private ArrayList conds = new ArrayList();
    
    /** 二级维度 */
    private ArrayList subItems = new ArrayList();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 加载数据
     * @param id
     * @return 成功true, 失败false
     */
    public boolean load(String id) {
        setId(id);
        List recs = ExecuteSQL.executeMyQuery("select * from SName where id=" + id + " order by id");
        String s = (String)((LazyDynaBean)recs.get(0)).get("name");
        setName(s != null?s.trim():"");
        loadConds();
        return true;
    }
    
    /**
     * 加载条件
     */
    private void loadConds() {
        conds.clear();
        String sql = "select * from SLegend where id=" + id + " order by norder";
        List recs = ExecuteSQL.executeMyQuery(sql);
        for (int j = 0; j < recs.size(); j++) {
            LazyDynaBean rec=(LazyDynaBean)recs.get(j);
            StatCond cond = new StatCond();
            cond.setId(rec.get("norder").toString());
            cond.setName(rec.get("legend").toString());
            cond.setFactor(rec.get("factor")!=null?rec.get("factor").toString().trim() + "`":"");
            cond.setExpr(rec.get("lexpr")!=null?rec.get("lexpr").toString().trim():"");
            conds.add(cond);
        }
    }
    
    public void clearSubItems() {
        subItems.clear(); 
    }
    
    public void addSubItem(StatItem item) {
        subItems.add(item);
    }
    
    public boolean hasSubItems() {
        return subItems.size() > 0;
    }

    public ArrayList getConds() {
        return conds;
    }

    public ArrayList getSubItems() {
        return subItems;
    }
    
}
