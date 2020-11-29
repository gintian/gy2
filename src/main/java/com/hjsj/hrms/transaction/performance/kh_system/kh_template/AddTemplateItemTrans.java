package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * <p>Title:AddTemplateItemTrans.java</p>
 * <p>Description>:增加模板项目交易类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-7-1 上午11:25:37</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class AddTemplateItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String itemdesc = SafeCode.decode((String)this.getFormHM().get("name"));
			/**模板是否已经有项目=0有项目=1还没有项目 */
			String isHaveItem = (String)this.getFormHM().get("isHaveItem");
			/**=0 同级项目，=1下级项目，2插入项目*/
			String opt=(String)this.getFormHM().get("opt");
			/**=1是项目=2是指标*/
			String type=(String)this.getFormHM().get("type");
			String itemid=(String)this.getFormHM().get("itemid");
			String  pointid=(String)this.getFormHM().get("pointid");
			String templateid=(String)this.getFormHM().get("id");
			String subsys_id=(String)this.getFormHM().get("subsys_id");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			int newid = new Integer(idg.getId("per_template_item.item_Id")).intValue();
			int seq=bo.getMaxId("per_template_item", "seq");
			int insertseq=0;
			String parentid="";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RecordVo vo = new RecordVo("per_template_item");
			vo.setInt("kind", 2);
			if("-1".equals(itemid)&&!"-1".equals(pointid))
			{
				itemid=bo.getItemidByPointid(pointid, templateid);
			}
			if("0".equals(opt))
			{
				if("1".equals(isHaveItem))
				{
					
				}
				else
				{
					parentid=bo.getParentId(itemid);
				}
				
				vo.setInt("item_id",newid);
				vo.setInt("seq",seq);
				vo.setString("template_id",templateid);
				vo.setString("itemdesc",itemdesc);
				if(parentid!=null&&!"".equals(parentid))
				{
					vo.setInt("parent_id",Integer.parseInt(parentid));
					 String sql="update per_template_item set kind=1 where item_id="+parentid;
				  	 dao.update(sql);
				}
			}
			else if("1".equals(opt))
			{
				vo.setInt("item_id",newid);
				vo.setInt("seq",seq);
				vo.setString("template_id",templateid);
				vo.setString("itemdesc",itemdesc);
				if(itemid!=null&&!"".equals(itemid))
				{
					vo.setInt("parent_id",Integer.parseInt(itemid));
					 String sql="update per_template_item set kind=1 where item_id="+itemid;
				  	 dao.update(sql);
				}	
			}
			else if("2".equals(opt))
			{
				/**插入项目，把选择的项目的seq付给新增的项目，比seq大的顺序号全部加一*/
			   insertseq = bo.getInsertSeq(itemid);
			   parentid=bo.getParentId(itemid);
			   vo.setInt("item_id",newid);
			   vo.setInt("seq",insertseq);
			   vo.setString("template_id",templateid);
			   vo.setString("itemdesc",itemdesc);
				if(parentid!=null&&!"".equals(parentid))
				{
					 vo.setInt("parent_id",Integer.parseInt(parentid));
					 String sql="update per_template_item set kind=1 where item_id="+parentid;
				  	 dao.update(sql);
				}
			}
			 RecordVo vv=new RecordVo("per_template");
	    	  vv.setString("template_id",templateid);
	    	  vv=dao.findByPrimaryKey(vv);
			  if(vv!=null)
			  {
				  if("1".equals(vv.getString("status")))//权重模版，分值默认为模版总分
				  {
					  vo.setString("score",vv.getString("topscore"));
				  }
				  else//分值模版权重默认为1
				  {
					  vo.setInt("rank", 1);
				  }
			  }
			dao.addValueObject(vo);
			/**下级增加时，要设置孩子id*/
			if("1".equals(opt))
			{
				bo.setTemplateItemChild(itemid,String.valueOf(newid));
			}
			/**插入时，要从新调序*/
			if("2".equals(opt))
			{
				bo.configSeq(String.valueOf(insertseq), String.valueOf(newid), "per_template_item");
			}
			this.getFormHM().put("subsys_id",subsys_id);
			this.getFormHM().put("templateid",templateid);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
