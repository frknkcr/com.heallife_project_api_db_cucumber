package stepDefinitions;

import java.sql.*;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.testng.asserts.SoftAssert;
import utilities.DB_Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static utilities.DB_Utils.*;

public class DBStepDefinition {

    public static ResultSet resultset;

    String query;


    @Then("Database connection is closed")
    public void database_connection_is_closed() {
        closeConnection();
    }

    @Given("User sets the database connection")
    public void user_sets_the_database_connection() {
        createConnection();
    }

    @Given("Creates query with {string}")
    public void creates_query_with(String query) throws SQLException {
        this.query = query;
        resultset = getStatement().executeQuery(query);
    }

    @Given("Verifies that datas : {string} values : {string} message : {string}")
    public void verifies_that_datas_values_message(String actualValueColumn, String expectedValue, String message) throws SQLException {
        String[] columnsArr = actualValueColumn.split("#");
        String[] valuesArr = expectedValue.split("#");
        for (int i = 1; i <= columnsArr.length; i++) {
            resultset.absolute(i);
            Assert.assertEquals(message, resultset.getString(columnsArr[i - 1]), valuesArr[i - 1]);
        }
    }

    @Given("Verifies that it CONTAINS datas : {string} values : {string} message : {string}")
    public void verifies_that_it_contains_datas_values_message(String actualValueColumn, String expectedValue, String message) throws SQLException {

        // bu stepte CONTAINS asserti yapılıyor, assertEquals icin ayrı method var.

        String[] columnsArr = actualValueColumn.split("#");
        String[] valuesArr = expectedValue.split("#");

        resultset.absolute(0);
        for (int i = 0; i < columnsArr.length; i++) {
            while (resultset.next()) {
                Assert.assertTrue(message, resultset.getString(columnsArr[i]).contains(valuesArr[i]));
            }
        }
    }


    @Then("Verifies that datas : {string} values : {string}")
    public void verifiesThatDatasValues(String name, String value) throws SQLException {
        resultset.absolute(1);
        System.out.println(resultset.getString(name));
        Assert.assertEquals(value, resultset.getString(name));
    }

    @Given("Verifies that the result number of query is {int}")
    public void verifies_that_the_result_number_of_query_is(int sayi) throws Exception {
        int actualRowNumber = DB_Utils.getRowCount();
        Assert.assertEquals("Wrong number", sayi, actualRowNumber);
    }


    @And("Verify that the appointments made for the morning are less than the appointments for the afternoon")
    public void verifyThatTheAppointmentsMadeForTheMorningAreLessThanTheAppointmentsForTheAfternoon() throws SQLException {
        resultset.absolute(0);
        resultset.next();
        boolean control = resultset.getBoolean(1);
        Assert.assertTrue(control);
    }

    @And("Verify the first five")
    public void verifyTheFirstFive(List<Integer> list) throws SQLException {
        resultset.absolute(0);

        boolean control = true;
        int index = 0;
        while (resultset.next()) {
            int data = resultset.getInt(1);

            if (data != list.get(index)) {
                control = false;
                break;
            }
            index++;
        }
        Assert.assertTrue(control);
    }

    @Given("Creates update query with {string}")

    public void creates_update_query_with(String query) throws SQLException {
        DB_Utils.updateQuery(query);
    }

    @Given("It should be verified that multiple data entries can be made")
    public void ıt_should_be_verified_that_multiple_data_entries_can_be_made() throws SQLException {
        resultset.absolute(0);
        boolean control = false;
        while (resultset.next()) {


            if (resultset.getString("note").equals("deneme text") & resultset.getString("comment").equals("new comment")) {

                control = true;
                break;
            }
        }
        Assert.assertTrue(control);
    }


    @Given("Verify that number of result is two")
    public void verify_that_number_of_result_is_two() throws SQLException {
        int count = 0;
        while (resultset.next()) {
            count++;

        }
        System.out.println(count);
        assertTrue(count == 2);
    }

    @Given("Verify that max id is three")
    public void verify_that_max_id_is_three() throws SQLException {
        int expectedMaxresult = 3;
        resultset.absolute(0);
        resultset.next();
        Assert.assertEquals("", expectedMaxresult, resultset.getInt("max_id"));


    }

    @Given("Verify gender and mail information of patients whose patient name contains {string}")
    public void verify_gender_and_mail_information_of_patients_whose_patient_name_contains(String string) throws SQLException {
        resultset.absolute(0);

        List<String> patientsList = new ArrayList<>();
        while (resultset.next()) {
            patientsList.add(resultset.getString("gender"));
            patientsList.add(resultset.getString("email"));
        }
        System.out.println(patientsList);

        Assert.assertTrue(patientsList.contains("Female"));//[Female, reshu@gmail.com, Male, umair@gmail.com]
        Assert.assertTrue(patientsList.contains("reshu@gmail.com"));
        Assert.assertTrue(patientsList.contains("Male"));
        Assert.assertTrue(patientsList.contains("umair@gmail.com"));
    }

    @Given("Verify that the patient IDs below selected as offline as the payment type")
    public void verify_that_the_patient_i_ds_below_selected_as_offline_as_the_payment_type(List<Integer> list) throws SQLException {
        resultset.absolute(0);

        boolean control = true;
        int index = 0;
        while (resultset.next()) {
            int data = resultset.getInt(1);

            if (data != list.get(index)) {
                control = false;
                break;
            }
            index++;
        }
        Assert.assertTrue(control);
    }

    @Given("Add a new record to the consultant_register table")
    public void add_a_new_record_to_the_consultant_register_table() throws SQLException {

        try {
            String sql = "INSERT INTO consultant_register (id, ipd_id, date, ins_date, instruction, cons_doctor, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "1");
            preparedStatement.setString(2, "8");
            preparedStatement.setString(3, "null");
            preparedStatement.setString(4, "null");
            preparedStatement.setString(5, "null");
            preparedStatement.setString(6, "null");
            preparedStatement.setString(7, "null");
            preparedStatement.close();
            System.out.println("data added");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("Verify from the database that the contents of the department table match the {string} and {string}  {string} {string} information")
    public void verifyFromTheDatabaseThatTheContentsOfTheDepartmentTableMatchTheAndInformation(String actDepartment_name, String actCreated_at, String expDepartmen_name, String expCreated_at) throws SQLException {


        String departmenQuery = "select department_name,created_at from heallife_hospitaltraining.department;";

        try {
            DB_Utils.createConnection();
            resultset = DB_Utils.getStatement().executeQuery(departmenQuery);
            resultset.absolute(0);
            resultset.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        System.out.println(resultset.getString("department_name"));
        System.out.println(resultset.getString("created_at"));

        Assert.assertEquals("", expDepartmen_name, resultset.getString("department_name"));
        Assert.assertEquals("", expCreated_at, resultset.getString("created_at"));
    }

}


