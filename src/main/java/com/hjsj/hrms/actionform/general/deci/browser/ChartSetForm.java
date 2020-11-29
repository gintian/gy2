/**
 * 
 */
package com.hjsj.hrms.actionform.general.deci.browser;

import com.hjsj.hrms.taglib.general.ChartParameter;
import com.hrms.struts.action.FrameForm;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 19, 2006:1:50:59 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ChartSetForm extends FrameForm {

	private ChartParameter chartParameter;
	
	/**
	 * 
	 */
	public ChartSetForm() {
		super();

	}

	
	@Override
    public void outPutFormHM() {
		this.setChartParameter((ChartParameter)this.getFormHM().get("chartParameter"));
	}

	@Override
    public void inPutTransHM() {
		
	}

	
	public ChartParameter getChartParameter() {
		return chartParameter;
	}

	public void setChartParameter(ChartParameter chartParameter) {
		this.chartParameter = chartParameter;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
