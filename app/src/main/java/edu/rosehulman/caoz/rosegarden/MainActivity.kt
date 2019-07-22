package edu.rosehulman.caoz.rosegarden

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Task as Task

class MainActivity : AppCompatActivity(), ListFragment.OnSelectedListener,
    LoginFragment.OnLoginButtonPressedListener
{

    private  val auth = FirebaseAuth.getInstance()
    lateinit var authStateListener: FirebaseAuth.AuthStateListener

    // Request code for launching the sign in Intent.
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        initializeListeners()




    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }


    private fun initializeListeners() {

        authStateListener = FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
            val user = auth.currentUser
            Log.d(Constants.TAG,"In auth listener, user = $user")
            if(user != null){
                Log.d(Constants.TAG,"UID: ${user.uid}")
                Log.d(Constants.TAG,"Name: ${user.displayName}")
                Log.d(Constants.TAG,"Email: ${user.email}")
                Log.d(Constants.TAG,"Phone: ${user.phoneNumber}")
                Log.d(Constants.TAG,"Photo URL: ${user.photoUrl}")
                switchToPhotoFragment(user.uid)
            }
            else{
                //toolbar.title ="Photo Bucket"
                switchToLoginFragment()
            }
        }

    }



    private fun switchToLoginFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, LoginFragment())
        ft.commit()
    }
    private fun switchToPhotoFragment(uid:String) {

        val fragment = ListFragment.newInstance(uid)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container,fragment)
        ft.commit()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {

            R.id.action_logout -> {
                auth.signOut()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onSelected(task: edu.rosehulman.caoz.rosegarden.Task) {
        val taskFragment = TaskFragment.newInstance(task)
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.slide_in_left,
            android.R.anim.slide_out_right);
        ft.replace(R.id.fragment_container,taskFragment)
        ft.addToBackStack("detail")
        ft.commit()
    }





    override fun onLoginButtonPressed(type: Int) {
        if(type==1) {
            launchLoginUI()
        }
        if(type==2) {

        }
    }







    private fun launchLoginUI() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
            //AuthUI.IdpConfig.FacebookBuilder().build()
        )
// Create and launch sign-in intent

        val loginIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.login)
            .build()

        startActivityForResult(
            loginIntent,  RC_SIGN_IN
        )
    }

}

