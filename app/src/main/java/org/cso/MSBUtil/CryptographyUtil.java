package org.cso.MSBUtil;


public class CryptographyUtil {

	 public static String Decrypt(String EncString)
     {
         String DecData = "";
         try
         {
         int temp = 0;
         char chrtemp = '\0';
         int splitindex = 0;
         for (int i = 0; i < EncString.length() / 3; i++)
         {
             if ((i % 4) == 0)
             {
                 temp = Integer.parseInt((EncString.substring(splitindex, splitindex+3))) - (int)Math.pow(1, 1);
             }
             else if ((i % 4) == 1)
             {
                 temp =  Integer.parseInt((EncString.substring(splitindex, splitindex+3))) - (int)Math.pow(2, 2);
             }
             else if ((i % 4) == 2)
             {
                 temp =  Integer.parseInt((EncString.substring(splitindex, splitindex+3))) - (int)Math.pow(3, 3);
             }
             else
             {
                 temp =  Integer.parseInt((EncString.substring(splitindex, splitindex+3))) - (int)Math.pow(4, 4);
             }
             chrtemp = (char)temp;
             DecData += String.valueOf(chrtemp);
             splitindex += 3;
         }
         }catch (Exception e) {
 			System.out.println("Decrypt Error: "+e.toString());
 		}
         return DecData;
     }


	 public static String Encrypt(String originalString)
     {
		 String code="" ;
		 try
		 {
        
         byte[] strascii = originalString.getBytes();
         
         
         for (int i = 0; i < strascii.length; i++)
         {
             if ((i % 4) == 0)
             {
                 code += String.format("%03d",((strascii[i] + (int)Math.pow(1, 1))));
             }
             else if ((i % 4) == 1)
             {
                 code +=  String.format("%03d",((strascii[i] + (int)Math.pow(2, 2))));
             }
             else if ((i % 4) == 2)
             {
                 code +=  String.format("%03d",((strascii[i] + (int)Math.pow(3, 3))));
             }
             else
             {
                 code += String.format("%03d",((strascii[i] + (int)Math.pow(4, 4))));
             }
         }
         }catch (Exception e) {
			System.out.println("Encrypt Error: "+e.toString());
		}
         return code;
     }
	
}
