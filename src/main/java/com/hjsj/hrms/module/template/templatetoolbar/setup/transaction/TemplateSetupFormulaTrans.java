package com.hjsj.hrms.module.template.templatetoolbar.setup.transaction;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hjsj.hrms.module.template.templatetoolbar.setup.businessobject.TemplateGzFormulaBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class TemplateSetupFormulaTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		String opt = (String) this.getFormHM().get("opt");
		opt = opt!=null&&opt.length()>0?opt:"";
		
		String cHz = (String) this.getFormHM().get("cHz");//公式组m名称
		cHz = cHz!=null&&cHz.trim().length()>0?cHz:"";
		
		String groupId = (String) this.getFormHM().get("groupId");//公式组ID
		groupId = groupId!=null&&groupId.trim().length()>0?groupId:"";
		
		String tableid = (String)this.getFormHM().get("tableid");
		tableid = tableid!=null&&tableid.length()>0?tableid:"";
		
		String cfactor = (String)this.getFormHM().get("cfactor");//无值
		cfactor=cfactor!=null&&cfactor.trim().length()>0?cfactor:"";
		cfactor = SafeCode.decode(cfactor);
		cfactor = PubFunc.keyWord_reback(cfactor);
		
		String item = (String)this.getFormHM().get("item");//计算项目
		item=item!=null&&item.trim().length()>0?item:"";
		item = SafeCode.decode(item);
		item = PubFunc.keyWord_reback(item);
		
		ChangeFormulaBo formulabo = new ChangeFormulaBo();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if("1".equals(opt)){
			String userName = this.userView.getUserFullName();//获得当前用户名
			this.getFormHM().put("userName", userName);
		}else if("2".equals(opt)){
			TemplateGzFormulaBo tbo = new TemplateGzFormulaBo(dao,this.userView);
			ArrayList gzList=tbo.getGzFormula(tableid);//获得公式组数据
			net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(gzList);//转换json格式
			this.getFormHM().put("data", jsonArray);
		}else if("3".equals(opt)){//点击新增的时候，先创建公式组
			cHz = PubFunc.keyWord_reback(cHz);
			
			groupId = formulabo.saveItem(dao,tableid,cHz,item,cfactor,this.getFrameconn());//生成的公式组id
			this.getFormHM().put("groupId",groupId);
		}else if("4".equals(opt)){//编辑公式组的时候
			String flag=formulabo.updateItem(dao,tableid, groupId, cHz);
			this.getFormHM().put("flag", flag);
		}else if("5".equals(opt)){//有效状态
		      String state = (String)this.getFormHM().get("state");
		      state = state!=null&&state.length()>0?state:"0";
            String flag=formulabo.updateValidState(dao,tableid, groupId, state);
            this.getFormHM().put("flag", flag);
        }
		
		
	}

}
