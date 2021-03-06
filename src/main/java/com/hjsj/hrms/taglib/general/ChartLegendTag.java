package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.businessobject.sys.EchartsBo;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.taglib.TagUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import com.hrms.struts.taglib.TagUtility;

/**
 * <p>
 * Title:ChartLegendTag
 * </p>
 * <p>
 * Description:统计图例展现
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 4, 2005:2:12:27 PM
 * </p>
 *
 * @author chenmengqing
 * @version 1.0
 *
 */
public class ChartLegendTag extends BodyTagSupport {

	/**
	 *
	 */
	private static final long serialVersionUID = 1231555086736724204L;

	EchartsBo chartbo = null;

	/**表单名称(即javaBean)*/
	private String name;

	/**图片标题*/
	private String title;

	/**宽度*/
	private int width = 100;

	/**高度*/
	private int height = 100;

	/**统计图显示类型*/
	private int chart_type = ChartConstants.PIE_3D;

	/**ArrayList CodeItem ,pie bar 数据集合（ArrayList / HashMap）*/
	private String legends;

	/**用户hashmap排序*/
	private ArrayList orderlist=new ArrayList();
	private String data;

	/**默认为JFreeChart*/
	private boolean anychart=false;

	/**x坐标旋转角度,为零时不旋转*/
	private int xangle=0;

	private String islabelname = "true";//islabelname为true时，饼状图显示标签描述信息，反之，则不显示

	private String orient = "horizontal";//布局方式，默认为水平布局，可选为：'horizontal' | 'vertical'

	public String getOrient() {
		return orient;
	}

	public void setOrient(String orient) {
		this.orient = orient;
	}

	public String getIslabelname() {
		return islabelname;
	}

	public void setIslabelname(String islabelname) {
		this.islabelname = islabelname;
	}

	/**anychart增加一个点击事件*/
	private String pointClick=null;

	/**参数控制类*/
	private String chartParameter = "";

	/**是否显示数据求和*/
	private String isneedsum = "true";

	/**线状图是否显示百分比*/
	private String labelIsPercent="0";

	/**统计图背景颜色*/
	private String bg_color="#F4F7F7";

	/**小数点的位数*/
	private int numDecimals=0;
	//private String xdwidth="";  //相对宽度
	//private String xdheight=""; //相对高度
	/**是否在图上显示统计项具体的值*/
	private String label_enabled="true";
	/**是否在选中统计项给出悬浮框提示*/
	private String tooltip_enabled="true";

	/**是否为总裁桌面发来的请求标识true*/
	private String biDesk;

	/**x-轴总字数*/
	private String total;

	/**chart输出面板*/
	private String chartpnl;

	/**是否透明*/
	private boolean transparent = false;


	/**状态良好值*/
	private ArrayList minvalue=new ArrayList();
	/**状态正常值*/
	private ArrayList maxvalue=new ArrayList();
	/**预警值*/
	private ArrayList valves=new ArrayList();
	/**当前值*/
	private ArrayList cvalues=new ArrayList();

	/** 配置线性图，柱状图（包括3D模式）Y轴是否自动适应 **/
	private String yAxisAuto = "false";  //="true" 则不需要设置y轴的最小值和最大值，"false"则按照 程序老的逻辑走

	public String getyAxisAuto() {
		return yAxisAuto;
	}

	public void setyAxisAuto(String yAxisAuto) {
		this.yAxisAuto = yAxisAuto;
	}

	private String notem="0";

	/**
	 *
	 */
	private boolean pieoutin=false;


	public String getNotem() {
		return notem;
	}

	public void setNotem(String notem) {
		this.notem = notem;
	}
	/**
	 * author : xiegh
	 *
	 * date : 20180426
	 *
	 * anychart图表 ——————>Echarts图表
	 *
	 * 现图表支持：平面饼状图、平面柱状图、平面折线图、分组柱状图、柱状占比图和雷达图。
	 *
	 *
	 */
	public int doStartTag() throws JspException {
		try{
			//获取当前登录人的信息
			UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);

			//获取图表数据集
			Object value = TagUtils.getInstance().lookup(pageContext, name,legends, scope);

			//如果锁版本过低 或者  图表类型为 仪表盘 温度计时，不显示图表
			if (value == null&&chart_type!=ChartConstants.Circular_Gauge&&chart_type!=ChartConstants.Vertical_Gauge)
				return 0;

			//实例化anychartbo类  初始化图表的参数
			initChartparamters();

			//创建echart图表Json
			String tmp = chartbo.createEchartBuffer(value,pointClick,biDesk,total);

			//输出Json字符串到前端渲染
			pageContext.getOut().println(tmp);

			return EVAL_BODY_BUFFERED;

		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}

	private void initChartparamters() {
		chartbo = new EchartsBo(this.title,this.chart_type,this.width,this.height,this.label_enabled);
		chartbo.setNumDecimals(this.numDecimals);
		chartbo.setBg_color(bg_color);
		chartpnl = chartpnl == null ?"":chartpnl;
		chartbo.setChartpnl(this.chartpnl);
		chartbo.setXangle(xangle);
		chartbo.setBg_color(bg_color);
		chartbo.setIsneedsum(isneedsum);
		chartbo.setTooltip_enabled(tooltip_enabled);
		chartbo.setOrient(orient);
		HashMap event=new HashMap();
		if(!(this.pointClick==null||this.pointClick.length()==0)){
			event.put("pointClick", this.pointClick);
		}

	}

