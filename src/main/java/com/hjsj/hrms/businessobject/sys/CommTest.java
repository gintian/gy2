package com.hjsj.hrms.businessobject.sys;

import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;
import org.smslib.helper.CommPortIdentifier;
import org.smslib.helper.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 检测短信猫
 * 2012-10-24
 */
public class CommTest
{

	// 日志
	private static Category log = Category.getInstance(CommTest.class.getName());
	static CommPortIdentifier portId;

	static Enumeration portList;

	static int bauds[] = { 9600, 14400, 19200, 28800, 33600, 38400, 56000, 57600, 115200, 128000, 238000, 203400, 256000 };

	public static Enumeration getCleanPortIdentifiers() throws Exception
	{
	    Enumeration enumeration = null;
	    try
	    {
	        enumeration = CommPortIdentifier.getPortIdentifiers();
	        
	    }
	    catch(Exception ex)
	    {
	        ex.printStackTrace();
	        throw new Exception("请正确配置jdk环境下的驱动包RXTXcomm.jar、rxtxParallel.dll、rxtxSerial.dll");
	    } catch (Error e) {
	    	e.printStackTrace();
	    	throw new Exception("请正确配置jdk环境下的驱动包RXTXcomm.jar、rxtxParallel.dll、rxtxSerial.dll");
	    }
	    
	    return enumeration;
	}

	/**
	 * 检测短信猫
	 * @return 将检测到的猫封装成LazyDynaBean，放到List中返回
	 */
	public static List test() throws Exception
	{
		List list=new ArrayList();
		
		try
		{
    		portList = getCleanPortIdentifiers();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		try {
    		while (portList.hasMoreElements())
    		{
    			portId = (CommPortIdentifier) portList.nextElement();
    			
    			
    			
    			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
    			{
    
    				 for (int i = 0; i < bauds.length; i++) {
    	  					SerialPort serialPort = null;
    	  					try {
    	  						InputStream inStream;
    	  						OutputStream outStream;
    	  						int c;
    	  						String response;
    	  						serialPort = portId.open("SMSLibCommTester", 1971);
    	  						serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
    	  						serialPort.setSerialPortParams(bauds[i], SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    	  					
    	  						inStream = serialPort.getInputStream();
    	  						outStream = serialPort.getOutputStream();
    	  						serialPort.enableReceiveTimeout(1000);
    	  						c = inStream.read();
    	  						while (c != -1) {
                                    c = inStream.read();
                                }
    	  						outStream.write('A');
    	  						outStream.write('T');
    	  						outStream.write('\r');
    	  						Thread.sleep(1000);
    	  						response = "";
    	  						StringBuilder sb = new StringBuilder();
    	  						c = inStream.read();
    	  						while (c != -1)
    	  						{
    	  							sb.append((char) c);
    	  							c = inStream.read();
    	  						}
    	  						response = sb.toString();
    	  						if (response.indexOf("OK") >= 0) {
    	  							LazyDynaBean bean = new LazyDynaBean();
    	  							bean.set("com", portId.getName());
    	  							bean.set("bit", bauds[i] + "");
    	  							try {
    	  								// AT CGMI 查询厂商
    	  								outStream.write('A');
    	  								outStream.write('T');
    	  								outStream.write('+');
    	  								outStream.write('C');
    	  								outStream.write('G');
    	  								outStream.write('M');
    	  								outStream.write('I');
    	  								outStream.write('\r');
    	  								response = "";
    	  								c = inStream.read();
    	  								while (c != -1) {
    	  									response += (char) c;
    	  									c = inStream.read();
    	  								}
//    	  								System.out.println(" Found--: " + response);
//    	  								System.out.println(" 查询厂商Found:" + response.replaceAll("\\s+OK\\s+", "").replaceAll("AT\\+CGMI","").replaceAll("\n", "").replaceAll("\r", ""));
    	  								
    	  								bean.set("company", response.replaceAll("\\s+OK\\s+", "").replaceAll("AT\\+CGMI","").replaceAll("\n", "").replaceAll("\r", ""));
    	  								
    	  								
    	  								// AT+CGMM 查询型号
//    	  								System.out.print("  Getting Info...");
    	  								outStream.write('A');
    	  								outStream.write('T');
    	  								outStream.write('+');
    	  								outStream.write('C');
    	  								outStream.write('G');
    	  								outStream.write('M');
    	  								outStream.write('M');
    	  								outStream.write('\r');
    	  								
    	  								response = "";
    	  								c = inStream.read();
    	  								while (c != -1)
    	  								{
    	  									response += (char) c;
    	  									c = inStream.read();
    	  								}
//    	  								System.out.println(" 查询型号Found: " + response.replaceAll("\\s+OK\\s+", "").replaceAll("AT\\+CGMM", "").replaceAll("\n", "").replaceAll("\r", ""));
    	  								
    	  								
    	  								bean.set("modeltype",response.replaceAll("\\s+OK\\s+", "").replaceAll("AT\\+CGMM", "").replaceAll("\n", "").replaceAll("\r", ""));
    	  								
    	  								break;
    								}
    								catch (Exception e)
    								{
    									
//    									System.out.println("找不到设备");
    									log.error("找不到设备,错误原因：" + e.getMessage());
//    									throw new Exception("找不到设备");
    								}
    								
    	  							list.add(bean);
    							}
    							else
    							{
//    								System.out.println("找不到设备");
    								log.error("找不到设备");
//    								throw new Exception("找不到设备");
    							}
    	            	  
    	  					}
    						catch (Exception e)
    						{
//    							System.out.println("找不到设备");
    							Throwable cause = e;
    							while (cause.getCause() != null)
    							{
    								cause = cause.getCause();
    							}
//    							System.out.println(" (" + cause.getMessage() + ")");
    							log.error("找不到设备,错误原因：" + e.getMessage());
//    							throw new Exception("找不到设备");
    						}
    						finally
    						{
    							if (serialPort != null)
    							{
    								serialPort.close();
    							}
    						}
    	            	  
    	            	  }   
    			}
    			
    			
    		}
		}
		catch(Exception ex)
		{
		    ex.printStackTrace();
		    log.error(ex.getMessage());
//		    throw new Exception(ex.getMessage());
		}
		return list;
	}
}
