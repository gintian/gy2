package com.hjsj.hrms.transaction.mobileapp.statis;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.CombineFactor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class GeneralQueryStat {
	private String lexpr;
	private String lfactor;
	private String infokind;
	private String history;
    public String getInfokind() {
		return infokind;
	}
	public void setInfokind(String infokind) {
		this.infokind = infokind;
	}
	public  void getGeneralQueryLexrfacor(String[] curr_id,String userbase,String history,Connection conn)
    {
       if(curr_id==null||curr_id.length==0||curr_id[0].length()==0)
            return;
        if(history==null|| "".equals(history))
            history="0";
        ContentDAO dao=new ContentDAO(conn);
        RecordVo vo=new RecordVo("lexpr");
        vo.setString("id",curr_id[0]);
        try
        {
            vo=dao.findByPrimaryKey(vo);
            this.lexpr=vo.getString("lexpr");
            this.lfactor=vo.getString("factor");
            this.history=vo.getString("history");
            String type=vo.getString("type");
            if(this.infokind==null||this.infokind.length()<=0)
            	this.infokind="1";
            int i=Integer.parseInt(this.infokind);
            switch(i)
   		    {
   		       case 1:
		       {
		    	   if(type==null||!"1".equals(type))
	               {
	            		this.lexpr="";
	            		this.lfactor="";
	            		this.history="";
	            		return;
	               }
		    	   break;
		       }case 2:
		       {
		    	   if(type==null||!"2".equals(type))
	               {
	            		this.lexpr="";
	            		this.lfactor="";
	            		this.history="";
	            		return;
	               }
		    	   break;
		       }case 3:
		       {
		    	   if(type==null||!"3".equals(type))
	               {
	            		this.lexpr="";
	            		this.lfactor="";
	            		this.history="";
	            		return;
	               }
		    	   break;
		       }
   		    } 		
            String fuzzy=vo.getString("fuzzyflag");
            this.lfactor=this.lfactor.replaceAll("\\$THISMONTH\\[\\]","当月");  /*兼容报表管理、常用查询*/
            /**表过式分析*/      
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();  	             
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();  	         	
        }
        
    
    }
	public String getLexpr() {
		return lexpr;
	}
	public void setLexpr(String lexpr) {
		this.lexpr = lexpr;
	}
	public String getLfactor() {
		return lfactor;
	}
	public void setLfactor(String lfactor) {
		this.lfactor = lfactor;
	}

	public void combineCond(String condid,String seclexpr,String secfactor,Connection conn){
		ContentDAO dao=new ContentDAO(conn);
        RecordVo vo=new RecordVo("lexpr");
        String [] condids = condid.split(",");
        
        try
        {
        	this.lexpr=seclexpr;
	    	this.lfactor=secfactor;
        	for(int i=0;i<condids.length;i++){
        		if(condids[i].length()>0){
	                vo.setString("id",condids[i]);
		            vo=dao.findByPrimaryKey(vo);
		            String[] style= getCombinLexprFactor(vo.getString("lexpr"),vo.getString("factor"),lexpr,this.lfactor);
				    if(style!=null && style.length==2)
				    {
				    	this.lexpr=style[0];
				    	this.lfactor=style[1];
				    }
        		}
        	}
        	this.lfactor=this.lfactor.replaceAll("\\$THISMONTH\\[\\]","当月");  /*兼容报表管理、常用查询*/
            /**表过式分析*/
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();  	             
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();  	         	
        }
	}
	
	//合并表达式
	private String[] getCombinLexprFactor(String lexpr,String factor,String seclexpr,String secfactor)
	{
		String[] style=new String[2];
		ArrayList lexprFactor=new ArrayList();
		factor = PubFunc.keyWord_reback(factor);
		lexprFactor.add(lexpr + "|" + factor);
		lexprFactor.add(seclexpr + "|" + secfactor);
		CombineFactor combinefactor=new CombineFactor();
		String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
		StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
		if(Stok.hasMoreTokens())
		{
			style[0]=Stok.nextToken();
			style[1]=Stok.nextToken();
		}
		return style;
	}
	public String getHistory() {
		return history;
	}
	public void setHistory(String history) {
		this.history = history;
	}
}
