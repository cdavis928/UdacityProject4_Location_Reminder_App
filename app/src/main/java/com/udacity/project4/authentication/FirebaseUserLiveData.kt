package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserLiveData : LiveData<FirebaseUser?>() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        value = firebaseAuth.currentUser
    }

    // Checks if there is currently a logged in user
    override fun onActive() {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    // Stop observing FirebaseAuth to prevent memory leaks
    override fun onInactive() {
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}