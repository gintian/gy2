package com.hjsj.hrms.actionform.performance.options;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:CheckBodyObjectForm.java</p>
 * <p> Description:定义主体类别</p>
 * <p>Company:hjsj</p>
 * <p> create time:2009-06-19 10:14:15</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class CheckBodyObjectForm extends FrameForm
{
    // private String bodyId;
    // private String name;
    // private String status;
    // private String Seq;
    private String busitype = "0"; // 业务分类 =0(绩效考核); =1(能力素质)	
    private String bodyType;
    private String dbType;//数据库类型，由于level在oracle中是关键字所以在sql中的level对应oracle中的level_O
    /**
      * 参数对象
      */
    private RecordVo checkbodyobjectvo = new RecordVo("per_mainbodyset");
    /** 标识操作信息 */
    private String info;
    /** 删除字符串 */
    private String deletestr;
    /** 代表上移和下移 */
    private String num;
    /** 用于控制显示按钮 */
    private String show;

    /***********************************************************************
     * 临时中的项目调整顺序
     */
    private String[] sort_fields;
    private ArrayList sortlist = new ArrayList();
    private int current = 1;
    private PaginationForm setlistform = new PaginationForm();
    private ArrayList setlist = new ArrayList();   
    private String noself;//区分是民主推荐-投票人类别=1 还是绩效主体类别=0

    
    @Override
    public void inPutTransHM()
    {
		this.getFormHM().put("dbType",this.getDbType());
		// this.getFormHM().put("bodyId",this.getBodyId());
		// this.getFormHM().put("name",this.getName());
		// this.getFormHM().put("status",this.getStatus());
		this.getFormHM().put("bodyType", this.getBodyType());
		this.getFormHM().put("busitype",this.getBusitype());
	
		this.getFormHM().put("num", this.getNum());
		this.getFormHM().put("show", this.getShow());
		this.getFormHM().put("deletestr", this.getDeletestr());
		this.getFormHM().put("info", this.getInfo());
		this.getFormHM().put("checkbodyobjectvo", this.getCheckbodyobjectvo());
		this.getFormHM().put("noself", this.getNoself());
    }

    @Override
    public void outPutFormHM()
    {
	    this.setReturnflag((String)this.getFormHM().get("returnflag")); 
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("setlist"));
		this.setSetlist((ArrayList) this.getFormHM().get("setlist"));
		this.setInfo((String) this.getFormHM().get("info"));
		this.setShow((String) this.getFormHM().get("show"));
		this.setSort_fields((String[]) this.getFormHM().get("sort_fields"));
		this.setSortlist((ArrayList) this.getFormHM().get("sortlist"));
		this.setCheckbodyobjectvo((RecordVo) this.getFormHM().get("checkbodyobjectvo"));
		this.setDbType((String)this.getFormHM().get("dbType"));
		this.setBusitype((String)this.getFormHM().get("busitype"));
		// this.setName((String)this.getFormHM().get("name"));
		// this.setStatus((String)this.getFormHM().get("status"));
		this.setBodyType((String) this.getFormHM().get("bodyType"));
		this.setNoself((String) this.getFormHM().get("noself"));
    }

    
    public String getInfo()
    {
    	return info;
    }

    public void setInfo(String info)
    {
    	this.info = info;
    }

    public PaginationForm getSetlistform()
    {
    	return setlistform;
    }

    public void setSetlistform(PaginationForm setlistform)
    {
    	this.setlistform = setlistform;
    }

    public ArrayList getSetlist()
    {
    	return setlist;
    }

    public void setSetlist(ArrayList setlist)
    {
    	this.setlist = setlist;
    }

    public String getDeletestr()
    {
    	return deletestr;
    }

    public void setDeletestr(String deletestr)
    {
    	this.deletestr = deletestr;
    }

    public String getNum()
    {
    	return num;
    }

    public void setNum(String num)
    {
    	this.num = num;
    }

    public String getShow()
    {
    	return show;
    }

    public void setShow(String show)
    {
    	this.show = show;
    }

    public String[] getSort_fields()
    {
    	return sort_fields;
    }

    public void setSort_fields(String[] sort_fields)
    {
    	this.sort_fields = sort_fields;
    }

    public ArrayList getSortlist()
    {
    	return sortlist;
    }

    public void setSortlist(ArrayList sortlist)
    {
    	this.sortlist = sortlist;
    }

    public String getBodyType()
    {
    	return bodyType;
    }

    public void setBodyType(String bodyType)
    {
    	this.bodyType = bodyType;
    }

    public RecordVo getCheckbodyobjectvo()
    {
    	return checkbodyobjectvo;
    }

    public void setCheckbodyobjectvo(RecordVo checkbodyobjectvo)
    {
    	this.checkbodyobjectvo = checkbodyobjectvo;
    }

    public int getCurrent()
    {
    	return current;
    }

    public void setCurrent(int current)
    {
    	this.current = current;
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
    {
    	return super.validate(arg0, arg1);
    }

    public String getDbType()
    {   
        return dbType;
    }
    public void setDbType(String dbType)
    {   
        this.dbType = dbType;
    }

	public String getNoself() {
		return noself;
	}

	public void setNoself(String noself) {
		this.noself = noself;
	}

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

}
