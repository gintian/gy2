package com.hjsj.hrms.businessobject.lawbase;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.transaction.lawbase.CommonBusiness;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import javax.sql.RowSet;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LawDirectory {

     /**
     * 得到上级部门的id
     * @param codeitemid
     * @return
     */
    public String getUpDeptId(String codeitemid,String codesetid,Connection conn)
    {
        String orgSql="SELECT codesetid,parentid,codeitemid from organization where codeitemid='"+ codeitemid +"'";
        orgSql=orgSql+" and codesetid='"+codesetid+"'";
        ContentDAO dao=new ContentDAO(conn);
        RowSet rowSet=null;
        String parentid="";
        try
        {
            rowSet=dao.search(orgSql);
            if(rowSet.next())
            {
                parentid=rowSet.getString("parentid");
            }
        
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return parentid;
    }
    public String getOrgStrs(String codeitemid,String codesetid,Connection conn)
    {
        ArrayList list=new ArrayList();  
        
        boolean isCorrect=false;
        if(codeitemid==null)
        {
            return "";
        }else if(codeitemid.length()<=0)
        {
            return "''";
        }
        String root=codeitemid;
        
        while(!isCorrect)
        {
            list.add(codeitemid);
            codeitemid=getUpDeptId(codeitemid,codesetid,conn);
            if(root.equals(codeitemid))
            {
                isCorrect=true;
                break;
            }           
            root=codeitemid;
            
        }
        StringBuffer OrgStr=new StringBuffer();
        for(int i=0;i<list.size();i++)
        {
            OrgStr.append("'"+list.get(i).toString()+"',");
        }
        String b0110s=OrgStr.toString().substring(0,OrgStr.length()-1);
        return b0110s;
    }
    /**
     * 得到附件表的主键最大值+1
     * @param conn
     * @return
     */
    public String getMaxEitId(Connection conn)
    {
        String v_type=Sql_switcher.sqlToInt("ext_file_id");       
        String sql ="select max("+v_type+")+1 as ext_file_id from law_ext_file";
        String value_id="";
        ContentDAO dao=new ContentDAO(conn);
        try
        {
            RowSet rs=dao.search(sql);
            if(rs.next())
            {
                value_id=rs.getString("ext_file_id");               
            }else
            {
                value_id="1";
            }
        }catch(Exception e )
        {
            e.printStackTrace();
        }
        if(value_id==null||value_id.length()<=0)
        {
            value_id="1";
        }
        return value_id;
    }
    
    public void reBuildIndex(InputStream inputstream,String file_id,String base_id,String ext,String digest)
    {
        IndexWriter writer = null;
        try
        {
            String indexPath = getLawbaseDir();         
            if(inputstream==null) {
                return;
            }
            if (IndexReader.indexExists(indexPath)) {
                IndexReader ir = null;
                try {
                    ir = IndexReader.open(indexPath);
                    ir.delete(new Term("id", file_id));
                } catch (Exception err) {
                    err.printStackTrace();
                } finally {
                    try {
                        ir.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            
            if (IndexReader.indexExists(indexPath)) {
                writer = new IndexWriter(indexPath, new ChineseAnalyzer(),
                        false);
            } else {
                writer = new IndexWriter(indexPath, new ChineseAnalyzer(),
                        true);
            }
            Document doc = new Document();
            doc.add(Field.Keyword("id", file_id));
            if(base_id!=null&&base_id.length()>0) {
                doc.add(Field.Keyword("base_id", base_id));
            }
            
            if ("doc".equals(ext.trim().toLowerCase())) {
                doc
                        .add(Field.Text("body",
                                (Reader) new InputStreamReader(
                                        CommonBusiness.wordToText(inputstream))));
            }
            if ("xls".equals(ext.trim().toLowerCase())|| "xlsx".equals(ext.trim().toLowerCase())) {
                doc.add(Field
                        .Text("body", (Reader) new InputStreamReader(
                                CommonBusiness.excelToText(inputstream))));
            }
            if ("txt".equals(ext.trim().toLowerCase())
                    || "html".equals(ext.trim().toLowerCase())) {
                doc.add(Field.Text("body", (Reader) new InputStreamReader(
                        inputstream)));
            }
            // 将文档写入索引
            writer.addDocument(doc);
            // 索引优化
            // writer.optimize();
            // 关闭写索引器
            
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally
        {
            try
            {
                if(writer!=null) {
                    writer.close();
                }
            }catch(Exception e)
            {
                //e.printStackTrace();
            }
        }
        //CommonBusiness.addIndex(file_id, digest);
    }
    
    public static String getLawbaseDir()
    {
        String tempDirName = System.getProperty("java.io.tmpdir");    
        String lawDir="lawbase";
        String lawbase=tempDirName+File.separator+lawDir;
        
        boolean isCorrect=false;
        try
        {
            if (tempDirName == null) {
                throw new RuntimeException(
                        "Temporary directory system property (java.io.tmpdir) is null.");
            }
            File tempDir = new File(tempDirName);
            if (!tempDir.exists()) 
            {
                tempDir.mkdirs();              
            }
            String [] fileList = tempDir.list(); //目录下所有文件及目录
            for(int i=0;i<fileList.length ; i++)
            {
                String fileName = fileList[i];
                if(fileName.equalsIgnoreCase(lawDir)){
                    isCorrect=true;
                    break;
                }
            }
            if(!isCorrect)
            {
                File tempLawDir = new File(lawbase);
                tempLawDir.mkdirs();
            }
           
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return lawbase;
    }     
    /**
     * 得到制度节点下所有的子节点
     * @param base_id
     * @param conn
     * @return
     */
    public String getBase_Ids(String base_id,Connection conn)
    {
        StringBuffer base_ids=new StringBuffer();
        ArrayList all_base_ids=new ArrayList();
        ArrayList new_list=getBase_ChildIds(base_id,conn);
        all_base_ids.add(base_id);
        boolean isCorrect=true;
        StringBuffer new_base_ids=new StringBuffer();
        do
        {
            if(new_list!=null&&new_list.size()>0)
            {
                new_base_ids=new StringBuffer();
                for(int i=0;i<new_list.size();i++)
                {
                    all_base_ids.add(new_list.get(i));
                    new_base_ids.append("'"+new_list.get(i).toString()+"',");
                }
                new_base_ids.setLength(new_base_ids.length()-1);
                new_list=getBase_ChildIds(new_base_ids.toString(),conn);
            }else
            {
                isCorrect=false;
            }
        }while(isCorrect);
        for(int i=0;i<all_base_ids.size();i++)
        {
            base_ids.append("'"+all_base_ids.get(i).toString()+"',");
        }
        base_ids.setLength(base_ids.length()-1);
        return base_ids.toString();
    }
    public ArrayList getBase_ChildIds(String base_ids,Connection conn)
    {
        ArrayList list=new ArrayList();
        StringBuffer sql=new StringBuffer();
        sql.append("select * from law_base_struct ");
        sql.append(" where up_base_id in("+base_ids+") and base_id<>up_base_id");
        try
        {
            ContentDAO dao=new ContentDAO(conn);
            RowSet rs=dao.search(sql.toString());
            if(rs.next())
            {
                list.add(rs.getString("base_id"));
            }           
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 判断是否有新文章
     * @param conn
     * @param base_ids
     * @param days
     * @return
     */
    public boolean isNewLawText(Connection conn,String base_ids,String days)
    {
        boolean isCorrect=false;
        if(days==null||days.length()<=0) {
            days="5";
        }
        String cur_d=PubFunc.getStringDate("yyyy-MM-dd");
        StringBuffer sql=new StringBuffer();
        sql.append("select file_id from law_base_file");
        sql.append(" where base_id in("+base_ids+")");
        sql.append(" and "+Sql_switcher.diffDays(Sql_switcher.dateValue(cur_d),"issue_date")+"<="+days);
        sql.append(" and "+Sql_switcher.diffDays(Sql_switcher.dateValue(cur_d),"issue_date")+">=0");        
        try
        {
            ContentDAO dao=new ContentDAO(conn);
            RowSet rs=dao.search(sql.toString());
            if(rs.next())
            {
                isCorrect=true;
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return isCorrect;
    }
    public String updateRoleContent(String role_content,String delete_id)
    {
        if(role_content==null||role_content.length()<=0) {
            return "";
        }
        if(delete_id==null||delete_id.length()<=0) {
            return role_content;
        }
        String []role_contents=role_content.split(",");
        StringBuffer buf=new StringBuffer();
        for(int i=0;i<role_contents.length;i++)
        {
          String one_content=role_contents[i];
          if(!one_content.equalsIgnoreCase(delete_id))
          {
              buf.append(one_content+",");
          }
        }
        if(buf.length()>0) {
            buf.setLength(buf.length()-1);
        }
        return buf.toString();
    }
    
    /**
     * 判断文档节点是否有子节点
     * @param base_id
     * @return
     */
    public static boolean hasChildNode(String base_id){
        boolean has = false;
        StringBuffer sql = new StringBuffer();
        sql.append("select 1 from law_base_struct where up_base_id = '" + base_id + "'");
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                has = true;
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
            KqUtilsClass.closeDBResource(conn);
        }
        
        return has;
    }
}
