package com.hjsj.hrms.businessobject.org.yfileschart;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.xmlparameter.SetOrgOptionParameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import com.yworks.yfiles.server.tiles.servlet.BaseServlet;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import y.base.Node;
import y.io.JPGIOHandler;
import y.view.*;
import y.view.YLabel.Factory;
import y.view.YLabel.Painter;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

/**
 * 机构图
 * @author guodd
 *
 */
public class OrgMapBo {
    public static String appImagesPath = null;

	//机构图
	public static final int ORGMAP = 0;
	//汇报关系
	public static final int POSRELATION = 1;
	
	
	private HashMap orgInfoMap = new HashMap();
	
	//反转节点展示方向   岗位汇报关系图第一个节点使用，因为岗位汇报关系是同个一个指标关联的，
	//跟机构的编码规律不同，使用机构图算法有些差异，靠此参数修正
	boolean posConvert = false;
	
	
	//岗位汇报关系   岗位上级信息 
	private HashMap posUpInfoMap;
	
	private UserView userView;
	
	public OrgMapBo(){
		
	}
	
	public OrgMapBo(UserView userView){
		this.userView = userView;
	}
	
	private String org_root_caption = "组织机构";//顶级机构显示文字 chent 20170729
	
	/**
	 * 设置 map 默认样式
	 * @param graph 画布
	 * @param optionMap 参数 
	 * @param icon 图标
	 */
	public void setDefaultRealizer(Graph2D graph,HashMap optionMap,ImageIcon icon){
		
		
		//初始化节点样式工厂
		GenericNodeRealizer.Factory factory = GenericNodeRealizer.getFactory();
	    Map configurationMap = factory.createDefaultConfigurationMap();

	    GenericNodeRealizer.Painter painter = null;
	    
	    String isshowshadow = optionMap.get("isshowshadow").toString();
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
	    
	    String cellcolor = optionMap.get("cellcolor").toString();
	    String transitcolor = optionMap.get("transitcolor").toString();
	    
	    nr.setFillColor(new Color(Integer.parseInt(cellcolor.replace("#",""),16)));
	    if(transitcolor.length()>0) {
            nr.setFillColor2(new Color(Integer.parseInt(transitcolor.replace("#",""),16)));
        }
	    
	    String bordercolor = optionMap.get("bordercolor").toString();
	    if(bordercolor.length()>0) {
            nr.setLineColor(new Color(Integer.parseInt(bordercolor.replace("#",""),16)));
        } else {
            nr.setLineColor(null);
        }
	    
	    String borderwidth = optionMap.get("borderwidth").toString();
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
		    Painter labelpainter = new MyLablePainter(optionMap.get("fontcolor").toString());
	        Map map = f.createDefaultConfigurationMap();
	        map.put(Painter.class, labelpainter);
	        f.addConfiguration("buttonC", map);
		    NodeLabel label = nr.createNodeLabel();
		    label.setTextColor(Color.red);
		    label.setIcon(icon);
		    label.setVisible(true);
		    label.setConfiguration("buttonC");
		    nr.addLabel(label);
	    }
	    
        nr.setConfiguration("nrpainter");
	    graph.setDefaultNodeRealizer(nr);
	    
	    //连接线样式
	    PolyLineEdgeRealizer er = new PolyLineEdgeRealizer();
	    er.setSmoothedBends(false);
	    
	    String linecolor = optionMap.get("linecolor").toString();
	    er.setLineColor(new Color(Integer.parseInt(linecolor.replaceAll("#", ""),16)));
	    
	    String linewidth  = optionMap.get("linewidth").toString();
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
	 * 获取参数
	 * @param type 0：机构图 ，1：岗位汇报关系
	 * @return
	 */
	public HashMap getMapOptions(int type){
		
		HashMap optionMap = new HashMap();
		SetOrgOptionParameter options = new SetOrgOptionParameter();
		
		String constant = "ORG_MAPOPTION";
		if(type == POSRELATION){
			constant = "POS_MAPOPTION";
			Connection conn = null;
			try{
				conn = AdminDb.getConnection();
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			    String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
				seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
				optionMap.put("seprartor", seprartor);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				OrgMapBo.closeDBTool(conn);
			}
			
		}
		ArrayList resultnodenamevaluelist=options.ReadOutParameterXml(constant);
		
			for(int i=0;i<resultnodenamevaluelist.size();i++){
				LabelValueView labelvalue=(LabelValueView)resultnodenamevaluelist.get(i);
				optionMap.put(labelvalue.getLabel(),labelvalue.getValue());
			}
			
    	if(optionMap.size()==0){
    		if(type == POSRELATION){
    			optionMap.put("isshowposup","fasle");
    		}else{
    			optionMap.put("dbnames", "Usr");
				optionMap.put("isshowpersonconut", "false");
				optionMap.put("isshowpersonname", "false");
				optionMap.put("isshowphoto", "false");
				optionMap.put("isshoworgconut", "false");
				optionMap.put("isshowposname", "false");
    		}
    		
    	}
    	
	    if(!optionMap.containsKey("maptheme")){
	    	optionMap = MapThemeBo.getThemeOptions(optionMap, MapThemeBo.THEMES_BULE);
	    }
    	
    	return optionMap;
	}
	
	
	
