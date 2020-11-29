package com.hjsj.hrms.actionform.general.sprelationmap;

import com.hjsj.hrms.businessobject.general.sprelationmap.ChartParameterCofig;
import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.constant.SystemConfig;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
public class RelationMapForm extends FrameForm{
	
	/**审批关系图种类，=1人员审批关系，=2职位审批关系，=3用户（业务）审批关系*/
	private String relationType;
	/**画图面板的宽度，屏幕的宽度，不是实际所占的宽度*/
    private String clientWidth;
    /**画图面板的高度，屏幕的高度，不是实际所占的高度*/
	private String clientHeight;
	/**画图面板的宽度，实际宽度*/
	private String trueWidth;
	/**画图面板的高度，实际高度*/
	private String trueHeight;
	
	
	
	
	private String a_code;

	private HashMap dataMap = new HashMap();
	
	private String xmlData;
	
	private String printXmlData;//打印预览页面用xml文件
	
	private HashMap printDataMap = new HashMap();// print browse use
	
	
	private String opter;
	
	private String currentNodeId;

	private String imagesPath="";
	private CardTagParamView cardparam=new CardTagParamView();
    
	/**展示登记表参数*/
	private String nbase;
	private String objid;
	private String tabid;
	private ChartParameterCofig chartParam;
	
	private ArrayList fieldSetList = new ArrayList();
	
	private ArrayList fieldList = new ArrayList();
	private ArrayList selectedFieldList = new ArrayList();

	private String fieldsetid;
	private String itemid;
	private String right_fields;
	
	/**汇报关系维护页面*/
	
	private ArrayList upPersonList = new  ArrayList();
	private String upId;
	private String currId;
	private ArrayList spRelationList = new ArrayList();
	private String spRelationId;
	
	private ArrayList downPersonList = new ArrayList();
	private String downId;

	
	private String menuConstantTop;
	private String menuConstantLeft;
	
	private String fontStyle;
	private String infokind;
    private String nodeType;//展开节点，是当前节点的父节点，还是孩子节点。
    /**
     * 2013-02-25  田野添加审批人的部门、单位、职位拼接信息和姓名信息
     *	形如：部门名/编码
     */
	String department ;//部门拼接信息
	String unit;//单位拼接信息
	String position ;//职位拼接信息
	String name ;//姓名
	
	HashMap reportData = new HashMap();
	
