package ru.pravvich.jdbs.sqlAdapters;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.pravvich.jdbs.ConnectionReactor;
import ru.pravvich.jdbs.PropertyLoader;
import ru.pravvich.jdbs.StubPropertyLoader;
import ru.pravvich.jdbs.requestGeneration.Generator;
import ru.pravvich.jdbs.requestGeneration.RequestGenerator;
import ru.pravvich.models.Item;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.hamcrest.core.Is.is;

public class DeleteCommentTest {

    private Connection root;
    private Connection testDB;
    private PropertyLoader property;
    private Generator requests;
    private ConnectionReactor reactor;

    private void init() {
        property = new StubPropertyLoader();
        requests = new RequestGenerator();
        reactor = new ConnectionReactor(this.property);
    }

    private void initTestDatabase() throws SQLException {

        this.root = DriverManager.getConnection(
                this.property.getRootDB(),
                this.property.getUsername(),
                this.property.getPassword());


        try (final PreparedStatement createDB =

                     root.prepareStatement("CREATE DATABASE test_auto_services");

        ) {

            createDB.executeUpdate();
        }

    }

    private void createTestTables() throws SQLException {

        this.testDB = DriverManager.getConnection(
                this.property.getUrlDB(),
                this.property.getUsername(),
                this.property.getPassword());


        try (final PreparedStatement tasks =
                     this.testDB.prepareStatement(this.property.getScriptForCreateTasks());


             final PreparedStatement comments =
                     this.testDB.prepareStatement(this.property.getScriptForCreateComments())
        ) {

            tasks.executeUpdate();
            comments.executeUpdate();
        }
    }

    @Before
    public void before() {

        init();

        try {

            initTestDatabase();
            createTestTables();


            final Item item = new Item("name", 1L, "my header",
                    "my description", 1, new ArrayList<>());

            new AdditionItem(this.reactor, this.requests)
                    .addItem(item);

            new AdditionComment(this.reactor, this.requests).addCommit(item, "commit");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void whenDeleteCommentThenCommentIsDeleted() throws SQLException {

        final DeleteComment deleter = new DeleteComment(this.reactor, this.requests);

        FindById finder = new FindById(this.reactor, this.requests);

        final Item before = finder.findById(1);

        deleter.deleteCommit("commit");

        final Item after = finder.findById(1);

        this.reactor.closeConnection();

        Assert.assertThat(before.getCommits().get(0), is("commit"));
        Assert.assertNull(after.getCommits().get(0));
    }


    @After
    public void cleanResources() {
        try {

            this.testDB.close();

            deleteTestDatabase();

            this.root.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteTestDatabase() throws SQLException {

        this.root.prepareStatement("DROP DATABASE test_auto_services").executeUpdate();

    }
}