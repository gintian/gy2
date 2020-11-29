package com.hjsj.hrms.businessobject.general.sprelationmap;

import com.hjsj.hrms.businessobject.org.yfileschart.LoadFont;
import com.hjsj.hrms.businessobject.org.yfileschart.MyLablePainter;
import com.hjsj.hrms.businessobject.org.yfileschart.MyNodePainter;
import com.hjsj.hrms.businessobject.org.yfileschart.OrgMapBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.org.yfileschart.OrgMapServlet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.yworks.yfiles.server.tiles.servlet.BaseServlet;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import y.base.Edge;
import y.base.EdgeCursor;
import y.base.EdgeMap;
import y.base.Node;
import y.layout.LayoutOrientation;
import y.layout.PortConstraintKeys;
import y.layout.hierarchic.IncrementalHierarchicLayouter;
import y.layout.hierarchic.incremental.SimplexNodePlacer;
import y.view.*;
import y.view.YLabel.Factory;
import y.view.YLabel.Painter;

import javax.sql.RowSet;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

/**
 * 
 * @author lizw
 *
 */
public class RelationMapBo {
	private Connection conn;
	private UserView userView;
	private String relationType;
	/**如果节点展示照片，为照片的路径*/
	public static String imagesPath;
	public static HashMap colorMap = new HashMap();
	public static HashMap colorMap2 = new HashMap();
	/**页面中右侧屏幕的宽度*/
	private double clientWidth=0.0;
	/**页面中右侧屏幕的高度*/
	private double clientHeight=0.0;
	/**页面中，画图面板的实际高度*/
	private double  trueWidth=0.0;
	/**页面中，画图面板的实际宽度*/
	private double trueHeight=0.0;
	
	private double scrollHeight=0.0;
	
	private double scrollWidth=0.0;
	private boolean isKAI=true;//判断是展开还是收起操作
	
	private String menuConstantTop;
	
	private String menuConstantLeft;
	
	private RelationMapNode currentNode=null;//当前点击节点
	
	private RelationMapNode rightNode=null;//最右边的节点
	
	private RelationMapNode bottomNode=null;//最下面的节点
	private String a_code="";
	private String nodeType="";//parnet,self,child;
	/** Parameters Object*/
	public static ChartParameterCofig chartParam=null;
	
	private HashMap nodeHintInfo = new HashMap();
	
