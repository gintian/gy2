package com.hjsj.hrms.actionform.train;

import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-13:10:37:16</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class InfoPickForm extends FrameForm {

	
	    private DateStyle first_date=new DateStyle();
	    private String timeLength="0";
	    private String timeDecimalwidth="0";
	    private String manTxtLength="4";
	    private String manTxtDecimalwidth="0";
	    private ArrayList infoAddList=new ArrayList();
	    private ArrayList infoDetailAddList=new ArrayList();
	    
		/**
		 * @return 返回 infoDetailAddList。
		 */
		public ArrayList getInfoDetailAddList() {
			return infoDetailAddList;
		}
		/**
		 * @param infoDetailAddList 要设置的 infoDetailAddList。
		 */
		public void setInfoDetailAddList(ArrayList infoDetailAddList) {
			this.infoDetailAddList = infoDetailAddList;
		}
		/**
		 * @return 返回 infoAddList。
		 */
		public ArrayList getInfoAddList() {
			return infoAddList;
			
		}
		/**
		 * @param infoAddList 要设置的 infoAddList。
		 */
		public void setInfoAddList(ArrayList infoAddList) {
			this.infoAddList = infoAddList;
		}
	    /**
	     * 第一次新增标识
	     * 
	     */
	    private String firstFlag="0";
	    /**
		 * 新建及编辑功能控制
		 */
        private String judge="0";
        private String investigate="0";
        /**
        /*添加需求明细时需求采集表传的主表id
        */
        private String r19id="0";       
        
        /**
        /*修改需求明细需求采集表传的主表id
        */
        private String newr91id="0";
        
	    /**
	     * 需求调查表对象
	     */
	    private RecordVo infoPickDetailvo=new RecordVo("R22");
	    private ArrayList pickInfolst=new ArrayList();
	    private ArrayList pickInfoDetaillst=new ArrayList();
	    private PaginationForm infoPickForm=new PaginationForm();
	    private PaginationForm infoPickDetailForm=new PaginationForm();
	    private String pickTableName="";		//采集表名称
	    private String factNum="";			//实有人数
	    private String infoId="0";
	    private ArrayList dynamicCol=new ArrayList();	//动态列名ArrayList
	    private ArrayList dynamicColDetail=new ArrayList(); //明细
		/**
		 * @return 返回 dynamicColDetail。
		 */
		public ArrayList getDynamicColDetail() {
			return dynamicColDetail;
		}
		/**
		 * @param dynamicColDetail 要设置的 dynamicColDetail。
		 */
		public void setDynamicColDetail(ArrayList dynamicColDetail) {
			this.dynamicColDetail = dynamicColDetail;
		}
		/**
		 * @return 返回 dynamicCol。
		 */
		public ArrayList getDynamicCol() {
			return dynamicCol;
		}
		/**
		 * @param dynamicCol 要设置的 dynamicCol。
		 */
		public void setDynamicCol(ArrayList dynamicCol) {
			this.dynamicCol = dynamicCol;
		}
		/**
		 * @return 返回 factNum。
		 */
		public String getFactNum() {
			return factNum;
		}
		/**
		 * @param factNum 要设置的 factNum。
		 */
		public void setFactNum(String factNum) {
			this.factNum = factNum;
		}
		/**
		 * @return 返回 pickTableName。
		 */
		public String getPickTableName() {
			return pickTableName;
		}
		/**
		 * @param pickTableName 要设置的 pickTableName。
		 */
		public void setPickTableName(String pickTableName) {
			this.pickTableName = pickTableName;
		}
	    public void setFirstFlag(String firstFlag)
	    {
	    	this.firstFlag=firstFlag;
	    }
	    
	    public String getFirstFlag()
	    {
	    	return this.firstFlag;
	    }
	    /**
	     * 需求调查表id属性
	     */
	    public String getR19id()
	    {
	    	return this.r19id;
	    }
	    
	    public void setR19id(String r19id)
	    {
	    	this.r19id=r19id;
	    }
	    /**
	     * 采集表id
	     */
	    public String getInvestigate()
	    {
	    	return this.investigate;
	    }
	    
	    public void setInvestigate(String investigate)
	    {
	    	this.investigate=investigate;
	    }
	    
	    @Override
        public void outPutFormHM() {
	        this.setInfoPickDetailvo((RecordVo)this.getFormHM().get("infoPickDetailTb"));
	        this.getInfoPickForm().setList((ArrayList)this.getFormHM().get("infoPicklist"));
	     //   System.out.println((ArrayList)this.getFormHM().get("infoPicklist"));
	        this.setFirst_date((DateStyle)this.getFormHM().get("first_date"));
	        this.setPickInfolst((ArrayList)this.getFormHM().get("pickInfolst"));
	        this.setPickInfoDetaillst((ArrayList)this.getFormHM().get("pickInfoDetaillst"));
	        this.setInvestigate(this.getFormHM().get("investigate").toString());
	        this.setR19id(this.getFormHM().get("r19id").toString());
	        this.getInfoPickDetailForm().setList((ArrayList)this.getFormHM().get("pickInfoDetaillst"));
	        this.setNewr91id(this.getFormHM().get("newr19id").toString());
	        this.setFirstFlag(this.getFormHM().get("firstFlag").toString());
	        this.setPickTableName(this.getFormHM().get("pickTableName").toString());
	        this.setFactNum(this.getFormHM().get("factNum").toString());
	        this.setInfoId(this.getFormHM().get("infoId").toString());
	        this.setTimeLength(this.getFormHM().get("timeLength").toString());
	        this.setTimeDecimalwidth(this.getFormHM().get("timeDecimalwidth").toString());
	        this.setManTxtLength(this.getFormHM().get("manTxtLength").toString());
	        this.setManTxtDecimalwidth(this.getFormHM().get("manTxtDecimalwidth").toString());
	        this.setDynamicCol((ArrayList)this.getFormHM().get("dynamicCol"));
	        this.setInfoAddList((ArrayList)this.getFormHM().get("infoAddList"));
	        this.setInfoDetailAddList((ArrayList)this.getFormHM().get("infoDetailAddList"));
	        this.setDynamicColDetail((ArrayList)this.getFormHM().get("dynamicColDetail"));
	        
	       // System.out.println("------->infoPickForm-outPutFormHM-->");
	        
	    }

	    /* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM() {
		    this.getFormHM().put("selectedlist",(ArrayList)this.getInfoPickForm().getSelectedList());
		    this.getFormHM().put("slt",(ArrayList)this.getInfoPickDetailForm().getSelectedList());
		    this.getFormHM().put("infoPickDetailov",this.getInfoPickDetailvo());
	        this.getFormHM().put("infoPickDetailTb",this.getInfoPickDetailvo());
	        this.getFormHM().put("judge",this.getJudge()); 
	        this.getFormHM().put("first_date",this.getFirst_date());
	        this.getFormHM().put("pickInfolst",this.getPickInfolst());
	        this.getFormHM().put("pickInfoDetaillst",this.getPickInfoDetaillst());
	        this.getFormHM().put("investigate",this.getInvestigate());
	        this.getFormHM().put("r19id",this.getR19id());
	        this.getFormHM().put("newr19id",this.getNewr91id());
	        this.getFormHM().put("firstFlag",this.getFirstFlag());
	        this.getFormHM().put("pickTableName",this.getPickTableName());
	        this.getFormHM().put("factNum",this.getFactNum());
	        this.getFormHM().put("infoId",this.getInfoId());
	        this.getFormHM().put("timeLength",this.getTimeLength());
	        this.getFormHM().put("timeDecimalwidth",this.getTimeDecimalwidth());
	        this.getFormHM().put("manTxtLength",this.getManTxtLength());
	        this.getFormHM().put("manTxtDecimalwidth",this.getManTxtDecimalwidth());
	        this.getFormHM().put("dynamicCol",this.getDynamicCol());
	        this.getFormHM().put("infoAddList",this.getInfoAddList());
	        this.getFormHM().put("infoDetailAddList",this.getInfoDetailAddList());
	        this.getFormHM().put("dynamicColDetail",this.getDynamicColDetail());
	            
	        
	    }
	    
	    public String getNewr91id()
	    {
	    	return this.newr91id;
	    }
	    
	    public void setNewr91id(String newr91id)
	    {
	      this.newr91id=newr91id;	
	    }
	    public ArrayList getPickInfolst()
	    {
	    	return this.pickInfolst;
	    }
	    
	    public void setPickInfolst(ArrayList pickInfolst)
	    {
	    	this.pickInfolst=pickInfolst;
	    }
	    
	    public ArrayList getPickInfoDetaillst()
	    {
	    	return this.pickInfoDetaillst;
	    }
	    /**
	     * 需求详细信息ArrayList属性
	     */
	    public void setPickInfoDetaillst(ArrayList pickInfoDetaillst)
	    {
	    	this.pickInfoDetaillst=pickInfoDetaillst;
	    }
	    /**
	     * @return Returns the infoPickForm.
	     */
	    public PaginationForm getInfoPickForm() {
	        return infoPickForm;
	    }
	    /**
	     * @param infoPickForm The infoPickForm to set.
	     */
	    public void setInfoPickForm(PaginationForm infoPickForm) {
	        this.infoPickForm = infoPickForm;
	    }
	    
	    
	    public PaginationForm getInfoPickDetailForm() {
	        return infoPickDetailForm;
	    }
	    /**
	     * @param infoPickForm The infoPickForm to set.
	     */
	    public void setInfoPickDetailForm(PaginationForm infoPickDetailForm) {
	        this.infoPickDetailForm = infoPickDetailForm;
	    }
	    /**
	     * @return Returns the infoPickDetailvo.
	     */
	    public RecordVo getInfoPickDetailvo() {
	        return infoPickDetailvo;
	    }
	    /**
	     * @param infoPickDetailvo The infoPickDetailvo to set.
	     */
	    public void setInfoPickDetailvo(RecordVo infoPickDetailvo) {
	        this.infoPickDetailvo = infoPickDetailvo;
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
	          
	    	//System.out.println("----InfoPickForm-->path-->"+arg0.getPath());
	    	//System.out.println("----InfoPickForm-->parameter-->"+arg1.getParameterNames());
	    	
	    	 /**
	         * 新建
	         */
	        if("/selfservice/educate/infopick/infopicksearch".equals(arg0.getPath()) && arg1.getParameter("b_add")!=null)
	        {
	        	  this.setFirstFlag("1");
	        	 
	        	  this.setR19id("0");
	        	  this.setJudge("1");
	        	  //重置操作
	        	  this.setFirst_date(new DateStyle());
	        	 
	        	  this.getInfoPickDetailvo().clearValues();
	        	  this.getPickInfoDetaillst().clear();
	        	  this.getFormHM().put("r19id","0");
	        	  this.getFormHM().put("newr19id","0");
	        	  this.getFormHM().put("pickTableName","");
	        	  this.getFormHM().put("factNum","");
	        	   	  
	        	// System.out.println("----->com.hjsj.hrms.actionform.infopick-->infoPick-->add"+getInfoPickDetailvo());
	        }
	        else
	        {
	        	this.setFirstFlag("0");
	        }
	        
	       
	        if("/selfservice/educate/infopick/addinfopickdetail".equals(arg0.getPath()) && arg1.getParameter("b_returnmain")!=null)
	        {
	        	 this.setFirst_date(new DateStyle());
	        	 
	             this.getInfoPickDetailvo().clearValues();
	             this.setFirstFlag("1");
	        	
	        	 this.setR19id("0");
	        	 this.getPickInfoDetaillst().clear();	        	 
	        		 
	            // System.out.println("----->com.hjsj.hrms.actionform.infopick-->infoPick-->returnmain");
	        }
	        
	        /**
	         * 修改明细叶面返回
	         */
	        if("/selfservice/educate/infopick/addinfopickdetail".equals(arg0.getPath()) && arg1.getParameter("b_return")!=null)
	        {
	        	
	        	  this.setJudge("1");
	        	  //重置操作
	        	  this.setFirst_date(new DateStyle());
	        	 
	        	  this.getInfoPickDetailvo().clearValues();
	        	     	  
	        	  
	        }
	               
	        /**
	         * 详细信息查看
	         */
	        if("/selfservice/educate/infopick/viewinfopick".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {	        		        	
	        	this.setJudge("2");
	        }
	        /**
	         * 需求明细添加
	         */
	        if("/selfservice/educate/infopick/addinfopickdetail".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	        	this.setJudge("3");
	        	this.setFirst_date(new DateStyle());
	        	this.getInfoPickDetailvo().clearValues();
	        	
	        }
	        if("/selfservice/educate/infopick/addinfopickdetail".equals(arg0.getPath()) && arg1.getParameter("b_save")!=null)
	        {
	        	this.setJudge("3");
	        	
	        }
	        /**
	         * 修改需求明细
	         */
	        
	        if("/selfservice/educate/infopick/addinfopickdetail".equals(arg0.getPath()) && arg1.getParameter("b_modify")!=null)
	        {
	        	this.setJudge("4");
	        	 
	         } 
	        if("/selfservice/educate/infopick/addinfopickdetail".equals(arg0.getPath()) && arg1.getParameter("b_savemodify")!=null)
	        {
	        	this.setJudge("4");
	        	 
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
	   
	     
	  

		/**
		 * @return 返回 infoId。
		 */
		public String getInfoId() {
			return infoId;
		}
		/**
		 * @param infoId 要设置的 infoId。
		 */
		public void setInfoId(String infoId) {
			this.infoId = infoId;
		}
		/**
		 * @return 返回 manTxtDecimalwidth。
		 */
		public String getManTxtDecimalwidth() {
			return manTxtDecimalwidth;
		}
		/**
		 * @param manTxtDecimalwidth 要设置的 manTxtDecimalwidth。
		 */
		public void setManTxtDecimalwidth(String manTxtDecimalwidth) {
			this.manTxtDecimalwidth = manTxtDecimalwidth;
		}
		/**
		 * @return 返回 manTxtLength。
		 */
		public String getManTxtLength() {
			return manTxtLength;
		}
		/**
		 * @param manTxtLength 要设置的 manTxtLength。
		 */
		public void setManTxtLength(String manTxtLength) {
			this.manTxtLength = manTxtLength;
		}
		/**
		 * @return 返回 timeDecimalwidth。
		 */
		public String getTimeDecimalwidth() {
			return timeDecimalwidth;
		}
		/**
		 * @param timeDecimalwidth 要设置的 timeDecimalwidth。
		 */
		public void setTimeDecimalwidth(String timeDecimalwidth) {
			this.timeDecimalwidth = timeDecimalwidth;
		}
		/**
		 * @return 返回 timeLength。
		 */
		public String getTimeLength() {
			return timeLength;
		}
		/**
		 * @param timeLength 要设置的 timeLength。
		 */
		public void setTimeLength(String timeLength) {
			this.timeLength = timeLength;
		}
}
