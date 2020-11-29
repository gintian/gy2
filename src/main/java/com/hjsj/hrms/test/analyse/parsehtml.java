package com.hjsj.hrms.test.analyse;

import com.hjsj.hrms.utils.PubFunc;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.cyberneko.html.filters.ElementRemover;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
public class parsehtml {
public static void main(String[] argv) throws Exception {
	FileOutputStream fos =null;
	FileInputStream stream = null;
	try{
		fos =  new FileOutputStream("c:\\b.doc");
DOMParser parser = new DOMParser();
//for (int i = 0; i < argv.length; i++) {
stream=new FileInputStream("d:\\a.html");
InputSource ss=new InputSource(stream);
parser.parse(ss);
Document doc=parser.getDocument();
parser.getDocument().createElement("Tr");
//XPath reportPath = XPath();// 取得根节点
//List childlist=reportPath.selectNodes(doc);
ElementRemover er=new ElementRemover();
//doc.createElement(er);
//doc.appendChild(er);

print(parser.getDocument(), "",doc);
//}
XMLSerializer serializer = new XMLSerializer();
// Insert your PipedOutputStream here instead of System.out!			
serializer.setOutputByteStream(fos);
OutputFormat out=new OutputFormat();
out.setEncoding("UTF-8");
serializer.setOutputFormat(out);
serializer.serialize(doc);
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		PubFunc.closeIoResource(fos);
		PubFunc.closeIoResource(stream);
	}

}
public static void print(Node node, String indent,Document doc) {

if (node.getNodeValue() != null){
	if("".equals(node.getNodeValue().trim())){
		//System.out.println("ss" + node.getNodeName());
	}else{
	//System.out.println("xx" + node.getNodeName());
	}
}
else
{
	System.out.println("xx" + node.getNodeName());
	if("TABLE".equalsIgnoreCase(node.getNodeName()))
	{
		
    	System.out.println(node.getNodeName());
    	System.out.println("z" + node.getAttributes().getNamedItem("style"));
    	//System.out.println("zz" + node.getTextContent());
    	System.out.println("zzz" + node.getChildNodes().getLength());
    	NodeList no=node.getChildNodes();
    	for(int n=0;n<no.getLength();n++)
    	{
    		if(n==1)
    		{
    		  Node s=no.item(n).cloneNode(true);
    		  node.appendChild(s);
    		  System.out.println("s" + s.getNodeName()+"--"+s.getNodeValue());
    		}
    	}
    	
		//Element tr=doc.createElement("tr");
		//Element td=doc.createElement("td");
		//td.setTextContent("邓灿是个王八蛋");
		//tr.appendChild(td);


	}
}

Node child = node.getFirstChild();
while (child != null) {
print(child, indent+" ",doc);
System.out.println("cc" + child.getNodeValue());
child = child.getNextSibling();
}
}
}

