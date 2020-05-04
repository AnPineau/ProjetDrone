package com.example.projetdrone;

public class Util {

    public static String calculChecksum(String trame) {
        int checksum = 0;
        if (trame.startsWith("$")) {
            trame = trame.substring(1, trame.length());
        }
        int end = trame.indexOf('*');

        if(end==-1){
            end=trame.length();
        }

        for (int i = 0; i < end; i++) {
            checksum = checksum ^ trame.charAt(i);
        }
        String hex = Integer.toHexString(checksum);
        if (hex.length() == 1)
            hex = "0" + hex;
        return hex.toUpperCase();
    }

    public static float NMEAtoGoogleMap(String str1, String str2){
        int minutesPosition = str1.indexOf('.') - 2;
        float minutes = Float.parseFloat(str1.substring(minutesPosition));
        float decimalDegrees = Float.parseFloat(str1.substring(minutesPosition))/60.0f;

        float degree = Float.parseFloat(str1) - minutes;
        float wholeDegrees = (int)degree/100;

        float position = wholeDegrees + decimalDegrees;
        if(str2.equals("S") || str2.equals("W")) {
            position = -position;
        }
        return position;
    }
}
