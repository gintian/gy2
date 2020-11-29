/**
 * 
 */
package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.taglib.general.ChartConstants;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author cmq May 7, 20099:25:44 AM
 */
public class AnychartBo {

	private int width;
	private int height;
	private String islabelname = "true";//islabelname为true时，饼状图显示标签描述信息，反之，则不显示
	/**
	 * bar ,scale 设置y座标的值
	 */
	private float maxvalue = 0;
	private float minvalue = 0;
	/** 标题 */
	private String title;
	private int chart_type;
	/** x 座标旋转角度 */
	private int xangle = 0;
	/** 统计图动画效果 */
	private boolean animation = true;
	/** 小数点的位数 */
	private int numDecimals = 0;
	/** 输入面板 */
	private String chartpnl = "";
	private String bg_color = "#F4F7F7";
	private String tooltipfontsize = "12";
	private String labelfontsize = "12";
	private String legfontsize = "12";
	private String label_enabled = "true";
	private String tooltip_enabled = "true";
	private String isneedsum = "true";
	// 是否透明 guodd 2014-12-22
	private boolean transparent = false;
	//线状柱状图Y轴是否自适应  haosl 2017-8-16
	private String yAxisAuto = "false";
	private int maxXangle = 90;

	public void setyAxisAuto(String yAxisAuto) {
		this.yAxisAuto = yAxisAuto;
	}

	public String getIslabelname() {
		return islabelname;
	}

	public void setIslabelname(String islabelname) {
		this.islabelname = islabelname;
	}

