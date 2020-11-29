package com.hjsj.hrms.businessobject.gz.piecerate;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/** 计件薪资报表定义类
 * @author wangjh
 * 2013-3-26
 */
public class PieceReportDefBo {
	private Connection conn = null;
	// 报表ID
	private int reportId=-1;
	
	// 报表名称
	private String reportName="";
	
	// 作业类别
	private String reportKind="";
	//顺序
	private String reportSortId="";
	
	// 显示指标列表，逗号分隔 
	private String showFields="";
	
	// 分组指标列表，逗号分隔 
	private String groupFields="";
	
	// 是否分组
	private boolean isGroup=false;
	
	// 排序指标
	private String orderFields="";//有顺序
	
	// 统计指标及方式
	HashMap summaryMap = new HashMap();
	String summaryFlds="";
	
	// 统计条件
	private String condClause="";

	// xml内容
	private String xmlContent="";
	// 保存最近的错误信息
	private String lastError="";
	
	//
    

	public PieceReportDefBo(Connection _con) {
		conn = _con;
	}
	
	public PieceReportDefBo(Connection _con, int _reportId) {
		conn = _con;
		this.setReportId(_reportId);
		if (_reportId>0)
			loadReport(_reportId);	
		
	}
	
	private boolean loadReport(int _reportId){
		boolean bResult=false;
		try {
			if (_reportId<1) return bResult;		
			ContentDAO dao = new ContentDAO(conn);
		
			StringBuffer buffer = new StringBuffer("");
			buffer.append("select * from hr_summarydef where defid = "+ String.valueOf(_reportId));
			RowSet rs = dao.search(buffer.toString());			
			try {
				if (rs.next()) {
					this.reportId = rs.getInt("defid");					
					this.reportName =rs.getString("defname").replaceAll("'", "").replaceAll("\"", "");
					this.reportKind =rs.getString("Busiid");
					this.condClause =rs.getString("cond");
					this.reportSortId =rs.getString("sortid");
					this.xmlContent=Sql_switcher.readMemo(rs, "content");
					loadXml();
	
				}
			} catch (Exception e) {
				throw e;
			} finally {
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bResult;
	}

	
	// 清空属性数据
	private void clear() {
		reportId = -1;
		reportName = "";
		reportKind = "";
		isGroup = false;
		showFields = "";
		groupFields = "";
		orderFields="";
		summaryFlds="";
		if (summaryMap!=null)
			summaryMap.clear();
	}
	
	public String GetMaxSortId(ContentDAO dao){
		String strResult="1";
		String sqlstr = "select max(Sortid) as sortid from hr_summarydef ";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				if(dynabean.get("sortid")!=null&&dynabean.get("sortid").toString().trim().length()>0){
					strResult = Integer.parseInt(dynabean.get("sortid").toString())+1+"";
				}

			}
		} catch(GeneralException e) {
			e.printStackTrace();
		}		
		return strResult;
	}	
	
