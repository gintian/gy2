package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SaveSortFormulaTrans 
 * 类描述：保存计算公式排序
 * 创建人：liuyz
 * 创建时间：Jun 5, 2017 2:49:52 PM
 * @version
 */
public class SaveSortFormulaTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String id = (String) this.getFormHM().get("id"); //薪资类别id/人事异动模版id
		id=id!=null&&id.trim().length()>0?id:"";	
		
		String itemid = (String)hm.get("itemid"); //公式id
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
		String groupid=(String)hm.get("groupid"); 
		groupid=groupid!=null&&groupid.length()>0?groupid:"";
		
		String sorting=(String)hm.get("sorting"); //排序后的顺序 3、人事异动：原来顺序序号_项目指标id,...;  1、薪资 更改后序号_项目指标id,...
		sorting=sorting!=null&&sorting.length()>0?sorting:"";
		sorting=SafeCode.decode(sorting);
		
		String module = (String)hm.get("module"); //模块号，1是薪资 ， 3是人事异动
		String formulaType = (String)hm.get("formulaType");//公式类别，1是计算公式，2是审核公式 
		DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
		if("2".equals(formulaType)){  //审核公式
			if("1".equals(module))
				id = PubFunc.decrypt(SafeCode.decode(id));
			String sortings[]=sorting.split(",");
			ArrayList salaryformulaList=new ArrayList();
			for(String items:sortings)
			{
				salaryformulaList.add( Arrays.asList(items.split("_")));
			}
			String flag="0";
			if("3".equals(module))
				flag="0";
			else
				flag="1";
				
			String salarySql=" update hrpchkformula set seq=? where tabid="+id+" and chkid=? and flag="+flag;
			try {
				dao.batchUpdate(salarySql, salaryformulaList);
				hm.put("info","ok");
			} catch (SQLException e) {
				e.printStackTrace();
				hm.put("info","false");
			}
		}else if("1".equals(module)){
			id = PubFunc.decrypt(SafeCode.decode(id));
			String sortings[]=sorting.split(",");
			ArrayList salaryformulaList=new ArrayList();
			for(String items:sortings)
			{
				salaryformulaList.add( Arrays.asList(items.split("_")));
			}
			String salarySql=" update SALARYFORMULA set SORTID=? where SALARYID="+id+" and itemid=? ";
			try {
				dao.batchUpdate(salarySql, salaryformulaList);
				hm.put("info","ok");
			} catch (SQLException e) {
				e.printStackTrace();
				hm.put("info","false");
			}
		}else if("3".equals(module)){
			String formulas=bo.getFormulas(id,groupid,"0"); //获取所有计算公式
			formulas=formulas.replace("START_DATE", "START*DATE");
			formulas=formulas.replace("start_date", "start*date");
			String sortings[]=sorting.split(",");
			String formulaItems[]=formulas.split("\\`");
			ArrayList sortFormulaItems=new ArrayList();
			StringBuffer formulaitems=new StringBuffer();
			for(int i=0;i<sortings.length;i++)
			{
				 String value[]=sortings[i].split("_");
				 String sortItemSeq=value[0];   //原项目排序位置
				 String sortItemName=value[1];  //项目名称id
				 String strItem=formulaItems[Integer.parseInt(sortItemSeq)]; 
			     if ("".equals(strItem)){
					 continue;
				 }
		         int k = strItem.indexOf("=");
		         String itemName= strItem.substring(0,k);
		         String itemSeq = itemName.split("_")[0];
		         itemName = itemName.split("_")[1]+"_2";
		         if(itemName.equalsIgnoreCase(sortItemName+"_2") && itemSeq.equals(sortItemSeq))
		         {
	                 String _formula= strItem.substring(k+1,strItem.length());
	                 formulaitems.append(itemName+"="+_formula+"`");
		         }
			}
			ChangeFormulaBo formulabo = new ChangeFormulaBo();
			formulabo.alertItem(dao,id,groupid,formulaitems.toString(),"");
			hm.put("info","ok");
		}
	}
}
