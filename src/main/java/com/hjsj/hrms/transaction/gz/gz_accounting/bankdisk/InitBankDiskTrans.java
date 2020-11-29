package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.*;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
public class InitBankDiskTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
       try
       {
		  HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		  String a_code = (String)map.get("code");
		  String salaryid=(String)map.get("salaryid");
		  String model = (String)map.get("model");
		  String boscount="";
		  String bosdate="";
		  String spSQL="";
		  String tableName=this.userView.getUserName()+"_salary_"+salaryid;
		  SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		  String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		  String priv_mode=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
		  String order=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView);
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
			  boscount=(String)map.get("count");
			  bosdate=(String)map.get("bosdate");
			  SalaryReportBo bo=new SalaryReportBo(this.getFrameconn(),salaryid,"");
			  spSQL=bo.getSpSQL(this.getUserView(), boscount, bosdate,model);
		  }
		  CashListBo clb = new CashListBo(this.getFrameconn(),model,salaryid);
		  clb.setUserview(this.userView);
		  String privSql=clb.getPrivSql(this.userView, gzbo);
		  String opt=(String)map.get("opt");
		  String o=(String)map.get("o");
		  String filterSql ="";
		  String beforeSql = "";
		  
		  String m_filterSql ="";
		  String m_beforeSql = "";
		  
		  String oSql="";
		  if(order!=null&&!"".equals(order.trim()))
			  oSql="select * from "+tableName+" where 1=1 ";
		  else
		      oSql="select a.dbid,b.a0100,b.a0000,b.a00z0,b.a00z1,b.nbase from dbname a,"+tableName+" b where UPPER(a.pre)=UPPER(b.nbase)";
		  String filterCondId="all";
		  BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn(),this.getUserView());  //xieguiquan 增加参数this.getUserView() 20100827
		  /**代发银行列表*/
		  bo.setUserview(this.userView);
		  ArrayList bankList = bo.getBankTemplatesList();
		  String count=bo.getBankCount();//=1or=2
		  String bank_id="*";
		  ArrayList list= new ArrayList();
		  String tabname="";
		  String sql="";
		  String a0100="";
		  ArrayList filterCondList = bo.getFilterCondList(salaryid);
		  ArrayList column= new ArrayList();
		  String scope="";
		  String username="";
		  if("1".equalsIgnoreCase(count))
		  {
			  filterCondId=(String)map.get("condid");
		  }
		  else
		  {
		  
	    	   if("init".equalsIgnoreCase(opt))
	    	   {
	    		   bank_id=bo.getFirstBank_id();
	    		   m_beforeSql=(String)map.get("s");
	    		   beforeSql = PubFunc.decrypt(SafeCode.decode((String)map.get("s")));
	    		   filterCondId=(String)map.get("condid");
	    		   if(bank_id!=null&&bank_id.length()!=0&&!"*".equalsIgnoreCase(bank_id)){
		    		   ArrayList list1=bo.getFirstScope(bank_id);
		    		   scope=(String)list1.get(0);
		    		   username=(String)list1.get(1);
	    		   }
	    	   }
	    	   else if("add".equalsIgnoreCase(opt))
	    	   {
	    		   /* 银行报盘-银行名称-新建（安全引起的问题） xiaoyun 2014-9-23 start */
    			   //bank_id=(String)map.get("bank_id");
	    		   bank_id = (String)this.getFormHM().get("bank_id");
    			   /* 银行报盘-银行名称-新建（安全引起的问题） xiaoyun 2014-9-23 start */
    			   m_filterSql=(String)this.getFormHM().get("filterSql");
    			   m_beforeSql=(String)this.getFormHM().get("beforeSql");
    			   filterSql = PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("filterSql")));
    			   beforeSql = PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("beforeSql")));
    			   username=(String)this.getFormHM().get("username");
    			  // beforeSql=SafeCode.decode(beforeSql);
	    	   }
	    	  /**列名的field_name*/
	    	   HashMap salarySetMap = bo.getSalarySetFields(salaryid);
	    	   if(bank_id!=null&&bank_id.length()!=0&&!"*".equalsIgnoreCase(bank_id)){
	    		   column=bo.getColumns(bank_id,salarySetMap);
	    	   }
	    	 
	    	  ArrayList columnsInfo = bo.getFieldInfoFromSalarySet(column,salaryid,2,salarySetMap);
	    	  /**数据列表*/
	    	  tabname="TT"+this.userView.getUserName()+"_gz_b";
	    	  if("1".equals(model))
	    		  oSql+=" and ("+spSQL+")";
	    	  sql = "select T.* from "+tabname+" T,("+oSql+") S where T.a0100=S.a0100 and UPPER(T.pre)=UPPER(S.nbase)  and T.a00z1=S.a00z1";
	    	  /**后加*/
	    	  filterCondId=(String)this.getFormHM().get("filterCondId");
	    	  if(filterSql!=null&&!"".equals(filterSql))
			     {
			    	 filterCondId=(String)map.get("condid");
	    		     //filterCondId=(String)this.getFormHM().get("filterCondId");
			    	 if(filterCondId==null||filterCondId.trim().length()==0)
			    	 {
			    		 filterCondId="new";
			    	 }
			     }
	    	  /**后加*/
	    	  else
	    	  {
	    		  filterCondId="all";
	    	  }
		   if(beforeSql == null)
		   {
			   beforeSql = "";
		   }
		   if(filterSql ==null)
		   {
			   filterSql = "";
		   }
		   beforeSql = PubFunc.keyWord_reback(SafeCode.decode(beforeSql));
		   filterSql = PubFunc.keyWord_reback(SafeCode.decode(filterSql));
		   
	//     大数据时容易产生错误，组装的sql太长	    
	//	   a0100=bo.getA0100s(beforeSql,filterSql,tableName,priv_mode,privSql,model,spSQL);
	//	   sql+=" and ("+a0100+")"; 
		   sql+=" and exists (select null from "+tableName+" where 1=1 and "+tableName+".a0100=T.a0100 and upper("+tableName+".nbase)=upper(T.pre) ";
		   if(filterSql!=null&&filterSql.trim().length()>0)
		   {
			   sql+=" and "+SafeCode.decode(filterSql);
		   }
		   if(beforeSql!=null&&beforeSql.trim().length()>0)
		   {
			   sql+=" and "+SafeCode.decode(SafeCode.decode(beforeSql)).trim().substring(3);
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
	    	  HashMap hm = bo.getFormatMap(column,bank_id);
	    	  HashMap lengthMap =bo.getDefault_length(bank_id);
	    	  bo.setSalaryid(salaryid);
	    	  
	    	  ArrayList dataList = bo.getPersonInfoList(tableName,a_code,column,columnsInfo,hm,lengthMap,model,spSQL);
	    	 
	    	  bo.createBankDiskTempTable(bo.getBankItemInfo(bank_id,salaryid,1,salarySetMap),salaryid,bank_id,this.userView,dataList,column);
		   
	    	  //ArrayList itemList =bo.getItemidAndDescList(bank_id);
	    	  list =bo.getLabelList(bo.getBankItemInfo(bank_id,salaryid,2,salarySetMap),hm);
	    	 
		  }
		  if(o!=null)
		  {
			  filterCondId=(String)map.get("condid");
		  }
		//  SafeCode.decode(content==null?"":content)
          this.getFormHM().put("bank_id",bank_id);
		  this.getFormHM().put("dataList",list);
		  this.getFormHM().put("bankList",bankList);
		  this.getFormHM().put("tabname",tabname);
		  this.getFormHM().put("sql",sql);
		  this.getFormHM().put("columnListSize",String.valueOf(column.size()));
		  this.getFormHM().put("salaryid",salaryid);
		  this.getFormHM().put("code",a_code);
		  this.getFormHM().put("tableName",tableName);
		  this.getFormHM().put("filterCondList",filterCondList);
		  this.getFormHM().put("filterCondId",filterCondId);
		  this.getFormHM().put("filterSql",m_filterSql);
		  this.getFormHM().put("count",count);
		  this.getFormHM().put("beforeSql", m_beforeSql);
		  this.getFormHM().put("model",model);
		  this.getFormHM().put("bosdate",bosdate);
		  this.getFormHM().put("boscount",boscount);
		  this.getFormHM().put("scope",scope);
		  this.getFormHM().put("username", username);
       }
       catch(Exception e)
       {
    	   e.printStackTrace();
    	   throw GeneralExceptionHandler.Handle(e);
       }
       finally
       {
    	   this.getFormHM().remove("o");
       }
    }

}
