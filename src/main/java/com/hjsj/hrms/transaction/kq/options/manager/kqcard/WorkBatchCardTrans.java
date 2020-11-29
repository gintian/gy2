package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCrads;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 批量得到手工发卡人员
 * <p>Title:WorkBatchCardTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 6, 2007 1:58:33 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class WorkBatchCardTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String a_code=(String)this.getFormHM().get("a_code");
    	ArrayList r_code=(ArrayList)this.getFormHM().get("r_code");
    	String kq_gno=(String)this.getFormHM().get("kq_gno");
    	if(a_code==null||a_code.length()<=0){
    		ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());		
    		a_code="UN"+managePrivCode.getPrivOrgId();
		}else{
			if(a_code.indexOf("EP")!=-1)
			{
				this.getFormHM().put("selected_emp","");
    			return;
			}
    	}
    	KqCrads kqCrads=new KqCrads(this.getFrameconn());
    	String org_id=kqCrads.getOrgId(a_code,"",this.userView,this.getFormHM());
    	//String code_kind=RegisterInitInfoData.getDbB0100(org_id,"1",this.getFormHM(),this.userView,this.getFrameconn());
    	//ArrayList kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),code_kind);
    	String kind="";			
		if(a_code.indexOf("UN")!=-1)
		{
			kind="2";
		}else if(a_code.indexOf("UM")!=-1)
		{
			kind="1";
		}else if(a_code.indexOf("@K")!=-1)
		{
			kind="0";
		}else if(a_code.indexOf("EP")!=-1)
		{
			kind="a01";
		}else
		{
			kind="0";
		}
		String code="";
		if(a_code.length()>2)
		{
			code=a_code.substring(2);
		}
		KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
		ArrayList kq_dbase_list = kqUtilsClass.setKqPerList(code, kind);
		
		KqParameter para=new KqParameter(this.userView,org_id,this.getFrameconn());
	    HashMap hashmap =para.getKqParamterMap();
		String kq_type=(String)hashmap.get("kq_type");
		String card_no=(String)hashmap.get("cardno");
		
		String sql_str=kqCrads.getQueryString(kq_dbase_list,this.userView,code,kind,kq_type,card_no,kq_gno);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		CommonData vo =null;
		ArrayList list=new ArrayList();
		String nbase="";
		String a0100="";
		ArrayList g_list=new ArrayList();
		try
		{
			this.frowset=dao.search(sql_str);
			while(this.frowset.next())
			{
				nbase=this.frowset.getString("nbase");
				a0100=this.frowset.getString("a0100");
				boolean isCorrect=true;
				if(r_code!=null&&r_code.size()>0)
		    	{
		    		for(int i=0;i<r_code.size();i++)
		    		{
		    			if(r_code.get(i).toString().equals(nbase+"`"+a0100))
		    			{
		    				isCorrect=false;
		    				break;
		    			}
		    		}
		    	}
				if(isCorrect)
				{
					vo = new CommonData();
					vo.setDataName(this.frowset.getString("a0101"));
	    			vo.setDataValue(this.frowset.getString("nbase")+"`"+this.frowset.getString("a0100"));
	    			String g_no="";
	    			if(kq_gno!=null&&kq_gno.length()>0)
	    	    	{
	    				g_no=this.frowset.getString(kq_gno)!=null?this.frowset.getString(kq_gno):"";
	    	    	}
	    			g_list.add(g_no);
	    			list.add(vo);
				}				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("selected_emp",list);
		this.getFormHM().put("gno_list",g_list);
    }

}
