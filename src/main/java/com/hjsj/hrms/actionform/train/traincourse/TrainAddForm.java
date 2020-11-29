package com.hjsj.hrms.actionform.train.traincourse;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>
 * Title:TrainAddForm.java
 * </p>
 * <p>
 * Description:培训添加通用
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-08-13 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainAddForm extends FrameForm
{
    /** 添加时候需要默认输入的机构编码 如单位和部门 */
    private String a_code = "";

    /** 表名 */
    private String fieldsetid = "";

    /** 表的所有字段集合 */
    private ArrayList fieldlist = new ArrayList();

    /** 只读字段集合 */
    private ArrayList itemidarr;

    /** 只读字段字符串 如r3127,r3128, */
    private String readonlyFilds;

    /** 初始字段和值字符串 如r3127:01,r3128:02, */
    private String initValue = "";

    /** 前台新增或者修改页面的标题 */
    private String titlename = "";

    /** 标识新增还是编辑，利用它来控制前台页面保存并继续按钮的显示否 */
    private String chkflag = "";

    /** 主键字段名 */
    private String primaryField;

    /** 主键的值 */
    private String priFldValue;

    /** 单位权限码 */
    private String orgparentcode;

    /** 部门权限码 */
    private String deptparentcode;

    /** 隐藏字段字符串 如： r3111,r3112, */
    private String hideFilds; 

    /** 隐藏图片集合 */
    private ArrayList hidePics = new ArrayList();

    /** 隐藏图片字符串 如 imgr3127,imgr3128 */
    private String hideimgids;

    /** 需要在页面上不显示出来但还要保存的字段及其值的字符串,在此设置了就不用在initValue中设置了,但是还要在 hideFilds中设置。 */
    private String hideSaveFlds;

    /**单位部门是否联动*/
    private String isUnUmRela;
    
    /**培训班开始时间－时分秒部分*/
    private String r3115_time;
    
    /**培训班结束时间－时分秒部分*/
    private String r3116_time;
    
    private String r3101;
    
    //用来判断是新建 还是编辑 课程
    private String addCourse;
    
    @Override
    public void outPutFormHM()
    {

	this.setA_code((String) this.getFormHM().get("a_code"));
	this.setFieldsetid((String) this.getFormHM().get("fieldsetid"));
	this.setFieldlist((ArrayList) this.getFormHM().get("fieldlist"));
	this.setItemidarr((ArrayList) this.getFormHM().get("itemidarr"));
	this.setTitlename((String) this.getFormHM().get("titlename"));
	this.setChkflag((String) this.getFormHM().get("chkflag"));
	this.setPrimaryField((String) this.getFormHM().get("primaryField"));
	this.setOrgparentcode((String) this.getFormHM().get("orgparentcode"));
	this.setHideFilds((String) this.getFormHM().get("hideFilds"));
	this.setReadonlyFilds((String) this.getFormHM().get("readonlyFilds"));
	this.setHidePics((ArrayList) this.getFormHM().get("hidePics"));
	this.setDeptparentcode((String) this.getFormHM().get("deptparentcode"));
	this.setHideimgids((String) this.getFormHM().get("hideimgids"));
	this.setPriFldValue((String) this.getFormHM().get("priFldValue"));
	this.setHideSaveFlds((String) this.getFormHM().get("hideSaveFlds"));
	this.setIsUnUmRela((String) this.getFormHM().get("isUnUmRela"));
	this.setR3115_time((String) this.getFormHM().get("r3115_time"));
	this.setR3116_time((String) this.getFormHM().get("r3116_time"));
	this.setR3101((String)this.getFormHM().get("r3101"));
	this.setAddCourse((String)this.getFormHM().get("addCourse"));
    }

    @Override
    public void inPutTransHM()
    {

	this.getFormHM().put("a_code", this.getA_code());
	this.getFormHM().put("fieldsetid", this.getFieldsetid());
	this.getFormHM().put("fieldlist", this.getFieldlist());
	this.getFormHM().put("itemidarr", this.getItemidarr());
	this.getFormHM().put("titlename", this.getTitlename());
	this.getFormHM().put("chkflag", this.getChkflag());
	this.getFormHM().put("primaryField", this.getPrimaryField());
	this.getFormHM().put("orgparentcode", this.getOrgparentcode());
	this.getFormHM().put("hideFilds", this.getHideFilds());
	this.getFormHM().put("readonlyFilds", this.getReadonlyFilds());
	this.getFormHM().put("hidePics", this.getHidePics());
	this.getFormHM().put("deptparentcode", this.getDeptparentcode());
	this.getFormHM().put("hideimgids", this.getHideimgids());
	this.getFormHM().put("priFldValue", this.getPriFldValue());
	this.getFormHM().put("hideSaveFlds", this.getHideSaveFlds());
	this.getFormHM().put("isUnUmRela", this.getIsUnUmRela());
	this.getFormHM().put("r3115_time", this.getR3115_time());
	this.getFormHM().put("r3116_time", this.getR3116_time());
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        
        if(("/train/traincourse/traindataAdd".equals(arg0.getPath())&&(arg1.getParameter("b_save")!=null || arg1.getParameter("b_saveContinue")!=null))) {
            arg1.setAttribute("targetWindow", "1");
        }
        return super.validate(arg0, arg1);
   }
    
    public String getChkflag()
    {

	return chkflag;
    }

    public void setChkflag(String chkflag)
    {

	this.chkflag = chkflag;
    }

    public ArrayList getFieldlist()
    {

	return fieldlist;
    }

    public void setFieldlist(ArrayList fieldlist)
    {

	this.fieldlist = fieldlist;
    }

    public String getFieldsetid()
    {

	return fieldsetid;
    }

    public void setFieldsetid(String fieldsetid)
    {

	this.fieldsetid = fieldsetid;
    }

    public ArrayList getItemidarr()
    {

	return itemidarr;
    }

    public void setItemidarr(ArrayList itemidarr)
    {

	this.itemidarr = itemidarr;
    }

    public String getTitlename()
    {

	return titlename;
    }

    public void setTitlename(String titlename)
    {

	this.titlename = titlename;
    }

    public String getA_code()
    {

	return a_code;
    }

    public void setA_code(String a_code)
    {

	this.a_code = a_code;
    }

    public String getInitValue()
    {

	return initValue;
    }

    public void setInitValue(String initValue)
    {

	this.initValue = initValue;
    }

    public String getPrimaryField()
    {

	return primaryField;
    }

    public void setPrimaryField(String primaryField)
    {

	this.primaryField = primaryField;
    }

    public String getOrgparentcode()
    {

	return orgparentcode;
    }

    public void setOrgparentcode(String orgparentcode)
    {

	this.orgparentcode = orgparentcode;
    }

    public String getHideFilds()
    {

	return hideFilds;
    }

    public void setHideFilds(String hideFilds)
    {

	this.hideFilds = hideFilds;
    }

    public String getReadonlyFilds()
    {

	return readonlyFilds;
    }

    public void setReadonlyFilds(String readonlyFilds)
    {

	this.readonlyFilds = readonlyFilds;
    }

    public ArrayList getHidePics()
    {

	return hidePics;
    }

    public void setHidePics(ArrayList hidePics)
    {

	this.hidePics = hidePics;
    }

    public String getDeptparentcode()
    {

	return deptparentcode;
    }

    public void setDeptparentcode(String deptparentcode)
    {

	this.deptparentcode = deptparentcode;
    }

    public String getHideimgids()
    {

	return hideimgids;
    }

    public void setHideimgids(String hideimgids)
    {

	this.hideimgids = hideimgids;
    }

    public String getPriFldValue()
    {

	return priFldValue;
    }

    public void setPriFldValue(String priFldValue)
    {

	this.priFldValue = priFldValue;
    }

    public String getHideSaveFlds()
    {

	return hideSaveFlds;
    }

    public void setHideSaveFlds(String hideSaveFlds)
    {

	this.hideSaveFlds = hideSaveFlds;
    }

    public String getIsUnUmRela()
    {
    
        return isUnUmRela;
    }

    public void setIsUnUmRela(String isUnUmRela)
    {
    
        this.isUnUmRela = isUnUmRela;
    }

    public String getR3115_time()
    {
    
        return r3115_time;
    }

    public void setR3115_time(String r3115_time)
    {
    
        this.r3115_time = r3115_time;
    }

    public String getR3116_time()
    {
    
        return r3116_time;
    }

    public void setR3116_time(String r3116_time)
    {
    
        this.r3116_time = r3116_time;
    }

	public String getR3101() {
		return r3101;
	}

	public void setR3101(String r3101) {
		this.r3101 = r3101;
	}

	public String getAddCourse() {
		return addCourse;
	}

	public void setAddCourse(String addCourse) {
		this.addCourse = addCourse;
	}
	

}
