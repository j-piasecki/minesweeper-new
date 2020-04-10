package com.github.breskin.minesweeper;

import android.graphics.Color;

public class Theme {
    public enum ColorType {
        FieldType1, FieldType2, FieldType3, FieldType4, FieldType5, FieldType6, FieldType7, FieldType8,
        Background, FancyButtonText, FancyButtonBackground, FancyButtonForeground, FancyButtonIcon, SliderForeground, SliderBackground, Square, SquareRevealed, SquareRed, SquareGreen, Flag, FlagHandle,
        Mine, HubBackground, HubText, DefaultButton, DefaultButtonFilledText, ImageButtonIcon, ImageButtonBackground, CheckBox, CheckBoxHovered, Header, SecondLivesWidget, HomeTransition
    }

    public static int getColor(ColorType type) {
        return getColor(type, 1);
    }

    public static int getColor(ColorType type, float alpha) {
        switch (type) {
            case FieldType1: return Color.argb((int)(255 * alpha), 0, 64, 255);
            case FieldType2: return Color.argb((int)(255 * alpha), 0, 200, 0);
            case FieldType3: return Color.argb((int)(255 * alpha), 255, 0, 0);
            case FieldType4: return Color.argb((int)(255 * alpha), 0, 0, 192);
            case FieldType5: return Color.argb((int)(255 * alpha), 192, 0, 0);
            case FieldType6: return Color.argb((int)(255 * alpha), 0, 255, 255);
            case FieldType7: return Color.argb((int)(255 * alpha), 0, 0, 0);
            case FieldType8: return Color.argb((int)(255 * alpha), 200, 200, 200);
        }

        if (DataManager.DARK_THEME)
            return getColorDark(type, alpha);
        else
            return getColorLight(type, alpha);
    }

    static int getColorDark(ColorType type, float alpha) {
        switch (type) {
            case Background: return Color.argb((int)(alpha * 255), 0, 0, 0);
            case FancyButtonText: return Color.argb((int)(alpha * 255), 0, 0, 0);
            case FancyButtonBackground: return Color.argb((int)(alpha * 255), 153, 153, 153);
            case FancyButtonForeground: return Color.argb((int)(alpha * 255), 191, 191, 191);
            case FancyButtonIcon: return Color.argb((int)(alpha * 255), 32, 32, 32);
            case SliderForeground: return Color.argb((int)(alpha * 255), 255, 255, 255);
            case SliderBackground: return Color.argb((int)(alpha * 128), 255, 255, 255);
            case Square: return Color.argb((int)(alpha * 255), 64, 64, 64);
            case SquareRevealed: return Color.argb((int)(alpha * 255), 96, 96, 96);
            case SquareRed: return Color.argb((int)(alpha * 255), 192, 0, 0);
            case SquareGreen: return Color.argb((int)(alpha * 255), 0, 192, 0);
            case Flag: return Color.argb((int)(alpha * 255), 200, 0, 0);
            case FlagHandle: return Color.argb((int)(alpha * 255), 0, 0, 0);
            case Mine: return Color.argb((int)(alpha * 255), 255, 255, 255);
            case HubBackground: return Color.argb((int)(alpha * 160), 0, 0, 0);
            case HubText: return Color.argb((int)(alpha * 255), 255, 255, 255);
            case DefaultButton: return Color.argb((int)(alpha * 255), 204, 204, 204);
            case DefaultButtonFilledText: return Color.argb((int)(alpha * 255), 0, 0, 0);
            case ImageButtonIcon: return Color.argb((int)(alpha * 255), 255, 255, 255);
            case ImageButtonBackground: return Color.argb((int)(alpha * 96), 255, 255, 255);
            case CheckBox: return Color.argb((int)(alpha * 255), 255, 255, 255);
            case CheckBoxHovered: return Color.argb((int)(alpha * 24), 255, 255, 255);
            case Header: return Color.argb((int)(alpha * 255), 255, 255, 255);
            case SecondLivesWidget: return Color.argb((int)(alpha * 255), 255, 255, 255);
            case HomeTransition: return Color.argb((int)(alpha * 255), 255, 255, 255);
        }

        return Color.BLACK;
    }

    static int getColorLight(ColorType type, float alpha) {
        switch (type) {
            case Background: return Color.argb((int)(alpha * 255), 216, 216, 216);
            case FancyButtonText: return Color.argb((int)(alpha * 255), 0, 0, 0);
            case FancyButtonBackground: return Color.argb((int)(alpha * 255), 148, 148, 148);
            case FancyButtonForeground: return Color.argb((int)(alpha * 255), 168, 168, 168);
            case FancyButtonIcon: return Color.argb((int)(alpha * 255), 48, 48, 48);
            case SliderForeground: return Color.argb((int)(alpha * 255), 32, 32, 32);
            case SliderBackground: return Color.argb((int)(alpha * 128), 32, 32, 32);
            case Square: return Color.argb((int)(alpha * 255), 164, 164, 164);
            case SquareRevealed: return Color.argb((int)(alpha * 255), 200, 200, 200);
            case SquareRed: return Color.argb((int)(alpha * 255), 208, 32, 32);
            case SquareGreen: return Color.argb((int)(alpha * 255), 32, 208, 32);
            case Flag: return Color.argb((int)(alpha * 255), 200, 0, 0);
            case FlagHandle: return Color.argb((int)(alpha * 255), 0, 0, 0);
            case Mine: return Color.argb((int)(alpha * 255), 48, 48, 48);
            case HubBackground: return Color.argb((int)(alpha * 208), 160, 160, 160);
            case HubText: return Color.argb((int)(alpha * 255), 32, 32, 32);
            case DefaultButton: return Color.argb((int)(alpha * 255), 32, 32, 32);
            case DefaultButtonFilledText: return Color.argb((int)(alpha * 255), 255, 255, 255);
            case ImageButtonIcon: return Color.argb((int)(alpha * 255), 32, 32, 32);
            case ImageButtonBackground: return Color.argb((int)(alpha * 96), 32, 32, 32);
            case CheckBox: return Color.argb((int)(alpha * 255), 32, 32, 32);
            case CheckBoxHovered: return Color.argb((int)(alpha * 24), 32, 32, 32);
            case Header: return Color.argb((int)(alpha * 255), 32, 32, 32);
            case SecondLivesWidget: return Color.argb((int)(alpha * 255), 32, 32, 32);
            case HomeTransition: return Color.argb((int)(alpha * 255), 160, 160, 160);
        }

        return Color.WHITE;
    }
}
