package com.l.main.PackageServlet;

import com.l.main.board.domain.Weight;
import com.l.main.board.service.ShowPackageBoxSum;
import com.l.main.board.serviceImpl.PackageBoxSum;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PackageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //resp.setContentType("text/html;charset=utf-8");

        /*Weight w = new Weight();
        ShowPackageBoxSum sum = new PackageBoxSum();

        *//*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());*//*
        String date = "2018-11-29";
        double s = sum.show_packageboxsum(date,"");
        PrintWriter out = resp.getWriter();
        out.print("净重："+s);*/
    }
}
