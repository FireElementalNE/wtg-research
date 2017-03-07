import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Constants {
    final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    final static String ACTIVITY_SUPERCLASS = "android.app.Activity";
    final static String SET_ONCLICK_LISTNER = "setOnClickListener";
    final static String ONCLICK = "onClick";
    final static String CONTEXT_CLASS = "android.content.Context";
    final static String ON_CLICK_LISTENER_CLASS = "android.view.ViewOnClickListener";
    final static String JAVA_CLASS_CLASS = "java.lang.Class";

    final static String LOG_SUFFIX = "_logfile.log";

    final static String OUT_TAG = "OUT";
    final static String ERR_TAG = "ERR";
    final static String SCR_TAG = "SCR";

    final static String NOPARSE = "NOPARSE";

    final static String INTENT_CLASS = "android.content.Intent";

    final static Pattern XML_FILENAME = Pattern.compile(("^.*\\.xml$"));
    final static Pattern TARGET_ACTIVITY = Pattern.compile("\\(android\\.content\\.Context,java\\.lang.Class\\)\\>\\(\\$r\\d+, class \\\"([\\w\\W]+)\\\"\\)");
    final static Pattern TARGET_INVOKE_LINE = Pattern.compile("<android\\.content\\.Intent: void <init>\\(android\\.content\\.Context,java\\.lang\\.Class\\)>");
    final static Pattern ANDROID_SKIP = Pattern.compile("^android\\..*$");


    final static boolean PRINT_ST = true;
    final static boolean DEBUG = false;
    // Warning this is a lot of output
    final static boolean CG_VERBOSE = false;

    // a lot of extra XML files
    final static List<String> XML_EXCLUDES = new ArrayList<>(Arrays.asList(
        "res/anim/abc_fade_in.xml",
        "res/anim/abc_fade_out.xml",
        "res/anim/abc_grow_fade_in_from_bottom.xml",
        "res/anim/abc_popup_enter.xml",
        "res/anim/abc_popup_exit.xml",
        "res/anim/abc_shrink_fade_out_from_bottom.xml",
        "res/anim/abc_slide_in_bottom.xml",
        "res/anim/abc_slide_in_top.xml",
        "res/anim/abc_slide_out_bottom.xml",
        "res/anim/abc_slide_out_top.xml",
        "res/color/abc_background_cache_hint_selector_material_dark.xml",
        "res/color/abc_background_cache_hint_selector_material_light.xml",
        "res/color/abc_primary_text_disable_only_material_dark.xml",
        "res/color/abc_primary_text_disable_only_material_light.xml",
        "res/color/abc_primary_text_material_dark.xml",
        "res/color/abc_primary_text_material_light.xml",
        "res/color/abc_search_url_text.xml",
        "res/color/abc_secondary_text_material_dark.xml",
        "res/color/abc_secondary_text_material_light.xml",
        "res/color/switch_thumb_material_dark.xml",
        "res/color/switch_thumb_material_light.xml",
        "res/drawable/abc_btn_borderless_material.xml",
        "res/drawable/abc_btn_check_material.xml",
        "res/drawable/abc_btn_colored_material.xml",
        "res/drawable/abc_btn_default_mtrl_shape.xml",
        "res/drawable/abc_btn_radio_material.xml",
        "res/drawable/abc_cab_background_internal_bg.xml",
        "res/drawable/abc_cab_background_top_material.xml",
        "res/drawable/abc_dialog_material_background_dark.xml",
        "res/drawable/abc_dialog_material_background_light.xml",
        "res/drawable/abc_edit_text_material.xml",
        "res/drawable/abc_item_background_holo_dark.xml",
        "res/drawable/abc_item_background_holo_light.xml",
        "res/drawable/abc_list_selector_background_transition_holo_dark.xml",
        "res/drawable/abc_list_selector_background_transition_holo_light.xml",
        "res/drawable/abc_list_selector_holo_dark.xml",
        "res/drawable/abc_list_selector_holo_light.xml",
        "res/drawable/abc_ratingbar_full_material.xml",
        "res/drawable/abc_seekbar_thumb_material.xml",
        "res/drawable/abc_seekbar_track_material.xml",
        "res/drawable/abc_spinner_textfield_background_material.xml",
        "res/drawable/abc_switch_thumb_material.xml",
        "res/drawable/abc_tab_indicator_material.xml",
        "res/drawable/abc_text_cursor_material.xml",
        "res/drawable/abc_textfield_search_material.xml",
        "res/layout/abc_action_bar_title_item.xml",
        "res/layout/abc_action_bar_up_container.xml",
        "res/layout/abc_action_bar_view_list_nav_layout.xml",
        "res/layout/abc_action_menu_item_layout.xml",
        "res/layout/abc_action_menu_layout.xml",
        "res/layout/abc_action_mode_bar.xml",
        "res/layout/abc_action_mode_close_item_material.xml",
        "res/layout/abc_activity_chooser_view.xml",
        "res/layout/abc_activity_chooser_view_list_item.xml",
        "res/layout/abc_alert_dialog_button_bar_material.xml",
        "res/layout/abc_alert_dialog_material.xml",
        "res/layout/abc_dialog_title_material.xml",
        "res/layout/abc_expanded_menu_layout.xml",
        "res/layout/abc_list_menu_item_checkbox.xml",
        "res/layout/abc_list_menu_item_icon.xml",
        "res/layout/abc_list_menu_item_layout.xml",
        "res/layout/abc_list_menu_item_radio.xml",
        "res/layout/abc_popup_menu_item_layout.xml",
        "res/layout/abc_screen_content_include.xml",
        "res/layout/abc_screen_simple.xml",
        "res/layout/abc_screen_simple_overlay_action_mode.xml",
        "res/layout/abc_screen_toolbar.xml",
        "res/layout/abc_search_dropdown_item_icons_2line.xml",
        "res/layout/abc_search_view.xml",
        "res/layout/abc_select_dialog_material.xml",
        "res/layout/notification_media_action.xml",
        "res/layout/notification_media_cancel_action.xml",
        "res/layout/notification_template_big_media.xml",
        "res/layout/notification_template_big_media_narrow.xml",
        "res/layout/notification_template_lines.xml",
        "res/layout/notification_template_media.xml",
        "res/layout/notification_template_part_chronometer.xml",
        "res/layout/notification_template_part_time.xml",
        "res/layout/select_dialog_item_material.xml",
        "res/layout/select_dialog_multichoice_material.xml",
        "res/layout/select_dialog_singlechoice_material.xml",
        "res/layout/support_simple_spinner_dropdown_item.xml",
        "res/color-v11/abc_background_cache_hint_selector_material_dark.xml",
        "res/color-v11/abc_background_cache_hint_selector_material_light.xml",
        "res/drawable-v21/abc_action_bar_item_background_material.xml",
        "res/drawable-v21/abc_btn_colored_material.xml",
        "res/color-v23/abc_color_highlight_material.xml",
        "res/drawable-v23/abc_control_background_material.xml"));

}

