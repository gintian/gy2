package com.hjsj.hrms.actionform.dtgh.party;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * 
 * @author xujian
 *Jan 14, 2010
 */
public class PartyBusinessForm extends FrameForm {
	
	private String codesetid;
	private String codesetdesc;
	private String codemess;//当前组织单元名称
	private String fieldstr;//显示列表指标
	private String partylike;// 显示当前组织单元下所有组织单元 1是
	private String querylike;//模糊查询 1是
	private String query;//是否是点击查询按钮进行显示
	private ArrayList selectfieldlist = new ArrayList();//查询条件指标集
	private String sqlstr;
	private String wherestr;
	private String columnstr;
	private String orderby;
	private String isShowCondition;//是否显示查询
	private ArrayList fieldList = new ArrayList();//显示列表指标
	private String backdate;
	private ArrayList fieldsetlist = new ArrayList();//指标集
	private String param;
	private ArrayList infofieldlist = new ArrayList();//主集指标集
	private String a_code;
	private String fieldsetid;
	private String first;
	private String codeitemid;
	private String codeitemdesc;
	private String isrefresh;
	private ArrayList codeitemlist = new ArrayList();//主集被选择项
	private ArrayList codeitemidlist = new ArrayList();
	private String type;//区分主集新增还是修改
	private String end_date;
	private ArrayList list = new ArrayList();//子集数据集vo
	private PaginationForm partyBusinessForm = new PaginationForm(); 
	private String subtype;//区分子集新增还是修改
	private String i9999;
	private ArrayList selectedlist = new ArrayList();
	private int current=1;
	private String ps_c_job;
	private ArrayList medialist;
	private String filename;
	private FormFile picturefile;
	private String ps_c_card_attach;
	private String zp_job_template;
	private String return_code;
	private String returnvalue = "1";
	
