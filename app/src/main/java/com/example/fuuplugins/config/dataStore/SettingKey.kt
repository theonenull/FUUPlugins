package com.example.fuuplugins.config.dataStore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object ThemeKey {
    val primary = longPreferencesKey("primary")
    val onPrimary = longPreferencesKey("on_primary")
    val primaryContainer = longPreferencesKey("primary_container")
    val onPrimaryContainer = longPreferencesKey("on_primary_container")
    val inversePrimary = longPreferencesKey("inverse_primary")
    val secondary = longPreferencesKey("secondary")
    val onSecondary = longPreferencesKey("on_secondary")
    val secondaryContainer = longPreferencesKey("secondary_container")
    val onSecondaryContainer = longPreferencesKey("on_secondary_container")
    val tertiary = longPreferencesKey("tertiary")
    val onTertiary = longPreferencesKey("on_tertiary")
    val tertiaryContainer = longPreferencesKey("tertiary_container")
    val onTertiaryContainer = longPreferencesKey("on_tertiary_container")
    val background = longPreferencesKey("background")
    val onBackground = longPreferencesKey("on_background")
    val surface = longPreferencesKey("surface")
    val onSurface = longPreferencesKey("on_surface")
    val surfaceVariant = longPreferencesKey("surface_variant")
    val onSurfaceVariant = longPreferencesKey("on_surface_variant")
    val surfaceTint = longPreferencesKey("surface_tint")
    val inverseSurface = longPreferencesKey("inverse_surface")
    val inverseOnSurface = longPreferencesKey("inverse_on_surface")
    val error = longPreferencesKey("error")
    val onError = longPreferencesKey("on_error")
    val errorContainer = longPreferencesKey("error_container")
    val onErrorContainer = longPreferencesKey("on_error_container")
    val outline = longPreferencesKey("outline")
    val outlineVariant = longPreferencesKey("outline_variant")
    val scrim = longPreferencesKey("scrim")
}

enum class Color_Attribute(val key:  Preferences.Key<Long>, val description: String) {
    PRIMARY(longPreferencesKey("primary"), "主要颜色是应用程序屏幕和组件中最常显示的颜色。"),
    ON_PRIMARY(longPreferencesKey("on_primary"), "用于显示在主颜色之上的文本和图标的颜色。"),
    PRIMARY_CONTAINER(longPreferencesKey("primary_container"), "容器的首选色调。"),
    ON_PRIMARY_CONTAINER(longPreferencesKey("on_primary_container"), "应该用于 primaryContainer 之上的内容的颜色（和状态变体）。"),
    INVERSE_PRIMARY(longPreferencesKey("inverse_primary"), "在需要反向配色方案的地方（例如 SnackBar 上的按钮）用作“主”颜色的颜色。"),
    SECONDARY(longPreferencesKey("secondary"), "辅助色提供了更多方式来强调和区分您的产品。次要颜色最适合浮动操作按钮、选择控件、突出显示选定的文本、链接和标题。"),
    ON_SECONDARY(longPreferencesKey("on_secondary"), "用于显示在辅助颜色之上的文本和图标的颜色。"),
    SECONDARY_CONTAINER(longPreferencesKey("secondary_container"), "在容器中使用的色调颜色。"),
    ON_SECONDARY_CONTAINER(longPreferencesKey("on_secondary_container"), "用于 secondaryContainer 顶部内容的颜色（和状态变体）。"),
    TERTIARY(longPreferencesKey("tertiary"), "第三色，可用于平衡主要颜色和次要颜色，或引起对输入字段等元素的高度关注。"),
    ON_TERTIARY(longPreferencesKey("on_tertiary"), "用于显示在第三颜色之上的文本和图标的颜色。"),
    TERTIARY_CONTAINER(longPreferencesKey("tertiary_container"), "在容器中使用的色调颜色。"),
    ON_TERTIARY_CONTAINER(longPreferencesKey("on_tertiary_container"), "应该用于 tertiaryContainer 之上的内容的颜色（和状态变体）。"),
    BACKGROUND(longPreferencesKey("background"), "显示在可滚动内容后面的背景颜色。"),
    ON_BACKGROUND(longPreferencesKey("on_background"), "用于显示在背景颜色之上的文本和图标的颜色。"),
    SURFACE(longPreferencesKey("surface"), "影响卡片、工作表和菜单等组件表面的表面颜色。"),
    ON_SURFACE(longPreferencesKey("on_surface"), "用于显示在表面颜色之上的文本和图标的颜色。"),
    SURFACE_VARIANT(longPreferencesKey("surface_variant"), "具有与 surface 类似用途的颜色的另一种选择。"),
    ON_SURFACE_VARIANT(longPreferencesKey("on_surface_variant"), "可用于 surface 顶部内容的颜色（和状态变体）。"),
    SURFACE_TINT(longPreferencesKey("surface_tint"), "此颜色将由应用色调提升的组件使用，并应用于 surface 的顶部。海拔越高，这种颜色使用得越多。"),
    INVERSE_SURFACE(longPreferencesKey("inverse_surface"), "与 surface 形成鲜明对比的颜色。对于位于其他具有 surface 颜色的表面之上的表面非常有用。"),
    INVERSE_ON_SURFACE(longPreferencesKey("inverse_on_surface"), "与 inverseSurface 形成鲜明对比的颜色。对于位于 inverseSurface 容器顶部的内容很有用。"),
    ERROR(longPreferencesKey("error"), "错误颜色用于指示组件中的错误，例如文本字段中的无效文本。"),
    ON_ERROR(longPreferencesKey("on_error"), "用于显示在错误颜色之上的文本和图标的颜色。"),
    ERROR_CONTAINER(longPreferencesKey("error_container"), "错误容器的首选色调。"),
    ON_ERROR_CONTAINER(longPreferencesKey("on_error_container"), "应该用于 errorContainer 之上的内容的颜色（和状态变体）。"),
    OUTLINE(longPreferencesKey("outline"), "用于边界的微妙颜色。轮廓颜色角增加了对比度以达到可访问性的目的。"),
    OUTLINE_VARIANT(longPreferencesKey("outline_variant"), "当不需要强烈对比时，用于装饰元素边界的实用颜色。"),
    SCRIM(longPreferencesKey("scrim"), "遮盖内容的稀松布颜色。")
}






