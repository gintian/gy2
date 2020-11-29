package com.hjsj.hrms.actionform.selfinfomation;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

public class RelationForm extends FrameForm {
	
	//节点请求链接
	private String action;
	
	//action链接参数名称
	private String paramkey;
	
	//超链接请求显示目标
	private String target;
	
	//action链接参数名称dbpre参数名称
	private String dbnamekey;
	//action链接参数名称a0100参数名称
	private String a0100key;
	
	//action连接中b0110key参数名称
	private String b0110key;
	
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub

	}

	
	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		if("/selfservice/selfinfo/relation".equals(mapping.getPath())&&request.getParameter("b_init")!=null)
	    {
	        this.paramkey=null;
	        this.a0100key=null;
	        this.dbnamekey=null;
	        this.b0110key=null;
	    }
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getParamkey() {
		return paramkey;
	}

	public void setParamkey(String paramkey) {
		this.paramkey = paramkey;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getDbnamekey() {
		return dbnamekey;
	}

	public void setDbnamekey(String dbnamekey) {
		this.dbnamekey = dbnamekey;
	}

	public String getA0100key() {
		return a0100key;
	}

	public void setA0100key(String a0100key) {
		this.a0100key = a0100key;
	}

	public String getB0110key() {
		return b0110key;
	}

	public void setB0110key(String b0110key) {
		this.b0110key = b0110key;
	}

}
