package com.hjsj.hrms.businessobject.stat.crosstab;

/**
 * 统计条件(常用统计条件项)
 * <p>create time 2014.9.11</p>
 * @author genglz
 * @version 1.0
 */
public class StatCond {
    
    private String id;
    
    private String name;
    
    private String expr;
    
    private String factor;

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

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }
    
    
}
