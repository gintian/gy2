package com.hjsj.hrms.interfaces.general.print;

import com.hjsj.hrms.interfaces.report.ReportParamterVo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class PageOptionsXml {
//	创建用户相关配置文件
	
	public String createPageOptionsXML(ReportParseVo parsevo){
		ReportParseXml reportParseXml=new ReportParseXml();
		ReportParamterVo revo=reportParseXml.getReportParamterVoFromReportParseVo(parsevo);
		String temp = null;
		//根节点
		Element report = new Element("report");		
		report.setAttribute("name",revo.getName()!=null?revo.getName():"");
		report.setAttribute("pagetype",revo.getPagetype()!=null?revo.getPagetype():"A4");
		report.setAttribute("unit",revo.getUnit()!=null?revo.getUnit():"px");
		report.setAttribute("orientation",revo.getOrientation()!=null?revo.getOrientation():"0");
		report.setAttribute("top",revo.getTop()!=null?revo.getTop():"");
		report.setAttribute("left",revo.getLeft()!=null?revo.getLeft():"");
		report.setAttribute("right",revo.getRight()!=null?revo.getRight():"");
		report.setAttribute("bottom",revo.getBottom()!=null?revo.getBottom():"");
		report.setAttribute("value",revo.getValue()!=null?revo.getValue():"");
 	    report.setAttribute("width",revo.getWidth()!=null?revo.getWidth():"210");
 	    report.setAttribute("height",revo.getHeight()!=null?revo.getHeight():"297");
 	    //标题
 	    Element title_item =new Element("title");	 	   
 	    title_item.setAttribute("content",revo.getTitle_c());
 	    report.addContent(title_item);
 	   /**节点,报表表头**/
 	   Element head_item = new Element("head");	 	   
	   head_item.setAttribute("content",revo.getHead_c());		   
	   report.addContent(head_item);
	   /**节点,报表表尾**/
	   Element tile_item=new Element("tile");
	   tile_item.setAttribute("content",revo.getTile_c());		  
	   report.addContent(tile_item);
	   /**节点,报表表体**/
	    Element body_item=new Element("body");
	    body_item.setAttribute("content",revo.getBody_c());	
	    report.addContent(body_item);
	    
		Document myDocument = new Document(report);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		temp= outputter.outputString(myDocument);
		
		/**System.out.println("*********创建XML**************");
		System.out.println(temp);
		System.out.println("********************");*/
		return temp;
	}
}
