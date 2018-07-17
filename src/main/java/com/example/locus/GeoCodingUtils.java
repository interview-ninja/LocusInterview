package com.example.locus;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class GeoCodingUtils {
    public static List<GeoPoint> decodePoly(String encoded) {

        List<GeoPoint> poly = new ArrayList<GeoPoint>();
        int index = 0, len = encoded.length()-3;
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            GeoPoint p = new GeoPoint((double) (((double) lat / 1E5) ),
                    (double) (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    public static double getBearing(GeoPoint geoPoint1, GeoPoint geoPoint2) {
        double lat1 = toRadians(geoPoint1.getLatitude()), lat2 = toRadians(geoPoint2.getLatitude()), lng1 = toRadians(geoPoint1.getLongitude()), lng2 = toRadians(geoPoint2.getLongitude());
        double dLon = (lng2-lng1);
        double degree = Math.toDegrees(Math.atan2(sin(dLon) * cos(lat2),
                cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)));

        if (degree >= 0) {
            return degree;
        } else {
            return 360 + degree;
        }

    }

    public static double distance(GeoPoint geoPoint1, GeoPoint geoPoint2) {
        double lat1 = geoPoint1.getLatitude(), lat2 = geoPoint2.getLatitude(), lon1 = geoPoint1.getLongitude(), lon2 = geoPoint2.getLongitude();
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2) ;
        return Math.sqrt(distance);
    }

    public static GeoPoint movePoint(GeoPoint geoPoint, double distanceInMetres, double bearing) {
        double latitude = geoPoint.getLatitude(), longitude = geoPoint.getLongitude();
        double brngRad = toRadians(bearing);
        double latRad = toRadians(latitude);
        double lonRad = toRadians(longitude);
        int earthRadiusInMetres = 6371000;
        double distFrac = distanceInMetres / earthRadiusInMetres;

        double latitudeResult = asin(sin(latRad) * cos(distFrac) + cos(latRad) * sin(distFrac) * cos(brngRad));
        double a = atan2(sin(brngRad) * sin(distFrac) * cos(latRad), cos(distFrac) - sin(latRad) * sin(latitudeResult));
        double longitudeResult = (lonRad + a + 3 * PI) % (2 * PI) - PI;
        return new GeoPoint(toDegrees(latitudeResult) , toDegrees(longitudeResult));
    }


}
