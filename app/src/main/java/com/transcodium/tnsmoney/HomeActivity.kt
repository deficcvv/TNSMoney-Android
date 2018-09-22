package com.transcodium.tnsmoney

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import com.transcodium.tnsmoney.classes.Anim
import com.transcodium.tnsmoney.classes.CoinsCore
import com.firebase.jobdispatcher.*
import com.transcodium.tnsmoney.classes.jobs.AssetsDataJob
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main
import java.lang.Exception
import kotlin.coroutines.experimental.CoroutineContext


class HomeActivity : DrawerActivity() {



    val homeActivity by lazy {
        this
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Anim(this).slideWindow(
                Gravity.START,
                Gravity.END
        )

        setContentView(R.layout.activity_home)

        super.onCreate(savedInstanceState)

        launch(Dispatchers.IO) {

            //lets start the job for r
            fetchStatsData()
        }

    }//end onCreate



    /**
     * doPeriodicTask
     */
    suspend fun fetchStatsData(){

        CoinsCore.fetchUserCoins(mActivity, true)

        try {
            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(mActivity))

            val job = dispatcher.newJobBuilder()
                    .setService(AssetsDataJob::class.java)
                    .setTag("assets_data")
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                    .setTrigger(Trigger.executionWindow(0, 30))
                    .setRecurring(true)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .build()

            dispatcher.mustSchedule(job)
        }catch(e: Exception){
            Log.e("JoB Error","${e.message}")
            e.printStackTrace()
        }

    }

}//end class
