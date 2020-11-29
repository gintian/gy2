package com.hjsj.hrms.businessobject.stat.crosstab;

/**
 * 交叉表的一行或一列
 * <p>create time 2014.9.12</p>
 * @author genglz
 * @version 1.0
 */
public class CrossTabItem {
    
    /** 一级维度 */
    private StatItem level1;
    
    /** 一级条件 */
    private StatCond cond1;

    /** 二级维度(可为空) */
    private StatItem level2;
    
    /** 二级条件(可为空) */
    private StatCond cond2;

    public StatItem getLevel1() {
        return level1;
    }

    public void setLevel1(StatItem level1) {
        this.level1 = level1;
    }

    public StatCond getCond1() {
        return cond1;
    }

    public void setCond1(StatCond cond1) {
        this.cond1 = cond1;
    }

    public StatItem getLevel2() {
        return level2;
    }

    public void setLevel2(StatItem level2) {
        this.level2 = level2;
    }

    public StatCond getCond2() {
        return cond2;
    }

    public void setCond2(StatCond cond2) {
        this.cond2 = cond2;
    }

    
}
