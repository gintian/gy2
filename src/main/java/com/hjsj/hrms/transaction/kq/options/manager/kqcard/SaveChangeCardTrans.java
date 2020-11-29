package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCrads;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 保存换卡纪录
 * <p>Title:SaveChangeCardTrans.java</p>
 * <p>Description:判断是否作废如果不作废修改状态为-1，但是如果卡号表中没有这个卡的话，现在没有做处理</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 8, 2007 4:37:55 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SaveChangeCardTrans extends IBusiness {

public void execute() throws GeneralException
    {
    	String old_card=(String)this.getFormHM().get("old_card");
    	String kq_cardno=(String)this.getFormHM().get("kq_cardno");
    	String flag="xxx";
    	if(kq_cardno==null||kq_cardno.length()<=0)
    	{
    		ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.getFrameconn());
    		String org_id=managePrivCode.getPrivOrgId();   
    		KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,org_id,this.getFrameconn());
    		kq_cardno=kq_paramter.getCardno();
    	}
    	if(kq_cardno==null||kq_cardno.length()<=0)
    		return;
    	if(old_card==null||old_card.length()<=0)
    		return;
    	String s_flag=(String)this.getFormHM().get("s_flag");
    	if(s_flag==null||!"1".equals(s_flag))
    	{
    		return;
    	}    	
    	String new_card=(String)this.getFormHM().get("new_card");
    	if(new_card==null||new_card.length()<=0)
    	{
    		return;
    	}
    	String a0100=(String)this.getFormHM().get("a0100");
    	String nbase=(String)this.getFormHM().get("nbase");
    	String lost_flag=(String)this.getFormHM().get("lost_flag");
    	if(lost_flag==null||!"0".equals(lost_flag))
    	{
    		lost_flag="-1";
    	}
    	upNbase(nbase,a0100,new_card,kq_cardno);
    	KqCrads kqCrads=new KqCrads(this.getFrameconn());
    	//xiexd 2014.09.29作废考勤卡号
    	if(kqCrads.searchKqCards(new_card,"1")<1){
    		kqCrads.addKqCards(new_card,"1");
    	}else{
    		//判断当前卡号是否存在考勤卡表中，不存在则添加
    		if(kqCrads.searchKqCards(old_card,"1")<1){
        		kqCrads.addKqCards(old_card,"1");
        	}
    		kqCrads.upKqCards(new_card,"1");
    		kqCrads.upKqCards(old_card,lost_flag); 
    	}
    	flag="ok";
    	this.getFormHM().put("flag", flag);
    }
/**
 * 修改人员表考勤卡号
 * @param nbase
 * @param a0100
 * @param card_no
 * @param kq_cardno
 * @throws GeneralException
 */
    private void upNbase(String nbase,String a0100,String card_no,String kq_cardno)throws GeneralException
    {
       StringBuffer sql=new StringBuffer();
       sql.append("update "+nbase+"A01 set");
       sql.append(" "+kq_cardno+"='"+card_no+"'");
       sql.append(" where a0100='"+a0100+"'");
       ContentDAO dao=new ContentDAO(this.getFrameconn());
       try
       {
    	   dao.update(sql.toString());
       }catch(Exception e)
       {
    	 e.printStackTrace();
    	 throw GeneralExceptionHandler.Handle(e);
       }
    }
}
