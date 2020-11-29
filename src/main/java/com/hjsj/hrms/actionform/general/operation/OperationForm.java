package com.hjsj.hrms.actionform.general.operation;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class OperationForm extends FrameForm {
	private RecordVo	operationVo;
	private RecordVo	template_tableVo;
	private RecordVo	t_wf_defineVo;
	private String 	treeStr ;
	private String	sql;
	private String	where; 
	private String	column;
	private String	orderby;
	private PaginationForm pageListForm = new PaginationForm();
	private String usertype;//=1自定义表单审批模式 =0 固定表单审批模式
	private ArrayList edit_param=new ArrayList();
	private ArrayList appeal_param=new ArrayList();
	private String[] inputname;
	private String[] inputparam;
	private String[] appname;
	private String[] appparam;
	private String out_type;//输出格式 1分页导出、 2连续导出
	/*
	 *=1固定表单 =0 自定义表单
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	
	private String sp_flag;
	private String bsp_flag;
	private String reject_type;//驳回方式 =1：逐级驳回 =2：驳回到发起人  郭峰
	private String def_flow_self="0";//自定义审批流程 0 1
	/*
	 * =0不用审批 =1 用审批
	 */
	private String validateflag;
	private String sms;
	private String email;
	private String inputurl;
	private String appurl;
	private String selstr;
	private String uflag;
	private String sbch;
	private String operationcode;
	private HashMap spflagmap;
	private String enduser;
	private String endusertype;
	private ArrayList enduserList = new ArrayList();
	private String relation_id;
	private ArrayList relationList = new ArrayList();
	private String template_sp;
	private String template_bos;
	private String sp_bos_flag;
	private String email_staff;
	private String template_staff;
	private ArrayList template_spList = new ArrayList();
	private String operationtype;
	private String enduservalue;
	private String tabid;
	private String layer;//层级
	private String codeitemname;
	private String chgstate;
	private String code_leader;
	private String mode;
	private String checked;
	private Object codeitemname_buffer;
	private Object codeitemid_buffer;
	private Object layer_buffer;
	private Object chgstate_buffer;
	private ArrayList fieldList = new ArrayList();
	private ArrayList codeList;
	private String infor_type;//=1:人员 =2：单位 =3：岗位  郭峰
	private String notice_initiator="";
    private String template_initiator="";//通知发起人时的模版
    private String no_sp_yj="";//是否填写审批意见
    private String sync_archive_data="";//审批单据时同步档案库信息 
    
    
	public String getOut_type() {
		return out_type;
	}

	public void setOut_type(String out_type) {
		this.out_type = out_type;
	}

	/** 
     * @return notice_initiator 
     */
    public String getNotice_initiator() {
        return notice_initiator;
    }

    /** 
     * @return no_sp_yj 
     */
    public String getNo_sp_yj() {
        return no_sp_yj;
    }

    /** 
     * @param noSpYj 要设置的 no_sp_yj 
     */
    public void setNo_sp_yj(String noSpYj) {
        no_sp_yj = noSpYj;
    }

    /** 
     * @param noticeInitiator 要设置的 notice_initiator 
     */
    public void setNotice_initiator(String noticeInitiator) {
        notice_initiator = noticeInitiator;
    }

    /** 
     * @return template_initiator 
     */
    public String getTemplate_initiator() {
        return template_initiator;
    }

    /** 
     * @param templateInitiator 要设置的 template_initiator 
     */
    public void setTemplate_initiator(String templateInitiator) {
        template_initiator = templateInitiator;
    }

    public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getEndusertype() {
		return endusertype;
	}

	public void setEndusertype(String endusertype) {
		this.endusertype = endusertype;
	}

	public String getOperationcode() {
		return operationcode;
	}

	public void setOperationcode(String operationcode) {
		this.operationcode = operationcode;
	}

	/*
	 * uflag=1 修改固定表单
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setOut_type((String)this.getFormHM().get("out_type"));
		this.getPageListForm().setList((ArrayList)this.getFormHM().get("pageList"));
		this.setTreeStr((String) hm.get("treecode"));
		this.setOperationVo((RecordVo) hm.get("operationVo"));
		this.setSql((String) hm.get("sql"));
		this.setWhere((String) hm.get("where"));
		this.setColumn((String) hm.get("column"));
		this.setOrderby((String) hm.get("orderby"));
		this.setT_wf_defineVo((RecordVo) hm.get("t_wf_defineVo"));
		this.setTemplate_tableVo((RecordVo) hm.get("template_tableVo"));
		this.setSp_flag((String) hm.get("sp_flag"));
		this.setBsp_flag((String) hm.get("bsp_flag"));
		this.setEmail((String) hm.get("email"));
		this.setSms((String) hm.get("sms"));
		this.setUsertype((String) hm.get("usertype"));
		this.setSelstr((String) hm.get("selstr"));
		this.setUflag((String) hm.get("uflag"));
		this.setAppurl((String) hm.get("appurl"));
		this.setInputurl((String) hm.get("inputurl"));
		this.setSbch((String) hm.get("sbch"));
		this.setValidateflag((String)hm.get("validateflag"));
		this.setEdit_param((ArrayList)hm.get("edit_param"));		
		this.setAppeal_param((ArrayList)hm.get("appeal_param"));
		this.setOperationcode((String)hm.get("operationcode"));
		this.setSpflagmap((HashMap)hm.get("spflagmap"));
		this.setEndusertype((String)hm.get("endusertype"));
		this.setEnduserList((ArrayList)hm.get("enduserList"));
		this.setEnduser((String)hm.get("enduser"));
		this.setTemplate_bos((String)hm.get("template_bos"));
		this.setTemplate_sp((String)hm.get("template_sp"));
		this.setTemplate_spList((ArrayList)hm.get("template_spList"));
		this.setSp_bos_flag((String)hm.get("sp_bos_flag"));
		this.setEmail_staff((String)hm.get("email_staff"));
		this.setTemplate_staff((String)hm.get("template_staff"));
		this.setOperationtype((String)hm.get("operationtype"));
		this.setEnduservalue((String)hm.get("enduservalue"));
		this.setRelation_id((String)hm.get("relation_id"));
		this.setRelationList((ArrayList)hm.get("relationList"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setFieldList((ArrayList)hm.get("fieldList"));
		this.setLayer((String)this.getFormHM().get("layer"));
		this.setCodeList((ArrayList)hm.get("codeList"));
		this.setCodeitemname((String)this.getFormHM().get("codeitemname"));
		this.setChgstate((String)this.getFormHM().get("chgstate"));
		this.setCodeitemname_buffer((Object)this.getFormHM().get("codeitemname_buffer"));
		this.setCodeitemid_buffer((Object)this.getFormHM().get("codeitemid_buffer"));
		this.setLayer_buffer((Object)this.getFormHM().get("layer_buffer"));
		this.setChgstate_buffer((Object)this.getFormHM().get("chgstate_buffer"));
		this.setCode_leader((String)this.getFormHM().get("code_leader"));
		this.setMode((String)this.getFormHM().get("mode"));
		this.setChecked((String)this.getFormHM().get("checked"));
		this.setInfor_type((String)this.getFormHM().get("infor_type"));
		this.setReject_type((String)this.getFormHM().get("reject_type"));
		this.setDef_flow_self((String)this.getFormHM().get("def_flow_self"));
		this.setNotice_initiator((String) this.getFormHM().get("notice_initiator"));
		this.setTemplate_initiator((String) this.getFormHM().get("template_initiator"));
		this.setNo_sp_yj((String) this.getFormHM().get("no_sp_yj"));
		this.setSync_archive_data((String) this.getFormHM().get("sync_archive_data"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		hm.put("t_wf_defineVo",this.getT_wf_defineVo());
		hm.put("template_tableVo",this.getTemplate_tableVo());
		hm.put("bsp_flag",this.getBsp_flag());
		hm.put("sp_flag",this.getSp_flag());
		hm.put("email",this.getEmail());
		hm.put("sms",this.getSms());
		hm.put("usertype",this.getUsertype());
		hm.put("inputurl",this.getInputurl());
		hm.put("appurl",this.getAppurl());
		if(this.getPagination()!=null)
			hm.put("delsel",(ArrayList)this.getPagination().getSelectedList());
		hm.put("validateflag",this.getValidateflag());
		hm.put("inputname", this.getInputname());
		hm.put("inputparam", this.getInputparam());
		hm.put("appname", this.getAppname());
		hm.put("appparam", this.getAppparam());
		hm.put("operationcode", this.getOperationcode());
		hm.put("enduser",this.getEnduser());
		hm.put("template_sp",this.getTemplate_sp());
		hm.put("template_bos",this.getTemplate_bos());
		hm.put("email_staff", this.email_staff);
		hm.put("template_staff", this.template_staff);
		hm.put("enduservalue",this.getEnduservalue());
		hm.put("endusertype", this.endusertype);
		hm.put("relation_id", this.relation_id);
		this.getFormHM().put("tabid", (String)this.getTabid());
		this.getFormHM().put("fieldList", (ArrayList)this.getFieldList());
		this.getFormHM().put("layer", (String)this.getLayer());
		this.getFormHM().put("codeList", (ArrayList)this.getCodeList());
		this.getFormHM().put("codeitemname", (String)this.getCodeitemname());
		this.getFormHM().put("chgstate", (String)this.getChgstate());
		this.getFormHM().put("codeitemname_buffer", (Object)this.getCodeitemname_buffer());
		this.getFormHM().put("codeitemid_buffer", (Object)this.getCodeitemid_buffer());
		this.getFormHM().put("layer_buffer", (Object)this.getLayer_buffer());
		this.getFormHM().put("chgstate_buffer", (Object)this.getChgstate_buffer());
		this.getFormHM().put("code_leader", (String)this.getCode_leader());
		this.getFormHM().put("mode", (String)this.getMode());
		this.getFormHM().put("checked", (String)this.getChecked());
		this.getFormHM().put("infor_type", (String)this.getInfor_type());
		this.getFormHM().put("reject_type", (String)this.getReject_type());
		this.getFormHM().put("def_flow_self", (String)this.getDef_flow_self());
		this.getFormHM().put("notice_initiator", (String)this.getNotice_initiator());
		this.getFormHM().put("template_initiator",this.getTemplate_initiator());
		this.getFormHM().put("no_sp_yj", this.getNo_sp_yj());
		this.getFormHM().put("sync_archive_data", this.getSync_archive_data());
		this.getFormHM().put("out_type", this.getOut_type());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/general/operation/showtable".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPageListForm().getPagination()!=null)
            	this.getPageListForm().getPagination().firstPage();              
        }
		if("/general/operation/updateapproveway".equals(arg0.getPath())&&arg1.getParameter("b_report")!=null){
		    arg1.setAttribute("targetWindow", "1");
		}
		return super.validate(arg0, arg1);
	}
	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public RecordVo getOperationVo() {
		return operationVo;
	}

	public void setOperationVo(RecordVo operationVo) {
		this.operationVo = operationVo;
	}

	public HashMap getSpflagmap() {
		return spflagmap;
	}

	public void setSpflagmap(HashMap spflagmap) {
		this.spflagmap = spflagmap;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public RecordVo getT_wf_defineVo() {
		return t_wf_defineVo;
	}

	public void setT_wf_defineVo(RecordVo vo) {
		t_wf_defineVo = vo;
	}

	public RecordVo getTemplate_tableVo() {
		return template_tableVo;
	}

	public void setTemplate_tableVo(RecordVo template_tableVo) {
		this.template_tableVo = template_tableVo;
	}

	public String getTreeStr() {
		return treeStr;
	}

	public void setTreeStr(String treeStr) {
		this.treeStr = treeStr;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getBsp_flag() {
		return bsp_flag;
	}

	public void setBsp_flag(String bsp_flag) {
		this.bsp_flag = bsp_flag;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}


	public String getSp_flag() {
		return sp_flag;
	}

	public void setSp_flag(String sp_flag) {
		this.sp_flag = sp_flag;
	}

	public String getAppurl() {
		return appurl;
	}

	public void setAppurl(String appurl) {
		this.appurl = appurl;
	}

	public String getInputurl() {
		return inputurl;
	}

	public void setInputurl(String inputurl) {
		this.inputurl = inputurl;
	}

	public String getSelstr() {
		return selstr;
	}

	public void setSelstr(String selstr) {
		this.selstr = selstr;
	}

	public String getValidateflag() {
		return validateflag;
	}

	public void setValidateflag(String validateflag) {
		this.validateflag = validateflag;
	}

	public String getUflag() {
		return uflag;
	}

	public void setUflag(String uflag) {
		this.uflag = uflag;
	}

	public String getSbch() {
		return sbch;
	}

	public void setSbch(String sbch) {
		this.sbch = sbch;
	}

	public String[] getAppname() {
		return appname;
	}

	public void setAppname(String[] appname) {
		this.appname = appname;
	}

	public String[] getAppparam() {
		return appparam;
	}

	public void setAppparam(String[] appparam) {
		this.appparam = appparam;
	}

	public String[] getInputname() {
		return inputname;
	}

	public void setInputname(String[] inputname) {
		this.inputname = inputname;
	}

	public String[] getInputparam() {
		return inputparam;
	}

	public void setInputparam(String[] inputparam) {
		this.inputparam = inputparam;
	}

	public ArrayList getAppeal_param() {
		return appeal_param;
	}

	public void setAppeal_param(ArrayList appeal_param) {
		this.appeal_param = appeal_param;
	}

	public ArrayList getEnduserList() {
		return enduserList;
	}

	public void setEnduserList(ArrayList enduserList) {
		this.enduserList = enduserList;
	}

	public ArrayList getEdit_param() {
		return edit_param;
	}

	public void setEdit_param(ArrayList edit_param) {
		this.edit_param = edit_param;
	}

	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		this.setOperationcode("");
		super.reset(mapping, request);
	}

	public String getTemplate_sp() {
		return template_sp;
	}

	public void setTemplate_sp(String template_sp) {
		this.template_sp = template_sp;
	}

	public String getTemplate_bos() {
		return template_bos;
	}

	public void setTemplate_bos(String template_bos) {
		this.template_bos = template_bos;
	}

	public String getSp_bos_flag() {
		return sp_bos_flag;
	}

	public void setSp_bos_flag(String sp_bos_flag) {
		this.sp_bos_flag = sp_bos_flag;
	}

	public ArrayList getTemplate_spList() {
		return template_spList;
	}

	public void setTemplate_spList(ArrayList template_spList) {
		this.template_spList = template_spList;
	}

	public String getEnduser() {
		return enduser;
	}

	public void setEnduser(String enduser) {
		this.enduser = enduser;
	}

	public String getEmail_staff() {
		return email_staff;
	}

	public void setEmail_staff(String email_staff) {
		this.email_staff = email_staff;
	}

	public String getTemplate_staff() {
		return template_staff;
	}

	public String getOperationtype() {
		return operationtype;
	}

	public void setOperationtype(String operationtype) {
		this.operationtype = operationtype;
	}

	public String getEnduservalue() {
		return enduservalue;
	}

	public String getRelation_id() {
		return relation_id;
	}

	public void setRelation_id(String relation_id) {
		this.relation_id = relation_id;
	}

	public ArrayList getRelationList() {
		return relationList;
	}

	public void setRelationList(ArrayList relationList) {
		this.relationList = relationList;
	}

	public void setEnduservalue(String enduservalue) {
		this.enduservalue = enduservalue;
	}

	public void setTemplate_staff(String template_staff) {
		this.template_staff = template_staff;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public ArrayList getCodeList() {
		return codeList;
	}

	public void setCodeList(ArrayList codeList) {
		this.codeList = codeList;
	}

	public String getCodeitemname() {
		return codeitemname;
	}

	public void setCodeitemname(String codeitemname) {
		this.codeitemname = codeitemname;
	}

	public Object getCodeitemname_buffer() {
		return codeitemname_buffer;
	}

	public void setCodeitemname_buffer(Object codeitemname_buffer) {
		this.codeitemname_buffer = codeitemname_buffer;
	}

	public Object getCodeitemid_buffer() {
		return codeitemid_buffer;
	}

	public void setCodeitemid_buffer(Object codeitemid_buffer) {
		this.codeitemid_buffer = codeitemid_buffer;
	}

	public Object getLayer_buffer() {
		return layer_buffer;
	}

	public void setLayer_buffer(Object layer_buffer) {
		this.layer_buffer = layer_buffer;
	}

	public String getChgstate() {
		return chgstate;
	}

	public void setChgstate(String chgstate) {
		this.chgstate = chgstate;
	}

	public Object getChgstate_buffer() {
		return chgstate_buffer;
	}

	public void setChgstate_buffer(Object chgstate_buffer) {
		this.chgstate_buffer = chgstate_buffer;
	}

	public String getCode_leader() {
		return code_leader;
	}

	public void setCode_leader(String code_leader) {
		this.code_leader = code_leader;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getInfor_type() {
		return infor_type;
	}

	public void setInfor_type(String infor_type) {
		this.infor_type = infor_type;
	}

	public String getReject_type() {
		return reject_type;
	}

	public void setReject_type(String reject_type) {
		this.reject_type = reject_type;
	}

    public String getDef_flow_self() {
        return def_flow_self;
    }

    public void setDef_flow_self(String def_flow_self) {
        this.def_flow_self = def_flow_self;
    }

    public String getSync_archive_data() {
        return sync_archive_data;
    }

    public void setSync_archive_data(String sync_archive_data) {
        this.sync_archive_data = sync_archive_data;
    }

	

}
