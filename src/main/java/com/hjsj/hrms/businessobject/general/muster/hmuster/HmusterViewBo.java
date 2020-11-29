package com.hjsj.hrms.businessobject.general.muster.hmuster;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.ykcard.MadeCardCellLine;
import com.hjsj.hrms.interfaces.general.HmusterXML;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class HmusterViewBo {
	private Connection conn = null;
	private BufferedImage gg = new BufferedImage(1, 1,
			BufferedImage.TYPE_INT_RGB);

	private String infor_Flag="";
	private Graphics g = gg.createGraphics(); // 获得画布
	private float lt =0; // 表格整体往左靠，需减的像素
	private int t_space =30; // 表格整体往下移，需加的像素
	private MadeCardCellLine madeCardCellLine = new MadeCardCellLine();
	private int column_num=0;
	private String privConditionStr=" ";
	private String modelFlag="";
	private float  precent=0.24f;
	private UserView userView=null;
	private HashMap  gridNoMap=null;
	private String isGroupPoint=null;
	private String groupPoint="";
	private String isGroupPoint2="0";
	private String groupPoint2="";
	private String a0100="";
	private int pageRows=0;
	private boolean isyxj=false;  
	private boolean isylj=false;
	private boolean isfzhj=false;
	private boolean iszj=false;
	private String[] fzhj=null;
	private String[] zj=null;
	private int totalPage=1;
	private String summary="";
	private String isGroupNoPage="0";  // 1:分组不分页 
	private String isGroupedSerials="0";//1:按分组显示序列
	private long countall=0;//总行数
	/** 花名册垂直方向偏移 */
	private int deltaTop = 10;
	private String dataarea = "0";//横线分栏 1：不分栏多行数据区
	private String itemHeArr = "";
	private String groupNcode = ""; //当前页面分组指标值
	/** 花名册表格中，除最后一行或数据区外，其他所有插入文本的单元格 */
	private HashMap textFormatMap = new HashMap();
	private double textDataHeight=0;
	private double divWidth=0;
	private double divHeight=0;
	private String printGrind="1";//是否打印格线，默认打印
	private String tabid;
	private String column="";//分栏标志
	private boolean isGroupTerm2=false;
	private boolean isGroupV2=false;//表中是否存在第二分组指标字段
	private String groupCount="0";//我的薪酬中，是否分组合计=1分组合计，=0不分组合计
	private String mergeGrid="";
	/** 所有底边格 */
	private ArrayList bottomColumns = null;
	private String no_manger_priv="";//不按管理范围来取数
	private String returnflag="";//=mobile时为移动应用
	private String yearMont="";//标题中的年月
    /** 工资发放/审批数据表 */
    private String salaryDataTable = "";
    /** 包括salaryDataTable表和dbname表条件, 含where关键字 */
    private StringBuffer salaryDataTableCond = new StringBuffer("");
    
    /* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 xiaoyun 2014-8-20 start */
    /** 是否显示能力素质考核链接参数 */    
    private String linktype;
    /* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 xiaoyun 2014-8-20 end */
	
	/**
	 * @see #getResourceCloumn
	 */
	private int resourceCloumn=-1;
	private String checksalary="";  // 工资分析: "analysis"
	
	private HashMap topDateTitleMap = new HashMap();//高级花名册日期型上标题
	
	public  boolean tableTypeFlag=false;//判断表格设置纵向分栏是卡片式还是列表式（true 列表式  false 横向分栏或者纵向分栏卡片式）
	
	private HashMap<String, HashMap<String,Object>> musterCellMap=null;//高级花名册参数配置
	
	public HmusterViewBo(Connection conn,String tabid)
	{
		this.conn=conn;
		this.tabid=tabid;
		musterCellMap=new HashMap<String, HashMap<String,Object>>();
		init(tabid);
	}
	public HmusterViewBo(Connection conn) {
		this.conn = conn;
	}

	public HmusterViewBo() {

	}
	//初始化加载参数
	private void init(String tabid) {
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(conn);
		try {
			rs=dao.search("select * from muster_cell where tabid=?", Arrays.asList(tabid));
			while(rs.next()) {
				if(musterCellMap.get(rs.getString("gridno"))==null) {
					HashMap<String,Object> map=new HashMap<String, Object>();
					map.put("nWordWrap", rs.getString("nWordWrap"));
					musterCellMap.put(rs.getString("gridno"), map);
				}else {
					musterCellMap.get(rs.getString("gridno")).put("nWordWrap", rs.getString("nWordWrap"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}
	
public boolean isHaveGroup2(String tableName)
{
	boolean flag=false;
	RowSet rs = null;
	try
	{
		ContentDAO dao = new ContentDAO(this.conn);
		rs = dao.search("select * from "+tableName+" where 1=2");
		ResultSetMetaData data=rs.getMetaData();
		String group2="groupv2";
		for(int i=1;i<=data.getColumnCount();i++)
		{
			String columnName=data.getColumnName(i).toLowerCase();
			if(group2.toLowerCase().equalsIgnoreCase(columnName))
			{
				flag=true;
				break;
			}
			else {
                continue;
            }
		}
	}
	catch(Exception e)
	{
		flag=false;
	}finally{
		try
		{
			if(rs!=null) {
                rs.close();
            }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	return flag;
}
	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public String round(String v, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
	}

	/**
	 * 转换成字体布局字符
	 */
	private String[] transAlign(int Align) {
		String[] temp = new String[2];
		if (Align == 0) {
			temp[0] = "top";
			temp[1] = "left";
		} else if (Align == 1) {
			temp[0] = "top";
			temp[1] = "center";
		} else if (Align == 2) {
			temp[0] = "top";
			temp[1] = "right";
		} else if (Align == 3) {
			temp[0] = "bottom";
			temp[1] = "left";
		} else if (Align == 4) {
			temp[0] = "bottom";
			temp[1] = "center";
		} else if (Align == 5) {
			temp[0] = "bottom";
			temp[1] = "right";
		} else if (Align == 6) {
			temp[0] = "middle";
			temp[1] = "left";
		} else if (Align == 7) {
			temp[0] = "middle";
			temp[1] = "center";
		} else if (Align == 8) {
			temp[0] = "middle";
			temp[1] = "right";
		}
		return temp;
	}

	
	/*
	 * 处理页面显示虚线
	 */
	public String getStyleName(String[] temp) {
		String style_name="";
			style_name = madeCardCellLine.GetCardCellLineShowcss(temp[14],
					temp[16], temp[15], temp[17]);		
		return style_name;
	}
	
	
	/*
	 * 处理页面显示虚线
	 */
	public String getStyleName(String[] temp,int flag,String printGrid) {
		String style_name="";
		if("1".equals(printGrid)) {
            style_name = madeCardCellLine.GetCardCellLineShowcss(temp[14],temp[16],
                    temp[15], temp[17]);
        } else{
			if(flag==1) {
                style_name = madeCardCellLine.GetCardCellLineShowcss("0",
                        "0","0","1");
            } else {
                style_name = madeCardCellLine.GetCardCellLineShowcss("0",
                        "0","0","0");
            }
		}
		
		return style_name;
	}

	/*
	 * 处理页面显示虚线
	 */
	public String getStyleName2(String[] temp,String printGrid,int begin) {
		String style_name="";
		if("1".equals(printGrid)) {
            style_name= madeCardCellLine.GetCardCellLineShowcss(temp[14],temp[16],
                    temp[15], temp[17]);
        } else
		{
			if(begin==1) {
                style_name= madeCardCellLine.GetCardCellLineShowcss("0",
                        "0", "1", "0");
            } else {
                style_name= madeCardCellLine.GetCardCellLineShowcss("0",
                    "0", "0", "0");
            }
		}
		return style_name;
	}

	/**
	 * 转换成字符样式
	 */
	private String getFontStyle(String fontStyle, int aFontSize) {//update by wangcq on 2014-11-21
		StringBuffer style = new StringBuffer("");
		int font = Integer.parseInt(fontStyle) - 1;
		boolean b,t,s,u;  //粗体、斜体、删除线、下划线
		b = (font & 0x00000001) == 0x00000001;
		t = (font & 0x00000002) == 0x00000002;
		s = (font & 0x00000004) == 0x00000004;
		u = (font & 0x00000008) == 0x00000008;
		if(font > 0){
			if(u && s) {
                style.append("text-decoration:underline line-through;");//既有下划线，又有删除线
            } else{
				if(u) {
                    style.append("text-decoration:underline;") ;
                }
				if(s) {
                    style.append("text-decoration:line-through;") ;
                }
			}
			if(t) {
                style.append("font-style:italic;") ;
            }
			if(b) {
                style.append("font-weight:bold;") ;
            }
		}
		else {
            style.append("font-weight:normal;");
        }
		style.append("font-size:" + aFontSize + "pt;");
		return style.toString();
	}
	/**
	 * 转换成字符样式
	 */
	private String getFontStyle(String fontStyle, int aFontSize,String[] temp,String str,int rowh) {
		float a_width = Float.parseFloat(temp[4].trim());

		if(str!=null&&str.trim().length()>0&&!"&nbsp;".equalsIgnoreCase(str)){
			int fonts = 0; // 每行的字数
			for (int i = 1; i <= a_width / aFontSize; i++) {
				if ((i * aFontSize + (i + 1) * 2) <= a_width) {
					fonts = i;
				} else {
					break;
				}
			}
//			int row = (str.getBytes().length/2)/fonts+1;
//			if(row+2>rowh/aFontSize)
//				aFontSize = (aFontSize/row)+2;
			
		}
		
		String style = "";
		if ("2".equals(fontStyle)) {
            style = "font-weight:bold;font-size:" + aFontSize + "pt";
        } else if (fontStyle.endsWith("3")) {
            style = "font-style:italic;font-size:" + aFontSize + "pt";
        } else if ("4".equals(fontStyle)) {
            style = "font-style:italic;font-weight:bold;font-size:" + aFontSize + "pt";
        } else {
            style = "font-weight:normal;font-size:" + aFontSize + "pt";
        }
		return style;
	}
	
	/**
	 * 生成绝对定位的table(每个table表示一个单元格)
	 * 
	 * @param border
	 *            边宽
	 * @param align
	 *            字体布局位置
	 * @param top、left、width、height
	 *            表格的绝对位置
	 * @param type
	 *            1:表格 2：标题
	 * @param context
	 *            内容
	 */
	public String executeAbsoluteTable(int type, int Align, String fontName,
			String fontSize, String fontStyle, String border, String top,
			String left, String width, String height, String context,
			String style_name,boolean wordWrap) {
		context=context!=null&&context.trim().length()>0?context:"&nbsp;";
		StringBuffer tempTable = new StringBuffer("");
		String[] temp = transAlign(Align);
		String aValign = temp[0];
		String aAlign = temp[1];
		tempTable.append(" <table   border=\"" + border
				+ "\" cellspacing=\"0\" align=\"center\" cellpadding=\"0\"");
		tempTable.append(" class=\"ListTable5\" ");
		tempTable.append(" style=\"position:absolute;top:");
		tempTable.append(Float.parseFloat(top)+deltaTop-this.getTextDataHeight());
		tempTable.append("px;left:");
		tempTable.append(left);
		tempTable.append("px;width:");
		tempTable.append(width);
		tempTable.append("px;height:");
		tempTable.append(height);
		tempTable.append("px\"> \n ");
		tempTable.append(" <tr valign=\"middle\" align=\"center\"> \n ");
		tempTable.append(" <td ");
		if (type == 1) {
			if("0".equals(border)&&!this.tableTypeFlag) {//打印空行 border为0 设置底部边框宽度为0
				tempTable.append("style=\"padding-top:0;padding-bottom:0;border-bottom:0\" class=\"" + style_name + "\" ");
			}else {
				tempTable.append("style=\"padding-top:0;padding-bottom:0;\" class=\"" + style_name + "\" ");
			}
		}
		tempTable.append(" valign=\"middle");
		//tempTable.append(aValign);
		if(type==1) {
            tempTable.append("\"align=\"center");
        } else {
            tempTable.append("\" align=\"left");
        }
		//tempTable.append("' align='");
		//tempTable.append(aAlign);
		tempTable.append("\"> \n ");
		int aFontSize = Integer.parseInt(fontSize);		//32158 取消字体自动减一 导致标题字体跟内容字体不一致
		String style = getFontStyle(fontStyle, aFontSize);
		tempTable.append("<div style=\"padding-top:1px;padding-bottom:1px;overflow:hidden;position:relative;text-align:"+aAlign+";width:"+(Float.parseFloat(width)-1.5)+"px;"+(wordWrap?"word-wrap:break-word;":"")+"max-height:"+(Float.parseFloat(height)-1)+"px;\" valign=\""+aValign+"\">");

		tempTable.append(" <font face=\"");
		tempTable.append(fontName);
		tempTable.append("\" style=\"");
		tempTable.append(style);
		tempTable.append("\" > \n");
		tempTable.append(context);
		tempTable.append("</font>");
		tempTable.append("</div></td></tr></table> \n ");

		return tempTable.toString();
	}
	/**
	 * 生成绝对定位的table(每个table表示一个单元格)
	 * 
	 * @param border
	 *            边宽
	 * @param align
	 *            字体布局位置
	 * @param top、left、width、height
	 *            表格的绝对位置
	 * @param type
	 *            1:表格 2：标题
	 * @param context
	 *            内容
	 */
	private String executeAbsoluteTable1(int type, int Align, String fontName,
			String fontSize, String fontStyle, String border, String top,
			String left, String width, String height, String context,
			String style_name, int z_index) {
		context=context!=null&&context.trim().length()>0?context:"&nbsp;";
		StringBuffer tempTable = new StringBuffer("");
		String[] temp = transAlign(Align);
		String aValign = temp[0];
		String aAlign = temp[1];
		tempTable.append(" <table   border=\"" + border
				+ "\" cellspacing=\"0\" align=\"center\" cellpadding=\"0\"");
		tempTable.append(" style=\"position:absolute;top:");
		tempTable.append(Float.parseFloat(top)+deltaTop+5);
		tempTable.append("px;left:");
		tempTable.append(left);
		tempTable.append("px;width:");
		tempTable.append(context.length()*30);//不知之前为何注销此行，导致标题换行 现在放开
//		tempTable.append(width);
		tempTable.append("px;height:");
		tempTable.append(height);
		tempTable.append("px;z-index: "+z_index+";\"> \n ");
		tempTable.append(" <tr valign=\"middle\" align=\"center\"> \n ");
		tempTable.append(" <td ");
		if (type == 1) {
			tempTable.append(" class=\"" + style_name + "\" ");
		}
		tempTable.append(" valign=\"middle");
		//tempTable.append(aValign);
		if(type==1) {
            tempTable.append("\" align=\"center");
        } else {
            tempTable.append("\" align=\"left");
        }
		//tempTable.append("' align='");
		//tempTable.append(aAlign);
		tempTable.append("\"> \n ");
		int aFontSize = Integer.parseInt(fontSize);		
		String style = getFontStyle(fontStyle, aFontSize);
		tempTable.append(" <font face=\"");
		tempTable.append(fontName);
		tempTable.append("\" style=\"");
		tempTable.append(style);
		tempTable.append("\" > \n ");
		tempTable.append(context);
		tempTable.append("</font>");
		tempTable.append("</td></tr></table> \n ");

		return tempTable.toString();
	}
	
	
	public int  getFitFontSize(int fontSize,float width,float height,String context)
	{
		width-=5;
		height-=5;
		float size=Integer.parseInt(String.valueOf(height/(fontSize+2)).substring(0,String.valueOf(height/(fontSize+2)).indexOf("."))) *Integer.parseInt(String.valueOf(width/(fontSize+2)).substring(0,String.valueOf(width/(fontSize+2)).indexOf(".")));
		while(fontSize>0&&(context.getBytes().length/2)>size)
		{
			fontSize=fontSize-1;
			size=Integer.parseInt(String.valueOf(height/(fontSize+2)).substring(0,String.valueOf(height/(fontSize+2)).indexOf("."))) *Integer.parseInt(String.valueOf(width/(fontSize+2)).substring(0,String.valueOf(width/(fontSize+2)).indexOf(".")));
		}
		return fontSize;
	}
	
	
	/**
	 * 生成绝对定位的table(每个table表示一个单元格)
	 * 
	 * @param border
	 *            边宽
	 * @param align
	 *            字体布局位置
	 * @param top、left、width、height
	 *            表格的绝对位置
	 * @param type
	 *            1:表格 2：标题
	 * @param context
	 *            内容
	 */
	public String executeAbsoluteTable2(int type, int Align, String fontName,
			String fontSize, String fontStyle, String border, String top,
			String left, String width, String height, String context,
			String style_name,boolean wordWrap) {
		context=context!=null&&context.trim().length()>0?context:"&nbsp;";
		/* 高级花名册分栏图片不显示问题 xiaoyun 2014-5-29 start */
		boolean hasLink = context.startsWith("<a href=\"javascript") || context.startsWith("<img src=");  // 姓名链接
		/* 高级花名册分栏图片不显示问题 xiaoyun 2014-5-29 end */
		if(!hasLink) {
            context=context.replaceAll(" ","&nbsp;");
        }
        context=context.replaceAll("\r\n", "<br>");
		StringBuffer tempTable = new StringBuffer("");
		String[] temp = transAlign(Align);
		String aValign = temp[0];
		String aAlign = temp[1];
		tempTable.append(" <table   border=\"" + border
				+ "\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" ");
		if (type == 1) {
            tempTable.append(" class=\"ListTable5\" ");
        }
		tempTable.append(" style=\"position:absolute;");
		// FIXME 下面两行代码解决内容多撑大单元格的问题，但是有个副作用：强制显示上边线。
		if("RecordRow_self_rtb".equals(style_name)||"RecordRow_self_tb".equals(style_name))//【22952】changxy 20160920 最后一列单元格显示线条为最左边的其余都不显示 RecordRow_self_rtb 如果取消设置top样式导致内容不居中 设置顶部线条颜色为白色
        {
            tempTable.append("BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-RIGHT: medium none; BORDER-TOP: 1px solid white;");
        } else if(this.tableTypeFlag) {
            tempTable.append("BORDER-BOTTOM: medium none; BORDER-LEFT: 1px solid black; BORDER-RIGHT: medium none; BORDER-TOP: 1px solid white;");
        } else if("RecordRow_self_t".equals(style_name)) {
            tempTable.append("BORDER-BOTTOM:  1px solid black; BORDER-LEFT:  1px solid black; BORDER-RIGHT:  1px solid black; BORDER-TOP: medium none;");
        } else if("RecordRow_self_b".equals(style_name)) {
            tempTable.append("BORDER-BOTTOM: medium none; BORDER-LEFT: 1px solid black; BORDER-RIGHT: 1px solid black; BORDER-TOP: 1px solid black;");
        } else {
            tempTable.append("BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-RIGHT: medium none; BORDER-TOP: 1px solid black;");
        }
		tempTable.append("TABLE-LAYOUT: fixed;");
        tempTable.append("top:");
		tempTable.append((Float.parseFloat(top)+deltaTop-this.getTextDataHeight()));
		tempTable.append("px;left:");
		tempTable.append(left);
		tempTable.append("px;width:");
		tempTable.append(width);
		tempTable.append("px;height:");
		tempTable.append(height+"px");			
	
//		if(context.getBytes().length/2>(Float.parseFloat(height)/(Float.parseFloat(fontSize)+5))*(Float.parseFloat(width)/(Float.parseFloat(fontSize)+4)))
//			tempTable.append(";table-layout:fixed");
//		
		
		
		tempTable.append("\"> \n ");
		tempTable.append(" <tr valign=\""+aValign+"\" align=\""+aAlign+"\"> \n "); //30315 高级花名册前后台显示的问题
		tempTable.append(" <td ");
		if (type == 1) {
			tempTable.append(" class=\"" + style_name + "\" ");
		}
        tempTable.append(" style=\"");
        tempTable.append("height:"+ (Float.parseFloat(height)-1) +"px; width:"+width+"px;padding-top:0;padding-bottom:0;\"");
		tempTable.append(" valign=\"middle");
		//tempTable.append(aValign);
		if(type==1) {
            tempTable.append("\" align=\""+aAlign); //30315 高级花名册前后台显示的问题
        } else {
            tempTable.append("\" align=\"left");
        }
		//tempTable.append("' align='");
		//tempTable.append(aAlign);
		tempTable.append("\"> \n ");
		int aFontSize = Integer.parseInt(fontSize) - 1;
		if(!hasLink)  // 含链接时不能用context计算字体
        {
            aFontSize=getFitFontSize(aFontSize,Float.parseFloat(width),Float.parseFloat(height),context);
        }
		
		String style = getFontStyle(fontStyle, aFontSize);
		tempTable.append("<div style=\"position:relative;text-align:"+aAlign+";width:"+(Float.parseFloat(width)-1.5)+"px;"+(wordWrap?"word-wrap:break-word;":"")+"max-height:"+(Float.parseFloat(height)-2.5)+"px;\" valign=\"middle\" align=\""+aAlign+"\">");
		tempTable.append(" <font face=\"");
		tempTable.append(fontName);
		tempTable.append("\" style=\"");
		tempTable.append(style+";");
		tempTable.append("\" > \n ");
		tempTable.append(context);
		tempTable.append("</font></div></td></tr></table> \n ");
		return tempTable.toString();
	}
	
	
	
	
	
	
	

	// 返回字体设置样式
	public int getFontStyle(String fontEffect) {
		int style = 0;
		if ("0".equals(fontEffect) || "1".equals(fontEffect)) {
            style = Font.PLAIN;
        } else if ("2".equals(fontEffect)) {
            style = Font.BOLD;
        } else if ("3".equals(fontEffect)) {
            style = Font.ITALIC;
        } else if ("4".equals(fontEffect)) {
            style = Font.BOLD + Font.ITALIC;
        }
		return style;
	}

	/**
	 * 根据列宽计算出字符串折行后的总高
	 * 
	 * @param fontName
	 *            字体名称
	 * @param fontEffect
	 *            已设好的字体样式
	 * @param fontSize
	 *            字体大小
	 * @param str
	 *            填入列中的字体长度
	 * @param columnWidth
	 *            列的固定宽度
	 * @return
	 */
	public int getFontwidth_height(int str, float columnWidth, String fontName,
			int fontEffect, int fontSize, String datatype) {
	    if(!"M".equals(datatype)) {
            fontSize+=2;
        }
		int height = 0;
		String fontStyle = ""; // 字体风格
		Font font = new Font(fontName, fontEffect, fontSize);
		g.setFont(font);
		int aheight = g.getFontMetrics().getHeight(); // 每一行字的高度
        int awidth = g.getFontMetrics().charWidth('汉');//fontSize;  // 汉字宽
		int count = 0;
		if (!"A".equals(datatype) && !"M".equals(datatype)) {
            count = str / 2;
        } else {
            count = str;
        }
		int rows = 0; // 字的行数
		int fonts = 0; // 每行的字数
		for (int i = 1; i <= columnWidth / awidth; i++) {
		    if("M".equals(datatype)&&((i * awidth) <= columnWidth)) {
		        fonts = i;
		    }
		    else if ((i * awidth + (i + 1) * 2) <= columnWidth) {
				fonts = i;
			} else {
				break;
			}
		}
		// 求得字行数
		if(fonts==0) {
            rows = 1;
        } else if (fonts!=0&&count % fonts != 0) {
			rows = count / fonts + 1;
		} else {
            rows = count / fonts;
        }

		if (rows == 1) {
            height = 25;
        } else {
			height = (rows) *aheight;
			if (height < 25) {
                height = 25;
            }
		}
		if("M".equals(datatype))  // 加上下边距
        {
            height+=aheight*2;
        }
		return height;
	}
	
	/**
	 * 取花名册表格中，除最后一行外，其他所有插入文本的单元格
	 * @param tabid
	 * @return
	 */
	public HashMap getTextFormat(String tabid)
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		RowSet rs2=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			StringBuffer buf2 = new StringBuffer();
			/*String sql = "select rtop from muster_cell where tabid="+tabid+" group by rtop order by rtop";
			RowSet rs=dao.search(sql);
			RowSet rs1=null;
			RowSet rs2=null;
			int count=0;
			while(rs.next())
			{
				if(count!=0)
				{
					buf.setLength(0);
					buf2.setLength(0);
		    		float f = rs.getFloat("rtop");
		    		buf.append("select count(*) from muster_cell where tabid="+tabid+" and rtop<"+f);
		    		buf2.append("select count(*) from muster_cell where tabid="+tabid+" and rtop<"+f+" and UPPER(flag)='H'");
			    	rs1=dao.search(buf.toString());
			    	rs2=dao.search(buf2.toString());
			    	int i1=0;
			    	int i2=0;
			    	while(rs1.next())
			    	{
			    		i1=rs1.getInt(1);
			    	}
			    	while(rs2.next())
		    		{
		    			i2=rs2.getInt(1);
		    		}
			    	buf.setLength(0);
			    	buf2.setLength(0);
			    	buf.append("select count(*) from muster_cell where tabid="+tabid+" and rtop>="+f);
		    		buf2.append("select count(*) from muster_cell where tabid="+tabid+" and rtop>="+f+" and UPPER(flag)!='H'");
		    		int i3=0;
		    		int i4=0;
		    		rs1=dao.search(buf.toString());
		    		rs2=dao.search(buf2.toString());
		    		while(rs1.next())
		    		{
		    			i3=rs1.getInt(1);
		    		}
		    		while(rs2.next())
		    		{
		    			i4=rs2.getInt(1);
		    		}
		    		if(i1!=0&&i1==i2&&i3!=0&&i3==i4)
		    		{
		    			buf2.setLength(0);
		    			buf2.append("select gridno from muster_cell where tabid="+tabid+" and rtop>="+f);
		    			rs1=dao.search(buf2.toString());
		    			while(rs1.next())
		    			{
		    				map.put(rs1.getString("gridno"), rs1.getString("gridno"));
		    			}
		    			break;
		    		}
				}
				count++;
			}*/
			buf.append(" select count(*) from muster_cell where rtop<(select max(rtop) from muster_cell where tabid="+tabid);
			buf.append(" and rleft in (select min(rleft) from muster_cell where tabid="+tabid+")) and tabid="+tabid);
			int a=0;
			int b=0;
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				a=rs.getInt(1);
			}
			buf2.append(" select count(*) from muster_cell where rtop<(select max(rtop) from muster_cell where tabid="+tabid);
			buf2.append(" and rleft in (select min(rleft) from muster_cell where tabid="+tabid+")) and tabid="+tabid);
			buf2.append(" and (UPPER(flag)='H' or flag is null  or flag='')");
			rs2 = dao.search(buf2.toString());
			while(rs2.next())
			{
				b=rs2.getInt(1);
			}
			if(a!=0&&a==b)
			{
				buf.setLength(0);
				buf.append(" select gridno from muster_cell where rtop<(select max(rtop) from muster_cell where tabid="+tabid);
				buf.append(" and rleft in (select min(rleft) from muster_cell where tabid="+tabid+")) and tabid="+tabid);
				buf.append(" and (UPPER(flag)='H' or flag is null or flag='')");
				rs=dao.search(buf.toString());
				StringBuffer buf_sql = new StringBuffer("");
				while(rs.next())
				{
					//map.put(rs.getString("gridno"), rs.getString("gridno"));
					buf_sql.append(","+rs.getString("gridno"));
				}
				buf.setLength(0);
				buf.append(" select gridno from muster_cell where tabid="+tabid+" and gridno not in ("+buf_sql.toString().substring(1)+")");
				rs=dao.search(buf.toString());
				while(rs.next())
				{
					map.put(rs.getString("gridno"), rs.getString("gridno"));
				}
			}
			else
			{
				String sql = "select rtop from muster_cell where tabid="+tabid+" group by rtop order by rtop";
				rs=null;
				rs2=null;
				rs = dao.search(sql);
				int i=0;
				while(rs.next())
				{
					if(i==0)
					{
						i++;
						continue;
					}
					i++;
					float rtop=rs.getFloat("rtop");
					rs2=dao.search("select count(*) from muster_cell where tabid="+tabid+" and rtop<"+rtop+"  and (UPPER(flag)='H' or flag is null  or flag='')");//比较的这行没算在上面
					int top=0;
					int bottom=0;
					int notH=0;
					while(rs2.next())
					{
						top=rs2.getInt(1);
					}
					rs2=dao.search("select count(*) from muster_cell where tabid="+tabid+" and rtop>="+rtop+"");
					while(rs2.next())
					{
						bottom=rs2.getInt(1);
					}
					rs2=dao.search("select count(*) from muster_cell where tabid="+tabid+" and rtop<"+rtop);
					while(rs2.next())
					{
						notH=rs2.getInt(1);
					}
					if(top!=0&&top==bottom&&notH==top)
					{
						rs2=dao.search("select gridno from muster_cell where tabid="+tabid+" and rtop>="+rtop);
						while(rs2.next())
						{
				    		map.put(rs2.getString("gridno"), rs2.getString("gridno"));
						}
						break;
					}
				}
			}
			if(rs!=null) {
                rs.close();
            }
			if(rs2!=null) {
                rs2.close();
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
				if(rs2!=null) {
                    rs2.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	public double getTextData(String tabid)
	{
		double f=0;
		RowSet rs = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			buf.append(" select max(rheight) from muster_cell where rtop=(select max(rtop) from muster_cell where tabid="+tabid);
			buf.append(" and rleft in (select min(rleft) from muster_cell where tabid="+tabid+")) and tabid="+tabid);
		    rs = dao.search(buf.toString());
		    while(rs.next())
		    {
		    	f=rs.getDouble(1);
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return f;
	}


	/**
	 * 根据tabid,动态生成高级花名册表头,及表的最左端、最上端、表头的总高
	 * 
	 * @return ArrayList
	 */
	public ArrayList getHmusterTableHead(String tabid, ArrayList tableHeaderList,float r_bottomn,float pix,String printGrid)
			throws GeneralException {
		
		/**
		 * 需要考虑纵分 列表式 设置两列标题
		 * 标题顺序bodylist顺序已经排列好，按照距离左边和顶部 由小到大排序
		 * */
		//changxy 20170227
		ArrayList copylist=new ArrayList();
		if(this.tableTypeFlag){
		
		float tab_width=0;
		for (int i = 0; i < tableHeaderList.size(); i++) {
			String[] arr=((String[])tableHeaderList.get(i)).clone();//克隆数组内的参数  
			tab_width+=Float.parseFloat(arr[4]);
			copylist.add(arr);
		}
		int colNum=793/(int)tab_width==0?1:793/(int)tab_width;
		
		ArrayList secList=new ArrayList();
		for (int i = 0; i < colNum-1; i++) {
			if(i==0) {
                secList=updateList(tableHeaderList,pix);
            } else {
                secList=updateList(secList,pix);
            }
			tableHeaderList.addAll(secList);
		}
	}
		ArrayList list = new ArrayList();
		StringBuffer tabHeader = new StringBuffer("");
		float[] lth = new float[3];
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String sql2 = "select min(Rleft) a,min(RTop)+"+this.t_space+" b,max(RHeight) c from muster_cell where tabid="
				+ tabid;
		double divh=0.0;
		ArrayList columnList = new ArrayList();
		int row = 0; // 表头的行数
		try {
			rowSet = dao.search(sql2);
			float minLeft = 0;
			float minTop = 0;
			float maxHeight = 0;
			if (rowSet.next()) {
				minLeft = rowSet.getFloat("a");
				lth[0] = minLeft - lt;
				minTop = rowSet.getFloat("b");
				lth[1] = minTop;
				maxHeight = rowSet.getFloat("c");
				lth[2] = maxHeight;
			}

			float tRleft = 0;
			float tRwidth = 0;
			for (Iterator tt = tableHeaderList.iterator(); tt.hasNext();) {
				String[] temp = (String[]) tt.next();
				String gridno=temp[0];
				
				if(this.getTextFormatMap()!=null&&this.getTextFormatMap().get(temp[0])!=null&& "1".equals(this.column)&& "1".equals(this.dataarea)) {
                    continue;
                }
				String l = temp[2];
				String t = temp[3];
				String w = temp[4];
				/*if(gridno.equals(((String[])(tableHeaderList.get(tableHeaderList.size()-1)))[0])) {
					//标题最后一格宽度+1
					w=(Float.parseFloat(w)+1)+"";
				}*/
				w=(Float.parseFloat(w)+1)+"";
				String h = temp[5];
				/*if(((String[])tableHeaderList.get(tableHeaderList.size()-1))[0].equals(temp[0])) {
					w=(Float.parseFloat(temp[4])+3)+"";
				}*/
				
				
				if(temp.length>20&&temp[20]!=null&& "1".equals(temp[20])) {
                    continue;
                }
				
				int flag=0;
				if(Float.parseFloat(temp[3]) + Float.parseFloat(temp[5])==r_bottomn) {
                    flag=1;
                }
				
				String context = temp[1];
				if (context == null) {
                    context = "&nbsp;";
                }
				String fontSize = temp[12];
				String fontName = temp[10];
				String fontStyle = temp[11];
				int Align = Integer.parseInt(temp[13]);
				if (context != null && context.indexOf("`") != -1) {
                    context = context.replaceAll("`", "<br>");
                }

				if("1".equals(temp[19])) {
                    context="&nbsp;";
                }
				if (Float.parseFloat(l) != lth[0]&& "0".equals(dataarea)) {
					l = String.valueOf(Float.parseFloat(l) - 1);
					w = String.valueOf(Float.parseFloat(w) + 1);
				}

				if (Float.parseFloat(t) != minTop) {
					t = String.valueOf(Float.parseFloat(t) - 1);
					h = String.valueOf(Float.parseFloat(h) + 1);
				}
                divh+=Double.parseDouble(w);
               
                boolean wordWarp=false;
    	    	if(musterCellMap!=null&&musterCellMap.get(gridno)!=null) {
    	    		if(musterCellMap.get(gridno).get("nWordWrap")!=null&&"1".equals(musterCellMap.get(gridno).get("nWordWrap"))) {
    	    			wordWarp=true;
    	    		}
    	    	}
                
				// 处理虚线 L,T,R,B,
				String style_name = getStyleName(temp,flag,printGrid);
				tabHeader.append(executeAbsoluteTable(1, Align, fontName,
						fontSize, fontStyle, "1", t, l, w, h, context,
						style_name,wordWarp));
			}
            this.setDivWidth(divh+this.t_space);
			list.add(tabHeader.toString());
			list.add(lth);
			if(copylist.size()>0){
				tableHeaderList.clear();//不移除第二列表  
				tableHeaderList.addAll(copylist);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	
	
	private String getTableBodyHeightSql(float alt,String tabid)
	{
		RecordVo vo=new RecordVo("muster_title");
		if(!vo.hasAttribute("extendattr"))
		{
			try
			{
				DbWizard dbWizard=new DbWizard(this.conn);
				Table table=new Table("muster_title");
				
				Field obj=new Field("extendattr","extendattr");
				obj.setDatatype(DataType.CLOB);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setAlign("left");	
				
				table.addField(obj);
				dbWizard.addColumns(table);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel("muster_title");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		String sql="";
		if(alt>0) {
            sql="select TMargin,BMargin,RMargin,LMargin,Paper,PaperW,PaperH,GridNo,Hz,RLeft-"
                + alt
                + " RLeft,rWidth,RTop+"+this.t_space+",rHeight,muster_title.FontSize,muster_title.FontName,muster_title.FontEffect,muster_title.Flag,muster_name.paperOri,muster_title.extendattr  from muster_title ,muster_name where muster_name.tabid=muster_title.tabid and muster_name.tabid="
                + tabid;
        } else {
            sql="select TMargin,BMargin,RMargin,LMargin,Paper,PaperW,PaperH,GridNo,Hz,RLeft+"
                + -alt
                + " RLeft,rWidth,RTop+"+this.t_space+",rHeight,muster_title.FontSize,muster_title.FontName,muster_title.FontEffect,muster_title.Flag,muster_name.paperOri,muster_title.extendattr   from muster_title ,muster_name where muster_name.tabid=muster_title.tabid and muster_name.tabid="
                + tabid;
        }
		return sql;
	}
	
	/**
	 * 获得表体的高度(单位:像素)
	 * 
	 * @param tabid
	 *            花名册id
	 * @param tableHeaderTop
	 *            表头最顶端的位置
	 * @param tableHeaderHeight
	 *            表头的高
	 * @author dengc
	 * @return ArrayList
	 */

	public ArrayList getTableBodyHeight(String tabid, float tableHeaderTop,
			float tableHeaderHeight, float alt, String column)
			throws GeneralException {
		/**
		 * select TMargin,
       BMargin,
       RMargin,
       LMargin,
       Paper,
       PaperW,
       PaperH,
       GridNo,
       Hz,
       RLeft - 117.0 RLeft,
       rWidth,
       RTop + 42,
       rHeight,
       muster_title.FontSize,
       muster_title.FontName,
       muster_title.FontEffect,
       muster_title.Flag,
       muster_name.paperOri,
       muster_title.extendattr
  	from muster_title, muster_name
 	where muster_name.tabid = muster_title.tabid
   	and muster_name.tabid = 1
		 * */
		String sql =getTableBodyHeightSql(alt,tabid);		
		ArrayList list = new ArrayList(); // 所有的标题信息列表
		double pageTotalPix = 0; // 页面总象素
		double tableBodyPix = 0; // 表体象素
		ArrayList titleList = new ArrayList();
		int titleTop = 0; // 标题最上端的位置
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		RowSet rowSet2 = null;
		try {
			rowSet = dao.search(sql);
			while (rowSet.next()) {//temp[9] rleft
				String[] temp = new String[19];
				
				
				temp[0] = rowSet.getString("TMargin")!=null?rowSet.getString("TMargin"):"";
				temp[1] = rowSet.getString("BMargin")!=null?rowSet.getString("BMargin"):"";
				temp[2] = rowSet.getString("RMargin")!=null?rowSet.getString("RMargin"):"";
				temp[3] = rowSet.getString("LMargin")!=null?rowSet.getString("LMargin"):"";
				int ftop = 0;
				for (int i = 4; i < 13; i++) {
					String setvalues= rowSet.getString(i + 1)!=null?rowSet.getString(i + 1):"";
					if(i==11){
						int ntop = (int)Float.parseFloat(setvalues);
						if(ntop>10000){
							ftop = ntop;
							setvalues=(ntop/1000-20)+"";
						}
					}
					if(i==9){//rleft 位置 sql 中取到库中存储的rleft -117.0 如果（Rleft-117）小于0 导致前台显示位置为页面开始的左侧 【24711】
						int Rleft=(int)Float.parseFloat(setvalues);//Integer.parseInt(setvalues);
						Rleft=Rleft<0?-Rleft:Rleft;
						setvalues=Rleft+"";
						
					}
					temp[i] = setvalues;
				}
				temp[13] = rowSet.getString("FontSize")!=null?rowSet.getString("FontSize"):"";
				temp[14] = rowSet.getString("FontName")!=null?rowSet.getString("FontName"):"";
				temp[15] = rowSet.getString("FontEffect")!=null?rowSet.getString("FontEffect"):"";
				temp[16] = rowSet.getString("Flag")!=null?rowSet.getString("Flag"):"";
				temp[17] = rowSet.getString("paperOri")!=null?rowSet.getString("paperOri"):"";
				temp[18] = rowSet.getString("extendattr")!=null?rowSet.getString("extendattr"):"";
				if(ftop>10000) {
                    temp[8] ="";
                }
				titleList.add(temp);
				if (titleTop == 0) {
                    titleTop = Integer.parseInt(temp[11]);
                } else {
					if (Integer.parseInt(temp[11]) > titleTop) {
                        titleTop = Integer.parseInt(temp[11]);
                    }
				}
			}
			
			if (titleList.size() > 0) {
				String[] temp = (String[]) titleList.get(0);
				if ("0".equals(column)) {
					if ("2".equals(temp[17]))  //横向
					{
						tableBodyPix = Double.parseDouble(temp[5])
						/ precent
						- (Double.parseDouble(temp[0]) + Double
								.parseDouble(temp[1]))
						/ precent
						- (tableHeaderTop + tableHeaderHeight)+this.t_space;
					}
					else                       //纵向
					{
						tableBodyPix = Double.parseDouble(temp[6])
						/ precent
						- (Double.parseDouble(temp[0]) + Double
								.parseDouble(temp[1]))
						/ precent
						- (tableHeaderTop + tableHeaderHeight)+this.t_space;
					}
				}
				else if ("1".equals(column)) {
					if (Integer.parseInt(temp[17]) == 2) {
                        tableBodyPix = Double.parseDouble(temp[5])
                                / precent-  Double.parseDouble(temp[1]) / precent-tableHeaderTop;
                    } else {
                        tableBodyPix = Double.parseDouble(temp[6])
                                / precent- Double.parseDouble(temp[1])/ precent-tableHeaderTop;
                    }
					
				} else if ("2".equals(column)) {
					if (Integer.parseInt(temp[17]) == 2) {
                        tableBodyPix = Double.parseDouble(temp[6])
                                / precent
                                - (Double.parseDouble(temp[2]) + Double
                                        .parseDouble(temp[3])) /precent;
                    } else {
                        tableBodyPix = Double.parseDouble(temp[5])
                                / precent
                                - (Double.parseDouble(temp[2]) + Double
                                        .parseDouble(temp[3])) / precent;
                    }
				}
			}
			else
			{
				String sql2 = "select TMargin,BMargin,RMargin,LMargin,Paper,PaperW,PaperH,paperOri  from muster_name  where tabid="
					+ tabid;
				rowSet2 = dao.search(sql2);
				if (rowSet2.next()) {
					
					if ("2".equals(column)) {
						if (rowSet2.getInt("paperOri") == 2) {
                            tableBodyPix = rowSet2.getFloat("PaperW")
                                    / precent
                                    - (rowSet2.getFloat("RMargin") + rowSet2
                                            .getFloat("LMargin")) / precent;
                        } else {
                            tableBodyPix = rowSet2.getFloat("PaperH")
                                    / precent
                                    - (rowSet2.getFloat("RMargin") + rowSet2
                                            .getFloat("LMargin")) / precent;
                        }
					} else {
						if (rowSet2.getInt("paperOri") == 2) // 横向
						{
							tableBodyPix = rowSet2.getDouble("PaperW")
									/ precent
									- (rowSet2.getFloat("RMargin") + rowSet2
											.getFloat("LMargin")) / precent- (tableHeaderTop + tableHeaderHeight);
						} else {
							tableBodyPix = rowSet2.getDouble("PaperH")
									/ precent
									- (rowSet2.getFloat("RMargin") + rowSet2
											.getFloat("LMargin")) / precent- (tableHeaderTop + tableHeaderHeight);
						}
					}
				}
			}
			list.add(titleList);
			list.add(round(String.valueOf(tableBodyPix), 0));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
				if(rowSet2!=null) {
                    rowSet2.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 得到查询的所有数据中最高列的高度
	 */
	public int getMaxColumnHeight(String sql, ArrayList list)
			throws GeneralException {
		int height = 0;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			rowSet = dao.search(sql);
			if (rowSet.next()) {
				for (Iterator t = list.iterator(); t.hasNext();) {
					String[] temp = (String[]) t.next();
					float columnWidth = Float.parseFloat(temp[4]) - 3;
					String dateType = temp[9];
					if("M".equalsIgnoreCase(dateType)) {//备注型指标不参与计算高度，内容覆盖 中船个性化
						continue;
					}
					int aLength = getFontwidth_height(rowSet.getInt("C"
							+ temp[0]), columnWidth, temp[10],
							getFontStyle(temp[11]), Integer.parseInt(temp[12]),
							dateType);
					if (aLength > height) {
                        height = aLength;
                    }
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return height;
	}

	/**
	 * 求出每页的行数
	 * 
	 * @param height
	 *            每行的高度
	 * @param isAutoCount
	 *            0:为自动计算 1:用户指定
	 * @param pageRows
	 *            n:为用户指定的每页行数
	 * @param nOperation
	 *            汇总标示 (0:无页小计和页累计 1:页小计 2:页累计 3:页小计和页累计都有 )
	 */
	public int getPageRows(String tabid, int height, String isAutoCount,
			String pageRows, float[] lth, int nOperation, int bodyHeight) {
		int rows = 0;
		pageRows=pageRows!=null&&pageRows.trim().length()>0?pageRows:"20";
		if (isAutoCount==null|| "0".equals(isAutoCount)) {
			if(height==0) {
                height=20;
            }
			rows = bodyHeight / height;
			if (nOperation == 1 || nOperation == 2){
				rows--;
			}
			if (nOperation == 3)
			{
				rows--;
				rows--;
			}
			if(this.isfzhj)
			{
				rows=rows-3;
			}

		} else {
			int aPageRows = Integer.parseInt(pageRows);
			if (nOperation == 1 || nOperation == 2) {
                aPageRows = aPageRows + 1;
            }
			if (nOperation == 3) {
                aPageRows = aPageRows + 2;
            }
			
			if(this.isfzhj) {
                aPageRows++;
            }

			/*if (aPageRows * height > bodyHeight) // 如果用户自定义得行数乘行高大于表体允许的范围，仍按自动计算
			{
				if (nOperation == 1 || nOperation == 2)
					bodyHeight = bodyHeight - height;
				if (nOperation == 3)
					bodyHeight = bodyHeight - height * 2;
				rows = bodyHeight / height;

			} else*/
			if(bodyHeight/aPageRows<15)
			{
				pageRows=bodyHeight/20+"";
			}
			rows = Integer.parseInt(pageRows);
		}
		if(rows<1) {
            rows=1;
        }
		return rows;
	}

	
	public static void main(String[] args)
	{
		String tableName="dds";
		String sql="select * from "+tableName;
		
		String temp=sql.substring(0,sql.indexOf(tableName)+tableName.length());
		temp+=",dbname";
		sql=temp+sql.substring(sql.indexOf(tableName)+tableName.length());
		
		if(sql.indexOf("where")!=-1)
		{
			temp=sql.substring(0,sql.indexOf("where")+5);
			temp+=" "+tableName+".nbase=dbname.pre ";
			
			sql=temp+" and "+sql.substring(sql.indexOf("where")+5);
		}
		else {
            sql+=" where "+tableName+".nbase=dbname.pre ";
        }
	}
	
	/**
	 * 根据薪资花名册排序要求 修改sql
	 * @param sql
	 * @return
	 */
	public String getInserSql(String sql,String tableName)
	{
		String temp=sql.substring(0,sql.indexOf(tableName)+tableName.length());
		temp+=",dbname";
		sql=temp+sql.substring(sql.indexOf(tableName)+tableName.length());
		String order="";
		
		if(sql.indexOf("order by")!=-1)
		{
			order=sql.substring(sql.indexOf("order by"));
			sql=sql.substring(0,sql.indexOf("order by"));
		}
		
		if(sql.indexOf("where")!=-1)
		{
			temp=sql.substring(0,sql.indexOf("where")+5);
			temp+=" upper("+tableName+".nbase)=upper(dbname.pre) ";
			
			sql=temp+" and "+sql.substring(sql.indexOf("where")+5);
		}
		else {
            sql+=" where "+tableName+".nbase=dbname.pre ";
        }
		
		if(order.length()>0) {
            sql+=" "+order+",dbname.dbid,recidx,A00Z0,A00Z1";
        } else {
            sql+=" order by dbname.dbid,recidx, A00Z0, A00Z1";
        }
		return sql;
	}
	/**
	 * 根据薪资花名册排序要求 修改sql
	 * @param sql
	 * @return
	 */
	public String getSqlSort(String sql,String tableName)
	{
		String order="";
		if(sql.indexOf("order by")!=-1)
		{
			order=sql.substring(sql.indexOf("order by"));
			sql=sql.substring(0,sql.indexOf("order by"));
		}

		if(order.length()>0) {
            sql+=" "+order;
        } else {
            sql+=" order by recidx";
        }
		return sql;
	}
	
	private String getDBPre(ResultSet resultSet, String dbpre) {
	    String s=null;
	    try{
	        s=resultSet.getString("NBASE");
	    }
	    catch(Exception e){}
	    if(s==null) {
            s=dbpre;
        }
	    return s;
	}
	
	/**
	 * 得到表体内容 & 当前页的分组指标 & 当前页的行数 & 显示的临时照片名称列表 (不分栏)
	 * 
	 * @param isGroupTerm
	 *            表头条件中是否包含分组指标
	 * @param BottomnList
	 *            表头底端的列
	 * @param isGroupPoint
	 *            是否采用分组显示
	 * @param sql
	 *            查询语句
	 * @param rows
	 *            每页的行数
	 * @param currpage
	 *            当前页数
	 * @param keyList
	 *            关键字段列表
	 * @param nOperation
	 *            汇总标示
	 * @param rowHeight
	 *            行高
	 * @param field
	 *            需小计的列名
	 * @param fields
	 *            需累计的列名
	 * @param bodywidth
	 *            表体长度
	 * @param zeroPrint
	 *            0:不为零打印 1：零打印
	 * @param emptyRow
	 *            0：空行不打印 1：空行打印
	 * @param lth
	 *            表的最左端、最上端、表头的总高
	 * @param fontStyle
	 *            [0]FontName [1]FontEffect [2]FontSize
	 * @return ArrayList
	 */
	private ArrayList getBodyContext(boolean isGroupTerm, String isGroupPoint,
			String existColumn, String emptyRow, String zeroPrint, float[] lth,
			ArrayList bottomnList, String sql, int rows, int currpage,
			ArrayList keyList, int nOperation, int rowHeight, String[] field,
			String[] fields, float bodywidth, String[] fontStyle,
			String tableName, String dbpre,String printGrid,String infor_Flag) throws GeneralException {
		HmusterPdf hmusterPdf = new HmusterPdf(this.conn);
		StringBuffer bodyContext = new StringBuffer("");
		RowSet resultSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList photoList = new ArrayList();
		String groupV = "";
		String groupN="";
		String privA0100 = "";
		boolean hasHREF = true;
		int i = 0; // 序号
		int n = 1; // 行数
		boolean canMerge=true;  // 是否能合并
		if("1".equals(isGroupPoint2)&&this.fzhj != null && this.fzhj.length >0&&(isGroupTerm||isGroupTerm2)) {
            canMerge=false;     // 合并有问题
        }
		int p = 0; // 分组显示的页数
		int ii = 0; // 分组显示的序号
		int nn = 0; // 分组显示时控制换页的变量
		int groups=0;  // 分组合计行数
		int groupRowcount=0;//分组分页 每页记录数  
		int realGroups=0;
		ArrayList groupsNum=new ArrayList();//记录合计行的行位置 
		RowSet rs =null;
		int isValues = 0; // 判断查询中是否有值
		try {
			boolean isFirst=true;
			currpage=currpage>1?currpage:1;
			if (!"stipend".equals(infor_Flag)&&!isGroupTerm&&!isGroupTerm2&&( isGroupPoint== null|| !"1".equals(isGroupPoint)))
			{
			    String orderby = "";
			    if(sql.indexOf("order by")!=-1)
		        {
		            orderby=sql.substring(sql.indexOf("order by"));
		            sql=sql.substring(0,sql.indexOf("order by"));
		        }

				if(sql.toLowerCase().indexOf("where")>0) {
                    sql+=" and (recidx>"+rows*(currpage-1)+" and recidx<="+rows*currpage+")";
                } else {
                    sql+=" where (recidx>"+rows*(currpage-1)+" and recidx<="+rows*currpage+")";
                }
                if(orderby.length()>0) {
                    sql+=" "+orderby;
                }
			}
			if(!isGroupTerm&&!isGroupTerm2&&!"stipend".equals(infor_Flag)) {
                sql=getSqlSort(sql,tableName);
            }

			/* 页小计初始化 */
			double[] pageCount=initCount(field);
			double[] totalCount = initCount(fields);
			double[] fzhjCount=initCount(this.fzhj);
			double[] zjCount = initCount(this.zj);
			//第二分组指标合计
			double[] fzhjCount2 = initCount(this.fzhj);
			int begin=0;
			boolean flag=false;
			String groupItem="first";
			
			itemHeArr=itemHeArr!=null?itemHeArr:"";
			String heitemid[] = new String[bottomnList.size()];
			String heitemvalue[] = new String[bottomnList.size()];
			String herevalue[] = new String[bottomnList.size()];
			String isHeMain[] = new String[bottomnList.size()];
			int m[]=new int[bottomnList.size()];  // 合并单元格需合并的行数
			String[] heArr = itemHeArr.split(",");
			for(int h=0;h<bottomnList.size();h++){
				if(heArr[h]!=null&&heArr[h].length()>1){
					String heirem[] = heArr[h].split(":");
					if(heirem[1]!=null&& "true".equalsIgnoreCase(heirem[1])){
						heitemid[h] ="true";
						heitemvalue[h] = "&nbsp;";
						herevalue[h] = "&nbsp;";
						m[h] = 0;
					}
					if(heirem[2]!=null&& "true".equalsIgnoreCase(heirem[2])){
						isHeMain[h] = "true";
					}
				}
			}
			int countAll = 0;
			if(isGroupPoint == null||!"1".equals(isGroupPoint)){
				rs = dao.search("select max(recidx) as recidx from "+tableName);
				if(rs.next()){
					countAll = rs.getInt("recidx");
				}
			}
			String priormainid = "";  // 上条记录
			String mainidvalue = "";  // 当前记录
			/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 xiaoyun 2014-8-20 start */
			String a0100 = "";
			/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 xiaoyun 2014-8-20 end */
			String mainArr[] = new String[bottomnList.size()];
			resultSet=dao.search(sql);
			
			boolean countflag=false;
			if(sql.indexOf("order")>-1){//计算查询记录总数，当只有一条时 取消行合并，changxy 20161229
				String countsql="select count(*) from ("+sql.split("order")[0]+") a";
				ResultSet countrs=dao.search(countsql);
				while(countrs.next()){
					int count=countrs.getInt(1);
					if(count>1){
						countflag=true;
					}
				}
			}else{
				String countsql=" select count(*) from ("+sql+") a";
				ResultSet countrs=dao.search(countsql);
				while(countrs.next()){
					int count=countrs.getInt(1);
					if(count>1){
						countflag=true;
					}
				}
			}
			
//			ResultSetMetaData data=resultSet.getMetaData();//wangcq 2014-11-19
			StringBuffer strTable = new StringBuffer();
			String endhref="</a>";
			DecimalFormat myformat=null;
			String group2Value="";
			String macth="-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";
			BigDecimal zero = new BigDecimal("0");
			String a0100tran="";
			CheckPrivSafeBo checkPrivSafeBo = null;
			if(!userView.isSuper_admin()&& "true".equalsIgnoreCase(this.no_manger_priv)){
				checkPrivSafeBo=new CheckPrivSafeBo(this.conn,this.userView);
			}
			boolean lastrow = false;
			String A0100LastTime = "";//上一行A01000的值
			while (resultSet.next()) {   
				isValues++;
				int recidx = 0;
				if(infor_Flag!=null&& "1".equals(infor_Flag)){
					mainidvalue = resultSet.getString("A0100");
					/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 xiaoyun 2014-8-20 start */
					a0100 = mainidvalue;
					/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 xiaoyun 2014-8-20 end */
					if(!userView.isSuper_admin()&& "true".equalsIgnoreCase(this.no_manger_priv)){
						String privDbpre=checkPrivSafeBo.checkDb(dbpre);
						if(privDbpre.equalsIgnoreCase(dbpre)){
							privA0100 = checkPrivSafeBo.checkA0100("", dbpre, mainidvalue, "");
							if(privA0100.equalsIgnoreCase(mainidvalue)){
								hasHREF = true;
							}else{
								hasHREF = false;
							}
						}else{
							hasHREF = false;
						}
					}
					a0100tran = "~" + SafeCode.encode(PubFunc.convertTo64Base(mainidvalue.toString()));
				}else if(infor_Flag!=null&& "2".equals(infor_Flag)) {
                    mainidvalue = resultSet.getString("B0110");
                } else if(infor_Flag!=null&& "3".equals(infor_Flag)) {
                    mainidvalue = resultSet.getString("E01A1");
                }
	            String href="";
	            if("1".equals(infor_Flag)&&"false".equalsIgnoreCase(no_manger_priv)){//添加控制 勾选不按管理范围取数 不允许穿透  no_manger_priv
	            	if(hasHREF){
	            		href+="<a href=\"javascript:openSelfInfo(\\'/workbench/browse/showselfinfo.do?b_search=link`userbase="+
	            		getDBPre(resultSet,dbpre)+"`flag=notself`returnvalue=100000`a0100=";
	            		if("mobile".equalsIgnoreCase(this.getReturnflag())) {
                            href="<a href=\"javascript:test(\\'"+getDBPre(resultSet,dbpre)+"\\',\\'";
                        }
	            	}
	            }

				if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))//按第二分组指标分组
				{
	    			if(isValues==1)//第一条记录
		    		{
	    				group2Value=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
	    				//add(this.fzhj,fzhjCount2,resultSet);
		    		}
		    		
				}
				//liuy 2015-3-2 7408：员工管理/花名册/高级花名册165，有两个分组指标时，标题为第1分组指标，出不来数据。按第1分组指标分组分页功能不灵。 start
				/* 分组显示 */
				//考虑有分组指标一或二 并且有组内记录数的情况
				if (/*(!isGroupTerm ||!isGroupTerm2) && */isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {//分组分页
					recidx = resultSet.getInt("recidx");
					i++;
					String tempGroupN = " ";
					if (resultSet.getString("GroupN") != null) {
                        tempGroupN = "".equals(resultSet.getString("GroupN"))?" ":resultSet.getString("GroupN");
                    }
					if(groupN.equals(tempGroupN)) {//上一条记录的分组指标等于当前记录的分组指标 记录数增1
						groupRowcount++;
					}else {
						groupRowcount=0;
					}
					if (!groupN.equals(tempGroupN)) {//按组显示序号 为1 
						p++;
						if("1".equals(this.isGroupedSerials)) {
							nn = 0;
						}
						if (p > currpage)
						{
							flag=true;
							break;
						}
						groupN = tempGroupN;						
						if(resultSet.getString("GroupV")!=null) {
                            groupV=resultSet.getString("GroupV");
                        } else {
                            groupV=" ";
                        }
						// 累计清空
						totalCount = initCount(fields);
						fzhjCount = initCount(this.fzhj);
					} else if (nn != 0 &&(nn % rows == 0||(groupRowcount==rows))&&groupRowcount>=rows) { //p 
						
						p++;
						if (p > currpage) {
							groupRowcount=0;
							break;
						}else {
							groupRowcount=0;
						}
					}

					ii++;
					nn++;
					if (p < currpage) {
						// //////// 处理页累计	
						add(fields,totalCount,resultSet);
						add(this.fzhj,fzhjCount,resultSet);
						add(this.zj,zjCount,resultSet);
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							group2Value=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
						}
						continue;
					}
				} else if ((!isGroupTerm||!isGroupTerm2) && isGroupPoint != null&& "1".equals(isGroupPoint)&& "1".equals(isGroupNoPage)) {//分组不分页
					recidx = resultSet.getInt("recidx");
					String tempGroupN = " ";
					if (resultSet.getString("GroupN") != null) {
                        tempGroupN = "".equals(resultSet.getString("GroupN"))?" ":resultSet.getString("GroupN");
                    }
					if(resultSet.getString("GroupV")!=null) {
                        groupV=resultSet.getString("GroupV");
                    } else {
                        groupV=" ";
                    }
					if (i % rows == 0) {
						p++;
						if (p > currpage) {
							flag=true;
							break;
						}
					}
					//liuy 2015-12-8 14944：中国民航工程咨询公司 薪资发放--薪资报表排序有问题 begin
					if (!groupN.equals(tempGroupN)) {
						if("1".equals(isGroupedSerials)){//分组不分页是否按组显示序号
							i=0;
						}
						groupN = tempGroupN;
						fzhjCount = initCount(this.fzhj);
					}//liuy 2015-12-8 end
					i++;
					if (p < currpage) {
						if (!groupN.equals(tempGroupN)) {
							fzhjCount = initCount(this.fzhj);
						}
						groupN = tempGroupN;
						// ////////处理页累计
						add(fields,totalCount,resultSet);
						add(this.fzhj,fzhjCount,resultSet);// TODO 不需要计算，用calcGroupSum计算
						add(this.zj,zjCount,resultSet);
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							group2Value=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
						}
						continue;
					}
				}else if (!isGroupTerm&&!isGroupTerm2&&( isGroupPoint== null|| !"1".equals(isGroupPoint))){//不分组
					recidx = resultSet.getInt("recidx");
					if(isFirst){
						i=rows*(currpage-1);
						isFirst=false;
					}
					i++;
				}else{
					recidx = resultSet.getInt("recidx");
					if (i % rows == 0) {
						p++;
						if (p > currpage) {
                            break;
                        }
					}
					i++;
					if (p < currpage) {
						// ////////处理页累计
						add(fields,totalCount,resultSet);
						add(this.fzhj,fzhjCount,resultSet);
						add(this.zj,zjCount,resultSet);
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							group2Value=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
						}
						continue;
					}
				}
				//liuy 2015-3-2 end
				begin++;
				int columnNum = 0;
				StringBuffer tempBuf=new StringBuffer("");
				/**我的薪酬花名册，数值全为空或者零时，该行数据不显示*/
				int zeroCount=0;//我的薪酬花名册，判断值为空或者为零的总列数
				int columnCount=0;//我的薪酬花名册，画出的总列数
				StringBuffer mygzBuf=new StringBuffer("");//我的薪酬花名册用
				lastrow =false;
				for (Iterator t = bottomnList.iterator(); t.hasNext();) {
					//fontStyle
					tempBuf.setLength(0);
					String[] temp = (String[]) t.next();
					String gridno=temp[0];
					boolean wordWarp=false;
			    	if(musterCellMap!=null&&musterCellMap.get(gridno)!=null) {
			    		if(musterCellMap.get(gridno).get("nWordWrap")!=null&&"1".equals(musterCellMap.get(gridno).get("nWordWrap"))) {
			    			wordWarp=true;
			    		}
			    	}
					fontStyle[0]=temp[10];
					fontStyle[1]=temp[11];
					fontStyle[2]=temp[12];//单元格指标字体按后台取的样式设置
					if(temp[20]!=null&& "1".equals(temp[20])) {
                        continue;
                    }
					columnNum++;
					columnCount++;
					String[] align_arr = transAlign(Integer.parseInt(temp[13]));
					String context_align = align_arr[1];
					String context = "";
					
					if ("S".equals(temp[7])) {
						if (!isGroupTerm &&!isGroupTerm2 && isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
							context = String.valueOf(nn);
						} else {
                            context = String.valueOf(i);
                        }
					} else if (!isGroupTerm &&!isGroupTerm2 && "P".equals(temp[7])) // 生成照片
					{
						String tempName = hmusterPdf.createPhotoFile(getDBPre(resultSet,dbpre)+ "A00", resultSet.getString("A0100"), "P");
						if(tempName.length()>0)
						{
							photoList.add(tempName);
							int a_width=0;
								if(temp[4]!=null&&temp[4].trim().length()>0){
									if(temp[4].indexOf(".")!=-1) {
                                        a_width=(Integer.parseInt(temp[4].substring(0,temp[4].indexOf("."))))-5;
                                    } else {
                                        a_width=Integer.parseInt(temp[4])-5;
                                    }
								}					
							context = "<img src=\"/servlet/vfsservlet?fromjavafolder=true&fileid="+ tempName+ 
										"\"  height=\""+(rowHeight-5)+"\" width=\""+a_width+"\" border=1>";//a_width+a_width/2
						}
						else {
                            context="&nbsp;";
                        }
					}else {
						if("M".equals(temp[9])) {
                            context=Sql_switcher.readMemo(resultSet,"C"+temp[0]);
                        }
						if (!isGroupTerm &&!isGroupTerm2 && existColumn.indexOf("," + temp[0] + ",") != -1){
							try{
								context = resultSet.getString("C" + temp[0]);
								if (context != null && context.indexOf("`") != -1) {
                                    context = context.replaceAll("`", "<br>");
                                }
							}catch(Exception e){}
						}
						else if ((isGroupTerm ||isGroupTerm2) && "G".equals(temp[7])&& isGroupPoint != null&& "1".equals(isGroupPoint)) {
                            context = resultSet.getString("GroupV");
                        } else if (isGroupTerm||isGroupTerm2){
							if("E".equals(temp[7])&&this.isGroupV2)
							{
								context = resultSet.getString("GroupV2");
							}
							else if("E".equals(temp[7]))
							{
								context = "&nbsp;";
							}
							else if(!isGroupTerm&&!isGroupTerm2&&groupPoint!=null&& "1".equals(groupPoint)){
								context = resultSet.getString("GroupV");
							}else{
								try {
									context = resultSet.getString("C" + temp[0]);
									if (context != null && context.indexOf("`") != -1) {
                                        context = context.replaceAll("`", "<br>");
                                    }
								} catch (Exception e) {}
							}
						}else
							if("E".equalsIgnoreCase(temp[7])&&this.isGroupV2)
							{
								context = resultSet.getString("GroupV2");
							}else {
                                context = "&nbsp;";
                            }
						if ("N".equals(temp[9])|| "R".equals(temp[7])) {
							// 页小计
							count(pageCount,field,context,temp);						
							// 处理页累计
							count(totalCount,fields,context,temp);
							// 分组合计
							count(fzhjCount,this.fzhj,context,temp);	
							// 总计
							count(zjCount,this.zj,context,temp);
						}
						
						if (context == null) {
							context = "&nbsp;";
							if ("1".equals(zeroPrint) && "N".equals(temp[9])) {
                                context = "0";
                            }
						}
						if (context != null && !"".equals(context)&& !"&nbsp;".equals(context)&& "N".equals(temp[9])) {
							float f = Float.parseFloat(context);
							if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
							{
									context = round(context, Integer.parseInt(temp[6]));
									if(Double.parseDouble(context)==0) {
										context="&nbsp;";
									}
							} else {
                                context = round(context, Integer.parseInt(temp[6]));
                            }
						}
						if ("D".equals(temp[9])) {
							if(context==null){	
								context = "&nbsp;";
							}
						}
					}
					int aFontSize = Integer.parseInt(fontStyle[2]);
					String style = getFontStyle(fontStyle[1], aFontSize,temp,context,rowHeight);
					if("1".equals(temp[19])) {
                        context="&nbsp;";
                    }
					String style_name = getStyleName2(temp, printGrid, begin); // 处理虚线
					if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&this.fzhj != null && this.fzhj.length >0&&columnNum==1){//分组分页
						if(isGroupTerm||isGroupTerm2){// 插入分组指标
							String tempGroupN = " ";
							if (resultSet.getString("GroupN") != null) {
                                tempGroupN = "".equals(resultSet.getString("GroupN"))?" ":resultSet.getString("GroupN");
                            }
							if (!groupN.equals(tempGroupN)) {
								fzhjCount=calcGroupSum(fzhjCount,this.fzhj," ".equals(groupN)?"":groupN,null, tableName);
								groupN = tempGroupN;						
								if(resultSet.getString("GroupV")!=null) {
                                    groupV=resultSet.getString("GroupV");
                                } else {
                                    groupV=" ";
                                }
								if(begin==1)
								{
									//add(this.fzhj,fzhjCount,resultSet);
								}
								else
								{
							    	//fzhjCount2=totalCounts(fzhjCount2,this.fzhj,this.isGroupTerm2,this.isGroupPoint2,rows,currpage,tableName,sql);
							    	bodyContext.append(getPageCountRows(lth, n, bottomnList,
										this.fzhj, zeroPrint, fzhjCount, rowHeight, Integer.parseInt(fontStyle[2]),
											ResourceFactory.getProperty("gz.gz_acounting.total"),printGrid,19));
									n++;
									//groups++;
								}
								fzhjCount = initCount(this.fzhj);
								//add(this.fzhj,fzhjCount,resultSet);
							}
							else
							{
								//add(this.fzhj,fzhjCount,resultSet);
							}
						}
						else{
							String temp2=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
							if(!temp2.equals(group2Value))
							{
								fzhjCount2=calcGroupSum(fzhjCount2,this.fzhj," ".equals(groupN)?"":groupN,group2Value, tableName);
								if(begin==1)
								{
									add(this.fzhj,fzhjCount2,resultSet);  // TODO 不需要计算，使用calcGroupSum计算
								}
								else
								{
							    	//fzhjCount2=totalCounts(fzhjCount2,this.fzhj,this.isGroupTerm2,this.isGroupPoint2,rows,currpage,tableName,sql);
							    	bodyContext.append(getPageCountRows(lth, n, bottomnList,
										this.fzhj, zeroPrint, fzhjCount2, rowHeight, Integer.parseInt(fontStyle[2]),
										"小计",printGrid,19));
							    	n++;
							    	fzhjCount2 = initCount(this.fzhj);
								    add(this.fzhj,fzhjCount2,resultSet);
								    //groups++;
								}
								group2Value=temp2;
							}
							else
							{
								add(this.fzhj,fzhjCount2,resultSet);
							}
						}
					}else if(!isGroupTerm &&!isGroupTerm2 && isGroupPoint != null&& "1".equals(isGroupPoint)&& "1".equals(isGroupNoPage)){//分组不分页
						String itemvalue = resultSet.getString("GroupN");
						itemvalue=itemvalue!=null?itemvalue:"";
						column_num=0;
						if (!"first".equalsIgnoreCase(groupItem)&&this.fzhj != null && this.fzhj.length > 0&&
								this.isfzhj&&!isGroupTerm &&!isGroupTerm2 && isGroupPoint != null&& "1".equals(isGroupPoint)&&
								!itemvalue.equalsIgnoreCase(groupItem)) // 显示分组合计
						{
							//fzhjCount=totalCounts(fzhjCount,this.fzhj,isGroupTerm,isGroupPoint,rows,currpage,tableName,sql);
							fzhjCount=calcGroupSum(fzhjCount,this.fzhj,groupItem,null, tableName);
							bodyContext.append(getPageCountRows(lth, n, bottomnList,
									this.fzhj, zeroPrint, fzhjCount, rowHeight, Integer.parseInt(fontStyle[2]),
									ResourceFactory.getProperty("planar.stat.total"),printGrid,19));//小计
							groupsNum.add(n);//每次出现合计行记录下当前合计行的位置 原因：如果行合并跨多个合计行 计算行高和位置时会不准确 
							n++;
							fzhjCount = initCount(this.fzhj);
							groups++;
							realGroups++;
							lastrow = true; //合计行标志				
						}
						groupItem = itemvalue;
					}
					context=context!=null&&context.trim().length()>0?context.trim():"&nbsp;";
					if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&
							((temp[20]!=null&&(temp[20].toUpperCase().indexOf("<NAMECOUNT>")!=-1||
								temp[20].toUpperCase().indexOf("<NAMEVALUE>")!=-1)&&"N".equalsIgnoreCase(temp[9]))))
					{
						int slope=Integer.parseInt(temp[6]);
						StringBuffer format =new StringBuffer("");
						for(int sindex=0;sindex<slope;sindex++)
						{
							format.append("0");
						}
						myformat = new DecimalFormat(format.length()>0?("0."+format.toString()):"0");
						context=myformat.format(Float.parseFloat(context));
					}
					//我的薪酬，
					if("stipend".equals(infor_Flag))
					{
		    			if ("S".equals(temp[7])|| "P".equals(temp[7])||(temp[18]!=null&&
		    					!"".equals(temp[18])&& "A01".equalsIgnoreCase(temp[18]))||
		    					(temp[19]!=null&&(temp[19].toUpperCase().endsWith("Z0")||
		    							temp[19].toUpperCase().endsWith("Z1")|| "A0101".equalsIgnoreCase(temp[19])||
		    							"b0110".equalsIgnoreCase(temp[19])|| "e0122".equalsIgnoreCase(temp[19])||
		    							"e01a1".equalsIgnoreCase(temp[19])))) {
			    			zeroCount++;
			    		}else{
                           if("".equals(context.trim())||"&nbsp;".equalsIgnoreCase(context)){
                        	   zeroCount++;
                           }else if(context.matches(macth))
                           {
                        	   BigDecimal tempBD = new BigDecimal(context);
                        	   if(zero.compareTo(tempBD)==0) {
                                   zeroCount++;
                               }
                           }
			    		}
					}
					/**有自动合并格的指标*/
					if(canMerge&&heitemid[columnNum-1]!=null&&heitemid[columnNum-1].trim().length()>0
							/*&&temp[19]!=null&&temp[19].equalsIgnoreCase(heitemid[columnNum-1])*/
							&&context!=null&&!"".equals(context)/*&&!context.equalsIgnoreCase("&nbsp;")*/)
						//&&!heitemid[columnNum-1].equalsIgnoreCase(groupPoint)
					{
						boolean ismain = false;
						//liuy 2015-2-28 7561：花名册中一个人多条记录时姓名可以设置合并单元格，但是照片不能合并，照片应该自动合并 start
						boolean PhotoColMerge = false;
						if(!StringUtils.isEmpty(priormainid)) {
                            if("P".equalsIgnoreCase(temp[7])&&A0100LastTime.equalsIgnoreCase(a0100)) {
                                PhotoColMerge = true;
                            }
                        }
						//liuy 2015-2-28 end
						priormainid = mainidvalue;
						/**合并单元格时，按人员合并*/
						if(isHeMain[columnNum-1]!=null&& "true".equalsIgnoreCase(isHeMain[columnNum-1])){
							if(n>1&&mainArr[columnNum-1]!=null&&!mainArr[columnNum-1].equalsIgnoreCase(mainidvalue)){
								ismain = true;
							}
							priormainid = mainArr[columnNum-1];
							mainArr[columnNum-1] = mainidvalue;
						}
						if(priormainid != null && priormainid.length() > 0) {
                            priormainid = "~" + SafeCode.encode(PubFunc.convertTo64Base(priormainid));
                        }
						if(columnNum==1) {
                            strTable = new StringBuffer();
                        }
						if(heitemvalue[columnNum-1].equalsIgnoreCase(context)||PhotoColMerge){//liuy 2015-2-27 7561：花名册中一个人多条记录时姓名可以设置合并单元格，但是照片不能合并，照片应该自动合并
							if(n>1) {
                                m[columnNum-1]++;
                            }
							if((i>0&&i%rows == 0)||((isGroupPoint == null||!"1".equals(isGroupPoint))&&countAll==recidx)||
									ismain||resultSet.isLast()){
								int topheight=0; //rtop 行合并时 考虑分组合计时添加的合计行  区分有合计行与无合计行
								int groupsSum=0;//记录在合并行范围内出现的合计行
								if(!lastrow){//最后一行上方是非合计行
									for (int j = 0; j < groupsNum.size(); j++) {
										if(n-m[columnNum-1]<Integer.parseInt(groupsNum.get(j).toString())){ //当前行-合计行<合计行出现的位置 则合并行内出现合计行
											groupsSum++;
										}
									}
								}
								if(groups!=0){
									if(m[columnNum-1]!=0)//行合并
                                    {
                                        if(n-1==(m[columnNum-1]+realGroups)) {
                                            topheight = 0; //+1 算上当前自己行 原因：rtop会上移一个格
                                        } else {
                                            topheight = (n - m[columnNum - 1] - 1 - groupsSum) * rowHeight;
                                        }
                                    } else {
                                        topheight=(n-groups+1)*rowHeight;
                                    }
								}else{												//m[columnNum-1] 存储是当前合并行的数
									if(ismain) {
                                        topheight=(n-m[columnNum-1]-1)*rowHeight;
                                    } else {
                                        topheight=(n-m[columnNum-1]-1)*rowHeight;
                                    }
								}
								//liuy 2015-3-9 7797：高级花名册-研发中心加班统计表（页面有串行的现象且合计行没有显示姓名） begin
								if(i % rows == 0&&lastrow&&topheight>0)//最后一行的前面一行是合计
                                {
                                    topheight-=rowHeight;
                                }
								/*if(topheight>0&&(!resultSet.isLast()&&i % rows != 0))// 合并单元格错位 27789 国贸集团员工管理高级花名册设置了行合并单元格，数据错乱显示 
									topheight-=rowHeight;   rtop的高度会有问题导致错行 先将其注释掉 changxy 20170518*/
								//liuy 2015-3-9 end
								int rowsheight=0;
								if(ismain){
									rowsheight=(m[columnNum-1])*rowHeight;
									tempBuf.append(getTempTableHe(lth,topheight, rowsheight, temp,
											columnNum, style_name, context_align,style+";Font-family:"+fontStyle[0]+";"));
								}else{
									rowsheight=0;//  行合并 //rtop 行合并时 考虑分组合计时添加的合计行  区分当前页有无合计行
									if(groups!=0){
										if(n-1==m[columnNum-1]+realGroups) {
                                            rowsheight=(m[columnNum-1]+1+realGroups)*rowHeight;		//-1 原因：合并计算行高会出现合并格过高
                                        } else{
											if(lastrow&&i%rows==0)//最后一行是合并行
                                            {
                                                rowsheight=(m[columnNum-1]+2)*rowHeight;
                                            } else {
												rowsheight=(m[columnNum-1]+1+groupsSum)*rowHeight;
											}
										}
									}else{
										rowsheight=(m[columnNum-1]+1)*rowHeight;
									}
									tempBuf.append(getTempTableHe(lth,topheight, rowsheight, temp,
										columnNum, style_name, context_align,style+";Font-family:"+fontStyle[0]+";"));
								}
								if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&&
										"a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
									if(hasHREF){
					            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
					            		{
					            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                tempBuf.append(href+a0100tran+"\\',\\'"+context+"\\')\">");
                                            } else {
                                                tempBuf.append(context);
                                            }
					            		}
					            		else {
					            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                tempBuf.append(href+a0100tran+"\\')\">");
                                            }
					            			
					            		}
					            			
					            	}
							    }
								tempBuf.append("<font face=\""+fontStyle[0]+"\" style=\""+style+(getFontMargin(context_align))+"\">");
								if (!isGroupTerm &&!isGroupTerm2 && "P".equals(temp[7])) {
                                    tempBuf.append(context);
                                } else
								{
									if(!hasFieldReadPriv(temp,false)) {
                                        context="";
                                    }
									if (!hasHREF) {
                                        tempBuf.append(context.replaceAll(" ","&nbsp;"));
                                    } else {
                                        tempBuf.append(context);
                                    }
								}
								tempBuf.append("</font>");
								if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
									if(hasHREF){
										if("false".equalsIgnoreCase(no_manger_priv)) {
                                            tempBuf.append(endhref);
                                        }
					            	}
							    }
								tempBuf.append("</div></td></tr></table> \n ");
								if((i%rows == 0||countAll==recidx||resultSet.isLast())&&ismain){
									tempBuf.append(getTempTableHe(lth,(n-1)*rowHeight,rowHeight, temp,
											columnNum, style_name, context_align,style+";Font-family:"+fontStyle[0]+";"));
									if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
										if(hasHREF){
											if("false".equalsIgnoreCase(no_manger_priv)) {
                                                tempBuf.append(href+a0100tran+"\\')\">");
                                            }
											
						            	}
								    }
									tempBuf.append("<font face=\""+fontStyle[0]+"\" style=\""+style+(getFontMargin(context_align))+"\">");
									if (!isGroupTerm &&!isGroupTerm2 && "P".equals(temp[7])) {
                                        tempBuf.append(context);
                                    } else
									{
										if(!hasFieldReadPriv(temp,false)) {
                                            context="";
                                        }
										if (!hasHREF) {
                                            tempBuf.append(context.replaceAll(" ","&nbsp;"));
                                        } else {
                                            tempBuf.append(context);
                                        }
									}
									tempBuf.append("</font>");
									if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
										if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
						            		tempBuf.append(endhref);
						            	}
								    }
									tempBuf.append("</div></td></tr></table> \n ");
								}
								m[columnNum-1]=0;
							}
							if (!isGroupTerm &&!isGroupTerm2 && isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
								if(ismain) {
                                    strTable.append(getTempTableHe(lth,(n-m[columnNum-1]-1)*rowHeight, (m[columnNum-1])*rowHeight, temp,
                                            columnNum, style_name, context_align,style+";Font-family:"+fontStyle[0]+";"));
                                } else {
                                    strTable.append(getTempTableHe(lth,(n-m[columnNum-1]-1)*rowHeight, (m[columnNum-1]+1)*rowHeight, temp,
                                        columnNum, style_name, context_align,style+";Font-family:"+fontStyle[0]+";"));
                                }
								if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
									if(hasHREF){
					            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
					            		{
					            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                tempBuf.append(href+a0100tran+"\\',\\'"+context+"\\')\">");
                                            } else {
                                                tempBuf.append(context);
                                            }
					            		}
					            		else {
					            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                tempBuf.append(href+a0100tran+"\\')\">");
                                            }
					            			
					            		}
					            			
					            	}
							    }
								strTable.append("<font face=\""+fontStyle[0]+"\" style=\""+style+(getFontMargin(context_align))+"\">");
								if (!isGroupTerm &&!isGroupTerm2 && "P".equals(temp[7])) {
                                    strTable.append(context);
                                } else
								{
									if(!hasFieldReadPriv(temp,false)) {
                                        context="";
                                    }
									if (!hasHREF) {
                                        strTable.append(context.replaceAll(" ","&nbsp;"));
                                    } else {
                                        strTable.append(context);
                                    }
								}
								strTable.append("</font>");
								if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
									if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
					            		tempBuf.append(endhref);
					            	}
							    }
								strTable.append("</div></td></tr></table> \n ");
							}
							heitemvalue[columnNum-1] = context;
							if(!ismain) {
                                herevalue[columnNum-1] = context;
                            }
							if(i%rows==0||ismain||resultSet.isLast())
							{
								bodyContext.append(tempBuf.toString());
							}
							else {
                                continue;
                            }
						}else{
							if(i>0&&(i%rows == 0||resultSet.isLast())){//最后一行数据
								tempBuf.append(getTempTable(lth, n, rowHeight, temp,columnNum, style_name, context_align,style+";Font-family:"+fontStyle[0]+";",wordWarp));
								if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
									if(hasHREF){
					            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
					            		{
					            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                tempBuf.append(href+a0100tran+"\\',\\'"+context+"\\')\">");
                                            } else {
                                                tempBuf.append(context);
                                            }
					            		}
					            		else
					            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                tempBuf.append(href+a0100tran+"\\')\">");
                                            }
					            			
					            	}
							    }
								tempBuf.append("<font face=\""+fontStyle[0]+"\" style=\""+style+(getFontMargin(context_align))+"\">");
								if (!isGroupTerm && !isGroupTerm2 && "P".equals(temp[7])) {
                                    tempBuf.append(context);
                                } else
								{
									if(!hasFieldReadPriv(temp,false)) {
                                        context="";
                                    }
									if (!hasHREF) {
                                        tempBuf.append(context.replaceAll(" ","&nbsp;"));
                                    } else {
                                        tempBuf.append(context);
                                    }
								}
								tempBuf.append("</font>");
								if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
									if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
					            		tempBuf.append(endhref);
					            	}
							    }
								tempBuf.append("</div></td></tr></table> \n ");
//								m[columnNum-1]=0;
							}
							if (!isGroupTerm &&!isGroupTerm2 && isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
								strTable.append(getTempTable(lth, n, rowHeight, temp,columnNum, style_name, context_align,style+";Font-family:"+fontStyle[0]+";",wordWarp));
								if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
									if(hasHREF){
					            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
					            		{
					            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                tempBuf.append(href+a0100tran+"\\',\\'"+context+"\\')\">");
                                            } else {
                                                tempBuf.append(context);
                                            }
					            		}
					            		else {
					            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                tempBuf.append(href+a0100tran+"\\')\">");
                                            }
					            		}
					            	}
							    }
								strTable.append("<font face=\""+fontStyle[0]+"\" style=\""+style+(getFontMargin(context_align))+"\">");
								if (!isGroupTerm &&!isGroupTerm2 && "P".equals(temp[7])) {
                                    strTable.append(context);
                                } else
								{
									if(!hasFieldReadPriv(temp,false)) {
                                        context="";
                                    }
									if (!hasHREF) {
                                        strTable.append(context.replaceAll(" ","&nbsp;"));
                                    } else {
                                        strTable.append(context);
                                    }
								}
								strTable.append("</font>");
								if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
									if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
					            		tempBuf.append(endhref);
					            	}
							    }
								strTable.append("</div></td></tr></table> \n ");
							}
							herevalue[columnNum-1] = context;
						}
						// 如果是最后一条记录(列值变化)，要把前一个值显示到相应位置(在当前行上方) 显示到当前行上方若上一行是合计行会导致此列值放到上一行
						if(!heitemvalue[columnNum-1].equalsIgnoreCase(herevalue[columnNum-1])){
							int topheight=0;
							 topheight= (n-1-m[columnNum-1]-groups)*rowHeight;
							if(lastrow&&groups>=2){
								groups--; //当前行上方有合计行时 并且合计行上方的指标有合并格存在 //4476
								topheight=(n-1-m[columnNum-1]-groups)*rowHeight;
							}
							//区分如果合并行从第一行开始时 
										//m[num-1]+1 计算合并行数从0开始
							if((n-1)==(m[columnNum-1]+1+realGroups)) {
                                topheight= (n-1-m[columnNum-1]-1-realGroups)*rowHeight;
                            }
							if(lastrow&&topheight>0&&groups<=1)//
                            {
                                topheight-=rowHeight;
                            }
							if(groups==0&&topheight>0)//
                            {
                                topheight-=rowHeight;
                            }
							
							/*	
								topheight=(n-m[columnNum-1]-1-groups)*rowHeight; //当前行-合并行-1-合计行=上一个单元格位置
							//}
							if(topheight>0 && groups<=1&&lastrow)//Bug0041168 合并单元格错位  注释掉此位置 单一行会出现错位的问题
								topheight-=rowHeight;
							if(groups==0&&topheight>0)//
								topheight-=rowHeight;
							//liuy 2015-3-9 7797：高级花名册-研发中心加班统计表（页面有串行的现象且合计行没有显示姓名） begin
							if(topheight>0 && groups>=2)
								topheight+=rowHeight*(groups-2);
							//liuy 2015-3-9 end
*/							// 去掉分组一合计行
							int tableHeight=(m[columnNum-1]+1)*rowHeight;
							if((n-1)==(m[columnNum-1]+1+realGroups)&&realGroups!=0) {
                                tableHeight=(m[columnNum-1]+realGroups)*rowHeight;
                            }
							if(("1".equals(isGroupPoint2)||"1".equals(isGroupPoint))&&this.fzhj != null && this.fzhj.length >0&&(isGroupTerm||isGroupTerm2)){
								if(topheight>0) {
                                    topheight-=rowHeight;
                                }
							}    //添加n>1 条件 原因：当有多行列内容相同并且是从起始行还是 进行行合并时 如果不加n>1 第一行会单独进行行合并 数据展现不正确 
							if(countflag){//分组指标默认合并 当查询只有一条记录时 查询出得结果被&nbsp覆盖导致看不见数据  changxy 20161229
							tempBuf.append(getTempTableHe(lth,topheight,tableHeight, temp,columnNum, style_name, context_align,style+";Font-family:"+fontStyle[0]+";"));
							if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
								if(hasHREF){
				            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
				            		{
				            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                            tempBuf.append(href+priormainid+"\\',\\'"+context+"\\')\">");//mainidvalue
                                        } else {
                                            tempBuf.append(context);//mainidvalue
                                        }
				            		}
				            		else {
				            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                            tempBuf.append(href+priormainid+"\\')\">");//mainidvalue
                                        }
				            		}
				            	}
						    }
							tempBuf.append("<font face=\""+fontStyle[0]+"\" style=\""+style+(getFontMargin(context_align))+"\">");
							
							if (!isGroupTerm &&!isGroupTerm2 && "P".equals(temp[7])) {
                                tempBuf.append(heitemvalue[columnNum-1]);
                            } else
							{
								if(!hasFieldReadPriv(temp,false)) {
                                    heitemvalue[columnNum-1]="";
                                }
								if (!hasHREF) {
                                    tempBuf.append(heitemvalue[columnNum-1].replaceAll(" ","&nbsp;"));
                                } else {
                                    tempBuf.append(heitemvalue[columnNum-1]);
                                }
							}
							tempBuf.append("</font>");
							if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
								if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
				            		tempBuf.append(endhref);
				            	}
						    }
							tempBuf.append("</div></td></tr></table> \n ");
						}	
							heitemvalue[columnNum-1] = context;
							if(isGroupPoint == null||!"1".equals(isGroupPoint)){
								if(countAll==recidx){
									tempBuf.append(getTempTableHe(lth,(n-1)*rowHeight,rowHeight, temp,columnNum, style_name, context_align,style+";Font-family:"+fontStyle[0]+";"));
									if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
										if(hasHREF){
						            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
						            		{
						            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                    tempBuf.append(href+a0100tran+"\\',\\'"+context+"\\')\">");
                                                } else {
                                                    tempBuf.append(context);
                                                }
						            		}
						            		else {
						            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                    tempBuf.append(href+a0100tran+"\\')\">");
                                                }
						            		}
						            	}
								    }
									tempBuf.append("<font face=\""+fontStyle[0]+"\" style=\""+style+(getFontMargin(context_align))+"\">");
									
									if (!isGroupTerm &&!isGroupTerm2 && "P".equals(temp[7])) {
                                        tempBuf.append(heitemvalue[columnNum-1]);
                                    } else
									{
										if(!hasFieldReadPriv(temp,false)) {
                                            heitemvalue[columnNum-1]="";
                                        }
										if (!hasHREF) {
                                            tempBuf.append(heitemvalue[columnNum-1].replaceAll(" ","&nbsp;"));
                                        } else {
                                            tempBuf.append(heitemvalue[columnNum-1]);
                                        }
									}
									tempBuf.append("</font>");
									if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
										if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
						            		tempBuf.append(endhref);
						            	}
								    }
									tempBuf.append("</div></td></tr></table> \n ");
								}
							  	
							}
							m[columnNum-1]=0;
						}else{
							continue;
						}
					}else{
						tempBuf.append(getTempTable(lth, n, rowHeight, temp,
							columnNum, style_name, context_align,style+";Font-family:"+fontStyle[0]+";",wordWarp));
						if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
							if(hasHREF){
			            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
			            		{
			            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                        tempBuf.append(href+a0100tran+"\\',\\'"+context+"\\')\">");
                                    } else {
                                        tempBuf.append(context);
                                    }
			            		}
			            		else {
			            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                        tempBuf.append(href+a0100tran+"\\')\">");
                                    }
			            			
			            		}
			            	}
					    }
						tempBuf.append("<font face=\""+fontStyle[0]+"\" style=\""+style+(getFontMargin(context_align))+"\">");
						if (!isGroupTerm &&!isGroupTerm2 && "P".equals(temp[7])) {
                            tempBuf.append(context);
                        } else
						{
							if(!hasFieldReadPriv(temp,false)) {
                                context="";
                            }
							if (!hasHREF) {
                                context=context.replaceAll(" ","&nbsp;");
                            }
							if("M".equals(temp[9]) || "A".equals(temp[9])) {
                                context=context.replaceAll("\r\n", "<br>");
                            }
							tempBuf.append(context);
						}
						tempBuf.append("</font>");
						if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[19])&& "1".equals(this.infor_Flag)){
							if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
			            		tempBuf.append(endhref);
			            		/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 xiaoyun 2014-8-20 start */
			            		if(StringUtils.equals(linktype, "1")) {
			            			String select = Sql_switcher.isnull("max(pl.plan_id)", "0");
				            		RowSet row = dao.search("select "+select+" from per_plan pl left join per_object po on pl.plan_id=po.plan_id where pl.status=7 and busitype='1' and po.object_id='"+mainidvalue+"'");
				            		if(row.next()) {
				            			int planId = row.getInt(1);
				            			if(planId > 0) {
				            				tempBuf.append("&nbsp;<img src=\"/images/icon_fbxgfx.gif\" border=0 title=\"能力素质雷达图\" style=\"cursor:pointer; vertical-align:middle;margin-left:2px;\" onclick=\"openAbilityDialog(\\'"+PubFunc.encrypt(mainidvalue)+"\\',"+planId+");\"/>");			            		 
				            			}
				            		}
			            		}
			            		/* 任务：3818 点击人员高级花名册链接显示最近一个已结束能力素质雷达图 xiaoyun 2014-8-20 end */
			            	}
					    }
						tempBuf.append("</div></td></tr></table> \n ");
					}
					if(!"stipend".equals(infor_Flag)) {
                        bodyContext.append(tempBuf.toString());
                    } else {
                        mygzBuf.append(tempBuf.toString());
                    }
				}
				if("stipend".equals(infor_Flag))
				{
					if(zeroCount!=columnCount)
					{
						bodyContext.append(mygzBuf.toString());
						n++;
					}
					else
					{
						nn--;
						i--;
					}
				}else
				{
					n++;
				}
				A0100LastTime = a0100;//保存本次a0100
				
			}
			if(!isGroupTerm&&!isGroupTerm2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&this.fzhj!=null&&this.fzhj.length>0)
			{
				boolean tempflag = false;
				if(n>=1){
					resultSet.previous();//光标重置 重新查询 设置分组指标程序rs取数据时未将光标重新定位到开始位置 29226 changxy
					String temp = resultSet.getString("groupN")==null?"":resultSet.getString("groupN");
					String temp2 = resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
					if(resultSet.next()){
						if(!temp2.equals(resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2"))||!temp.equals(resultSet.getString("groupN")==null?"":resultSet.getString("groupN"))) {
                            tempflag = true;
                        }
					}
					if(tempflag){
						fzhjCount2=calcGroupSum(fzhjCount2,this.fzhj," ".equals(groupN)?"":groupN,group2Value, tableName);
						bodyContext.append(getPageCountRows(lth, n, bottomnList,
								this.fzhj, zeroPrint, fzhjCount2, rowHeight, Integer.parseInt(fontStyle[2]),
								"小计",printGrid,19));
						n++;
						fzhjCount2 = initCount(this.fzhj);
					}
				}
				if (!tempflag) {					
					//fzhjCount2=totalCounts(fzhjCount2,this.fzhj,this.isGroupTerm2,this.isGroupPoint2,rows,currpage,tableName,sql);
					// TODO fzhjCount2=calcGroupSum(fzhjCount2,this.fzhj,,, tableName);
					bodyContext.append(getPageCountRows(lth, n, bottomnList,this.fzhj, zeroPrint, fzhjCount2, rowHeight, Integer.parseInt(fontStyle[2]),"小计",printGrid,19));
					n++;
					fzhjCount2 = initCount(this.fzhj);
				}
			}else if(!isGroupTerm &&!isGroupTerm2 && isGroupPoint != null&& "1".equals(isGroupPoint)&& "1".equals(isGroupNoPage)){//分组不分页
				column_num=0;
				if (this.fzhj != null && this.fzhj.length >0&&this.isfzhj&&!isGroupTerm &&!isGroupTerm2 && isGroupPoint != null&& "1".equals(isGroupPoint)&&(totalPage==currpage||flag)) // 显示分组合计
				{
					//fzhjCount=totalCounts(fzhjCount,this.fzhj,isGroupTerm,isGroupPoint,rows,currpage,tableName,sql);
					// TODO fzhjCount=calcGroupSum(fzhjCount,this.fzhj,,null, tableName);
					bodyContext.append(getPageCountRows(lth, n, bottomnList,this.fzhj, zeroPrint, fzhjCount, rowHeight, Integer.parseInt(fontStyle[2]),ResourceFactory.getProperty("planar.stat.total"),printGrid,19));
					n++;
					fzhjCount = initCount(this.fzhj);
					groups++;
					realGroups++;
				}
				column_num=0;
				if (this.zj!= null && this.zj.length > 0&&this.iszj&&(totalPage==currpage)) // 显示总计
				{
					if (!"stipend".equals(infor_Flag)&&!isGroupTerm &&!isGroupTerm2&&( isGroupPoint== null|| !"1".equals(isGroupPoint)))
					{
			    		zjCount=totalCounts(zjCount,this.zj,isGroupTerm,isGroupPoint,rows,currpage,tableName,sql);
					}
					bodyContext.append(getPageCountRows(lth, n, bottomnList,this.zj, zeroPrint, zjCount, rowHeight, Integer.parseInt(fontStyle[2]),ResourceFactory.getProperty("workdiary.message.total"),printGrid,19));
					n++;
				}
				if (n < rows) // 如果纪录没有打满一页
					{
						if ("1".equals(printGrid)&&emptyRow != null && "1".equals(emptyRow)) // 如果空行打印
						{
							int blankRows=rows-n+1;
							appendBlankRows(bottomnList, n, blankRows, printGrid, fontStyle, lth, rowHeight, bodyContext);
							i+=blankRows;
							n+=blankRows;
						}
					}
			}else{
//				if (isValues != 0) {
				if(itemHeArr.trim().length()>0) {
                    if(!isGroupTerm &&!isGroupTerm2 && isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)){//分组分页
                        bodyContext.append(strTable);
                    }
                }
				// 设置分组指标2并插入分组指标, 合计行要在空行上面
				if("1".equals(isGroupPoint2)&&this.fzhj != null && this.fzhj.length >0&&(isGroupTerm||isGroupTerm2)){ 
					fzhjCount=calcGroupSum(fzhjCount,this.fzhj," ".equals(groupN)?"":groupN,null, tableName);
					bodyContext.append(getPageCountRows(lth, n, bottomnList,this.fzhj, zeroPrint, fzhjCount, rowHeight, 
							Integer.parseInt(fontStyle[2]),ResourceFactory.getProperty("gz.gz_acounting.total"),printGrid,19));
					n++;
				}
				if (n < rows) // 如果纪录没有打满一页
				{
					if ("1".equals(printGrid)&&emptyRow != null && "1".equals(emptyRow)) // 如果空行打印
					{
						int blankRows=rows-n+1;
						appendBlankRows(bottomnList, n, blankRows, printGrid, fontStyle, lth, rowHeight, bodyContext);
						i+=blankRows;
						n+=blankRows;
					}
				}
				/* 显示页小计 */
				column_num=0;
				if (field != null && field.length > 0&& this.isyxj) {
					bodyContext.append(getPageCountRows(lth, n, bottomnList,field, zeroPrint, pageCount, rowHeight, Integer.parseInt(fontStyle[2]),"hmuster.label.pageCount",printGrid,19));
					n++;
				}
				column_num=0;
				if (fields != null && fields.length > 0&&this.isylj) // 显示页累计
				{
					if(!isGroupTerm &&!isGroupTerm2 &&( isGroupPoint== null|| !"1".equals(isGroupPoint)))
					{
						totalCount=totalCounts(totalCount,fields,isGroupTerm,isGroupPoint,rows,currpage,tableName,sql);
					}
					bodyContext.append(getPageCountRows(lth, n, bottomnList,fields, zeroPrint, totalCount, rowHeight, Integer.parseInt(fontStyle[2]),"hmuster.label.toatlCount",printGrid,19));
					n++;
				}

				column_num=0;
				if (this.fzhj != null && this.fzhj.length > 0&&this.isfzhj && isGroupPoint != null
						&& "1".equals(isGroupPoint)&&(flag||totalPage==currpage||isValues==0)) // 显示分组合计
				{
					if("1".equals(isGroupPoint2)&&(isGroupTerm||isGroupTerm2)){
						// 不输出
					}
					else{
						//fzhjCount=totalCounts(fzhjCount,this.fzhj,isGroupTerm,isGroupPoint,rows,currpage,tableName,sql);
						if("1".equals(isGroupNoPage))//组内记录数 分组不分页时 分组指标应为空  changxy 20170904
                        {
                            fzhjCount=calcGroupSum(fzhjCount,this.fzhj,"",null, tableName);
                        } else {
                            fzhjCount=calcGroupSum(fzhjCount,this.fzhj," ".equals(groupN)?"":groupN,null, tableName);
                        }
						bodyContext.append(getPageCountRows(lth, n, bottomnList,this.fzhj, zeroPrint, fzhjCount, rowHeight, Integer.parseInt(fontStyle[2]),ResourceFactory.getProperty("gz.gz_acounting.total"),printGrid,19));
						n++;
					}
				}

				column_num=0;
				if (this.zj!= null && this.zj.length > 0&&this.iszj&&(totalPage==currpage||isValues== 0)) // 显示总计
				{
					if (!"stipend".equals(infor_Flag)&&!isGroupTerm &&!isGroupTerm2 &&( isGroupPoint== null|| !"1".equals(isGroupPoint))) {
                        zjCount=totalCounts(zjCount,this.zj,isGroupTerm,isGroupPoint,rows,currpage,tableName,sql);
                    } else if ((isGroupTerm||isGroupTerm2)&&("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))) {
                        zjCount=totalCounts3(zjCount,zj,tableName,bottomnList);  // 重新计算总计
                    }
					bodyContext.append(getPageCountRows(lth, n, bottomnList,this.zj, zeroPrint, zjCount, rowHeight, Integer.parseInt(fontStyle[2]),ResourceFactory.getProperty("workdiary.message.total"),printGrid,19));
					n++;
				}
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			try{
				if(resultSet!=null)
				{
					try
					{
						resultSet.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				if(rs!=null)
				{
					try
					{
						rs.close();
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				System.gc();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		bodyContext.append(" </table> ");
		ArrayList list = new ArrayList();
		list.add(bodyContext.toString()); // 表体内容
		list.add(groupV); // 当前页的分组指标
		list.add(new Integer(n)); // 当前页的行数
		list.add(photoList); // 如果表中需显示照片，则列出显示的临时照片名称列表，已备删除
		setGroupNcode(groupN); // 当前页的分组指标值
		return list;
	}

	
	
	public void add(String[] fields,double[] count,ResultSet rs)
	{
		try
		{
			if (fields != null && fields.length > 0) {
				for (int b = 0; b < fields.length; b++) {
					String a_context = rs.getString(fields[b]);
					if (a_context != null){
						BigDecimal b1 = new BigDecimal(Double.toString(count[b]));
						BigDecimal b2 = new BigDecimal(Double.toString(Double.parseDouble(a_context)));
						count[b] = b1.add(b2).doubleValue();
					}
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 日期纠正格式
	 * @param str
	 * @return
	 */
	public String strToDate(String str){
		String dateStr="";
		String year="";
		String month="";
		String day = "";
		char arr[] = str.toCharArray();
		int n=0;
		for(int i=0;i<arr.length;i++){
			if(n==0){
				if(arr[i]>47&&arr[i]<58){
					year+=arr[i];
				}else{
					n=1;
				}
			}else if(n==1){
				if(arr[i]>47&&arr[i]<58){
					month+=arr[i];
				}else{
					n=2;
				}
			}else if(n==2){
				if(arr[i]>47&&arr[i]<58){
					day+=arr[i];
				}
			}
		}
		if(year.length()>3){
			dateStr+=year;
			dateStr+="-"+month;
			dateStr+="-"+day;
		}else if(day.length()>3){
			dateStr+=day;
			dateStr+="-"+month;
			dateStr+="-"+year;
		}else if(month.length()>3){
			dateStr+=month;
			dateStr+="-"+day;
			dateStr+="-"+year;
		}else{
			dateStr = str;
		}
		return dateStr;
	}
	
	/**
	 * 生成空行
	 * @param bottomnList
	 * @param row 起始行号
	 * @param rows 空行数
	 */
	private void appendBlankRows(ArrayList bottomnList, int row, int rows, String printGrid, String[] fontStyle,
			float[] lth, int rowHeight, StringBuffer bodyContext) {
		for (int i=0;i<rows;i++){
			int columnNum = 0;
			for (Iterator t = bottomnList.iterator(); t.hasNext();) {
				columnNum++;
				String[] temp = (String[]) t.next();
				String gridno=temp[0];
				if(temp[20]!=null&& "1".equals(temp[20])) {
                    continue;
                }
				String context = " ";
				float width = Float.parseFloat(temp[4]) - 3;
				String style_name = getStyleName2(temp,printGrid,2); // 处理虚线
				// L,T,R,B,
				String[] tt = transAlign(Integer.parseInt(temp[11]));
				int aFontSize = Integer.parseInt(fontStyle[2]);
				boolean wordWarp=false;
		    	if(musterCellMap!=null&&musterCellMap.get(gridno)!=null) {
		    		if(musterCellMap.get(gridno).get("nWordWrap")!=null&&"1".equals(musterCellMap.get(gridno).get("nWordWrap"))) {
		    			wordWarp=true;
		    		}
		    	}
				bodyContext.append(getTempTable(lth, row,rowHeight, temp, columnNum, style_name,tt[1],"font-weight:normal;font-size:"+aFontSize+"pt;Font-family:"+ResourceFactory.getProperty("hmuster.label.fontSt")+";",wordWarp));								
				bodyContext.append(context+ " </div></td></tr></table> \n ");
			}
			row++;
		}
	}
	
	public void count(double[] count,String[] field,String context,String[] temp)
	{

		if (field != null && field.length > 0) {
			for (int b = 0; b < field.length; b++) {
				if (field[b].equals("C" + temp[0])) {
					if (context != null&&!"&nbsp;".equals(context)&&context.length()>0){
						BigDecimal b1 = new BigDecimal(Double.toString(count[b]));
						BigDecimal b2 = new BigDecimal(Double.toString(Double.parseDouble(context)));
						count[b] = b1.add(b2).doubleValue();
					}
					break;
				}
			}
		}
	}
	
	
	
	/**
	 * 计算页累计（不为分组查询时）
	 * @param totalCount
	 * @param isGroupTerm  表头条件中是否包含分组指标
	 * @param isGroupPoint 是否按分组查询 1：是
	 * @param rows  每页行数
	 * @param currentPage 当前页
	 * @return
	 */
	public double[] totalCounts(double[] totalCount,String[] fields,boolean isGroupTerm,String isGroupPoint,int rows,int currentPage,String tablename,String sql)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer sql_ext=new StringBuffer("");
		try
		{
			if(fields.length>0&&!isGroupTerm&&(isGroupPoint== null|| !"1".equals(isGroupPoint)))
			{
				for(int i=0;i<fields.length;i++)
				{
                    String fld = "";
				    boolean RoundBeforeAggregate = true;
				    int slope=0;
			        String[] temp = getCell(fields[i]);
			        if (temp != null) {
			            String extendattr=temp[20]==null?"":temp[20].toUpperCase();
			            if(extendattr.indexOf("ROUNDBEFOREAGGREGATE")!=-1) {
                            RoundBeforeAggregate = "1".equals(extendattr.substring(extendattr.indexOf("<ROUNDBEFOREAGGREGATE>")+
                                    "<ROUNDBEFOREAGGREGATE>".length(),extendattr.indexOf("</ROUNDBEFOREAGGREGATE>")));
                        }
			            slope=Integer.parseInt(temp[6]);
			        }
                    if(RoundBeforeAggregate) {
                        fld = "sum("+Sql_switcher.round(fields[i], slope)+") "+fields[i];
                    } else {
                        fld = "sum("+fields[i]+") "+fields[i];
                    }
					sql_ext.append(","+fld);
				}
				String asql="select "+sql_ext.substring(1)+" from "+tablename+" where recidx<="+(rows*(currentPage));
				rowSet=dao.search(asql);
				if(rowSet.next())
				{
					for (int b = 0; b < fields.length; b++) {
						String a_context = rowSet.getString(fields[b]);
						if (a_context != null){
							//BigDecimal b1 = new BigDecimal(Double.toString(totalCount[b]));
							BigDecimal b2 = new BigDecimal(Double.toString(Double.parseDouble(a_context)));
							totalCount[b] =b2.doubleValue();
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return totalCount;
	}
	
	/**
	 * 计算总计
	 */
    public double[] totalCountsZj(double[] totalCount,String[] fields, String tableName, ArrayList bottomnList) {
        return totalCounts3(totalCount,fields, tableName, bottomnList);
    }
	
	
	/**
	 * 计算总计
	 * @param totalCount
	 * @param fields
	 * @return
	 */
	private double[] totalCounts3(double[] totalCount,String[] fields, String tableName, ArrayList bottomnList)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer sql_ext=new StringBuffer("");
		try
		{
				String asql="";
				if(needCalcAfterSum(bottomnList)){
					// 直接从临时表取值
					StringBuffer sumflds=new StringBuffer("");
					getTotalSumFlds(bottomnList, infor_Flag, tableName, "0", tabid, sumflds);
					asql="select "+sumflds.substring(1)+" from " + getCalcAfterSumTmpTable() + " " + tableName;
				}else {
                    asql="select "+getGroupSql(bottomnList, infor_Flag, tableName,"0",tabid).substring(1);
                }

				/* 权限控制 */
				asql+=this.privConditionStr;
				if("stipend".equals(infor_Flag)&&this.a0100.length()>0)
				{
					if(asql.substring(asql.toUpperCase().lastIndexOf("FROM")).toLowerCase().indexOf("where")>0){
						asql+=" and  a0100='"+this.a0100+"' ";
					}else{
						asql+=" where a0100='"+this.a0100+"'";
					}
				}
				rowSet=dao.search(asql);
				if(rowSet.next())
				{
					/* 处理解决3053号问题时同步发现的空指针异常 xiaoyun 2014-7-16 start */
					if(fields != null) {
						for (int b = 0; b < fields.length; b++) {
							String a_context = rowSet.getString(fields[b]);
							if (a_context != null){
								BigDecimal b2 = new BigDecimal(Double.toString(Double.parseDouble(a_context)));
								totalCount[b] =b2.doubleValue();
							}
						}
					}
					/* 处理解决3053号问题时同步发现的空指针异常 xiaoyun 2014-7-16 end */
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return totalCount;
	}
	
	/**
	 * 计算分组合计
	 * @param totalCount
	 * @return
	 */
	public double[] calcGroupSum(double[] vals,String[] fields,String groupn, String groupn2, String tablename)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer sql_ext=new StringBuffer("");
		try
		{
			if(fields.length>0)
			{
				for(int i=0;i<fields.length;i++)
				{
				    String fld = "";
				    if(isRecordCountCell(fields[i])) {
				        if(isCalcRecCountByPerson()) {
                            fld = "Count(Distinct NBASE"+Sql_switcher.concat()+"A0100) " + fields[i];
                        } else {
                            fld = "Count(*) " + fields[i];
                        }
				    }
				    else {
                        fld = getGroupSumExpr(fields[i]);//"sum("+fields[i]+") "+fields[i];
                    }
				    sql_ext.append(","+fld);
				}
				String cond="";
				if(groupn!=null&&groupn.length()>0) {
                    cond="groupn='"+groupn+"'";
                } else {
                    cond="groupn is null";
                }
				if(groupn2!=null){
					if(groupn2.length()>0) {
                        cond+=" and groupn2='"+groupn2+"'";
                    } else {
                        cond+=" and groupn2 is null";
                    }
				}
				String asql="select "+sql_ext.substring(1)+" from "+tablename+" where "+cond;
				rowSet=dao.search(asql);
				if(rowSet.next())
				{
					for (int b = 0; b < fields.length; b++) {
						String a_context = rowSet.getString(fields[b]);
						boolean flag=false;
						if((groupn==null|| "".equals(groupn))&&a_context != null&& "0".equals(a_context)){//当分组指标为空时  30118 薪资自定义高级花名册分组统计记录数列合计取不到数据
							flag=true;
						}
						if (a_context != null&&!flag){//分组指标为空分组汇总sql查询为0时不走下面程序
							BigDecimal b2 = new BigDecimal(Double.toString(Double.parseDouble(a_context)));
							vals[b] =b2.doubleValue();
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return vals;
	}
	
	/**
	 * 是否组内记录数
	 * @return
	 */
	private boolean isRecordCountCell(String colNo) {
	    if(bottomColumns == null) {
            return false;
        }
        for (Iterator t = bottomColumns.iterator(); t.hasNext();) {
	        String[] temp = (String[]) t.next();
	        if (("C" + temp[0]).equals(colNo)) {
	            return "R".equals(temp[7]);
	        }
        }
	    return false;
	}
	
    private static int MOD_GZANALYZE    =  6;  // 工资分析名册
    private static int MOD_BXANALYZE    =  8;  // 保险分析花名册
    private static int MOD_BXXZMUSTER   = 11;  // 保险自定义花名册
    private static int MOD_GZTAOMUSTER  = 14;  // 工资自定义报表
    
    /**
     * 工资分析等分组记录数为人数
     * @return
     */
	private boolean isCalcRecCountByPerson() {
	    if("salary".equals(modelFlag)) {
            return true;
        }
	    int[] mods = {MOD_GZANALYZE, MOD_BXANALYZE, MOD_GZTAOMUSTER, MOD_BXXZMUSTER};
	    for(int i=0; i<mods.length; i++){
	        if(String.valueOf(mods[i]).equals(modelFlag)) {
                return true;
            }
	    }
	    return false;
	}
	
    private String[] getCell(String colNo) {
        if(bottomColumns == null) {
            return null;
        }
        for (Iterator t = bottomColumns.iterator(); t.hasNext();) {
            String[] temp = (String[]) t.next();
            if (("C" + temp[0]).equals(colNo)) {
                return temp;
            }
        }
        return null;
    }
    
	private String getGroupSumExpr(String colNo) {
        String fld = "sum("+colNo+") "+colNo;
        String[] temp = getCell(colNo);
	    if (temp == null) {
            return fld;
        }
	    if("M".equalsIgnoreCase(temp[9])) {
            return "null as " + colNo;
        }
        String extendattr=temp[20]==null?"":temp[20].toUpperCase();
        if(extendattr.indexOf("GROUPSUM")!=-1)
        {
            boolean RoundBeforeAggregate = true;
            if(extendattr.indexOf("ROUNDBEFOREAGGREGATE")!=-1) {
                RoundBeforeAggregate = "1".equals(extendattr.substring(extendattr.indexOf("<ROUNDBEFOREAGGREGATE>")+
                        "<ROUNDBEFOREAGGREGATE>".length(),extendattr.indexOf("</ROUNDBEFOREAGGREGATE>")));
            }
            String groupSum = extendattr.substring(extendattr.indexOf("<GROUPSUM>")+"<GROUPSUM>".length(),extendattr.indexOf("</GROUPSUM>"));
            // 0求和/1平均/2最大/3最小
            if(("0".equals(groupSum) || "1".equals(groupSum)) && !"N".equalsIgnoreCase(temp[9]))
            {
                return "max(C"+temp[0]+") C"+temp[0];
            }
 
            int slope=Integer.parseInt(temp[6]);
            if("0".equals(groupSum))
            {
                if(RoundBeforeAggregate) {
                    fld = "sum("+Sql_switcher.round("C"+temp[0], slope)+") C"+temp[0];
                } else {
                    fld = "sum(C"+temp[0]+") C"+temp[0];
                }
            }
            else if("1".equals(groupSum))
            {
                if(RoundBeforeAggregate) {
                    fld = "avg("+Sql_switcher.round("C"+temp[0], slope)+") C"+temp[0];
                } else {
                    fld = "avg(C"+temp[0]+") C"+temp[0];
                }
            }
            else if("2".equals(groupSum))
            {
                fld = "max(C"+temp[0]+") C"+temp[0];
            }
            else if("3".equals(groupSum))
            {
                fld = "min(C"+temp[0]+") C"+temp[0];
            }
        }
        return fld;
	}
	
	//数组清零
	public double[] initCount(String[] info)
	{
		double[] count=null;
		if (info != null && info.length > 0) {
			count = new double[info.length];
			for (int a = 0; a < info.length; a++) {
				count[a] = 0;
			}
		}
		return count;
	}
	//数组清零
	public double[] initCountValue(String tablename,String[] info,String orgname){
		
		double[] count=null;
		if (info != null && info.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("select ");
			count = new double[info.length];
			for (int a = 0; a < info.length; a++) {
				buf.append("sum("+count[a]+") as "+count[a]);
				if(a+1<info.length){
					buf.append(",");
				}
			}
			buf.append(" from "+tablename+" where GroupN='"+orgname+"'");
		}
		
		return count;
	}
	/***
	 * 单元格设置居左或者居右时设置font边距
	 * @param context_align
	 * @return
	 */
	private String getFontMargin(String context_align) {
		if("left".equals(context_align)) {
            return ";margin-left:3px;";
        } else if("right".equals(context_align)) {
            return ";margin-right:3px;";
        } else {
            return "";
        }
	}
	
	public String getTempTable(float[] lth, int n, int rowHeight,
			String[] temp, int columnNum, String style_name,
			String context_align,String font_style,boolean wordWrap) {
		StringBuffer bodyContext = new StringBuffer("");
		bodyContext
				.append(" <table    cellspacing=\"0\"  align=\"center\" cellpadding=\"1\"");
		bodyContext.append(" class=\"ListTable5\" ");
		bodyContext.append(" style=\"position:absolute;");
		//bodyContext.append("border:1px solid black;border-collapse:collapse;");
		// FIXME 下面两行代码解决内容多撑大单元格的问题，但是有个副作用：强制显示上边线。
       // bodyContext.append("BORDER-BOTTOM: medium none; BORDER-LEFT: medium none; BORDER-RIGHT: medium none; BORDER-TOP: 1px solid black;");
	    //bodyContext.append("TABLE-LAYOUT: fixed;");
		bodyContext.append("top:");
		bodyContext.append((lth[1] + lth[2] - 1) + (n - 1) * rowHeight+deltaTop);
		bodyContext.append("px;left:");
		float a_left = Float.parseFloat(temp[2].trim());
		float a_width = Float.parseFloat(temp[4].trim());
		if (columnNum != 1) {
			a_left--;
			//a_width++;
		}
		
		bodyContext.append(String.valueOf(a_left));
		bodyContext.append("px;width:");
		bodyContext.append(a_width);
		bodyContext.append("px;height:");
		bodyContext.append(rowHeight );
		bodyContext.append("px;z-index: "+columnNum);
		bodyContext.append(";font:"+font_style);		
		bodyContext.append(" \">");
		bodyContext.append(" <tr valign=\"middle\" align=\"center\"> \n ");
		bodyContext.append(" <td ");
		bodyContext.append(" class=\"" + style_name + "\" style=\" "+font_style+";");
		bodyContext.append("height:"+rowHeight +"px; width:"+a_width+"px; padding-top:0;padding-bottom:0;");
		if("M".equals(temp[9]))  // 备注型
        {
            bodyContext.append("word-wrap:break-word;");  // 连续字母、数字中间允许换行
        }
		bodyContext.append("\" ");
		bodyContext.append(" valign=\"middle\" align=\"");
		bodyContext.append(context_align);
		bodyContext.append("\">");
		bodyContext.append("<div style=\"padding-top:1px;position:relative;overflow:hidden;text-align:"+context_align+";width:"+(a_width-1.5)+"px;"+(wordWrap?"word-wrap:break-word;":"")+"max-height:"+(rowHeight-1.5)+"px;\" valign=\"middle\" align=\""+context_align+"\">");
		return bodyContext.toString();
	}
	public String getTempTableHe(float[] lth, int topheight, int rowHeight,
			String[] temp, int columnNum, String style_name,
			String context_align,String font_style) {
		StringBuffer bodyContext = new StringBuffer("");
		bodyContext.append(" <table   cellspacing=\"0\"  align=\"center\" cellpadding=\"1\"");
		bodyContext.append(" class=\"ListTable5\" ");
		bodyContext.append(" style=\"position:absolute;");
		if(rowHeight==0) {
            bodyContext.append("display:none;");
        }
		bodyContext.append("top:").append(((lth[1] + lth[2] - 1) + topheight+deltaTop-this.getTextDataHeight()));
		bodyContext.append("px;left:");
		float a_left = Float.parseFloat(temp[2].trim());
		float a_width = Float.parseFloat(temp[4].trim());
		if (columnNum != 1) {
			a_left--;
			a_width++;
		}
		bodyContext.append(String.valueOf(a_left));
		bodyContext.append("px;width:");
		bodyContext.append(a_width);
		bodyContext.append("px;height:");
		bodyContext.append(rowHeight);
		bodyContext.append("px;z-index: "+columnNum);
		bodyContext.append(";font:"+font_style);		
		bodyContext.append(" \"> ");
		bodyContext.append(" <tr valign=\"middle\" align=\"center\"> ");
		bodyContext.append(" <td ");
		bodyContext.append(" class=\"" + style_name + "\";");
		bodyContext.append(" valign=\"middle\" style=\"width:"+a_width+"px;height:"+(rowHeight)+"px;padding-top:0;padding-bottom:0;\"align=\"");
		bodyContext.append(context_align);
		bodyContext.append("\">");
		if(musterCellMap.get(temp[0]).get("nWordWrap")!=null&&"1".equals(musterCellMap.get(temp[0]).get("nWordWrap"))) {//自动换行设置  bug 53780
			bodyContext.append("<div style=\"position:relative;text-align:center;width:"+(a_width-1.5)+"px;word-wrap:break-word; max-height:"+(rowHeight-1)+"px;\" valign=\"middle\" align=\""+context_align+"\">");
		}else {
			bodyContext.append("<div style=\"position:relative;text-align:center;width:"+(a_width-1.5)+"px; overflow:hidden; line-height:"+(rowHeight-1)+"px;height:"+(rowHeight-1)+"px;\" valign=\"middle\" align=\""+context_align+"\">");
		}
		return bodyContext.toString();
	}
	public int  getFitFontSize1(int fontSize,float width,float height,String context)
	{
		int afontSize=fontSize;
		String[] temps=context.split("<Br>");
		int constant=7;
		if(temps.length>2&&temps.length<5) {
            constant=6;
        } else if(temps.length>=5&&temps.length<8) {
            constant=5;
        } else if(temps.length>=8) {
            constant=2;
        }
		
		while(true)
		{
			if((afontSize+constant)*temps.length<=height) {
                break;
            } else {
                afontSize--;
            }
		}
		
		int maxNum=0;
		for(int i=0;i<temps.length;i++)
		{
			int a_max=0;
			if(temps[i].getBytes().length%2==1) {
                a_max=temps[i].getBytes().length/2+1;
            } else {
                a_max=temps[i].getBytes().length/2;
            }
			if(a_max>maxNum)
			{
					maxNum=a_max;
			}
		}
		//constant=8;
		//if(maxNum>2)
		constant=5;
        if(maxNum>=4&&maxNum<8) {
            constant=3;
        } else if(maxNum>=8) {
            constant=2;
        }
		
		while(true)
		{
			if((afontSize+constant)*maxNum<=width) {
                break;
            } else {
                afontSize--;
            }
		}
		return afontSize;
	}

	/**
	 * 生成页小计html
	 * 
	 * @param bottomnList
	 *            列集合
	 * @param field
	 *            需小计的列
	 * @param zeroPrint
	 *            是否零打印
	 * @param pageCount
	 *            页小计值
	 * @param rowHeight
	 *            行高
	 * @return String
	 */
	public String getPageCountRows(float lth[], int n, ArrayList bottomnList,
			String[] field, String zeroPrint, double[] pageCount,
			int rowHeight, int fontSize, String resourceProperty,String printGrid,int arrindex) {
		StringBuffer bodyContext = new StringBuffer("");
		int columnNum = 0;
		int flag=0;
		boolean isResource=true;
		for (Iterator t = bottomnList.iterator(); t.hasNext();) {
			columnNum++;
			String[] temp = (String[]) t.next();
			String gridno=temp[0];
			if(temp[20]!=null&& "1".equals(temp[20])) {
                continue;
            }
			String[] a_temp = transAlign(Integer.parseInt(temp[13]));
			String aAlign =a_temp[1];
			
			String context = "&nbsp;";
			//if (temp[7].equals("S")) {
			//if(column_num==0){
				//column_num++;
				
			/**
			 * select GridNo,Hz,Rleft,RTop+");
		       this.t_space
		       ,RWidth,RHeight,Slope,Flag,noperation,Field_Type,fontName,
		       fontEffect,fontSize,align,L,T,R,B,SETNAME,Field_name,nhide,
			*/
			if(this.resourceCloumn!=-1&&!"N".equalsIgnoreCase(temp[9])&&!"R".equalsIgnoreCase(temp[7]))  // 记录数格
			{
				if(this.resourceCloumn!=-2)
				{
			    	if(this.resourceCloumn==Integer.parseInt(temp[0]))
			    	{
				    	if(resourceProperty.indexOf(".")!=-1) {
                            context=ResourceFactory.getProperty(resourceProperty);
                        } else {
                            context=resourceProperty;
                        }
		    		}
				}else if(flag==0&&!"N".equalsIgnoreCase(temp[9])){
					if(resourceProperty.indexOf(".")!=-1) {
                        context=ResourceFactory.getProperty(resourceProperty);
                    } else {
                        context=resourceProperty;
                    }
					flag++;
				}
			}else {
				for (int b = 0; b < field.length; b++) {
					if (field[b].equals("C" + temp[0])) {
						if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
                        {
                            if (pageCount[b] == 0) {
                                context = round("0", Integer.parseInt(temp[6]));//页小计改为0 取消为空设置  30131	首都机场集团高级花名册和登记表显示异常问题
                            } else {
                                context = round(String.valueOf(pageCount[b]), Integer.parseInt(temp[6]));
                            }
                        } else {
                            context = round(String.valueOf(pageCount[b]),Integer.parseInt(temp[6]));
                        }
						break;
					}
				}
			}
			if("1".equals(temp[19])) {
                context="&nbsp;";
            }
			if(!hasFieldReadPriv(temp,arrindex==18)) {
                context="";
            }
			boolean wordWarp=false;
	    	if(musterCellMap!=null&&musterCellMap.get(gridno)!=null) {
	    		if(musterCellMap.get(gridno).get("nWordWrap")!=null&&"1".equals(musterCellMap.get(gridno).get("nWordWrap"))) {
	    			wordWarp=true;
	    		}
	    	}
			String style_name = getStyleName2(temp,printGrid,2); // 处理虚线 L,T,R,B,
			bodyContext.append(getTempTable(lth, n, rowHeight, temp, columnNum,
					style_name, aAlign,"font-weight:normal;font-size:"+fontSize+"pt;"
							+ "Font-family:"+ResourceFactory.getProperty("gz.gz_acounting.m.font")+";",wordWarp));
			bodyContext.append(context);
			bodyContext.append("</div></td></tr></table> \n ");

		}
		return bodyContext.toString();
	}

	/**
	 * 生成页小计html
	 * 
	 * @param bottomnList
	 *            列集合
	 * @param field
	 *            需小计的列
	 * @param zeroPrint
	 *            是否零打印
	 * @param pageCount
	 *            页小计值
	 * @param rowHeight
	 *            行高
	 * @return String
	 */
	public String getPageCountRows(String columnLine, float pix,
			float h_tableHeight, float h_tableWidth, ArrayList headerList,
			String column, int n, ArrayList bottomnList, String[] field,
			String zeroPrint, double[] pageCount, String resourceProperty) {
		StringBuffer bodyContext = new StringBuffer("");
		float hr_pix = 0; // 分隔线的坐标
		float hr_width=0; //分隔线宽度
		float hr_left=0;//分隔线离左边窗口距离
		boolean isFirst = true; // 表的第一单元格
		int hr_i=0;
		boolean isResource=true;
		int flag=0;
		for (Iterator t = headerList.iterator(); t.hasNext();) {

			String[] temp = (String[]) t.next();
			String gridno=temp[0];
			if(temp[20]!=null&& "1".equals(temp[20])) {
                continue;
            }
			if(this.getTextFormatMap().size()>0&&this.getTextFormatMap().get(temp[0])==null&&(("1".equals(column)&& "1".equals(this.dataarea)))) {
                continue;
            }
			int type = 1;
			String border = "1";
			int align = Integer.parseInt(temp[13]);
			String fontName = temp[10];
			String fontSize = temp[12];
			String fontStyle = temp[11];

			float topPix = Float.parseFloat(temp[3]);
			String left = temp[2];
			if ("1".equals(column)) {
				if("0".equals(dataarea)) {
                    topPix = Float.parseFloat(temp[3]) + (n - 1)* h_tableHeight;
                } else{
					topPix = Float.parseFloat(temp[3]) + n* (h_tableHeight)-2;
				}
				//topPix = Float.parseFloat(temp[3]) + (n - 1) * h_tableHeight;
				topPix += pix * (n - 1);

				if (isFirst) {
                    hr_pix = topPix+deltaTop;
                }

			} else {

				left = String.valueOf(Float.parseFloat(left) + (n - 1)* h_tableWidth + pix * (n - 1));

			}

			String width = temp[4];
			String aheight = h_tableHeight+"";//temp[5];
			String context = "&nbsp;";
			if (temp[7]==null|| "H".equals(temp[7])|| "".equals(temp[7])) {
				context = temp[1];
				if (context != null && context.indexOf("`") != -1) {
                    context = context.replaceAll("`", "<br>");
                }
			}
			else{
		    	if(this.resourceCloumn!=-1&&this.resourceCloumn!=-2&&this.resourceCloumn==Integer.parseInt(temp[0]))
		    	{
				
				    if(resourceProperty.indexOf(".")!=-1) {
                        context=ResourceFactory.getProperty(resourceProperty);
                    } else {
                        context=resourceProperty;
                    }
		    	}
	    		else if(this.resourceCloumn==-2&&flag==0&&!"N".equalsIgnoreCase(temp[9]))
	    		{
		    		if(resourceProperty.indexOf(".")!=-1) {
                        context=ResourceFactory.getProperty(resourceProperty);
                    } else {
                        context=resourceProperty;
                    }
			    	flag++;
		    	}else {
		    		for (int b = 0; b < field.length; b++) {
		    			if (field[b].equals("C" + temp[0])) {
				    		if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
				    		{
					    		if (pageCount[b] == 0) {
                                    context = "&nbsp;";
                                } else {
                                    context = round(String.valueOf(pageCount[b]),
                                            Integer.parseInt(temp[6]));
                                }
					    	} else {
                                context = round(String.valueOf(pageCount[b]),
                                        Integer.parseInt(temp[6]));
                            }
				    		break;
			    		}
	    			}
	    		}
			}
			if("1".equals(temp[19])) {
                context="&nbsp;";
            }
//			如果用户对该指标无权限，则不予显示数据
			if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&temp[18]!=null&&temp[18].trim().length()>0)
			{
				if(!"nbase".equalsIgnoreCase(temp[18])&&!"a0100".equalsIgnoreCase(temp[18])&&"0".equalsIgnoreCase(this.userView.analyseFieldPriv(temp[18])))
				{
					if(temp[17]!=null&&temp[17].toUpperCase().startsWith("V_EMP_"))
					{
						
					}
					else
					{
						context="";
					}
				}
			}
			boolean wordWarp=false;
	    	if(musterCellMap!=null&&musterCellMap.get(gridno)!=null) {
	    		if(musterCellMap.get(gridno).get("nWordWrap")!=null&&"1".equals(musterCellMap.get(gridno).get("nWordWrap"))) {
	    			wordWarp=true;
	    		}
	    	}
			// 处理虚线 L,T,R,B,
			String style_name = getStyleName2(temp,this.printGrind,3);
			bodyContext.append(executeAbsoluteTable(type, align, fontName,
					fontSize, fontStyle, border, String.valueOf(topPix), left,
					width, aheight, context, style_name,wordWarp));
//			hr_width+=Float.parseFloat(width);
			
			if(hr_i<1) {
                hr_left=Float.parseFloat(left);
            }
			hr_width=Float.parseFloat(left)+Float.parseFloat(width)-hr_left;
			hr_i++;
			isFirst = false;
		}
		if (columnLine != null && "1".equals(columnLine)) {
			if (n != 1 && "1".equals(column)) {
				bodyContext.append(" \n <hr style=\"position:absolute;top:"
						+ hr_pix + "px;left:"+hr_left+"px;width:"+hr_width+"px;height:\"> ");
			}
		}

		return bodyContext.toString();
	}
	public String createTitlePic(String tabid,String gridno) throws Exception {
		File tempFile = null;
		String filename = "";
		ServletUtilities.createTempDir();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		InputStream in = null;
		java.io.FileOutputStream fout = null;
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select extendattr,content from muster_title where tabid="+tabid+" and gridno="+gridno);
			rowSet=dao.search(strsql.toString());
			if (rowSet.next()) {
				String extendattr=Sql_switcher.readMemo(rowSet,"extendattr").toUpperCase();
				String ext="";
				if(extendattr.indexOf("<EXT>")!=-1)
				{
					ext=extendattr.substring(extendattr.indexOf("<EXT>")+5, extendattr.indexOf("</EXT>"));
				}
				if(ext==null|| "".equals(ext))
				{
					return filename;
				}
				
				tempFile = File.createTempFile(ServletUtilities.tempFilePrefix,ext, new File(System.getProperty("java.io.tmpdir")));
				in = rowSet.getBinaryStream("content");
				fout = new java.io.FileOutputStream(tempFile);
				int len;
				byte buf[] = new byte[1024];

				while ((len = in.read(buf, 0, 1024)) != -1) {
					fout.write(buf, 0, len);
				}
				filename = tempFile.getName();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeIoResource(fout);//资源释放  jingq upd 2014.12.29
			PubFunc.closeIoResource(in);
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return filename;
	}

	/**
	 * 生成标题html & 总页数 & 翻页标签放的位置
	 * 
	 * @param titelTopList
	 *            表头上部的标题信息列表
	 * @param titleBottomnList
	 *            表头下部的标题信息列表
	 * @param tableBodyHeight
	 *            表体高度 （像素）
	 * @param paperRows
	 *            每页行数
	 * @param height
	 *            每行的高度
	 * @author dengc
	 * @return ArrayList list[0]:上标题内容 list[1]:下标题内容 list[2]:总页数
	 *         list[3]:翻页标签放的位置 created : 2006/03/31
	 */
	private ArrayList getTitleHtml(boolean isGroupTerm, String column,
			int height, int factRows, ArrayList titelList, String userName,
			String currentPage, int paperRows, String infor_Flag,
			String tempTable, String isGroupPoint, String groupV,
			float tableHeaderTop, float tableHeaderHeight, String history,
			String year, String month, String counts,
			String dbpre,int totalPage,String tabid) throws GeneralException {
		boolean isChangeFlag=this.getSetChangeFlag(tabid);//是否包含年月子集指标
		ArrayList list = new ArrayList();
		StringBuffer topInfo = new StringBuffer(" ");
		StringBuffer bottomnInfo = new StringBuffer(" ");
		float top = 0; // 标题最底端的坐标
		int count = -1; // 总行数
		int pages = 0; // 总页数
		/* 标识：3045 薪资分析报表标题错误 xiaoyun 2014-7-16 start */
		String extendattr = ""; //表头或表尾格式说明 
		/* 标识：3045 薪资分析报表标题错误 xiaoyun 2014-7-16 end */
		try {
			/* 生成表头上部的标题信息 */
			if (titelList.size() >= 1) {
				//for (Iterator t = titelList.iterator(); t.hasNext();) {
				for (int i = 0; i < titelList.size(); i++) {	
					String[] temp = (String[]) titelList.get(i);
					String context = "";
					int z_index = 20;  // TODO 需要置顶，应该是最大z-index
					switch (Integer.parseInt(temp[16])) {
					case 0:
						context = temp[8].toString(); // 文本描述
						if(context!=null) {
                            context = context.replaceAll("\\r\\n", "<br>");
                        }
						break;
					case 1:
						context=this.getCreateTableDate(temp[18]);
						break;
					case 2:
						Date dd = new Date(); // 制表时间
						context = ResourceFactory.getProperty("hmuster.label.createTableTime")
								+ DateFormat.getTimeInstance(DateFormat.MEDIUM,Locale.CHINA).format(dd);
						break;
					case 3:
						context=this.getCreateTablePerson(userName, temp[18]);
						break;
					case 4: // 总页数
						pages = totalPage;
						context = ResourceFactory.getProperty("hmuster.label.total")
								+ pages
								+ ResourceFactory.getProperty("hmuster.label.paper");
						break;
					case 5:  // 页码
						context = ResourceFactory.getProperty("hmuster.label.d")
								+ currentPage
								+ ResourceFactory.getProperty("hmuster.label.paper"); 
						break;
					case 12: //-#-
						context = "-"
						+ currentPage
						+"-"; 
						break;
					case 6:
						if (count == -1) // 总行数
						{
							count=getCount(infor_Flag,tempTable,isGroupTerm);
						}
						context =  count+"";
						break;
					case 7: // 分组指标
						if (isGroupTerm&& "1".equals(isGroupNoPage) || isGroupPoint == null|| "0".equals(isGroupPoint)) {//liuy 2015-3-2 7408：员工管理/花名册/高级花名册165，有两个分组指标时，标题为第1分组指标，出不来数据。按第1分组指标分组分页功能不灵。
							context = "&nbsp;";
						} else {
							context = groupV;
						}
						break;
					case 8: // ()年()月	
						context=getYearMonth(infor_Flag,history,tempTable,year,month,1,counts,isChangeFlag);
						topDateTitleMap.put(i, context);
						break;
					case 9: // ()年()月()次
						context=getYearMonth(infor_Flag,history,tempTable,year,month,2,counts,isChangeFlag);
						topDateTitleMap.put(i, context);
						break;
					case 10:   //标题变量
						if(temp[18]!=null&&temp[18].trim().length()>1)
						{
							context=getTitleVarValue(temp[18],infor_Flag,tempTable,year,month,isGroupTerm);
						}
						else {
                            context="&nbsp;";
                        }
						break;
					case 14:   //组内记录
						if (!isGroupTerm&&isGroupPoint!= null&& "1".equals(isGroupPoint)&&isGroupNoPage!=null&& "0".equals(isGroupNoPage)) {
							if(temp[18]!=null&&temp[18].trim().length()>1){
								context=getTitleCount(this.groupNcode,tempTable);
							}else {
                                context="&nbsp;";
                            }
						}else{
							context="&nbsp;";
						}
						break;
					case 13://图片
						String filename=this.createTitlePic(tabid, temp[7]);
						String extarr=temp[18];
						String transparent="false";
						z_index = 1;  // 图片置底
						if(extarr.toUpperCase().indexOf("TRANSPARENT")!=-1)
						{
							transparent=extarr.toUpperCase().substring(extarr.toUpperCase().indexOf("<TRANSPARENT>")+13,extarr.toUpperCase().indexOf("</TRANSPARENT>"));
						}
						
						if(!"".equals(filename))
						{
							if("true".equalsIgnoreCase(transparent))
							{
								context="<span style=\"width:"+temp[10]+"px;height:"+temp[12]+"px;display:inline-block;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader";
								context+="(src=\"/servlet/vfsservlet?fromjavafolder=true&fileid="+ PubFunc.encrypt(filename)+ "\",sizingMethod=scale,enable=true);\"></span>";
							}
							else
							{
						    	context = "<img src=\"/servlet/vfsservlet?fromjavafolder=true&fileid="+ PubFunc.encrypt(filename)+ "\" height=\""+temp[12]+"\" width=\""+temp[10]+"\">";//border=1
							}
						}
						break;
					case 16:// 审批意见
					    context = getGzTaoSpProcess();
                        if(context!=null) {
                            context = context.replaceAll("\\r\\n", "<br>");
                        }
					    break;
					}
					context=context!=null?context:"";
					/* 标识：3045 薪资分析报表标题错误 xiaoyun 2014-7-16 start */
					extendattr = temp[18];
					//liuy 2015-2-12 2758：薪资分析报表标题错误 start
					boolean lastpageonly = false; // 表尾是否只在最后一页显示True-是，False-否
					if(extendattr.contains("<lastpageonly>")) {
						int start = extendattr.indexOf("<lastpageonly>");							
						int end = extendattr.indexOf("</lastpageonly>");
						lastpageonly = "true".equalsIgnoreCase(extendattr.substring(start+14, end));
					}
					/* 标识：3045 薪资分析报表标题错误 xiaoyun 2014-7-16 end */
					if (Float.parseFloat(temp[11]) < tableHeaderTop){ // 表头上部标题
						if(!lastpageonly||(lastpageonly&&totalPage == Integer.parseInt(currentPage))){
							topInfo.append(executeAbsoluteTable1(2, 6, temp[14],
									temp[13], temp[15], "0", temp[11], temp[9], "",
									temp[12], context, " ", z_index));
						}
					}else if (Float.parseFloat(temp[11]) > (tableHeaderTop + tableHeaderHeight)){ // 表头下部标题
						if(!lastpageonly || (lastpageonly && totalPage == Integer.parseInt(currentPage))) {
							context=context.replaceAll("  ","&nbsp;");
							bottomnInfo.append(executeAbsoluteTable1(2, 6,
									temp[14], temp[13], temp[15], "0", String.valueOf(Float.parseFloat(temp[11])+ height * factRows+30),//底部标题位置固定最后一行下方
									temp[9], "", temp[12], context, " ", z_index));
							if (!"2".equals(column)) {
								if (top < Float.parseFloat(temp[11]) + height
										* factRows) {
                                    top = Float.parseFloat(temp[11]) + height* factRows + 20;
                                }
							} else {
								if (top < Float.parseFloat(temp[11]) + height) {
                                    top = Float.parseFloat(temp[11]) + height + 20;
                                }
							}
						}
					}else {
                        topInfo.append(executeAbsoluteTable1(2, 6, temp[14],
                                temp[13], temp[15], "0", temp[11], temp[9], "700",
                                temp[12], context, " ", z_index));
                    }
					//liuy 2015-2-12 end
				}
			}

			if (pages == 0) {
				pages =totalPage;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		if (top == 0) // 如果没有下标题
		{
			if (!"2".equals(column)||this.tableTypeFlag)//纵分下标题设置位置
            {
                top = tableHeaderTop + tableHeaderHeight + height * factRows;
            } else {
                top = tableHeaderTop + tableHeaderHeight + height;
            }
		}
		
		list.add(topInfo.toString()); // 上标题
		list.add(bottomnInfo.toString()); // 下标题
		list.add(String.valueOf(pages)); // 总页数
		list.add(String.valueOf(top)); // 翻页标签放的位置
		return list;
	}
	
	/**
	 * 格式化审批意见
	 * 
	 * @param appProcess
	 * @return
	 */
	private String formatAppProcess(String appProcess) {
/*
 审批意见字段内容：
　　　报批: 2013.11.26 15:47
　　　  超级用户组 su 报批给 ggg
　　　驳回: 2013.11.26 15:48
　　　  超级用户组 ggg 驳回审批。
　　　  驳回原因：数据有误
　　　报批: 2013.11.26 15:49
　　　  超级用户组 su 报批给 ggg
　　　批准: 2013.11.26 15:51
　　　  超级用户组 ggg
　　　  同意，审批通过。

　根据“报批”、“驳回”、“批准”等关键字分析审批意见内容，
  把其中的时间、审批人、审批意见提取出来并组合成新格式显示：
　　　2013.11.26 11:57 超级用户组 su 报批给 ggg
　　　2013.11.26 11:58 超级用户组 ggg 批准 同意，审批通过。

*/
	    String s="";
	    if(appProcess==null) {
            return "";
        }
	    s = appProcess.trim().replaceAll("\\n", "\r\n");
	    String[] sl = s.split("\\r\\n");
	    s = "";
	    String l = "";
	    int lineNum = 0;
	    String key = "";
	    for (int i=0; i<sl.length; i++) {
	        if (sl[i].trim().startsWith("报批:") || sl[i].trim().startsWith("驳回:") || 
	            sl[i].trim().startsWith("批准:")) {
	          if (l.trim().length() > 0) {
                  s += l.trim() + "\r\n";
              }
	          lineNum = 1;
	          l = sl[i].trim().substring(4).trim() + " ";
	          key = sl[i].trim().substring(0, 2);
	        }
	        else {
	          lineNum++;
	          l = l + sl[i].trim();
	          if ("批准".equals(key) && (lineNum == 2)) {
                  l += " 批准 ";
              }
	        };
	    }
	    if (l.trim().length() > 0) {
            s += l.trim() + "\r\n";
        }
	    
	    return s.trim();
	}
	
	/**
	 * 返回审批意见
	 * @return
	 */
	public String getGzTaoSpProcess() {
	    String s = "";
	    if("salary".equals(infor_Flag)&&salaryDataTable.length()>0&&salaryDataTableCond.length()>0){
	        String sql="select AppProcess from " + salaryDataTable + ", dbname " + salaryDataTableCond +
	                    " order by dbname.dbid, A0000, A00Z0, A00Z1";
	        sql=Sql_switcher.sqlTop(sql, 1);
	        
	        RowSet rs = null;
	        try{
	            ContentDAO dao = new ContentDAO(this.conn);
	            rs = dao.search(sql);
	            if(rs.next())
	            {
	                String appProcess=rs.getString("AppProcess");
	                s = formatAppProcess(appProcess);
	            }
	        }catch(Exception e){
	            e.printStackTrace();
	        }finally
	        {
	            if(rs!=null)
	            {
	                try
	                {
	                    rs.close();
	                }catch(Exception e)
	                {
	                    e.printStackTrace();
	                }
	            }
	        }

	    }
	    return s;
	}
	
	/**
	 * 判断花名册中，是否有年月变化子集
	 * @param tabid
	 * @return
	 */
public boolean getSetChangeFlag(String tabid)
{
	boolean flag= false;
	RowSet rs = null;
	try{
		ContentDAO dao = new ContentDAO(this.conn);
		rs = dao.search("select setname,Field_Name from Muster_Cell where Tabid="+tabid);
		while(rs.next())
		{
			String setname=rs.getString("setname");
			if(setname==null|| "".equals(setname)) {
                continue;
            }
			FieldSet vo = DataDictionary.getFieldSetVo(setname.toLowerCase());
			if(vo==null) {
                continue;
            }
			if(vo.getChangeflag()!=null&&!"0".equals(vo.getChangeflag()))//按年或按月变化的子集
			{
				flag=true;
				break;
			}
		}
	}catch(Exception e){
		e.printStackTrace();
	}finally
	{
		if(rs!=null)
		{
			try
			{
				rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	return flag;
}
	
	public String getYearMonth(String infor_Flag,String history,String tempTable,String year,String month,int type,String count,boolean ischangeflag)
	{
		String context="&nbsp;"; 
		try
		{
			Calendar c=Calendar.getInstance();
			if("salary".equals(infor_Flag)|| "stipend".equals(infor_Flag))
			{
				if("analysis".equalsIgnoreCase(checksalary)&&"0".equals(history)){// 薪资分析-全部
		    		String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
		    		if(value!=null&&!"".equals(value))
		    		{
		    			year=value.substring(0,4);
		    			month=trimMonth(value.substring(5,7));
			    		context=year+ResourceFactory.getProperty("kq.wizard.year")+month+ResourceFactory.getProperty("kq.wizard.month");
					    if(type==2&&count!=null&&count.length()>0) {
                            context+=count+ResourceFactory.getProperty("hmuster.label.count");
                        }
		    		}
				}
				else if("2".equals(history)&&year!=null&&year.contains("-")){//区间
					String year2="";
					String month2="";
	    			year2=year.substring(5,9);
					year=year.substring(0,4);
					if(month.contains("-")){
						month2=trimMonth(month.substring(3,5));
						month=trimMonth(month.substring(0,2));
					}
					context=year+ResourceFactory.getProperty("kq.wizard.year")+month+ResourceFactory.getProperty("kq.wizard.month")+"～"+
							year2+ResourceFactory.getProperty("kq.wizard.year")+month2+ResourceFactory.getProperty("kq.wizard.month");
				}
				else{
					if(year!=null&&year.length()>0)
					{
						context=year+ResourceFactory.getProperty("kq.wizard.year");
						if(month!=null&&month.length()>0) {
                            context+=trimMonth(month)+ResourceFactory.getProperty("kq.wizard.month");
                        }
					    if(type==2&&count!=null&&count.length()>0) {
                            context+=count+ResourceFactory.getProperty("hmuster.label.count");
                        }
					}				
					else{
						HashMap m=getMusterYearMonth(tempTable);
						Date d=(Date)m.get("date");
						String a00z1=(String)m.get("count");
						if(d!=null){
							c.setTime(d);
							context=c.get(Calendar.YEAR)+ResourceFactory.getProperty("kq.wizard.year")+trimMonth(String.valueOf(c.get(Calendar.MONTH)+1))+ResourceFactory.getProperty("kq.wizard.month");
						    if(a00z1!=null&&type==2) {
                                context+=a00z1+ResourceFactory.getProperty("hmuster.label.count");
                            }
						}
						else{
				    		String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
				    		if(value!=null&&!"".equals(value))
				    		{
				    			year=value.substring(0,4);
				    			month=trimMonth(value.substring(5,7));
					    		context=year+ResourceFactory.getProperty("kq.wizard.year")+month+ResourceFactory.getProperty("kq.wizard.month");
							    if(type==2&&count!=null&&count.length()>0&&!"-1".equals(count)) {
                                    context+=count+ResourceFactory.getProperty("hmuster.label.count");
                                }
				    		}
						}
					}
				}
			}
			else if ("81".equals(modelFlag)) {
                HashMap m = getMusterYearMonth(tempTable);
                Date d = (Date)m.get("date");
                String a00z1 = (String)m.get("count");
                if(d!=null){
                    c.setTime(d);
                    context=c.get(Calendar.YEAR)+ResourceFactory.getProperty("kq.wizard.year")+trimMonth(String.valueOf(c.get(Calendar.MONTH)+1))+ResourceFactory.getProperty("kq.wizard.month");
                    if(a00z1!=null&&type==2) {
                        context+=a00z1+ResourceFactory.getProperty("hmuster.label.count");
                    }
                }
			}
			else
			{
				//type=2标题中的年月次，在取当前记录和取部分历史记录，以及只有一般子集时，都不应该取数，当涉及年月子集指标时，只在取某次历史记录和工资当前表时可取
                if(type==2)
                {
                	if(ischangeflag&& "3".equals(history))
                	{
                		context = year
						+ ResourceFactory.getProperty("hmuster.label.year")
						+ trimMonth(month)
						+ ResourceFactory.getProperty("hmuster.label.month");
				    	context+=count+ResourceFactory.getProperty("hmuster.label.count");
                	}
                }else{
		    		if ("3".equals(history)) {//某次
		    			context = year
			    				+ ResourceFactory.getProperty("hmuster.label.year")
			    				+ trimMonth(month)
			    				+ ResourceFactory.getProperty("hmuster.label.month");
				 
	    			} else if("2".equals(history)) {//取部分历史
	    				if(this.yearMont!=null&&this.yearMont.length()>0)
	    				{
	    					context=this.yearMont;
	    				}else{
				    		String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
				    		String ayear="";
				    		String amonth="";
				    		String aday="";
				    		if(value!=null&&!"".equals(value))
				    		{
				    			ayear=value.substring(0,4);
				    			amonth=value.substring(5,7);
				    			aday=value.substring(8);
				    		}else if(this.userView!=null&&this.userView.getAppdate()!=null&&!"".equals(this.userView.getAppdate())){
				    			ayear=this.userView.getAppdate().substring(0,4);
				    			amonth=this.userView.getAppdate().substring(5,7);
				     			aday=this.userView.getAppdate().substring(8);
				    		}else
				    		{
				    			ayear=c.get(Calendar.YEAR)+"";
				    			amonth=c.get(Calendar.MONTH)+1+"";
				    		}
				    		context = ayear+ResourceFactory.getProperty("kq.wizard.year")+amonth+ResourceFactory.getProperty("kq.wizard.month");
	    				}
	    			}else{//取最后一条
	    				String value=ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
			    		String ayear="";
			    		String amonth="";
			    		String aday="";
			    		if(value!=null&&!"".equals(value))
			    		{
			    			ayear=value.substring(0,4);
			    			amonth=value.substring(5,7);
			    			aday=value.substring(8);
			    		}else if(this.userView!=null&&this.userView.getAppdate()!=null&&!"".equals(this.userView.getAppdate())){
			    			ayear=this.userView.getAppdate().substring(0,4);
			    			amonth=this.userView.getAppdate().substring(5,7);
			     			aday=this.userView.getAppdate().substring(8);
			    		}else
			    		{
			    			ayear=c.get(Calendar.YEAR)+"";
			    			amonth=c.get(Calendar.MONTH)+1+"";
			    		}
			    		context = ayear+ResourceFactory.getProperty("kq.wizard.year")+amonth+ResourceFactory.getProperty("kq.wizard.month");
	    			}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{

		}
		return context;
	}
	
	/**
	 * 去掉月份中的前导0
	 * @param month
	 * @return
	 */
	private String trimMonth(String month){
		if(month!=null&&month.length()==2){
			if(month.charAt(0)=='0') {
                month=month.substring(1);
            }
		}
		return month;
	}
		
	public HashMap getMusterYearMonth(String musterTable){
		HashMap m=new HashMap();
		Date d=null;
		String a00z1=null;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		DbWizard dbw = new DbWizard(conn);
		if("salary".equals(infor_Flag)&& "salary".equalsIgnoreCase(this.modelFlag))
		{
			try {
			    if (dbw.isExistField(musterTable, "declare_tax", false)) {
    				rowSet=dao.search("select Max(declare_tax) AS declare_tax, Max(a00z1) AS a00z1 from "+musterTable);
    				if(rowSet.next())
    				{
    					d=rowSet.getDate("declare_tax");
    				    a00z1=rowSet.getString("a00z1");
    				}
			    }
			} catch (Exception e) {}
			try {
				if(d==null && dbw.isExistField(musterTable, "a00z0", false)){
					rowSet=dao.search("select Max(a00z2) AS a00z0, Max(a00z3) AS a00z1 from "+musterTable);
					if(rowSet.next())
					{
						d=rowSet.getDate("a00z0");
					    a00z1=rowSet.getString("a00z1");
					}							
				}
			} catch (Exception e) {}
		}else{
			try{
			    if("81".equals(modelFlag)&&dbw.isExistField(musterTable, "Q03Z0", false)) {
                    rowSet=dao.search("select Max(Q03Z0) AS Q03Z0 from "+musterTable);
                    if(rowSet.next())
                    {
                        String s=rowSet.getString("Q03Z0");
                        if(s != null) {
                            s += "-01";
                        }
                        if(s == null) {
                            s = ConstantParamter.getAppdate(this.userView.getUserName()).replaceAll("\\.","-");
                        }
                        if(s != null) {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            d = df.parse(s);
                        }
                    }
			    }
			    else if(dbw.isExistField(musterTable, "a00z0", false)) {
    				rowSet=dao.search("select Max(a00z0) AS a00z0, Max(a00z1) AS a00z1 from "+musterTable);
    				if(rowSet.next())
    				{
    					d=rowSet.getDate("a00z0");
    				    a00z1=rowSet.getString("a00z1");
    				}
			    }
			} catch (Exception e) {}
		}
		m.put("date", d);
		m.put("count", a00z1);
		return m;
	}
	
	/**
	 * 取得记录的总行数
	 * @param infor_Flag
	 * @param tempTable
	 * @return
	 */
	public int getCount(String infor_Flag,String tempTable,boolean isGroupTerm)
	{
		int count=0;
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String tempCount2 = "";
			String where = this.privConditionStr;
			if ("1".equals(infor_Flag)|| "stipend".equals(infor_Flag)|| "salary".equals(infor_Flag)) {
				tempCount2 = " count(a0100) a ";							
			} else if ("2".equals(infor_Flag)) {
                tempCount2 = " count(b0110) a ";
            } else if ("3".equals(infor_Flag)) {
                tempCount2 = " count(e01a1) a ";
            } else {
                tempCount2 = " count(*) a ";
            }
			String sql2 = "select " + tempCount2 + " from "+ tempTable + where;
			
			
			
			dao.update("update "+tempTable+" set groupN='' where groupN is null");
			HmusterXML hmxml = new HmusterXML(this.conn,tabid);
			String GROUPFIELD=hmxml.getValue(HmusterXML.GROUPFIELD);
			GROUPFIELD=GROUPFIELD!=null?GROUPFIELD:"";
			if(GROUPFIELD.trim().length()>4){
				FieldItem item = DataDictionary.getFieldItem(GROUPFIELD);
				if((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))){
					if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						//orderby=" order by A0000,recidx";
					} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						sql2="select count(*) from ("+sql2+" group by GroupN,GroupV) a";
					}
					
				}else{
					if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						//orderby=" order by GroupN,recidx";
					} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
						sql2="select count(*) from ("+sql2+" group by GroupN,GroupV) a";
					}
				}
			}else{
				if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
					//orderby=" order by GroupN,recidx";
				} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
					sql2="select count(*) from ("+sql2+" group by GroupN,GroupV) a";
				}
			}
			
			
			
			
			
			
			
			rowSet = dao.search(sql2);
			if (rowSet.next()) {
				count = rowSet.getInt(1);
			} else {
				count = 0;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return count;
	}
	
	
	public String  isNotNull(String fieldName)
	{
		String str=fieldName+" is not null ";
		if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
            str+=" and "+fieldName+"<>''";
        }
		return str;
	}
	public String getTitleCount(String groupV,String tempTable){
		String context="";
		ResultSet resultSet=null;
		try{
			StringBuffer buf = new StringBuffer();
			buf.append("select count(*) as counts from ");
			buf.append(tempTable);
			if(groupV!=null&&groupV.trim().length()>0) {
                buf.append(" where groupN='"+groupV+"'");
            } else {
                buf.append(" where groupN='' or groupN is null ");
            }
			ContentDAO dao = new ContentDAO(conn);
			resultSet=dao.search(buf.toString());
			if(resultSet.next()){
				context = resultSet.getInt("counts")+"";
			}else{
				context = "0";
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try
			{
				if(resultSet!=null) {
                    resultSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return context;
	}
	
	/**
	 * 取得标题变量
	 * @param extendAtrr  变量的描述信息
	 * @return
	 */
	public String getTitleVarValue(String extendAtrr,String infor_Flag,String tempTable,boolean isGroupTerm)
	{
		String context="";
		HashMap map=analyseExtendAttr(extendAtrr);   //分析字符串描述
		int mode=Integer.parseInt((String)map.get("mode"));
		String type=(String)map.get("type");
		String expr=(String)map.get("expr");
		if(mode==1)      //求个数
		{
				context=String.valueOf(getCount(infor_Flag,tempTable,isGroupTerm));
		}
		else
		{
			// 2 首记录  3末记录   4平均值   5求总合   6求最大   7求最小
			String sql=getTitleVarSql(map,tempTable);
			ResultSet resultSet=null;
			Statement statement=null;
			try
			{
				if(sql!=null&&sql.trim().length()>0){
					ContentDAO dao = new ContentDAO(conn);
					resultSet=dao.search(sql);
					String value="";

					if(resultSet.next())
					{
						if(!"M".equals(type)) {
                            value=resultSet.getString(expr);
                        } else {
                            value=Sql_switcher.readMemo(resultSet,expr);
                        }
					}

					if("N".equals(type))
					{
						String dec=(String)map.get("dec");
						if(value!=null&&!"".equals(value)) {
                            value=round(value,Integer.parseInt(dec));
                        } else {
                            value="";
                        }
					}
					context=value;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try
				{
					if(resultSet!=null) {
                        resultSet.close();
                    }
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}
		return context;
	}
	/**
	 * 取得标题变量
	 * @param extendAtrr  变量的描述信息
	 * @return
	 */
	public String getTitleVarValue(String extendAtrr,String infor_Flag,String tempTable,
			String year,String month,boolean isGroupTerm)
	{
		String context="";
		HashMap map=analyseExtendAttr(extendAtrr);   //分析字符串描述
		int mode=Integer.parseInt((String)map.get("mode"));
		String type=(String)map.get("type");
		String expr=(String)map.get("expr");
		String BIGNUM = extendAttrXML(extendAtrr,"BIGNUM");
		if(mode==1)      //求个数
		{
				context=String.valueOf(getCount(infor_Flag,tempTable,isGroupTerm));
				if("true".equalsIgnoreCase(BIGNUM)){
					context = NumToRMBBo.NumToRMBStr(Double.parseDouble(context));
				}
		}
		else
		{
			// 2 首记录  3末记录   4平均值   5求总合   6求最大   7求最小
			String sql= "";
			if("salary".equalsIgnoreCase(infor_Flag)) {
                sql= getTitleVarSql(map,tempTable,year,month);
            } else {
                sql= getTitleVarSql1(map,tempTable);
            }
			ResultSet resultSet=null;
			Statement statement=null;
			try
			{
				if(sql!=null&&sql.trim().length()>0){
					ContentDAO dao = new ContentDAO(conn);
					resultSet=dao.search(sql);
					String value="";

					if(mode==3){
						while(resultSet.next()){
							if(!"M".equals(type)) {
                                value=resultSet.getString(expr);
                            } else {
                                value=Sql_switcher.readMemo(resultSet,expr);
                            }
						}
					}else{
						if(resultSet.next())
						{
							if(!"M".equals(type)) {
                                value=resultSet.getString(expr);
                            } else {
                                value=Sql_switcher.readMemo(resultSet,expr);
                            }
						}
					}

					if("N".equals(type))
					{
						String dec=(String)map.get("dec");
						if(value!=null&&value.trim().length()>0){
							value=round(value,Integer.parseInt(dec));
							if(Integer.parseInt(dec)>0){
								int n = 8;
								n = n-value.substring(value.indexOf(".")).length();
								if("true".equalsIgnoreCase(BIGNUM)){
									value = NumToRMBBo.NumToRMBStr(Double.parseDouble(value));
								}
								for(int i=0;i<n;i++){
									value ="&nbsp;"+value;
								}
							}else{
								int n = 8;
								n = n-value.length();
								if("true".equalsIgnoreCase(BIGNUM)){
									value = NumToRMBBo.NumToRMBStr(Double.parseDouble(value));
								}
								for(int i=0;i<n;i++){
									value ="&nbsp;"+value;
								}
							}
						}else{
							if("true".equalsIgnoreCase(BIGNUM)){
								value = NumToRMBBo.NumToRMBStr(0);
							}else {
                                value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0";
                            }
						}
					}
					context=value;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try
				{
					if(resultSet!=null) {
                        resultSet.close();
                    }
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}
		return context;
	}
	
	/**
	 * 取得求标题变量的sql语句
	 * @param map
	 * @param tempTable   临时表名
	 * @return
	 */
	public String getTitleVarSql(HashMap map,String tempTable)
	{
		StringBuffer sql=new StringBuffer("");
		if(this.gridNoMap==null)
		{
			if(!"stipend".equals(this.modelFlag)) {
                this.gridNoMap=getNoMap(tempTable.split("_")[2]);
            } else {
                this.gridNoMap=getNoMap(tempTable.split("_")[1]);
            }
		}
		String mode=(String)map.get("mode");
		String expr=((String)map.get("expr")).toUpperCase();
		sql.append("select ");
		if(!"2".equals(mode)&!"3".equals(mode))
		{
			if("4".equals(mode)) //平均值
			{
				sql.append("avg(");
			}
			else if("5".equals(mode)) //求总合
			{
				sql.append("sum(");	
			}
			else if("6".equals(mode)) //求最大
			{
				sql.append("max(");	
			}
			else if("7".equals(mode)) //求最小
			{
				sql.append("min(");
			}
		}
		if(this.gridNoMap.get(expr)!=null){
			sql.append("C"+(String)this.gridNoMap.get(expr));
			if(!"2".equals(mode)&!"3".equals(mode))
			{
				sql.append(")");
			}
			sql.append(" "+expr+" from "+tempTable);	
			if("2".equals(mode))
			{
				if (isGroupPoint != null&& "1".equals(isGroupPoint)) {
					sql.append(" order by GroupN,recidx");
				} else if (isGroupPoint != null&& "1".equals(isGroupPoint)) {
					sql.append(" group by GroupN,GroupV  ");
				}
				else {
                    sql.append(" where recidx=1");
                }
			}
			if("3".equals(mode)) {
                sql.append(" where recidx=(select max(recidx) from "+tempTable+")");
            }
		}else{
			FieldItem fielditem = DataDictionary.getFieldItem(expr);
		}
		return sql.toString();
	}
	
	/**
	 * 取得求标题变量的sql语句
	 * @param map
	 * @param tempTable   临时表名
	 * @return
	 */
	public String getTitleVarSql(HashMap map,String tempTable,String year,String month)
	{
		StringBuffer sql=new StringBuffer("");
		if(this.gridNoMap==null)
		{
			if(!"stipend".equals(this.modelFlag)) {
                this.gridNoMap=getNoMap(tempTable.split("_")[2]);
            } else {
                this.gridNoMap=getNoMap(tempTable.split("_")[1]);
            }
		}
		String mode=(String)map.get("mode");
		String expr=((String)map.get("expr")).toUpperCase();
		sql.append("select ");
		if(!"2".equals(mode)&!"3".equals(mode))
		{
			if("4".equals(mode)) //平均值
			{
				sql.append("avg(");
			}
			else if("5".equals(mode)) //求总和
			{
				sql.append("sum(");	
			}
			else if("6".equals(mode)) //求最大
			{
				sql.append("max(");	
			}
			else if("7".equals(mode)) //求最小
			{
				sql.append("min(");
			}
		}
		if(this.gridNoMap.get(expr)!=null){
			sql.append(Sql_switcher.isnull("C"+(String)this.gridNoMap.get(expr), "0"));
			if(!"2".equals(mode)&!"3".equals(mode))
			{
				sql.append(")");
			}
			sql.append(" "+expr+" from "+tempTable);	
			if("2".equals(mode))
			{
				if (isGroupPoint != null&& "1".equals(isGroupPoint)) {
					sql.append(" order by GroupN,recidx");
				} else if (isGroupPoint != null && "1".equals(isGroupPoint)) {
					sql.append(" group by GroupN,GroupV  ");
				}
				else {
                    sql.append(" where recidx=1");
                }
			}
			if("3".equals(mode)) {
                sql.append(" where recidx=(select max(recidx) from "+tempTable+")");
            }
		}else{
			if(groupPoint!=null&&groupPoint.trim().length()>0){
				String itemid = groupPoint;
				if(itemid!=null&&itemid.trim().length()>5){
					itemid = itemid.substring(0,5);
				}
				FieldItem fielditem1 = DataDictionary.getFieldItem(itemid);
				if("UN".equalsIgnoreCase(fielditem1.getCodesetid())|| "UM".equalsIgnoreCase(fielditem1.getCodesetid())){
					FieldItem fielditem = DataDictionary.getFieldItem(expr);
					sql.append(Sql_switcher.isnull(expr, "0"));
					if(!"2".equals(mode)&!"3".equals(mode))
					{
						sql.append(")");
					}
					sql.append(" "+expr+" from ");
					sql.append(fielditem.getFieldsetid());
					if(year!=null&&year.trim().length()==4){
						sql.append(" where ");
						sql.append(Sql_switcher.year(fielditem.getFieldsetid()+"Z0")+"="+year);
						if(month!=null&&month.length()>0){
							sql.append(" and ");
							sql.append(Sql_switcher.month(fielditem.getFieldsetid()+"Z0")+"="+month);
						}
						sql.append(" and B0110='");
						sql.append(this.getGroupNcode());
						sql.append("'");
					}else{
						ContentDAO dao = new ContentDAO(this.conn);
						RowSet rowSet;
						try {
							rowSet = dao.search("select a00z0 from "+tempTable);
							Calendar c=Calendar.getInstance();
							while(rowSet.next()){
								Date d=rowSet.getDate(1);
								if(d!=null){
									c.setTime(d);
									year = c.get(Calendar.YEAR)+"";
									month = (c.get(Calendar.MONTH)+1)+"";
									break;
								}
							}
						} catch (SQLException e) {}
						sql.append(" where ");
						if(year!=null&&year.trim().length()==4){
							sql.append(Sql_switcher.year(fielditem.getFieldsetid()+"Z0")+"="+year);
							if(month!=null&&month.length()>0){
								sql.append(" and ");
								sql.append(Sql_switcher.month(fielditem.getFieldsetid()+"Z0")+"="+month);
							}
							sql.append(" and ");
						}
						sql.append(" B0110='");
						sql.append(this.getGroupNcode());
						sql.append("'");
					}
				}else {
                    return "";
                }
			}else {
                return "";
            }
		}
		return sql.toString();
	}
	
	/**
	 * 取得求标题变量的sql语句
	 * @param map
	 * @param tempTable   临时表名
	 * @return
	 */
	public String getTitleVarSql1(HashMap map,String tempTable)
	{
		StringBuffer sql=new StringBuffer("");
		if(this.gridNoMap==null)
		{
			if(!"stipend".equals(this.modelFlag)) {
                this.gridNoMap=getNoMap(tempTable.split("_")[2]);
            } else {
                this.gridNoMap=getNoMap(tempTable.split("_")[1]);
            }
		}
		String mode=(String)map.get("mode");
		String expr=((String)map.get("expr")).toUpperCase();
		sql.append("select ");
		if(!"2".equals(mode)&!"3".equals(mode))
		{
			if("4".equals(mode)) //平均值
			{
				sql.append("avg(");
			}
			else if("5".equals(mode)) //求总和
			{
				sql.append("sum(");	
			}
			else if("6".equals(mode)) //求最大
			{
				sql.append("max(");	
			}
			else if("7".equals(mode)) //求最小
			{
				sql.append("min(");
			}
		}
		if(this.gridNoMap.get(expr)!=null){
			sql.append(Sql_switcher.isnull("C"+(String)this.gridNoMap.get(expr), "0"));
			if(!"2".equals(mode)&!"3".equals(mode))
			{
				sql.append(")");
			}
			sql.append(" "+expr+" from "+tempTable);	
			if("2".equals(mode))
			{
				if (isGroupPoint != null&& "1".equals(isGroupPoint)) {
					sql.append(" order by GroupN,recidx");
				} else if (isGroupPoint != null&& "1".equals(isGroupPoint)) {
					sql.append(" group by GroupN,GroupV  ");
				}
				else {
                    sql.append(" where recidx=1");
                }
			}
			if("3".equals(mode)) {
                sql.append(" where recidx=(select max(recidx) from "+tempTable+")");
            }
		}else{
			if(groupPoint!=null&&groupPoint.trim().length()>0){
				String itemid = groupPoint;
				if(itemid!=null&&itemid.trim().length()>5){
					itemid = itemid.substring(0,5);
				}
				FieldItem fielditem1 = DataDictionary.getFieldItem(itemid);
				if("UN".equalsIgnoreCase(fielditem1.getCodesetid())|| "UM".equalsIgnoreCase(fielditem1.getCodesetid())){
					FieldItem fielditem = DataDictionary.getFieldItem(expr);
					sql.append(Sql_switcher.isnull(expr, "0"));
					if(!"2".equals(mode)&!"3".equals(mode))
					{
						sql.append(")");
					}
					sql.append(" "+expr+" from ");
					sql.append(fielditem.getFieldsetid());
					sql.append(" where  B0110='");
					sql.append(this.getGroupNcode());
					sql.append("'");
				}else {
                    return "";
                }
			}else {
                return "";
            }
		}
		return sql.toString();
	}
	
	
	public HashMap getNoMap(String tabid)
	{
		HashMap map=new HashMap();
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			String sql="select GridNo,field_Name from muster_cell where field_Name is not null ";
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                sql+=" and field_Name<>''";
            }
			sql+=" and  tabid="+tabid;
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				map.put(rowSet.getString("field_Name").toUpperCase(),rowSet.getString("gridno"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	
	
	
	
	private HashMap  analyseExtendAttr(String extendAttr)
	{
		HashMap map=new HashMap();
		String s=extendAttr;
		String setid=s.substring(s.indexOf("<SETID>")+7,s.indexOf("</SETID>"));
		String expr=s.substring(s.indexOf("<EXPR>")+6,s.indexOf("</EXPR>"));
		String mode=s.substring(s.indexOf("<MODE>")+6,s.indexOf("</MODE>"));
		String type=s.substring(s.indexOf("<TYPE>")+6,s.indexOf("</TYPE>"));
		String format=s.substring(s.indexOf("<FORMAT>")+8,s.indexOf("</FORMAT>"));
		String len=s.substring(s.indexOf("<LEN>")+5,s.indexOf("</LEN>"));
		String dec=s.substring(s.indexOf("<DEC>")+5,s.indexOf("</DEC>"));
		
		map.put("setid",setid);
		map.put("expr",expr);
		map.put("mode",mode);
		map.put("type",type);
		map.put("format",format);
		map.put("len",len);
		map.put("dec",dec);
		
		return map;
	}
	
	
	
	
	
	/**
	 * 得到花名册的总页数
	 * 
	 * @param infor_Flag
	 *            信息群标识
	 * @param isGroupPoint
	 *            是否分组
	 * @param tempTable
	 *            临时表名称
	 * @param paperRows
	 *            页行数
	 * @param isGroupTerm
	 *            表头条件里是否有分组指标
	 * @return int
	 */
	public int getHmusterTotalPages(boolean isGroupTerm, String infor_Flag,
			String isGroupPoint, String tempTable, int paperRows,
			 String dbpre) throws GeneralException {
		int pages = 0;
		int count = 0;
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String tempCount = "";
		String where = this.privConditionStr;
		if ("1".equals(infor_Flag)||
				"5".equals(this.modelFlag)|| "salary".equals(this.modelFlag)|| "stipend".equals(this.modelFlag)) {
			tempCount = " count(a0100) ";

		} else if ("2".equals(infor_Flag)) {
			tempCount = " count(b0110) ";
		} else if ("3".equals(infor_Flag)) {
			tempCount = " count(e01a1) ";
		} else {
            tempCount = " count(*) ";
        }

		try {
			if (!isGroupTerm && isGroupPoint != null
					&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
				String sql = "select " + tempCount + " from " + tempTable
						+ where + " group by GroupN";
				if(isGroupTerm2&&"1".equals(isGroupPoint2)) {//插入两个分组指标 且分组分页
					sql=" select count(a0100),GroupN from "+tempTable+" "+where+" group by GroupN,GroupN2 order by groupN";
					rowSet=dao.search(sql);
					String groupn="";//记录上次分组指标一的内容
					int groupnCount=0;//记录相同分组指标记录数 用于分页计算总页数
					int totalCount=0;
					pages=1;
					while(rowSet.next()) {
						totalCount++;
						if(rowSet.isFirst()) {
							groupn=rowSet.getString("groupn");
							groupnCount++;
						}else {
							if(groupn.equals(rowSet.getString("groupn"))) {
								groupnCount++;
								if(groupnCount%paperRows==0) {//能被整除 页数+1 记录条数清空
									pages++;
									groupnCount=0;
								}
							}else {
								groupn=rowSet.getString("groupn");
								pages++;
								groupnCount=0;
							}
						}
						
					}
					//pages++;
					countall=totalCount;
				}else {
					rowSet = dao.search(sql);
					while (rowSet.next()) {
						int s = rowSet.getInt(1);
						countall +=s;
						if (s < paperRows) {
                            pages++;
                        } else {
							if (s % paperRows == 0) {
                                pages += s / paperRows;
                            } else {
                                pages += s / paperRows + 1;
                            }
						}
					}
				}
			}/*else if("salary".equals(infor_Flag)&&isGroupTerm && isGroupPoint != null
					&& isGroupPoint.equals("1")&&isGroupNoPage.equals("0")){//分组分页 有分组查询指标 查询
				sql = "select count(a) from (select " + tempCount
				+ " a from " + tempTable + where
				+ " group by GroupN ) temp";
				System.out.println(sql);
				rowSet = dao.search(sql);
				while (rowSet.next()) {
					pages=rowSet.getInt(1);
					countall=pages;
				}
				pageRows=1;
			} */else // 如果不分组查询
			{
				String sql = "";
				if (!isGroupTerm&&!isGroupTerm2)  // 明细
                {
                    sql = "select " + tempCount + " from " + tempTable + where;
                } else{  // 汇总
					if("1".equals(isGroupPoint2)) {
						if(isGroupTerm2) {//有分组指标2且有组内记录数
							sql = "select count(a) from (select " + tempCount
									+ " a from " + tempTable + where
									+ " group by GroupN ) temp";
						}else {
							sql = "select count(a) from (select " + tempCount
									+ " a from " + tempTable + where
									+ " group by GroupN,GroupN2 ) temp";
						}
						
					}else {
						sql = "select count(a) from (select " + tempCount
								+ " a from " + tempTable + where
								+ " group by GroupN ) temp";
					}
				}
				rowSet = dao.search(sql);
				if (rowSet.next()) {
					count = rowSet.getInt(1);
				} else {
					count = 0;
				}
				if (count % paperRows == 0) {
                    pages = count / paperRows;
                } else {
                    pages = count / paperRows + 1;
                }
				if(isGroupTerm&&(!"1".equals(isGroupPoint2)&& "1".equals(isGroupPoint)||"1".equals(isGroupPoint2)&& "1".equals(isGroupPoint))&& "0".equals(isGroupNoPage))//有分组指标 无分组指标二 有分组指标一  分组分页 解决只插入一个分组指标时分组分页 页面无法翻页
                {
                    pages=count;
                }
				
					countall=count;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return pages;
	}
	
	
	
	//取得权限控制语句
	public String getPrivCondition(String infor_Flag,String dbpre)
	{
		StringBuffer privConditionStr=new StringBuffer("");
		try
		{
			HmusterXML hmxml = new HmusterXML(this.conn,this.tabid);
			String SHOW_PART_JOB="false";
			if("3".equals(modelFlag)) {
                SHOW_PART_JOB=hmxml.getValue(HmusterXML.SHOW_PART_JOB);
            }
			if(SHOW_PART_JOB==null|| "".equals(SHOW_PART_JOB)) {
                SHOW_PART_JOB="false";
            }
			if (!userView.isSuper_admin()) {
				if ("1".equals(infor_Flag)&& "false".equalsIgnoreCase(this.no_manger_priv)&&!"ALL".equals(dbpre)) // 人员库
				{
					String conditionSql = " select "+dbpre+"A01.A0100 "+ userView.getPrivSQLExpression(dbpre, true);
					if(this.userView.getStatus()==4&&(this.userView.getManagePrivCodeValue()==null||"".equals(this.userView.getManagePrivCodeValue()))){
						if(!"UN".equalsIgnoreCase(this.userView.getManagePrivCode())) {//自助用户 人员范围设置顶级节点时 this.userView.getManagePrivCodeValue()会为空  判断this.userView.getManagePrivCode()是否为UN UN则为顶级节点
							if(conditionSql.indexOf("where")!=-1) {
								conditionSql=conditionSql.substring(0, conditionSql.indexOf("where"));
								conditionSql=conditionSql+" WHERE "+dbpre+"A01.A0100 ='"+this.userView.getA0100()+"'";
							}else if(conditionSql.indexOf("WHERE")!=-1) {
								conditionSql=conditionSql.substring(0, conditionSql.indexOf("WHERE"));
								conditionSql=conditionSql+" WHERE "+dbpre+"A01.A0100 ='"+this.userView.getA0100()+"'";
							}
							
						}
					}
					privConditionStr.append(" where ((A0100 in (" + conditionSql+ " ))");
					/**加入兼职人员*/
					 String parttimerSQL =""; 
					 if(userView.getManagePrivCodeValue()!=null&&!"".equals(userView.getManagePrivCodeValue())&&!"false".equals(SHOW_PART_JOB))// 27663业务用户判断是否有兼职指标 ，判断是否要求显示兼职人员
                     {
                         parttimerSQL=this.getQueryFromPartLike(userView, dbpre, userView.getManagePrivCodeValue());
                     }
					 if(parttimerSQL!=null&&!"".equals(parttimerSQL))
					 {
						 privConditionStr.append(" or ("+parttimerSQL+")");
					 }
					 privConditionStr.append(")");
				}
/*				String codesetid=userView.getManagePrivCode();
				String codeValue=userView.getManagePrivCodeValue();
				if (infor_Flag.equals("2")) // 2：机构
				{
					String conditionSql = " select codeitemid from organization  where ( codesetid='UN' or codesetid='UM') and  codeitemid like '"+ codeValue+"%'";
					privConditionStr.append(" where b0110 in (" + conditionSql + " )");
					
				}
				
				if (infor_Flag.equals("3")) //  3：职位
				{
					String conditionSql = " select codeitemid from organization  where codesetid='@K' and  codeitemid like '"+ codeValue+"%'";
					privConditionStr.append(" where e01a1 in (" + conditionSql + " )");
				
				}
*/
                if("2".equals(infor_Flag) || "3".equals(infor_Flag)) {
                    String codevalue="";
                    codevalue=userView.getUnitIdByBusi("4");
                    String[] valuearr=StringUtils.split(codevalue,"`");
                    if(valuearr.length==0) {
                        return "";
                    }
                    StringBuffer value=new StringBuffer();
                    if ("2".equals(infor_Flag)) // 2：机构
                    {
                        value.append("select codeitemid from organization  where ( codesetid='UN' or codesetid='UM') and  ");
                    } else {
                        value.append("select codeitemid from organization  where codesetid='@K' and ");
                    }
                    value.append("(");
                    for(int i=0;i<valuearr.length;i++)
                    {
                        if(i!=0) {
                            value.append(" or ");
                        }
                        value.append(" codeitemid like '");          
                        value.append(valuearr[i].substring(2));
                        value.append("%'");
                    }
                    value.append(")");
                    if ("2".equals(infor_Flag)) {
                        privConditionStr.append(" where b0110 in (" + value + " )");
                    } else {
                        privConditionStr.append(" where e01a1 in (" + value + " )");
                    }
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return privConditionStr.toString();
	}
	
	
	/**
	 * 用于分栏花名册
	 * @param h_headerList  指标参数信息 包含位置等信息
	 * @param isGroupTerm
	 * @param infor_Flag
	 * @param tableName
	 * @param isGroupPoint
	 * @param tabid
	 * @return
	 */	
	private ArrayList getSql(ArrayList h_headerList,boolean isGroupTerm,String infor_Flag,
			String tableName,String isGroupPoint,String tabid)
	{
		StringBuffer h_sql = new StringBuffer("select ");
		StringBuffer h_sql_ext = new StringBuffer("");
		String isPhoto="0";
		
		for (Iterator t = h_headerList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			/**分组指标G或组内记录数R*/
			if (temp[7] != null && ("G".equals(temp[7])|| "R".equals(temp[7]))) {
				isGroupTerm = true;
			}
			/**分组指标二E和组内记录数R*/
			if (temp[7] != null && ("E".equals(temp[7])|| "R".equals(temp[7]))) {
				isGroupTerm2 = true;
			}
			if (temp[7] != null && !"S".equals(temp[7])&&!"E".equals(temp[7])&& !"G".equals(temp[7]) && !"H".equals(temp[7])&& !"P".equals(temp[7]) && !"R".equals(temp[7])&& !"".equals(temp[7])) {
				h_sql_ext.append(",C" + temp[0]);
			}
			if ("P".equals(temp[7])) {
                isPhoto ="1";
            }
		}

		h_sql_ext.append(",GroupV,GroupN ");
		if(this.isGroupV2)
		{
    	    if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&isGroupPoint != null && "1".equals(isGroupPoint))
    	     {
    	    	h_sql_ext.append(",GroupV2,GroupN2");
    	     }
    	    else if(isGroupTerm) {
                h_sql_ext.append(",MAX(GroupV2) as GroupV2,MAX(GroupN2) as GroupN2");
            } else {
                h_sql_ext.append(",GroupV2,GroupN2");
            }
		}
		if ("1".equals(infor_Flag)|| "salary".equals(infor_Flag)) {
		    if("1".equals(infor_Flag)) {
                h_sql_ext.append(",NBASE");
            }
			h_sql_ext.append(",a0100");
		}else if ("2".equals(infor_Flag)) {
            h_sql_ext.append(",b0110");
        } else if ("3".equals(infor_Flag)) {
            h_sql_ext.append(",e01a1");
        }
		h_sql_ext.append(",recidx");
		String orderby="";
		/**按第一分组指标分组*/
		if ((isGroupPoint != null && "1".equals(isGroupPoint))) {
			try
			{
		    	ContentDAO dao = new ContentDAO(this.conn);
		     	dao.update("update "+tableName+" set groupN='' where groupN is null");
		     	if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&this.isGroupV2)
				{
		         	dao.update("update "+tableName+" set groupN2='' where groupN2 is null");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			HmusterXML hmxml = new HmusterXML(this.conn,tabid);
			String GROUPFIELD=hmxml.getValue(HmusterXML.GROUPFIELD);
			GROUPFIELD=GROUPFIELD!=null?GROUPFIELD:"";
			String GROUPFIELD2=hmxml.getValue(HmusterXML.GROUPFIELD2);
			GROUPFIELD2=GROUPFIELD2!=null?GROUPFIELD2:"";
			if(GROUPFIELD.trim().length()>4){
				FieldItem item = DataDictionary.getFieldItem(GROUPFIELD);
				/**分组指标关联机构代码指标*/
				if((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))){
					h_sql_ext.append(",(select A0000 from organization where codeitemid=");
					h_sql_ext.append(tableName);
					h_sql_ext.append(".GroupN) AS A0000 ");
					if (!isGroupTerm) {
						if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
						{
						    orderby=" group by "+Sql_switcher.month("a00z0");	
						}
						else
						{
				    		if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
					    	{
					    		if(GROUPFIELD2.trim().length()>4)
						    	{
						    		FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
							    	if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
								    	h_sql_ext.append(",(select A0000 from organization where codeitemid=");
								    	h_sql_ext.append(tableName);
									    h_sql_ext.append(".GroupN2) AS A00002 ");
								    	orderby=" order by A0000,A00002,recidx";
					    			}else
					    			{
						    			orderby=" order by A0000,GroupN2,recidx";
						    		}
					    		}
					    		else
					    		{
						    		orderby=" order by A0000,GroupN2,recidx";
					    		}
					    	}
				    		else
				    		{
					    		orderby=" order by A0000,recidx";
				    		}  
						}
					} else if (isGroupTerm) {
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2";
							}
							else
							{
						    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2  ";
							}
						}
						else{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV";
							}
							else
							{
					        	orderby=" group by GroupN,GroupV  order by A0000 ";
							}
						}
					}
					
				}else{
					if (!isGroupTerm) {
						if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
						{
						    orderby=" group by "+Sql_switcher.month("a00z0");	
						}
						else{
				    		if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
				    		{
				    			if(GROUPFIELD2.trim().length()>4)
					    		{
						    		FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
						    		if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
							     		h_sql_ext.append(",(select A0000 from organization where codeitemid=");
								       	h_sql_ext.append(tableName);
								     	h_sql_ext.append(".GroupN2) AS A00002 ");
								     	orderby=" order by GroupN,A00002,recidx";
							    	}else
							    	{
								    	orderby=" order by GroupN,GroupN2,recidx";
						    		}
					     		}
					     		else
					    		{
					    			orderby=" order by GroupN,GroupN2,recidx";
						    	}
					    	}else {
                                orderby=" order by GroupN,recidx";
                            }
						}
					} else if (isGroupTerm) {
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
							}else
							{
						    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
							}
						}else{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV order by GroupN ";
							}
							else
							{
					        	orderby=" group by GroupN,GroupV order by GroupN ";
							}
						}
					}
				}
			}else{
				if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
					if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
					{
					    orderby=" group by "+Sql_switcher.month("a00z0");	
					}
					else
					{
	     				if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
	    				{
	    					if(GROUPFIELD2.trim().length()>4)
		    				{
		    					FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
		    					if((item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())|| "UM".equalsIgnoreCase(item2.getCodesetid())|| "@K".equalsIgnoreCase(item2.getCodesetid())))|| "B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))|| "E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))){
				    				h_sql_ext.append(",(select A0000 from organization where codeitemid=");
				     				h_sql_ext.append(tableName);
					     			h_sql_ext.append(".GroupN2) AS A00002 ");
					     			orderby=" order by GroupN,A00002,recidx";
					    		}else
				    			{
					    			orderby=" order by GroupN,GroupN2,recidx";
				    			}
				    		}
					    	else
					    	{
					     		orderby=" order by GroupN,GroupN2,recidx";
					     	}
				    	}else {
                            orderby=" order by GroupN,recidx";
                        }
					}
				} else if (isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)) {
					if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
					{
						if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
						{
							orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
						}else
						{
					    	orderby=" group by GroupN,GroupV,GroupN2,GroupV2 order by GroupN,GroupN2 ";
						}
					}
					else{
						if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
						{
							orderby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV order by GroupN ";
						}else
						{
				        	orderby=" group by GroupN,GroupV order by GroupN ";
						}
					}
				}
			}
		}else{
			if (!isGroupTerm) {
                orderby=" order by recidx";
            }
			if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
			{
				orderby=" group by "+Sql_switcher.month("a00z0");
			}
		}
		h_sql_ext.append(" from " + tableName);

		if (isGroupTerm||("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))) {
			try
			{
				ContentDAO dao = new ContentDAO(this.conn);
				dao.update("update "+tableName+" set groupN='' where groupN is null");
				if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&this.isGroupV2)
				{
			    	dao.update("update "+tableName+" set groupN2='' where groupN2 is null");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			h_sql_ext = new StringBuffer("");
			h_sql_ext.append(getGroupSql(h_headerList, infor_Flag,tableName, isGroupPoint,tabid));
		}
		/* 权限控制 */
        h_sql_ext.append(this.privConditionStr);
        /*if(this.modelFlag.equals("5"))
        {
        	if(this.privConditionStr.trim().length()>0)
        	{
        		if(this.getSql().trim().length()>0)
        	     	h_sql_ext.append(" and ("+this.getSql()+")");
        	}
        	else
        	{
        		if(this.getSql().trim().length()>0)
        	     	h_sql_ext.append(" where ("+this.getSql()+")");
        	}
        }*/
        if(needCalcAfterSum(h_headerList)){//汇总后计算 不需要group by
        	orderby=" "+orderby.substring(orderby.indexOf("order"));
        }
        h_sql_ext.append(orderby);
		h_sql.append(h_sql_ext.substring(1));
		
		ArrayList list=new ArrayList();
		list.add(h_sql.toString()+"/"+isPhoto);
		list.add(isGroupTerm?"1":"0");
		return list;
		
	}
	
	/**
	 * 花名册取数sql, 用于不分栏花名册
	 * @param tableName
	 * @return
	 * @throws GeneralException
	 */
	public String getMusterSql(String tableName, ArrayList bottomnList) throws GeneralException {
		StringBuffer sql = new StringBuffer("select ");
		StringBuffer tempSql = new StringBuffer("");
		if(bottomnList == null){
			ArrayList aList = getBottomnList(tabid);
			bottomnList=(ArrayList) aList.get(0);
		}
		try {
			boolean isGroupTerm = false; // 是表内条件否有分组指标
			
			/* 根据条件生成查询语句 和 求得每个字符列内容的最大长度的sql语句 */
			for (Iterator t = bottomnList.iterator(); t.hasNext();) {
				String[] temp = (String[]) t.next();
				if(temp[20]!=null&& "1".equals(temp[20])) {
                    continue;
                }
				String extendAttr = temp[20];
				extendAttr=extendAttr!=null?extendAttr:"";
				if (temp[7] != null && ("G".equals(temp[7])|| "R".equals(temp[7]))) {
					isGroupTerm = true;
				}
				if (temp[7] != null && ("E".equals(temp[7]))) {
					isGroupTerm2= true;
				}
				
			   /**H为文本型字段，原来过滤掉，，先加上*/
				if (temp[7] != null && !"".equals(temp[7])&& !"G".equals(temp[7])&& !"E".equals(temp[7]) && !"S".equals(temp[7])&& !"H".equals(temp[7]) && !"P".equals(temp[7])&& !"R".equals(temp[7])) {
					tempSql.append(",C");
					tempSql.append(temp[0]);
				}
			}
			tempSql.append(",GroupV,GroupN");
			if(this.isGroupV2)
			{
		    	if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&isGroupTerm&&isGroupPoint != null && "1".equals(isGroupPoint))
		     	{
		            tempSql.append(",GroupV2,GroupN2");
		     	}
		    	else if(isGroupTerm)
		    	{
    		    	tempSql.append(",MAX(GroupV2) as GroupV2,MAX(GroupN2) as GroupN2");
		    	}
		    	else {
                    tempSql.append(",GroupV2,GroupN2");
                }
			}
			
			if ("1".equals(infor_Flag)|| "stipend".equals(infor_Flag)|| "salary".equals(infor_Flag)) {
                if("1".equals(infor_Flag)) {
                    tempSql.append(",NBASE");
                }
				tempSql.append(",A0100");
			}	
			else if ("2".equals(infor_Flag)) {
                tempSql.append(",B0110");
            } else if ("3".equals(infor_Flag)) {
                tempSql.append(",E01A1");
            }
			String groupby="";
			String orderby="";
			
			if (isGroupPoint != null && "1".equals(isGroupPoint)) {
				HmusterXML hmxml = new HmusterXML(this.conn,tabid);
				String GROUPFIELD=hmxml.getValue(HmusterXML.GROUPFIELD);
				String GROUPFIELD2=hmxml.getValue(HmusterXML.GROUPFIELD2);
				GROUPFIELD2=GROUPFIELD2!=null?GROUPFIELD2:"";
				GROUPFIELD=GROUPFIELD!=null?GROUPFIELD:"";
	     		FieldItem itemGroup = DataDictionary.getFieldItem(GROUPFIELD);
	     		FieldItem itemGroup2 = DataDictionary.getFieldItem(GROUPFIELD2);
				boolean orgA0000Order=(itemGroup!=null&&("UN".equalsIgnoreCase(itemGroup.getCodesetid())||
						"UM".equalsIgnoreCase(itemGroup.getCodesetid())||
						"@K".equalsIgnoreCase(itemGroup.getCodesetid())))||
						(GROUPFIELD.length()>4&&("B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))||
						"E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))||
						"E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))));
				boolean orgA0000Order2= (itemGroup2!=null&&("UN".equalsIgnoreCase(itemGroup2.getCodesetid())||
						"UM".equalsIgnoreCase(itemGroup2.getCodesetid())||
						"@K".equalsIgnoreCase(itemGroup2.getCodesetid())))||
						(GROUPFIELD2.length()>4&&("B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))||
	     				"E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))||
	     				"E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))));
				/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 start */
				boolean isCode = /**! xiaoyun 2014-10-11 人民交通出版社*/((itemGroup!=null&&itemGroup.isCode()&&!("UN".equalsIgnoreCase(itemGroup.getCodesetid())||
						"UM".equalsIgnoreCase(itemGroup.getCodesetid())||
						"@K".equalsIgnoreCase(itemGroup.getCodesetid())))/*||
						(GROUPFIELD.length()>4&&!(GROUPFIELD.substring(0,5).equalsIgnoreCase("B0110")||
						GROUPFIELD.substring(0,5).equalsIgnoreCase("E0122")||
						GROUPFIELD.substring(0,5).equalsIgnoreCase("E01A1"))) xiaoyun 2014-10-11 人民交通出版社*/);
				/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 end */
				
				if(GROUPFIELD.trim().length()>4){
					if(orgA0000Order){
						tempSql.append(",(select A0000 from organization where codeitemid="+tableName+".GroupN) AS A0000 ");
						if (!isGroupTerm&&!isGroupTerm2) {
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								groupby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV";
								orderby=" order by A0000,recidx";
							}
							else
							{
						        if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						        {
							        if(GROUPFIELD2.trim().length()>4)
								    {
								    	if(orgA0000Order2){
								    		tempSql.append(",(select A0000 from organization where codeitemid="+
								    				tableName+".GroupN2) AS A00002 ");
							    			orderby=" order by A0000,A00002,recidx";
							    		}else
							    		{
							     			orderby=" order by A0000,GroupN2,recidx";
							    		}
							    	}
							    	else
							    	{
							    		orderby=" order by A0000,GroupN2,recidx";
							    	}
						    	}
						    	else
						    	{
						    		orderby=" order by A0000,recidx";
						    	}
							}
					    		
						} else if (isGroupTerm||isGroupTerm2) {
							if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
							{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									groupby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2 ";
									orderby="";
								}
								else
								{
									if(orgA0000Order2){
										groupby=" group by GroupN,GroupV,GroupN2,GroupV2";
						    			orderby=" order by A0000,A00002,recidx";
									}
						    		else{
						    			groupby=" group by GroupN,GroupV,GroupN2,GroupV2";
						    			orderby=" order by A0000,GROUPN2,recidx";
						    		}
								}
							}else{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									groupby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV ";
									orderby="";
								}
								else
								{
									groupby=" group by GroupN,GroupV";
						        	orderby=" order by A0000";
								}
							}
						}
						
					}else{
						if (!isGroupTerm&&!isGroupTerm2) {
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								groupby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV ";
								orderby="";
							}
							else{
						    	if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						    	{
						    		if(GROUPFIELD2.trim().length()>4)
						    		{
							     		if(orgA0000Order2){
								    		tempSql.append(",(select A0000 from organization where codeitemid="+
								    				tableName+".GroupN2) AS A00002 ");
								    		orderby=" order by GroupN,A00002,recidx";
							     		}else
							    		{
							    			orderby=" order by GroupN,GroupN2,recidx";
							    		}
						    		}
							    	else
						    		{
						    			orderby=" order by GroupN,GroupN2,recidx";
							    	}
						    	}else {
                                    orderby=" order by GroupN,recidx";
                                }
							}
						} else if (isGroupTerm||isGroupTerm2) {
							if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
							{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									groupby=" group by "+Sql_switcher.month("A00z0")+",GroupN,GroupV,GroupN2,GroupV2";
									orderby=" order by GroupN,GroupN2 ";
								}else
								{
									if(orgA0000Order2){
						    			tempSql.append(",(select A0000 from organization where codeitemid="+
						    					tableName+".GroupN2) AS A00002 ");
						    			/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 start */
						    			if(isCode) {
						    				tempSql.append(",(select A0000 from codeitem where codesetid='"+itemGroup.getCodesetid()+"' and codeitemid="+
						    						tableName+".GroupN) AS A00001");
						    				orderby=" order by GroupN,A00001,A00002,GroupN2 ";
						    			}else {
						    				orderby=" order by GroupN,A00002,GroupN2 ";
						    			}
						    			/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 end */
						    			groupby=" group by GroupN,GroupV,GroupN2,GroupV2";
						    		}
							    	else{
							    		groupby=" group by GroupN,GroupV,GroupN2,GroupV2";
							    		orderby=" order by GroupN,GroupN2 ";
							    	}
								}
							}else{
								if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
								{
									groupby=" group by "+Sql_switcher.month("A00z0")+",GroupN,GroupV";
									orderby=" order by GroupN ";
								}else
								{
									/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 start */
									if(isCode) {
										tempSql.append(",(select A0000 from codeitem where codesetid='"+itemGroup.getCodesetid()+"' and codeitemid="+
					    						tableName+".GroupN) AS A00001");
					    				orderby=" order by GroupN,A00001 ";
					    			}else {
					    				orderby=" order by GroupN ";
					    			}
									/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 end */
									groupby=" group by GroupN,GroupV";
								}
							}
						}
					}
				}else{
					if (!isGroupTerm&&!isGroupTerm2) {
						if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
						{
							groupby=" group by "+Sql_switcher.month("a00z0")+" ,GroupN,GroupV ";
							orderby="";
						}
						else{
				    		if(this.isGroupV2&&this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
				    		{
				     			if(GROUPFIELD2.trim().length()>4)
					    		{
						    		if(orgA0000Order2){
						    			tempSql.append(",(select A0000 from organization where codeitemid="+
						    					tableName+".GroupN2) AS A00002 ");
							    		orderby=" order by GroupN,A00002,recidx";
						    		}else
						    		{
						    			orderby=" order by GroupN,GroupN2,recidx";
							    	}
						    	}
						    	else
					     		{
						     		orderby=" order by GroupN,GroupN2,recidx";
					    		}
				    		}else {
                                orderby=" order by GroupN,recidx";
                            }
						}
					} else if (isGroupTerm||isGroupTerm2) {
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								groupby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV,GroupN2,GroupV2";
								orderby=" order by GroupN,GroupN2 ";
							}
							else
							{
					    		if(orgA0000Order2){
					    			tempSql.append(",(select A0000 from organization where codeitemid="+
					    					tableName+".GroupN2) AS A00002 ");
					    			groupby=" group by GroupN,GroupV,GroupN2,GroupV2";
						    		orderby=" order by GroupN,A00002,GroupN2 ";
					    		}
						    	else{
						    		groupby=" group by GroupN,GroupV,GroupN2,GroupV2";
						    		orderby=" order by GroupN,GroupN2 ";
						    	}
							}
						}
						else{
							if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))
							{
								groupby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV";
								orderby=" order by GroupN ";
							}else
							{
								groupby=" group by GroupN,GroupV";
				    	    	orderby=" order by GroupN ";
							}
						}
					}
				}
			}
			else
			{
				if("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount)){
					groupby=" group by "+Sql_switcher.month("a00z0")+",GroupN,GroupV";
					orderby=" order by "+Sql_switcher.month("a00z0");
				}
			}
			//(select sum(1) from su_muster_1012 muster_b where muster_b.groupn=su_muster_1012.groupn and muster_b.a0000 <= su_muster_1012.a0000 ) as recidx  分组自增长查询
			tempSql.append(",recidx from "+tableName);
			/*if(!infor_Flag.equals("1"))
			tempSql.append(",(select sum(1) from "+tableName+" muster_b where muster_b.groupn="+tableName+".groupn and muster_b.a0000 <= "+tableName+".a0000 ) as recidx");
			else
				tempSql.append(",recidx");
			tempSql.append(" from "+tableName);*/
			if (isGroupTerm||isGroupTerm2||("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))) {
				tempSql = new StringBuffer("");
				tempSql.append(getGroupSql(bottomnList, infor_Flag, tableName,isGroupPoint,tabid));
			}

			/* 权限控制 */
			tempSql.append(this.privConditionStr);
			if("stipend".equals(infor_Flag)&&this.a0100.length()>0)
			{
				if(tempSql.toString().substring(tempSql.toString().toUpperCase().lastIndexOf("FROM")).toLowerCase().indexOf("where")>0){
					tempSql.append(" and  a0100='"+this.a0100+"' ");
				}else{
					tempSql.append(" where a0100='"+this.a0100+"'");
				}
			}
			if(!needCalcAfterSum(bottomnList)) {
                tempSql.append(groupby);
            }
			tempSql.append(orderby);

			sql.append(tempSql.substring(1));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql.toString();
	}

    /**
     * 花名册取数sql, 用于分栏或不分栏花名册
     * @param tableName
     * @return
     * @throws GeneralException
     */
    public String getMusterSqlAll(String tableName) throws GeneralException {
        String sql = null;           
        if("1".equals(column)||"2".equals(column)) {// 横分或纵分
            /* 表头列集合 && 表体总高 && 表体总宽 && 表的左坐标及顶坐标 */
            ArrayList horizontalList = getHeaderList(tabid, "1");
            ArrayList h_headerList = (ArrayList) horizontalList.get(0);
            boolean isGroupTerm = false; // 是表内条件否有分组指标
            
            ArrayList aa_list=getSql(h_headerList,isGroupTerm,infor_Flag,tableName,isGroupPoint,tabid);
            String[] sql_temp=((String)aa_list.get(0)).split("/");
            sql = sql_temp[0];
        }               
        else {
            sql = getMusterSql(tableName,null);
        }
        if(/*!isGroupTerm&&*/!isGroupTerm2&&!"stipend".equals(infor_Flag)) {
            sql=getSqlSort(sql,tableName);
        }

        return sql;
    }    
    
    
    public ArrayList updateList(ArrayList tableHeaderList,float pix){
		ArrayList copylist=new ArrayList();
		ArrayList secBodyList=new ArrayList();
		for (int i = 0; i < tableHeaderList.size(); i++) {
			String[] arr=((String[])tableHeaderList.get(i)).clone();//克隆数组内的参数  
			copylist.add(arr);
		}
		for (int j = 0; j < copylist.size(); j++) {
			String[] secTemp=(String[])copylist.get(j);
			String[] args=(String[])copylist.get(copylist.size()-1);
			//第二列第一个单元格 距离左边起始位置=第一列最后一个单元格距左位置+最后一个单元格宽度
			float startRleft=Float.parseFloat(args[2])+Float.parseFloat(args[4]);
			if(this.tableTypeFlag) {
				startRleft+=pix;
			}
			float gridNo=Float.parseFloat(args[0]);
			if(j==0){//第二列标题 第一个单元格基于第一列标题最后一个单元格的位置开始
				secTemp[2]=startRleft+"";
			}else{
				String[] lastArgs=(String[])secBodyList.get(j-1);
				secTemp[2]=String.valueOf(Float.parseFloat(lastArgs[2]) +Float.parseFloat(lastArgs[4]));
			}
			gridNo=gridNo+j+1;
//			secTemp[0]=String.valueOf(gridNo);
			secBodyList.add(secTemp);
//			tableHeaderList.add(secTemp);
		}
//		tableHeaderList.addAll(secBodyList);
	
		return secBodyList;
    }
	/**
	 * 显示web(人员、职位、单位)页面
	 * 
	 * @param pageRows
	 *            每页行数
	 * @param currpage
	 *            哪一页
	 * @param isGroupPoint
	 *            是否选用分组指标 1:选用
	 * @param groupPoint
	 *            已选的分组指标
	 * @param isAutoCount
	 *            0:为自动计算 1:用户指定
	 * @param pageRows
	 *            n:为用户指定的每页行数
	 * @param zeroPrint
	 *            0:不为零打印 1：零打印
	 * @param emptyRow
	 *            0：空行不打印 1：空行打印
	 * @param column
	 *            0:不分栏 1：横向分栏 2：纵向分栏
	 * @param pix
	 *            栏间距的像素
	 * @param columnLine
	 *            //分隔线 1：为有分隔线
	 * @param infor_Flag
	 *            信息群标示
	 * @param printGrid 
	 * 			  打印格线  1：打印  0：不打印
	 * @return list[0]:表头 list[1]:表体
	 */
	public ArrayList getHumster(String infor_Flag, String tabid,
			String isGroupPoint, String groupPoint, String tableName,
			String pageRows, String currpage, String isAutoCount,
			String zeroPrint, String emptyRow, String column, String pix,
			String columnLine, String user, String dbpre, String history,
			String year, String month, String count, UserView userView,
			String operateMethod,String printGrid,String modelFlag) throws GeneralException {
		this.modelFlag=modelFlag;
		this.userView=userView;
		this.isGroupPoint=isGroupPoint;
		this.infor_Flag=infor_Flag;
		this.printGrind=printGrid;
		this.column=column;
		setGroupV2(isHaveGroup2(tableName));
		pageRows=pageRows!=null&&pageRows.trim().length()>0?pageRows:"0";
		pageRows= "0".equals(pageRows)?"20":pageRows;
		ArrayList list = new ArrayList();
		HmusterBo hmusterBo = new HmusterBo(this.conn);
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			String tableFontStyle = "select FontName,FontEffect,FontSize,tmargin from muster_name where tabid=" + tabid;
			String[] fontStyle = new String[3];
			rowSet = dao.search(tableFontStyle);
			if (rowSet.next()) {
				fontStyle[0] = rowSet.getString("FontName");
				fontStyle[1] = rowSet.getString("FontEffect");
				fontStyle[2] = rowSet.getString("FontSize");
				this.t_space=Integer.parseInt(round(String.valueOf(rowSet.getFloat("tmargin")/this.precent),0));
			}
			ArrayList aList = getBottomnList(tabid);
			int nOperation = ((Integer) aList.get(1)).intValue();
			
			/* 权限控制 是否显示兼职人员库*/
			if("3".equals(modelFlag)|| "21".equals(modelFlag)|| "41".equals(modelFlag)) {
                this.privConditionStr=getPrivCondition(infor_Flag,dbpre);
            }
			
			if ("0".equals(column)) // 不分栏
			{
				list = getNHumsterHtml(aList, currpage, user, zeroPrint,
						emptyRow, pageRows, isAutoCount, column, isGroupPoint,
						tableName, tabid, history, year, month, count,
						infor_Flag, hmusterBo, dbpre, fontStyle,
						operateMethod,printGrid);

			} else if ("1".equals(column)) // 横分
			{
				//横向分栏 不支持 空行打印 列合并 行合并 页小计 页累计 分组合计 总计 暂改为只要为横向分栏则直接取消这些设置项 保证页面显示格式正确
				if("0".equals(dataarea)){
					emptyRow="0";//取消空行打印
				}
				/* 表头列集合 && 表体总高 && 表体总宽 && 表的左坐标及顶坐标 */
				ArrayList horizontalList = getHeaderList(tabid, "1");
				ArrayList h_headerList = (ArrayList) horizontalList.get(0);
				float h_tableHeight = ((Float) horizontalList.get(1)).floatValue();
				float h_tableWidth = ((Float) horizontalList.get(2)).floatValue();
				float[] h_ltPix = (float[]) horizontalList.get(3);
				
				boolean isGroupTerm = false; // 是表内条件否有分组指标

				/* 查询语句 */
				ArrayList aa_list=getSql(h_headerList,isGroupTerm,infor_Flag,tableName,isGroupPoint,tabid);
				String[] sql_temp=((String)aa_list.get(0)).split("/");
				if("1".equals((String)aa_list.get(1))) {
                    isGroupTerm=true;
                }
				
				StringBuffer h_sql=new StringBuffer(sql_temp[0]);
			  
				/** *************-------------------**************** */
				ArrayList titleInfoList = getTableBodyHeight(tabid, 0, 0, lt,column);
				ArrayList titelList = (ArrayList) titleInfoList.get(0); // 表头的标题信息列表
				int bodyHeight = Integer.parseInt((String) titleInfoList.get(1)); // 表体高度（像素）
				/** ************--------------------*************** */
				if (pix == null || "".equals(pix)) {
                    pix = "0";
                }
				int h_rows = getHorizontalrows(bodyHeight, h_tableHeight+1, pix,nOperation); // 得到每页的行数；

				if ("1".equals(isAutoCount)) {
						h_rows = Integer.parseInt(pageRows);
				}

				String header = "";
				if("1".equals(dataarea)){
					float r_bottomn=((Float)aList.get(7)).floatValue();   // 表头底边所在位置
					ArrayList tabHeaderList = getHmusterTableHead(tabid,
							h_headerList,r_bottomn,printGrid,h_tableHeight); // 根据tabid,动态生成高级花名册表头,及表的最左端、最上端、表头的总高
					header = (String) tabHeaderList.get(0);
				}

				// 得到表体内容 & 当前页的分组指标 & 当前页的行数 & 显示的临时照片名称列表 (横向分栏)
				int totalPage=getHmusterTotalPages(isGroupTerm, infor_Flag,
						isGroupPoint, tableName, h_rows,
						dbpre);
				if(Integer.parseInt(currpage)>=totalPage) {
                    currpage=String.valueOf(totalPage);
                }
				this.totalPage=totalPage;
				ArrayList bodyList = getHorizontalBodyContext(isGroupTerm,
						column, columnLine, h_tableHeight, h_tableWidth,
						bodyHeight, Float.parseFloat(pix), isGroupPoint,
						emptyRow, zeroPrint, h_headerList, h_sql.toString(),
						h_rows, Integer.parseInt(currpage), tableName, dbpre,
						aList);
				String groupV = (String) bodyList.get(1);
				int factRows = ((Integer) bodyList.get(2)).intValue();
                
				String a_height = String.valueOf(h_tableHeight+ Float.parseFloat(pix));
				a_height = a_height.substring(0, a_height.indexOf("."));
				ArrayList titleList = getTitleHtml(isGroupTerm, column, Integer
						.parseInt(a_height), factRows - 2, titelList, user,
						String.valueOf(currpage), h_rows, infor_Flag,
						tableName, isGroupPoint, groupV, h_ltPix[1],
						h_tableHeight, history, year, month, count,
						dbpre,totalPage,tabid);
			    this.setDivHeight((factRows+1)*(h_tableHeight+2)+(this.t_space*2));
				list.add((String) titleList.get(0)); // 上标题html
				list.add(header); // 表头html
				list.add((String) bodyList.get(0)); // 表体html
				list.add((String) titleList.get(1)); // 下标题html
				list.add(getTurnPageCode(currpage, (String) titleList.get(2),
						(String) titleList.get(3), h_ltPix[0], operateMethod)); // 翻页符号
				list.add((ArrayList) bodyList.get(3)); // 临时照片的名称列表，已备删除

			} else if ("2".equals(column)) // 纵分
			{
				
				/*****
				 * 纵向分栏添加区分卡片式与列表式
				 * 是否分栏0|1;1横向|2纵向;间距;打印分隔线;1记录方式|0列表方式;1多行数据区|0最底行是数据区
				 * <SPILTCOLUMN>1:2:0:0:0:0:</SPILTCOLUMN>  列表式
				 * <SPILTCOLUMN>1:2:0:0:1:0:</SPILTCOLUMN>  卡片式
				 * */
				HmusterXML hmxml = new HmusterXML(this.conn,tabid);
				String iscolum=hmxml.getValue(HmusterXML.RECORDWAY);//判断纵向分栏是卡片式还是列表式
				if("0".equals(iscolum)){
					this.tableTypeFlag=true;
				}
				/* 表头列集合 && 表体总高 && 表体总宽 && 表的左坐标及顶坐标 */
				ArrayList horizontalList = getHeaderList(tabid, "1"); //坐标信息
				ArrayList h_headerList = (ArrayList) horizontalList.get(0);// 取出库中存储的指标信息 集合
				float h_tableHeight = ((Float) horizontalList.get(1)).floatValue(); //表格的宽 高
				float h_tableWidth = ((Float) horizontalList.get(2)).floatValue();
				float[] h_ltPix = (float[]) horizontalList.get(3);
				
				boolean isGroupTerm = false; // 是表内条件否有分组指标
				
				ArrayList keyList = new ArrayList(); // 关键字段列表
				if ("1".equals(infor_Flag)) {
                    keyList.add("a0100");
                } else if ("2".equals(infor_Flag)) {
                    keyList.add("b0110");
                } else if ("3".equals(infor_Flag)) {
                    keyList.add("e01a1");
                }

				/* 查询语句 */
				ArrayList aa_list=getSql(h_headerList,isGroupTerm,infor_Flag,tableName,isGroupPoint,tabid);
				String[] sql_temp=((String)aa_list.get(0)).split("/");
				if("1".equals((String)aa_list.get(1))) {
                    isGroupTerm=true;
                }
				
				StringBuffer h_sql=new StringBuffer(sql_temp[0]);//查询高级花名册数据表
				float r_bottomn=((Float)aList.get(7)).floatValue();
				ArrayList tabHeaderList = getHmusterTableHead(tabid,//表头
						h_headerList,r_bottomn,Float.parseFloat(pix),printGrid);
				/** *************-------------------**************** */
				ArrayList titleInfoList = getTableBodyHeight(tabid, 0, 0, lt,column);
				ArrayList titelList = (ArrayList) titleInfoList.get(0); // 表头的标题信息列表
				int bodyHeight = Integer.parseInt((String) titleInfoList.get(1)); // 表体宽度（像素）
				/** ************--------------------*************** */
				if (pix == null || "".equals(pix)) {
                    pix = "0";
                }
				int h_rows = getHorizontalrows(bodyHeight, h_tableWidth, pix,nOperation); // 得到每页的行数；
				if(this.tableTypeFlag) {
                    h_rows=getHorizontalrows(bodyHeight, h_tableHeight, pix,nOperation); // 得到每页的行数；
                }
				if ("1".equals(isAutoCount)) {
//					if (Integer.parseInt(pageRows) < h_rows)
						h_rows = Integer.parseInt(pageRows);
				}
				// 得到表体内容 & 当前页的分组指标 & 当前页的行数 & 显示的临时照片名称列表 (纵向分栏)
				int totalPage=0;
				if(this.tableTypeFlag){
					int colNum=793/(int)h_tableWidth;
					colNum=colNum==0?1:colNum;//计算纵分时页面上能放置几列表格 1122为A4纸宽度转换像素
					totalPage=getHmusterTotalPages(isGroupTerm, infor_Flag,isGroupPoint, tableName, colNum*h_rows,dbpre);
				}else {
                    totalPage=getHmusterTotalPages(isGroupTerm, infor_Flag,isGroupPoint, tableName, h_rows,dbpre);
                }
				if(Integer.parseInt(currpage)>=totalPage) {
                    currpage=String.valueOf(totalPage);
                }
				ArrayList bodyList = getHorizontalBodyContext(isGroupTerm,
						column, columnLine, h_tableHeight, h_tableWidth,
						bodyHeight, Float.parseFloat(pix), isGroupPoint,
						emptyRow, zeroPrint, h_headerList, h_sql.toString(),
						h_rows, Integer.parseInt(currpage), tableName, dbpre,
						aList);
				String groupV = (String) bodyList.get(1);
				int factRows = ((Integer) bodyList.get(2)).intValue();
				if(this.tableTypeFlag&&totalPage==Integer.parseInt(currpage))//纵分列表式 最后一页factrows 改为每页行数
                {
                    factRows=h_rows+2;
                }
				ArrayList titleList = getTitleHtml(isGroupTerm, column,(int)h_tableHeight ,
						factRows - 2, titelList, user,
						String.valueOf(currpage), h_rows, infor_Flag,
						tableName, isGroupPoint, groupV, h_ltPix[1],
						h_tableHeight, history, year, month, count,
						dbpre,totalPage,tabid);
				this.setDivHeight((factRows+1)*(h_tableHeight+2)+(this.t_space*2));
				list.add((String) titleList.get(0)); // 上标题html
				list.add((String)tabHeaderList.get(0)); // 表头html
				list.add((String) bodyList.get(0)); // 表体html
				list.add((String) titleList.get(1)); // 下标题html
				list.add(getTurnPageCode(currpage, (String) titleList.get(2),
						(String) titleList.get(3), h_ltPix[0], operateMethod)); // 翻页符号
				list.add((ArrayList) bodyList.get(3)); // 临时照片的名称列表，已备删除

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * liuy 2015-2-28 判断照片是否该自动合并
	 * @param tabid
	 * @return
	 */
	public boolean isPhotoColMerge(String tabid){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select GridNo,Hz,Rleft,RTop+");
		sql.append(this.t_space);
		sql.append(",RWidth,RHeight,Slope,Flag,noperation,Field_Type,fontName,");
		sql.append("fontEffect,fontSize,align,L,T,R,B,SETNAME,Field_name,nhide,");
		sql.append("ExtendAttr from muster_cell where tabid=");
		sql.append(tabid);
		sql.append(" order by Rleft,RTop ");
		boolean PhotoColMerge = false;
		try {
			rowSet = dao.search(sql.toString());
			while (rowSet.next()) {
				String extendAttr = rowSet.getString("ExtendAttr");
				extendAttr=extendAttr!=null?extendAttr:"";
				String ColMerge = "false"; //是否合并
				if(extendAttr.indexOf("<ColMerge>")!=-1){
					ColMerge = extendAttr.substring(extendAttr.indexOf("<ColMerge>")+"<ColMerge>".length(),extendAttr.indexOf("</ColMerge>"));
					if(!PhotoColMerge) {
                        PhotoColMerge = Boolean.parseBoolean(extendAttr.substring(extendAttr.indexOf("<ColMerge>")+"<ColMerge>".length(),extendAttr.indexOf("</ColMerge>")));
                    }
				}
				if(PhotoColMerge) {
                    break;
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			PubFunc.closeResource(rowSet);
		}
		return PhotoColMerge;
	}
	
	/**
	 * 生成不分栏的花名册html
	 * 
	 * @param pageRows  每页行数
	 * @param currpage  哪一页
	 * @param isGroupPoint 是否选用分组指标 1:选用
	 * @param isAutoCount  0:为自动计算 1:用户指定
	 * @param pageRows     n:为用户指定的每页行数
	 * @param zeroPrint    0:不为零打印 1：零打印
	 * @param emptyRow     0：空行不打印 1：空行打印
	 * @param column       0:不分栏 1：横向分栏 2：纵向分栏
	 * @param infor_Flag   信息群标示
	 * @param user         用户名称
	 * @param zeroPrint    是否零打印
	 * @param tableName    临时表名称
	 * @param tabid        花名册id
	 * @param history      1:最后一条历史纪录 3：某次历史纪录 2：部分历史纪录
	 * @param year    年
	 * @param month   月
	 * @param count   次
	 * @param hmusterBo
	 * @param dbpre   人员库
	 * @param fontStyle 字体类型
	 * @param printGrid 打印格线
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getNHumsterHtml(ArrayList aList, String currpage,
			String user, String zeroPrint, String emptyRow, String pageRows,
			String isAutoCount, String column, String isGroupPoint,
			String tableName, String tabid, String history, String year,
			String month, String count, String infor_Flag, HmusterBo hmusterBo,
			String dbpre, String[] fontStyle,
			String operateMethod,String printGrid) throws GeneralException {

		ArrayList list = new ArrayList();
		StringBuffer existColumn = new StringBuffer("");
		StringBuffer sql2 = new StringBuffer("select "); // 求得每个字符列内容的最大长度的sql语句
		ArrayList list2 = new ArrayList(); // 与上面相对应得列
		ArrayList tableHeaderList = new ArrayList();
		ArrayList list3 = new ArrayList(); // 哪些列有页小计或累计
		int nOperation = 0; // 汇总标示
		float tableBodyWidth = 0; // 表体的宽度

		StringBuffer tempSql2 = new StringBuffer("");
		ArrayList bottomnList = null;

		try {
			bottomnList = (ArrayList) aList.get(0); // 取得表头最底端的列
			String isPhoto ="";

			nOperation = ((Integer) aList.get(1)).intValue();
			list3 = (ArrayList) aList.get(2); // 哪些列有页小计或累计
			tableBodyWidth = ((Float) aList.get(3)).floatValue(); // 得到表体的宽度（单位：像素）
			tableHeaderList = (ArrayList) aList.get(4);
			float tableHeaderHeight = ((Float) aList.get(5)).floatValue();
			float r_bottomn=((Float)aList.get(7)).floatValue();   // 表头底边所在位置
			boolean isGroupTerm = false; // 是表内条件否有分组指标
			boolean PhotoColMerge = isPhotoColMerge(tabid); //照片是否合并
			/* 根据条件生成查询语句 和 求得每个字符列内容的最大长度的sql语句 */
			for (Iterator t = bottomnList.iterator(); t.hasNext();) {
				String[] temp = (String[]) t.next();
				if(temp[20]!=null&& "1".equals(temp[20])) {
                    continue;
                }
				String fieldName = temp[19];
				String extendAttr = temp[20];
				extendAttr=extendAttr!=null?extendAttr:"";
				String ColMerge = "false"; //是否合并
				String ColMergeByMain = "false"; //是否按人员,单位,职位合并
				if(extendAttr.indexOf("<ColMerge>")!=-1) {
                    ColMerge = extendAttr.substring(extendAttr.indexOf("<ColMerge>")+"<ColMerge>".length(),extendAttr.indexOf("</ColMerge>"));
                }
				if(extendAttr.indexOf("<ColMergeByMain>")!=-1) {
                    ColMergeByMain = extendAttr.substring(extendAttr.indexOf("<ColMergeByMain>")+"<ColMergeByMain>".length(),extendAttr.indexOf("</ColMergeByMain>"));
                }
				//liuy 2015-2-27 7561：花名册中一个人多条记录时姓名可以设置合并单元格，但是照片不能合并，照片应该自动合并
				if(temp[7] != null && ("P".equals(temp[7]))&&PhotoColMerge){
					ColMerge = "True";
					ColMergeByMain = "True";
				}//liuy 2015-2-27 end
				String RowMerge="false";
				if(extendAttr.indexOf("<RowMerge>")!=-1){
					RowMerge = extendAttr.substring(extendAttr.indexOf("<RowMerge>")+"<RowMerge>".length(),extendAttr.indexOf("</RowMerge>"));
				}
				
				itemHeArr +=fieldName+":"+ColMerge+":"+ColMergeByMain+",";
				if (temp[7] != null && ("G".equals(temp[7])|| "R".equals(temp[7]))) {
					isGroupTerm = true;
				}
				if (temp[7] != null && ("E".equals(temp[7]))) {
					isGroupTerm2= true;
				}
				
			   /**H为文本型字段，原来过滤掉，，先加上*/
				if (temp[7] != null && !"".equals(temp[7])&& !"G".equals(temp[7])&& !"E".equals(temp[7]) && !"S".equals(temp[7])&& !"H".equals(temp[7]) && !"P".equals(temp[7])&& !"R".equals(temp[7])) {
					existColumn.append(",");
					existColumn.append(temp[0]);
				}
				if ("P".equals(temp[7])) {
                    isPhoto = temp[4];
                }
				if (("A".equals(temp[7]) || "B".equals(temp[7]) || "K".equals(temp[7]))&& ("A".equals(temp[9]) || "M".equals(temp[9]))) {
						if ("M".equals(temp[9])||("A0101".equalsIgnoreCase(temp[19])&&!"1".equals(infor_Flag)))
						{
							tempSql2.append(","+Sql_switcher.isnull("max("+ Sql_switcher.datalength("C" + temp[0])+ ")","0")+" C" + temp[0]);
						}else{
							tempSql2.append(",max("+ Sql_switcher.length("C" + temp[0])+ ") C" + temp[0]);
						}

					list2.add(temp);
				}
			}
			existColumn.append(",");
			
			if (isGroupPoint != null && "1".equals(isGroupPoint)) {
				ContentDAO dao = new ContentDAO(this.conn);
				dao.update("update "+tableName+" set groupN='' where groupN is null");// FIXME 慢
				if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&this.isGroupV2) {
                    dao.update("update "+tableName+" set groupN2='' where groupN2 is null");
                }
			}
			boolean isColumn = true; // 最底层是否有内容
			if (tempSql2.length() < 3) {
                isColumn = false;
            }

			tempSql2.append(" from ");
			tempSql2.append(tableName);

			if (isGroupTerm||isGroupTerm2||("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))) {
				ContentDAO dao = new ContentDAO(this.conn);
				dao.update("update "+tableName+" set groupN='' where groupN is null");
				if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&isGroupPoint!=null&& "1".equals(isGroupPoint)) {
                    dao.update("update "+tableName+" set groupN2='' where groupN2 is null");
                }
			}

			String sql=getMusterSql(tableName, bottomnList);
			sql2.append(tempSql2.substring(1));
			ArrayList tabHeaderList = getHmusterTableHead(tabid,
					tableHeaderList,r_bottomn,0,printGrid); // 根据tabid,动态生成高级花名册表头,及表的最左端、最上端、表头的总高
			String header = (String) tabHeaderList.get(0);
			float[] lth = (float[]) tabHeaderList.get(1);
			lth[2] = tableHeaderHeight;
			int height = 0;
			if (isColumn) {
                height = getMaxColumnHeight(sql2.toString(), list2); // 得到查询的所有数据中最高列的高度
            } else {
                height = 32;
            }
			/** *************-------------------**************** */
			ArrayList titleInfoList = getTableBodyHeight(tabid, lth[1],
					tableHeaderHeight, lt, column);
			ArrayList titelList = (ArrayList) titleInfoList.get(0); // 表头的标题信息列表
			int bodyHeight = Integer.parseInt((String) titleInfoList.get(1)); // 表体高度（像素）

			/** ************--------------------*************** */
 			if (isPhoto.trim().length()>0) {
				int a_width=0;
				if(isPhoto.indexOf(".")!=-1) {
                    a_width=(Integer.parseInt(isPhoto.substring(0,isPhoto.indexOf("."))));
                } else {
                    a_width=Integer.parseInt(isPhoto);
                }
				int aheight=a_width+a_width/2;
				if (height < aheight) {
                    height = aheight;
                }
			}

			int rows = getPageRows(tabid, height, isAutoCount, pageRows, lth,nOperation, bodyHeight); // 求出每页的行数
			this.pageRows=rows;
			
			if ("1".equals(isAutoCount)&& pageRows.equals(String.valueOf(rows))) {
				if (nOperation == 1 || nOperation == 2) {
                    height = bodyHeight / (rows + 1);
                }
				if (nOperation == 3) {
                    height = bodyHeight / (rows + 2);
                } else {
                    height = bodyHeight / rows;
                }
			}

			ArrayList keyList = new ArrayList(); // 关键字段列表
			if ("1".equals(infor_Flag)) {
                keyList.add("a0100");
            } else if ("2".equals(infor_Flag)) {
                keyList.add("b0110");
            } else if ("3".equals(infor_Flag)) {
                keyList.add("e01a1");
            }

			ArrayList fieldList = getFieldandFieldsList(list3);
			String[] field=(String[])fieldList.get(0);
			String[] fields=(String[])fieldList.get(1);

			if(!"stipend".equalsIgnoreCase(infor_Flag)){
				String infor_Flag2 = infor_Flag;
				if ("81".equals(modelFlag)) 
				{
					ContentDAO dao = new ContentDAO(this.conn);
					String fieldsetid=getFieldSetids(dao, tabid);
					
					if ("Q07".equalsIgnoreCase(fieldsetid) || "Q09".equalsIgnoreCase(fieldsetid)) 
					{
						infor_Flag2 = "2";
					}else 
					{
						infor_Flag2 = "1";
					}
				}
				totalPage = getHmusterTotalPages(isGroupTerm, infor_Flag2,isGroupPoint, tableName, rows,dbpre);
			}
			if(currpage!=null&&currpage.trim().length()>0&&Integer.parseInt(currpage)>=totalPage) {
                currpage=String.valueOf(totalPage);
            }
			ArrayList lists = getBodyContext(isGroupTerm, isGroupPoint,existColumn.toString(), emptyRow, zeroPrint, lth,
					bottomnList, sql, rows, Integer.parseInt(currpage), keyList, nOperation, height,
					field, fields, tableBodyWidth, fontStyle, tableName, dbpre,printGrid,infor_Flag);
			String tabBodyContext = (String) lists.get(0);
			String groupV = (String) lists.get(1);
			int factRows = ((Integer) lists.get(2)).intValue(); // 表体的实际行数
			
			this.setDivHeight((factRows+1)*(height+2)+(this.t_space*2));
			ArrayList titleList = getTitleHtml(isGroupTerm, column, height,
					factRows-1, titelList, user, String.valueOf(currpage), rows,
					infor_Flag, tableName, isGroupPoint, groupV, lth[1],
					lth[2], history, year, month, count, dbpre,totalPage,tabid);
			list.add((String) titleList.get(0)); // 上标题html
			list.add(header); // 表头html
			list.add(tabBodyContext); // 表体html
			list.add((String) titleList.get(1)); // 下标题html
			if(!"stipend".equals(infor_Flag)){
				String page = getTurnPageCode(currpage, (String) titleList.get(2),
						(String) titleList.get(3), lth[0], operateMethod);
				list.add(page); // 翻页符号
			}else {
                list.add("");
            }
			list.add((ArrayList) lists.get(3)); // 临时照片的名称列表，已备删除

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;

	}
	
	/**
	 * 获取数据源表的名字
	 * @param dao
	 * @param tabid
	 * @return
	 */
	public String getFieldSetids(ContentDAO dao,String tabid){
		String fieldsetid="";
		ResultSet rs = null;
		try {
			rs = dao.search("select SetName,Field_Name from muster_cell where Tabid='"+tabid+"'");
			while(rs.next()){
				String setname = rs.getString("SetName");
				if(setname!=null&&setname.trim().length()>0)
				{
					fieldsetid=setname;
					break;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs!=null) 
			{
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return fieldsetid;
	}

	private void getTotalSumFlds(ArrayList bottomnList, String infor_Flag,
			String tableName, String isGroupPoint,String tabid, StringBuffer sumflds)
	{
		/* 根据条件生成查询语句 和 求得每个字符列内容的最大长度的sql语句 */
		// GridNo,Hz,Rleft,RTop,RWidth,RHeight,Slope,Flag,noperation,Field_Type,fontName,
		String a_temp = "";
		if ("1".equals(infor_Flag)|| "stipend".equals(infor_Flag)|| "81".equals(this.modelFlag)|| "5".equals(this.modelFlag)) {
            a_temp = "a0100";
        } else if ("2".equals(infor_Flag)) {
            a_temp = "b0110";
        } else if ("3".equals(infor_Flag)) {
            a_temp = "e01a1";
        }
		if("1".equals(infor_Flag)|| "2".equals(infor_Flag)|| "3".equals(infor_Flag)|| "stipend".equals(infor_Flag)){
			sumflds.append(",max("+a_temp+") as "+a_temp);
		}
		for (Iterator t = bottomnList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			String extendattr=temp[20]==null?"":temp[20].toUpperCase();
			if(extendattr.indexOf("GROUPSUM")!=-1&&("N".equalsIgnoreCase(temp[9])))
			{
				String groupSum = extendattr.substring(extendattr.indexOf("<GROUPSUM>")+"<GROUPSUM>".length(),extendattr.indexOf("</GROUPSUM>"));
				/**求和*/
				/*if(groupSum.equals("0"))
				{
					sumflds.append(" ,sum(C"+temp[0]+") C"+temp[0]);
				}else if(groupSum.equals("1"))
				{
					sumflds.append(" ,avg(C"+temp[0]+") C"+temp[0]);
				}
				else if(groupSum.equals("2"))
				{
					sumflds.append(" ,max(C"+temp[0]+") C"+temp[0]);
				}
				else if(groupSum.equals("3"))
				{
					sumflds.append(" ,min(C"+temp[0]+") C"+temp[0]);
				}
				else if(groupSum.equals("4"))
				{
					sumflds.append(" ,max(C"+temp[0]+") C"+temp[0]);
				}*/
				sumflds.append(" ,sum(C"+temp[0]+") C"+temp[0]);// 全部求和
			}
			else
			{
	    		if (temp[9] != null && "N".equals(temp[9])) {
	    			if("stipend".equals(infor_Flag)&&temp[19]!=null&&temp[19].toUpperCase().endsWith("Z1"))
	    			{
	    				sumflds.append(",max(C");
	    				sumflds.append(temp[0] + ") C" + temp[0]);
	    			}
	    			else
	    			{
	    				sumflds.append(",sum(C");
	    				sumflds.append(temp[0] + ") C" + temp[0]);
	    			}
		    	} else if("人员名单`".equals(temp[1])){
		    		sumflds.append(",' ' C" + temp[0]);
	    		
		    	}else if (temp[7] != null && "R".equals(temp[7])) {
		     		if("salary".equalsIgnoreCase(infor_Flag))
		       		{
		    			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                            sumflds.append(",count(distinct nbase||a0100) C" + temp[0]);
                        } else {
                            sumflds.append(",count(distinct nbase+a0100) C" + temp[0]);
                        }
			    	}
			    	else {
                        sumflds.append(",count(*) C" + temp[0]);
                    }
	     		} else if(temp[7]!=null&&!"S".equals(temp[7])&& !"G".equals(temp[7])&&!"E".equals(temp[7]) && !"H".equals(temp[7])&& !"P".equals(temp[7]) && !"R".equals(temp[7])&& !"".equals(temp[7]))
	    		{
	     			sumflds.append(",max( C" + temp[0]+") as C"+temp[0]);
    			}
	    		else {
                    sumflds.append(",' ' C" + temp[0]);
                }
			}
		}
		HmusterXML hmxml = new HmusterXML(this.conn,tabid);
		String GROUPFIELD=hmxml.getValue(HmusterXML.GROUPFIELD);
		GROUPFIELD=GROUPFIELD!=null?GROUPFIELD:"";
		FieldItem item = DataDictionary.getFieldItem(GROUPFIELD);
		boolean orgA0000Order=(item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())||
				"UM".equalsIgnoreCase(item.getCodesetid())||
				"@K".equalsIgnoreCase(item.getCodesetid())))||
				(GROUPFIELD.length()>4&&("B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))||
				"E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))||
				"E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))));
		
		String GROUPFIELD2=hmxml.getValue(HmusterXML.GROUPFIELD2);
		GROUPFIELD2=GROUPFIELD2!=null?GROUPFIELD2:"";
		FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
		boolean orgA0000Order2=(item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())||
				"UM".equalsIgnoreCase(item2.getCodesetid())||
				"@K".equalsIgnoreCase(item2.getCodesetid())))||
				(GROUPFIELD2.length()>4&&("B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))||
				"E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))||
				"E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))));
		
		if(this.isGroupV2)
		{
    	    if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&isGroupPoint != null && "1".equals(isGroupPoint))
	        {
    	    	sumflds.append(",GroupV2,GroupN2");
				if(orgA0000Order2){
					sumflds.append(",(select A0000 from organization where codeitemid="+tableName+".GroupN2) AS A00002 ");
				}
	        }
	        else{
	        	sumflds.append(",MAX(GroupV2) as GroupV2,MAX(GroupN2) as GroupN2");
	        }
		}
		if (isGroupPoint != null && "1".equals(isGroupPoint)){
			sumflds.append(",GroupN,GroupV");
			if(GROUPFIELD.trim().length()>4){
				if(orgA0000Order){
					sumflds.append(",(select A0000 from organization where codeitemid="+tableName+".GroupN) AS A0000 ");
				}
			}
		}
		sumflds.append(",max(recidx) as recidx ");
	}	
	
	private void getGroupFlds(ArrayList bottomnList, String infor_Flag,
			String tableName, String isGroupPoint,String tabid, StringBuffer sumflds, StringBuffer flds)
	{
		if(flds==null)  // 支持参数为null
        {
            flds=new StringBuffer("");
        }
		
		/* 根据条件生成查询语句 和 求得每个字符列内容的最大长度的sql语句 */
		// GridNo,Hz,Rleft,RTop,RWidth,RHeight,Slope,Flag,noperation,Field_Type,fontName,
		String a_temp = "";
		if ("1".equals(infor_Flag)|| "stipend".equals(infor_Flag)|| "81".equals(this.modelFlag)|| "5".equals(this.modelFlag)) {
		    if("1".equals(infor_Flag)) {
                a_temp = "NBASE,a0100";
            } else {
                a_temp = "a0100";
            }
		}
		else if ("2".equals(infor_Flag)) {
            a_temp = "b0110";
        } else if ("3".equals(infor_Flag)) {
            a_temp = "e01a1";
        }
		if("1".equals(infor_Flag)|| "2".equals(infor_Flag)|| "3".equals(infor_Flag)|| "stipend".equals(infor_Flag)){
		    if("1".equals(infor_Flag)) {
                sumflds.append(",max(NBASE) as NBASE,max(a0100) as a0100");
            } else {
                sumflds.append(",max("+a_temp+") as "+a_temp);
            }
			flds.append(","+a_temp);
		}
		for (Iterator t = bottomnList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			String extendattr=temp[20]==null?"":temp[20].toUpperCase();
			if(extendattr.indexOf("GROUPSUM")!=-1&&("N".equalsIgnoreCase(temp[9])))
			{
				String groupSum = extendattr.substring(extendattr.indexOf("<GROUPSUM>")+"<GROUPSUM>".length(),extendattr.indexOf("</GROUPSUM>"));
			    FieldItem item = DataDictionary.getFieldItem(temp[18]);
			    //指标精度默认为10位  插入计算公式走默认长度设置
			    int length = 8;
			    if(!"C".equalsIgnoreCase(temp[7])) {
			        //防止计算精度不够，默认长度加4位
	                length = item.getItemlength()+4;
			    }
                String scope = temp[6];
                scope = StringUtils.isEmpty(scope)?"0":scope;
				/**求和*/
				if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
				    if("0".equals(groupSum))
	                {
	                    sumflds.append(",cast(sum(C"+temp[0]+") as number("+length+","+scope+")) C"+temp[0]);
	                }else if("1".equals(groupSum))
	                {
	                    sumflds.append(",cast(avg(C"+temp[0]+") as number("+length+","+scope+")) C"+temp[0]);
	                }
	                else if("2".equals(groupSum))
	                {
	                    sumflds.append(",cast(max(C"+temp[0]+") as number("+length+","+scope+") )C"+temp[0]);
	                }
	                else if("3".equals(groupSum))
	                {
	                    sumflds.append(",cast(min(C"+temp[0]+") as number("+length+","+scope+")) C"+temp[0]);
	                }
	                else if("4".equals(groupSum))
	                {
	                    sumflds.append(",cast(max(C"+temp[0]+") as number("+length+","+scope+")) C"+temp[0]);
	                }
				}else {
				    if("0".equals(groupSum))
	                {
	                    sumflds.append(",sum(C"+temp[0]+") C"+temp[0]);
	                }else if("1".equals(groupSum))
	                {
	                    sumflds.append(",avg(C"+temp[0]+") C"+temp[0]);
	                }
	                else if("2".equals(groupSum))
	                {
	                    sumflds.append(",max(C"+temp[0]+") C"+temp[0]);
	                }
	                else if("3".equals(groupSum))
	                {
	                    sumflds.append(",min(C"+temp[0]+") C"+temp[0]);
	                }
	                else if("4".equals(groupSum))
	                {
	                    sumflds.append(",max(C"+temp[0]+") C"+temp[0]);
	                }
				}
				
			}
			else
			{
	    		if (temp[9] != null && "N".equals(temp[9])) {
	    		    FieldItem item = DataDictionary.getFieldItem(temp[18]);
	    		    int length = 8;
	                if(!"C".equalsIgnoreCase(temp[7])) {
	                    //防止计算精度不够，默认长度加4位
	                    length = item.getItemlength()+4;
	                }
	                String scope = temp[6];
	                scope = StringUtils.isEmpty(scope)?"0":scope;
	                if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
	                    
	                    if("stipend".equals(infor_Flag)&&temp[19]!=null&&temp[19].toUpperCase().endsWith("Z1"))
	                    {
	                        sumflds.append(",cast(max(C");
	                        sumflds.append(temp[0] + ") as number("+length+","+scope+") ) C" + temp[0]);
	                    }
	                    else
	                    {
	                        sumflds.append(",cast(sum(C");
	                        sumflds.append(temp[0] + ") as number("+length+","+scope+")) C" + temp[0]);
	                    }
	                }else {
	                    if("stipend".equals(infor_Flag)&&temp[19]!=null&&temp[19].toUpperCase().endsWith("Z1"))
	                    {
	                        sumflds.append(",max(C");
	                        sumflds.append(temp[0] + ") C" + temp[0]);
	                    }
	                    else
	                    {
	                        sumflds.append(",sum(C");
	                        sumflds.append(temp[0] + ") C" + temp[0]);
	                    }
	                }
		    	} else if("人员名单`".equals(temp[1])){
		    		sumflds.append(",' ' C" + temp[0]);
	    		
		    	}else if (temp[7] != null && "R".equals(temp[7])) {
		     		if("salary".equalsIgnoreCase(infor_Flag))
		       		{
		    			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                            sumflds.append(",count(distinct nbase||a0100) C" + temp[0]);
                        } else {
                            sumflds.append(",count(distinct nbase+a0100) C" + temp[0]);
                        }
			    	}
			    	else {
                        sumflds.append(",count(*) C" + temp[0]);
                    }
	     		} else if(temp[7]!=null&&!"S".equals(temp[7])&& !"G".equals(temp[7])&&!"E".equals(temp[7])&&!"H".equals(temp[7])
	     				&& !"P".equals(temp[7]) && !"R".equals(temp[7])&& !"".equals(temp[7])&&!"M".equals(temp[9]))
	    		{
	     			sumflds.append(",max( C" + temp[0]+") as C"+temp[0]);
    			}
	    		else {
                    sumflds.append(",' ' C" + temp[0]);
                }
			}
			flds.append(",C"+temp[0]);
		}
		HmusterXML hmxml = new HmusterXML(this.conn,tabid);
		String GROUPFIELD=hmxml.getValue(HmusterXML.GROUPFIELD);
		GROUPFIELD=GROUPFIELD!=null?GROUPFIELD:"";
		FieldItem item = DataDictionary.getFieldItem(GROUPFIELD);
		boolean orgA0000Order=(item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())||
				"UM".equalsIgnoreCase(item.getCodesetid())||
				"@K".equalsIgnoreCase(item.getCodesetid())))||
				(GROUPFIELD.length()>4&&("B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))||
				"E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))||
				"E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))));
		
		String GROUPFIELD2=hmxml.getValue(HmusterXML.GROUPFIELD2);
		GROUPFIELD2=GROUPFIELD2!=null?GROUPFIELD2:"";
		FieldItem item2 = DataDictionary.getFieldItem(GROUPFIELD2);
		boolean orgA0000Order2=(item2!=null&&("UN".equalsIgnoreCase(item2.getCodesetid())||
				"UM".equalsIgnoreCase(item2.getCodesetid())||
				"@K".equalsIgnoreCase(item2.getCodesetid())))||
				(GROUPFIELD2.length()>4&&("B0110".equalsIgnoreCase(GROUPFIELD2.substring(0,5))||
				"E0122".equalsIgnoreCase(GROUPFIELD2.substring(0,5))||
				"E01A1".equalsIgnoreCase(GROUPFIELD2.substring(0,5))));
		/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 start */
		boolean isCode = !((item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())||
				"UM".equalsIgnoreCase(item.getCodesetid())||
				"@K".equalsIgnoreCase(item.getCodesetid())))||
				(GROUPFIELD != null && GROUPFIELD.length()>4&&("B0110".equalsIgnoreCase(GROUPFIELD.substring(0,5))||
				"E0122".equalsIgnoreCase(GROUPFIELD.substring(0,5))||
				"E01A1".equalsIgnoreCase(GROUPFIELD.substring(0,5))))) && (item != null && item.isCode());
		/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 end */
		
		if(this.isGroupV2)
		{
    	    if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&isGroupPoint != null && "1".equals(isGroupPoint))
	        {
    	    	sumflds.append(",GroupV2,GroupN2");
	        	flds.append(",GroupV2,GroupN2");
				if(orgA0000Order2){
					sumflds.append(",(select A0000 from organization where codeitemid="+tableName+".GroupN2) AS A00002 ");
					flds.append(",A00002 ");
					/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 start */
					if(isCode) {
						sumflds.append(",(select A0000 from codeitem where codesetid='"+item.getCodesetid()+"' and codeitemid="+
								tableName+".GroupN) AS A00001");
					}
	    			/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 end */
				}
	        } else{
	        	sumflds.append(",MAX(GroupV2) as GroupV2,MAX(GroupN2) as GroupN2");
	        	flds.append(",GroupV2,GroupN2");
	        }
		}
		if (isGroupPoint != null && "1".equals(isGroupPoint)){
			sumflds.append(",GroupN,GroupV");
			flds.append(",GroupN,GroupV");
			if(GROUPFIELD.trim().length()>4){
				if(orgA0000Order){
					sumflds.append(",(select A0000 from organization where codeitemid="+tableName+".GroupN) AS A0000 ");
					flds.append(",A0000 ");
				}else{
					/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 start */
					if(!(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))&&!("stipend".equalsIgnoreCase(infor_Flag)&& "1".equals(this.groupCount))){
						if(isCode) {
							sumflds.append(",(select A0000 from codeitem where codesetid='"+item.getCodesetid()+"' and codeitemid="+
									tableName+".GroupN) AS A00001");
						}
					}
					/* 标识：3281 搜房：高级花名册画的自定义表、分析表，按某指标排序，默认按组织机构的顺序排列。 xiaoyun 2014-7-24 end */
				}
			}
		}
		sumflds.append(",max(recidx) as recidx ");
		flds.append(",recidx ");
	}

	// 取得包含分组指标的sql语句
	public String getGroupSql(ArrayList bottomnList, String infor_Flag,
			String tableName, String isGroupPoint,String tabid) {
		StringBuffer tempSql = new StringBuffer("");
		StringBuffer sumflds=new StringBuffer("");
		StringBuffer flds=new StringBuffer("");
		getGroupFlds(bottomnList, infor_Flag, tableName, isGroupPoint, tabid, sumflds, flds);

		if(needCalcAfterSum(bottomnList)) {
            return calcAfterSum(tableName, sumflds.toString(), flds.toString(), "");  // 汇总后计算
        } else{
			tempSql.append(sumflds.toString() + " from "+tableName);
			return tempSql.toString();
		}
	}
	
	/**
	 * 是否需要汇总后计算
	 * @param bottomnList
	 * @return
	 */
	private boolean needCalcAfterSum(ArrayList bottomnList)
	{
		for (Iterator t = bottomnList.iterator(); t.hasNext();) {
			String[] temp = (String[]) t.next();
			String extendattr=temp[20]==null?"":temp[20].toUpperCase();
			if(extendattr.indexOf("GROUPSUM")!=-1)
			{
				String groupSum = extendattr.substring(extendattr.indexOf("<GROUPSUM>")+"<GROUPSUM>".length(),extendattr.indexOf("</GROUPSUM>"));
				if("4".equals(groupSum))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private String getCalcAfterSumTmpTable()
	{
		return "T#"+this.userView.getUserName()+"_mus_a2";//CS为a1
	}

	/*
	 * 汇总后计算
	 */
	private String calcAfterSum(String tableName,String sumflds,String flds,String cond)
	{
		// 把汇总数据导入临时表，再进行计算
		String tmptable=getCalcAfterSumTmpTable();
		String strWhere=cond==null?"":cond;
		String strGroupBy="GROUPN, GROUPV, GROUPN2, GROUPV2";
		DbWizard dbw=new DbWizard(conn);
		if(dbw.isExistTable(tmptable)) {
            dbw.dropTable(tmptable);
        }
		dbw.createTempTable(tableName, tmptable, sumflds.substring(1), strWhere, strGroupBy);
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			HmusterBo bo=new HmusterBo(conn,userView);
			ArrayList musterfieldlist= bo.getMusterField(tabid, tableName);
			rowSet = dao.search("select * from muster_cell where tabid="+ tabid + " and UPPER(flag)='C' order by gridno");
			while (rowSet.next()) {
				int gridNo = rowSet.getInt("gridno");
				String fieldType = rowSet.getString("field_type");
				String flag = rowSet.getString("flag");
				String queryCond = Sql_switcher.readMemo(rowSet, "QueryCond").trim();
				int slope = rowSet.getInt("slope");
				int rcount = rowSet.getInt("rcount");
				String extendAttr = rowSet.getString("ExtendAttr")==null?"":rowSet.getString("ExtendAttr");
				String SimpleFormula = "false";
				if(extendAttr.indexOf("<SimpleFormula>")!=-1){
					SimpleFormula = extendAttr.substring(extendAttr.indexOf("<SimpleFormula>")+"<SimpleFormula>".length(),extendAttr.indexOf("</SimpleFormula>"));
				}
				String groupSum = "";
				if(extendAttr.indexOf("GroupSum")!=-1)
				{
					groupSum = extendAttr.substring(extendAttr.indexOf("<GroupSum>")+"<GroupSum>".length(),extendAttr.indexOf("</GroupSum>"));
				}
				if(!"C".equalsIgnoreCase(flag)||!"4".equals(groupSum)/*4:汇总后计算*/) {
                    continue;
                }

				int varType = 6; // float
				if ("D".equals(fieldType)) {
                    varType = 9;
                } else if ("A".equals(fieldType) || "M".equals(fieldType)) {
                    varType = 7;
                }
				int infoGroup = 0; // forPerson 人员
				if ("2".equals(infor_Flag)) {
					infoGroup = 3; // forUnit 单位
				} else if ("3".equals(infor_Flag)) {
					infoGroup = 1; // forPosition 职位
				}
				String a_dbpre = "";
				try {
					if("true".equalsIgnoreCase(SimpleFormula)){
						YksjParser yp = new YksjParser(userView, musterfieldlist,YksjParser.forNormal, varType, infoGroup, "Ht", a_dbpre);
						yp.setCon(conn);
						yp.run(queryCond);
						String sqlstr = yp.getSQL();
						dao.update("update "+tmptable+" set C"+gridNo+"="+sqlstr);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		String sql=flds + " from "+ tmptable ;//+ " " + tableName;   31374	云南金孔雀交通企业集团：高级花名册取数的时候系统提示“选择列表中的列 'T#su_mus_a2.NBASE' 无效，因为该列没有包含在聚合函数或 GROUP BY 子句中。”
		return sql;
	}
	
	/**
	 * 是否需要合计(页小计,页累计,分组合计,总计)
	 * @param noperation: 
	 * @param flag: 0 总计 1 分组合计,2 页累计,3 页小计
	 * @return
	 */
	public String isAdd(String noperation,int flag)
	{
		String isAdd="0";
		if(noperation!=null)
		{
			if(flag==0)
			{
				if("8".equals(noperation)|| "9".equals(noperation)|| "10".equals(noperation)|| "11".equals(noperation)
						|| "12".equals(noperation)|| "13".equals(noperation)|| "14".equals(noperation)|| "15".equals(noperation))
				{
					isAdd="1";
				}
			}
			else if(flag==1)
			{
				if("4".equals(noperation)|| "5".equals(noperation)|| "7".equals(noperation)|| "6".equals(noperation)
						|| "12".equals(noperation)|| "13".equals(noperation)|| "14".equals(noperation)|| "15".equals(noperation))
				{
					isAdd="1";
				}
			}
			else if(flag==2)
			{
				if("2".equals(noperation)|| "3".equals(noperation)|| "6".equals(noperation)|| "7".equals(noperation)
						|| "10".equals(noperation)|| "11".equals(noperation)|| "14".equals(noperation)|| "15".equals(noperation))
				{
					isAdd="1";
				}
			}
			else if(flag==3)
			{
				if("1".equals(noperation)|| "3".equals(noperation)|| "5".equals(noperation)|| "7".equals(noperation)
						|| "9".equals(noperation)|| "13".equals(noperation)|| "11".equals(noperation)|| "15".equals(noperation))
				{
					isAdd="1";
				}
			}
			/*String s=Integer.toBinaryString(Integer.parseInt(noperation));
			String d=s;
			for(int i=4;i>0;i--)
			{
				if(i>s.length())
					d="0"+d;
			}
			isAdd=String.valueOf(d.charAt(flag));*/
		}
		return isAdd;
	}
	
	
	
	public ArrayList getFieldandFieldsList(ArrayList list) {
		ArrayList a_list = new ArrayList();
		StringBuffer fieldName = new StringBuffer("");
		StringBuffer fieldNames = new StringBuffer("");
		StringBuffer fzhjNames=new StringBuffer("");
		StringBuffer zjNames=new StringBuffer("");
		
		for (Iterator t = list.iterator(); t.hasNext();) {
			String[] tt = (String[]) t.next();
			if ("1".equals(isAdd(tt[1],3))) {
				this.isyxj=true;
				fieldName.append("#" + tt[0]);
			}

			if ("1".equals(isAdd(tt[1],2))) {
				this.isylj=true;
				fieldNames.append("#" + tt[0]);
			}
			
			if ("1".equals(isAdd(tt[1],1))) {
				this.isfzhj=true;
				fzhjNames.append("#" + tt[0]);
			}
			
			if ("1".equals(isAdd(tt[1],0))) {
				this.iszj=true;
				zjNames.append("#" + tt[0]);
			}

		}
		String[] field = null;
		String[] fields = null;
		float[] pageCount = null;
		if (fieldName.length() > 2) {
			field = fieldName.substring(1).split("#"); // 需小计的列名
		}
		if (fieldNames.length() > 2) {
			fields = fieldNames.substring(1).split("#"); // 需累计的列名
		}
		if(fzhjNames.length()>2)
		{
			this.fzhj=fzhjNames.substring(1).split("#"); //分组合计
		}
		if(zjNames.length()>2)
		{
			this.zj=zjNames.substring(1).split("#"); //总计
		}
		a_list.add(field);
		a_list.add(fields);
		return a_list;

	}

	/**
	 * 产生翻页代码 和 返回按钮
	 * 
	 */
	private String getTurnPageCode(String currentPage, String totalPage,
			String top, float left_point, String operateMethod) {
		StringBuffer code = new StringBuffer("");
		if (true/*!totalPage.equals("0")*/) {//薪资调用高级花名册如果查询结果为0 翻页工具条消失会导致页面布局有问题，现放开
				/* 问题号：4121 各机构编制情况：在表格工具中，去掉取数条件，进入各机构编制情况，页面显示有错误 xiaoyun 2014-9-4 start */
				//code.append("<table border='0'  > \n <tr>");
				code.append("<table border=\"0\"  ><tr>");
				/* 问题号：4121 各机构编制情况：在表格工具中，去掉取数条件，进入各机构编制情况，页面显示有错误 xiaoyun 2014-9-4 end */
				code.append("<td valign=\"top\" style=\"white-space:nowrap;\">");
				code.append(ResourceFactory.getProperty("label.page.serial")+" ");
				code.append(currentPage);
				code.append(" "+ResourceFactory.getProperty("label.page.sum")+" ");
				code.append(countall);
				code.append(" "+ResourceFactory.getProperty("label.page.row")+" ");
				code.append(totalPage);
				code.append(" "+ResourceFactory.getProperty("label.page.page"));
				code.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				if(Integer.parseInt(currentPage)>1) {
					code.append("<a href=\"javascript:goto(1)\">");
					code.append(ResourceFactory.getProperty("label.banner.home"));
					code.append("</a>&nbsp;");		
				} else {
					code.append(ResourceFactory.getProperty("label.banner.home") + "&nbsp;");
				}
				if(Integer.parseInt(currentPage)>1){
					code.append("<a href=\"javascript:goto(");
					code.append((Integer.parseInt(currentPage)-1));
					code.append(")\">");
					code.append(ResourceFactory.getProperty("workdiary.message.last.page"));
					code.append("</a>&nbsp;");
				}else{
					code.append(ResourceFactory.getProperty("workdiary.message.last.page"));
					code.append("&nbsp;");
				}
				if(Integer.parseInt(currentPage)<Integer.parseInt(totalPage)){
					code.append("<a href=\"javascript:goto(");
					code.append((Integer.parseInt(currentPage)+1));
					code.append(")\">");
					code.append(ResourceFactory.getProperty("workdiary.message.next.page"));
					code.append("</a>&nbsp;");
				}else{
					code.append(ResourceFactory.getProperty("workdiary.message.next.page"));
					code.append("&nbsp;");
				}
				if(Integer.parseInt(currentPage)<Integer.parseInt(totalPage)){
					code.append("<a href=\"javascript:goto(");
					code.append(totalPage);
					code.append(")\">");
					code.append(ResourceFactory.getProperty("workdiary.message.end.page"));
					code.append("</a>&nbsp;");
				}else{
					code.append(ResourceFactory.getProperty("workdiary.message.end.page"));
					code.append("&nbsp;");
				}
				
				code.append(ResourceFactory.getProperty("reportinnercheck.di"));
				/* 高级花名册当前所在页input输入框样式调整 xiaoyun 2014-8-11 start */
				//code.append("<input type=text size='4' onkeypress='event.returnValue=IsDigit();' name='pageSelect' value='");
				code.append("<input type=text size=\"4\" onkeypress=\"event.returnValue=IsDigit();\" name=\"pageSelect\" class=\"text4\" value=\"");
				/* 高级花名册当前所在页input输入框样式调整 xiaoyun 2014-8-11 end */
				code.append(currentPage);
				code.append("\" />");
				code.append(ResourceFactory.getProperty("hmuster.label.paper"));
				code.append("&nbsp;&nbsp;<a href=\"javascript:gotos()\" >");
				/* 问题号：4121 各机构编制情况：在表格工具中，去掉取数条件，进入各机构编制情况，页面显示有错误 xiaoyun 2014-9-4 start */
				//code.append("<img src=\"/images/go.gif\" border=0 align=\"absmiddle\">");
				code.append("<img src=\"/images/go.gif\" border=0 align=\"absmiddle\">");
				/* 问题号：4121 各机构编制情况：在表格工具中，去掉取数条件，进入各机构编制情况，页面显示有错误 xiaoyun 2014-9-4 end */
				//code.append(ResourceFactory.getProperty("workdiary.message.to.page"));
				code.append("</a>");
				code.append("&nbsp;");
				code.append("</td></tr></table>");

		}
//
//		if (!operateMethod.equals("direct"))
//			code.append("<input type='button' value='"
//							+ ResourceFactory
//									.getProperty("kq.search_feast.back")
//							+ "'  class='mybutton' onclick='returns()' style='position:absolute;top:5;left:"
//							+ (totalPage.equals("0") ? left_point
//									: (left_point + 380))
//							+ ";width:;height:'  >  \n");
//		

		return code.toString();
	}

	/**
	 * 得到表体内容 & 当前页的分组指标 & 当前页的行数 & 显示的临时照片名称列表 (横向/纵向分栏)
	 * 
	 * @param BottomnList
	 *            表头底端的列
	 * @param isGroupPoint
	 *            是否采用分组显示
	 * @param sql
	 *            查询语句
	 * @param rows
	 *            每页的行数
	 * @param currpage
	 *            当前页数
	 * @param keyList
	 *            关键字段列表
	 * @param nOperation
	 *            汇总标示
	 * @param rowHeight
	 *            行高
	 * @param field
	 *            需小计的列名
	 * @param fields
	 *            需累计的列名
	 * @param bodywidth
	 *            表体长度
	 * @param zeroPrint
	 *            0:不为零打印 1：零打印
	 * @param emptyRow
	 *            0：空行不打印 1：空行打印
	 * @param lth
	 *            表的最左端、最上端、表头的总高
	 * @param fontStyle
	 *            [0]FontName [1]FontEffect [2]FontSize
	 * @return ArrayList
	 */
	private ArrayList getHorizontalBodyContext(boolean isGroupTerm,
			String column, String columnLine, float h_tableHeight,
			float h_tableWidth, int tableHeight, float pix,
			String isGroupPoint, String emptyRow, String zeroPrint,
			ArrayList headerList, String sql, int rows, int currpage,
			String tableName, String dbpre, ArrayList aList)
			throws GeneralException {
		
		//29.7 x 21  cm  1122 *  793  A4纸张大小转换成像素 1英寸等于2.54cm 96像素/英寸 计算页面纵分时页面可以放几列 几行
		
		int colNum=793/(int)h_tableWidth;
		colNum=colNum==0?1:colNum;//计算纵分时页面上能放置几列表格
		int realColNum=1;//记录页写了多少列
		HmusterPdf hmusterPdf = new HmusterPdf(this.conn);
		StringBuffer bodyContext = new StringBuffer("");
		StringBuffer allBuffer = new StringBuffer("");
		boolean canMerge=true;  // 是否能合并
		if("1".equals(isGroupPoint2)&&this.fzhj != null && this.fzhj.length >0&&(isGroupTerm||isGroupTerm2)) {
            canMerge=false;     // 合并有问题
        }
		//ContentDAO dao = new ContentDAO(this.conn);
		//RowSet rowSet = null;
		
		Connection a_conn = AdminDb.getConnection();	
		ResultSet resultSet=null;
		Statement stmt=null;
		
		ArrayList photoList = new ArrayList();
		String groupV = "";
		String groupN="";
		String privA0100 = "";
		boolean hasHREF = true;
		float hr_pix = 0; // 分隔线的坐标
		int i = 0; // 序号
		int n = 1; // 行数
		int p = 0; // 分组显示的页数
		int ii = 0; // 分组显示的序号
		int nn = 0; // 分组显示时控制换页的变量
		float div_Top=0; //线高
        int recordRow=0;
		ArrayList list3 = null;
		//if (column.equals("1"))
		//	list3 = (ArrayList) aList.get(2); // 哪些列有页小计或累计
	//	else
			list3 = (ArrayList) aList.get(6);
		ArrayList fieldList = getFieldandFieldsList(list3);
		String[] field = (String[]) fieldList.get(0);
		String[] fields = (String[]) fieldList.get(1);
		ArrayList bottomnList = (ArrayList) aList.get(0); // 取得表头最底端的列
		int nOperation = ((Integer) aList.get(1)).intValue();
		ArrayList tableHeaderList = (ArrayList) aList.get(4);
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			resultSet = dao.search(sql);//获取显示数据
			/* 页小计初始化 */
			double[] pageCount = null;
			if (field != null && field.length > 0) {
				pageCount = new double[field.length];
				for (int a = 0; a < field.length; a++) {
					pageCount[a] = 0;
				}
			}

			double[] totalCount = null;
			if (fields != null && fields.length > 0) {
				totalCount = new double[fields.length];
				for (int a = 0; a < fields.length; a++) {
					totalCount[a] = 0;
				}
			}
			double[] zjCount = null;
			if (this.zj != null && this.zj.length > 0/*&&fields!=null&&fields.length>0*/) {
				zjCount = new double[this.zj.length];
				for (int a = 0; a < this.zj.length; a++) {
					zjCount[a] = 0;
				}
			}
			double[] fzhjCount = null;
			if (this.fzhj != null && this.fzhj.length > 0/*&&fields!=null&&fields.length>0*/) {
				fzhjCount = new double[this.fzhj.length];
				for (int a = 0; a < this.fzhj.length; a++) {
					fzhjCount[a] = 0;
				}
			}
			//第二分组指标,也支持合计
			double[] fzhjCount2=null;
			if(this.fzhj!=null&&this.fzhj.length>0)
			{
				fzhjCount2= new double[this.fzhj.length];
				for(int a=0;a<this.fzhj.length;a++)
				{
					fzhjCount2[a]=0;
				}
			}
			String endhref="</a>";
			int countAll = 0;
			if(isGroupPoint == null||!"1".equals(isGroupPoint)){
				RowSet rs = dao.search("select max(recidx) as recidx from "+tableName);
				if(rs.next()){
					countAll = rs.getInt("recidx");
				}
			}
			boolean flag=false;
			String groupItem="first";
			int isValues = 0;
			int serial=1;
			int addvar=0;
			boolean isFirst = true; // 表的第一单元格
			int begin=0;
			this.mergeGrid=this.mergeGrid==null?"":this.mergeGrid;
			ArrayList aheaderList=new ArrayList();
			for (Iterator t = headerList.iterator(); t.hasNext();) {
				String[] temp = (String[]) t.next();
				if((this.getTextFormatMap().size()>0&&this.getTextFormatMap().get(temp[0])==null)&&("1".equals(column)&& "1".equals(this.dataarea))) {
                    continue;
                }
				aheaderList.add(temp);
			}
			String mergeitemid[] = new String[aheaderList.size()];//gridno
			String mergevalue[] = new String[aheaderList.size()];//数据值
			String ismergemain[] = new String[aheaderList.size()];//是否按机构/人员合并
			float[] toppixvalue = new float[aheaderList.size()];//格子的顶部坐标
			int mergeheight[]=new int[aheaderList.size()];//合并格子的高度
			//row
			String[] rowitemid = new String[aheaderList.size()];//行合并
			int arrayindex=0;
 			String[] heArr = mergeGrid.split(",");
			for(int h=0;h<heArr.length;h++){
				if(heArr[h]!=null&&heArr[h].length()>1){
					String heirem[] = heArr[h].split(":");
					if((this.getTextFormatMap().size()>0&&this.getTextFormatMap().get(heirem[0])==null)&&("1".equals(column)&& "1".equals(this.dataarea))) {
                        continue;
                    }
					if(heirem[1]!=null&& "true".equalsIgnoreCase(heirem[1])){
						mergeitemid[arrayindex] = heirem[0];  //gridno  
						mergevalue[arrayindex] = "&nbsp;";//context
						mergeheight[arrayindex] = 0;
						toppixvalue[arrayindex]=0;//top
					}
					if(heirem[2]!=null&& "true".equalsIgnoreCase(heirem[2])){
						ismergemain[arrayindex] = "true";
					}
					if(heirem[3]!=null&& "true".equalsIgnoreCase(heirem[3]))
					{
						rowitemid[arrayindex]=heirem[0];
					}
					arrayindex++;
				}
			}
			String mainidvalue = "";
			String mainArr[] = new String[aheaderList.size()];
			StringBuffer strTable = new StringBuffer();
			int topvalue_index=0;
			boolean istotal=false;
			boolean isbreak=false;
			DecimalFormat myformat=null;
			String group2Value="";
			ArrayList rowMargeList = new ArrayList();
			boolean writeGroup=true;
			String a0100tran = "";
			CheckPrivSafeBo checkPrivSafeBo = null;
			if(!userView.isSuper_admin()&& "true".equalsIgnoreCase(this.no_manger_priv)){
				checkPrivSafeBo=new CheckPrivSafeBo(this.conn,this.userView);
			}
			while (resultSet.next()) {
				isValues++;
				int recidx=0;
				if (this.zj != null && this.zj.length > 0) {
                    add(this.zj,zjCount,resultSet);
                }
				if(infor_Flag!=null&& "1".equals(infor_Flag)){
					mainidvalue = resultSet.getString("A0100");
					if(!userView.isSuper_admin()&& "true".equalsIgnoreCase(this.no_manger_priv)){
						String privDbpre=checkPrivSafeBo.checkDb(dbpre);
						if(privDbpre.equalsIgnoreCase(dbpre)){
							privA0100 = checkPrivSafeBo.checkA0100("", dbpre, mainidvalue, "");
							if(privA0100.equalsIgnoreCase(mainidvalue)){
								hasHREF = true;
							}else{
								hasHREF = false;
							}
						}else{
							hasHREF = false;
						}
					}
					a0100tran = "~" + SafeCode.encode(PubFunc.convertTo64Base(mainidvalue.toString()));
				}else if(infor_Flag!=null&& "2".equals(infor_Flag)) {
                    mainidvalue = resultSet.getString("B0110");
                } else if(infor_Flag!=null&& "3".equals(infor_Flag)) {
                    mainidvalue = resultSet.getString("E01A1");
                }
	            String href="";
	            if("1".equals(infor_Flag)) {
	            	if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
	            		href+="<a href=\"javascript:openSelfInfo(\\'/workbench/browse/showselfinfo.do?b_search=link`userbase="+
	            		getDBPre(resultSet,dbpre)+"`returnvalue=100000`flag=notself`a0100=";
	            		if("mobile".equalsIgnoreCase(this.getReturnflag())) {
                            href="<a href=\"javascript:test(\\'"+getDBPre(resultSet,dbpre)+"\\',\\'";
                        }
	            	}
	            }

				if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
				{
	    			if(isValues==1)//第一条记录
		    		{
	    				group2Value=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
		    		}
				}
				/** 分组显示 分组分页*/
				if (/*!isGroupTerm && */isGroupPoint != null&& "1".equals(isGroupPoint)&& "0".equals(isGroupNoPage)) {
					String tempGroupN = " ";
					if (resultSet.getString("GroupN") != null) {
                        tempGroupN = "".equals(resultSet.getString("GroupN"))?" ":resultSet.getString("GroupN");
                    }
					if (!groupN.equals(tempGroupN)) {
						p++;
						nn = 0;
						if("1".equals(this.isGroupedSerials)) {//按组显示序号 序号从1开始
							addvar=0;
						}
						if (p > currpage)
						{
							flag=true;
							isbreak=true;
							if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
							{
				    			
					    		String temp=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
					    		if(temp.equalsIgnoreCase(group2Value)) {
                                    writeGroup=false;
                                }
							}
							break;
						}
						groupN = tempGroupN;						
						if(resultSet.getString("GroupV")!=null) {
                            groupV=resultSet.getString("GroupV");
                        } else {
                            groupV=" ";
                        }
						
					
						
						// 累计清空
						if (fields != null && fields.length > 0) {
							totalCount = new double[fields.length];
							for (int a = 0; a < fields.length; a++) {
								totalCount[a] = 0;
							}
						}
						if(this.fzhj!=null&&this.fzhj.length>0)
						{
							fzhjCount = new double[this.fzhj.length];
							for (int a = 0; a < this.fzhj.length; a++) {
								fzhjCount[a] = 0;
							}
						}
					} else if (nn != 0 && nn % rows == 0) {

						p++;
						if (p > currpage)
						{
							if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
							{
				    			
					    		String temp=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
					    		if(temp.equalsIgnoreCase(group2Value)) {
                                    writeGroup=false;
                                }
							}
							break;
						}
					}

					ii++;
					nn++;
					if (p < currpage) {
						// //////// 处理页累计	
						if (fields != null && fields.length > 0) {
							for (int b = 0; b < fields.length; b++) {
								String a_context = resultSet.getString(fields[b]);
								if (a_context != null) {
                                    totalCount[b] += Double.parseDouble(a_context);
                                }
							}
						}
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							String temp = "";
							if(resultSet.getString("GroupN2")!=null) {
                                temp = resultSet.getString("GroupN2");
                            }
							if(!group2Value.equalsIgnoreCase(temp))
							{
								if(this.fzhj!=null&&this.fzhj.length>0)
								{
									fzhjCount2 = new double[this.fzhj.length];
									for (int a = 0; a < this.fzhj.length; a++) {
										fzhjCount2[a] = 0;
									}
								}
								group2Value=temp;
							
							}
							else
							{
							}
						}
						add(this.fzhj,fzhjCount2,resultSet);
						add(this.fzhj,fzhjCount,resultSet);
						addvar++;
						continue;
					}
				} 
				/**分组不分页显示*/
				else if (!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)&& "1".equals(isGroupNoPage)) {
					String tempGroupN = " ";
					if (resultSet.getString("GroupN") != null) {
                        tempGroupN = "".equals(resultSet.getString("GroupN"))?" ":resultSet.getString("GroupN");
                    }
					if(resultSet.getString("GroupV")!=null) {
                        groupV=resultSet.getString("GroupV");
                    } else {
                        groupV=" ";
                    }
					if (i % rows == 0) {
						p++;
						if (p > currpage)
						{
							if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
							{
				    			
					    		String temp=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
					    		if(temp.equalsIgnoreCase(group2Value)) {
                                    writeGroup=false;
                                }
							}
							break;
						}
					}
					if (p < currpage) {
						if (!groupN.equals(tempGroupN)) {
							fzhjCount = initCount(this.fzhj);
						}
						groupN = tempGroupN;
						// ////////处理页累计
						if (fields != null && fields.length > 0) {
							for (int b = 0; b < fields.length; b++) {
								String a_context = resultSet.getString(fields[b]);
								if (a_context != null) {
                                    totalCount[b] += Double.parseDouble(a_context);
                                }
							}
						}
						i++;
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							String temp = "";
							if(resultSet.getString("GroupN2")!=null) {
                                temp = resultSet.getString("GroupN2");
                            }
							if(!group2Value.equalsIgnoreCase(temp))
							{
								if(this.fzhj!=null&&this.fzhj.length>0)
								{
									fzhjCount2 = new double[this.fzhj.length];
									for (int a = 0; a < this.fzhj.length; a++) {
										fzhjCount2[a] = 0;
									}
								}
								group2Value=temp;
							
							}
							else
							{
							}
						}
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
			    			group2Value=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");

						}
						add(this.fzhj,fzhjCount2,resultSet);
						add(this.fzhj,fzhjCount,resultSet);
						continue;
					}
				}
				/**不分组*/
				else if (!isGroupTerm&&( isGroupPoint== null|| !"1".equals(isGroupPoint))){
					recidx = resultSet.getInt("recidx");
					if(this.tableTypeFlag){
						if (i %(rows*colNum) == 0) {//colNum 为列数  //处理当前页内
							p++;//p是从0开始累加 
							if (p > currpage)
							{
								if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
								{
					    			
						    		String temp=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
						    		if(temp.equalsIgnoreCase(group2Value)) {
                                        writeGroup=false;
                                    }
								}
								if(i/(rows*currpage)==colNum)//colNum列数
                                {
                                    break;//跳出while 循环
                                }
							}
						}
					}else{
						if (i % rows == 0) {
							p++;
							if (p > currpage)
							{
								if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
								{
					    			
						    		String temp=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
						    		if(temp.equalsIgnoreCase(group2Value)) {
                                        writeGroup=false;
                                    }
								}
									break;//跳出while 循环
							}
						}
					}
					if (p < currpage) {
						// ////////处理页累计
						if (fields != null && fields.length > 0) {
							for (int b = 0; b < fields.length; b++) {
								String a_context = resultSet.getString(fields[b]);
								if (a_context != null) {
                                    totalCount[b] += Double.parseDouble(a_context);
                                }
							}
						}
						i++;
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							String temp = "";
							if(resultSet.getString("GroupN2")!=null) {
                                temp = resultSet.getString("GroupN2");
                            }
							if(!group2Value.equalsIgnoreCase(temp))
							{
								if(this.fzhj!=null&&this.fzhj.length>0)
								{
									fzhjCount2 = new double[this.fzhj.length];
									for (int a = 0; a < this.fzhj.length; a++) {
										fzhjCount2[a] = 0;
									}
								}
								group2Value=temp;
							
							}
							else
							{
							}
						}
						add(this.fzhj,fzhjCount2,resultSet);
						add(this.fzhj,fzhjCount,resultSet);
						continue;
					}
				}else{
					recidx = resultSet.getInt("recidx");
					if (i % rows == 0) {
						p++;
						if (p > currpage)
						{
							if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
							{
				    			
					    		String temp=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
					    		if(temp.equalsIgnoreCase(group2Value)) {
                                    writeGroup=false;
                                }
							}
							break;
						}
					}
					
					if (p < currpage) {
						// ////////处理页累计
						if (fields != null && fields.length > 0) {
							for (int b = 0; b < fields.length; b++) {
								String a_context = resultSet.getString(fields[b]);
								if (a_context != null) {
                                    totalCount[b] += Double.parseDouble(a_context);
                                }
							}
						}
						i++;
						if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2))
						{
							String temp = "";
							if(resultSet.getString("GroupN2")!=null) {
                                temp = resultSet.getString("GroupN2");
                            }
							if(!group2Value.equalsIgnoreCase(temp))
							{
								if(this.fzhj!=null&&this.fzhj.length>0)
								{
									fzhjCount2 = new double[this.fzhj.length];
									for (int a = 0; a < this.fzhj.length; a++) {
										fzhjCount2[a] = 0;
									}
								}
								group2Value=temp;
							
							}
							else
							{
							}
						}
						add(this.fzhj,fzhjCount,resultSet);
						add(this.fzhj,fzhjCount2,resultSet);
						continue;
					}
				}
				float hr_width=0;
				float hr_left=0;
				int hr_i=0;
				int x=0;
				//begin++;
				recordRow++;
				int columnNum = 0;
				String rowValue="";
				float rowLeft=0;
				float rowWidth=0;
				int rowmergeindex=0;
				StringBuffer tempBuf=new StringBuffer("");
				// FIXME left<0的单元格显示不出来
					if(this.tableTypeFlag) {
                        if(i!=0&&(i-colNum*rows*(currpage-1))!=0&&(i-colNum*rows*(currpage-1))%rows==0/*i!=0&&i==rows*(2*currpage-1)*/){//第一个表格结束第二表格开始行(距离左边的位置要按照第一个表格最后一列的位置开始)
                           aheaderList=updateList(aheaderList,pix);
                           realColNum++;
                           n=1;
                           begin=0;//显示边线样式
                       }
                    }
					int lastCell=0;
					for (Iterator t = aheaderList.iterator(); t.hasNext();) {
						tempBuf=new StringBuffer("");
						columnNum++;
						lastCell++;
						topvalue_index++;
						String[] temp = (String[]) t.next();
						if((this.getTextFormatMap().size()>0&&this.getTextFormatMap().get(temp[0])==null)&&("1".equals(column)&& "1".equals(this.dataarea))) {
                            continue;
                        }
						int type = 1;
						String border = "1";
						int align = Integer.parseInt(temp[13]);
						String fontName = temp[10];
						String fontSize = temp[12];
						String fontStyle = temp[11];

						float topPix = Float.parseFloat(temp[3]);//纵分 top值
						topPix=topPix+h_tableHeight;//空出标题行的高度显示标题行 ， 导出两列多条 控制top需要考虑 数据行开始时距离顶部位置
						String left = temp[2];
						String width = temp[4];
						String aheight = temp[5];//h_tableHeight+"";
						String context = "&nbsp;";
												
						if ("S".equals(temp[7])) {//第一列 序号排序
							if (!isGroupTerm &&isGroupPoint != null && "1".equals(isGroupPoint)&& "0".equals(this.isGroupNoPage)) {
								context = String.valueOf(serial+addvar);
								serial++;
							} else
							{
								if(this.tableTypeFlag) {
                                    context = String.valueOf((currpage-1)*rows*colNum+serial);//colNum 纵分多列每列的列数
                                } else {
                                    context = String.valueOf((currpage-1)*rows+serial);
                                }
								serial++;
							}
						} else if (temp[7]==null|| "H".equals(temp[7])|| "".equals(temp[7])) {
							context = temp[1];//单元格为文本型时内容是否为空待定  默认不为空
							if (context != null && context.indexOf("`") != -1) {
                                context = context.replaceAll("`", "<br>");
                            }
						} else if (!isGroupTerm && "P".equals(temp[7])) {
							String tempName = hmusterPdf.createPhotoFile(getDBPre(resultSet,dbpre)+ "A00", resultSet.getString("a0100"), "P");
							if(tempName!=null&&tempName.length()>1){
								photoList.add(tempName);
								context = "<img src=\"/servlet/vfsservlet?fromjavafolder=true&fileid="+ tempName+ "\"  height=\""
										+ (Float.parseFloat(aheight) - 5)
										+ "\" width=\""
										+ (Float.parseFloat(width) - 5) + "\" border=1>";
							}
							else {
                                context="&nbsp;";
                            }
						}
						// else
						// if(temp[7]!=null&&!temp[7].equals("S")&&!temp[7].equals("H")&&!temp[7].equals("P")&&!temp[7].equals("R")&&!temp[7].equals(""))
						else {
							if (isGroupTerm && "G".equals(temp[7])&& isGroupPoint != null&& "1".equals(isGroupPoint)) {
                                context = resultSet.getString("GroupV");
                            } else if (isGroupTerm){
								if("E".equals(temp[7])&&this.isGroupV2) {
                                    context=resultSet.getString("GroupV2");
                                } else if("E".equals(temp[7])) {
                                    context="&nbsp;";
                                } else{
									try{
										context = resultSet.getString("C" + temp[0]); 
										if (context != null && context.indexOf("`") != -1)   // 取列数据
                                        {
                                            context = context.replaceAll("`", "<br>");
                                        }
									}catch(Exception e){}
								}
							}else{
								if("E".equalsIgnoreCase(temp[7])&&this.isGroupV2) {
                                    context=resultSet.getString("GroupV2");
                                } else if("E".equals(temp[7])) {
                                    context="&nbsp;";
                                } else{
									try{
										context = resultSet.getString("C" + temp[0]);//代码型查询内容
										if(context.contains("`")) {
                                            context = context.replaceAll("`", "、");
                                        }
									}catch(Exception e){}
								}
							}
							// 页小计
							if (field != null && field.length > 0) {
								for (int b = 0; b < field.length; b++) {

									if (field[b].equals("C" + temp[0])) {
										if (context != null) {
                                            pageCount[b] += Double.parseDouble(context);
                                        }
										break;
									}
								}
							}
							// //////// 处理页累计
							if ("N".equals(temp[9])) {
								if (fields != null && fields.length > 0) {
									for (int b = 0; b < fields.length; b++) {

										if (fields[b].equals("C" + temp[0])) {

											if (context != null) {
                                                totalCount[b] += Double.parseDouble(context);
                                            }
											break;
										}
									}
								}
							}
							if ("N".equals(temp[9])|| "R".equals(temp[7])) {
								count(fzhjCount,this.fzhj,context,temp);	
							}
							if (context == null) {
								context = "&nbsp;";
								if ("1".equals(zeroPrint) && "N".equals(temp[9])) {
                                    context = "0";
                                }

						}

							if (context != null && !"".equals(context)&& !"&nbsp;".equals(context)&& "N".equals(temp[9])) {
								float f = Float.parseFloat(context);
								if (zeroPrint != null && "0".equals(zeroPrint)) // 不为零打印
								{
									if (f == 0) {
                                        context = "&nbsp;";
                                    } else {
                                        context = round(context, Integer.parseInt(temp[6]));
                                    }

								} else {
                                    context = round(context, Integer.parseInt(temp[6]));
                                }

							}

						}
						
					if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&this.fzhj != null && this.fzhj.length >0&&columnNum==1)
					{
						String temp2=resultSet.getString("groupN2")==null?"":resultSet.getString("groupN2");
						if(!temp2.equals(group2Value))
						{
							group2Value=temp2;
							if(begin==1)
							{
								fzhjCount2 = initCount(this.fzhj);
								add(this.fzhj,fzhjCount2,resultSet);
							}
							else
							{
								/**第二分组合计后，要将合并格画出。*/
								int acolumnNum = 0;
								StringBuffer atempBuf=new StringBuffer("");
								for (Iterator it = aheaderList.iterator(); it.hasNext();) {
									atempBuf.setLength(0);
									acolumnNum++;
									topvalue_index++;
									String[] atemp = (String[]) it.next();
									int atype = 1;
									String aborder = "1";
									int aalign = Integer.parseInt(atemp[13]);
									String gridno=atemp[0];
									boolean wordWarp=false;
							    	if(musterCellMap!=null&&musterCellMap.get(gridno)!=null) {
							    		if(musterCellMap.get(gridno).get("nWordWrap")!=null&&"1".equals(musterCellMap.get(gridno).get("nWordWrap"))) {
							    			wordWarp=true;
							    		}
							    	}
									String afontName = atemp[10];
									String afontSize = atemp[12];
									String afontStyle = atemp[11];
									String aleft = atemp[2];
									String awidth = atemp[4];
									String aaheight = atemp[5];//h_tableHeight+"";
									// 处理虚线 L,T,R,B,
									String style_name = getStyleName2(atemp,this.printGrind,begin);
									if (!"1".equals(column)) {
										left = String.valueOf(Float.parseFloat(left) + (n - 1)* h_tableWidth + pix * (n - 1));
									} 
									if((this.getTextFormatMap().size()>0&&this.getTextFormatMap().get(atemp[0])==null)&&("1".equals(column)&& "1".equals(this.dataarea))) {
                                        continue;
                                    }
									if(mergeitemid[acolumnNum-1]!=null&&mergeitemid[acolumnNum-1].trim().length()>0&&atemp[0].equalsIgnoreCase(mergeitemid[acolumnNum-1])&&mergevalue[acolumnNum-1]!=null&&!"".equalsIgnoreCase(mergevalue[acolumnNum-1])&&!"&nbsp;".equalsIgnoreCase(mergevalue[acolumnNum-1]))
									{
										String va="";
										if(mergevalue[acolumnNum-1]!=null&&!"".equals(mergevalue[acolumnNum-1])&&!"&nbsp;".equalsIgnoreCase(mergevalue[acolumnNum-1])&&atemp[18]!=null&& "a0101".equalsIgnoreCase(atemp[18])&& "1".equals(this.infor_Flag)){
											if(hasHREF){
							            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
							            		{
							            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                        va=href+a0100tran+"\\',\\'"+mergevalue[acolumnNum-1]+"\\')\">"+mergevalue[acolumnNum-1]+endhref;
                                                    } else {
                                                        va=mergevalue[acolumnNum-1];
                                                    }
							            		}
							            		else
							            		{
							            			if("false".equalsIgnoreCase(no_manger_priv)) {
                                                        va=href+a0100tran+"\\')\">"+mergevalue[acolumnNum-1]+endhref;
                                                    } else {
                                                        va=mergevalue[acolumnNum-1];
                                                    }
							            		}
							            	}
											
									    }
										if (!isGroupTerm && "P".equals(atemp[7])) {
                                            va=mergevalue[acolumnNum-1];
                                        } else
										{
											if(!hasFieldReadPriv(atemp,true)) {
                                                mergevalue[acolumnNum-1]="";
                                            }
											if (!hasHREF) {
                                                va=mergevalue[acolumnNum-1].replaceAll(" ","&nbsp;");
                                            } else {
                                                va=mergevalue[acolumnNum-1];
                                            }
										}
										if(rowitemid[acolumnNum-1]!=null&&rowitemid[acolumnNum-1].trim().length()>0
												&&atemp[0].equalsIgnoreCase(rowitemid[acolumnNum-1]))// row merge
										 {
											 if(acolumnNum==1)//第一个格要看下一个是否合并
										    	{
										    		if(rowitemid[acolumnNum]!=null&&rowitemid[acolumnNum].trim().length()>0)//下一个合并
										    		{
										    			LazyDynaBean bean = new LazyDynaBean();
										    			bean.set("type",atype+"");
										    			bean.set("align", aalign+"");
										    			bean.set("fontName", afontName);
										    			bean.set("fontSize", afontSize);
										    			bean.set("fontStyle", afontStyle);
										    			bean.set("border", aborder);
										    			bean.set("topvalue", String.valueOf(toppixvalue[acolumnNum-1]));
										    			bean.set("left", aleft);
										    			bean.set("width", awidth);
										    			bean.set("height", String.valueOf((mergeheight[acolumnNum-1]+1)*Float.parseFloat(aaheight)-(mergeheight[acolumnNum-1])));
										    			bean.set("content",va);
										    			bean.set("style_name",style_name);
										    			bean.set("cloumn",acolumnNum+"");
										    			bean.set("gridno", atemp[0]);
										    			rowMargeList.add(bean);
										    		}
										    		else
										    		{
										    			atempBuf.append(executeAbsoluteTable(atype, aalign,afontName, afontSize, afontStyle, aborder, String.valueOf(toppixvalue[acolumnNum-1]), 
											         			aleft, awidth, String.valueOf((mergeheight[acolumnNum-1]+1)*Float.parseFloat(aaheight)-(mergeheight[acolumnNum-1])),va, style_name,wordWarp));

											    		}
											    	}
											    	else if(acolumnNum==aheaderList.size())//最后一个格子
											    	{
											    		if(rowitemid[acolumnNum-2]!=null&&rowitemid[acolumnNum-2].trim().length()>0)//前一个合并
														{
											    			LazyDynaBean bean = new LazyDynaBean();
											    			bean.set("type",atype+"");
											    			bean.set("align", aalign+"");
											    			bean.set("fontName", afontName);
											    			bean.set("fontSize", afontSize);
											    			bean.set("fontStyle", afontStyle);
											    			bean.set("border", aborder);
											    			bean.set("topvalue", String.valueOf(toppixvalue[acolumnNum-1]));
											    			bean.set("left", aleft);
											    			bean.set("width", awidth);
											    			bean.set("height", String.valueOf((mergeheight[acolumnNum-1]+1)*Float.parseFloat(aaheight)-(mergeheight[acolumnNum-1])));
											    			bean.set("content",va);
											    			bean.set("style_name",style_name);
											    			bean.set("cloumn",acolumnNum+"");
											    			bean.set("gridno", atemp[0]);
											    			rowMargeList.add(bean);
														}
											    		else{
											    			atempBuf.append(executeAbsoluteTable(atype, aalign,afontName, afontSize, afontStyle, aborder, String.valueOf(toppixvalue[acolumnNum-1]), 
												         			aleft, awidth, String.valueOf((mergeheight[acolumnNum-1]+1)*Float.parseFloat(aaheight)-(mergeheight[acolumnNum-1])),va, style_name,wordWarp));

											    		}
											    	}
											    	else
													{
											    		if(rowitemid[acolumnNum]!=null&&rowitemid[acolumnNum].trim().length()>0||rowitemid[acolumnNum-2]!=null&&rowitemid[acolumnNum-2].trim().length()>0)//前一个或者后一个有合并
														{
											    			LazyDynaBean bean = new LazyDynaBean();
											    			bean.set("type",atype+"");
											    			bean.set("align", aalign+"");
											    			bean.set("fontName", afontName);
											    			bean.set("fontSize", afontSize);
											    			bean.set("fontStyle", afontStyle);
											    			bean.set("border", aborder);
											    			bean.set("topvalue", String.valueOf(toppixvalue[acolumnNum-1]));
											    			bean.set("left", aleft);
											    			bean.set("width", awidth);
											    			bean.set("height", String.valueOf((mergeheight[acolumnNum-1]+1)*Float.parseFloat(aaheight)-(mergeheight[acolumnNum-1])));
											    			bean.set("content",va);
											    			bean.set("style_name",style_name);
											    			bean.set("cloumn",acolumnNum+"");
											    			bean.set("gridno", atemp[0]);
											    			rowMargeList.add(bean);
														}
											    		else{
											    			atempBuf.append(executeAbsoluteTable(atype, aalign,afontName, afontSize, afontStyle, aborder, String.valueOf(toppixvalue[acolumnNum-1]), 
												         			aleft, awidth, String.valueOf((mergeheight[acolumnNum-1]+1)*Float.parseFloat(aaheight)-(mergeheight[acolumnNum-1])),va, style_name,wordWarp));

											    		}
													}
											 }
											else
											{
												atempBuf.append(executeAbsoluteTable(atype, aalign,afontName, afontSize, afontStyle, aborder, String.valueOf(toppixvalue[acolumnNum-1]), 
												    	aleft,awidth, String.valueOf((mergeheight[acolumnNum-1]+1)*Float.parseFloat(aaheight)-(mergeheight[acolumnNum-1])),va, style_name,wordWarp));
											}
										}
										bodyContext.append(atempBuf.toString());
									}
								arrayindex=0;
								for(int h=0;h<heArr.length;h++){
									if(heArr[h]!=null&&heArr[h].length()>1){
										String heirem[] = heArr[h].split(":");
										if((this.getTextFormatMap().size()>0&&this.getTextFormatMap().get(heirem[0])==null)&&("1".equals(column)&& "1".equals(this.dataarea))) {
                                            continue;
                                        }
										if(heirem[1]!=null&& "true".equalsIgnoreCase(heirem[1])){
											mergeitemid[arrayindex] = heirem[0];
											mergevalue[arrayindex] = "&nbsp;";
											mergeheight[arrayindex] = 0;
											toppixvalue[arrayindex]=0;
										}
										if(heirem[2]!=null&& "true".equalsIgnoreCase(heirem[2])){
											ismergemain[arrayindex] = "true";
											}
											if(heirem[3]!=null&& "true".equalsIgnoreCase(heirem[3]))
											{
												rowitemid[arrayindex]=heirem[0];
											}
										arrayindex++;
									}
								}
									bodyContext.append(getPageCountRows(columnLine, pix,
											h_tableHeight, h_tableWidth, headerList, column, n,
											bottomnList, this.fzhj, zeroPrint, fzhjCount2,
											"hmuster.label.pageCount"));
									n++;
							    	fzhjCount2 = initCount(this.fzhj);
								    add(this.fzhj,fzhjCount2,resultSet);
								}
						}
						else
							{
								add(this.fzhj,fzhjCount2,resultSet);
							}
						}
						if(!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)&& "1".equals(isGroupNoPage)){//分组不分页
							String itemvalue = resultSet.getString("GroupN");
							itemvalue=itemvalue!=null?itemvalue:"";
							column_num=0;
							if (!"first".equalsIgnoreCase(groupItem)&&this.fzhj != null && this.fzhj.length > 0&&this.isfzhj&&!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)&&!itemvalue.equalsIgnoreCase(groupItem)) // 显示分组合计
							{
								
								bodyContext.append(getPageCountRows(columnLine, pix,
										h_tableHeight, h_tableWidth, headerList, column, n,
										bottomnList, this.fzhj, zeroPrint, fzhjCount,
										"planar.stat.total"));
								n++;
								istotal=true;
								fzhjCount = initCount(this.fzhj);
							}
							groupItem = itemvalue;
						}
						if("1".equals(temp[19])) {
                            context="&nbsp;";
                        }
						// 处理虚线 L,T,R,B,
						String style_name = getStyleName2(temp,this.printGrind,begin);
						if(this.tableTypeFlag){
							if(begin==0) {//列表式 第一列 左边线展现
								style_name="RecordRow_self_t";
							}else {
								style_name="RecordRow_self_lt";
							}
						}
						ArrayList isPersonViewFilter=(ArrayList)aList.get(0);//[22142] 使用sql视图 查询用户无指标权限 现针对有V_EMP_的指标默认不需要权限 changxy 20160902
						boolean viewFlag=true;
						for (int j = 0; j < isPersonViewFilter.size(); j++) {
							String[] contaPerView=(String[])isPersonViewFilter.get(j);
							if(contaPerView[1]!=null&&temp[1].equals(contaPerView[1])&&contaPerView[18].toUpperCase().startsWith("V_EMP_")){
								viewFlag=false;
								break;
							}
						}
						if(!hasFieldReadPriv(temp,true)&&viewFlag) {
                            context="";
                        }
						if ("1".equals(column)||this.tableTypeFlag) {
							if("0".equals(dataarea)) {
                                if(this.tableTypeFlag)//第一行时 topPix应为h_tableHeight
                                {
                                    topPix = Float.parseFloat(temp[3]) + (n - 1)* h_tableHeight+h_tableHeight;//
                                }
                                else {
                                    topPix = Float.parseFloat(temp[3]) + (n - 1) * h_tableHeight;//
                                }
                            } else{
								topPix = Float.parseFloat(temp[3]) + n* (h_tableHeight)-1;
							}
							if(!this.tableTypeFlag) {
								topPix += pix * (n - 1);
							}

							if (isFirst) {
                                hr_pix = (topPix+deltaTop);
                            }
						} else {
							if(x==0){
								div_Top = topPix;
								x++;
							}
							left = String.valueOf(Float.parseFloat(left) + (n - 1)* h_tableWidth + pix * (n - 1));
						}
						if(istotal&& "&nbsp;".equalsIgnoreCase(context)) {
                            context="&nbsp;&nbsp;";
                        }
						if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&!"&nbsp;&nbsp;".equalsIgnoreCase(context)&&((temp[20].toUpperCase().indexOf("<NAMECOUNT>")!=-1||temp[20].toUpperCase().indexOf("<NAMEVALUE>")!=-1)&&"N".equalsIgnoreCase(temp[9])))
						{
							int slope=Integer.parseInt(temp[6]);
							StringBuffer format =new StringBuffer("");
							for(int sindex=0;sindex<slope;sindex++)
							{
								format.append("0");
							}
							myformat = new DecimalFormat(format.length()>0?("0."+format.toString()):"0");
							context=myformat.format(Float.parseFloat(context));
						}
						/**有合并行的，不分栏，多行数据区才可以合并*///当下面有空值的时候，空值上面的第一个画不出来
						if(mergeitemid[columnNum-1]!=null&&mergeitemid[columnNum-1].trim().length()>0
								&&temp[0].equalsIgnoreCase(mergeitemid[columnNum-1])&&context!=null
								&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)
								&&/*(column.equals("1")&&this.dataarea.equals("1"))&&*/!"&nbsp;&nbsp;".equalsIgnoreCase(context))
						{
							if(recordRow==1)//第一条记录，先不画出，和以后的记录比较，但是要记下绝对定位的坐标
							{
								if(resultSet.isLast())
								{
									if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1]))// row merge
									{
										if(rowmergeindex==0)
										{
											rowValue=context;
											rowLeft=Float.parseFloat(left);
											rowWidth=Float.parseFloat(width);
										}
									if(columnNum==1)
										{
											if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)
											{
												rowmergeindex++;
											}
											else
											{
												if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
													if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
									            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
									            		{
									            				context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
									            		}
									            		else{
									            				context=href+a0100tran+"\")\">"+context+endhref;
									            				
									            		}
									            	}
											    }
												tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
											}
										}
										else if(columnNum==aheaderList.size())
										{
											if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)
											{
												if(context.equalsIgnoreCase(rowValue))
												{
													rowWidth+=Float.parseFloat(width);
													if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
														if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
										            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
										            		{
										            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
										            		}
										            		else{
										            			context=href+a0100tran+"\\')\">"+context+endhref;
										            		}
										            	}
												    }
													tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,context, style_name,true));
												}
												else
												{
													if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
														if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
										            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
										            		{
										            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
										            		}
										            		else{
										            			
										            			context=href+a0100tran+"\\')\">"+context+endhref;
										            		}
										            	}
												    }
													tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,rowValue, style_name,true));
													tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
													
												}
											}
											else
											{
												if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
													if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
									            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
									            		{
									            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
									            		}
									            		else{
									            			context=href+a0100tran+"\\')\">"+context+endhref;
									            		}
									            	}
											    }
									    		tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
											}
										}
										else
										{
											if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0&&rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)
											{
												if(context.equalsIgnoreCase(rowValue))
												{
													rowmergeindex++;
													rowWidth+=Float.parseFloat(width);
												}
												else
												{
													tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,rowValue, style_name,true));
													rowValue=context;
													rowLeft=Float.parseFloat(left);
													rowWidth=Float.parseFloat(width);
													rowmergeindex++;
												}
											}
											else if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//before
											{
												if(context.equalsIgnoreCase(rowValue))
												{
													rowWidth+=Float.parseFloat(width);
													if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
														if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
										            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
										            		{
										            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
										            		}
										            		else{
										            			context=href+a0100tran+"\\')\">"+context+endhref;
										            		}
										            	}
												    }
													tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,context, style_name,true));

												}else
												{
													tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,rowValue, style_name,true));
													tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
				
												}
											}
											else if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)//after
											{
												rowValue=context;
												rowLeft=Float.parseFloat(left);
												rowWidth=Float.parseFloat(width);
												rowmergeindex++;
											}
											else
											{
												if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
													if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
									            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
									            		{
									            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
									            		}
									            		else{
									            			context=href+a0100tran+"\\')\">"+context+endhref;
									            		}
									            	}
											    }
												tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
											}
										}
										
									}else{
							     	    if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
							     	    	if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
							            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
							            		{
							            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
							            		}
							            		else{
							            			context=href+a0100tran+"\\')\">"+context+endhref;
							            		}
							            	}
							        	 }
							        	if (!isGroupTerm && "P".equals(temp[7])) {
                                            context=context;
                                        } else
							        	{
							        		if(!hasFieldReadPriv(temp,true)) {
                                                context="";
                                            }
							        		if (!hasHREF) {
                                                context=context.replaceAll(" ","&nbsp;");
                                            }
							    	    }
							    	
								        tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), 
							    		left, width, aheight,context, style_name,true));
								
									}
								}
								else
								{
						    		toppixvalue[columnNum-1]=topPix;
						    		//liuy 2015-2-28 不分栏，多行数据时合并姓名列第一行姓名没有链接  start
						    		if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
							    		if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
						            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
						            		{
						            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
						            		}
						            		else{
						            			context=href+a0100tran+"\\')\">"+context.replaceAll(" ", "&nbsp")+endhref;
						            		}
						            	}
								    }
								    if (!isGroupTerm && "P".equals(temp[7])) {
                                        context=context;
                                    } else
								    {
							    		if(!hasFieldReadPriv(temp,true)) {
                                            context="";
                                        }
							    		if (!hasHREF) {
                                            context=context.replaceAll(" ","&nbsp;");
                                        }
								    }
								    //liuy 2015-2-28 end
						     		mergevalue[columnNum-1]=context;
						    		if(ismergemain[columnNum-1]!=null&& "true".equalsIgnoreCase(ismergemain[columnNum-1]))//按人员或机构合并
							    	{
								    	mainArr[columnNum-1]=mainidvalue;
							    	}
								}
							}else
							{
								if(ismergemain[columnNum-1]!=null&& "true".equalsIgnoreCase(ismergemain[columnNum-1]))//按人员或机构合并
								{
									if(!mainArr[columnNum-1].equalsIgnoreCase(mainidvalue))//人员或机构不同了，要画格子
									{	
								    	if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
								    		if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
							            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
							            		{
							            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
							            		}
							            		else{
							            			context=href+a0100tran+"\\')\">"+context.replaceAll(" ", "&nbsp")+endhref;
							            		}
							            	}
									    }
									    if (!isGroupTerm && "P".equals(temp[7])) {
                                            context=context;
                                        } else
									    {
								    		if(!hasFieldReadPriv(temp,true)) {
                                                context="";
                                            }
								    		if (!hasHREF) {
                                                context=context.replaceAll(" ","&nbsp;");
                                            }
									    }
									    if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1]))// row merge
									    {
									    	if(columnNum==1)//第一个格要看下一个是否合并
									    	{
									    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)//下一个合并
									    		{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			LazyDynaBean bean = new LazyDynaBean();
									    			bean.set("type",type+"");
									    			bean.set("align", align+"");
									    			bean.set("fontName", fontName);
									    			bean.set("fontSize", fontSize);
									    			bean.set("fontStyle", fontStyle);
									    			bean.set("border", border);
									    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
									    			bean.set("left", left);
									    			bean.set("width", width);
									    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/));
									    			bean.set("content",context);
									    			bean.set("style_name",style_name);
									    			bean.set("cloumn",columnNum+"");
									    			bean.set("gridno", temp[0]);
									    			rowMargeList.add(bean);
									    		}
									    		else
									    		{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
										         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/),context, style_name,true));

									    		}
									    	}
									    	else if(columnNum==aheaderList.size())//最后一个格子
									    	{
									    		if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个合并
												{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			LazyDynaBean bean = new LazyDynaBean();
									    			bean.set("type",type+"");
									    			bean.set("align", align+"");
									    			bean.set("fontName", fontName);
									    			bean.set("fontSize", fontSize);
									    			bean.set("fontStyle", fontStyle);
									    			bean.set("border", border);
									    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
									    			bean.set("left", left);
									    			bean.set("width", width);
									    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/));
									    			bean.set("content",context);
									    			bean.set("style_name",style_name);
									    			bean.set("cloumn",columnNum+"");
									    			bean.set("gridno", temp[0]);
									    			rowMargeList.add(bean);
												}
									    		else{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
										         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/),context, style_name,true));

									    		}
									    	}
									    	else
											{
									    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0||rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个或者后一个有合并
												{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			LazyDynaBean bean = new LazyDynaBean();
									    			bean.set("type",type+"");
									    			bean.set("align", align+"");
									    			bean.set("fontName", fontName);
									    			bean.set("fontSize", fontSize);
									    			bean.set("fontStyle", fontStyle);
									    			bean.set("border", border);
									    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
									    			bean.set("left", left);
									    			bean.set("width", width);
									    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/));
									    			bean.set("content",context);
									    			bean.set("style_name",style_name);
									    			bean.set("cloumn",columnNum+"");
									    			bean.set("gridno", temp[0]);
									    			rowMargeList.add(bean);
												}
									    		else{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
										         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/),mergevalue[columnNum-1], style_name,true));
									    			mergevalue[columnNum-1]=context;

									    		}
											}
									    }
									    else
									    {
									    	/*float ff=0;
							    			if(mergeheight[columnNum-1]>=1)
							    				ff=1*mergeheight[columnNum-1];*/
								         	tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
									         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/),mergevalue[columnNum-1], style_name,true));
									    }
										toppixvalue[columnNum-1]=topPix;
										mergeheight[columnNum-1]=0;
										mergevalue[columnNum-1]=context;
										mainArr[columnNum-1]=mainidvalue;
									}
									else
									{
										mergeheight[columnNum-1]++;
									}
								}																						//n!=1 (this.tableTypeFlag?n!=1:true) 纵向分栏 列表式 判断是否是第一行 第一行时没有行合并 其他分栏不考虑此情况
																														//this.tableTypeFlag?(mergevalue[columnNum-1].equalsIgnoreCase(context)?n!=1:true):true 设置纵分时 上个格内容与当前内容相同时 判断是否是第一行数据 
								else if(mergevalue[columnNum-1]!=null&&!mergevalue[columnNum-1].equalsIgnoreCase(context)&&(this.tableTypeFlag?(mergevalue[columnNum-1].equalsIgnoreCase(context)?n!=1:true):true))//合并的格子的值与上个值不同了，要画格子了、、、第一个合并是是空值有问题
								{
									String va="";
									if(mergevalue[columnNum-1]!=null&&!"".equals(mergevalue[columnNum-1])&&!"&nbsp;".equalsIgnoreCase(mergevalue[columnNum-1])&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
										if(hasHREF){
											if("false".equalsIgnoreCase(no_manger_priv)) {
                                                va=href+a0100tran+"\\')\">"+mergevalue[columnNum-1]+endhref;
                                            } else {
                                                va=mergevalue[columnNum-1];
                                            }
						            	}
								    }
									if (!isGroupTerm && "P".equals(temp[7])) {
                                        va=mergevalue[columnNum-1];
                                    } else
									{
										if(!hasFieldReadPriv(temp,true)) {
                                            mergevalue[columnNum-1]="";
                                        }
										if (!hasHREF) {
                                            va=mergevalue[columnNum-1].replaceAll(" ","&nbsp;");
                                        } else {
                                            va=mergevalue[columnNum-1];
                                        }
									}
									 if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1]))// row merge
									 {
										 if(columnNum==1)//第一个格要看下一个是否合并
									    	{
									    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)//下一个合并
									    		{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			LazyDynaBean bean = new LazyDynaBean();
									    			bean.set("type",type+"");
									    			bean.set("align", align+"");
									    			bean.set("fontName", fontName);
									    			bean.set("fontSize", fontSize);
									    			bean.set("fontStyle", fontStyle);
									    			bean.set("border", border);
									    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
									    			bean.set("left", left);
									    			bean.set("width", width);
									    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/));
									    			bean.set("content",va);
									    			bean.set("style_name",style_name);
									    			bean.set("cloumn",columnNum+"");
									    			bean.set("gridno", temp[0]);
									    			rowMargeList.add(bean);
									    		}
									    		else
									    		{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
										         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/),va, style_name,true));

									    		}
									    	}
									    	else if(columnNum==aheaderList.size())//最后一个格子
									    	{
									    		if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个合并
												{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			LazyDynaBean bean = new LazyDynaBean();
									    			bean.set("type",type+"");
									    			bean.set("align", align+"");
									    			bean.set("fontName", fontName);
									    			bean.set("fontSize", fontSize);
									    			bean.set("fontStyle", fontStyle);
									    			bean.set("border", border);
									    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
									    			bean.set("left", left);
									    			bean.set("width", width);
									    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/));
									    			bean.set("content",va);
									    			bean.set("style_name",style_name);
									    			bean.set("cloumn",columnNum+"");
									    			bean.set("gridno", temp[0]);
									    			rowMargeList.add(bean);
												}
									    		else{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
										         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/),va, style_name,true));

									    		}
									    	}
									    	else
											{
									    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0||rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个或者后一个有合并
												{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			LazyDynaBean bean = new LazyDynaBean();
									    			bean.set("type",type+"");
									    			bean.set("align", align+"");
									    			bean.set("fontName", fontName);
									    			bean.set("fontSize", fontSize);
									    			bean.set("fontStyle", fontStyle);
									    			bean.set("border", border);
									    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
									    			bean.set("left", left);
									    			bean.set("width", width);
									    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/));
									    			bean.set("content",va);
									    			bean.set("style_name",style_name);
									    			bean.set("cloumn",columnNum+"");
									    			bean.set("gridno", temp[0]);
									    			rowMargeList.add(bean);
												}
									    		else{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
										         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/),va, style_name,true));

									    		}
											}
									 }
									 else
									 {
										 if("&nbsp;".equals(va)||toppixvalue[columnNum-1]==0.0||n==1)
										 {
											 if(!va.equals(context)) {
												 tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
											 }
										 }
										 else
										 {
											 /*float ff=0;
								    			if(mergeheight[columnNum-1]>=1)
								    				ff=1*mergeheight[columnNum-1];*/
											 //mergeheight 合并行数 从0计数
											
												 tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
														 left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)/*-ff*/),va, style_name,true));
											 
										 }
									 }
									toppixvalue[columnNum-1]=topPix;
									mergeheight[columnNum-1]=0;
									mergevalue[columnNum-1]=context;
								}
								else
								{
									mergeheight[columnNum-1]++;
								}
								if((recordRow>0&&recordRow%rows==0)||resultSet.isLast())
								{
									if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
										if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
						            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
						            		{
						            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
						            		}
						            		else{
						            			context=href+a0100tran+"\\')\">"+context+endhref;
						            		}
						            	}
								    }
									if (!isGroupTerm && "P".equals(temp[7])) {
                                        context=context;
                                    } else
									{
										if(!hasFieldReadPriv(temp,true)) {
                                            mergevalue[columnNum-1]="";
                                        }
										if (!hasHREF) {
                                            context=context.replaceAll(" ","&nbsp;");
                                        }
									}
									if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1])&&context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context))// row merge
									 {
										 if(columnNum==1)//第一个格要看下一个是否合并
									    	{
									    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)//下一个合并
									    		{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			LazyDynaBean bean = new LazyDynaBean();
									    			bean.set("type",type+"");
									    			bean.set("align", align+"");
									    			bean.set("fontName", fontName);
									    			bean.set("fontSize", fontSize);
									    			bean.set("fontStyle", fontStyle);
									    			bean.set("border", border);
									    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
									    			bean.set("left", left);
									    			bean.set("width", width);
									    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/));
									    			bean.set("content",context);
									    			bean.set("style_name",style_name);
									    			bean.set("cloumn",columnNum+"");
									    			bean.set("gridno", temp[0]);
									    			rowMargeList.add(bean);
									    		}
									    		else
									    		{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
										         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/),context, style_name,true));

									    		}
									    	}
									    	else if(columnNum==aheaderList.size())//最后一个格子
									    	{
									    		if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个合并
												{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			LazyDynaBean bean = new LazyDynaBean();
									    			bean.set("type",type+"");
									    			bean.set("align", align+"");
									    			bean.set("fontName", fontName);
									    			bean.set("fontSize", fontSize);
									    			bean.set("fontStyle", fontStyle);
									    			bean.set("border", border);
									    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
									    			bean.set("left", left);
									    			bean.set("width", width);
									    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/));
									    			bean.set("content",context);
									    			bean.set("style_name",style_name);
									    			bean.set("cloumn",columnNum+"");
									    			bean.set("gridno", temp[0]);
									    			rowMargeList.add(bean);
												}
									    		else{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
										         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/),context, style_name,true));

									    		}
									    	}
									    	else
											{
									    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0||rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个或者后一个有合并
												{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			LazyDynaBean bean = new LazyDynaBean();
									    			bean.set("type",type+"");
									    			bean.set("align", align+"");
									    			bean.set("fontName", fontName);
									    			bean.set("fontSize", fontSize);
									    			bean.set("fontStyle", fontStyle);
									    			bean.set("border", border);
									    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
									    			bean.set("left", left);
									    			bean.set("width", width);
									    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/));
									    			bean.set("content",context);
									    			bean.set("style_name",style_name);
									    			bean.set("cloumn",columnNum+"");
									    			bean.set("gridno", temp[0]);
									    			rowMargeList.add(bean);
												}
									    		else{
									    			/*float ff=0;
									    			if(mergeheight[columnNum-1]>=1)
									    				ff=1*mergeheight[columnNum-1];*/
									    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
										         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])/*-ff*/),context, style_name,true));

									    		}
											}
									 }
									else
									{
										/*/*float ff=0;
						    			if(mergeheight[columnNum-1]>=1)
						    				ff=1*mergeheight[columnNum-1];*/
										//最后一行时 toppixvalue对应的值 
								    	tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
										    	left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)/*+(mergeheight[columnNum-1])*//*-ff*/),context, style_name,true));
								    	
									}
								}
							}

					    	if(hr_i<1) {
                                hr_left=Float.parseFloat(left);
                            }
					     	hr_width=Float.parseFloat(left)+Float.parseFloat(width)-hr_left;
				    		hr_i++;
						}
						else
						{
							if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1])&&context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&!"&nbsp;&nbsp;".equalsIgnoreCase(context))// row merge
							{
								if(rowmergeindex==0)
								{
									rowValue=context;
									rowLeft=Float.parseFloat(left);
									rowWidth=Float.parseFloat(width);
								}
								if(columnNum==1)
								{
									if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)
									{
										rowmergeindex++;
									}
									else
									{
										if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
											if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
							            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
							            		{
							            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
							            		}
							            		else{
							            			context=href+a0100tran+"\\')\">"+context+endhref;
							            		}
							            	}
									    }
										tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
									}
								}
								else if(columnNum==aheaderList.size())
								{
									if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)
									{
										if(context.equalsIgnoreCase(rowValue))
										{
											rowWidth+=Float.parseFloat(width);
											if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
												if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
								            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
								            		{
								            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
								            		}
								            		else{
								            			context=href+a0100tran+"\\')\">"+context+endhref;
								            		}
								            	}
										    }
											tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,context, style_name,true));
										}
										else
										{
											if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
												if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
								            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
								            		{
								            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
								            		}
								            		else{
								            			context=href+a0100tran+"\\')\">"+context+endhref;
								            		}
								            	}
										    }
											tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,rowValue, style_name,true));
											tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
											
										}
									}
									else
									{
										if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
											if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
							            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
							            		{
							            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
							            		}
							            		else{
							            			context=href+a0100tran+"\\')\">"+context+endhref;
							            		}
							            	}
									    }
							    		tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
									}
								}
								else
								{
									if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0&&rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)
									{
										if(context.equalsIgnoreCase(rowValue))
										{
											rowmergeindex++;
											rowWidth+=Float.parseFloat(width);
										}
										else
										{
											tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,rowValue, style_name,true));
											rowValue=context;
											rowLeft=Float.parseFloat(left);
											rowWidth=Float.parseFloat(width);
											rowmergeindex++;
										}
									}
									else if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//before
									{
										if(context.equalsIgnoreCase(rowValue))
										{
											rowWidth+=Float.parseFloat(width);
											if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
												if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
								            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
								            		{
								            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
								            		}
								            		else{
								            			context=href+a0100tran+"\\')\">"+context+endhref;
								            		}
								            	}
										    }
											tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,context, style_name,true));

										}else
										{
											tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,rowValue, style_name,true));
											tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
		
										}
									}
									else if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)//after
									{
										rowValue=context;
										rowLeft=Float.parseFloat(left);
										rowWidth=Float.parseFloat(width);
										rowmergeindex++;
									}
									else
									{
										if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
											if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
							            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
							            		{
							            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
							            		}
							            		else{
							            			context=href+a0100tran+"\\')\">"+context+endhref;
							            		}
							            	}
									    }
										tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
									}
								}
								
							}
							else
							{
								if(mergeitemid[columnNum-1]!=null&&mergeitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(mergeitemid[columnNum-1])/*&&(column.equals("1")&&this.dataarea.equals("1"))*/&&("".equalsIgnoreCase(context)|| "&nbsp;".equalsIgnoreCase(context)|| "&nbsp;&nbp;".equalsIgnoreCase(context))&&mergevalue[columnNum-1]!=null&&!"".equals(mergevalue[columnNum-1])&&!"&nbsp;".equalsIgnoreCase(mergevalue[columnNum-1]))
								{
										if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1]))// row merge
										{
											if(columnNum-1>0&&rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0&&(context==null|| "&nbsp;".equalsIgnoreCase(context)|| "&nbsp;&nbsp;".equalsIgnoreCase(context)))
											{
										    	tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,rowValue, style_name,true));
											}
											else
											{
												tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
												    	left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])),mergevalue[columnNum-1], style_name,true));

											}
										}
										else
										{
											tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
											    	left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])),mergevalue[columnNum-1], style_name,true));

										}
								}
								else if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1]))// row merge
								{
									if(columnNum-1>0&&rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0&&(context==null|| "&nbsp;".equalsIgnoreCase(context)|| "&nbsp;&nbsp;".equalsIgnoreCase(context))&&(!"".equals(rowValue)&&!"&nbsp;".equalsIgnoreCase(rowValue)))
									{
								    	tempBuf.append(executeAbsoluteTable2(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), String.valueOf(rowLeft), String.valueOf(rowWidth), aheight,rowValue, style_name,true));
								    	rowValue="&nbsp;";
									}
								}
						    	if(context!=null&&!"".equals(context)&&!"&nbsp;".equalsIgnoreCase(context)&&temp[19]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
						    		if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
					            		if("mobile".equalsIgnoreCase(this.getReturnflag()))
					            		{
					            			context=href+a0100tran+"\\',\""+context+"\")\">"+context+endhref;
					            		}
					            		else{
					            			context=href+a0100tran+"\\')\">"+context+endhref;
					            		}
					            	}
					    	    }
				        		tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(topPix), left, width, aheight,context, style_name,true));
							}
					        if(hr_i<1) {
                                hr_left=Float.parseFloat(left);
                            }
					         hr_width=Float.parseFloat(left)+Float.parseFloat(width)-hr_left;
				    	    hr_i++;
						}
						isFirst = false;
						if(n==1) {//纵分 列表式 n=1为每列开始行
							toppixvalue[columnNum-1]=topPix;
							mergeheight[columnNum-1]=0; //累计合并行清空
							
						}
						bodyContext.append(tempBuf);
					}// end headerList loop
				//}
				
				if (columnLine != null && "1".equals(columnLine)) {
					if("1".equals(dataarea)) {
                        columnLine="0";
                    }
					if (n != 1 && "1".equals(column)) {
						bodyContext.append(" \n <hr style=\"position:absolute;top:"+ hr_pix+ "px;left:"+hr_left+"px;width:"+hr_width+"px;height:\"> ");
					}else if ("2".equals(column)) {
						/**bs先不要中间的分割线了*/
						//bodyContext.append(" \n <div style=\'position:absolute;top:"+(div_Top+letop+5)+";left:"+(hr_left+h_tableWidth+pix/2)+";height:"+h_tableHeight+";width:1;background-color:#000000;'></div> ");
					}
				}
				i++;
				n++;
			}// end while loop
			if(isbreak)
			{
				int columnNum = 0;
				StringBuffer tempBuf=new StringBuffer("");
				for (Iterator t = aheaderList.iterator(); t.hasNext();) {
					tempBuf.setLength(0);
					columnNum++;
					topvalue_index++;
					String[] temp = (String[]) t.next();
					int type = 1;
					String border = "1";
					int align = Integer.parseInt(temp[13]);
					String fontName = temp[10];
					String fontSize = temp[12];
					String fontStyle = temp[11];
					String left = temp[2];
					String width = temp[4];
					String aheight = temp[5];//h_tableHeight+"";
					// 处理虚线 L,T,R,B,
					String style_name = getStyleName2(temp,this.printGrind,begin);
					if (!"1".equals(column)) {
						left = String.valueOf(Float.parseFloat(left) + (n - 1)* h_tableWidth + pix * (n - 1));
					} 
					if((this.getTextFormatMap().size()>0&&this.getTextFormatMap().get(temp[0])==null)&&("1".equals(column)&& "1".equals(this.dataarea))) {
                        continue;
                    }
					if(mergeitemid[columnNum-1]!=null&&mergeitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(mergeitemid[columnNum-1])&&mergevalue[columnNum-1]!=null&&!"".equalsIgnoreCase(mergevalue[columnNum-1])&&!"&nbsp;".equalsIgnoreCase(mergevalue[columnNum-1]))
					{
						String va="";
		                String href="";
		                if("1".equals(infor_Flag)) {
		                	if(hasHREF&&"false".equalsIgnoreCase(no_manger_priv)){
		                		href+="<a href=\"javascript:openSelfInfo(\\'/workbench/browse/showselfinfo.do?b_search=link`userbase="+
		                		getDBPre(resultSet,dbpre)+"`returnvalue=100000`flag=notself`a0100=";
		                		if("mobile".equalsIgnoreCase(this.getReturnflag())) {
                                    href="<a href=\"javascript:test(\\'"+getDBPre(resultSet,dbpre)+"\\',\\'";
                                }
		                	}
		                }

						if(mergevalue[columnNum-1]!=null&&!"".equals(mergevalue[columnNum-1])&&!"&nbsp;".equalsIgnoreCase(mergevalue[columnNum-1])&&temp[18]!=null&& "a0101".equalsIgnoreCase(temp[18])&& "1".equals(this.infor_Flag)){
							if(hasHREF){
								if("false".equalsIgnoreCase(no_manger_priv)) {
                                    va=href+a0100tran+"\\')\">"+mergevalue[columnNum-1]+endhref;
                                } else {
                                    va=mergevalue[columnNum-1];
                                }
			            	}
					    }
						if (!isGroupTerm && "P".equals(temp[7])) {
                            va=mergevalue[columnNum-1];
                        } else
						{
							if(!hasFieldReadPriv(temp,true)) {
                                mergevalue[columnNum-1]="";
                            }
							if (!hasHREF) {
                                va=mergevalue[columnNum-1].replaceAll(" ","&nbsp;");
                            } else {
                                va=mergevalue[columnNum-1];
                            }
						}
						if(rowitemid[columnNum-1]!=null&&rowitemid[columnNum-1].trim().length()>0&&temp[0].equalsIgnoreCase(rowitemid[columnNum-1]))// row merge
						 {
							 if(columnNum==1)//第一个格要看下一个是否合并
						    	{
						    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0)//下一个合并
						    		{
						    			LazyDynaBean bean = new LazyDynaBean();
						    			bean.set("type",type+"");
						    			bean.set("align", align+"");
						    			bean.set("fontName", fontName);
						    			bean.set("fontSize", fontSize);
						    			bean.set("fontStyle", fontStyle);
						    			bean.set("border", border);
						    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
						    			bean.set("left", left);
						    			bean.set("width", width);
						    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])));
						    			bean.set("content",va);
						    			bean.set("style_name",style_name);
						    			bean.set("cloumn",columnNum+"");
						    			bean.set("gridno", temp[0]);
						    			rowMargeList.add(bean);
						    		}
						    		else
						    		{
						    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
							         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])),va, style_name,true));

						    		}
						    	}
						    	else if(columnNum==aheaderList.size())//最后一个格子
						    	{
						    		if(rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个合并
									{
						    			LazyDynaBean bean = new LazyDynaBean();
						    			bean.set("type",type+"");
						    			bean.set("align", align+"");
						    			bean.set("fontName", fontName);
						    			bean.set("fontSize", fontSize);
						    			bean.set("fontStyle", fontStyle);
						    			bean.set("border", border);
						    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
						    			bean.set("left", left);
						    			bean.set("width", width);
						    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])));
						    			bean.set("content",va);
						    			bean.set("style_name",style_name);
						    			bean.set("cloumn",columnNum+"");
						    			bean.set("gridno", temp[0]);
						    			rowMargeList.add(bean);
									}
						    		else{
						    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
							         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])),va, style_name,true));

						    		}
						    	}
						    	else
								{
						    		if(rowitemid[columnNum]!=null&&rowitemid[columnNum].trim().length()>0||rowitemid[columnNum-2]!=null&&rowitemid[columnNum-2].trim().length()>0)//前一个或者后一个有合并
									{
						    			LazyDynaBean bean = new LazyDynaBean();
						    			bean.set("type",type+"");
						    			bean.set("align", align+"");
						    			bean.set("fontName", fontName);
						    			bean.set("fontSize", fontSize);
						    			bean.set("fontStyle", fontStyle);
						    			bean.set("border", border);
						    			bean.set("topvalue", String.valueOf(toppixvalue[columnNum-1]));
						    			bean.set("left", left);
						    			bean.set("width", width);
						    			bean.set("height", String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])));
						    			bean.set("content",va);
						    			bean.set("style_name",style_name);
						    			bean.set("cloumn",columnNum+"");
						    			bean.set("gridno", temp[0]);
						    			rowMargeList.add(bean);
									}
						    		else{
						    			tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
							         			left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])),va, style_name,true));

						    		}
								}
						 }
						else
						{
					    	tempBuf.append(executeAbsoluteTable(type, align,fontName, fontSize, fontStyle, border, String.valueOf(toppixvalue[columnNum-1]), 
							    	left, width, String.valueOf((mergeheight[columnNum-1]+1)*Float.parseFloat(aheight)-(mergeheight[columnNum-1])),va, style_name,true));
						}
					}
					bodyContext.append(tempBuf.toString());
				}
			}
			StringBuffer margeBuf = new StringBuffer();
			if(rowMargeList.size()>0)
			{
				HashMap existMap = new HashMap();
			    for(int out=0;out<rowMargeList.size();out++)
			    {
			    	if(existMap.get(out+"")!=null) {
                        continue;
                    }
			    	LazyDynaBean outBean = (LazyDynaBean)rowMargeList.get(out);
				    String top = (String)outBean.get("topvalue");
				    String cloumn = (String)outBean.get("cloumn");
				    String height=(String)outBean.get("height");
				    String content=(String)outBean.get("content");
				    String gridno = (String)outBean.get("gridno");
				    String left = (String)outBean.get("left");
					String width=(String)outBean.get("width");
					String type=(String)outBean.get("type");
					String fontName=(String)outBean.get("fontName");
					String fontSize=(String)outBean.get("fontSize");
					String fontStyle = (String)outBean.get("fontStyle");
					String  border=(String)outBean.get("border");
					String style_name=(String)outBean.get("style_name");
					String align=(String)outBean.get("align");
					int columnNum=1;
					int col = Integer.parseInt(cloumn);
				    for(int in=0;in<rowMargeList.size();in++)
				    {
				     	LazyDynaBean inBean =(LazyDynaBean)rowMargeList.get(in);
				    	String intop = (String)inBean.get("topvalue");
					    String incloumn = (String)inBean.get("cloumn");
					    String inheight=(String)inBean.get("height");
					    String incontent=(String)inBean.get("content");
					    String ingridno = (String)inBean.get("gridno");
					    String inwidth=(String)inBean.get("width");
					    int inclo = Integer.parseInt(incloumn);
					    if((top.equalsIgnoreCase(intop)&&cloumn.equalsIgnoreCase(incloumn))||!(top.equalsIgnoreCase(intop)))//同一个格子
					    {
						    continue;
					    }
					    if(top.equalsIgnoreCase(intop)&&height.equalsIgnoreCase(inheight)&&content.equalsIgnoreCase(incontent)&&inclo==(col+columnNum))
					    {
							columnNum++;
							width=String.valueOf(Float.parseFloat(width)+Float.parseFloat(inwidth));
							existMap.put(in+"", "1");
					    }
					    else
					    {
					    	continue;
					    }
				    }
				    margeBuf.append(executeAbsoluteTable(Integer.parseInt(type), Integer.parseInt(align),fontName, fontSize, fontStyle, border, top, 
					    	left, width, height,content, style_name,true));
		    	}  	
			}
			allBuffer.append(margeBuf.toString());
 			allBuffer.append(bodyContext.toString());
			if(this.isGroupPoint2!=null&& "1".equals(this.isGroupPoint2)&&this.fzhj!=null&&this.fzhj.length>0&&writeGroup)
			{
				allBuffer.append(getPageCountRows(columnLine, pix,
						h_tableHeight, h_tableWidth, aheaderList, column, n,
						bottomnList, this.fzhj, zeroPrint, fzhjCount2,
						"hmuster.label.pageCount"));
				n++;
				fzhjCount2 = initCount(this.fzhj);
			}
			if(!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)&& "1".equals(isGroupNoPage)){
				if (this.fzhj != null && this.fzhj.length > 0&&this.isfzhj&&!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)&&(totalPage==currpage))
				{
					
					allBuffer.append(getPageCountRows(columnLine, pix,
							h_tableHeight, h_tableWidth, aheaderList, column, n,
							bottomnList, this.fzhj, zeroPrint, fzhjCount,
							"planar.stat.total"));
					n++;
				}
			}else
			{
				if (this.fzhj != null && this.fzhj.length > 0&&this.isfzhj&&!isGroupTerm && isGroupPoint != null&& "1".equals(isGroupPoint)&&(flag||totalPage==currpage||isValues== 0))
				{
					
					allBuffer.append(getPageCountRows(columnLine, pix,
							h_tableHeight, h_tableWidth, aheaderList, column, n,
							bottomnList, this.fzhj, zeroPrint, fzhjCount,
							"planar.stat.total"));
					n++;
				}
			}
			//处理空行打印问题
			if("1".equals(emptyRow))
			{  
				if("1".equals(dataarea)) {
                    columnLine="0";
                }
				if(n<=rows) {
					while(n<=rows)//当前列为空的值打印满
					{
						n++;
						allBuffer.append(getEmptyRows(columnLine,pix,h_tableHeight,h_tableWidth, aheaderList,
								column,n,bottomnList,zeroPrint,(realColNum<colNum?false:true),n-1==rows,begin));
						/*if(!"1".equals(column)) {
							n++;
							allBuffer.append(getEmptyRows(columnLine,pix,h_tableHeight,h_tableWidth, aheaderList,
									column,n,bottomnList,zeroPrint,(realColNum<colNum?false:true)));
						}else {
							allBuffer.append(getEmptyRows(columnLine,pix,h_tableHeight,h_tableWidth, aheaderList,
									column,n,bottomnList,zeroPrint,(realColNum<colNum?false:true)));
							n++;
						}*/
					}
				}
				
				if(realColNum<colNum) {
					int empNum=1;
					ArrayList aheadListBak=(ArrayList)aheaderList.clone();
					for(int k=0;k<colNum-realColNum;k++) {
						aheadListBak=updateList(aheadListBak,pix);
						while(empNum<=rows) {
							empNum++;
							allBuffer.append(getEmptyRows(columnLine,pix,h_tableHeight,h_tableWidth, aheadListBak,
									column,empNum,bottomnList,zeroPrint,((k==(colNum-realColNum-1))?true:false),empNum-1==rows,begin));
						}
						empNum=1;
					}
				}
			}
						
			/* 显示页小计 */
			if (field != null && field.length > 0
					/*&& (nOperation == 1 || nOperation == 3)*/) {
				allBuffer.append(getPageCountRows(columnLine, pix,
						h_tableHeight, h_tableWidth, aheaderList, column, n,
						bottomnList, field, zeroPrint, pageCount,
						"hmuster.label.pageCount"));
				n++;
			}
			if (fields != null && fields.length > 0
					/*&& (nOperation == 2 || nOperation == 3)*/) // 显示页累计
			{
				totalCount=this.totalCounts(totalCount, fields, isGroupTerm, isGroupPoint, rows, currpage, tableName, sql);
				allBuffer.append(getPageCountRows(columnLine, pix,
						h_tableHeight, h_tableWidth, aheaderList, column, n,
						bottomnList, fields, zeroPrint, totalCount,
						"hmuster.label.toatlCount"));
				n++;
			}
			
			/**总计*/
			if(this.zj!=null&&this.zj.length>0&&currpage==this.totalPage/*&&fields!=null&&fields.length>0*/)
			{
				zjCount=totalCounts3(zjCount,zj,tableName,bottomnList);  // 重新计算总计
				allBuffer.append(getPageCountRows(columnLine, pix,
						h_tableHeight, h_tableWidth, aheaderList, column, n,
						bottomnList, this.zj, zeroPrint, zjCount,
						"workdiary.message.total"));
				n++;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			try
			{
				if(resultSet!=null)
				{
					resultSet.close();
					resultSet=null;
				}
				if(stmt!=null) {
                    stmt.close();
                }
				if(a_conn!=null) {
                    a_conn.close();
                }
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		
		ArrayList list = new ArrayList();
		list.add(allBuffer.toString()); // 表体内容
		list.add(groupV); // 当前页的分组指标
		list.add(new Integer(n)); // 当前页的行数
		list.add(photoList); // 如果表中需显示照片，则列出显示的临时照片名称列表，已备删除
		this.setGroupNcode(groupN);
		return list;
	}
	
	/**
	 * 用户是否有该指标读权限
	 * @param grid 单元格定义
	 * @param isSplidColumn 是否分栏
	 * @return
	 */
	private boolean hasFieldReadPriv(String[] grid, boolean isSplidColumn){
		boolean b=true;
		String[] clogrid=grid.clone();
		// 如果用户对该指标无权限，则不予显示数据(对于考勤模块放开权限)
		int idx=isSplidColumn?18:19;
		if(!"81".equals(this.modelFlag)&&!"stipend".equals(this.modelFlag)&&clogrid[idx]!=null&&clogrid[idx].trim().length()>0)
		{
			FieldItem fielditem = DataDictionary.getFieldItem(clogrid[idx]);
			if("5".equals(this.modelFlag)&&!clogrid[idx-1].toUpperCase().startsWith("V_EMP_")) {
				clogrid[idx]=clogrid[idx].replaceAll("_1","").replace("_2", "");
			}
			if(fielditem!=null){
				if(!"nbase".equalsIgnoreCase(clogrid[idx])&&!"a0100".equalsIgnoreCase(clogrid[idx])&&
						"0".equalsIgnoreCase(this.userView.analyseFieldPriv(clogrid[idx])))
				{
				
					if(clogrid[idx-1]!=null&&clogrid[idx-1].toUpperCase().startsWith("V_EMP_"))
					{
						
					}else if("Z03".equalsIgnoreCase(clogrid[18].trim())){//针对招聘 指标权限放开changxy 29267
						
					}
					else
					{
						b=false;
					}
				}
			}
		}
		return b;
	}
	
	//分栏 打印空行  isLastCol 纵向分栏 列表式 判断是否是最后一列  最后一列 左 下 右 有边线 其他只有左 下有边线
	public String getEmptyRows(String columnLine, float pix,
			float h_tableHeight, float h_tableWidth, ArrayList headerList,
			String column, int n, ArrayList bottomnList,
			String zeroPrint,boolean isLastCol,boolean islastRow,int begin) {
		StringBuffer bodyContext = new StringBuffer("");
		float hr_pix = 0; // 分隔线的坐标
		float hr_width=0;
		float hr_left=0;
		float hr_i=0;
		boolean isFirst = true; // 表的第一单元格
		int num=0;
		for (Iterator t = headerList.iterator(); t.hasNext();) {
			num++;
			String[] temp = (String[]) t.next();
			String gridno=temp[0];
			if(this.getTextFormatMap().size()>0&&this.getTextFormatMap().get(temp[0])==null) {
                continue;
            }
			int type = 1;
			String border = "1";
			int align = Integer.parseInt(temp[13]);
			String fontName = temp[10];
			String fontSize = temp[12];
			String fontStyle = temp[11];
			temp[14]="1";//L
			temp[15]="0";//T
			temp[17]="1";//B
			
			if("1".equals(column)&&pix!=0) {//横分 分割线不为0 时 空单元格上边线不为0
				temp[15]="1";
			}
			if(isLastCol&&num==headerList.size()) {
				temp[16]="1";//R
			}else {
				temp[16]="0";//R
			}
			float topPix = Float.parseFloat(temp[3]);
			String left = temp[2];

			if ("1".equals(column)|| "2".equals(column)) {
				if("0".equals(dataarea)) {
                    topPix = Float.parseFloat(temp[3]) + (n - 1)* h_tableHeight;
                } else{
					topPix = Float.parseFloat(temp[3]) + (n-1)* (h_tableHeight)-n;
				}
				/*topPix += pix * (n - 1);
				topPix = Float.parseFloat(temp[3]) + n * h_tableHeight;*/
				if("0".equals(dataarea)&&!this.tableTypeFlag) {
					topPix += pix * n;
				}
                //topPix=(float)(topPix-this.getTextDataHeight());
				if (isFirst) {
					hr_pix = topPix+deltaTop;
				}

			} else {
				left = String.valueOf(Float.parseFloat(left) + (n - 1)
						* h_tableWidth + pix * (n - 1));

			}
            
			String width = temp[4];
			String aheight = (Float.parseFloat(temp[5])-1)+"";
			String context = "&nbsp;";

			boolean wordWarp=false;
	    	if(musterCellMap!=null&&musterCellMap.get(gridno)!=null) {
	    		if(musterCellMap.get(gridno).get("nWordWrap")!=null&&"1".equals(musterCellMap.get(gridno).get("nWordWrap"))) {
	    			wordWarp=true;
	    		}
	    	}
			// 处理虚线 L,T,R,B,
			String style_name = getStyleName2(temp,this.printGrind,3);
			if(this.tableTypeFlag){
				if(begin==0) {//列表式 第一列 左边线展现
					style_name="RecordRow_self_t";
				}else {
					style_name="RecordRow_self_lt";
				}
			}
			bodyContext.append(executeAbsoluteTable(type, align, fontName,
					fontSize, fontStyle, (islastRow?"1":"0"), String.valueOf(topPix), left,
					width, aheight, context, style_name,wordWarp));
//			hr_width+=Float.parseFloat(width);
			if(hr_i<1) {
                hr_left=Float.parseFloat(left);
            }
			hr_width=Float.parseFloat(left)+Float.parseFloat(width)-hr_left;
			hr_i++;
			isFirst = false;
		}
		if (columnLine != null && "1".equals(columnLine)) {
			if (n != 1 && "1".equals(column)) {
				bodyContext.append(" \n <hr style=\"position:absolute;top:"
						+ hr_pix + "px;left:"+hr_left+"px;width:"+hr_width+"px;height:\"> ");
			}
		}

		return bodyContext.toString();
	}
	
	
	
	

	/**
	 * 得到每页的行数(横向分栏)
	 */
	public int getHorizontalrows(int bodyHeight, float h_tableHeight,
			String pix, int nOperation) {
		int rows = 0;
		float increaseHeight = 0;
		
		if (nOperation == 1 || nOperation == 3) // 有页小计
		{
			increaseHeight += h_tableHeight;
			increaseHeight += Float.parseFloat(pix);
		}
		if (nOperation == 2 || nOperation == 3) // 有页累计
		{
			increaseHeight += h_tableHeight;
			increaseHeight += Float.parseFloat(pix);
		}
		for (int i = 1; i < bodyHeight / h_tableHeight; i++) {
			increaseHeight += h_tableHeight;
			increaseHeight += Float.parseFloat(pix);
			if (increaseHeight > bodyHeight) {
                break;
            }
			rows++;
		}
		if (rows == 0) {
            rows = 1;
        }
		return rows;
	}

	
	
	
	
	
	
	
	
	
	/**
	 * 表头列集合 && 表体总高 && 表体总宽 && 表的左坐标及顶坐标
	 * 
	 * @param tabid
	 * @param column
	 *            0: 不分栏, 1: 横向分栏, 2: 纵向分栏
	 *            多行数据区时，column=1
	 * @return ArrayList
	 */
	public ArrayList getHeaderList(String tabid, String column)
			throws GeneralException {
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		float bodyWidth = 0; // 表总宽
		float bodyHeight = 0; // 表总高
		ArrayList bodyList = new ArrayList();
		float[] ltPix = { 0, 0 };
		StringBuffer sql = new StringBuffer();
		sql.append("select GridNo,Hz,Rleft,RTop+");
		sql.append(this.t_space);
		sql.append(",RWidth,RHeight,Slope,Flag,noperation,Field_Type,fontName,");
		sql.append("fontEffect,fontSize,align,L,T,R,B,Field_Name,nhide,ExtendAttr,SETNAME ");
		sql.append("from muster_cell where   tabid=");
		sql.append(tabid);
		sql.append(" order by Rleft,RTop ");
		try {
			int tempVar=1;
			if(this.getTextFormatMap().size()>0&& "1".equals(this.column)&& "1".equals(this.dataarea))
			{
				String str="select max(rheight) from muster_cell where tabid="+tabid;
				Set keySet = this.getTextFormatMap().keySet();
				String str_key="";
				for(Iterator it = keySet.iterator();it.hasNext();)
				{
					String key = (String)it.next();
					str_key+=","+key;
				}
				str_key=str_key.substring(1);
				str+=" and gridno in("+str_key+")";
				rowSet = dao.search(str);
				while(rowSet.next())
				{
					bodyHeight=rowSet.getFloat(1);
				}
			}
			
			/* 将标头放入list中 */
			rowSet = dao.search(sql.toString());  //查询所有列头信息

			float aRleft = 0;
			float aRleft2 = 0;
			int aa = 0;
			int xx=0;
			float moveLeft=0;//当某一列的rleft为负值时 表格其他列后移 [24683]
			while (rowSet.next()) {
				/* #### 所有花名册都往左靠，边距为20像素 ##### */
				if (aa == 0) {
					float tt = rowSet.getFloat(3);//Rleft 距离左边的距离					
					if(tt-20>0) {
                        lt=tt-20;
                    } else {
                        lt =0;
                    }
				
				}
				aa++;

				/* 表的左坐标及顶坐标 */
				if (ltPix[0] == 0) {
					ltPix[0] = rowSet.getFloat(3) - lt;
					ltPix[1] = (float)(rowSet.getFloat(4)-this.getTextDataHeight());
				}

				String[] temp = new String[21];
				String fieldName = rowSet.getString("Field_name");
				FieldItem item =null;
				if(fieldName!=null&&fieldName.trim().length()>0)
				{
					if(!"5".equals(this.modelFlag)){			//如果不是工资变动模块
						item=DataDictionary.getFieldItem(fieldName);
					}else
					{
						item=DataDictionary.getFieldItem(fieldName.substring(0,fieldName.length()-2));
					}					
				}	
				for (int i = 0; i < 20; i++) {
					if(i==1)
					{
						temp[i]=Sql_switcher.readMemo(rowSet,"hz");
					}
					else if (i == 2) {
						if (rowSet.getFloat(i + 1) - lt != ltPix[0]) {
                            temp[i] = String.valueOf(rowSet.getFloat(i + 1)- lt - 1);
                        } else {
                            temp[i] = String.valueOf(rowSet.getFloat(i + 1)- lt);
                        }
						if(moveLeft==0&&Float.parseFloat(temp[i])<0){
							moveLeft=-Float.parseFloat(temp[i]);
						}
						temp[i]=(Float.parseFloat(temp[i])+moveLeft)+"";
					} else if (i == 4) {
						if (rowSet.getFloat(3) - lt != ltPix[0]) {
                            temp[i] = String.valueOf(rowSet.getFloat(i + 1) + 1);
                        } else {
                            temp[i] = String.valueOf(rowSet.getFloat(i + 1));
                        }
					} else if (i == 3) {
						if (rowSet.getFloat(i + 1) != ltPix[1]) {
                            temp[i] = String.valueOf(rowSet.getFloat(i + 1) - 1);
                        } else {
                            temp[i] = String.valueOf(rowSet.getFloat(i + 1));
                        }
					} else if (i == 5) {
						if (rowSet.getFloat(4) != ltPix[1]) {
                            temp[i] = String.valueOf(rowSet.getFloat(i + 1) + 1);
                        } else {
                            temp[i] = String.valueOf(rowSet.getFloat(i + 1));
                        }
					} else if (i == 7) {
						if (rowSet.getString(i + 1)!=null&&("A".equals(rowSet.getString(i + 1))|| "B".equals(rowSet.getString(i + 1))|| "K".equals(rowSet.getString(i + 1)))) {

							if (item == null){
							    if("salary".equals(modelFlag)||"15".equals(modelFlag))// 为了处理工资类别中的A00Z0等
                                {
                                    temp[i] = rowSet.getString(i + 1);
                                } else {
                                    temp[i] = "H";
                                }
							}
							else if ("0".equals(item.getUseflag())) {
                                temp[i] = "H";
                            } else {
                                temp[i] = rowSet.getString(i + 1);
                            }
						} else {
                            temp[i] = rowSet.getString(i + 1);
                        }

					} else if (i == 9) {
						/*if (item != null && !item.getUseflag().equals("0"))
							temp[i] = item.getItemtype();
						else*/
							temp[i] = rowSet.getString(i + 1);
					} else {
                        temp[i] = rowSet.getString(i + 1);
                    }
					
					if(temp[i]==null) {
                        temp[i]="";
                    }
				}
				temp[20]=rowSet.getString(21);
				bodyList.add(temp);//temp  取出库中存储的指标信息

				//每个指标存储的位置信息 表体总高为 cs设置的高度  宽度 累加
				/* 取得表体总宽 */
				if (aRleft2 == 0) {
					aRleft2 = rowSet.getFloat(3);
					bodyWidth += rowSet.getFloat(5);
					aRleft2 += rowSet.getFloat(5);
				}
				if (aRleft2 == rowSet.getFloat(3)) {
					bodyWidth += rowSet.getFloat(5);
					aRleft2 += rowSet.getFloat(5);
				}

				 //取得表体总高 
				if (aRleft == 0&&(this.getTextFormatMap()==null||this.getTextFormatMap().size()==0|| "0".equals(this.column)|| "0".equals(this.dataarea))) {
					aRleft = rowSet.getFloat(3);
					/**textFormatmap中为下层指标*/
					if(this.getTextFormatMap().size()>0&&this.getTextFormatMap().get(rowSet.getString(1))!=null&&xx<=tempVar&& "1".equals(this.column)&& "1".equals(this.dataarea)) {
                        bodyHeight += rowSet.getFloat(6);
                    } else if(/*this.getTextFormatMap().size()>0&&xx<=tempVar&&*/"1".equals(this.column)&& "0".equals(this.dataarea))// 横行分栏
                    {
                        bodyHeight += rowSet.getFloat(6);
                    } else if(this.getTextFormatMap().size()==0&&xx<=1) {
                        bodyHeight += rowSet.getFloat(6);
                    }
				} else if (aRleft != 0 && aRleft == rowSet.getFloat(3)) {
					/*if(this.getTextFormatMap().size()>0&&this.getTextFormatMap().get(rowSet.getString(1))!=null&&xx<=1)
				    	bodyHeight += rowSet.getFloat(6);
					if(this.getTextFormatMap().size()==0&&xx<1)
						bodyHeight += rowSet.getFloat(6);*/
					if(this.getTextFormatMap().size()>0&&this.getTextFormatMap().get(rowSet.getString(1))!=null&&xx<=tempVar&& "1".equals(this.column)&& "1".equals(this.dataarea)) {
                        bodyHeight += rowSet.getFloat(6);
                    } else if(/*this.getTextFormatMap().size()>0&&xx<=tempVar&&*/"1".equals(this.column)&& "0".equals(this.dataarea))// 横行分栏
                    {
                        bodyHeight += rowSet.getFloat(6);
                    } else if(this.getTextFormatMap().size()==0&&xx<=1) {
                        bodyHeight += rowSet.getFloat(6);
                    }
				}
				xx++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		list.add(bodyList);
		list.add(new Float(bodyHeight));
		list.add(new Float(bodyWidth));
		list.add(ltPix);
		return list;
	}

	/**
	 * (不分栏的处理方法) 取得表头最底端的列 && 求得表底是否有页小计和累计 && 哪些列有页小计或累计 &&得到表体的宽度（单位：像素）
	 * &表头的所有列 &&表高
	 * 
	 * @param tabid
	 *            高级花名册的id
	 * @return ArrayList
	 */
	public ArrayList getBottomnList(String tabid) throws GeneralException {
		////横向分栏 不支持列合并 行合并 页小计 页累计 分组合计 总计 暂改为只要为横向分栏则直接取消这些设置项 保证页面显示格式正确
		boolean type=true;
		if("1".equals(this.column)) {
			if("0".equals(dataarea)) {
				type=false;
			}
		}
		
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select GridNo,Hz,Rleft,RTop+");
		sql.append(this.t_space);
		sql.append(",RWidth,RHeight,Slope,Flag,noperation,Field_Type,fontName,");
		sql.append("fontEffect,fontSize,align,L,T,R,B,Field_name,nhide,");
		sql.append("ExtendAttr,SETNAME from muster_cell where tabid=");
		sql.append(tabid);
		sql.append(" order by Rleft,RTop ");
		//wangcq 2014-11-20 begin 查询临时表字段类型
		String tabName = this.userView.getUserName() + "_Muster_" +tabid;
		StringBuffer sql_muster = new StringBuffer("");
		sql_muster.append("select * from ");
		sql_muster.append(tabName);
		sql_muster.append(" where 1=2");
		//wangcq 2014-11-20 end
		ArrayList columnList = new ArrayList();
		bottomColumns = new ArrayList();
		ArrayList columnsList = new ArrayList(); // 装有页小计或页累计的列-横分、不分(String[]
													// 0:列名 1:汇总标示 )
		ArrayList colColumnsList = new ArrayList(); // 装有页小计或页累计的列-纵分(String[]
		// 0:列名 1:汇总标示 )
		float t_width = 0;
		float t_height = 0;
		float r_left = 0;
		float r_bottomn = 0;
		float tableBodyWidth = 0; // 表体的宽度
		float bodyHeight = 0; // 表头的高
		String yxj = "0"; // 页小计
		String ylj = "0"; // 页累计
		int nOperation = 0;
		try {
			DbWizard dbw = new DbWizard(this.conn);
			ResultSetMetaData data = null;
			if(dbw.isExistTable(tabName, false)){				
				rowSet = dao.search(sql_muster.toString());
				data=rowSet.getMetaData();
			}
			/* 将标头放入list中 */
			rowSet = dao.search(sql.toString());
			int aa = 0;
			float aRleft = 0;
			boolean print = true;
			boolean PhotoColMerge = isPhotoColMerge(tabid);//照片是否自动合并
			while (rowSet.next()) {
				String[] temp = new String[21];
				/* #### 所有花名册都往左靠，边距为20像素 ##### */
				if (aa == 0) {
					float tt = rowSet.getFloat(3);
					this.lt = tt - 1;	
				}
				aa++;
				String flag = rowSet.getString("Flag");//liuy 2015-2-28 用于判断是该列什么类型的数据
				String fieldName = rowSet.getString("Field_name");
				String setname = rowSet.getString("SETNAME");
				String extendAttr = rowSet.getString("ExtendAttr");
				extendAttr=extendAttr!=null?extendAttr:"";
				String ColMerge = "false"; //是否合并
				String ColMergeByMain = "false"; //是否按人员,单位,职位合并
				if(extendAttr.indexOf("<ColMerge>")!=-1){
					ColMerge = extendAttr.substring(extendAttr.indexOf("<ColMerge>")+"<ColMerge>".length(),extendAttr.indexOf("</ColMerge>"));
				}
				if(extendAttr.indexOf("<ColMergeByMain>")!=-1){
					ColMergeByMain = extendAttr.substring(extendAttr.indexOf("<ColMergeByMain>")+"<ColMergeByMain>".length(),extendAttr.indexOf("</ColMergeByMain>"));
				}
				//liuy 2015-2-27 7561：花名册中一个人多条记录时姓名可以设置合并单元格，但是照片不能合并，照片应该自动合并
				if(flag != null && "P".equals(flag)&&PhotoColMerge){
					ColMerge = "True";
					ColMergeByMain = "True";
				}//liuy 2015-2-27 end
				String RowMerge="false";
				if(extendAttr.indexOf("<RowMerge>")!=-1){
					RowMerge = extendAttr.substring(extendAttr.indexOf("<RowMerge>")+"<RowMerge>".length(),extendAttr.indexOf("</RowMerge>"));
				}
				if(!type) {
					ColMerge="false";
					ColMergeByMain="false";
					RowMerge="false";
				}
				//itemHeArr +=fieldName+":"+ColMerge+":"+ColMergeByMain+",";
				mergeGrid += rowSet.getString("GridNo")+":"+ColMerge+":"+ColMergeByMain+":"+RowMerge+",";
				if(fieldName==null) {
                    fieldName="";
                }
				FieldItem item =null;
				if(!"5".equals(this.modelFlag))			//如果不是工资变动模块
                {
                    item=DataDictionary.getFieldItem(fieldName);
                } else
				{
					if(fieldName!=null&&fieldName.length()>1) {
                        item=DataDictionary.getFieldItem(fieldName.substring(0,fieldName.length()-2));
                    }
				}
				
				for (int i = 0; i < 20; i++) {
					if (i == 2) {
                        temp[i] = String.valueOf(rowSet.getFloat(i + 1) - this.lt);
                    } else if (i == 7) {
						if (rowSet.getString(i + 1)!=null&&
						        ("A".equals(rowSet.getString(i + 1))|| "B".equals(rowSet.getString(i + 1))||
						                "K".equals(rowSet.getString(i + 1)))) {
							if ((!"salary".equalsIgnoreCase(this.modelFlag)&&!"5".equals(this.modelFlag))&&
							        !"15".equalsIgnoreCase(this.modelFlag) &&
							        (item == null || "0".equals(item.getUseflag()))) {
                                temp[i] = "H";
                            } else {
                                temp[i] = rowSet.getString(i + 1);
                            }
						} else {
                            temp[i] = rowSet.getString(i + 1);
                        }
					} else if (i == 9) {
						/*if (!this.modelFlag.equalsIgnoreCase("salary")&&item != null && !item.getUseflag().equals("0"))
							temp[i] = item.getItemtype();
						else*/
							temp[i] = rowSet.getString(i + 1);
					} else{
						if(fieldName.equalsIgnoreCase(setname+"z1")&&i==6&&!"81".equals(this.modelFlag)) {
                            temp[i] = "0";
                        } else {
                            temp[i] = rowSet.getString(i + 1);
                        }
					}
					//处理oracle null值错误
					if(temp[i]==null) {
                        temp[i]="";
                    }
				}
				
				if(!"M".equals(temp[9]) && isTextType("C"+temp[0], data)) //wangcq 2014-11-20 花名册定位查询多条数据时，相应数据类型改变，此时temp[9]的类型也需改变
                {
                    temp[9] = "M";
                }
				
				temp[20]=rowSet.getString(22);
				/*noperation=1页小计
				=2页累计
				=4分组合计
				=8总计
				=3页小计，页累计
				=5页小计，分组合计
				=9页小计，总计
				=7页小计，页累计，分组合计
				=11页小计，页累计，总计
				=13页小计，分组合计，总计
				=14页累计，分组合计，总计
				=15页小计，页累计，分组合计，总计
				=12分组合计，总计
				=10页累计，总计
				=6页累计，分组合计*/
				/*flag A：人员库
				B：单位库
				K:职位库
				P：照片
				H：文本
				C：计算结果
				S:序号
				R:组内记录数*/
				if (("N".equalsIgnoreCase(temp[9])|| "R".equalsIgnoreCase(temp[7]))&&type) {
					if (!"0".equals(temp[8])) {
						if ("1".equals(temp[8])) {
                            yxj = "1";
                        } else if ("2".equals(temp[8])) {
                            ylj = "1";
                        } else if ("3".equals(temp[8])) {
							yxj = "1";
							ylj = "1";
						}
						String[] a = new String[2];
						a[0] = "C" + temp[0];
						a[1] = temp[8];
						colColumnsList.add(a);
					}
				}
				if(!print){
					temp[14]="1";
					print = true;
				}
				if(temp[20]!=null&& "1".equals(temp[20])){
					if(columnList.size()>0){
						String[] arr = (String[])columnList.get(columnList.size()-1);
						if(arr!=null&&arr.length==21){
							arr[16]="1";
							columnList.remove(columnList.size()-1);
							columnList.add(arr);
						}
					}else {
                        print = false;
                    }
				}
				
				columnList.add(temp);
			}
			/*
			 * 将标头底部列号按显示顺序输出
			 */
		
			if(lt>0)
			{
				rowSet = dao.search("select max(rleft+rwidth)-min(rleft) t_width,max(rtop+rheight)-min(rtop) t_height,min(rleft)-"
								+ this.lt
								+ " r_left,max(rtop+rheight)+"+this.t_space+" r_bottomn   from muster_cell where tabid="
								+ tabid);
			}
			else {
                rowSet = dao.search("select max(rleft+rwidth)-min(rleft) t_width,max(rtop+rheight)-min(rtop) t_height,min(rleft)+"
                        + (-this.lt)
                        + " r_left,max(rtop+rheight)+"+this.t_space+" r_bottomn   from muster_cell where tabid="
                        + tabid);
            }
			if (rowSet.next()) {
				t_width = rowSet.getFloat("t_width");
				t_height = rowSet.getFloat("t_height");
				r_left = rowSet.getFloat("r_left");
				r_bottomn = rowSet.getFloat("r_bottomn");
			}
			bodyHeight = t_height;
			tableBodyWidth = t_width;

			float temp_width = 0;
			float temp_left = 1;
			if(this.getTextFormatMap()!=null&&this.getTextFormatMap().size()>0)
			{
				for (int i = 0; i < columnList.size(); i++) {
					
		    		String[] a_temp = (String[]) columnList.get(i);
                if(this.getTextFormatMap().get(a_temp[0])==null) {
                	continue;
                }else{
			    		bottomColumns.add(a_temp);
			    		if ("N".equals(a_temp[9])|| "R".equals(a_temp[7])) {
				    		if (!"0".equals(a_temp[8])) {
				    			String[] a = new String[2];
					    		a[0] = "C" + a_temp[0];
					    		a[1] = a_temp[8];
					    		columnsList.add(a);
			    			}
			    		}
		    		}
	    		}
			}
			else
			{
	    		while (temp_width < tableBodyWidth) {

	    			for (int i = 0; i < columnList.size(); i++) {
					
			    		String[] a_temp = (String[]) columnList.get(i);
                     //                                 rleft                      rtop                            rheight
			    		if (temp_left == Float.parseFloat(a_temp[2])&& (Float.parseFloat(a_temp[3]) + Float.parseFloat(a_temp[5])) == r_bottomn) {
				    		bottomColumns.add(a_temp);
				    		if ("N".equals(a_temp[9])|| "R".equals(a_temp[7])) {
					    		if (!"0".equals(a_temp[8])) {
					    			String[] a = new String[2];
						    		a[0] = "C" + a_temp[0];
						    		a[1] = a_temp[8];
						    		columnsList.add(a);
				    			}
				    		}
				    		temp_width += Float.parseFloat(a_temp[4]);
			    			temp_left += Float.parseFloat(a_temp[4]);
			    			break;
			    		}
		    		}
	    		}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if ("1".equals(yxj) && "1".equals(ylj)) {
            nOperation = 3;
        } else if ("1".equals(yxj) && "0".equals(ylj)) {
            nOperation = 1;
        } else if ("0".equals(yxj) && "1".equals(ylj)) {
            nOperation = 2;
        }

		list.add(bottomColumns);
		list.add(new Integer(nOperation));
		list.add(columnsList);
		list.add(new Float(tableBodyWidth));
		list.add(columnList);
		list.add(new Float(bodyHeight));
		list.add(colColumnsList);
		list.add(new Float(r_bottomn));
		return list;
	}
	public String extendAttrXML(String ext,String par){
		String value="";
		String starStr = "<"+par+">";
		String endStr = "</"+par+">";
		if(ext.indexOf(starStr)!=-1){
			value = ext.substring(ext.indexOf(starStr)
					+starStr.length(),
					ext.indexOf(endStr));
		}
		return value;
	}
	public String getQueryFromPartLike(UserView userview,String dbpre,String vorgcode)
	{
		StringBuffer buf=new StringBuffer();
		ArrayList fieldlist=new ArrayList();
		String strWhere=null;		
		try
		{
			
			
			 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			 /**兼职参数*/
			 String partflag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
			
			 String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
			 /**兼职单位字段*/
			 String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");	
			 /**兼职部门字段*/
			 String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
			 /**兼职排序字段*/
			 String order_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "order");
			 /**兼职岗位字段*/
			 String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos");
			 /**任免标识=0任=1免*/
			 String appoint=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "appoint");
			 /**分析此定义的数据集和指标是否构库*/
			 FieldSet fieldset=null;
			 if(setid!=null&&!"".equals(setid.trim())&&!"#".equals(setid)) {
                 fieldset=DataDictionary.getFieldSetVo(setid);
             }
			 FieldItem unititem=null;
			 if(unit_field!=null&&!"".equals(unit_field.trim())&&!"#".equals(unit_field)) {
                 unititem=DataDictionary.getFieldItem(unit_field);
             }
			 FieldItem deptitem=null;
			 if(dept_field!=null&&!"".equals(dept_field.trim())&&!"#".equals(dept_field)) {
                 deptitem=DataDictionary.getFieldItem(dept_field);
             }
			 FieldItem orderitem=null;
			 if(order_field!=null&&!"".equals(order_field.trim())&&!"#".equals(order_field)) {
                 orderitem=DataDictionary.getFieldItem(order_field);
             }
			 FieldItem positem =null;
			 if(pos_field!=null&&!"".equals(pos_field.trim())&&!"#".equals(pos_field)) {
                 positem=DataDictionary.getFieldItem(pos_field);
             }
			 FieldItem appointitem = null;
			 if(appoint!=null&&!"".equals(appoint.trim())&&!"#".equals(appoint)) {
                 appointitem=DataDictionary.getFieldItem(appoint);
             }
			 if(partflag==null|| "".equalsIgnoreCase(partflag)|| "false".equalsIgnoreCase(partflag)||fieldset==null|| "0".equals(fieldset.getUseflag())) {
                 return "";
             }
			 String app_set=dbpre.toUpperCase()+setid.toUpperCase();
			 String privsql=userView.getPrivSQLExpression(dbpre, true);
			 privsql=privsql.substring(12);
			 String mainset=dbpre.toUpperCase()+"A01".toUpperCase();
			 buf.append(" a0100 in (");
			 buf.append("select "+dbpre+"A01.a0100 from "+dbpre+"A01 ");
			 if(privsql.indexOf(app_set)!=-1)
			 {
				 
			 }else {
				 buf.append(" left join ");
				 buf.append(app_set+" on "+dbpre+"A01.A0100="+app_set+".A0100 ");
			 }
			 if(unititem!=null&& "1".equals(unititem.getUseflag())) {
                 privsql = privsql.replaceAll(mainset+".B0110", app_set+"."+unit_field);
             }
			 if(deptitem!=null&& "1".equals(deptitem.getUseflag())) {
                 privsql = privsql.replaceAll(mainset+".E0122", app_set+"."+dept_field);
             }
			 buf.append(privsql);
			 if(appointitem!=null&& "1".equals(appointitem.getUseflag())) {
                 buf.append(" and "+app_set+"."+appointitem.getItemid()+"='0'");
             }
			 buf.append(")");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return buf.toString();
	}
	private String sql;
	/**
	 * 从人事异动进入花名册中，显示记录要根据人事异动流程显示不同人员
	 * @param sql
	 * @return
	 */
	public String getA0100s(String sql)
	{
		String str="";
		try
		{
			/*StringBuffer buf = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				buf.append(" or a0100='"+rs.getString("a0100")+"'");
			}
			if(buf.toString().length()>0)
				str=buf.toString().substring(3);
			if(rs!=null)
				rs.close();*/
			int w_index=sql.indexOf("from");
			//int o_index=sql.lastIndexOf("order");
			if(w_index==-1) {
                return str;
            }
			str=sql.substring(w_index+5);
			/*if(o_index==-1)
			{
				str=sql.substring(w_index+5);
			}
			else
			{
				str=sql.substring(w_index+5, o_index-1);
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public String getCreateTablePerson(String userName,String xml)
	{
		String str="";
		try
		{
			String ss=userName;
			String prefix="";
			if(xml.indexOf("<prefix>")!=-1)
			{
				String fix=xml.substring(xml.indexOf("<prefix>")+8, xml.indexOf("</prefix>"));
				if(fix.length()>0) {
                    prefix=fix;
                }
			}
			str=prefix+ss;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public String getCreateTableDate(String xml)
	{
		String str="";
		try
		{
			GregorianCalendar d = new GregorianCalendar();//制表日期
			String dateStr= d.get(Calendar.YEAR)+ "."+ (d.get(Calendar.MONTH)+1)+ "."+ d.get(Calendar.DATE);
			String prefix="";
			
			if(xml.indexOf("<prefix>")!=-1)
			{
				String fix=xml.substring(xml.indexOf("<prefix>")+8, xml.indexOf("</prefix>"));
				if(fix.length()>0) {
                    prefix=fix;
                }
			}
			if(xml.indexOf("<format>")!=-1)
			{
				String format=xml.substring(xml.indexOf("<format>")+8, xml.indexOf("</format>"));
				/* 0: 1991.12.3
			      1: 1990.01.01
			      2: 1990年2月10日
			      3: 1990年01月01日
			      4: 1991-12-3
			      5: 1990-01-01*/
                int year=d.get(Calendar.YEAR);
                int month=d.get(Calendar.MONTH)+1;
                int day=d.get(Calendar.DATE);
				if(format.length()>0)
				{
					if("0".equals(format))
					{
						dateStr=year+"."+month+"."+day;
					}
					else if("1".equals(format))
					{
						dateStr=year+"."+((month>=10)?month+"":"0"+month)+"."+((day>=10)?day+"":"0"+day);
					}
					else if("2".equals(format))
					{
						dateStr=year+ResourceFactory.getProperty("hmuster.label.year")+month+ResourceFactory.getProperty("hmuster.label.month")+day+ResourceFactory.getProperty("hmuster.label.day");
					}
					else if("3".equals(format))
					{
						dateStr=year+ResourceFactory.getProperty("hmuster.label.year")+
						        ((month>=10)?month+"":"0"+month)+ResourceFactory.getProperty("hmuster.label.month")+
						        ((day>=10)?day+"":"0"+day)+ResourceFactory.getProperty("hmuster.label.day");
					}
					else if("4".equals(format))
					{
						dateStr=year+"-"+month+"-"+day;
					}
					else if("5".equals(format))
					{
						dateStr=year+"-"+((month>=10)?month+"":"0"+month)+"-"+((day>=10)?day+"":"0"+day);
					}
					else if("6".equals(format))
					{
						dateStr=year+ResourceFactory.getProperty("hmuster.label.year")+((month>=10)?month+"":"0"+month)+ResourceFactory.getProperty("hmuster.label.month");
					}
				}
			}
			str=prefix+dateStr;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	public double[] totalCounts2(double[] totalCount,String[] fields,boolean isGroupTerm,String isGroupPoint,int rows,int currentPage,String tablename,String sql)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		StringBuffer sql_ext=new StringBuffer("");
		try
		{
			if(fields.length>0/*&&!isGroupTerm&&(isGroupPoint== null|| !isGroupPoint.equals("1"))*/)
			{
				/*String asql=" select gridno from muster_cell where tabid="+tablename.substring(tablename.lastIndexOf("_")+1)+" and flag='R' and noperation is not null and noperation!=0";
				rowSet = dao.search(asql);
				HashMap amap=new HashMap();
				while(rowSet.next())
				{
					amap.put("C"+rowSet.getString("gridno"), "1");
				}
				StringBuffer ids=new StringBuffer("");
				for(int i=0;i<fields.length;i++)
				{
					if(amap.get(fields[i].toUpperCase())!=null)
						sql_ext.append(",count(*) "+fields[i]);
					else
			    		sql_ext.append(",sum("+fields[i]+") "+fields[i]);
					if(i!=0)
						ids.append(",");
					ids.append(fields[i].substring(1));
				}

				String sqls="select "+sql_ext.substring(1)+" from "+tablename;*/
				rowSet=dao.search(sql);
				while(rowSet.next())
				{
					for (int b = 0; b < fields.length; b++) {
						String a_context = rowSet.getString(fields[b]);
						if (a_context != null){
							BigDecimal b1 = new BigDecimal(Double.toString(totalCount[b]));
							BigDecimal b2 = new BigDecimal(Double.toString(Double.parseDouble(a_context)));
							totalCount[b] = b1.add(b2).doubleValue();
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return totalCount;
	}
	public double getDivWidth(String tabid)
	{
		double width=0;
		RowSet rs = null;
		try
		{
			StringBuffer buf = new StringBuffer("");
			buf.append("select max(rleft) rleft,rwidth from  muster_cell ");
			buf.append(" where tabid="+tabid+" group by rwidth order by rleft desc");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				double rleft=rs.getDouble("rleft");
				double rwidth=rs.getDouble("rwidth");
				width=rleft+rwidth;
			}
			buf.setLength(0);
			buf.append("select min(rleft) rleft from muster_cell where tabid="+tabid);
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				double nleft=rs.getDouble("rleft");
				width=width+nleft;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return width;
	}
	/**
	 * 判断总计，小计，累计等在哪个格显示
	 * resourceCloumn=-1默认，没有位置显示
	 * 先在序号位置显示
	 * 如果没有序号有分组指标，在分组指标显示
	 * 如果没有分组指标，如果为人员花名册时，在姓名位置显示，如果为机构花名册，在机构名称位置显示，如果是职位花名册，在职位名称位置显示
	 * 如果以上都没有，人员的找‘姓名’，机构的找‘部门名称’或者‘单位名称’，职位的，找岗位名称
	 * 以上都没有找第一个非数值型的
	 * @param tabid
	 * @return
	 */
	public int getResourceCloumn(String tabid,String info_flag)
	{
		RowSet rs = null;
		try
		{
			boolean flag=true;
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "select * from muster_cell where tabid="+tabid+" and UPPER(flag)='S'";
			rs=dao.search(sql);
			/**是否有序号指标*/
			while(rs.next())
			{
				this.resourceCloumn=rs.getInt("GridNo");
				flag=false;
				break;
			}
			/**是否有分组指标一*/
			if(flag)
			{
				sql="select * from muster_cell where tabid="+tabid+" and UPPER(flag)='G'";
				rs=dao.search(sql);
				while(rs.next())
				{
					this.resourceCloumn=rs.getInt("GridNo");
					flag=false;
					break;
				}
			}
			/**是否有分组指标二*/
			if(flag)
			{
				sql="select * from muster_cell where tabid="+tabid+" and UPPER(flag)='E'";
				rs=dao.search(sql);
				while(rs.next())
				{
					this.resourceCloumn=rs.getInt("GridNo");
					flag=false;
					break;
				}
			}
			if(flag)
			{
				if("1".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_name)='A0101'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
				else if("2".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and (UPPER(field_name)='B0110' or UPPER(field_name)='E0122')";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
				else if("3".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_name)='E01A1'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
			}
			if(flag)
			{
				if("1".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_hz)='姓名'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
				else if("2".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and (UPPER(field_hz)='部门名称' or UPPER(field_hz)='单位名称')";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
				else if("3".equals(info_flag))
				{
					sql="select * from muster_cell where tabid="+tabid+" and UPPER(field_name)='岗位名称'";
					rs=dao.search(sql);
					while(rs.next())
					{
						this.resourceCloumn=rs.getInt("GridNo");
						flag=false;
						break;
					}
				}
			}
			if(flag)
			{
				/*sql="select * from muster_cell where tabid="+tabid+" and gridno=1 and field_type is not null and UPPER(field_type)<>'N'";
				rs=dao.search(sql);
				while(rs.next())
				{
					this.resourceCloumn=7;
					flag=false;
					break;
				}*/
				this.resourceCloumn=-2;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return resourceCloumn;
	}
	
	public ArrayList getHmusterTableHead(String tabid, ArrayList tableHeaderList,float r_bottomn,String printGrid,float h_height)throws GeneralException 
	{
		
        ArrayList list = new ArrayList();
        StringBuffer tabHeader = new StringBuffer("");
        float[] lth = new float[3];
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        String sql2 = "select min(Rleft) a,min(RTop)+"+this.t_space+" b,max(RHeight) c from muster_cell where tabid="
		+ tabid;
        double divh=0.0;
        ArrayList columnList = new ArrayList();
        int row = 0; // 表头的行数
        try {
	    rowSet = dao.search(sql2);
	    float minLeft = 0;
    	float minTop = 0;
    	float maxHeight = 0;
    	if (rowSet.next()) {
	    	minLeft = rowSet.getFloat("a");
	    	lth[0] = minLeft - lt;
	    	minTop = rowSet.getFloat("b");
	    	lth[1] = minTop;
	    	maxHeight = rowSet.getFloat("c");
     		lth[2] = maxHeight;
    	}

    	float tRleft = 0;
    	float tRwidth = 0;
    	for (Iterator tt = tableHeaderList.iterator(); tt.hasNext();) {
    		String[] temp = (String[]) tt.next();
	    	String gridno=temp[0];
	    	if(this.getTextFormatMap()!=null&&this.getTextFormatMap().get(temp[0])!=null) {
                continue;
            }
	    	String l = temp[2];
	    	String t = temp[3];
	     	String w = temp[4];
	     	String h = temp[5];
	    	if(temp.length>20&&temp[20]!=null&& "1".equals(temp[20])) {
                continue;
            }
		
	     	int flag=0;
	    	if(Float.parseFloat(temp[3]) + Float.parseFloat(temp[5])==r_bottomn) {
                flag=1;
            }
		
	    	String context = temp[1];
	    	if (context == null) {
                context = "&nbsp;";
            }
	    	String fontSize = temp[12];
	    	String fontName = temp[10];
	    	String fontStyle = temp[11];
	    	int Align = Integer.parseInt(temp[13]);
	    	if (context != null && context.indexOf("`") != -1) {
                context = context.replaceAll("`", "<br>");
            }

	    	if("1".equals(temp[19])) {
                context="&nbsp;";
            }
	      	if (Float.parseFloat(l) != lth[0]&& "0".equals(dataarea)) {
	     		l = String.valueOf(Float.parseFloat(l) - 1);
	    		w = String.valueOf(Float.parseFloat(w) + 1);
	    	}
	      	if("1".equals(dataarea)) {
	      		w = String.valueOf(Float.parseFloat(w));//bug 37759
	      	}
	      	
	    	if (Float.parseFloat(t) != minTop) {
	    		t = String.valueOf(Float.parseFloat(t) - 1);
	    		h = String.valueOf(Float.parseFloat(h) + 1);
	    	}
            divh+=Double.parseDouble(w);
		// 处理虚线 L,T,R,B,
	    	String style_name = getStyleName(temp,flag,printGrid);
	    	boolean wordWarp=false;
	    	if(musterCellMap!=null&&musterCellMap.get(gridno)!=null) {
	    		if(musterCellMap.get(gridno).get("nWordWrap")!=null&&"1".equals(musterCellMap.get(gridno).get("nWordWrap"))) {
	    			wordWarp=true;
	    		}
	    	}
	    	tabHeader.append(executeAbsoluteTable(1, Align, fontName,
				fontSize, fontStyle, "1", (Float.parseFloat(t)+this.getTextDataHeight())+"", l, w, h, context,
				style_name,wordWarp));//h_height+""
    	}
        this.setDivWidth(divh+this.t_space);
    	list.add(tabHeader.toString());
    	list.add(lth);
    } catch (Exception e) {
    	e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
    } finally{
		try
		{
			if(rowSet!=null) {
                rowSet.close();
            }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
    return list;
  }
  public String parseSelectPoint(String tocope,String fromCope)
  {
	  String temp="";
	  try
	  {
		  StringBuffer ss=new StringBuffer("");
		  if(fromCope!=null&&fromCope.length()>0&&tocope!=null&&tocope.length()>0)
		  {
			  ss.append(fromCope.substring(0,4));
			  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
			  ss.append(fromCope.substring(5,7));
			  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
			  ss.append("~");
			  ss.append(tocope.substring(0,4));
			  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
			  ss.append(tocope.substring(5,7));
			  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
		  }else if(fromCope!=null&&fromCope.length()>0)
		  {
			  ss.append(fromCope.substring(0,4));
			  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
			  ss.append(fromCope.substring(5,7));
			  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
		  }else if(tocope!=null&&tocope.length()>0)
		  {
			  ss.append(tocope.substring(0,4));
			  ss.append(ResourceFactory.getProperty("kq.wizard.year"));
			  ss.append(tocope.substring(5,7));
			  ss.append(ResourceFactory.getProperty("kq.wizard.month"));
		  }
		  temp=ss.toString();
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return temp;
  }
  /**
   * wangcq 2014-11-20 查看字段是否已修改为大文本类型
   * @param columnName 字段名称
   * @param data       临时表字段属性
   * @return           为大文本返回true
   * @throws SQLException
   */
	public static boolean isTextType(String columnName, ResultSetMetaData data) throws SQLException{
		if(data != null) {
            for(int s=1; s<=data.getColumnCount(); s++){
                if(columnName.equals(data.getColumnName(s))){
                    switch(data.getColumnType(s))
                    {
                        case java.sql.Types.CLOB:
                        case java.sql.Types.LONGVARCHAR:
                        case java.sql.Types.LONGVARBINARY:
                            return true;
                    }
                }
            }
        }
		return false;
	}
	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public int getPageRows() {
		return pageRows;
	}

	public void setPageRows(int pageRows) {
		this.pageRows = pageRows;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getIsGroupNoPage() {
		return isGroupNoPage;
	}

	public void setIsGroupNoPage(String isGroupNoPage) {
		this.isGroupNoPage = isGroupNoPage;
	}
	
	public String getIsGroupedSerials() {
		return isGroupedSerials;
	}
	public void setIsGroupedSerials(String isGroupedSerials) {
		this.isGroupedSerials = isGroupedSerials;
	}
	public String getGroupPoint() {
		return groupPoint;
	}

	public void setGroupPoint(String groupPoint) {
		this.groupPoint = groupPoint;
	}

	public String getDataarea() {
		return dataarea;
	}

	public void setDataarea(String dataarea) {
		this.dataarea = dataarea;
	}

	public String getGroupNcode() {
		return groupNcode;
	}

	public void setGroupNcode(String groupNcode) {
		this.groupNcode = groupNcode;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public HashMap getTextFormatMap() {
		return textFormatMap;
	}

	public void setTextFormatMap(HashMap textFormatMap) {
		this.textFormatMap = textFormatMap;
	}

	public double getTextDataHeight() {
		return textDataHeight;
	}

	public void setTextDataHeight(double textDataHeight) {
		this.textDataHeight = textDataHeight;
	}

	public double getDivWidth() {
		return divWidth;
	}

	public void setDivWidth(double divWidth) {
		this.divWidth = divWidth;
	}

	public double getDivHeight() {
		return divHeight;
	}

	public void setDivHeight(double divHeight) {
		this.divHeight = divHeight;
	}
	public String getPrintGrind() {
		return printGrind;
	}
	public void setPrintGrind(String printGrind) {
		this.printGrind = printGrind;
	}
	public String getIsGroupPoint2() {
		return isGroupPoint2;
	}
	public void setIsGroupPoint2(String isGroupPoint2) {
		this.isGroupPoint2 = isGroupPoint2;
	}
	public String getGroupPoint2() {
		return groupPoint2;
	}
	public void setGroupPoint2(String groupPoint2) {
		this.groupPoint2 = groupPoint2;
	}
	public boolean isGroupV2() {
		return isGroupV2;
	}
	public void setGroupV2(boolean isGroupV2) {
		this.isGroupV2 = isGroupV2;
	}
	public String getGroupCount() {
		return groupCount;
	}
	public void setGroupCount(String groupCount) {
		this.groupCount = groupCount;
	}
	public String getNo_manger_priv() {
		return no_manger_priv;
	}
	public void setNo_manger_priv(String no_manger_priv) {
		this.no_manger_priv = no_manger_priv;
	}
	public String getReturnflag() {
		return returnflag;
	}
	public void setReturnflag(String returnflag) {
		this.returnflag = returnflag;
	}
	public String getYearMont() {
		return yearMont;
	}
	public void setYearMont(String yearMont) {
		this.yearMont = yearMont;
	}
	public HashMap getTopDateTitleMap() {
		return topDateTitleMap;
	}
	public void setTopDateTitleMap(HashMap topDateTitleMap) {
		this.topDateTitleMap = topDateTitleMap;
	}
	public String getChecksalary() {
		return checksalary;
	}
	public void setChecksalary(String checksalary) {
		this.checksalary = checksalary;
	}
	public String getIsGroupPoint() {
		return isGroupPoint;
	}
	public void setIsGroupPoint(String isGroupPoint) {
		this.isGroupPoint = isGroupPoint;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getInfor_Flag() {
		return infor_Flag;
	}
	public void setInfor_Flag(String infor_Flag) {
		this.infor_Flag = infor_Flag;
	}
	public String getModelFlag() {
		return modelFlag;
	}
	public void setModelFlag(String modelFlag) {
		this.modelFlag = modelFlag;
	}
	public UserView getUserView() {
		return userView;
	}
	public void setUserView(UserView userView) {
		this.userView = userView;
	}
    public String[] getZj() {
        return zj;
    }
    public String getSalaryDataTable() {
        return salaryDataTable;
    }
    public void setSalaryDataTable(String salaryDataTable) {
        this.salaryDataTable = salaryDataTable;
    }
    public StringBuffer getSalaryDataTableCond() {
        return salaryDataTableCond;
    }
    public void setSalaryDataTableCond(StringBuffer salaryDataTableCond) {
        this.salaryDataTableCond = salaryDataTableCond;
    }
    public int getDeltaTop() {
        return deltaTop;
    }
    public void setDeltaTop(int deltaTop) {
        this.deltaTop = deltaTop;
    }
    
    /**
     * 哈药-领导桌面-获取高级花名册总页数
     * @return 总页数
     * @author xiaoyun 2014-8-14
     */
	public int getTotalPage() {
		return totalPage;
	}
	public String getLinktype() {
		return linktype;
	}
	public void setLinktype(String linktype) {
		this.linktype = linktype;
	}
	public boolean isTableTypeFlag() {
		return tableTypeFlag;
	}
	public void setTableTypeFlag(boolean tableTypeFlag) {
		this.tableTypeFlag = tableTypeFlag;
	}	

}