package com.hjsj.hrms.businessobject.ykcard;

import com.hjsj.hrms.businessobject.performance.statistic.StatisticPlan;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.ykcard.RGridView;
import com.hjsj.hrms.valueobject.ykcard.RPageView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.sql.RowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

public class YkcardExcel {
	private String name;               /*执行该标签所在的form*/
	private String property;           /*所在form中的标签参数的View*/
	private String scope;              /*session page*/
	private String disting_pt="1024";  /*分辨率800*600或者1024*768*/
	private String nid;                /*人员ID*/
	private String queryflag;          /*0代表安条件查询1代表安月时间查询2代表安时间段查询3.安时间季度查询*/
	private String userbase;           /*人员库*/
	private int cyear;                 /*年*/               
	private int cmonth;                /*月*/
	private int tabid;                 /*登记表id*/
	private int pageid;                /*登记表页id*/	
	private String cardtype;           /*登记表类型常量表里设置的常量*/
	private String userpriv;           /*用户权限*/
	private String istype;             /*0表示薪酬表。2表示机构1表示职位表3登记表*/
	private String havepriv;           /*是否有权限*/
	private int season;
	private int cyyear;
	private int cymonth;
	private int csyear;
	private int cdyear;
	private int cdmonth;
	private int cmmonth;
	private int cmyear;
	private int ctimes;
	private String cdatestart;
	private String cdateend;
	private int  queryflagtype=1;      /*统计方式只要不是月都是累加并且非数字的不显示*/
	private String infokind;           /*1人员登记表2单位登记表4职位登记表5计划登记表*/
	private String plan_id;    //活动计划编号
	private String b0110;
	private String nbase;
	private String fieldpurv;	
	int cyearcurrent=Calendar.getInstance().get(Calendar.YEAR)-2;
	int countyear=6;
	int degreecurrent=2;
	private int left_blank=40;
	private Connection conn;
	private UserView userView;
	private  List pageDateList=new ArrayList();
	private float top_pix=0;
	private float bottom_pix=0;
	private HashMap row_rgrid_map=new HashMap();//行信息
	private HashMap cel_rgrid_map=new HashMap();//列信息
	private HashMap row_rpage_map=new HashMap();//行信息
	private HashMap cel_rpage_map=new HashMap();//列信息
	private int rowLayNum=1;
	private int colLayNum=1;
	/** 外框 max x */
	private float tab_width=0;
	/** 外框 max y */
	private float tab_hieght=0;
	/** 外框 min x */
    private float tab_min_x=0;
    /** 外框 min y */
    private float tab_min_y=0;
	
