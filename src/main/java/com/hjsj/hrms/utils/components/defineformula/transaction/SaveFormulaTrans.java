package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SaveFormulaTrans 
 * 类描述：保存计算公式
 * 创建人：zhaoxg
 * 创建时间：Jun 2, 2015 3:38:58 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 2, 2015 3:38:58 PM
 * 修改备注： 
 * @version
 */

public class SaveFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			HashMap hm = this.getFormHM();
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			String id = (String) this.getFormHM().get("id"); //薪资类别id/人事异动模版id
			id=id!=null&&id.trim().length()>0?id:"";	
			
			String itemid = (String)hm.get("itemid"); //公式id
			itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
			
			String groupid=(String)hm.get("groupid");
			groupid=groupid!=null&&groupid.length()>0?groupid:"";
			
			String itemname = (String)hm.get("itemname");//项目指标id
			itemname = itemname!=null&&itemname.trim().length()>0?itemname:"";
			
			String formula = (String)hm.get("formula"); //要修改的公式计算内容
			formula=formula!=null&&formula.trim().length()>0?formula:"";
			
			formula=PubFunc.keyWord_reback(SafeCode.decode(formula));// 公式内容加密传递 zhanghua 2017-4-19
			if(formula.indexOf("\r\n") == -1)//暂时判断如果没有\r\n的将所有\n的转为\r\n，如果不替换，cs显示错误（这里只是默认如果有\r\n的全部为\r\n，如果\n的全为\n）sunjian  17-10-27 bug【32311】
				formula = formula.replace("\n", "\r\n");
			String module = (String)hm.get("module"); //模块号，1是薪资 ， 3是人事异动，4是考勤假期管理
			String formulaType = (String)hm.get("formulaType");//公式类别，2是审核公式
			DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
			if("2".equals(formulaType)){ //更新薪资审批公式，人事和薪资一样，拿了出来,gaohy,2016-1-5
				if("1".equals(module))
					id = PubFunc.decrypt(SafeCode.decode(id));
				bo.saveGzSpFormula(formula, itemid);
			}else if("1".equals(module)){
				id = PubFunc.decrypt(SafeCode.decode(id));
				bo.saveGzFormula(id, formula, itemid);//更新薪资计算公式
				hm.put("info","ok");
			}else if("3".equals(module)){// 3是人事异动(gaohy,2015-12-17)
				String seq = (String)hm.get("seq"); //排序号 lis 20160711
				String formulas=bo.getFormulas(id,groupid,"0"); //获取所有计算公式
				formulas=formulas.replace("START_DATE", "START*DATE");
				formulas=formulas.replace("start_date", "start*date");
				String formulaItems[]=formulas.split("\\`");
				for (int i = 0; i < formulaItems.length; i++) {
				    /*
					String formulaItem[]=formulaItems[i].split("\\=");
					String itemName=formulaItem[0].substring(formulaItem[0].indexOf("_")+1);
					if(itemName.equalsIgnoreCase(itemname+"_2")){
						formulaItems[i]=itemName+"="+formula;
					}else if(formulaItem.length==2){
						formulaItems[i]=formulaItem[0].substring(formulaItem[0].indexOf("_")+1)+"="+formulaItem[1];
					}else if(formulaItem.length==1){//计算项目没有计算公式
						formulaItems[i]=formulaItem[0].substring(formulaItem[0].indexOf("_")+1)+"=";
					}
					*/
					
					
					   String strItem=formulaItems[i];
					   if ("".equals(strItem)){
						   continue;
					   }
			            int k = strItem.indexOf("=");
			            String itemName= strItem.substring(0,k);
			            String itemSeq = itemName.split("_")[0];
			            itemName = itemName.split("_")[1]+"_2";
			            if(itemName.equalsIgnoreCase(itemname+"_2") && itemSeq.equals(seq)){
	                        formulaItems[i]=itemName+"="+formula;
			            }
	                    else{
	                        String _formula= strItem.substring(k+1,strItem.length());
	                        formulaItems[i]=itemName+"="+_formula;
	                    }
			           
				}
				StringBuffer formulaitems=new StringBuffer();
				for (int j = 0; j < formulaItems.length; j++) {
					formulaitems.append(formulaItems[j]);
					formulaitems.append("`");
				}
				//不改变alertItem方法进行查询
				RecordVo vo=new RecordVo("gzadj_formula");
				vo.setInt("id",Integer.parseInt(groupid));
				vo.setInt("tabid",Integer.parseInt(id));
				vo=dao.findByPrimaryKey(vo);
				
				ChangeFormulaBo formulabo = new ChangeFormulaBo();
				formulabo.alertItem(dao,id,groupid,formulaitems.toString(),vo.getString("cfactor"));
				hm.put("info","ok");
			} else if("4".equals(module)){
				//考勤假期管理
			    String hoildayType = (String) this.getFormHM().get("hoildayType");
			    if(StringUtils.isNotEmpty(hoildayType))
			        hoildayType = PubFunc.decrypt(hoildayType);
			    
			    String hoildayYear = (String) this.getFormHM().get("hoildayYear");
			    // 更新假期管理计算公式
                bo.saveKqFormula(itemname, formula, hoildayType, hoildayYear);
                hm.put("info", "ok");

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
