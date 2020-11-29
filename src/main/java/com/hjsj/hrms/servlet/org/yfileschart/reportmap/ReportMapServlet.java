package com.hjsj.hrms.servlet.org.yfileschart.reportmap;

import com.hjsj.hrms.actionform.general.sprelationmap.RelationMapForm;
import com.hjsj.hrms.businessobject.general.sprelationmap.ChartParameterCofig;
import com.hjsj.hrms.businessobject.general.sprelationmap.RelationMapBo;
import com.hjsj.hrms.businessobject.org.yfileschart.OrgMapBo;
import com.hjsj.hrms.servlet.org.yfileschart.OrgMapServlet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.yworks.yfiles.server.tiles.servlet.BaseServlet;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.LazyDynaBean;
import y.base.Edge;
import y.base.Node;
import y.base.NodeCursor;
import y.geom.YPoint;
import y.geom.YVector;
import y.layout.BufferedLayouter;
import y.layout.Layouter;
import y.view.*;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class ReportMapServlet extends BaseServlet{

	private static final String GRAPH_NAME = "ReportMapTree";
	
	protected void handleGetRequest(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		handlePostRequest(req,res);
	}

	protected void handlePostRequest(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		
		String requestURI = req.getRequestURI();   
		HttpSession session = req.getSession();
		Connection conn = null;
		Graph2D graph = null;
		
		
		/*
		 *保存一些参数
		 *key：“ChartParameterCofig” >>  参数类（类型：ChartParameterCofig）
		 *key：“textfont”>> 字体（类型：Font）
		 *key：“textcolor”>> 字体颜色（类型：Color）
		 *key:“layouter”>> 显示布局类（类型 TreeLayouter）
		 */
		HashMap reportOption = null;
		
		UserView userView = (UserView)session.getAttribute(WebConstant.userView);
		if (null == OrgMapServlet.expandicon || null == OrgMapServlet.shrinkicon){
			OrgMapBo omb = new OrgMapBo();
		    OrgMapServlet.expandicon = omb.loadIcon(req, "expand.png", 8, 8);
            OrgMapServlet.shrinkicon = omb.loadIcon(req, "shrink.png", 8, 8);
		}
		
		try{
			conn = AdminDb.getConnection();
			if (requestURI.indexOf("/initialize") >= 0) {
				graph = newGraph();
				
				RelationMapBo rmb = new RelationMapBo(conn, userView, "");
				//解析参数
				reportOption = rmb.initStyle(graph,OrgMapServlet.expandicon);
				
				RelationMapForm relationMapForm = (RelationMapForm)session.getAttribute("relationMapForm");
				HashMap reportData = relationMapForm.getReportData();
				if(reportData == null || reportData.size() == 0){
					cacheGraph(session, GRAPH_NAME, graph);
					return;
				}
				
				Font textfont = (Font)reportOption.get("textfont");
				Color textcolor = (Color)reportOption.get("textcolor");
				cacheGraph(session, GRAPH_NAME, graph);
				//将汇报数据生成graph
				rmb.data2Graph(graph,reportData,textfont,textcolor,reportOption.get("direction").toString());
				
				layout(graph,session,(Layouter)reportOption.get("layouter"));
				
				session.setAttribute("reportMapOptions", reportOption);
				
				Rectangle2D.Double newBounds = getDoubleBounds(session);
		        
		        //设置垂直靠上，水平靠左
		        String cwidth = "10";
		        String cheight = "10";
		        double x = 0;
		        double y = 0;
		        if("1".equals(rmb.chartParam.getDirection())){
		        	double height = Double.parseDouble(cheight);
		        	y = height/2-100;
		        	x = newBounds.width/2;
		        }else{
		        	x = Double.parseDouble(cwidth)/2-100;
		        	y = newBounds.height/2;
		        }
				
				HashMap nodeHintInfo = rmb.getNodeHintInfo();
				writeResponse(res,newBounds, new YVector(x, y),nodeHintInfo);
			}else if(requestURI.indexOf("/toggleNode") >= 0){
				
				graph = getGraph(session, GRAPH_NAME);
				reportOption = (HashMap)session.getAttribute("reportMapOptions");
				RelationMapBo rmb = new RelationMapBo(conn, userView);
				RelationMapBo.chartParam = (ChartParameterCofig)reportOption.get("ChartParameterCofig");
				String nodeId = req.getParameter("nodeId");
				
				Node rnode = (Node)getForId(graph, nodeId);
				NodeLabel buttonLabel = graph.getRealizer(rnode).getLabel(1);
		    	if(!buttonLabel.isVisible()){
		    		//res.getWriter().write("{\"state\":\"re\"}");
		    		return;
		    	}
				NodeCursor nc = rnode.successors();
				if(nc.size()>0){
					hideChild(graph,nodeId,OrgMapServlet.expandicon);
				}else{
					expandChild(graph,rnode,reportOption,rmb,session);
				}
				
				//获取节点当前坐标
				YPoint oldPos = new YPoint(graph.getX(rnode), graph.getY(rnode));
				
				
				
				layout(graph,session,(Layouter)reportOption.get("layouter"));
			    YPoint newPos = new YPoint(graph.getX(rnode), graph.getY(rnode));
		        YVector shift = new YVector(oldPos, newPos);
			    Rectangle2D.Double newBounds = getDoubleBounds(session);
			    HashMap nodeHintInfo = rmb.getNodeHintInfo();
		        writeResponse(res, newBounds, shift,nodeHintInfo);
			}else if(requestURI.indexOf("/download") >= 0){
		    	//下载....
		    	graph = getGraph(session, GRAPH_NAME);
		    	Graph2D downLoadGraph = (Graph2D)graph.createCopy();
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
				String date = df.format(new Date());

		    	String fileName = PubFunc.getPinYin(userView.getUserName())+date+"_reportMap.jpg";
		    	OrgMapBo.outPutGraph2Jpeg(res, downLoadGraph,fileName);
		    	downLoadGraph = null;
		    	req.getRequestDispatcher("/servlet/vfsservlet?fromjavafolder=true&fileid=" + PubFunc.encrypt(fileName)).forward(req,res);
		    }else if(requestURI.indexOf("/getCodeitemid") >= 0){
		    	graph = getGraph(session, GRAPH_NAME);
				String nodeId = req.getParameter("nodeId");
				Node rnode = (Node)getForId(graph, nodeId);
				
				// 每个节点上携带的隐藏信息：  relationid`mainbody_id    > relationid 是所属汇报线id，mainbody_id 是对象的实际id 比如 UN01、Usr00000001 等
				String[] info = graph.getRealizer(rnode).getLabel(2).getText().split("`");
				if(info.length<2)
					return;
				
				String output = info[1];
				if(info[1].indexOf("UN")==-1 && info[1].indexOf("UM")==-1 && info[1].indexOf("@K")==-1){
					String dbname = output.substring(0,3);
					String a0100 = output.substring(3);
					if(!checkPrivA0100(a0100,dbname,userView)){
						return;
					}
					dbname = PubFunc.convertTo64Base(dbname);
					output = dbname+"`"+SafeCode.encode(a0100);
				}
				res.getWriter().write(output);
		    }
			
			System.gc();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(conn);
		}
		
		
	}

	private boolean checkPrivA0100(String a0100,String dbname,UserView userView){
		try {
			String sql = "select 1 "+userView.getPrivSQLExpression(dbname, false)+" and a0100='"+a0100+"'";
			if(ExecuteSQL.executeMyQuery(sql).size()<1)
				return false;
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	protected Rectangle2D.Double getDoubleBounds(HttpSession session) {
	    Rectangle bounds = getGraphBounds(session, GRAPH_NAME);
	    if(bounds != null) {
	      return new Rectangle2D.Double(bounds.x, bounds.y, bounds.width, bounds.height);
	    } else {
	      return null;
	    }
	  }
	
	
	private void writeResponse(HttpServletResponse response, Rectangle2D.Double bounds, YVector shift,HashMap hintInfo) throws IOException {
		response.setCharacterEncoding("GBK");
	    PrintWriter writer = getWriter(response);
	    
	    JSONArray js = JSONArray.fromObject(hintInfo);
	    
	    writer.write("{\n");
	    if(bounds != null && shift != null){
	    	bounds.x -= 10;
		    bounds.y -= 10;
		    bounds.width += 20;
		    bounds.height += 20;
		    writer.write("\"bounds\" : { \"minX\" : " + bounds.x + ", \"minY\" : " + bounds.y +
		        ", \"maxX\" : " + (bounds.x + bounds.width) +
		        ", \"maxY\" : " + (bounds.y + bounds.height) + " }, \n");
		    writer.write("  \"shift\" : { \"x\" : " + shift.getX() + ", \"y\" : " + shift.getY() + " }, \n");
	    }
	    writer.write("  \"hintInfo\" :"+js.get(0).toString());
	    writer.write("\n}");

	    response.setStatus(200);
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
	
	private void expandChild(Graph2D graph,Node rnode,HashMap reportOption,RelationMapBo rmb,HttpSession session){
		//获取信息label
		NodeLabel nl = graph.getRealizer(rnode).getLabel(2);
		String[] info = nl.getText().split("`");
		String relationid = info[0];
		Font textfont = (Font)reportOption.get("textfont");
		Color textcolor = (Color)reportOption.get("textcolor");
		
		if("relation".equals(info[0])){
			RelationMapForm relationMapForm = (RelationMapForm)session.getAttribute("relationMapForm");
			HashMap reportData = relationMapForm.getReportData();
			ArrayList relationList = (ArrayList)reportData.get("relationList");
			for(int i=0;i<relationList.size();i++){
				LazyDynaBean rebean = (LazyDynaBean)relationList.get(i);
				if(info[1].equals(rebean.get("relation_id"))){
					String relationNodeid = getId(graph, rnode);
					LazyDynaBean rootbean = (LazyDynaBean)reportData.get("selectObj");
					rmb.buildSingleRelation(graph, relationNodeid,info[1],(ArrayList)rebean.get("alist"),rootbean, textfont, textcolor);
					break;
				}
				
			}
			
		}else{
			//查询下级
			ArrayList childList = rmb.getChildNode(nl.getText());
			if(!"1".equals(reportOption.get("direction").toString()))
				  Collections.reverse(childList);
			int childNum = 0;
			for(int i=0;i<childList.size();i++){
			    LazyDynaBean cbean = (LazyDynaBean)childList.get(i);
			    String object_id = cbean.get("object_id").toString();
			    
			    //判断自己上级的上级是不是自己的主管领导，如果不是，跳过
			    String grandparent = info.length>2?info[2]:"";
			    String bossid = "";
			    if(grandparent.length()>0){
			    	    bossid = rmb.findManagerPerson(object_id, relationid);
			    }
			    if(bossid.length()>0 && bossid.toUpperCase().indexOf(grandparent.toUpperCase())==-1)
			    	    continue;
			    
			    cbean.set("mainbody_id",object_id);
			    cbean.set("parent_id", info[1]);
			   if(! rmb.isPersonInPriv(object_id))
				    continue;
			   Node cnode =  rmb.createRelationNode(graph, relationid, cbean, textfont, textcolor,true);
			   Edge e = graph.createEdge(rnode, cnode);
			   EdgeRealizer er = graph.getRealizer(e);
			   er.setSourceArrow(Arrow.SHORT);
			   graph.getRealizer(rnode).getLabel(1).setIcon(OrgMapServlet.shrinkicon);
			   childNum++;
			}
			if(childNum<1)
				graph.getRealizer(rnode).getLabel(1).setVisible(false);
		}
	}
	
	private void layout(Graph2D graph, HttpSession session,Layouter layouter) {
			
	    new BufferedLayouter(layouter).doLayout(graph);
	    cacheGraph(session, GRAPH_NAME, graph);
	  }
	
	public void init() throws ServletException {
		super.init();
	}

	public void initialize() throws ServletException {
		super.initialize();
	}

	
}
