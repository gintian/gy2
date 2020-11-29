package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Title:SearchWarnSettingListTrans
 * Description:查询预警设置,对应hrpwarn表格
 * Company:hjsj
 * create time:Jun 6, 2006:5:42:54 PM
 * @author zhouhaimao
 * @version 1.0
 *
 */
public class SearchConfigListTrans extends IBusiness implements IConstant {

	/**
	 * 获取当前用户权限范围内的预警表(hrpwarn)中的预警信息SQL语句
	 * @return
	 */
    private String getWarnSQL(){
    	StringBuffer strSql = new StringBuffer();
        strSql.append("select ");
        strSql.append(Key_HrpWarn_Fields[0]);
        for (int i = 1; i < Key_HrpWarn_Fields.length; i++) {
            strSql.append(",");
            strSql.append(Key_HrpWarn_Fields[i]);
        }
        strSql.append(" from ");
        strSql.append(Key_HrpWarn_Table);


        UserView userview = (UserView)getUserView();
        if( userview.isSuper_admin() ){//管理员
        	//所有预警条件
        }else{//一般用户
        	strSql.append(" where b0110='UN' or b0110 ='UN"+userview.getUserOrgId()+"'");
        }
       // System.out.println(strSql.toString());
        strSql.append(" order by norder");
        return strSql.toString();
    }


    public void execute() throws GeneralException {
    	HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String edition = (String)hm.get("edition");
		if(edition!=null&& "4".equalsIgnoreCase(edition)){
			this.getFormHM().put("edition","4");
		}else{
			this.getFormHM().put("edition","5");
		}
    	DomainTool tool = new DomainTool(); //预警对象分析工具对象

        String strSql = this.getWarnSQL();

        ContentDAO dao = new ContentDAO(this.getFrameconn());

        ArrayList voList = new ArrayList();
        try {

            this.frowset=dao.search(strSql.toString());
            String value=null;

            while (this.frowset.next()) {
            	DynaBean dbean = new LazyDynaBean();
                for (int i = 0; i < Key_HrpWarn_Fields.length; i++) {
                	value=this.frowset.getString(Key_HrpWarn_Fields[i]);
                	if(value==null||value.length()<=0|| "null".equals(value)){
                		value="";
                	}
                	if(("cmsg".equalsIgnoreCase(Key_HrpWarn_Fields[i])&&value.getBytes().length>60)){
                		value=PubFunc.splitString(value,60)+"......";
                	}
                	dbean.set(Key_HrpWarn_Fields[i], value);
                }
                // 封装预警控制信息(xml)
                String xml = dbean.get( Key_HrpWarn_FieldName_CtrlInf ).toString();
                ConfigCtrlInfoVO ctrlVo =  new ConfigCtrlInfoVO(xml);
                dbean.set(Key_XmlResul_Freq, ctrlVo.getFreqShow());  //预警频度显示信息
                String domain_name=tool.getDomainNames(ctrlVo.getStrDomain());
                dbean.set(Key_Domain_Names, domain_name.getBytes().length>40?PubFunc.splitString(domain_name,40)+"......":domain_name);//预警对象中文名称显示(逗号分割)
                dbean.set( Key_HrpWarn_Ctrl_VO, ctrlVo);//预警控制对象          

                voList.add(dbean);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);

        } catch(Exception e){
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);

        }finally {
            this.getFormHM().put(Key_List_Query_FormVo, voList);
        }
    }


}