	public void setNumDecimals(int numDecimals) {
		this.numDecimals = numDecimals;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getChart_type() {
		return chart_type;
	}

	public void setChart_type(int chart_type) {
		this.chart_type = chart_type;
	}

	public AnychartBo() {
		initFontSize();
	}

	public AnychartBo(String title, int chart_type, int width, int height) {
		this.chart_type = chart_type;
		this.title = title;
		this.width = width;
		this.height = height;
		initFontSize();
	}

	public AnychartBo(String title, int chart_type, int width, int height,
			String label_enabled) {
		this.chart_type = chart_type;
		this.title = title;
		this.width = width;
		this.height = height;
		this.label_enabled = label_enabled;
		initFontSize();
	}

	/**
	 * 输入雷达图xml
	 * 
	 * @param hm
	 * @return
	 */
	private String outRadarXml(HashMap hm) {
		StringBuffer buf = new StringBuffer();
		double sum = 0;
		DecimalFormat df1 = (DecimalFormat) DecimalFormat.getInstance();
		if (this.numDecimals == 0) {
            df1.applyPattern("0");
        } else {
			String fmtstr = StringUtils.rightPad("0.", 2 + this.numDecimals,
			"0");
			df1.applyPattern(fmtstr);
		}
		Element root = new Element("anychart");
		Document myDocument = new Document(root);
		Element settings = new Element("settings");
		root.addContent(settings);
		Element animation = new Element("animation");
		if (this.animation) {
            animation.setAttribute("enabled", "True");
        } else {
            animation.setAttribute("enabled", "False");
        }
		settings.addContent(animation);

		Element margin = new Element("margin");
		root.addContent(margin);
		margin.setAttribute("all", "0");

		Element context_menu = new Element("context_menu");
		settings.addContent(context_menu);
		context_menu.setAttribute("version_info", "false");
		context_menu.setAttribute("about_anychart", "false");
		Element save_as_image_item_text = new Element("save_as_image_item_text");
		context_menu.addContent(save_as_image_item_text);
		save_as_image_item_text.setText("保存");
		Element print_chart_item_text = new Element("print_chart_item_text");
		context_menu.addContent(print_chart_item_text);
		print_chart_item_text.setText("打印");

		Element charts = new Element("charts");
		root.addContent(charts);
		Element chart = new Element("chart");
		chart.setAttribute("plot_type", "Radar");

		charts.addContent(chart);
		// data_plot_settings
		Element data_plot_settings = new Element("data_plot_settings");

		Element radar = new Element("radar");
		data_plot_settings.addContent(radar);
		chart.addContent(data_plot_settings);
		// styles
		Element styles = new Element("styles");
		chart.addContent(styles);
		Element tooltip_style = new Element("tooltip_style");
		styles.addContent(tooltip_style);
		tooltip_style.setAttribute("name", "idGeneralTooltip");

		Element tooltip_stylefont = new Element("font");
		tooltip_style.addContent(tooltip_stylefont);
		tooltip_stylefont.setAttribute("render_as_html", "true");
		Element tooltip_stylefmt = new Element("format");
		tooltip_style.addContent(tooltip_stylefmt);
		tooltip_stylefmt.setText("{%Name}{enabled:false}:{%Value}{numDecimals:"
				+ this.numDecimals + "}");
		Element tooltip_stylebg = new Element("background");
		tooltip_style.addContent(tooltip_stylebg);
		Element bgcorners = new Element("corners");
		tooltip_stylebg.addContent(bgcorners);
		tooltip_stylebg.setAttribute("type", "Rounded");
		tooltip_stylebg.setAttribute("all", "3");

		Element bginside_margin = new Element("inside_margin");
		tooltip_stylebg.addContent(bginside_margin);
		tooltip_stylebg.setAttribute("left", "5");
		tooltip_stylebg.setAttribute("top", "2");
		tooltip_stylebg.setAttribute("right", "5");
		tooltip_stylebg.setAttribute("bottom", "2");

		// styles end.
		Element data = new Element("data");
		chart.addContent(data);
		/*
		 * <series name="USA" type="Line"> <tooltip enabled="true"
		 * style="idGeneralTooltip" /> <point name="GDP" y="1" /> <point
		 * name="GDP Real Growth Rate" y="0.3666666666666667" /> <point
		 * name="Infant Mortality" y="0.06578947368421052" /> <point name="Life
		 * Expectancy" y="0.9576093653727663" /> <point name="Population"
		 * y="0.22638827767366515" /> <point name="Area" y="0.5390698290165805" />
		 * <point name="Density" y="0.02995156531259858" /> <point
		 * name="Population Growth Rate" y="0.3087248322147651" /> </series>
		 */
		ArrayList list = null;
		Iterator it = hm.entrySet().iterator();
		if (hm.size() > 0) {
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();

				String linename = (String) entry.getKey();
				/** 最小值和最大值过滤掉 */
				if ("minmax".equalsIgnoreCase(linename)) {
                    continue;
                }
				list = (ArrayList) entry.getValue();
				Element series = new Element("series");
				series.setAttribute("name", linename);
				series.setAttribute("type", "Line");
				data.addContent(series);
				Element tooltip = new Element("tooltip");
				series.addContent(tooltip);
				tooltip.setAttribute("enabled", "true");
				tooltip.setAttribute("style", "idGeneralTooltip");

				int j = 0;
				for (j = 0; j < list.size(); j++) {
					CommonData vo = (CommonData) list.get(j);
					String name = vo.getDataName();
					String value = vo.getDataValue();
					Element point = new Element("point");
					if (name.length() > 6 && this.chart_type != 41) {
						point.setAttribute("name", name.substring(0, 6)
								+ "\r\n" + name.substring(6));
					} else {
						point.setAttribute("name", name);
					}

					point.setAttribute("y", value);
					series.addContent(point);
				}
			}
		}
		/** chart_settings */
		Element chart_settings = new Element("chart_settings");
		chart.addContent(chart_settings);
		addBackgroundBorderStyle1(chart_settings);
		// title
		Element title = new Element("title");
		chart_settings.addContent(title);
		title.setAttribute("enabled", "true");
		Element text = new Element("text");
		title.addContent(text);
		text.setText(this.title);
		// axes
		Element axes = new Element("axes");
		chart_settings.addContent(axes);
		Element y_axis = new Element("y_axis");
		axes.addContent(y_axis);
		Element y_axisscale = new Element("scale");
		y_axis.addContent(y_axisscale);
		/** 设置雷达图最小值和最大值 */
		String minmax = (String) hm.get("minmax");
		String smin = "0";
		String smax = "100";
		if (minmax == null || minmax.length() == 0) {
			minmax = "0,100";// 默认值
		}
		String[] minmaxarr = StringUtils.split(minmax, ",");
		if (minmaxarr.length == 2) {
			smin = minmaxarr[0];
			smax = minmaxarr[1];
		}
		y_axisscale.setAttribute("minimum", smin);
		y_axisscale.setAttribute("maximum", smax);
		// end.
		Element x_axis = new Element("x_axis");
		axes.addContent(x_axis);
		Element x_axislbl = new Element("labels");
		x_axis.addContent(x_axislbl);
		x_axislbl.setAttribute("padding", "5");
		// legend
		Element legend = new Element("legend");
		chart_settings.addContent(legend);
		legend.setAttribute("enabled", "true");
		legend.setAttribute("position", "Bottom");
		legend.setAttribute("align", "Center");
		// legend 透明
		if (transparent) {
			Element background = new Element("background");
			background.setAttribute("enabled", "False");
			legend.addContent(background);
		}
		Element legtitle = new Element("title");
		legend.addContent(legtitle);
		legtitle.setAttribute("enabled", "false");

		Element legicon = new Element("icon");
		legend.addContent(legicon);
		legicon.setAttribute("type", "Box");
		// --end.

		try {
			XMLOutputter outputter = new XMLOutputter();
			// Format format0=Format.getPrettyFormat();
			Format format0 = Format.getCompactFormat();
			format0.setEncoding("UTF-8");
			outputter.setFormat(format0);
			buf.append(outputter.outputString(myDocument.getContent()));
			// System.out.println("="+buf.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return buf.toString();
	}

	/**
	 * 输出线xml内容
	 * 
	 * @param map
	 * @return
	 */
	public String outLineXml(HashMap hm,String biDesk) {
		StringBuffer buf = new StringBuffer();
		double sum = 0;
		DecimalFormat df1 = (DecimalFormat) DecimalFormat.getInstance();
		if (this.numDecimals == 0) {
            df1.applyPattern("0");
        } else {
			String fmtstr = StringUtils.rightPad("0.", 2 + this.numDecimals,
			"0");
			df1.applyPattern(fmtstr);
		}
		Element root = new Element("anychart");
		Document myDocument = new Document(root);
		Element settings = new Element("settings");
		root.addContent(settings);
		Element animation = new Element("animation");
		if (this.animation) {
            animation.setAttribute("enabled", "True");
        } else {
            animation.setAttribute("enabled", "False");
        }
		settings.addContent(animation);

		Element margin = new Element("margin");
		root.addContent(margin);
		margin.setAttribute("all", "0");

		Element context_menu = new Element("context_menu");
		settings.addContent(context_menu);
		context_menu.setAttribute("version_info", "false");
		context_menu.setAttribute("about_anychart", "false");
		Element save_as_image_item_text = new Element("save_as_image_item_text");
		context_menu.addContent(save_as_image_item_text);
		save_as_image_item_text.setText("保存");
		Element print_chart_item_text = new Element("print_chart_item_text");
		context_menu.addContent(print_chart_item_text);
		print_chart_item_text.setText("打印");

		Element charts = new Element("charts");
		root.addContent(charts);
		Element chart = new Element("chart");
		if (this.chart_type == ChartConstants.CATEGORY_BAR
				|| this.chart_type == ChartConstants.CATEGORY_3DBAR) {
            chart.setAttribute("plot_type", "CategorizedVertical");
        }
		if (this.chart_type == ChartConstants.CATEGORY_HORIZONTAL_BAR
				|| this.chart_type == ChartConstants.CATEGORY_HORIZONTAL_3DBAR) {
            chart.setAttribute("plot_type", "CategorizedHorizontal");
        }
		charts.addContent(chart);

		Element data_plot_settings = new Element("data_plot_settings");
		data_plot_settings.setAttribute("default_series_type", "Line");
		chart.addContent(data_plot_settings);
		if (chart_type == ChartConstants.LINE_VERTICAL3D
				|| chart_type == ChartConstants.LINE_HORIZONTAL3D) {
            data_plot_settings.setAttribute("enable_3d_mode", "true");
        } else {
            data_plot_settings.setAttribute("enable_3d_mode", "false");
        }

		Element line_series = new Element("line_series");
		data_plot_settings.addContent(line_series);
		//label_settings
		Element label_settings = new Element("label_settings");
		line_series.addContent(label_settings);
		label_settings.setAttribute("enabled",this.label_enabled);
		label_settings.setAttribute("mode","Outside");
		label_settings.setAttribute("multi_line_align","CenterTop");//Outside
        //label_settings.addContent(position);
        Element font = new Element("font");
        label_settings.addContent(font);
        //font.setAttribute("color","White");
        font.setAttribute("size",labelfontsize);
        Element lblformat = new Element("format");
        lblformat.setText("{%Value}{numDecimals:0}");
        label_settings.addContent(lblformat);
		// tooltip_settings
		Element tooltip_settings = new Element("tooltip_settings");
		line_series.addContent(tooltip_settings);
		tooltip_settings.setAttribute("enabled", tooltip_enabled);
		// format,小数点位数
		Element format = new Element("format");
		format.setText("{%Name}{enabled:false}:{%Value}{numDecimals:"
				+ this.numDecimals + "}");
		tooltip_settings.addContent(format);
		Element tooltipfont = new Element("font");
		tooltip_settings.addContent(tooltipfont);
		tooltipfont.setAttribute("size", tooltipfontsize);

		Element data = new Element("data");
		chart.addContent(data);
		/*
		 * <series name="Series 1"> <point name="P1" y="3.6" /> <point name="P2"
		 * y="7.1" /> <point name="P3" y="8.5" /> <point name="P4" y="9.2" />
		 * <point name="P5" y="10.1" /> <point name="P6" y="11.6" /> <point
		 * name="P7" y="16.4" /> <point name="P8" y="18.0" />
		 */
		ArrayList list = null;
		Iterator it = hm.entrySet().iterator();
		if (hm.size() > 0) {
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				list = (ArrayList) entry.getValue();
				String linename = (String) entry.getKey();
				Element series = new Element("series");
				series.setAttribute("name", linename);
				data.addContent(series);
				int j = 0;
				for (j = 0; j < list.size(); j++) {
					CommonData vo = (CommonData) list.get(j);
					String name = vo.getDataName();
					String value = vo.getDataValue();
					Element point = new Element("point");
					point.setAttribute("name", name);
					point.setAttribute("y", value);
					sum += Double.parseDouble(value);
					series.addContent(point);
				}
				if(this.title.contains("平均")){
					sum = (sum*1.0)/list.size();
				}
			}
		}

		Element chart_settings = new Element("chart_settings");
		chart.addContent(chart_settings);
		addBackgroundBorderStyle1(chart_settings);
		Element title = new Element("title");
		chart_settings.addContent(title);
		title.setAttribute("enabled", "true");
		Element text = new Element("text");
		title.addContent(text);
		// text.setText(this.title); tianye add 判断是否显示总数代码
		if ("false".equalsIgnoreCase(isneedsum)) {
            text.setText(this.title);
        } else{
			if (StringUtils.isEmpty(biDesk) || !StringUtils.equals(biDesk, "true")) {
				text.setText(this.title + "(" + df1.format(sum) + ")");
			}else{
				text.setText(this.title);
			}
		}

		Element legend = new Element("legend");
		chart_settings.addContent(legend);
		if (StringUtils.isEmpty(biDesk) || !StringUtils.equals(biDesk, "true")) {
			legend.setAttribute("enabled", "true");
		}else{
			legend.setAttribute("enabled", "false");
		}
		legend.setAttribute("position", "right");
		// legend.setAttribute("align","Spread");
		legend.setAttribute("ignore_auto_item", "true");

		// 透明化 guodd 2014-12-22
		if (transparent) {
			Element background = new Element("background");
			background.setAttribute("enabled", "False");
			legend.addContent(background);
		}

		Element legtitle = new Element("title");
		legend.addContent(legtitle);
		// <title enabled="true">
		// <text>Products Sales</text>
		// </title>
		legtitle.setAttribute("enabled", "false");
		Element legfont = new Element("font");
		legend.addContent(legfont);
		legfont.setAttribute("size", legfontsize);

		Element legformat = new Element("format");
		// <format>{%Icon} {%Name} ({%YValue})</format>
		legend.addContent(legformat);
		legformat.setText("{%Icon} {%Name}{enabled:false}");
		Element items = new Element("items");
		legend.addContent(items);
		Element item = new Element("item");
		items.addContent(item);
		item.setAttribute("source", "series");
		Element axes = new Element("axes");
		chart_settings.addContent(axes);
		Element y_axis = new Element("y_axis");
		axes.addContent(y_axis);
		Element y_axistitle = new Element("title");
		y_axis.addContent(y_axistitle);
		Element y_axistext = new Element("text");
		y_axistitle.addContent(y_axistext);
		y_axistext.setText("y");
		y_axistitle.setAttribute("enabled", "false");

		Element y_scale = new Element("scale");
		y_axis.addContent(y_scale);
		//y非自动适应走原逻辑
		if("false".equals(yAxisAuto)) {
            y_scale.setAttribute("minimum", String.valueOf(this.minvalue));
        }

		Element labels = new Element("labels");
		y_axis.addContent(labels);
		labels.setAttribute("align", "Inside");

		Element labelsfmt = new Element("format");
		if (sum <= 1) {
            labelsfmt.setText("{%Value}{numDecimals:2}");
        } else {
            labelsfmt.setText("{%Value}{numDecimals:" + this.numDecimals + "}");
        }
		labels.addContent(labelsfmt);

		Element x_axis = new Element("x_axis");
		axes.addContent(x_axis);
		// x_axis.setAttribute("position","Normal");
		/* 坐标旋转 */
		if (xangle != 0) {
			Element xlabels = new Element("labels");
			xlabels.setAttribute("display_mode", "Normal");
			if (this.xangle == maxXangle) {
				xangle = 0;
				xlabels.setAttribute("width", "15");
			}else {
                xlabels.setAttribute("width", "100");
            }
			xlabels.setAttribute("rotation", String.valueOf(xangle));
			x_axis.addContent(xlabels);
		}
		// <x_axis position="Normal">
		// <scale inverted="False" />
		// </x_axis>

		Element x_axistitle = new Element("title");
		x_axis.addContent(x_axistitle);
		x_axistitle.setAttribute("enabled", "false");
		Element x_axistext = new Element("text");
		x_axistitle.addContent(x_axistext);
		x_axistext.setText("x");

		try {
			XMLOutputter outputter = new XMLOutputter();
			// Format format0=Format.getPrettyFormat();
			Format format0 = Format.getCompactFormat();
			format0.setEncoding("UTF-8");
			outputter.setFormat(format0);
			buf.append(outputter.outputString(myDocument.getContent()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return buf.toString();
	}

	/**
	 * 分组柱状图
	 * 
	 * @param list
	 * @return
	 */
	public String outGalleryBarChartXml(ArrayList list) {
		StringBuffer buf = new StringBuffer();
		double sum = 0;
		double ymax = 0;
		DecimalFormat df1 = (DecimalFormat) DecimalFormat.getInstance();
		if (this.numDecimals == 0) {
            df1.applyPattern("0");
        } else {
			String fmtstr = StringUtils.rightPad("0.", 2 + this.numDecimals,
			"0");
			df1.applyPattern(fmtstr);
		}
		Element root = new Element("anychart");
		Document myDocument = new Document(root);
		Element settings = new Element("settings");
		root.addContent(settings);
		setNo_data(settings);
		Element animation = new Element("animation");
		if (this.animation) {
            animation.setAttribute("enabled", "True");
        } else {
            animation.setAttribute("enabled", "False");
        }
		settings.addContent(animation);

		Element margin = new Element("margin");
		root.addContent(margin);
		margin.setAttribute("all", "0");

		Element context_menu = new Element("context_menu");
		settings.addContent(context_menu);
		context_menu.setAttribute("version_info", "false");
		context_menu.setAttribute("about_anychart", "false");
		Element save_as_image_item_text = new Element("save_as_image_item_text");
		context_menu.addContent(save_as_image_item_text);
		save_as_image_item_text.setText("保存");
		Element print_chart_item_text = new Element("print_chart_item_text");
		context_menu.addContent(print_chart_item_text);
		print_chart_item_text.setText("打印");

		Element charts = new Element("charts");
		root.addContent(charts);
		Element chart = new Element("chart");
		if (this.chart_type == ChartConstants.CATEGORY_BAR
				|| this.chart_type == ChartConstants.CATEGORY_3DBAR) {
            chart.setAttribute("plot_type", "CategorizedVertical");
        }
		if (this.chart_type == ChartConstants.CATEGORY_HORIZONTAL_BAR
				|| this.chart_type == ChartConstants.CATEGORY_HORIZONTAL_3DBAR) {
            chart.setAttribute("plot_type", "CategorizedHorizontal");
        }
		charts.addContent(chart);

		Element data_plot_settings = new Element("data_plot_settings");
		data_plot_settings.setAttribute("default_series_type", "Bar");
		chart.addContent(data_plot_settings);
		if (chart_type == ChartConstants.CATEGORY_3DBAR
				|| chart_type == ChartConstants.CATEGORY_HORIZONTAL_3DBAR) {
            data_plot_settings.setAttribute("enable_3d_mode", "true");
        } else {
            data_plot_settings.setAttribute("enable_3d_mode", "false");
        }
		if (list.size() == 1) {
            data_plot_settings.setAttribute("z_aspect", "0.5");
        }
		Element bar_series = new Element("bar_series");
		data_plot_settings.addContent(bar_series);
		bar_series.setAttribute("group_padding", "1");
		bar_series.setAttribute("point_padding", "0.2");
		if (list.size() > 0) {
			LazyDynaBean abean = (LazyDynaBean) list.get(0);
			ArrayList dataList = (ArrayList) abean.get("dataList");
			if (list.size() < 3 && dataList.size() == 1) {
                bar_series.setAttribute("point_width", "100");
            }
		}

		Element label_settings = new Element("label_settings");
		bar_series.addContent(label_settings);
		label_settings.setAttribute("enabled", this.label_enabled);
		Element lblformat = new Element("format");
		lblformat.setText("{%YValue}{numDecimals:" + this.numDecimals + "}");
		label_settings.addContent(lblformat);
		Element lblfont = new Element("font");
		label_settings.addContent(lblfont);
		lblfont.setAttribute("size", "10");

		// tooltip_settings
		Element tooltip_settings = new Element("tooltip_settings");
		bar_series.addContent(tooltip_settings);
		tooltip_settings.setAttribute("enabled", tooltip_enabled);
		// format,小数点位数
		Element format = new Element("format");
		format.setText("{%SeriesName}{enabled:false}:{%Value}{numDecimals:"
				+ this.numDecimals + "}");
		tooltip_settings.addContent(format);
		Element tooltipfont = new Element("font");
		tooltip_settings.addContent(tooltipfont);
		tooltipfont.setAttribute("size", tooltipfontsize);

		Element data = new Element("data");
		chart.addContent(data);
		/** 转向 */
		ArrayList plist = new ArrayList();
		int a = 0;
		int b = 0;// 找dataList最多的数据 2010.11.25 xieguiquan
		// 解决：例子：归档年报，a单位归档上下半年，b仅归档下半年，报表分析，按单位显示，b的归档数据显示不出来。
		for (int j = 0; j < list.size(); j++) {
			LazyDynaBean abean = (LazyDynaBean) list.get(j);
			ArrayList dataList = (ArrayList) abean.get("dataList");
			if (dataList.size() > a) {
				a = dataList.size();
				b = j;
			}

		}
		if (list.size() > 0) {
			LazyDynaBean abean = (LazyDynaBean) list.get(b);
			ArrayList dataList = (ArrayList) abean.get("dataList");
			for (int i = 0; i < dataList.size(); i++) {
				CommonData vo = (CommonData) dataList.get(i);
				String name = vo.getDataName();
				plist.add(name);
			}
		}
		for (int i = 0; i < plist.size(); i++) {
			Element series = new Element("series");
			series.setAttribute("name", (String) plist.get(i));
			data.addContent(series);
			for (int j = 0; j < list.size(); j++) {
				LazyDynaBean abean = (LazyDynaBean) list.get(j);
				String categoryName = (String) abean.get("categoryName");
				ArrayList dataList = (ArrayList) abean.get("dataList");
				if (dataList.size() - 1 < i)// xieguiquan 2010.11.25
					// dataList的第i个值不存在。
					// 报表分析，a单位归档一次，b单位归档两次，图片不显示
                {
                    continue;
                }
				CommonData vo = (CommonData) dataList.get(i);
				String name = vo.getDataName();
				String value = vo.getDataValue();
				Element point = new Element("point");
				// point.setAttribute("name",categoryName);
				point.setAttribute("y", value);
				Element name1 = new Element("name");
				point.addContent(name1);
				name1.setText(categoryName);
				series.addContent(point);
				sum = sum + Double.parseDouble(value);
				/** 设置为和有点难看，但最大好象存在问题 */
				ymax = Math.max(ymax, Double.parseDouble(value));
			}
		}

		Element chart_settings = new Element("chart_settings");
		chart.addContent(chart_settings);
		addBackgroundBorderStyle1(chart_settings);
		Element title = new Element("title");
		chart_settings.addContent(title);
		title.setAttribute("enabled", "true");
		title.setAttribute("position", "bottom");
		Element text = new Element("text");
		title.addContent(text);
		if ("false".equalsIgnoreCase(isneedsum)) {
            text.setText(this.title);
        } else {
            text.setText(this.title + "(" + df1.format(sum) + ")");
        }

		// Element legend = new Element("legend");
		// chart_settings.addContent(legend);
		// legend.setAttribute("enabled","true");
		// legend.setAttribute("position","right");
		// //legend.setAttribute("align","Spread");
		// legend.setAttribute("ignore_auto_item","true");
		//        
		// if(transparent){
		// Element background = new Element("background");
		// background.setAttribute("enabled","False");
		// legend.addContent(background);
		// }
		//        
		// Element legtitle = new Element("title");
		// legend.addContent(legtitle);
		// // <title enabled="true">
		// // <text>Products Sales</text>
		// // </title>
		// legtitle.setAttribute("enabled","false");
		// Element legfont = new Element("font");
		// legend.addContent(legfont);
		// legfont.setAttribute("size","8");
		//        
		// Element legformat = new Element("format");
		// // <format>{%Icon} {%Name} ({%YValue})</format>
		// legend.addContent(legformat);
		// legformat.setText("{%Icon} {%Name}{enabled:false}");
		// Element items = new Element("items");
		// legend.addContent(items);
		// Element item = new Element("item");
		// items.addContent(item);
		// item.setAttribute("source","series");
		Element axes = new Element("axes");
		chart_settings.addContent(axes);
		Element y_axis = new Element("y_axis");
		axes.addContent(y_axis);
		Element y_axistitle = new Element("title");
		y_axis.addContent(y_axistitle);
		Element y_axistext = new Element("text");
		y_axistitle.addContent(y_axistext);
		y_axistext.setText("y");
		y_axistitle.setAttribute("enabled", "false");

		Element labels = new Element("labels");
		y_axis.addContent(labels);
		labels.setAttribute("align", "Inside");
		Element labelsfmt = new Element("format");
		labelsfmt.setText("{%Value}{numDecimals:" + this.numDecimals + "}");
		labels.addContent(labelsfmt);

		/** y座标设置 */
		Element y_scale = new Element("scale");
		y_axis.addContent(y_scale);

		y_scale.setAttribute("type", "Linear");
		if (!(chart_type == ChartConstants.VERTICAL_BAR_3D || chart_type == ChartConstants.HORIZONTAL_BAR_3D)) {
			if (sum == 0) {
                sum = 1;// 平面直方图最大值不能设置为零，负责包js错误49行错误
            }
		}
		/** sum->改成ymax++0.1*ymax */
		if("false".equals(this.yAxisAuto)) {
			if(ymax<=10){
				y_scale.setAttribute("maximum", String.valueOf(ymax + 1));
			}else{
				y_scale.setAttribute("maximum", String.valueOf(ymax + 0.2 * ymax));
			}
			y_scale.setAttribute("minimum", String.valueOf(this.minvalue));
		}
		
		y_scale.setAttribute("type", "Linear");

		Element x_axis = new Element("x_axis");
		axes.addContent(x_axis);
		if (xangle != 0) {
			Element xlabels = new Element("labels");
			xlabels.setAttribute("display_mode", "Normal");
			if (this.xangle >= 75) {
				this.xangle = 0;
				xlabels.setAttribute("width", "15");
			}
			xlabels.setAttribute("rotation", String.valueOf(xangle));
			x_axis.addContent(xlabels);
		}

		Element labels1 = new Element("labels");
		x_axis.addContent(labels1);
		Element font1 = new Element("font");
		labels1.addContent(font1);
		font1.setAttribute("size", "12");
		// labels1.setAttribute("rotation","45");
		Element x_axistitle = new Element("title");
		x_axis.addContent(x_axistitle);
		x_axistitle.setAttribute("enabled", "false");
		Element x_axistext = new Element("text");
		x_axistitle.addContent(x_axistext);
		x_axistext.setText("x");
		try {
			XMLOutputter outputter = new XMLOutputter();
			Format format0 = Format.getPrettyFormat();
			format0.setEncoding("UTF-8");
			outputter.setFormat(format0);
			buf.append(outputter.outputString(myDocument));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 生成仪表盘XML
	 * 
	 * @param gvalue
	 * @param yvalue
	 * @param rvalue
	 * @param cvalue
	 * @return
	 */
	private String outGaugeBoardChartXml(ArrayList minvalue,
			ArrayList maxvalue, ArrayList valves, ArrayList cvalues) {
		StringBuffer buf = new StringBuffer();
		DecimalFormat df1 = (DecimalFormat) DecimalFormat.getInstance();
		if (this.numDecimals == 0) {
            df1.applyPattern("0");
        } else {
			String fmtstr = StringUtils.rightPad("0.", 2 + this.numDecimals,
			"0");
			df1.applyPattern(fmtstr);
		}
		if (minvalue.size() == 0) {
			minvalue.add("0");
		}
		if (maxvalue.size() == 0) {
			maxvalue.add("0");
		}
		if (valves.size() == 1) {
			valves.add("0.5");
			valves.add("1");
			valves.add("0");
		}else if (valves.size() == 0) {
			valves.add("0.5");
			valves.add("1");
			valves.add("0");
			valves.add("0");
		}
		if (cvalues.size() == 0) {
			cvalues.add("0");
		}
		ArrayList newvalves = new ArrayList();
		newvalves.add((String) valves.get(0));
		for (int i = 1; i < valves.size(); i++) {
			if (Double.parseDouble((String) valves.get(i)) > 10000) {
				newvalves
				.add((Double.parseDouble((String) valves.get(i)) / 10000)
						+ "");
			} else {
				newvalves
				.add((Double.parseDouble((String) valves.get(i))) + "");
			}
		}
		valves.removeAll(valves);
		valves = newvalves;

		ArrayList newminvalue = new ArrayList();
		for (int i = 0; i < minvalue.size(); i++) {
			if (Double.parseDouble((String) minvalue.get(i)) > 10000) {
				newminvalue
				.add((Double.parseDouble((String) minvalue.get(i)) / 10000)
						+ "");
			} else {
				newminvalue.add((Double.parseDouble((String) minvalue.get(i)))
						+ "");
			}
		}
		minvalue.removeAll(minvalue);
		minvalue = newminvalue;

		Double max = Double.parseDouble((String) maxvalue.get(0));
		maxvalue.removeAll(maxvalue);
		maxvalue.add(Double.parseDouble((String) valves.get(3)) * max + "");
		Double real = Double.parseDouble((String) valves.get(3));
		ArrayList newmaxvalue = new ArrayList();

		for (int i = 0; i < maxvalue.size(); i++) {
			if (Double.parseDouble((String) maxvalue.get(i)) > 10000) {
				newmaxvalue
				.add((Double.parseDouble((String) maxvalue.get(i)) / 10000)
						+ "");
			} else {
				newmaxvalue.add((Double.parseDouble((String) maxvalue.get(i)))
						+ "");
			}
		}
		maxvalue.removeAll(maxvalue);
		maxvalue = newmaxvalue;
		Element root = new Element("anychart");
		Document myDocument = new Document(root);
		Element settings = new Element("settings");
		root.addContent(settings);
		Element animation = new Element("animation");
		if (this.animation) {
            animation.setAttribute("enabled", "True");
        } else {
            animation.setAttribute("enabled", "False");
        }
		settings.addContent(animation);

		Element margin = new Element("margin");
		root.addContent(margin);
		margin.setAttribute("all", "0");

		Element context_menu = new Element("context_menu");
		settings.addContent(context_menu);
		context_menu.setAttribute("version_info", "false");
		context_menu.setAttribute("about_anychart", "false");
		Element save_as_image_item_text = new Element("save_as_image_item_text");
		context_menu.addContent(save_as_image_item_text);
		save_as_image_item_text.setText("保存");
		Element print_chart_item_text = new Element("print_chart_item_text");
		context_menu.addContent(print_chart_item_text);
		print_chart_item_text.setText("打印");

		Element gauges = new Element("gauges");
		root.addContent(gauges);

		Element gauge = new Element("gauge");
		gauges.addContent(gauge);

		Element gauge_settings = new Element("chart_settings");
		gauge.addContent(gauge_settings);
		addBackgroundBorderStyle1(gauge_settings);
		Element title = new Element("title");
		gauge_settings.addContent(title);
		title.setAttribute("enabled", "true");
		title.setAttribute("position", "bottom");
		Element font1 = new Element("font");
		title.addContent(font1);
		font1.setAttribute("size", "20");
		font1.setAttribute("color", "red");
		Element text = new Element("text");
		title.addContent(text);
		if (((Double.parseDouble((String) ((CommonData) cvalues.get(0))
				.getDataValue()))) > 10000) {
			text.setText(this.title
					+ "("
					+ df1.format((Double
							.parseDouble((String) ((CommonData) cvalues.get(0))
									.getDataValue())) / 10000) + "万" + ")");
		} else {
			text.setText(this.title
					+ "("
					+ df1.format((Double
							.parseDouble((String) ((CommonData) cvalues.get(0))
									.getDataValue()))) + ")");
		}

		Element circular = new Element("circular");
		gauge.addContent(circular);

		Element axis = new Element("axis");
		circular.addContent(axis);
		axis.setAttribute("radius", "50");
		axis.setAttribute("start_angle", "85");
		axis.setAttribute("sweep_angle", "190");
		axis.setAttribute("size", "3");
		Element labels = new Element("labels");
		axis.addContent(labels);
		labels.setAttribute("align", "Center");
		labels.setAttribute("padding", "6");

		Element format = new Element("format");
		labels.addContent(format);
		if (((Double.parseDouble((String) ((CommonData) cvalues.get(0))
				.getDataValue()))) > 10000) {
			format.setText("{%Value}{numDecimals:" + 0/* this.numDecimals */
					+ "}万");
		} else {
			format.setText("{%Value}{numDecimals:" + 0/* this.numDecimals */
					+ "}");
		}

		Element scale = new Element("scale");
		axis.addContent(scale);
		if (maxvalue.size() != 0
				&& (Double.parseDouble((String) (maxvalue.get(0)))) == 0) {
			maxvalue.remove(0);
			maxvalue.add(Math.round((Double
					.parseDouble((String) ((CommonData) cvalues.get(0))
							.getDataValue())))
							+ "");
		}
		scale.setAttribute("minimum", Double.parseDouble((String) (minvalue
				.get(0)))
				+ "");
		scale.setAttribute("maximum", real * max + "");
		scale.setAttribute("major_interval", ((real * max - Double
				.parseDouble((String) (minvalue.get(0)))) / 10)
				+ "");

		Element scale_bar = new Element("scale_bar");
		axis.addContent(scale_bar);
		// Element fill = new Element("fill");
		// fill.setAttribute("color","#292929");
		// scale_bar.addContent(fill);

		Element major_tickmark = new Element("major_tickmark");
		axis.addContent(major_tickmark);
		major_tickmark.setAttribute("align", "Center");
		major_tickmark.setAttribute("length", "0");
		major_tickmark.setAttribute("padding", "0");

		Element minor_tickmark = new Element("minor_tickmark");
		axis.addContent(minor_tickmark);
		minor_tickmark.setAttribute("enabled", "false");

		Element color_ranges = new Element("color_ranges");
		axis.addContent(color_ranges);
		/** 仪表盘的color区域计算 */
		/*
		 * int gangle=Math.round((gvalue/Math.max(rvalue, gvalue))*100); int
		 * yangle=Math.round((yvalue/Math.max(rvalue, gvalue))*100); int
		 * rangle=Math.round((yvalue/Math.max(rvalue, gvalue))*100);
		 */
		for (int i = 0; i < valves.size(); i++) {
			String vcolor = "#00FF00";// 默认绿色
			if (valves.size() == 1
					&& Double.parseDouble((String) (valves.get(1))) == 0) {
				color_ranges.addContent(addcolor(Double
						.parseDouble((String) minvalue.get(0)), Double
						.parseDouble((String) (maxvalue.get(0))), vcolor));
				break;
			}
			switch (i) {
			case 0:
				color_ranges.addContent(addcolor(Double
						.parseDouble((String) minvalue.get(0)), Double
						.parseDouble((String) (valves.get(1)))
						* real, vcolor));
				break;
			case 1:
				vcolor = "#FFFF00";
				color_ranges.addContent(addcolor(Double
						.parseDouble((String) valves.get(1))
						* real, Double.parseDouble((String) (valves.get(2)))
						* real, vcolor));
				break;
			case 2:
				vcolor = "#FF0000";
				color_ranges.addContent(addcolor(Double
						.parseDouble((String) valves.get(2))
						* real, Double.parseDouble((String) (valves.get(3)))
						* real, vcolor));
				break;
			case 3:
				vcolor = "#FF0000";
				break;
			}
			if (i == valves.size() - 1
					&& Double.parseDouble((String) valves.get(i)) != 0) {
				color_ranges.addContent(addcolor(Double
						.parseDouble((String) valves.get(i))
						* real, max * real, "#FF0000"));
			}
		}
		Element frame = new Element("frame");
		circular.addContent(frame);
		Element inner_stroke = new Element("inner_stroke");
		inner_stroke.setAttribute("enabled", "false");
		frame.addContent(inner_stroke);
		Element outer_stroke = new Element("outer_stroke");
		outer_stroke.setAttribute("enabled", "false");
		frame.addContent(outer_stroke);
		Element effects = new Element("effects");
		effects.setAttribute("enabled", "false");
		frame.addContent(effects);
		Element background = new Element("background");
		frame.addContent(background);
		Element framefill = new Element("fill");
		framefill.setAttribute("type", "Gradient");
		background.addContent(framefill);
		Element gradient = new Element("gradient");
		framefill.addContent(gradient);
		gradient.setAttribute("angle", "45");
		Element key = new Element("key");
		gradient.addContent(key);
		key.setAttribute("color", "#FDFDFD");
		key = new Element("key");
		gradient.addContent(key);
		key.setAttribute("color", "#F7F3F4");
		Element border = new Element("border");
		background.addContent(border);
		border.setAttribute("enabled", "true");
		border.setAttribute("color", "#A9A9A9");

		Element pointers = new Element("pointers");
		circular.addContent(pointers);
		Element pointer = new Element("pointer");
		pointers.addContent(pointer);
		if (((Double.parseDouble((String) ((CommonData) cvalues.get(0))
				.getDataValue()))) > 10000) {
			pointer.setAttribute("value", String.valueOf(((Double
					.parseDouble((String) ((CommonData) cvalues.get(0))
							.getDataValue()))) / 10000));
		} else {
			pointer.setAttribute("value", String.valueOf(((Double
					.parseDouble((String) ((CommonData) cvalues.get(0))
							.getDataValue())))));
		}
		Element plabel = new Element("label");
		pointer.addContent(plabel);
		plabel.setAttribute("enabled", "true"); // 指针是的值不显示
		Element lbackground = new Element("background");
		plabel.addContent(lbackground);
		lbackground.setAttribute("enabled", "false");
		Element lblformat = new Element("format");
		plabel.addContent(lblformat);
		if (((Double.parseDouble((String) ((CommonData) cvalues.get(0))
				.getDataValue()))) > 10000) {
			lblformat.setText("{%Value}{numDecimals:" + this.numDecimals + "}"
					+ "万");
		} else {
			lblformat.setText("{%Value}{numDecimals:" + this.numDecimals + "}"
					+ "");
		}

		Element needle_pointer_style = new Element("needle_pointer_style");
		pointer.addContent(needle_pointer_style);
		needle_pointer_style.setAttribute("thickness", "7");
		needle_pointer_style.setAttribute("point_thickness", "5");
		needle_pointer_style.setAttribute("point_radius", "3");
		Element needlefill = new Element("fill");
		needle_pointer_style.addContent(needlefill);
		needlefill.setAttribute("color", "Rgb(230,230,230)");
		Element pborder = new Element("border");
		needle_pointer_style.addContent(pborder);
		pborder.setAttribute("color", "Black");
		pborder.setAttribute("opacity", "0.7");
		Element peffects = new Element("effects");
		needle_pointer_style.addContent(peffects);
		peffects.setAttribute("enabled", "true");
		Element bevel = new Element("bevel");
		peffects.addContent(bevel);
		bevel.setAttribute("enabled", "true");
		bevel.setAttribute("distance", "2");
		bevel.setAttribute("shadow_opacity", "0.6");
		bevel.setAttribute("highlight_opacity", "0.6");
		Element drop_shadow = new Element("drop_shadow");
		peffects.addContent(drop_shadow);
		drop_shadow.setAttribute("enabled", "true");
		drop_shadow.setAttribute("distance", "1");
		drop_shadow.setAttribute("blur_x", "1");
		drop_shadow.setAttribute("blur_y", "1");
		drop_shadow.setAttribute("opacity", "0.4");

		Element cap = new Element("cap");
		needle_pointer_style.addContent(cap);
		Element capbg = new Element("background");
		cap.addContent(capbg);
		Element capborder = new Element("border");
		capbg.addContent(capborder);
		capborder.setAttribute("color", "#D3D2CC");
		capborder.setAttribute("opacity", "0.9");
		Element capfill = new Element("fill");
		capbg.addContent(capfill);
		capfill.setAttribute("type", "Gradient");
		Element capgradient = new Element("gradient");
		capfill.addContent(capgradient);
		capgradient.setAttribute("type", "Linear");
		capgradient.setAttribute("angle", "45");
		Element capkey = new Element("key");
		capgradient.addContent(capkey);
		capkey.setAttribute("color", "#FDFDFD");
		capkey = new Element("key");
		capgradient.addContent(capkey);
		capkey.setAttribute("color", "#F7F3F4");

		Element capeffects = new Element("effects");
		cap.addContent(capeffects);
		capeffects.setAttribute("enabled", "true");

		Element capbevel = new Element("bevel");
		capeffects.addContent(capbevel);

		capbevel.setAttribute("enabled", "true");
		capbevel.setAttribute("distance", "2");
		capbevel.setAttribute("shadow_opacity", "0.6");
		capbevel.setAttribute("highlight_opacity", "0.6");

		Element capdrop_shadow = new Element("drop_shadow");
		capeffects.addContent(capdrop_shadow);
		capdrop_shadow.setAttribute("enabled", "true");
		capdrop_shadow.setAttribute("distance", "1");
		capdrop_shadow.setAttribute("blur_x", "1");
		capdrop_shadow.setAttribute("blur_y", "1");
		capdrop_shadow.setAttribute("opacity", "0.4");

		Element panimation = new Element("animation");
		pointer.addContent(panimation);
		panimation.setAttribute("enabled", "true");
		panimation.setAttribute("start_time", "0");
		panimation.setAttribute("duration", "0.5");
		panimation.setAttribute("interpolation_type", "Bounce");

		try {
			XMLOutputter outputter = new XMLOutputter();
			Format format0 = Format.getPrettyFormat();
			format0.setEncoding("UTF-8");
			outputter.setFormat(format0);
			buf.append(outputter.outputString(myDocument));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// System.out.println(buf.toString());
		return buf.toString();
	}

	/**
	 * 创建仪表盘color区域 <color_range start="0" end="30" align="Inside"
	 * start_size="60" end_size="60" padding="6" color="Green"> <border
	 * enabled="true" color="Black" opacity="0.4"/> <label enabled="true"
	 * align="Inside" padding="34"> <format>Poor</format> <position
	 * valign="Center" halign="Center"/> <font bold="true" size="11"/> </label>
	 * <fill opacity="0.6"/> </color_range>
	 * 
	 * @param angle
	 * @return
	 */
	private Element addcolor(double start, double end, String color) {
		Element color_range = new Element("color_range");
		color_range.setAttribute("start", String.valueOf(start));
		color_range.setAttribute("end", String.valueOf(end));
		color_range.setAttribute("align", "Inside");
		color_range.setAttribute("start_size", "60");
		color_range.setAttribute("end_size", "60");
		color_range.setAttribute("padding", "6");
		color_range.setAttribute("color", color);
		Element border = new Element("border");
		color_range.addContent(border);
		border.setAttribute("enabled", "true");
		border.setAttribute("color", "Black");
		border.setAttribute("opacity", "0.4");
		Element label = new Element("label");
		color_range.addContent(label);
		label.setAttribute("enabled", "true");
		label.setAttribute("align", "Inside");
		label.setAttribute("padding", "34");

		Element format = new Element("format");
		label.addContent(format);
		if (end != 0) {
			// format.addContent(String.valueOf(Math.round(start))+"万"+"~"+String.valueOf(Math.round(end))+"万");
		}

		Element position = new Element("position");
		label.addContent(position);
		position.setAttribute("valign", "Center");
		position.setAttribute("halign", "Center");
		Element font = new Element("font");
		label.addContent(font);
		font.setAttribute("size", "11");
		font.setAttribute("halign", "Center");
		Element fill = new Element("fill");
		color_range.addContent(fill);
		Element gradient = new Element("gradient");
		fill.addContent(gradient);
		fill.setAttribute("type", "gradient");
		fill.setAttribute("opacity", "0.6");
		gradient.setAttribute("angle", "15");
		Element key = new Element("key");
		Element key2 = new Element("key");
		gradient.addContent(key);
		gradient.addContent(key2);
		key.setAttribute("color", color);
		key.setAttribute("position", "0");
		key2.setAttribute("color", color);
		key2.setAttribute("position", "1");

		return color_range;
	}

	/**
	 * 生成温度计XML
	 * 
	 * @param cvalues
	 * @return
	 */
	private String outThermometerChartXml(ArrayList valves, ArrayList cvalues,
			String notem) {
		StringBuffer buf = new StringBuffer();
		DecimalFormat df1 = (DecimalFormat) DecimalFormat.getInstance();
		if (this.numDecimals == 0) {
            df1.applyPattern("0");
        } else {
			String fmtstr = StringUtils.rightPad("0.", 2 + this.numDecimals,
			"0");
			df1.applyPattern(fmtstr);
		}
		Element root = new Element("anychart");
		Document myDocument = new Document(root);

		setCommonElement(root);

		Element gauges = new Element("gauges");
		root.addContent(gauges);

		Element gauge = new Element("gauge");
		gauges.addContent(gauge);

		Element gauge_settings = new Element("chart_settings");
		gauge.addContent(gauge_settings);
		addBackgroundBorderStyle(gauge_settings);
		Element title = new Element("title");
		gauge_settings.addContent(title);
		title.setAttribute("enabled", "true");
		title.setAttribute("position", "bottom");
		Element text = new Element("text");
		title.addContent(text); 
		if(Double.parseDouble(((CommonData) cvalues.get(0)).getDataValue())>1.0){
			text.setText(this.title+ "("+ df1.format(Double.parseDouble(((CommonData) cvalues.get(0)).getDataValue())/100) + ")");
		}else{
			text.setText(this.title+ "("+ df1.format(Double.parseDouble(((CommonData) cvalues.get(0)).getDataValue())) + ")");
		}


		// 图形属性
		Element linear = new Element("linear");
		gauge.addContent(linear);
		linear.setAttribute("orientation", "vertical");

		// ---------------------------------------->
		Element axis = new Element("axis");
		linear.addContent(axis);
		axis.setAttribute("size", "12");
		String position = "50";
		if (cvalues.size() > 1) {
			position = 100 / (cvalues.size() + 1) + "";
		}
		axis.setAttribute("position", position);

		Element labels = new Element("labels");
		axis.addContent(labels);
		labels.setAttribute("align", "Outside");
		labels.setAttribute("padding", "6");

		Element format = new Element("format");
		labels.addContent(format);
		format.setText("{%Value}{numDecimals:" + 1/* this.numDecimals */+ "}");

		// <scale minimum="0" maximum="1" major_interval="0.1"
		// minor_interval="2" />

		Element scale = new Element("scale");
		axis.addContent(scale);
		scale.setAttribute("minimum", "0");
		scale.setAttribute("maximum", "1");
		scale.setAttribute("major_interval", "0.1");
		scale.setAttribute("minor_interval", "2");

		Element scale_bar = new Element("scale_bar");
		axis.addContent(scale_bar);
		Element fill = new Element("fill");
		fill.setAttribute("color", "#292929");
		scale_bar.addContent(fill);

		Element major_tickmark = new Element("major_tickmark");
		axis.addContent(major_tickmark);
		major_tickmark.setAttribute("align", "Center");
		major_tickmark.setAttribute("length", "10");
		major_tickmark.setAttribute("padding", "0");

		Element minor_tickmark = new Element("minor_tickmark");
		axis.addContent(minor_tickmark);
		minor_tickmark.setAttribute("enabled", "true");

		Element pointers = new Element("pointers");
		linear.addContent(pointers);
		Element pointer = new Element("pointer");
		pointers.addContent(pointer);
		pointer.setAttribute("type", "Thermometer");
		if(Double.parseDouble(((CommonData) cvalues.get(0)).getDataValue())>1.0){
			pointer.setAttribute("value", Double.parseDouble(((CommonData) cvalues.get(0)).getDataValue())/100+"");
		}else{
			pointer.setAttribute("value", Double.parseDouble(((CommonData) cvalues.get(0)).getDataValue())+"");
		}
		pointer.setAttribute("color", "red");
		Element markpointer = new Element("pointer");
		pointers.addContent(markpointer);
		markpointer.setAttribute("type", "Marker");
		markpointer.setAttribute("value", (String) valves.get(0) == null ? "0"
				: (String) valves.get(0));
		markpointer.setAttribute("color", "red");

		Element marker_pointer_style = new Element("marker_pointer_style");
		markpointer.setContent(marker_pointer_style);
		marker_pointer_style.setAttribute("width", "6");
		marker_pointer_style.setAttribute("height", "6");

		// ------------------------------------<

		if (cvalues.size() > 1) {
			Element extra_axes = new Element("extra_axes");
			for (int i = 1; i < cvalues.size(); i++) {
				Element axis1 = new Element("axis");
				linear.addContent(extra_axes);
				extra_axes.addContent(axis1);
				axis1.setAttribute("size", "5");
				axis1.setAttribute("position", Double.parseDouble(position) * 2
						+ "");
				axis1.setAttribute("name", "axis" + i);

				Element labels1 = new Element("labels");
				axis1.addContent(labels1);
				labels1.setAttribute("align", "Outside");
				labels1.setAttribute("padding", "6");

				Element format1 = new Element("format");
				labels1.addContent(format1);
				format1.setText("{%Value}{numDecimals:" + 1/* this.numDecimals */
						+ "}");

				// <scale minimum="0" maximum="1" major_interval="0.1"
				// minor_interval="2" />

				Element scale1 = new Element("scale");
				axis1.addContent(scale1);
				scale1.setAttribute("minimum", "0");
				scale1.setAttribute("maximum", "1");
				scale1.setAttribute("major_interval", "0.1");
				scale1.setAttribute("minor_interval", "2");

				Element scale_bar1 = new Element("scale_bar");
				axis1.addContent(scale_bar1);
				Element fill1 = new Element("fill");
				fill1.setAttribute("color", "#292929");
				scale_bar1.addContent(fill1);

				Element major_tickmark1 = new Element("major_tickmark");
				axis1.addContent(major_tickmark1);
				major_tickmark1.setAttribute("align", "Center");
				major_tickmark1.setAttribute("length", "6");
				major_tickmark1.setAttribute("padding", "0");

				Element minor_tickmark1 = new Element("minor_tickmark");
				axis1.addContent(minor_tickmark1);
				minor_tickmark1.setAttribute("enabled", "true");

				Element pointer1 = new Element("pointer");
				pointers.addContent(pointer1);
				if ("0".equals(notem)) {
					pointer1.setAttribute("type", "bar");
				} else {
					pointer1.setAttribute("type", "Thermometer");
				}

				pointer1.setAttribute("axis", "axis" + i);
				if(Double.parseDouble(((CommonData) cvalues.get(i)).getDataValue())>1.0){
					pointer1.setAttribute("value", Double.parseDouble(((CommonData) cvalues.get(i)).getDataValue())/100+"");
				}else{
					pointer1.setAttribute("value", ((CommonData) cvalues.get(i)).getDataValue());
				}

				pointer1.setAttribute("color", "red");

				Element pointer2 = new Element("pointer");
				pointers.addContent(pointer2);
				pointer2.setAttribute("type", "Marker");
				pointer2.setAttribute("axis", "axis" + i);
				pointer2.setAttribute("value",
						(String) valves.get(0) == null ? "0" : (String) valves
								.get(0));
				pointer2.setAttribute("color", "red");

				Element marker_pointer_style1 = new Element(
				"marker_pointer_style");
				pointer2.setContent(marker_pointer_style1);
				marker_pointer_style1.setAttribute("width", "4");
				marker_pointer_style1.setAttribute("height", "4");

			}
		}
		try {
			XMLOutputter outputter = new XMLOutputter();
			Format format0 = Format.getPrettyFormat();
			format0.setEncoding("UTF-8");
			outputter.setFormat(format0);
			buf.append(outputter.outputString(myDocument));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// System.out.println(buf.toString());
		return buf.toString();
	}

	/**
	 * 生成双刻度仪表盘XML
	 * 
	 * @param cvalues
	 * @return
	 */
	private String outCircularBindingChartXml(ArrayList valves,
			ArrayList cvalues) {
		StringBuffer buf = new StringBuffer();
		DecimalFormat df1 = (DecimalFormat) DecimalFormat.getInstance();
		if (this.numDecimals == 0) {
            df1.applyPattern("0");
        } else {
			String fmtstr = StringUtils.rightPad("0.", 2 + this.numDecimals,
			"0");
			df1.applyPattern(fmtstr);
		}

		Element root = new Element("anychart");
		Document myDocument = new Document(root);

		setCommonElement(root);

		Element gauges = new Element("gauges");
		root.addContent(gauges);

		Element gauge = new Element("gauge");
		gauges.addContent(gauge);

		Element gauge_settings = new Element("chart_settings");
		gauge.addContent(gauge_settings);
		addBackgroundBorderStyle1(gauge_settings);
		Element title = new Element("title");
		gauge_settings.addContent(title);
		title.setAttribute("enabled", "true");
		title.setAttribute("position", "bottom");
		Element font1 = new Element("font");
		title.addContent(font1);
		font1.setAttribute("size", "12");
		font1.setAttribute("color", "red");
		Element text = new Element("text");
		title.addContent(text);
		if (((Double.parseDouble((String) ((CommonData) cvalues.get(0))
				.getDataValue()))) > 10000) {
			text.setText(this.title
					+ "("
							+ df1.format((Double
									.parseDouble((String) ((CommonData) cvalues.get(0))
											.getDataValue())) / 10000) + "万" + ")");
		} else {
			text.setText(this.title
					+ "("
					+ df1.format((Double
							.parseDouble((String) ((CommonData) cvalues.get(0))
									.getDataValue()))) + ")");
		}

		Element chart_background = new Element("chart_background");
		gauge.addContent(chart_background);

		chart_background.setAttribute("enabled", "true");
		Element corners = new Element("corners");
		chart_background.addContent(corners);
		corners.setAttribute("type", "Rounded");
		corners.setAttribute("all", "15");
		// 图形属性
		Element circular = new Element("circular");
		gauge.addContent(circular);
		Element margin = new Element("margin");
		circular.addContent(margin);
		// ---------------------------------------->
		Element axis = new Element("axis");
		circular.addContent(axis);

		valves.add("0");
		valves.add("0");
		valves.add("0");

		Element scale = new Element("scale");
		axis.addContent(scale);
		scale.setAttribute("minimum", (String) valves.get(1));
		Double maxval = Double.parseDouble((String) valves.get(3));
		if (maxval == 0.0) {
			maxval = (Double.parseDouble(((CommonData) cvalues.get(0))
					.getDataValue()));
		}
		if(maxval>10000){
			maxval=maxval/10000;
		}
		scale.setAttribute("maximum", maxval
				* Double.parseDouble((String) valves.get(2)) + "");
		String ma_int = (maxval * Double.parseDouble((String) valves.get(2)))
		/ 5 + "";
		scale.setAttribute("major_interval", ma_int);

		Element scale_bar = new Element("scale_bar");
		axis.addContent(scale_bar);
		scale_bar.setAttribute("enabled", "true");
		scale_bar.setAttribute("size", "10");

		Element fill = new Element("fill");
		scale_bar.addContent(fill);
		fill.setAttribute("enabled", "true");
		fill.setAttribute("type", "solid");
		fill.setAttribute("color", "#7AA4E0");
		fill.setAttribute("opacity", "0.5");

		Element labels = new Element("labels");
		axis.addContent(labels);
		labels.setAttribute("enabled", "true");
		labels.setAttribute("rotate_circular", "true");
		labels.setAttribute("auto_orientation", "true");
		labels.setAttribute("align", "outside");

		Element font = new Element("font");
		font.setAttribute("size", "12");

		Element format = new Element("format");
		labels.addContent(format);
		if(Double.parseDouble((String) valves.get(3))>10000) {
            format.setText("{%Value}{numDecimals:" + 2/* this.numDecimals */+ "}万");
        } else {
            format.setText("{%Value}{numDecimals:" + 2/* this.numDecimals */+ "}");
        }


		Element major_tickmark = new Element("major_tickmark");
		axis.addContent(major_tickmark);
		major_tickmark.setAttribute("enabled", "true");

		Element minor_tickmark = new Element("minor_tickmark");
		axis.addContent(minor_tickmark);
		minor_tickmark.setAttribute("enabled", "true");

		// Element extra_axes = new Element("extra_axes");
		// circular.addContent(extra_axes);
		// Element axis1 = new Element("axis");
		// axis1.setAttribute("name","axis1");
		// axis1.setAttribute("radius","45");
		// extra_axes.addContent(axis1);
		//        
		// Element scale1 = new Element("scale");
		// axis1.addContent(scale1);
		// scale1.setAttribute("minimum","0");
		// scale1.setAttribute("maximum","100");
		// scale1.setAttribute("major_interval","25");
		//        
		// Element scale_bar1 = new Element("scale_bar");
		// axis1.addContent(scale_bar1);
		// scale_bar1.setAttribute("enable","true");
		// scale_bar1.setAttribute("size","10");
		//        
		// Element fill1 = new Element("fill");
		// scale_bar1.addContent(fill1);
		// fill1.setAttribute("enabled","true");
		// fill1.setAttribute("type","solid");
		// fill1.setAttribute("color","#66CCCC");
		// fill1.setAttribute("opacity","0.5");
		//        
		// Element labels1 = new Element("labels");
		// axis1.addContent(labels1);
		// labels1.setAttribute("enabled","true");
		// labels1.setAttribute("rotate_circular","true");
		// labels1.setAttribute("auto_orientation","true");
		// labels1.setAttribute("align","inside");

		// Element font1 = new Element("font");
		// font1.setAttribute("size","12");
		//        
		// Element format1 = new Element("format");
		// labels1.addContent(format1);
		// format1.setText("{%Value}{numDecimals:"+0/*this.numDecimals*/+"}"+"%");
		//        
		// Element major_tickmark1 = new Element("major_tickmark");
		// axis1.addContent(major_tickmark1);
		// major_tickmark1.setAttribute("enable","true");
		//        
		// Element minor_tickmark1 = new Element("minor_tickmark");
		// axis1.addContent(minor_tickmark1);
		// minor_tickmark1.setAttribute("enabled","true");

		Element pointers = new Element("pointers");
		circular.addContent(pointers);

		CommonData cd = new CommonData();
		cd.setDataValue("0");
		CommonData cd1 = new CommonData();
		cd1.setDataValue("0");
		cvalues.add(cd);
		cvalues.add(cd1);

		Element markpointer = new Element("pointer");
		pointers.addContent(markpointer);
		markpointer.setAttribute("type", "Marker");
		if (((Double.parseDouble((String) ((CommonData) cvalues.get(0))
				.getDataValue()))) > 10000) {
			markpointer.setAttribute("value", ((Double.parseDouble((String) ((CommonData) cvalues.get(0))
					.getDataValue())))/10000+"");
		}else{
			markpointer.setAttribute("value", ((CommonData) cvalues.get(0))
					.getDataValue() == null ? "0" : ((CommonData) cvalues.get(0))
							.getDataValue());
		}
		markpointer.setAttribute("color", "red");

		Element marker_pointer_style = new Element("marker_pointer_style");
		markpointer.setContent(marker_pointer_style);
		marker_pointer_style.setAttribute("shape", "TriangleUp");
		marker_pointer_style.setAttribute("align", "outside");
		marker_pointer_style.setAttribute("width", "10");
		marker_pointer_style.setAttribute("height", "10");

		// Element markpointer1 = new Element("pointer");
		// pointers.addContent(markpointer1);
		// markpointer1.setAttribute("axis","axis1");
		// markpointer1.setAttribute("type","Marker");
		// markpointer1.setAttribute("value",((CommonData)cvalues.get(1)).getDataValue()==null?"0":((CommonData)cvalues.get(1)).getDataValue());
		// markpointer1.setAttribute("color","yellow");

		// Element marker_pointer_style1 = new Element("marker_pointer_style");
		// markpointer1.setContent(marker_pointer_style1);
		// marker_pointer_style1.setAttribute("shape","TriangleUp");
		// marker_pointer_style1.setAttribute("align","Inside");
		// marker_pointer_style1.setAttribute("width","5");
		// marker_pointer_style1.setAttribute("height","5");

		Element frame = new Element("frame");
		circular.addContent(frame);
		frame.setAttribute("enabled", "false");

		// ------------------------------------<

		try {
			XMLOutputter outputter = new XMLOutputter();
			Format format0 = Format.getPrettyFormat();
			format0.setEncoding("UTF-8");
			outputter.setFormat(format0);
			buf.append(outputter.outputString(myDocument));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
//		System.out.println(buf.toString());
		return buf.toString();
	}

	/**
	 * 设置统计图公共属性
	 * 
	 * @param root
	 */
	private void setCommonElement(Element root) {
		Element margin = new Element("margin");// 设置边距
		root.addContent(margin);
		margin.setAttribute("all", "0");

		Element settings = new Element("settings");
		root.addContent(settings);
		Element animation = new Element("animation");// 展现统计图时是否动画
		if (this.animation) {
            animation.setAttribute("enabled", "True");
        } else {
            animation.setAttribute("enabled", "False");
        }
		settings.addContent(animation);

		Element context_menu = new Element("context_menu");
		settings.addContent(context_menu);
		context_menu.setAttribute("version_info", "false");
		context_menu.setAttribute("about_anychart", "false");
		Element save_as_image_item_text = new Element("save_as_image_item_text");
		context_menu.addContent(save_as_image_item_text);
		save_as_image_item_text.setText("保存");
		Element print_chart_item_text = new Element("print_chart_item_text");
		context_menu.addContent(print_chart_item_text);
		print_chart_item_text.setText("打印");
	}

	/* 标识：2580 立体直方图下方红色方框标示占用空间过大,去除标示内容 xiaoyun 2014-6-18 start */
	/*
	 * 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全(增加x-轴总字数参数，用于计算是否需要旋转) xiaoyun
	 * 2014-7-9 start
	 */
	/**
	 * @param list
	 * @param biDesk
	 *            是否为总裁桌面发来的请求
	 * @param total
	 *            x-轴总字数
	 * @return
	 */
	public String outBarXml(ArrayList list, String biDesk, String total)
	/*
	 * 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全(增加x-轴总字数参数，用于计算是否需要旋转) xiaoyun
	 * 2014-7-9 end
	 */
	// private String outBarXml(ArrayList list)
	/* 标识：2580 立体直方图下方红色方框标示占用空间过大,去除标示内容 xiaoyun 2014-6-18 end */
	{

		StringBuffer buf = new StringBuffer();
		double sum = 0;
		double ymax = 0;
		DecimalFormat df1 = (DecimalFormat) DecimalFormat.getInstance();
		if (this.numDecimals == 0) {
            df1.applyPattern("0");
        } else {
			String fmtstr = StringUtils.rightPad("0.", 2 + this.numDecimals,
			"0");
			df1.applyPattern(fmtstr);
		}
		Element root = new Element("anychart");
		Document myDocument = new Document(root);
		Element settings = new Element("settings");
		root.addContent(settings);
		Element animation = new Element("animation");
		if (this.animation) {
            animation.setAttribute("enabled", "True");
        } else {
            animation.setAttribute("enabled", "False");
        }
		settings.addContent(animation);

		Element margin = new Element("margin");
		root.addContent(margin);
		margin.setAttribute("all", "0");

		Element context_menu = new Element("context_menu");
		settings.addContent(context_menu);
		context_menu.setAttribute("version_info", "false");
		context_menu.setAttribute("about_anychart", "false");
		Element save_as_image_item_text = new Element("save_as_image_item_text");
		context_menu.addContent(save_as_image_item_text);
		save_as_image_item_text.setText("保存");
		Element print_chart_item_text = new Element("print_chart_item_text");
		context_menu.addContent(print_chart_item_text);
		print_chart_item_text.setText("打印");

		Element charts = new Element("charts");
		root.addContent(charts);
		Element chart = new Element("chart");
		if (this.chart_type == ChartConstants.VERTICAL_BAR
				|| this.chart_type == ChartConstants.VERTICAL_BAR_3D) {
            chart.setAttribute("plot_type", "CategorizedVertical");
        }
		if (this.chart_type == ChartConstants.HORIZONTAL_BAR
				|| this.chart_type == ChartConstants.HORIZONTAL_BAR_3D) {
            chart.setAttribute("plot_type", "CategorizedHorizontal");
        }
		charts.addContent(chart);

		Element data_plot_settings = new Element("data_plot_settings");
		data_plot_settings.setAttribute("default_series_type", "Bar");
		chart.addContent(data_plot_settings);
		if (chart_type == ChartConstants.VERTICAL_BAR_3D
				|| chart_type == ChartConstants.HORIZONTAL_BAR_3D) {
            data_plot_settings.setAttribute("enable_3d_mode", "true");
        } else {
            data_plot_settings.setAttribute("enable_3d_mode", "false");
        }
		if (list.size() == 1) {
            data_plot_settings.setAttribute("z_aspect", "0.5");
        }
		Element bar_series = new Element("bar_series");
		data_plot_settings.addContent(bar_series);
		bar_series.setAttribute("group_padding", "0.5");

		if (list.size() < 3) {
            bar_series.setAttribute("point_width", "100");
        }
		// bar_series.setAttribute("style","Silver");
		Element label_settings = new Element("label_settings");
		bar_series.addContent(label_settings);
		// 2012-3-8控制图形上是否显示值
		label_settings.setAttribute("enabled", this.label_enabled);
		Element lblformat = new Element("format");
		lblformat.setText("{%YValue}{numDecimals:" + this.numDecimals + "}");
		label_settings.addContent(lblformat);
		Element lblfont = new Element("font");
		label_settings.addContent(lblfont);
		lblfont.setAttribute("size", labelfontsize);

		// tooltip_settings
		Element tooltip_settings = new Element("tooltip_settings");
		bar_series.addContent(tooltip_settings);
		tooltip_settings.setAttribute("enabled", tooltip_enabled);
		// format,小数点位数
		Element format = new Element("format");
		format.setText("{%Name}{enabled:false}\r\n值:{%Value}{numDecimals:"
				+ this.numDecimals
				+ "}\r\n百分比:{%YPercentOfSeries}{numDecimals:2}%");
		tooltip_settings.addContent(format);
		Element tooltipfont = new Element("font");
		tooltip_settings.addContent(tooltipfont);
		tooltipfont.setAttribute("size", tooltipfontsize);
		// data domain
		Element data = new Element("data");
		chart.addContent(data);
		Element series = new Element("series");
		series.setAttribute("name", "Series 1");
		series.setAttribute("palette", "Default");
		series.setAttribute("type", "Bar");
		data.addContent(series);
		// <point name="Product A" y="1222"/>
		/**
		 * xiegh add 20170815
		 * 当高度小于等于400时，柱形的个数大于10时，则显示图例，不显示下面的标签；个数小于等于10个时，则显示标签，不显示图例
		 * 当高度大于400时，柱形的个数大于15时，则显示图例，不显示下面的标签；个数小于等于15个时，则显示标签，不显示图例
		 * flag为true,表示显示图例 不显示标签
		 */
		boolean isLegend = false;//是否显示例图
		if(height<=400) {
            isLegend = list.size()>7?true:false;
        } else {
            isLegend = list.size()>12?true:false;
        }
		double mxxvalue = 0.0;
		ArrayList relist = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			Element point = new Element("point");
			CommonData vo = (CommonData) list.get(i);
			String name = vo.getDataName();
			String value = vo.getDataValue();
			if(Double.parseDouble(value)<mxxvalue){
				mxxvalue = Double.parseDouble(value);
			}
			if (relist.size() <= 0) {
				relist.add(vo);
			} else {
				boolean iscon = false;
				for (int r = 0; r < relist.size(); r++) {
					CommonData rvo = (CommonData) relist.get(r);
					String rname = rvo.getDataName();
					String rvalue = rvo.getDataValue();
					if (rname.equalsIgnoreCase(name)
							&& value.equalsIgnoreCase(rvalue)) {
						iscon = true;
						break;
					}
				}
				if (iscon) {
                    continue;
                }
				relist.add(vo);
			}
			point.setAttribute("name", name);
			point.setAttribute("y", value);
			setPointColor(point, i);
			series.addContent(point);

			sum = sum + Double.parseDouble(value);
			/** 设置为和有点难看，但最大好象存在问题 */
			ymax = Math.max(ymax, Double.parseDouble(value));
		}
		if(this.title.contains("平均")){
			sum = (sum*1.0)/list.size();
		}
		/*
		 * <chart_settings> <title enabled="true"> <text>Single-Series: with
		 * Simple Legend</text> </title> <legend enabled="true"
		 * position="Bottom" align="Spread" ignore_auto_item="true">
		 * <format>{%Icon} {%Name} (${%YValue}{numDecimals:0})</format>
		 * <template></template> <title enabled="true"> <text>Products Sales</text>
		 * </title> <columns_separator enabled="false"/> <background>
		 * <inside_margin left="10" right="10"/> </background> <items> <item
		 * source="Points"/> </items> </legend> <axes> <y_axis> <title
		 * enabled="false"><text>Sales (in USD)</text></title> <labels
		 * align="Inside"> <format>${%Value}{numDecimals:0}</format> </labels>
		 * </y_axis> <x_axis> <title enabled="false"><text>Products</text></title>
		 * </x_axis> </axes> </chart_settings>
		 */
		Element chart_settings = new Element("chart_settings");
		chart.addContent(chart_settings);
		addBackgroundBorderStyle(chart_settings);

		Element title = new Element("title");
		chart_settings.addContent(title);
		title.setAttribute("enabled", "true");
		Element text = new Element("text");
		title.addContent(text);
		if (!StringUtils.isEmpty(biDesk) || StringUtils.equals(biDesk, "true")) {

		} else {
			// text.setText(this.title+"("+df1.format(sum)+")");
			if ("false".equalsIgnoreCase(isneedsum)) {
                text.setText(this.title);
            } else {
                text.setText(this.title + "(" + df1.format(sum) + ")");
            }
		}
		// <legend enabled="true" position="Right" align="Spread"
		// ignore_auto_item="true" padding="15">
		Element legend = new Element("legend");
		chart_settings.addContent(legend);
		legend.setAttribute("enabled", isLegend+""); // false 不显示图例
		legend.setAttribute("position", "bottom");
		// legend.setAttribute("align","Spread");
		legend.setAttribute("ignore_auto_item", "true");
		if (list.size() > 17) {
            legend.setAttribute("height", "90");
        }
		legend.setAttribute("width", "100%");

		if (transparent) {
			Element background = new Element("background");
			background.setAttribute("enabled", "False");
			legend.addContent(background);
		}
		Element legtitle = new Element("title");
		legend.addContent(legtitle);
		// <title enabled="true">
		// <text>Products Sales</text>
		// </title>
		legtitle.setAttribute("enabled", "false");
		Element legfont = new Element("font");
		legend.addContent(legfont);
		legfont.setAttribute("size", legfontsize);

		Element legformat = new Element("format");
		// <format>{%Icon} {%Name} ({%YValue})</format>
		legend.addContent(legformat);
		legformat
		.setText("{%Icon} ({%Name}{enabled:false}) ({%YValue}{numDecimals:"
				+ this.numDecimals + "})");
		Element items = new Element("items");
		legend.addContent(items);
		Element item = new Element("item");
		items.addContent(item);
		item.setAttribute("source", "Points");

		Element axes = new Element("axes");
		chart_settings.addContent(axes);

		Element y_axis = new Element("y_axis");
		y_axis.setAttribute("position", "Left");
		axes.addContent(y_axis);
		Element y_axistitle = new Element("title");
		y_axis.addContent(y_axistitle);
		Element y_axistext = new Element("text");
		y_axistitle.addContent(y_axistext);
		y_axistext.setText("y");
		y_axistitle.setAttribute("enabled", "false");
		/** y座标设置 */
		Element y_scale = new Element("scale");
		y_axis.addContent(y_scale);

		y_scale.setAttribute("type", "Linear");
		if (!(chart_type == ChartConstants.VERTICAL_BAR_3D || chart_type == ChartConstants.HORIZONTAL_BAR_3D)) {
			if (sum == 0) {
                sum = 1;// 平面直方图最大值不能设置为零，负责包js错误49行错误
            }
		}
		/** sum->改成ymax++0.1*ymax */
		// liuy 2015-1-20 特殊控制，当y轴最大值为0时，默认为100
		if("false".equals(this.yAxisAuto)) {
			if (ymax == 0) {
                y_scale.setAttribute("maximum", "100");
            } else {
                y_scale.setAttribute("maximum", String.valueOf(ymax + 0.2 * ymax));
            }
			if(mxxvalue<0.0){
				y_scale.setAttribute("minimum", String.valueOf(mxxvalue + 0.2 * mxxvalue));
			}else{
				y_scale.setAttribute("minimum", String.valueOf(this.minvalue));
			}
		}
		
		y_scale.setAttribute("type", "Linear");

		Element labels = new Element("labels");
		y_axis.addContent(labels);
		labels.setAttribute("align", "Inside");
		Element labelsfmt = new Element("format");
		/*
		 * if(sum<=1) labelsfmt.setText("{%Value}{numDecimals:2}"); else
		 */
		labelsfmt.setText("{%Value}{numDecimals:" + this.numDecimals + "}");
		labels.addContent(labelsfmt);

		Element x_axis = new Element("x_axis");

		axes.addContent(x_axis);

		Element x_axistitle = new Element("title");
		x_axis.addContent(x_axistitle);
		/* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-9 start */
		// liuy 2015-3-25 8174：总裁桌面换上高速的配置文件，直方图在V70版本中不显示，饼图可以显示，不对
		/*
		 * if((StringUtils.isNotEmpty(biDesk) && StringUtils.equals(biDesk,
		 * "true"))) { // 获取屏幕分辨率宽度 Dimension srcSize =
		 * Toolkit.getDefaultToolkit().getScreenSize(); int width =
		 * srcSize.width; int temp = Integer.parseInt(total); if(temp > 0) { int
		 * quotient = width/temp; if(quotient < 36) { int rotation =
		 * (36-quotient)*10; if(rotation > 60) { rotation = 270; } if(rotation <
		 * 45) { rotation = 45; } xangle = rotation; Element xlabels = new
		 * Element("labels"); xlabels.setAttribute("display_mode", "Normal");
		 * xlabels.setAttribute("rotation", String.valueOf(xangle));
		 * x_axis.addContent(xlabels); } } } else if(xangle!=0){
		 */
		Element xlabels = new Element("labels");
		xlabels.setAttribute("display_mode", "Normal");
		if(xangle!=0){
    		xlabels.setAttribute("width",xangle==maxXangle?"15":"100" );
    		if(xangle==maxXangle) {
                xangle = 0;
            }
		}
		xlabels.setAttribute("rotation", String.valueOf(xangle));
		x_axis.addContent(xlabels);
		x_axis.setAttribute("enabled", !isLegend+"");
		// }
		/* 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全 xiaoyun 2014-7-9 end */

		x_axistitle.setAttribute("enabled", "false");
		Element x_axistext = new Element("text");
		x_axistitle.addContent(x_axistext);
		x_axistext.setText("x");

		/*
		 * <axes> <y_axis> <title><text>Sales (in USD)</text></title> <labels
		 * align="Inside"> <format>${%Value}{numDecimals:0}</format> </labels>
		 * </y_axis> <x_axis> <title enabled="false"><text>Products</text></title>
		 * </x_axis> </axes>
		 */

		try {
			XMLOutputter outputter = new XMLOutputter();
			Format format0 = Format.getPrettyFormat();
			format0.setEncoding("UTF-8");
			outputter.setFormat(format0);
			buf.append(outputter.outputString(myDocument));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return buf.toString();

		/* 标识：2580 立体直方图下方红色方框标示占用空间过大,去除标示内容 xiaoyun 2014-6-18 start */
		// Element items = new Element("items");
		/*
		 * if(StringUtils.isEmpty(biDesk) || !StringUtils.equals(biDesk,
		 * "true")) { Element legend = new Element("legend");
		 * //chart_settings.addContent(legend);
		 * legend.setAttribute("enabled","true"); // false 不显示图例
		 * legend.setAttribute("position","bottom");
		 * //legend.setAttribute("align","Spread");
		 * legend.setAttribute("ignore_auto_item","true"); if(list.size()>8)
		 * legend.setAttribute("height","60");
		 * legend.setAttribute("width","100%");
		 * 
		 * Element legtitle = new Element("title"); legend.addContent(legtitle); //
		 * <title enabled="true"> // <text>Products Sales</text> // </title>
		 * legtitle.setAttribute("enabled","false"); Element legfont = new
		 * Element("font"); legend.addContent(legfont);
		 * legfont.setAttribute("size",legfontsize);
		 * 
		 * Element legformat = new Element("format"); // <format>{%Icon} {%Name}
		 * ({%YValue})</format> legend.addContent(legformat);
		 * legformat.setText("{%Icon} ({%Name}{enabled:false})
		 * ({%YValue}{numDecimals:"+this.numDecimals+"})");
		 * legend.addContent(items); } else { }
		 */
		/* 标识：2580 立体直方图下方红色方框标示占用空间过大,去除标示内容 xiaoyun 2014-6-18 end */

	}

	/**
	 * 柱状占比图
	 * 
	 * @param list
	 * @return
	 */
	private String outBarLineXml(ArrayList list) {
		StringBuffer buf = new StringBuffer();
		double sum = 0;
		double max = 0;
		DecimalFormat df1 = (DecimalFormat) DecimalFormat.getInstance();
		if (this.numDecimals == 0) {
            df1.applyPattern("0");
        } else {
			String fmtstr = StringUtils.rightPad("0.", 2 + this.numDecimals,
			"0");
			df1.applyPattern(fmtstr);
		}
		Element root = new Element("anychart");
		Document myDocument = new Document(root);
		Element settings = new Element("settings");
		root.addContent(settings);
		Element animation = new Element("animation");
		if (this.animation) {
            animation.setAttribute("enabled", "True");
        } else {
            animation.setAttribute("enabled", "False");
        }
		settings.addContent(animation);

		Element margin = new Element("margin");
		root.addContent(margin);
		margin.setAttribute("all", "0");

		Element context_menu = new Element("context_menu");
		settings.addContent(context_menu);
		context_menu.setAttribute("version_info", "false");
		context_menu.setAttribute("about_anychart", "false");
		Element save_as_image_item_text = new Element("save_as_image_item_text");
		context_menu.addContent(save_as_image_item_text);
		save_as_image_item_text.setText("保存");
		Element print_chart_item_text = new Element("print_chart_item_text");
		context_menu.addContent(print_chart_item_text);
		print_chart_item_text.setText("打印");

		Element charts = new Element("charts");
		root.addContent(charts);
		Element chart = new Element("chart");
		chart.setAttribute("plot_type", "CategorizedVertical");
		charts.addContent(chart);
		Element data_plot_settings = new Element("data_plot_settings");
		{
			Element line_series = new Element("line_series");
			data_plot_settings.addContent(line_series);
			Element line_style = new Element("line_style");
			line_series.addContent(line_style);
			Element line = new Element("line");
			line.setAttribute("thickness", "3");
			line_style.addContent(line);
			Element tooltip_settings = new Element("tooltip_settings");
			tooltip_settings.setAttribute("enabled", tooltip_enabled);
			Element format = new Element("format");
			format.setText("{%Value}{numDecimals:0}%");
			tooltip_settings.addContent(format);
			line_series.addContent(tooltip_settings);
			//label_settings
			Element label_settings = new Element("label_settings");
			line_series.addContent(label_settings);
			label_settings.setAttribute("enabled",this.label_enabled);
			label_settings.setAttribute("mode","Outside");
			label_settings.setAttribute("multi_line_align","CenterTop");//Outside
	        //label_settings.addContent(position);
	        Element font = new Element("font");
	        label_settings.addContent(font);
	        //font.setAttribute("color","White");
	        font.setAttribute("size",labelfontsize);
	        Element lblformat = new Element("format");
	        lblformat.setText("{%Value}{numDecimals:0}");
	        label_settings.addContent(lblformat);

		}
		chart.addContent(data_plot_settings);
		Element bar_series = new Element("bar_series");
		data_plot_settings.addContent(bar_series);
		// tooltip_settings
		Element format = new Element("format");
		format.setText("{%Value}{numDecimals:" + this.numDecimals + "}");
		Element tooltip_settings = new Element("tooltip_settings");
		bar_series.addContent(tooltip_settings);
		tooltip_settings.addContent(format);
		tooltip_settings.setAttribute("enabled", tooltip_enabled);

		// data domain
		Element data = new Element("data");
		chart.addContent(data);
		Element series = new Element("series");
		series.setAttribute("name", "Series 2");
		series.setAttribute("type", "Bar");
		series.setAttribute("color", "#1D8BD1");
		// <point name="Product A" y="1222"/>
		for (int i = 0; i < list.size(); i++) {
			Element point = new Element("point");
			CommonData vo = (CommonData) list.get(i);
			String name = vo.getDataName();
			String value = vo.getDataValue();
			point.setAttribute("name", name);
			point.setAttribute("y", value);
			series.addContent(point);
			sum = sum + Double.parseDouble(value);
			if (max < Double.parseDouble(value)) {
                max = Double.parseDouble(value);
            }
		}
		
		Element series2 = new Element("series");
		series2.setAttribute("name", "Series 1");
		series2.setAttribute("y_axis", "extra_y_axis_1");
		series2.setAttribute("type", "Line");
		series2.setAttribute("color", "#F1683C");

		for (int i = 0; i < list.size(); i++) {
			Element point = new Element("point");
			CommonData vo = (CommonData) list.get(i);
			String name = vo.getDataName();
			String value = vo.getDataValue();
			double y = Double.parseDouble(value);
			double d = (y / sum);
			value = PubFunc.round(d + "", 3);
			float f = Float.valueOf(value).floatValue();
			f = f * 100;
			value = PubFunc.round(f + "", 0);
			point.setAttribute("name", name);
			point.setAttribute("y", value);
			series2.addContent(point);
		}
		
		
		data.addContent(series2);
		data.addContent(series);
		Element chart_settings = new Element("chart_settings");
		chart.addContent(chart_settings);
		this.addBackgroundBorderStyle1(chart_settings);
		Element title = new Element("title");
		chart_settings.addContent(title);
		title.setAttribute("enabled", "true");
		Element text = new Element("text");
		title.addContent(text);
		// text.setText(this.title+"("+df1.format(sum)+")");
		double avg = 0.0;
		if(this.title.contains("平均")) {
            avg = (sum*1.0)/list.size();
        } else {
            avg = sum;
        }
		if ("false".equalsIgnoreCase(isneedsum)) {
            text.setText(this.title);
        } else {
            text.setText(this.title + "(" + df1.format(avg) + ")");
        }

		// <legend enabled="true" position="Right" align="Spread"
		// ignore_auto_item="true" padding="15">
		/*
		 * Element legend = new Element("legend");
		 * chart_settings.addContent(legend);
		 * legend.setAttribute("enabled","true"); // false 不显示图例
		 * legend.setAttribute("position","bottom");
		 * //legend.setAttribute("align","Spread");
		 * legend.setAttribute("ignore_auto_item","true"); if(list.size()>8)
		 * legend.setAttribute("height","60");
		 * legend.setAttribute("width","100%");
		 * 
		 * Element legtitle = new Element("title"); legend.addContent(legtitle); //
		 * <title enabled="true"> // <text>Products Sales</text> // </title>
		 * legtitle.setAttribute("enabled","false"); Element legfont = new
		 * Element("font"); legend.addContent(legfont);
		 * legfont.setAttribute("size","12");
		 * 
		 * Element legformat = new Element("format"); // <format>{%Icon} {%Name}
		 * ({%YValue})</format> legend.addContent(legformat);
		 * legformat.setText("{%Icon} ({%Name}{numDecimals:0})
		 * ({%YValue}{numDecimals:"+this.numDecimals+"})"); Element items = new
		 * Element("items"); legend.addContent(items); Element item = new
		 * Element("item"); items.addContent(item);
		 * item.setAttribute("source","Points");
		 */
		Element axes = new Element("axes");
		chart_settings.addContent(axes);

		Element y_axis = new Element("y_axis");
		y_axis.setAttribute("position", "Left");
		axes.addContent(y_axis);
		Element y_axistitle = new Element("title");
		y_axis.addContent(y_axistitle);
		Element y_axistext = new Element("text");
		y_axistitle.addContent(y_axistext);
		y_axistext.setText("y");
		y_axistitle.setAttribute("enabled", "false");
		/** y座标设置 */
		Element y_scale = new Element("scale");
		y_axis.addContent(y_scale);

		y_scale.setAttribute("type", "Linear");
		if (!(chart_type == ChartConstants.VERTICAL_BAR_3D || chart_type == ChartConstants.HORIZONTAL_BAR_3D)) {
			if (sum == 0) {
                sum = 1;// 平面直方图最大值不能设置为零，负责包js错误49行错误
            }
		}
		max = (max + 30.00);
		//y非自动适应走原逻辑
		if("false".equals(yAxisAuto)) {
			y_scale.setAttribute("maximum", String.valueOf((max)));
			y_scale.setAttribute("minimum", String.valueOf(this.minvalue));
		}
		y_scale.setAttribute("show_last_label", "false");
		Element minor_grid = new Element("minor_grid");
		minor_grid.setAttribute("enabled", "false");
		y_scale.addContent(minor_grid);
		Element major_grid = new Element("major_grid");
		major_grid.setAttribute("interlaced", "false");
		y_scale.addContent(major_grid);

		Element labels = new Element("labels");
		y_axis.addContent(labels);
		labels.setAttribute("align", "Inside");
		Element labelsfmt = new Element("format");
		labelsfmt.setText("{%Value}{numDecimals:" + this.numDecimals + "}");
		labels.addContent(labelsfmt);

		Element x_axis = new Element("x_axis");
		axes.addContent(x_axis);
		if (chart_type == ChartConstants.Bar_Line_CHART) {
			Element extra = new Element("extra");
			axes.addContent(extra);
			Element y_axis2 = new Element("y_axis");
			extra.addContent(y_axis2);
			y_axis2.setAttribute("name", "extra_y_axis_1");
			y_axis2.setAttribute("enabled", "true");
			Element scale = new Element("scale");
			scale.setAttribute("minimum", "0");
			scale.setAttribute("maximum", "100");
			scale.setAttribute("major_interval", "10");
			scale.setAttribute("show_last_label", "false");
			y_axis2.addContent(scale);
			Element minor_grid_2 = new Element("minor_grid");
			minor_grid_2.setAttribute("enabled", "false");
			y_axis2.addContent(minor_grid_2);
			Element major_grid_2 = new Element("major_grid");
			major_grid_2.setAttribute("interlaced", "false");
			y_axis2.addContent(major_grid_2);
			Element labels_2 = new Element("labels");
			Element format_2 = new Element("format");
			format_2.setText("{%Value}{numDecimals:0}%");
			labels_2.addContent(format_2);
			y_axis2.addContent(labels_2);
			Element title_2 = new Element("title");
			title_2.setAttribute("enabled", "false");
			y_axis2.addContent(title_2);
		}
		Element x_axistitle = new Element("title");
		x_axis.addContent(x_axistitle);

		Element xlabels = new Element("labels");
		if(xangle!=0){
			xlabels.setAttribute("width", xangle==maxXangle?"15":"100");
			xangle = 0;
		}
		xlabels.setAttribute("display_mode", "Normal");
		xlabels.setAttribute("rotation", String.valueOf(xangle));
		x_axis.addContent(xlabels);
		
		x_axistitle.setAttribute("enabled", "false");
		Element x_axistext = new Element("text");
		x_axistitle.addContent(x_axistext);
		x_axistext.setText("x");

		/*
		 * <axes> <y_axis> <title><text>Sales (in USD)</text></title> <labels
		 * align="Inside"> <format>${%Value}{numDecimals:0}</format> </labels>
		 * </y_axis> <x_axis> <title enabled="false"><text>Products</text></title>
		 * </x_axis> </axes>
		 */

		try {
			XMLOutputter outputter = new XMLOutputter();
			Format format0 = Format.getPrettyFormat();
			format0.setEncoding("UTF-8");
			outputter.setFormat(format0);
			buf.append(outputter.outputString(myDocument));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 生成图形xml格式
	 * 
	 * @param list
	 * @param biDesk
	 *            是否为总裁桌面发来的请求(图形展示修改)
	 * @return
	 */
	public String outPieXml(ArrayList list, String biDesk,boolean pieoutin) {
		boolean isZore = true;
		for (int i = 0; i < list.size(); i++) {
			CommonData vo = (CommonData) list.get(i);
			String value = vo.getDataValue();
			if (value != null && !"0".equals(value) && !"".equals(value)
					&& !"0.0".equals(value)) {
				isZore = false;
				break;
			}
		}
		if (isZore) {
            list = new ArrayList();
        }
		StringBuffer buf = new StringBuffer();
		double sum = 0;
		DecimalFormat df1 = (DecimalFormat) DecimalFormat.getInstance();
		if (this.numDecimals == 0) {
            df1.applyPattern("0");
        } else {
			String fmtstr = StringUtils.rightPad("0.", 2 + this.numDecimals,
			"0");
			df1.applyPattern(fmtstr);
		}
		Element root = new Element("anychart");
		Document myDocument = new Document(root);
		Element settings = new Element("settings");
		root.addContent(settings);
		setNo_data(settings);// 汉化无数据
		Element animation = new Element("animation");
		if (this.animation) {
            animation.setAttribute("enabled", "True");
        } else {
            animation.setAttribute("enabled", "False");
        }
		settings.addContent(animation);

		Element margin = new Element("margin");
		root.addContent(margin);
		margin.setAttribute("all", "0");

		Element context_menu = new Element("context_menu");
		settings.addContent(context_menu);
		context_menu.setAttribute("version_info", "false");
		context_menu.setAttribute("about_anychart", "false");
		Element save_as_image_item_text = new Element("save_as_image_item_text");
		context_menu.addContent(save_as_image_item_text);
		save_as_image_item_text.setText("保存");
		Element print_chart_item_text = new Element("print_chart_item_text");
		context_menu.addContent(print_chart_item_text);
		print_chart_item_text.setText("打印");

		Element charts = new Element("charts");
		root.addContent(charts);
		Element chart = new Element("chart");
		chart.setAttribute("plot_type", "Pie");
		charts.addContent(chart);
		// data_plot_settings
		Element data_plot_settings = new Element("data_plot_settings");
		chart.addContent(data_plot_settings);
		if (chart_type == ChartConstants.PIE_3D) {
            data_plot_settings.setAttribute("enable_3d_mode", "false");
        }
		if (chart_type == ChartConstants.PIE) {
            data_plot_settings.setAttribute("enable_3d_mode", "true");
        }
		// pie_series
		Element pie_series = new Element("pie_series");
		data_plot_settings.addContent(pie_series);
		// tooltip_settings
		Element tooltip_settings = new Element("tooltip_settings");
		pie_series.addContent(tooltip_settings);
		tooltip_settings.setAttribute("enabled", tooltip_enabled);
		// format,小数点位数
		Element format = new Element("format");
		format.setText("{%Name}{enabled:false}\r\n值:{%Value}{numDecimals:"
				+ this.numDecimals
				+ "}\r\n百分比:{%YPercentOfSeries}{numDecimals:2}%");
		tooltip_settings.addContent(format);
		Element tooltipfont = new Element("font");
		tooltip_settings.addContent(tooltipfont);
		tooltipfont.setAttribute("size", tooltipfontsize);

		/* 标识：2581 总裁桌面饼图通过引线的方式标明各统计项的含义 xiaoyun 2014-6-23 start */
		/**
		 * <data_plot_settings enable_3d_mode="true"> <pie_series>
		 * <tooltip_settings enabled="true"> <format>{%Name}{enabled:false}&#xD;
		 * 值:{%Value}{numDecimals:0}&#xD;
		 * 百分比:{%YPercentOfSeries}{numDecimals:2}%</format> <font size="12" />
		 * </tooltip_settings> <label_settings enabled="true" mode="Inside"
		 * multi_line_align="CenterTop"> <position anchor="CenterTop"
		 * valign="Center" halign="Center" padding="20" /> <font size="12" />
		 * <format>{%YPercentOfSeries}{numDecimals:2}%</format>
		 * </label_settings> </pie_series> </data_plot_settings>
		 */

		/* 标识：2581 总裁桌面饼图通过引线的方式标明各统计项的含义 xiaoyun 2014-6-23 end */
		// label_settings
		Element label_settings = new Element("label_settings");
		pie_series.addContent(label_settings);
		label_settings.setAttribute("enabled", this.label_enabled);
		/* 标识：2581 总裁桌面饼图通过引线的方式标明各统计项的含义 xiaoyun 2014-6-23 start */
		// if(StringUtils.isNotEmpty(biDesk) && StringUtils.equals(biDesk,
		// "true")) {
		// label_settings.setAttribute("mode","Outside");
		// } else {
		if(pieoutin) {
            label_settings.setAttribute("mode", "Outside");
        } else {
            label_settings.setAttribute("mode", "Inside");
        }
		// }
		/* 标识：2581 总裁桌面饼图通过引线的方式标明各统计项的含义 xiaoyun 2014-6-23 end */
		label_settings.setAttribute("multi_line_align", "CenterTop");// Outside
		// <position anchor="Center" valign="Center" halign="Center"
		// padding="20"/>
		Element position = new Element("position");
		label_settings.addContent(position);
		position.setAttribute("anchor", "CenterTop");
		position.setAttribute("valign", "Center");
		position.setAttribute("halign", "Center");
		position.setAttribute("padding", "20");
		Element font = new Element("font");
		label_settings.addContent(font);
		// font.setAttribute("color","White");
		font.setAttribute("size", labelfontsize);
		Element lblformat = new Element("format");
		/* 标识：2581 总裁桌面饼图通过引线的方式标明各统计项的含义 xiaoyun 2014-6-23 start */
	/*	 if(StringUtils.isNotEmpty(biDesk) && StringUtils.equals(biDesk,
		 "true")) {
			 lblformat.setText("{%YPercentOfSeries}{numDecimals:2}%");
		 } else {
			 lblformat.setText("{%Name}-{%YPercentOfSeries}{numDecimals:2}%");
		 }*/
		/**
		 * xiegh add 20170815
		 * ①当调用的是多维统计时，则不显示描述信息，显示图例
		 * ②当高度小于等于400时，柱形的个数大于10时，则显示图例，不现实下面的标签；个数小于等于10个时，则显示标签，不显示图例
		 * ③当高度大于400时，柱形的个数大于15时，则显示图例，不现实下面的标签；个数小于等于15个时，则显示标签，不显示图例
		 * flag为true,表示显示图例 不显示标签
		 */
		 boolean isLegend=false;//是否显示例图
		 if("false".equals(islabelname)){
			 lblformat.setText("{%YPercentOfSeries}{numDecimals:2}%");
			 	isLegend = true;
		 }else{
			 if((height<=400&&list.size()>7)||(height>400&&list.size()>12)){
				 	lblformat.setText("{%YPercentOfSeries}{numDecimals:2}%");
				 	isLegend = true;
			 }else{
			 	lblformat.setText("{%Name}-{%YPercentOfSeries}{numDecimals:2}%");
			 	isLegend = false;
			 }
		 }
		
		/* 标识：2581 总裁桌面饼图通过引线的方式标明各统计项的含义 xiaoyun 2014-6-23 end */
		label_settings.addContent(lblformat);
		// data domain
		Element data = new Element("data");
		chart.addContent(data);
		Element series = new Element("series");
		series.setAttribute("name", "Series 1");
		series.setAttribute("type", "Pie");
		if (sumStaticItemValue(list) != 0)// 没有数据的时候就不再加载默认项
        {
            data.addContent(series);
        }
		// <point name="Product A" y="1222"/>
		for (int i = 0; i < list.size(); i++) {
			Element point = new Element("point");
			CommonData vo = (CommonData) list.get(i);
			String name = vo.getDataName();
			if(!isLegend){
				if (name.length() > 12 ) {
                    name= name.substring(0, 6)+ "\n" + name.substring(6,12)+"\n"+name.substring(12);
                } else if(name.length() >6&&name.length()<=12) {
                    name = name.substring(0, 6)+ "\n" + name.substring(6);
                }
			}
			point.setAttribute("name", name);
			String value = vo.getDataValue();
			point.setAttribute("y", value);
			setPointColor(point, i);
			//point.setAttribute("color", getHyjtPointColor(i));
			series.addContent(point);
			sum = sum + Double.parseDouble(vo.getDataValue());
		}

		// <chart_settings>
		Element chart_settings = new Element("chart_settings");
		chart.addContent(chart_settings);
		this.addBackgroundBorderStyle1(chart_settings);
		Element title = new Element("title");
		chart_settings.addContent(title);
		title.setAttribute("enabled", "false");
		if (chart_type == ChartConstants.PIE && this.height > 400) {
            title.setAttribute("padding", "100");
        } else {
            title.setAttribute("padding", "5");
        }
		Element text = new Element("text");
		title.addContent(text);
		// text.setText(this.title+"("+df1.format(sum)+")");
		if ("false".equalsIgnoreCase(isneedsum)) {
            text.setText(this.title);
        } else {
            text.setText(this.title + "(" + df1.format(sum) + ")");
        }
		// <legend enabled="true" position="Right" align="Spread"
		// ignore_auto_item="true" padding="15">
		Element legend = new Element("legend");
		/* 标识：2581 总裁桌面饼图通过引线的方式标明各统计项的含义 xiaoyun 2014-6-23 start */
		// if(StringUtils.isEmpty(biDesk) || !StringUtils.equals(biDesk,
		// "true")) {
		chart_settings.addContent(legend);
		// }
		/* 标识：2581 总裁桌面饼图通过引线的方式标明各统计项的含义 xiaoyun 2014-6-23 end */
		legend.setAttribute("enabled", isLegend+"");
	
		
		/** 如果展示的图形高度小于250时,图例则显示在右边 */
		if (this.height <= 250) {
            legend.setAttribute("position", "right");
        } else {
            legend.setAttribute("position", "bottom");
        }
		// legend.setAttribute("align","Spread");
		legend.setAttribute("ignore_auto_item", "true");
		if (list.size() > 27) {
            legend.setAttribute("height", "90");
        }
		// 针对哈药多饼图专版开发控制（现在chart标签传递的参数越来越多，此处采用chartpnl名称唯一来标示用于哈药多饼图页面使用 xuj
		// add 2014-9-1）
		if (this.getChartpnl() != null
				&& this.getChartpnl().startsWith("multi_pie")) {
			legend.setAttribute("height", "60");
			legend.setAttribute("width", "420");
		}
		Element font1 = new Element("font1");
		legend.addContent(font1);
		font1.setAttribute("size", "4");
		if (transparent) {
			Element background = new Element("background");
			background.setAttribute("enabled", "False");
			legend.addContent(background);
		}

		Element legtitle = new Element("title");
		legend.addContent(legtitle);
		// <title enabled="true">
		// <text>Products Sales</text>
		// </title>
		legtitle.setAttribute("enabled", "false");
		Element legfont = new Element("font");
		legend.addContent(legfont);
		legfont.setAttribute("size", legfontsize);

		Element legformat = new Element("format");
		// <format>{%Icon} {%Name} ({%YValue})</format>
		legend.addContent(legformat);
		if (StringUtils.isEmpty(biDesk) || !StringUtils.equals(biDesk, "true")) {
			legformat.setText("{%Icon}{%Name}"/** ({%YValue})"*{numDecimals:"+this.numDecimals+"})** */
			);
		} else {
			legformat.setText("{%Icon} {%Name}"/** *({%YValue}{numDecimals:"+this.numDecimals+"})** */
			);
		}
		Element items = new Element("items");
		legend.addContent(items);
		Element item = new Element("item");
		items.addContent(item);
		item.setAttribute("source", "Points");
		try {
			XMLOutputter outputter = new XMLOutputter();
			Format format0 = Format.getPrettyFormat();
			format0.setEncoding("UTF-8");
			outputter.setFormat(format0);
			buf.append(outputter.outputString(myDocument));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 取点颜色, 颜色定义详见：
	 * http://www.anychart.com/products/anychart/docs/users-guide/index.html?Color-Table.html<br>
	 * 哈药集团专版
	 * 
	 * @param i
	 * @return
	 */
	private String getHyjtPointColor(int i) {
		String[] colors = { "#F0F8FF", "#FAEBD7", "#00FFFF", "#7FFFD4",
				"#F0FFFF", "#F5F5DC", "#FFE4C4", "#FFEBCD", "#0000FF",
				"#7FFF00", "#8A2BE2", "#FF7F50", "#6495ED", "#FFF8DC",
				"#006400", "#ADD8E6", "#7CFC00", "#F08080", "#FF00FF",
		"#00FF00" };


//		String[] colors = { "#9acd32", "#ffff00", "#58ceff", "#a85aff",
//		"#e0e0e0", "#ff8cb2", "#ff9e07", "#ff7f50", "#40e0d0",
//		"#ffffa0", "#ff0f00", "#ffb974", "#a9f5ff", "#04d215",
//		"#ff59ff", "#c3d004", "#b20077", "#4aa3ff", "#00fa9a",
//		"#eaadea" };
		return colors[i % colors.length];
	}

	private void setPointColor(Element point, int i) {
		if ("HaYaoJiTuan".equals(SystemConfig.getPropertyValue("clientName"))) // 哈药集团专版
        {
            point.setAttribute("color", getHyjtPointColor(i));
        }
	}

	/**
	 * 雷达图
	 * 
	 * @param map
	 *            key value minmax 0.1,2020.00 String
	 *            //最小值和最大值,键值固定为minmax,最小值,最大值 图例 ArrayList 对象 ... ...
	 * @param event
	 * @return
	 */
	public String outRadarChart(HashMap map, HashMap event) {
		StringBuffer buf = new StringBuffer();
		String tmp = "";
		String chartname = "chart";
		if (!(this.chartpnl == null || this.chartpnl.length() == 0)) {
            chartname = chartname + this.chartpnl;
        }

		if ((this.chartpnl == null || this.chartpnl.length() == 0)) {
			buf
			.append("<script type=\"text/javascript\" language=\"javascript\" src=\"/anychart/js/AnyChart.js\"></script>");
			buf
			.append("<script type=\"text/javascript\" language=\"javascript\" src=\"/ajax/basic.js\"></script>");
		}

		buf.append("<script type=\"text/javascript\" language=\"javascript\">");
		buf.append(" var chart = new AnyChart('/anychart/swf/AnyChart.swf');");
		// bgColor
		// buf.append(" chart.bgColor='"+bg_color+"';");
		tmp = outRadarXml(map);
		// System.out.println("ss="+tmp);
		tmp = SafeCode.encode(tmp);
		buf.append("  var xmldata='");
		buf.append(tmp);
		buf.append("';");
		buf.append(" xmldata=getDecodeStr(xmldata);");
		Iterator it = event.entrySet().iterator();
		if (event.size() > 0) {
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String eventname = (String) entry.getValue();
				String eventtype = (String) entry.getKey();
				buf.append(chartname);
				buf.append(".addEventListener('");
				buf.append(eventtype);
				buf.append("',");
				buf.append(eventname);
				buf.append(");");
			}
		}
		buf.append(" chart.width ='");
		if (this.width == -1) {
			buf.append("100%");
		} else {
			buf.append(this.width);
		}
		buf.append("';");
		// buf.append(" chart.width ='100%';");

		buf.append(" chart.height ='");
		if (this.height == -1) {
            buf.append("100%");
        } else {
            buf.append(this.height);
        }
		buf.append("';");
		/** 加上前台报错,解决不了日期显示的问题 */
		buf.append(" chart.wMode=\"transparent\";");
		buf.append(" chart.setData(xmldata);");
		// buf.append(" chart.setXMLFile('/test/aaa.xml');");
		if (this.chartpnl == null || this.chartpnl.length() == 0) {
            buf.append(" chart.write();");
        } else {
			buf.append(" chart.write('");
			buf.append(this.chartpnl);
			buf.append("');");
		}

		buf.append("</script>");
		// System.out.println("script="+buf.toString());
		return buf.toString();

	}

	/**
	 * 输出线图
	 * 
	 * @param map
	 * @return
	 */
	public String outLineChart(HashMap map, HashMap event,String biDesk) {
		StringBuffer buf = new StringBuffer();
		String tmp = "";
		String chartname = "chart";
		if (!(this.chartpnl == null || this.chartpnl.length() == 0)) {
            chartname = chartname + this.chartpnl;
        }
		if ((this.chartpnl == null || this.chartpnl.length() == 0)) {
			buf
			.append("<script type=\"text/javascript\" language=\"javascript\" src=\"/anychart/js/AnyChart.js\"></script>");
			buf
			.append("<script type=\"text/javascript\" language=\"javascript\" src=\"/ajax/basic.js\"></script>");
		}

		buf.append("<script type=\"text/javascript\" language=\"javascript\">");
		buf.append(" var " + chartname
				+ " = new AnyChart('/anychart/swf/AnyChart.swf');");
		// bgColor
		// buf.append(" chart.bgColor='"+bg_color+"';");
		tmp = outLineXml(map,biDesk);
		// System.out.println("ss="+tmp);
		tmp = SafeCode.encode(tmp);
		buf.append("  var xmldata='");
		buf.append(tmp);
		buf.append("';");
		buf.append(" xmldata=getDecodeStr(xmldata);");
		Iterator it = event.entrySet().iterator();
		if (event.size() > 0) {
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String eventname = (String) entry.getValue();
				String eventtype = (String) entry.getKey();
				buf.append(chartname);
				buf.append(".addEventListener('");
				buf.append(eventtype);
				buf.append("',");
				buf.append(eventname);
				buf.append(");");
			}
		}
		buf.append(chartname + ".width ='");
		if (this.width == -1) {
			buf.append("100%");
		} else {
			buf.append(this.width);
		}
		buf.append("';");
		// buf.append(" chart.width ='100%';");

		buf.append(chartname + ".height ='");
		if (this.height == -1) {
            buf.append("100%");
        } else {
            buf.append(this.height);
        }
		buf.append("';");
		/** 加上前台报错,解决不了日期显示的问题 */
		buf.append(chartname + ".wMode=\"transparent\";");
		buf.append(chartname + ".setData(xmldata);");
		// buf.append(" chart.setXMLFile('/test/aaa.xml');");
		if (this.chartpnl == null || this.chartpnl.length() == 0) {
            buf.append(chartname + ".write();");
        } else {
			buf.append(chartname + ".write('");
			buf.append(this.chartpnl);
			buf.append("');");
		}

		buf.append("</script>");
		// System.out.println("script="+buf.toString());
		return buf.toString();
	}

	/**
	 * @param gvalue
	 *            良好
	 * @param yvalue
	 *            正常
	 * @param rvalue
	 *            预警
	 * @param cvalue
	 *            当前值
	 * @return
	 */
	public String outGaugeBoardChart(ArrayList minvalue, ArrayList maxvalue,
			ArrayList valves, ArrayList cvalues, String notem) {
		StringBuffer buf = new StringBuffer();
		String chartname = "chart";
		if (!(this.chartpnl == null || this.chartpnl.length() == 0)) {
            chartname = chartname + this.chartpnl;
        }
		/**
		 * 如果未定义输出面板,则输出js， 在同一个页面中有多个图形时，如果每个图形前面都输出JS文件时，只有第一个能输出，
		 * 所以对这种有多个图形的页面时，这两个文件放在页面中引用。
		 */
		if ((this.chartpnl == null || this.chartpnl.length() == 0)) {
			buf
			.append("<script type=\"text/javascript\" language=\"javascript\" src=\"/anychart/js/AnyChart.js\"></script>");
			buf
			.append("<script type=\"text/javascript\" language=\"javascript\" src=\"/ajax/basic.js\"></script>");
		}
		buf.append("<script type=\"text/javascript\" language=\"javascript\">");

		buf.append(" var ");
		buf.append(chartname);
		buf.append(" = new AnyChart('/anychart/swf/AnyChart.swf');");
		// bgColor
		buf.append(chartname);
		buf.append(".");
		buf.append("bgColor='white';");

		buf.append(chartname);
		buf.append(".wMode='transparent';");
		String tmp = "";
		// 判断统计图类型
		switch (chart_type) {
		case ChartConstants.Circular_Gauge:
			tmp = outGaugeBoardChartXml(minvalue, maxvalue, valves, cvalues);
			break;
		case ChartConstants.Vertical_Gauge:
			tmp = outThermometerChartXml(valves, cvalues, notem);
			break;
		case ChartConstants.Circular_Bind:
			tmp = outCircularBindingChartXml(valves, cvalues);
			break;

		}
		if (chart_type == ChartConstants.Circular_Gauge
				|| chart_type == ChartConstants.Circular_Bind) {
			tmp = SafeCode.encode(tmp);
			buf.append("  var xmldata='");
			buf.append(tmp);
			buf.append("';");
			buf.append(" xmldata=getDecodeStr(xmldata);");
			buf.append(chartname);
			buf.append(".");
			buf.append("width =");
			if (this.width == -1) {
				buf.append("\"90%\"");
			} else {
				buf.append(this.width);
			}
			buf.append(";");

			buf.append(chartname);
			buf.append(".");
			buf.append("height =");
			if (this.height == -1) {
                buf.append("\"90%\"");
            } else {
                buf.append(this.height);
            }
			buf.append(";");
			buf.append(chartname);
			buf.append(".");
			buf.append("setData(xmldata);");
			if (this.chartpnl == null || this.chartpnl.length() == 0) {
				buf.append(chartname);
				buf.append(".");
				buf.append("write();");
			} else {
				buf.append(chartname);
				buf.append(".");
				buf.append(" write('");
				buf.append(this.chartpnl);
				buf.append("');");
			}
			buf.append("</script>");
		} else {
			tmp = SafeCode.encode(tmp);
			buf.append("  var xmldata='");
			buf.append(tmp);
			buf.append("';");
			buf.append(" xmldata=getDecodeStr(xmldata);");
			buf.append(chartname);
			buf.append(".");
			buf.append("width =");
			if (this.width == -1) {
				buf.append("'100%'");
			} else {
				buf.append(this.width);
			}
			buf.append(";");

			buf.append(chartname);
			buf.append(".");
			buf.append("height =");
			if (this.height == -1) {
                buf.append("'100%'");
            } else {
                buf.append(this.height);
            }
			buf.append(";");
			buf.append(chartname);
			buf.append(".");
			buf.append("setData(xmldata);");
			if (this.chartpnl == null || this.chartpnl.length() == 0) {
				buf.append(chartname);
				buf.append(".");
				buf.append("write();");
			} else {
				buf.append(chartname);
				buf.append(".");
				buf.append(" write('");
				buf.append(this.chartpnl);
				buf.append("');");
			}
			buf.append("</script>");
		}

		return buf.toString();

	}

	/* 标识：2580 增加是否为总裁桌面发来的请求参数(特殊处理) xiaoyun 2014-6-18 start */
	/*
	 * 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全(增加x-轴总字数参数，用于计算是否需要旋转) xiaoyun
	 * 2014-7-9 start
	 */
	/**
	 * 输出饼柱状图
	 * 
	 * @param chart_type
	 * @biDesk 是否为总裁桌面发来的请求
	 * @return
	 */
	// public String outPieBarChart(ArrayList list,HashMap event)
	public String outPieBarChart(ArrayList list, HashMap event, String biDesk,
			String total,boolean pieoutin)
	/*
	 * 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全(增加x-轴总字数参数，用于计算是否需要旋转) xiaoyun
	 * 2014-7-9 end
	 */
	/* 标识：2580 增加是否为总裁桌面发来的请求参数(特殊处理) xiaoyun 2014-6-18 end */
	{
		StringBuffer buf = new StringBuffer();
		String tmp = "";
		String chartname = "chart";
		if (!(this.chartpnl == null || this.chartpnl.length() == 0)) {
            chartname = chartname + this.chartpnl;
        }
		/**
		 * 如果未定义输出面板,则输出js， 在同一个页面中有多个图形时，如果每个图形前面都输出JS文件时，只有第一个能输出，
		 * 所以对这种有多个图形的页面时，这两个文件放在页面中引用。
		 */
		if ((this.chartpnl == null || this.chartpnl.length() == 0)) {
			buf
			.append("<script type=\"text/javascript\" language=\"javascript\" src=\"/anychart/js/AnyChart.js\"></script>");
			buf
			.append("<script type=\"text/javascript\" language=\"javascript\" src=\"/ajax/basic.js\"></script>");
		}
		buf.append("<script type=\"text/javascript\" language=\"javascript\">");

		buf.append(" var ");
		buf.append(chartname);
		buf.append(" = new AnyChart('/anychart/swf/AnyChart.swf');");
		// buf.append(" var chart = new
		// AnyChart('/anychart/swf/AnyChart.swf');");
		// bgColor
		// buf.append(chartname );
		// buf.append(".");
		// buf.append("bgColor='"+bg_color+"';");

		buf.append(chartname);
		buf.append(".wMode='transparent';");

		Iterator it = event.entrySet().iterator();
		if (event.size() > 0) {
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String eventname = (String) entry.getValue();
				String eventtype = (String) entry.getKey();
				buf.append(chartname);
				buf.append(".addEventListener('");
				buf.append(eventtype);
				buf.append("',");
				buf.append(eventname);
				buf.append(");");
			}
		}
		boolean isPie = false;
		switch (chart_type) {
		case ChartConstants.PIE: // 平面
		{
			isPie = true;
		}
		case ChartConstants.PIE_3D:// 立体
		{
			tmp = outPieXml(list, biDesk,pieoutin);
			tmp = SafeCode.encode(tmp);

			break;
		}
		case ChartConstants.VERTICAL_BAR:
		case ChartConstants.VERTICAL_BAR_3D:
		case ChartConstants.HORIZONTAL_BAR:
		case ChartConstants.HORIZONTAL_BAR_3D: {
			/* 标识：2580 立体直方图下方红色方框标示占用空间过大,去除标示内容 xiaoyun 2014-6-18 start */
			// tmp=outBarXml(list);
			/*
			 * 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全(增加x-轴总字数参数，用于计算是否需要旋转)
			 * xiaoyun 2014-7-9 start
			 */
			tmp = outBarXml(list, biDesk, total);
			/*
			 * 标识：2749 总裁桌面：立体直方图显示连续工龄统计项时，文件显示不全(增加x-轴总字数参数，用于计算是否需要旋转)
			 * xiaoyun 2014-7-9 end
			 */
			/* 标识：2580 立体直方图下方红色方框标示占用空间过大,去除标示内容 xiaoyun 2014-6-18 end */
			// System.out.println(tmp);
			tmp = SafeCode.encode(tmp);
			break;
		}
		case ChartConstants.CATEGORY_BAR: // 分组柱状图
		case ChartConstants.CATEGORY_3DBAR:
		case ChartConstants.CATEGORY_HORIZONTAL_BAR: // 分组柱状图
		case ChartConstants.CATEGORY_HORIZONTAL_3DBAR: {
			tmp = outGalleryBarChartXml(list);
			tmp = SafeCode.encode(tmp);
			break;
		}
		case ChartConstants.Bar_Line_CHART: {

			tmp = outBarLineXml(list);
			// System.out.println(tmp);
			tmp = SafeCode.encode(tmp);
			break;
		}
		}
		
		buf.append("  var xmldata='");
		buf.append(tmp);
		buf.append("';");
		buf.append(" xmldata=getDecodeStr(xmldata);");
		buf.append(chartname);
		buf.append(".");
		/** 宽度为负1时,按100%宽度显示图表 */
		if (this.width == -1) {
				buf.append("width ='100%';");
		} else {
			buf.append("width =");
			buf.append(this.width);
			buf.append(";");
		}
		buf.append(chartname);
		buf.append(".");
		buf.append(" height =");
		if (this.height == -1) {
            buf.append("'100%';");
        } else {
            buf.append(this.height);
        }
		buf.append(";");
		buf.append(chartname);
		buf.append(".");
		buf.append("setData(xmldata);");
		if (this.chartpnl == null || this.chartpnl.length() == 0) {
			buf.append(chartname);
			buf.append(".");
			buf.append("write();");
		} else {
			buf.append(chartname);
			buf.append(".");
			buf.append(" write('");
			buf.append(this.chartpnl);
			buf.append("');");
		}
		buf.append("</script>");

		return buf.toString();
	}

	/**
	 * 不同皮肤用灰色边线框 <chart_background> <border enabled="true" color="gray">
	 * </border> </chart_background>
	 * 
	 * @param chart_settings
	 */
	private void addBackgroundBorderStyle(Element chart_settings) {
		Element chart_background = new Element("chart_background");
		chart_settings.addContent(chart_background);
		Element border = new Element("border");
		chart_background.addContent(border);
		border.setAttribute("enabled", "true");
		border.setAttribute("color", "gray");
		border.setAttribute("opacity", "0.1");
		// <fill type="Solid" color="RoyalBlue" opacity="0.5" />
		// 设置透明化
		if (transparent) {
			border.setAttribute("opacity", "0.0");

			// 背景透明
			Element fill = new Element("fill");
			fill.setAttribute("type", "Solid");
			fill.setAttribute("color", "white");
			fill.setAttribute("opacity", "0");
			chart_background.addContent(fill);

			// x、y标度轴透明
			Element data_plot_background = new Element("data_plot_background");
			chart_settings.addContent(data_plot_background);
			Element data_fill = (Element) fill.clone();
			data_plot_background.addContent(data_fill);
			Element x_axis_plane = new Element("x_axis_plane");
			Element x_fill = (Element) fill.clone();
			x_axis_plane.addContent(x_fill);
			Element y_axis_plane = new Element("y_axis_plane");
			Element y_fill = (Element) fill.clone();
			y_axis_plane.addContent(y_fill);
			data_plot_background.addContent(x_axis_plane);
			data_plot_background.addContent(y_axis_plane);
		}
	}

	/**
	 * 不同皮肤用灰色边线框 <chart_background> <border enabled="true" color="gray">
	 * </border> </chart_background>
	 * 
	 * @param chart_settings
	 */
	private void addBackgroundBorderStyle1(Element chart_settings) {
		Element chart_background = new Element("chart_background");
		chart_settings.addContent(chart_background);
		Element border = new Element("border");
		chart_background.addContent(border);
		border.setAttribute("enabled", "true");
		border.setAttribute("color", "white");
		border.setAttribute("opacity", "0.1");
		Element fill1 = new Element("fill");
		chart_background.addContent(fill1);
		fill1.setAttribute("type", "Solid");
		fill1.setAttribute("color", "white");
		fill1.setAttribute("opacity", "0.1");
		// <fill type="Solid" color="RoyalBlue" opacity="0.5" />
		// 设置透明化
		if (transparent) {
			border.setAttribute("opacity", "0.0");

			// 背景透明
			Element fill = new Element("fill");
			fill.setAttribute("type", "Solid");
			fill.setAttribute("color", "white");
			fill.setAttribute("opacity", "0");
			chart_background.addContent(fill);

			// x、y标度轴透明
			Element data_plot_background = new Element("data_plot_background");
			chart_settings.addContent(data_plot_background);
			Element data_fill = (Element) fill.clone();
			data_plot_background.addContent(data_fill);
			Element x_axis_plane = new Element("x_axis_plane");
			Element x_fill = (Element) fill.clone();
			x_axis_plane.addContent(x_fill);
			Element y_axis_plane = new Element("y_axis_plane");
			Element y_fill = (Element) fill.clone();
			y_axis_plane.addContent(y_fill);
			data_plot_background.addContent(x_axis_plane);
			data_plot_background.addContent(y_axis_plane);
		}
	}


	public static String computeXangle(ArrayList list) {
		int size = list.size();
		int angle = 45;
		if (size <= 3) {
            return "";
        } else if(size>3&&size<11) {
            angle = 45;
        } else if(size>10&&size<16) {
            angle = 75;
        } else {
            angle = 90;//最大不能设置成90  会显示异常的
        }
	
		return "" + angle;
	}

	/**
	 * 计算所有统计项的值的和
	 * 
	 * @param value
	 * @return
	 * @author liuy
	 */
	private double sumStaticItemValue(ArrayList list) {
		double sum = 0;
		for (int i = 0; i < list.size(); i++) {
			CommonData vo = (CommonData) list.get(i);
			String datavalue = vo.getDataValue();
			sum = sum + Double.parseDouble(datavalue);
		}
		return sum;
	}

	/**
	 * 汉化无数据提示
	 * 
	 * @param settings
	 * @author liuy
	 */
	private void setNo_data(Element settings) {
		Element no_data = new Element("no_data");
		settings.addContent(no_data);
		Element label = new Element("label");
		no_data.addContent(label);
		Element text = new Element("text");
		label.addContent(text);
		text.setText("无数据");
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

	public String getChartpnl() {
		return chartpnl;
	}

	public void setChartpnl(String chartpnl) {
		this.chartpnl = chartpnl;
	}

	public String getBg_color() {
		return bg_color;
	}

	public void setBg_color(String bg_color) {
		this.bg_color = bg_color;
	}

	public float getMaxvalue() {
		return maxvalue;
	}

	public void setMaxvalue(float maxvalue) {
		this.maxvalue = maxvalue;
	}

	public float getMinvalue() {
		return minvalue;
	}

	public void setMinvalue(float minvalue) {
		this.minvalue = minvalue;
	}

	public void setXangle(int xangle) {
		this.xangle = xangle;
	}

	/**
	 * 如果system.properties文件问配置默认为12px
	 */
	private void initFontSize() {
		String tooltipfontsizevalue = SystemConfig
		.getPropertyValue("charttooltipfontsize");
		tooltipfontsize = tooltipfontsizevalue.length() > 0 ? tooltipfontsizevalue
				: tooltipfontsize;
		String labelfontsizevalue = SystemConfig
		.getPropertyValue("chartlabelfontsize");
		labelfontsize = labelfontsizevalue.length() > 0 ? labelfontsizevalue
				: labelfontsize;
		String legfontsizevalue = SystemConfig
		.getPropertyValue("chartlegfontsize");
		legfontsize = legfontsizevalue.length() > 0 ? legfontsizevalue
				: legfontsize;
	}

	/**
	 * 柱状分组 图
	 * 
	 * @param groupData
	 *            数据list，格式为：ArrayList<ArrayList[String
	 *            groupName,CommonData,CommonData,........]>
	 *            里面存具体数据的list必须在第一个位置放置String类型分组名称 例如： ArrayList groupList =
	 *            new ArrayList(); ArrayList group1 = new ArrayList();
	 *            group1.add("分组1"); group1.add(CommonData);......
	 *            groupList.add(group1);........
	 * @return
	 */
	public String outGroupBarXml(ArrayList groupData) {
		StringBuilder sb = new StringBuilder();
		Element root = new Element("anychart");
		Document myDocument = new Document(root);

		Element settings = new Element("settings");
		root.addContent(settings);
		Element animation = new Element("animation");
		if (this.animation) {
            animation.setAttribute("enabled", "True");
        } else {
            animation.setAttribute("enabled", "False");
        }
		settings.addContent(animation);

		Element margin = new Element("margin");
		root.addContent(margin);
		margin.setAttribute("all", "0");

		Element charts = new Element("charts");
		root.addContent(charts);
		Element chart = new Element("chart");
		charts.addContent(chart);
		// 竖向显示
		chart.setAttribute("plot_type", "CategorizedVertical");
		Element data_plot_settings = new Element("data_plot_settings");
		chart.addContent(data_plot_settings);
		data_plot_settings.setAttribute("data_plot_settings", "Bar");

		if (this.chart_type == ChartConstants.VERTICAL_BAR_3D) {
            data_plot_settings.setAttribute("enable_3d_mode", "true");
        } else {
            data_plot_settings.setAttribute("enable_3d_mode", "false");
        }

		Element bar_series = new Element("bar_series");
		data_plot_settings.addContent(bar_series);
		bar_series.setAttribute("group_padding", "0.5");
		// <label_settings enabled="True" rotation="0">
		Element label_settings = new Element("label_settings");
		bar_series.addContent(label_settings);
		label_settings.setAttribute("enabled", "True");
		// 旋转角度
		label_settings.setAttribute("rotation", "0");

		Element lformat = new Element("format");
		label_settings.addContent(lformat);
		lformat.setText("{%Value}{numDecimals:" + this.numDecimals + "}");

		Element tooltip_settings = new Element("tooltip_settings");
		bar_series.addContent(tooltip_settings);
		tooltip_settings.setAttribute("enabled", "true");
		Element tformat = new Element("format");
		tooltip_settings.addContent(tformat);
		tformat.setText("{%SeriesName} \n 值:{%Value}{numDecimals:"
				+ this.numDecimals
				+ "} \n {%SeriesName}中比例:{%YPercentOfSeries}{numDecimals:"
				+ this.numDecimals + "}%");

		Element data = new Element("data");
		chart.addContent(data);
		for (int i = 0; i < groupData.size(); i++) {
			ArrayList dataList = (ArrayList) groupData.get(i);
			Element series = new Element("series");
			data.addContent(series);
			series.setAttribute("name", dataList.get(0).toString());
			series.setAttribute("type", "Bar");
			for (int k = 1; k < dataList.size(); k++) {
				CommonData cd = (CommonData) dataList.get(k);
				Element point = new Element("point");
				point.setAttribute("name", cd.getDataName());
				point.setAttribute("y", cd.getDataValue());
				series.addContent(point);
			}
		}

		Element chart_settings = new Element("chart_settings");
		chart.addContent(chart_settings);
		addBackgroundBorderStyle1(chart_settings);
		Element title = new Element("title");
		Element text = new Element("text");
		text.setText(this.title);
		title.addContent(text);
		chart_settings.addContent(title);
		// enabled="true" position="bottom" ignore_auto_item="false"
		// width="100%"
		Element legend = new Element("legend");
		chart_settings.addContent(legend);
		legend.setAttribute("enabled", "true");
		legend.setAttribute("position", "bottom");
		legend.setAttribute("ignore_auto_item", "false");
		legend.setAttribute("width", "100%");

		Element legendTitle = new Element("title");
		legendTitle.setAttribute("enabled", "false");
		legend.addContent(legendTitle);

		Element legendFormat = new Element("format");
		legend.addContent(legendFormat);
		legendFormat
		.setText("{%Icon} ({%Name}{enabled:false}) ({%YValue}{numDecimals:"
				+ this.numDecimals + "})");

		// <axes>
		// <y_axis>
		// <title>
		// <text>Sales</text>
		// </title>
		// </y_axis>
		// <x_axis>
		// <labels align="Outside"/>
		// <title>
		// <text>Retail Channel</text>
		// </title>
		// </x_axis>
		// </axes>
		Element axes = new Element("axes");
		chart_settings.addContent(axes);

		Element y_axis = new Element("y_axis");
		Element yTitle = new Element("title");
		Element yText = new Element("text");
		yText.setText("");
		yTitle.addContent(yText);
		y_axis.addContent(yTitle);
		axes.addContent(y_axis);

		Element x_axis = new Element("x_axis");
		Element xTitle = new Element("title");
		Element xText = new Element("text");
		xText.setText("");
		xTitle.addContent(xText);
		x_axis.addContent(xTitle);
		axes.addContent(x_axis);

		try {
			XMLOutputter outputter = new XMLOutputter();
			Format format0 = Format.getPrettyFormat();
			format0.setEncoding("UTF-8");
			outputter.setFormat(format0);
			sb.append(outputter.outputString(myDocument));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return sb.toString();
	}

	public String getIsneedsum() {
		return isneedsum;
	}

	public void setIsneedsum(String isneedsum) {
		this.isneedsum = isneedsum;
	}

	public String getTooltip_enabled() {
		return tooltip_enabled;
	}

	public void setTooltip_enabled(String tooltip_enabled) {
		this.tooltip_enabled = tooltip_enabled;
	}

	/**
	 * 是否透明化
	 * 
	 * @param transparent
	 */
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

}
