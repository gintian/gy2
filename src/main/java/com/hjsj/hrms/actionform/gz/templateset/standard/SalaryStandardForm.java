package com.hjsj.hrms.actionform.gz.templateset.standard;

import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class SalaryStandardForm extends FrameForm {
	private String opt="new";  // new   edit
	private String standardID="";
	
	private PaginationForm standardlistform=new PaginationForm();
	private String pkg_id="";
	private String pkgIsActive="0";  // 0:没启用  1:启用
	
	private String hfactor="";
	private String s_hfactor="";
	private String vfactor="";
	private String s_vfactor="";
	private String item="";
	private String hcontent="";
	private String vcontent="";
	
	private String hfactor_name="";
	private String s_hfactor_name="";
	private String vfactor_name="";
	private String s_vfactor_name="";
	
	private GzStandardItemVo gzStandardItemVo=null;
	private ArrayList gzItemList=new ArrayList();
	private String gzStandardItemHtml="";
	private String gzStandardName="";
	
	private String typeSpec="0"; //用来判断是新增的还是修改等1：是新增的时候其余是其他的
	private String   optType="0";   // 0: 增减横栏目  1：增减子横栏目   2: 增减纵栏目  3：增减子纵栏目
	private String[] columnsItemValue=null;
    private ArrayList selectItemList=new ArrayList();
	private	ArrayList parentItemList=new ArrayList();
	private String parentItemId="";
	private String type="";   // 0:横向栏目 1:横向子栏目  2:纵向栏目  3:纵向子栏目  4:结果指标   5:代码树 6：指标熟
	private String flag="";   // 0:库 1：指标集 2：指标  3：代码  4：UN  5:UM  6@K  7:数字  8: 日期
	private String id="";
	private String desc="";
	private String title="";
	private String isOperOrManage;//选择归属单位时，按炒作单位还是管理范围展现机构树，=0按管理=1按操作，
	private String filename;
	@Override
    public void outPutFormHM() {
		this.setIsOperOrManage((String)this.getFormHM().get("isOperOrManage"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
		this.setTitle((String)this.getFormHM().get("title"));
		this.setOpt((String)this.getFormHM().get("opt"));
		this.setStandardID((String)this.getFormHM().get("standardID"));
		this.setPkg_id((String)this.getFormHM().get("pkg_id"));
		this.setPkgIsActive((String)this.getFormHM().get("pkgIsActive"));
		this.setGzStandardName((String)this.getFormHM().get("gzStandardName"));
		
		this.getStandardlistform().setList((ArrayList)this.getFormHM().get("standardlist"));
		this.setHfactor_name((String)this.getFormHM().get("hfactor_name"));
		this.setS_hfactor_name((String)this.getFormHM().get("s_hfactor_name"));
		this.setVfactor_name((String)this.getFormHM().get("vfactor_name"));
		this.setS_vfactor_name((String)this.getFormHM().get("s_vfactor_name"));
		this.setHfactor((String)this.getFormHM().get("hfactor"));
		this.setS_hfactor((String)this.getFormHM().get("s_hfactor"));
		this.setVfactor((String)this.getFormHM().get("vfactor"));
		this.setS_vfactor((String)this.getFormHM().get("s_vfactor"));
		this.setItem((String)this.getFormHM().get("item"));
		this.setHcontent((String)this.getFormHM().get("hcontent"));
		this.setVcontent((String)this.getFormHM().get("vcontent"));
		
		this.setGzStandardItemVo((GzStandardItemVo)this.getFormHM().get("gzStandardItemVo"));
		this.setGzStandardItemHtml((String)this.getFormHM().get("gzStandardItemHtml"));
		this.setGzItemList((ArrayList)this.getFormHM().get("gzItemList"));
		
		this.setOptType((String)this.getFormHM().get("optType"));
		this.setSelectItemList((ArrayList)this.getFormHM().get("selectItemList"));
		this.setParentItemList((ArrayList)this.getFormHM().get("parentItemList"));
		this.setParentItemId((String)this.getFormHM().get("parentItemId"));
		this.setType((String)this.getFormHM().get("type"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setId((String)this.getFormHM().get("id"));
		this.setDesc((String)this.getFormHM().get("desc"));
		
		this.setFilename((String) this.getFormHM().get("filename"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("isOperOrManage",this.getIsOperOrManage());
		this.getFormHM().put("selectedList",
				this.getStandardlistform().getSelectedList());	
		
		this.getFormHM().put("gzStandardName",this.getGzStandardName());
		this.getFormHM().put("hfactor",this.getHfactor());
		this.getFormHM().put("s_hfactor",this.getS_hfactor());
		this.getFormHM().put("vfactor",this.getVfactor());
		this.getFormHM().put("s_vfactor",this.getS_vfactor());
		this.getFormHM().put("item",this.getItem());
		this.getFormHM().put("hcontent",this.getHcontent());
		this.getFormHM().put("vcontent",this.getVcontent());
		
		this.getFormHM().put("columnsItemValue",this.getColumnsItemValue());
		this.getFormHM().put("gzItemList",this.getGzItemList());
		
		this.getFormHM().put("filename", this.getFilename());
		
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if(arg1.getParameter("b_query")!=null&& "query".equals(arg1.getParameter("b_query")))
		{
			if(this.getStandardlistform()!=null)
				this.getStandardlistform().getPagination().firstPage();
		}
		return super.validate(arg0, arg1);
	}
	
	public PaginationForm getStandardlistform() {
		return standardlistform;
	}

	public void setStandardlistform(PaginationForm standardlistform) {
		this.standardlistform = standardlistform;
	}

	public String getHcontent() {
		return hcontent;
	}

	public void setHcontent(String hcontent) {
		this.hcontent = hcontent;
	}

	public String getHfactor() {
		return hfactor;
	}

	public void setHfactor(String hfactor) {
		this.hfactor = hfactor;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getS_hfactor() {
		return s_hfactor;
	}

	public void setS_hfactor(String s_hfactor) {
		this.s_hfactor = s_hfactor;
	}

	public String getS_vfactor() {
		return s_vfactor;
	}

	public void setS_vfactor(String s_vfactor) {
		this.s_vfactor = s_vfactor;
	}

	public String getVcontent() {
		return vcontent;
	}

	public void setVcontent(String vcontent) {
		this.vcontent = vcontent;
	}

	public String getVfactor() {
		return vfactor;
	}

	public void setVfactor(String vfactor) {
		this.vfactor = vfactor;
	}

	public String getHfactor_name() {
		return hfactor_name;
	}

	public void setHfactor_name(String hfactor_name) {
		this.hfactor_name = hfactor_name;
	}

	public String getS_hfactor_name() {
		return s_hfactor_name;
	}

	public void setS_hfactor_name(String s_hfactor_name) {
		this.s_hfactor_name = s_hfactor_name;
	}

	public String getS_vfactor_name() {
		return s_vfactor_name;
	}

	public void setS_vfactor_name(String s_vfactor_name) {
		this.s_vfactor_name = s_vfactor_name;
	}

	public String getVfactor_name() {
		return vfactor_name;
	}

	public void setVfactor_name(String vfactor_name) {
		this.vfactor_name = vfactor_name;
	}

	public GzStandardItemVo getGzStandardItemVo() {
		return gzStandardItemVo;
	}

	public void setGzStandardItemVo(GzStandardItemVo gzStandardItemVo) {
		this.gzStandardItemVo = gzStandardItemVo;
	}

	public String getGzStandardItemHtml() {
		return gzStandardItemHtml;
	}

	public void setGzStandardItemHtml(String gzStandardItemHtml) {
		this.gzStandardItemHtml = gzStandardItemHtml;
	}

	public ArrayList getGzItemList() {
		return gzItemList;
	}

	public void setGzItemList(ArrayList gzItemList) {
		this.gzItemList = gzItemList;
	}

	public String getGzStandardName() {
		return gzStandardName;
	}

	public void setGzStandardName(String gzStandardName) {
		this.gzStandardName = gzStandardName;
	}

	public String[] getColumnsItemValue() {
		return columnsItemValue;
	}

	public void setColumnsItemValue(String[] columnsItemValue) {
		this.columnsItemValue = columnsItemValue;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getParentItemId() {
		return parentItemId;
	}

	public void setParentItemId(String parentItemId) {
		this.parentItemId = parentItemId;
	}

	public ArrayList getParentItemList() {
		return parentItemList;
	}

	public void setParentItemList(ArrayList parentItemList) {
		this.parentItemList = parentItemList;
	}

	public ArrayList getSelectItemList() {
		return selectItemList;
	}

	public void setSelectItemList(ArrayList selectItemList) {
		this.selectItemList = selectItemList;
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getPkg_id() {
		return pkg_id;
	}

	public void setPkg_id(String pkg_id) {
		this.pkg_id = pkg_id;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public String getStandardID() {
		return standardID;
	}

	public void setStandardID(String standardID) {
		this.standardID = standardID;
	}

	public String getPkgIsActive() {
		return pkgIsActive;
	}

	public void setPkgIsActive(String pkgIsActive) {
		this.pkgIsActive = pkgIsActive;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIsOperOrManage() {
		return isOperOrManage;
	}

	public void setIsOperOrManage(String isOperOrManage) {
		this.isOperOrManage = isOperOrManage;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTypeSpec() {
		return typeSpec;
	}

	public void setTypeSpec(String typeSpec) {
		this.typeSpec = typeSpec;
	}

}
