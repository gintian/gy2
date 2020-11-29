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
public class SaveItemTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	 public void execute() throws GeneralException {
        RecordVo vo=(RecordVo)this.getFormHM().get("itemov");
        if(vo==null)
            return;
        String flag=(String)this.getFormHM().get("flag");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
       
        if("1".equals(flag))
        {
            /**新加建议，进行保存处理*/
            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
            String id=this.getFormHM().get("id").toString();
            String itemid=idg.getId("investigate_item.id");
            vo.setString("itemid",itemid);
            vo.setString("id",id);
            String name=vo.getString("name")==null?"":vo.getString("name");
            String fillflag=vo.getString("fillflag");
            if(fillflag==null|| "".equals(fillflag))
            	fillflag="0";
            vo.setString("name",PubFunc.doStringLength(name,250));
            
            try
			{
            	StringBuffer sb=new StringBuffer();
            	sb.append("insert into investigate_item(itemid,id,name,status,fillflag,selects,maxvalue,minvalue) values( ");
            	sb.append("'");
            	sb.append(vo.getString("itemid"));
            	sb.append("','");
            	sb.append(vo.getString("id"));
            	sb.append("','");
            	sb.append(vo.getString("name"));
            	sb.append("',");
            	sb.append(vo.getString("status"));
            	sb.append(","+fillflag);
            	sb.append(","+("".equals(vo.getString("selects").trim())?"0":vo.getString("selects")));
            	sb.append(","+("".equals(vo.getString("maxvalue").trim())?"0":vo.getString("maxvalue")));
            	sb.append(","+("".equals(vo.getString("minvalue").trim())?"0":vo.getString("minvalue")));
            	sb.append(")");
            	dao.update(sb.toString());
			}
            catch(Exception ex)
			{           	
            	ex.printStackTrace();
            	throw GeneralExceptionHandler.Handle(ex); 
			}
            
            cat.debug("add_boardvo="+vo.toString());           
           // dao.addValueObject(vo);   
            
        }
        else if("0".equals(flag))
        {
	        /**
	         * 点编辑链接后，进行保存处理
	         */
            cat.debug("update_itemvo="+vo.toString());
            
            
	        try
	        {
	        	 String name=vo.getString("name")==null?"":vo.getString("name");
	             vo.setString("name",PubFunc.doStringLength(name,250));
	             String fillflag=vo.getString("fillflag");
	             if(fillflag==null|| "".equals(fillflag))
	             	fillflag="0";
	             StringBuffer sb=new StringBuffer();
	             sb.append("update investigate_item set id='");
	             sb.append(vo.getString("id"));
	             sb.append("',name='");
	             sb.append(vo.getString("name"));
	             sb.append("',status=");
	             sb.append(vo.getString("status"));
	             sb.append(" ,fillflag="+fillflag);
	             sb.append(",selects="+vo.getString("selects"));
	             sb.append(",maxvalue="+("".equals(vo.getString("maxvalue").trim())?"0":vo.getString("maxvalue")));
	             sb.append(",minvalue="+("".equals(vo.getString("minvalue").trim())?"0":vo.getString("minvalue")));
	             sb.append(" where itemid='");
	             sb.append(vo.getString("itemid"));
	             sb.append("'");
	             dao.update(sb.toString());
	            //dao.updateValueObject(vo);
	        }
	        catch(Exception sqle)
	        {
	    	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }
        }
        else
        {
	           	
                
       
        }
        ((RecordVo)this.getFormHM().get("itemov")).clearValues();
        ((RecordVo)this.getFormHM().get("itemov")).setString("status","0");
    }
}
