/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.utils.javabean.SubSetDomain;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Title:单元</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 18, 20066:17:12 PM
 * @author chenmengqing
 * @version 4.0
 */
public class TemplateSetBo extends TCell {
	private Connection conn;
	private String display_e0122="0";
	private UserView userview;	
	private TemplateTableBo tablebo;
	/**业务类型
	 * 对人员调入的业务单独处理
	 * =0人员调入,=1调出（须指定目标人员库）,=2离退(须指定目标人员库),=3调动,
	 * =10其它不作特殊处理的业务
	 * 如果目标库未指定的话，则按源库进行处理
	 */	
	private int operationtype=10; 
	/**变化前，变化后标识
	 * =1变化前
	 * =2变化后
	 * */
	private int chgstate;
	/**宽度及高度,整个表格的*/
	private Rectangle rect=new Rectangle(0,0,0,0);
	/**是否必填*/
	private boolean yneed=false;
	
 
	
	/**子集控制位*/
	private boolean subflag=false;
	/**控制参数*/
	private String xml_param;
	/**父对象*/
	private TemplatePageBo pagebo;
	/**前缀字符串*/
	private String preCon="";
	/**联动指标*/
	private String relation_field="";
	/**默认值**/
	private String defaultValue="";
	
	private boolean  isFinished_record=false;  //判断当前单元格显示的记录 是否为已结束任务的记录  用于浏览结束后记录的模板时，变化前指标不动态取库中的值
	private int signnumber=0;
	private String sub_domain_id="";
	/**附件类型 ，个人：1 ，    公共： 0*/
	private String attachmentType="0";
	private String attachmentXml="";//保存附件设置，获取附件显示哪个分类。

