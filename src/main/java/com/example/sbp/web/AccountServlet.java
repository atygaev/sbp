package com.example.sbp.web;

import com.example.sbp.db.AccountDb;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccountServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getAttribute("userId") != null && req.getAttribute("amount") != null) {
            int userid = (Integer) req.getAttribute("userId");

            int amount = (Integer) req.getAttribute("amount");

            AccountDb.create(userid, amount);
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

        int userid = jsonObject.getAsJsonPrimitive("userId").getAsInt();

        int amount = jsonObject.has("amount") ? jsonObject.getAsJsonPrimitive("amount").getAsInt() : 0;

        AccountDb.create(userid, amount);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // /accounts?phone=<value>
        if (req.getParameter("phone") != null) {
            JsonArray users = new JsonArray();

            for (List<String> userData : AccountDb.fetchAllByPhone(req.getParameter("phone"))) {
                Iterator<String> iterator = userData.iterator();

                JsonObject user = new JsonObject();
                user.addProperty("id", Integer.parseInt(iterator.next()));
                user.addProperty("amount", iterator.next());

                users.add(user);
            }

            res.getWriter().println(users.toString());
            res.getWriter().flush();
            return;
        }

        // /accounts
        JsonArray users = new JsonArray();

        for (List<String> data : AccountDb.fetchAll()) {

            Iterator<String> iterator = data.iterator();

            JsonObject user = new JsonObject();
            user.addProperty("id", Integer.parseInt(iterator.next()));
            user.addProperty("userId", Integer.parseInt(iterator.next()));
            user.addProperty("amount", iterator.next());

            users.add(user);
        }

        res.getWriter().println(users.toString());
        res.getWriter().flush();
    }
}
