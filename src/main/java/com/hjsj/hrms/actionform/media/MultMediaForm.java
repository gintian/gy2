/*
 * Created on 2005-8-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.media;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MultMediaForm extends FrameForm {

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
	 * @return Returns the cond_str.
	 */
	public String getCond_str() {
		return cond_str;
	}
	/**
	 * @param cond_str The cond_str to set.
	 */
	public void setCond_str(String cond_str) {
		this.cond_str = cond_str;
	}
	/**
	 * @return Returns the dbcond.
	 */
	public String getDbcond() {
		return dbcond;
	}
	/**
	 * @param dbcond The dbcond to set.
	 */
	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}
	/**
	 * @return Returns the filesort.
	 */
	public String getFilesort() {
		return filesort;
	}
	/**
	 * @param filesort The filesort to set.
	 */
	public void setFilesort(String filesort) {
		this.filesort = filesort;
	}
	/**
	 * @return Returns the filetitle.
	 */
	public String getFiletitle() {
		return filetitle;
	}
	/**
	 * @param filetitle The filetitle to set.
	 */
	public void setFiletitle(String filetitle) {
		this.filetitle = filetitle;
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
	/**
	 * @return Returns the mediafile.
	 */
	public FormFile getMediafile() {
		return mediafile;
	}
	/**
	 * @param mediafile The mediafile to set.
	 */
	public void setMediafile(FormFile mediafile) {
		this.mediafile = mediafile;
	}
	/**
	 * @return Returns the order_by.
	 */
	public String getOrder_by() {
		return order_by;
	}
	/**
	 * @param order_by The order_by to set.
	 */
	public void setOrder_by(String order_by) {
		this.order_by = order_by;
	}
	/**
	 * @return Returns the sortcond.
	 */
	public String getSortcond() {
		return sortcond;
	}
	/**
	 * @param sortcond The sortcond to set.
	 */
	public void setSortcond(String sortcond) {
		this.sortcond = sortcond;
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
	 * @return Returns the userbase.
	 */
	public String getUserbase() {
		return userbase;
	}
	/**
	 * @param userbase The userbase to set.
	 */
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	private String userbase="Usr";
	private String treeCode;
	private String strsql;
	private String cond_str;
	private String code;
	private String kind;
	private String dbcond;
	private String order_by;
	private String filetitle;
	private FormFile mediafile;
	private String filesort;         //上传的文件类型
	private String sortcond;         //多媒体类型的查询权限的字符串
	private String a0100;
	private String i9999;
	private String returnvalue;
	private HttpSession session;
	private int current=1;
	private String b0110;
	private String e0122;
	private String e01a1;
	private String a0101;
	private String isUserEmploy; //is 0为自助 1为业务
	private String setname="a00";
	private String check_main;
	private ArrayList fileTypeList=new ArrayList();
	/**
	 * 是否是申请修改，0为否，1为是
	 */
	private String isAppEdite = "0";
	/**
	 * 人员信息是否需要审批功能
	 * =0或null需要
	 * =1不需要
	 */
	private String approveflag;
	private String inputchinfor;
	private ArrayList browsefields=new ArrayList();
	private String columns;
	private String setprv;
	/*标志是否是从浏览页面进入的多媒体修改页面 1为是，0为否*/
	private String button;
	
	private String multimedia_maxsize;//多媒体文件大小
	private String virAxx;
	
	public String getMultimedia_maxsize() {
		return multimedia_maxsize;
	}
	public void setMultimedia_maxsize(String multimedia_maxsize) {
		this.multimedia_maxsize = multimedia_maxsize;
	}
	public String getButton() {
		return button;
	}
	public void setButton(String button) {
		this.button = button;
	}
	public String getApproveflag() {
		return approveflag;
	}
	public void setApproveflag(String approveflag) {
		this.approveflag = approveflag;
	}
	public ArrayList getBrowsefields() {
		return browsefields;
	}
	public void setBrowsefields(ArrayList browsefields) {
		this.browsefields = browsefields;
	}
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	/**
	 * @return Returns the a0101.
	 */
	public String getA0101() {
		return a0101;
	}
	/**
	 * @param a0101 The a0101 to set.
	 */
	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}
	/**
	 * @return Returns the b0110.
	 */
	public String getB0110() {
		return b0110;
	}
	/**
	 * @param b0110 The b0110 to set.
	 */
	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}
	/**
	 * @return Returns the e0122.
	 */
	public String getE0122() {
		return e0122;
	}
	/**
	 * @param e0122 The e0122 to set.
	 */
	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}
	/**
	 * @return Returns the e01a1.
	 */
	public String getE01a1() {
		return e01a1;
	}
	/**
	 * @param e01a1 The e01a1 to set.
	 */
	public void setE01a1(String e01a1) {
		this.e01a1 = e01a1;
	}
	/**
	 * @return Returns the session.
	 */
	public HttpSession getSession() {
		return session;
	}
	/**
	 * @param session The session to set.
	 */
	public void setSession(HttpSession session) {
		this.session = session;
	}
	/**
	 * @return Returns the returnvalue.
	 */
	public String getReturnvalue() {
		return returnvalue;
	}
	/**
	 * @param returnvalue The returnvalue to set.
	 */
	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}
	  private PaginationForm multMediaForm=new PaginationForm();  
	/**
	 * @return Returns the a0100.
	 */
	public String getA0100() {
		return a0100;
	}
	/**
	 * @param a0100 The a0100 to set.
	 */
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	/**
	 * @return Returns the i9999.
	 */
	public String getI9999() {
		return i9999;
	}
	/**
	 * @param i9999 The i9999 to set.
	 */
	public void setI9999(String i9999) {
		this.i9999 = i9999;
	}
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		 this.setUserbase((String)this.getFormHM().get("userbase"));
		 this.setA0100((String)this.getFormHM().get("a0100"));
	     this.setCond_str((String)this.getFormHM().get("cond_str"));
	     this.setCode((String)this.getFormHM().get("code"));
	     this.setKind((String)this.getFormHM().get("kind"));
	 	 this.setStrsql((String)this.getFormHM().get("strsql"));
	     this.setTreeCode((String)this.getFormHM().get("treeCode"));
	     this.setDbcond((String)this.getFormHM().get("dbcond"));
	     this.setOrder_by((String)this.getFormHM().get("order_by"));
	     this.getMultMediaForm().setList((ArrayList)this.getFormHM().get("detailinfolist"));
	     this.setSortcond((String)this.getFormHM().get("sortcond"));
	     this.setFiletitle((String)this.getFormHM().get("filetitle"));
	     this.setUserbase((String)this.getFormHM().get("userbase"));
	     this.getMultMediaForm().getPagination().gotoPage(current);
	     this.setB0110((String)this.getFormHM().get("b0110"));
	     this.setE0122((String)this.getFormHM().get("e0122"));
	     this.setE01a1((String)this.getFormHM().get("e01a1"));
	     this.setA0101((String)this.getFormHM().get("a0101"));
	     this.setMultimedia_maxsize((String)this.getFormHM().get("multimedia_maxsize"));
	     this.setIsUserEmploy((String)this.getFormHM().get("isUserEmploy"));
	     this.setBrowsefields((ArrayList)this.getFormHM().get("browsefields"));
	     this.setColumns((String)this.getFormHM().get("columns"));
	     this.setApproveflag((String)this.getFormHM().get("approveflag"));
	     this.setInputchinfor((String)this.getFormHM().get("inputchinfor"));
	     this.setFileTypeList((ArrayList)this.getFormHM().get("fileTypeList"));
	     this.setCheck_main((String)this.getFormHM().get("check_main"));
	     this.setIsAppEdite((String) this.getFormHM().get("isAppEdite"));
	     if (this.getFormHM().get("button") != null) {
	    	 this.setButton((String) this.getFormHM().get("button"));
	     }
	     if (this.getFormHM().get("returnvalue") != null) {
	    	 this.setReturnvalue((String) this.getFormHM().get("returnvalue"));
	     }
	     
	     this.setVirAxx((String)this.getFormHM().get("virAxx"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		//this.getFormHM().put("selectedlist",(ArrayList)this.getMultMediaForm().getSelectedList());
		if(this.getPagination()!=null) {
			this.getFormHM().put("selectedlist",(ArrayList)this.getPagination().getSelectedList());
		}else {
			this.getFormHM().put("selectedlist",new ArrayList());
		}
		this.getFormHM().put("userbase",userbase);
		this.getFormHM().put("a0100",a0100);
		this.getFormHM().put("i9999",i9999);
		this.getFormHM().put("filetitle",filetitle);
		this.getFormHM().put("filesort",filesort);
		this.getFormHM().put("file",mediafile);
		this.getFormHM().put("setname",setname);
		this.getFormHM().put("isUserEmploy",this.getIsUserEmploy());
		this.getFormHM().put("setprv",this.getSetprv());
		/**cmq changed from 多媒体信息按组织机构树过滤 at 20091228*/
		this.getFormHM().put("code", this.getCode());
		this.getFormHM().put("kind", this.getKind());
		/**end.*/
		this.setIsAppEdite("0");
		this.getFormHM().remove("isAppEdite");
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		if (map.get("isAppEdite") != null) {
			this.setIsAppEdite((String)map.get("isAppEdite"));
			((Map) this.getFormHM().get("requestPamaHM")).remove("isAppEdite");
		}
		this.getFormHM().put("isAppEdite", this.getIsAppEdite());
		this.getFormHM().put("button", map.get("button"));
		this.getFormHM().put("virAxx", this.getVirAxx());
	}

	/**
	 * @return Returns the multMediaForm.
	 */
	public PaginationForm getMultMediaForm() {
		return multMediaForm;
	}
	/**
	 * @param multMediaForm The multMediaForm to set.
	 */
	public void setMultMediaForm(PaginationForm multMediaForm) {
		this.multMediaForm = multMediaForm;
	}
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
	   try
	   {
	    if("/workbench/media/showinfodata".equals(arg0.getPath())&&arg1.getParameter("b_search")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();//?
        }	
	    if("/workbench/media/searchmediainfolist".equals(arg0.getPath()) && arg1.getParameter("br_add")!=null)
        {
        	if(this.multMediaForm.getPagination()!=null)
        	 this.multMediaForm.getPagination().lastPage();
        	 current=this.multMediaForm.getPagination().getCurrent();
        }  
        if("/workbench/media/searchmediainfolist".equals(arg0.getPath()) && arg1.getParameter("b_delete")!=null)
        {
        	if(this.multMediaForm.getPagination()!=null)
        	  current=this.multMediaForm.getPagination().getCurrent();
        }
	   /*	    session=arg1.getSession();
	   	   	if(this.mediafile!=null && arg0.getPath().equals("/workbench/media/upmediainfo") && arg1.getParameter("b_save")!=null)
	   	   	{
	   	   		if(this.getFiletitle()==null || this.getFiletitle().length()==0)
	                  this.filetitle=ResourceFactory.getProperty("conlumn.mediainfo.upfiletitle");   	    	  
	   			new StructureExecSqlString().MultiMediaInsert(this.getUserbase() + "A00",this.getA0100(),this.getMediafile(),this.filetitle,this.filesort,userView.getUserName()); 		   	
	   	   	}*/
	   }catch(Exception e)
	   {
	   	  e.printStackTrace();
	   }
         return super.validate(arg0, arg1);
	}
	public String getIsUserEmploy() {
		return isUserEmploy;
	}
	public void setIsUserEmploy(String isUserEmploy) {
		this.isUserEmploy = isUserEmploy;
	}
	public String getSetname() {
		return setname;
	}
	public void setSetname(String setname) {
		this.setname = setname;
	}
	public ArrayList getFileTypeList() {
		return fileTypeList;
	}
	public void setFileTypeList(ArrayList fileTypeList) {
		this.fileTypeList = fileTypeList;
	}
	public String getCheck_main() {
		return check_main;
	}
	public void setCheck_main(String check_main) {
		this.check_main = check_main;
	}
	public String getSetprv() {
		return setprv;
	}
	public void setSetprv(String setprv) {
		this.setprv = setprv;
	}
	public String getIsAppEdite() {
		return isAppEdite;
	}
	public void setIsAppEdite(String isAppEdite) {
		this.isAppEdite = isAppEdite;
	}
	public String getInputchinfor() {
		return inputchinfor;
	}
	public void setInputchinfor(String inputchinfor) {
		this.inputchinfor = inputchinfor;
	}
    public String getVirAxx() {
        return virAxx;
    }
    public void setVirAxx(String virAxx) {
        this.virAxx = virAxx;
    }
	
}
