package com.hjsj.hrms.transaction.kq.register.history.statfx;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.statfx.RegisterStatBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 点击人员属性信息，展现人员请假详细信息
 * @author Owner
 * wangyao
 */
public class GetUserInFoTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String b0110 = (String) hm.get("b0110"); //功能号
        String itemid = (String) hm.get("itemid"); // 指标号
        String a0100 = (String) hm.get("a0100");
        String scope = (String) hm.get("scope"); //时间
        String usernbase = (String) hm.get("usernbase"); //人员库
        RegisterStatBo register = new RegisterStatBo();
        String getTable = register.getkq_item_sdata(itemid, this.getFrameconn());
        //==0 直接找Q03
        if (getTable == null || "".equals(getTable)) {
            /**标题头 **/
            ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            ArrayList searchfieldlist = RegisterStatBo.setnamelist(itemid, this.getFrameconn(), fielditemlist);

            ArrayList sqllist = register.getTableListnull(scope, a0100, usernbase, itemid, b0110);
            this.getFormHM().put("sqlstrs", sqllist.get(0).toString());
            this.getFormHM().put("strwheres", sqllist.get(1).toString());
            this.getFormHM().put("orderbys", sqllist.get(2).toString());
            this.getFormHM().put("columnss", sqllist.get(3).toString());
            this.getFormHM().put("tableValue", "1");
            this.getFormHM().put("searchfieldlist", searchfieldlist);
        } else {
            String item_id = getTableItem(itemid);
            ArrayList searchfieldlist = new ArrayList();
            ArrayList fieldlist = DataDictionary.getFieldList(getTable, Constant.USED_FIELD_SET);// 字段名
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem field = (FieldItem) fieldlist.get(i);
                if ("b0110".equalsIgnoreCase(field.getItemid()) || "e0122".equalsIgnoreCase(field.getItemid())
                        || "A0101".equalsIgnoreCase(field.getItemid()) || field.getItemid().equalsIgnoreCase(getTable + "03")
                        || field.getItemid().equalsIgnoreCase(getTable + "z1")
                        || field.getItemid().equalsIgnoreCase(getTable + "z3")) {
                    searchfieldlist.add(field);
                }
            }
            ArrayList sqllist = register.getTableList(scope, a0100, usernbase, item_id, b0110, getTable);
            this.getFormHM().put("tableValue", "2"); //用来判断前台页面展现
            this.getFormHM().put("sqlstrs", sqllist.get(0).toString());
            this.getFormHM().put("strwheres", sqllist.get(1).toString());
            this.getFormHM().put("orderbys", sqllist.get(2).toString());
            this.getFormHM().put("columnss", sqllist.get(3).toString());
            this.getFormHM().put("searchfieldlist", searchfieldlist);
        }
    }

    private String getTableItem(String itemid) {
        String itemids = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String sql = "select item_id from kq_item where upper(fielditemid)='" + itemid.toUpperCase() + "'";
        RowSet rowSet = null;
        try {
            rowSet = dao.search(sql);
            if (rowSet.next()) {
                itemids = rowSet.getString("item_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return itemids;
    }
}
