package android.ssoim.com.handytorch;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.io.IOException;

import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class HandyTorchMain extends AppCompatActivity implements SurfaceHolder.Callback{

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private Button torchBtn;
    private RippleBackground mRippleEffect;
    private boolean mTorchBool;

    private final String onTorch = Parameters.FLASH_MODE_TORCH;
    private final String offTorch = Parameters.FLASH_MODE_OFF;

    private static final int REQUEST_GET_ACCOUNT = 112;
    private static final int PERMISSION_REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handy_torch_main);

        init();

    }

    @Override
    protected void onResume() {

        // 2012.05.03 SurfaceView 추가
	        /*
	         * Dummy SurfaceView 생성
	         * * surfaceview 는 최소 1dp이상이어야 한다는...
	         */
        mSurfaceView = (SurfaceView) this.findViewById(R.id.surface);
        mSurfaceView.setZOrderOnTop(true); 								// 서피스 뷰 transparent시 필수
        mSurfaceHolder = mSurfaceView.getHolder();
//	        mSurfaceHolder.setFormat(PixelFormat.OPAQUE);
        mSurfaceHolder.addCallback(this);
//	        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // 2012.05.03 폰 슬립 방지
        disablePhoneSleep();

        super.onResume();
    }

    // 폰 슬립 방지 메소드
    private void disablePhoneSleep() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }



    @Override
    protected void onPause() {
        if( mCamera != null ){
            mTorchBool = false;
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        if( mCamera != null ){
            mCamera.release();
            mCamera = null;
        }

        super.onDestroy();
    }



    private void init() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }

        torchBtn = (Button) findViewById(R.id.torch_btn);
        torchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTorchBool) {
                    mTorchBool = false;
                } else {
                    mTorchBool = true;
                }

                setBtnAndTorch(mTorchBool);

                Log.d("BOOL", mTorchBool +"");
            }
        });

        // Ripple Effect
        mRippleEffect = (RippleBackground) findViewById(R.id.ripple_content);


    }

    private void setBtnAndTorch(boolean bool) {
        if(bool) {
            setTorchOn();
        } else {
            setTorchOff();
        }
    }


    private void setTorchOn() {


        torchBtn.setBackgroundResource(R.drawable.cancel_ic);
        mRippleEffect.startRippleAnimation();

        try{
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch( Exception e ){
            e.printStackTrace();
        }

        if(mCamera!=null){
//            if( !mTorchEnabled){

//                    Parameters params = mCamera.getParameters();
//                    params.setFlashMode(onTorch);
//                    mCamera.setParameters( params );
//                    mCamera.startPreview();


                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setFlashMode(onTorch);
                    mCamera.setParameters(parameters);
                    mCamera.startPreview();

                    try {
                        mCamera.setPreviewDisplay(mSurfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                }

//            }
        }
    }


    private void setTorchOff() {
        torchBtn.setBackgroundResource(R.drawable.on_ic);
        mRippleEffect.stopRippleAnimation();

        try{
            mCamera =Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch( Exception e ){
        }

        if(mCamera!=null){
//                    Parameters params = mCamera.getParameters();
//                    params.setFlashMode(offTorch);
//                    mCamera.setParameters( params );
//                    mCamera.stopPreview();

                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setFlashMode(offTorch);
                    mCamera.setParameters( parameters );
                    mCamera.stopPreview();

        }
    }


    private boolean checkPermission() {
//        int result = ContextCompat.checkSelfPermission(getApplicationContext(), GET_ACCOUNTS);
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{GET_ACCOUNTS, CAMERA}, REQUEST_GET_ACCOUNT);
//        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

//                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted)
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access location data and camera", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access  camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }

                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = null;
    }

    @Override
    public void onBackPressed() {

        if( mCamera != null ){
            mCamera.release();
            mCamera = null;
        }

        finish();


        super.onBackPressed();
    }
}
