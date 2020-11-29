package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.*;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteBankTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String bank_id = (String)this.getFormHM().get("bank_id");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String a_code=(String)this.getFormHM().get("code");
			String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			String temp_table_name="TT"+this.userView.getUserName()+"_gz_b";
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn(),this.getUserView());	//xieguiquan 增加参数this.getUserView() 20100827
			String filterCondId=(String)this.getFormHM().get("filterCondId");
			String filterSql = (String)this.getFormHM().get("filterSql");
			String  beforeSql = (String)this.getFormHM().get("beforeSql");
			beforeSql=PubFunc.decrypt(SafeCode.decode(beforeSql));
			filterSql=PubFunc.decrypt(SafeCode.decode(filterSql));
			String model=(String)this.getFormHM().get("model");
		    String boscount=(String)this.getFormHM().get("boscount");
			String bosdate=(String)this.getFormHM().get("bosdate");
			bo.deleteBankInfo("gz_bank",bank_id);
			bo.deleteBankInfo("gz_bank_item",bank_id);
			bo.deleteTempTable(temp_table_name);
			ArrayList bankList = bo.getBankTemplatesList();
			///------
			String count=bo.getBankCount();//=1or=2
			ArrayList columnsList = new ArrayList();
			ArrayList list= new ArrayList();
			String tabname="";
			String sql="";
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
			String a0100=bo.getA0100s(beforeSql,filterSql,tableName,priv_mode,privSql,model,spSQL);
			ArrayList filterCondList = bo.getFilterCondList(salaryid);
			ArrayList column= new ArrayList();
			String nextBank_id="*";
			 String username="";
			if("1".equalsIgnoreCase(count))
			{
				  
			}
			else
			{
				  

			  //------
                  nextBank_id=bo.getFirstBank_id();
	    		  /**要显示的列名*/
                  HashMap salarySetMap = bo.getSalarySetFields(salaryid);
    			  columnsList = bo.getTemplateColumns(nextBank_id,salarySetMap);
		    	  /**列名的field_name*/
    			
		    	  column=bo.getColumns(nextBank_id,salarySetMap);
		    	
		     	  ArrayList columnsInfo = bo.getFieldInfoFromSalarySet(column,salaryid,2,salarySetMap);
		    	  /**数据列表*/
		    	  HashMap map =bo.getFormatMap(column,nextBank_id);
		    	  HashMap lengthMap =bo.getDefault_length(bank_id);
		    	  bo.setSalaryid(salaryid);
		     	  ArrayList dataList = bo.getPersonInfoList(tableName,a_code,column,columnsInfo,map,lengthMap,model,spSQL);
		    	  tabname="TT"+this.userView.getUserName()+"_gz_b";
		    	  if("1".equals(model))
		    		  oSql+=" and ("+spSQL+")";
		     	  sql = "select T.* from "+tabname+" T,("+oSql+") S where T.a0100=S.a0100 and UPPER(T.pre)=UPPER(S.nbase) and T.a00z1=S.a00z1";
		     	  sql+=" and ("+a0100+")"; 
		     	 if(order!=null&&!"".equals(order.trim()))
				    {
				    	order = "S."+order.replaceAll(",", ",S.");
				    	sql+=" order by "+order;
				    }
				    else
				    {
				    	 sql+=" order by dbid,S.a0000,S.a00z0,S.a00z1";
				    }
	    		  bo.createBankDiskTempTable(bo.getBankItemInfo(nextBank_id,salaryid,1,salarySetMap),salaryid,nextBank_id,this.userView,dataList,column);
		     	  //ArrayList itemList =bo.getItemidAndDescList(bank_id);
		    	  list =bo.getLabelList(bo.getBankItemInfo(nextBank_id,salaryid,2,salarySetMap),map);
		    	   filterCondList = bo.getFilterCondList(salaryid);
		    	   ArrayList list1=bo.getFirstScope(nextBank_id);
	    		   username=(String)list1.get(1);
			  }
			
	          this.getFormHM().put("bank_id",nextBank_id);
	          this.getFormHM().put("columnsList",columnsList);
			  this.getFormHM().put("dataList",list);
			  this.getFormHM().put("column",column);
			  this.getFormHM().put("bankList",bankList);
			  this.getFormHM().put("columnListSize",String.valueOf(column.size()));
			  this.getFormHM().put("size",String.valueOf(bankList.size()));
			  this.getFormHM().put("salaryid",salaryid);
			  this.getFormHM().put("code",a_code);
			  this.getFormHM().put("tableName",tableName);
			  this.getFormHM().put("filterCondList",filterCondList);
			  this.getFormHM().put("tabname",tabname);
			  this.getFormHM().put("sql",sql);
			  this.getFormHM().put("filterCondId",filterCondId);
			  this.getFormHM().put("count",count);
			  this.getFormHM().put("username", username);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}

}
