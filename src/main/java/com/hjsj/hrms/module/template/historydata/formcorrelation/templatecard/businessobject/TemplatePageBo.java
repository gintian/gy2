/**
 * 
 */
package com.hjsj.hrms.module.template.historydata.formcorrelation.templatecard.businessobject;

import com.hjsj.hrms.businessobject.general.template.TTitle;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplatePage;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateSet;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:TemplatePageBo.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-12-18 上午10:13:22</p>
 * <p>@version: 7.0</p>
 */
public class TemplatePageBo {
	private static int PixelInInch = 96;    
    private Connection conn=null;
    private UserView userView = null;
	private int tabId=-1;
	private int pageId=-1;
	private String task_id="";//批量审批时，会有多个id
    private String selfApply="0";// =1 自助用户申请
    private String basePre="";
    private String a0100="";
    private String b0110="";
	private String e01a1="";
	private String objectId="";//当前查看的人员
	private String curTaskId="";//当前查看的人员的流程号
    /** 区分报审、报备、加签 
     * 1：报审 2：加签  3 报备 
    */
    private String approveFlag="0";     
    /**签章处于元素中第几个*/
    private int signnumber=0;
    private ArrayList pageCellList=null;//当前模板页所有单元格 不要直接调用this.
    private TemplateParam paramBo;
    private TemplateUtilBo utilBo;  
    private String display_e0122="0";
    private int firstPageNo = -1;//第一个插入页码得页签
    private ArrayList firstPageTitleList = null;//第一个插入页码得页签中所有页码属性集合
    private String noShowPageNo = "";//设置得不显示得页
    private String archive_id = "";

	/**
     * @param conn
     * @param tabid
     * @param pageid
     * @return 
     */
    public TemplatePageBo(Connection conn,UserView userview,int tabid, int pageid,String archive_id) {
        this.conn = conn;
        this.userView = userview;
        this.tabId = tabid;
        this.pageId = pageid;
        this.archive_id = archive_id;
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
			display_e0122="0";
        this.paramBo=new TemplateParam(this.conn,this.userView,tabid,archive_id);
		utilBo = new TemplateUtilBo(conn, userview);
    }
    
    public TemplatePageBo(Connection conn,UserView userview,TemplateParam paramBo, int pageid,String task_id) {
        this.conn = conn;
        this.userView = userview;
        this.pageId = pageid;
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
			display_e0122="0";
        this.paramBo=paramBo;
        this.tabId = paramBo.getTabId();
        this.archive_id = paramBo.getArchive_id();
        this.task_id = task_id;
        utilBo = new TemplateUtilBo(conn, userview);
    }
    
   /** 
	* @Title: getAllCell 
	* @Description: 
	* @param @return
	* @return ArrayList
	*/ 
    public ArrayList getAllCell() { 
		if (pageCellList==null)
			pageCellList = utilBo.getArchiveCell(tabId, archive_id, pageId);
	    return pageCellList;
	}  	

