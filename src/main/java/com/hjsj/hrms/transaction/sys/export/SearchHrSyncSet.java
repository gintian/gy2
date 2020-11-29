package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SearchHrSyncSet.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 31, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SearchHrSyncSet extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		// 人员
		String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
		dbnamestr = hsb.getDBMess(dbnamestr);
		this.getFormHM().put("dbnamestr",dbnamestr);
		// 基本指标
		String fieldstr=hsb.getTextValue(HrSyncBo.FIELDS);	
		ArrayList list=hsb.getFields(fieldstr);
		fieldstr=hsb.getMess(list);		
		this.getFormHM().put("fieldstr",fieldstr);
		ArrayList sync_fieldlist=hsb.searcSyncFields();	
		this.getFormHM().put("sync_fieldlist",sync_fieldlist);
		//翻译
		String codefieldstr=hsb.getTextValue(HrSyncBo.CODE_FIELDS);	
		ArrayList codelist=hsb.getFields(codefieldstr);
		codefieldstr=hsb.getMess(codelist);		
		this.getFormHM().put("codefieldstr",codefieldstr);
		//机构
		String orgfieldstr=hsb.getTextValue(HrSyncBo.ORG_FIELDS);	
		ArrayList orglist=hsb.getFields(orgfieldstr);
		orgfieldstr=hsb.getMess(orglist);		
		this.getFormHM().put("orgfieldstr",orgfieldstr);
		//机构翻译
		String orgcodefieldstr=hsb.getTextValue(HrSyncBo.ORG_CODE_FIELDS);	
		ArrayList orgcodelist=hsb.getFields(orgcodefieldstr);
		orgcodefieldstr=hsb.getMess(orgcodelist);		
		this.getFormHM().put("orgcodefieldstr",orgcodefieldstr);
		//职位
		String postfieldstr=hsb.getTextValue(HrSyncBo.POST_FIELDS);	
		ArrayList postlist=hsb.getFields(postfieldstr);
		postfieldstr=hsb.getMess(postlist);		
		this.getFormHM().put("postfieldstr",postfieldstr);
		//职位翻译		
		String postcodefieldstr=hsb.getTextValue(HrSyncBo.POST_CODE_FIELDS);	
		ArrayList postcodelist=hsb.getFields(postcodefieldstr);
		postcodefieldstr=hsb.getMess(postcodelist);		
		this.getFormHM().put("postcodefieldstr",postcodefieldstr);
		//全部代码翻译成代码描述	
		String code_value = hsb.getAttributeValue(HrSyncBo.CODE);
		code_value=code_value!=null&&code_value.trim().length()>0?code_value:"0";
		this.getFormHM().put("code_value",code_value);
		String sync_field = hsb.getAttributeValue(HrSyncBo.KEY_FIELd);	
		this.getFormHM().put("sync_field",sync_field);
		String sync_A01=hsb.getAttributeValue(HrSyncBo.SYNC_A01);//同步人员	
		sync_A01=sync_A01!=null&&sync_A01.trim().length()>0?sync_A01:"0";
		this.getFormHM().put("sync_A01",sync_A01);
		String sync_B01=hsb.getAttributeValue(HrSyncBo.SYNC_B01);//同步单位 
		sync_B01=sync_B01!=null&&sync_B01.trim().length()>0?sync_B01:"0";
		this.getFormHM().put("sync_B01",sync_B01);
		String sync_K01=hsb.getAttributeValue(HrSyncBo.SYNC_K01);//同步岗位
		sync_K01=sync_K01!=null&&sync_K01.trim().length()>0?sync_K01:"0";
		this.getFormHM().put("sync_K01",sync_K01);
		// 同步照片
		String photo = hsb.getAttributeValue(HrSyncBo.photo);
		photo=photo!=null&&photo.trim().length()>0?photo:"0";
		this.getFormHM().put("photo",photo);
		
		// 同步指标变动前后信息
		String fieldChange = hsb.getAttributeValue(HrSyncBo.FIELDCHANGE);
		fieldChange=fieldChange!=null&&fieldChange.trim().length()>0?fieldChange:"0";
		this.getFormHM().put("fieldChange",fieldChange);
		
		// 翻译指标包含代码
		String fieldAndCode = hsb.getAttributeValue(HrSyncBo.FIELDANDCODE);
		fieldAndCode=fieldAndCode!=null&&fieldAndCode.trim().length()>0?fieldAndCode:"0";
		this.getFormHM().put("fieldAndCode",fieldAndCode);
		
		// 分隔符
		String fieldAndCodeSeq = hsb.getAttributeValue(HrSyncBo.FIELDANDCODESEQ);
		fieldAndCodeSeq=fieldAndCodeSeq!=null&&fieldAndCodeSeq.trim().length()>0?fieldAndCodeSeq:":";
		this.getFormHM().put("fieldAndCodeSeq",fieldAndCodeSeq);

		String jz_field=hsb.getAttributeValue(hsb.JZ_FIELD);//同步兼职
		jz_field = jz_field == null || jz_field.length()<1 ? "0" : jz_field;
		this.getFormHM().put("jz_field", jz_field);
		String onlyfield = hsb.getAttributeValue(hsb.HR_ONLY_FIELD);//唯一性指标		

		ArrayList onlylist=hsb.getFields(onlyfield);
		String onlyfieldstr=hsb.getMess(onlylist);	
		this.getFormHM().put("onlyfieldstr", onlyfieldstr);
		
		String sync_mode= hsb.getAttributeValue(HrSyncBo.SYNC_MODE);//同步方式
		sync_mode=sync_mode!=null&&sync_mode.trim().length()>0?sync_mode:"time_job";//定时任务：time_job；触发器：trigger	
		this.getFormHM().put("sync_mode",sync_mode);
		String fail_limit=hsb.getAttributeValue(HrSyncBo.FAIL_LIMIT);
		fail_limit=fail_limit!=null&&fail_limit.trim().length()>0?fail_limit:"";//失败次数上限
		this.getFormHM().put("fail_limit", fail_limit);
	}

}
