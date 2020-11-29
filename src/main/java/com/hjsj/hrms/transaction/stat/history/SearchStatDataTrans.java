 /*
 * Created on 2005-6-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat.history;

 import com.hjsj.hrms.businessobject.parse.parsebusiness.Factor;
 import com.hjsj.hrms.businessobject.stat.StatCondAnalyse;
 import com.hjsj.hrms.utils.PubFunc;
 import com.hrms.frame.codec.SafeCode;
 import com.hrms.frame.dao.ContentDAO;
 import com.hrms.hjsj.sys.DataDictionary;
 import com.hrms.hjsj.sys.FieldItem;
 import com.hrms.hjsj.sys.FieldSet;
 import com.hrms.hjsj.utils.Sql_switcher;
 import com.hrms.struts.exception.GeneralException;
 import com.hrms.struts.exception.GeneralExceptionHandler;
 import com.hrms.struts.facade.transaction.IBusiness;
 import com.hrms.struts.valueobject.UserView;
 import org.apache.commons.beanutils.LazyDynaBean;

 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchStatDataTrans extends IBusiness {

	
    private String getMainQueryFields(ArrayList list,String infokind)
    {
        StringBuffer strfields=new StringBuffer();
        int j=0;
        if("1".equals(infokind))
        {
        	for(int i=0;i<list.size();i++)
            {
                FieldItem item=(FieldItem)list.get(i);
    			if("b0110,e0122,e01a1".indexOf(item.getItemid().toLowerCase())!=-1)
    				continue;  
                if(j!=0)
                    strfields.append(",");
                ++j;
              
                strfields.append(item.getItemid());
            }
        }else if("2".equals(infokind))
        {
        	for(int i=0;i<list.size();i++)
            {
                FieldItem item=(FieldItem)list.get(i);
    			if("b0110,".indexOf(item.getItemid().toLowerCase())!=-1)
    				continue;  
                if(j!=0)
                    strfields.append(",");
                ++j;
              
                strfields.append(item.getItemid());
            }
        }else if("3".equals(infokind))
        {
        	for(int i=0;i<list.size();i++)
            {
                FieldItem item=(FieldItem)list.get(i);
    			if("e01a1,e0122".indexOf(item.getItemid().toLowerCase())!=-1)
    				continue;  
                if(j!=0)
                    strfields.append(",");
                ++j;
              
                strfields.append(item.getItemid());
            }
        }
        
        return strfields.toString();    	
    }
    
	public void execute() throws GeneralException {
	 	// TODO Auto-generated method stub
		
		String uniqueitem= (String)this.getFormHM().get("uniqueitem");
		String backdate = (String)this.getFormHM().get("backdates");
		if(backdate.length()!=10)
			backdate=(String)this.getFormHM().get("backdate");
		StatCondAnalyse cond = new StatCondAnalyse();
		StringBuffer orderby=new StringBuffer();	
		String showflag=(String)this.getFormHM().get("showflag");
		String strlexpr="";
		String strfactor="";
		String statid=(String)this.getFormHM().get("statid");
		String sbase=getNbase(statid);
		if(sbase.endsWith(","))
			sbase=sbase.substring(0,sbase.length()-1);
		sbase=filterPrivDB(sbase,this.userView);
		if(sbase.length()==0)
			sbase="#";
		if(showflag!=null&& "1".equals(showflag))//点击图例触发，传入的是汉字
		{
			String showLegend=(String)this.getFormHM().get("showLegend");
			if(showLegend==null||showLegend.length()<=0)
				showLegend="";
			showLegend=SafeCode.decode(showLegend);
			
			LazyDynaBean bean=getStatDataForName(statid ,showLegend);
			strlexpr=(String)bean.get("lexpr");
			strfactor=(String)bean.get("factor");
			strfactor=strfactor+"`";
			
			String chart_type=(String)this.getFormHM().get("chart_type");
			String tmpbackdate = (String)((HashMap)this.getFormHM().get("requestPamaHM")).get("tmpbackdates");
			if(!"5".equals(chart_type)&&!"20".equals(chart_type))
				if(tmpbackdate!=null&&tmpbackdate.length()==10)
					backdate=tmpbackdate;
		}else
		{
			strlexpr=(String)this.getFormHM().get("strlexpr");
			strlexpr=SafeCode.decode(strlexpr);
			strlexpr=PubFunc.keyWord_reback(strlexpr);
			if(strlexpr!=null)
			  strlexpr=strlexpr.replaceAll("a","+");
			
			strfactor=(String)this.getFormHM().get("strfactor");
			strfactor=SafeCode.decode(strfactor);
			strfactor=PubFunc.keyWord_reback(strfactor);
			if(!strfactor.endsWith("`"))
				strfactor+= "`";
		}
		
		//判断查询条件中使用的指标是否包含在快照指标中，如果没有 则报错 wangrd 2013-01-03
        String[] factorfields = strfactor.toUpperCase().split("`");
        String snap_fields = (String)this.getFormHM().get("snap_fields");
        for(int n=0;n<factorfields.length;n++){
            String fld = factorfields[n];
            if(fld.length()>5){
                fld = fld.substring(0,5).toUpperCase();
                if (fld.equalsIgnoreCase(uniqueitem)) continue;
                if (snap_fields.indexOf(","+fld+",")<0){                    
                    FieldItem fielditem = DataDictionary.getFieldItem(fld);
                    if(fielditem!=null){
                        fld = fielditem.getItemdesc();
                        throw GeneralExceptionHandler.Handle(new GeneralException("指标["+fld+"]已删除，请重新设置统计条件！"));
                        
                    }   
                }
            }
        }      

		String userbase=(String)this.getFormHM().get("userbase");
		String querycond=(String)this.getFormHM().get("querycond");
		String infokind=(String)this.getFormHM().get("infokind");	
		try{
		String columns="";
		ArrayList mainlist=new ArrayList();
		String strQuery="";
		StringBuffer strsql=new StringBuffer();

	    try
	    {
	    	if(infokind!=null && "1".equals(infokind))
			{
				strQuery =cond.getCondQueryString(
						strlexpr,
						strfactor,
						"Usr",
						false,
						userView.getUserName(),
						querycond,userView,infokind,true);
				//cat.debug("---->sql======" + strQuery);
						//System.out.println(strQuery);
				String[] fields = strfactor.toUpperCase().split("`");
				int size=fields.length;
				ArrayList fieldsetlist = new ArrayList();
				for(int n=0;n<size;n++){
					String tmp = fields[n];
					if(tmp.length()>5){
						String itemid = tmp.substring(0,5);
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null){
							String fieldsetid = fielditem.getFieldsetid();
							FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
							if(!fieldset.isMainset()){
								fieldsetlist.add(fieldsetid.toUpperCase());
							}	
						}
					}
				}
				strQuery=strQuery.toUpperCase().substring(strQuery.indexOf("WHERE")+6);
				/*if(strQuery.indexOf("AND 1=2")!=-1)
					strQuery=" 1=2 AND "+strQuery;*/
				/*int indexI9999 = strQuery.indexOf("I9999");
				
				if(indexI9999>13)
					strQuery = strQuery.substring(0,indexI9999-5)+"))";*/
				strQuery=deleteI9999(strQuery);
				size = fieldsetlist.size();
				for	(int n=0;n<size;n++){
					String fieldsetid = (String)fieldsetlist.get(n);
					strQuery=strQuery.replaceAll(("Usr"+fieldsetid+"\\.").toUpperCase(), "heh"+n+".");
					//子查询添加快照时间点，否则查询的是所有的 guodd 2015-03-05
					while(strQuery.indexOf("SELECT A0100 FROM "+(userbase+fieldsetid).toUpperCase()+" WHERE")!=-1){
						strQuery = strQuery.replace("SELECT A0100 FROM "+(userbase+fieldsetid).toUpperCase()+" WHERE", "SELECT A0100 FROM hr_emp_hisdata heh"+n+" WHERE "+" heh"+n+".id="+userbase.toUpperCase()+"A01.id and ");
					}
					strQuery=strQuery.replaceAll(("Usr"+fieldsetid).toUpperCase(), "hr_emp_hisdata heh"+n);
				}
				strQuery=strQuery.replaceAll(("Usr"+"A01\\.").toUpperCase(), "heh.");
				strQuery=strQuery.replaceAll(("UsrA01 Left join").toUpperCase(), "hr_emp_hisdata heh left join");
				strQuery=strQuery.replaceAll(("Usr"+"A01").toUpperCase(), "hr_emp_hisdata");
				//strQuery=strQuery.replaceAll("FROM", "FROM hr_emp_hisdata ");
		        strQuery = "from hr_emp_hisdata heh right join hr_hisdata_list hhl on heh.id=hhl.id where hhl.create_date="+Sql_switcher.dateValue(backdate)+" and " + strQuery;

				mainlist= getStatItemList(strfactor,infokind);
		        strsql.append("select distinct a0000,");
		        strsql.append(uniqueitem+",");        
		        strsql.append("b0110,");
	            strsql.append("e0122,");
	            strsql.append("e01a1");
		        columns=getMainQueryFields(mainlist,infokind);     
		        columns = (columns+",").toUpperCase().replaceAll((uniqueitem+",").toUpperCase(), "");
		        if(columns.endsWith(","))
		        	columns = columns.substring(0, columns.length()-1);
		        strsql.append(",");
		        strsql.append(columns + " ");
				orderby.append(" order by ");
				orderby.append("a0000");
				this.getFormHM().put("columns", columns.toUpperCase()+",B0110,E0122,E01A1,"+uniqueitem);
				
		    }
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(e);
	    }
		
		
	    
        this.getFormHM().put("strsql",strsql.toString() + strQuery+" and nbase in('"+sbase.replace(",","','")+"')");//zhangcq 2016-5-23 人员取值的sql拼接
	    this.getFormHM().put("cond_str",""); 
	    this.getFormHM().put("fieldlist",mainlist);    
	    this.getFormHM().put("order_by",orderby.toString());
		}catch(Exception e){}
	
	}
	/**
	 * 取得定义的主集指标
	 * @return
	 */
	private ArrayList getStatItemList(String factors,String infokind)
	{
		ArrayList statitemlist=new ArrayList();	
		FieldItem fielditem=new FieldItem();
		String fieldname="";
		if("1".equals(infokind))
		{
			fielditem=DataDictionary.getFieldItem("b0110");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("e0122");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("e01a1");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("a0101");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);	
		}else if("2".equals(infokind))
		{
			fielditem=DataDictionary.getFieldItem("b0110");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
		}else if("3".equals(infokind))
		{
			fielditem=DataDictionary.getFieldItem("e0122");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
			fielditem=DataDictionary.getFieldItem("e01a1");
			fieldname=fielditem.getItemid();
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			statitemlist.add(fielditem);
		}
			
		HashSet fieldItemSet=getStatFieldItem(factors,this.userView);
		Iterator it = fieldItemSet.iterator();		
		while(it.hasNext())
		{
			   String item=(String)it.next();
			   if("2".equals(infokind))
			   {
				   if("b0110".equalsIgnoreCase(item))
					   continue;
			   }else if("3".equals(infokind))
			   {
				   if("e0122".equalsIgnoreCase(item)|| "e01a1".equalsIgnoreCase(item))
					   continue;
			   }else if("1".equals(infokind)){
				   if("b0110".equalsIgnoreCase(item)|| "a0101".equalsIgnoreCase(item)|| "e0122".equalsIgnoreCase(item)|| "e01a1".equalsIgnoreCase(item))
					   continue;
			   }
			   
			   fielditem=DataDictionary.getFieldItem(item);
			   fieldname=fielditem.getItemid();
			   fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			   /**
			    * cmq changed at 20120427 因为多表（数据量大）关联查询速度比较慢，
			    * 查询引擎做了优化,子集指标不能直接从返回的SQL取得  
			    */
			   if(("1".equalsIgnoreCase(infokind)&&!fielditem.isMainSet())||("1".equalsIgnoreCase(infokind)&&!fielditem.getFieldsetid().toUpperCase().startsWith("A")))
				   continue;
			   statitemlist.add(fielditem);
		}		
		return statitemlist;
	}
	/**
	 * 得到统计项
	 * @param dao
	 * @param userView
	 * @param id
	 * @param norder
	 * @return
	 */
	public HashSet getStatFieldItem(String factors,UserView userView)
	{
		HashSet fieldItemSet = new HashSet();
		if(factors==null||factors.length()<=0)
			return fieldItemSet;		
		if(factors!=null&&factors.length()>0)
		{
			String factorArr[]=factors.split("`");
			String factorstr=""; 
			for(int i=0;i<factorArr.length;i++)
			{
				factorstr=factorArr[i];
				factorstr=factorstr.toUpperCase();
				Factor factor = new Factor(userView.getDbname(), factorstr);
				String item=factor.getItem();
				if(item!=null&&item.length()>0)
				{
					fieldItemSet.add(item);
				}						
			}
		}	
		return fieldItemSet;
	}
	/**
	 * 
	 * @param statida
	 * @param name
	 * @return
	 */
	private LazyDynaBean getStatDataForName(String statid ,String name)
	{
		String sql="select * from hr_hisdata_slegend where id="+statid+" and legend='"+name+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		LazyDynaBean bean=new LazyDynaBean();
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				bean.set("lexpr", this.frowset.getString("lexpr")!=null?this.frowset.getString("lexpr"):"");
				bean.set("factor", this.frowset.getString("factor")!=null?this.frowset.getString("factor"):"");
				bean.set("norder", this.frowset.getString("norder")!=null?this.frowset.getString("norder"):"");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bean;
	}
	
	private String getNbase(String statid)
	{
		String sql="select sbase from hr_hisdata_sname where id="+statid;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String sbase="";
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				sbase=this.frowset.getString("sbase");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sbase;
	}
	
	private String filterPrivDB(String nbases,UserView userView){
		StringBuffer sb=new StringBuffer(",#");
		String[] tmps=nbases.split(",");
		for(int i=tmps.length-1;i>=0;i--){
			String dbpre=tmps[i];
			if(userView.hasTheDbName(dbpre)){
				sb.append(","+dbpre);
			}
		}
		return sb.substring(1);
	}
	/**
	 * 去掉sql语句中的i9999的条件
	 * @param str sql语句
	 * @return
	 */
	private String deleteI9999(String str){
	    str=str.replaceAll("I9999 IS NULL", "1=2");
        String where = str.replaceAll(" +"," ");
        str = "";
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        //去除 and i9999
        while(where.indexOf("AND I9999") > -1){
            str += where.substring(0, where.indexOf("AND I9999"));
            where = where.substring(where.indexOf("AND I9999"));
            int index = where.indexOf("(");
            indexs.add(index);
            where = where.substring(index + 1);
            while (indexs != null && indexs.size() > 0){
                int indexNum = where.indexOf("(");
                if(indexNum < 0)
                    break;
                
                index = 0;
                String temp = where.substring(0, indexNum +1);
                boolean flag = true;
                //判断要两个左括号中是否包含右括号
                while (temp.indexOf(")") > -1) {
                    index = index + temp.indexOf(")");
                    if(indexs.size() > 1) {
                        temp = temp.substring(temp.indexOf(")") + 1);
                        index++;
                        indexs.remove(indexs.size() -1);
                    } else {
                        flag = false;
                        break;
                    }
                }
                
                if(flag) {
                    indexs.add(indexNum);
                    where = where.substring(indexNum +1);
                } else {
                    where = where.substring(index + 1);
                    indexs.remove(0);
                }
            }
        }
        //去掉没有删除完的右括号
        while (indexs != null && indexs.size() > 0 && where.indexOf(")") > -1) {
            where = where.substring(where.indexOf(")") + 1);
            indexs.remove(indexs.size() -1);
        }
        
        where = str + where;
        str = "";
        //去除 and (i9999
        while(where.indexOf("AND (I9999") > -1){
            str += where.substring(0, where.indexOf("AND (I9999"));
            where = where.substring(where.indexOf("AND (I9999"));
            int index = where.indexOf("(");
            indexs.add(index);
            where = where.substring(index + 1);
            while (indexs != null && indexs.size() > 0){
                int indexNum = where.indexOf("(");
                if(indexNum < 0)
                    break;
                
                index = 0;
                String temp = where.substring(0, indexNum +1);
                boolean flag = true;
                //判断要两个左括号中是否包含右括号
                while (temp.indexOf(")") > -1) {
                    index = index + temp.indexOf(")");
                    if(indexs.size() > 1) {
                        temp = temp.substring(temp.indexOf(")") + 1);
                        index++;
                        indexs.remove(indexs.size() -1);
                    } else {
                        flag = false;
                        break;
                    }
                }
                
                if(flag) {
                    indexs.add(indexNum);
                    where = where.substring(indexNum +1);
                } else {
                    where = where.substring(index + 1);
                    indexs.remove(0);
                }
            }
        }
        //去掉没有删除完的右括号
        while (indexs != null && indexs.size() > 0 && where.indexOf(")") > -1) {
            where = where.substring(where.indexOf(")") + 1);
            indexs.remove(indexs.size() -1);
        }
        
        str += where;
        
        return str;
    }
}
