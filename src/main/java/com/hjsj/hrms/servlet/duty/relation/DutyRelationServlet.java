package com.hjsj.hrms.servlet.duty.relation;

import com.hjsj.hrms.businessobject.org.yfileschart.LoadFont;
import com.hjsj.hrms.businessobject.org.yfileschart.OrgMapBo;
import com.hjsj.hrms.servlet.org.yfileschart.OrgMapServlet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.yworks.yfiles.server.tiles.servlet.BaseServlet;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.LazyDynaBean;
import y.base.Node;
import y.base.NodeCursor;
import y.geom.YPoint;
import y.geom.YVector;
import y.layout.BufferedLayouter;
import y.layout.LayoutOrientation;
import y.layout.Layouter;
import y.layout.tree.TreeLayouter;
import y.layout.tree.XCoordComparator;
import y.view.GenericNodeRealizer;
import y.view.Graph2D;
import y.view.NodeLabel;
import y.view.NodeRealizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DutyRelationServlet extends BaseServlet{

	private static final String GRAPH_NAME = "PosRelationMap";
	 

	protected void handleGetRequest(HttpServletRequest arg0,
			HttpServletResponse arg1) throws ServletException, IOException {
		
		handlePostRequest(arg0, arg1); 
	}
	
	protected void handlePostRequest(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		
		
		String requestURI = req.getRequestURI();   
		HttpSession session = req.getSession();
		Connection conn = null;
		Graph2D graph = null;
		OrgMapBo omb = new OrgMapBo();
		
		if (null == OrgMapServlet.expandicon)
		    OrgMapServlet.expandicon = omb.loadIcon(req, "expand.png", 8, 8);
		
		if (null == OrgMapServlet.shrinkicon)
            OrgMapServlet.shrinkicon = omb.loadIcon(req, "shrink.png", 8, 8);
		
		try {
			conn = AdminDb.getConnection();
			if (requestURI.indexOf("/initialize") >= 0) {
				//传进去一个map，用来存岗位的上级名称
				omb.setPosUpInfoMap(new HashMap());
				
				graph = newGraph();
				cacheGraph(session, GRAPH_NAME, graph);
				UserView userView = (UserView)session.getAttribute(WebConstant.userView);
				
				HashMap optionMap = initData(req,session,graph,omb);
				HashMap orgInfoMap = omb.getOrgShowInfo(optionMap,userView.getUserName(),conn,true);
				optionMap.put("orgInfoMap", orgInfoMap);
				session.setAttribute("posMapOptions", optionMap);
				
				
				loadGraph(graph,conn,optionMap,userView,omb);
				cacheGraph(session, GRAPH_NAME, graph);
				layout(graph, session,(TreeLayouter)optionMap.get("treelayouter"));
				
				
				HashMap posUpMap = omb.getPosUpInfoMap();
				session.setAttribute("posUpMap", posUpMap);
				 //设置垂直靠上，水平靠左
				Rectangle2D.Double newBounds = getDoubleBounds(session);
		        String cwidth = req.getParameter("cwidth");
		        String cheight = req.getParameter("cheight");
		        double x = 0;
		        double y = 0;
		        if("true".equals(optionMap.get("graphaspect"))){
		        	double height = Double.parseDouble(cheight);
		        	y = height/2-100;
		        	x = newBounds.width/2;
		        }else{
		        	x = Double.parseDouble(cwidth)/2-100;
		        	y = newBounds.height/2;
		        }
		        	
		        writeResponse(res, newBounds,new YVector(x, y),posUpMap);
		    }else if(requestURI.indexOf("/download") >= 0){
		    	//下载....
		    	graph = getGraph(session, GRAPH_NAME);
		    	HashMap optionMap = (HashMap)session.getAttribute("posMapOptions");
		    	String treeVertical = optionMap.get("graphaspect").toString();
		    	Graph2D downLoadGraph = (Graph2D)graph.createCopy();
		    	NodeCursor nc = downLoadGraph.nodes();
		    	for(;nc.ok();nc.next()){
		    		Node node = nc.node();
		    		NodeRealizer nr =downLoadGraph.getRealizer(node) ;
		    		if("true".equals(treeVertical))
		    			nr.setSize(nr.getWidth(), nr.getHeight()-16);
		    		else
		    			nr.setSize(nr.getWidth()-16, nr.getHeight());
			    	nr.getLabel(1).setVisible(false);
		    	}
		    	UserView userView = (UserView)session.getAttribute(WebConstant.userView);
		    	String fileName = userView.getUserName()+"_PosMap.jpg";
		    	omb.outPutGraph2Jpeg(res, downLoadGraph,fileName);
		    	//cacheGraph(session, "downloadgraph", downLoadGraph);
		    	//res.getWriter().write("downloadgraph");
		    	req.getRequestDispatcher("/servlet/vfsservlet?fromjavafolder=true&fileid=" + PubFunc.encrypt(fileName)).forward(req,res);
		    }else{
		    	String nodeId = req.getParameter("id");
		    	graph = getGraph(session, GRAPH_NAME);
		    	HashMap optionMap = (HashMap)session.getAttribute("posMapOptions");
		    	omb.setOrgInfoMap((HashMap)optionMap.get("orgInfoMap"));
		    	omb.setPosUpInfoMap((HashMap)session.getAttribute("posUpMap"));
			    Node rootNode;
			    if(nodeId==null || nodeId.length()<1){
			    	rootNode = graph.getNodeArray()[0];
			    }else{
			    	rootNode = (Node)getForId(graph, nodeId);
			    }
			    NodeRealizer nr = graph.getRealizer(rootNode);
			    UserView userView = (UserView)session.getAttribute(WebConstant.userView);
			    
			    if(requestURI.indexOf("/toggleNode") >= 0){
			    	
			    	// 展开 & 收缩 节点
			    	String expandflag = req.getParameter("expandflag");
			    	NodeLabel imageLabel = nr.getLabel(1);
			    	if(!imageLabel.isVisible())
			    		return;
			    	
				    YPoint oldPos = new YPoint(graph.getX(rootNode), graph.getY(rootNode));
				    
				    NodeCursor nc = rootNode.successors();
				    
				    if("all".equals(expandflag)){
				    	hideChild(graph,nodeId,null);
				    	showChild(graph,rootNode,optionMap,true,omb,userView,conn,OrgMapServlet.shrinkicon,expandflag);
				    }else if("shrink".equals(expandflag)){
				    	hideChild(graph,nodeId,OrgMapServlet.expandicon);
				    }else{	
				    	if(nc.size()>0){
				    		hideChild(graph,nodeId,OrgMapServlet.expandicon);
				    	}else{
				    		showChild(graph,rootNode,optionMap,false,omb,userView,conn,OrgMapServlet.shrinkicon,expandflag);
				    	}
				    }
				    
				    layout(graph,session,(TreeLayouter)optionMap.get("treelayouter"));
				    YPoint newPos = new YPoint(graph.getX(rootNode), graph.getY(rootNode));
			        YVector shift = new YVector(oldPos, newPos);
				    Rectangle2D.Double newBounds = getDoubleBounds(session);
					
				    
				    HashMap posUpMap = omb.getPosUpInfoMap();
				    session.setAttribute("posUpMap", posUpMap);
			        writeResponse(res, newBounds, shift,posUpMap);
			    }else if(requestURI.indexOf("/getCodeitemid") >= 0){
			    	
			    	//获取节点属性
					NodeLabel codeLabel = nr.getLabel(2);
			    	String text = codeLabel.getText();
					if(text.length()<1)
						return;
					else
						res.getWriter().write(text);
			    }
		    }
		
		
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
		}
		
	}
	
	
	private void hideChild(Graph2D graph,String nodeId,ImageIcon expandicon){
		Node rootnode = (Node)getForId(graph, nodeId);
		if(expandicon!=null){
			NodeRealizer nr = (GenericNodeRealizer)graph.getRealizer(rootnode);
			nr.getLabel(1).setIcon(expandicon);
		}
    	for(NodeCursor nc = rootnode.successors();nc.ok();nc.next()){
    		Node node = nc.node();
    		String id = getId(graph, node); 
    		hideChild(graph,id,null);
    		graph.removeNode(node);
    	}
	}
	
	private void showChild(Graph2D graph,Node rootNode,HashMap optionMap,boolean expandflag,OrgMapBo bo,UserView userView,Connection conn,ImageIcon shrinkicon,String allflag){
		NodeRealizer nr = graph.getRealizer(rootNode);
		NodeLabel iconLabel = nr.getLabel(1);
		iconLabel.setIcon(shrinkicon);
		NodeLabel codeLabel = nr.getLabel(2);
		String text = codeLabel.getText();
		String code = text.length()>2?text.substring(2):"";
	    optionMap.put("code", code);
	    List childList = bo.searchPosByCode(optionMap,conn,userView,false);
	    bo.expandPosChildNodes(rootNode, graph, childList,optionMap,expandflag,userView,conn,shrinkicon,allflag);
	}
	
	
	private HashMap initData(HttpServletRequest request,HttpSession session,Graph2D graph,OrgMapBo omb){
		String code = request.getParameter("code");
		
		HashMap optionMap = omb.getMapOptions(OrgMapBo.POSRELATION);
		
		code = code==null?"":code.trim();
		optionMap.put("code", code);
		
		String constant = request.getParameter("constant");
		optionMap.put("constant", constant);
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = sdf.format(new Date());
		optionMap.put("backdate", backdate);
		
        omb.setDefaultRealizer(graph, optionMap,OrgMapServlet.expandicon);
		
        String fontstyle = optionMap.get("fontstyle").toString();
        String fontfamily = optionMap.get("fontfamily").toString();
        String fontsize = optionMap.get("fontsize").toString();
        String fontcolor = optionMap.get("fontcolor").toString();
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
			textfamily = "楷体_GB2312";
		else if("xinsong".equals(fontfamily))
			textfamily = "宋体";
		else if("fangsong".equals(fontfamily))
			textfamily = "仿宋体";
		else if("heiti".equals(fontfamily))
			textfamily = "黑体";
		else if("yahei".equals(fontfamily))
			textfamily = "微软雅黑";
		else if("lishu".equals(fontfamily))
			textfamily = "隶书";
		else if("youyuan".equals(fontfamily))
			textfamily = "幼圆";
		
		
		Font textfont = LoadFont.getFont(textfamily, fonts, Integer.parseInt(fontsize));
		Color textcolor = new Color(Integer.parseInt(fontcolor.replaceAll("#", ""),16));
        
		optionMap.put("textfont", textfont);
		optionMap.put("textcolor", textcolor);
		
        //节点排列方式
		TreeLayouter treelayouter = new TreeLayouter(); 
	    
	    String cellhspacewidth = optionMap.get("cellhspacewidth").toString();
	    String cellvspacewidth = optionMap.get("cellvspacewidth").toString();
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
	    
	    optionMap.put("treelayouter", treelayouter);
	    
	    return optionMap;
	}
	
	
	private void loadGraph(Graph2D graph,Connection conn,HashMap optionMap,UserView userView,OrgMapBo bo){
		String roottext = "汇报关系";
		Node rootNode  = graph.createNode();
		
		String code = optionMap.get("code").toString();
		List infoList = bo.searchPosByCode(optionMap,conn,userView,true);
		if(code.length()>0){
			if(infoList.size() == 0){
				graph.removeNode(rootNode);
				return ;
			}
			LazyDynaBean ldb = (LazyDynaBean)infoList.get(0);
			bo.setPosConvert(true);
			bo.content2OrgNode(ldb, rootNode, graph, true, OrgMapServlet.shrinkicon, optionMap,true,"");
			
	    }else{
	    	if(infoList.size()==1){
	    		LazyDynaBean ldb = (LazyDynaBean)infoList.get(0);
				bo.content2OrgNode(ldb, rootNode, graph, true, OrgMapServlet.shrinkicon, optionMap,true,"");
	    	}else{
	    		LazyDynaBean ldb = new LazyDynaBean();
	    		ldb.set("codeitemid", "");
	    		ldb.set("codeitemdesc",roottext);
	    		ldb.set("codesetid", "@K");
	    		ldb.set("nextorgnum", "1");
	    		ldb.set("personnum", "1");
	    		
	    		bo.content2OrgNode(ldb, rootNode, graph, true, OrgMapServlet.shrinkicon, optionMap,true,"");
	    	}
	    }
		
	}
	
	protected Rectangle2D.Double getDoubleBounds(HttpSession session) {
	    Rectangle bounds = getGraphBounds(session, GRAPH_NAME);
	    if(bounds != null) {
	      return new Rectangle2D.Double(bounds.x, bounds.y, bounds.width, bounds.height);
	    } else {
	      return null;
	    }
	  }
	
	private void writeResponse(HttpServletResponse response, Rectangle2D.Double bounds, YVector shift,HashMap posUpMap) throws IOException {
		response.setCharacterEncoding("GBK");
	    PrintWriter writer = getWriter(response);
	    bounds.x -= 10;
	    bounds.y -= 10;
	    bounds.width += 20;
	    bounds.height += 20;
	    JSONArray js = JSONArray.fromObject(posUpMap);
	    
	    writer.write("{\n  \"bounds\" : { \"minX\" : " + bounds.x + ", \"minY\" : " + bounds.y +
	        ", \"maxX\" : " + (bounds.x + bounds.width) +
	        ", \"maxY\" : " + (bounds.y + bounds.height) + " }, \n");
	    writer.write("  \"shift\" : { \"x\" : " + shift.getX() + ", \"y\" : " + shift.getY() + " }, \n");
	    writer.write("  \"posUpJson\" :"+js.get(0).toString());
	    writer.write("\n}");

	    response.setStatus(200);
	  }
	
    
    private void layout(Graph2D graph, HttpSession session,TreeLayouter treelayouter ) {

	    Layouter layouter = null;
	      layouter = treelayouter;

	    new BufferedLayouter(layouter).doLayout(graph);
	    cacheGraph(session, GRAPH_NAME, graph);
	  }
    
//    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
//    String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
//	 seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
}
