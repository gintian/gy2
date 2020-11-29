package com.hjsj.hrms.businessobject.duty;

import com.hrms.frame.dao.ContentDAO;

import javax.sql.RowSet;
import java.util.ArrayList;

public class MoveSdutyBo {
       
	   RowSet frowset = null;
	   ContentDAO dao;
	   private ArrayList itemidList = new ArrayList();
	   
	   
	   public MoveSdutyBo(ContentDAO dao,RowSet frowset){
		   this.frowset = frowset;
		   this.dao = dao;
	   }
	   
	   /**
	    * 移动节点 a0000
	    * @param codeitemid 
	    * @param codesetid
	    * @param itemidArr 本级所有节点，按顺序排
	    * @param movetype 移动类型   up向上，down向下
	    */
	   public void updateA0000(String codeitemid,String codesetid,String[] itemidArr,String movetype){
			try{
				StringBuffer sql = new StringBuffer();
			  String codeitemidB="";
			  
			  for(int i=0;i<itemidArr.length;i++){
				  if(itemidArr[i].equals(codeitemid)){
					  if("up".equals(movetype)) {
                          codeitemidB = itemidArr[i-1];
                      } else {
                          codeitemidB = itemidArr[i+1];
                      }
					  
					  break;
				  }
			  }
			  
			 int upcount=0;
			 int downcount=0;
			 sql.delete(0, sql.length());
			 sql.append("select count(*) count from codeitem where codesetid='"+codesetid+"' and codeitemid like '");
			 if("up".equals(movetype)) {
                 sql.append(codeitemidB+"%' ");
             } else {
                 sql.append(codeitemid+"%' ");
             }
			 this.frowset = dao.search(sql.toString());
			 
			 if(frowset.next()) {
                 upcount = frowset.getInt("count");
             }
			 
			 
			 sql.delete(0, sql.length());
			 sql.append("select count(*) count from codeitem where codesetid='"+codesetid+"' and codeitemid like '");
			 if("up".equals(movetype)) {
                 sql.append(codeitemid+"%' ");
             } else {
                 sql.append(codeitemidB+"%' ");
             }
			 
			 this.frowset = dao.search(sql.toString());
			 if(frowset.next()) {
                 downcount = frowset.getInt("count");
             }
			 
			 sql.delete(0, sql.length());
			 sql.append(" update codeitem set a0000 = a0000-"+upcount+" where codesetid='"+codesetid+"' and codeitemid like '");
			 if("up".equals(movetype)) {
                 sql.append(codeitemid+"%' ");
             } else {
                 sql.append(codeitemidB+"%' ");
             }
			 
			 dao.update(sql.toString());
			 
			 sql.delete(0, sql.length());
			 sql.append(" update codeitem set a0000 = a0000+"+downcount+" where codesetid='"+codesetid+"' and codeitemid like '");
			 if("up".equals(movetype)) {
                 sql.append(codeitemidB+"%' ");
             } else {
                 sql.append(codeitemid+"%' ");
             }
			 
			 dao.update(sql.toString());
			 
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	   
	   /**
	    * 初始化a0000
	    * @param codesetid
	    */
	   public void initA0000(String codesetid){
			
			try{
				
				ArrayList codeitemList = new ArrayList();
				String sql  = " select codeitemid from codeitem where codesetid='"+codesetid+"' and codeitemid=parentid ";
				frowset = dao.search(sql);
				
				while(frowset.next()) {
                    codeitemList.add(frowset.getString("codeitemid"));
                }
				
				getcodeitemids(codesetid,codeitemList);
				
				ArrayList paramlist = new ArrayList();
				sql = " update codeitem set a0000=? where codesetid='"+codesetid+"' and codeitemid=?";
				for(int i=0;i<itemidList.size();i++){
					ArrayList param = new ArrayList();
					param.add(new Integer(i+1));
					param.add(itemidList.get(i));
					paramlist.add(param);
				}
				dao.batchUpdate(sql, paramlist);
			}catch (Exception e) {
			}
		}
	   
	   private void getcodeitemids(String codesetid,ArrayList codeitemid){
			try{
				for(int i=0;i<codeitemid.size();i++){
					itemidList.add(codeitemid.get(i));
					ArrayList parentitemid = new ArrayList();
					StringBuffer sql = new StringBuffer(" select codeitemid from codeitem where codesetid='");
					sql.append(codesetid+"' and ");
					sql.append(" parentid = '"+codeitemid.get(i)+"' and codeitemid<>parentid order by a0000,codeitemid");
				    frowset = dao.search(sql.toString());
				    while(frowset.next()){
				    	parentitemid.add(frowset.getString("codeitemid"));
				    }
				
				   getcodeitemids(codesetid,parentitemid);
			   }
			}catch(Exception e){
				e.printStackTrace();
			}
	  }
	   
	   /**
	    * 添加时获取a0000
	    * @param parentid
	    * @param codesetid
	    * @return
	    */
	   public String getaddA0000(String parentid,String codesetid){
		   int a0000=0;
		   try{
			   
			   String sql = "select max(a0000) a0000 from codeitem where codesetid='"+codesetid+"'";
			      if(parentid != null && parentid.trim().length()>0) {
                      sql+=" and codeitemid like '"+parentid+"%'";
                  }
			   this.frowset = dao.search(sql);
			   this.frowset.next();
			   a0000 = this.frowset.getInt("a0000");
			   
			   sql = " update codeitem set a0000=a0000+1 where codesetid='"+codesetid+"'  and a0000>"+a0000;
			   dao.update(sql);
		   }catch (Exception e) {
		   }
		   return a0000+1+"";
	   }
	   
	   /**
	    * 插入式向后移动a0000
	    * @param codesetid
	    * @param a0000
	    */
	   public void moveA0000(String codesetid,int a0000){
           try{
			   String sql = " update codeitem set a0000=a0000+1 where codesetid='"+codesetid+"' and a0000>"+a0000;
			   dao.update(sql);
		   }catch (Exception e) {
		   }
	   }
}
