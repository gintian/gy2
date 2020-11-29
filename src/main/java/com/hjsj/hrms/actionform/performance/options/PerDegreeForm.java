package com.hjsj.hrms.actionform.performance.options;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:PerDegreeForm.java</p>
 * <p>Description:等级分类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-06-26 10:52:59</p>
 * @author JinChunhai
 * @version 5.0
 */

public class PerDegreeForm extends FrameForm
{
	
	private String busitype = "0";	// 业务分类字段 =0(绩效考核); =1(能力素质)
    private Integer itemCount;    
    private ArrayList degrees = new ArrayList();
    //高级设置中临时表记录存储
    private ArrayList extpro = new ArrayList();   
    private ArrayList sortlist = new ArrayList();
    //设置应选中的考核等级，考核评估里面设置考核定级的调用时，出来的页面应该选中与计划关联的等级分类
    private String idSel;    
    private String mode;    
    private String oper;    
    private String value;    
    private String grouped;    
    private ArrayList groupList = new ArrayList();
    private String plan_id = "";  // 考核计划号
    private String qy;
    //按比例计算时取整方式参数 0 :取整 1:四舍五入
    private String toRoundOff="0";
    private String degreeId;    
    private String sort_fields;
    private RecordVo perdegreevo = new RecordVo("per_degree");
    /** 标识操作信息 */
    private String info;
    /** 删除字符串 */
    private String deletestr;
    /** 代表上移和下移 */
    private String num;
    private int current = 1;
    private PaginationForm setlistform = new PaginationForm();
    private ArrayList setlist = new ArrayList();    
    private String checkResult="";   
    private String UMGrade="";//所属部门等级

    @Override
    public void inPutTransHM()
    {
    	
    	this.getFormHM().put("busitype", this.getBusitype());
    	this.getFormHM().put("plan_id", this.getPlan_id());
		this.getFormHM().put("degreeId", this.getDegreeId());
		this.getFormHM().put("num", this.getNum());
		this.getFormHM().put("info", this.getInfo());
		this.getFormHM().put("deletestr", this.getDeletestr());
		this.getFormHM().put("perdegreevo", this.getPerdegreevo());
	
		this.getFormHM().put("toRoundOff", this.getToRoundOff());
		this.getFormHM().put("qy", this.getQy());
		this.getFormHM().put("itemCount", this.getItemCount());
		this.getFormHM().put("sort_fields", this.getSort_fields());
		this.getFormHM().put("mode", this.getMode());
		this.getFormHM().put("oper", this.getOper());
		this.getFormHM().put("value", this.getValue());
		this.getFormHM().put("grouped", this.getGrouped());
		this.getFormHM().put("groupList", this.getGroupList());
		this.getFormHM().put("checkResult", this.getCheckResult());
		this.getFormHM().put("idSel", this.getIdSel());
		this.getFormHM().put("UMGrade", this.getUMGrade());
    }

