/**
 *
 */
package com.hjsj.hrms.businessobject.sys;

import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;


/**
 * author : xiegh 
 *
 * date : 20180426
 *
 * ehr图表重构
 *
 * anychart图表 ——————>Echarts图表
 *
 * 现图表支持：平面饼状图、平面柱状图、平面折线图、分组柱状图、柱状占比图和雷达图。
 *
 * 方法：
 *
 * createEchartBuffer()  
 *
 * initEchartParameter() 
 *
 * outEchartRadarXml() 
 *
 * outEchartBarAndLineXml() 
 *
 * outEchartGalleryBarXml()
 *
 * outEchartLineXml()
 *
 * outEchartPieXml()
 *
 * 新加属性：
 * StringBuffer formatLabel 刻度标签样式
 * StringBuffer itemStyleBuf 柱状图item样式
 */
public class EchartsBo {
	private boolean showpercent = true;//默认显示百分比
	DecimalFormat df1 = null;
	StringBuffer formatLabel = null;
	StringBuffer itemStyleBuf = null;
	StringBuffer averMarkLine = null;
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
	private boolean isneedtitle = true;
	private boolean issupplydecimals = false;//是否给整数补充小数
	private String orient = "horizontal";//布局方式，默认为水平布局，可选为：'horizontal' | 'vertical'

	public void setOrient(String orient) {
		this.orient = orient;
	}

	public String getIslabelname() {
		return islabelname;
	}

	public void setIslabelname(String islabelname) {
		this.islabelname = islabelname;
	}

