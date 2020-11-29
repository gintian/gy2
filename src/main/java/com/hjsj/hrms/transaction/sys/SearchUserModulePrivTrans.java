/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>Title:SearchUserModulePrivTrans</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jul 14, 20063:08:09 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchUserModulePrivTrans extends IBusiness {
	
	/**字段对应表*/
	private HashMap hm=new HashMap();
	private EncryptLockClient lockclient;
	
	public SearchUserModulePrivTrans() {
		hm.put("0",ResourceFactory.getProperty("label.module.emss"));
		hm.put("1",ResourceFactory.getProperty("label.module.mass"));
		hm.put("2",ResourceFactory.getProperty("label.module.trss"));
		hm.put("3",ResourceFactory.getProperty("label.module.prss"));
		hm.put("4",ResourceFactory.getProperty("label.module.ress"));
		hm.put("5",ResourceFactory.getProperty("label.module.atss"));
		hm.put("6",ResourceFactory.getProperty("label.module.atma"));
		hm.put("7",ResourceFactory.getProperty("label.module.rema"));
		hm.put("8",ResourceFactory.getProperty("label.module.sama"));
		hm.put("9",ResourceFactory.getProperty("label.module.prma"));
		hm.put("10",ResourceFactory.getProperty("label.module.trma"));
		hm.put("11",ResourceFactory.getProperty("label.module.bama"));
		hm.put("12",ResourceFactory.getProperty("label.module.gzzd"));
		hm.put("13",ResourceFactory.getProperty("label.module.bbgl"));		
		hm.put("14",ResourceFactory.getProperty("label.module.bxfl"));	
		hm.put("15",ResourceFactory.getProperty("label.module.htgl"));	
		hm.put("16",ResourceFactory.getProperty("label.module.fmtl"));
		hm.put("17",ResourceFactory.getProperty("label.module.rm"));	
		hm.put("18",ResourceFactory.getProperty("label.module.cgzs"));	
		hm.put("19",ResourceFactory.getProperty("label.module.ldjc"));		
		hm.put("20",ResourceFactory.getProperty("label.module.rsyd"));		
		hm.put("21",ResourceFactory.getProperty("label.module.jd"));	
		hm.put("22",ResourceFactory.getProperty("label.module.eatss"));			
	}

	/**
	 * 求operuser表对应字段列表
	 * @return
	 */
	private ArrayList getFieldList(int type)
	{
		ArrayList list=new ArrayList();
		FieldItem item=null;//new FieldItem();
		/**业务平台用户*/
//		if(type==0)
//		{
//			item.setItemid("username");
//			item.setItemdesc("username");
//			item.setItemtype("A");
//			item.setCodesetid("0");	
//			list.add(item);
//		}
		int[] modules=lockclient.getPrivModule();
		int j=0;
		for(int i=6;i<23;i++)
		{
			if(modules[i]==0)
			{
				//j++;
				continue;
			}
			item=new FieldItem();
			item.setItemid("c"+(i));
			item.setItemdesc((String)this.hm.get(String.valueOf(i)));
			item.setItemtype("A");
			item.setCodesetid("0");
			list.add(item);
		}
		return list;
	}
	/**
	 * 组合查询SQL串
	 * @param type
	 * @param fieldlist
	 * @return
	 */
	private String getQueryString(int type,ArrayList fieldlist)throws Exception 
	{
	  StringBuffer strsql=new StringBuffer();	
	  String name=null;	  
	  try
	  {
		int k=0;
		String cx=null;
		if(type==0)
		{
			strsql.append("select username ,");	

			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				cx=item.getItemid();
				k=Integer.parseInt(cx.substring(1));
				name=Sql_switcher.substr("module_ctrl",String.valueOf(k+1),"1");
				strsql.append(name);
				strsql.append(" ");
				strsql.append(item.getItemid());
				strsql.append(",");
			}	
			strsql.setLength(strsql.length()-1);			
		}
		else
		{
			ArrayList dblist=getDbList();
			for(int i=0;i<dblist.size();i++)
			{
				String dbpre=(String)dblist.get(i);
				strsql.append("select a0000,a0100,a0101,b0110,e0122,e01a1,'");
				strsql.append(dbpre);
				strsql.append("' as dbpre,");
				for(int j=0;j<fieldlist.size();j++)
				{
					FieldItem item=(FieldItem)fieldlist.get(j);
					cx=item.getItemid();
					k=Integer.parseInt(cx.substring(1));					
					name=Sql_switcher.substr("groups",String.valueOf(k+1),"1");	//6去掉自助服务功能
					strsql.append(name);
					strsql.append(" ");
					strsql.append(item.getItemid());
					strsql.append(",");
				}					
				strsql.setLength(strsql.length()-1);
				strsql.append(" from ");
				strsql.append(dbpre);
				strsql.append("a01");
				strsql.append(" UNION ");
			}
			strsql.setLength(strsql.length()-7);			
		}

	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  throw GeneralExceptionHandler.Handle(ex);
	  }
	  return strsql.toString();
	}
	/**
	 * 取登录用户库的列表
	 * @return
	 * @throws Exception
	 */
    private ArrayList getDbList()throws Exception
    {
        /**登录参数表*/
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
        String db_strs = login_vo.getString("str_value").toLowerCase();
		ArrayList dblist=this.userView.getPrivDbList(); 
		
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		dblist=dbvo.getAllDbNameVoList();
		ArrayList list=new ArrayList();
		for(int i=0;i<dblist.size();i++)
		{
			RecordVo dbname=(RecordVo)dblist.get(i);
			if(db_strs.indexOf(dbname.getString("pre").toLowerCase())==-1)
				continue;
			//CommonData vo=new CommonData();			
			//vo.setDataName(dbname.getString("dbname"));
			//vo.setDataValue(dbname.getString("pre"));
			list.add(/*vo*/dbname.getString("pre"));
		}        
        return list;
    }
	/**
	 * 求显示的字段列表
	 * @param type
	 * @param fieldlist
	 * @return
	 */
	private String getDisplayColumns(int type,ArrayList fieldlist)
	{
		StringBuffer columns=new StringBuffer();
		if(type==0)
		{
			columns.append("username,");
		}
		else
		{
			columns.append("b0110,e0122,e01a1,a0101,dbpre,a0100,");
			
		}

		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem item=(FieldItem)fieldlist.get(i);	
			String name=item.getItemid();
			name=name.substring(1);
			//if(lockclient.getModuleCount(Integer.parseInt(name))==0)
			//	continue;			
			columns.append(item.getItemid());
			columns.append(",");
		}
		return columns.toString();
	}
	
	public void execute() throws GeneralException {
	 int type=0;
	 try
	 {
		//test 
//		ArrayList list=DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,Constant.ALL_FIELD_SET);
//		for(int i=0;i<list.size();i++)
//		{
//			FieldItem item=(FieldItem)list.get(i);
//			System.out.println("fielditem="+item.toString());
//		}
		/**控制显示前台的页签*/ 
		String flag=(String)this.getFormHM().get("flag");
		if(flag==null|| "".equals(flag))
			flag="1";
		if("1".equals(flag))
			type=1;
		else
			type=0;
		lockclient =(EncryptLockClient)this.getFormHM().get("lock");		
		ArrayList fieldlist=getFieldList(type);
		this.getFormHM().put("strsql",getQueryString(type,fieldlist));
		this.getFormHM().put("columns",getDisplayColumns(type,fieldlist));
		this.getFormHM().put("fieldlist",fieldlist);
	 }
	 catch(Exception ex)
	 {
		 ex.printStackTrace();
		 throw GeneralExceptionHandler.Handle(ex);
	 }
	}

}
