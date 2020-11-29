package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：ItemCodeLinkageTrans 
 * 类描述：计算公式---项目内数据联动 
 * 创建人：zhaoxg
 * 创建时间：Jun 5, 2015 10:21:35 AM
 * 修改人：zhaoxg
 * 修改时间：Jun 5, 2015 10:21:35 AM
 * 修改备注： 
 * @version
 */
public class ItemCodeLinkageTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String id = (String)this.getFormHM().get("id");//薪资类别id,或者人事异动模版id
			id=id!=null&&id.length()>0?id:"";
			String opt = (String) this.getFormHM().get("opt");
			String _itemid = (String) this.getFormHM().get("itemid");//公式id
			ArrayList<HashMap> list = new ArrayList<HashMap>();
			HashMap<String,String> map = new HashMap<String,String>();
			ContentDAO dao = new ContentDAO(this.frameconn);
			String formulaType = (String)getFormHM().get("formulaType");//公式类别，2是审核公式
			String module = (String)this.getFormHM().get("module");//模块号，1是薪资,3是人事异动，4是考勤假期管理
			String formula = this.getFormHM().get("formula")==null?"":(String)this.getFormHM().get("formula");//公式
			if(StringUtils.isNotBlank(formula)) {
				formula = SafeCode.decode(formula);
				formula=PubFunc.keyWord_reback(formula);
			}
			
			if("1".equals(module)){//薪资
				id = PubFunc.decrypt(SafeCode.decode(id));
				
				DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
				list = bo.gzItemCodeLinkage(id, opt, _itemid,formulaType);
				this.getFormHM().put("data", list);
			}else if("3".equals(module)){//人事异动
				DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
				ArrayList listdata=bo.tempIemCodeLinkage(id,opt,_itemid,formulaType,formula);//liuyz 传递公式类是是审核公式还是计算公式
				this.getFormHM().put("data", listdata);
			} else if("4".equals(module)){
			    //考勤假期管理
                DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
                ArrayList listdata=bo.getFieldList(opt,_itemid);
                this.getFormHM().put("data", listdata);
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
