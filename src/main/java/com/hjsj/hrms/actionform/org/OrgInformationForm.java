/*
 * Created on 2005-12-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.org;

import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OrgInformationForm extends FrameForm {

	/**信息群标识*/
	private String infor_Flag="1";
	//机构集编码
	private String codesetid;
	//机构编码
	private String codeitemid;
	//机构描述
	private String codeitemdesc;
	//机构父接点
	private String parentid;
	//机构子节点
	private String childid;
	//状态
	private String state;
	//机构层
	private String grade;
	//转换代码
	private String corcode;
	//人员编码
	private String a0000;
	private String groupid;
	private String pos_cond;
	//加载组织结构数代码
	private String treeCode;
	//某机构项的编码
	private String code;
	//某机构的编码长度
	private String len;
	private String kind;
	private String labelmessage;
	private String first;
	private String isrefresh;
	private ArrayList codesetlist=new ArrayList();
	//划转的目标的机构名
	private String tarcodeitemdesc;
	//是否划转
	private String istransfer;
	//过滤划转到机构单位，部门
	private String ishavedept;
	//目标树
	private String tarTreeCode;
	private String transfercodeitemid;
	private ArrayList transferorglist=new ArrayList();
	//撤销结构人的转移到的机构列表
	private String[] right_fields;
	private String dbpre;
	private ArrayList dbprelist=new ArrayList();
	private ArrayList bolishlist=new ArrayList();
	//撤销的机构名
	private String bolishorgname;
	private String tarorgname;
	private String tarorgid;
	private String[] left_fields;
	private String ishavepersonmessage;
	private ArrayList list=new ArrayList();
	private ArrayList movepersons=new ArrayList();
	private ArrayList orgcodeitemidlist=new ArrayList(); 
    private ArrayList chooselist=new ArrayList();
    private String chooseed;
	private String input_orgid;//数据输入的orgid
	private FormFile file;
	private ArrayList codelist;
	private String issuperuser;
	private String manageprive;
	private String transfercodeitemidall;
	private ArrayList newidlist = new ArrayList();
	private String firstNodeCode;
	private String combinetext;
	private String vorganization = "0";
	private String vflag;
	private String orgtype;
	private String isorg;
	private String start_date;//有效日期起
	private String end_date;//有效日期止  xuj 2009-10-30 在organazition（vorganization虚拟机构）表中增加“有效日期起”、“有效日期止”两个字段
	private String combinecodeitemid;//合并后的机构编码
	private ArrayList archivedatelist=new ArrayList();//归档时间点
	private String backdate;//还原时间点
	private ArrayList codeitemlist = new ArrayList();//合并后待选代码
	private ArrayList fieldlist = new ArrayList();//变动历史子集指标
	private String changemsg;//是否显示变动信息
	private String isnewcombineorg;//标示组织合并时是不是采用的新的机构编码 yes 是 no 不是
	//private String msgb0110;//要存变更信息的b0110集合
	private String returnvalue;
	private String query;
	private String idordesc;
	private String unitfillable;
	private String posfillable;
	
	private String addFuncFlag;//机构编码 新增机构功能控制标识
	
	private String virtualOrgSet;//虚拟机构是否可编辑标识
	
	public String getFirstNodeCode() {
		return firstNodeCode;
	}
	public void setFirstNodeCode(String firstNodeCode) {
		this.firstNodeCode = firstNodeCode;
	}
	public ArrayList getNewidlist() {
		return newidlist;
	}
	public void setNewidlist(ArrayList newidlist) {
		this.newidlist = newidlist;
	}
	public ArrayList getCodelist() {
		return codelist;
	}
	public void setCodelist(ArrayList codelist) {
		this.codelist = codelist;
	}
	public FormFile getFile() {
		return file;
	}
	public void setFile(FormFile file) {
		this.file = file;
	}
	public String getInput_orgid() {
		return input_orgid;
	}
	public void setInput_orgid(String input_orgid) {
		this.input_orgid = input_orgid;
	}
	public String getChooseed() {
		return chooseed;
	}
	public void setChooseed(String chooseed) {
		this.chooseed = chooseed;
	}
	public ArrayList getChooselist() {
		return chooselist;
	}
	public void setChooselist(ArrayList chooselist) {
		this.chooselist = chooselist;
	}
	/**
	 * @return Returns the len.
	 */
	public String getLen() {
		return len;
	}
	/**
	 * @param len The len to set.
	 */
	public void setLen(String len) {
		this.len = len;
	}

	/**
	 * @return Returns the code.
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code The code to set.
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return Returns the kind.
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * @param kind The kind to set.
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		// TODO Auto-generated method stub
		super.reset(mapping, request);
		try{
			if(this.getFieldlist()!=null)
				for(int i=0;i<this.getFieldlist().size();i++){
					FieldItem fielditem = (FieldItem)this.getFieldlist().get(i);
					fielditem.setValue("");
					fielditem.setViewvalue("");
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		this.left_fields=null;
		this.isrefresh="";
		
	}
	private PaginationForm organizationForm=new PaginationForm();
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.getOrganizationForm().setList((ArrayList)this.getFormHM().get("orglist"));
        this.setLabelmessage((String)this.getFormHM().get("labelmessage"));
	    this.setCodesetlist((ArrayList)this.getFormHM().get("codesetlist"));
	    this.setKind((String)this.getFormHM().get("kind"));
	    this.setLen((String)this.getFormHM().get("len"));
	    this.setFirst((String)this.getFormHM().get("first"));
	    this.setGrade((String)this.getFormHM().get("grade"));
	    this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
	    this.setCodeitemdesc((String)this.getFormHM().get("codeitemdesc"));
	    this.setIsrefresh((String)this.getFormHM().get("isrefresh"));	
	    this.setIstransfer((String)this.getFormHM().get("istransfer"));
	    this.setIshavedept((String)this.getFormHM().get("ishavedept"));
	    this.setTarTreeCode((String)this.getFormHM().get("tarTreeCode"));
	    this.setTransferorglist((ArrayList)this.getFormHM().get("transferorglist"));
	    this.setBolishlist((ArrayList)this.getFormHM().get("bolishlist"));
	    this.setDbprelist((ArrayList)this.getFormHM().get("dbprelist"));
	    this.setIshavepersonmessage((String)this.getFormHM().get("ishavepersonmessage"));
	    this.setList((ArrayList)this.getFormHM().get("inforlist"));
	    this.setRight_fields((String[])this.getFormHM().get("right_fields"));
	    if(this.getFormHM().get("movepersons")!=null)
	         this.setMovepersons((ArrayList)this.getFormHM().get("movepersons"));
	    this.setCodesetid((String)this.getFormHM().get("codesetid"));
	    this.setChooselist((ArrayList)this.getFormHM().get("chooselist"));
	    this.setCode((String)this.getFormHM().get("code"));
	    this.setCodelist((ArrayList)this.getFormHM().get("codelist"));
	    this.setIssuperuser((String)this.getFormHM().get("issuperuser"));
	    this.setManageprive((String)this.getFormHM().get("manageprive"));
	    this.setTransfercodeitemidall((String)this.getFormHM().get("transfercodeitemidall"));
	    this.setNewidlist((ArrayList)this.getFormHM().get("newidlist"));
	    this.setFirstNodeCode((String)this.getFormHM().get("firstNodeCode"));
	    this.setCombinetext((String)this.getFormHM().get("combinetext"));
	    this.setOrgtype((String)this.getFormHM().get("orgtype"));
	    this.setIsorg((String)this.getFormHM().get("isorg"));
	    this.setCorcode((String)this.getFormHM().get("corcode"));
	    this.setStart_date((String)this.getFormHM().get("start_date"));
	    this.setEnd_date((String)this.getFormHM().get("end_date"));
	    this.setArchivedatelist((ArrayList)this.getFormHM().get("archivedatelist"));
	    this.setCodeitemlist((ArrayList)this.getFormHM().get("codeitemlist"));
	    this.setFieldlist((ArrayList)this.getFormHM().get("childfielditemlist"));
	    this.setChangemsg((String)this.getFormHM().get("changemsg"));
	    this.setIsnewcombineorg((String)this.getFormHM().get("isnewcombineorg"));
	    this.setBackdate((String)this.getFormHM().get("backdate"));
	    this.setPosfillable((String)this.getFormHM().get("posfillable"));
	    this.setUnitfillable((String)this.getFormHM().get("unitfillable"));
	    this.setAddFuncFlag((String)this.getFormHM().get("addFuncFlag"));
	    this.setvirtualOrgSet((String)this.getFormHM().get("virtualOrgSet"));
	}
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		String vflag=SystemConfig.getPropertyValue("vorganization");
		if(vflag==null|| "false".equals(vflag)|| "".equals(vflag)){
			this.setVflag("0");
		}else{
			this.setVflag("1");
		}
		
		this.getFormHM().put("selectedlist",this.getOrganizationForm().getSelectedList());
        this.getFormHM().put("code",code);
        this.getFormHM().put("kind",kind);
        this.getFormHM().put("codeitemid",codeitemid);
        this.getFormHM().put("codesetid",codesetid);
        this.getFormHM().put("first",first);
        this.getFormHM().put("grade",grade);
        this.getFormHM().put("codeitemdesc",codeitemdesc);
        this.getFormHM().put("tarcodeitemdesc",tarcodeitemdesc);
        this.getFormHM().put("istransfer",istransfer);
        this.getFormHM().put("ishavedept",ishavedept);
        this.getFormHM().put("transfercodeitemid",transfercodeitemid);
        this.getFormHM().put("transferorglist",transferorglist);
        //撤销人的转移到的结构
        this.getFormHM().put("persontoorg",this.tarorgid);
        this.getFormHM().put("persons",this.left_fields);
        this.getFormHM().put("bolishlist",bolishlist);
        this.getFormHM().put("dbpre",dbpre);
        this.getFormHM().put("bolishorgname",bolishorgname);
        this.getFormHM().put("right_fields",this.getRight_fields());
        this.getFormHM().put("chooseed",this.chooseed);
        this.getFormHM().put("orgcodeitemidlist",orgcodeitemidlist);
        this.getFormHM().put("input_orgid",this.input_orgid);
        this.getFormHM().put("file",this.getFile());
        this.getFormHM().put("vorganization",this.getVorganization());
        this.getFormHM().put("vflag",this.getVflag());
        this.getFormHM().put("corcode", this.getCorcode());
        this.getFormHM().put("start_date", this.getStart_date());
        this.getFormHM().put("end_date", this.getEnd_date());
        this.getFormHM().put("combinecodeitemid", this.getCombinecodeitemid());
        this.getFormHM().put("backdate", this.getBackdate());
        this.getFormHM().put("fieldlist", this.getFieldlist());
        //this.getFormHM().put("msgb0110", this.getMsgb0110());
        this.getFormHM().put("movepersons", this.movepersons);
        this.getFormHM().put("query", query);
        this.getFormHM().put("idordesc", idordesc);
        this.getFormHM().put("isorg", this.getIsorg());
        this.getFormHM().put("virtualOrgSet", virtualOrgSet);
	}
	/**
	 * @return Returns the a0000.
	 */
	public String getA0000() {
		return a0000;
	}
	/**
	 * @param a0000 The a0000 to set.
	 */
	public void setA0000(String a0000) {
		this.a0000 = a0000;
	}
	/**
	 * @return Returns the childid.
	 */
	public String getChildid() {
		return childid;
	}
	/**
	 * @param childid The childid to set.
	 */
	public void setChildid(String childid) {
		this.childid = childid;
	}
	/**
	 * @return Returns the codeitemdesc.
	 */
	public String getCodeitemdesc() {
		return codeitemdesc;
	}
	/**
	 * @param codeitemdesc The codeitemdesc to set.
	 */
	public void setCodeitemdesc(String codeitemdesc) {
		this.codeitemdesc = codeitemdesc;
	}
	/**
	 * @return Returns the codeitemid.
	 */
	public String getCodeitemid() {
		return codeitemid;
	}
	/**
	 * @param codeitemid The codeitemid to set.
	 */
	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}
	/**
	 * @return Returns the codesetid.
	 */
	public String getCodesetid() {
		return codesetid;
	}
	/**
	 * @param codesetid The codesetid to set.
	 */
	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}
	/**
	 * @return Returns the grade.
	 */
	public String getGrade() {
		return grade;
	}
	/**
	 * @param grade The grade to set.
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}
	/**
	 * @return Returns the groupid.
	 */
	public String getGroupid() {
		return groupid;
	}
	/**
	 * @param groupid The groupid to set.
	 */
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	/**
	 * @return Returns the parentid.
	 */
	public String getParentid() {
		return parentid;
	}
	/**
	 * @param parentid The parentid to set.
	 */
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	/**
	 * @return Returns the pos_cond.
	 */
	public String getPos_cond() {
		return pos_cond;
	}
	/**
	 * @param pos_cond The pos_cond to set.
	 */
	public void setPos_cond(String pos_cond) {
		this.pos_cond = pos_cond;
	}
	/**
	 * @return Returns the state.
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state The state to set.
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return Returns the treeCode.
	 */
	public String getTreeCode() {
		return treeCode;
	}
	/**
	 * @param treeCode The treeCode to set.
	 */
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	/**
	 * @return Returns the organizationForm.
	 */
	public PaginationForm getOrganizationForm() {
		return organizationForm;
	}
	/**
	 * @param organizationForm The organizationForm to set.
	 */
	public void setOrganizationForm(PaginationForm organizationForm) {
		this.organizationForm = organizationForm;
	}
	/**
	 * @return Returns the labelmessage.
	 */
	public String getLabelmessage() {
		return labelmessage;
	}
	/**
	 * @param labelmessage The labelmessage to set.
	 */
	public void setLabelmessage(String labelmessage) {
		this.labelmessage = labelmessage;
	}
	/**
	 * @return Returns the codesetlist.
	 */
	public ArrayList getCodesetlist() {
		return codesetlist;
	}
	/**
	 * @param codesetlist The codesetlist to set.
	 */
	public void setCodesetlist(ArrayList codesetlist) {
		this.codesetlist = codesetlist;
	}
	/**
	 * @return Returns the first.
	 */
	public String getFirst() {
		return first;
	}
	/**
	 * @param first The first to set.
	 */
	public void setFirst(String first) {
		this.first = first;
	}
	/**
	 * @return Returns the isrefresh.
	 */
	public String getIsrefresh() {
		return isrefresh;
	}
	/**
	 * @param isrefresh The isrefresh to set.
	 */
	public void setIsrefresh(String isrefresh) {
		this.isrefresh = isrefresh;
	}

	/**
	 * @return Returns the tarcodeitemdesc.
	 */
	public String getTarcodeitemdesc() {
		return tarcodeitemdesc;
	}
	/**
	 * @param tarcodeitemdesc The tarcodeitemdesc to set.
	 */
	public void setTarcodeitemdesc(String tarcodeitemdesc) {
		this.tarcodeitemdesc = tarcodeitemdesc;
	}
	
	/**
	 * @return Returns the istransfer.
	 */
	public String getIstransfer() {
		return istransfer;
	}
	/**
	 * @param istransfer The istransfer to set.
	 */
	public void setIstransfer(String istransfer) {
		this.istransfer = istransfer;
	}
	/**
	 * @return Returns the ishavedept.
	 */
	public String getIshavedept() {
		return ishavedept;
	}
	/**
	 * @param ishavedept The ishavedept to set.
	 */
	public void setIshavedept(String ishavedept) {
		this.ishavedept = ishavedept;
	}
	/**
	 * @return Returns the tarTreeCode.
	 */
	public String getTarTreeCode() {
		return tarTreeCode;
	}
	/**
	 * @param tarTreeCode The tarTreeCode to set.
	 */
	public void setTarTreeCode(String tarTreeCode) {
		this.tarTreeCode = tarTreeCode;
	}
	/**
	 * @return Returns the transfercodeitemid.
	 */
	public String getTransfercodeitemid() {
		return transfercodeitemid;
	}
	/**
	 * @param transfercodeitemid The transfercodeitemid to set.
	 */
	public void setTransfercodeitemid(String transfercodeitemid) {
		this.transfercodeitemid = transfercodeitemid;
	}
	/**
	 * @return Returns the transferorglist.
	 */
	public ArrayList getTransferorglist() {
		return transferorglist;
	}
	/**
	 * @param transferorglist The transferorglist to set.
	 */
	public void setTransferorglist(ArrayList transferorglist) {
		this.transferorglist = transferorglist;
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
	 * @return Returns the infor_Flag.
	 */
	public String getInfor_Flag() {
		return infor_Flag;
	}
	/**
	 * @param infor_Flag The infor_Flag to set.
	 */
	public void setInfor_Flag(String infor_Flag) {
		this.infor_Flag = infor_Flag;
	}
	
	/**
	 * @return Returns the bolishlist.
	 */
	public ArrayList getBolishlist() {
		return bolishlist;
	}
	/**
	 * @param bolishlist The bolishlist to set.
	 */
	public void setBolishlist(ArrayList bolishlist) {
		this.bolishlist = bolishlist;
	}
	/**
	 * @return Returns the bolishorgname.
	 */
	public String getBolishorgname() {
		return bolishorgname;
	}
	/**
	 * @param bolishorgname The bolishorgname to set.
	 */
	public void setBolishorgname(String bolishorgname) {
		this.bolishorgname = bolishorgname;
	}
	/**
	 * @return Returns the tarorgname.
	 */
	public String getTarorgname() {
		return tarorgname;
	}
	/**
	 * @param tarorgname The tarorgname to set.
	 */
	public void setTarorgname(String tarorgname) {
		this.tarorgname = tarorgname;
	}
	/**
	 * @return Returns the tarorgid.
	 */
	public String getTarorgid() {
		return tarorgid;
	}
	/**
	 * @param tarorgid The tarorgid to set.
	 */
	public void setTarorgid(String tarorgid) {
		this.tarorgid = tarorgid;
	}
	/**
	 * @return Returns the left_fields.
	 */
	public String[] getLeft_fields() {
		return left_fields;
	}
	/**
	 * @param left_fields The left_fields to set.
	 */
	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}
	/**
	 * @return Returns the dbpre.
	 */
	public String getDbpre() {
		return dbpre;
	}
	/**
	 * @param dbpre The dbpre to set.
	 */
	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}
	/**
	 * @return Returns the dbprelist.
	 */
	public ArrayList getDbprelist() {
		return dbprelist;
	}
	/**
	 * @param dbprelist The dbprelist to set.
	 */
	public void setDbprelist(ArrayList dbprelist) {
		this.dbprelist = dbprelist;
	}
	/**
	 * @return Returns the ishavepersonmessage.
	 */
	public String getIshavepersonmessage() {
		return ishavepersonmessage;
	}
	/**
	 * @param ishavepersonmessage The ishavepersonmessage to set.
	 */
	public void setIshavepersonmessage(String ishavepersonmessage) {
		this.ishavepersonmessage = ishavepersonmessage;
	}
	public ArrayList getList() {
		return list;
	}
	public void setList(ArrayList list) {
		this.list = list;
	}
	public ArrayList getMovepersons() {
		return movepersons;
	}
	public void setMovepersons(ArrayList movepersons) {
		this.movepersons = movepersons;
	}
	
	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	   {
	        if("/org/orginfo/searchorglist".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null)
	        {
	            if(this.organizationForm.getPagination()!=null)
	            	this.organizationForm.getPagination().firstPage();//?
	        }	
	        if("/org/orginfo/searchorgtree".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	            if(this.organizationForm.getPagination()!=null)
	            	this.organizationForm.getPagination().firstPage();//?
	        }	
	        if("/org/orginfo/searchorglist".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
	        {
	        	this.setCodeitemid("");
            }
	        if("/org/orginfo/searchorglist".equals(arg0.getPath()) && arg1.getParameter("b_kind")!=null)
	        {
	        	this.setCodeitemid("");
            }
	        if("/org/orginfo/searchorglist".equals(arg0.getPath()) && arg1.getParameter("b_inputorgtree")!=null){
	        	arg1.setAttribute("targetWindow", "1");
	        }
	        	
       return super.validate(arg0, arg1);
	    }
	public ArrayList getOrgcodeitemidlist() {
		return orgcodeitemidlist;
	}
	public void setOrgcodeitemidlist(ArrayList orgcodeitemidlist) {
		this.orgcodeitemidlist = orgcodeitemidlist;
	}
	public String getIssuperuser() {
		return issuperuser;
	}
	public void setIssuperuser(String issuperuser) {
		this.issuperuser = issuperuser;
	}
	public String getManageprive() {
		return manageprive;
	}
	public void setManageprive(String manageprive) {
		this.manageprive = manageprive;
	}
	public String getTransfercodeitemidall() {
		return transfercodeitemidall;
	}
	public void setTransfercodeitemidall(String transfercodeitemidall) {
		this.transfercodeitemidall = transfercodeitemidall;
	}
	public String getCombinetext() {
		return combinetext;
	}
	public void setCombinetext(String combinetext) {
		this.combinetext = combinetext;
	}
	public String getVorganization() {
		return vorganization;
	}
	public void setVorganization(String vorganization) {
		this.vorganization = vorganization;
	}
	public String getVflag() {
		return vflag;
	}
	public void setVflag(String vflag) {
		this.vflag = vflag;
	}
	public String getOrgtype() {
		return orgtype;
	}
	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}
	public String getIsorg() {
		return isorg;
	}
	public void setIsorg(String isorg) {
		this.isorg = isorg;
	}
	public String getCorcode() {
		return corcode;
	}
	public void setCorcode(String corcode) {
		this.corcode = corcode;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getCombinecodeitemid() {
		return combinecodeitemid;
	}
	public void setCombinecodeitemid(String combinecodeitemid) {
		this.combinecodeitemid = combinecodeitemid;
	}
	public ArrayList getArchivedatelist() {
		return archivedatelist;
	}
	public void setArchivedatelist(ArrayList archivedatelist) {
		this.archivedatelist = archivedatelist;
	}
	public String getBackdate() {
		return backdate;
	}
	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}
	public ArrayList getCodeitemlist() {
		return codeitemlist;
	}
	public void setCodeitemlist(ArrayList codeitemlist) {
		this.codeitemlist = codeitemlist;
	}
	public String getChangemsg() {
		return changemsg;
	}
	public void setChangemsg(String changemsg) {
		this.changemsg = changemsg;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public String getIsnewcombineorg() {
		return isnewcombineorg;
	}
	public void setIsnewcombineorg(String isnewcombineorg) {
		this.isnewcombineorg = isnewcombineorg;
	}
//	public String getMsgb0110() {
//		return msgb0110;
//	}
//	public void setMsgb0110(String msgb0110) {
//		this.msgb0110 = msgb0110;
//	}
	public String getReturnvalue() {
		return returnvalue;
	}
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getIdordesc() {
		return idordesc;
	}
	public void setIdordesc(String idordesc) {
		this.idordesc = idordesc;
	}
	public String getUnitfillable() {
		return unitfillable;
	}
	public void setUnitfillable(String unitfillable) {
		this.unitfillable = unitfillable;
	}
	public String getPosfillable() {
		return posfillable;
	}
	public void setPosfillable(String posfillable) {
		this.posfillable = posfillable;
	}
	public String getAddFuncFlag() {
		return addFuncFlag;
	}
	public void setAddFuncFlag(String addFuncFlag) {
		this.addFuncFlag = addFuncFlag;
	}
	public String getvirtualOrgSet() {
		return virtualOrgSet;
	}
	public void setvirtualOrgSet(String virtualOrgSet) {
		this.virtualOrgSet = virtualOrgSet;
	}
	
}
