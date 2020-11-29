package com.hjsj.hrms.transaction.train.trainCosts;

import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训费用</p>
 * <p>Description:显示培训费用</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class TrainCostsInforTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String r2501 = (String)hm.get("r2501");
		r2501=r2501!=null?r2501:"";
		hm.remove("r2501");
		
		String flag = (String)hm.get("flag");
		flag=flag!=null?flag:"1";
		hm.remove("flag");
		
		if("1".equals(flag))
			viewCostsData(r2501);
		else
			viewCostsChart(r2501);
		this.getFormHM().put("r2501",r2501);
	}
	private void viewCostsData(String r2501){
		ArrayList list = new ArrayList();
		ArrayList fieldlist = DataDictionary.getFieldList("r45",Constant.USED_FIELD_SET);//this.userView.getPrivFieldList("r45");
		StringBuffer buf = new StringBuffer();
		StringBuffer wherestr = new StringBuffer();
		StringBuffer columns = new StringBuffer();
		buf.append("select ");
		for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			
			if("r4502".equalsIgnoreCase(fielditem.getItemid())){
				buf.append("(select R2502 from R25 where R2501=r45.R4502) as "+fielditem.getItemid());
				fielditem.setCodesetid("0");
				fielditem.setItemdesc(ResourceFactory.getProperty("lable.zp_plan.name"));
			}else
				buf.append(fielditem.getItemid());
			columns.append(fielditem.getItemid());
			buf.append(",");
			columns.append(",");
			if("r4501".equalsIgnoreCase(fielditem.getItemid())){
				list.add(0,fielditem);
			}else
				list.add(fielditem);
		}
		
		wherestr.append(" from r45 where r4502='");
		TrainPlanBo bo = new TrainPlanBo(this.frameconn);
		if(bo.checkPlanPiv(r2501, this.userView))
		    wherestr.append(r2501);
		wherestr.append("'");
		this.getFormHM().put("setlist",list);
		this.getFormHM().put("sql",buf.substring(0,buf.length()-1));
		this.getFormHM().put("wherestr",wherestr.toString());
		this.getFormHM().put("columns",columns.substring(0,columns.length()-1));
	}
	private void viewCostsChart(String r2501){
		ArrayList list = new ArrayList();
		ContentDAO dao  = new ContentDAO(this.frameconn);
		ArrayList fieldlist = DataDictionary.getFieldList("r45",Constant.USED_FIELD_SET);//this.userView.getPrivFieldList("r45");
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		for (int i = 0; i < fieldlist.size(); i++) {
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			if(fielditem.getItemdesc().equals(ResourceFactory.getProperty("train.b_plan.total.costs")))
				continue;
			if("N".equalsIgnoreCase(fielditem.getItemtype())){
				sql.append("sum("+fielditem.getItemid()+") "+fielditem.getItemid()+",");
			}
		}
		sql.setLength(sql.length()-1);
		sql.append(" from r45 where r4502='");
		TrainPlanBo bo = new TrainPlanBo(this.frameconn);
		if(bo.checkPlanPiv(r2501, this.userView))
		    sql.append(r2501);
		sql.append("'");
		//System.out.println(sql);
		try{
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next()){
				for(int i=0;i<fieldlist.size();i++){
					FieldItem fielditem = (FieldItem)fieldlist.get(i);
					if(fielditem.getItemdesc().equals(ResourceFactory.getProperty("train.b_plan.total.costs")))
						continue;
					if("N".equalsIgnoreCase(fielditem.getItemtype())){
						CommonData vo =new CommonData();
						vo.setDataName(fielditem.getItemdesc());
						vo.setDataValue(String.valueOf(this.frowset.getFloat(fielditem.getItemid())));
						list.add(vo);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		统计分析显示的每个费用单个比例 应以类型拉分 LiWeichao 注
//		StringBuffer buf = new StringBuffer();
//		buf.append("select * from r45 where r4502='");
//		buf.append(r2501);
//		buf.append("'");
//		try {
//			this.frowset = dao.search(buf.toString());
//			while(this.frowset.next()){
//				for(int i=0;i<fieldlist.size();i++){
//					FieldItem fielditem = (FieldItem)fieldlist.get(i);
//					if(fielditem.getItemdesc().equals(ResourceFactory.getProperty("train.b_plan.total.costs")))
//						continue;
//					if(fielditem.getItemtype().equalsIgnoreCase("N")){
//						CommonData vo =new CommonData();
//						vo.setDataName(fielditem.getItemdesc());
//						vo.setDataValue(String.valueOf(this.frowset.getFloat(fielditem.getItemid())));
//						list.add(vo);
//					}
//				}
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		this.getFormHM().put("setlist",list);
	}
}