    @Override
    public void outPutFormHM()
    {
    	this.setBusitype((String)this.getFormHM().get("busitype"));
    	this.setPlan_id((String)this.getFormHM().get("plan_id")); 
	    this.setUMGrade((String)this.getFormHM().get("UMGrade")); 
	    this.setReturnflag((String)this.getFormHM().get("returnflag")); 
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
	
		this.setDegreeId((String) this.getFormHM().get("degreeId"));
		this.setPerdegreevo((RecordVo) this.getFormHM().get("perdegreevo"));
	
		this.setInfo((String) this.getFormHM().get("info"));
		this.setQy((String) this.getFormHM().get("qy"));
		this.setToRoundOff((String) this.getFormHM().get("toRoundOff"));
		this.setDegrees((ArrayList) this.getFormHM().get("degrees"));
		this.setExtpro((ArrayList)this.getFormHM().get("extpro"));
		this.setItemCount((Integer)this.getFormHM().get("itemCount"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setSort_fields((String)this.getFormHM().get("sort_fields"));
		this.setMode((String)this.getFormHM().get("mode"));
		this.setOper((String)this.getFormHM().get("oper"));
		this.setValue((String)this.getFormHM().get("value"));
		this.setGrouped((String)this.getFormHM().get("grouped"));
		this.setGroupList((ArrayList)this.getFormHM().get("groupList"));
		this.setCheckResult((String)this.getFormHM().get("checkResult"));
		this.setIdSel((String)this.getFormHM().get("idSel"));
    }

    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {

    	super.reset(arg0, arg1);
    	this.setQy("0");
    }

    public String getInfo()
    {

    	return info;
    }

    public void setInfo(String info)
    {

    	this.info = info;
    }

    public PaginationForm getSetlistform()
    {

    	return setlistform;
    }

    public void setSetlistform(PaginationForm setlistform)
    {

    	this.setlistform = setlistform;
    }

    public ArrayList getSetlist()
    {

    	return setlist;
    }

    public void setSetlist(ArrayList setlist)
    {

    	this.setlist = setlist;
    }

    public String getDeletestr()
    {

    	return deletestr;
    }

    public void setDeletestr(String deletestr)
    {

    	this.deletestr = deletestr;
    }

    public String getNum()
    {

    	return num;
    }

    public void setNum(String num)
    {

    	this.num = num;
    }

    public RecordVo getPerdegreevo()
    {

    	return perdegreevo;
    }

    public void setPerdegreevo(RecordVo perdegreevo)
    {

    	this.perdegreevo = perdegreevo;
    }

    public String getDegreeId()
    {

    	return degreeId;
    }

    public void setDegreeId(String degreeId)
    {

    	this.degreeId = degreeId;
    }

    public String getQy()
    {

    	return qy;
    }

    public void setQy(String qy)
    {

    	this.qy = qy;
    }

    public ArrayList getDegrees()
    {

    	return degrees;
    }

    public void setDegrees(ArrayList degrees)
    {

    	this.degrees = degrees;
    }

    public ArrayList getExtpro()
    {
    
        return extpro;
    }

    public void setExtpro(ArrayList extpro)
    {
    
        this.extpro = extpro;
    }

    public Integer getItemCount()
    {
    
        return itemCount;
    }

    public void setItemCount(Integer itemCount)
    {
    
        this.itemCount = itemCount;
    }

    public ArrayList getSortlist()
    {
    
        return sortlist;
    }

    public void setSortlist(ArrayList sortlist)
    {
    
        this.sortlist = sortlist;
    }

    public String getSort_fields()
    {
    
        return sort_fields;
    }

    public void setSort_fields(String sort_fields)
    {
    
        this.sort_fields = sort_fields;
    }

    public String getGrouped()
    {
    
        return grouped;
    }

    public void setGrouped(String grouped)
    {
    
        this.grouped = grouped;
    }

    public String getMode()
    {
    
        return mode;
    }

    public void setMode(String mode)
    {
    
        this.mode = mode;
    }

    public String getOper()
    {
    
        return oper;
    }

    public void setOper(String oper)
    {
    
        this.oper = oper;
    }

    public String getValue()
    {
    
        return value;
    }

    public void setValue(String value)
    {
    
        this.value = value;
    }

    public String getCheckResult()
    {
    
        return checkResult;
    }

    public void setCheckResult(String checkResult)
    {
    
        this.checkResult = checkResult;
    }

    public String getIdSel()
    {
    
        return idSel;
    }

    public void setIdSel(String idSel)
    {
    
        this.idSel = idSel;
    }

	public String getUMGrade() {
		return UMGrade;
	}

	public void setUMGrade(String grade) {
		UMGrade = grade;
	}

	public ArrayList getGroupList() {
		return groupList;
	}

	public void setGroupList(ArrayList groupList) {
		this.groupList = groupList;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

	public String getToRoundOff() {
		return toRoundOff;
	}

	public void setToRoundOff(String toRoundOff) {
		this.toRoundOff = toRoundOff;
	}

}
