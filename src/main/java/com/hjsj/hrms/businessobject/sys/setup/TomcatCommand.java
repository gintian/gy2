package com.hjsj.hrms.businessobject.sys.setup;

import java.io.*;

public class TomcatCommand {
	private static void Shutdown(){
        Runtime R=Runtime.getRuntime();
        String os=(String) System.getenv().get("OS");
        String tomcat_home=System.getenv("CATALINA_HOME");      
        try {
        	
            String[] command =new String[4];
            if(os.startsWith("Windows")){
            	command[0]="cmd";
            	command[1]="/c";
            	command[2]=tomcat_home+"\\bin\\shutdown.bat";
            	command[3]="";
            }else{
            	command[0]="./shutdown.sh";
            	command[1]="";
            	command[2]="";
            	command[3]="";
            }
            Process P= R.exec(command);
            InputStream errin=P.getErrorStream();
            Reader r=new InputStreamReader(errin);
            BufferedReader br1=new BufferedReader(r);
            String l="";
            while((l=br1.readLine())!=null){
                System.out.println(l);
            }
            errin.close();
            r.close();
            br1.close();
            try {
                int waitvalue = P.waitFor();
//                System.out.println(waitvalue);
            } catch (InterruptedException ex1) {
                ex1.printStackTrace();
            }
            int exitvalue=P.exitValue();
//            System.out.println(exitvalue);
            InputStream in=P.getInputStream();
            Reader rr=new InputStreamReader(in);
            BufferedReader br=new BufferedReader(rr);
            String line="";
//            while((line=br.readLine())!=null){
//                System.out.println(line);
//            }
            in.close();
            rr.close();
            br.close();
            
//            System.out.println("end here");
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
	}
	private static void StartUP(){
        Runtime R=Runtime.getRuntime();
        String os=(String) System.getenv().get("OS");
        String tomcat_home=System.getenv("CATALINA_HOME");      
        try {
        	
            String[] command =new String[4];
            if(os.startsWith("Windows")){
            	command[0]="cmd";
            	command[1]="/c";
            	command[2]=tomcat_home+"\\bin\\startup.bat";
            	command[3]="";
            }else{
            	command[0]="./startup.sh";
            	command[1]="";
            	command[2]="";
            	command[3]="";
            }
            Process P= R.exec(command);
            InputStream errin=P.getErrorStream();
            Reader r=new InputStreamReader(errin);
            BufferedReader br1=new BufferedReader(r);
            String l="";
//            while((l=br1.readLine())!=null){
//                System.out.println(l);
//            }
            try {
                int waitvalue = P.waitFor();
//                System.out.println(waitvalue);
            } catch (InterruptedException ex1) {
                ex1.printStackTrace();
            }
            errin.close();
            r.close();
            br1.close();
            int exitvalue=P.exitValue();
//            System.out.println(exitvalue);
            InputStream in=P.getInputStream();
            Reader rr=new InputStreamReader(in);
            BufferedReader br=new BufferedReader(rr);
            String line="";
//            while((line=br.readLine())!=null){
//                System.out.println(line);
//            }
            in.close();
            rr.close();
            br.close();
            System.out.println("end here");
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
	}
	private static void Restart(){
		Shutdown();
		StartUP();
	}
	/**
	 * @param args
	 */
	  public static void Command(String args) {
		  if(args.length()>0){
			  if("starup".equalsIgnoreCase(args)){
				  StartUP();
			  }
			  if("shutdown".equalsIgnoreCase(args)){
				  Shutdown();
				
			  }
			  if("restart".equalsIgnoreCase(args)){
				  Restart();
			  }
		  	}
//		  else{
//			  System.out.println("command errer!");
//		  }
    }

}
