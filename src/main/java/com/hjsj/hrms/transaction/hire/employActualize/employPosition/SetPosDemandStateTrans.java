package com.hjsj.hrms.transaction.hire.employActualize.employPosition;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SetPosDemandStateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(hm!=null&&hm.get("b_opt")!=null&& "opt".equals((String)hm.get("b_opt")))//这代表来着招聘岗位页面上的那几个操作按钮
			{
				String opt=(String)hm.get("opt");   //1:发布  2：暂停  3：删除 4:结束
				String b_opt=(String)hm.get("b_opt");
				ArrayList posDemandlist=(ArrayList)this.getFormHM().get("selectedlist");
				
				if(posDemandlist.size()>0)
				{
					StringBuffer whl_str=new StringBuffer("");
					StringBuffer whl_str2=new StringBuffer("");
					StringBuffer zpt_whl=new StringBuffer("");
					StringBuffer z04buf=new StringBuffer();
					for(Iterator t=posDemandlist.iterator();t.hasNext();)
		        	{
		        		LazyDynaBean a=(LazyDynaBean)t.next();
		        		String z0301 = a.get("z0301").toString();
		        		whl_str.append(" or z0301='" + z0301 + "'");
		        		whl_str2.append(" or zp_pos_id='" + z0301 +  "'");
		        		zpt_whl.append(" or zp_pos_id='" + z0301 + "'");
		        		z04buf.append(" or z0407='" + z0301 + "'");
		        	}
					
					if("3".equalsIgnoreCase(opt))
					{
						dao.delete("delete from z03 where "+whl_str.substring(3),new ArrayList());	
						dao.delete("delete from z04 where "+z04buf.substring(3), new ArrayList());
						dao.delete("delete from zp_pos_tache where "+zpt_whl.substring(3),new ArrayList());
						
					}
					else
					{				
						String state="";
						if("1".equals(opt))
							state="04";
						else if("2".equals(opt))
							state="09";
						else if("4".equals(opt))
							state="06";
						dao.update("update z03 set z0319='"+state+"' where "+whl_str.substring(3));
						//删除职位下所有的申请记录
						/*if(opt.equals("4"))
						{
							dao.delete("delete from zp_pos_tache where "+whl_str2.substring(3),new ArrayList());
						}*/
						/**需求结束后，将对应的订单结束*/
						if("4".equals(opt))
						{
							dao.update("update z04 set z0410='1' where "+z04buf.substring(3));
						}
						if("1".equals(opt))
						{
							PositionDemand bo = new PositionDemand(this.getFrameconn());
							ArrayList z03list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
							ArrayList z04list=DataDictionary.getFieldList("Z04",Constant.USED_FIELD_SET);
							for(int i=0;i<posDemandlist.size();i++)
							{
								LazyDynaBean bean =(LazyDynaBean)posDemandlist.get(i);
								//String z0301=PubFunc.decrypt((String)bean.get("z0301"));招聘岗位,暂停后重新发布时,这里是从标签里取得,没有加密
								String z0301=(String)bean.get("z0301");
								RecordVo vo=new RecordVo("z03");
								vo.setString("z0301",z0301);
								vo=dao.findByPrimaryKey(vo);
								String shrs="0";
								shrs=vo.getString("z0315");
								if(!"09".equals(vo.getString("z0319")))
							    	bo.addHireOrder(z03list, z04list, Integer.parseInt(shrs), vo.getString("z0301"),this.getUserView());
							}					
						}
					}
				}
				hm.remove("b_opt");
			}
			else
			{//这是查看需求岗位那个页面上进来的,z0301都是加密的,需要解密
				
					String z0301=(String)this.getFormHM().get("z0301");
					z0301 = com.hjsj.hrms.utils.PubFunc.decrypt(z0301);
					String opt=(String)this.getFormHM().get("opt");
					
					ArrayList sqlParams = new ArrayList();
					sqlParams.add(z0301);
					
					if("3".equalsIgnoreCase(opt))
					{
						dao.delete("delete from  z03  where z0301=?", sqlParams);				
					}
					else
					{				
						String state="";
						if("1".equals(opt))
							state="04";
						else if("2".equals(opt))
							state="09";
						else if("4".equals(opt))
							state="06";
						dao.update("update z03 set z0319='"+state+"' where z0301=?", sqlParams);
                        /**原来设置成结束状态时删掉人员与职位的对应信息，天辰提要求不删*/
						/*if(opt.equals("4"))
						{
							dao.delete("delete from zp_pos_tache where zp_pos_id=?", sqlParams);
						}
						*/
						
					}
					this.getFormHM().put("opt",opt);
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
