/**
 * <p>Title:MultMediaForm.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-22 上午11:05:23</p>
 * <p>@version: 6.0</p>
 * <p>@author:wangrd</p>
 */
package com.hjsj.hrms.actionform.general.inform.multimedia;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:MultMediaForm.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-22 上午11:05:23</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class MultiMediaFileForm extends FrameForm {
    /**模块类型*/
    private String kind="6";//6 人员
    private String dbFlag="A";//6 人员
    /**应用库*/
    private String nbase;
    /**信息集*/
    private String setId; 
    private String editFlag="false";//是否修改

    private String a0100;
    private String i9999="";
    private String mainGuid="";//主集guid
    private String childGuid="";//子集guid
    private String mediaId="";//多媒体id
    
    /**多媒体文件list*/
    private ArrayList multimedialist;
    
    private String isvisible;/**控制页面是否显示关闭按钮*/
    private String canEdit;/**是否允许操作 true=允许操作 false=只能查看 edit=附件审批 view=附件审批查看*/ 
    
    /**自助附件审批*/
    private String chg_id;
    private String sequence;
    private String state;
    private String modified;
    
    private String pos="";
    
    private String unit="";
    private String b0110;   
    private String e0122;
    private String e01a1;
    private String a0101;    
    
    /**文件夹名称*/
    private String foldername="";
    private String multimediaflag="";
    /**判断是业务还是自助平台*/
    private String is_yewu;  
    
    private int current=1;     
    
    /**多媒体分类*/
    private ArrayList fileTypeList = new ArrayList();
    /**上传的文件类型*/
    private String filetype="";
    /**上传的文件标题*/
    private String filetitle="";
    /**文件上传路径*/
    private String filepath="";
    /**说明*/
    private String decription="";
    /**多媒体*/
    private FormFile picturefile;

    private PaginationForm recordListForm=new PaginationForm(); 
    /**
     * 关联多媒体文件list
     */
    private ArrayList a00_mul_list = new ArrayList();
    private String setname;//信息集ID
    private String fieldsetdesc;//信息集名称
    private String delete_record;//删除多媒体子集记录 0不删，1删除
    
    public ArrayList getA00_mul_list() {
		return a00_mul_list;
	}
	public void setA00_mul_list(ArrayList a00_mul_list) {
		this.a00_mul_list = a00_mul_list;
	}
	public String getSetname() {
		return setname;
	}
	public void setSetname(String setname) {
		this.setname = setname;
	}
	public String getFieldsetdesc() {
		return fieldsetdesc;
	}
	public void setFieldsetdesc(String fieldsetdesc) {
		this.fieldsetdesc = fieldsetdesc;
	}
	public String getDelete_record() {
		return delete_record;
	}
	public void setDelete_record(String delete_record) {
		this.delete_record = delete_record;
	}
	public String getFiletitle() {
        return filetitle;
    }
    public void setFiletitle(String filetitle) {
        this.filetitle = filetitle;
    }
    @Override
    public void inPutTransHM() {
        this.getFormHM().put("kind",this.getKind());
        this.getFormHM().put("dbflag",this.getDbFlag());
        this.getFormHM().put("nbase",this.getNbase());
        this.getFormHM().put("setid",this.getSetId());
        this.getFormHM().put("a0100",this.getA0100());
        this.getFormHM().put("i9999",this.getI9999());
        this.getFormHM().put("foldername",this.getFoldername());    
        this.getFormHM().put("isvisible", this.getIsvisible());
        this.getFormHM().put("canedit", this.getCanEdit());
        this.getFormHM().put("filetitle",this.getFiletitle());
        this.getFormHM().put("mediaId",this.getMediaId());
        
        try{
            if(this.recordListForm.getPagination().getSelectedList()!=null)
            {                
                ArrayList list = (ArrayList)this.getRecordListForm().getSelectedList();
                this.getFormHM().put("selectedlist",list);
            }
            }
        catch(Exception e){
            
        }
        this.getFormHM().put("picturefile",this.getPicturefile());
        this.getFormHM().put("filetype",this.getFiletype());
        this.getFormHM().put("filetitle",this.getFiletitle());
        this.getFormHM().put("description",this.getDecription());
        this.getFormHM().put("filepath",this.getFilepath());
        
        this.getFormHM().put("editflag",this.getEditFlag());
        this.getFormHM().put("mainguid",this.getMainGuid());
        this.getFormHM().put("childguid",this.getChildGuid());
        
        this.getFormHM().put("a00_mul_list", this.getA00_mul_list());
        this.getFormHM().put("setname", this.getSetname());
        this.getFormHM().put("fieldsetdesc", this.getFieldsetdesc());
        this.getFormHM().put("delete_record", this.getDelete_record());
        
        this.getFormHM().put("chg_id", this.getChg_id());
        this.getFormHM().put("sequence", this.getSequence());
        this.getFormHM().put("state", this.getState());
        this.getFormHM().put("modified", this.getModified());
        
    }
    @Override
    public void outPutFormHM() {
        this.setMainGuid((String)this.getFormHM().get("mainguid"));
        this.setChildGuid((String)this.getFormHM().get("childguid"));
        this.setMultimediaflag((String)this.getFormHM().get("multimediaflag"));
        this.setIsvisible((String)this.getFormHM().get("isvisible"));
        this.setCanEdit((String)this.getFormHM().get("canedit"));
        
        this.setMediaId((String)this.getFormHM().get("mediaId"));
        this.setKind((String)this.getFormHM().get("kind"));
        this.setDbFlag((String)this.getFormHM().get("dbflag"));
        this.setNbase((String)this.getFormHM().get("nbase"));
        this.setSetId((String)this.getFormHM().get("setid"));
        this.setA0100((String)this.getFormHM().get("a0100"));    
        this.setI9999((String)this.getFormHM().get("i9999"));
        this.setMultimedialist((ArrayList)this.getFormHM().get("multimedialist"));        
        this.getRecordListForm().setList((ArrayList)this.getFormHM().get("multimedialist"));
        this.getRecordListForm().getPagination().gotoPage(current);
        
        this.setFileTypeList((ArrayList)this.getFormHM().get("fileTypeList"));
        this.setFiletype((String)this.getFormHM().get("filetype"));
        this.setFiletitle((String)this.getFormHM().get("filetitle"));
        this.setFilepath((String)this.getFormHM().get("filepath"));
        this.setDecription((String)this.getFormHM().get("description"));
        //【2034】员工管理\信息浏览\附件：设成“每页6刷新“，但再次选择该分类，设置没生效  jingq add 2014.11.18
        this.setPagerows(this.getRecordListForm().getPagination().getPageCount());
        
        this.setUnit((String)this.getFormHM().get("unit"));
        this.setPos((String)this.getFormHM().get("pos"));
        this.setB0110((String)this.getFormHM().get("b0110"));
        this.setE0122((String)this.getFormHM().get("e0122"));
        this.setE01a1((String)this.getFormHM().get("e01a1"));
        this.setA0101((String)this.getFormHM().get("a0101"));
        
        this.setEditFlag((String)this.getFormHM().get("editflag"));

        
        this.setPicturefile((FormFile)this.getFormHM().get("picturefile"));
        this.setA00_mul_list((ArrayList)this.getFormHM().get("a00_mul_list"));
        this.setSetname((String)this.getFormHM().get("setname"));
        this.setFieldsetdesc((String)this.getFormHM().get("fieldsetdesc"));
        this.setDelete_record((String)this.getFormHM().get("delete_record"));
        
        this.setSequence((String)this.getFormHM().get("sequence"));
        this.setModified((String)this.getFormHM().get("modified"));
    }
    
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
       try
       {

         arg1.setAttribute("targetWindow", "1");
        if("/general/inform/multimedia/multimedia_tree".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        	    this.modified = "false";
 
        if("/general/inform/multimedia/opermultimedia".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            String link= arg1.getParameter("b_query").toString();
            if (link!=null && "link".equals(link)){                
                if(this.recordListForm.getPagination()!=null)
                    this.recordListForm.getPagination().firstPage();        
                if(this.recordListForm.getPagination()!=null)
                    current=this.recordListForm.getPagination().getCurrent();
                this.pagerows=15;
            }
        }  
        if("/general/inform/multimedia/opermultimedia".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
        {
            if(this.recordListForm.getPagination()!=null)
              current=this.recordListForm.getPagination().getCurrent();
        }

        if("/general/inform/org_tree".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
        {
            if(this.recordListForm.getPagination()!=null)
              current=this.recordListForm.getPagination().getCurrent();
            this.setSetId("");
        }
       }catch(Exception e)
       {
          e.printStackTrace();
       }
         return super.validate(arg0, arg1);
    }
    public String getNbase() {
        return nbase;
    }
    public void setNbase(String dbname) {
        this.nbase = dbname;
    }
    public String getA0100() {
        return a0100;
    }
    public void setA0100(String a0100) {
        this.a0100 = a0100;
    }
    public String getKind() {
        return kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
    public String getIs_yewu() {
        return is_yewu;
    }
    public void setIs_yewu(String is_yewu) {
        this.is_yewu = is_yewu;
    }
    public String getI9999() {
        return i9999;
    }
    public void setI9999(String i9999) {
        this.i9999 = i9999;
    }
    public String getFoldername() {
        return foldername;
    }
    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }
    public String getMultimediaflag() {
        return multimediaflag;
    }
    public void setMultimediaflag(String multimediaflag) {
        this.multimediaflag = multimediaflag;
    }
    public ArrayList getMultimedialist() {
        return multimedialist;
    }
    public void setMultimedialist(ArrayList multimedialist) {
        this.multimedialist = multimedialist;
    }
    public String getIsvisible() {
        return isvisible;
    }
    public void setIsvisible(String isvisible) {
        this.isvisible = isvisible;
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
    public String getA0101() {
        return a0101;
    }
    public void setA0101(String a0101) {
        this.a0101 = a0101;
    }
    public PaginationForm getRecordListForm() {
        return recordListForm;
    }
    public void setRecordListForm(PaginationForm recordListForm) {
        this.recordListForm = recordListForm;
    }
    public String getSetId() {
        return setId;
    }
    public void setSetId(String setId) {
        this.setId = setId;
    }

    public FormFile getPicturefile() {
        return picturefile;
    }
    public void setPicturefile(FormFile picturefile) {
        this.picturefile = picturefile;
    }
    public ArrayList getFileTypeList() {
        return fileTypeList;
    }
    public void setFileTypeList(ArrayList fileTypeList) {
        this.fileTypeList = fileTypeList;
    }
    public String getFiletype() {
        return filetype;
    }
    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }
    public String getFilepath() {
        return filepath;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    public String getDecription() {
        return decription;
    }
    public void setDecription(String decription) {
        this.decription = decription;
    }
    public String getEditFlag() {
        return editFlag;
    }
    public void setEditFlag(String editFlag) {
        this.editFlag = editFlag;
    }
    public String getMainGuid() {
        return mainGuid;
    }
    public void setMainGuid(String mainGuid) {
        this.mainGuid = mainGuid;
    }
    public String getChildGuid() {
        return childGuid;
    }
    public void setChildGuid(String childGuid) {
        this.childGuid = childGuid;
    }
    public String getMediaId() {
        return mediaId;
    }
    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
    public String getDbFlag() {
        return dbFlag;
    }
    public void setDbFlag(String dbFlag) {
        this.dbFlag = dbFlag;
    }
    public String getCanEdit() {
        return canEdit;
    }
    public void setCanEdit(String canEdit) {
        this.canEdit = canEdit;
    }
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getChg_id() {
		return chg_id;
	}
	public void setChg_id(String chg_id) {
		this.chg_id = chg_id;
	}
    
	
    

}
