package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterUpdateTrans extends IBusiness{
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM();		
		try
		{		
		   ArrayList formslist=(ArrayList)hm.get("forms");
		   String columns=(String)hm.get("columns");//传入的少q03z5
		   String code=(String)hm.get("code");
		   String kind=(String)hm.get("kind");	 
		 
		   RegisterInitInfoData initData = new RegisterInitInfoData();
		   
		   String updateSql="";
		   if(columns!=null&&columns.length()>0){
			  //得到update，SQL语句			  
			   ArrayList fielditemlist=DataDictionary.getFieldList("Q03",
						Constant.USED_FIELD_SET);
			   fielditemlist = initData.getNewItemList(fielditemlist, "", this.frameconn);		
			   //增加判断Q03中那些指标是从A01主集中取得的，过滤出去；A01主集导入到Q03的信息不能			   
			   updateSql=updateSql(fielditemlist);	
			   // 过滤formslist中的不是q03的数据
			   formslist = updateFormslist(formslist,fielditemlist,columns);
		      //updateSql=initData.updateSQL(columns);
		      //处理前台传过来的数据，改变为符合上面update的元素的list,增加过滤掉A01指标信息	
		      ArrayList recordlist = initData.getFormsList(formslist,fielditemlist,this.getFrameconn());
		      
		      //判断是否可以起草保存
		      ArrayList newList =opUpdate(recordlist,code,kind);
		      
		      if(newList!=null&&newList.size()>0){
		        ContentDAO dao = new ContentDAO(this.getFrameconn());			       
                dao.batchUpdate(updateSql,newList);
                this.getFormHM().put("type","success");
		      }else{
		    	this.getFormHM().put("type","nosave");
		      }
		   }else{
			   this.getFormHM().put("type","lost");
		   }
		}
		catch(Exception e){
	       e.printStackTrace();
	       throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.error.number"),"",""));    
		}

	}
	
	private ArrayList updateFormslist (ArrayList formslist,ArrayList fielditemlist, String columns) {
		ArrayList list = new ArrayList();
		String []cols = columns.split(",");
		
		StringBuffer buff = new StringBuffer();
		for (int i = 0;i < fielditemlist.size(); i++) {
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			if ("M".equalsIgnoreCase(fielditem.getItemtype())) {
				continue;
			}
			buff.append(",");
			buff.append(fielditem.getItemid().toLowerCase());
		}
		buff.append(",");
		String clu = buff.toString();
		for (int i = 0; i < cols.length; i++) {
			if (clu.contains("," + cols[i].toLowerCase() + ",")) {
				list.add(formslist.get(i));
			}
		}
		
		return list;
	}
	/* 判断保存数据是否可以更改
	 * @param recordlist,
	 *       传递进来的处理后的多条数据记录
	 * @return 
	 *       返回验证过的List
	 * */
	private ArrayList opUpdate(ArrayList recordlist,String code,String kind){
    	
        ContentDAO dao = new ContentDAO(this.getFrameconn());
    	
    	//如果该纪录已经提交则跳过
    	ArrayList newlist=new ArrayList();
    	for(int r=0;r<recordlist.size();r++)
    	{
    	   ArrayList record=(ArrayList)recordlist.get(r);
    	   StringBuffer sql=new StringBuffer();
    	   int i=record.size();
    	   sql.append("select Q03Z5 from Q03 where ");
    	   sql.append(" nbase='"+record.get(i-3).toString()+"'");
    	   sql.append(" and a0100='"+record.get(i-2).toString()+"'");
    	   String date=record.get(i-1).toString();    	   
    	   sql.append(" and Q03Z0 like '"+date+"%'");
    	   sql.append(" and Q03Z5 in ('01','07')");
           
           try{
               this.frowset = dao.search(sql.toString());
               if(this.frowset.next()){
        	       newlist.add(record);
        	   }
           }catch(Exception e){
        	  e.printStackTrace();
           }
    	} 
        return newlist;
    }
	private String updateSql(ArrayList fielditemlist)
    {
		RegisterInitInfoData initData = new RegisterInitInfoData();
    	StringBuffer sql=new StringBuffer();
    	sql.append("update Q03 set ");
    	for(int r=0;r<fielditemlist.size();r++)
		{
			FieldItem fielditem=(FieldItem)fielditemlist.get(r);
			//判断指标是否需要更新
			boolean needUpdateItem = initData.needUpdateItem(fielditem);
			if(needUpdateItem)
			{
				sql.append(fielditem.getItemid()+"=?,");
			}
		} 
    	sql.setLength(sql.length()-1);
    	sql.append(" where nbase=? and a0100=? and q03z0=?");
    	return sql.toString();
    }
}
