package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExchangeAddTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String r5701 = (String) hm.get("r5701");
        if (r5701 != null && r5701.length() > 0)
            r5701 = PubFunc.decrypt(r5701);

        r5701 = r5701 == null || r5701.length() < 1 ? "" : r5701;
        hm.remove("r5701");
        List itemList = new ArrayList();
        
        if(!this.userView.isSuper_admin()){
            TrainCourseBo bo = new TrainCourseBo(this.userView);
            String priv = bo.getUnitIdByBusi();
            if(priv != null && priv.length() > 0 && priv.indexOf("UN") == -1)
                throw new GeneralException("",ResourceFactory.getProperty("train.exchangemanage.piv.error"),"",""); 
        }

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            if (r5701 != null && r5701.trim().length() > 0) {// 修改
                RecordVo vo = new RecordVo("r57");
                vo.setString("r5701", r5701);
                vo = dao.findByPrimaryKey(vo);
                List fieldList = DataDictionary.getFieldList("R57", Constant.USED_FIELD_SET);
                FieldItemView fieldItemView = null;
                FieldItem fieldItem = null;
                for (int i = 0; i < fieldList.size(); i++) {
                    fieldItemView = new FieldItemView();
                    fieldItem = (FieldItem) fieldList.get(i);
                    if ("R5701".equalsIgnoreCase(fieldItem.getItemid()) || "R5709".equalsIgnoreCase(fieldItem.getItemid())
                            || "createtime".equalsIgnoreCase(fieldItem.getItemid()) || "createuser".equalsIgnoreCase(fieldItem.getItemid())) {
                        continue;
                    } else if("R5713".equalsIgnoreCase(fieldItem.getItemid())){
                        String r5713 = vo.getString("r5713");
                        r5713 = r5713 == null || r5713.length() < 1 ? "01" : r5713;
                        this.getFormHM().put("editStatus", r5713);
                        continue;
                    } else {
                        fieldItemView.setValue(vo.getString(fieldItem.getItemid()));
                        if (!"0".equals(fieldItem.getCodesetid())) {
                            if ("un".equalsIgnoreCase(fieldItem.getCodesetid())) {
                                String codesetid = fieldItem.getCodesetid();
                                String codeitemid = vo.getString(fieldItem.getItemid());
                                if (vo.getString(fieldItem.getItemid()) != null && vo.getString(fieldItem.getItemid()).length() > 0) {
                                    String sql = "select codeitemdesc from organization where codesetid='" + codesetid + "' and codeitemid='"
                                        + codeitemid + "'";
                                    RowSet rs = dao.search(sql);
                                    if (rs.next()) {
                                        String codeitemdesc = rs.getString("codeitemdesc");
                                        fieldItemView.setViewvalue(codeitemdesc);
                                    } else {
                                        if ("HJSJ".equalsIgnoreCase(vo.getString(fieldItem.getItemid()))) {
                                            // 公共资源
                                            fieldItemView.setViewvalue(ResourceFactory.getProperty("jx.khplan.hjsj"));
                                        } else
                                            // 这是之前有组织机构，但是后来被删除了的情况
                                            fieldItemView.setViewvalue("");
                                    }
                                } else {
                                    // 这是新建的时候就没有选择组织机构的情况
                                    fieldItemView.setViewvalue("");
                                }
                            } else {
                                String codesetid = fieldItem.getCodesetid();
                                String codeitemid = vo.getString(fieldItem.getItemid());
                                if (vo.getString(fieldItem.getItemid()) != null && vo.getString(fieldItem.getItemid()).length() > 0) {
                                    String sql = "select codeitemdesc from codeitem where codesetid='" + codesetid + "' and codeitemid='"
                                        + codeitemid + "'";
                                    RowSet rs = dao.search(sql);
                                    if (rs.next()) {
                                        String codeitemdesc = rs.getString("codeitemdesc");
                                        fieldItemView.setViewvalue(codeitemdesc);
                                    } else {
                                        // 这是之前有这个代码，但是后来被删除了的情况
                                        fieldItemView.setViewvalue("");
                                    }
                                } else {
                                    // 这是新建的时候就没有选择代码的情况
                                    fieldItemView.setViewvalue("");
                                }
                            }
                        }
                        fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
                        fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
                        fieldItemView.setCodesetid(fieldItem.getCodesetid());
                        fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
                        fieldItemView.setDisplayid(fieldItem.getDisplayid());
                        fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
                        fieldItemView.setExplain(fieldItem.getExplain());
                        fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
                        if ("r5707".equalsIgnoreCase(fieldItem.getItemid()))
                            fieldItemView.setItemdesc("数　　量");
                        else
                            fieldItemView.setItemdesc(fieldItem.getItemdesc());
                        // if (!fieldItem.getItemid().equalsIgnoreCase("b0110"))
                        // {
                        fieldItemView.setItemid(fieldItem.getItemid());
                        // }
                        fieldItemView.setItemlength(fieldItem.getItemlength());
                        fieldItemView.setItemtype(fieldItem.getItemtype());
                        fieldItemView.setModuleflag(fieldItem.getModuleflag());
                        fieldItemView.setState(fieldItem.getState());
                        fieldItemView.setUseflag(fieldItem.getUseflag());
                        fieldItemView.setPriv_status(fieldItem.getPriv_status());
                        fieldItemView.setRowflag(String.valueOf(fieldList.size() - 1)); // 在struts用来表示换行的变量
                        fieldItemView.setFillable(fieldItem.isFillable());
                    }
                    itemList.add(fieldItemView.clone());
                }
            } else {// 新增
                List fieldList = DataDictionary.getFieldList("R57", Constant.USED_FIELD_SET);
                FieldItemView fieldItemView = null;
                FieldItem fieldItem = null;
                for (int i = 0; i < fieldList.size(); i++) {
                    fieldItemView = new FieldItemView();
                    fieldItem = (FieldItem) fieldList.get(i);
                    if ("R5701".equalsIgnoreCase(fieldItem.getItemid()) || "R5709".equalsIgnoreCase(fieldItem.getItemid())
                            || "createtime".equalsIgnoreCase(fieldItem.getItemid()) || "createuser".equalsIgnoreCase(fieldItem.getItemid())) {
                        continue;
                    } else if("R5713".equalsIgnoreCase(fieldItem.getItemid())){
                        this.getFormHM().put("editStatus", "01");
                        continue;
                    } else {
                        fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
                        fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
                        fieldItemView.setCodesetid(fieldItem.getCodesetid());
                        fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
                        fieldItemView.setDisplayid(fieldItem.getDisplayid());
                        fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
                        fieldItemView.setExplain(fieldItem.getExplain());
                        fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
                        if ("r5707".equalsIgnoreCase(fieldItem.getItemid()))
                            fieldItemView.setItemdesc("数　　量");
                        else
                            fieldItemView.setItemdesc(fieldItem.getItemdesc());
                        fieldItemView.setItemid(fieldItem.getItemid());
                        fieldItemView.setItemlength(fieldItem.getItemlength());
                        fieldItemView.setItemtype(fieldItem.getItemtype());
                        fieldItemView.setModuleflag(fieldItem.getModuleflag());
                        fieldItemView.setState(fieldItem.getState());
                        fieldItemView.setUseflag(fieldItem.getUseflag());
                        fieldItemView.setPriv_status(fieldItem.getPriv_status());
                        fieldItemView.setRowflag(String.valueOf(fieldList.size() - 1)); // 在struts用来表示换行的变量
                        fieldItemView.setFillable(fieldItem.isFillable());
                    }
                    itemList.add(fieldItemView.clone());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        } finally {
            this.getFormHM().put("itemList", itemList);
        }

        String temp = "";
        if (!userView.isSuper_admin()) {
            TrainCourseBo bo = new TrainCourseBo(this.userView);
            temp = bo.getUnitIdByBusi();
        }
        this.getFormHM().put("r5701", r5701);
        this.getFormHM().put("orgparentcode", temp);
    }

    public String getDescById(String codeitemId, String codesetId) {
        RowSet rs = null;
        String s = "";
        String sql = " select codeitemdesc from organization where codeitemId = '" + codeitemId + "' and codesetid = '" + codesetId + "'";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            rs = dao.search(sql);
            if (rs.next()) {
                s = rs.getString("codeitemdesc").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

}