	/**
	 * 通过 id 查询 机构
	 * @param optionMap  机构图显示参数
	 * @param conn  
	 * @param isRoot  是否根节点，如果是只搜索值为code的信息，如果不是则搜索值为code的子机构信息
	 * @return
	 */
	public List searchInfoByCode(HashMap optionMap,Connection conn,boolean isRoot,String allflag){
		
		List infoList = new ArrayList();
		
		String code = optionMap.get("code").toString();
		String backdate = optionMap.get("backdate").toString();
		
		//如果code说明没有任何机构范围，直接跳出
		if("#".equals(code)) {
            return infoList;
        }
		//如果显示人数并且没有授权人员库，跳出
		if("true".equals(optionMap.get("isshowpersonconut")) && optionMap.get("dbnames").toString().length()<1) {
            return infoList;
        }
		
		//查询语句
		StringBuffer sqlstr = new StringBuffer();
		
		
		String dbnames = PubFunc.nullToStr((String)optionMap.get("dbnames"));
		if(" ".equals(dbnames)) {
            dbnames = "Usr";
        }
		String personMainSet = dbnames+"A01";
		//获取高级授权条件语句
		String priStr=""; 
		try {
			priStr = userView.getPrivSQLExpression(dbnames, false);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		if(!"true".equals(optionMap.get("ishistory"))){
			//数据来源sql
			StringBuffer orgSetSql = new StringBuffer();
			orgSetSql.append(" select * from (select codeitemid,codesetid,codeitemdesc,parentid,childid,a0000,view_chart,start_date,end_date from organization union all ");
			orgSetSql.append(" select codeitemid,codesetid,codeitemdesc,parentid,childid,a0000,view_chart,start_date,end_date from vorganization) O ");
			orgSetSql.append(" where "+Sql_switcher.dateValue(backdate)+" between O.start_date and O.end_date ");
			if(!isRoot) {
                orgSetSql.append(" and (O.view_chart<>1 or O.view_chart is null) ");
            }
			if("false".equals(optionMap.get("isshowposname"))) {
                orgSetSql.append(" and O.codesetid<>'@K' ");
            }
			
			//人员来源sql
			StringBuilder personSetSql = new StringBuilder();
			personSetSql.append("select A0100,B0110,E0122,E01A1 ");
			personSetSql.append("from (");
			personSetSql.append(" select A0100,B0110,E0122,E01A1 from "+personMainSet+" where e01a1 not in (select codeitemid from organization where view_chart='1') ");
			if(priStr.trim().length()>0) {
                personSetSql.append(" and a0100 in (select a0100 "+priStr+" ) ");
            }
			
			// 兼职人员 chent 20170516 start
		    if(optionMap.containsKey("isshowpartjobperson") && "true".equals(optionMap.get("isshowpartjobperson"))){
		    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
		        String flag = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
		        if("true".equals(flag)){//启用兼职
		        	String setid = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "setid");
	                String unit = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "unit"); 
	                String dept = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "dept"); 
	                String pos = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos"); 
	                String appoint_field = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");// 任免标识。 0：任 1：免
		        	if(StringUtils.isNotEmpty(appoint_field)) {
		        	    String curPartTable = dbnames + setid;// 兼职表
		        	    personSetSql.append(" union all ");
		        	    personSetSql.append(" SELECT a0100 ");
		        	    if(StringUtils.isNotEmpty(unit)){
		        	        personSetSql.append(","+unit);
		        	    } else {
		        	        personSetSql.append(",'' ");
		        	    }
		        	    
		        	    if(StringUtils.isNotEmpty(dept)){
		        	        personSetSql.append(","+dept);
		        	    } else {
		        	        personSetSql.append(",'' ");
		        	    }
		        	    
		        	    if(StringUtils.isNotEmpty(pos)){
		        	        personSetSql.append(","+pos);
		        	    } else {
		        	        personSetSql.append(",'' ");
		        	    }
		        	    
		        	    personSetSql.append(" from "+curPartTable+" ");
		        	    personSetSql.append(" where 1=1 ");
		        	    if(StringUtils.isNotEmpty(unit)){
		        	        personSetSql.append(" and " +unit+" not in (select codeitemid from organization where view_chart='1') and "+appoint_field+"='0' ");
		        	    }
		        	    
		        	    if(StringUtils.isNotEmpty(dept)){
		        	        personSetSql.append(" and " +dept+" not in (select codeitemid from organization where view_chart='1') and "+appoint_field+"='0' ");
		        	    }
		        	    
		        	    if(StringUtils.isNotEmpty(pos)){
		        	        personSetSql.append(" and " +pos+" not in (select codeitemid from organization where view_chart='1') and "+appoint_field+"='0' ");
		        	    }
		        	}
		        }
		    }
		    // 兼职人员 chent 20170516 end
		    personSetSql.append(" ) T");
			// 拼加 查询sql
			sqlstr.append(" select OrgSet.codeitemid,OrgSet.codesetid,OrgSet.codeitemdesc,OrgSet.a0000,count(a0100) as personNum,OrgSet.view_chart,");
			sqlstr.append(" (select count(1) from ("+orgSetSql+") C where C.parentid<>C.codeitemid and C.parentid=OrgSet.codeitemid and (C.view_chart<>1 or C.view_chart is null)");
//			if(!"true".equals(optionMap.get("isshowposname")))
//				sqlstr.append(" and codesetid<>'@K' ");
			sqlstr.append(" ) as nextOrgNum ");
			sqlstr.append(" from ("+orgSetSql+") OrgSet left join ");
			sqlstr.append(" ("+personSetSql+") PersonSet");
			sqlstr.append(" on "+Sql_switcher.substr("PersonSet.B0110", "1", Sql_switcher.length("OrgSet.codeitemid"))+"= OrgSet.codeitemid or "+Sql_switcher.substr("PersonSet.e0122", "1", Sql_switcher.length("OrgSet.codeitemid"))+"= OrgSet.codeitemid OR "+Sql_switcher.substr("PersonSet.e01a1", "1", Sql_switcher.length("OrgSet.codeitemid"))+"= OrgSet.codeitemid ");
		
		}else{
			//历史机构进入
			sqlstr.append(" select OrgSet.codeitemid,OrgSet.codesetid,OrgSet.codeitemdesc,OrgSet.a0000,'0' as personNum,'0' as view_chart,");
			sqlstr.append(" (select count(1) from hr_org_history where catalog_id='"+optionMap.get("catalog_id"));
			sqlstr.append("' and parentid<>codeitemid and parentid=OrgSet.codeitemid ) as nextOrgNum ");
			sqlstr.append(" from hr_org_history OrgSet ");
		}
		
		
		//拼加 where
		if(code.length()>0){
			if(isRoot) {
                sqlstr.append(" where OrgSet.codeitemid = '"+code+"' ");
            } else {
                sqlstr.append(" where OrgSet.parentid = '"+code+"' and OrgSet.parentid<>OrgSet.codeitemid");
            }
		}else{
			// 按照业务范围显示机构，只显示权限范围内的。chent 20180316 add start
			StringBuilder codeStr = new StringBuilder();
			if(this.userView.isSuper_admin()) {
				sqlstr.append(" where OrgSet.codeitemid = OrgSet.parentid ");
			} else {
				String busi= OrgMapBo.getBusi_org_dept(userView);
				if(busi.length()>2){
					String[] bArr = busi.split("`");
					for(String b : bArr) {
						if(StringUtils.isNotEmpty(b)) {
							String c = b.substring(2);
							codeStr.append("'"+c+"',");
						}
					}
				}
				if(codeStr.length() > 0) {
					codeStr.deleteCharAt(codeStr.length()-1);
				}
				sqlstr.append(" where OrgSet.codeitemid in ("+codeStr+")");
			}
			// 按照业务范围显示机构，只显示权限范围内的。chent 20180316 add end
		}
		
		if("true".equals(optionMap.get("ishistory"))) {
            sqlstr.append(" and catalog_id='"+optionMap.get("catalog_id")+"'");
        }
			
		sqlstr.append(" group by OrgSet.codeitemid,OrgSet.codesetid,OrgSet.codeitemdesc,OrgSet.a0000");
		if(!"true".equals(optionMap.get("ishistory"))) {
            sqlstr.append(",OrgSet.view_chart ");
        }
		sqlstr.append(" order by OrgSet.a0000 ");
		if(isRoot && "00".equals(code)){//“组织机构”这个虚拟的顶级节点 chent
			LazyDynaBean rec=new LazyDynaBean();
			rec.set("codeitemid", "0");	
			rec.set("codesetid", "0");
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			String org_root_caption = sysbo.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
	        if(StringUtils.isNotEmpty(org_root_caption)){
	        	this.org_root_caption = org_root_caption;
	        }
			rec.set("codeitemdesc", this.org_root_caption);	
			rec.set("a0000", "0");	
			rec.set("personnum", "0");	
			rec.set("view_chart", "0");	
			rec.set("nextorgnum", "1");	
			infoList.add(rec);
		}else {
			infoList = ExecuteSQL.executeMyQuery(sqlstr.toString(),conn);
		}
		
		if("false".equals(optionMap.get("isshowpersonname")) || optionMap.get("dbnames").toString().length()<1 || isRoot || (("all".equals(allflag) && "true".equals(optionMap.get("isshowphoto")) ) && infoList.size()<1)) {
            return infoList;
        }
		
		
		
		//加载人员节点
		StringBuffer loadpersonsql = new StringBuffer();
		String codewhere = " and (codeitemid='"+code+"')";
		String orderbysql = " order by a0000";
		
		loadpersonsql.append("select codeitemid,codesetid,codeitemdesc,parentid, A0000 ");
		loadpersonsql.append("from (");
		
		loadpersonsql.append("SELECT b.a0100 codeitemid,'RY' codesetid,  b.A0101 codeitemdesc,a.codeitemid parentid, b.A0000 from organization a, ");
	    loadpersonsql.append(personMainSet+" b where ");
	    loadpersonsql.append(" (((b.E0122= a.codeitemid) and ((b.E01A1 is null) or (b.e01a1=' ') or (" + Sql_switcher.trim("b.E01A1") + " =");
	    loadpersonsql.append("''))) or ");   // 有部门，无职位
	    loadpersonsql.append("(b.E01A1=a.codeitemid) or ");  // 有职位
	         // 有单位，无部门职位
	    loadpersonsql.append("((b.B0110=a.codeitemid) and ((b.E0122 is null) or (b.E0122=' ') or (" + Sql_switcher.trim("b.E0122") + "='')) and ((b.E01A1 is null) or (b.E01A1=' ') or  (" + Sql_switcher.trim("b.E01A1") + "='')))");
	    loadpersonsql.append(")");
	    loadpersonsql.append(" and "+Sql_switcher.dateValue(backdate)+" between a.start_date and a.end_date ");
	    loadpersonsql.append(" and (a.view_chart<>1 or a.view_chart is null)");
	    if(priStr.trim().length()>0) {
            loadpersonsql.append(" and b.a0100 in (select a0100 "+priStr+" ) {where}");
        }
	   
	    // 兼职人员 chent 20170516 start
	    if(optionMap.containsKey("isshowpartjobperson") && "true".equals(optionMap.get("isshowpartjobperson"))){
	    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
	        String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
	        if("true".equals(flag)){//启用兼职
	        	String setid = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "setid");
                String unit = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "unit"); 
                String dept = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "dept"); 
                String pos = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos"); 
                String appoint_field = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");// 任免标识。 0：任 1：免
                //String order = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "order");
	        	if(StringUtils.isNotEmpty(appoint_field)) {
	        	    String curPartTable = dbnames + setid;// 兼职表
	        	    loadpersonsql.append(" union all ");
	        	    loadpersonsql.append(" SELECT a.a0100 codeitemid,'PJ' codesetid, c.A0101 codeitemdesc ,'"+code+"' parentid ");
	        	    loadpersonsql.append(", c.a0000 " );
	        	    
	        	    loadpersonsql.append(" from "+curPartTable+" a, "+personMainSet+" c ");
	        	    loadpersonsql.append(" where ");
	        	    loadpersonsql.append(" exists (select 1  from Usra01 b ");
	        	    loadpersonsql.append(" where b.A0100=a.A0100  ");
	        	    loadpersonsql.append(" and (1=2 ");
	        	    if(StringUtils.isNotEmpty(unit)){
	        	        loadpersonsql.append(" or ("+unit+"='"+code+"'  ");
	        	        if(StringUtils.isNotEmpty(dept)){
	        	            loadpersonsql.append("and nullif("+dept+",'') is null ");
	        	        }
	        	        if(StringUtils.isNotEmpty(pos)){
	        	            loadpersonsql.append("and nullif("+pos+",'') is null ");
	        	        }
	        	        loadpersonsql.append(") ");
	        	    }
	        	    
	        	    if("true".equals(optionMap.get("isshowposname"))){
	        	        if(StringUtils.isNotEmpty(dept)){
	        	            loadpersonsql.append(" or ("+dept+"='"+code+"'  ");
	        	            if(StringUtils.isNotEmpty(pos)){
	        	                loadpersonsql.append(" and nullif("+pos+",'') is null ");
	        	            }
	        	            loadpersonsql.append(")");
	        	        }
	        	        if(StringUtils.isNotEmpty(pos)){
	        	            loadpersonsql.append(" or "+pos+"='"+code+"' ");
	        	        }
	        	    }else {
	        	        if(StringUtils.isNotEmpty(dept)){
	        	            loadpersonsql.append(" or ("+dept+"='"+code+"'  ");
	        	            loadpersonsql.append(")");
	        	        }
	        	    }
	        	    
	        	    loadpersonsql.append(" ) and "+appoint_field+"='0' ) and a.A0100=c.A0100 ");
	        	}
	        }
	    	
	    }
	 // 兼职人员 chent 20170516 end
	    
	    loadpersonsql.append(" ) T");
	    String sql = loadpersonsql.toString().replace("{where}", codewhere)+orderbysql;
	    List personList = ExecuteSQL.executeMyQuery(sql,conn);
	    
	    if("true".equals(optionMap.get("isshowposname"))){
	    	// 人员加载在最前面
	    	infoList.addAll(0,personList);
	    	return infoList;
	    }
	    codewhere = " and ((codeitemid like'"+code+"%' and codesetid='@K' and parentid='"+code+"')"
	    		+ " or (codeitemid='"+code+"' and (codesetid='UM' or codesetid='UN')))";
	    sql = loadpersonsql.toString().replace("{where}", codewhere)+orderbysql;
	    List posPerson = ExecuteSQL.executeMyQuery(sql,conn);
	    for(int i=0; i<posPerson.size(); i++) {
	    	LazyDynaBean person = (LazyDynaBean)posPerson.get(i);
	    	if(person != null) {
	    		infoList.add(person);
	    	}
	    }
		return infoList;
	}
	
	/**
	 * 加载节点
	 * @param rootNode 父节点
	 * @param graph  画布
	 * @param childList 待加载的节点
	 * @param optionMap 参数
	 * @param expandflag 加载标示  true 加载所有下级； false 加载childList中的节点
	 * @param conn 
	 * @param shrinkicon 图标
	 */
	public void expandChildNodes(Node rootNode,Graph2D graph,List childList,HashMap optionMap,boolean expandflag,Connection conn,ImageIcon shrinkicon,String allflag){
		
		if(!"true".equals(optionMap.get("graphaspect"))) {
            Collections.reverse(childList);
        }
		
		  for(int i=0;i<childList.size();i++){
			  LazyDynaBean ldb = (LazyDynaBean)childList.get(i);
			  
			  // 排除掉没有权限的机构
			  if(!"RY".equals(ldb.get("codesetid")) && !"PJ".equals(ldb.get("codesetid")) && !userView.isSuper_admin()) {
				  boolean flag = false;
				  String codeitemid = (String)ldb.get("codeitemid");
				  String busi = OrgMapBo.getBusi_org_dept(userView);
				  if(busi.length()>2){
					  String[] arr = busi.split("`");
					  for(String unit : arr) {
						  String code = unit.substring(2);
						  if(codeitemid.startsWith(code)) {
							  flag = true;
							  break;
						  }
					  }
				  }
				  if(!flag) {
					  continue ;
				  }
			  }
			  
			  Node node = graph.createNode();
			  
			  boolean iconflag = true;
			  if("RY".equals(ldb.get("codesetid")) || "PJ".equals(ldb.get("codesetid"))){// 人员  | 兼职人员节点生成 chent 20170517
				  iconflag = content2PersonNode(ldb,node, graph, optionMap);
			  } else {
				  iconflag = content2OrgNode(ldb, node,graph,expandflag,shrinkicon,optionMap,false,allflag);
			  }
			  
			  graph.createEdge(rootNode, node);
			  
			  if(expandflag && iconflag){
				  optionMap.put("code", ldb.get("codeitemid").toString());
				  List nextchildList = searchInfoByCode(optionMap,conn,false,allflag);
				  expandChildNodes(node, graph, nextchildList, optionMap, expandflag, conn, shrinkicon, allflag);
			  }
			  
		  }
		
		
		
	}
	
	
	
	public NodeLabel createNodeLabel(NodeRealizer nr){
		NodeLabel label = nr.createNodeLabel();
		label.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_SIZE);
		label.setConfiguration("CroppingLabel");
		label.setVisible(false);
		return label;
	}
	
	public static String getBusi_org_dept(UserView userView) {
		String busi = "";
				String busi_org_dept = "";
				try {
					
					busi_org_dept = userView.getUnitIdByBusi("4");
					if (busi_org_dept.length() > 0) {
						busi = PubFunc.getTopOrgDept(busi_org_dept);
					}else{
						busi=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
					}
				} catch (Exception e) {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`
					e.printStackTrace();
				}
		return busi;
	}
	
	/**
	 * 获取图片
	 * @param a0100
	 * @param optionMap
	 * @return
	 */
	public byte[] getPhote(String a0100,String dbnames){
		String sql = "select fileid from "+dbnames+"A00 where a0100='"+a0100+"' and flag='P'";
		Connection conn = null;
		RowSet rs = null;
		byte[] buf = null;
		InputStream in = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()) {
				String fileid = rs.getString("fileid");
				if(StringUtils.isNotEmpty(fileid)) {
                    in = VfsService.getFile(fileid);
                }
			}
			
			if(in!=null){
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		        byte[] buff = new byte[100];
		        int rc = 0;
		        while ((rc = in.read(buff, 0, 100)) > 0) {
		            byteArrayOutputStream.write(buff, 0, rc);
		        }
		        buf = byteArrayOutputStream.toByteArray();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(rs);
			PubFunc.closeIoResource(conn);
		}
		return buf;
	}


	/**
	 * 岗位汇报关系图 查询
	 * @param optionMap 参数集合
	 * @param conn 
	 * @param userView 
	 * @param isRoot 是否是根节点
	 * @return
	 */
	public List searchPosByCode(HashMap optionMap,Connection conn,UserView userView,boolean isRoot){
		
		List infoList = new ArrayList();
                StringBuffer sqlstr = new StringBuffer();
    	try{
		    	String constant = (String)optionMap.get("constant");
		    	String backdate = (String)optionMap.get("backdate");
		    	String code = (String)optionMap.get("code");
		    	
		    	//取直接上级字段名(系统参数,可配)
		    	String superiorFld = "";
    			RecordVo ps_superior_vo=ConstantParamter.getRealConstantVo("PS_SUPERIOR",conn);
                if(ps_superior_vo!=null){
                    superiorFld=ps_superior_vo.getString("str_value");
                }
                if(StringUtils.isBlank(superiorFld)){
                	return infoList;
                }
		    	StringBuffer posWhereSql = new StringBuffer();
		    	posWhereSql.append(" select K01.E01A1  from K01 K01, organization org ");
		    	if(code.length()>0){
		    		if(isRoot){
		    			posWhereSql.setLength(0);
		    			posWhereSql.append("'"+code+"'");
		    		}else{
		    		    posWhereSql.append(" where K01.E01A1 = org.CodeItemId  and K01."+superiorFld+" like '"+code+"'  and "+userView.getUnitPosWhereByPriv("org.CodeItemId"));
		    		}
		    	}else{
		    		
		    		if(Sql_switcher.searchDbServer()!=Constant.ORACEL){
		    			posWhereSql.setLength(0);
		    			posWhereSql.append(" select  top(50) K01.E01A1  from K01 K01, organization org ");
		    		}
		    		if(userView.isSuper_admin())
					{
						if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                            posWhereSql.append(" where K01.E01A1 = org.CodeItemId  and K01."+constant+" is null ");
                        } else {
                            posWhereSql.append(" where K01.E01A1 = org.CodeItemId  and K01."+constant+" like '' ");
                        }
					}else{
						
						posWhereSql.append(" where K01.E01A1 = org.CodeItemId  and "+Sql_switcher.isnull("K01."+constant,"-1")+"  not in(select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and "+userView.getUnitPosWhereByPriv("org.CodeItemId")+") and "+userView.getUnitPosWhereByPriv("org.CodeItemId"));
//						if(dbserver.equalsIgnoreCase("oracle"))	
//						{
//							posWhereSql.append(" where K01.E01A1 = org.CodeItemId  and K01."+constant+"  not in(select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and "+userView.getUnitPosWhereByPriv("org.CodeItemId")+") and "+userView.getUnitPosWhereByPriv("org.CodeItemId"));							
//						}
//						else
//						{
//							posWhereSql.append(" where K01.E01A1 = org.CodeItemId  and K01."+constant+"  not in(select codeitemid from K01 K01, organization org where K01.E01A1 = org.CodeItemId and "+userView.getUnitPosWhereByPriv("org.CodeItemId")+") and "+userView.getUnitPosWhereByPriv("org.CodeItemId"));
//						}
					}
					posWhereSql.append(" and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date ");
		    		posWhereSql.append(" order by K01.E01A1 ");
		    		if(Sql_switcher.searchDbServer()==Constant.ORACEL){
		    			String sql = "select E01A1 from ("+posWhereSql.toString()+") where rownum<51 ";
		    			posWhereSql.setLength(0);
		    			posWhereSql.append(sql);
		    		}
		    	}
 
		    	sqlstr.append(" select organization.codesetid,organization.codeitemid,organization.codeitemdesc  as codeitemdesc,");
			sqlstr.append(" (select count(1) from k01 inner join organization org on k01.E01A1 =org.codeitemid  where ").append(superiorFld).append("=organization.codeitemid ");
			sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between org.start_date and org.end_date ");
			sqlstr.append(") as nextorgnum,0 as personnum,");
		    	sqlstr.append(" 'org' as infokind from organization where organization.codeitemid in (");
		    	sqlstr.append(posWhereSql);
		    	//39239 鄂尔多斯市君正能源化工有限公司；已撤消或删除岗位在汇报表关系里还出现 因此放开对撤销日期的控制
		    	sqlstr.append(" ) and "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
		    	sqlstr.append(" GROUP BY organization.codesetid,organization.A0000,organization.codeitemid,organization.codeitemdesc,organization.parentid,organization.childid, organization.grade order by organization.codeitemid,organization.A0000");
		    	
		    	infoList = ExecuteSQL.executeMyQuery(sqlstr.toString(),conn);
		    	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		    	
		
		
		return infoList;
	}
	
	
	/**
	 * 岗位汇报关系图  节点加载
	 * @param rootNode  父节点
	 * @param graph
	 * @param childList 子节点list
	 * @param optionMap 参数集合
	 * @param expandflag  展开方式（true 展开所有下级 ）
	 * @param conn 
	 * @param shrinkicon 
	 */
	public void expandPosChildNodes(Node rootNode,Graph2D graph,List childList,HashMap optionMap,boolean expandflag,UserView userView,Connection conn,ImageIcon shrinkicon,String allflag){
		
		if(!"true".equals(optionMap.get("graphaspect"))) {
            Collections.reverse(childList);
        }
		
		  for(int i=0;i<childList.size();i++){
			  LazyDynaBean ldb = (LazyDynaBean)childList.get(i);
			  Node node = graph.createNode();
			  
			  boolean iconflag = true;
//			  
				  iconflag = content2OrgNode(ldb, node,graph,expandflag,shrinkicon,optionMap,true,allflag);
			  
			  graph.createEdge(rootNode, node);
			  
			  if(expandflag && iconflag){
				  optionMap.put("code", ldb.get("codeitemid").toString());
				  List nextchildList = searchPosByCode(optionMap,conn,userView,false);
				  expandPosChildNodes(node, graph, nextchildList,optionMap,expandflag,userView,conn,shrinkicon,allflag);
			  }
			  
		  }
		
		
		
		
	}
	
	
	/**
	 * 获取 机构 的显示格式信息
	 * @param code codeitemid
	 * @param graphaspect 是否垂直显示
	 * @param isshowpos 是否显示岗位
	 * @param conn 
	 * @return
	 */
	public HashMap getOrgShowInfo(HashMap optionMap,String userName,Connection conn,boolean isPosRelation){
		
		
		String tablename = "";
		
		if(isPosRelation){
			tablename = createPosData(conn, optionMap,userName);
		}else{
			tablename = createOrgData(conn, optionMap,userName);
		}
		
		////////////////////////////////分开
		
		boolean vertical = false;  
		String code = optionMap.get("code").toString();
		
		//是否垂直显示
		if("true".equals(optionMap.get("graphaspect"))) {
            vertical = true;
        }
		
		String dataWhere = " where  ";
		if(!isPosRelation){
			//是否显示岗位
			if(!"true".equals(optionMap.get("isshowposname"))) {
                dataWhere +=" codesetid <> '@K' and ";
            }
			//机构过滤
			if(!"".equals(code) && !"00".equals(code)) {
                dataWhere += " codeitemid like '"+code+"%' and ";
            }
		}
		
		dataWhere+=" 1=1 ";
		
		
		String ceiling = "ceiling";
		try {
			if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                ceiling = " ceil";
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		StringBuffer dataSqls = new StringBuffer();
		if(vertical){
			// 向下
			/**
			 * 向下展开逻辑
			 * --如果又是根基节点又是叶子节点，垂直
			 * (select codeitemid, case when (TOPORG=1 and LEAFORG=1 and BoCount>=5) then 'C'  
			 * --如果本级个数大于5，并且本级个数大于下级个数，垂直
			 * when (BoCount>=5 and BoCount>=ISNULL(nextcount,0)) then 'C' 
			 * --如果本级个数大于等于个，垂直
			 * when (BOCOUNT>=8) then 'C'  
			 * --其他情况（本级个数小于或者（>本级个数>=5 并且本级个数小于等于下级个数））都是水平
			 * else 'S' end AS direction 
			 */
			dataSqls.append(" select VO.codeitemid, codeitemdesc, grade, FX.direction, DX.textlength, vflag, ");
			dataSqls.append(" case when FX.direction='C' then case when DX.maxlen <= 8  then DX.maxlen else 8 end ");
			dataSqls.append(" when FX.direction='S' then case when maxlen >20 and DX.textlength<= 20 then 2 when maxlen <=20 then "+ceiling+"(maxlen/10.0) else "+ceiling+"(DX.textlength/10.0) end end AS height, ");
			dataSqls.append(" case when FX.direction='C' then case when DX.maxlen <= 16  then "+ceiling+"(maxlen/8.0)  when DX.maxlen > 16 and DX.textlength <= 16 then 2 else "+ceiling+"(maxlen/8.0) end ");
			dataSqls.append(" when FX.direction='S' then  case when maxlen >10 then 10 else maxlen end end AS width	 ");
			dataSqls.append(" from "+tablename+" VO, ");
			////
			dataSqls.append(" (select codeitemid, "+Sql_switcher.length("codeitemdesc")+" as textlength, ");
			dataSqls.append(" (select xilen from (select grade AS AGrade, floor(avg("+Sql_switcher.length("codeitemdesc")+")/0.8) as xilen ");
			dataSqls.append(" from "+tablename+" "+dataWhere+" group by grade ) A where grade=AGrade) as xilen, ");
			dataSqls.append(" (select maxlen from (select grade AS AGrade, max("+Sql_switcher.length("codeitemdesc")+") as maxlen ");
			dataSqls.append(" from "+tablename+" "+dataWhere+" group by grade ) A where grade=AGrade) as maxlen ");
			dataSqls.append(" from "+tablename+" "+dataWhere+" ) DX,");
			////
			dataSqls.append(" (select codeitemid, case when (TOPORG=1 and LEAFORG=1 and BoCount>=5) then 'C' ");
			dataSqls.append(" when (BoCount>=5 and BoCount>="+Sql_switcher.isnull("nextcount", "0")+") then 'C' when (BOCOUNT>=8) then 'C' ");
			dataSqls.append(" else 'S' end AS direction from ( ");
			   dataSqls.append(" select codeitemid,case when codeitemid=parentid then 1 else 0 end as TOPORG,");
			   dataSqls.append(" case when codeitemid in (select parentid from "+tablename+" "+dataWhere+"  group by parentid) then 0 else 1 end as LEAFORG, ");
			   dataSqls.append(" (select aa from (select grade AS AGrade,count(1) as aa from "+tablename+" "+dataWhere+"  group by grade) A where grade=AGrade) as BOCOUNT, ");
			   dataSqls.append(" (select aa from (select grade AS AGrade,count(1) as aa from "+tablename+" "+dataWhere+"  group by grade) A where grade=AGrade-1) as NEXTCOUNT ");
			   dataSqls.append(" from "+tablename+" "+dataWhere);
			dataSqls.append(" ) T ");
			dataSqls.append(" ) FX  ");
			/////
			dataSqls.append(" where  VO.codeitemid=DX.codeitemid and VO.codeitemid=FX.codeitemid order by VO.codeitemid ");
			
		}else{
			// 向右
			dataSqls.append(" select VO.codeitemid, codeitemdesc, grade, FX.direction, DX.textlength, ");
			dataSqls.append(" case when FX.direction='C' then case when DX.maxlen <= 8  then DX.maxlen else 8 end ");
			dataSqls.append(" when FX.direction='S' then case when maxlen >20 and DX.textlength <= 20 then 2 when maxlen <=20 then "+ceiling+"(maxlen/10.0) else "+ceiling+"(DX.textlength/10.0) end end AS height,");
			dataSqls.append(" case when FX.direction='C' then case when DX.maxlen <= 16  then "+ceiling+"(maxlen/8.0)  when DX.maxlen > 16 and DX.textlength <= 16 then 2 else "+ceiling+"(maxlen/8.0) end ");
			dataSqls.append(" when FX.direction='S' then case when maxlen >10 then 10 else maxlen end end AS width ");
			dataSqls.append(" from "+tablename+" VO,");
			///
			dataSqls.append(" (select codeitemid, "+Sql_switcher.length("codeitemdesc")+" as textlength, ");
			dataSqls.append(" (select xilen from (select grade AS AGrade, floor(avg("+Sql_switcher.length("codeitemdesc")+")/0.8) as xilen ");
			dataSqls.append(" from "+tablename+" "+dataWhere+" group by grade ) A where grade=AGrade) as xilen,");
			dataSqls.append(" (select maxlen from (select grade AS AGrade, max("+Sql_switcher.length("codeitemdesc")+") as maxlen ");
			dataSqls.append(" from "+tablename+" "+dataWhere+" group by grade ) A where grade=AGrade) as maxlen  ");
			dataSqls.append(" from "+tablename+" "+dataWhere+" ) DX,  ");
			/// 2014-6-3, WJH 向右暂改为全水平
			// dataSqls.append(" (select codeitemid, case when (gNum<=2) then 'S' when (BoCount*3<=levelmax) then 'C' else 'S' end AS direction  ");
			dataSqls.append(" (select codeitemid, 'S' AS direction  ");
			dataSqls.append(" from (  ");
				dataSqls.append(" select codeitemid,  case when codeitemid=parentid then 1 else 0 end as TOPORG,  ");
				dataSqls.append(" case when codeitemid in (select parentid from "+tablename+" "+dataWhere+"  group by parentid) then 0 else 1 end as LEAFORG,  ");
				dataSqls.append(" grade as GRADE, vflag,  ");
				dataSqls.append(" (select max(grade)-min(grade) as GNum from "+tablename+" "+dataWhere+") as gNum,  ");
				dataSqls.append(" (select aa from (select grade AS AGrade,count(1) as aa from "+tablename+" "+dataWhere+" group by grade) A where grade=AGrade) as BOCOUNT,  ");
				dataSqls.append(" (select max(aa) from (select grade AS AGrade,count(1) as aa from "+tablename+" "+dataWhere+" group by grade) A ) as LevelMax  ");
				dataSqls.append(" from "+tablename+" "+dataWhere);
				dataSqls.append(" ) T  ");
			dataSqls.append(" ) FX ");
			///
			dataSqls.append(" where  VO.codeitemid=DX.codeitemid and VO.codeitemid=FX.codeitemid order by VO.codeitemid  ");
			
		}
		
		// 字段：direction 节点显示方向（'C'垂直，'S'水平） / textlength 机构名长度  / width 节点字符宽度   / height  节点高度     // width和height是用来计算节点的场合宽的，这里不是准确的数值
		List dataList = ExecuteSQL.executeMyQuery(dataSqls.toString(),conn);
		
		for(int i=0;i<dataList.size();i++){
			LazyDynaBean ldb = (LazyDynaBean)dataList.get(i);
			String codeitemidKey = ldb.get("codeitemid").toString();
			orgInfoMap.put(codeitemidKey, ldb);
		}
		
		
		//岗位汇报关系图        根节点重新设置一下节点大小，不使用查询出的数据
		if(code.length()>0 && isPosRelation){
			LazyDynaBean ldb = (LazyDynaBean)orgInfoMap.get(code);
			//字符长度
			int len = Integer.parseInt(ldb.get("textlength").toString());
			
			//可分成多少段（字数太多要分段换行）
			int col = 1;
			
			if(len>8){
				col = len/8;
				if(len%8>0) {
                    col++;
                }
				
				len = 8;
			}
			if(vertical){
				ldb.set("height", len+"");
				ldb.set("width",col+"");
			}else{
				ldb.set("width", len+"");
				ldb.set("height",col+"");
			}
			orgInfoMap.put(code, ldb);
		}
		
		return orgInfoMap;
	}
	
	
	private String createOrgData(Connection conn,HashMap optionMap,String username){
		
		String tablename = username+"_YFILES_ORGINFO";
		StringBuffer viewSql = new StringBuffer();
		
		ContentDAO dao = new ContentDAO(conn);
		try{
			if(Sql_switcher.searchDbServer() == Constant.ORACEL) {
                viewSql.append("SELECT VIEW_NAME FROM USER_VIEWS WHERE VIEW_NAME =  '"+tablename.toUpperCase()+"'");
            } else {
                viewSql.append(" SELECT NAME FROM SYSOBJECTS WHERE NAME='"+tablename+"' ");
            }
				//viewSql.append(" SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME = '"+tablename+"' ");
			List isexsit = ExecuteSQL.executeMyQuery(viewSql.toString(),conn);
			if(isexsit.size()>0){
				viewSql.setLength(0);
				viewSql.append(" DROP VIEW "+tablename);
				dao.update(viewSql.toString());
			}
				
			viewSql.setLength(0);
			viewSql.append(" create view "+tablename+" as ");
			viewSql.append(" select codesetid, codeitemid, codeitemdesc, parentid, childid, grade, a0000, layer, corcode, 0 as vflag from organization");
			viewSql.append("  union select codesetid, codeitemid, codeitemdesc, parentid, childid, grade, a0000, layer, corcode, 1 as vflag from vorganization");
			
	        if("true".equals(optionMap.get("ishistory"))){
	        	String catalog_id = optionMap.get("catalog_id").toString();
				viewSql.setLength(0);
				viewSql.append("CREATE VIEW "+tablename+" as");
				viewSql.append(" select codesetid, codeitemid, codeitemdesc, parentid, childid, grade,'1' as vflag from hr_org_history where catalog_id='"+catalog_id+"'");
			}
	        
	        dao.update(viewSql.toString());
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		return tablename;
	}
	
	private String createPosData(Connection conn,HashMap optionMap,String username){
		
		String tablename = username+"_YFILES_POSDATA";
		try{
			DbWizard dbw = new DbWizard(conn);
			String constant = (String)optionMap.get("constant");
			String backdate=(String)optionMap.get("backdate");

			if(dbw.isExistTable(tablename, false)) {
                dbw.dropTable(tablename);
            }
			
			StringBuffer viewSql = new StringBuffer();
			if(Sql_switcher.searchDbServer() == Constant.ORACEL){
				viewSql.append(" create table "+tablename+" as(");
				viewSql.append(" select codesetid, codeitemid,codeitemdesc,1 as grade,K01."+constant+" as parentid,'0' as childid,'0' as vflag from organization inner join K01 on K01.E01A1=organization.codeitemid");
				if(StringUtils.isNotBlank(backdate)){
					viewSql.append(" where "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
				}
				viewSql.append(" )");
			}else{
				viewSql.append(" select codesetid, codeitemid,codeitemdesc,1 as grade,K01."+constant+" as parentid,'0' as childid,'0' as vflag into ");
				viewSql.append(tablename+" from organization inner join K01 on K01.E01A1=organization.codeitemid");
				if(StringUtils.isNotBlank(backdate)){
					viewSql.append(" where "+Sql_switcher.dateValue(backdate)+" between organization.start_date and organization.end_date ");
				}
			}

			
			ContentDAO dao = new ContentDAO(conn);
			dao.update(viewSql.toString());
			
			int i=1;
			viewSql.setLength(0);
			int grade=2;
			while(i>0 && grade <20){
				viewSql.setLength(0);
				viewSql.append(" update "+tablename+" set grade= "+grade+" where parentid in (select codeitemid from "+tablename+" where grade="+(grade-1)+")   ");
				i = dao.update(viewSql.toString());
				grade++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return tablename;
	}
	/**
	 * 生成 node
	 * @param nodeInfo 机构的对象
	 * @param graph 
	 * @param optionMap 参数
	 * @return
	 */
	public boolean content2OrgNode(LazyDynaBean nodeInfo,Node sourceNode,Graph2D graph,boolean expandflag,ImageIcon shrinkicon, HashMap optionMap,boolean isPosRelation,String allflag){
		
		NodeRealizer nr = graph.getRealizer(sourceNode);
		
		  //隐藏label保存节点信息  .   label的index 为2
		  NodeLabel infolabel = createNodeLabel(nr);
		  infolabel.setText(nodeInfo.get("codesetid").toString()+nodeInfo.get("codeitemid"));
		  nr.addLabel(infolabel);
		
		
		
		boolean treeVertical = false; //整体展开方向 true垂直，false水平
		if("true".equals(optionMap.get("graphaspect"))) {
            treeVertical = true;
        }
		
		
		boolean vertical = true; //节点垂直显示 true垂直，false水平
		int rownum = 1; //节点行数
		int colnum = 1;//节点列数
		
		int wordSplitLength = 10;//换行长度
		LazyDynaBean orgInfo = null;
		
		 if(orgInfoMap.containsKey(nodeInfo.get("codeitemid"))){
			 orgInfo = (LazyDynaBean)orgInfoMap.get(nodeInfo.get("codeitemid"));
			 String expandType = (String)orgInfo.get("direction");
			 
			 if("S".equals(expandType)) {
                 vertical = false;
             }
			 
			 // 获取显示高宽。height为2.00时，无法直接转换成int，需要通过double类型转化 。 chent 20171123 modify
			 rownum = (int)Double.parseDouble((String)orgInfo.get("height"))==0?1:(int)Double.parseDouble((String)orgInfo.get("height"));
			 colnum = (int)Double.parseDouble((String)orgInfo.get("width"))==0?1:(int)Double.parseDouble((String)orgInfo.get("width"));
			 if(vertical) {
                 wordSplitLength = rownum;
             } else {
                 wordSplitLength = colnum;
             }
			 
			 if(this.posConvert && treeVertical && vertical){
				 vertical = false;
				 // 获取显示高宽。height为2.00时，无法直接转换成int，需要通过double类型转化 。 chent 20171123 modify
				 colnum = (int)Double.parseDouble((String)orgInfo.get("height"));
				 rownum = (int)Double.parseDouble((String)orgInfo.get("width"));
			 }
		 }else if("0".equals(nodeInfo.get("codeitemid"))){//顶级的“组织机构”节点时，要特殊处理一下 chent 20170729
			 vertical = false;
			 colnum = this.org_root_caption.length();
		 }else{
			 wordSplitLength = nodeInfo.get("codeitemdesc").toString().length();
			 vertical = !treeVertical;
			 if(vertical) {
                 rownum = wordSplitLength;
             } else {
                 colnum = wordSplitLength;
             }
		 }
		 
		 ArrayList numList = new ArrayList();
		//如果横向 展开 并且 节点 为横向时，判断是否加载 机构数和人数信息
		 String nextorgnum = nodeInfo.get("nextorgnum").toString();
		 String personnum =  nodeInfo.get("personnum").toString();
		 String posupdesc ="";
		 if(isPosRelation){
			 String posup = getUnitAndDept(nodeInfo.get("codeitemid").toString());
			 
			 
			 if("true".equals(optionMap.get("isshowposup"))){
				 String seprartor = optionMap.get("seprartor").toString();
				 posupdesc = posup.replaceAll(",",seprartor);
				 if(vertical) {
                     rownum = rownum<8?8:rownum;
                 } else {
                     colnum = colnum<8?8:colnum;
                 }
				 wordSplitLength = wordSplitLength<8?8:wordSplitLength;
			 }else{
				 if(posup.length()>0){
					 String nodeid = BaseServlet.getId(graph, sourceNode);
					 posUpInfoMap.put(nodeid, posup.substring(0, posup.length()-1));
				 }
			 }
		 }else{
			 
			 String orgnumdesc = "机构数:"+nextorgnum;//全角空格
			 String pernumdesc = "人数:"+personnum;
			 if(!vertical && !treeVertical){
				 int maxlen = 0;
				 if("true".equals(optionMap.get("isshoworgconut")) && !"@K".equals(nodeInfo.get("codesetid")) && !"0".equals(nodeInfo.get("codeitemid"))){////顶级的“组织机构”节点时，不显示机构树数。该节点是虚拟节点除非再取一次，太麻烦 chent 20170729
					 numList.add("　"+orgnumdesc);
					 maxlen = 7;
				 }
				 if("true".equals(optionMap.get("isshowpersonconut")) && !"0".equals(nodeInfo.get("codeitemid"))){//顶级的“组织机构”节点时，不显示人数。该节点是虚拟节点除非再取一次，太麻烦  chent 20170729
					 numList.add("　　"+pernumdesc);
					 if(maxlen == 0) {
                         maxlen = 6;
                     }
				 }
				 
				 if(maxlen>colnum) {
                     colnum = maxlen;
                 }
			 }
		 }
		 //截断后的 str集合
		 List infoList = new ArrayList(); 
		 
		 // 机构名称计算几行
		 String orgdesc = posupdesc+nodeInfo.get("codeitemdesc").toString().trim();
		 if(vertical){//如果信息是竖向显示，将括弧改成垂直型的
			 orgdesc = orgdesc.replaceAll("\\(", "︵");
			 orgdesc = orgdesc.replaceAll("（", "︵");
			 orgdesc = orgdesc.replaceAll("\\)", "︶");
			 orgdesc = orgdesc.replaceAll("）", "︶");
		 }
		 orgdesc = ToSBC(orgdesc);//半角转全角，因为有字母或数字时占半个字节，竖向时对不齐
		 
		 //对 字符 进行分段截取
		 int doFlag=0;
		 do{
			 String code = "";
			 
			 //如果第一个是︶或者、则从前一行拉一个字过来，美观一些  垂直显示的时候不进行判读
			 if(!vertical && (orgdesc.indexOf("︶") == 0 || orgdesc.indexOf("、")==0)){
				 String before = (String)infoList.get(doFlag-1);
				 if(StringUtils.isNotEmpty(before) && before.length() > 1) {
					 code = before.substring(before.length()-1);
					 before = before.substring(0,before.length()-1);
					 infoList.set(doFlag-1, before);
					 orgdesc = code+orgdesc;
				 }
			 }
			 //如果长度不够截取了，就跳出 
			 if(orgdesc.length()<=wordSplitLength){
				 infoList.add(orgdesc);//.toCharArray()
				 break;
			 }
			 //按 允许的长度 截取字符
			 String substrs = orgdesc.substring(0, wordSplitLength);
			 
			 //如果最后一个是左括弧（或上括弧），则放到下一行里  垂直显示的时候不进行判读
			 if(!vertical && (substrs.endsWith("︵") || substrs.endsWith("（")) && substrs.length() > 1){
				 substrs = orgdesc.substring(0, wordSplitLength-1);
				 orgdesc = orgdesc.substring(wordSplitLength-1);
			 }else {
                 orgdesc = orgdesc.substring(wordSplitLength);
             }
			 
			 infoList.add(substrs); //.toCharArray()
			 
			 doFlag++;
			 
		 }while(true);
		 
		 
		 
		 
		 //设置节点按钮状态
		 boolean iconflag = true;
		 if("0".equals(nextorgnum) && "0".equals(personnum)) {
             iconflag = false;
         } else if(!"0".equals(personnum) && "0".equals(nextorgnum) && "false".equals(optionMap.get("isshowpersonname"))) {
             iconflag = false;
         }
		 
		 //当点击 隐藏的机构时，不显示下级按钮
		  if(nodeInfo.getMap().containsKey("view_chart") && "1".equals(nodeInfo.get("view_chart"))) {
              iconflag = false;
          }
		 
		 NodeLabel iconlabel = nr.getLabel(1);
		 if(treeVertical){
		    iconlabel.setPosition(NodeLabel.BOTTOM);
		    iconlabel.setOffset(10, 10);
		 }else{
			iconlabel.setPosition(NodeLabel.RIGHT);
		 }
		 
		  if(!iconflag){
		     iconlabel.setVisible(false);
		  }
		  
		  if(iconflag && expandflag && !("all".equals(allflag)&& "true".equals(optionMap.get("isshowphoto")) && "0".equals(nodeInfo.get("nextorgnum")))) {
              iconlabel.setIcon(shrinkicon);
          }
		 
		 
		 Font textfont = (Font)optionMap.get("textfont");
		 Color textColor = (Color)optionMap.get("textcolor");
		 //向节点内添加字
		 NodeLabel textlabel = nr.createNodeLabel();
      	 textlabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_SIZE);
      	 textlabel.setAlignment(NodeLabel.TOP_TEXT_POSITION); 
		 textlabel.setFont(textfont);
		 textlabel.setTextColor(textColor);
		 
		 if(!treeVertical) {
             textlabel.setAlignment(NodeLabel.LEFT_TEXT_POSITION);
         }
		 
		 StringBuffer textstr = new StringBuffer();
		 if(vertical){
			 if(infoList.size()==1){
				 char[] chart = ((String) infoList.get(0)).toCharArray();
				 for(int b=0;b<chart.length;b++) {
                     textstr.append(chart[b]+"\n");
                 }
			 }else{
				 /**
				  * format String “一二三四五六七八九”
				  * to ：
				  * 
				  * 一六+\n
				  * 二七+\n
				  * 三八+\n
				  * 四九+\n
				  * 五+空格（全角）
				  */
				 for(int k = 0;k<wordSplitLength;k++){
					 for(int t = 0;t<infoList.size();t++){
						 char[] chart = ((String) infoList.get(t)).toCharArray();
						 if(chart.length<k+1) {
                             textstr.append("　");//此处为全角空格，做占位用
                         } else {
                             textstr.append(chart[k]);
                         }
					 }
					 textstr.append("\n");
				 }
			 }
		 }else{
			 for(int t = 0;t<infoList.size();t++){
				 char[] chart = ((String) infoList.get(t)).toCharArray();
				 textstr.append(new String(chart)+"\n"); 
			 }
			 
		 }
		 
		 textlabel.setText(textstr.toString());
		 nr.addLabel(textlabel);
		 
		 
		 
		//换行比例  加上换行间距，相当于每个字高度加大25%
		 double lineHeightAdd = 0;
		 //楷体换行间距比较小，所以改为增加5%
		 if("yahei".equals(optionMap.get("fontfamily"))) {
             lineHeightAdd = 0.2;
         }
		 double fontsize = Double.parseDouble(optionMap.get("fontsize").toString());
		 //添加机构数和人数信息
		 if(numList.size()>0){
			 NodeLabel numLabel = nr.createNodeLabel();
			 numLabel.setModel(NodeLabel.FREE);
			 numLabel.setAlignment(NodeLabel.LEFT_TEXT_POSITION);
			 String numtext = "";
			 if(!vertical){
				 numLabel.setOffset(4, infoList.size()*fontsize*(1+lineHeightAdd));
				 numtext = numList.get(0).toString();
				 if(numList.size()>1) {
                     numtext +="\n"+numList.get(1).toString();
                 }
			 }else{
				 
			 }
			 numLabel.setText(numtext);
			 numLabel.setFont(textfont);
			 numLabel.setFontSize((int)fontsize-2);
			 numLabel.setTextColor(textColor);
			 nr.addLabel(numLabel);
		 }
		 
		 
		 
		//计算节点的长宽大小
		 
		 double nodewidth = 0;
		 double nodeheight = 0;
		 
		 
		 //雅黑字体比较大
		 if("yahei".equals(optionMap.get("fontfamily"))) {
             fontsize = fontsize*1.1;
         }
		 
		 //应对斜体时字体实际宽度不字号大，每个字的宽度加5%
		 double widthFontSize = fontsize;
		 if(optionMap.get("fontstyle").toString().indexOf("italic") != -1) {
             widthFontSize= fontsize*1.05;
         }
		 
		 //要显示的总行数，要是为竖向就是总列数
		 //rownum = rownum<infoList.size()?infoList.size():rownum;
		 rownum += numList.size();
		 if(vertical){
			 colnum = colnum<infoList.size()?infoList.size():colnum;
			 if(treeVertical && colnum<2) {
                 colnum = 2;
             }
			 //计算高度，lineHeightAdd为一行字加上换行的之间的距离
			 nodeheight = rownum*fontsize*(1+lineHeightAdd)+fontsize*0.5-fontsize*lineHeightAdd;
			 nodewidth = colnum*widthFontSize+fontsize*0.5;
			 
			 if(treeVertical){
				//单元格当竖向显示，并且展开方式为垂直时，高度+18 是 加减按钮的位置
				 nodeheight+=18;
			 }
			 
			 else{
				//单元格当竖向显示，并且展开方式为水平时，宽度+12 是 padding
				 nodewidth += 12;
			 }
			 nr.setSize(nodewidth, nodeheight);
		 }else{
			 rownum = rownum<infoList.size()?infoList.size():rownum;
			 //横向 如果 行数为一行，则自动为两行
			 rownum =!treeVertical && rownum<2?2:rownum;
			 
			 nodewidth = colnum*widthFontSize+fontsize*0.5;
			 nodeheight = rownum*fontsize*(1+lineHeightAdd)+fontsize*0.25-fontsize*lineHeightAdd;
			 if(!treeVertical ){
				//单元格当水平显示，并且展开方式为水平时，宽度+18 是 加减按钮的位置
				 nodewidth += 18;
				 
				 //只有一行时，垂直居中显示
				 if(infoList.size()<2 && numList.size()<1){
					 textlabel.setModel(NodeLabel.FREE);
					 textlabel.setOffset(0,(nodeheight-fontsize)/2-fontsize/5);
				 }
			 }else
				//单元格当水平显示，并且展开方式为竖向时，高度+18 是 加减按钮的位置
             {
                 nodeheight+=18;
             }
			 nr.setSize(nodewidth, nodeheight);//设置节点大小
		 }
		  
		 return iconflag;
	}
	 
	/**
	 * 生成人员节点
	 * @param nodeInfo
	 * @param sourceNode
	 * @param graph
	 * @param optionMap
	 * @return
	 */
	private boolean content2PersonNode(LazyDynaBean nodeInfo,Node sourceNode,Graph2D graph, HashMap optionMap){
		  Color textColor = (Color)optionMap.get("textcolor");
		  Font font = (Font)optionMap.get("textfont");
		  NodeRealizer nr = graph.getRealizer(sourceNode);
		  
		  NodeLabel iconlabel = nr.getLabel(1);
	      iconlabel.setVisible(false);
	      
	      NodeLabel codeLabel = createNodeLabel(nr);
	      codeLabel.setText(nodeInfo.get("codesetid").toString()+nodeInfo.get("codeitemid").toString());
	      nr.addLabel(codeLabel);
	      if("PJ".equals(nodeInfo.get("codesetid"))){//兼职人员显示背景 chent 20170517
        	 String partcolorstr = (String)optionMap.get("partjobpersoncolor");
        	 Color partjobpersoncolor = new Color(Integer.parseInt(partcolorstr.replace("#",""),16));
        	 nr.setFillColor(partjobpersoncolor);
         }
	      if("true".equals(optionMap.get("isshowphoto"))){
		      
		      ImageIcon personphoto=null;
		      //从本地文件读取。。。。。
		      try{
		          if (VfsService.existPath()) {
		        	  byte[] ss = getPhote(nodeInfo.get("codeitemid").toString(), optionMap.get("dbnames").toString());
		        	  personphoto = new ImageIcon(ss);
		          }
		      }catch(Exception e){
		      }
		      //如果没有，从数据库里读取。。。。
		      if(personphoto==null){
                  String fileWName="";
                  try {
                      fileWName = ServletUtilities.createPhotoFile(optionMap.get("dbnames").toString() + "A00", nodeInfo.get("codeitemid").toString(), "P", null);
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
                  if(StringUtils.isNotBlank(fileWName)){
                      fileWName=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileWName;
					  try {
						  personphoto=this.getImageIcon(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator") ,fileWName);
					  } catch (IOException e) {
						  e.printStackTrace();
					  } catch (GeneralException e) {
						  e.printStackTrace();
					  }
				  }
		      }
		      //如果没有，加载默认图片
		      if(personphoto==null || personphoto.getIconWidth()<0 || personphoto.getIconHeight()<0 ) {
                  personphoto = new ImageIcon(appImagesPath + "/photo.jpg");
              }
		      
		         nr.setSize(75, 110);
		    	 personphoto.setImage(personphoto.getImage().getScaledInstance(57,76,Image.SCALE_SMOOTH));
		         NodeLabel photolabel = nr.createNodeLabel();
		         photolabel.setIcon(personphoto);
		         photolabel.setText(nodeInfo.get("codeitemdesc").toString());
		         photolabel.setTextColor(textColor);
		         photolabel.setFont(font);
		         photolabel.setFontSize(16);
		         nr.addLabel(photolabel);
		  }else{
			  
			  nr.setSize(60, 25);
			  String labelText = nodeInfo.get("codeitemdesc").toString();
			  int fontsize = Integer.parseInt(optionMap.get("fontsize").toString());
			  NodeLabel textLabel = new NodeLabel();
			  textLabel.setAutoSizePolicy(NodeLabel.AUTOSIZE_NODE_SIZE);
			  textLabel.setAlignment(NodeLabel.TOP_TEXT_POSITION); 
			  textLabel.setFont(font);
			  textLabel.setTextColor(textColor);
			  if("true".equals(optionMap.get("graphaspect"))){
				 char[] text = labelText.toCharArray();
				 labelText = "";
				 for(int k=0;k<text.length;k++){
					 labelText += text[k]+"\n";
				 }
				 
				 nr.setSize(fontsize+fontsize/2,fontsize*1.25*4);
				 textLabel.setAlignment(NodeLabel.TOP_TEXT_POSITION); 
			  }else{
				 nr.setSize(fontsize*4+fontsize/2,fontsize*1.2+fontsize*0.5);
				 textLabel.setAlignment(NodeLabel.LEFT_TEXT_POSITION); 
			  }
			  textLabel.setText(labelText);
			  nr.addLabel(textLabel);
			  
		  }
	      
		return false;
	}

	/**
	 * 获取图片 特殊处理bmp格式图片
	 * @param path
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 11:34 2018/12/28
	 */
	private ImageIcon getImageIcon(String path,String fileName) throws IOException, GeneralException {
		ImageIcon icon = null;

		if (".bmp".equalsIgnoreCase(fileName.substring(fileName.lastIndexOf(".")))) {
			try {
				BufferedImage bi = null;
				bi = ImageIO.read(new File(path+fileName));
				ImageProducer producer = bi.getSource();
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Image image = toolkit.createImage(producer);
				icon = new ImageIcon(image);
			} catch (IOException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		} else {
			icon = new ImageIcon(path+ fileName);
		}
		return icon;

	}
	
	public static String getUnitAndDept(String posId){
		
		String e0122desc = "";
		String b0110desc = "";
		for(int i=posId.length()-1;i>0;i--){
			String subcode = posId.substring(0,i);
			e0122desc = AdminCode.getCodeName("UM", subcode);
			//如果找到部门，接着找单位
			if(e0122desc.length()>0){
				e0122desc+=",";
				for(int k=subcode.length()-1;k>0;k--){
					String upsubcode = subcode.substring(0,k);
					b0110desc = AdminCode.getCodeName("UN", upsubcode);
					//找到单位跳出循环
					if(b0110desc.length()>0){
						b0110desc+=",";
						break;
					}
				}
				//跳出循环
				break;
			}else{//如果找不到部门，有可能直接挂在单位下
				b0110desc = AdminCode.getCodeName("UN", subcode);
				//找到单位，跳出循环
				if(b0110desc.length()>0){
					b0110desc+=",";
					break;
				}
			}
			
		}
		return b0110desc+e0122desc;
	}
	
    public ImageIcon loadIcon(HttpServletRequest req, String iconName, int scaleWidth, int scaleHeight) {
        ImageIcon icon = null;
        try {
            if (null == appImagesPath) { 
                appImagesPath = req.getSession().getServletContext().getRealPath("/images");
                if ("weblogic".equals(SystemConfig.getPropertyValue("webserver"))) {
                    appImagesPath = req.getSession().getServletContext().getResource("/images").getPath();//.substring(0);
                    if (appImagesPath.indexOf(':') != -1) {
                        appImagesPath = appImagesPath.substring(1);
                    } else {
                        appImagesPath = appImagesPath.substring(0);
                    }
                    int nlen = appImagesPath.length();
                    StringBuffer buf = new StringBuffer();
                    buf.append(appImagesPath);
                    buf.setLength(nlen - 1);
                    appImagesPath = buf.toString();
                }
        
                //imageIcon要求路径必须是反斜杠分隔
                appImagesPath = appImagesPath.replaceAll("\\\\", "/");
            }
            
            icon = new ImageIcon(appImagesPath + "/" + iconName);
            icon.setImage(icon.getImage().getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return icon;
    }
	
    
    /**
     * 下载机构图，生成jpg图片
     * @param res
     * @param graph
     */
    public static void outPutGraph2Jpeg(HttpServletResponse res,Graph2D graph,String fileName){
    	FileOutputStream fout = null;
    	try{
	    	JPGIOHandler jPGIOHandler = new JPGIOHandler();
	    	jPGIOHandler.setAntialiasingEnabled(true);
	    	res.setHeader("Content-Disposition", "attachment;filename="+fileName);
	    	res.setContentType("image/jpeg");
	    	ServletOutputStream servletOutputStream = res.getOutputStream();
//	    	jPGIOHandler.write(graph, servletOutputStream);
	    	fout = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
	    	jPGIOHandler.write(graph, fout);
	    	fout.flush();
    	}catch(Exception e){
    		e.printStackTrace();
    	} finally {
			PubFunc.closeResource(fout);
		}
    }
    
    
    /**
     * 半角转全角字符   
     * @param input
     * @return
     */
    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
          if (c[i] == ' ') {
            c[i] = '\u3000';
          } else if (c[i] < '\177') {
            c[i] = (char) (c[i] + 65248);

          }
        }
        return new String(c);
    }


    
    
	/**
	 * 关闭数据库工具  支持关闭类型：Connection、RowSet、ResultSet
	 * @param obj,需要关闭的对象
	 */
	public static void closeDBTool(Object obj){
		
		if(obj == null) {
            return;
        }
		
		try{
				if(obj instanceof Connection) {
                    ((Connection) obj).close();
                } else if(obj instanceof RowSet) {
                    ((RowSet) obj).close();
                } else if(obj instanceof ResultSet) {
                    ((ResultSet) obj).close();
                }
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setOrgInfoMap(HashMap orgInfoMap) {
		this.orgInfoMap = orgInfoMap;
	}

	public boolean isPosConvert() {
		return posConvert;
	}

	public void setPosConvert(boolean posConvert) {
		this.posConvert = posConvert;
	}

	public HashMap getPosUpInfoMap() {
		return posUpInfoMap;
	}

	public void setPosUpInfoMap(HashMap posUpInfoMap) {
		this.posUpInfoMap = posUpInfoMap;
	}
	
}
