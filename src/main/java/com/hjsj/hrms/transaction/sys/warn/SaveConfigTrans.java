package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.businessobject.sys.warn.ConfigCtrlInfoVO;
import com.hjsj.hrms.businessobject.sys.warn.ContextTools;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.SyncSystemUtilBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveWarnSettingTrans</p>
 * <p>Description:保存</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 7, 2005:10:22:36 AM</p>
 * @author zhouhaimao
 * @version 1.0
 * 
 */
public class SaveConfigTrans extends IBusiness implements IConstant {

    public void execute() throws GeneralException {
        DynaBean dbean = (DynaBean) getFormHM().get(Key_RecorderVo);
        
        ConfigCtrlInfoVO ctrlvo = (ConfigCtrlInfoVO)getFormHM().get(Key_HrpWarn_Ctrl_VO);
        ctrlvo.setStrSimpleExpress(PubFunc.keyWord_reback(ctrlvo.getStrSimpleExpress()));
        ctrlvo.setStrDomain(PubFunc.keyWord_reback(ctrlvo.getStrDomain()));
        ctrlvo.setStrEmail(PubFunc.keyWord_reback(ctrlvo.getStrEmail()));
        RecordVo vo = new RecordVo(Key_HrpWarn_Table);
        String warntype=(String)dbean.get("warntype");
        if(warntype!=null&& "0".equals(warntype))
        {
        	 if(ctrlvo.getStrNbase()==null||ctrlvo.getStrNbase().length()<=0)
        	 {
        		 throw GeneralExceptionHandler.Handle(new GeneralException("人事预警必须设置人员库！"));
        	 }
        }
        // ContentDAO只支持RecordVo，所以需要从DynaBean转换为RecordVo
        dynaBean2RecordVo(dbean, vo);
        vo.setString("username", userView.getUserName());

        ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO)dbean.get(Key_HrpWarn_Ctrl_VO);
        if( ctrlVo==null || "0".equals(ctrlVo.getIsComplex())){
        	//System.out.println("无控制信息");
        	;//vo.setString("csource","bs");
        }
        
