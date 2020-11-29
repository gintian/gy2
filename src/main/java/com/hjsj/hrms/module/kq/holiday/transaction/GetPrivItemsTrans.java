package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class GetPrivItemsTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String filterItems = (String) this.getFormHM().get("filterItems");
            filterItems = "," + filterItems + ",";
            ArrayList<FieldItem> privItemsList = (ArrayList<FieldItem>)this.getUserView().getPrivFieldList("A01");
            ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
            for (FieldItem fi : privItemsList) {
                if((StringUtils.isNotEmpty(filterItems) && filterItems.contains(fi.getItemid()))
                        || "a0101".equalsIgnoreCase(fi.getItemid()) || "b0110".equalsIgnoreCase(fi.getItemid())
                        || "e0122".equalsIgnoreCase(fi.getItemid()) || "e01a1".equalsIgnoreCase(fi.getItemid()))
                    continue;
                
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("id", fi.getItemid());
                map.put("fieldItemId", fi.getItemid());
                map.put("text", fi.getItemdesc());
                map.put("fieldItemType", fi.getItemtype());
                map.put("fieldSetId", fi.getFieldsetid());
                map.put("checked", false);
                map.put("leaf", "true");
                columnsList.add(map);
            }
            
            this.formHM.put("children", columnsList);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
