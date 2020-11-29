package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.*;



public class ExeTopicTrans extends IBusiness{

	public void execute() throws GeneralException {
		ArrayList list = (ArrayList)this.getFormHM().get("list");
		ArrayList shortQuestions = null;
		HashMap questions = null;
		try {
			for(int k = 0 ; k < list.size() ; k ++){
			RecordVo vo=(RecordVo)this.getFormHM().get("topicov");//保存问卷调查表
			ContentDAO dao=new ContentDAO(this.getFrameconn()); 
			 
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
			String id=idg.getId("investigate.id");
			
			Object [] o = (Object [] )list.get(k);

			ArrayList li = (ArrayList)o[0];
			vo.setString("id", id);
			vo.setString("content", li.get(0).toString());
			vo.setString("description", li.get(1).toString());
			vo.setString("flag", "1");
			vo.setString("status", "1");
			vo.setInt("days", 30);
			vo.setDate("releasedate", new Date());
			dao.addValueObject(vo);
			
			questions = (HashMap)o[2];
			for (Iterator iter = questions.entrySet().iterator();iter.hasNext();) {//如果是选择题
				//String itemid=idg.getId("investigate_item.id");
				 Map.Entry entry = (Map.Entry) iter.next(); //map.entry 同时取出键值
				 Object keyo = entry.getKey();
				 
				 String [] keys = keyo.toString().split("[|]");
				 String key = keys[0];
				 String status  = key.toString().substring(key.toString().length()-1,key.toString().length());
				 key = key.toString().substring(0,key.toString().length()-1);
				 String itemid = keys[1];
				 this.insertItem(itemid, id, key.toString(),status);
				 Object val = entry.getValue();
				 List options = (List)val;
				 for(int i = 0 ; i < options.size() ; i++){ 
					 String pointid = idg.getId("invpoints.id");
					 this.insertPoint(pointid, itemid, options.get(i).toString());
				 }
				}
			shortQuestions = (ArrayList)o[1];
			for(int i = 0 ; i < shortQuestions.size() ; i++){
				//String itemid = idg.getId("investigate_item.id");
				String s = shortQuestions.get(i).toString();
				String [] values = s.split("[|]");
				String itemid = values[1];
				String value = values[0];
				String status = "1";
				this.insertItem(itemid, id, values[0],status);
			}
		  }
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.import.error.excel")));
		}
	}
	
	//插入问卷调查题目表
	public void insertItem(String itemId , String id , String name,String status){
		ContentDAO dao=new ContentDAO(this.getFrameconn()); 
		List list = new ArrayList();
		list.add(itemId);
		list.add(id);
		list.add(name);
		list.add(status);
		list.add(null);
		list.add(null);
		list.add(null);
		list.add(null);
		name = name.trim();
		name = name.replace("\n", "");
		name = name.replace("\r", "");
		String sql = "insert into investigate_item values(?,?,?,?,?,?,?,?)";
		try {
			dao.insert(sql, list);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	//取出相应表中相应字段的长度
	public void selectLengthByName(){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rs = null;
		String sql = "select name,length from syscolumns where id=object_id( 'investigate_item')";
		try {
			rs = dao.search(sql);
			System.out.println(rs.getString("name").toString());
			System.out.println(rs.getInt("length")+"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//插入问卷调查选项表
	public void insertPoint(String pointId, String id , String name){
		ContentDAO dao=new ContentDAO(this.getFrameconn()); 
		List list = new ArrayList();
		list.add(pointId);
		list.add(id);
		list.add(name);
		list.add("1");
		list.add("0");
		String sql = "insert into investigate_point values(?,?,?,?,?)";
		try {
			dao.insert(sql, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
