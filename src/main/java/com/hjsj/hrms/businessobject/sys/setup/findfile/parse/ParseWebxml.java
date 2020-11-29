package com.hjsj.hrms.businessobject.sys.setup.findfile.parse;

import com.hjsj.hrms.businessobject.sys.setup.findfile.Findfile;
import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Document;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParseWebxml {
	private static Document doc ;
	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static void updateSessionTime(String min) throws  IOException{
	    FileInputStream fis = null;
	    Reader r = null;
	    BufferedReader br = null;
	    OutputStream os = null;
	    Writer  w = null;
	    BufferedWriter bw = null;
	    try{
		String webxmlpath=Findfile.findPath("web.xml");
		File wf=new File(webxmlpath);
		File tempwf=new File(webxmlpath+System.currentTimeMillis());
		wf.renameTo(tempwf);
		fis=new FileInputStream(tempwf);
		r=new InputStreamReader(fis);
		br=new BufferedReader(r);
		os=new FileOutputStream(wf);
		w=new OutputStreamWriter(os);
		bw=new BufferedWriter(w);
		String line="";
		while((line=br.readLine())!=null){
			Pattern pattern=Pattern.compile("\\s*<session-timeout>\\d*</session-timeout>");
			Matcher matcher=pattern.matcher(line);
			boolean b=matcher.matches();
			if(b){
			line="<session-timeout>"+min+"</session-timeout>";
			}
			bw.write(line);
			bw.newLine();
		}
		bw.flush();
		tempwf.deleteOnExit();
	   }catch(Exception e){
	       e.printStackTrace();
	   }
	   finally{
	       PubFunc.closeIoResource(br);
	       PubFunc.closeIoResource(fis);
	       PubFunc.closeIoResource(bw);
	       PubFunc.closeIoResource(w);
	       PubFunc.closeIoResource(r);
	       PubFunc.closeIoResource(os);
	   }
	}
}
