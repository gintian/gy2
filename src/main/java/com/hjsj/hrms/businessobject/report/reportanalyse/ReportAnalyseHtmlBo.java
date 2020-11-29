package com.hjsj.hrms.businessobject.report.reportanalyse;

import com.hjsj.hrms.businessobject.report.ReportResultBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hjsj.hrms.businessobject.ykcard.MadeCardCellLine;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ReportAnalyseHtmlBo {
	private Connection conn = null;

	private ReportResultBo reportResultBo = null;

	private float percent = 0.26f;
	private String weekid="";
	private UserView userView;
	private int minTop_px =0;
	private int a_left=0;
	/** 控制生成报表的上间距 */
	private int toolBarRows = 2;//=1(表示有一行按钮),30px  =2（表示有两行按钮）,60px

	public ReportAnalyseHtmlBo(Connection conn) {
		this.conn = conn;
		this.reportResultBo = new ReportResultBo(conn);
	}

	/**
	 * 生成报表分析html编辑页面
	 * 
	 * @param unitcode
	 * @return
	 */
	public String creatHtmlView(String unitcode, String tabid, String yearid,
			String countid, TnameBo tnameBo,String reportType) {
		
		StringBuffer html = new StringBuffer("");
		// l,t,w,h
		//liuy 2014-8-29 begin
		int toolBarHeight = 60;
		if(this.toolBarRows==2){//判断tooBarRows的值，根据tooBarRows的值设置上间距
			toolBarHeight = 75; 
			if (userView!=null) {
				if("hcm".equals(userView.getBosflag())){
					toolBarHeight=75;
				}
			}
		}else{
			toolBarHeight = 30;
			if (userView!=null) {
				if("hcm".equals(userView.getBosflag())){
					toolBarHeight=40;
				}
			}
		}//liuy 2014-8-29 end
		
		minTop_px=-(tnameBo.getItemGridArea()[1]-toolBarHeight);
		a_left=-(tnameBo.getItemGridArea()[0]-10);
		String tableHeader = createTableHeader(tnameBo.getGridList(), tnameBo
				.getItemGridArea(),tnameBo);
		String tableDataArea = createData(tnameBo, tabid,
				tnameBo.getUserName(), tnameBo.getRowInfoBGrid(), tnameBo
						.getColInfoBGrid(), tnameBo.getDataArea(), tnameBo
						.getRowSerialNo(), tnameBo.getColSerialNo(), tnameBo
						.getTnameVo(), tnameBo.getRowMap(),
				tnameBo.getColMap(), unitcode, yearid, countid,reportType);
		html.append(tableHeader);
		html.append(tableDataArea);
		return html.toString();
	}

	/**
	 * @param flag
	 *            1:有边线 0：无边
	 * @param context1
	 * @param context2
	 * @param a_width
	 * @param aValign
	 * @param aAlign
	 * @return
	 */
	public String getContext(int flag, String context1, String context2,
			float a_width, String aValign, String aAlign) {
		StringBuffer tempTable = new StringBuffer("");
		tempTable.append(" <td ");
		if (flag == 1) {
            tempTable.append(" class='RecordRow_self' ");
        }
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		if (a_width > 1) {
            tempTable.append("' width='" + a_width + "' > \n ");
        } else {
            tempTable.append("' width='" + a_width * 100 + "%' > \n");
        }
		int aFontSize = 9;
		String style = getFontStyle("1", aFontSize);
		tempTable.append(" <font face='"
				+ ResourceFactory.getProperty("hmuster.label.fontSt")
				+ "' style='");
		tempTable.append(style);
		tempTable.append(";line-height:17px;' > \n ");//添加行高，防止ie11内容把边框撑大1像素   wangb 20190327
		tempTable.append(context1);
		tempTable.append("</font>&nbsp;");
		tempTable.append(context2);
		tempTable.append("</td>");
		return tempTable.toString();
	}

	/**
	 * 生成表头和项目栏
	 * 
	 * @param gridList
	 *            单元格集合
	 * @param itemGridNo
	 *            项目单元格id号
	 * @return
	 */
	public String createTableHeader(ArrayList gridList, int[] itemGridArea,TnameBo tnameBo) {//xgq -增加参数TnameBo
		StringBuffer htmlHeader = new StringBuffer("");
		MadeCardCellLine madeCardCellLine = new MadeCardCellLine();
		RecordVo vo2=null;
		HashMap rowBgridMap=new HashMap();
		HashMap colBgridMap=new HashMap();
		for(int i=0;i<tnameBo.getRowInfoBGrid().size();i++)
		{
			vo2=(RecordVo)tnameBo.getRowInfoBGrid().get(i);
			rowBgridMap.put(vo2.getString("gridno"),String.valueOf(i));				
		}
		for(int i=0;i<tnameBo.getColInfoBGrid().size();i++)
		{
			vo2=(RecordVo)tnameBo.getColInfoBGrid().get(i);
			colBgridMap.put(vo2.getString("gridno"),String.valueOf(i));
		}

		for (Iterator t = gridList.iterator(); t.hasNext();) {
			RecordVo vo = (RecordVo) t.next();
		//	vo.setInt("rtop", vo.getInt("rtop") + minTop_px); // 集体上移
			String context = "&nbsp;";
			// 处理虚线 L,T,R,B,
			String style_name = madeCardCellLine.GetCardCellLineShowcss(String
					.valueOf(vo.getInt("l")), String.valueOf(vo.getInt("r")),
					String.valueOf(vo.getInt("t")), String.valueOf(vo
							.getInt("b")));
			if (vo.getString("hz") != null
					&& vo.getString("hz").indexOf("`") != -1) {
				context = vo.getString("hz").replaceAll("`", "");
			}
			if (vo.getInt("flag") != 3) {
				autoEditBorder(itemGridArea, vo); // 自动修改单元格的边线位置，使其不会出现重叠效果
				htmlHeader.append(executeAbsoluteTable2(vo.getInt("align"), vo
						.getString("fontname"), String.valueOf(vo
						.getInt("fontsize") + 1), String.valueOf(vo
						.getInt("fonteffect")), "1", String.valueOf(vo
						.getInt("rtop")), String.valueOf(vo.getInt("rleft")),
						String.valueOf(vo.getInt("rwidth")), String.valueOf(vo
								.getInt("rheight")), context, style_name,vo,rowBgridMap,colBgridMap));
			}
		}
		return htmlHeader.toString();
	}

	/**
	 * 将计算公式中左表达式中涉及到的号码映射成实际得数据结果集中的下标值
	 * 
	 * @param list
	 * @param map
	 * @return
	 */
	public String transformDeedIndex(HashSet set, HashMap map) {
		StringBuffer lexpr = new StringBuffer("");
		for (Iterator t = set.iterator(); t.hasNext();) {
			String temp = (String) t.next();
			String num = (String) map.get(temp);
			if (num != null) {
                lexpr.append("," + num);
            }
		}
		return lexpr.toString();
	}

	public String transformDeedIndex(HashSet set) {
		StringBuffer lexpr = new StringBuffer("");
		for (Iterator t = set.iterator(); t.hasNext();) {
			String temp = (String) t.next();
			lexpr.append("," + temp);
		}
		return lexpr.toString();
	}

	/**
	 * 计算单元格是否在计算公式左表达式范围内，如果在，则设置单元格颜色
	 * 
	 * @param i
	 *            行号
	 * @param j
	 *            列号
	 * @param rowDeedIndex
	 * @param colDeedIndex
	 * @return
	 */
	public String getBackColor(int i, int j, String rowDeedIndex,
			String colDeedIndex) {
		String color = "";
		if (rowDeedIndex.indexOf("," + i) != -1) {
            color = "#7ED6AC";
        } else {
			if (colDeedIndex.indexOf("," + j) != -1) {
                color = "#7ED6AC";
            }
		}
		return color;
	}

	/**
	 * 判断该单元格是否需要自动计算
	 * 
	 * @return
	 */
	public boolean getAccount(int i, int j, String rowChangeIndex,
			String colChangeIndex) {
		boolean flag = false;
		if (rowChangeIndex.indexOf("," + i) != -1) {
            flag = true;
        } else {
			if (colChangeIndex.indexOf("," + j) != -1) {
                flag = true;
            }
		}
		return flag;
	}

	/**
	 * get数据区html
	 * 
	 * @param rowInfoBGrid
	 * @param colInfoBGrid
	 * @param itemGridvo
	 * @param status
	 * @param minTop_px
	 * @param dataArea
	 * @param rowSerialNo
	 *            横表栏序号所在位置
	 * @param colSerialNo
	 *            纵表栏序号所在位置
	 * @param reportType =1，一般 =2，年 =3，半年 =4，季报 =5，月报 =6,周报  8:年汇总
	 * @return
	 */
	public String createData(TnameBo tnameBo, String tabid, String userName,
			ArrayList rowInfoBGrid, ArrayList colInfoBGrid, RecordVo dataArea,
			String rowSerialNo, String colSerialNo, RecordVo tnameVo,
			HashMap rowMap, HashMap colMap, String unitcode, String yearid,
			String countid,String reportType) {
		StringBuffer html = new StringBuffer("");
		try {
			ArrayList resultList = new ArrayList();

			TnameExtendBo tnameExtendBo = new TnameExtendBo(this.conn);
			tnameExtendBo.execute_Ta_table(tabid, tnameBo);
			if("6".equals(reportType)) {
                tnameExtendBo.setWeekid(this.weekid);
            }
			resultList = tnameExtendBo.getReportAnalyseResult(unitcode, yearid,
					countid, tabid, tnameBo,reportType);
			String fontName = tnameVo.getString("fontname");
			int fontSize = tnameVo.getInt("fontsize");
			int fontStyle = tnameVo.getInt("fonteffect");
			int colNum = 0;
			int rowNum = 0;
			if (resultList.size() > 0) {
			//	for (int i = 0; i < resultList.size(); i++) {
				for (int i = 0; i < colInfoBGrid.size(); i++) {
					rowNum = 0;
					String[] rowInfo =null;
					if(resultList.size()>=(i+1)) {
                        rowInfo=(String[]) resultList.get(i);
                    }
					RecordVo colVo = (RecordVo) colInfoBGrid.get(i);
					if (colVo.getInt("flag1") != 4) {
                        colNum++;
                    }

				//	for (int j = 0; j < rowInfo.length; j++) {
					for (int j = 0; j < rowInfoBGrid.size(); j++) {
						String context = "";
						int flag = 0;
						RecordVo rowVo = (RecordVo) rowInfoBGrid.get(j);
						int r = rowVo.getInt("r");
						int npercent = rowVo.getInt("npercent") >= colVo
								.getInt("npercent") ? rowVo.getInt("npercent")
								: colVo.getInt("npercent");
						String top = String.valueOf(colVo.getInt("rtop"));
						String left = String.valueOf(rowVo.getInt("rleft"));
						String width = String.valueOf(rowVo.getInt("rwidth"));
						String height = String.valueOf(colVo.getInt("rheight"));
						if (rowVo.getInt("flag1") != 4) {
                            rowNum++;
                        }
						if (colVo.getInt("flag1") == 4
								&& rowVo.getInt("flag1") == 4) {
							context = "";
						} else if (colVo.getInt("flag1") == 4) {
							context = String.valueOf(rowNum);
						} else if (rowVo.getInt("flag1") == 4) {
							context = String.valueOf(colNum);
						} else {
							if (rowInfo==null||Float.parseFloat(rowInfo[j]) == 0) {
                                context = "";
                            } else {
                                context = PubFunc.round(rowInfo[j], npercent);
                            }

							flag = 1;
						}
						
						if("8".equals(reportType)) {
                            flag=0;
                        }
						html.append(executeAbsoluteTable_data(yearid,countid,flag, top, left,
								width, height, context, i, j, fontSize,
								fontStyle, fontName, npercent, unitcode, r));
					}
				}
			} else {
				for (int i = 0; i < colInfoBGrid.size(); i++) {
					rowNum = 0;
					RecordVo colVo = (RecordVo) colInfoBGrid.get(i);
					if (colVo.getInt("flag1") != 4) {
                        colNum++;
                    }

					for (int j = 0; j < rowInfoBGrid.size(); j++) {
						String context = "";
						int flag = 0;
						RecordVo rowVo = (RecordVo) rowInfoBGrid.get(j);
						int r = rowVo.getInt("r");
						int npercent = rowVo.getInt("npercent") >= colVo
								.getInt("npercent") ? rowVo.getInt("npercent")
								: colVo.getInt("npercent");
						String top = String.valueOf(colVo.getInt("rtop"));
						String left = String.valueOf(rowVo.getInt("rleft"));
						String width = String.valueOf(rowVo.getInt("rwidth"));
						String height = String.valueOf(colVo.getInt("rheight"));
						if (rowVo.getInt("flag1") != 4) {
                            rowNum++;
                        }
						if (colVo.getInt("flag1") == 4
								&& rowVo.getInt("flag1") == 4) {
							context = "";
						} else if (colVo.getInt("flag1") == 4) {
							context = String.valueOf(rowNum);
						} else if (rowVo.getInt("flag1") == 4) {
							context = String.valueOf(colNum);
						} else {
							flag = 1;
						}
						
						if("8".equals(reportType)) {
                            flag=0;
                        }
						html.append(executeAbsoluteTable_data(yearid,countid,flag, top, left,
								width, height, context, i, j, fontSize,
								fontStyle, fontName, npercent, unitcode, r));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html.toString();
	}

	/**
	 * 自动修改单元格的边线位置，使其不会出现重叠效果
	 * 
	 * @param itemVo
	 *            项目单元格对象
	 * @param vo
	 *            其他单元格对象
	 */
	public void autoEditBorder(int[] itemGridArea, RecordVo vo) {

		if ((vo.getInt("rleft") + vo.getInt("rwidth")) <= (itemGridArea[0] + itemGridArea[2])
				&& (vo.getInt("rtop") + vo.getInt("rheight")) <= (itemGridArea[1] + itemGridArea[3]))
		{
			vo.setInt("rleft",vo.getInt("rleft")+a_left);
			vo.setInt("rtop",vo.getInt("rtop")+minTop_px);
			return;
		}
		else {
			if (vo.getInt("flag") == 1) // 横表栏
			{
				if (vo.getInt("rtop") != itemGridArea[1]) {
					vo.setInt("rtop", vo.getInt("rtop") - 1);
					vo.setInt("rheight", vo.getInt("rheight") + 1);
				}
				vo.setInt("rleft", vo.getInt("rleft") - 1);
				vo.setInt("rwidth", vo.getInt("rwidth") + 1);
			} else // 纵表栏
			{
				if (vo.getInt("rleft") != itemGridArea[0]) {
					vo.setInt("rleft", vo.getInt("rleft") - 1);
					vo.setInt("rwidth", vo.getInt("rwidth") + 1);
				}
				vo.setInt("rtop", vo.getInt("rtop") - 1);
				vo.setInt("rheight", vo.getInt("rheight") + 1);
			}
			vo.setInt("rleft", vo.getInt("rleft")+a_left);
			vo.setInt("rtop",vo.getInt("rtop")+minTop_px);
		}
	}

	/*
	 * 处理页面显示虚线
	 */
	public String getStyleName(RecordVo vo) {
		// 处理虚线 L,T,R,B,
		String style_name = "RecordRow_self";
		if (vo.getInt("l") == 0) {
			style_name = "RecordRow_self_l";
			if (vo.getInt("r") == 0) {
                style_name = "RecordRow_self_two";
            }
		} else if (vo.getInt("t") == 0) {
			style_name = "RecordRow_self_t";
		} else if (vo.getInt("r") == 0) {
			style_name = "RecordRow_self_r";
		} else if (vo.getInt("b") == 0) {
			style_name = "RecordRow_self_b";
		}
		return style_name;
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
			String style_name) {

		StringBuffer tempTable = new StringBuffer("");
		String[] temp = transAlign(Align);
		String aValign = temp[0];
		String aAlign = temp[1];
		tempTable.append(" <table   border='" + border
				+ "' cellspacing='0'  align='center' cellpadding='1'");
		if (type == 1) {
            tempTable.append(" class='ListTable' ");
        }
		tempTable.append(" style='position:absolute;top:");
		tempTable.append(top);
		tempTable.append(";left:");
		tempTable.append(left);
		tempTable.append(";width:");
		tempTable.append(width);
		tempTable.append(";height:");
		tempTable.append(height);
		tempTable.append("'> \n ");
		tempTable.append(" <tr valign='middle' align='center'> \n ");
		tempTable.append(" <td ");
		if (type == 1) {
			tempTable.append(" class='" + style_name + "' ");
		}
		tempTable.append(" valign='");
		tempTable.append(aValign);
		tempTable.append("' align='");
		tempTable.append(aAlign);
		tempTable.append("'> \n ");
		int aFontSize = 0;
		aFontSize = Integer.parseInt(fontSize) - 1;
		String style = getFontStyle(fontStyle, aFontSize);
		tempTable.append(" <font face='");
		tempTable.append(fontName);
		tempTable.append("' style='");
		tempTable.append(style);
		tempTable.append(";line-height:17px;' > \n ");//添加行高，防止ie11内容把边框撑大1像素   wangb 20190327
		tempTable.append(context);
		tempTable.append("</font></td></tr></table> \n ");

		return tempTable.toString();
	}

	/**
	 * 生成绝对定位的table(每个table表示一个单元格,主要针对表头对象,点击底层表头，选中相应的行列数据)
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
	public String executeAbsoluteTable2(int Align, String fontName,
			String fontSize, String fontStyle, String border, String top,
			String left, String width, String height, String context,
			String style_name,RecordVo vo,HashMap rowBgridMap,HashMap colBgridMap) {//xgq -2010 01 12增参数 vo, rowBgridMap, colBgridMap

		StringBuffer tempTable = new StringBuffer("");
		String[] temp = transAlign(Align);
		String aValign = temp[0];
		String aAlign = temp[1];
		
		String div_id=style_name.replaceAll("RecordRow_self", "headerDiv");
		div_id="headerDiv";
		if(Integer.parseInt(height)<19)
		{
			tempTable.append(" <div id='"+div_id+"' ");		
			tempTable.append(" align='");
			tempTable.append(aAlign+"' ");
		}
		else
		{
			tempTable.append(" <table   border='" + border
					+ "' cellspacing='0'  align='center' cellpadding='1'");
			tempTable.append(" class='ListTable' ");
		}
		
		StringBuffer a_style = new StringBuffer("");
		/* 报表分析报表浏览 样式调整 xiaoyun 2014-6-28 start */
		a_style.append(" style='margin-left:-6px;table-layout:fixed;position:absolute;top:");
		//a_style.append(" style='table-layout:fixed;position:absolute;top:");
		/* 报表分析报表浏览 样式调整 xiaoyun 2014-6-28 end */
		if(Integer.parseInt(height)<19)
		{
			a_style.append(Integer.parseInt(top));
		}
		else {
            a_style.append(top);
        }
		a_style.append(";left:");
		a_style.append(left);
		a_style.append(";width:");
		a_style.append(width);
		a_style.append(";height:");
		a_style.append(height);
		
		
		if("1".equals(vo.getString("flag")))	//行
		{
			
			if(vo.getInt("flag1")!=4&&rowBgridMap.get(vo.getString("gridno"))!=null)
			{
					a_style.append(";cursor:hand");	
					tempTable.append(" onclick=\"selectRowOrColumn('a"+(String)rowBgridMap.get(vo.getString("gridno"))+"')\" ");
					tempTable.append(" onDblClick=\"clearSelected('a"+(String)rowBgridMap.get(vo.getString("gridno"))+"')\" ");
			}
		}
		else if("2".equals(vo.getString("flag")))//列
		{
			if(vo.getInt("flag1")!=4&&colBgridMap.get(vo.getString("gridno"))!=null)
			{
				a_style.append(";cursor:hand");	
				tempTable.append(" onclick=\"selectRowOrColumn('b"+(String)colBgridMap.get(vo.getString("gridno"))+"')\" ");
				tempTable.append(" onDblClick=\"clearSelected('b"+(String)colBgridMap.get(vo.getString("gridno"))+"')\" ");
			}
		}
		tempTable.append(a_style + "'");
		
		if(Integer.parseInt(height)<19)
		{
			tempTable.append("  > \n ");
		}
		else
		{
			tempTable.append("  > \n ");
			tempTable.append(" <tr valign='middle' align='center'> \n ");
			tempTable.append(" <td ");
			tempTable.append(" class='" + style_name + "' ");
			tempTable.append(" valign='");
			tempTable.append(aValign);
			tempTable.append("' align='");
			tempTable.append(aAlign);
			tempTable.append("' ");
			
			tempTable.append(" width='100%'");
			tempTable.append(" height='100%'");
			
			tempTable.append(" > \n ");
		}
		int aFontSize = 0;
		aFontSize = Integer.parseInt(fontSize);
		
	/*	
		String style = getFontStyle(fontStyle, aFontSize);
		tempTable.append(" <font face='");
		tempTable.append(fontName);
		tempTable.append("' style='");
		tempTable.append(style);
		tempTable.append("' > \n ");
		tempTable.append(context);
		tempTable.append("</font>");
		*/

		
		tempTable.append(" <font face='");
		tempTable.append(fontName);
		tempTable.append("' style='font-size:");
		tempTable.append(aFontSize);
		tempTable.append("pt;line-height:17px;' > \n");//添加行高，防止ie11内容把边框撑大1像素   wangb 20190327
		tempTable.append(getFontStyle2(1,fontStyle)); 
		
		
		tempTable.append(context);	
		tempTable.append(getFontStyle2(0,fontStyle));
		tempTable.append("</font>");
		
		
		
		
		
		if(Integer.parseInt(height)<19)
		{
			tempTable.append("</div>");
		}
		else {
            tempTable.append("</td></tr></table> \n ");
        }

		return tempTable.toString();
	}
	
	private String getFontStyle2(int flag,String fontStyle)
	{
		String style="";
		if(flag==1)
		{
			if("2".equals(fontStyle)) {
                style="<b>";
            } else if(fontStyle.endsWith("3")) {
                style="<i>";
            } else if("4".equals(fontStyle)) {
                style="<b><i>";
            }
		}
		else
		{
			if("2".equals(fontStyle)) {
                style="</b>";
            } else if(fontStyle.endsWith("3")) {
                style="</i>";
            } else if("4".equals(fontStyle)) {
                style="</b></i>";
            }
			
		}
		return style;
		
	}

	/**
	 * 生成绝对定位的table(每个table表示一个单元格)
	 * 
	 * @param flag
	 *            1:包含编辑框 0：只显示
	 * @param top
	 * @param left
	 * @param width
	 * @param height
	 * @param context
	 *            值
	 * @param i
	 * @param j
	 * @param status
	 *            报表状态
	 * @param color
	 *            单元格背景色
	 * @param isAutoAccount
	 *            是否需要自动计算
	 * @param operateObject
	 *            1:操作未上报的表 2：操作已上报的表
	 * @param r
	 *            是否有左边线
	 * @return
	 */
	public String executeAbsoluteTable_data(String yearid,String countid,int flag, String top, String left,
			String width, String height, String context, int i, int j,
			int fontSize, int fontStyle, String fontName, int npercent,
			String unitcode, int r) {

		StringBuffer tempTable = new StringBuffer("");
		tempTable
				.append(" <table   border='1' cellspacing='0'  align='center' cellpadding='1'");
		tempTable.append(" class='ListTable' ");
		/* 报表分析-报表浏览 样式调整 xiaoyun 2014-6-28 start */
		//tempTable.append(" style='table-layout:fixed;position:absolute;top:");
		tempTable.append(" style='margin-left:-6px;table-layout:fixed;position:absolute;top:");
		/* 报表分析-报表浏览 样式调整 xiaoyun 2014-6-28 end */
		tempTable.append(top);
		tempTable.append(";left:");
		tempTable.append(left);
		tempTable.append(";width:");
		tempTable.append(width);
		tempTable.append(";height:");
		tempTable.append(height);
		tempTable.append("'> \n ");
		tempTable.append(" <tr valign='middle' align='center'> \n ");
		tempTable.append(" <td class='");
		if (r == 1) {
            tempTable.append("RecordRow_self");
        } else {
            tempTable.append("RecordRow_self_r");
        }
		tempTable.append("'  id='aa" + i + "_" + j + "'");
		tempTable.append(" width=");
//		tempTable.append(width);
		tempTable.append("100%");
		tempTable.append(" height=");
//		tempTable.append(height);
		tempTable.append("100%");
		
		tempTable.append("  align='center' > \n ");
		if (flag == 1) {
			tempTable.append("<input type='text' name='a" + i + "_" + j
					+ "' value='");
			tempTable.append(context);
			tempTable.append("'");
			tempTable.append(" readOnly ");
			 // 用于反查 xgq 2010 03 12
				 tempTable.append("  _document_oncontextmenu=\"setReverseID('a"+i+"_"+j+"')\"  ");
			tempTable.append(" class='TEXT_NB' style='height: 15px; width: ");
			tempTable.append((Integer.parseInt(width) * 0.8));
			tempTable
					.append("px;font-size:"+fontSize+"pt;text-align= right' ");
	    	if(!"0".equals(yearid)&&!"0".equals(countid)) {
                tempTable.append(" onClick='changeGrid("+ i + "," + j + ");'");
            }
			tempTable
					.append("   onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'   />");
		} else {
			int aFontSize = fontSize - 1;
			String style = getFontStyle(String.valueOf(fontStyle), aFontSize);
			tempTable.append(" <font face='");
			tempTable.append(fontName);
			tempTable.append("' style='");
			tempTable.append(style);
			tempTable.append(";line-height:17px;' > \n ");//添加行高，防止ie11内容把边框撑大1像素   wangb 20190327
			tempTable.append(context);
			tempTable.append("</font>");

			tempTable.append("<input type='hidden' name='a" + i + "_" + j
					+ "' value='0' ");
		}
		tempTable.append("</td></tr></table> \n ");
		return tempTable.toString();
	}

	/**
	 * 转换成字符样式
	 */
	private String getFontStyle(String fontStyle, int aFontSize) {
		String style = "";
		if ("2".equals(fontStyle)) {
            style = "font-weight：bold;font-size:" + aFontSize + "pt";
        } else if (fontStyle.endsWith("3")) {
            style = "font-style：italic;font-size:" + aFontSize + "pt";
        } else if ("4".equals(fontStyle)) {
            style = "font-style：italic;font-weight:bold;font-size：" + aFontSize
                    + "pt";
        } else {
            style = "font-weight:normal;font-size:" + aFontSize + "pt";
        }
		return style;
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

	/**
	 * 生成绝对定位的背景页面
	 * 
	 * @param top、left、width、height
	 *            表格的绝对位置
	 */
	public String executeAbsoluteBackground(int top, int left, float width,
			float height) {
		StringBuffer tempHtml = new StringBuffer("");

		tempHtml.append("<div id=idDIV ");
		tempHtml.append(" style='position:absolute;top:");
		tempHtml.append(top);
		tempHtml.append(";left:");
		tempHtml.append(left);
		tempHtml.append(";width:");
		tempHtml.append(width);
		tempHtml.append(";height:");
		tempHtml.append(height);
		tempHtml
				.append(";border:thin outset buttonface;background : #ffffff ;'> \n ");
		tempHtml.append("&nbsp;</div>");

		tempHtml.append("<div ");
		tempHtml.append(" style='position:absolute;top:");
		tempHtml.append(top);
		tempHtml.append(";left:");
		tempHtml.append(left + width);
		tempHtml.append(";width:");
		tempHtml.append(20);
		tempHtml.append(";height:");
		tempHtml.append(2);
		tempHtml.append(";'> \n ");
		tempHtml.append("&nbsp;</div>");
		return tempHtml.toString();
	}

	
	
	
	
	
	/**
	 * 根据报表结果表里的坐标得到相对应得 列名 和 行名
	 * @param i 横坐标
	 * @param j 纵坐标
	 * @param tabid 表id
	 * @return  [0] ta_XXX 列名  [1] ta_XXX 横别名  [2]tname 列名称 [3]tname 行名称
	 */
	public ArrayList getGridInfoList(int i,int j,String tabid,TnameBo tnameBo)
	{
		ArrayList list=new ArrayList();
		String tnameColName="";           //列名称
		String tnameRowName="";			  //行名称
		String col_archiveName="C"+(i+1);
		String row_archiveName=String.valueOf(j+1);
		
		if(tnameBo.getColInfoBGrid().size()>j&&tnameBo.getRowInfoBGrid().size()>i)
		{
			RecordVo rowGridVo=(RecordVo)tnameBo.getColInfoBGrid().get(j);
			RecordVo colGridVo=(RecordVo)tnameBo.getRowInfoBGrid().get(i);
			tnameColName=colGridVo.getString("hz");
			tnameRowName=rowGridVo.getString("hz");
			if(colGridVo.getString("archive_item")!=null&&!"".equals(colGridVo.getString("archive_item"))) {
                col_archiveName=colGridVo.getString("archive_item");
            }
			if(rowGridVo.getString("archive_item")!=null&&!"".equals(rowGridVo.getString("archive_item"))) {
                row_archiveName=rowGridVo.getString("archive_item");
            }
			list.add(col_archiveName);
			list.add(row_archiveName);
			list.add(tnameColName);
			list.add(tnameRowName);
		}
		return list;
	}
	
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			Connection con = DriverManager
					.getConnection(
							"jdbc:microsoft:sqlserver://127.0.0.1:1433;DatabaseName=ykchr2;SelectMethod=Cursor",
							"sa", "");
			
			TnameBo nameBo=new TnameBo(con,"1");
			ReportAnalyseHtmlBo htmlBo=new ReportAnalyseHtmlBo(con);
			String html=htmlBo.creatHtmlView("ga","1","1998",
					"1",nameBo,"1");
		//	System.out.println(html);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getWeekid() {
		return weekid;
	}

	public void setWeekid(String weekid) {
		this.weekid = weekid;
	}

	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public void setToolBarRows(int toolBarRows) {
		this.toolBarRows = toolBarRows;
	}

}
