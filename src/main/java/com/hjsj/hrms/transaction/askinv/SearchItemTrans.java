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
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchItemTrans extends IBusiness {

	 /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
	//ResultSet frowset=null;
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
        String itemid=(String)hm.get("itemid");
        //取得调查表id及名称
	  	 String id=(String)hm.get("id");
	  	 if(id!=null)
	  	 {
	  		 id=PubFunc.decryption(id);
	  		 this.getFormHM().put("id",id);
	  	 }
	  	 else
	  	 {
	  	 	id=this.getFormHM().get("id").toString();
	  	 }
	  	//String content=(String)hm.get("content");
	  	String content=(String)this.getFormHM().get("content");
	  	/**
	  	 * 通过超链接传的参数,中文易出现乱码
	  	 * 解决办法：在ActionForm增加和参数同名的属性
	  	 * 即加
	  	 */
	  	if(!(content==null || "".equals(content)))
	  	{
		  	/* 	  		
	  		try
			{
	  			content=ChangeStr.ToGbCode(content);
			}
	  		catch(IOException ex)
			{
	  			ex.printStackTrace();
			}
			*/			
	  		this.getFormHM().put("content",content);	  		
	  	}

        String flag=(String)this.getFormHM().get("flag");
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
        if("1".equals(flag))
            return;
        cat.debug("------>investigate_item_id====="+itemid);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
       
        RecordVo vo=new RecordVo("investigate_item");
           
	 
        try
        {  
    	 StringBuffer strsql=new StringBuffer();
    	 strsql.append("select * from investigate_item where itemid='");
    	 strsql.append(itemid);
    	 strsql.append("'");
         this.frowset = dao.search(strsql.toString());
         if (this.frowset.next()) 
         {
             vo.setString("itemid", PubFunc.nullToStr(this.frowset.getString("itemid")));
             String temp = frowset.getString("id");
             if (temp == null || "".equals(temp)) {
                 vo.setString("id", "0");
             } else {
                 vo.setString("id", frowset.getString("id"));
             }

             temp = frowset.getString("name");
             if (temp == null || "".equals(temp)) {
                 vo.setString("name", "");
             } else {
                 vo.setString("name", frowset.getString("name"));
             }
             vo.setString("status",PubFunc.NullToZero(frowset.getString("status")));
             vo.setString("fillflag",(frowset.getString("fillflag")==null|| "".equals(frowset.getString("fillflag")))?"0":frowset.getString("fillflag"));
             vo.setString("selects",(frowset.getString("selects")==null|| "".equals(frowset.getString("selects")))?"0":frowset.getString("selects"));
             vo.setString("maxvalue", (frowset.getString("maxvalue")==null|| "0".equals(frowset.getString("maxvalue")))?"":frowset.getString("maxvalue"));
             vo.setString("minvalue", (frowset.getString("minvalue")==null|| "0".equals(frowset.getString("minvalue")))?"":frowset.getString("minvalue"));
         }
         vo.setString("itemid",itemid);
         //vo=dao.findByPrimaryKey(vo);
        }
        catch(Exception  sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("itemTb",vo);
        }
    }

}
