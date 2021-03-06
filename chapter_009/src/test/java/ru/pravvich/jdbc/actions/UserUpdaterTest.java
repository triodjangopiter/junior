package ru.pravvich.jdbc.actions;

import org.junit.Assert;
import org.junit.Test;
import ru.pravvich.jdbc.PropertiesLoader;
import ru.pravvich.user.User;

import java.sql.Timestamp;

import static org.hamcrest.core.Is.is;

public class UserUpdaterTest extends TestDatabase {

    @Test
    public void whenUserUpdateThenUserUpdated() {

        final User user = new User(0, "username", "login", "password", "email",
                new Timestamp(System.currentTimeMillis()), "test");

        final PropertiesLoader originalScripts =
                new PropertiesLoader("database_scripts");

        final UserAdder adder = new UserAdder(connection, originalScripts);

        adder.addUser(user);

        //test.
        final UserUpdater updater = new UserUpdater(connection, originalScripts);

        final User updatedUser = new User(
                0, "new_username", "new_login", "new_password", "new_email",
                new Timestamp(System.currentTimeMillis()), "test");


        //get user before update.
        final User before = new SingleUserGetter(connection, originalScripts)
                .getUserBy(1);

        //tested method.
        updater.updateUserById(1, updatedUser);

        //get user after update.
        final User after = new SingleUserGetter(connection, originalScripts)
                .getUserBy(1);


        Assert.assertThat(before.getName(), is("username"));
        Assert.assertThat(after.getName(), is("new_username"));

    }

    @Test
    public void whenUpdateUserLoginPasswordOnUniqueValuesThenReturnTrue() {
        final User user = new User(0, "username", "login", "password", "email",
                new Timestamp(System.currentTimeMillis()), "test");

        final PropertiesLoader originalScripts =
                new PropertiesLoader("database_scripts");

        final UserAdder adder = new UserAdder(connection, originalScripts);

        adder.addUser(user);

        //test.
        final UserUpdater updater = new UserUpdater(connection, originalScripts);

        final User updatedUser = new User(
                0, "new_username", "new_login", "new_password", "new_email",
                new Timestamp(System.currentTimeMillis()), "test");


        //get user before update.
        final User before = new SingleUserGetter(connection, originalScripts)
                .getUserBy(1);

        //tested method.
        final boolean result = updater.updateAndGet(1, updatedUser);

        //get user after update.
        final User after = new SingleUserGetter(connection, originalScripts)
                .getUserBy(1);


        Assert.assertTrue(result);
        Assert.assertThat(before.getName(), is("username"));
        Assert.assertThat(after.getName(), is("new_username"));
    }

    @Test
    public void whenUpdateUserLoginPasswordOnNotUniqueValuesThenReturnFalse() {
        final User user = new User(
                0, "username", "login", "password", "email",
                new Timestamp(System.currentTimeMillis()), "test");

        final User user1 = new User(
                0, "username1", "login1", "password1", "email1",
                new Timestamp(System.currentTimeMillis()), "test1");

        final PropertiesLoader originalScripts =
                new PropertiesLoader("database_scripts");

        final UserAdder adder = new UserAdder(connection, originalScripts);

        adder.addUser(user);
        adder.addUser(user1);

        //test.
        final UserUpdater updater =
                new UserUpdater(connection, originalScripts);

        final User updatedUser = new User(
                0, "username1", "login1", "password1", "email1",
                new Timestamp(System.currentTimeMillis()), "test1");


        //get user before update.
        final User before = new SingleUserGetter(connection, originalScripts)
                .getUserBy(1);

        //tested method.
        final boolean result = updater.updateAndGet(1, updatedUser);

        //get user after update.
        final User after = new SingleUserGetter(connection, originalScripts)
                .getUserBy(1);


        Assert.assertFalse(result);
        Assert.assertThat(before.getLogin(), is("login"));
        Assert.assertThat(after.getLogin(), is("login"));
    }
}