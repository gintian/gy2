package com.hjsj.hrms.transaction.train.plan;

import com.hjsj.hrms.businessobject.train.TrParamXmlBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hjsj.hrms.businessobject.train.b_plan.PlanTransBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class SearchCreatPlanTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String linkDesc = "";
        if (hm.get("b_query") != null) {
            linkDesc = (String) hm.get("b_query");
            hm.remove("b_query");
        } else if (hm.get("b_query0") != null) {
            linkDesc = (String) hm.get("b_query0");
            hm.remove("b_query0");
        }
        String model = (String) hm.get("model"); // 1:计划制定 2：计划审核
        if ("2".equals(linkDesc)) {
            model = "2";
            linkDesc = "init";
        }

        String trainPlanID = ""; // 培训计划
        ArrayList trainPlanList = new ArrayList(); // 培训计划列表

        String codeid = "";
        String codeset = "";
        String timeFlag = ""; // 显示时间条件 1：全部 2：本年度 3：本季度 4：本月份 5.某时间段
        String startTime = "";
        String endTime = "";
        String stateFlag = ""; // 显示状态条件 显示状态条件 0：所有状态 01：起草状态 03:已批状态 05:执行中状态
                               // 06：结束状态
        TrainPlanBo trainPlanBo = new TrainPlanBo(this.getFrameconn());

        String userDeptId = this.getUserView().getUserDeptId();
        String userOrgId = this.getUserView().getUserOrgId();
        TrainCourseBo tb = new TrainCourseBo(this.userView);
        if ("1".equals(model)) // 1:计划制定
        {

            if (!this.getUserView().isAdmin() && !"1".equals(this.getUserView().getGroupId())) {
                // if(userView.getStatus()==4 || userView.getStatus()==0){
                // codeid=this.getUserView().getManagePrivCodeValue();
                // codeset=this.getUserView().getManagePrivCode();
                // }else{
                // if(this.getUserView().getManagePrivCodeValue().length()==0)
                // {
                // if(userDeptId!=null&&userDeptId.trim().length()>0)
                // {
                // codeid=userDeptId;
                // codeset="UM";
                // }
                // else if(userOrgId!=null&&userOrgId.trim().length()>0)
                // {
                // codeid=userOrgId;
                // codeset="UN";
                // }
                // }
                // else
                // {
                // codeid=this.getUserView().getManagePrivCodeValue();
                // codeset=this.getUserView().getManagePrivCode();
                // }
                //					
                // }
                String tmp = tb.getUnitIdByBusi();
                if (tmp != null && tmp.length() > 2 && tmp.indexOf("`") == -1) {
                    codeid = tmp.split("`")[0].substring(0, 2);
                    codeset = tmp.split("`")[0].substring(2);
                }
            }
            PlanTransBo transbo = new PlanTransBo(this.getFrameconn(), "4");
            this.getFormHM().put("planStateList", transbo.spFlagList());
            this.getFormHM().put("timeConditionList", transbo.timeFlagList0());
        } else // 2：计划审核
        {
            if (linkDesc != null && "link".equals(linkDesc)) {
                // codeid=(String)hm.get("code");
                // codeset=(String)hm.get("codeset");
                String a_code = (String) hm.get("a_code");
                hm.remove("a_code");
                if (a_code != null && a_code.trim().length() > 1) {
                    codeid = a_code.substring(2);
                    codeset = a_code.substring(0, 2);
                }
                // linkDesc="init";
            } else {
                if (hm.get("code") == null) {
                    // if(!this.getUserView().isAdmin()&&!this.getUserView().getGroupId().equals("1"))
                    // {
                    // if(this.getUserView().getManagePrivCodeValue().length()==0)
                    // {
                    // if(userDeptId!=null&&userDeptId.trim().length()>0)
                    // {
                    // codeid=userDeptId;
                    // codeset="UM";
                    // }
                    // else if(userOrgId!=null&&userOrgId.trim().length()>0)
                    // {
                    // codeid=userOrgId;
                    // codeset="UN";
                    // }
                    // }
                    // else
                    // {
                    // codeid=this.getUserView().getManagePrivCodeValue();
                    // codeset=this.getUserView().getManagePrivCode();
                    // }
                    //						
                    // }
                    String tmp = tb.getUnitIdByBusi();
                    if (!this.userView.isSuper_admin() && tmp != null && tmp.length() > 2 && tmp.indexOf("`") == -1) {
                        codeid = tmp.split("`")[0].substring(0, 2);
                        codeset = tmp.split("`")[0].substring(2);
                    }
                } else {
                    codeid = (String) hm.get("code");
                    codeset = (String) hm.get("codeset");
                    if (codeid != null && codeid.indexOf("`") != -1) {
                        codeid = "";
                        codeset = "";
                    }
                }
            }

            if (this.getFormHM().get("trainPlanID") == null || ((String) this.getFormHM().get("trainPlanID")).trim().length() == 0) {
                trainPlanID = "-1";
            } else
                trainPlanID = (String) this.getFormHM().get("trainPlanID");
            
            if (trainPlanID != null && trainPlanID.length() > 0 && !"-1".equalsIgnoreCase(trainPlanID))
                trainPlanID = PubFunc.decrypt(SafeCode.decode(trainPlanID));

            trainPlanID = trainPlanID != null && trainPlanID.length() > 0 && !"0".equals(trainPlanID) ? trainPlanID : "-1";
            trainPlanList = trainPlanBo.getTrainPlanList(this.userView);
            PlanTransBo transbo = new PlanTransBo(this.getFrameconn(), "5");
            this.getFormHM().put("planStateList", transbo.spFlagList());
            this.getFormHM().put("timeConditionList", transbo.timeFlagList0());
        }
        
        String oldmodel = (String) this.getFormHM().get("oldmodel");
        
        if ((linkDesc != null && "init".equalsIgnoreCase(linkDesc)) || !model.equalsIgnoreCase(oldmodel)) {
            timeFlag = "1";
            stateFlag = "00";
        } else {
            timeFlag = (String) this.getFormHM().get("timeFlag");
            timeFlag = timeFlag != null && timeFlag.length() > 0 ? timeFlag : "1";
            startTime = (String) this.getFormHM().get("startTime");
            endTime = (String) this.getFormHM().get("endTime");
            stateFlag = (String) this.getFormHM().get("stateFlag");
            stateFlag = stateFlag != null && stateFlag.length() > 0 ? stateFlag : "00";
        }
        
        if(oldmodel == null || oldmodel.length() < 1 || !model.equalsIgnoreCase(oldmodel))
            oldmodel = model;
        
        ArrayList fieldlist = new ArrayList();
        ArrayList list = DataDictionary.getFieldList("r31", Constant.USED_FIELD_SET);

        TrParamXmlBo trParamXmlBo = new TrParamXmlBo(this.getFrameconn());
        HashMap para_map = trParamXmlBo.getAttributeValues();
        String fieldStr = (String) para_map.get("plan_mx"); // 常量表 参数TR_PARAM (
                                                            // R3121,R3124,R3125)
        if (fieldStr == null) {
            // fieldStr="";
            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.plan.set.activities.item") + "!"));
        } else {
            fieldStr = fieldStr.toLowerCase();
            if (fieldStr.indexOf("r3101") == -1)
                fieldStr = "r3101," + fieldStr;
        }
        HashMap unit_depart_map = trainPlanBo.getUnit_Depart(codeid, codeset, model, this.getUserView());

        GregorianCalendar d = new GregorianCalendar();
        String a_creatData = d.get(Calendar.YEAR) + "." + (d.get(Calendar.MONTH) + 1) + "." + d.get(Calendar.DATE); // 创建日期

        /*
         * if(Sql_switcher.searchDbServer()==Constant.ORACEL) { Field temp=new
         * Field("rownum",ResourceFactory.getProperty("recidx.label"));
         * temp.setNullable(false); temp.setKeyable(false);
         * temp.setDatatype(DataType.INT); temp.setSortable(false);
         * temp.setReadonly(true); fieldlist.add(temp);
         * 
         * 
         * 
         * }
         */

        for (int i = 0; i < list.size(); i++) {
            FieldItem item = (FieldItem) list.get(i);
            Field field = (Field) item.cloneField();
            if (fieldStr.length() > 0) {
                if (fieldStr.indexOf(item.getItemid().toLowerCase()) == -1 && !"b0110".equals(item.getItemid().toLowerCase()) && !"e0122".equals(item.getItemid().toLowerCase())
                        && !"r3127".equalsIgnoreCase(item.getItemid()))
                    continue;
            }

            if ("B0110".equalsIgnoreCase(item.getItemid())) {
                if (unit_depart_map.get("b0110") != null && ((String) unit_depart_map.get("b0110")).length() > 0) {
                    field.setReadonly(true); // 此字段为只读状态
                    field.setValue((String) unit_depart_map.get("b0110"));
                }
                if (fieldStr.length() > 0 && fieldStr.indexOf("b0110") == -1)
                    field.setVisible(false);
            } else if ("e0122".equalsIgnoreCase(item.getItemid())) {
                if (unit_depart_map.get("e0122") != null && ((String) unit_depart_map.get("e0122")).length() > 0) {
                    field.setReadonly(true); // 此字段为只读状态
                    field.setValue((String) unit_depart_map.get("e0122"));

                }
                if (fieldStr.length() > 0 && fieldStr.indexOf("e0122") == -1)
                    field.setVisible(false);
            } else if ("r3125".equalsIgnoreCase(item.getItemid()) || "r3118".equalsIgnoreCase(item.getItemid()) || "r3101".equalsIgnoreCase(item.getItemid())) {
                field.setReadonly(true); // 此字段为只读状态

            } else if ("r3127".equalsIgnoreCase(item.getItemid())) // 单据状态
            {

                field.setValue("01");
                field.setReadonly(true); // 此字段为只读状态
                if (fieldStr.indexOf(item.getItemid().toLowerCase()) == -1)
                    field.setVisible(false);

            } else if ("1".equals(model) && "r3131".equalsIgnoreCase(item.getItemid()))
                field.setReadonly(true);
            else
                field.setReadonly(false);

            if ("r3125".equalsIgnoreCase(item.getItemid())) {
                field.setVisible(false);
                Field field1 = new Field("trainplan");
                field1.setLabel(field.getLabel());
                field1.setLength(field.getLength());
                field1.setCodesetid("0");
                field1.setVisible(true);
                field1.setDatatype(field.getDatatype());
                field1.setReadonly(true);
                fieldlist.add(field1);
            }
            if ("r3101".equalsIgnoreCase(item.getItemid())) {
                field.setVisible(false);
            }
            if ("r3117".equalsIgnoreCase(field.getName()))
                field.setReadonly(true);
            fieldlist.add(field);
        }

        String buttonNames = "";
        if (("1".equals(model) && userView.hasTheFunction("090401")) || ("2".equals(model) && userView.hasTheFunction("090501")))
            buttonNames = "movefirst,prevpage,moveprev,movenext,nextpage,movelast,appendrecord";
        else
            buttonNames = "movefirst,prevpage,moveprev,movenext,nextpage,movelast";

        String extendSql = (String) this.getFormHM().get("extendSql"); // 查询条件
        this.getFormHM().put("extendSql", "");

        extendSql = searchWhere(SafeCode.decode(extendSql));

        String orderSql = (String) this.getFormHM().get("orderSql"); // 排序sql
        orderSql = PubFunc.decrypt(SafeCode.decode(orderSql));
        this.getFormHM().put("orderSql", "");

        String sql = trainPlanBo.getPlanListSql1(this.getUserView(), codeid, codeset, timeFlag, startTime, endTime, stateFlag, model, trainPlanID, extendSql, orderSql, list);

        this.userView.getHm().put("train_sql", sql);
        this.getFormHM().put("buttonNames", buttonNames);
        this.getFormHM().put("ratifyTrainPlanList", trainPlanBo.getRatifyTrainPlanList());
        this.getFormHM().put("trainPlanID", SafeCode.encode(PubFunc.encrypt(trainPlanID)));
        this.getFormHM().put("trainPlanList", trainPlanList);
        this.getFormHM().put("fieldSize", String.valueOf(fieldlist.size()));
        this.getFormHM().put("userName", this.getUserView().getUserName());
        this.getFormHM().put("sql", sql);
        this.getFormHM().put("fieldlist", fieldlist);
        this.getFormHM().put("model", model);
        this.getFormHM().put("linkDesc", linkDesc);
        this.getFormHM().put("codeID", codeid);
        this.getFormHM().put("codeSet", codeset);
        this.getFormHM().put("timeFlag", timeFlag);
        this.getFormHM().put("stateFlag", stateFlag);
        this.getFormHM().put("oldmodel", oldmodel);
        if (!"5".equals(timeFlag)) {
            startTime = "";
            endTime = "";
        }

        this.getFormHM().put("startTime", startTime);
        this.getFormHM().put("endTime", endTime);
    }

    private String searchWhere(String formula) {
        String wherestr = "";
        try {
            TrainCourseBo bo = new TrainCourseBo("r31");
            wherestr = bo.getWhereStr(formula);
            if (wherestr != null && wherestr.length() > 0)
                wherestr = wherestr.substring(4);
            if (wherestr.indexOf("WHERE") != -1)
                wherestr = wherestr.substring(wherestr.indexOf("WHERE") + 5, wherestr.length());
            if (wherestr.indexOf("where") != -1)
                wherestr = wherestr.substring(wherestr.indexOf("where") + 5, wherestr.length());
            wherestr = wherestr.replaceAll("A01", "r31");
        } catch (GeneralException e) {
            // TODO Auto-generated catch block
        }
        return wherestr;
    }
}
