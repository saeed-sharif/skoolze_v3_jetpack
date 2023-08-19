package com.mobivone.sms.navSetup


import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mobivone.sms.views.mainScreen.MainComposable

@Composable
fun navGraph(navControler: NavHostController) {
    NavHost(navController = navControler, startDestination = Screens.MainScreen.route ){
        composable(Screens.MainScreen.route){
            MainComposable(navController = navControler)
        }
    }
}