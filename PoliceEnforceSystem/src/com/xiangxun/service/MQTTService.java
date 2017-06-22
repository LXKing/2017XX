package com.xiangxun.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttPersistence;
import com.ibm.mqtt.MqttPersistenceException;
import com.ibm.mqtt.MqttSimpleCallback;
import com.xiangxun.activity.R;
import com.xiangxun.activity.mine.NewMessageCenter;
import com.xiangxun.activity.warn.WarnListActivity;
import com.xiangxun.app.XiangXunApplication;
import com.xiangxun.bean.LoginInfo;
import com.xiangxun.bean.MDate;
import com.xiangxun.bean.SystemMessage;
import com.xiangxun.bean.WarnData;
import com.xiangxun.bean.WarnMessage;
import com.xiangxun.cfg.SystemCfg;
import com.xiangxun.db.DBManager;
import com.xiangxun.util.Logger;
import com.xiangxun.util.MessageTypeParaser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/* 
 * PushService that does all of the work.
 * Most of the logic is borrowed from KeepAliveService.
 * http://code.google.com/p/android-random/source/browse/trunk/TestKeepAlive/src/org/devtcg/demo/keepalive/KeepAliveService.java?r=219
 */
public class MQTTService extends Service {
    // this is the log tag
    public static final String TAG = "MQTTService";

    // the IP address, where your MQTT broker is running.
    private static String MQTT_HOST = "192.168.10.97";
    // the port at which the broker is running.
    private static int MQTT_BROKER_PORT_NUM = 1883;
    // Let's not use the MQTT persistence.
    private static MqttPersistence MQTT_PERSISTENCE = null;
    // We don't need to remember any state between the connections, so we use a
    // clean start.
    private static boolean MQTT_CLEAN_START = true;
    // Let's set the internal keep alive for MQTT to 15 mins. I haven't tested
    // this value much. It could probably be increased.
    private static short MQTT_KEEP_ALIVE = 45;
    // Set quality of services to 0 (at most once delivery), since we don't want
    // push notifications
    // arrive more than once. However, this means that some messages might get
    // lost (delivery is not guaranteed)
    private static int[] MQTT_QUALITIES_OF_SERVICE = {2};
    private static int MQTT_QUALITY_OF_SERVICE = 2;
    // The broker should not retain any messages.
    private static boolean MQTT_RETAINED_PUBLISH = false;

    // MQTT client ID, which is given the broker. In this example, I also use
    // this for the topic header.
    // You can use this to run push notifications for multiple apps with one
    // MQTT broker.
    public static String MQTT_CLIENT_ID = "XiangXun";

    // These are the actions for the service (name are descriptive enough)
    private static final String ACTION_START = MQTT_CLIENT_ID + ".START";
    private static final String ACTION_STOP = MQTT_CLIENT_ID + ".STOP";
    private static final String ACTION_KEEPALIVE = MQTT_CLIENT_ID + ".KEEP_ALIVE";
    private static final String ACTION_RECONNECT = MQTT_CLIENT_ID + ".RECONNECT";

    // Connectivity manager to determining, when the phone loses connection
    private ConnectivityManager mConnMan;
    // Notification manager to displaying arrived push notifications
    private NotificationManager mNotifMan;

    // Whether or not the service has been started.
    private static boolean mStarted;

    // This the application level keep-alive interval, that is used by the
    // AlarmManager
    // to keep the connection active, even when the device goes to sleep.
    private static final long KEEP_ALIVE_INTERVAL = 1000 * 45;

    // Retry intervals, when the connection is lost.
    private static final long INITIAL_RETRY_INTERVAL = 1000 * 10;
    private static final long MAXIMUM_RETRY_INTERVAL = 1000 * 60 * 30;

    // Preferences instance
    private SharedPreferences mPrefs;
    // We store in the preferences, whether or not the service has been started
    public static final String PREF_STARTED = "isStarted";
    // We also store the deviceID (target)
    public static final String PREF_DEVICE_ID = "deviceID";
    // We store the last retry interval
    public static final String PREF_RETRY = "retryInterval";

    // Notification title
    public static String NOTIF_TITLE = "Tokudu";
    // This is the instance of an MQTT connection.
    private MQTTConnection mConnection;
    private long mStartTime;
    private Handler mHandler;

    private String mDevId;

