/**
 * 
 */
package com.hjsj.hrms.actionform.general.inform;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 *<p>Title:MInformForm</p> 
 *<p>Description:信息维护表单</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-15:下午01:21:28</p> 
 *@author cmq
 *@version 4.0
 */
public class MInformForm extends FrameForm {
	/**应用库前缀*/
	private String dbname;
	/***数据表名称*/
	private String tablename;
	/**查询语句*/
	private String sql;
	/**查询语句*/
	private String itemsql;
	/**字段列表*/
	private ArrayList fieldlist=new ArrayList();
	/**子集列表*/
	private ArrayList setlist=new ArrayList();
	/**应用库前缀*/
	private ArrayList dblist=new ArrayList();
	/**选中的信息集名称*/
	private String setname;
    /**组织机构代码*/
    private String a_code;
    /**隐藏&显示指标*/
    private ArrayList hidefieldlist;
    /**指标排序*/
    private String sortfieldstr;
    /**指标排序*/
    private ArrayList sortfieldlist;
    /**排序数组*/
    private String sort_fields[];
    /**A0100*/
    private String a0100;
    /**照片*/
	private FormFile picturefile;
	/**操作类型*/
    private String type="";
    /**多媒体文件数组*/
    private String multimedia[];
    /**多媒体文件list*/
    private ArrayList multimedialist;
    /**i9999字段*/
    private String i9999="";
    /**多媒体文件的类型*/
    
	private String b0110;	
	private String e0122;
	private String e01a1;
	private String a0101;
	
    private String multimediaflag="";
    /**多媒体文件夹树*/
    private String treemenu="";
    /**文件上传路径*/
    private String filepath="";
    /**上传的文件类型*/
    private String filetype="";
    /**上传的文件标题*/
    private String filetitle="";
    /**文件夹名称*/
    private String foldername="";
    /**移动记录*/
    private String recordnum="1";
    /**排序*/
    private String sort_str;
    /**排序范围*/
    private String sort_record_scope;
    
    private PaginationForm recordListForm=new PaginationForm();    
    
    private ArrayList list=new ArrayList();
    /**添加按钮隐藏标识，当值为‘1’是隐藏  */
    private String buttonflag ="";
    
    /**常用查询*/
    private ArrayList searchlist = new ArrayList();
    /**是否显示查询结果*/
    private String viewsearch;
    
    private int current=1;
    
    private String kind="";
    
	private String pos="";
	
	private String unit="";
	
	/**是否显示照片*/
	private String display_state; 
	
	private String check_main;
	/**新增多媒体文件的权限*/
	private String newFilePriv;
	/**新增多媒体分类*/
	private ArrayList fileTypeList = new ArrayList();
	/**判断是业务还是自助平台*/
	private String is_yewu;
    /**照片的宽*/
	private String photo_w;
	/**照片的高*/
	private String photo_h;
	/**人员库主集中的代码指标list*/
	private ArrayList codeitemlist=new ArrayList();
	private String codeitem;
	private String viewdata;/**是否显示当前记录*/
	private String prive;/**子集权限值*/
	private String isvisible;/**控制页面是否显示关闭按钮*/
	private String inforflag="";/**1.人员维护  2.外部培训*/
	private String viewbutton="";/**控制页面是否显示按钮*/
	private String photo_maxsize;
	private String codeitemid=""; /**导航指标*/
	private String itemtable;
	/**字段列表*/
	private ArrayList itemlist=new ArrayList();
	private String modleflag; /**显示方式 0.上下显示  1.列表方式*/

