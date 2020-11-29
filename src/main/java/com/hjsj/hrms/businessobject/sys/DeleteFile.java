package com.hjsj.hrms.businessobject.sys;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 
 *<p>Title:DeleteFile.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 14, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class DeleteFile {

private static File m_root;   //记录要删除的路径
private static ArrayList m_dirs;//

	/**
	* 功能：删除文件夹方法
	* @param dir 要删除的文件夹路径(java.io.File类型)
	*/
	public void deleteDirs(java.io.File dir)throws GeneralException{
		try{
			m_root=dir;
			m_dirs= new ArrayList();
			 
			if(!m_root.isDirectory()){ //判断输入的是否为路径
				System.out.println(m_root.toString()+" 不是路径");
			}else{
			   //输出m_dirs中记录的值
			   //for(int i=0 ; i<m_dirs.size() ; i++){
			   // System.out.println(((File)m_dirs.get(i)).toString());
			   //}
				//m_dirs.add(m_root);
				this.visitAll(m_root); //获取指定路径下的所有文件已经文件夹（递归调用）
		        this.rootDelete();   //删除list中的所有文件（倒叙循环删除）
	        }
	   }catch(Exception ex){
		   //System.out.println("error in deleteDirs : "+ex.getMessage());
		   ex.printStackTrace();
		   throw GeneralExceptionHandler.Handle(ex);
	   }  
	}

	/**
	* 功能：删除文件或文件夹
	* 注意：使用倒叙删除，先删除文件，然后删除空文件夹
	*/
	private void rootDelete()throws GeneralException{
	try{  
		if(m_dirs!=null){
			//使用倒叙循环删除（先删除文件，再删除文件夹）
			for(int i=m_dirs.size()-1 ; i>=0 ; i--){
				File f=(File)m_dirs.remove(i);//获取之后删除list中的数据
				//删除数据
				if(!f.delete()){
					System.out.println("文件路径:"+f.toString()+" 不存在");
				}
			}
		}else{
			System.out.println("获取文件list列表（m_dirs）为空");
		}
	}catch(Exception ex){
		//System.out.println("error in rootDelete : "+ex.getMessage());
		ex.printStackTrace();
		throw GeneralExceptionHandler.Handle(ex);
	}
}

	/**
	* 功能：获取所有文件和文件夹，存储在m_dirs中
	* 注意：递归调用
	* @param tempRoot 文件路径
	*/
	private void visitAll(File tempRoot)throws GeneralException{
		try{
			//获取指定路径下的所有文件
			File[] dirs=tempRoot.listFiles();
			if(dirs!=null){
				//将文件数组转换成List对象
				List dirslist=Arrays.asList(dirs);
				//将dirslist完全添加到m_dirs中
				m_dirs.addAll(dirslist);
				//递归调用
				for(int i=0 ; i<dirslist.size() ; i++){
					this.visitAll((File)dirslist.get(i));
				}    
			}
	   }catch(Exception ex){
		   //System.out.println("error in visitAll : "+ex.getMessage());
		   ex.printStackTrace();
		   throw GeneralExceptionHandler.Handle(ex);
	   }
   }
}

