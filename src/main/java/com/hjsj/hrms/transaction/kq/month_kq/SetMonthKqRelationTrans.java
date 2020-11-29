package com.hjsj.hrms.transaction.kq.month_kq;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 进入参数设置
 * <p>Title:SetMonthKqRelationTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author jinjiawei
 * @version 1.0
 * */
public class SetMonthKqRelationTrans extends IBusiness{

	public void execute() throws GeneralException {
		ArrayList list = this.getRelationsInfo();
		
		ArrayList list2 = this.getCodeItemDesc();
		
		MonthKqBo mBo = new MonthKqBo(this.frameconn);
	
		ConstantXml constant = new ConstantXml(this.frameconn, "kq_monthly");
		//String defValue = constant.getNodeAttributeValue("/param/Kq_Parameters",
		//		"def_value");
		String defValue = mBo.getParam();
		String relation = mBo.getParam1();
		//String relation =  constant.getNodeAttributeValue("/param/Kq_Parameters",
		//"sp_relation");
	//	if("".equals(defValue.trim()) || null == defValue){
	//		defValue = "请选择...";
	///	}
	//	if("".equals(relation.trim()) || null == relation){
	//		defValue = "请选择...";
	//	}
		this.getFormHM().put("defValue", defValue);
		this.getFormHM().put("relation", relation);
		this.getFormHM().put("setRelationList", list);
		this.getFormHM().put("codeItemList", list2);
		this.getFormHM().put("kqdefValue", getImgInfoById(defValue));
	//	this.getFormHM().clear();
	}
	
	//得到审批关系放入集合
	public ArrayList getRelationsInfo(){
		String sql = "select * from t_wf_relation";
		ArrayList list = new ArrayList();
		MonthKqBean beans = null;
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			//beans = new MonthKqBean();
			///beans.setItemid("select");
			//beans.setItemdesc("请选择...");
			//list.add(beans);
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				beans = new MonthKqBean();
				if(null != this.frowset.getString("relation_id")
						&& null != this.frowset.getString("cname")){
				beans.setItemid(this.frowset.getString("relation_id"));
				beans.setItemdesc(this.frowset.getString("cname"));
				list.add(beans);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	//得到考勤项目放入集合
	public ArrayList getCodeItemDesc(){
		String sql = "select * from codeitem where codesetid = '27' and invalid = '1'";
		ArrayList list = new ArrayList();
		MonthKqBean beans = null;
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
		//	beans = new MonthKqBean();
		//	beans.setItemid("select");
		//	beans.setItemdesc("请选择...");
		//	list.add(beans);
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				beans = new MonthKqBean();
				if(null != this.frowset.getString("codeitemid")
						){
					beans.setItemid(this.frowset.getString("codeitemid"));		
					if(null != this.frowset.getString("corcode")){						
						beans.setItemdesc(this.frowset.getString("corcode") + this.frowset.getString("codeitemdesc"));
					}else{
						beans.setItemdesc(this.frowset.getString("codeitemdesc"));
					}
					//System.out.println(this.getImgInfoById(this.frowset.getString("codeitemid")));
					list.add(beans);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//保存完之后取得考勤对应的图标 动态在前台显示 实现无刷新页面动态变更前台展示效果
	public String getImgInfoById(String codeitemid){
		String sql = "select codeitemdesc from codeitem where codesetid='27' and codeitemid = '"+codeitemid+"'";
		String imgInfo = "";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				imgInfo = this.frowset.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imgInfo;
	}
	
	
}
