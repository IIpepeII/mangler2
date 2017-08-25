package wordcardapp;

import com.google.gson.Gson;
import controller.ImageReader;
import db_con.DatabaseController;
import model.Wuser;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

/**
 * This class responsible for the server settings and collect every route in its main method.
 */
public class Main {

    public static void main(String[] args) {

        staticFileLocation("/public"); //Spark needed the declaration of the path to the static file folders.
        File uploadDir = new File("upload");
        uploadDir.mkdir(); // create the upload directory if it doesn't exist
        externalStaticFileLocation("upload");//Spark needed declaration of the path to the external static file folders.
        DatabaseController dBase = new DatabaseController(); // This object is responsible for the continous connection with the database.
        dBase.createTablesIfNotExists();
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        port(8888); // The server listens through this port.
        String hardCodedAppKey = "123456"; //Admin key for the server. Theacher options available after login with this code.

        //redirecting to the localhost:8888/student page
        get("/", (req, res) -> {
            res.redirect("/student");
            return null;
        });

        /*
         * Rendering student template and create put key "admin" with value 0
         * in the session for the template engines to rendering login fields correctly.
         */
        get("/student", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            if (req.session().attribute("admin") == null) {
                req.session().attribute("admin", 0);
                model.put("admin", "false");
            } else if (req.session().attribute("admin").equals(1)) {
                model.put("admin", "true");
            } else {
                req.session().attribute("admin", 0);
                model.put("admin", "false");
            }
            req.session().attribute("user", new Wuser());

            return new ThymeleafTemplateEngine().render(
                    new ModelAndView(model, "student")
            );
        });

        /*
         * If "admin" key not exists or have a value 0 make a redirect to student page else
         * allowing to render the teacher template.
         */
        get("/teacher", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            if (req.session().attribute("admin") == null || req.session().attribute("admin").equals(0)) {
                res.redirect("/student");
            } else if (req.session().attribute("admin").equals(1)) {
                model.put("admin", "true");
            } else {
                model.put("admin", "false");
            }
            return new ThymeleafTemplateEngine().render(
                    new ModelAndView(model, "teacher")
            );
        });

        /*
         * Check of app key. If its incorrect redirect to student page.
         */
        post("/auth_key", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            if (req.queryParams("key").equals(hardCodedAppKey)) {
                req.session().attribute("admin", 1);
                res.redirect("/teacher");
                return null;
            } else {
                res.redirect("/student");
                return null;
            }
        });

        get("/wordcards", (req, res) -> {
            if (req.session().attribute("admin") == null || req.session().attribute("admin").equals(0)) {
                res.redirect("/student");
            }
            Gson json = new Gson();
            return json.toJson(dBase.getAllWordCards());
        });

        get("/logout", (req, res) -> {
            req.session().attribute("admin", 0);
            Map<String, Object> model = new HashMap<>();
            res.redirect("/student");
            return null;
        });

        get("/results", (req, res) -> {
            if (req.session().attribute("admin") == null || req.session().attribute("admin").equals(0)) {
                res.redirect("/student");
                return null;
            }
            Gson json = new Gson();
            return json.toJson(dBase.getAllResults());
        });

        get("/mixed_cards", (req, res) -> {
            Gson json = new Gson();
            Wuser wuser = req.session().attribute("user");
            wuser.setResultToZero();
            wuser.setStartTime();
            wuser.setCurrentCardIndexToZero();
            wuser.setCards(dBase.getMixedCards());
            return json.toJson(wuser.getCards());
        });

        post("/exercise", (req, res) -> {
            Gson json = new Gson();
            Wuser wuser = req.session().attribute("user");
            wuser.setResultToZero();
            wuser.setCurrentCardIndexToZero();
            wuser.setStartTime();
            String theme = req.queryParams("theme");
            wuser.setCards(dBase.getCardsByTheme(theme));
            return json.toJson(wuser.getCards());
        });

        get("/getcard", (req, res) -> {
            Gson json = new Gson();
            Wuser wuser = req.session().attribute("user");
            if (wuser.isLastIndex()) {
                return json.toJson(null);
            }
            return json.toJson(wuser.getCard());
        });

        /*
         * Compare the users inputs with the records in the database. When the current
         * card is the last one in the current user's wordCardList send a simple "OK".
         */
        post("/evaluate", (req, res) -> {
            String hun = req.queryParams("hun");
            String eng = req.queryParams("eng");
            Wuser wuser = req.session().attribute("user");
            wuser.setEndTime();
            if (wuser.getCardListSize() == 0) {
                return "OK";
            } else if (wuser.getCurrentIndex() == 0) {
                if (wuser.evalCardWithIndexZero(hun, eng)) {
                    wuser.setResult();
                }
            } else if (wuser.evalCard(hun, eng)) {
                wuser.setResult();
            }
            return "OK";
        });

        get("/result_to_zero", (req, res) -> {
            Wuser wuser = req.session().attribute("user");
            wuser.setResultToZero();
            return "OK";
        });

        get("/get_result", (req, res) -> {
            Wuser wuser = req.session().attribute("user");
            return String.valueOf(wuser.getResult());
        });

        post("/del_wordcard", (req, res) -> {
            if (req.session().attribute("admin").equals(0) || req.session().attribute("admin") == null) {
                res.redirect("/student");
            }
            Integer id = Integer.parseInt(req.queryParams("id"));
            dBase.delWordCard(id);
            return "OK";
        });

        post("/del_result", (req, res) -> {
            if (req.session().attribute("admin").equals(0) || req.session().attribute("admin") == null) {
                res.redirect("/student");
            }
            Integer id = Integer.parseInt(req.queryParams("id"));
            dBase.delResult(id);
            return "OK";
        });

        /*
         * Read an image from the /Upload external static folder and put a
         * Base64 encoded form into the response.
         * Html can handle base64 coded resources.
         */
        post("/getimage", (req, res) -> {
            String picLocation = req.queryParams("picLocation");
            return ImageReader.base64image(picLocation);
        });

        post("/save_result", (req, res) -> {
            String firstName = req.queryParams("fname");
            String lastName = req.queryParams("lname");
            Wuser wuser = req.session().attribute("user");
            String result = String.valueOf(wuser.getResult() * 10);
            dBase.addNewResult(firstName, lastName, result, wuser.getStartTime(), wuser.getEndTime());
            res.redirect("/student");
            return "OK";
        });

        /*
         * Create a temp file then copying it to the /upload folder. Make a new record
         * in the word_card table by the properties come with the uploaded file.
         */
        post("/upload", (req, res) -> {
            if (req.session().attribute("admin") == null || req.session().attribute("admin").equals(0)) {
                res.redirect("/student");
            }
            Path tempFile = Files.createTempFile(uploadDir.toPath(), "", ".jpg");
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            List<String> postParams = new ArrayList<>();
            String fileName = tempFile.toString();
            postParams.add(fileName);
            postParams.add(req.queryParams("theme"));
            postParams.add(req.queryParams("hun"));
            postParams.add(req.queryParams("eng"));
            dBase.addNewWordCard(postParams);

            res.redirect("/teacher");
            return "OK";
        });
    }
}