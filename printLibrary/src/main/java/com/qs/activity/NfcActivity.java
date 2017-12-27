package com.qs.activity;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.widget.TextView;

import com.qs.demo3506.R;

public class NfcActivity extends Activity {

	private TextView mId;
	private TextView mType;
	private TextView mSector;
	private TextView mBlock;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private TextView mSize;
	private NfcAdapter nfcAdapter = null;
	private PendingIntent mPendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);
		mId = (TextView) findViewById(R.id.id);
		mType = (TextView) findViewById(R.id.type);
		mBlock = (TextView) findViewById(R.id.block);
		mSector = (TextView) findViewById(R.id.sector);  
		mSize = (TextView) findViewById(R.id.size);
		
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		mFilters = new IntentFilter[] { tech };//������

		mTechLists = new String[][] {new String[] { MifareClassic.class.getName() },new String[] { IsoDep.class.getName() } };
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);


	}       
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
					mTechLists);
		}
	}

	@Override   
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if (!intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED))
			return;
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		try{
			mId.setText("ID:  "+bytesToHexString(tagFromIntent.getId()));

			MifareClassic mifare = MifareClassic.get(tagFromIntent);
			int type = mifare.getType();
			String sType = "Mifare Classic";
			switch (type) {
			case MifareClassic.TYPE_CLASSIC:
				sType = "Mifare Classic";    
				break;
			case MifareClassic.TYPE_PLUS:
				sType = "Mifare Classic PLUS";
				break;
			case MifareClassic.TYPE_PRO:
				sType = "Mifare Classic PRO";
				break;
			case MifareClassic.TYPE_UNKNOWN:
			
				sType = "Mifare Classic UNKNOWN";   
				break;
			}  

			mType.setText("Type:  "+sType+"");
			mSector.setText("SectorCount:  "+mifare.getSectorCount()+"");
			mBlock.setText(("BlockCount:  "+mifare.getBlockCount())+"");
			mSize.setText("Size:  "+mifare.getSize()+"");
		}catch(Exception e){
			e.printStackTrace();
		}



	}      
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(this);
		}
	}


	private String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("0x");
		if (src == null || src.length <= 0) {
			return null;      
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			// System.out.println(buffer);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString();
	}
}
