package com.hjsj.hrms.actionform.gz.bonus;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;
/**
 * <p>Title:BonusForm.java</p>
 * <p>Description:奖金管理</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-07-02 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */


public class BonusForm extends FrameForm
{
    private String a_code = "";
    
    private String businessDate = "";
    
    private ArrayList dateList = new ArrayList();    
    
    private String sql = "";
    
    private ArrayList fieldlist = new ArrayList();   
    
    private String doStatusFld = "";//处理状态字段
    
    private String bonusSet = "";
    
    private String paramStr="";
    
    private ArrayList fieldInfoList =  new ArrayList();   
    
    private String jobnumFld = ""; //工号字段
    
    private String expr="";
    
    private String factor="";
    
    private String itemid="";
    
    private ArrayList itemList= new ArrayList();   
    
    private String refItem="";
    
    private ArrayList refItemList= new ArrayList();
    /** 导入文件 */
    private FormFile file;
    
    private String codeLen="0";
    
    @Override
    public void inPutTransHM()
    {
	this.getFormHM().put("a_code", this.getA_code());
	this.getFormHM().put("businessDate", this.getBusinessDate());
	this.getFormHM().put("dateList", this.getDateList());
	this.getFormHM().put("fieldlist", this.getFieldlist());
	this.getFormHM().put("sql", this.getSql());
	this.getFormHM().put("doStatusFld", this.getDoStatusFld());
	this.getFormHM().put("bonusSet", this.getBonusSet());
	this.getFormHM().put("paramStr", this.getParamStr());
	this.getFormHM().put("fieldInfoList", this.getFieldInfoList());
	this.getFormHM().put("jobnumFld", this.getJobnumFld());
	this.getFormHM().put("expr", this.getExpr());
	this.getFormHM().put("factor", this.getFactor());
	this.getFormHM().put("itemid", this.getItemid());
	this.getFormHM().put("itemList", this.getItemList());
	this.getFormHM().put("refItem", this.getRefItem());
	this.getFormHM().put("refItemList", this.getRefItemList());
	this.getFormHM().put("file", this.getFile());
	this.getFormHM().put("codeLen", this.getCodeLen());
    }
    @Override
    public void outPutFormHM()
    {
	this.setA_code((String) this.getFormHM().get("a_code"));
	this.setBusinessDate((String) this.getFormHM().get("businessDate"));
	this.setDateList((ArrayList)this.getFormHM().get("dateList"));
	this.setSql((String) this.getFormHM().get("sql"));
	this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
	this.setDoStatusFld((String) this.getFormHM().get("doStatusFld"));
	this.setBonusSet((String) this.getFormHM().get("bonusSet"));
	this.setParamStr((String) this.getFormHM().get("paramStr"));
	this.setFieldInfoList((ArrayList)this.getFormHM().get("fieldInfoList"));
	this.setJobnumFld((String) this.getFormHM().get("jobnumFld"));
	this.setExpr((String) this.getFormHM().get("expr"));
	this.setFactor((String) this.getFormHM().get("factor"));
	this.setItemid((String) this.getFormHM().get("itemid"));
	this.setItemList((ArrayList)this.getFormHM().get("itemList"));
	this.setRefItem((String) this.getFormHM().get("refItem"));
	this.setRefItemList((ArrayList)this.getFormHM().get("refItemList"));
	this.setFile((FormFile)this.getFormHM().get("file"));
	this.setCodeLen((String) this.getFormHM().get("codeLen"));
    }
    public String getA_code()
    {
    
        return a_code;
    }
    public void setA_code(String a_code)
    {
    
        this.a_code = a_code;
    }
    public String getBusinessDate()
    {
    
        return businessDate;
    }
    public void setBusinessDate(String businessDate)
    {
    
        this.businessDate = businessDate;
    }
    public ArrayList getDateList()
    {
    
        return dateList;
    }
    public void setDateList(ArrayList dateList)
    {
    
        this.dateList = dateList;
    }
    public ArrayList getFieldlist()
    {
    
        return fieldlist;
    }
    public void setFieldlist(ArrayList fieldlist)
    {
    
        this.fieldlist = fieldlist;
    }
    public String getSql()
    {
    
        return sql;
    }
    public void setSql(String sql)
    {
    
        this.sql = sql;
    }
    public String getDoStatusFld()
    {
    
        return doStatusFld;
    }
    public void setDoStatusFld(String doStatusFld)
    {
    
        this.doStatusFld = doStatusFld;
    }
    public String getBonusSet()
    {
    
        return bonusSet;
    }
    public void setBonusSet(String bonusSet)
    {
    
        this.bonusSet = bonusSet;
    }
    public String getParamStr()
    {
    
        return paramStr;
    }
    public void setParamStr(String paramStr)
    {
    
        this.paramStr = paramStr;
    }
    public ArrayList getFieldInfoList()
    {
    
        return fieldInfoList;
    }
    public void setFieldInfoList(ArrayList fieldInfoList)
    {
    
        this.fieldInfoList = fieldInfoList;
    }
    public String getJobnumFld()
    {
    
        return jobnumFld;
    }
    public void setJobnumFld(String jobnumFld)
    {
    
        this.jobnumFld = jobnumFld;
    }
    public String getExpr()
    {
    
        return expr;
    }
    public void setExpr(String expr)
    {
    
        this.expr = expr;
    }
    public String getFactor()
    {
    
        return factor;
    }
    public void setFactor(String factor)
    {
    
        this.factor = factor;
    }
    public String getItemid()
    {
    
        return itemid;
    }
    public void setItemid(String itemid)
    {
    
        this.itemid = itemid;
    }
    public ArrayList getItemList()
    {
    
        return itemList;
    }
    public void setItemList(ArrayList itemList)
    {
    
        this.itemList = itemList;
    }
    public String getRefItem()
    {
    
        return refItem;
    }
    public void setRefItem(String refItem)
    {
    
        this.refItem = refItem;
    }
    public ArrayList getRefItemList()
    {
    
        return refItemList;
    }
    public void setRefItemList(ArrayList refItemList)
    {
    
        this.refItemList = refItemList;
    }
    public FormFile getFile()
    {
    
        return file;
    }
    public void setFile(FormFile file)
    {
    
        this.file = file;
    }
    public String getCodeLen()
    {
    
        return codeLen;
    }
    public void setCodeLen(String codeLen)
    {
    
        this.codeLen = codeLen;
    }
    
}
