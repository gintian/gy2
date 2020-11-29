package com.hjsj.hrms.transaction.performance.achivement.standarditem;

import com.hjsj.hrms.businessobject.performance.achivement.StandardItemBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveStandardItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			/**=base基本指标规则=standard加分或者扣分指标*/
			 String model=(String)this.getFormHM().get("model");
             //opt=0增加同级=1增加下级=2插入=3编辑
	         String opt=(String)this.getFormHM().get("opt");
	         String desc=SafeCode.decode((String)this.getFormHM().get("desc"));
	         String score=(String)this.getFormHM().get("score");
	         String beforeitemid=(String)this.getFormHM().get("beforeitemid");
	         /**是否已有项目=0有=1没有*/
	         String isHaveItem=(String)this.getFormHM().get("isHaveItem");
	         String top_value=(String)this.getFormHM().get("top");
	         String bottom_value=(String)this.getFormHM().get("bottom");
	         String point_id=(String)this.getFormHM().get("point_id");
	         RecordVo vo = new RecordVo("per_standard_item");
	         ContentDAO dao = new ContentDAO(this.getFrameconn());
	         
	         if("3".equals(opt))
	         {
	        	 vo.setInt("item_id",Integer.parseInt(beforeitemid));
	        	 vo=dao.findByPrimaryKey(vo);
	        	 vo.setString("itemdesc",desc);
	        	 if("base".equals(model))
	        	 {
	            	 vo.setDouble("score",Double.parseDouble((score==null|| "".equals(score)?"0":score)));
	            	 vo.setDouble("top_value",Double.parseDouble((top_value==null|| "".equals(top_value)?"0":top_value)));
	            	 vo.setDouble("bottom_value",Double.parseDouble((bottom_value==null|| "".equals(bottom_value)?"0":bottom_value)));
	        	 }
	        	 dao.updateValueObject(vo);
	         }else
	         {
	        	 StandardItemBo bo = new StandardItemBo(this.getFrameconn());
	        	 int itemid=bo.getMaxValueByCloumn("item_id","per_standard_item");
	        	 int seq=bo.getMaxValueByCloumn("seq", "per_standard_item");
	        	 if("base".equals(model))
	        	 {
	        		 vo.setInt("item_id",itemid);
	            	 vo.setInt("seq",seq);
	            	 vo.setString("itemdesc",desc);
	            	 vo.setDouble("score",Double.parseDouble((score==null|| "".equals(score)?"0":score)));
	            	 vo.setDouble("top_value",Double.parseDouble((top_value==null|| "".equals(top_value)?"0":top_value)));
	            	 vo.setDouble("bottom_value",Double.parseDouble((bottom_value==null|| "".equals(bottom_value)?"0":bottom_value)));
	            	 vo.setString("point_id",point_id);
	        	 }
	        	 else{
	            	 /**增加第一个项目*/
        	         if("1".equals(isHaveItem))
	                 {
        	        	 vo.setInt("item_id",itemid);
    	            	 vo.setInt("seq",seq);
    	            	 vo.setString("itemdesc",desc);
    	            	 /*vo.setDouble("score",Double.parseDouble((score==null||score.equals("")?"0":score)));
    	            	 vo.setDouble("top_value",Double.parseDouble((top_value==null||top_value.equals("")?"0":top_value)));
    	            	 vo.setDouble("bottom_value",Double.parseDouble((bottom_value==null||bottom_value.equals("")?"0":bottom_value)));*/
    	            	 vo.setString("point_id",point_id);
	                  }
        	         else
        	         {
        	        	 /**增加同级项目*/
    	            	 if("0".equals(opt))
    	            	 {
    	            		 RecordVo vo2=new RecordVo("per_standard_item");
    	            		 vo2.setInt("item_id",Integer.parseInt(beforeitemid));
    	            		 vo2=dao.findByPrimaryKey(vo2);
    	            		 String parentid=vo2.getString("parent_id");
    	            		 vo.setInt("item_id", itemid);
    	            		 vo.setInt("seq",seq);
    	            		 vo.setString("itemdesc",desc);
        	            	/* vo.setDouble("score",Double.parseDouble((score==null||score.equals("")?"0":score)));
        	            	 vo.setDouble("top_value",Double.parseDouble((top_value==null||top_value.equals("")?"0":top_value)));
        	            	 vo.setDouble("bottom_value",Double.parseDouble((bottom_value==null||bottom_value.equals("")?"0":bottom_value)));*/
        	            	 vo.setString("point_id",point_id);
        	            	 if(parentid!=null&&!"".equals(parentid))
        	             	 {
        	            		 vo.setInt("parent_id",Integer.parseInt(parentid));
        	            	 }
    	            	 }
    	            	 else if("1".equals(opt))
    	            	 {
    	            		 vo.setInt("item_id", itemid);
    	            		 vo.setInt("seq",seq);
    	            		 vo.setString("itemdesc",desc);
        	            	/* vo.setDouble("score",Double.parseDouble((score==null||score.equals("")?"0":score)));
        	            	 vo.setDouble("top_value",Double.parseDouble((top_value==null||top_value.equals("")?"0":top_value)));
        	            	 vo.setDouble("bottom_value",Double.parseDouble((bottom_value==null||bottom_value.equals("")?"0":bottom_value)));*/
        	            	 vo.setString("point_id",point_id);
    	             		 vo.setInt("parent_id",Integer.parseInt(beforeitemid));
    	            		 bo.configChildId(Integer.parseInt(beforeitemid), itemid);
    	            	 }
    	            	 else/**插入*/
    	            	 {
    	            		 RecordVo vo2=new RecordVo("per_standard_item");
    	            		 vo2.setInt("item_id",Integer.parseInt(beforeitemid));
    	            		 vo2=dao.findByPrimaryKey(vo2);
    	            		 seq=vo2.getInt("seq");
    	            		 String parentid = vo2.getString("parent_id");
    	            		 vo.setInt("item_id", itemid);
    	            		 vo.setInt("seq",seq);
    	            		 vo.setString("itemdesc",desc);
        	            	/* vo.setDouble("score",Double.parseDouble((score==null||score.equals("")?"0":score)));
        	             	 vo.setDouble("top_value",Double.parseDouble((top_value==null||top_value.equals("")?"0":top_value)));
        	            	 vo.setDouble("bottom_value",Double.parseDouble((bottom_value==null||bottom_value.equals("")?"0":bottom_value)));*/
        	             	 vo.setString("point_id",point_id);
        	            	 if(parentid!=null&&!"".equals(parentid))
        	            	 {
        	            		 vo.setInt("parent_id",Integer.parseInt(parentid));
        	            	 }
        	             	 bo.configSeq(String.valueOf(seq), String.valueOf(itemid), "per_standard_item");
    	            	 }
    	         	
    	             }
	        	 }
	        	 dao.addValueObject(vo);
	         }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
