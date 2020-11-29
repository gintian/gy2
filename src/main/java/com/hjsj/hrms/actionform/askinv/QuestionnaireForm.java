/*
 * 创建日期 2005-7-12
 *
 * 
 */
package com.hjsj.hrms.actionform.askinv;

import com.hjsj.hrms.actionform.welcome.WelcomeForm;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import com.hrms.struts.valueobject.UserView;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
/**
 * @author luangaojiong
 * 
 * 问卷调查出题Form
 */

public class QuestionnaireForm extends FrameForm {

	
	  
	private String displayContral = "0";

	//公告显示标识
	private String boardflag = "0";

	private String id = "0";

	private String name = "0";

	private String flag = "0";

	private String approve = "0";

	//调查主题
	private String topic = "";

	//调查项目
	private String item = "";

	//欢迎页面主题第一轮循环
	private ArrayList topicList = new ArrayList();

	//欢迎页面要点第二轮循环
	private ArrayList pointList = new ArrayList();

	private ArrayList checklist = new ArrayList(); //checkbox

	private String itemid = "0";

	private String topicid = "0";

	private String successmsg = "";

	private String sumNum = "0";

	private String totalNum = "0";

	private String precent = "0";

	private String precentWidth = "0";

	private String itemName = "";

	private String pointName = "";

	private String pointid = "";

	//图形第二个要点循环
	private ArrayList endviewlst = new ArrayList();

	//图形第一个项目循环 及与问卷有关的项目属性ArrayList
	private ArrayList itemwhilelst = new ArrayList();

	protected UserView userView = this.getUserView();

	/**
	 * 公告对象
	 */

	private PaginationForm questionnaireForm = new PaginationForm();

	//图片列表

	private ArrayList picList = new ArrayList();

	/**
	 * 公告对象列表
	 */
	private ArrayList list = new ArrayList();
	
	private ArrayList answerList = new ArrayList();
	private ArrayList answerDesc = new ArrayList();
	private ArrayList essayDesc = new ArrayList();
	private String state = "0";

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public ArrayList getEssayDesc() {
		return essayDesc;
	}

	public void setEssayDesc(ArrayList essayDesc) {
		this.essayDesc = essayDesc;
	}

	public ArrayList getAnswerDesc() {
		return answerDesc;
	}

	public void setAnswerDesc(ArrayList answerDesc) {
		this.answerDesc = answerDesc;
	}

	public ArrayList getAnswerList() {
		return answerList;
	}

	public void setAnswerList(ArrayList answerList) {
		this.answerList = answerList;
	}

	private String message = "";
	
	private String columnCount;
	/**是否出现关闭按钮*/
	private String isClose;
	/**区分返回地址*/
	private String enteryType;
	/**区分返回的是热点调查还是学习评估*/
	private String enteryFlag="0";
	/**版本*/
	private String home;

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

	public void setPicList(ArrayList picList) {
		this.picList = picList;

	}

	public ArrayList getPicList() {
		return this.picList;
	}

	//查阅项目第一轮循环属性
	public void setItemwhilelst(ArrayList itemwhilelst) {
		this.itemwhilelst = itemwhilelst;
	}

	public void clearMessage() {
		this.getFormHM().put("message", "");
	}

	public ArrayList getItemwhilelst() {
		return this.itemwhilelst;
	}

	//查阅要点第二轮循环属性
	public void setEndviewlst(ArrayList endviewlst) {
		this.endviewlst = endviewlst;
	}

