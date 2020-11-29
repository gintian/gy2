package com.hjsj.hrms.actionform.general.template;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class NodeDefineForm extends FrameForm{
	private String strsql;
	private String columns;
	private String tabid;
	private String node_id;
	private String nodename;
	private String nodenamenew;
	private String objecttype="#";
	private ArrayList rolelist=new ArrayList();
	private PaginationForm nodeForm=new PaginationForm(); 
	private PaginationForm  NodeDefineForm=new PaginationForm(); 
    private ArrayList list=new ArrayList();
	private String[] right_fields;
	private String returnflag;
	
	private String isOperate="1";   //是否可定义审批流程
	
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setRolelist((ArrayList)this.getFormHM().get("rolelist"));
		this.getNodeForm().setList((ArrayList)this.getFormHM().get("nodelist"));	
		//this.setNodename((String)this.getFormHM().get("nodename"));
	    this.setList((ArrayList)this.getFormHM().get("tranlist"));
	    this.setIsOperate((String)this.getFormHM().get("isOperate"));
	}

	@Override
    public void inPutTransHM() {
        this.getFormHM().put("tabid",this.tabid);
        this.getFormHM().put("selectedlist",(ArrayList)this.getNodeForm().getSelectedList());
        this.getFormHM().put("right_fields",this.getRight_fields());
	}

	/**
	 * @return Returns the columns.
	 */
	public String getColumns() {
		return columns;
	}

	/**
	 * @param columns The columns to set.
	 */
	public void setColumns(String columns) {
		this.columns = columns;
	}

	/**
	 * @return Returns the strsql.
	 */
	public String getStrsql() {
		return strsql;
	}

	/**
	 * @param strsql The strsql to set.
	 */
	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	/**
	 * @return Returns the tabid.
	 */
	public String getTabid() {
		return tabid;
	}

	/**
	 * @param tabid The tabid to set.
	 */
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	/**
	 * @return Returns the node_id.
	 */
	public String getNode_id() {
		return node_id;
	}

	/**
	 * @param node_id The node_id to set.
	 */
	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}

	/**
	 * @return Returns the nodename.
	 */
	public String getNodename() {
		return nodename;
	}

	/**
	 * @param nodename The nodename to set.
	 */
	public void setNodename(String nodename) {
		this.nodename = nodename;
	}

	/**
	 * @return Returns the objecttype.
	 */
	public String getObjecttype() {
		return objecttype;
	}

	/**
	 * @param objecttype The objecttype to set.
	 */
	public void setObjecttype(String objecttype) {
		this.objecttype = objecttype;
	}

	/**
	 * @return Returns the rolelist.
	 */
	public ArrayList getRolelist() {
		return rolelist;
	}

	/**
	 * @param rolelist The rolelist to set.
	 */
	public void setRolelist(ArrayList rolelist) {
		this.rolelist = rolelist;
	}

	/**
	 * @return Returns the nodeForm.
	 */
	public PaginationForm getNodeForm() {
		return nodeForm;
	}

	/**
	 * @param nodeForm The nodeForm to set.
	 */
	public void setNodeForm(PaginationForm nodeForm) {
		this.nodeForm = nodeForm;
	}

	/**
	 * @return Returns the nodenamenew.
	 */
	public String getNodenamenew() {
		return nodenamenew;
	}

	/**
	 * @param nodenamenew The nodenamenew to set.
	 */
	public void setNodenamenew(String nodenamenew) {
		this.nodenamenew = nodenamenew;
	}

	/**
	 * @return Returns the right_fields.
	 */
	public String[] getRight_fields() {
		return right_fields;
	}

	/**
	 * @param right_fields The right_fields to set.
	 */
	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	/**
	 * @return Returns the list.
	 */
	public ArrayList getList() {
		return list;
	}

	/**
	 * @param list The list to set.
	 */
	public void setList(ArrayList list) {
		this.list = list;
	}

	@Override
    public String getReturnflag() {
		return this.returnflag;
	}

	@Override
    public void setReturnflag(String returnflag) {
		this.returnflag = returnflag;
	}

	public String getIsOperate() {
		return isOperate;
	}

	public void setIsOperate(String isOperate) {
		this.isOperate = isOperate;
	}

	public PaginationForm getNodeDefineForm() {
		return NodeDefineForm;
	}

	public void setNodeDefineForm(PaginationForm nodeDefineForm) {
		NodeDefineForm = nodeDefineForm;
	}

}
