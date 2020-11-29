package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:ListTrainProTrans.java
 * </p>
 * <p>
 * Description:培训项目列表交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class ListTrainProTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String code = (String) hm.get("code");
	if(code != null && code.length() > 0 && !"null".equalsIgnoreCase(code) && !"all".equalsIgnoreCase(code))
	    code = PubFunc.decrypt(SafeCode.decode(code));

	ArrayList fieldList = DataDictionary.getFieldList("r13", Constant.USED_FIELD_SET);
	//ArrayList fieldList = this.getUserView().getPrivFieldList("r13", Constant.USED_FIELD_SET);
	StringBuffer columns = new StringBuffer();
	for (int i = 0; i < fieldList.size(); i++)
	{
	    FieldItem item = (FieldItem) fieldList.get(i);
	    String itemid = (String) item.getItemid();
	    columns.append("," + itemid);
	}
	String strwhere = "from r13 where 1=1";
	if(code==null)
	    code="all";
	if("".equals(code))
	    code="all";
	if (!"all".equals(code))
	    strwhere = strwhere + " and (r1301='"+code+"' or r1308='" + code + "')";
//	else if(code.equals("all"))
//	    strwhere = strwhere + " and (r1308 is null or r1308='')";
	
//	 判断登录用户为哪种类型的用户：用户管理的还是帐号分配里的
	if(!this.userView.isSuper_admin())
	{
//		int status = this.userView.getStatus();	
//	    if (status == 4)// 帐号分配里面的用户
//		{
//	    	if ("UN".equalsIgnoreCase(this.userView.getManagePrivCode())){
//	    		String manamgePrivCode = this.userView.getManagePrivCodeValue();
//	    		strwhere+=" and (b0110='HJSJ' or b0110 like '"+manamgePrivCode+"%')";
//	    	}else
//	    		strwhere += " and B0110 = 'HJSJ'";
//		}
//		else if (status == 0)// 用户管理里面的用户
//		{
			String a_code="";
//			a_code = this.userView.getUnit_id();
//			a_code = PubFunc.getTopOrgDept(a_code);
			TrainCourseBo bo = new TrainCourseBo(this.userView);
			a_code = bo.getUnitIdByBusi();
			String str="";
			if(a_code.indexOf("UN`")==-1){//UN`全部
				String unitarr[] = a_code.split("`"); 
				for(int i=0;i<unitarr.length;i++){
					if(unitarr[i]!=null&&unitarr[i].trim().length()>2&& "UN".equalsIgnoreCase(unitarr[i].substring(0, 2))){
							str +="B0110 like '"+unitarr[i].substring(2)+"%' or ";
					}
				}
				if(str.length()>0)
					strwhere += " and (b0110='HJSJ' or "+str.substring(0, str.lastIndexOf("or")-1)+")";
				else{
//					if ("UN".equalsIgnoreCase(this.userView.getManagePrivCode())){
//			    		String manamgePrivCode = this.userView.getManagePrivCodeValue();
//			    		strwhere+=" and (b0110='HJSJ' or b0110 like '"+manamgePrivCode+"%')";
//			    	}else
			    		strwhere += " and B0110 = 'HJSJ'";
				}
			}
//		}
	}
	
	TrainCourseBo bo = new TrainCourseBo("r13");
	String search = (String) this.getFormHM().get("strParam");
	search=PubFunc.reBackWord(search);
    search = PubFunc.keyWord_reback(search);
    String wherestr = ""; 
    if (search != null && search.trim().length() > 0)
        wherestr = bo.getWhereStr(search);
    
    if(wherestr !=null && wherestr.length() > 0)
        strwhere += " " +wherestr;
    
	this.userView.getHm().put("train_sql", "select " + columns.substring(1) + " " + strwhere + " order by r1301");
	this.getFormHM().put("columns", columns.substring(1));
	this.getFormHM().put("strwhere", strwhere);
	this.getFormHM().put("strsql", "select " + columns.substring(1));
	this.getFormHM().put("fields", fieldList);
    }

}
