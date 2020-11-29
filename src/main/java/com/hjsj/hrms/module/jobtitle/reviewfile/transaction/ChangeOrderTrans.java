package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title: ChangeOrderTrans </p>
 * <p>Description : 公示材料改变右侧顺序 </p>
 * <p>Company: hjsj</p>
 * <p>create time  2017-3-29 下午5:52:24</p>
 * @author sunj
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ChangeOrderTrans  extends IBusiness{

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		String list = (String)this.getFormHM().get("list");//noticefielditems节点的值
		String type = (String)this.getFormHM().get("type");//type为1表示新增或者修改Str_value的值，type为2时表示查询Str_value的节点的值
		ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
		String msg = reviewFileBo.changeConstant(list, type);
		this.getFormHM().put("msg",msg);
		
		/*
		 * 由于职称公告通知对象为机构时，希望能按照“业务范围>操作单位>人员范围”控制权限，但是选人控件无法传入模块id,就没办法控制业务范围的权限
		 * 所以，在这里查到机构权限的ID，传导选人控件，选人控件直接根据orgid 来控制权限
		 */
		String unitIdByBusi = this.userView.getUnitIdByBusi("9");//取得所属单位
		StringBuffer unitIds = new StringBuffer();
		if(unitIdByBusi.split("`")[0].length() > 2){// 不是最高权限
			String[] b0110Array = unitIdByBusi.split("`");
			for(int i=0; i<b0110Array.length; i++){
				String id = b0110Array[i].substring(2);
				unitIds.append(id);
				if(i<b0110Array.length-1)
					unitIds.append(",");
			}
		} 
		if("1".equals(type)){//新增/修改时，把当前用户的业务范围取出，做为公示通知对象 chent 20170421
			ArrayList<HashMap<String, String>> unitIdByBusiList = new ArrayList<HashMap<String, String>>();
			if(unitIdByBusi.split("`")[0].length() > 2){// 不是最高权限
				String[] b0110Array = unitIdByBusi.split("`");
				for(int i=0; i<b0110Array.length; i++){
					
					String id = b0110Array[i].substring(2);
					String orgpre = b0110Array[i].substring(0, 2);
					String name = AdminCode.getCodeName(orgpre, id);
					String type1 = "org";
					
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("id", PubFunc.encrypt(id));
					map.put("name", name);
					map.put("type", type1);
					map.put("orgpre", orgpre);
					unitIdByBusiList.add(map);
				}
			} else {// 最高权限
				// 不处理
			}
			this.getFormHM().put("unitIdByBusiList", unitIdByBusiList);
		}
		this.getFormHM().put("unitIds", unitIds.toString());
	}

}
