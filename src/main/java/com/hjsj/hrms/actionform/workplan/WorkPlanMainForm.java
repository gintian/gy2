package com.hjsj.hrms.actionform.workplan;

import com.hrms.struts.action.FrameForm;

public class WorkPlanMainForm extends FrameForm {
    
    private String objectid; //团队id 人员usr+人员编号
    private String plantype; //查看的计划类型
    private String periodtype;//期间类型
    private String periodyear;//年
    private String periodmonth;//月 根据期间类型不同代码月份、季度、上半年
    private String periodweek;//周
    private String displayselfplan;//是否显示的是个人计划

    @Override
    public void inPutTransHM() {

        this.getFormHM().put("objectid",this.getObjectid());
    }

    @Override
    public void outPutFormHM() {


    }

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

}
