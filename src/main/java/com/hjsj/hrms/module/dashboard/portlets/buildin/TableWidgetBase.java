package com.hjsj.hrms.module.dashboard.portlets.buildin;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.List;

public abstract class TableWidgetBase extends IBusiness {

    public abstract TableModel getChartData();

    @Override
    public void execute() throws GeneralException {
        TableModel tableModel = this.getChartData();
        this.getFormHM().put("columns",tableModel.getColumns());
        this.getFormHM().put("tableDatas",tableModel.getTableDatas());
    }

    protected class TableModel{
        List<ColumnModel> columns;
        List<RecordModel> tableDatas;

        public TableModel() {
        }

        public TableModel(List<ColumnModel> columns, List<RecordModel> tableDatas) {
            this.columns = columns;
            this.tableDatas = tableDatas;
        }

        public List<ColumnModel> getColumns() {
            return columns;
        }

        public void setColumns(List<ColumnModel> columns) {
            this.columns = columns;
        }

        public List<RecordModel> getTableDatas() {
            return tableDatas;
        }

        public void setTableDatas(List<RecordModel> tableDatas) {
            this.tableDatas = tableDatas;
        }
    }
    protected class ColumnModel{
        String label;
        String field;
        String width="";

        public ColumnModel(String label, String field) {
            this.label = label;
            this.field = field;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }
    }
    protected class RecordModel extends HashMap {

        public RecordModel() {
        }

        public void setDetailUrl(String setDetailUrl){
            this.put("detailUrl",setDetailUrl);
        }
    }

}
