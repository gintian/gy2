package com.hjsj.hrms.transaction.train.traincourse;

import com.hjsj.hrms.businessobject.train.TrainAddBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * Title:TrainAddTrans.java
 * </p>
 * <p>
 * Description:培训通用添加
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-08-13 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainAddTrans extends IBusiness {

    public void execute() throws GeneralException {

        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String addCouse = (String) hm.get("addCourse");
        this.getFormHM().put("addCourse", addCouse);
        hm.remove("addCourse");
        String fieldsetid = (String) hm.get("fieldset");
        String priFldValue = (String) hm.get("priFldValue");
        String initValue = (String) hm.get("initValue");// 需要初始化的字段1:初始值1,初始值字段2:初始值2,
        String hideFlds = (String) hm.get("hideFilds");// 不需要显示的字段 r3127,r3128,
        String a_code = (String) hm.get("a_code");
        String itemidarr = (String) hm.get("readonlyFilds");// 只读指标
        // r3127,r3128,
        String hidepics = (String) hm.get("hidepics");// 隐藏字段旁边辅助输入的图片：imgr3127,imgr3128
        if (hidepics == null) {
            hidepics = (String) hm.get("hideimgids");
        }
        String hideSaveFlds = (String) hm.get("hideSaveFlds");
        String isUnUmRela = (String) hm.get("isUnUmRela");
        String classid = (String) hm.get("classid");
        String dbname = "";
        if (hm.get("dbname") != null) {
            dbname = hm.get("dbname").toString();
            dbname = PubFunc.decrypt(SafeCode.decode(dbname));
        }

        hm.remove("dbname");
        hm.remove("priFldValue");
        hm.remove("initValue");
        hm.remove("hideFilds");
        hm.remove("a_code");
        hm.remove("readonlyFilds");
        hm.remove("hidepics");
        hm.remove("hideSaveFlds");
        hm.remove("isUnUmRela");
        hm.remove("classid");

        initValue = SafeCode.decode(initValue); // 初始值是中文名称时候需要转码
        this.getFormHM().put("fieldsetid", isNull(fieldsetid));
        this.getFormHM().put("initValue", isNull(initValue));
        this.getFormHM().put("readonlyFilds", isNull(itemidarr));
        this.getFormHM().put("a_code", isNull(a_code));
        this.getFormHM().put("hideFilds", isNull(hideFlds));
        this.getFormHM().put("hideimgids", isNull(hidepics));
        this.getFormHM().put("isUnUmRela", isUnUmRela == null ? "false" : isUnUmRela);
        this.getFormHM().put("r3101", isNull(classid));

        String[] readonlyFilds = null;
        ArrayList readonlyFilds1 = new ArrayList();
        if (itemidarr != null && !"".equals(itemidarr))
            readonlyFilds = itemidarr.split(",");

        if (readonlyFilds != null)
            for (int j = 0; j < readonlyFilds.length; j++) {
                String temp = readonlyFilds[j];
                if ("".equals(temp))
                    continue;
                LazyDynaBean abean = new LazyDynaBean();
                abean.set("itemid", temp);
                readonlyFilds1.add(abean);
            }
        this.getFormHM().put("itemidarr", readonlyFilds1);

        String[] hidepics1 = null;
        ArrayList hidepics2 = new ArrayList();
        if (hidepics != null && !"".equals(hidepics))
            hidepics1 = hidepics.split(",");

        if (hidepics1 != null)
            for (int j = 0; j < hidepics1.length; j++) {
                String temp = hidepics1[j];
                if ("".equals(temp))
                    continue;
                LazyDynaBean abean = new LazyDynaBean();
                abean.set("imgid", temp);
                hidepics2.add(abean);
            }
        this.getFormHM().put("hidePics", hidepics2);

        TrainAddBo bo = new TrainAddBo(fieldsetid, this.frameconn);

        String b0110 = "";
        String e0122 = "";
        if (a_code != null && a_code.length() > 2) {
            String temp = a_code.substring(0, 2);
            int len = a_code.length();
            if ("UN".equalsIgnoreCase(temp))
                b0110 = a_code.substring(2, len);
            if ("UM".equalsIgnoreCase(temp)) {
                e0122 = a_code.substring(2, len);
                b0110 = getParentCodeValue(e0122);// 获得部门的上级单位
            }
        }

        String primaryField = bo.getPrimaryField();
        String recName = bo.getRecName();// 页面标题
        HashMap initVals = bo.getInitValueMap(initValue);

        this.getFormHM().put("titlename", recName);
        this.getFormHM().put("primaryField", primaryField);// 将主键的字段名存起来

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList fieldList = DataDictionary.getFieldList(fieldsetid, Constant.USED_FIELD_SET);
        ArrayList fieldInfoList = new ArrayList();
        try {
            boolean isNew = false;
            if (priFldValue == null || "".equals(priFldValue))// 新建时候要生成主键
            {
                IDGenerator idg = new IDGenerator(2, this.getFrameconn());
                priFldValue = idg.getId(fieldsetid.toUpperCase() + "." + primaryField.toUpperCase());
                this.getFormHM().put("chkflag", "add");
                isNew = true;
            } else{
                this.getFormHM().put("chkflag", "edit");
                priFldValue = PubFunc.decrypt(SafeCode.decode(priFldValue));
            }

            this.getFormHM().put("priFldValue", SafeCode.encode(PubFunc.encrypt(priFldValue)));// 不管是新增还是修改，都将主键值存起来

            FormatValue formatValue = new FormatValue();
            for (int i = 0; i < fieldList.size(); i++)// 循环字段
            {
                FieldItem fieldItem = (FieldItem) fieldList.get(i);
                // 过滤隐藏的指标
                if ("0".equals(fieldItem.getState()))
                    continue;

                String itemid = fieldItem.getItemid();
                String itemName = fieldItem.getItemdesc();
                String itemType = fieldItem.getItemtype();
                String codesetId = fieldItem.getCodesetid();

                if ("r3702".equalsIgnoreCase(itemid))
                    codesetId = "14";
                if ("r4105".equalsIgnoreCase(itemid))
                    codesetId = "1_06";
                if ("r4106".equalsIgnoreCase(itemid))
                    codesetId = "1_26";
                if ("r4114".equalsIgnoreCase(itemid))
                    codesetId = "1_27";

                // System.out.println(itemid+"--"+itemName+"--"+itemType+"--"+codesetId);
                if (hideFlds != null && hideFlds.length() > 1) {
                    if (hideFlds.indexOf(itemid) != -1)// 不显示的字段
                        continue;
                }

                // 不显示编号字段（利用序号生成器产生的字段值）
                if (itemid.equalsIgnoreCase(primaryField))
                    continue;

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
                // 61820 包含秒的，实际长度应为19位，否则，前台秒无法输入完整
                if("D".equalsIgnoreCase(fieldItem.getItemtype()) && 18==fieldItem.getItemlength()) {
                    fieldItemView.setItemlength(19);
                } else {
                    fieldItemView.setItemlength(fieldItem.getItemlength());
                }
                fieldItemView.setItemtype(itemType);
                fieldItemView.setModuleflag(fieldItem.getModuleflag());
                fieldItemView.setState(fieldItem.getState());
                fieldItemView.setUseflag(fieldItem.getUseflag());
                fieldItemView.setPriv_status(fieldItem.getPriv_status());
                fieldItemView.setRowflag(String.valueOf(fieldList.size() - 1)); // 在struts用来表示换行的变量
                fieldItemView.setFillable(fieldItem.isFillable());// 是否为必填项

                if (isNew)// 新建
                {
                    if (itemid.equals(primaryField)) {
                        fieldItemView.setViewvalue(priFldValue);
                        fieldItemView.setValue(priFldValue);
                    } else {
                        String temp = (String) initVals.get(itemid);// 按默认值进行初始化
                        if (temp == null) {
                            fieldItemView.setViewvalue("");
                            fieldItemView.setValue("");
                        } else {
                            if ("0".equals(codesetId)) {
                                if ("r3125".equals(itemid) || "R3125".equals(itemid))
                                    fieldItemView.setViewvalue(getPlanDesc(temp));
                                else
                                    fieldItemView.setViewvalue(temp);
                                fieldItemView.setValue(temp);
                            } else {
                                fieldItemView.setViewvalue(AdminCode.getCode(codesetId, temp) != null ? AdminCode.getCode(codesetId, temp).getCodename() : "");
                                fieldItemView.setValue(temp);
                            }
                        }
                    }
                    if ("b0110".equalsIgnoreCase(itemid)) {
                        fieldItemView.setViewvalue(AdminCode.getCode("UN", b0110) != null ? AdminCode.getCode(codesetId, b0110).getCodename() : "");
                        fieldItemView.setValue(b0110);
                        String temp = "";
                        TrainCourseBo tb = new TrainCourseBo(this.userView);
                        temp = tb.getUnitIdByBusi();
                        // if(!userView.isSuper_admin()){
                        // if(userView.getStatus()==4)
                        // temp=this.getUserView().getManagePrivCodeValue();
                        // else{
                        // String codeall = userView.getUnit_id();
                        // if(codeall!=null&&codeall.length()>2)
                        // temp=codeall;//.split("`")[0].substring(2);
                        // else if("".equals(temp))
                        // temp=this.getUserView().getManagePrivCodeValue();
                        // }
                        // }else
                        // temp=this.getUserView().getManagePrivCodeValue();
                        this.getFormHM().put("orgparentcode", temp);
                        // this.getFormHM().put("orgparentcode",
                        // userView.getManagePrivCodeValue());
                    }
                    if ("e0122".equalsIgnoreCase(itemid)) {
                        fieldItemView.setViewvalue(AdminCode.getCode("UM", e0122) != null ? AdminCode.getCode(codesetId, e0122).getCodename() : "");
                        fieldItemView.setValue(e0122);
                        if (b0110.length() > 0)
                            this.getFormHM().put("deptparentcode", b0110);
                        else {
                            String temp = "";
                            TrainCourseBo tb = new TrainCourseBo(this.userView);
                            temp = tb.getUnitIdByBusi();
                            // if(!userView.isSuper_admin()){
                            // if(userView.getStatus()==4)
                            // temp=this.getUserView().getManagePrivCodeValue();
                            // else{
                            // String codeall = userView.getUnit_id();
                            // if(codeall!=null&&codeall.length()>2)
                            // temp=codeall;//.split("`")[0].substring(2);
                            // else if("".equals(temp))
                            // temp=this.getUserView().getManagePrivCodeValue();
                            // }
                            // }else
                            // temp=this.getUserView().getManagePrivCodeValue();
                            this.getFormHM().put("deptparentcode", temp.split("`")[0]);
                        }
                    }
                } else
                // 修改
                {
                    ArrayList sqlParams = new ArrayList();
                    StringBuffer strsql = new StringBuffer();
                    strsql.append("select " + itemid);
                    strsql.append(" from " + fieldsetid);
                    strsql.append(" where " + primaryField + "=?");
                    sqlParams.add(priFldValue);
                    if (!"".equals(dbname)) {
                        strsql.append(" and nbase=?");
                        sqlParams.add(dbname);
                    }
                    if ("r40".equalsIgnoreCase(fieldsetid)) {
                        strsql.append(" and r4005=?");
                        sqlParams.add(classid);
                    }

                    this.frowset = dao.search(strsql.toString(), sqlParams);
                    if (this.frowset.next()) {
                        String value = "";
                        if ("D".equals(itemType)) {
                            Date date = this.frowset.getTimestamp(itemid);
                            if (date != null) {
                                value = DateUtils.FormatDate(date, "yyyy.MM.dd HH:mm:ss");
                            } else {
                                value = null;
                            }
                        } else {
                            value = this.frowset.getString(itemid);
                        }
                        if (value == null) {
                            fieldItemView.setViewvalue("");
                            fieldItemView.setValue("");
                        } else {
                            if ("A".equals(itemType) || "M".equals(itemType)) {
                                if (!"0".equals(codesetId)) {
                                    String codevalue = value;
                                    if (codevalue.trim().length() > 0 && codesetId != null && codesetId.trim().length() > 0)
                                        fieldItemView.setViewvalue(AdminCode.getCode(codesetId, codevalue) != null ? AdminCode.getCode(codesetId, codevalue).getCodename() : "");
                                    else
                                        fieldItemView.setViewvalue("");
                                    fieldItemView.setValue(value != null ? value.toString() : "");

                                    if ("b0110".equalsIgnoreCase(itemid)) {
                                        String temp = "";
                                        TrainCourseBo tb = new TrainCourseBo(this.userView);
                                        temp = tb.getUnitIdByBusi();
                                        // if(!userView.isSuper_admin()){
                                        // if(userView.getStatus()==4)
                                        // temp=this.getUserView().getManagePrivCodeValue();
                                        // else{
                                        // String codeall =
                                        // userView.getUnit_id();
                                        // if(codeall!=null&&codeall.length()>2)
                                        // temp=codeall;//.split("`")[0].substring(2);
                                        // else if("".equals(temp))
                                        // temp=this.getUserView().getManagePrivCodeValue();
                                        // }
                                        // }else
                                        // temp=this.getUserView().getManagePrivCodeValue();
                                        this.getFormHM().put("orgparentcode", temp);
                                        // this.getFormHM().put("orgparentcode",
                                        // userView.getManagePrivCodeValue());
                                    }
                                    if ("e0122".equalsIgnoreCase(itemid)) {
                                        TrainCourseBo tb = new TrainCourseBo(this.userView);
                                        String temp = tb.getUnitIdByBusi();
                                        this.getFormHM().put("deptparentcode", temp.split("`")[0]);
                                    }

                                } else {
                                    if ("r3705".equalsIgnoreCase(itemid)) {
                                        fieldItemView.setViewvalue(getChildDesc(classid, priFldValue, value));
                                        fieldItemView.setValue(value);
                                    } else if ("r3125".equals(itemid) || "R3125".equals(itemid)) {
                                        fieldItemView.setViewvalue(getPlanDesc(value));
                                        fieldItemView.setValue(value);
                                    } else {
                                        fieldItemView.setViewvalue(value);
                                        fieldItemView.setValue(value);
                                    }
                                }
                            } else if ("D".equals(itemType)) // 日期型有待格式化处理
                            {
                                if ("r3115".equalsIgnoreCase(itemid)) {
                                    this.getFormHM().put("r3115_time", value.substring(11, 20));
                                }

                                else if ("r3116".equalsIgnoreCase(itemid))
                                    this.getFormHM().put("r3116_time", value.substring(11, 20));

                                // 61820 兼容带时间的数据
                                if (value != null && value.length() >= 10) {
                                    value = formatValue.format(fieldItem, value);
                                    value = PubFunc.replace(value, ".", "-");
                                } else {
                                    value = "";
                                }
                                fieldItemView.setViewvalue(value);
                                fieldItemView.setValue(value);
                            } else
                                // 数值类型的有待格式化处理
                                fieldItemView.setValue(PubFunc.DoFormatDecimal(value != null ? value.toString() : "", fieldItem.getDecimalwidth()));
                        }
                    }

                }
                fieldInfoList.add(fieldItemView);

            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            this.getFormHM().put("fieldlist", fieldInfoList);
        }
    }

    /**
     * 根据部门编码，查找对应的上级单位编码值,通过递归找到上级单位 节点。
     * 
     * @param codevalue
     * @return
     */
    private String getParentCodeValue(String codevalue) {

        String value = "";
        StringBuffer buf = new StringBuffer();
        buf.append("select codeitemid,codesetid,parentid from organization where codeitemid=?");
        ArrayList paralist = new ArrayList();
        paralist.add(codevalue);
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());

            RowSet rset = dao.search(buf.toString(), paralist);
            if (rset.next()) {
                String codeid = rset.getString("codesetid");
                String parentid = rset.getString("parentid");
                
                if (!"UN".equalsIgnoreCase(codeid)) {
                	if(parentid.equalsIgnoreCase(codevalue)) {
                		return "";
                	}
                	
                	value = getParentCodeValue(parentid);
                } else
                    value = rset.getString("codeitemid");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public String isNull(String str) {

        if (str == null)
            return "";
        return str;
    }

    // 加载r3705 liwc
    private String getChildDesc(String classid, String r3701, String id) {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String desc = "";
        String r3702 = "";
        String strSql = "select r3702 from r37 where r3701='" + r3701 + "'";
        try {
            this.frowset = dao.search(strSql);
            if (this.frowset.next())
                r3702 = this.frowset.getString("r3702");
            if (r3702 == null || r3702.length() != 2)
                return "";
            if ("01".equals(r3702))// 教师
                strSql = "Select a.R0402 name from R04 a,R41 b Where a.R0401=b.R4106 and b.R4103='" + classid + "' and a.r0401='" + id + "'";
            else if ("02".equals(r3702))// 机构
                strSql = "Select a.R0102  name from R01 a,R31 b Where a.R0101=b.R3128 and b.R3101='" + classid + "' and a.r0101='" + id + "'";
            else if ("03".equals(r3702))// 资料
                strSql = "Select a.R0702 name from R07 a,R41 b Where a.R0701=b.R4114 and b.R4103='" + classid + "' and a.r0701='" + id + "'";
            else if ("04".equals(r3702))// 场所
                strSql = "Select a.R1011 name from R10 a,R31 b Where a.R1001=b.R3126 and b.R3101='" + classid + "' and a.r1001='" + id + "'";
            else if ("05".equals(r3702))// 项目
                strSql = "Select a.R1302 name from R13 a,R41 b Where a.R1301=b.R4105 and b.R4103='" + classid + "' and a.r1301='" + id + "'";
            this.frowset = dao.search(strSql);
            if (this.frowset.next())
                desc = this.frowset.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return desc;
    }

    /**
     * 获取培训计划名称
     * 
     * @param value
     * @return
     */
    private String getPlanDesc(String value) {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String desc = "";
        String strSql = "select r2502 from R25 where R2501='" + value + "'";
        try {
            this.frowset = dao.search(strSql);
            if (this.frowset.next())
                desc = this.frowset.getString("r2502");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return desc;
    }
}
