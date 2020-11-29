 /*
 * Created on 2005-6-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat.history;

 import com.hjsj.hrms.businessobject.parse.parsebusiness.Factor;
 import com.hjsj.hrms.businessobject.stat.StatCondAnalyse;
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
 import java.util.HashSet;
 import java.util.Iterator;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchStatDoubleDataTrans extends IBusiness {

	
    private String getMainQueryFields(ArrayList list,String infokind)
    {
        StringBuffer strfields=new StringBuffer();
        int j=0;
        if("1".equals(infokind))
        {
        	for(int i=0;i<list.size();i++)
            {
                FieldItem item=(FieldItem)list.get(i);
    			if("b0110,e0122".indexOf(item.getItemid().toLowerCase())!=-1)
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
		
		String uniqueitem = (String)this.getFormHM().get("uniqueitem");
		String backdate = (String)this.getFormHM().get("backdates");
		StatCondAnalyse cond = new StatCondAnalyse();
		StringBuffer orderby=new StringBuffer();	
		String nbase = (String)this.getFormHM().get("nbase");
		String strlexpr= (String)this.getFormHM().get("strlexpr");
		String strfactor= (String)this.getFormHM().get("strfactor");
		strfactor = SafeCode.decode(strfactor);
		strlexpr = SafeCode.decode(strlexpr);
		try{
		String columns="";
		ArrayList mainlist=new ArrayList();
		String strQuery="";
		StringBuffer strsql=new StringBuffer();

	    try
	    {
	    	
				strQuery =cond.getCondQueryString(
						strlexpr,
						strfactor,
						"Usr",
						false,
						userView.getUserName(),
						"",userView,"1",true);
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
					//strQuery = strQuery.substring(0,indexI9999-13);
					strQuery = strQuery.substring(0,indexI9999-5)+"))";*/
				strQuery=delI9999(strQuery);
				size = fieldsetlist.size();
				//查询历史时点特殊处理，不然查询人数不对  wangb 2020-01-07
				strQuery = strQuery.replaceAll("WHERE", "").replaceAll("where", "");
				for	(int n=0;n<size;n++){
					String fieldsetid = (String)fieldsetlist.get(n);
					strQuery=strQuery.replaceAll((nbase+fieldsetid).toUpperCase()+"\\.", "heh"+n+".");
					strQuery=strQuery.replaceAll((nbase+fieldsetid).toUpperCase(), "hr_emp_hisdata heh"+n+",hr_hisdata_list hh_"+n+" WHERE heh"+n+".id=hh_"+n+".id and "+Sql_switcher.dateToChar("hh_"+n+".create_date", "yyyy-MM-dd")+"='2019-12-06' and ");
				}
				strQuery=strQuery.replaceAll(("UsrA01\\.").toUpperCase(), "heh.");
				strQuery=strQuery.replaceAll(("UsrA01 Left join").toUpperCase(), "hr_emp_hisdata heh left join");
				strQuery=strQuery.replaceAll(("UsrA01").toUpperCase(), "hr_emp_hisdata");

		        strQuery = "from hr_emp_hisdata heh right join hr_hisdata_list hhl on heh.id=hhl.id where hhl.create_date="+Sql_switcher.dateValue(backdate)+" and " + strQuery;

				mainlist= getStatItemList(strfactor,"1");
		        strsql.append("select distinct a0000,");
		        strsql.append(uniqueitem+",");        
		        strsql.append("b0110,");
	            strsql.append("e0122,");
		        columns=getMainQueryFields(mainlist,"1");     
		        strsql.append(columns);
		        if(!strsql.toString().contains("e01a1")){
		        	strsql.append(",e01a1");
		        }
				orderby.append(" order by ");
				orderby.append("a0000");
				this.getFormHM().put("columns", columns.toUpperCase()+",B0110,E0122,E01A1,"+uniqueitem);
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(e);
	    }
		
		
	    
        this.getFormHM().put("strsql",strsql.toString());
	    this.getFormHM().put("cond_str",strQuery+" and nbase in('"+nbase.replace(",","','")+"')"); 
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
	
	private String delI9999(String str){
		int index=0;
		int f=0;
		str=str.replaceAll("I9999 IS NULL", "1=2");
		while(f<20&&(index=str.indexOf("AND (I9999"))>5){
			//str=str.substring(0,index)+str.substring(index+69); 少算了8个字符   guodd 14-10-20
			str=str.substring(0,index)+str.substring(index+77);
			index=str.indexOf("AND (I9999");
			f++;
		}
		while(f<20&&(index=str.indexOf("AND I9999"))>5){
			//str=str.substring(0,index)+str.substring(index+66);少算了8个字符  guodd 14-10-20
			str=str.substring(0,index)+str.substring(index+74);
			index=str.indexOf("AND I9999");
			f++;
		}
		return str;
	}
}