        String flag=(String)this.getFormHM().get( Key_Flag );
        ContentDAO dao=new ContentDAO(getFrameconn());        
        if(flag.equals( Key_Flag_NewAndAdd )){
            /**
             * 新建，进行insert保存处理
             * 由于与c/s共用一张表格，所以不能使用IDGenerator
             * 而是使用“最大值加1”生成的主键
             */
            vo.setString("ntype","0");        	
            int iId = TransTool.getNextId(Key_HrpWarn_Table, Key_HrpWarn_FieldName_ID);//idg.getId( Key_HrpWarn_TableID );
            vo.setInt( Key_HrpWarn_FieldName_ID, iId);
            
            if(vo.getString(Key_HrpWarn_FieldName_Valid)==null|| "".equals(vo.getString(Key_HrpWarn_FieldName_Valid))){
                vo.setString(Key_HrpWarn_FieldName_Valid,"0");
            }
            cat.debug("warnConfigVO="+vo.toString());           
            dao.addValueObject(vo);   
            String sql = "select * from hrpwarn where wid='"+ iId +"'";
            DynaBean bean = null;
            try {
				ArrayList list = TransTool.executeQuerySql(sql,this.getFrameconn());
				bean = (DynaBean)list.get(0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(bean == null){				
			}else{
				
				/*System.out.println("新增");
	            System.out.println(ctrlvo.generateStringXML());
	            for (int i = 0; i < Key_HrpWarn_Fields.length; i++) {       	
	            	System.out.println(Key_HrpWarn_Fields[i]+"="+(String)bean.get(Key_HrpWarn_Fields[i]));
	            }*/
	            
				bean.set(Key_HrpWarn_Ctrl_VO,ctrlvo);	    	
				if(ctrlvo.getStrEveryone()!=null&& "true".equalsIgnoreCase(ctrlvo.getStrEveryone()))
				{
					//清空邮件表
					sql="delete from email_content where wid='"+iId+"'";
					//清空短信
					String sql1 = "delete from t_sys_smsbox where wid='"+iId+"'";
					//清空微信
					String sql2 = "delete from t_sys_weixin_msg where wid='"+iId+"'";
					//清空钉钉
					String sql3 = "delete from t_sys_dingtalk_msg where wid='"+iId+"'";
					try {
						dao.delete(sql, new ArrayList());
						dao.delete(sql1, new ArrayList());
						dao.delete(sql2, new ArrayList());
						//xus 17/4/20 判断如果不存在t_sys_dingtalk_msg表 则创建此表
				    	DbWizard dbw=new DbWizard(getFrameconn());
				    	if(!dbw.isExistTable("t_sys_dingtalk_msg",false)){
				    		DDWarnUtil.createDDTableInfo("t_sys_dingtalk_msg", getFrameconn());
				    	}else{
				    		dao.delete(sql3, new ArrayList());
				    	}
					} catch (SQLException e) {
						
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				try {
					ScanTrans.warnResult(bean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
           // this.saveHrpwarn_result(dbean,String.valueOf(iId));
			
        }else{//点编辑链接后，进行update保存处理
        	
        	String iId=vo.getString("wid");
 	        try
 	        {
 	            dao.updateValueObject(vo);
 	            DynaBean bean=(DynaBean) ContextTools.getWarnConfigCache().get(iId);
				dbean.set("norder", vo.getString("norder"));
 	            /**清空告警结果表*/
 	          //  String str_sql="delete from hrpwarn_result where wid="+vo.getString("wid");
 	            
 	           // System.out.println(str_sql);
 	            
 	          //  dao.update(str_sql);
 	        }
 	        catch(SQLException sqle)
 	        {
 	    	     sqle.printStackTrace();
 	    	     throw GeneralExceptionHandler.Handle(sqle);            
 	        }
 	        if(ctrlvo.getStrEveryone()!=null&& "true".equalsIgnoreCase(ctrlvo.getStrEveryone()))
			{
				//清空邮件表
				String sql="delete from email_content where wid='"+iId+"'";
				//清空短信
				String sql1 = "delete from t_sys_smsbox where wid='"+iId+"'";
				//清空微信
				String sql2 = "delete from t_sys_weixin_msg where wid='"+iId+"'";
				//清空钉钉
				String sql3 = "delete from t_sys_dingtalk_msg where wid='"+iId+"'";
				try {
					dao.delete(sql, new ArrayList());
					dao.delete(sql1, new ArrayList());
					dao.delete(sql2, new ArrayList());
					//xus 17/4/20 判断如果不存在t_sys_dingtalk_msg表 则创建此表
			    	DbWizard dbw=new DbWizard(getFrameconn());
			    	if(!dbw.isExistTable("t_sys_dingtalk_msg",false)){
			    		DDWarnUtil.createDDTableInfo("t_sys_dingtalk_msg", getFrameconn());
			    	}else{
			    		dao.delete(sql3, new ArrayList());
			    	}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
	        /*
	        System.out.println("修改");
	        System.out.println(ctrlvo.generateStringXML());
	        for (int i = 0; i < Key_HrpWarn_Fields.length; i++) {       	
	        	System.out.println(Key_HrpWarn_Fields[i]+"="+(String)dbean.get(Key_HrpWarn_Fields[i]));
	        }
	        */
	        
	        dbean.set(Key_HrpWarn_FieldName_CtrlInf,ctrlvo.generateStringXML());
	        dbean.set(Key_HrpWarn_Ctrl_VO,ctrlvo);
	        try {
				ScanTrans.getInstance().warnResult(dbean );
			} catch (Exception e) {
				e.printStackTrace();
			}
	        
        }
        
        //集群环境保存修改刷新其他节点预警缓存 guodd 2018-06-15
        SyncSystemUtilBo.sendSyncCmd(SyncSystemUtilBo.SYNC_TYPE_RELOAD_WARN);
    }
    
    private void dynaBean2RecordVo(DynaBean dbean,RecordVo vo){
        ConfigCtrlInfoVO ctrlVo = (ConfigCtrlInfoVO)getFormHM().get(Key_HrpWarn_Ctrl_VO);
        for (int i = 0; i < Key_HrpWarn_Fields.length; i++) {
        	vo.setString( Key_HrpWarn_Fields[i], PubFunc.keyWord_reback((String)dbean.get(Key_HrpWarn_Fields[i])));        	
        }
       vo.setString(Key_HrpWarn_FieldName_CtrlInf, ctrlVo.generateStringXML());         
    }

}
