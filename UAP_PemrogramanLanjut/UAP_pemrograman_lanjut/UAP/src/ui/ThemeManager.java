package ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class ThemeManager {
    private static boolean darkMode = false;
    private static final List<Runnable> listeners = new ArrayList<>();

    private ThemeManager() {}

    // Status tema
    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean dark) {
        if (darkMode != dark) {
            darkMode = dark;
            notifyListeners();
        }
    }

    public static void toggleDarkMode() {
        setDarkMode(!darkMode);
    }

    // Listeners
    public static void addThemeChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    public static void removeThemeChangeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    // Background Colors
    public static Color getBackgroundPrimary() {
        return darkMode ? new Color(18, 18, 24) : new Color(247, 250, 255);
    }

    public static Color getBackgroundSecondary() {
        return darkMode ? new Color(28, 28, 38) : new Color(232, 242, 252);
    }

    public static Color getCardBackground() {
        return darkMode ? new Color(32, 32, 44) : Color.WHITE;
    }

    public static Color getCardBorder() {
        return darkMode ? new Color(55, 55, 70) : new Color(220, 230, 242);
    }

    // Sidebar Colors
    public static Color getSidebarGradientTop() {
        return darkMode ? new Color(12, 12, 18) : new Color(18, 33, 58);
    }

    public static Color getSidebarGradientBottom() {
        return darkMode ? new Color(6, 6, 10) : new Color(7, 18, 38);
    }

    public static Color getSidebarGlow() {
        return darkMode ? new Color(100, 100, 180) : new Color(65, 120, 230);
    }

    // Text Colors
    public static Color getTextPrimary() {
        return darkMode ? new Color(240, 240, 245) : new Color(20, 28, 44);
    }

    public static Color getTextSecondary() {
        return darkMode ? new Color(160, 165, 180) : new Color(85, 92, 105);
    }

    public static Color getTextMuted() {
        return darkMode ? new Color(120, 125, 140) : new Color(90, 98, 110);
    }

    public static Color getTitleColor() {
        return darkMode ? new Color(240, 240, 250) : new Color(20, 20, 20);
    }

    // Field Colors
    public static Color getFieldBorder() {
        return darkMode ? new Color(70, 100, 140) : new Color(120, 195, 235);
    }

    public static Color getFieldFillTop() {
        return darkMode ? new Color(38, 42, 55) : new Color(232, 246, 255);
    }

    public static Color getFieldFillBottom() {
        return darkMode ? new Color(32, 36, 48) : new Color(220, 242, 255);
    }

    public static Color getFieldBorderDisabled() {
        return darkMode ? new Color(50, 55, 65) : new Color(185, 205, 220);
    }

    public static Color getFieldFillDisabled() {
        return darkMode ? new Color(28, 30, 38) : new Color(242, 245, 248);
    }

    public static Color getFieldFocusBorder() {
        return darkMode ? new Color(80, 160, 220) : new Color(60, 160, 235);
    }

    public static Color getFieldHoverBorder() {
        return darkMode ? new Color(90, 170, 220) : new Color(95, 185, 235);
    }

    public static Color getFieldFocusGlow() {
        return darkMode ? new Color(80, 160, 220, 55) : new Color(60, 160, 235, 55);
    }

    // Icon Colors
    public static Color getIconColor() {
        return darkMode ? new Color(100, 170, 235) : new Color(45, 120, 215);
    }

    public static Color getIconBgLight() {
        return darkMode ? new Color(45, 50, 65) : new Color(235, 247, 255);
    }

    //  Accent Colors
    public static Color getPillBlue() {
        return darkMode ? new Color(60, 140, 240) : new Color(40, 125, 235);
    }

    public static Color getPillBlueBorder() {
        return darkMode ? new Color(50, 110, 190) : new Color(25, 90, 170);
    }

    // Chart Colors
    public static Color getChartLine() {
        return darkMode ? new Color(70, 140, 220) : new Color(27, 74, 132);
    }

    public static Color getChartGrid() {
        return darkMode ? new Color(50, 55, 70) : new Color(230, 234, 240);
    }

    public static Color getChartPoint() {
        return darkMode ? new Color(45, 50, 60) : Color.WHITE;
    }

    // Table Colors
    public static Color getTableHeaderBackground() {
        return darkMode ? new Color(35, 40, 55) : new Color(240, 245, 250);
    }

    public static Color getTableRowEven() {
        return darkMode ? new Color(28, 30, 40) : Color.WHITE;
    }

    public static Color getTableRowOdd() {
        return darkMode ? new Color(32, 35, 48) : new Color(248, 250, 255);
    }

    public static Color getTableRowHover() {
        return darkMode ? new Color(45, 50, 70) : new Color(230, 245, 255);
    }

    public static Color getTableSelectionBackground() {
        return darkMode ? new Color(55, 90, 140) : new Color(40, 125, 235);
    }

    public static Color getTableBorder() {
        return darkMode ? new Color(50, 55, 70) : new Color(220, 230, 242);
    }

    //  Button Colors
    public static Color getButtonPrimary() {
        return darkMode ? new Color(50, 130, 230) : new Color(40, 125, 235);
    }

    public static Color getButtonPrimaryHover() {
        return darkMode ? new Color(70, 150, 245) : new Color(55, 140, 245);
    }

    public static Color getButtonPrimaryPressed() {
        return darkMode ? new Color(40, 110, 200) : new Color(30, 100, 200);
    }

    public static Color getButtonSecondary() {
        return darkMode ? new Color(60, 65, 80) : new Color(90, 98, 112);
    }

    public static Color getButtonSecondaryHover() {
        return darkMode ? new Color(75, 80, 100) : new Color(110, 118, 132);
    }

    // Statistics Card Colors
    public static Color getStatCardDonor() {
        return darkMode ? new Color(40, 85, 145) : new Color(27, 74, 132);
    }

    public static Color getStatCardMasuk() {
        return darkMode ? new Color(60, 145, 180) : new Color(54, 150, 190);
    }

    public static Color getStatCardSaldo() {
        return darkMode ? new Color(55, 155, 115) : new Color(64, 170, 120);
    }

    // Dashboard Blob Colors
    public static Color getBlobLight() {
        return darkMode ? new Color(60, 65, 90, 60) : new Color(255, 255, 255, 150);
    }

    public static Color getBlobAccent() {
        return darkMode ? new Color(45, 70, 110, 80) : new Color(210, 232, 248, 140);
    }

    // Combo/Popup Colors
    public static Color getPopupBackground() {
        return darkMode ? new Color(35, 38, 50) : new Color(245, 252, 255);
    }

    public static Color getPopupSelectionBackground() {
        return darkMode ? new Color(55, 100, 160) : new Color(40, 125, 235);
    }

    public static Color getPopupSelectionForeground() {
        return Color.WHITE;
    }

    // Nav Button Colors
    public static Color getNavButtonActive() {
        return darkMode ? new Color(255, 255, 255, 22) : new Color(255, 255, 255, 18);
    }

    public static Color getNavButtonHover() {
        return darkMode ? new Color(255, 255, 255, 14) : new Color(255, 255, 255, 10);
    }

    public static Color getNavButtonBorderActive() {
        return darkMode ? new Color(255, 255, 255, 40) : new Color(255, 255, 255, 35);
    }

    public static Color getNavButtonBorderHover() {
        return darkMode ? new Color(255, 255, 255, 25) : new Color(255, 255, 255, 20);
    }

    public static Color getNavButtonShadowActive() {
        return new Color(0, 0, 0, darkMode ? 70 : 55);
    }

    public static Color getNavButtonShadowHover() {
        return new Color(0, 0, 0, darkMode ? 45 : 35);
    }

    //  Scrollbar Colors
    public static Color getScrollbarThumb() {
        return darkMode ? new Color(70, 75, 95) : new Color(180, 190, 205);
    }

    public static Color getScrollbarTrack() {
        return darkMode ? new Color(35, 38, 50) : new Color(240, 243, 248);
    }

    // Tab Colors
    public static Color getTabActiveBackground() {
        return darkMode ? new Color(50, 130, 230) : new Color(40, 125, 235);
    }

    public static Color getTabInactiveBackground() {
        return darkMode ? new Color(45, 50, 65) : new Color(230, 238, 248);
    }

    public static Color getTabHoverShadow() {
        return darkMode ? new Color(0, 0, 0, 30) : new Color(0, 0, 0, 22);
    }

    // Toast Colorsx
    public static Color getToastBackground() {
        return darkMode ? new Color(45, 50, 65) : new Color(50, 55, 65);
    }

    public static Color getToastText() {
        return Color.WHITE;
    }
}
