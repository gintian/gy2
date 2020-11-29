/**
 * 
 */
package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:CourseTransAdd
 * </p>
 * <p>
 * Description:添加培训课程记录
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class CourseTransAdd extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String a_code = (String) hm.get("a_code");
        String id = (String) hm.get("id");
        id = PubFunc.decrypt(SafeCode.decode(id));
        a_code = PubFunc.decrypt(SafeCode.decode(a_code));
        hm.remove("id");
        List itemList = new ArrayList();
        if (id != null && id.trim().length() > 0) {
            TrainCourseBo tbo = new TrainCourseBo(userView, this.frameconn);
            if (!this.userView.isSuper_admin()) {
                this.getFormHM().put("isP", tbo.getCodeIsParent(id) ? "1" : "0");
            } else
                this.getFormHM().put("isP", "0");
        }
        this.getFormHM().put("id", "");
        this.getFormHM().put("a_code", SafeCode.encode(PubFunc.encrypt(a_code)));
        String r5022 = "01";// 审批状态
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            if (id != null && id.trim().length() > 0) {// 修改
                RecordVo vo = new RecordVo("r50");
                vo.setString("r5000", id);
                vo = dao.findByPrimaryKey(vo);
                this.getFormHM().put("id", SafeCode.encode(PubFunc.encrypt(id)));
                r5022 = vo.getString("r5022");
                String imageurl = vo.getString("imageurl");
                if (!"".equals(imageurl)) {
                    if (imageurl.length() > 18) {
                        imageurl = imageurl.substring(imageurl.lastIndexOf("\\") + 1, imageurl.lastIndexOf("\\") + 19) + "...";
                    } else {
                        imageurl = imageurl.substring(imageurl.lastIndexOf("\\") + 1);
                    }
                }
                List fieldList = DataDictionary.getFieldList("R50", Constant.USED_FIELD_SET);
                FieldItemView fieldItemView = null;
                FieldItem fieldItem = null;
                for (int i = 0; i < fieldList.size(); i++) {
                    fieldItemView = new FieldItemView();
                    fieldItem = (FieldItem) fieldList.get(i);
                    if("0".equals(fieldItem.getState()))
                        continue;
                    
                    if ("R5000".equalsIgnoreCase(fieldItem.getItemid())
                            || "R5022".equalsIgnoreCase(fieldItem.getItemid())) {
                        continue;
                    } else if ("R5004".equalsIgnoreCase(fieldItem.getItemid())) {
                        if (vo.getString(fieldItem.getItemid()) == null
                                || vo.getString(fieldItem.getItemid()).length() < 1)
                            fieldItemView.setValue("");
                        else
                            fieldItemView.setValue(vo.getString(fieldItem.getItemid()));
                        if (vo.getString(fieldItem.getItemid()) != null
                                && vo.getString(fieldItem.getItemid()).length() > 0) {
                            RecordVo recordVo = new RecordVo("codeitem");
                            recordVo.setString("codeitemid", vo.getString(fieldItem.getItemid()));
                            recordVo.setString("codesetid", "55");
                            try {
                                recordVo = dao.findByPrimaryKey(recordVo);
                            } catch (Exception e) {
                                e.fillInStackTrace();
                            }
                            String temp = recordVo.getString("codeitemdesc");
                            fieldItemView.setViewvalue(temp);
                        } else
                            fieldItemView.setViewvalue("");

                        fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
                        fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
                        fieldItemView.setCodesetid("55");
                        fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
                        fieldItemView.setDisplayid(fieldItem.getDisplayid());
                        fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
                        fieldItemView.setExplain(fieldItem.getExplain());
                        fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
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
                        fieldItemView.setVisible(false);
                    } else {
                        if ("N".equalsIgnoreCase(fieldItem.getItemtype())) {
                            String value = vo.getString(fieldItem.getItemid());
                            fieldItemView.setValue(PubFunc.round(value, fieldItem.getDecimalwidth()));
                        } else
                            fieldItemView.setValue(vo.getString(fieldItem.getItemid()));

                        if (!"0".equals(fieldItem.getCodesetid())) {
                            if (fieldItem.getCodesetid().indexOf("1_") != -1) {
                                TrainCourseBo bo = new TrainCourseBo(this.frameconn);
                                fieldItemView.setViewvalue(bo.codeFlagToName(fieldItem.getCodesetid(),
                                        vo.getString(fieldItem.getItemid())));
                            } else if ("un".equalsIgnoreCase(fieldItem.getCodesetid())) {
                                RecordVo voCodeItem = new RecordVo("organization");
                                voCodeItem.setString("codesetid", fieldItem.getCodesetid());
                                voCodeItem.setString("codeitemid", vo.getString(fieldItem.getItemid()));
                                if (vo.getString(fieldItem.getItemid()) != null
                                        && vo.getString(fieldItem.getItemid()).length() > 0) {
                                    voCodeItem = dao.findByPrimaryKey(voCodeItem);
                                    fieldItemView.setViewvalue(voCodeItem.getString("codeitemdesc"));
                                } else
                                    fieldItemView.setViewvalue("");
                            } else {
                                RecordVo voCodeItem = new RecordVo("codeitem");
                                voCodeItem.setString("codesetid", fieldItem.getCodesetid());
                                voCodeItem.setString("codeitemid", vo.getString(fieldItem.getItemid()));
                                if (vo.getString(fieldItem.getItemid()) != null
                                        && vo.getString(fieldItem.getItemid()).length() > 0) {
                                    voCodeItem = dao.findByPrimaryKey(voCodeItem);
                                    fieldItemView.setViewvalue(voCodeItem.getString("codeitemdesc"));
                                } else
                                    fieldItemView.setViewvalue("");

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
                this.getFormHM().put("imagename", imageurl);
            } else {// 新增
                List fieldList = DataDictionary.getFieldList("R50", Constant.USED_FIELD_SET);
                FieldItemView fieldItemView = null;
                FieldItem fieldItem = null;
                for (int i = 0; i < fieldList.size(); i++) {
                    fieldItemView = new FieldItemView();
                    fieldItem = (FieldItem) fieldList.get(i);
                    if("0".equals(fieldItem.getState()))
                        continue;
                    
                    if ("R5000".equalsIgnoreCase(fieldItem.getItemid())
                            || "R5022".equalsIgnoreCase(fieldItem.getItemid())) {
                        continue;
                    } else if ("R5004".equalsIgnoreCase(fieldItem.getItemid())) {
                        if (a_code.length() > 0 && !"".equals(a_code)) {
                            fieldItemView.setValue(a_code);
                            try {
                                RecordVo recordVo = new RecordVo("codeitem");
                                recordVo.setString("codeitemid", a_code);
                                recordVo.setString("codesetid", "55");
                                recordVo = dao.findByPrimaryKey(recordVo);
                                a_code = recordVo.getString("codeitemdesc");
                                fieldItemView.setViewvalue(a_code);
                            } catch (Exception e) {
                                fieldItemView.setViewvalue("");
                            }
                        } else
                            fieldItemView.setValue("");
                        fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
                        fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
                        fieldItemView.setCodesetid("55");
                        fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
                        fieldItemView.setDisplayid(fieldItem.getDisplayid());
                        fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
                        fieldItemView.setExplain(fieldItem.getExplain());
                        fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
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
                        fieldItemView.setVisible(false);
                    } else {
                        fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
                        fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
                        fieldItemView.setCodesetid(fieldItem.getCodesetid());
                        fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
                        fieldItemView.setDisplayid(fieldItem.getDisplayid());
                        fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
                        fieldItemView.setExplain(fieldItem.getExplain());
                        fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
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
                this.getFormHM().put("imagename", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        } finally {
            this.getFormHM().put("itemlist", itemList);
            this.getFormHM().put("r5022", r5022);
        }

        String temp = "";
        // if(!userView.isSuper_admin()){
        // if(userView.getStatus()==4)
        // temp=this.getUserView().getManagePrivCodeValue();
        // else{
        // String codeall = userView.getUnit_id();
        // if(codeall!=null&&codeall.length()>2)
        // temp=codeall;//.split("`")[0].substring(2);
        // if("".equals(temp))
        // temp=this.getUserView().getManagePrivCodeValue();
        // }
        // }else
        // temp=this.getUserView().getManagePrivCodeValue();
        if (!userView.isSuper_admin()) {
            TrainCourseBo bo = new TrainCourseBo(this.userView);
            temp = bo.getUnitIdByBusi();
        }
        this.getFormHM().put("orgparentcode", temp);
    }
}
