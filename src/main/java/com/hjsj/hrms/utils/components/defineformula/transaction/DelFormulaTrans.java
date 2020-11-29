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

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：DelFormulaTrans 
 * 类描述：删除计算公式
 * 创建人：zhaoxg
 * 创建时间：Jun 2, 2015 4:17:26 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 2, 2015 4:17:26 PM
 * 修改备注： 
 * @version
 */
public class DelFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			HashMap hm = this.getFormHM();		
			String base = "no";		
			
			
			String itemid=(String)hm.get("itemid"); //公式id
			itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
			
			ArrayList itemid_array= (ArrayList)hm.get("itemid_array"); //公式id
			itemid_array=(ArrayList) (itemid_array!=null&&itemid_array.size()>0?itemid_array:"");
			
			String id= (String)hm.get("id"); //薪资类别id ,人事异动模版id
			id=id!=null&&id.trim().length()>0?id:"";
			
			String itemname = (String)hm.get("itemname");
			itemname = itemname!=null&&itemname.length()>0?itemname:"";
			
			String groupid=(String)hm.get("groupid");//公式组id
			groupid = groupid!=null&&groupid.length()>0?groupid:"";
			
			String module = (String)hm.get("module"); //模块标识
			String formulaType = (String)hm.get("formulaType");//2是审核公式
			DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
			if("2".equals(formulaType)){//人事和薪资一样，gaohy,2016-1-5
				if("1".equals(module))
					id = PubFunc.decrypt(SafeCode.decode(id));
				base = bo.deleteGzSpFormula(id, itemid_array,module);//删除薪资审核公式
				this.getFormHM().put("@eventlog", base);		
			}else if("1".equals(module)){
				id = PubFunc.decrypt(SafeCode.decode(id));
				base = bo.delGzFormula(id, itemid);//删除薪资计算公式
			}else if("3".equals(module)){//人事异动，gaohy
				String seq = (String)hm.get("seq"); //模块标识
				String formulas=bo.getFormulas(id,groupid,"0"); //获取所有计算公式
				String formulaItems[]=formulas.split("\\`");
				itemname=itemname.substring(itemname.indexOf("_")+1);
				itemname=itemname.replace("START*DATE", "START_DATE");
				itemname=itemname.replace("start*date", "start_date");
				ArrayList list = new ArrayList();
				for(int i=0; i<formulaItems.length;i++){
					String formulaItem[]=formulaItems[i].split("\\=");
					String formulaItemName = formulaItem[0];
					String itemName = formulaItemName.substring(formulaItemName.indexOf("_")+1, formulaItemName.lastIndexOf("_"));
					String itemSeq = formulaItemName.substring(0, formulaItemName.indexOf("_"));//获取要删除项目的序号
					if(!(itemname.equalsIgnoreCase(itemName) && itemSeq.equals(seq))){
						list.add(formulaItems[i].toString().substring(formulaItems[i].toString().indexOf("_")+1));
					}
				}

				StringBuffer formulaitems=new StringBuffer();
				for (int j = 0; j < list.size(); j++){
					formulaitems.append(list.get(j));
					formulaitems.append("`");
				}
				//不改变alertItem方法进行查询
				RecordVo vo=new RecordVo("gzadj_formula");
				vo.setInt("id",Integer.parseInt(groupid));
				vo.setInt("tabid",Integer.parseInt(id));
				vo=dao.findByPrimaryKey(vo);
				String cfactor = "";
				if("1".equals(vo.getString("formula").split("`").length) || vo.getString("formula").split("`").length == 1)
					cfactor = "";
				else
					cfactor = vo.getString("cfactor");
				
				ChangeFormulaBo formulabo = new ChangeFormulaBo();
				formulabo.alertItem(dao,id,groupid,formulaitems.toString(),cfactor);
			}
			hm.put("base",base);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