	/**
	 * 根据图例类型创建相应的图例line
	 *
	 * @param hm
	 * @return
	 * @throws JspException
	 */
	private JFreeChart createLineChart(HashMap hm) throws JspException {
		JFreeChart freechart = null;
		Color color = ColorHelper.getColor(bg_color);
		ArrayList list = null;
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Iterator it = hm.entrySet().iterator();
		if(hm.size()>0){
			if (orderlist != null&&orderlist.size()>0){
				ArrayList olist=orderlist;
				for(int o=0;o<olist.size();o++)
				{
					String name=(String)olist.get(o);
					list=(ArrayList)hm.get(name);
					int j = 0;
					for (j = 0; j < list.size(); j++) {
						CommonData vo = (CommonData) list.get(j);
						dataset.addValue(Double.parseDouble(vo.getDataValue()), name,vo.getDataName());
					}
				}
			}else{
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					list = (ArrayList) entry.getValue();
					String name = (String) entry.getKey();
					int j = 0;
					for (j = 0; j < list.size(); j++) {
						CommonData vo = (CommonData) list.get(j);
						dataset.addValue(Double.parseDouble(vo.getDataValue()), name,vo.getDataName());
					}
				}
			}

			if (chart_type == ChartConstants.LINE_HORIZONTAL)
				freechart = ChartFactory.createLineChart(title, "", "",
						dataset, PlotOrientation.HORIZONTAL, true, true, false);
			else if (chart_type == ChartConstants.LINE_VERTICAL3D)
				freechart = ChartFactory.createLineChart3D(title, "", "",
						dataset, PlotOrientation.VERTICAL, true, true, false);
			else if (chart_type == ChartConstants.LINE_HORIZONTAL3D)
				freechart = ChartFactory.createLineChart3D(title, "", "",
						dataset, PlotOrientation.HORIZONTAL, true, true, false);
			else
				freechart = ChartFactory.createLineChart(title, "", "",
						dataset, PlotOrientation.VERTICAL, true, true, false);
		}else
			freechart = ChartFactory.createLineChart(title, "", "",
					dataset, PlotOrientation.HORIZONTAL, true, true, false);

		ChartParameter chartParameters = null;
		if (!"".equals(this.chartParameter)) {
			// 参数设置
			chartParameters = (ChartParameter) TagUtils.getInstance().lookup(
					pageContext, name, chartParameter, scope);
		}

		// 修改渲染参数后的效果 可以看到文字很清晰了，实际上和PHOTOSHOP里吧文字设置成名晰是一样的。将文字的抗锯齿参数关闭。
		// 使用的关闭抗锯齿后，字体尽量选择12到14号的宋体字。 这样文字最清晰好看
		//freechart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,
		//		RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		// 图形的绘制结构对象
		CategoryPlot plot = freechart.getCategoryPlot();
		if (chartParameters == null) { // 如果图形参数为空 （目的保证先前程序正常运行）
			LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) plot.getRenderer();
			lineandshaperenderer.setShapesVisible(true);
			lineandshaperenderer.setDrawOutlines(true);
			lineandshaperenderer.setUseFillPaint(true);
			lineandshaperenderer.setFillPaint(Color.white);
			// 节点上显示数值
			lineandshaperenderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			lineandshaperenderer.setItemLabelsVisible(true);

			// lineandshaperenderer.setItemLabelsVisible(new Boolean(true));
			// lineandshaperenderer.setItemLabelsVisible(true,true);

			CategoryAxis axis = plot.getDomainAxis();
			// 设置分类轴分类标签的最大宽度
			axis.setMaximumCategoryLabelWidthRatio(50f);
			// axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);

			ChartParameter achartParameters=new ChartParameter();
			NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
			if(hm.size()==1){
				DecimalFormat decimalFormat = new DecimalFormat("0.0");   //  0.00  显示如:56.89  0.00%  显示如:56.89%  0  显示如57 0% 显示如57%
				yAxis.setNumberFormatOverride(decimalFormat);
			}

			yAxis.setUpperMargin(achartParameters.getItemUpperMargin());// 设置设置最高的一个Item与图片顶端的距离
			yAxis.setLowerMargin(achartParameters.getItemLowerMargin());// 设置最低的一个Item与图片底端的距离
			if (achartParameters.isAutoRangeValue()) {
				// 是否自动调整数据轴显示区间(true:不是 false : 是)
				yAxis.setAutoRangeIncludesZero(false);
			} else {
				yAxis.setLowerBound(achartParameters.getNumberAxisStartValue());// 区间最小值
				yAxis.setUpperBound(achartParameters.getNumberAxisEndValue());// 区间最大值
			}
			double rangetick = achartParameters.getNumberAxisIncrement();
			if (rangetick == 0) {
			} else {
				yAxis.setTickUnit(new NumberTickUnit(rangetick));// 区间值增量
			}

