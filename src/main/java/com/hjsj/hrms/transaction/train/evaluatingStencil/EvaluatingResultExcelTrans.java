package com.hjsj.hrms.transaction.train.evaluatingStencil;

import com.hjsj.hrms.actionform.askinv.EndViewForm;
import com.hjsj.hrms.actionform.welcome.WelcomeForm;
import com.hjsj.hrms.businessobject.train.EvaluatingToExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.math.BigDecimal;
import java.util.*;

public class EvaluatingResultExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String opt=(String)this.getFormHM().get("opt");			
			String outName="";
			/*
			 * 培训调查文件暂时未调用  暂时保留
			 * opt = 1 培训班 =2 调查问卷
			 * 
			 */
			if("1".equals(opt))
			{
				String r3101=(String)this.getFormHM().get("r3101");
				String templateid=(String)this.getFormHM().get("templateid");
				String titleName="";
	            ContentDAO dao=new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select * from r31 where r3101='"+r3101+"'");
				if(this.frowset.next())
						titleName=this.frowset.getString("r3130");
				EvaluatingToExcelBo bo=new EvaluatingToExcelBo(this.getFrameconn());
				bo.setR3101(r3101);
				outName=bo.getEvaluatingExcel(templateid,titleName);
				//outName=outName.replaceAll(".xls","#");
			}
			else
			{
			  	 ArrayList itemtxtlist=new ArrayList();  //项目对象id
			  	 //取得调查表id及名称
			  	 String id=(String)this.getFormHM().get("id");
  			  	 String name="";
			  	 ContentDAO dao=new ContentDAO(this.getFrameconn());
				
				 /**得到主题名*/
				 this.frowset=dao.search("select content from investigate where id="+id);
				 if(this.frowset.next())
				 {
				   			name=PubFunc.nullToStr(this.frowset.getString("content"));
				  }
				 
				   	
				   		/**得到问答项目列表*/
				   	   
				StringBuffer sbtp=new StringBuffer();
				sbtp.append("select investigate_item.itemid,investigate_item.name from investigate_item ");
				sbtp.append(Sql_switcher.right_join("investigate_item","investigate_content","investigate_item.itemid","investigate_content.itemid"));
				sbtp.append(" where investigate_item.id='");
				sbtp.append(id);
				sbtp.append("' group by investigate_item.itemid,investigate_item.name  order by investigate_item.itemid");
				   		
			
				this.frowset=dao.search(sbtp.toString());
				while(this.frowset.next())
				{
				   			WelcomeForm wf=new WelcomeForm();
				   			String itemid=PubFunc.NullToZero(this.frowset.getString("itemid"));
				   			String itemname=PubFunc.nullToStr(this.frowset.getString("name"));
				   			wf.setItemName(itemname);
				   			wf.setItemid(itemid);
				   			itemtxtlist.add(wf);
				}
				  
				  
				   
				LinkedHashMap htpoint=new LinkedHashMap();
		        LinkedHashMap htpointnull = new LinkedHashMap();
			    Hashtable htitem=new Hashtable();
			    ArrayList resultlist=new ArrayList();	//结果表对象
				
					/**得到结果表中的对象列表*/
				this.frowset=dao.search("select pointid,itemid,context from investigate_result");
				while(this.frowset.next())
				{
						DynaBean vo=new LazyDynaBean();
						vo.set("pointid",PubFunc.NullToZero(this.frowset.getString("pointid")));
						vo.set("itemid",PubFunc.NullToZero(this.frowset.getString("itemid")));
						//vo.set("context",PubFunc.nullToStr(this.frowset.getString("context")));
						vo.set("context",PubFunc.nullToStr(Sql_switcher.readMemo(this.frowset,"context")));
						resultlist.add(vo);
				}

				    //得到要点hashtable
				this.frowset=dao.search("select pointid,name ,itemid from investigate_point  order by pointid ");
				while(this.frowset.next())
			    {
				    	String pidtemp = PubFunc.NullToZero(this.frowset.getString("pointid"));
						htpointnull.put(pidtemp, PubFunc.NullToZero(this.frowset.getString("itemid")));
						htpoint.put(pidtemp, PubFunc.nullToStr(this.frowset.getString("name")));
				}

				    //		  得到项目hashtable
				this.frowset=dao.search("select itemid,name from investigate_item  order by investigate_item.itemid");
				while(this.frowset.next())
				{
				    	htitem.put(PubFunc.NullToZero(this.frowset.getString("itemid")),PubFunc.NullToZero(this.frowset.getString("name")));
				}
			  	//第二个要点调查循环
				StringBuffer strsql=new StringBuffer();
			  	strsql.append("SELECT  count(investigate_result.pointid) as sumNum ,investigate_point.pointid as pointid,");
			  	strsql.append("investigate_point.itemid as itemid  ");
			  	strsql.append("FROM investigate_point ");
				strsql.append(Sql_switcher.left_join("investigate_point","investigate_result","investigate_point.pointid","investigate_result.pointid")); 
			  	strsql.append(" where investigate_point.itemid in (select itemid from investigate_item where id ='"+id+"' ) and state=2 group by investigate_point.pointid,investigate_point.itemid");

			    
			    	
			    String sumNumStr="0";
			    ArrayList endviewlst=new ArrayList();
			    String pointidStr="";
			    String pointName="";
			    String itemidStr="";
			    String sumNum="0";
			         
		 ////////////////   取得百分比   /////
			    HashMap percentMap=new HashMap();
			    StringBuffer strsql3=new StringBuffer();
				strsql3.append("SELECT  count(investigate_result.pointid) as sumNum ,investigate_point.itemid as itemid  ");
				strsql3.append("FROM investigate_point ");
				strsql3.append(Sql_switcher.left_join("investigate_point","investigate_result","investigate_point.pointid","investigate_result.pointid")); 
				strsql3.append(" where investigate_point.itemid in (select itemid from investigate_item where id ='"+id+"' ) and investigate_result.state=2 group by investigate_point.itemid");		
			    this.frowset = dao.search(strsql3.toString());
			    while(this.frowset.next())
			    {
			        	String itemid=this.frowset.getString("itemid");
			        	String  count=this.frowset.getString("sumNum");
			        	percentMap.put(itemid,count);
			    }
			      ////////////////////////////////// 
			        
			   this.frowset = dao.search(strsql.toString()+"  order by itemid,pointid");
			   while(this.frowset.next())
			   {

			          EndViewForm evf=new EndViewForm();        
			          String itemName="";
			          itemidStr=PubFunc.NullToZero(this.frowset.getString("itemid"));
			          sumNum=PubFunc.NullToZero(this.frowset.getString("sumNum"));
			          pointidStr=PubFunc.NullToZero(this.frowset.getString("pointid"));
			          
			          String percent="0%";
			          String count=(String)percentMap.get(this.frowset.getString("itemid"));
			          if(!"0".equals(count))
			          {
			        	  BigDecimal a=new BigDecimal(count);
			        	  BigDecimal b=new BigDecimal(sumNum);
			        	  percent=b.divide(a,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).divide(new BigDecimal("1"),0,BigDecimal.ROUND_HALF_UP).toString()+"%";  
			          }
			          
			          if(htpoint.containsKey(pointidStr))
			          {
			          	pointName=htpoint.get(pointidStr).toString();
			          }
			          else
			          {
			          	pointName="";
			          }
			          
			          if(htitem.containsKey(itemidStr))
			          {
			          	itemName=htitem.get(itemidStr).toString();
			          }
			          else
			          {
			          	itemName="";
			          }
			          evf.setItemid(itemidStr);
			          evf.setItemName(itemName);
			          evf.setPointid(pointidStr);
			          evf.setPointName(pointName);
			          evf.setSumNum(sumNum);
			          evf.setConextFlag(getResultTxtFlag(resultlist,pointidStr,itemidStr));
			          evf.setPrecent(percent);
			          endviewlst.add(evf);
			         	              
			      }
			     // this.getFormHM().put("endviewlst",endviewlst);
			    
			      //第一个项目循环
				  	 ArrayList itemwhilelst=new ArrayList();
				     StringBuffer strsql2=new StringBuffer();
				  	 	
				  	 	strsql2.append("SELECT  ");
					  	strsql2.append("investigate_point.itemid as itemid  ");
					  	strsql2.append("FROM investigate_point ");
					  	strsql2.append(Sql_switcher.left_join("investigate_point","investigate_result","investigate_point.pointid","investigate_result.pointid"));
					  	strsql2.append(" where investigate_point.itemid in (select itemid from investigate_item where id ='"+id+"' ) group by investigate_point.itemid order by investigate_point.itemid");
					//	System.out.println("sql2-->"+strsql2.toString());
					  	cat.debug("---->SearchEndViewListTrans-->sql2-->"+strsql2.toString());
				  	 	this.frowset=dao.search(strsql2.toString());
					  	while(this.frowset.next())
				  	 	{
				  	 	 EndViewForm evf=new EndViewForm(); 
				  	 	 String itemid=PubFunc.NullToZero(this.frowset.getString("itemid"));
				  	 	 String itemName="";
				  	   if(htitem.containsKey(itemid))
				          {
				          	itemName=htitem.get(itemid).toString();
				          }
				          else
				          {
				          	itemName="";
				          }
				  	   	  evf.setItemid(itemid);
				  	   	  evf.setItemName(itemName);
				  	   	  
				  	   	 ArrayList lst=new ArrayList();
						 lst=(ArrayList)getSecondlst(itemid,endviewlst);
						 lst = doPointNull(itemid, htitem, htpointnull, htpoint, lst); //初始化没有点击的数据项
				  	   	  evf.setEndviewlst(lst);
				  	   	 
				  	   	  evf.setPicList(getPicList(lst));
				  	   	 
				  	   	  itemwhilelst.add(evf);
				  	    
				  	   
				  	 	}
					  //	System.out.println("---SearchEndViewListTrans-->"+itemwhilelst.size());
					  	
				  	  EvaluatingToExcelBo bo=new EvaluatingToExcelBo(this.getFrameconn(),this.userView);
					  outName=bo.getInvestigateExcel(itemwhilelst,itemtxtlist,name);  
					  //outName=outName.replaceAll(".xls","#");
			}
			outName = PubFunc.encrypt(outName);
			this.getFormHM().put("outName",outName);
			
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	
	/**
	 * 判断要点描述所有的是否为空
	 * @param lst
	 * @return
	 */
	private String getResultTxtFlag(ArrayList list,String pointid,String itemid)
	{
		String flag="0";
		
		for(int i=0;i<list.size();i++)
		{
			DynaBean  vo=(DynaBean)list.get(i);
			if(vo.get("pointid").toString().equals(pointid)
				&& vo.get("itemid").toString().equals(itemid)
				&& !"".equals(vo.get("context").toString().trim()))
			{
				flag="1";
				break;
			}
		}
		
		return flag;
	}

		
	  //得到图像lst
	  public ArrayList getPicList(ArrayList lst)
	  {
	  	 ArrayList list=new ArrayList();
	  	  for(int i=0;i<lst.size();i++)
	  	  {
	  	  EndViewForm evf=new EndViewForm(); 
	  	  	evf=(EndViewForm)lst.get(i);
	        CommonData vo=new CommonData();
	        vo.setDataName(evf.getPointName());
	        vo.setDataValue(evf.getSumNum());
	        list.add(vo);
	        
	        
	  	  }
	        
	        return list;
	  }
	  
	  //得到要查找的lst内容
	  public ArrayList getSecondlst(String itemid,ArrayList plst)
	  {
	  	ArrayList ls=new ArrayList();
	  	for(int i=0;i<plst.size();i++)
	  	{
	  		EndViewForm evf=new EndViewForm(); 
	  		evf=(EndViewForm)plst.get(i);
	  		if(evf.getItemid().equals(itemid))
	  		{
	  			ls.add(evf);
	  		}
	  		
	  		
	  	}
	  	
	  	return ls;	  	
	  }
	  
	  /*
		 * 
		 *  Title:判断查找的要点内容是否为空 
		 * 
		 * 
		 *  
		 */
		public ArrayList doPointNull(String itemid, Hashtable htitem,
                LinkedHashMap htpointnull, LinkedHashMap htpoint, ArrayList lst) {
		  //获取某项目要点列表
            ArrayList list = new ArrayList();

            Set mapSet =  htpointnull.keySet();
            for (Iterator t = mapSet.iterator(); t.hasNext();){
                String set = (String)t.next();
                String typeid = (String)htpointnull.get(set);
                if (typeid.equals(itemid)) {
                    if (!list.contains(set)) {
                        list.add(set);
                    }
                    
                }

            }
            //end while

			//list为空时的判断及初始化
			if (lst.size() == 0 || lst == null) {
				lst = new ArrayList();
				for (int i = 0; i < list.size(); i++) {
					EndViewForm evf = new EndViewForm();
					String pointid = list.get(i).toString();
					String itemname = htitem.get(itemid).toString();
					String pointName = htpoint.get(pointid).toString();
					evf.setItemid(itemid);
					evf.setItemName(itemname);
					evf.setPointid(pointid);
					evf.setPointName(pointName);
					evf.setSumNum("0");
					lst.add(evf);

				}
			} else {
				if (list.size() > lst.size()) {
					for (int i = 0; i < list.size(); i++) {
						String pointid = list.get(i).toString();
						String flag = "0";
						for (int j = 0; j < lst.size(); j++) {
							EndViewForm evf = new EndViewForm();
							evf = (EndViewForm) lst.get(j);
							if (pointid.equals(evf.getPointid())) {
								flag = "1";
							}

						}

						if ("1".equals(flag)) {

						} else {

							EndViewForm evf = new EndViewForm();
							String itemname = htitem.get(itemid).toString();
							String pointName = htpoint.get(pointid).toString();
							evf.setItemid(itemid);
							evf.setItemName(itemname);
							evf.setPointid(pointid);
							evf.setPointName(pointName);
							evf.setSumNum("0");
							lst.add(evf);
						}

					}
				}
			}

			return lst;
		}
	
}
