package com.hitoncloud.near.community;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.hitoncloud.near.Activity.OrderActivity;
import com.hitoncloud.near.FullImageActivity;
import com.hitoncloud.near.R;
import com.hitoncloud.near.adapter.ChatAdapter;
import com.hitoncloud.near.adapter.CommonFragmentPagerAdapter;
import com.hitoncloud.near.application.MyApplication;
import com.hitoncloud.near.enity.FullImageInfo;
import com.hitoncloud.near.enity.MessageInfo;
import com.hitoncloud.near.utils.Constants;
import com.hitoncloud.near.utils.GlobalOnItemClickManagerUtils;
import com.hitoncloud.near.utils.MediaManager;
import com.hitoncloud.near.widget.EmotionInputDetector;
import com.hitoncloud.near.widget.NoScrollViewPager;
import com.hitoncloud.near.widget.StateButton;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMSoundElem;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 功能:聊天的基本业务逻辑
 */
public class ChatActivity extends AppCompatActivity {

    @Bind(R.id.chat_list)
    EasyRecyclerView chatList;
    @Bind(R.id.emotion_voice)
    ImageView emotionVoice;
    @Bind(R.id.edit_text)
    EditText editText;
    @Bind(R.id.voice_text)
    TextView voiceText;
    @Bind(R.id.emotion_button)
    ImageView emotionButton;
    @Bind(R.id.emotion_add)
    ImageView emotionAdd;
    @Bind(R.id.emotion_send)
    StateButton emotionSend;
    @Bind(R.id.viewpager)
    NoScrollViewPager viewpager;
    @Bind(R.id.emotion_layout)
    RelativeLayout emotionLayout;

    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;

    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private List<MessageInfo> messageInfos;
    //录音相关
    int animationRes = 0;
    int res = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;

    String uuid;  //消息唯一确定值
    String cacheMsg = Environment.getExternalStorageDirectory() + "/com.hitoncloud.sliderimg";
    
    String targetid;//目标对象ID
    String targetnickname;//聊天对象昵称
    String peer;

    Toolbar toolbar;