			/*
			 * LegendTitle legendtitle = new LegendTitle(freechart.getPlot());
			 * legendtitle.setPosition(RectangleEdge.RIGHT);
			 * legendtitle.setHorizontalAlignment(HorizontalAlignment.LEFT);
			 */
			freechart.setBackgroundPaint(color);
			TextTitle tt = new TextTitle();
			tt.setFont(new Font("黑体",Font.PLAIN,20));
			tt.setText(title);
			freechart.setTitle(tt);
		}else {
			freechart.setAntiAlias(false);//字体不模糊
			plot.setNoDataMessage(chartParameters.getNoDataMessage());
			HashMap markerMap = chartParameters.getMarkerMap();// 控制值集合
			if (markerMap == null || markerMap.size() == 0) {
			} else {
				HashMap tempMap = new HashMap();
				Iterator it1 = markerMap.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry entry = (Map.Entry) it1.next();
					String label = (String) entry.getKey();
					String value = (String) entry.getValue();
					if(tempMap.containsKey(value)){// 如果集合中以存在此值
						String labe = (String) tempMap.get(value);
						//tempMap.put(value,"                "+labe+" / "+label);
						if("平均值".equalsIgnoreCase(label)){
							value = this.formatValue(value,2);
						}
						int n = value.length() + 8 ;
						String temp = "";
						for(int i = 0 ; i<n; i++){
							temp+=" ";
						}
						tempMap.put(value,"                "+temp.toString()+labe+" / "+label+"("+value+")");
					}else{
						//tempMap.put(value,"           "+label);
						if("平均值".equalsIgnoreCase(label)){
							value = this.formatValue(value,2);
						}
						int n = value.length() + 8 ;
						String temp = "";
						for(int i = 0 ; i<n; i++){
							temp+=" ";
						}
						tempMap.put(value,"           "+temp.toString()+label+"("+value+")");
					}
				}

				Iterator ittt = tempMap.entrySet().iterator();
				double te = 0.0D;
				while(ittt.hasNext()){
					Map.Entry entry = (Map.Entry) ittt.next();
					String value= (String) entry.getKey();
					double v = Double.parseDouble(value);
					v = Double.parseDouble(this.formatValue(String.valueOf(v),2));
					if(te < v){
						te = v;
					}
				}

				Iterator itt = tempMap.entrySet().iterator();
				while (itt.hasNext()) {
					Map.Entry entry = (Map.Entry) itt.next();
					String value = (String) entry.getKey();
					String label = (String) entry.getValue();

					double v = Double.parseDouble(value);
					v = Double.parseDouble(this.formatValue(String.valueOf(v),2));

					// 值相同则为一条线（描述信息改变）
					/*
					ValueMarker valueMarker = new ValueMarker(v);
					valueMarker.setPaint(Color.BLUE);
					valueMarker.setLabel(label);
					valueMarker.setLabelFont(new Font("SansSerif", Font.PLAIN,12));
					plot.addRangeMarker(valueMarker, Layer.BACKGROUND);
					*/
					double rangetick = chartParameters.getNumberAxisIncrement();
					//System.out.println("rangetick=" + rangetick);
					if(rangetick == 0.0D){
						NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
						NumberTickUnit ntu = yAxis.getTickUnit();
						rangetick = ntu.getSize();
					}

					double flag = 0.0D;
					if(te < 100){
						flag = rangetick / 10;
					}else{
						flag = rangetick;
					}
					//System.out.println("纵向线图" + label + "  " +v +"  " + (v-flag) +"  " +  (v+flag));

					IntervalMarker imarker = new IntervalMarker(v-flag,v+flag);
					imarker.setPaint(Color.CYAN);
					imarker.setLabel(label);
					imarker.setLabelFont(new Font("SansSerif", Font.PLAIN,12));
					plot.addRangeMarker(imarker, Layer.BACKGROUND);
				}
			}

			String tt = chartParameters.getChartTitle();
			if (tt == null) {

			} else {
				if ("no".equals(tt)) {
					title = "";
				} else {
					TextTitle t = new TextTitle(tt, new Font("SansSerif",Font.PLAIN, 18));
					String temp = chartParameters.getChartTitleAlign();
					// 右对齐
					if ("LEFT".equals(temp)) {
						t.setHorizontalAlignment(org.jfree.ui.HorizontalAlignment.LEFT);
					} else if ("RIGHT".equals(temp)) {
						t.setHorizontalAlignment(org.jfree.ui.HorizontalAlignment.RIGHT);
					} else {
						t.setHorizontalAlignment(org.jfree.ui.HorizontalAlignment.CENTER);
					}
					t.setFont(new Font("黑体",Font.PLAIN,20));
					freechart.setTitle(t);
				}
			}

			// 图形的绘制单元
			LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
			DecimalFormat df2 = (DecimalFormat) DecimalFormat.getInstance();
			df2.applyPattern("0.0%");
			if (chartParameters.isItemLabelsVisible()) {
				// 显示每个节点的数值
				if(labelIsPercent==null|| "0".equals(labelIsPercent)){
					renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
				}else{
					renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",df2));
				}
				renderer.setItemLabelsVisible(true);
				// 设置数值显示字体信息
				renderer.setItemLabelFont(new Font("Default", Font.PLAIN, 9));
			}

			if(chartParameters.getLineNodeIsMarked()==1)  //绘制线状图纵轴虚线
			{
				float dash1[] = {2.0f};
				BasicStroke bs = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
				Iterator ait = hm.entrySet().iterator();
				while (ait.hasNext()) {
					Map.Entry entry = (Map.Entry) ait.next();
					list = (ArrayList) entry.getValue();
					String name = (String) entry.getKey();
					int j = 0;
					for (j = 0; j < list.size(); j++) {
						CommonData vo = (CommonData) list.get(j);
						CategoryMarker marker = new CategoryMarker(vo.getDataName(), Color.orange,bs);
						marker.setDrawAsLine(true);
						marker.setLabelOffset(new RectangleInsets(2, 5, 2, 5));
						plot.addDomainMarker(marker, Layer.BACKGROUND);
					}
					//	dataset.addValue(Double.parseDouble(vo.getDataValue()), name,vo.getDataName());
				}
			}

			renderer.setShapesVisible(true);
			renderer.setUseFillPaint(true);
			renderer.setFillPaint(Color.BLUE);
			renderer.setStroke(new BasicStroke(chartParameters.getStrokeWidth(), 1, 1));  //dengcan 2007-07-19  设置折线宽度

			// 分类轴参数设置
			CategoryAxis xAxis = plot.getDomainAxis();
			String categoryLabelType = chartParameters.getCategoryLabelType();// 分类轴标签类型
			if ("STANDARD".equalsIgnoreCase(categoryLabelType)) {// 标准
				xAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
			} else if ("DOWN_45".equalsIgnoreCase(categoryLabelType)) {// 下偏移45度
				xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
			} else if ("DOWN_90".equalsIgnoreCase(categoryLabelType)) {// 下偏移90度
				xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
				// xAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI
				// / 3.0));
			} else if ("UP_45".equalsIgnoreCase(categoryLabelType)) {// 上偏移45度
				xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
			} else if ("UP_90".equalsIgnoreCase(categoryLabelType)) {// 上偏移90度
				xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
				// xAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI
				// / 6.0));
			}

			xAxis.setMaximumCategoryLabelWidthRatio(chartParameters.getCategoryLabelMaxWidth());// item描述信息最大宽度
			xAxis.setLowerMargin(chartParameters.getItemLeftMargin());// 设置距离图片左端距离
			xAxis.setUpperMargin(chartParameters.getItemRightMargin());// 设置距离图片右端距离
			xAxis.setCategoryMargin(chartParameters.getItemMargin());// 组与组之间的距离
			xAxis.setTickLabelFont(new Font("STSong-Light",Font.PLAIN,12));   //dengcan 2007-07-19  修改下坐标字体大小

			// 数据轴参数设置
			NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
			if(hm.size()==1)  //指定纵坐标的显示格式
			{
				DecimalFormat decimalFormat = new DecimalFormat("0.0");   //  0.00  显示如:56.89  0.00%  显示如:56.89%  0  显示如57 0% 显示如57%
				yAxis.setNumberFormatOverride(decimalFormat);
			}
			yAxis.setUpperMargin(chartParameters.getItemUpperMargin());// 设置设置最高的一个Item与图片顶端的距离
			yAxis.setLowerMargin(chartParameters.getItemLowerMargin());// 设置最低的一个Item与图片底端的距离
			if (chartParameters.isAutoRangeValue()) {
				// 是否自动调整数据轴显示区间(true:不是 false : 是)
				yAxis.setAutoRangeIncludesZero(false);
			} else {
				yAxis.setLowerBound(chartParameters.getNumberAxisStartValue());// 区间最小值
				yAxis.setUpperBound(chartParameters.getNumberAxisEndValue());// 区间最大值
			}
			double rangetick = chartParameters.getNumberAxisIncrement();
			if (rangetick == 0) {
			} else {
				yAxis.setTickUnit(new NumberTickUnit(rangetick));// 区间值增量
			}

			if(chartParameters.getWidth()!=0&&chartParameters.getHeight()!=0)
			{
				this.width=chartParameters.getWidth();
				this.height=chartParameters.getHeight();
			}
			freechart.setBackgroundPaint(color);
		}
		return freechart;
	}



	private JFreeChart createCategoryBarChart(ArrayList list)throws JspException{
		JFreeChart freechart = null;
		Color color = ColorHelper.getColor(bg_color);

		// 数据源封装
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < list.size(); i++) {
			LazyDynaBean abean=(LazyDynaBean)list.get(i);
			String categoryName=(String)abean.get("categoryName");
			ArrayList dataList=(ArrayList)abean.get("dataList");
			for(int j=0;j<dataList.size();j++){
				CommonData vo=(CommonData)dataList.get(j);
				dataset.addValue(Double.parseDouble(vo.getDataValue()),vo.getDataName(), categoryName);
			}
		}
		if(chart_type==ChartConstants.CATEGORY_BAR)
			freechart = ChartFactory.createBarChart(title, "", "", dataset, PlotOrientation.VERTICAL,true, true, false);
		if(chart_type==ChartConstants.CATEGORY_3DBAR)
			freechart = ChartFactory.createBarChart3D(title, "", "", dataset, PlotOrientation.VERTICAL,true, true, false);
		ChartParameter chartParameters = null;
		CategoryPlot plot = freechart.getCategoryPlot();
		if (!"".equals(this.chartParameter)) {
			// 参数设置
			chartParameters = (ChartParameter) TagUtils.getInstance().lookup(
					pageContext, name, chartParameter, scope);
		}

		if(chartParameters==null)
		{
			freechart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			//freechart.setBackgroundPaint(Color.WHITE);
			freechart.setBackgroundPaint(color);
			TextTitle tt = new TextTitle();
			tt.setFont(new Font("黑体", Font.PLAIN, 20));
			tt.setText(title);
			freechart.setTitle(tt);

			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			rangeAxis.setUpperMargin(0.05);
			BarRenderer renderer2 = (BarRenderer) plot.getRenderer();
			renderer2.setItemMargin(-0.2);
			renderer2.setMaximumBarWidth(0.05);
			CategoryItemRenderer renderer = plot.getRenderer();
			renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			renderer.setItemLabelsVisible(true);
			ItemLabelPosition p = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.TOP_CENTER, TextAnchor.TOP_CENTER, 0.0);
			renderer.setPositiveItemLabelPosition(p);

			CategoryAxis domainAxis = plot.getDomainAxis();
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		}else{
			String tt = chartParameters.getChartTitle();
			if (tt == null) {
			} else {
				if ("no".equals(tt)) {
					title = "";
				} else {
					TextTitle t = new TextTitle(tt, new Font("SansSerif",Font.PLAIN, 18));
					String temp = chartParameters.getChartTitleAlign();
					// 右对齐
					if ("LEFT".equals(temp)) {
						t.setHorizontalAlignment(org.jfree.ui.HorizontalAlignment.LEFT);
					} else if ("RIGHT".equals(temp)) {
						t.setHorizontalAlignment(org.jfree.ui.HorizontalAlignment.RIGHT);
					} else {
						t.setHorizontalAlignment(org.jfree.ui.HorizontalAlignment.CENTER);
					}
					t.setFont(new Font("黑体",Font.PLAIN,20));
					freechart.setTitle(t);
				}
			}

			//分类轴参数设置
			CategoryAxis xAxis = plot.getDomainAxis();
			String categoryLabelType = chartParameters.getCategoryLabelType();// 分类轴标签类型
			if ("STANDARD".equalsIgnoreCase(categoryLabelType)) {// 标准
				xAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
			} else if ("DOWN_45".equalsIgnoreCase(categoryLabelType)) {// 下偏移45度
				xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
			} else if ("DOWN_90".equalsIgnoreCase(categoryLabelType)) {// 下偏移90度
				xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
				// xAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI
				// / 3.0));
			} else if ("UP_45".equalsIgnoreCase(categoryLabelType)) {// 上偏移45度
				xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
			} else if ("UP_90".equalsIgnoreCase(categoryLabelType)) {// 上偏移90度
				xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
				// xAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI
				// / 6.0));
			}

			xAxis.setMaximumCategoryLabelWidthRatio(chartParameters.getCategoryLabelMaxWidth());// item描述信息最大宽度
			xAxis.setLowerMargin(chartParameters.getItemLeftMargin());// 设置距离图片左端距离
			xAxis.setUpperMargin(chartParameters.getItemRightMargin());// 设置距离图片右端距离
			xAxis.setCategoryMargin(chartParameters.getItemMargin());// 组与组之间的距离
			xAxis.setTickLabelFont(new Font("STSong-Light",Font.PLAIN,12));   //dengcan 2007-07-19  修改下坐标字体大小

			// 数据轴参数设置
			NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
			yAxis.setUpperMargin(chartParameters.getItemUpperMargin());// 设置设置最高的一个Item与图片顶端的距离
			yAxis.setLowerMargin(chartParameters.getItemLowerMargin());// 设置最低的一个Item与图片底端的距离
			if (chartParameters.isAutoRangeValue()) {
				// 是否自动调整数据轴显示区间(true:不是 false : 是)
				yAxis.setAutoRangeIncludesZero(false);
			} else {
				yAxis.setLowerBound(chartParameters.getNumberAxisStartValue());// 区间最小值
				yAxis.setUpperBound(chartParameters.getNumberAxisEndValue());// 区间最大值
			}

			double rangetick = chartParameters.getNumberAxisIncrement();
			if (rangetick == 0) {
			} else {
				yAxis.setTickUnit(new NumberTickUnit(rangetick));// 区间值增量
			}

			if(chartParameters.isItemLabelsVisible()){
				//NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
				//rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				//rangeAxis.setUpperMargin(0.15);
				CategoryItemRenderer renderer = plot.getRenderer();
				renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
				//renderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
				renderer.setItemLabelsVisible(true);
			}

			if(chartParameters.getWidth()!=0&&chartParameters.getHeight()!=0){
				this.width=chartParameters.getWidth();
				this.height=chartParameters.getHeight();
			}

			freechart.setBackgroundPaint(color);
		}

		return freechart;
	}


	/**
	 * 根据图例类型创建相应的图例Pie Bar
	 *
	 * @param list
	 * @return
	 * @throws JspException
	 */
	private JFreeChart createPieBarChartByType(ArrayList list) throws JspException{
		JFreeChart freechart=null;
		Color  color=ColorHelper.getColor(bg_color);
		DecimalFormat df1=(DecimalFormat)DecimalFormat.getInstance();
		df1.applyPattern("0");
		DecimalFormat df2 = (DecimalFormat) DecimalFormat.getInstance();
		df2.applyPattern("0.00%");
		double sum=0;
		switch(chart_type){
			case ChartConstants.PIE:
			case ChartConstants.PIE_3D:{
				DefaultPieDataset dataset=new DefaultPieDataset();
				for(int i=0;i<list.size();i++){
					CommonData vo=(CommonData)list.get(i);
					dataset.setValue(vo.getDataName(),Double.parseDouble(vo.getDataValue()));
					sum=sum+Double.parseDouble(vo.getDataValue());
				}
				if(chart_type==ChartConstants.PIE_3D){
					freechart=ChartFactory.createPieChart(title,dataset,true,true,false);
					freechart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

					PiePlot plot = (PiePlot) freechart.getPlot();
					plot.setSectionOutlinesVisible(false);
					//plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
					plot.setNoDataMessage("");
					plot.setLabelGap(0.02);
					plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} {2}", df1, df2));
					plot.setShadowYOffset(0);
					plot.setShadowYOffset(0);
				}else{
					freechart=ChartFactory.createPieChart3D(title,dataset,true,true,false);
					freechart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

					PiePlot3D plot = (PiePlot3D) freechart.getPlot();
					plot.setSectionOutlinesVisible(false);
					//plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
					plot.setLabelGap(0.02);
					plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} {2}", df1, df2));
					plot.setNoDataMessage("");

					if(this.height>400){
						this.height=400;
						//plot.setDepthFactor(0.05);
					}
					plot.setShadowYOffset(0);
					plot.setShadowYOffset(0);
					plot.setForegroundAlpha(0.9f);
				}
				break;
			}
			case ChartConstants.VERTICAL_BAR:
			case ChartConstants.VERTICAL_BAR_3D:
			case ChartConstants.HORIZONTAL_BAR:
			case ChartConstants.HORIZONTAL_BAR_3D:{
				double maxValue = 0.0;
				// 数据源封装
				DefaultCategoryDataset dataset=new DefaultCategoryDataset();
				for(int i=0;i<list.size();i++){
					CommonData vo=(CommonData)list.get(i);
					double n = Double.parseDouble(vo.getDataValue());
					if(n>maxValue){
						maxValue = n;
					}
					dataset.addValue(n,vo.getDataName(),""/* vo.getDataName() */);
					sum=sum+Double.parseDouble(vo.getDataValue());
				}
				// 显示类型(水平/垂直)
				PlotOrientation xy;
				if(chart_type==ChartConstants.VERTICAL_BAR||chart_type==ChartConstants.VERTICAL_BAR_3D){
					xy=PlotOrientation.VERTICAL;
				}else{
					xy=PlotOrientation.HORIZONTAL;
				}

				if(chart_type==ChartConstants.VERTICAL_BAR||chart_type==ChartConstants.HORIZONTAL_BAR){
					freechart=ChartFactory.createBarChart(title,"","",dataset,xy,true,true,false);
				}else{
					freechart=ChartFactory.createBarChart3D(title,"","",dataset,xy,true,true,false);
				}

				freechart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
				freechart.setBackgroundPaint(Color.WHITE);
				ChartParameter chartParameters = null;
				if(!"".equals(this.chartParameter)){
					// 参数设置
					chartParameters = (ChartParameter)TagUtils.getInstance().lookup(pageContext,name,chartParameter,scope);
				}
				if(chartParameters == null){//如果图形参数为空 （目的保证先前程序正常运行）
					if(chart_type==ChartConstants.VERTICAL_BAR||chart_type==ChartConstants.HORIZONTAL_BAR){
						CategoryPlot cplot = (CategoryPlot) freechart.getPlot();
						BarRenderer renderer = (BarRenderer) cplot.getRenderer();
						renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
						renderer.setItemLabelsVisible(true);
						renderer.setBaseOutlinePaint(Color.BLACK);
						renderer.setItemLabelPaint(new Color(0, 0, 0), true);
						renderer.setDrawBarOutline(true);
						// renderer.setLegendItemLabelGenerator(new
						// StandardCategorySeriesLabelGenerator("0.00%"));

						renderer.setMaximumBarWidth(0.05);
						// CategoryPlot categoryplot =
						// freechart.getCategoryPlot();

						NumberAxis yAxis = (NumberAxis) cplot.getRangeAxis();
						// 数据轴的数据标签是否自动确定
						yAxis.setAutoTickUnitSelection(true);
						// System.out.println(maxValue);
						BigDecimal decimal=new BigDecimal(String.valueOf(maxValue));

						BigDecimal v=decimal.multiply(new BigDecimal("1.5"));

						// System.out.println("v="+v.toString());
						yAxis.setUpperBound(Double.parseDouble(v.toString()));// 最大

						// NumberAxis na= (NumberAxis)
						// categoryplot.getRangeAxis();
						// System.out.println(max+max*0.5);
						// na.setAutoTickUnitSelection(true);
						// na.setUpperBound(max+max*0.5);

						// categoryplot.setRenderer(renderer);
						// na.setLowerMargin(chartParameters.getItemLowerMargin());//设置最低的一个Item与图片底端的距离
						// na.setFixedAutoRange(10D);
						// va.setLowerBound(5.0D);
						/*
						 * va.setAutoRange(false);
						 * va.setAutoRangeMinimumSize(5D,true);
						 * va.setAutoTickUnitSelection(true);
						 * va.setFixedAutoRange(100D);
						 */

						/*
						 * va.setAutoRange(true);
						 *
						 * va.setLowerBound(0); va.setUpperBound(100);
						 */
					}else{
						BarRenderer3D renderer = new BarRenderer3D();
						renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
						renderer.setBaseOutlinePaint(Color.BLACK);
						renderer.setItemLabelPaint(new Color(0, 0, 0), true);
						renderer.setItemLabelFont(new Font("黑体",Font.PLAIN,12));
						renderer.setItemLabelsVisible(true);
						renderer.setDrawBarOutline(true);
						renderer.setItemLabelAnchorOffset(10D);
						renderer.setPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
						renderer.setMaximumBarWidth(0.05);
						CategoryPlot categoryplot = freechart.getCategoryPlot();
						categoryplot.setRenderer(renderer);
						categoryplot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
						categoryplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
					}

				}else{
					freechart.setAntiAlias(false);//字体不模糊
					// 图形的绘制结构对象
					CategoryPlot plot = freechart.getCategoryPlot();
					plot.setNoDataMessage(chartParameters.getNoDataMessage());
					HashMap markerMap = chartParameters.getMarkerMap();// 控制值集合
					if(markerMap==null||markerMap.size()==0){
					}else{
						HashMap tempMap = new HashMap();
						Iterator it = markerMap.entrySet().iterator();
						while(it.hasNext()){
							Map.Entry entry = (Map.Entry) it.next();
							String label= (String) entry.getKey();
							String value = (String) entry.getValue();
							if(tempMap.containsKey(value)){// 如果集合中以存在此值
								String labe = (String) tempMap.get(value);
								//tempMap.put(value,"                "+labe+" / "+label);
								if("平均值".equalsIgnoreCase(label)){
									value = this.formatValue(value,2);
								}
								int n = value.length() + 8 ;
								String temp = "";
								for(int i = 0 ; i<n; i++){
									temp+=" ";
								}
								tempMap.put(value,"                "+temp.toString()+labe+" / "+label+"("+value+")");
							}else{
								//tempMap.put(value,"           "+label);
								if("平均值".equalsIgnoreCase(label)){
									value = this.formatValue(value,2);
								}
								int n = value.length() + 8 ;
								String temp = "";
								for(int i = 0 ; i<n; i++){
									temp+=" ";
								}
								tempMap.put(value,"           "+temp.toString()+label+"("+value+")");
							}
						}

						Iterator ittt = tempMap.entrySet().iterator();
						double te = 0.0D;
						while(ittt.hasNext()){
							Map.Entry entry = (Map.Entry) ittt.next();
							String value= (String) entry.getKey();
							double v = Double.parseDouble(value);
							v = Double.parseDouble(this.formatValue(String.valueOf(v),2));
							if(te < v){
								te = v;
							}
						}

						Iterator itt = tempMap.entrySet().iterator();
						while(itt.hasNext()){
							Map.Entry entry = (Map.Entry) itt.next();
							String value= (String) entry.getKey();
							String label = (String) entry.getValue();
							// 值相同则为一条线（描述信息改变）

							double v = Double.parseDouble(value);
							v = Double.parseDouble(this.formatValue(String.valueOf(v),2));
							// 值相同则为一条线（描述信息改变）
    						/*
    						ValueMarker valueMarker = new ValueMarker(v);
    						valueMarker.setPaint(Color.BLUE);
    						valueMarker.setLabel(label);
    						valueMarker.setLabelFont(new Font("SansSerif", Font.PLAIN,12));
    						plot.addRangeMarker(valueMarker, Layer.BACKGROUND);
    						*/
							double rangetick = chartParameters.getNumberAxisIncrement();
							if(rangetick == 0.0D){
								NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
								NumberTickUnit ntu = yAxis.getTickUnit();
								rangetick = ntu.getSize();
							}
							double flag = 0.0D;
							if(te < 100){
								flag = rangetick / 10;
							}else{
								flag = rangetick;
							}

							IntervalMarker imarker = new IntervalMarker(v-flag,v+flag);
							imarker.setPaint(Color.CYAN);
							imarker.setLabel(label);
							imarker.setLabelFont(new Font("SansSerif", Font.PLAIN,12));
							plot.addRangeMarker(imarker, Layer.BACKGROUND);

						}

					}

					String tt = chartParameters.getChartTitle();
					if(tt == null){
					}else{
						if("no".equals(tt)){
							title = "";
						}else{
							title = chartParameters.getChartTitle();

							/*
							 * if(title == null || title.equals("")){ }else{
							 */
							TextTitle t = new TextTitle(title, new Font("SansSerif", Font.PLAIN, 18));
							String temp =chartParameters.getChartTitleAlign();
							// 右对齐
							if("LEFT".equals(temp)){
								t.setHorizontalAlignment(org.jfree.ui.HorizontalAlignment.LEFT);
							}else if("RIGHT".equals(temp)){
								t.setHorizontalAlignment(org.jfree.ui.HorizontalAlignment.RIGHT);
							}else{
								t.setHorizontalAlignment(org.jfree.ui.HorizontalAlignment.CENTER);
							}
							freechart.setTitle(t);
							/* } */
						}
					}

					// 图形的绘制单元
					BarRenderer renderer = (BarRenderer) plot.getRenderer();
					if(chartParameters.isItemLabelsVisible()){
						// 显示每个柱的数值
						renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
						renderer.setItemLabelsVisible(true);
						// 设置柱数值显示字体信息
						renderer.setItemLabelFont(new Font("SansSerif",Font.PLAIN,10));
					}
					// 组内Item之间距离
					renderer.setItemMargin(chartParameters.getItemMargin());

					renderer.setMaximumBarWidth(0.05);

					// 分类轴参数设置
					CategoryAxis xAxis = plot.getDomainAxis();
					String categoryLabelType = chartParameters.getCategoryLabelType();// 分类轴标签类型
					if("STANDARD".equalsIgnoreCase(categoryLabelType)){// 标准
						xAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
					}else if("DOWN_45".equalsIgnoreCase(categoryLabelType)){// 下偏移45度
						xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
					}else if("DOWN_90".equalsIgnoreCase(categoryLabelType)){// 下偏移90度
						xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
						// xAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI
						// / 6.0));
					}else if("UP_45".equalsIgnoreCase(categoryLabelType)){// 上偏移45度
						xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
					}else if("UP_90".equalsIgnoreCase(categoryLabelType)){// 上偏移90度
						xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
						// xAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI
						// / 6.0));
					}
					xAxis.setMaximumCategoryLabelWidthRatio(chartParameters.getCategoryLabelMaxWidth());// item描述信息最大宽度
					xAxis.setLowerMargin(chartParameters.getItemLeftMargin());// 设置距离图片左端距离
					xAxis.setUpperMargin(chartParameters.getItemRightMargin());// 设置距离图片右端距离
					xAxis.setCategoryMargin(chartParameters.getItemMargin());// 组与组之间的距离

					// xAxis.setLabelAngle(Math.PI/2);

					// 数据轴参数设置
					NumberAxis  yAxis= (NumberAxis) plot.getRangeAxis();
					yAxis.setUpperMargin(chartParameters.getItemUpperMargin());// 设置设置最高的一个Item与图片顶端的距离
					yAxis.setLowerMargin(chartParameters.getItemLowerMargin());// 设置最低的一个Item与图片底端的距离
					if(chartParameters.isAutoRangeValue()){
						// 是否自动调整数据轴显示区间(true:不是 false : 是)
						yAxis.setAutoRangeIncludesZero(false);
					}else{
						yAxis.setLowerBound(chartParameters.getNumberAxisStartValue());// 区间最小值
						yAxis.setUpperBound(chartParameters.getNumberAxisEndValue());// 区间最大值
					}
					double rangetick = chartParameters.getNumberAxisIncrement();
					if(rangetick == 0){
					}else{
						yAxis.setTickUnit(new NumberTickUnit(rangetick));// 区间值增量
					}
				}
				break;
			}
		}// switch end.

		freechart.setBackgroundPaint(color);
		if("true".equalsIgnoreCase(this.isneedsum)){
			//freechart.setTitle(title+"("+df1.format(sum)+")");
			TextTitle tt = new TextTitle();
			tt.setFont(new Font("黑体",Font.PLAIN,20));
			tt.setText(title+"("+df1.format(sum)+")");
			freechart.setTitle(tt);
		}else{
			//freechart.setTitle(title);
			TextTitle tt = new TextTitle();
			tt.setFont(new Font("黑体",Font.PLAIN,20));
			tt.setText(title);
			freechart.setTitle(tt);
		}

		return freechart;
	}

	private int outPutChart(JFreeChart freechart) {
		try {
			//String filename = ServletUtilities.saveChartAsPNG(freechart, width,height, null, null);

			com.hjsj.hrms.servlet.ServletUtilities.createTempDir();
			String prefix ="public-jfreechart-";  //com.hjsj.hrms.servlet.ServletUtilities.tempFilePrefix;//"jfreechart-onetime-";  //com.hjsj.hrms.servlet.ServletUtilities.tempFilePrefix;
		    /*File tempFile = File.createTempFile(prefix, ".png", new File(System.getProperty("java.io.tmpdir")));
		    ChartUtilities.saveChartAsPNG(tempFile,freechart, width, height,null);*/
			File tempFile = File.createTempFile(prefix, ".jpg", new File(System.getProperty("java.io.tmpdir")));
			ChartUtilities.saveChartAsJPEG(tempFile,freechart, width, height,null);
			if (pageContext.getSession() != null)
				com.hjsj.hrms.servlet.ServletUtilities.registerPhotoForDeletion(tempFile, pageContext.getSession());
			String filename=tempFile.getName();

			// 下面注释方法为何输出不了？
			// ServletUtilities.sendTempFile(filename,(HttpServletResponse)pageContext.getResponse());
			String url = ((HttpServletRequest) pageContext.getRequest()).getContextPath();
			StringBuffer charturl = new StringBuffer();
			charturl.append(url);
			charturl.append("/servlet/DisplayChart?filename=");
			charturl.append(filename);
			StringBuffer str_html = new StringBuffer();

			str_html.append("<img src=\"");
			str_html.append(charturl.toString());
			str_html.append("\" border=0>");
			
			/*//加百分比
			if(xdwidth.equals("")||xdheight.equals("")){
				str_html.append("<img src=\"");
				str_html.append(charturl.toString());
				str_html.append("\" border=0>");
			}else{
				System.out.println(xdwidth + " " + xdheight);
				str_html.append("<img src=\"");
				str_html.append(charturl.toString());
				str_html.append("\"");
				str_html.append(" border=0 width=\"");
				str_html.append(xdwidth+"%\" ");
				str_html.append(" height=\"");
				str_html.append(xdheight +"%\"");
				str_html.append(" >");
			}*/

			pageContext.getOut().println(str_html.toString());
			return EVAL_BODY_BUFFERED;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取规范的表达式的值,自动四舍五入
	 * @param exprValue 表达式值
	 * @param flag      小数位
	 * @return  规范后的值
	 */
	public String formatValue(String exprValue , int flag){

		StringBuffer sb = new StringBuffer();
		if(flag == 0){
			sb.append("####");
		}else{
			sb.append("####.");
			for(int i = 0 ; i < flag ; i++){
				sb.append("0");
			}
		}
		DecimalFormat df = new DecimalFormat(sb.toString());
		String dstr = df.format(Double.parseDouble(exprValue));
		return dstr;
	}

	public int doAfterBody() throws JspException {

		return super.doAfterBody();
	}

	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

	public ChartLegendTag() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	private String scope;

	public int getChart_type() {
		return chart_type;
	}

	public void setChart_type(int chart_type) {
		this.chart_type = chart_type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getLegends() {
		return legends;
	}

	public void setLegends(String legends) {
		this.legends = legends;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getChartParameter() {
		return chartParameter;
	}

	public void setChartParameter(String chartParameter) {
		this.chartParameter = chartParameter;
	}

	public String getIsneedsum() {
		return isneedsum;
	}

	public void setIsneedsum(String isneedsum) {
		this.isneedsum = isneedsum;
	}
	
	/*public String getXdheight() {
		return xdheight;
	}

	public void setXdheight(String xdheight) {
		this.xdheight = xdheight;
	}

	public String getXdwidth() {
		return xdwidth;
	}

	public void setXdwidth(String xdwidth) {
		this.xdwidth = xdwidth;
	}
	 */

	public String getLabelIsPercent() {
		return labelIsPercent;
	}

	public void setLabelIsPercent(String labelIsPercent) {
		this.labelIsPercent = labelIsPercent;
	}

	public boolean isAnychart() {
		return anychart;
	}

	public void setAnychart(boolean anychart) {
		this.anychart = anychart;
	}

	public String getChartpnl() {
		return chartpnl;
	}

	public void setChartpnl(String chartpnl) {
		this.chartpnl = chartpnl;
	}

	public void setOrderlist(ArrayList orderlist) {
		this.orderlist = orderlist;
	}

	public ArrayList getOrderlist() {
		return orderlist;
	}

	public String getPointClick() {
		return pointClick;
	}

	public void setPointClick(String pointClick) {
		this.pointClick = pointClick;
	}

	public String getBg_color() {
		return bg_color;
	}

	public void setBg_color(String bg_color) {
		this.bg_color = bg_color;
	}

	public int getXangle() {
		return xangle;
	}

	public void setXangle(int xangle) {
		this.xangle = xangle;
	}

	public String getLabel_enabled() {
		return label_enabled;
	}

	public void setLabel_enabled(String label_enabled) {
		this.label_enabled = label_enabled;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getTooltip_enabled() {
		return tooltip_enabled;
	}

	public void setTooltip_enabled(String tooltip_enabled) {
		this.tooltip_enabled = tooltip_enabled;
	}

	public String getBiDesk() {
		return biDesk;
	}

	public void setBiDesk(String biDesk) {
		this.biDesk = biDesk;
	}

	public int getNumDecimals() {
		return numDecimals;
	}

	public void setNumDecimals(int numDecimals) {
		this.numDecimals = numDecimals;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}
	public boolean isTransparent() {
		return transparent;
	}

	public ArrayList getMinvalue() {
		return minvalue;
	}

	public void setMinvalue(ArrayList minvalue) {
		this.minvalue = minvalue;
	}

	public ArrayList getMaxvalue() {
		return maxvalue;
	}

	public void setMaxvalue(ArrayList maxvalue) {
		this.maxvalue = maxvalue;
	}

	public ArrayList getValves() {
		return valves;
	}

	public void setValves(ArrayList valves) {
		this.valves = valves;
	}

	public ArrayList getCvalues() {
		return cvalues;
	}

	public void setCvalues(ArrayList cvalues) {
		this.cvalues = cvalues;
	}

	public boolean isPieoutin() {
		return pieoutin;
	}

	public void setPieoutin(boolean pieoutin) {
		this.pieoutin = pieoutin;
	}


}
