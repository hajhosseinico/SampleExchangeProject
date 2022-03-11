package ir.hajhosseini.payseracurrencyexchanger.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import ir.hajhosseini.payseracurrencyexchanger.ui.dashboard.DashboardFragment
import javax.inject.Inject

/**
 * MainFragmentFactory
 */

class MainFragmentFactory
@Inject
constructor() : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        return  when(className){
            DashboardFragment::class.java.name ->{
                DashboardFragment()
            }
            else -> super.instantiate(classLoader, className)
        }
    }
}
