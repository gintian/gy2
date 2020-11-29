package com.hjsj.hrms.actionform.train.resource;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class TrainFileForm extends FrameForm {
	private String filesql = "";
	private String strwhere;
	private String columns;
	private FormFile picturefile; //上传文件
	private ArrayList filelist = new ArrayList();
	private String filename = "";
	private String itemid = "";
	private String type = "";  //区分上传的文件的所属 [0或空--培训资料][1--培训教师]
	private String myself = ""; //区分是否自助;0,自助服务；1,培训管理
	
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("picturefile", this.getPicturefile());
		this.getFormHM().put("filename", this.getFilename());
		this.getFormHM().put("type", this.getType());
		this.getFormHM().put("myself", this.getMyself());
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setFilesql((String) this.getFormHM().get("filesql"));
		this.setStrwhere((String) this.getFormHM().get("strwhere"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setFilelist((ArrayList) this.getFormHM().get("filelist"));
		this.setItemid((String) this.getFormHM().get("itemid"));
		this.setFilename((String)this.getFormHM().get("filename"));
		this.setType((String) this.getFormHM().get("type"));
		this.setPicturefile((FormFile)this.getFormHM().get("picturefile"));
		this.setMyself((String) this.getFormHM().get("myself"));
	}

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {

        try {
            if ("/train/resource/file_upload".equals(arg0.getPath()) && arg1.getParameter("b_query") != null) {
                if (this.getPagination() != null){
                    this.getPagination().firstPage();
                    this.pagerows=20;
                }
                arg1.setAttribute("formpath", "/train/resource/file_upload.do?b_query=link&r0701="+this.itemid+"&type="+this.type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.validate(arg0, arg1);
    }
	
	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public ArrayList getFilelist() {
		return filelist;
	}

	public void setFilelist(ArrayList filelist) {
		this.filelist = filelist;
	}

	public String getFilesql() {
		return filesql;
	}

	public void setFilesql(String filesql) {
		this.filesql = filesql;
	}

	public FormFile getPicturefile() {
		return picturefile;
	}

	public void setPicturefile(FormFile picturefile) {
		this.picturefile = picturefile;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getMyself() {
		return myself;
	}

	public void setMyself(String myself) {
		this.myself = myself;
	}

}