	private ArrayList fieldslist = new ArrayList();
	private String orgparentcode="";
	private String deptparentcode="";
	private String posparentcode="";
	private String defitem=""; /**选项卡默认子集按钮*/
	private ArrayList subFlds = new ArrayList();
	private String recordCount="";
	private ArrayList readOnlyFlds = new ArrayList();
	private String empInfo="";
	private String t_vorg="";
	private String keys="";
	private String isInsert="0";
	private String isTestBirthday="0";
	private String reserveitem="";
	private String resitemid="";
	private String curri9999 = "";//插入记录时用
	/**是否需要审核，1为需要，0为不需要*/
	private String approveflag = "";
	/**是否直接入库，如果1：不直接入库；0为直接入库*/
	private String inputchinfor = "";
	private String returnvalue;
	private String isself;
	
	private String encryptParam;   //add by wangchaoqun on 2014-9-11
	private boolean fileHasPro;    //add by wangchaoqun on 2014-9-12
	
	private String train; //train：外部培训，为其他值时，不加载此参数
	public boolean isFileHasPro() {
		return fileHasPro;
	}

	public void setFileHasPro(boolean fileHasPro) {
		this.fileHasPro = fileHasPro;
	}

	public String getEncryptParam() {
		return encryptParam;
	}

	public void setEncryptParam(String encryptParam) {
		this.encryptParam = encryptParam;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("isvisible", this.getIsvisible());
		this.getFormHM().put("dbname", this.getDbname());
		this.getFormHM().put("setname", this.getSetname());
		this.getFormHM().put("a_code", this.getA_code());
		this.getFormHM().put("sortfieldstr",this.getSortfieldstr());
		this.getFormHM().put("sort_fields",this.getSort_fields());
		this.getFormHM().put("a0100",this.getA0100());
		this.getFormHM().put("picturefile",this.getPicturefile());
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("multimedia",this.getMultimedia());
		this.getFormHM().put("i9999",this.getI9999());
		this.getFormHM().put("multimediaflag",this.getMultimediaflag());
		this.getFormHM().put("treemenu",this.getTreemenu());
		this.getFormHM().put("filepath",this.getFilepath());
		this.getFormHM().put("foldername",this.getFoldername());
		this.getFormHM().put("recordnum",this.getRecordnum());
		this.getFormHM().put("sort_str",this.getSort_str());
		this.getFormHM().put("sort_record_scope",this.getSort_record_scope());
		this.getFormHM().put("viewsearch",this.getViewsearch());
		this.getFormHM().put("selectedlist",(ArrayList)this.getRecordListForm().getSelectedList());
		this.getFormHM().put("filetype",this.getFiletype());
		this.getFormHM().put("filetitle",this.getFiletitle());
		this.getFormHM().put("kind",this.getKind());
		this.getFormHM().put("viewdata",this.getViewdata());
		this.getFormHM().put("inforflag",this.getInforflag());
		
		this.getFormHM().put("orgparentcode",this.getOrgparentcode());
		this.getFormHM().put("deptparentcode",this.getDeptparentcode());
		this.getFormHM().put("posparentcode",this.getPosparentcode());
		this.getFormHM().put("defitem",this.getDefitem());
		this.getFormHM().put("SubFlds",this.getSubFlds());
		this.getFormHM().put("recordCount",this.getRecordCount());
		this.getFormHM().put("readOnlyFlds",this.getReadOnlyFlds());
		this.getFormHM().put("t_vorg",this.getT_vorg());
		this.getFormHM().put("isInsert",this.getIsInsert());	
		this.getFormHM().put("isTestBirthday",this.getIsTestBirthday());
		this.getFormHM().put("curri9999",this.getCurri9999());
		this.getFormHM().put("train", this.getTrain());
	}

