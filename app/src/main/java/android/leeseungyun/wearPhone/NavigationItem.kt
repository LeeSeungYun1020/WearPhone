package android.leeseungyun.wearPhone

data class NavigationItem(val titleID: Int, val drawableID: Int)

val navigationItems = listOf(
    NavigationItem(R.string.dial, R.drawable.ic_dialpad_white_24dp),
    NavigationItem(R.string.log, R.drawable.ic_phone_log_24px),
    NavigationItem(R.string.contacts, R.drawable.ic_contacts_white_24dp),
    NavigationItem(R.string.settings, R.drawable.ic_settings_white_24dp)
)