	//
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("nodeType", this.getNodeType());
		this.getFormHM().put("fontStyle",this.getFontStyle());
		this.getFormHM().put("menuConstantTop", this.getMenuConstantTop());
		this.getFormHM().put("menuConstantLeft", this.getMenuConstantLeft());
		this.getFormHM().put("upId",this.getUpId());
		this.getFormHM().put("currId", this.getCurrId());
		this.getFormHM().put("trueWidth",this.getTrueWidth());
		this.getFormHM().put("trueHeight",this.getTrueHeight());
		this.getFormHM().put("relationType",this.getRelationType());
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("xmlData", this.getXmlData());
		this.getFormHM().put("opter", this.getOpter());
		this.getFormHM().put("currentNodeId", this.getCurrentNodeId());
		this.getFormHM().put("imagesPath",this.getImagesPath());
		this.getFormHM().put("clientHeight", this.getClientHeight());
		this.getFormHM().put("clientWidth", this.getClientWidth());
		this.getFormHM().put("nbase",this.getNbase());
		this.getFormHM().put("objid",this.getObjid());
		this.getFormHM().put("tabid", this.getTabid());
		this.getFormHM().put("chartParam", this.getChartParam());
		this.getFormHM().put("printXmlData", this.getPrintXmlData());
		this.getFormHM().put("department", this.getDepartment());
		this.getFormHM().put("unit", this.getUnit());
		this.getFormHM().put("position", this.getPosition());
		this.getFormHM().put("name", this.getName());
	}

	@Override
    public void outPutFormHM() {
		this.setNodeType((String)this.getFormHM().get("nodeType"));
		this.setPrintDataMap((HashMap)this.getFormHM().get("printDataMap"));
		this.setInfokind((String)this.getFormHM().get("infokind"));
		this.setFontStyle((String)this.getFormHM().get("fontStyle"));
		this.setMenuConstantTop((String)this.getFormHM().get("menuConstantTop"));
		this.setMenuConstantLeft((String)this.getFormHM().get("menuConstantLeft"));
		this.setUpId((String)this.getFormHM().get("upId"));
		this.setCurrId((String)this.getFormHM().get("currId"));
		this.setUpPersonList((ArrayList)this.getFormHM().get("upPersonList"));
		this.setSpRelationList((ArrayList)this.getFormHM().get("spRelationList"));
		this.setDownPersonList((ArrayList)this.getFormHM().get("downPersonList"));
		this.setTrueHeight((String)this.getFormHM().get("trueHeight"));
		this.setTrueWidth((String)this.getFormHM().get("trueWidth"));
		this.setSelectedFieldList((ArrayList)this.getFormHM().get("selectedFieldList"));
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setFieldSetList((ArrayList)this.getFormHM().get("fieldSetList"));
		this.setChartParam((ChartParameterCofig)this.getFormHM().get("chartParam"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setObjid((String)this.getFormHM().get("objid"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setRelationType((String)this.getFormHM().get("relationType"));
		this.setClientHeight((String)this.getFormHM().get("clientHeight"));
		this.setClientWidth((String)this.getFormHM().get("clientWidth"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setDataMap((HashMap)this.getFormHM().get("dataMap"));
		this.setXmlData((String)this.getFormHM().get("xmlData"));
		this.setOpter((String)this.getFormHM().get("opter"));
		this.setCurrentNodeId((String)this.getFormHM().get("currentNodeId"));
		this.setUnit((String)this.getFormHM().get("unit"));
		this.setDepartment((String)this.getFormHM().get("department"));
		this.setName((String)this.getFormHM().get("name"));
		this.setPosition((String)this.getFormHM().get("position"));
		this.setReportData((HashMap)this.getFormHM().get("reportData"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		try{
			this.getFormHM().put("session",arg1.getSession());
		   String path = arg1.getSession().getServletContext().getRealPath("/general/sprelationmap/images");
		   if("weblogic".equals(SystemConfig.getPropertyValue("webserver")))
		   {
		  	  path=arg1.getSession().getServletContext().getResource("/general/sprelationmap/relation_map_drawable.jsp").getPath();//.substring(0);
			  	  //System.out.println("path:>>>>>>>>>>>>>>>>>>>>>>"+path);
			  	 
//		      if(path.indexOf(':')!=-1)
//		  	  {
//				 path=path.substring(1);   
//		   	  }
//		  	  else
//		   	  {
//				 path=path.substring(0);      
//		   	  }
//		      int nlen=path.length();
//		  	  StringBuffer buf=new StringBuffer();
//		   	  buf.append(path);
//		  	  buf.setLength(nlen-1);
//		   	  path=buf.toString();
			  	  
			  path = path.substring(1);
			  path = path.replace("relation_map_drawable.jsp", "images");
		   }
		   path=SafeCode.encode(path);
		   if("/general/sprelationmap/relation_map_drawable".equals(arg0.getPath())&&arg1.getParameter("b_card")!=null)
	        {
	        	cardparam.setPageid(0);
	        }
		   this.setImagesPath(path);
		   
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.validate(arg0, arg1);
	}
	public ChartParameterCofig getChartParam() {
		return chartParam;
	}

	public void setChartParam(ChartParameterCofig chartParam) {
		this.chartParam = chartParam;
	}
	public CardTagParamView getCardparam() {
		return cardparam;
	}

	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}

	public String getImagesPath() {
		return imagesPath;
	}

	public void setImagesPath(String imagesPath) {
		this.imagesPath = imagesPath;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}
		public String getA_code() {
			return a_code;
		}

		public void setA_code(String a_code) {
			this.a_code = a_code;
		}
		public HashMap getDataMap() {
			return dataMap;
		}

		public void setDataMap(HashMap dataMap) {
			this.dataMap = dataMap;
		}
		public String getXmlData() {
			return xmlData;
		}

		public void setXmlData(String xmlData) {
			this.xmlData = xmlData;
		}

		public String getOpter() {
			return opter;
		}

		public void setOpter(String opter) {
			this.opter = opter;
		}
		public String getCurrentNodeId() {
			return currentNodeId;
		}

		public void setCurrentNodeId(String currentNodeId) {
			this.currentNodeId = currentNodeId;
		}
		public String getNbase() {
			return nbase;
		}

		public void setNbase(String nbase) {
			this.nbase = nbase;
		}

		public String getObjid() {
			return objid;
		}

		public void setObjid(String objid) {
			this.objid = objid;
		}

		public String getTabid() {
			return tabid;
		}

		public void setTabid(String tabid) {
			this.tabid = tabid;
		}
		public ArrayList getFieldSetList() {
			return fieldSetList;
		}

		public void setFieldSetList(ArrayList fieldSetList) {
			this.fieldSetList = fieldSetList;
		}

		public ArrayList getFieldList() {
			return fieldList;
		}

		public void setFieldList(ArrayList fieldList) {
			this.fieldList = fieldList;
		}

		public ArrayList getSelectedFieldList() {
			return selectedFieldList;
		}

		public void setSelectedFieldList(ArrayList selectedFieldList) {
			this.selectedFieldList = selectedFieldList;
		}

		public String getFieldsetid() {
			return fieldsetid;
		}

		public void setFieldsetid(String fieldsetid) {
			this.fieldsetid = fieldsetid;
		}

		public String getItemid() {
			return itemid;
		}

		public void setItemid(String itemid) {
			this.itemid = itemid;
		}

		public String getRight_fields() {
			return right_fields;
		}

		public void setRight_fields(String right_fields) {
			this.right_fields = right_fields;
		}

		public String getClientWidth() {
			return clientWidth;
		}

		public void setClientWidth(String clientWidth) {
			this.clientWidth = clientWidth;
		}

		public String getClientHeight() {
			return clientHeight;
		}

		public void setClientHeight(String clientHeight) {
			this.clientHeight = clientHeight;
		}

		public String getTrueWidth() {
			return trueWidth;
		}

		public void setTrueWidth(String trueWidth) {
			this.trueWidth = trueWidth;
		}

		public String getTrueHeight() {
			return trueHeight;
		}

		public void setTrueHeight(String trueHeight) {
			this.trueHeight = trueHeight;
		}
		public ArrayList getUpPersonList() {
			return upPersonList;
		}

		public void setUpPersonList(ArrayList upPersonList) {
			this.upPersonList = upPersonList;
		}

		public String getCurrId() {
			return currId;
		}

		public void setCurrId(String currId) {
			this.currId = currId;
		}

		public ArrayList getSpRelationList() {
			return spRelationList;
		}

		public void setSpRelationList(ArrayList spRelationList) {
			this.spRelationList = spRelationList;
		}

		public ArrayList getDownPersonList() {
			return downPersonList;
		}

		public void setDownPersonList(ArrayList downPersonList) {
			this.downPersonList = downPersonList;
		}
		public String getUpId() {
			return upId;
		}

		public void setUpId(String upId) {
			this.upId = upId;
		}

		public String getDownId() {
			return downId;
		}

		public void setDownId(String downId) {
			this.downId = downId;
		}
		public String getSpRelationId() {
			return spRelationId;
		}

		public void setSpRelationId(String spRelationId) {
			this.spRelationId = spRelationId;
		}
		public String getMenuConstantTop() {
			return menuConstantTop;
		}

		public void setMenuConstantTop(String menuConstantTop) {
			this.menuConstantTop = menuConstantTop;
		}

		public String getMenuConstantLeft() {
			return menuConstantLeft;
		}

		public void setMenuConstantLeft(String menuConstantLeft) {
			this.menuConstantLeft = menuConstantLeft;
		}
		public String getInfokind() {
			return infokind;
		}

		public void setInfokind(String infokind) {
			this.infokind = infokind;
		}

		public String getFontStyle() {
			return fontStyle;
		}

		public void setFontStyle(String fontStyle) {
			this.fontStyle = fontStyle;
		}

		public String getPrintXmlData() {
			return printXmlData;
		}

		public void setPrintXmlData(String printXmlData) {
			this.printXmlData = printXmlData;
		}
		public HashMap getPrintDataMap() {
			return printDataMap;
		}

		public void setPrintDataMap(HashMap printDataMap) {
			this.printDataMap = printDataMap;
		}
		public String getNodeType() {
			return nodeType;
		}

		public void setNodeType(String nodeType) {
			this.nodeType = nodeType;
		}

		public String getDepartment() {
			return department;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getPosition() {
			return position;
		}

		public void setPosition(String position) {
			this.position = position;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}


		public HashMap getReportData() {
			return reportData;
		}

		public void setReportData(HashMap reportData) {
			this.reportData = reportData;
		}

		
		
}
