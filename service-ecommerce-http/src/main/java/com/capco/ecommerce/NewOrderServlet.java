package com.capco.ecommerce;

import com.capco.ecommerce.dispatcher.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            // we are not caring about any security essues, we are only,
            // showing how to use http as a starting point
            String email = req.getParameter("email");
            BigDecimal value = new BigDecimal(req.getParameter("amount"));
            String orderID = UUID.randomUUID().toString();

            Order order = new Order(orderID, value, email);
            orderDispatcher.send("ECOMMERCE_NEW_ORDER", email,
                    new CorrelationId(NewOrderServlet.class.getSimpleName()), order);

            System.out.println("New Order sent successfully");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New Order sent");
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }
}
