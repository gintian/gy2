package com.hjsj.hrms.actionform.general.template;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

public class MyApplyForm extends FrameForm
{

    private RecordVo businessApplyvo = new RecordVo("template_table");

    private PaginationForm setlistform = new PaginationForm();
    /**业务类型
	 * =1,日常管理
	 * =2,工资管理
	 * =3,警衔管理
	 * =8,保险管理
	 * 
	 * //1:人事异动；2：是薪资管理	；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整   23：考勤业务办理  24：非考勤业务(业务申请不包含考勤信息)
	 */
	private String type;
    private ArrayList setlist = new ArrayList();
    private String operationcode="";
    public ArrayList getSetlist()
    {

	return setlist;
    }

    public void setSetlist(ArrayList setlist)
    {

	this.setlist = setlist;
    }

    public PaginationForm getSetlistform()
    {

	return setlistform;
    }

    public void setSetlistform(PaginationForm setlistform)
    {

	this.setlistform = setlistform;
    }

    public RecordVo getBusinessApplyvo()
    {

	return businessApplyvo;
    }

    public void setBusinessApplyvo(RecordVo businessApplyvo)
    {

	this.businessApplyvo = businessApplyvo;
    }

    @Override
    public void inPutTransHM()
    {

	this.getFormHM().put("businessApplyvo", this.getBusinessApplyvo());
	this.getFormHM().put("pagerows", this.getPagerows()==0?"21":(this.getPagerows()+""));
    
    }

    @Override
    public void outPutFormHM()
    {
    this.setType((String)this.getFormHM().get("type"));
    this.setOperationcode((String)this.getFormHM().get("operationcode"));
	this.setBusinessApplyvo((RecordVo) this.getFormHM().get("businessApplyvo"));
	this.getSetlistform().setList((ArrayList) this.getFormHM().get("setlist"));
	this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
	if(this.getFormHM().get("pagerows")==null||((String)this.getFormHM().get("pagerows")).trim().length()==0)
        this.setPagerows(21);
    else
        this.setPagerows(Integer.parseInt(((String)this.getFormHM().get("pagerows"))));
    }
    
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {

        if("/general/template/myapply/busidesktop".equals(arg0.getPath())&&arg1.getParameter("operate")==null){//这样代表这来自菜单中的业务办理,或者说来自于合同办理的导航图
           
            HttpSession session = arg1.getSession();
            TaskDeskForm approvedTaskForm =(TaskDeskForm)session.getAttribute("approvedTaskForm");//已办任务的Form，要将翻页的数据置为1
            TaskDeskForm taskDeskForm = (TaskDeskForm)session.getAttribute("taskDeskForm");//待办任务,将要翻页的数据置为1
            TaskDeskForm ownerApplyForm = (TaskDeskForm)session.getAttribute("ownerApplyForm");//我的申请,将要翻页的数据置为1
            MyApplyForm myApplyForm = (MyApplyForm)session.getAttribute("businessApplyForm");
            
            if(approvedTaskForm!=null){
                approvedTaskForm.getTaskListForm().getPagination().gotoPage(1);//将已办任务的From的翻页数据置为1
                approvedTaskForm.setQuery_type("1");//将查询方式置为默认按照天数查询
                approvedTaskForm.setDays("10");//将默认查询方式的天数置为10天
                approvedTaskForm.setStart_date("");//将开始日期置空
                approvedTaskForm.setEnd_date("");//将结束日期置空
                approvedTaskForm.setTemplateId("-1");//将查询模版ID置为全部
            }
            if(taskDeskForm!=null){
                taskDeskForm.getTaskListForm().getPagination().gotoPage(1);//将已办任务的From的翻页数据置为1
                taskDeskForm.setQuery_type("1");//将查询方式置为默认按照天数查询
                taskDeskForm.setDays("0");//置为默认
                taskDeskForm.setStart_date("");//将开始日期置空
                taskDeskForm.setEnd_date("");//将结束日期置空
                taskDeskForm.setTemplateId("-1");//将查询模版ID置为全部
            }
            if(ownerApplyForm!=null){
                ownerApplyForm.getPagination().gotoPage(1);//将我的申请的From的翻页数据置为1
                ownerApplyForm.setQuery_method("1");//将默认的查询状态置为运行中
            }
            if(myApplyForm!=null){
                myApplyForm.getSetlistform().getPagination().gotoPage(1);
            }
            
        }
        return super.validate(arg0, arg1);
    }
    
	public String getOperationcode() {
		return operationcode;
	}

	public void setOperationcode(String operationcode) {
		this.operationcode = operationcode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
