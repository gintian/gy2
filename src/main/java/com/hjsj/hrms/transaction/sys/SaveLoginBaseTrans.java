package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * <p>Title:SaveLoginBaseTrans</p>
 * <p>Description:用于保存用户登录信息库表usr,trs,oth,</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 21, 2005:5:03:48 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveLoginBaseTrans extends IBusiness {

    /**
     * 
     */
    public SaveLoginBaseTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {

        
        StringBuffer strdb=new StringBuffer();
        try
        {        
            String[] dbArr=(String[])this.getFormHM().get("dbArr");
            ArrayList dbList=(ArrayList)this.getFormHM().get("dblist");
            ArrayList sellist=(ArrayList)this.getFormHM().get("selectedlist");        	
	        for(int i=0;i<dbArr.length;i++)
	        {
	            strdb.append(dbArr[i]);
	            strdb.append(",");
	        }
	        RecordVo  vo=new RecordVo("constant");
	        vo.setString("constant","SS_LOGIN");
	        vo.setString("str_value",strdb.toString());
	        vo.setString("describe","login_table");
	        ContentDAO dao=new ContentDAO(this.getFrameconn());

            dao.updateValueObject(vo);
            ConstantParamter.putConstantVo(vo,"SS_LOGIN");
            /**前台界面的显示*/
            for(int i=0;i<dbList.size();i++)
            {
                CommonData data=(CommonData)dbList.get(i);
                String dbpre=data.getDataValue();
                if(strdb.indexOf(dbpre)!=-1)
                    sellist.set(i,"1");
                else
                    sellist.set(i,"0");                    
            }
        }
        catch(Exception ee)
        {
          ee.printStackTrace();
  	      throw GeneralExceptionHandler.Handle(ee);            
        }
        finally
        {
            this.getFormHM().put("","");
        }
    }

}
