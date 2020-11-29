package com.hjsj.hrms.module.system.instructions.dao.impl;

import com.hjsj.hrms.module.system.instructions.dao.InstructionsDao;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class InstructionsDaoImpl implements InstructionsDao {

    private ContentDAO dao;

    public InstructionsDaoImpl(Connection conn, UserView userView) {
        this.dao = new ContentDAO(conn);
    }

    @Override
    /**
     *
     * @Author sheny
     * @param String constant 数据库区分部门，岗位，基准岗位唯一标识
     * @return java.lang.String 是否显示附件
     * @throws GeneralException 异常信息
     * @Date 2020/5/9 13:35
     */
    public String getAccessory(String constant) throws GeneralException {
        RowSet frowset = null;
        String accessoryFlag = "";
        try {
            String sql="select str_value from constant where upper(constant)='"+constant+"'";
            frowset=this.dao.search(sql);
            if(frowset.next()){
                accessoryFlag=frowset.getString("str_value");
            }
            if(accessoryFlag==null|| "".equals(accessoryFlag)){
                accessoryFlag="false";
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new GeneralException("getPositionAccessoryError");
        }finally {
            PubFunc.closeResource(frowset);
        }
        return accessoryFlag;
    }

    @Override
    /**
     *
     * @Author sheny
     * @param String constant 数据库区分部门，岗位，基准岗位唯一标识
     * @return java.lang.String 显示模板信息
     * @throws GeneralException 异常信息
     * @Date 2020/5/9 13:36
     */
    public String getValue(String constant) throws GeneralException {
        RowSet frowset = null;
        StringBuffer value = new StringBuffer();
        try {
            String sql="select str_value,name from constant c,rname r where cast(c.str_value as varchar(100))=cast(r.tabid as varchar(100)) and upper(c.constant)='"+constant+"'";
            frowset=this.dao.search(sql);
            if(frowset.next()){
                String describe = frowset.getString("name");
                String str_value = frowset.getString("str_value");
                if (StringUtils.isNotEmpty(str_value)) {
                    value.append("(").append(str_value).append(")").append(describe);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new GeneralException("getPositionValueError");
        }finally {
            PubFunc.closeResource(frowset);
        }
        return value.toString();
    }
    /**
     *
     * @Author sheny
     * @param flagA K:岗位 H:基准岗位 B:部门
     * @return List data 相关模板数据
     * @throws GeneralException 异常信息
     * @Date 2020/5/9 13:38
     */
    @Override
    public List getData(String flagA) throws GeneralException {
        List data = new ArrayList();
        RowSet frowset = null;
        try {
            String sql="select tabid,name from rname where flagA='"+flagA+"'";
            frowset=this.dao.search(sql);
            while(frowset.next()){
                String name = frowset.getString("name");
                String tabid = frowset.getString("tabid");
                StringBuffer value = new StringBuffer();
                value.append("(").append(tabid).append(")").append(name);
                data.add(value.toString());
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new GeneralException("getPositionValueError");
        }finally {
            PubFunc.closeResource(frowset);
        }
        return data;
    }

    @Override
    /**
     *
     * @Author sheny
     * @param String constant 数据库区分部门，岗位，基准岗位唯一标识
     * @return void
     * @throws GeneralException 异常信息
     * @Date 2020/5/9 13:39
     */
    public void deleteInstruct(String constant) throws GeneralException {
        try {
            RecordVo vo = new RecordVo("constant");
            vo.setObject("constant", constant);
            this.dao.deleteValueObject(vo);
        } catch (Exception e) {
            // TODO: handle exception
            throw new GeneralException("deleteInstrctionError");
        }
    }

    @Override
    /**
     *
     * @Author sheny
     * @param String constant 数据库区分部门，岗位，基准岗位唯一标识
     * @param String type
     * @param String str_value 相关信息
     * @param describe 相关信息描述
     * @return void
     * @throws GeneralException 异常信息
     * @Date 2020/5/9 13:39
     */
    public void insertInstruct(String constant, String type, String str_value, String describe) throws GeneralException {
        try {
            RecordVo vo=new RecordVo("constant");
            vo.setString("constant",constant);
            vo.setString("type",type);
            vo.setString("str_value",str_value);
            vo.setString("describe",describe);
            this.dao.addValueObject(vo);
        } catch (Exception e) {
            // TODO: handle exception
            throw new GeneralException("saveInstrctionError");
        }
    }
}
