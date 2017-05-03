package com.victon.tpms.base.db;

import android.content.Context;

import com.victon.tpms.base.db.entity.RecordData;
import com.victon.tpms.common.helper.DataHelper;
import com.victon.tpms.common.usb.UsbData;
import com.victon.tpms.common.utils.DigitalTrans;
import com.victon.tpms.common.view.BleData;
import com.victon.tpms.entity.ManageDevice;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * author：Administrator on 2016/10/12 11:14
 * company: xxxx
 * email：1032324589@qq.com
 */
public class DbObervable {
    private DbHelper helper;

    public static DbObervable getInstance(Context context) {
        return new DbObervable(context);
    }

    private DbObervable(Context context) {
        helper = DbHelper.getInstance(context);
    }

    public Observable<Boolean> updateRecord(int deviceId, String name, RecordData data) {
        Observable<Boolean> observable = Observable.just(helper.update(deviceId,name,data));
        toSubscribe(observable, new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

            }
        });
        return observable;
    }
    public Observable<BleData> getReBleData(List<RecordData> datas, final ManageDevice defaultDevice) {
        return Observable.from(datas).map(new Func1<RecordData, BleData>() {
            @Override
            public BleData call(RecordData recordData) {
                byte[] data = DigitalTrans.hex2byte(recordData.getData());
                return DataHelper.getData(recordData.getName(),defaultDevice,data);
            }
        });
    }
    public Observable<BleData> getBleData(final UsbData device, final ManageDevice defaultDevice, final int rssi, final byte[] data) {
        return Observable.create(new Observable.OnSubscribe<BleData>() {
            @Override
            public void call(Subscriber<? super BleData> subscriber) {
                subscriber.onNext(DataHelper.getData(device,defaultDevice));
            }
        });
    }
    private <T> void toSubscribe(Observable<T> o,Subscriber<T> s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
}