	public void setNumDecimals(int numDecimals) {
		this.numDecimals = numDecimals;

		df1 = (DecimalFormat) DecimalFormat.getInstance();
		if (this.numDecimals == 0) {
            df1.applyPattern("0");
        } else {
			String fmtstr = StringUtils.rightPad("0.", 2 + this.numDecimals,"0");
			df1.applyPattern(fmtstr);
		}
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

	public EchartsBo() {
		initFontSize();
	}

	public boolean isShowpercent() {
		return showpercent;
	}

	public void setShowpercent(boolean showpercent) {
		this.showpercent = showpercent;
	}

	public EchartsBo(String title, int chart_type, int width, int height) {
		this.chart_type = chart_type;
		this.title = title;
		this.width = width;
		this.height = height;
		initFontSize();
	}

	public EchartsBo(String title, int chart_type, int width, int height,
					 String label_enabled) {
		this.chart_type = chart_type;
		this.title = title;
		this.width = width;
		this.height = height;
		this.label_enabled = label_enabled;
		initFontSize();
		initEchartParameter();
	}

	/**
	 *
	 */
	private void initEchartParameter() {

/*		formatLabel = new StringBuffer();//格式化刻度标签
		formatLabel.append(" function(params){\n")
         .append("var newParamsName = \"\";\n")
         .append("var paramsNameNumber = params.length;\n")
         .append("var provideNumber = 4;\n")
         .append("var rowNumber = Math.ceil(paramsNameNumber / provideNumber);\n")
         .append("if (paramsNameNumber > provideNumber) {\n")
             .append("for (var p = 0; p < rowNumber; p++) {\n")
                 .append("var tempStr = \"\";\n")
                 .append("var start = p * provideNumber;\n")
                 .append("var end = start + provideNumber;\n")
                 .append("if (p == rowNumber - 1) {\n")
                     .append("tempStr = params.substring(start, paramsNameNumber);\n")
                 .append("} else {\n")
                     .append("tempStr = params.substring(start, end) + '\\n';\n")
                 .append("}\n")
                 .append("newParamsName += tempStr;\n")
             .append("}\n")
         .append("} else {\n")
             .append("newParamsName = params;\n")
         .append("}\n")
         .append("return newParamsName;\n")
     .append("}\n");*/

		formatLabel = new StringBuffer();//格式化刻度标签
		formatLabel.append(" function(value){\n")
				.append("var ret = \"\";")//拼接加\n返回的类目项
				.append("var maxLength =${MAXLENGTH};")//${MAXLENGTH}变量 每项显示文字个数 wangb 20180706
//		        .append("var maxLength = "+(xangle==90?"1":"4")+";")//每项显示文字个数
				.append("var valLength = value.length;")//X轴类目项的文字个数
				.append("var rowN = Math.ceil(valLength / maxLength);") //类目项需要换行的行数
				.append("if (rowN > 1)")//如果类目项的文字大于3,
				.append("{\n")
				.append("for (var i = 0; i < rowN; i++) {\n")
				.append("var temp = \"\";")//每次截取的字符串
				.append("var start = i * maxLength;")//开始截取的位置
				.append("var end = start + maxLength;")//结束截取的位置
				//这里也可以加一个是否是最后一行的判断，但是不加也没有影响，那就不加吧
				.append("temp = value.substring(start, end) + \"\\n\";")
				.append("ret += temp;") //凭借最终的字符串
				.append("}\n")
				.append("return ret;")
				.append("}\n")
				.append("else {\n")
				.append("return value;\n")
				.append("}\n")
				.append("}\n");


		itemStyleBuf = new StringBuffer();
		itemStyleBuf.append("itemStyle: {\n")
				//.append("barGap:'30%',")
				//.append("barCategoryGap:'10%',")
				.append(" shadowBlur: 10,")
				.append("normal: {\n")
				.append("color: function(params) {")
//		    	.append("var colorList = ['#C33531','#EFE42A','#64BD3D','#EE9201','#29AAE3','#B74AE5','#0AAF9F','#E89589','#FF7F50','#FF83FA','#9400D3','#848484','#ed464c','#f5c101','#0aa6e8','#1fcf03','#005eaa','#339ca8','#d9b014','#32a487','#333333','#FFB6C1','#FF69B4','#D8BFD8','#DDA0DD','#FF00FF'];")
				.append("var colorList = ${COLORLIST};")// ${COLORLIST} 变量 通过统计项动态配色 wangb 20180706
				.append("return colorList[params.dataIndex]")
				.append("},")
				.append("label: {\n")
				.append("show: "+label_enabled+",${labelFormatter},\n")//label_enabled是否显示bar上面的标签  ${labelFormatter} bar上面显示的数值，整数型是否添加小数位u
				.append("position: 'top',\n")
				.append("textStyle: {\n")
				.append("color: '#615a5a'\n")
				.append("}\n")
//	    .append("formatter:function(params){\n")
//	    .append("if(params.value===0){\n")
//	    .append("return '';\n")
//	    .append("} else { return params.value; }\n")
				.append("}}}\n");


		averMarkLine = new StringBuffer();
		averMarkLine.append("markLine : {\n")
				.append("silent:true,\n")
				.append("lineStyle:{color: {type: 'radial',x: 0.5,y: 0.5,r: 0.5,colorStops: [{offset: 0, color: 'red'}, {offset: 1, color: 'red'}],globalCoord: true}},\n")
				.append("data : [\n")
				.append("{type : 'average', name: '平均值'}\n")
				.append("]\n")
				.append("}\n");
	}
	/**
	 * 输出Echarts图表Json
	 * @param value
	 * @param pointClick
	 * @param biDesk
	 * @param total
	 * @return
	 */
	public String createEchartBuffer(Object value, String pointClick, String biDesk,String total) {
		StringBuffer chartJson = null;
		try {
			String option = "";
			ArrayList list = null;
			//引入echart文件
			chartJson = createHeaderData();

			//平面折现图 /*this.chart_type == 1000*/
			breakcode : if(value instanceof HashMap){
				HashMap map = (HashMap)value;

				if(checkMapStatus(map)){ // =true:数据为空
					processNullDataTip(chartJson);
					break breakcode;
				}
				if(this.chart_type == 41)//能力素质 界面中的雷达图标识是 41  数据结构是HashMap
                {
                    option = outEchartRadarInMap(map, biDesk, total);
                } else if(this.chart_type == 33)//二维统计柱状图
                {
                    option = outEchartSacredFormXml(map, biDesk, total);
                } else if(this.chart_type == 1000)//常用统计折线图
                {
                    option = outEchartLineXml(map,biDesk,total);
                } else if(this.chart_type == 11) //报表统计折线图
                {
                    option = outEchartLineGroupsXml(map,biDesk,total);
                } else {
                    option = outEchartLineTransDataXml(map,biDesk,total);
                }
			}else{
				list = (ArrayList)value;
				if(checkDataListStatus(list)){//数据为空时
					processNullDataTip(chartJson);
					break breakcode;
				}
				if(this.chart_type == 0)//值不存在时，默认值为12
                {
                    this.chart_type = 12;
                }
				//分组柱状图
				if(this.chart_type == 29){
					option = outEchartGalleryBarXml(list, biDesk, total);
				}
				//二维统计柱状图
				if(this.chart_type == 299){
					option = outEchartTwoDimXml(list, biDesk, total);
				}
				//11： 平面柱状图 12 or 31 ：立体柱状图
				if(this.chart_type == 11 || this.chart_type == 12 || this.chart_type == 31){
					option = outEchartBarXml(list, biDesk, total);
				}
				//雷达图
				if(this.chart_type == 55) {
                    option = outEchartRadarXml(list, biDesk, total);
                }

				//20：平面饼状图 5：立体饼状图
				if(this.chart_type == 20 || this.chart_type == 5){
					option = outEchartPieXml(list, biDesk, total);
				}
				//柱状占比图
				if(this.chart_type == 40){
					option = outEchartBarAndLineXml(list, biDesk, total);
				}
			}

			chartJson.append(option);

			//添加图表点击事件
			if(null != pointClick && pointClick.length() > 0) {
                chartJson.append(addEventManager(pointClick));
            }
			// 使用刚指定的配置项和数据显示图表。
			chartJson.append("myChart"+chartpnl+".setOption(option);");
			if(this.width == -1){// 解决IE浏览器 自适应闪烁问题  wangb 20181108 bug 41777
				chartJson.append("var userAgent = navigator.userAgent;");
				chartJson.append("var isOpera = userAgent.indexOf(\"Opera\") > -1;");
				chartJson.append("var isIE = userAgent.indexOf(\"compatible\") > -1 && userAgent.indexOf(\"MSIE\") > -1 && !isOpera;");
				chartJson.append("if(isIE){");//解决ie浏览器 统计图不自适应问题,和闪烁问题
				chartJson.append("	window.onresize=function(){");
				chartJson.append("    setTimeout(function(){myChart"+chartpnl+".resize();},0);");
				chartJson.append("  };");
				chartJson.append("  window.onload = function(){window.onresize(); };");
				chartJson.append("}else{");
				chartJson.append("	window.onresize= function(){chartDiv.style.width='100%';chartDiv.firstChild.style.width='100%';"+(this.height== -1? "chartDiv.style.height='100%';chartDiv.firstChild.style.height='100%';":"")+"myChart"+chartpnl+".resize();};");
				chartJson.append("}");
			}
			chartJson.append("</script>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chartJson.toString();
	}

	/**
	 * @param list
	 */
	private boolean checkDataListStatus(ArrayList list) {
		boolean isNull = true;
		if(list == null || list.size() == 0) {
            return isNull;
        }
		for(int k =0;k <list.size();k++){
			Object obj = list.get(k);
			String classname = obj.getClass().getName();
			if("org.apache.commons.beanutils.LazyDynaBean".equals(classname)){
				LazyDynaBean bean = (LazyDynaBean)list.get(k);
				ArrayList beanList = (ArrayList)bean.get("dataList");
				if(beanList.size() > 0) {
                    isNull = false;
                }
			}else {
                isNull = false;
            }
		}
		return isNull;
	}

	/**
	 * @param map
	 */
	private boolean checkMapStatus(HashMap map) {
		boolean isNull = true;
		try {
			if(map == null || map.size() == 0) {
                return isNull;
            }
			for(Iterator it = map.entrySet().iterator();it.hasNext();){
				Entry entry  = (Entry) it.next();
				if("minmax".equals(entry.getKey())) {
                    continue;
                }
				ArrayList entrylist = (ArrayList) entry.getValue();
				if(entrylist !=null && entrylist.size() > 0){
					isNull = false;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isNull;
	}

	public String outEchartSacredFormXml(HashMap map, String biDesk, String total) {
		String yAxisStr = "";
		StringBuffer buf = null;
		boolean decimalsflag =false;//整数位是否补上小数位标识
		try {
			Map<String,ArrayList> linkedMap = new LinkedHashMap<String,ArrayList>();
			for(Iterator it = map.entrySet().iterator();it.hasNext();){
				Entry entry = (Entry) it.next();
				String key = (String)entry.getKey();
				if("合计".equals(key)) {
                    continue;
                }
				ArrayList list = (ArrayList)entry.getValue();
				for(int i = 0; i< list.size();i++){
					CommonData data = (CommonData)list.get(i);
					String name = data.getDataName();
					if("合计".equals(name)) {
                        continue;
                    }
					String value = data.getDataValue();
					if(linkedMap.containsKey(name)){
						ArrayList arraylist = linkedMap.get(name);
						arraylist.add(new CommonData(value,key));
					}else{
						ArrayList lis = new ArrayList();
						lis.add(new CommonData(value,key));
						linkedMap.put(name, lis);
					}
				}

			}

			Map<String,ArrayList> finallinkedMap = new LinkedHashMap<String,ArrayList>();
			ListIterator<Entry<String,ArrayList>> k=new ArrayList<Entry<String,ArrayList>>(linkedMap.entrySet()).listIterator(linkedMap.size());
			while(k.hasPrevious()) {  //这个就是反向遍历  目的：为了是图表指标顺序与下面表格数据行顺序保持一致
				Entry<String, ArrayList> entry=k.previous();
				String key = entry.getKey();
				yAxisStr += ",'" + key + "'";
				ArrayList list = (ArrayList)entry.getValue();
				for(int i = 0; i< list.size();i++){
					CommonData data = (CommonData)list.get(i);
					String name = data.getDataName();
					String value = data.getDataValue();
					if(Double.parseDouble(value) %1 > 0) {
                        decimalsflag = true;
                    }
					if(finallinkedMap.containsKey(name)){
						ArrayList arraylist = finallinkedMap.get(name);
						arraylist.add(value);
					}else{
						ArrayList lis = new ArrayList();
						lis.add(value);
						finallinkedMap.put(name, lis);
					}
				}
			}
			StringBuffer seriesData = new StringBuffer();
			for(Entry<String, ArrayList> mapEle : finallinkedMap.entrySet()){//经过三次遍历 组装成最终想要的数据option
				String key = mapEle.getKey();
				ArrayList list = mapEle.getValue();
				seriesData.append(",{")
						.append("name: '"+key+"',")
						.append("type: 'bar',")
						.append("stack: '总量',")
						.append("label: {")
						.append("normal: {")
						.append("show: true,")
						.append(labelFormatter(decimalsflag,false)+",")
						.append("position: 'insideRight'")
						.append("},")
						.append("formatter:function(params){\n")
						.append("if(params.value===0){\n")
						.append(" return '';\n")
						.append("} else { return params.value; }\n")
						.append("}")
						.append("},")
						.append("data:" + list.toString())
						.append("}");
			}

			if(yAxisStr.length() > 0) {
                yAxisStr = yAxisStr.substring(1);
            }

			buf = new StringBuffer();
			buf.append("option = {")
					.append("tooltip : {")
					.append("trigger: 'axis',")
					.append("confine:true,")
					.append("axisPointer : {")            // 坐标轴指示器，坐标轴触发有效
					.append("type : 'shadow'")        // 默认为直线，可选为：'line' | 'shadow'
					.append("}")
					.append("},")
					.append("toolbox:{show:true,right:'1%',feature:{saveAsImage:{type:'png',title:'下载',name:'"+(this.title.length()==0?"条形堆叠图":this.title)+"',pixelRatio:2}}},")
					.append("grid: {")
					.append("left: '3%',")
					.append("right: '4%',")
					//.append("bottom: '3%',")
					.append("containLabel: true")
					.append("},")
					.append("xAxis:  {")
					.append("type: 'value'")
					.append("},")
					.append("yAxis: {")
					.append("type: 'category',")
					.append("data: ["+yAxisStr+"],")
					.append("axisLabel:{margin:16}")//堆叠统计图 y轴 内容与轴线重叠  wangb 20180718 bug 38674
					.append("},")
					.append("series: [")
					.append(seriesData.length() > 0 ? seriesData.toString().substring(1) : "")
					.append("]")
					.append(yDataScroll(linkedMap.size()))
					.append("};");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 处理空数据提示
	 * @param chartJson
	 */
	private void processNullDataTip(StringBuffer chartJson) {
		try {
			chartJson.append("option ={};");
			chartJson.append("var chartDivObj =  document.getElementById(chartDivId);");
			chartJson.append("if(chartDivObj){");
			chartJson.append("chartDivObj.style.color = 'black';");
			chartJson.append("chartDivObj.style.textAlign='center';");
			//报表管理、报表分析下，没数据时，提示信息样式修改  wangb 20180703 bug 37866
			chartJson.append("chartDivObj.style.height=20;");
//			chartJson.append("chartDivObj.style.paddingTop=200;");
			chartJson.append("chartDivObj.style.paddingTop=20;");
//			chartJson.append("chartDivObj.style.fontSize=40;");
			chartJson.append("chartDivObj.style.fontSize=20;");
//			chartJson.append("chartDivObj.style.width='98%';");
			chartJson.append("chartDivObj.style.width=document.body.clientWidth-40;");
			chartJson.append("chartDivObj.innerHTML='无数据';");
			chartJson.append("}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String outEchartRadarInMap(HashMap map, String biDesk, String total) {
		StringBuffer buf = null;
		try {
			String minmax = (String)map.get("minmax");
			String maxvalue = df1.format(Double.parseDouble(minmax.split(",")[1]));
			String indicatorData = "";
			String seriesData ="";
			Iterator it = map.entrySet().iterator();//遍历map 柱状折线图数据
			String linename = "";
			//StringBuffer seriesData = new StringBuffer("[");
			boolean isFirst = true;
			StringBuffer legendData = new StringBuffer();
			int index = 0;
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				if("minmax".equals(key)) {
                    continue;
                }
				ArrayList list = (ArrayList)entry.getValue();
				String seriesValue = "";
				for(int i =0;i<list.size();i++){
					CommonData comm = (CommonData)list.get(i);
					String name = comm.getDataName();
					String value = comm.getDataValue();
					seriesValue += "," + df1.format(Double.parseDouble(value));
					if(isFirst) {
                        indicatorData +=",{ name: '"+name+"', max: "+maxvalue+"}";
                    }
				}
				legendData.append(",'"+key+"'");
				seriesValue = "{ value : [" + (seriesValue.length()>0?seriesValue.substring(1):"")+ "], name : '"+key+"',itemStyle:{color:"+itemColor(null, index)+"} },";
				seriesData += seriesValue;
				isFirst = false;
				index++;
			}

			if(indicatorData.length() > 0) {
                indicatorData = indicatorData.substring(1);
            }

			if(seriesData.length() > 0) {
                seriesData = seriesData.substring(0, seriesData.length()-1);
            }
			buf = new StringBuffer();
			buf.append("option = {")
					.append("title: {")
					// .append("text: '基础雷达图'")
					.append("},")
					.append("tooltip: {show:true,trigger:'item',confine:true},")
					.append("toolbox:{show:true,right:'1%',feature:{saveAsImage:{type:'png',title:'下载',name:'"+(this.title.length()==0?"雷达图":this.title)+"',pixelRatio:2}}},")
					.append("legend:{")
					.append("bottom:'bottom',")
					.append("data:["+(legendData.length()>0? legendData.substring(1):"")+"]")
					.append("},")
					.append("radar: {")
					.append("name: {")
					.append("textStyle: {")
					.append("color: 'black',")
					.append("borderRadius: 3,")
					.append("padding: [3, 5]")
					.append("}")
					.append("},")
					.append("indicator: [")
					.append(indicatorData)
					.append("]")
					.append("},")
					.append("series: [{")
					.append("type: 'radar',")
					.append("data : [")
					.append(seriesData)
					/*  .append("{")
                          .append("value : ["+seriesValue.substring(1)+"],")
                          .append("name : '"+this.title+"',")
                          //这里的配置显示数值
                          .append("label: {")
                              .append("normal: {")
                                  .append("show: true,")
                                  .append("formatter:function(params) {")
                                      .append("return params.value;")
                                  .append("}")
                              .append("}")
                          .append("}")
                      .append("}")  */
					.append("]")
					.append("}]")
					.append("};");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();

	}

	/**
	 * 雷达图
	 * @param list
	 * @param biDesk
	 * @param total
	 * @return
	 */
	private String outEchartRadarXml(ArrayList list, String biDesk, String total) {
		StringBuffer buf = null;
		try {
			double maxvalue = 0;
			String indicatorData = "";
			String seriesValue = "";
			for(int i =0;i<list.size();i++){
				CommonData comm = (CommonData)list.get(i);
				String value = comm.getDataValue();
				double val = Double.parseDouble(value);
				if( val > maxvalue) {
                    maxvalue = val;
                }
			}
			for(int i =0;i<list.size();i++){
				CommonData comm = (CommonData)list.get(i);
				String name = comm.getDataName();
				String value = comm.getDataValue();
				seriesValue += "," + df1.format(Double.parseDouble(value));
				indicatorData +=",{ name: '"+name+"', max: "+maxvalue+"}";
			}
			if(indicatorData.length() > 0) {
                indicatorData = indicatorData.substring(1);
            }

			buf = new StringBuffer();
			buf.append("option = {")
					.append("title: {")
					// .append("text: '基础雷达图'")
					.append("},")
					.append("toolbox:{show:true,right:'1%',feature:{saveAsImage:{type:'png',title:'下载',name:'"+(this.title.length()==0?"雷达图":this.title)+"',pixelRatio:2}}},\n")
					.append("tooltip: {show:true,trigger:'item',confine:true},")
					.append("radar: {")
					.append("name: {")
					.append("textStyle: {")
					.append("color: 'black',")
					.append("borderRadius: 3,")
					.append("padding: [3, 5]")
					.append("}")
					.append("},")
					.append("indicator: [")
					.append(indicatorData)
					.append("]")
					.append("},")
					.append("series: [{")
					.append("type: 'radar',")
					.append("data : [")
					.append("{")
					.append("value : ["+seriesValue.substring(1)+"]")
					.append(",name : '数据详情'")// bug 38529  统计分析 雷达图 悬浮提示第一行内容显示不对，改为文件提示  wangb 20180704
					//这里的配置显示数值
					.append(",label: {")
					.append("normal: {")
					.append("show: true,")
					.append("formatter:function(params) {")
					.append("return params.value;")
					.append("}")
					.append("}")
					.append("}")
					.append("}")
					.append("]")
					.append("}]")
					.append("};");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 *
	 * 柱状占比图
	 * @param list
	 * @param biDesk
	 * @param total
	 * @return
	 */
	private String outEchartBarAndLineXml(ArrayList list, String biDesk, String total) {
		String xdata = "";
		String ydata ="";
		String percentdata = "";
		double sumValue = 0;//求和
		boolean decimalsflag =false;//整数位是否补上小数位标识
		//初始化数据
		for (int i = 0; i < list.size(); i++) {
			CommonData vo  = (CommonData)list.get(i);
			String name = vo.getDataName();
			String value = vo.getDataValue();
			xdata+=",\""+name+"\"";
			if(Double.parseDouble(value) %1 > 0) {
                decimalsflag = true;
            }
			ydata+=","+df1.format(Double.parseDouble(value))+"";
			sumValue = sumValue + Double.parseDouble(value);
		}
		DecimalFormat df2 = (DecimalFormat) DecimalFormat.getInstance();
		String fmtstr = StringUtils.rightPad("0.", 4,"0");
		df2.applyPattern(fmtstr);
		for (int i = 0; i < list.size(); i++) {
			CommonData vo  = (CommonData)list.get(i);
			String value = vo.getDataValue();
			double y = Double.parseDouble(value);
			percentdata+=","+df2.format(y *100 / sumValue);//显示百分比
		}
		StringBuffer buf = new StringBuffer();
		try {
			buf.append("option = {\n")
					.append("title: {\n")
					.append("text: ''\n")
					.append("},\n")
					.append("toolbox:{show:true,right:'1%',feature:{saveAsImage:{type:'png',title:'下载',name:'"+(this.title.length()==0?"柱状占比图":this.title)+"',pixelRatio:2}}},\n")
					.append("barMaxWidth:80,\n")
					.append("grid: {")
					.append("left: '1%',")
					.append("right: '7%',");
			//.append("bottom: '5%',");
			if(this.width == -1){
				buf.append("top: '10%',");
			}
			buf.append("width:'auto',")
					.append("containLabel: true")
					.append("},")
					.append("tooltip: {\n")
					.append("trigger: 'axis',\n")
//				        .append("axisPointer: {\n")
//				            .append("type: 'cross',\n")
//				            .append("label: {\n")
//				                .append("backgroundColor: '#283b56'\n")
//				            .append("},\n")
//				        .append("}\n")
					.append("formatter:function(params){\n")
					.append("var str = params[0].name +'<br/>';\n")
					.append("str += '<span style=\"display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[0].color + '\"></span>'+params[0].value+'<br/>';\n")
					.append("str += '<span style=\"display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[1].color + '\"></span>'+params[1].value+'%';")
					.append("return str;\n")
					.append("}\n")
					.append("},\n")
					.append("xAxis: [\n")
					.append("{\n")
					.append("type: 'category',\n")
					.append("data:["+xdata.substring(1)+"],\n")
					.append("axisLabel:{\n")
					.append("interval : 0,\n");

			if(list.size() < 5) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
            } else if(list.size() < 16) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "4"));
            } else{
				buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
				buf.append(",rotate :-45\n");
				buf.append(",margin:24\n");
			}
			//if(list.size() > 8 && list.size() <= 20){
			//if(list.size() >3){// bug 38574  统计项个数大于3个不倾斜显示，每行显示2个字  wangb 20180702
//				            		buf.append("formatter :"+formatLabel);
			//}else{
//				            		if(90 == xangle)
//				            			buf.append(",rotate:"+xangle+"\n");
			//}
			buf.append("}\n")
					.append("},\n")
					.append("{\n")
					.append("type: 'category',\n")
					.append("data:[]\n")
					.append("}\n")
					.append("],\n")
					.append("yAxis: [\n")
					.append("{\n")
					.append("type: 'value',\n")
					.append("axisLabel: {formatter: '{value}%'}\n")
					.append("},\n")
					.append("{\n")
					.append("type: 'value'\n")
					.append("}\n")
					.append("],\n")
					.append("series: [\n")
					.append("{\n")
					// .append("name:'预购队列',\n")
					.append("type:'bar',\n")
//				            .append("xAxisIndex: 1,\n")
					.append("yAxisIndex: 1,\n")
					.append(itemStyleBuf.toString().replace("${COLORLIST}",itemColor(ydata.substring(1),-1)).replace("${labelFormatter}",labelFormatter(decimalsflag,true)))
					.append(",data:["+ydata.substring(1)+"]\n")
					.append("},\n")
					.append("{\n")
					//.append("name:'最新成交价',\n")
					.append("type:'line',\n")
					.append("data:["+percentdata.substring(1)+"]\n")
					.append("}\n")
					.append("]\n")
					.append(xDataScroll(list.size(), biDesk))
					.append("};\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 分组柱状图
	 * @param datalist
	 * @param biDesk
	 * @param total
	 * @return
	 */
	/*private String outEchartGalleryBarXml(ArrayList datalist, String biDesk, String total) {


		StringBuffer buf = null;
		ArrayList list = null;
		try {
			String xAxisData = "";
			HashMap map = new HashMap();
			for(int k =0;k <datalist.size();k++){
				LazyDynaBean bean = (LazyDynaBean)datalist.get(k);
				String key = (String) bean.get("categoryName");
				xAxisData +=",'" + key + "'";
				ArrayList beanList = (ArrayList)bean.get("dataList");
				for(int j = 0;j<beanList.size();j++){
					CommonData commonData = (CommonData)beanList.get(j);
					String name = commonData.getDataName();
					name = name==null?"":name;

					String value = commonData.getDataValue();
					value = value == null?"":value;

					if(map.containsKey(name)){
						ArrayList list1 = (ArrayList)map.get(name);
						list1.add(df1.format(Double.parseDouble(value)));
					}else{
						ArrayList barlist = new ArrayList();
						barlist.add(df1.format(Double.parseDouble(value)));
						map.put(name, barlist);
					}
				}
			}
			map.put("categoryName", xAxisData.substring(1));
			String linename = "";
			StringBuffer seriesData = new StringBuffer();
			for(Iterator it = map.entrySet().iterator();it.hasNext();){
				Map.Entry entry = (Map.Entry)it.next();
				String key = (String)entry.getKey();
				StringBuffer objstr = new StringBuffer();
				if("categoryName".equals(key)){
					 linename= (String)entry.getValue();
				}else{
					ArrayList dataList = (ArrayList)entry.getValue();
					String datestr = dataList.toString();
					objstr.append(",{")
					.append("name: '"+key+"',")
					.append("type: 'bar',")
					.append("data:"+datestr)
					.append("}");
				}
				seriesData.append(objstr);
			}
			String seriesDatas = seriesData.toString().substring(1);
			seriesDatas = "[" + seriesDatas + "]";
			buf = new StringBuffer();
			// 指定图表的配置项和数据
			buf.append("option = {\n")
					.append("title : {\n")
					.append("text: '"+this.optionTitle+"',\n")
					.append("left: '70'\n")
					.append("},\n")
			        .append("tooltip : {")
			            .append("trigger: 'axis'")
			        .append("},")
					//.append("legend: {data:["+linename.substring(1)+"],left:'center',padding:[5,10]},\n")
				    .append("xAxis: [{\n")
				        .append("type: 'category',\n")
				        .append("data: ["+linename+"]\n")
				    .append("}],\n")
				    .append("yAxis: [{\n")
				        .append("type: 'value'\n")
				    .append("}],\n")

				    .append("calculable : true,")
				    .append("series:"+seriesDatas+"\n")
				.append("};\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}*/
	public String outEchartTwoDimXml(ArrayList datalist, String biDesk, String total) {
		/*报表管理 */

		StringBuffer buf = new StringBuffer();
		StringBuffer xAxisData = new StringBuffer();
		int count = 0;
		if(datalist.size() > 0) {
            count = ((ArrayList)((LazyDynaBean)datalist.get(0)).get("dataList")).size();//每组统计项
        }
		String[] xAxisName = new String[count];
		ArrayList valueData = new ArrayList();
		boolean decimalsflag =false;//整数位是否补上小数位标识
		try {
			for( int i = 0 ; i < datalist.size() ; i++){
				LazyDynaBean bean = (LazyDynaBean)datalist.get(i);
				xAxisData.append(",'"+ bean.get("categoryName") +"'");
				ArrayList beanList = (ArrayList)bean.get("dataList");
				String[] value = new String[beanList.size()];
				for( int j = 0 ; j < beanList.size() ; j++){
					CommonData commonData = (CommonData)beanList.get(j);
					String name = commonData.getDataName();
					if(Double.parseDouble(commonData.getDataValue()) %1 > 0) {
                        decimalsflag = true;
                    }
					value[j]= df1.format(Double.parseDouble(commonData.getDataValue()))+",";
					if(i == 0) //只需要添加一次即可
                    {
                        xAxisName[j] = "'"+ name + "'";
                    }
				}
				valueData.add(value);
			}

			StringBuffer[] data = new StringBuffer[count];
			for( int i = 0 ; i < data.length ; i++){
				data[i] = new StringBuffer();
			}
			for(int i = 0 ; i < count ; i++){
				for(int j = 0 ; j < valueData.size() ; j++){
					String[] values = (String[])valueData.get(j);
					if(values.length >i) {
                        data[i].append(values[i]);
                    }
				}
				data[i].setLength(data[i].length()-1);
			}
			StringBuffer seriesDatas = new StringBuffer();
			seriesDatas.append("[");
			for( int i = 0 ; i < count ; i++){
				seriesDatas.append("{\n")
						.append("name:"+xAxisName[i]+",\n")
						.append("type:'bar',\n")
						.append("data:["+data[i]+"],\n")
						.append(itemStyleBuf.toString().replace("${COLORLIST}",itemColor(data[i].toString(),i)).replace("${labelFormatter}", labelFormatter(decimalsflag,true)))
						.append("},");
			}
			seriesDatas.setLength(seriesDatas.length()-1);
			seriesDatas.append("]");
//	ArrayList list = null;
//	try {
//		String xAxisData = "";
//		LinkedHashMap map = new LinkedHashMap();
//		for(int k =0;k <datalist.size();k++){
//			LazyDynaBean bean = (LazyDynaBean)datalist.get(k);
//			String key = (String) bean.get("categoryName");
//			xAxisData +=",'" + key + "'";
//			ArrayList beanList = (ArrayList)bean.get("dataList");
//			for(int j = 0;j<beanList.size();j++){
//				CommonData commonData = (CommonData)beanList.get(j);
//				String name = commonData.getDataName();
//				name = name==null?"":name;
//
//				String value = commonData.getDataValue();
//				value = value == null?"":value;
//
//				if(map.containsKey(name)){
//					ArrayList list1 = (ArrayList)map.get(name);
//					list1.add(df1.format(Double.parseDouble(value)));
//				}else{
//					ArrayList barlist = new ArrayList();
//					barlist.add(df1.format(Double.parseDouble(value)));
//					map.put(name, barlist);
//				}
//			}
//		}
//		map.put("categoryName", xAxisData.substring(1));
//		String linename = "";
//		StringBuffer seriesData = new StringBuffer();
//		for(Iterator it = map.entrySet().iterator();it.hasNext();){
//			Map.Entry entry = (Map.Entry)it.next();
//			String key = (String)entry.getKey();
//			StringBuffer objstr = new StringBuffer();
//			if("categoryName".equals(key)){
//				 linename= (String)entry.getValue();
//			}else{
//				ArrayList dataList = (ArrayList)entry.getValue();
//				String datestr = dataList.toString();
//				objstr.append(",{")
//				.append("name: '"+key+"',")
//				.append("type: 'bar',")
//				.append("data:"+datestr)
//				.append("}");
//			}
//			seriesData.append(objstr);
//		}
//		String seriesDatas = "";
//		if(seriesData.length() > 0)
//			seriesDatas = seriesData.toString().substring(1);
//		seriesDatas = "[" + seriesDatas + "]";
			buf = new StringBuffer();
			// 指定图表的配置项和数据
			buf.append("option = {\n")
					.append("title : {\n")
					.append("text: '"+this.title+"',\n")
					.append("left: '70'\n")
					.append("},\n")
					.append("barMaxWidth:80,\n")
					.append("tooltip : {")
					.append("trigger: 'axis'")
					.append("},")
					.append("toolbox:{show:true,right:'1%',feature:{saveAsImage:{type:'png',title:'下载',name:'"+(this.title.length()==0?"柱状图":this.title)+"',pixelRatio:2}}},\n")
					//.append("grid: {left: '1%',right: '10%',bottom: '10%',width:'auto',containLabel: true},\n")
					.append("grid: {left: '1%',right: '10%',width:'auto',containLabel: true},\n")
					//.append("legend: {data:["+linename.substring(1)+"],left:'center',padding:[5,10]},\n")
					.append("xAxis: [{\n")
					.append("type: 'category',\n")
//			        .append("data: ["+linename+"]\n")
					.append("data: ["+xAxisData.substring(1)+"],\n")
					.append("axisLabel:{\n")
					.append("interval : 0,\n");
			if(datalist.size() < 5) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
            } else if(datalist.size() < 16) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "4"));
            } else{
				buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
				buf.append(",rotate :-45\n");
				buf.append(",margin:24\n");
			}
			buf.append("}\n")
					.append("}],\n")
					.append("yAxis: [{\n")
					.append("type: 'value'\n")
					.append("}],\n")

					.append("calculable : true,")
					.append("series:"+seriesDatas+"\n")
					.append(xDataScroll(datalist.size(),biDesk))
					.append("};\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}
	public String outEchartGalleryBarXml(ArrayList datalist, String biDesk, String total) {


		StringBuffer buf = new StringBuffer();
		StringBuffer xAxisData = new StringBuffer();
		boolean decimalsflag =false;//整数位是否补上小数位标识
		int count = 0;
		if(datalist.size() > 0) {
            count = ((ArrayList)((LazyDynaBean)datalist.get(0)).get("dataList")).size();//统计人数分组
        }
		String[] legendDatas = new String[count];
		StringBuffer[] valueData = new StringBuffer[count];
		for( int i = 0 ; i < valueData.length ; i++){
			valueData[i] = new StringBuffer();
		}

		try {
			for( int i = 0 ; i < datalist.size() ; i++){
				LazyDynaBean bean = (LazyDynaBean)datalist.get(i);
				xAxisData.append(",'"+ bean.get("categoryName") +"'");
				ArrayList beanList = (ArrayList)bean.get("dataList");
				for( int j = 0 ; j < beanList.size() ; j++){
					CommonData commonData = (CommonData)beanList.get(j);
					String name = commonData.getDataName();
					String value = commonData.getDataValue();
					if(Double.parseDouble(value) %1 > 0) {
                        decimalsflag = true;
                    }
					valueData[j].append(df1.format(Double.parseDouble(value))+",");
					if(i == 0) //只需要添加一次即可
                    {
                        legendDatas[j] = "'"+ name + "'";
                    }
				}
			}

			StringBuffer seriesDatas = new StringBuffer();
			seriesDatas.append("[");
			for( int i = 0 ; i < legendDatas.length ; i++){
				StringBuffer data = valueData[i];
				data.setLength(data.length()-1);
				seriesDatas.append("{\n")
						.append("name:"+legendDatas[i]+",\n")
						.append("type:'bar',\n")
						.append("data:["+data+"],\n")
						.append(itemStyleBuf.toString().replace("${COLORLIST}",itemColor(data.toString(),i)).replace("${labelFormatter}", labelFormatter(decimalsflag,true)))
						.append("},");
			}
			seriesDatas.setLength(seriesDatas.length()-1);
			seriesDatas.append("]");
			// 指定图表的配置项和数据
			buf.append("option = {\n")
					.append("title : {\n")
					.append("text: '"+this.title+"',\n")
					.append("left: 'center'\n")
					.append((this.title==null||this.title.length()==0? "":",top:'94%'\n"))//有title时重叠了
					.append("},\n")
					.append((this.title==null||this.title.length()==0? "":"height:'80%',"))//有title时重叠了
					.append("barMaxWidth:80,\n")
					.append("tooltip : {\n")
					.append("trigger:'axis',\n")
					.append("axisPointer : { type : ''}\n")// 坐标轴指示器，坐标轴触发有效
					.append("},\n")
					.append("toolbox:{show:true,right:'1%',feature:{saveAsImage:{type:'png',title:'下载',name:'"+(this.title.length()==0?"分组柱状图":this.title)+"',pixelRatio:2}}},\n");
			buf.append("calculable : true,\n");
			buf.append("legend: {data:"+Arrays.toString(legendDatas)+",");
			//【61976】为解决图例过多问题
			if("vertical".equalsIgnoreCase(orient)) {
				buf.append("type: 'scroll',orient: 'vertical',right: 4,top: 20,bottom:66},\n")
						.append("grid: {")
						.append("left: '1%',")
						.append("right: '12%',");
			}else {
				buf.append("left:'center',padding:[5,10]},\n")
						.append("grid: {")
						.append("left: '1%',")
						.append("right: '8%',");
			}
			if(this.width == -1){
				buf.append("top: '10%',");
			}
			buf.append("width:'auto',")
					.append("containLabel: true")
					.append("},");
			buf.append("xAxis: [{\n")
					.append("type: 'category',\n")
					.append("data: ["+(xAxisData.length()==0?"":xAxisData.substring(1))+"],\n")
					.append("axisLabel:{\n")
					.append("interval : 0,\n");
			if(datalist.size() < 5) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
            } else if(datalist.size() < 16) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "4"));
            } else{
				buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
				buf.append(",rotate :-45\n");
				buf.append(",margin:24\n");
			}
			buf.append("}\n");
			buf.append("}],\n")
					.append("yAxis: [{\n")
					.append("type: 'value'\n")
					.append("}],\n")
					.append("calculable : true,\n")
					.append("series:"+seriesDatas+"\n")
					.append(xDataScroll(datalist.size(),biDesk))
//				    .append((this.title.length()>0? ",height:titleHeight\n":""))
					.append("};\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}


	/**
	 * 折线图
	 * @param map
	 * @param biDesk
	 * @param total
	 * @return
	 */
	public String outEchartLineTransDataXml(HashMap map, String biDesk, String total) {

		StringBuffer xdata = new StringBuffer();
		StringBuffer[] ydata = new StringBuffer[map.size()];
		for(int i = 0 ; i < ydata.length ; i++){
			ydata[i] = new StringBuffer();
		}
		StringBuffer keyname = new StringBuffer();// 折线名称
		double sumValue = 0;//求和
		StringBuffer buf = null;
		ArrayList list = null;
		boolean decimalsflag =false;//整数位是否补上小数位标识
		try {
			LinkedHashMap linkedmap = new LinkedHashMap();
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				if("minmax".equals(key)) {
                    continue;
                }
				keyname.append(",'"+key+"'");
				list = (ArrayList) entry.getValue();
				for (int j = 0; j < list.size(); j++) {
					CommonData vo = (CommonData) list.get(j);

					String name = vo.getDataName();
					name = name==null?"":name;

					String value = vo.getDataValue();
					value = value==null?"":value;
					if(Double.parseDouble(value) %1 > 0) {
                        decimalsflag = true;
                    }
					if(linkedmap.containsKey(name)){
						ArrayList list1 = (ArrayList)linkedmap.get(name);
						CommonData data = new CommonData(value, key);
						list1.add(data);
					}else{
						ArrayList list2 = new ArrayList();
						CommonData data = new CommonData(value, key);
						list2.add(data);
						linkedmap.put(name, list2);
					}
				}
			}

			Iterator it1 = linkedmap.entrySet().iterator();//遍历map 柱状折线图数据
			while (it1.hasNext()) {
				Entry entry = (Entry) it1.next();
				String key = (String) entry.getKey();
				if("minmax".equals(key)) {
                    continue;
                }
				xdata.append(",'"+key+"'");
				list = (ArrayList) entry.getValue();
				for(int j = 0 ; j < list.size() ; j++){
					CommonData vo = (CommonData) list.get(j);
					String value = vo.getDataValue();
					ydata[j].append(","+df1.format(Double.parseDouble(value)));
				}
			}
			String sumStr = "";
			if(this.title.contains("平均")){//求平均值
				sumStr = "{type : 'average', name: '平均值'}";
			}
			String titleText = ("true".equals(isneedsum)?this.title+"("+df1.format(sumValue)+")":this.title);//柱状标题
			StringBuffer seriesData = new StringBuffer();
			seriesData.append("[");
			String[] keynames = keyname.length()>0? keyname.substring(1).split(","):null;
			StringBuffer seriesStr = new StringBuffer();
			for(int i = 0 ; i < ydata.length ; i++){
				seriesStr.append(",{\n");
				if(keynames != null && keynames.length >= i) {
                    seriesStr.append("name:"+keynames[i] +",\n");
                }
				seriesStr.append("type:'line',\n")
						.append("markLine : {\n")
						.append("silent:true,\n")
						.append("data:["+(sumStr.length()>0? sumStr:"")+"]\n")
						.append("},\n")
						.append("data:["+(ydata[i].length()>0? ydata[i].substring(1):"")+"],\n")
						.append("itemStyle:{\n")
						.append("normal:{\n")
						.append("lineStyle:{\n")
						.append("color:"+itemColor(null,i)+"\n")
						.append("},\n")
						.append("color:"+itemColor(null,i)+",\n")
						.append("label:{show:true,"+labelFormatter(decimalsflag,true)+"}")
						.append("}\n")
						.append("}\n")
//					.append(itemStyleBuf.toString().replace("${COLORLIST}",(ydata.length==1? itemColor(ydata[i].substring(1),-1):itemColor(ydata[i].substring(1),i))))
						.append("}\n");
			}
			seriesData.append(seriesStr.substring(1));
			seriesData.append("]");

			buf = new StringBuffer();
			// 指定图表的配置项和数据
			buf.append("option ={\n")
					.append("title : {\n")
					.append("text: '"+titleText+"',\n")
					.append("left: 'center'\n")
					.append("},\n")
					.append("toolbox:{show:true,right:'1%',feature:{saveAsImage:{type:'png',title:'下载',name:'"+(titleText.length()==0?"折线图":titleText)+"',pixelRatio:2}}},\n")
					.append("tooltip:{"+(ydata.length ==1? "show:true":"trigger: 'axis'")+"},\n")
					.append("legend:{\n")
					.append("data:["+(keyname.length()>0? keyname.substring(1):"")+"]\n")
					.append((titleText==null||titleText.length()==0? "":",top:'8%'"))//有title重叠了
					.append("},\n")
					.append("grid: {")
					.append("left: '1%',")
					.append("right: '10%',");
			if(this.width == -1){
				buf.append("top: '18%',");
			}
			//buf.append("bottom: '5%',")
			buf.append("width:'auto',")
					.append("containLabel: true")
					.append("},")
					//.append("legend: {data:["+linename.substring(1)+"],left:'center',padding:[5,10]},\n")
					.append("xAxis: {\n")
					.append("type: 'category',\n")
					.append("data: ["+(xdata.length() > 0 ? xdata.substring(1) : "")+"],\n")
					.append("axisLabel:{\n")
					.append("interval : 0,\n");

			if(linkedmap.size() < 5) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
            } else if(linkedmap.size() < 16) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "4"));
            } else{
				buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
				buf.append(",rotate :-45\n");
				buf.append(",margin:24\n");
			}
			buf.append("}\n")
					.append("},\n")
					.append("yAxis: {\n")
					.append("type: 'value'\n")
					.append("},\n")
					.append("series:"+seriesData+"\n")
					.append("};\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 报表管理 统计分析
	 * @param map
	 * @param biDesk
	 * @param total
	 * @return
	 */
	public String outEchartLineGroupsXml(HashMap map, String biDesk, String total){
		StringBuffer buf = new StringBuffer();
		StringBuffer seriesData =new StringBuffer();
		ArrayList xAxisData = new ArrayList();//统计项名称 集合
		ArrayList namelist = new ArrayList();//统计组 名称 集合
		ArrayList datalist = new ArrayList();//统计值 集合
		ArrayList list = null;
		LinkedHashMap linkedmap = new LinkedHashMap();
		Iterator it = map.entrySet().iterator();
		boolean decimalsflag =false;//整数位是否补上小数位标识
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			if("minmax".equals(key)) {
                continue;
            }
			xAxisData.add(key);
			list = (ArrayList) entry.getValue();
			ArrayList data = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				CommonData vo = (CommonData) list.get(i);
				String name = vo.getDataName();
				name = name==null?"":name;
				String value = vo.getDataValue();
				value = value==null?"":value;
				if(Double.parseDouble(value) %1 > 0) {
                    decimalsflag = true;
                }
				if(xAxisData.size()<2) {
                    namelist.add(name);
                }
				data.add(value);
			}
			datalist.add(data);
		}
		String sumStr="";//平均值
		seriesData.append("[");
		StringBuffer seriesStr = new StringBuffer();
		for(int i = 0 ; i < namelist.size() ; i++){
			//折线图 x轴坐标 与柱状图x轴坐标显示一致，数据格式也一致
			StringBuffer datastr = new StringBuffer();
			for(int j=0 ; j < datalist.size() ; j++ ){
				ArrayList data = (ArrayList)datalist.get(j);
				datastr.append((String)data.get(i)+",");
			}
			datastr.setLength(datastr.length()-1);

			seriesStr.append(",{\n");
			if(namelist != null && namelist.size() >= i) {
                seriesStr.append("name:'"+namelist.get(i) +"',\n");
            }
			seriesStr.append("type:'line',\n")
					.append("markLine : {\n")
					.append("silent:true,\n")
					.append("data:["+(sumStr.length()>0? sumStr:"")+"]\n")
					.append("},\n")
//				.append("data:["+datalist.get(i)+"],\n")
					.append("data:["+datastr+"],\n")
					.append("itemStyle:{\n")
					.append("normal:{\n")
					.append("lineStyle:{\n")
					.append("color:"+itemColor(null,i)+"\n")
					.append("},\n")
					.append("color:"+itemColor(null,i)+",\n")
					.append("label:{show:true,"+labelFormatter(decimalsflag,true)+"}")
					.append("}\n")
					.append("}\n")
//				.append(itemStyleBuf.toString().replace("${COLORLIST}",(ydata.length==1? itemColor(ydata[i].substring(1),-1):itemColor(ydata[i].substring(1),i))))
					.append("}\n");
		}
		seriesData.append(seriesStr.substring(1));
		seriesData.append("]\n");
		StringBuffer xAxisNameStr = new StringBuffer();
		for( int i = 0 ; i < xAxisData.size() ; i ++){
			xAxisNameStr.append(",'"+xAxisData.get(i)+"'");
		}
		StringBuffer nameStr = new StringBuffer();
		for(int i = 0 ; i < namelist.size() ; i++){
			nameStr.append(",'"+namelist.get(i)+"'");
		}
		// 指定图表的配置项和数据
		buf.append("option ={\n")
				.append("title : {\n")
//							.append("text: '"+titleText+"',\n")//折线图title
				.append("left: 'center'\n")
				.append("},\n")
				.append("toolbox:{show:true,top:30,right:'1%',feature:{saveAsImage:{type:'png',title:'下载',name:'"+(this.title.length()==0?"折线图":this.title)+"',pixelRatio:2}}},\n")
				.append("tooltip:{"+(datalist.size() ==1? "show:true":"trigger: 'axis'")+"},\n")
				.append("legend:{\n")
//							.append("top:100,")
				.append("type:'scroll',")
				.append("data:["+(namelist.size()>0? nameStr.substring(1):"")+"]\n")
				//.append((titleText==null||titleText.length()==0? "":",top:'8%'"))//有title重叠了
				.append("},\n")
				.append("grid: {")
				.append("left: '1%',")
				.append("right: '10%',");
		if(this.width == -1){
			buf.append("top: '14%',");
		}
		//buf.append("bottom: '5%',")
		buf.append("width:'auto',")
				.append("containLabel: true")
				.append("},")
				//.append("legend: {data:["+linename.substring(1)+"],left:'center',padding:[5,10]},\n")
				.append("xAxis: {\n")
				.append("type: 'category',\n")
				.append("data: ["+(xAxisNameStr.length() > 0 ? xAxisNameStr.substring(1) : "")+"],\n")
				.append("axisLabel:{\n")
				.append("interval : 0,\n");

		if(xAxisData.size() < 5) {
            buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
        } else if(xAxisData.size() < 16) {
            buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "4"));
        } else{
			buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
			buf.append(",rotate :-45\n");
			buf.append(",margin:24\n");
		}
		buf.append("}\n")
				.append("},\n")
				.append("yAxis: {\n")
				.append("type: 'value'\n")
				.append("},\n")
				.append("series:"+seriesData+"\n")
				.append(xDataScroll(xAxisData.size(),biDesk))
				.append("};\n");

		return buf.toString();
	}

	/**
	 * 折线图
	 * @param map
	 * @param biDesk
	 * @param total
	 * @return
	 */
	public String outEchartLineXml(HashMap map, String biDesk, String total) {

		String xdata = "";
		String ydata ="";
		double sumValue = 0;//求和
		StringBuffer buf = null;
		ArrayList list = null;
		int pointCount = 0;
		boolean decimalsflag =false;//整数位是否补上小数位标识
		try {
			Iterator it = map.entrySet().iterator();//遍历map 柱状折线图数据
			String linename = "";
			StringBuffer seriesData = new StringBuffer("[");
			boolean isFirst = true;
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				if("minmax".equals(key)) {
                    continue;
                }
				list = (ArrayList) entry.getValue();
				linename += ",'"+key+"'";
				StringBuffer childdata = new StringBuffer();
				childdata.append("{name:'"+key+"',type:'line', ");
			/*	if("true".equals(this.label_enabled)){//柱状图的顶部加数据显示
					childdata.append(itemStyleBuf);
				} */
				if(map.size() == 1){
					childdata.append("markLine : {")
							.append("silent:true,\n")
							.append("data : [")
							.append("{type : 'average', name: '平均值'}")
							.append("]")
							.append("},");
				}
				childdata.append("data:[");
				for (int j = 0; j < list.size(); j++) {
					CommonData vo = (CommonData) list.get(j);
					String name = vo.getDataName();
					name = name==null?"":name;
					if(isFirst){
						pointCount = list.size();
						xdata+=",\""+name+"\"";
					}
					String value = vo.getDataValue();
					value = value==null?"":value;
					if(Double.parseDouble(value) %1 > 0) {
                        decimalsflag = true;
                    }
					if(this.issupplydecimals) {
						decimalsflag = true;
					}
					ydata+=","+df1.format(Double.parseDouble(value));
					childdata.append(df1.format(Double.parseDouble(value))+",");
					sumValue = sumValue + Double.parseDouble(value);
				}
				isFirst = false;
				if(list.size() > 0) {
                    childdata.deleteCharAt(childdata.length()-1);
                }
				childdata.append("]");
				seriesData.append(childdata);
				seriesData.append(",itemStyle:{\n")
						.append("normal:{\n")
						.append("lineStyle:{color:"+itemColor(null,0)+"},\n")
						.append("color:"+itemColor(null,0)+",\n")
						.append("label:{show:true,"+labelFormatter(decimalsflag,true)+"}")
						.append("}\n")
						.append("}\n")
						.append("},");
			}
			if(seriesData.length() > 0) {
                seriesData.deleteCharAt(seriesData.length()-1);
            }
			seriesData.append("]");
			if(this.title.contains("平均")){//求平均值
				sumValue = (sumValue*1.0)/list.size();
			}
			String titleText = ("true".equals(isneedsum)?this.title+"("+(sumValue%1==0? (int)sumValue:df1.format(sumValue))+")":this.title);//柱状标题
			//if(titleText.startsWith("个数"))
			//titleText = "";
			String isShowTitle = !this.isneedtitle?"show:false,":"";
			buf = new StringBuffer();
			String decimals = ".";
			for(int i = 0 ; i < numDecimals ; i++){
				decimals += "0";
			}
			// 指定图表的配置项和数据
			buf.append("option ={\n")
					.append("title : {\n")
					.append("text: '"+titleText+"',\n")
					.append(isShowTitle)
					.append("left: 'center'\n")
					//.append(",subtext: '纯属虚构'")
					.append("},\n")
					.append("toolbox:{show:true,right:'1%',feature:{saveAsImage:{type:'png',title:'下载',name:'"+(titleText.length()==0?"折线图":titleText)+"',pixelRatio:2}}},");
			if("true".equals(islabelname)){
				buf.append("tooltip : {\n")//气泡提示框,鼠标悬浮交互时的信息提示,常用于展现更详细的数据
						.append("trigger: 'axis',\n")
						.append("formatter:function(params){")
						.append(" var tolTitle = '';\n")
						.append("  if(params[0].name.length >0){\n")
						.append("tolTitle = params[0].name+':';\n")
						.append("}\n")
						.append(" if(params[0].value > 0 ){\n")
						.append(" var paramvalue = params[0].value;\n")
						.append("if(paramvalue %1 ==0 && paramvalue.toString().indexOf('.')==-1){\n");
				buf.append("paramvalue= paramvalue "+(decimalsflag? "+'"+decimals+"'":"")+";\n");
				buf.append("}else{\n")
						.append("var length = paramvalue.toString().split('.')[1].length;\n")//获取小数位置
						.append("length = "+numDecimals+" -length;\n")//小数位数不够继续补充位数
						.append("var strNum = '';\n")
						.append("for(var i = 0 ; i < length ; i++){\n")
						.append("strNum +='0';\n")
						.append("}\n")
						.append("paramvalue= paramvalue+strNum;\n")
						.append("}\n")
						.append("tolTitle += paramvalue + '('+(Math.round((params[0].value/"+sumValue+") * 10000)/100)+'%)';\n")
						.append("}else{\n")
						.append(" tolTitle += '0(0.00%)';\n")
						.append("}\n")
						.append("return tolTitle;\n")
						.append("}")
						.append("},\n");
			}
			buf.append("grid: {")
					.append("left: '1%',")
					.append("right: '10%',");
			if(this.width == -1){
				buf.append("top: '14%',");
			}
			//buf.append("bottom: '5%',")
			buf.append("width:'auto',")
					.append("containLabel: true")
					.append("},")
					//.append("legend: {data:["+linename.substring(1)+"],left:'center',padding:[5,10]},\n")
					.append("xAxis: {\n")
					.append("type: 'category',\n")
					.append("data: ["+(xdata.length() > 0 ? xdata.substring(1) : "")+"],\n")
					.append("axisLabel:{\n")
					.append("interval : 0,\n");
			if(list.size() < 5) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
            } else if(list.size() < 16) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "4"));
            } else{
				buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
				buf.append(",rotate :-45\n");
				buf.append(",margin:24\n");
			}

//			            	if(pointCount > 8 && pointCount <= 20){
//				        	if(pointCount >3){// bug 38574  统计项个数大于3个不倾斜显示，每行显示2个字  wangb 20180702 
//			            		buf.append("formatter :"+formatLabel);//格式化刻度标签
//			            	}else{
//			            		buf.append("rotate:"+xangle+"\n");
//			            	}
			buf.append("}\n")
					.append("},\n")
					.append("yAxis: {\n")
					.append("type: 'value'\n")
					.append("},\n")
					.append("series:"+seriesData+"\n")
					.append(xDataScroll(list.size(), biDesk))
					.append("};\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 饼状图
	 * @param list
	 * @param biDesk
	 * @param total
	 * @return
	 */
	public String outEchartPieXml(ArrayList list, String biDesk,String total ) {
		String xdata = "";
		String series ="";
		double sumValue = 0;//求和
		StringBuffer buf = null;
		StringBuffer colorData = new StringBuffer();
		try {
			//初始化数据
			for (int i = 0; i < list.size(); i++) {
				CommonData vo  = (CommonData)list.get(i);
				String name = vo.getDataName();
				String value = vo.getDataValue();
				colorData.append(value+",");
				value = df1.format(Double.parseDouble(value));
				xdata+=",\""+name+"\"";
				series+=",{value:"+value+", name:'"+name+"'}";
				sumValue = sumValue + Double.parseDouble(value);
			}
			colorData.setLength(colorData.length()-1);
			if(this.title.contains("平均")){
				sumValue = (sumValue*1.0)/list.size();
			}
			String titleText = ("true".equals(isneedsum)?this.title+"("+df1.format(sumValue)+")":this.title);
//			if(titleText.startsWith("个数"))
//				titleText = "''";
//			else
			titleText =  "'"+titleText+"'";
			if(this.width == -1){//饼状图 title 规则 不清楚 先不显示titile
//				titleText = "'" + (chartpnl.length()==0?"":"饼图"+(Integer.parseInt(chartpnl.substring(chartpnl.length()-1))+1)+"")+"',show:false";
				titleText = "'',show:false";
			}
			buf = new StringBuffer();
			// 指定图表的配置项和数据
			buf.append("option ={\n")
					.append("title : {\n")
					.append("text: "+titleText+",\n")
					.append("subtext: '',show:true,")
					.append("x:'center'\n")
					.append("},\n")
					.append("toolbox:{show:true,right:'1%',feature:{saveAsImage:{type:'png',title:'下载',name:"+(titleText.length()==0?"平面饼状图":titleText.replace("show:false","show:true"))+",pixelRatio:2}}},\n")
					.append("color:"+itemColor(colorData.toString(),-1)+",")//饼状图修改配色
					.append("tooltip : {\n")
					.append("trigger: 'item',\n")
//				        .append("formatter: '{a} <br/>{b} : {c} "+(showpercent?"({d}%)":"")+"'\n")
					//bug 38533 平面饼状态图 悬浮 提示信息 内容一行 显示2行高度 不对    wangb 20180704
					.append("formatter:function(params){\n")
					.append(" var tolTitle = '';\n")
					.append("  if(params.name.length >0){\n")
					.append("tolTitle = params.name+':';\n")
					.append("}\n")
					.append(" if(params.value > 0 ){\n")
					.append("tolTitle += params.value + '('+params.percent+'%)';\n")
					.append("}else{\n")
					.append(" tolTitle = '0(0.00%)';\n")
					.append("}\n")
					.append("return tolTitle;\n")
//				        	.append("if(params.seriesName){\n")
//				        	.append("  return "+ (this.width == -1?"":"params.seriesName+'<br/>'+")+"params.name+':'+params.value"+(showpercent?"+'<br/>百分比:'+params.percent+'%'":"")+";\n")
//				        	.append("}else{\n")
//				        	.append("  return params.name+':'+params.value"+(showpercent?"+'<br/>百分比:'+params.percent+'%'":"")+";\n")
//				        	.append("}\n")
					.append("}\n")
					.append("},\n");
			if(width>900){
				buf.append("legend: {\n")
						.append("orient: 'vertical',\n")
						.append("right: '2%',\n")//图例放在右边 可以避免与饼状图标签重叠
						.append("top: 'center',\n")
						//.append("type:'scroll',")
						.append("orient:'vertical',")
						.append("data: ["+xdata.substring(1)+"]\n")
						.append("},\n");
			}
			buf.append("grid: {")
					.append("left: '1%',")
					.append("right: '1%',")
					//.append("bottom: '5%',")
					.append("width:'auto',")
					.append("containLabel: true")
					.append("},");
			buf.append("series : [\n")
					.append("{\n")
					.append("name: '" + (chartpnl.length()==0?"":"饼图"+(Integer.parseInt(chartpnl.substring(chartpnl.length()-1))+1)+"")+"',\n")
					.append("type: 'pie',\n")
					.append("radius : '55%',\n")
					.append("center: ['"+(width>900?"40%":"55%")+"', '"+((width == 600 || width == 1)?"60%":"45%")+"'],\n")//距离左边和上边的距离
					.append("startAngle:0,\n")//饼图起始角度为0，
					.append("data:[\n")
					.append(series.length() > 0 ? series.substring(1) : "")
					.append("]\n");
			if("true".equals(islabelname)){//是否显示标签
				buf.append(",itemStyle: {\n");
				buf.append("normal: {")
						.append("label: {")
						.append("show: true,")
						//.append("formatter: '{b} : {c}人 ({d}%)'")   //在饼状图上直接显示百分比
//									                  .append("formatter: '{b}:({d}%)'")
						.append("formatter: '{b}\\n({d}%)'")   //bug 37280 第一行显示统计条件名称 第二行显示百分比  wangb 20180705
						.append("},")
						.append("labelLine: { show: true }")
						.append("},");
				buf.append("emphasis: {\n")
						.append("shadowBlur: 10,\n")
						.append("shadowOffsetX: 0,\n")
						.append("shadowColor: 'rgba(0, 0, 0, 0.5)'\n")
						.append("}\n")
						.append("}\n");
			}
			buf.append("}\n")
					.append("]\n")
					.append("};\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	/**
	 * 添加图表监听事件
	 * @param pointClick
	 * @return
	 * 由于点击雷达图节点获取不到当前节点的信息，现在将雷达图的监听事件去除 chart_type == 55 ：雷达图
	 */
	private String addEventManager(String pointClick) {
		String eventStr = ((chart_type == 55 || chart_type == 33) ?"" : "myChart"+chartpnl+".on('click', function (params) {"+pointClick+"(params);});\n");
//				 + "setTimeout(function(){window.onresize=function(){myChart"+chartpnl+".resize();}},200);";
		//		  + "if(getBrowseVersion())\n"
//		  		+ " window.attachEvent(\"resize\", function () {myChart"+chartpnl+".resize();});  \n"
//		  + "else\n"
//		  		+ " window.addEventListener(\"resize\", function () {myChart"+chartpnl+".resize();});\n";
		return eventStr;
	}

	/**
	 * 引入echart文件
	 * @return
	 */
	private StringBuffer createHeaderData() {
		String id = "".equals(this.chartpnl)?"main":this.chartpnl;
		StringBuffer buf = new StringBuffer();
		/*<!-- 引入 echarts.js -->*/
		buf.append("<script id='echartId' src=\"/echarts/echarts.min.js\" charset='utf-8'></script>");
		buf.append("<script id='shine' src=\"/echarts/shine.js\" charset='utf-8'></script>");

		/*<!-- 为ECharts准备一个具备大小（宽高）的Dom -->*/
		buf.append("<div id=\"main"+id+"\" style=\"text-align:left;\"></div>");
		buf.append(" <script type=\"text/javascript\">");
		buf.append("var chartDivId = 'main"+id+"';");
		buf.append("var chartDiv = document.getElementById('main"+id+"');\n");
		if(this.width == -1)
//			buf.append("document.getElementById('main"+id+"').style.width='800';");
        {
            buf.append("chartDiv.style.width='100%';");
        } else if(this.width == 1200) {
            buf.append("chartDiv.style.width=document.body.clientWidth-60+'px';");
        } else
//			buf.append("chartDivId.style.width='100%';");
        {
            buf.append("chartDiv.style.width="+this.width+"+'px';");
        }

		if(this.height > 0) {
            buf.append("chartDiv.style.height="+this.height+"+'px';");
        } else if(width == 1200) {
            buf.append("if(document.body.clientHeight > 268){chartDiv.style.height=document.body.clientHeight-70+'px';}else{chartDiv.style.height=268+'px';}");//当body高度大于268 显示body高度 否则显示 268px 高度  wangb 20180718
        } else //if(this.height == -1)
        {
            buf.append("chartDiv.style.height='100%';");
        }

		buf.append("var chartHeight = parseInt(chartDiv.offsetHeight)? parseInt(chartDiv.offsetHeight)-120+'px':parseInt(chartDiv.style.height)-120 +'px';");
//		buf.append("var titleHeight = parseInt(chartDiv.style.height)-100+'px';");//统计图 往上移100px 防止 title与统计项名称重叠 wangb 20180717
		// 基于准备好的dom，初始化echarts实例
		buf.append("var myChart"+chartpnl+" = echarts.init(document.getElementById('main"+id+"'),'shine');");
		return buf;
	}


	/**
	 * @param list
	 * @param biDesk
	 *            是否为总裁桌面发来的请求
	 * @param total
	 *            x-轴总字数
	 * @return
	 *
	 *   window.addEventListener("resize",function(){myChart.resize(); });
	 */
	public String outEchartBarXml(ArrayList list, String biDesk, String total){
		String xdata = "";
		String ydata ="";
		double sumValue = 0;//求和
		StringBuffer buf = null;
		boolean decimalsflag =false;//整数位是否补上小数位标识
		try {
			//初始化数据
			for (int i = 0; i < list.size(); i++) {
				Object o = list.get(i);
				if(o instanceof CommonData){
					CommonData vo  = (CommonData)o;
					String name = vo.getDataName();
					String value = vo.getDataValue();
					xdata+=",\""+name+"\"";
					if(Double.parseDouble(value) %1 > 0) {
                        decimalsflag = true;
                    }
					//ydata+=",\""+df1.format(Double.parseDouble(value))+"\"";
					ydata+=",{value:\""+df1.format(Double.parseDouble(value))+"\"";
					if(vo.containsKey("codeitemid")){
						String key = (String)vo.get("codeitemid");
						ydata+=",id:\""+key+"\"";
					}
					ydata+="}";
					sumValue = sumValue + Double.parseDouble(value);
				}else if(o instanceof LazyDynaBean){
					LazyDynaBean lyBean = (LazyDynaBean)o;
					String name = (String) lyBean.get("dataName");
					String value = (String) lyBean.get("dataValue");
					xdata+=",\""+name+"\"";
					if(Double.parseDouble(value) %1 > 0) {
                        decimalsflag = true;
                    }
					ydata+=",\""+df1.format(Double.parseDouble(value))+"\"";
					sumValue = sumValue + Double.parseDouble(value);
				}

			}
//			if(this.title.contains("平均")){
//				sumValue = (sumValue*1.0)/list.size();
//			}
			if(this.title.contains("平均")){//求平均值
				sumValue = (sumValue*1.0)/list.size();
			}
			String titleText = ("true".equals(isneedsum)?this.title+"("+(sumValue%1==0? (int)sumValue:df1.format(sumValue))+")":this.title);//柱状标题
//			if(this.title.startsWith("个数"))
//				titleText = "";
			String isShowTitle = !this.isneedtitle?"show:false,":"";
			buf = new StringBuffer();
			// 指定图表的配置项和数据
			buf.append("option = {\n")
					.append("title : {\n")
					.append("text: '"+titleText+"',\n")
					.append(isShowTitle)
					.append("left: 'center'\n")
//			.append("top:'94%'")
					//.append(",subtext: '纯属虚构'")
					.append("},\n")
					.append("toolbox:{show:true,right:'1%',feature:{saveAsImage:{show:true,type:'png',title:'下载',name:'"+(titleText.length()==0?"柱状图":titleText)+"',pixelRatio:2}}},\n")
//			.append("height:'80%',")
					.append("barMaxWidth:80,\n");
			if("true".equals(islabelname)){
				buf.append("tooltip : {\n")//气泡提示框,鼠标悬浮交互时的信息提示,常用于展现更详细的数据
						.append("trigger: 'axis',\n")
						.append("formatter:function(params){")
						.append(" var tolTitle = '';\n")
						.append("  if(params[0].name.length >0){\n")
						.append("tolTitle = params[0].name+':';\n")
						.append("}\n")
						.append(" if(params[0].value > 0 ){\n")
						.append("tolTitle += params[0].value + '('+(Math.round((params[0].value/"+sumValue+") * 10000)/100)+'%)';\n")
						.append("}else{\n")
						.append(" tolTitle += '0(0.00%)';\n")
						.append("}\n")
						.append("return tolTitle;\n")
//					.append("return params[0].name+':'+params[0].value"+(showpercent?"+'('+(Math.round((params[0].value/"+sumValue+") * 10000)/100)+'%)'":"")+";")//保留2位小数
						.append("},")
						.append("axisPointer : { type : ''}\n")// 坐标轴指示器，坐标轴触发有效
						.append(",confine:true,padding: [10,10]")
						.append("},\n");
			}
			buf.append("legend: {\n")//图例,表述数据和图形的关联,每个图表最多仅有一个图例,混搭图表共享
					.append("data:["+xdata.substring(1)+"],\n")
					.append("orient: 'vertical',\n")
					.append("left: 'left'\n")
					.append("},\n")

					/*	.append("toolbox: {\n")
                        .append("show : true,\n")
                        .append("feature : {\n")
                        .append("mark : {show: true},\n")*/
					// .append("dataZoom: {show: true,type:'slider',end:70},") //数据缩放视图
					/*//.append("dataView : {show: true, readOnly: true},\n")
                    //.append("magicType : {show: true, type: ['line', 'bar']},\n")//type: ['line', 'bar', 'stack', 'tiled']
                    .append("magicType : {show: true, type: ['line', 'bar']},")
                    .append("restore : {show: true},\n")
                    .append("saveAsImage : {show: true}\n")
                    *//*****************自定义按钮myTool2：设置统计范围********************//*
		    .append(",myTool2:{")//自定义按钮 danielinbiti,这里增加，selfbuttons可以随便取名字    
                .append("show:true,")//是否显示    
                .append("title:'设置统计范围',") //鼠标移动上去显示的文字    
                .append("icon:'path://M525.4 721.2H330.9c-9 0-18.5-7.7-18.5-18.1V311c0-9 9.3-18.1 18.5-18.1h336.6c9.3 0 18.5 9.1 18.5 18.1v232.7c0 6 8.8 12.1 15 12.1 6.2 0 15-6 15-12.1V311c0-25.6-25.3-48.9-50.1-48.9h-335c-26.2 0-50.1 23.3-50.1 48.9v389.1c0 36.3 20 51.5 50.1 51.5h197.6c6.2 0 9.3-7.5 9.3-15.1 0-6-6.2-15.3-12.4-15.3zM378.8 580.6c-6.2 0-12.3 8.6-12.3 14.6s6.2 14.6 12.3 14.6h141.4c6.2 0 12.3-5.8 12.3-13.4 0.3-9.5-6.2-15.9-12.3-15.9H378.8z m251.6-91.2c0-6-6.2-14.6-12.3-14.6H375.7c-6.2 0-12.4 8.6-12.4 14.6s6.2 14.6 12.4 14.6h240.8c6.2 0.1 13.9-8.5 13.9-14.6z m-9.2-120.5H378.8c-6.2 0-12.3 8.6-12.3 14.6s6.2 14.6 12.3 14.6h240.8c7.7 0 13.9-8.6 13.9-14.6s-6.2-14.6-12.3-14.6z m119.4 376.6L709 714.1c9.2-12 14.6-27 14.6-43.2 0-39.4-32.1-71.4-71.8-71.4-39.7 0-71.8 32-71.8 71.4s32.1 71.4 71.8 71.4c16.3 0 31.3-5.4 43.4-14.5l31.6 31.5c3.8 3.8 10 3.8 13.8 0 3.8-3.8 3.8-10 0-13.8z m-88.8-23.6c-28.3 0-51.3-22.8-51.3-51s23-51 51.3-51c28.3 0 51.3 22.8 51.3 51s-23 51-51.3 51z',") //图标    
                .append("onclick:function() {")//点击事件,这里的option1是chart的option信息    
                        .append("javascript:statset();")
                      .append("}")    
                .append("}")
			*//*************************************//*
			 *//*****************自定义按钮myTool3：切换到饼状图********************//*
		    .append(",myTool3:{")//自定义按钮 danielinbiti,这里增加，selfbuttons可以随便取名字    
                .append("show:true,")//是否显示    
                .append("title:'饼图',") //鼠标移动上去显示的文字    
                .append("icon:'path://M525.4 721.2H330.9c-9 0-18.5-7.7-18.5-18.1V311c0-9 9.3-18.1 18.5-18.1h336.6c9.3 0 18.5 9.1 18.5 18.1v232.7c0 6 8.8 12.1 15 12.1 6.2 0 15-6 15-12.1V311c0-25.6-25.3-48.9-50.1-48.9h-335c-26.2 0-50.1 23.3-50.1 48.9v389.1c0 36.3 20 51.5 50.1 51.5h197.6c6.2 0 9.3-7.5 9.3-15.1 0-6-6.2-15.3-12.4-15.3zM378.8 580.6c-6.2 0-12.3 8.6-12.3 14.6s6.2 14.6 12.3 14.6h141.4c6.2 0 12.3-5.8 12.3-13.4 0.3-9.5-6.2-15.9-12.3-15.9H378.8z m251.6-91.2c0-6-6.2-14.6-12.3-14.6H375.7c-6.2 0-12.4 8.6-12.4 14.6s6.2 14.6 12.4 14.6h240.8c6.2 0.1 13.9-8.5 13.9-14.6z m-9.2-120.5H378.8c-6.2 0-12.3 8.6-12.3 14.6s6.2 14.6 12.3 14.6h240.8c7.7 0 13.9-8.6 13.9-14.6s-6.2-14.6-12.3-14.6z m119.4 376.6L709 714.1c9.2-12 14.6-27 14.6-43.2 0-39.4-32.1-71.4-71.8-71.4-39.7 0-71.8 32-71.8 71.4s32.1 71.4 71.8 71.4c16.3 0 31.3-5.4 43.4-14.5l31.6 31.5c3.8 3.8 10 3.8 13.8 0 3.8-3.8 3.8-10 0-13.8z m-88.8-23.6c-28.3 0-51.3-22.8-51.3-51s23-51 51.3-51c28.3 0 51.3 22.8 51.3 51s-23 51-51.3 51z',") //图标    
                .append("onclick:function() {")//点击事件,这里的option1是chart的option信息    
                        //.append("myChart.setOption("+outEchartPieXml(list, biDesk, total)+");return;</script>")
                      .append("}")    
                .append("}")
			*//*************************************//*
			.append("}\n")
			.append("},\n")*/

					.append("calculable : true,\n")
					.append("grid: {")
					.append("left: '1%',")
					.append("right: '8%',");
			//.append("bottom: '5%',");
			if(this.width == -1){
				buf.append("top: '10%',");
			}
			buf.append("width:'auto',")
					.append("containLabel: true")
					.append("},")
					.append("xAxis : [\n")//直角坐标系中的横轴，通常并默认为类目型;数组中每项代表一条横轴坐标轴,规定最多同时存在2条横轴
					.append("{\n")
					.append("type : 'category',\n")
					.append("data : ["+xdata.substring(1)+"],\n")
					.append("axisTick: {alignWithLabel: false},\n")
					.append("axisLabel:{\n")
					.append("interval : 0,\n");
			if(list.size() < 5) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
            } else if(list.size() < 16) {
                buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "4"));
            } else{
				buf.append("formatter :"+formatLabel.toString().replace("${MAXLENGTH}", "10"));
				buf.append(",rotate :-45\n");
				buf.append(",margin:24\n");
			}

//			if(list.size() > 8 && list.size() <= 20){
			//if(list.size() > 3){// bug 38574  统计项个数大于3个不倾斜显示，每行显示2个字  wangb 20180702
//				buf.append("formatter :"+formatLabel);
			//}else{
//				 buf.append(",rotate:"+xangle+"\n");
//		    }
			buf.append(",showMinLabel:true\n")
					.append("}\n")
					.append("}\n")
					.append("],\n")
					.append("yAxis : [\n")//直角坐标系中的纵轴，通常并默认为类目型;数组中每项代表一条纵轴坐标轴,规定最多同时存在2条纵轴
					.append("{\n")
					.append("type : 'value'\n")
					.append("}\n")
					.append("],\n")
					.append("series : [\n")//数据系列,一个图表可能包含多个系列,每个系列可能包含多个数据;驱动图表生成数据内容,数组中每项代表一个系列的特殊选项及数据
					.append("{\n")
					//.append("name:'蒸发量',")
					.append("type:'bar',\n")
					//.append("barMaxWidth:150,\n")
					.append("data:["+ydata.substring(1)+"],\n");
			buf.append(itemStyleBuf.toString().replace("${COLORLIST}",itemColor(ydata.substring(1),-1)).replace("${labelFormatter}", labelFormatter(decimalsflag,true)));
/*			if(!"true".equals(this.label_enabled)){//柱状图的顶部加数据显示
				buf.append("markPoint : {\n")
				.append("data : [\n")
				.append("{type : 'max', name: '最大值'},\n")
				.append("{type : 'min', name: '最小值'}\n")
				.append("]\n")
				.append("},\n");
			}*/
			//buf.append(averMarkLine)
			buf.append((this.title.contains("平均")? ",markLine:{data:[{type:'average'}],itemStyle:{normal:{color:"+itemColor(null,0)+"}}}\n":""));//平均值 线颜色修改
			buf.append("}\n")
					.append("]\n")
					.append(xDataScroll(list.size(),biDesk))
					.append("};\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * x轴统计项太多时，添加滚动条
	 * @param data
	 * @return
	 */
	private String xDataScroll(int length,String biDesk){
		StringBuffer str = new StringBuffer();
//		String[] datas= data.split(",");
//		int length = datas.length;
		boolean flag = false;
		int index=0;
		if("true".equalsIgnoreCase(biDesk)){//领导桌面进入
			if(length <=6) {
                return "";
            }
			index = (int)((6.0/length)*100);
			str.append(",dataZoom:{\n")
					.append("realtime:true,\n")
					.append("height:20,\n")
					.append("textStyle:false,")
					.append("start:0,\n")
					.append("end:"+index+"\n")
					.append("}");
			String id = "".equals(this.chartpnl)?"main":this.chartpnl;
			if(this.height>0 && this.width>0 && this.width <=800)//ext 弹窗显示统计图
            {
                str.append(",\nheight:"+this.height+"\n");
            } else {
                str.append(",\nheight:'100%'-60\n");
            }
			return str.toString();
		}else{
			//不同宽度下显示显示个数
			if(this.width >0 && this.width <=400 && length > 4) {
                index = (int)((4.0/length)*100);
            } else if(this.width > 400 && this.width<=600 && length > 8) {
                index = (int)((8.0/length)*100);
            } else if(this.width > 600 && this.width<=800 && length > 10) {
                index = (int)((10.0/length)*100);
            } else if(length >= 16){
				index = (int)((15.0/length)*100);
			}else{
				flag=true;
			}
			if(flag)//统计项小于16时，不添加滚动条
            {
                return "";
            }
		}

		str.append(",dataZoom:{\n")
				.append("realtime:true,\n")
				.append("height:20,\n")
				.append("textStyle:false,")
				.append("start:0,\n")
				.append("end:"+index+"\n")
				.append("}");
		String id = "".equals(this.chartpnl)?"main":this.chartpnl;
		if(this.height>0 && this.width>0 && this.width <=800){//ext 弹窗显示统计图
			if(length >15) {
                str.append(",\nheight:"+(this.height-140)+"\n");
            } else {
                str.append(",\nheight:"+this.height+"\n");
            }
		}else{
			str.append(",\nheight:chartHeight\n");
		}
		return str.toString();
	}

	private String yDataScroll(int length){
		StringBuffer str = new StringBuffer();
		boolean flag = false;
		int index =0;
		if(this.height>0 && this.height<=400 && length>6) {
            index = 100-(int) ((6.0/length)*100);
        } else {
            flag=true;
        }
		if(flag) {
            return "";
        }
		str.append(",dataZoom:{\n")
				.append("realtime:true,\n")
				.append("yAxisIndex:0,\n")
				.append("textStyle:false,")
				.append("start:100,\n")
				.append("end:"+index+"\n")
				.append("}");
		return str.toString();
	}
	/**
	 * 统计图统计项数值小数位处理
	 * @param flag 是否补上小数位
	 * @param showZeroFlag 是否显示整数0  true 显示 false 不显示
	 * @return
	 */
	private StringBuffer labelFormatter(boolean flag,boolean showZeroFlag){
		StringBuffer str = new StringBuffer();
		String decimals = ".";
		for(int i = 0 ; i < numDecimals ; i++){
			decimals += "0";
		}
		str.append("formatter:function(param){\n")
				.append("if(param.value %1 ==0 && param.value.toString().indexOf('.')==-1){\n");
		if(showZeroFlag) {
            str.append("return param.value "+(flag? "+'"+decimals+"'":"")+";\n");
        } else {
            str.append("return param.value==0? \"\":param.value"+(flag? "+'"+decimals+"'":"")+";\n");
        }

		str.append("}else{\n")
				.append("var length = param.value.toString().split('.')[1].length;\n")//获取小数位置
				.append("length = "+numDecimals+" -length;\n")//小数位数不够继续补充位数
				.append("var strNum = '';\n")
				.append("for(var i = 0 ; i < length ; i++){\n")
				.append("strNum +='0';\n")
				.append("}\n")
				.append("return param.value+strNum;\n")
				.append("}\n")
				.append("}\n");
		return str;
	}


	/**
	 * 统计图统计项动态分配色域  wangb 20180706
	 * @param data 统计项数据， 
	 * @param index 分组统计用到此参数
	 * @return
	 */
	String[] colors = new String[]{"'#338DC9'","'#EE7541'","'#2BD62B'","'#DBDC26'","'#8FbC8B'","'#D2B48C'","'#DC648A'","'#21B2AA'","'#B0C4DE'","'#DDA0DD'","'#9C9AFF'","'#9C3164'","'#FFB248'","'#1fcf03'","'#005eaa'","'#339ca8'","'#d9b014'","'#32a487'","'#333333'","'#FFB6C1'","'#FF69B4'","'#D8BFD8'","'#DDA0DD'","'#FF00FF'"};
	private String itemColor(String data,int index){

		StringBuffer str = new StringBuffer();
		if(data == null){//折线图， data值为空
			str.append(Arrays.toString(colors));
			if(index > -1)//获取对应色值
            {
                return colors[index%colors.length];
            }
			return str.toString();
		}
		String[] datas = data.split(",");
		str.append("[");
		if(index == -1){//单项统计配色
			for(int i = 0 ; i < datas.length ; i++){
				str.append(colors[i%colors.length]+",");
			}
		}else{//分组配色
			for(int i = 0 ; i < datas.length ; i++){
				str.append(colors[index%colors.length]+",");
			}
		}
		str.setLength(str.length()-1);
		str.append("]");
		return str.toString();
	}


	public static String computeXangle(ArrayList list) {
		int size = list.size();
		int angle = 45;
		/**
		 * 统计项名称显示规则：
		 * 小于5个，每行10个汉字，超过换行；
		 * 5-15，每行四个汉字换行，
		 * 大于等于16倾斜45，每行10汉字，超过换行。
		 */
		/*
		if (size <= 3) 
			return 0+"";
		else if(size>3&&size<11)
			angle = 45;
		else if(size>10&&size<16)
			angle = 75;
		else
			angle = 90;//最大不能设置成90  会显示异常的
		*/
		if(size <= 15) {
            return "" + 0;
        }
		return "" + 45;
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

	public boolean isIsneedtitle() {
		return isneedtitle;
	}

	public void setIsneedtitle(boolean isneedtitle) {
		this.isneedtitle = isneedtitle;
	}

	public boolean isIssupplydecimals() {
		return issupplydecimals;
	}

	public void setIssupplydecimals(boolean issupplydecimals) {
		this.issupplydecimals = issupplydecimals;
	}


}
