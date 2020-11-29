/*
 * Created on 2005-5-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.propose;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class HtmlFileListForm extends FrameForm {

	private String flag = "0";

	private String fileflag = "0";

	private FormFile file;

	private String status = "1";

	private String name = "";

	private String description = "";

	private String message = "";
	
	private String unitcode = "";//所属单位

	/**
	 * 匿名
	 */

	private RecordVo htmlFileListvo = new RecordVo("resource_list");

	/**
	 * 建议对象列表
	 */
	private PaginationForm htmlFileListForm = new PaginationForm();

	/**
	 * 
	 */

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		if (this.getFormHM().get("proposeTb") != null)
			this
					.setHtmlFileListvo((RecordVo) this.getFormHM().get(
							"proposeTb"));
//		System.out.println(this.htmlFileListvo);		
		this.getHtmlFileListForm().setList(
				(ArrayList) this.getFormHM().get("proposelist"));
		this.setUnitcode(this.getFormHM().get("unitcode").toString());
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getHtmlFileListForm().getSelectedList());
		this.getFormHM().put("proposevo", this.getHtmlFileListvo());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("fileflag", this.getFileflag());
		this.getFormHM().put("file", this.getFile());
		this.getFormHM().put("unitcode", this.getUnitcode());
	}

	/**
	 * @return Returns the htmlFileListForm.
	 */
	public PaginationForm getHtmlFileListForm() {
		return htmlFileListForm;
	}

	/**
	 * @param htmlFileListForm
	 *            The htmlFileListForm to set.
	 */
	public void setHtmlFileListForm(PaginationForm htmlFileListForm) {
		this.htmlFileListForm = htmlFileListForm;
	}

	/**
	 * @return Returns the htmlFileListvo.
	 */
	public RecordVo getHtmlFileListvo() {
		return htmlFileListvo;
	}

	/**
	 * @param htmlFileListvo
	 *            The htmlFileListvo to set.
	 */
	public void setHtmlFileListvo(RecordVo htmlFileListvo) {
		this.htmlFileListvo = htmlFileListvo;
	}

	/*
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		super.reset(arg0, arg1);
	}

	/*
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)

	{
		String tp = arg1.getParameter("fileflag");
		if (tp == null || "".equals(tp)) {

		} else {
			this.setFileflag(tp);
			this.getFormHM().put("fileflag", this.getFileflag());
		}

		if ("/selfservice/propose/searchhtmlFileList".equals(arg0.getPath())
				&& arg1.getParameter("b_add") != null) {
			this.setFlag("1");
			this.getHtmlFileListvo().clearValues();
		}
		if ("/selfservice/propose/upsend".equals(arg0.getPath())
				&& arg1.getParameter("b_add") != null) {
			this.setFlag("1");
		}

		if ("/selfservice/propose/upfilelist".equals(arg0.getPath())
				&& arg1.getParameter("b_up") != null){
			
			this.setName("");
			this.setDescription("");

		}
		/** 编辑 */
		if ("/selfservice/propose/addhtmlfile".equals(arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.setFlag("0");
		}	
		if("/selfservice/propose/upfilelist".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
			//【4797】系统管理/内容维护/流程上传，打开表格上传，翻至第2页，打开流程上传，直接就是第二页  jingq add 2014.11.10
			if(this.htmlFileListForm.getPagination()!=null){
				this.htmlFileListForm.getPagination().firstPage();
			}
		}
		/*如果是有新增标识，跳到最后一页 guodd 2018-12-12*/
		String isAdd = (String)this.getFormHM().get("isAdd");
		if("true".equals(isAdd)) {
			this.getFormHM().remove("isAdd");
			if(this.htmlFileListForm.getPagination()!=null){
				this.htmlFileListForm.getPagination().lastPage();
			}
		}
		return super.validate(arg0, arg1);
	}

	/**
	 * @return Returns the flag.
	 */
	public String getFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            The flag to set.
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setFileflag(String fileflag) {
		this.fileflag = fileflag;
	}

	public String getFileflag() {
		return this.fileflag;
	}

	// 取得一个随机数
	public String getStrg() {
		String filenametmp = "";
		try {
			RandomStrg RSTR = new RandomStrg();

			RSTR.setCharset("a-z");
			RSTR.setLength("3");
			RSTR.generateRandomObject();

			filenametmp = (new java.util.Date()).toLocaleString();
			String filenametemp2 = "";
			for (int i = 0; i < filenametmp.length(); i++) {
				if ("9".equals(filenametmp.substring(i, i + 1))
						|| "8".equals(filenametmp.substring(i, i + 1))
						|| "7".equals(filenametmp.substring(i, i + 1))
						|| "6".equals(filenametmp.substring(i, i + 1))
						|| "5".equals(filenametmp.substring(i, i + 1))
						|| "4".equals(filenametmp.substring(i, i + 1))
						|| "3".equals(filenametmp.substring(i, i + 1))
						|| "2".equals(filenametmp.substring(i, i + 1))
						|| "1".equals(filenametmp.substring(i, i + 1))
						|| "0".equals(filenametmp.substring(i, i + 1)))
					filenametemp2 = filenametemp2
							+ (filenametmp.substring(i, i + 1));
			}

			filenametmp = filenametemp2 + RSTR.getRandom();
			return filenametmp;
		} catch (Exception ex) {

		}
		return "";
	}

	/**
	 * @return 返回 message。
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            要设置的 message。
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}
	
	
}