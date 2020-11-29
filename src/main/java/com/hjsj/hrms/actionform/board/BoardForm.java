/*
 * Created on 2005-5-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.board;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author luangaojiong
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class BoardForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
	private String flag = "0";

	private String approve = "0";

	private FormFile file = null;

	private String message = "";
	
	private String usrnames;
	
	private String chflag="";
	private String selectPerson;
	private String noticeperson;
	private String trainid;
	
	private ArrayList spersonlist = new ArrayList();
	private String sperson;
	private String announce="";//通知类型 1 ehr系统公告栏 2 招聘首页公告 3 社会招聘公告 4 校园招聘公告 11 培训新闻
	private String opt="";//接口 1 公告栏维护 2 招聘公告、培训新闻
	
	private ArrayList msgList = new ArrayList();
	private String msg = "";
	private String unitcode = "";//该用户的所属单位
	
	private String tmpnbase = "";
	/**
	 * 公告栏维护添加模糊查询
	 * 下拉列表模糊查询需要的参数     jingq   add   2014.5.9
	 */
	private ArrayList typelist = new ArrayList();	//下拉列表显示的数据集合
	private String thistype;			//下拉列表默认显示的数据
	private String seltype;				//查询方式  1 界面按钮查询
	private String selparam;			//模糊查询字符串型参数
	private String type;				//现在 社会招聘公告 4 校园招聘公从数据库查，不是写死，这里为了区别是不是首页公告和招聘公示
	public String getSelparam() {
		return selparam;
	}

	public void setSelparam(String selparam) {
		this.selparam = selparam;
	}
	//模糊查询时间类型参数
	private String begintime;			//开始时间
	private String endtime;				//结束时间
	
	

	public String getBegintime() {
		return begintime;
	}

	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getSeltype() {
		return seltype;
	}

	public void setSeltype(String seltype) {
		this.seltype = seltype;
	}

	public String getThistype() {
		return thistype;
	}

	public void setThistype(String thistype) {
		this.thistype = thistype;
	}

	public ArrayList getTypelist() {
		return typelist;
	}

	public void setTypelist(ArrayList typelist) {
		this.typelist = typelist;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ArrayList getMsgList() {
		return msgList;
	}

	public void setMsgList(ArrayList msgList) {
		this.msgList = msgList;
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

	protected UserView userView = this.getUserView();

	/**
	 * 建议对象
	 */
	private RecordVo boardvo = new RecordVo("announce");

	/**
	 * 建议对象列表
	 */

	private PaginationForm boardForm = new PaginationForm();

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

	public String getAnnounce() {
		return announce;
	}

	public void setAnnounce(String announce) {
		this.announce = announce;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
    public void outPutFormHM() {
		this.setBoardvo((RecordVo) this.getFormHM().get("boardTb"));
		this.getBoardForm().setList(
				(ArrayList) this.getFormHM().get("boardlist"));
		String str = this.getBoardvo().getString("content");
        str = str.replaceAll("&lt;", "<");
        str = str.replaceAll("&gt;", ">");
        this.getBoardvo().setString("content", str);
        this.setChflag((String)this.getFormHM().get("chflag"));
        this.setSelectPerson((String)this.getFormHM().get("selectPerson"));
        this.setNoticeperson((String)this.getFormHM().get("noticeperson"));
        this.setSpersonlist((ArrayList)this.getFormHM().get("spersonlist"));
        this.setSperson((String)this.getFormHM().get("sperson"));
        this.setTrainid((String)this.getFormHM().get("trainid"));
        this.setOpt((String)this.getFormHM().get("opt"));
        this.setAnnounce((String)this.getFormHM().get("announce"));
        this.setMsgList((ArrayList)this.getFormHM().get("msgList"));
        this.setMsg(this.getFormHM().get("msg").toString());
        this.setUnitcode(this.getFormHM().get("unitcode").toString());
        this.setTmpnbase((String)this.getFormHM().get("tmpnbase"));
        
        this.setTypelist((ArrayList) this.getFormHM().get("typelist"));
        this.setThistype((String) this.getFormHM().get("thistype"));
        this.setSeltype((String) this.getFormHM().get("seltype"));
        this.setSelparam((String) this.getFormHM().get("selparam"));
        this.setBegintime((String) this.getFormHM().get("begintime"));
        this.setEndtime((String) this.getFormHM().get("endtime"));
        
        this.setType((String) this.getFormHM().get("type"));
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getBoardForm().getSelectedList());
		this.getFormHM().put("boardov", this.getBoardvo());
		//System.out.println(this.getBoardvo());
		this.getFormHM().put("boardTb", this.getBoardvo());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("file",this.getFile());
		this.getFormHM().put("usrnames",this.getUsrnames());
		this.getFormHM().put("chflag",this.getChflag());
		this.getFormHM().put("selectPerson", this.getSelectPerson());
		this.getFormHM().put("noticeperson", this.getNoticeperson());
		this.getFormHM().put("sperson", this.getSperson());
		this.getFormHM().put("chflag", this.getChflag());
		this.getFormHM().put("trainid", this.getTrainid());
		this.getFormHM().put("opt", this.getOpt());
		this.getFormHM().put("announce", this.getAnnounce());
		this.getFormHM().put("msgList", this.getMsgList());
		this.getFormHM().put("msg", this.getMsg());
		this.getFormHM().put("unitcode",this.getUnitcode());
		this.getFormHM().put("tmpnbase", this.getTmpnbase());
		
		this.getFormHM().put("typelist", this.getTypelist());
		this.getFormHM().put("thistype", this.getThistype());
		this.getFormHM().put("seltype", this.getSeltype());
		this.getFormHM().put("selparam", this.getSelparam());
		this.getFormHM().put("begintime", this.getBegintime());
		this.getFormHM().put("endtime", this.getEndtime());

		this.getFormHM().put("type", this.getType());
	}

	/**
	 * @return Returns the proposeForm.
	 */
	public PaginationForm getBoardForm() {
		return boardForm;
	}

	/**
	 * @param proposeForm
	 *            The proposeForm to set.
	 */
	public void setBoardForm(PaginationForm boardForm) {
		this.boardForm = boardForm;
	}

	/**
	 * @return Returns the Boardvo.
	 */
	public RecordVo getBoardvo() {
		return boardvo;
	}

	/**
	 * @param proposevo
	 *            The Boardvo to set.
	 */
	public void setBoardvo(RecordVo boardvo) {
		this.boardvo = boardvo;
	}

	/*
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		super.reset(arg0, arg1);
		try {	//清空form中的值
			if(this.getSelparam()!=null){
				this.setSelparam("");
				this.setThistype("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		//清空缓存
		if("/selfservice/infomanager/board/searchboard".equals(arg0.getPath())
				&&arg1.getParameter("b_query")!=null){
			if(this.getBoardForm()!=null){
	              this.getBoardForm().getPagination().firstPage();
			}
		}
		/**
		 * 新建
		 */
		userView = this.getUserView();
		if ("/selfservice/infomanager/board/searchboard".equals(arg0.getPath())
				&& arg1.getParameter("b_add") != null) {
			this.setFlag("1");
			this.getFormHM().put("flag", this.getFlag());
			this.getBoardvo().clearValues();
			//this.getBoardvo().removeValues();
		}
		/**
		 * 编辑
		 */
		else if ("/selfservice/infomanager/board/addboard".equals(
                arg0.getPath())
				&& arg1.getParameter("b_query") != null) {

			this.setFlag("0");
			this.getFormHM().put("flag", this.getFlag());
		}
		/**
		 * 答复
		 */
		else if ("/selfservice/infomanager/board/replyboard".equals(
                arg0.getPath())
				&& arg1.getParameter("b_query") != null) {

			this.setFlag("3");
			this.getFormHM().put("flag", this.getFlag());
		}
		/**
		 * 查阅
		 */
		else if ("/selfservice/infomanager/board/viewboard".equals(
                arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.setFlag("2");
			this.getFormHM().put("flag", this.getFlag());
		} else if ("selfservice/infomanager/board/searchboard".equals(
                arg0.getPath())
				&& arg1.getParameter("b_query") != null) {
			this.getFormHM().put("flag", "100");
		}
		//上传文件的处理
		if (this.getFormHM().get("flag") != null) {
			//执行插入操作
			if ("1".equals(this.getFormHM().get("flag").toString())
					&& "/selfservice/infomanager/board/addboard".equals(arg0.getPath())&& arg1.getParameter("b_save") != null)
			{
				
			
			}
			//执行更新操作
			if ("0".equals(this.getFormHM().get("flag").toString())
					&& "/selfservice/infomanager/board/addboard".equals(arg0.getPath())&& arg1.getParameter("b_save") != null) {

			}
		}
		return super.validate(arg0, arg1);
	}

    public String getFlag() {
        return flag;
    }
    
    /**
     * @param flag The flag to set.
     */
   
    public void setFlag(String flag) {
        this.flag = flag;
    }
    
    /**
     * @return Returns the approve.
     */
   
   public String getApprove()
    {
    	return this.approve;
    }
    
   /*
    * @param approve to set
    */
  
    public void setApprove(String approve)
    {
    	this.approve=approve;
    }

	public String getUsrnames() {
		return usrnames;
	}

	public void setUsrnames(String usrnames) {
		this.usrnames = usrnames;
	}

	public String getNoticeperson() {
		return noticeperson;
	}

	public void setNoticeperson(String noticeperson) {
		this.noticeperson = noticeperson;
	}

	public String getSelectPerson() {
		return selectPerson;
	}

	public void setSelectPerson(String selectPerson) {
		this.selectPerson = selectPerson;
	}

	public ArrayList getSpersonlist() {
		return spersonlist;
	}

	public void setSpersonlist(ArrayList spersonlist) {
		this.spersonlist = spersonlist;
	}

	public String getChflag() {
		return chflag;
	}

	public void setChflag(String chflag) {
		this.chflag = chflag;
	}

	public String getSperson() {
		return sperson;
	}

	public void setSperson(String sperson) {
		this.sperson = sperson;
	}

	public String getTrainid() {
		return trainid;
	}

	public void setTrainid(String trainid) {
		this.trainid = trainid;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getTmpnbase() {
		return tmpnbase;
	}

	public void setTmpnbase(String tmpnbase) {
		this.tmpnbase = tmpnbase;
	}

}

