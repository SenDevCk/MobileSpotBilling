package org.cso.MSBUtil;

public class PrintUtilZebra {
	public static int LineNo;
	public  PrintUtilZebra() {
		LineNo=0;
	}
	static String GetYOffSet(int Line)
	{
		return String.valueOf((Line*30));
	}
	
	static String PrintAtLine(int Line,String Data)
	{
	  return "TEXT 5 0 0 "+GetYOffSet(Line)+"  "+Data+"\r\n";
	}
		
	public static String PrintLargeAtLine(int Line,String Data)
	{
	  return "TEXT 4 0 0 "+GetYOffSet(Line)+" "+Data+"\r\n";
	}
	
	public static String PrintNextData(String Header,String Data)
	{
		//String str="LEFT\r\n" + "TEXT 5 0 0 "+GetYOffSet(LineNo)+"  "+String.format("%-18s:", Header)+"\r\n";
		int len;
		String rep=" ";
		len=18-Header.length();
		
		for(int j=0;j<len;j++){
			rep= rep + "_" ;
			} 
		String str="LEFT\r\n" + "TEXT 5 0 0 "+GetYOffSet(LineNo)+"  "+String.format("%-18s", Header.trim())+"\r\n";
		str+="LEFT\r\n" + "TEXT 5 0 160 "+GetYOffSet(LineNo)+"  "+":"+"\r\n";
		str+="RIGHT 500\r\n"+"TEXT 5 0 0 "+GetYOffSet(LineNo)+"  "+Data+"\r\n" + "LEFT\r\n";
		LineNo++;
		return str;
	}
	
	public static String PrintNextData1(String Header,String Data)
	{
		//String str="LEFT\r\n" + "TEXT 5 0 0 "+GetYOffSet(LineNo)+"  "+String.format("%-18s:", Header)+"\r\n";
		int len;
		String rep=" ";
		len=18-Header.length();
		
		for(int j=0;j<len;j++){
			rep= rep + "_" ;
			} 
		String str="LEFT\r\n" + "TEXT 5 0 0 "+GetYOffSet(LineNo)+"  "+String.format("%-18s", Header.trim())+"\r\n";
		str+="LEFT\r\n" + "TEXT 5 0 260 "+GetYOffSet(LineNo)+"  "+":"+"\r\n";
		//str+="RIGHT 450\r\n"+"TEXT 5 0 0 "+GetYOffSet(LineNo)+"  "+Data+"\r\n" + "LEFT\r\n";
		str+="RIGHT 500\r\n"+"TEXT 5 0 0 "+GetYOffSet(LineNo)+"  "+Data+"\r\n" + "LEFT\r\n";
		LineNo++;
		return str;
	}
	
	
	public static String PrintNextData2(String Header,String Data1,String Data2)
	{
		int len;
		String rep=" ";
		len=18-Header.length();
		
		for(int j=0;j<len;j++){
			rep= rep + "_" ;
			} 
		String str="LEFT\r\n" + "TEXT 5 0 0 "+GetYOffSet(LineNo)+"  "+String.format("%-18s", Header.trim())+"\r\n";
		str+="LEFT\r\n" + "TEXT 5 0 160 "+GetYOffSet(LineNo)+"  "+Data1+":"+"\r\n";
		str+="RIGHT 500\r\n"+"TEXT 5 0 0 "+GetYOffSet(LineNo)+"  "+Data2+"\r\n" + "LEFT\r\n";
		LineNo++;
		return str;
	}
	
	
	
	public static String PrintNext(String Data)
	{
	  String str=PrintAtLine(LineNo,Data);
	  LineNo++;
	  return str;
	}
	
	public static String PrintLargeNext(String Data)
	{
		 String str=PrintLargeAtLine(LineNo,Data);
		 LineNo+=2;
		 return str;
	}

}
