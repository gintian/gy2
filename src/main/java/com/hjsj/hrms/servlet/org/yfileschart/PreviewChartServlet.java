package com.hjsj.hrms.servlet.org.yfileschart;

import com.hjsj.hrms.businessobject.org.yfileschart.OrgMapBo;
import com.yworks.yfiles.server.tiles.servlet.BaseServlet;
import y.base.Node;
import y.layout.BufferedLayouter;
import y.layout.LayoutOrientation;
import y.layout.Layouter;
import y.layout.tree.TreeLayouter;
import y.layout.tree.XCoordComparator;
import y.view.Graph2D;
import y.view.NodeLabel;
import y.view.NodeRealizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

public class PreviewChartServlet extends BaseServlet{
	
	public final static  String GRAPH_NAME = "PreviewChart";
	public static ImageIcon personicon;
	
	public void initialize() throws ServletException {

	}

	protected void handleGetRequest(HttpServletRequest arg0,
			HttpServletResponse arg1) throws ServletException, IOException {
		handlePostRequest(arg0, arg1);
	}

	protected void handlePostRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		String graphaspect = request.getParameter("graphaspect");
		String isshowshadow = request.getParameter("isshowshadow");
		String cellcolor = request.getParameter("cellcolor").replace("＃", "#");
		String transitcolor = request.getParameter("transitcolor").replace("＃", "#");
		String bordercolor = request.getParameter("bordercolor").replace("＃", "#");
		String borderwidth = request.getParameter("borderwidth");
		String linecolor = request.getParameter("linecolor").replace("＃", "#");
		String linewidth = request.getParameter("linewidth");
		String cellhspacewidth = request.getParameter("cellhspacewidth");
		String cellvspacewidth = request.getParameter("cellvspacewidth");
		String fontfamily = request.getParameter("fontfamily");
		String fontstyle = request.getParameter("fontstyle");
		String fontsize = request.getParameter("fontsize");
		String fontcolor = request.getParameter("fontcolor").replace("＃", "#");
		
		String isshowpersonconut = request.getParameter("isshowpersonconut");
		String isshowpersonname = request.getParameter("isshowpersonname");
		String isshowphoto = request.getParameter("isshowphoto");
		String isshoworgconut = request.getParameter("isshoworgconut");
		String isshowposname = request.getParameter("isshowposname");
		
		HashMap optionMap = new HashMap();
		optionMap.put("graphaspect", graphaspect);
		optionMap.put("isshowshadow", isshowshadow);
		optionMap.put("cellcolor", cellcolor);
		optionMap.put("transitcolor", transitcolor);
		optionMap.put("bordercolor", bordercolor);
		optionMap.put("borderwidth", borderwidth);
		optionMap.put("linecolor", linecolor);
		optionMap.put("linewidth", linewidth);
		optionMap.put("fontfamily", fontfamily);
		optionMap.put("fontstyle", fontstyle);
		optionMap.put("fontsize", fontsize);
		optionMap.put("fontcolor", fontcolor);
		
		optionMap.put("isshowpersonconut", isshowpersonconut);
		optionMap.put("isshowpersonname", isshowpersonname);
		optionMap.put("isshoworgconut", isshoworgconut);
		optionMap.put("isshowphoto", isshowphoto);
		
        TreeLayouter treelayouter = new TreeLayouter(); 
	    
	    treelayouter.setMinimalNodeDistance(Integer.parseInt(cellhspacewidth));
	    treelayouter.setMinimalLayerDistance(Integer.parseInt(cellvspacewidth));
	    treelayouter.setComparator(new XCoordComparator()); //important to keep node order of collapsed/expanded items.
	    byte showtype;
		if("true".equals(optionMap.get("graphaspect")))
		    showtype =  LayoutOrientation.TOP_TO_BOTTOM;
		else
			showtype =  LayoutOrientation.LEFT_TO_RIGHT;
		treelayouter.setLayoutOrientation(showtype); 
		treelayouter.setLayoutStyle(TreeLayouter.ORTHOGONAL_STYLE);
		
