/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team.dig.vtdm.distance;

import java.util.ArrayList;

import team.dig.vtdm.entities.Line;
import team.dig.vtdm.entities.Point;
import team.dig.vtdm.entities.ThreeDLine;
import team.dig.vtdm.services.DistanceService;

/**
 * @author Administrator
 */
public class FrechetDistanceCalculator implements SequenceDistanceCalculator {

    @Override
    public double getDistance(ArrayList<Point> r, ArrayList<Point> s) {
        // make sure the original objects will not be changed
        ArrayList<Point> r_clone = DistanceService.clonePointsList(r);
        ArrayList<Point> s_clone = DistanceService.clonePointsList(s);

        return getFrechet(r_clone, s_clone);
    }

    private double getFrechet(ArrayList<Point> r, ArrayList<Point> s) {
        double duration = 0;
        assert (r.size() > 1 && s.size() > 1);
        double dR = r.get(r.size() - 1).timeStamp - r.get(0).timeStamp;
        double dS = s.get(s.size() - 1).timeStamp - s.get(0).timeStamp;

        if (dR > dS) {
            duration = dS;
        } else {
            duration = dR;
        }

        double calculateTimeInterval = duration / 1000;

        ArrayList<ThreeDLine> r3d = getThreeDLine(r);
        ArrayList<ThreeDLine> s3d = getThreeDLine(s);
        long rStartTime = r.get(0).timeStamp;
        long sStartTime = s.get(0).timeStamp;

        double max = 0;
        EuclideanDistanceCalculator eclidean = new EuclideanDistanceCalculator();

        for (int i = 0; i < 1000; i++) {
            Point rCurrentPoint;
            Point sCurrentPoint;

            int rCurrentLine = 0;
            int sCurrentLine = 0;

            for (int j = 0; j < r.size() - 1; j++) {
                if (rStartTime + i * calculateTimeInterval >= r.get(j).timeStamp && rStartTime + i * calculateTimeInterval <= r.get(j + 1).timeStamp) {
                    rCurrentLine = j;
                    break;
                }
            }

            for (int j = 0; j < s.size() - 1; j++) {
                if (sStartTime + i * calculateTimeInterval >= s.get(j).timeStamp && sStartTime + i * calculateTimeInterval <= s.get(j + 1).timeStamp) {
                    sCurrentLine = j;
                    break;
                }
            }

            rCurrentPoint = r3d.get(rCurrentLine).getPointByTime((long) (rStartTime + i * calculateTimeInterval));
            sCurrentPoint = s3d.get(sCurrentLine).getPointByTime((long) (sStartTime + i * calculateTimeInterval));

            double distance = eclidean.getDistance(rCurrentPoint, sCurrentPoint);

            if (distance > max) {
                max = distance;

            }
        }

        return max;
    }

    private ArrayList<Line> getPolyline(ArrayList<Point> r) {
        ArrayList<Line> result = new ArrayList<Line>();

        if (r.size() < 2) {
            return result;
        }

        for (int i = 0; i < r.size() - 1; i++) {
            Line tempLine = new Line(r.get(i), r.get(i + 1));
            result.add(tempLine);
        }

        return result;
    }

    private ArrayList<ThreeDLine> getThreeDLine(ArrayList<Point> r) {
        ArrayList<ThreeDLine> result = new ArrayList<>();

        if (r.size() < 2) {
            return result;
        }

        for (int i = 0; i < r.size() - 1; i++) {
            ThreeDLine tempLine = new ThreeDLine(r.get(i), r.get(i + 1));
            result.add(tempLine);
        }

        return result;
    }

}
