package com.hjsj.hrms.actionform.train.trainexam.exam;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class TrainExamPlanForm extends FrameForm 
{
	private String sqlstr;
	private String where;
	private String column;
	
	//考试计划编号
	private String r5400;
	
	//计划名称
	private String planName="";
	
	//界面操作标记
	private String e_flag;
	
	//最大顺序号
	private int maxOrder;
	
	private String status;
	private String showStyle;
	//计划状态列表
	private ArrayList statusList;
	
	//答卷方式列表
	private ArrayList showStyleList;
	
	//试卷列表
	private ArrayList examPapers;
	
	//分页
	private PaginationForm recordListForm = new PaginationForm();
	
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
    private String hideFilds = "create_user,create_time,"; 

    /** 隐藏图片集合 */
    private ArrayList hidePics = new ArrayList();

    /** 隐藏图片字符串 如 imgr3127,imgr3128 */
    private String hideimgids;

    /** 需要在页面上不显示出来但还要保存的字段及其值的字符串,在此设置了就不用在initValue中设置了,但是还要在 hideFilds中设置。 */
    private String hideSaveFlds;

    /**单位部门是否联动*/
    private String isUnUmRela;
    
    private String r5405_time;
    private String r5406_time;
    
    private boolean emailEnable;
    private boolean smsEnable;
    private ArrayList messageTmpList;
    private String messageTmp;
    private String messageSue;
    private boolean autoCompute;
    private boolean autoRelease;
    
    private boolean weixinEnable;
    
    private boolean dingTalk;

    private boolean pendingTask;
    
    public boolean getWeixinEnable() {
		return weixinEnable;
	}

	public void setWeixinEnable(boolean weixinEnable) {
		this.weixinEnable = weixinEnable;
	}

	//是否允许学员自行重考
    private boolean enabled;
    //允许重考的次数
    private String times;
    
	@Override
    public void outPutFormHM()
	{
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setWhere((String)this.getFormHM().get("where"));
		this.setColumn((String)this.getFormHM().get("column"));
	    this.setE_flag((String)this.getFormHM().get("e_flag"));
	    this.setR5400((String)this.getFormHM().get("r5400"));
	    String tmpmaxOrder = (String)this.getFormHM().get("maxOrder");
	    tmpmaxOrder = tmpmaxOrder==null||tmpmaxOrder.length()<1?"0":tmpmaxOrder;
	    this.setMaxOrder(Integer.parseInt(tmpmaxOrder));
	    this.setStatusList((ArrayList)this.getFormHM().get("statusList"));
	    this.setShowStyleList((ArrayList)this.getFormHM().get("showStyleList"));
	    this.setPlanName((String)this.getFormHM().get("planName"));
	    this.setStatus((String)this.getFormHM().get("status"));
	    this.setShowStyle((String)this.getFormHM().get("showStyle"));
	   
	    this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
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
		this.setExamPapers((ArrayList)this.getFormHM().get("examPapers"));
		this.setR5405_time((String)this.getFormHM().get("r5405_time"));
		this.setR5406_time((String)this.getFormHM().get("r5406_time"));
		this.setEmailEnable(((Boolean)this.getFormHM().get("emailEnable")).booleanValue());
		this.setSmsEnable(((Boolean)this.getFormHM().get("smsEnable")).booleanValue());
		this.setMessageTmpList((ArrayList)this.getFormHM().get("messageTmpList"));
		this.setMessageTmp((String)this.getFormHM().get("messageTmp"));
		this.setMessageSue((String)this.getFormHM().get("messageSue"));
		this.setAutoCompute(((Boolean)this.getFormHM().get("autoCompute")).booleanValue());
		this.setAutoRelease(((Boolean)this.getFormHM().get("autoRelease")).booleanValue());
		this.setEnabled(((Boolean)this.getFormHM().get("enabled")).booleanValue());
		this.setTimes((String)this.getFormHM().get("times"));
		
		this.setWeixinEnable(((Boolean)this.getFormHM().get("weixinEnable")).booleanValue());
		this.setDingTalk(((Boolean)this.getFormHM().get("dingTalk")).booleanValue());
		this.setPendingTask(((Boolean)this.getFormHM().get("pendingTask")).booleanValue());
	}
	
	@Override
    public void inPutTransHM()
    {
		this.getFormHM().put("planName", this.getPlanName());		
		this.getFormHM().put("e_flag",this.getE_flag());
		this.getFormHM().put("r5400",this.getR5400());
		if(this.getPagination()!=null)			
		{
			this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		}
		this.getFormHM().put("statusList", this.getStatusList());
		this.getFormHM().put("showStyleList", this.getShowStyleList());
		this.getFormHM().put("status", this.status);
		this.getFormHM().put("showStyle", this.showStyle);
		
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
		this.getFormHM().put("r5405_time", this.getR5405_time());	
		this.getFormHM().put("r5406_time", this.getR5406_time());	
		this.getFormHM().put("emailEnable", Boolean.valueOf(this.getEmailEnable()));
		this.getFormHM().put("smsEnable", Boolean.valueOf(this.getSmsEnable()));
		this.getFormHM().put("messageTmpList", this.getMessageTmpList());
		this.getFormHM().put("messageTmp", this.getMessageTmp());
		this.getFormHM().put("messageSue", this.getMessageSue());
		this.getFormHM().put("autoCompute", Boolean.valueOf(getAutoCompute()));
		this.getFormHM().put("autoRelease", Boolean.valueOf(getAutoRelease()));
		this.getFormHM().put("enabled", Boolean.valueOf(getEnabled()));
		this.getFormHM().put("times", this.getTimes());
		
		this.getFormHM().put("weixinEnable", Boolean.valueOf(this.getWeixinEnable()));
		this.getFormHM().put("dingTalk", Boolean.valueOf(this.getDingTalk()));
		this.getFormHM().put("pendingTask", Boolean.valueOf(this.isPendingTask()));
    }
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
		try
		{
		    if ("/train/trainexam/exam/plan".equals(arg0.getPath()) && arg1.getParameter("b_query") != null
		            && "link".equalsIgnoreCase(arg1.getParameter("b_query")))
		    {
		    	if (this.getPagination() != null)
		    		this.getPagination().firstPage(); 
		    } 
		    
		} 
		catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		return super.validate(arg0, arg1);
    }
	
	public String getSqlstr() {
		return sqlstr;
	}
	
	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}
	
	public String getWhere() {
		return where;
	}
	
	public void setWhere(String where) {
		this.where = where;
	}
	
	public String getColumn() {
		return column;
	}
	
	public void setColumn(String column) {
		this.column = column;
	}
	
	public String getR5400() {
		return r5400;
	}

	public void setR5400(String r5400) {
		this.r5400 = r5400;
	}
	
	public String getE_flag() {
		return e_flag;
	}
	
	public void setE_flag(String e_flag) {
		this.e_flag = e_flag;
	}
	
	public int getMaxOrder(){
		return maxOrder;
	}
	
	public void setMaxOrder(int order){
		this.maxOrder = order;
	}
	
	public PaginationForm getRecordListForm() {
		return recordListForm;
	}
	
	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public ArrayList getStatusList() {
		return statusList;
	}

	public void setStatusList(ArrayList statusList) {
		this.statusList = statusList;
	}

	public ArrayList getShowStyleList() {
		return showStyleList;
	}

	public void setShowStyleList(ArrayList showStyleList) {
		this.showStyleList = showStyleList;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getShowStyle() {
		return showStyle;
	}

	public void setShowStyle(String showStyle) {
		this.showStyle = showStyle;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getChkflag() {
		return chkflag;
	}

	public void setChkflag(String chkflag) {
		this.chkflag = chkflag;
	}

	public ArrayList getItemidarr() {
		return itemidarr;
	}

	public void setItemidarr(ArrayList itemidarr) {
		this.itemidarr = itemidarr;
	}
    
    public String getReadonlyFilds() {
		return readonlyFilds;
	}

	public void setReadonlyFilds(String readonlyFilds) {
		this.readonlyFilds = readonlyFilds;
	}

	public String getInitValue() {
		return initValue;
	}

	public void setInitValue(String initValue) {
		this.initValue = initValue;
	}

	public String getTitlename() {
		return titlename;
	}

	public void setTitlename(String titlename) {
		this.titlename = titlename;
	}

	public String getPrimaryField() {
		return primaryField;
	}

	public void setPrimaryField(String primaryField) {
		this.primaryField = primaryField;
	}

	public String getPriFldValue() {
		return priFldValue;
	}

	public void setPriFldValue(String priFldValue) {
		this.priFldValue = priFldValue;
	}

	public String getOrgparentcode() {
		return orgparentcode;
	}

	public void setOrgparentcode(String orgparentcode) {
		this.orgparentcode = orgparentcode;
	}

	public String getDeptparentcode() {
		return deptparentcode;
	}

	public void setDeptparentcode(String deptparentcode) {
		this.deptparentcode = deptparentcode;
	}

	public String getHideFilds() {
		return hideFilds;
	}

	public void setHideFilds(String hideFilds) {
		this.hideFilds = hideFilds;
	}

	public ArrayList getHidePics() {
		return hidePics;
	}

	public void setHidePics(ArrayList hidePics) {
		this.hidePics = hidePics;
	}

	public String getHideimgids() {
		return hideimgids;
	}

	public void setHideimgids(String hideimgids) {
		this.hideimgids = hideimgids;
	}

	public String getHideSaveFlds() {
		return hideSaveFlds;
	}

	public void setHideSaveFlds(String hideSaveFlds) {
		this.hideSaveFlds = hideSaveFlds;
	}

	public String getIsUnUmRela() {
		return isUnUmRela;
	}

	public void setIsUnUmRela(String isUnUmRela) {
		this.isUnUmRela = isUnUmRela;
	}

	public ArrayList getExamPapers() {
		return examPapers;
	}

	public void setExamPapers(ArrayList examPapers) {
		this.examPapers = examPapers;
	}

	public String getR5405_time() {
		return r5405_time;
	}

	public void setR5405_time(String r5405_time) {
		this.r5405_time = r5405_time;
	}

	public String getR5406_time() {
		return r5406_time;
	}

	public void setR5406_time(String r5406_time) {
		this.r5406_time = r5406_time;
	}

	public boolean getEmailEnable() {
		return emailEnable;
	}

	public void setEmailEnable(boolean emailEnable) {
		this.emailEnable = emailEnable;
	}

	public boolean getSmsEnable() {
		return smsEnable;
	}

	public void setSmsEnable(boolean smsEnable) {
		this.smsEnable = smsEnable;
	}

	public ArrayList getMessageTmpList() {
		return messageTmpList;
	}

	public void setMessageTmpList(ArrayList messageTmpList) {
		this.messageTmpList = messageTmpList;
	}

	public String getMessageTmp() {
		return messageTmp;
	}

	public void setMessageTmp(String messageTmp) {
		this.messageTmp = messageTmp;
	}

	public String getMessageSue() {
		return messageSue;
	}

	public void setMessageSue(String messageSue) {
		this.messageSue = messageSue;
	}

	public boolean getAutoCompute() {
		return autoCompute;
	}

	public void setAutoCompute(boolean autoCompute) {
		this.autoCompute = autoCompute;
	}

	public boolean getAutoRelease() {
		return autoRelease;
	}

	public void setAutoRelease(boolean autoRelease) {
		this.autoRelease = autoRelease;
	}

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public boolean getDingTalk() {
        return dingTalk;
    }

    public void setDingTalk(boolean dingTalk) {
        this.dingTalk = dingTalk;
    }

    public boolean isPendingTask() {
        return pendingTask;
    }

    public void setPendingTask(boolean pendingTask) {
        this.pendingTask = pendingTask;
    }

}
