package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：InitCondFormulaTrans 
 * 类描述：初始化计算公式列表
 * 创建人：zhaoxg
 * 创建时间：Jun 5, 2015 10:20:54 AM
 * 修改人：zhaoxg
 * 修改时间：Jun 5, 2015 10:20:54 AM
 * 修改备注： 
 * @version
 */
public class InitCondFormulaTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();

		
		String id = (String)hm.get("id"); //薪资类别id 或者 人事异动模版Id
		id=id!=null&&id.length()>0?id:"";
		
		String groupid = (String)hm.get("groupid");//人事异动公式组id
		groupid = groupid!=null&&groupid.length()>0?groupid:"";
		
		String actflag = (String)hm.get("actflag");//人事异动-行为标识，新增、修改、调整、删除等
		actflag = actflag!=null&&actflag.length()>0?actflag:"";
		
		String formulaType = (String)hm.get("formulaType");//公式类别，1:计算公式  2：审核公式/校验公式
		String module = (String)hm.get("module");//模块号，1：薪资  2：薪资总额  3：人事异动  4:考勤假期管理  5...其他
		String flag = (String)hm.get("flag");//1:返回fields  2：返回数据list   一个类分开处理   放置多次执行数据库查询
		
		/** 此处的fields用来定义数据列，useflag代表状态 hzname代表名称  
		 * runflag是薪资模块采用的执行情况 itemid是公式主键id itemname是公式对应字段
		 * 除runflag外其他四项是必须的
		 **/
		String[] fields=null;
		if("1".equals(module)){//薪资
			fields = new String[]{"useflag","hzname","runflag","itemid","itemname","seq"};//默认显示
		}else if("3".equals(module)){//人事异动
			fields = new String[]{"hzname","itemname","seq"};//默认显示
		} else if("4".equals(module))//考勤假期管理
		    fields = new String[]{"hzname","itemname"};//默认显示	
		
		if("2".equals(formulaType)){//审批公式要显示的列头
			fields = new String[]{"spname","validflag","itemid","information","seq"};
		} 	
		
		DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
		try {
			if("1".equals(flag)){
				hm.put("fields", fields);
			}else{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				ArrayList list = new ArrayList();	
				if("2".equals(formulaType)){//审批公式数据
					if("1".equals(module))
						id = PubFunc.decrypt(SafeCode.decode(id));//解码和解密
					list = bo.getGzSpFormulaList(id, fields,module);
			    }else if("1".equals(module)){
					id = PubFunc.decrypt(SafeCode.decode(id));//解码和解密
					list = bo.initGzFormula(id, fields);
				}else if("3".equals(module)){//人事异动
					list = bo.initGzFormulaTemp( id, groupid, fields);
				} else if("4".equals(module)){
					//考勤假期管理
                    list = bo.getKqFormulaTemp();
                }
				hm.put("data", list);	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
