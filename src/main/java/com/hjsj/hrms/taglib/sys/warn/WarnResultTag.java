package com.hjsj.hrms.taglib.sys.warn;

import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.transaction.sys.warn.ScanTotal;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.ArrayList;

/* 
 * 预警信息标签
 * usage:
 * <hrms:warnInfo style="panel" width="300px" height="16px" scrolldelay="4000"></hrms:warnInfo></td>
 */
public class WarnResultTag extends TagSupport implements IConstant {
	private String width;
	private String height;
	private String scrolldelay;
	private String style;
	
//	private static String InfoJspPanel="system/warn/info_panel.jsp";
//	private static String InfoJspScrollLine="system/warn/info_scrollline.jsp";

	/*
	 * 预警结果内容输出
	 */
	public int doStartTag() throws JspException {
		
		if( getStyle()==null || "panel".equalsIgnoreCase(getStyle())){
			outputHtml();
			//outputPanel();
			//copyJsp2Tag(InfoJspPanel);
			
		}else if( "scrollLine".equalsIgnoreCase(getStyle())){
			outputScollLine();
			//copyJsp2Tag(InfoJspScrollLine);
		}
		
		return super.doStartTag();
	}
	
	private void outputHtml(){
		UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		ScanTotal st = new ScanTotal( userview );
		ArrayList alTotal = st.execute();
		
		try {
			int iRows = alTotal.size()>5?5:alTotal.size();			
			if(iRows>0)
				pageContext.getOut().println("<table width=\"99%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" class=\"ListTable\">");
   			    //pageContext.getOut().println("<table width=\"99%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >");

			for( int i=0; i<iRows; i++){
				CommonData cData = (CommonData)alTotal.get(i);
				pageContext.getOut().println("		<tr class=\""+(i%2==0?"trDeep":"trDeep")+"\"><td class=\"RecordRow\">");
				//pageContext.getOut().println("		<tr ><td>");

				pageContext.getOut().println("		<img src=\"/images/forumme.gif\"> <a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+ cData.getDataValue()+"\">"+/*(i+1)+". "+*/cData.getDataName()+"</a>");
				pageContext.getOut().println("		</td></tr>");
			}
			if( alTotal.size()>5 ){
				pageContext.getOut().println("		<tr class=\"trDeep\"><td class=\"RecordRow\" align=\"right\"><a href=\"/system/warn/info_all.do?br_query=link\">>>更多(共"+alTotal.size()+"项)</a></td></tr>");
				//pageContext.getOut().println("		<tr ><td  align=\"right\"><a href=\"/system/warn/info_all.do?br_query=link\">>>更多(共"+alTotal.size()+"项)</a></td></tr>");

			}
			if(iRows>0)
				pageContext.getOut().println("</table>");
			
		} catch (IOException e) {	
			e.printStackTrace();
		}
	}
	
	
	
	
/*	public void copyJsp2Tag(String strFileName){
		StringBuffer sb = (StringBuffer)ContextTools.getJvmCache().get( strFileName);
		if( sb == null ){
			// 因为打开文件非常消耗系统资源，所以必须缓存预警信息的jsp内容
			sb = new StringBuffer();
			ContextTools.getJvmCache().put(strFileName,sb);
			
			try {
				String path = pageContext.getServletConfig().getServletContext().getRealPath(strFileName);
				File f = new File(path);
				Reader r = new FileReader(f);
				BufferedReader br = new BufferedReader(r);
				String strLine = null;
				while( (strLine=br.readLine()) !=null){
					sb.append(strLine);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			pageContext.getOut().println(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
	
/*	private void outputPanel(){
		try {
			pageContext.getOut().println("<div id=warnResultShow>" );
			pageContext.getOut().println("<table id='warnInfoTable' width=\""+getWidth()+"\"height=\""+getHeight()+"\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" class=\"ListTable\">" );
//			pageContext.getOut().println("<thead>" );
//			pageContext.getOut().println("	<tr>" );
//			pageContext.getOut().println("		<td align=\"center\" class=\"TableRow\" nowrap>预警信息</bean:message></td>" );
//			pageContext.getOut().println("	</tr>" );
//			pageContext.getOut().println("</thead>" );
			pageContext.getOut().println("<script>" );
			pageContext.getOut().println("var marqueeContent=new Array();");
			pageContext.getOut().println("var marqueeInterval=new Array();" );
			pageContext.getOut().println("" );
			pageContext.getOut().println("//接下来的是定义一些要使用到的函数" );
			pageContext.getOut().println("function initMarquee(outparamters){" );
			pageContext.getOut().println("	var tempArray=outparamters.getValue(\""+Key_FormMap_UserView_Result+"\");" );
			pageContext.getOut().println("	if(typeof(tempArray)==\"undefined\"||tempArray.length<1){" );
			pageContext.getOut().println("		Element.hide('warnResultShow');" );
			pageContext.getOut().println("		return;");
			pageContext.getOut().println("	}");

			pageContext.getOut().println("	var lines=tempArray.length>10?10:tempArray.length;");
			pageContext.getOut().println("	for(var i=0;i<lines;i++) {");
			pageContext.getOut().println("		marqueeContent[i]='<font siz=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid='+tempArray[i].dataValue+'\">'+(i+1)+'. '+tempArray[i].dataName+'</a></font>';");
			pageContext.getOut().println("		document.write('<tr class=\"'+(i%2==0?'trDeep':'trShallow')+'\"><td class=\"RecordRow\">'+marqueeContent[i]+'</td></tr>');");
			pageContext.getOut().println("	}");
			pageContext.getOut().println("	if(tempArray.length>10){");
			pageContext.getOut().println("		document.write('<tr class=\"'+(i%2==0?'trDeep':'trShallow')+'\"><td class=\"RecordRow\" align=\"right\"><a href=\"/system/warn/info_all.do?br_query=link\">>>更多(共'+tempArray.length+'项)</a></td></tr>');");
			pageContext.getOut().println("	}");
			pageContext.getOut().println("}");

			pageContext.getOut().println("function initScan() {" );
			pageContext.getOut().println("	var tatolPars=\"isRole=true\";" );
			pageContext.getOut().println("	var request=new Request({method:'post',asynchronous:false,parameters:tatolPars,onSuccess:initMarquee,functionId:'1010020307'});" );
			pageContext.getOut().println("}" );
			
			pageContext.getOut().println("</script>");
			pageContext.getOut().println("<script language=\"javascript\">");
			pageContext.getOut().println("	initScan();" );
//			不能定时刷新，sessino过期的冲突
//			pageContext.getOut().println("	setInterval(\"initScan()\",1000*60*10);" );
			pageContext.getOut().println("</script>" );
			pageContext.getOut().println("</table>" );
			pageContext.getOut().println("</div>" );
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
*/	
	/*
	 * 滚动字幕格式
	 * 实用ajax前台异步查询
	 */
	private void outputScollLine(){		
		try {
			pageContext.getOut().println("<div id=warnResultShow>" );
			pageContext.getOut().println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"0\" class=\"ListTable\">" );
			pageContext.getOut().println("<tr>" );
			pageContext.getOut().println("	<td>" );
			pageContext.getOut().println("<script>" );
			pageContext.getOut().println("var marqueeContent=new Array();");
			pageContext.getOut().println("var marqueeInterval=new Array();" );
			pageContext.getOut().println("var marqueeId=0;" );
			pageContext.getOut().println("var isStopScoll=false;" );
			pageContext.getOut().println("var marqueeDelay='"+getScrolldelay()+"';" );
			pageContext.getOut().println("var marqueeHeight='"+getHeight()+"';" );
			pageContext.getOut().println("var marqueeWidth='"+getWidth()+"';" );
			pageContext.getOut().println("" );
			pageContext.getOut().println("//接下来的是定义一些要使用到的函数" );
			pageContext.getOut().println("function initMarquee(outparamters){" );
			pageContext.getOut().println("	var tempArray=outparamters.getValue(\""+Key_FormMap_UserView_Result+"\");" );
			pageContext.getOut().println("	if(typeof(tempArray)==\"undefined\"||tempArray.length<1){" );
			pageContext.getOut().println("		Element.hide('warnResultShow');" );
			pageContext.getOut().println("		return;");
			pageContext.getOut().println("	}");
			pageContext.getOut().println("	for(var i=0;i<tempArray.length;i++) {");
			pageContext.getOut().println("		marqueeContent[i]='<font siz=1><a href=\"/system/warn/result_manager.do?b_query=link&warn_wid='+tempArray[i].dataValue+'\">'+tempArray[i].dataName+'</a></font>';");
			pageContext.getOut().println("	}");
			pageContext.getOut().println("	var str=''+marqueeContent[0];" );
			pageContext.getOut().println("	document.write('<div id=marqueeBox style=\"overflow:hidden;height:'+marqueeHeight+'px;width:'+marqueeWidth+'px\" onmouseover=\"stopScroll()\" onmouseout=\"startScroll()\"><div>'+str+'</div></div>');" );
			pageContext.getOut().println("	if(marqueeContent.length>0)marqueeInterval[0]=setInterval(\"startMarquee()\",marqueeDelay);" );
			pageContext.getOut().println("}" );
			pageContext.getOut().println("" );

			pageContext.getOut().println("function stopScroll() {" );
			pageContext.getOut().println("	isStopScoll=true;" );
			pageContext.getOut().println("}" );
			pageContext.getOut().println("" );
			pageContext.getOut().println("function startScroll() {" );
			pageContext.getOut().println("	isStopScoll=false;" );
			pageContext.getOut().println("}" );
			pageContext.getOut().println("" );

			pageContext.getOut().println("function reinitMarquee() {" );
			pageContext.getOut().println("	if(isStopScoll==true)return;");
			pageContext.getOut().println("	var str=marqueeContent[0];" );
			pageContext.getOut().println("	marqueeBox.childNodes[(marqueeBox.childNodes.length==1?0:1)].innerHTML=str;" );
			pageContext.getOut().println("	marqueeId=0;" );
			pageContext.getOut().println("}" );
			pageContext.getOut().println("" );
			pageContext.getOut().println("function startMarquee() {" );
			pageContext.getOut().println("	if(isStopScoll==true)return;");
			pageContext.getOut().println("	marqueeId+=1;" );
			pageContext.getOut().println("	if(marqueeId>=marqueeContent.length)marqueeId=0;" );
			pageContext.getOut().println("	var str=marqueeContent[marqueeId];" );
			pageContext.getOut().println("" );
			pageContext.getOut().println("	if(marqueeBox.childNodes.length==1) {" );
			pageContext.getOut().println("		var nextLine=document.createElement('DIV');" );
			pageContext.getOut().println("		nextLine.innerHTML=str;" );
			pageContext.getOut().println("		marqueeBox.appendChild(nextLine);" );
			pageContext.getOut().println("	}else{" );
			pageContext.getOut().println("		marqueeBox.childNodes[0].innerHTML=str;" );
			pageContext.getOut().println("		marqueeBox.appendChild(marqueeBox.childNodes[0]);" );
			pageContext.getOut().println("		marqueeBox.scrollTop=0;" );
			pageContext.getOut().println("	}" );
			pageContext.getOut().println("	clearInterval(marqueeInterval[1]);" );
			pageContext.getOut().println("	marqueeInterval[1]=setInterval(\"scrollMarquee()\",20);" );
			pageContext.getOut().println("}" );
			pageContext.getOut().println("" );
			pageContext.getOut().println("function scrollMarquee() {" );
			pageContext.getOut().println("	if(isStopScoll==true)return;");
			pageContext.getOut().println("	marqueeBox.scrollTop+=2;" );
			pageContext.getOut().println("	if(marqueeBox.scrollTop%marqueeHeight==(marqueeHeight-1)){" );
			pageContext.getOut().println("		clearInterval(marqueeInterval[1]);" );
			pageContext.getOut().println("	}" );
			pageContext.getOut().println("}" );
			pageContext.getOut().println("function initScan() {" );
			pageContext.getOut().println("	var tatolPars=\"isRole=true\";" );
			pageContext.getOut().println("	var request=new Request({method:'post',asynchronous:false,parameters:tatolPars,onSuccess:initMarquee,functionId:'1010020307'});" );
			pageContext.getOut().println("}" );
			pageContext.getOut().println("</script>");
			pageContext.getOut().println("<script language=\"javascript\">");
			pageContext.getOut().println("	initScan();" );
			pageContext.getOut().println("</script>" );
			pageContext.getOut().println("</td>" );
			pageContext.getOut().println("</tr>" );
			pageContext.getOut().println("</table>" );
			pageContext.getOut().println("</div>" );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getHeight() {
			if( getStyle()==null || "panel".equals(getStyle())){
				height="0";
				
			}else if( "scrollLine".equals(getStyle())){
				height="16";
			}			
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getScrolldelay() {
		if(scrolldelay==null)scrolldelay="4000";
		return scrolldelay;
	}

	public void setScrolldelay(String scrolldelay) {
		this.scrolldelay = scrolldelay;
	}

	public String getWidth() {
			if( getStyle()==null || "panel".equals(getStyle())){
				width="100%";
				
			}else if( "scrollLine".equals(getStyle())){
				width="300";
			}			
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
}

