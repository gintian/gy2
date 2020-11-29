package com.hjsj.hrms.servlet.org.yfileschart;

import com.hjsj.hrms.businessobject.org.yfileschart.LoadFont;
import com.hjsj.hrms.businessobject.org.yfileschart.OrgMapBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import com.yworks.yfiles.server.tiles.servlet.BaseServlet;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrgMapServlet extends BaseServlet{

	private static final String GRAPH_NAME = "OrgMapTree";
	
	public static ImageIcon expandicon;
	public static ImageIcon shrinkicon;

	
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
		UserView userView = (UserView)session.getAttribute(WebConstant.userView);
		OrgMapBo omb = new OrgMapBo(userView);
		
		if(null == expandicon)
		    expandicon = omb.loadIcon(req, "expand.png", 8, 8);
		
		if (null == shrinkicon)
		    shrinkicon = omb.loadIcon(req, "shrink.png", 8, 8);		
		
		try{
			conn = AdminDb.getConnection();
			if (requestURI.indexOf("/initialize") >= 0) {
				
				graph = newGraph();
				HashMap optionMap = initData(req,session,graph,omb);
				HashMap orgInfoMap = omb.getOrgShowInfo(optionMap,userView.getUserName(), conn,false);
				optionMap.put("orgInfoMap", orgInfoMap);
		    	loadGraph(graph,conn,optionMap,omb);
		        cacheGraph(session, GRAPH_NAME, graph); 
		        layout(graph, session,(TreeLayouter)optionMap.get("treelayouter"));
		        
		        Rectangle2D.Double newBounds = getDoubleBounds(session);
		        
		        session.setAttribute("orgMapOptions", optionMap);
		        
		        
		        //设置垂直靠上，水平靠左
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
		        	
		        writeResponse(res, newBounds,new YVector(x, y));
		        
		    }else if(requestURI.indexOf("/download") >= 0){
		    	//下载....
		    	
		    	graph = getGraph(session, GRAPH_NAME);
		    	HashMap optionMap = (HashMap)session.getAttribute("orgMapOptions");
		    	String treeVertical = optionMap.get("graphaspect").toString();
		    	Graph2D downLoadGraph = (Graph2D)graph.createCopy();
		    	NodeCursor nc = downLoadGraph.nodes();
		    	for(;nc.ok();nc.next()){
		    		Node node = nc.node();
		    		NodeRealizer nr =downLoadGraph.getRealizer(node) ;
		    		if(nr.getLabel(2).getText().indexOf("RY") > -1)
		    			continue;
		    		if("true".equals(treeVertical))
		    			nr.setSize(nr.getWidth(), nr.getHeight()-16);
		    		else
		    			nr.setSize(nr.getWidth()-16, nr.getHeight());
			    	nr.getLabel(1).setVisible(false);
		    	}
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
				String date = df.format(new Date());
		    	String fileName = PubFunc.getPinYin(userView.getUserName())+date+"_OrgMap.jpg";
		    	OrgMapBo.outPutGraph2Jpeg(res, downLoadGraph,fileName);
		    	//cacheGraph(session, "downloadgraph", downLoadGraph);
		    	//res.getWriter().write(fileName);
		    	req.getRequestDispatcher("/servlet/vfsservlet?fromjavafolder=true&fileid=" + PubFunc.encrypt(fileName)).forward(req,res);
		    }else{
		    	String nodeId = req.getParameter("id");
		    	graph = getGraph(session, GRAPH_NAME);
		    	HashMap optionMap = (HashMap)session.getAttribute("orgMapOptions");
		    	omb.setOrgInfoMap((HashMap)optionMap.get("orgInfoMap"));
			    Node rootNode;
			    //如果没有任何节点 return
			    if(graph.nodeCount()<1)
			    	return;
			    if(nodeId==null || nodeId.length()<1){
			    	rootNode = graph.getNodeArray()[0];
			    }else{
			    	rootNode = (Node)getForId(graph, nodeId);
			    }
			    NodeRealizer nr = graph.getRealizer(rootNode);
			    
			    if(requestURI.indexOf("/toggleNode") >= 0){
			    	// 展开 & 收缩 节点
			    	String expandflag = req.getParameter("expandflag");
			    	NodeLabel imageLabel = nr.getLabel(1);
			    	if(!imageLabel.isVisible()){
			    		res.getWriter().write("{\"state\":\"re\"}");
			    		return;
			    	}
				    YPoint oldPos = new YPoint(graph.getX(rootNode), graph.getY(rootNode));
				    
				    NodeCursor nc = rootNode.successors();
				    
				    
				    if("all".equals(expandflag)){
				    	hideChild(graph,nodeId,null);
				    	showChild(graph,rootNode,optionMap,true,omb,conn,shrinkicon,expandflag);
				    }else{	
				    	if(nc.size()>0){
				    		if(nodeId==null || nodeId.length()<1){
				    			res.getWriter().write("{\"state\":\"re\"}");
				    			return;
				    		}
				    		hideChild(graph,nodeId,expandicon);
				    	}else{
				    		showChild(graph,rootNode,optionMap,false,omb,conn,shrinkicon,expandflag);
				    	}
				    }
				    
				    layout(graph,session,(TreeLayouter)optionMap.get("treelayouter"));
				    YPoint newPos = new YPoint(graph.getX(rootNode), graph.getY(rootNode));
			        YVector shift = new YVector(oldPos, newPos);
				    Rectangle2D.Double newBounds = getDoubleBounds(session);
					
			        writeResponse(res, newBounds, shift);
			        
			    }else if(requestURI.indexOf("/getCodeitemid") >= 0){
			    	
			    	//获取节点属性
					NodeLabel codeLabel = nr.getLabel(2);
			    	String text = codeLabel.getText();
			    	if(text.indexOf("RY")!=-1){
			    		String a0100 = text.substring(2);
			    		text = SafeCode.encode(text);
			    	}
					if(text.length()<1)
						return;
					else{
						res.getWriter().write(text+"`"+PubFunc.convertTo64Base(optionMap.get("dbnames").toString()));
					}
			    }
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
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
	
	private void writeResponse(HttpServletResponse response, Rectangle2D.Double bounds, YVector shift) throws IOException {
	    PrintWriter writer = getWriter(response);
	    bounds.x -= 10;
	    bounds.y -= 10;
	    bounds.width += 20;
	    bounds.height += 20;
	    
	    writer.write("{\n  \"bounds\" : { \"minX\" : " + bounds.x + ", \"minY\" : " + bounds.y +
	        ", \"maxX\" : " + (bounds.x + bounds.width) +
	        ", \"maxY\" : " + (bounds.y + bounds.height) + " }, \n");
	    writer.write("  \"shift\" : { \"x\" : " + shift.getX() + ", \"y\" : " + shift.getY() + " }");
	    writer.write("\n}");

	    response.setStatus(200);
	  }
	
	private void layout(Graph2D graph, HttpSession session,TreeLayouter treelayouter ) {

	    Layouter layouter = null;
	      layouter = treelayouter;

	    new BufferedLayouter(layouter).doLayout(graph);
	    cacheGraph(session, GRAPH_NAME, graph);
	  }
	
	private void loadGraph(Graph2D graph,Connection conn,HashMap optionMap,OrgMapBo bo){
		
		String roottext = "";
		Node rootNode  = graph.createNode();
		
		String code = optionMap.get("code").toString();
		
		List infoList = bo.searchInfoByCode(optionMap,conn,true,"");
		
		
		if(code.length()>0){
			//没有找到指定节点，说明数据有问题
			if(infoList.size() == 0){
				graph.clear();
				return;
			}
			LazyDynaBean ldb = (LazyDynaBean)infoList.get(0);
			
			bo.content2OrgNode(ldb, rootNode, graph, true, shrinkicon, optionMap,false,"");
	    }else{
	    	
	    	Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
	    	 roottext=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
			 if(roottext==null||roottext.length()<=0)
			 {
				 roottext=ResourceFactory.getProperty("tree.orgroot.orgdesc");
			 }
			 
			 //如果顶级节点只有有个，并且没有设置组织机构名称，直接显示第一级机构，否则显示组织机构名称。前提是不是历史机构
	    	if((infoList.size()==1 && roottext.equals(ResourceFactory.getProperty("tree.orgroot.orgdesc"))) || "true".equals(optionMap.get("ishistory"))){
	    		LazyDynaBean ldb = (LazyDynaBean)infoList.get(0);
	    		
	    		bo.content2OrgNode(ldb, rootNode, graph, true, shrinkicon, optionMap,false,"");
				optionMap.put("code", ldb.get("codeitemid"));
	    	}else{
	    		LazyDynaBean ldb = new LazyDynaBean();
	    		ldb.set("codeitemid", "");
	    		ldb.set("codeitemdesc",roottext);
	    		ldb.set("codesetid", "UN");
	    		ldb.set("nextorgnum", "1");
	    		ldb.set("personnum", "1");
	    		
	    		bo.content2OrgNode(ldb, rootNode, graph, true, shrinkicon, optionMap,false,"");
	    	}
			 
	    }
		
		
		//infoList = bo.searchInfoByCode(optionMap,conn,false);
		//bo.expandChildNodes(rootNode, graph, infoList,optionMap,false,conn,shrinkicon);
		
	}
	
	
	private void showChild(Graph2D graph,Node rootNode,HashMap optionMap,boolean expandflag,OrgMapBo omb,Connection conn,ImageIcon shrinkicon,String allflag){
		NodeRealizer nr = graph.getRealizer(rootNode);
		NodeLabel iconLabel = nr.getLabel(1);
		NodeLabel codeLabel = nr.getLabel(2);
		String text = codeLabel.getText();
	    String code = text.length()>2?text.substring(2):"";
	    optionMap.put("code", code);
	    List childList = omb.searchInfoByCode(optionMap,conn,false,allflag);
	    //在最下级（非人员节点）上点击展开所有下级时，不展开人员节点故不把加号改为减号
	    if(childList.size()>0)
	        iconLabel.setIcon(shrinkicon);
	    
	    omb.expandChildNodes(rootNode, graph, childList,optionMap,expandflag,conn,shrinkicon,allflag);
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
	
	private HashMap initData(HttpServletRequest request,HttpSession session,Graph2D graph,OrgMapBo omb){
		String code = request.getParameter("code");
		String backdate = request.getParameter("backdate");
		String ishistory = request.getParameter("ishistory");
		String catalog_id = request.getParameter("catalog_id");
		UserView userView = (UserView)session.getAttribute(WebConstant.userView);
		
		HashMap optionMap = omb.getMapOptions(OrgMapBo.ORGMAP);
		
		/** 权限机构:code为空，说明点的是“组织机构”四个字。
		 * 如果“组织机构”下只有一个机构则只显示这一个（不显示“组织机构”节点）；
		 * 如有多个，则显示“组织机构”并且下面显示多个。chent 20170715 add
		 *  */
		if(code==null || code!=null && code.length()==0) {
			code = "00";
			String busi= OrgMapBo.getBusi_org_dept(userView);
			if(busi.length()>2){
				// UN02和UN02`都需要当做只有一个机构 chent 20180315 update
				if(busi.indexOf("`") == -1) {
					code = busi.substring(2);
				} else if(busi.split("`").length == 1) {
					String b = busi.split("`")[0];
					if(StringUtils.isNotEmpty(b)) {
						code = b.substring(2);
					}
				}
			}else{
				code="#";
			}
		}
		optionMap.put("code", code);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		optionMap.put("backdate", backdate);
    	
		//历史机构进入.....
		optionMap.put("ishistory",ishistory);
		if("true".equals(ishistory)){
			optionMap.put("catalog_id",catalog_id);
			optionMap.put("isshowpersonconut", "false");
			optionMap.put("isshowpersonname", "false");
			optionMap.put("isshowphoto", "false");
			optionMap.put("isshoworgconut", "false");
			optionMap.put("isshowposname", "true");
		}
    	
    	//人员库
		String dbnameini="Usr";
		ArrayList Dblist=userView.getPrivDbList();
		if(Dblist!=null&&Dblist.size()>0)
			dbnameini=Dblist.get(0).toString();	
    	String dbpre = optionMap.get("dbnames").toString();
    	dbpre = dbpre.substring(0, 1).toUpperCase()+dbpre.substring(1);
		if(!userView.hasTheDbName(dbpre)){
			optionMap.put("dbnames", dbnameini);
		}
		
        omb.setDefaultRealizer(graph, optionMap,expandicon);
		
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

	public void initialize() throws ServletException {

	}
	
}
