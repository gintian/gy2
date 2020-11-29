package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.resource.TrainResourceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:ListTrainResourceTrans.java
 * </p>
 * <p>
 * Description:培训体系列表交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-21 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class ListTrainResourceTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String type = (String) hm.get("type");
	hm.remove("type");
	TrainResourceBo bo = new TrainResourceBo(this.frameconn, type);

	StringBuffer columns = new StringBuffer();
	String recTable = bo.getRecTable();
	ArrayList list = bo.getItems();
	for (int i = 0; i < list.size(); i++)
	{
	    LazyDynaBean abean = (LazyDynaBean) list.get(i);
	    String itemid = (String) abean.get("itemid");
	    columns.append(","+itemid);
	}
	StringBuffer strsql = new StringBuffer();
	StringBuffer strwhere = new StringBuffer(" from ");
	strwhere.append(bo.getRecTable()+" where 1=1 ");
	if("5".equals(type)){
		String a_code = (String) hm.get("a_code");
		a_code=a_code==null||a_code.trim().length()<0?"":a_code;
		hm.remove("a_code");
		
		if(a_code.trim().length()<1){
			a_code = (String) this.getFormHM().get("a_code");
			a_code=a_code==null||a_code.trim().length()<0?"":a_code;
		}
		a_code= "all".equalsIgnoreCase(a_code)?"":a_code;
		
		if(a_code != null && a_code.length() > 0 && !"null".equalsIgnoreCase(a_code))
            a_code = PubFunc.decrypt(SafeCode.decode(a_code));
		
		if(a_code.trim().length()>0){
			strwhere.append("and R0700 like '");
			strwhere.append(a_code);
			strwhere.append("%'");
		}
		strsql.append(columns.length()>0?"select " + columns.substring(1):"");
		strsql.append(",case (select count(fileid) from tr_res_file where r0701=R07.r0701 and (type='0' or type='' or type is null)) when 0 then 0 else 1 end as fileflag");
		columns.append(",fileflag");
		this.getFormHM().put("a_code", SafeCode.encode(PubFunc.encrypt(a_code)));
	}else{
		strsql.append(columns.length()>0?"select " + columns.substring(1):"");
	}
	if("2".equals(type)){
		strsql.append(",case (select count(fileid) from tr_res_file where r0701=R04.r0401 and type='1') when 0 then 0 else 1 end as fileflag");
		columns.append(",fileflag");
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
			//String a_code = this.userView.getUnit_id();
			//a_code = PubFunc.getTopOrgDept(a_code);
		TrainCourseBo tb = new TrainCourseBo(this.userView);
		String a_code = tb.getUnitIdByBusi();
			if(a_code.indexOf("UN`")==-1){
				String unitarr[] = a_code.split("`"); 
				String str="";
				for(int i=0;i<unitarr.length;i++){
					if(unitarr[i]!=null&&unitarr[i].trim().length()>2&&unitarr[i].startsWith("UN")){
							str +="B0110 like '"+unitarr[i].substring(2)+"%' or ";
					}
				}
				if(str.length()>0)
					strwhere.append(" and (b0110='HJSJ' or "+str.substring(0, str.lastIndexOf("or")-1)+")");
				else{
//					if ("UN".equalsIgnoreCase(this.userView.getManagePrivCode()))
//						strwhere.append(" and (b0110='HJSJ' or B0110 like '"
//								+ this.userView.getManagePrivCodeValue() + "%')");
//					else
						strwhere.append(" and B0110 = 'HJSJ'");
				}
			}
//		}
//	    if(manamgePrivCode.equals(""))
//			throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
	}
	String search = (String) this.getFormHM().get("strParam");
	String wherestr = ""; 
	if (search != null && search.trim().length() > 0)
        wherestr = bo.getWhereStr(search);
	
	if(wherestr !=null && wherestr.length() > 0)
	    strwhere.append(wherestr);
	
	this.userView.getHm().put("train_strParam", search);
	this.userView.getHm().put("train_sql", strsql.toString()+strwhere.toString() + " order by " + bo.getPrimaryField());
	this.getFormHM().put("columns", columns.length()>0?columns.substring(1):"");
	this.getFormHM().put("strwhere", strwhere.toString());
	this.getFormHM().put("strsql", strsql.toString());
	this.getFormHM().put("fields", bo.getItems2());
	this.getFormHM().put("primaryField", bo.getPrimaryField());
	this.getFormHM().put("recTable", recTable);
	this.getFormHM().put("nameFld", bo.getNameFld());
	this.getFormHM().put("resType", type);
    }
    
}
