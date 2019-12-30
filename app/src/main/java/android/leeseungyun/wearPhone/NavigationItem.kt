package android.leeseungyun.wearPhone

data class NavigationItem(val titleID: Int, val drawableID: Int)

enum class MainNavigation(val pos: Int) {
    DIAL(0), CALLLOG(1), CONTACTS(2), SETTINGS(3)
}

val mainNavigationItems = listOf(
    NavigationItem(R.string.dial, R.drawable.ic_dialpad_white_24dp),
    NavigationItem(R.string.log, R.drawable.ic_phone_log_24px),
    NavigationItem(R.string.contacts, R.drawable.ic_contacts_white_24dp),
    NavigationItem(R.string.settings, R.drawable.ic_settings_white_24dp)
)