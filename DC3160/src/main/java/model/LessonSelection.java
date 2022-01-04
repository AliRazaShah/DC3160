package model;

import java.sql.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import util.DBUtil;


public class LessonSelection {

    private HashMap<String, Lesson> chosenLessons;
    private int ownerID;

    PreparedStatement ps = null;

    public LessonSelection(int owner) {

        chosenLessons = new HashMap<String, Lesson>();
        this.ownerID = owner;

        // The sql query that will get all the selected lessons for the current user
        String sql = "SELECT * FROM lessons A JOIN lessons_booked B ON A.lessonid = B.lessonid WHERE B.clientid = " + ownerID;

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

            // Add all the lessons from the db result to the lessons
            while (rs.next()) {
                String lessonID = rs.getString("lessonid");
                String description = rs.getString("description");
                Timestamp startDateTime = rs.getTimestamp("startDateTime");
                Timestamp endDateTime = rs.getTimestamp("endDateTime");
                int level = rs.getInt("level");

                Lesson lesson = new Lesson(description, startDateTime, endDateTime, level, lessonID);
                this.addLesson(lesson);
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
     * @return the items
     */
    public Set<Entry<String, Lesson>> getItems() {
        return chosenLessons.entrySet();
    }

    public void addLesson(Lesson l) {
        Lesson lesson = new Lesson(l);
        this.chosenLessons.put(lesson.getId(), lesson);
    }

    public Lesson getLesson(String id) {
        if (this.chosenLessons.containsKey(id)) {
            return this.chosenLessons.get(id);
        } else return null;
    }

    public int getNumChosen() {
        return this.chosenLessons.size();
    }

    public int getOwner() {
        return this.ownerID;
    }

    public void updateBooking() {

        // DELETE all entries for current user
        String deleteQuery = "DELETE FROM lessons_booked WHERE clientid = " + this.getOwner();

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
            // Executes query
            ps = con.prepareStatement(deleteQuery);
            ps.executeUpdate();
            System.out.println("Deleted all items in table lesson_booked for clientid: " + this.getOwner());

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

        // Adds to db for each lesson currently selected
        for (Entry thisE: this.getItems()) {

            // Query that will insert the lesson to lessons_booked
            String insertQuery = "INSERT INTO lessons_booked VALUES (?, ?)";

            // Connect to db
            db = new DBUtil();
            con = null;
            try {
                con = db.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                // Assign value and execute query
                ps = con.prepareStatement(insertQuery);
                ps.setInt(1, this.getOwner());
                ps.setString(2, thisE.getKey().toString());

                ps.executeUpdate();

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

    }

    public void removeLesson(String lessonid) {
        this.chosenLessons.remove(lessonid);
    }

}
