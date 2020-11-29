package com.hjsj.hrms.transaction.kq.register.sort;

import com.hjsj.hrms.businessobject.kq.register.sort.SortBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 保存排序
 * <p>
 * Title:SaveSortTrans.java
 * </p>
 * <p>
 * Description>:把选定的指标保存起来，以后每次不同的用户登录进入时，根据各自用户选定的默认排序指标对考勤日明细、月汇总信息数据排序显示。
 *				“默认排序”内容保存到kq_parameter表中
 *				B0110=UN ,Name=kq_order_登录用户名；content=选定排序指标用逗号隔开。
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2011-07-20 09:28:32
 * </p>
 * <p>
 * 
 * @version: 1.0
 *           </p>
 *           <p>
 * @author: wangzhongjun
 */
public class SaveSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		String sortitem = (String) this.getFormHM().get("sortitem");
		
		try{
			SortBo bo = new SortBo(this.frameconn, this.userView);
			if (bo.saveOrUpdateSort(sortitem.replaceAll("`", ","))) {
				if(sortitem.length() > 0) {
					this.formHM.put("msg", "默认排序创建成功！");
				} else {
					this.formHM.put("msg", "默认排序清除成功！");
				}
			} else {
				this.formHM.put("msg", "操作失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.formHM.put("msg", "操作失败！");
		}
		
	}
	
	

}
