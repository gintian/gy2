/*
 * Created on 2005-5-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.cardingcard;

import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.action.FrameForm;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class CardingcardConstantForm extends FrameForm {
	private String constant;
	private String type;
	private String str_value;
	private String describe;
	CardTagParamView cardparam=new CardTagParamView();
	private RecordVo constant_vo=ConstantParamter.getConstantVo("SS_CALLINGCARD");
	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setConstant_vo((RecordVo)this.getFormHM().get("constant_vo"));

	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("constant_vo",constant_vo);

	}

	/**
	 * @return Returns the constant.
	 */
	public String getConstant() {
		return constant;
	}
	/**
	 * @param constant The constant to set.
	 */
	public void setConstant(String constant) {
		this.constant = constant;
	}
	/**
	 * @return Returns the constant_vo.
	 */
	public RecordVo getConstant_vo() {
		return constant_vo;
	}
	/**
	 * @param constant_vo The constant_vo to set.
	 */
	public void setConstant_vo(RecordVo constant_vo) {
		this.constant_vo = constant_vo;
	}
	/**
	 * @return Returns the describe.
	 */
	public String getDescribe() {
		return describe;
	}
	/**
	 * @param describe The describe to set.
	 */
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	/**
	 * @return Returns the str_value.
	 */
	public String getStr_value() {
		return str_value;
	}
	/**
	 * @param str_value The str_value to set.
	 */
	public void setStr_value(String str_value) {
		this.str_value = str_value;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return Returns the cardparam.
	 */
	public CardTagParamView getCardparam() {
		return cardparam;
	}
	/**
	 * @param cardparam The cardparam to set.
	 */
	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}
}
