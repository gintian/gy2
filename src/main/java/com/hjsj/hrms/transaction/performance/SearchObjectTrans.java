/*
 * 创建日期 2005-6-25
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.performance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * @author luangaojiong
 * 
 * 搜索考核对象列表
 *  
 */
public class SearchObjectTrans extends IBusiness {

	String id = "0"; //计划号

	public void execute() throws GeneralException {
		
		//if(this.userView.getStatus()!=4)
		//{
			//throw new GeneralException("","非自助平台用户不能使用该功能!","","");
			
		//}
		
		this.getFormHM().put(" strSQL2","select plan_id,name from per_plan where status=? and plan_id in  (select plan_id from per_mainbody where  mainbody_id='"+this.userView.getA0100()+"') and gather_type=0");
		/**
		 * 调用计划执行
		 */
		doPlanId();
		
	}

	/**
	 * 处理计划号
	 * 
	 * @return
	 */
	void doPlanId() {
		/**
		 * 得到提交的planId号
		 */
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");

		if (hm.get("planId") != null) {
			id = hm.get("planId").toString();
		}
		else
		{
			id="0";
		}
		
		/**
		 * 第一次进入标记
		 */
		String planFlag="0";
		if(hm.get("planFlag")==null)
		{
			//System.out.println("---->planFlag is null");
			planFlag="0";
			String strSQL="SELECT per_mainbody.object_id, per_object.A0101 FROM per_mainbody, per_object where per_mainbody.object_id = per_object.object_id and 1>2";		//SQL语句
			this.getFormHM().put("strSQL",strSQL );
		}
		else
		{
			planFlag= hm.get("planFlag").toString();
		}

		if ("0".equals(planFlag)) {
			this.getFormHM().put("planNum", "");	//清计划
			this.getFormHM().put("objectId","");	//清对象
			this.getFormHM().put("outHtml","");		//清输出的内容
			String strSQL="SELECT per_mainbody.object_id, per_object.A0101 FROM per_mainbody, per_object where per_mainbody.object_id = per_object.object_id and 1>2";		//SQL语句
			this.getFormHM().put("strSQL",strSQL );
			
		} else {
		/**
		 * 没有计划id号传过来
		 */
	
		if ("0".equals(id)) {

			this.getFormHM().put("planNum", "0");
			this.getFormHM().put("userId", "0");
			this.getFormHM().put("outHtml", "");
			String strSQL="SELECT per_mainbody.object_id, per_object.A0101 FROM per_mainbody, per_object where per_mainbody.object_id = per_object.object_id and 1>2";		//SQL语句
			this.getFormHM().put("strSQL",strSQL );
			return;
		} else {
			/**
			 * 有计划id号传过来
			 */
			this.getFormHM().put("objectId","");
			this.getFormHM().put("outHtml", "");
			this.getFormHM().put("userId", this.userView.getA0100());
			this.getFormHM().put("planNum", id);

			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT per_mainbody.object_id, per_object.A0101 FROM per_mainbody,per_object where per_mainbody.object_id = per_object.object_id  and per_mainbody.plan_id='");
				sb.append(id);
				sb.append("' and per_mainbody.mainbody_id='");
				sb.append(this.userView.getA0100());
				sb.append("' and per_mainbody.object_id <>'");
				sb.append(this.userView.getA0100());
				sb.append("' group by per_mainbody.object_id, per_object.A0101 order by per_mainbody.object_id");
				
				//System.out.println("-------->com.hjsj.hrms.transaction.performance.SearchObjectTrans--->doPlanId()-->sql-->"+sb.toString());
				
				
				this.getFormHM().put("strSQL",sb.toString());
						
				
			} catch (Exception ex) {
				//System.out.println("-------->com.hjsj.hrms.transaction.performance.SearchObjectTrans--->doPlanId()-->error");
				ex.printStackTrace();
			}
		 }
		}
		
	}

}