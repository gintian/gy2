package com.hjsj.hrms.actionform.sys.dbinit;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:信息集树</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 2, 2008:1:23:35 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DbinitForm extends FrameForm {
	
	/**树*/
	private String bs_tree;
	/**信息群前缀*/
	private String infor;
	/**子集名称*/
	private String setid;
	/**列表对象*/
	private PaginationForm listForm = new PaginationForm();
	private PaginationForm listfieldForm = new PaginationForm();
	/**指标集类型*/
	private String settype;
	private ArrayList subsetList = new ArrayList();
	 /**子集变化指标，0=一般子集，1=按月变化,2=按年变化*/
    private String qobj="0";
    /** 新增指标集指标*/
    private String code;  //指标集代号
    private String name;  //指标集名称
    private String customdesc;  //构库后名字
    private String useflag;// 是否够库
    private String multimedia_file_flag;
    private String setexplain;//子集解释 guodd 2018-04-24
    /** 删除字符串*/
	private String deletestr;
	private String isrefresh;
	/** 指标信息*/
	private String indexcode;
	private String indexname;
	private String content;
	private String itemlength;  //指标长度
	private String decimalwidth; //小数位
	private ArrayList dateList = new ArrayList();
	private ArrayList joincodeList = new ArrayList(); //代码类
	private String joincodename; //名字
	private String itemtype; //指标类型
	private String codelength; //代码型长度
	private String numberlength; //数字型长度
	private String datelength; //日期型长度
	private String dateitemtype; //日期类型
	private String intitemtype;//整数类型
	private String codeitemtype; //代码类型
	private String bzitemtype; //备注类型
	/** 输入方式*/
	private String inputtype;
	/** M型代码的输入方式集合*/
	private List inputtypeMList = new ArrayList();;
	private String app_fashion="";//指标类型
	private String bitianxiang;  //保留字符；1必填；0不用控制；
	private ArrayList codesetList;//某个确定的管理所有已购库的子集
	private RecordVo busiFieldVo=null;
	/**调整人员库*/
	private PaginationForm dbListForm = new PaginationForm();
	/**构库*/
	private ArrayList setlist = new ArrayList();
	private String[] left_fields;
	private String[] right_fields;
	private String type;
	private String[] fieldsetlist;
	private String path="";
	/** 指标排序*/
	private ArrayList sortlist = new ArrayList();
	private String[] sort_fields;
	/** 采集表*/
	private ArrayList userlist = new ArrayList();//人员
	private ArrayList unitslist = new ArrayList();//单位
	private ArrayList indexlist = new ArrayList();//指标
	private String[] userid;
	private String[] unitsid;
	private String[] indexid;
	private String usefy="1"; //1为选中；0为不选中
	private String usedata="20"; 
	private String indexexplain; //指标解释
	private String unitname; //单位采集表
	private String username; //人员采集表
	private ArrayList alllist = new ArrayList();
	private String[] index_list;
	private String tableid;
	private String tablename;
	/** 指标导出*/
	private ArrayList exportlist = new ArrayList();//指标导出
	private String[] indexexport;
	//指标体系，区分用户模式和开发者模式下指标代号   jingq add 2015.01.21
	private String dev_flag;//=0/=null 用户模式，=1开发者模式

	public String getDev_flag() {
		return dev_flag;
	}

	public void setDev_flag(String dev_flag) {
		this.dev_flag = dev_flag;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("busiFieldVo",this.getBusiFieldVo());
		this.getFormHM().put("settype",this.getSettype());
		this.getFormHM().put("infor",this.getInfor());
		this.getFormHM().put("setid", this.getSetid());
		this.getFormHM().put("subsetList", this.getSubsetList());
		this.getFormHM().put("qobj",this.getQobj());
		this.getFormHM().put("name",this.getName());
		this.getFormHM().put("code",this.getCode());
		this.getFormHM().put("customdesc",this.getCustomdesc());
		this.getFormHM().put("deletestr",this.getDeletestr());
		this.getFormHM().put("useflag",this.getUseflag());
		this.getFormHM().put("multimedia_file_flag", this.getMultimedia_file_flag());
		this.getFormHM().put("setexplain", this.getSetexplain());
		this.getFormHM().put("indexcode",this.getIndexcode());
		this.getFormHM().put("indexname",this.getIndexname());
		this.getFormHM().put("content",this.getContent());
		this.getFormHM().put("itemlength",this.getItemlength());
		this.getFormHM().put("decimalwidth",this.getDecimalwidth());
		this.getFormHM().put("joincodename",this.getJoincodename());
		this.getFormHM().put("itemtype",this.getItemtype());
		this.getFormHM().put("codelength",this.getCodelength());
		this.getFormHM().put("numberlength",this.getNumberlength());
		this.getFormHM().put("datelength",this.getDatelength());
		this.getFormHM().put("dateitemtype",this.getDateitemtype());
		this.getFormHM().put("intitemtype",this.getIntitemtype());
		this.getFormHM().put("codeitemtype",this.getCodeitemtype());
		this.getFormHM().put("bzitemtype",this.getBzitemtype());
		this.getFormHM().put("app_fashion",this.getApp_fashion());
		this.getFormHM().put("bitianxiang",this.getBitianxiang());
		this.getFormHM().put("usefy",this.getUsefy());
		this.getFormHM().put("usedata",this.getUsedata());
		this.getFormHM().put("selectedlist",(ArrayList)this.getListForm().getSelectedList());
		this.getFormHM().put("selectedlistfield",(ArrayList)this.getListfieldForm().getSelectedList());
		this.getFormHM().put("dbselectedlist",(ArrayList)this.getDbListForm().getSelectedList());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("type",this.getType());
		this.getFormHM().put("path",this.getPath());
		this.getFormHM().put("dev_flag", this.getDev_flag());
	}

	@Override
    public void outPutFormHM() {
		this.setBs_tree((String)this.getFormHM().get("bs_tree"));
		this.getListForm().setList((ArrayList)this.getFormHM().get("list"));
		this.getListfieldForm().setList((ArrayList)this.getFormHM().get("list"));
		this.getDbListForm().setList((ArrayList)this.getFormHM().get("dblist"));
		this.setSubsetList((ArrayList) this.getFormHM().get("subsetList"));
		this.setName((String)this.getFormHM().get("name"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setSettype((String)this.getFormHM().get("settype"));
		this.setQobj((String)this.getFormHM().get("qobj"));
		this.setCustomdesc((String)this.getFormHM().get("customdesc"));
		this.setUseflag((String)this.getFormHM().get("useflag"));
		this.setMultimedia_file_flag((String)this.getFormHM().get("multimedia_file_flag"));
		this.setSetexplain((String)this.getFormHM().get("setexplain"));
		this.setIsrefresh((String)this.getFormHM().get("isrefresh"));
		this.setIndexcode((String)this.getFormHM().get("indexcode"));
		this.setIndexname((String)this.getFormHM().get("indexname"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setItemlength((String)this.getFormHM().get("itemlength"));
		this.setDecimalwidth((String)this.getFormHM().get("decimalwidth"));
		this.setDateList((ArrayList) this.getFormHM().get("dateList"));
		this.setJoincodeList((ArrayList) this.getFormHM().get("joincodeList"));
		this.setJoincodename((String)this.getFormHM().get("joincodename"));
		this.setItemtype((String)this.getFormHM().get("itemtype"));
		this.setCodelength((String)this.getFormHM().get("codelength"));
		this.setNumberlength((String)this.getFormHM().get("numberlength"));
		this.setDatelength((String)this.getFormHM().get("datelength"));
		this.setDateitemtype((String)this.getFormHM().get("dateitemtype"));
		this.setIntitemtype((String)this.getFormHM().get("intitemtype"));
		this.setCodeitemtype((String)this.getFormHM().get("codeitemtype"));
		this.setBzitemtype((String)this.getFormHM().get("bzitemtype"));
		this.setApp_fashion((String)this.getFormHM().get("app_fashion"));
		this.setBitianxiang((String)this.getFormHM().get("bitianxiang"));
		this.setCodesetList((ArrayList)this.getFormHM().get("codesetList"));
		this.setBusiFieldVo((RecordVo) this.getFormHM().get("busiFieldVo"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setUserlist((ArrayList)this.getFormHM().get("userlist"));
		this.setUnitslist((ArrayList)this.getFormHM().get("unitslist"));
		this.setIndexlist((ArrayList)this.getFormHM().get("indexlist"));
		this.setUserid((String[])this.getFormHM().get("userid"));
		this.setUnitsid((String[])this.getFormHM().get("unitsid"));
		this.setIndexid((String[])this.getFormHM().get("indexid"));
		this.setUsefy((String)this.getFormHM().get("usefy"));
		this.setUsedata((String)this.getFormHM().get("usedata"));
		this.setIndexexplain((String)this.getFormHM().get("indexexplain"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setLeft_fields((String[])this.getFormHM().get("left_fields"));
		this.setRight_fields((String[])this.getFormHM().get("right_fields"));
		this.setFieldsetlist((String[])this.getFormHM().get("fieldsetlist"));
		this.setExportlist((ArrayList)this.getFormHM().get("exportlist"));
		this.setIndexexport((String[])this.getFormHM().get("indexexport"));
		this.setUnitname((String)this.getFormHM().get("unitname"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.setAlllist((ArrayList)this.getFormHM().get("alllist"));
		this.setIndex_list((String[])this.getFormHM().get("index_list"));
		this.setTableid((String)this.getFormHM().get("tableid"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setSetid((String)this.getFormHM().get("setid"));
		this.setInputtypeMList((List) this.getFormHM().get("inputtypeMList"));
		this.setDev_flag((String) this.getFormHM().get("dev_flag"));
	}

	public String getBs_tree() {
		return bs_tree;
	}
	public void setBs_tree(String bs_tree) {
		this.bs_tree = bs_tree;
	}

	public PaginationForm getListForm() {
		return listForm;
	}

	public void setListForm(PaginationForm listForm) {
		this.listForm = listForm;
	}

	public String getInfor() {
		return infor;
	}

	public void setInfor(String infor) {
		this.infor = infor;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}
	
	public ArrayList getSubsetList() {
		return subsetList;
	}
	public void setSubsetList(ArrayList subsetList) {
		this.subsetList = subsetList;
	}
	
	public String getQobj() {
		return qobj;
	}
	public void setQobj(String qobj) {
		this.qobj = qobj;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCustomdesc() {
		return customdesc;
	}

	public void setCustomdesc(String customdesc) {
		this.customdesc = customdesc;
	}
	public String getDeletestr() {
		return deletestr;
	}

	public void setDeletestr(String deletestr) {
		this.deletestr = deletestr;
	}
	public String getUseflag() {
		return useflag;
	}

	public void setUseflag(String useflag) {
		this.useflag = useflag;
	}

	public String getIsrefresh() {
		return isrefresh;
	}
	public void setIsrefresh(String isrefresh) {
		this.isrefresh = isrefresh;
	}
	
	public String getIndexcode() {
		return indexcode;
	}
	public void setIndexcode(String indexcode) {
		this.indexcode = indexcode;
	}
	
	public String getIndexname() {
		return indexname;
	}
	public void setIndexname(String indexname) {
		this.indexname = indexname;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getItemlength() {
		return itemlength;
	}
	public void setItemlength(String itemlength) {
		this.itemlength = itemlength;
	}
	
	public String getDecimalwidth() {
		return decimalwidth;
	}
	public void setDecimalwidth(String decimalwidth) {
		this.decimalwidth = decimalwidth;
	}
	
	public ArrayList getDateList() {
		return dateList;
	}
	public void setDateList(ArrayList dateList) {
		this.dateList = dateList;
	}
	
	public ArrayList getJoincodeList() {
		return joincodeList;
	}
	public void setJoincodeList(ArrayList joincodeList) {
		this.joincodeList = joincodeList;
	}
	
	public String getJoincodename() {
		return joincodename;
	}
	public void setJoincodename(String joincodename) {
		this.joincodename = joincodename;
	}
	public String getItemtype() {
		return itemtype;
	}
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}
	public String getCodelength() {
		return codelength;
	}
	public void setCodelength(String codelength) {
		this.codelength = codelength;
	}
	public String getNumberlength() {
		return numberlength;
	}
	public void setNumberlength(String numberlength) {
		this.numberlength = numberlength;
	}
	public String getDatelength() {
		return datelength;
	}
	public void setDatelength(String datelength) {
		this.datelength = datelength;
	}
	public String getDateitemtype() {
		return dateitemtype;
	}
	public void setDateitemtype(String dateitemtype) {
		this.dateitemtype = dateitemtype;
	}
	public String getIntitemtype() {
		return intitemtype;
	}
	public void setIntitemtype(String intitemtype) {
		this.intitemtype = intitemtype;
	}
	public String getCodeitemtype() {
		return codeitemtype;
	}
	public void setCodeitemtype(String codeitemtype) {
		this.codeitemtype = codeitemtype;
	}
	public String getBzitemtype() {
		return bzitemtype;
	}
	public void setBzitemtype(String bzitemtype) {
		this.bzitemtype = bzitemtype;
	}
	public String getApp_fashion() {
		return app_fashion;
	}
	public void setApp_fashion(String app_fashion) {
		this.app_fashion = app_fashion;
	}
	public String getBitianxiang() {
		return bitianxiang;
	}
	public void setBitianxiang(String bitianxiang) {
		this.bitianxiang = bitianxiang;
	}

	public ArrayList getCodesetList() {
		return codesetList;
	}

	public void setCodesetList(ArrayList codesetList) {
		this.codesetList = codesetList;
	}
	public RecordVo getBusiFieldVo() {
		return busiFieldVo;
	}
	public void setBusiFieldVo(RecordVo busiFieldVo) {
		this.busiFieldVo = busiFieldVo;
	}
	
	public ArrayList getSortlist() {
		return sortlist;
	}
	public void setSortlist(ArrayList sortlist) {
		this.sortlist = sortlist;
	}
	
	public String[] getSort_fields() {
		return sort_fields;
	}
	public void setSort_fields(String[] sort_fields) {
		this.sort_fields = sort_fields;
	}
	
	public ArrayList getUserlist() {
		return userlist;
	}
	public void setUserlist(ArrayList userlist) {
		this.userlist = userlist;
	}
	
	public ArrayList getUnitslist() {
		return unitslist;
	}
	public void setUnitslist(ArrayList unitslist) {
		this.unitslist = unitslist;
	}
	
	public ArrayList getIndexlist() {
		return indexlist;
	}
	public void setIndexlist(ArrayList indexlist) {
		this.indexlist = indexlist;
	}
	
	public String[] getUserid() {
		return userid;
	}
	public void setUserid(String[] userid) {
		this.userid = userid;
	}
	
	public String[] getUnitsid() {
		return unitsid;
	}
	public void setUnitsid(String[] unitsid) {
		this.unitsid = unitsid;
	}
	public String[] getIndexid() {
		return indexid;
	}
	public void setIndexid(String[] indexid) {
		this.indexid = indexid;
	}
	
	public String getUsefy() {
		return usefy;
	}
	public void setUsefy(String usefy) {
		this.usefy = usefy;
	}
	
	public String getUsedata() {
		return usedata;
	}
	public void setUsedata(String usedata) {
		this.usedata = usedata;
	}
	
	public String getIndexexplain() {
		return indexexplain;
	}
	public void setIndexexplain(String indexexplain) {
		this.indexexplain = indexexplain;
	}
	public ArrayList getExportlist() {
		return exportlist;
	}
	public void setExportlist(ArrayList exportlist) {
		this.exportlist = exportlist;
	}
	public String[] getIndexexport() {
		return indexexport;
	}
	public void setIndexexport(String[] indexexport) {
		this.indexexport = indexexport;
	}
	
	@Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        if("/system/dbinit/inforlist".equals(mapping.getPath())&&request.getParameter("b_query")!=null)
        {
            if(this.listForm.getPagination()!=null)
              this.listForm.getPagination().firstPage();//?
        }
        if("/system/dbinit/fieldsetlist".equals(mapping.getPath())&&request.getParameter("b_query")!=null&&!"bank".equals(request.getParameter("b_query")))
        {
            if(this.listForm.getPagination()!=null)
              this.listForm.getPagination().firstPage();//?
        }
        if("/system/dbinit/fielditemlist".equals(mapping.getPath())&&request.getParameter("b_query")!=null&& "link".equals(request.getParameter("b_query")))
        {
            if(this.listForm.getPagination()!=null)
              this.listfieldForm.getPagination().firstPage();//?
        }
        if("/system/dbinit/fieldsetlist".equals(mapping.getPath())&&request.getParameter("b_addsave")!=null)
        {
        	if(this.listForm.getPagination()!=null)
                this.listForm.getPagination().lastPage();//?
        }
        /*if(mapping.getPath().equals("/system/dbinit/fieldsetlist")&&request.getParameter("b_query")!=null){
        	if(this.getPagination()!=null) this.getPagination().firstPage();   
        }*/
        if(request.getParameter("b_query")!=null&& "query".equals(request.getParameter("b_query")))
        	if(this.getListForm()!=null)
        		this.getListForm().getPagination().firstPage();
        if("/system/dbinit/fielditemlist".equals(mapping.getPath())&&request.getParameter("b_query")!=null&& "links".equals(request.getParameter("b_query")))
        {
        	if(this.listForm.getPagination()!=null)
                this.listForm.getPagination().lastPage();//?
        }
        if("/system/dbinit/fielditemlist".equals(mapping.getPath())&&request.getParameter("b_query")!=null){
        	if(this.getPagination()!=null) this.getPagination().firstPage();   
        }
        if(request.getParameter("b_query")!=null&& "query".equals(request.getParameter("b_query")))
        	if(this.getListForm()!=null)
        		this.getListForm().getPagination().firstPage();
        String pajs=request.getSession().getServletContext().getRealPath("/js");
        if("weblogic".equals(SystemConfig.getPropertyValue("webserver")))
        {
			try {
				pajs = request.getSession().getServletContext().getResource("/js").getPath();
				if (pajs.indexOf(':') != -1) {
					pajs = pajs.substring(1);
				} else {
					pajs = pajs.substring(0);
				}
				int nlen = pajs.length();
				StringBuffer buf = new StringBuffer();
				buf.append(pajs);
				buf.setLength(nlen - 1);
				pajs = buf.toString();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
        }
        this.setPath(pajs);
		this.getFormHM().put("lock", request.getSession().getServletContext().getAttribute("lock"));
		return super.validate(mapping, request);
	}

	public String getSettype() {
		return settype;
	}

	public void setSettype(String settype) {
		this.settype = settype;
	}

	public PaginationForm getDbListForm() {
		return dbListForm;
	}

	public void setDbListForm(PaginationForm dbListForm) {
		this.dbListForm = dbListForm;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getFieldsetlist() {
		return fieldsetlist;
	}

	public void setFieldsetlist(String[] fieldsetlist) {
		this.fieldsetlist = fieldsetlist;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	public String getUnitname() {
		return unitname;
	}
	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public ArrayList getAlllist() {
		return alllist;
	}

	public void setAlllist(ArrayList alllist) {
		this.alllist = alllist;
	}
	public String[] getIndex_list() {
		return index_list;
	}

	public void setIndex_list(String[] index_list) {
		this.index_list = index_list;
	}
	
	public String getTableid() {
		return tableid;
	}
	public void setTableid(String tableid) {
		this.tableid = tableid;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public PaginationForm getListfieldForm() {
		return listfieldForm;
	}

	public void setListfieldForm(PaginationForm listfieldForm) {
		this.listfieldForm = listfieldForm;
	}

	public String getMultimedia_file_flag() {
		return multimedia_file_flag;
	}

	public void setMultimedia_file_flag(String multimedia_file_flag) {
		this.multimedia_file_flag = multimedia_file_flag;
	}
	
	public String getInputtype() {
		return inputtype;
	}
	public void setInputtype(String inputtype) {
		this.inputtype = inputtype;
	}

	public List getInputtypeMList() {
		return inputtypeMList;
	}
	public void setInputtypeMList(List inputtypeMList) {
		this.inputtypeMList = inputtypeMList;
	}
	public String getSetexplain() {
		return setexplain;
	}

	public void setSetexplain(String setexplain) {
		this.setexplain = setexplain;
	}
	
}