		 //字体设置
	    int fonts = 0;
		if(  fontstyle.indexOf("italic") >-1 && fontstyle.indexOf("thick") >-1){
			fonts = 1+2;//
		}else if(fontstyle.indexOf("italic") >-1){
			fonts = 2;//
		}else if(fontstyle.indexOf("thick") >-1){
			fonts = 1;
		}else
			fonts = 0; 
		
		
		String textfamily = "宋体";
		if("kaiti".equals(fontfamily))
			textfamily = "楷体";
		else if("xinsong".equals(fontfamily))
			textfamily = "新宋体";
		else if("fangsong".equals(fontfamily))
			textfamily = "仿宋";
		else if("heiti".equals(fontfamily))
			textfamily = "黑体";
		else if("yahei".equals(fontfamily))
			textfamily = "微软雅黑";
		else if("lishu".equals(fontfamily))
			textfamily = "隶书";
		else if("youyuan".equals(fontfamily))
			textfamily = "幼圆";
		
		
		Font textfont = new Font(textfamily, fonts, Integer.parseInt(fontsize));
		Color textcolor = new Color(Integer.parseInt(fontcolor.replaceAll("#", ""),16));
		
		Graph2D graph = newGraph();
		OrgMapBo bo = new OrgMapBo();
		
		if (null == personicon) {
		    personicon = bo.loadIcon(request, "person.png", 57, 76);
		}
		
		bo.setDefaultRealizer(graph, optionMap, null);
		
		String text = "单位";
		int width = textfont.getSize()*3;
		int height = textfont.getSize()+10;
//		if("true".equals(isshoworgconut)){
//			  text +="\n下级机构数："+0;
//		  }
//		  if("true".equals(isshowpersonconut)){
//			  text +="\n人数："+1;
//		  }
		Node root=graph.createNode();
		NodeRealizer or = graph.getRealizer(root);
		or.setSize(width, height);
		NodeLabel ol = or.createNodeLabel();
		ol.setText(text);
		ol.setFont(textfont);
		ol.setTextColor(textcolor);
		or.setLabel(ol);
		
		Node umnode = root.createCopy(graph);
		NodeRealizer umnr = graph.getRealizer(umnode);
		NodeLabel umol = umnr.getLabel(0);
		umol.setText("部门");
		
		Node umnode2 = umnode.createCopy(graph);
		graph.createEdge(root, umnode);
		graph.createEdge(root, umnode2);
		
		
		
			Node pnode = graph.createNode();
			NodeRealizer nr = graph.getRealizer(pnode);
			NodeLabel pl = nr.createNodeLabel();
			pl.setText("岗位");
			pl.setFont(textfont);
			pl.setTextColor(textcolor);
			nr.setSize(width, height);
			nr.addLabel(pl);
			
			Node pnode2 = pnode.createCopy(graph);
			
	    if("true".equals(isshowposname)){	
			graph.createEdge(umnode2, pnode);
			graph.createEdge(umnode2, pnode2);
		}else{
			graph.removeNode(pnode);
			graph.removeNode(pnode2);
		}
		
		if("true".equals(isshowpersonname)){
		
			Node personNode=graph.createNode();
			NodeRealizer pr = graph.getRealizer(personNode);
			if("true".equals(optionMap.get("isshowphoto"))){
				 pr.setSize(75, 100);
		         NodeLabel photolabel = pr.createNodeLabel();
		         photolabel.setIcon(personicon);
		         photolabel.setText("人员");
		         photolabel.setFont(textfont);
		         photolabel.setFontSize(16);
		         photolabel.setTextColor(textcolor);
		         pr.addLabel(photolabel);
			}else{
			
				NodeLabel ml = pr.createNodeLabel();
				pr.setSize(width, height);
				ml.setText("人员");
				ml.setFont(textfont);
				ml.setTextColor(textcolor);
				pr.addLabel(ml);
			}
			
			Node personNode2 = personNode.createCopy(graph);
			Node personNode3 = personNode.createCopy(graph);
			if("true".equals(isshowposname)){
				graph.createEdge(pnode, personNode);
				graph.createEdge(pnode, personNode2);
				graph.createEdge(pnode2, personNode3);
			}else{
				graph.createEdge(umnode, personNode);
				graph.createEdge(umnode, personNode2);
				graph.createEdge(umnode2, personNode3);
			}
		
		}
		
		layout(graph,session,treelayouter);
	}
     
	private void layout(Graph2D graph, HttpSession session,TreeLayouter treelayouter ) {

	    Layouter layouter = null;
	      layouter = treelayouter;

	    new BufferedLayouter(layouter).doLayout(graph);
	    cacheGraph(session, GRAPH_NAME, graph);
	  }
	
}
