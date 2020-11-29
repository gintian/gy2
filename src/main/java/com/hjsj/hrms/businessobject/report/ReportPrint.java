package com.hjsj.hrms.businessobject.report;

import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

class MyPoint {

	public float x = 0f;

	public float y = 0f;

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
}

/**
 * @author lzy
 * 
 */
public class ReportPrint {

	// 因为报表设计平台中存入数据库的页宽与页高单位为毫米需要转换为象素点scale转换系数
	public static float scale = 0.27f;

	// 如果为真时为处理综合表，否则为单表处理
	private boolean integrateFlag = false; // 综合表标识
	private boolean zflg = false; // 是否
	private int[] itemGridArea = null; // 综合表的数据区域
	private int pageValue = 1;
	private boolean mflag = false; // 是否新页中写表尾
	private float page_pyl = 0.0f;
	private int integrate_left_pyl;
	private int integrate_top_pyl;

	private PdfWriter writer;

	// 用户名
	private String username = "";

	// 报表ID
	private String tabid = "";

	private Document document = null;

	// 表格列表
	private ArrayList gridList = new ArrayList();

	// 标题列表
	private ArrayList pageList = new ArrayList();

	// 参数列表
	private HashMap paramMap = new HashMap();

	// 横表栏底层单元格列表（按顺序排列）
	private ArrayList rowInfoBGrid = new ArrayList();

	// 纵表栏底层单元格列表（按顺序排列）
	private ArrayList colInfoBGrid = new ArrayList();

	// 填报单位编码
	private String unitcode = "";

	// 表对象
	private RecordVo tnameVo = null;

	String fontName = "";

	String fontSize = "";

	String fontEffect = "";

	private float page_width = 0;

	private float page_height = 0;

	// 不含页边距宽
	private float real_width = 0;

	// 不含页边距高
	private float real_height = 0;

	private String operateObject = "";

	private Connection con = null;

	private boolean paginationHeader = false;

	private int maxTop = 0;

	private int pageCount = 0;

	private float t_margin = 0;

	private float b_margin = 0;

	private float l_margin = 0;

	private float r_margin = 0;

	private float integrate_t_margin = 0;

	private float integrate_b_margin = 0;

	private float integrate_l_margin = 0;

	private float integrate_r_margin = 0;

	private String paperori = "";

	private ArrayList resultList = null;

	// 全局参数表示当前单元格线是否为虚线。l、r、t、b分别为上下左右四条线
	private boolean lLine = false;

	private boolean rLine = false;

	private boolean tLine = false;

	private boolean bLine = false;

	private Font font = null; // 当前单元格字体

	// 综合表标题
	private String integerateTitle = "";

	private String integerateFontName = "";

	private String integerateFontSize = "";

	private String integerateFontEffect = "";

	private String integerateFont = "";

	private String integerateFontColor = "";

	private String integerateFontItalic = "";

	private String integerateFontBold = "";

	private String integerateFontStrike = "";

	private String integerateFontUnderLine = "";

	private String integeratePaperOri = "";

	private String integerateHead_flw = "";

	private String integerateHead_frw = "";

	private String integerateHead_fmw = "";

	private String integerateHeadFontName = "";

	private String integerateHeadFontSize = "";

	private String integerateHeadFontEffect = "";

	private String integerateHeadFont = "";

	private String integerateHeadFontColor = "";

	private String integerateHeadFontItalic = "";

	private String integerateHeadFontBold = "";

	private String integerateHeadFontStrike = "";

	private String integerateHeadFontUnderLine = "";

	private String integerateBodyFontName = "";

	private String integerateBodyFontSize = "";

	private String integerateBodyFontEffect = "";

	private String integerateBodyFont = "";

	private String integerateBodyFontColor = "";

	private String integerateBodyFontUnderLine = "";

	private String integerateBodyFontItalic = "";

	private String integerateBodyFontBold = "";

	private String integerateUnderHead_flw = "";

	private String integerateUnderHead_frw = "";

	private String integerateUnderHead_fmw = "";

	private String integerateUnderHeadFontName = "";

	private String integerateUnderHeadFontSize = "";

	private String integerateUnderHeadFontEffect = "";

	private String integerateUnderHeadFont = "";

	private String integerateUnderHeadFontColor = "";

	private String integerateUnderHeadFontItalic = "";

	private String integerateUnderHeadFontBold = "";

	private String integerateUnderHeadFontStrike = "";

	private String integerateUnderHeadFontUnderLine = "";

	private int minTop;

	private int minLeft;

	private float titleHeight = 0f;

	private float rowHeight = 0f;

	// 列数
	int colNum = 0;

	// list里存放要画虚线的坐标
	private ArrayList lineList = null;

	private int baseFontSize = 0;

	private ContentDAO dao;
	private UserView userview;

	public ReportPrint(TnameBo bo, Connection con, String operateObject) throws SQLException {
		super();
		this.con = con;

		this.dao = new ContentDAO(con);

		lineList = new ArrayList();

		tabid = bo.getTabid(); // 报表ID
		gridList = bo.getGridList(); // 表格列表
		pageList = bo.getPageList(); // 报表头信息列表
		rowInfoBGrid = bo.getRowInfoBGrid(); // 横表栏按顺序排列的相关信息集和
		colInfoBGrid = bo.getColInfoBGrid(); // 纵表栏按顺序排列的相关信息集和

		tnameVo = bo.getTnameVo(); // 表对象
		paramMap = bo.getParamMap(); // 参数列表
		unitcode = bo.getUnitcode(); // 填报单位
		username = bo.getUserName(); // 用户名
		tnameVo = bo.getTnameVo(); // 表对象
		userview = bo.getUserview();

		this.operateObject = operateObject;
		init();
	}

