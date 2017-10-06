package novels;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class Story {
    @SerializedName("ID")
    public String id;

    @SerializedName("message")
    public String text;

    @SerializedName("answers")
    public List<Choice> choices;

    @SerializedName("type")
    public String type;

    @SerializedName("time")
    public Time time;


    public Story(String id, String text, Choice... choices) {        //"..." refer to varargs method
        this.id = id;
        this.text = text;
        this.choices = Arrays.asList(choices);                      //convert Array of choices to List of choices
    }

    public boolean isType(String storyType){                        //check the input type of its story(Timeout, Input, NextArc, Map,...)
        return this.type.equalsIgnoreCase(storyType);
    }
}