	private HashMap linkMap = new HashMap();
	
	
	public RelationMapBo(Connection conn,UserView userView,String relationType){
		this.conn=conn;
		this.userView=userView;
		this.relationType=relationType;
		//if(RelationMapBo.chartParam==null)
			RelationMapBo.chartParam=this.getChartParameter();
		nodeHintInfo.clear();
	}
	public RelationMapBo(Connection conn,UserView userView){
		this.conn=conn;
		this.userView=userView;
		nodeHintInfo.clear();
	}
	public ArrayList getRelationList(String a_code){
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try{
			if("UN".equalsIgnoreCase(a_code)) {
                return list;
            }
			
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql = new StringBuffer("");
			
			if(a_code == null || "".equals(a_code)){
				sql.append("select Relation_id,cname,actor_type,'A' as rtype from t_wf_relation where validflag=1 and actor_type<>4 ");
			}else{
			
				sql.append(" select Relation_id,cname,actor_type,'A' as rtype from t_wf_relation where validflag=1 ");
				sql.append(" and relation_id in (select relation_id from t_wf_mainbody where UPPER(object_id)='");
				sql.append(a_code.toUpperCase()+"' and SP_GRADE=9) and actor_type<>4 union All ");
				
				sql.append(" select Relation_id,cname,actor_type,'B' as rtype from t_wf_relation where validflag=1 ");
				sql.append(" and relation_id in (select relation_id from t_wf_mainbody where UPPER(mainbody_id)='");
				sql.append(a_code.toUpperCase()+"' and SP_GRADE=9) and  ");
				sql.append(" relation_id not in ");
				sql.append("(select relation_id from t_wf_mainbody where UPPER(object_id)='");
				sql.append(a_code.toUpperCase()+"' and SP_GRADE=9) and actor_type<>4 union All ");
				
				sql.append(" select Relation_id,cname,actor_type,'C' as rtype from t_wf_relation where validflag=1 ");
				sql.append(" and relation_id not in ");
				sql.append(" (select relation_id from t_wf_mainbody where UPPER(object_id)='");
				sql.append(a_code.toUpperCase()+"' and SP_GRADE=9)  ");
				sql.append(" and relation_id not in (");
				sql.append(" (select relation_id from t_wf_mainbody where UPPER(mainbody_id)='");
				sql.append(a_code.toUpperCase()+"' and SP_GRADE=9)) and actor_type<>4 ");
				sql.append(" order by Relation_id");
			}
			rs = dao.search(sql.toString());
			while(rs.next()){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("relation_id",rs.getString("relation_id"));
				bean.set("cname",rs.getString("cname"));
				bean.set("actor_type", rs.getString("actor_type"));
				bean.set("a_code",a_code);
				bean.set("rtype", rs.getString("rtype"));
				list.add(bean);
			}
			if(list.size()==0 && a_code != null && !"".equals(a_code)){
				String id=a_code;
				boolean isPerson=false;
				if(!"UN".equalsIgnoreCase(id.substring(0, 2))&&!"UM".equalsIgnoreCase(id.substring(0, 2))&&!"@K".equalsIgnoreCase(id.substring(0, 2)))
				{
					isPerson=true;
					RecordVo vo = new RecordVo(a_code.substring(0,3)+"A01");
					vo.setString("a0100", a_code.substring(3));
					vo = dao.findByPrimaryKey(vo);
					String tt=vo.getString("e01a1");
					String codeset="@K";
					if(tt==null|| "".equals(tt)){
						tt=vo.getString("e0122");
						codeset="UM";
					}
					if(tt==null|| "".equals(tt)){
						tt=vo.getString("b0110");
						codeset="UN";
					}
					id=codeset+tt;
				}
				int i=0;
				if(isPerson){
					rs = dao.search("select Relation_id,cname,actor_type from t_wf_relation where validflag=1 " +
							"and relation_id in (select relation_id from t_wf_mainbody where UPPER(object_id)='"+id.toUpperCase()+"' and SP_GRADE=9) and actor_type<>4 order by seq");
					while(rs.next()){
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("relation_id",rs.getString("relation_id"));
						bean.set("cname",rs.getString("cname"));
						bean.set("actor_type", rs.getString("actor_type"));
						bean.set("a_code",id);
						list.add(bean);
					}
					if(list.size()>0) {
                        i++;
                    }
				}
				
				while(i==0){
					String str="select codesetid,codeitemid from organization where codeitemid=(select parentid from organization where UPPER(codeitemid)='"+id.substring(2).toUpperCase()+"')";
					rs = dao.search(str);
					String newCode="";
					while(rs.next()){
						newCode = rs.getString("codesetid")+rs.getString("codeitemid");
					}
					if(newCode==null|| "".equals(newCode)) {
                        i++;
                    }
					rs = dao.search("select Relation_id,cname,actor_type from t_wf_relation where validflag=1 " +
							"and relation_id in (select relation_id from t_wf_mainbody where UPPER(object_id)='"+newCode.toUpperCase()+"' and SP_GRADE=9) and actor_type<>4 order by seq");
					while(rs.next()){
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("relation_id",rs.getString("relation_id"));
						bean.set("cname",rs.getString("cname"));
						bean.set("actor_type", rs.getString("actor_type"));
						bean.set("a_code",newCode);
						list.add(bean);
					}
					if(list.size()>0||newCode.equalsIgnoreCase(id)){
						i++;
					}else{
						id=newCode;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}
	public HashMap getRelationDataMap(String a_code){
		HashMap map = new HashMap();
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList nodeList = new ArrayList();
			ArrayList lineList = new ArrayList();
			if(a_code==null|| "UN".equalsIgnoreCase(a_code)){
				map.put("node",nodeList);
				map.put("line",lineList);
				this.trueHeight=this.getTruePanelHeight();
				this.trueWidth=this.getTruePanelWidth();
				return map;
			}
			ArrayList toolTextList = this.getToolTextList(RelationMapBo.chartParam.getDesc_items(),"desc");
			ArrayList hintTextList = this.getToolTextList(RelationMapBo.chartParam.getHint_items(), "hint");
			ArrayList relationList = this.getRelationList(a_code);
			RelationMapBo.createTempDir(this.userView);
			double panelHeight=0.0;
			double panelWidth=0.0;
			boolean isCircle=false;
			if("circle".equalsIgnoreCase(RelationMapBo.chartParam.getShape())) {
                isCircle=true;
            }
			boolean isHX=false;
			if("2".equals(RelationMapBo.chartParam.getDirection())) {
                isHX=true;
            }
			if(isHX){
				for(int i=0;i<relationList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)relationList.get(i);
					String relation_id=(String)bean.get("relation_id");
					ArrayList alist = new ArrayList();
					String currCode=(String)bean.get("a_code");
					this.setA_code(currCode);
					alist.add(this.getBaseInfo(currCode,"",toolTextList,hintTextList));
					HashMap amap = new HashMap();
					this.doMethod(dao, currCode, relation_id, alist,toolTextList,hintTextList,amap);	
					BigDecimal aa = RelationMapBo.getValue(ChartParameterCofig.CONSTANT_LEFT,ChartParameterCofig.LEFT_SPACE, "+");
					BigDecimal bb = RelationMapBo.getValue((alist.size()+2)+"", RelationMapBo.chartParam.getWidth(), "*");
					if(isCircle) {
                        bb = RelationMapBo.getValue((alist.size()+2)+"", RelationMapBo.chartParam.getCircle(), "*");
                    }
					BigDecimal cc = RelationMapBo.getValue((alist.size()+2)+"", RelationMapBo.chartParam.getLr_spacing(), "*");
					double sumWidth=aa.add(bb).add(cc).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).doubleValue();
					if(sumWidth>panelWidth) {
                        panelWidth=sumWidth;
                    }
					bean.set("alist",alist);
				}
				BigDecimal aa = RelationMapBo.getValue(ChartParameterCofig.Y_TOP,ChartParameterCofig.Y_BOTTOM, "+").setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
				BigDecimal bb = RelationMapBo.getValue(relationList.size()+"",RelationMapBo.chartParam.getHeight(), "*").setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
				if(isCircle) {
                    bb = RelationMapBo.getValue(relationList.size()+"",RelationMapBo.chartParam.getCircle(), "*").setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
                }
				BigDecimal cc = RelationMapBo.getValue(relationList.size()+"",RelationMapBo.chartParam.getTb_spacing(), "*").setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
				BigDecimal dd=aa.add(bb).add(cc);
				panelHeight=dd.doubleValue();
				this.trueHeight=panelHeight;
				this.trueHeight=this.getTruePanelHeight();
				this.trueWidth=panelWidth;
				this.trueWidth=this.getTruePanelWidth();
				
				RelationMapNode root=new RelationMapNode();
				String rootName = this.getRootName(a_code);
				root.setName(rootName);
				root.setId("ROOT");
				root.setWidth(RelationMapBo.chartParam.getWidth());
				
				if(relationList.size()<=1){
					if(isCircle){
						root.setX(RelationMapBo.getValue(ChartParameterCofig.CONSTANT_LEFT,ChartParameterCofig.LEFT_SPACE, "+").add(new BigDecimal(RelationMapBo.chartParam.getRadius())).toString());
						BigDecimal BigY = RelationMapBo.getValue(this.trueHeight+"",RelationMapBo.getValue(ChartParameterCofig.Y_TOP,ChartParameterCofig.CONSTANT_TOP, "+").add(new BigDecimal(RelationMapBo.chartParam.getRadius())).toString(), "-");
						root.setY(BigY.toString());
					}else{
						root.setX(RelationMapBo.getValue(ChartParameterCofig.CONSTANT_LEFT,ChartParameterCofig.LEFT_SPACE, "+").toString());
						BigDecimal t=new BigDecimal(RelationMapBo.chartParam.getRadius());
						BigDecimal BigY = RelationMapBo.getValue(this.trueHeight+"",RelationMapBo.getValue(ChartParameterCofig.Y_TOP,ChartParameterCofig.CONSTANT_TOP, "+").toString(), "-").subtract(t);
						BigDecimal zz=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"4", "/");
						root.setY(BigY.add(zz).toString());
					}
				}else{
					if(isCircle){
						root.setX(RelationMapBo.getValue(ChartParameterCofig.CONSTANT_LEFT,ChartParameterCofig.LEFT_SPACE, "+").add(new BigDecimal(RelationMapBo.chartParam.getRadius())).toString());
						BigDecimal ee=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(),relationList.size()+"", "*");
						BigDecimal lr_space=RelationMapBo.getValue(RelationMapBo.chartParam.getTb_spacing(),relationList.size()+"", "*");
						BigDecimal er=ee.add(lr_space).divide(new BigDecimal("2"));
						BigDecimal c=RelationMapBo.getValue(ChartParameterCofig.Y_TOP, ChartParameterCofig.CONSTANT_TOP, "+");
						BigDecimal height=new BigDecimal(String.valueOf(this.trueHeight)).subtract(er).subtract(c);
						BigDecimal ff=height;
						root.setY(ff.toString());
					}else{
						root.setX(RelationMapBo.getValue(ChartParameterCofig.CONSTANT_LEFT,ChartParameterCofig.LEFT_SPACE, "+").toString());
						BigDecimal ee=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),relationList.size()+"", "*");
						BigDecimal lr_space=RelationMapBo.getValue(RelationMapBo.chartParam.getTb_spacing(),relationList.size()+"", "*");
						BigDecimal zz=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"4", "/");
						BigDecimal er=ee.add(lr_space).divide(new BigDecimal("2"));
						BigDecimal c=RelationMapBo.getValue(ChartParameterCofig.Y_TOP, ChartParameterCofig.CONSTANT_TOP, "+");
						BigDecimal height=new BigDecimal(this.trueHeight).subtract(er).subtract(c);
						BigDecimal ff=height.add(zz);
						root.setY(ff.toString());
					}
				}
				root.setShape(RelationMapBo.chartParam.getShape());
				root.setRadius(RelationMapBo.chartParam.getRadius());
				root.setHeight(RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2", "/").toString());
				this.setMenuConstantTop(RelationMapBo.getValue(ChartParameterCofig.Y_TOP,ChartParameterCofig.CONSTANT_TOP,"+").toString());
				root.setColor(ChartParameterCofig.TOP_COLOR);
				nodeList.add(root);
				for(int i=0;i<relationList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)relationList.get(i);
					String relation_id=(String)bean.get("relation_id");
					String color=RelationMapBo.getColorImpl(relation_id);
					String cname=(String)bean.get("cname");
					RelationMapNode onenode=new RelationMapNode();
					onenode.setName(cname);
					onenode.setId("Node"+i);
					onenode.setHeight(RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2","/").toString());
					if(isCircle){
						BigDecimal ee=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+");
						String ff=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE,ChartParameterCofig.CONSTANT_LEFT,"+").add(ee).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						ff=RelationMapBo.getValue(ff, RelationMapBo.chartParam.getRadius(),"+").toString();
						onenode.setX(ff);
						BigDecimal gg=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+");
						BigDecimal ii=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(),"1", "*").add(new BigDecimal(RelationMapBo.chartParam.getTb_spacing())).multiply(new BigDecimal(i+"")).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP);
						String hh=new BigDecimal(this.trueHeight+"").subtract(gg.add(ii)).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						hh=RelationMapBo.getValue(hh, RelationMapBo.chartParam.getRadius(), "-").toString();
						onenode.setY(hh.toString());

					}else{
						BigDecimal ee=RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+");
						String ff=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE,ChartParameterCofig.CONSTANT_LEFT,"+").add(ee).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						onenode.setX(ff);
						BigDecimal gg=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+").add(RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"4","/"));
						BigDecimal ii=new BigDecimal(RelationMapBo.chartParam.getHeight()).add(new BigDecimal(RelationMapBo.chartParam.getTb_spacing())).multiply(new BigDecimal(i+"")).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP);
						String hh=new BigDecimal(this.trueHeight+"").subtract(gg.add(ii)).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						onenode.setY(hh.toString());
					}
					
					onenode.setWidth(RelationMapBo.chartParam.getWidth());
					onenode.setColor(color);
					onenode.setShape(RelationMapBo.chartParam.getShape());
					onenode.setRadius(RelationMapBo.chartParam.getRadius());
					nodeList.add(onenode);
					RelationMapLine aline = new RelationMapLine();
					aline.setFrom("ROOT");
					aline.setTo(onenode.getId());
					aline.setArrowAtEnd("0");
					aline.setArrowAtStart("0");
					aline.setColor(color);
					lineList.add(aline);
					if(i==0){
						this.setMenuConstantLeft(RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE,ChartParameterCofig.CONSTANT_LEFT,"+").toString());
					}
					ArrayList alist = (ArrayList)bean.get("alist");	
					for(int j=0;j<alist.size();j++){
						LazyDynaBean abean=(LazyDynaBean)alist.get(j);
						RelationMapNode node=new RelationMapNode();
						String mainbody_id=(String)abean.get("mainbody_id");
						node.setId(relation_id+"`"+((String)abean.get("mainbody_id")).toUpperCase());
						if("true".equalsIgnoreCase(RelationMapBo.chartParam.getShow_pic())){
					        String tempFile=this.createPhotoFile(mainbody_id.substring(0,3)+ "A00", mainbody_id.substring(3), "P");
					        if(tempFile!=null&&!"".equals(tempFile)){
				    	    	node.setImageNode("1");
				    	    	node.setImageurl("images/"+tempFile);
					        }
						}
						if(j==0){
							RelationMapLine bline = new RelationMapLine();
							bline.setFrom(onenode.getId());
							bline.setTo(node.getId());
							bline.setArrowAtEnd("0");
							bline.setArrowAtStart("0");
							bline.setColor(color);
							lineList.add(bline);
						}
					    node.setName((String)abean.get("a0101"));
					    node.setToolText((String)abean.get("tooltext"));
					    if(isCircle){
					    	BigDecimal ee=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE, ChartParameterCofig.CONSTANT_LEFT, "+");
						    BigDecimal ff=RelationMapBo.getValue(j+"", RelationMapBo.chartParam.getCircle(), "*");
						    BigDecimal gg=RelationMapBo.getValue(j+"", RelationMapBo.chartParam.getLr_spacing(), "*");
						    BigDecimal ll=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(),"+").multiply(new BigDecimal("2"));
						    String xx=ee.add(ff).add(gg).add(ll).toString();
						    xx=RelationMapBo.getValue(xx, RelationMapBo.chartParam.getRadius(), "+").toString();
					    	node.setX(xx);
						    BigDecimal hh=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+");
							BigDecimal jj=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(i+""));
							String kk=new BigDecimal(this.trueHeight+"").subtract(hh).subtract(jj).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						    kk=RelationMapBo.getValue(kk, RelationMapBo.chartParam.getRadius(), "-").toString();
							node.setY(kk);
					    }else{
					    	BigDecimal ee=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE, ChartParameterCofig.CONSTANT_LEFT, "+");
						    BigDecimal ff=RelationMapBo.getValue(j+"", RelationMapBo.chartParam.getWidth(), "*");
						    BigDecimal gg=RelationMapBo.getValue(j+"", RelationMapBo.chartParam.getLr_spacing(), "*");
						    BigDecimal ll=RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(),"+").multiply(new BigDecimal("2"));
						    String xx=ee.add(ff).add(gg).add(ll).toString();
					    	node.setX(xx);
					    	
						    BigDecimal hh=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+");
							BigDecimal jj=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(i+""));
							BigDecimal t=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), "4", "/");
							String kk=new BigDecimal(this.trueHeight+"").subtract(hh.add(jj).add(t)).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						    node.setY(kk);
					    }
					    
					    node.setColor(color);
					    node.setHeight(RelationMapBo.chartParam.getHeight());
					    node.setWidth(RelationMapBo.chartParam.getWidth());
					    node.setShape(RelationMapBo.chartParam.getShape());
					    node.setRadius(RelationMapBo.chartParam.getRadius());
					    if(!("true").equals((String)abean.get("hideInfoLink")))//田野添加判断是否有权限查看基本信息而是否执行node.setLink();
                        {
                            node.setLink("j-reloadDataJS-"+node.getId()+"^"+node.getX()+"^"+node.getY()+"^"+(String)abean.get("hideInfoLink"));
                        }
					    if(j==alist.size()-1){//最后一个几点即为当前节点
					    	node.setColor(ChartParameterCofig.CURROBJECT_COLOR);
					    }
					    nodeList.add(node);
					   // System.out.println(node.toNodeXml());
					    if(!((String)abean.get("object_id")).equalsIgnoreCase(((String)abean.get("mainbody_id")))){
						    RelationMapLine line = new RelationMapLine();
						    line.setTo(relation_id+"`"+((String)abean.get("mainbody_id")).toUpperCase());
						    line.setFrom(relation_id+"`"+((String)abean.get("object_id")).toUpperCase());
						    line.setColor(color);
						    lineList.add(line);
					    }
					}
				}
				
			}else{//
				for(int i=0;i<relationList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)relationList.get(i);
					String relation_id=(String)bean.get("relation_id");
					ArrayList alist = new ArrayList();
					String currCode=(String)bean.get("a_code");
					this.setA_code(currCode);
					alist.add(this.getBaseInfo(currCode,"",toolTextList,hintTextList));
					HashMap amap = new HashMap();
					this.doMethod(dao, currCode, relation_id, alist,toolTextList,hintTextList,amap);	
					/**最上两层的节点高度，取设置高度的一半*/
					BigDecimal aa=RelationMapBo.getValue(ChartParameterCofig.Y_TOP,ChartParameterCofig.Y_BOTTOM, "+");
					BigDecimal bb=RelationMapBo.getValue((alist.size()+1)+"", RelationMapBo.chartParam.getHeight(), "*");
					if(isCircle) {
                        bb=RelationMapBo.getValue((alist.size()+2)+"", RelationMapBo.chartParam.getCircle(), "*");
                    }
					BigDecimal cc=RelationMapBo.getValue((alist.size()+3)+"", RelationMapBo.chartParam.getTb_spacing(), "*");
					double sumHeight=aa.add(bb).add(cc).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).doubleValue();
					if(sumHeight>panelHeight) {
                        panelHeight=sumHeight;
                    }
					bean.set("alist",alist);
				}
				panelWidth =RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal(relationList.size()+1)).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
				if(isCircle) {
                    panelWidth =RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal(relationList.size()+1)).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
				this.trueHeight=panelHeight;
				this.trueHeight=this.getTruePanelHeight();
				this.trueWidth=panelWidth;
				this.trueWidth=this.getTruePanelWidth();
				if(trueHeight>clientHeight){
					scrollHeight=this.trueHeight-this.clientHeight;
				}
				RelationMapNode root=new RelationMapNode();
				String rootName = this.getRootName(a_code);
				root.setName(rootName);
				root.setId("ROOT");
				root.setWidth(RelationMapBo.chartParam.getWidth());
				if(relationList.size()<=1){
					if(isCircle)
					{
						String temp=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE,ChartParameterCofig.CONSTANT_LEFT, "+").add(new BigDecimal(RelationMapBo.chartParam.getRadius())).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						root.setX(temp);
					}else{
						String temp=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE,ChartParameterCofig.CONSTANT_LEFT, "+").toString();
						root.setX(temp);
					}
					
				}else{
					if(isCircle){
						BigDecimal aa=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(),relationList.size()+"", "*");
						BigDecimal lr_space=RelationMapBo.getValue(RelationMapBo.chartParam.getLr_spacing(),relationList.size()+"", "*");
						BigDecimal hh=aa.add(lr_space).divide(new BigDecimal("2")).add(RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE,ChartParameterCofig.CONSTANT_LEFT, "+"));
						String dd=hh.subtract(new BigDecimal(RelationMapBo.chartParam.getCircle()).divide(new BigDecimal("2"))).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						dd=RelationMapBo.getValue(dd, RelationMapBo.chartParam.getRadius(), "+").toString();
						root.setX(dd.toString());
					}else{
						BigDecimal aa=RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(),relationList.size()+"", "*");
						BigDecimal lr_space=RelationMapBo.getValue(RelationMapBo.chartParam.getLr_spacing(),(relationList.size()-1)+"", "*");
						BigDecimal hh=aa.add(lr_space).divide(new BigDecimal("2")).add(RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE,ChartParameterCofig.CONSTANT_LEFT, "+"));
						String dd=hh.subtract(new BigDecimal(RelationMapBo.chartParam.getWidth()).divide(new BigDecimal("2"))).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						root.setX(dd.toString());
					}
				}
				root.setShape(RelationMapBo.chartParam.getShape());
				root.setRadius(RelationMapBo.chartParam.getRadius());
				root.setHeight(RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2", "/").toString());
				String temp=RelationMapBo.getValue(this.trueHeight+"",ChartParameterCofig.Y_TOP, "-").subtract(new BigDecimal(ChartParameterCofig.CONSTANT_TOP)).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
				if(isCircle) {
                    temp=RelationMapBo.getValue(temp,RelationMapBo.chartParam.getRadius(),"-").toString();
                }
				root.setY(temp);
				this.setMenuConstantTop(RelationMapBo.getValue(ChartParameterCofig.Y_TOP,ChartParameterCofig.CONSTANT_TOP,"+").toString());
				root.setColor(ChartParameterCofig.TOP_COLOR);
				nodeList.add(root);
				for(int i=0;i<relationList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)relationList.get(i);
					String relation_id=(String)bean.get("relation_id");
					String color=RelationMapBo.getColorImpl(relation_id);
					String cname=(String)bean.get("cname");
					RelationMapNode onenode=new RelationMapNode();
					onenode.setName(cname);
					onenode.setId("Node"+i);
					onenode.setHeight(RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2","/").toString());
					if(isCircle){
						BigDecimal aa=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal(i+""));
						String bb=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE,ChartParameterCofig.CONSTANT_LEFT,"+").add(aa).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						bb=RelationMapBo.getValue(bb, RelationMapBo.chartParam.getRadius(), "+").toString();
						onenode.setX(bb);
						BigDecimal cc=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+");
						BigDecimal ii=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(),"0", "+").add(new BigDecimal(RelationMapBo.chartParam.getTb_spacing()));
						String dd=new BigDecimal(this.trueHeight+"").subtract(cc.add(ii)).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						dd=RelationMapBo.getValue(dd,RelationMapBo.chartParam.getRadius(),"-").toString();
						onenode.setY(dd.toString());
					}else{
						BigDecimal aa=RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal(i+""));
						String bb=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE,ChartParameterCofig.CONSTANT_LEFT,"+").add(aa).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						onenode.setX(bb);
						BigDecimal cc=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+");
						BigDecimal ii=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2", "/").add(new BigDecimal(RelationMapBo.chartParam.getTb_spacing()));
						String dd=new BigDecimal(this.trueHeight+"").subtract(cc.add(ii)).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						onenode.setY(dd.toString());
					}
					
					onenode.setWidth(RelationMapBo.chartParam.getWidth());
					onenode.setColor(color);
					onenode.setShape(RelationMapBo.chartParam.getShape());
					onenode.setRadius(RelationMapBo.chartParam.getRadius());
					nodeList.add(onenode);
					RelationMapLine aline = new RelationMapLine();
					aline.setFrom("ROOT");
					aline.setTo(onenode.getId());
					aline.setArrowAtEnd("0");
					aline.setArrowAtStart("0");
					aline.setColor(color);
					lineList.add(aline);
					if(i==0){
						this.setMenuConstantLeft(RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE,ChartParameterCofig.CONSTANT_LEFT,"+").toString());
					}
					ArrayList alist = (ArrayList)bean.get("alist");	
					for(int j=0;j<alist.size();j++){
						LazyDynaBean abean=(LazyDynaBean)alist.get(j);
						RelationMapNode node=new RelationMapNode();
						String mainbody_id=(String)abean.get("mainbody_id");
						node.setId(relation_id+"`"+((String)abean.get("mainbody_id")).toUpperCase());
						if("true".equalsIgnoreCase(RelationMapBo.chartParam.getShow_pic())){
					        String tempFile=this.createPhotoFile(mainbody_id.substring(0,3)+ "A00", mainbody_id.substring(3), "P");
					        if(tempFile!=null&&!"".equals(tempFile)){
				    	    	node.setImageNode("1");
				    	    	node.setImageurl("images/"+tempFile);
					        }
						}
						if(j==0){
							RelationMapLine bline = new RelationMapLine();
							bline.setFrom(onenode.getId());
							bline.setTo(node.getId());
							bline.setArrowAtEnd("0");
							bline.setArrowAtStart("0");
							bline.setColor(color);
							lineList.add(bline);
						}
					    node.setName((String)abean.get("a0101"));
					    node.setToolText((String)abean.get("tooltext"));
					    if(isCircle){
					    	String ee=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE, ChartParameterCofig.CONSTANT_LEFT, "+").add(RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal(i+""))).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
							ee=RelationMapBo.getValue(ee, RelationMapBo.chartParam.getRadius(), "+").toString();
						    node.setX(ee.toString());
						    
						    BigDecimal ff=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+");
							BigDecimal gg=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(),"2", "*").add(new BigDecimal(RelationMapBo.chartParam.getTb_spacing()).multiply(new BigDecimal("3")));
							BigDecimal jj=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(j+""));
							String kk=new BigDecimal(this.trueHeight+"").subtract(ff).subtract(gg).subtract(jj).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
							kk=RelationMapBo.getValue(kk,RelationMapBo.chartParam.getRadius(),"-").toString();
						    node.setY(kk);
					    }else{
					    	String ee=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE, ChartParameterCofig.CONSTANT_LEFT, "+").add(RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal(i+""))).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						    node.setX(ee.toString());
						    BigDecimal ff=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+");
							BigDecimal gg=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"1", "*").add(new BigDecimal(RelationMapBo.chartParam.getTb_spacing()).multiply(new BigDecimal("3")));
							BigDecimal jj=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(j+""));
							String kk=new BigDecimal(this.trueHeight+"").subtract(ff).subtract(gg).subtract(jj).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						    node.setY(kk);
					    }
					    
					    node.setColor(color);
					    node.setHeight(RelationMapBo.chartParam.getHeight());
					    node.setWidth(RelationMapBo.chartParam.getWidth());
					    node.setShape(RelationMapBo.chartParam.getShape());
					    node.setRadius(RelationMapBo.chartParam.getRadius());
					    if(!("true").equals((String)abean.get("hideInfoLink")))//田野添加判断是否有权限查看基本信息而是否执行node.setLink();
                        {
                            node.setLink("j-reloadDataJS-"+node.getId()+"^"+node.getX()+"^"+node.getY()+"^"+(String)abean.get("hideInfoLink"));
                        }
					    if(j==alist.size()-1){//最后一个几点即为当前节点
					    	node.setColor(ChartParameterCofig.CURROBJECT_COLOR);
					    }
					    nodeList.add(node);
					   // System.out.println(node.toNodeXml());
					    if(!((String)abean.get("object_id")).equalsIgnoreCase(((String)abean.get("mainbody_id")))){
						    RelationMapLine line = new RelationMapLine();
						    line.setTo(relation_id+"`"+((String)abean.get("mainbody_id")).toUpperCase());
						    line.setFrom(relation_id+"`"+((String)abean.get("object_id")).toUpperCase());
						    line.setColor(color);
						    lineList.add(line);
					    }
					}
				}
			}
			map.put("node",nodeList);
			map.put("line",lineList);
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return map;
	}
	public ArrayList getToolTextList(String param,String type){
		ArrayList toolTextList = new ArrayList();
		RowSet rs = null;
		try{
			if(param==null|| "".equals(param.trim())){
				if("hint".equals(type)){
					FieldItem item = DataDictionary.getFieldItem("b0110");
					if(item!=null) {
                        toolTextList.add(this.getBean(item.getItemid(), item.getItemtype(), item.getCodesetid(),0,item.getItemdesc(),"A01"));
                    }
                    item=DataDictionary.getFieldItem("e0122");
                    if(item!=null) {
                        toolTextList.add(this.getBean(item.getItemid(), item.getItemtype(), item.getCodesetid(),0,item.getItemdesc(),"A01"));
                    }
                    item=DataDictionary.getFieldItem("e01a1");
                    if(item!=null) {
                        toolTextList.add(this.getBean(item.getItemid(), item.getItemtype(), item.getCodesetid(),0,item.getItemdesc(),"A01"));
                    }
                    item=DataDictionary.getFieldItem("A0101");
                    if(item!=null) {
                        toolTextList.add(this.getBean(item.getItemid(), item.getItemtype(), item.getCodesetid(),0,item.getItemdesc(),"A01"));
                    }
			    	
				}else if("desc".equalsIgnoreCase(type)){
					FieldItem item = DataDictionary.getFieldItem("b0110");
					if(item!=null) {
                        toolTextList.add(this.getBean(item.getItemid(), item.getItemtype(), item.getCodesetid(),0,item.getItemdesc(),"A01"));
                    }
				    item=DataDictionary.getFieldItem("e0122");
	                if(item!=null) {
                        toolTextList.add(this.getBean(item.getItemid(), item.getItemtype(), item.getCodesetid(),0,item.getItemdesc(),"A01"));
                    }
	                item=DataDictionary.getFieldItem("e01a1");
	                if(item!=null) {
                        toolTextList.add(this.getBean(item.getItemid(), item.getItemtype(), item.getCodesetid(),0,item.getItemdesc(),"A01"));
                    }
	                item=DataDictionary.getFieldItem("A0101");
	                if(item!=null) {
                        toolTextList.add(this.getBean(item.getItemid(), item.getItemtype(), item.getCodesetid(),0,item.getItemdesc(),"A01"));
                    }

				}
			}else{
				String[] arr=param.split(",");
				for(int i=0;i<arr.length;i++){
					if(arr[i]==null|| "".equals(arr[i])) {
                        continue;
                    }
					FieldItem item=DataDictionary.getFieldItem(arr[i].toLowerCase());
					if(item!=null){
						if(!"0".equals(this.userView.analyseFieldPriv(item.getItemid()))) {
                            toolTextList.add(this.getBean(item.getItemid(), item.getItemtype(), item.getCodesetid(),0,item.getItemdesc(),item.getFieldsetid()));
                        }
					}
				}	
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return toolTextList;
	}
	public LazyDynaBean getBean(String itemid,String itemtype,String codesetid,int scale,String itemdesc,String fieldset){
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("itemid",itemid);
		bean.set("itemtype",itemtype);
		bean.set("codesetid",codesetid);
		bean.set("scale",scale+"");
		bean.set("itemdesc",itemdesc);
		bean.set("fieldset", fieldset);
		return bean;
	}
	 public static void createTempDir(UserView userView) {
	        if (RelationMapBo.imagesPath == null) {
	            throw new RuntimeException(
	                    "Images directory is not exists.");
	        }
	        File tempDir = new File(RelationMapBo.imagesPath);
	        String fileprefix=userView.getUserName().toUpperCase()+"-";
	        if(tempDir.exists())
	        {
	        	File[] fileList = tempDir.listFiles();
	        	for(int i=0;i<fileList.length;i++){
	        		File file = fileList[i];
	        		if(file.getName().toUpperCase().startsWith(fileprefix)) {
                        file.delete();
                    }
	        	}
	        }
	        if (!tempDir.exists()) {
	            tempDir.mkdirs();
	        }
	    }
	 public String createPhotoFile(String userTable, String userNumber,
				String flag) throws Exception {
			File tempFile = null;
			String filename = "";
			if("UN".equalsIgnoreCase(userTable.substring(0, 2))|| "UM".equalsIgnoreCase(userTable.substring(0, 2))|| "@K".equalsIgnoreCase(userTable.substring(0, 2))) {
                return filename;
            }
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet = null;
			InputStream in = null;
		 	java.io.FileOutputStream fout = null;
			try {
				StringBuffer strsql = new StringBuffer();
				strsql.append("select ext,Ole from ");
				strsql.append(userTable);
				strsql.append(" where A0100='");
				strsql.append(userNumber);
				strsql.append("' and Flag='");
				strsql.append(flag);
				strsql.append("'");
				rowSet=dao.search(strsql.toString());
				if (rowSet.next()) {
					tempFile = File.createTempFile(PubFunc.getStrg()+"-",rowSet.getString("ext"), new File(RelationMapBo.imagesPath));
					in = rowSet.getBinaryStream("Ole");
					fout = new java.io.FileOutputStream(tempFile);
					int len;
					byte buf[] = new byte[1024];
					while ((len = in.read(buf, 0, 1024)) != -1) {
						fout.write(buf, 0, len);
					}
					filename = tempFile.getName();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			finally{
				PubFunc.closeResource(fout);
				PubFunc.closeIoResource(in);
				PubFunc.closeResource(rowSet);
			}
			return filename;
		}
	public HashMap parseDataXml(String xml,String currentNodeId,String freshType,String nodeType){
		HashMap map = new HashMap();
		try{
			ArrayList nodeList = new ArrayList();
			ArrayList lineList = new ArrayList();
			HashMap connectorMap = new HashMap();
			HashMap nodeIdMap = new HashMap();
			HashMap childrenMap = new HashMap();
			ArrayList anodeList = new ArrayList();
			ArrayList alineList = new ArrayList();
			if("1".equals(freshType)){
				currentNodeId="-1`X";
			}
			String[] temp = currentNodeId.split("`");
			String relation_id=temp[0];
			String mainbody_id=temp[1];
			int layer = 1;//共有几层下级
			this.getChildrenLink(childrenMap, relation_id, mainbody_id, new ContentDAO(this.conn),layer);
			int visibleChildren = 0;//页面上展现出的孩子节点数量
			int lay=1;//页面展现孩子节点的层数
			ArrayList currChildren = this.getCurrentNodeChildren(currentNodeId, childrenMap);
			visibleChildren=this.parseXml(xml, anodeList, alineList, nodeIdMap, connectorMap,childrenMap,currentNodeId,lay,freshType);
			//田野修改添加了判断其他节点是否移动 修改判断父节点方法，不在现有的汇报关系图里的父节点应该不算数，并把查询父节点代码提到此处
			 ArrayList parentList = this.getParentList(relation_id, this.getA_code());
			    for(int p = 0 ;p< parentList.size();p++){
			    	LazyDynaBean bean =(LazyDynaBean )parentList.get(p);
			    	String  parenMainbody_id =(String)bean.get("mainbody_id");
			    	if(!connectorMap.containsKey(relation_id+"`"+parenMainbody_id)){
			    		parentList.remove(p);
			    	}
			    }
				int csize = currChildren.size();
				int psize = parentList.size();
			boolean isCircle=false;
			if("circle".equalsIgnoreCase(RelationMapBo.chartParam.getShape())) {
                isCircle=true;
            }
			boolean isHX=false;
			if("2".equals(RelationMapBo.chartParam.getDirection())) {
                isHX=true;
            }
		    boolean isYAdd=true;
		    if("1".equals(freshType)){//刷新页面
		    	for(int i=0;i<anodeList.size();i++){
					RelationMapNode node = (RelationMapNode)anodeList.get(i);
					String id=node.getId();
					if("root".equalsIgnoreCase(id)){
						node.setColor(ChartParameterCofig.TOP_COLOR);
					}else if(id.indexOf("`")!=-1){
						String[] arr= id.split("`");
						if(arr[1].equalsIgnoreCase(this.getA_code())) {
                            node.setColor(ChartParameterCofig.CURROBJECT_COLOR);
                        } else {
                            node.setColor(RelationMapBo.getColorImpl(arr[0]));
                        }
					}else{
						
					}
					if("rectangle".equalsIgnoreCase(node.getShape())&& "circle".equalsIgnoreCase(RelationMapBo.chartParam.getShape()))//1：矩形 2：圆形
					{
						node.setX(RelationMapBo.getValue(node.getX(), RelationMapBo.chartParam.getRadius(), "+").toString());
						node.setY(RelationMapBo.getValue(node.getY(), RelationMapBo.chartParam.getRadius(), "-").toString());
					}else if("circle".equalsIgnoreCase(node.getShape())&& "rectangle".equalsIgnoreCase(RelationMapBo.chartParam.getShape())){
						node.setX(RelationMapBo.getValue(node.getX(), RelationMapBo.chartParam.getRadius(), "-").toString());
						node.setY(RelationMapBo.getValue(node.getY(), RelationMapBo.chartParam.getRadius(), "+").toString());
					}
					node.setShape(RelationMapBo.chartParam.getShape());
					nodeList.add(node);
				}
		    	for(int i=0;i<alineList.size();i++){
					RelationMapLine line = (RelationMapLine)alineList.get(i);
					line.setColor(RelationMapBo.getColorImpl(relation_id));
					lineList.add(line);
				}
		    	
		    }else if(currChildren.size()==0){//如果没有下级节点，按原样展示
		    	for(int i=0;i<anodeList.size();i++){
					RelationMapNode node = (RelationMapNode)anodeList.get(i);
					String id=node.getId();
					nodeList.add(node);
				}
		    	for(int i=0;i<alineList.size();i++){
					RelationMapLine line = (RelationMapLine)alineList.get(i);
					line.setColor(RelationMapBo.getColorImpl(relation_id));
					lineList.add(line);
				}
		    }else if(this.isKAI){//展开操作
		    	if(isHX){
		    		double increamHeight=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(currChildren.size()+"")).doubleValue();
			    	if(isCircle) {
                        increamHeight=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(currChildren.size()+"")).doubleValue();
                    }
				    if(currChildren.size()==1){
				    	increamHeight=0.0;
				    }
				    /*
					田野修改前代码
					ArrayList parentList = this.getParentList(relation_id, this.getA_code());
					int csize = currChildren.size();
					int psize = parentList.size();
				     */
					BigDecimal aa0=new BigDecimal("0");
					if(psize<csize)//其他节点用移动
					{
					    int s=csize-psize;
					    BigDecimal aa1=RelationMapBo.getValue(RelationMapBo.getValue(RelationMapBo.chartParam.getTb_spacing(),RelationMapBo.chartParam.getHeight(), "+").toString(), "2", "/");
					    if(isCircle) {
                            aa1=RelationMapBo.getValue(RelationMapBo.getValue(RelationMapBo.chartParam.getTb_spacing(),RelationMapBo.chartParam.getCircle(), "+").toString(), "2", "/");
                        }

					    aa0=aa1.multiply(new BigDecimal(s));
					    //increamHeight=increamHeight/2;
					}else{
						increamHeight=0;
					}
			    	double curHeight=this.trueHeight;
				    this.trueHeight=RelationMapBo.getValue(this.trueHeight+"",increamHeight+"","+").doubleValue();
				    this.trueHeight=this.getTruePanelHeight();//获得真实高度，哪个高用那个
				    BigDecimal cc=null;
				    BigDecimal bb=null;
				    if(isCircle){
				    	cc=RelationMapBo.getValue(currChildren.size()+"", RelationMapBo.chartParam.getCircle(), "*");
				    	bb=RelationMapBo.getValue((currChildren.size()-1)+"", RelationMapBo.chartParam.getTb_spacing(), "*");
				    }else{
				    	cc=RelationMapBo.getValue(currChildren.size()+"", RelationMapBo.chartParam.getHeight(), "*");
				    	bb=RelationMapBo.getValue((currChildren.size()-1)+"", RelationMapBo.chartParam.getTb_spacing(), "*");
				    }
				    double childHeight=cc.add(bb).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).doubleValue();// 展示孩子需要的总宽
				    double currentX=RelationMapBo.getValue(this.currentNode.getX(),"0","+").doubleValue();//当前的节点的X
				    BigDecimal currentY=RelationMapBo.getValue(this.currentNode.getY(),"0","+");//当前节点的y
				    
				    double newCurrentY=RelationMapBo.getValue(currentY+"", RelationMapBo.getValue(childHeight+"", "2", "/").toString(), "-").doubleValue();//当前节点的新X
				    double aa=RelationMapBo.getValue(newCurrentY+"",RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2","/").toString(),"-").doubleValue();//当前节点新位置的中间点
				    double begin=RelationMapBo.getValue(aa+"",RelationMapBo.getValue(childHeight+"","2","/").toString(),"+").doubleValue();
				   
				    double childX=RelationMapBo.getValue(currentX+"",RelationMapBo.chartParam.getWidth(),"+").add(new BigDecimal(RelationMapBo.chartParam.getLr_spacing())).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();//孩子几点的y坐标
				    if(isCircle) {
                        childX=RelationMapBo.getValue(currentX+"",RelationMapBo.chartParam.getCircle(),"+").add(new BigDecimal(RelationMapBo.chartParam.getLr_spacing())).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();//孩子几点的y坐标
                    }
				   // BigDecimal width=RelationMapBo.getValue(this.rightNode.getX(), RelationMapBo.chartParam.getWidth(),"+");
				    BigDecimal bestBottom = RelationMapBo.getValue(this.bottomNode.getY(), RelationMapBo.chartParam.getHeight(), "-");
				    BigDecimal bBottom = RelationMapBo.getValue(begin+"", increamHeight+"","-");
				    BigDecimal bestM=null;
				    if(bestBottom.compareTo(bBottom)<0)
				    {
				    	bestM=bestBottom;  
				    }else{
				    	bestM=bBottom;
				    }
				    if(bestM.compareTo(new BigDecimal("0"))>=0) {
                        isYAdd=false;
                    }
				    BigDecimal bestRight=new BigDecimal("0");
					bestRight.setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP);
					String curY=this.currentNode.getY();
					for(int i=0;i<anodeList.size();i++){
						RelationMapNode node = (RelationMapNode)anodeList.get(i);
						String key=node.getId().toUpperCase();
						String relationid=key.split("`")[0];
						BigDecimal old=new BigDecimal(node.getY()).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
						if(psize<csize&&("root".equalsIgnoreCase(key)||Integer.parseInt(relationid)>=Integer.parseInt(relation_id))){
							if("root".equalsIgnoreCase(key)||Integer.parseInt(relationid)>Integer.parseInt(relation_id)){
								String y=RelationMapBo.getValue(node.getY(),increamHeight+"", "-").toString();
					        	 node.setY(y);
							}else{
								String y=RelationMapBo.getValue(node.getY(),aa0.toString(), "-").toString();
					        	 node.setY(y);
							}
						}
						if(isYAdd){
				    		double newY=RelationMapBo.getValue(node.getY(),increamHeight+"","+").doubleValue();
					    	node.setY(newY+"");
						}
						BigDecimal xx=RelationMapBo.getValue(node.getX(),RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+").toString(),"+");
				        if(isCircle) {
                            xx=RelationMapBo.getValue(node.getX(),RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+").toString(),"+");
                        }
						if(xx.compareTo(bestRight)>=0) {
                            bestRight=xx;
                        }
						nodeList.add(node);
					}
					for(int i=0;i<alineList.size();i++){
						RelationMapLine line = (RelationMapLine)alineList.get(i);
						if(connectorMap.get(line.getTo().toUpperCase())!=null&&childrenMap.get(line.getTo().toUpperCase())!=null) {
                            continue;
                        }
						lineList.add(line);
					}
					
					String color=RelationMapBo.getColorImpl(relation_id);
					BigDecimal pyl = new BigDecimal("0");
					if(psize>=csize){
						BigDecimal aa1 = RelationMapBo.getValue(curY,RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2","/").toString(), "+");
						if(isCircle) {
                            aa1=new BigDecimal(this.currentNode.getY());
                        }
						
						BigDecimal aa2=new BigDecimal(csize).multiply(new BigDecimal(RelationMapBo.chartParam.getHeight())).add((new BigDecimal(RelationMapBo.chartParam.getTb_spacing()).multiply(new BigDecimal(csize))));
						if(isCircle) {
                            aa2=new BigDecimal(csize).multiply(new BigDecimal(RelationMapBo.chartParam.getCircle())).add((new BigDecimal(RelationMapBo.chartParam.getTb_spacing()).multiply(new BigDecimal(csize))));
                        }
						BigDecimal aa3 = aa2.divide(new BigDecimal("2"));
						pyl=aa1.add(aa3);
						
					}else{
						BigDecimal newCurr=RelationMapBo.getValue(curY, aa0.toString(), "-");
						BigDecimal aa1 = RelationMapBo.getValue(newCurr.toString(),RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2","/").toString(), "+");
						if(isCircle) {
                            aa1=newCurr;
                        }
						
						BigDecimal aa2=new BigDecimal(csize).multiply(new BigDecimal(RelationMapBo.chartParam.getHeight())).add((new BigDecimal(RelationMapBo.chartParam.getTb_spacing()).multiply(new BigDecimal(csize))));
						if(isCircle) {
                            aa2=new BigDecimal(csize).multiply(new BigDecimal(RelationMapBo.chartParam.getCircle())).add((new BigDecimal(RelationMapBo.chartParam.getTb_spacing()).multiply(new BigDecimal(csize))));
                        }
						BigDecimal aa3 = aa2.divide(new BigDecimal("2"));
						pyl=aa1.add(aa3);
					}
					for(int i=0;i<currChildren.size();i++){
						LazyDynaBean abean = (LazyDynaBean)currChildren.get(i);
						RelationMapNode node=new RelationMapNode();
						String object_id=(String)abean.get("object_id");
						node.setId(relation_id+"`"+((String)abean.get("object_id")).toUpperCase());
						if(nodeIdMap.get(node.getId().toUpperCase())!=null&&childrenMap.get(node.getId().toUpperCase())!=null) {
                            continue;
                        }
						if("true".equalsIgnoreCase(RelationMapBo.chartParam.getShow_pic())){
						    String tempFile=this.createPhotoFile(object_id.substring(0,3)+ "A00", object_id.substring(3), "P");
						    if(tempFile!=null&&!"".equals(tempFile)){
						    	node.setImageNode("1");
						    	node.setImageurl("images/"+tempFile);
						    }
						}
					    node.setName((String)abean.get("a0101"));
					    node.setToolText((String)abean.get("tooltext"));
					    if(currChildren.size()==1){
					    	node.setY(currentNode.getY());
					    }else{
					    	BigDecimal yy=pyl.subtract(RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(i+""))).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
					        if(isCircle) {
                                yy=pyl.subtract(RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(i+""))).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
                            }
					    	if(isYAdd) {
                                yy=RelationMapBo.getValue(yy.toString(),increamHeight+"","+");
                            }
					    	node.setY(yy.toString());
					    }
					    
					    node.setX(childX+"");
					    if(this.a_code.equalsIgnoreCase(object_id)) {
                            node.setColor(ChartParameterCofig.CURROBJECT_COLOR);
                        } else {
                            node.setColor(color);
                        }
					    node.setWidth(RelationMapBo.chartParam.getWidth());
					    node.setHeight(RelationMapBo.chartParam.getHeight());
					    node.setShape(RelationMapBo.chartParam.getShape());
					    node.setRadius(RelationMapBo.chartParam.getRadius());
					    if(!("true").equals((String)abean.get("hideInfoLink")))//田野添加判断是否有权限查看基本信息而是否执行node.setLink();
                        {
                            node.setLink("j-reloadDataJS-"+node.getId()+"^"+node.getX()+"^"+node.getY()+"^"+"child"+"^"+(String)abean.get("hideInfoLink"));
                        }
					    nodeList.add(node);
						RelationMapLine line = new RelationMapLine();
						line.setFrom(relation_id+"`"+((String)abean.get("mainbody_id")).toUpperCase());
						line.setTo(relation_id+"`"+((String)abean.get("object_id")).toUpperCase());
						line.setArrowAtEnd("0");
						line.setArrowAtStart("1");
						line.setColor(color);
						lineList.add(line);
					}
					if(!isYAdd) {
                        this.trueHeight=new BigDecimal(curHeight+"").setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
                    }
					this.trueHeight=this.getTruePanelHeight();
					BigDecimal right=bestRight.add(new BigDecimal(RelationMapBo.chartParam.getWidth())).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP);
					if(isCircle) {
                        right=bestRight.add(new BigDecimal(RelationMapBo.chartParam.getCircle())).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP);
                    }
					BigDecimal currRight=RelationMapBo.getValue(childX+"",RelationMapBo.chartParam.getWidth(), "+");
					if(isCircle) {
                        currRight=RelationMapBo.getValue(childX+"",RelationMapBo.chartParam.getCircle(), "+");
                    }
					BigDecimal ri = null;
					if(right.compareTo(currRight)>=0) {
                        ri=right;
                    } else {
                        ri=currRight;
                    }
					if(this.trueWidth<ri.doubleValue()) {
                        this.trueWidth=ri.doubleValue();
                    }
					this.trueWidth=this.getTruePanelWidth();
		    	}else{//纵向展开操作
		    	
			    	double increamHeight=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getTb_spacing(), "+").doubleValue();
			    	if(isCircle) {
                        increamHeight=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getTb_spacing(), "+").doubleValue();
                    }
				    double curHeight=this.trueHeight;
				    this.trueHeight=RelationMapBo.getValue(this.trueHeight+"",increamHeight+"","+").doubleValue();
				    this.trueHeight=this.getTruePanelHeight();//获得真实高度，哪个高用那个
				    BigDecimal cc=null;
				    BigDecimal bb=null;
				    if(isCircle){
				    	cc=RelationMapBo.getValue(currChildren.size()+"", RelationMapBo.chartParam.getCircle(), "*");
				    	bb=RelationMapBo.getValue((currChildren.size()-1)+"", RelationMapBo.chartParam.getLr_spacing(), "*");
				    }else{
				    	cc=RelationMapBo.getValue(currChildren.size()+"", RelationMapBo.chartParam.getWidth(), "*");
				    	bb=RelationMapBo.getValue((currChildren.size())+"", RelationMapBo.chartParam.getLr_spacing(), "*");
				    }
				    double childWidth=cc.add(bb).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).doubleValue();// 展示孩子需要的总宽
				    double currentX=RelationMapBo.getValue(this.currentNode.getX(),"0","+").doubleValue();//当前的节点的X
				    double currentY=RelationMapBo.getValue(this.currentNode.getY(),"0","+").doubleValue();//当前节点的y
				    double newCurrentX=RelationMapBo.getValue(currentX+"", RelationMapBo.getValue(childWidth+"", "2", "/").toString(), "+").doubleValue();//当前节点的新X
				    double aa=RelationMapBo.getValue(newCurrentX+"",RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(),"2","/").toString(),"+").doubleValue();//当前节点新位置的中间点
				    double begin=RelationMapBo.getValue(aa+"",RelationMapBo.getValue(childWidth+"","2","/").toString(),"-").doubleValue();
				    double childY=RelationMapBo.getValue(currentY+"",RelationMapBo.chartParam.getHeight(),"-").subtract(new BigDecimal(RelationMapBo.chartParam.getTb_spacing())).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();//孩子几点的y坐标
				    if(isCircle) {
                        childY=RelationMapBo.getValue(currentY+"",RelationMapBo.chartParam.getCircle(),"-").subtract(new BigDecimal(RelationMapBo.chartParam.getTb_spacing())).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();//孩子几点的y坐标
                    }
				    BigDecimal width=RelationMapBo.getValue(this.rightNode.getX(), RelationMapBo.chartParam.getWidth(),"+");
				    if(Double.parseDouble(this.bottomNode.getY())<=childY) {
                        isYAdd=false;
                    }
				    BigDecimal bestRight=new BigDecimal("0");
					bestRight.setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP);
				    BigDecimal currentXBig=new BigDecimal(currentX+"").setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
				   				    /*
					田野修改前代码
					ArrayList parentList = this.getParentList(relation_id, this.getA_code());
					int csize = currChildren.size();
					int psize = parentList.size();
				     */
				    BigDecimal aa0=new BigDecimal("0");
				    String currX=this.currentNode.getX();
				    if(psize<csize)//其他节点用移动
				    {
				    	int s=csize-psize;
				    	BigDecimal aa1=RelationMapBo.getValue(RelationMapBo.getValue(RelationMapBo.chartParam.getLr_spacing(),RelationMapBo.chartParam.getWidth(), "+").toString(), "2", "/");
				    	if(isCircle) {
                            aa1=RelationMapBo.getValue(RelationMapBo.getValue(RelationMapBo.chartParam.getLr_spacing(),RelationMapBo.chartParam.getCircle(), "+").toString(), "2", "/");
                        }
				    	aa0=aa1.multiply(new BigDecimal(s));
				    }
					for(int i=0;i<anodeList.size();i++){
						RelationMapNode node = (RelationMapNode)anodeList.get(i);
						String key=node.getId().toUpperCase();
						String relationid=key.split("`")[0];
						if(psize<csize&&("root".equalsIgnoreCase(key)||Integer.parseInt(relationid)>=Integer.parseInt(relation_id))){
							if("root".equalsIgnoreCase(key)||Integer.parseInt(relationid)>Integer.parseInt(relation_id))
							{
								node.setX(RelationMapBo.getValue(node.getX(), childWidth/2+"","+").toString());
							}else{
						    	node.setX(RelationMapBo.getValue(node.getX(), aa0.toString(),"+").toString());
							}
				    	}
						if(isYAdd)
						{
							double newY=RelationMapBo.getValue(node.getY(),increamHeight+"","+").doubleValue();
							node.setY(newY+"");
						}
						BigDecimal xx=RelationMapBo.getValue(node.getX(),RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+").toString(),"+");
				        if(isCircle) {
                            xx=RelationMapBo.getValue(node.getX(),RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+").toString(),"+");
                        }
						if(xx.compareTo(bestRight)>=0) {
                            bestRight=xx;
                        }
						nodeList.add(node);
					}
					for(int i=0;i<alineList.size();i++){
						RelationMapLine line = (RelationMapLine)alineList.get(i);
						if(connectorMap.get(line.getTo().toUpperCase())!=null&&childrenMap.get(line.getTo().toUpperCase())!=null) {
                            continue;
                        }
						lineList.add(line);
					}
					
					String color=RelationMapBo.getColorImpl(relation_id);
					BigDecimal pyl = new BigDecimal("0");
					if(psize>=csize){//孩子个数小于父节点个数
						BigDecimal aa1 = RelationMapBo.getValue(currX,RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(),"2","/").toString(), "+");
						if(isCircle) {
                            aa1=new BigDecimal(currX);
                        }
						
						BigDecimal aa2=new BigDecimal(csize).multiply(new BigDecimal(RelationMapBo.chartParam.getWidth())).add((new BigDecimal(RelationMapBo.chartParam.getLr_spacing()).multiply(new BigDecimal(csize))));
						if(isCircle) {
                            aa2=new BigDecimal(csize).multiply(new BigDecimal(RelationMapBo.chartParam.getCircle())).add((new BigDecimal(RelationMapBo.chartParam.getLr_spacing()).multiply(new BigDecimal(csize))));
                        }
						BigDecimal aa3 = aa2.divide(new BigDecimal("2"));
						pyl=aa1.subtract(aa3);
						
					}else{
						BigDecimal newCurr=RelationMapBo.getValue(currX, aa0.toString(), "+");
						BigDecimal aa1 = RelationMapBo.getValue(newCurr.toString(),RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(),"2","/").toString(), "+");
						if(isCircle) {
                            aa1=newCurr;
                        }
						
						BigDecimal aa2=new BigDecimal(csize).multiply(new BigDecimal(RelationMapBo.chartParam.getWidth())).add((new BigDecimal(RelationMapBo.chartParam.getLr_spacing()).multiply(new BigDecimal(csize))));
						if(isCircle) {
                            aa2=new BigDecimal(csize).multiply(new BigDecimal(RelationMapBo.chartParam.getCircle())).add((new BigDecimal(RelationMapBo.chartParam.getLr_spacing()).multiply(new BigDecimal(csize))));
                        }
						BigDecimal aa3 = aa2.divide(new BigDecimal("2"));
						pyl=aa1.subtract(aa3);
					}
					BigDecimal cons=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_LEFT, "2", "/");
					for(int i=0;i<currChildren.size();i++){
						LazyDynaBean abean = (LazyDynaBean)currChildren.get(i);
						RelationMapNode node=new RelationMapNode();
						String object_id=(String)abean.get("object_id");
						node.setId(relation_id+"`"+((String)abean.get("object_id")).toUpperCase());
						if(nodeIdMap.get(node.getId().toUpperCase())!=null&&childrenMap.get(node.getId().toUpperCase())!=null) {
                            continue;
                        }
						if("true".equalsIgnoreCase(RelationMapBo.chartParam.getShow_pic())){
						    String tempFile=this.createPhotoFile(object_id.substring(0,3)+ "A00", object_id.substring(3), "P");
						    if(tempFile!=null&&!"".equals(tempFile)){
						    	node.setImageNode("1");
						    	node.setImageurl("images/"+tempFile);
						    }
						}
					    node.setName((String)abean.get("a0101"));
					    node.setToolText((String)abean.get("tooltext"));
					    if(currChildren.size()==1){
					    	node.setX(currentNode.getX());
					    }else{
					    	BigDecimal xx=RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal(i+"")).add(pyl);
					        if(isCircle) {
                                xx=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal(i+"")).add(pyl);
                            }
					    	if(xx.compareTo(bestRight)>=0) {
                                bestRight=xx;
                            }
					    	xx=xx.add(cons);
					    	node.setX(xx.toString());
					    }
					    if(isYAdd) {
                            node.setY((childY+increamHeight)+"");
                        } else {
                            node.setY(childY+"");
                        }
					    if(this.a_code.equalsIgnoreCase(object_id)) {
                            node.setColor(ChartParameterCofig.CURROBJECT_COLOR);
                        } else {
                            node.setColor(color);
                        }
					    node.setWidth(RelationMapBo.chartParam.getWidth());
					    node.setHeight(RelationMapBo.chartParam.getHeight());
					    node.setShape(RelationMapBo.chartParam.getShape());
					    node.setRadius(RelationMapBo.chartParam.getRadius());
					    if(!("true").equals((String)abean.get("hideInfoLink")))//田野添加判断是否有权限查看基本信息而是否执行node.setLink();
                        {
                            node.setLink("j-reloadDataJS-"+node.getId()+"^"+node.getX()+"^"+node.getY()+"^"+"child"+"^"+(String)abean.get("hideInfoLink"));
                        }
					    nodeList.add(node);
						RelationMapLine line = new RelationMapLine();
						line.setFrom(relation_id+"`"+((String)abean.get("mainbody_id")).toUpperCase());
						line.setTo(relation_id+"`"+((String)abean.get("object_id")).toUpperCase());
						line.setArrowAtEnd("0");
						line.setArrowAtStart("1");
						line.setColor(color);
						lineList.add(line);
					}
					if(!isYAdd) {
                        this.trueHeight=new BigDecimal(curHeight+"").setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
                    }
					this.trueHeight=this.getTruePanelHeight();
					BigDecimal right=bestRight.add(new BigDecimal(RelationMapBo.chartParam.getWidth())).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP);
					if(isCircle) {
                        right=bestRight.add(new BigDecimal(RelationMapBo.chartParam.getCircle())).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP);
                    }
					if(this.trueWidth<right.doubleValue()) {
                        this.trueWidth=right.doubleValue();
                    }
					this.trueWidth=this.getTruePanelWidth();
		    	}
		    	
		    }else{//收起操作，以页面实际展现的孩子节点来算页面的长宽变化
		    	//visibleChildren lay
		    	if(isHX){
		    		double increamHeight=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(visibleChildren+"")).doubleValue();
		    		if(isCircle) {
                        increamHeight=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(visibleChildren+"")).doubleValue();
                    }
				    BigDecimal childWidth=RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal(lay+""));
				    if(isCircle) {
                        childWidth=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal(lay+""));
                    }
				    double currentX=RelationMapBo.getValue(this.currentNode.getX(),"0","+").doubleValue();//当前的节点的X
				    double currentY=RelationMapBo.getValue(this.currentNode.getY(),"0","+").doubleValue();//当前节点的y
				    BigDecimal currentXBig = new BigDecimal(currentX+"").setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
				    BigDecimal currentYBig = new BigDecimal(currentY+"").setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
				    boolean isYSubtract = true;
				    BigDecimal trueBig= new BigDecimal(this.trueHeight);
				    BigDecimal clientBig = new BigDecimal(this.clientHeight);
				    BigDecimal subtract=trueBig.subtract(clientBig).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
				    if(subtract.compareTo(new BigDecimal("0"))<=0){
				    	isYSubtract=false;
				    	this.trueHeight=this.clientHeight;
				    }else{
				    	isYSubtract=true;
				    	if(subtract.compareTo(new BigDecimal(increamHeight+""))<=0)
				    	{
				    		increamHeight=subtract.doubleValue();
				    	}else{
				    		
				    	}
				    }
				    				    /*
					田野修改前代码
					ArrayList parentList = this.getParentList(relation_id, this.getA_code());
					int csize = currChildren.size();
					int psize = parentList.size();
				     */
				    BigDecimal aa0=new BigDecimal("0");
					if(psize<csize)//其他节点用移动
					{
					    int s=csize-psize;
					    BigDecimal aa1=RelationMapBo.getValue(RelationMapBo.getValue(RelationMapBo.chartParam.getTb_spacing(),RelationMapBo.chartParam.getHeight(), "+").toString(), "2", "/");
					    aa0=aa1.multiply(new BigDecimal(s));
					    increamHeight=increamHeight/2;
					}else{
						increamHeight=0;
					}
				    if(isYSubtract)
				    {
				    	this.trueHeight=RelationMapBo.getValue(this.trueHeight+"",increamHeight+"","-").doubleValue();
						this.trueHeight=this.getTruePanelHeight();//获得真实高度，哪个高用那个
				    }
				   
				    double height=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(visibleChildren+"")).doubleValue();
				    for(int i=0;i<anodeList.size();i++){
						RelationMapNode node = (RelationMapNode)anodeList.get(i);
						String key=node.getId().toUpperCase();
						String relationid=key.split("`")[0];
						if(nodeIdMap.get(node.getId().toUpperCase())!=null&&childrenMap.get(node.getId().toUpperCase())!=null) {
                            continue;
                        }
						BigDecimal old=new BigDecimal(node.getY()).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
                        if(psize>=csize){
							
						}else{
							if("root".equalsIgnoreCase(key)||Integer.parseInt(relationid)>=Integer.parseInt(relation_id)) {
                                if("root".equalsIgnoreCase(key)||Integer.parseInt(relationid)>Integer.parseInt(relation_id)) {
                                    node.setY(RelationMapBo.getValue(node.getY(), increamHeight / 2 + "", "+").toString());
                                } else {
                                    node.setY(RelationMapBo.getValue(node.getY(), aa0.toString(), "+").toString());
                                }
                            }
							if(isYSubtract){
								double newY=RelationMapBo.getValue(node.getY(),increamHeight+"","-").doubleValue();
								node.setY(newY+"");
							}
						}
						nodeList.add(node);
					}
					for(int i=0;i<alineList.size();i++){
						RelationMapLine line = (RelationMapLine)alineList.get(i);
						if(connectorMap.get(line.getTo().toUpperCase())!=null&&childrenMap.get(line.getTo().toUpperCase())!=null) {
                            continue;
                        }
						lineList.add(line);
					}
					BigDecimal twidth=new BigDecimal(this.trueWidth);
					BigDecimal newWidth=twidth.subtract(childWidth).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP);
					trueWidth=newWidth.doubleValue();
					trueWidth=this.getTruePanelWidth();
		    	}else{//纵向
			    	double increamHeight=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(lay+"")).doubleValue();
			    	if(isCircle) {
                        increamHeight=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(lay+"")).doubleValue();
                    }
				    BigDecimal cc=null;
				    BigDecimal bb=null;
				    if(isCircle){
				    	cc=RelationMapBo.getValue(visibleChildren+"", RelationMapBo.chartParam.getCircle(), "*");
				    	bb=RelationMapBo.getValue((visibleChildren-1)+"", RelationMapBo.chartParam.getLr_spacing(), "*");
				    }else{
				    	cc=RelationMapBo.getValue(visibleChildren+"", RelationMapBo.chartParam.getWidth(), "*");
				    	bb=RelationMapBo.getValue((visibleChildren-1)+"", RelationMapBo.chartParam.getLr_spacing(), "*");
				    }
				    double childWidth=cc.add(bb).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).doubleValue();// 展示孩子需要的总宽
				    double currentX=RelationMapBo.getValue(this.currentNode.getX(),"0","+").doubleValue();//当前的节点的X
				    double currentY=RelationMapBo.getValue(this.currentNode.getY(),"0","+").doubleValue();//当前节点的y
				    BigDecimal currentXBig=new BigDecimal(currentX+"").setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
				    BigDecimal currentYBig = new BigDecimal(currentY+"").setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
				    boolean isYSubtract =true;
				    boolean isClient=true;
				    if(this.trueHeight>this.clientHeight) {
                        isClient=false;
                    }
				    BigDecimal bottomY=RelationMapBo.getValue(this.bottomNode.getY(), "0", "+");
				    if(bottomY.compareTo(currentYBig)<=0) {
                        isYSubtract=false;
                    }
				    if(isYSubtract&&!isClient)
				    {
				    	this.trueHeight=RelationMapBo.getValue(this.trueHeight+"",increamHeight+"","-").doubleValue();
						this.trueHeight=this.getTruePanelHeight();//获得真实高度，哪个高用那个
				    }
				    				    /*
					田野修改前代码
					ArrayList parentList = this.getParentList(relation_id, this.getA_code());
					int csize = currChildren.size();
					int psize = parentList.size();
				     */
				    BigDecimal aa0=new BigDecimal("0");
				    if(psize<csize)//其他节点用移动
				    {
				    	int s=csize-psize;
				    	BigDecimal aa1=RelationMapBo.getValue(RelationMapBo.getValue(RelationMapBo.chartParam.getLr_spacing(),RelationMapBo.chartParam.getWidth(), "+").toString(), "2", "/");
				    	aa0=aa1.multiply(new BigDecimal(s));
				    }
				    for(int i=0;i<anodeList.size();i++){
						RelationMapNode node = (RelationMapNode)anodeList.get(i);
						String key=node.getId().toUpperCase();
						String relationid=key.split("`")[0];
						if(nodeIdMap.get(node.getId().toUpperCase())!=null&&childrenMap.get(node.getId().toUpperCase())!=null) {
                            continue;
                        }
						BigDecimal old=new BigDecimal(node.getX()).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
						if(psize>=csize){
							
						}else{
							if("root".equalsIgnoreCase(key)||Integer.parseInt(relationid)>=Integer.parseInt(relation_id))
							{
								if("root".equalsIgnoreCase(key)||Integer.parseInt(relationid)>Integer.parseInt(relation_id)) {
                                    node.setX(RelationMapBo.getValue(node.getX(), childWidth/2+"", "-").toString());
                                } else {
                                    node.setX(RelationMapBo.getValue(node.getX(), aa0.toString(), "-").toString());
                                }
							}
						}
						if(bottomY.compareTo(currentYBig)<=0){
							
						}else{
							double newY=RelationMapBo.getValue(node.getY(),increamHeight+"","-").doubleValue();
							newY = newY<currentY?currentY:newY;
							node.setY(newY+"");
						}
						nodeList.add(node);
					}
					for(int i=0;i<alineList.size();i++){
						RelationMapLine line = (RelationMapLine)alineList.get(i);
						if(connectorMap.get(line.getTo().toUpperCase())!=null&&childrenMap.get(line.getTo().toUpperCase())!=null) {
                            continue;
                        }
						lineList.add(line);
					}
					BigDecimal right=RelationMapBo.getValue(this.rightNode.getX(),RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+").toString(),"+").subtract(new BigDecimal(childWidth+""));
					if(isCircle) {
                        right=RelationMapBo.getValue(this.rightNode.getX(),RelationMapBo.getValue(RelationMapBo.chartParam.getRadius(), RelationMapBo.chartParam.getLr_spacing(), "+").toString(),"+").subtract(new BigDecimal(childWidth+""));
                    }
					if(this.rightNode.getId().equalsIgnoreCase(this.currentNode.getId())){
						if(isCircle) {
                            right=RelationMapBo.getValue(this.rightNode.getX(),RelationMapBo.getValue(RelationMapBo.chartParam.getRadius(), RelationMapBo.chartParam.getLr_spacing(), "+").toString(),"+").subtract(RelationMapBo.getValue(childWidth+"", "2", "/"));
                        } else {
                            right=RelationMapBo.getValue(this.rightNode.getX(),RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+").toString(),"+").subtract(RelationMapBo.getValue(childWidth+"", "2", "/"));
                        }
					   
					}
					BigDecimal twidth=new BigDecimal(this.trueWidth);
					if(psize<csize) {
                        this.trueWidth=twidth.subtract(aa0).doubleValue();
                    }
					trueWidth=this.getTruePanelWidth();
		    	}
		    }
			//System.out.println("trueHeight="+trueHeight+";trueWidth="+trueWidth+";clientHeight="+this.clientHeight+";clientWidth="+this.clientWidth);
		   ArrayList alist = new ArrayList();
		    for(int i=nodeList.size()-1;i>=0;i--)
		    {
		    	alist.add(nodeList.get(i));
		    }
			map.put("node",alist);
			map.put("line",lineList);
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 找当前节点下的所有子节点（孩子，孙子等）
	 * @param map
	 * @param relation_id
	 * @param currentId
	 * @param dao
	 */
	public void getChildrenLink(HashMap map ,String relation_id,String currentId,ContentDAO dao,int layer){
		RowSet rs = null;
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select object_id,mainbody_id,actor_type,b0110,e0122,e01a1,a0101 from t_wf_mainbody where SP_GRADE=9 and relation_id=");
			sql.append(relation_id);
			sql.append(" and UPPER(mainbody_id)='"+currentId+"'");
			rs = dao.search(sql.toString());
			while(rs.next()){
				map.put(relation_id+"`"+rs.getString("object_id").toUpperCase(),layer+"");
				this.getChildrenLink(map, relation_id, rs.getString("object_id"), dao,++layer);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 得到所以可以显示的所有节点，与当前查看人无关的人员，不用显示出来（主要是上级的）
	 * @return
	 */
	public HashMap getAllCanVisible(String object_id,String relation_id){
		HashMap map = new HashMap();
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			map.put(object_id.toUpperCase(), "1");
			this.getAllParent(dao, object_id, relation_id, map);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return map;
	}
/***
 * 得到点击节点的父节点，孩子节点，
 */
	
	
	/**
	 * 得到所有父节点
	 * @param relation_id
	 * @param object_id
	 * @param map
	 * @return
	 */
	public ArrayList getParentList(String relation_id,String object_id){
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try{
			StringBuffer sql = new StringBuffer("");
			ContentDAO dao  = new ContentDAO(this.conn);
			/*if(this.nodeType.equalsIgnoreCase("parent")||this.nodeType.equalsIgnoreCase("self")){*/
				sql.append(" select object_id,mainbody_id,relation_id from t_wf_mainbody ");
				sql.append(" where relation_id="+relation_id);
				sql.append(" and UPPER(object_id)='"+object_id.toUpperCase()+"'");
				rs = dao.search(sql.toString());
				while(rs.next()){
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("mainbody_id",rs.getString("mainbody_id"));
					bean.set("object_id", rs.getString("object_id"));
					list.add(bean);
				}
			/*}else{
				sql.append(" select object_id,mainbody_id relation_id from t_wf_mainbody ");
				sql.append(" where relation_id="+relation_id);
				sql.append(" and UPPER(object_id)='"+object_id.toUpperCase()+"'");
				rs = dao.search(sql.toString());
				HashMap map = new HashMap();
				this.getAllChild(dao, this.getA_code(), relation_id, map);
				while(rs.next()){
					if(map.get(rs.getString("object_id").toUpperCase())==null)
						continue;
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("mainbody_id",rs.getString("mainbody_id"));
					bean.set("object_id", rs.getString("object_id"));
					list.add(bean);
				}
			}*/
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 得到所有孩子节点
	 * @param relation_id
	 * @param mainbody_id
	 * @param map
	 * @return
	 */
	public ArrayList getChildList(String relation_id,String mainbody_id){
		ArrayList list  = new ArrayList();
		RowSet rs = null;
		try{
			StringBuffer sql = new StringBuffer("");
			ContentDAO dao  = new ContentDAO(this.conn);
			if("parent".equalsIgnoreCase(this.nodeType)){
				sql.append(" select object_id,mainbody_id relation_id from t_wf_mainbody ");
				sql.append(" where relation_id="+relation_id);
				sql.append(" and UPPER(mainbody_id)='"+mainbody_id.toUpperCase()+"'");
				rs = dao.search(sql.toString());
				HashMap map =getAllCanVisible(this.getA_code(), relation_id);
				while(rs.next()){
					if(map.get(rs.getString("object_id"))==null) {
                        continue;
                    }
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("mainbody_id",rs.getString("mainbody_id"));
					bean.set("object_id", rs.getString("object_id"));
					list.add(bean);
				}
			}else{
				sql.append(" select object_id,mainbody_id relation_id from t_wf_mainbody ");
				sql.append(" where relation_id="+relation_id);
				sql.append(" and UPPER(mainbody_id)='"+mainbody_id.toUpperCase()+"'");
				rs = dao.search(sql.toString());
				while(rs.next()){
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("mainbody_id",rs.getString("mainbody_id"));
					bean.set("object_id", rs.getString("object_id"));
					list.add(bean);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}
	public ArrayList getCurrentNodeChildren(String currentNodeId,HashMap nodeIdMap){
		ArrayList list  = new ArrayList();
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			String[] temp = currentNodeId.split("`");
			String relation_id=temp[0];
			String mainbody_id=temp[1];
			StringBuffer sql = new StringBuffer();
			HashMap amap = new HashMap();
			if("parent".equalsIgnoreCase(this.nodeType)){
				HashMap map = this.getAllCanVisible(this.a_code, relation_id);
				sql.append("select A.object_id,A.mainbody_id,A.actor_type,A.b0110,A.e0122,A.e01a1,A.a0101 from t_wf_mainbody A ");
				sql.append(" left join t_wf_mainbody B on A.relation_id=B.relation_id and  UPPER(A.object_id)=UPPER(B.mainbody_id)");
				sql.append(" where A.SP_GRADE=9 and A.relation_id=");
				sql.append(relation_id);
				sql.append(" and UPPER(A.mainbody_id)='"+mainbody_id.toUpperCase()+"'");
				sql.append(" order by B.object_id");//此排序是为使有共同孩子节点的人，排在一起，否则，线会乱
				rs = dao.search(sql.toString());
				ArrayList toolTextList = this.getToolTextList(RelationMapBo.chartParam.getDesc_items(),"desc");
				ArrayList hintTextList = this.getToolTextList(RelationMapBo.chartParam.getHint_items(), "hint");
				while(rs.next()){
					String object_id=rs.getString("object_id");
					if(amap.get(object_id.toUpperCase())!=null) {
                        continue;
                    }
					amap.put(object_id.toUpperCase(), "1");
					if(map.get(object_id.toUpperCase())==null) {
                        continue;
                    }
					if(nodeIdMap.get(object_id.toUpperCase())==null) {
                        nodeIdMap.put(relation_id+"`"+object_id.toUpperCase(), "1");
                    }
					LazyDynaBean bean = this.getBaseInfo(object_id, mainbody_id,toolTextList,hintTextList);
					if(bean!=null){
						bean.set("relation_id",relation_id);
						list.add(bean);
					}
				}
			}else{  
				sql.append("select A.object_id,A.mainbody_id,A.actor_type,A.b0110,A.e0122,A.e01a1,A.a0101 from t_wf_mainbody A ");
				sql.append(" left join t_wf_mainbody B on A.relation_id=B.relation_id and UPPER(A.object_id)=UPPER(B.mainbody_id)");
				sql.append(" where A.SP_GRADE=9 and A.relation_id=");
				sql.append(relation_id);
				sql.append(" and UPPER(A.mainbody_id)='"+mainbody_id.toUpperCase()+"'");
				sql.append(" order by B.object_id");//此排序是为使有共同孩子节点的人，排在一起，否则，线会乱
				rs = dao.search(sql.toString());
				ArrayList toolTextList = this.getToolTextList(RelationMapBo.chartParam.getDesc_items(),"desc");
				ArrayList hintTextList = this.getToolTextList(RelationMapBo.chartParam.getHint_items(), "hint");
				while(rs.next()){
					String object_id=rs.getString("object_id");
					if(amap.get(object_id.toUpperCase())!=null) {
                        continue;
                    }
					amap.put(object_id.toUpperCase(), "1");
					if(nodeIdMap.get(object_id.toUpperCase())==null) {
                        nodeIdMap.put(relation_id+"`"+object_id.toUpperCase(), "1");
                    }
					LazyDynaBean bean = this.getBaseInfo(object_id, mainbody_id,toolTextList,hintTextList);
					if(bean!=null){
						bean.set("relation_id",relation_id);
						list.add(bean);
					}
				}
			}
			
			//按照a0000重新排序 guodd
			list = reverseChildList(list,"object_id");
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 按照a0000 对人员排序 guodd
	 * @param list
	 * @return
	 */
	private ArrayList reverseChildList(ArrayList list,String key){
		ArrayList sortList = new ArrayList();
	    
		ArrayList dblist = DataDictionary.getDbpreList();
		String dbStr = DataDictionary.getDbpreString();
		ArrayList empList = new ArrayList();
		HashMap empMap = new HashMap();
		for(int i=0;i<list.size();i++){
			LazyDynaBean b = (LazyDynaBean)list.get(i);
			String empId = b.get(key).toString();
			if(empId.length()>3 && dbStr.toLowerCase().indexOf(empId.substring(0, 3).toLowerCase())!=-1 ){
				empList.add(empId);
				empMap.put(empId.toLowerCase(), b);
				list.remove(b);
				i--;
			}
		}
		
		StringBuffer sql = new StringBuffer();
		for(int i=0;i<dblist.size();i++){
			String dbpre = dblist.get(i).toString();
			sql.setLength(0);
			sql.append("select a0100 from ");
			sql.append(dbpre);
			sql.append("A01 where a0100 in (");
			for(int k=0;k<empList.size();k++){
				String mainId = empList.get(k).toString();
				if(!mainId.substring(0, 3).toLowerCase().equals(dbpre.toLowerCase())) {
                    continue;
                }
				sql.append("'");
				sql.append(mainId.substring(3));
				sql.append("',");
			}
			sql.append(" 'else' ) order by a0000 ");
			
			List rs = ExecuteSQL.executeMyQuery(sql.toString(),this.conn);
			for(int j=0;j<rs.size();j++){
				LazyDynaBean b = (LazyDynaBean)rs.get(j);
				sortList.add(empMap.get(dbpre.toLowerCase()+b.get("a0100")));
			}
		}
		
		sortList.addAll(list);
		
		return sortList;
	}
	
	public int parseXml(String xml,ArrayList anodeList,ArrayList alineList,HashMap nodeIdMap,HashMap connectorMap,HashMap childrenMap,String currentNodeId,int lay,String freshType){
		StringBuffer b_xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		b_xml.append(xml);
		int visibleChildren=0;
		try{
			HashMap layerMap = new HashMap();
			//xus 20/4/23 xml 编码改造
			 Document doc = PubFunc.generateDom(xml);
			Element chart =  doc.getRootElement();
			List rootChildrenList = (List)chart.getChildren();
			List setList=null;
			List connectorList=null;
			for(int i=0;i<rootChildrenList.size();i++){
				Element child=(Element)rootChildrenList.get(i);
				if("dataset".equalsIgnoreCase(child.getName()))
				{
					 setList  = (List)child.getChildren();
					
				}
				else if("connectors".equalsIgnoreCase(child.getName())){
					connectorList = (List)child.getChildren();
				}else{
					continue;
				}
			}
			for(int i=0;i<setList.size();i++){
				Element element = (Element)setList.get(i);
				if(childrenMap.get(element.getAttributeValue("id").toUpperCase())!=null){//如果页面上已经存在当前节点的子节点，说明是收起操作
					this.isKAI=false;
					break;
				}
			}
			for(int i=0;i<setList.size();i++){
				Element element = (Element)setList.get(i);
				RelationMapNode node = new RelationMapNode();
				node.setX(element.getAttributeValue("x"));
				node.setY(element.getAttributeValue("y"));
				node.setAlpha(element.getAttributeValue("alpha"));
				node.setBorderColor(element.getAttributeValue("bordercolor"));
				node.setColor(element.getAttributeValue("color"));
				node.setHeight(element.getAttributeValue("height"));
				node.setId(element.getAttributeValue("id"));
				node.setImageAlign(element.getAttributeValue("imagealign"));
				node.setImageHeight(element.getAttributeValue("imageheight"));
				node.setImageNode(element.getAttributeValue("imagenode"));
				node.setImageurl(element.getAttributeValue("imageurl"));
				node.setImageWidth(element.getAttributeValue("imagewidth"));
				node.setLink(element.getAttributeValue("link"));
				node.setName(element.getAttributeValue("name").replaceAll("<", "&lt;").replaceAll(">","&gt;"));
				node.setToolText(element.getAttributeValue("tooltext").replaceAll("<", "&lt;").replaceAll(">","&gt;"));
				node.setWidth(element.getAttributeValue("width"));
				node.setShape(element.getAttributeValue("shape"));
				node.setRadius(element.getAttributeValue("radius"));
				node.setNumSides(element.getAttributeValue("numsides"));
				nodeIdMap.put(node.getId().toUpperCase(), node.getId().toUpperCase());
				
				if(!this.isKAI&&childrenMap.get(element.getAttributeValue("id").toUpperCase())!=null&& "0".equals(freshType))
				{
					String layer = (String)childrenMap.get(element.getAttributeValue("id").toUpperCase());
					if(layerMap.get(layer)==null){
						layerMap.put(layer, "1");
					}else{
						int yer=Integer.parseInt(((String)layerMap.get(layer)));
						yer++;
						layerMap.put(layer, yer+"");
					}
					continue;
				}
				if(node.getId().equalsIgnoreCase(currentNodeId))
				{
					this.currentNode=node;
				}
				double x=0.0;
				if(Double.parseDouble(node.getX())>x){
					x=Double.parseDouble(node.getX());
					this.rightNode=node;
				}
				if(this.isKAI){
					double y=Double.parseDouble(node.getY());
					if(Double.parseDouble(node.getY())<=y){
						y=Double.parseDouble(node.getY());
						this.bottomNode=node;
					}
				}
				else{//如果是收起操作，找最低节点，不算当前收起的节点
					if(childrenMap.get(element.getAttributeValue("id").toUpperCase())==null){
						double y=Double.parseDouble(node.getY());
						if(Double.parseDouble(node.getY())<=y){
							this.bottomNode=node;
						}
					}
				}
                anodeList.add(node);
			}
			for(int i=0;i<connectorList.size();i++){
				Element element=(Element)connectorList.get(i);
				RelationMapLine line = new RelationMapLine();
				line.setArrowAtEnd(element.getAttributeValue("arrowatend"));
				line.setArrowAtStart(element.getAttributeValue("arrowatstart"));
				line.setColor(element.getAttributeValue("color"));
				line.setFrom(element.getAttributeValue("from"));
				line.setTo(element.getAttributeValue("to"));
				line.setLabel(element.getAttributeValue("label"));
				line.setStrength(element.getAttributeValue("strength"));
				connectorMap.put(element.getAttributeValue("to").toUpperCase(),"1");
				alineList.add(line);
			}
			Set  keyset = layerMap.keySet();
			Iterator it = keyset.iterator();
			while(it.hasNext()){
				String key = (String)it.next();
				int  value =Integer.parseInt((String)layerMap.get(key));
				if(value>visibleChildren) {
                    visibleChildren=value;
                }
			}
			lay = layerMap.size();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return visibleChildren;
	}
	public HashMap parseXml(String xml){
		StringBuffer b_xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		b_xml.append(xml);
		HashMap map = new HashMap();
		ArrayList anodeList = new ArrayList();
		ArrayList alineList = new ArrayList();
		try{
			//xus 20/4/23 xml 编码改造
			 Document doc = PubFunc.generateDom(b_xml.toString());
			Element chart =  doc.getRootElement();
			List rootChildrenList = (List)chart.getChildren();
			List setList=null;
			List connectorList=null;
			for(int i=0;i<rootChildrenList.size();i++){
				Element child=(Element)rootChildrenList.get(i);
				if("dataset".equalsIgnoreCase(child.getName()))
				{
					 setList  = (List)child.getChildren();
				}
				else if("connectors".equalsIgnoreCase(child.getName())){
					connectorList = (List)child.getChildren();
				}else{
					continue;
				}
			}
			
			for(int i=0;i<setList.size();i++){
				Element element = (Element)setList.get(i);
				RelationMapNode node = new RelationMapNode();
				node.setX(element.getAttributeValue("x"));
				node.setY(element.getAttributeValue("y"));
				node.setAlpha(element.getAttributeValue("alpha"));
				node.setBorderColor(element.getAttributeValue("bordercolor"));
				node.setColor(element.getAttributeValue("color"));
				node.setHeight(element.getAttributeValue("height"));
				node.setId(element.getAttributeValue("id"));
				node.setImageAlign(element.getAttributeValue("imagealign"));
				node.setImageHeight(element.getAttributeValue("imageheight"));
				node.setImageNode(element.getAttributeValue("imagenode"));
				node.setImageurl(element.getAttributeValue("imageurl"));
				node.setImageWidth(element.getAttributeValue("imagewidth"));
				node.setLink(element.getAttributeValue("link"));
				node.setName(element.getAttributeValue("name").replaceAll("<", "&lt;").replaceAll(">","&gt;"));
				node.setToolText(element.getAttributeValue("tooltext").replaceAll("<", "&lt;").replaceAll(">","&gt;"));
				node.setWidth(element.getAttributeValue("width"));
				node.setShape(element.getAttributeValue("shape"));
				node.setRadius(element.getAttributeValue("radius"));
				node.setNumSides(element.getAttributeValue("numsides"));
                anodeList.add(0,node);
			}
			for(int i=0;i<connectorList.size();i++){
				Element element=(Element)connectorList.get(i);
				RelationMapLine line = new RelationMapLine();
				line.setArrowAtEnd(element.getAttributeValue("arrowatend"));
				line.setArrowAtStart(element.getAttributeValue("arrowatstart"));
				line.setColor(element.getAttributeValue("color"));
				line.setFrom(element.getAttributeValue("from"));
				line.setTo(element.getAttributeValue("to"));
				line.setLabel(element.getAttributeValue("label"));
				line.setStrength(element.getAttributeValue("strength"));
				alineList.add(line);
			}
			map.put("node",anodeList);
			map.put("line",alineList);
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	private int mlay=0;
	//田野添加判断是当前登陆人，根据是否是登陆人判断是否开始显示查询基本信息连接
	public void doMethod(ContentDAO dao,String object_id,String relation_id,ArrayList list,ArrayList toolTextList,ArrayList hintTextList,HashMap amap,int layer,Map currentRelationMap){
		RowSet rs = null;
		ArrayList result = new ArrayList();
		try{
			//获取主管领导id
			String manager = findManagerPerson(object_id,relation_id);
			
			
			StringBuffer sql = new StringBuffer("");
			sql.append("select A.object_id,A.mainbody_id,A.actor_type,A.b0110,A.e0122,A.e01a1,A.a0101 from t_wf_mainbody A ");
			sql.append(" left join t_wf_mainbody B on A.relation_id=B.relation_id and UPPER(A.mainbody_id)=UPPER(B.object_id)");
			sql.append(" where A.SP_GRADE=9 and A.relation_id="+relation_id+" and UPPER(A.object_id)='"+object_id.toUpperCase()+"' ");
			sql.append(" order by B.mainbody_id");//此排序是为了使有同父几点的人，能排在一起，否则线就乱了
			rs = dao.search(sql.toString());
			layer++;
			while(rs.next()){
				//如果自己(此方法传入的object_id)的主管领导不是 自己上级的上级（只查二级，多级不管），说明自己不能被这条线上的领导审批，跳过自己
				if(currentRelationMap.containsKey("manager") && currentRelationMap.get("manager").toString().length()>0 &&
						currentRelationMap.get("manager").toString().toUpperCase().indexOf((rs.getString("mainbody_id").toUpperCase()))==-1) {
                    continue;
                }
				if(amap.get(rs.getString("mainbody_id").toUpperCase()+rs.getString("object_id").toUpperCase())!=null||amap.get(rs.getString("object_id").toUpperCase()+rs.getString("mainbody_id").toUpperCase())!=null)
				{
					return;
				}
				amap.put(rs.getString("mainbody_id").toUpperCase()+rs.getString("object_id").toUpperCase(), "1");
				amap.put(rs.getString("object_id").toUpperCase()+rs.getString("mainbody_id").toUpperCase(), "1");
				LazyDynaBean bean = this.getBaseInfo(rs.getString("mainbody_id"), rs.getString("mainbody_id"), toolTextList,hintTextList);
				if(bean==null) {
                    return;
                }
				bean.set("object_id",rs.getString("object_id"));
				bean.set("mainbody_id", rs.getString("mainbody_id"));
				bean.set("actor_type",rs.getString("actor_type"));
				bean.set("lay", layer+"");
				if(layer>this.mlay) {
                    this.mlay=layer;
                }
				//添加判断是否存在登录人的主关系审批映射中
				String mainbodyid = rs.getString("mainbody_id").toUpperCase();
				if(currentRelationMap.get("isAdmin")==null&&!currentRelationMap.containsKey(mainbodyid)) {
                    bean.set("hideInfoLink", "true");
                }
				result.add(bean);
				//主管领导传入下一级
				currentRelationMap.put("manager", manager);
				//this.doMethod(dao,rs.getString("mainbody_id"), relation_id, list,toolTextList,hintTextList,amap,layer,currentRelationMap);
			}
			result = reverseChildList(result,"mainbody_id");
			list.addAll(result);
			for(int i=0;i<result.size();i++){
				LazyDynaBean bean = (LazyDynaBean)result.get(i);
				this.doMethod(dao,bean.get("mainbody_id").toString(), relation_id, list,toolTextList,hintTextList,amap,layer,currentRelationMap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 添加查询登录人主汇报关系所有人员Map
	 * @param mainbody_id
	 * @return
	 * @throws GeneralException
	 */
	public void getCurrentRelationMap(String mainbody_id,Map currentRelationMap) throws GeneralException {
		StringBuffer strsql = new StringBuffer();
		ResultSet rset = null;
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
				strsql.append("select tm.object_id object_id from t_wf_relation tr  left join t_wf_mainbody tm on tr.Relation_id=tm.Relation_id where tr.default_line=1 and upper(tm.mainbody_id)='"
						+ mainbody_id.toUpperCase() + "' and tm.sp_grade=9");
			// strsql.append(" order by ");
			ContentDAO dao = new ContentDAO(conn);
			rset=dao.search(strsql.toString());
			while (rset.next()) {
				String object_id = rset.getString("object_id").toUpperCase();
				currentRelationMap.put(object_id,object_id);
				getCurrentRelationMap(object_id,currentRelationMap);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			try {
				if (rset != null) {
					rset.close();
				}
	
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ee) {
				ee.printStackTrace();
			}

		}
	}
	private HashMap laymap = new HashMap();
	private int maxLay=0;//z展现的层数
	private int maxLayCount=0;//节点最多那层的节点数
	private int t_num = 0;
	private HashMap p_cMap = new HashMap();//存放下级和上级
	/**
	 * 分析数据，算出展现所需
	 * @param list
	 */
	public void someWork(ArrayList list){

		HashMap map = new HashMap();
		for(int i=0;i<list.size();i++){
			LazyDynaBean bean = (LazyDynaBean)list.get(i);
			String mainbody_id=(String)bean.get("mainbody_id");
			String object_id=(String)bean.get("object_id");
			/*if(map.get(mainbody_id.toUpperCase())!=null)
				continue;*/
			map.put(mainbody_id.toUpperCase(), bean);
			ArrayList uperList = new ArrayList();
			ArrayList downerList = new ArrayList();
			if(this.p_cMap.get(mainbody_id.toUpperCase()+"_p")!=null){
				uperList=(ArrayList)this.p_cMap.get(mainbody_id.toUpperCase()+"_p");
			}
			if(this.p_cMap.get(mainbody_id.toUpperCase()+"_c")!=null){
				downerList = (ArrayList)this.p_cMap.get(mainbody_id.toUpperCase()+"_c");
			}
			HashMap innerMap = new HashMap();
			for(int j=0;j<list.size();j++){
				LazyDynaBean innerBean = (LazyDynaBean)list.get(j);
				String innerMid=(String)innerBean.get("mainbody_id");
				String innerOid=(String)innerBean.get("object_id");
				if(innerMid.equalsIgnoreCase(mainbody_id)&&innerOid.equalsIgnoreCase(object_id))//自己
				{
					continue;
				}
				/*if(innerMap.get(innerMid.toUpperCase())!=null)
				{
					//continue;
				}*/
				innerMap.put(innerMid.toUpperCase(), "1");
				if(mainbody_id.equalsIgnoreCase(innerOid)){//是父节点
					boolean isadd=true;
					for(int k=0;k<uperList.size();k++){
						LazyDynaBean kbean=(LazyDynaBean)uperList.get(k);
						String k_Mid = (String)kbean.get("mainbody_id");
						if(k_Mid.equalsIgnoreCase(innerMid)){
							isadd=false;
							break;
						}
					}
					if(isadd) {
                        uperList.add(innerBean);
                    }
				}
				if(object_id.equalsIgnoreCase(innerMid)){//是孩子
					boolean isadd=true;
					for(int k=0;k<downerList.size();k++){
						LazyDynaBean kbean=(LazyDynaBean)downerList.get(k);
						String k_Mid = (String)kbean.get("mainbody_id");
						if(k_Mid.equalsIgnoreCase(innerMid)){
							isadd=false;
							break;
						}
					}
					if(isadd) {
                        downerList.add(innerBean);
                    }
				}	
			}
			if(this.p_cMap.get(mainbody_id.toUpperCase()+"_c")==null) {
                this.p_cMap.put(mainbody_id.toUpperCase()+"_c", downerList);
            }
			if(this.p_cMap.get(mainbody_id.toUpperCase()+"_p")==null) {
                this.p_cMap.put(mainbody_id.toUpperCase()+"_p", uperList);
            }
			bean.set("uperlist", uperList);
			bean.set("downerlist",downerList);
		}
		LazyDynaBean selfbean =(LazyDynaBean)list.get(0);
		String selfM=(String)selfbean.get("mainbody_id");
		this.lineMap.put(selfM.toUpperCase(), "1");
		ArrayList selfUp = (ArrayList)this.p_cMap.get(selfM.toUpperCase()+"_p");
		this.dgWidth2(selfUp, "_p");
		map.clear();
		for(int i=0;i<list.size();i++){
			
			LazyDynaBean bean = (LazyDynaBean)list.get(i);
			String mainbody_id =(String)bean.get("mainbody_id");
			if(map.get(mainbody_id.toUpperCase())!=null) {
                continue;
            }
			map.put(mainbody_id.toUpperCase(), "1");
			ArrayList uplist = (ArrayList)this.p_cMap.get(mainbody_id.toUpperCase()+"_p");
			ArrayList downlist = (ArrayList)this.p_cMap.get(mainbody_id.toUpperCase()+"_c");
			int num = 0;
			int unum=0;
			int dnum=0;
			this.t_num=1;
			String _type = "";
			if(uplist!=null&&uplist.size()>0){
				num = uplist.size();
				_type="_p";
				this.dgWidth(uplist, num, _type);
				unum=this.t_num;
			}
			num = 0;
			this.t_num=1;
			if(downlist!=null&&downlist.size()>0){
				num = downlist.size();
				_type="_c";
				this.dgWidth(downlist, num, _type);
				dnum=this.t_num;
			}
			if(unum>=dnum) {
                bean.set("width", unum+"");
            } else {
                bean.set("width", dnum+"");
            }
			String lay=(String)bean.get("lay");
			if(Integer.parseInt(lay)>this.maxLay) {
                this.maxLay=Integer.parseInt(lay);
            }
			if(laymap.get(lay)!=null){
				ArrayList alist=(ArrayList)laymap.get(lay);
				alist.add(bean);
			}else{
				ArrayList alist = new ArrayList();
				alist.add(bean);
				laymap.put(lay, alist);
			}
		}
		Set keySet=this.laymap.keySet();
		Iterator it = keySet.iterator();
		while(it.hasNext()){
			String key=(String)it.next();
			ArrayList layList = (ArrayList)this.laymap.get(key);
			if(layList.size()>this.maxLayCount) {
                this.maxLayCount=layList.size();
            }
		}
		
	}
	public void dgWidth(ArrayList list,int num,String _type){
		if(num>1) {
            this.t_num+=(num-1);
        }
        for(int i=0;i<list.size();i++){
			LazyDynaBean bean = (LazyDynaBean)list.get(i);
			String mainbody_id =(String)bean.get("mainbody_id");
			ArrayList alist = (ArrayList)this.p_cMap.get(mainbody_id.toUpperCase()+_type);
			if(alist!=null&&alist.size()>0){
				this.dgWidth(alist, alist.size(), _type);
			}
			
        }
	}
	private HashMap lineMap = new HashMap();
	private int lineNum=1;
	public void dgWidth2(ArrayList list,String _type){
        for(int i=0;i<list.size();i++){
        	if(i>0) {
                lineNum++;
            }
			LazyDynaBean bean = (LazyDynaBean)list.get(i);
			String mainbody_id =(String)bean.get("mainbody_id");
			if(lineMap.get(mainbody_id.toUpperCase())==null) {
                lineMap.put(mainbody_id.toUpperCase(),lineNum+"" );
            }
			ArrayList alist = (ArrayList)this.p_cMap.get(mainbody_id.toUpperCase()+_type);
			if(alist!=null&&alist.size()>0){
				this.dgWidth2(alist, _type);
			}
			
        }
	}
	public HashMap drawChart(String a_code){

		HashMap map = new HashMap();
		RowSet rs = null;
		try{
			String currentId = this.userView.getDbname()+this.userView.getA0100();
			//获取登录人的主关系
			Map currentRelationMap = new HashMap();
			//登录人放入currentRelationMap
			if(this.userView.isAdmin()) {
                currentRelationMap.put("isAdmin","1");
            } else
			{
				currentRelationMap.put(currentId.toUpperCase(), currentId.toUpperCase());
				getCurrentRelationMap(currentId,currentRelationMap);
			}
			
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList nodeList = new ArrayList();
			ArrayList lineList = new ArrayList();
			if(a_code==null || a_code.trim().length()==0 || "UN".equalsIgnoreCase(a_code)){
				map.put("node",nodeList);
				map.put("line",lineList);
				this.trueHeight=this.getTruePanelHeight();
				this.trueWidth=this.getTruePanelWidth();
				return map;
			}
			ArrayList toolTextList = this.getToolTextList(RelationMapBo.chartParam.getDesc_items(),"desc");
			ArrayList hintTextList = this.getToolTextList(RelationMapBo.chartParam.getHint_items(), "hint");
			ArrayList relationList = this.getRelationList(a_code);
			RelationMapBo.createTempDir(this.userView);
			double panelHeight=0.0;
			double panelWidth=0.0;
			boolean isCircle=false;
			if("circle".equalsIgnoreCase(RelationMapBo.chartParam.getShape())) {
                isCircle=true;
            }
			boolean isHX=false;
			if("2".equals(RelationMapBo.chartParam.getDirection())) {
                isHX=true;
            }
			if(isHX){//横向展示
				//
				int lay=0;
				for(int i=0;i<relationList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)relationList.get(i);
					String relation_id=(String)bean.get("relation_id");
					ArrayList alist = new ArrayList();
					String currCode=(String)bean.get("a_code");
					this.setA_code(currCode);
					LazyDynaBean selfBean = this.getBaseInfo(currCode,"",toolTextList,hintTextList);
					selfBean.set("lay", "1");
					alist.add(selfBean);
					HashMap amap = new HashMap();
					
					this.doMethod(dao, currCode, relation_id, alist,toolTextList,hintTextList,amap,1,currentRelationMap);	
					this.lineMap=new HashMap();
					this.laymap=new HashMap();
					this.maxLay=0;
					this.maxLayCount=0;
					this.lineNum=1;
					this.p_cMap=new HashMap();
					this.someWork(alist);
					bean.set("laymap",this.laymap);
					bean.set("maxlay",this.maxLay+"");
					bean.set("maxlaycount", this.maxLayCount+"");
					bean.set("line", this.lineMap);
					bean.set("p_cmap", this.p_cMap);
					if(this.maxLay>lay) {
                        lay = this.maxLay;
                    }
					BigDecimal aa=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_LEFT,ChartParameterCofig.LEFT_SPACE, "+");
					BigDecimal bb=RelationMapBo.getValue((this.maxLay+2)+"", RelationMapBo.chartParam.getWidth(), "*");
					if(isCircle) {
                        bb=RelationMapBo.getValue((this.maxLay+2)+"", RelationMapBo.chartParam.getCircle(), "*");
                    }
					BigDecimal cc=RelationMapBo.getValue((this.maxLay+3)+"", RelationMapBo.chartParam.getTb_spacing(), "*");
					double sumPanel=aa.add(bb).add(cc).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).doubleValue();
					if(sumPanel>panelWidth) {
                        panelWidth=sumPanel;
                    }
				}
				this.trueWidth=panelWidth;
				this.trueWidth=this.getTruePanelWidth();
				int mod = relationList.size()%2;
				if(mod!=0){
					mod=relationList.size()/2;
				}else{
					mod=(relationList.size()-1)/2+1;
				}
				int maxWidthCount=0;
				for(int i=0;i<relationList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)relationList.get(i);
					HashMap laymap =(HashMap)bean.get("laymap");
					int maxLay = Integer.parseInt(((String)bean.get("maxlay")));
					int maxlayWidth=0;
					for(int j=0;j<maxLay;j++){
						int key = j+1;
						ArrayList layList = (ArrayList)laymap.get(key+"");
						if(layList==null) {
                            continue;
                        }
						int layWidth=0;
						for(int k=0;k<layList.size();k++){
							LazyDynaBean abean=(LazyDynaBean)layList.get(k);
							String width=(String)abean.get("width");
							layWidth+=Integer.parseInt(width);
						}
						if(layWidth>maxlayWidth) {
                            maxlayWidth=layWidth;
                        }
					}
					if(maxlayWidth==0) {
                        maxlayWidth=1;
                    }
					maxWidthCount+=maxlayWidth;
				}
				BigDecimal a = RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(maxWidthCount+""));
				if(isCircle) {
                    a=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(maxWidthCount+""));
                }
				BigDecimal b = RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP, ChartParameterCofig.Y_TOP, "+").add(new BigDecimal(ChartParameterCofig.Y_BOTTOM));
				this.trueHeight=a.add(b).doubleValue();
				this.trueHeight=this.getTruePanelHeight();
				BigDecimal constant =RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP, ChartParameterCofig.Y_TOP, "+");
				//if(isCircle)
					//constant =RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP, ChartParameterCofig.Y_TOP, "+").add(new BigDecimal(RelationMapBo.chartParam.getRadius()));
				BigDecimal MaxBottom=new BigDecimal("0");
				BigDecimal bottom  = new BigDecimal(this.trueHeight).subtract(constant);
				BigDecimal sumBottom = new BigDecimal("0");
				BigDecimal first = new BigDecimal("0");
				BigDecimal last = new BigDecimal("0");
				for(int i=0;i<relationList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)relationList.get(i);
					String relation_id=(String)bean.get("relation_id");
					String color=RelationMapBo.getColorImpl(relation_id);
					String cname=(String)bean.get("cname");
					HashMap laymap =(HashMap)bean.get("laymap");
					HashMap lineMap =(HashMap)bean.get("line");
					HashMap p_cMap = (HashMap)bean.get("p_cmap");
					String rtype = (String)bean.get("rtype");//A下级，b上级，c没关系
					MaxBottom = new BigDecimal("0");
					int maxLay = Integer.parseInt(((String)bean.get("maxlay")));
					int maxWidth=0;
					for(int j=0;j<maxLay;j++){
						int key = j+1;
						ArrayList layList = (ArrayList)laymap.get(key+"");
						if(layList==null) {
                            continue;
                        }
						for(int k=0;k<layList.size();k++){
							LazyDynaBean abean=(LazyDynaBean)layList.get(k);
							String mainbody_id=(String)abean.get("mainbody_id");
							String width=(String)abean.get("width");
							//String a0101=(String)abean.get("a0101");
							String linenum = (String)lineMap.get(mainbody_id.toUpperCase());
							//System.out.println("a0101="+a0101+";width="+width+";line="+linenum);
							ArrayList uperList = (ArrayList)p_cMap.get(mainbody_id.toUpperCase()+"_p");
							ArrayList downerList = (ArrayList)p_cMap.get(mainbody_id.toUpperCase()+"_c");
							RelationMapNode node=new RelationMapNode();
							node.setId(relation_id+"`"+((String)abean.get("mainbody_id")).toUpperCase());
							if("true".equalsIgnoreCase(RelationMapBo.chartParam.getShow_pic())){
						        String tempFile=this.createPhotoFile(mainbody_id.substring(0,3)+ "A00", mainbody_id.substring(3), "P");
						        if(tempFile!=null&&!"".equals(tempFile)){
					    	    	node.setImageNode("1");
					    	    	node.setImageurl("images/"+tempFile);
						        }
							}
						    node.setName((String)abean.get("a0101"));
						    node.setToolText((String)abean.get("tooltext"));
						    node.setColor(color);
						    node.setHeight(RelationMapBo.chartParam.getHeight());
						    node.setWidth(RelationMapBo.chartParam.getWidth());
						    node.setShape(RelationMapBo.chartParam.getShape());
						    node.setRadius(RelationMapBo.chartParam.getRadius());
						    int size =Integer.parseInt(width);
					    	BigDecimal lineWidth=new BigDecimal("0");
					    	int lineNum = Integer.parseInt(linenum);
					    	if(size>maxWidth) {
                                maxWidth=size;
                            }
						    if(isCircle){
						    	if(lineNum>1) {
                                    lineWidth=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), (lineNum-1)+"", "*").add(RelationMapBo.getValue(RelationMapBo.chartParam.getTb_spacing(),(lineNum-1)+"", "*"));
                                } else{
						    		lineWidth=RelationMapBo.getValue(RelationMapBo.chartParam.getTb_spacing(),"1", "*");
						    	}
						    	if(size<=1){
						    		BigDecimal cc = lineWidth.add(constant);
						    		BigDecimal ee=bottom.subtract(cc).subtract(sumBottom);
						    		node.setY(ee.toString());
						    		if(cc.compareTo(MaxBottom)>0){
						    			MaxBottom=cc;
						    		}
						    		
						    	}else{
							    	BigDecimal hh=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), ""+size, "*");
							    	BigDecimal ii=RelationMapBo.getValue(RelationMapBo.chartParam.getTb_spacing(), (size)+"", "*");
							    	BigDecimal ll = hh.add(ii).divide(new BigDecimal("2"));
							    	BigDecimal mm = RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(),"2", "/");
							    	BigDecimal cc = ll.subtract(mm);
							    	BigDecimal ee=RelationMapBo.getValue(cc.toString(), lineWidth.toString(),"+").add(constant);
								    BigDecimal nn = bottom.subtract(ee).subtract(sumBottom);
							    	node.setY(nn.toString());
							    	if(ee.compareTo(MaxBottom)>0){
						    			MaxBottom=ee;
						    		}
						    	}
							    BigDecimal ff=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_LEFT,ChartParameterCofig.LEFT_SPACE,"+");
								BigDecimal gg=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(),"2", "*").add(new BigDecimal(RelationMapBo.chartParam.getLr_spacing()).multiply(new BigDecimal("2")));
								BigDecimal jj=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal((maxLay-key)+""));
								String kk=ff.add(gg).add(jj).toString();
							    node.setX(kk);
						    }else{
						    	if(lineNum>1) {
                                    lineWidth=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), (lineNum-1)+"", "*").add(RelationMapBo.getValue(RelationMapBo.chartParam.getTb_spacing(),(lineNum-1)+"", "*"));
                                } else {
                                    lineWidth=RelationMapBo.getValue(RelationMapBo.chartParam.getTb_spacing(),"1", "*");
                                }
						    	if(size<=1){
						    		BigDecimal cc = lineWidth.add(constant);
						    		BigDecimal ee=bottom.subtract(cc).subtract(sumBottom);
						    		node.setY(ee.toString());
						    		if(cc.compareTo(MaxBottom)>0){
						    			MaxBottom=cc;
						    		}
						    		
						    	}else{
							    	BigDecimal hh=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), ""+size, "*");
							    	BigDecimal ii=RelationMapBo.getValue(RelationMapBo.chartParam.getTb_spacing(), (size)+"", "*");
							    	BigDecimal ll = hh.add(ii).divide(new BigDecimal("2"));
							    	BigDecimal mm = RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2", "/");
							    	BigDecimal cc = ll.subtract(mm);
							    	BigDecimal ee=RelationMapBo.getValue(cc.toString(), lineWidth.toString(),"+").add(constant);
								    BigDecimal nn = bottom.subtract(ee).subtract(sumBottom);
							    	node.setY(nn.toString());
							    	if(ee.compareTo(MaxBottom)>0){
						    			MaxBottom=ee;
						    		}
						    	}
							    BigDecimal ff=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_LEFT,ChartParameterCofig.LEFT_SPACE,"+");
								BigDecimal gg=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2", "*").add(new BigDecimal(RelationMapBo.chartParam.getLr_spacing()).multiply(new BigDecimal("2")));
								BigDecimal jj=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getLr_spacing(), "+").multiply(new BigDecimal((maxLay-key)+""));
								String kk=ff.add(gg).add(jj).toString();
							    node.setX(kk);
						    }
						    if(this.getA_code().equalsIgnoreCase(mainbody_id)){//当前展示审批关系的节点，该节点用不同颜色（固定）
						    	node.setColor(ChartParameterCofig.CURROBJECT_COLOR);
						    	if(!("true").equals((String)abean.get("hideInfoLink")))//田野添加判断是否有权限查看基本信息而是否执行node.setLink();
                                {
                                    node.setLink("j-reloadDataJS-"+node.getId()+"^"+node.getX()+"^"+node.getY()+"^self"+"^"+(String)abean.get("hideInfoLink"));//展示下级节点时用来区分是展示下面的，还是上面的
                                }
						    }else{
						    	if(!("true").equals((String)abean.get("hideInfoLink")))//田野添加判断是否有权限查看基本信息而是否执行node.setLink();
                                {
                                    node.setLink("j-reloadDataJS-"+node.getId()+"^"+node.getX()+"^"+node.getY()+"^parent"+"^"+(String)abean.get("hideInfoLink"));
                                }
						    }
						    if(!"C".equalsIgnoreCase(rtype)) {
                                nodeList.add(node);
                            }
						    if(uperList.size()>0){
						    	for(int l=0;l<uperList.size();l++){
						    		LazyDynaBean lbean = (LazyDynaBean)uperList.get(l);
						    		String m_id=(String)lbean.get("mainbody_id");
						    		RelationMapLine line = new RelationMapLine();
								    line.setTo(relation_id+"`"+m_id.toUpperCase());
								    line.setFrom(relation_id+"`"+mainbody_id.toUpperCase());
								    line.setArrowAtEnd("1");
								    line.setArrowAtStart("0");
								    line.setColor(color);
								    lineList.add(line);
						    	}
						    }else{
						    	RelationMapLine line = new RelationMapLine();
							    line.setTo(relation_id+"`Node"+i);
							    line.setFrom(relation_id+"`"+mainbody_id.toUpperCase());
							    line.setArrowAtEnd("0");
							    line.setArrowAtStart("0");
							    line.setColor(color);
							    lineList.add(line);
						    }
						}	
					}
					maxWidthCount+=maxWidth;
					RelationMapNode onenode=new RelationMapNode();
					if("C".equalsIgnoreCase(rtype)) {
                        onenode.setName(cname+"(无)");
                    } else {
                        onenode.setName(cname);
                    }
					onenode.setId(relation_id+"`Node"+i);
					onenode.setHeight(RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2","/").toString());
					onenode.setWidth(RelationMapBo.chartParam.getWidth());
					onenode.setColor(color);
					onenode.setShape(RelationMapBo.chartParam.getShape());
					onenode.setRadius(RelationMapBo.chartParam.getRadius());
					if(isCircle){
						BigDecimal aa=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+");
						BigDecimal bb=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_LEFT,ChartParameterCofig.LEFT_SPACE, "+");
						String dd=RelationMapBo.getValue(aa.toString(), bb.toString(),"+").toString();
						onenode.setX(dd);
						BigDecimal hh = RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(maxWidth));
						BigDecimal kk = RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), "4", "/");
						BigDecimal jj = hh.divide(new BigDecimal("2")).subtract(kk);
						String ff = bottom.subtract(jj).subtract(sumBottom).toString();
						onenode.setY(ff.toString());
					}else{
						BigDecimal aa=RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+");
						BigDecimal bb=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_LEFT,ChartParameterCofig.LEFT_SPACE, "+");
						String dd=RelationMapBo.getValue(aa.toString(), bb.toString(),"+").toString();
						onenode.setX(dd);
						
						BigDecimal hh = RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal(maxWidth));
						BigDecimal kk = RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), "4", "/");
						BigDecimal jj = hh.divide(new BigDecimal("2")).subtract(kk);
						String ff = bottom.subtract(jj).subtract(sumBottom).toString();
						onenode.setY(ff.toString());
					}
					if(isCircle) {
                        sumBottom =sumBottom.add(MaxBottom).add(new BigDecimal(RelationMapBo.chartParam.getCircle()));
                    } else {
                        sumBottom = sumBottom.add(MaxBottom).add(new BigDecimal(RelationMapBo.chartParam.getHeight()));
                    }
					if(i==0) {
                        first = new BigDecimal(onenode.getY());
                    }
					if(i==relationList.size()-1) {
                        last = new BigDecimal(onenode.getY());
                    }
					nodeList.add(onenode);
					RelationMapLine line = new RelationMapLine();
				    line.setFrom(relation_id+"`Node"+i);
				    line.setTo("ROOT");
				    line.setArrowAtEnd("0");
				    line.setArrowAtStart("0");
				    line.setColor(color);
				    lineList.add(line);
				}
				RelationMapNode root=new RelationMapNode();
				String rootName = this.getRootName(a_code);
				root.setName(rootName);
				root.setId("ROOT");
				root.setWidth(RelationMapBo.chartParam.getWidth());
				root.setShape(RelationMapBo.chartParam.getShape());
				root.setRadius(RelationMapBo.chartParam.getRadius());
				root.setHeight(RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2", "/").toString());
				
				this.setMenuConstantTop(RelationMapBo.getValue(ChartParameterCofig.Y_TOP,ChartParameterCofig.CONSTANT_TOP,"+").toString());
				root.setColor(ChartParameterCofig.TOP_COLOR);
				String xx=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE, ChartParameterCofig.CONSTANT_LEFT, "+").toString();
				root.setX(xx);
				if(relationList.size()==0){
					BigDecimal t = new BigDecimal(this.trueHeight).subtract(RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP, ChartParameterCofig.Y_TOP, "+"));
					String temp = t.toString();
					if(isCircle) {
                        temp=RelationMapBo.getValue(t.toString(), RelationMapBo.chartParam.getRadius(), "-").toString();
                    }
					root.setY(temp);
				} else if(relationList.size()==1){
					root.setY(first.toString());
				}else{
					BigDecimal aa = RelationMapBo.getValue(first.toString(), last.subtract(RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), "2", "/")).toString(), "-").divide(new BigDecimal("2"));
					BigDecimal tt = new BigDecimal(this.trueHeight).subtract(RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP, ChartParameterCofig.Y_TOP, "+"));
					BigDecimal bb = RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), "4", "/");
					BigDecimal dd = new BigDecimal(this.trueHeight).subtract(first);
					BigDecimal cc = tt.subtract(aa).add(bb).subtract(dd);
					root.setY(cc.toString());
				}
				nodeList.add(root);
			}else{//
				int lay=0;
				for(int i=0;i<relationList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)relationList.get(i);
					String relation_id=(String)bean.get("relation_id");
					String rtype = (String)bean.get("rtype");//A下级，b上级，c没关系
					ArrayList alist = new ArrayList();
					String currCode=(String)bean.get("a_code");
					this.setA_code(currCode);
					LazyDynaBean selfBean = this.getBaseInfo(currCode,"",toolTextList,hintTextList);
					selfBean.set("lay", "1");
					alist.add(selfBean);
					HashMap amap = new HashMap();
					this.doMethod(dao, currCode, relation_id, alist,toolTextList,hintTextList,amap,1,currentRelationMap);	
					this.lineMap=new HashMap();
					this.laymap=new HashMap();
					this.maxLay=0;
					this.maxLayCount=0;
					this.lineNum=1;
					this.p_cMap=new HashMap();
					this.someWork(alist);
					bean.set("laymap",this.laymap);
					bean.set("maxlay",this.maxLay+"");
					bean.set("maxlaycount", this.maxLayCount+"");
					bean.set("line", this.lineMap);
					bean.set("p_cmap", this.p_cMap);
					if(this.maxLay>lay) {
                        lay = this.maxLay;
                    }
					BigDecimal aa=RelationMapBo.getValue(ChartParameterCofig.Y_TOP,ChartParameterCofig.Y_BOTTOM, "+");
					BigDecimal bb=RelationMapBo.getValue((this.maxLay+1)+"", RelationMapBo.chartParam.getHeight(), "*");
					if(isCircle) {
                        bb=RelationMapBo.getValue((this.maxLay+2)+"", RelationMapBo.chartParam.getCircle(), "*");
                    }
					BigDecimal cc=RelationMapBo.getValue((this.maxLay+3)+"", RelationMapBo.chartParam.getTb_spacing(), "*");
					double sumHeight=aa.add(bb).add(cc).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).doubleValue();
					if(sumHeight>panelHeight) {
                        panelHeight=sumHeight;//算出展示面板的总高，
                    }
				}
				this.trueHeight=panelHeight;
				this.trueHeight=this.getTruePanelHeight();
		    	if(trueHeight>clientHeight){
					scrollHeight=this.trueHeight-this.clientHeight;
				}
				int mod = relationList.size()%2;
				if(mod!=0){
					mod=relationList.size()/2;
				}else{
					mod=(relationList.size()-1)/2+1;
				}
				String rootX="0";
				int maxWidthCount=0;
				BigDecimal MaxLeft = RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE, ChartParameterCofig.CONSTANT_LEFT, "+");
				BigDecimal first = new BigDecimal("0");
				BigDecimal last = new BigDecimal("0");
				for(int i=0;i<relationList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)relationList.get(i);
					String relation_id=(String)bean.get("relation_id");
					String color=RelationMapBo.getColorImpl(relation_id);
					String rtype = (String)bean.get("rtype");//A下级，b上级，c没关系
					String cname=(String)bean.get("cname");
					HashMap laymap =(HashMap)bean.get("laymap");
					HashMap lineMap =(HashMap)bean.get("line");
					HashMap p_cMap = (HashMap)bean.get("p_cmap");
					int maxLay = Integer.parseInt(((String)bean.get("maxlay")));
					int maxWidth=0;
					BigDecimal Left  = new BigDecimal("0").add(MaxLeft);
					if(i!=0){
						if(!isCircle) {
                            Left = Left.add(RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+"));
                        } else {
                            Left = Left.add(RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+"));
                        }
					}
					for(int j=0;j<maxLay;j++){
						int key = j+1;
						ArrayList layList = (ArrayList)laymap.get(key+"");
						if(layList==null) {
                            continue;
                        }
						for(int k=0;k<layList.size();k++){
							LazyDynaBean abean=(LazyDynaBean)layList.get(k);

							String mainbody_id=(String)abean.get("mainbody_id");
							String width=(String)abean.get("width");
							if(width==null|| "0".equals(width)) {
                                width="1";
                            }
							String a0101=(String)abean.get("a0101");
							String linenum = (String)lineMap.get(mainbody_id.toUpperCase());
							//System.out.println("a0101="+a0101+";width="+width+";line="+linenum);
							ArrayList uperList = (ArrayList)p_cMap.get(mainbody_id.toUpperCase()+"_p");
							ArrayList downerList = (ArrayList)p_cMap.get(mainbody_id.toUpperCase()+"_c");
							RelationMapNode node=new RelationMapNode();
							node.setId(relation_id+"`"+((String)abean.get("mainbody_id")).toUpperCase());
							if("true".equalsIgnoreCase(RelationMapBo.chartParam.getShow_pic())){
						        String tempFile=this.createPhotoFile(mainbody_id.substring(0,3)+ "A00", mainbody_id.substring(3), "P");
						        if(tempFile!=null&&!"".equals(tempFile)){
					    	    	node.setImageNode("1");
					    	    	node.setImageurl("images/"+tempFile);
						        }
							}
						    node.setName((String)abean.get("a0101"));
						    node.setToolText((String)abean.get("tooltext"));
						    
						    node.setColor(color);
						    node.setHeight(RelationMapBo.chartParam.getHeight());
						    node.setWidth(RelationMapBo.chartParam.getWidth());
						    node.setShape(RelationMapBo.chartParam.getShape());
						    node.setRadius(RelationMapBo.chartParam.getRadius());
						    int size =Integer.parseInt(width);
					    	BigDecimal lineWidth=new BigDecimal("0");
					    	int lineNum = Integer.parseInt(linenum);
					    	if(size>maxWidth) {
                                maxWidth=size;
                            }
						    if(isCircle){
						    	if(lineNum>1) {
                                    lineWidth=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), (lineNum-1)+"", "*").add(RelationMapBo.getValue(RelationMapBo.chartParam.getLr_spacing(),(lineNum-1)+"", "*"));
                                }
						    	if(size<=1){
						    		BigDecimal ee=lineWidth.add(Left);
						    		
						    		node.setX(ee.toString());
						    		if(ee.compareTo(MaxLeft)>0) {
                                        MaxLeft=ee;
                                    }
						    	}else{
							    	BigDecimal hh=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), ""+size, "*");
							    	BigDecimal ii=RelationMapBo.getValue(RelationMapBo.chartParam.getLr_spacing(), (size-1)+"", "*");
							    	
							    	BigDecimal ll = hh.add(ii).divide(new BigDecimal("2"));
							    	BigDecimal mm = RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(),"2", "/");
							    	String ee=lineWidth.toString();
								    BigDecimal nn = ll.add(new BigDecimal(ee)).subtract(mm).add(Left);
							    	node.setX(nn.toString());
							    	if(nn.compareTo(MaxLeft)>0) {
                                        MaxLeft=nn;
                                    }
						    	}
							    BigDecimal ff=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+");
								BigDecimal gg=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(),"2", "*").add(new BigDecimal(RelationMapBo.chartParam.getTb_spacing()).multiply(new BigDecimal("2")));
								BigDecimal jj=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal((maxLay-key)+""));
								String kk=new BigDecimal(this.trueHeight+"").subtract(ff).subtract(gg).subtract(jj).subtract(new BigDecimal(RelationMapBo.chartParam.getRadius())).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
							    node.setY(kk);
						    }else{
						    	
						    	if(lineNum>1) {
                                    lineWidth=RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), (lineNum-1)+"", "*").add(RelationMapBo.getValue(RelationMapBo.chartParam.getLr_spacing(),(lineNum-1)+"", "*"));
                                }
						    	if(size<=1){
						    		BigDecimal ee=lineWidth.add(Left);
						    		node.setX(ee.toString());
						    		if(ee.compareTo(MaxLeft)>0) {
                                        MaxLeft = ee;
                                    }
						    	}else{
							    	BigDecimal hh=RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), ""+size, "*");
							    	BigDecimal ii=RelationMapBo.getValue(RelationMapBo.chartParam.getLr_spacing(), (size-1)+"", "*");
							    	BigDecimal ll = hh.add(ii).divide(new BigDecimal("2"));
							    	BigDecimal mm = RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(),"2", "/");
							    	String ee=lineWidth.toString();
								    BigDecimal nn = ll.add(new BigDecimal(ee)).subtract(mm).add(Left);
							    	node.setX(nn.toString());
							    	if(nn.compareTo(MaxLeft)>0) {
                                        MaxLeft=nn;
                                    }
						    	}
							    BigDecimal ff=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+");
								BigDecimal gg=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"1", "*").add(new BigDecimal(RelationMapBo.chartParam.getTb_spacing()).multiply(new BigDecimal("3")));
								BigDecimal jj=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(), RelationMapBo.chartParam.getTb_spacing(), "+").multiply(new BigDecimal((maxLay-key)+""));
								String kk=new BigDecimal(this.trueHeight+"").subtract(ff).subtract(gg).subtract(jj).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
							    node.setY(kk);
						    }
						    if(this.getA_code().equalsIgnoreCase(mainbody_id)){
						    	node.setColor(ChartParameterCofig.CURROBJECT_COLOR);
						    	if(!("true").equals((String)abean.get("hideInfoLink")))//田野添加判断是否有权限查看基本信息而是否执行node.setLink();
                                {
                                    node.setLink("j-reloadDataJS-"+node.getId()+"^"+node.getX()+"^"+node.getY()+"^self"+"^"+(String)abean.get("hideInfoLink"));
                                }
						    }else{
						    	if(!("true").equals((String)abean.get("hideInfoLink")))//田野添加判断是否有权限查看基本信息而是否执行node.setLink();
                                {
                                    node.setLink("j-reloadDataJS-"+node.getId()+"^"+node.getX()+"^"+node.getY()+"^parent"+"^"+(String)abean.get("hideInfoLink"));
                                }
						    }
						    if(!"C".equalsIgnoreCase(rtype)) {
                                nodeList.add(node);
                            }
						    if(uperList.size()>0){
						    	for(int l=0;l<uperList.size();l++){
						    		LazyDynaBean lbean = (LazyDynaBean)uperList.get(l);
						    		String m_id=(String)lbean.get("mainbody_id");
						    		RelationMapLine line = new RelationMapLine();
								    line.setTo(relation_id+"`"+m_id.toUpperCase());
								    line.setFrom(relation_id+"`"+mainbody_id.toUpperCase());
								    line.setArrowAtEnd("1");
								    line.setArrowAtStart("0");
								    line.setColor(color);
								    lineList.add(line);
						    	}
						    }else{
						    	RelationMapLine line = new RelationMapLine();
							    line.setTo(relation_id+"`Node"+i);
							    line.setFrom(relation_id+"`"+mainbody_id.toUpperCase());
							    line.setArrowAtEnd("0");
							    line.setArrowAtStart("0");
							    line.setColor(color);
							    lineList.add(line);
						    }
						}	
					}
					maxWidthCount+=maxWidth;
					RelationMapNode onenode=new RelationMapNode();
					if("C".equalsIgnoreCase(rtype)) {
                        onenode.setName(cname+"(无)");
                    } else {
                        onenode.setName(cname);
                    }
					onenode.setId(relation_id+"`Node"+i);
					onenode.setHeight(RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2","/").toString());
					onenode.setWidth(RelationMapBo.chartParam.getWidth());
					onenode.setColor(color);
					onenode.setShape(RelationMapBo.chartParam.getShape());
					onenode.setRadius(RelationMapBo.chartParam.getRadius());
					if(isCircle){
						BigDecimal aa=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+").
						              multiply(new BigDecimal(maxWidth+"")).subtract(new BigDecimal(RelationMapBo.chartParam.getLr_spacing()));
						BigDecimal cc = aa.divide(new BigDecimal("2"));
						BigDecimal gg=cc.subtract(RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), "2", "/")).add(Left);
						String dd=gg.setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						onenode.setX(dd);
						if(i==mod){
							rootX=dd;
						}
						BigDecimal ee=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+");
						BigDecimal ii=RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(),"1", "/").add(new BigDecimal(RelationMapBo.chartParam.getTb_spacing()));
						String ff=new BigDecimal(this.trueHeight+"").subtract(ee.add(ii).add(new BigDecimal(RelationMapBo.chartParam.getRadius()))).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						onenode.setY(ff.toString());
					}else{
						BigDecimal aa=RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+").
						              multiply(new BigDecimal(maxWidth+"")).subtract(new BigDecimal(RelationMapBo.chartParam.getLr_spacing()));
						
						BigDecimal cc = aa.divide(new BigDecimal("2"));
						BigDecimal gg=cc.subtract(RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), "2", "/")).add(Left);
						String dd=gg.setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						onenode.setX(dd);
						if(i==mod){
							rootX=dd;
						}
						BigDecimal ee=RelationMapBo.getValue(ChartParameterCofig.CONSTANT_TOP,ChartParameterCofig.Y_TOP,"+");
						BigDecimal ii=RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2", "/").add(new BigDecimal(RelationMapBo.chartParam.getTb_spacing()));
						String ff=new BigDecimal(this.trueHeight+"").subtract(ee.add(ii)).setScale(ChartParameterCofig.SCALE,BigDecimal.ROUND_HALF_UP).toString();
						onenode.setY(ff.toString());
					}
					if(i==0) {
                        first = new BigDecimal(onenode.getX());
                    }
					if(i==relationList.size()-1) {
                        last = new BigDecimal(onenode.getX());
                    }
					nodeList.add(onenode);
					RelationMapLine line = new RelationMapLine();
				    line.setFrom(relation_id+"`Node"+i);
				    line.setTo("ROOT");
				    line.setArrowAtEnd("0");
				    line.setArrowAtStart("0");
				    line.setColor(color);
				    lineList.add(line);
				}
				RelationMapNode root=new RelationMapNode();
				String rootName = this.getRootName(a_code);
				root.setName(rootName);
				root.setId("ROOT");
				root.setWidth(RelationMapBo.chartParam.getWidth());
				root.setShape(RelationMapBo.chartParam.getShape());
				root.setRadius(RelationMapBo.chartParam.getRadius());
				root.setHeight(RelationMapBo.getValue(RelationMapBo.chartParam.getHeight(),"2", "/").toString());
				String temp=RelationMapBo.getValue(this.trueHeight+"","30", "-").subtract(new BigDecimal(ChartParameterCofig.CONSTANT_TOP)).toString();
				if(isCircle) {
                    temp=RelationMapBo.getValue(temp,RelationMapBo.chartParam.getRadius(),"-").toString();
                }
				root.setY(temp);
				
				this.setMenuConstantTop(RelationMapBo.getValue(ChartParameterCofig.Y_TOP,ChartParameterCofig.CONSTANT_TOP,"+").toString());
				root.setColor(ChartParameterCofig.TOP_COLOR);
				if(relationList.size()==0){
					rootX=RelationMapBo.getValue(ChartParameterCofig.LEFT_SPACE, ChartParameterCofig.CONSTANT_LEFT, "+").toString();
					root.setX(rootX);
				}else{
					BigDecimal aa=last.subtract(first).divide(new BigDecimal("2"));
					BigDecimal bb=first.add(aa);
					root.setX(bb.toString());
				}
				nodeList.add(root);
				if(!isCircle) {
                    MaxLeft = MaxLeft.add(RelationMapBo.getValue(RelationMapBo.chartParam.getWidth(), RelationMapBo.chartParam.getLr_spacing(), "+"));
                } else {
                    MaxLeft = MaxLeft.add(RelationMapBo.getValue(RelationMapBo.chartParam.getCircle(), RelationMapBo.chartParam.getLr_spacing(), "+"));
                }
				
				if(isCircle) {
                    panelWidth =MaxLeft.setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
                } else{
					panelWidth =MaxLeft.setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				this.trueWidth=panelWidth;
				this.trueWidth=this.getTruePanelWidth();
				ArrayList descSortList = new ArrayList();
				for(int i=nodeList.size()-1;i>=0;i--){//如果不倒排一下，拖动节点时会有问题，可能和画法有关，必须从上往下画，但是现在的算法是从下网上画的
					//System.out.println(((RelationMapNode)nodeList.get(i)).toNodeXml());
					descSortList.add((RelationMapNode)nodeList.get(i));
				}
			}
			
			map.put("node",nodeList);
			map.put("line",lineList);
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return map;
	
	}
	public void getAllParent(ContentDAO dao,String object_id,String relation_id,HashMap amap)
	{
		RowSet rs = null;
		try{
			String sql = "select object_id,mainbody_id,actor_type,b0110,e0122,e01a1,a0101 from t_wf_mainbody where SP_GRADE=9" +
					" and relation_id="+relation_id+" and UPPER(object_id)='"+object_id.toUpperCase()+"'";
			rs = dao.search(sql);
			while(rs.next()){
				if(amap.get(rs.getString("mainbody_id").toUpperCase())==null) {
                    amap.put(rs.getString("mainbody_id").toUpperCase(), "1");
                }
				this.getAllParent(dao, rs.getString("mainbody_id"), relation_id, amap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void getAllChild(ContentDAO dao,String mainbody_id,String relation_id,HashMap amap){
		RowSet rs = null;
		try{
			String sql = "select object_id,mainbody_id,actor_type,b0110,e0122,e01a1,a0101 from t_wf_mainbody where SP_GRADE=9" +
					" and relation_id="+relation_id+" and UPPER(mainbody_id)='"+mainbody_id.toUpperCase()+"'";
			rs = dao.search(sql);
			while(rs.next()){
				if(amap.get(rs.getString("object_id").toUpperCase())==null) {
                    amap.put(rs.getString("object_id").toUpperCase(), "1");
                }
				this.getAllChild(dao, rs.getString("object_id"), relation_id, amap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 找直接上级，一级一级找下去，直到最顶
	 * @param dao
	 * @param object_id
	 * @param relation_id
	 * @param list
	 */
	public void doMethod(ContentDAO dao,String object_id,String relation_id,ArrayList list,ArrayList toolTextList,ArrayList hintTextList,HashMap amap){
		RowSet rs = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("select A.object_id,A.mainbody_id,A.actor_type,A.b0110,A.e0122,A.e01a1,A.a0101 from t_wf_mainbody A ");
			sql.append(" left join t_wf_mainbody B on A.relation_id=B.relation_id and UPPER(A.mainbody_id)=UPPER(B.object_id)");
			sql.append(" where A.SP_GRADE=9 and A.relation_id="+relation_id+" and UPPER(A.object_id)='"+object_id.toUpperCase()+"' ");
			sql.append(" order by B.mainbody_id");
			rs = dao.search(sql.toString());
			while(rs.next()){
				if(amap.get(rs.getString("mainbody_id").toUpperCase())!=null)
				{
					return;
				}
				amap.put(rs.getString("mainbody_id").toUpperCase(), "1");
				LazyDynaBean bean = this.getBaseInfo(rs.getString("mainbody_id"), rs.getString("mainbody_id"), toolTextList,hintTextList);
				bean.set("object_id",rs.getString("object_id"));
				bean.set("mainbody_id", rs.getString("mainbody_id"));
				bean.set("actor_type",rs.getString("actor_type"));
				list.add(0,bean);
				this.doMethod(dao,rs.getString("mainbody_id"), relation_id, list,toolTextList,hintTextList,amap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public LazyDynaBean getBaseInfo(String a_code,String mainbody_id,ArrayList toolTextList,ArrayList hintTextList ){
		LazyDynaBean bean = null;
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			boolean isPerson=true;
			if("UN".equalsIgnoreCase(a_code.substring(0, 2))|| "UM".equalsIgnoreCase(a_code.substring(0, 2))|| "@K".equalsIgnoreCase(a_code.substring(0, 2))) {
                isPerson=false;
            }
			if(isPerson){
				boolean isHasE0122=false;
				StringBuffer buf = new StringBuffer();
				StringBuffer from = new StringBuffer(" from "+a_code.substring(0,3)+"A01 ");
				HashMap setmap = new HashMap();
				HashMap itemMap = new HashMap();
				for(int i=0;i<toolTextList.size();i++){
					LazyDynaBean abean =(LazyDynaBean)toolTextList.get(i);
					String itemid=(String)abean.get("itemid");
					String fieldSet=(String)abean.get("fieldset");
					if("e0122".equalsIgnoreCase(itemid)) {
                        isHasE0122=true;
                    }
					if(itemMap.get(itemid.toUpperCase())==null){
						itemMap.put(itemid.toUpperCase(), "1");
						buf.append(a_code.substring(0,3)+fieldSet+"."+itemid+",");
						if(!"A01".equalsIgnoreCase(fieldSet)&&setmap.get(fieldSet.toUpperCase())==null){
							from.append(" left join (select * from "+a_code.substring(0,3)+fieldSet+" A ");
							from.append(" where A.i9999=(select Max(b.i9999) from "+a_code.substring(0,3)+fieldSet+" B ");
							from.append(" where A.a0100=B.a0100)) "+a_code.substring(0,3)+fieldSet+" ");
							from.append(" on "+a_code.substring(0,3)+"A01.a0100="+a_code.substring(0,3)+fieldSet+".a0100 ");
							setmap.put(fieldSet.toUpperCase(),"1");
						}
					}
				}
				for(int i=0;i<hintTextList.size();i++){
					LazyDynaBean abean =(LazyDynaBean)hintTextList.get(i);
					String itemid=(String)abean.get("itemid");
					String fieldSet=(String)abean.get("fieldset");
					if("e0122".equalsIgnoreCase(itemid)) {
                        isHasE0122=true;
                    }
					if(itemMap.get(itemid.toUpperCase())==null){
						itemMap.put(itemid.toUpperCase(), "1");
						buf.append(a_code.substring(0,3)+fieldSet+"."+itemid+",");
						if(!"A01".equalsIgnoreCase(fieldSet)&&setmap.get(fieldSet.toUpperCase())==null){
							from.append(" left join (select * from "+a_code.substring(0,3)+fieldSet+" A ");
							from.append(" where A.i9999=(select Max(b.i9999) from "+a_code.substring(0,3)+fieldSet+" B ");
							from.append(" where A.a0100=B.a0100)) "+a_code.substring(0,3)+fieldSet+" ");
							from.append(" on "+a_code.substring(0,3)+"A01.a0100="+a_code.substring(0,3)+fieldSet+".a0100 ");
							setmap.put(fieldSet.toUpperCase(),"1");
						}
					}
				}
				StringBuffer sql = new StringBuffer("select ");
				sql.append(buf.toString()+a_code.substring(0,3)+"A01.a0100 ");
				if(!isHasE0122){
					sql.append(","+a_code.substring(0, 3)+"A01.e0122 ");
				}
				sql.append(from.toString());
				sql.append(" where "+a_code.substring(0,3)+"A01.a0100='"+a_code.substring(3)+"'");
				rs = dao.search(sql.toString());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				while(rs.next()){
					bean = new LazyDynaBean();
					bean.set("object_id", a_code);
					if(mainbody_id==null|| "".equals(mainbody_id)) {
                        bean.set("mainbody_id",a_code);
                    } else {
                        bean.set("mainbody_id",mainbody_id);
                    }
					StringBuffer toolText = new StringBuffer("");
					for(int i=0;i<toolTextList.size();i++){
						LazyDynaBean abean =(LazyDynaBean)toolTextList.get(i);
						String itemid=(String)abean.get("itemid");
						String codesetid=(String)abean.get("codesetid");
						String itemdesc=(String)abean.get("itemdesc");
						String itemtype=(String)abean.get("itemtype");
						int scale=Integer.parseInt((String)abean.get("scale"));
						String value="";
						if("D".equalsIgnoreCase(itemtype)){
							if(rs.getDate(itemid)!=null) {
                                value=format.format(rs.getDate(itemid));
                            }
						}else if("N".equalsIgnoreCase(itemtype)){
							if(rs.getString(itemid)!=null){
								if(scale==0){//判断是否是否有小数位
									FieldItem fi = DataDictionary.getFieldItem(itemid);
									int decimalwidth = fi.getDecimalwidth();
									if(decimalwidth==0)
									{
										value=rs.getInt(itemid)+"";
									}else
									{
										value=rs.getDouble(itemid)+"";
									}
								}
							}else{
								value=PubFunc.round(rs.getString(itemid), scale);
							}
						}else if("A".equalsIgnoreCase(itemtype)){
							if(rs.getString(itemid)!=null){
								value=rs.getString(itemid);
								if(!"0".equals(codesetid)) {
                                    value=AdminCode.getCodeName(codesetid, value);
                                }
							}
						}else if("M".equalsIgnoreCase(itemtype)){
							value=Sql_switcher.readMemo(rs,itemid);
							
						}else{
							if(rs.getString(itemid)!=null){
								value=rs.getString(itemid);
							}
						}
						if(i!=0) {
                            toolText.append("&lt;BR&gt;");
                        }
						toolText.append(itemdesc+":"+value);
					}
					StringBuffer hintText = new StringBuffer("");
					for(int i=0;i<hintTextList.size();i++){
						LazyDynaBean abean =(LazyDynaBean)hintTextList.get(i);
						String itemid=(String)abean.get("itemid");
						String codesetid=(String)abean.get("codesetid");
						String itemdesc=(String)abean.get("itemdesc");
						String itemtype=(String)abean.get("itemtype");
						int scale=Integer.parseInt((String)abean.get("scale"));
						String value="";
						if("D".equalsIgnoreCase(itemtype)){
							if(rs.getDate(itemid)!=null) {
                                value=format.format(rs.getDate(itemid));
                            }
						}else if("N".equalsIgnoreCase(itemtype)){
							if(rs.getString(itemid)!=null){
								if(scale==0){//判断是否是否有小数位
									FieldItem fi = DataDictionary.getFieldItem(itemid);
									int decimalwidth = fi.getDecimalwidth();
									if(decimalwidth==0)
									{
										value=rs.getInt(itemid)+"";
									}else
									{
										value=rs.getDouble(itemid)+"";
									}
								}
							}else{
								value=PubFunc.round(rs.getString(itemid), scale);
							}
						}else if("A".equalsIgnoreCase(itemtype)){
							if(rs.getString(itemid)!=null){
								value=rs.getString(itemid);
								if(!"0".equals(codesetid)) {
                                    value=AdminCode.getCodeName(codesetid, value);
                                }
							}
						}else if("M".equalsIgnoreCase(itemtype)){
							value=Sql_switcher.readMemo(rs,itemid);
							
						}else{
							if(rs.getString(itemid)!=null){
								value=rs.getString(itemid);
							}
						}
						if(i!=0) {
                            hintText.append("&lt;BR&gt;");
                        }
						hintText.append(itemdesc+":"+value);
					}
					bean.set("tooltext", hintText.toString());
					bean.set("a0101", toolText.toString());
					bean.set("e0122_code","UM"+rs.getString("e0122"));
				}
			}
			else
			{
				bean = new LazyDynaBean();
				bean.set("object_id", a_code);
				if(mainbody_id==null|| "".equals(mainbody_id)) {
                    bean.set("mainbody_id",a_code);
                } else {
                    bean.set("mainbody_id",mainbody_id);
                }
				if("UN".equalsIgnoreCase(a_code.substring(0, 2))){
					
					FieldItem item = DataDictionary.getFieldItem("b0110");
					if(item!=null){
				    	bean.set("tooltext", item.getItemdesc()+":"+AdminCode.getCodeName("UN", a_code.substring(2)));
					    bean.set("a0101", item.getItemdesc()+":"+AdminCode.getCodeName("UN", a_code.substring(2)));
					}else{
						bean.set("tooltext", AdminCode.getCodeName("UN", a_code.substring(2)));
					    bean.set("a0101", AdminCode.getCodeName("UN", a_code.substring(2)));
					}
				}else if("UM".equalsIgnoreCase(a_code.substring(0, 2))){
					
					FieldItem item = DataDictionary.getFieldItem("e0122");
					if(item!=null){
				    	bean.set("tooltext", item.getItemdesc()+":"+AdminCode.getCodeName("UM", a_code.substring(2)));
					    bean.set("a0101", item.getItemdesc()+":"+AdminCode.getCodeName("UM", a_code.substring(2)));
					}else{
						bean.set("tooltext", AdminCode.getCodeName("UM", a_code.substring(2)));
					    bean.set("a0101", AdminCode.getCodeName("UM", a_code.substring(2)));
					}
				}else if("@K".equalsIgnoreCase(a_code.substring(0, 2))){
					
					FieldItem item = DataDictionary.getFieldItem("e01a1");
					if(item!=null){
				    	bean.set("tooltext", item.getItemdesc()+":"+AdminCode.getCodeName("@K", a_code.substring(2)));
					    bean.set("a0101", item.getItemdesc()+":"+AdminCode.getCodeName("@K", a_code.substring(2)));
					}else{
						bean.set("tooltext", AdminCode.getCodeName("@K", a_code.substring(2)));
					    bean.set("a0101", AdminCode.getCodeName("@K", a_code.substring(2)));
					}
				}
				bean.set("e0122_code","");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return bean;
	}
		
		public void saveParameterConfig(ChartParameterCofig cpc){
			RowSet rs = null;
			PreparedStatement pstmt = null;	
			try{
//				<?xml version="1.0" encoding="GB2312"?>
//				<graph_param>
//				   //direction:图形方向  1：纵向 2：横向  shape:图形  1：矩形 2：圆形  
//				   < graph direction=’1|2’  shape=’1|2’  >   
//				   // lr_spacing:左右间距   tb_spacing:上下间距  width:节点宽   height:节点高  radius:半径
//				     border_widht:边宽    
//				   <set  lr_spacing=’xxx’  tb_spacing=’xxx’  width=’xxx’ height=’xxx’  radius=’xxx’  bgcolor='' />
//				<font name=’xxx’  size=’xxx’   />
//				//  desc_items:描述信息指标   hint_items:提示信息指标  show_pic:显示人员照片
//				<personnel  desc_items=’a0103,a0105,a0402’  hint_items=’xxxx,xxxx,xxxx,xxxxx’  show_pic=’true’  > 
//				</ graph_param >
				String xml ="<?xml version=\"1.0\"  encoding=\"GB2312\"?><graph_param></graph_param>";
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search("select str_value from constant where UPPER(constant)='REPORTRELATIONCHART'");
				boolean isHave=false;
				while(rs.next()){
					xml=Sql_switcher.readMemo(rs, "str_value");
					isHave=true;
				}
				//xus 20/4/23 xml 编码改造
				 Document doc = PubFunc.generateDom(xml);
		        ArrayList paramList = new ArrayList();
		        StringBuffer strsql=new StringBuffer();	
				if(isHave){
		    		
			        Element graph_param = doc.getRootElement();
			        Element graph=graph_param.getChild("graph");
			        if(graph==null){
			        	graph= new Element("graph");
			        }else{
			        	graph_param.removeChild("graph");
			        }
			        graph.setAttribute("direction", cpc.getDirection());
					graph.setAttribute("shape", cpc.getShape());
					graph_param.addContent(graph);
					
					Element set=graph_param.getChild("set");
					if(set==null){
						set = new Element("set");
					}else{
						graph_param.removeChild("set");
					}
					set.setAttribute("lr_spacing",cpc.getLr_spacing());
					set.setAttribute("tb_spacing", cpc.getTb_spacing());
					set.setAttribute("width",cpc.getWidth());
					set.setAttribute("height",cpc.getHeight());
					set.setAttribute("radius",cpc.getRadius());
					set.setAttribute("border_width", cpc.getBorder_width());
					set.setAttribute("bgcolor",cpc.getBgColor());
					
					//yfiles 新加参数
					set.setAttribute("transitcolor",cpc.getTransitcolor());
					set.setAttribute("border_color",cpc.getBorder_color());
					set.setAttribute("linecolor",cpc.getLinecolor());
					set.setAttribute("linewidth",cpc.getLinewidth());
					set.setAttribute("isshowshadow",cpc.getIsshowshadow());
					set.setAttribute("theme",cpc.getTheme());
					graph_param.addContent(set);
					
					Element font =graph_param.getChild("font");
					if(font==null){
						font = new Element("font");
					}else{
						graph_param.removeChild("font");
					}
					font.setAttribute("name",cpc.getFontName());
					font.setAttribute("size",cpc.getFontSize());
					//yfiles 新加参数
					font.setAttribute("style",cpc.getFontstyle());
					font.setAttribute("color",cpc.getFontcolor());
					graph_param.addContent(font);
					
					Element personnel = graph_param.getChild("personnel");
					if(personnel==null)
					{
						personnel=new Element("personnel");
					}else{
						graph_param.removeChild("personnel");
					}
					personnel.setAttribute("desc_items",cpc.getDesc_items());
					personnel.setAttribute("hint_items", cpc.getHint_items());
					personnel.setAttribute("show_pic",cpc.getShow_pic());
					graph_param.addContent(personnel);
			        StringBuffer buf = new StringBuffer();
			    	XMLOutputter outputter=new XMLOutputter();
					Format format=Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					buf.append(outputter.outputString(doc));
					xml=buf.toString();
									
					strsql.append("update constant set str_value=? where constant='REPORTRELATIONCHART'");
					paramList.add(buf.toString());							
					/*
					switch(Sql_switcher.searchDbServer())
					{
					  case Constant.MSSQL:
					  {
						  pstmt.setString(1, buf.toString());
						  break;
					  }
					  case Constant.ORACEL:
					  {
						  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
						          getBytes())), buf.toString().length());
						  break;
					  }
					  case Constant.DB2:
					  {
						  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(buf.toString().
						          getBytes())), buf.toString().length());
						  break;
					  }
				   }
				   */
					
				}else{
					Element graph_param = doc.getRootElement();
					Element graph = new Element("graph");
					graph.setAttribute("direction", cpc.getDirection());
					graph.setAttribute("shape", cpc.getShape());
					graph_param.addContent(graph);
					Element set = new Element("set");
					set.setAttribute("lr_spacing",cpc.getLr_spacing());
					set.setAttribute("tb_spacing", cpc.getTb_spacing());
					set.setAttribute("width",cpc.getWidth());
					set.setAttribute("height",cpc.getHeight());
					set.setAttribute("radius",cpc.getRadius());
					set.setAttribute("border_width",cpc.getBorder_width());
					set.setAttribute("bgcolor", cpc.getBgColor());
					//yfiles 新加参数
					set.setAttribute("transitcolor",cpc.getTransitcolor());
					set.setAttribute("border_color",cpc.getBorder_color());
					set.setAttribute("linecolor",cpc.getLinecolor());
					set.setAttribute("linewidth",cpc.getLinewidth());
					set.setAttribute("isshowshadow",cpc.getIsshowshadow());
					set.setAttribute("theme",cpc.getTheme());
					graph_param.addContent(set);
					Element font = new Element("font");
					font.setAttribute("name",cpc.getFontName());
					font.setAttribute("size",cpc.getFontSize());
					//yfiles 新加参数
					font.setAttribute("style",cpc.getFontstyle());
					font.setAttribute("color",cpc.getFontcolor());
					graph_param.addContent(font);
					Element personnel = new Element("personnel");
					personnel.setAttribute("desc_items",cpc.getDesc_items());
					personnel.setAttribute("hint_items", cpc.getHint_items());
					personnel.setAttribute("show_pic",cpc.getShow_pic());
					graph_param.addContent(personnel);
					StringBuffer buf = new StringBuffer();
				    XMLOutputter outputter=new XMLOutputter();
					Format format=Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					buf.append(outputter.outputString(doc));
					xml=buf.toString();
					strsql.append("insert into constant(constant,type,describe,str_value) values(?,?,?,?)");	
					//pstmt.setString(1, "REPORTRELATIONCHART");
					//pstmt.setString(2, "A");
					//pstmt.setString(3,ResourceFactory.getProperty("general.sprelation.relationparam"));
					//pstmt.setString(4,xml);
					paramList.add("REPORTRELATIONCHART");	
					paramList.add("A");	
					paramList.add(ResourceFactory.getProperty("general.sprelation.relationparam"));	
					paramList.add(xml);	
				}
				//pstmt.executeUpdate();
				dao.update(strsql.toString(), paramList);
			    
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
					if(pstmt!=null) {
                        pstmt.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		
		public ChartParameterCofig getChartParameter(){
			RowSet rs = null;
			 ChartParameterCofig cpc =new ChartParameterCofig();
			try{
				String xml = "<?xml version=\"1.0\"  encoding=\"GB2312\"?><graph_param></graph_param>";
				
				//是否是第一次使用yfiles进入
				boolean isfirstyFiles = false;
				
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search("select str_value from constant where UPPER(constant)='REPORTRELATIONCHART'");
				if(rs.next()){
					xml=Sql_switcher.readMemo(rs, "str_value");
				}else {
                    isfirstyFiles = true;
                }
				//xus 20/4/23 xml 编码改造
				 Document doc = PubFunc.generateDom(xml);
//					<?xml version="1.0" encoding="GB2312"?>
//					<graph_param>
//					   //direction:图形方向  1：纵向 2：横向  shape:图形  1：矩形 2：圆形  
//					   < graph direction=’1|2’  shape=’1|2’  >   
//					   // lr_spacing:左右间距   tb_spacing:上下间距  width:节点宽   height:节点高  radius:半径
//					     border_widht:边宽    
//					   <set  lr_spacing=’xxx’  tb_spacing=’xxx’  width=’xxx’ height=’xxx’  radius=’xxx’   />
//					<font name=’xxx’  size=’xxx’   />
//					//  desc_items:描述信息指标   hint_items:提示信息指标  show_pic:显示人员照片
//					<personnel  desc_items=’a0103,a0105,a0402’  hint_items=’xxxx,xxxx,xxxx,xxxxx’  show_pic=’true’  > 
//					</ graph_param >
			    Element root = doc.getRootElement();
			    List childList = (List)root.getChildren();
			    for(int i=0;i<childList.size();i++){
			    	Element element = (Element)childList.get(i);
			    	if("graph".equalsIgnoreCase(element.getName())){
			    		cpc.setDirection(element.getAttributeValue("direction"));
			    		cpc.setShape(element.getAttributeValue("shape"));
			    	}else if("set".equalsIgnoreCase(element.getName())){
			    		cpc.setLr_spacing(element.getAttributeValue("lr_spacing"));
			    		cpc.setTb_spacing(element.getAttributeValue("tb_spacing"));
			    		cpc.setWidth(element.getAttributeValue("width"));
			    		cpc.setHeight(element.getAttributeValue("height"));
			    		cpc.setRadius(element.getAttributeValue("radius"));
			    		cpc.setBorder_width(element.getAttributeValue("border_width"));
			    		cpc.setBgColor(element.getAttributeValue("bgcolor")==null?"":element.getAttributeValue("bgcolor"));
			    		if(element.getAttribute("isshowshadow") == null || element.getAttributeValue("isshowshadow").length()<1) {
                            isfirstyFiles = true;
                        } else{
			    			cpc.setTransitcolor(element.getAttributeValue("transitcolor"));
			    			cpc.setBorder_color(element.getAttributeValue("border_color"));
			    			cpc.setLinecolor(element.getAttributeValue("linecolor"));
			    			cpc.setLinewidth(element.getAttributeValue("linewidth"));
			    			cpc.setIsshowshadow(element.getAttributeValue("isshowshadow"));
			    			cpc.setTheme(element.getAttributeValue("theme"));
			    			
			    		}
			    		
			    	}else if("font".equalsIgnoreCase(element.getName())){
			    		cpc.setFontName(element.getAttributeValue("name"));
			    		cpc.setFontSize(element.getAttributeValue("size"));
			    		if(element.getAttribute("color") == null) {
                            isfirstyFiles = true;
                        } else{
				    		cpc.setFontcolor(element.getAttributeValue("color"));
			    			cpc.setFontstyle(element.getAttributeValue("style"));
			    		}
			    	}else if("personnel".equalsIgnoreCase(element.getName())){
			    		cpc.setDesc_items(element.getAttributeValue("desc_items"));
			    		if(!"".equals(element.getAttributeValue("desc_items"))){
			    			StringBuffer buf = new StringBuffer("");
			    			String[] arr = element.getAttributeValue("desc_items").split(",");
			    			for(int j=0;j<arr.length;j++){
			    				if(arr[j]==null|| "".equals(arr[j])) {
                                    continue;
                                }
			    				FieldItem item = DataDictionary.getFieldItem(arr[j].toLowerCase());
			    				if(item!=null) {
                                    buf.append(","+item.getItemdesc());
                                }
			    			}
			    			if(buf.toString().length()>0){
			    				cpc.setDesc_items_desc(buf.toString().substring(1));
			    			}
			    		}
			    		cpc.setHint_items(element.getAttributeValue("hint_items"));
			    		if(!"".equals(element.getAttributeValue("hint_items"))){
			    			StringBuffer buf = new StringBuffer("");
			    			String[] arr = element.getAttributeValue("hint_items").split(",");
			    			for(int j=0;j<arr.length;j++){
			    				if(arr[j]==null|| "".equals(arr[j])) {
                                    continue;
                                }
			    				FieldItem item = DataDictionary.getFieldItem(arr[j].toLowerCase());
			    				if(item!=null) {
                                    buf.append(","+item.getItemdesc());
                                }
			    			}
			    			if(buf.toString().length()>0){
			    				cpc.setHint_items_desc(buf.toString().substring(1));
			    			}
			    		}
			    		cpc.setShow_pic(element.getAttributeValue("show_pic"));
			    	}
			    }
			    
			    if(isfirstyFiles){
			        cpc.setDirection("1");
			        cpc.setBgColor("#46b5f4");
			        cpc.setTransitcolor("#46b5f4");
			        cpc.setLr_spacing("20");
			        cpc.setTb_spacing("20");
			        cpc.setIsshowshadow("false");
			        cpc.setBorder_width("1");
			        cpc.setBorder_color("#46b5f4");
			        cpc.setLinecolor("#46b5f4");
			        cpc.setLinewidth("1");
			        cpc.setFontName("song");
			        cpc.setFontSize("16");
			        cpc.setFontcolor("#ffffff");
			        cpc.setFontstyle("general");
			        cpc.setTheme("0");
			    }
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return cpc;
		}
		public String getRootName(String a_code){
			RowSet rs = null;
			String name="";
			try{
				if(a_code.length()>0){
					if("UN".equalsIgnoreCase(a_code.substring(0, 2))|| "UM".equalsIgnoreCase(a_code.substring(0, 2))|| "@K".equalsIgnoreCase(a_code.substring(0, 2))){
						name=AdminCode.getCodeName(a_code.substring(0, 2), a_code.substring(2))+ResourceFactory.getProperty("general.sprelation.relation");
					}else{
						RecordVo vo = new RecordVo(a_code.substring(0, 3)+"A01");
						vo.setString("a0100",a_code.substring(3));
						ContentDAO dao = new ContentDAO(this.conn);
						if(dao.isExistRecordVo(vo)){
							vo = dao.findByPrimaryKey(vo);
							name=vo.getString("a0101")+ResourceFactory.getProperty("general.sprelation.relation");
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return name;
		}
		public ArrayList getSelectedList(String items){
			ArrayList list  = new ArrayList();
			try{
				if(items==null|| "".equals(items)){
					return list;
				}
				String[] arr=items.split(",");
				for(int i=0;i<arr.length;i++){
					if(arr[i]==null|| "".equals(arr[i])) {
                        continue;
                    }
					FieldItem item=DataDictionary.getFieldItem(arr[i].toLowerCase());
					if(item!=null){
						list.add(new CommonData(item.getItemid(),item.getItemdesc()));
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return list;
		}
		public ArrayList getFieldSetList(String infokind){
			ArrayList list = null;
			if("1".equals(infokind)){
				list=this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, Constant.USED_FIELD_SET);
			}else if("2".equals(infokind)){
				list=this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET, Constant.USED_FIELD_SET); 
			}else if("3".equals(infokind)){
				list= this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET, Constant.USED_FIELD_SET);
			}
			if(list==null) {
                return new ArrayList();
            }
			ArrayList returnList = new ArrayList();
			for(int i=0;i<list.size();i++){
				returnList.add(new CommonData(((FieldSet)list.get(i)).getFieldsetid(),((FieldSet)list.get(i)).getCustomdesc()));
			}
			return returnList;
		}
		public  ArrayList getItemList(String fieldSetid){
			ArrayList list = this.userView.getPrivFieldList(fieldSetid,  Constant.USED_FIELD_SET);
			ArrayList returnList = new ArrayList();
			if(list==null) {
                return returnList;
            }
			for(int i=0;i<list.size();i++){
				returnList.add(new CommonData(((FieldItem)list.get(i)).getItemid(),((FieldItem)list.get(i)).getItemdesc()));
			}
			return returnList;
		}
		public double getTruePanelHeight(){
			return this.clientHeight>=this.trueHeight?(this.clientHeight):(this.trueHeight);
		}
		public double getTruePanelWidth(){
			return this.clientWidth>=this.trueWidth?(this.clientWidth):(this.trueWidth);
		}
		public double getClientHeight() {
			return clientHeight;
		}
		public void setClientHeight(double clientHeight) {
			this.clientHeight = clientHeight;
		}
		public double getClientWidth() {
			return clientWidth;
		}
		public void setClientWidth(double clientWidth) {
			this.clientWidth = clientWidth;
		}
		public double getTrueWidth() {
			return trueWidth;
		}
		public void setTrueWidth(double trueWidth) {
			this.trueWidth = trueWidth;
		}
		public double getTrueHeight() {
			return trueHeight;
		}
		public void setTrueHeight(double trueHeight) {
			this.trueHeight = trueHeight;
		}
		public static BigDecimal getValue(String a,String b,String opt){
			BigDecimal abig = new BigDecimal(a==null?"0":a);
			BigDecimal bbig = new BigDecimal(b==null?"0":b);
			return "+".equals(opt)?abig.add(bbig):("-".equals(opt)?abig.subtract(bbig):("/".equals(opt)?abig.divide(bbig):("*".equals(opt)?abig.multiply(bbig):new BigDecimal("0")))).setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
		}
		/***
		 * 解决精度问题，在页面上点一下节点时，有时候该节点会往下移动一点，不好判断，差距在1以内的，按一样看待。
		 * @param a
		 * @param b
		 * @return
		 */
		public static int getValue(BigDecimal a,BigDecimal b){
			int ret=0;
			a.setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
			b.setScale(ChartParameterCofig.SCALE, BigDecimal.ROUND_HALF_UP);
			if(a.compareTo(b)==0) {
                ret=0;
            } else{
				BigDecimal sub=a.subtract(b);
				BigDecimal abs=sub.abs();
				BigDecimal one= new BigDecimal("1");
				if(abs.compareTo(one)<=0) {
                    ret=0;
                } else {
                    ret = a.compareTo(b);
                }
			}
			return ret;
		}
		public ArrayList getUpPersonList(String currId){
			ArrayList list = new ArrayList();
			/**
			 * 2013-02-25 田野添加
			 * 定义Map用于存放根据currId查询出审批人的单位、岗位、部门和姓名
			 * 放入关系映射集合list里，取值依据集合中第二个数据
			 */
			Map approverInfoMap = new HashMap();
			RowSet rs = null;
			try{
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			  	String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			  	if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122)) {
                    display_e0122="0";
                }
			  	String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
				seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
				StringBuffer buf = new StringBuffer();
				//田野添加代码处理单位、部门和职位情况
				if(currId.startsWith("UN")||currId.startsWith("UM")||currId.startsWith("@K")){
					list.add(getApproverInfoMap(currId,display_e0122,approverInfoMap));
					return list;
				}
				//修改查询语句
				//buf.append("select e0122,a0101 from "+currId.substring(0, 3)+"A01 ");修改前代码
				buf.append("select e0122,a0101,b0110,e01a1 from "+currId.substring(0, 3)+"A01 ");
				buf.append(" where a0100='");
				buf.append(currId.substring(3));
				buf.append("'");
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search(buf.toString());
				while(rs.next()){
					CommonData cd = new CommonData();
					//根据查询到的编码查询相应信息的具体部门/单位/职位的名称，并放入approverInfoMap
					String departmentCode = rs.getString("e0122");//部门编码
					if(departmentCode!=null) {
                        getApproverInfoMap("UM"+departmentCode,display_e0122,approverInfoMap);
                    }
					String unitCode = rs.getString("b0110");//单位编码
					if(unitCode!=null) {
                        getApproverInfoMap("UN"+unitCode,display_e0122,approverInfoMap);
                    }
					String positionCode = rs.getString("e01a1");//职位编码
					if(positionCode!=null) {
                        getApproverInfoMap("@K"+positionCode,display_e0122,approverInfoMap);
                    }
					String name = rs.getString("a0101");//姓名
					approverInfoMap.put("name", name);
					
					if("0".equals(display_e0122)) {
                        cd.setDataName(AdminCode.getCodeName("UM", rs.getString("e0122"))+"/"+rs.getString("a0101"));
                    } else{
	        			  CodeItem citem=AdminCode.getCode("UM",rs.getString("e0122"),Integer.parseInt(display_e0122));
	        			  if(citem!=null) {
                              cd.setDataName(citem.getCodename()+seprartor+rs.getString("a0101"));
                          } else {
                              cd.setDataName(AdminCode.getCodeName("UM", rs.getString("e0122"))+"/"+rs.getString("a0101"));
                          }
	        		  }
					
					cd.setDataValue(currId);
					list.add(cd);
					list.add(approverInfoMap);//放入关系映射集合中
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null){
						rs.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return list;
		}
		/**
		 * 田野 2013-02-26
		 * 前台页面选择的不是人员时
		 * 获取单位、岗位或部门信息存放在approverInfoMap中
		 * @param currId 前台传过来的UN/UM/@K编码
		 * @param display_e0122 
		 * @return 
		 */
		public Map getApproverInfoMap (String currId,String display_e0122,Map approverInfoMap){
			if(currId.indexOf("UN")==-1||currId.indexOf("UM")==-1||currId.indexOf("@K")==-1){
				String flag = currId.substring(0,2);
				currId = currId.substring(2);
				String name = AdminCode.getCode(flag,currId,Integer.parseInt(display_e0122)).getCodename();//部门/单位/职位名
				if("UN".equals(flag)){
					approverInfoMap.put("unit", (name.lastIndexOf("/")== -1? name: name.substring(name.lastIndexOf("/")+1))+"/"+currId);
				}
				if("UM".equals(flag)){
					//拼接形式如：'部门名/编号' 截取最小单位
					approverInfoMap.put("department", (name.lastIndexOf("/")== -1? name: name.substring(name.lastIndexOf("/")+1))+"/"+currId);
					String unitId = findParentId(currId,"UN");
					String unitName=AdminCode.getCode("UN",unitId,Integer.parseInt(display_e0122)).getCodename();
					approverInfoMap.put("unit", (unitName.lastIndexOf("/")== -1? unitName: unitName.substring(unitName.lastIndexOf("/")+1))+"/"+unitId);
				}
				if("@K".equals(flag)){
					approverInfoMap.put("position", (name.lastIndexOf("/")== -1? name: name.substring(name.lastIndexOf("/")+1))+"/"+currId);
					String departmentId = findParentId(currId,"UM");
					if(StringUtils.isNotEmpty(departmentId)){   //wangcq 2014-12-17
						String depantmentName = AdminCode.getCode("UM",departmentId,Integer.parseInt(display_e0122)).getCodename();
						approverInfoMap.put("department", (depantmentName.lastIndexOf("/")== -1? depantmentName: depantmentName.substring(depantmentName.lastIndexOf("/")+1))+"/"+departmentId);
						String unitId = findParentId(departmentId,"UN");
						String unitName = AdminCode.getCode("UN",unitId,Integer.parseInt(display_e0122)).getCodename();
						approverInfoMap.put("unit", (unitName.lastIndexOf("/")== -1? unitName: unitName.substring(unitName.lastIndexOf("/")+1))+"/"+unitId);
					}else{    //岗位上无部门的，单位还是得取出来
						String unitId = findParentId(currId,"UN");
						String unitName = AdminCode.getCode("UN",unitId,Integer.parseInt(display_e0122)).getCodename();
						approverInfoMap.put("unit", (unitName.lastIndexOf("/")== -1? unitName: unitName.substring(unitName.lastIndexOf("/")+1))+"/"+unitId);
					}
				}
				
			}
			
			return approverInfoMap;
		}
		/**
		 * 田野 2013-03-02
		 * 根据id去查询上级编码
		 * @param id 根据编号查询
		 * @return
		 */
		public String findParentId(String id ,String parentCodesetid){
			String parentId = "";
			String codesetid = "";
			RowSet rs = null;
			try{
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search("select codesetid,parentid from organization where codeitemid='"+id+"'");
				while(rs.next()){
					parentId=rs.getString("parentid");
					if(id.equals(parentId))   //wangcq 2014-12-17 防止找到顶级节点时还未找到相应上级编码造成的死循环
                    {
                        return "";
                    }
					RowSet parentrs = dao.search("select codesetid,parentid from organization where codeitemid='"+parentId+"'");
					while(parentrs.next()){
						codesetid = parentrs.getString("codesetid");
						if(codesetid.equals(parentCodesetid)){
							return parentId;
						}else{
							parentId = findParentId(parentId,parentCodesetid);
						}
					}
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return parentId;
		}
		public ArrayList spRelationList(String currId){
			ArrayList list = new ArrayList();
			RowSet rs = null;
			try{
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search("select Relation_id,cname  from t_wf_relation where validflag=1 and Actor_type=1" +//and relation_id in (select relation_id from t_wf_mainbody where UPPER(mainbody_id)='"+currId.toUpperCase()+"')
						" order by seq");
				while(rs.next()){
					list.add(new CommonData(rs.getString("relation_id"),rs.getString("cname")));
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return list;
		}
		public ArrayList getDownPersonList(String relation_id,String currId){
			ArrayList list = new ArrayList();
			RowSet rs = null;
			try{
				ContentDAO dao = new ContentDAO(this.conn);
				StringBuffer buf = new StringBuffer();
				buf.append("select object_id,b0110,e0122,e01a1,a0101 from t_wf_mainbody ");
				buf.append(" where relation_id="+relation_id);
				buf.append(" and upper(mainbody_id)=");
				buf.append("'");
				buf.append(currId.toUpperCase());
				buf.append("'");
				buf.append(" and sp_grade=9");
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			  	String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			  	String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
				seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
				if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122)) {
                    display_e0122="0";
                }
				rs = dao.search(buf.toString());
				while(rs.next()){
					String object_id=rs.getString("object_id");
					String dataName="";
					if("UN".equalsIgnoreCase(object_id.substring(0, 2))){
						dataName=AdminCode.getCodeName("UN",object_id.substring(2));
					}else if("UM".equalsIgnoreCase(object_id.substring(0, 2))){
						dataName=AdminCode.getCodeName("UM",object_id.substring(2));
					}else if("@K".equalsIgnoreCase(object_id.substring(0, 2))){
						dataName=AdminCode.getCodeName("@K",object_id.substring(2));
					}else{
						RecordVo vo = new RecordVo(object_id.substring(0,3)+"A01");
						vo.setString("a0100",object_id.substring(3));
						if(dao.isExistRecordVo(vo)){
							vo = dao.findByPrimaryKey(vo);
							if("0".equals(display_e0122)) {
                                dataName=AdminCode.getCodeName("UM",vo.getString("e0122"))+"/"+vo.getString("a0101");
                            } else{
			        			  CodeItem citem=AdminCode.getCode("UM",vo.getString("e0122"),Integer.parseInt(display_e0122));
			        			  if(citem!=null) {
                                      dataName=citem.getCodename()+seprartor+vo.getString("a0101");
                                  } else {
                                      dataName=AdminCode.getCodeName("UM",vo.getString("e0122"))+"/"+vo.getString("a0101");
                                  }
			        		  }
							
						}
					}
					list.add(new CommonData(object_id,dataName));
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return list;
		}
		public String getPersonByCond(String b0110,String e0122,String e01a1,String a0101,String selected){
			String str="";
			RowSet rs = null;
			try{
				  RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
		          String nbase_str="";
		          if(login_vo!=null) {
                      nbase_str = login_vo.getString("str_value").toLowerCase();
                  }
		          if(nbase_str==null||nbase_str.trim().length()==0) {
                      return str;
                  }
		          String[] selectedArr=selected.split("/");
		          String[] arr=nbase_str.split(",");
		          StringBuffer value=new StringBuffer();
		          StringBuffer name=new StringBuffer();
		          Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		  		 String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		  		 String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		  		 String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
				 seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
				 if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122)) {
                     display_e0122="0";
                 }
		  		 FieldItem item = DataDictionary.getFieldItem(pinyin_field);
		          ContentDAO dao = new ContentDAO(this.conn);
		          for(int i=0;i<arr.length;i++){
		        	  if(arr[i]==null|| "".equals(arr[i])) {
                          continue;
                      }
		        	  StringBuffer buf = new StringBuffer();
		        	  buf.append("select e0122,a0101,a0100 from ");
		        	  buf.append(arr[i]+"A01 ");
		        	  buf.append(" where 1=1 ");
		        	  if(b0110!=null&&b0110.trim().length()>0) {
                          buf.append(" and (b0110 like '"+b0110+"%'  )");
                      }
		        	  if(e0122!=null&&e0122.trim().length()>0) {
                          buf.append(" and (e0122 like '"+e0122+"%' )");
                      }
		        	  if(e01a1!=null&&e01a1.trim().length()>0) {
                          buf.append(" and (e01a1 like '"+e01a1+"%' )");
                      }
		        	  if(a0101!=null&&a0101.trim().length()>0)
		        	  {
		        		  buf.append(" and (a0101 like '%"+a0101+"%' ");
			        	  if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field))&&item!=null&& "1".equals(item.getUseflag())) {
                              buf.append(" or "+pinyin_field+" like '%"+a0101+"%' ");
                          }
		  	        	  buf.append(")");
		        	  }
		        	  StringBuffer abuf = new StringBuffer();
		        	  for(int j=0;j<selectedArr.length;j++){
		        		  if(selectedArr[j]==null|| "".equals(selectedArr[j])) {
                              continue;
                          }
		        		  if(selectedArr[j].substring(0,3).equalsIgnoreCase(arr[i])) {
                              abuf.append(",'"+selectedArr[j].substring(3)+"'");
                          }
		        	  }
		        	  if(abuf.length()>0){
		        		  buf.append(" and a0100 not in (");
		        		  buf.append(abuf.toString().substring(1)+")");
		        	  }
		        	  String privCode=this.userView.getManagePrivCode();
		        	  String privCodeValue=this.userView.getManagePrivCodeValue();
		        	  if(!this.userView.isSuper_admin()){
		        		  if(privCode==null|| "".equals(privCode)) {
                              buf.append(" and 1=2 ");
                          } else{
		        			  if("UN".equalsIgnoreCase(privCode)){
		        				  buf.append(" and (b0110 like '"+privCodeValue+"%'");
		        				  if("".equals(privCodeValue)) {
                                      buf.append(" or b0110 is null ");
                                  }
		        				  buf.append(")");
		        			  }else if("UM".equalsIgnoreCase(privCode)){
		        				  buf.append(" and (e0122 like '"+privCodeValue+"%'");
		        				  if("".equals(privCodeValue)) {
                                      buf.append(" or e0122 is null ");
                                  }
		        				  buf.append(")");
		        			  }else if("@K".equalsIgnoreCase(privCode)){
		        				  buf.append(" and (e01a1 like '"+privCodeValue+"%'");
		        				  if("".equals(privCodeValue)) {
                                      buf.append(" or e01a1 is null ");
                                  }
		        				  buf.append(")");
		        			  }
		        		  }
		        	  }
		        	  rs = dao.search(buf.toString());
		        	  while(rs.next()){
		        		  value.append(arr[i].toUpperCase()+rs.getString("a0100")+"`");
		        		  if("0".equals(display_e0122)) {
                              name.append(AdminCode.getCodeName("UM",rs.getString("e0122"))+"/"+rs.getString("a0101")+"`");
                          } else{
		        			  CodeItem citem=AdminCode.getCode("UM",rs.getString("e0122"),Integer.parseInt(display_e0122));
		        			  if(citem!=null) {
                                  name.append(citem.getCodename()+seprartor+rs.getString("a0101")+"`");
                              } else {
                                  name.append(AdminCode.getCodeName("UM",rs.getString("e0122"))+"/"+rs.getString("a0101")+"`");
                              }
		        		  }
		        	  }
		          }
		          if(value.length()>0){
		        	  str=value.toString().substring(0, value.length()-1)+"@"+name.toString().substring(0, name.length()-1);
		          }
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return str;
		}
		public String saveRelation(String relation_id,String mainbody_id,String object_ids){
			String str="0";
			try{
				ContentDAO dao = new ContentDAO(this.conn);
				//汇报关系维护排除把自己设置成自己的直接上级
				String pre=mainbody_id.substring(0, 3);
				String id=mainbody_id.substring(3);
				String name="";
				String sql="select a0101 from "+pre+"a01 where a0100='"+id+"'";
				ResultSet rs=null;
				rs=dao.search(sql);
				while(rs.next()){
					name=rs.getString("a0101");
				}
				if(object_ids.indexOf(mainbody_id)!=-1){
					str="不能将"+name+"设为"+name+"的下级,保存失败！";
					return str;
				}
				
		
				StringBuffer buf = new StringBuffer("");
				buf.append(" delete from t_wf_mainbody where ");
				buf.append(" relation_id="+relation_id);
				buf.append(" and UPPER(mainbody_id)='"+mainbody_id.toUpperCase()+"' ");
				buf.append(" and sp_grade=9 ");
				/*buf.append(" and UPPER(object_id) in ('");
				buf.append(object_ids.replaceAll("`","','"));
				buf.append("')");*/
				dao.delete(buf.toString(),new ArrayList());
				RecordVo mainbodyVo = new RecordVo(mainbody_id.subSequence(0, 3)+"A01");
				mainbodyVo.setString("a0100",mainbody_id.substring(3));
				mainbodyVo = dao.findByPrimaryKey(mainbodyVo);
				Calendar sc=Calendar.getInstance();
				sc.set(Calendar.YEAR,9999);
				sc.set(Calendar.MONTH,11);
				sc.set(Calendar.DAY_OF_MONTH,31);
				String[] arr = object_ids.split("`");
				
				HashMap map=new HashMap();
				map.put(mainbody_id, mainbody_id);
				//防止审批关系死循环
				
				for(int i=0;i<arr.length;i++){
					if(arr[i]==null|| "".equals(arr[i])) {
                        continue;
                    }
					map.put(arr[i], arr[i]);
				}
				
				//ResultSet ros=null;
				String mainbodyId=mainbody_id.toUpperCase();
				//递归写法防止审批关系死循环
				str=getMainbodyId( mainbodyId, relation_id, map, name);
				if(!"0".equals(str)){
					return str;
				}
//				for(int i=0;i<999;i++){
//					StringBuffer stringBuffer=new StringBuffer();//一层一层往上找直接上级
//					stringBuffer.append("select mainbody_id  from t_wf_mainbody where relation_id="+relation_id+" and sp_grade=9 and  UPPER(object_id)='"+mainbodyId+"'");
//					ros=dao.search(stringBuffer.toString());
//					if(ros.next()){
//						mainbodyId=ros.getString("mainbody_id");
//						if(map.get(mainbodyId)!=null){			
//						String pre_sp=mainbodyId.substring(0, 3);
//						String id_sp=mainbodyId.substring(3);
//						String name_sp="";
//						String sqll="select a0101 from "+pre_sp+"a01 where a0100='"+id_sp+"'";
//						ResultSet rss=null;
//						rss=dao.search(sqll);
//						while(rss.next()){
//							name_sp=rss.getString("a0101");
//						}
//							str="不能将"+name_sp+"设为"+name+"的下级(否则会造成死循环)，保存失败！";
//							return str;
//						}else{
//							map.put(mainbodyId, mainbodyId);
//						}
//						
//					}else{
//						break;
//					}
//				}
				
				for(int i=0;i<arr.length;i++){
					if(arr[i]==null|| "".equals(arr[i])) {
                        continue;
                    }
					RecordVo vo = new RecordVo("t_wf_mainbody");
					vo.setInt("relation_id",Integer.parseInt(relation_id));
					vo.setString("object_id",arr[i]);
					vo.setString("mainbody_id",mainbody_id);
					vo.setString("actor_type","1");
					vo.setInt("sp_grade",9);
					vo.setInt("groupid", 1);
					vo.setString("create_user", this.userView.getUserName());
					vo.setDate("create_time",new Date());
					vo.setString("mod_user", this.userView.getUserName());
					vo.setDate("mod_time",new Date());
					//vo.setDate("start_date",new Date());
				//	vo.setDate("end_date",sc.getTime());
					vo.setString("b0110",mainbodyVo.getString("b0110"));
					vo.setString("e0122",mainbodyVo.getString("e0122"));
					vo.setString("e01a1",mainbodyVo.getString("e01a1"));
					vo.setString("a0101",mainbodyVo.getString("a0101"));
					dao.addValueObject(vo);
				}

			}catch(Exception e){
				str="1";
				e.printStackTrace();
			}
			return str;
		}
		
		/**
		 * 递归写法防止审批关系死循环
		 * @param mainbodyId
		 * @param relation_id
		 * @param map
		 * @param name
		 * @return
		 */
		public String getMainbodyId(String mainbodyId,String relation_id,HashMap map,String name){
			
			StringBuffer stringBuffer=new StringBuffer();//一层一层往上找直接上级
			stringBuffer.append("select mainbody_id  from t_wf_mainbody where relation_id="+relation_id+" and sp_grade=9 and  UPPER(object_id)='"+mainbodyId+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			ResultSet ros=null;
			String str="0";
			try{
				ros=dao.search(stringBuffer.toString());
				while(ros.next()){
					mainbodyId=ros.getString("mainbody_id");
					if(mainbodyId!=null){
						if(map.get(mainbodyId)!=null){
							String pre_sp=mainbodyId.substring(0, 3);
							String id_sp=mainbodyId.substring(3);
							String name_sp="";
							String sqll="select a0101 from "+pre_sp+"a01 where a0100='"+id_sp+"'";
							ResultSet rss=null;
							rss=dao.search(sqll);
							while(rss.next()){
								name_sp=rss.getString("a0101");
							}
								str="不能将"+name_sp+"设为"+name+"的下级(否则会造成死循环)，保存失败！";
								return str;
							}else{
								map.put(mainbodyId, mainbodyId);
								return getMainbodyId(mainbodyId,relation_id,map,name);
							}
						
					}else{
						return str;
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			return str;

			
		}
		/**
		 * 复制汇报关系（直接上级）
		 * @param from
		 * @param to
		 * @param object_ids
		 * @param relation_id
		 * @param isClear
		 * @return
		 */
		public String copyRelation(String from,String to,String object_ids,String relation_id,String isClear){
			String str="0";
			RowSet rs = null;
			try{
				ContentDAO dao = new ContentDAO(this.conn);
				if("1".equals(isClear))
				{
					//
					StringBuffer del = new StringBuffer();
					del.append("delete from t_wf_mainbody ");
					del.append(" where relation_id="+relation_id);
					del.append(" and UPPER(mainbody_id)='");
					del.append(to.toUpperCase());
					del.append("'");
					del.append(" and sp_grade=9 ");
					/*del.append(" and UPPER(object_id) in (");
					del.append("'"+object_ids.toUpperCase().toString().replaceAll("`", "','")+"'");
					del.append(")");*/
					dao.delete(del.toString(), new ArrayList());
				}
				RecordVo mainbodyVo = new RecordVo(to.subSequence(0, 3)+"A01");
				mainbodyVo.setString("a0100",to.substring(3));
				mainbodyVo = dao.findByPrimaryKey(mainbodyVo);
				Calendar sc=Calendar.getInstance();
				sc.set(Calendar.YEAR,9999);
				sc.set(Calendar.MONTH,11);
				sc.set(Calendar.DAY_OF_MONTH,31);
				String[] arr = object_ids.split("`");
				for(int i=0;i<arr.length;i++){
					if(arr[i]==null|| "".equals(arr[i])) {
                        continue;
                    }
					RecordVo vo = new RecordVo("t_wf_mainbody");
					vo.setInt("relation_id",Integer.parseInt(relation_id));
					vo.setString("object_id",arr[i]);
					vo.setString("mainbody_id",to);
					if(dao.isExistRecordVo(vo)) {
                        dao.deleteValueObject(vo);
                    }
					vo.setString("actor_type","1");
					vo.setInt("sp_grade",9);
					vo.setInt("groupid", 1);
					vo.setString("create_user", this.userView.getUserName());
					vo.setDate("create_time",new Date());
					vo.setString("mod_user", this.userView.getUserName());
					vo.setDate("mod_time",new Date());
					//vo.setDate("start_date",new Date());
					//vo.setDate("end_date",sc.getTime());
					vo.setString("b0110",mainbodyVo.getString("b0110"));
					vo.setString("e0122",mainbodyVo.getString("e0122"));
					vo.setString("e01a1",mainbodyVo.getString("e01a1"));
					vo.setString("a0101",mainbodyVo.getString("a0101"));
					dao.addValueObject(vo);
				}
			}catch(Exception e){
				str="1";
				e.printStackTrace();
			}
			return str;
		}
		/**
		 * 随机生成颜色值，
		 * @return
		 */
		public static String getColor(){
			StringBuffer src= new StringBuffer("96E38D27B1A5F4C0");
			Random random=new Random(System.currentTimeMillis());
			StringBuffer strpwd=new StringBuffer();
			int index=0;
			for(int i=0;i<6;i++)
			{
				index=random.nextInt(16);
				strpwd.append(src.charAt(index));
			}
			return strpwd.toString();
		}
		/**
		 * 不能重色
		 * @param relation_id
		 * @return
		 */
		public static String getColorImpl(String relation_id){
			String color="";
			if(chartParam!=null&&!"".equals(chartParam.getBgColor().trim())) {
                color=chartParam.getBgColor();
            } else{
				if(RelationMapBo.colorMap.get(relation_id)!=null) {
                    color=(String)RelationMapBo.colorMap.get(relation_id);
                } else{
					int i=0;
					while(i==0){
					   color=RelationMapBo.getColor();
					   if(!color.equalsIgnoreCase(ChartParameterCofig.CURROBJECT_COLOR)&&!color.equalsIgnoreCase(ChartParameterCofig.TOP_COLOR)&&!RelationMapBo.colorMap2.containsKey(color.toUpperCase())&&!"000000".equalsIgnoreCase(color)) {
                           i++;
                       }
					}
					RelationMapBo.colorMap.put(relation_id, color);
					RelationMapBo.colorMap2.put(color, color);
				}
			}
			return color;
		}
		public String getFontStyle(){
			StringBuffer style=new StringBuffer();
			style.append("<style name='MyFirstFontStyle' type='font'");
			style.append(" leftMargin='0'");
			style.append(" letterSpacing='2'");
			style.append(" font='"+RelationMapBo.chartParam.getFontName()+"'");
			style.append(" size='"+RelationMapBo.chartParam.getFontSize()+"'");
			style.append(" color='000000'");
			style.append(" bold='0'");
			style.append(" underline='0'");
			style.append(" isHTML='1'");
			style.append(" />");
            return style.toString();
		}
		public boolean isCanAdd(String mainbody,String object_id,String relation_id){
			boolean flag=true;
			RowSet rs = null;
			try{
				StringBuffer sql = new StringBuffer();
				sql.append(" select mainbody_id from t_wf_mainbody ");
				sql.append(" where relation_id="+relation_id);
				sql.append(" and UPPER(mainbody_id)='");
				sql.append(mainbody.toUpperCase());
				sql.append("'");
				sql.append(" and UPPER(object_id)='");
				sql.append(object_id.toUpperCase());
				sql.append("'");
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search(sql.toString());
				while(rs.next()){
					flag=false;
				}
				sql.setLength(0);
				sql.append(" select mainbody_id from t_wf_mainbody ");
				sql.append(" where relation_id="+relation_id);
				sql.append(" and UPPER(mainbody_id)='");
				sql.append(object_id.toUpperCase());
				sql.append("'");
				sql.append(" and UPPER(object_id)='");
				sql.append(mainbody.toUpperCase());
				sql.append("'");
				rs = dao.search(sql.toString());
				while(rs.next()){
					flag=false;
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return flag;
		}
		
		/**
		 * 设置graph默认样式，chartParam必须有值
		 * @param graph
		 * @return
		 */
		public HashMap initStyle(Graph2D graph,ImageIcon icon){
			HashMap paramMap = new HashMap();
		    paramMap.put("ChartParameterCofig", chartParam);
		    
		    
		    setDefaultRealizer(graph,icon);
		    
		    String fontstyle = chartParam.getFontstyle();
	        String fontfamily = chartParam.getFontName();
	        String fontsize = chartParam.getFontSize();
	        String fontcolor = chartParam.getFontcolor();
	      //字体设置
		    int fonts = 0;
			if(  fontstyle.indexOf("italic") >-1 && fontstyle.indexOf("thick") >-1){
				fonts = 1+2;//
			}else if(fontstyle.indexOf("italic") >-1){
				fonts = 2;//
			}else if(fontstyle.indexOf("thick") >-1){
				fonts = 1;
			}else {
                fonts = 0;
            }
			
			String textfamily = "宋体";
			if("kaiti".equals(fontfamily)) {
                textfamily = "楷体_GB2312";
            } else if("xinsong".equals(fontfamily)) {
                textfamily = "宋体";
            } else if("fangsong".equals(fontfamily)) {
                textfamily = "仿宋体";
            } else if("heiti".equals(fontfamily)) {
                textfamily = "黑体";
            } else if("yahei".equals(fontfamily)) {
                textfamily = "微软雅黑";
            } else if("lishu".equals(fontfamily)) {
                textfamily = "隶书";
            } else if("youyuan".equals(fontfamily)) {
                textfamily = "幼圆";
            }
			
			
			Font textfont = LoadFont.getFont(textfamily, fonts, Integer.parseInt(fontsize));
			Color textcolor = new Color(Integer.parseInt(fontcolor.replaceAll("#", ""),16));
			paramMap.put("textfont", textfont);
			paramMap.put("textcolor",textcolor);
			//添加展开方向
			paramMap.put("direction",chartParam.getDirection());
		    
		    
		    
		      //节点排列方式
            //TreeLayouter treelayouter = new TreeLayouter();  
			IncrementalHierarchicLayouter hl= (IncrementalHierarchicLayouter)new IncrementalHierarchicLayouter();
			hl.setAutomaticEdgeGroupingEnabled(true);
            ((SimplexNodePlacer) hl.getNodePlacer()).setBaryCenterModeEnabled(true);
            hl.setNodeToNodeDistance(Integer.parseInt(chartParam.getLr_spacing()));
            hl.setMinimumLayerDistance(Integer.parseInt(chartParam.getTb_spacing()));
		    byte showtype;
			if("1".equals(chartParam.getDirection())) {
                showtype =  LayoutOrientation.TOP_TO_BOTTOM;
            } else {
                showtype =  LayoutOrientation.LEFT_TO_RIGHT;
            }
			hl.setLayoutOrientation(showtype);
			//graph.addDataProvider(IncrementalHierarchicLayouter.SWIMLANE_DESCRIPTOR_DPKEY,);
			paramMap.put("layouter", hl);
			return paramMap;
		}
		
		
		/**
		 * 设置全局样式
		 * @param graph
		 */
		private void setDefaultRealizer(Graph2D graph,ImageIcon icon) {
			//初始化节点样式工厂
			GenericNodeRealizer.Factory factory = GenericNodeRealizer.getFactory();
		    Map configurationMap = factory.createDefaultConfigurationMap();

		    y.view.GenericNodeRealizer.Painter painter = null; 
		    
		    String isshowshadow = chartParam.getIsshowshadow();
		    if("true".equals(isshowshadow)){
		    	painter = new ShadowNodePainter(new MyNodePainter());
		        ((ShadowNodePainter)painter).setShadowDistance(10, 10);
		    }else{
		    	painter = new MyNodePainter();
		    }
		    
		    configurationMap.put(GenericNodeRealizer.Painter.class, painter);
		    
		    factory.addConfiguration("nrpainter", configurationMap);
		    
		    //节点样式对象
		    GenericNodeRealizer nr = new GenericNodeRealizer();
			
		    String bgColor = chartParam.getBgColor();
		    String transitcolor = chartParam.getTransitcolor();
		    
		    nr.setFillColor(new Color(Integer.parseInt(bgColor.replace("#",""),16)));
		    if(transitcolor.length()>0) {
                nr.setFillColor2(new Color(Integer.parseInt(transitcolor.replace("#",""),16)));
            }
		    
		    String bordercolor = chartParam.getBorder_color();
		    if(bordercolor.length()>0) {
                nr.setLineColor(new Color(Integer.parseInt(bordercolor.replace("#",""),16)));
            } else {
                nr.setLineColor(null);
            }
		    
		    String borderwidth = chartParam.getBorder_width();
		    LineType Btype = LineType.LINE_1;
		    if("1".equals(borderwidth)) {
                Btype = LineType.LINE_1;
            } else if("2".equals(borderwidth)) {
                Btype = LineType.LINE_2;
            } else if("3".equals(borderwidth)) {
                Btype = LineType.LINE_3;
            } else if("4".equals(borderwidth)) {
                Btype = LineType.LINE_4;
            } else if("5".equals(borderwidth)) {
                Btype = LineType.LINE_5;
            } else if("6".equals(borderwidth)) {
                Btype = LineType.LINE_6;
            } else if("7".equals(borderwidth)) {
                Btype = LineType.LINE_7;
            }
		    nr.setLineType(Btype);
		    
		    
		    if(icon!=null){
		    	Factory f = NodeLabel.getFactory();
			    Painter labelpainter = new MyLablePainter(chartParam.getFontcolor());
		        Map map = f.createDefaultConfigurationMap();
		        map.put(YLabel.Painter.class, labelpainter);
		        f.addConfiguration("buttonC", map);
			    NodeLabel label = nr.createNodeLabel();
			    label.setTextColor(Color.red);
			    label.setIcon(icon);
			    label.setVisible(false);
			    if("1".equals(chartParam.getDirection())) {
                    label.setPosition(NodeLabel.BOTTOM);
                } else {
                    label.setPosition(NodeLabel.RIGHT);
                }
			    label.setConfiguration("buttonC");
			    nr.addLabel(label);
			    
		    }
		    
	        nr.setConfiguration("nrpainter");
		    graph.setDefaultNodeRealizer(nr);
		    
		    //连接线样式
		    PolyLineEdgeRealizer er = new PolyLineEdgeRealizer();
		    er.setSmoothedBends(false);
		    
		    String linecolor = chartParam.getLinecolor();
		    er.setLineColor(new Color(Integer.parseInt(linecolor.replaceAll("#", ""),16)));
		    
		    String linewidth  = chartParam.getLinewidth();
		    LineType Ltype = LineType.LINE_1;
		    if("1".equals(linewidth)) {
                Ltype = LineType.LINE_1;
            } else if("2".equals(linewidth)) {
                Ltype = LineType.LINE_2;
            } else if("3".equals(linewidth)) {
                Ltype = LineType.LINE_3;
            } else if("4".equals(linewidth)) {
                Ltype = LineType.LINE_4;
            } else if("5".equals(linewidth)) {
                Ltype = LineType.LINE_5;
            } else if("6".equals(linewidth)) {
                Ltype = LineType.LINE_6;
            } else if("7".equals(linewidth)) {
                Ltype = LineType.LINE_7;
            }
		    er.setLineType(Ltype);
		    graph.setDefaultEdgeRealizer(er);
		}

		/**
		 * 展开单条汇报线
		 * @param graph
		 * @param relationNodeid 汇报线的节点 id
		 * @param relationid 汇报线id
		 * @param alist 下级对象集合
		 * @param rootbean 查询对象
		 * @param textfont
		 * @param textcolor
		 */
		public void buildSingleRelation(Graph2D graph,String relationNodeid,String relationid,ArrayList alist,LazyDynaBean rootbean,Font textfont,Color textcolor){
			//清空 连接信息 map
			linkMap.clear();
			HashMap relationMap  = new HashMap();
			relationMap.put("reid_"+relationid,relationNodeid);
			//创建节点
			HashSet hs = createNodeByList(graph,alist,relationid,textfont,textcolor,rootbean);
			//连接 节点
			connectNode2Node(graph, hs, relationMap, rootbean, textfont, textcolor);
			 
		}
		
		/**
		 * 将数据编译成 graph
		 * @param graph
		 * @param reportData 数据对象
		 * @param textfont 
		 * @param textcolor
		 */
		public void data2Graph(Graph2D graph,HashMap reportData,Font textfont,Color textcolor,String direction){
			
			String desc = "汇报关系";
			
			Node root = createNormalNode(graph, desc,textfont,textcolor,"root");
			
			//存放汇报线对象节点的信息 集合
			HashMap relationMap = new HashMap();
			
			//存放参与对象节点的 信息 格式：<mainbody_id_relation_id   ,    ArrayList<node_id_object_id_relation_id>  >  
			//Ps: key:当前对象id_汇报线id  value：ArrayList对象<当前对象节点id_下级对象id_汇报线id>  》》》》》》 value中ArrayList 存的是下级信息的集合。因为一个对象可能有多个下级
			linkMap.clear();
			ArrayList relationList = (ArrayList)reportData.get("relationList");
			
			//当选择的对象没有上级，不知道有没有下级的时候存在此Set中
			HashSet onHead = new HashSet();
			 
			
			if(reportData.containsKey("selectObj")){
				LazyDynaBean selectBean = (LazyDynaBean)reportData.get("selectObj");
			
				//查出数据生成节点
				for(int i=0;i<relationList.size();i++){
					LazyDynaBean relationbean = (LazyDynaBean)relationList.get(i);
					String relationid = relationbean.get("relation_id").toString();
					String nodeInfoId = "relation`"+relationid;
					ArrayList alist = (ArrayList)relationbean.get("alist");
					//如果是横向展开 需要反转一下list显示顺序才正确 guodd 
					if(!"1".equals(direction)) {
                        Collections.reverse(alist);
                    }
					if(alist.size()<1){
						String id = relationid+"`"+selectBean.get("object_id");
						//查询 是否存在下级
						HashMap childrenMap = new HashMap();
						this.getChildrenLink(childrenMap, id.split("`")[0], id.split("`")[1], new ContentDAO(this.conn));
						if(childrenMap.size()<1) {
                            continue;
                        }
					}
					Node relationNode = createNormalNode(graph, relationbean.get("cname").toString(),textfont,textcolor,nodeInfoId);
					relationMap.put("reid_"+relationid, BaseServlet.getId(graph, relationNode));
					
					Edge re = graph.createEdge(root, relationNode);
					EdgeRealizer rer = graph.getRealizer(re);
					//rer.setArrow(Arrow.SHORT);
					
					
					
					HashSet hs = createNodeByList(graph,alist,relationid,textfont,textcolor,selectBean);
					onHead.addAll(hs);
					
				}
				
				connectNode2Node(graph, onHead, relationMap, selectBean, textfont, textcolor);
				
			}else{
					for(int i=0;i<relationList.size();i++){
						LazyDynaBean relationbean = (LazyDynaBean)relationList.get(i);
						String relationid = relationbean.get("relation_id").toString();
						String nodeInfoId = "relation`"+relationid;
						Node relationNode = createNormalNode(graph, relationbean.get("cname").toString(),textfont,textcolor,nodeInfoId);
						relationMap.put("reid_"+relationid, BaseServlet.getId(graph, relationNode));
						
						Edge re = graph.createEdge(root, relationNode);
						EdgeRealizer rer = graph.getRealizer(re);
						//rer.setArrow(Arrow.SHORT);
						
						ArrayList alist = (ArrayList)relationbean.get("alist");
						//如果是横向展开 需要反转一下list显示顺序才正确 guodd 
						if(!"1".equals(direction)) {
                            Collections.reverse(alist);
                        }
						
						for(int b = 0;b<alist.size();b++){
							LazyDynaBean cbean = (LazyDynaBean)alist.get(b);
							//第一级节点，上级为空
							cbean.set("parent_id", "");
							Node cnode=  createRelationNode(graph,relationid,cbean,textfont,textcolor,true);
							graph.createEdge(relationNode, cnode);
						}
						
						EdgeMap egMap = graph.createEdgeMap();  
						String targetEdgeGroupID = i+"egroud";  
						for (EdgeCursor ec = relationNode.outEdges(); ec.ok(); ec.next()) {  
						  egMap.set(ec.edge(), targetEdgeGroupID);  
						}  
						graph.addDataProvider(PortConstraintKeys.TARGET_GROUPID_KEY, egMap);  
					}
			}
		}
		
		/**
		 * 创建节点
		 * @param graph
		 * @param alist 需要生成节点的对象集合
		 * @param relationid 汇报线 id
		 * @param textfont
		 * @param textcolor
		 * @param rootbean 选中对象（比如查张某的汇报关系，rootbean就是张某的信息对象）
		 * @return
		 */
		private HashSet createNodeByList(Graph2D graph,ArrayList alist,String relationid,Font textfont,Color textcolor,LazyDynaBean rootbean){
			
			HashSet onHead = new HashSet();
			
			//循环这条汇报线上的所有参与对象，并创建对象节点，将创建的节点的一些信息保存到linkMap中
			for(int k=0;alist!=null && k<alist.size();k++){
				
				LazyDynaBean nodeBean = (LazyDynaBean)alist.get(k);
				//object_id为当前对象的下级 对象id
				String object_id = nodeBean.get("object_id").toString();
				//当前对象 id
				String mainbody_id = nodeBean.get("mainbody_id").toString();
				
				
				if(!isPersonInPriv(mainbody_id)){
					alist.remove(nodeBean);
					k--;
					continue;
				}
				
				//检查当前对象在linkMap里是否已经存在，已经存在说明此节点已经创建，不用继续创建，只需将下级信息保存 到value中下级集合list中
				if(linkMap.containsKey(mainbody_id+"_"+relationid)){
					ArrayList value = (ArrayList)linkMap.get(mainbody_id+"_"+relationid);
					String nodeid = value.get(0).toString().split("_")[0];
					value.add(nodeid+"_"+object_id+"_"+relationid);
					linkMap.put(mainbody_id+"_"+relationid,value);
					continue;
				}
				Node rNode = createRelationNode(graph,relationid,nodeBean,textfont,textcolor,false);
				String nodeid = BaseServlet.getId(graph, rNode);  
				ArrayList nodelist = new ArrayList();
				nodelist.add(nodeid+"_"+object_id+"_"+relationid);
				linkMap.put(mainbody_id+"_"+relationid,nodelist); 
			}
			
			
			//创建选择对象的节点 比如树上选择的张某，这里先创建一个张某的节点
			Node selectNode = createRelationNode(graph,relationid,rootbean,textfont,textcolor,true);
			GenericNodeRealizer gnr = (GenericNodeRealizer)graph.getRealizer(selectNode); 
			gnr.setLineColor(gnr.getLineColor().darker());
			
			
			//没有上级，但是有下级
			if(alist==null || alist.size()==0) {
                onHead.add(relationid+"`"+rootbean.get("object_id")+"`"+BaseServlet.getId(graph, selectNode));
            } else{
				
				ArrayList nodelist = new ArrayList();
				nodelist.add(BaseServlet.getId(graph, selectNode));
				linkMap.put("selObj"+relationid,nodelist);
			}
			
			return onHead;
		}
		
		/**
		 * 将节点连接起来
		 * @param graph
		 * @param onHead  可能为一个一条汇报关系的顶层的 对象 集合
		 * @param relationMap 汇报线 节点 信息的 集合
		 * @param rootbean 选中对象（比如查张某的汇报关系，rootbean就是张某的信息对象）
		 * @param textfont 
		 * @param textcolor
		 */
		private void connectNode2Node(Graph2D graph,HashSet onHead,HashMap relationMap,LazyDynaBean rootbean,Font textfont,Color textcolor){
			// 将生成的节点关联起来
			//获取 “值” 的遍历对象
			Iterator ite = linkMap.values().iterator();
			// 获取key 的 集合
			Set keyset = new HashSet(linkMap.keySet());
			//关联选中节点的次数记录
			int connectRootNum=0;
			
			while(ite.hasNext()){
				
				
				//值中存的是ArrayList，获取下级List
				ArrayList childList = (ArrayList)ite.next();
				
				if(childList.get(0).toString().split("_").length<3){
					continue;
				}
				
				
				// list存的值：当前对象节点id_下级id_汇报线id，现在是取出   当前对象也就是list对应的key的对象的节点id
				String nodeId = childList.get(0).toString().split("_")[0];
				//根据id查询出节点，作为上级节点
				Node upnode = (Node)BaseServlet.getForId(graph,nodeId);
				NodeLabel mainLabel = graph.getRealizer(upnode).getLabel(2);
				String mainId = mainLabel.getText().split("`")[1];
				
				//遍历子集合
				for(int i=0;i<childList.size();i++){
					
					/*逻辑有些复杂...... 数据结构设计有问题，现在只能这么麻烦了。 数据结构设计方式： 查询出的对象 包含 的是下级 对象的 id 。  关键是树上选择的对象 是要查上级的，而不是下级
					 * 
					 *   例如：
					 *      查询出的对象信息：        
					 *           AXXX（当前选择对象id）
					 *             |
					 *              -----BXXXXX（下级对象id）
					 * 
					 * 我现在要将AXXX和他的上级关联起来，只能根据谁的下级id是AXXX才能得到 AXXX 的上级是谁。
					 * 
					 * 所以全是反正来的。凑合着看吧
					 */
					
					
					/*
					 * 因为 linkMap 的key中 是对象id和relationid，自身的node id存在value里，所以还得获取 子对象id后还得以子对象id为key去 linkMap里查询 子对象 的node id.
					 */
					String[] mainUp  = childList.get(i).toString().split("_");
					//获取子对象id
					String objectid = mainUp[1]+"_"+mainUp[2];
					Edge e = null;
					//如果 linkMap 里存在 子对象 的 node 对象 ，则将两个节点关联起来
					if(linkMap.containsKey(objectid)){
						//通过子对象id 获取 子 对象 的 node id；并通过 node id 获取 Node对象
						String[] mainDown  = ((ArrayList)linkMap.get(objectid)).get(0).toString().split("_"); 
						Node downNode = (Node)BaseServlet.getForId(graph, mainDown[0]);
						//更新一下label信息，加入上级的id
						NodeLabel label = graph.getRealizer(downNode).getLabel(2);
						String[] info = label.getText().split("`");
						label.setText(info[0]+"`"+info[1]+"`"+mainId);
						//将两个节点关联起来
						e = graph.createEdge(upnode,downNode);
						
						
						//说明这个节点已经有上级了，将这个节点从 key 的 集合 中删除。最后剩下的 都是没有上级的，没有上级就代表是顶级，要跟 汇报线节点连接
						keyset.remove(objectid);
					}else{
						//如果没有查到，则表明下级是  选中的查询对象，关联一下
						String selectNodeId =  ((ArrayList)linkMap.get("selObj"+mainUp[2])).get(0).toString();
						Node selectNode = (Node)BaseServlet.getForId(graph, selectNodeId);
						NodeLabel label = graph.getRealizer(selectNode).getLabel(2);
						String[] info = label.getText().split("`");
						//如果不是第一次关联 选中节点，重置 选中节点的 信息，去掉上级节点id
						if(connectRootNum>0) {
                            label.setText(info[0]+"`"+info[1]);
                        } else	//如果是第一次关联 选中节点，信息中加入上级的id
                        {
                            label.setText(info[0]+"`"+info[1]+"`"+mainId);
                        }
						connectRootNum++;
						e = graph.createEdge(upnode, selectNode);
						keyset.remove("selObj"+mainUp[2]);
					}
					
					//设置连接线的箭头样式
					EdgeRealizer er = graph.getRealizer(e);
					er.setSourceArrow(Arrow.SHORT);
				}
				
				
				
				
			}
			
			//keyset 里 关联了上级的都删除了，剩下的就是没有上级的，也就是汇报关系顶层，这些节点直接连接到 汇报关系节点下
			Iterator keyite = keyset.iterator();
			while(keyite.hasNext()){
				String key = keyite.next().toString();
				if(key.indexOf("selObj")!= -1) {
                    continue;
                }
				String[] mainDown = ((ArrayList)linkMap.get(key)).get(0).toString().split("_");
				Node downnode = (Node)BaseServlet.getForId(graph, mainDown[0]);
				String relationNodeId = "reid_"+key.split("_")[1];
				Node upnode = (Node)BaseServlet.getForId(graph,relationMap.get(relationNodeId).toString());
				graph.createEdge(upnode, downnode);
			    NodeRealizer nr = graph.getRealizer(upnode);
			    nr.getLabel(1).setVisible(true);
			    nr.getLabel(1).setIcon(OrgMapServlet.shrinkicon);
			}
			
			//当 查询一条汇报关系线时，查询结果里是没有 选中的对象的（比如树上选中了张某） 。如果张某在一条汇报关系的顶层，那么查询结果是空。但是如果 张某有下级，也要将他显示出来，可以查看下级。直接将他挂到 所属的汇报关系节点上
			Iterator headite = onHead.iterator();
			while(headite.hasNext()){
				String[] id = headite.next().toString().split("`");
				
					Node objectNode = (Node)BaseServlet.getForId(graph, id[2]);//createRelationNode(graph,id.split("`")[0],rootbean,textfont,textcolor,true);
					GenericNodeRealizer gnr = (GenericNodeRealizer)graph.getRealizer(objectNode);
					gnr.setLineColor(gnr.getLineColor().darker());
					String relationNodeId = "reid_"+id[0];
					Node upnode = (Node)BaseServlet.getForId(graph,relationMap.get(relationNodeId).toString());
					graph.createEdge(upnode, objectNode);
					NodeRealizer nr = graph.getRealizer(upnode);
				    nr.getLabel(1).setVisible(true);
				    nr.getLabel(1).setIcon(OrgMapServlet.shrinkicon);
					
			}
		}
		
		
		/**
		 * 创建一般节点（不参与审批关系中的节点，比如根节点、汇报关系名称节点等）
		 * @param graph
		 * @param nodedesc
		 * @param textFont
		 * @param fontcolor
		 * @return
		 */
		private Node createNormalNode(Graph2D graph,String nodedesc,Font textFont,Color fontcolor,String infoid){
			Node node = graph.createNode();
			NodeRealizer nr = graph.getRealizer(node);
		    
			NodeLabel info = nr.createNodeLabel();
	    	info.setText(infoid);
	    	info.setVisible(false);
	    	info.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_SIZE);
	    	nr.addLabel(info);
			
			
			int len = nodedesc.length();
			int widthchar = 8;
			double heightchar = 2;
			if(!"true".equals(chartParam.getShow_pic())){
				heightchar = 1.8;
			}
			
			if(heightchar==2 && len>widthchar && len<widthchar*2){
				nodedesc = nodedesc.substring(0, len/2)+"\n"+nodedesc.substring(len/2);
				//widthchar = len/2+len%2;
			}else if (heightchar==2 && len>widthchar*2){
				nodedesc = nodedesc.substring(0, widthchar)+"\n"+nodedesc.substring(widthchar, widthchar*2-1)+"...";
			}else if(heightchar == 1.8 && len>widthchar){
				nodedesc = nodedesc.substring(0, widthchar-1)+"...";
			}
			NodeLabel nl = nr.createNodeLabel();
			nl.setText(nodedesc);
			nl.setFont(textFont);
			nl.setTextColor(fontcolor);
			nr.addLabel(nl);
			nr.setWidth((widthchar*1.1)*(textFont.getSize()+2));
			nr.setHeight((1.8*1.5)*(textFont.getSize()+2));
			
			return node;
		}
		
		/**
		 * 创建审批关系节点（人或部门，实际参与汇报关系中的节点）
		 * @param graph 
		 * @param relationid 汇报关系id
		 * @param nodeBean 节点数据对象
		 * @param textfont 
		 * @param textcolor
		 * @param showtoggleicon 是否显示展开下级图标
		 * @return
		 */
		public Node createRelationNode(Graph2D graph,String relationid,LazyDynaBean nodeBean,
				                        Font textfont,Color textcolor,boolean showtoggleicon){
	    	Node rNode = graph.createNode();
	    	NodeRealizer nr = graph.getRealizer(rNode);
	    	
	    	String mainbody_id = nodeBean.get("mainbody_id").toString();
	    	String parent_id = nodeBean.get("parent_id")!=null?nodeBean.get("parent_id").toString():"";
	    	String nodetype = mainbody_id.substring(0,2).toUpperCase();//节点类型，UN、UM、@K表示机构,其它表示人员
	    	String nodedesc = nodeBean.get("a0101").toString();
	    	String nodeName = "";
	    	if(nodedesc.split(":").length > 1) {
	    	    nodeName = nodedesc.split(":")[1];//OrgMapBo.ToSBC(nodedesc.split(":")[1]);全部转成全角
	    	}
	    	// 设定为最多显示五个字，长度超出五个字，显示四个，剩余的用“...”代替
	    	//nodeName = nodeName.length()>5?nodeName.substring(0, 4)+"...":nodeName;
	    	nodeName = substrChinese(nodeName,8);//最大允许8个英文字母的长度 haosl update 20170406
	    	//设置隐藏label，保存一些必要的信息     
	    	NodeLabel info = nr.createNodeLabel();
	    	String mark = relationid+"`"+mainbody_id;
	    	//如果不是机构，加上上级的id信息
	    	if(!"UN".equals(nodetype) && !"UM".equals(nodetype) && !"@K".equals(nodetype)) {
                mark+="`"+parent_id;
            }
	    	info.setText(mark);
	    	info.setVisible(false);
	    	info.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_SIZE);
	    	nr.addLabel(info);
	    	
	    	//字体大小
    		int fontsize = textfont.getSize();
	    	String[] hint_text = nodeBean.get("tooltext").toString().split("&lt;BR&gt;");
	    	//显示图片
	    	//if("true".equals(chartParam.getShow_pic())){
	    		//节点照片
	    		ImageIcon nodePhoto;
	    		if("@K".equals(nodetype)){
		  		    nodePhoto = new ImageIcon(OrgMapBo.appImagesPath + "/report_pos.png");
	    			String[] posup = new OrgMapBo().getUnitAndDept(mainbody_id.substring(2)).split(",");
	    			posup[0] = "所在单位:"+posup[0];
	    			if(posup.length>1) {
                        posup[1] = "所在部门:"+posup[1];
                    }
	    			hint_text = posup;
	    		}else if ("UN".equals(nodetype)){
	    			nodePhoto = new ImageIcon(OrgMapBo.appImagesPath + "/report_unit.png");
	    			 hint_text = new String[]{};
	    		}else if("UM".equals(nodetype)){
	    			nodePhoto = new ImageIcon(OrgMapBo.appImagesPath + "/report_dept.png");
	    			hint_text = getOrgUp(mainbody_id.substring(2), "UM").split(",");
	    		}else{
	    			byte[] photo = new OrgMapBo().getPhote(mainbody_id.substring(3), mainbody_id.substring(0,3));
		  		      if(photo!=null){
		  		    	nodePhoto = new ImageIcon(photo);
		  			      if(nodePhoto.getIconWidth()<0 || nodePhoto.getIconHeight()<0) {
                              nodePhoto = new ImageIcon(OrgMapBo.appImagesPath + "/photo.jpg");
                          }
		  		      }else{
		  		    	nodePhoto = new ImageIcon(OrgMapBo.appImagesPath + "/photo.jpg");
		  		      }
	    		}
	    		
	    		//计算图片大小。每个节点的高度为 = 节点名称占 一行，详细指标占3行（详细指标字体大小为节点名称的4/5） ，行距统一为1.5倍
	    		// 图片高 = （字体大小*1.5） + （字体大小*4/5 * 1.5）*3
	    		int pheight = (int)((fontsize*1.5)+(fontsize*4/5*1.5)*3);
	    		//图片宽 ：因为图片是4:3的比例，所以宽为高的3/4；
	    		int pwidth = pheight*3/4;
	    		nodePhoto.setImage(nodePhoto.getImage().getScaledInstance(pwidth,pheight,Image.SCALE_SMOOTH));
	    		NodeLabel iconlabel = nr.createNodeLabel();
	    		iconlabel.setIcon(nodePhoto);
	    		iconlabel.setPosition(NodeLabel.LEFT);
	    		nr.addLabel(iconlabel);
	    		
	    		//设置字体位置：左边距：图片左上角和节点左边缘的宽度+图片宽度+1/2的字体宽度缝隙
	    		double offXtext = iconlabel.getOffsetX()+pwidth+fontsize/2;
	    		NodeLabel nl = nr.createNodeLabel();
	    		nl.setModel(NodeLabel.FREE);
	    		//         上边距：1/10的图片高度
	    		nl.setOffset(offXtext,pheight/10);
	    		nl.setText(nodeName);
	    		nl.setFont(textfont);
				nl.setTextColor(textcolor);
	    		nr.addLabel(nl);
	    		
	    		String  fielddesc = "";
	    		for(int i=0;i<hint_text.length&&i<3;i++){
	    			if(hint_text[i].split(":").length<2) {
                        continue;
                    }
	    			String field = hint_text[i].split(":")[1];
	    			
	    			field = substrChinese(field,12);//最大允许12个英文字母的长度(6个汉字)haosl update 20170406
//	    			if(field.length()>7)//超过七个字显示六个剩余的用“...”代替。因为指标描述比节点名称字体要小，所以这里可以显示7个。
//	    				field = OrgMapBo.ToSBC(field.substring(0,5))+"...";
	    			fielddesc+=field+"\n";
	    		}
	    		NodeLabel fieldlabel = nr.createNodeLabel();
	    		fieldlabel.setModel(NodeLabel.FREE);
	    		fieldlabel.setOffset(offXtext, pheight/10+fontsize*1.5);
	    		fieldlabel.setText(fielddesc);
	    		fieldlabel.setAlignment(NodeLabel.LEFT_TEXT_POSITION);
	    		fieldlabel.setFont(textfont);
	    		fieldlabel.setFontSize(fontsize*4/5);
	    		fieldlabel.setTextColor(textcolor);
	    		nr.addLabel(fieldlabel);
	    		
	    		//根据图片大小和字计算节点大小
	    		double linewidth = 1.5; //行距，微软雅黑字体比较大，单独处理一下
	    		if("yahei".equals(chartParam.getFontName())) {
                    linewidth = 1.7;
                }
	    		double nodeheight = (fontsize*linewidth)+(fontsize*4/5*linewidth)*3+0.5*fontsize;
	    		double nodewidth = nodeheight*3/4+fontsize*1.1*5+fontsize;
	    		nr.setWidth(nodewidth);
	    		nr.setHeight(nodeheight);
//	    	}else{
//	    		if (nodetype.equals("UN")){
//	    			 hint_text = new String[]{};
//	    		}else if(nodetype.equals("UM")){
//	    			hint_text = getOrgUp(mainbody_id.substring(2), "UM").split(",");
//	    		}else if(nodetype.equals("@K")){
//	    			String[] posup = new OrgMapBo().getUnitAndDept(mainbody_id.substring(2)).split(",");
//	    			posup[0] = "所在单位:"+posup[0];
//	    			if(posup.length>1)
//	    				posup[1] = "所在部门:"+posup[1];
//	    			hint_text = posup;
//	    		}
//	    		if(nodeName.length()>4)
//	    			nodeName = nodeName.substring(0,3)+"...";
//	    		NodeLabel nl = nr.createNodeLabel();
//		    	nl.setText(nodeName);
//		    	nl.setFont(textfont);
//		    	nl.setTextColor(textcolor);
//		    	nr.addLabel(nl);
//		    	
//	    		nr.setWidth((5*1.1)*(fontsize+2));
//				nr.setHeight((1.8*1.5)*(fontsize+2));
//	    	}
	    	
	    	//将节点hint信息放进map里，生成json数据传到前台
	    	String hint_info_desc = nodedesc+"`";
	    	for(int k=0;k<hint_text.length;k++){
	    		hint_info_desc+=hint_text[k]+"`";
	    	}
	    	String[] hintinfo = new String[2];
	    	hintinfo[0] = hint_info_desc;
	    	hintinfo[1] = mainbody_id;
	    	nodeHintInfo.put(BaseServlet.getId(graph, rNode),hintinfo);
	    	
	    	//是否显示展开按钮
	    	if(showtoggleicon){
	    		//判断是否有下级，如果没有，不显示展开按钮
	    		HashMap childrenMap = new HashMap();
				this.getChildrenLink(childrenMap, relationid, mainbody_id, new ContentDAO(this.conn));
				if(childrenMap.size()==0) {
                    return rNode;
                }
				//走到这里说明有下级，显示按钮
	    		nr.getLabel(1).setVisible(true);
	    		//if("1".equals(chartParam.getDirection()))
	    		////	nr.getLabel(1).setPosition(NodeLabel.BOTTOM);
	    		//else
	    		//	nr.getLabel(1).setPosition(NodeLabel.RIGHT);
	    	}
	    	
	    	
	    	return rNode;
	    }
		
		public ArrayList getChildNode(String re_main){
			String[] temp = re_main.split("`");
			String relation_id=temp[0];
			String mainbody_id=temp[1];
			
			resetDescAndHintList();
			HashMap childrenMap = new HashMap();
			int layer = 1;//共有几层下级
			this.getChildrenLink(childrenMap, relation_id, mainbody_id, new ContentDAO(this.conn));
			
			if(childrenMap.size()==0) {
                return new ArrayList();
            }
			int visibleChildren = 0;//页面上展现出的孩子节点数量
			int lay=1;//页面展现孩子节点的层数
			ArrayList currChildren = this.getCurrentNodeChildren(re_main, childrenMap);
			return currChildren;
		}
		
		public void resetDescAndHintList(){
			this.chartParam.setDesc_items("a0101");
			String hint_items = chartParam.getHint_items();
			if(hint_items.length()>0){
				String[] itemarray = hint_items.split(",");
				hint_items = "";
				for(int i=0;i<itemarray.length;i++){
					if(!"a0101".equals(itemarray[i]) && !"e01a1".equals(itemarray[i]) && !"e0122".equals(itemarray[i]) && !"b0110".equals(itemarray[i])) {
                        hint_items+= itemarray[i]+",";
                    }
				}
			}
		    hint_items = "b0110,e0122,e01a1,"+hint_items;
			this.chartParam.setHint_items(hint_items);
			
		}
		
		/**
		 * 查询机构的上级描述 
		 * @param orgId 
		 * @return
		 */
		private String getOrgUp(String orgId,String orgtype){
			String desc = "";
			String sql = "select codeitemdesc from organization where codesetid='UN' and  codeitemid ="+Sql_switcher.substr("'"+orgId+"'", "1", Sql_switcher.length("codeitemid"))+"order by codeitemid desc";
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = null;
			try{
				rs = dao.search(sql);
				if(rs.next()) {
                    desc ="所在单位:"+rs.getString("codeitemdesc");
                }
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				PubFunc.closeDbObj(rs);
			}
			return desc;
			
		}
		
		public String getUnitAndDept(String posId){
			
			String codeitemid = "";
			String codesetid = "";
			String e0122 = "";
			String b0110 = "";
			RowSet rs = null;
			String sql = "select codesetid,codeitemid,codeitemdesc from organization where codeitemid = (select e0122 from k01 where e01a1='"+posId+"')";
			try{
				ContentDAO dao = new ContentDAO(conn);
				rs = dao.search(sql);
				while(rs.next()){
					codeitemid = rs.getString("codeitemid");
					codesetid = rs.getString("codesetid");
					e0122 += rs.getString("codeitemdesc")+",";
					
					if("UM".equals(codesetid)){
						sql = "select codeitemdesc from organization where codesetid='UN' and  codeitemid ="+Sql_switcher.substr("'"+codeitemid+"'", "1", Sql_switcher.length("codeitemid"))+"order by codeitemid desc";
						rs = dao.search(sql);
						if(rs.next()) {
                            b0110 += rs.getString("codeitemdesc")+",";
                        }
						
						break;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				PubFunc.closeDbObj(rs);
			}
			return b0110+e0122;
		}
		
		/**
		 * 找当前节点下的所有子节点（孩子，孙子等）
		 * @param map
		 * @param relation_id
		 * @param currentId
		 * @param dao
		 */
		public void getChildrenLink(HashMap map ,String relation_id,String currentId,ContentDAO dao){
			RowSet rs = null;
			try{
				
				StringBuffer sql = new StringBuffer();
				sql.append("select object_id,mainbody_id from t_wf_mainbody where SP_GRADE=9 and relation_id=");
				sql.append(relation_id);
				sql.append(" and UPPER(mainbody_id)='"+currentId.toUpperCase()+"' "+getPrivSql());
				rs = dao.search(sql.toString());
				while(rs.next()){
					
					if(!isPersonInPriv(rs.getString("object_id"))) {
                        continue;
                    }
						
					map.put(relation_id+"`"+rs.getString("object_id").toUpperCase(),"1");
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * 根据 权限 获取 汇报线 顶级 对象
		 * @param relationid 要查询的汇报关系线 id
		 * @return
		 */
		public ArrayList getRelationAllChild(String relationid){
			ArrayList sortList = new ArrayList();
			ArrayList list = new ArrayList();
			
			RowSet rs = null;
			ContentDAO dao = null;
			ArrayList valuelist = new ArrayList();
			try{
				StringBuffer sql = new StringBuffer();
				int dbServer = Sql_switcher.searchDbServer();
				if(dbServer==1){
					sql.append("select distinct mainbody_id from t_wf_mainbody where SP_GRADE=9 and relation_id=? and mainbody_id not in (select object_id from t_wf_mainbody where SP_GRADE=9 and relation_id=? "+getPrivSql()+" ) ");
				}else if(dbServer==2){
					sql.append("select distinct mainbody_id from t_wf_mainbody where SP_GRADE=9 and relation_id=? and upper(mainbody_id) not in (select upper(object_id) from t_wf_mainbody where SP_GRADE=9 and relation_id=? "+getPrivSql()+" ) ");
				}
				sql.append(getPrivSql());
				dao = new ContentDAO(conn);
				valuelist.add(relationid);
				valuelist.add(relationid);
				rs = dao.search(sql.toString(), valuelist);
				
				ArrayList empList = new ArrayList();
				while(rs.next()){
					String mainId = rs.getString("mainbody_id");
					String dbStr = DataDictionary.getDbpreString();
					if(mainId.length()>3 && dbStr.toLowerCase().indexOf(mainId.substring(0, 3).toLowerCase())!=-1){
						empList.add(mainId);
					}else{
						list.add(rs.getString("mainbody_id")); 
					}
				}
				
				//按照a0000重新排序 guodd
				ArrayList dblist = DataDictionary.getDbpreList();
				for(int i=0;i<dblist.size();i++){
					String dbpre = dblist.get(i).toString();
					sql.setLength(0);
					sql.append("select a0100 from ");
					sql.append(dbpre);
					sql.append("A01 where a0100 in (");
					for(int k=0;k<empList.size();k++){
						String mainId = empList.get(k).toString();
						if(!mainId.substring(0, 3).toLowerCase().equals(dbpre.toLowerCase())) {
                            continue;
                        }
						sql.append("'");
						sql.append(mainId.substring(3));
						sql.append("',");
					}
					sql.append(" 'else' ) order by a0000 ");
					
					rs = dao.search(sql.toString());
					while(rs.next()) {
                        sortList.add(dbpre+rs.getString("a0100"));
                    }
				}
				
				sortList.addAll(list);
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				PubFunc.closeDbObj(rs);
				dao = null;
				valuelist = null;
			}
			return sortList;
		}
		
		
		private String  getPrivSql(){
			String priSql = " and 1=1 ";
			if(!userView.isSuper_admin()){
				String privtype = userView.getManagePrivCode();
				if("UN".equalsIgnoreCase(privtype)) {
                    priSql = " and  B0110 like '"+userView.getManagePrivCodeValue()+"%' ";
                } else if("UM".equalsIgnoreCase(privtype)) {
                    priSql = " and  E0122 like '"+userView.getManagePrivCodeValue()+"%' ";
                } else {
                    priSql = " and  E01A1 like '"+userView.getManagePrivCodeValue()+"%' ";
                }
			}
			
			return priSql;
		}
		
		public boolean isPersonInPriv(String object_id){
			
			String privCodeValue = this.userView.getManagePrivCodeValue();
			
			if(object_id.indexOf("UN")!=-1 || object_id.indexOf("UM")!=-1 || object_id.indexOf("@K")!=-1){
				if(object_id.substring(2).length() < privCodeValue.length() || object_id.substring(2).indexOf(privCodeValue) != 0) {
                    return false;
                }
			}else{
				String sqlstr = " select 'a' from "+object_id.substring(0,3)+"A01 where a0100='"+object_id.substring(3)+"' "+getPrivSql();
				List list = ExecuteSQL.executeMyQuery(sqlstr, conn);
				if(list==null || list.size()<1) {
                    return false;
                }
			}
			
			return true;
		}
		
		/**
		 * 寻找主管领导
		 * @param mainid 人员id ：Usr000000009
		 * @param relationId relationId;
		 * @return 查询出的主管领导 id
		 */
		public String findManagerPerson(String mainid,String relationId){
			String manager = "";
			String sql = "select mainbody_id from t_wf_mainbody where UPPER(object_id)='"+mainid.toUpperCase()+"' and relation_id="+relationId+" and sp_grade=10";
			List re = ExecuteSQL.executeMyQuery(sql);
			for(int i=0;i<re.size();i++){
				manager+=((LazyDynaBean)re.get(i)).get("mainbody_id")+",";
			}

			return manager;
		}
		
		/**
		 * 截取字符串并添加..
		 * 
		 * @param content 输入的内容
		 * @param maxSize 最大英文字母个数
		 * haosl 20170406
		 * @return
		 */
		public String substrChinese(String content, Integer maxSize) {
			String result = content;
			if (StringUtils.isNotBlank(result)) {
				int valueLength = 0;
				int endIndex = 0;
				String chinese = "[\u0391-\uFFE5]";
				/* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
				for (int i = 0; i < result.length(); i++) {
					/* 获取一个字符 */
					String temp = result.substring(i, i + 1);
					/* 判断是否为中文字符 */
					if (temp.matches(chinese)) {
						/* 中文字符长度为2 */
						valueLength += 2;
					} else {
						/* 其他字符长度为1 */
						valueLength += 1;
					}
					if(valueLength <= maxSize){
						endIndex++;
					}
				}
				if(valueLength>maxSize) {
                    result = result.substring(0, endIndex) + "...";
                }
			}
			return result;
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
		public String getA_code() {
			return a_code;
		}
		public void setA_code(String a_code) {
			this.a_code = a_code;
		}
		public HashMap getNodeHintInfo() {
			return nodeHintInfo;
		}
		
}
