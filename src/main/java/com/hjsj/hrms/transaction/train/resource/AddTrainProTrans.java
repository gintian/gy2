package com.hjsj.hrms.transaction.train.resource;

import java.util.ArrayList;
import java.util.HashMap;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.resource.TrainProjectBo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>
 * Title:AddTrainProTrans.java
 * </p>
 * <p>
 * Description:添加培训项目交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class AddTrainProTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {

        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String priFldValue = (String) hm.get("priFldValue");
        priFldValue = PubFunc.decrypt(SafeCode.decode(priFldValue));
        String code = (String) hm.get("code");
        hm.remove("priFldValue");

        if (code == null || code.length() < 1) {
            code = (String) this.getFormHM().get("code");
        }
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        TrainProjectBo bo = new TrainProjectBo(this.getFrameconn());
        ArrayList fieldList = DataDictionary.getFieldList("r13", Constant.USED_FIELD_SET);
        ArrayList fieldInfoList = new ArrayList();
        TrainCourseBo tb = new TrainCourseBo(this.userView);
        try {
            boolean isNew = false;
            // 新建时候要生成主键
            if (priFldValue == null || priFldValue.equals(""))
            {
                IDGenerator idg = new IDGenerator(2, this.getFrameconn());
                priFldValue = idg.getId("R13.R1301");
                this.getFormHM().put("dispSaveContinue", "true");
                isNew = true;
            } else {
                this.getFormHM().put("dispSaveContinue", "false");
            }
            
            this.getFormHM().put("r1301", SafeCode.encode(PubFunc.encrypt(priFldValue)));

            for (int i = 0; i < fieldList.size(); i++)
            {
                FieldItem fieldItem = (FieldItem) fieldList.get(i);
                String itemid = fieldItem.getItemid();
                String itemName = fieldItem.getItemdesc();
                String itemType = fieldItem.getItemtype();
                String codesetId = fieldItem.getCodesetid();

                FieldItemView fieldItemView = new FieldItemView();
                fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
                fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
                fieldItemView.setCodesetid(codesetId);
                fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
                fieldItemView.setDisplayid(fieldItem.getDisplayid());
                fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
                fieldItemView.setExplain(fieldItem.getExplain());
                fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
                fieldItemView.setItemdesc(itemName);
                fieldItemView.setItemid(itemid);
                fieldItemView.setItemlength(fieldItem.getItemlength());
                fieldItemView.setItemtype(itemType);
                fieldItemView.setModuleflag(fieldItem.getModuleflag());
                fieldItemView.setState(fieldItem.getState());
                fieldItemView.setUseflag(fieldItem.getUseflag());
                fieldItemView.setPriv_status(fieldItem.getPriv_status());
                // 在struts用来表示换行的变量
                fieldItemView.setRowflag(String.valueOf(fieldList.size() - 1)); 
                // 是否为必填项
                fieldItemView.setFillable(fieldItem.isFillable());
                if ("0".equals(fieldItem.getState()) && !"R1301".equalsIgnoreCase(fieldItem.getItemid()) && !"R1302".equalsIgnoreCase(fieldItem.getItemid())
                        && !"B0110".equalsIgnoreCase(fieldItem.getItemid()) && !"R1308".equalsIgnoreCase(fieldItem.getItemid())) {
                    continue;
                }
                
                // 新建
                if (isNew)
                {
                    if (itemid.equals("r1301")) {
                    	fieldItemView.setValue(priFldValue);
                    	//chenxg 用于安全优化：同步到左侧的树中
                        fieldItemView.setViewvalue(SafeCode.encode(PubFunc.encrypt(priFldValue)));
                    } else if ("r1308".equalsIgnoreCase(itemid) && code != null && !code.equals("all")) {
                        String temp = bo.getR1308(code);
                        fieldItemView.setViewvalue(temp);
                        fieldItemView.setValue(code);
                    } else {
                        fieldItemView.setViewvalue("");
                        fieldItemView.setValue("");
                    }
                    if ("b0110".equalsIgnoreCase(itemid)) {
                        String codevalue = "";
                        codevalue = tb.getUnitIdByBusi();
                        this.getFormHM().put("orgparentcode", codevalue);
                    }
                } else
                // 修改
                {
                    StringBuffer strsql = new StringBuffer();
                    strsql.append("select " + itemid + " from r13 where r1301='");
                    strsql.append(priFldValue);
                    strsql.append("'");

                    this.frowset = dao.search(strsql.toString());
                    if (this.frowset.next()) {
                        // 65939 zxj 20200926 日期型数据部能用getString获取，改为标准方法
                        String value = PubFunc.getValueByFieldType(this.frowset, this.frowset.getMetaData(), itemid);
                        if (value == null) {
                            fieldItemView.setViewvalue("");
                            fieldItemView.setValue("");
                        } else {
                            if ("A".equals(itemType) || "M".equals(itemType)) {
                                if (!"0".equals(codesetId)) {
                                    String codevalue = value;
                                    if (codevalue.trim().length() > 0 && codesetId != null && codesetId.trim().length() > 0) {
                                        fieldItemView.setViewvalue(AdminCode.getCode(codesetId, codevalue) != null ? AdminCode.getCode(codesetId, codevalue).getCodename() : "");
                                    } else {
                                        fieldItemView.setViewvalue("");
                                    }
                                    fieldItemView.setValue(value != null ? value.toString() : "");

                                    if ("b0110".equalsIgnoreCase(itemid)) {
                                        String temp = "";
                                        temp = tb.getUnitIdByBusi();
                                        this.getFormHM().put("orgparentcode", temp);
                                        if (value.equalsIgnoreCase("hjsj")) {
                                            fieldItemView.setViewvalue("公共资源");
                                        }
                                    } else if ("r1308".equalsIgnoreCase(itemid) && code != null && !code.equals("all")) {
                                        String temp = bo.getR1308(code);
                                        fieldItemView.setViewvalue(temp);
                                        fieldItemView.setValue(code);
                                    }

                                } else {
                                    if (itemid.equals("r1301")) {
                                        //chenxg 用于安全优化：与左侧的树中的节点的id对应
                                        fieldItemView.setViewvalue(SafeCode.encode(PubFunc.encrypt(value)));
                                        fieldItemView.setValue(value);
                                    } else {
                                        fieldItemView.setViewvalue(value);
                                        fieldItemView.setValue(value);
                                    }
                                }
                            } else if ("D".equals(itemType)) { 
                                // 日期型有待格式化处理
                                if (value != null && value.length() >= 10 && fieldItem.getItemlength() == 10) {
                                    fieldItemView.setViewvalue(new FormatValue().format(fieldItem, value.substring(0, 10)));
                                    fieldItemView.setValue(new FormatValue().format(fieldItem, value.substring(0, 10)));
                                } else if (value != null && value.toString().length() >= 10 && fieldItem.getItemlength() == 4) {
                                    fieldItemView.setViewvalue(new FormatValue().format(fieldItem, value.substring(0, 4)));
                                    fieldItemView.setValue(new FormatValue().format(fieldItem, value.substring(0, 4)));
                                } else if (value != null && value.toString().length() >= 10 && fieldItem.getItemlength() == 7) {
                                    fieldItemView.setViewvalue(new FormatValue().format(fieldItem, value.substring(0, 7)));
                                    fieldItemView.setValue(new FormatValue().format(fieldItem, value.substring(0, 7)));
                                } else {
                                    fieldItemView.setViewvalue("");
                                    fieldItemView.setValue("");
                                }
                            } else {
                                // 数值类型的有待格式化处理
                                fieldItemView.setValue(PubFunc.DoFormatDecimal(value != null ? value.toString() : "", fieldItem.getDecimalwidth()));
                            }
                        }
                    }
                }
                fieldInfoList.add(fieldItemView);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            this.getFormHM().put("fields", fieldInfoList);
        }
    }
}
