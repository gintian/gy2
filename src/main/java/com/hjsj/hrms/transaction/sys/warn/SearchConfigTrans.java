package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Title:SearchWarnSettingTrans
 * Description:根据传过来的ID查询对应的对象，用于编辑修改，如果是新增，则不查询
 * Company:hjsj
 * create time:Jun 7, 2006:10:22:17 AM
 * @author zhouhaimao
 * @version 1.0
 *  
 */
public class SearchConfigTrans extends IBusiness implements IConstant{

    public void execute() throws GeneralException {

        //编辑过程传递预警ID号
        String strID = (String)((HashMap) this.getFormHM().get(Key_Request_Param_HashMap)).get("warn_wid");
        ArrayList emailtemplateList=getEmailTemplateList(2);
        this.getFormHM().put("emailtemplateList", emailtemplateList);
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑 用户的使用习惯。
         */
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        
        try {
			this.frowset=dao.search("select setid,description from t_sys_warntype");
			ArrayList tranfieldsetlist=new ArrayList();            
	        while(this.frowset.next())
	        {
	        	CommonData dataobj = new CommonData();
				String setid = this.getFrowset().getString("setid");
				String setdesc = this.getFrowset().getString("description");
				dataobj = new CommonData(setid,"("+setid+")"+setdesc);
				tranfieldsetlist.add(dataobj);
	        }
	        this.getFormHM().put("tranfieldsetlist", tranfieldsetlist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
       
        if ( strID == null || strID.trim().length()<1 || getFormHM().get(Key_Flag).equals( Key_Flag_NewAndAdd )){
        	this.getFormHM().put(Key_Flag, Key_Flag_NewAndAdd);        	
        	return;
        }    
        this.getFormHM().put(Key_Flag, Key_Flag_Update);
        RecordVo vo = new RecordVo( Key_HrpWarn_Table );//new RecordVo( Key_HrpWarn_Table );
        DynaBean dbean = new LazyDynaBean();
        DomainTool tool = new DomainTool();
        try {
            vo.setString("wid", strID);
            vo = dao.findByPrimaryKey(vo);

            for (int i = 0; i < Key_HrpWarn_Fields.length; i++) {
            	if(i==10)
            	{
            		if(vo.getString(Key_HrpWarn_Fields[i])==null||vo.getString(Key_HrpWarn_Fields[i]).length()<=0)
            			dbean.set(Key_HrpWarn_Fields[i],"0");
            		else
            			dbean.set(Key_HrpWarn_Fields[i], vo.getString(Key_HrpWarn_Fields[i]));
            	}else
            	  dbean.set(Key_HrpWarn_Fields[i], vo.getString(Key_HrpWarn_Fields[i]));
            }
            
            // 解析XML结果存入recordVo的虚拟字段中
            ConfigCtrlInfoVO ctrlVo =  new ConfigCtrlInfoVO(dbean.get( Key_HrpWarn_FieldName_CtrlInf ).toString());
            
            dbean.set(Key_XmlResul_Freq, ctrlVo.getFreqShow());
            dbean.set(Key_Domain_Names, tool.getDomainNames(ctrlVo.getStrDomain()));
            dbean.set(Key_HrpWarn_Template, tool.getTemplate(ctrlVo.getStrTemplate(), dao));
            dbean.set(Key_HrpWarn_Nbase, tool.getNbases(ctrlVo.getStrNbase(), dao));
            dbean.set( Key_HrpWarn_Ctrl_VO, ctrlVo);               
            dbean.set("Obj",ctrlVo.getStrDomain());  
            if(dbean.get(Key_HrpWarn_FieldName_Org)==null||dbean.get(Key_HrpWarn_FieldName_Org).toString().length()<=0)
            	dbean.set(Key_HrpWarn_FieldName_Org,"UN");
            this.getFormHM().put(Key_HrpWarn_Nbase, ctrlVo.getStrNbase());            
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
            
        } finally {
            this.getFormHM().put( Key_RecorderVo, dbean);//vo);
        }        
        
    }    
    /**
	 * 取得所有薪资发放的邮件模板
	 * @return ArrayList
	 */
	public ArrayList getEmailTemplateList(int type)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select id,name from email_name where nmodule=2 or nmodule=1  order by id";
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				list.add(new CommonData(this.frowset.getString("id"),this.frowset.getString("name")));
			}
			if(type==1)
			{
	    		if(list.size()==0)
	    		{
	    			list.add(new CommonData("","      "));
	    		}
	    		list.add(new CommonData("0",ResourceFactory.getProperty("label.gz.new")));
	    	}
			else
			{
				if(list.size()==1)
				{
		    		list.add(0,new CommonData("","      "));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
}