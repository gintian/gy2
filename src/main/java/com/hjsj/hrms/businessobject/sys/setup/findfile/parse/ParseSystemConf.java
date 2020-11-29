package com.hjsj.hrms.businessobject.sys.setup.findfile.parse;

import com.hjsj.hrms.businessobject.sys.options.JWhichUtil;
import com.hjsj.hrms.businessobject.sys.setup.findfile.Findfile;
import com.hjsj.hrms.utils.PubFunc;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class ParseSystemConf {
	public static void savaOrupdateProperty(String property,String value) throws IOException{
		boolean flag=false;
		String sysfilepath=Findfile.findPath("system.properties");
		File sysfile=new File(sysfilepath);
		File sysfiletemp=new File(sysfilepath+System.currentTimeMillis());
		sysfile.renameTo(sysfiletemp);
		FileInputStream fis=new FileInputStream(sysfiletemp);
		Reader r=new InputStreamReader(fis);
		BufferedReader br=new BufferedReader(r);	
		OutputStream os=new FileOutputStream(sysfile);
		Writer w=new OutputStreamWriter(os);
		BufferedWriter bw=new BufferedWriter(w);
		String line="";
		try {			
			while((line=br.readLine())!=null){
				if(!"".equals(line.trim())){
					int centeri=line.indexOf('=');
					String inproperty=line.substring(0,centeri);
					String invalue=line.substring(centeri+1,line.length());
					if(inproperty.equalsIgnoreCase(property)){
						invalue=value;
						flag=true;
					}
					String wstr=inproperty+"="+invalue;
					bw.write(wstr);
					bw.newLine();
				}
			}
			if(!flag){
				bw.write(property+"="+value);
			}
			bw.flush();
		} finally{
			PubFunc.closeIoResource(bw);
			PubFunc.closeIoResource(w);
			PubFunc.closeIoResource(os);
			PubFunc.closeIoResource(br);
			PubFunc.closeIoResource(r);
			PubFunc.closeIoResource(fis);
		}
		sysfiletemp.deleteOnExit();
	}
	public static void savaOrupdateProperty(HashMap hm) throws IOException{
		
		String sysfilepath=Findfile.findPath("system.properties");
		File sysfile=new File(sysfilepath);
		File sysfiletemp=new File(sysfilepath+System.currentTimeMillis());
		sysfile.renameTo(sysfiletemp);
		FileInputStream fis=new FileInputStream(sysfiletemp);
		Reader r=new InputStreamReader(fis);
		BufferedReader br=new BufferedReader(r);	
		OutputStream os=new FileOutputStream(sysfile);
		Writer w=new OutputStreamWriter(os);
		BufferedWriter bw=new BufferedWriter(w);
		String line="";
		try {
			while((line=br.readLine())!=null){
				int centeri=line.indexOf('=');
				String inproperty=line.substring(0,centeri);
				String invalue=line.substring(centeri+1,line.length());
				if(hm.containsKey(inproperty)){
					invalue=(String) hm.get(inproperty);
				}
				String wstr=inproperty+"="+invalue;
				bw.write(wstr);
				bw.newLine();
			}
			bw.flush();
		}finally{
			PubFunc.closeIoResource(bw);
			PubFunc.closeIoResource(w);
			PubFunc.closeIoResource(os);
			PubFunc.closeIoResource(br);
			PubFunc.closeIoResource(r);
			PubFunc.closeIoResource(fis);
		}
		sysfiletemp.deleteOnExit();
	}
	
	/**
	 * 删除system.properties某属性
	 * @param hm 
	 * @throws IOException
	 */
	public static void delProperty(HashMap hm) throws IOException{
		String rootPath=JWhichUtil.getResourceFilePath("system.properties");	    	
		File file = new File(rootPath); 
        FileInputStream fis = null;
        InputStreamReader isr = null;
        FileOutputStream fos = null;
        BufferedReader br = null;
        PrintWriter pw = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis); 
	        br = new BufferedReader(isr); 
	        StringBuffer buf = new StringBuffer(); 
	        String line = "";
	        while ((line = br.readLine()) != null) { 
	        	boolean flag = true;
	        	for(Iterator i= hm.keySet().iterator();i.hasNext();){
	        		String prokey = (String)i.next();
		        	if(line.indexOf(prokey)!=-1){
		        		flag = false;
		        		break;
		        	}
	        	}
	        	if(flag){
	                buf = buf.append(line); 
	                buf = buf.append("\n\r");
	        	}
            } 
            fos = new FileOutputStream(file); 
            pw = new PrintWriter(fos); 
            pw.write(buf.toString().toCharArray()); 
            pw.flush(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) { 
            e.printStackTrace(); 
        } finally{
        	PubFunc.closeResource(pw);
        	PubFunc.closeResource(fos);
        	PubFunc.closeResource(br);
        	PubFunc.closeResource(isr);
            PubFunc.closeResource(fis);
            PubFunc.closeResource(file);
            
        }
	}
	
}