	public String getAttachmentXml() {
		return attachmentXml;
	}
	public void setAttachmentXml(String attachmentXml) {
		this.attachmentXml = attachmentXml;
	}
	public TemplatePageBo getPagebo() {
		return pagebo;
	}
	public void setPagebo(TemplatePageBo pagebo) {
		this.pagebo = pagebo;
	}
	/**求子集区域对应的字段列表及控制内容*/
	public ArrayList getSubDomainFieldFmtList()
	{
		TSubSetDomain subdomain=new TSubSetDomain(this.xml_param);
		return subdomain.getFieldfmtlist();
	}
	/**
	 * 是否为代码
	 * @return
	 */
	@Override
    public boolean isBcode() {
		String temp=this.getCodeid();
		if(temp==null|| "0".equals(temp)|| "".equals(temp)) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isSubflag() {
		return subflag;
	}

	public void setSubflag(boolean subflag) {
		this.subflag = subflag;
	}

	public String getXml_param() {
		return xml_param;
	}

	public void setXml_param(String xml_param) {
		this.xml_param = xml_param;
		parserSubDomain(xml_param);//解析xml中的联动指标、启用选人组件、默认值等设置。
	}
	
	public boolean isYneed() {
		return yneed;
	}

	public void setYneed(boolean yneed) {
		this.yneed = yneed;
	}

	public TemplateSetBo(Connection conn) {
		this.conn=conn;
		
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122)) {
			display_e0122="0";
		}
	}

	
	
	public TemplateSetBo(Connection conn,String _display_e0122) {
		this.conn=conn;
		display_e0122=_display_e0122;
		if(_display_e0122==null|| "00".equals(_display_e0122)|| "".equals(_display_e0122)) {
			display_e0122="0";
		}
	}
	
	
	public int getChgstate() {
		return chgstate;
	}

	public void setChgstate(int chgstate) {
		this.chgstate = chgstate;
	}
	@Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("{");
        str.append("Hz=");
        str.append(this.getHz());
        str.append(",setname=");
        str.append(this.getSetname());
        str.append(",field_name=");
        str.append(this.getField_name());
        str.append(",field_type=");
        str.append(this.getField_type());
        str.append(",flag=");
        str.append(this.getFlag());
        str.append(",subflag=");
        str.append(subflag);
        str.append(",rtop=");
        str.append(this.getRtop());        
        str.append(",rleft=");
        str.append(this.getRleft());
        str.append(",rheight=");
        str.append(this.getRheight());
        str.append(",rwidth=");
        str.append(this.getRwidth());
        str.append(",sub_domain_id=");
        str.append(this.getSub_domain_id());
        str.append("}");		
		return str.toString();
	}
	

	/**
	 * 样式重叠，线变粗啦
	 * @return
	 */
	private String getTablePos()
	{
		StringBuffer strpos=new StringBuffer();
		strpos.append("table-layout:fixed;position:absolute;top:");
		if(this.getRtop()!=rect.y)//如果当前表格的上边坐标不是最小的那个
		{
			strpos.append(this.getRtop()-1);
			strpos.append("px");			
			strpos.append(";height:");
			strpos.append(this.getRheight()+1);
			strpos.append("px");
		}
		else
		{
  		    strpos.append(this.getRtop());
			strpos.append("px");		  
			strpos.append(";height:");
			strpos.append(this.getRheight());
			strpos.append("px");
		}
		strpos.append(";left:");
		if(this.getRleft()!=rect.x)
		{
			strpos.append(this.getRleft()-1);
			strpos.append("px");
			strpos.append(";width:");
			strpos.append(this.getRwidth()+1);
			strpos.append("px");
		}
		else
		{
		    strpos.append(this.getRleft());
			strpos.append("px");
			strpos.append(";width:");
			strpos.append(this.getRwidth());
			strpos.append("px");
		}
		return strpos.toString();
	}
	/**
	 * 求对应的单元格内容
	 * @return
	 */
	private String getOutText()
	{
	  StringBuffer strcontent=new StringBuffer();		
//	  if(this.getFlag().equals("H"))
//	  {
		String[] strs=StringUtils.split(this.getHz(),"`");
		for(int i=0;i<strs.length;i++)
		{
			strcontent.append(strs[i]);
			strcontent.append("`");
		}
		if(strcontent.length()>0) {
			strcontent.setLength(strcontent.length()-1);
		}
		if(strcontent.length()==0) {
			strcontent.append("&"); //补空
		}
//	  }
//	  else
//	  {
//		 strcontent.append("&");
//	  }
	  if(strcontent.toString().trim().length()==0) //解决空单元格边框 显示不全的问题
	  {
		  strcontent.append("empty_context");
	  }
	  return strcontent.toString();	  
	}
	/**
	 * 创建输入框
	 * @param td
	 */
	private void createEditor(Element td,String attachmentindex)
	{
		if(this.getFlag()==null|| "".equalsIgnoreCase(this.getFlag())) {
			this.setFlag("H");
		}
		if(this.getHz()==null) {
			this.setHz("");
		}
		String flag=this.getFlag();
		String field_type=this.getField_type();
		String field_name = this.getField_name(); //xgq 电子签章
//		if(field_name!=null&&field_name.equalsIgnoreCase("signature")){ 
//			flag="S";
//		}
		if("H".equals(flag))//汉字描述
		{		
			Element font=new Element("font");
			font.setAttribute("face",this.getFontname());
			font.setAttribute("style",this.getFontStyle());
			font.setText(getOutText());
			td.addContent(font);				
		}
		else if("A".equals(flag)|| "B".equals(flag)|| "K".equals(flag)) //指标
		{
			//不再屏蔽条件定位为3的情况 朝阳卫生局 2015-09-30
		  if(((this.getHismode()==2||this.getHismode()==3||(this.getHismode()==4))&&this.getChgstate()==1)||(this.subflag))
		  {/**条件1：序号定位| 2：(条件序号&&!最近第&&最初第)&&变化前) 3：子集（subflag=true）**/
			  if(!this.subflag && (this.getHismode()==2 || this.getHismode()==4) && (this.getMode()==0 || this.getMode()==2)){
				//序号定位 、条件定位的最近第、 最初第因为是一条记录  按普通当前记录显示方式显示 wangrd 20151026
				 FieldItem fldItem = DataDictionary.getFieldItem(field_name,this.getSetname());
				 if (fldItem!=null && "M".equalsIgnoreCase(fldItem.getItemtype())){
					 createTextAreaEditor(td); 
				 }
				 else {
					 createInputEditor(td,1);
				 }
			  }
			  else {
				  createDivPanel(td);
			  }
		  }
		  else
		  {
			  if("D".equalsIgnoreCase(field_type))
			  {
					createInputEditor(td,0);			  
			  }
			  else if("N".equalsIgnoreCase(field_type))
			  {
					createInputEditor(td,0);				  
			  }
			  else if("M".equalsIgnoreCase(field_type))
			  {
				  createTextAreaEditor(td);
			  }
			  else if("A".equalsIgnoreCase(field_type))
			  {
				createInputEditor(td,1);
			  }
		  }
		}
		else if("P".equals(flag)) //picture
		{
			createImageEditor(td);
		}
		else if("F".equals(flag)) //attachment
		{
			createAttachmentEditor(td,attachmentindex);
		}	
		else if("V".equals(flag))//临时变量
		{
			createInputVarEditor(td);
		}	
		else if("C".equals(flag))//计算公式 wangrd 2013-12-30
		{            
		    Element text=new Element("input");
	        text.setAttribute("type","text"); 
	        text.setAttribute("id","calcitem_"+String.valueOf(this.getGridno()));
	        text.setAttribute("name","item_calformula");	        
	        text.setAttribute("readonly","true");	                
	        text.setAttribute("value","");	
	        StringBuffer style=new StringBuffer();
            style.append("width:");       
            style.append(this.getRwidth()-2);
            style.append("px;");
            style.append("font-size:");
            style.append(this.getFontsize());
            style.append("pt;text-align:left");
            text.setAttribute("style",style.toString());
	        td.addContent(text);
            
		}
		else if("S".equals(flag))//签章
		{
			Element font=new Element("font");
			font.setAttribute("face",this.getFontname());
			font.setAttribute("style",this.getFontStyle());
			font.setText("&");
			td.addContent(font);
		} 
	}
	/**
	 * 创建历史记录输出面板
	 * @param td
	 */
	private void createDivPanel(Element td) {
		//Element text=new Element("div");
		String[] align=getHValign(this.getAlign()); 
		if(!isSubflag())//字段
		{
			if(this.getField_name()==null) {
				return;
			}
			String field_name=this.getField_name().toLowerCase();//+"_"+this.getChgstate();
			if(this.sub_domain_id!=null&&this.sub_domain_id.length()>0){
				field_name=this.getField_name()+"_"+this.sub_domain_id;
				}
			td.setAttribute("field",field_name+"_"+this.chgstate);
		}
		else{//子集
			if(this.sub_domain_id!=null&&this.sub_domain_id.length()>0){
				td.setAttribute("field","t_"+this.getSetname().toLowerCase()+"_"+this.sub_domain_id+"_"+this.chgstate);
				if(2==this.chgstate)
				{
					td.setAttribute("ondblclick","showSubsets('t_"+this.getSetname().toLowerCase()+"_"+this.sub_domain_id+"_"+this.chgstate+"','"+this.getField_hz()+"',"+this.pagebo.getTabid()+")");
					td.setAttribute("title","请双击编辑子集");
				}
			}else{
				td.setAttribute("field","t_"+this.getSetname().toLowerCase()+"_"+this.chgstate);
				if(2==this.chgstate)
				{
					td.setAttribute("ondblclick","showSubsets('t_"+this.getSetname().toLowerCase()+"_"+this.chgstate+"','"+this.getField_hz()+"',"+this.pagebo.getTabid()+")");
					td.setAttribute("title","请双击编辑子集");
				}
			}
			
		}
		td.setAttribute("nowrap","false");
		String setname="templet_"+pagebo.getTabid();
		td.setAttribute("dataset",setname);
		StringBuffer style=new StringBuffer();
		style.append("font-size:");
		style.append(this.getFontsize());
		style.append("pt;");
		style.append("font-family:"+this.getFontname()+";");

		//style.append("text-align:");
		//style.append(align[0]);
		style.append("vertical-align:");
		if(isSubflag()) {
			style.append("top");
		} else {
			style.append(align[1]);
		}
		td.setAttribute("style",style.toString());
		td.setAttribute("extra","panel");
		//td.addContent(text);
	}
	
	/**
	 * 创建输出框INPUT
	 * @param td
	 */
	private void createInputEditor(Element td,int flag) {
		Element text=new Element("input");
		text.setAttribute("type","text");
		String field_name=this.getField_name().toLowerCase();//+"_"+this.getChgstate();
		if("D".equalsIgnoreCase(this.getField_type())&&this.chgstate==1)
		{
			if(this.getDisformat()>=7&&this.getDisformat()<=23)
			{
				int dis=this.getDisformat();
				if(dis==9) {
					dis=8;
				}
				if(dis==11) {
					dis=10;
				}
				if(dis==23||dis==12) {
					dis=14;
				}
				if(dis==22||dis==13) {
					dis=15;
				}
				field_name+="_"+dis;
			}
		}
		
		FieldItem fielditem=DataDictionary.getFieldItem(field_name);
		String itemmemo="";
		if(fielditem!=null) {
			itemmemo=fielditem.getExplain();
		}
		if(itemmemo==null) {
			itemmemo="";
		}
		itemmemo = itemmemo.replaceAll("\r\n", "\n");
        itemmemo = itemmemo.replaceAll("\n", "<br>");
        if("<br>".equals(itemmemo.trim())) {
			itemmemo="";
		}
		if(this.sub_domain_id!=null&&this.sub_domain_id.length()>0){
			text.setAttribute("field",field_name+"_"+this.sub_domain_id+"_"+this.chgstate);
			}else{
				text.setAttribute("field",field_name+"_"+this.chgstate);
			}
		
		if(this.chgstate==1)
		{
			text.setAttribute("readOnly","true");
		}
		//解决用户管理范围为“”的时候，在界面生成组织机构树控制不住范围的问题
		if("b0110".equalsIgnoreCase(field_name)|| "e01a1".equalsIgnoreCase(field_name)|| "e0122".equals(field_name)|| "parentid".equalsIgnoreCase(field_name)){
		    String xmlvalue=this.getXml_param();
		    Document doc = null;
		    Element element=null;
		    if(xmlvalue!=null&&xmlvalue.trim().length()>5)
	        {
	            try {
	                doc=PubFunc.generateDom(xmlvalue);;
	                String xpath="/sub_para/para";
	                XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
	                List childlist=findPath.selectNodes(doc);   
	                if(childlist!=null&&childlist.size()>0)
	                {
	                    element=(Element)childlist.get(0);
	                    String priv =(String)element.getAttributeValue("limit_manage_priv");
	                    if("1".equals(priv)){
	                        if(!this.userview.isSuper_admin()){
	                            String privcode =this.userview.getManagePrivCode();
	                            if(privcode!=null&&privcode.length()<2) {
									text.setAttribute("disabled","true");
								}
	                        }
	                    }
	                }
	            } catch (JDOMException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
		}
		String setname="templet_"+pagebo.getTabid();
		text.setAttribute("dataset",setname);
		StringBuffer style=new StringBuffer();
//			style.append("height:");
//			style.append(this.getRheight()-4);
//			style.append("px;");
		style.append("width:");
		if(this.isYneed()/*||(this.getChgstate()==2&&itemmemo.length()>0)*/) {
			style.append(this.getRwidth()-15);
		} else {
			style.append(this.getRwidth()-2);
		}
		style.append("px;");
		style.append("font-size:");
		style.append(this.getFontsize());
		String[] aligns=getHValign(this.getAlign()); //bug 34301 7x包60锁input标签内容左对齐，没有按照设置的对齐显示。
		style.append("pt;text-align:"+aligns[0]);
		style.append(";font-family:"+this.getFontname()+";");
		text.setAttribute("style",style.toString());
		text.setAttribute("extra","editor");
		td.addContent(text);
		
		if(this.isYneed())
		{
			Element font=new Element("font");
			font.setAttribute("face",this.getFontname());
			font.setAttribute("style",this.getFontStyle());
			font.setAttribute("color", "red");
			font.setText("*");
			td.addContent(font);				
		}
		if(this.getChgstate()==2&&itemmemo.length()>0)
		{
			//itemmemo=PubFunc.toHtml(itemmemo);
			//itemmemo =itemmemo.replaceAll("\r\n", "\n");
			//itemmemo = itemmemo.replaceAll("\n", "<br>");
			
			text.setAttribute("onmouseout","UnTip()");
			text.setAttribute("onmouseover","Tip(\'"+itemmemo+"\',STICKY ,true)");
			//<a href="index.htm" onmouseover="Tip('Some text')" onmouseout="UnTip()">Homepage </a>			
			//text.setAttribute("title",itemmemo);
			//td.setAttribute("title",itemmemo);
		}
	}
	
	private void createInputVarEditor(Element td) {
		Element text=new Element("input");
		text.setAttribute("type","text");
		String field_name=this.getField_name().toLowerCase();//+"_"+this.getChgstate();
		//bug 37389 7x包60锁这是临时变量只读，已经可以编辑。
		text.setAttribute("field",field_name);
		if(StringUtils.isNotBlank(this.xml_param)){
			try{
				Document doc=PubFunc.generateDom(this.xml_param);;
				Element element=null;
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				String xpath="/sub_para/para";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点		
				element =(Element) findPath.selectSingleNode(doc);
				if(element!=null){
					String readOnly = element.getAttributeValue("readOnly");
					if("1".equals(readOnly)){
						text.setAttribute("readOnly","true");
						text.setAttribute("disabled","true");
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			//text.setAttribute("readOnly","true");
		}
		

		String setname="templet_"+pagebo.getTabid();
		text.setAttribute("dataset",setname);
		StringBuffer style=new StringBuffer();

		style.append("width:");
		if(this.isYneed()) {
			style.append(this.getRwidth()-15);
		} else {
			style.append(this.getRwidth()-2);
		}
		style.append("px;");
		style.append("font-size:");
		style.append(this.getFontsize());
		style.append("pt;text-align:left");
		style.append(";font-family:"+this.getFontname()+";");
		text.setAttribute("style",style.toString());
		text.setAttribute("extra","editor");
		td.addContent(text);
		if(this.isYneed())
		{
			Element font=new Element("font");
			font.setAttribute("face",this.getFontname());
			font.setAttribute("style",this.getFontStyle());
			font.setAttribute("color", "red");
			font.setText("*");
			td.addContent(font);				
		}		
	}	
	private void createTextAreaEditor(Element td) {
		Element text=new Element("textarea");
		String field_name=this.getField_name().toLowerCase();//+"_"+this.getChgstate();
		if(this.sub_domain_id!=null&&this.sub_domain_id.length()>0){
			text.setAttribute("field",field_name+"_"+this.sub_domain_id+"_"+this.chgstate);
			}else{
				text.setAttribute("field",field_name+"_"+this.chgstate);
			}
		String setname="templet_"+pagebo.getTabid();
		text.setAttribute("dataset",setname);
		StringBuffer style=new StringBuffer();
		style.append("height:");
		style.append(this.getRheight()-6);
		style.append("px;");
		style.append("width:");
		if(this.isYneed()) {
			style.append(this.getRwidth()-16);
		} else {
			style.append(this.getRwidth()-2);
		}
		style.append("px;");
		style.append("font-size:");
		style.append(this.getFontsize());
		style.append("pt;text-align:left");
		style.append("font-family:"+this.getFontname()+";");
		text.setAttribute("style",style.toString());
		text.setAttribute("extra","editor");
        String itemmemo="";
        FieldItem fielditem=DataDictionary.getFieldItem(field_name);
        if(fielditem!=null) {
			itemmemo=fielditem.getExplain();
		}
        if(itemmemo==null) {
			itemmemo="";
		}
        itemmemo = itemmemo.replaceAll("\r\n", "\n");
        itemmemo = itemmemo.replaceAll("\n", "<br>");
        if("<br>".equals(itemmemo.trim())) {
			itemmemo="";
		}
        if(this.getChgstate()==2&&itemmemo.length()>0)
        {
            text.setAttribute("onmouseout","UnTip()");
            text.setAttribute("onmouseover","Tip(\'"+itemmemo+"\',STICKY ,true)");
        }         
		td.addContent(text);
		if(this.isYneed())
		{
			Element font=new Element("font");
			font.setAttribute("face",this.getFontname());
			font.setAttribute("style",this.getFontStyle());
			font.setAttribute("color", "red");
			font.setText("*");
			td.addContent(font);				
		}		
	}
	
	/**
	 * 照片
	 * @param td
	 */
	private void createImageEditor(Element td) {
		/*
		Element text=new Element("img");
		text.setAttribute("src","/images/photo.jpg");
		text.setAttribute("height",String.valueOf(this.getRheight()-6));
		text.setAttribute("width",String.valueOf(this.getRwidth()));
		text.setAttribute("onclick","upload(paramter);");  //定义一个空的函数,主要为了上传照片用
		td.addContent(text);
		*/
		Element text=new Element("input");
		text.setAttribute("type","image");
		text.setAttribute("src","/images/photo.jpg");
		text.setAttribute("field","photo");
		String setname="templet_"+pagebo.getTabid();
		text.setAttribute("dataset",setname);
		StringBuffer style=new StringBuffer();
		style.append("height:");
		style.append(this.getRheight()-7);
		style.append("px;");
		style.append("width:");
		style.append(this.getRwidth()-2);
		style.append("px;");
		text.setAttribute("style",style.toString());
		text.setAttribute("extra","editor");
		/**业务类型为0时，才需要上传照片*/
		if(this.operationtype==0)
		{
			text.setAttribute("ondblclick","upload_picture('"+setname+"');");
			text.setAttribute("onclick","upload_picture('"+setname+"');");
		}
		td.addContent(text);		
		
	}
	/**
	 * 附件
	 * @param td
	 */
	private void createAttachmentEditor(Element td, String attachmentindex) {
		String value = "";
		value = "<div id='attachmentid"+attachmentindex+"'>";
		value+=	"<iframe src=\"/general/template/upload_attachment.do?b_query=link\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>";
		value+="</div>";
		Element font=new Element("font");
		font.setAttribute("face",this.getFontname());
		font.setAttribute("style",this.getFontStyle());
		font.setText(value);
		td.addContent(font);
	}
	/**
	 * 签章
	 * @param td
	 */
	private void createSignatureEditor(Element td) {
	
		//Element text=new Element("textarea");
		//String field_name=this.getField_name().toLowerCase();//+"_"+this.getChgstate();
		//text.setAttribute("field",field_name);
	//	Element text=new Element("input");
	//	text.setAttribute("type","text");
		String setname="templet_"+pagebo.getTabid();
	//	text.setAttribute("dataset",setname);
//		StringBuffer style=new StringBuffer();
			td.setAttribute("height", this.getRheight()-6+"");
			td.setAttribute("width", this.getRwidth()-15+"");
//		td.append("height:");
//		style.append(this.getRheight()-6);
//		style.append("px;");
//		style.append("width:");
//		if(this.isYneed())
//			style.append(this.getRwidth()-15);
//		else
//			style.append(this.getRwidth()-2);
//		style.append("px;");
//		style.append("font-size:");
//		style.append(this.getFontsize());
//		style.append("pt;text-align:left");
//		text.setAttribute("style",style.toString());
//		text.setAttribute("extra","editor");
//		text.setAttribute("ondblclick","DoMouseSignature('"+setname+"');");
//		td.addContent(text);
	
		
	}
	/**
	 * 数字换算
	 * @param strV
	 * @param flag
	 * @return
	 */
	private String[] exchangNumToCn(int year,int month,int day)
	{
		String[] strarr=new String[3];
		StringBuffer buf=new StringBuffer();
		String value=String.valueOf(year);
		for(int i=0;i<value.length();i++)
		{
			switch(value.charAt(i))
			{
			case '1':
				buf.append("一");
				break;
			case '2':
				buf.append("二");
				break;
			case '3':
				buf.append("三");
				break;
			case '4':
				buf.append("四");
				break;
			case '5':
				buf.append("五");
				break;
			case '6':
				buf.append("六");
				break;
			case '7':
				buf.append("七");
				break;
			case '8':
				buf.append("八");
				break;
			case '9':
				buf.append("九");
				break;
			case '0':
				buf.append("零");
				break;
			}
		}
		strarr[0]=buf.toString();
		buf.setLength(0);
		switch(month)
		{
		case 1:
			buf.append("一");
			break;
		case 2:
			buf.append("二");
			break;
		case 3:
			buf.append("三");
			break;
		case 4:
			buf.append("四");
			break;
		case 5:
			buf.append("五");
			break;
		case 6:
			buf.append("六");
			break;
		case 7:
			buf.append("七");
			break;
		case 8:
			buf.append("八");
			break;
		case 9:
			buf.append("九");
			break;
		case 10:
			buf.append("十");
			break;			
		case 11:
			buf.append("十一");
			break;
		case 12:
			buf.append("十二");
			break;
		}
		strarr[1]=buf.toString();
		buf.setLength(0);
		switch(day)
		{
		case 1:
			buf.append("一");
			break;
		case 2:
			buf.append("二");
			break;
		case 3:
			buf.append("三");
			break;
		case 4:
			buf.append("四");
			break;
		case 5:
			buf.append("五");
			break;
		case 6:
			buf.append("六");
			break;
		case 7:
			buf.append("七");
			break;
		case 8:
			buf.append("八");
			break;
		case 9:
			buf.append("九");
			break;
		case 10:
			buf.append("十");
			break;			
		case 11:
			buf.append("十一");
			break;
		case 12:
			buf.append("十二");
			break;			
		case 13:
			buf.append("十三");
			break;			
		case 14:
			buf.append("十四");
			break;			
		case 15:
			buf.append("十五");
			break;			
		case 16:
			buf.append("十六");
			break;			
		case 17:
			buf.append("十七");
			break;			
		case 18:
			buf.append("十八");
			break;			
		case 19:
			buf.append("十九");
			break;			
		case 20:
			buf.append("二十");	
			break;			
		case 21:
			buf.append("二十一");
			break;			
		case 22:
			buf.append("二十二");	
			break;			
		case 23:
			buf.append("二十三");
			break;			
		case 24:
			buf.append("二十四");	
			break;			
		case 25:
			buf.append("二十五");
			break;			
		case 26:
			buf.append("二十六");	
			break;			
		case 27:
			buf.append("二十七");
			break;			
		case 28:
			buf.append("二十八");	
			break;			
		case 29:
			buf.append("二十九");
			break;			
		case 30:
			buf.append("三十");	
			break;			
		case 31:
			buf.append("三十一");				
			break;
		}		
		strarr[2]=buf.toString();
		return strarr;
	}
	/**
	 * 计算年龄
	 * @param nyear
	 * @param nmonth
	 * @param nday
	 * @return
	 */
	private String getAge(int nyear,int nmonth,int nday)
	{
		int ncyear,ncmonth,ncday;
		Date curdate=new Date();
		ncyear=DateUtils.getYear(curdate);
		ncmonth=DateUtils.getMonth(curdate);
		ncday=DateUtils.getDay(curdate);
		StringBuffer buf=new StringBuffer();
	
		/*
		double fcage=ncyear+ncmonth*0.01+ncday*0.0001;
		double fage=nyear+nmonth*0.01+nday*0.0001;
		long nage= Math.round(fcage-fage);
		buf.append(nage);*/
		int result =ncyear-nyear;   
        if   (nmonth>ncmonth)   {   
            result = result-1;   
        }   
        else 
        {
            if   (nmonth==ncmonth)  {   
                if   (nday >ncday)   {   
                    result   =   result   -   1;   
                }   
            }   
        }
		buf.append(result);
		return buf.toString();
	}
	/**
	 * 格式化日期字符串
	 * @param value 日期字段值 yyyy-mm-dd
	 * @param ext 扩展
	 * @return
	 */
	private String formatDateValue(String value,String ext)
	{
		StringBuffer buf=new StringBuffer();
		
		
		if(ext!=null&&ext.indexOf("<EXPR>")!=-1)
		{
			 
			int f=ext.indexOf("<EXPR>");
			int t=ext.indexOf("</FACTOR>"); 
			String _temp=ext.substring(0,f);
			String _temp2=ext.substring(t+9);
			ext=_temp+_temp2; 
		}
		
		int idx=ext.indexOf(",");  //-,至今
		String prefix="",strext="";
		if(idx==-1)
		{
			String[] preCond=getPrefixCond(ext);
			prefix=preCond[0];
		}
		else
		{
			prefix=ext.substring(0,idx);
			strext=ext.substring(idx+1);
		}
		if("".equals(value))
		{
			buf.append(prefix);
			buf.append(strext);
			return buf.toString();
		}
		else
		{
			buf.append(prefix);
		}
		value=value.replaceAll("[^(0-9)]", "-");//使用正则强制将非数字替换成-。bugUnparseable date: "2017.05.11"
		if(value.endsWith("-")){
			value=value.substring(0,value.length()-1);
		}
		String fomart="yyyy-MM-dd";//有些日期格式不带日期，导致转换错误
		if(value.split("-").length==2){
			fomart="yyyy-MM";
		}
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(fomart);
	    try {
	        format.setLenient(false);
	        format.parse(value);
	        date = DateUtils.getDate(value,fomart);
	    } catch (Exception e) {
	        return buf.toString();
	    }
		int year=DateUtils.getYear(date);
		int month=DateUtils.getMonth(date);
		int day=DateUtils.getDay(date);
		String strv[]=exchangNumToCn(year,month,day);	
		value=value.replaceAll("-",".");
		switch(this.getDisformat())
		{
		case 6: //1991.12.3
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 7: //91.12.3
			if(year>=2000) {
				buf.append(year);
			} else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 8://1991.2
			buf.append(year);
			buf.append(".");
			buf.append(month);			
			break;
		case 9://1992.02
			buf.append(value.substring(0,7));
			break;
		case 10://92.2
			if(year>=2000) {
				buf.append(year);
			} else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			buf.append(month);
			break;
		case 11://98.02
			if(year>=2000) {
				buf.append(year);
			} else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			if(month>=10) {
				buf.append(month);
			} else
			{
				buf.append("0");
				buf.append(month);
			}
			break;
		case 12://一九九一年一月二日

			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");
			buf.append(strv[2]);
			buf.append("日");
			break;
		case 13://一九九一年一月
			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");			
			break;
		case 14://1991年1月2日
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 15://1991年1月
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			break;
		case 16://91年1月2日
			if(year>=2000) {
				buf.append(year);
			} else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 17://91年1月
			if(year>=2000) {
				buf.append(year);
			} else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append("年");
			buf.append(month);
			buf.append("月");			
			break;
		case 18://年龄
			buf.append(getAge(year,month,day));
			break;
		case 19://1991（年）
			buf.append(year);
			break;
		case 20://1 （月）
			buf.append(month);
			break;
		case 21://23 （日）
			buf.append(day);
			break;
		case 22://1999年02月
			buf.append(year);
			buf.append("年");
			if(month>=10) {
				buf.append(month);
			} else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			break;
		case 23://1999年02月03日
			buf.append(year);
			buf.append("年");
			if(month>=10) {
				buf.append(month);
			} else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			if(day>=10) {
				buf.append(day);
			} else
			{
				buf.append("0");
				buf.append(day);
			}		
			buf.append("日");
			break;
		case 24://1992.02.01
			buf.append(year);
			buf.append(".");
			if(month>=10) {
				buf.append(month);
			} else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append(".");
			if(day>=10) {
				buf.append(day);
			} else
			{
				buf.append("0");
				buf.append(day);
			}		
			break;
		default:
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);			
			break;
		}
		return buf.toString();
	}
	
	
	/**
	 * 从模板表中取数
	 * @param rset
	 * @return
	 */
	private ArrayList getTemplateFieldValue(RowSet rset)
	{
		ArrayList list=new ArrayList();
		String field_type=this.getField_type();
		if(field_type==null|| "".equals(field_type)) {
			return list;
		}
		String field_name=null;	
		String format=this.getFormula();
		boolean flag = false;
		try
		{	
		
			if(rset.isBeforeFirst()){
				rset.next();
				flag = true;
			}
			
			if(format!=null&&format.indexOf("<EXPR>")!=-1)
			{
				 
				int f=format.indexOf("<EXPR>");
				int t=format.indexOf("</FACTOR>"); 
				String _temp=format.substring(0,f);
				String _temp2=format.substring(t+9);
				format=_temp+_temp2; 
			} 
			
//			if(rset.next())
//			{
				if(this.getChgstate()==0) {
					field_name=this.getField_name();
				} else//变化前或变化后指标
				{
					field_name=this.getField_name()+"_"+this.getChgstate();
					if(this.sub_domain_id!=null&&this.sub_domain_id.length()>0){
					field_name=this.getField_name()+"_"+this.sub_domain_id+"_"+this.getChgstate();
					}
				}
				if("M".equalsIgnoreCase(field_type))
				{
					//判断数据字典里的指标类型
					FieldItem item=DataDictionary.getFieldItem(this.getField_name());
					if(item!=null&&item.getItemtype()!=null){
						if("M".equalsIgnoreCase(item.getItemtype())){
							list.add(Sql_switcher.readMemo(rset,field_name));
						}	
						else if("D".equalsIgnoreCase(item.getItemtype()))
						{
							/**yyyy-MM-dd*/
							String str = Sql_switcher.readMemo(rset,field_name);
							String values ="";
							if(str.indexOf("`")!=-1){
								String strs[] =str.split("`");
								for(int i=0;i<strs.length;i++){
									if(strs[i].trim().length()>0){
										values += formatDateValue(strs[i],format);
										if(i<strs.length-1){
											values+="`";
										}
									}
								}
							}else{
								values = formatDateValue(str,format);
							}
							list.add(values);
						}
						else if("N".equalsIgnoreCase(item.getItemtype()))
						{
							int ndec=this.getDisformat();//小数点位数
							String prefix=((format==null)?"":format);
							String str = Sql_switcher.readMemo(rset,field_name);
							String values ="";
							if(str.indexOf("`")!=-1){
								String strs[] =str.split("`");
								for(int i=0;i<strs.length;i++){
									if(strs[i].trim().length()>0){
										values += prefix+PubFunc.DoFormatDecimal(strs[i],ndec);
										if(i<strs.length-1){
											values+="`";
										}
									}
								}
							}else{
								values = prefix+PubFunc.DoFormatDecimal(str,ndec);
							}
							list.add(values);
							
						}else{
						//	if(this.sub_domain_id!=null&&this.sub_domain_id.length()>0){
								String str =rset.getString(field_name);
								String values ="";
								if(str.indexOf("`")!=-1){
									String strs[] =str.split("`");
									for(int i=0;i<strs.length;i++){
										if(strs[i].trim().length()>0){
											if(this.getCodeid()!=null&&!"0".equals(this.getCodeid())){
											values += AdminCode.getCodeName(this.getCodeid(),strs[i]);
											}else {
												values += strs[i];
											}
											if(i<strs.length-1){
												values+="`";
											}
										}
									}
								}else{
									if(this.getCodeid()!=null&&!"0".equals(this.getCodeid())){
									values = AdminCode.getCodeName(this.getCodeid(),str);
									}else {
										values = str;
									}
								}
								list.add(values);
//								}else{
//									list.add(Sql_switcher.readMemo(rset,field_name));
//								}
						}
					}
					
				}
				else if("D".equalsIgnoreCase(field_type))
				{
					/**yyyy-MM-dd*/
					String datevalue=PubFunc.FormatDate(rset.getDate(field_name));
					list.add(formatDateValue(datevalue,format));
				}
				else if("N".equalsIgnoreCase(field_type))
				{
					int ndec=this.getDisformat();//小数点位数
					String prefix=((format==null)?"":format);
					list.add(prefix+PubFunc.DoFormatDecimal(rset.getString(field_name),ndec));
				}
				else //'A'
				{
					String codevalue=rset.getString(field_name);
					codevalue=((codevalue==null)?"":codevalue.trim());					
					if(!this.isBcode()){
						if(field_name!=null&&field_name.startsWith("codesetid_")){
							if("UM".equalsIgnoreCase(codevalue)) {
								codevalue="部门";
							} else if("UN".equalsIgnoreCase(codevalue)) {
								codevalue="单位";
							}
						}
							
						list.add(codevalue);
					}
					else
					{
						if("UM".equalsIgnoreCase(this.getCodeid())&&AdminCode.getCodeName(this.getCodeid(),codevalue).trim().length()==0)
						{
							list.add(AdminCode.getCodeName("UN",codevalue));
						}
						else
						{
							if("UM".equalsIgnoreCase(this.getCodeid())&&Integer.parseInt(display_e0122)>0)
							{
								CodeItem item=AdminCode.getCode("UM",codevalue,Integer.parseInt(display_e0122));
								if(item!=null)
				    	    	{
				    	    		list.add(item.getCodename());
				        		}
				    	    	else
				    	    	{
				    	    		list.add(AdminCode.getCodeName(this.getCodeid(),codevalue));
				    	    	}
							}
							else {
								list.add(AdminCode.getCodeName(this.getCodeid(),codevalue));
							}
						}
					}
				}
//			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
		   try
		   {
			   if(flag) {
				   rset.previous();//回到初态,临时模板中的数据一次性查出,对单个人
			   }
			   
		   }
		   catch(Exception ex)
		   {
			   ex.printStackTrace();
		   }
		}
		return list;
	}

	private ArrayList getTemplateVarFieldValue(RowSet rset)
	{
		ArrayList list=new ArrayList();
		String field_type=this.getField_type();
		if(field_type==null|| "".equals(field_type)) {
			return list;
		}
		String field_name=null;	
		String format=this.getFormula();
		boolean flag = false;
		try
		{
			if(rset.isBeforeFirst()){
				rset.next();
				flag = true;
			}
//			if(rset.next())
//			{
				field_name=this.getField_name();
				if("M".equalsIgnoreCase(field_type))
				{
					list.add(Sql_switcher.readMemo(rset,field_name));
				}
				else if("D".equalsIgnoreCase(field_type))
				{
					/**yyyy-MM-dd*/
					String datevalue=PubFunc.FormatDate(rset.getDate(field_name));
					list.add(formatDateValue(datevalue,format));
				}
				else if("N".equalsIgnoreCase(field_type))
				{
					int ndec=this.getDisformat();//小数点位数
					String prefix=((format==null)?"":format);
					list.add(prefix+PubFunc.DoFormatDecimal(rset.getString(field_name),ndec));
				}
				else //'A'
				{
					String codevalue=rset.getString(field_name);
					codevalue=((codevalue==null)?"":codevalue);					
					if(!this.isBcode()) {
						list.add(codevalue);
					} else
					{
						list.add(AdminCode.getCodeName(this.getCodeid(),codevalue));
					}
				}
//			}
				 if(flag) {
					 rset.previous();//回到初态,临时模板中的数据一次性查出,对单个人
				 }
				 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}

	private String getPattern(String strPattern,String formula)
	{
		int iS,iE;
		String result="";
		String sSP="<"+strPattern+">";
		iS=formula.indexOf(sSP);
		String sEP="</"+strPattern+">";
		iE=formula.indexOf(sEP);
		if(iS>=0 && iS<iE)
		{
			result=formula.substring(iS+sSP.length(), iE);
		}
		return result;
	}	
	/**
	 * 解释Formula字段的内容
	 * for example
	 * ssssfsf<EXPR>1+2</EXPR><FACTOR>A0303=222,A0404=pppp</FACTOR>
	 * @return
	 */
	  public String[] getPrefixCond(String formula)
	  {
		   String[] preCond=new String[3];
		   int idx=formula.indexOf("<");
		   if(idx==-1)
		   {
			   preCond[0]=formula; 
		   }
		   else
		   {
			   preCond[0]=formula.substring(0, idx);
			   preCond[2]=getPattern("FACTOR",formula)+",";
			   preCond[2]=preCond[2].replaceAll(",", "`");
			   preCond[1]=getPattern("EXPR",formula);
		   }
		   return preCond;
	  }	
	/**
	 * 解释Formula字段的内容
	 * for example
	 * ssssfsf,uu<EXPR>1+2</EXPR><FACTOR>A0303=222,A0404=pppp</FACTOR>
	 * @return
	 */
   private String[] getPrefixCond()
   {
	   String[] preCond=new String[3];
	   StringBuffer buf=new StringBuffer();
	   buf.append("<?xml version='1.0' encoding='GB2312'?>");
	   buf.append("<formula>");
	   buf.append(this.getFormula());
	   buf.append("</formula>");
	   try
	   {
		   Document doc=PubFunc.generateDom(buf.toString());;
		   Element root=doc.getRootElement();
		   preCond[0]=root.getText();
		   List list=XPath.selectNodes(root,"//EXPR");
		   if(list!=null&&list.size()>0)
		   {
			   Element expr=(Element)list.get(0);
			   preCond[1]=expr.getText();
			   
		   }
		   list=XPath.selectNodes(root,"//FACTOR");
		   if(list!=null&&list.size()>0)
		   {
			   Element factor=(Element)list.get(0);
			   String strf=factor.getText();
			   strf=strf.replaceAll(",","`");
			   preCond[2]=strf;//factor.getText();
		   }
	   }
	   catch(Exception ex)
	   {
		   ex.printStackTrace();
	   }
	   return preCond;
   }
	/**
	 * 取得所有子集序号列表
	 * @param subtab
	 * @param a0100
	 * @return
	 */
	private ArrayList getSubSetI9999s(String subtab,String a0100)
	{
		ArrayList paralist=new ArrayList();
		paralist.add(a0100);
		
		String fieldname="a0100";
		if(this.tablebo.getInfor_type()==2) {
			fieldname="b0110";
		} else if(this.tablebo.getInfor_type()==3) {
			fieldname="e01a1";
		}
		StringBuffer buf=new StringBuffer();
		buf.append("select I9999 from ");
		buf.append(subtab);
		buf.append(" where "+fieldname+"=? order by I9999");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString(),paralist);
			paralist.clear();
			while(rset.next()) {
				paralist.add(rset.getString("I9999"));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return paralist;
	}
	/**
	 * 取得对应的数据
	 * @param rset
	 * @param fielditem
	 * @return
	 */
	private ArrayList getFieldValue(RowSet rset,FieldItem fielditem)
	{
		ArrayList list=new ArrayList();
		String field_type=fielditem.getItemtype();
		String field_name=fielditem.getItemid();
		String format=this.getFormula();
		try
		{
			while(rset.next())
			{
				if("M".equalsIgnoreCase(field_type))
				{
					list.add(Sql_switcher.readMemo(rset,field_name));
				}
				else if("D".equalsIgnoreCase(field_type))
				{
					/**yyyy-MM-dd*/
					String datevalue=PubFunc.FormatDate(rset.getDate(field_name));

					list.add(formatDateValue(datevalue,format));
				}
				else if("N".equalsIgnoreCase(field_type))
				{
					int ndec=this.getDisformat();//小数点位数
					String prefix=((format==null)?"":format);
					if(this.getHismode()==3||this.getHismode()==4) {
						prefix="";
					}
					list.add(prefix+PubFunc.DoFormatDecimal(rset.getString(field_name),ndec));
				}
				else //'A'
				{
					String codevalue=rset.getString(field_name);
					codevalue=((codevalue==null)?"":codevalue.trim());
					if(!this.isBcode()) {
						list.add(codevalue);
					} else
					{
						if("UM".equalsIgnoreCase(this.getCodeid())&&AdminCode.getCodeName(this.getCodeid(),codevalue).trim().length()==0)
						{
							list.add(AdminCode.getCodeName("UN",codevalue));
						}
						else
						{
							if("UM".equalsIgnoreCase(this.getCodeid())&&Integer.parseInt(display_e0122)>0)
							{
								CodeItem item=AdminCode.getCode("UM",codevalue,Integer.parseInt(display_e0122));
								if(item!=null)
				    	    	{
				    	    		list.add(item.getCodename());
				        		}
				    	    	else
				    	    	{
				    	    		list.add(AdminCode.getCodeName(this.getCodeid(),codevalue));
				    	    	}
							}
							else {
								list.add(AdminCode.getCodeName(this.getCodeid(),codevalue));
							}
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 从档案库取数据
	 * @param dbpre 应用库前缀
	 * @param a0100 人员编码
	 * @return
	 */
	private ArrayList getArchiveFieldValue(String dbpre,String a0100)
	{
		ArrayList strlist=new ArrayList();
		try
		{
			FieldItem fielditem=DataDictionary.getFieldItem(this.getField_name());
			if(fielditem==null|| "0".equals(fielditem.getUseflag())) {
				return strlist;
			}
			/**子集*/
			String subcode=fielditem.getFieldsetid();
			String ssubtab=dbpre+subcode;
			if(this.tablebo.getInfor_type()==2||this.tablebo.getInfor_type()==3) {
				ssubtab=subcode;
			}
			String fieldname=fielditem.getItemid();
			/**无读、写权限*/
			if(this.tablebo!=null&&this.tablebo.isBEmploy())
			{
				if("0".equals(this.userview.analyseFieldPriv(fieldname,0))&& "0".equals(this.tablebo.getUnrestrictedMenuPriv_Input()))
				{
					if("0".equals(this.userview.analyseFieldPriv(fieldname))&& "0".equals(this.tablebo.getUnrestrictedMenuPriv_Input())) {
						return strlist;
					}
				}
			}
			else
			{
				if("0".equals(this.userview.analyseFieldPriv(fieldname))&&(this.tablebo!=null&& "0".equals(this.tablebo.getUnrestrictedMenuPriv_Input()))) {
					return strlist;
				}
			}
			StringBuffer buf=new StringBuffer();
			ArrayList paralist=new ArrayList();
			/**人员*/
		//	if(fielditem.isPerson())
			{
				buf.append("select ");
				buf.append(fieldname);
				buf.append(" from ");
				buf.append(ssubtab);			
				if(this.tablebo.getInfor_type()==1) {
					buf.append(" where a0100=?");
				} else if(this.tablebo.getInfor_type()==2) {
					buf.append(" where b0110=?");
				} else if(this.tablebo.getInfor_type()==3) {
					buf.append(" where e01a1=?");
				}
				if(fielditem.isMainSet())
				{
					paralist.add(a0100);
				}
				else
				{
					paralist.add(a0100);					
					/**按序号定位,*/
					if(this.getHismode()==2||this.getHismode()==1)
					{
						/**求子集序号列表*/
						ArrayList i9999list=getSubSetI9999s(ssubtab,a0100);
						int size=i9999list.size();
						if(size>0)
						{
							/**初值为-1*/
							String curri9999="-1";
							switch(this.getMode())
							{
							case 0://倒数第...条（最近第）
								if(this.getHismode()==1)//当前记录，也为一条记录
								{
									if(size>=1) {
										curri9999=(String)i9999list.get(size-1);
									}
								}
								else
								{
									if(size>=this.getRcount())//子集记录大于要取的的记录数
									{
										if(this.getRcount()==0)//兼容，有的库中数据此值为0
										{
											this.setRcount(1);
										}
										if(size==this.getRcount()) {
											curri9999=(String)i9999list.get(0);
										} else {
											curri9999=(String)i9999list.get(size-this.getRcount());
										}
									}
								}
								buf.append(" and I9999=?");							
								paralist.add(curri9999);							
								break;
							case 1://倒数...条（最近）
								if(size>=this.getRcount())
								{
									if(size==this.getRcount()) {
										curri9999=(String)i9999list.get(0);
									} else {
										curri9999=(String)i9999list.get(size-this.getRcount());
									}
								}
								buf.append(" and I9999>=? order by I9999");
								paralist.add(curri9999);							
								break;
							case 2://正数第...条(最初第)
								if(size>=this.getRcount()) {
									curri9999=(String)i9999list.get(this.getRcount()-1);
								}
								buf.append(" and I9999=?");							
								paralist.add(curri9999);							
								break;
							case 3://正数...条（最初）
								if(size>=this.getRcount()) {
									curri9999=(String)i9999list.get(this.getRcount()-1);
								}
								buf.append(" and I9999<=? order by I9999");
								paralist.add(curri9999);							
								break;
							}
						}
					}
					else//按条件定位 ==3
					{
						String[] preCond=getPrefixCond(this.getFormula());
						String cond=preCond[1];
					//	this.setFormula(preCond[0]);
						if(cond!=null&&cond.length()>0)
						{
							FactorList factorlist=new FactorList(preCond[1],preCond[2],"");
							String strw=factorlist.getSingleTableSqlExpression(ssubtab);
							buf.append(" and (");
							buf.append(strw);
							buf.append(") order by I9999");
						}
						else
						{
							buf.append(" and 1=2");
						}
					}
					
				}
			}
			/**单位*/
			if(fielditem.isOrg())
			{
				
			}
			/**职位*/
			if(fielditem.isPos())
			{
				
			}
			
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString(),paralist);
			
			strlist=getFieldValue(rset,fielditem);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return strlist;
	}
	/**
	 * 实际取数
	 * A：人员库B：单位库K:职位库P：照片H：文本C：计算结果V:临时变量
	 * @param dbpre 库前缀
	 * @param a0100 人员编号
	 * @param rset 业务模板对应的此人的数据
	 * @param userview 登录用户对象
	 * @return
	 */
	public String getCellContent(String dbpre,String a0100,RowSet rset,UserView userview,TemplateTableBo tablebo)
	{
		String strc="";
		
		if(this.getNhide()==0)
		{
			boolean bdr=false;
			if(dbpre==null|| "".equals(dbpre)) {
				bdr=true;
			}
			this.userview=userview;
			this.tablebo=tablebo;
			if(this.getFlag()==null|| "".equalsIgnoreCase(this.getFlag())) {
				this.setFlag("H");
			}
			if(this.getHz()==null) {
				this.setHz("");
			}
			char cflag=this.getFlag().charAt(0);
			ArrayList strlist=new ArrayList();
			StringBuffer buf=new StringBuffer();
			int i=0;
			switch(cflag)
			{
				case 'A': //人员库
				case 'B'://单位库
				case 'K'://职位库
					/**历史记录，从档案子集直接取数
					 * =2多条 =3条件定位 
					 * */
					if((this.getHismode()==2||this.getHismode()==3||this.getHismode()==4)&&(!bdr)&&this.getChgstate()==1)//去掉this.isFinished_record,这里都不动态去库（人员库 ）里的值了 
					{
						//strlist=getArchiveFieldValue(dbpre,a0100);
						 strlist=getTemplateFieldValue(rset);
						 if(strlist.size()>0){
						 String str[] = strlist.get(0).toString().split("`");
						 strlist.clear();
						 for(int n =0;n<str.length;n++){
							 if(str[n].length()>0) {
								 strlist.add(str[n]);
							 }
						 }
						 }
					}
					else {
						strlist=getTemplateFieldValue(rset);
					}
					for(i=0;i<strlist.size();i++)
					{
						buf.append(strlist.get(i));
						if(i<strlist.size()) {
							buf.append("\n");
						}
					}//for i loop end.				
					strc=buf.toString();
					break;				
				case 'C'://计算公式
					break;
				case 'V'://变量
					strlist=getTemplateVarFieldValue(rset);
					for(i=0;i<strlist.size();i++)
					{
						buf.append(strlist.get(i));
						if(i<strlist.size()) {
							buf.append("\n");
						}
					}//for i loop end.
					strc=buf.toString();
					break;
				case 'P'://照片
					if(this.operationtype!=0)
					{
						//String a00tab=dbpre+"A00";
						//strc=createPhotoFile(a00tab,a0100,"P");
						strc=createPhotoFile(rset);
					}
					else
					{
						strc=createPhotoFile(rset);
	
					}
					break;
				case 'S'://电子签章
					break;
				default://'H'
					strc=this.getHz().replaceAll("`","\n");    //StringUtils.split(this.getHz(),"`");
					break;
			}
		}
		return strc;
	}
	
	/**
	 * 创建照片
	 * @param userTable
	 * @param userNumber
	 * @param flag
	 * @return
	 * @throws Exception
	 */
    public  String createPhotoFile(String a00tab, String a0100, String flag) {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        ResultSet rs = null;  
        PreparedStatement pstmt=null;
        InputStream in = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select ext,Ole from ");
            strsql.append(a00tab);
            strsql.append(" where A0100='");
            strsql.append(a0100);
            strsql.append("' and Flag='");
            strsql.append(flag);
            strsql.append("'");

            pstmt=conn.prepareStatement(strsql.toString());
            rs=pstmt.executeQuery();   
            if (rs.next()) {
                java.io.FileOutputStream fout = null;
                try {
                	tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rs.getString("ext"),
                			new File(System.getProperty("java.io.tmpdir")));             
                	in = rs.getBinaryStream("Ole");                
                	fout = new java.io.FileOutputStream(tempFile);                
                	int len;
                	byte buf[] = new byte[1024];
                	
                	while ((len = in.read(buf, 0, 1024)) != -1) {
                		fout.write(buf, 0, len);
                		
                	}
                } finally {
                	PubFunc.closeDbObj(fout);
                }
               
                filename= tempFile.getName();                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeIoResource(in);
        	PubFunc.closeResource(rs);
        	PubFunc.closeResource(pstmt);
        }
        return filename;
    }
	
	/**
	 * 创建照片
	 * @param userTable
	 * @param userNumber
	 * @param flag
	 * @return
	 * @throws Exception
	 */
    public  String createPhotoFile(RowSet rset) {
        File tempFile = null;
        String filename="";
        ServletUtilities.createTempDir();
        boolean flag = false;
        InputStream in =null;
        FileOutputStream fout =null;
        try {

        	if(rset.isBeforeFirst()){
				rset.next();
				flag = true;
			}
                String ext=rset.getString("ext");
                if(ext==null|| "".equalsIgnoreCase(ext))
                {
					rset.previous();                
                	return "";
                }
                tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, rset.getString("ext"),
                        new File(System.getProperty("java.io.tmpdir")));             
//                in = rset.getBinaryStream("photo");                
                String fileid=rset.getString("fileid");
                if(StringUtils.isNotBlank(fileid)) {
                	if("2".equals(VfsService.getFileEntity(fileid).getStatus())) {//文件存在
                		in = VfsService.getFile(fileid);
                	}
                }
                fout = new FileOutputStream(tempFile);                
                if(in!=null) {
                	int len;
                    byte buf[] = new byte[1024];
				    while ((len = in.read(buf, 0, 1024)) != -1) {
					    fout.write(buf, 0, len);
				    }
			    }
               
                filename= tempFile.getName();
                
                if(flag) {
					rset.previous();//回到初态,临时模板中的数据一次性查出,对单个人
				}
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
        	PubFunc.closeIoResource(fout);
        	PubFunc.closeIoResource(in);
        }
        return filename;
    }
    
	/**
	 * 创建HTML对象
	 * @param element
	 * @param userview
	 */
	public void createCellView(Element element,UserView userview,String attachmentindex)
	{
		String field_name = this.getField_name(); //xgq 电子签章
		String  flag =	this.getFlag();
		Element div=new Element("div");
		if("S".equals(flag)){//S:数据来源标识 代表这电子签章
			div.setAttribute("id","signature"+this.getPagebo().getPageid()+"S"+this.signnumber);
			StringBuffer style=new StringBuffer();
			if(this.getRtop()!=rect.y)
			{
				style.append("top:");
				style.append(this.getRtop()-1);
				style.append(";");			
			}
			else
			{
				style.append("top:");
				style.append(this.getRtop());
				style.append(";");	
			}
			if(this.getRleft()!=rect.x)
			{
				style.append("left:");
				style.append(this.getRleft()-1);
				style.append(";");
			}
			else
			{
				style.append("left:");
				style.append(this.getRleft());
				style.append(";");
			}
			style.append("height:");
			style.append(this.getRheight()+1);
			style.append("px;");
			style.append("width:");
			if(this.isYneed()) {
				style.append(this.getRwidth());
			} else {
				style.append(this.getRwidth());
			}
			style.append("px;");
			style.append("font-size:");
			style.append(this.getFontsize());
			style.append("pt;text-align:left");
			style.append("font-family:"+this.getFontname()+";");
			div.setAttribute("style",style.toString());
		}
		Element table=new Element("table");
		table.setAttribute("border","0");

		table.setAttribute("style",getTablePos());
		table.setAttribute("cellspacing","0");
		//table.setAttribute("class","ft");
		//table.setAttribute("table-layout","fixed");
		Element tr=new Element("tr");
		Element td=new Element("td");
		String[] align=getHValign(this.getAlign()); 
		
		if(this.getRtop()!=rect.y) {
			td.setAttribute("height", String.valueOf(this.getRheight()+1));
		} else {
			td.setAttribute("height", String.valueOf(this.getRheight()));
		}
		if(this.getRleft()!=rect.x) {
			td.setAttribute("width", String.valueOf(this.getRwidth()+1));
		} else {
			td.setAttribute("width", String.valueOf(this.getRwidth()));
		}
	//	td.setAttribute("height", String.valueOf(this.getRheight()));
	//	td.setAttribute("width", String.valueOf(this.getRwidth()));
		
		td.setAttribute("valign",align[1]);
		if(this.isSubflag()) {
			td.setAttribute("align","left");//由于60锁子集表格控件引用的是ext4，而表格控件目前基本都在ext6的基础上在修改优化，导致在ext4下当其渲染的td设置水平居中	，
		}
											//表格控件的列会被设置成居中，导致列错位，固此处不修改表格控件了,在此处直接默认子集水平居左
		else {
			td.setAttribute("align",align[0]);
		}
		td.setAttribute("nowrap","false");		
		td.setAttribute("class",getBorderLineCss(this.rect));//没有边框线
		
		createEditor(td,attachmentindex);//在td里面创建input,textarea,font等标签
		if("S".equals(flag)){
			String setname="templet_"+pagebo.getTabid();
			td.setAttribute("ondblclick","DoMouseSignature('"+setname+"','signature"+this.getPagebo().getPageid()+"S"+this.signnumber+"',"+this.getPagebo().getPageid()+")");
			td.setAttribute("title","请双击进行签章");
		}
		tr.addContent(td);
		table.addContent(tr);
		
		if("S".equals(flag)){
			div.addContent(table);
			element.addContent(div);	
		}else{
			element.addContent(table);	
		}
	}
	/**
	 * 子集中格式化日期字符串
	 * @param value 日期字段值 yyyy-mm-dd
	 * @param ext 扩展
	 * @return
	 */
	public String formatDateFiledsetValue(String value,String ext ,int disformat)
	{
		StringBuffer buf=new StringBuffer();
		int idx=ext.indexOf(",");  //-,至今
		String prefix="",strext="";
		if(idx==-1)
		{
			String[] preCond=getPrefixCond(ext);
			prefix=preCond[0];
		}
		else
		{
			prefix=ext.substring(0,idx);
			strext=ext.substring(idx+1);
		}
		if("".equals(value))
		{
			buf.append(prefix);
			buf.append(strext);
			return buf.toString();
		}
		else
		{
			buf.append(prefix);
		}
		Date date=DateUtils.getDate(value,"yyyy-MM-dd");
		int year=DateUtils.getYear(date);
		int month=DateUtils.getMonth(date);
		int day=DateUtils.getDay(date);
		String strv[]=exchangNumToCn(year,month,day);	
		value=value.replaceAll("-",".");
		switch(disformat)
		{
		case 0: //1991.12.3
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 1: //91.12.3
			if(year>=2000) {
				buf.append(year);
			} else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 2://1991.2
			buf.append(year);
			buf.append(".");
			buf.append(month);			
			break;
		case 3://1992.02
            buf.append(year);
            buf.append(".");            
            if (month>9){                
                buf.append(month);  
            }
            else {
                buf.append("0"+month);    
            }
			
			break;
		case 4://92.2
			if(year>=2000) {
				buf.append(year);
			} else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			buf.append(month);
			break;
		case 5://98.02
			if(year>=2000) {
				buf.append(year);
			} else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append(".");
			if(month>=10) {
				buf.append(month);
			} else
			{
				buf.append("0");
				buf.append(month);
			}
			break;
		case 6://一九九一年一月二日

			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");
			buf.append(strv[2]);
			buf.append("日");
			break;
		case 7://一九九一年一月
			buf.append(strv[0]);
			buf.append("年");
			buf.append(strv[1]);
			buf.append("月");			
			break;
		case 8://1991年1月2日
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 9://1991年1月
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			break;
		case 10://91年1月2日
			if(year>=2000) {
				buf.append(year);
			} else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 11://91年1月
			if(year>=2000) {
				buf.append(year);
			} else
			{
				String temp=String.valueOf(year);
				buf.append(temp.substring(2));
			}
			buf.append("年");
			buf.append(month);
			buf.append("月");			
			break;
		case 12://年龄
			buf.append(getAge(year,month,day));
			break;
		case 13://1991（年）
			buf.append(year);
			break;
		case 14://1 （月）
			buf.append(month);
			break;
		case 15://23 （日）
			buf.append(day);
			break;
		case 16://1999年02月
			buf.append(year);
			buf.append("年");
			if(month>=10) {
				buf.append(month);
			} else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			break;
		case 17://1999年02月03日
			buf.append(year);
			buf.append("年");
			if(month>=10) {
				buf.append(month);
			} else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			if(day>=10) {
				buf.append(day);
			} else
			{
				buf.append("0");
				buf.append(day);
			}		
			buf.append("日");
			break;
		case 18://1992.02.01
			buf.append(year);
			buf.append(".");
			if(month>=10) {
				buf.append(month);
			} else
			{
				buf.append("0");
				buf.append(month);
			}
			buf.append(".");
			if(day>=10) {
				buf.append(day);
			} else
			{
				buf.append("0");
				buf.append(day);
			}		
			break;
		default:
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);			
			break;
		}
		return buf.toString();
	}

	public Rectangle getRect() {
		return rect;
	}
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
	public void setUserview(UserView userview) {
		this.userview = userview;
	}
	public String getSub_domain_id() {
		return sub_domain_id;
	}
	public void setSub_domain_id(String sub_domain_id) {
		this.sub_domain_id = sub_domain_id;
	}
	public void setOperationtype(int operationtype) {
		this.operationtype = operationtype;
	}
	public String getPreCon() {
		return preCon;
	}
	public void setPreCon(String preCon) {
		this.preCon = preCon;
	}
	public boolean isFinished_record() {
		return isFinished_record;
	}
	public void setFinished_record(boolean isFinished_record) {
		this.isFinished_record = isFinished_record;
	}
	public int getSignnumber() {
		return signnumber;
	}
	public void setSignnumber(int signnumber) {
		this.signnumber = signnumber;
	}
	public String getAttachmentType() {
		return attachmentType;
	}
	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}
	public String getRelation_field() {
		return relation_field;
	}
	public void setRelation_field(String relation_field) {
		this.relation_field = relation_field;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void parserSubDomain(String sub_domain) {
		SubSetDomain subdomain=new SubSetDomain(this.xml_param);
		this.defaultValue=subdomain.getDefault_value();//默认值
		this.relation_field=subdomain.getRelation_field();//联动指标
	}
}
