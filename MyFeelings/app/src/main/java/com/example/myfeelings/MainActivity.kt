package com.example.myfeelings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.json.JSONArray
import org.json.JSONException
import utilities.Emociones
import utilities.JSONFile
import utilities.customCircleDrawable
import utilities.CustomBarDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    var JsonFile: JSONFile? = null
    var veryHappy = 0.0F
    var happy = 0.0F
    var neutral = 0.0F
    var sad = 0.0F
    var verysad = 0.0F
    var data:Boolean = false
    var lista = ArrayList<Emociones>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        JsonFile = JSONFile()

        fetchingData()

        if(!data){
            var emociones = ArrayList<Emociones>()
            val fondo = customCircleDrawable(this, emociones)
            graph.background = fondo
            graphVeryHappy.background = CustomBarDrawable(this, Emociones("Muy feliz", 0.0F, R.color.mustard, veryHappy))
            graphHappy.background = CustomBarDrawable(this, Emociones("Feliz", 0.0F, R.color.orange, happy))
            graphNeutral.background = CustomBarDrawable(this, Emociones("Neutral", 0.0F, R.color.greenie, neutral))
            graphSad.background = CustomBarDrawable(this, Emociones("Triste", 0.0F, R.color.blue,sad))
            graphVerySad.background = CustomBarDrawable(this, Emociones("Muy triste",0.0F,R.color.deepBlue,verysad))
        }else{
            actualizarGrafica()
            iconoMayoria()
        }

        guardarButton.setOnClickListener {
            guardar()
        }

        veryHappyButton.setOnClickListener {
            veryHappy++
            iconoMayoria()
            actualizarGrafica()
        }

        happyButton.setOnClickListener {
            happy++
            iconoMayoria()
            actualizarGrafica()
        }

        neutralButton.setOnClickListener {
            neutral++
            iconoMayoria()
            actualizarGrafica()
        }

        SadButton.setOnClickListener {
            sad++
            iconoMayoria()
            actualizarGrafica()
        }

        verySadButton.setOnClickListener {
            verysad++
            iconoMayoria()
            actualizarGrafica()
        }




    }

    fun fetchingData(){
        try{
            var json: String = JsonFile?.getData(this)?:""
            if(json != ""){
                this.data = true
                var jsonArray: JSONArray = JSONArray(json)
                this.lista = parseJson(jsonArray)

                for (i in lista){
                    when(i.nombre){
                        "Muy feliz" -> veryHappy = i.total
                        "Feliz" -> happy = i.total
                        "Neutral" -> neutral = i.total
                        "Triste" -> sad = i.total
                        "Muy triste" -> verysad = i.total
                    }
                }
            }else{
                this.data = false
            }
        }catch(exception: JSONException){
            exception.printStackTrace()
        }
    }
    fun iconoMayoria(){

        if (happy>veryHappy && happy>neutral && happy>sad && happy>verysad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_happy))
        }
        if (veryHappy>happy && veryHappy>neutral && veryHappy>sad && veryHappy>verysad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_veryhappy))
        }
        if (neutral>veryHappy && neutral>happy && neutral>sad && neutral>verysad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_neutral))
        }
        if (sad>veryHappy && sad>neutral && sad>happy && sad>verysad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_sad))
        }
        if (verysad>veryHappy && verysad>neutral && verysad>sad && verysad>happy){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_verysad))
        }



    }
    fun actualizarGrafica(){

        val total = veryHappy + happy + neutral + verysad + sad

        var pVH: Float = (veryHappy * 100/total).toFloat()
        var pH: Float = (happy * 100/total).toFloat()
        var pN: Float = (neutral * 100/total).toFloat()
        var pS: Float = (sad * 100/total).toFloat()
        var pVS: Float = (verysad * 100/total).toFloat()

        lista.clear()
        lista.add(Emociones("Muy feliz", pVH, R.color.mustard, veryHappy))
        lista.add(Emociones("Feliz", pH, R.color.orange, happy))
        lista.add(Emociones("Neutral", pN, R.color.greenie, neutral))
        lista.add(Emociones("Triste", pS, R.color.blue,sad))
        lista.add(Emociones("Muy triste",pVS,R.color.deepBlue,verysad))

        val fondo = customCircleDrawable(this,lista)

        graphVeryHappy.background = CustomBarDrawable(this, Emociones("Muy feliz", pVH, R.color.mustard, veryHappy))
        graphHappy.background = CustomBarDrawable(this, Emociones("Feliz", pH, R.color.orange, happy))
        graphNeutral.background = CustomBarDrawable(this, Emociones("Neutral", pN, R.color.greenie, neutral))
        graphSad.background = CustomBarDrawable(this, Emociones("Triste", pS, R.color.blue,sad))
        graphVerySad.background = CustomBarDrawable(this, Emociones("Muy triste",pVS,R.color.deepBlue,verysad))

        graph.background = fondo

    }
    fun parseJson(JsonArray:JSONArray): ArrayList<Emociones>{
        var lista = ArrayList<Emociones>()

        for(i in 0..JsonArray.length()){
            try{
                val nombre = JsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = JsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color = JsonArray.getJSONObject(i).getInt("color")
                val total = JsonArray.getJSONObject(i).getDouble("total").toFloat()
                var emocion = Emociones(nombre, porcentaje, color, total)
                lista.add(emocion)
            }catch(exception:JSONException){
                exception.printStackTrace()
            }
        }
        return lista
    }
    fun guardar(){

        var JsonArray = JSONArray()
        var  o : Int = 0
        for(i in lista){
            Log.d("objetos", i.toString())
            var j:JSONObject = JSONObject()
            j.put("nombre",i.nombre)
            j.put("porcentaje",i.porcentaje)
            j.put("color",i.color)
            j.put("total",i.total)

            JsonArray.put(o,j)
            o++
        }

        JsonFile?.saveData(this,JsonArray.toString())

        Toast.makeText(this,"Datos guardados",Toast.LENGTH_SHORT).show()

    }

}
