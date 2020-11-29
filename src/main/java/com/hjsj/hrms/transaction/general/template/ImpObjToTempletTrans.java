/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * <p>Title:非人员调入的业务模板增加处理的人员</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 26, 20069:17:33 AM
 * @author chenmengqing
 * @version 4.0
 */
public class ImpObjToTempletTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList objlist=(ArrayList)this.getFormHM().get("objlist");//如：[Usr00001402, ]
		String setname=(String)this.getFormHM().get("setname");//如：sutemplet_20
		String tabid=(String)this.getFormHM().get("tabid");
        boolean kqFlag=false;
        if (!"".equals(tabid)){
        	TemplateTableParamBo parambo=new TemplateTableParamBo(this.frameconn);
        	kqFlag= parambo.isKqTempalte(Integer.parseInt(tabid));//判断当前模板是否定义了考勤参数
        }
        
		String _sql=(String)this.getFormHM().get("_sql");//
		if(_sql!=null&&_sql.length()>0)
			_sql=SafeCode.decode(_sql);
		_sql=PubFunc.decrypt(_sql);
		_sql = PubFunc.keyWord_reback(_sql);
		try
		{
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			String no_priv_ctrl=tablebo.getNo_priv_ctrl(); //手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
            
			HashMap hm=new HashMap();
			ArrayList a0100list=new ArrayList();
			String first_base=null;
			String a0100=null;
			String unit_value=null;
			HashMap nameMap = new HashMap();
			HashMap indexMap = getIndexMap( tablebo, tabid,nameMap);
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			 
			String indexnames = ""; 
			if(_sql!=null&&_sql.length()>0)
			{
				
				this.frowset=dao.search(_sql);
				int i=0;
				ArrayList basePreList=new ArrayList();//存放选中人员的人员库
				ArrayList a0100List = new ArrayList();//存放选中人员的a0100
				while(this.frowset.next())
				{
					if(tablebo.getInfor_type()==1)
					{
						String obj_id=this.frowset.getString("dbase")+this.frowset.getString("a0100");
						if(obj_id==null|| "".equals(obj_id))
							continue;
						if("selectall".equalsIgnoreCase(obj_id))
							continue;
						String pre=obj_id.substring(0,2).toLowerCase();
						/**对人员信息群时，过滤单位、部门及职位*/
						if("UN".equalsIgnoreCase(pre)|| "UM".equalsIgnoreCase(pre)|| "@K".equalsIgnoreCase(pre))
							continue;
						pre=obj_id.substring(0,3).toLowerCase();
						/**按人员库进行分类*/
						if(!hm.containsKey(pre))
						{
							a0100list=new ArrayList();
						}
						else
						{
							a0100list=(ArrayList)hm.get(pre);
						}
						if(indexMap!=null){
							if(indexMap.get(obj_id.toLowerCase())!=null){
								
							}else{
								if(nameMap!=null&&nameMap.get(obj_id.toLowerCase())!=null)
								indexnames +=nameMap.get(obj_id.toLowerCase())+",";
								continue;
							}
						}
						a0100list.add(obj_id.substring(3));
						a0100List.add(pre+":"+obj_id.substring(3));//用于安全平台改造，判断A0100是否存在用户权限管理范围之内
						if(i==0)
						{
							first_base=pre;
							a0100=obj_id.substring(3);
						}
						i++;
						hm.put(pre,a0100list);
						basePreList.add(pre);
					}
					else 
					{
						String key="b0110";
						if(tablebo.getInfor_type()==3)
							key="e01a1";
						if(a0100list==null)
							a0100list=new ArrayList();
						if(i==0)
						{
							unit_value=this.frowset.getString(key);
						}
						i++;
						a0100list.add(this.frowset.getString(key));
					}
				}
				if(tablebo.getInfor_type()==2)
					hm.put("B",a0100list);
				if(tablebo.getInfor_type()==3)
					hm.put("K",a0100list);
				/**安全信息改造，当选人时,判断是否存在不在用户范围内的人员begin**/
				CheckPrivSafeBo safeBo = new CheckPrivSafeBo(this.frameconn,this.userView);
				if(!this.userView.isSuper_admin()){
				    for(int j=0;j<basePreList.size();j++){//过滤出用户权限的人员库
				    	 String paramBasePre =(String)basePreList.get(j);
				    	 paramBasePre = paramBasePre.trim();
				    	 String returnBasePre =safeBo.checkDb(paramBasePre);//这个方法当不越权时返回传进去的人员库，越权时返回当前人员的第一个人员库
				    	 /**当返回的人员库值的长度大于0并且不等于传进去的人员库时说明越权**/
				    	 if(returnBasePre.trim().length()>0&&!paramBasePre.equals(returnBasePre)){//如果当前用户的人员库没有这个选中人员的人员库，终止导入
				    		 throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.user.ultra.vires"));
			             }
				    }
				    
				}
				
				if(!this.userView.isSuper_admin() && !"1".equals(no_priv_ctrl)){
					/**验证管理范围，如果越权则返回实有的管理范围**/
					String paramManapriv=this.userView.getManagePrivCodeValue();
					String realManapriv=safeBo.checkOrg(paramManapriv, "");
					if (kqFlag&& userView.getKqManageValue() != null && !"".equals(userView.getKqManageValue())){
						paramManapriv=userView.getKqManageValue().substring(2);	
						realManapriv=paramManapriv;
					}
					for(int j=0;j<a0100List.size();j++){
						String paramPreA0100=(String) a0100List.get(j);
						String[]paramArray=paramPreA0100.split(":");
						String paramPre=paramArray[0].trim();//这里所有的人员库都进行了验证，如果越权的人员库，在上面就结束了
						String paramA0100=paramArray[1].trim();//这里的A0100尚未进行验证
						String realA0100=safeBo.checkA0100(realManapriv, paramPre, paramA0100, "");
						if(realA0100.trim().length()>0&&!realA0100.equals(paramA0100)){
							throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.user.ultra.vires"));
						}
					}
				}
		        /**安全信息改造，当选人时,判断是否存在不在用户范围内的人员end**/
				if(this.frowset!=null)
					this.frowset.close();
				
			}
			else //_sql为null
			{
				ArrayList basePreList=new ArrayList();//存放选中人员的人员库
				ArrayList a0100List = new ArrayList();//存放选中人员的a0100
				for(int i=0;i<objlist.size();i++)
				{
					String obj_id=(String)objlist.get(i);
					if(obj_id==null|| "".equals(obj_id))
						continue;
					if("selectall".equalsIgnoreCase(obj_id))
						continue;
					
					if(tablebo.getInfor_type()==1)//如果是人员
					{
						String pre=obj_id.substring(0,2).toLowerCase();
						/**对人员信息群时，过滤单位、部门及职位*/
						if("UN".equalsIgnoreCase(pre)|| "UM".equalsIgnoreCase(pre)|| "@K".equalsIgnoreCase(pre))
							continue;
						pre=obj_id.substring(0,3).toLowerCase();
						/**按人员库进行分类*/
						if(!hm.containsKey(pre))//hm包含所有的人员库
						{
							a0100list=new ArrayList();
						}
						else
						{
							a0100list=(ArrayList)hm.get(pre);
						}
						if(indexMap!=null){
							if(indexMap.get(obj_id.toLowerCase())!=null){
								
							}else{
								if(nameMap!=null&&nameMap.get(obj_id.toLowerCase())!=null)
								indexnames +=nameMap.get(obj_id.toLowerCase())+",";
								continue;
							}
						}
						a0100list.add(obj_id.substring(3));
						a0100List.add(pre+":"+obj_id.substring(3));//用于安全平台改造，判断A0100是否存在用户权限管理范围之内
						if(i==0)
						{
							first_base=pre;
							a0100=obj_id.substring(3);
						}
						hm.put(pre,a0100list);
						basePreList.add(pre);
					}
					else///如果不是人员
					{
						if(a0100list==null)
							a0100list=new ArrayList();
						if(i==0)
						{
							unit_value=obj_id;
						}
						a0100list.add(obj_id);
					}
				} //for objlist loop end.
				
				if(tablebo.getInfor_type()==2)
					hm.put("B",a0100list);
				if(tablebo.getInfor_type()==3)
					hm.put("K",a0100list);
				/**安全信息改造，当选人时,判断是否存在不在用户范围内的人员begin**/
				CheckPrivSafeBo safeBo = new CheckPrivSafeBo(this.frameconn,this.userView);
				if(!this.userView.isSuper_admin()){
				    for(int i=0;i<basePreList.size();i++){//过滤出用户权限的人员库
				    	 String paramBasePre =(String)basePreList.get(i);
				    	 paramBasePre = paramBasePre.trim();
				    	 String returnBasePre =safeBo.checkDb(paramBasePre);//这个方法当不越权时返回传进去的人员库，越权时返回当前人员的第一个人员库
				    	 /**当返回的人员库值的长度大于0并且不等于传进去的人员库时说明越权**/
				    	 if(returnBasePre.trim().length()>0&&!paramBasePre.equals(returnBasePre)){//如果当前用户的人员库没有这个选中人员的人员库，终止导入
				    		 throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.user.ultra.vires"));
			             }
				    }
				    
				}
				
				if(!this.userView.isSuper_admin() && !"1".equals(no_priv_ctrl)){
					/**验证管理范围，如果越权则返回实有的管理范围**/
					String paramManapriv=this.userView.getManagePrivCodeValue();
					String realManapriv=safeBo.checkOrg(paramManapriv, "");
					if (kqFlag&& userView.getKqManageValue() != null && !"".equals(userView.getKqManageValue())){
						paramManapriv=userView.getKqManageValue().substring(2);	
						realManapriv=paramManapriv;
					}
					for(int i=0;i<a0100List.size();i++){
						String paramPreA0100=(String) a0100List.get(i);
						String[]paramArray=paramPreA0100.split(":");
						String paramPre=paramArray[0].trim();//这里所有的人员库都进行了验证，如果越权的人员库，在上面就结束了
						String paramA0100=paramArray[1].trim();//这里的A0100尚未进行验证
						String realA0100=safeBo.checkA0100(realManapriv, paramPre, paramA0100, "");
						if(realA0100.trim().length()>0&&!realA0100.equals(paramA0100)){
							throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.user.ultra.vires"));
						}
					}
				}
		        /**安全信息改造，当选人时,判断是否存在不在用户范围内的人员end**/
			}

			///开始一个人员库一个人员库地导入
			Iterator iterator=hm.entrySet().iterator();
			ArrayList tempList=null;
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				String pre=entry.getKey().toString();
				a0100list =(ArrayList)entry.getValue();
				if(a0100list.size()==0)
					continue;
				
				if(a0100list.size()<=500)
					tablebo.impDataFromArchive(a0100list,pre);
				else
				{
					
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
							tablebo.impDataFromArchive(tempList,pre);
						
					}
					
				}
			}
			
			/**主要为了前台定位到当前的选中的第一个人*/
			if(tablebo.getInfor_type()==1)
			{
				if(!(first_base==null|| "".equals(first_base)))
				{
					this.getFormHM().put("basepre",first_base);
					this.getFormHM().put("a0100",a0100);
				}
			}
			else if(tablebo.getInfor_type()==2)
			{
				if(!(unit_value==null|| "".equals(unit_value)))
				{
					this.getFormHM().put("b0110",unit_value);
				}
			}
			else if(tablebo.getInfor_type()==3)
			{
				if(!(unit_value==null|| "".equals(unit_value)))
				{
					this.getFormHM().put("e01a1",unit_value);
				}
			}
			if(indexnames.length()>1)
				this.getFormHM().put("indexnames", indexnames);
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	public HashMap getIndexMap(TemplateTableBo tablebo,String tabid,HashMap nameMap){
		//检索条件map
		HashMap indexMap = null;
		try{
			String factor=tablebo.getFactor();
			if(factor==null||factor.trim().length()==0)
				return indexMap;
			if("1".equals(tablebo.getFilter_by_factor())){
				indexMap = new HashMap();
			ArrayList dblist=new ArrayList();
			ContentDAO dao=new ContentDAO(this.frameconn);
			String init_base=tablebo.getInit_base();
		
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
						return indexMap;
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
			
			
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			for(int e=0;e<dblist.size();e++)
			{
				String BasePre=(String)dblist.get(e);
				StringBuffer sql=new StringBuffer();
 
				sql.append("select a0100 from ");
				int infoGroup = 0; // forPerson 人员
				int varType = 8; // logic								
				String whereIN=InfoUtils.getWhereINSql(this.userView,BasePre);
				whereIN="select a0100 "+whereIN;	
				String no_priv_ctrl=tablebo.getNo_priv_ctrl(); //手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
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
				
				ArrayList a0100list =new ArrayList();
//				String a0l00s="";
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next()){
					indexMap.put(BasePre.toLowerCase()+this.frowset.getString("a0100"), BasePre.toLowerCase()+this.frowset.getString("a0100"));
//					if(a0l00s.length()>1)
//						a0l00s+=",'"+this.frowset.getString("a0100")+"'";
//					else{
//						a0l00s = "'"+this.frowset.getString("a0100")+"'";
//					}
				}
				sql.setLength(0);
				sql.append(" select a0100,a0101 from "+BasePre+"a01 " );
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next()){
					nameMap.put(BasePre.toLowerCase()+this.frowset.getString("a0100"), this.frowset.getString("a0101"));
				}
			}
			}
		}catch(Exception e2){
			e2.printStackTrace();
		}
		
		return indexMap;
	}
}
