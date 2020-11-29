package com.hjsj.hrms.actionform.ht.param;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * <p>Title:ContractParamForm.java</p>
 * <p>Description:合同参数/合同信息集</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-03-11 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class ContractParamForm extends FrameForm
{
    //人员库
    private ArrayList nbase = new ArrayList();
    //人员基本情况指标
    private ArrayList empIndex = new ArrayList();

    private String paramStr="";
    //人员子集
    private ArrayList empSubSet = new ArrayList();
    //合同子集
    private String htSubSet="";
    //合同相关子集
    private ArrayList htRelSubSet = new ArrayList();
    //合同标识代码类
    private String httype ="";
    //代码类集
    private ArrayList codeset =  new ArrayList();
    
    /** 可选的字段名数组 */
    private String left_fields[];

    /** 选中的字段名数组 */
    private String right_fields[];
    
    private String returnvalue="1";
    
    
    @Override
    public void inPutTransHM()
    {
	this.getFormHM().put("nbase", this.getNbase());
	this.getFormHM().put("empIndex", this.getEmpIndex());
	this.getFormHM().put("paramStr", this.getParamStr());
	this.getFormHM().put("empSubSet", this.getEmpSubSet());
	this.getFormHM().put("htSubSet", this.getHtSubSet());
	this.getFormHM().put("htRelSubSet", this.getHtRelSubSet());
	this.getFormHM().put("httype", this.getHttype());
	this.getFormHM().put("codeset", this.getCodeset());
	this.getFormHM().put("left_fields", this.getLeft_fields());
	this.getFormHM().put("right_fields", this.getRight_fields());
    }
    @Override
    public void outPutFormHM()
    {
	this.setNbase((ArrayList) this.getFormHM().get("nbase"));
	this.setEmpIndex((ArrayList) this.getFormHM().get("empIndex"));
	this.setParamStr((String) this.getFormHM().get("paramStr"));
	this.setEmpSubSet((ArrayList) this.getFormHM().get("empSubSet"));
	this.setHtSubSet((String) this.getFormHM().get("htSubSet"));
	this.setHtRelSubSet((ArrayList) this.getFormHM().get("htRelSubSet"));
	this.setHttype((String) this.getFormHM().get("httype"));
	this.setCodeset((ArrayList) this.getFormHM().get("codeset"));
	this.setLeft_fields((String[]) this.getFormHM().get("left_fields"));
	this.setRight_fields((String[]) this.getFormHM().get("right_fields"));
    }
    public ArrayList getNbase()
    {    
        return nbase;
    }
    public void setNbase(ArrayList nbase)
    {    
        this.nbase = nbase;
    }
    public ArrayList getEmpIndex()
    {
    
        return empIndex;
    }
    public void setEmpIndex(ArrayList empIndex)
    {
    
        this.empIndex = empIndex;
    }
    public String getParamStr()
    {
    
        return paramStr;
    }
    public void setParamStr(String paramStr)
    {
    
        this.paramStr = paramStr;
    }
    public ArrayList getEmpSubSet()
    {
    
        return empSubSet;
    }
    public void setEmpSubSet(ArrayList empSubSet)
    {
    
        this.empSubSet = empSubSet;
    }
    public ArrayList getHtRelSubSet()
    {
    
        return htRelSubSet;
    }
    public void setHtRelSubSet(ArrayList htRelSubSet)
    {
    
        this.htRelSubSet = htRelSubSet;
    }
    public String getHtSubSet()
    {
    
        return htSubSet;
    }
    public void setHtSubSet(String htSubSet)
    {
    
        this.htSubSet = htSubSet;
    }
    public ArrayList getCodeset()
    {
    
        return codeset;
    }
    public void setCodeset(ArrayList codeset)
    {
    
        this.codeset = codeset;
    }
    public String getHttype()
    {
    
        return httype;
    }
    public void setHttype(String httype)
    {
    
        this.httype = httype;
    }
    public String[] getLeft_fields()
    {
    
        return left_fields;
    }
    public void setLeft_fields(String[] left_fields)
    {
    
        this.left_fields = left_fields;
    }
    public String[] getRight_fields()
    {
    
        return right_fields;
    }
    public void setRight_fields(String[] right_fields)
    {
    
        this.right_fields = right_fields;
    }
    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }
    public String getReturnvalue() {
        return returnvalue;
    }
    
}
