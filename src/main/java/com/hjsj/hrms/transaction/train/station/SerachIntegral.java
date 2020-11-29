package com.hjsj.hrms.transaction.train.station;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>
 * SerachIntegral.java
 * </p>
 * <p>
 * Description:查询下拉列表内容与积分管理xml解析
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2012-05-2 10:03:20
 * </p>
 * 
 * @author Xuzhe
 * @version 5.0
 */

public class SerachIntegral extends IBusiness {

    public void execute() throws GeneralException {
        /**
         * subset=积分子集， cur_point_field=可用积分指标， used_point_field=已用积分指标 reg=登记表
         */
        ConstantXml constantbo = new ConstantXml(this.getFrameconn(), "TR_PARAM");
        String subset = constantbo.getNodeAttributeValue("/param/point_set", "subset");
        String cur_point_field = constantbo.getNodeAttributeValue("/param/point_set", "cur_point_field");
        String used_point_field = constantbo.getNodeAttributeValue("/param/point_set", "used_point_field");
        String reg = constantbo.getNodeAttributeValue("/param/em_point_tab", "id");

        this.getFormHM().put("emp_list", getFieldSetList());
        this.getFormHM().put("post_setid", cur_point_field);
        this.getFormHM().put("post_setxid", used_point_field);
        this.getFormHM().put("post_list", getCanSelectedItemList());
        this.getFormHM().put("emp_setid", subset);
        this.getFormHM().put("reg_list", getTabList());
        this.getFormHM().put("reg_setid", reg);
    }

    // 积分子集
    private ArrayList getFieldSetList() {

        ArrayList list = new ArrayList();

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            CommonData aCommonData1 = new CommonData("#", "请选择...");
            list.add(aCommonData1);

            String sql = "select fieldSetid,customdesc from FieldSet where fieldSetid like 'A%' and fieldSetid<>'A01' and useFlag = '1' order by displayorder";
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                String setid = this.frowset.getString("fieldSetid");
                String customdesc = this.frowset.getString("customdesc");

                CommonData aCommonData = new CommonData(setid, customdesc);
                list.add(aCommonData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 可用积分指标 已用积分指标
    private ArrayList getCanSelectedItemList() {
        String setid = (String) this.getFormHM().get("emp_setid");
        ConstantXml constantbo = new ConstantXml(this.getFrameconn(), "TR_PARAM");
        String subset = constantbo.getNodeAttributeValue("/param/point_set", "subset");

        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.getFrameconn());

        try {
            CommonData aCommonData1 = new CommonData("#", "请选择...");
            list.add(aCommonData1);

            String flag = (String) this.getFormHM().get("flag");
            StringBuffer sql = new StringBuffer("select Itemid,itemdesc from FieldItem where fieldsetid=UPPER('");
            if ("9".equals(flag)) {
                sql.append(setid);
            } else {
                sql.append(subset);
            }
            sql.append("') and itemtype='N' and itemdesc<>'次数' and useFlag = '1' order by displayid");

            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                String itemid = this.frowset.getString("Itemid");
                String itemdesc = this.frowset.getString("itemdesc");

                CommonData aCommonData = null;
                aCommonData = new CommonData(itemid, itemdesc);

                list.add(aCommonData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 登记表
    private ArrayList getTabList() {

        ArrayList list = new ArrayList();

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            String sql = "select name,tabid from rname  where flagA='A' order by tabid";
            this.frowset = dao.search(sql);

            CommonData aCommonData1 = new CommonData("#", "请选择...");
            list.add(aCommonData1);
            while (this.frowset.next()) {
                String t = this.frowset.getString("tabid");

                String d = this.frowset.getString("name");

                CommonData aCommonData = null;

                aCommonData = new CommonData(t, d);

                list.add(aCommonData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
