package com.hjsj.hrms.transaction.org.funwd;

import com.hjsj.hrms.businessobject.sys.org.ProjectSet;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
  */
public class FunctionWizardTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM();
		HashMap reqhm = (HashMap) hm.get("requestPamaHM");
		ProjectSet projectset = new ProjectSet();
		String flag = (String)reqhm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		String planid  = (String)reqhm.get("planid");
		planid =planid !=null&&planid.trim().length()>0?planid:"";
		String checktemp  = (String)reqhm.get("checktemp");
		checktemp =checktemp !=null&&checktemp.trim().length()>0?checktemp:"";
		
		ArrayList alist = aList(flag);
		ArrayList dlist = dList(flag);
		ArrayList nlist = nList(flag,planid);
		ArrayList vlist = new ArrayList();
		vlist.addAll(nlist);
		vlist.addAll(alist);
		
		hm.put("alist",alist);
		hm.put("dlist",dlist);
		hm.put("nlist",nlist);
		hm.put("vlist",vlist);
		hm.put("datestr","");
		functionListunit();
		functionListpos();
		this.getFormHM().put("checktemp",checktemp);
		this.getFormHM().put("standlist",new ArrayList());
		this.getFormHM().put("standidlist",new ArrayList());
		
		this.getFormHM().put("itemlist",standList());
		this.getFormHM().put("codearr","");
		this.getFormHM().put("strarr","");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList standlist = projectset.standList(dao,this.userView);
		ArrayList standidlist = projectset.standidList(dao,this.userView);
//		this.getFormHM().put("standlist",standlist);
//		this.getFormHM().put("standidlist",standidlist);
		this.getFormHM().put("statlist",projectset.condList());
		this.getFormHM().put("rangelist",projectset.rangeList());
	}
	private void functionListunit(){
		ArrayList listset = new ArrayList();
		ArrayList fieldsetlistunit = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlistunit.add(obj1);
			listset = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
			for(int i=0;i<listset.size();i++){
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				 CommonData obj=new CommonData(fieldset.getFieldsetid()
							,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				 fieldsetlistunit.add(obj);
			}
			this.getFormHM().put("fieldsetlistunit",fieldsetlistunit);
	}
	private void functionListpos(){
		ArrayList listset = new ArrayList();
		ArrayList fieldsetlistpos = new ArrayList();
		 CommonData obj1=new CommonData("","");
		 fieldsetlistpos.add(obj1);
		listset = this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
			for(int i=0;i<listset.size();i++){
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				 CommonData obj=new CommonData(fieldset.getFieldsetid()
							,fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				 fieldsetlistpos.add(obj);
			}
			this.getFormHM().put("fieldsetlistpos",fieldsetlistpos);
	}
	/**
	 * 
	 * @param flag　1.绩效评估（绩效系数）
	 * @return
	 */
	private ArrayList aList(String flag){
		ArrayList alist = new ArrayList();
		if("2".equals(flag)){
			CommonData obj = new CommonData("", "");
			 alist.add(obj);
			 obj=new CommonData("计划名称:计划名称","计划名称");
			 alist.add(obj);
			 obj=new CommonData("单位名称:单位名称","单位名称");
			 alist.add(obj);
			 obj=new CommonData("部门名称:部门名称","部门名称");
			 alist.add(obj);
			 obj=new CommonData("职位名称:职位名称","职位名称");
			 alist.add(obj);
			 obj=new CommonData("姓名:姓名","姓名");
			 alist.add(obj);
			 obj=new CommonData("人员编号:人员编号","人员编号");
			 alist.add(obj);
		}else{
		 CommonData obj=new CommonData("所属部门:所属部门","所属部门");
		 alist.add(obj);
		 obj=new CommonData("对象类别:对象类别","对象类别");
		 alist.add(obj);
		}
		return alist;
	}
	/**分组指标
	 * 
	 */
	private ArrayList standList(){
		ArrayList alist = new ArrayList();
		CommonData obj = new CommonData("", "");
		 alist.add(obj);
		 obj=new CommonData("本次得分","本次得分");
		 alist.add(obj);
		 obj=new CommonData("总分","总分");
		 alist.add(obj);
		return alist;
	}
	/**
	 * 
	 * @param flag　1.绩效评估（绩效系数）
	 * @return
	 */
	private ArrayList dList(String flag){
		ArrayList alist = new ArrayList();
		return alist;
	}
	/**
	 * 
	 * @param flag　1.绩效评估（绩效系数）
	 * @return
	 */
	private ArrayList nList(String flag,String planid){
		ArrayList alist = new ArrayList();
		if("1".equals(flag)){
			CommonData obj = new CommonData("", "");
			 alist.add(obj);
			 obj=new CommonData("总分:总分","总分");
			alist.add(obj);
//			 obj=new CommonData("所属部门:所属部门","所属部门");
//			 alist.add(obj);
//			 obj=new CommonData("对象类别:对象类别","对象类别");
//			 alist.add(obj);
			 obj=new CommonData("本次得分:本次得分","本次得分");
			alist.add(obj);
			if(!"".equals(planid)){
				LoadXml loadxml = new LoadXml(this.frameconn,planid,"");
				ArrayList planlist = loadxml.getRelatePlanValue("Plan","ID");
				ContentDAO dao = new ContentDAO(this.frameconn);
				StringBuffer sql = new StringBuffer();
				sql.append("select * from per_plan ");
				if(planlist.size()>0){
					sql.append(" where plan_id in (");
					for(int i=0;i<planlist.size();i++){
						sql.append("'"+planlist.get(i).toString().trim()+"',");
					}
					sql.setLength(sql.length()-1);
					sql.append(")");
					try {
						this.frowset = dao.search(sql.toString());
						while(this.frowset.next()){
							
							obj=new CommonData("G_"+this.frowset.getInt("plan_id")+":"+this.frowset.getString("name")+".得分",this.frowset.getString("name")+".得分");
							alist.add(obj);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
		}
		if("2".equals(flag)){
		}
		return alist;
	}
	
}
