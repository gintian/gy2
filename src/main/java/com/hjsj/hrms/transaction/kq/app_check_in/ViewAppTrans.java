/*
 * Created on 2006-2-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.app_check_in.ViewAllApp;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author wxh
 * 
 */
public class ViewAppTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String flag = (String) this.getFormHM().get("flag");
            String table = (String) this.getFormHM().get("table");

            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
            ArrayList class_list = kqUtilsClass.getKqClassList();
            this.getFormHM().put("class_list", class_list);
            String appReaField = kqUtilsClass.getAppReaField(new ContentDAO(frameconn)).toLowerCase();

            String ta = table.toLowerCase();
            ArrayList fieldList = DataDictionary.getFieldList(table, Constant.USED_FIELD_SET);// 字段名
            ArrayList viewlist = new ArrayList();
            for (int j = 0; j < fieldList.size(); j++) {
                FieldItem field_new = new FieldItem();
                FieldItem field = (FieldItem) fieldList.get(j);
                if (field.getItemid().equals(ta + "z3")) {
                    field_new.setItemid(ta + "ld");
                    field_new.setItemdesc("申请时长");
                    field_new.setItemtype("N");
                    field_new.setState("1");
                    field_new.setVisible(true);
                    fieldList.add(j + 1, field_new);
                    break;
                }
            }

            for (int i = 0; i < fieldList.size(); i++) {
                FieldItem field_new = new FieldItem();
                FieldItem field = (FieldItem) fieldList.get(i);
                field.setValue("");
                field.setViewvalue("");
                if ("1".equals(field.getState()))
                    field.setVisible(true);
                else
                    field.setVisible(false);
                /*
                 * if(field.getItemid().equals(ta+"01")||field.getItemid().equals
                 * (
                 * "nbase")||field.getItemid().equals("a0100")||field.getItemid(
                 * ).equals(ta+"09")||field.getItemid().equals(ta+"11")||field.
                 * getItemid
                 * ().equals(ta+"13")||field.getItemid().equals(ta+"15"))
                 * field.setVisible(false); else
                 * if(field.getItemid().equals("q1517"
                 * )||field.getItemid().equals("q1519"))
                 * field.setVisible(false); else field.setVisible(true);
                 */
                if ("q15".equalsIgnoreCase(table) && (ta + "04").equalsIgnoreCase(field.getItemid()))// 请假申请不显示参考班次
                    continue;
                if ("q11".equalsIgnoreCase(table) && (ta + "z4").equalsIgnoreCase(field.getItemid()))// 不现实数据库的申请时长，显示计算的时长
                	continue;
                
                if (field.getItemid().equals(ta + "07"))
                    this.getFormHM().put("visi", ta + "07");
                
                // 部门领导与部门领导意见 如果state=1显示否则不现实
                if (field.getItemid().equals(ta + "09") || field.getItemid().equals(ta + "11") || field.getItemid().equals(ta + "13") || field.getItemid().equals(ta + "15")) {
                    if (!field.isVisible()) {
                        // continue;
                    }
                }
                
                field_new = (FieldItem) field.cloneItem();
                viewlist.add(field_new);
            }
            /* 新增 */
            String tcodeid = "";
            ArrayList infolist = (ArrayList) this.getFormHM().get("selectedinfolist");
            if ("1".equals(flag)) {
                if (infolist.size() == 0)
                    throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("error.kq.notselect"), "", ""));
            }
            
            this.getFormHM().put("infolist", infolist);
            SearchAllApp searchAllApp = new SearchAllApp();
            this.getFormHM().put("salist", searchAllApp.getTableList(table, this.frameconn));
            for (int i = 0; i < viewlist.size(); i++) {
                FieldItem field = (FieldItem) viewlist.get(i);
                field.setValue("");
                field.setViewvalue("");
                if (field.getItemid().equals(ta + "05")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String strDate = sdf.format(new java.util.Date());
                    field.setItemlength(20);
                    field.setValue(strDate);
                }
                
                if (field.getItemid().equals(ta + "11") || field.getItemid().equals(ta + "15")) {
                    tcodeid = codesetidQ(ta, field.getItemid());
                }
                
                // if(field.getItemid().equals(ta+"07")||field.getItemid().equals(ta+"11")||field.getItemid().equals(ta+"15"))
                if (field.getItemid().equals(ta + "07")) {
                    field.setItemtype("M");
                }
                
                if (field.getItemid().equals(ta + "11") || field.getItemid().equals(ta + "15")) {
                    if ("M".equals(tcodeid)) {
                        field.setItemtype("M");
                    } else if ("A".equals(tcodeid)) {
                        if ("q1515".equals(field.getItemid()) || "q1115".equals(field.getItemid()) || "q1315".equals(field.getItemid())) {
                            this.getFormHM().put("salistko", searchAllApp.getOneList15(field.getItemid(), this.getFrameconn()));
                        }
                        if ("q1511".equals(field.getItemid()) || "q1111".equals(field.getItemid()) || "q1311".equals(field.getItemid())) {
                            this.getFormHM().put("salist11", searchAllApp.getOneList11(field.getItemid(), this.getFrameconn()));
                        }
                    }
                }
                // if(field.getItemid().equals("q1503"))
                // {
                // this.getFormHM().put("salist",searchAllApp.getOneList("0",this.getFrameconn()));
                // }
                // if(field.getItemid().equals("q1103"))
                // {
                // this.getFormHM().put("salist",searchAllApp.getOneList("1",this.getFrameconn()));
                // }
                // if(field.getItemid().equals("q1303"))
                // {
                // this.getFormHM().put("salist",searchAllApp.getOneList("3",this.getFrameconn()));
                // }
                if (field.getItemid().equalsIgnoreCase(ta + "09")) {
                    // 部门领导

                    /*
                     * if(this.userView.getManagePrivCode().equalsIgnoreCase("UM"
                     * )&&getIsFunc(table)) {
                     * field.setValue(this.userView.getUserFullName()); }
                     */
                    field.setReadonly(true);
                }
                if (field.getItemid().equalsIgnoreCase(ta + "13")) {
                    // 单位领导
                    /*
                     * if(this.userView.getManagePrivCode().equalsIgnoreCase("UN"
                     * )&&getIsFunc(table)) {
                     * field.setValue(this.userView.getUserFullName()); }
                     */
                    field.setReadonly(true);
                }
                if (field.getItemid().equals(ta + "01") || "nbase".equals(field.getItemid()) || "a0100".equals(field.getItemid()) || "b0110".equals(field.getItemid()) || "e0122".equals(field.getItemid()) || "a0101".equals(field.getItemid()) || field.getItemid().equals(ta + "z5") || field.getItemid().equals(ta + "z0") || "e01a1".equals(field.getItemid()))
                    field.setVisible(false);
                else if ("q1517".equals(field.getItemid()) || "q1519".equals(field.getItemid()))
                    field.setVisible(false);
                /*
                 * else field.setVisible(true);
                 */
                /******
                 * 中建需求***** if(field.getItemid().equals((new
                 * StringBuilder(String
                 * .valueOf(ta))).append("11").toString())||field
                 * .getItemid().equals((new
                 * StringBuilder(String.valueOf(ta))).append("09").toString()) ||
                 * field.getItemid().equals((new
                 * StringBuilder(String.valueOf(ta))).append("13").toString()))
                 * field.setVisible(false);
                 */
                if (field.getItemid().equals(appReaField) && "Q11".equalsIgnoreCase(ta)) {
                    field.setReadonly(true);
                }
            }
            this.getFormHM().put("viewlist", viewlist);
            if ("0".equals(flag)) {
                HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
                String bill_id = (String) hm.get("bill_id");
                /* 查阅单个申请记录 */
                view(table, viewlist, bill_id);
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /* 查阅单个申请记录 */
    private void view(String table, ArrayList fieldlist, String bill_id) throws GeneralException {

        String temp;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String insertname = "";
        String ta = table.toLowerCase();
        String app_type = "";
        String z5 = "";
        String start_d = null;
        String end_d = null;
        String a0100 = null;
        try {
            RecordVo vo = new RecordVo(table.toLowerCase());

            vo.setString(ta + "01", bill_id);
            insertname = ta + "01";

            vo = dao.findByPrimaryKey(vo);
            z5 = vo.getString(ta + "z5");
            if (z5 == null || z5.length() <= 0)
                z5 = "";
            
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem field = (FieldItem) fieldlist.get(i);
                if (field.getItemid().equals(insertname))
                    field.setValue(bill_id);
                else {
                    if ("D".equals(field.getItemtype())) {
                        if (field.getItemid().equals(ta + "z7") && vo.getDate(field.getItemid()) == null) {
                            continue;
                        }
                        
                        if (vo.getDate(field.getItemid()) != null) {
                            if (field.getItemid().equals(ta + "z1")) {
                                start_d = DateUtils.format(vo.getDate(field.getItemid()), "yyyy-MM-dd HH:mm");
                                field.setValue(start_d);// .replace('-','.' )
                            }
                            
                            if (field.getItemid().equals(ta + "z3")) {
                                end_d = DateUtils.format(vo.getDate(field.getItemid()), "yyyy-MM-dd HH:mm");
                                field.setValue(end_d);// .replace('-','.' )
                            }
                            
                            int len = field.getItemlength();
                            if (len > 10) 
							{
                            	field.setValue(DateUtils.format(vo.getDate(field.getItemid()), "yyyy-MM-dd HH:mm"));
							}else 
							{
								String da = DateUtils.format(vo.getDate(field.getItemid()), "yyyy-MM-dd HH:mm");
								da = da.replace("-", ".");
								if (len == 4) 
								{
									da = da.substring(0, 4);
									field.setValue(da);
								}else if (len == 7) 
								{
									da = da.substring(0, 7);
									field.setValue(da);
								}else if (len == 10) 
								{
									da = da.substring(0, 10);
									field.setValue(da);
								}
							}
                                                                                                                // )
                        }
                    } else if ("A".equals(field.getItemtype())) {
                        if ("A0100".equalsIgnoreCase(field.getItemid()))
                            a0100 = vo.getString(field.getItemid().toLowerCase());
                        
                        field.setValue(vo.getString(field.getItemid().toLowerCase()));
                        temp = vo.getString(field.getItemid().toLowerCase());
                        
                        if (field.getItemid().equalsIgnoreCase(table + "11") || field.getItemid().equalsIgnoreCase(table + "15")) {
                            temp = AdminCode.getCode(field.getCodesetid(), temp) != null ? AdminCode.getCode(field.getCodesetid(), temp).getCodename() : "";
                            if (field.getItemid().equalsIgnoreCase(table + "11") && !"0".equals(field.getCodesetid())) {
                                if (temp != null && temp.length() > 0)
                                    this.getFormHM().put("mess2", field.getValue());
                                else
                                    this.getFormHM().put("mess2", "");
                            } else if (field.getItemid().equalsIgnoreCase(table + "15") && !"0".equals(field.getCodesetid())) {
                                if (temp != null && temp.length() > 0)
                                    this.getFormHM().put("mess1", field.getValue());
                                else
                                    this.getFormHM().put("mess1", "");
                            }
                        } else if (temp != null && temp.trim().length() > 0 && !"0".equals(field.getCodesetid())) {
                            temp = AdminCode.getCode(field.getCodesetid(), temp) != null ? AdminCode.getCode(field.getCodesetid(), temp).getCodename() : "";
                        }

                        field.setViewvalue(temp);
                        if ("q1103".equals(field.getItemid()) || "q1303".equals(field.getItemid()) || "q1503".equals(field.getItemid())) {
                            app_type = vo.getString(field.getItemid().toLowerCase());
                        }
                        
                        if (field.getItemid().equalsIgnoreCase(ta + "09")) {
                            // 部门领导
                            /*
                             * if(this.userView.getManagePrivCode().equalsIgnoreCase
                             * ("UM")&&getIsFunc(table)) {
                             * if(field.getValue()==null
                             * ||field.getValue().length()<=0) {
                             * if((!z5.equals("03")&&!z5.equals("07")))
                             * field.setValue(this.userView.getUserFullName());
                             * } }
                             */
                            field.setReadonly(true);
                        }
                        if (field.getItemid().equalsIgnoreCase(ta + "13")) {
                            // 单位领导
                            /*
                             * if(this.userView.getManagePrivCode().equalsIgnoreCase
                             * ("UN")&&getIsFunc(table)) {
                             * if(field.getValue()==null
                             * ||field.getValue().length()<=0) {
                             * if((!z5.equals("03")&&!z5.equals("07")))
                             * field.setValue(this.userView.getUserFullName());
                             * }
                             * 
                             * }
                             */
                            field.setReadonly(true);
                        }
                    } else {
                        if (field.getItemid().equalsIgnoreCase(ta + "ld")) {
                            field.setValue("1");
                        } else {
                            field.setValue(vo.getString(field.getItemid().toLowerCase()));
                        }
                    }
                }
            }
            String isAllow = "true";
            if (vo.getString(ta + "z5") != null && "03".equals(vo.getString(ta + "z5")))
                isAllow = "false";
            /*
             * if(table!=null&&table.equalsIgnoreCase("q15")) { String
             * q1519=vo.getString("q1501"); String
             * sql="select 1 from q15 where q1519='"
             * +q1519+"' and q15z0='01' and q15z5='03' and q1517=1";
             * this.frowset=dao.search(sql); if(this.frowset.next())
             * isAllow="false"; }
             */
            for (int j = 0; j < fieldlist.size(); j++) {
                FieldItem field = (FieldItem) fieldlist.get(j);
                if (field.getItemid().equalsIgnoreCase(ta + "ld")) {
                    ViewAllApp viewAllApp = new ViewAllApp(this.getFrameconn());
                	String duration = viewAllApp.getAppTimeLenDesc(vo, ta, this.userView);
                	
                    field.setValue(duration);
                }
            }
            this.getFormHM().put("isAllow", isAllow);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem field = (FieldItem) fieldlist.get(i);
                if (field.getItemid().equals(ta + "01") || "nbase".equals(field.getItemid()) || "a0100".equals(field.getItemid()) || field.getItemid().equals(ta + "z5") || field.getItemid().equals(ta + "z0") || "e01a1".equals(field.getItemid()))
                    field.setVisible(false);
                else if ("q1517".equals(field.getItemid()) || "q1519".equals(field.getItemid()))
                    field.setVisible(false);
                /*
                 * else field.setVisible(true);
                 */
                if ("b0110".equals(field.getItemid()) || "e0122".equals(field.getItemid()) || "a0101".equals(field.getItemid()))
                    field.setReadonly(true);
                /******
                 * 中建需求**** if(field.getItemid().equals((new
                 * StringBuilder(String
                 * .valueOf(ta))).append("11").toString())||field
                 * .getItemid().equals((new
                 * StringBuilder(String.valueOf(ta))).append("09").toString()) ||
                 * field.getItemid().equals((new
                 * StringBuilder(String.valueOf(ta))).append("13").toString()))
                 * field.setVisible(false);
                 */
            }

            this.getFormHM().put("fieldlist", fieldlist);
            this.getFormHM().put("viewlist", fieldlist);
            this.getFormHM().put("table", table);
            this.getFormHM().put("mess", app_type);
            this.getFormHM().put("z5", z5);
        }

    }

    
    private String codesetidQ(String teble, String itemid) {
        String codesetid = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuffer sql = new StringBuffer();
        RowSet rowSet = null;
        String itemtype = "";
        String codeid = "";
        try {
            sql.append("select itemtype,codesetid from t_hr_busifield  where fieldsetid='" + teble + "' and itemid='" + itemid + "'");
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                itemtype = rowSet.getString("itemtype");
                codeid = rowSet.getString("codesetid");
            }
            
            if ("A".equals(itemtype) && "0".equals(codeid)) {
                codesetid = "M";
            } else {
                codesetid = "A";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rowSet != null)
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return codesetid;
    }

}
