package com.hjsj.hrms.module.system.instructions.businessobject.impl;

import com.hjsj.hrms.module.system.instructions.businessobject.InstructionsService;
import com.hjsj.hrms.module.system.instructions.dao.InstructionsDao;
import com.hjsj.hrms.module.system.instructions.dao.impl.InstructionsDaoImpl;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructionsServiceImpl implements InstructionsService {
    private Connection conn;
    private UserView userView;

    public InstructionsServiceImpl(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    @Override
    /**
     *
     * @Author sheny
     * @return instructionsData
     * @throws GeneralException 异常信息
     * @Date 2020/5/9 13:27
     */
    public Map initInstrucion() throws GeneralException {
        InstructionsDao instructionDao = new InstructionsDaoImpl(this.conn,this.userView);
        //岗位职责说明书是否显示附件
        String PositionAccessoryFlag = instructionDao.getAccessory("PS_CARD_ATTACH");
        //岗位职责说明书显示模板信息
        String PositionValue = instructionDao.getValue("ZP_POS_TEMPLATE");
        //基准岗位说明书是否显示附加
        String StandardAccessoryFlag = instructionDao.getAccessory("PS_C_CARD_ATTACH");
        //基准岗位说明书显示模板信息
        String StandardValue = instructionDao.getValue("ZP_JOB_TEMPLATE");
        //部门岗位说明书是否显示附件
        String DepartmentAccessoryFlag = instructionDao.getAccessory("UNIT_CARD_ATTACH");
        //部门岗位说明书显示模板信息
        String DepartmentValue = instructionDao.getValue("ZP_UNIT_TEMPLATE");
        //部门岗位说明书选择模板数据
        List DepartmentData = instructionDao.getData("B");
        //岗位说明书选择模板信息
        List PositionData = instructionDao.getData("K");
        //基准岗位说明书选择模板信息
        List StandardData = instructionDao.getData("H");

        Map instructionsData = new HashMap();
        instructionsData.put("PositionAccessoryFlag",PositionAccessoryFlag);
        instructionsData.put("PositionValue",PositionValue);
        instructionsData.put("StandardAccessoryFlag",StandardAccessoryFlag);
        instructionsData.put("StandardValue",StandardValue);
        instructionsData.put("DepartmentAccessoryFlag",DepartmentAccessoryFlag);
        instructionsData.put("DepartmentValue",DepartmentValue);
        instructionsData.put("DepartmentData",DepartmentData);
        instructionsData.put("PositionData",PositionData);
        instructionsData.put("StandardData",StandardData);
        return instructionsData;
    }
    /**
     *
     * @Author sheny
     * @param flag department：部门 position：岗位 standard 基准岗位
     * @param accessoryFlag 是否显示附件
     * @param value 显示模板信息
     * @throws GeneralException 异常信息
     * @Date 2020/5/9 13:32
     */
    @Override
    public void saveInstrucion(String flag, String accessoryFlag, String value) throws GeneralException{
        InstructionsDao instructionDao = new InstructionsDaoImpl(this.conn,this.userView);
        //模板编号
        String str_value = "";
        if (value.indexOf("(")!=-1){
            str_value = value.substring(value.indexOf("(")+1,value.indexOf(")"));
        }
        if("department".equalsIgnoreCase(flag)){
            instructionDao.deleteInstruct("ZP_UNIT_TEMPLATE");
            instructionDao.insertInstruct("ZP_UNIT_TEMPLATE","0",str_value ,ResourceFactory.getProperty("System.Instructions.department"));
        } else if("position".equalsIgnoreCase(flag)){
            instructionDao.deleteInstruct("ZP_POS_TEMPLATE");
            instructionDao.insertInstruct("ZP_POS_TEMPLATE","0",str_value , ResourceFactory.getProperty("hire.trust.synopsis"));
            instructionDao.deleteInstruct("PS_CARD_ATTACH");
            instructionDao.insertInstruct("PS_CARD_ATTACH","0",accessoryFlag ,ResourceFactory.getProperty("System.Instructions.position"));
        } else if("standard".equalsIgnoreCase(flag)){
            instructionDao.deleteInstruct("ZP_JOB_TEMPLATE");
            instructionDao.insertInstruct("ZP_JOB_TEMPLATE","0",str_value , ResourceFactory.getProperty("hire.trust.job_synopsis"));
            instructionDao.deleteInstruct("PS_C_CARD_ATTACH");
            instructionDao.insertInstruct("PS_C_CARD_ATTACH","0",accessoryFlag ,ResourceFactory.getProperty("System.Instructions.standard"));
        }
    }
}
