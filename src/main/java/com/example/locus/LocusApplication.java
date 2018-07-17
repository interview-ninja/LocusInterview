package com.example.locus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class LocusApplication {

    public static void main(String[] args)  {
        String encodedPolyLine = "", inputLine;
        List<GeoPoint> geoPoints, answerList = new ArrayList<>();
        double splitDistance = 50;

        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=12.9451208,77.619549&destination=Salarpuria+Greenage,+Hosur+Road&mode=DRIVING&key=AIzaSyDkP60b6GNwdpd2IIeOihUADbvR9tS-xdw");
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()));
            while ((inputLine = bufferedReader.readLine()) != null) {
                if (inputLine.contains("overview_polyline")) {
                    inputLine = bufferedReader.readLine();
                    encodedPolyLine = inputLine.split("\"")[3];
                }
            }

            geoPoints = GeoCodingUtils.decodePoly(encodedPolyLine);

            for(int index = 0; index < geoPoints.size(); index++) {
                if(index >= 1) {
                    double currentDistance = GeoCodingUtils.distance(geoPoints.get(index), geoPoints.get(index-1));
                    int currentDistanceCovered = 0;
                    for(int j = 0; j < (int)(currentDistance/splitDistance); j++) {
                        currentDistanceCovered += splitDistance;
                        GeoPoint currentLatlng = geoPoints.get(index), previousLatlng = geoPoints.get(index - 1);
                        double bearing = GeoCodingUtils.getBearing(previousLatlng, currentLatlng);
                        answerList.add(GeoCodingUtils.movePoint(previousLatlng, currentDistanceCovered, bearing));
                    }
                }
                answerList.add(geoPoints.get(index));
            }
            bufferedReader.close();
            for(int i = 0; i < answerList.size(); i++)
                System.out.println(answerList.get(i));
        } catch (IOException e) {
            System.out.println("ERROR ocurred!!!");
        }
    }
}
