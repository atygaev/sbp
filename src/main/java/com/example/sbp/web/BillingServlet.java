package com.example.sbp.web;

import com.example.sbp.db.BillingDb;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class BillingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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

        int acc1 = jsonObject.getAsJsonPrimitive("acc1").getAsInt();
        int acc2 = jsonObject.getAsJsonPrimitive("acc2").getAsInt();

        int summa = jsonObject.getAsJsonPrimitive("summa").getAsInt();

        BillingDb.makePayment(acc1, acc2, summa);
    }
}
