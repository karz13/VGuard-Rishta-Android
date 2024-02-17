package com.tfl.vguardrishta.custom

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import org.objectweb.asm.Opcodes



class ProgressStepView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

        private var bottom:Float = 0.0f;
     var currentPoints:Float = 1000f
    var bonusPoints:Float =0f
    var percentValue:Int=0
     var data = LinkedHashMap<String,String>()
    private  var displayMetrics=DisplayMetrics()
    private var graphcurrentPos:Float = 0.0f;
    private var left:Float = 0.0f;
    private var maxX:Float = 0.0f;
    private var maxY:Float = 0.0f;
    private var midpointX:Float = 0.0f;
    private var rectHeight:Float = 0.0f
    private var rectLength:Float = 0.0f
    private var right:Float = 0.0f
    private var steps:Int = 0;
    private  var textBounds= Rect()
    private var top:Float = 0.0f
    private val backgroundPaint = Paint().apply {
        color = Color.LTGRAY
        isAntiAlias=true
        style = Paint.Style.FILL
        strokeWidth = 20f


    }
    private val textPaint =Paint().apply {
        color = android.graphics.Color.BLACK
        isAntiAlias=true
        style = Paint.Style.FILL
        textAlign=Paint.Align.CENTER
        textSize=64f
        //typeface= Typeface.DEFAULT_BOLD

    }

    private val textPaint2 = Paint().apply {
        color = Color.BLACK
        isAntiAlias=true
        style = Paint.Style.FILL
        textAlign=Paint.Align.CENTER
        textSize=34f
        //typeface= Typeface.DEFAULT_BOLD
    }
    private val textPaint3 = Paint().apply {
        color =Color.rgb(240, Opcodes.MONITOREXIT,0)
        isAntiAlias=true
        style = Paint.Style.FILL
        textAlign=Paint.Align.CENTER
        textSize=34f
        //typeface= Typeface.DEFAULT_BOLD
    }
    private val foregroundPaint = Paint().apply {
        color = Color.rgb(240, Opcodes.MONITOREXIT,0)
        style = Paint.Style.FILL
        isAntiAlias=true
        strokeWidth = 20f
    }
    private val blackPaint = Paint().apply {
        color = android.graphics.Color.BLACK
        style = Paint.Style.FILL
        strokeWidth = 20f
    }


    init {
        obtainScreenDimensions()
        midpointX=maxX/2
        left=midpointX-10
        right=midpointX+10
        bottom= (maxY*0.8).toFloat()



    }


    fun obtainScreenDimensions(){

        val systemService = context.getSystemService(Context.WINDOW_SERVICE)
        if (systemService != null) {
            (systemService as WindowManager).defaultDisplay.getMetrics(displayMetrics)
            maxX = displayMetrics.widthPixels.toFloat()
            maxY = (displayMetrics.heightPixels - 400).toFloat()
            rectLength = (maxX / 2.5).toFloat()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var fillcolor:Paint;
        val stepInterval = (maxY * 0.9 - 200.0) / (data.size - 1)
        canvas.drawRoundRect(
            left, 200.0f,
            right, maxY as Float * 0.9f, 50.0f, 50.0f, backgroundPaint
        )
        DrawText(canvas,textPaint,"Base Points",300f,100f)
        DrawText(canvas,textPaint,"Bonus Points",midpointX+350f,100f)
        if(currentPoints>2000f){
            graphcurrentPos=3000f

            graphcurrentPos += ((currentPoints - 2000f) * 0.75).toFloat()

            canvas.drawRoundRect(
                left,
                200.0f,
                right,
                (((maxY*0.9)/data.entries.last().key.toFloat())*graphcurrentPos).toFloat(),
                50.0f,
                50.0f,
                foregroundPaint
            )


        } else {

            graphcurrentPos = (currentPoints*1.5).toFloat()
            canvas.drawRoundRect(
                left,
                200.0f,
                right,
                (((maxY*0.9)/data.entries.last().key.toFloat())*graphcurrentPos).toFloat(),
                50.0f,
                50.0f,
                foregroundPaint
            )

        }
        var ypos = 200f
        for (i in data){
                if(i.key.toFloat()>0){
                    percentValue= ((i.value.toFloat()/i.key.toFloat())*100).toInt()

                    val rect = RectF(
                        midpointX +200f, ypos - 200.0f,
                        ((midpointX+100)+(rectLength*0.75)).toFloat(),  ypos -80

                    )
                    canvas.drawRoundRect(rect, 60.0f, 60.0f, backgroundPaint)
                    DrawText(canvas, textPaint, "$percentValue%", rect.centerX(), rect.centerY())

                }
            if(currentPoints<i.key.toFloat()){
                fillcolor = backgroundPaint
            }else{
                    bonusPoints = (currentPoints*(percentValue.toFloat()/100))
                fillcolor = foregroundPaint
            }
           var rect =  RectF(100.0f, ypos - 50.0f, rectLength, ypos + 100.0f);
            canvas.drawRoundRect(rect, 60.0f, 60.0f, fillcolor)
            DrawText(canvas, textPaint, i.key, rect.centerX(), rect.centerY());
            val rect2 = RectF(
                midpointX + 150.0f, ypos - 50.0f,
                midpointX+rectLength, 100.0f + ypos
            )
            canvas.drawRoundRect(rect2, 60.0f, 60.0f, fillcolor)
            DrawText(canvas, textPaint, i.value, rect2.centerX(), rect2.centerY())
            canvas.drawCircle((maxX / 2), ypos, 50.0f, fillcolor)
            if (currentPoints >= i.key.toFloat()) {
                DrawTick(canvas, ypos)
            }

            ypos = (ypos +stepInterval).toFloat()
        }
       if(currentPoints==0.0f||currentPoints==1000f||currentPoints==2000f||currentPoints==4000f||currentPoints==6000f){
            Log.d("TRUYE",currentPoints.toString())
       }else{


           DrawText(
               canvas,
               textPaint2,
               currentPoints.toString(),
               midpointX-80f,
               (((maxY*0.9)/data.entries.last().key.toFloat())*graphcurrentPos).toFloat()
           )
           DrawText(
               canvas,
               textPaint3,
               bonusPoints.toString(),
               midpointX+80f,
               (((maxY*0.9)/data.entries.last().key.toFloat())*graphcurrentPos).toFloat()
           )

       }
    }


    fun DrawText(canvas: Canvas, paint: Paint, text: String, cx: Float, cy: Float) {

        paint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(text, cx, cy - textBounds.exactCenterY(), paint)
    }

    fun DrawTick(canvas: Canvas, ypos: Float) {

        val drawable = context.getDrawable(com.tfl.vguardrishta.R.drawable.anim_tick)
        if (drawable != null) {
            val animVectDrawable2 = drawable as AnimatedVectorDrawable
            val i = maxX.toInt()
            val f = 25f
            animVectDrawable2.setBounds(
                i / 2 - 25,
                (ypos - f).toInt(),
                i / 2 + 25,
                (f + ypos).toInt()
            )
            animVectDrawable2.draw(canvas!!)
            return
        }
        throw NullPointerException("null cannot be cast to non-null type android.graphics.drawable.AnimatedVectorDrawable")
    }


}