package com.example.sbp.web;

import com.example.sbp.db.UserDb;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getRequestURI().contains("accounts")) {
            ArrayList<Byte> requestContent = new ArrayList<>();

            byte[] buffer = new byte[256];

            int buffer_size = 0;

            while ((buffer_size = req.getInputStream().read(buffer)) > -1) {
                for (int i = 0; i < buffer_size; i++) {
                    requestContent.add(buffer[i]);
                }
            }

            byte[] stringBytes = new byte[requestContent.size()];
            int i = 0;

            for (Object o : requestContent.toArray()) {
                stringBytes[i++] = (byte) o;
            }

            String s = new String(stringBytes);

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(s, JsonObject.class);

            //////////////////////////
            String userId = req.getRequestURI().replaceAll(".*/users/(\\d+)/accounts.*", "$1");

            req.setAttribute("userId", Integer.parseInt(userId));
            req.setAttribute("amount", jsonObject.has("amount") ? jsonObject.getAsJsonPrimitive("amount").getAsInt() : 0);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/accounts");
            dispatcher.forward(req, res);

//            saveAccount(req, res);
            return;
        }

        ArrayList<Byte> requestContent = new ArrayList<>();

        byte[] buffer = new byte[256];

        int buffer_size = 0;

        while ((buffer_size = req.getInputStream().read(buffer)) > -1) {
            for (int i = 0; i < buffer_size; i++) {
                requestContent.add(buffer[i]);
            }
        }

        byte[] stringBytes = new byte[requestContent.size()];
        int i = 0;

        for (Object o : requestContent.toArray()) {
            stringBytes[i++] = (byte) o;
        }

        String s = new String(stringBytes);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(s, JsonObject.class);

        int resCode = UserDb.saveUser(jsonObject.getAsJsonPrimitive("phone").getAsString());

        res.setStatus(resCode);
        res.getWriter().flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getRequestURI().contains("accounts")) {
            getAccounts(req, res);
            return;
        }

        JsonArray users = new JsonArray();

        for (List<String> userData : UserDb.fetchAll()) {
            JsonObject user = new JsonObject();
            user.addProperty("id", Integer.parseInt(userData.get(0)));
            user.addProperty("phone", userData.get(1));

            users.add(user);
        }

        res.getWriter().println(users.toString());
        res.getWriter().flush();
    }

    private void getAccounts(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String userId = req.getRequestURI().replaceAll(".*/users/(\\d+)/accounts.*", "$1");

        JsonArray users = new JsonArray();

        for (List<String> userData : UserDb.fetchUserAccounts(Integer.parseInt(userId))) {
            Iterator<String> iterator = userData.iterator();

            JsonObject user = new JsonObject();
            user.addProperty("id", Integer.parseInt(iterator.next()));
            user.addProperty("amount", iterator.next());

            users.add(user);
        }

        res.getWriter().println(users.toString());
        res.getWriter().flush();
    }
}
