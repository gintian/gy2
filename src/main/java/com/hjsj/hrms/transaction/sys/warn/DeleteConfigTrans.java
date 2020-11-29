package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:DeleteIpAddrTrans</p>
 * <p>Description:删除ip地址，ip_addr</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 6, 2005:5:43:18 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DeleteConfigTrans extends IBusiness implements IConstant {

    /**
     * 
     */
    public DeleteConfigTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        ArrayList list=(ArrayList)this.getFormHM().get(Key_List_SelectedVo);
        if(list==null)
            return;
        cat.debug("list size="+list.size());
        if(list==null||list.size()==0)
        	return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
        	// 由于SearchConfigListTran查询出来的bean是DynaBean类型，所以需要进行类型转换
        	RecordVo  tempVo = new RecordVo( Key_HrpWarn_Table );
        	DynaBean dbean = null;
        	StringBuffer strsql=new StringBuffer();
        	strsql.append("delete from hrpwarn_result where wid in (");
        	for( int i = 0 ; i < list.size(); i++){
        		dbean = (DynaBean)list.get(i);
        		tempVo.setString( Key_HrpWarn_FieldName_ID, (String)dbean.get(Key_HrpWarn_FieldName_ID));
        		dao.deleteValueObject( tempVo);
        		strsql.append((String)dbean.get(Key_HrpWarn_FieldName_ID));
        		strsql.append(",");
        	}
        	/**清空预警结果表*/
        	strsql.setLength(strsql.length()-1);
        	strsql.append(")");
        	dao.update(strsql.toString());
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
  	        throw GeneralExceptionHandler.Handle(sqle);             
        }
    }

}
