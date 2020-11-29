package com.hjsj.hrms.actionform.kq.options.struts;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class KqBusiTemplateMappingForm extends FrameForm {

    private ArrayList mappings = new ArrayList();
    
    @Override
    public void inPutTransHM() {
        this.getFormHM().put("mappings", this.getMappings());
    }

    @Override
    public void outPutFormHM() {
        this.setMappings((ArrayList)this.getFormHM().get("mappings"));
    }
    
    public ArrayList getMappings(){
        return this.mappings;
    }
    
    public void setMappings(ArrayList mappings){
        this.mappings = mappings;
    }

}
