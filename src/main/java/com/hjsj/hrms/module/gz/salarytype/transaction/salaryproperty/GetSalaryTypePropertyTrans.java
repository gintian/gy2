package com.hjsj.hrms.module.gz.salarytype.transaction.salaryproperty;

import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryPropertyBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.*;
/**
 * 项目名称 ：ehr
 * 类名称：GetSalaryTypePropertyTrans
 * 类描述：得到薪资属性
 * 创建人： lis
 * 创建时间：2015-12-8
 */
public class GetSalaryTypePropertyTrans  extends IBusiness {
	
	@Override
    public void execute() throws GeneralException {
		try
		{
			VersionControl vc = new VersionControl();
			Map propertyData = new HashMap();//存放薪资属性
			
			HashMap hm=(HashMap)this.getFormHM();
			String salaryid=(String)hm.get("salaryid"); 
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			
			String gz_module = hm.get("gz_module")==null?"0":(String)hm.get("gz_module"); //0 薪资 1保险
			
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,gz_module);
			SalaryPropertyBo bo=new SalaryPropertyBo(this.getFrameconn(),salaryid,Integer.parseInt(gz_module),this.getUserView());
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			
			/** 人员范围页面数据  strat  */
			ArrayList dbList=bo.getDbList();
			//1是简单查询，2是复杂查询
			String   personScope=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.COND_MODE,"flag");//
			//是否限制用户管理范围
			String priv_mode =bo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			if(StringUtils.isBlank(priv_mode))
			{
				priv_mode="1";//限制用户管理范围 默认勾选上，zhaoxg add 2016-12-19
			}
			String priv_mode_func = "0";
			if(("0".equals(gz_module)&&this.userView.hasTheFunction("324080801"))||("1".equals(gz_module)&&this.userView.hasTheFunction("325050801"))){//限制用户管理范围 有权限才能修改 0:没权限 1：有权限 zhaoxg add 2016-12-19
				priv_mode_func = "1";
			}
			String manager =bo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			String isShare="1";//薪资类别是否共享
			if(StringUtils.isBlank(manager))
			{
				isShare="0";
				manager="";
			}
			
			String    condStr=bo.getVo().getString("cond");//简单条件或复杂条件表达式
			if(condStr==null)
				condStr="";
			String    cexpr=bo.getVo().getString("cexpr");//逻辑关系
			if(cexpr==null)
				cexpr="";		
			//ArrayList<CommonData> selectedlist = bo.reParseExpression(cexpr,condStr);
			propertyData.put("dbList",dbList);
			propertyData.put("condStr",SafeCode.encode(condStr));
			propertyData.put("cexpr",SafeCode.encode(cexpr));
			//propertyData.put("selectedlist",selectedlist);
			propertyData.put("isShare",isShare);
			propertyData.put("managerid",StringUtils.isBlank(manager)?"":SafeCode.encode(PubFunc.encrypt(manager)));
			String fullname=bo.getFullName(manager);
			fullname=StringUtils.isBlank(fullname)?"":"("+fullname+")";
			propertyData.put("manager", manager+fullname);
			propertyData.put("priv_mode",priv_mode);
			propertyData.put("priv_mode_func",priv_mode_func);
			propertyData.put("personScope",personScope);
			/** 人员范围页面数据  end  */
			
