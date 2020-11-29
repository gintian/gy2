package com.hjsj.hrms.businessobject.ykcard;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.ykcard.RGridView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class YkcardViewSubclass {

	private XmlSubdomain domain;
	private ArrayList fieldlist=new ArrayList();
	private HashMap hash=new HashMap();
	private float sumwidth=0;
	private float avewidth=0;
	private int fontsize;
	private String lh;//横线
	private String ls;//竖线
	private String colheadheight;//0表示指定的标题行高(毫米、浮点数)，其他值表示自动计算标题行高
    private String datarowcount;//0表示指定数据行数，数据行行高均匀分布；其他值表示有几条记录显示几行，
    private String customcolhead;
    private HashMap customcolheadwidth=new HashMap();
    private String fontweight="";
    private int statYear;           //年
	private int statMonth;         //月
	private int ctimes;             //次数
	private String userbase;
	private String nId;
	private Connection conn;
	private HashMap cellhash=new HashMap();
	private  int NMIN = 0;                                  /*子集纪录的最小和最大的比较值常量*/
	private  int NMAX = 120;   
	private static final int CONDITIONQUERY_TYPE = 0;       //条件查询
	private static final int DATEQUERY_TYPE = 1;            //时间月查询
	private static final int BETWEENDATEQUERY_TYPE = 2;     //时间段查询
	private static final int SEASONQUERY_TYPE = 3;          //季节查询
	private static final int YEARQUERY_TYPE = 4;            //年查询
	private static final String PERSONKEYTYPE = "A0100";    //人员库特殊指标
	private static final String UNITKEYTYPE = "B0110";      //单位库特殊指标
	private static final String POSTKEYTYPE = "E01A1";      //岗位库特殊指标
	private static final String STDPOSKEYTYPE = "H0100";    //基准岗位库特殊指标
	private static final char CELLFIELD_NUMBERTYPE = 'N';   //数字类型常量
	private static final char CELLFIELD_DATETYPE = 'D';     //日期类型常量
	private static final char CELLFIELD_MEMOTYPE = 'M';     //备注类型常量
	private static final char CELLFIELD_STRINGTYPE = 'A';   //字符类型常量
	private int table_width;
	private boolean isMaxwidth=false;
	private int newline=0;//实际换行
	private int fact_row=0;
	private int fact_width=0;
	private int fact_height=0;
	private boolean auto_heigh=false;
	private double pix=0.24;
	private float pdf_h_base=1.002f;
	private float pdf_w_base=1.002f;
	private UserView userview;
	BufferedImage gg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	private static final int  PerCycle_Year      = 0; //年度
	private static final int  PerCycle_HalfYear  = 1; //半年
	private static final int PerCycle_Quarter   = 2; //季度
	private static final int  PerCycle_Month     = 3; //月度
	private static final int  PerCycle_Random    = 7; //不定期
	
	private boolean needfilter=false;
	
	private String display_zero="";
	private String userpriv="";
	private String fieldpurv="";
	private String fenlei_type="";//分类类型
	private String bizDate;
	private String print_File="true";//判断是否输出附件false 不输出，true 输出，默认输出
	private String searchDateSql=" and 1=1 ";//按日期查询拼接sql changxy
	private Boolean ykcard_auto=false;//单元格字体自适应 true 自适应 false 不自动适应
	

	public Boolean getYkcard_auto() {
		return ykcard_auto;
	}

	public void setYkcard_auto(Boolean ykcard_auto) {
		this.ykcard_auto = ykcard_auto;
	}

	public String getSearchDateSql() {
		return searchDateSql;
	}

	public void setSearchDateSql(String searchDateSql) {
		this.searchDateSql = searchDateSql;
	}
	/**
	 * 查询指标 便于YkcardTag调用 查询子集指标
	 * */
	public ArrayList getFieldList(){
		return this.fieldlist;
	}
	
	
	public String getBizDate() {
		return bizDate;
	}
	public void setBizDate(String bizDate) {
		this.bizDate = bizDate;
	}
	public String getUserpriv() {
		return userpriv;
	}
	public void setUserpriv(String userpriv) {
		this.userpriv = userpriv;
	}
	private int nFlag=0;
	public YkcardViewSubclass(Connection conn,int statYear,int statMonth,int ctimes,String userbase,String nId)
	{
		this.statYear=statYear;
		this.statMonth=statMonth;
		this.ctimes=ctimes;
		this.userbase=userbase;
		this.nId=nId;
		this.conn=conn;
	}
	public YkcardViewSubclass(Connection conn,int statYear,int statMonth,int ctimes,String userbase,String nId,UserView userview)
	{
		this.statYear=statYear;
		this.statMonth=statMonth;
		this.ctimes=ctimes;
		this.userbase=userbase;
		this.nId=nId;
		this.conn=conn;
		this.userview=userview;
	}
	public YkcardViewSubclass(Connection conn,int statYear,int statMonth,int ctimes,String userbase,String nId,UserView userview,boolean needfilter)
	{
		this.statYear=statYear;
		this.statMonth=statMonth;
		this.ctimes=ctimes;
		this.userbase=userbase;
		this.nId=nId;
		this.conn=conn;
		this.userview=userview;
		this.needfilter=needfilter;
	}
	public void getXmlSubdomain(String subdomain)
	{
		XmlSubdomain xmlSubdomain=new XmlSubdomain(subdomain);
		xmlSubdomain.getParaAttribute();
		this.domain=xmlSubdomain;		
		getFields();		
		getFieldContent();
		getAverageWidths();
		getLh();
		getLs();
		getColheadheight();
		getDatarowcount();
		getCustomcolhead();
	}
	public void getXmlSubdomain(String subdomain,RGridView rgrid)
	{
		XmlSubdomain xmlSubdomain=new XmlSubdomain(subdomain);
		xmlSubdomain.getParaAttribute();
		this.domain=xmlSubdomain;
		getCustomcolhead();//得到是否自动与子集上方单元格对其
		getFields(rgrid);
		setFieldwhithForCustomcolhead(rgrid,this.conn);
		getFieldContent();
		getAverageWidths();
		getLh();
		getLs();
		getColheadheight();
		getDatarowcount();
		getCustomcolhead();
	}
	public YkcardViewSubclass()
	{}
	/**
	 * 得到操作子集的字段
	 * @return
	 */
	public boolean  getFields(RGridView rgrid)
	{
		boolean isCorrect=true;
		String fileds=this.domain.getFields();
		String filedArray[]=fileds.split("`");
		if(filedArray==null||filedArray.length<=0) {
            return false;
        }
		String field="";
		this.fieldlist=new ArrayList();
		GetCardCellValue getCardCellValue=new GetCardCellValue();
		String isView=getCardCellValue.getISVIEWForCexpress(rgrid.getCexpress());
        if(isView!=null&& "1".equals(isView))//走试图
		{
        	for(int i=0;i<filedArray.length;i++)
			{
				field=filedArray[i];			
				if(field!=null&&field.length()>0)
				{
					this.fieldlist.add(field);
				}
			}
		}else
		{
			for(int i=0;i<filedArray.length;i++)
			{
				field=filedArray[i];			
				if(field!=null&&field.length()>0)
				{
					 if(this.nFlag!=5)
					 {
						 FieldItem fielditem=(FieldItem)DataDictionary.getFieldItem(field);
						 if(fielditem==null) {
                             continue;
                         }
						 if("1".equals(fielditem.getUseflag())&&this.nFlag!=5)
						 {
							 if(this.userview!=null&&"selfinfo".equalsIgnoreCase(userpriv))
							 {
								 if((fieldpurv!=null&& "1".equals(fieldpurv))||!"0".equals(this.userview.analyseFieldPriv(field,0)))
								 {
									 this.fieldlist.add(field);
								 }
							 }else {
                                 this.fieldlist.add(field);
                             }
						 }
					 }else
					 {
						 this.fieldlist.add(field);
					 }
						
				}
					
			}
		}		
		return isCorrect;
	}
	public boolean  getFields()
	{
		boolean isCorrect=true;
		String fileds=this.domain.getFields();
		String filedArray[]=fileds.split("`");
		if(filedArray==null||filedArray.length<=0) {
            return false;
        }
		String field="";
		this.fieldlist=new ArrayList();
		for(int i=0;i<filedArray.length;i++)
		{
			field=filedArray[i];			
			if(field!=null&&field.length()>0)
			{
				 if(this.nFlag!=5)
				 {
					 FieldItem fielditem=(FieldItem)DataDictionary.getFieldItem(field);
					 if(fielditem==null) {
                         this.fieldlist.add(field);
                     } else
					 if("1".equals(fielditem.getUseflag())&&this.nFlag!=5)
					 {
						 if(this.userview!=null&&"selfinfo".equalsIgnoreCase(userpriv))
						 {
							 if((fieldpurv!=null&& "1".equals(fieldpurv))||!"0".equals(this.userview.analyseFieldPriv(field,0)))
							 {
								 this.fieldlist.add(field);
							 }
						 }else {
                             this.fieldlist.add(field);
                         }
					 }
				 }else
				 {
					 this.fieldlist.add(field);
				 }
					
			}
				
		}		
		return isCorrect;
	}
	public void getFieldContent()
	{
		if(this.fieldlist==null||this.fieldlist.size()<=0) {
            return;
        }
		String field="";
		this.sumwidth=0;
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.conn);		
		try
		{
			for(int i=0;i<fieldlist.size();i++)
			{
				HashMap one_hash=new HashMap();
				field=this.fieldlist.get(i).toString();
				one_hash=this.domain.getFieldAttribute(field);
				hash.put(field,one_hash);
				String width=(String)one_hash.get("width");
				if(width!=null&&width.length()>0)
				{
					int in_w=Integer.parseInt(width);
					this.sumwidth=in_w+this.sumwidth;
				}
				StringBuffer sql=new StringBuffer();
	    		sql.append("select * from fielditem where itemid ='"+field+"'");
	    		sql.append(" and fieldsetid='"+this.domain.getSetname().toUpperCase()+"'");
	    		rs=dao.search(sql.toString());
	    		if(rs.next())
	    		{
	    			YkcardSubclassFieldBean bean=new YkcardSubclassFieldBean();
	    			bean.setName(field);		
	    			bean.setNeed((String)one_hash.get("need"));
	    			bean.setDefaultt((String)one_hash.get("default"));
	    			bean.setPre((String)one_hash.get("pre"));
	    			bean.setSlop((String)one_hash.get("slop"));
	    			bean.setAlign((String)one_hash.get("align"));
	    			bean.setFieldsetid(this.domain.getSetname());
	    			bean.setItemlength(rs.getString("itemlength"));
	    			bean.setItemtype(rs.getString("itemtype"));
	    			bean.setCodesetid(rs.getString("codesetid"));
	    			this.cellhash.put(field,bean);
	    		}else
	    		{
	    			sql.setLength(0);
	    			sql.append("select * from t_hr_busifield where itemid ='"+field+"'");
	    			sql.append(" and upper(fieldsetid)='"+this.domain.getSetname().toUpperCase()+"'");
	    			rs=dao.search(sql.toString());
		    		if(rs.next())
		    		{
		    			YkcardSubclassFieldBean bean=new YkcardSubclassFieldBean();
		    			bean.setName(field);		
		    			bean.setNeed((String)one_hash.get("need"));
		    			bean.setDefaultt((String)one_hash.get("default"));
		    			bean.setPre((String)one_hash.get("pre"));
		    			bean.setSlop((String)one_hash.get("slop"));
		    			bean.setAlign((String)one_hash.get("align"));
		    			bean.setFieldsetid(this.domain.getSetname());
		    			bean.setItemlength(rs.getString("itemlength"));
		    			bean.setItemtype(rs.getString("itemtype"));
		    			bean.setCodesetid(rs.getString("codesetid"));
		    			this.cellhash.put(field,bean);
		    		}else
		    		{
		    			if("p04".equalsIgnoreCase(this.domain.getSetname()))
		    			{
		    				
		    				if("TargetScore".equalsIgnoreCase(field)||field.indexOf("score_")!=-1)
		    				{
		    					YkcardSubclassFieldBean bean=new YkcardSubclassFieldBean();
		    					bean.setName(field);		
				    			bean.setNeed((String)one_hash.get("need"));
				    			bean.setDefaultt((String)one_hash.get("default"));
				    			bean.setPre((String)one_hash.get("pre"));
				    			bean.setSlop((String)one_hash.get("slop"));
				    			bean.setAlign((String)one_hash.get("align"));
				    			bean.setFieldsetid(this.domain.getSetname());
				    			bean.setItemlength("10");
				    			bean.setItemtype("N");
				    			bean.setCodesetid("0");
				    			this.cellhash.put(field,bean);
		    				}
		    				if(field.indexOf("reasons_")!=-1)
		    				{
		    					YkcardSubclassFieldBean bean=new YkcardSubclassFieldBean();
		    					bean.setName(field);		
				    			bean.setNeed((String)one_hash.get("need"));
				    			bean.setDefaultt((String)one_hash.get("default"));
				    			bean.setPre((String)one_hash.get("pre"));
				    			bean.setSlop((String)one_hash.get("slop"));
				    			bean.setAlign((String)one_hash.get("align"));
				    			bean.setFieldsetid(this.domain.getSetname());
				    			//bean.setItemlength("10");
				    			bean.setItemtype("A");
				    			bean.setCodesetid("0");
				    			this.cellhash.put(field,bean);
		    				}
		    				if("summarizes".equalsIgnoreCase(field))
		    				{
		    					YkcardSubclassFieldBean bean=new YkcardSubclassFieldBean();
		    					bean.setName(field);		
				    			bean.setNeed((String)one_hash.get("need"));
				    			bean.setDefaultt((String)one_hash.get("default"));
				    			bean.setPre((String)one_hash.get("pre"));
				    			bean.setSlop((String)one_hash.get("slop"));
				    			bean.setAlign((String)one_hash.get("align"));
				    			bean.setFieldsetid(this.domain.getSetname());
				    			bean.setItemlength("0");
				    			bean.setItemtype("M");
				    			bean.setCodesetid("0");
				    			this.cellhash.put(field,bean);
		    				}
		    			}else if("per_key_event".equalsIgnoreCase(this.domain.getSetname()))
		    			{
		    				YkcardSubclassFieldBean bean=new YkcardSubclassFieldBean();	    					
		    				bean.setName(field);		
			    			bean.setNeed((String)one_hash.get("need"));
			    			bean.setDefaultt((String)one_hash.get("default"));
			    			bean.setPre((String)one_hash.get("pre"));
			    			bean.setSlop((String)one_hash.get("slop"));
			    			bean.setAlign((String)one_hash.get("align"));
			    			bean.setFieldsetid(this.domain.getSetname());
			    			if("A0101".equalsIgnoreCase(field))
			    			{
			    				bean.setItemlength("30");
				    			bean.setItemtype("A");
				    			bean.setCodesetid("0");
			    			}else if("B0110".equalsIgnoreCase(field))
			    			{
			    				bean.setItemlength("30");
				    			bean.setItemtype("A");
				    			bean.setCodesetid("UN");
			    			}else if("E0122".equalsIgnoreCase(field))
			    			{
			    				bean.setItemlength("30");
				    			bean.setItemtype("A");
				    			bean.setCodesetid("UM");
			    			}else if("key_event".equalsIgnoreCase(field))
			    			{
			    				bean.setItemlength("0");
				    			bean.setItemtype("M");
				    			bean.setCodesetid("0");
			    			}else if("score".equalsIgnoreCase(field))
			    			{
			    				bean.setItemlength("10");
				    			bean.setItemtype("N");
				    			bean.setCodesetid("0");
			    			}else if("busi_date".equalsIgnoreCase(field))
			    			{
			    				bean.setItemlength("10");
				    			bean.setItemtype("D");
				    			bean.setCodesetid("0");
			    			}else if("point_id".equalsIgnoreCase(field))
			    			{
			    				bean.setItemlength("30");
				    			bean.setItemtype("A");
				    			bean.setCodesetid("0");
			    			}else if("pointname".equalsIgnoreCase(field))
			    			{
			    				bean.setItemlength("0");
				    			bean.setItemtype("M");
				    			bean.setCodesetid("0");
			    			}else if("Description".equalsIgnoreCase(field))
			    			{
			    				bean.setItemlength("0");
				    			bean.setItemtype("M");
				    			bean.setCodesetid("0");
			    			}			    			
			    			this.cellhash.put(field,bean);
		    			}
		    		}
	    		}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			rs=null;
			/*if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
		}
		
	}
	/**
	 * 求平均宽度
	 * @return
	 */
	public void getAverageWidths()
	{
		//int fs=this.fieldlist.size();
		//this.avewidth=this.sumwidth/fs;
		this.avewidth=this.fact_width/this.sumwidth;
	}
    public String viewSubClass(String infokind,String userbase, Connection conn,UserView userview,RGridView rgrid,String disting_pt,byte nFlag)
    {
    	String colhead=this.domain.getColhead();
    	String setname=this.domain.getSetname();    	
    	StringBuffer html=new StringBuffer();
    	int topn;                                                  //单元格的上边位置
        int leftn;                                                 //单元格的左边位置
        int heights;                                               //单元格的高
        int widthn;     	
    	this.fontweight=rgrid.getFonteffect();
	    if(fontweight !=null && "2".equals(fontweight))         //字体是否时粗体
        {
            fontweight="bold";
        } else {
            fontweight="normal";
        }
	    if("800".equals(disting_pt))
        {     
	             leftn=(int)Float.parseFloat(rgrid.getRleft("1"));
	             topn=(int)Float.parseFloat(rgrid.getRtop("1"));
	             widthn=(int)Float.parseFloat(rgrid.getRwidth("1")) + 1;
	             heights=(int)Float.parseFloat(rgrid.getRheight("1"))  +1;
        }
        else
        {
	             leftn=(int)Float.parseFloat(rgrid.getRleft());
	             topn=(int)Float.parseFloat(rgrid.getRtop());
	             widthn=(int)Float.parseFloat(rgrid.getRwidth()) + 1;
	             heights=(int)Float.parseFloat(rgrid.getRheight()) +1;
        }
    	float para_array[]=viewParaSrray(widthn);
    	html.append("<table border=\"0\" cellspacing=\"0\" valign=\"top\" width=\"100%\" align=\"left\" cellpadding=\"0\" class=\"ListTable\">");
    	if(colhead!=null&& "true".equals(colhead))//显示字段
    	{
    		html.append(viewColumnsName(para_array,rgrid,disting_pt));
    	}
    	String sql="";
    	StringBuffer fields=new StringBuffer();
        for(int i=0;i<this.fieldlist.size();i++)
        {
        	fields.append(this.fieldlist.get(i).toString()+",");
        }
        if(fields.length()>0) {
            fields.setLength(fields.length()-1);
        }
        String cBase="";          //人员库
    	switch(nFlag)
		{	
			//人员库
			case 0:
			{
				sql=getPersonSql(rgrid,userbase,userview,nFlag,PERSONKEYTYPE,fields.toString());				
				break;
			}//岗位库
			case 4:
			{
				if("1".equals(infokind))
				{
					nId=getOrgnId("4",nId,userbase);				
				}else
				{
					cBase="K";
				}
				sql=getPostSql(rgrid,userbase,userview,nFlag,POSTKEYTYPE,fields.toString());
				break;
			}
//			单位库
			case 2:
			{
				if("1".equals(infokind))
				{
					nId=getOrgnId("2",nId,userbase);		
				}else
				{
					cBase="B";
				}
				sql=getUnitSql(rgrid,userbase,userview,nFlag,UNITKEYTYPE,fields.toString());
				break;
			}
		}
    	if(sql!=null&&sql.length()>0) {
            html.append(viewContentValue(sql,conn,para_array,rgrid,disting_pt));
        }
    	html.append("</table>");
    	return html.toString();
    }
    public String viewColumnsName(float para_array[],RGridView rgrid,String disting_pt)
    {
    	StringBuffer html=new StringBuffer(); 
    	int heights=(Integer.parseInt(rgrid.getFontsize())+10);
    	html.append("<tr>");
    	MadeFontsizeToCell mc=new MadeFontsizeToCell(); 
    	for(int i=0;i<this.fieldlist.size();i++)
    	{
    		String field=this.fieldlist.get(i).toString();
    		HashMap hash=(HashMap)this.hash.get(field);
    		String class_str="";    		
    		if(i!=this.fieldlist.size()-1) {
                class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0",this.lh,this.domain.getHl(),this.domain.getVl());
            } else {
                class_str= new MadeCardCellLine().GetCardCellLineShowcss("0","0","0",this.lh,this.domain.getHl(),this.domain.getVl());
            }
    		int widthn=((int)para_array[i]);    		
    		this.fontsize=mc.ReDrawLitterRect(widthn,heights,(String)hash.get("title"),Integer.parseInt(rgrid.getFontsize()),rgrid.getFontName(),disting_pt,rgrid.getField_type(),rgrid.getSlope());
    		///this.fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights,(String)hash.get("title"));
    		html.append("<td align=\"center\" valign=\"middle\" width=\""+widthn+"\" height=\""+heights+"\" class=\""+class_str+"\">");
    		html.append("<font  color=\"#0000FF\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + this.fontsize + "pt\">");
    		html.append(hash.get("title"));
    		html.append("</font>");
    		html.append("</td>");
    	}
    	html.append("</tr>");
    	return html.toString();
    }
    public String viewContent(float para_array[],RGridView rgrid,String userbase,String nid)
    {
    	StringBuffer html=new StringBuffer(); 
    	
    	return html.toString();    	
    }
    
    /*
     * 循环保存表格宽度
     */
    public void forWidth(float[] para_array)
    {
    	int sumwidth=0;    	
    	for(int i=0;i<this.fieldlist.size();i++)
    	{   
    		String field=this.fieldlist.get(i).toString();
    		HashMap hash=(HashMap)this.hash.get(field);
    		String width1_str=(String)hash.get("width");    		
    		int width1=Integer.parseInt(width1_str);      		
    		int width=0;
    		if(this.customcolhead!=null&& "true".equalsIgnoreCase(this.customcolhead))
			{
				HashMap headwidth=this.customcolheadwidth;
				String width_str=(String)headwidth.get(field);
				if(width_str!=null&&width_str.length()>0)
				{
					width=(int)Float.parseFloat(width_str);
					sumwidth=sumwidth+width;
				}else{
					if(i!=this.fieldlist.size()-1)
	        		{
	                 //width=(overall_wodth_ave*width1)/this.avewidth; 
	        			//width=Math.round((width1*this.avewidth));
	        			width=(int)(width1*this.avewidth);
	           		    sumwidth=sumwidth+width;
	        		}
	        		else
	        		{
	        			//width=overall_wodth-(int)sumwidth;
	        			width=this.fact_width-sumwidth;
	        		}
				}					
			}    		
    		else
    		{
    			if(i!=this.fieldlist.size()-1)
        		{
                 //width=(overall_wodth_ave*width1)/this.avewidth; 
        			//width=Math.round((width1*this.avewidth));
        			width=(int)(width1*this.avewidth);
           		    sumwidth=sumwidth+width;
        		}
        		else
        		{
        			//width=overall_wodth-(int)sumwidth;
        			width=this.fact_width-sumwidth;
        		}
    		}
    		para_array[i]= width;
    	}
    }
    public float[] viewParaSrray(float overall_wodth)
    {
    	
    	float[] para_array = null;
    	if (this.domain.getMultimedia()==null|| "".equals(this.domain.getMultimedia())|| "false".equals(this.domain.getMultimedia())||"false".equals(print_File)) {
    		para_array =new float[this.fieldlist.size()];
    		//float para_array[]=new float[this.fieldlist.size()];
        	//float overall_wodth_ave=overall_wodth/this.fieldlist.size();
    		//调用保存宽度方法
    		this.forWidth(para_array);
		}else if (("true".equals(this.domain.getMultimedia()))&&"true".equals(print_File)) {
			para_array=new float[this.fieldlist.size()+1];
			//调用保存宽度的方法
			this.forWidth(para_array);
			
			int width_file=40;//固定附件的宽度
			int width_file1=0;
			int sumwidth1=0;
			for (int i = 0; i < this.fieldlist.size(); i++) {
				width_file1=(int)(width_file/this.fieldlist.size());
				para_array[i]=para_array[i]-width_file1;
				sumwidth1=sumwidth1+(int)para_array[i];
			}
			para_array[this.fieldlist.size()]=overall_wodth-sumwidth1;
		}
    	
    	return para_array;
    }
    public void viewContent(RGridView rgrid)
    {
        
    }
    public String getPersonSql(RGridView rgrid,String cBase,UserView userview,byte nFlag,String keyType,String cFldnames)
    {
    	StringBuffer cSql=new StringBuffer();
    	String cName=this.domain.getSetname();
        String cStr1=this.userbase+this.domain.getSetname();
        GetCardCellValue getCardCellValue=new GetCardCellValue();
        String isView=getCardCellValue.getISVIEWForCexpress(rgrid.getCexpress());
        if(isView!=null&& "1".equals(isView))//走试图
		{
        	rgrid.setIsView(isView);
        	getCardCellValue.getViewForCexpress(rgrid);
			cStr1=rgrid.getCSetName();			
			cSql.append(getSubsetSql(cStr1,rgrid,userbase,userview,nFlag,PERSONKEYTYPE,cFldnames));
		}else  if(!"A01".equals(cName))   //人员子集
		{
        	cSql.append(getSubsetSql(cStr1,rgrid,userbase,userview,nFlag,PERSONKEYTYPE,cFldnames));
        }else
        {
//        	是主集的情况生成的sql语句
        	cSql.append("SELECT ");        	
        	cSql.append(cFldnames);
        	cSql.append(" From ");
        	cSql.append(cStr1);
		    cSql.append(" WHERE ");
		    cSql.append(cStr1);
		    cSql.append(".");
		    cSql.append("A0100='");
		    cSql.append(nId);
		    cSql.append("'");
        }
        return cSql.toString();
    }
    public String getSubsetSql(String cName,RGridView rgrid,String cBase,UserView userview,byte nFlag,String keyType,String cFldnames)
    {
    	ResultSet rset = null;
	    ContentDAO dao = null;
    	StringBuffer cSql=new StringBuffer();
        String cStr1=cName;
        StringBuffer cSql1=new StringBuffer();
        GetCardCellValue cardcell=new GetCardCellValue();
        cardcell.setBizDate(this.bizDate);
        String cValue="";	
        boolean isCorrect=false;  //isCorrect 暂时未用 先用于判断是否是视图  //ORDER BY I9999  走试图取消按照I9999 排序
        GetCardCellValue getCardCellValue=new GetCardCellValue();
        String isView=getCardCellValue.getISVIEWForCexpress(rgrid.getCexpress());
        if(isView!=null&&"1".equals(isView)){
        	isCorrect=true;
        }
        int nCur;	
        int nI=0;
        try
        {
        	cSql.append("SELECT ");	        	
			cSql.append(cFldnames);
			cSql.append(" From ");
			cSql.append(cStr1);			
			cSql.append(" WHERE ");			
			cSql.append(cStr1);
			cSql.append(".");
			cSql.append(keyType);
			cSql.append("='");
			cSql.append(nId);
			cSql.append("'");
			if("1".equals(rgrid.getIsView()))
			{
				cSql.append(" and upper("+cStr1+".nbase)='"+cBase.toUpperCase()+"'");
			}
//        	定位人员编号
    		cSql1.append("SELECT ");
    		cSql1.append(cStr1);
    		cSql1.append(".I9999 From ");
    		cSql1.append(cStr1);
        	if(rgrid.getMode() !=null && rgrid.getMode().length()>0 && Integer.parseInt(rgrid.getMode()) >=5)		{
        		
    			//有一个cell没有编写上 带有条件的
    			cValue=cardcell.GetSqlCond(rgrid,cBase,userview,nFlag);
    			if(cValue==null||cValue.length()<=0) {
                    cValue="1=1";
                }
    			cSql1.append(" WHERE ");
    			cSql1.append(cValue);
    			cSql1.append(" AND ");			
    			cSql1.append(cStr1);
    			cSql1.append(".");
    			cSql1.append(keyType);
    			cSql1.append("='");
    			cSql1.append(nId+"'");
    			cSql1.append(searchDateSql);
    			if(!isCorrect)
    			{
    			  cSql1.append(" ORDER BY ");
    			  cSql1.append(cStr1);
    			  cSql1.append(".I9999");
    			}
    		}	
    		else
    		{
    			//组合子集中所有纪录的sql
    			cSql1.append(" WHERE ");			
    			cSql1.append(cStr1);
    			cSql1.append(".");
    			cSql1.append(keyType);
    			cSql1.append("='");
    			cSql1.append(nId+"'");
    			cSql1.append(searchDateSql);
    			if(!isCorrect)
    			{
    			  cSql1.append(" ORDER BY ");
    			  cSql1.append(cStr1);
    			  cSql1.append(".I9999");
    			}
    		}	
    		int[] narrId=new int[1000];  // FIXME 记录超1000会有问题
    			try{
    				//conn = AdminDb.getConnection();
    				dao = new ContentDAO(this.conn);
    				//查询出该人员的该子集的所有纪录
    				rset=dao.search(cSql1.toString());
    				for(nI=0;rset.next();nI++)
    				{
    					//把各个纪录放到数组中以便查询出符合条件的某条纪录
    					narrId[nI +1]=Integer.parseInt(rset.getString("I9999"));
    					if(narrId[nI + 1]<NMIN) {
                            NMIN=narrId[nI + 1];
                        }
    					if(narrId[nI +1]>NMAX) {
                            NMAX=narrId[nI + 1];
                        }
    				}
    				
    			}catch (SQLException sqle){
    	            sqle.printStackTrace();
                }
                
                finally{
                	PubFunc.closeDbObj(rset);
                }
        	if(rgrid.getMode() !=null && rgrid.getMode().length()>0)
    		{
    			switch(Integer.parseInt(rgrid.getMode()))
    			{					        		
    				case 0:
    				{
    				   cSql.append(" AND ");
    				   cSql.append(cStr1);
    				   cSql.append(".I9999=");
    				   if(nI>=rgrid.getRcount()) {
                           cSql.append(narrId[nI-rgrid.getRcount()+1]);
                       } else {
                           cSql.append(0);
                       }
    				   cSql.append(searchDateSql);
    				   break;
    				}
    				case 5:
    				{
    					cSql.append(" AND ");
    				    cSql.append(cStr1);
    					cSql.append(".I9999=");
    					if(nI>=rgrid.getRcount()) {
                            cSql.append(narrId[nI-rgrid.getRcount() +1]);
                        } else {
                            cSql.append(0);
                        }
    					cSql.append(searchDateSql);
    					break;
    				}
    				case 1:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append(".I9999>=");
    					if(nI>=rgrid.getRcount()) {
                            cSql.append(narrId[nI-rgrid.getRcount()+1]);
                        } else {
                            cSql.append(0);
                        }
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append(".I9999<=");
    					cSql.append(NMAX);
    					cSql.append(searchDateSql);
    					if(!isCorrect)
    					{
    					  cSql.append(" ORDER BY ");
    					  cSql.append(cStr1);
    					  cSql.append(".I9999");;
    					}
    					break;
    				}
    				case 2:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    				    cSql.append(".I9999=");
    					cSql.append(narrId[rgrid.getRcount()]);
    					cSql.append(searchDateSql);
    					break;
    				}
    				case 7:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append(".I9999=");
    					cSql.append(narrId[rgrid.getRcount()]);
    					cSql.append(searchDateSql);
    					break;
    				}
    				case 3:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append(".I9999<=");
    					if(nI>=rgrid.getRcount()) {
                            cSql.append(narrId[rgrid.getRcount()]);
                        } else {
                            cSql.append(NMAX);
                        }
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append(".I9999>=");
    					cSql.append(NMIN);
    					cSql.append(searchDateSql);
    					if(!isCorrect)
    					{
                          cSql.append(" ORDER BY ");
                          cSql.append(cStr1);
                          cSql.append(".I9999");
    					}
    					break;
    				}
    				case 4:
    				{
    					if(!"".equals(cardcell.GetSqlCond(rgrid,cBase,userview,nFlag))){
    						cSql.append(" AND ");
    						cSql.append(cardcell.GetSqlCond(rgrid,cBase,userview,nFlag));
						}
    					cSql.append(searchDateSql);
    					//cSql.append("A0100 in(select "+cBase+"A01.a0100 from "+cBase+"A01 where"+cardcell.GetSqlCond(rgrid,cBase,userview,nFlag)+")");
    					if(!isCorrect)
    					{
    					  cSql.append(" ORDER BY ");
    					  cSql.append(cStr1);
    					  cSql.append(".I9999");
    					}
    					break;
    				}
    				case 6:
    				{
    					
    					if(nI>=rgrid.getRcount()) {
                            nCur=nI-rgrid.getRcount()+1;
                        } else {
                            nCur=1;
                        }
    					 cSql.append(" AND ");
    					 cSql.append(cStr1);
    					 cSql.append(".I9999 IN(-1");
    					 for(int nK=nCur;nK<=nI;nK++)
    					 {
    						cSql.append("," + narrId[nK]);
    					 }
    					if(nI>0)
    					{
    					  //cValue=cSql.toString().substring(0,cSql.toString().length()-1);  //去掉最后一个字符
    					  //cSql.delete(0,cSql.length());
    				      //cSql.append(cValue);
    				    }
    				    cSql.append(")" );
    				    cSql.append(searchDateSql);
    				    if(!isCorrect)
    					{
    				       cSql.append(" ORDER BY ");
    				       cSql.append(cStr1);
    				       cSql.append(".I9999");
    					}
    					break;
    				}
    				case 8:
    				{
    					if(nI>=rgrid.getRcount()) {
                            nCur=rgrid.getRcount();
                        } else {
                            nCur=nI;
                        }
    					 cSql.append(" AND ");
    					 cSql.append(cStr1);
    					 cSql.append(".I9999 IN(-1");
    					  for(int nK=1;nK<=nCur;nK++)
    					  {
    						cSql.append( "," + narrId[nK]);
    					  }
    					  if(nI>0){
    					    // cValue=cSql.toString().substring(0,cSql.toString().length()-1);
    					    // cSql.delete(0,cSql.length());
    					    // cSql.append(cValue);
    					  }
    					  cSql.append(") " );
    					  cSql.append(searchDateSql);
    					  if(!isCorrect)
    				      {
    					       cSql.append(" ORDER BY ");
    					       cSql.append(cStr1);
    					       cSql.append(".I9999");
    					  }
    					  break;
    				}					        		
    			}
    		}
        }catch(Exception e)
        {
          e.printStackTrace();	
        }finally{
        	rset=null;
        }
        
        return cSql.toString();
    }
    public String getZpSubsetSql(String cName,RGridView rgrid,String cBase,UserView userview,byte nFlag,String keyType,String cFldnames)
    {
    	StringBuffer cSql=new StringBuffer();
    	String cStr1= "zp_pos_tache";
        String cStr=cBase+"A01";
        StringBuffer cSql1=new StringBuffer();
        GetCardCellValue cardcell=new GetCardCellValue();
        cardcell.setBizDate(this.bizDate);
        String cValue="";	
        boolean isCorrect=false;
        int nCur;	
        int nI=0;
        String zp_base=getZPBASE(conn);//招聘人员库
        String I9999="I9999";
        try
        {
        	cSql.append("SELECT ");	        	
			cSql.append(cFldnames);
			cSql.append(" From ");
			cSql.append(cStr+",Z03,zp_pos_tache");			
			cSql.append(" WHERE ");	
			cSql.append(cStr);
			cSql.append(".A0100 = zp_pos_tache.A0100 and zp_pos_tache.zp_pos_id=z03.z0301 and ");			
			cSql.append(cStr);
			cSql.append(".A0100");				
			cSql.append("='");
			cSql.append(nId);
			cSql.append("'");
			if(zp_base!=null&&!zp_base.equalsIgnoreCase(cBase)) {
                cSql.append(" and 1=2");
            }
//        	定位人员编号			
    		cSql1.append("SELECT zp_pos_tache.theNumber From zp_pos_tache, "+cStr);
    		cSql1.append(" where "+cStr+".A0100=zp_pos_tache.A0100 and "+cStr1+".A0100='"+nId+"'");
    		if(zp_base!=null&&!zp_base.equalsIgnoreCase(cBase)) {
                cSql1.append(" and 1=2");
            }
    		I9999="theNumber";
        	if(rgrid.getMode() !=null && rgrid.getMode().length()>0 && Integer.parseInt(rgrid.getMode()) >=5)		{
        		
    			//有一个cell没有编写上 带有条件的
    			cValue=cardcell.GetSqlCond(rgrid,cBase,userview,nFlag);
    			if(cValue==null||cValue.length()<=0) {
                    cValue="1=2";
                } else {
                    cSql1.append(" and "+cValue);
                }
    			if(!isCorrect)
    			{
    			  cSql1.append(" ORDER BY ");
    			  cSql1.append(cStr1);
    			  cSql1.append("."+I9999);
    			}
    		}	
    		else
    		{
    			if(!isCorrect)
    			{
    			  cSql1.append(" ORDER BY ");
    			  cSql1.append(cStr1);
    			  cSql1.append("."+I9999);
    			}
    		}	
    		ResultSet rset = null;
    	    ContentDAO dao = null;
    		Connection conn = null;
    		int[] narrId=new int[1000];  // FIXME 记录超1000会有问题
    			try{
    				conn = AdminDb.getConnection();
    				dao = new ContentDAO(conn);
    				//查询出该人员的该子集的所有纪录
    				rset=dao.search(cSql1.toString());
    				for(nI=0;rset.next();nI++)
    				{
    					//把各个纪录放到数组中以便查询出符合条件的某条纪录
    					narrId[nI +1]=Integer.parseInt(rset.getString(I9999));
    					if(narrId[nI + 1]<NMIN) {
                            NMIN=narrId[nI + 1];
                        }
    					if(narrId[nI +1]>NMAX) {
                            NMAX=narrId[nI + 1];
                        }
    				}
    				
    			}catch (SQLException sqle){
    	            sqle.printStackTrace();
                }
                catch (GeneralException ge){
    	            ge.printStackTrace();
                }
                finally{
    	          try{
    		        if (rset != null){
    			     rset.close();
    		        }
    		       if (conn != null){
    			     conn.close();
    		       }
    	          }catch (SQLException sql){
    		         sql.printStackTrace();
    	         }
                }
        	if(rgrid.getMode() !=null && rgrid.getMode().length()>0)
    		{
    			switch(Integer.parseInt(rgrid.getMode()))
    			{					        		
    				case 0:
    				{
    				   cSql.append(" AND ");
    				   cSql.append(cStr1);
    				   cSql.append("."+I9999+"=");
    				   if(nI>=rgrid.getRcount()) {
                           cSql.append(narrId[nI-rgrid.getRcount()+1]);
                       } else {
                           cSql.append(0);
                       }
    				  
    				   break;
    				}
    				case 5:
    				{
    					cSql.append(" AND ");
    				    cSql.append(cStr1);
    					cSql.append("."+I9999+"=");
    					if(nI>=rgrid.getRcount()) {
                            cSql.append(narrId[nI-rgrid.getRcount() +1]);
                        } else {
                            cSql.append(0);
                        }
    					break;
    				}
    				case 1:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append("."+I9999+">=");
    					if(nI>=rgrid.getRcount()) {
                            cSql.append(narrId[nI-rgrid.getRcount()+1]);
                        } else {
                            cSql.append(0);
                        }
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append("."+I9999+"<=");
    					cSql.append(NMAX);
    					if(!isCorrect)
    					{
    					  cSql.append(" ORDER BY ");
    					  cSql.append(cStr1);
    					  cSql.append("."+I9999+"");;
    					}
    					break;
    				}
    				case 2:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    				    cSql.append("."+I9999+"=");
    					cSql.append(narrId[rgrid.getRcount()]);
    					break;
    				}
    				case 7:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append("."+I9999+"=");
    					cSql.append(narrId[rgrid.getRcount()]);
    					break;
    				}
    				case 3:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append("."+I9999+"<=");
    					if(nI>=rgrid.getRcount()) {
                            cSql.append(narrId[rgrid.getRcount()]);
                        } else {
                            cSql.append(NMAX);
                        }
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append("."+I9999+">=");
    					cSql.append(NMIN);
    					if(!isCorrect)
    					{
                          cSql.append(" ORDER BY ");
                          cSql.append(cStr1);
                          cSql.append("."+I9999+"");
    					}
    					break;
    				}
    				case 4:
    				{
    					cSql.append(" AND ");
    					cSql.append(cardcell.GetSqlCond(rgrid,cBase,userview,nFlag));
    					if(!isCorrect)
    					{
    					  cSql.append(" ORDER BY ");
    					  cSql.append(cStr1);
    					  cSql.append("."+I9999+"");
    					}
    					break;
    				}
    				case 6:
    				{
    					
    					if(nI>=rgrid.getRcount()) {
                            nCur=nI-rgrid.getRcount()+1;
                        } else {
                            nCur=1;
                        }
    					 cSql.append(" AND ");
    					 cSql.append(cStr1);
    					 cSql.append("."+I9999+" IN(-1");
    					 for(int nK=nCur;nK<=nI;nK++)
    					 {
    						cSql.append("," + narrId[nK]);
    					 }
    					if(nI>0)
    					{
    					  //cValue=cSql.toString().substring(0,cSql.toString().length()-1);  //去掉最后一个字符
    					  //cSql.delete(0,cSql.length());
    				      //cSql.append(cValue);
    				    }
    				    cSql.append(")" );
    				    if(!isCorrect)
    					{
    				       cSql.append(" ORDER BY ");
    				       cSql.append(cStr1);
    				       cSql.append("."+I9999+"");
    					}
    					break;
    				}
    				case 8:
    				{
    					if(nI>=rgrid.getRcount()) {
                            nCur=rgrid.getRcount();
                        } else {
                            nCur=nI;
                        }
    					 cSql.append(" AND ");
    					 cSql.append(cStr1);
    					 cSql.append("."+I9999+" IN(-1");
    					  for(int nK=1;nK<=nCur;nK++)
    					  {
    						cSql.append( "," + narrId[nK]);
    					  }
    					  if(nI>0){
    					    // cValue=cSql.toString().substring(0,cSql.toString().length()-1);
    					    // cSql.delete(0,cSql.length());
    					    // cSql.append(cValue);
    					  }
    					  cSql.append(")" );
    					  if(!isCorrect)
    				      {
    					       cSql.append(" ORDER BY ");
    					       cSql.append(cStr1);
    					       cSql.append("."+I9999+"");
    					  }
    					  break;
    				}					        		
    			}
    		}
        }catch(Exception e)
        {
          e.printStackTrace();	
        }
        return cSql.toString();
    }
    /**
     * 得到招聘人员库前缀
     * @param conn
     * @return
     */
    private String getZPBASE(Connection conn)
    {
  	  String sql="select str_value from constant where Upper(constant)='ZP_DBNAME'";
  	  ContentDAO dao=new ContentDAO(conn);
  	  RowSet rs=null;
  	  String zpbase="";
  	  try {
  		rs=dao.search(sql);
  		if(rs.next()) {
            zpbase=rs.getString("str_value");
        }
  	} catch (SQLException e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();
  	}finally
  	{
  		if(rs!=null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
  	}
  	return zpbase;
    }
    public String viewContentValue(String sql,Connection conn,float para_array[],RGridView rgrid,String disting_pt)
    {
    	StringBuffer html=new StringBuffer();
    	RowSet rs=null;
    	ContentDAO dao=new ContentDAO(this.conn);
    	MadeFontsizeToCell mc=new MadeFontsizeToCell(); 
    	int heights=(Integer.parseInt(rgrid.getFontsize())+10);
    	float all_height=Float.parseFloat(rgrid.getRheight());  
    	String colhead=this.domain.getColhead();
    	if(colhead!=null&& "true".equals(colhead))//显示字段
    	{
    		all_height=all_height-(Integer.parseInt(rgrid.getFontsize())+10);
    	}  
    	/**字段怎没有啦,chenmengqing added at 20070601*/
    	if(this.fieldlist.size()==0) {
            return "";
        }
    	int count=getCount(sql,conn);
    	ArrayList strLst=null;
    	ArrayList strLstNoPre=null;
    	GetCardCellValue cellValue=new GetCardCellValue();
    	try
    	{
    		rs=dao.search(sql);
    		String class_str="";
    		String fieldValue="";
    		int tt=0;
    		while(rs.next())
    		{
    			tt++;
    			if(tt==count)
    			{
    				heights=(int)all_height-count;
    			}else
    			{
    				all_height=all_height-(Integer.parseInt(rgrid.getFontsize())+10);
    			}
    			html.append("<tr>");
    			for(int i=0;i<this.fieldlist.size();i++)
    			{
    				String field=this.fieldlist.get(i).toString();
    				int widthn=((int)para_array[i]); 
    				YkcardSubclassFieldBean bean=new YkcardSubclassFieldBean();
    				bean=(YkcardSubclassFieldBean)this.cellhash.get(field);
    				strLst=new ArrayList();   
    				strLstNoPre=new ArrayList();
    				switch(bean.getItemtype().toUpperCase().charAt(0))
    				{
    					case CELLFIELD_STRINGTYPE:         //字符类型
    					{   
    					  fieldValue=rs.getString(field);
    					  if(fieldValue!=null && fieldValue.length()>0)
    					  {
    						strLst.add(fieldValue);//明码于代码输出    						
    					  }					     
    						break;
    					}
    					case CELLFIELD_MEMOTYPE:          //备注类型 
    					{
    						String valueresult="";
    						fieldValue=rs.getString(field);
    						if(fieldValue!=null && fieldValue.length()>0)
    						{
    							for(int r=0;r<fieldValue.length();r++)
    							{
    							   if("\n".equals(fieldValue.substring(i,i+1))) {
                                       valueresult+="<br>";
                                   } else {
                                       valueresult+=fieldValue.substring(i,i+1);
                                   }
    							}						  
    						   strLst.add(valueresult);//明码于代码输出    						   
    						}
    						break;
    					}
    					case CELLFIELD_DATETYPE:               //日期控制格式
    					{
    						Date date=rs.getTimestamp(field);   
    						if(date!=null)
    						{
    							String strdata=DateUtils.format(date,"yyyy.MM.dd HH:mm:ss");
    							java.sql.Timestamp fdate=null;
	    						if(strdata!=null && strdata.length()>=4) {
                                    fdate=rs.getTimestamp(field);
                                }
        						getCellDateListValue(bean, strLst, strdata,fdate);
    						}else
    						{
    							strLst.add("");
    						}						
    						break;
    					}
    					case CELLFIELD_NUMBERTYPE:            //数值类型
    					{
    						float fv=rs.getFloat(field);
    						
    						
    						if(display_zero!=null&& "1".equals(display_zero)&&fv==0)
    						{
    							strLst.add("");
    						}else {
                                getCellDecimalListValue(bean, strLst, fv,strLstNoPre);
                            }
    						break;				
    					}
    				}
    				if(bean.getCodesetid()!=null&&!"0".equals(bean.getCodesetid()))
    				{			
    		     	  strLst=cellValue.GetValueofField(strLst,bean.getCodesetid());  //代码的转换
    				}
    				if(tt==count)
        			{
    					if(i!=this.fieldlist.size()-1) {
                            class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0","0",this.domain.getHl(),this.domain.getVl());
                        } else {
                            class_str= new MadeCardCellLine().GetCardCellLineShowcss("0","0","0","0",this.domain.getHl(),this.domain.getVl());
                        }
        			}else
        			{
        				if(i!=this.fieldlist.size()-1) {
                            class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0",this.lh,this.domain.getHl(),this.domain.getVl());
                        } else {
                            class_str= new MadeCardCellLine().GetCardCellLineShowcss("0","0","0",this.lh,this.domain.getHl(),this.domain.getVl());
                        }
        			}
    				
    				this.fontsize=mc.ReDrawLitterRect(widthn,heights,(String)hash.get("title"),Integer.parseInt(rgrid.getFontsize()),rgrid.getFontName(),disting_pt,rgrid.getField_type(),rgrid.getSlope());
    				//this.fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights,(String)hash.get("title"));
    				String[] align=mc.getAlign(bean.getAlign());	
    				html.append("<td align=\""+align[0]+"\" valign=\""+ align[1]+"\" width=\""+widthn+"\" height=\""+heights+"\" class=\""+class_str+"\">");
    	    		html.append("<font  color=\"#0000FF\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + this.fontsize + "pt\">");
    	    		//html.append();
    	    		String fontStr="";   
    	    		if(strLst==null||strLst.size()<=0)
    	    		{
    	    			html.append("<br>");
    	    		}else
    	    		{
    	    			fontStr=(String)strLst.get(0);
    	    			fontsize=mc.ReDrawLitterRect(widthn,heights,fontStr,Integer.parseInt(rgrid.getFontsize()),rgrid.getFontName(),disting_pt,rgrid.getField_type(),rgrid.getSlope());
    	    			//this.fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights,fontStr);
    	    			html.append("<font  color=\"#0000FF\" style=\"font-weight:" + fontweight + ";font-family:"+rgrid.getFontName()+";font-size:" + fontsize + "pt\">");
    	    			html.append(strLst.get(0));
    	    			html.append("</font>");
    	    		}
    	    		html.append("</font>");
    	    		html.append("</td>");
    			}
    			html.append("</tr>");
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return html.toString();
    }
    private void getCellDateListValue(
    		YkcardSubclassFieldBean bean,   
    		ArrayList strLst,
    		String  strdata,
    		java.sql.Timestamp fdatetemp)
    		throws SQLException {
    		boolean bIsNull;
    		String cStr1=""; 
    		java.sql.Timestamp fdate;
    		int wYear=0;
    		int wMonth=0;
    		int wDay=0;
    		int nIdx=0;
    		int hours=0;
    		int minutes=0;
    		String strPre="";
    		String strExt="";	
    		StringBuffer cStr=new StringBuffer();	
    		GetCardCellValue cellValue=new GetCardCellValue();    		
    	    if(strdata ==null || strdata.length()<4)  //保证值为合法日期形
    		{
    			bIsNull=true;
    		}
    		else
    		{
    			bIsNull=false;
    			fdate=fdatetemp;//data.getDate(1);
    			wYear=fdate.getYear() + 1900;
    			wMonth=fdate.getMonth() + 1;
    			wDay=fdate.getDate();
    			hours=fdate.getHours();
    			minutes=fdate.getMinutes();
        	}
    		if(bean.getPre() !=null && bean.getPre().length()>0)    //判断前缀
    		{
    		   nIdx=bean.getPre().indexOf(",");
    	  	   if(nIdx ==-1)
    		   {
    			  strPre=bean.getPre();
    			  strExt="";						
    		   }
    		   else
    		   {
    			  strPre=bean.getPre().substring(0,nIdx).trim();
    			  strExt=bean.getPre().substring(nIdx+1,bean.getPre().length());   
    		   }
    		}
            if(bIsNull)                                     
            {
                // 只定义前缀时，未维护值不显示‘-’
                if (strExt == null || strExt.length() == 0) {
                    cStr1 = "";
                } else {
                    cStr1=strPre;
                    cStr1+=strExt;
                }
                strLst.add(cStr1);
            }
            else {
                switch((Integer.parseInt(bean.getSlop())+6))                                 //日期的现实格式
                {
                    case 6:  //1991.12.3
                    {
                        cStr.append(wYear);
                        cStr.append(".");
                        cStr.append(wMonth);
                        cStr.append(".");
                        cStr.append(wDay);
                        if(cStr !=null)
                        {
                            cStr1=strPre;
                            cStr1+=cStr.toString();   //cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1);
                        }
                        break;
                    }
                    case 7:   //99.2.23
                    {
                        cStr.append(String.valueOf(wYear).substring(2,4));
                        cStr.append(".");
                        cStr.append(wMonth);
                        cStr.append(".");
                        cStr.append(wDay);
                        if(cStr !=null)
                        {
                            cStr1=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 8:  //1991.2
                    {
                        cStr.append(wYear);
                        cStr.append(".");
                        cStr.append(wMonth);
                        if(cStr !=null)
                        {
                            cStr1=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1);
                        }
                        break;
                    }
                    case 9:   //1991.02
                    {
                        if(wMonth <10) {
                            cStr.append(wYear + ".0" + wMonth);
                        } else {
                            cStr.append(wYear + "." + wMonth);
                        }
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 10:  //98.2
                    {
                        cStr.append(String.valueOf(wYear).substring(2,4));
                        cStr.append(".");
                        cStr.append(wMonth);
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 11:  //98.02
                    {
                        cStr.append(String.valueOf(wYear).substring(2,4));
                        if(wMonth <10) {
                            cStr.append(cStr.toString() + ".0" + wMonth);
                        } else {
                            cStr.append(cStr.toString() + "." + wMonth);
                        }
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 12:   //一九九一年一月二日
                    {
                        cStr.append(cellValue.getDateYear(wYear));
                        cStr.append("年");
                        cStr.append(cellValue.getDateMonth(wMonth));
                        cStr.append("月");
                        cStr.append(cellValue.getDateDay(wDay));
                        cStr.append("日");
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 13:  //一九九一年一月
                    {
                        cStr.append(cellValue.getDateYear(wYear));
                        cStr.append("年");
                        cStr.append(cellValue.getDateMonth(wMonth));
                        cStr.append("月");
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 14:  //1991年10月5日
                    {
                        cStr.append(wYear);
                        cStr.append("年");
                        cStr.append(wMonth);
                        cStr.append("月");
                        cStr.append(wDay);
                        cStr.append("日");
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 15:  //1991年10月
                    {
                        cStr.append(wYear);
                        cStr.append("年");
                        cStr.append(wMonth);
                        cStr.append("月");
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 16:   //91年10月5日
                    {
                        cStr.append(String.valueOf(wYear).substring(2,4));
                        cStr.append("年");
                        cStr.append(wMonth);
                        cStr.append("月");
                        cStr.append(wDay);
                        cStr.append("日");
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 17:   //91年10月
                    {
                        cStr.append(String.valueOf(wYear).substring(2,4));
                        cStr.append("年");
                        cStr.append(wMonth);
                        cStr.append("月");
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 18:  //求年龄
                    {
                        int wcYear = Calendar.getInstance().get(Calendar.YEAR);        //获得当前年
                        int wcMonth= Calendar.getInstance().get(Calendar.MONTH) + 1;   //获得当前月
                        int wcDay= Calendar.getInstance().get(Calendar.DATE) ;
                        if(wYear == 1899 || wYear==1889)
                        {
                          wYear = Calendar.getInstance().get(Calendar.YEAR);        //获得当前年
                          wMonth= Calendar.getInstance().get(Calendar.MONTH) + 1;   //获得当前月
                          wDay= Calendar.getInstance().get(Calendar.DATE) ;         //获得当前日
                        }        //获得当前日
                        int nAge=cellValue.GetHisAge(wcYear,wcMonth,wcDay,wYear,wMonth,wDay);
                        if(nAge>2000) {
                            cStr.append("");
                        } else {
                            cStr.append(String.valueOf(nAge));
                        }
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 19:    ///get the year
                    {
                        cStr.append(String.valueOf(wYear));
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 20:   ///get the month
                    {
                        cStr.append(String.valueOf(wMonth));
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 21:  //get the day
                    {
                        cStr.append(String.valueOf(wDay));
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 22:    //1991年01月
                    {
                        if(wMonth >9) {
                            cStr.append(wYear + "年" + wMonth + "月");
                        } else {
                            cStr.append(wYear + "年0" + wMonth + "月");
                        }
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 23:    //1991年01月05日
                    {
                        cStr.append(wYear);
                        cStr.append("年");
                        if(wMonth>9) {
                            cStr.append(String.valueOf(wMonth));
                        } else {
                            cStr.append("0" + wMonth);
                        }
                        cStr.append("月");
                        if(wDay>9) {
                            cStr.append(String.valueOf(wDay));
                        } else {
                            cStr.append("0" + wDay);
                        }
                        cStr.append("日");
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 24:    //1991.01.01
                    {
                        cStr.append(wYear);
                        cStr.append(".");
                        if(wMonth>9) {
                            cStr.append(String.valueOf(wMonth));
                        } else {
                            cStr.append("0" + wMonth);
                        }
                        cStr.append(".");
                        if(wDay>9) {
                            cStr.append(String.valueOf(wDay));
                        } else {
                            cStr.append("0" + wDay);
                        }
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }case 25:    //1990.01.01 10:30
                    {
                        cStr.append(wYear);
                        cStr.append(".");
                        if(wMonth>9) {
                            cStr.append(String.valueOf(wMonth));
                        } else {
                            cStr.append("0" + wMonth);
                        }
                        cStr.append(".");
                        if(wDay>9) {
                            cStr.append(String.valueOf(wDay));
                        } else {
                            cStr.append("0" + wDay);
                        }
                        if(hours!=0&&minutes!=0) {
                            cStr.append(" " + hours + ":" + minutes);
                        }
                        if(cStr !=null)
                        {
                            cStr1+=strPre;
                            cStr1+=cStr.toString();//cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay)) {
                            strLst.add(cStr1.toString());
                        }
                        break;
                    }
                    case 26:    // 月份(一月-十二月)
                    {
                        if(wMonth==1) {
                            cStr1 = "一月";
                        } else if(wMonth==2) {
                            cStr1 = "二月";
                        } else if(wMonth==3) {
                            cStr1 = "三月";
                        } else if(wMonth==4) {
                            cStr1 = "四月";
                        } else if(wMonth==5) {
                            cStr1 = "五月";
                        } else if(wMonth==6) {
                            cStr1 = "六月";
                        } else if(wMonth==7) {
                            cStr1 = "七月";
                        } else if(wMonth==8) {
                            cStr1 = "八月";
                        } else if(wMonth==9) {
                            cStr1 = "九月";
                        } else if(wMonth==10) {
                            cStr1 = "十月";
                        } else if(wMonth==11) {
                            cStr1 = "十一月";
                        } else if(wMonth==12) {
                            cStr1 = "十二月";
                        }

                        strLst.add(cStr1.toString());
                        break;
                    }
                    default:
                    {
                        cStr.append(wYear);
                        cStr.append(".");
                        cStr.append(wMonth);
                        cStr.append(".");
                        cStr.append(wDay);
                        if(cStr !=null)
                        {
                            cStr1=strPre;
                            cStr1+=cStr.toString();   //cell.getStrPre() + cStr1;
                        }
                        if(!"1899.12.30".equalsIgnoreCase(wYear + "." + wMonth + "." + wDay))
                        //if(strPre==null||strPre.length()<=0)
                        //	if(strPre!=null&&strPre.length()>0)
                        {
                            strLst.add(cStr1);
                        }
                        break;
                    }
                }
            }
    	}
    /****************************************
	  * 返回单元格的类型为数值类型List的值       *
	  * *************************************/
	private void getCellDecimalListValue(
		YkcardSubclassFieldBean bean,   
		ArrayList strLst,
		float fieldFloatValue,ArrayList strLstNoPre)
		throws SQLException {
		String pattern="###";   //浮点数的精度
		String cStr;
	    if(Integer.parseInt(bean.getSlop())>0) {
            pattern+=".";
        }
	    for(int i=0;i<Integer.parseInt(bean.getSlop());i++) {
            pattern +="#";
        }
	   //cStr=new  BigDecimal(pattern).format((double)fieldFloatValue).trim();
	    BigDecimal b = new BigDecimal(Float.toString(fieldFloatValue));
        BigDecimal one = new BigDecimal("1");
        cStr=b.divide(one,Integer.parseInt(bean.getSlop()),BigDecimal.ROUND_HALF_UP).toString();
	    strLstNoPre.add(cStr);
	    if(cStr !=null) {
            cStr=bean.getPre() +cStr;            //前缀加上格式化后的值
        }
	    strLst.add(cStr);
	}
	public int getCount(String sql,Connection conn)
	{
	   int count=0;
	   if(sql==null||sql.length()<=0) {
           return 0;
       }
	   if(sql.indexOf("From")!=-1)
	   {
		   int v=sql.indexOf("From");
		   int c=sql.indexOf("ORDER");
		   if(c==-1) {
               c=sql.length();
           }
		   String countSQL=sql.substring(v,c);
		   try
		   {
			   countSQL="select count(*) as aa "+countSQL;			  
			   ContentDAO dao=new ContentDAO(conn);
			   RowSet rs=dao.search(countSQL);
			   if(rs.next())
			   {
				   count=rs.getInt(1); 
			   }
		   }catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
	   return count;
	}
	/**************Pdf********************/
	 public PdfPTable viewSubClassPdf(String infokind,String userbase, Connection conn,UserView userview,RGridView rgrid,String disting_pt,byte nFlag,String platform)
	 {
		 float mMtoItext=297f/842;
		 float converPxTomm=1f/96*25.4f;
		 String colhead=this.domain.getColhead();
	     String setname=this.domain.getSetname();
	     GetCardCellValue getCardCellValue=new GetCardCellValue();
		 String isView=getCardCellValue.getISVIEWForCexpress(rgrid.getCexpress());
		 if(!"1".equals(isView))
		 {
			 if(nFlag!=5)
		     {
		    	 if(userpriv!=null&&"selfinfo".equalsIgnoreCase(userpriv))
			     {
			    		if(!"1".equals(fieldpurv)&& "0".equals(userview.analyseTablePriv(setname,0)))
				    	{
			    			return null;
				    	}
			     }else
			     {
			    	 if(this.fenlei_type!=null&&this.fenlei_type.length()>0&&!userview.isSuper_admin()&&(nFlag==0))
		    	     {
		    	    	  String priv_flag= userview.analyseSubTablePriv(this.fenlei_type,setname)+"";    	  
		    	    	  if(priv_flag==null||priv_flag.length()<=0|| "-1".equals(priv_flag)) {
                              return null;
                          }
		    	     }else if("0".equals(userview.analyseTablePriv(setname))&&!"zpselfinfo".equals(this.userpriv))//招聘  子集导出pdf不需要权限   28708 changxy
				     {
				    		return null;
				      }
			     } 
		     }
		 }	     	    
	     getTable_width(conn,rgrid);
	     PdfPTable table = null;
	     PdfPTable table1 = null;
	     if(this.fieldlist==null||this.fieldlist.size()<=0) {
             return null;
         }
	     float topn;                                                  //单元格的上边位置
		 float leftn;                                                 //单元格的左边位置
		 float heightn;                                           //单元格的高
		 float widthn;       	
	     leftn=Float.parseFloat(rgrid.getRleft())*pdf_w_base;        
	     leftn=(float)(leftn*converPxTomm/mMtoItext);
         widthn=Float.parseFloat(rgrid.getRwidth())*pdf_w_base;
         widthn=(float)(widthn*converPxTomm/mMtoItext);
         heightn=Float.parseFloat(rgrid.getRheight())*pdf_h_base;  
         heightn=(float)(heightn*converPxTomm/mMtoItext);
         //int fact_width=(int)Float.parseFloat(rgrid.getRwidth()) + 1;
 	     //int fact_height=(int)Float.parseFloat(rgrid.getRheight()) +1;
	     this.fontweight=rgrid.getFonteffect();
	     print_File="false";
	     float para_array[]=viewParaSrray(widthn);	     
	     /*if(colhead!=null&&colhead.equals("true"))//显示字段
	     {
	    	 viewColumnsNamePDF(para_array,rgrid,disting_pt,table);
	     }*/
	     String sql="";
	     StringBuffer fields=new StringBuffer();
	     for(int i=0;i<this.fieldlist.size();i++)
	     {
	        	fields.append(this.fieldlist.get(i).toString()+",");
	     }
	     fields.setLength(fields.length()-1);
	     String cBase="";
	     switch(nFlag)
	     {	
				//人员库
				case 0:
				{
					sql=getPersonSql(rgrid,userbase,userview,nFlag,PERSONKEYTYPE,fields.toString());
					break;
				}//岗位库
				case 4:
				{
					if("1".equals(infokind))
					{
						nId=getOrgnId("4",nId,userbase);				
					}else
					{
						cBase="K";
					}
					sql=getPostSql(rgrid,userbase,userview,nFlag,POSTKEYTYPE,fields.toString());
					break;					
				}
//				单位库
				case 2:
				{
					if("1".equals(infokind))
					{
						nId=getOrgnId("2",nId,userbase);		
					}else
					{
						cBase="B";
					}
					sql=getUnitSql(rgrid,userbase,userview,nFlag,UNITKEYTYPE,fields.toString());
					break;					
				}
				case 5://计划目标表
				{
					sql=getPlanSql(rgrid,userbase,userview,nFlag,PERSONKEYTYPE,this.fieldlist);										
					break;
				}
				case 6:
				{
					String cName=this.domain.getSetname();
			        String cStr1=this.userbase+this.domain.getSetname();
			        sql=getZpSubsetSql(cName,rgrid,userbase,userview,nFlag,PERSONKEYTYPE,fields.toString());
					break;
				}
				case 7:  // 基准岗位
				{
			        sql=getStdPosSql(rgrid,userbase,userview,nFlag,STDPOSKEYTYPE,fields.toString());
					break;
				}
		 }
	    /* if(sql!=null&&sql.length()>0)
	        viewContentValuePDF(sql,conn,para_array,rgrid,disting_pt,table);*/
	     ArrayList subClassList=rowHeightNum(para_array,rgrid,disting_pt,sql);
	     Paragraph paragraph = null;
 		 PdfPCell cell=null;
 		 String[]ltrb=null;
 		 boolean isCorrect=false;
 		 if(subClassList==null|subClassList.size()<=0) {
             return table;
         }
 		 //liuy 2015-12-21 优化登记表不显示格线的情况下，数据导出Pdf垂直居中 begin
		 boolean tempflag = false;//子集是否垂直居中显示
		 if(("A".equals(rgrid.getFlag())|| "B".equals(rgrid.getFlag())|| "K".equals(rgrid.getFlag()))&& "1".equals(rgrid.getSubflag())){
			 XmlSubdomain xmlSubdomain=new XmlSubdomain(rgrid.getSub_domain());
			 xmlSubdomain.getParaAttribute();
			 if(StringUtils.isNotEmpty(xmlSubdomain.getFields())){
				 if(("7".equals(rgrid.getAlign())||"6".equals(rgrid.getAlign())||"8".equals(rgrid.getAlign()))&&"false".equals(xmlSubdomain.getVl())&&"false".equals(xmlSubdomain.getHl())) {
                     tempflag = true;
                 }
			 }
		 }//liuy 2015-12-21 end
		 if(tempflag){
		 	table1=new PdfPTable(para_array);
 		 	table=new PdfPTable(1);
		 }else {
             table=new PdfPTable(para_array);
         }
	     table.getDefaultCell().setBorder(0);
	     table.setHorizontalAlignment(0);
	     if(tempflag){
		     table.getDefaultCell().setMinimumHeight(heightn);
		     table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);  
			 table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
	     }
	     table.setSpacingBefore(0);	     
	     table.getDefaultCell().setPadding(0);
	     for(int r=0;r<subClassList.size();r++)
	     {
	    		HashMap one_hash=(HashMap)subClassList.get(r);
	    		isCorrect=true;
	    		float heights=0;
		    	for(int i=0;i<this.fieldlist.size();i++)
		    	{
		    		MadeFontsizeToCell mc=new MadeFontsizeToCell();
		    		String field=this.fieldlist.get(i).toString();
		    		String class_str="";    		    		
					HashMap hash=(HashMap)one_hash.get(field);
					if(this.customcolhead!=null&& "true".equalsIgnoreCase(this.customcolhead))
					{
						HashMap headwidth=this.customcolheadwidth;
						String width_str=(String)headwidth.get(field);
						if(width_str!=null&&width_str.length()>0)
						{
							widthn=(int)Float.parseFloat(width_str);
						}else {
                            widthn=((int)para_array[i]);
                        }
					}else
					{
						widthn=((int)para_array[i]); 
					}					 
		    		String title=(String)hash.get("title");
		    		ArrayList v_list=new ArrayList();
		    		v_list.add(title);
		    		String[] aligns=(String[])hash.get("align");	
		    		Integer height_I=(Integer)hash.get("height");
		    		Integer max_row_I=(Integer)one_hash.get("max_row");
		    		int max_row=max_row_I.intValue();	
		    		int align=6;
		    		if(r==subClassList.size()-1&&!tempflag)//liuy 2015-12-21
     			    {
		    			if(i!=this.fieldlist.size()-1) {
                            if(i==0&&this.isMaxwidth) {
                                ltrb = getLtrb("1", "0", this.ls, this.lh);
                            } else {
                                ltrb = getLtrb("0", "0", this.ls, this.lh);
                            }
                        } else {
                            ltrb= getLtrb("0","0","1",this.lh);
                        }
		    			if(aligns[0]!=null&& "left".equals(aligns[0])) {
                            align=9;
                        } else if(aligns[0]!=null&& "right".equals(aligns[0])) {
                            align=11;
                        } else if(aligns[0]!=null&& "center".equals(aligns[0])) {
                            align=10;
                        }
		    			heights=heightn; 
     			    }else
     			    {
     				    if(i!=this.fieldlist.size()-1) {
                            if(i==0&&this.isMaxwidth) {
                                ltrb = getLtrb("1", "0", this.ls, this.lh);
                            } else {
                                ltrb = getLtrb("0", "0", this.ls, this.lh);
                            }
                        } else {
                            ltrb= getLtrb("0","0","1",this.lh);
                        }
     				    if(aligns[0]!=null&& "left".equals(aligns[0])) {
                            align=6;
                        } else if(aligns[0]!=null&& "right".equals(aligns[0])) {
                            align=8;
                        } else if(aligns[0]!=null&& "center".equals(aligns[0])) {
                            align=7;
                        }
     				    height_I=(Integer)hash.get("height");
 		    		    int height=height_I.intValue(); 		    		   
 		    		    if(this.auto_heigh) {
                            heights=(max_row*height)+4;
                        } else {
                            heights=height;
                        }
 		    		   heights=heights*pdf_h_base;
     			    }
		    		
		    		if(subClassList.size()>1&&heights!=(heightn/(subClassList.size()-r)))//27558 changxy  20170515
                    {
                        heights=(heightn/(subClassList.size()-r));
                    }
		    		
		    		if(title!=null&&title.length()>0) {
                        title=title.replaceAll("&nbsp;", " ");
                    }
		    		//liuy 2015-4-1 7864：员工管理-信息浏览-姓名后的放大镜-员工基本情况表-输出pdf(页面格式有问题) start
		    		int size=mc.ReDrawLitterRect((int)widthn, (int)heights, v_list, this.fontsize);
		    		Font font=FontFamilyType.getFont("宋体",rgrid.getFonteffect(),size-2,platform);
		    		//liuy 2015-4-1 end
 		    		paragraph=new Paragraph(title+"",font);
	    	    	cell = new PdfPCell(paragraph);
	    	    	cell.setFixedHeight(heights);				
	    	   		cell.setMinimumHeight(heights);	    	   			
	    	   		cell=excecute(cell,align,ltrb);
	    	   		if(tempflag) {
                        table1.addCell(cell);
                    } else {
                        table.addCell(cell);
                    }
     			 }		    	 
		    	heightn=heightn-heights;	
		  }	
	      if(tempflag) {
              table.addCell(table1);
          }
	      if(!isCorrect)
 		  {
 			  for(int i=0;i<this.fieldlist.size();i++)
 			  {
 				  paragraph=new Paragraph("");
 	    		  cell = new PdfPCell(paragraph);
 	    		  cell.setFixedHeight(heightn);				
 	   			  cell.setMinimumHeight(heightn); 
 	   			  table.addCell(cell);
 			   }
 		 }
	     return table;
	 }
	 public void viewColumnsNamePDF(float para_array[],RGridView rgrid,String disting_pt,PdfPTable table)
	 {
		 MadeFontsizeToCell mc=new MadeFontsizeToCell(); 
		 float heights=(Integer.parseInt(rgrid.getFontsize())*pdf_h_base+12);
		 Paragraph paragraph = null;
		 PdfPCell cell=null;
		 String[]ltrb=null;
	     for(int i=0;i<this.fieldlist.size();i++)
	     {
	    		String field=this.fieldlist.get(i).toString();
	    		HashMap hash=(HashMap)this.hash.get(field);	    		
	    		int widthn=((int)para_array[i]);    		
	    		this.fontsize=mc.ReDrawLitterRect(widthn,(int)heights,(String)hash.get("title"),Integer.parseInt(rgrid.getFontsize()),rgrid.getFontName(),disting_pt,rgrid.getField_type(),rgrid.getSlope());
	    		//this.fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights,(String)hash.get("title"));
	    		Font font=FontFamilyType.getFont(rgrid.getFontName(),rgrid.getFonteffect(),fontsize);
	    		paragraph=new Paragraph(hash.get("title")+"",font);
	    		cell = new PdfPCell(paragraph);
	    		cell.setFixedHeight(heights);				
	   			cell.setMinimumHeight(heights);
	   			 if(i!=this.fieldlist.size()-1) {
                     if(i==0&&this.isMaxwidth) {
                         ltrb = getLtrb("1", "0", this.ls, this.lh);
                     } else {
                         ltrb = getLtrb("0", "0", this.ls, this.lh);
                     }
                 } else {
                     ltrb= getLtrb("0","0","1",this.lh);
                 }
	   			cell=excecute(cell,7,ltrb);
	   			table.addCell(cell);
	     }
	 }
	 public void viewContentValuePDF(String sql,Connection conn,float para_array[],RGridView rgrid,String disting_pt,PdfPTable table)
	    {
	    	StringBuffer html=new StringBuffer();
	    	RowSet rs=null;
	    	ContentDAO dao=new ContentDAO(this.conn);
	    	MadeFontsizeToCell mc=new MadeFontsizeToCell(); 
	    	float heights=(Integer.parseInt(rgrid.getFontsize())*pdf_h_base+12);
	    	float all_height=Float.parseFloat(rgrid.getRheight())*pdf_h_base;;  
	    	String colhead=this.domain.getColhead();
	    	if(colhead!=null&& "true".equals(colhead))//显示字段
	    	{
	    		all_height=all_height-(Integer.parseInt(rgrid.getFontsize())*pdf_h_base+12);
	    	}  
	    	int count=getCount(sql,conn);
	    	ArrayList strLst=null;
	    	ArrayList strLstNoPre=null;
	    	GetCardCellValue cellValue=new GetCardCellValue();
	    	try
	    	{
	    		rs=dao.search(sql);	    		
	    		String fieldValue="";
	    		int tt=0;
	    		Paragraph paragraph = null;
	    		PdfPCell cell=null;
	    		String[]ltrb=null;
	    		boolean isCorrect=false;
	    		while(rs.next())
	    		{
	    			isCorrect=true;
	    			tt++;
	    			if(tt==count)
	    			{
	    				heights=(int)all_height;
	    			}else
	    			{
	    				all_height=all_height-(Integer.parseInt(rgrid.getFontsize())*0.93f+12);
	    			}
	    			for(int i=0;i<this.fieldlist.size();i++)
	    			{
	    				String field=this.fieldlist.get(i).toString();
	    				int widthn=((int)para_array[i]); 
	    				fieldValue=rs.getString(field);
	    				YkcardSubclassFieldBean bean=new YkcardSubclassFieldBean();
	    				bean=(YkcardSubclassFieldBean)this.cellhash.get(field);
	    				strLst=new ArrayList();   
	    				strLstNoPre=new ArrayList();
	    				switch(bean.getItemtype().toUpperCase().charAt(0))
	    				{
	    					case CELLFIELD_STRINGTYPE:         //字符类型
	    					{   
	    					  if(fieldValue!=null && fieldValue.length()>0)
	    					  {
	    						strLst.add(fieldValue);//明码于代码输出    						
	    					  }					     
	    						break;
	    					}
	    					case CELLFIELD_MEMOTYPE:          //备注类型 
	    					{
	    						String valueresult="";
	    						if(fieldValue!=null && fieldValue.length()>0)
	    						{
	    							for(int r=0;r<fieldValue.length();r++)
	    							{
	    							   if("\n".equals(fieldValue.substring(i,i+1))) {
                                           valueresult+="<br>";
                                       } else {
                                           valueresult+=fieldValue.substring(i,i+1);
                                       }
	    							}						  
	    						   strLst.add(valueresult);//明码于代码输出    						   
	    						}
	    						break;
	    					}
	    					case CELLFIELD_DATETYPE:               //日期控制格式
	    					{
	    						String strdata=fieldValue;
	    						java.sql.Timestamp fdate=null;
	    						if(strdata!=null && strdata.length()>=4) {
                                    fdate=rs.getTimestamp(field);
                                }
	    						getCellDateListValue(bean, strLst, strdata,fdate);
	    						break;
	    					}
	    					case CELLFIELD_NUMBERTYPE:            //数值类型
	    					{
	    						float fv=rs.getFloat(field);
	    						getCellDecimalListValue(bean, strLst, fv,strLstNoPre);
	    						break;				
	    					}
	    				}
	    				if(bean.getCodesetid()!=null&&!"0".equals(bean.getCodesetid()))
	    				{			
	    		     	  strLst=cellValue.GetValueofField(strLst,bean.getCodesetid());  //代码的转换
	    				}
	    				this.fontsize=mc.ReDrawLitterRect(widthn,(int)heights,(String)hash.get("title"),Integer.parseInt(rgrid.getFontsize()),rgrid.getFontName(),disting_pt,rgrid.getField_type(),rgrid.getSlope());
	    				//this.fontsize=mc.getFitFontSize(Integer.parseInt(rgrid.getFontsize()), widthn,heights,(String)hash.get("title"));
	    				Font font=FontFamilyType.getFont(rgrid.getFontName(),rgrid.getFonteffect(),fontsize);
	    				int align=6;
	    	    		if(tt==count)
	        			{
	    					if(i!=this.fieldlist.size()-1) {
                                if(i==0&&this.isMaxwidth) {
                                    ltrb = getLtrb("1", "0", this.ls, this.lh);
                                } else {
                                    ltrb = getLtrb("0", "0", this.ls, this.lh);
                                }
                            } else {
                                ltrb= getLtrb("0","0","1","0");
                            }
	    					align=9;
	        			}else
	        			{
	        				if(i!=this.fieldlist.size()-1) {
                                if(i==0&&this.isMaxwidth) {
                                    ltrb = getLtrb("1", "0", this.ls, this.lh);
                                } else {
                                    ltrb = getLtrb("0", "0", this.ls, this.lh);
                                }
                            } else {
                                ltrb= getLtrb("0","0","1",this.lh);
                            }
	        			}
	    				String str_v="";
	    				if(strLst!=null&&strLst.size()>0)
	    	    		{
	    					str_v=(String)strLst.get(0);
	    	    		}
	    				paragraph=new Paragraph(str_v+"",font);
	    	    		cell = new PdfPCell(paragraph);
	    	    		cell.setFixedHeight(heights);				
	    	   			cell.setMinimumHeight(heights);	    	   			
	    	   			cell=excecute(cell,align,ltrb);
	    	   			table.addCell(cell);
	    			}
	    		}
	    		if(!isCorrect)
	    		{
	    			for(int i=0;i<this.fieldlist.size();i++)
	    			{
	    				paragraph=new Paragraph("");
	    	    		cell = new PdfPCell(paragraph);
	    	    		cell.setFixedHeight(all_height);				
	    	   			cell.setMinimumHeight(all_height); 
	    	   			table.addCell(cell);
	    			}
		    		
	    		}
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	   		}
	   }
	 /**
		 * 表格样式边线
		 * @return String[] ltrb
		 *   ltrb[0]=左
		 *   ltrb[1]=上
		 *   ltrb[2]=右
		 *   ltrb[3]=下
		 * **/
		public String[] getLtrb(String l,String t,String r,String b )
		{
			String[] ltrb = new String[4];
			ltrb[0]=l!=null?l:"0";
			ltrb[1]=t!=null?t:"0";
		    ltrb[2]=r!=null?r:"0";
			ltrb[3]=b!=null?b:"0";
			
			return ltrb;
		}
		/**
		 * (每个cell表示一个单元格)	 
		 * @param align     单元格内容的排列方式
		 * @param ltrb[]    c.L,c.T,c.R,c.B
		 * @param PdfPCell cell
		 */
		public  PdfPCell  excecute(PdfPCell cell,int align,String[] ltrb)
		{
			try
			{
				Rectangle borders = new Rectangle(0f, 0f);
				  
				  if("1".equals(ltrb[0]))
				  {
					  borders.setBorderWidthLeft(0.6f);
					  borders.setBorderColorLeft(new Color(0, 0, 0));
				  }
				  else
				  {
					  //borders.setBorderColorLeft(new Color(255, 255, 255));
					  borders.setBorderWidthLeft(0f);
				  }
					  
				  
				  if("1".equals(ltrb[1]))
				  {
					  borders.setBorderWidthTop(0.6f);
					  borders.setBorderColorTop(new Color(0, 0, 0));	
				  }
				  else
				  {
					  //borders.setBorderColorLeft(new Color(255, 255, 255));
					  borders.setBorderWidthLeft(0f);
				  }
				  
				  if("1".equals(ltrb[2]))
				  {
					  borders.setBorderWidthRight(0.6f);
					  borders.setBorderColorRight(new Color(0, 0, 0));
				  }
				  else
				  {
					  //borders.setBorderColorLeft(new Color(255, 255, 255));
					  borders.setBorderWidthLeft(0f);
				  }
				  
				  if("1".equals(ltrb[3]))
				  {
					  borders.setBorderWidthBottom(0.6f);
					  borders.setBorderColorBottom(new Color(0, 0, 0));
				  }
				  else
				  {
					  //borders.setBorderColorLeft(new Color(255, 255, 255));
					  borders.setBorderWidthLeft(0f);
				  }
				  
				  cell.cloneNonPositionParameters(borders);
				/*  单元格内容的排列方式
				 * =0上左 =1上中  =2上右  =3下左  =4下中  =5下右 =6中左  =7中中 =8中右=9上左 
			     */		
				
				 if(align==0)   
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);    //基于最合适的
				 }
				 else if(align==1)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				 }
				 else if(align==2)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				 }
				 else if(align==3)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
					 cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				 }
				 else if(align==4)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					 cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				 }
				 else if(align==5)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					 cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				 }
				 else if(align==6)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
					 cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				 }
				 else if(align==7)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_CENTER);   //居中
					 cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				 }
				 else if(align==8)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_RIGHT);   //居右
					 cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				 }else if(align==9)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_LEFT);   //居右
					 cell.setVerticalAlignment(Element.ALIGN_TOP);
				 }	else if(align==10)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_CENTER);   //居右
					 cell.setVerticalAlignment(Element.ALIGN_TOP);
				 }else if(align==11)
				 {
					 cell.setHorizontalAlignment(Element.ALIGN_RIGHT);   //居右
					 cell.setVerticalAlignment(Element.ALIGN_TOP);
				 }	
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return cell;
		}
		public String getLh() {
			String hs=this.domain.getHl();
			if(hs!=null&& "true".equals(hs))
			{
			    lh="1";
			}else
			{
				lh="0";
			}
			return lh;
		}
		public void setLh(String lh) {
			this.lh = lh;
		}
		public String getLs() {
			String ss=this.domain.getVl();
			if(ss!=null&& "true".equals(ss))
			{
			    ls="1";
			}else
			{
				ls="0";
			}
			return ls;
		}
		public String getColheadheight() {
			colheadheight=this.domain.getColheadheight();
			if(colheadheight==null||colheadheight.length()<0) {
                colheadheight="0";
            }
			return colheadheight;
		}
		public void setColheadheight(String colheadheight) {
			this.colheadheight = colheadheight;
		}
		public String getDatarowcount() {
			datarowcount=this.domain.getDatarowcount();
			if(datarowcount==null||datarowcount.length()<=0) {
                datarowcount="0";
            }
			if("0".equals(datarowcount)) {
                auto_heigh=true;
            }
			return datarowcount;
		}
		
		public void setDatarowcount(String datarowcount) {
			this.datarowcount = datarowcount;
		}
		public void setLs(String ls) {
			this.ls = ls;
		}		
		private String getOrgnId(String nFlag,String nId,String cBase)
		{
			StringBuffer nIdSql=new StringBuffer();
			if("4".equals(nFlag))
			{
			    nIdSql.append("select k01.e01a1 as orgid from k01,");
			    nIdSql.append(cBase);
			    nIdSql.append("A01 where k01.e01a1=");
			    nIdSql.append(cBase);
			    nIdSql.append("a01.e01a1 and ");
			    nIdSql.append(cBase);
			    nIdSql.append("a01.a0100='");
			    nIdSql.append(nId);
			    nIdSql.append("'");
			}else if("2".equals(nFlag))
			{
				 nIdSql.append("select B01.b0110 as orgid from B01,");
				 nIdSql.append(cBase);
				 nIdSql.append("A01 where B01.b0110=");
				 nIdSql.append(cBase);
				 nIdSql.append("a01.b0110 and ");
				 nIdSql.append(cBase);
				 nIdSql.append("a01.a0100='");
				 nIdSql.append(nId);
				 nIdSql.append("'");
			}
			RowSet rs=null;
			try
			{
				ContentDAO dao=new ContentDAO(this.conn);
				rs=dao.search(nIdSql.toString());
				if(rs.next())
				{
					nId=rs.getString("orgid");
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return nId;
	}
		/**
		 * 单位的SQL
		 * @param rgrid
		 * @param cBase
		 * @param userview
		 * @param nFlag
		 * @param keyType
		 * @param cFldnames
		 * @return
		 */
	public String  getUnitSql(RGridView rgrid,String cBase,UserView userview,byte nFlag,String keyType,String cFldnames)
	{
		StringBuffer cSql=new StringBuffer();
		String cStr="";
		String cStr1="";	
		try{
		      cStr="B01";   //主集表名
		      cStr1=this.domain.getSetname(); //子集表名
		      if(!"B01".equals(cStr1))   //unit subset
		      {
		    	  cSql.append(getSubsetSql(cStr1,rgrid,userbase,userview,nFlag,UNITKEYTYPE,cFldnames));
		      }else
		      {
		    	    cSql.append("SELECT ");				    
				    cSql.append(cFldnames);
				    cSql.append(" From ");
				    cSql.append(cStr1);
				    cSql.append(" WHERE ");
				    cSql.append(cStr1);
				    cSql.append(".");
				    cSql.append("B0110='");
				    cSql.append(this.nId);
				    cSql.append("'");
		      }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return cSql.toString();
	}
	/**
	 * 目标计划表
	 * @param rgrid
	 * @param cBase
	 * @param userview
	 * @param nFlag
	 * @param keyType
	 * @param cFldnames
	 * @return
	 */
	public String  getPlanSql(RGridView rgrid,String cBase,UserView userview,byte nFlag,String keyType,ArrayList fieldlist)
	{
		StringBuffer cSql=new StringBuffer();
		String cStr="";
		String cStr1=this.domain.getSetname();;	
		String perPlanId=rgrid.getPlan_id();
		StringBuffer fields=new StringBuffer();
		String field="";
		ArrayList<String> list=getPlanobjCond(perPlanId,this.userbase,this.nId);
		String objCond=list.get(0);
		String object_type=list.get(1);
		if("P04".equalsIgnoreCase(cStr1))
		{
			GetCardCellValue cardcell=new GetCardCellValue();
			StringBuffer cSql1=new StringBuffer(); 
			cSql1.append("SELECT ");
    		cSql1.append(cStr1);
    		cSql1.append(".seq From ");
    		cSql1.append(cStr1);
        	if(rgrid.getMode() !=null && rgrid.getMode().length()>0 && Integer.parseInt(rgrid.getMode()) >=5)		{
        		
    			//有一个cell没有编写上 带有条件的
    			String cValue="";
				try {
					cValue = cardcell.GetSqlCond(rgrid,cBase,userview,nFlag);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			if(cValue==null||cValue.length()<=0) {
                    cValue="1=1";
                }
    			cSql1.append(" WHERE ");
    			cSql1.append(cValue);
    			cSql1.append(" AND p04.plan_id="+perPlanId+"");	
    			cSql1.append(" AND "+Sql_switcher.isnull("P04.Chg_type", "0")+"<>3");
    			cSql1.append(" AND ");	
    			if("a0100".equalsIgnoreCase(keyType))
    			{
    				if("2".equals(object_type)) {
                        cSql1.append(" "+cStr1+"."+keyType+"='"+this.nId+"' and upper(p04.nbase)='"+cBase.toUpperCase()+"'");
                    } else {
                        cSql1.append(" "+cStr1+"."+keyType+"='"+this.nId+"' ");
                    }
    			}
    			else 
    			{
    				cSql1.append(" "+cStr1+"."+keyType+"='"+this.nId+"'");
    			}
    			cSql1.append(" ORDER BY ");
    			cSql1.append(cStr1);
    			cSql1.append(".seq");
    		}	
    		else
    		{
    			//组合子集中所有纪录的sql
    			cSql1.append(" WHERE ");
    			cSql1.append(" p04.plan_id="+perPlanId+"");
    			cSql1.append(" AND "+Sql_switcher.isnull("P04.Chg_type", "0")+"<>3");
    			cSql1.append(" AND ");	
    			if("a0100".equalsIgnoreCase(keyType))
    			{
    				if("2".equals(object_type)) {
                        cSql1.append(" "+cStr1+"."+keyType+"='"+this.nId+"' and upper(p04.nbase)='"+cBase.toUpperCase()+"'");
                    } else {
                        cSql1.append(" "+cStr1+"."+keyType+"='"+this.nId+"' ");
                    }
    			}
    			else 
    			{
    				cSql1.append(" "+cStr1+"."+keyType+"='"+this.nId+"'");
    			}
    			
    			cSql1.append(" ORDER BY ");
   			    cSql1.append(cStr1);
   			    cSql1.append(".seq");
    		}	
    		ResultSet rset = null;
    	    ContentDAO dao = null;
    		Connection conn = null;
    		int nI=0;
    		int[] narrId=new int[1000];  // FIXME 记录超1000会有问题
    			try{
    				conn = AdminDb.getConnection();
    				dao = new ContentDAO(conn);
    				//查询出该人员的该子集的所有纪录
    				rset=dao.search(cSql1.toString());
    				for(nI=0;rset.next();nI++)
    				{
    					//把各个纪录放到数组中以便查询出符合条件的某条纪录
    					narrId[nI +1]=Integer.parseInt(rset.getString("seq"));
    					if(narrId[nI + 1]<NMIN) {
                            NMIN=narrId[nI + 1];
                        }
    					if(narrId[nI +1]>NMAX) {
                            NMAX=narrId[nI + 1];
                        }
    				}
    				
    			}catch (SQLException sqle){
    	            sqle.printStackTrace();
                }
                catch (GeneralException ge){
    	            ge.printStackTrace();
                }
                finally{
    	          try{
    		        if (rset != null){
    			     rset.close();
    		        }
    		       if (conn != null){
    			     conn.close();
    		       }
    	          }catch (SQLException sql){
    		         sql.printStackTrace();
    	         }
             }
			ArrayList bodyIds=new ArrayList();
			ArrayList rbodyIds=new ArrayList();
			for(int i=0;i<fieldlist.size();i++)
		    {
				field=this.fieldlist.get(i).toString();
				if((field.indexOf("score_")!=-1&&!"score_org".equals(field)))
				{
					bodyIds.add(field.substring(6)); 
				}
				if(field.indexOf("reasons_")!=-1){
					//rbodyIds.add(field.substring(8));
				}
				if("P0400".equalsIgnoreCase(field)|| "NBASE".equalsIgnoreCase(field)|| "A0100".equalsIgnoreCase(field)|| "B0110".equalsIgnoreCase(field)
						|| "Chg_type".equalsIgnoreCase(field)|| "plan_id".equalsIgnoreCase(field)|| "item_id".equalsIgnoreCase(field)|| "seq".equalsIgnoreCase(field)
						||(field.indexOf("score_")!=-1&&!"score_org".equals(field))||field.indexOf("reasons_")!=-1
						)
				{
					
				}else {
					fields.append(",");
					if("P0424".equalsIgnoreCase(field)) // P0424调整人只取姓名: Usr00000629/张普法
					{
						fields.append(Sql_switcher.substr("P0424", "13", Sql_switcher.length("P0424"))+" as P0424");
					}else {
                        fields.append(field);
                    }
				}
		    }
			String p04View="(select P0400,NBASE,A0100,B0110,Chg_type,plan_id,item_id,seq"+ fields.toString() +" from P04) P04";
			if(bodyIds.size()<=0&&rbodyIds.size()<=0)
			{
				cSql.append("SELECT * From "+p04View+" where "+objCond +" ");
			}else
			{
				
				String from="";
				from=p04View;
				for(int i=0;i<bodyIds.size();i++)
				{
					 String bodyid=bodyIds.get(i).toString();
					 String scorefld = "score_" + bodyIds.get(i);
					 String atab = "a" + bodyIds.get(i);
					 if(bodyid.indexOf("-")!=-1)
					 {
						 scorefld=scorefld.replaceAll("-", "X");
						 atab=atab.replaceAll("-", "X");
					 }
					 StringBuffer mainBodyCond =new StringBuffer();
					 mainBodyCond.append(" mainbody_Id in (select mainbody_id FROM per_mainbody");
					 mainBodyCond.append(" where plan_id = " + perPlanId + " and");
					 mainBodyCond.append(" object_id = '" + this.nId+ "' and");
					 mainBodyCond.append(" body_id = " + bodyid );
					 mainBodyCond.append(")");
					 if(i>0)
					 {
						 from="("+from+")";
					 }
					 from+=" left join (select P0400, Avg(" + Sql_switcher.isnull("score", "0")+ ") AS "+ scorefld+"";
					 from+=" from per_target_evaluation";
					 from+=" where "+mainBodyCond.toString()+" and plan_id = "+perPlanId;
					 from+=" group by P0400";
					 from+=")"+atab;
					 from+=" on  P04.P0400 = " + atab + ".P0400";
					
				}
				for(int i=0;i<rbodyIds.size();i++)
				{
					 String bodyid=rbodyIds.get(i).toString();
					 String scorefld = "reasons_" + rbodyIds.get(i);
					 String atab = "b" + rbodyIds.get(i);
					 if(bodyid.indexOf("-")!=-1)
					 {
						 scorefld=scorefld.replaceAll("-", "X");
						 atab=atab.replaceAll("-", "X");
					 }
					 /*StringBuffer mainBodyCond =new StringBuffer();
					 mainBodyCond.append(" mainbody_Id in (select mainbody_id FROM per_mainbody");
					 mainBodyCond.append(" where plan_id = " + perPlanId + " and");
					 mainBodyCond.append(" object_id = '" + this.nId+ "' and");
					 mainBodyCond.append(" body_id = " + bodyid );
					 mainBodyCond.append(")");*/
					 if(i>0)
					 {
						 from="("+from+")";
					 }
					 from+=" left join (select P0400,e.reasons AS "+ scorefld+",m.a0101 as "+scorefld+"_a";
					 from+=" from per_mainbody";
					 from+=" m left join per_target_evaluation e on m.mainbody_id=e.mainbody_id where m.plan_id = "+perPlanId+" and m.object_id = '" + this.nId+ "' and m.body_id = "+bodyid;
					 from+=")"+atab;
					 from+=" on  P04.P0400 = " + atab + ".P0400";
					
				}
				
				cSql.append("select * from " +from);
				cSql.append(" where "+objCond);
			}
			if(rgrid.getMode() !=null && rgrid.getMode().length()>0)
    		{
    			switch(Integer.parseInt(rgrid.getMode()))
    			{					        		
    				case 0:
    				{
    				   cSql.append(" AND ");
    				   cSql.append(cStr1);
    				   cSql.append(".seq=");
    				   if(nI>=rgrid.getRcount()) {
                           cSql.append(narrId[nI-rgrid.getRcount()+1]);
                       } else {
                           cSql.append(0);
                       }
    				  
    				   break;
    				}
    				case 5:
    				{
    					cSql.append(" AND ");
    				    cSql.append(cStr1);
    					cSql.append(".seq=");
    					if(nI>=rgrid.getRcount()) {
                            cSql.append(narrId[nI-rgrid.getRcount() +1]);
                        } else {
                            cSql.append(0);
                        }
    					break;
    				}
    				case 1:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append(".seq>=");
    					if(nI>=rgrid.getRcount()) {
                            cSql.append(narrId[nI-rgrid.getRcount()+1]);
                        } else {
                            cSql.append(0);
                        }
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append(".seq<=");
    					cSql.append(NMAX);
    					break;
    				}
    				case 2:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    				    cSql.append(".seq=");
    					cSql.append(narrId[rgrid.getRcount()]);
    					break;
    				}
    				case 7:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append(".seq=");
    					cSql.append(narrId[rgrid.getRcount()]);
    					break;
    				}
    				case 3:
    				{
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append(".seq<=");
    					if(nI>=rgrid.getRcount()) {
                            cSql.append(narrId[rgrid.getRcount()]);
                        } else {
                            cSql.append(NMAX);
                        }
    					cSql.append(" AND ");
    					cSql.append(cStr1);
    					cSql.append(".seq>=");
    					cSql.append(NMIN);
    					
    					break;
    				}
    				case 4:
    				{
    					cSql.append(" AND ");
    					try {
							cSql.append(cardcell.GetSqlCond(rgrid,cBase,userview,nFlag));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					
    					break;
    				}
    				case 6:
    				{
    					int nCur=0;
    					if(nI>=rgrid.getRcount()) {
                            nCur=nI-rgrid.getRcount()+1;
                        } else {
                            nCur=1;
                        }
    					 cSql.append(" AND ");
    					 cSql.append(cStr1);
    					 cSql.append(".seq IN(-1");
    					 for(int nK=nCur;nK<=nI;nK++)
    					 {
    						cSql.append("," + narrId[nK]);
    					 }
    					if(nI>0)
    					{
    					  //cValue=cSql.toString().substring(0,cSql.toString().length()-1);  //去掉最后一个字符
    					  //cSql.delete(0,cSql.length());
    				      //cSql.append(cValue);
    				    }
    				    cSql.append(")" );
    				    
    					break;
    				}
    				case 8:
    				{
    					int nCur=0;
    					if(nI>=rgrid.getRcount()) {
                            nCur=rgrid.getRcount();
                        } else {
                            nCur=nI;
                        }
    					 cSql.append(" AND ");
    					 cSql.append(cStr1);
    					 cSql.append(".seq IN(-1");
    					  for(int nK=1;nK<=nCur;nK++)
    					  {
    						cSql.append( "," + narrId[nK]);
    					  }
    					  if(nI>0){
    					    // cValue=cSql.toString().substring(0,cSql.toString().length()-1);
    					    // cSql.delete(0,cSql.length());
    					    // cSql.append(cValue);
    					  }
    					  cSql.append(")" );    					 
    					  break;
    				}					        		
    			}
    		}
    		cSql.append(" order by "+cStr1+".seq");
		}else if("per_key_event".equalsIgnoreCase(cStr1)){
			XmlSubdomain xmlSubdomain=new XmlSubdomain(rgrid.getSub_domain());
			String sum = xmlSubdomain.getParaAttribute("sum");
			sum = (sum==null||sum.length()==0)?"1":sum;
			if(!"1".equals(sum)){
				cSql.append("select * from (");
				cSql.append("select event_id as event_id, B0110 as B0110, E0122 as E0122, ");
				cSql.append(" A0101 as A0101, A0100 as A0100, NBASE as NBASE, ");
				cSql.append(Sql_switcher.sqlToChar("key_event")+" as key_event, ");
				cSql.append(" score as score, busi_date as busi_date, per_key_event.point_id, ");
				cSql.append(Sql_switcher.sqlToChar("pointname")+" as pointname, ");
				cSql.append(Sql_switcher.sqlToChar("description")+" as description, object_type as object_type ");
				cSql.append(" from per_key_event left join per_point ");
				cSql.append("  on per_key_event.point_id = per_point.point_id ");
				cSql.append("where " + objCond );
			    cSql.append(") per_key_event ");  // 避免历史记录定位条件指标为per_point.pointname时，表名不对
				cSql.append(" where " + objCond);
				cSql.append(" order by "+cStr1+".event_id");
			}else{
				cSql.append("select * from (");
				cSql.append("select max(event_id) as event_id, max(B0110) as B0110, max(E0122) as E0122, ");
				cSql.append(" max(A0101) as A0101, max(A0100) as A0100, max(NBASE) as NBASE, ");
				cSql.append(" max("+Sql_switcher.sqlToChar("key_event")+") as key_event, ");
				cSql.append(" sum(score) as score, max(busi_date) as busi_date, per_key_event.point_id, ");
				cSql.append(" max("+Sql_switcher.sqlToChar("pointname")+") as pointname, ");
				cSql.append(" max("+Sql_switcher.sqlToChar("description")+") as description, max(object_type) as object_type ");
				cSql.append(" from per_key_event left join per_point ");
				cSql.append("  on per_key_event.point_id = per_point.point_id ");
				cSql.append("where " + objCond );
				cSql.append(" group by per_key_event.point_id ");
			    cSql.append(") per_key_event ");  // 避免历史记录定位条件指标为per_point.pointname时，表名不对
				cSql.append(" where " + objCond);
				cSql.append(" order by "+cStr1+".event_id");
			}
		}		
		return cSql.toString();
	}
	
	private ArrayList<String> getPlanobjCond(String plan_id,String userbase,String nid)
    {
		ArrayList<String> list=new ArrayList<String>();
    	String sql="select object_type from per_plan where plan_id = " + plan_id;
    	String cStr1=this.domain.getSetname();
    	
    	ContentDAO dao=new ContentDAO(this.conn);
    	StringBuffer objCond=new StringBuffer();
    	RowSet rs=null;
    	String object_type="";    	
    	try {
			rs=dao.search(sql);
			if(rs.next()) {
                object_type=rs.getString("object_type");
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
    	if("P04".equalsIgnoreCase(cStr1))
    	{
    		objCond.append(" P04.plan_id = " + plan_id+ " and ");
        	objCond.append(Sql_switcher.isnull("P04.Chg_type", "0")+ "<> 3");  // 不显示已删除的任务
    		if(object_type!=null&& "2".equals(object_type)) {
                objCond.append(" and upper(p04.nbase)='"+userbase.toUpperCase()+"' and p04.a0100='"+nid+"'");
            } else {
                objCond.append(" and p04.b0110='"+nid+"'");
            }
    	}else if("per_key_event".equalsIgnoreCase(cStr1)){
    		String dates[]=getPerDate(plan_id);
    		objCond.append(" Busi_date>="+Sql_switcher.dateValue(dates[0]));
    		objCond.append("  AND Busi_date<=" + Sql_switcher.dateValue(dates[1]));
    		if(object_type!=null&& "2".equals(object_type))
    		{
    			objCond.append("and upper(per_key_event.nbase)='"+userbase.toUpperCase()+"'");
    			objCond.append("and per_key_event.A0100 ='"+nid+"'");
    			objCond.append("and Object_type='"+object_type+"'");
    		}else
    		{
    			objCond.append(" and per_key_event.b0110='"+nid+"'");
    			objCond.append("and Object_type='1'");
    		}       
    	}
    	list.add(objCond.toString());
    	list.add(object_type);
    	
		return list;
    }
	/**
	 * 关键子集取时间
	 * @param plan_id
	 * @return
	 */
	private String[] getPerDate(String plan_id)
	{
		String sql="select * from per_plan where plan_id ="+plan_id;
		String perdates[]=new String[2];
		String start_date="";
		String end_date="";
		//考核周期（AssessCycle）：(0,1,2,3,7)=(年度,半年,季度,月度,不定期)
		int cycle=0;
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			rs=dao.search(sql);
			if(rs.next())
			{
				cycle=rs.getInt("cycle");
				switch(cycle)
				  {	
						case 0: //年度
						{
							String theyear=rs.getString("theyear");
							start_date=theyear+".01.01";
							end_date=theyear+".12.31";
							break;
						}
						case 1: //半年
						{
							String theyear=rs.getString("theyear");
							String thequarter=rs.getString("thequarter");
							int nY=Integer.parseInt(theyear);
							int nM=Integer.parseInt(thequarter);
							int nMonth=6*(nM-1)+1;
							start_date=theyear+"."+nMonth+".01";
							nY=nY+(nM/2);
							nMonth=((6*nM+1)%12);
							end_date=nY+"."+nMonth+".01";
							Date date=DateUtils.getDate(end_date, "yyyy.MM.dd");
							date=DateUtils.addDays(date, -1);
							end_date=DateUtils.format(date, "yyyy.MM.dd");							
							break;
						}
						case 2: //季度
						{
							String theyear=rs.getString("theyear");
							String thequarter=rs.getString("thequarter");
							int nY=Integer.parseInt(theyear);
							int nM=Integer.parseInt(thequarter);
							int nMonth=3*(nM-1)+1;
							start_date=theyear+"."+nMonth+".01";
							nY=nY+(nM /4);
							nMonth=(3*nM+1) % 12;
							end_date=nY+"."+nMonth+".01";
							Date date=DateUtils.getDate(end_date, "yyyy.MM.dd");
							date=DateUtils.addDays(date, -1);
							end_date=DateUtils.format(date, "yyyy.MM.dd");			
							break;
						}
						case 3: //月度
						{
							String theyear=rs.getString("theyear");
							String month=rs.getString("themonth");
							int nY=Integer.parseInt(theyear);
							int nM=Integer.parseInt(month);
							start_date=theyear+"."+month+".01";
							nY=nY+(nM / 12);
							int nMonth=(nM+1) % 12;
							end_date=nY+"."+nMonth+".01";
							Date date=DateUtils.getDate(end_date, "yyyy.MM.dd");
							date=DateUtils.addDays(date, -1);
							end_date=DateUtils.format(date, "yyyy.MM.dd");	
							break;
						}
						case 7: //不定期
						{
							Date date=rs.getDate("start_date");
							start_date=DateUtils.format(date, "yyyy.MM.dd");
							date=rs.getDate("end_date");
							end_date=DateUtils.format(date, "yyyy.MM.dd");							
							break;
						}
				  }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		perdates[0]=start_date;
		perdates[1]=end_date;
		return perdates;
	}
	/**
	 * 岗位
	 * @param rgrid
	 * @param cBase
	 * @param userview
	 * @param nFlag
	 * @param keyType
	 * @param cFldnames
	 * @return
	 */
	public String  getPostSql(RGridView rgrid,String cBase,UserView userview,byte nFlag,String keyType,String cFldnames)
	{
		StringBuffer cSql=new StringBuffer();
		String cStr="";
		String cStr1="";	
		try{
			   cStr="K01";   //主集表名   
		      cStr1=this.domain.getSetname(); //子集表名
		      if(!"K01".equals(cStr1))   // post sub set
			  {
		    	  cSql.append(getSubsetSql(cStr1,rgrid,userbase,userview,nFlag,keyType,cFldnames));
		      }else
		      {
		    	  cSql.append("SELECT ");					
				  cSql.append(cFldnames);
				  cSql.append(" From ");
				  cSql.append(cStr1);
				  cSql.append(" WHERE ");
				  cSql.append(cStr1);
				  cSql.append(".");
				  cSql.append("E01A1='");
				  cSql.append(nId);
				  cSql.append("'");
		      }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return cSql.toString();
	}
	
	/**
	 * 基准岗位
	 * @param rgrid
	 * @param cBase
	 * @param userview
	 * @param nFlag
	 * @param keyType
	 * @param cFldnames
	 * @return
	 */
	public String getStdPosSql(RGridView rgrid,String cBase,UserView userview,byte nFlag,String keyType,String cFldnames)
	{
		StringBuffer cSql=new StringBuffer();
		String cStr="";
		String cStr1="";	
		try{
			   cStr="H01";   //主集表名   
		      cStr1=this.domain.getSetname(); //子集表名
		      if(!"H01".equals(cStr1))   // sub set
			  {
		    	  cSql.append(getSubsetSql(cStr1,rgrid,userbase,userview,nFlag,keyType,cFldnames));
		      }else
		      {
		    	  cSql.append("SELECT ");					
				  cSql.append(cFldnames);
				  cSql.append(" From ");
				  cSql.append(cStr1);
				  cSql.append(" WHERE ");
				  cSql.append(cStr1);
				  cSql.append(".");
				  cSql.append(keyType+"='");
				  cSql.append(nId);
				  cSql.append("'");
		      }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return cSql.toString();
	}
	
    public int getTable_width(Connection conn,RGridView rgrid) {
		
    	String id=rgrid.getTabid();
    	StringBuffer sql=new StringBuffer();
    	sql.append("select (MAX(RLeft + Rwidth) - MIN(RLeft)) AS Expr1 ");
    	sql.append(" from RGrid ");
    	sql.append(" where (Tabid = '"+id+"') ");
    	sql.append(" AND (Rtop =(SELECT MIN(rtop) FROM RGrid WHERE Tabid = '"+id+"'))");
    	ContentDAO dao=new ContentDAO(this.conn);
    	float  width=0;
    	try
    	{
    		RowSet rs=dao.search(sql.toString());
    		
    		if(rs.next())
    		{
    			String ws=rs.getString("Expr1");
    			width=Float.parseFloat(ws);
    		} 
     	}catch(Exception e)       
        {
        	e.printStackTrace();
        }
     	float factWidth=Float.parseFloat(rgrid.getRwidth());
        if(width==factWidth) {
            this.isMaxwidth=true;
        } else {
            this.isMaxwidth=false;
        }
		return table_width;
	}
    public int getTable_height(Connection conn,RGridView rgrid) {
		
    	String id=rgrid.getTabid();
    	StringBuffer sql=new StringBuffer();
    	sql.append("select (MAX(RTop + RHeight) - MIN(RTop)) AS Expr1 ");
    	sql.append(" from RGrid ");
    	sql.append(" where (Tabid = '"+id+"') ");
    	sql.append(" AND (RLeft =(SELECT MIN(RLeft) FROM RGrid WHERE Tabid = '"+id+"'))");
    	ContentDAO dao=new ContentDAO(this.conn);
    	float  width=0;
    	try
    	{
    		RowSet rs=dao.search(sql.toString());
    		
    		if(rs.next())
    		{
    			String ws=rs.getString("Expr1");
    			width=Float.parseFloat(ws);
    		} 
     	}catch(Exception e)       
        {
        	e.printStackTrace();
        }
     	float factWidth=Float.parseFloat(rgrid.getRwidth());
        if(width==factWidth) {
            this.isMaxwidth=true;
        } else {
            this.isMaxwidth=false;
        }
		return table_width;
	}
	public void setTable_width(int table_width) {
		this.table_width = table_width;
	}	
	private ArrayList getsubClassRows(float []para_array,RGridView rgrid,String disting_pt,String sql)
	{
		ArrayList list =new ArrayList();
		String colhead=this.domain.getColhead();
		MadeFontsizeToCell mc=new MadeFontsizeToCell(); 
		String plan_id= rgrid.getPlan_id();	
		int fontsize=12;
		if(rgrid.getFontsize()!=null&&rgrid.getFontsize().length()>0) {
            fontsize=Integer.parseInt(rgrid.getFontsize());
        }
		int strHeight=0;
		int titleHeight=0;
		if(colhead!=null&& "true".equals(colhead))//显示字段
    	{
			int max_row=0;
			HashMap one_hash=new HashMap();
			for(int i=0;i<this.fieldlist.size();i++)
	    	{
	    		String field=this.fieldlist.get(i).toString();
	    		HashMap hash=(HashMap)this.hash.get(field);
	    		String class_str="";  
	    		int widthn=((int)para_array[i]);  
	    		String title=(String)hash.get("title");
	    		int rows=mc.ReChangeRowNum(widthn,title,fontsize,rgrid.getFontName(),"","A");
	    		if(max_row<rows) {
                    max_row=rows;
                }
	    		hash.put("type","A");
	    		hash.put("width",new Integer(widthn));
	    		hash.put("rows",new Integer(rows));
	    		String []align=new String[2];
	    		align[0]="center";
	    		align[1]="middle";
	    		hash.put("align",align);
	    		if(this.colheadheight==null|| "0".equals(this.colheadheight)) {
                    titleHeight=mc.ReHeight(fontsize,rgrid.getFontName());
                } else {
                    titleHeight=(int)(Float.parseFloat(this.colheadheight)*3);
                }
	    		hash.put("height",new Integer(titleHeight));
	    		one_hash.put(field,hash);
	    		one_hash.put("max_row",new Integer(max_row));
	    	}
			list.add(one_hash);
			newline=newline+max_row;
			fact_row++;
			this.fact_height=this.fact_height-titleHeight;
    	}
		try
    	{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rs=null;  
			RowSet rs1 = null;
    		rs=dao.search(sql);
    		String class_str="";
    		String fieldValue="";
    		ArrayList strLst=null;
        	ArrayList strLstNoPre=null;
        	GetCardCellValue cellValue=new GetCardCellValue();
        	int rowCount=100;
        	int dataHeight=0;
        	if(!auto_heigh)
        	{	rowCount=Integer.parseInt(this.datarowcount);
        	    dataHeight=(this.fact_height-rowCount)/rowCount;
        	}
        	int row=0;
    		while(rs.next())
    		{
    			int max_row=0;
    			row++;
    		    if(row>rowCount) {
                    break;
                }
    			HashMap one_hash=new HashMap();
    			for(int i=0;i<this.fieldlist.size();i++)
    			{
    				String field=this.fieldlist.get(i).toString();
    				int widthn=((int)para_array[i]); 
    				YkcardSubclassFieldBean bean=new YkcardSubclassFieldBean();
    				bean=(YkcardSubclassFieldBean)this.cellhash.get(field);
    				strLst=new ArrayList();   
    				strLstNoPre=new ArrayList();    				
    				switch(bean.getItemtype().toUpperCase().charAt(0))
    				{
    					case CELLFIELD_STRINGTYPE:         //字符类型
    					{   
    					  String p0400="";
    					  if(field.indexOf("reasons_")!=-1){
    						  String bodyid=field.substring(8);
    						  field=field.replaceAll("-", "#");
    						  p0400=rs.getString("p0400");
    						  String tmpsql = "select P0400,e.reasons AS "+ field+",m.a0101 as "+field+"_a";
    						  tmpsql+=" from per_mainbody";
    						  tmpsql+=" m left join per_target_evaluation e on m.mainbody_id=e.mainbody_id where m.plan_id = "+plan_id+" and m.object_id = '" + this.nId+ "' and m.body_id = "+bodyid+" and p0400='"+p0400+"'";
    						  String ss="select * from (select * from p04) p04 left join ("+tmpsql+") tt on p04.p0400=tt.p0400 ";
    						  ss+=" "+sql.substring(sql.lastIndexOf("where"));
    						  rs1 = dao.search(ss);
    						  StringBuffer reasons= new StringBuffer();
    						  String tmpname="";
    						  while(rs1.next()){
    							  if(rs1.getString(field+"_a")==null||rs1.getString(field)==null) {
                                      continue;
                                  }
    							  if(this.needfilter){
    								  if(reasons.length()>0){
    									  reasons.insert(0, tmpname);
    									  reasons.append(rs1.getString(field+"_a")+":"+(rs1.getString(field)==null?"":rs1.getString(field))+"<br>");
    									  tmpname="";
    								  }else{
    									  tmpname=rs1.getString(field+"_a")+":";
    									  reasons.append((rs1.getString(field)==null?"":rs1.getString(field))+"<br>");
    								  }
    							  }else{
    								  if(reasons.length()>0){
    									  reasons.insert(0, tmpname);
    									  reasons.append(rs1.getString(field+"_a")+":"+(rs1.getString(field)==null?"":rs1.getString(field))+"\n");
    									  tmpname="";
    								  }else{
    									  tmpname=rs1.getString(field+"_a")+":";
    									  reasons.append((rs1.getString(field)==null?"":rs1.getString(field))+"\n");
    								  }
    							  }
    						  }
    						  fieldValue = reasons.toString(); 
    					  }else{
    						  fieldValue=rs.getString(field);
    					  }
    					  if(fieldValue!=null && fieldValue.length()>0)
    					  {
    						strLst.add(fieldValue);//明码于代码输出    						
    					  }					     
    						break;
    					}
    					case CELLFIELD_MEMOTYPE:          //备注类型 
    					{
    						String valueresult="";
    						fieldValue=rs.getString(field);
    						fieldValue=fieldValue==null?"":fieldValue;
    						if("summarizes".equalsIgnoreCase(field)){
    							if(fieldValue.length()>0&&fieldValue.toLowerCase().indexOf("<?xml ")!=-1){
	    							XmlSubdomain xmlSubdomain = new XmlSubdomain(fieldValue);
	    							fieldValue = xmlSubdomain.getRecString();
    							}
    						}
    						if(needfilter){
	    						if(fieldValue!=null && fieldValue.length()>0)
	    						{
	    							for(int r=0;r<fieldValue.length();r++)
	    							{
	    							   if("\n".equals(fieldValue.substring(r,r+1))) {
                                           valueresult+="<br>";
                                       } else {
                                           valueresult+=fieldValue.substring(r,r+1);
                                       }
	    							}					  
	    						   strLst.add(valueresult);//明码于代码输出    						   
	    						}
    						}else {
                                strLst.add(fieldValue);
                            }
    						
    						break;
    					}
    					case CELLFIELD_DATETYPE:               //日期控制格式
    					{
    						  
    						/*String strdata=rs.getString(field);
    						java.sql.Date fdate=null;
    						if(strdata!=null && strdata.length()>=4)
    							fdate=rs.getDate(field);
    						getCellDateListValue(bean, strLst, strdata,fdate);*/
    						Date date=rs.getTimestamp(field);
    						if(date!=null)
    						{
    							String strdata=DateUtils.format(date,"yyyy.MM.dd HH:mm:ss");
        						java.sql.Timestamp fdate=null;
        						if(strdata!=null && strdata.length()>=4) {
                                    fdate=rs.getTimestamp(field);
                                }
        						getCellDateListValue(bean, strLst, strdata,fdate);
    						}else
    						{
    							getCellDateListValue(bean, strLst, "",null);
    						}						
    						break;
    					}
    					case CELLFIELD_NUMBERTYPE:            //数值类型
    					{
    						float fv=rs.getFloat(field.replaceAll("-", "X")); 
    						if(display_zero!=null&& "1".equals(display_zero)&&fv==0)
    						{
    							//strLst;
    						}else if(fv==0)
    						{
    							
    						}else {
                                getCellDecimalListValue(bean, strLst, fv,strLstNoPre);
                            }
    						break;				
    					}
    				}
    				if(bean.getCodesetid()!=null&&bean.getCodesetid().length()>0&&!"0".equals(bean.getCodesetid()))
    				{			
    		     	  strLst=cellValue.GetValueofField(strLst,bean.getCodesetid());  //代码的转换
    				}  
    				if("P04".equalsIgnoreCase(bean.getFieldsetid())&&"item_id".equalsIgnoreCase(field)){
    					try{		
    						for(int nIdx=0;nIdx<strLst.size();nIdx++)
    						{
    							if(strLst.get(nIdx) !=null)
    							{
    								String codevalue=strLst.get(nIdx).toString();
    							  	if(codevalue.length()>0)
    							  	{
    							  		rs1 = dao.search("select itemdesc from per_template_item where item_id="+codevalue);
    							  		if(rs1.next()){
    							  			strLst.set(nIdx,rs1.getString("itemdesc"));
    							  		}else{
    							  			strLst.set(nIdx,"");
    							  		}
    							  	}
    							  	else {
                                        strLst.set(nIdx,"");
                                    }
    							}
    							else
    							{
    								strLst.set(nIdx,"");
    							}		
    						}
    						
    					}catch (Exception e){
    					}
    					finally{	 
    					}
    				}
    				String[] align=mc.getAlign(bean.getAlign());	
    				
    	    		String fontStr="";  
    	    		int rows=1;
    	    		
    	    		if(strLst==null||strLst.size()<=0)
    	    		{
    	    			fontStr="";
    	    			if(max_row<rows) {
                            max_row=rows;
                        }
    	    		}else
    	    		{
    	    			fontStr=(String)strLst.get(0);
    	    			rows=mc.ReChangeRowNum(widthn,fontStr,fontsize,rgrid.getFontName(),"",bean.getItemtype());
    		    		if(max_row<rows) {
                            max_row=rows;
                        }
    	    		}    	    		
    	    		HashMap hash=new HashMap();   
    	    		
    	    		//上个单元格是日期型 且当前单元格同样也是日期型 且存在前缀符
    	    		if(i!=0&&StringUtils.isNotEmpty(this.fieldlist.get(i-1).toString())
    	    		 &&bean.getItemtype().toUpperCase().charAt(0)==CELLFIELD_DATETYPE&&bean.getPre() !=null && bean.getPre().length()>0) {
    	    			YkcardSubclassFieldBean lastBean=(YkcardSubclassFieldBean)this.cellhash.get(this.fieldlist.get(i-1).toString());
    	    			if(lastBean.getItemtype().toUpperCase().charAt(0)==CELLFIELD_DATETYPE) {
    	    				HashMap map=(HashMap)one_hash.get(this.fieldlist.get(i-1).toString());
    	    				if(StringUtils.isEmpty(map.get("title").toString())) {
                                fontStr="";
                            }
    	    			}
    	    			
    	    		}
    	    		
    	    		hash.put("title",fontStr);
    	    		hash.put("type",bean.getItemtype());
    	    		hash.put("width",new Integer(widthn));
    	    		hash.put("rows",new Integer(rows));    	    		
    	    		hash.put("align",align);
    	    		
    	    		if(!auto_heigh) {
                        strHeight=dataHeight;
                    } else {
                        strHeight=mc.ReHeight(fontsize,rgrid.getFontName());
                    }
    	    		hash.put("height",new Integer(strHeight));
    	    		 if(field.indexOf("reasons_")!=-1){
						  field=field.replaceAll("#", "-");
    	    		 }
    	    		one_hash.put(field,hash);    	    		
    			}
    			
    			int I9999=0;
    			ResultSetMetaData rsmd = rs.getMetaData();
    			//查询的总字段数
    			int columnCount = rsmd.getColumnCount();
    			//循环出查询的所有字段名
    			for(int i=1;i<=columnCount;i++){    
    				//得到当前查询的字段名与I9999比较
    				if("I9999".equals(rsmd.getColumnName(i))){
        				I9999=rs.getInt("I9999");
            			one_hash.put("I9999", new Integer(I9999));
        			}
    			}
    			one_hash.put("max_row",new Integer(max_row));
    			newline=newline+max_row;
	    		fact_row++;
    			list.add(one_hash);
    		}
    		
    		if(!this.auto_heigh&&row<rowCount)
    		{
    			row++;
        		for(;row<=rowCount;row++)
        		{
        			HashMap one_hash=new HashMap();
        			for(int i=0;i<this.fieldlist.size();i++)
        			{
        				String field=this.fieldlist.get(i).toString();
        				String []align=new String[2];
        	    		align[0]="center";
        	    		align[1]="middle";
        				int widthn=((int)para_array[i]);
        				int I9999=i;
        				hash.put("title","");
        	    		hash.put("type","");
        	    		hash.put("width",new Integer(widthn));
        	    		hash.put("rows",new Integer(1));    	    		
        	    		hash.put("align",align);
        	    		hash.put("I9999", new Integer(i));
        	    		if(!auto_heigh) {
                            hash.put("height",new Integer(dataHeight));
                        } else {
                            strHeight=mc.ReHeight(fontsize,rgrid.getFontName());
                        }
        	    		one_hash.put("max_row",new Integer("1"));
        	    		one_hash.put(field,hash);    	    		
        			}
        			fact_row++;
        			list.add(one_hash);
        		}
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return list;
	}
	private ArrayList reSubClassRows(float []para_array,RGridView rgrid,String disting_pt,ArrayList list,int fontsize)
	{
		this.newline=0;
		this.fact_row=0;
		MadeFontsizeToCell mc=new MadeFontsizeToCell(); 	
		ArrayList new_list=new ArrayList();
		for(int r=0;r<list.size();r++)
		{
			HashMap one_hash=(HashMap)list.get(r);
			int max_row=0;
			for(int i=0;i<this.fieldlist.size();i++)
	    	{
				String field=this.fieldlist.get(i).toString();
				HashMap hash=(HashMap)one_hash.get(field);
				int widthn=((int)para_array[i]);  
	    		String title=(String)hash.get("title");
	    		String strtype=(String)hash.get("type");
	    		int rows=mc.ReChangeRowNum(widthn,title,fontsize,rgrid.getFontName(),"",strtype);	    		
	    		if(max_row<rows) {
                    max_row=rows;
                }
	    		hash.put("rows",new Integer(rows));
	    		int strHeight=mc.ReHeight(fontsize,rgrid.getFontName());
	    		hash.put("height",new Integer(strHeight));
	    		one_hash.put(field,hash);	    		
	    	}
			one_hash.put("max_row",new Integer(max_row));
			new_list.add(one_hash);
			newline=newline+max_row;
			fact_row++;
		}	
		return new_list;
	}
	public ArrayList rowHeightNum(float []para_array,RGridView rgrid,String disting_pt,String sql)
	{
		ArrayList subClassList=getsubClassRows(para_array,rgrid,disting_pt,sql);
		int heights;
		if("800".equals(disting_pt))
	    {     
		    heights=(int)Float.parseFloat(rgrid.getRheight("1"))  +1;
	    }
	    else
	    {
		    heights=(int)Float.parseFloat(rgrid.getRheight()) +1;
	    }	
		MadeFontsizeToCell mc=new MadeFontsizeToCell(); 		
		if(rgrid.getFontsize()!=null&&rgrid.getFontsize().length()>0) {
            fontsize=Integer.parseInt(rgrid.getFontsize());
        }
//		int strHeight=mc.ReHeight(fontsize,rgrid.getFontName());	
		newline=newline==0?1:newline;
		int strHeight=heights/newline;//newline 重新计算的行数，包括内容换行
		int fact_height=(newline*strHeight)+newline/1;
		while(heights<fact_height)
		{
			fontsize=fontsize-1;
			subClassList=reSubClassRows(para_array,rgrid,disting_pt,subClassList,fontsize);
			strHeight=mc.ReHeight(fontsize,rgrid.getFontName());		
			fact_height=(newline*strHeight)+newline/1;
		}
		return subClassList;
	}
	 public String viewSubClassHtml(String infokind,String userbase, Connection conn,UserView userview,RGridView rgrid,String disting_pt,byte nFlag)
	 {
		    String colhead=this.domain.getColhead();		    
	    	String setname=this.domain.getSetname();  
	    	GetCardCellValue getCardCellValue=new GetCardCellValue();
			String isView=getCardCellValue.getISVIEWForCexpress(rgrid.getCexpress());
			if(!"1".equals(isView))
			{
				if(nFlag!=5)
		    	{
		    		if(userpriv!=null&&"selfinfo".equalsIgnoreCase(userpriv))
			    	{
			    		if(!"1".equals(fieldpurv)&& "0".equals(userview.analyseTablePriv(setname,0)))
				    	{
			    			return "";
				    	}
			    	}else
			    	{
			    		 String priv_flag="";
			    		 if(this.fenlei_type!=null&&this.fenlei_type.length()>0&&!userview.isSuper_admin()&&(nFlag==0))
			    	     {
			    	    	  priv_flag= userview.analyseSubTablePriv(this.fenlei_type,setname)+"";    	  
			    	    	  if(priv_flag==null||priv_flag.length()<=0|| "-1".equals(priv_flag)) {
                                  return "";
                              }
			    	     }else if("0".equals(userview.analyseTablePriv(setname)))
				    	 {
				    		return "";
				    	 }
			    	} 
		    	}
			}	
	    	StringBuffer html=new StringBuffer();         //单元格的高
	        int widthn;     	
	        int heightn;
	    	this.fontweight=rgrid.getFonteffect();
		    if(fontweight !=null && "2".equals(fontweight))         //字体是否时粗体
            {
                fontweight="bold";
            } else {
                fontweight="normal";
            }
		    if("800".equals(disting_pt))
	        {     
		       widthn=(int)Float.parseFloat(rgrid.getRwidth("1")) + 1;
		       heightn=(int)Float.parseFloat(rgrid.getRheight("1"))  +1;
		    }
	        else
	        {
		       widthn=(int)Float.parseFloat(rgrid.getRwidth()) + 1;
		       heightn=(int)Float.parseFloat(rgrid.getRheight()) +1;
		    }	
		    this.fact_height=heightn;
		    this.fact_width=widthn;
	    	float para_array[]=viewParaSrray(widthn);
	    	String sql="";
	    	StringBuffer fields=new StringBuffer();
	        for(int i=0;i<this.fieldlist.size();i++)
	        {
	        	fields.append(this.fieldlist.get(i).toString()+",");
	        }
	        if(fields.length()>0){
	        	fields.setLength(fields.length()-1);
	        	fields.append(",I9999");
	        }

	        if(fields==null||fields.length()<=0) {
                return "&nbsp;";
            }
	        String cBase="";          //人员库
	    	switch(nFlag)
			{	
				//人员库
				case 0:
				{
					sql=getPersonSql(rgrid,userbase,userview,nFlag,PERSONKEYTYPE,fields.toString());				
					break;
				}//岗位库
				case 4:
				{
					if("1".equals(infokind))
					{
						nId=getOrgnId("4",nId,userbase);				
					}else
					{
						cBase="K";
					}
					sql=getPostSql(rgrid,userbase,userview,nFlag,POSTKEYTYPE,fields.toString());
					break;
				}
//				单位库
				case 2:
				{
					if("1".equals(infokind))
					{
						nId=getOrgnId("2",nId,userbase);		
					}else
					{
						cBase="B";
					}
					sql=getUnitSql(rgrid,userbase,userview,nFlag,UNITKEYTYPE,fields.toString());
					break;
				}case 5:
				{
					sql=getPlanSql(rgrid,userbase,userview,nFlag,PERSONKEYTYPE,this.fieldlist);
					break;
				}
				case 6:
				{
					String cName=this.domain.getSetname();
			        String cStr1=this.userbase+this.domain.getSetname();
			        sql=getZpSubsetSql(cName,rgrid,userbase,userview,nFlag,PERSONKEYTYPE,fields.toString());
					break;
				}
				case 7:  // 基准岗位
				{
					sql=getStdPosSql(rgrid,userbase,userview,nFlag,STDPOSKEYTYPE,fields.toString());
					break;
				}
			}
	    	ArrayList subClassList=rowHeightNum(para_array,rgrid,disting_pt,sql);
	    	//liuy 2015-12-17 优化登记表不显示格线的情况下，数据显示垂直居中 begin
	    	if("1".equals(infokind)||"2".equals(infokind)||"4".equals(infokind)){	    		
    			if(("7".equals(rgrid.getAlign())||"6".equals(rgrid.getAlign())||"8".equals(rgrid.getAlign()))&&"false".equals(this.domain.getVl())&&"false".equals(this.domain.getHl()))//设置子集垂直居中
                {
                    html.append("<table border=\"0\" cellspacing=\"0\" valign=\"top\" width=\""+(int)Float.parseFloat(rgrid.getRwidth())+"\"   cellpadding=\"0\" class=\"\">");
                } else {
                    html.append("<table border=\"0\" cellspacing=\"0\" valign=\"top\" width=\""+(int)Float.parseFloat(rgrid.getRwidth())+"\" height=\"100%\"  cellpadding=\"0\" class=\"\">");
                }
	    	}else {
                html.append("<table border=\"0\" cellspacing=\"0\" valign=\"top\" width=\""+(int)Float.parseFloat(rgrid.getRwidth())+"\" height=\"100%\"  cellpadding=\"0\" class=\"\">");
            }
	    	//liuy 2015-12-17 end
	    	MadeFontsizeToCell mc=new MadeFontsizeToCell();                              //创建的字体适应cell大小的对象
	       // mc.setAuto("1");
	    	int rowHeight=0;
	    	if(this.ykcard_auto) {
	    		newline=newline==0?1:newline;
	    		rowHeight=heightn/newline;
	    		fontsize=fontsize+1;
	    	}
	        for(int r=0;r<subClassList.size();r++)
	    	{
	    		HashMap one_hash=(HashMap)subClassList.get(r);
	    		html.append("<tr>");
	    		int heights=0;	
		    	for(int i=0;i<this.fieldlist.size();i++)
		    	{
		    		
		    		String field=this.fieldlist.get(i).toString();
		    		String class_str="";    		    		
					HashMap hash=(HashMap)one_hash.get(field);
					if(this.customcolhead!=null&& "true".equalsIgnoreCase(this.customcolhead))
					{
						HashMap headwidth=this.customcolheadwidth;
						String width_str=(String)headwidth.get(field);
						if(width_str!=null&&width_str.length()>0)
						{
							widthn=(int)Float.parseFloat(width_str);
						}else {
                            widthn=((int)para_array[i]);
                        }
					}else
					{
						widthn=((int)para_array[i]); 
					}					 
					Integer height_I=(Integer)hash.get("height");
		    		String title=(String)hash.get("title");		
		    		title=title!=null&&title.trim().length()>0?title:"　";
		    		String strtype=(String)hash.get("type");
		    		Integer max_row_I=(Integer)one_hash.get("max_row");
		    		int max_row=max_row_I.intValue();
		    		if(r==subClassList.size()-1)
        			{
		    			if (this.domain.getMultimedia()==null|| "".equals(this.domain.getMultimedia())|| "false".equals(this.domain.getMultimedia())) {
			    			if(i!=this.fieldlist.size()-1) {
                                class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0","0",this.domain.getHl(),this.domain.getVl());
                            } else {
                                class_str= new MadeCardCellLine().GetCardCellLineShowcss("0","0","0","0",this.domain.getHl(),this.domain.getVl());
                            }
		    			}else if ("true".equals(this.domain.getMultimedia())) {
		    				class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0","0",this.domain.getHl(),this.domain.getVl());
						}
		    			if(this.auto_heigh) {
                            heights=heightn-6;
                        } else
    					{
    						height_I=(Integer)hash.get("height");
    						heights=height_I.intValue();
    					}
		    			if(subClassList.size()>1&&heights!=(heightn/(subClassList.size()-r)))//27558 changxy  20170515
                        {
                            heights=(heightn/(subClassList.size()-r));
                        }
        			}else
        			{
        				if (this.domain.getMultimedia()==null|| "".equals(this.domain.getMultimedia())|| "false".equals(this.domain.getMultimedia())) {
	        				if(i!=this.fieldlist.size()-1) {
                                class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0",this.lh,this.domain.getHl(),this.domain.getVl());
                            } else {
                                class_str= new MadeCardCellLine().GetCardCellLineShowcss("0","0","0",this.lh,this.domain.getHl(),this.domain.getVl());
                            }
        				}else if ("true".equals(this.domain.getMultimedia())) {
        					class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0",this.lh,this.domain.getHl(),this.domain.getVl());
        				}
    		    		int height=height_I.intValue();
    		    		if(this.auto_heigh) {
                            heights=(max_row*height)+6;
                        } else {
                            heights=height_I.intValue();        //不显示格线时 行高不均分
                        }
    		    		if(subClassList.size()>1&&heights!=(heightn/(subClassList.size()-r))&&!("0".equals(ls)&& "0".equals(lh)))//27558 changxy  20170515
                        {
                            heights=(heightn/(subClassList.size()-r));
                        }
        			}		    		
		    		String[] align=(String[])hash.get("align");		
		    		boolean onlytitle=false;
                    if(r==subClassList.size()-1)
		    		{
                    	if(subClassList.size()>1&&heights!=height_I.intValue())//27558 changxy  20170515
                        {
                            html.append("<td align=\""+align[0]+"\" style=\"display:table-cell;text-align:"+align[1]+";\" width=\""+widthn+"\"  class=\""+class_str+"\" "+(this.ykcard_auto?"nowrap":"")+" >");
                        } else if(r==subClassList.size()-1&&subClassList.size()>1){//最后一行记录居中29013
                    		html.append("<td align=\""+align[0]+"\" style=\"display:table-cell;text-align:"+align[1]+";\" width=\""+widthn+"\"  class=\""+class_str+"\" "+(this.ykcard_auto?"nowrap":"")+">");
                    	}else{//当子集内容为空时 标题行居顶显示
                    		 html.append("<td align=\""+align[0]+"\" style=\"display:table-cell;text-align:"+align[1]+";\" width=\""+widthn+"\"  class=\""+class_str+"\" "+(this.ykcard_auto?"nowrap":"")+">");
                    		 onlytitle=true;
                    	}
		    		}		    		   
		    		else{
		    			html.append("<td align=\""+align[0]+"\" style=\"display:table-cell;text-align:"+align[1]+";\" height=\""+((rowHeight!=0&&(rowHeight<heights))?(rowHeight-1):heights)+"\" width=\""+widthn+"\" class=\""+class_str+"\" "+(this.ykcard_auto?"nowrap":"")+">");
		    		}
		    		ArrayList v_list=new ArrayList();
		    		v_list.add(title);
		    		int fontsize2=this.ykcard_auto?this.fontsize:Integer.parseInt(rgrid.getFontsize());//mc.ReDrawLitterRect(widthn,(rowHeight!=0?rowHeight:heights), v_list, this.fontsize);
		    		if(onlytitle) {
                        html.append("<div style=\"margin-top:10px\">");
                    }
		    		if(colhead!=null&& "true".equals(colhead)&&r==0) {
                        html.append("<font  style=\"font-weight:" + fontweight + ";"+setFontMarginPx(align[0])+"font-family:"+rgrid.getFontName()+";font-size:" + (fontsize2+0.5) + "pt\"  color=\"#000000\">");
                    } else {
                        html.append("<font  style=\"font-weight:" + fontweight + ";"+setFontMarginPx(align[0])+"font-family:"+rgrid.getFontName()+"; font-size:" + (fontsize2+0.5) + "pt\"  color=\"#15428b\">");
                    }
		    		html.append(title.trim());	//子集单元格水平居左 内容前留白	    		
		    		html.append("</font>");		    		
		    		if(onlytitle) {
                        html.append("</div>");
                    }
		    		html.append("</td>");
		    		
		    		
					if(i==this.fieldlist.size()-1)
		    		{
						//判断是否已经勾选显示附件选项
		    			if (this.domain.getMultimedia()==null|| "".equals(this.domain.getMultimedia())|| "false".equals(this.domain.getMultimedia())) {
						}else if ("true".equals(this.domain.getMultimedia())) {
							boolean flag = false;
							int I9999 = 0; 
							//判断在集合里面是否有I9999这个键
							if(one_hash.containsKey("I9999")) {
								Integer I9999_I = (Integer)one_hash.get("I9999");
								I9999 = I9999_I.intValue(); 
							}
							if(strtype==null|| "".equals(strtype)||userbase==null|| "".equals(userbase)||setname==null|| "".equals(setname)){
								flag=false;
							}else {
								if(I9999 > 0) {
									MultiMediaBo mmBo= new MultiMediaBo(conn,userview,strtype,userbase,setname,nId,I9999);
									//查询是否有记录
									flag=mmBo.isHasMultimediaRecord();
								}
							}
							if (flag==true) {
								
								widthn=((int)para_array[i+1]); 					 
							 	Integer height_I1=height_I;
							 	//显示附件图片
							 	title="<img border='0' src='/images/muli_view.gif' title='查看附件'/>";
					    		String strtype1=strtype;
					    		Integer max_row_I1=max_row_I;
					    		int max_row1=max_row_I1.intValue();
					    		//判断是否是执行最后一行
					    		if(r==subClassList.size()-1)
			        			{
					    			if(i!=this.fieldlist.size()-1) {
                                        class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0","0",this.domain.getHl(),this.domain.getVl());
                                    } else {
                                        class_str= new MadeCardCellLine().GetCardCellLineShowcss("0","0","0","0",this.domain.getHl(),this.domain.getVl());
                                    }
			    					if(this.auto_heigh) {
                                        heights=heightn-6;
                                    } else
			    					{
			    						height_I1=height_I;
			    						heights=height_I1.intValue();
			    					}	
			        			}else
			        			{
			        				if(i!=this.fieldlist.size()) {
                                        class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0",this.lh,this.domain.getHl(),this.domain.getVl());
                                    } else {
                                        class_str= new MadeCardCellLine().GetCardCellLineShowcss("0","0","0",this.lh,this.domain.getHl(),this.domain.getVl());
                                    }
			        				
			    		    		int height=height_I1.intValue();
			    		    		if(this.auto_heigh) {
                                        heights=(max_row1*height)+6;
                                    } else {
                                        heights=height_I1.intValue();
                                    }
			        			}		    		
					    		align=(String[])hash.get("align");		
					    		if(r==subClassList.size()-1)
					    		{
					    			html.append("<td align=\""+align[0]+"\" valign=\"top\" width=\""+widthn+"\"  class=\""+class_str+"\" "+(this.ykcard_auto?"nowrap":"")+">");
					    		}		    		   
					    		else{
					    			html.append("<td align=\""+align[0]+"\" style=\"display:table-cell;text-align:"+align[1]+";\" height=\""+(rowHeight!=0&&(rowHeight<heights)?(rowHeight-1):heights)+"\" width=\""+widthn+"\" class=\""+class_str+"\" "+(this.ykcard_auto?"nowrap":"")+">");
					    		}
					    		v_list=new ArrayList();
					    		v_list.add(title);
					    		fontsize2=this.ykcard_auto?this.fontsize:Integer.parseInt(rgrid.getFontsize());//mc.ReDrawLitterRect(widthn, (rowHeight!=0?rowHeight:heights), v_list, this.fontsize);
					    		if(colhead!=null&& "true".equals(colhead)&&r==0){
						    		html.append("<font  style=\"font-weight:" + fontweight + ";"+setFontMarginPx(align[0])+"font-family:"+rgrid.getFontName()+"; font-size:" + (fontsize2) + "pt\"  color=\"#000000\">");
						    		html.append("附件");
					    		}else{
					    			html.append("<font  style=\"font-weight:" + fontweight + ";"+setFontMarginPx(align[0])+"font-family:"+rgrid.getFontName()+";font-size:" + (fontsize2) + "pt\"  color=\"#15428b\">");
					    			//window.showModalDialog隐藏地址栏location=no属性，仅在internet设置了才起作用
					    			html.append("<a href=\"javascript:\" onClick=\"window.showModalDialog('/general/inform/multimedia/multimedia_tree.do?b_query=link&setid="+setname+"&a0100="+nId+"&nbase="+userbase+"&i9999="+I9999+"&dbflag=A&canedit=false','','dialogLeft:400px;dialogTop:200px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;location=no;status:no')\">");
						    		html.append(title);
					    		}
					    		html.append("</a>");
					    		html.append("</font>");		    		
					    		html.append("</td>");
							}else {
								
								widthn=((int)para_array[i+1]); 					 
							 	Integer height_I1=height_I;
					    		title="　";		
					    		String strtype1=strtype;
					    		Integer max_row_I1=max_row_I;
					    		int max_row1=max_row_I1.intValue();

					    		if(r==subClassList.size()-1)
			        			{
					    			if(i!=this.fieldlist.size()-1) {
                                        class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0","0",this.domain.getHl(),this.domain.getVl());
                                    } else {
                                        class_str= new MadeCardCellLine().GetCardCellLineShowcss("0","0","0","0",this.domain.getHl(),this.domain.getVl());
                                    }
			    					if(this.auto_heigh) {
                                        heights=heightn-6;
                                    } else
			    					{
			    						height_I1=height_I;
			    						heights=height_I1.intValue();
			    					}	
			        			}else
			        			{
			        				if(i!=this.fieldlist.size()) {
                                        class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0",this.lh,this.domain.getHl(),this.domain.getVl());
                                    } else {
                                        class_str= new MadeCardCellLine().GetCardCellLineShowcss("0","0","0",this.lh,this.domain.getHl(),this.domain.getVl());
                                    }
			        				
			    		    		int height=height_I1.intValue();
			    		    		if(this.auto_heigh) {
                                        heights=(max_row1*height)+6;
                                    } else {
                                        heights=height_I1.intValue();
                                    }
			        			}		    		
					    		align=(String[])hash.get("align");		
					    		if(r==subClassList.size()-1)
					    		{
					    			html.append("<td align=\""+align[0]+"\" valign=\"top\" width=\""+widthn+"\"  class=\""+class_str+"\" "+(this.ykcard_auto?"nowrap":"")+">");
					    		}		    		   
					    		else{
					    			html.append("<td align=\""+align[0]+"\" style=\"display:table-cell;text-align:"+align[1]+";\" height=\""+(rowHeight!=0&&(rowHeight<heights)?(rowHeight-1):heights)+"\" width=\""+widthn+"\" class=\""+class_str+"\" "+(this.ykcard_auto?"nowrap":"")+">");
					    		}
					    		v_list=new ArrayList();
					    		v_list.add(title);
					    		fontsize2=this.fontsize;//mc.ReDrawLitterRect(widthn, (rowHeight!=0?rowHeight:heights), v_list, this.fontsize);
					    		if(colhead!=null&& "true".equals(colhead)&&r==0){
						    		html.append("<font  style=\"font-weight:" + fontweight + ";"+setFontMarginPx(align[0])+"font-family:"+rgrid.getFontName()+";font-size:" + (fontsize2) + "pt\"  color=\"#000000\">");
						    		html.append("附件");
					    		}else{
					    			html.append("<font  style=\"font-weight:" + fontweight + ";"+setFontMarginPx(align[0])+"font-family:"+rgrid.getFontName()+"; font-size:" + (fontsize2) + "pt\"  color=\"#15428b\">");
						    		html.append(title.trim());
					    		}
					    		html.append("</font>");		    		
					    		html.append("</td>");
							}
						}
		    		}
		    	}
		    	heightn=heightn-heights;
		    	html.append("</tr>");
	    	}	    	
	    	html.append("</table>");	    	
           return html.toString(); 
           
	 }
	 //子集内容 居左或居右 设置font边距
	 private String setFontMarginPx(String align) {
		 	if("left".equals(align)) {
                return ";margin-left:3px;";
            } else if("right".equals(align)) {
                return ";margin-right:3px;";
            } else {
                return "";
            }
	 }
	 
	 public String viewSubClassStr(String infokind,String userbase, Connection conn,UserView userview,RGridView rgrid,String disting_pt,byte nFlag)
	 {
		    String colhead=this.domain.getColhead();
	    	String setname=this.domain.getSetname();
	    	if(userpriv!=null&&"selfinfo".equalsIgnoreCase(userpriv))
	    	{
	    		if(!"1".equals(fieldpurv)&& "0".equals(userview.analyseTablePriv(setname,0)))
		    	{
	    			return "";
		    	}
	    	}else
	    	{
	    		if(this.fenlei_type!=null&&this.fenlei_type.length()>0&&!userview.isSuper_admin()&&(nFlag==0))
	    	     {
	    	    	 String priv_flag= userview.analyseSubTablePriv(this.fenlei_type,setname)+"";    	  
	    	    	  if(priv_flag==null||priv_flag.length()<=0|| "-1".equals(priv_flag)) {
                          return "";
                      }
	    	     }else if("0".equals(userview.analyseTablePriv(setname)))
		    	{
		    		return "";
		    	}
	    	} 
	    	StringBuffer html=new StringBuffer();         //单元格的高
	        int widthn;     	
	        int heightn;
	    	this.fontweight=rgrid.getFonteffect();
		    if(fontweight !=null && "2".equals(fontweight))         //字体是否时粗体
            {
                fontweight="bold";
            } else {
                fontweight="normal";
            }
		    if("800".equals(disting_pt))
	        {     
		       widthn=(int)Float.parseFloat(rgrid.getRwidth("1")) + 1;
		       heightn=(int)Float.parseFloat(rgrid.getRheight("1"))  +1;
		    }
	        else
	        {
		       widthn=(int)Float.parseFloat(rgrid.getRwidth()) + 1;
		       heightn=(int)Float.parseFloat(rgrid.getRheight()) +1;
		    }
		    this.fact_height=heightn;
		    this.fact_width=heightn;
	    	float para_array[]=viewParaSrray(widthn);
	    	String sql="";
	    	StringBuffer fields=new StringBuffer();
	        for(int i=0;i<this.fieldlist.size();i++)
	        {
	        	fields.append(this.fieldlist.get(i).toString()+",");
	        }
	        if(fields.length()>0) {
                fields.setLength(fields.length()-1);
            }
	        String cBase="";          //人员库
	    	switch(nFlag)
			{	
				//人员库
				case 0:
				{
					sql=getPersonSql(rgrid,userbase,userview,nFlag,PERSONKEYTYPE,fields.toString());				
					break;
				}//岗位库
				case 4:
				{
					if("1".equals(infokind))
					{
						nId=getOrgnId("4",nId,userbase);				
					}else
					{
						cBase="K";
					}
					sql=getPostSql(rgrid,userbase,userview,nFlag,POSTKEYTYPE,fields.toString());
					break;
				}
//				单位库
				case 2:
				{
					if("1".equals(infokind))
					{
						nId=getOrgnId("2",nId,userbase);		
					}else
					{
						cBase="B";
					}
					sql=getUnitSql(rgrid,userbase,userview,nFlag,UNITKEYTYPE,fields.toString());
					break;
				}
			}
	    	ArrayList subClassList=rowHeightNum(para_array,rgrid,disting_pt,sql);	    	
	        for(int r=0;r<subClassList.size();r++)
	    	{
	    		HashMap one_hash=(HashMap)subClassList.get(r);
	    		if(r>0) {
                    html.append("\r\n");
                }
	    		int heights=0;
		    	for(int i=0;i<this.fieldlist.size();i++)
		    	{
		    		String field=this.fieldlist.get(i).toString();
		    		String class_str="";    		    		
					HashMap hash=(HashMap)one_hash.get(field);
					widthn=((int)para_array[i]);  
		    		String title=(String)hash.get("title");		    		
		    		Integer max_row_I=(Integer)one_hash.get("max_row");
		    		int max_row=max_row_I.intValue();
		    		if(r==subClassList.size()-1)
        			{
    					if(i!=this.fieldlist.size()-1) {
                            class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0","0",this.domain.getHl(),this.domain.getVl());
                        } else {
                            class_str= new MadeCardCellLine().GetCardCellLineShowcss("0","0","0","0",this.domain.getHl(),this.domain.getVl());
                        }
    					heights=heightn-6;
        			}else
        			{
        				if(i!=this.fieldlist.size()-1) {
                            class_str= new MadeCardCellLine().GetCardCellLineShowcss("0",this.ls,"0",this.lh,this.domain.getHl(),this.domain.getVl());
                        } else {
                            class_str= new MadeCardCellLine().GetCardCellLineShowcss("0","0","0",this.lh,this.domain.getHl(),this.domain.getVl());
                        }
        				Integer height_I=(Integer)hash.get("height");
    		    		int height=height_I.intValue();
    		    		heights=(max_row*height)+6;	
        			}
		    		html.append(title);		    		
		    		if(i!=this.fieldlist.size()-1) {
                        html.append("\t");
                    }
		    	}
		    	heightn=heightn-heights;
		    	html.append("\r\n");
	    	} 	    	
           return html.toString(); 
	 }
	 
	 /***
	  * 获取子集内容 list（map（fielditemid，value），......）
	  * */
	 public ArrayList outWordListMap(String infokind,String userbase, Connection conn,UserView userview,RGridView rgrid,String disting_pt,byte nFlag) throws Exception{
		 	ArrayList list=new ArrayList();
		 	String plan_id= rgrid.getPlan_id();	
		 	MadeFontsizeToCell mc=new MadeFontsizeToCell(); 
		 	String sql="";
	    	StringBuffer fields=new StringBuffer();
		 	String colhead=this.domain.getColhead();
	    	String setname=this.domain.getSetname();  
	    	GetCardCellValue getCardCellValue=new GetCardCellValue();
			String isView=getCardCellValue.getISVIEWForCexpress(rgrid.getCexpress());
			if(!"1".equals(isView))
			{
				if(userpriv!=null&&"selfinfo".equalsIgnoreCase(userpriv))
		    	{
		    		if(!"1".equals(fieldpurv)&& "0".equals(userview.analyseTablePriv(setname,0)))
			    	{
		    			return new ArrayList();
			    	}
		    	}else
		    	{
		    		if(this.fenlei_type!=null&&this.fenlei_type.length()>0&&!userview.isSuper_admin()&&(nFlag==0))
		    	     {
		    	    	  String priv_flag= userview.analyseSubTablePriv(this.fenlei_type,setname)+"";    	  
		    	    	  if(priv_flag==null||priv_flag.length()<=0|| "-1".equals(priv_flag)) {
                              return new ArrayList();
                          }
		    	     }else if("0".equals(userview.analyseTablePriv(setname)))
			    	{
			    		return new ArrayList();
			    	}
		    	}
			}
			
			for(int i=0;i<this.fieldlist.size();i++)
	        {
	        	fields.append(this.fieldlist.get(i).toString()+",");
	        }
	        if(fields.length()>0) {
                fields.setLength(fields.length()-1);
            }
	        if(fields==null||fields.length()<=0) {
                return new ArrayList();
            }
	        String cBase="";          //人员库
	    	switch(nFlag)
			{	
				//人员库
				case 0:
				{
					sql=getPersonSql(rgrid,userbase,userview,nFlag,PERSONKEYTYPE,fields.toString());				
					break;
				}//岗位库
				case 4:
				{
					if("1".equals(infokind))
					{
						nId=getOrgnId("4",nId,userbase);				
					}else
					{
						cBase="K";
					}
					sql=getPostSql(rgrid,userbase,userview,nFlag,POSTKEYTYPE,fields.toString());
					break;
				}
//				单位库
				case 2:
				{
					if("1".equals(infokind))
					{
						nId=getOrgnId("2",nId,userbase);		
					}else
					{
						cBase="B";
					}
					sql=getUnitSql(rgrid,userbase,userview,nFlag,UNITKEYTYPE,fields.toString());
					break;
				}
				case 5://计划目标表
				{
					sql=getPlanSql(rgrid,userbase,userview,nFlag,PERSONKEYTYPE,this.fieldlist);										
					break;
				}
				case 6:
				{
					String cName=this.domain.getSetname();
			        String cStr1=this.userbase+this.domain.getSetname();
			        sql=getZpSubsetSql(cName,rgrid,userbase,userview,nFlag,PERSONKEYTYPE,fields.toString());
					break;
				}
				case 7:  // 基准岗位
				{
					sql=getStdPosSql(rgrid,userbase,userview,nFlag,STDPOSKEYTYPE,fields.toString());
					break;
				}
			}
	    	ContentDAO dao=new ContentDAO(this.conn);
	    	RowSet rs=null;
	    	RowSet rs1 = null;
    		String class_str="";
    		String fieldValue="";
    		ArrayList strLst=null;
        	ArrayList strLstNoPre=null;
        	GetCardCellValue cellValue=new GetCardCellValue();
        	/** Rcount 子集记录数
        	 * Mode 0,1,2,3,4,5,6,7,8]=[0最近第,1最近,2最初第,3最初,4条件, 5条件最近第,6条件最近,7条件最初第,8条件最初] 
        		rgrid.getRcount(); 最初 order by i9999(2, 3, 7, 8)  最近 order by i9999(0, 1, 5, 6)
        	 * **/
	    	int rowCount=100;
	    	if("4".equals(rgrid.getMode())){
	    		if(Integer.parseInt(this.datarowcount)!=0) {
                    rowCount=Integer.parseInt(this.datarowcount);
                }
	    		
	    	}else{
	    		rowCount=rgrid.getRcount();
	    	}
	    	try {
				if(sql.length()>0){
					rs=dao.search(sql);
					int row=0;
		    		while(rs.next())
		    		{
		    			int max_row=0;
		    		    if(row>rowCount) {
                            break;
                        }
		    			HashMap one_hash=new HashMap();
		    			for(int i=0;i<this.fieldlist.size();i++)
		    			{
		    				String field=this.fieldlist.get(i).toString();
		    				YkcardSubclassFieldBean bean=new YkcardSubclassFieldBean();
		    				bean=(YkcardSubclassFieldBean)this.cellhash.get(field);
		    				strLst=new ArrayList();   
		    				strLstNoPre=new ArrayList();    				
		    				switch(bean.getItemtype().toUpperCase().charAt(0))
		    				{
		    					case CELLFIELD_STRINGTYPE:         //字符类型
		    					{   
		    					  String p0400="";
		    					  if(field.indexOf("reasons_")!=-1){
		    						  String bodyid=field.substring(8);
		    						  field=field.replaceAll("-", "#");
		    						  p0400=rs.getString("p0400");
		    						  String tmpsql = "select P0400,e.reasons AS "+ field+",m.a0101 as "+field+"_a";
		    						  tmpsql+=" from per_mainbody";
		    						  tmpsql+=" m left join per_target_evaluation e on m.mainbody_id=e.mainbody_id where m.plan_id = "+plan_id+" and m.object_id = '" + this.nId+ "' and m.body_id = "+bodyid+" and p0400='"+p0400+"'";
		    						  String ss="select * from (select * from p04) p04 left join ("+tmpsql+") tt on p04.p0400=tt.p0400 ";
		    						  ss+=" "+sql.substring(sql.lastIndexOf("where"));
		    						  rs1 = dao.search(ss);
		    						  StringBuffer reasons= new StringBuffer();
		    						  String tmpname="";
		    						  while(rs1.next()){
		    							  if(rs1.getString(field+"_a")==null||rs1.getString(field)==null) {
                                              continue;
                                          }
		    							  if(this.needfilter){
		    								  if(reasons.length()>0){
		    									  reasons.insert(0, tmpname);
		    									  reasons.append(rs1.getString(field+"_a")+":"+(rs1.getString(field)==null?"":rs1.getString(field))+"<br>");
		    									  tmpname="";
		    								  }else{
		    									  tmpname=rs1.getString(field+"_a")+":";
		    									  reasons.append((rs1.getString(field)==null?"":rs1.getString(field))+"<br>");
		    								  }
		    							  }else{
		    								  if(reasons.length()>0){
		    									  reasons.insert(0, tmpname);
		    									  reasons.append(rs1.getString(field+"_a")+":"+(rs1.getString(field)==null?"":rs1.getString(field))+"\n");
		    									  tmpname="";
		    								  }else{
		    									  tmpname=rs1.getString(field+"_a")+":";
		    									  reasons.append((rs1.getString(field)==null?"":rs1.getString(field))+"\n");
		    								  }
		    							  }
		    						  }
		    						  fieldValue = reasons.toString(); 
		    					  }else{
		    						  fieldValue=rs.getString(field);
		    					  }
		    					  if(fieldValue!=null && fieldValue.length()>0)
		    					  {
		    						strLst.add(fieldValue);//明码于代码输出    						
		    					  }					     
		    						break;
		    					}
		    					case CELLFIELD_MEMOTYPE:          //备注类型 
		    					{
		    						String valueresult="";
		    						fieldValue=rs.getString(field);
		    						fieldValue=fieldValue==null?"":fieldValue;
		    						if("summarizes".equalsIgnoreCase(field)){
		    							if(fieldValue.length()>0&&fieldValue.toLowerCase().indexOf("<?xml ")!=-1){
			    							XmlSubdomain xmlSubdomain = new XmlSubdomain(fieldValue);
			    							fieldValue = xmlSubdomain.getRecString();
		    							}
		    						}
		    						if(needfilter){
			    						if(fieldValue!=null && fieldValue.length()>0)
			    						{
			    							for(int r=0;r<fieldValue.length();r++)
			    							{
			    							   if("\n".equals(fieldValue.substring(r,r+1))) {
                                                   valueresult+="<br>";
                                               } else {
                                                   valueresult+=fieldValue.substring(r,r+1);
                                               }
			    							}					  
			    						   strLst.add(valueresult);//明码于代码输出    						   
			    						}
		    						}else {
                                        strLst.add(fieldValue);
                                    }
		    						
		    						break;
		    					}
		    					case CELLFIELD_DATETYPE:               //日期控制格式
		    					{
		    						Date date=rs.getTimestamp(field);
		    						if(date!=null)
		    						{
		    							String strdata=DateUtils.format(date,"yyyy.MM.dd HH:mm:ss");
		    							strLst.add(strdata);
		    						}else
		    						{
		    							strLst.add("");
//		    							getCellDateListValue(bean, strLst, "",null); 内容为空不添加前缀符，由导出控件统一处理
		    						}						
		    						break;
		    					}
		    					case CELLFIELD_NUMBERTYPE:            //数值类型
		    					{
		    						float fv=rs.getFloat(field.replaceAll("-", "X")); 
		    						if(display_zero!=null&& "1".equals(display_zero)&&fv==0)
		    						{
		    						}else if(fv==0)
		    						{
		    							
		    						}else {
                                        getCellDecimalListValue(bean, strLst, fv,strLstNoPre);
                                    }
		    						break;				
		    					}
		    				}
		    				if("P04".equalsIgnoreCase(bean.getFieldsetid())&&"item_id".equalsIgnoreCase(field)){
		    					try{		
		    						for(int nIdx=0;nIdx<strLst.size();nIdx++)
		    						{
		    							if(strLst.get(nIdx) !=null)
		    							{
		    								String codevalue=strLst.get(nIdx).toString();
		    							  	if(codevalue.length()>0)
		    							  	{
		    							  		rs1 = dao.search("select itemdesc from per_template_item where item_id="+codevalue);
		    							  		if(rs1.next()){
		    							  			strLst.set(nIdx,rs1.getString("itemdesc"));
		    							  		}else{
		    							  			strLst.set(nIdx,"");
		    							  		}
		    							  	}
		    							  	else {
                                                strLst.set(nIdx,"");
                                            }
		    							}
		    							else
		    							{
		    								strLst.set(nIdx,"");
		    							}		
		    						}
		    						
		    					}catch (Exception e){
		    					}
		    					finally{	 
		    						PubFunc.closeDbObj(rs1);
		    					}
		    				}
		    	    		one_hash.put(field.toLowerCase(),strLst.size()>0?strLst.get(0).toString():"");    	    		
		    			}
		    			
		    			
		    			list.add(one_hash);
		    			row++;
		    		}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeDbObj(rs);
			}
			
		 return list;
	 }
	 
	 public String viewSubClassExcelStr(String infokind,String userbase, Connection conn,UserView userview,RGridView rgrid,String disting_pt,byte nFlag)
	 {
		    String colhead=this.domain.getColhead();
	    	String setname=this.domain.getSetname();  
	    	GetCardCellValue getCardCellValue=new GetCardCellValue();
			String isView=getCardCellValue.getISVIEWForCexpress(rgrid.getCexpress());
			if(!"1".equals(isView))
			{
				if(userpriv!=null&&"selfinfo".equalsIgnoreCase(userpriv))
		    	{
		    		if(!"1".equals(fieldpurv)&& "0".equals(userview.analyseTablePriv(setname,0)))
			    	{
		    			return "";
			    	}
		    	}else
		    	{
		    		if(this.fenlei_type!=null&&this.fenlei_type.length()>0&&!userview.isSuper_admin()&&(nFlag==0))
		    	     {
		    	    	  String priv_flag= userview.analyseSubTablePriv(this.fenlei_type,setname)+"";    	  
		    	    	  if(priv_flag==null||priv_flag.length()<=0|| "-1".equals(priv_flag)) {
                              return "";
                          }
		    	     }else if("0".equals(userview.analyseTablePriv(setname)))
			    	{
			    		return "";
			    	}
		    	}
			}	    	 
	    	StringBuffer html=new StringBuffer();         //单元格的高
	        int widthn;     	
	        int heightn;
	    	this.fontweight=rgrid.getFonteffect();
		    if(fontweight !=null && "2".equals(fontweight))         //字体是否时粗体
            {
                fontweight="bold";
            } else {
                fontweight="normal";
            }
		    if("800".equals(disting_pt))
	        {     
		       widthn=(int)Float.parseFloat(rgrid.getRwidth("1")) + 1;
		       heightn=(int)Float.parseFloat(rgrid.getRheight("1"))  +1;
		    }
	        else
	        {
		       widthn=(int)Float.parseFloat(rgrid.getRwidth()) + 1;
		       heightn=(int)Float.parseFloat(rgrid.getRheight()) +1;
		    }
		    this.fact_height=heightn;
		    this.fact_width=heightn;
		    print_File="false";
	    	float para_array[]=viewParaSrray(widthn);
	    	String sql="";
	    	StringBuffer fields=new StringBuffer();
	        for(int i=0;i<this.fieldlist.size();i++)
	        {
	        	fields.append(this.fieldlist.get(i).toString()+",");
	        }
	        if(fields.length()>0) {
                fields.setLength(fields.length()-1);
            }
	        if(fields==null||fields.length()<=0) {
                return "";
            }
	        String cBase="";          //人员库
	    	switch(nFlag)
			{	
				//人员库
				case 0:
				{
					sql=getPersonSql(rgrid,userbase,userview,nFlag,PERSONKEYTYPE,fields.toString());				
					break;
				}//岗位库
				case 4:
				{
					if("1".equals(infokind))
					{
						nId=getOrgnId("4",nId,userbase);				
					}else
					{
						cBase="K";
					}
					sql=getPostSql(rgrid,userbase,userview,nFlag,POSTKEYTYPE,fields.toString());
					break;
				}
//				单位库
				case 2:
				{
					if("1".equals(infokind))
					{
						nId=getOrgnId("2",nId,userbase);		
					}else
					{
						cBase="B";
					}
					sql=getUnitSql(rgrid,userbase,userview,nFlag,UNITKEYTYPE,fields.toString());
					break;
				}
				case 7:  // 基准岗位
				{
					sql=getStdPosSql(rgrid,userbase,userview,nFlag,STDPOSKEYTYPE,fields.toString());
					break;
				}
			}
	    	ArrayList subClassList=rowHeightNum(para_array,rgrid,disting_pt,sql);
	    	int strlen[]=new int[this.fieldlist.size()];
	    	//liuy 2015-12-21 优化登记表不显示格线的情况下，数据导出Pdf垂直居中 begin
			 boolean tempflag = false;//子集是否垂直居中显示
			 if(("1".equals(infokind)||"2".equals(infokind)||"4".equals(infokind))){							 
				 XmlSubdomain xmlSubdomain=new XmlSubdomain(rgrid.getSub_domain());
				 xmlSubdomain.getParaAttribute();
				 if(StringUtils.isNotEmpty(xmlSubdomain.getFields())){
					 if(("7".equals(rgrid.getAlign())||"6".equals(rgrid.getAlign())||"8".equals(rgrid.getAlign()))&&"false".equals(xmlSubdomain.getVl())&&"false".equals(xmlSubdomain.getHl())) {
                         tempflag = true;
                     }
				 }
			 }
	    	for(int r=0;r<subClassList.size();r++)
	    	{
	    		HashMap one_hash=(HashMap)subClassList.get(r);
		    	for(int i=0;i<this.fieldlist.size();i++)
		    	{
		    		String field=this.fieldlist.get(i).toString();		    		 		    		
					HashMap hash=(HashMap)one_hash.get(field);
					widthn=((int)para_array[i]);  
		    		String title=(String)hash.get("title");	
		    		title=title!=null&&title.length()>0?title:"";
		    		int olen=strlen[i];    		
		    		int nlen=title.length();
		    		//if(tempflag){		    	//23443 组织机构--单位管理--信息维护--登记表，点击输出excel，输出的excel格式有误，数据都挤一起了 changxy 20161019		
		    			int length = getWordCount(title);
		    			nlen=length;
		    		//}
		    		if(olen<nlen) {
                        strlen[i]=nlen;
                    }
		    	}
	    	} 
	    	String bn=" ";
	        for(int r=0;r<subClassList.size();r++)
	    	{
	        	/*if(tempflag)   //23443 组织机构--单位管理--信息维护--登记表，点击输出excel，输出的excel格式有误，数据都挤一起了 changxy 20161019	
	        		bn=" ";
	        	else {					
	        		if(colhead!=null&&colhead.equals("true")&&r==0)//显示字段
	        		{
	        			bn=" ";
	        		}else
	        			bn="  ";
				}*/
	    		HashMap one_hash=(HashMap)subClassList.get(r);
	    		if(r>0) {
                    html.append("\r\n");
                }
	    		int heights=0;
		    	for(int i=0;i<this.fieldlist.size();i++)
		    	{
		    		
		    		String field=this.fieldlist.get(i).toString();		    		 		    		
					HashMap hash=(HashMap)one_hash.get(field);
					widthn=((int)para_array[i]);  
		    		String title=(String)hash.get("title");	
		    		title=title!=null&&title.length()>0?title:"";
		    		html.append(title);	 
		    		int nb=strlen[i]-title.length();
		    		//if(tempflag){		// excell 导出放开 changxy 20161014 23318    			
		    			int length = getWordCount(title);
		    			nb=strlen[i]-length;
		    		//}
		    		if(nb>0)
		    		{
		    			for(int b=0;b<nb;b++)
		    			{
		    				html.append(bn);
		    			}
		    		}
		    		html.append(" ");
		    	}
		    	heightn=heightn-heights;
		    	if(tempflag) {
                    html.append("\r");
                } else {
                    html.append("\r\n");
                }
	    	} 	    	
           return html.toString(); 
	 }
	 public int getWordCount(String s){
		 int length = 0;
     	 for(int i = 0; i < s.length(); i++){
             int ascii = Character.codePointAt(s, i);
             if(ascii >= 0 && ascii <=255) {
                 length++;
             } else {
                 length += 2;
             }
         }
         return length;
    }
	public int getFact_height() {
		return fact_height;
	}
	public void setFact_height(int fact_height) {
		this.fact_height = fact_height;
	}
	public int getFact_width() {
		return fact_width;
	}
	public void setFact_width(int fact_width) {
		this.fact_width = fact_width;
	}
	public int getNFlag() {
		return nFlag;
	}
	public void setNFlag(int flag) {
		nFlag = flag;
	}
	public String getDisplay_zero() {
		return display_zero;
	}
	public void setDisplay_zero(String display_zero) {
		this.display_zero = display_zero;
	}
	public String getCustomcolhead() {
		customcolhead=this.domain.getCustomcolhead();
		if(customcolhead==null||customcolhead.length()<=0) {
            customcolhead="false";
        }
		return customcolhead;
	}
	public void setCustomcolhead(String customcolhead) {
		this.customcolhead = customcolhead;
	}
    
    /**
     * 取子集格上方子集格
     * @Title: getUpSubCell   
     * @Description:    
     * @param rgrid
     * @param conn
     */
    private RGridView getUpSubCell(RGridView rgrid,Connection conn)
    {
            StringBuffer sql=new StringBuffer();
            sql.append("select * from rgrid where tabid='"+rgrid.getTabid()+"' ");
            sql.append(" and pageid='"+rgrid.getPageId()+"' and subflag='1'");
            sql.append(" and rtop+rheight="+rgrid.getRtop()+" and rleft="+rgrid.getRleft()+"");
            sql.append(" and rwidth="+rgrid.getRwidth());
            sql.append(" order by rleft");
            RowSet rs=null;
            try
            {
                ContentDAO dao=new ContentDAO(conn);
                rs=dao.search(sql.toString());
                if(rs.next())
                {
                    RGridView g = new RGridView();
                    g.setTabid(rs.getString("tabid"));
                    g.setPageId(rs.getString("pageid"));
                    g.setRtop(rs.getString("rtop"));
                    g.setRleft(rs.getString("rleft"));
                    g.setRwidth(rs.getString("rwidth"));
                    g.setRheight(rs.getString("rheight"));
                    return g;
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }finally
            {
                if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
    }
    
    public void setFieldwhithForCustomcolhead(RGridView rgrid,Connection conn)
    {
        if(this.fieldlist==null||this.fieldlist.size()<=0) {
            return;
        }
        if(this.customcolhead!=null&& "true".equalsIgnoreCase(this.customcolhead))
        {
            RGridView subcell = rgrid;
            RGridView up = null;
            // 子集格上方还是子集格的，继续向上找
            while(true) {
              up = getUpSubCell(subcell, conn);
              if(up == null) {
                  break;
              } else {
                  subcell = up;
              }
            }
            
            ArrayList list=new ArrayList();
            StringBuffer sql=new StringBuffer();
            sql.append("select * from rgrid where tabid='"+subcell.getTabid()+"' ");
            sql.append(" and pageid='"+subcell.getPageId()+"'");
            sql.append(" and rtop+rheight="+subcell.getRtop()+" and rleft>="+subcell.getRleft()+"");
            sql.append(" and rleft+rwidth<="+(Float.parseFloat(subcell.getRleft())+Float.parseFloat(subcell.getRwidth())));
            sql.append(" order by rleft");          
            RowSet rs=null;
            try
            {
                ContentDAO dao=new ContentDAO(conn);
                rs=dao.search(sql.toString());
                while(rs.next())
                {
                    list.add(rs.getString("rwidth"));
                }
                if(list.size()>0&&list.size()<this.fieldlist.size())
                {
                    ArrayList cc_list=new ArrayList();
                    for(int i=0;i<list.size();i++)
                    {
                        cc_list.add(this.fieldlist.get(i));
                        customcolheadwidth.put(this.fieldlist.get(i), list.get(i));
                    }
                    this.fieldlist=cc_list;
                }else
                {
                    for(int i=0;i<list.size();i++)
                    {
                        customcolheadwidth.put(this.fieldlist.get(i), list.get(i));
                        if(this.fieldlist.size()==(i+1)) {
                            break;
                        }
                    }
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }finally
            {
                if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
             
            
        }
    }
	public String getFenlei_type() {
		return fenlei_type;
	}
	public void setFenlei_type(String fenlei_type) {
		this.fenlei_type = fenlei_type;
	}
    public String getFieldpurv() {
        return fieldpurv;
    }
    public void setFieldpurv(String fieldpurv) {
        this.fieldpurv = fieldpurv;
    }
}
