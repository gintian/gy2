/**
 * 
 */
package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.businessobject.sys.AnychartBo;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.ArrayList;

/**
 * @author cmq
 * Jun 5, 20101:26:40 PM
 */
public class GaugeBoardTag extends BodyTagSupport {

	/**状态良好值*/
	private ArrayList gvalue=new ArrayList();
	/**状态正常值*/
	private ArrayList yvalue=new ArrayList();
	/**预警值*/
	private ArrayList rvalue=new ArrayList();
	/**当前值*/
	private ArrayList cvalue=new ArrayList();
	/**指标名称*/
	private String title="";
	/**chart输出面板*/
	private String chartpnl;
	private int width = 100;// 宽度

	private int height = 100;// 高度
	/**小数点的位数*/
	private int numDecimals=0;	
	public int getNumDecimals() {
		return numDecimals;
	}
	public void setNumDecimals(int numDecimals) {
		this.numDecimals = numDecimals;
	}
	/**
	 * 
	 */
	public GaugeBoardTag() {
		super();
	}
	/**
	 * 
	 * @return
	 */
	private String outBoardJs()
	{
		StringBuffer buf=new StringBuffer();

		/**flash style*/
		AnychartBo chartbo=new AnychartBo(this.title,-1,this.width,this.height);
		chartbo.setNumDecimals(this.numDecimals);
		chartbo.setChartpnl(this.chartpnl);		
		buf.append(chartbo.outGaugeBoardChart(gvalue, yvalue, rvalue, cvalue,""));
		return buf.toString();
	}
	
	public int doStartTag() throws JspException {
        try
        {   
        	StringBuffer buf=new StringBuffer();
        	String js=outBoardJs();
        	//System.out.println("javascript="+js);
			buf.append(js);
        	
	        pageContext.getOut().println(buf.toString());

	        return EVAL_BODY_BUFFERED;   
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	return SKIP_BODY;
        }
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getChartpnl() {
		return chartpnl;
	}
	public void setChartpnl(String chartpnl) {
		this.chartpnl = chartpnl;
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
	public ArrayList getGvalue() {
		return gvalue;
	}
	public void setGvalue(ArrayList gvalue) {
		this.gvalue = gvalue;
	}
	public ArrayList getYvalue() {
		return yvalue;
	}
	public void setYvalue(ArrayList yvalue) {
		this.yvalue = yvalue;
	}
	public ArrayList getRvalue() {
		return rvalue;
	}
	public void setRvalue(ArrayList rvalue) {
		this.rvalue = rvalue;
	}
	public ArrayList getCvalue() {
		return cvalue;
	}
	public void setCvalue(ArrayList cvalue) {
		this.cvalue = cvalue;
	}	
	

}
