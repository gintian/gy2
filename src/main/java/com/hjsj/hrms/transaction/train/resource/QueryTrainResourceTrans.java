package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.resource.TrainResourceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryTrainResourceTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String type = (String) hm.get("type");
	if (!TrainResourceBo.hasTrainResourcePrivByType(type, this.userView))
	    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.function.nopriv"),"",""));
	
	TrainResourceBo bo = new TrainResourceBo(this.frameconn, type);

	String search = (String) this.getFormHM().get("strParam");
	this.userView.getHm().put("train_strParam", search);
	String recTable = bo.getRecTable();
	String wherestr="";
	StringBuffer strsql = new StringBuffer();
	StringBuffer strwhere = new StringBuffer();
	StringBuffer columns = new StringBuffer();
	ArrayList list = bo.getItems();
	for (int i = 0; i < list.size(); i++)
	{
	    LazyDynaBean abean = (LazyDynaBean) list.get(i);
	    String itemid = (String) abean.get("itemid");
	    columns.append(","+itemid);
	}
	if("5".equals(type)){
		String a_code = (String) hm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		hm.remove("a_code");
		
		if(a_code.trim().length()<1){
			a_code = (String) this.getFormHM().get("a_code");
			a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		}
		a_code= "all".equalsIgnoreCase(a_code)?"":a_code;
		if(a_code != null && a_code.length() > 0 && !"null".equalsIgnoreCase(a_code))
            a_code = PubFunc.decrypt(SafeCode.decode(a_code));
		
		if(a_code.trim().length()>0){
			strwhere.append(" and R0700 like '");
			strwhere.append(a_code);
			strwhere.append("%'");
		}
		if (search != null && search.trim().length() > 0)
		{
			//TrainCourseBo tb = new TrainCourseBo();
			//wherestr = tb.getSearchWhere(search);
			
			wherestr = bo.getWhereStr(search);
//			String searcharr[] = search.split("::");
//			if (searcharr.length == 3)
//			{
//				String sexpr = searcharr[0];
//				String sfactor = searcharr[1];
//				sexpr = PubFunc.keyWord_reback(sexpr);
//				sfactor = PubFunc.keyWord_reback(sfactor);
//				sfactor = PubFunc.reBackWord(sfactor);
//
//				boolean blike = false;
//				blike = searcharr[2] != null && searcharr[2].equals("1") ? true : false;
//				FactorList factor = new FactorList(sexpr, sfactor, "", false, blike, true, 1, "su");
//				wherestr = factor.getSqlExpression();
//				if (wherestr.indexOf("WHERE") != -1)
//					wherestr = wherestr.substring(wherestr.indexOf("WHERE") + 5, wherestr.length());
//				else if (wherestr.indexOf("where") != -1)
//					wherestr = wherestr.substring(wherestr.indexOf("where") + 5, wherestr.length());
//				//if (wherestr.indexOf("I9999") != -1)
//					//wherestr = wherestr.substring(0, wherestr.lastIndexOf("AND"));
//
//				wherestr = wherestr.replace("A01", recTable);
//				if(!"r50".equalsIgnoreCase(recTable))
//					wherestr = wherestr.replaceAll(recTable+"00", recTable+"01");
//			}
		}
		strsql.append(columns.length()>0?"select " + columns.substring(1):"");
		strsql.append(",case (select count(fileid) from tr_res_file where r0701=R07.r0701) when 0 then 0 else 1 end as fileflag");
		columns.append(",fileflag");
		
	}else{
		if (search != null && search.trim().length() > 0)
		{
			///TrainCourseBo tb = new TrainCourseBo();
			//wherestr = tb.getSearchWhere(search);
			wherestr = bo.getWhereStr(search);
//			String searcharr[] = search.split("::");
//			if (searcharr.length == 3)
//			{
//				String sexpr = searcharr[0];
//				String sfactor = searcharr[1];
//				sexpr = PubFunc.keyWord_reback(sexpr);
//				sfactor = PubFunc.keyWord_reback(sfactor);
//				sfactor = PubFunc.reBackWord(sfactor);
//
//				boolean blike = false;
//				blike = searcharr[2] != null && searcharr[2].equals("1") ? true : false;
//				FactorList factor = new FactorList(sexpr, sfactor, "", false, blike, true, 1, "su");
//				wherestr = factor.getSqlExpression();
//				if (wherestr.indexOf("WHERE") != -1)
//					wherestr = wherestr.substring(wherestr.indexOf("WHERE") + 5, wherestr.length());
//				else if (wherestr.indexOf("where") != -1)
//					wherestr = wherestr.substring(wherestr.indexOf("where") + 5, wherestr.length());
//				//if (wherestr.indexOf("I9999") != -1)
//				//	wherestr = wherestr.substring(0, wherestr.lastIndexOf("AND"));
//
//				wherestr = wherestr.replace("A01", recTable);
//				if(!"r50".equalsIgnoreCase(recTable))
//					wherestr = wherestr.replaceAll(recTable+"00", recTable+"01");
//			}
		}
		strsql.append(columns.length()>0?"select " + columns.substring(1):"");
		if("2".equals(type)){
			strsql.append(",case (select count(fileid) from tr_res_file where r0701=R04.r0401 and type='1') when 0 then 0 else 1 end as fileflag");
			columns.append(",fileflag");
		}
	}
	
