package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ShowTwoDimResultTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			String userbase=(String)this.getFormHM().get("userbase");
			if(userbase==null|| "".equals(userbase))
			{
				 userbase="Usr";
			 } 
			String userbases=(String)this.getFormHM().get("userbases");
			String infoFlag = (String)this.getFormHM().get("infor_Flag");
			//String statId=(String)this.getFormHM().get("statid");
			String selOne=(String)this.getFormHM().get("selOne");
			String selTwo=(String)this.getFormHM().get("selTwo");
			int v=(int)Integer.parseInt((String)this.getFormHM().get("vv"));
			int h=(int)Integer.parseInt((String)this.getFormHM().get("hh"));
			String result = (String)this.getFormHM().get("result");
			String chartType = (String)this.getFormHM().get("chartType");
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String chartclick = (String)hm.get("chartclick");
			hm.remove("chartclick");
			if(("true".equals(chartclick) && "29".equals(chartType))){//add by xiegh on 20170902  bug:31179   配置相同的横纵轴 ， 折线图和柱状图 显示出来的横纵轴不一样，获取sql语句时需要调换横纵条件 
				int j = v;
				v=h;
				h=j;
			}
		     boolean ret=true;
			 if(result==null|| "".equals(result)|| "0".equals(result))
			   {
			      ret=true; 
			   }else{
			      ret =Boolean.getBoolean(result);
			   }
			StringBuffer orderby=new StringBuffer();
			String sql=new StatDataEncapsulation().getDataSQL(selOne,selTwo,userbase,"",v,h,userView.getUserName(),userView.getManagePrivCode(),userView,infoFlag,ret);
			
		    StringBuffer strsql=new StringBuffer();
		    strsql.append("select ");
		  
		    if("1".equals(infoFlag))
		    {
		    	userbase=userbase.toUpperCase();
		    	strsql.append(" distinct ");
		        strsql.append(userbase);
		        strsql.append("a01.a0100 as a0100,");
		        strsql.append("## as db,");
		        strsql.append("a0000 as a0000,");
		        strsql.append(userbase);        
		        strsql.append("a01.b0110 as b0110,");
	            strsql.append(userbase);
	            strsql.append("a01.e0122 as e0122,");
	            strsql.append(userbase);
	            strsql.append("a01.e01a1 as e01a1,a0101,username");
	            
		        String tmpsql =(strsql.toString()+sql).toUpperCase();
		        StringBuffer sb = new StringBuffer();
		        if(userbases!=null&&userbases.length()>0){
			        if(userbases.indexOf("`")==-1){
						sb.append(" from ("+tmpsql.replaceAll(userbase, userbases).replaceAll("##", "'"+getStart(0)+userbases+"'")+"");
					}else{
						String[] tmpdbpres=userbases.split("`");
						for(int n=0;n<tmpdbpres.length;n++){
							String tmpdbpre=tmpdbpres[n];
							if(tmpdbpre.length()==3){
								if(sb.length()>0){
									sb.append(" union all "+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
								}else{
									sb.append(" from ("+tmpsql.replaceAll(userbase, tmpdbpre).replaceAll("##", "'"+getStart(n)+tmpdbpre+"'"));
								}
							}
						}
					}
		        }else{
		        	sb.append(" from ("+tmpsql.replaceAll("##", "'"+getStart(0)+userbase+"'")+"");
		        }
		        sql=sb.toString()+") tt";
		        
		        strsql.setLength(0);
		        strsql.append("select a0000,");
		        strsql.append("a0100,");
		        strsql.append("b0110,");
	            strsql.append("e0122,");
	            strsql.append("e01a1");
		        strsql.append(",a0101,UserName,db ");
		        
//		    	 strsql.append(userbase);
//		    	 strsql.append("A01.A0100,B0110,E0122,E01A1,A0101,UserName ");
		    	 orderby.append(" order by db,B0110,E0122,");
				 orderby.append("a0000");
				 
		    }
		    if("2".equals(infoFlag)){
		    	strsql.append(" * ");
		    	orderby.append(" order by b01.b0110");
		    }
           if("3".equals(infoFlag))
           {
		    	strsql.append(" * ");
		    	orderby.append(" order by K01.e01a1");
		    }
		   
	        this.getFormHM().put("strsql",strsql.toString());
		    this.getFormHM().put("cond_str",sql);
		    this.getFormHM().put("order_by",orderby.toString());
		    this.getFormHM().put("infor_Flag",infoFlag);		    
	   }catch(Exception e)
	   {
	   	e.printStackTrace();
    	throw GeneralExceptionHandler.Handle(e);
	   }
	}

	private String getStart(int i){
		String [] str={"A","B","C","D","E","F","G","H","I","J","K","O","P","Q","R","S","T","U","V","X","Y","Z"};
		return str[i];
	}
}
