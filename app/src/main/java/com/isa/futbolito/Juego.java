package com.isa.futbolito;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Juego extends AppCompatActivity implements SensorEventListener2 {
    private float xPos, xAccel, xVel = 0.0f;
    private float yPos, yAccel, yVel = 0.0f;
    private float xMax, yMax;
    private Bitmap ball;
    private SensorManager sensorManager;
    //
    private int GolesA = 0;
    private int GolesB = 0;
    private Boolean flagGolA=false;
    private Boolean flagGolB=false;
    Puntos puntos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bola bola = new Bola(this);
        setContentView(bola);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getSupportActionBar().hide();



        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        xMax = (float) size.x -100;
        yMax = (float) size.y -100;
        Log.d("cosa", "Valors maximos: x = " + xMax + "   y = "+ yMax);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

        //
        //
        puntos = new Puntos(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size.x, size.y);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        this.addContentView(puntos, layoutParams);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onFlushCompleted(Sensor sensor) { }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAccel = (int) sensorEvent.values[0];
            yAccel = -(int) sensorEvent.values[1];
            Log.d("cosa", "Valores: xVALOR = " + (int) sensorEvent.values[0] + "  yVALOR = " + sensorEvent.values[1]);
            Log.d("cosa", "Valores: xACEL = " + xAccel + "  yACEL = " + yAccel);
           // xAccel = - (int) Math.pow(sensorEvent.values[0], 2);
            //yAccel = (int) Math.pow(sensorEvent.values[1], 2);
            updateBall();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {  }

    private void updateBall() {
        xPos -= xAccel;
        yPos -= yAccel;

        if (xPos > xMax) {
            xPos = xMax;
        } else if (xPos < 0) {
            xPos = 0;
        }

        if (yPos > yMax) {
            yPos = yMax;
        } else if (yPos < 0) {
            yPos = 0;
        }

        //Porteria A superior
        if((xPos > (xMax/2)-95-100) && (yPos < 220) && (xPos < (xMax/2)-95-90)){
            xPos = ((xMax/2)-95) -100;
        }
        if((xPos > (xMax - (xMax/2)+185)) && (yPos < 220) && (xPos < (xMax - (xMax/2)+195))){
            xPos = (xMax - (xMax/2)+195) ;
        }
        if((xPos >= (xMax/2)-95-90) && (xPos <= (xMax - (xMax/2)+185)) && (yPos <= 220) && !flagGolA){
            GolesA ++;
            puntos.invalida();
            flagGolA = true;
        } else if(flagGolA && !((xPos >= (xMax/2)-95-90) && (xPos <= (xMax - (xMax/2)+185)) && (yPos <= 220))){
            Log.d("puntos", "x: " + xPos + "  y: " + yPos);
            flagGolA = false;
        }

        //Porteria B inferior
        if((xPos > (xMax/2)-95-100) && (yPos > (yMax-130)) && (xPos < (xMax/2)-95-90)){
            xPos = ((xMax/2)-95) -100;
        }
        if((xPos > (xMax - (xMax/2)+185)) && (yPos > (yMax-130)) && (xPos < (xMax - (xMax/2)+195))){
            xPos = (xMax - (xMax/2)+195) ;
        }
        if((xPos >= (xMax/2)-95-90) && (xPos <= (xMax - (xMax/2)+185)) && (yPos >= (yMax-130)) && !flagGolB){
            GolesB ++;
            puntos.invalida();
            flagGolB = true;
        } else if(flagGolB && !((xPos >= (xMax/2)-95-90) && (xPos <= (xMax - (xMax/2)+185)) && (yPos >= (yMax-130)))){
            Log.d("puntos", "x: " + xPos + "  y: " + yPos);
            flagGolB = false;
        }

        Log.d("cosa", "Valores: x = " + xPos + "  y = " + yPos);
    }

    public class Bola extends View {
        Paint lapiz = new Paint();
        public Bola(Context context) {
            super(context);
            Bitmap ballSrc = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
            ball = Bitmap.createScaledBitmap(ballSrc, 100, 100, true);
            setBackgroundResource(R.drawable.cancha_copia);
            //
        }

        public void onDraw(Canvas lienzo){
            lienzo.drawBitmap(ball, xPos, yPos, null);
            lapiz.setColor(Color.RED);
            Rect p1 = new Rect();
            p1.set((int)(xMax/2)-95, 50, (int)(xMax - (xMax/2)+195), 220);
            //lienzo.drawRect(p1, lapiz);
            p1.set((int)(xMax/2)-95, (int)(yMax-130), (int)(xMax - (xMax/2)+195), (int) (yMax+50));
            //lienzo.drawRect(p1, lapiz);
            invalidate();
        }
    }

    private class Puntos extends View{
        Paint lapiz = new Paint();
        public Puntos(Context context) {
            super(context);

        }
        public void invalida(){
            this.invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            String puntaje = GolesA + " - " + GolesB;
            lapiz.setTextSize(100);
            Rect rect = new Rect();
            lapiz.getTextBounds(puntaje, 0, puntaje.length(), rect);
            canvas.translate(xMax, yMax/2);
            lapiz.setStyle(Paint.Style.FILL);

            lapiz.setStyle(Paint.Style.STROKE);
            canvas.translate(-xMax, -yMax/2);

            canvas.rotate(90, xMax + rect.exactCenterX()-90,yMax/2 + rect.exactCenterY()-20);
            lapiz.setStyle(Paint.Style.FILL);
            canvas.drawText(puntaje, xMax, yMax/2, lapiz);


        }
    }
}