package com.hjsj.hrms.actionform.hire.demandPlan;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * <p>Title:HireOrderForm.java</p>
 * <p>Description:招聘订单</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-05-11 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class HireOrderForm extends FrameForm
{

    private String a_code;
    private String startDate;
    private String endDate;
    private String queryItem;
    private String queryValue;
    private ArrayList queryItemList = new ArrayList();
    private ArrayList fieldlist = new ArrayList();
    private String sql;
    private String startNum;
    private String endNum;
    private String codeValue;
    private String codeValue2;
    private String paramStr;
    private String orderid;
    private ArrayList fieldslist = new ArrayList();
    private String endFlag = "0";
    private String delFlag="0";
    
    @Override
    public void inPutTransHM()
    {
	this.getFormHM().put("a_code", this.getA_code());
	this.getFormHM().put("startDate", this.getStartDate());
	this.getFormHM().put("endDate", this.getEndDate());
	this.getFormHM().put("queryItem", this.getQueryItem());
	this.getFormHM().put("queryValue", this.getQueryValue());
	this.getFormHM().put("queryItemList", this.getQueryItemList());
	this.getFormHM().put("fieldlist", this.getFieldlist());
	this.getFormHM().put("startNum", this.getStartNum());
	this.getFormHM().put("endNum", this.endNum);
	this.getFormHM().put("sql", this.getSql());
	this.getFormHM().put("codeValue", this.getCodeValue());
	this.getFormHM().put("codeValue2", this.getCodeValue2());
	this.getFormHM().put("paramStr", this.getParamStr());
	this.getFormHM().put("orderid", this.getOrderid());
	this.getFormHM().put("fieldslist", this.getFieldslist());
	this.getFormHM().put("endFlag", endFlag);
	this.getFormHM().put("delFlag", delFlag);
    }

    @Override
    public void outPutFormHM()
    {
	this.setA_code((String) this.getFormHM().get("a_code"));
	this.setStartDate((String) this.getFormHM().get("startDate"));
	this.setEndDate((String) this.getFormHM().get("endDate"));
	this.setQueryItem((String) this.getFormHM().get("queryItem"));
	this.setQueryValue((String) this.getFormHM().get("queryValue"));
	this.setQueryItemList((ArrayList) this.getFormHM().get("queryItemList"));
	this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
	this.setSql((String) this.getFormHM().get("sql"));
	this.setStartNum((String) this.getFormHM().get("startNum"));
	this.setEndNum((String) this.getFormHM().get("endNum"));
	this.setCodeValue((String) this.getFormHM().get("codeValue"));
	this.setCodeValue2((String) this.getFormHM().get("codeValue2"));
	this.setParamStr((String) this.getFormHM().get("paramStr"));
	this.setOrderid((String) this.getFormHM().get("orderid"));
	this.setFieldslist((ArrayList) this.getFormHM().get("fieldslist"));
	this.setEndFlag((String) this.getFormHM().get("endFlag"));
	this.setDelFlag((String) this.getFormHM().get("delFlag"));
    }

    public String getA_code()
    {
    
        return a_code;
    }

    public void setA_code(String a_code)
    {
    
        this.a_code = a_code;
    }

    public String getEndDate()
    {
    
        return endDate;
    }

    public void setEndDate(String endDate)
    {
    
        this.endDate = endDate;
    }

    public ArrayList getFieldlist()
    {
    
        return fieldlist;
    }

    public void setFieldlist(ArrayList fieldlist)
    {
    
        this.fieldlist = fieldlist;
    }

    public String getQueryItem()
    {
    
        return queryItem;
    }

    public void setQueryItem(String queryItem)
    {
    
        this.queryItem = queryItem;
    }

    public ArrayList getQueryItemList()
    {
    
        return queryItemList;
    }

    public void setQueryItemList(ArrayList queryItemList)
    {
    
        this.queryItemList = queryItemList;
    }

    public String getQueryValue()
    {
    
        return queryValue;
    }

    public void setQueryValue(String queryValue)
    {
    
        this.queryValue = queryValue;
    }


    public String getSql()
    {
    
        return sql;
    }

    public void setSql(String sql)
    {
    
        this.sql = sql;
    }

    public String getStartDate()
    {
    
        return startDate;
    }

    public void setStartDate(String startDate)
    {
    
        this.startDate = startDate;
    }

    public String getEndNum()
    {
    
        return endNum;
    }

    public void setEndNum(String endNum)
    {
    
        this.endNum = endNum;
    }

    public String getStartNum()
    {
    
        return startNum;
    }

    public void setStartNum(String startNum)
    {
    
        this.startNum = startNum;
    }

    public String getCodeValue()
    {
    
        return codeValue;
    }

    public void setCodeValue(String codeValue)
    {
    
        this.codeValue = codeValue;
    }

    public String getCodeValue2()
    {
    
        return codeValue2;
    }

    public void setCodeValue2(String codeValue2)
    {
    
        this.codeValue2 = codeValue2;
    }

    public String getParamStr()
    {
    
        return paramStr;
    }

    public void setParamStr(String paramStr)
    {
    
        this.paramStr = paramStr;
    }

    public String getOrderid()
    {
    
        return orderid;
    }

    public void setOrderid(String orderid)
    {
    
        this.orderid = orderid;
    }

    public ArrayList getFieldslist()
    {
    
        return fieldslist;
    }

    public void setFieldslist(ArrayList fieldslist)
    {
    
        this.fieldslist = fieldslist;
    }

    public String getEndFlag()
    {
    
        return endFlag;
    }

    public void setEndFlag(String endFlag)
    {
    
        this.endFlag = endFlag;
    }

    public String getDelFlag()
    {
    
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
    
        this.delFlag = delFlag;
    }  
    
}