 	/**
 	 * @Title: getPageHtml
 	 * @Description: 获取前台显示的Html框架，不带具体单元格值
 	 * @param @return
 	 * @param @throws Exception
 	 * @return String
 	 */
	public String getPageHtml() throws Exception {
		/** 输出的HMTL内容 */
		StringBuffer strhtml = new StringBuffer();
		try {
			ArrayList pageList = this.utilBo.getAllArchiveTemplatePage(tabId, archive_id, pageId);
			TemplatePage pageParam = (TemplatePage) pageList.get(0);
			/** 纸张背景 */
			Element div = new Element("div");
			div.setAttribute("class", "pageStyle");
			int direct = this.paramBo.getTable_vo().getInt("paperori");
			if (pageParam.getPaperOrientation() !=0){
				direct= pageParam.getPaperOrientation();
			}
			int width = 0;
			int height = 0;
			if (direct == 1) {
				width = this.paramBo.getTable_vo().getInt("paperw");
				height = this.paramBo.getTable_vo().getInt("paperh");
			} else {
				width = this.paramBo.getTable_vo().getInt("paperh");
				height = this.paramBo.getTable_vo().getInt("paperw");
			}
			int wpx = Math.round((float) (width / 25.4 * PixelInInch));
			int hpx = Math.round((float) (height / 25.4 * PixelInInch));
			StringBuffer style = new StringBuffer();
			style.append("left:4px;top:5px;width:"); // 定位4

			style.append(wpx);
			style.append("px");
			style.append(";height:");
			style.append(hpx);
			style.append("px");
			style.append(";position:absolute");
			div.setAttribute("style", style.toString());

			StringBuffer divlist = new StringBuffer();
			divlist.append("<div id=\"emplist\" style=\"");

			String value = "140";
			if (this.paramBo.getInfor_type() != 1)
				value = "180";
			divlist.append("left:4px;top:5px;width:" + value + "px;"); // 定位4
			divlist.append("height:100%");
			divlist.append(";");
			divlist.append("overflow: auto;");
			divlist.append("top: expression(this.offsetParent.scrollTop+5);");
			divlist.append("position:relative\">");
			divlist.append("</div>");
			/** 标题 */
			ArrayList titlelist = getAllTitle("0");
			if (titlelist.size() > 0)
				createTitleElement(titlelist, div);
			else {
				if(this.firstPageNo!=-1&&this.pageId>this.firstPageNo) {
		        	this.firstPageTitleList = this.getAllTitle("1");
		        	int currentPage = pageId;
		        	for(int j=0;j<this.firstPageTitleList.size();j++) {
		        		TTitle title = (TTitle) this.firstPageTitleList.get(j);
		        		title.setPageid(currentPage);
					    title.setRtop(hpx-30);
		        		title.createTitleView(div, this.userView);
		        	}
		        }
			}
			/** 输出单元格 */
			ArrayList celllist = getAllCell();
			if (celllist.size() > 0)
				createAllCellElement(celllist, div);

			/** 输出超文标志 */
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setExpandEmptyElements(true);// must

			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String htmlview = outputter.outputString(div);

			// 还原附件iframe
			htmlview = htmlview.replaceAll("&gt;&lt;iframe", "><iframe");
			htmlview = htmlview.replaceAll("&gt;&lt;/iframe&gt;", "></iframe>");
			htmlview = htmlview.replaceAll("&lt;div id='attachmentid",
					"<div id='attachmentid");
			htmlview = htmlview.replaceAll("</iframe>&lt;/div&gt;",
					"</iframe></div>");
			// 标题照片
			htmlview = htmlview.replaceAll("tp&lt;", "<");
			htmlview = htmlview.replaceAll("/&gt;tp", "/>");
			//liuyz 2016-12-29 解决&nbsp;在火狐和谷歌、IE浏览器中显示不同问题。&ensp;“半角空格” 此空格有个相当稳健的特性，就是其占据的宽度正好是1/2个中文宽度，而且基本上不受字体影响。
			htmlview = htmlview.replaceAll("empty_context", "&ensp;");
			htmlview = htmlview.replaceAll("&amp;", "&ensp;");
			htmlview = htmlview.replaceAll("`", "<br>");
			// strhtml.append("<div style=\"border-collapse: collapse;height: expression(document.body.clientHeight)\">");
			strhtml.append(htmlview);
			strhtml.append("\n");
			/*strhtml.append("<div id='printPreviewdiv'>");
			strhtml.append("</div>");*/
			strhtml.insert(0, divlist.toString());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return strhtml.toString();
	} 

	/**
	 * 取得对应标题内容
	 * @return
	 */
	private ArrayList getAllTitle(String flag){
		ArrayList list=new ArrayList();
		try
		{
			ArrayList titleList = utilBo.getTemplateArchiveTitle(tabId, archive_id,-1);
			for(int i=0;i<titleList.size();i++)
			{
				HashMap map = (HashMap) titleList.get(i);
				TTitle title=new TTitle();
				int pageid = Integer.parseInt((String)map.get("pageid"));
				int flag_ = Integer.parseInt((String)map.get("flag"));
				if("1".equals(flag)) {
					//页标题与当前页不一致时不加此标题
					if((this.pageId!=pageid||pageid!=firstPageNo)&&flag_!=5) {
						continue;
					}
				}else if("0".equals(flag)) {
					if(pageId!=pageid) {
						continue;
					}
				}
				title.setGridno(Integer.parseInt((String)map.get("gridno")));
				title.setPageid(pageid);
				title.setTabid(Integer.parseInt((String)map.get("tabid")));
				title.setFlag(flag_);
				title.setFonteffect(Integer.parseInt((String)map.get("fonteffect")));
				title.setFontname((String)map.get("fontname"));
				title.setFontsize(Integer.parseInt((String)map.get("fontsize")));
				title.setHz((String)map.get("hz"));
				title.setRtop(Integer.parseInt((String)map.get("rtop")));
				title.setRleft(Integer.parseInt((String)map.get("rleft")));
				title.setRwidth(Integer.parseInt((String)map.get("rwidth")));
				title.setRheight(Integer.parseInt((String)map.get("rheight")));
				title.setExtendattr((String)map.get("extendattr"));
				list.add(title);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
    /**
     * @Title: createTitleElement
     * @Description: 创建HTML的表头
     * @param titlelist
     * @param div
     * @throws void
     */
    private void createTitleElement(ArrayList titlelist, Element div) {
    	int height = this.paramBo.getTable_vo().getInt("paperh");
	    int hpx = Math.round((float) (height / 25.4 * PixelInInch));
    	 for (int i = 0; i < titlelist.size(); i++) {
             TTitle title = (TTitle) titlelist.get(i);
             int flag = title.getFlag();
             if(flag==5&&this.firstPageNo == this.pageId) {
            	int currentPage = pageId;//当前页码在所有可显示得页码中得位置
             	title.setPageid(currentPage);
             	title.setRtop(hpx-30);//由于此次修改后所有页码都跟着第一个页码走，因此页码得位置就取了个固定值，防止压到内容。
             } else if(flag==5&&this.pageId>this.firstPageNo)
             	continue;//页码大于起始页码得都在下面得方法中按第一个页码属性输出
             title.setCon(this.conn);
             //title.setIns_id(this.ins_id); todowangrd
             title.createTitleView(div, this.userView);
         }
         if(this.firstPageNo!=-1&&this.pageId>this.firstPageNo) {
         	this.firstPageTitleList = this.getAllTitle("1");
         	int currentPage = pageId;
         	for(int j=0;j<this.firstPageTitleList.size();j++) {
         		TTitle title = (TTitle) this.firstPageTitleList.get(j);
         		title.setPageid(currentPage);
			    title.setRtop(hpx-30);
         		title.createTitleView(div, this.userView);
         	}
         }
    }
	
	/**
	 * 创建单元格输出对象
	 * @param celllist
	 * @param div
	 */
	private String createAllCellElement(ArrayList celllist,Element div)
	{
        StringBuffer divString = new StringBuffer();
        HashMap priMap = new HashMap();
		for(int i=0;i<celllist.size();i++)
		{
			TemplateSet cell=(TemplateSet)celllist.get(i);	
			String fieldname  = cell.getField_name();
			String fieldType = cell.getFlag();

			if (!priMap.isEmpty() && (priMap.get(fieldname.toLowerCase()) != null || "S".equalsIgnoreCase(fieldType))) {
				if ((!"S".equalsIgnoreCase(fieldType) && "3".equals(priMap.get(fieldname.toLowerCase())))
						|| ("S".equalsIgnoreCase(fieldType)
								&& "3".equals(priMap.get("S_" + cell.getPageId() + "_" + cell.getGridno()))))
					cell.setYneed(true);
			}
			this.signnumber = cell.getGridno();//打印pdf签章不对。
			createCellView(div,cell);
		}	
		
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setExpandEmptyElements(true);// must
        format.setEncoding("UTF-8");
        outputter.setFormat(format);
        divString.append(outputter.outputString(div));
        return divString.toString();
	}   
	
	
	/**
	 * 创建HTML对象
	 * @param element
	 * @param userview
	 */
	public void createCellView(Element element,TemplateSet setBo)
	{
		String  flag =	setBo.getFlag();
		Element div=new Element("div");
		Element table=new Element("table");
		table.setAttribute("class","cellTableStyle");
		String cellStyle="";
		if("H".equals(flag)){
		    cellStyle=cellStyle+"line-height:1.1;";//bug19494
		} 
		if(!"".equals(cellStyle))
			table.setAttribute("style",cellStyle);
		table.setAttribute("cellspacing","0");

		String divStyle= getDivPos(setBo);		
		div.setAttribute("style",divStyle);
		div.setAttribute("class",getBorderLineCss(setBo.getRect(),setBo));//没有边框线
		
		Element tr=new Element("tr");
		Element td=new Element("td");
		String[] align=getHValign(setBo.getAlign()); 		
		
		td.setAttribute("valign",align[1]);
		td.setAttribute("style","padding:1px;");
		if(setBo.isSubflag())
			td.setAttribute("align","left");			
		else
			td.setAttribute("align",align[0]);
		if("H".equals(flag)){
	        if(setBo.getRtop()!=setBo.getRect().y)
	            td.setAttribute("height", String.valueOf(setBo.getRheight()+1));
	        else
	            td.setAttribute("height", String.valueOf(setBo.getRheight()));
	        if(setBo.getRleft()!=setBo.getRect().x){
	        	if("left".equals(align[0]))
	        		td.setAttribute("width", String.valueOf(setBo.getRwidth()+1));
	        	if("right".equals(align[0]))
	        		td.setAttribute("width", String.valueOf(setBo.getRwidth()-1));
	        }else
	            td.setAttribute("width", String.valueOf(setBo.getRwidth()));
	        td.setAttribute("nowrap","nowrap");	
        }
		else {
		    td.setAttribute("nowrap","false");	
		}
		
		createEditor(td,setBo);
		int signatureType = this.paramBo.getTemplateModuleParam().getSignatureType();
		tr.addContent(td);
		table.addContent(tr);
		div.addContent(table);
		if("S".equals(flag)&&(signatureType==0||signatureType==2)){
			Element divs=new Element("div");
			divs.setAttribute("id","signature"+setBo.getPageId()+"S"+this.signnumber);
			divs.addContent(div);
			element.addContent(divs);
		}else{
			element.addContent(div);
		}
	}

	/**
	 * 创建输入框
	 * @param td
	 */
	private void createEditor(Element td,TemplateSet setBo)
	{
		if(setBo.getFlag()==null|| "".equalsIgnoreCase(setBo.getFlag()))
			setBo.setFlag("H");
		String flag=setBo.getFlag();
		String field_type=setBo.getField_type();
		String field_name = setBo.getField_name(); //xgq 电子签章
		if("H".equals(flag))//汉字描述
		{		
			Element font=new Element("font");
			font.setAttribute("face",setBo.getFontname());
			font.setAttribute("style",getFontStyle(setBo));
			
			font.setText(getOutText(setBo));
			td.addContent(font);				
		}
		else if("A".equals(flag)|| "B".equals(flag)|| "K".equals(flag)) //指标
		{
		    if(((setBo.getHismode()==2||setBo.getHismode()==3||(setBo.getHismode()==4))&&setBo.getChgstate()==1)||(setBo.isSubflag()))
		    {
		      if(!setBo.isSubflag()&&((setBo.getHismode()==2 || setBo.getHismode()==4) && (setBo.getMode()==0 
                      || setBo.getMode()==2))){
                 FieldItem fldItem = DataDictionary.getFieldItem(field_name,setBo.getSetname());
                 if (fldItem!=null && "M".equalsIgnoreCase(fldItem.getItemtype())){
                     createTextAreaEditor(td,setBo); 
                 }
                 else {
                     createInputEditor(td,setBo);
                 }
              }
		      else {
		          createDivPanel(td,setBo);			  
		      }
		  }
		  else
		  {
			  if("D".equalsIgnoreCase(field_type))
			  {
					createInputEditor(td,setBo);			  
			  }
			  else if("N".equalsIgnoreCase(field_type))
			  {
					createInputEditor(td,setBo);				  
			  }
			  else if("M".equalsIgnoreCase(field_type))
			  {
				  createTextAreaEditor(td,setBo);
			  }
			  else if("A".equalsIgnoreCase(field_type))
			  {
				createInputEditor(td,setBo);
			  }
		  }
		}
		else if("P".equals(flag)) //picture
		{
			createImageEditor(td,setBo);
		}
		else if("F".equals(flag)) //attachment
		{
			createAttachmentEditor(td,setBo);
		}	
		else if("V".equals(flag))//临时变量
		{
		    createInputEditor(td,setBo);
		}	
		else if("C".equals(flag))//计算公式
		{            
		    createInputVarEditor(td,setBo);
		}
		else if("S".equals(flag))//签章
		{
			createSignatureEditor(td,setBo);
		} 
	}
	/** 
	* @Title: setElementPublicProperty 
	* @Description: 设置元素的公用属性，id field_name 用于前台显示数据及修改数据
	* @param @param td
	* @return void
	*/ 
	private void setElementPublicProperty(Element text,TemplateSet setBo) {		
		text.setAttribute("id",setBo.getUniqueId());//单元格唯一键值
		text.setAttribute("field",setBo.getTableFieldName());//数据库中的字段名称		
		text.setAttribute("fieldsetid","dataset_"+setBo.getPageId());//数据结构对象的Id
		text.setAttribute("recordsetid","dataset_"+setBo.getPageId());//数据对象的Id
	}
	/**
	 * 创建历史记录输出面板
	 * @param td
	 */
	private void createDivPanel(Element td,TemplateSet setBo) {
		Element text=new Element("div");
		String[] align=getHValign(setBo.getAlign()); 		
		
		td.setAttribute("nowrap","false");		
		StringBuffer style=new StringBuffer();
		style.append("font-size:");
		double fontsize = this.getRealFontSize(setBo.getFontsize());
		style.append(fontsize);
		style.append("pt !important; ");
		style.append("font-family:"+setBo.getFontname()+";");
		style.append("white-space:pre-wrap;word-wrap:break-word;overflow:auto;");//bug 39603 解决div标签中内容不自动换行，和换行符不识别
		if(setBo.isSubflag()){
			style.append("padding:0px;vertical-align:");
			style.append("top");
		}else{
			style.append("padding:1px;vertical-align:");
			style.append(align[1]);	
		}
		if(setBo.isSubflag()){
			String height = "";
			String width = "";
	        if(setBo.getRtop()!=setBo.getRect().y)
	        	height=String.valueOf(setBo.getRheight()-3);
	        else
	        	height=String.valueOf(setBo.getRheight()-3);
	        if(setBo.getRleft()!=setBo.getRect().x)
	        	width=String.valueOf(setBo.getRwidth()-3);
	        else
	        	width=String.valueOf(setBo.getRwidth()-3);
	        text.setAttribute("nowrap","nowrap");	
			text.setAttribute("autocomplete","off");//bug 46568 禁止浏览器记录 文本框之前输入过的内容
			setElementPublicProperty(text,setBo);
			text.setAttribute("style",style.toString()+";height:"+height+"px;width:"+width+"px;");
			text.setAttribute("extra","panel");
			td.addContent(text);
		}
		else {
			setElementPublicProperty(text,setBo);
			style.append(";overflow-y:auto");		
			style.append(";height:100%");		
			text.setAttribute("style",style.toString());
			text.setAttribute("extra","panel");
			td.addContent(text);
		}
		if(setBo.isSubflag()){
			String hz=setBo.getHz();
			hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
		}
	}
	/**
	 * 创建输出框INPUT
	 * @param td
	 */
	private void createInputEditor(Element td,TemplateSet setBo) {
		String field_name=setBo.getField_name().toLowerCase();
		FieldItem fielditem=DataDictionary.getFieldItem(field_name); 
		if(fielditem!=null){
			if(fielditem.getItemlength()>=255&&"0".equals(setBo.getCodeid())&&"A".equals(setBo.getField_type())&&"A".equals(setBo.getFlag())){//大于255的字符型指标看做大文本处理
				createTextAreaEditor(td,setBo);
				return;
			}
		}
		if(setBo.getChgstate()==1)
		{
			createDivEditor(td,setBo);
			return;
		}
		Element text=new Element("input");
		text.setAttribute("type","text");
		setElementPublicProperty(text,setBo);
		
		String itemmemo="";
		if(fielditem!=null)
			itemmemo=fielditem.getExplain();
		if(itemmemo==null)
			itemmemo="";
		itemmemo = itemmemo.replaceAll("\r\n", "\n");
        itemmemo = itemmemo.replaceAll("\n", "<br>");
        if("<br>".equals(itemmemo.trim()))
            itemmemo="";		
	
		StringBuffer style=new StringBuffer();
		style.append("width:");
		if(setBo.isYneed())
			style.append(setBo.getRwidth()-20);
		else
			style.append(setBo.getRwidth()-5);
		style.append("px;");
		style.append("font-size:");
		double fontsize = this.getRealFontSize(setBo.getFontsize());
		style.append(fontsize+"pt;");
	    style.append("font-family:"+setBo.getFontname()+";");
		String[] align=getHValign(setBo.getAlign()); 
		String hAlign= align[0];
        style.append("text-align:"+hAlign);
		
		
		text.setAttribute("style",style.toString());
		text.setAttribute("extra","editor");
		text.setAttribute("autocomplete","off");//bug 46568 禁止浏览器记录 文本框之前输
		text.setAttribute("onfocus", "processFocus(this)");
		text.setAttribute("onclick", "processClick(this)");
		text.setAttribute("onblur", "processBlur(this)");
		text.setAttribute("name",setBo.getUniqueId()+"_view");
		boolean isSpecialCodesetId = false;
		td.addContent(text);
		if(setBo.isYneed())
		{
			Element font=new Element("font");
			font.setAttribute("face",setBo.getFontname());
			font.setAttribute("style",getFontStyle(setBo));
			font.setAttribute("color", "red");
			font.setText("*");
			td.addContent(font);				
		}
		if(setBo.getChgstate()==2&&itemmemo.length()>0)
		{
			td.setAttribute("onmouseover","templateTip(\'"+setBo.getUniqueId()+"\',\'"+itemmemo+"\')");	
		}
		if(setBo.isSpecialItem()){
			if("codesetid".equals(setBo.getField_name())){
				text.setAttribute("codesource","GetSpecialTemplateSetTree");
				isSpecialCodesetId = true;
			}else if("parentid".equals(setBo.getField_name()))
				text.setAttribute("onlySelectCodeset","false");
		}
		if ((setBo.getChgstate()==2 && (setBo.isBcode() || isSpecialCodesetId))|| (setBo.isVarItem() && setBo.isBcode()) ){		
			text.setAttribute("plugin","codetree");
			if(isSpecialCodesetId)
				text.setAttribute("codesetid","codesetid");
			else
				text.setAttribute("codesetid",setBo.getCodeid());
			if("UN".equals(setBo.getCodeid())|| "UM".equals(setBo.getCodeid())|| "@K".equals(setBo.getCodeid())){
				text.setAttribute("ctrltype","0");
				if (setBo.isBLimitManagePriv()){
				    text.setAttribute("ctrltype","3");//1：按管理范围 3:业务范围
				}
				if("UM".equals(setBo.getCodeid()))//只有关联UM代码类的指标才能选择其上级单位UN，关联UN的不能选择其下级部门UM
					text.setAttribute("onlySelectCodeset","false");
				if("e0122".equalsIgnoreCase(setBo.getField_name()))
					text.setAttribute("isShowLayer",display_e0122);
			}else{
				text.setAttribute("ctrltype","");
				text.setAttribute("onlySelectCodeset","false");//普通代码型指标不控制只能选择子节点
			}
			text.setAttribute("nmodule","8");//人事异动
			text.setAttribute("inputname",setBo.getUniqueId()+"_view");
			text.setAttribute("afterfunc","afterSelectCode");			
			
			Element hiddenInput=new Element("input");
			hiddenInput.setAttribute("type","hidden");
			hiddenInput.setAttribute("name",setBo.getUniqueId()+"_value");
			hiddenInput.setAttribute("id",setBo.getUniqueId()+"_codevalue");
			td.addContent(hiddenInput);
		}
		else if ((setBo.getChgstate()==2 || setBo.isVarItem() ) && ("D".equals(setBo.getField_type()))){
			text.setAttribute("plugin","datetimeselector");
			text.setAttribute("inputname",setBo.getUniqueId()+"_view");
			if(setBo.getDisformat()==25) //日期格式支持小时：分钟
				text.setAttribute("format","Y-m-d H:i");
			else
				text.setAttribute("format","Y-m-d");
			text.setAttribute("afterfunc","afterSelectDate");
			text.setAttribute("onchange","contentChange(this)");
			text.setAttribute("spaceselect","false");//日期控件允许手工输入。
		}
	}
	
	private void createInputVarEditor(Element td,TemplateSet setBo) {
		Element text=new Element("input");
		text.setAttribute("type","text");
		setElementPublicProperty(text,setBo);
        StringBuffer style=new StringBuffer();
        style.append("width:");
        if(setBo.isYneed())
            style.append(setBo.getRwidth()-20);
        else
            style.append(setBo.getRwidth()-5);
        style.append("px;");
        style.append("font-size:");
        double fontsize = this.getRealFontSize(setBo.getFontsize());
        style.append(fontsize+"pt;");
        style.append("font-family:"+setBo.getFontname()+";");
        String[] align=getHValign(setBo.getAlign()); 
        String hAlign= align[0];
        style.append("text-align:"+hAlign);
        
        
        text.setAttribute("style",style.toString());
		text.setAttribute("extra","editor");
		text.setAttribute("onfocus", "processFocus(this)");
		text.setAttribute("onclick", "processClick(this)");
		text.setAttribute("onblur", "processBlur(this)");
		td.addContent(text);
		if(setBo.isYneed())
		{
			Element font=new Element("font");
			font.setAttribute("face",setBo.getFontname());
			font.setAttribute("style",getFontStyle(setBo));
			font.setAttribute("color", "red");
			font.setText("*");
			td.addContent(font);				
		}		
	}	
	private void createTextAreaEditor(Element td,TemplateSet setBo) {
		Element text=new Element("textarea");
		setElementPublicProperty(text,setBo);
		String field_name=setBo.getField_name().toLowerCase();//+"_"+setBo.getChgstate();
		FieldItem fieldItem = DataDictionary.getFieldItem(field_name);
		if(fieldItem!=null){
			if(fieldItem.getItemlength()!=0&&fieldItem.getItemlength()!=10&&!(paramBo!=null&&paramBo.getOpinion_field()!=null&&paramBo.getOpinion_field().trim().length()>0&&fieldItem.getItemid().equalsIgnoreCase(paramBo.getOpinion_field())))//liuyz 审批意见指标不需要进行字数判断。
				if("M".equalsIgnoreCase(setBo.getOld_fieldType())){
					text.setAttribute("onBlur","jugeLength(this,"+fieldItem.getItemlength()+")");
				}else{
					text.setAttribute("onBlur","jugeNormalLength(this,"+fieldItem.getItemlength()+")");
				}
		}
		StringBuffer style=new StringBuffer();
		style.append("height:");
		style.append(setBo.getRheight()-6);
		style.append("px;");
		style.append("width:");
		if(setBo.isYneed())
			style.append(setBo.getRwidth()-21);
		else
			style.append(setBo.getRwidth()-5);
		style.append("px;");
		style.append("font-size:");
		double fontsize = this.getRealFontSize(setBo.getFontsize());
		style.append(fontsize);
		style.append("pt;resize:none;");
	    style.append("font-family:"+setBo.getFontname()+";");
	    style.append("text-align:left");
		style.append(";overflow:auto;");
		if (setBo.getChgstate()==1){
		    style.append(";border: 0;");
		}
		style.append("white-space:pre-wrap;white-space:-moz-pre-wrap;word-wrap:break-word;");
		text.setAttribute("style",style.toString());
		text.setAttribute("extra","textarea");
		text.setAttribute("autocomplete","off");
		text.setAttribute("onfocus", "processFocus(this)");
		text.setAttribute("onclick", "processClick(this)");
		text.setAttribute("onblur", "processBlur(this)");
		
        String itemmemo="";
        if(fieldItem!=null)
            itemmemo=fieldItem.getExplain();
        if(itemmemo==null)
            itemmemo="";
        itemmemo = itemmemo.replaceAll("\r\n", "\n");
        itemmemo = itemmemo.replaceAll("\n", "<br>");
        if("<br>".equals(itemmemo.trim()))
            itemmemo="";
        if(setBo.getChgstate()==2&&itemmemo.length()>0)
        {
            td.setAttribute("onmouseover","templateTip(\'"+setBo.getUniqueId()+"\',\'"+itemmemo+"\')"); 
        } 
        
		td.addContent(text);
		if(setBo.isYneed())
		{
			Element font=new Element("font");
			font.setAttribute("face",setBo.getFontname());
			font.setAttribute("style",getFontStyle(setBo));
			font.setAttribute("color", "red");
			font.setText("*");
			td.addContent(font);				
		}		
	}
	
	/**
	 * 照片
	 * @param td
	 */
	private void createImageEditor(Element td,TemplateSet setBo) {
		Element text=new Element("input");
		text.setAttribute("type","image");
		text.setAttribute("src","/images/photo.jpg");
		setElementPublicProperty(text,setBo);
		StringBuffer style=new StringBuffer();
		style.append("height:");
		style.append(setBo.getRheight()-7);
		style.append("px;");
		style.append("width:");
		style.append(setBo.getRwidth()-5);
		style.append("px;");
		text.setAttribute("style",style.toString());
		text.setAttribute("extra","photo");
		/**业务类型为0时，才需要上传照片*/
		
		if(!"".equals(paramBo.getDest_base())||paramBo.getOperationType()==10)
		{
			text.setAttribute("ondblclick","upload_picture('"+setBo.getUniqueId()+"');");
			text.setAttribute("onclick","upload_picture('"+setBo.getUniqueId()+"');");
		}
		td.addContent(text);		
		
	}
	/**
	 * 附件
	 * @param td
	 */
	private void createAttachmentEditor(Element td, TemplateSet setBo) {
		Element text=new Element("div");
		String height = "";
		String width = "";
        if(setBo.getRtop()!=setBo.getRect().y)
        	height=String.valueOf(setBo.getRheight()-3);
        else
        	height=String.valueOf(setBo.getRheight()-3);
        if(setBo.getRleft()!=setBo.getRect().x)
        	width=String.valueOf(setBo.getRwidth()-3);
        else
        	width=String.valueOf(setBo.getRwidth()-3);
        text.setAttribute("nowrap","nowrap");	
		setElementPublicProperty(text,setBo);
		text.setAttribute("style","height:"+height+"px;width:"+width+"px;");
		text.setAttribute("extra","panel");
		Element font=new Element("font");
		font.setAttribute("face",setBo.getFontname());
		font.setAttribute("style",getFontStyle(setBo));
		text.addContent(font);
		td.addContent(text);
	}
	/**
	 * 变化前指标显示Div
	 * @param td
	 */
	private void createDivEditor(Element td, TemplateSet setBo) {
		Element text=new Element("Div");
        text.setAttribute("nowrap","nowrap");	
		setElementPublicProperty(text,setBo);
		text.setAttribute("extra","panel");
		StringBuffer style=new StringBuffer();
		style.append("height:");
		style.append(setBo.getRheight()-6);
		style.append("px;");
		style.append("width:100%;");
		style.append("font-size:");
		double fontsize = this.getRealFontSize(setBo.getFontsize());
		style.append(fontsize);
		style.append("pt;resize:none;");
	    style.append("font-family:"+setBo.getFontname()+";");
	    
		style.append("white-space:pre-wrap;white-space:-moz-pre-wrap;word-wrap:break-word;");
		String[] align=getHValign(setBo.getAlign()); 
	    style.append("text-align:");
	    style.append(align[0]);
	    style.append(";display:table-cell;vertical-align:");
	    style.append(align[1]);
	    style.append(";");
		if (setBo.getChgstate()==1){
		    style.append("border: 0;");
		}
		text.setAttribute("style",style.toString());
		td.addContent(text);
	}
	/**
	 * 签章
	 * @param td
	 */
	private void createSignatureEditor(Element td,TemplateSet setBo) {
		int signatureType = this.paramBo.getTemplateModuleParam().getSignatureType();
		Element div=new Element("div");
		String id = "";
		String name = "";
		if(signatureType==1){
			id = setBo.getUniqueId()+"_signid";
			name = this.userView.getUserName()+"_"+setBo.getUniqueId()+"_signObj";
			div.setAttribute("id", id);
			div.setAttribute("name", name);
			div.setAttribute("style",getFontStyle(setBo));
			td.addContent(div);
		}
		setElementPublicProperty(td,setBo);
		String field_name=setBo.getField_name().toLowerCase();
		FieldItem fielditem=DataDictionary.getFieldItem(field_name);
		String itemmemo="";
		if(fielditem!=null)
			itemmemo=fielditem.getExplain();
		if(itemmemo==null)
			itemmemo="";
		itemmemo = itemmemo.replaceAll("\r\n", "\n");
        itemmemo = itemmemo.replaceAll("\n", "<br>");
        if("<br>".equals(itemmemo.trim()))
            itemmemo="";		
	
		StringBuffer style=new StringBuffer();
		style.append("width:");
		if(setBo.isYneed())
			style.append(setBo.getRwidth()-20);
		else
			style.append(setBo.getRwidth()-5);
		style.append("px;");
		style.append("font-size:");
		double fontsize = this.getRealFontSize(setBo.getFontsize());
		style.append(fontsize+"pt;");
	    style.append("font-family:"+setBo.getFontname()+";");
		String[] align=getHValign(setBo.getAlign()); 
		String hAlign= align[0];
        style.append("text-align:"+hAlign);
		
		String tdname = setBo.getUniqueId()+"_view";
        td.setAttribute("style",style.toString());
        td.setAttribute("name",tdname);
        
        if("1".equals(this.getApproveFlag())){
        	if(signatureType==1){//BJCA
            	td.setAttribute("ondblclick","DoMouseSignature('"+name+"|"+setBo.getPageId()+"|"+id+"','"+signatureType+"')");
            }
        	else if(signatureType==0||signatureType==2){
            	String setname=this.userView.getUserName()+"_templet_"+setBo.getTabId();
            	if(signatureType==2) {
            		if(this.userView.getStatus()==4) {//自助用户
            			setname=this.userView.getDbname()+this.userView.getA0100()+"_templet_"+setBo.getTabId();
            		}
            		td.setAttribute("onmouseleave","DoMouseSignatureLeave('"+setBo.getPageId()+"','"+this.signnumber+"')");
            		td.setAttribute("onmouseover","DoMouseSignatureOver('"+setname+"|signature"+setBo.getPageId()+"S"+this.signnumber+"|"+setBo.getPageId()+"','"+signatureType+"')");
            	}else
            		td.setAttribute("ondblclick","DoMouseSignature('"+setname+"|signature"+setBo.getPageId()+"S"+this.signnumber+"|"+setBo.getPageId()+"','"+signatureType+"')");
            }
        	if(signatureType!=2)
        		td.setAttribute("title","请双击进行签章");
        }
	}

	/**
	 * 显示单元格标题 flag=H
	 * @return
	 */
	private String getOutText(TemplateSet setBo)
	{
		StringBuffer strcontent = new StringBuffer();
		String hz=  setBo.getHz();
		String[] strs =hz.split("`");
		for (int i = 0; i < strs.length; i++) {
			strcontent.append(strs[i]);
			strcontent.append("`");
		}
		if (strcontent.length() > 0)
			strcontent.setLength(strcontent.length() - 1);
		if (strcontent.length() == 0)
			strcontent.append("&"); // 补空

		if (strcontent.toString().trim().length() == 0) // 解决空单元格边框 显示不全的问题
			strcontent.append("empty_context");
		String str =strcontent.toString().replace(" ", "empty_context");
		return str;
	}

	
	/**
     * 单元格样式  线变粗了
     * @return
     */
    private String getDivPos(TemplateSet setBo)
    {
        StringBuffer style=new StringBuffer();
        style.append("position:absolute");
        int top = setBo.getRtop();
        int height = setBo.getRheight();
        if(setBo.getRtop()!=setBo.getRect().y)
        {
            top = setBo.getRtop()-1;
            height= setBo.getRheight()+1;
        }
        
        int left = setBo.getRleft();
        int width = setBo.getRwidth();
        if(setBo.getRleft()!=setBo.getRect().x)
        {
            left= setBo.getRleft()-1;
            width =setBo.getRwidth()+1;
        }
        style.append(";top:"+top+"px");
        style.append(";left:"+left+"px");
        style.append(";height:"+height+"px");
        style.append(";width:"+width+"px");
        style.append(";overflow: hidden;");
        return style.toString();
    }
	
	private String getCellCss(TemplateSet setBo)
	{
		String css="";		
		if(setBo.getR()==1&&setBo.getB()==0&&setBo.getL()==0&&setBo.getT()==0)
			css="r_line";
		else if(setBo.getR()==0&&setBo.getB()==1&&setBo.getL()==0&&setBo.getT()==0)
			css="b_line";
		else if(setBo.getR()==0&&setBo.getB()==0&&setBo.getL()==1&&setBo.getT()==0)
			css="l_line";
		else if(setBo.getR()==0&&setBo.getB()==0&&setBo.getL()==0&&setBo.getT()==1)
			css="t_line";
		
		else if(setBo.getR()==1&&setBo.getB()==1&&setBo.getL()==0&&setBo.getT()==0)
			css="rb_line";
		else if(setBo.getR()==1&&setBo.getB()==0&&setBo.getL()==1&&setBo.getT()==0)
			css="lr_line";		
		else if(setBo.getR()==1&&setBo.getB()==0&&setBo.getL()==0&&setBo.getT()==1)
			css="rt_line";	
		else if(setBo.getR()==0&&setBo.getB()==1&&setBo.getL()==1&&setBo.getT()==0)
			css="lb_line";	
		else if(setBo.getR()==0&&setBo.getB()==1&&setBo.getL()==0&&setBo.getT()==1)
			css="tb_line";	
		else if(setBo.getR()==0&&setBo.getB()==0&&setBo.getL()==1&&setBo.getT()==1)
			css="lt_line";	
		
		else if(setBo.getR()==1&&setBo.getB()==1&&setBo.getL()==1&&setBo.getT()==0)
			css="lrb_line";
		else if(setBo.getR()==0&&setBo.getB()==1&&setBo.getL()==1&&setBo.getT()==1)
			css="ltb_line";
		else if(setBo.getR()==1&&setBo.getB()==0&&setBo.getL()==1&&setBo.getT()==1)
			css="lrt_line";
		else if(setBo.getR()==1&&setBo.getB()==1&&setBo.getL()==0&&setBo.getT()==1)
			css="rtb_line";
		
		else if(setBo.getR()==1&&setBo.getB()==1&&setBo.getL()==1&&setBo.getT()==1)
			css="lrtb_line";
		else 
			css="no_line";
		return css;
	}
	
	/**
	 * 取得样式
	 * @param rect 表格外边框区域
	 * @return
	 */
	protected String getBorderLineCss(Rectangle rect,TemplateSet setBo)
	{
		String css=getCellCss(setBo);
		return css;
	}	
	
	/**
	 * 求得单元格字体信息
	 * @return
	 */
	public String getFontStyle(TemplateSet setBo)
	{
		StringBuffer style=new StringBuffer();
		style.append("font-size:");
		double fontsize = this.getRealFontSize(setBo.getFontsize());
		style.append(fontsize);
		style.append("pt");
		switch(setBo.getFonteffect())
		{
		case 2:
			style.append(";font-weight:");
			style.append("bold");
			break;
		case 3:
			style.append(";font-style:");
			style.append("italic");			
			break;
		case 4:
			style.append(";font-weight:");
			style.append("bold");
			style.append(";font-style:");
			style.append("italic");				
			break;
		}
		return style.toString();
	}	
	/**
	 * 得到真实的字体大小
	 * @param fontsize
	 * @return
	 */
	private double getRealFontSize(int fontsize) {
		double fontsize_real = 0.0;
		if(fontsize==11)
			fontsize_real = 10.5;
		else if(fontsize==8)
			fontsize_real = 7.5;
		else if(fontsize==7)
			fontsize_real = 6.5;
		else if(fontsize==6)
			fontsize_real = 5.5;
		else
			fontsize_real = fontsize;
		return fontsize_real;
	}
	/**
	 * 排列方式
	 * @param ali
	 * @return
	 */
	protected String[] getHValign(int ali) {
		String[] align = new String[2];
		switch(ali)
		{
		case 0:
			align[0] = "left";
			align[1] = "top";			
			break;
		case 1:
			align[0] = "center";
			align[1] = "top";		
			break;	
		case 2:
			align[0] = "right";
			align[1] = "top";			
			break;
		case 3:
			align[0] = "left";
			align[1] = "bottom";			
			break;
		case 4:
			align[0] = "center";
			align[1] = "bottom";			
			break;
		case 5:
			align[0] = "right";
			align[1] = "bottom";			
			break;
		case 6:
			align[0] = "left";
			align[1] = "middle";
			break;
		case 7:
			align[0] = "center";
			align[1] = "middle";
			break;
		case 8:
			align[0] = "right";
			align[1] = "middle";			
			break;
		}
		return align;
	}	
	public int getTabid() {
		return tabId;
	}


	public void setTabid(int tabid) {
		this.tabId = tabid;
	}

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
		this.pageCellList=null;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String taskId) {
		task_id = taskId;
	}

	public String getSelfApply() {
		return selfApply;
	}

	public void setSelfApply(String selfApply) {
		this.selfApply = selfApply;
	}

    public String getObjectId() {
        return objectId;
    }


    public void setObjectId(String objectId) {
        this.objectId = objectId;        
        if (this.paramBo.getInfor_type()==1){
            int i = this.objectId.indexOf("`");
            if (i>0){
                String basepre=this.objectId.substring(0,i);
                String a0100=this.objectId.substring(i+1);
                this.a0100 = a0100;
                this.basePre = basepre;
            }
        }
        else if (this.paramBo.getInfor_type()==2){
            this.b0110=this.objectId;
        }
        else if (this.paramBo.getInfor_type()==3){
            this.e01a1=this.objectId;
        }  
        
    }

    public String getApproveFlag() {
        return approveFlag;
    }

    public void setApproveFlag(String approveFlag) {
        this.approveFlag = approveFlag;
    }

    public String getCurTaskId() {
        return curTaskId;
    }

    public void setCurTaskId(String curTaskId) {
        this.curTaskId = curTaskId;
    }

    public TemplateParam getParamBo() {
		return paramBo;
	}

	public void setParamBo(TemplateParam paramBo) {
		this.paramBo = paramBo;
	}
	public int getFirstPageNo() {
		return firstPageNo;
	}

	public void setFirstPageNo(int firstPageNo) {
		this.firstPageNo = firstPageNo;
	}

	public String getNoShowPageNo() {
		return noShowPageNo;
	}

	public void setNoShowPageNo(String noShowPageNo) {
		this.noShowPageNo = noShowPageNo;
	}

	public String getArchive_id() {
		return archive_id;
	}

	public void setArchive_id(String archive_id) {
		this.archive_id = archive_id;
	}
	
}
