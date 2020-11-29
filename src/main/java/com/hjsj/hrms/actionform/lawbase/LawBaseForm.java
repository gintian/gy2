package com.hjsj.hrms.actionform.lawbase;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Title:LawBaseForm Description:规章制度表单 Company:hjsj create time:Jun 1,
 * 2005:10:50:21 AM
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class LawBaseForm extends FrameForm {

	

	// 待调整的规章制度列表
	private String lawbase[];

	private String basetype;

	private String digest;
	
	private String digest_desc;

	private String file_id;

	private FormFile content;
	
	private String fileName;
	/**
	 * 日期属性
	 */
	private DateStyle first_date = new DateStyle();

	private DateStyle second_date = new DateStyle();

	private DateStyle third_date = new DateStyle();

	/**
	 * flg 是否有子结点标识
	 */
	private String flg;

	/**
	 * 增加修改标识
	 */
	String flag = "0";

	/**
	 * 目录名称
	 */
	String baseStructName = "";

	/**
	 * 目录号
	 */
	String base_id = "";

	private FormFile file;
	
	private FormFile manuscript;

	private String message = "";
	private String law_ext_save;
    private String a_id;
	/**
	 * 库对象
	 */

	private RecordVo law_base_vo = new RecordVo("law_base_struct");

	private RecordVo lawFileVo = new RecordVo("law_base_file");

	private PaginationForm lawbaseForm = new PaginationForm();
	
	private PaginationForm paginationForm = new PaginationForm();
	private PaginationForm roleListForm=new PaginationForm();
    private String status;//有效性
    private String sp_result;
    private ArrayList contentlist = new ArrayList();
    private String note_num;
    private String note_nums;
    private String contenttype;
    private String issuedate;
    private String enddate;
    private String username;
    private String sign;
    private ArrayList selectrname = new ArrayList();
	private String[] right_fields;
	private ArrayList personlist = new ArrayList();
	private ArrayList personname = new ArrayList();
	private String selectname;
	private String order_name;
	private String order_type;
	private String b0110;
	private String tarTreeCode;
	private String transfercodeitemid;
	private String keywords;
	private String keyword;
	private String priv;
	private String checkflag;
	private String dbpre_str;
	private ArrayList lawBaseFileList = new ArrayList(); 
	/**
	 * 查询条件
	 */
	private String cond_str = " where 1=1";

	/**
	 * checkbox状态
	 * 
	 */
	private String check = "";
	
	private String viewhide="";
	private String viewhide1;
	private String viewhide4;
	private String viewhide5;
	
	
	private String year;
	private String dirname;
	private String orgid;
	private ArrayList yearlist=new ArrayList();
	private String url;
	private String tmppath;
	private String orgname;
	/*
	 * 文档指标设置
	 */
	private String left_fields1[];
	private String right_fields1[];
	
	private String usable_fields;
	private String usable_value;
	private String table_fields;
	private String table_value;
	private String selectFlag;
	
	private ArrayList fieldlist = new ArrayList();
	private String colums;
	private String field_str_item;
	private String file_str_item;
	
	private HashMap itemMap;
	private ArrayList itemList;
	
	private ArrayList useableFileItem;//文档浏览/特征检索/文档可选指标
	private ArrayList table_field;//文档浏览/特征检索/文档列表指标
	
	private String closeFlag = ""; //知识库，分类授权页面关闭标识
	
	private String returnvalue="1"; //返回页面标识，如返回导航图dxt
	
	private String encryptParam=""; //链接中部分加密后的参数
	
	public String getCloseFlag(){
		return closeFlag;
	}
	
	public void setCloseFlag(String closeFlag){
		this.closeFlag = closeFlag;
	}
	
	
	public String getFile_str_item() {
		return file_str_item;
	}

	public void setFile_str_item(String fileStrItem) {
		file_str_item = fileStrItem;
	}

	public ArrayList getTable_field() {
		return table_field;
	}

	public void setTable_field(ArrayList tableField) {
		table_field = tableField;
	}

	public ArrayList getUseableFileItem() {
		return useableFileItem;
	}

	public void setUseableFileItem(ArrayList useableFileItem) {
		this.useableFileItem = useableFileItem;
	}

	public ArrayList getItemList() {
		return itemList;
	}

	public void setItemList(ArrayList itemList) {
		this.itemList = itemList;
	}

	public HashMap getItemMap() {
		return itemMap;
	}

	public void setItemMap(HashMap itemMap) {
		this.itemMap = itemMap;
	}

	public String getSelectFlag() {
		return selectFlag;
	}

	public void setSelectFlag(String selectFlag) {
		this.selectFlag = selectFlag;
	}

	public String getUsable_fields()
    {
        return usable_fields;
    }

    public void setUsable_fields(String usable_fields)
    {
        this.usable_fields = usable_fields;
    }

    public String getUsable_value()
    {
        return usable_value;
    }

    public void setUsable_value(String usable_value)
    {
        this.usable_value = usable_value;
    }

    public String getTable_fields()
    {
        return table_fields;
    }

    public void setTable_fields(String table_fields)
    {
        this.table_fields = table_fields;
    }

    public String getTable_value()
    {
        return table_value;
    }

    public void setTable_value(String table_value)
    {
        this.table_value = table_value;
    }

    public String[] getLeft_fields1()
    {
        return left_fields1;
    }

    public void setLeft_fields1(String[] left_fields1)
    {
        this.left_fields1 = left_fields1;
    }

    public String[] getRight_fields1()
    {
        return right_fields1;
    }

    public void setRight_fields1(String[] right_fields1)
    {
        this.right_fields1 = right_fields1;
    }

    public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public PaginationForm getLawbaseForm() {
		return lawbaseForm;
	}

	public void setLawbaseForm(PaginationForm lawbaseForm) {
		this.lawbaseForm = lawbaseForm;
	}

	public DateStyle getFirst_date() {
		return this.first_date;
	}

	public void setFirst_date(DateStyle first_date) {
		this.first_date = first_date;
	}

	public DateStyle getSecond_date() {
		return this.second_date;
	}

	public void setSecond_date(DateStyle second_date) {
		this.second_date = second_date;
	}

	public DateStyle getThird_date() {
		return this.third_date;
	}

	public void setThird_date(DateStyle third_date) {
		this.third_date = third_date;
	}

	public void setLawFileVo(RecordVo lawFileVo) {
		this.lawFileVo = lawFileVo;
	}

	public RecordVo getLawFileVo() {
		return this.lawFileVo;
	}

	/*
	 * 上传文件
	 */
	public FormFile getFile() {
		return this.file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	private String fname;

	public String getFname() {
		return this.fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	private String size;

	public String getSize() {
		return this.size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	/*
	 * 目录名称属性
	 */
	public void setBaseStructName(String baseStructName) {
		this.baseStructName = baseStructName;
	}

	public String getBaseStructName() {
		return this.baseStructName;
	}

	public void setBase_id(String base_id) {
		this.base_id = base_id;
	}

	public String getBase_id() {
		return this.base_id;
	}

	/**
	 * 增删改标识
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getFlag() {
		return this.flag;
	}

	public LawBaseForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return 返回 message。
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 清提示
	 * 
	 * @param message
	 */
	public void clearMessage() {
		this.getFormHM().put("message2", "");
	}

	/**
	 * @param message
	 *            要设置的 message。
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
    public void outPutFormHM() {
		this.setDbpre_str((String)this.getFormHM().get("dbpre_str"));
		this.getLawbaseForm().setList(
				(ArrayList) this.getFormHM().get("filelist"));
		this.getPaginationForm().setList(
				(ArrayList) this.getFormHM().get("affixList"));
		this.setLaw_base_vo((RecordVo) this.getFormHM().get("law_base_vo"));
		this.setCond_str((String) this.getFormHM().get("cond_str"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setBase_id((String) this.getFormHM().get("base_id"));
		this.setBaseStructName(this.getFormHM().get("baseStructName")
				.toString());
		this.setLawFileVo((RecordVo) this.getFormHM().get("lawFileTb"));
		this.setFirst_date((DateStyle) this.getFormHM().get("first_date"));
		this.setSecond_date((DateStyle) this.getFormHM().get("second_date"));
		this.setThird_date((DateStyle) this.getFormHM().get("third_date"));
		this.getFormHM().put("check", this.getCheck());
		this.setMessage(this.getFormHM().get("message2").toString());
		this.setLawbase((String[]) this.getFormHM().get("lawbase"));
		this.setFlg((String) getFormHM().get("flg"));
		//this.lawbaseForm.getPagination().gotoPage(1);
		this.digest = this.lawFileVo.getString("digest");
		this.setStatus((String)this.getFormHM().get("status"));
		this.setDigest((String)this.getFormHM().get("digest"));
		
		this.setLaw_ext_save((String)this.getFormHM().get("law_ext_save"));
		this.getRoleListForm().setList((ArrayList)this.getFormHM().get("rolelist"));
		this.setSp_result((String)this.getFormHM().get("sp_result"));
		this.setA_id((String)this.getFormHM().get("a_id"));
		this.setBasetype((String)this.getFormHM().get("basetype"));
		this.setContentlist((ArrayList)this.getFormHM().get("contentlist"));
		this.setSign((String)this.getFormHM().get("sign"));
		this.setSelectrname((ArrayList)this.getFormHM().get("selectrname"));
		this.setRight_fields((String[])this.getFormHM().get("right_fields"));
		this.setNote_nums((String)this.getFormHM().get("note_nums"));
		this.setContenttype((String)this.getFormHM().get("contenttype"));
		this.setIssuedate((String)this.getFormHM().get("issuedate"));
		this.setUsername((String)this.getFormHM().get("username"));
		this.setEnddate((String)this.getFormHM().get("enddate"));
		this.setPersonlist((ArrayList)this.getFormHM().get("personlist"));
		this.setPersonname((ArrayList)this.getFormHM().get("personname"));
		this.setSelectname((String)this.getFormHM().get("selectname"));
		this.setOrder_name((String)this.getFormHM().get("order_name"));
		this.setOrder_type((String)this.getFormHM().get("order_type"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setTarTreeCode((String)this.getFormHM().get("tarTreeCode"));
		this.setTransfercodeitemid((String)this.getFormHM().get("transfercodeitemid"));
		this.setKeywords((String)this.getFormHM().get("keywords"));
		this.setKeyword((String)this.getFormHM().get("keyword"));
		this.setPriv((String)this.getFormHM().get("priv"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
		this.setViewhide((String)this.getFormHM().get("viewhide"));
		this.setViewhide1((String)this.getFormHM().get("viewhide1"));
		this.setViewhide4((String)this.getFormHM().get("viewhide4"));
		this.setViewhide5((String)this.getFormHM().get("viewhide5"));
		this.setYearlist((ArrayList)this.getFormHM().get("yearlist"));
		this.setUrl((String)this.getFormHM().get("url"));
		this.setOrgname((String)this.getFormHM().get("orgname"));
		this.setUsable_value((String)this.getFormHM().get("usable_value"));
		this.setTable_value((String)this.getFormHM().get("table_value"));
		this.setUsable_fields((String)this.getFormHM().get("usable_fields"));
		this.setTable_fields((String)this.getFormHM().get("table_fields"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setColums((String)this.getFormHM().get("colums"));
		this.setField_str_item((String)this.getFormHM().get("field_str_item"));
		this.setFile_str_item((String)this.getFormHM().get("file_str_item"));
		this.setLawBaseFileList((ArrayList)this.getFormHM().get("lawBaseFileList"));
		this.setSelectFlag((String)this.getFormHM().get("selectFlag"));
		this.setItemMap((HashMap)this.getFormHM().get("itemMap"));
		this.setItemList((ArrayList)this.getFormHM().get("itemList"));
		this.setUseableFileItem((ArrayList)this.getFormHM().get("useableFileItem"));
		this.setTable_field((ArrayList)this.getFormHM().get("table_field"));
		this.setCloseFlag((String)this.getFormHM().get("closeFlag"));
		this.setDigest_desc((String)this.getFormHM().get("digest_desc"));
		this.setEncryptParam((String)this.getFormHM().get("encryptParam"));
	}


	@Override
    public void inPutTransHM() {
		this.getFormHM().put("dbpre_str", this.getDbpre_str());
		this.getFormHM().put("checkflag", this.getCheckflag());
		this.getFormHM().put("a_id",this.getA_id());
		this.getFormHM().put("fileName", this.getFileName());
		this.getFormHM().put("content", this.getContent());
		this.getFormHM().put("selectList", this.getPaginationForm().getSelectedList());
		this.getFormHM().put("digest", digest);
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getLawbaseForm().getSelectedList());
		this.getFormHM().put("selectedrolelist",this.getRoleListForm().getSelectedList());
		this.getFormHM().put("law_base_vo", this.getLaw_base_vo());
		this.getFormHM().put("lawFileTb", this.getLawFileVo());
		this.getFormHM().put("lawFileVo", this.getLawFileVo());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("base_id", this.getBase_id());
		this.getFormHM().put("baseStructName", this.getBaseStructName());
		this.getFormHM().put("cond_str", this.getCond_str());
		this.getFormHM().put("first_date", this.getFirst_date());
		this.getFormHM().put("second_date", this.getSecond_date());
		this.getFormHM().put("third_date", this.getThird_date());
		this.getFormHM().put("check", this.getCheck());
		this.getFormHM().put("message2", this.getMessage());
		/** 上传文件 */
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("manuscript",this.getManuscript());
		this.getFormHM().put("lawbase", this.getLawbase());
		String temp = getLawFileVo().getString("name");
		this.getFormHM().put("name", temp);
		this.getFormHM().put("basetype", basetype);
		this.getFormHM().put("file_id", this.getFile_id());
		this.getFormHM().put("status",this.getStatus());
		this.getFormHM().put("sp_result",this.getSp_result());
		this.getFormHM().put("note_num",this.getNote_num());
		this.getFormHM().put("note_nums",this.getNote_nums());
		this.getFormHM().put("contenttype",this.getContenttype());
		this.getFormHM().put("issuedate",this.getIssuedate());
		this.getFormHM().put("username",this.getUsername());
		this.getFormHM().put("right_fields",this.getRight_fields());
		this.getFormHM().put("enddate",this.getEnddate());
		this.getFormHM().put("selectname",this.getSelectname());
		this.getFormHM().put("order_name",this.getOrder_name());
		this.getFormHM().put("order_type",this.getOrder_type());
		this.getFormHM().put("b0110",this.getB0110());
		this.getFormHM().put("transfercodeitemid",this.getTransfercodeitemid());
		this.getFormHM().put("keywords",this.getKeywords());
		this.getFormHM().put("keyword",this.getKeyword());
		// System.out.println(check);
		this.getFormHM().put("viewhide1", viewhide1);
		this.getFormHM().put("viewhide4", viewhide4);
		this.getFormHM().put("viewhide5", viewhide5);
		
		this.getFormHM().put("dirname", dirname);
		this.getFormHM().put("orgid", orgid);
		this.getFormHM().put("year", year);
		this.getFormHM().put("usable_value", this.getUsable_value());
		this.getFormHM().put("table_value", this.getTable_value());
		this.getFormHM().put("lawBaseFileList", this.getLawBaseFileList());
		this.getFormHM().put("field_str_item", this.getField_str_item());
		this.getFormHM().put("selectFlag", this.getSelectFlag());
		this.getFormHM().put("itemMap", this.getItemMap());
		this.getFormHM().put("itemList", this.getItemList());
		this.getFormHM().put("useableFileItem", this.getUseableFileItem());
		this.getFormHM().put("table_field", this.getTable_field());
		this.getFormHM().put("file_str_item", this.getFile_str_item());
		this.getFormHM().put("closeFlag", this.getCloseFlag());
		this.getFormHM().put("digest_desc", this.getDigest_desc());
		this.getFormHM().put("encryptParam", this.getEncryptParam());
	}

	public RecordVo getLaw_base_vo() {
		return law_base_vo;
	}

	public void setLaw_base_vo(RecordVo law_base_vo) {
		this.law_base_vo = law_base_vo;
	}

	public String getCond_str() {
		return cond_str;
	}

	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {

		/*
		 * 右面文件列表操作
		 */
		if ("/selfservice/lawbase/law_maintenance".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.lawbaseForm.getPagination().gotoPage(1);
			/*if (this.getPagination() != null) {
				this.getPagination().firstPage();//
			}*/
		}

		if ("/selfservice/lawtext/law_maintenance".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.lawbaseForm.getPagination().gotoPage(1);
			/*if (this.getPagination() != null) {
				this.getPagination().firstPage();//
			}*/
		}
		if ("/selfservice/lawbase/lawtext/law_maintenance".equals(arg0.getPath())
				&& arg1.getParameter("b_select") != null) {
			this.lawbaseForm.getPagination().gotoPage(1);
		}
		if ("/selfservice/lawbase/lawtext/law_maintenance".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null&&arg1.getParameter("isback")==null) {
			this.lawbaseForm.getPagination().gotoPage(1);
			this.setKeyword("");
			this.setNote_nums("");
			this.setContenttype("");
			this.setIssuedate("");
			this.setEnddate("");
			this.setUsername("");
		}

		/*
		 * 目录页面加载页面
		 */
		if ("/selfservice/lawbase/add_law_base".equals(arg0.getPath())
				&& arg1.getParameter("b_add") != null) {
			this.setFlag("3");			
			this.getFormHM().put("flag", this.getFlag());
			this.getLaw_base_vo().clearValues();
		}
		/*
		 * 目录修改页面加载
		 */
		if ("/selfservice/lawbase/add_law_base".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.setFlag("4");
			this.getFormHM().put("flag", this.getFlag());

		}
		/*
		 * 右面文件添加操作
		 */

		if ("/selfservice/lawbase/law_maintenance".equals(arg0.getPath())
				&& arg1.getParameter("b_add") != null) {
			this.setFlag("1");
			this.getFormHM().put("flag", this.getFlag());
			this.setFirst_date(new DateStyle());
			this.setSecond_date(new DateStyle());
			this.setThird_date(new DateStyle());
			this.setDigest("");
			this.getLawFileVo().clearValues();
			this.setFile(null);
			this.setManuscript(null);
		}
		/**
		 * 右面文件编辑
		 */

		else if ("/selfservice/lawbase/law_into_base".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {

			this.setFlag("0");
			this.digest = "";
			this.getFormHM().put("flag", this.getFlag());
		} else if ("/selfservice/lawbase/law_into_base".equals(arg0.getPath())
				&& arg1.getParameter("b_view") != null) {
			this.setFlag("2");
			this.getFormHM().put("flag", this.getFlag());
		} else if ("selfservice/infomanager/board/searchboard".equals(
                arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.getFormHM().put("flag", "100");
		} else if ("/selfservice/lawbase/law_into_base".equals(arg0.getPath())
				&& arg1.getParameter("b_save") != null) {

		}

		/*
		 * 左面目录树操作
		 */

		if ("/selfservice/lawbase/add_law_base".equals(arg0.getPath())
				&& arg1.getParameter("b_add") != null) {
			this.getLaw_base_vo().clearValues();
		}
		if ("/selfservice/lawbase/add_law_base".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
		}
		
		//分类的角色授权和清除 
		if ("/selfservice/lawbase/add_law_base_role".equals(arg0.getPath())
				&& arg1.getParameter("b_add") != null) {
			this.roleListForm.getPagination().gotoPage(1);
		}
		if ("/selfservice/lawbase/update_law_base_role".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null&&arg1.getParameter("b_update") == null) {
			this.roleListForm.getPagination().gotoPage(1);
		}
		
		//文件的角色授权和清除  
		if ("/selfservice/lawbase/lawtext/law_onetext_role".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.roleListForm.getPagination().gotoPage(1);
		}
		if ("/selfservice/lawbase/update_law_onetext_role".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null&&arg1.getParameter("b_update") == null) {
			this.roleListForm.getPagination().gotoPage(1);
		}
		
		if ("/bi/document/display".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			 try {	
			tmppath = arg1.getSession().getServletContext().getRealPath(File.separator+"bjgaofa");
		   if("weblogic".equals(SystemConfig.getPropertyValue("webserver")))
		   {
			  
				tmppath=arg1.getSession().getServletContext().getResource(File.separator+"bjgaofa").getPath();
			//.substring(0);
		      if(tmppath.indexOf(':')!=-1)
		  	  {
		    	  tmppath=tmppath.substring(1);   
		   	  }
		  	  else
		   	  {
		  		tmppath=tmppath.substring(0);      
		   	  }
		      int nlen=tmppath.length();
		  	  StringBuffer buf=new StringBuffer();
		   	  buf.append(tmppath);
		  	  buf.setLength(nlen-1);
		  	tmppath=buf.toString();
		   }
			 } catch (MalformedURLException e) {
					e.printStackTrace();
				}finally{
					this.getFormHM().put("tmppath", tmppath=SafeCode.encode(tmppath));
				}
		}
		return super.validate(arg0, arg1);
	}

	
	public String[] getLawbase() {
		return lawbase;
	}

	public void setLawbase(String[] lawbase) {
		this.lawbase = lawbase;
	}

	public String getFlg() {
		return flg;
	}

	public void setFlg(String flg) {
		this.flg = flg;
	}

	public String getBasetype() {
		return basetype;
	}

	public void setBasetype(String basetype) {
		this.basetype = basetype;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public PaginationForm getPaginationForm() {
		return paginationForm;
	}

	public void setPaginationForm(PaginationForm paginationForm) {
		this.paginationForm = paginationForm;
	}



	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public FormFile getContent() {
		return content;
	}

	public void setContent(FormFile content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLaw_ext_save() {
		return law_ext_save;
	}

	public void setLaw_ext_save(String law_ext_save) {
		this.law_ext_save = law_ext_save;
	}

	public PaginationForm getRoleListForm() {
		return roleListForm;
	}

	public void setRoleListForm(PaginationForm roleListForm) {
		this.roleListForm = roleListForm;
	}

	public String getSp_result() {
		return sp_result;
	}

	public void setSp_result(String sp_result) {
		this.sp_result = sp_result;
	}

	public String getA_id() {
		return a_id;
	}

	public void setA_id(String a_id) {
		this.a_id = a_id;
	}

	public ArrayList getContentlist() {
		return contentlist;
	}

	public void setContentlist(ArrayList contentlist) {
		this.contentlist = contentlist;
	}

	public String getContenttype() {
		return contenttype;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	public String getIssuedate() {
		return issuedate;
	}

	public void setIssuedate(String issuedate) {
		this.issuedate = issuedate;
	}

	public String getNote_num() {
		return note_num;
	}

	public void setNote_num(String note_num) {
		this.note_num = note_num;
	}
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public FormFile getManuscript() {
		return manuscript;
	}

	public void setManuscript(FormFile manuscript) {
		this.manuscript = manuscript;
	}

	public ArrayList getSelectrname() {
		return selectrname;
	}

	public void setSelectrname(ArrayList selectrname) {
		this.selectrname = selectrname;
	}


	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNote_nums() {
		return note_nums;
	}

	public void setNote_nums(String note_nums) {
		this.note_nums = note_nums;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public ArrayList getPersonlist() {
		return personlist;
	}

	public void setPersonlist(ArrayList personlist) {
		this.personlist = personlist;
	}

	public ArrayList getPersonname() {
		return personname;
	}

	public void setPersonname(ArrayList personname) {
		this.personname = personname;
	}

	public String getSelectname() {
		return selectname;
	}

	public void setSelectname(String selectname) {
		this.selectname = selectname;
	}

	public String getOrder_name() {
		return order_name;
	}

	public void setOrder_name(String order_name) {
		this.order_name = order_name;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getTarTreeCode() {
		return tarTreeCode;
	}

	public void setTarTreeCode(String tarTreeCode) {
		this.tarTreeCode = tarTreeCode;
	}

	public String getTransfercodeitemid() {
		return transfercodeitemid;
	}

	public void setTransfercodeitemid(String transfercodeitemid) {
		this.transfercodeitemid = transfercodeitemid;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getDbpre_str() {
		return dbpre_str;
	}

	public void setDbpre_str(String dbpre_str) {
		this.dbpre_str = dbpre_str;
	}

	public String getViewhide() {
		return viewhide;
	}

	public void setViewhide(String viewhide) {
		this.viewhide = viewhide;
	}

	public String getViewhide1() {
		return viewhide1;
	}

	public void setViewhide1(String viewhide1) {
		this.viewhide1 = viewhide1;
	}

	public String getViewhide4() {
		return viewhide4;
	}

	public void setViewhide4(String viewhide4) {
		this.viewhide4 = viewhide4;
	}

	public String getViewhide5() {
		return viewhide5;
	}

	public void setViewhide5(String viewhide5) {
		this.viewhide5 = viewhide5;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getDirname() {
		return dirname;
	}

	public void setDirname(String dirname) {
		this.dirname = dirname;
	}

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public ArrayList getYearlist() {
		return yearlist;
	}

	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTmppath() {
		return tmppath;
	}

	public void setTmppath(String tmppath) {
		this.tmppath = tmppath;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getColums() {
		return colums;
	}

	public void setColums(String colums) {
		this.colums = colums;
	}

	public String getField_str_item() {
		return field_str_item;
	}

	public void setField_str_item(String field_str_item) {
		this.field_str_item = field_str_item;
	}

	public ArrayList getLawBaseFileList() {
		return lawBaseFileList;
	}

	public void setLawBaseFileList(ArrayList lawBaseFileList) {
		this.lawBaseFileList = lawBaseFileList;
	}

	public String getDigest_desc() {
		return digest_desc;
	}

	public void setDigest_desc(String digestDesc) {
		digest_desc = digestDesc;
	}

    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }

    public String getReturnvalue() {
        return returnvalue;
    }

    public String getEncryptParam() {
        return encryptParam;
    }

    public void setEncryptParam(String encryptParam) {
        this.encryptParam = encryptParam;
    }
	

}