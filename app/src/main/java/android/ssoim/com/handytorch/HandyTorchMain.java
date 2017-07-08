package android.ssoim.com.handytorch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

import com.skyfishjy.library.RippleBackground;

public class HandyTorchMain extends AppCompatActivity implements SurfaceHolder.Callback{

    private Button torchBtn;
    private RippleBackground mRippleEffect;
    private boolean torchBool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handy_torch_main);

        init();

    }

    private void init() {
        torchBtn = (Button) findViewById(R.id.torch_btn);
        torchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(torchBool) {
                    torchBool = false;
                } else {
                    torchBool = true;
                }

                setBtnAndTorch(torchBool);
            }
        });

        // Ripple Effect
        mRippleEffect = (RippleBackground) findViewById(R.id.ripple_content);
        mRippleEffect.startRippleAnimation();

    }

    private void setBtnAndTorch(boolean bool) {

    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
