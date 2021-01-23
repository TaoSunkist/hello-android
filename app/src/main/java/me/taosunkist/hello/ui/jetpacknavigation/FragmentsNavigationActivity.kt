package me.taosunkist.hello.ui.jetpacknavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.taosunkist.hello.R

class FragmentsNavigationActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.jetpack_navigation_activity)
		if (savedInstanceState == null) {
			supportFragmentManager.beginTransaction()
				.replace(R.id.container, JetpackNavigationFragment.newInstance())
				.commitNow()
		}
	}
}