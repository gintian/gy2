package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 检查数据处理人员
 * <p>Title:CheckClassUserTrans.java</p>
 * <p>Description>:CheckClassUserTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jun 9, 2010 3:59:27 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class CheckClassUserTrans extends IBusiness 
{
    public void execute() throws GeneralException 
	{
        // 0: 数据处理,  1: 数据处理-个别处理 , 2:数据处理进度监测, 3:生成日明细进度监测
        String tran = (String)this.getFormHM().get("tran");
        tran = tran == null ? "" : tran;
        
        if ("3".equals(tran)) {
            this.getFormHM().put("flag", (String)userView.getHm().get("analyse_result"));
            return;
        }
        
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        
    	String hasTheCount = "0";
    	String hasTheCollect = "0";
    	if(userView.hasTheFunction("0C3121") || userView.hasTheFunction("2702021"))
    		hasTheCount = "1";
    	
    	if(userView.hasTheFunction("0C3122") || userView.hasTheFunction("2702022"))
    		hasTheCollect = "1";
    	
    	this.getFormHM().put("hasTheCount", hasTheCount);
    	this.getFormHM().put("hasTheCollect", hasTheCollect);
    	
    	String analyseType = "";
		String mark = KqParam.getInstance().getData_processing();
		mark = mark != null && mark.length() > 0 ? mark : "0";
		if("1".equalsIgnoreCase(mark))
		{
			analyseType = "101";
		}else
		{
			analyseType = "1";
		}
		
		if("2".equals(tran)) { //数据处理进度监测
	        String analyse_result = (String)this.userView.getHm().get("analyse_result");
	        if ("begin".equalsIgnoreCase(analyse_result) || "finished".equalsIgnoreCase(analyse_result))
	            this.getFormHM().put("flag", analyse_result);
	        else if ("error".equalsIgnoreCase(analyse_result)) {
	            this.getFormHM().put("flag", analyse_result);
	            this.getFormHM().put("error_info", (String)this.userView.getHm().get("error_info"));
	        } else {	        
                //简易办法，查看有没有自己正在处理的数据
    		    String info = analysingInfo(dao, analyseType);
                this.getFormHM().put("flag", info);
	        }
            return;
        }
		
        if(!"101".equals(analyseType))
        {
            this.getFormHM().put("flag", "0");
            return;         
        }
		
		String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");
		if(start_date==null||start_date.length()<=0)
			throw new GeneralException("","处理起始时间不能为空！","","");
		
		if(end_date==null||end_date.length()<=0)
			throw new GeneralException("","处理结束时间不能为空！","","");
		
		start_date = start_date.replaceAll("-","\\.");
		end_date = end_date.replaceAll("-","\\.");
		String tablename="kq_analyse_result";
		StringBuffer sql=new StringBuffer();
		sql.append("select distinct cur_user from "+tablename+" where ");
		sql.append(" q03z0>='"+start_date+"' and q03z0<='"+end_date+"'");	
		sql.append(" and "+Sql_switcher.isnull("cur_user", "'"+this.userView.getUserName()+"'")+"<>'"+this.userView.getUserName()+"' ");
		String codewhere="";
		
		String flag="0";
		String cur_user="";
		
		if("0".equals(tran))//数据处理
		{
			String a_code=(String)this.getFormHM().get("a_code");
			String kind="2";
			String code="";
			if(a_code.length()>2)
			{
				code=a_code.substring(2);
			}
			if(a_code.indexOf("UN")!=-1)
			{
				kind="2";
				codewhere="b0110 like '"+code+"%'";	
			}else if(a_code.indexOf("UM")!=-1)
			{
				kind="1";
				codewhere="e0122 like '"+code+"%'";
				
			}else if(a_code.indexOf("@K")!=-1)
			{
				kind="0";
				codewhere="e01a1 like '"+code+"%'";	
			}else if(a_code.indexOf("EP")!=-1)
			{
				kind="-1";
				String nbase=(String)this.getFormHM().get("nbase");			
				codewhere=" a0100='"+code+"' and nbase='"+nbase+"'";
			}
			
			ArrayList kq_dbase_list=new ArrayList();
			KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
			if("-1".equals(kind))
			{
				String nbase = (String)this.getFormHM().get("nbase");								
				kq_dbase_list.add(nbase);
			}				
			else
				  kq_dbase_list = kqUtilsClass.setKqPerList(code,kind);
			
			for(int i=0;i<kq_dbase_list.size();i++)
			{
				String nbase=(String)kq_dbase_list.get(i);
				String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);					
				String sqls=sql.toString()+" and "+codewhere+" and nbase='"+nbase+"'";
				if(!this.userView.isSuper_admin())
			    {
			    	 if(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)
			    		 sqls+=" and  EXISTS(select a0100 "+whereIN+" and "+nbase+"A01.a0100="+tablename+".a0100)";
				     else
				    	 sqls+=" and  EXISTS(select a0100 "+whereIN+" where "+nbase+"A01.a0100="+tablename+".a0100)";
			    }
				try {
					this.frowset=dao.search(sqls);
					if(this.frowset.next())
					{
						flag="1";
						cur_user=this.frowset.getString("cur_user");
						break;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}else if("1".equals(tran))//个别数据处理
		{
			String nbase=(String)this.getFormHM().get("nbase");
			String date=(String)this.getFormHM().get("specdata");
			if(date==null||date.length()<=0)
			{
				this.getFormHM().put("flag", "0");
				return;
			}
			sql.append(" and nbase='"+nbase+"'");
			sql.append(" and a0100 in(");
			String str[]= date.split("/");			
	    	for(int i=0;i<str.length;i++)
	        {
	    		String value=str[i];
	    		String strvalue[]=value.split(",");
	    		String a0100=strvalue[1];
	    		a0100=a0100.substring(1,a0100.length()-1);
		    	sql.append("'"+a0100+"',");		
	    	}
	    	sql.setLength(sql.length()-1);			
	    	sql.append(")");
	    	try {
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					flag="1";			
					cur_user=this.frowset.getString("cur_user");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else
		{
			this.getFormHM().put("flag", "0");
			return;
		}
		this.getFormHM().put("flag", flag);		
		this.getFormHM().put("cur_user", cur_user);
	}
    
    private String analysingInfo(ContentDAO dao, String analyseType) {
        StringBuilder info = new StringBuilder();
        StringBuilder sql = new StringBuilder();
        String whr = "";
        
        String resultTab = "";
        if ("101".equals(analyseType)) {
            resultTab = "kq_analyse_result";
            whr = "cur_user='" + this.userView.getUserName() + "'";
        } else {
            resultTab = "kt_" + this.userView.getUserName() + "_dd";
        }
        
        sql.append("SELECT COUNT(*) rec FROM ").append(resultTab);
        if (!"".equals(whr))
            sql.append(" WHERE ").append(whr);
        
        int total = 0;
        int analysed = 0;
        
        //本次需处理的记录总数
        try {
            this.frowset = dao.search(sql.toString());
            if (this.frowset.next())
                total = this.frowset.getInt("rec");
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        /* 当前已处理完毕的记录数
         * 不是精确数字，要得到确切进度需要在数据处理存储过程里记录进度，暂不提供
         */
        sql.setLength(0);
        sql.append("SELECT COUNT(*) rec FROM ").append(resultTab);
        
        //有刷卡数据的可以认为是已处理完毕的
        sql.append(" WHERE (card_time is not null");
        
        //结果不是正常或休息的是已处理完毕的
        sql.append(" or (isok<>'正常' and isok<>'休息')");
        
        //精简模式isnormal=1的是已批量处理完的
        if ("1".equals(KqParam.getInstance().getQuickAnalyseMode()))
          sql.append(" or isnormal='1'");
        
        sql.append(")");

        if (!"".equals(whr))
            sql.append(" and ").append(whr);
        
        try {
            this.frowset = dao.search(sql.toString());
            if (this.frowset.next())
                analysed = this.frowset.getInt("rec");
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        info.append(analysed).append("/").append(total);
        return info.toString();
        
    }
}
