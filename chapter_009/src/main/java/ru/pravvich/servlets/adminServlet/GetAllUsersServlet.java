package ru.pravvich.servlets.adminServlet;

import ru.pravvich.jdbc.DBJoint;
import ru.pravvich.jdbc.ScriptExecutor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static ru.pravvich.servlets.Paths.ALL_USERS;

/**
 * Controller for view all users.
 */
public class GetAllUsersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF8");

        try {

            viewAllUsers(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send on view list all users.
     */
    private void viewAllUsers(final HttpServletRequest request,
                              final HttpServletResponse response)

            throws ServletException, IOException, SQLException {


        final ScriptExecutor dbExecutor = getDBExecutor(request);


        request.setAttribute("allUsers", dbExecutor.getAllUsers());

        request.getRequestDispatcher(ALL_USERS.get())
                .forward(request, response);

    }

    private ScriptExecutor getDBExecutor(final HttpServletRequest request)
            throws SQLException {

        final DBJoint db = (DBJoint) request
                .getServletContext().getAttribute("db");

        return db.getDBScriptExecutor();
    }
}
