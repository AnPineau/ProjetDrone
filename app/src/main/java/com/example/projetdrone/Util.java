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

    //return un tableau contenant Latitude exprimée en ddmm.mmmm, indicateur de latitude N=nord, S=sud,Longitude exprimée en dddmm.mmmm ,indicateur de longitude E=est, W=ouest
    public static String[] GoogleMapToNMEA(double lat, double lng){
        String[] res = new String[4];
        if(lat<0) {
            lat = -lat;
            res[1] = "S";
        }else{
            res[1] = "N";
        }
        if(lng<0){
            lng=-lng;
            res[3] = "W";
        }else{
            res[3] = "E";
        }

        double entierLat =  Math.floor(lat);
        double entierLng =  Math.floor(lng);

        double decimaleLat = lat-entierLat;
        double decimaleLng = lng-entierLng;

        res[0] = Double.toString(entierLat*100+decimaleLat*60);
        res[2] = Double.toString(entierLng*100+decimaleLng*60);

        return res;
    }

    public static String createGPRMCTrame(double hour, double lat, double lng, double vitesse, int date){
        String trame= "";
        String[] pos = GoogleMapToNMEA(lat, lng);

        trame+="$GPRMC,"+hour+",A,"+pos[0]+","+pos[1]+","+pos[2]+","+pos[3]+","+vitesse+",00.00,"+date+",,,";

        String checksum = calculChecksum(trame);

        trame+="*"+checksum;

        return trame;
    }
}
