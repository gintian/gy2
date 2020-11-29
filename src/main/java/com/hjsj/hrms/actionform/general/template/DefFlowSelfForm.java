package com.hjsj.hrms.actionform.general.template;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

/**
 * <p>Title:DefFlowSelfForm.java</p>
 * <p>Description>自定义审批流程:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 26, 2013 5:57:09 PM</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class DefFlowSelfForm extends FrameForm {       
    
    private String fromflag ;//来自哪个模块，card： 卡片模式 list：列表模式
    private String defFlowSelfXml ;//流程记录xml格式
    private PaginationForm defFlowSelfListform = new PaginationForm();//数据bean
    
    
    @Override
    public void inPutTransHM() {
        ;
    }

    public String getDefFlowSelfXml() {
        return defFlowSelfXml;
    }

    public void setDefFlowSelfXml(String defFlowSelfXml) {
        this.defFlowSelfXml = defFlowSelfXml;
    }

    public PaginationForm getDefFlowSelfListform() {
        return defFlowSelfListform;
    }

    public void setDefFlowSelfListform(PaginationForm defFlowSelfListform) {
        this.defFlowSelfListform = defFlowSelfListform;
    }

    @Override
    public void outPutFormHM() {
        this.setFromflag((String)this.getFormHM().get("fromflag"));
        this.setDefFlowSelfXml((String)this.getFormHM().get("defFlowSelfXml"));
        this.getDefFlowSelfListform().setList((ArrayList)this.getFormHM().get("defFlowSelfList"));

    }

    public String getFromflag() {
        return fromflag;
    }

    public void setFromflag(String fromflag) {
        this.fromflag = fromflag;
    }

}
