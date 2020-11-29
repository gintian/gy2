package com.hjsj.hrms.businessobject.train.resource;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class TrainRoomBo {
    private Connection conn;
    private static String nbase;
    private static String a0100;
    private static String year;
    private static String month;
    private static HashMap map;
    
    private TrainRoomBo(){}
    
    public TrainRoomBo(Connection conn,String year,String month){
    	this.conn = conn;
    	this.year = year;
    	this.month = month;
    	init();
    }
    
    public TrainRoomBo(Connection conn,String year,String month, String nbase, String a0100){
    	this.conn = conn;
    	this.year = year;
    	this.month = month;
    	this.nbase = nbase;
    	this.a0100 = a0100;
    	init();
    }
    
    private void init(){
    	StringBuffer sql = new StringBuffer("select r1001,nbase,a0100,r6101,r6103,r6105,r6111,r6113 from r61");
    	sql.append(" where "+Sql_switcher.year("r6103")+"='"+this.year+"'");
    	sql.append(" and "+Sql_switcher.month("r6103")+"='"+this.month+"'");
    	if(nbase==null||nbase.length()<1||a0100==null||a0100.length()<1) {
            sql.append(" and r6111 in ('02','03')");//02已报批，03已批，07驳回
        } else {
            sql.append(" and ((nbase='"+this.nbase+"' and a0100='"+this.a0100+"') or r6111 = '03')");
        }
    	
    	this.map = new HashMap();
    	RowSet rs = null;
    	ArrayList list = null;
    	HashMap hashMap = null;
    	ContentDAO dao = new ContentDAO(conn);
    	try {
			rs = dao.search(sql.toString());
			while(rs.next()){
				String nbase = rs.getString("nbase");
				String a0100 = rs.getString("a0100");
				Date r6101 = rs.getTimestamp("r6101");
				Date r6103 = rs.getTimestamp("r6103");
				String r6111 = rs.getString("r6111");
				String key = DateUtils.getDay(r6101)+"_"+rs.getString("r1001");
				String tmp = "&nbsp;";
				//if("01".equals(r6111)){
				//	tmp+="&nbsp;<font color='black'>";
				//}else 
				if("02".equals(r6111)){
					tmp+="&nbsp;<font color='blue'";
				}else if("03".equals(r6111)){
					tmp+="&nbsp;<font color='green'";
				}else if("07".equals(r6111)){
					tmp+="&nbsp;<font color='red'";
				}
				if(this.nbase==null||this.nbase.length()<1||this.a0100==null||this.a0100.length()<1){
					String r6105 = rs.getString("r6105");
					if(r6105!=null&&r6105.trim().length()>0) {
                        tmp+=" title='事由："+r6105+"'";
                    }
					tmp+=">";
				}else{
					String r6113 = rs.getString("r6113");
					if(r6113!=null&&r6113.trim().length()>0) {
                        tmp+=" title='意见："+r6113+"'";
                    }
					tmp+=">";
				}
				tmp+=DateUtils.FormatDate(r6101, "HH:mm")+"~"+DateUtils.FormatDate(r6103, "HH:mm")+"</font>&nbsp;";
				
				hashMap = (HashMap) map.get(key);
				if(hashMap==null) {
                    hashMap = new HashMap();
                }
				list = (ArrayList) hashMap.get(nbase+a0100);
				if(list==null) {
                    list = new ArrayList();
                }
				
				list.add(tmp);
				hashMap.put(nbase+a0100, list);
				map.put(key, hashMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
    }
    
    public static String getHtml(String key){
    	HashMap hashMap = (HashMap) map.get(key);
    	if(hashMap==null) {
            return "";
        }
    	StringBuffer buffer = new StringBuffer();
		Iterator it = hashMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			ArrayList list = (ArrayList)hashMap.get(entry.getKey());
			if(list==null) {
                return "";
            }
			for (int i = 0; i < list.size(); i++) {
				String value = (String)list.get(i);
				if(value==null||value.trim().length()<1) {
                    return "";
                }
				buffer.append(value);
				if(nbase!=null&&a0100!=null&&(nbase+a0100).equals(entry.getKey())){
					value = value.substring(value.indexOf(">")+1);
					value = value.substring(0,value.indexOf("<"));
					String _v[] = key.split("_");
					String _t[] = value.split("~");
					String _t1 = year+"-"+month+"-"+_v[0]+" "+_t[0]+":00";
					String _t2 = year+"-"+month+"-"+_v[0]+" "+_t[1]+":00";
					buffer.append("(<a href='###' onclick=\"del('"+SafeCode.encode(PubFunc.encrypt(_v[1]))+"','"+_t1+"','"+_t2+"');\" title='删除'>X</a>)&nbsp;");//显示删除
				}
				buffer.append("<br/>");
			}
		}
    	//if(buffer.length()>5)
		//	buffer.setLength(buffer.length()-5);
		String buffer1=buffer.toString();
		if("&nbsp;&nbsp;".equals(buffer1.substring(0, 12))) {
            buffer1=buffer1.substring(6, buffer.length());
        }
    	return buffer1;
    }
    /**
     * 获取审批意见的长度
     * @return
     */
    public static int getR6113Length() {
        FieldItem fi = DataDictionary.getFieldItem("r6113", "r61");
        int length = fi.getItemlength();
        return length;
    }
}
