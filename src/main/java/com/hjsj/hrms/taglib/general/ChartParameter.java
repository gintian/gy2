/**
 * 
 */
package com.hjsj.hrms.taglib.general;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * <p>Title:ChartParameter</p>
 * <p>Description:jfreechart参数设置信息类</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 18, 2006:10:03:32 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ChartParameter implements Serializable {
	
	//chart参数设置
	private String chartTitle;  //图形标题
	private String chartTitleAlign; //标题排列(左/居中/右)
	
	//图形的绘制结构对象
	private String noDataMessage; //无数据显示信息
	private HashMap markerMap;    //控制线(key 控制描述信息 value 控制值double类型)
	private String markers;       //控制值说明标识
	
	//图形的绘制单元参数设置
	private boolean isItemLabelsVisible; //是否显示数值
	private double itemMargin; //每个组所包含的平行柱或数据点的之间距离
	private float  strokeWidth; //折线宽度
	
	//分类轴参数设置
	private String categoryLabelType;//分类轴标签显示样式(标准/垂直/偏移)
	private float categoryLabelMaxWidth ; //分类轴标签最大宽度(描述信息)
	private double categoryMargin; //组与组之间的距离
	private double itemLeftMargin;//第一个Item与数据轴(左)的距离
	private double itemRightMargin;//最后一个Item与边界(右)的距离
	private int    lineNodeIsMarked;  //线状图是否显示纵轴虚线
	
	//数据轴参数设置(柱状图与线状图适用)
	private boolean isAutoRangeValue; //是否自动设置区间值(既:系统默认起始/终止值)
	private double numberAxisStartValue; //数据轴起始值 
	private double numberAxisEndValue;   //数据轴终止值	
	private double numberAxisIncrement;  //数据轴数据增量
	
	private double itemLowerMargin; //最低的一个Item与图片底端的距离
	private double itemUpperMargin; //最高的一个Item与图片顶端的距离
	
	
	private int width=0;   //图形宽度
	private int height=0;  //图形高度
	
	
	/**
	 * 构造器
	 */
	public ChartParameter() {
		init();	
	}
	
	/**
	 * 系统默认参数设置
	 */
	public void init(){
		this.strokeWidth=1f;
		this.chartTitleAlign="center";
		this.noDataMessage="";
		this.markerMap = new HashMap();
		
		this.isItemLabelsVisible=true;	
		this.itemMargin=0.3D;
		
		this.categoryLabelType="UP_45";
		this.categoryLabelMaxWidth=30f;
		this.categoryMargin=5D;
		
		this.itemLeftMargin=0D;
		this.itemRightMargin=0D;
		
		this.isAutoRangeValue =true;
		this.numberAxisStartValue=0D;
		this.numberAxisEndValue=0D;
		this.numberAxisIncrement=0D;

		this.itemLowerMargin=0.1D;
		this.itemUpperMargin=0.1D;
		this.lineNodeIsMarked=0;
		
	}

	/**
	 * 分析配置信息字符串，构造图例参数对象
	 * @param chartParameterStr 参数设置字符串
	 */
	public void analyseChartParameter(String chartParameterStr){
		//init();
		//chartParameter = "男女的平均年龄分析`1`1,0,100,`10`10`5`10`10`标准值,控制值,`";
		if(chartParameterStr == null || "".equals(chartParameterStr)){
			
		}else{
			try {
				chartParameterStr = new String(chartParameterStr.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			String [] cps = chartParameterStr.split("`");

			//标题文本设置
			if(cps[0]==null || "".equals(cps[0])){
				this.setChartTitle("no");
			}else{
				this.setChartTitle(cps[0]);
			}
			
			/*System.out.println("标题="+this.getChartTitle());*/
			
			//标题对齐设置
			String titleAlign = cps[1];
			if("0".equals(titleAlign)){//左对齐
				this.setChartTitleAlign("LEFT");
			}else if("1".equals(titleAlign)){//居中
				this.setChartTitleAlign("CENTER");
			}else if("2".equals(titleAlign)){//右对齐
				this.setChartTitleAlign("RIGHT");
			}
			
			/*System.out.println("对齐方式="+this.getChartTitleAlign());*/
			
			//自动设置或指定区间
			String auto = cps[2];
			if(auto.charAt(0)=='0'){//自动设置
				this.setAutoRangeValue(true);
			}else if(auto.charAt(0)=='1'){//指定区间
				
				//System.out.println("auto=" + auto);
				
				String [] temp = auto.split(",");
				this.setAutoRangeValue(false);
				this.setNumberAxisStartValue(Double.parseDouble(temp[1]));
				this.setNumberAxisEndValue(Double.parseDouble(temp[2]));
				
			}
			//坐标起始位置(图形与分类轴距离)
			this.setItemLowerMargin(Double.parseDouble(cps[3])/100);
			//坐标终止位置(图形与顶端距离)
			this.setItemUpperMargin((100-Double.parseDouble(cps[4]))/100);

			//数据轴增量
			String str = cps[5];
			if(str==null || "".equals(str)){
				this.setNumberAxisIncrement(0.0D);
			}else{
				this.setNumberAxisIncrement(Double.parseDouble(str));
			}

			//分类轴起始位置
			this.setItemLeftMargin(Double.parseDouble(cps[6])/100);
			//分类轴终止位置
			this.setItemRightMargin((100-Double.parseDouble(cps[7]))/100);

			//控制值集合设置
			String markers = cps[8];
			if("null".equals(markers)){
				this.setMarkers("");
			}else{
				this.setMarkers(markers);
			}
			
			//图形大小
			if(cps.length>=10&&cps[9].trim().length()>0)
			{
				String[] temps=cps[9].split(",");
				this.width=Integer.parseInt(temps[1]);
				this.height=Integer.parseInt(temps[0]);
			}
		}
		
		
	}

	
	/**
	 * 分析用户图例参数设置对象生成配置信息字符串
	 * @param chartParamet 参数设置对象
	 * @return
	 */
	public static String analyseChartParameter(ChartParameter chartParameter){	
		String chartParameterStr = "";
		
		String algin = chartParameter.getChartTitleAlign(); //标题对齐
		
		//System.out.println("algin=" + algin);
		
		if("left".equalsIgnoreCase(algin)){
			chartParameterStr+="0";
		}else if("center".equalsIgnoreCase(algin)){
			chartParameterStr+="1";
		}else if("right".equalsIgnoreCase(algin)){
			chartParameterStr+="2";
		}
		chartParameterStr+="`";
		
		// （左 右 上 下） 边距
		String marginLeft = String.valueOf((int)(chartParameter.getItemLeftMargin()*100));
		String marginRight = String.valueOf(100-(int)(chartParameter.getItemRightMargin()*100));
		String marginTop = String.valueOf((int)(chartParameter.getItemLowerMargin()*100));
		String marginBottom = String.valueOf(100-(int)(chartParameter.getItemUpperMargin()*100));

		
		/*System.out.println(marginLeft);
		System.out.println(marginRight);
		System.out.println(marginTop);
		System.out.println(marginBottom);*/
		
		
		chartParameterStr+=marginLeft;
		chartParameterStr+="`";
		chartParameterStr+=marginRight;
		chartParameterStr+="`";
		chartParameterStr+=marginTop;
		chartParameterStr+="`";
		chartParameterStr+=marginBottom;
		chartParameterStr+="`";
		
		
		String isAuto = String.valueOf(chartParameter.isAutoRangeValue());//是否自动
		if("true".equalsIgnoreCase(isAuto)){
			chartParameterStr+="true";
		}else{
			String startValue = String.valueOf((chartParameter.getNumberAxisStartValue()));//起始值
			String endValue = String.valueOf((chartParameter.getNumberAxisEndValue()));//终值
			chartParameterStr+="false";
			chartParameterStr+=",";
			chartParameterStr+=startValue;
			chartParameterStr+=",";
			chartParameterStr+=endValue;
		}
		chartParameterStr+="`";
		
		String increment = String.valueOf((chartParameter.getNumberAxisIncrement()));//增量
		chartParameterStr+=increment;
		
		return chartParameterStr;
	}
	
	

	
	public float getCategoryLabelMaxWidth() {
		return categoryLabelMaxWidth;
	}



	public void setCategoryLabelMaxWidth(float categoryLabelMaxWidth) {
		this.categoryLabelMaxWidth = categoryLabelMaxWidth;
	}



	public String getCategoryLabelType() {
		return categoryLabelType;
	}



	public void setCategoryLabelType(String categoryLabelType) {
		this.categoryLabelType = categoryLabelType;
	}



	public double getCategoryMargin() {
		return categoryMargin;
	}



	public void setCategoryMargin(double categoryMargin) {
		this.categoryMargin = categoryMargin;
	}



	public String getChartTitle() {
		return chartTitle;
	}



	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}


	public boolean isItemLabelsVisible() {
		return isItemLabelsVisible;
	}



	public void setItemLabelsVisible(boolean isItemLabelsVisible) {
		this.isItemLabelsVisible = isItemLabelsVisible;
	}



	public double getItemLeftMargin() {
		return itemLeftMargin;
	}



	public void setItemLeftMargin(double itemLeftMargin) {
		this.itemLeftMargin = itemLeftMargin;
	}



	public double getItemLowerMargin() {
		return itemLowerMargin;
	}



	public void setItemLowerMargin(double itemLowerMargin) {
		this.itemLowerMargin = itemLowerMargin;
	}



	public double getItemMargin() {
		return itemMargin;
	}



	public void setItemMargin(double itemMargin) {
		this.itemMargin = itemMargin;
	}



	public double getItemRightMargin() {
		return itemRightMargin;
	}



	public void setItemRightMargin(double itemRightMargin) {
		this.itemRightMargin = itemRightMargin;
	}



	public double getItemUpperMargin() {
		return itemUpperMargin;
	}



	public void setItemUpperMargin(double itemUpperMargin) {
		this.itemUpperMargin = itemUpperMargin;
	}



	public HashMap getMarkerMap() {
		return markerMap;
	}



	public void setMarkerMap(HashMap markerMap) {
		this.markerMap = markerMap;
	}



	public String getNoDataMessage() {
		return noDataMessage;
	}



	public void setNoDataMessage(String noDataMessage) {
		this.noDataMessage = noDataMessage;
	}



	public double getNumberAxisEndValue() {
		return numberAxisEndValue;
	}



	public void setNumberAxisEndValue(double numberAxisEndValue) {
		this.numberAxisEndValue = numberAxisEndValue;
	}



	public double getNumberAxisIncrement() {
		return numberAxisIncrement;
	}



	public void setNumberAxisIncrement(double numberAxisIncrement) {
		this.numberAxisIncrement = numberAxisIncrement;
	}



	public double getNumberAxisStartValue() {
		return numberAxisStartValue;
	}



	public void setNumberAxisStartValue(double numberAxisStartValue) {
		this.numberAxisStartValue = numberAxisStartValue;
	}



	public boolean isAutoRangeValue() {
		return isAutoRangeValue;
	}



	public void setAutoRangeValue(boolean isAutoRangeValue) {
		this.isAutoRangeValue = isAutoRangeValue;
	}

	
	
	public String getMarkers() {
		return markers;
	}

	public void setMarkers(String markers) {
		this.markers = markers;
	}

	
	public String getChartTitleAlign() {
		return chartTitleAlign;
	}

	public void setChartTitleAlign(String chartTitleAlign) {
		this.chartTitleAlign = chartTitleAlign;
	}

	public static void main(String [] args){
		ChartParameter cp = new ChartParameter();
		String temp="男女的平均年龄分析`1`0`0`100``0`100``";
		cp.analyseChartParameter(temp);
		
	}

	public float getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public int getLineNodeIsMarked() {
		return lineNodeIsMarked;
	}

	public void setLineNodeIsMarked(int lineNodeIsMarked) {
		this.lineNodeIsMarked = lineNodeIsMarked;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
}
