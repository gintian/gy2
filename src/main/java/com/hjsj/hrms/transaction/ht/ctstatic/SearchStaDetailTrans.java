package com.hjsj.hrms.transaction.ht.ctstatic;

import com.hjsj.hrms.businessobject.ht.ctstatic.CtstaticBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchStaDetailTrans.java
 * </p>
 * <p>
 * Description:查询合同统计详细信息
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-03-18 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchStaDetailTrans extends IBusiness {
    public void execute() throws GeneralException {

        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        // 是否是触发的图例的事件 =tu 图例，null为其他
        String mark = (String) hm.get("mark");
        hm.remove("mark");
        String a_code = (String) hm.get("a_code");
        a_code = a_code != null ? a_code : "";
        hm.remove("a_code");
        this.getFormHM().put("a_code", a_code);

        String dbname = (String) hm.get("dbname");
        dbname = dbname != null ? dbname : "";
        hm.remove("dbname");
        this.getFormHM().put("dbname", dbname);

        String itemid = (String) hm.get("itemid");
        itemid = itemid != null ? itemid : "";
        hm.remove("itemid");
        this.getFormHM().put("itemid", itemid);

        String itemvalue = (String) hm.get("itemvalue");
        itemvalue = itemvalue != null ? SafeCode.decode(itemvalue) : "";
        if ("空".equals(itemvalue)) {
            itemvalue = "no";
        }
        hm.remove("itemvalue");

        FieldItem fielditem = DataDictionary.getFieldItem(itemid);
        String subSet = fielditem.getFieldsetid();
        this.getFormHM().put("subSet", subSet);
//        if (mark != null && "tu".equals(mark) && fielditem.isCode()) {
//            String codeSetid = fielditem.getCodesetid();
//            ArrayList codeList = AdminCode.getCodeItemList(codeSetid);
//            for (int i = 0; i < codeList.size(); i++) {
//                CodeItem codeItem = (CodeItem) codeList.get(i);
//                if (itemvalue.equals(codeItem.getCodename())) {
//                    itemvalue = codeItem.getCodeitem();
//                }
//            }
//        }
        this.getFormHM().put("itemvalue", itemvalue);
        ArrayList mainFlds = new ArrayList();
        ArrayList subFlds = new ArrayList();
        String[] itemSel = (String[]) this.getFormHM().get("right_fields");

        // 按调整的顺序保存列名
        ArrayList columsList = new ArrayList();

        if (itemSel != null) {
            ArrayList fieldsSel = new ArrayList();
            for (int i = 0; i < itemSel.length; i++) {
                String temp = itemSel[i];
                if("NULL".equalsIgnoreCase(temp)){
                    this.getFormHM().remove("right_fields");
                    continue;
                }
                String[] array = temp.split(":");
                if ("A01".equalsIgnoreCase(array[0])) {
                    mainFlds.add(array[1]);

                } else {
                    subFlds.add(array[1]);
                }

                columsList.add(array[1]);

                CommonData temp1 = new CommonData(temp, array[2]);
                fieldsSel.add(temp1);
            }
            this.getFormHM().put("fieldsSel", fieldsSel);
            // this.getFormHM().remove("right_fields");
        }
        ArrayList itemlist = new ArrayList();
        if (mainFlds.size() == 0) {
            itemlist.add("b0110");
            itemlist.add("e0122");
            itemlist.add("e01a1");
            itemlist.add("a0101");

            // 按顺序保存
            columsList.add(0, "a0101");
            columsList.add(0, "e01a1");
            columsList.add(0, "e0122");
            columsList.add(0, "b0110");
        } else {
            for (int i = 0; i < mainFlds.size(); i++)
                itemlist.add(mainFlds.get(i));
        }
        if (subFlds.size() == 0) {
            ArrayList fieldList = DataDictionary.getFieldList(subSet, Constant.USED_FIELD_SET);
            for (int i = 0; i < fieldList.size(); i++)// 循环字段
            {
                FieldItem fieldItem = (FieldItem) fieldList.get(i);
                itemlist.add(fieldItem.getItemid());
                columsList.add(fieldItem.getItemid());
            }
        } else {
            for (int i = 0; i < subFlds.size(); i++)
                itemlist.add(subFlds.get(i));
        }

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        CtstaticBo bo = new CtstaticBo(dao, this.userView);
        String sqlStr = bo.sqlAllStr(dbname, itemid, a_code, itemvalue, columsList);
        ArrayList dataList = bo.getDataList(sqlStr, columsList);
        ArrayList items = bo.getFldCns(columsList);
        this.getFormHM().put("datalist", dataList);
        this.getFormHM().put("items", items);
        this.userView.getHm().put("ht_sql", sqlStr);

    }

}
