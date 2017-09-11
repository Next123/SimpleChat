package cn.bmob.imdemo.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.BaseActivity;
import cn.bmob.imdemo.base.ImageLoaderFactory;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.ui.widget.BottomMenuDialog;
import cn.bmob.imdemo.ui.widget.LoadDialog;
import cn.bmob.imdemo.util.PhotoUtils;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MyAccountActivity extends BaseActivity {

    @Bind(R.id.img_my_avatar)
    ImageView imgMyAvatar;
    @Bind(R.id.tv_my_username)
    TextView tvMyUsername;
    @Bind(R.id.tv_my_gender)
    TextView tvMyGender;
    @Bind(R.id.tv_my_birthday)
    TextView tvMyBirthday;
    @Bind(R.id.tv_my_email)
    TextView tvMyEmail;
    @Bind(R.id.tv_my_motto)
    TextView tvMyMotto;

    private User user;
    private BottomMenuDialog dialog;
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private PhotoUtils photoUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        ButterKnife.bind(this);
        setTitle("个人账号");
        initView();
        setAvatarChangeListener();
    }


    @Override
    protected void initView() {
        user = getCurrentUser();
        if (user != null) {
            tvMyUsername.setText(user.getUsername());//昵称
            ImageLoaderFactory.getLoader().loadAvator(imgMyAvatar, user.getAvatar(), R.mipmap.head);//头像
            tvMyGender.setText(user.isGender() ? "男" : "女");//性别  默认女
            tvMyBirthday.setText(user.getBirthday() == null ? "未设置" : user.getBirthday());//生日
            tvMyEmail.setText(user.getEmail() == null ? "未设置" : user.getEmail());//邮箱
            tvMyMotto.setText(user.getMotto() == null ? "这个人很懒，什么都没有留下！" : user.getMotto());
        }
    }

    private void setAvatarChangeListener() {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    LoadDialog.show(mContext);
                    final BmobFile bmobFile = new BmobFile(new File(uri.getPath()));
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //上传成功
                                user.setAvatar(bmobFile.getFileUrl());
                                updateUserAvatar();
                            } else {
                                //上传失败
                                LoadDialog.dismiss(mContext);
                                Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }

    //更新用户头像
    private void updateUserAvatar() {
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showUserAvatar();
                    LoadDialog.dismiss(mContext);
                }
            }
        });
    }

    //显示用户头像
    private void showUserAvatar() {
        System.out.println("------------------dasasdadaaa----------------------------------");
        ImageLoaderFactory.getLoader().loadAvator(imgMyAvatar, user.getAvatar(), R.mipmap.head);//头像
    }

    //显示底部dialog
    @TargetApi(23)
    private void showPhotoDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new BottomMenuDialog(mContext);
        dialog.setConfirmListener(new View.OnClickListener() {//拍照
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkPermission = checkSelfPermission(Manifest.permission.CAMERA);
                    //如果未授权
                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                        //应用是否显示获取权限dialog
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                        } else {
                            //如果不显示   自己创建一个dialog
                            new AlertDialog.Builder(mContext)
                                    .setMessage("您需要在设置里打开相机权限。")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create().show();
                        }
                        return;
                    }
                }
                photoUtils.takePicture(MyAccountActivity.this);
            }
        });
        dialog.setMiddleListener(new View.OnClickListener() {//本地选取
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                photoUtils.selectPicture(MyAccountActivity.this);
            }
        });

        dialog.show();

    }

    //拍照后 结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(MyAccountActivity.this, requestCode, resultCode, data);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        User user = getCurrentUser();
        tvMyUsername.setText(user.getUsername());
        tvMyEmail.setText(user.getEmail() == null ? "未设置" : user.getEmail());
        tvMyMotto.setText(user.getMotto() == null ? "这个人很懒，什么都没有留下！" : user.getMotto());
    }

    @OnClick({R.id.rl_my_avatar, R.id.rl_my_username, R.id.rl_my_gender, R.id.rl_my_birthday, R.id.rl_my_email, R.id.rl_my_motto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_my_avatar://头像
                showPhotoDialog();
                break;
            case R.id.rl_my_username://昵称
                startActivity(UpdateNameActivity.class, null, false);
                break;
            case R.id.rl_my_gender://性别
                LinearLayout gendrLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_gender_alertdialog, null);
                LinearLayout man_layout = (LinearLayout) gendrLayout.findViewById(R.id.ll_gender_man);
                LinearLayout woman_layout = (LinearLayout) gendrLayout.findViewById(R.id.ll_gender_woman);
                final CheckBox man_box = (CheckBox) gendrLayout.findViewById(R.id.cb_gender_man);
                final CheckBox woman_box = (CheckBox) gendrLayout.findViewById(R.id.cb_gender_woman);
                if (getCurrentUser().isGender()) {
                    man_box.setChecked(true);
                    woman_box.setChecked(false);
                } else {
                    woman_box.setChecked(true);
                    man_box.setChecked(false);
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final AlertDialog dialog = builder.create();
                dialog.setView(gendrLayout);
                dialog.setCancelable(true);
                dialog.show();

                //点击 男
                man_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        User newUser = new User();
                        newUser.setGender(true);
                        User _user = getCurrentUser();
                        newUser.update(_user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    man_box.setChecked(true);
                                    woman_box.setChecked(false);
                                    tvMyGender.setText(getCurrentUser().isGender() ? "男" : "女");
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });
                //点击 女
                woman_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        User newUser = new User();
                        newUser.setGender(false);
                        User _user = getCurrentUser();
                        newUser.update(_user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                man_box.setChecked(false);
                                woman_box.setChecked(true);
                                tvMyGender.setText(getCurrentUser().isGender() ? "男" : "女");
                            }
                        });
                        dialog.dismiss();
                    }
                });

                break;
            case R.id.rl_my_birthday://生日
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        User newUser = new User();
                        newUser.setBirthday(year + "-" + (month + 1) + "-" + day);
                        User _user = getCurrentUser();
                        newUser.update(_user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                tvMyBirthday.setText(getCurrentUser().getBirthday());
                            }
                        });
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                Date date = new Date();
                datePickerDialog.getDatePicker().setMaxDate(date.getTime());
                datePickerDialog.show();
                break;
            case R.id.rl_my_email://邮箱
                startActivity(UpdateEmailActivity.class, null, false);
                break;
            case R.id.rl_my_motto://个性签名
                startActivity(UpdateMottoActivity.class, null, false);
                break;
        }
    }


}