	// 创建新报表
	public boolean newReport() {
		boolean bResult=false;
		lastError = "";
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer buffer = new StringBuffer();
		try {
			String defId="";
			String sortId=GetMaxSortId(dao);
			
			try {
				defId = new IDFactoryBean().getId("hr_summarydef.defId", "", conn);				
			} catch (Exception e) {
				lastError = "";//ResourceFactory.getProperty("gz.budget.execrate.createindexfail");  //"生成实发索引失败！";
				throw new Exception(lastError);
			}
			try {
				buffer.append("insert into hr_summarydef(defId,ModuleCode,BusiId,defName,SortId,Content,Cond,UseFlag) ");
				buffer.append(" values(");
				buffer.append(defId);
				buffer.append(",'SAL_JJ'");
				buffer.append(",'"+this.reportKind+"'");
				buffer.append(",'"+this.reportName+"'");
				buffer.append(","+sortId);
				buffer.append(",'"+getXmlContent()+"'");
				buffer.append(",'"+condClause+"'");
				buffer.append(",1");
				buffer.append(")");
		
				dao.update(buffer.toString());
				this.reportId = Integer.parseInt(defId);				
				this.reportSortId = sortId;
				bResult=true;
			} catch (Exception e) {
				lastError="新增报表失败!";
				throw e;				
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bResult;
		
	}
	
	// 保存入库
	public boolean saveReport(){
		boolean bResult=false;
		lastError = "";
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer buffer = new StringBuffer();
		try {
			try {
				buffer.append("update hr_summarydef ");
				buffer.append(" set ");
				buffer.append(" BusiId=");
				buffer.append("'"+this.reportKind+"'");
				buffer.append(",defName=");
				buffer.append("'"+this.reportName+"'");
				buffer.append(",Cond=");
				buffer.append("'"+condClause+"'");
				buffer.append(",Content=");
				buffer.append("'"+getXmlContent()+"'");				
				buffer.append(" where defId=");
				buffer.append(this.reportId);		
				dao.update(buffer.toString());
					bResult=true;
			} catch (Exception e) {
				lastError="更新报表失败!";
				throw e;				
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bResult;
	}
	
	
	// 加载xml
	private void loadXml(){
		try {
			String StrValue=this.xmlContent;
	    	if (StrValue == null || StrValue.trim().length()<=4){
                 return;
	    	} 
	    	else {
	    		Document doc = PubFunc.generateDom(StrValue);
	    		String xpath = "//param";
	    		XPath xpath_ = XPath.newInstance(xpath);
	    		Element ele = (Element) xpath_.selectSingleNode(doc);
	    		if (ele != null)
	    		{
	    			if (ele.getChild("menus") !=null){
	    				this.showFields=ele.getChild("menus").getTextTrim(); 	 
	    			}
	    			if (ele.getChild("group") !=null){
						String groupvalue = ele.getChild("group").getAttributeValue("isgroup");
						if ("1".equals(groupvalue)){
							  this.isGroup=true;
						}
						else {
							  this.isGroup=false;  
						}
						this.groupFields = ele.getChild("group").getAttributeValue("groupmenu")==null?"":ele.getChild("group").getAttributeValue("groupmenu");	    			  
						
						List lst= (List)ele.getChild("group").getChildren("summarytype");
						for (int i=0;i<lst.size();i++){
							  Element summaryEle  =(Element)lst.get(i);
							  summaryMap.put(summaryEle.getAttributeValue("menu"),summaryEle.getAttributeValue("type")); 
							  summaryFlds= summaryFlds+","+summaryEle.getAttributeValue("menu")+":"+summaryEle.getAttributeValue("type");
						}
						if (summaryFlds.length()>0) summaryFlds=summaryFlds.substring(1);
	    		    			  		  
	    		  }
	    		  if (ele.getChild("order") !=null){
	    			  this.orderFields = ele.getChild("order").getTextTrim();	
/*	    			  String[] arrValue= this.orderFields.split(",");	    			  
						for (int i=0;i<arrValue.length;i++){
							String[] arrValue1= arrValue[i].split(":");
							if (arrValue1.length>1){
								orderMap.put(arrValue1[0],arrValue1[1]); 	
							}
							  
						}*/	  	    			  
	    		  } 			
	    		}	    		
	     	}	    		

	  	 } catch(Exception e) {
				e.printStackTrace();
		   }	
		
	}
	private void saveXml(){
		Element ele =null;
		try {
			Element root = null;		
			root = new Element("param");
			
			ele = new Element("menus");
			ele.setText(this.showFields);
			root.addContent(ele);
			
			ele = new Element("group");
			if (this.isGroup){				
				ele.setAttribute("isgroup", "1");
			}
			else {
				ele.setAttribute("isgroup", "0");
			}
			ele.setAttribute("groupmenu", this.groupFields);
			
		    String[] arrValue= summaryFlds.split(",");		  
			for (int i=0;i<arrValue.length;i++){
				String[] arrValue1= arrValue[i].split(":");
				if (arrValue1.length>1){					
					Element summaryEle = new Element("summarytype");
					summaryEle.setAttribute("menu",arrValue1[0]);
					summaryEle.setAttribute("type",arrValue1[1]);
					ele.addContent(summaryEle);		
				}
				  
			}
			root.addContent(ele);
			
/*			String orderValue="";
			if (orderMap !=null){				
				iter = orderMap.entrySet().iterator();
				
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					orderValue= orderValue+","+(String)key+":"+(String)val;
				}
				if (orderValue.length()>0)
					orderValue=orderValue.substring(1);	
			}*/
			
			ele = new Element("order");
			ele.setText(this.orderFields);
			root.addContent(ele);      		

			Document myDocument = new Document(root);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			this.xmlContent = outputter.outputString(myDocument);			
		
		
		} catch(Exception e) {
			e.printStackTrace();
		}	

	}
	
	public String getXmlContent(){
		saveXml();
		return this.xmlContent;
	}
	
	/**   
	 * @Title: getReplaceedFlds   
	 * @Description: 将S04表的中s0401 s0402 分别替换为s0203,s0303,为了前台显示名称   
	 * @param @param oriFlds
	 * @param @return 
	 * @return String 
	 * @author:wangrd   
	 * @throws   
	*/
	public String getReplaceedFlds(String oriFlds){
	    String strFlds=oriFlds.toUpperCase();
        if ((","+strFlds+",").indexOf("S0203")<0){
            strFlds=strFlds.replace("S0401", "S0203");
        }
        else {
            strFlds=strFlds.replace(",S0401", "");  
            strFlds=strFlds.replace("S0401,", "");  
        }
        
        if ((","+strFlds+",").indexOf("S0303")<0){
            strFlds=strFlds.replace("S0402", "S0303");
        }
        else {
            strFlds=strFlds.replace(",S0402", "");  
            strFlds=strFlds.replace("S0402,", "");  
        }

        return strFlds;
        
    }
	
	public String getSql_FirstShowFields(){
		String showFlds= this.showFields;
		showFlds=getReplaceedFlds(showFlds);
		//showFlds=showFlds.toLowerCase().replace("s0401", "S0203");
		//showFlds=showFlds.toLowerCase().replace("s0402", "S0303");
		return showFlds;
		
	}
	
	
	public String getSql_OrderFields(){
		String ordFlds="";
		String[] arrValue= this.orderFields.split(",");	  
		for (int i=0;i<arrValue.length;i++){
			String[] arrValue1= arrValue[i].split(":");
			if (arrValue1.length>1){
			    if ("s0401".equalsIgnoreCase(arrValue1[0])){
			        
			    }
				ordFlds=ordFlds+","+arrValue1[0]+" "+ arrValue1[1];		
			}
		  
	    }
		if (ordFlds.length()>0){
			
			ordFlds= ordFlds.substring(1);
			ordFlds=" order by "+ ordFlds;
		}
		//ordFlds=getReplaceedFlds(ordFlds);
		ordFlds=ordFlds.toLowerCase().replace("s0401", "S0203");
		ordFlds=ordFlds.toLowerCase().replace("s0402", "S0303");
		return ordFlds;

	}
	
	public String getSql_GroupFields(){
		String grpFlds="";
		if (this.isGroup){
			grpFlds=" group by "+ this.groupFields;			
		}	
		grpFlds=getReplaceedFlds(grpFlds);
		//grpFlds=grpFlds.toLowerCase().replace("s0401", "S0203");
		//grpFlds=grpFlds.toLowerCase().replace("s0402", "S0303");
		return grpFlds;
		
	}
	
	public String getSql_ShowFields(){
		String showFlds="";
		
		String grpFlds= ","+this.groupFields.toUpperCase()+",";
		grpFlds=getReplaceedFlds(grpFlds);
		String fields=getReplaceedFlds(this.showFields);
		String[] arrStr= fields.split(",");
		for (int i=0;i<arrStr.length;i++){
			String orginalfld=arrStr[i];
			
			String fld=orginalfld;
			if ("S0401".equalsIgnoreCase(orginalfld)){
				fld= "S0203";
			}
			if ("S0402".equalsIgnoreCase(orginalfld)){
				fld= "S0303";
			}
				
			FieldItem fldItem=getLocalFieldItem(fld);
			String cfld=fld;
			if (fldItem!=null){					
				cfld=fldItem.getFieldsetid()+"."+fldItem.getItemid();
				if (!this.isGroup) {					
					showFlds=showFlds+","+cfld	;
				}
				else{
					if (grpFlds.indexOf(","+orginalfld.toUpperCase()+",")>-1){
						showFlds=showFlds+","+cfld	;
					}					
					else {
						String value= (String)this.summaryMap.get(orginalfld);
						if (value==null){
						    value="count";
						}
						if (("s0203".equalsIgnoreCase(fld)||("S0303".equalsIgnoreCase(fld)))){
							if ((value!=null)&&("sum".equalsIgnoreCase(value)||("avg".equalsIgnoreCase(value)))){
								value="count";	
							}
						}
						if ((value!=null)&&(!"".equals(value))){
							if ("count".equals(value)){
							    if ("D".equalsIgnoreCase(fldItem.getItemtype())){							        
							        showFlds=showFlds+","+value+"("+Sql_switcher.isnull(cfld, 
							                Sql_switcher.dateValue("2014-02-14"))+") as "+ fld;		
							    }
							    else {
							        showFlds=showFlds+","+value+"("+Sql_switcher.isnull(cfld, "'1'")+") as "+ fld;		
							        
							    }
								
							}
							else {								
								showFlds=showFlds+","+value+"("+cfld+") as "+ fld;		
							}
						}
					}
				}			
			}
		}	
		if (showFlds.length()>0)
			showFlds= showFlds.substring(1);		
	
		return showFlds;
	}
	
	
	
	// 设置汇总方式
	/**
	 * @param fld
	 * @param type： 值范围：avg|sum|max|min|count
	 * @return
	 */
	public boolean setSummaryType(String fld, String type){
		return false;
		
	}

	// 设置排序方式
	/**
	 * @param fld
	 * @param type： 值范围：asc|desc，或""， 设置为""时，去掉排序指标
	 * @return
	 */
	public boolean setOrderType(String fld, String type){
		return false;
		
	}
	
	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportKind() {
		if (reportKind==null)
			return "";
		else 
			return reportKind;
	}

	public void setReportKind(String reportKind) {
		this.reportKind = reportKind;
	}

	public String getShowFields() {
		return showFields;
	}

	public void setShowFields(String showFields) {
		this.showFields = showFields;
	}

	public String getGroupFields() {
		return groupFields;
	}

	public void setGroupFields(String groupFields) {
		this.groupFields = groupFields;
	}

	public boolean isGroup() {
		return isGroup;
	}

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	public String getCondClause() {
		return condClause;
	}

	public void setCondClause(String condClause) {
		this.condClause = condClause;
	}

	public int getReportId() {
		return reportId;
	}



	public HashMap getSummaryMap() {
		if (summaryMap == null)
			summaryMap = new HashMap();
		return summaryMap;
	}

	public String getLastError() {
		return lastError;
	}
	
	public static ArrayList getSetList(){
		return getSetList(false); 
	}
	
	public static ArrayList getSetList(boolean addBlankLine){
		ArrayList fieldList = new ArrayList();
		CommonData dataobj=null;		
		if (addBlankLine){
			dataobj = new CommonData("","");
			fieldList.add(dataobj);	
		}
		dataobj = new CommonData("S01","计件作业单");
		fieldList.add(dataobj);
		dataobj = new CommonData("S04","作业明细表");
		fieldList.add(dataobj);
		dataobj = new CommonData("S05","作业人员信息表");
		fieldList.add(dataobj);	
		dataobj = new CommonData("S02","产品信息表");
		fieldList.add(dataobj);
		dataobj = new CommonData("S03","产品工序信息表");
		fieldList.add(dataobj);
		return fieldList;
	}
	
	public ArrayList getCondSetList(boolean addBlankLine){
		ArrayList fieldList = new ArrayList();
		CommonData dataobj=null;		
		if (addBlankLine){
			dataobj = new CommonData("","");
			fieldList.add(dataobj);	
		}
		dataobj = new CommonData("S01:计件作业单","计件作业单");
		fieldList.add(dataobj);
		dataobj = new CommonData("S04:作业明细表","作业明细表");
		fieldList.add(dataobj);
		dataobj = new CommonData("S05:作业人员信息表","作业人员信息表");
		fieldList.add(dataobj);	
		dataobj = new CommonData("S02:产品信息表","产品信息表");
		fieldList.add(dataobj);
		dataobj = new CommonData("S03:产品工序信息表","产品工序信息表");
		fieldList.add(dataobj);
		return fieldList;
	}
	
	public ArrayList getLeftFieldList(String setid)  {	
		
		return getLeftFieldList(setid,false);
		
	}
	
	public ArrayList getLeftFieldList(String setid,boolean addBlankLine)  {
		ArrayList list=getDispFieldList(setid);
        if(list == null)  return null;
        FieldItem fldItem = null;
        ArrayList Displist = new ArrayList();
		if (addBlankLine){
			Displist.add( new CommonData("",""));	
		}
        for(int i = 0; i < list.size(); i++)  {
        	fldItem = (FieldItem)list.get(i); 
        	if ("M".equals(fldItem.getItemtype())) continue;
            Displist.add(new CommonData(fldItem.getItemid(),fldItem.getItemdesc()));
        
        }
        return Displist;
    }
	public ArrayList getCondFieldList(String setid,boolean addBlankLine)  {
		ArrayList list=getDispFieldList(setid);
		if(list == null)  return null;
		FieldItem fldItem = null;
		ArrayList Displist = new ArrayList();
		if (addBlankLine){
			Displist.add( new CommonData("",""));	
		}
		for(int i = 0; i < list.size(); i++)  {
			fldItem = (FieldItem)list.get(i); 
			if ("nbase".equalsIgnoreCase(fldItem.getItemid())){continue;}
			Displist.add(new CommonData(fldItem.getItemid()+":"+fldItem.getItemdesc(),fldItem.getItemdesc()));
			
		}
		return Displist;
	}
	
	public ArrayList getDispFieldList(String setid)  {
		ArrayList list=DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
		if(list == null)  return null;
		FieldItem fldItem = null;
		ArrayList Displist = new ArrayList();
		String exceptStr="";
		if ("S01".equals(setid)){
			exceptStr=",S0100,USERFLAG,CURR_USER,APPUSER,APPPROCESS,";
		}
		else if ("S02".equals(setid)){
			exceptStr=",S0100,S0200";
		}
		else if ("S03".equals(setid)){
			exceptStr=",S0100,S0300,S0311";
		}
		else if ("S04".equals(setid)){
			exceptStr=",S0100,I9999";
		}
		else if ("S05".equals(setid)){
			exceptStr="S0100,I9999,A0100";
		}
		exceptStr=","+exceptStr.toUpperCase()+",";
		
		for(int i = 0; i < list.size(); i++){
			fldItem = (FieldItem)list.get(i);
			if (exceptStr.indexOf(","+fldItem.getItemid().toUpperCase()+",")>-1) continue;
			if (!fldItem.isVisible()) continue;
					Displist.add(fldItem);
		}
		return Displist;
	}
	
	public ArrayList getAllFiledList(){
		ArrayList resultList = new ArrayList();
		ArrayList setList=getSetList();
		for (int i=0;i<setList.size();i++){
			String setid =(String)((CommonData)setList.get(i)).getDataValue();			
			ArrayList list=getDispFieldList(setid);

			if (list.size()>0){
				for(int j = 0; j < list.size(); j++){
					FieldItem fldItem = (FieldItem)list.get(j);		
					resultList.add(fldItem);
				}	
			}
			
		}
		return resultList;

	}
	
	public ArrayList getSelectedFiledList(String selectFlds){
		ArrayList resultList = new ArrayList();
		String[] arrFlds= selectFlds.split(",");
		for (int i=0;i<arrFlds.length;i++){
			String itemid =arrFlds[i];
			if ("".equals(itemid.trim())) continue;
			FieldItem fldItem= getLocalFieldItem(itemid);
			if (fldItem!=null){
				resultList.add(new CommonData(fldItem.getItemid(),fldItem.getItemdesc()));					
			}
		}

		return resultList;
	}
	
	public boolean CheckTjWhere(UserView userView,String cond){
		boolean bResult=false;	
		ArrayList fieldList = this.getAllFiledList();

		YksjParser yp = new YksjParser(userView, fieldList, YksjParser.forSearch, YksjParser.LOGIC
				, YksjParser.forPerson,"Ht", "");			
		yp.setCon(this.conn);
		try{
			bResult = yp.Verify_where(cond.trim());
		}catch (Exception e) {
			e.printStackTrace();
			bResult = false;
		}
		if (!bResult) {// 校验通过
			lastError = yp.getStrError();
		} 
		
		return bResult;
	}	
	
	public static FieldItem getLocalFieldItem(String itemid){
		ArrayList allList = new ArrayList();
		FieldItem resultFldItem=null;
		ArrayList setList=getSetList();
		for (int i=0;i<setList.size();i++){
			String setid =(String)((CommonData)setList.get(i)).getDataValue();			
			ArrayList list=DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);

			if (list.size()>0){
				for(int j = 0; j < list.size(); j++){
					FieldItem fldItem = (FieldItem)list.get(j);		
					allList.add(fldItem);
				}	
			}
		}
		
		for (int i=0;i<allList.size();i++){
			FieldItem fldItem = (FieldItem)allList.get(i);		
			if (fldItem.getItemid().equalsIgnoreCase(itemid)){
				resultFldItem= fldItem;
				break;				
			}		
			
		}
		return resultFldItem;
	}	
	
	public String getOrderFields() {
		return orderFields;
	}



	public void setSummaryMap(HashMap summaryMap) {
		this.summaryMap = summaryMap;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public String getSummaryFlds() {
		return summaryFlds;
	}

	public void setOrderFields(String orderFields) {
		this.orderFields = orderFields;
	}

	public void setSummaryFlds(String summaryFlds) {
		this.summaryFlds = summaryFlds;
	}

	public String getReportSortId() {
		return reportSortId;
	}
	

}
