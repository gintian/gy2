package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.*;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:GetSalaryTemplatePropertyTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 29, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class GetSalaryTemplatePropertyTrans  extends IBusiness {
	
	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)hm.get("salaryid"); 
			String gz_module=(String)hm.get("gz_module"); 
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,gz_module);
			SalaryPropertyBo bo=new SalaryPropertyBo(this.getFrameconn(),salaryid,Integer.parseInt(gz_module),this.getUserView());
			String priecerate_valid = "";//计件薪资，赵旭光加
			String  flow_ctrl=bo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL,"flag");
			if(flow_ctrl==null||flow_ctrl.trim().length()==0)
				flow_ctrl="0";
			
			String  reject_mode=bo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL,"reject_mode");
			if(reject_mode==null||reject_mode.trim().length()==0)
				reject_mode="1";  //驳回方式  1:逐级驳回  2：驳回到发起人
			String  verify_ctrl=bo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL);
			if(verify_ctrl==null||verify_ctrl.trim().length()==0) ////是否按审核条件控制
				verify_ctrl="0";
			
			String verify_ctrl_ff=bo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_ctrl_ff");
			String verify_ctrl_sp=bo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_ctrl_sp");
			if("0".equals(verify_ctrl))
			{
				verify_ctrl_ff="0";
				verify_ctrl_sp="0";
			}
			else
			{
				if(verify_ctrl_ff==null||verify_ctrl_ff.trim().length()==0)
					verify_ctrl_ff="1";
				if(verify_ctrl_sp==null||verify_ctrl_sp.trim().length()==0)
					verify_ctrl_sp="1";
			}
			
			String  piecerate=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECERATE);
			ArrayList piecerateList=bo.getSalarySetList("N","");
			
			ArrayList dbList=bo.getDbList();
			String   personScope=bo.getPersonScope();
			ArrayList moneyTypeList=bo.getMoneyStyleList();
			String moneyType=String.valueOf(bo.getVo().getString("nmoneyid"));
			ArrayList varyModelList=bo.getVaryModelList();
			ArrayList calculateTaxTimeList=bo.getSalarySetList("D","");
			String    calculateTaxTime=bo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_DATE_FIELD);
			ArrayList appealTaxTimeList=calculateTaxTimeList;
			String    appealTaxTime=bo.getCtrlparam().getValue(SalaryCtrlParamBo.DECLARE_TAX);
			ArrayList sendSalaryItemList=bo.getSalarySetList("A","42");
			String    sendSalaryItem=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PAY_FLAG);
			ArrayList taxTypeList=bo.getSalarySetList("A","46");
			String    taxType=bo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_MODE);
			ArrayList ratepayingDeclareList=bo.getSalarySetList("A","0");
			String    ratepayingDecalre=bo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_DESC);
			String    condStr=bo.getVo().getString("cond");
			String nmodule=(String)hm.get("nmodule");
			ArrayList mailTemplateList=bo.getEmailTemplateListByNmodule(nmodule,1);
			String mailTemplateId=bo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE);
			String mailNotice=bo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"mail");
			String smsNotice=bo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"sms");
			String a01z0Flag=bo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");
			if(a01z0Flag==null)
				a01z0Flag="0";
			if(mailTemplateId==null)
				mailTemplateId="";
			if(mailNotice==null)
				mailNotice="";
			if(smsNotice==null)
				smsNotice="";
			if(condStr==null)
				condStr="";
			String    cexpr=bo.getVo().getString("cexpr");
			if(cexpr==null)
				cexpr="";
			String amount_ctrl=bo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");
			if(amount_ctrl==null|| "".equals(amount_ctrl))
			{
				amount_ctrl="0";
			}
			
			String amount_ctrl_ff=bo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_ff");
			String amount_ctrl_sp=bo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_sp");
			if("0".equals(amount_ctrl))
			{
				amount_ctrl_ff="0";
				amount_ctrl_sp="0";
			}
			else
			{
				if(amount_ctrl_ff==null||amount_ctrl_ff.trim().length()==0)
					amount_ctrl_ff="1";
				if(amount_ctrl_sp==null||amount_ctrl_sp.trim().length()==0)
					amount_ctrl_sp="1";
			}
			
			String ctrlType = bo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"ctrl_type");
			if("0".equals(amount_ctrl))
			{
				ctrlType="1";
			}else
			{
				if(ctrlType==null||ctrlType.trim().length()==0)
					ctrlType="1";
			}
			
			String priv_mode =bo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			if(priv_mode==null|| "".equals(priv_mode))
			{
				priv_mode="0";
			}
			String manager =bo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String isShare="1";
			if(manager==null||manager.trim().length()==0)
			{
				isShare="0";
				manager="";
			}
			String  bonus=bo.getCtrlparam().getValue(SalaryCtrlParamBo.BONUS);
			//xujian 2009-9-22
			GrossPayManagement gross = new GrossPayManagement(this.getFrameconn());
			String orgid = bo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid");
			ArrayList orgList = gross.getOrgOrDeptListFromSalaryset("UN",salaryid);
			String deptid = bo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
			ArrayList deptList = gross.getOrgOrDeptListFromSalaryset("UM",salaryid);
			String contrlLevelId = bo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD, "layer");
			ArrayList contrlLevelList = gross.getContrlLevelList();
			String sum_type = bo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD, "sum_type");
			sum_type = sum_type!=null&&sum_type.trim().length()>0?sum_type:"0";
			
			
			String  sp_relation_id=bo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL,"sp_relation_id");
			ArrayList sprelationList=bo.getSpRelationList();
			if("0".equals(flow_ctrl))
				sp_relation_id="";
			else if(sp_relation_id==null)
				sp_relation_id=""; 
			this.getFormHM().put("spRelationList", sprelationList);
			this.getFormHM().put("sp_relation_id", sp_relation_id);

		
			this.getFormHM().put("verify_ctrl",verify_ctrl);
			this.getFormHM().put("verify_ctrl_ff",verify_ctrl_ff);
			this.getFormHM().put("verify_ctrl_sp",verify_ctrl_sp);
			
			
			
			this.getFormHM().put("reject_mode",reject_mode);
			this.getFormHM().put("orgid",orgid);
			this.getFormHM().put("orgList",orgList);
			this.getFormHM().put("deptid",deptid);
			this.getFormHM().put("deptList",deptList);
			this.getFormHM().put("contrlLevelId", contrlLevelId);
			this.getFormHM().put("contrlLevelList", contrlLevelList);
			this.getFormHM().put("sum_type", sum_type);
			
			this.getFormHM().put("bonusItemFld", bonus);
			this.getFormHM().put("bonusItemFldList", this.getBonusList(salaryid));
			this.getFormHM().put("mailTemplateList", mailTemplateList);
			this.getFormHM().put("mailTemplateId", mailTemplateId);
			this.getFormHM().put("mailNotice", mailNotice);
			this.getFormHM().put("msNotice", smsNotice);
			this.getFormHM().put("isShare",isShare);
			this.getFormHM().put("manager",manager);
			this.getFormHM().put("amount_ctrl",amount_ctrl);
			this.getFormHM().put("amount_ctrl_ff",amount_ctrl_ff);
			this.getFormHM().put("amount_ctrl_sp",amount_ctrl_sp);
			
			this.getFormHM().put("priv_mode",priv_mode);
			this.getFormHM().put("flow_ctrl",flow_ctrl);
			this.getFormHM().put("piecerate",piecerate);
			this.getFormHM().put("piecerateList",piecerateList);
			this.getFormHM().put("condStr",condStr);
			this.getFormHM().put("cexpr",cexpr);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("dbList",dbList);
			this.getFormHM().put("personScope",personScope);
			this.getFormHM().put("moneyTypeList",moneyTypeList);
			this.getFormHM().put("moneyType",moneyType);
			this.getFormHM().put("varyModelList",varyModelList);
			this.getFormHM().put("calculateTaxTimeList",calculateTaxTimeList);
			this.getFormHM().put("calculateTaxTime",calculateTaxTime);
			this.getFormHM().put("appealTaxTimeList",appealTaxTimeList);
			this.getFormHM().put("appealTaxTime",appealTaxTime);
			this.getFormHM().put("sendSalaryItemList",sendSalaryItemList);
			this.getFormHM().put("sendSalaryItem",sendSalaryItem);
			this.getFormHM().put("taxTypeList",taxTypeList);
			this.getFormHM().put("taxType",taxType);
			this.getFormHM().put("ratepayingDeclareList",ratepayingDeclareList);
			this.getFormHM().put("ratepayingDecalre",ratepayingDecalre);
			this.getFormHM().put("gz_module",gz_module);
			this.getFormHM().put("a01z0Flag",a01z0Flag);
			this.getFormHM().put("ctrlType", ctrlType);
			
			
			/**取得全部的计算公式*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			ArrayList list=gzbo.getSubmitTypeList();

            //加载默认审批项目 wangrd 2013-12-04
            String filterid=gzbo.getFiltersIds(salaryid);
            String  sp_default_filter_id=bo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL,"default_filterid");
            ArrayList spDefaultFilterList=bo.getFiterItemList(filterid);
            if("0".equals(flow_ctrl))
                sp_default_filter_id="";
            else if(sp_relation_id==null)
                sp_relation_id=""; 
            this.getFormHM().put("sp_default_filter_id", sp_default_filter_id);
            this.getFormHM().put("spDefaultFilterList", spDefaultFilterList); 
			
			String subNoShowUpdateFashion=gzbo.getLprogramAttri("no_show",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoShowUpdateFashion==null||subNoShowUpdateFashion.trim().length()==0)
				subNoShowUpdateFashion="0";
			
			String subNoPriv=gzbo.getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
				subNoPriv="0";			
			this.getFormHM().put("subNoPriv",subNoPriv);
			
			String allowEditSubdata=gzbo.getLprogramAttri("allow_edit_subdata",SalaryLProgramBo.CONFIRM_TYPE);
			if(allowEditSubdata==null||allowEditSubdata.trim().length()==0)
			    allowEditSubdata="0";			
			this.getFormHM().put("allowEditSubdata",allowEditSubdata);			
			
			this.getFormHM().put("subNoShowUpdateFashion",subNoShowUpdateFashion);
			this.getFormHM().put("fieldsetlist", list);
			String isUpdateSet=getIsUpdateSet(list);
			this.getFormHM().put("isUpdateSet", isUpdateSet);
			/**数据更新方式列表*/
		    ArrayList typelist=new ArrayList();  
		    CommonData vo=new CommonData("2",ResourceFactory.getProperty("label.gz.notchange"));
		    typelist.add(vo);
	        vo=new CommonData("0",ResourceFactory.getProperty("label.gz.update"));
	        typelist.add(vo);
	        vo=new CommonData("1",ResourceFactory.getProperty("label.gz.append"));
	        typelist.add(vo);
	        this.getFormHM().put("typelist",typelist);
			/**取按隶属部门进行所得税管理指标*/
	        TaxMxBo tmb = new TaxMxBo(this.getFrameconn());
	        String ls_dept=tmb.getDeptID();
	        String islsDept="0";
	        ArrayList lsDeptList=new ArrayList();
	        String lsDept="";
	        if("true".equalsIgnoreCase(ls_dept))
	        {
	        	islsDept="1";
	        	lsDeptList=bo.getSalarySetList("A","UM");
	        	lsDept=bo.getCtrlparam().getValue(SalaryCtrlParamBo.LS_DEPT);
	        	lsDept=lsDept==null?"":lsDept;
	        }
	        String field_priv=bo.getCtrlparam().getValue(SalaryCtrlParamBo.FIELD_PRIV);
	        if(field_priv==null|| "".equals(field_priv))
	        	field_priv="1";
	        String read_field=bo.getCtrlparam().getValue(SalaryCtrlParamBo.READ_FIELD);
	        if(read_field==null|| "".equals(read_field))
	        	read_field="0";
	        String collect_je_field=bo.getCtrlparam().getValue(SalaryCtrlParamBo.COLLECT_JE_FIELD);
	        if(collect_je_field==null|| "".equals(collect_je_field))
	        	collect_je_field="";
	        ArrayList number_field_list=new ArrayList();
	        ArrayList fieldlist=gzbo.getGzFieldList();
	        CommonData _vo=new CommonData("","");
	        number_field_list.add(_vo);
	        for(int i=0;i<fieldlist.size();i++)
			{
	        	FieldItem field=(FieldItem)fieldlist.get(i);
				if("a0000".equalsIgnoreCase(field.getItemid())|| "a00z1".equalsIgnoreCase(field.getItemid())|| "a00z3".equalsIgnoreCase(field.getItemid()))
					continue;
	        	if("N".equalsIgnoreCase(field.getItemtype()))
				{
					_vo=new CommonData(field.getItemid(),field.getItemdesc());
					number_field_list.add(_vo);
				}
			}
	        
	        
	        String  royalty_valid=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
	    	if(royalty_valid==null||royalty_valid.trim().length()==0)
				royalty_valid="0";
	        String royalty_setid="";   
	        String royalty_date=""; 
	        String royalty_period="";  
	        String royalty_relation_fields="";   
	      
	        ArrayList setList=new ArrayList(); 
			CommonData dataobj = new CommonData("", "");
			setList.add(dataobj);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select distinct fieldsetid from salaryset where salaryid="+salaryid+" and lower(fieldsetid) not in ('a00','a01')");
			while(this.frowset.next())
			{
				FieldSet avo=DataDictionary.getFieldSetVo(this.frowset.getString("fieldsetid").toLowerCase());
				if(avo!=null)
				{
					 if(avo.getFieldsetid().toUpperCase().charAt(0)=='B'||avo.getFieldsetid().toUpperCase().charAt(0)=='K')
						 continue;
					 dataobj = new CommonData(avo.getFieldsetid(),avo.getFieldsetid()+ ":" + avo.getCustomdesc());
					 setList.add(dataobj);
				}
			}
			/*
			ArrayList fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
			for (int i = 0; i < fieldsetlist.size(); i++)
			{
			    FieldSet fieldset = (FieldSet) fieldsetlist.get(i);
			    if (fieldset.getFieldsetid().equalsIgnoreCase("A00") || fieldset.getFieldsetid().equalsIgnoreCase("A01"))
			    	continue;
			    dataobj = new CommonData(fieldset.getFieldsetid(), fieldset.getFieldsetid() + ":" + fieldset.getCustomdesc());
			    setList.add(dataobj);
			}
			*/
	        ArrayList periodList=bo.getPeriodList();
	        ArrayList dateList=new ArrayList();
	        ArrayList fieldList=new ArrayList(); 
	        String strExpression="";
	         
	  //    if(royalty_valid.equals("1"))
	        {
	        	royalty_setid=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
	        	royalty_date=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"date");
	        	royalty_period=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"period");
	        	strExpression=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"strExpression");
	        	priecerate_valid=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"valid");//与计件薪资关联，赵旭光增加
		    	if(priecerate_valid==null||priecerate_valid.trim().length()==0)
		    		priecerate_valid="0";
	        	if(strExpression==null)
	        		strExpression="";
	        	royalty_relation_fields=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
	        	dateList=bo.getFieldList(1,royalty_setid,salaryid);
	        	fieldList=bo.getFieldList(2,royalty_setid,salaryid); 
	        }
	        
	        //取得计件薪资需要的数据，lis
	        String period = "";//计件薪资，周期
	        String priecerate_str = "";//计件薪资，月份
	        String relation_field = "";//计件薪资，指标
	        String priecerate_expression_str=""; //计件薪资
	        priecerate_expression_str=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"strExpression");
	        period=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"period");
	        priecerate_str=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"firstday");
	        relation_field=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"relation_field");
	        //end lis
	        this.getFormHM().put("priecerate_valid", priecerate_valid);
	        this.getFormHM().put("periodList", periodList);
	        this.getFormHM().put("dateList", dateList);
	        this.getFormHM().put("setList", setList);
	        this.getFormHM().put("fieldList", fieldList);
	        this.getFormHM().put("royalty_valid", royalty_valid);
	        this.getFormHM().put("royalty_setid", royalty_setid);
	        this.getFormHM().put("royalty_date", royalty_date);
	        this.getFormHM().put("royalty_period", royalty_period);
	        this.getFormHM().put("royalty_relation_fields", royalty_relation_fields);
	        this.getFormHM().put("strExpression",SafeCode.encode(strExpression));
	        
	        this.getFormHM().put("priecerate_expression_str",SafeCode.encode(priecerate_expression_str));
	        this.getFormHM().put("priecerate_zhouq1",period);
	        this.getFormHM().put("priecerate_str",priecerate_str);
	        this.getFormHM().put("priecerate_zhibiao",relation_field);
	        
	        this.getFormHM().put("royalty_valid",royalty_valid);
	        this.getFormHM().put("number_field_list",number_field_list);
	        this.getFormHM().put("collect_je_field",collect_je_field);
	        this.getFormHM().put("field_priv", field_priv);
	        this.getFormHM().put("islsDept", islsDept);
	        this.getFormHM().put("lsDept",lsDept);
	        this.getFormHM().put("lsDeptList",lsDeptList);
			this.getFormHM().put("read_field", read_field);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	

	
	private String getIsUpdateSet(ArrayList list)
	{
		String isUpdateSet="none";
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean dynabean=(LazyDynaBean)list.get(i);
			String type=(String)dynabean.get("type");
			if("0".equals(type))
				isUpdateSet="block";
		}
		return isUpdateSet;
	}
	//获得奖金项目指标的列表
	public ArrayList getBonusList(String salaryid) throws GeneralException
	{
	    ArrayList list  = new ArrayList();
	    CommonData temp = new CommonData("", "");
	    list.add(temp);
	    ContentDAO dao = new ContentDAO(this.frameconn);
	    String sql = "select * from salaryset where itemtype='N' and itemid!='A0000' and salaryid="+salaryid;
	    try
	    {
		RowSet rs = dao.search(sql);
		while (rs.next())
		{
		    String itemid = rs.getString("itemid");
		    String itemdesc = rs.getString("itemdesc");	

		    temp = new CommonData(itemid, itemdesc);
		    list.add(temp);  
		}
	    } catch (Exception e)
	    {
		e.printStackTrace();
		throw GeneralExceptionHandler.Handle(e);
	    }
	    return list;
	}
}
