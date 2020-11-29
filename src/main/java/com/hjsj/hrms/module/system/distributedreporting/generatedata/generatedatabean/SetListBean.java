package com.hjsj.hrms.module.system.distributedreporting.generatedata.generatedatabean;

import java.util.List;

public class SetListBean {
    private String set_name;
    private String set_id;
    private List<FielditemListBean> fielditem_list;

    public String getSet_name() {
        return set_name;
    }

    public void setSet_name(String set_name) {
        this.set_name = set_name;
    }

    public String getSet_id() {
        return set_id;
    }

    public void setSet_id(String set_id) {
        this.set_id = set_id;
    }

    public List<FielditemListBean> getFielditem_list() {
        return fielditem_list;
    }

    public void setFielditem_list(List<FielditemListBean> fielditem_list) {
        this.fielditem_list = fielditem_list;
    }


}
