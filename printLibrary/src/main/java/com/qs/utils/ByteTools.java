package com.qs.utils;

public class ByteTools {
	
	public static int makeWord(byte a, byte b){
		//�����㴮��һ��ֻ�ܴ���8��λ����ô�ͷָ��ֽڵ��ֽڣ�һ���ֽ�8λ�����ȰѸ�8λ����һ��16λ�ı�����i = xx��Ȼ��ִ��i<<8����i |= xx;��ͺϲ��ˡ����i���Ǵ��˶Է�16λ������
		return (a &0xff)|( ((b&0xff )<< 8));
	}
	
	
	
	/**
	 * ByteתBit
	 */
	public static String byteToBit(byte b) {
		return "" +(byte)((b >> 7) & 0x1) + 
		(byte)((b >> 6) & 0x1) + 
		(byte)((b >> 5) & 0x1) + 
		(byte)((b >> 4) & 0x1) + 
		(byte)((b >> 3) & 0x1) + 
		(byte)((b >> 2) & 0x1) + 
		(byte)((b >> 1) & 0x1) + 
		(byte)((b >> 0) & 0x1);
	}

	/**
	 * BitתByte
	 */
	public static byte BitToByte(String byteStr) {
		int re, len;
		if (null == byteStr) {
			return 0;
		}
		len = byteStr.length();
		if (len != 4 && len != 8) {
			return 0;
		}
		if (len == 8) {// 8 bit����
			if (byteStr.charAt(0) == '0') {// ����
				re = Integer.parseInt(byteStr, 2);
			} else {// ����
				re = Integer.parseInt(byteStr, 2) - 256;
			}
		} else {//4 bit����
			re = Integer.parseInt(byteStr, 2);
		}
		return (byte) re;
	}
	
	
	public static String AsciiStringToString(String content){
		String result = "";
		int length = content.length() / 2;
		for(int i = 0; i < length ;i++){
			String c = content.substring(i*2, i*2+2);
			int a =hexStringToAlgorism(c);
			char b = (char) a;
			String d = String.valueOf(b);
			result+=d;
		}
		return result;
	}
	
	
	
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }
    
    
    public static String hexStringToBinary(String hex) {
        hex = hex.toUpperCase();
        String result = "";
        int max = hex.length();
        for (int i = 0; i < max; i++) {
            char c = hex.charAt(i);
            switch (c) {
            case '0':
                result += "0000";
                break;
            case '1':
                result += "0001";
                break;
            case '2':
                result += "0010";
                break;
            case '3':
                result += "0011";
                break;
            case '4':
                result += "0100";
                break;
            case '5':
                result += "0101";
                break;
            case '6':
                result += "0110";
                break;
            case '7':
                result += "0111";
                break;
            case '8':
                result += "1000";
                break;
            case '9':
                result += "1001";
                break;
            case 'A':
                result += "1010";
                break;
            case 'B':
                result += "1011";
                break;
            case 'C':
                result += "1100";
                break;
            case 'D':
                result += "1101";
                break;
            case 'E':
                result += "1110";
                break;
            case 'F':
                result += "1111";
                break;
            }
        }
        return result;
    }
}
