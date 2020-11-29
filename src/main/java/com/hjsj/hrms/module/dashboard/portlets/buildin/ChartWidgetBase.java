package com.hjsj.hrms.module.dashboard.portlets.buildin;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;

public abstract class ChartWidgetBase extends IBusiness {

    public abstract List<ItemModel> getChartData();

    @Override
    public void execute() throws GeneralException {
        List chartDatas = this.getChartData();
        this.getFormHM().put("chartDatas",chartDatas);
    }

    protected class ItemModel{
        String dataName;
        int dataValue;

        public ItemModel(String dataName, int dataValue) {
            this.dataName = dataName;
            this.dataValue = dataValue;
        }
    }
}
