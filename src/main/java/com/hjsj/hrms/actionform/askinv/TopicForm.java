/*
 * Created on 2005-5-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.askinv;

import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TopicForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
	    private DateStyle first_date=new DateStyle();
	    private String judge="0";
	    private String outName = "";
          
	    /**
	     * 建议对象
	     */
	    private RecordVo topicvo=DynamicCreatColumnVo();
	    private FormFile file; //文件路径

	    private ArrayList list = new ArrayList(); //存放问卷名称和填表说明
	    private String selectPerson;
	    private String noticeperson;
		private String chflag="";
		private String trainid;
		
		private ArrayList spersonlist = new ArrayList();
		private String sperson;
	    
		private PaginationForm msgPageForm=new PaginationForm();
	    
	    
	    
	    /**
	     * 建议对象列表
	     */
	    
	    private PaginationForm topicForm=new PaginationForm();     
	    private ArrayList msg = new ArrayList();
	  
	   
	    

		@Override
        public void outPutFormHM() {
	    	this.setSelectPerson((String)this.getFormHM().get("selectPerson"));
	    	this.setNoticeperson((String)this.getFormHM().get("noticeperson"));
	    	this.setSpersonlist((ArrayList)this.getFormHM().get("spersonlist"));
	        this.setSperson((String)this.getFormHM().get("sperson"));
	        this.setTrainid((String)this.getFormHM().get("trainid"));
	        this.setChflag((String)this.getFormHM().get("chflag"));
	        
	        this.setTopicvo((RecordVo)this.getFormHM().get("topicTb"));
	        this.getTopicForm().setList((ArrayList)this.getFormHM().get("topiclist"));
	        this.setFirst_date((DateStyle)this.getFormHM().get("first_date"));
	        this.setList((ArrayList)this.getFormHM().get("list"));
	        this.setOutName(this.getFormHM().get("outName").toString());
	        
	        this.setMsg((ArrayList)this.getFormHM().get("msg"));
	        this.getMsgPageForm().setList((ArrayList)this.getFormHM().get("msg"));
	    }

	    /* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM() {
	    	this.getFormHM().put("selectPerson", this.getSelectPerson());
	    	this.getFormHM().put("noticeperson", this.getNoticeperson());
	    	this.getFormHM().put("noticeperson", this.getNoticeperson());
			this.getFormHM().put("sperson", this.getSperson());
			this.getFormHM().put("chflag", this.getChflag());
			this.getFormHM().put("trainid", this.getTrainid());
			this.getFormHM().put("chflag",this.getChflag());
			
		    this.getFormHM().put("selectedlist",(ArrayList)this.getTopicForm().getSelectedList());
	        this.getFormHM().put("topicov",this.getTopicvo());
	        this.getFormHM().put("topicTb",this.getTopicvo());
	        this.getFormHM().put("judge",this.getJudge()); 
	        this.getFormHM().put("first_date",this.getFirst_date());
	        this.getFormHM().put("file", file);
	                
	        this.getFormHM().put("outName", this.getOutName());
	    }

	    
	    public RecordVo DynamicCreatColumnVo()
	    {
	    	RecordVo a_topicvo=new RecordVo("investigate");
	    	if(!a_topicvo.hasAttribute("description"))
	    	{
	    		Connection con=null;
	    		try {			
	    			con = (Connection) AdminDb.getConnection();
		    		DbWizard dbWizard=new DbWizard(con);
		    		DBMetaModel dbmodel=new DBMetaModel(con);
		    		
		    		Table table=new Table("investigate");
		    		Field obj=new Field("description","description");
		    		obj.setDatatype(DataType.CLOB);
					obj.setKeyable(false);			
					obj.setVisible(false);
					obj.setAlign("left");	
					table.addField(obj);
					dbWizard.addColumns(table);
					dbmodel.reloadTableModel("investigate");
					a_topicvo=new RecordVo("investigate");
	    		}
	    		catch(Exception e)
	    		{
	    			e.printStackTrace();
	    		}
	    		finally
	    		{
	    			try{
						if (con != null) {
							con.close();
						}
	    			}
	    			catch(Exception ee)
	    			{
	    				ee.printStackTrace();
	    			}
	    		}
	    	}
	    	
	    	
	    	return a_topicvo;
	    }
	    
	    /**
	     * @return Returns the TopicForm.
	     */
	    public PaginationForm getTopicForm() {
	        return topicForm;
	    }
	    /**
	     * @param TopicForm The TopicForm to set.
	     */
	    public void setTopicForm(PaginationForm topicForm) {
	        this.topicForm = topicForm;
	    }
	    /**
	     * @return Returns the Topicvo.
	     */
	    public RecordVo getTopicvo() {
	        return topicvo;
	    }
	    /**
	     * @param topicvo The Topicvo to set.
	     */
	    public void setTopicvo(RecordVo topicvo) {
	        this.topicvo = topicvo;
	    }
	    /* 
	     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	     */
	    @Override
        public void reset(ActionMapping arg0, HttpServletRequest arg1) {
	        super.reset(arg0, arg1);
	    }
	    /* 
	     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	     */
	    @Override
        public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    	
	    	if(arg1.getParameter("operate")!=null&& "init".equals(arg1.getParameter("operate")))
			{
				if(this.getTopicForm()!=null)
					this.getTopicForm().getPagination().firstPage();
			}
	    	
	        /**
	         * 新建
	         */
	        if("/selfservice/infomanager/askinv/searchtopic".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
	        {
	        	
	        	  this.setJudge("1");
	        	  //重置操作
	        	  this.setFirst_date(new DateStyle());
	        	 
	        	  this.getTopicvo().clearValues();
	        	  this.getTopicvo().setString("flag","1");
	        	  this.getTopicvo().setString("status","0");
	        	  this.getTopicvo().setString("days","30");
	        }
	        /**
	         * 编辑
	         */
	        if("/selfservice/infomanager/askinv/addtopic".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	        	
	        	 this.setJudge("0");
	        }
	        /**结束调查*/
	        if("/selfservice/infomanager/askinv/addtopic".equals(arg0.getPath()) && arg1.getParameter("b_end")!=null)
	        {
	        	this.getTopicvo().clearValues();
	        	this.setJudge("2");
	        }
	        return super.validate(arg0, arg1);
	    }
	  
	    public String getJudge() {
	        return judge;
	    }
	    
	    /**
	     * @param flag The flag to set.
	     */
	   
	    public void setJudge(String judge) {
	        this.judge = judge;
	    }
	    
	    public DateStyle getFirst_date()
	    {
	      return first_date;
	    }
	   
	    public void setFirst_date(DateStyle first_date)
	    {
	      this.first_date = first_date;
	    }

		public FormFile getFile() {
			return file;
		}

		public void setFile(FormFile file) {
			this.file = file;
		}

		public ArrayList getList() {
			return list;
		}

		public void setList(ArrayList list) {
			this.list = list;
		}

		public String getSelectPerson() {
			return selectPerson;
		}

		public void setSelectPerson(String selectPerson) {
			this.selectPerson = selectPerson;
		}

		public String getNoticeperson() {
			return noticeperson;
		}

		public void setNoticeperson(String noticeperson) {
			this.noticeperson = noticeperson;
		}

		public String getChflag() {
			return chflag;
		}

		public void setChflag(String chflag) {
			this.chflag = chflag;
		}

		public String getTrainid() {
			return trainid;
		}

		public void setTrainid(String trainid) {
			this.trainid = trainid;
		}

		public ArrayList getSpersonlist() {
			return spersonlist;
		}

		public void setSpersonlist(ArrayList spersonlist) {
			this.spersonlist = spersonlist;
		}

		public String getSperson() {
			return sperson;
		}

		public void setSperson(String sperson) {
			this.sperson = sperson;
		}

		public String getOutName() {
			return outName;
		}

		public void setOutName(String outName) {
			this.outName = outName;
		}

		public PaginationForm getMsgPageForm() {
			return msgPageForm;
		}

		public void setMsgPageForm(PaginationForm msgPageForm) {
			this.msgPageForm = msgPageForm;
		}

	    public ArrayList getMsg() {
			return msg;
		}

		public void setMsg(ArrayList msg) {
			this.msg = msg;
		}





}