    TIMMessageListener tml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.theme));//修改状态栏颜色
        getIntentDate();
        initView();
        peer = targetid;  //获取与用户 "sample_user_1" 的会话


        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initWidget();

        File tmpDir = new File(cacheMsg);//消息
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }

        //接收消息

        tml = new TIMMessageListener() {
        @Override
        public boolean onNewMessages(List<TIMMessage> list) {
            for (TIMMessage msg : list) {
                for (int i = 0; i < msg.getElementCount(); ++i) {
                    TIMElem elem = msg.getElement(i);
                    //获取当前元素的类型
                    TIMElemType elemType = elem.getType();
                    Log.e("debug10", String.valueOf(elemType));
                    if (elemType == TIMElemType.Text) {
                        final String receivemsg = ((TIMTextElem) elem).getText();
                        MessageInfo message = new MessageInfo();
                        message.setContent(receivemsg);
                        message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                        message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
                        messageInfos.add(message);
                        chatAdapter.add(message);
                        chatList.scrollToPosition(chatAdapter.getCount() - 1);

                        //处理文本消息
                    } else if (elemType == TIMElemType.Image) {
                        //处理图片消息
                        //图片元素
                        final TIMImageElem e = (TIMImageElem) elem;

                        for(final TIMImage image : e.getImageList()) {
                            uuid = image.getUuid();
                            image.getImage(cacheMsg+"/"+uuid+".jpg", new TIMCallBack() {
                                @Override
                                public void onError(int code, String desc) {//获取图片失败
                                    Log.e("debug", "getImage failed. code: " + code + " errmsg: " + desc);
                                }

                                @Override
                                public void onSuccess() {//成功，参数为图片数据
                                    MessageInfo message = new MessageInfo();
                                    message.setImageUrl(image.getUrl());
                                    message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                                    message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
                                    messageInfos.add(message);
                                    chatAdapter.add(message);
                                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                                }
                            });
                            break;//只发大图
                        }

                    }else if(elemType == TIMElemType.Sound){
                        //语音消息
                        final TIMSoundElem voice = (TIMSoundElem) elem;
                        uuid = voice.getUuid();
                        voice.getSoundToFile(cacheMsg+"/"+uuid+".wav", new TIMCallBack() {
                            @Override
                            public void onError(int code, String desc) {
                                Log.e("debug", "getSound failed. code: " + code + " errmsg: " + desc);
                            }

                            @Override
                            public void onSuccess() {
                                MessageInfo message = new MessageInfo();
                                message.setFilepath(cacheMsg+"/"+uuid+".wav");
                                message.setVoiceTime(voice.getDuration());
                                message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                                message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
                                messageInfos.add(message);
                                chatAdapter.add(message);
                                chatList.scrollToPosition(chatAdapter.getCount() - 1);

                            }
                        });

                    }//...处理更多消息
                }
            }
            return false;
        }
    };

        TIMManager.getInstance().addMessageListener(tml);


    }


    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_chat);
        toolbar.setTitle(targetnickname);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatActivity.this.finish();
            }
        });
    }

    private void getIntentDate() {
        Intent intent = getIntent();
        targetid = intent.getStringExtra("taskpublisher");
        Log.e("CHAT",targetid);
        targetnickname = intent.getStringExtra("nickname");
        
    }

    private void initWidget() {
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceButton(emotionVoice)
                .bindToVoiceText(voiceText)
                .build();

        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        chatAdapter.addItemClickListener(itemClickListener);
        LoadData();
    }


    /**
     * item点击事件
     */
    private ChatAdapter.onItemClickListener itemClickListener = new ChatAdapter.onItemClickListener() {
        @Override
        public void onHeaderClick(int position) {
            Toast.makeText(ChatActivity.this, "onHeaderClick", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onImageClick(View view, int position) {
            int location[] = new int[2];
            view.getLocationOnScreen(location);
            FullImageInfo fullImageInfo = new FullImageInfo();
            fullImageInfo.setLocationX(location[0]);
            fullImageInfo.setLocationY(location[1]);
            fullImageInfo.setWidth(view.getWidth());
            fullImageInfo.setHeight(view.getHeight());
            fullImageInfo.setImageUrl(messageInfos.get(position).getImageUrl());
            EventBus.getDefault().postSticky(fullImageInfo);
            startActivity(new Intent(getApplicationContext(), FullImageActivity.class));
            overridePendingTransition(0, 0);
        }

        @Override
        public void onVoiceClick(final ImageView imageView, final int position) {
            if (animView != null) {
                animView.setImageResource(res);
                animView = null;
            }
            switch (messageInfos.get(position).getType()) {
                case 1:
                    animationRes = R.drawable.voice_left;
                    res = R.mipmap.icon_voice_left3;
                    break;
                case 2:
                    animationRes = R.drawable.voice_right;
                    res = R.mipmap.icon_voice_right3;
                    break;
            }
            animView = imageView;
            animView.setImageResource(animationRes);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();
            MediaManager.playSound(messageInfos.get(position).getFilepath(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    animView.setImageResource(res);
                }
            });
        }
    };


    /**
     * 构造聊天数据
     */
    private void LoadData() {
        messageInfos = new ArrayList<>();
//        MessageInfo messageInfo = new MessageInfo();
//        messageInfo.setContent("你好，欢迎使用Rance的聊天界面框架");
//        messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
//        messageInfo.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
//        messageInfos.add(messageInfo);
//
//        MessageInfo messageInfo1 = new MessageInfo();
//        messageInfo1.setFilepath("http://www.trueme.net/bb_midi/welcome.wav");
//        messageInfo1.setVoiceTime(3000);
//        messageInfo1.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
//        messageInfo1.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
//        messageInfo1.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
//        messageInfos.add(messageInfo1);
//
//        MessageInfo messageInfo2 = new MessageInfo();
//        messageInfo2.setImageUrl("http://img4.imgtn.bdimg.com/it/u=1800788429,176707229&fm=21&gp=0.jpg");
//        messageInfo2.setType(Constants.CHAT_ITEM_TYPE_LEFT);
//        messageInfo2.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
//        messageInfos.add(messageInfo2);
//
//        MessageInfo messageInfo3 = new MessageInfo();
//        messageInfo3.setContent("[微笑][色][色][色]");
//        messageInfo3.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
//        messageInfo3.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
//        messageInfo3.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
//        messageInfos.add(messageInfo3);
//
//       chatAdapter.addAll(messageInfos);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(final MessageInfo messageInfo) {
        messageInfo.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        messageInfo.setSendState(Constants.CHAT_ITEM_SENDING);
        messageInfos.add(messageInfo);
        chatAdapter.add(messageInfo);
        chatList.scrollToPosition(chatAdapter.getCount() - 1);

        //发送消息
        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //获取单聊会话

        TIMConversation conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                peer);                      //会话对方用户帐号//对方id


        if (messageInfo.getMsgtype() == 1){
            //1为图片
            //添加图片6
            TIMImageElem elem = new TIMImageElem();
            elem.setPath(messageInfo.getImageUrl());
            msg.addElement(elem);

        }else if(messageInfo.getMsgtype() == 2){
            //2为语音
            //添加语音
            TIMSoundElem elem = new TIMSoundElem();
            elem.setPath(messageInfo.getFilepath()); //填写语音文件路径
            elem.setDuration(messageInfo.getVoiceTime());  //填写语音时长
            msg.addElement(elem);

        }else{
            //文字
            // 添加文本内容
            TIMTextElem elem = new TIMTextElem();
            elem.setText(messageInfo.getContent());
            msg.addElement(elem);
        }


        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                Log.e("debugtim", "send message failed. code: " + code + " errmsg: " + desc);
                messageInfo.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e("debugtim", "SendMsg ok");
                messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                chatAdapter.notifyDataSetChanged();
            }
        });



//
//
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
//                chatAdapter.notifyDataSetChanged();
//            }
//        }, 500);

//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                MessageInfo message = new MessageInfo();
//                message.setContent("这是模拟消息回复");
//                message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
//                message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
//                messageInfos.add(message);
//                chatAdapter.add(message);
//                chatList.scrollToPosition(chatAdapter.getCount() - 1);
//            }
//        }, 3000);
    }

    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TIMManager.getInstance().removeMessageListener(tml);
        ButterKnife.unbind(this);
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }
}


