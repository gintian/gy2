package com.hjsj.hrms.actionform.gz.bonus;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * <p>Title:BonusParamForm.java</p>
 * <p>Description:奖金参数设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-07-02 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class BonusParamForm extends FrameForm
{
    //人员库
    private ArrayList nbase = new ArrayList();    
    //奖金子集
    private ArrayList bonusSetList = new ArrayList();
    private String bonusSet = "";
    //工号
    private ArrayList jobnumList = new ArrayList();
    private String jobnum = "";
    
    private String paramStr="";
    
    private String menuid = "";
    
    // 表对象
    private RecordVo codeitemVo = new RecordVo("codeitem");
    
    private ArrayList codeDataList = new ArrayList();
    
    private String codeLen="0";
    @Override
    public void inPutTransHM()
    {
	this.getFormHM().put("nbase", this.getNbase());
	this.getFormHM().put("bonusSetList", this.bonusSetList);
	this.getFormHM().put("bonusSet", this.getBonusSet());
	this.getFormHM().put("jobnumList", this.getJobnumList());
	this.getFormHM().put("jobnum", this.getJobnum());
	this.getFormHM().put("paramStr", this.getParamStr());
	this.getFormHM().put("menuid", this.getMenuid());
	this.getFormHM().put("codeDataList", this.getCodeDataList());
	this.getFormHM().put("codeitemVo", this.getCodeitemVo());
	this.getFormHM().put("codeLen", this.getCodeLen());
    }

    @Override
    public void outPutFormHM()
    {
	this.setNbase((ArrayList) this.getFormHM().get("nbase"));
	this.setBonusSetList((ArrayList) this.getFormHM().get("bonusSetList"));
	this.setBonusSet((String) this.getFormHM().get("bonusSet"));
	this.setJobnumList((ArrayList) this.getFormHM().get("jobnumList"));
	this.setJobnum((String) this.getFormHM().get("jobnum"));
	this.setParamStr((String) this.getFormHM().get("paramStr"));
	this.setMenuid((String) this.getFormHM().get("menuid"));
	this.setCodeDataList((ArrayList) this.getFormHM().get("codeDataList"));
	this.setCodeitemVo((RecordVo)this.getFormHM().get("codeitemVo"));
	this.setCodeLen((String) this.getFormHM().get("codeLen"));
    }

    public String getBonusSet()
    {
    
        return bonusSet;
    }

    public void setBonusSet(String bonusSet)
    {
    
        this.bonusSet = bonusSet;
    }

    public ArrayList getBonusSetList()
    {
    
        return bonusSetList;
    }

    public void setBonusSetList(ArrayList bonusSetList)
    {
    
        this.bonusSetList = bonusSetList;
    }

    public String getJobnum()
    {
    
        return jobnum;
    }

    public void setJobnum(String jobnum)
    {
    
        this.jobnum = jobnum;
    }

    public ArrayList getJobnumList()
    {
    
        return jobnumList;
    }

    public void setJobnumList(ArrayList jobnumList)
    {
    
        this.jobnumList = jobnumList;
    }

    public ArrayList getNbase()
    {
    
        return nbase;
    }

    public void setNbase(ArrayList nbase)
    {
    
        this.nbase = nbase;
    }

    public String getParamStr()
    {
    
        return paramStr;
    }

    public void setParamStr(String paramStr)
    {
    
        this.paramStr = paramStr;
    }

    public String getMenuid()
    {
    
        return menuid;
    }

    public void setMenuid(String menuid)
    {
    
        this.menuid = menuid;
    }

    public ArrayList getCodeDataList()
    {
    
        return codeDataList;
    }

    public void setCodeDataList(ArrayList codeDataList)
    {
    
        this.codeDataList = codeDataList;
    }

    public RecordVo getCodeitemVo()
    {
    
        return codeitemVo;
    }

    public void setCodeitemVo(RecordVo codeitemVo)
    {
    
        this.codeitemVo = codeitemVo;
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
