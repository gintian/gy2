package com.hjsj.hrms.businessobject.param;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;

/** 
 *<p>Title:PerGradeBo.java</p> 
 *<p>Description:创建指标标度js</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 6, 2008</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class PerGradeBo 
{	
	
	public String replaceChar(String s)
	{
		String temp="";
		if(s!=null)
		{ 
			temp=s.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\"").replaceAll("\'", "\\\\\'");
		}
		return temp;
	}	
	
	public void createPerGradeJS(String path,Connection con)
	{
		FileOutputStream fileOut=null;
		ByteBuffer buffer = null;
		FileChannel fc =null;
		try
		{
			StringBuffer buf_codeitem = new StringBuffer();
			buf_codeitem.append(" var per_grade = new Array();");
			buf_codeitem.append("\r\n");
			String sql =" select per_grade.*,per_point.pointkind,pts.subsys_id from per_pointset pts,per_grade,per_point "
				+"where pts.pointsetid=per_point.pointsetid and per_grade.point_id=per_point.point_id order by per_grade.point_id,per_grade.grade_id";   //gradevalue desc,gradecode";
			ContentDAO dao  = new ContentDAO(con);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				buf_codeitem.append("per_grade[per_grade.length] = ");
				buf_codeitem.append("{point_id:\"");
				buf_codeitem.append(replaceChar(rs.getString("point_id")));
				buf_codeitem.append("\",gradedesc:\"");
				buf_codeitem.append(replaceChar(Sql_switcher.readMemo(rs,"gradedesc")));
				buf_codeitem.append("\",gradecode:\"");
				buf_codeitem.append(replaceChar(rs.getString("gradecode").toUpperCase()));
				
				buf_codeitem.append("\",top_value:\"");
				if(rs.getString("top_value")!=null) {
                    buf_codeitem.append(replaceChar(rs.getString("top_value")));
                }
				buf_codeitem.append("\",bottom_value:\"");
				if(rs.getString("bottom_value")!=null) {
                    buf_codeitem.append(replaceChar(rs.getString("bottom_value")));
                }
				buf_codeitem.append("\",pointkind:\"");
				buf_codeitem.append(replaceChar(rs.getString("pointkind")));				
				buf_codeitem.append("\",");
				buf_codeitem.append("gradevalue:\"");
				buf_codeitem.append(replaceChar(rs.getString("gradevalue")));
				buf_codeitem.append("\",subsys_id:\"");
				buf_codeitem.append(replaceChar(rs.getString("subsys_id")));
				
				buf_codeitem.append("\"};");
				buf_codeitem.append("\r\n");
			}
			PubFunc.closeDbObj(rs);
			buf_codeitem.append("var point_grade=new Array();\r\n");
			buf_codeitem.append("var point_id=\"\";\r\n");
			buf_codeitem.append("var temp=new Array();\r\n");
			buf_codeitem.append("for( var i=0 ; i<per_grade.length ; i++){\r\n");
			buf_codeitem.append("   if(point_id==\"\")\r\n");
			buf_codeitem.append("      point_id=per_grade[i].point_id;\r\n");
			buf_codeitem.append("   if(point_id!=per_grade[i].point_id)\r\n");
			buf_codeitem.append("   {\r\n");
			buf_codeitem.append("     point_grade['p'+point_id]=temp;\r\n");
			buf_codeitem.append("     temp=new Array();\r\n");
			buf_codeitem.append("     point_id=per_grade[i].point_id;\r\n");
			buf_codeitem.append("   }\r\n");
			buf_codeitem.append("temp[temp.length]=per_grade[i];\r\n");
			buf_codeitem.append("}\r\n");
			buf_codeitem.append("point_grade['p'+point_id]=temp;");
			
			//了解程度
			sql="select * from per_know where status=1 order by seq";
			rs = dao.search(sql);
			buf_codeitem.append("\r\n var per_know = new Array();");
			while(rs.next())
			{
				String know_id =rs.getString("know_id");
				String name=replaceChar(rs.getString("name"));
				buf_codeitem.append("\r\n per_know[per_know.length] ={gradecode:\""+know_id+"\",");
				buf_codeitem.append("gradedesc:\""+name+"\"");
				buf_codeitem.append("};");
			}
			PubFunc.closeDbObj(rs);	
			
			//总体评价
			sql="select id,degree_id,itemname from per_degreedesc order by degree_id,id";
			rs = dao.search(sql);
			buf_codeitem.append("\r\n var per_degreedesc = new Array();");
			while(rs.next())
			{
				String id =rs.getString("id");
				String degree_id=replaceChar(rs.getString("degree_id"));
				String itemname=replaceChar(rs.getString("itemname"));
				buf_codeitem.append("\r\n per_degreedesc[per_degreedesc.length] ={id:\""+id+"\",");
				buf_codeitem.append("degree_id:\""+degree_id+"\",itemname:\""+itemname+"\"");
				buf_codeitem.append("};");
			}
			PubFunc.closeDbObj(rs);	
			
			// 绩效标准标度
			sql="select * from per_grade_template";
			rs = dao.search(sql);
			buf_codeitem.append("\r\n var per_standdegree = new Array();");
			while(rs.next())
			{
				
				String grade_template_id=rs.getString("grade_template_id");
				String gradevalue=replaceChar(rs.getString("gradevalue"));
				String gradedesc=replaceChar(rs.getString("gradedesc"));
				String top_value=rs.getString("top_value");
				String bottom_value=rs.getString("bottom_value");
				
				buf_codeitem.append("\r\n per_standdegree['"+grade_template_id.toUpperCase()+"'] ={grade_template_id:\""+grade_template_id.toUpperCase()+"\",");
				buf_codeitem.append("gradevalue:\""+gradevalue+"\",gradedesc:\""+gradedesc+"\",");
				buf_codeitem.append("top_value:\""+top_value+"\",bottom_value:\""+bottom_value+"\"");
				buf_codeitem.append("};");
			}
			PubFunc.closeDbObj(rs);	
			
			// 能力素质标准标度
			sql="select * from per_grade_competence";
			rs = dao.search(sql);
			buf_codeitem.append("\r\n var per_competencedegree = new Array();");
			while(rs.next())
			{
				
				String grade_template_id=rs.getString("grade_template_id");
				String gradevalue=replaceChar(rs.getString("gradevalue"));
				String gradedesc=replaceChar(rs.getString("gradedesc"));
				String top_value=rs.getString("top_value");
				String bottom_value=rs.getString("bottom_value");
				
				buf_codeitem.append("\r\n per_competencedegree['"+grade_template_id.toUpperCase()+"'] ={grade_template_id:\""+grade_template_id.toUpperCase()+"\",");
				buf_codeitem.append("gradevalue:\""+gradevalue+"\",gradedesc:\""+gradedesc+"\",");
				buf_codeitem.append("top_value:\""+top_value+"\",bottom_value:\""+bottom_value+"\"");
				buf_codeitem.append("};");
			}
			PubFunc.closeDbObj(rs);						
			
			//绩效指标
			sql="select * from per_point";
			rs = dao.search(sql);
			buf_codeitem.append("\r\n var per_pointArray = new Array();");
			while(rs.next())
			{
				
				String point_id=replaceChar(rs.getString("point_id"));
				String pointname=replaceChar(rs.getString("pointname")); //rs.getString("pointname").replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"","\\\"");
				String pointkind=replaceChar(rs.getString("pointkind"));
				String status=replaceChar(rs.getString("status"));
				
				
				buf_codeitem.append("\r\n per_pointArray['"+point_id+"'] ={point_id:\""+point_id+"\",");
				buf_codeitem.append("pointname:\""+pointname+"\",pointkind:\""+pointkind+"\",");
				buf_codeitem.append("status:\""+status+"\"");
				buf_codeitem.append("};");
			}
			PubFunc.closeDbObj(rs);			
			
			String filename="pergrade.js";
		    fileOut = new FileOutputStream(path+System.getProperty("file.separator")+filename);
		    fc = fileOut.getChannel();
		    buffer = ByteBuffer.allocate(buf_codeitem.toString().getBytes().length);
		    buffer.put(buf_codeitem.toString().getBytes());
		    buffer.flip();
		    fc.write( buffer );  	
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			if(buffer!=null) {
                buffer.clear();
            }
			PubFunc.closeIoResource(fileOut);
			if(fc!=null)
			{
				try
				{
					fc.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
}
