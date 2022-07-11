package com.example.firebasetest


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetest.databinding.ActivityVerificationBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding
    private lateinit var auth: FirebaseAuth
    private var forceResendingToken : PhoneAuthProvider.ForceResendingToken? = null
    private var mCallBack:PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var textEmail = ""
    private var textPassword = ""
    private var mVerificationId : String? = null

    private val TAG ="MAIN_TAG"

    private lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.relativeLayoutGetOTP.visibility = View.VISIBLE
        binding.relativeLayoutVerifyOTP.visibility = View.GONE
        auth = Firebase.auth
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        val intentDataUser = intent

         textEmail = intentDataUser.getStringExtra("email").toString()
        textPassword = intentDataUser.getStringExtra("password").toString()

        mCallBack = object  : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){


            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signWithPhoneAuthCredential(phoneAuthCredential)

            }

            override fun onVerificationFailed(e: FirebaseException) {
            progressDialog.dismiss()
                Toast.makeText(this@VerificationActivity,"${e.message}",Toast.LENGTH_LONG).show()

            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {

                Log.d(TAG,"onCodeSent:$verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()


                Toast.makeText(this@VerificationActivity,"Verification code sent...",Toast.LENGTH_LONG).show()
                binding.relativeLayoutGetOTP.visibility = View.GONE
                binding.relativeLayoutVerifyOTP.visibility = View.VISIBLE

            }


        }
        binding.idBtnGetOtp.setOnClickListener {

            val phone = binding.idEdtPhoneNumber.text.toString().trim()
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this@VerificationActivity,"Please enter phone number",Toast.LENGTH_LONG).show()

            }else{
                startPhoneNumberVerification(phone)
            }
        }

        binding.resendtx.setOnClickListener{

            val phone = binding.idEdtPhoneNumber.text.toString().trim()
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this@VerificationActivity,"Please enter phone number",Toast.LENGTH_LONG).show()

            }else{
                resendVerificationCode(phone, forceResendingToken!!)
            }

        }
        binding.idBtnVerify.setOnClickListener{

            val code = binding.idEdtOtp.text.toString().trim()
            if (TextUtils.isEmpty(code)){
                Toast.makeText(this@VerificationActivity,"Please enter phone number",Toast.LENGTH_LONG).show()

            }else{
                verifyPhoneWithCode(mVerificationId,code)
            }
        }





    }
    private fun startPhoneNumberVerification(phone :String){
        progressDialog.setMessage("Verifing Phone Number...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun resendVerificationCode(phone: String,token : PhoneAuthProvider.ForceResendingToken){
        progressDialog.setMessage("Resend Code...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack!!)
            .setForceResendingToken(token)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)


    }

    private fun verifyPhoneWithCode(verificationId : String?,code:String){
        progressDialog.setMessage("Verifing code...")
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId.toString(),code)
        signWithPhoneAuthCredential(credential)
    }

    private fun signWithPhoneAuthCredential(credential: PhoneAuthCredential){
        progressDialog.setMessage("Logging IN")

        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val phone = auth.currentUser?.phoneNumber
                //SignUp(textEmail,textPassword)
                Toast.makeText(this,"Logged In as $phone",Toast.LENGTH_LONG).show()
                val intent1 = Intent(this, SetupProfileActivity::class.java)
                startActivity(intent1)
                this.finish()

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this,"${e.message}",Toast.LENGTH_LONG).show()

            }
    }
/*    fun SignUp(email: String, password: String) {


        try {
            auth = Firebase.auth
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"sign up successully",Toast.LENGTH_LONG).show()

                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this,"sorry your parameters not completed",Toast.LENGTH_LONG).show()

            }

        } catch (e: Exception) {
            Toast.makeText(this,"please, write email and password",Toast.LENGTH_LONG).show()

        }
    }*/


}