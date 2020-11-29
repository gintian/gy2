package com.hjsj.hrms.utils.components.definetempvar.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.definetempvar.businessobject.DefineTempVarBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 项目名称 ：ehr
 * 类名称：InitDefineTempVar
 * 类描述：弹出临时变量窗口的数据初始化
 * 创建人： lis
 * 创建时间：2015-10-30
 */
public class InitDefineTempVar extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String id = (String)hm.get("id"); //薪资类别id,人事异动模版id
		id=id!=null&&id.length()>0?id:"";
		String module = (String)hm.get("module"); //模块号，1是薪资
		String type = (String)hm.get("type");//1是薪资类别
		type = type!=null&&type.length()>0?type:"";
		String flag = (String)hm.get("flag");//1:返回临时变量数据集合  2：返回临时变量公式；   一个类分开处理   放置多次执行数据库查询
		String nflag = (String)hm.get("nflag");//0：工资发放|保险核算，2：报表，1：工资变动或者日常管理
		nflag=nflag!=null&&nflag.trim().length()>0?nflag:"0";
		String infor_type = (String)hm.get("infor_type");
		DefineTempVarBo varBo = new DefineTempVarBo(this.getFrameconn(), this.userView);
		try {
			if("1".equals(module)){
				if("1".equals(type)){
					id = PubFunc.decrypt(SafeCode.decode(id));//解码和解密
					if("1".equals(flag)){//返回临时变量数据集合
						ArrayList list = new ArrayList();				
						list = varBo.initTempVar(id, nflag);
						hm.put("data", list);	
					}
					
				}else if("3".equals(type)){//人事异动,gaohy，2016-1-6
					if("1".equals(flag)){//返回临时变量数据集合
						ArrayList list = new ArrayList();				
						list = varBo.initTemplateVar(id);
						hm.put("data", list);	
					}
				}
				
				if("2".equals(flag)){//返回临时变量公式
					String nid = (String)hm.get("nid");
					String cValue = varBo.cValue(nid);
					hm.put("cValue", cValue);
				}
				else if("3".equals(flag)){//返回子集数据集合
					ArrayList fieldsetlist = varBo.fieldListTemp(nflag,infor_type);
					hm.put("fieldsetlist",fieldsetlist);
				}
				else if("4".equals(flag)){//，子集下拉框选择子集或，返回子集指标数据集合
					String fieldsetid = (String)hm.get("fieldsetid");//子集id
					fieldsetid = fieldsetid!=null&&fieldsetid.length()>0?fieldsetid:"";
					
					if("tempvar".equalsIgnoreCase(fieldsetid)){//当下拉框选中的是临时变量
						hm.put("itemlist",varBo.itemList(type,id));
					}else{
						hm.put("itemlist",varBo.getItemList(fieldsetid,this.userView));
					}
				}
				else if("5".equals(flag)){//代码类数据集合
					hm.put("codelist",varBo.codeList());
				}
				else if("6".equals(flag)){//代码类数据集合
					String codeid = (String)hm.get("codeid");//子集id
					codeid = codeid!=null&&codeid.length()>0?codeid:"";
					hm.put("codeItemlist",varBo.codeListItem(codeid));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
