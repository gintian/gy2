package com.hjsj.hrms.module.recruitment.recruitflow.actionform;

import com.hrms.struts.action.FrameForm;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 * <p>
 * Title:RecruitflowForm.java
 * </p>
 * <p>
 * Description: 招聘流程定义
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-6 上午09:48:51
 * </p>
 * 
 * @author chenxg
 * @version 1.0
 *
 */
public class RecruitflowForm extends FrameForm{
    
	private String records = "";
    private String strsql = "";
    private String codeitemdesc = "";//所属单位名称
    private String msg = "";//标志是否有招聘过程数据
    private ArrayList columns = new ArrayList();
    //流程id
    private String flowid = "";
    //流程环节id
    private String linkid = "";
    //流程id
    private String xjflowid = "";
    //流程名称
    private String flowName = "";
    //流程名称
    private String sysName = "";
    //流程基本信息
    private LazyDynaBean flowBean = new LazyDynaBean();
    //所有流程生成的下拉表信息
    private String flowHtml = "";
    //已选流程环节id
    private String node_id = "";
    //已选流程环节用户自定义名称
    private String custom_name = "";
    //流程描述
    private String description = "";
    //所属机构编码
    private String b0110 = "";
    //环节序号
    private String seq = "";
    //标志字段(用来判断是启用流程还是停用流程)
    private String flag = "";
    //流程状态
    private String valid="1";
    //流程环节序号
    private ArrayList seqs = new ArrayList();
    //流程环节用户定义名称
    private ArrayList custom_names = new ArrayList();
    //流程环节备注
    private ArrayList remarks = new ArrayList();
    //流程环节是否可用
    private ArrayList valids = new ArrayList();
    //流程环节id
    private ArrayList ids = new ArrayList();
    //提示信息
    private String message="";
    //是否是上级流程
    private String isParent="";
    //招聘环节是否必须顺序进行
    private String skipflag = "1";
    
    
    @Override
    public void outPutFormHM() {
        this.setStrsql((String)this.getFormHM().get("strsql"));
        this.setValid((String) this.getFormHM().get("valid"));
        this.setColumns((ArrayList)this.getFormHM().get("columns"));
        this.setFlowid((String)this.getFormHM().get("flowid"));
        this.setLinkid((String)this.getFormHM().get("linkid"));
        this.setXjflowid((String)this.getFormHM().get("xjflowid"));
        this.setB0110((String)this.getFormHM().get("b0110"));
        this.setFlowName((String)this.getFormHM().get("flowName"));
        this.setFlowBean((LazyDynaBean)this.getFormHM().get("flowBean"));
        this.setFlowHtml((String)this.getFormHM().get("flowHtml"));
        this.setNode_id((String)this.getFormHM().get("node_id"));
        this.setDescription((String)this.getFormHM().get("description"));
        this.setMessage((String)this.getFormHM().get("message"));
        this.setSysName((String)this.getFormHM().get("sysName"));
        this.setMsg((String)this.getFormHM().get("msg"));
        this.setCustom_name((String)this.getFormHM().get("custom_name"));
        this.setCodeitemdesc((String)this.getFormHM().get("codeitemdesc"));
        this.setRecords((String)this.getFormHM().get("records"));
        this.setIsParent((String)this.getFormHM().get("isParent"));
        this.setSkipflag((String)this.getFormHM().get("skipflag"));
    }
    
	@Override
    public void inPutTransHM() {
        this.getFormHM().put("strsql",this.getStrsql());
        this.getFormHM().put("columns",this.getColumns());
        this.getFormHM().put("flowid",this.getFlowid());
        this.getFormHM().put("linkid",this.getLinkid());
        this.getFormHM().put("flowName",this.getFlowName());
        this.getFormHM().put("flowBean",this.getFlowBean());
        this.getFormHM().put("flowHtml",this.getFlowHtml());
        this.getFormHM().put("node_id",this.getNode_id());
        this.getFormHM().put("custom_name",this.getCustom_name());
        this.getFormHM().put("description",this.getDescription());
        this.getFormHM().put("flag",this.getFlag());
        this.getFormHM().put("valids",this.getValids());
        this.getFormHM().put("remarks",this.getRemarks());
        this.getFormHM().put("custom_names",this.getCustom_names());
        this.getFormHM().put("seqs",this.getSeqs());
        this.getFormHM().put("ids",this.getIds());
        this.getFormHM().put("seq",this.getSeq());
        this.getFormHM().put("isParent",this.getIsParent());
        this.getFormHM().put("skipflag", this.getSkipflag());
    }

    public String getStrsql() {
        return strsql;
    }

    public void setStrsql(String strsql) {
        this.strsql = strsql;
    }

    public String getFlowid() {
        return flowid;
    }

    public void setFlowid(String flowid) {
        this.flowid = flowid;
    }

    public ArrayList getColumns() {
        return columns;
    }

    public void setColumns(ArrayList columns) {
        this.columns = columns;
    }

    public LazyDynaBean getFlowBean() {
        return flowBean;
    }

    public void setFlowBean(LazyDynaBean flowBean) {
        this.flowBean = flowBean;
    }

    public String getFlowHtml() {
        return flowHtml;
    }

    public void setFlowHtml(String flowHtml) {
        this.flowHtml = flowHtml;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

	public String getNode_id() {
		return node_id;
	}

	public void setNode_id(String nodeId) {
		node_id = nodeId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCustom_name() {
		return custom_name;
	}

	public void setCustom_name(String customName) {
		custom_name = customName;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getXjflowid() {
		return xjflowid;
	}

	public void setXjflowid(String xjflowid) {
		this.xjflowid = xjflowid;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public ArrayList getSeqs() {
		return seqs;
	}

	public void setSeqs(ArrayList seqs) {
		this.seqs = seqs;
	}

	public ArrayList getCustom_names() {
		return custom_names;
	}

	public void setCustom_names(ArrayList customNames) {
		custom_names = customNames;
	}

	public ArrayList getRemarks() {
		return remarks;
	}

	public void setRemarks(ArrayList remarks) {
		this.remarks = remarks;
	}

	public ArrayList getValids() {
		return valids;
	}

	public void setValids(ArrayList valids) {
		this.valids = valids;
	}

	public ArrayList getIds() {
		return ids;
	}

	public void setIds(ArrayList ids) {
		this.ids = ids;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getLinkid() {
		return linkid;
	}

	public void setLinkid(String linkid) {
		this.linkid = linkid;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public String getCodeitemdesc() {
		return codeitemdesc;
	}

	public void setCodeitemdesc(String codeitemdesc) {
		this.codeitemdesc = codeitemdesc;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getRecords() {
		return records;
	}

	public void setRecords(String records) {
		this.records = records;
	}

	public String getIsParent() {
		return isParent;
	}

	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}

	public String getSkipflag() {
		return skipflag;
	}

	public void setSkipflag(String skipflag) {
		this.skipflag = skipflag;
	}
    
}