	public ArrayList getEndviewlst() {
		return this.endviewlst;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemName() {
		return this.itemName;
	}

	public void setPointid(String pointid) {
		this.pointid = pointid;
	}

	public String getPointid() {
		return this.pointid;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getPointName() {
		return this.pointName;
	}

	//提示消息

	public void setTopicid(String topicid) {
		this.topicid = topicid;
	}

	public String getTopicid() {
		return this.topicid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getItemid() {
		return this.itemid;
	}

	//主题表名称属性
	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return this.topic;
	}

	//项目名称属性
	public void setItem(String item) {
		this.item = item;
	}

	public String getItem() {
		return this.item;
	}

	//欢迎页面要点列表第二轮循环属性
	public void setPointList(ArrayList pointList) {
		this.pointList = pointList;
	}

	public ArrayList getPointList() {
		return this.pointList;
	}

	//欢迎页面主题表第一轮循环属性

	public void setTopicList(ArrayList topicList) {
		this.topicList = topicList;
	}

	public ArrayList getTopicList() {
		return this.topicList;
	}

	/**
	 * @return Returns the outlineForm.
	 */
	public PaginationForm getQuestionnaireForm() {
		return questionnaireForm;
	}

	/**
	 * @param outlineForm
	 *            The outlineForm to set.
	 */
	public void setQuestionnaireForm(PaginationForm questionnaireForm) {
		this.questionnaireForm = questionnaireForm;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public ArrayList getList() {
		return this.list;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	//To get and set content of topic
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public void setSuccessmsg(String successmsg) {
		this.successmsg = successmsg;
	}

	public String getSuccessmsg() {
		return this.successmsg;
	}

	@Override
    public void outPutFormHM() {
        this.setIsClose((String)this.getFormHM().get("isClose"));
        this.setEnteryType((String)this.getFormHM().get("enteryType"));
        this.setHome((String)this.getFormHM().get("home"));
		this.setColumnCount((String)this.getFormHM().get("columnCount"));
		this.setId((String) this.getFormHM().get("id"));
		this.setName((String) this.getFormHM().get("name"));
		this.setItem(this.getFormHM().get("item").toString());
		this.setItemid(this.getFormHM().get("itemid").toString());
		this.setList((ArrayList) this.getFormHM().get("boardlist"));
		this.setTopic(this.getFormHM().get("topic").toString());
		this.setPointList((ArrayList) this.getFormHM().get("pointList"));
		this.setSuccessmsg(this.getFormHM().get("successmsg").toString());
		this.setId(this.getFormHM().get("topicid").toString());
		this.setEndviewlst((ArrayList) this.getFormHM().get("endviewlst"));
		this.setItemwhilelst((ArrayList) this.getFormHM().get("itemwhilelst"));
		this.setTopicList((ArrayList) this.getFormHM().get("topicList"));
		this.setMessage(this.getFormHM().get("message").toString());
		this.setAnswerList((ArrayList) this.getFormHM().get("answerList"));
		this.setAnswerDesc((ArrayList) this.getFormHM().get("answerDesc"));
		this.setEssayDesc((ArrayList) this.getFormHM().get("essayDesc"));
		this.setState((String) this.getFormHM().get("state"));
		this.setEnteryFlag((String) this.getFormHM().get("enteryFlag"));

	}

	/*
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {

		this.getFormHM().put("home", this.getHome());
		this.getFormHM().put("isClose", this.getIsClose());
		this.getFormHM().put("enteryType", this.getEnteryType());
		this.getFormHM().put("columnCount",this.getColumnCount());
		this.getFormHM().put("topic", this.getTopic());
		this.getFormHM().put("item", this.getItem());
		this.getFormHM().put("successmsg", this.getSuccessmsg());
		this.getFormHM().put("itemid", this.getItemid());
		this.setList(new ArrayList());
		this.getFormHM().put("pointList", this.getPointList());
		this.setSuccessmsg(this.getFormHM().get("successmsg").toString());
		this.getFormHM().put("topicid", this.getTopic());
		this.getFormHM().put("topicList", this.getTopicList());
		this.getFormHM().put("answerList", this.getAnswerList());
		this.getFormHM().put("answerDesc", this.getAnswerDesc());
		this.getFormHM().put("essayDesc", this.getEssayDesc());
		this.getFormHM().put("state", this.getState());

		//初始化百分比及总和等数据
		this.getFormHM().put("id", this.getId());
		this.getFormHM().put("name", this.getName());
		this.getFormHM().put("message", this.getMessage());
		
		this.getFormHM().put("enteryFlag", this.getEnteryFlag());
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
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {

		
		if("/selfservice/infomanager/askinv/questionnaire".equals(
                arg0.getPath())
		&& arg1.getParameter("b_examine") != null)
		{
			String [] itemEndViewStr=arg1.getParameterValues("itemEndView");
			ArrayList list=new ArrayList();
			for(int i=0;i<itemEndViewStr.length;i++)
			{
				list.add(itemEndViewStr[i]);
			}
			
			this.getFormHM().put("questionnarieEndView", list);
			
		}
		
		
		/**
		 * 判断提交按钮提取提交数据
		 *  
		 */
		if ("/selfservice/infomanager/askinv/questionnaire".equals(arg0.getPath())
				&& arg1.getParameter("b_save") != null) {
				
			doSelectItem(arg1);
			doDescribeText(arg1);
			doItemQuestion(arg1);
			if(arg1.getParameter("id")!=null)
			{
				//System.out.println("---->QuestionnaireForm--Validate----id--->"+id);
				//System.out.println("---->QuestionnaireForm--Validate----arg1.getParameter(id)--->"+arg1.getParameter("id"));
				this.getFormHM().put("topicid2",arg1.getParameter("id"));
			}
		
		}
		return super.validate(arg0, arg1);
	}
	/**
	 * 处理选择题
	 * @param request
	 */
	public void doSelectItem(HttpServletRequest request)
	{
		Map mp =  request.getParameterMap();
		Set sk = mp.keySet();
		HashMap hmSave = new HashMap();
		Iterator iterator = sk.iterator();
		ArrayList list=new ArrayList();
		while (iterator.hasNext()) {

			String typeKey = iterator.next().toString();
			String typeValue = mp.get(typeKey).toString();
			//System.out.println("----QuestionnaireForm-->typeKey is"+typeKey);
			//System.out.println("----QuestionnaireForm-->typeValue is"+typeValue);
			if(typeKey.length()>1  && typeKey.startsWith("¤"))
			{
				WelcomeForm wf=new WelcomeForm();
				wf.setItemid(typeKey.substring(1,typeKey.length()));
				wf.setPointList(getSelectList(request,typeKey));
				list.add(wf);
			}
	
		}

		this.getFormHM().put("SelectItemSave",list);
	}
	
	/**
	 * 得到选项集
	 * @param request
	 */
	ArrayList getSelectList(HttpServletRequest request,String paramename)
	{
		ArrayList lst=new ArrayList();
		
		String [] strlst=request.getParameterValues(paramename);
		for(int i=0;i<strlst.length;i++)
		{
			lst.add(strlst[i]);
		}
		
		return lst;
	}
	/**
	 * 处理要素描述文本
	 * @param request
	 */
	public void doDescribeText(HttpServletRequest request)
	{
		Map mp =  request.getParameterMap();
		Set sk = mp.keySet();
		HashMap hmSave = new HashMap();
		Iterator iterator = sk.iterator();
		ArrayList list=new ArrayList();
		while (iterator.hasNext()) {

			String typeKey = iterator.next().toString();
			String typeValue = mp.get(typeKey).toString();
			
			if(typeKey.length()>1  && typeKey.startsWith("☆"))
			{
				WelcomeForm wf=new WelcomeForm();
				String splitStr[]=typeKey.split("☆");
				if(splitStr.length>=3)
				{
					wf.setItemid(splitStr[1].trim());
					wf.setPointid(splitStr[2].trim());
				
				}
				
				if(request.getParameter(typeKey)==null || "".equals(request.getParameter(typeKey).trim()))
				{
					
				}
				else
				{
				
					wf.setPointContext(request.getParameter(typeKey));
					
					list.add(wf);
				}
				
			}
	
		}

		this.getFormHM().put("DescribeTextSave",list);
	}
	/**
	 * 处理项目问答题
	 * @param request
	 */
	
	public void doItemQuestion(HttpServletRequest request)
	{					Map mp =  request.getParameterMap();
		Set sk = mp.keySet();
		HashMap hmSave = new HashMap();
		Iterator iterator = sk.iterator();
		ArrayList list=new ArrayList();
		while (iterator.hasNext()) {

			String typeKey = iterator.next().toString();
			String typeValue = mp.get(typeKey).toString();
		//System.out.println("----QuestionnaireForm-->typeKey is"+typeKey);
		//System.out.println("----QuestionnaireForm-->typeValue is"+typeValue);
			if("itemid¤".equals(typeKey.substring(0,typeKey.length()>7?7:typeKey.length())))
			{
				if(!"".equals(request.getParameter(typeKey).trim()))
				{		
					
					WelcomeForm wf=new WelcomeForm();
					wf.setItemid(typeKey.substring(7,typeKey.length()));
					wf.setItemContext(request.getParameter(typeKey));
					list.add(wf);
					
				}
			
			}

		}

		this.getFormHM().put("ItemQuestionSave",list);
		
	}

	public String getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(String columnCount) {
		this.columnCount = columnCount;
	}

	public String getIsClose() {
		return isClose;
	}

	public void setIsClose(String isClose) {
		this.isClose = isClose;
	}

	public String getEnteryType() {
		return enteryType;
	}

	public void setEnteryType(String enteryType) {
		this.enteryType = enteryType;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

    public String getEnteryFlag() {
        return enteryFlag;
    }

    public void setEnteryFlag(String enteryFlag) {
        this.enteryFlag = enteryFlag;
    }

}