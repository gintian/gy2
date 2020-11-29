package com.hjsj.hrms.actionform.train.trainexam.exam;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Title:MyTestForm.java
 * </p>
 * <p>
 * Description:自测考试
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-05 13:23:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class MyTestForm extends FrameForm {
   
	// 自测考试sql
	private String myTestSql;
	
	// 自测考试where条件
	private String myTestWhere;
	
	//自测考试列
	private String myTestColumn;
	
	// 自测考试排序
	private String myTestOrder;
	
	// 试卷编号
	private String r5300;
	
	// 课程编号
	private String r5000;
	
	// 答卷模式,1为正版考试，2为单题考试
	private String modelType;
	
	// 自考测试
	private String testCount;
	//课程状态  =0：未学；=1：正学；=2：已学
	private String state;

	public String getTestCount() {
		return testCount;
	}

	public void setTestCount(String testCount) {
		this.testCount = testCount;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public String getR5000() {
		return r5000;
	}

	public void setR5000(String r5000) {
		this.r5000 = r5000;
	}

	public String getR5300() {
		return r5300;
	}

	public void setR5300(String r5300) {
		this.r5300 = r5300;
	}

	@Override
    public void inPutTransHM() {
	
    }

    @Override
    public void outPutFormHM() {
    	this.setMyTestColumn((String) this.getFormHM().get("myTestColumn"));
    	this.setMyTestOrder((String) this.getFormHM().get("myTestOrder"));
    	this.setMyTestSql((String) this.getFormHM().get("myTestSql"));
    	this.setMyTestWhere((String) this.getFormHM().get("myTestWhere"));
    	this.setR5300((String) this.getFormHM().get("r5300"));
    	this.setR5000((String) this.getFormHM().get("r5000"));
    	this.setModelType((String) this.getFormHM().get("modelType"));
    	this.setTestCount((String) this.getFormHM().get("testCount"));
    	this.setState((String) this.getFormHM().get("state"));
    }

    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	
	return super.validate(arg0, arg1);
    }

	public String getMyTestSql() {
		return myTestSql;
	}

	public void setMyTestSql(String myTestSql) {
		this.myTestSql = myTestSql;
	}

	public String getMyTestWhere() {
		return myTestWhere;
	}

	public void setMyTestWhere(String myTestWhere) {
		this.myTestWhere = myTestWhere;
	}

	public String getMyTestColumn() {
		return myTestColumn;
	}

	public void setMyTestColumn(String myTestColumn) {
		this.myTestColumn = myTestColumn;
	}

	public String getMyTestOrder() {
		return myTestOrder;
	}

	public void setMyTestOrder(String myTestOrder) {
		this.myTestOrder = myTestOrder;
	}

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    
}
