package com.hjsj.hrms.businessobject.performance.workplan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PersonListShowBo{
	
	private Connection conn = null;
	private UserView userView = null;
	public PersonListShowBo(Connection conn, UserView userView)
	{
		this.conn = conn;
		this.userView = userView;
	}
	
	String usernamefield = ConstantParamter.getLoginUserNameField().toLowerCase();//在人员库中得到用户名字段
	/**
	 * 得到sql语句的select部分，供<paginationdb>标签使用
	 * @param  report  未报为0， 报批或已批为1 
	 * @param  email_field 电子邮箱字段名称
	 * @param  mobile_field 移动电话字段名称
	 * @return  sql语句的select部分的字符串
	 * */
	public String getSqlSelect(String report){
		String strSelect = "";
		if("0".equals(report))//如果是未报
        {
            strSelect = "select a0000,a0100,b0110,e0122,e01a1,a0101,email_field,mobile_field,nbase ";
        } else if("1".equals(report))//如果是已报或已批
        {
            strSelect = "select a0000,a0100,b0110,e0122,e01a1,a0101,approver,curr_user,email_field,mobile_field,nbase ";
        }
		return strSelect;
	}
	/**
	 * 得到sql语句的from部分，供<paginationdb>标签使用
	 * @param  cycle 日报/周报/月报/季报/年报
	 * @param  type  工作类型。包括工作日志和工作小结
	 * @param  flag  审批情况。
	 * @param  year 年份
	 * @param  month月份
	 * @param  codeitemid 单位或部门编号
	 * @param  name 如果是日报，则获得第几天。如果是周报，则获得第几周。以此类推
	 * @param  report 审批情况。
	 * @param  manageLimit  管理范围
	 * @param  alist  所管理的数据库前缀
	 * @param  email_field  电子信箱字段
	 * @param  mobile_field  移动电话字段
	 * @return  sql语句的from部分
	 * */
	public String getSqlFrom(String cycle,String type,String flag,String year,String month,String codeitemid,String name,String report,String manageLimit,ArrayList alist,String email_field,String mobile_field){
		boolean isUnit = this.getIsUnit(codeitemid);//判断传过来的codeitemid是否为单位
		String departRange = "";
		if(isUnit){
			departRange = "(e0122 like'"+codeitemid+"%' or (b0110='"+codeitemid+"' and e0122 is null))";
		}else{
			departRange = "e0122 like'"+codeitemid+"%'";
		}
		StringBuffer strFrom = new StringBuffer();
		int n = alist.size();		
		if("0".equals(report)){//如果未报
			strFrom.append("from (");
			for(int i=0;i<n;i++){//得到P01里面未报的人员
				strFrom.append("select (select a0000 from "+alist.get(i)+"a01 a where a.a0100=p.a0100) as a0000,a0100,b0110,e0122,e01a1,a0101,(select "+email_field+" from "+alist.get(i)+"a01 a where a.a0100=p.a0100) as email_field,(select "+mobile_field+" from "+alist.get(i)+"a01 a where a.a0100=p.a0100) as mobile_field ,'"+alist.get(i)+"' as nbase from p01 p where 1=1 "+manageLimit+" and nbase= '"+alist.get(i)+"' and "+departRange+" and p.a0100 in (select a0100 from p01 p where 1=1  ");
				if("0".equals(cycle)){
					strFrom.append(" and state='0' and log_type='"+type+"' and (("+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.month("P0104")+"="+month+") or ("+Sql_switcher.year("P0106")+"='"+year+"' and "+Sql_switcher.month("P0106")+"="+month+")) and "+Sql_switcher.day("P0104")+"="+name+" and p0115 in('01','07') and "+departRange);
				}else if("1".equals(cycle)){
					strFrom.append(" and state='1' and log_type='"+type+"' and (("+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.month("P0104")+"="+month+") or ("+Sql_switcher.year("P0106")+"='"+year+"' and "+Sql_switcher.month("P0106")+"="+month+")) and "+Sql_switcher.week("P0104")+"="+name+" and p0115 in('01','07') and "+departRange);
				}else if("2".equals(cycle)){
					strFrom.append(" and state='2' and log_type='"+type+"' and (("+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.month("P0104")+"="+name+") or ("+Sql_switcher.year("P0106")+"='"+year+"' and "+Sql_switcher.month("P0106")+"="+name+")) and p0115 in('01','07') and "+departRange);
				}else if("3".equals(cycle)){
					strFrom.append(" and state='3' and log_type='"+type+"' and "+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.quarter("P0104")+"="+name+" and p0115 in('01','07') and "+departRange);
				}else if("4".equals(cycle)){
					strFrom.append(" and state='4' and log_type='"+type+"' and "+Sql_switcher.year("P0104")+"='"+year+"' and p0115 in('01','07') and "+departRange);
				}
				strFrom.append(" and nbase= '"+alist.get(i)+"' ");
				strFrom.append(manageLimit+" )");
				strFrom.append(" union ");
			}
			for(int i=0;i<n;i++){//得到所管人员中 除去p01里面符合条件的人员 的人员
				strFrom.append("select a0000,a0100,b0110,e0122,e01a1,a0101,"+email_field+" as email_field,"+mobile_field+" as mobile_field,'"+alist.get(i)+"' as nbase from "+alist.get(i)+"a01 p  where 1=1 "+manageLimit+ "  and "+departRange+" and p.a0100 not in  (select a0100 from p01 p where 1=1  ");
				if("0".equals(cycle)){
					strFrom.append(" and state='0' and log_type='"+type+"' and "+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.month("P0104")+"="+month+" and "+Sql_switcher.day("P0104")+"="+name+" and p0115 in('01','02','03','07') and "+departRange);
				}else if("1".equals(cycle)){
					strFrom.append(" and state='1' and log_type='"+type+"' and (("+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.month("P0104")+"="+month+") or ("+Sql_switcher.year("P0106")+"='"+year+"' and "+Sql_switcher.month("P0106")+"="+month+")) and "+Sql_switcher.week("P0104")+"="+name+" and p0115 in('01','02','03','07') and "+departRange);
				}else if("2".equals(cycle)){
					strFrom.append(" and state='2' and log_type='"+type+"' and (("+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.month("P0104")+"="+name+") or ("+Sql_switcher.year("P0106")+"='"+year+"' and "+Sql_switcher.month("P0106")+"="+name+")) and p0115 in('01','02','03','07') and "+departRange);
				}else if("3".equals(cycle)){
					strFrom.append(" and state='3' and log_type='"+type+"' and "+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.quarter("P0104")+"="+name+" and p0115 in('01','02','03','07') and "+departRange);
				}else if("4".equals(cycle)){
					strFrom.append(" and state='4' and log_type='"+type+"' and "+Sql_switcher.year("P0104")+"='"+year+"' and p0115 in('01','02','03','07') and "+departRange);
				}
				strFrom.append(" and nbase= '"+alist.get(i)+"' ");
				strFrom.append(manageLimit+" )");
				if(i!=(n-1)) {
                    strFrom.append(" union all ");
                }
			}
			strFrom.append(" )  a");
			
		}else{//如果是已报或已批
			strFrom.append("from (");
			for(int i=0;i<n;i++){
				if("0".equals(cycle)){
					strFrom.append("select p.a0100,p.b0110,p.e0122,p.e01a1,p.a0101,(select a0101 from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as approver,p.curr_user,(select a0000 from "+alist.get(i)+"a01 a where a.a0100=p.a0100) as a0000,(select "+email_field+" from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as email_field,(select "+mobile_field+" from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as mobile_field ,nbase from p01 p ");
					strFrom.append(" where  p.state='0' and p.log_type='"+type+"' and (("+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.month("P0104")+"="+month+") or ("+Sql_switcher.year("P0106")+"='"+year+"' and "+Sql_switcher.month("P0106")+"="+month+"))");
					strFrom.append(" and p.P0115"+flag+"and "+Sql_switcher.day("P0104")+"="+name+" and p.e0122 like'"+codeitemid+"%'");
				}
				else if("1".equals(cycle)){
					strFrom.append("select p.a0100,p.b0110,p.e0122,p.e01a1,p.a0101,(select a0101 from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as approver,p.curr_user,(select a0000 from "+alist.get(i)+"a01 a where a.a0100=p.a0100) as a0000,(select "+email_field+" from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as email_field,(select "+mobile_field+" from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as mobile_field ,nbase from p01 p ");
					strFrom.append(" where  p.state='1' and p.log_type='"+type+"' and (("+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.month("P0104")+"="+month+") or ("+Sql_switcher.year("P0106")+"='"+year+"' and "+Sql_switcher.month("P0106")+"="+month+"))");
					strFrom.append(" and p.P0115"+flag+"and "+Sql_switcher.week("P0104")+"="+name+" and p.e0122 like'"+codeitemid+"%'");
				}
				else if("2".equals(cycle)){
					strFrom.append("select p.a0100,p.b0110,p.e0122,p.e01a1,p.a0101,(select a0101 from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as approver,p.curr_user,(select a0000 from "+alist.get(i)+"a01 a where a.a0100=p.a0100) as a0000,(select "+email_field+" from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as email_field,(select "+mobile_field+" from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as mobile_field ,nbase from p01 p ");
					strFrom.append(" where  p.state='2' and p.log_type='"+type+"' and (("+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.month("P0104")+"="+name+") or ("+Sql_switcher.year("P0106")+"='"+year+"' and "+Sql_switcher.month("P0106")+"="+name+" and p.P0115"+flag+" and p.e0122 like'"+codeitemid+"%'");
				}
				else if("3".equals(cycle)){
					strFrom.append("select p.a0100,p.b0110,p.e0122,p.e01a1,p.a0101,(select a0101 from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as approver,p.curr_user,(select a0000 from "+alist.get(i)+"a01 a where a.a0100=p.a0100) as a0000,(select "+email_field+" from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as email_field,(select "+mobile_field+" from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as mobile_field ,nbase from p01 p ");
					strFrom.append(" where  p.state='3' and p.log_type='"+type+"' and "+Sql_switcher.year("P0104")+"='"+year+"' and "+Sql_switcher.quarter("P0104")+"="+name+" and p.P0115"+flag+" and p.e0122 like'"+codeitemid+"%'");
				}
				else if("4".equals(cycle)){
					strFrom.append("select p.a0100,p.b0110,p.e0122,p.e01a1,p.a0101,(select a0101 from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as approver,p.curr_user,(select a0000 from "+alist.get(i)+"a01 a where a.a0100=p.a0100) as a0000,(select "+email_field+" from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as email_field,(select "+mobile_field+" from "+alist.get(i)+"a01 a where a."+usernamefield+"=p.curr_user) as mobile_field ,nbase from p01 p ");
					strFrom.append(" where  p.state='4' and p.log_type='"+type+"' and "+Sql_switcher.year("P0104")+"='"+year+"' and p.P0115"+flag+" and p.e0122 like'"+codeitemid+"%'");
				}
				strFrom.append(manageLimit);//再加上管理范围
				strFrom.append(" and nbase= '"+alist.get(i)+"' ");
				if(i!=(n-1)) {
                    strFrom.append(" union ");
                }
			}
			strFrom.append(" )  a");
		}
		
		return strFrom.toString();
	
	}
	public String getSqlColums(String report){
		String colums = "";
		if("0".equals(report)) {
            colums = "a0100,b0110,e0122,e01a1,a0101,nbase";
        } else if("1".equals(report)) {
            colums = "a0100,b0110,e0122,e01a1,a0101,approver,curr_user,nbase";
        }
		return colums;
		
	}
	/**
	 * 查出数组中不同的数据。即筛选出不同的数据库前缀
	 * */
	public String[] getDifferent(String[] para){
		String strTemp = "";
		HashMap map1 = new HashMap();
		for(int i=0;i<para.length;i++){
			if(map1.get(para[i])==null){
				strTemp += para[i]+",";
				map1.put(para[i], "1");
			}
		}
		String[] str = strTemp.split(",");
		return str;
	}
	
	/**
	 * 根据传递过来的参数得到发送邮件的sql语句:未全选时
	 * @param  params 从js中得到的数据
	 * @param  mailOrMessage  电子信箱字段名称或移动电话字段名称
	 * @param  flag  填报状态
	 * @return  发送邮件的sql语句
	 * */
	public StringBuffer getSqlNotAll(String params,String mailOrMessage,String flag){
		
		params = params.replaceAll("；", ";");
		String[] array = params.split(";");
		String[] person = array[0].split(","); //如果是未报，则人员编号字符串。如果是已报，则审批人字符串
		String[] prefix = array[1].split(",");//库前缀字符串
		String[] assist = array[2].split(",");//当是审批人字符串的时候，还要用到人员编号
		String[] basePrefix = this.getDifferent(prefix); //得到所涉及到的库前缀的名称
		int n = basePrefix.length;//库前缀的个数
		int m = prefix.length;//记录的个数
		int personcount = person.length;   //当是审批人编号的时候，可能没有审批人编号
		boolean isEmpty = true;
		for(int i=0;i<personcount;i++){
			if(!"nodata".equals(person[i])) {
                isEmpty = false;
            }
		}
		String[] basePerson = new String[n];  //得到对应每个库的人员编号代码。未报：人员编号。已报：审批人编号
		String[] assis = new String[n];
		
		if(isEmpty){ //如果审批人为空
			for(int i=0;i<n;i++){ 
				basePerson[i]="('')";
				assis[i]="('')";
			}
		}else{
			for(int i=0;i<n;i++){
				basePerson[i]="('";
				assis[i]="('";
			}
			for(int j=0;j<m;j++){
				for(int k=0;k<n;k++){
					if(prefix[j].equals(basePrefix[k])){
						if("nodata".equals(person[j])) {
                            break;
                        }
						basePerson[k] = basePerson[k] + person[j]+"','";
						assis[k] = assis[k]+ assist[j]+"','";
						break;
					}
				}
			}
			//把人员编码转换成这种形式：（'00000001','00000002'）
			for(int i=0;i<n;i++){
				basePerson[i] = basePerson[i].substring(0, basePerson[i].length()-2)+")";
				assis[i] = assis[i].subSequence(0, assis[i].length()-2)+")";
			}
		}
		
		
		//生成sql语句
		StringBuffer sbsql = new StringBuffer();	
		for(int i=0;i<n;i++){
			if("01".equals(flag)){
				sbsql.append("select a0101 as name ,"+mailOrMessage+" as address from "+basePrefix[i]+"a01 u where u.a0100 in "+basePerson[i]);
			}else if("02".equals(flag)){
				String usernamefield = ConstantParamter.getLoginUserNameField().toLowerCase();
				sbsql.append("select distinct approver as name,approver as approver,u."+mailOrMessage+" as address from "+basePrefix[i]+"a01 u,p01 p where u."+usernamefield+" =p.curr_user and p.curr_user in "+basePerson[i]+" and p.a0100 in "+assis[i]);
			}
			if(i!=(n-1)) {
                sbsql.append(" union ");
            }
		}
		return sbsql;
	}
	/**
	 * 根据传递过来的参数得到发送邮件的sql语句:全选时
	 * @param  sqlFrom 为getSqlFrom方法的返回值
	 * @param  mailOrMessage  电子信箱字段名称或移动电话字段名称
	 * @param  flag  填报状态
	 * @return  发送邮件的sql语句
	 * */
	public StringBuffer getSqlAll(String sqlFrom,String mailOrMessage,String flag){
		
		sqlFrom = PubFunc.keyWord_reback(sqlFrom);
		StringBuffer sbsql = new StringBuffer();
		if("01".equals(flag)){
			sbsql.append("select a0101 as name ,"+mailOrMessage+" as address  "+sqlFrom);
		}else if("02".equals(flag)){
			sbsql.append("select approver as name,approver as approver,"+mailOrMessage+" as address "+sqlFrom);
		}
		return sbsql;
	}
	/**
	 * 发送电子邮件
	 * @param  标志位，判断是发邮件还是发信箱
	 * @param  mailOrMessage  电子信箱字段名称或移动电话字段名称
	 * @param  plan_id  填报状态
	 * @param  numberlist  人员编号
	 * @param  sqlFrom 为getSqlFrom方法的返回值
	 * @param  content为输入的邮件内容
	 * @param  list 为要返回的数组
	 * @param  title 邮件标题
	 * */
	public ArrayList sendMessage(String flag,String mailOrMessage,String plan_id,String numberlist,String strFrom,String content,ArrayList list,String title){
		try{
			ContentDAO dao = new ContentDAO(this.conn);	
			RowSet rowSet = null;
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String sysTime=bartDateFormat.format(new Date());
			StringBuffer sbsql = new StringBuffer();
			String receiver = "";//保存收件人的姓名
			String address_str = "";//保存联系方式
			
			//plan_id ="01":未报状态，则获得收件人的通讯地址;="02":已报状态，则获得审批人的通讯地址
			if("1".equals(numberlist)){//如果是全选状态
				sbsql = this.getSqlAll(strFrom,mailOrMessage, plan_id);
			}else{//如果不是全选状态
				sbsql = this.getSqlNotAll(numberlist, mailOrMessage,plan_id);
			}
			
			rowSet = dao.search(sbsql.toString());
			while(rowSet.next()){
				String approver = "";
				if("02".equals(plan_id))//如果是报批
                {
                    approver = rowSet.getString("approver"); //审批人
                }
				
				receiver = rowSet.getString("name");//收件人
				address_str = rowSet.getString("address");
				
				String _content=content;			
				_content=_content.replaceAll("#","＃");
				_content=_content.replaceAll("＃发件人名称＃", this.userView.getUserFullName());
				_content=_content.replaceAll("＃收件人名称＃",receiver); 				
				_content=_content.replaceAll("＃审批人名称＃",approver); 
				_content=_content.replaceAll("＃系统时间＃",sysTime);
				
				if("1".equals(flag))
				{
					_content=_content.replaceAll(" ", ""); 
					_content=_content.replaceAll("\\r","");
					_content=_content.replaceAll("\\n","");
					_content=_content.replaceAll("\\r\\n","");
				}
				else
				{
					_content=_content.replaceAll(" ", "&nbsp;&nbsp;");
					_content=_content.replaceAll("\r\n", "<br>");
				}		
				if(address_str!=null && address_str.trim().length()>0)
				{						
					LazyDynaBean dyvo=new LazyDynaBean();
					if("1".equals(flag))
					{
						dyvo.set("sender",this.userView.getUserFullName());
						dyvo.set("receiver",receiver);
						dyvo.set("phone_num",address_str);
						dyvo.set("msg",_content);
						list.add(dyvo);
					}
					else
					{
						dyvo.set("title",title);
						dyvo.set("content",_content);
						dyvo.set("email",address_str);
						list.add(dyvo);
					}						
				}
			}
			if(rowSet!=null) {
                rowSet.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public boolean getIsUnit(String codeid){
		boolean isUnit = false;
		try{
			RowSet rowSet = null;
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sbsql = new StringBuffer();
			sbsql.append("select codesetid from organization where codeitemid='"+codeid+"'");
			rowSet = dao.search(sbsql.toString());
			String codesetid ="";
			if(rowSet.next()){
				codesetid = rowSet.getString("codesetid");
			}
			if("UN".equals(codesetid)) {
                isUnit = true;
            } else {
                isUnit = false;
            }
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return isUnit;
	}

}
