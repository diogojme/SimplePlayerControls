package br.android.simplemediacontroller.player.utils;

/**
 * Created by diogojayme on 7/16/15.
 */
public class TimerConverter {

    /**
     * Formata o tempo em MM:SS, eg: 13230 ms , 03:13 min
     *
     * */
    public static String progressToTime(int time) {
        int minutes = (int) Math.floor(time / 60f);
        String strMinutes = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        int seconds = time % 60;
        String strSeconds = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
        return strMinutes + ":" + strSeconds;
    }

    /**
     * Convert milliseconds into time hh:mm:ss
     * @param milliseconds
     * @return time in String
     */
    public static String progressToTime(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000))%60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if(hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }
}
