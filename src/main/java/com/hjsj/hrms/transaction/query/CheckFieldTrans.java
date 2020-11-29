package com.hjsj.hrms.transaction.query;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class CheckFieldTrans extends IBusiness {
    private String fieldSets = ",";
    private String changeFlag = "0";

    public void execute() throws GeneralException {
        String fields = (String) this.getFormHM().get("fields");
        ArrayList<String> list = getFields(fields);
        String flag = "0";
        this.fieldSets = this.fieldSets.substring(1);
        if (StringUtils.isNotEmpty(this.fieldSets)) {
            String[] fieldSet = this.fieldSets.split(",");
            if (fieldSet.length == 1)
                flag = "1";
            else if (fieldSet.length > 1)
                flag = "2";
        }

        this.getFormHM().put("flag", flag);
        if ("1".equals(flag)) {
            this.getFormHM().put("fieldList", list);
            ArrayList<String> yearList = new ArrayList<String>();
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            for(int i = 0; i< 5; i++) {
                yearList.add("" + (year - i));
            }
            
            this.getFormHM().put("yearList", yearList);
            this.getFormHM().put("changeFlag", changeFlag);
        }

    }

    private ArrayList<String> getFields(String fields) {
        ArrayList<String> list = new ArrayList<String>();
        String[] fieldItems = fields.split(",");
        String fieldSetid = "";
        for (int i = 0; i < fieldItems.length; i++) {
            String value = fieldItems[i];
            if (StringUtils.isEmpty(value))
                continue;

            FieldItem fi = DataDictionary.getFieldItem(value);
            if (fi == null)
                continue;

            fieldSetid = fi.getFieldsetid();
            if (!"A01".equals(fieldSetid) && !fieldSets.contains("," + fieldSetid + ","))
                this.fieldSets += fieldSetid + ",";

        }

        if (!"A01".equals(fieldSetid)) {
            ArrayList<FieldItem> itemList = DataDictionary.getFieldList(fieldSetid, Constant.USED_FIELD_SET);
            for (int i = 0; i < itemList.size(); i++) {
                FieldItem fi = itemList.get(i);
                String option = fi.getItemid() + "/" + fi.getFieldsetid() + "/" + fi.getItemtype() + "/"
                        + fi.getCodesetid() + ":" + fi.getItemdesc();
                list.add(option);
            }
            
            FieldSet fieldSet = DataDictionary.getFieldSetVo(fieldSetid);
            this.changeFlag = fieldSet.getChangeflag();
            String fieldSetOption = fieldSetid + ":" + fieldSet.getFieldsetdesc() + "及条件...";
            list.add(fieldSetOption);
        }

        return list;
    }
}
