package com.example.macrobenchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = "com.bupp.wood_spoon_eater",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) { // this = MacrobenchmarkScope
        pressHome()
        val intent = Intent()
        intent.setPackage("com.bupp.wood_spoon_eater")
        intent.action = "com.bupp.wood_spoon_eater.myaction"
        startActivityAndWait(intent)
    }

//    @Test
//    fun measureScroll() {
//        val compilationMode = CompilationMode()
//        benchmarkRule.measureRepeated(
//            packageName = "mypackage.myapp",
//            metrics = listOf(FrameTimingMetric()),
//            compilationMode = compilationMode,
//            iterations = 5,
//            setupBlock = {
//                // before starting to measure, navigate to the UI to be measured
//                val intent = Intent()
//                intent.action = ACTION
//                startActivityAndWait(intent)
//            }
//        ) {
//            val recycler = device.findObject(By.res("mypackage.myapp", "recycler_id"))
//            // Set gesture margin to avoid triggering gesture nav
//            // with input events from automation.
//            recycler.setGestureMargin(device.displayWidth / 5)
//
//            // Scroll down several times
//            for (i in 1..10) {
//                recycler.scroll(Direction.DOWN, 2f)
//                device.waitForIdle()
//            }
//        }
//    }
}