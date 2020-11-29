package com.hjsj.hrms.transaction.kq.register.select;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.KqselectTerm;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 考勤查询条件
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class KqSelectTermTrans extends IBusiness{
  
	public void execute() throws GeneralException
	{
        ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");  
        String like=(String)this.getFormHM().get("like");
        if(like==null||like.length()<=0)
        	like="";
        String nbase=(String)this.getFormHM().get("select_pre");
        /**解决不定期汇没有得到人员库**/
		   if(nbase==null||nbase.length()<=0)
			{
			   ArrayList kq_dbase_list = (ArrayList)this.getFormHM().get("kq_dbase_list");
				if(kq_dbase_list!=null&&kq_dbase_list.size()>0)
				{
					nbase=kq_dbase_list.get(0).toString();
				}else
				{
					 if(kq_dbase_list==null||kq_dbase_list.size()<=0)
					   {
						   //kq_dbase_list=userView.getPrivDbList(); 
						   kq_dbase_list=RegisterInitInfoData.getDase3(this.getFormHM(),this.userView,this.getFrameconn()); 
					   }else
					   {
						   String code_kind="";
						   String kind="";
						   String code="";
						   HashMap map=new HashMap();
						   if(code==null||code.length()<=0)
						   {
								 code="";
						   }	
						   if(kind==null||kind.length()<=0)
						   {
							   kind="2";
						   }
						   if(kind==null||kind.length()<=0)
						   {
								kind=RegisterInitInfoData.getKindValue(kind,this.userView);
								code="";
						   }		   
						   if("2".equals(kind))
						   {
								code=RegisterInitInfoData.getKqPrivCodeValue(userView);
						   }else 
						   {
						    	code_kind=RegisterInitInfoData.getDbB0100(RegisterInitInfoData.getKqPrivCodeValue(userView),kind,map,this.userView,this.getFrameconn()); 
						   }
						   if(code!=null&&code.length()>0)
							{
				    			if("2".equals(kind))
				    			{
				    				kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),code);
				    			}else if(code_kind!=null&&code_kind.length()>0)
				    			{
				    				kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),code_kind);
				    			}else{
				    				kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),code);
				    			}
				    		}else
				    		{
				    			kq_dbase_list=RegisterInitInfoData.getDase3(this.getFormHM(),this.userView,this.getFrameconn());
				    		}
					   }
				}
				KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
				ArrayList listdb=kqUtilsClass.getKqNbaseList(kq_dbase_list);
				if(listdb!=null&&listdb.size()>1)
				{
					CommonData da=(CommonData) listdb.get(1);
					nbase=da.getDataValue();
				}
			}
		/**结束**/
        KqselectTerm kqselectTerm=new KqselectTerm(this.userView);
        String whereIN=kqselectTerm.getWhereSql(factorlist,like,nbase);         
        this.getFormHM().put("selectsturs", "1");
        this.getFormHM().put("selectResult", whereIN);
    }

}
