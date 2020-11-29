package com.hjsj.hrms.module.recruitment.position.businessobject;

import java.io.Serializable;



/**
 * 
 * <p>Title: PositionPlan </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-1-23 上午09:51:37</p>
 * @author xiongyy
 * @version 1.0
 */
public class PositionPlan implements Serializable{
    private String name;//方案名
    private String jsMethod;//方案方法  有三个参数(a,b,c)   c就是type名 也就是分类名 如日期类查询 A 状态类查询B 前台有个数组 b 参数代表前台数组的位置 a参数代表数组的值 后台会根据这个数组来查询条件
    private String type;//前台标签的name名字
    private String id;//前台标签的id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PositionPlan(String name,String jsMethod,String type,String id){
        this.name = name;
        this.jsMethod = jsMethod;
        this.type = type;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJsMethod() {
        return jsMethod;
    }

    public void setJsMethod(String jsMethod) {
        this.jsMethod = jsMethod;
    }
    
}
