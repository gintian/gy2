package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.*;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ChangeBankidTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			 
			  String a_code = (String)this.getFormHM().get("code");
			  String salaryid=(String)this.getFormHM().get("salaryid");
			  String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			  String bank_id=(String)this.getFormHM().get("bank_id");
			  String tabname="TT"+this.userView.getUserName()+"_gz_b";
			  String beforeSql = (String)this.getFormHM().get("beforeSql");
			  String filterSql = (String)this.getFormHM().get("filterSql");
			  /* 薪资发放-银行报盘-切换银行 xiaoyun 2014-9-23 start */
			  beforeSql = PubFunc.keyWord_reback(PubFunc.decrypt(SafeCode.decode(beforeSql))); 
			  //beforeSql=PubFunc.keyWord_reback(SafeCode.decode(beforeSql));
			  //filterSql=PubFunc.keyWord_reback(SafeCode.decode(filterSql));
			  filterSql=PubFunc.keyWord_reback(PubFunc.decrypt(SafeCode.decode(filterSql)));
			  /* 薪资发放-银行报盘-切换银行 xiaoyun 2014-9-23 end */
			  BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn(),this.userView);
			  String filterCondId = (String)this.getFormHM().get("filterCondId");
			  String model=(String)this.getFormHM().get("model");
			  String boscount=(String)this.getFormHM().get("boscount");
			  String bosdate=(String)this.getFormHM().get("bosdate");
			  String a0100="";
			  /**代发银行列表*/
			  ArrayList bankList = bo.getBankTemplatesList();
			  if(bank_id==null|| "".equals(bank_id))
			  {
			      bank_id=bo.getFirstBank_id();
			  }
        	  /**列名的field_name*/
			  HashMap salarySetMap = bo.getSalarySetFields(salaryid);
			  ArrayList column=bo.getColumns(bank_id,salarySetMap);
			  SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			  String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			  String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			  String order=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView);
			  String spSQL="";
			  if("0".equals(model))
			  {
		    	  if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
		    		  tableName=this.userView.getUserName()+"_salary_"+salaryid;
		    	  else
		    		  tableName=manager+"_salary_"+salaryid;
			  }
			  else
			  {
				  tableName="salaryhistory";
				  SalaryReportBo srb=new SalaryReportBo(this.getFrameconn(),salaryid,"");
				  spSQL=srb.getSpSQL(this.getUserView(), boscount, bosdate,model);
			  }
		
			  String oSql="";
			  if(order!=null&&!"".equals(order.trim()))
				  oSql="select * from "+tableName+" where 1=1 ";
			  else
			      oSql="select a.dbid,b.a0100,b.a0000,b.a00z0,b.a00z1,b.nbase from dbname a,"+tableName+" b where UPPER(a.pre)=UPPER(b.nbase)";
			  CashListBo clb = new CashListBo(this.getFrameconn(),model,salaryid);
			  clb.setUserview(this.userView);
			  String privSql=clb.getPrivSql(this.userView, gzbo);
			  ArrayList columnsInfo = bo.getFieldInfoFromSalarySet(column,salaryid,2,salarySetMap);
			  /**数据列表*/
			  HashMap map =bo.getFormatMap(column,bank_id);
			  HashMap lengthMap =bo.getDefault_length(bank_id);
			  bo.setSalaryid(salaryid);
			  ArrayList dataList = bo.getPersonInfoList(tableName,a_code,column,columnsInfo,map,lengthMap,model,spSQL);
			  //ArrayList itempropertylist=bo.getBankItemInfo(bank_id);
			  bo.createBankDiskTempTable(bo.getBankItemInfo(bank_id,salaryid,1,salarySetMap),salaryid,bank_id,this.userView,dataList,column);
			  ArrayList list =bo.getLabelList(bo.getBankItemInfo(bank_id,salaryid,2,salarySetMap),map);
			  if("1".equals(model))
	    		  oSql+=" and ("+spSQL+")";
			  String sql= "select T.* from "+tabname+" T,("+oSql+") S where T.a0100=S.a0100 and UPPER(T.pre)=UPPER(S.nbase)  and T.a00z1=S.a00z1";
		 
//		     大数据时容易产生错误，组装的sql太长	    
		  	//	   a0100=bo.getA0100s(beforeSql,filterSql,tableName,priv_mode,privSql,model,spSQL);
		  	//	   sql+=" and ("+a0100+")"; 
		  		   sql+=" and exists (select null from "+tableName+" where 1=1 and "+tableName+".a0100=T.a0100 and upper("+tableName+".nbase)=upper(T.pre) ";
		  		   if(filterSql!=null&&filterSql.trim().length()>0)
				   {
		  			  /* 薪资发放-银行报盘-切换银行 xiaoyun 2014-9-23 start */
					   //sql+=" and "+SafeCode.decode(filterSql);
		  			 sql+=" and "+filterSql;
					  /* 薪资发放-银行报盘-切换银行 xiaoyun 2014-9-23 end */
				   }
				   if(beforeSql!=null&&beforeSql.trim().length()>0)
				   {
					   /* 薪资发放-银行报盘-切换银行 xiaoyun 2014-9-23 start */
					   //sql+=" and "+SafeCode.decode(SafeCode.decode(beforeSql)).trim().substring(3);
					   sql+=" and " + beforeSql.trim().substring(3);
					   /* 薪资发放-银行报盘-切换银行 xiaoyun 2014-9-23 end */
				   }
				   if(privSql!=null&&privSql.trim().length()>0)
				   {
					   sql+=" and "+privSql;
				    }
					if("1".equals(model))
					{
						sql+=" and ("+spSQL+")";
					} 
					
					sql+=" )";
		      
		      if(order!=null&&!"".equals(order.trim()))
			    {
			    	order = "S."+order.replaceAll(",", ",S.");
			    	sql+=" order by "+order;
			    }
			    else
			    {
			    	 sql+=" order by dbid,S.a0000,S.a00z0,S.a00z1";
			    }
		      ArrayList list1=bo.getFirstScope(bank_id);
		      String scope=(String)list1.get(0);
		      String usernmae=(String)list1.get(1);
			  this.getFormHM().put("tabname",tabname);
	          this.getFormHM().put("bank_id",bank_id);
			  this.getFormHM().put("dataList",list);
			  this.getFormHM().put("tabname",tabname);
			  this.getFormHM().put("sql",sql);
			  this.getFormHM().put("column",column);
			  this.getFormHM().put("bankList",bankList);
			  this.getFormHM().put("bankListSize",String.valueOf(bankList.size()));
			  this.getFormHM().put("columnListSize",String.valueOf(column.size()));
			  this.getFormHM().put("salaryid",salaryid);
			  this.getFormHM().put("code",a_code);
			  this.getFormHM().put("tableName",tableName);
			  this.getFormHM().put("filterCondId",/*"*"*/filterCondId);
			  /* 薪资发放-银行报盘-切换银行 xiaoyun 2014-9-23 start */
			  beforeSql = SafeCode.encode(PubFunc.encrypt(beforeSql));
			  filterSql = SafeCode.encode(PubFunc.encrypt(filterSql));
			  /* 薪资发放-银行报盘-切换银行 xiaoyun 2014-9-23 end */
			  this.getFormHM().put("beforeSql", beforeSql);
			  this.getFormHM().put("filterSql",filterSql);
			  this.getFormHM().put("scope", scope);
			  this.getFormHM().put("username", usernmae);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}

}
