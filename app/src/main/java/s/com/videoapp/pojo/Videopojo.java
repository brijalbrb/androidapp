package s.com.videoapp.pojo;

import java.io.Serializable;
import java.util.List;

public class Videopojo {

    private String id;
    private String link;
    private String string;
    private String info;
    private List<ArrayBean> array;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<ArrayBean> getArray() {
        return array;
    }

    public void setArray(List<ArrayBean> array) {
        this.array = array;
    }

    public static class ArrayBean implements Serializable{

        private String link;
        private String text;
        private String start;
        private double step_teime;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public double getStep_teime() {
            return step_teime;
        }

        public void setStep_teime(double step_teime) {
            this.step_teime = step_teime;
        }
    }
}
