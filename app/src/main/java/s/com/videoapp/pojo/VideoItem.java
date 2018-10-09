package s.com.videoapp.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoItem implements Serializable {

    public String userId = "";
    public String videoTitle = "";
    public String link = "";
    public String videoThumbnail = "";
    public int year = 0;
    public String text = "";
    public List<Tasks> taskArray = new ArrayList<>();

    public static class Tasks implements Serializable {

        public  String link;
        public  String text;
        public  String start;

    }
}

