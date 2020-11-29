package com.hjsj.hrms.module.gz.salarytype.businessobject;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


/**
 * 操作工资属性类别面板对象
 *<p>Title:SalaryPropertyBo.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 29, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class SalaryPropertyBo {
	private Connection conn=null;
	/**薪资类别
	 *=0 薪资 
	 *=1 保险
	 */
	private int gz_type=0;
	private String salaryid="";
	/**薪资控制参数*/
	private SalaryCtrlParamBo ctrlparam=null;
	private RecordVo vo=null;
	private String unit_type="1"; //单位性质 =1,国家机关 =2,事业单位	=3,企业单位 =4,军队使用	=5,其    它
	private UserView userView=null;
	
	public SalaryPropertyBo(Connection con )
	{
		this.conn=con;
	}
	
	public SalaryPropertyBo(Connection con,String salaryid,int gz_type,UserView userView) throws GeneralException
	{
		try
		{
			this.conn=con;
			this.salaryid=salaryid;
			this.gz_type=gz_type;
			ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(this.salaryid));
			vo=getRecordVo();
			this.userView=userView;
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			if(sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type")!=null&&sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type").length()>0)
				this.unit_type=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @author lis
	 * @Description: 获取不同模块的通知模板
	 * @date 2016-1-4
	 * @param nmodule
	 * @param type
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getEmailTemplateListByNmodule(String nmodule,int type) throws GeneralException
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			String sql = "select id,name from email_name where nmodule=? order by id";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql,Arrays.asList(nmodule));
			list.add(new CommonData(" ",ResourceFactory.getProperty("label.select.dot")));
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("id"),rs.getString("name")));
			}
			if(type==1)
			{
				list.add(new CommonData("createnew",ResourceFactory.getProperty("label.gz.new")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return list;
	}
	
	/**
	 * 取得薪资项目指标列表
	 * @param type  D：日期  A:字符型  N:数字型
	 * @param codesetid 代码id
	 * @return
	 */
	public ArrayList getSalarySetList(String type,String codesetid)throws GeneralException
	{
		ArrayList list=new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select * from salaryset where salaryid=? and itemtype=? ";
			if("A".equalsIgnoreCase(type)&&codesetid.length()>0) {
				if(codesetid.indexOf(",") != -1) {
					String[] codesetid_ = codesetid.split(",");
					sql+=" and ( 1=2 ";
					for(int i = 0; i < codesetid_.length; i++) {
						sql+=" or codesetid='"+codesetid_[i]+"'";
					}
					sql+=")";
				}else {
					sql+=" and codesetid='"+codesetid+"'";
				}
			}
			
			rowSet=dao.search(sql,Arrays.asList(this.salaryid,type));
			list.add(new CommonData("","(空)"));
			while(rowSet.next())
			{
				CommonData data=new CommonData(rowSet.getString("itemid"),rowSet.getString("itemdesc"));
				list.add(data);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rowSet);
		}
		return list;
	}

	/**
	 * @author lis
	 * @Description: 获取过滤薪资项目
	 * @date 2015-12-22
	 * @param salaryid 薪资类别id
	 * @return
	 * @throws GeneralException 
	 */
	public String getFiltersIds(String salaryid) throws GeneralException
	{
		String ret_str = "";
		try
		{
			BankDiskSetBo bo = new BankDiskSetBo(this.conn);
			String xml=bo.getCondXML(salaryid);
			SalaryLProgramBo sLPBo = new SalaryLProgramBo(xml);
			ret_str = sLPBo.getValue(SalaryLProgramBo.FILTERS);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return ret_str;
	}
	
    /**
     * @throws GeneralException    
     * @Title: getFiterItemList   
     * @Description:  获取共享过滤项目  
     * @param @param fiterItemIds  过滤薪资指标id 
     * @param @return 
     * @return ArrayList    
     * @author wangrd  
     * @throws   
    */
    public ArrayList getFiterItemList(String fiterItemIds) throws GeneralException
    { 
    	ArrayList list=new ArrayList();
    	RowSet rset = null;
        try
        {
            StringBuffer buf=new StringBuffer();
            buf.append("select id,chz,cfldname,scope,username from gzitem_filter where ");
            if(fiterItemIds==null|| "".equals(fiterItemIds))
            {
                buf.append(" 1=2 ");
            }
            else
            {
                buf.append("id in (");
                buf.append(fiterItemIds);
                buf.append(")");
            }
            buf.append(" and scope =0 order by norder ");//共享的
            ContentDAO dao=new ContentDAO(this.conn);
            rset=dao.search(buf.toString());
            list.add(new CommonData("",""));
            while(rset.next()){         
                CommonData data=new CommonData(rset.getString("id"),rset.getString("chz"));
                list.add(data);
            
            }  
            rset.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
        	PubFunc.closeResource(rset);
        }
        return list;
        
    }
	
    /**
	 * 求薪资数据各子集提交方式,数据提交至档案库USRAXX
	 * @return
	 * @author sli
	 * @date 2015-12-22
	 * @throws GeneralException
	 */
	public ArrayList getSubmitTypeLists(SalaryTemplateBo gzbo)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			
			StringBuffer buf=new StringBuffer();
			ArrayList<FieldItem> fieldItems = gzbo.getSalaryItemList(null, salaryid+"", 2);
			for(int i=0;i<fieldItems.size();i++)
			{
				FieldItem fieldItem = fieldItems.get(i);
				String setid = fieldItem.getFieldsetid();
				if(setid.charAt(0)!='A')
					continue;
				if("A00".equalsIgnoreCase(setid))
					continue;
				if(buf.indexOf(setid)==-1)
				{
					buf.append(setid);
					buf.append(",");
				}
			}//for i loop end.
			SalaryLProgramBo lpbo=new SalaryLProgramBo(gzbo.getTemplatevo().getString("lprogram"));
			HashMap map=lpbo.getSubmitMap();
			String[] seta=StringUtils.split(buf.toString(),",");
			
			for(int i=0;i<seta.length;i++)
			{
				String setid=seta[i];
				FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
				if(fieldset==null)
					continue;
				if("0".equalsIgnoreCase(fieldset.getUseflag()))
					continue;
				LazyDynaBean dynabean=new LazyDynaBean();
				dynabean.set("setid", setid);
				dynabean.set("name", fieldset.getCustomdesc());
				String type=(String)map.get(setid);
				if(type==null||type.length()==0)
				{
					if("0".equals(fieldset.getChangeflag()))
					{
						dynabean.set("type", "2");
					}
					else
						dynabean.set("type", "1");
				}
				else
					dynabean.set("type", type);
				list.add(dynabean);
			}//for i loop end.
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return list;		
	}
	
	/**
	 * 取得审批关系列表
	 * @return
	 */
	public ArrayList getSpRelationList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			DbWizard dbw = new DbWizard(this.conn);
			if(dbw.isExistTable("t_wf_relation",false))
			{
				ContentDAO dao=new ContentDAO(this.conn);
				String sql="select * from t_wf_relation where validflag=1 and actor_type='4'";
				
				RowSet rowSet=dao.search(sql);
				list.add(new CommonData("","(空)"));
				while(rowSet.next())
				{
					CommonData data=new CommonData(rowSet.getString("relation_id"),rowSet.getString("cname"));
					list.add(data);
				}
				if(rowSet!=null)
					rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	/**
	 * @author lis
	 * @Description: 保存薪资属性
	 * @date 2016-1-4
	 * @param form 薪资属性数据集合
	 * @param gz_module 0是薪资
	 * @throws GeneralException
	 */
	public void saveStandardProperty(MorphDynaBean form,String gz_module,ArrayList list)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			
			/** 保存适用范围   start  */
			StringBuffer cbase=new StringBuffer("");
			ArrayList dbValue = new ArrayList();
			//人员库数据
			if(form.get("dbValue") instanceof ArrayList){
				dbValue = (ArrayList)form.get("dbValue");
				for(int i=0;i<dbValue.size();i++)
				{
					if(!"-1".equals((String)dbValue.get(i)))
						cbase.append(dbValue.get(i) + ",");
				}
			}else{
				cbase.append((String)form.get("dbValue") + ",");
			}
			String personScope = (String)form.get("personScope");
			String condStr=(String)form.get("condStr");//简单或复杂条件表达式
			String cexpr=(String)form.get("cexpr");//简单条件关系，1*2+3
			condStr=SafeCode.decode(condStr);
			if("1".equals(personScope)){//复杂条件时 将`还原为\n
				condStr=condStr.replaceAll("!","\r");
				condStr=condStr.replaceAll("`","\n");
			}
			//由于前台对加号做了特殊处理，这里转回来
			if(cexpr.contains("convert"))
				cexpr = cexpr.replaceAll("convert", "+");
			condStr=PubFunc.keyWord_reback(condStr);
			cexpr=PubFunc.keyWord_reback(SafeCode.decode(cexpr));
			this.ctrlparam.setValue(SalaryCtrlParamBo.COND_MODE,"flag",personScope);
			
			String moneyType = (String)form.get("moneyType");
			StringBuffer sql=new StringBuffer("update salarytemplate set cbase=?");
			if(moneyType.length()>0)
				sql.append(",nmoneyid=?");
			else 
				sql.append(",nmoneyid=null");//当货币种类为空时，sli
			sql.append(",cond=?,cexpr=? where salaryid=?");
			ArrayList paramList = new ArrayList();
			paramList.add(cbase.toString());
			if(moneyType.length()>0)
			{
				double dl=Double.parseDouble(moneyType);//修正moneyType不为整数
				paramList.add((int)dl);
			}
			paramList.add(condStr);
			paramList.add(cexpr);
			paramList.add(Integer.parseInt(salaryid));
			dao.update(sql.toString(),paramList);
			
			this.ctrlparam.setValue(SalaryCtrlParamBo.PRIV_MODE, "flag", (String)form.get("priv_mode"));
			String manager = (String)form.get("manager");
			manager=PubFunc.decrypt(SafeCode.decode(manager));
			this.ctrlparam.setValue(SalaryCtrlParamBo.SHARE_SET, "user", manager);
			if(StringUtils.isNotBlank(manager))
			{
				dao.update("update salarytemplate set nflag=1 where salaryid=?",Arrays.asList(this.salaryid));
				
				int res_type = 12;//12:薪资，18：保险
		        if ("1".equals(gz_module)) {
		            res_type = 18;
		        }
		        ApplicationOrgBo applicationOrgBo = new ApplicationOrgBo(this.conn, this.salaryid, this.userView);
	        	//自动保存资源权限
		        applicationOrgBo.saveResource(manager, "0", res_type);
			}
			else
			{
				dao.update("update salarytemplate set nflag=0 where salaryid=?",Arrays.asList(this.salaryid));
			}
			/** 保存适用范围     end  */
			
			/** 保存计税参数     start  */
			
			this.ctrlparam.setValue(SalaryCtrlParamBo.TAX_DATE_FIELD,(String)form.get("calculateTaxTime"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.DECLARE_TAX,(String)form.get("appealTaxTime"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.PAY_FLAG,(String)form.get("sendSalaryItem"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.TAX_MODE,(String)form.get("taxType"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.TAX_DESC,(String)form.get("ratepayingDecalre"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.LS_DEPT, (String)form.get("lsDept"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.TAX_UNIT, (String)form.get("taxUnit"));
			
			this.ctrlparam.setValue(SalaryCtrlParamBo.HIRE_DATE, (String)form.get("hiredate"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.DISABILITY, (String)form.get("disability"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.DISABILITY, "percent", (String)form.get("percent"));
			
			/** 保存计税参数     end  */
			
			/** 保存审批方式    start  */
			String rightvalue = (String)form.get("rightvalue");
	    	String addrightvalue = (String)form.get("addrightvalue");
	    	String delrightvalue = (String)form.get("delrightvalue");
	    	this.ctrlparam.setValue(SalaryCtrlParamBo.COMPARE_FIELD, rightvalue);
	    	this.ctrlparam.setValue(SalaryCtrlParamBo.ADD_MAN_FIELD, addrightvalue);
	    	this.ctrlparam.setValue(SalaryCtrlParamBo.DEL_MAN_FIELD, delrightvalue);
	    	
	    	//是否需要审批
	    	String flow_ctrl = (String)form.get("flow_ctrl");
	    	this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"flag",flow_ctrl);
	    	this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"flag",flow_ctrl);
			
			if("1".equals(flow_ctrl))//需要审批
			{
				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"reject_mode",(String)form.get("reject_mode"));
				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"sp_relation_id",(String)form.get("sp_relation_id"));
//				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"default_filterid",(String)form.get("sp_default_filter_id"));
			}
			else
			{
				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"reject_mode","1");
				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"sp_relation_id","");				
//				this.ctrlparam.setValue(SalaryCtrlParamBo.FLOW_CTRL,"sp_default_filter_id","");	
				String str = "update t_hr_pendingtask set Pending_status='4' where (Pending_status='0' or Pending_status='3') and ext_flag like '%_"+salaryid+"'";//设置成不需要审批，把当前薪资类别的待办全置成无效 zhaoxg add 2015-4-13
				dao.update(str);
			}
			//归属单位
			this.ctrlparam.setValue(SalaryCtrlParamBo.SUM_FIELD, "orgid",(String)form.get("orgid"));
			//归属部门
			this.ctrlparam.setValue(SalaryCtrlParamBo.SUM_FIELD, "deptid",(String)form.get("deptid"));
			//汇总指标
			this.ctrlparam.setValue(SalaryCtrlParamBo.SUM_FIELD, "collect_field", (String)form.get("collectPoint"));
			//模板id
			this.ctrlparam.setValue(SalaryCtrlParamBo.NOTE, (String)form.get("mailTemplateId"));
			//短信通知
			String smsNotice = (String)form.get("smsNotice");
			this.ctrlparam.setValue(SalaryCtrlParamBo.NOTE,"sms", smsNotice);
			//邮件通知
			String mailNotice = (String)form.get("mailNotice");
			this.ctrlparam.setValue(SalaryCtrlParamBo.NOTE, "mail",mailNotice);
			
	    	/** 保存审批方式     end  */
			
			/** 保存其他参数     start  */
			
			//根据gz_module判断是薪资还是保险，0:薪资，1:保险，lis添加
			if("0".equals(gz_module)){
				String amount_ctrl_ff = (String)form.get("amount_ctrl_ff");
				String amount_ctrl_sp = (String)form.get("amount_ctrl_sp");
				String amount_ctrl = (String)form.get("amount_ctrl");
				String ctrl_type = (String)form.get("ctrl_type");
				String royalty_valid = (String)form.get("royalty_valid");
				String priecerate_valid = (String)form.get("priecerate_valid");
				this.ctrlparam.setValue(SalaryCtrlParamBo.AMOUNT_CTRL, "amount_ctrl_ff",amount_ctrl_ff);
				this.ctrlparam.setValue(SalaryCtrlParamBo.AMOUNT_CTRL, "amount_ctrl_sp",amount_ctrl_sp);
				//---------------------------如果设置了总额里面的归属部门和归属单位，那么这里判断下薪资类别里面是否含有这俩薪资项，防止总额校验那报错--------------------
				if("1".equals(amount_ctrl)){
					GzAmountXMLBo gzAmountXMLBo=new GzAmountXMLBo(this.conn,1);
					HashMap gzXmlMap=gzAmountXMLBo.getValuesMap();
					if(gzXmlMap!=null)
					{
						HashMap um_un=(HashMap)gzXmlMap.get("hs");
						//判断总额参数设置是否设置是否进行部门总额控制选项0：是，1：否 sunjian 2017-06-02
						String ctrl_type1=(String)gzXmlMap.get("ctrl_type");
						if(um_un!=null)
						{
							String belongUN=(String)um_un.get("orgid");
							String belongUM=(String)um_un.get("deptid");
							if(belongUN.length()>0||belongUM.length()>0){
								StringBuffer sb = new StringBuffer();
								if(belongUN.length()>0){
									sb.append(",'");
									sb.append(belongUN);
									sb.append("'");
								}
								if(belongUM.length()>0){
									sb.append(",'");
									sb.append(belongUM);
									sb.append("'");
								}
								String str = "select itemid from salaryset where salaryid="+this.salaryid+" and itemid in ("+sb.toString().substring(1)+")";
								RowSet rs = dao.search(str);
								StringBuffer _sb = new StringBuffer();
								while(rs.next()){
									_sb.append(",");
									_sb.append(rs.getString("itemid"));
									_sb.append(",");
								}
								if(_sb.toString().indexOf(belongUN)==-1){
									throw GeneralExceptionHandler.Handle(new Exception("总额控制设置了归属单位，薪资项目里面没有此字段！"));
								}
								//只有在其设置了是否进行部门总额控制之才去判断
								if("0".equals(ctrl_type1) && _sb.toString().indexOf(belongUM)==-1){
									throw GeneralExceptionHandler.Handle(new Exception("总额控制设置了归属部门，薪资项目里面没有此字段！"));
								}
							}
						}
					}
				}
				//-----------------------------------end--------------------------------------------------
				this.ctrlparam.setValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag", amount_ctrl);
				this.ctrlparam.setValue(SalaryCtrlParamBo.AMOUNT_CTRL, "ctrl_type",ctrl_type);
				
				this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "valid",royalty_valid);
				this.ctrlparam.setValue(SalaryCtrlParamBo.PIECEPAY, "valid",priecerate_valid);
			}
			
			String verify_ctrl = "";
			if(form.get("verify_ctrl") != null)
				verify_ctrl = (String)form.get("verify_ctrl");
			String verify_ctrl_ff = "";
			if(form.get("verify_ctrl_ff") != null)
				verify_ctrl_ff = (String)form.get("verify_ctrl_ff");
			String verify_ctrl_sp = "";
			if(form.get("verify_ctrl_sp") != null)
				verify_ctrl_sp = (String)form.get("verify_ctrl_sp");
			String a01z0Flag = "";
			if(form.get("a01z0Flag") != null)
				a01z0Flag = (String)form.get("a01z0Flag");
			this.ctrlparam.setValue(SalaryCtrlParamBo.VERIFY_CTRL,verify_ctrl);
			this.ctrlparam.setValue(SalaryCtrlParamBo.VERIFY_CTRL, "verify_ctrl_ff",verify_ctrl_ff);
			this.ctrlparam.setValue(SalaryCtrlParamBo.VERIFY_CTRL, "verify_ctrl_sp",verify_ctrl_sp);
			
			this.ctrlparam.setValue(SalaryCtrlParamBo.A01Z0,"flag",a01z0Flag);
			
			this.ctrlparam.setValue(SalaryCtrlParamBo.BONUS, (String)form.get("bonusItemFld"));
			
			String field_priv = "";
			if(form.get("field_priv") != null)
				field_priv = (String)form.get("field_priv");
			String collect_je_field = "";
			if(form.get("collect_je_field") != null)
				collect_je_field = (String)form.get("collect_je_field");
			String read_field = "";
			if(form.get("read_field") != null)
				read_field = (String)form.get("read_field");
			this.ctrlparam.setValue(SalaryCtrlParamBo.FIELD_PRIV, field_priv);
			this.ctrlparam.setValue(SalaryCtrlParamBo.COLLECT_JE_FIELD, collect_je_field);
			this.ctrlparam.setValue(SalaryCtrlParamBo.READ_FIELD, read_field);
			
			//提成薪资
			this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "setid",(String)form.get("royalty_setid"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "date",(String)form.get("royalty_date"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "period",(String)form.get("royalty_period"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "relation_fields",(String)form.get("royalty_relation_fields"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.ROYALTIES, "strExpression",PubFunc.keyWord_reback(SafeCode.decode((String)form.get("strExpression"))));//提成薪资数据范围表达式是加密传的这里解密
			
			//计件薪资
			this.ctrlparam.setValue(SalaryCtrlParamBo.PIECEPAY, "period",(String)form.get("priecerate_period"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.PIECEPAY, "firstday",(String)form.get("priecerate_firstday"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.PIECEPAY, "strExpression",(String)form.get("priecerate_expression_str"));
			this.ctrlparam.setValue(SalaryCtrlParamBo.PIECEPAY, "relation_field",(String)form.get("priecerateFields"));
			this.ctrlparam.saveParameter();
			list.add(this.ctrlparam.getDoc1());
			list.add(this.ctrlparam.getXml2());
			/** 保存其他参数     end  */
			/*
			this.ctrlparam.setValue(SalaryCtrlParamBo.SUM_FIELD, "layer",layer);
			*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 得到标准类别当前纪录信息
	 * @return
	 * @throws GeneralException
	 */
	public RecordVo getRecordVo()throws GeneralException
	{
		RecordVo v=new RecordVo("salarytemplate");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			v.setInt("salaryid",Integer.parseInt(this.salaryid));
			v=dao.findByPrimaryKey(v);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return v;
	}
	
	
	
	
	
	
	/**
	 * 获得币种类型列表
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getMoneyStyleList()throws GeneralException
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select  nstyleid,cname from moneyStyle");
			//将这里置为-1因为人名币同样也是0导致人名币存不进去（或者将rowSet.getString("nstyleid")每个加一也可以）
			list.add(new CommonData("-1","(空)"));
			while(rowSet.next())
			{
				CommonData data=new CommonData(rowSet.getString("nstyleid"),rowSet.getString("cname")+"          ");
				list.add(data);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rowSet);
		}
		return list;
	}
	
	/**
	 * 获得子集中的指标
	 * @param type  1： 取日期型指标  2：取非代码型指标
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getFieldList(int type,String setid,String salaryid) throws GeneralException
	{
		ArrayList fieldList=new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet=dao.search("select * from salaryset where salaryid=?",Arrays.asList(salaryid));
			HashMap existFieldMap=new HashMap();
			while(rowSet.next())
				existFieldMap.put(rowSet.getString("itemid").toLowerCase(),"1");
			
			
			if(setid!=null&&setid.trim().length()>0)
			{
				ArrayList fielditemlist=DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET); 
				if(fielditemlist!=null)
				{
					for(int i=0;i<fielditemlist.size();i++)
				    {
				      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
				      if("M".equals(fielditem.getItemtype())|| "N".equals(fielditem.getItemtype()))
				    	  continue;
				      if(type==1&&!"D".equals(fielditem.getItemtype()))
				    	  continue;
				      
				      if(type==2&&existFieldMap.get(fielditem.getItemid().toLowerCase())==null)
				    	  continue;
				      
				      fieldList.add(new CommonData(fielditem.getItemid(),fielditem.getItemdesc()));
				    }
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rowSet);
		}
		return fieldList;
	}
	
	
	/**
	 * 取得人员库列表
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getDbList() throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			DbNameBo dd=new DbNameBo(this.conn);
			ArrayList dblist=dd.getAllDbNameVoList(this.userView);
			
			String cbase=","+this.vo.getString("cbase").toLowerCase();
			
			
			LazyDynaBean abean=null;
			for(int i=0;i<dblist.size();i++)
			{
				RecordVo vo=(RecordVo)dblist.get(i);
				
				String dbpre=vo.getString("pre");
				String dbname=vo.getString("dbname");
				String isSelected="0";
				if(cbase.indexOf(","+dbpre.toLowerCase()+",")!=-1)
					isSelected="1";
				abean=new LazyDynaBean();
				abean.set("pre",dbpre);
				abean.set("dbname",dbname);
				abean.set("isSelected",isSelected);
				list.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	/**
	 * @author lis
	 * @Description: 解析简单条件表达式
	 * @date 2015-12-24
	 * @param condStr 简单条件表达式
	 * @param cexpr 逻辑关系
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<CommonData> reParseExpression(String cexpr,String condStr)throws GeneralException
	{
		ArrayList<CommonData> selectedlist=new ArrayList();
		try
		{
			ArrayList<FieldItem> list=null;
			FactorList factorlist=new FactorList(cexpr,condStr,"");
			list=factorlist.getAllFieldList();
			if(!(list==null||list.size()==0))
			{
				for(int i=0;i<list.size();i++)
				{
					 FieldItem fielditem = list.get(i);
				     CommonData dataobj = new CommonData(fielditem.getItemid(), fielditem.getItemdesc());;
				     selectedlist.add(dataobj);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return selectedlist;
	}
	
	/**
	 * @author lis
	 * @Description: 从薪资项目中获取单位或部门指标
	 * @date 2015-12-26
	 * @param codesetid
	 * @param salaryid
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList getOrgOrDeptListFromSalaryset(String codesetid,String salaryid) throws GeneralException
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append(" select  distinct s.itemid,s.itemdesc from salaryset s,fielditem f where s.itemid = f.itemid and upper(f.itemtype)='A' and upper(f.codesetid)=? and upper(s.itemid) not in('E0122','B0110') and salaryid=?");
			ContentDAO dao = new ContentDAO(this.conn);
			list.add(new CommonData("","(空)"));
			rs = dao.search(sb.toString(),Arrays.asList(codesetid.toUpperCase(),salaryid));
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return list;
	}
	
	/**
	 * 保存薪资数据提交方式
	 * @param setlist		需要归档提交的数据集列表
	 * @param typelist		数据集提交类型列表
	 * @return
	 * @throws GeneralException
	 */
	public String saveSubmitType(String confirm_type,String subNoShowUpdateFashion,String subNoPriv,String allowEditSubdata)throws GeneralException
	{
		String bflag="";
		try
		{	
			SalaryTemplateBo salaryTemplateBo = new SalaryTemplateBo(conn, Integer.valueOf(salaryid), this.userView);
			ContentDAO dao=new ContentDAO(this.conn);
			
			SalaryLProgramBo lpbo=new SalaryLProgramBo(salaryTemplateBo.getTemplatevo().getString("lprogram"));
			lpbo.setValue(SalaryLProgramBo.CONFIRM_TYPE,confirm_type);
			lpbo.setValue(SalaryLProgramBo.CONFIRM_TYPE,"no_show",subNoShowUpdateFashion);
			lpbo.setValue(SalaryLProgramBo.CONFIRM_TYPE,"subNoPriv",subNoPriv);
			lpbo.setValue(SalaryLProgramBo.CONFIRM_TYPE,"allow_edit_subdata",allowEditSubdata);
			String str=lpbo.outPutContent();
			bflag = str;
			RecordVo vo=new RecordVo("salarytemplate");
			vo.setString("lprogram", str);
			vo.setInt("salaryid", Integer.valueOf(this.salaryid));
			dao.updateValueObject(vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;		
	}
	
	/**
	 * @author lis
	 * @Description: 得到指标名称
	 * @date 2016-1-4
	 * @param value
	 * @return
	 * @throws GeneralException
	 */
	public static String getSalarySet(String value) throws GeneralException{
		StringBuffer str = new StringBuffer();
		if(value==null||value.length()==0){
			return str.toString();
		}
		try{
			String[] values = value.split(",");
			for(int i=0;i<values.length;i++){
				if(values[i]!=null&&values[i].length()>0){
					FieldItem item = DataDictionary.getFieldItem(values[i]);
					str.append(item==null?values[i]:item.getItemdesc());
					if(i!=values.length-1)
						str.append(",");
				}
			}			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return str.toString();		
	}
	
	/**
	 * 添加参数gz_module，判断是薪资还是保险,lis修改
	 * @param name
	 * @param gz_module
	 * @return
	 * @throws GeneralException 
	 */
	public static String getAttributeName(String name,String gz_module) throws GeneralException{
		String str="";
		HashMap map = new HashMap();
		try{
			map.put("/param/cond_mode/flag", ResourceFactory.getProperty("gz.columns.menScope"));//人员范围
			map.put("/param/manager/user", ResourceFactory.getProperty("gz.templateset.shareFashion"));//共享方式
			map.put("/param/priv_mode/flag", ResourceFactory.getProperty("gz.templateset.rightFilter"));//限制用户管理范围
			map.put("/param/tax_date_field", ResourceFactory.getProperty("gz.templateset.computeTaxPoint"));//计税时间指标
			map.put("/param/declare_tax", ResourceFactory.getProperty("gz.templateset.appealTaxPoint"));//报税时间指标
			map.put("/param/pay_flag", ResourceFactory.getProperty("gz.templateset.paySalaryPoint"));//发薪标识指标
			map.put("/param/tax_mode", ResourceFactory.getProperty("gz.templateset.computeTaxTypePoint"));//计税方式指标
			map.put("/param/tax_desc", ResourceFactory.getProperty("gz.templateset.taxDeclare"));//纳税项目说明
			map.put("/param/tax_unit", ResourceFactory.getProperty("gz.templateset.taxUnit"));//计税单位指标
			map.put("/param/flow_ctrl/flag", ResourceFactory.getProperty("gz_new.gz_spMode"));//审批方式
			map.put("/param/flow_ctrl/reject_mode", ResourceFactory.getProperty("gz_new.gz_rejectMode"));//驳回方式
			map.put("/param/flow_ctrl/sp_relation_id", ResourceFactory.getProperty("gz_new.gz_auditRelation"));//审批关系
			map.put("/param/flow_ctrl/default_filterid", ResourceFactory.getProperty("gz.templateset.DefaultSpFilterItem"));//"默认审批项目"
			map.put("/param/sum_field/orgid", ResourceFactory.getProperty("gz.budget.unit.belong_to.field"));//归属单位指标
			map.put("/param/sum_field/deptid", ResourceFactory.getProperty("gz.templateset.lsdeptfield"));//归属部门指标
			map.put("/param/note/sms", ResourceFactory.getProperty("label.sms.notes"));//短信通知
			map.put("/param/note/mail", ResourceFactory.getProperty("performance.email.notice"));//邮件通知
			map.put("/param/note", ResourceFactory.getProperty("menu.gz.template"));//邮件模板
			
			////薪资总额控制/控制范围
			String amount_ctrl = ResourceFactory.getProperty("gz.templateset.gzTotalControl") +
			"/" + ResourceFactory.getProperty("gz_new.gz_controlScope");
			
			String amount_ctrl_ff = amount_ctrl + "/" + ResourceFactory.getProperty("gz_new.gz_controlFf");
			map.put("/param/amount_ctrl/amount_ctrl_ff", amount_ctrl_ff);//薪资总额控制/控制范围/控制发放
			String amount_ctrl_sp = amount_ctrl + "/" + ResourceFactory.getProperty("gz_new.gz_controlSp");
			map.put("/param/amount_ctrl/amount_ctrl_sp", amount_ctrl_sp);//薪资总额控制/控制范围/控制审批
			map.put("/param/amount_ctrl/ctrl_type", ResourceFactory.getProperty("gz_new.gz_controlMode"));//控制方式
			map.put("/param/amount_ctrl/flag", ResourceFactory.getProperty("gz.templateset.info4"));//是否进行总额控制
			
			String ff="1".equals(gz_module)?ResourceFactory.getProperty("gz.report.welfare"):ResourceFactory.getProperty("gz.report.salary");//保险":"薪资
			//审核公式控制/控制范围
			String verify_ctrl = ResourceFactory.getProperty("gz_new.gz_spFormulaControl") + "/" + ResourceFactory.getProperty("gz_new.gz_controlScope");
			//审核公式控制/控制范围/控制ff发放
			map.put("/param/verify_ctrl/verify_ctrl_ff", verify_ctrl+"/" +ResourceFactory.getProperty("gz_new.gz_control")+ff+ResourceFactory.getProperty("gz_new.gz_accounting.pay"));
			//审核公式控制/控制范围/控制ff审批
			map.put("/param/verify_ctrl/verify_ctrl_sp", verify_ctrl+"/" +ResourceFactory.getProperty("gz_new.gz_control")+ff+ResourceFactory.getProperty("gz_new.gz_accounting.approveoperation"));//
			map.put("/param/verify_ctrl", ResourceFactory.getProperty("gz_new.gz_isControlSpFormula"));//是否进行审核公式控制
			
			map.put("/param/a01z0/flag", ResourceFactory.getProperty("gz_new.gz_showStopSign"));//显示停放标识
			
			map.put("/param/fieldpriv", ResourceFactory.getProperty("gz_new.gz_field_priv"));//非写权限指标参与计算
			map.put("/param/readfield", ResourceFactory.getProperty("gz.lable.inforeadfield"));//读权限指标允许重新引入
			
			map.put("/param/royalties/valid", ResourceFactory.getProperty("gz_new.gz_royaltySalary"));//提成薪资
			map.put("/param/royalties/strExpression", ResourceFactory.getProperty("gz_new.gz_royaltySalary")+"/"+ResourceFactory.getProperty("gz.templateset.cond"));//提成薪资/数据范围
			map.put("/param/royalties/date", ResourceFactory.getProperty("gz_new.gz_royaltySalary")+"/"+ResourceFactory.getProperty("gz_new.gz_planDateField"));//提成薪资/计划日期指标
			map.put("/param/royalties/period",ResourceFactory.getProperty("gz_new.gz_royaltySalary")+"/"+ResourceFactory.getProperty("stat.info.setup.label.seasonal"));//提成薪资/周期
			map.put("/param/royalties/setid", ResourceFactory.getProperty("gz_new.gz_royaltySalary")+"/"+ResourceFactory.getProperty("gz_new.gz_royaltyDataSet"));//提成薪资/提成数据子集
			map.put("/param/royalties/relation_fields", ResourceFactory.getProperty("gz_new.gz_royaltySalary")+"/"+ResourceFactory.getProperty("id_factory.guideline"));//提成薪资/关联指标
			
			map.put("/param/piecepay/valid", ResourceFactory.getProperty("gz_new.gz_priecerateSalary"));//计件薪资
			map.put("/param/piecepay/strExpression", ResourceFactory.getProperty("gz_new.gz_priecerateSalary")+"/"+ResourceFactory.getProperty("gz.templateset.cond"));//计件薪资/数据范围
			map.put("/param/piecepay/period", ResourceFactory.getProperty("gz_new.gz_priecerateSalary")+"/"+ResourceFactory.getProperty("stat.info.setup.label.seasonal"));//计件薪资/周期
			map.put("/param/piecepay/firstday", ResourceFactory.getProperty("gz_new.gz_priecerateSalary")+"/"+ResourceFactory.getProperty("stat.info.setup.label.seasonal")+"/"+ResourceFactory.getProperty("datestyle.month"));//计件薪资/周期/月
			map.put("/param/piecepay/relation_field", ResourceFactory.getProperty("gz_new.gz_priecerateSalary")+"/"+ResourceFactory.getProperty("gz_new.gz_prieceField"));//计件薪资/引入指标
			
			map.put("/Params/ConfirmType/allow_edit_subdata", ResourceFactory.getProperty("gz.templateset.allowEditSubdata"));//允许修改已归档数据
			map.put("/Params/ConfirmType/subNoPriv", ResourceFactory.getProperty("gz_new.gz_submitNoJudgmentSetAndFieldPriv"));//数据提交入库不判断子集及指标权限
			map.put("/Params/ConfirmType/no_show", ResourceFactory.getProperty("gz_new.gz_submitNoShowOpt"));//提交时不显示数据操作方式设置
			map.put("/Params/ConfirmType", ResourceFactory.getProperty("gz.templateset.sjtjfs"));//数据提交方式
			
			map.put("/Params/hidden_items/hidden_item", ResourceFactory.getProperty("gz_new.gz_spPoint"));//审批指标
			str=(String) map.get(name);
			str = str==null||str.length()==0?name:str;
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return str;
	}
	
	public static String getAttributeValue(String ul,String name,Connection con,String salaryid) throws GeneralException{
		String str="";
		HashMap map = new HashMap();
		RowSet rs = null;
		try{
			String check = ResourceFactory.getProperty("gz_new.gz_check");
			String cancelCheck = ResourceFactory.getProperty("gz_new.gz_cancelCheck");
			map.put("/param/cond_mode/flag0", ResourceFactory.getProperty("gz.templateset.simpleCondition"));//简单条件
			map.put("/param/cond_mode/flag1", ResourceFactory.getProperty("gz.templateset.complexCondition"));//复杂条件
			map.put("/param/priv_mode/flag1", check);//勾选
			map.put("/param/priv_mode/flag0", cancelCheck);//取消勾选
			map.put("/param/manager/user", "不共享");//不共享
			map.put("/param/flow_ctrl/flag1", ResourceFactory.getProperty("t_template.approve.ok"));//需要审批
			map.put("/param/flow_ctrl/flag0", ResourceFactory.getProperty("t_template.approve.no"));//不需要审批
			map.put("/param/flow_ctrl/reject_mode1", ResourceFactory.getProperty("gz_new.gz_layerReject"));//逐级驳回
			map.put("/param/flow_ctrl/reject_mode2", ResourceFactory.getProperty("gz_new.gz_rejectToOriginator"));//驳回发起人
			map.put("/param/note/sms1", check);
			map.put("/param/note/sms0", cancelCheck);
			map.put("/param/note/mail1", check);
			map.put("/param/note/mail0", cancelCheck);
			map.put("/param/amount_ctrl/amount_ctrl_ff1", check);
			map.put("/param/amount_ctrl/amount_ctrl_ff0", cancelCheck);
			map.put("/param/amount_ctrl/amount_ctrl_sp1", check);
			map.put("/param/amount_ctrl/amount_ctrl_sp0", cancelCheck);
			map.put("/param/amount_ctrl/ctrl_type1", ResourceFactory.getProperty("gz_new.gz_forceControl"));//强行控制
			map.put("/param/amount_ctrl/ctrl_type0", ResourceFactory.getProperty("system.options.itemwarn"));//预警提示
			map.put("/param/amount_ctrl/flag1", check);
			map.put("/param/amount_ctrl/flag0", cancelCheck);
			
			map.put("/param/verify_ctrl/verify_ctrl_ff1", check);
			map.put("/param/verify_ctrl/verify_ctrl_ff0", cancelCheck);
			map.put("/param/verify_ctrl/verify_ctrl_sp1", check);
			map.put("/param/verify_ctrl/verify_ctrl_sp0", cancelCheck);
			map.put("/param/verify_ctrl1", check);
			map.put("/param/verify_ctrl0", cancelCheck);
			
			map.put("/param/a01z0/flag1", check);
			map.put("/param/a01z0/flag0", cancelCheck);
			
			map.put("/param/fieldpriv1", check);
			map.put("/param/fieldpriv0", cancelCheck);
			
			map.put("/param/readfield1", check);
			map.put("/param/readfield0", cancelCheck);
			
			map.put("/param/royalties/valid1", check);
			map.put("/param/royalties/valid0", cancelCheck);
			
			map.put("/param/piecepay/valid1", check);
			map.put("/param/piecepay/valid0", cancelCheck);
			map.put("/param/piecepay/firstday1", ResourceFactory.getProperty("gz_new.gz_naturalMonth"));//自然月份
			
			map.put("/Params/ConfirmType/allow_edit_subdata1", check);
			map.put("/Params/ConfirmType/allow_edit_subdata0", cancelCheck);
			map.put("/Params/ConfirmType/subNoPriv1", check);
			map.put("/Params/ConfirmType/subNoPriv0", cancelCheck);
			map.put("/Params/ConfirmType/no_show1", check);
			map.put("/Params/ConfirmType/no_show0", cancelCheck);
			if("/Params/ConfirmType".equalsIgnoreCase(ul)){
				StringBuffer buf = new StringBuffer();
				if(name!=null&&name.length()!=0){
					String[] _name = name.split(";");
					for(int i=0;i<_name.length;i++){
						if(_name[i].indexOf("`")!=-1){
							String[] s = _name[i].split("`");
							FieldSet fieldset=DataDictionary.getFieldSetVo(s[0]);
							String temp = s[1];
							if("2".equals(s[1])){
								temp = ResourceFactory.getProperty("label.gz.notchange");
							}else if("0".equals(s[1])){
								temp = ResourceFactory.getProperty("label.gz.update");
							}else{
								temp = ResourceFactory.getProperty("label.gz.append");
							}
							buf.append(fieldset.getCustomdesc()+":"+temp+"\r");
						}else{
							buf.append("||"+getSalarySet(_name[i])+ResourceFactory.getProperty("gz_new.gz_cumulativeUpdate"));//累计更新
						}
					}					
				}
				map.put("/Params/ConfirmType"+name, buf.toString());
			}
			
			ContentDAO dao=new ContentDAO(con);
			if("/param/tax_date_field".equalsIgnoreCase(ul)){
				rs = dao.search("select itemdesc from salaryset where salaryid="+salaryid+" and itemid='"+name+"' ");
				if(rs.next()){
					map.put("/param/tax_date_field"+name, rs.getString("itemdesc"));
				}				
			}
			if("/param/declare_tax".equalsIgnoreCase(ul)){
				rs = dao.search("select itemdesc from salaryset where salaryid="+salaryid+" and itemid='"+name+"' ");
				if(rs.next()){
					map.put("/param/declare_tax"+name, rs.getString("itemdesc"));
				}
			}
			if("/param/pay_flag".equalsIgnoreCase(ul)){
				rs = dao.search("select itemdesc from salaryset where salaryid="+salaryid+" and itemid='"+name+"' ");
				if(rs.next()){
					map.put("/param/pay_flag"+name, rs.getString("itemdesc"));
				}
			}
			if("/param/tax_mode".equalsIgnoreCase(ul)){
				rs = dao.search("select itemdesc from salaryset where salaryid=? and itemid=? ",Arrays.asList(salaryid,name));
				if(rs.next()){
					map.put("/param/tax_mode"+name, rs.getString("itemdesc"));
				}
			}
			if("/param/tax_desc".equalsIgnoreCase(ul)){
				rs = dao.search("select itemdesc from salaryset where salaryid=? and itemid=? ",Arrays.asList(salaryid,name));
				if(rs.next()){
					map.put("/param/tax_desc"+name, rs.getString("itemdesc"));
				}
			}
			if("/param/tax_unit".equalsIgnoreCase(ul)){
				rs = dao.search("select itemdesc from salaryset where salaryid=? and itemid=? ",Arrays.asList(salaryid,name));
				if(rs.next()){
					map.put("/param/tax_unit"+name, rs.getString("itemdesc"));
				}
			}
			if("/param/flow_ctrl/sp_relation_id".equals(ul)){
				DbWizard dbw = new DbWizard(con);
				if(dbw.isExistTable("t_wf_relation",false))
				{
					String sql="select * from t_wf_relation where validflag=1 and actor_type='4' and relation_id=? ";
					
					rs=dao.search(sql,Arrays.asList(name));
					if(rs.next())
					{
						map.put("/param/flow_ctrl/sp_relation_id"+name, rs.getString("cname"));
					}
				}
			}
			if("/param/flow_ctrl/default_filterid".equals(ul)){
	            StringBuffer buf=new StringBuffer();
	            buf.append("select id,chz,cfldname,scope,username from gzitem_filter where ");
	            buf.append("id=?");
	            buf.append(" and scope =0 order by norder ");//共享的
	            rs=dao.search(buf.toString(),Arrays.asList(name));
				if(rs.next())
				{
					map.put("/param/flow_ctrl/default_filterid"+name, rs.getString("chz"));
				}
			}
			if("/param/sum_field/orgid".equals(ul)){
				map.put("/param/sum_field/orgid"+name,getSalarySet(name));
			}
			if("/param/sum_field/deptid".equals(ul)){
				map.put("/param/sum_field/deptid"+name,getSalarySet(name));
			}
			if("/param/note".equals(ul)){
				String sql = "select id,name from email_name where nmodule=5 and id=?";
				rs = dao.search(sql,Arrays.asList(name));
				if(rs.next()){
					map.put("/param/note"+name,rs.getString("name"));
				}
			}
			if("/Params/hidden_items/hidden_item".equals(ul)){
				map.put("/Params/hidden_items/hidden_item"+name,getSalarySet(name));
			}
			if("/param/royalties/date".equals(ul)){
				map.put("/param/royalties/date"+name,getSalarySet(name));
			}
			if("/param/royalties/relation_fields".equals(ul)){
				map.put("/param/royalties/relation_fields"+name,getSalarySet(name));
			}
			if("/param/royalties/period".equals(ul)){
				if("1".equals(name))map.put("/param/royalties/period"+name,ResourceFactory.getProperty("datestyle.month"));//月
				else if("2".equals(name))map.put("/param/royalties/period"+name,ResourceFactory.getProperty("performance.workplan.workplanview.quarter"));//季
				else if("3".equals(name))map.put("/param/royalties/period"+name,ResourceFactory.getProperty("stat.info.setup.archive_type.half"));//半年
				else map.put("/param/royalties/period"+name,ResourceFactory.getProperty("datestyle.year"));//年
			}
			if("/param/piecepay/period".equals(ul)){
				if("1".equals(name))map.put("/param/piecepay/period"+name,ResourceFactory.getProperty("datestyle.month"));//月
				else if("2".equals(name))map.put("/param/piecepay/period"+name,ResourceFactory.getProperty("performance.workplan.workplanview.quarter"));//季
				else if("3".equals(name))map.put("/param/piecepay/period"+name,ResourceFactory.getProperty("stat.info.setup.archive_type.half"));//半年
				else map.put("/param/piecepay/period"+name,ResourceFactory.getProperty("datestyle.year"));//年
			}
			if("/param/piecepay/relation_field".equals(ul)){
				if(name!=null&&name.length()>0){
					name = PubFunc.keyWord_reback(name);
					StringBuffer context = new StringBuffer();
					String[] fields = name.split(",");
					for(int i=0;i<fields.length;i++){
						String[] field = fields[i].split("=");
						context.append(getSalarySet(field[0]));
						context.append("=");
						context.append(getSalarySet(field[1]));
						context.append(",");
					}
					map.put("/param/piecepay/relation_field"+name,context.toString());
				}				
			}
			
			str=(String) map.get(ul+name);
			str = str==null||str.length()==0?name:str;
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return str;
	}
	
	/**
     * 查询薪资项目子集
     * @param salaryid
     * @param rightvalue 设置对比值
	 * @throws GeneralException 
     * @throws GeneralException
     */
	public ArrayList leftList(String salaryid,String rightvalue) throws GeneralException{
		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList retlist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where initflag<>3 and salaryid=");
		sql.append(salaryid);
		sql.append(" and itemtype in ('N','A'");
		sql.append(") and fieldsetid not like 'H%' "); //排除基准岗位指标 zhanghua 2017-6-19
		if(rightvalue!=null&&!"".equals(rightvalue))
		{
	    	sql.append("and itemid not in ('");
    		sql.append(rightvalue);
	    	sql.append("')");
		}
		sql.append("group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return retlist;
	}
	
	 /**
     * 将设置对比值设置为list
     * @param salaryid
     * @param rightvalue 设置对比值
	 * @throws GeneralException 
     * @throws GeneralException
     */
	public ArrayList rightList(String salaryid,String rightvalue) throws GeneralException{
		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
		ArrayList retlist=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where initflag<>3 and salaryid=");
		sql.append(salaryid);
		sql.append(" and itemid in ('");
		sql.append(rightvalue);
		sql.append("') group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return retlist;
	}
	
	public ArrayList leftList1(String salaryid,String rightvalue) throws GeneralException{
		rightvalue=rightvalue!=null?rightvalue.replaceAll(",","','"):"";
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList retlist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select itemid,itemdesc,sortid from salaryset where initflag<>3 and salaryid=");
		sql.append(salaryid);
		sql.append(" and fieldsetid not like 'H%' ");//排除基准岗位指标 zhanghua 2017-6-19
		if(rightvalue!=null&&!"".equals(rightvalue))
		{
	    	sql.append("and itemid not in ('");
    		sql.append(rightvalue);
	    	sql.append("')");
		}
		sql.append("group by itemid,itemdesc,sortid order by sortid");
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql.toString());
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("itemid").toString(),dynabean.get("itemdesc").toString());
				retlist.add(obj);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return retlist;
	}
	/**
	 * 获取业务用户关联自助用户名称。
	 * @param username 自助用户名
	 * @return
	 */
	public String getFullName(String username){
		String fullname="";
		if(StringUtils.isBlank(username))
			return "";
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			String strSql="select fullname from operuser where upper(username)='"+username.toUpperCase()+"'";
			RowSet rs=dao.search(strSql);
			if(rs.next()){
				fullname=rs.getString("fullname");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return fullname;
	}
	
	public SalaryCtrlParamBo getCtrlparam() {
		return ctrlparam;
	}

	public void setCtrlparam(SalaryCtrlParamBo ctrlparam) {
		this.ctrlparam = ctrlparam;
	}

	public int getGz_type() {
		return gz_type;
	}

	public void setGz_type(int gz_type) {
		this.gz_type = gz_type;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getUnit_type() {
		return unit_type;
	}

	public void setUnit_type(String unit_type) {
		this.unit_type = unit_type;
	}

	public RecordVo getVo() {
		return vo;
	}

	public void setVo(RecordVo vo) {
		this.vo = vo;
	}
}
