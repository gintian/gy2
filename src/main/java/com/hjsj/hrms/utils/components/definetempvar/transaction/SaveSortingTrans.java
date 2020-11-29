package com.hjsj.hrms.utils.components.definetempvar.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 项目名称 ：ehr
 * 类名称：SaveSortingTrans
 * 类描述：保存排序
 * 创建人： lis
 * 创建时间：2015-10-30
 */
public class SaveSortingTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			String ori_nid = (String) this.getFormHM().get("ori_nid");//移动数据id
			String ori_seq = (String) this.getFormHM().get("ori_seq");//移动数据排序号
			String to_nid = (String) this.getFormHM().get("to_nid");//目标数据id
			String to_seq = (String) this.getFormHM().get("to_seq");//目标数据排序号
			
			String nflag = (String)getFormHM().get("nflag");//0：工资发放|保险核算，2：报表，1：工资变动或者日常管理
			nflag=nflag!=null&&nflag.trim().length()>0?nflag:"0";//人事异动的只有0
			String type = (String)this.getFormHM().get("type");//1是薪资类别，3是人事异动
			
			String cstate = (String) this.getFormHM().get("cstate");//薪资类别id
			if(!"3".equals(type))//人事异动不需要加密和解密,gaohy	,2016-1-6
			cstate = PubFunc.decrypt(SafeCode.decode(cstate));
			
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer str = new StringBuffer();		
			ArrayList list = new ArrayList();
			
			String dropPosition = "";
			if(Integer.valueOf(ori_seq) > Integer.valueOf(to_seq))
				dropPosition = "before";
			else if(Integer.valueOf(ori_seq) < Integer.valueOf(to_seq))
				dropPosition = "after";
			else if(Integer.valueOf(ori_nid) < Integer.valueOf(to_nid))//当目标排序号和移动的相同时根据id判断排序
				dropPosition = "before";
			else if(Integer.valueOf(ori_nid) > Integer.valueOf(to_nid))
				dropPosition = "after";
			
			//将移动对象的seq替换成目标对象的
			str.append("update midvariable set sorting =? where nid=?");
			list.add(to_seq);
			list.add(ori_nid);
			dao.update(str.toString(),list);
			
			str.setLength(0);
			list = new ArrayList();
			if("before".equals(dropPosition)){//上移
				//在移动对象和目标对象之间的对象seq都加1.
				if("1".equals(type)){
					str.append("update midvariable set sorting = sorting+1 where sorting>=? and sorting<=? and  templetid=0 and(cstate=? or cstate is null) and nflag=? and nid<>?");
				}else if("3".equals(type)){
					str.append("update midvariable set sorting = sorting+1 where sorting>=? and sorting<=? and  templetid<>0 and(templetId = ? or cstate = '1') and nflag=? and nid<>?");
				}
				list.add(to_seq);
				list.add(ori_seq);
				list.add(cstate);
				list.add(nflag);
				list.add(ori_nid);
				dao.update(str.toString(),list);
			}else if("after".equals(dropPosition)){//下移
				//在移动对象和目标对象之间的对象seq都减1.
				if("1".equals(type)){
					str.append("update midvariable set sorting = sorting-1 where sorting>=? and sorting<=? and  templetid=0 and(cstate=? or cstate is null) and nflag=? and nid<>?");
				}else if("3".equals(type)){
					str.append("update midvariable set sorting = sorting-1 where sorting>=? and sorting<=? and  templetid<>0 and(templetId = ? or cstate = '1') and nflag=? and nid<>?");
				}
				list.add(ori_seq);
				list.add(to_seq);
				list.add(cstate);
				list.add(nflag);
				list.add(ori_nid);
				dao.update(str.toString(),list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		
	}

}
