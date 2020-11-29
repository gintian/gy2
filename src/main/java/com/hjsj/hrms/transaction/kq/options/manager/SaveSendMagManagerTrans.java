package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCrads;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveSendMagManagerTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String select_pre=(String)this.getFormHM().get("select_pre");
    	String a0100=(String)this.getFormHM().get("a0100");
    	String cardno_value=(String)this.getFormHM().get("cardno_value");    	
    	KqCardLength kqCardLength=new KqCardLength(this.getFrameconn());
    	int cardlength = kqCardLength.getCardLend();
    	String s = "";
    	for(int i=0;i<cardlength-cardno_value.length();i++){
    		s=s+"0";
    	}
    	cardno_value = s+cardno_value;
    	String magcard_setid=(String)this.getFormHM().get("magcard_setid");
    	ArrayList itemid_arr = (ArrayList)this.getFormHM().get("itemid_arr");
		ArrayList value_arr = (ArrayList)this.getFormHM().get("itemvalue_arr");
		KqCrads kqCrads=new KqCrads(this.getFrameconn());
		ArrayList newfieldlist=kqCrads.checkDate(itemid_arr,value_arr);
    	//ArrayList newfieldlist=(ArrayList)this.getFormHM().get("newfieldlist");
    	String strTableName=select_pre+magcard_setid;
    	RecordVo vo=new RecordVo(strTableName);    
    	if(newfieldlist==null||newfieldlist.size()<=0)
    		throw GeneralExceptionHandler.Handle(new GeneralException("","保存操作出错！","",""));
    	for(int i=0;i<newfieldlist.size();i++)
    	{
    		FieldItem field=(FieldItem)newfieldlist.get(i);       		
    		 if("N".equals(field.getItemtype()))
             {
          	   if(field.getValue()!=null&&field.getValue().length()>0)
          	   {
          		   vo.setDouble(field.getItemid().toLowerCase(),Double.parseDouble(field.getValue()));
          	   }
             }else  if("D".equals(field.getItemtype()))
 	   		 {
            	 if(field.getValue()!=null&&field.getValue().length()>0)
            	 {
            		 java.util.Date dd=DateUtils.getDate(field.getValue(),"yyyy-MM-dd");
               		 vo.setDate(field.getItemid().toLowerCase(),dd);
            	 }
 	   		 }else{
 	   			 vo.setString(field.getItemid().toLowerCase(),field.getValue());
        	 }
    	}
    	boolean isCorrect=saveManger(strTableName,a0100,vo,cardno_value);
    	if(isCorrect)
    		this.getFormHM().put("flag", "0");
    	else
    		this.getFormHM().put("flag", "1");
    }
    /**
     * 新增一条记录
     * @param strTableName
     * @param userid
     * @param vo
     * @return
     */
    private synchronized boolean saveManger(String strTableName,String userid,RecordVo vo,String cardno_value)
    {
    	boolean isCorrect=true;
    	StructureExecSqlString structureExecSqlString=new StructureExecSqlString();
    	String i9999=structureExecSqlString.getUserI9999(strTableName, userid, "a0100", this.getFrameconn());
    	vo.setString("a0100", userid);
    	vo.setString("i9999", i9999);
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
    		dao.addValueObject(vo);
    		saveKqCardno(cardno_value,strTableName,userid);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		isCorrect=false;
    	}
    	return isCorrect;
    }
    private boolean saveKqCardno(String cardno_value,String strTableName,String userid)
    {
    	String kq_cardno=(String)this.getFormHM().get("kq_cardno");
		if(kq_cardno==null||kq_cardno.length()<=0)
		{
			KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,"",this.getFrameconn());  			
			kq_cardno=kq_paramter.getCardno();
		}	
		String select_pre=(String)this.getFormHM().get("select_pre");
		String up="update "+select_pre+"A01 set "+kq_cardno+"='"+cardno_value+"',C01SP = '02' where a0100='"+userid+"'";		
		boolean isCorrect=true;
		try
    	{
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
    		dao.update(up);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		isCorrect=false;
    	}
		return isCorrect;
    }    
}
