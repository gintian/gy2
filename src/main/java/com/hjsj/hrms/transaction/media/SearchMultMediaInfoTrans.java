/*
 * Created on 2005-8-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.media;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchMultMediaInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		this.getFormHM().put("multimedia_maxsize", "-1");
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		this.getFormHM().put("returnvalue", reqhm.get("returnvalue"));
		reqhm.remove("returnvalue");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String userbase = (String) reqhm.get("userbase");
		if (userbase == null||userbase.length()==0)
			userbase=(String)this.getFormHM().get("userbase");//人员库
		String tablename=userbase + "A00";                       //表的名称
		String A0100=(String)this.getFormHM().get("a0100"); 
		
		if (this.userView.getA0100().length() > 0) {
			if (this.userView.getStatus() == 4) {
				this.getFormHM().put("isUserEmploy","1");
			} else {
				this.getFormHM().put("isUserEmploy","0");
			}
		} else {
			this.getFormHM().put("isUserEmploy","0");
		}
		
		//自助用户登录业务平台，按业务平台权限走，同理业务用户登录自助平台，按自助权限走，因为前台区分了 wangrd 2013-09-22
        if("notself".equals(reqhm.get("flag"))&&"1".equals(this.getFormHM().get("isUserEmploy"))){
            this.getFormHM().put("isUserEmploy","0");
        }
        if("infoself".equals(reqhm.get("flag"))&&"0".equals(this.getFormHM().get("isUserEmploy"))){
            this.getFormHM().put("isUserEmploy","1");
        }
        
		if("notself".equals(reqhm.get("flag"))&&"A0100".equals(A0100)){
			A0100="A0100";
		}else{
		    //A0100的值为“A0100”时，默认是员工自助-我的信息-信息维护中维护自己的信息
		    //当userView中a0100为空时，登录用户为业务用户，员工自助-我的信息-信息维护页面不显示数据
			if("A0100".equals(A0100) && StringUtils.isNotEmpty(userView.getA0100()))
			    A0100=userView.getA0100();
			
		}
		
		/*判断是否拥有主集*/
		String check = CheckMain( dao, "6", userbase, A0100);
		if("yes".equalsIgnoreCase(check))
			this.getFormHM().put("check_main","yes");	
		else
			this.getFormHM().put("check_main","no");	
		
		//操纵表的名称
	      tablename=userbase + "A00";
	      
		  StringBuffer cond=new StringBuffer();
		  cond.append("select flag,sortname from mediasort where dbflag=1");		  
		  String mediasort="''";
		  int n=0;
		  try{
			  this.frowset=dao.search(cond.toString());
	          while(this.frowset.next())
	          {
	    		  String flagsort=this.frowset.getString("flag");
	              /**多媒体类型权限分析*/
	              if(userView.isSuper_admin())
	              {
              		mediasort+=",";
	              	mediasort+="'" + flagsort + "'";
	              	n++;
	              }
	              else
	              {
		              if(userView.hasTheMediaSet(flagsort))
		              {
		           		mediasort+=",";
		              	mediasort+="'" + flagsort + "'";
		              	n++;
		              }	    
	              }        
	          }
		 }catch(Exception e)
		 {}
	
		  cond.delete(0,cond.length());
		  cond.append("select flag,sortname from mediasort where flag in (");
		  cond.append(mediasort);
		  cond.append(") and dbflag=1");
		  /**应用库前缀过滤条件*/
		 // System.out.println(cond.toString());
		 this.getFormHM().put("sortcond",cond.toString());
		StringBuffer strsql=new StringBuffer();
		String b0110="";
		String e0122="";
		String e01a1="";
		String a0101="";
		try{
		    strsql.append("select b0110,e0122,e01a1,a0101 from ");
		    strsql.append(userbase);
		    strsql.append("A01 where a0100='");
		    strsql.append(A0100);
		    strsql.append("'");
		    this.frowset = dao.search(strsql.toString()); 
		    if(this.frowset.next())
			{
			     b0110=this.getFrowset().getString("B0110");
			     e0122=this.getFrowset().getString("E0122");
			     e01a1=this.getFrowset().getString("E01A1");
			     a0101=this.getFrowset().getString("a0101");			
			 }
		}catch(Exception e){}
		strsql.delete(0,strsql.length());
		//保存sql的字符串
	    ArrayList list=new ArrayList();                             //封装子集的数据
	/*	if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
			A0100=userView.getUserId();                             //如果A0100的值为A0100表示员工资助取其ID
	*/	
		if("notself".equals(reqhm.get("flag"))){                     //如果是业务用户进入业务平台A0100不变
			//A0100="A0100";
		}else{
			if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
				A0100=userView.getUserId();                             //如果A0100的值为A0100表示员工资助取其ID
		}
		strsql.append("select ");
		strsql.append(tablename);
		strsql.append(".a0100,");
		strsql.append(userbase);
		strsql.append("A01.b0110,");
		strsql.append(userbase);
		strsql.append("A01.e0122,");
		strsql.append(userbase);
		strsql.append("A01.e01a1,");
		strsql.append(userbase);
		strsql.append("A01.a0101,");
		strsql.append(tablename);
		strsql.append(".i9999,");
		strsql.append(tablename);
		strsql.append(".state,");
		strsql.append(tablename);
		strsql.append(".title,");
		strsql.append(tablename);
		strsql.append(".flag,");
		strsql.append(tablename);
		strsql.append(".fileid,");
