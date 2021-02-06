package com.rejfin.pricehunter.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.rejfin.pricehunter.R
import com.rejfin.pricehunter.databinding.FragmentConfigFileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception


class ConfigFileFragment : Fragment() {
    private val JSON_FILE_REQUEST = 1

    private var _binding: FragmentConfigFileBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentConfigFileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.menu.getItem(0).isVisible = false

        binding.btLoadFile.setOnClickListener {
            loadJson()
        }

        binding.btInstruction.setOnClickListener {
            val webIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/Rejfin/PriceHunter/blob/master/FIREBASE_INTEGRATION.md#6-mobile-app"))
            startActivity(webIntent)
        }
    }

    private fun saveConfigValues(jsonString:String?){
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if(jsonString != null){
            try{
                val jsonData = JSONObject(jsonString)
                val x = jsonData.getJSONObject("project_info")
                val y = jsonData.getJSONArray("client")
                val z = y.getJSONObject(0)


                val a = listOf(x["firebase_url"].toString(),
                        x["storage_bucket"].toString(),
                        z.getJSONObject("client_info")["mobilesdk_app_id"].toString(),
                        z.getJSONArray("api_key").getJSONObject(0)["current_key"].toString(),
                        x["project_id"].toString()
                )

                pref.edit().putString("firebase_config", a.toString()).apply()
                binding.ivFile.setColorFilter(ContextCompat.getColor(requireContext(), R.color.right))
                binding.tvError.visibility = View.GONE

                CoroutineScope(Dispatchers.IO).launch{
                    delay(1000)
                    if(requireActivity().supportFragmentManager.backStackEntryCount > 0){
                        requireActivity().supportFragmentManager.popBackStack()
                    }else{
                        requireActivity().supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, HomeFragment())
                            .commit()
                    }
                }

            }catch (e : Exception){
                binding.ivFile.setColorFilter(ContextCompat.getColor(requireContext(), R.color.wrong))
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    private fun loadJson(){
        val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
        fileIntent.type = "application/json"
        startActivityForResult(fileIntent, JSON_FILE_REQUEST)
    }

    private fun readJsonFile(context: Context, uri: Uri): String? {
        val reader: BufferedReader?
        val builder = StringBuilder()
        try {
            reader = BufferedReader(InputStreamReader(context.contentResolver.openInputStream(uri)))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return builder.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data != null){
                val jsonString = readJsonFile(requireContext(), data.data!!)
                saveConfigValues(jsonString)
            }
    }
}