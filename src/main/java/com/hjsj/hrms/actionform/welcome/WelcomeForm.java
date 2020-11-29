package com.hjsj.hrms.actionform.welcome;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-2:18:19:19</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class WelcomeForm extends FrameForm {

	   private String displayContral="0";	//右框架显示
	   
	   private String ext_content = "";
	   //公告显示标识
	   private String boardflag="0";
	   private String id="0";
	   private String mdid="0";
     
	   private String name="0";
       private String description="";
       
	   private String flag="0";
       private String approve="0";
       private String viewcount;
       //调查主题
       private String topic="";
       //调查项目
       private String item="";
       //欢迎页面主题第一轮循环
       private ArrayList topicList=new ArrayList();
       private ArrayList topicList2=new ArrayList();
       private ArrayList trainEvaluateList=new ArrayList();  //培训评估列表
       
       
       
       //欢迎页面要点第二轮循环
       private ArrayList pointList=new ArrayList();
       private ArrayList checklist=new ArrayList(); //checkbox
       private String itemid="0";
       private String topicid="0";
       private String successmsg="";
       private String sumNum="0";
       private String totalNum="0";
       private String precent="0";
       private String precentWidth="0";
            
       private String itemName="";
       private String pointName="";
       private String pointid="";
       private String describestatus="0";   //要点表中的描述标识
       private String pointContext="";
       private String conextFlag="0";	//要点内描述是否为空标识：统计用
       private String multitem="";	//问卷点多名称在html页面显示
       private String itemContext="";	//问卷问答题
       private String welcome_marquee;//公告信息由滚动的效果,效果设置
       private String news="0";
       private String fillflag="";
       private String selects="";
       private String maxvalue="";
       private String minvalue="";
       private String opinion="";
       private HashMap plansMap=new HashMap();
       /**领导骄傲和或者员工考核*/
       private String performanceType;
       private ArrayList myResultList = new ArrayList();
       private ArrayList underlingResultList = new ArrayList();  
       /**进入标志=0是从原来自助平台进入，=1是从5.0页面进入*/
       private String enteryType;
       /**从5.0页面进入，只有一个热点调查，，homePageHotId为id值*/
	   private String homePageHotId;
	   private ArrayList moreList = new ArrayList();
	   private String home;//返回页面标志
	   /**=train为培训，=rese为问卷*/
	   private String discriminateFlag;
	   private String newLeaderStatus;
	   /**北京公安，指标是否填写过=1填写过，不用在填写了，=0没填写过，弹出填写页面*/
	   private String isFillInfo;
	   private ArrayList itemsList = new ArrayList();
	   //11: 培训新闻
	   private String annouceFlag;	  
	   
	   private String chartFlag="false";
	   
	/**
     * @return the chartFlag
     */
    public String getChartFlag() {
        return chartFlag;
    }
    /**
     * @param chartFlag the chartFlag to set
     */
    public void setChartFlag(String chartFlag) {
        this.chartFlag = chartFlag;
    }
    public HashMap getPlansMap() {
		return plansMap;
	}
	public void setPlansMap(HashMap plansMap) {
		this.plansMap = plansMap;
	}
	/**
	 * @return 返回 pointContext。
	 */
	public String getPointContext() {
		return pointContext;
	}
	/**
	 * @param pointContext 要设置的 pointContext。
	 */
	public void setPointContext(String pointContext) {
		this.pointContext = pointContext;
	}
       //图形第二个要点循环
       private ArrayList endviewlst=new ArrayList();
       //图形第一个项目循环 及与问卷有关的项目属性ArrayList
       private ArrayList itemwhilelst=new ArrayList();
       protected UserView userView =this.getUserView();
	    /**
	     * 公告对象
	     */
	    private RecordVo boardvo=new RecordVo("announce");
	   
	    private PaginationForm welcomeForm=new PaginationForm();     
	    
	    //图片列表
	    
	    private ArrayList picList=new ArrayList();
	    
	    /**
	     * 公告对象列表
	     */
	    private ArrayList list=new ArrayList();
	    private String userName="";
	    private String context="";
	    
	    
	    private ArrayList answerList = new ArrayList();
		public ArrayList getAnswerList() {
			return answerList;
		}
		public void setAnswerList(ArrayList answerList) {
			this.answerList = answerList;
		}
		public ArrayList getAnswerDesc() {
			return answerDesc;
		}
		public void setAnswerDesc(ArrayList answerDesc) {
			this.answerDesc = answerDesc;
		}
		public ArrayList getEssayDesc() {
			return essayDesc;
		}
		public void setEssayDesc(ArrayList essayDesc) {
			this.essayDesc = essayDesc;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		private ArrayList answerDesc = new ArrayList();
		private ArrayList essayDesc = new ArrayList();
		private String state = "0";

		
	    
		/**
		 * @return 返回 context。
		 */
   public String getContext() {
   			return context;
		}
		/**
		 * @param context 要设置的 context。
		 */
		public void setContext(String context) {
			this.context = context;
		}
	/**
	 * @return 返回 itemStatus。
	 */
	public String getItemStatus() {
		return itemStatus;
	}
	/**
	 * @param itemStatus 要设置的 itemStatus。
	 */
	public void setItemStatus(String itemStatus) {
		this.itemStatus = itemStatus;
	}
       private String itemStatus="0";		//项目表中单题、问答、多选标识
	/**
	 * @return 返回 describestatus。
	 */
	public String getDescribestatus() {
		return describestatus;
	}
	/**
	 * @param describestatus 要设置的 describestatus。
	 */
	public void setDescribestatus(String describestatus) {
		this.describestatus = describestatus;
	}
     
		/**
		 * @return 返回 userName。
		 */
		public String getUserName() {
			return userName;
		}
		/**
		 * @param userName 要设置的 userName。
		 */
		public void setUserName(String userName) {
			this.userName = userName;
		}
	    public void setDisplayContral(String displayContral)
	    {
	    	this.displayContral=displayContral;
	    }
	    
	    public String getDisplayContral()
	    {
	    	return this.displayContral;
	    }
	    
	    public void setBoardflag(String boardflag)
	    {
	    	this.boardflag=boardflag;
	    }
	    
	    public String getBoardflag()
	    {
	    	return this.boardflag;
	    }
	    
	    public void setPicList(ArrayList picList)
	    {
	    	this.picList=picList;
	    	
	    }
	    	    
	    public ArrayList getPicList()
	    {
	    	return this.picList;
	    }
	    //查阅项目第一轮循环属性
	    public void setItemwhilelst(ArrayList itemwhilelst)
        {
        	this.itemwhilelst=itemwhilelst;
        }
        
        public ArrayList getItemwhilelst()
        {
        	return this.itemwhilelst;
        }
        //查阅要点第二轮循环属性
        public void setEndviewlst(ArrayList endviewlst)
        {
        	this.endviewlst=endviewlst;
        }
        
        public ArrayList getEndviewlst()
        {
        	return this.endviewlst;
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
	    //提示消息
	    public void setSuccessmsg(String successmsg)
	    {
	    	this.successmsg=successmsg;
	    }
	    
	    public String getSuccessmsg()
	    {
	    	return this.successmsg;
	    }
	    /**
	     * 清除清息
	     * @param topicid
	     */
	    public void clearSuccessmsg()
	    {
	    	this.getFormHM().put("successmsg","");
	    }
	    
	    public void setTopicid(String topicid)
	    {
	    	this.topicid=topicid;
	    }
	    
	    public String getTopicid()
	    {
	    	return this.topicid;
	    }
	  
	    public void setItemid(String itemid)
	    {
	    	this.itemid=itemid;
	    }
	    
	    public String getItemid()
	    {
	    	return this.itemid;
	    }
	    
	    //主题表名称属性
	    public void setTopic(String topic)
	    {
	    	this.topic=topic;
	    }
	    
	    public String getTopic()
	    {
	    	return this.topic;
	    }
	    
	    //项目名称属性
	    public void setItem(String item)
	    {
	    	this.item=item;
	    }
	    
	    public String getItem()
	    {
	    	return this.item;
	    }
	    
	    //欢迎页面要点列表第二轮循环属性
	    public void setPointList(ArrayList pointList)
	    {
	    	this.pointList=pointList;
	    }
	    
	    public ArrayList getPointList()
	    {
	    	return this.pointList;
	    }
	    //欢迎页面主题表第一轮循环属性
	    
	    public void setTopicList(ArrayList topicList)
	    {
	    	this.topicList=topicList;
	    }
	    
	    public ArrayList getTopicList()
	    {
	    	return this.topicList;
	    }
	    
	    /**
	     * @return Returns the outlineForm.
	     */
	    public PaginationForm getWelcomeForm() {
	        return welcomeForm;
	    }
	    /**
	     * @param outlineForm The outlineForm to set.
	     */
	    public void setWelcomeForm(PaginationForm welcomeForm) {
	        this.welcomeForm = welcomeForm;
	    }
	    
	    
	    public void setChecklist(ArrayList checkist)
	    {
	    	this.checklist=checklist;
	    }
	    
	    public ArrayList getChecklist()
	    {
	    	return this.checklist;
	    }
	   
	    
	    public void setList(ArrayList list)
	    {
	    	this.list=list;
	    }
	    
	    public ArrayList getList()
	    {
	    	return this.list;
	    }
	    
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
	    		
		
	    @Override
        public void outPutFormHM() {
	    	this.setMdid((String)this.getFormHM().get("mdid"));
	    	this.setItemsList((ArrayList)this.getFormHM().get("itemsList"));
	    	this.setIsFillInfo((String)this.getFormHM().get("isFillInfo"));
	    	this.setNewLeaderStatus((String)this.getFormHM().get("newLeaderStatus"));
	    	this.setDiscriminateFlag((String)this.getFormHM().get("discriminateFlag"));
	    	this.setHome((String)this.getFormHM().get("home"));
	    	this.setMoreList((ArrayList)this.getFormHM().get("moreList"));
	    	this.setHomePageHotId((String)this.getFormHM().get("homePageHotId"));
	    	this.setEnteryType((String)this.getFormHM().get("enteryType"));
	    	this.setBoardflag((String)this.getFormHM().get("boardflag"));
	    	this.setId((String)this.getFormHM().get("id"));
		    this.setName((String)this.getFormHM().get("name"));
		    this.setDescription((String)this.getFormHM().get("description"));
		    this.setViewcount((String)this.getFormHM().get("viewcount"));
		    
		    //项目
		    this.setItem(this.getFormHM().get("item").toString());
		   	this.setItemid(this.getFormHM().get("itemid").toString());
	    	this.setBoardvo((RecordVo)this.getFormHM().get("boardTb"));
	    	this.getWelcomeForm().setList((ArrayList)this.getFormHM().get("boardlist"));
	        this.setList((ArrayList)this.getFormHM().get("boardlist"));
	        this.setTopic(this.getFormHM().get("topic").toString());
	        this.setPointList((ArrayList)this.getFormHM().get("pointList"));
	        this.setSuccessmsg(this.getFormHM().get("successmsg").toString());
	        this.setId(this.getFormHM().get("topicid").toString());
	        
	        //得到百分比与总和等数据
	       
	        this.setTotalNum((String)this.getFormHM().get("totalNum"));
	        this.setSumNum((String)this.getFormHM().get("sumNum"));
	        this.setPrecent((String)this.getFormHM().get("precent"));
	        this.setPrecentWidth((String)this.getFormHM().get("precentWidth"));
	        this.setEndviewlst((ArrayList)this.getFormHM().get("endviewlst"));
	        this.setItemwhilelst((ArrayList)this.getFormHM().get("itemwhilelst"));
	        this.setTopicList((ArrayList)this.getFormHM().get("topicList"));
	        this.setTopicList2((ArrayList)this.getFormHM().get("topicList2"));
	        this.setTrainEvaluateList((ArrayList)this.getFormHM().get("trainEvaluateList"));
	        
	        this.setDisplayContral(this.getFormHM().get("displayContral").toString());
	    	//this.setPicList((ArrayList)this.getFormHM().get("picList"));
	    	
	    	///System.out.println(this.getBoardvo().getString("content"));
	        String str = this.getBoardvo().getString("content");
	        str = str.replaceAll("&lt;", "<");
	        str = str.replaceAll("&gt;", ">");
	        this.setExt_content(str);
	        this.setWelcome_marquee((String)this.getFormHM().get("welcome_marquee"));
	        this.setNews((String)this.getFormHM().get("news"));
	        this.setPlansMap((HashMap)this.getFormHM().get("plansMap"));
	        this.setPerformanceType((String)this.getFormHM().get("performanceType"));
	        this.setMyResultList((ArrayList)this.getFormHM().get("myResultList"));
	        this.setUnderlingResultList((ArrayList)this.getFormHM().get("underlingResultList"));
	        //公告回复
	        this.setOpinion((String)this.getFormHM().get("opinion"));
	        
	        this.setAnnouceFlag((String)this.getFormHM().get("annouceFlag"));
	        
	        this.setAnswerList((ArrayList) this.getFormHM().get("answerList"));
			this.setAnswerDesc((ArrayList) this.getFormHM().get("answerDesc"));
			this.setEssayDesc((ArrayList) this.getFormHM().get("essayDesc"));
			this.setState((String) this.getFormHM().get("state"));
			this.setChartFlag((String) this.getFormHM().get("chartFlag"));
	    }
	    
	    

	    /* 
	     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	     */
	    @Override
        public void inPutTransHM() {
	    	this.getFormHM().put("mdid",this.getMdid());
	    	this.getFormHM().put("itemsList",this.getItemsList());
	    	this.getFormHM().put("isFillInfo", this.getIsFillInfo());
	    	this.getFormHM().put("newLeaderStatus", this.getNewLeaderStatus());
	    	this.getFormHM().put("discriminateFlag", this.getDiscriminateFlag());
	    	this.getFormHM().put("home", this.getHome());
	    	this.getFormHM().put("homePageHotId", this.getHomePageHotId());
	    	this.getFormHM().put("enteryType",this.getEnteryType());
	    	this.getFormHM().put("boardflag",this.getBoardflag());
	        this.getFormHM().put("boardov",this.getBoardvo());
	        this.getFormHM().put("boardTb",this.getBoardvo());
	        this.getFormHM().put("flag",this.getFlag()); 
	        this.getFormHM().put("topic",this.getTopic());
	        this.getFormHM().put("item",this.getItem());
	        if(this.getWelcomeForm()!=null&&this.getWelcomeForm().getSelectedList()!=null)
	        	this.getFormHM().put("selectedlist",(ArrayList)this.getWelcomeForm().getSelectedList());
	        this.getFormHM().put("successmsg",this.getSuccessmsg());
	        this.getFormHM().put("itemid",this.getItemid());
	    	this.setList(new ArrayList());
	        this.getFormHM().put("pointList",this.getPointList());
	        this.setSuccessmsg(this.getFormHM().get("successmsg").toString());
	        this.getFormHM().put("topicid",this.getTopic());
	        this.getFormHM().put("topicList",this.getTopicList());
	        	        
	        //初始化百分比及总和等数据
	        this.getFormHM().put("id",this.getId());
	        this.getFormHM().put("name",this.getName());
	        this.getFormHM().put("precent",this.getPrecent());
	        this.getFormHM().put("precentWidth",this.getPrecentWidth());
	        this.getFormHM().put("sumNum",this.getSumNum());
	        this.getFormHM().put("totalNum",this.getTotalNum());
	        this.getFormHM().put("displayContral",this.getDisplayContral());
	        
	        this.getFormHM().put("annouceFlag", this.getAnnouceFlag());
	        
	        this.getFormHM().put("answerList", this.getAnswerList());
			this.getFormHM().put("answerDesc", this.getAnswerDesc());
			this.getFormHM().put("essayDesc", this.getEssayDesc());
			this.getFormHM().put("state", this.getState());
			this.getFormHM().put("chartFlag", this.getChartFlag());
	        
	    }
	   	   
	    /**
	     * @return Returns the Boardvo.
	     */
	    public RecordVo getBoardvo() {
	        return boardvo;
	    }
	    /**
	     * @param proposevo The Boardvo to set.
	     */
	    public void setBoardvo(RecordVo boardvo) {
	        this.boardvo = boardvo;
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
	        /**
	         * 搜索
	         */
	    	userView =this.getUserView();
	        if("/selfservice/welcome/welcome".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	        	 this.setSuccessmsg("");
	       		 this.getFormHM().put("successmsg",this.getSuccessmsg());
	        	  this.setFlag("1");
	        	  this.getFormHM().put("flag",this.getFlag());
	        }
	        /**
	         * 更多搜索
	         */
	       else if("/selfservice/welcome/welcome".equals(arg0.getPath()) && arg1.getParameter("b_more")!=null)
	        {
	       		 this.setSuccessmsg("");
	       		 this.getFormHM().put("successmsg",this.getSuccessmsg());
	        	 this.setFlag("2");
	        	 this.getFormHM().put("flag",this.getFlag());
	        }
	       else if("/selfservice/welcome/welcome".equals(arg0.getPath()) && arg1.getParameter("b_return")!=null)
	       {
	       	this.setSuccessmsg("");
	        this.getFormHM().put("successmsg",this.getSuccessmsg());
	       }
	       
	       /**
	        * 得到热点调查问答题及多选题
	        */
	       getHotQuestion(arg1);
	      	        
	        return super.validate(arg0, arg1);
	    }
	    /**
	     * 得到热点调查问答题方法
	     * @param request
	     */
	    public void getHotQuestion(HttpServletRequest request)
	    
	    {
	    	/**
	    	 * 得到主题id号
	    	 * 
	    	 */
	    	if(request.getParameter("id")!=null)
	    	{
	    		this.getFormHM().put("hotid",request.getParameter("id"));
	    		/**
	    		 * 暂时处理代码
	    		 */
	    		HashMap mhtemp=new HashMap();
	    		mhtemp.put("id",request.getParameter("id"));
	    		 this.getFormHM().put("requestPamaHM",mhtemp);
	    	}
	    	else
	    	{
	    		this.getFormHM().put("hotid","0");
	    	}
	    	/**
	    	 * 得到项目id号
	    	 */
	    	if(request.getParameter("itemid")!=null)
	    	{
	    		this.getFormHM().put("hotitemid",request.getParameter("itemid"));
	    	}
	    	else
	    	{
	    		this.getFormHM().put("hotitemid","0");
	    	}
	    	/**
	    	 * 得到项目问答内容
	    	 */
	    	if(request.getParameter("hotquestion")!=null)
	    	{
	    		this.getFormHM().put("hotquestion",request.getParameter("hotquestion"));
	    	}
	    	else
	    	{
	    		this.getFormHM().put("hotquestion","");
	    	}
	    	/**
	    	 * 单选内容的处理
	    	 */
	    	doSingleSubject(request);
	    	
	    	
	    	/*********************************************************************
	    	 * 
	    	 * 以下为多选内容的处理
	    	 * 
	    	 *********************************************************************/
	    	/**
	    	 * 得到多选内容
	    	 */
	    	if(request.getParameterValues("hotmultcheck")!=null)
	    	{
	    		String []strArray=request.getParameterValues("hotmultcheck");
	    		if(strArray.length<=0)
	    		{
	    			this.getFormHM().put("hotmultchecklst",new ArrayList());
	    		}
	    		else
	    		{
	    			ArrayList list=new ArrayList();
	    			for(int i=0;i<strArray.length;i++)
	    			{
	    				//System.out.println(strArray[i]);
	    				list.add(strArray[i]);
	    			}
	    			this.getFormHM().put("hotmultchecklst",list);
	    		}
	    	}
	    	else
	    	{
	    		this.getFormHM().put("hotmultchecklst",new ArrayList());
	    	}
	    	/**
	    	 * 得到单选多选内容的文本
	    	 */
	    	Map mp = request.getParameterMap();
			Set sk = mp.keySet();
			HashMap hmSave = new HashMap();
			Iterator iterator = sk.iterator();

			while (iterator.hasNext()) {

				String typeKey = iterator.next().toString();
				String typeValue = mp.get(typeKey).toString();
				//System.out.println("------>welcomeForm-->getHotQuestion1-->"+typeKey);
				if ("point¤".equals(typeKey.substring(0,typeKey.length()>6?6:typeKey.length()))) {
				
					//System.out.println("------>welcomeForm-->getHotQuestion-->"+typeKey);
					if(!"".equals(request.getParameter(typeKey).trim()))
					{
						hmSave.put(typeKey, request.getParameter(typeKey));
					}
				}

			}
			
			if(hmSave.size()>0)
			{
				this.getFormHM().put("multTextlist", hmSave); 
			}
			else
			{
				this.getFormHM().put("multTextlist",new HashMap());
			}
			/**
			 * 得到多选内容结束
			 */
	    }
	    
	    /**
	     * 单选内的容的处理函数
	     * @param request
	     */
	    public void doSingleSubject(HttpServletRequest request)
	    {
	    	/**
	    	 * 得到单选内容
	    	 */
	    	if(request.getParameterValues("hotcheck")!=null)
	    	{
	    		String []strArray=request.getParameterValues("hotcheck");
	    		if(strArray.length<=0)
	    		{
	    			this.getFormHM().put("hotchecklst",new ArrayList());
	    		}
	    		else
	    		{
	    			ArrayList list=new ArrayList();
	    			for(int i=0;i<strArray.length;i++)
	    			{
	    				//System.out.println(strArray[i]);
	    				
	    				list.add(strArray[i]);
	    			}
	    			this.getFormHM().put("hotchecklst",list);
	    		}
	    	}
	    	else
	    	{
	    		this.getFormHM().put("hotchecklst",new ArrayList());
	    	}
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
	/**
	 * @return 返回 itemContext。
	 */
	public String getItemContext() {
		return itemContext;
	}
	/**
	 * @param itemContext 要设置的 itemContext。
	 */
	public void setItemContext(String itemContext) {
		this.itemContext = itemContext;
	}
	/**
	 * @return 返回 multitem。
	 */
	public String getMultitem() {
		return multitem;
	}
	/**
	 * @param multitem 要设置的 multitem。
	 */
	public void setMultitem(String multitem) {
		this.multitem = multitem;
	}
	@Override
    public UserView getUserView() {
		return userView;
	}
	@Override
    public void setUserView(UserView userView) {
		this.userView = userView;
	}
	public String getViewcount() {
		return viewcount;
	}
	public void setViewcount(String viewcount) {
		this.viewcount = viewcount;
	}
	public String getExt_content() {
		return ext_content;
	}
	public void setExt_content(String ext_content) {
		this.ext_content = ext_content;
	}
	public ArrayList getTopicList2() {
		return topicList2;
	}
	public void setTopicList2(ArrayList topicList2) {
		this.topicList2 = topicList2;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList getTrainEvaluateList() {
		return trainEvaluateList;
	}
	public void setTrainEvaluateList(ArrayList trainEvaluateList) {
		this.trainEvaluateList = trainEvaluateList;
	}
	public String getWelcome_marquee() {
		return welcome_marquee;
	}
	public void setWelcome_marquee(String welcome_marquee) {
		this.welcome_marquee = welcome_marquee;
	}
	public String getNews() {
		return news;
	}
	public void setNews(String news) {
		this.news = news;
	}
	public String getFillflag() {
		return fillflag;
	}
	public void setFillflag(String fillflag) {
		this.fillflag = fillflag;
	}
	public String getSelects() {
		return selects;
	}
	public void setSelects(String selects) {
		this.selects = selects;
	}
	public String getMaxvalue() {
		return maxvalue;
	}
	public void setMaxvalue(String maxvalue) {
		this.maxvalue = maxvalue;
	}
	public String getMinvalue() {
		return minvalue;
	}
	public void setMinvalue(String minvalue) {
		this.minvalue = minvalue;
	}
	public String getPerformanceType() {
		return performanceType;
	}
	public void setPerformanceType(String performanceType) {
		this.performanceType = performanceType;
	}
	public String getOpinion() {
		return opinion;
	}
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	public ArrayList getMyResultList() {
		return myResultList;
	}
	public void setMyResultList(ArrayList myResultList) {
		this.myResultList = myResultList;
	}
	public ArrayList getUnderlingResultList() {
		return underlingResultList;
	}
	public void setUnderlingResultList(ArrayList underlingResultList) {
		this.underlingResultList = underlingResultList;
	}
	public String getEnteryType() {
		return enteryType;
	}
	public void setEnteryType(String enteryType) {
		this.enteryType = enteryType;
	}
	public String getHomePageHotId() {
		return homePageHotId;
	}
	public void setHomePageHotId(String homePageHotId) {
		this.homePageHotId = homePageHotId;
	}
	public ArrayList getMoreList() {
		return moreList;
	}
	public void setMoreList(ArrayList moreList) {
		this.moreList = moreList;
	}
	public String getHome() {
		return home;
	}
	public void setHome(String home) {
		this.home = home;
	}
	public String getDiscriminateFlag() {
		return discriminateFlag;
	}
	public void setDiscriminateFlag(String discriminateFlag) {
		this.discriminateFlag = discriminateFlag;
	}
	public String getNewLeaderStatus() {
		return newLeaderStatus;
	}
	public void setNewLeaderStatus(String newLeaderStatus) {
		this.newLeaderStatus = newLeaderStatus;
	}
	public String getIsFillInfo() {
		return isFillInfo;
	}
	public void setIsFillInfo(String isFillInfo) {
		this.isFillInfo = isFillInfo;
	}
	public ArrayList getItemsList() {
		return itemsList;
	}
	public void setItemsList(ArrayList itemsList) {
		this.itemsList = itemsList;
	}
    public String getMdid() {
		return mdid;
	}
    
	public void setMdid(String mdid) {
		this.mdid = mdid;
	}
	
    public void setAnnouceFlag(String annouceFlag) {
        this.annouceFlag = annouceFlag;
    }
    
    public String getAnnouceFlag() {
        return annouceFlag;
    }
}