	/**
	 * 页面信息初始化(页边距 / 页大小 / 页方向 /页数)
	 */
	public void init() {
		RowSet rs = null;
		try {
			if (!this.zflg) {// 默认
				t_margin = tnameVo.getInt("tmargin") / scale;// 顶边距
				b_margin = tnameVo.getInt("bmargin") / scale;// 底边距
				l_margin = tnameVo.getInt("lmargin") / scale;// 左边距
				r_margin = tnameVo.getInt("rmargin") / scale;// 右边距
				paperori = tnameVo.getString("paperori"); // 纸张方向
				page_width = tnameVo.getInt("paperw");// 纸张宽度
				page_height = tnameVo.getInt("paperh");// 纸张高度
			} else {// 自定义综合表定义
				// 页边距
				t_margin = this.integrate_t_margin;
				b_margin = this.integrate_b_margin;
				l_margin = this.integrate_l_margin;
				r_margin = this.integrate_r_margin;
				if ("0".equals(integeratePaperOri.trim())) {
					integeratePaperOri = "1";
				} else if ("1".equals(integeratePaperOri.trim())) {
					integeratePaperOri = "0";
				}
				paperori = integeratePaperOri;

				page_width = this.getPage_width();// 纸张宽度
				page_height = this.getPage_height();// 纸张高度
			}
			// page_width = tnameVo.getInt("paperw");//纸张宽度
			// page_height = tnameVo.getInt("paperh");//纸张高度

			// 毫米转为像素(PDF为像素显示)
			page_height = page_height / scale;
			page_width = page_width / scale;

			// 纸张方向
			if ("1".equals(paperori)) {// 横向
				real_width = page_width - l_margin - r_margin;
				real_height = page_height - b_margin;// -t_margin;
			} else {// 纵向
				real_width = page_height - l_margin - r_margin;
				real_height = page_width - b_margin;// -t_margin;
			}

			// 计算报表页数
			pageCount = (int) (maxTop / real_height);
			if (maxTop % real_height > 0) {
				pageCount++;
			}

			// 找到最大纵坐标用来计算分页
			rs = dao.search("select max(rtop),min(rtop),min(rleft) from tgrid2 where tabid = " + tabid);
			if (rs != null && rs.next()) {
				maxTop = rs.getInt(1);
				minTop = rs.getInt(2);
				minLeft = rs.getInt(3);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

	}

	/**
	 * 获取最末页的数据表格总高度
	 * 
	 * @return
	 */
	public int getBottomNum() {
		int beginAbsolutY = 0;
		int y = 0;
		int top = 0;
		int height = 0;
		// 当前行索引
		int rowCount = -1;
		// 当前页数用来生成页码
		int pageNo = 1;
		// 用来存储在当前单元格前所有单元格height的和
		int beforeAllHeight = 0;
		// 上一单元格的height
		int beforeHeight = 0;
		// 上一单元格的top
		int beforeTop = 0;
		for (Iterator t = colInfoBGrid.iterator(); t.hasNext();) {
			RecordVo vo = (RecordVo) t.next();
			if (vo.getInt("flag") == 2) {
				top = vo.getInt("rtop");
				height = vo.getInt("rheight");
				setLine(vo);
				if (rowCount == -1) {
					beginAbsolutY = top;
					y = top;
				}
				rowCount++;
				if ((y + beforeAllHeight) > (real_height)) {
					try {
						pageNo++;
						beginAbsolutY = top;
						beforeAllHeight = 0;

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (beforeTop != top) {
                    beforeAllHeight += height;
                }
				beforeHeight = height;
				beforeTop = top;
			}
		}
		this.pageValue = pageNo;
		return beforeAllHeight;
	}

	/**
	 * 输出综合表
	 * 
	 * @param output_stream
	 */
	public void createZHBPDF(OutputStream output_stream) {

		colNum = 0;
		if (document == null) {
			document = new Document();
		}
		// 判断是横向页还是纵向页
		if ("1".equals(paperori)) {
			document.setPageSize(new Rectangle(page_width, page_height));
		} else {
			document.setPageSize(new Rectangle(page_height, page_width));
		}

		// 设置页边距
		document.setMargins(t_margin, b_margin, l_margin, r_margin);

		try {
			writer = PdfWriter.getInstance(document, output_stream);
			document.open();

			// 页面设置(重合处理)
			createHeaders(document, writer, 1);
			createIntegrateHeaders(writer, 1);

			createTableHeaders(writer, document);

			int beginAbsolutY = 0;
			int y = 0;
			int top = 0;
			int height = 0;
			// 当前行索引
			int rowCount = -1;
			// 当前页数用来生成页码
			int pageNo = 1;

			// 用来存储在当前单元格前所有单元格height的和
			int beforeAllHeight = 0;

			// 上一单元格的height
			int beforeHeight = 0;
			// 上一单元格的top
			int beforeTop = 0;
			for (Iterator t = colInfoBGrid.iterator(); t.hasNext();) {
				RecordVo vo = (RecordVo) t.next();
				output_stream.flush();
				if (vo.getInt("flag") == 2) {
					top = vo.getInt("rtop");
					height = vo.getInt("rheight");

					setLine(vo);
					if (rowCount == -1) {
						beginAbsolutY = top;
						y = top;
					}

					rowCount++;
					if ((y + beforeAllHeight) > (real_height)) {
						try {
							pageNo++;
							createCol(writer, y, beginAbsolutY, beforeTop, beforeHeight);
							printBrokenLine(writer);
							lineList = new ArrayList();
							document.newPage();
							beginAbsolutY = top;

							// 重合操作
							createIntegrateHeaders(writer, pageNo);
							createHeaders(document, writer, pageNo);

							createTableHeaders(writer, document);
							beforeAllHeight = 0;
							createData(writer, rowCount, rowCount, y);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						createData(writer, rowCount, rowCount, y + beforeAllHeight);
					}
					if (beforeTop != top) {
                        beforeAllHeight += height;
                    }

					beforeHeight = height;
					beforeTop = top;
				}
			}
			createCol(writer, y, beginAbsolutY, top, height);
			printBrokenLine(writer);

			if (this.mflag) {
				createNewPage(document, writer);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// if (document != null)
			// document.close();
		}

	}

	/**
	 * 创建PDF到输出流
	 * 
	 * @param output_stream
	 */
	public void createPDF(OutputStream output_stream) {
		colNum = 0;
		// 用用标识是否是追加报表。用于多报表打印
		boolean append = false;

		if (document == null) {
			document = new Document();
		} else {
			append = true;
		}
		// 判断是横向页还是纵向页
		if ("1".equals(paperori)) {
			// document = new Document(new Rectangle(page_width, page_height),
			// t_margin, b_margin, l_margin, r_margin);
			document.setPageSize(new Rectangle(page_width, page_height));
		} else {
			// document = new Document(new Rectangle(page_height, page_width),
			// t_margin, b_margin, l_margin, r_margin);
			document.setPageSize(new Rectangle(page_height + 20, page_width));
		}

		// 设置页边距
		document.setMargins(t_margin, b_margin, l_margin, r_margin);

		try {
			if (append) {
				document.newPage();
			} else {
				writer = PdfWriter.getInstance(document, output_stream);
			}
			document.open();

			/*
			 * if (this.flg) { createIntegrateHeaders(writer, 1); } else {
			 */
			createHeaders(document, writer, 1);
			// }

			createTableHeaders(writer, document);

			int beginAbsolutY = 0;
			int y = 0;
			int top = 0;
			int height = 0;
			// 当前行索引
			int rowCount = -1;
			// 当前页数用来生成页码
			int pageNo = 1;
			// 用来存储在当前单元格前所有单元格height的和
			int beforeAllHeight = 0;
			// 上一单元格的height
			int beforeHeight = 0;
			// 上一单元格的top
			int beforeTop = 0;
			for (Iterator t = colInfoBGrid.iterator(); t.hasNext();) {
				RecordVo vo = (RecordVo) t.next();
				output_stream.flush();
				if (vo.getInt("flag") == 2) {
					top = vo.getInt("rtop");
					height = vo.getInt("rheight");
					setLine(vo);
					if (rowCount == -1) {
						beginAbsolutY = top;
						y = top;
					}
					rowCount++;
					if ((y + beforeAllHeight) > real_height) {
						try {
							pageNo++;
							createCol(writer, y, beginAbsolutY, beforeTop, beforeHeight);
							printBrokenLine(writer);
							lineList = new ArrayList();
							document.newPage();
							beginAbsolutY = top;
							createHeaders(document, writer, pageNo);
							createTableHeaders(writer, document);
							beforeAllHeight = 0;
							createData(writer, rowCount, rowCount, y);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						createData(writer, rowCount, rowCount, y + beforeAllHeight);
					}
					if (beforeTop != top) {
                        beforeAllHeight += height;
                    }
					beforeHeight = height;
					beforeTop = top;
				}
			}
			createCol(writer, y, beginAbsolutY, top, height);
			printBrokenLine(writer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// if (document != null)
			// document.close();
		}
	}

	public int getMaxValue() {
		int temp = 0;

		ArrayList list = new ArrayList();
		// 遍历表头信息
		for (Iterator t = pageList.iterator(); t.hasNext();) {
			RecordVo vo = (RecordVo) t.next();
			int top = vo.getInt("rtop"); // 报表标题顶边距
			if (integrateFlag == true) {
				if (top > itemGridArea[1]) { // 是表尾部分
					list.add(String.valueOf(top));
				}
			}
		}

		if (list == null || list.size() == 0) {
			return temp;
		} else {
			int[] tt = new int[list.size()];
			for (int i = 0; i < list.size(); i++) {
				int n = Integer.parseInt((String) (list.get(i)));
				tt[i] = n;
			}
			Arrays.sort(tt);
			return tt[tt.length - 1];
		}
	}

	public int getMinValue() {
		int temp = 0;

		ArrayList list = new ArrayList();
		// 遍历表头信息
		for (Iterator t = pageList.iterator(); t.hasNext();) {
			RecordVo vo = (RecordVo) t.next();
			int top = vo.getInt("rtop"); // 报表标题顶边距
			if (integrateFlag == true) {
				if (top > itemGridArea[1]) { // 是表尾部分
					list.add(String.valueOf(top));
				}
			}
		}

		if (list == null || list.size() == 0) {
			return temp;
		} else {
			int[] tt = new int[list.size()];
			for (int i = 0; i < list.size(); i++) {
				int n = Integer.parseInt((String) (list.get(i)));
				tt[i] = n;
			}
			Arrays.sort(tt);
			return tt[0];
		}

	}

	public String getFullName(String username) {
		
		return StringUtils.isEmpty(userview.getUserFullName())?username:userview.getUserFullName();
	}

	public void createNewPage(Document doc, PdfWriter writer) {
		this.getBottomNum();
		Date date = new Date(); // 制表时间
		String content = "";
		try {
			doc.newPage();
			// 遍历表头信息
			for (Iterator t = pageList.iterator(); t.hasNext();) {
				RecordVo vo = (RecordVo) t.next();
				int left = vo.getInt("rleft"); // 报表标题左边距
				int top = vo.getInt("rtop"); // 报表标题顶边距
				top = top - 5;
				fontName = vo.getString("fontname");// 字体名称
				fontSize = vo.getString("fontsize");// 字体大小
				fontEffect = vo.getString("fonteffect");// 字体效果
				font = FontFamilyType.getFont(fontName, fontEffect, Integer.parseInt(fontSize));

				this.baseFontSize = (int) font.size();

				switch (vo.getInt("flag")) {
				case 0:// 内容
					content = vo.getString("hz");
					content = content.replaceAll("&nbsp;", " ");
					break;
				case 1:// 制表日期
					GregorianCalendar d = new GregorianCalendar();
					content = ResourceFactory.getProperty("hmuster.label.createTableDate") + ":" + d.get(Calendar.YEAR)
							+ "." + (d.get(Calendar.MONTH) + 1) + "." + d.get(Calendar.DATE);
					break;
				case 2: // 制表时间
					content = ResourceFactory.getProperty("hmuster.label.createTableTime")
							+ DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.CHINA).format(date);
					break;
				case 3: // 制表人
					content = ResourceFactory.getProperty("hmuster.label.createTableMen") + "：" + getFullName(username);
					break;
				case 4: // 总页数
					content = "共" + pageValue + 1 + "页";
					break;
				case 5: // 页码
					content = "第" + String.valueOf(pageValue + 1) + "页";
					break;
				case 9: // 参数定义
					String hz = vo.getString("hz");
					HashMap param_map = (HashMap) paramMap.get(hz);
					if (param_map != null) {
						String type = (String) param_map.get("paramtype");
						content = (String) param_map.get("a_value");
						if ("代码".equals(type)) {
							String[] myArray = content.split("/");
							if (myArray.length == 2) {
								content = myArray[1];
							}
						}
					}
					break;
				}

				if (integrateFlag == true) {

					if (top > itemGridArea[1]) { // 是表尾部分
						int fg = (int) (itemGridArea[1]); // 新基准
						int tt = this.getMinValue(); // 表尾中最上层的TOP坐标
						int pyl = fg - tt; // 偏移量

						top = (int) (top + pyl);
						writeParagraph(writer, content, left, top, 0, 100, 0, 0, Integer.parseInt(fontSize), 0);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 画标题(0代表)、制表日期(1代表)、制表时间(2代表)、页码(5代表)、页数(4代表)、图片(10代表)
	 * 
	 * @param doc
	 *            pdf文档对象
	 * @param writer
	 *            pdf输出对象
	 */
	public void createHeaders(Document doc, PdfWriter writer, int pageNO) {
		Date date = new Date(); // 制表时间
		String content = "";
		try {
			// 遍历表头信息
			for (Iterator t = pageList.iterator(); t.hasNext();) {
				RecordVo vo = (RecordVo) t.next();
				int left = vo.getInt("rleft"); // 报表标题左边距
				int top = vo.getInt("rtop"); // 报表标题顶边距
				top = top - 5;
				fontName = vo.getString("fontname");// 字体名称
				fontSize = vo.getString("fontsize");// 字体大小
				fontEffect = vo.getString("fonteffect");// 字体效果
				font = FontFamilyType.getFont(fontName, fontEffect, Integer.parseInt(fontSize));
				// ----------------------------解决 报表日期 文字及格式的自定义显示 zhaoxg 2013-6-5----------
				String extendattr1 = vo.getString("extendattr");
				String temp = "";
				String formattemp = "";
				int format = 0;
				if (extendattr1.indexOf("<prefix>") != -1) {
					int fromIndex = extendattr1.indexOf("<prefix>");
					int toIndex = extendattr1.indexOf("</prefix>");
					temp = extendattr1.substring(fromIndex + 8, toIndex).trim();
				}
				if (extendattr1.indexOf("<format>") != -1) {
					int fromIndex = extendattr1.indexOf("<format>");
					int toIndex = extendattr1.indexOf("</format>");
					formattemp = extendattr1.substring(fromIndex + 8, toIndex).trim();
					if (formattemp.length() > 0) {
                        format = Integer.parseInt(formattemp);
                    }
				}
				// -------------------
				if (font != null) {
                    this.baseFontSize = (int) font.size();
                } else {
                    this.baseFontSize = Integer.parseInt(fontSize);
                }
				switch (vo.getInt("flag")) {
				case 0:// 内容
					content = vo.getString("hz");
					content = content.replaceAll("&nbsp;", " ");
					break;
				case 1:// 制表日期
					GregorianCalendar d = new GregorianCalendar();
					StringBuffer aa = new StringBuffer();
					if ("".equals(temp))
						// content.append(ResourceFactory.getProperty("hmuster.label.createTableDate")+":"+formatDateFiledsetValue(d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE),format));
						// dml 2012年1月17日17:14:06 lisuju提 制表日期去掉前缀符不应该再出现前缀福
                    {
                        aa.append(formatDateFiledsetValue(
                                d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1) + "-" + d.get(Calendar.DATE),
                                format));
                    } else {
						if (!(temp.lastIndexOf(":") != -1 || temp.lastIndexOf("：") != -1)) {
                            temp += ":";
                        }
						aa.append(temp + formatDateFiledsetValue(
								d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1) + "-" + d.get(Calendar.DATE),
								format));
					}
					content = aa.toString();
					break;
				case 2: // 制表时间
					content = ResourceFactory.getProperty("hmuster.label.createTableTime")
							+ DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.CHINA).format(date);
					break;
				case 3: // 制表人
					content = ResourceFactory.getProperty("hmuster.label.createTableMen") + "：" + getFullName(username);
					break;
				case 4: // 总页数
					content = "共" + pageCount + "页";
					break;
				case 5: // 页码
					content = "第" + String.valueOf(pageNO) + "页";
					break;
				case 9: // 参数定义
					String hz = vo.getString("hz");
					HashMap param_map = (HashMap) paramMap.get(hz);
					if (param_map != null) {
						String type = (String) param_map.get("paramtype");
						content = (String) param_map.get("a_value");
						if ("代码".equals(type)) {
							String[] myArray = content.split("/");
							if (myArray.length == 2) {
								content = myArray[1];
							}
						}
					}
					break;
				case 10: // 参数定义

					String extendattr = vo.getString("extendattr");
					String ext = getExtendAttrContext(1, extendattr);
					if (ext == null || "".equalsIgnoreCase(ext)) {
						break;
					}
					boolean bwidth = true;
					boolean bheight = true;
					String stretch = getExtendAttrContext(2, extendattr);
					if (stretch == null || "".equalsIgnoreCase(stretch)) {
						break;
					}
					if (!"True".equalsIgnoreCase(stretch)) {
						bwidth = false;
						bheight = false;
					}
					String proportional = getExtendAttrContext(4, extendattr);
					if (proportional == null || "".equalsIgnoreCase(proportional)) {
						break;
					}

					if ("True".equalsIgnoreCase(stretch) && "True".equalsIgnoreCase(proportional)) {
						bwidth = false;
						bheight = true;
					}
					String tempName = createPhotoFile(vo, ext);

					if (tempName != null && tempName.length() > 0) {
						Image image = Image.getInstance(
								System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + tempName);
						// image.scaleAbsolute(vo.getInt("rwidth"),vo.getInt("rheight"));
						if (bwidth) {
                            image.scaleAbsoluteWidth(vo.getInt("rwidth"));
                        }
						if (bheight && !bwidth) {
                            image.scaleAbsoluteWidth(vo.getInt("rwidth") / 2);
                        }
						if (bheight) {
                            image.scaleAbsoluteHeight(vo.getInt("rheight"));
                        }
						PdfPCell pdfCell = new PdfPCell(image, false);
						if (!bheight && !bwidth) {
							pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
							pdfCell.setVerticalAlignment(Element.ALIGN_TOP);
						} else {
							pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							pdfCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
						}
						pdfCell.setBorder(0);
						PdfPTable table = new PdfPTable(1);
						table.setTotalWidth(vo.getInt("rwidth"));
						table.setLockedWidth(true);
						table.addCell(pdfCell);
						table.writeSelectedRows(0, -1, vo.getInt("rleft"), page_width - top, writer.getDirectContent());

					}

					break;
				}
				if (vo.getInt("flag") == 10) {
                    continue;
                }

				if (integrateFlag == true) { // 综合表的左 与顶 坐标
					left = left + this.getIntegrate_left_pyl();
					top = top + this.getIntegrate_top_pyl();
				}

				if (integrateFlag == true) {
					this.getBottomNum();
					if (top > itemGridArea[1]) { // 是表尾部分
						if (pageNO == pageValue) {

							int n = this.getBottomNum(); // 数据区域高度
							int fg = (int) (n + itemGridArea[1] + itemGridArea[3]); // 新基准
							int tt = this.getMinValue(); // 表尾中最上层的TOP坐标
							int pyl = fg - tt; // 偏移量

							// 判断剩余部分是否可以容下表尾 否则换页写表尾
							int maxv = this.getMaxValue();

							int ttop = maxv + pyl + 100;

							int h = 0;
							if ("1".equals(paperori)) {
								h = (int) page_height;
							} else {
								h = (int) page_width;
							}
							if (h < ttop) {
								this.mflag = true;
							} else {
								top = (int) (top + pyl);
								writeParagraph(writer, content, left, top, 0, 100, 0, 0, Integer.parseInt(fontSize), 0);
							}
						}
					} else {
						writeParagraph(writer, content, left, top, 0, 100, 0, 0, Integer.parseInt(fontSize), 0);
					}
				} else {
					writeParagraph(writer, content, left, top, 0, 100, 0, 0, Integer.parseInt(fontSize), 0);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向pdf画cell的方法
	 * 
	 * @param writer
	 *            pdf输出
	 * @param content
	 *            要输出的内容
	 * @param left
	 *            x轴坐标即横坐标
	 * @param top
	 *            y轴坐标即纵坐标
	 */
	public void writeParagraph(PdfWriter writer, String content, int left, int top, int border, int width, int height,
			int align, int fontsize, int a_flag) {
		if (content == null || "undefined".equals(content.trim())) {
			content = "";
		}
		content = content.replace("\r\n", "`");
		String s = content;
		content = "";
		// 去除content字符串中的空格
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == 13 || s.charAt(i) == 10) {
				continue;
			} else {
				content += s.charAt(i);
			}
		}
		// content = content.trim();
		if ("0".equals(content)) {
			content = "";
		}

		content = content.replaceAll("`", "\n");
		if ("乙".equals(content)) {// 乙不做处理
			return;
		}
		if ("—".equals(content)) {
			return;
		}
		if (border == 0 && width == 100 && height == 0 && align == 0) {
			// 表头不设置字体
		} else {
			int new_size = 0;
			if (a_flag == 0) // 标题
            {
                new_size = fontsize;
            } else // 表头
            {
                new_size = getFitFontSize(fontsize, width, height, content);
            }
			font.setSize(new_size);
		}
		s = "";
		// below delete by wangchaoqun on 2014-10-13
		// 处理标题过长时进行换行处理时效果并不好，而且系统本身支持用户在标题过长时自行将标题进行换行处理
		// if(content.length()>20&&(real_width-left/scale)/font.size()>1){
		// if(content.indexOf("\n")!=-1){
		// while(content.indexOf("\n")!=-1){
		// if(content.substring(0,
		// content.indexOf("\n")).length()>(real_width-left/scale)/font.size()-2){
		// s += content.substring(0,(int)((real_width-left/scale)/font.size()))+"\n";
		// content = content.substring((int)((real_width-left/scale)/font.size()));
		// }else{
		// s += content.substring(0,content.indexOf("\n"))+"\n";
		// content = content.substring(content.indexOf("\n")+1);
		// }
		// }
		// while(content.length()>(real_width-left/scale)/font.size()){
		// s += content.substring(0,(int)((real_width-left/scale)/font.size()))+"\n";
		// content = content.substring((int)((real_width-left/scale)/font.size()));
		// }
		// if(s.length()>0){
		// s += content;
		// content = s;
		// }
		// }else{
		// while(content.length()>(real_width-left/scale)/font.size()){
		// s += content.substring(0,(int)((real_width-left/scale)/font.size()))+"\n";
		// content = content.substring((int)((real_width-left/scale)/font.size()));
		// }
		// if(s.length()>0){
		// s += content;
		// content = s;
		// }
		// }
		// }
		// above delete by wangchaoqun on 2014-10-13

		Paragraph pragraph = new Paragraph(content, font);
		// Paragraph pragraph = new Paragraph(content);
		PdfPTable table = new PdfPTable(1);
		pragraph.setAlignment(1);
		PdfPCell cell = new PdfPCell(pragraph);
		if (border == 0) {
			cell.setNoWrap(true);
			cell.setBorderWidth(0);
		}
		if (border == 1) {
			table.setLockedWidth(true);
		}

		// 设置列最大高度
		cell.setFixedHeight(height);
		cell.setMinimumHeight(height);
		// ALIGN_JUSTIFIED基于最合适的
		if (align == 0) {
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		} else if (align == 1) {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		} else if (align == 2) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		} else if (align == 3) {
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		} else if (align == 4) {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		} else if (align == 5) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		} else if (align == 6) {
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		} else if (align == 7) {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		} else if (align == 8) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}

		table.addCell(cell);
		table.setTotalWidth(width);

		// String paperori = tnameVo.getString("paperori");
		float myTop = 0;
		// 因pdf坐标系是以左下角为原点，所以纵坐标需要换算
		// writer.flush();
		// 如果是综合表将报表左上移
		if (integrateFlag) {
			left = left - (int) minLeft + (int) l_margin;
			top = top - (int) minTop + (int) t_margin + (int) getTitleHeight() + (int) getRowHeight() + 5;
		}
		if ("1".equals(this.paperori)) {
			table.writeSelectedRows(0, -1, left, page_height - top, writer.getDirectContent());
			myTop = page_height - top;
		} else {

			table.writeSelectedRows(0, -1, left, page_width - top, writer.getDirectContent());

			myTop = page_width - top;
		}
		if (border == 0) {
			return;
		}
		if (!lLine) {
			MyPoint[] point = new MyPoint[2];
			point[0] = new MyPoint();
			point[1] = new MyPoint();
			point[0].x = left;
			point[0].y = myTop;
			point[1].x = left;
			point[1].y = myTop - height;
			lineList.add(point);
		}
		if (!rLine) {
			MyPoint[] point = new MyPoint[2];
			point[0] = new MyPoint();
			point[1] = new MyPoint();
			point[0].x = left + width;
			point[0].y = myTop;
			point[1].x = left + width;
			point[1].y = myTop - height;
			lineList.add(point);
		}
		if (!tLine) {
			MyPoint[] point = new MyPoint[2];
			point[0] = new MyPoint();
			point[1] = new MyPoint();
			point[0].x = left;
			point[0].y = myTop;
			point[1].x = left + width;
			point[1].y = myTop;
			lineList.add(point);
		}
		if (!bLine) {
			MyPoint[] point = new MyPoint[2];
			point[0] = new MyPoint();
			point[1] = new MyPoint();
			point[0].x = left;
			point[0].y = myTop - height;
			point[1].x = left + width;
			point[1].y = myTop - height;
			lineList.add(point);
		}
	}

	/**
	 * 获得PDF单元格最佳字体大小
	 * 
	 * @param fontSize
	 *            原始字体大小
	 * @param width
	 *            单元格宽度
	 * @param height
	 *            单元格高度
	 * @param context
	 *            单元格内容
	 * @return
	 */
	public int getFitFontSize(int fontSize, float width, float height, String context) {

		width -= 5;
		height -= 5;
		float size = Integer
				.parseInt(String.valueOf(height / (fontSize + 2)).substring(0,
						String.valueOf(height / (fontSize + 2)).indexOf(".")))
				* Integer.parseInt(String.valueOf(width / (fontSize + 2)).substring(0,
						String.valueOf(width / (fontSize + 2)).indexOf(".")));
		while (fontSize > 0 && (context.getBytes().length / 2) > size) {
			fontSize = fontSize - 1;
			size = Integer
					.parseInt(String.valueOf(height / (fontSize + 2)).substring(0,
							String.valueOf(height / (fontSize + 2)).indexOf(".")))
					* Integer.parseInt(String.valueOf(width / (fontSize + 2)).substring(0,
							String.valueOf(width / (fontSize + 2)).indexOf(".")));
		}
		return fontSize;
	}

	/**
	 * 
	 * @param writer
	 * 
	 * @param beginY
	 *            相对坐标的y
	 * @param beginAbsolutY
	 *            起始绝对纵坐标
	 * @param endAbsolutY
	 *            结束绝对纵坐标
	 * @param endHeight
	 *            最后一个超出页面格的height
	 * 为处理跨页项目，gridList会循环数次，将跨页项目都添加到gridList，直到最后循环时一起处理
	 */
	public void createCol(PdfWriter writer, int beginY, int beginAbsolutY, int endAbsolutY, int endHeight) {
		int rleft = 0;
		int rtop = 0;
		int rwidth = 0;
		int rheight = 0;
		int align = 0;
		ArrayList arrayList = new ArrayList();
		for (Iterator t = gridList.iterator(); t.hasNext();) {
			RecordVo vo = (RecordVo) t.next();
			if (vo.getInt("flag") == 2) {

				int fontsize = 9;
				if (vo.getString("fontsize") != null && vo.getString("fontsize").trim().length() > 0) {
                    fontsize = Integer.parseInt(vo.getString("fontsize"));
                }

				rleft = vo.getInt("rleft");
				rtop = vo.getInt("rtop");
				rwidth = vo.getInt("rwidth");
				rheight = vo.getInt("rheight");
				setLine(vo);
				align = vo.getInt("align");
				// vo.getString("r"));
				if ((rtop >= beginAbsolutY) && (rtop < (endAbsolutY + endHeight))) {
					if (rtop + rheight > endAbsolutY + endHeight) {
						//第一页放不下，将此项放入list中等最后处理
						vo.setInt("rleft", rleft);
						vo.setInt("rtop", endAbsolutY + endHeight);
						vo.setInt("rwidth", rwidth);
						vo.setInt("rheight", rheight - (endAbsolutY + endHeight - rtop));
						arrayList.add(vo);
						//插入此次能够放下的部分
						writeParagraph(writer, vo.getString("hz"), rleft, beginY + (rtop - beginAbsolutY), 1, rwidth,
								endAbsolutY + endHeight - rtop, align, fontsize, 1);
					} else {
						writeParagraph(writer, vo.getString("hz"), rleft, beginY + (rtop - beginAbsolutY), 1, rwidth,
								rheight, align, fontsize, 1);
					}
				}
			}
		}
		for (Iterator tt = arrayList.iterator(); tt.hasNext();) {
			RecordVo vo = (RecordVo) tt.next();
			gridList.add(vo);
		}
	}

	/**
	 * 设置cellborder的标志位
	 * 
	 * @param vo
	 */
	public void setLine(RecordVo vo) {
		if (vo.getInt("l") == 1) {
			lLine = true;
		} else {
			lLine = false;
		}
		if (vo.getInt("r") == 1) {
			rLine = true;
		} else {
			rLine = false;
		}
		if (vo.getInt("t") == 1) {
			tLine = true;
		} else {
			tLine = false;
		}
		if (vo.getInt("b") == 1) {
			bLine = true;
		} else {
			bLine = false;
		}
	}

	public void setLine(boolean flg) {
		lLine = flg;
		rLine = flg;
		bLine = flg;
		tLine = flg;
	}

	/**
	 * 综合表 表头表尾处理 说明:首页/中间页显示表头不显示表尾 末页显示表尾
	 * 
	 * @param writer
	 */
	public void createIntegrateHeaders(PdfWriter writer, int pageNO) {

		this.getBottomNum();// 目的获取总页数

		fontName = this.getIntegerateFontName();
		fontSize = this.getIntegerateFontSize();
		if ("[0]".equals(fontSize) || "".equals(fontSize.trim())) {
			fontSize = "20";
		}
		int fontStyle = 0;
		if ("#fi[1]".equals(integerateFontItalic)) {
            fontStyle = fontStyle | Font.ITALIC;
        }
		if ("#fb[1]".equals(integerateFontBold)) {
            fontStyle = fontStyle | Font.BOLD;
        }
		if ("#fs[1]".equals(integerateFontStrike)) {
            fontStyle = fontStyle | Font.STRIKETHRU;
        }
		if ("#fu[1]".equals(integerateFontUnderLine)) {
            fontStyle = fontStyle | Font.UNDERLINE;
        }
		font = FontFamilyType.getFont(fontName, fontStyle, Integer.parseInt(fontSize));
		if (!"".equals(integerateFontColor)) {
			integerateFontColor = integerateFontColor.replaceFirst("#", "0x");
			Integer color = Integer.decode(integerateFontColor);
			font.setColor(new Color(color.intValue()));
		}
		titleHeight = 0;
		// 处理标题
		if (!"".equals(integerateTitle)) {
			Paragraph pragraph = new Paragraph(integerateTitle, font);
			pragraph.setAlignment(Paragraph.ALIGN_CENTER);
			try {
				PdfPTable table = new PdfPTable(1);
				PdfPCell cellCenter = new PdfPCell(pragraph);
				cellCenter.setHorizontalAlignment(Element.ALIGN_CENTER);
				cellCenter.setBorderWidth(0);
				table.addCell(cellCenter);
				table.setTotalWidth(this.real_width);
				titleHeight = table.getRowHeight(0);
				if ("1".equals(paperori)) {
					table.setTotalWidth(page_width - this.l_margin - this.r_margin);
					table.writeSelectedRows(0, -1, l_margin, page_height - this.t_margin, writer.getDirectContent());
				} else {
					table.setTotalWidth(page_height - this.l_margin - this.r_margin);
					table.writeSelectedRows(0, -1, l_margin, page_width - this.t_margin, writer.getDirectContent());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fontName = this.getIntegerateHeadFontName();
		fontSize = this.getIntegerateHeadFontSize();
		if ("[0]".equals(fontSize) || "".equals(fontSize.trim())) {
			fontSize = "20";
		}
		fontStyle = 0;
		if ("#fi[1]".equals(integerateHeadFontItalic)) {
            fontStyle = fontStyle | Font.ITALIC;
        }
		if ("#fb[1]".equals(integerateHeadFontBold)) {
            fontStyle = fontStyle | Font.BOLD;
        }
		if ("#fs[1]".equals(integerateHeadFontStrike)) {
            fontStyle = fontStyle | Font.STRIKETHRU;
        }
		if ("#fu[1]".equals(integerateHeadFontUnderLine)) {
            fontStyle = fontStyle | Font.UNDERLINE;
        }

		try {
			font = FontFamilyType.getFont(fontName, fontStyle, Integer.parseInt(fontSize));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!"".equals(integerateHeadFontColor)) {
			integerateHeadFontColor = integerateHeadFontColor.replaceFirst("#", "0x");
			Integer color = Integer.decode(integerateHeadFontColor);
			font.setColor(new Color(color.intValue()));
		}
		ReportParseVo vo = new ReportParseVo();
		Paragraph pragraphLeft = new Paragraph(getParamterValuse(integerateHead_flw, vo), font);
		Paragraph pragraphRight = new Paragraph(getParamterValuse(integerateHead_frw, vo), font);
		Paragraph pragraphCenter = new Paragraph(getParamterValuse(integerateHead_fmw, vo), font);
		pragraphLeft.setAlignment(Paragraph.ALIGN_LEFT);
		pragraphRight.setAlignment(Paragraph.ALIGN_RIGHT);
		pragraphCenter.setAlignment(Paragraph.ALIGN_CENTER);
		PdfPTable table = new PdfPTable(3);
		PdfPCell cellLeft = new PdfPCell(pragraphLeft);
		cellLeft.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell cellRight = new PdfPCell(pragraphRight);
		cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
		PdfPCell cellCenter = new PdfPCell(pragraphCenter);
		cellCenter.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellLeft.setBorderWidth(0);
		cellRight.setBorderWidth(0);
		cellCenter.setBorderWidth(0);

		table.addCell(cellLeft);
		table.addCell(cellCenter);
		table.addCell(cellRight);
		table.setTotalWidth(this.real_width);
		rowHeight = table.getRowHeight(0);
		try {
			if ("1".equals(paperori)) {
				table.setTotalWidth(page_width - this.l_margin - this.r_margin);
				table.writeSelectedRows(0, -1, l_margin, page_height - (this.t_margin + titleHeight),
						writer.getDirectContent());
			} else {
				table.setTotalWidth(page_height - this.l_margin - this.r_margin);
				table.writeSelectedRows(0, -1, l_margin, page_width - (this.t_margin + titleHeight),
						writer.getDirectContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (this.pageValue == pageNO) { // 最后一页
			// 处理下表头
			fontName = this.getIntegerateUnderHeadFontName();
			fontSize = this.getIntegerateUnderHeadFontSize();
			if ("[0]".equals(fontSize) || "".equals(fontSize.trim())) {
				fontSize = "20";
			}
			fontStyle = 0;
			if ("#fi[1]".equals(integerateUnderHeadFontItalic)) {
                fontStyle = fontStyle | Font.ITALIC;
            }
			if ("#fb[1]".equals(integerateUnderHeadFontBold)) {
                fontStyle = fontStyle | Font.BOLD;
            }
			if ("#fs[1]".equals(integerateUnderHeadFontStrike)) {
                fontStyle = fontStyle | Font.STRIKETHRU;
            }
			if ("#fu[1]".equals(integerateUnderHeadFontUnderLine)) {
                fontStyle = fontStyle | Font.UNDERLINE;
            }
			try {
				font = FontFamilyType.getFont(fontName, fontStyle, Integer.parseInt(fontSize));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!"".equals(integerateUnderHeadFontColor)) {
				integerateUnderHeadFontColor = integerateUnderHeadFontColor.replaceFirst("#", "0x");
				Integer color = Integer.decode(integerateUnderHeadFontColor);
				font.setColor(new Color(color.intValue()));
			}
			pragraphLeft = new Paragraph(getParamterValuse(integerateUnderHead_flw, vo), font);
			pragraphRight = new Paragraph(getParamterValuse(integerateUnderHead_frw, vo), font);
			pragraphCenter = new Paragraph(getParamterValuse(integerateUnderHead_fmw, vo), font);
			pragraphLeft.setAlignment(Paragraph.ALIGN_LEFT);
			pragraphRight.setAlignment(Paragraph.ALIGN_RIGHT);
			pragraphCenter.setAlignment(Paragraph.ALIGN_CENTER);
			table = new PdfPTable(3);
			// table.setWidths(200);
			cellLeft = new PdfPCell(pragraphLeft);
			cellLeft.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellRight = new PdfPCell(pragraphRight);
			cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellCenter = new PdfPCell(pragraphCenter);
			cellCenter.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellLeft.setBorderWidth(0);
			cellRight.setBorderWidth(0);
			cellCenter.setBorderWidth(0);

			table.addCell(cellLeft);
			table.addCell(cellCenter);
			table.addCell(cellRight);
			if ("1".equals(paperori)) {
				table.setTotalWidth(page_width - this.l_margin - this.r_margin);
			} else {
				table.setTotalWidth(page_height - this.l_margin - this.r_margin);
			}

			table.writeSelectedRows(0, -1, this.l_margin, this.b_margin + 50, writer.getDirectContent());
		}

		// 设置表体的字体属性
		fontName = this.getIntegerateBodyFontName();
		fontSize = this.getIntegerateBodyFontSize();
		if ("[0]".equals(fontSize) || "".equals(fontSize.trim())) {
			fontSize = "10";
		}
		fontStyle = Font.NORMAL;
		if ("#fi[1]".equals(integerateBodyFontItalic)) {
            fontStyle = fontStyle | Font.ITALIC;
        }
		if ("#fb[1]".equals(integerateBodyFontBold)) {
            fontStyle = fontStyle | Font.BOLD;
        }
		if ("#fu[1]".equals(integerateBodyFontUnderLine)) {
            fontStyle = fontStyle | Font.UNDERLINE;
        }
		font = FontFamilyType.getFont(fontName, fontStyle, Integer.parseInt(fontSize));
		if (!"".equals(integerateBodyFontColor)) {
			integerateBodyFontColor = integerateBodyFontColor.replaceFirst("#", "0x");
			Integer color = Integer.decode(integerateBodyFontColor);
			font.setColor(new Color(color.intValue()));
		}
	}

	private String getParamterValuse(String parameter_name, ReportParseVo vo) {
		String str = "";
		parameter_name = vo.getRealcontent(parameter_name, userview, pageValue, null, pageCount, dao);
		if (parameter_name != null) {
			if ("&[日期]".equalsIgnoreCase(parameter_name)) {
				GregorianCalendar d = new GregorianCalendar();
				str = d.get(Calendar.YEAR) + "." + (d.get(Calendar.MONTH) + 1) + "." + d.get(Calendar.DATE);
			} else if ("&[制作人]".equalsIgnoreCase(parameter_name)) {
				str = getFullName("制作人:" + username);
			} else if ("&[页码]".equalsIgnoreCase(parameter_name)) {
				str = "第" + String.valueOf(pageValue) + "页";

			} else if ("&[时间]".equalsIgnoreCase(parameter_name)) {
				str = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.CHINA).format(new Date());
			} else {
                str = parameter_name;
            }
		}
		return str;
	}

	/**
	 * 画横表头
	 */
	public void createTableHeaders(PdfWriter writer, Document document) {
		try {
			int rleft = 0;
			int rtop = 0;
			int rwidth = 0;
			int rheight = 0;
			int align = 0;
			for (Iterator t = gridList.iterator(); t.hasNext();) {
				RecordVo vo = (RecordVo) t.next();
				setLine(vo);
				if (vo.getInt("flag") == 1 || vo.getInt("flag") == 0) {
					if (!integrateFlag) {
						fontName = vo.getString("fontname");
						fontSize = vo.getString("fontsize");
						fontEffect = vo.getString("fonteffect");
						font = FontFamilyType.getFont(fontName, fontEffect, Integer.parseInt(fontSize));
					}
					rleft = vo.getInt("rleft");
					rtop = vo.getInt("rtop");
					rwidth = vo.getInt("rwidth");
					rheight = vo.getInt("rheight");
					align = vo.getInt("align");
					writeParagraph(writer, vo.getString("hz"), rleft, rtop, 1, rwidth, rheight, align,
							Integer.parseInt(fontSize), 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 画数据区域即表体
	 * 
	 * @param writer
	 * 
	 * @param rowBegin
	 *            从第几行
	 * @param rowEnd
	 *            到第几行
	 * @param top
	 */
	public void createData(PdfWriter writer, int rowBegin, int rowEnd, int top) {
		if (rowBegin > rowEnd || rowBegin < 0) {
            return;
        }
		// 行数
		int rowNum = 0;
		ReportResultBo reportResultBo = new ReportResultBo(con);
		reportResultBo.setColinfolist(this.colInfoBGrid);
		// resultList中是String[]型的列表。每一个String[]是表体中的一行数据
		if ("1".equals(operateObject)) {
            resultList = reportResultBo.getTBxxResultList(tabid, username);
        }
		if ("2".equals(operateObject)) {
            resultList = reportResultBo.getTTxxResultList(tabid, unitcode);
        }
		// 判断是否有数据、没数据画空表
		if (resultList.size() > 0) {
			for (int i = rowBegin; i <= rowEnd; i++) {
				rowNum = 0;
				String[] rowInfo = (String[]) resultList.get(i);
				// 纵栏表头
				RecordVo colVo = (RecordVo) colInfoBGrid.get(i);
				// 如果不是甲栏则列数加1
				if (colVo.getInt("flag1") != 4) {
                    colNum++;
                }

				for (int j = 0; j < rowInfo.length; j++) {
					String context = "";
					RecordVo rowVo = (RecordVo) rowInfoBGrid.get(j);

					int npercent = 0;
					// if(rowVo.getInt("flag1")==2&&colVo.getInt("flag1")==2) //统计个数
					// npercent=0;
					// else
					/*
					 * if(rowVo.getInt("flag1")==3) npercent=rowVo.getInt("npercent"); else
					 * if(colVo.getInt("flag1")==3) npercent=colVo.getInt("npercent"); else
					 */
					npercent = rowVo.getInt("npercent") >= colVo.getInt("npercent") ? rowVo.getInt("npercent")
							: colVo.getInt("npercent");
					int left = rowVo.getInt("rleft");
					int width = rowVo.getInt("rwidth");
					int height = colVo.getInt("rheight");
					if (colVo.getInt("flag1") == 4 && rowVo.getInt("flag1") == 4) {
						context = "";
					} else if (colVo.getInt("flag1") == 4) {
						rowNum++;
						context = String.valueOf(rowNum);
					} else if (rowVo.getInt("flag1") == 4) {
						context = String.valueOf(colNum);
					} else {
						if (rowInfo[j] == null || "0".equals(rowInfo[j])) {
                            context = "";
                        } else {
                            context = PubFunc.round(rowInfo[j], npercent);
                        }
					}
					setLine(true);
					if (rowVo.getInt("l") == 0) {
						lLine = false;
					}
					if (rowVo.getInt("r") == 0) {
						rLine = false;
					}
					if (colVo.getInt("t") == 0) {
						tLine = false;
					}
					if (colVo.getInt("b") == 0) {
						bLine = false;
					}
					/*
					 * writeParagraph(writer, context, left, top, 1, width, height, 8);
					 */
					writeDataParagraph(writer, context, left, top, 1, width, height, 8);
				}
			}
		} else {
			for (int i = rowBegin; i <= rowEnd; i++) {
				rowNum = 0;
				// 纵栏表头
				RecordVo colVo = (RecordVo) colInfoBGrid.get(i);
				// 如果不是甲栏则列数加1
				if (colVo.getInt("flag1") != 4) {
                    colNum++;
                }
				for (int j = 0; j < rowInfoBGrid.size(); j++) {
					String context = "";
					RecordVo rowVo = (RecordVo) rowInfoBGrid.get(j);
					int left = rowVo.getInt("rleft");
					int width = rowVo.getInt("rwidth");
					int height = colVo.getInt("rheight");
					if (rowVo.getInt("flag1") != 4) {
                        rowNum++;
                    }
					if (colVo.getInt("flag1") == 4 && rowVo.getInt("flag1") == 4) {
						context = "";
					} else if (colVo.getInt("flag1") != 4 && rowVo.getInt("flag1") == 4) {
						context = String.valueOf(colNum);
					} else if (rowVo.getInt("flag1") != 4 && colVo.getInt("flag1") == 4) {
						context = String.valueOf(rowNum);
					}
					setLine(true);
					// 根据横表头和纵表头调置表体单元格上下左右边线是否为虚线
					if (rowVo.getInt("l") == 0) {
						lLine = false;
					}
					if (rowVo.getInt("r") == 0) {
						rLine = false;
					}
					if (colVo.getInt("t") == 0) {
						tLine = false;
					}
					if (colVo.getInt("b") == 0) {
						bLine = false;
					}
					/*
					 * writeParagraph(writer, context, left, top, 1, width, height, 8);
					 */
					writeDataParagraph(writer, context, left, top, 1, width, height, 8);
				}
			}
		}
	}

	/**
	 * 画虚线
	 * 
	 * @param writer
	 */
	private void printBrokenLine(PdfWriter writer) {
		PdfContentByte cb = writer.getDirectContent();
		Iterator it = lineList.iterator();
		MyPoint[] p = null;
		while (it.hasNext()) {
			p = (MyPoint[]) it.next();
			cb.moveTo(p[0].getX(), p[0].getY());
			cb.lineTo(p[1].x, p[1].y);
			cb.setColorStroke(new Color(255, 255, 255));
			cb.stroke();
		}
	}

	/**
	 * 向pdf画cell的方法
	 * 
	 * @param writer
	 *            pdf输出
	 * @param content
	 *            要输出的内容
	 * @param left
	 *            x轴坐标即横坐标
	 * @param top
	 *            y轴坐标即纵坐标
	 */
	public void writeDataParagraph(PdfWriter writer, String content, int left, int top, int border, int width,
			int height, int align) {
		if (content == null || "undefined".equals(content.trim())) {
			content = "";
		}
		// System.out.println(writer);
		String s = content;
		content = "";
		// 去除content字符串中的空格
		int n = 0;
		for (int i = 0; i < s.length(); i++) {
			String temp = "";
			if (s.charAt(i) == 13 || s.charAt(i) == 10) {
				continue;
			} else {
				content += s.charAt(i);
				temp += s.charAt(i);
				if ("0".indexOf(temp) != -1) {
					n++;
				}
			}
		}
		// content = content.trim();
		if ("0".equals(content)) {
			content = "";
		}
		// 去除0.00类似数据dumeilong
		if (content.indexOf(".") != -1) {
			if (n == content.length() - 1) {
				content = "";
			}
		} else {
			if (n == content.length()) {
				content = "";
			}
		}

		content = content.replaceAll("`", "\n");
		if (border == 0 && width == 100 && height == 0 && align == 0) {
			// 表头不设置字体
		} else {
			int new_size = getFitFontSize((int) font.size(), width, height, content); // getDataFontSize((int)
																						// font.size(), width, height,
																						// content);
			font.setSize(new_size);
		}

		Paragraph pragraph = new Paragraph(content, font);
		// Paragraph pragraph = new Paragraph(content);
		PdfPTable table = new PdfPTable(1);
		pragraph.setAlignment(1);
		PdfPCell cell = new PdfPCell(pragraph);
		if (border == 0) {
			cell.setNoWrap(true);
			cell.setBorderWidth(0);
		}
		if (border == 1) {
			table.setLockedWidth(true);
		}

		// 设置列最大高度
		cell.setFixedHeight(height);
		cell.setMinimumHeight(height);
		// ALIGN_JUSTIFIED基于最合适的
		if (align == 0) {
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		} else if (align == 1) {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		} else if (align == 2) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		} else if (align == 3) {
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		} else if (align == 4) {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		} else if (align == 5) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		} else if (align == 6) {
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		} else if (align == 7) {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		} else if (align == 8) {
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}

		table.addCell(cell);
		table.setTotalWidth(width);

		// String paperori = tnameVo.getString("paperori");
		float myTop = 0;
		// 因pdf坐标系是以左下角为原点，所以纵坐标需要换算
		// writer.flush();
		// 如果是综合表将报表左上移
		if (integrateFlag) {
			left = left - (int) minLeft + (int) l_margin;
			top = top - (int) minTop + (int) t_margin + (int) getTitleHeight() + (int) getRowHeight() + 5;
		}
		if ("1".equals(this.paperori)) {
			table.writeSelectedRows(0, -1, left, page_height - top, writer.getDirectContent());
			myTop = page_height - top;
		} else {

			table.writeSelectedRows(0, -1, left, page_width - top, writer.getDirectContent());

			myTop = page_width - top;
		}
		if (border == 0) {
			return;
		}
		if (!lLine) {
			MyPoint[] point = new MyPoint[2];
			point[0] = new MyPoint();
			point[1] = new MyPoint();
			point[0].x = left;
			point[0].y = myTop;
			point[1].x = left;
			point[1].y = myTop - height;
			lineList.add(point);
		}
		if (!rLine) {
			MyPoint[] point = new MyPoint[2];
			point[0] = new MyPoint();
			point[1] = new MyPoint();
			point[0].x = left + width;
			point[0].y = myTop;
			point[1].x = left + width;
			point[1].y = myTop - height;
			lineList.add(point);
		}
		if (!tLine) {
			MyPoint[] point = new MyPoint[2];
			point[0] = new MyPoint();
			point[1] = new MyPoint();
			point[0].x = left;
			point[0].y = myTop;
			point[1].x = left + width;
			point[1].y = myTop;
			lineList.add(point);
		}
		if (!bLine) {
			MyPoint[] point = new MyPoint[2];
			point[0] = new MyPoint();
			point[1] = new MyPoint();
			point[0].x = left;
			point[0].y = myTop - height;
			point[1].x = left + width;
			point[1].y = myTop - height;
			lineList.add(point);
		}
	}

	public String createPhotoFile(RecordVo vo, String ext) {
		String fileName = "";
		java.io.FileOutputStream fout = null;
		try {
			File tempFile = File.createTempFile(ServletUtilities.tempFilePrefix, ext,
					new File(System.getProperty("java.io.tmpdir")));
			InputStream in = (InputStream) vo.getObject("content");
			if (in == null) {
                return "";
            }
			fout = new java.io.FileOutputStream(tempFile);
			int len;
			byte buf[] = new byte[1024];

			while ((len = in.read(buf, 0, 1024)) != -1) {
				fout.write(buf, 0, len);

			}
			fout.close();

			fileName = tempFile.getName();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(fout);
		}
		return fileName;
	}

	/**
	 * 取得tpage下的extendAttr字段的内容<image>
	 * <ext>.JPG|.BMP</ext><stretch>拉伸True|False</stretch>
	 * <transparent>透明True|False</transparent>
	 * <proportional>保持比例True|False</proportional>
	 * <background>置底True(默认值)|置顶False</background> 1表示图片，2表示拉伸，3透明，4保持比列，5置底
	 * </image>
	 *
	 * @return
	 */
	public String getExtendAttrContext(int flag, String extendAttr) {
		String temp = "";

		if (extendAttr != null && extendAttr.length() > 0) {
			if (extendAttr.indexOf("<image>") != -1) {
				if (flag == 1) {
					if (extendAttr.indexOf("<ext>") == -1) {
                        temp = ".jpg";
                    } else {
						int fromIndex = extendAttr.indexOf("<ext>");
						int toIndex = extendAttr.indexOf("</ext>");
						temp = extendAttr.substring(fromIndex + 5, toIndex).trim();

					}
				}
				if (flag == 2) {
					if (extendAttr.indexOf("<stretch>") == -1) {
                        temp = "True";
                    } else {
						int fromIndex = extendAttr.indexOf("<stretch>");
						int toIndex = extendAttr.indexOf("</stretch>");
						temp = extendAttr.substring(fromIndex + 9, toIndex).trim();

					}
				}
				if (flag == 3) {
					if (extendAttr.indexOf("<transparent>") == -1) {
                        temp = "True";
                    } else {
						int fromIndex = extendAttr.indexOf("<transparent>");
						int toIndex = extendAttr.indexOf("</transparent>");
						temp = extendAttr.substring(fromIndex + 13, toIndex).trim();

					}
				}
				if (flag == 4) {
					if (extendAttr.indexOf("<proportional>") == -1) {
                        temp = "True";
                    } else {
						int fromIndex = extendAttr.indexOf("<proportional>");
						int toIndex = extendAttr.indexOf("</proportional>");
						temp = extendAttr.substring(fromIndex + 14, toIndex).trim();

					}
				}
				if (flag == 5) {
					if (extendAttr.indexOf("<background>") == -1) {
                        temp = "True";
                    } else {
						int fromIndex = extendAttr.indexOf("<background>");
						int toIndex = extendAttr.indexOf("</background>");
						temp = extendAttr.substring(fromIndex + 12, toIndex).trim();

					}
				}
			}
		}

		return temp;
	}

	/**
	 * 获得PDF单元格最佳字体大小(数据区域)
	 * 
	 * @param fontSize
	 *            原始字体大小
	 * @param width
	 *            单元格宽度
	 * @param height
	 *            单元格高度
	 * @param context
	 *            单元格内容
	 * @return
	 */
	public int getDataFontSize(int fontsize, float width, float height, String context) {

		if (this.baseFontSize == 0) {
		} else {
			fontsize = this.baseFontSize;
		}

		int length = context.getBytes().length;

		if (length != 0) {

			String temp = String.valueOf(width * 2 / length);
			int maxFontSize = Integer.parseInt(temp.substring(0, temp.indexOf(".")));

			if (fontsize > maxFontSize) {
				return maxFontSize - 1;
			}
		}

		return fontsize;

	}

	/**
	 * 子集中格式化日期字符串
	 * 
	 * @param value
	 *            日期字段值 yyyy-mm-dd
	 * @return
	 */
	private String formatDateFiledsetValue(String value, int disformat) {
		StringBuffer buf = new StringBuffer();

		String prefix = "", strext = "";

		if ("".equals(value)) {
			buf.append(prefix);
			buf.append(strext);
			return buf.toString();
		} else {
			buf.append(prefix);
		}
		Date date = DateUtils.getDate(value, "yyyy-MM-dd");
		int year = DateUtils.getYear(date);
		int month = DateUtils.getMonth(date);
		int day = DateUtils.getDay(date);
		value = value.replaceAll("-", ".");
		switch (disformat) {
		case 0: // 1991.12.3
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		case 1:// 1992.02.01
			buf.append(year);
			buf.append(".");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			buf.append(".");
			if (day >= 10) {
                buf.append(day);
            } else {
				buf.append("0");
				buf.append(day);
			}
			break;
		case 2:// 1991年1月2日
			buf.append(year);
			buf.append("年");
			buf.append(month);
			buf.append("月");
			buf.append(day);
			buf.append("日");
			break;
		case 3:// 1999年02月03日
			buf.append(year);
			buf.append("年");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			if (day >= 10) {
                buf.append(day);
            } else {
				buf.append("0");
				buf.append(day);
			}
			buf.append("日");
			break;
		case 4: // 1991-12-3
			buf.append(year);
			buf.append("-");
			buf.append(month);
			buf.append("-");
			buf.append(day);
			break;
		case 5:// 1992-02-01
			buf.append(year);
			buf.append("-");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			buf.append("-");
			if (day >= 10) {
                buf.append(day);
            } else {
				buf.append("0");
				buf.append(day);
			}
			break;
		case 6:// 1999年02月
			buf.append(year);
			buf.append("年");
			if (month >= 10) {
                buf.append(month);
            } else {
				buf.append("0");
				buf.append(month);
			}
			buf.append("月");
			break;
		default:
			buf.append(year);
			buf.append(".");
			buf.append(month);
			buf.append(".");
			buf.append(day);
			break;
		}
		return buf.toString();
	}

	public boolean isPaginationHeader() {
		return paginationHeader;
	}

	public void setPaginationHeader(boolean paginationHeader) {
		this.paginationHeader = paginationHeader;
	}

	public float getReal_height() {
		return real_height;
	}

	public void setReal_height(float real_height) {
		this.real_height = real_height;
	}

	public float getReal_width() {
		return real_width;
	}

	public void setReal_width(float real_width) {
		this.real_width = real_width;
	}

	public int getPageCount() {
		return pageCount;
	}

	public ArrayList getColInfoBGrid() {
		return colInfoBGrid;
	}

	public void setColInfoBGrid(ArrayList colInfoBGrid) {
		this.colInfoBGrid = colInfoBGrid;
	}

	public ArrayList getGridList() {
		return gridList;
	}

	public void setGridList(ArrayList gridList) {
		this.gridList = gridList;
	}

	public ArrayList getRowInfoBGrid() {
		return rowInfoBGrid;
	}

	public void setRowInfoBGrid(ArrayList rowInfoBGrid) {
		this.rowInfoBGrid = rowInfoBGrid;
	}

	public ArrayList getResultList() {
		return resultList;
	}

	public void setResultList(ArrayList resultList) {
		this.resultList = resultList;
	}

	public float getB_margin() {
		return b_margin;
	}

	public void setB_margin(float b_margin) {
		this.b_margin = b_margin;
	}

	public float getL_margin() {
		return l_margin;
	}

	public void setL_margin(float l_margin) {
		this.l_margin = l_margin;
	}

	public float getR_margin() {
		return r_margin;
	}

	public void setR_margin(float r_margin) {
		this.r_margin = r_margin;
	}

	public float getT_margin() {
		return t_margin;
	}

	public void setT_margin(float t_margin) {
		this.t_margin = t_margin;
	}

	public float getPage_height() {
		return page_height;
	}

	public void setPage_height(float page_height) {
		this.page_height = page_height;
	}

	public float getPage_width() {
		return page_width;
	}

	public void setPage_width(float page_width) {
		this.page_width = page_width;
	}

	public String getPaperori() {
		return paperori;
	}

	public void setPaperori(String paperori) {
		this.paperori = paperori;
	}

	public String getFontEffect() {
		return fontEffect;
	}

	public void setFontEffect(String fontEffect) {
		this.fontEffect = fontEffect;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getIntegerateTitle() {
		return integerateTitle;
	}

	public void setIntegerateTitle(String integerateTitle) {
		this.integerateTitle = integerateTitle;
	}

	public boolean isIntegrateFlag() {
		return integrateFlag;
	}

	public void setIntegrateFlag(boolean integrateFlag) {
		this.integrateFlag = integrateFlag;
	}

	public String getIntegerateFont() {
		return integerateFont;
	}

	public void setIntegerateFont(String integerateFont) {
		this.integerateFont = integerateFont;
	}

	public String getIntegerateFontEffect() {
		return integerateFontEffect;
	}

	public void setIntegerateFontEffect(String integerateFontEffect) {
		this.integerateFontEffect = integerateFontEffect;
	}

	public String getIntegerateFontName() {
		return integerateFontName;
	}

	public void setIntegerateFontName(String integerateFontName) {
		this.integerateFontName = integerateFontName;
	}

	public String getIntegerateFontSize() {
		return integerateFontSize;
	}

	public void setIntegerateFontSize(String integerateFontSize) {
		this.integerateFontSize = integerateFontSize;
	}

	public float getIntegrate_b_margin() {
		return integrate_b_margin;
	}

	public void setIntegrate_b_margin(float integrate_b_margin) {
		this.integrate_b_margin = integrate_b_margin;
	}

	public float getIntegrate_l_margin() {
		return integrate_l_margin;
	}

	public void setIntegrate_l_margin(float integrate_l_margin) {
		this.integrate_l_margin = integrate_l_margin;
	}

	public float getIntegrate_r_margin() {
		return integrate_r_margin;
	}

	public void setIntegrate_r_margin(float integrate_r_margin) {
		this.integrate_r_margin = integrate_r_margin;
	}

	public float getIntegrate_t_margin() {
		return integrate_t_margin;
	}

	public void setIntegrate_t_margin(float integrate_t_margin) {
		this.integrate_t_margin = integrate_t_margin;
	}

	public String getIntegerateFontColor() {
		return integerateFontColor;
	}

	public void setIntegerateFontColor(String integerateFontColor) {
		this.integerateFontColor = integerateFontColor;
	}

	public String getIntegerateFontItalic() {
		return integerateFontItalic;
	}

	public void setIntegerateFontItalic(String integerateFontItalic) {
		this.integerateFontItalic = integerateFontItalic;
	}

	public String getIntegerateFontBold() {
		return integerateFontBold;
	}

	public void setIntegerateFontBold(String integerateFontBold) {
		this.integerateFontBold = integerateFontBold;
	}

	public String getIntegerateFontStrike() {
		return integerateFontStrike;
	}

	public void setIntegerateFontStrike(String integerateFontStrike) {
		this.integerateFontStrike = integerateFontStrike;
	}

	public String getIntegerateFontUnderLine() {
		return integerateFontUnderLine;
	}

	public void setIntegerateFontUnderLine(String integerateFontUnderLine) {
		this.integerateFontUnderLine = integerateFontUnderLine;
	}

	public String getIntegeratePaperOri() {
		return integeratePaperOri;
	}

	public void setIntegeratePaperOri(String integeratePaperOri) {
		this.integeratePaperOri = integeratePaperOri;
	}

	public String getIntegerateHead_flw() {
		return integerateHead_flw;
	}

	public void setIntegerateHead_flw(String integerateHead_flw) {
		this.integerateHead_flw = integerateHead_flw;
	}

	public String getIntegerateHead_fmw() {
		return integerateHead_fmw;
	}

	public void setIntegerateHead_fmw(String integerateHead_fmw) {
		this.integerateHead_fmw = integerateHead_fmw;
	}

	public String getIntegerateHead_frw() {
		return integerateHead_frw;
	}

	public void setIntegerateHead_frw(String integerateHead_frw) {
		this.integerateHead_frw = integerateHead_frw;
	}

	public String getIntegerateHeadFont() {
		return integerateHeadFont;
	}

	public void setIntegerateHeadFont(String integerateHeadFont) {
		this.integerateHeadFont = integerateHeadFont;
	}

	public String getIntegerateHeadFontBold() {
		return integerateHeadFontBold;
	}

	public void setIntegerateHeadFontBold(String integerateHeadFontBold) {
		this.integerateHeadFontBold = integerateHeadFontBold;
	}

	public String getIntegerateHeadFontColor() {
		return integerateHeadFontColor;
	}

	public void setIntegerateHeadFontColor(String integerateHeadFontColor) {
		this.integerateHeadFontColor = integerateHeadFontColor;
	}

	public String getIntegerateHeadFontEffect() {
		return integerateHeadFontEffect;
	}

	public void setIntegerateHeadFontEffect(String integerateHeadFontEffect) {
		this.integerateHeadFontEffect = integerateHeadFontEffect;
	}

	public String getIntegerateHeadFontItalic() {
		return integerateHeadFontItalic;
	}

	public void setIntegerateHeadFontItalic(String integerateHeadFontItalic) {
		this.integerateHeadFontItalic = integerateHeadFontItalic;
	}

	public String getIntegerateHeadFontName() {
		return integerateHeadFontName;
	}

	public void setIntegerateHeadFontName(String integerateHeadFontName) {
		this.integerateHeadFontName = integerateHeadFontName;
	}

	public String getIntegerateHeadFontSize() {
		return integerateHeadFontSize;
	}

	public void setIntegerateHeadFontSize(String integerateHeadFontSize) {
		this.integerateHeadFontSize = integerateHeadFontSize;
	}

	public String getIntegerateHeadFontStrike() {
		return integerateHeadFontStrike;
	}

	public void setIntegerateHeadFontStrike(String integerateHeadFontStrike) {
		this.integerateHeadFontStrike = integerateHeadFontStrike;
	}

	public String getIntegerateHeadFontUnderLine() {
		return integerateHeadFontUnderLine;
	}

	public void setIntegerateHeadFontUnderLine(String integerateHeadFontUnderLine) {
		this.integerateHeadFontUnderLine = integerateHeadFontUnderLine;
	}

	public String getIntegerateBodyFont() {
		return integerateBodyFont;
	}

	public void setIntegerateBodyFont(String integerateBodyFont) {
		this.integerateBodyFont = integerateBodyFont;
	}

	public String getIntegerateBodyFontBold() {
		return integerateBodyFontBold;
	}

	public void setIntegerateBodyFontBold(String integerateBodyFontBold) {
		this.integerateBodyFontBold = integerateBodyFontBold;
	}

	public String getIntegerateBodyFontColor() {
		return integerateBodyFontColor;
	}

	public void setIntegerateBodyFontColor(String integerateBodyFontColor) {
		this.integerateBodyFontColor = integerateBodyFontColor;
	}

	public String getIntegerateBodyFontEffect() {
		return integerateBodyFontEffect;
	}

	public void setIntegerateBodyFontEffect(String integerateBodyFontEffect) {
		this.integerateBodyFontEffect = integerateBodyFontEffect;
	}

	public String getIntegerateBodyFontItalic() {
		return integerateBodyFontItalic;
	}

	public void setIntegerateBodyFontItalic(String integerateBodyFontItalic) {
		this.integerateBodyFontItalic = integerateBodyFontItalic;
	}

	public String getIntegerateBodyFontName() {
		return integerateBodyFontName;
	}

	public void setIntegerateBodyFontName(String integerateBodyFontName) {
		this.integerateBodyFontName = integerateBodyFontName;
	}

	public String getIntegerateBodyFontSize() {
		return integerateBodyFontSize;
	}

	public void setIntegerateBodyFontSize(String integerateBodyFontSize) {
		this.integerateBodyFontSize = integerateBodyFontSize;
	}

	public String getIntegerateUnderHead_flw() {
		return integerateUnderHead_flw;
	}

	public void setIntegerateUnderHead_flw(String integerateUnderHead_flw) {
		this.integerateUnderHead_flw = integerateUnderHead_flw;
	}

	public String getIntegerateUnderHead_fmw() {
		return integerateUnderHead_fmw;
	}

	public void setIntegerateUnderHead_fmw(String integerateUnderHead_fmw) {
		this.integerateUnderHead_fmw = integerateUnderHead_fmw;
	}

	public String getIntegerateUnderHeadFont() {
		return integerateUnderHeadFont;
	}

	public void setIntegerateUnderHeadFont(String integerateUnderHeadFont) {
		this.integerateUnderHeadFont = integerateUnderHeadFont;
	}

	public String getIntegerateUnderHeadFontBold() {
		return integerateUnderHeadFontBold;
	}

	public void setIntegerateUnderHeadFontBold(String integerateUnderHeadFontBold) {
		this.integerateUnderHeadFontBold = integerateUnderHeadFontBold;
	}

	public String getIntegerateUnderHeadFontColor() {
		return integerateUnderHeadFontColor;
	}

	public void setIntegerateUnderHeadFontColor(String integerateUnderHeadFontColor) {
		this.integerateUnderHeadFontColor = integerateUnderHeadFontColor;
	}

	public String getIntegerateUnderHeadFontEffect() {
		return integerateUnderHeadFontEffect;
	}

	public void setIntegerateUnderHeadFontEffect(String integerateUnderHeadFontEffect) {
		this.integerateUnderHeadFontEffect = integerateUnderHeadFontEffect;
	}

	public String getIntegerateUnderHeadFontItalic() {
		return integerateUnderHeadFontItalic;
	}

	public void setIntegerateUnderHeadFontItalic(String integerateUnderHeadFontItalic) {
		this.integerateUnderHeadFontItalic = integerateUnderHeadFontItalic;
	}

	public String getIntegerateUnderHeadFontName() {
		return integerateUnderHeadFontName;
	}

	public void setIntegerateUnderHeadFontName(String integerateUnderHeadFontName) {
		this.integerateUnderHeadFontName = integerateUnderHeadFontName;
	}

	public String getIntegerateUnderHeadFontSize() {
		return integerateUnderHeadFontSize;
	}

	public void setIntegerateUnderHeadFontSize(String integerateUnderHeadFontSize) {
		this.integerateUnderHeadFontSize = integerateUnderHeadFontSize;
	}

	public String getIntegerateUnderHeadFontStrike() {
		return integerateUnderHeadFontStrike;
	}

	public void setIntegerateUnderHeadFontStrike(String integerateUnderHeadFontStrike) {
		this.integerateUnderHeadFontStrike = integerateUnderHeadFontStrike;
	}

	public String getIntegerateUnderHeadFontUnderLine() {
		return integerateUnderHeadFontUnderLine;
	}

	public void setIntegerateUnderHeadFontUnderLine(String integerateUnderHeadFontUnderLine) {
		this.integerateUnderHeadFontUnderLine = integerateUnderHeadFontUnderLine;
	}

	public String getIntegerateUnderHead_frw() {
		return integerateUnderHead_frw;
	}

	public void setIntegerateUnderHead_frw(String integerateUnderHead_frw) {
		this.integerateUnderHead_frw = integerateUnderHead_frw;
	}

	public String getIntegerateBodyFontUnderLine() {
		return integerateBodyFontUnderLine;
	}

	public void setIntegerateBodyFontUnderLine(String integerateBodyFontUnderLine) {
		this.integerateBodyFontUnderLine = integerateBodyFontUnderLine;
	}

	public float getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(float rowHeight) {
		this.rowHeight = rowHeight;
	}

	public float getTitleHeight() {
		return titleHeight;
	}

	public void setTitleHeight(float titleHeight) {
		this.titleHeight = titleHeight;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public PdfWriter getWriter() {
		return writer;
	}

	public void setWriter(PdfWriter writer) {
		this.writer = writer;
	}

	public int[] getItemGridArea() {
		return itemGridArea;
	}

	public void setItemGridArea(int[] itemGridArea) {
		this.itemGridArea = itemGridArea;
	}

	public boolean isZflg() {
		return zflg;
	}

	public void setZflg(boolean zflg) {
		this.zflg = zflg;
	}

	public int getIntegrate_left_pyl() {
		return integrate_left_pyl;
	}

	public void setIntegrate_left_pyl(int integrate_left_pyl) {
		this.integrate_left_pyl = integrate_left_pyl;
	}

	public int getIntegrate_top_pyl() {
		return integrate_top_pyl;
	}

	public void setIntegrate_top_pyl(int integrate_top_pyl) {
		this.integrate_top_pyl = integrate_top_pyl;
	}

	public ArrayList getPageList() {
		return pageList;
	}

	public void setPageList(ArrayList pageList) {
		this.pageList = pageList;
	}

}
