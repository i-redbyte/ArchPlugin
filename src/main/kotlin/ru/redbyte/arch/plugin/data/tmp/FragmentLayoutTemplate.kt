package ru.redbyte.arch.plugin.data.tmp

class FragmentLayoutTemplate : Template<NoParams> {
    override fun generate(params: NoParams): String {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<FrameLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    android:id=\"@+id/container\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\" />"
    }
}
