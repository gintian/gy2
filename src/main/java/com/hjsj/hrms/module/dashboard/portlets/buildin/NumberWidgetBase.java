package com.hjsj.hrms.module.dashboard.portlets.buildin;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;

public abstract class NumberWidgetBase  extends IBusiness {

    public abstract List<NumberModel> getNumberData();

    @Override
    public void execute() throws GeneralException {
        List numberDatas = this.getNumberData();
        this.getFormHM().put("numberDatas",numberDatas);
    }

    protected class NumberModel{
        String itemName;
        int number =0;
        String unit;
        String style;

        public NumberModel(String itemName, int number, String unit, String style) {
            this.itemName = itemName;
            this.number = number;
            this.unit = unit;
            this.style = style;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }
    }
}
