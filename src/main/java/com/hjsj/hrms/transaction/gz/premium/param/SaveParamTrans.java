package com.hjsj.hrms.transaction.gz.premium.param;

import com.hjsj.hrms.businessobject.ht.ContractBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>
 * Title:SearchBonusParamTrans.java
 * </p>
 * <p>
 * Description:奖金参数
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-02 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveParamTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
    	String setid=this.getFormHM().get("setid")==null?"":(String)this.getFormHM().get("setid");
    	String dist_field=this.getFormHM().get("dist_field")==null?"":(String)this.getFormHM().get("dist_field");
    	String rep_field=this.getFormHM().get("rep_field")==null?"":(String)this.getFormHM().get("rep_field");
    	String keep_save_field=this.getFormHM().get("keep_save_field")==null?"":(String)this.getFormHM().get("keep_save_field");
    	String bonus_sum_field=this.getFormHM().get("bonus_sum_field")==null?"":(String)this.getFormHM().get("bonus_sum_field");
    	String dist_sum_field=this.getFormHM().get("dist_sum_field")==null?"":(String)this.getFormHM().get("dist_sum_field");
    	String surplus_field=this.getFormHM().get("surplus_field")==null?"":(String)this.getFormHM().get("surplus_field");
    	String cardid=this.getFormHM().get("cardid")==null?"":(String)this.getFormHM().get("cardid");
    	String salaryid=this.getFormHM().get("salaryid")==null?"":(String)this.getFormHM().get("salaryid");
    	String stat_dbpre=this.getFormHM().get("stat_dbpre")==null?"":(String)this.getFormHM().get("stat_dbpre");
    	String checkUn_field=this.getFormHM().get("checkUn_field")==null?"":(String)this.getFormHM().get("checkUn_field");
    	
    	ContractBo bo = new ContractBo(this.frameconn, this.userView);
    	ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
    	xml.setAttributeValue("/Params/BONUS_SET", "setid",setid);
    	xml.setAttributeValue("/Params/BONUS_SET", "dist_field",dist_field);
    	xml.setAttributeValue("/Params/BONUS_SET", "rep_field",rep_field);
    	xml.setAttributeValue("/Params/BONUS_SET", "keep_save_field",keep_save_field);
    	xml.setAttributeValue("/Params/BONUS_SET", "bonus_sum_field",bonus_sum_field);
    	xml.setAttributeValue("/Params/BONUS_SET", "dist_sum_field",dist_sum_field);
    	xml.setAttributeValue("/Params/BONUS_SET", "surplus_field",surplus_field);
    	xml.setAttributeValue("/Params/BONUS_SET", "cardid",cardid);
    	xml.setAttributeValue("/Params/BONUS_SET", "salaryid",salaryid);
    	xml.setAttributeValue("/Params/BONUS_SET", "stat_dbpre",stat_dbpre);
       	xml.setAttributeValue("/Params/BONUS_SET", "checkUn_field",checkUn_field);
       	
    	xml.saveStrValue();
    }
}
