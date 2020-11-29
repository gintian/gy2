package com.hjsj.hrms.module.dashboard.portlets.buildin;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;

public abstract class ListWidgetBase extends IBusiness {

    public abstract List<RecordData> getListData();

    @Override
    public void execute() throws GeneralException {
        List<RecordData> listDatas = this.getListData();
        this.getFormHM().put("listDatas",listDatas);
    }

    protected class RecordData{
        String msg;
        String date;
        String number;
        String detailUrl;

        public RecordData(String msg, String date, String number, String detailUrl) {
            this.msg = msg;
            this.date = date;
            this.number = number;
            this.detailUrl = detailUrl;
        }

        public String getMsg(){
            return this.msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getDetailUrl() {
            return detailUrl;
        }

        public void setDetailUrl(String detailUrl) {
            this.detailUrl = detailUrl;
        }
    }

}
