package android.zyapi;

import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.posapi.ByteTools;
import android.posapi.Conversion;
import android.posapi.PosApi;
import android.posapi.PosBroadcastSender;
import android.util.Log;

public class IcEx {
	public static String TAG = IcEx.class.getSimpleName();

	public static final int POS_IC_RESET                	=  53;
	public static final int POS_IC_CLOSE               		 =  54;
	public static final int POS_IC_CMD                 		=  55;
	public static final int POS_MEMORY_CARD_GET_TYPE       =  56;
	public static final int POS_MEMORY_CARD_READ           =  57;
	public static final int POS_MEMORY_CARD_WRITE          =  58;
	public static final int POS_MEMORY_CARD_RESET          =  59;
	public static final int POS_MEMORY_CARD_SELECT         =  60;
	public static final int POS_MEMORY_CARD_AUTH           =  62;
	public static final int POS_MEMORY_CARD_CLOSE          =  63;
	public static final int POS_IC_SELECT                  =  61;
	public static final int POS_MEMORY_CARD_GET_PSC        =  64;
	public static final int POS_MEMORY_CARD_MODIFY_PSC     =  65;
	public static final int POS_IC_GET_CPU_CARD_STATE 	   =  66;
	public static final int POS_IC_CHECK_ICC1_SLOT 	       =  67;

	public static final int COMM_SUCCESS = 1;
	public static final int COMM_FAILED  = 0;


	public Context mContext = null;
	public PosApi mApi = null;
	byte [] buffer = null;
	private int mCurCmdFlag = -1;
	private int mIcExSerial = 4 ;
	private OnIcEventListener mIcEventListener = null;
	private OnMemoryCardEventListener memoryCardEventListener = null;
	
	public interface OnIcEventListener{
		void onIcReset(int state, byte[] atr, int length);
		void onIcApdu(int state, byte[] atr, int length);
		void onIcClose(int state);
		void onCheckICC1Slot(int state);

	}
	
	public interface OnMemoryCardEventListener{
		void onSelect(int state);
		void onAuth(int state);
		void onReset(int state, byte[] atr, int length);
		void onGetType(int state, byte type);
		void onGetPSC(int state, byte[] key, int keyLength);
		void onModifyPSC(int state);
		void onRead(int state, byte[] resp, int length);
		void onWrite(int state);
		void onClose(int state);
	}

	public IcEx(Context context ,PosApi api){
		this.mContext = context;
		this.mApi = api;

	}


	public void setOnIcEventListener(OnIcEventListener listener){
		this.mIcEventListener = listener;
	}


	public OnMemoryCardEventListener getMemoryCardEventListener() {
		return memoryCardEventListener;
	}


	public void setMemoryCardEventListener(OnMemoryCardEventListener memoryCardEventListener) {
		this.memoryCardEventListener = memoryCardEventListener;
	}


	public void init(){
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(PosApi.ACTION_POS_COMM_STATUS);
		mContext.registerReceiver(receiver, mFilter);
		mApi.extendSerialInit(4, 4, 1, 1, 1, 1);
	}

	public void close(){
		if(mContext==null||receiver==null) return;
		mContext.unregisterReceiver(receiver);
	}

	public int selectICC1(){
		mCurCmdFlag = POS_IC_SELECT;
		String cmd ="1B25304D";
		byte[]mCmd =Conversion.HexString2Bytes(cmd);
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}


	/*

	 */
	public int checkICC1Slot(){
		mCurCmdFlag = POS_IC_CHECK_ICC1_SLOT;
		String cmd ="1B252B4D";
		byte[]mCmd =Conversion.HexString2Bytes(cmd);
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}


	public int getCpuCardState(){
		mCurCmdFlag = POS_IC_GET_CPU_CARD_STATE;
		String cmd ="1B25394D00";
		byte[]mCmd =Conversion.HexString2Bytes(cmd);
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}

