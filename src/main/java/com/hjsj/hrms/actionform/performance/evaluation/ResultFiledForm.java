package com.hjsj.hrms.actionform.performance.evaluation;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * <p>Title:ResultFiledForm.java</p>
 * <p>Description:考核评估/结果归档</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-28 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ResultFiledForm extends FrameForm
{
	
	private String busitype = "0";	// 业务分类字段 =0(绩效考核); =1(能力素质)
    private String setName;    
    private String source;   
    private String target;    
    private String haveaccord;    
    private ArrayList subSet = new ArrayList();//考核结果归档子集    
    private ArrayList sourcePoints = new ArrayList();//源指标    
    private ArrayList sourceCodes = new ArrayList();//代码对应－－源代码    
    private ArrayList targetCodes = new ArrayList();//代码对应－－目标代码    
    private ArrayList accordCodes = new ArrayList();//代码对应－－一已对应代码    
    private String strParm;    
    private String filedType;//归档类型　1--人员的归档 2--单位 部门 团队的归档 4--团队负责人的归档    
    private String dispBtFlag;//按钮显示控制
    
    @Override
    public void inPutTransHM()
    {
    	this.getFormHM().put("busitype", this.getBusitype());
		this.getFormHM().put("setName",this.getSetName());
		this.getFormHM().put("source",this.getSetName());
		this.getFormHM().put("target",this.getSetName());
		this.getFormHM().put("haveaccord",this.getSetName());
		this.getFormHM().put("strParm",this.getStrParm());
		this.getFormHM().put("filedType",this.getFiledType());
		this.getFormHM().put("dispBtFlag",this.getDispBtFlag());
    }

    @Override
    public void outPutFormHM()
    {
    	this.setBusitype((String)this.getFormHM().get("busitype"));
		this.setSetName((String)this.getFormHM().get("setName"));
		this.setSubSet((ArrayList)this.getFormHM().get("subSet"));
		this.setSourcePoints((ArrayList)this.getFormHM().get("sourcePoints"));
		this.setSourceCodes((ArrayList)this.getFormHM().get("SourceCodes"));
		this.setTargetCodes((ArrayList)this.getFormHM().get("TargetCodes"));
		this.setAccordCodes((ArrayList)this.getFormHM().get("AccordCodes"));
		this.setSource((String)this.getFormHM().get("source"));
		this.setTarget((String)this.getFormHM().get("target"));
		this.setHaveaccord((String)this.getFormHM().get("haveaccord"));
		this.setStrParm((String)this.getFormHM().get("strParm"));
		this.setFiledType((String)this.getFormHM().get("filedType"));
		this.setDispBtFlag((String)this.getFormHM().get("dispBtFlag"));
    }

    public String getSetName()
    {    
        return setName;
    }

    public void setSetName(String setName)
    {   
        this.setName = setName;
    }

    public ArrayList getSubSet()
    {    
        return subSet;
    }

    public void setSubSet(ArrayList subSet)
    {    
        this.subSet = subSet;
    }

    public ArrayList getSourcePoints()
    {    
        return sourcePoints;
    }

    public void setSourcePoints(ArrayList sourcePoints)
    {    
        this.sourcePoints = sourcePoints;
    }

    public ArrayList getAccordCodes()
    {    
        return accordCodes;
    }

    public void setAccordCodes(ArrayList accordCodes)
    {    
        this.accordCodes = accordCodes;
    }

    public ArrayList getSourceCodes()
    {    
        return sourceCodes;
    }

    public void setSourceCodes(ArrayList sourceCodes)
    {    
        this.sourceCodes = sourceCodes;
    }

    public ArrayList getTargetCodes()
    {    
        return targetCodes;
    }

    public void setTargetCodes(ArrayList targetCodes)
    {    
        this.targetCodes = targetCodes;
    }

    public String getHaveaccord()
    {    
        return haveaccord;
    }

    public void setHaveaccord(String haveaccord)
    {    
        this.haveaccord = haveaccord;
    }

    public String getSource()
    {    
        return source;
    }

    public void setSource(String source)
    {    
        this.source = source;
    }

    public String getTarget()
    {    
        return target;
    }

    public void setTarget(String target)
    {    
        this.target = target;
    }

    public String getStrParm()
    {    
        return strParm;
    }

    public void setStrParm(String strParm)
    {    
        this.strParm = strParm;
    }

    public String getFiledType()
    {    
        return filedType;
    }

    public void setFiledType(String filedType)
    {    
        this.filedType = filedType;
    }

	public String getDispBtFlag()
	{
		return dispBtFlag;
	}

	public void setDispBtFlag(String dispBtFlag)
	{
		this.dispBtFlag = dispBtFlag;
	}

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}
    
}
