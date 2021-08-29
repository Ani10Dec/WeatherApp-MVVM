package com.example.weatherappmvvm.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.weatherappmvvm.R
import com.example.weatherappmvvm.databinding.ActivityRegisterBinding
import com.example.weatherappmvvm.repository.Repository
import com.example.weatherappmvvm.repository.database.UserDao
import com.example.weatherappmvvm.repository.database.UserDataBase
import com.example.weatherappmvvm.repository.retrofit.ApiServices
import com.example.weatherappmvvm.repository.retrofit.RetrofitInstances
import com.example.weatherappmvvm.utils.Apphelper
import com.example.weatherappmvvm.viewmodel.RegisterViewModel
import com.example.weatherappmvvm.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class RegisterActivity : AppCompatActivity() {

    // Global Variables
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var stGender: String
    private lateinit var viewModel: RegisterViewModel
    private lateinit var apiServices: ApiServices
    private lateinit var userDao: UserDao
    private var isBtnCheckActive = false

    override fun onResume() {
        super.onResume()
        val gender = resources.getStringArray(R.array.gender)
        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_item, gender)
        binding.genderDropdown.setAdapter(arrayAdapter)
        binding.genderDropdown.setOnItemClickListener { parent, _, position, _ ->
            stGender = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, stGender, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        // Api Service Instance
        apiServices =
            RetrofitInstances.getPinCodeRetroInstance().create(ApiServices::class.java)

        // Initiate userDao
        userDao = UserDataBase.getDatabaseInstance(applicationContext).getUserDao

        // Setting ViewModel
        val repo = Repository(apiServices, userDao)
        val factory = ViewModelFactory(repo, "register")
        viewModel = ViewModelProvider(this, factory).get(RegisterViewModel::class.java)

        // Connecting XML ViewModel to ViewModel Class
        binding.viewModel = viewModel

        // Defining Activity Lifecycle
        binding.lifecycleOwner = this

        // Any Toast Message
        viewModel.message.observe(this, {
            it.getContentIfNotHandledOrReturnNull()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        })

        // Checking for PinCode Validation
        viewModel.inputPinCode.observe(this, {
            isBtnCheckActive = if (it.length == 6) {
                binding.btnPinCheck.setBackgroundColor(0xFF1A7ED5.toInt())
                true
            } else {
                binding.btnPinCheck.setBackgroundColor(0xFFDAD4D4.toInt())
                false
            }
        })

        // Setting District and State Value
        viewModel.pinCodeResponse.observe(this, {
            Apphelper.hideProgressBar()
            Apphelper.hideKeyboard(this)
            if (it.listIterator().next().Status == "Success") {
                val current = it.listIterator().next().PostOffice.listIterator().next()
                binding.tvState.text = current.State
                binding.tvDistrict.text = current.District
            } else {
                Toast.makeText(this, "Please enter valid pin code", Toast.LENGTH_SHORT).show()
            }
        })

        // Calendar Instances
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(myCalendar)
        }

        // Open Date Picker Dialog
        binding.datePicker.setOnClickListener {
            val calender = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val month = calender.get(Calendar.MONTH)
            val day = calender.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(this, datePicker, year, month, day)
            datePickerDialog.datePicker.maxDate = Date().time
            datePickerDialog.show()
        }

        // Getting PinCode Data
        binding.btnPinCheck.setOnClickListener {
            if (Apphelper.isOnline(this)) {
                if (isBtnCheckActive) {
                    Apphelper.showProgressBar(this)
                    viewModel.getPinCodeData()
                } else {
                    Toast.makeText(this, "Please enter valid pin code", Toast.LENGTH_SHORT).show()
                }
            } else {
                Apphelper.hideKeyboard(this)
                Toast.makeText(this, "No Internet!\nCheck Internet Connection", Toast.LENGTH_LONG)
                    .show()
            }
        }

        // Register User
        binding.btnRegister.setOnClickListener {
            if (checkForInputValidation()) {
                viewModel.addUserToDatabase()
                Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("name", binding.etName.text.toString())
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateLabel(myCalendar: Calendar) {
        val dateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        binding.datePicker.setText(sdf.format(myCalendar.time))
        binding.etAge.setText("${getAge(myCalendar.timeInMillis)} Years")
    }

    private fun getAge(timeInMillis: Long): Int {
        val dob = Calendar.getInstance()
        dob.timeInMillis = timeInMillis

        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--
        }
        return age
    }


    private fun checkForInputValidation(): Boolean {
        if (binding.etPhone.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etPhone.text.toString().length < 10) {
            Toast.makeText(this, "Please Enter Valid Number", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etName.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.genderDropdown.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please Enter Your Gender", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.datePicker.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your date of birthday", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!binding.datePicker.text.toString().contains("/")) {
            Toast.makeText(this, "Please enter valid date of birthday", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etAge.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please select your age", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etAddressLine1.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etAddressLine1.text.toString().length < 4) {
            Toast.makeText(this, "Please enter valid address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etPinCode.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your pin code", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etPinCode.text.toString().length < 6) {
            Toast.makeText(this, "Please enter valid pin code", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.tvDistrict.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please fetch district from pin code", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.tvState.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please fetch state from pin code", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}