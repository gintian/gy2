package com.hjsj.hrms.actionform.train.request;

import com.hrms.struts.action.FrameForm;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
/**
 * <p>
 * Title:TrainEffectEvalForm.java
 * </p>
 * <p>
 * Description:培训效果评估
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-08-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainEffectEvalForm extends FrameForm
{
    private LazyDynaBean temJob = new LazyDynaBean();
    
    private LazyDynaBean quesJob = new LazyDynaBean();
    
    private LazyDynaBean quesTeacher = new LazyDynaBean();    
    
    private LazyDynaBean temTeacher = new LazyDynaBean();    

    private LazyDynaBean ctrl_apply = new LazyDynaBean();
    
    private LazyDynaBean ctrl_count = new LazyDynaBean();
    
    private String r3127 = "";
    
//    private LazyDynaBean checkClass = new LazyDynaBean();
    
    private String className;
    
    @Override
    public void inPutTransHM()
    {
	this.getFormHM().put("temJob", this.getTemJob());
	this.getFormHM().put("temTeacher", this.getTemTeacher());
	this.getFormHM().put("quesJob", this.getQuesJob());
	this.getFormHM().put("quesTeacher", this.getQuesTeacher());
	this.getFormHM().put("className", this.getClassName());
	this.getFormHM().put("ctrl_count", this.getCtrl_count());
	this.getFormHM().put("ctrl_apply", this.getCtrl_apply());
	this.getFormHM().put("r3127", this.getR3127());
//	this.getFormHM().put("checkClass", this.getCheckClass());
    }

    @Override
    public void outPutFormHM()
    {
	this.setTemJob((LazyDynaBean) this.getFormHM().get("temJob"));
	this.setTemTeacher((LazyDynaBean) this.getFormHM().get("temTeacher"));
	this.setQuesJob((LazyDynaBean) this.getFormHM().get("quesJob"));
	this.setQuesTeacher((LazyDynaBean) this.getFormHM().get("quesTeacher"));
	this.setClassName((String) this.getFormHM().get("className"));
	this.setCtrl_apply((LazyDynaBean) this.getFormHM().get("ctrl_apply"));
	this.setCtrl_count((LazyDynaBean) this.getFormHM().get("ctrl_count"));
	this.setR3127((String) this.getFormHM().get("r3127"));
//	this.setCheckClass((LazyDynaBean) this.getFormHM().get("checkClass"));
    }

    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {

	super.reset(arg0, arg1);
	this.temJob.set("run", "0");
	this.quesJob.set("run", "0");
	this.quesTeacher.set("run", "0");
	this.temTeacher.set("run", "0");
    }
    
    public LazyDynaBean getQuesJob()
    {
    
        return quesJob;
    }

    public void setQuesJob(LazyDynaBean quesJob)
    {
    
        this.quesJob = quesJob;
    }

    public LazyDynaBean getQuesTeacher()
    {
    
        return quesTeacher;
    }

    public void setQuesTeacher(LazyDynaBean quesTeacher)
    {
    
        this.quesTeacher = quesTeacher;
    }

    public LazyDynaBean getTemJob()
    {
    
        return temJob;
    }

    public void setTemJob(LazyDynaBean temJob)
    {
    
        this.temJob = temJob;
    }

    public LazyDynaBean getTemTeacher()
    {
    
        return temTeacher;
    }

    public void setTemTeacher(LazyDynaBean temTeacher)
    {
    
        this.temTeacher = temTeacher;
    }

    public String getClassName()
    {
    
        return className;
    }

    public void setClassName(String className)
    {
    
        this.className = className;
    }

	public LazyDynaBean getCtrl_apply() {
		return ctrl_apply;
	}

	public void setCtrl_apply(LazyDynaBean ctrlApply) {
		ctrl_apply = ctrlApply;
	}

	public LazyDynaBean getCtrl_count() {
		return ctrl_count;
	}

	public void setCtrl_count(LazyDynaBean ctrlCount) {
		ctrl_count = ctrlCount;
	}

	public String getR3127() {
		return r3127;
	}

	public void setR3127(String r3127) {
		this.r3127 = r3127;
	}

//	public LazyDynaBean getCheckClass() {
//		return checkClass;
//	}
//
//	public void setCheckClass(LazyDynaBean checkClass) {
//		this.checkClass = checkClass;
//	}

}
