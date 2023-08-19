package com.mobivone.sms.navSetup

sealed class Screens(val route:String){
    object MainScreen:Screens("mainScreen")
}
