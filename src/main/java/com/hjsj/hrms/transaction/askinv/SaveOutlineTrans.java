/*
 * Created on 2005-5-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveOutlineTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	 public void execute() throws GeneralException {
        RecordVo vo=(RecordVo)this.getFormHM().get("outlineov");
        if(vo==null)
            return;
        String itemid=this.getFormHM().get("itemid").toString();
        String na=vo.getString("name")==null?"":vo.getString("name");
        String sql="select * from investigate_point where itemid='"+itemid+"' and name='"+na+"'";
        String flag=(String)this.getFormHM().get("flag");
        String oldName = (String)this.getFormHM().get("oldName");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try{
	       this.frowset=dao.search(sql);
	        while(this.frowset.next()){
	            if("1".equals(flag))
	                throw GeneralExceptionHandler.Handle(new Exception("问卷调查，同一题选项不能重复!"));
	            else if( !na.equals(oldName) )
	                throw GeneralExceptionHandler.Handle(new Exception("问卷调查，同一题选项不能重复!"));
	        }
	        if("1".equals(flag))
	        {	
	            /**新加要点，进行保存处理*/
	            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	            String pointid=idg.getId("invpoints.id");
	            String name=vo.getString("name")==null?"":vo.getString("name");
	            vo.setString("name",PubFunc.doStringLength(name,100));
	            
	            vo.setString("pointid",pointid);
	            vo.setString("itemid",itemid);
	                    
	            cat.debug("add_boardvo="+vo.toString());           
	            dao.addValueObject(vo);   
	            
	        }
	        else if("0".equals(flag))
	        {
		        /**点编辑链接后，进行保存处理*/
	            cat.debug("update_itemvo="+vo.toString());
		        try
		        {
		        	String name=vo.getString("name")==null?"":vo.getString("name");
		            vo.setString("name",PubFunc.doStringLength(name,100));
		            dao.updateValueObject(vo);
		        }
		        catch(Exception sqle)
		        {
		    	     sqle.printStackTrace();
		    	     throw GeneralExceptionHandler.Handle(sqle);            
		        }
	        }
	       
	        ((RecordVo)this.getFormHM().get("outlineov")).clearValues();
	        //初始化status状态
	        ((RecordVo)this.getFormHM().get("outlineov")).setString("status","1");
	        ((RecordVo)this.getFormHM().get("outlineov")).setString("describestatus","0");
	         this.getFormHM().put("flag","1");
        }catch(Exception e){
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }
    }
}