	private String sign; // 保存成功与否的标志
	
	
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getReturn_code() {
		return return_code;
	}

	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}

	public String getZp_job_template() {
		return zp_job_template;
	}

	public void setZp_job_template(String zp_job_template) {
		this.zp_job_template = zp_job_template;
	}

	public String getPs_c_card_attach() {
		return ps_c_card_attach;
	}

	public void setPs_c_card_attach(String ps_c_card_attach) {
		this.ps_c_card_attach = ps_c_card_attach;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}


	public FormFile getPicturefile() {
		return picturefile;
	}

	public void setPicturefile(FormFile picturefile) {
		this.picturefile = picturefile;
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("fieldstr", this.fieldstr);
		this.getFormHM().put("partylike", partylike);
		this.getFormHM().put("querylike", querylike);
		this.getFormHM().put("query", query);
		this.getFormHM().put("backdate", backdate);
		this.getFormHM().put("param", param);
		this.getFormHM().put("infofieldlist", infofieldlist);
		this.getFormHM().put("a_code", a_code);
		this.getFormHM().put("fieldsetid", fieldsetid);
		this.getFormHM().put("first", first);
		if(this.getPagination()!=null){
			this.getFormHM().put("codeitemlist",this.getPagination().getSelectedList());
			this.getPagination().unSelectedAll();
		}
		this.getFormHM().put("codeitemid", codeitemid);
		this.getFormHM().put("type", type);
		this.getFormHM().put("end_date", end_date);
		this.getFormHM().put("subtype", subtype);
		this.getFormHM().put("i9999", i9999);
		this.getFormHM().put("selectedlist",this.getPartyBusinessForm().getSelectedList());
		this.getFormHM().put("ps_c_job", ps_c_job);
		this.getFormHM().put("filename", this.filename);
		this.getFormHM().put("picturefile", this.picturefile);
		this.getFormHM().put("sign",sign);
	}

	@Override
    public void outPutFormHM() {
		this.setCodesetid((String)this.getFormHM().get("codesetid"));
		this.setCodesetdesc((String)this.getFormHM().get("codesetdesc"));
		this.setFieldstr((String)this.getFormHM().get("fieldstr"));
		this.setPartylike((String)this.getFormHM().get("partylike"));
		this.setQuery((String)this.getFormHM().get("query"));
		this.setIsShowCondition((String)this.getFormHM().get("isShowCondition"));
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setWherestr((String)this.getFormHM().get("wherestr"));
		this.setColumnstr((String)this.getFormHM().get("columnstr"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setSelectfieldlist((ArrayList)this.getFormHM().get("selectfieldlist"));
		this.setCodemess((String)this.getFormHM().get("codemess"));
		this.setFieldsetlist((ArrayList)this.getFormHM().get("fieldsetlist"));
		this.setInfofieldlist((ArrayList)this.getFormHM().get("infofieldlist"));
		this.setFirst((String)this.getFormHM().get("first"));
		this.setCodeitemid((String)this.getFormHM().get("codeitemid"));
		this.setCodeitemdesc((String)this.getFormHM().get("codeitemdesc"));
		this.setIsrefresh((String)this.getFormHM().get("isrefresh"));
		this.setCodeitemlist((ArrayList)this.getFormHM().get("codeitemlist"));
		this.setCodeitemidlist((ArrayList)this.getFormHM().get("codeitemidlist"));
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.getPartyBusinessForm().setList((ArrayList)this.getFormHM().get("list"));
		this.getPartyBusinessForm().getPagination().gotoPage(current);
		this.setType((String)this.getFormHM().get("type"));
		this.setPs_c_job((String)this.getFormHM().get("ps_c_job"));
		this.setMedialist((ArrayList)this.getFormHM().get("medialist"));
		this.setPs_c_card_attach((String)this.getFormHM().get("ps_c_card_attach"));
		this.setZp_job_template((String)this.getFormHM().get("zp_job_template"));
		this.setReturn_code((String)this.getFormHM().get("return_code"));
		this.setBackdate((String)this.getFormHM().get("backdate"));
		
		this.setSign((String)this.getFormHM().get("sign"));
	}

	public ArrayList getMedialist() {
		return medialist;
	}

	public void setMedialist(ArrayList medialist) {
		this.medialist = medialist;
	}

	@Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
		
	        if("/dtgh/party/searchpartybusinesslist".equals(mapping.getPath())&&(request.getParameter("b_query_sub")!=null||request.getParameter("b_save_sub")!=null||request.getParameter("b_delete_sub")!=null))
	        {
	           if(this.subtype!=null&& "edit".equalsIgnoreCase(this.subtype))
	           {
	        	   if(this.partyBusinessForm.getPagination()!=null)	 	             
	 	            current=this.partyBusinessForm.getPagination().getCurrent();
	           }else
	           {
	        	   if(this.partyBusinessForm.getPagination()!=null)
	        	   {
	        		    this.partyBusinessForm.getPagination().lastPage();
		 	            current=this.partyBusinessForm.getPagination().getCurrent();
	        	   }  
	           }
	           request.setAttribute("targetWindow", "0");
	        }
	        
	        if("/dtgh/party/searchpartybusinesslist".equals(mapping.getPath())&& request.getParameter("b_query")!=null && "init".equals(request.getParameter("b_query")))
	        {
	            if(this.partyBusinessForm.getPagination()!=null)
	            {
	               this.setPagination(null);
	            }
	                
	        }   
		return super.validate(mapping, request);
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getCodesetdesc() {
		return codesetdesc;
	}

	public void setCodesetdesc(String codesetdesc) {
		this.codesetdesc = codesetdesc;
	}

	public String getFieldstr() {
		return fieldstr;
	}

	public void setFieldstr(String fieldstr) {
		this.fieldstr = fieldstr;
	}

	public String getPartylike() {
		return partylike;
	}

	public void setPartylike(String partylike) {
		this.partylike = partylike;
	}

	public String getQuerylike() {
		return querylike;
	}

	public void setQuerylike(String querylike) {
		this.querylike = querylike;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getWherestr() {
		return wherestr;
	}

	public void setWherestr(String wherestr) {
		this.wherestr = wherestr;
	}

	public String getColumnstr() {
		return columnstr;
	}

	public void setColumnstr(String columnstr) {
		this.columnstr = columnstr;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public ArrayList getSelectfieldlist() {
		return selectfieldlist;
	}

	public void setSelectfieldlist(ArrayList selectfieldlist) {
		this.selectfieldlist = selectfieldlist;
	}

	public String getCodemess() {
		return codemess;
	}

	public void setCodemess(String codemess) {
		this.codemess = codemess;
	}

	public String getIsShowCondition() {
		return isShowCondition;
	}

	public void setIsShowCondition(String isShowCondition) {
		this.isShowCondition = isShowCondition;
	}

	public String getBackdate() {
		return backdate;
	}

	public void setBackdate(String backdate) {
		this.backdate = backdate;
	}

	public ArrayList getFieldsetlist() {
		return fieldsetlist;
	}

	public void setFieldsetlist(ArrayList fieldsetlist) {
		this.fieldsetlist = fieldsetlist;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public ArrayList getInfofieldlist() {
		return infofieldlist;
	}

	public void setInfofieldlist(ArrayList infofieldlist) {
		this.infofieldlist = infofieldlist;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getCodeitemid() {
		return codeitemid;
	}

	public void setCodeitemid(String codeitemid) {
		this.codeitemid = codeitemid;
	}

	public String getCodeitemdesc() {
		return codeitemdesc;
	}

	public void setCodeitemdesc(String codeitemdesc) {
		this.codeitemdesc = codeitemdesc;
	}

	public String getIsrefresh() {
		return isrefresh;
	}

	public void setIsrefresh(String isrefresh) {
		this.isrefresh = isrefresh;
	}

	public ArrayList getCodeitemlist() {
		return codeitemlist;
	}

	public void setCodeitemlist(ArrayList codeitemlist) {
		this.codeitemlist = codeitemlist;
	}

	public ArrayList getCodeitemidlist() {
		return codeitemidlist;
	}

	public void setCodeitemidlist(ArrayList codeitemidlist) {
		this.codeitemidlist = codeitemidlist;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public PaginationForm getPartyBusinessForm() {
		return partyBusinessForm;
	}

	public void setPartyBusinessForm(PaginationForm partyBusinessForm) {
		this.partyBusinessForm = partyBusinessForm;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getI9999() {
		return i9999;
	}

	public void setI9999(String i9999) {
		this.i9999 = i9999;
	}

	public String getPs_c_job() {
		return ps_c_job;
	}

	public void setPs_c_job(String ps_c_job) {
		this.ps_c_job = ps_c_job;
	}

    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }

    public String getReturnvalue() {
        return returnvalue;
    }

}
