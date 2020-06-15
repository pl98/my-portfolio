// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.JsonObject; 

/** Servlet that returns login status and log in/log out url depending on status. */
@WebServlet("/login-status")
public class LoginStatusServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      UserService userService = UserServiceFactory.getUserService();

      JsonObject root = new JsonObject();
      root.addProperty("status", userService.isUserLoggedIn());

      if (userService.isUserLoggedIn()) {
          String logoutUrl = userService.createLogoutURL("/");
          root.addProperty("url", logoutUrl);
      }
      else {
          String loginUrl = userService.createLoginURL("/nickname");
          root.addProperty("url", loginUrl);
      }

      response.getWriter().println(root.toString());
  }
}