	@Override
    public void outPutFormHM() {
		   this.setReturnflag((String)this.getFormHM().get("returnflag"));  
	    	this.setCurri9999((String)this.getFormHM().get("curri9999"));
		this.setOrgparentcode((String)this.getFormHM().get("orgparentcode"));
		this.setDeptparentcode((String)this.getFormHM().get("deptparentcode"));
		this.setPosparentcode((String)this.getFormHM().get("posparentcode"));
		this.setIsvisible((String)this.getFormHM().get("isvisible"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setSetname((String)this.getFormHM().get("setname"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setFieldslist((ArrayList)this.getFormHM().get("fieldslist"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setHidefieldlist((ArrayList)this.getFormHM().get("hidefieldlist"));
		this.setSortfieldlist((ArrayList)this.getFormHM().get("sortfieldlist"));
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setSortfieldstr((String)this.getFormHM().get("sortfieldstr"));
		this.setPicturefile((FormFile)this.getFormHM().get("picturefile"));
		this.setMultimedia((String[])this.getFormHM().get("multimedia"));
		this.setMultimedialist((ArrayList)this.getFormHM().get("multimedialist"));
		this.setI9999((String)this.getFormHM().get("i9999"));
		this.setMultimediaflag((String)this.getFormHM().get("multimediaflag"));
		this.setTreemenu((String)this.getFormHM().get("treemenu"));
		this.setFilepath((String)this.getFormHM().get("filepath"));
		this.setFoldername((String)this.getFormHM().get("foldername"));
		this.setSort_str((String)this.getFormHM().get("sort_str"));
		this.setSort_record_scope((String)this.getFormHM().get("sort_record_scope"));
		this.setB0110((String)this.getFormHM().get("b0110"));
	    this.setE0122((String)this.getFormHM().get("e0122"));
	    this.setE01a1((String)this.getFormHM().get("e01a1"));
	    this.setA0101((String)this.getFormHM().get("a0101"));
	    this.getRecordListForm().setList((ArrayList)this.getFormHM().get("multimedialist"));
	    this.getRecordListForm().getPagination().gotoPage(current);
	    this.setFiletype((String)this.getFormHM().get("filetype"));
	    this.setFiletitle((String)this.getFormHM().get("filetitle"));
		this.setSearchlist((ArrayList)this.getFormHM().get("searchlist"));
		this.setViewsearch((String)this.getFormHM().get("viewsearch"));
		this.setKind((String)this.getFormHM().get("kind"));
	    this.setUnit((String)this.getFormHM().get("unit"));
	    this.setPos((String)this.getFormHM().get("pos"));
	    this.setDisplay_state((String)this.getFormHM().get("display_state"));
	    this.setPhoto_w((String)this.getFormHM().get("photo_w"));
	    this.setPhoto_h((String)this.getFormHM().get("photo_h"));
	    this.setCodeitemlist((ArrayList)this.getFormHM().get("codeitemlist"));
	    this.setCodeitem((String)this.getFormHM().get("codeitem"));
	    this.setCheck_main((String)this.getFormHM().get("check_main"));
	    this.setNewFilePriv((String)this.getFormHM().get("newFilePriv"));
	    this.setFileTypeList((ArrayList)this.getFormHM().get("fileTypeList"));
	    this.setIs_yewu((String)this.getFormHM().get("is_yewu"));
	    this.setViewdata((String)this.getFormHM().get("viewdata"));
	    this.setPrive((String)this.getFormHM().get("prive"));
	    this.setInforflag((String)this.getFormHM().get("inforflag"));
	    this.setViewbutton((String)this.getFormHM().get("viewbutton"));
	    this.setPhoto_maxsize((String)this.getFormHM().get("photo_maxsize"));
	    this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
	    this.setItemsql((String)this.getFormHM().get("itemsql"));
	    this.setItemtable((String)this.getFormHM().get("itemtable"));
	    this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
	    this.setModleflag((String)this.getFormHM().get("modleflag"));
	    this.setDefitem((String)this.getFormHM().get("defitem"));
	    this.setSubFlds((ArrayList)this.getFormHM().get("SubFlds"));
	    this.setRecordCount((String)this.getFormHM().get("recordCount"));
	    this.setReadOnlyFlds((ArrayList)this.getFormHM().get("readOnlyFlds"));
	    this.setT_vorg((String)this.getFormHM().get("t_vorg"));
	    this.setKeys((String)this.getFormHM().get("keys"));
	    this.setIsInsert((String)this.getFormHM().get("isInsert"));
	    this.setIsTestBirthday((String)this.getFormHM().get("isTestBirthday"));
	    this.setReserveitem((String)this.getFormHM().get("reserveitem"));
	    this.setResitemid((String)this.getFormHM().get("resitemid"));
	    this.setInputchinfor((String) this.getFormHM().get("inputchinfor"));
	    this.setApproveflag((String) this.getFormHM().get("approveflag"));
	    this.setButtonflag((String)this.getFormHM().get("buttonflag"));
	    this.setEmpInfo((String)this.getFormHM().get("empInfo"));
	    this.setEncryptParam((String)this.getFormHM().get("encryptParam"));
	    if("true".equals(this.getFormHM().get("fileHasPro"))){  
	    	//当文件存在问题时，将fileHasPro设为true
	    	this.setFileHasPro(true);
	    	this.getFormHM().remove("fileHasPro");
	    }
	    this.setTrain((String) this.getFormHM().get("train"));
	}

    public String getButtonflag() {
		return buttonflag;
	}

	public void setButtonflag(String buttonflag) {
		this.buttonflag = buttonflag;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}
    
	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	
	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public ArrayList getHidefieldlist() {
		return hidefieldlist;
	}

	public void setHidefieldlist(ArrayList hidefieldlist) {
		this.hidefieldlist = hidefieldlist;
	}

	public String getSortfieldstr() {
		return sortfieldstr;
	}

	public void setSortfieldstr(String sortfieldstr) {
		this.sortfieldstr = sortfieldstr;
	}

	public ArrayList getSortfieldlist() {
		return sortfieldlist;
	}

	public void setSortfieldlist(ArrayList sortfieldlist) {
		this.sortfieldlist = sortfieldlist;
	}

	public String[] getSort_fields() {
		return sort_fields;
	}

	public void setSort_fields(String[] sort_fields) {
		this.sort_fields = sort_fields;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public FormFile getPicturefile() {
		return picturefile;
	}

	public void setPicturefile(FormFile picturefile) {
		this.picturefile = picturefile;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getMultimedia() {
		return multimedia;
	}

	public void setMultimedia(String[] multimedia) {
		this.multimedia = multimedia;
	}

	public ArrayList getMultimedialist() {
		return multimedialist;
	}

	public void setMultimedialist(ArrayList multimedialist) {
		this.multimedialist = multimedialist;
	}

	public String getI9999() {
		return i9999;
	}

	public void setI9999(String i9999) {
		this.i9999 = i9999;
	}

	public String getTreemenu() {
		return treemenu;
	}

	public void setTreemenu(String treemenu) {
		this.treemenu = treemenu;
	}

	public String getMultimediaflag() {
		return multimediaflag;
	}

	public void setMultimediaflag(String multimediaflag) {
		this.multimediaflag = multimediaflag;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getFoldername() {
		return foldername;
	}

	public void setFoldername(String foldername) {
		this.foldername = foldername;
	}

	public String getRecordnum() {
		return recordnum;
	}

	public void setRecordnum(String recordnum) {
		this.recordnum = recordnum;
	}

	public String getSort_str() {
		return sort_str;
	}

	public void setSort_str(String sort_str) {
		this.sort_str = sort_str;
	}

	public String getSort_record_scope() {
		return sort_record_scope;
	}

	public void setSort_record_scope(String sort_record_scope) {
		this.sort_record_scope = sort_record_scope;
	}

	public String getA0101() {
		return a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getE0122() {
		return e0122;
	}

	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}

	public String getE01a1() {
		return e01a1;
	}

	public void setE01a1(String e01a1) {
		this.e01a1 = e01a1;
	}


	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
	   try
	   {
	    if("/general/inform/emp/view/opermultimedia".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();
        }	
	    if("/general/inform/emp/view/opermultimedia".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
        	if(this.recordListForm.getPagination()!=null)
        	 current=this.recordListForm.getPagination().getCurrent();
        }  
        if("/general/inform/emp/view/opermultimedia".equals(arg0.getPath()) && arg1.getParameter("br_add")!=null)
        {
        	if(this.recordListForm.getPagination()!=null)
        	  current=this.recordListForm.getPagination().getCurrent();
        }
        if("/general/inform/emp/view/opermultimedia".equals(arg0.getPath()) && arg1.getParameter("br_update")!=null)
        {
        	if(this.recordListForm.getPagination()!=null)
        	  current=this.recordListForm.getPagination().getCurrent();
        }
        if("/general/inform/org_tree".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
        	if(this.recordListForm.getPagination()!=null)
        	  current=this.recordListForm.getPagination().getCurrent();
        	this.setSetname("");
        }
        //【9265】自助服务-员工信息-多媒体（对已经批准的进行退回时，会报出退回失败，下面有个返回按钮，但是返回按钮不好使）  jingq add 2015.04.30
        if("/general/inform/emp/view/opermultimedia".equals(arg0.getPath())&&arg1.getParameter("b_approve")!=null){
        	arg1.setAttribute("targetWindow", "1");
        }
	   }catch(Exception e)
	   {
	   	  e.printStackTrace();
	   }
         return super.validate(arg0, arg1);
	}

	public PaginationForm getRecordListForm() {
		return recordListForm;
	}

	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getFiletitle() {
		return filetitle;
	}

	public void setFiletitle(String filetitle) {
		this.filetitle = filetitle;
	}

	
	public ArrayList getSearchlist() {
		return searchlist;
	}

	public void setSearchlist(ArrayList searchlist) {
		this.searchlist = searchlist;
	}

	public String getViewsearch() {
		return viewsearch;
	}

	public void setViewsearch(String viewsearch) {
		this.viewsearch = viewsearch;
	}

	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getDisplay_state() {
		return display_state;
	}

	public void setDisplay_state(String display_state) {
		this.display_state = display_state;
	}

	public String getPhoto_h() {
		return photo_h;
	}

	public void setPhoto_h(String photo_h) {
		this.photo_h = photo_h;
	}

	public String getPhoto_w() {
		return photo_w;
	}

	public void setPhoto_w(String photo_w) {
		this.photo_w = photo_w;
	}

	public String getCodeitem() {
		return codeitem;
	}

	public void setCodeitem(String codeitem) {
		this.codeitem = codeitem;
	}

	public ArrayList getCodeitemlist() {
		return codeitemlist;
	}

	public void setCodeitemlist(ArrayList codeitemlist) {
		this.codeitemlist = codeitemlist;
	}

	public String getCheck_main() {
		return check_main;
	}

	public void setCheck_main(String check_main) {
		this.check_main = check_main;
	}

	public String getNewFilePriv() {
		return newFilePriv;
	}

	public void setNewFilePriv(String newFilePriv) {
		this.newFilePriv = newFilePriv;
	}

	public ArrayList getFileTypeList() {
		return fileTypeList;
	}

	public void setFileTypeList(ArrayList fileTypeList) {
		this.fileTypeList = fileTypeList;
	}

	public String getIs_yewu() {
		return is_yewu;
	}

	public void setIs_yewu(String is_yewu) {
		this.is_yewu = is_yewu;
	}

	public String getViewdata() {
		return viewdata;
	}

	public void setViewdata(String viewdata) {
		this.viewdata = viewdata;
	}

	public String getPrive() {
		return prive;
	}

	public void setPrive(String prive) {
		this.prive = prive;
	}

	public String getIsvisible() {
		return isvisible;
	}

	public void setIsvisible(String isvisible) {
		this.isvisible = isvisible;
	}

	public String getInforflag() {
		return inforflag;
	}

	public void setInforflag(String inforflag) {
		this.inforflag = inforflag;
	}

	public String getViewbutton() {
		return viewbutton;
	}

	public void setViewbutton(String viewbutton) {
		this.viewbutton = viewbutton;
	}

	public String getPhoto_maxsize() {
		return photo_maxsize;
	}

	public void setPhoto_maxsize(String photo_maxsize) {
		this.photo_maxsize = photo_maxsize;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}
	

	public ArrayList getFieldslist()
	{
	
	    return fieldslist;
	}

	public void setFieldslist(ArrayList fieldslist)
	{
	
	    this.fieldslist = fieldslist;
	}

	public String getDeptparentcode()
	{
	
	    return deptparentcode;
	}

	public void setDeptparentcode(String deptparentcode)
	{
	
	    this.deptparentcode = deptparentcode;
	}

	public String getOrgparentcode()
	{
	
	    return orgparentcode;
	}

	public void setOrgparentcode(String orgparentcode)
	{
	
	    this.orgparentcode = orgparentcode;
	}

	
	public String getPosparentcode()
	{
	
	    return posparentcode;
	}

	public void setPosparentcode(String posparentcode)
	{
	
	    this.posparentcode = posparentcode;
	}

	public String getItemsql() {
		return itemsql;
	}

	public void setItemsql(String itemsql) {
		this.itemsql = itemsql;
	}

	public String getItemtable() {
		return itemtable;
	}

	public void setItemtable(String itemtable) {
		this.itemtable = itemtable;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getModleflag() {
		return modleflag;
	}

	public void setModleflag(String modleflag) {
		this.modleflag = modleflag;
	}

	public String getDefitem() {
		return defitem;
	}

	public void setDefitem(String defitem) {
		this.defitem = defitem;
	}

	public ArrayList getSubFlds()
	{
	
	    return subFlds;
	}

	public void setSubFlds(ArrayList subFlds)
	{
	
	    this.subFlds = subFlds;
	}

	public String getRecordCount()
	{
	
	    return recordCount;
	}

	public void setRecordCount(String recordCount)
	{
	
	    this.recordCount = recordCount;
	}

	public ArrayList getReadOnlyFlds()
	{
	
	    return readOnlyFlds;
	}

	public void setReadOnlyFlds(ArrayList readOnlyFlds)
	{
	
	    this.readOnlyFlds = readOnlyFlds;
	}

	public String getT_vorg() {
		return t_vorg;
	}

	public void setT_vorg(String t_vorg) {
		this.t_vorg = t_vorg;
	}

	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

	public String getIsInsert()
	{
	
	    return isInsert;
	}

	public void setIsInsert(String isInsert)
	{
	
	    this.isInsert = isInsert;
	}

	public String getIsTestBirthday()
	{
	
	    return isTestBirthday;
	}

	public void setIsTestBirthday(String isTestBirthday)
	{
	
	    this.isTestBirthday = isTestBirthday;
	}

	public String getReserveitem() {
		return reserveitem;
	}

	public void setReserveitem(String reserveitem) {
		this.reserveitem = reserveitem;
	}

	public String getResitemid() {
		return resitemid;
	}

	public void setResitemid(String resitemid) {
		this.resitemid = resitemid;
	}

	public String getCurri9999()
	{
	
	    return curri9999;
	}

	public void setCurri9999(String curri9999)
	{
	
	    this.curri9999 = curri9999;
	}

	public String getApproveflag() {
		return approveflag;
	}

	public void setApproveflag(String approveflag) {
		this.approveflag = approveflag;
	}

	public String getInputchinfor() {
		return inputchinfor;
	}

	public void setInputchinfor(String inputchinfor) {
		this.inputchinfor = inputchinfor;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getIsself() {
		return isself;
	}

	public void setIsself(String isself) {
		this.isself = isself;
	}	
	
	public String getEmpInfo() {
		return empInfo;
	}

	public void setEmpInfo(String empInfo) {
		this.empInfo = empInfo;
	}

    public String getTrain() {
        return train;
    }

    public void setTrain(String train) {
        this.train = train;
    }
	
}
