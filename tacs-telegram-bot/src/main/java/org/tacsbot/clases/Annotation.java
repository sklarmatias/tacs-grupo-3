package org.tacsbot.clases;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class Annotation {

    private User user;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;


}
