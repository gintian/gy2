package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:按检索条件导入人员</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 7, 2010 11:02:24 AM</p> 
 *@author dengc
 *@version 5.0
 */
public class ImportIndexMenTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("tabid");
			String flag=(String)this.getFormHM().get("flag");   //   1:清空当前人员,重新引入  2:不清空,引入符合条件的数据
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			String no_priv_ctrl=tablebo.getNo_priv_ctrl(); //手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
			String intbase=tablebo.getInit_base();
			String factor=tablebo.getFactor();
			String init_base=tablebo.getInit_base();
			ArrayList dblist=new ArrayList();
			ContentDAO dao=new ContentDAO(this.frameconn);
			
			if(this.userView.isSuper_admin())
			{
				this.frowset=dao.search("select * from dbname");
				while(this.frowset.next())
					dblist.add(this.frowset.getString("pre"));
			}
			else
			{
				if(init_base!=null&&init_base.trim().length()>0)
				{
					if(this.userView.getDbpriv().toString().toLowerCase().indexOf(","+init_base.toLowerCase()+",")==-1)
						return;
					dblist.add(init_base);
				}
				else
				{
					String[] temps=this.userView.getDbpriv().toString().split(",");
					for(int i=0;i<temps.length;i++)
					{
						if(temps[i].trim().length()>0)
							dblist.add(temps[i]);
					}
				}	
			}
			
			if("1".equals(flag))
				dao.update("delete from "+this.userView.getUserName()+"templet_"+tabid);
			
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			for(int e=0;e<dblist.size();e++)
			{
				String BasePre=(String)dblist.get(e);
				
				if(intbase!=null&&intbase.trim().length()>0)
				{
					if(!intbase.equalsIgnoreCase(BasePre))
						continue;
				}
				
				StringBuffer sql=new StringBuffer();
 
				sql.append("select a0100 from ");
				int infoGroup = 0; // forPerson 人员
				int varType = 8; // logic								
				String whereIN=InfoUtils.getWhereINSql(this.userView,BasePre);
				whereIN="select a0100 "+whereIN;		
				if("1".equals(no_priv_ctrl))
					whereIN="";
				YksjParser yp = new YksjParser(this.userView ,alUsedFields,
						YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
				YearMonthCount ymc=null;	
				yp.setSupportVar(true,"select  *  from   midvariable where nflag=0 and templetid= "+tabid);  //支持临时变量
				yp.run_Where(factor, ymc,"","", dao, whereIN,this.frameconn,"A", null);
				String tempTableName = yp.getTempTableName();
				sql.append(tempTableName);
				sql.append(" where " + yp.getSQL());
				
				if("2".equals(flag))
				{
					sql.append(" and a0100 not in (select a0100 from ");
					sql.append(this.userView.getUserName()+"templet_"+tabid);
					sql.append(" where upper(basepre)='");
					sql.append(BasePre.toUpperCase());
					sql.append("')");
				}
				
				ArrayList a0100list =new ArrayList();
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next())
					a0100list.add(this.frowset.getString("a0100"));
				
				if(a0100list.size()==0)
					continue;
				
				if(a0100list.size()<=500)
					tablebo.impDataFromArchive(a0100list,BasePre);
				else
				{
					ArrayList tempList=null;
					int size=a0100list.size();
					int n=size/500+1;
					for(int i=0;i<n;i++)
					{
						tempList=new ArrayList();
						for(int j=i*500;j<(i+1)*500;j++)
						{
							if(j<a0100list.size())
								tempList.add((String)a0100list.get(j));
							else
								break;
						}
						if(tempList.size()>0)
							tablebo.impDataFromArchive(tempList,BasePre);
						
					}
					
				}
				
				
				
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