    // Static method to start the service
    public static void actionStart(Context ctx) {
        if (mStarted == true) {
            Intent i = new Intent(ctx, MQTTService.class);
            i.setAction(ACTION_STOP);
            ctx.startService(i);
            mStarted = false;
        }
        Intent i = new Intent(ctx, MQTTService.class);
        i.setAction(ACTION_START);
        ctx.startService(i);
    }

    // Static method to stop the service
    public static void actionStop(Context ctx) {
        Intent i = new Intent(ctx, MQTTService.class);
        i.setAction(ACTION_STOP);
        ctx.startService(i);
    }

    // Static method to send a keep alive message
    public static void actionPing(Context ctx) {
        Intent i = new Intent(ctx, MQTTService.class);
        i.setAction(ACTION_KEEPALIVE);
        ctx.startService(i);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate() {
        super.onCreate();

        log("Creating service");
        mStartTime = System.currentTimeMillis();
        mHandler = new Handler();
        MQTT_HOST = SystemCfg.getServerIP(this);

        // Get instances of preferences, connectivity manager and notification
        // manager
        mPrefs = getSharedPreferences(TAG, MODE_PRIVATE);
        mConnMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        mNotifMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mDevId = "xiangxun_message_shf";
        /*
		 * If our process was reaped by the system for any reason we need to
		 * restore our state with merely a call to onCreate. We record the last
		 * "started" value and restore it here if necessary.
		 */
        handleCrashedService();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // do something when the message is arrived
            }
        };

    }

    // This method does any necessary clean-up need in case the server has been
    // destroyed by the system
    // and then restarted
    private void handleCrashedService() {
        if (wasStarted() == true) {
            log("Handling crashed service...");
            // stop the keep alives
            stopKeepAlives();
            // Do a clean start
            start();
        }
    }

    @Override
    public void onDestroy() {
        log("Service destroyed (started=" + mStarted + ")");

        // Stop the services, if it has been started
        if (mStarted == true) {
            XiangXunApplication.getInstance().getThreadPool().execute(new Runnable() {

                @Override
                public void run() {
                    stop();
                    setStarted(false);
                }
            });

        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        log("Service started with intent=" + intent);
        if (intent == null)
            return;
        // Do an appropriate action based on the intent.
        if (intent.getAction().equals(ACTION_STOP) == true) {
            stop();
            // stopSelf();
        } else if (intent.getAction().equals(ACTION_START) == true) {
            start();
        } else if (intent.getAction().equals(ACTION_KEEPALIVE) == true) {
            keepAlive();
        } else if (intent.getAction().equals(ACTION_RECONNECT) == true) {
            if (isNetworkAvailable()) {
                reconnectIfNecessary();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("Service started with intent=" + intent);
        if (intent != null) {
            // Do an appropriate action based on the intent.
            if (intent.getAction().equals(ACTION_STOP) == true) {
                XiangXunApplication.getInstance().getThreadPool().execute(new Runnable() {

                    @Override
                    public void run() {
                        stop();
						/*
						 * mHandler.post(new Runnable() {
						 * 
						 * @Override public void run() {
						 * MQTTService.this.stopSelf(); } });
						 */
                    }
                });

            } else if (intent.getAction().equals(ACTION_START) == true) {
                XiangXunApplication.getInstance().getThreadPool().execute(new Runnable() {

                    @Override
                    public void run() {
                        start();
                    }
                });

            } else if (intent.getAction().equals(ACTION_KEEPALIVE) == true) {
                keepAlive();
            } else if (intent.getAction().equals(ACTION_RECONNECT) == true) {
                if (isNetworkAvailable()) {
                    XiangXunApplication.getInstance().getThreadPool().execute(new Runnable() {

                        @Override
                        public void run() {
                            reconnectIfNecessary();
                        }
                    });
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // log helper function
    private void log(String message) {
        log(message, null);
    }

    private void log(String message, Throwable e) {
        if (e != null) {
            Logger.e(message, e);
        } else {
            Logger.i(message);
        }

    }

    // Reads whether or not the service has been started from the preferences
    private boolean wasStarted() {
        return mPrefs.getBoolean(PREF_STARTED, false);
    }

    // Sets whether or not the services has been started in the preferences.
    private void setStarted(boolean started) {
        mPrefs.edit().putBoolean(PREF_STARTED, started).commit();
        mStarted = started;
    }

    private synchronized void start() {
        log("Starting service...");

        // Do nothing, if the service is already running.
        if (mStarted == true) {
            Log.w(TAG, "Attempt to start connection that is already active");
            return;
        }

        // Establish an MQTT connection
        new Thread(new Runnable() {

            @Override
            public void run() {
                connect();
            }
        }).start();
        // Register a connectivity listener
        registerReceiver(mConnectivityChanged, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private synchronized void stop() {
        // Do nothing, if the service is not running.
        if (mStarted == false) {
            Log.w(TAG, "Attempt to stop connection not active.");
            return;
        }

        // Save stopped state in the preferences
        setStarted(false);

        // Remove the connectivity receiver
        if (mConnectivityChanged != null) {
            try {
                unregisterReceiver(mConnectivityChanged);
            } catch (IllegalArgumentException iae) {
            }
            mConnectivityChanged = null;
        }
        // Any existing reconnect timers should be removed, since we explicitly
        // stopping the service.
        cancelReconnect();

        // Destroy the MQTT connection if there is one
        if (mConnection != null) {
            mConnection.disconnect();
            mConnection = null;
        }
    }

    //
    private synchronized void connect() {
        log("Connecting...");
        // fetch the device ID from the preferences.

        // Create a new connection only if the device id is not NULL
        if (mDevId == null) {
            log("Device ID not found.");
        } else {
            try {
                mConnection = new MQTTConnection(MQTT_HOST, mDevId);
            } catch (MqttException e) {
                // Schedule a reconnect, if we failed to connect
                log("MqttException: " + (e.getMessage() != null ? e.getMessage() : "NULL"));
                System.out.println();
                e.printStackTrace();
                if (isNetworkAvailable()) {
                    scheduleReconnect(mStartTime);
                }
            }
            setStarted(true);
        }
    }

    private synchronized void keepAlive() {
        try {
            // Send a keep alive, if there is a connection.
            if (mStarted == true && mConnection != null) {
                mConnection.sendKeepAlive();
            }
        } catch (MqttException e) {
            log("MqttException: " + (e.getMessage() != null ? e.getMessage() : "NULL"), e);

            mConnection.disconnect();
            mConnection = null;
            cancelReconnect();
        }
    }

    // Schedule application level keep-alives using the AlarmManager
    private void startKeepAlives() {
        Intent i = new Intent();
        i.setClass(this, MQTTService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + KEEP_ALIVE_INTERVAL, KEEP_ALIVE_INTERVAL, pi);
    }

    // Remove all scheduled keep alives
    private void stopKeepAlives() {
        Intent i = new Intent();
        i.setClass(this, MQTTService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.cancel(pi);
    }

    // We schedule a reconnect based on the starttime of the service
    public void scheduleReconnect(long startTime) {
        // the last keep-alive interval
        long interval = mPrefs.getLong(PREF_RETRY, INITIAL_RETRY_INTERVAL);

        // Calculate the elapsed time since the start
        long now = System.currentTimeMillis();
        long elapsed = now - startTime;

        // Set an appropriate interval based on the elapsed time since start
        if (elapsed < interval) {
            interval = Math.min(interval * 4, MAXIMUM_RETRY_INTERVAL);
        } else {
            interval = INITIAL_RETRY_INTERVAL;
        }

        log("Rescheduling connection in " + interval + "ms.");

        // Save the new internval
        mPrefs.edit().putLong(PREF_RETRY, interval).commit();

        // Schedule a reconnect using the alarm manager.
        Intent i = new Intent();
        i.setClass(this, MQTTService.class);
        i.setAction(ACTION_RECONNECT);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, now + interval, pi);
    }

    // Remove the scheduled reconnect
    public void cancelReconnect() {
        Intent i = new Intent();
        i.setClass(this, MQTTService.class);
        i.setAction(ACTION_RECONNECT);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.cancel(pi);
    }

    private synchronized void reconnectIfNecessary() {
        if (mStarted == true && mConnection == null) {
            log("Reconnecting...");
            connect();

        }
    }

    // This receiver listeners for network changes and updates the MQTT
    // connection
    // accordingly
    private BroadcastReceiver mConnectivityChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get network info
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            // Is there connectivity?
            boolean hasConnectivity = (info != null && info.isConnected()) ? true : false;

            log("Connectivity changed: connected=" + hasConnectivity);

            if (hasConnectivity) {
                XiangXunApplication.getInstance().getThreadPool().submit(new Runnable() {

                    @Override
                    public void run() {
                        reconnectIfNecessary();
                    }
                });

            } else if (mConnection != null) {
                // if there no connectivity, make sure MQTT connection is
                // destroyed
                mConnection.disconnect();
                cancelReconnect();
                mConnection = null;
            }
        }
    };

    // Check if we are online
    private boolean isNetworkAvailable() {
        NetworkInfo info = mConnMan.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        return info.isConnected();
    }

    // This inner class is a wrapper on top of MQTT client.
    private class MQTTConnection implements MqttSimpleCallback {
        IMqttClient mqttClient = null;

        // Creates a new connection given the broker address and initial topic
        public MQTTConnection(String brokerHostName, String initTopic) throws MqttException {
            // Create connection spec
            String mqttConnSpec = "tcp://" + brokerHostName + ":" + MQTT_BROKER_PORT_NUM;
            // Create the client and connect
            mqttClient = MqttClient.createMqttClient(mqttConnSpec, MQTT_PERSISTENCE);
            String clientID = XiangXunApplication.getInstance().getUserName();// MQTT_CLIENT_ID
            mqttClient.connect(clientID, MQTT_CLEAN_START, MQTT_KEEP_ALIVE);

            // register this client app has being able to receive messages
            mqttClient.registerSimpleHandler(this);

            // Subscribe to an initial topic, which is combination of client ID
            // and device ID.
            initTopic = "xiangxun_" + XiangXunApplication.getInstance().getDevId();// MQTT_CLIENT_ID
            subscribeToTopic(initTopic);
            log("Connection established to " + brokerHostName + " on topic " + initTopic);
            // Save start time
            mStartTime = System.currentTimeMillis();
            // Star the keep-alives
            startKeepAlives();
        }

        // Disconnect
        public void disconnect() {
            stopKeepAlives();
            XiangXunApplication.getInstance().getThreadPool().execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        mqttClient.disconnect();
                    } catch (MqttPersistenceException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        /*
         * Send a request to the message broker to be sent messages published
         * with the specified topic name. Wildcards are allowed.
         */
        private void subscribeToTopic(String topicName) throws MqttException {

            if ((mqttClient == null) || (mqttClient.isConnected() == false)) {
                // quick sanity check - don't try and subscribe if we don't have
                // a connection
                log("Connection error" + "No connection");
            } else {
                String[] topics = {topicName};
                mqttClient.subscribe(topics, MQTT_QUALITIES_OF_SERVICE);
            }
        }

        /*
         * Sends a message to the message broker, requesting that it be
         * published to the specified topic.
         */
        private void publishToTopic(String topicName, String message) throws MqttException {
            if ((mqttClient == null) || (mqttClient.isConnected() == false)) {
                // quick sanity check - don't try and publish if we don't have
                // a connection
                log("No connection to public to");
            } else {
                mqttClient.publish(topicName, message.getBytes(), MQTT_QUALITY_OF_SERVICE, MQTT_RETAINED_PUBLISH);
            }
        }

        /*
         * Called if the application loses it's connection to the message
         * broker.
         */
        public void connectionLost() throws Exception {
            log("Loss of connection" + " connection downed");
            stopKeepAlives();
            // null itself
            mConnection = null;
            if (isNetworkAvailable() == true) {
                XiangXunApplication.getInstance().getThreadPool().execute(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        reconnectIfNecessary();
                    }
                });

            }
        }

        /*
         * Called when we receive a message from the message broker.
         */
        public void publishArrived(String topicName, byte[] payload, int qos, boolean retained) {
            Intent intent;

            final String s = new String(payload);
            SystemMessage sm = new SystemMessage();
            sm.datetime = MDate.getDate();
            sm.isread = 0;
            sm.msgtype = Integer.parseInt(s.toString().split("_")[0]);
            sm.isSigned = 1;
            sm.isAck = 1;
            sm.text = s.toString().split("_")[1];
            if (6 == sm.msgtype) {
                if (!SystemCfg.getIsWarn(MQTTService.this))
                    return;

                WarnData wranData = XiangXunApplication.getGson().fromJson(sm.text, WarnData.class);
//				String departmentCode = SystemCfg.getDepartmentCode(MQTTService.this);
//				if (wranData.getQsbm() != null) {
//					if (!wranData.getQsbm().equals(departmentCode))
//						return;
//				}
                String warnCross = SystemCfg.getWarnCross(MQTTService.this);
                if (!warnCross.contains(wranData.getKkmc()))
                    return;
                String warnType = SystemCfg.getWarnTypeCode(MQTTService.this);
                if (!warnType.contains(wranData.getBklx()))
                    return;
                String warnDirect = SystemCfg.getWarnDirectCode(MQTTService.this);
                if (!warnDirect.contains(wranData.getFxlx()))
                    return;

                int count = DBManager.getInstance().QueryWarnMessageCount(wranData.getYjxh());
                if (count > 0)
                    return;

                wranData.setIsOk(-1);
                wranData.setIsDo(-1);
                wranData.setIsAck(-1);
                wranData.setIsUpFile(0);

                WarnMessage warnMessage = new WarnMessage();
                warnMessage.yjxh = wranData.getYjxh();
                warnMessage.datetime = wranData.getYjsj();
                warnMessage.dataInfo = XiangXunApplication.getGson().toJson(wranData);
                DBManager.getInstance().add(warnMessage);
                sm.text = wranData.getHphm() + "|" + DBManager.getInstance().getNameByTypeAndCodeFromDic("BLACKTYPE_CODE", wranData.getBklx());
                intent = new Intent(MQTTService.this, WarnListActivity.class);
            } else {
                DBManager.getInstance().add(sm);

                //Intent intent = new Intent(MQTTService.this, MessageCenterActivity.class);
                intent = new Intent(MQTTService.this, NewMessageCenter.class);
            }

            // Show a notification
            final NotificationManager notificationManager = (NotificationManager) MQTTService.this.getSystemService(Context.NOTIFICATION_SERVICE);

            final Notification notification = new Notification(R.drawable.app_icon, MQTTService.this.getResources().getString(R.string.app_name), System.currentTimeMillis());

            // Hide the notification after its selected
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            switch (sm.msgtype) {
                case 1:
                    LoginInfo.messageType = 0;
                    break;
                case 2:
                    LoginInfo.messageType = 1;
                    break;
                case 3:
                    LoginInfo.messageType = 2;
                    break;
                case 99:
                    LoginInfo.messageType = 3;
                    break;
                default:
                    break;
            }

            final PendingIntent activity = PendingIntent.getActivity(MQTTService.this, 0, intent, 0);
			/*
			 * if(MessageCenterActivity.isInstanciated()){
			 * MessageCenterActivity.instance().setCurrentPosition(0); }
			 */
            notification.setLatestEventInfo(MQTTService.this, "移动警务执法系统", new String(MessageTypeParaser.paraseMessageType(sm.msgtype) + ":" + sm.text), activity);

            notification.number += 1;
            notification.flags = notification.flags | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_AUTO_CANCEL;

            if (6 == sm.msgtype) {
                Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.warn);
                notification.sound = sound;
            } else
                notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification.vibrate = new long[]{1000, 1000, 1000, 1000, 1000};

            System.out.println("接收到的消息：" + s.toString());

            notificationManager.notify(0, notification);

        }

        public void sendKeepAlive() throws MqttException {
            log("Sending keep alive");
            // publish to a keep-alive topic
            publishToTopic("xiangxun" + SystemCfg.getPoliceCode(XiangXunApplication.getInstance()), ""); //XiangXunApplication.getInstance().getDevId(), "");
        }
    }

    private InputStream String2InputStream(String str) {
        ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
        return stream;
    }

    private int getMsgType(String xml) {
        int res = -1;
        if (xml == null)
            return res;
        else {
            XmlPullParser parser = Xml.newPullParser();
            try {
                parser.setInput(String2InputStream(xml), "UTF-8");
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:

                            break;
                        case XmlPullParser.START_TAG:
                            if (parser.getName().equals("msgtype"))
                                return Integer.parseInt(parser.getAttributeValue("", "value"));
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                        default:
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    private String getMsg(String xml) {
        if (xml == null)
            return null;
        else {
            if (getMsgType(xml) == 3)
                return xml;
            XmlPullParser parser = Xml.newPullParser();
            try {
                parser.setInput(String2InputStream(xml), "UTF-8");
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:

                            break;
                        case XmlPullParser.START_TAG:

                            if (parser.getName().equals("text"))
                                return parser.nextText();
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                        default:
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}