package com.hjsj.hrms.transaction.lawbase;

import java.util.ArrayList;
import java.util.Collections;

public class Zip {
	static final int BUFFER = 2048;

	public static void main(String argv[]) {
		ArrayList a = new ArrayList();
		a.add("d");
		a.add("c");
		a.add("b");
		a.add("a");
		Collections.sort(a);
		for (int i = 0; i < a.size(); i++) {
			System.out.println(a.get(i));
		}
		int i = Collections.binarySearch(a, "c");
		System.out.println(i);
		
		
		
//		LinkedList l = new LinkedList();
//		l.add("1");
//		l.add("2");
//		l.add("3");
//		l.add(0, l.get(l.size() - 1));
//		l.remove(l.size() - 1);
//		for (int i = 0; i < l.size(); i++) {
//			System.out.println(l.get(i));
//		}
//		try {
//			BufferedInputStream origin = null;
//			FileOutputStream dest = new FileOutputStream("c:\\zip\\myfigs.zip");
//			CheckedOutputStream checksum = new CheckedOutputStream(dest,
//					new Adler32());
//			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
//					checksum));
//			byte data[] = new byte[BUFFER];
//			// get a list of files from current directory
//			File f = new File(".");
//			String files[] = f.list();
//			for (int i = 0; i < files.length; i++) {
//				System.out.println("Adding: " + files[i]);
//				FileInputStream fi = new FileInputStream(files[i]);
//				origin = new BufferedInputStream(fi, BUFFER);
//				ZipEntry entry = new ZipEntry(files[i]);
//				out.putNextEntry(entry);
//				int count;
//				while ((count = origin.read(data, 0, BUFFER)) != -1) {
//					out.write(data, 0, count);
//				}
//				origin.close();
//			}
//			out.close();
//			//System.out.println("checksum:" + checksum.getChecksum().getValue());
//		} catch (Exception e) {
//			e.printStackTrace();
		//}
//		HashSet set = new HashSet();
//		set.add("1");
//		set.add("1");
//		Iterator it = set.iterator();
//		while(it.hasNext()) {
//			System.out.println(it.next());
//		}
//		for( int i = 0; i<set.size();i++)
////			System.out.println(set.);
//		System.out.println("fdsafsd");
	}
}
