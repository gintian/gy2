package com.hjsj.hrms.module.system.distributedreporting.generatedata.generatedatabean;

import java.util.List;

public class MenusBean {
    private String photo;
    private String psn_status;
    private List<SetListBean> set_list;
    private List<VerifyListBean> verify_list;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPsn_status() {
        return psn_status;
    }

    public void setPsn_status(String psn_status) {
        this.psn_status = psn_status;
    }

    public List<SetListBean> getSet_list() {
        return set_list;
    }

    public void setSet_list(List<SetListBean> set_list) {
        this.set_list = set_list;
    }

    public List<VerifyListBean> getVerify_list() {
        return verify_list;
    }

    public void setVerify_list(List<VerifyListBean> verify_list) {
        this.verify_list = verify_list;
    }


}
