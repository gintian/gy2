package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.transaction.sys.export.syncFrigger.CreateSyncFrigger;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 *<p>Title:EditHrSyncName.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 4, 2009:11:45:22 AM</p> 
 *@author huaitao
 *@version 1.0
 */
public class DeleteHrSyncName extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String type = (String)this.getFormHM().get("type");
		ContentDAO dao = new ContentDAO(this.frameconn);
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		ArrayList selectedlist = (ArrayList)this.getFormHM().get("selectedlist");
		for(int i=0;i<selectedlist.size();i++){
			LazyDynaBean bean = (LazyDynaBean)selectedlist.get(i);
			String field = (String)bean.get("field");
			String tofield=(String)bean.get("tofield");
			/*if(field.equalsIgnoreCase("a0101"))
			    continue;*/
			if("A".equalsIgnoreCase(type)){
				String codefieldstr = hsb.getTextValue(HrSyncBo.CODE_FIELDS).toUpperCase();
				String onlyfield = hsb.getAttributeValue(HrSyncBo.HR_ONLY_FIELD);
				if(onlyfield!=null)
					onlyfield=onlyfield.toUpperCase();
				if(codefieldstr.indexOf(field.toUpperCase()) != -1){
					throw new GeneralException("该字段已经被设置为翻译型指标，现在无法删除！");
				}
				if(field.equalsIgnoreCase(onlyfield)){
					throw new GeneralException("该字段已经被设置为唯一性字段指标，现在无法删除！");
				}
				String customname = hsb.getAppAttributeValue(HrSyncBo.A,field);
				
				hsb.delAppAttributeValue(HrSyncBo.A,field);
				String fields = hsb.getTextValue(HrSyncBo.FIELDS);
				String codefields = hsb.getTextValue(HrSyncBo.CODE_FIELDS);
				if(fields.indexOf(field+",") != -1){
					fields = fields.substring(0,fields.indexOf(field+","))+fields.substring(fields.indexOf(field+",")+(field+",").length());
					hsb.setTextValue(HrSyncBo.FIELDS,fields);
				}else if(fields.indexOf(","+field) != -1){
					fields = fields.substring(0,fields.lastIndexOf(","));
					hsb.setTextValue(HrSyncBo.FIELDS,fields);
				}else{
					if(fields.indexOf(field) != -1) //添加 最后一个指标 前后没有逗号  判断  29820 wangb 20170717
						fields="";
					hsb.setTextValue(HrSyncBo.FIELDS,fields);
				}
				if(codefields.indexOf(field+",") != -1){
					codefields = codefields.substring(0,codefields.indexOf(field+","))+codefields.substring(codefields.indexOf(field+",")+(field+",").length());
					hsb.setTextValue(HrSyncBo.CODE_FIELDS,codefields);
				}else if(codefields.indexOf(","+field) != -1){
					codefields = codefields.substring(0,codefields.lastIndexOf(","));
					hsb.setTextValue(HrSyncBo.CODE_FIELDS,codefields);
				}else{
					hsb.setTextValue(HrSyncBo.CODE_FIELDS,codefields);
				}
				if(!"A0101".equalsIgnoreCase(customname)){
					hsb.deleteColumn("t_hr_view",customname);
				}
			}
			else if("B".equalsIgnoreCase(type)){
				String codefieldstr = hsb.getTextValue(HrSyncBo.ORG_CODE_FIELDS).toUpperCase();
				if(codefieldstr.indexOf(field.toUpperCase()) != -1){
					throw new GeneralException("该字段已经被设置为翻译型指标，现在无法删除！");
				}
				String customname = hsb.getAppAttributeValue(HrSyncBo.B,field);
				hsb.delAppAttributeValue(HrSyncBo.B,field);
				String orgfields = hsb.getTextValue(HrSyncBo.ORG_FIELDS);
				String codeorgfields = hsb.getTextValue(HrSyncBo.ORG_CODE_FIELDS);
				if(orgfields.indexOf(field+",")!=-1){
					orgfields = orgfields.substring(0,orgfields.indexOf(field+","))+orgfields.substring(orgfields.indexOf(field+",")+(field+",").length());
					hsb.setTextValue(HrSyncBo.ORG_FIELDS,orgfields);
				}else if(orgfields.indexOf(","+field)!=-1){
					orgfields = orgfields.substring(0,orgfields.lastIndexOf(","));
					hsb.setTextValue(HrSyncBo.ORG_FIELDS,orgfields);
				}else{
					if(orgfields.indexOf(field) != -1)//添加 最后一个指标 前后没有逗号  判断  29820 wangb 20170717
						orgfields="";
					hsb.setTextValue(HrSyncBo.ORG_FIELDS,orgfields);
				}
				if(codeorgfields.indexOf(field+",") != -1){
					codeorgfields = codeorgfields.substring(0,codeorgfields.indexOf(field+","))+codeorgfields.substring(codeorgfields.indexOf(field+",")+(field+",").length());
					hsb.setTextValue(HrSyncBo.ORG_CODE_FIELDS,codeorgfields);
				}else if(codeorgfields.indexOf(","+field) != -1){
					codeorgfields = codeorgfields.substring(0,codeorgfields.lastIndexOf(","));
					hsb.setTextValue(HrSyncBo.ORG_CODE_FIELDS,codeorgfields);
				}else{
					hsb.setTextValue(HrSyncBo.ORG_CODE_FIELDS,codeorgfields);
				}
				hsb.deleteColumn("t_org_view",customname);
			}else if("K".equalsIgnoreCase(type)){
				String codefieldstr = hsb.getTextValue(HrSyncBo.POST_CODE_FIELDS).toUpperCase();
				if(codefieldstr.indexOf(field.toUpperCase()) != -1){
					throw new GeneralException("该字段已经被设置为翻译型指标，现在无法删除！");
				}
				String customname = hsb.getAppAttributeValue(HrSyncBo.K,field);
				hsb.delAppAttributeValue(HrSyncBo.K,field);
				String postfields = hsb.getTextValue(HrSyncBo.POST_FIELDS);
				String codeorgfields = hsb.getTextValue(HrSyncBo.POST_CODE_FIELDS);
				if(postfields.indexOf(field+",")!=-1){
					postfields = postfields.substring(0,postfields.indexOf(field+","))+postfields.substring(postfields.indexOf(field+",")+(field+",").length());
					hsb.setTextValue(HrSyncBo.POST_FIELDS,postfields);
				}else if(postfields.indexOf(","+field)!=-1){
					postfields = postfields.substring(0,postfields.lastIndexOf(","));
					hsb.setTextValue(HrSyncBo.POST_FIELDS,postfields);
				}else{
					if(postfields.indexOf(field) != -1)//添加 最后一个指标 前后没有逗号  判断  29820 wangb 20170717
						postfields="";
					hsb.setTextValue(HrSyncBo.POST_FIELDS,postfields);
				}
				if(codeorgfields.indexOf(field+",")!=-1){
					codeorgfields = codeorgfields.substring(0,codeorgfields.indexOf(field+","))+codeorgfields.substring(codeorgfields.indexOf(field+",")+(field+",").length());
					hsb.setTextValue(HrSyncBo.POST_CODE_FIELDS,codeorgfields);
				}else if(codeorgfields.indexOf(","+field)!=-1){
					codeorgfields = codeorgfields.substring(0,codeorgfields.lastIndexOf(","));
					hsb.setTextValue(HrSyncBo.POST_CODE_FIELDS,codeorgfields);
				}else{
					hsb.setTextValue(HrSyncBo.POST_CODE_FIELDS,codeorgfields);
				}
				hsb.deleteColumn("t_post_view",customname);
			}
			
		}
		hsb.saveParameter(dao);
		//删除字段时更新触发器 wangb 20170616
		boolean fieldAndCode = false;
		String fieldAndCodeSeq = ":";
		if ("1".equals(hsb.getAttributeValue(HrSyncBo.FIELDANDCODE))){
			fieldAndCode = true;
			fieldAndCodeSeq = hsb.getAttributeValue(HrSyncBo.FIELDANDCODESEQ);
		}
		CreateSyncFrigger csf = new CreateSyncFrigger(this.frameconn,
				this.userView, fieldAndCode, fieldAndCodeSeq);
		if("A".equalsIgnoreCase(type)){//删除人员视图字段 wangb 20170616
			//先删除触发器 wangb 20170615
			csf.delFrigger(CreateSyncFrigger.HR_FLAG);
			//后添加触发器 wangb 20170615
			csf.createFrigger(CreateSyncFrigger.HR_FLAG);
		}else if("B".equalsIgnoreCase(type)){//删除单位视图字段 wangb 20170616
			//先删除触发器 wangb 20170616
			csf.delFrigger(CreateSyncFrigger.ORG_FLAG);
			//后添加触发器 wangb 20170616
			csf.createFrigger(CreateSyncFrigger.ORG_FLAG);
		}else if("K".equalsIgnoreCase(type)){//删除岗位视图字段 wangb 20170616
			//先删除触发器 wangb 20170616
			csf.delFrigger(CreateSyncFrigger.POST_FLAG);
			//后添加触发器 wangb 20170616
			csf.createFrigger(CreateSyncFrigger.POST_FLAG);
		}
	}

}