// WJH 2013-6-13
		if(userView.isSuper_admin())
			mediasort = "";
		
		strsql.append("mediasort.sortname from " + userbase + "A01 INNER JOIN " + tablename + " ON " + userbase + "A01.A0100=" + tablename + ".A0100 ");
		strsql.append(" LEFT JOIN mediasort ON " + tablename + ".Flag=mediasort.flag ");
		strsql.append(" where ");
		strsql.append(tablename);
		strsql.append(".A0100='");
		strsql.append(A0100);
		strsql.append("' and ");
		if(userView.isSuper_admin()) {
			strsql.append("(" +tablename + ".flag is null OR upper(" + tablename);
			strsql.append(".flag)<>'P')");
		}else{
			strsql.append("upper(" + tablename);
			strsql.append(".flag)<>'P' ");
		}
        //sunx,1110,+
		if(mediasort!=null&&mediasort.length()>0)
			strsql.append(" and " + tablename + ".flag in("+mediasort+")");		  
		
		strsql.append(" and dbflag=1");		  
		
        try {	   
          //获取子集的纪录数据
		  this.frowset = dao.search(strsql.toString() + " order by i9999");             
		  while(this.frowset.next()) {
		     DynaBean vo=new LazyDynaBean();
		     vo.set("a0100",this.getFrowset().getString("A0100"));
		     vo.set("i9999",Integer.toString(this.getFrowset().getInt("I9999")));
		     vo.set("title",this.getFrowset().getString("TITLE")==null?"":this.getFrowset().getString("TITLE"));
		     // WJH 分类可能为空
		     if(this.getFrowset().getString("SORTNAME")==null)
		    	 vo.set("flag"," ");
		     else
		    	 vo.set("flag",this.getFrowset().getString("SORTNAME"));
		     vo.set("state",this.getFrowset().getString("STATE"));
		     vo.set("fileid",StringUtils.isEmpty(this.getFrowset().getString("fileid")) ? "" : this.getFrowset().getString("fileid"));
	         list.add(vo);
		  }
		 }catch(SQLException sqle)
		 {
		   sqle.printStackTrace();
		   throw GeneralExceptionHandler.Handle(sqle);
		 }
		 finally
		 {
		     String virAxx = SystemConfig.getPropertyValue("virtualOrgSet");
	         virAxx = StringUtils.isEmpty(virAxx) ? "" : virAxx; 
	         this.getFormHM().put("virAxx", virAxx);
	            
		 	if(b0110 !=null && b0110.trim().length()>0)
				 b0110=AdminCode.getCode("UN",b0110)!=null?AdminCode.getCode("UN",b0110).getCodename():"";
			if(e0122 !=null && e0122.trim().length()>0)
				e0122=AdminCode.getCode("UM",e0122)!=null?AdminCode.getCode("UM",e0122).getCodename():"";
			if(e01a1 !=null && e01a1.trim().length()>0)
				e01a1=AdminCode.getCode("@K",e01a1)!=null?AdminCode.getCode("@K",e01a1).getCodename():"";
    	    
			this.getFormHM().put("strsql",strsql.toString());
			this.getFormHM().put("detailinfolist",list);   
    	    this.getFormHM().put("b0110",b0110);
    	    this.getFormHM().put("e0122",e0122);
    	    this.getFormHM().put("e01a1",e01a1);//压回页面
    	    this.getFormHM().put("a0101",a0101);
		 }
	
	  

	}
	
	
	
	public String CheckMain(ContentDAO dao,String kind,String dbname,String A0100)
	{
		StringBuffer sb = new StringBuffer();
		String retstr = "";
		if("6".equals(kind)) // 人员
		{
			sb.append(" select *  from "+dbname+"a01 ");
			sb.append(" where a0100 = '"+A0100.toUpperCase()+"'");
		}else if("0".equals(kind)) // 职位
		{
			sb.append(" select * from k01 ");
			sb.append(" where  e01a1='"+A0100.toUpperCase()+"' ");
		}else	
		{
			sb.append(" select * from b01 ");
			sb.append(" where  b0110='"+A0100.toUpperCase()+"' ");
		}
		try
		{
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next())
				retstr = "yes";
			else
				retstr = "no";
			
			if(this.userView.isSuper_admin())
				retstr = "yes";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}

}