//	 判断登录用户为哪种类型的用户：用户管理的还是帐号分配里的
//	int status = this.userView.getStatus();	
	if(!this.userView.isSuper_admin())
	{
//	    if (status == 4)// 帐号分配里面的用户
//		{
//		    /**liwc 业务业务登陆 管理范围*/
//			if ("UN".equalsIgnoreCase(this.userView.getManagePrivCode()))
//				strwhere.append(" and (b0110='HJSJ' or b0110 like '"
//						+ this.userView.getManagePrivCodeValue() + "%')");
//			else
//				strwhere.append(" and B0110 = 'HJSJ'");
//		}
//		else if (status == 0)// 用户管理里面的用户
//		{
//			String a_code = this.userView.getUnit_id();
//			a_code = PubFunc.getTopOrgDept(a_code);
		TrainCourseBo tb = new TrainCourseBo(this.userView);
		String a_code = tb.getUnitIdByBusi();
		if(a_code.indexOf("UN`")==-1){//UN`全部
			String unitarr[] = a_code.split("`"); 
			String str="";
			for(int i=0;i<unitarr.length;i++){
				if(unitarr[i]!=null&&unitarr[i].trim().length()>2&& "UN".equalsIgnoreCase(unitarr[i].substring(0, 2))){
						str +="B0110 like '"+unitarr[i].substring(2)+"%' or ";
				}
			}
			if(str.length()>0)
				strwhere.append(" and (b0110='HJSJ' or "+str.substring(0, str.lastIndexOf("or")-1)+")");
			else{
//				if ("UN".equalsIgnoreCase(this.userView.getManagePrivCode()))
//					strwhere.append(" and B0110 like '"
//							+ this.userView.getManagePrivCodeValue() + "%'");
//				else
					strwhere.append(" and B0110 = 'HJSJ'");
			}
		}
//		}
//	    if(manamgePrivCode.equals(""))
//			throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
	}
	String wheresql = " from " + bo.getRecTable() + " where 1=1";
	if(wherestr.length()>1)
		wheresql=wheresql+wherestr;
	if(strwhere.length()>1)
		wheresql +=strwhere.toString();
	
    this.userView.getHm().put("train_sql", strsql.toString() + wheresql + " order by " + bo.getPrimaryField());
	this.getFormHM().put("columns", columns.substring(1));
	this.getFormHM().put("strwhere", wheresql);
	this.getFormHM().put("strsql",strsql.toString());
	this.getFormHM().put("fields", list);
	this.getFormHM().put("primaryField", bo.getPrimaryField());
	this.getFormHM().put("recTable", recTable);	
	if("1".equals(type))
		this.getFormHM().put("fields", bo.getItems2());
    }

}