			/** 计税参数页面数据  strat  */
			//计税时间指标
			String    calculateTaxTime=bo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_DATE_FIELD);
			//薪资项目中的日期型指标集合
			ArrayList calculateTaxTimeList=bo.getSalarySetList("D","");
			//报税时间指标
			String    appealTaxTime=bo.getCtrlparam().getValue(SalaryCtrlParamBo.DECLARE_TAX);
			//发薪标识指标
			String    sendSalaryItem=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PAY_FLAG);
			//薪资项目中的发薪标识指标集合
			ArrayList sendSalaryItemList=bo.getSalarySetList("A","42");
			//计税方式指标
			String    taxType=bo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_MODE);
			//薪资项目中的计税方式指标集合
			ArrayList taxTypeList=bo.getSalarySetList("A","46");
			//纳税项目指标
			ArrayList ratepayingDeclareList=bo.getSalarySetList("A","0");
			//薪资项目中的纳税项目指标集合
			String    ratepayingDecalre=bo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_DESC);
			//计税单位指标
			ArrayList taxUnitList=bo.getSalarySetList("A","UN");
			//薪资项目中的纳税项目指标集合
			String    taxUnit=bo.getCtrlparam().getValue(SalaryCtrlParamBo.TAX_UNIT);

			
			//薪资项目中的入职时间指标
			String    hiredate=bo.getCtrlparam().getValue(SalaryCtrlParamBo.HIRE_DATE);
			ArrayList hiredateList=bo.getSalarySetList("D","");
			//薪资项目中的是否残疾人指标
			String    disability=bo.getCtrlparam().getValue(SalaryCtrlParamBo.DISABILITY);
			ArrayList sf_List=bo.getSalarySetList("A","45");
			//薪资项目中的减征比例指标
			String    percent=bo.getCtrlparam().getValue(SalaryCtrlParamBo.DISABILITY,"percent");
			
			
			 /** 取按隶属部门进行所得税管理指标*/
	        TaxMxBo tmb = new TaxMxBo(this.getFrameconn());
	        String ls_dept=tmb.getDeptID();
	        String islsDept="0";
	        //归属部门
	        ArrayList lsDeptList=new ArrayList();
	        String lsDept="";
	        if("true".equalsIgnoreCase(ls_dept))
	        {
	        	islsDept="1";
	        	lsDeptList=bo.getSalarySetList("A","UM,UN");
	        	lsDept=bo.getCtrlparam().getValue(SalaryCtrlParamBo.LS_DEPT);
	        	lsDept=lsDept==null?"":lsDept;
	        }
	        
	        propertyData.put("hiredate",hiredate);
	        propertyData.put("hiredateList",hiredateList);
	        propertyData.put("disability",disability);
	        propertyData.put("sf_List",sf_List);
	        propertyData.put("percent",percent);
	        
	        propertyData.put("calculateTaxTime",calculateTaxTime);
	        propertyData.put("calculateTaxTimeList",calculateTaxTimeList);
	        propertyData.put("appealTaxTime",appealTaxTime);
	        propertyData.put("sendSalaryItem",sendSalaryItem);
	        propertyData.put("sendSalaryItemList",sendSalaryItemList);
	        propertyData.put("taxType",taxType);
	        propertyData.put("taxTypeList",taxTypeList);
	        propertyData.put("ratepayingDecalre",ratepayingDecalre);
	        propertyData.put("ratepayingDeclareList",ratepayingDeclareList);
	        propertyData.put("islsDept", islsDept);
	        propertyData.put("lsDept",lsDept);
	        propertyData.put("lsDeptList",lsDeptList);
	        propertyData.put("taxUnit",taxUnit);
	        propertyData.put("taxUnitList",taxUnitList);
			/** 计税参数页面数据  end */
	        
	        /** 审批方式页面数据  start */
	        String rightvalue=bo.getCtrlparam().getValue(SalaryCtrlParamBo.COMPARE_FIELD);
     		String addrightvalue = bo.getCtrlparam().getValue(SalaryCtrlParamBo.ADD_MAN_FIELD);
     		String delrightvalue = bo.getCtrlparam().getValue(SalaryCtrlParamBo.DEL_MAN_FIELD);
     		propertyData.put("leftlist",bo.leftList(salaryid,rightvalue));
     		propertyData.put("rightlist",bo.rightList(salaryid,rightvalue));
     		propertyData.put("addleftlist",bo.leftList1(salaryid,addrightvalue));
     		propertyData.put("addrightlist",bo.rightList(salaryid,addrightvalue));
     		propertyData.put("delleftlist",bo.leftList1(salaryid,delrightvalue));
     		propertyData.put("delrightlist",bo.rightList(salaryid,delrightvalue));
    		
	        //是否需要审批
	        String  flow_ctrl=bo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL,"flag");
	        String  reject_mode=bo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL,"reject_mode");
			if(reject_mode==null||reject_mode.trim().length()==0)
				reject_mode="1";  //驳回方式  1:逐级驳回  2：驳回到发起人
			
			//70锁版本程序由于审批界面没有汇总表，不需要设置汇总权限，汇总指标总能看见
			String collectPoint = "0";
			collectPoint = bo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD, "collect_field");
			ArrayList collectList = collectList(salaryid);
			propertyData.put("collectPoint",collectPoint);
			propertyData.put("collectList",collectList);
			
			//审批关系
			String  sp_relation_id=bo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL,"sp_relation_id");
			//薪资项目中审批关系指标
			ArrayList spRelationList=bo.getSpRelationList();
			if("0".equals(flow_ctrl))
				sp_relation_id="";
			else if(sp_relation_id==null)
				sp_relation_id=""; 
			
			//加载默认审批项目 wangrd 2013-12-04
			String filterid=bo.getFiltersIds(salaryid);
			String  sp_default_filter_id=bo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL,"default_filterid");
            ArrayList spDefaultFilterList=bo.getFiterItemList(filterid);
            if("0".equals(flow_ctrl))
                sp_default_filter_id="";
            else if(sp_relation_id==null)
                sp_relation_id=""; 
            
			//归属单位指标
            String orgid = bo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid");
			ArrayList orgList = bo.getOrgOrDeptListFromSalaryset("UN",salaryid);
			//归属部门指标
			String deptid = bo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
			ArrayList deptList = bo.getOrgOrDeptListFromSalaryset("UM",salaryid);
			
			//短信通知
			String smsNotice=bo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"sms");
			//邮件通知
			String mailNotice=bo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"mail");
			
			//模板id
			String mailTemplateId=bo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE);
			String nmodule=(String)hm.get("nmodule");
			//模板数据集合
			ArrayList mailTemplateList=bo.getEmailTemplateListByNmodule(nmodule,1);
			
			propertyData.put("rightvalue",rightvalue);
			propertyData.put("addrightvalue",addrightvalue);
			propertyData.put("delrightvalue",delrightvalue);
			propertyData.put("flow_ctrl",flow_ctrl);
			propertyData.put("gz_module",gz_module);
			propertyData.put("reject_mode",reject_mode);
			propertyData.put("sp_relation_id", sp_relation_id);
			propertyData.put("spRelationList", spRelationList);
			propertyData.put("sp_default_filter_id", sp_default_filter_id);
			propertyData.put("spDefaultFilterList", spDefaultFilterList); 
			propertyData.put("orgid",orgid);
			propertyData.put("orgList",orgList);
			propertyData.put("deptid",deptid);
			propertyData.put("deptList",deptList);
			propertyData.put("smsNotice", smsNotice);
			propertyData.put("mailNotice", mailNotice);
			propertyData.put("mailTemplateId", mailTemplateId);
			propertyData.put("mailTemplateList", mailTemplateList);
			
	        /** 审批方式页面数据  end */
			
			/** 数据更新方式页面  start */
			String lprogram = gzbo.getTemplatevo().getString("lprogram");
			SalaryLProgramBo lProgramBo = new SalaryLProgramBo(lprogram);
			String confirm_type = lProgramBo.getValue(SalaryLProgramBo.CONFIRM_TYPE);
			String[] updateStr = confirm_type.split(";");
			StringBuffer buf = new StringBuffer("");
			for(int i=0;i<updateStr.length;i++){
				if(updateStr[i].split("`").length == 1)
					buf.append(updateStr[i]+";");
					
			}
	        ArrayList list=bo.getSubmitTypeLists(gzbo);//子集数据列表
	        String isUpdateSet=getIsUpdateSet(list);//是否是更新当前子集
	        String subNoShowUpdateFashion=gzbo.getLprogramAttri("no_show",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoShowUpdateFashion==null||subNoShowUpdateFashion.trim().length()==0)
				subNoShowUpdateFashion="0";
			
			String subNoPriv=gzbo.getLprogramAttri("subNoPriv",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoPriv==null||subNoPriv.trim().length()==0)
				subNoPriv="0";			
			
			String allowEditSubdata=gzbo.getLprogramAttri("allow_edit_subdata",SalaryLProgramBo.CONFIRM_TYPE);
			if(allowEditSubdata==null||allowEditSubdata.trim().length()==0)
			    allowEditSubdata="0";	
			
			propertyData.put("buf", buf.toString());
			propertyData.put("fieldsetlist", list);
			propertyData.put("isUpdateSet", isUpdateSet);
			propertyData.put("subNoShowUpdateFashion",subNoShowUpdateFashion);
			propertyData.put("subNoPriv",subNoPriv);
			propertyData.put("allowEditSubdata",allowEditSubdata);			
	        
	        /** 数据更新方式页面  end */
	        
			/** 其他参数页面  start */
			
			String moneyType = String.valueOf(bo.getVo().getString("nmoneyid"));//币种
			ArrayList moneyTypeList = bo.getMoneyStyleList();//币种列表数据
			
			String amount_ctrl=bo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");//总额控制
			if(amount_ctrl==null|| "".equals(amount_ctrl))
			{
				amount_ctrl="0";
			}
			
			//控制薪资发放
			String amount_ctrl_ff=bo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_ff");
			//控制薪资审批
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
			
			//1:强制控制,2:预警提示
			String ctrl_type = bo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"ctrl_type");
			if("0".equals(amount_ctrl))
			{
				ctrl_type="1";
			}else
			{
				if(StringUtils.isBlank(ctrl_type))
					ctrl_type="1";
			}
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
			
			//停发标识
			String a01z0Flag=bo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");
			if(a01z0Flag==null)
				a01z0Flag="0";
			
			if(vc.searchFunctionId("3240214")){
				//奖金项目
				String  bonusItemFld = bo.getCtrlparam().getValue(SalaryCtrlParamBo.BONUS);
				propertyData.put("bonusItemFld", bonusItemFld);
				propertyData.put("bonusItemFldList", this.getBonusList(salaryid));
			}
			
			//非写指标参与计算
			String field_priv = bo.getCtrlparam().getValue(SalaryCtrlParamBo.FIELD_PRIV);
	        if(field_priv==null|| "".equals(field_priv))
	        	field_priv="1";
	        //读权限指标允许重新导入
	        String read_field=bo.getCtrlparam().getValue(SalaryCtrlParamBo.READ_FIELD);
	        if(read_field==null|| "".equals(read_field))
	        	read_field="0";
	        
	        if(vc.searchFunctionId("32416") || vc.searchFunctionId("32516")){
	        	//汇总审批发放金额指标
	        	String collect_je_field=bo.getCtrlparam().getValue(SalaryCtrlParamBo.COLLECT_JE_FIELD);
	        	if(collect_je_field==null|| "".equals(collect_je_field))
	        		collect_je_field="";
	        	ArrayList number_field_list=new ArrayList();
		        ArrayList<FieldItem> fieldlist=gzbo.getSalaryItemList(null, salaryid, 2);
		        CommonData _vo=new CommonData("blank", ResourceFactory.getProperty("label.select.dot"));//请选择...
		        number_field_list.add(_vo);
		        for(int i=0;i<fieldlist.size();i++)
				{
		        	FieldItem field = fieldlist.get(i);
					if("a0000".equalsIgnoreCase(field.getItemid())|| "a00z1".equalsIgnoreCase(field.getItemid())|| "a00z3".equalsIgnoreCase(field.getItemid()))
						continue;
		        	if("N".equalsIgnoreCase(field.getItemtype()))
					{
						_vo=new CommonData(field.getItemid(),field.getItemdesc());
						number_field_list.add(_vo);
					}
				}
	        	propertyData.put("collect_je_field",collect_je_field);
	        	propertyData.put("number_field_list",number_field_list);
	        }
	        
	        if("0".equals(gz_module)){
	        	if(vc.searchFunctionId("324080802")){
	        		 //提成薪资
	    	        String  royalty_valid=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
	    	    	if(royalty_valid==null||royalty_valid.trim().length()==0)
	    				royalty_valid="0";
	    	    	propertyData.put("royalty_valid",royalty_valid);
	        	}
	        	if(vc.searchFunctionId("32421")){
	        		//计件薪资，赵旭光加
	    	    	String priecerate_valid = bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"valid");//与计件薪资关联，赵旭光增加
	    	    	if(priecerate_valid==null||priecerate_valid.trim().length()==0)
	    	    		priecerate_valid="0";
	    	    	propertyData.put("priecerate_valid", priecerate_valid);
	        	}
	        }
	        
			propertyData.put("moneyType",moneyType);
			propertyData.put("moneyTypeList",moneyTypeList);
			propertyData.put("amount_ctrl",amount_ctrl);
			propertyData.put("amount_ctrl_ff",amount_ctrl_ff);
			propertyData.put("amount_ctrl_sp",amount_ctrl_sp);
			propertyData.put("ctrl_type", ctrl_type);
			propertyData.put("verify_ctrl",verify_ctrl);
			propertyData.put("verify_ctrl_ff",verify_ctrl_ff);
			propertyData.put("verify_ctrl_sp",verify_ctrl_sp);
			propertyData.put("a01z0Flag",a01z0Flag);
			propertyData.put("field_priv", field_priv);
			propertyData.put("read_field", read_field);
			
			/** 其他参数页面  end */
			
			 /** 计件薪资 start  */
	        String royalty_setid="";  //提成数据子集 id
	        String royalty_date=""; //计划日期指标
	        String royalty_period="1";  //周期,默认是月
	        String royalty_relation_fields="";   //关联指标
	      
	        ArrayList setList=new ArrayList(); //提成数据子集 
			CommonData dataobj = new CommonData("blank", ResourceFactory.getProperty("label.select.dot"));//请选择...
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
	        ArrayList dateList=new ArrayList();//日期列表
	        ArrayList fieldList=new ArrayList(); //指标列表
	        String strExpression="";//数据范围条件公式表达式
        	royalty_setid=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"setid");
        	royalty_date=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"date");
        	royalty_period=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"period");
        	strExpression=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"strExpression");
        	
        	if(strExpression==null)
        		strExpression="";
        	royalty_relation_fields=bo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
        	dateList=bo.getFieldList(1,royalty_setid,salaryid);
        	fieldList=bo.getFieldList(2,royalty_setid,salaryid); 
        	propertyData.put("royalty_setid", royalty_setid);
        	propertyData.put("setList", setList);
        	propertyData.put("strExpression",SafeCode.encode(strExpression));
	        propertyData.put("dateList", dateList);
	        propertyData.put("royalty_date", royalty_date);
	        propertyData.put("royalty_period", royalty_period);
	        propertyData.put("royalty_relation_fields", royalty_relation_fields);
	        propertyData.put("fieldList", fieldList);
	        /** 提成薪资 end  */
	        
	        /** 计件薪资 start  */
	        
	        //取得计件薪资需要的数据，lis
	        String priecerate_period = "";//计件薪资，周期
	        String priecerate_firstday = "1";//计件薪资，月份
	        String relation_field = "";//计件薪资，指标
	        String priecerate_expression_str=""; //计件薪资
	        priecerate_expression_str=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"strExpression");
	        priecerate_period=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"period");
	        priecerate_firstday=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"firstday");
	        relation_field=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"relation_field");
	        //end lis
	        
	        ArrayList priecerateList = new ArrayList();//计件薪资引入指标
			ArrayList priecerateFieldList = new ArrayList();//计件指标
			ArrayList salarySetFieldList = new ArrayList();//薪资项目
			
			priecerateList = getPriecerateField(relation_field, salaryid, dao);

			ArrayList usedFieldSet=DataDictionary.getFieldList("s05",Constant.USED_FIELD_SET);
			String excludeStr=",Nbase,A0100,I9999,S0100,".toUpperCase();
			for (int i=0;i<usedFieldSet.size();i++) {
				FieldItem fielditem = (FieldItem) usedFieldSet.get(i);
				if ("0".equals(fielditem.getState())) continue;
				if (!"N".equals(fielditem.getItemtype())) continue;
				if (excludeStr.indexOf(","+fielditem.getItemid().toUpperCase()+",")>-1) 
				{continue;}
				String str = fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc();
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("priecerateField", str);
				bean.set("dataValue", str);
				priecerateFieldList.add(bean);
			}
			
			SalaryTemplateBo templateBo = new SalaryTemplateBo(this.getFrameconn(), this.userView);
			ArrayList dylist = new ArrayList();
			dylist = templateBo.getSalaryItemList("itemtype = 'N'", salaryid, 1);
			
			String excludeStr1=",Nbase,A0100,A0000,A01Z2,A00Z3,A00Z0,A00Z1,B0110,E0122,A0101,A0120,".toUpperCase();
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("itemid").toString();
				String itemdesc = dynabean.get("itemdesc").toString();
				if (excludeStr1.indexOf(","+itemid.toUpperCase()+",")>-1) {continue;}
				String str2 = itemid+":"+itemdesc;
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("salartSetField", str2);
				bean.set("dataValue", str2);
				salarySetFieldList.add(bean);
			}
			
			propertyData.put("priecerateList", priecerateList);
			propertyData.put("priecerateFieldList", priecerateFieldList);
			propertyData.put("salarySetFieldList", salarySetFieldList);
			propertyData.put("priecerate_expression_str",SafeCode.encode(priecerate_expression_str));
	        propertyData.put("priecerate_period",priecerate_period);
	        propertyData.put("priecerate_firstday",priecerate_firstday);
	        propertyData.put("priecerateFields",relation_field);
	        //是否有应用机构
	        List elementList = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FILLING_AGENCY, SalaryCtrlParamBo.FILLING_AGENCYS);
	        propertyData.put("haveAppliOrg",elementList.size() > 0?true:false);
		        
			 /** 计件薪资 end  */
	        this.getFormHM().put("propertyData",propertyData);
	        
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @author lis
	 * @Description: 获得计件薪资指标
	 * @date 2015-12-23
	 * @param relation_field
	 * @param salaryid
	 * @param dao
	 * @return
	 * @throws SQLException
	 * @throws GeneralException
	 */
	private ArrayList getPriecerateField(String relation_field,String salaryid,ContentDAO dao) throws SQLException, GeneralException{
		ArrayList zblist = new ArrayList();
		try {
			if(StringUtils.isNotBlank(relation_field)){
	        	String[] zb = relation_field.split(",");
	        	String priecerateField = "";
				String salartSetField = "";
				RowSet rs1 = null;
				RowSet rs2 = null;
	        	for(int i=0;i<zb.length;i++){
	        		/* 安全问题：薪资类别-参数设置-属性-其他参数-计件薪资-设置-保存不上 xiaoyun 2014-10-15 start */
	        		String[] zb1 = PubFunc.keyWord_reback(zb[i]).split("=");
	        		/* 安全问题：薪资类别-参数设置-属性-其他参数-计件薪资-设置-保存不上 xiaoyun 2014-10-15 end */
	        		String sqll = "select itemid,itemdesc from t_hr_busifield where upper(fieldsetid) ='S05'  and itemid ='"+zb1[0]+"'";
	        		rs1 = dao.search(sqll);
	        		while(rs1.next()){
	        			String itemid1 = rs1.getString("itemid");
	        			String itemdesc1 = rs1.getString("itemdesc");
	        			priecerateField = itemid1 +":"+ itemdesc1;
	        		}
	        		if(zb1.length > 1){
	        			String tsql = "select itemid,itemdesc from salaryset where salaryid ='"+salaryid+"' and itemid ='"+zb1[1]+"'";
	        			rs2 = dao.search(tsql);
	        			while(rs2.next()){
	        				String itemid2 = rs2.getString("itemid");
	        				String itemdesc2 = rs2.getString("itemdesc");
	        				salartSetField = itemid2 +":"+ itemdesc2;
	        			}
	        		}
	        		LazyDynaBean bean = new LazyDynaBean();
	        		bean.set("priecerateField", priecerateField);
	        		bean.set("salartSetField", salartSetField);
					zblist.add(bean);
	        	}
	        	}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return zblist;
	}
	/**
	 * 获取汇总指标
	 * @param salaryid
	 * zhaoxg add 2014-11-13 搜房网需求
	 * @return
	 */
	public ArrayList collectList(String salaryid){
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList retlist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where salaryid=");
		sql.append(salaryid);
		sql.append(" and CODESETID <> '0' ");
		sql.append("group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			retlist.add(new CommonData("","(空)"));
			CommonData obj1=new CommonData("UNUM",ResourceFactory.getProperty("gz_new.gz_unitAndPar"));//单位&部门
			retlist.add(obj1);
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return retlist;
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
	    CommonData temp = new CommonData("blank", ResourceFactory.getProperty("label.select.dot"));//请选择...
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
