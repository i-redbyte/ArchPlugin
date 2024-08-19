package ru.redbyte.arch.plugin.data.templates

import ru.redbyte.arch.plugin.data.utils.TAB

class FragmentLayoutTemplate : Template<NoParams> {
    override fun generate(params: NoParams): String {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<FrameLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "${TAB}android:id=\"@+id/container\"\n" +
                "${TAB}android:layout_width=\"match_parent\"\n" +
                "${TAB}android:layout_height=\"match_parent\" />"
    }
}
