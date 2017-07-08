package android.ssoim.com.handytorch;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.skyfishjy.library.RippleBackground;

import java.io.IOException;

public class HandyTorchMain extends AppCompatActivity implements SurfaceHolder.Callback{

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private Button torchBtn;
    private RippleBackground mRippleEffect;
    private boolean mTorchBool;

    private final String onTorch = Parameters.FLASH_MODE_TORCH;
    private final String offTorch = Parameters.FLASH_MODE_OFF;

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
            mCamera = Camera.open();
        } catch( Exception e ){
            e.printStackTrace();
        }

        if(mCamera!=null){
//            if( !mTorchEnabled){

                    Parameters params = mCamera.getParameters();
                    params.setFlashMode(onTorch);
                    mCamera.setParameters( params );
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
            mCamera = Camera.open();
        } catch( Exception e ){
        }
        if(mCamera!=null){
                    Parameters params = mCamera.getParameters();
                    params.setFlashMode(offTorch);
                    mCamera.setParameters( params );
                    mCamera.stopPreview();

        }
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