	private HSSFWorkbook wb=null;
	private HSSFSheet sheet=null;
	private HSSFCellStyle style=null;	
	private HSSFCellStyle style_l=null;
	private HSSFCellStyle style_r=null;
	private HSSFCellStyle style_cc=null;
	private  float scale = 1f;
	private int topParamLayNum=0;					   // 表头 标题层数
	private HashMap topLayMap=new HashMap();
	private int bottomParamLayNum=0;				   // 表尾 标题层数
	private HashMap botLayMap=new HashMap();
	private HashMap allParamMap=new HashMap();		    //所有参数值
	private HashMap rPageToRgridMap=new HashMap();
	private HashMap centerMap=new HashMap();//除去表头和表尾层数
	private String fenlei_type="";//分类设置
	private ArrayList fontTypeList=null;
	
	
	/***
	 * 查询需要导出的人员或单位或岗位名称
	 * */
	public String  searchExportName(String nid,String dbname){
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		String sql="";
		String exportName="";
		if(nid==null||"".equals(nid)) {
            return null;
        }
		try {
			if("1".equals(infokind)){//人员
	    		sql="select A0101 from "+dbname+"A01 where A0100='"+nid+"'";
	    	}else if("2".equals(infokind)){//单位 UN   UM部门
	    		if(this.userView.getStatus()==4)
	            {
		    		sql="select organization.codeitemdesc from t_sys_result,organization where organization.codeitemid=t_sys_result.obj_id and t_sys_result.flag=1" +
		    				"  and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"' and t_sys_result.obj_id='"+nid+"'";
	            }else
	            {
	            	sql="select organization.codeitemdesc from "+this.userView.getUserName()+"BResult," +
	            			"organization where organization.codeitemid="+this.userView.getUserName()+"BResult.b0110 and "+this.userView.getUserName()+"BResult.b0110='"+nid+"'";
	            }        			
	    	}else if("4".equals(infokind)){//岗位 @K
	    		if(this.userView.getStatus()==4)
	            {
		    		sql="select organization.codeitemdesc from " +
		    				"t_sys_result,organization where organization.codeitemid=t_sys_result.obj_id and " +
		    				"t_sys_result.flag=2  and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"' and t_sys_result.obj_id='"+nid+"'";
	            }else
	            {
	            	sql="select organization.codeitemdesc " +
	            			"from "+this.userView.getUserName()+"KResult,organization" +
	            			" where organization.codeitemid="+this.userView.getUserName()+"KResult.E01A1 and "+this.userView.getUserName()+"KResult.E01A1='"+nid+"'";
	            }
	    	}else if("6".equals(infokind)){//基准岗位
	    		String codeset=new CardConstantSet().getStdPosCodeSetId();
	    		sql="select codeitemdesc from t_sys_result,CodeItem "+
	    				   " where CodeItem.codeitemid=t_sys_result.obj_id and codesetid='"+codeset+"'"+
	    				        " and t_sys_result.flag=5"+// 基准岗位
	    				        " and UPPER(t_sys_result.username)='"+userView.getUserName().toUpperCase()+"' and t_sys_result.obj_id= '"+nid+"'";
	    	}
			rs=dao.search(sql);
			while(rs.next()){
				exportName=rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exportName;
	}
	
	
	/***
	 * 生成excel文件 如果 font=this.wb.createFont(); 调用过多会导致打开excel报 超出最多允许的字体数
	 * 先查出所有字体创建缓存字体 减少重复创建字体
	 * */
	public ArrayList getFontTypeList(HSSFWorkbook wb,int tabid){
		ArrayList fontlist=new ArrayList();
		ArrayList tabidList=new ArrayList();
		tabidList.add(tabid);
			RowSet rs=null;
		try {
		ContentDAO dao=new ContentDAO(this.conn);
		rs=dao.search("select distinct Fontsize,Fontname,Fonteffect  from RGrid where Tabid=?", tabidList);
			while(rs.next()){
				HSSFFont font=wb.createFont();
				int fontsize=rs.getInt("Fontsize");
				String fontname=rs.getString("Fontname");
				int fonteffect=rs.getInt("Fonteffect");
				if(fontname==null||fontname.trim().length()==0){
					font.setFontName("宋体");
					font.setFontHeightInPoints((short)10);
				}else{
					font.setFontName(fontname);
					font.setFontHeightInPoints((short)fontsize);
				}
				if(fonteffect==2)
				 {
					 font.setBold(true);
				 }
				 else if(fonteffect==3)
				 {
					 font.setItalic(true);
				 }
				 else if(fonteffect==4)
				 {
					 font.setBold(true);
					 font.setItalic(true);
				 }
				fontlist.add(font);
			}
			rs=null;
			rs=dao.search("select distinct Fontsize,Fontname,Fonteffect  from RPage where Tabid=?", tabidList);
			while(rs.next()){

				HSSFFont font=wb.createFont();
				int fontsize=rs.getInt("Fontsize");
				String fontname=rs.getString("Fontname");
				int fonteffect=rs.getInt("Fonteffect");
				if(fontname==null||fontname.trim().length()==0){
					font.setFontName("宋体");
					font.setFontHeightInPoints((short)10);
				}else{
					font.setFontName(fontname);
					font.setFontHeightInPoints((short)fontsize);
				}
				if(fonteffect==2)
				 {
					font.setBold(true);
				 }
				 else if(fonteffect==3)
				 {
					 font.setItalic(true);
				 }
				 else if(fonteffect==4)
				 {
					 font.setBold(true);
					 font.setItalic(true);
				 }
				fontlist.add(font);
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return fontlist;
	}
	
	/***
	 * 根据传入参数取出font字体
	 * */
	public HSSFFont getFont(String fontName,int fontSize,int fontffect){
			HSSFFont baseaFont=null;
			for (int i = 0; i < this.fontTypeList.size(); i++) {
				HSSFFont font=(HSSFFont)this.fontTypeList.get(i);
				/*if(font.getFontName().equals("宋体")&&font.getFontHeightInPoints()==(short)10){
						baseaFont=font;
						return font;
				}else{*/
					if(font.getFontName().equals(fontName)&&font.getFontHeightInPoints()==(short)fontSize){
						if(fontffect==2){
						   if(font.getBold()) {
                               return font;
                           }
						}else if(fontffect==3){
							if(font.getItalic()) {
                                return font;
                            }
							
						}else if(fontffect==4){
						   if(font.getItalic()&&font.getBold()) {
                               return font;
                           }
	
						}else{
							return font;
						}
					}
				//}
			}
		return baseaFont;
	}
	
	
	
	
    public YkcardExcel(Connection conn,UserView userView,String tabid)
    {
        this.conn=conn;
        this.userView=userView;
     }    
    public String excelYkcard(int tabid,String nid,String dbname,UserView userview,String cyear,String querytype,String cmonth,String userpriv,String istype,String season,String ctimes,String cdatestart,String cdateend,String infokind,String fieldpurv)throws Exception
    {
    	this.infokind=infokind;
    	this.nid=nid;
    	this.userbase=dbname;
    	if(ctimes!=null) {
            this.ctimes=Integer.parseInt(ctimes);
        }
		if(season!=null) {
            this.season=Integer.parseInt(season);
        }
		if(cyear!=null) {
            this.cdyear=Integer.parseInt(cyear);
        }
		if(cmonth!=null) {
            this.cdmonth=Integer.parseInt(cmonth);
        }
		this.userpriv=userpriv;
		this.havepriv="";
		this.fieldpurv=fieldpurv;
		if(querytype!=null) {
            this.queryflagtype=Integer.parseInt(querytype);
        }
		if(this.queryflagtype==1){//月
			this.cyear=this.cdyear;
			this.cmmonth=this.cdmonth;
		}else if(this.queryflagtype==2){//时间段
			this.cdatestart=cdatestart;
			this.cdateend=cdateend;
		}else if(this.queryflagtype==3){//季度
			this.csyear=this.cdyear;
		}else if(this.queryflagtype==4){//年
			this.cyyear=this.cdyear;
		}
    	//tabid=9;
		 ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
	     if(this.infokind!=null&& "5".equals(this.infokind)&&this.plan_id!=null&&this.plan_id.length()>0)
	     {
	    	 StatisticPlan statisticPlan=new StatisticPlan(userview,conn);
	    	 alUsedFields=statisticPlan.khResultField(alUsedFields,this.plan_id);
	     }
    	this.tabid=tabid;
    	if(this.tabid==-1) {
            throw GeneralExceptionHandler.Handle(new GeneralException("","没有选择登记表,请选择!","",""));
        }
    	this.pageDateList=getPagecount(this.tabid,this.conn);    	
    	//liuy 2015-4-13 8687：我的信息/信息维护/子集中的登记表，生成excel，文件命名没有遵循后定的规范，不对。 begin
    	//String excel_filename="card_"+PubFunc.getStrg()+"_"+tabid+".xls";
    	String excel_filename="";
    	if("1".equals(infokind)|| "2".equals(infokind)|| "4".equals(infokind)|| "6".equals(infokind)){
    		String exportname=searchExportName(nid,dbname);
    		if(exportname!=null&&!"".equals(exportname)) {
                excel_filename=exportname+"_"+userView.getUserName()+"_card.xls";
            } else {
                excel_filename=userview.getUserName()+"_card.xls";
            }
    	}else{
    		excel_filename=userview.getUserName()+"_card.xls";
    	}
    	
    	
    	//liuy 2015-4-13 end
    	String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename;
		this.wb = new HSSFWorkbook();		
		ArrayList excelList=ykcardExcel(tabid);
		RGridView rgrid;
		String form_x="";
		String form_y="";
		String to_x="";
		String to_y="";
		String hz="";
		String align="";
		DataEncapsulation encap=new DataEncapsulation();  
		encap.setUserview(userview);
		GetCardCellValue card=new GetCardCellValue(); 
		this.fenlei_type=card.getOneFenleiYype(userview,userbase, this.nid, conn);
		StringBuffer content=new StringBuffer();
		String fontName="";
		String fontSize="";
		int fontEffect=0;
		float rheight=0;
		float rwidth=0;
		float rleft=0;
		float rtop=0;
		String title="";
		String lines[]=new String[4];
		ContentDAO dao=new ContentDAO (this.conn);		
		this.fontTypeList=getFontTypeList(this.wb,this.tabid);
		try
		{
			for(int i=0;i<excelList.size();i++)
			{
				HashMap one_hash=(HashMap)excelList.get(i);
				DynaBean rec=(DynaBean)one_hash.get("pageM");
				this.pageid=Integer.parseInt(rec.get("pageid")+"");
				title=rec.get("title").toString();		
				if(title!=null&&title.length()>0) {
                    title=title.trim();
                }
				this.sheet = wb.createSheet();
				wb.setSheetName(i,title);
				printSetup();
				HSSFPatriarch patriarch=this.sheet.createDrawingPatriarch();
				ArrayList cell_list=(ArrayList)one_hash.get("cellM");
				ArrayList rpagelist=(ArrayList)one_hash.get("rpageM"); 
				List setList=encap.GetSets(tabid,pageid,conn); 
				ArrayList cellW_list=(ArrayList)one_hash.get("cellW");			
	    		ArrayList rowH_list=(ArrayList)one_hash.get("rowH");//每行的高
	    	    int rpageT=Integer.parseInt((String)one_hash.get("rpageT"));//表头占几行
	    	    ArrayList splitSubgrids = new ArrayList();
	    	    ArrayList subgridContents = new ArrayList();
				for(int r=0;r<cell_list.size();r++)
				{
					LazyDynaBean abean=(LazyDynaBean)cell_list.get(r);
					rgrid=(RGridView)abean.get("rgrid");
					form_x=(String)abean.get("form_x");
					form_y=(String)abean.get("form_y");
					to_x=(String)abean.get("to_x");
					to_y=(String)abean.get("to_y");
					lines[0]=rgrid.getL();
					lines[1]=rgrid.getT();
					lines[2]=rgrid.getR();
					lines[3]=rgrid.getB();
					content.setLength(0);
					hz=rgrid.getCHz();
					align=rgrid.getAlign();
				    fontName=rgrid.getFontName();
				    if (fontName == null || fontName.length() == 0) {
                        fontName="宋体";
                    }
					fontSize=rgrid.getFontsize();
					fontEffect=rgrid.getFonteffect()!=null&&rgrid.getFonteffect().length()>0?Integer.parseInt(rgrid.getFonteffect()):0;
					rheight=Float.parseFloat(rgrid.getRheight());
					rwidth=Float.parseFloat(rgrid.getRwidth());
					rleft=Float.parseFloat(rgrid.getRleft());
					rtop=Float.parseFloat(rgrid.getRtop());
					//rwidth=getSheetWidth(tabid,pageid,rleft,rtop,rwidth,dao);
				/*	if(rgrid.getGridno().equals("21"))
					  System.out.println(rgrid.getGridno()+"-----"+rgrid.getCHz()+"----"+rgrid.getFlag());*/
					if(!"C".equals(rgrid.getFlag()))
					{
						 //liuy 2015-12-17 优化登记表不显示格线的情况下，数据导出Excel垂直居中 begin
						 boolean tempflag = false;
						 if(("A".equals(rgrid.getFlag())|| "B".equals(rgrid.getFlag())|| "K".equals(rgrid.getFlag()))&& "1".equals(rgrid.getSubflag())){
							 XmlSubdomain xmlSubdomain=new XmlSubdomain(rgrid.getSub_domain());
							 xmlSubdomain.getParaAttribute();
							 if(StringUtils.isNotEmpty(xmlSubdomain.getFields())){								 
								 if(("7".equals(rgrid.getAlign())||"6".equals(rgrid.getAlign())||"8".equals(rgrid.getAlign()))&&"false".equals(xmlSubdomain.getVl())&&"false".equals(xmlSubdomain.getHl())) {
                                     tempflag = true;
                                 }
							 }
						 }//liuy 2015-12-17 end
						 if("A".equals(rgrid.getFlag())&&!"1".equals(rgrid.getSubflag()))//A人员库
						 {
							 byte nFlag=0;                                 //0表示人员库
	                         ArrayList valueList=null;
	                         if("1".equalsIgnoreCase(rgrid.getIsView()))
	                         {
	                            	 valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
	                         }
	                         else if(!setList.isEmpty()) {
                                 for(int j=0;j<setList.size();j++)
                                 {
                                   DynaBean fieldset=(DynaBean)setList.get(j);
                                   if(fieldset.get("fieldsetid").equals(rgrid.getCSetName())){

                                          valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, fieldset);
                                          break;
                                   }
                                 }
                             }
	                         if(valueList !=null &&!valueList.isEmpty())
	                         {
	                        	     for(int j=0;j<valueList.size();j++)
		                              {
		                                 if(valueList.get(j)!=null && valueList.get(j).toString() !=null){
		                                    if(j>0) {
                                                content.append("\r\n");
                                            }
		                                    content.append(valueList.get(j)!=null?valueList.get(j).toString().replaceAll("&nbsp;", " "):""); 	 	                                	
		                                  }else{
		                                	  content.append("");      
		                                   }
		                              }	                              
		                            }else{
		                            content.append("");    
		                       } 
	                         content.append(queryRpageFromRgrid(rgrid,rpagelist,encap));
	                         executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
						 }else if("A".equals(rgrid.getFlag())&& "1".equals(rgrid.getSubflag()))
	                     {
							 byte nFlag=0;  
							 content.append(viewSubclass(rgrid,conn,userview,cyyear,cymonth,this.ctimes,userbase,nid,nFlag));
							 //liuy 2015-12-17 优化登记表不显示格线的情况下，数据导出Excel垂直居中 begin
	   	                	 if(tempflag) {
                                 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),rgrid.getAlign()/*align*/,lines,fontName,fontSize,fontEffect,rwidth,rheight);
                             } else {
                                 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),rgrid.getAlign()/*align*/,lines,fontName,fontSize,fontEffect,rwidth,rheight);
                             }
	   	                	 //liuy 2015-12-17 end
	                     }else if("B".equals(rgrid.getFlag())&&!"1".equals(rgrid.getSubflag()))
	                     {
	                    	 byte nFlag=2;                                                //2表示单位库
	                         ArrayList valueList=null;
	                         if(!setList.isEmpty()) {
                                 for(int j=0;j<setList.size();j++)
                                 {
                                     DynaBean fieldset=(DynaBean)setList.get(j);
                                     if(fieldset.get("fieldsetid").equals(rgrid.getCSetName())){
                                            valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, fieldset);
                                           break;
                                     }
                                 }
                             }
	                         if(valueList !=null &&!valueList.isEmpty()){
	                         for(int j=0;j<valueList.size();j++)
	                         {
	                        	//liuy 2015-3-10 7910：单位信息维护，登记表，输出excel，插入内容的单元格输出到excel中，都多了一行回车，不对。 begin
	                         	if(j>0) {
                                    content.append("\r\n");
                                }
	                            if(valueList.get(j)!=null && valueList.get(j).toString() !=null){
	                             //获得显示字体的大小                             
	                              content.append(valueList.get(j)!=null?valueList.get(j).toString().replaceAll("&nbsp;", " "):"");  
	                              //content.append("\r\n");   
	                            }else{
	                            	content.append("");
	                            }
	                            //liuy 2015-3-10 end
	                         }
	                         }else{
	                        	content.append("");  
	                       }
	                         content.append(queryRpageFromRgrid(rgrid,rpagelist,encap));
	                         executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
	                     }else if("B".equals(rgrid.getFlag())&& "1".equals(rgrid.getSubflag()))
		                 {
	                    	 byte nFlag=2;  
	                    	 content.append(viewSubclass(rgrid,conn,userview,cyyear,cymonth,this.ctimes,userbase,nid,nFlag));
	                    	 //liuy 2015-12-17 优化登记表不显示格线的情况下，数据导出Excel垂直居中 begin
	   	                	 if(tempflag) {
                                 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),"7"/*align*/,lines,fontName,fontSize,fontEffect,rwidth,rheight);
                             } else {
                                 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),"0"/*align*/,lines,fontName,fontSize,fontEffect,rwidth,rheight);
                             }
	   	                	 //liuy 2015-12-17 end
		                 }else if("K".equals(rgrid.getFlag())&&!"1".equals(rgrid.getSubflag()))
		                 {    
		                	 byte nFlag=4;                                            //4表示岗位库
	                         ArrayList valueList=null;
	                         if(!setList.isEmpty()) {
                                 for(int j=0;j<setList.size();j++)
                                 {
                                     DynaBean fieldset=(DynaBean)setList.get(j);
                                     if(fieldset.get("fieldsetid").equals(rgrid.getCSetName())){
                                            valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, fieldset);
                                           break;
                                     }
                                 }
                             }
	                         if(valueList !=null &&!valueList.isEmpty()){
	                            for(int j=0;j<valueList.size();j++)
	                            {
	                               if(valueList.get(j)!=null && valueList.get(j).toString() !=null){
	                                 //获得显示字体的大小
	                                 content.append(valueList.get(j)!=null?valueList.get(j).toString().replaceAll("&nbsp;", " "):""); 
	                                 if(j>0) {
                                         content.append("\r\n");
                                     }
	                               }else{                                   
	                            	 content.append("");
	                               }
	                             }
	                          }else{
	                        	content.append("");    
	                        }
	                         content.append(queryRpageFromRgrid(rgrid,rpagelist,encap));
	                         //System.out.println(content.toString());
	                         executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
	                     }else if("K".equals(rgrid.getFlag())&& "1".equals(rgrid.getSubflag()))
	                     {
	                    	 byte nFlag=4;
                             content.append(viewSubclass(rgrid,conn,userview,cyyear,cymonth,this.ctimes,userbase,nid,nFlag));       
	                    	 if (canSubgridSplitRow(cell_list, rgrid, content)) {
	                    	     splitSubgrids.add(rgrid);
	                    	     subgridContents.add(content.toString());
	                    	 }
	                    	 //liuy 2015-12-17 优化登记表不显示格线的情况下，数据导出Excel垂直居中 begin
	   	                	 if(tempflag) {
                                 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),"7"/*align*/,lines,fontName,fontSize,fontEffect,rwidth,rheight);
                             } else {
                                 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),"0"/*align*/,lines,fontName,fontSize,fontEffect,rwidth,rheight);
                             }
	   	                	 //liuy 2015-12-17 end
	                     }
	                     else if("E".equals(rgrid.getFlag()))  // 基准岗位
	                     {
	                    	 byte nFlag=7;
	                    	 if("1".equals(rgrid.getSubflag())){
	                    		 content.append(viewSubclass(rgrid,conn,userview,cyyear,cymonth,this.ctimes,userbase,nid,nFlag));   	
	                    		 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),"0"/*align*/,lines,fontName,fontSize,fontEffect,rwidth,rheight);
	                    	 }
	                    	 else{
	                    		 ArrayList valueList=null;
		                         valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
			                                       
		                         if(valueList !=null &&!valueList.isEmpty()){
		                        	 for(int j=0;j<valueList.size();j++)
		                             {
		                                if(valueList.get(j)!=null && valueList.get(j).toString() !=null){
		                                  //获得显示字体的大小
		                                  content.append(valueList.get(j)!=null?valueList.get(j).toString().replaceAll("&nbsp;", " "):"");
		                                  if(j>0) {
                                              content.append("\r\n");
                                          }
		                                }else{                                   
		                             	 content.append("");
		                                }
		                              }
		                          }else{
		                        	  content.append("");
		                         }	                         
		                         executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
	                    	 }
	                     }
	                     else if("H".equals(rgrid.getFlag())){  //H表示文字说明
	                    	 content.append(queryRpageFromRgrid(rgrid,rpagelist,encap));
	                    	 if(hz !=null && hz.length()>0)
	                    	 {
	                    		 String[] a_stok=hz.split("`");	                    		 
	                    		 if(a_stok!=null||a_stok.length>0)
	 	                         {
	 	                        	for(int s=0;s<a_stok.length;s++)
	 	                        	{
	 	                        		if(s>0) {
                                            content.append("\n");//content.append("\r\n"); //liuy 2015-8-18 12114： 苏州轨道交通 ：登记表导出excel回车都是音乐符。（所有版本）
                                        }
	 	                        		content.append(a_stok[s]);	                        		
	 	                        	}
	 	                         }else
	 	                         {
	 	                        	content.append(""); 
	 	                         } 
	                    		 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
	                    	 }  
	                    	 else
	                    	 {
	                    		 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
	                    	 }
	                     }else if("J".equals(rgrid.getFlag())){
	                    	 byte nFlag=5;                                            //5表示计划库
	                         ArrayList valueList=null;
	                         valueList = getTextValue(userbase, conn, card, rgrid, userview, nFlag, valueList, null);
		                                       
	                         if(valueList !=null &&!valueList.isEmpty()){
	                        	 for(int j=0;j<valueList.size();j++)
	                             {
	                                if(valueList.get(j)!=null && valueList.get(j).toString() !=null){
	                                  //获得显示字体的大小
	                                  content.append(valueList.get(j)!=null?valueList.get(j).toString().replaceAll("&nbsp;", " "):"");
	                                  if(j>0) {
                                          content.append("\r\n");
                                      }
	                                }else{                                   
	                             	 content.append("");
	                                }
	                              }
	                          }else{
	                        	  content.append("");
	                         }	                         
	                         executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
	                     }else if("P".equals(rgrid.getFlag()))
	                     {
	                    	 ArrayList filelist=createPhotoFile(dbname+"A00",nid,"P",conn);
	    	                 if(filelist!=null && filelist.size()>0){	    	                	  
	    	                	 executeCellImage(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),filelist,rwidth,0/*rheight*/,patriarch);
	    	                	 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),"",align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
	    	                 }else{
	    	                	 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),"",align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
	 	                     }
	                     }else if("D".equals(rgrid.getFlag())){
	                    	 byte nFlag=0;                                 //0表示人员库
	                    	 ArrayList valueList=card.getTextValueForCexpress(userbase, conn, card, rgrid, userview,alUsedFields,infokind,this.nid,this.plan_id);
	                         if(valueList !=null &&!valueList.isEmpty())
	                         {
	                        	     for(int j=0;j<valueList.size();j++)
		                              {
		                                 if(valueList.get(j)!=null && valueList.get(j).toString() !=null){
		                                    if(j>0) {
                                                content.append("\r\n");
                                            }
		                                    content.append(valueList.get(j)!=null?valueList.get(j).toString():""); 	 	                                	
		                                  }else{
		                                	  content.append("");      
		                                   }
		                              }	                              
		                            }else{
		                            content.append("");    
		                       } 
	                         content.append(queryRpageFromRgrid(rgrid,rpagelist,encap));
	                         executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
						
	                     }else
					     {
					    	 executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),"",align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
					     }	
					  }else if("C".equals(rgrid.getFlag()))
					  {
						    //System.out.println(card.getFormulaValue(rgrid));
						   // System.out.println(rgrid.getCexpress());
						    //content.append(card.getFormulaValue(rgrid));
					       //executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
					  }				
				}
				for(int r=0;r<cell_list.size();r++)
				{
					LazyDynaBean abean=(LazyDynaBean)cell_list.get(r);
					rgrid=(RGridView)abean.get("rgrid");
					form_x=(String)abean.get("form_x");
					form_y=(String)abean.get("form_y");
					to_x=(String)abean.get("to_x");
					to_y=(String)abean.get("to_y");
					lines[0]=rgrid.getL();
					lines[1]=rgrid.getT();
					lines[2]=rgrid.getR();
					lines[3]=rgrid.getB();
					content.setLength(0);
					hz=rgrid.getCHz();
					align=rgrid.getAlign();
				    fontName=rgrid.getFontName();
					fontSize=rgrid.getFontsize();
					fontEffect=rgrid.getFonteffect()!=null&&rgrid.getFonteffect().length()>0?Integer.parseInt(rgrid.getFonteffect()):0;
					rheight=Float.parseFloat(rgrid.getRheight());
					rwidth=Float.parseFloat(rgrid.getRwidth());
					rleft=Float.parseFloat(rgrid.getRleft());
					rtop=Float.parseFloat(rgrid.getRtop());
					if("C".equals(rgrid.getFlag()))
					{
	                     content.append(card.getFormulaValue(rgrid));
					     executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
					}
				}
				/*标题*/
				for(int r=0;r<rpagelist.size();r++)
				{
					LazyDynaBean abean=(LazyDynaBean)rpagelist.get(r);					
					RPageView rpage=(RPageView)abean.get("rpage");
					if(this.rPageToRgridMap.get(rpage.getGridno())!=null)
					{   
						continue;
					}
					if(rpage.getFlag()!=6)
	          	    {
						form_x=(String)abean.get("form_x");
						form_y=(String)abean.get("form_y");
						to_x=(String)abean.get("to_x");
						to_y=(String)abean.get("to_y");
						content.setLength(0);
						fontSize=rpage.getFontsize();
						fontName=rpage.getFontname();
						align="7";		
						lines[0]="";
						lines[1]="";
						lines[2]="";
						lines[3]="";				
						rheight=Float.parseFloat(rpage.getRheight());
						rwidth=Float.parseFloat(rpage.getRwidth());
						rleft=Float.parseFloat(rpage.getRleft());
						rtop=Float.parseFloat(rpage.getRtop());		
						rwidth=getSheetRpageWidth(tabid,pageid,rleft,rtop,rwidth,dao);
						fontEffect=rpage.getFonteffect()!=null&&rpage.getFonteffect().length()>0?Integer.parseInt(rpage.getFonteffect()):0;
						content.append(encap.getPageTitle(pageid,rpage.getFlag(),rpage.getHz(),nid,userbase,tabid,this.infokind,rpage.getExtendAttr()));
						//System.out.println(form_x+"--"+form_y+"--"+to_x+"--"+to_y+"--"+content.toString());
						/* 1518 插入Logo后标题的问题 xiaoyun 2014-5-26 start */
						//executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight);
						executeCell(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),content.toString(),align,lines,fontName,fontSize,fontEffect,rwidth,rheight*2);
						/* 1518 插入Logo后标题的问题 xiaoyun 2014-5-26 end */
	          	    }else
	          	    {
	          	    	String extendattr=rpage.getExtendAttr();
	        			if(extendattr!=null&&extendattr.length()>0)
	        			{
	        				String ext="";        				
	        				form_x=(String)abean.get("form_x");
							form_y=(String)abean.get("form_y");
							to_x=(String)abean.get("to_x");
							to_y=(String)abean.get("to_y");
							content.setLength(0);
							fontSize=rpage.getFontsize();
							fontName=rpage.getFontname();
							align="7";		
							lines[0]="";
							lines[1]="";
							lines[2]="";
							lines[3]="";				
							rheight=Float.parseFloat(rpage.getRheight());
							rwidth=Float.parseFloat(rpage.getRwidth());
							rleft=Float.parseFloat(rpage.getRleft());
							rtop=Float.parseFloat(rpage.getRtop());	
							float rpageWidth=getSheetRpageWidth(tabid,pageid,rleft,rtop,rwidth,dao);	             
	        				if(extendattr.indexOf("<format>")!=-1&&extendattr.indexOf("</format>")!=-1)
	        				{
	        					ext=extendattr.substring(extendattr.indexOf("<ext>")+5,extendattr.indexOf("</ext>"));
	        				}        				
	        				ArrayList filelist=createTitlePhotoFile(tabid,pageid,rpage.getGridno(),ext,conn);        				
	        				if(filelist!=null && filelist.size()>0)
	    	            	{
	        					for(int s=0;s<cell_list.size();s++)
	        					{
	        						LazyDynaBean bean=(LazyDynaBean)cell_list.get(r);
	        						RGridView rgridv=(RGridView)bean.get("rgrid");
	        						float fheight=Float.parseFloat(rgridv.getRheight());
	        						float fwidth=Float.parseFloat(rgridv.getRwidth());
	        						float fleft=Float.parseFloat(rgridv.getRleft());
	        						float ftop=Float.parseFloat(rgridv.getRtop());
	        						if(ftop<=rtop&&(ftop+fheight)>=(rtop+rheight)&&fleft<=rleft&&(fleft+fwidth)>=rpageWidth)
	        						{
	        							form_x=(String)bean.get("form_x");
	        							form_y=(String)bean.get("form_y");
	        							to_x=(String)bean.get("to_x");
	        							to_y=(String)bean.get("to_y");
	        							break;
	        						}
	        					}	        					  					
	        					executeCellImage(Integer.parseInt(form_x),Short.parseShort(form_y),Integer.parseInt(to_x),Short.parseShort(to_y),filelist,rwidth,rheight,patriarch);
	              	    	}
	        			}
	          	    }
					
					
				}			
				setSheetWidthHeight(cellW_list,rowH_list,rpageT);
				doSplitSubgrids(cell_list, splitSubgrids, subgridContents);
			}	
		}catch(Exception e)
		{
		  e.printStackTrace();			  
		}
		
		FileOutputStream fileOut = null;
		try{
			fileOut = new FileOutputStream(url);
			this.wb.write(fileOut);
			fileOut.flush();
		}finally{
			PubFunc.closeIoResource(fileOut);
			this.sheet=null;
			this.wb =null;
		}
    	return excel_filename;
    }
    
    /**
     * 毫米 -> 英寸
     * @param mm
     * @return
     */
    private double mmToInches(double mm) {
        return mm * 0.03937;
    }
    
    /**
     * 页面设置
     */
    private void printSetup() {
        DataEncapsulation encap = new DataEncapsulation();
        List rnameList=encap.getRname(tabid,conn);
        LazyDynaBean rec=null;
        if(rnameList.size() == 0) {
            return;
        }
        rec=(LazyDynaBean)rnameList.get(0);
        
        //设置打印参数
        sheet.setMargin(HSSFSheet.TopMargin, mmToInches(Double.parseDouble(rec.get("tmargin").toString())));  // 页边距（上）    
        sheet.setMargin(HSSFSheet.BottomMargin, mmToInches(Double.parseDouble(rec.get("bmargin").toString())));  // 页边距（下）    
        sheet.setMargin(HSSFSheet.LeftMargin, mmToInches(Double.parseDouble(rec.get("lmargin").toString())));  // 页边距（左）    
        sheet.setMargin(HSSFSheet.RightMargin, mmToInches(Double.parseDouble(rec.get("rmargin").toString())));  // 页边距（右    

        HSSFPrintSetup ps = sheet.getPrintSetup();    
        ps.setLandscape("2".equals(rec.get("paperori")));  // 打印方向，true：横向，false：纵向(默认)    
        //ps.setVResolution((short)600); 
        short paper = getPaperSize(Integer.parseInt(rec.get("paper").toString()));
        if(paper != 0) {
            ps.setPaperSize(paper); //纸张类型
        }
    }
    
    private short getPaperSize(int paper) {
        short paperSize = 0;
        // 1:A4, 2:A3, 3:A5, 4:B5
        switch(paper) {
            case 1:
                paperSize = A4_PAPERSIZE;
                break;
            case 2:
                paperSize = A3_PAPERSIZE;
                break;
            case 3:
                paperSize = A5_PAPERSIZE;
                break;
            case 4:
                paperSize = B5_PAPERSIZE;
                break;
        }        
        return paperSize;
    }
    
    private static short A3_PAPERSIZE = 8;
    private static short A4_PAPERSIZE = HSSFPrintSetup.A4_PAPERSIZE;
    private static short A5_PAPERSIZE = HSSFPrintSetup.A5_PAPERSIZE;
    private static short B5_PAPERSIZE = 13;
    
    
    /** 
     * 子集格内容拆分行
     * @param cell_list
     * @param grid
     * @return
     */
    private boolean canSubgridSplitRow(ArrayList cell_list, RGridView grid, StringBuffer content) {
        ArrayList sameRowGrids = new ArrayList();
        for(int r=0;r<cell_list.size();r++)
        {
            LazyDynaBean abean=(LazyDynaBean)cell_list.get(r);
            RGridView agrid=(RGridView)abean.get("rgrid");
            if (agrid.getRtop().equals(grid.getRtop()) && agrid != grid) {
                sameRowGrids.add(agrid);
            }
        }
        int height = calcHeight(content.toString(), Float.parseFloat(grid.getRwidth()), grid.getFontName(), 
                Integer.parseInt(grid.getFonteffect()), Integer.parseInt(grid.getFontsize()), "M");
        if (height <= Float.parseFloat(grid.getRheight())) {
            return false;
        }
        getTabArea(this.tabid,this.pageid,"rgrid");  // 重新计算当页值
        if (sameRowGrids.size() == 0 && Float.parseFloat(grid.getRwidth()) == (tab_width - tab_min_x)) {
            return true;
        }
        
        if (sameRowGrids.size() == 1) {
            RGridView agrid = (RGridView)sameRowGrids.get(0);
            if (!"1".equals(agrid.getSubflag()) && 
                    Float.parseFloat(grid.getRwidth()) + Float.parseFloat(agrid.getRwidth()) == (tab_width - tab_min_x)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 计算单元格内容高度
     * @param content
     * @param columnWidth
     * @param fontName
     * @param fontEffect
     * @param fontSize
     * @param datatype
     * @return
     */
    private int calcHeight(String content, float columnWidth, String fontName,
            int fontEffect, int fontSize, String datatype) {
        int height = 0;
        Font font = new Font(fontName, fontEffect, fontSize);
        BufferedImage gg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics g = gg.createGraphics(); // 获得画布
        g.setFont(font);
        int aheight = g.getFontMetrics().getHeight(); // 每一行字的高度
        String[] lines = content.split("\r\n");
        height = lines.length * aheight + lines.length * 2/* 行间距2像素 */;
        if("M".equals(datatype))  // 加上下边距
        {
            height+=aheight*2;
        }
        return height;
    }
    
    /**
     * 子集格分行
     * @param cell_list
     * @param grid
     * @param content
     */
    private void splitSubgrid(ArrayList cell_list, RGridView grid, String content) {
    	try {
    		ArrayList sameRowGrids = new ArrayList();
    		for(int r=0;r<cell_list.size();r++)
    		{
    			LazyDynaBean abean=(LazyDynaBean)cell_list.get(r);
    			RGridView agrid=(RGridView)abean.get("rgrid");
    			if (agrid.getRtop().equals(grid.getRtop()) && agrid != grid) {
                    sameRowGrids.add(agrid);
                }
    		}
    		
    		int form_x = 0;
    		int form_y = 0;
    		int to_x = 0;
    		int to_y = 0;
    		for(int r=0;r<cell_list.size();r++)
    		{
    			LazyDynaBean abean=(LazyDynaBean)cell_list.get(r);
    			RGridView agrid=(RGridView)abean.get("rgrid");
    			if (agrid == grid) {
    				form_x=Integer.parseInt((String)abean.get("form_x"));
    				form_y=Integer.parseInt((String)abean.get("form_y"));
    				to_x=Integer.parseInt((String)abean.get("to_x"));
    				to_y=Integer.parseInt((String)abean.get("to_y"));
    			}
    		}
    		String[] lines = content.trim().split("\r\n\r\n");
    		int moveStartRow = form_x+1;
    		int moveRows = lines.length-1;
    		if(moveRows <= 0) {
                return;
            }
    		sheet.shiftRows(moveStartRow, sheet.getLastRowNum(), moveRows, true, false);
    		
    		String[] boders = null;
    		String[] innerboders = new String[4];
    		String[] firstrowboders = new String[4];
    		String[] lastrowboders = new String[4];
    		innerboders[0]=grid.getL();
    		innerboders[1]="0";//grid.getT();
    		innerboders[2]=grid.getR();
    		innerboders[3]="0";//grid.getB();
    		
    		firstrowboders[0]=grid.getL();
    		firstrowboders[1]=grid.getT();
    		firstrowboders[2]=grid.getR();
    		firstrowboders[3]="0";//grid.getB();
    		
    		lastrowboders[0]=grid.getL();
    		lastrowboders[1]="0";//grid.getT();
    		lastrowboders[2]=grid.getR();
    		lastrowboders[3]=grid.getB();
    		
    		for(int r=0;r<lines.length;r++) {
    			int x1 = form_x + r;
    			short y1 = (short)form_y;
    			int x2 = x1;
    			short y2 = (short)to_y;
    			String s = lines[r];
    			int fontEffect=grid.getFonteffect()!=null&&grid.getFonteffect().length()>0?Integer.parseInt(grid.getFonteffect()):0;
    			float rheight = 35 * (Short.parseShort(grid.getFontsize()) / 9);  //  需要计算行高得到
    			float rwidth=Float.parseFloat(grid.getRwidth());
    			
    			if(r==0) {
                    boders = firstrowboders;
                } else if(r==lines.length-1) {
                    boders = lastrowboders;
                } else {
                    boders = innerboders;
                }
    			executeSubCell(x1,y1,x2,y2, s, "6"/*align*/,boders,grid.getFontName(),grid.getFontsize(),fontEffect,rwidth,rheight);
    		}
    		
    		for(int r=0;r<sameRowGrids.size();r++)
    		{
    			RGridView agrid=(RGridView)sameRowGrids.get(r);
    			for(int s=0;s<cell_list.size();s++)
    			{
    				LazyDynaBean abean=(LazyDynaBean)cell_list.get(s);
    				RGridView bgrid=(RGridView)abean.get("rgrid");
    				if (agrid == bgrid) {
    					form_x=Integer.parseInt((String)abean.get("form_x"));
    					form_y=Integer.parseInt((String)abean.get("form_y"));
    					to_x=Integer.parseInt((String)abean.get("to_x"));
    					to_y=Integer.parseInt((String)abean.get("to_y"));
    					
    					if(form_x < to_x || (short)form_y < (short)to_y) {
    						CellRangeAddress g = new CellRangeAddress(form_x, to_x, (short)form_y, (short)to_y);
    						for (int i=0;i<sheet.getNumMergedRegions()-1;i++) {
    							CellRangeAddress tmp = sheet.getMergedRegion(i);
    							if(tmp.equals(g)) {
                                    sheet.removeMergedRegion(i);
                                }
    							
    						}
    					}
    					
    					to_x=to_x+lines.length-1;
    					ExportExcelUtil.mergeCell(sheet, form_x, (short)form_y, to_x, (short)to_y);
    					break;
    				}
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    }
    
    private void doSplitSubgrids(ArrayList cell_list, ArrayList splitSubgrids, ArrayList subgridContents) {
        for (int s=splitSubgrids.size()-1;s>=0;s--) {
            splitSubgrid(cell_list, (RGridView)splitSubgrids.get(s), subgridContents.get(s).toString());
        }
    }
    
    private ArrayList ykcardExcel(int tabid)
    {
    	ArrayList excelList=new ArrayList();
    	for(int i=0;i<this.pageDateList.size();i++)
    	{
    		DynaBean rec=(DynaBean)this.pageDateList.get(i);
    		this.pageid=Integer.parseInt(rec.get("pageid")+"");
    		this.cel_rgrid_map=getTabNumMap("RGrid","rleft",this.pageid);
    		this.row_rgrid_map=getTabNumMap("RGrid","rtop",this.pageid);
    		this.row_rpage_map=getTabNumMap("rPage","rtop",this.pageid);
    		this.cel_rpage_map=getTabNumMap("rPage","rleft",this.pageid);
            this.rowLayNum=this.row_rgrid_map.size();
            this.colLayNum=this.cel_rgrid_map.size();
            float[] rgridArea=getRGridArea(conn,tabid,pageid);
            getTabArea(this.tabid,this.pageid,"rgrid");            
            this.top_pix=rgridArea[1];
            this.bottom_pix=rgridArea[3];
            this.topLayMap.clear();
			this.botLayMap.clear();
			this.allParamMap.clear();
			this.centerMap.clear();
            getPageList("t");
    		getPageList("b");
    		getPageList("all");
    		getPageList("c");
    		DataEncapsulation encap=new DataEncapsulation();                             //创建封装Grid数据的对象
    	    List rgrids=encap.getRgrid(tabid,pageid,conn); 
    	    List rpageList=encap.getRpage(tabid,pageid,conn);  
    	    ArrayList rpage_list=getRPageList(rpageList,conn,rgrids);
    		ArrayList cell_list=getRGridList(rgrids,this.conn);    		
    		HashMap one_hash=new HashMap();
    		one_hash.put("pageM",rec);
    		one_hash.put("cellM",cell_list);  
    		one_hash.put("rpageM",rpage_list);  
    		ArrayList cellW_list=reCellWidth(this.tabid,this.pageid,conn);//每列的宽
    		one_hash.put("cellW",cellW_list); 
    		ArrayList rowH_list=reRowHeight(this.tabid,this.pageid,conn);//每行的高
    		one_hash.put("rowH",rowH_list); 
    		one_hash.put("rpageT",this.topLayMap.size()+"");//表头占几行
    		excelList.add(one_hash);
    		
    	}
    	return excelList;
    }
    /**
     * 
     * @param intTabid
     * @param conn
     * @return
     * @throws Exception
     */
    private  List getPagecount(int intTabid,Connection conn) throws Exception {
		StringBuffer sql=new StringBuffer();
		sql.append("select * from rTitle where Tabid=");
		sql.append(intTabid);
		try {
			List rs = ExecuteSQL.executeMyQuery(sql.toString(),conn);
			sql.delete(0,sql.length());
			return rs;
		} catch (Exception e) {
		}
		return null;
	}
    /**
	 *  1取得横纵栏的层数
	 * @param flag  1: 横栏  2：纵栏
	 * @return
	 */
	public HashMap getTabNumMap(String  flag,String columnName,int pageid)
	{
		HashMap map=new HashMap();
		try
		{
			int num=0;
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";			
			if("RGrid".equalsIgnoreCase(flag))
			{
				sql="select "+columnName+" from RGrid  where tabid="+this.tabid+" and pageid='"+pageid+"'  group by "+columnName+" order by "+columnName;
			}
			else {
                sql="select "+columnName+" from rPage  where tabid="+this.tabid+" and pageid='"+pageid+"'  group by "+columnName+" order by "+columnName;
            }
			RowSet rowSet=dao.search(sql);
		    while(rowSet.next())
			{
				num++;				
				map.put(String.valueOf(rowSet.getFloat(1)),String.valueOf(num));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
	/**
	 * 得到数据坐标
	 * @return
	 */
	private ArrayList getRGridList(List rgrids,Connection conn)
	{
		ArrayList rGridList=new ArrayList();		 
	    ContentDAO dao=new ContentDAO(this.conn);
	    RGridView rgrid;	    
	    int topL=this.topLayMap.size();
	    try{
	     	
	        if(!rgrids.isEmpty())
	        {
	        	for(int i=0;i<rgrids.size();i++)
	        	{
	        		LazyDynaBean abean=new LazyDynaBean();
	        		rgrid=(RGridView)rgrids.get(i);
	        		float rleft=Float.parseFloat((String)rgrid.getRleft());
	        		float rtop=Float.parseFloat((String)rgrid.getRtop());
	        		float rwidth=Float.parseFloat((String)rgrid.getRwidth());
	        		float rheight=Float.parseFloat((String)rgrid.getRheight());	        		
	    			int cell_count=getTabNumCounts("rleft",rleft,(rleft+rwidth),dao);
	    			int row_count=getTabNumCounts("rtop",rtop,(rtop+rheight),dao);		    			
					int row_x=Integer.parseInt((String)this.row_rgrid_map.get(String.valueOf(rtop)));
					int row_y=Integer.parseInt((String)this.cel_rgrid_map.get(String.valueOf(rleft)));	
					abean.set("form_x",String.valueOf(row_x+topL+-1));
					abean.set("form_y",String.valueOf(row_y+-1));
					if(rtop+rheight==this.tab_hieght)
					{
						abean.set("to_x",String.valueOf(row_count+topL+row_x-1));
					}else
					{
						abean.set("to_x",String.valueOf(row_count+topL+row_x-2));
					}					
					if(rleft+rwidth==this.tab_width)
					{
						abean.set("to_y",String.valueOf(cell_count+row_y-1));
					}else
					{
						abean.set("to_y",String.valueOf(cell_count+row_y-2));
					}
					abean.set("rgrid",rgrid);
					rGridList.add(abean);
	        	}
	        }
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    return rGridList;
	}
	/**
	 * 得到数据坐标
	 * @return
	 */
	private ArrayList getRPageList(List rpageList,Connection conn,List rgrids)
	{
		ArrayList rGridList=new ArrayList();
	    ContentDAO dao=new ContentDAO(this.conn);	    
	    try{
	     	
	        if(!rpageList.isEmpty())
	        {
	        	for(int i=0;i<rpageList.size();i++)
	        	{
	        		LazyDynaBean abean=new LazyDynaBean();
	        		RPageView rpage=(RPageView)rpageList.get(i);	
	        		//System.out.println(rpage.getHz());
	        		float rleft=Float.parseFloat((String)rpage.getRleft());
	        		float rtop=Float.parseFloat((String)rpage.getRtop());
	        		float rwidth=Float.parseFloat((String)rpage.getRwidth());	        		
	        		short from_y=getColumn_y(rleft,rgrids,"from");
	        		short to_y=getColumn_y((rleft+rwidth),rgrids,"to");	
	        		if((int)from_y<=0) {
                        from_y=1;
                    }
	    			if(this.top_pix>=rtop)
	    			{
	    				int row_x=Integer.parseInt((String)this.topLayMap.get(rpage.getRtop()));
	    				abean.set("form_x",String.valueOf(row_x-1));
	    				abean.set("form_y",String.valueOf(from_y-1));
	    				abean.set("to_x",String.valueOf(row_x-1));
	    				if(to_y<from_y-1) {
                            to_y=(short)(from_y-1);
                        }
	    				abean.set("to_y",String.valueOf(to_y));
	    			}else if(this.top_pix<rtop&&this.bottom_pix>rtop)
	    			{
	    				int topL=this.topLayMap.size();	  
	    				int index_x=Integer.parseInt((String)this.allParamMap.get(rpage.getRtop()));
	    				int center_x=Integer.parseInt((String)this.centerMap.get(rpage.getRtop()));
	    				//int index_bx=Integer.parseInt((String)this.topLayMap.get(rpage.getRtop()));
	    				int row_x=index_x+center_x;//+topL;//changxy  计算层级防止图片顶到表格下方 changxy 25275
//	    				int row_x=index_x+this.rowLayNum+topL;
	    				RGridView rgrid=null;
	    				for (int j = 0; j < rgrids.size(); j++) {//设置表格最底部图片位置 changxy 
	    					rgrid=(RGridView)rgrids.get(j);
							if(rgrid.getGridno().equals(rpage.getGridno())){
								float rgridleft=rgrid.getRleft()!=null&&rgrid.getRleft().length()>0?Float.parseFloat(rgrid.getRleft()):0;
								float rgridwidth=rgrid.getRwidth()!=null&&rgrid.getRwidth().length()>0?Float.parseFloat(rgrid.getRwidth()):0;
								float rgridheight=rgrid.getRheight()!=null&&rgrid.getRheight().length()>0?Float.parseFloat(rgrid.getRheight()):0;
								float rgridtop=rgrid.getRtop()!=null&&rgrid.getRtop().length()>0?Float.parseFloat(rgrid.getRtop()):0;;
								if(rleft>=rgridleft&&rleft<=(rgridleft+rgridwidth)&&rtop>=rgridtop/*&&rtop<=(rgridtop+rgridheight)*/){
									row_x=this.rowLayNum+1;
								}
							}
						}
	    				
	    				
	    				abean.set("form_x",String.valueOf(row_x-1));
	    				abean.set("form_y",String.valueOf(from_y-1));
	    				abean.set("to_x",String.valueOf(row_x-1));
	    				if(to_y<from_y-1) {
                            to_y=(short)(from_y-1);
                        }
	    				abean.set("to_y",String.valueOf(to_y));
	    			}else if(this.bottom_pix<=rtop)
	    			{
	    				int topL=this.topLayMap.size();	 
	    				int index_x=Integer.parseInt((String)this.botLayMap.get(rpage.getRtop()));
	    				//int index_bx=Integer.parseInt((String)this.topLayMap.get(rpage.getRtop()));
	    				int row_x=index_x+this.rowLayNum+topL;
	    				abean.set("form_x",String.valueOf(row_x-1));
	    				abean.set("form_y",String.valueOf(from_y-1));
	    				abean.set("to_x",String.valueOf(row_x-1));
	    				if(to_y<from_y-1) {
                            to_y=(short)(from_y-1);
                        }
	    				abean.set("to_y",String.valueOf(to_y));
	    			}else
	    			{
	    				int topL=this.topLayMap.size();	  
	    				int index_x=Integer.parseInt((String)this.allParamMap.get(rpage.getRtop()));
	    				//int index_bx=Integer.parseInt((String)this.topLayMap.get(rpage.getRtop()));
	    				int row_x=index_x+this.rowLayNum+topL;
	    				abean.set("form_x",String.valueOf(row_x-1));
	    				abean.set("form_y",String.valueOf(from_y-1));
	    				abean.set("to_x",String.valueOf(row_x-1));
	    				if(to_y<from_y-1) {
                            to_y=(short)(from_y-1);
                        }
	    				abean.set("to_y",String.valueOf(to_y));
	    			}	    			
	    			
	    			abean.set("rpage",rpage);
					rGridList.add(abean);
	        	}
	        }
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    return rGridList;
	}
	/**
	 * 
	 * @param tabName
	 * @param where1
	 * @param where2
	 * @param dao
	 * @return
	 */
    private int getTabNumCounts(String tabName,float where1,float where2,ContentDAO dao)
    {
    	int counts=1;
    	StringBuffer sql=new StringBuffer();
    	sql.append("select count(distinct("+tabName+")) as count from RGrid where tabid="+this.tabid+" and pageid="+this.pageid+"");
    	sql.append(" and "+tabName+">"+where1+" and "+tabName+"<="+where2);
    	try
    	{
    		//System.out.println(sql.toString());
    		RowSet rs=dao.search(sql.toString());
    		if(rs.next()) {
                counts=rs.getInt("count");
            }
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
        return counts;	
    }
    /**
	 * @param a  起始 x坐标
	 * @param b	 起始 y坐标
	 * @param c	 终止 x坐标
	 * @param d  终止 y坐标
	 * @param content 内容
	 * @param style	  表格样式
	 * @param fontEffect 字体效果 =0,=1 正常式样 =2,粗体 =3,斜体 =4,斜粗体
	 */
    public void executeCell(int a,short b,int c,short d,String content,String style,String []lines,String fontName,String fontSize,int fontEffect,float rwidth,float rheight)
	 {
    	try {
    		//HSSFRow row = sheet.createRow(a);	
    		HSSFRow row = sheet.getRow(a);
    		if(row==null) {
                row = sheet.createRow(a);
            }
    		HSSFCell cell = row.createCell(b);	
    		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);		
    		this.style=getStyle(style,this.wb,lines);
    		//主要报错：此文件中的某些文本格式可能已经更改,因为它已经超出最多允许的字体数
    		//为什么要注销呢，因为太多内容是因为创建的字体太多了，你调用HSSFWorkbook的createFont之后就创建一种字体，
    		//就算字体属性完全一样也是要创建一个新字体对象的，创建的数目是有限的。   
    		//因此不要频繁调用HSSFWorkbook的createFont方法，
    		
    		
    		//2009-12-28日，去掉注释暂没发现问题，去掉720-743行注释，去掉746-753行注释，去掉757行注释
    		HSSFFont font=getFont(fontName,Integer.parseInt(fontSize), fontEffect);//将存储在集合中创建的对应字体取出
    		rwidth=rwidth/0.27f;
    		rheight=rheight/0.67f;		
    		String r_W=String.valueOf(rwidth);
    		if(r_W.indexOf(".")!=-1) {
                r_W=r_W.substring(0,r_W.indexOf("."));
            }
    		String r_H=String.valueOf(rheight);
    		if(r_H.indexOf(".")!=-1) {
                r_H=r_H.substring(0,r_H.indexOf("."));
            }
    		this.sheet.setColumnWidth(b,(short)((Short.parseShort(r_W))*10));	 
    		row.setHeight((short)((Short.parseShort(r_H))*10));
    		if(content!=null&&content.length()>0){
    			content=content.replaceAll("<br>","\r\n");
    			content=content.replaceAll("</br>","\r\n");//换行符
    		}
    		cell.setCellValue(content);
    		this.style.setFont(font);
    		cell.setCellStyle(this.style); 
    		short b1=b;
    		while(++b1<=d)
    		{
    			cell = row.createCell(b1);
    			
    			cell.setCellStyle(this.style); 
    		}
    		for(int a1=a+1;a1<=c;a1++)
    		{
    			//row = sheet.createRow(a1);
    			row = sheet.getRow(a1);
    			if(row==null) {
                    row = sheet.createRow(a1);
                }
    			
    			b1=b;
    			while(b1<=d)
    			{
    				cell = row.createCell(b1);				
    				cell.setCellStyle(this.style);
    				b1++;
    			}
    		}		 
    		
    		ExportExcelUtil.mergeCell(sheet, a,b,c,d);
    	} catch (Exception e) {
    		e.printStackTrace();
		}
	 }
    
    /**
     * 子集格
     * @see #splitSubgrid
     * @see #executeCell()
     */
    private void executeSubCell(int a,short b,int c,short d,String content,String style,String []lines,String fontName,String fontSize,int fontEffect,float rwidth,float rheight)
     {
    	try {
    		HSSFRow row = sheet.getRow(a);
    		if(row==null) {
                row = sheet.createRow(a);
            }
    		HSSFCell cell = row.createCell(b);
    		this.style=getStyle(style,this.wb,lines);
    		HSSFFont font = wb.createFont();
    		if(fontName==null||fontName.trim().length()==0)
    		{
    			font.setFontHeightInPoints((short)10);
    			font.setFontName("宋体");
    		}
    		else
    		{
    			font.setFontHeightInPoints(Short.parseShort(fontSize));
    			font.setFontName(fontName);             
    			if(fontEffect==2)
    			{
    				font.setBold(true);
    			}
    			else if(fontEffect==3)
    			{
    				font.setItalic(true);
    			}
    			else if(fontEffect==4)
    			{
    				font.setBold(true);
    				font.setItalic(true);
    			}
    		}
    		rwidth=rwidth/0.27f;
    		rheight=rheight/0.67f;     
    		String r_W=String.valueOf(rwidth);
    		if(r_W.indexOf(".")!=-1) {
                r_W=r_W.substring(0,r_W.indexOf("."));
            }
    		String r_H=String.valueOf(rheight);
    		if(r_H.indexOf(".")!=-1) {
                r_H=r_H.substring(0,r_H.indexOf("."));
            }
//         this.sheet.setColumnWidth(b,(short)((Short.parseShort(r_W))*10));   
    		row.setHeight((short)((Short.parseShort(r_H))*10));
    		if(content!=null&&content.length()>0){
    			content=content.replaceAll("<br>","\r\n");
    			content=content.replaceAll("</br>","\r\n");
    		}
    		cell.setCellValue(content);
    		this.style.setFont(font);
    		cell.setCellStyle(this.style); 
    		short b1=b;
    		while(++b1<=d)
    		{
    			cell = row.createCell(b1);
    			cell.setCellStyle(this.style); 
    		}
    		for(int a1=a+1;a1<=c;a1++)
    		{
    			row = sheet.getRow(a1);
    			if(row==null) {
                    row = sheet.createRow(a1);
                }
    			
    			b1=b;
    			while(b1<=d)
    			{
    				cell = row.createCell(b1);             
    				cell.setCellStyle(this.style);
    				b1++;
    			}
    		}       
    		
    		ExportExcelUtil.mergeCell(sheet, a,b,c,d);
    	} catch (Exception e) {
    		e.printStackTrace();
		}
     }    
    
    /**
     * 
     * @param a
     * @param b
     * @param c
     * @param d
     * @param filelist
     * @param rwidth
     * @param rheight 0表示不设置行高, 单位像素
     * @param patriarch
     */
    private void executeCellImage(int a,short b,int c,short d,ArrayList filelist,float rwidth,float rheight,HSSFPatriarch patriarch)
	{
    	try
    	{
    	    if(a==c&&rheight!=0) {
    	        HSSFRow row = sheet.getRow(a);
                if(row==null) {
                    row = sheet.createRow(a);
                }
                rheight=rheight/0.067f;     // 转单位
                row.setHeight((short)rheight);
    	    }
    	    //liuy 2015-3-19 7980：自助服务/常用统计/穿透后点基本情况，出现登记表，有照片时输出excel，照片上边和左边的距离算的有问题，导致excel中格线看不到了，不对。
     	    HSSFClientAnchor anchor = new HSSFClientAnchor(10,5,1023,255,b,Short.parseShort(String.valueOf(a)),
     	            d,Short.parseShort(String.valueOf(c)));
     	    anchor.setAnchorType(AnchorType.DONT_MOVE_DO_RESIZE);
    		byte[] byt=(byte[])filelist.get(0);
			String ext=(String)filelist.get(1);
			if(byt!=null&&(".JPG".equalsIgnoreCase(ext)|| ".PNG".equalsIgnoreCase(ext)|| ".BMP".equalsIgnoreCase(ext))) {
                patriarch.createPicture(anchor,this.wb.addPicture(byt, HSSFWorkbook.PICTURE_TYPE_JPEG));
            }
		
        }catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    /** 设置表格样式 */
	public HSSFCellStyle getStyle(String ali,HSSFWorkbook wb,String[] lines)
	{
		HSSFCellStyle a_style=wb.createCellStyle();
		if(lines[0]!=null&& "1".equals(lines[0]))
		{
			a_style.setBorderLeft(BorderStyle.THIN);
			a_style.setLeftBorderColor(HSSFColor.BLACK.index);
		}
		if(lines[1]!=null&& "1".equals(lines[1]))
		{
			a_style.setBorderTop(BorderStyle.THIN);
			a_style.setTopBorderColor(HSSFColor.BLACK.index);
		}
		if(lines[2]!=null&& "1".equals(lines[2]))
		{
			a_style.setBorderRight(BorderStyle.THIN);
			a_style.setRightBorderColor(HSSFColor.BLACK.index);
		}
		if(lines[3]!=null&& "1".equals(lines[3]))
		{
			a_style.setBorderBottom(BorderStyle.THIN);
			a_style.setBottomBorderColor(HSSFColor.BLACK.index);
		}
		a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
		a_style.setWrapText( true );		
		if ("0".equals(ali)) {
			a_style.setAlignment(HorizontalAlignment.LEFT);
			a_style.setVerticalAlignment(VerticalAlignment.TOP);
		} else if ("1".equals(ali)) {
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setVerticalAlignment(VerticalAlignment.TOP);
		} else if ("2".equals(ali)) {
			a_style.setAlignment(HorizontalAlignment.RIGHT);
			a_style.setVerticalAlignment(VerticalAlignment.TOP);
		} else if ("3".equals(ali)) {
			a_style.setAlignment(HorizontalAlignment.LEFT);
			a_style.setVerticalAlignment(VerticalAlignment.BOTTOM);
		} else if ("4".equals(ali)) {
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setVerticalAlignment(VerticalAlignment.BOTTOM);
		} else if ("5".equals(ali)) {
			a_style.setAlignment(HorizontalAlignment.RIGHT);
			a_style.setVerticalAlignment(VerticalAlignment.BOTTOM);
		} else if ("6".equals(ali)) {
			a_style.setAlignment(HorizontalAlignment.LEFT);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		} else if ("7".equals(ali)) {
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		} else if ("8".equals(ali)) {
			a_style.setAlignment(HorizontalAlignment.RIGHT);
			a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		}		
		return a_style;
	}
	/**
	 * 表格区域
	 * @param tabid
	 * @param pageid
	 * @param conn
	 */
	private void getTabArea(int tabid,int pageid,String flag)
	{
	    StringBuffer sql=new StringBuffer();
	    sql.append("select max(rleft+rwidth) as mm, min(rleft) nn from rgrid where tabid="+tabid+" and pageid="+pageid);
	    ContentDAO dao=new ContentDAO(this.conn);
	    try
	    {
	    	RowSet rs=dao.search(sql.toString());
	    	if(rs.next()) {
	    	   this.tab_width=rs.getFloat("mm");
	    	   this.tab_min_x=rs.getFloat("nn");
	    	}
	    	sql.setLength(0);
	    	sql.append("select max(rtop+rheight) as mm, min(rtop) nn from rgrid where tabid="+tabid+" and pageid="+pageid);
	    	rs=dao.search(sql.toString());
	    	if(rs.next()) {
	    		this.tab_hieght=rs.getFloat("mm");
	    		this.tab_min_y=rs.getFloat("nn");
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	}
	/**
	 * @param userbase
	 * @param conn
	 * @param card
	 * @param rgrid
	 * @param userview
	 * @param nFlag
	 * @param valueList
	 * @param fieldset
	 * @return
	 * @throws Exception
	 */
	private ArrayList getTextValue(String userbase, Connection conn, GetCardCellValue card, RGridView rgrid, UserView userview, byte nFlag, ArrayList valueList, DynaBean fieldset) {
		//获得单元格的内容值
		try{
		//System.out.println(rgrid.getCSetName() + rgrid.getField_name() + nFlag + userbase + Integer.parseInt(queryflag) + userview.getUserName() + rgrid.getCSetName()+nFlag+userbase+Integer.parseInt(queryflag)+Integer.parseInt(fieldset.get("changeflag").toString())+cyear+cmonth+ctimes+nid+userpriv+havepriv+cdatestart+cdateend+season);
			String changeflag="0";
			if(fieldset!=null) {
                changeflag=fieldset.get("changeflag").toString();
            } else if ("1".equalsIgnoreCase(rgrid.getIsView())) {
		        changeflag = card.viewIsChangeflag(rgrid, conn);
		      }
			if(queryflagtype==0) {
                valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflagtype,Integer.parseInt(changeflag),cyear,cmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,this.fieldpurv);
            } else if(queryflagtype==1)
		  {
			  if(infokind!=null&& "5".equals(infokind))
			  {
				  StatisticPlan statisticPlan=new StatisticPlan(userview,conn);
				   String table_name=statisticPlan.getPER_RESULT_TableName(this.plan_id);
				   rgrid.setCSetName(table_name);
				   valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflagtype,0,cyyear,cymonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,this.fieldpurv);
			  }else
			  {
				  valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflagtype,Integer.parseInt(changeflag),this.cyear,this.cmmonth,this.ctimes,nid,userview,userpriv,havepriv,this.cdatestart,this.cdateend,this.season,conn,this.fieldpurv);
			  }
		  }
		     
		   else if(queryflagtype==2) {
                valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflagtype,Integer.parseInt(changeflag),cdyear,cdmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,this.fieldpurv);
            } else if(queryflagtype==3) {
                valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflagtype,Integer.parseInt(changeflag),csyear,cdmonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,this.fieldpurv);
            } else if(queryflagtype==4) {
                valueList=card.GetFldValue(infokind,rgrid.getCSetName(),rgrid.getField_name(),nFlag,userbase,rgrid,queryflagtype,Integer.parseInt(changeflag),cyyear,cymonth,ctimes,nid,userview,userpriv,havepriv,cdatestart,cdateend,season,conn,this.fieldpurv);
            }
		   }
		   catch(Exception e)
		   {
			  e.printStackTrace();   
		   }
		 return valueList;
	}
	/*****对子集的显示*****/
	public String viewSubclass(RGridView rgrid,Connection conn,UserView userview,int statYear,           //年
			int statMonth,          //月
			int ctimes,             //次数
			String userbase,
			String nId,byte nFlag)
	{
		StringBuffer html=new StringBuffer();
		String sub_domain=rgrid.getSub_domain();
		if(sub_domain==null||sub_domain.length()<=0) {
            return "";
        }
		YkcardViewSubclass ykcardViewSubclass=new YkcardViewSubclass(conn,cyyear,cymonth,ctimes,userbase,nid,userview);
		int fact_width=(int)Float.parseFloat(rgrid.getRwidth()) + 1;
	    int fact_height=(int)Float.parseFloat(rgrid.getRheight()) +1;	 
	    ykcardViewSubclass.setFenlei_type(this.fenlei_type);
		ykcardViewSubclass.setFact_width(fact_width);
		ykcardViewSubclass.setFact_height(fact_height);
		ykcardViewSubclass.setUserpriv(userpriv);
		ykcardViewSubclass.getXmlSubdomain(rgrid.getSub_domain(),rgrid);
		html.append(ykcardViewSubclass.viewSubClassExcelStr(infokind,userbase,conn,userview,rgrid,disting_pt,nFlag));
		return html.toString();
	}
	
	/**
	 * 得到表格区域
	 * @param conn
	 * @param table
	 * @param pageid
	 * @return
	 */
	private float[] getRGridArea(Connection conn,int table,int pageid)
	{
		StringBuffer sql=new StringBuffer();
	    sql.append("select max(rleft+rwidth) as max_W from rgrid where tabid="+tabid+" and pageid="+pageid);
	    ContentDAO dao=new ContentDAO(this.conn);
	    float rgridA[]=new float[4];
	    try
	    {
	    	RowSet rs=dao.search(sql.toString());
	    	if(rs.next()) {
                rgridA[2]=rs.getFloat("max_W");
            }
	    	sql.setLength(0);
	    	sql.append("select max(rtop+rheight) as max_H from rgrid where tabid="+tabid+" and pageid="+pageid);
	    	rs=dao.search(sql.toString());
	    	if(rs.next()) {
                rgridA[3]=rs.getFloat("max_H");
            }
	    	sql.setLength(0);
	    	sql.append("select min(rtop) as min_top from rgrid where tabid="+tabid+" and pageid="+pageid);
	    	rs=dao.search(sql.toString());
	    	if(rs.next()) {
                rgridA[1]=rs.getFloat("min_top");
            }
	    	sql.setLength(0);
	    	sql.append("select min(rleft) as min_left from rgrid where tabid="+tabid+" and pageid="+pageid);
	    	rs=dao.search(sql.toString());
	    	if(rs.next()) {
                rgridA[0]=rs.getFloat("min_left");
            }
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    return rgridA;
	}
	/**
	 * 
	 * @param position  t:表头上部参数   b:表尾 下部参数 all:全部（封面）
	 * @return
	 */
	public ArrayList getPageList(String position)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			if("t".equals(position)) {
                sql="select * from rpage where tabid="+this.tabid+" and pageid="+this.pageid+"  and rtop<="+this.top_pix+" order by rtop,rleft";
            } else if("b".equals(position)) {
                sql="select * from rpage where tabid="+this.tabid+" and pageid="+this.pageid+" and rtop>="+this.bottom_pix+" order by rtop,rleft";
            } else if("all".equals(position)) {
                sql="select * from rpage where tabid="+this.tabid+" and pageid="+this.pageid+" order by rtop,rleft";
            } else if("c".equals(position)) {
                sql="select * from rpage where tabid="+this.tabid+" and pageid="+this.pageid+" and rtop<"+this.bottom_pix+"  and rtop>"+this.top_pix+" order by rtop,rleft";
            }
			RowSet recset=dao.search(sql);
			int rtop=-1;
			int layNum=0;			
			while(recset.next())
			{
				if("t".equals(position)){ ////27483 组织机构-岗位说明书标题不完全对其，导出excel后  上下错误很多。
					if(rtop==-1||Math.abs(recset.getInt("rtop")-rtop)>4){
						layNum++;
						if("t".equals(position)) {
                            this.topLayMap.put(recset.getString("rtop"),String.valueOf(layNum));
                        }
					}
				}else{
					if(recset.getInt("rtop")!=rtop)
					{
						layNum++;
						/*if(position.equals("t"))
							this.topLayMap.put(recset.getString("rtop"),String.valueOf(layNum));
				else*/ if("b".equals(position)) {
                        this.botLayMap.put(recset.getString("rtop"),String.valueOf(layNum));
                    } else if("all".equals(position)) {
                        this.allParamMap.put(recset.getString("rtop"),String.valueOf(layNum));
                    } else if("c".equals(position)) {
                        this.centerMap.put(recset.getString("rtop"),String.valueOf(layNum));
                    }
					}
				}
							
				RecordVo vo=new RecordVo("rpage");
				vo.setInt("tabid",recset.getInt("tabid"));
				vo.setInt("gridno",recset.getInt("gridno"));	
				String hz=recset.getString("hz");
				vo.setString("hz",hz);
				vo.setInt("rleft",recset.getInt("rleft"));
				if("t".equals(position)&&Math.abs(recset.getInt("rtop")-rtop)<=4&&rtop!=-1) {
                    vo.setInt("rtop",rtop);
                } else {
                    vo.setInt("rtop",recset.getInt("rtop"));
                }
				vo.setInt("rwidth",recset.getInt("rwidth"));
				vo.setInt("rheight",recset.getInt("rheight"));
				vo.setInt("fontsize",recset.getInt("fontsize"));	
				vo.setString("fontname",recset.getString("fontname"));				
				vo.setInt("fonteffect",recset.getInt("fonteffect"));
				vo.setInt("flag",recset.getInt("flag"));	
				list.add(vo);				
				if(rtop==-1||Math.abs(recset.getInt("rtop")-rtop)>4) {
                    rtop=recset.getInt("rtop");
                }
			}
			if("t".equals(position)) {
                this.topParamLayNum=layNum;
            } else {
                this.bottomParamLayNum=layNum;
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	 * @param userTable
	 * @param userNumber
	 * @param flag
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public ArrayList createPhotoFile(String userTable, String userNumber,String flag,Connection conn)  throws Exception {
		ArrayList list=new ArrayList();
		byte[] bytes=null;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		InputStream in= null;
		ByteArrayOutputStream outStream=new ByteArrayOutputStream();
		String      ext="";
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select ext,Ole from ");
			strsql.append(""+userTable);
			strsql.append(" where A0100='");
			strsql.append(userNumber);
			strsql.append("' and Flag='");
			strsql.append(flag);
			strsql.append("'");
			rowSet=dao.search(strsql.toString());
			if (rowSet.next()) {
				in = rowSet.getBinaryStream("ole");
				ext=rowSet.getString("ext");
				int len;
				byte buf[] = new byte[1024];
				while ((len = in.read(buf, 0, 1024)) != -1) {
					outStream.write(buf, 0, len);
				}
				bytes=outStream.toByteArray();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		} 
		finally{
			if(in!=null) {
                PubFunc.closeIoResource(in);
            }
			outStream.close();
			if(rowSet!=null) {
                rowSet.close();
            }
		}
		list.add(bytes);
		list.add(ext);
		
		return list;
	}
	public  ArrayList createTitlePhotoFile(int strTabid,int pageid, String gridno,String ext,Connection conn) throws Exception {
		ArrayList list=new ArrayList();
		byte[] bytes=null;
        ResultSet rs = null;  
        ContentDAO dao = null;
        InputStream in = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select * from rPage where (Tabid=");
            strsql.append(strTabid);
            strsql.append(" and gridno=");
            strsql.append(gridno);
            strsql.append(" and pageid="+pageid);
            strsql.append(")");
           
            dao = new ContentDAO(conn);
            rs = dao.search(strsql.toString());
            if (rs.next()) {
            	if(rs.getBinaryStream("content")!=null){            		
            		in = rs.getBinaryStream("content");				
            		
            		ByteArrayOutputStream outStream=new ByteArrayOutputStream();
            		try {
            			int len;
            			byte buf[] = new byte[1024];
            			while ((len = in.read(buf, 0, 1024)) != -1) {
            				outStream.write(buf, 0, len);
            			}
            			bytes=outStream.toByteArray();
            		} finally {
            			PubFunc.closeIoResource(outStream); 
            		}
            	}
            }else {
                return null;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(in);
            PubFunc.closeIoResource(rs);
        }
        list.add(bytes);
		list.add(ext);
        return list;
    }
	/**
	 * 取得标题在excel的纵坐标位置
	 * @param rleft
	 * @return excel第几列, flag="from"从1开始，flag="to"从0开始
	 */
	private short getColumn_y(float rleft,List rgrids,String flag)
	{
		short y=0;
		/*RGridView rgrid=null;
		for(int i=0;i<rgrids.size();i++)
    	{
    		rgrid=(RGridView)rgrids.get(i);			
			float a_rleft=Float.parseFloat((String)rgrid.getRleft());
    		float a_rtop=Float.parseFloat((String)rgrid.getRtop());
    		float a_width=Float.parseFloat((String)rgrid.getRwidth());
    		float a_height=Float.parseFloat((String)rgrid.getRheight()); 
			if(rleft>=a_rleft&&rleft<=(a_rleft+a_width))
			{
				if(flag.equals("from"))
				{
					y=Short.parseShort((String)this.cel_rgrid_map.get(String.valueOf(a_rleft)));
				}else if(flag.equals("to"))
				{
					String value=(String)this.cel_rgrid_map.get(String.valueOf(a_rleft+a_width));
					if(value==null||value.length()<=0)
						y=(short)(this.colLayNum);
					else
					    y=Short.parseShort((String)this.cel_rgrid_map.get(String.valueOf(a_rleft+a_width)));
					if(y==0)
					{
						y=Short.parseShort((String)this.cel_rgrid_map.get(String.valueOf(a_rleft)));	
					}
					--y;
				}				
				break;
			}
		}
		*/
        for(int i=1;i<=colLayNum;i++)
        {
            int l = getColLeft(i);
            int r = getColRight(i);
            if(rleft>=l&&rleft<=r||rleft>=l&&r==0)
            {
                y=(short)i;
                if("to".equals(flag)) {
                    --y;
                }
                break;
            }
        }
		return y;
	}
	
	/**
	 * 某列右边的 x 轴坐标(像素)
	 * @param col 从1开始
	 * @return 0表示超出范围
	 * @see #getColLeft
	 */
	private int getColRight(int col) {
	    return getColLeft(col+1);
	}

	/**
	 * 某列左边的 x 轴坐标(像素)
	 * @param col 从1开始
	 * @return
	 */
    private int getColLeft(int col) {
        int i = 0;
        Iterator t = cel_rgrid_map.keySet().iterator();
        while (t.hasNext()){
            String colpos = (String)t.next();
            if(String.valueOf(col).equals(cel_rgrid_map.get(colpos))) {
                i = Float.valueOf(colpos).intValue();
                break;
            }
        }

        return i;
    }
	
	private float getSheetWidth(int tabid,int pageid,float rleft,float rtop,float rwidth,ContentDAO dao)
	{
	  StringBuffer sql=new StringBuffer();
	  sql.append("select rleft from rgrid where tabid="+tabid+" and pageid="+pageid);
	  sql.append(" and rtop<>"+rtop+" and rleft>"+rleft+" order by rleft");	 
	  try
	  {
		  RowSet rs=dao.search(sql.toString());
		  if(rs.next())
		  {
			  float rleft2=rs.getFloat("rleft");
			  rwidth=rleft2-rleft;
		  }
	  }catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return rwidth;
	}
	private float getSheetRpageWidth(int tabid,int pageid,float rleft,float rtop,float rwidth,ContentDAO dao)
	{
	  StringBuffer sql=new StringBuffer();
	  sql.append("select rleft,rwidth from rgrid where tabid="+tabid+" and pageid="+pageid);
	  sql.append(" and rtop<>"+rtop+" and rleft>"+rleft+" order by rleft");	 
	  try
	  {
		  RowSet rs=dao.search(sql.toString());
		  if(rs.next())
		  {
			  float rleft2=rs.getFloat("rleft");
			  rwidth=rleft2-rleft;
			  rwidth=rwidth+rs.getFloat("rwidth");
		  }
	  }catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return rwidth;
	}
	/**
	 * 得到没列的宽
	 * @param tabid
	 * @param pageid
	 * @param conn
	 * @return
	 */
	private ArrayList reCellWidth(int tabid,int pageid,Connection conn)
	{
	   	String sql="";
	   	sql="select rleft from RGrid  where tabid="+tabid+" and pageid='"+pageid+"'  group by rleft order by rleft";
	    ArrayList cell_list=new ArrayList();
	   	try
	   	{
	   		ContentDAO dao=new ContentDAO(conn);
	   		RowSet rs=dao.search(sql);
	   		ArrayList list=new ArrayList();
	   		while(rs.next())
	   		{
	   			list.add(rs.getFloat("rleft")+"");
	   		}
	   		
	   		sql="select rwidth as rw from RGrid  where tabid="+tabid+" and pageid='"+pageid+"' and rleft="+list.get(list.size()-1)+"";
	   		float lwidth=0;
	   		rs=dao.search(sql);
	   		if(rs.next())
	   		{
	   			lwidth=rs.getFloat("rw");
	   		}
	   		float rleft1=0;
	   		float rleft2=0;	   		
	   		int i=0;
	   		for(i=0;i<list.size()-1;i++)
	   		{
	   			rleft1=Float.parseFloat(list.get(i).toString());	   			
	   			rleft2=Float.parseFloat(list.get(i+1).toString());
	   			cell_list.add(new Float(rleft2-rleft1));	   			
	   		}	   		
	   		cell_list.add(new Float(lwidth));	
	   	}catch(Exception e)
	   	{
	   		e.printStackTrace();
	   	}
	   	return cell_list;
	} 
	/**
	 * 得到没列的高
	 * @param tabid
	 * @param pageid
	 * @param conn
	 * @return
	 */
	private ArrayList reRowHeight(int tabid,int pageid,Connection conn)
	{
	   	String sql="";
	   	sql="select rtop from RGrid  where tabid="+tabid+" and pageid='"+pageid+"'  group by rtop order by rtop";
	   	ArrayList cell_list=new ArrayList();
	   	try
	   	{
	   		ContentDAO dao=new ContentDAO(conn);
	   		RowSet rs=dao.search(sql);
	   		ArrayList list=new ArrayList();
	   		while(rs.next())
	   		{
	   			list.add(rs.getFloat("rtop")+"");
	   		}
	   		sql="select rheight as rw from RGrid  where tabid="+tabid+" and pageid='"+pageid+"' and rtop="+list.get(list.size()-1);
	   		float lh=0;
	   		rs=dao.search(sql);
	   		if(rs.next())
	   		{
	   			lh=rs.getFloat("rw");
	   		}
	   		float rtop1=0;
	   		float rtop2=0;	   		
	   		int i=0;
	   		for(i=0;i<list.size()-1;i++)
	   		{
	   			rtop1=Float.parseFloat(list.get(i).toString());	   			
	   			rtop2=Float.parseFloat(list.get(i+1).toString());
	   			cell_list.add(new Float(rtop2-rtop1));	   			
	   		}	   		
	   		cell_list.add(new Float(lh));	
	   	}catch(Exception e)
	   	{
	   		e.printStackTrace();
	   	}
	   	return cell_list;
	} 
	private void setSheetWidthHeight(ArrayList cellW_list,ArrayList rowH_list,int rpageT)
	{
		for(int i=0;i<cellW_list.size();i++)
		{
			float width=Float.parseFloat(cellW_list.get(i).toString());
			width=width/0.27f;
			String r_W=String.valueOf(width);
			 if(r_W.indexOf(".")!=-1) {
                 r_W=r_W.substring(0,r_W.indexOf("."));
             }
			this.sheet.setColumnWidth((short)i,(short)((Short.parseShort(r_W))*10));
		}
		for(int i=0;i<rowH_list.size();i++)
		{
			//HSSFRow row = sheet.createRow(i+rpageT);	
			HSSFRow row = sheet.getRow(i+rpageT);
			if(row==null) {
                row = sheet.createRow(i+rpageT);
            }

			float rheight=Float.parseFloat(rowH_list.get(i).toString()); 			
			rheight=rheight/0.67f;
			String r_H=String.valueOf(rheight);
			 if(r_H.indexOf(".")!=-1) {
                 r_H=r_H.substring(0,r_H.indexOf("."));
             }
			 row.setHeight((short)((Short.parseShort(r_H))*10));
		}		
	}
	/**
	 * 查看标题是否在表格内部，如果是则插入的表格里面
	 * @param rgrid
	 * @param rpagelist
	 * @param encap
	 * @return
	 */
	private String queryRpageFromRgrid(RGridView rgrid,ArrayList rpagelist,DataEncapsulation encap)
	{
		StringBuffer content=new StringBuffer();
		float rheight=rgrid.getRheight()!=null&&rgrid.getRheight().length()>0?Float.parseFloat(rgrid.getRheight()):0;
		float rleft=rgrid.getRleft()!=null&&rgrid.getRleft().length()>0?Float.parseFloat(rgrid.getRleft()):0;;
		float rtop=rgrid.getRtop()!=null&&rgrid.getRtop().length()>0?Float.parseFloat(rgrid.getRtop()):0;;
		float rwidth=rgrid.getRwidth()!=null&&rgrid.getRwidth().length()>0?Float.parseFloat(rgrid.getRwidth()):0;;
		try
		{
			for(int r=0;r<rpagelist.size();r++)
			{
				LazyDynaBean abean=(LazyDynaBean)rpagelist.get(r);
				RPageView rpage=(RPageView)abean.get("rpage");
				float top=Float.parseFloat((String)rpage.getRtop());
        		float width=Float.parseFloat((String)rpage.getRwidth());	   
        		float height=Float.parseFloat((String)rpage.getRheight());	  
        		float left=Float.parseFloat((String)rpage.getRleft());
        		if(this.rPageToRgridMap.get(rpage.getGridno())!=null) {
                    continue;
                }
				if(top>=rtop&&top<=(rtop+rheight)&&(rpage.getFlag()!=6))//底部图片不显示问题 排除图片类型  changxy		
				{
					if(left>=rleft&&left<=(rleft+rwidth))
					{
						content.append(encap.getPageTitle(pageid,rpage.getFlag(),rpage.getHz(),nid,userbase,tabid,this.infokind,rpage.getExtendAttr()));
						rPageToRgridMap.put(rpage.getGridno(),"1");						
					}  
				}
		    }
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
		return content.toString();
	}
		
}
