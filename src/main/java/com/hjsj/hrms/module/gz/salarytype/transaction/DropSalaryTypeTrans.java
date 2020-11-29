package com.hjsj.hrms.module.gz.salarytype.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 项目名称 ：ehr7.x
 * 类名称：AddSalaryTypeTrans
 * 类描述：新增工资类别
 * 创建人： lis
 * 创建时间：2015-11-12
 */
public class DropSalaryTypeTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			String ori_id = (String) this.getFormHM().get("ori_id");
			ori_id = PubFunc.decrypt(SafeCode.decode(ori_id));
			String to_id = (String) this.getFormHM().get("to_id");
			to_id = PubFunc.decrypt(SafeCode.decode(to_id));
			String to_seq = (String) this.getFormHM().get("to_seq");
			String ori_seq = (String) this.getFormHM().get("ori_seq");
			String dropPosition = (String) this.getFormHM().get("dropPosition");
			if(StringUtils.isBlank(ori_seq))
				ori_seq = "1";
			if(StringUtils.isBlank(to_seq))
				to_seq = "1";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer str = new StringBuffer();		
			ArrayList list = new ArrayList();
			String direction = "";
			
			if(Integer.valueOf(ori_seq) > Integer.valueOf(to_seq))
				direction = "up";
			else if(Integer.valueOf(ori_seq) < Integer.valueOf(to_seq))
				direction = "down";
			else if(Integer.valueOf(ori_id) < Integer.valueOf(to_id))
				direction = "up";
			else if(Integer.valueOf(ori_id) > Integer.valueOf(to_id))
				direction = "down";
			
			if("up".equals(direction)){//上移
				//将上移对象的seq替换成目标对象的
				str.append("update salarytemplate set seq=? where salaryid=?");
				list.add(to_seq);
				list.add(ori_id);
				dao.update(str.toString(),list);
				str.setLength(0);
				list = new ArrayList();
				list.add(to_seq);
				list.add(ori_seq);
				//在移动对象和目标对象之间的对象seq都加1
				if("before".equals(dropPosition)){//extjs拖拽时，移动对象相对目标对象是在其上
					str.append("update salarytemplate set seq = seq+1 where seq>=? and seq<=?  and salaryid<>?");
					list.add(ori_id);
				}else{
					str.append("update salarytemplate set seq = seq+1 where seq>=? and seq<=?  and salaryid<>?");
					list.add(to_id);
				}
				
				dao.update(str.toString(),list);
			}else if("down".equals(direction)){//下移
				//将下移对象的seq替换成目标对象的
				str.append("update salarytemplate set seq =? where salaryid=?");
				list.add(to_seq);
				list.add(ori_id);
				dao.update(str.toString(),list);
				str.setLength(0);
				list = new ArrayList();
				list.add(ori_seq);
				list.add(to_seq);
				//在移动对象和目标对象之间的对象seq都减1.
				if("after".equals(dropPosition)){//extjs拖拽时，移动对象相对目标对象是在其上
					str.append("update salarytemplate set seq = seq-1 where seq>=? and seq<=?  and salaryid<>?");
					list.add(ori_id);
				}else{
					str.append("update salarytemplate set seq = seq-1 where seq>=? and seq<=?  and salaryid<>?");
					list.add(to_id);
				}
				dao.update(str.toString(),list);
			}
			//获取更新后收影响的seq列表
			list.remove(list.size()-1);
			str.setLength(0);
			str.append("select seq,salaryid from salarytemplate where seq>=? and seq<=?");
			ArrayList data=new ArrayList();
			LazyDynaBean bean=new LazyDynaBean();
			RowSet rs=dao.search(str.toString(),list);
			while(rs.next()){
				bean=new LazyDynaBean();
				bean.set("salaryid", rs.getString("salaryid"));
				bean.set("seq", rs.getString("seq"));
				data.add(bean);
			}
			this.getFormHM().put("data", data);

			//同步修改前端数据
			TableDataConfigCache cache = (TableDataConfigCache)userView.getHm().get("salaryType");
			LazyDynaBean oriBean=null;
			int toNum=-1,forNum=-1;
			if(cache.getTableData().size()>0){
				ArrayList<LazyDynaBean> dataList=cache.getTableData();
				for(int i=0;i<dataList.size();i++){
					LazyDynaBean tempBean=dataList.get(i);
					if(tempBean.get("salaryid").equals(ori_id)){
						oriBean=tempBean;
						forNum=i;
					}
					if(tempBean.get("salaryid").equals(to_id))
						toNum=i;
				}
				if(oriBean!=null){
					if(forNum<toNum)
						toNum--;
					if("after".equalsIgnoreCase(dropPosition))
						toNum++;
					dataList.remove(oriBean);
					dataList.add(toNum,oriBean);
				}
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
}
