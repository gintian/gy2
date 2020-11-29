package com.hjsj.hrms.module.questionnaire.analysis.transaction;

import com.hjsj.hrms.module.questionnaire.analysis.businessobject.AnalysisBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.jxcell.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ExportAnalysisDataTrans extends IBusiness {

	/**
	 * changxy 导出问卷分析 20160621
	 */
	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		try {
			Object data = this.getFormHM().get("data");
			String name = createExcel(data);
			//xus 20/3/2 vfs改造
//			name = SafeCode.encode(PubFunc.encryption(name));
			name = PubFunc.encrypt(name);
			this.getFormHM().put("filename", name);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String createExcel(Object data) {
		String outname = this.userView.getUserName() +"_question" +  ".xls";
		View view = new View();
		view.getLock();

		try {

			// 设置颜色
			view.setPaletteEntry(1, new Color(230, 230, 230));// 浅灰色
			view.setPaletteEntry(2, new Color(217, 217, 217));// 深灰色

			// 表头样式
			// view.setDefaultRowHeight(800);
			CellFormat cfTitle = view.getCellFormat();
			cfTitle.setFontSize(10);// 设置字体大小
			cfTitle.setBottomBorder((short) 1);// 设置边框为细实线
			cfTitle.setTopBorder(CellFormat.PatternSolid);
			cfTitle.setLeftBorder((short) 1);
			cfTitle.setRightBorder((short) 1);
			cfTitle.setMergeCells(true);// 合并单元格
			cfTitle.setWordWrap(true);
			// 水平对齐方式
			cfTitle.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
			// 垂直对齐方式
			cfTitle.setVerticalAlignment(CellFormat.VerticalAlignmentTop);

			// 内容样式
			CellFormat cfBody = view.getCellFormat();
			cfBody.setFontSize(10);
			cfBody.setBottomBorder(CellFormat.PatternSolid);// 设置边框为细实线
			cfBody.setTopBorder(CellFormat.PatternSolid);
			cfBody.setLeftBorder(CellFormat.PatternSolid);
			cfBody.setRightBorder(CellFormat.PatternSolid);
			cfBody.setHorizontalAlignment(CellFormat.HorizontalAlignmentLeft);// 水平居左
			cfBody.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);// 垂直居中

			// 表头样式
			CellFormat cfcolum = view.getCellFormat();
			cfcolum.setFontSize(10);
			cfcolum.setBottomBorder(CellFormat.PatternSolid);// 设置边框为细实线
			cfcolum.setTopBorder(CellFormat.PatternSolid);
			cfcolum.setLeftBorder(CellFormat.PatternSolid);
			cfcolum.setRightBorder(CellFormat.PatternSolid);
			cfcolum.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);// 水平居中
			cfcolum.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);// 垂直居中

			view.setDefaultColWidth(25 * 256);// 固定列宽
			int count = 0;
			JSONArray objs = JSONArray.fromObject(data);

			for (int i = 0; i < objs.size(); i++) {
				JSONObject obj = objs.getJSONObject(i);
				String type = obj.getString("type");
				String name = obj.getString("name");
				String itemid = obj.getString("itemid");
				String qnid = obj.getString("qnid");
				JSONArray colarr = obj.getJSONArray("column");
				JSONArray dataarr = obj.getJSONArray("data");
				JSONArray orderarr = obj.getJSONArray("order");

				for (int j = 0; j < colarr.size(); j++) {
					if (j == 0)
						view.setText(count, 0, name); // 设置标题

					cfTitle.setPattern(CellFormat.PatternSolid);
					cfTitle.setPatternFG(view.getPaletteEntry(1));// 设置添加背景色
					view.setCellFormat(cfTitle, count, 0, count,
							colarr.size() - 1); // 设置标题区域 和样式

				}

				count++;

				if (!"3".equals(type)) {

					for (int j = 0; j < colarr.size(); j++) {
						view.setColWidth(j, 20 * 256);

						if (j == 0 && "&nbsp;".equals(colarr.getString(j))) {
							view.setText(count, j, "");
						} else {
							view.setText(count, j, colarr.getString(j));// 设置colum列头
																		// 内容
						}
						cfcolum.setPattern(CellFormat.PatternSolid);
						cfcolum.setPatternFG(view.getPaletteEntry(2));
						view.setCellFormat(cfcolum, count, 0, count, j);// 设置colum列头样式
					}
					count++;
				}

				if ("3".equals(type) || "4".equals(type)) {
					AnalysisBo bo = new AnalysisBo();
					String codeset = "";
					HashMap<String, ArrayList<String>> map = getItemData(qnid,
							itemid, type, orderarr);
					int index = 0;
					for (int j = 0; j < orderarr.size(); j++) {// orderarr.size()
																// 设置填空题的位置
						ArrayList<String> list = null;
						codeset = bo.getItemCodeSet(qnid, itemid,
								new ContentDAO(this.frameconn));
						list = map.get(orderarr.getString(j));
						if(list!=null){//防止填空题内容为空时取内容报错  
							
							if ("3".equals(type)) { // 单项填空题题型 设置三列 自上而下 自左至右 依次
								int flag = 0;
								// 设置标题行合并单元格
								cfTitle.setPattern(CellFormat.PatternSolid);
								cfTitle.setPatternFG(view.getPaletteEntry(1));// 设置添加背景色
								view.setCellFormat(cfTitle, count - 1, 0,
										count - 1, list.size() > 3 ? 2 : list
												.size() - 1); // 设置标题区域 和样式
								
								for (int k = 0; k < list.size(); k++) {
									if (index < k)
										index = k;
									if (k % 3 == 0) // 每次塞到第三列 调到下一行
										flag++;
									if (codeset != null && !"".equals(codeset))
										view.setText(count + flag - 1, k % 3,
												AdminCode.getCodeName(codeset, list
														.get(k)));
									else
										view.setText(count + flag - 1, k % 3, list
												.get(k));
									cfBody.setWordWrap(true);
									view.setCellFormat(cfBody, count, 0, count
											+ flag - 1, k % 3);
								}
								index = (int) Math.ceil(index / 3); // 向上取整计算所占行数
							} else {
								for (int k = 0; k < list.size(); k++) {
									if (index < k)
										index = k;
									if (codeset != null && !"".equals(codeset))
										view.setText(count + k, j, AdminCode
												.getCodeName(codeset, list.get(k)));
									else
										view.setText(count + k, j, list.get(k));
									cfBody.setWordWrap(true);
									view.setCellFormat(cfBody, count, 0, count + k,
											j);
								}
							}
						}
					}
					count += index;
					count += 5;

				} else {
					// 导出data内容
					int flag = count;
					for (int j = 0; j < dataarr.size(); j++) {
						JSONObject arr = dataarr.getJSONObject(j);

						int index = 0;
						for (int k = 0; k < orderarr.size(); k++) {
							String str = orderarr.getString(k);
							if (str == null || "".equals(str))
								str = "dataname";
							// view.setText(count, k, arr.getString(str));
							Pattern pattern = Pattern
									.compile("-?[0-9]*.?[0-9]*");// 验证字符串是否是数字小数负数
							java.util.regex.Matcher macher = pattern
									.matcher(arr.getString(str));

							if (macher.matches()) {
								if (k != 0)
									view.setNumber(count, k, Double
											.parseDouble(arr.getString(str)));
								else if ("12".equals(type)) {// 打分题设置第一列为数值型
									view.setNumber(count, k, Double
											.parseDouble(arr.getString(str)));
								} else
									view.setText(count, k, arr.getString(str)); // 第一列的内容全部为文本型
							} else {
								view.setText(count, k, arr.getString(str));
							}
							cfBody.setWordWrap(true); // 自动换行
							// cfBody.setCustomFormat("0");
							view.setCellFormat(cfBody, count, 0, count, index);
							index++;
						}
						count++;
					}
					/***********************************************************
					 * 设置图表
					 */
					/*
					 * int width=view.getDefaultColWidth(); int
					 * height=view.getDefaultRowHeight(); int
					 * heightCount=(count-flag+6)*height;//总高度
					 * 
					 * double widthCount=(5*heightCount)/7;
					 * 
					 *//**
						 * x1 -第一个锚点坐标的对象;以列从工作表的左边缘。 y1 -第一个锚点坐标;以行从顶部边缘的工作表。
						 * x2 -第二锚点坐标;以列从工作表的左边缘。 y2 -第二锚点坐标;以行从顶部边缘的工作表。
						 */
					ChartShape chartshape = null;
					String chartType = (obj.getString("chartType") == null || "".equals(obj
							.getString("chartType"))) ? "" : obj
							.getString("chartType");
					if ("line".equals(chartType)) {// 点线图
						// ChartShape chartshape=view.addChart(
						// colarr.size()+1,flag-2 , 2*colarr.size()+1, count+4);
						if (colarr.size() <= 2)// 单选/多选 每增加三行数据 增加一列
							chartshape = view.addChart(colarr.size() + 1,
									flag - 2, 2 * colarr.size() + 1
											+ dataarr.size() / 3, count + 4);
						else
							chartshape = view.addChart(colarr.size() + 1,
									flag - 2, 2 * colarr.size() + 1, count + 4);

						ChartFormat chartFormat = chartshape.getLegendFormat();
						chartFormat.setFontItalic(false);
						chartFormat.setFontSize(180);
						chartshape.setLegendFormat(chartFormat);

						chartFormat = chartshape.getAxisFormat(
								ChartShape.XAxis, 0);
						chartFormat.setFontSize(166);
						chartshape.setAxisFormat(ChartShape.XAxis, 0,
								chartFormat);

						chartFormat = chartshape.getAxisFormat(
								ChartShape.YAxis, 0);
						chartFormat.setFontItalic(false);
						chartFormat.setFontSize(166);
						chartshape.setAxisFormat(ChartShape.YAxis, 0,
								chartFormat);

						if ("15".equals(type)) {
							chartshape.initData(new RangeRef(0, flag, colarr
									.size() - 1, count - 1), true);
						} else if ("14".equals(type) || "8".equals(type)
								|| "12".equals(type) || "7".equals(type)) {// 矩阵打分题
																			// 矩阵多选题
																			// 图片题
							chartshape.initData(new RangeRef(0, flag - 1,
									colarr.size() - 1, count - 1), true);
						} else if ("2".equals(type) || "1".equals(type)
								|| "13".equals(type) || "6".equals(type)
								|| "5".equals(type)) {
							chartshape.initData(new RangeRef(0, flag - 1,
									colarr.size() - 1, count - 1), false);
						} else {
							chartshape.initData(new RangeRef(0, flag, colarr
									.size() - 1, count - 1), false);
						}
						chartshape.setTitle(name);
						chartFormat = chartshape.getTitleFormat();
						chartFormat.setFontSize(166);
						chartshape.setTitleFormat(chartFormat);
						chartshape.setChartType(ChartShape.TypeLine);
						count += 5;
					} else if ("column".equals(chartType)) {
						/*******************************************************
						 * chartFormat.setDataLabelTypes(chartFormat.DataLabelYValueMask);
						 * chartFormat.setOrientation(45);//字体倾斜
						 * chartFormat.setMarkerSize(3);设置标记大小
						 * chartFormat.setFontItalic(true);//字体大小根据图表内容改变
						 * chartshape.setTickLabelPosition(chartshape.YAxis, 0,
						 * chartshape.TickLabelPositionLow);
						 * chartFormat.setDataLabelPosition(chartFormat.DataLabelPositionAxis);
						 * chartFormat.s
						 * chartshape.setLegendPosition(chartshape.LegendPlacementBottomLeftCorner);//设置图例位置
						 */
						if ("12".equals(type)) {// 打分题
							chartshape = view.addChart(colarr.size() + 1,
									flag - 2, 2 * colarr.size() + 1, count + 6);
						} else {
							if (colarr.size() <= 2 && dataarr.size() > 4)// 数据
																			// 列占2列
																			// 数据行占超过4行
								chartshape = view.addChart(colarr.size()+1,
										flag - 2, 3*colarr.size()+1, count    //列宽设置为 2colarr.size() 防止由于占用的列窄导致统计图显示不全
												+ dataarr.size() - 1);
							else
								chartshape = view.addChart(colarr.size()+1,
										flag - 2, 3*colarr.size()+1,
										count + 4);

						}

						ChartFormat chartFormat = chartshape.getChartFormat();
						chartFormat = chartshape.getMajorGridFormat(
								ChartShape.YAxis, 0);
						chartFormat.setFontSize(166);
						chartshape.setMajorGridFormat(ChartShape.YAxis, 0,
								chartFormat);// 设置坐标轴字体大小
						chartshape.setAxisScaleReversed(ChartShape.XAxis, 0,
								true);// 将x坐标轴放置上方 逆序类别
						chartFormat = chartshape.getLegendFormat();
						chartFormat.setFontSize(180);
						chartshape.setLegendFormat(chartFormat);
						chartshape.setAxisFormat(ChartShape.XAxis, 0,
								chartFormat);
						chartshape.initData(new RangeRef(0, flag - 1, colarr
								.size() - 1, count - 1), true); // true 与false
																// 的区别
																// 设置轴标签与系列的不同

						chartshape.setTitle(name);// 设置标题
						chartFormat = chartshape.getTitleFormat();
						chartFormat.setFontSize(166);
						chartshape.setTitleFormat(chartFormat);// 设置标题样式
						chartshape.setChartType(ChartShape.TypeBar);// 条状图
						if ("12".equals(type)) {
							count += 7;
						} else {
							if (colarr.size() <= 2 && dataarr.size() > 4)
								count += dataarr.size();
							else
								count += 5;
						}
						/*
						 * chartFormat=chartshape.getLegendFormat();
						 * chartFormat.setFontBold(true);
						 * chartshape.setLegendFormat(chartFormat);//设置图例字体
						 */
						chartFormat = chartshape.getAxisFormat(
								ChartShape.XAxis, 0);
						chartFormat.setFontItalic(false);
						chartFormat.setFontSize(166);
						chartshape.setAxisFormat(ChartShape.XAxis, 0,
								chartFormat);
						// chartFormat.setFontBold(true);

					} else if ("pie".equals(chartType)) {// 饼图
						chartshape = view.addChart(colarr.size() + 1, flag - 2,
								2 * colarr.size() + 1, count + 4);
						ChartFormat chartFormat = chartshape.getLegendFormat();
						chartFormat.setFontSize(180);
						chartshape.setLegendFormat(chartFormat);

						chartFormat = chartshape.getAxisFormat(
								ChartShape.XAxis, 0);
						chartFormat.setFontItalic(false);
						chartFormat.setFontSize(166);
						chartshape.setAxisFormat(ChartShape.XAxis, 0,
								chartFormat);

						chartFormat = chartshape.getAxisFormat(
								ChartShape.YAxis, 0);
						chartFormat.setFontItalic(false);
						chartFormat.setFontSize(166);
						chartshape.setAxisFormat(ChartShape.YAxis, 0,
								chartFormat);

						chartshape.setTitle(name);
						chartFormat = chartshape.getTitleFormat();
						chartFormat.setFontItalic(false);
						chartFormat.setFontSize(166);
						chartshape.setTitleFormat(chartFormat);
						chartshape.initData(new RangeRef(0, flag - 1, colarr
								.size() - 1, count - 1), false);
						chartshape.setChartType(ChartShape.TypePie);
						count += 5;
					}

				}
			}

			view.write(System.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + outname);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			view.releaseLock();
		}

		return outname;
	}

	/**
	 * 
	 * @Title: getItemData
	 * @Description:
	 * @param qnid
	 * @param itemid
	 * @param type
	 * @return
	 * @return HashMap<String,ArrayList<String>>
	 */
	private HashMap<String, ArrayList<String>> getItemData(String qnid,
			String itemid, String type, JSONArray arr) {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		StringBuffer sql = new StringBuffer();
		ResultSet rs = null;
		try {
			if ("14".equals(type)) {
				sql.append("select optid,score from qn_matrix_" + qnid
						+ "_data where itemid = '" + itemid + "'");
			} else {
				sql.append("select dataid");
				for (Object str : arr) {
					sql.append("," + str);
				}
				sql.append(" from qn_" + qnid + "_data");
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs = dao.search(sql.toString());
			ArrayList<String> list = new ArrayList<String>();
			while (rs.next()) {
				if ("14".equals(type)) {
					String optid = rs.getString("optid");
					if (map.containsKey(optid))
						list = map.get(optid);
					list.add(rs.getString("score"));
					map.put(optid, list);
				} else {
					for (int i = 0; i < arr.size(); i++) {
						String key = arr.getString(i);
						if (map.containsKey(key))
							list = map.get(key);
						else
							list = new ArrayList<String>();
						list.add(rs.getString(key));
						map.put(key, list);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
}
