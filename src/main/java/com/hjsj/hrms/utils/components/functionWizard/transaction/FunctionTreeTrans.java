package com.hjsj.hrms.utils.components.functionWizard.transaction;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.functionWizard.businessobject.FunctionWizardbo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：FunctionTreeTrans 
 * 类描述： 函数向导组件主页树结构
 * 创建人：zhaoxg
 * 创建时间：Oct 21, 2015 5:56:51 PM
 * 修改人：zhaoxg
 * 修改时间：Oct 21, 2015 5:56:51 PM
 * 修改备注： 
 * @version
 */
public class FunctionTreeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			String node = (String) this.getFormHM().get("node");//节点id
		    String opt=(String)this.getFormHM().get("opt");//1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
			String mode = (String)this.getFormHM().get("mode");//具体模块自定义标识 “xzgl_jsgs”具体功能点标识，xzgl_jsgs代表薪资计算公式调用
			String inforType = (String) this.getFormHM().get("inforType");//人员1，单位2，岗位3
			String type = (String) this.getFormHM().get("type");//临时变量 type：入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他
			
			ArrayList list = new ArrayList();
			String[] id = { "0", "1", "2", "3", "4", "10","11", "5", "6", "7", "8", "9" };
			if("root".equalsIgnoreCase(node)){
				String[] text = { ResourceFactory.getProperty("kq.formula.number"),
						ResourceFactory.getProperty("org.maip.str"),
						ResourceFactory.getProperty("kq.formula.date"),
						ResourceFactory.getProperty("kq.wizard.switch"),
						ResourceFactory.getProperty("org.maip.volatile"),
						ResourceFactory.getProperty("hrms.interfaces.sys.function"),
						ResourceFactory.getProperty("kq.wizard.kqfunc"),
						ResourceFactory.getProperty("kq.wizard.chlang"),
						ResourceFactory.getProperty("kq.wizard.boolen"),
						ResourceFactory.getProperty("kq.wizard.number"),
						ResourceFactory.getProperty("kq.wizard.option"),
						ResourceFactory.getProperty("kq.wizard.other") };
				String[] images = { "/images/bm.gif", "/images/bm2.gif",
						"/images/bm1.gif", "/images/bm3.gif", "/images/bm4.gif",
						"/images/bm10.gif","/images/bm10.gif", "/images/bm5.gif", "/images/bm6.gif",
						"/images/bm7.gif", "/images/bm8.gif", "/images/bm9.gif" };
				HashMap map = null;
				for(int i=0;i<id.length;i++){
					if (i == 5) {
						if (opt!=null&&!"1".equalsIgnoreCase(opt)
								&& !"2".equalsIgnoreCase(opt)&& !"5".equalsIgnoreCase(opt)) {
							continue;
						}
					}
					//考勤函数
					if (i == 6) {
						if("2".equals(inforType) || "3".equals(inforType))
							continue;
						
						if(StringUtils.isNotBlank(mode) && !"rsyd_jsgs".equalsIgnoreCase(mode))
							continue;
					}
					map = new HashMap();
					map.put("id", id[i]);
					map.put("text", text[i]);
					map.put("icon", images[i]);
					list.add(map);
				}
			}else if(isHaveId(id,node)){
				FunctionWizardbo bo = new FunctionWizardbo(this.frameconn,this.userView);
				list = bo.outMainpTree(node, opt, mode, type);
			}
			this.getFormHM().put("data", list);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 判断id是否满足第一层id（目前组件只能展开两层）
	 * @param id
	 * @param node
	 * @return
	 */
	private boolean isHaveId(String[] id,String node){
		boolean flag = false;
		try{
			for(int i=0;i<id.length;i++){
				if(id[i].equalsIgnoreCase(node)){
					flag = true;
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
}
