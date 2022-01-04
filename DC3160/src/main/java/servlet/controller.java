package servlet;

import java.io.IOException;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;


import model.Lesson;
import model.LessonSelection;
import model.LessonTimetable;
import util.DBUtil;

/**
 * Servlet implementation class LoginCheck
 */
@WebServlet(urlPatterns = "/coursework/do/*")
public class controller extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private LessonTimetable availableLessons;
    private LessonSelection lessonSelection;
    private static final int maxLessons = 3;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public controller() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Processes the incoming requests
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getPathInfo();
        RequestDispatcher dispatcher = null;

        HttpSession session = request.getSession();
        availableLessons = new LessonTimetable();

        if (action.equals("/login")) {
            // Checks if user is already logged in, if not then continues
            if (session.isNew() || session.getAttribute("user") == null) {

                // Get username and password from request
                PreparedStatement ps = null;
                String username = request.getParameter("username");
                String password = request.getParameter("password");


                // The sql query that will get the client id and username from the given details
                String sql = "SELECT clientid,username FROM clients WHERE username = ? AND password = ?";

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
                    // prepare statement and populate variables for query with username and password
                    ps = con.prepareStatement(sql);
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ResultSet rs = ps.executeQuery();

                    // If correct details are provided and user exists:
                    if (rs.next()) {
                        int cID = rs.getInt("clientid");

                        // Instantiate the lesson selection model and the selected lessons in session
                        lessonSelection = new LessonSelection(cID);
                        session.setAttribute("lessonSelection", lessonSelection);
                        // Sets session attribute for max lesson selected
                        if (lessonSelection.getNumChosen() >= maxLessons) {
                            session.setAttribute("MaxLessonsSelected", true);
                        } else {
                            session.setAttribute("MaxLessonsSelected", false);
                        }

                        System.out.println("Successful login, client id: " + cID);
                        // create a successful login page
                        session.setAttribute("clientID", cID);
                        session.setAttribute("user", username);
                        session.setAttribute("alertID", "loginSuccess");
                        dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
                        dispatcher.forward(request, response);

                    } else {
                        // Otherwise stay on page:
                        session.setAttribute("clientID", null);
                        session.setAttribute("alertID", "incorrectPass");
                        dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
                        dispatcher.forward(request, response);
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
            } else { // If user is already logged in, goes to timetable page
                dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
                dispatcher.forward(request, response);
            }
        } else {
            if (session.isNew() || session.getAttribute("user") == null && !action.equals("/addUser")) {
                dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
                dispatcher.forward(request, response);
            } else {
                if (action.equals("/viewTimetable")) {
                    // Sets lessonSelection
                    if (session.getAttribute("lessonSelection") == null) {
                        lessonSelection = new LessonSelection((Integer) session.getAttribute("clientID"));
                        session.setAttribute("lessonSelection", lessonSelection);
                    } else {
                        lessonSelection = (LessonSelection) session.getAttribute("lessonSelection");
                    }
                    // Sets session attribute for max lesson selected
                    if (lessonSelection.getNumChosen() >= maxLessons) {
                        session.setAttribute("MaxLessonsSelected", true);
                    } else {
                        session.setAttribute("MaxLessonsSelected", false);
                    }
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
                    dispatcher.forward(request, response);
                } else if (action.equals("/chooseLesson")) {

                    // Sets lessonSelection
                    if (session.getAttribute("lessonSelection") == null) {
                        lessonSelection = new LessonSelection((Integer) session.getAttribute("clientID"));
                        session.setAttribute("lessonSelection", lessonSelection);
                    } else {
                        lessonSelection = (LessonSelection) session.getAttribute("lessonSelection");
                    }

                    // Get the selected lesson id which will be added
                    String lessonID = request.getParameter("lessonid");
                    // Adds lesson if lesson isn't already added and max lessons hasn't been reached
                    if (lessonSelection.getLesson(lessonID) == null && lessonSelection.getNumChosen() < maxLessons) {
                        // Get the lesson from timetable with id
                        Lesson selectedLesson = availableLessons.getLesson(lessonID);
                        // Add it to the selected lessons
                        lessonSelection.addLesson(selectedLesson);
                        session.setAttribute("lessonSelection", lessonSelection);
                        if (lessonSelection.getNumChosen() >= maxLessons) {
                            session.setAttribute("MaxLessonsSelected", true);
                        } else {
                            session.setAttribute("MaxLessonsSelected", false);
                        }
                        // Go to lesson selection view
                        dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
                        dispatcher.forward(request, response);
                    } else { // Else stays on timetable view
                        dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
                        dispatcher.forward(request, response);
                    }
                } else if (action.equals("/viewSelection")) {
                    // Sets lessonSelection
                    if (session.getAttribute("lessonSelection") == null) {
                        lessonSelection = new LessonSelection((Integer) session.getAttribute("clientID"));
                        session.setAttribute("lessonSelection", lessonSelection);
                    } else {
                        lessonSelection = (LessonSelection) session.getAttribute("lessonSelection");
                    }
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
                    dispatcher.forward(request, response);
                } else if (action.equals("/finaliseBooking")) {
                    lessonSelection.updateBooking();
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
                    dispatcher.forward(request, response);
                } else if (action.equals("/removeLesson")) {
                    lessonSelection.removeLesson(request.getParameter("lessonid"));
                    session.setAttribute("lessonSelection", lessonSelection);
                    // Sets the session attribute for max lessons to false as a lesson has been removed
                    session.setAttribute("MaxLessonsSelected", false);
                    dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
                    dispatcher.forward(request, response);
                } else if (action.equals("/logout")) {
                    session.setAttribute("clientID", null);
                    session.setAttribute("user", null);
                    session.setAttribute("MaxLessonsSelected", false);
                    session.invalidate();
                    dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
                    dispatcher.forward(request, response);
                } else if (action.equals("/addUser")) {

                    // Get username and password from request
                    PreparedStatement ps = null;
                    String newUsername = request.getParameter("newUsername");
                    String newPassword = request.getParameter("newPassword");

                    // The sql query that will add the new username and password into db
                    String sql = "INSERT INTO clients(username, password) VALUES (?, ?)";

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
                        // prepare statement and populate variables for query with username and password
                        ps = con.prepareStatement(sql);
                        ps.setString(1, newUsername);
                        ps.setString(2, newPassword);
                        ps.executeUpdate();
                        System.out.println("Created user: " + newUsername);
                    } catch (SQLException e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }

                    try{
                        // The sql query that will get clientid
                        String sqlGet = "SELECT clientid,username FROM clients WHERE username = ? AND password = ?";

                        // prepare statement and populate variables for query with username and password
                        ps = con.prepareStatement(sqlGet);
                        ps.setString(1, newUsername);
                        ps.setString(2, newPassword);
                        ResultSet rs = ps.executeQuery();

                        if (rs.next()) {
                            int cID = rs.getInt("clientid");

                            // Instantiate the lesson selection model and the selected lessons in session
                            lessonSelection = new LessonSelection(cID);
                            session.setAttribute("lessonSelection", lessonSelection);
                            // Sets session attribute for max lesson selected
                            if (lessonSelection.getNumChosen() >= maxLessons) {
                                session.setAttribute("MaxLessonsSelected", true);
                            } else {
                                session.setAttribute("MaxLessonsSelected", false);
                            }

                            System.out.println("Successful signup, client id: " + cID);
                            // create a successful login page
                            session.setAttribute("clientID", cID);
                            session.setAttribute("user", newUsername);
                            session.setAttribute("alertID", "loginSuccess");
                            dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
                            dispatcher.forward(request, response);
                        } else {
                            dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");
                            dispatcher.forward(request, response);
                        }
                    } catch (SQLException e) {
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
        }
    }


    /**
     * Get method
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * POST method
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

}


