package com.example.macrobenchmark
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@LargeTest
@RunWith(Parameterized::class)
class SmallListStartupBenchmark(private val startupMode: StartupMode) {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureStartup(
        profileCompiled = true,
        startupMode = startupMode
    ) {
        action = "com.bupp.wood_spoon_eaters.features.main.RECYCLER_VIEW_ACTIVITY"
        putExtra("ITEM_COUNT", 5)
    }

    companion object {
        @Parameterized.Parameters(name = "mode={0}")
        @JvmStatic
        fun parameters(): List<Array<Any>> {
            return listOf(StartupMode.COLD, StartupMode.WARM)
                .map { arrayOf(it) }
        }
    }
}