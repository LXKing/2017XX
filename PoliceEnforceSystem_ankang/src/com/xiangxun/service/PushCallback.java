package com.xiangxun.service;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.RingtoneManager;

import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.MessageCenterActivity;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.SystemMessage;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.MessageTypeParaser;


public class PushCallback implements MqttCallback {

    private ContextWrapper context;

    public PushCallback(ContextWrapper context) {

        this.context = context;
    }

    @Override
    public void connectionLost(Throwable cause) {
        //We should reconnect here
    }

    @Override
    public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {

        final NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        final Notification notification = new Notification(R.drawable.app_icon,
                context.getResources().getString(R.string.app_name), System.currentTimeMillis());

        // Hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        SystemMessage sm = new SystemMessage();
        sm.datetime = MDate.getDate();
        sm.isread = 0;
        sm.msgtype = Integer.parseInt(message.toString().split("_")[0]);
        sm.text = message.toString().split("_")[1];
        DBManager.getInstance().add(sm);
        final Intent intent = new Intent(context, MessageCenterActivity.class);
        switch (sm.msgtype) {
		case 1:
			intent.putExtra("pageIndex", 0);
			break;
		case 2:
			intent.putExtra("pageIndex", 1);
			break;
		case 3:
			intent.putExtra("pageIndex", 2);
			break;
		case 99:
			intent.putExtra("pageIndex", 3);
			break;
		default:
			break;
		}
        
        final PendingIntent activity = PendingIntent.getActivity(context, 0, intent, 0);
/*        if(MessageCenterActivity.isInstanciated()){
        	MessageCenterActivity.instance().setCurrentPosition(0);
        }*/
        notification.setLatestEventInfo(context, "移动警务执法系统",
                new String(MessageTypeParaser.paraseMessageType(sm.msgtype) +  ":" + sm.text), activity);
        notification.number += 1;
		notification.flags = notification.flags
				| Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND
				| Notification.FLAG_AUTO_CANCEL;

		notification.sound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notification.vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
        
        System.out.println("接收到的消息："+message.toString());
        
        notificationManager.notify(0, notification);
        
    }

    @Override
    public void deliveryComplete(MqttDeliveryToken token) {
        //We do not need this because we do not publish
    }
}