	public int icReset(){
		mCurCmdFlag = POS_IC_RESET;
		String cmd ="1B25364D";
		byte[]mCmd =Conversion.HexString2Bytes(cmd);
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}

	public int icCmd(byte[]cmd ,int cmdLen){
		mCurCmdFlag = POS_IC_CMD;
		byte[] cbl  = new byte[1];
		StringBuffer sb = new StringBuffer();
		sb.append("1B25384D");
		cbl[0] = (byte)(cmdLen >> 8);
		sb.append(Conversion.Bytes2HexString(cbl));
		cbl[0] = (byte) (cmdLen & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		sb.append(Conversion.Bytes2HexString(cmd));
		Log.v(TAG, "apdu :"+sb.toString());
		//xor
		byte[] temp = Conversion.HexString2Bytes(sb.toString());
		int xor = ByteTools.checkSum(temp, temp.length);
		Log.v(TAG, "xor :"+xor);
		cbl[0] = (byte) (xor & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		byte[]mCmd =Conversion.HexString2Bytes(sb.toString());
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}

	public int icClose () {
		mCurCmdFlag = POS_IC_CLOSE;
		String icClose ="1B25374D";
		byte[]mCmd =Conversion.HexString2Bytes(icClose);
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}

	
	
	

	public int memoryCardSelect(int type){
		mCurCmdFlag = POS_MEMORY_CARD_SELECT;
		StringBuffer sb = new StringBuffer();
		sb.append("1B2540000001");
		switch (type) {
		case 1:
			sb.append("02");
			break;
		case 2:
			sb.append("04");
			break;
		case 3:
			sb.append("0A");
			break;
		case 4:
			sb.append("0B");
			break;
		case 5:
			sb.append("0C");
			break;
		case 6:
			sb.append("0D");
			break;
		case 7:
			sb.append("0E");
			break;
		case 8:
			sb.append("13");
			break;
		case 9:
			sb.append("14");
			break;
		case 10:
			sb.append("55");
			break;

		default:
			sb.append("55");
			break;
		}
		
		
		sb.append(getXor(sb.toString()));
		Log.v(TAG, "ic select :"+sb.toString());
		
		byte[]mCmd =Conversion.HexString2Bytes(sb.toString());
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}

	private String getXor(String s){
		//xor
		byte[] temp = Conversion.HexString2Bytes(s);
		int xor = ByteTools.checkSum(temp, temp.length);
		byte[] cbl  = new byte[1];
		cbl[0] = (byte) (xor & 0xff);
		return Conversion.Bytes2HexString(cbl);
	}

	public int memoryCardReset(){
		mCurCmdFlag = POS_MEMORY_CARD_RESET;
		String cmd ="1B254001";
		byte[]mCmd =Conversion.HexString2Bytes(cmd);
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}


	public int memoryCardGetType(){
		mCurCmdFlag = POS_MEMORY_CARD_GET_TYPE;
		String getType ="1B254002";
		byte[]mCmd =Conversion.HexString2Bytes(getType);
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}
	
	
	/**
	 * Request the reader to get PSC of memory card selected. If successful, return the PSC of
	   this card. It’s for SLE4442 /SLE4428 card only
	   There is 3-byte PSC for SLE4442.
�?		There is 2-byte PSC for SLE4428.
	   @param cardType 1:4442    2:4428  
	 * @return
	 */
	public int memoryCardGetPSC(int cardType){
		mCurCmdFlag = POS_MEMORY_CARD_GET_PSC;
		byte[] cbl  = new byte[1];
		StringBuffer sb = new StringBuffer();
		sb.append("1B254009");
		int cmdLength = 3;
		cbl[0] = (byte)(cmdLength >> 8);
		sb.append(Conversion.Bytes2HexString(cbl));
		cbl[0] = (byte) (cmdLength & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		sb.append("0000");
		//Le
		if(cardType ==1){
			cbl[0] = (byte) (3 & 0xff);
		}else{
			cbl[0] = (byte) (2 & 0xff);
		}
		sb.append(Conversion.Bytes2HexString(cbl));
		//xor
		sb.append(getXor(sb.toString()));
		Log.v(TAG, "memoryCardGetPSC :"+sb.toString());
		byte[]mCmd =Conversion.HexString2Bytes(sb.toString());
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}
	
	
	public int memoryCardModifyPSC(byte[]key,int keylength){
		mCurCmdFlag = POS_MEMORY_CARD_MODIFY_PSC;
		byte[] cbl  = new byte[1];
		StringBuffer sb = new StringBuffer();
		sb.append("1B25400A");
		int cmdLength = 3+keylength;
		cbl[0] = (byte)(cmdLength >> 8);
		sb.append(Conversion.Bytes2HexString(cbl));
		cbl[0] = (byte) (cmdLength & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		sb.append("0000");
		//Ln
		cbl[0] = (byte) (keylength & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		//PSC
		sb.append(Conversion.Bytes2HexString(key));
		//xor
		sb.append(getXor(sb.toString()));
		Log.v(TAG, "memoryCardModifyPSC :"+sb.toString());
		byte[]mCmd =Conversion.HexString2Bytes(sb.toString());
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}

	/**
	 * SLE4442 密码�?3 字节,默认为�?FFFFFF�?SLE4428 密码�?2 字节,默认为�?FFFF�?
	 * @param key
	 * @param keyLength
	 * @return
	 */
	public int memoryCardAuth(byte[]key,int keyLength){
		//1B 25 40 08 HLen LLen 00 00 Ln PSC Edc
		mCurCmdFlag = POS_MEMORY_CARD_AUTH;
		byte[] cbl  = new byte[1];
		StringBuffer sb = new StringBuffer();
		sb.append("1B254008");
		int cmdLength = 3+keyLength;
		cbl[0] = (byte)(cmdLength >> 8);
		sb.append(Conversion.Bytes2HexString(cbl));
		cbl[0] = (byte) (cmdLength & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		sb.append("0000");
		//Ln
		cbl[0] = (byte) (keyLength & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		//PSC
		sb.append(Conversion.Bytes2HexString(key));
		//xor
		sb.append(getXor(sb.toString()));
		Log.v(TAG, "memoryCardAuth :"+sb.toString());
		byte[]mCmd =Conversion.HexString2Bytes(sb.toString());
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}
	
	public int memoryCardRead(int starAddress,int length){
		mCurCmdFlag = POS_MEMORY_CARD_READ;
		Log.d(TAG, "memoryCardRead ---> starAddress :"+starAddress+"  length:"+length);
		byte[] cbl  = new byte[1];
		StringBuffer sb = new StringBuffer();
		sb.append("1B254003");
		cbl[0] = (byte)(3 >> 8);
		sb.append(Conversion.Bytes2HexString(cbl));
		cbl[0] = (byte) (3 & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		cbl[0] = (byte)(starAddress >> 8);
		sb.append(Conversion.Bytes2HexString(cbl));
		cbl[0] = (byte) (starAddress & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		cbl[0] = (byte)(length & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		//xor
		sb.append(getXor(sb.toString()));
		Log.d(TAG, "memoryCardRead :"+sb.toString());
		byte[]mCmd =Conversion.HexString2Bytes(sb.toString());
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}

	public int memoryCardWrite(int offSet,byte[]data,int length){
		//1B 25 40 04 HLen LLen AdrH AdrL Ln C-APDU Edc
		Log.d(TAG, "memoryCardWrite ---> starAddress :"+offSet+"  length:"+length);
		mCurCmdFlag = POS_MEMORY_CARD_WRITE;
		byte[] cbl  = new byte[1];
		StringBuffer sb = new StringBuffer();
		sb.append("1B254004");
		//HLen LLen
		int cmdLen = 3+length;
		cbl[0] = (byte)(cmdLen >> 8);
		sb.append(Conversion.Bytes2HexString(cbl));
		cbl[0] = (byte) (cmdLen & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		//AdrH AdrL
		cbl[0] = (byte)(offSet >> 8);
		sb.append(Conversion.Bytes2HexString(cbl));
		cbl[0] = (byte) (offSet & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		//Ln
		cbl[0] = (byte)(length & 0xff);
		sb.append(Conversion.Bytes2HexString(cbl));
		//C-APDU
		sb.append(Conversion.Bytes2HexString(data));
		//XOR
		sb.append(getXor(sb.toString()));
		
		Log.d(TAG, "memoryCardWrite :"+sb.toString());
		byte[]mCmd =Conversion.HexString2Bytes(sb.toString());
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}


	public int memoryCardClose(){
		mCurCmdFlag = POS_MEMORY_CARD_CLOSE;
		String cmd ="1B254005";
		byte[]mCmd =Conversion.HexString2Bytes(cmd);
		return mApi.extendSerialCmd(mIcExSerial, mCmd, mCmd.length);
	}


	BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action  = intent.getAction();

			if(action.equalsIgnoreCase(PosApi.ACTION_POS_COMM_STATUS)){
				int cmdFlag =intent.getIntExtra(PosApi.KEY_CMD_FLAG, -1);
				int status	=intent.getIntExtra(PosApi.KEY_CMD_STATUS , -1); 
				int bufferLen = intent.getIntExtra(PosApi.KEY_CMD_DATA_LENGTH, 0);
				buffer =intent.getByteArrayExtra(PosApi.KEY_CMD_DATA_BUFFER);

				switch(cmdFlag){
				case PosApi.POS_EXPAND_SERIAL4:
					Log.v(TAG, "serial4 recv:"+Conversion.Bytes2HexString(buffer));
					if(buffer == null ||bufferLen < 2) return;
					switch (mCurCmdFlag) {
					case POS_IC_RESET:
						if(buffer[1]==0x00){
							byte[] rapdu = getR_APDU(buffer);
							if(mIcEventListener!=null){
								mIcEventListener.onIcReset(COMM_SUCCESS, rapdu,bufferLen);
							}
						}else{
							if(mIcEventListener!=null){
								mIcEventListener.onIcReset(COMM_FAILED, null,0);
							}
						}
						break;
					case POS_IC_CLOSE:
						if(buffer[1]==0x00){
							if(mIcEventListener!=null){
								mIcEventListener.onIcClose(COMM_SUCCESS);
							}
						}else{
							if(mIcEventListener!=null){
								mIcEventListener.onIcClose(COMM_SUCCESS);
							}
						}
						break;
					case POS_IC_CHECK_ICC1_SLOT:
						if(buffer[1]==0x00){
							if(mIcEventListener!=null){

								mIcEventListener.onCheckICC1Slot(1);
							}
						}else{
							if(mIcEventListener!=null){
								mIcEventListener.onCheckICC1Slot(0);
							}
						}
						break;
						case POS_IC_CMD:
							if(buffer[1]==0x00){
								if(mIcEventListener!=null){
									byte[] rapdu = getR_APDU(buffer);
									mIcEventListener.onIcApdu(COMM_SUCCESS, rapdu,bufferLen);
								}
							}else{
								if(mIcEventListener!=null){
									mIcEventListener.onIcApdu(COMM_FAILED, null,0);
								}
							}
							break;
					case POS_MEMORY_CARD_GET_TYPE:
						if(buffer[1]==0x00){
							
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onGetType(COMM_SUCCESS,buffer[4]);
							}
						}else{
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onGetType(COMM_FAILED,(byte)0x00);
							}
						}
						
						break;
						
					case POS_MEMORY_CARD_CLOSE:
						if(buffer[1]==0x00){
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onClose(COMM_SUCCESS);
							}
						}else{
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onClose(COMM_FAILED);
							}
						}
						break;
					case POS_MEMORY_CARD_MODIFY_PSC:
						if(buffer[1]==0x00){
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onModifyPSC(COMM_SUCCESS);
							}
						}else{
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onModifyPSC(COMM_FAILED);
							}
						}
						break;
					case POS_MEMORY_CARD_GET_PSC:
						if(buffer[1]==0x00){
							try {
								byte[] rapdu = getR_APDU(buffer);
								int  atrLen = (buffer[3] &0xff)|( ((buffer[2]&0xff )<< 8));
								if(memoryCardEventListener!=null){
									memoryCardEventListener.onGetPSC(COMM_SUCCESS,rapdu,atrLen);
								}
							} catch (Exception e) {
								// TODO: handle exception
								if(memoryCardEventListener!=null){
									memoryCardEventListener.onGetPSC(COMM_FAILED,null,0);
								}
							}
						}else{
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onGetPSC(COMM_FAILED,null,0);
							}
						}
						break;
						
					case POS_MEMORY_CARD_AUTH:
						if(buffer[1]==0x00){
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onAuth(COMM_SUCCESS);
							}
						}else{
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onAuth(COMM_FAILED);
							}
						}
						break;
					case POS_MEMORY_CARD_SELECT:
						if(buffer[1]==0x00){
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onSelect(COMM_SUCCESS);
							}
						}else{
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onSelect(COMM_FAILED);
							}
						}
						break;
					case POS_MEMORY_CARD_RESET:
						if(buffer[1]==0x00){
							
							try {
								byte[] rapdu = getR_APDU(buffer);
								int  atrLen = (buffer[3] &0xff)|( ((buffer[2]&0xff )<< 8));
								if(memoryCardEventListener!=null){
									memoryCardEventListener.onReset(COMM_SUCCESS, rapdu, atrLen);
								}
							} catch (Exception e) {
								// TODO: handle exception
								if(memoryCardEventListener!=null){
									memoryCardEventListener.onReset(COMM_SUCCESS, null, 0);
								}
							}
							
							
							
						}else{
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onReset(COMM_FAILED, null, 0);
							}
						}
						break;
					case POS_MEMORY_CARD_READ:
						if(buffer[1]==0x00){
							byte[] rapdu = getR_APDU(buffer);
							int  atrLen = (buffer[3] &0xff)|( ((buffer[2]&0xff )<< 8));
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onRead(COMM_SUCCESS, rapdu, atrLen);
							}
							
						}else{
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onRead(COMM_FAILED, null, 0);
							}
						}
						break;
					case POS_MEMORY_CARD_WRITE:
						if(buffer[1]==0x00){
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onWrite(COMM_SUCCESS);
							}
						}else{
							if(memoryCardEventListener!=null){
								memoryCardEventListener.onWrite(COMM_FAILED);
							}
						}
						break;
					default:
						break;
					}
					break;

				}
				buffer = null;
			}

		}

	};


	private byte[] getR_APDU(byte [] recv){
		if(recv == null) return  null;
		int  atrLen = (buffer[3] &0xff)|( ((buffer[2]&0xff )<< 8));
		Log.v(TAG, "atrlen:"+atrLen);
		byte[] mAtr = new byte[atrLen];
		System.arraycopy(buffer, 4, mAtr, 0, atrLen);
		return mAtr;
	}




	public static int disposeMcGetType(Context ctx,int cmdFlag, int status ,byte[] buffer ,int bufferLen){
		if(buffer==null) return -1;
		Log.v("hello", "McGetType:"+Conversion.Bytes2HexString(buffer));
		if(buffer[2]==0x00){

			PosBroadcastSender.sendPosCommStatus(ctx, PosApi.POS_Memory_CARD_GET_TYPE, PosApi.COMM_STATUS_SUCCESS, buffer, bufferLen);
		}else{
			PosBroadcastSender.sendPosCommStatus(ctx, PosApi.POS_Memory_CARD_GET_TYPE, PosApi.COMM_STATUS_FAILED, buffer, bufferLen);
		}
		return 0;
	}

}
