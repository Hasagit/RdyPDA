package com.rdypda.presenter;

import android.content.Context;
import android.os.Build;
import android.posapi.PrintQueue;
import android.util.Log;

import com.rdypda.R;
import com.rdypda.util.PrintUtil;
import com.rdypda.view.viewinterface.ILlddrMsgView;

/**
 * Created by DengJf on 2017/12/26.
 */

public class LlddrMsgPresenter extends BasePresenter{
    private ILlddrMsgView view;
    private PrintUtil printUtil;

    public LlddrMsgPresenter( Context context,ILlddrMsgView view) {
        super(context);
        this.view = view;
        this.context = context;
        if (Build.MODEL.equals(context.getResources().getString(R.string.print_scan_model))){
            printUtil=new PrintUtil(context);
            printUtil.initPrintQueue();
        }
    }

    public void printEven(){
        /*if (!Build.MODEL.equals(context.getResources().getString(R.string.print_scan_model))){
            view.showMessage(context.getResources().getString(R.string.print_error_model));
            return;
        }
        //printUtil.printBarcode("18819437873");
        printUtil.printText("   物料编号：10011\n");
        printUtil.printText("   物料名称：鸡毛蒜皮\n");
        printUtil.printText("   批次：250\n");
        printUtil.printQRcode("物料编号>10011*物料名称>鸡毛蒜皮*批次>250");
        printUtil.printStart(new PrintQueue.OnPrintListener() {
            @Override
            public void onFailed(int i) {
                Log.e("print","failed");
            }

            @Override
            public void onFinish() {
                Log.e("print","finish");
            }

            @Override
            public void onGetState(int i) {
                Log.e("print","GetState   "+i);
            }

            @Override
            public void onPrinterSetting(int i) {
                Log.e("print","PrinterSetting   "+i);
            }
        });*/
        if (preferenUtil.getString("blueToothAddress").equals("")){
            view.showBlueToothAddressDialog();
        }


    }

    public void closePrint(){
        if (printUtil!=null){
            printUtil.closePrint();
        }
    }
}
