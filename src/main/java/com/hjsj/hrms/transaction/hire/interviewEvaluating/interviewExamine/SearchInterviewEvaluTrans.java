package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewExamine;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchInterviewEvaluTrans.java
 * </p>
 * <p>
 * Description:面试评价
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-05-14 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchInterviewEvaluTrans extends IBusiness {
    public void execute() throws GeneralException {
        ArrayList fieldset = new ArrayList();
        ArrayList fieldName = new ArrayList();
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String a0100 = (String) hm.get("a0100");
            hm.remove("a0100");
            this.getFormHM().put("a0100", a0100);

            String dbName = (String) this.getFormHM().get("dbName");
            dbName = PubFunc.decrypt(dbName);
            
            String subset = (String) this.getFormHM().get("examineNeedRecordSet");
            subset = PubFunc.decrypt(subset);
            
            if (subset != null && subset.trim().length() != 0) {
                ArrayList fieldList = DataDictionary.getFieldList(subset, Constant.USED_FIELD_SET);

                for (int i = 0; i < fieldList.size(); i++) {
                    FieldItem fieldItem = (FieldItem) fieldList.get(i);
                    String itemid = fieldItem.getItemid();
                    String itemName = fieldItem.getItemdesc();
                    String itemType = fieldItem.getItemtype();

                    LazyDynaBean abean = new LazyDynaBean();
                    abean.set("itemdesc", itemName);
                    abean.set("itemid", itemid);
                    abean.set("itemtype", itemType);
                    fieldName.add(abean);
                }

                this.getFormHM().put("fieldName", fieldName);
                ContentDAO dao = new ContentDAO(this.getFrameconn());

                String sql = "select * from " + dbName + subset + " where a0100='" + PubFunc.decrypt(a0100) + "'";
                this.frowset = dao.search(sql);
                while (this.frowset.next()) {
                    LazyDynaBean bean = new LazyDynaBean();
                    bean.set("i9999", this.frowset.getString("i9999"));
                    for (int i = 0; i < fieldList.size(); i++) {
                        FieldItem fieldItem = (FieldItem) fieldList.get(i);
                        String itemid = fieldItem.getItemid();
                        String itemType = fieldItem.getItemtype();
                        String codesetId = fieldItem.getCodesetid();

                        String itemvalue = "";

                        Object val = null;
                        if ("D".equalsIgnoreCase(itemType) && Sql_switcher.searchDbServer() == Constant.ORACEL)// 日期型
                            // oracle数据库必须这样取数据
                            val = this.getFrowset().getDate(itemid);
                        else
                            val = this.frowset.getString(itemid);

                        if (val != null) {
                            if ("A".equals(itemType) || "M".equals(itemType)) {
                                String value = (String) val;
                                if (!"0".equals(codesetId)) {
                                    String codevalue = value;
                                    if (codevalue.trim().length() > 0 && codesetId != null && codesetId.trim().length() > 0)
                                        itemvalue = AdminCode.getCode(codesetId, codevalue) != null ? AdminCode.getCode(
                                                codesetId, codevalue).getCodename() : "";

                                } else
                                    itemvalue = value;

                                //				if("M".equals(itemType) && itemvalue.length()>20)
                                //				    itemvalue=itemvalue.substring(0, 20)+"...";
                            } else if ("D".equals(itemType)) // 日期型有待格式化处理
                            {
                                if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
                                    String value = (String) val;

                                    if (value != null && value.length() >= 10 && fieldItem.getItemlength() == 10) {
                                        value = new FormatValue().format(fieldItem, value.substring(0, 10));
                                        value = PubFunc.replace(value, ".", "-");
                                        itemvalue = value;
                                    } else if (value != null && value.toString().length() >= 10 && fieldItem.getItemlength() == 4) {
                                        value = new FormatValue().format(fieldItem, value.substring(0, 4));
                                        value = PubFunc.replace(value, ".", "-");
                                        itemvalue = value;
                                    } else if (value != null && value.toString().length() >= 10 && fieldItem.getItemlength() == 7) {
                                        value = new FormatValue().format(fieldItem, value.substring(0, 7));
                                        value = PubFunc.replace(value, ".", "-");
                                        itemvalue = value;
                                    }
                                } else if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                                    Date dateVal = (Date) val;
                                    itemvalue = dateVal.toString();
                                }

                            } else
                            // 数值类型的有待格式化处理
                            {
                                String value = (String) val;
                                itemvalue = PubFunc.DoFormatDecimal(value != null ? value.toString() : "", fieldItem
                                        .getDecimalwidth());
                            }
                        }
                        bean.set(itemid, itemvalue);
                    }
                    fieldset.add(bean);
                }
            } else {
                throw GeneralExceptionHandler.Handle(new Exception("请到参数设置中维护面试过程全程记录子集！"));//dml2011年8月22日14:45:20

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            this.getFormHM().put("fieldset", fieldset);
        }
    }

}
