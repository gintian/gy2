package com.hjsj.hrms.actionform.performance.options;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class PerDegreedescForm extends FrameForm
{

    private String degreeId;

    private String flag;

    private RecordVo perdegreedescvo = new RecordVo("per_degreedesc");

    /** 标识操作信息 */
    private String info;

    /** 删除字符串 */
    private String deletestr;

    /** 代表上移和下移 */
    private String num;

    private int current = 1;

    private PaginationForm setlistform = new PaginationForm();

    private ArrayList setlist = new ArrayList();

    private String itemNo="";
    
    //判断记录是否为第一条或者是最后一条
    private String isForL="";
    
    @Override
    public void inPutTransHM()
    {
	this.getFormHM().put("degreeId", this.getDegreeId());
	this.getFormHM().put("flag", this.getFlag());
	this.getFormHM().put("perdegreedescvo", this.getPerdegreedescvo());
	this.getFormHM().put("num", this.getNum());
	this.getFormHM().put("deletestr", this.getDeletestr());
	this.getFormHM().put("info", this.getInfo());
	this.getFormHM().put("itemNo", this.getItemNo());
	this.getFormHM().put("isForL", this.getIsForL());
    }

    @Override
    public void outPutFormHM()
    {
    this.setReturnflag((String)this.getFormHM().get("returnflag")); 
	this.getSetlistform().setList((ArrayList) this.getFormHM().get("setlist"));
	this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
	this.setDegreeId((String) this.getFormHM().get("degreeId"));
	this.setFlag((String) this.getFormHM().get("flag"));
	this.setPerdegreedescvo((RecordVo) this.getFormHM().get("perdegreedescvo"));
	this.setInfo((String) this.getFormHM().get("info"));
	this.setItemNo((String) this.getFormHM().get("itemNo"));
	this.setIsForL((String) this.getFormHM().get("isForL"));
    }

    public String getFlag()
    {

	return flag;
    }

    public void setFlag(String flag)
    {

	this.flag = flag;
    }

    public String getDegreeId()
    {

	return degreeId;
    }

    public void setDegreeId(String degreeId)
    {

	this.degreeId = degreeId;
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

    public RecordVo getPerdegreedescvo()
    {

	return perdegreedescvo;
    }

    public void setPerdegreedescvo(RecordVo perdegreedescvo)
    {

	this.perdegreedescvo = perdegreedescvo;
    }

    public String getItemNo()
    {
    
        return itemNo;
    }

    public void setItemNo(String itemNo)
    {
    
        this.itemNo = itemNo;
    }

    public String getIsForL()
    {
    
        return isForL;
    }

    public void setIsForL(String isForL)
    {
    
        this.isForL = isForL;
    }
    
}
