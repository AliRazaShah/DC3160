package model;

import util.DBUtil;

import java.sql.*;
import java.util.HashMap;


public class LessonTimetable {
    private HashMap<String, Lesson> lessons;


    public LessonTimetable() {

        PreparedStatement ps = null;

        // The sql query that will get all the lessons from db
        String sql = "SELECT * FROM lessons";

        // Connect to db
        DBUtil db = new DBUtil();
        Connection con = null;
        try {
            con = db.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            lessons = new HashMap<String, Lesson>();

            while(rs.next()) {
                String lessonID = rs.getString("lessonid");
                String description = rs.getString("description");
                Timestamp startDateTime = rs.getTimestamp("startDateTime");
                Timestamp endDateTime = rs.getTimestamp("endDateTime");
                int level = rs.getInt("level");

                Lesson lesson = new Lesson(description, startDateTime, endDateTime, level, lessonID);
                lessons.put(lessonID, lesson);
            }
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                System.out.println(e);
            }
        }


    }

    /**
     * @return the lesson
     */
    public Lesson getLesson(String lessonID) {
        return lessons.get(lessonID);
    }

    public HashMap<String, Lesson> getLessons() {
        return lessons;
    }
}
