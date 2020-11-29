/*
 * Created on 2005-6-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.askinv;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EndViewForm extends FrameForm {

	/**
	 * 新建及编辑功能控制
	 */
        private String flag="0";
        
        private String id="0";
        private String name="0";
        private String sumNum="0";
        private String totalNum="0";
        private String precent="0";
        private String precentWidth="0";
        private String f="0";
        
		/**
		 * @return 返回 f。
		 */
		public String getF() {
			return f;
		}
		/**
		 * @param f 要设置的 f。
		 */
		public void setF(String f) {
			this.f = f;
		}
        //图片列表
	    
	    private ArrayList picList=new ArrayList();
	    private String itemid="";
        private String itemName="";
        private String pointName="";
        private String pointid="";
        //第二个要点循环
        private ArrayList endviewlst=new ArrayList();
        //第一个项目循环
        private ArrayList itemwhilelst=new ArrayList();
        private String conextFlag="0";	//要点内描述是否为空标识：统计用
        private ArrayList itemtxtlist=new ArrayList();
        /**必填项标志*/
        private String fillflag;
        
        private HashMap itemMap = new HashMap();
        
        
	/**
         * @return the itemMap
         */
        public HashMap getItemMap() {
            return itemMap;
        }
        /**
         * @param itemMap the itemMap to set
         */
        public void setItemMap(HashMap itemMap) {
            this.itemMap = itemMap;
        }
    /**
	 * @return 返回 flag。
	 */
	public String getFlag() {
		return flag;
	}
	/**
	 * @param flag 要设置的 flag。
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}
		/**
		 * @return 返回 conextFlag。
		 */
		public String getConextFlag() {
			return conextFlag;
		}
		/**
		 * @param conextFlag 要设置的 conextFlag。
		 */
		public void setConextFlag(String conextFlag) {
			this.conextFlag = conextFlag;
		}
        public void setPicList(ArrayList picList)
	    {
	    	this.picList=picList;
	    	
	    }
	    
	    
	    public ArrayList getPicList()
	    {
	    	return this.picList;
	    }
	    
        public void setItemwhilelst(ArrayList itemwhilelst)
        {
        	this.itemwhilelst=itemwhilelst;
        }
        
        public ArrayList getItemwhilelst()
        {
        	return this.itemwhilelst;
        }
        
        public void setEndviewlst(ArrayList endviewlst)
        {
        	this.endviewlst=endviewlst;
        }
        
        public ArrayList getEndviewlst()
        {
        	return this.endviewlst;
        }
        
        public void setItemid(String itemid)
        {
        	this.itemid=itemid;
        }
        
        public String getItemid()
        {
        	return this.itemid;
        }
        
        public void setItemName(String itemName)
        {
        	this.itemName=itemName;
        }
        
        public String getItemName()
        {
        	return this.itemName;
        }
        
        public void setPointid(String pointid)
        {
        	this.pointid=pointid;
        }
        public String getPointid()
        {
        	return this.pointid;
        }
        
        
        public void setPointName(String pointName)
        {
        	this.pointName=pointName;
        }
        
        public String getPointName()
        {
        	return this.pointName;
        }
        
        
       
	    
	    
	    private PaginationForm endViewForm=new PaginationForm();     
	    
	    private ArrayList chartList = new ArrayList();
	    private ArrayList allList = new ArrayList();
	  
	    @Override
        public void outPutFormHM() {
	      
	    	this.setAllList((ArrayList)this.getFormHM().get("allList"));
	    	this.setChartList((ArrayList)this.getFormHM().get("chartList"));
	        this.setId((String)this.getFormHM().get("id"));
	        this.setName((String)this.getFormHM().get("name"));
	        
	        this.setTotalNum((String)this.getFormHM().get("totalNum"));
	        this.setSumNum((String)this.getFormHM().get("sumNum"));
	        
	        this.setPrecent((String)this.getFormHM().get("precent"));
	      
	        this.setPrecentWidth((String)this.getFormHM().get("precentWidth"));
	        this.setEndviewlst((ArrayList)this.getFormHM().get("endviewlst"));
	        this.setItemwhilelst((ArrayList)this.getFormHM().get("itemwhilelst"));
	        this.setItemtxtlist((ArrayList)this.getFormHM().get("itemtxtlist"));
	        this.setF(this.getFormHM().get("f").toString());
	        this.setItemMap((HashMap)this.getFormHM().get("itemMap"));
	        
	    }

	    /* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM()
	    {
	    	
		
	      this.getFormHM().put("allList", this.getAllList());
	        this.getFormHM().put("id",this.getId());
	        this.getFormHM().put("name",this.getName());
	        
	      
	        this.getFormHM().put("precent",this.getPrecent());
	        this.getFormHM().put("precentWidth",this.getPrecentWidth());
	        this.getFormHM().put("itemwhilelst",this.getItemwhilelst());
	        this.getFormHM().put("sumNum",this.getSumNum());
	        this.getFormHM().put("totalNum",this.getTotalNum());
	        this.getFormHM().put("f",this.getF());
	        this.getFormHM().put("itemMap", this.getItemMap());
	    }
	    
	    //图例长度
	    public void setPrecentWidth(String precentWidth)
	    {
	    	this.precentWidth=precentWidth;
	    }
	    
	    public String getPrecentWidth()
	    {
	    	return this.precentWidth;
	    }
	    
	    //百分比
	    public void setPrecent(String precent)
	    {
	    	this.precent=precent;
	    }
	    
	    public String getPrecent()
	    {
	    	return this.precent;
	    }
	    
	    //项目要点数量
	    public void setSumNum(String sumNum)
	    {
	    	this.sumNum=sumNum;
	    }
	    
	    public String getSumNum()
	    {
	    	return this.sumNum;
	    }
	    //所有项目要点数量
	    public void setTotalNum(String totalNum)
	    {
	    	this.totalNum=totalNum;
	    }
	    
	    public String getTotalNum()
	    {
	    	return this.totalNum;
	    }

	    /**
	     * @return Returns the proposeForm.
	     */
	  
	    public PaginationForm getEndViewForm() {
	        return endViewForm;
	    }
	   
	    
	    /**
	     * @param proposeForm The proposeForm to set.
	     */
	  
	    public void setEndViewForm(PaginationForm endViewForm) {
	        this.endViewForm = endViewForm;
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
	        
	      
	       
	        return super.validate(arg0, arg1);
	    }
	  
	  
	    
	    /**
	     * @return Returns the approve.
	     */
	   
	  
	    public  String getId()
	    {
	    	return this.id;
	    }
	    
	    public void setId(String id)
	    {
	    	this.id=id;
	    }
	    
	    //To get and set content of topic 
	    public String getName()
	    {
	    	return this.name;
	    }
	    
	    public void setName(String name)
	    {
	    	this.name=name;
	    	
	    }


		/**
		 * @return 返回 itemtxtlist。
		 */
		public ArrayList getItemtxtlist() {
			return itemtxtlist;
		}
		/**
		 * @param itemtxtlist 要设置的 itemtxtlist。
		 */
		public void setItemtxtlist(ArrayList itemtxtlist) {
			this.itemtxtlist = itemtxtlist;
		}
		public String getFillflag() {
			return fillflag;
		}
		public void setFillflag(String fillflag) {
			this.fillflag = fillflag;
		}
		public ArrayList getChartList() {
			return chartList;
		}
		public void setChartList(ArrayList chartList) {
			this.chartList = chartList;
		}
		public ArrayList getAllList() {
			return allList;
		}
		public void setAllList(ArrayList allList) {
			this.allList = allList;
		}
}
