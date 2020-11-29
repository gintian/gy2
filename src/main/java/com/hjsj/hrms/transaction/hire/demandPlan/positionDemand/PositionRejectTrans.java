package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.businessobject.hire.ZpPendingtaskBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
* 
* 类名称：PositionRejectTrans   
* 类描述： 用与驳回用工需求 异步发送邮件
* Company:HJSJ   
* 创建人：zhaozk
* 创建时间：Sep 17, 2013 2:23:49 PM     
* @version    
*
 */
public class PositionRejectTrans extends IBusiness {

	public void execute() throws GeneralException {
		StringBuffer sql_whl=new StringBuffer("");
		HashMap hm=(HashMap)this.getFormHM();
		//安全平台改造，把“/”转换成了“／”所以现在要替换回来
		String tempz0301=(String)hm.get("z0301");
		tempz0301=tempz0301.replaceAll("／", "/");
		String[]  z0301s=tempz0301.split("/");
		/**安全改造,判断是否要驳回的z0301是否存在后台begin**/
		String checksql = (String) this.userView.getHm().get("hire_sql");
		int index = checksql.indexOf("order by");
		if(index!=-1){
			checksql = checksql.substring(0, index);
		}	
		checksql = checksql+" and z0301 in(";
		for(int i=0;i<z0301s.length;i++){
			String temp_Z0301=z0301s[i];
			if(i==0){
				checksql=checksql+"'"+temp_Z0301+"'";
			}else{
				checksql=checksql+",'"+temp_Z0301+"'";
			}
		}
		checksql=checksql+")";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		dao=new ContentDAO(this.getFrameconn());	
		try {
			this.frowset = dao.search(checksql);
			int count=0;
			while(this.frowset.next()){
				count++;
			}
			if(count<z0301s.length){
				throw new GeneralException(ResourceFactory.getProperty("label.hireemploye.no.contorl"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**安全改造,判断是否由要删除的z0301是否存在后台end**/
		for(int i=0;i<z0301s.length;i++)
			sql_whl.append(",'"+z0301s[i]+"'");
		String rejectCause=(String)hm.get("rejectCause");
		rejectCause=SafeCode.decode(rejectCause);
		String url_p=(String)hm.get("url_p");
		String moreLevelSP=(String)hm.get("moreLevelSP");
		String isSendMessage=(String)hm.get("isSendMessage");
		String opt=(String)hm.get("opt");
		hm.put("rejectCause", SafeCode.encode(rejectCause));
		hm.put("url_p", url_p);
		hm.put("moreLevelSP", moreLevelSP);
		hm.put("isSendMessage", isSendMessage);
		hm.put("z0301", (String)hm.get("z0301"));
		ZpPendingtaskBo zpbo = new ZpPendingtaskBo(this.frameconn, this.userView);
		try
		{
				PositionDemand pd = new PositionDemand(this.getFrameconn());
				for(int i=0;i<z0301s.length;i++)
				{
					if(z0301s[i]==null|| "".equals(z0301s[i]))
						continue;
					if("1".equals(opt)){
						if(moreLevelSP!=null&& "1".equals(moreLevelSP)){//如果选择了多级审批那么就需要判断是否是当前操作人员
							pd.checkCanOperate(z0301s[i], userView);
						}
						String targets=pd.getRejectTarget(z0301s[i]);
						String[] tar = targets.split(":");
						String target = tar[0];
						String inf = tar[1];
						String xml=pd.createXMLTarget(this.getUserView(), rejectCause, z0301s[i], "07",target);
						pd.saveXML(z0301s[i], xml);//驳回 审批流程
						//更新待办任务表数据
						zpbo.updatePendingTask(inf, getActortype(inf));
					}	
					/**opt=2 报批异步发送邮件、减少客户等待时间**/
					if("2".equals(opt)){
						if("1".equals(moreLevelSP))
						{
							if(isSendMessage==null||(isSendMessage!=null&& "0".equalsIgnoreCase(isSendMessage)))
								pd.rejectByLayer(z0301s[i], this.getUserView(), url_p);
							if(isSendMessage!=null&& "1".equalsIgnoreCase(isSendMessage)){
								pd.rejectByMessage(z0301s[i], this.getUserView());
							}
						}
					}
				}
				
				if("1".equals(opt)){
					String sql="update z03 set z0319='07',z0327=?  where z0301 in ("+sql_whl.substring(1)+")";
					ArrayList values = new ArrayList();
					values.add(rejectCause);
					/*PreparedStatement pt=this.getFrameconn().prepareStatement(sql);	
					pt.setString(1,rejectCause);
					pt.execute();*/
					dao.update(sql,values);
					//更新待办任务表
					zpbo.checkZpappr();
				}
	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		


	}

	/**
	 * 获取报批人的类型
	 * @param username 
	 * @return type 1：自助用户；2：业务用户。
	 */
	private String getActortype(String username) {
		String type = "";
		try {

			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search("select username from operuser where username='" + username + "'");
			if (this.frowset.next()) {
				type = "4";
			} else
				type = "1";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;

	}
}
