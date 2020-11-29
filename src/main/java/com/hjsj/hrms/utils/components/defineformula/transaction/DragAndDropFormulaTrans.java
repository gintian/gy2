package com.hjsj.hrms.utils.components.defineformula.transaction;


import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：DragAndDropFormulaTrans 
 * 类描述：计算公式拖放
 * 创建人：zhaoxg
 * 创建时间：Jun 5, 2015 9:40:52 AM
 * 修改人：zhaoxg
 * 修改时间：Jun 5, 2015 9:40:52 AM
 * 修改备注： 
 * @version
 */
public class DragAndDropFormulaTrans extends IBusiness {


	public void execute() throws GeneralException {
		String ori_itemid = (String) this.getFormHM().get("ori_itemid");//移动数据id
		String ori_seq = (String) this.getFormHM().get("ori_seq");//移动数据排序号
		String to_itemid = (String) this.getFormHM().get("to_itemid");//目标数据id
		String to_seq = (String) this.getFormHM().get("to_seq");//目标数据排序号
		
		String id = (String) this.getFormHM().get("id");//薪资类别id,人事异动模版id芬
		
		String module = (String)this.getFormHM().get("module");//模块号，1是薪资模块
		String formulaType = (String)getFormHM().get("formulaType");//2是审核公式
		DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
		try {
			if("2".equals(formulaType)){//人事和薪资相同，所以拿了出来,gaohy,2016-1-5
				if("1".equals(module))
					id=PubFunc.decrypt(SafeCode.decode(id));
				bo.moveGzSpFormula(id, ori_itemid,ori_seq, to_itemid, to_seq);
			}else if("1".equals(module)){
				id = PubFunc.decrypt(SafeCode.decode(id));
				bo.removeFormula(id, ori_itemid,ori_seq, to_itemid, to_seq);
			}else if("3".equals(module)){//人事异动-计算公式，gaohy,2015-12-24
				String flag = (String) this.getFormHM().get("flag");//1:人事异动公式 否则是公式组
				if("1".equals(flag)){
					String ori_itemname = (String) this.getFormHM().get("ori_itemname");//移动数据id
					String to_itemname = (String) this.getFormHM().get("to_itemname");//目标数据id
					String groupId = (String) this.getFormHM().get("groupId");
					
					String formulas=bo.getFormulas(id,groupId,"3"); //获取所有计算公式
					String formulaItems[]=formulas.split("\\`");
					String ori_formula = formulaItems[Integer.parseInt(ori_seq)];//移动的公式
					ArrayList list = new ArrayList();
					for(int i=0; i<formulaItems.length;i++){
						String formulaItem[]=formulaItems[i].split("\\=");
						if(i==Integer.parseInt(to_seq)){//遍历到目标公式则追加上去
							list.add(ori_formula.toString());
							list.add(formulaItems[i].toString());
						}else if(i==Integer.parseInt(ori_seq)){//遍历到被移动的公式就跳过
							continue;
						}else{
							list.add(formulaItems[i].toString());
						}
					}
					StringBuffer formulaitems=new StringBuffer();
					for (int j = 0; j < list.size(); j++){
						formulaitems.append(list.get(j));
						formulaitems.append("`");
					}
					ContentDAO dao = new ContentDAO(this.frameconn);
					ChangeFormulaBo formulabo = new ChangeFormulaBo();
					if(formulaitems.length()>0)
						formulabo.alertItem(dao,id,groupId,formulaitems.toString(),"");
				}else{
					bo.removeFormulaTemp(id, ori_itemid, ori_seq, to_itemid, to_seq);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}